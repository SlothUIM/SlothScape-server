package server.clip.doors;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.quests.MerlinsCrystal;
import server.model.players.skills.Skill;
import server.clip.WorldObject;
import server.world.Location;
import server.world.World;
import server.world.objects.GlobalObject;

public class DoorHandler {

	private static final int WEST = 0;
	private static final int NORTH = 1;
	private static final int EAST = 2;
	private static final int SOUTH = 3;

	private static final Map<Location, DoorDefinition> openDoors = new ConcurrentHashMap<>();

	private static final Map<Location, DoorDefinition> openByLocation = new HashMap<>();
	private static final Map<DoorKey, DoorDefinition> openByKey = new HashMap<>();
	private static volatile boolean indexBuilt = false;

	// FIX: Synchronized to prevent double-building on server boot
	private static synchronized void ensureIndex() {
		if (indexBuilt) return;
		rebuildOpenIndex();
	}

	public static void rebuildOpenIndex() {
        openByLocation.clear();
        openByKey.clear();

        for (DoorDefinition closed : DoorDefinition.definitions.values()) {
            if (closed.getOpenDef() != null) {
                indexDoor(closed, closed.getOpenDef());
            }
        }

        for (DoubleDoorDefinition ddd : DoubleDoorDefinition.definitions.values()) {
            indexDoor(ddd.getClosed1(), ddd.getOpen1());
            indexDoor(ddd.getClosed2(), ddd.getOpen2());
        }
        indexBuilt = true;
    }

    private static void indexDoor(DoorDefinition closed, DoorDefinition open) {
        openByLocation.put(open.getCoordinate(), closed);
        openByKey.put(new DoorKey(open.getId(), open.getX(), open.getY(), open.getH()), closed);
    }
	public static boolean isDoubleDoor(int id, int x, int y, int h) {
        return DoubleDoorDefinition.forCoordinate(new Location(x, y, h)) != null;
    }


	// IMPORTANT: Now resolves both closed and initially-open doors using the prebuilt index
	public static DoorDefinition findDoorAt(int x, int y, int height, int id) {
	    ensureIndex();
	    Location loc = new Location(x, y, height);

	    // 1. Check if it's a closed single door
	    DoorDefinition closed = DoorDefinition.forCoordinate(loc);
	    if (closed != null && closed.getId() == id) {
	        return closed;
	    }

	    // 2. Use the KEY index (Resolves open state back to closed definition)
	    DoorDefinition closedFromOpen = openByKey.get(new DoorKey(id, x, y, height));
	    if (closedFromOpen != null) {
	        return closedFromOpen;
	    }

	    // 3. Fallback to Location
	    return openByLocation.get(loc);
	}
	public static DoubleDoorDefinition findDoubleDoorAt(int x, int y, int height, int id) {
	    ensureIndex();
	    Location loc = new Location(x, y, height);

	    // Check if a double door is defined at this tile
	    DoubleDoorDefinition ddd = DoubleDoorDefinition.forCoordinate(loc);
	    if (ddd != null) {
	        // Verify the ID matches one of the 4 linked states (Closed 1/2 or Open 1/2)
	        if (id == ddd.getClosed1().getId() || id == ddd.getClosed2().getId() ||
	            id == ddd.getOpen1().getId() || id == ddd.getOpen2().getId()) {
	            return ddd;
	        }
	    }
	    return null;
	}
	public static void openDoubleDoor(Player player, DoubleDoorDefinition ddd, WorldObject clickedObject, int xOffset, int yOffset, int timer) {
		System.out.println(clickedObject);
	    // 1. Precise State Check: Must match coordinate AND the specific Closed ID
	    boolean isClickedClosedID = (clickedObject.id == ddd.getClosed1().getId() || clickedObject.id == ddd.getClosed2().getId());
	    
	    // 2. Also ensure we are actually at the closed coordinates
	    boolean isAtClosedPos = (clickedObject.getX() == ddd.getClosed1().getX() && clickedObject.getY() == ddd.getClosed1().getY() && clickedObject.getFace() == ddd.getClosed1().getFace()) || 
	                             (clickedObject.getX() == ddd.getClosed2().getX() && clickedObject.getY() == ddd.getClosed2().getY());

	    // If it's the closed ID at the closed position, we are OPENING.
	    // Otherwise, we treat it as an OPEN door being CLOSED.
	    boolean isOpening = isClickedClosedID && isAtClosedPos;

	    int type = 0;
        if (isForceWalk(clickedObject.id)) {
	        type = 0;
	    }
	    if (isOpening) {
	    	player.sendMessage("You open the double doors.");
	    	GlobalObject closed = new GlobalObject(-1, ddd.getClosed1().getX(), ddd.getClosed1().getY(), ddd.getClosed1().getH(), ddd.getClosed1().getFace(), 0);
	    	GlobalObject closed2 = new GlobalObject(-1, ddd.getClosed2().getX(), ddd.getClosed2().getY(), ddd.getClosed2().getH(), ddd.getClosed2().getFace(), 0);
	    	World.getWorld().getGlobalObjects().add(closed);
		    World.getWorld().getGlobalObjects().add(closed2);
	        // OPENING 
	    	//int type = 0;
	    	GlobalObject go1 = new GlobalObject(ddd.getOpen1().getId(), ddd.getOpen1().getX(), ddd.getOpen1().getY(), ddd.getOpen1().getH(), ddd.getOpen1().getFace(), type, timer, ddd.getClosed1().getId());
		    GlobalObject go2 = new GlobalObject(ddd.getOpen2().getId(), ddd.getOpen2().getX(), ddd.getOpen2().getY(), ddd.getOpen2().getH(), ddd.getOpen2().getFace(), type, timer, ddd.getClosed2().getId());
		     World.getWorld().getGlobalObjects().add(go1);
		    World.getWorld().getGlobalObjects().add(go2);
		       

		    player.getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 5, player.EffectVolume);
	        openDoors.put(ddd.getClosed1().getCoordinate(), ddd.getOpen1());
	        openDoors.put(ddd.getClosed2().getCoordinate(), ddd.getOpen2());
	        if (isForceWalk(clickedObject.id)) {
		        player.getPA().walkTo(xOffset, yOffset);
		    }
	    } else {
	        // CLOSING (Manual)
	        // We pass the clickedObject to our existing closeDoor logic
	        closeDoor(clickedObject);
	    }
	}

	public static boolean clickDoor(Player player, DoorDefinition door, WorldObject object) {
		ensureIndex();
		Location clickedLoc = object != null 
		        ? new Location(object.getX(), object.getY(), player.getHeight())
		        : (door != null ? door.getCoordinate() : null);

		    if (clickedLoc == null) return false;


		    int id = object != null ? object.id : (door != null ? door.getId() : -1);
		System.out.println(object);
		int x = object.getX();
	    int y = object.getY();
	    int h = player.getHeight();

	    DoubleDoorDefinition ddd = findDoubleDoorAt(x, y, clickedLoc.getZ(), id);
	    if (ddd != null) {
	    	int xOffset = 0, yOffset = 0;
			switch (object.getFace()) {
			case WEST:
				if (player.getX() == object.getX()) xOffset = -1;
				else if (player.getX() == object.getX() - 1) xOffset = 1;
				else return false;
				break;
			case NORTH:
				if (player.getY() == object.getY()) yOffset = 1;
				else if (player.getY() == object.getY() + 1) yOffset = -1;
				else return false;
				break;
			case EAST:
				if (player.getX() == object.getX()) xOffset = 1;
				else if (player.getX() == object.getX() + 1) xOffset = -1;
				else return false;
				break;
			case SOUTH:
				if (player.getY() == object.getY()) yOffset = -1;
				else if (player.getY() == object.getY() - 1) yOffset = 1;
				else return false;
				break;
			}
	        boolean req = isForceWalk(id);

			int timer = req ? 2 : 60;
		    if (!meetsRequirement(player, id) && !req)
		        return false;
	        openDoubleDoor(player, ddd, object, xOffset, yOffset, timer);
	        return true;
	    }
	    DoorDefinition closedDef = findDoorAt(clickedLoc.getX(), clickedLoc.getY(), clickedLoc.getZ(), id);
	    
	    boolean isAlreadyOpen = (closedDef != null && openDoors.containsKey(closedDef.getCoordinate()));

	    if (isAlreadyOpen) {
	        player.getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 6, player.EffectVolume);
	        closeDoor(object);
	        return true;
	    } 
	    
		// Otherwise, treat as closed: open it
	    int xOffset = 0, yOffset = 0;
		
		// If it's a diagonal door, bypass the strict cardinal coordinate checks
		// (The actual walk math is handled dynamically in openSingleDoor now!)
		if (door.getId() == 11665) { 
		    xOffset = 0; yOffset = 0;
			// Do nothing here, just let it pass so it doesn't hit "return false;"
		} else {
			// --- STANDARD DOORS ---
			switch (door.getFace()) {
				case WEST:
					if (player.getX() == door.getX()) xOffset = -1;
					else if (player.getX() == door.getX() - 1) xOffset = 1;
					break; // FIX: Removed 'return false' so the door still opens!
				case NORTH:
					if (player.getY() == door.getY()) yOffset = 1;
					else if (player.getY() == door.getY() + 1) yOffset = -1;
					break;
				case EAST:
					if (player.getX() == door.getX()) xOffset = 1;
					else if (player.getX() == door.getX() + 1) xOffset = -1;
					break;
				case SOUTH:
					if (player.getY() == door.getY()) yOffset = -1;
					else if (player.getY() == door.getY() - 1) yOffset = 1;
					break;
			}

		}

		player.getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 6, player.EffectVolume);

		// FIX: Cleaned up the evaluation logic
		if (!meetsRequirement(player, door.getId())) {
			return false;
		}

		boolean req = isForceWalk(door.getId());
		int timer = req ? 3 : 60;
		openSingleDoor(player, door, xOffset, yOffset, timer);
		return true;
	}
	public static boolean isDiagonalDoor(int id) {
		int[] diagonalDoors = { 11665 }; // Add other diagonal door IDs here!
		for (int diagId : diagonalDoors) {
			if (id == diagId) return true;
		}
		return false;
	}
	public static void openSingleDoor(Player player, DoorDefinition door, int xOffset, int yOffset, int timer) {
		DoorDefinition openDef = door.getOpenDef();
		
		if (openDef == null) {
			// Fallback: legacy math-based opening
			int openFace = (door.getFace() + 3) % 4;
			int openX = door.getX();
			int openY = door.getY();
			switch (door.getFace()) {
				case WEST:  openX--; break;
				case NORTH: openY++; break;
				case EAST:  openX++; break;
				case SOUTH: openY--; break;
			}
			openDef = new DoorDefinition(door.getId(), openX, openY, door.getH(), openFace);
		}

		player.sendMessage("You open the door.");

		// 1. Determine the exact object type (0 for straight, 9 for diagonal)
		boolean isDiag = isDiagonalDoor(door.getId());
		int type = isDiag ? 9 : 0;

		// 2. BUG FIX: Use the correct 'type' to remove the closed door, not a hardcoded 9!
		GlobalObject removeDoor = new GlobalObject(-1, door.getX(), door.getY(), door.getH(), door.getFace(), type, -1);
		
		// 3. Spawn the open door using the exact same type
		GlobalObject openingDoor = new GlobalObject(openDef.getId(), openDef.getX(), openDef.getY(), openDef.getH(), openDef.getFace(), type, timer);
	    
	    // Replace the closed door in Region cache and GlobalObjects queue
	    World.getWorld().getGlobalObjects().add(removeDoor);
	    World.getWorld().getGlobalObjects().add(openingDoor);

	    // Track open state at runtime for manual clicking
	    openDoors.put(door.getCoordinate(), openDef);
	    openByLocation.put(openDef.getCoordinate(), door);

	    if (isForceWalk(door.getId())) {
	    	if (door.getId() == 11665) { 
	    		// --- RANGING GUILD DIAGONAL DOOR ---
	    		player.getMovementQueue().stop(); 

	    		// Correctly identifies ALL outside tiles (2657,3439 | 2658,3439 | 2657,3438)
	    		boolean isOutside = player.getX() <= 2658 && player.getY() >= 3438;

	    		// Set the exact landing coordinate based on which side we are on
	    		int targetX = isOutside ? 2659 : 2657; // Inside : Outside
	    		int targetY = isOutside ? 3437 : 3439; // Inside : Outside
	    		
	    		// Dynamically calculate the distance from the player's CURRENT spot
	    		int walkX = targetX - player.getX();
	    		int walkY = targetY - player.getY();
	    		
	    		player.getPA().walkTo(walkX, walkY);
	    		
	    	} else {
	    		// --- STANDARD DOORS ---
	    		player.getPA().walkTo(xOffset, yOffset);
	    	}
	    }
	}

	public static void closeDoor(WorldObject openDoorObject) {
	    ensureIndex();
	    int type = 0;
	    if (openDoorObject == null) return;

	    Location currentLoc = new Location(openDoorObject.getX(), openDoorObject.getY(), openDoorObject.getHeight());
	    
	    // 1. Double Door Check
	    DoubleDoorDefinition ddd = DoubleDoorDefinition.forCoordinate(currentLoc);
	    if (ddd != null) {
				type = 0;
	    	 GlobalObject closed1 = new GlobalObject(ddd.getClosed1().getId(), ddd.getClosed1().getX(), ddd.getClosed1().getY(), ddd.getClosed1().getH(), ddd.getClosed1().getFace(), 0, -1, -1);
		        GlobalObject closed2 = new GlobalObject(ddd.getClosed2().getId(), ddd.getClosed2().getX(), ddd.getClosed2().getY(), ddd.getClosed2().getH(), ddd.getClosed2().getFace(), 0, -1, -1);
		        GlobalObject closed3 = new GlobalObject(-1, ddd.getOpen1().getX(), ddd.getOpen1().getY(), ddd.getOpen1().getH(), ddd.getOpen1().getFace(), 0, -1, -1);
		        GlobalObject closed4 = new GlobalObject(-1, ddd.getOpen2().getX(), ddd.getOpen2().getY(), ddd.getOpen2().getH(), ddd.getOpen2().getFace(), 0, -1, -1);

	        // Add to GlobalObjects (this handles visuals, region cache, and removes the ticking 'open' versions)
		        World.getWorld().getGlobalObjects().add(closed1);
		        World.getWorld().getGlobalObjects().add(closed2);	        
		        World.getWorld().getGlobalObjects().add(closed3);
		        World.getWorld().getGlobalObjects().add(closed4);
	        //World.getWorld().getGlobalObjects().remove(ddd.getOpen1().getId(), ddd.getOpen1().getX(), ddd.getOpen1().getY(), ddd.getOpen1().getH());
	        //World.getWorld().getGlobalObjects().remove(ddd.getOpen2().getId(), ddd.getOpen2().getX(), ddd.getOpen2().getY(), ddd.getOpen2().getH());
	        // Cleanup local tracking
	        openDoors.remove(ddd.getClosed1().getCoordinate());
	        openDoors.remove(ddd.getClosed2().getCoordinate());
	        return;
	    }

	    // 2. Single Door Fallback
	    DoorDefinition closedDef = findDoorAt(openDoorObject.getX(), openDoorObject.getY(), openDoorObject.getHeight(), openDoorObject.id);
	    if (closedDef != null) {
	        // Build the permanent closed objects
			if(closedDef.getId() == 11665)
				type = 9;
			else 
				type = 0;
			//player.getPA().object(-1, openDoorObject.getX(), openDoorObject.getY(), openDoorObject.getFace(), 0);
			GlobalObject closedDoor = new GlobalObject(-1, openDoorObject.getX(), openDoorObject.getY(), openDoorObject.getHeight(), openDoorObject.getFace(), type, -1, -1);
			GlobalObject openDoor = new GlobalObject(closedDef.getId(), closedDef.getX(), closedDef.getY(), closedDef.getH(), closedDef.getFace(), type, -1, -1);
		        
	        // Add permanently
	        World.getWorld().getGlobalObjects().add(closedDoor);
	        World.getWorld().getGlobalObjects().add(openDoor);
	        
	        // Cleanup local tracking
	        openDoors.remove(closedDef.getCoordinate());
	        openByLocation.remove(new Location(openDoorObject.getX(), openDoorObject.getY(), openDoorObject.getHeight()));
	    }
	}


	/**
	 * Determines if the door visually forces the player to walk through it.
	 * Purely visual behavior. No access logic here.
	 */
	public static boolean isForceWalk(int id) {
		switch(id) {
			case 11665: // Diagonal doors
			case 15660: // Godwars
			case 15658:
			case 11728:
			case 2883:
			case 2882:
			case 9719: // Tutorial Island
			case 9720:
			case 9709:
			case 20925: // Fishing Guild
			case 1805:  // Champions Guild
			case 34463: // Farming Guild
			case 34464:
			case 9716:
			case 9398:
			case 24318: // Warriors Guild
			case 24306:
			case 24309:
			case 9721:
			case 9722:
			case 2069:  // Wydin's door
			case 9723:
				return true;
		}
		return false;
	}

	/**
	 * Determines if the player is allowed to open the door.
	 * Handles all chatbox messages and access denials.
	 */
	public static boolean meetsRequirement(Player p, int id) {
		switch(id) {
			case 2069: // Wydin's back room door
				if (p.playerEquipment[p.playerChest] == 1005) {
					return true;
				} else {
					p.getDH().sendStatement("Wydin yells: Hey! You can't go in there without a white apron!");
					p.nextChat = 0;
					return false;
				}

			case 11728:
				if(p.getY() == 9482) return true;
				if(p.getX() == 2601 && p.getY() == 9481) {
					return World.getWorld().getEventHandler().isRunning(p, "door_entry");
				}
				return false;

			case 9721: // Financial Advisor Tutorial
				if (p.tutorialProgress >= 27) {
					if (p.getX() == 3124 && p.getY() == 3124) {
						p.getPA().chatbox(6180);
						p.getDH().chatboxText(
								"The guide here will tell you all about making cash. Just click on",
								"him to hear what he's got to say.", "", "", "Financial advice");
						p.getPA().chatbox(6179);
						p.getPA().drawHeadicon(1, 7, 0, 0);
						return true;
					} else if (p.getX() == 3125 && p.getY() == 3124) {
						return false;
					}
				}
				return false;

			case 9722: // Prayer Tutorial
				if (p.tutorialProgress >= 28) {
					if (p.getX() == 3129 && p.getY() == 3124) {
						p.getPA().chatbox(6180);
						p.getDH().chatboxText(
								"Follow the path to the chapel and enter it.",
								"Once inside talk to the monk. He'll tell you all about the skill.",
								"", "", "Prayer");
						p.getPA().chatbox(6179);
						p.getPA().drawHeadicon(1, 8, 0, 0);
						return true;
					} else if (p.getX() == 3130 && p.getY() == 3124) {
						return false;
					}
				}
				return false;

			case 9716:
				if (p.tutorialProgress >= 11) {
					if(p.getX() == 3086 && p.getY() == 3126)
						p.getDH().sendDialogues(3042, -1);
					return true;
				}
				return false;

			case 9710:
				if (p.tutorialProgress > 9) {
					p.getDH().sendDialogues(3038, -1);
					return true;
				}
				return false;

			case 9709:
				return p.tutorialProgress >= 7;

			case 9398:
				if(p.tutorialProgress >= 2) {
					p.getDH().sendDialogues(3011, -1);
					return true;
				}
				return false;

			case 20925: // Fishing Guild
				if(p.getSkills().getActualLevel(Skill.FISHING) >= 65) {
					p.sendMessage("Welcome to the Fishing Guild.");
					return true;
				} else {
					p.sendMessage("You must have a Fishing level of 65 to enter.");
					return false;
				}

			case 1805: // Champions Guild
				if(p.questPoints >= 32) {
					p.sendMessage("Welcome to the Champions guild.");
					return true;
				} else {
					p.sendMessage("You must have a combined total of 32 Quest Points to enter.");
					return false;
				}

			case 59: // Port Sarim jewelry shop
				if (p.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.GATHERING_ITEMS
						&& !p.getItems().playerHasItem(MerlinsCrystal.EXCALIBUR)
						&& !p.getItems().bankContains(MerlinsCrystal.EXCALIBUR)) {

					// Spawn the Beggar temporarily for the player (adjust spawn method to match your base)
					server.model.npcs.NPCHandler.spawnNpc(3177, p.getX(), p.getY(), p.getHeight(), 0, 0, 0, 0, 0);
					p.sendMessage("A beggar approaches you as you open the door.");
				}
				return true;

			case 34463: // Farming Guild
			case 34464:
				return p.getSkills().getActualLevel(Skill.FARMING) >= 65;

			case 24306: // Warriors Guild top floor
			case 24309:
				if(p.getHeight() == 2) {
					if(p.getItems().playerHasItem(8851, 100)) {
						p.getWarriorsGuild().cycle();
						return true;
					}
					return false;
				}
				return true;

			case 24318: // Warriors Guild Entrance
				if(p.getSkills().getActualLevel(Skill.ATTACK) + p.getSkills().getActualLevel(Skill.STRENGTH) < 130) {
					p.sendMessage("You must have a combined total of 130 in Attack and Strength to enter.");
					return false;
				}
				p.sendMessage("Welcome to the Warriors guild.");
				return true;
		}
		return true; // Default allow for normal doors
	}
}