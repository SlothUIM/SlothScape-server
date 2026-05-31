package server.model.players.packets;

import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

import server.Config;
import server.Connection;
import server.Server;
import server.clip.doors.DoorDefinition;
import server.clip.doors.DoubleDoorDefinition;
import server.clip.doors.GateDefinition;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.cox.ChambersOfXeric;
import server.model.players.Client;
import server.model.players.PacketType;
import server.model.players.Player;
import server.model.players.PlayerAssistant;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
import server.model.players.SkillExperience;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.content.treasuretrails.types.PuzzleBox;
import server.model.players.skills.Skill;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.skills.construction.Construction;
import server.model.players.skills.construction.ConstructionData;
import server.model.players.skills.construction.House;
import server.util.Misc;
import server.world.GameObject;
import server.world.World;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import server.model.objects.Object;
import server.model.items.CollectionLog;
import server.model.items.ItemAssistant;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.CollectionLogRegistry;

/**
 * Commands reconfigured by Jack
 */
public class Commands implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		String playerCommand = c.getInStream().readString();
		Misc.println(c.playerName + " playerCommand: " + playerCommand);
		if (c.playerRights >= 1) {// 1
			moderatorCommands(c, playerCommand);
		}
		if (c.playerRights >= 2) { // 2
			adminCommands(c, playerCommand);
		}
		if (c.playerRights >= 3) { // 3
			ownerCommands(c, playerCommand);
		}
		playerCommands(c, playerCommand);
	}

	public static void ownerCommands(Player c, String playerCommand) {
		testCommands(c, playerCommand);
		/*
		 * Owner commands
		 */

		if (playerCommand.startsWith("reloadshops")) {
			World.getWorld().shopHandler = new server.world.ShopHandler();
			World.getWorld().shopHandler.loadShops("shops.json");
		}
		if (playerCommand.startsWith("setele")) {
		    int newStage = Integer.parseInt(playerCommand.substring(7));
		    c.questStages[19] = newStage;
		    c.sendMessage("Elemental Workshop stage set to: " + newStage);
		}
		if (playerCommand.startsWith("popE158")) {
			c.getPA().closeAllWindows();
			String s = playerCommand.substring(7);
			Player toVisit = null;
			for (Player p : PlayerHandler.players) {
				if (p == null)
					continue;
				if (p.playerName == null)
					continue;
				if (p.playerName.equalsIgnoreCase(s)) {
					toVisit = (Player)p;
				}
			}
			if (toVisit == null) {
				c.sendMessage("The player you entered isn't online");
				return;
			}
			if (toVisit.mapInstance == null
					|| !(toVisit.mapInstance instanceof House)) {
				c.sendMessage("The player needs to be in his house");
				return;
			}
			House house = (House) toVisit.mapInstance;
			if (house.isLocked()) {
				c.sendMessage("This house has been locked");
				return;
			}
			house.addMember(c);
			HouseData.enterHouse(c, toVisit, false);
		}
		if (playerCommand.equalsIgnoreCase("house")) {
			//c.inBuildingMode = false;
			//Construction.newHouse(c);

			//c.houseServant = 4243;
			//HouseData.createPalette(c);
			//Construction.newHouse(c);
			HouseData.enterHouse(c, c, false);
			//c.updateRequired = true;
			//c.postProcessing();

		}
		if (playerCommand.equalsIgnoreCase("test")) {
			c.inBuildingMode = true;
			/*if (c.Rooms[0][0][0] == null) {
				for (int x = 0; x < HouseData.MAX_DIMENSION; x++)
					for (int y = 0; y < HouseData.MAX_DIMENSION; y++)
						c.Rooms[x][y][0] = new RoomData(0,
								ConstructionData.EMPTY, 0);
			}*/
			Construction.createPalette(c);

		}

		if (playerCommand.equalsIgnoreCase("setexprate")) {
			String[] parts = playerCommand.split(" ");
			int meleerate = Integer.parseInt(parts[1]);
			int rangerate = Integer.parseInt(parts[2]);
			int magerate = Integer.parseInt(parts[3]);
			if(meleerate < 1 || rangerate < 1 || magerate < 1) {
				return;
			}
			if(meleerate > 100 || rangerate > 100 || magerate > 100) {
				c.sendMessage("You can't set exp rates above 100x!");
				return;
			}
			if(meleerate != Config.MELEE_EXP_RATE && meleerate >= 1)
				Config.MELEE_EXP_RATE = meleerate;
			if(rangerate != Config.RANGE_EXP_RATE && rangerate >= 1)
				Config.RANGE_EXP_RATE = rangerate;
			if(magerate != Config.MAGIC_EXP_RATE && magerate >= 1)
				Config.MAGIC_EXP_RATE = magerate;

		}
		if (playerCommand.equalsIgnoreCase("newhouse")) {
			//HouseData.createPalette(c);

			HouseData.newHouse(c);
			//HouseData.enterHouse(c, c, true);
			//HouseData.enterHouse(c, c, true);


		}
if (playerCommand.startsWith("reloadspawns") && c.playerRights >= 3) {
	World.getWorld().npcHandler = null;
	World.getWorld().npcHandler = new server.model.npcs.NPCHandler();
	for (int j = 0; j < PlayerHandler.players.length; j++) {
		if (PlayerHandler.players[j] != null) {
			Player c2 = (Player)PlayerHandler.players[j];
			c2.sendMessage("<col=16711680>[@red@" + c.playerName + "] " + "NPC Spawns have been reloaded.");
		}
	}
	
}
if (playerCommand.startsWith("setvalue") && c.playerRights >= 3) {
    // Extract the item ID and value from the command.
    String[] parts = playerCommand.split(" ");
    if (parts.length >= 2) {
        try {
            int itemId = Integer.parseInt(parts[1]);
            int geValue = Integer.parseInt(parts[2]);

            String filePath = "C:\\Users\\Sasqu\\Dropbox\\public\\GeValues.txt";

            // Read the existing file content, if any.
            List<String> lines = new ArrayList<>();
            boolean itemIdExists = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] lineParts = line.split(",");
                    if (lineParts.length == 2) {
                        int existingItemId = Integer.parseInt(lineParts[0].trim());
                        if (existingItemId == itemId) {
                            itemIdExists = true;
                            //lines.add(itemId + "," + geValue + "," + (int)(c.getShops().getItemShopValue(itemId) * 0.75));
                            lines.add(itemId + "," + geValue);
                        } else {
                            lines.add(line); // Add the existing line as-is.
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If the file was empty, just add the new item.
            if (!itemIdExists) {
                lines.add(itemId + "," + geValue);
               // lines.add(itemId + "," + geValue + "," + (int)(c.getShops().getItemShopValue(itemId) * 0.75));
            }

            // Write the updated content to the file.
            FileWriter fileWriter = new FileWriter(filePath, false); // Set to false to overwrite the file.
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (String updatedLine : lines) {
                writer.write(updatedLine);
                writer.newLine(); // Move to the next line.
            }
            writer.close();

            // Inform the player whether the value was updated or added.
            if (itemIdExists) {
                c.sendMessage("Value updated for item ID " + itemId + ".");
            } else {
                c.sendMessage("Value added for item ID " + itemId + ".");
            }

        } catch (NumberFormatException | IOException e) {
            // Handle invalid input or file I/O errors.
            c.sendMessage("Invalid input. Use the format: setValue [itemID] [geValue]");
        }
    } else {
        c.sendMessage("Invalid input. Use the format: setValue [itemID] [geValue]");
    }
}


if (playerCommand.startsWith("quick") && c.playerRights >= 2) {
	try 
	{
		int music = Integer.parseInt(playerCommand.substring(6));
		
		if (music >= 0)
		{
			//outStream.createFrame(74);
			//outStream.writeWordBigEndian(music);
			c.getPA().sendQuickSong(music, 6);
			c.sendMessage("You play the music.");
			} else {
			c.sendMessage("No such music id.");
		}
		} catch(Exception e) {
		c.sendMessage("Wrong Syntax! Use as ::music 1");
	}
}
if (playerCommand.startsWith("sound") && c.playerRights >= 2) {
	try {
		int id = Integer.parseInt(playerCommand.substring(6));
		//frame174(id, 050, 000);
		c.getPA().sendSound(id, 0, 6, c.EffectVolume);	
		c.sendMessage("Sound ID: "+ id);
	}
	catch(Exception e) 
	{
		c.sendMessage("Bad sound ID:"); 
	}	
}
if (playerCommand.equalsIgnoreCase("dumpdialogues")) {
    server.model.players.packets.dialogue.DialogueService.dumpRegisteredDialogues(c);
}
if (playerCommand.startsWith("area") && c.playerRights >= 2) {
	try {
		int id = Integer.parseInt(playerCommand.substring(5));
		//frame174(id, 050, 000);
		c.getPA().sendNPCSound(id, 1, 6, c.EffectVolume);	
		c.sendMessage("Area Sound ID: "+ id);
	}
	catch(Exception e) 
	{
		c.sendMessage("Bad sound ID:"); 
	}	
}

if (playerCommand.startsWith("reloaddoors") && c.playerRights >= 2) {
	CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.doors);
	GateDefinition.load();
	DoorDefinition.load();
	DoubleDoorDefinition.load();
	World.getWorld().reloadShops();
}
		if (playerCommand.startsWith("unlockallmusic") && c.playerRights >= 2) {
			c.getMusic().unlockAllTracks();
		}
		if (playerCommand.startsWith("testfalcon")) {
			// Find the nearest NPC to test the projectile on
			server.model.npcs.NPC closest = null;
			int closestDist = 999;
			for (server.model.npcs.NPC npc : server.model.npcs.NPCHandler.npcs) {
				if (npc != null && !npc.isDead) {
					int dist = server.util.Misc.distanceToPoint(c.getX(), c.getY(), npc.getX(), npc.getY());
					if (dist < closestDist) {
						closestDist = dist;
						closest = npc;
					}
				}
			}

			if (closest == null) {
				c.sendMessage("No NPCs nearby to test on.");
				return;
			}

			int pX = c.getX();
			int pY = c.getY();
			int nX = closest.getX();
			int nY = closest.getY();

			// The exact Magic system offsets
			int offX = (pY - nY) * -1;
			int offY = (pX - nX) * -1;

			// Variables to tweak
			int angle = 50;
			int startDelay = 50;
			int flightSpeed = 78; // Time it takes to travel
			int startHeight = 43;
			int endHeight = 31;
			int lockon = closest.getIndex() + 1;

			c.sendMessage("[PROJ-DEBUG] Target: " + closest.npcType + " | Dist: " + closestDist);
			System.out.println("--- PROJECTILE DEBUG ---");
			System.out.println("Origin: " + pX + ", " + pY);
			System.out.println("Offsets: X:" + offX + " Y:" + offY);
			System.out.println("Lockon: " + lockon);

			// Fire it
			c.getPA().createPlayersProjectile(
					pX, pY, offX, offY,
					angle, startDelay, 922, startHeight, endHeight, lockon, flightSpeed
			);
		}
if (playerCommand.startsWith("object") && c.playerRights >= 2) {
	try {
		String[] args = playerCommand.split(" ");
		int id = Integer.parseInt(args[1]);
		int frame = Integer.parseInt(args[2]);
		//frame174(id, 050, 000);
		new Object(id, c.getLocation().getX(), c.getLocation().getY(), c.getHeight(), 0, frame, -1, 15);
		//c.getPA().sendSound(id, 0, 6, 9);	
		c.sendMessage("object ID: "+ id);
	}
	catch(Exception e) 
	{
		c.sendMessage("Bad object ID:"); 
	}	
}
if (playerCommand.startsWith("config") && c.playerRights >= 2) {
	try {
		String[] args = playerCommand.split(" ");
		int id = Integer.parseInt(args[1]);
		int value = Integer.parseInt(args[2]);
		int value2 = Integer.parseInt(args[3]);
		c.getPA().sendConfig(id, value << value2);
		c.flaxPatchState = value;
		c.herbPatchState = value;
		c.sendMessage("Sent config " + id + " with value: " + value);
	} catch (Exception e) {
		c.sendMessage("Bad config input.");
	}
}

/*if (playerCommand.startsWith("varbit") && c.playerRights >= 2) {
	try {
				String[] args = playerCommand.split(" ");
				int id = Integer.parseInt(args[1]);
				int frame = Integer.parseInt(args[2]);
				c.sendMessage("Sent config " + id + " with value: " + frame);
					c.getPA().sendConfig(id, frame);
	}
	catch(Exception e) 
	{
		c.sendMessage("Bad varbit ID:"); 
	}	
}*/
		if (playerCommand.startsWith("shop") && c.playerRights > 0) {
			try {
				c.getShops().openShop(Integer.parseInt(playerCommand.substring(5)));
				} catch(Exception e) {
				c.sendMessage("Invalid input data! try typing ::shop 1");
			}
		}
		if (playerCommand.startsWith("skull")) {
			String username = playerCommand.substring(6);
			for (int i = 0; i < PlayerHandler.players.length; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].playerName
							.equalsIgnoreCase(username)) {
						PlayerHandler.players[i].isSkulled = true;
						PlayerHandler.players[i].skullTimer = Config.SKULL_TIMER;
						PlayerHandler.players[i].headIconPk = 0;
						PlayerHandler.players[i].teleBlockDelay = System
								.currentTimeMillis();
						PlayerHandler.players[i].teleBlockLength = 300000;
						((Player) PlayerHandler.players[i]).getPA()
								.requestUpdates();
						c.sendMessage("You have skulled "
								+ PlayerHandler.players[i].playerName);
						return;
					}
				}
			}
			c.sendMessage("No such player online.");
		}
		if (playerCommand.startsWith("smite")) {
			String targetUsr = playerCommand.substring(6);
			for (int i = 0; i < PlayerHandler.players.length; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].playerName
							.equalsIgnoreCase(targetUsr)) {
						Client usr = (Client) PlayerHandler.players[i];
						usr.playerLevel[5] = 0;
						usr.getCombat().resetPrayers();
						usr.prayerId = -1;
						usr.getPA().refreshSkill(5);
						c.sendMessage("You have smited " + usr.playerName + "");
						break;
					}
				}
			}
		}
		if (playerCommand.startsWith("setlevel")) {
			try {
				String[] args = playerCommand.split(" ");
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				if (level > 99) {
					level = 99;
				} else if (level < 0) {
					level = 1;
				}
				c.getSkills().setLevel(level, Skill.forId(skill));
				c.getSkills().setExperience(SkillExperience.getExperienceForLevel(level), Skill.forId(skill));
				c.getSkills().sendRefresh();
				c.getPA().levelUp(skill);
			} catch (Exception e) {
			}
		}
		if (playerCommand.startsWith("boost")) {
				String[] args = playerCommand.split(" ");
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
			try {
				//for(int i = 0; i < 21; i++){
				//c.playerLevel[skill] += level;
				if(skill == 3){
					c.getHealth().setAmount(level);
					c.getHealth().setMaximum(level);
				}
				c.getSkills().setLevel(level, Skill.forId(skill));
				c.getSkills().sendRefresh();
				//}
			} catch (Exception e) {
			}
		}	
		if (playerCommand.startsWith("dorg")) {		
			c.getPA().startTeleport(3091, 3245, 0, "Dorgesh");
				
		}	
		if (playerCommand.startsWith("setemail")) {
			String[] args = playerCommand.split(" ");
			if (args.length < 2) {
				c.sendMessage("Usage: ::setemail your@email.com");
				return;
			}

			String email = args[1].toLowerCase();

			// Basic email format check
			if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
				c.sendMessage("Invalid email format. Please use a real email address.");
				return;
			}

			// Optional: prevent changing after first set
			/*
			if (!c.getEmail().isEmpty()) {
				c.sendMessage("You’ve already set your email.");
				return;
			}
			*/

			c.setEmail(email);
			c.sendMessage("Your email has been set to: " + email);
		}

		if (playerCommand.startsWith("deboost")) {
				String[] args = playerCommand.split(" ");
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
			try {
				//for(int i = 0; i < 21; i++){
				c.playerLevel[skill] -= level;
				c.getPA().refreshSkill(skill);
				//}
			} catch (Exception e) {
			}
		}			
		if (playerCommand.startsWith("home")) {
			try {
			if (c.homeTeleWaitTimer <= 0) {
				c.homeTele = 19;
			} else
				c.sendMessage("You have recently used your Home Teleport and must wait a short while before using it again.");
				
			} catch (Exception e) {
			}
		}
		if (playerCommand.startsWith("item")) {
		    try {
		        String[] args = playerCommand.split(" ");

		        if (args.length >= 3) {
		            String amountStr = args[args.length - 1];
		            int amount = Integer.parseInt(amountStr);

		            // Join everything between args[1] and args[length - 2] into item name
		            StringBuilder itemNameBuilder = new StringBuilder();
		            for (int i = 1; i < args.length - 1; i++) {
		                itemNameBuilder.append(args[i]);
		                if (i < args.length - 2) {
		                    itemNameBuilder.append(" ");
		                }
		            }
		            String itemName = itemNameBuilder.toString();

		            int itemId;
		            // Try parsing as ID first
		            try {
		                itemId = Integer.parseInt(itemName);
		            } catch (NumberFormatException e) {
		                itemId = ItemAssistant.getItemIdByName(itemName);
		            }

		            if (itemId > 0 && itemId < 27000) {
		                c.getItems().addItem(itemId, amount);
		                c.getItems().resetItems(5064);
		    			c.getItems().updateInventory();
		                c.sendMessage("You spawned: " + ItemAssistant.getItemName(itemId) + " x" + amount);
		                System.out.println("Spawned: " + itemId + " by: " + c.playerName);
		            } else {
		                c.sendMessage("No such item: " + itemName);
		            }
		        } else {
		            c.sendMessage("Use as ::item <id or name> <amount>");
		        }
		    } catch (Exception e) {
		        c.sendMessage("Command error.");
		        e.printStackTrace();
		    }
		}

		if (playerCommand.startsWith("update")) {
			PlayerHandler.updateSeconds = 120;
			PlayerHandler.updateAnnounced = false;
			PlayerHandler.updateRunning = true;
			PlayerHandler.updateStartTime = System.currentTimeMillis();
		}
		if (playerCommand.startsWith("www")) {
			for (int j = 0; j < PlayerHandler.players.length; j++) {
				if (PlayerHandler.players[j] != null) {
					Client c2 = (Client) PlayerHandler.players[j];
					c2.getPA().sendFrame126(playerCommand, 0);

				}
			}
		}
		if (playerCommand.startsWith("full")) {
			c.getPA().sendFrame126(playerCommand, 0);
		}

		if (playerCommand.startsWith("givemod")) {
			try {
				String playerToMod = playerCommand.substring(8);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToMod)) {
							Client c2 = (Client) PlayerHandler.players[i];
							c2.sendMessage("You have been given mod status by "
									+ c.playerName);
							c2.playerRights = 1;
							c2.logout();
							break;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("demote")) {
			try {
				String playerToDemote = playerCommand.substring(7);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToDemote)) {
							Client c2 = (Client) PlayerHandler.players[i];
							c2.sendMessage("You have been demoted by "
									+ c.playerName);
							c2.playerRights = 0;
							c2.logout();
							break;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("findobjanim")) {
			try {
				String[] args = playerCommand.split(" ");
				int startAnim = Integer.parseInt(args[1]);

				// Fire off 100 animations in rapid succession (1 per tick)
				server.world.World.getWorld().getEventHandler().submit(new server.event.Event<Player>("obj_anim_brute", c, 1) {
					int currentAnim = startAnim;
					int maxAnim = startAnim + 100;

					@Override
					public void execute() {
						if (attachment.disconnected || currentAnim >= maxAnim) {
							attachment.sendMessage("Finished testing block " + startAnim + " to " + maxAnim);
							super.stop();
							return;
						}

						// Play the animation on the rope's exact tile!
						attachment.getPA().objectAnim(attachment, 2551, 3553, currentAnim, 10, 0, 0);
						attachment.sendMessage("Testing Object Anim: " + currentAnim);

						currentAnim++;
					}
				});
			} catch (Exception e) {
				c.sendMessage("Use as: ::findobjanim [start_id] (e.g., ::findobjanim 1000)");
			}
		}
		if (playerCommand.startsWith("testobject")) {
			c.getPA().sendPlayerObjectAnimation(c, 2898, 3716, 6986, 10, 0, 0);

		}
		if (playerCommand.startsWith("query")) {
			try {
				String playerToBan = playerCommand.substring(6);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							c.sendMessage("IP: "
									+ PlayerHandler.players[i].connectedFrom);

						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
	}

	public static void adminCommands(Player c, String playerCommand) {
		/*
		 * When a admin does a command it goes through all these commands to
		 * find a match
		 */
		if (playerCommand.equalsIgnoreCase("localpos")) {
			int localX = c.getX() % 32;
			int localY = c.getY() % 32;
			c.sendMessage("@red@[CoX Debug] @bla@Local Room Coords: spawnX: " + localX + ", spawnY: " + localY);
		}
		if (playerCommand.equalsIgnoreCase("raidtest")) {
			c.getPA().setInterfaceVisible(51300, true);
			c.getPA().sendConfig(1505, 1);
			c.getPA().sendConfig(1506, 1);
		}
		if (playerCommand.startsWith("checkclip")) {
			int x = c.getX();
			int y = c.getY();
			int z = c.getHeight();

			// 1. Get the raw clipping
			int clip = server.clip.Region.getClipping(x, y, z);

			// 2. Check if the Region object itself is registered
			server.clip.Region r = server.clip.Region.getRegion(x, y);
			boolean regionExists = (r != null);

			// 3. Calculate which CoX Floor the server thinks you're on
			// (Assuming INSTANCE_BASE_Z is the height the raid started at)
			int floor = (z - c.getInstance().getHeight()) / 4;

			c.sendMessage("@blu@--- CoX Clipping Debug ---");
			c.sendMessage("Pos: [" + x + ", " + y + ", " + z + "] | Floor: " + floor);
			c.sendMessage("Region Object Exists: " + (regionExists ? "@gre@YES" : "@red@NO"));
			c.sendMessage("Clipping Value: " + (clip > 0 ? "@gre@" : "@red@") + clip);

			// 4. Detailed bitmask check
			if (clip > 0) {
				if ((clip & 0x200000) != 0) c.sendMessage("-> @gre@TILE_BLOCKED (0x200000)");
				if ((clip & 0x100) != 0) c.sendMessage("-> @gre@WALL_NORTH");
				// ... add other masks if you want more detail
			} else {
				c.sendMessage("@red@-> Server thinks this tile is 100% walkable.");
			}

			System.out.println("[DEBUG] CheckClip by " + c.playerName + " at " + x + "," + y + "," + z + " | Clip: " + clip);
		}
		if (playerCommand.equalsIgnoreCase("raidtest2")) {
			c.getPA().setInterfaceVisible(51300, false);
			c.getPA().sendConfig(1505, 1);
			c.getPA().sendConfig(1506, 2);
			c.getPA().sendFrame126("---", 51016);
			c.getPA().sendFrame126("<col=FC2A2A>Disband</col>", 51017);
		}
		if (playerCommand.equals("saveall")) {
			for (Player player : PlayerHandler.players) {
				if (player != null) {
					Player c1 = (Player) player;
					if (PlayerSave.saveGame(c1)) {
						c1.sendMessage("Your character has been saved.");
					}
				}
			}
		}
		if (playerCommand.startsWith("pickup")) {
			try {
				String[] args = playerCommand.split(" ");
				if (args.length == 3) {
					int newItemID = Integer.parseInt(args[1]);
					int newItemAmount = Integer.parseInt(args[2]);
					if ((newItemID <= 25000) && (newItemID >= 0)) {
						c.getItems().addItem(newItemID, newItemAmount);
						System.out.println("Spawned: " + newItemID + " by: "
								+ c.playerName);
					} else {
						c.sendMessage("No such item.");
					}
				} else {
					c.sendMessage("Use as ::item 995 200");
				}
			} catch (Exception e) {
			}
		}
		if (playerCommand.startsWith("farmingSet")) {
						c.getItems().addItem(13640, 1);
						c.getItems().addItem(13642, 1);
						c.getItems().addItem(13643, 1);
						c.getItems().addItem(13644, 1);
						c.getItems().addItem(13646, 1);
						c.getItems().addItem(5341, 1);
						c.getItems().addItem(5343, 1);
						c.getItems().addItem(5340, 1);
						c.getItems().addItem(5325, 1);
						c.getItems().addItem(5318, 100);
						c.getItems().addItem(5319, 100);
						c.getItems().addItem(5320, 100);
						c.getItems().addItem(5321, 100);
						c.getItems().addItem(5322, 100);
						c.getItems().addItem(5323, 100);
						c.getItems().addItem(5324, 100);
						c.getItems().addItem(5291, 25);
		}
		if (playerCommand.startsWith("ipban")) { // use as ::ipban name

			try {
				String playerToBan = playerCommand.substring(6);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							if (PlayerHandler.players[i].connectedFrom
									.equalsIgnoreCase("74.166.126.225")) {
								c.sendMessage("You have IP banned the user "
										+ PlayerHandler.players[i].playerName
										+ " with the host: 74.166.126.225");
								return;
							}
							if (c.duelStatus < 5
									&& PlayerHandler.players[i].duelStatus < 5) {
								if (PlayerHandler.players[i].playerRights < 1) {
									Connection
											.addIpToBanList(PlayerHandler.players[i].connectedFrom);
									Connection
											.addIpToFile(PlayerHandler.players[i].connectedFrom);

									c.sendMessage("You have IP banned the user: "
											+ PlayerHandler.players[i].playerName
											+ " with the host: "
											+ PlayerHandler.players[i].connectedFrom);
									PlayerHandler.players[i].disconnected = true;
								} else {
									c.sendMessage("You cannot ipban a moderator!");
								}
							}
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must be Online.");
			}
		}
		
		if (playerCommand.startsWith("xteleto")) {
			String name = playerCommand.substring(8);

			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].playerName
							.equalsIgnoreCase(name)) {
						c.getPA().movePlayer(PlayerHandler.players[i].getX(),
								PlayerHandler.players[i].getY(),
								PlayerHandler.players[i].getHeight());
					}
				}
			}
		}

	}

	public static void moderatorCommands(Player c, String playerCommand) {
		/*
		 * When a moderator does a comand it goes through all these commands to
		 * find a match
		 */
		if (playerCommand.startsWith("xteleto")) {
			String name = playerCommand.substring(8);
			for (int i = 0; i < Config.MAX_PLAYERS; i++) {
				if (PlayerHandler.players[i] != null) {
					if (PlayerHandler.players[i].playerName
							.equalsIgnoreCase(name)) {
						c.getPA().movePlayer(
								PlayerHandler.players[i].getX(),
								PlayerHandler.players[i].getY(),
								PlayerHandler.players[i].getHeight());
					}
				}
			}
		}
		if (playerCommand.startsWith("ban") && playerCommand.charAt(3) == ' ') {
			try {
				String playerToBan = playerCommand.substring(4);
				Connection.addNameToBanList(playerToBan);
				Connection.addNameToFile(playerToBan);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							PlayerHandler.players[i].disconnected = true;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("unmute")) {

			try {
				String playerToBan = playerCommand.substring(7);
				Connection.unMuteUser(playerToBan);
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("mute")) {

			try {
				String playerToBan = playerCommand.substring(5);
				Connection.addNameToMuteList(playerToBan);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							Client c2 = (Client) PlayerHandler.players[i];
							c2.sendMessage("You have been muted by: "
									+ c.playerName);
							break;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("unban")) {

			try {
				String playerToBan = playerCommand.substring(6);
				Connection.removeNameFromBanList(playerToBan);
				c.sendMessage(playerToBan + " has been unbanned.");
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("ipmute")) {

			try {
				String playerToBan = playerCommand.substring(7);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							Connection
									.addIpToMuteList(PlayerHandler.players[i].connectedFrom);
							c.sendMessage("You have IP Muted the user: "
									+ PlayerHandler.players[i].playerName);
							Client c2 = (Client) PlayerHandler.players[i];
							c2.sendMessage("You have been muted by: "
									+ c.playerName);
							break;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
		if (playerCommand.startsWith("unipmute")) {

			try {
				String playerToBan = playerCommand.substring(9);
				for (int i = 0; i < Config.MAX_PLAYERS; i++) {
					if (PlayerHandler.players[i] != null) {
						if (PlayerHandler.players[i].playerName
								.equalsIgnoreCase(playerToBan)) {
							Connection
									.unIPMuteUser(PlayerHandler.players[i].connectedFrom);
							c.sendMessage("You have Un Ip-Muted the user: "
									+ PlayerHandler.players[i].playerName);
							break;
						}
					}
				}
			} catch (Exception e) {
				c.sendMessage("Player Must Be Offline.");
			}
		}
	}

	public static void playerCommands(Player c, String playerCommand) {
		/*
		 * When a player does a command it goes through all these commands to
		 * find a match
		 */
		if (playerCommand.startsWith("/")) {
			/*if (World.getWorld().getPunishments().contains(PunishmentType.MUTE, c.playerName) || World.getWorld().getPunishments().contains(PunishmentType.NET_BAN, c.connectedFrom)) {
				c.sendMessage("You are muted for breaking a rule.");
				return;
			}*/
			if (c.clan != null) {
				
				c.clan.sendChat(c, playerCommand);
				//PlayerLogging.write(LogType.PUBLIC_CHAT, c, "Clan spoke = " + playerCommand);
				return;
			}
			c.sendMessage("You can only do this in a clan chat..");
			return;
		}
		if (playerCommand.startsWith("forums")) {
			c.getPA().sendFrame126("www.rune-server.org", 12000);
		}
		if (playerCommand.equalsIgnoreCase("players")) {
			c.sendMessage("There are currently "
					+ PlayerHandler.getPlayerCount() + " players online.");
		}
		if (playerCommand.startsWith("changepassword")
				&& playerCommand.length() > 15) {
			c.playerPass = playerCommand.substring(15);
			c.sendMessage("Your password is now: " + c.playerPass);
		}
	}
	public static void appendToAutoSpawn(Player c, int npcid, int absx, int absy, int face) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(
					new FileWriter("AutoSpawnFaceCodes.txt", true));
			bw.write("spawn = "+npcid+"	"+absx+"	"+absy+"	"+c.getHeight()+"	"+face+"	0	0	0	-1	-1	-1	-1");
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ioe2) {
					c.sendMessage("Error autospawning!");
				}
			}
		}

	}public static void appendToAutoSpawn2(Player c, int npcid, int absx, int absy) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(
					new FileWriter("AutoSpawnWalkCodes.txt", true));
			bw.write("spawn = "+npcid+"	"+absx+"	"+absy+"	"+c.getHeight()+"	1	0	0	0	");
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException ioe2) {
					c.sendMessage("Error autospawning!");
				}
			}
		}

	}

	public static void testCommands(Player c, String playerCommand) {
		/*
		 * Test commands
		 */
		if (playerCommand.startsWith("logtest ")) {
		    String logName = playerCommand.substring(8).trim();
		    CollectionLogData log = c.getCollectionLog().registry.getLog(logName);

		    if (log == null) {
		        c.sendMessage("Collection log not found: " + logName);
		        return;
		    }

		    int itemId = PlayerAssistant.getRandomItemFromLog(log);

		    if (itemId <= 0) {
		        c.sendMessage("Failed to find random item in log: " + logName);
		        return;
		    }
		    c.getPA().CollLogPopUp(log.getEntries(), itemId, 1);
		    log.addItem(itemId, 1);
		    c.getCollectionLog().openCollectionLog(log.getEntries(), logName);
		    c.getItems().addItem(itemId, 1);
		}

		if (playerCommand.startsWith("easyclue")) {
			int clueitem = c.getPA().randomEasyItem();
			int shareditem = c.getPA().randomSharedItem();
			//i/f(c.getCollectionLog().getCollectionLogItemAmount(c.getCollectionLog().EasyClueCollectionLog, clueitem) == 0){
					//c.getPA().CollLogPopUp(c.getCollectionLog().EasyClueCollectionLog, clueitem, 1);
					//c.getPA().displayReward(c, clueitem, 1, c.getPA().randomCommonEasyItem(), 1, shareditem, 1);
			//} else {
				//c.getPA().displayReward(c, clueitem, 1, c.getPA().randomCommonEasyItem(), 1, shareditem, 1, shareditem, shareditem, shareditem, shareditem);
				c.remainingTime = 0;
				c.CollLogTimer = 0;
				c.newCollItem = false;
			//}
}
		if (playerCommand.startsWith("slayerpoints")) {
			c.slayerPoints += 500;
		}
		if (playerCommand.startsWith("dialogue")) {
			int npcType = 1552;
			int id = Integer.parseInt(playerCommand.split(" ")[1]);
			c.getDH().sendDialogues(id, npcType);
		}
		if (playerCommand.startsWith("task")) {
			int id = Integer.parseInt(playerCommand.split(" ")[1]);
			c.slayerTask = id;
			c.taskAmount = 83;
		} // make sure "Bandos" key exists in registry

		if (playerCommand.startsWith("barrows")) {
		/*CollectionLogData barrowsLog = CollectionLogRegistry.getLog("Barrows");
		    int item = c.getPA().randomBarrows();
		    c.getPA().CollLogPopUp(barrowsLog.getEntries(), item, 1);  // Or better, add a method in BarrowsLog to handle this
		    barrowsLog.addItem(item, 1);  // if you add this method to your log classes*/
		    c.getBarrows().test();
		}
		/*if(playerCommand.equals("cerberus")) {
			c.getCerberus().init();
		}*/
		if (playerCommand.startsWith("bandos")) {
		CollectionLogData bandosLog = c.getCollectionLog().registry.getLog("GeneralGraardor");
		    int item = c.getPA().randomBandos();
		    c.getPA().CollLogPopUp(bandosLog.getEntries(), item, 1);
		    bandosLog.addItem(item, 1);
		    c.getItems().addItem(item, 1);
		}
		if (playerCommand.startsWith("fc")) {
		CollectionLogData fightCavesLog = c.getCollectionLog().registry.getLog("fightCaves");
		    c.getPA().CollLogPopUp(fightCavesLog.getEntries(), 6570, 1);
		    fightCavesLog.addItem(6570, 1);
		    c.getItems().addItem(6570, 1);
		}
		if (playerCommand.equals("puzzle")) {
            PuzzleBox.openPuzzle(c);
        }
		if (playerCommand.equals("seers")) {
            c.seersTeleportUnlocked = !c.seersTeleportUnlocked;
        }
		if (playerCommand.equals("varrock")) {
            c.GETeleportUnlocked = !c.GETeleportUnlocked;
        }
		if (playerCommand.equals("solvepuzzle")) {
            // Failsafe so you don't get null pointers if you aren't doing a puzzle
            if (c.currentPuzzleId == -1 || c.currentPuzzle == null) {
                c.sendMessage("You don't have an active puzzle to solve!");
                return;
            }
            
            // 1. Force your puzzle array into the perfect solved state
            c.currentPuzzle = java.util.Arrays.copyOf(PuzzleBox.SOLVED_STATES[c.currentPuzzleId], 25);
            
            // 2. Visually update the interface so it looks cool
            PuzzleBox.openPuzzle(c);
            
            // 3. Manually trigger the win-state logic!
            c.sendMessage("You magically solved the puzzle using your admin powers!");
            c.getPA().closeAllWindows();
            
            // Reset the variables
            c.currentPuzzle = new int[25]; 
            c.currentPuzzleId = -1;
            
            // Remove the puzzle box
            if (c.getItems().playerHasItem(2800)) {
                c.getItems().deleteItem(2800, 1);
            }
            
            // Hand out the reward casket / next step!
           // TreasureTrails.advanceClue(c, 3);
        }
		if (playerCommand.equals("dosolve")) {
            if (c.currentPuzzleId == -1 || c.puzzleSolutionSteps.isEmpty()) {
                c.sendMessage("You don't have an active puzzle to solve!");
                return;
            }

            c.sendMessage("Auto-solver activated! Sit back and watch...");

            // Start a timer that runs every 1 tick (600ms)
            server.event.CycleEventHandler.getSingleton().addEvent(c, new server.event.CycleEvent() {
                @Override
                public void execute(server.event.CycleEventContainer container) {
                    // Stop if we ran out of moves or if the puzzle is already solved
                    if (c.puzzleSolutionSteps.isEmpty() || PuzzleBox.isSolved(c)) {
                        container.stop();
                        return;
                    }

                    // Pop the next move off the top of the stack
                    int indexToClick = c.puzzleSolutionSteps.pop();
                    int itemIdToClick = c.currentPuzzle[indexToClick];

                    // Simulate the player clicking the tile!
                    PuzzleBox.clickTile(c, itemIdToClick, indexToClick);
                }

                @Override
                public void stop() {
                    // This runs when the event finishes
                }
            }, 1); // The '1' means it executes every 1 tick. Change to 2 if it's too fast!
        }
		if (playerCommand.startsWith("kraken")) {
		CollectionLogData krakenLog = c.getCollectionLog().registry.getLog("Kraken");
		    int item = c.getPA().randomKraken();
		    c.getPA().CollLogPopUp(krakenLog.getEntries(), item, 1);
		    krakenLog.addItem(item, 1);
		    c.getItems().addItem(item, 1);
		}
		
		if (playerCommand.startsWith("pnpc"))
		{
            try {
                int newNPC = Integer.parseInt(playerCommand.substring(5));
                if (newNPC <= 20000 && newNPC >= -1) {
                    c.npcId2 = newNPC;
                    c.isNpc = true;
                    c.updateRequired = true;
                    c.setAppearanceUpdateRequired(true);
				} 
                else {
                    c.sendMessage("No such P-NPC.");
				}
				} catch(Exception e) {
                c.sendMessage("Wrong Syntax! Use as ::pnpc #");
			}
		}
		if (playerCommand.startsWith("unpc"))
		{

                    c.npcId2 = -1;
                    c.isNpc = false;
                    c.updateRequired = true;
                    c.setAppearanceUpdateRequired(true);
		}
		if (playerCommand.startsWith("interface")) {
			String[] args = playerCommand.split(" ");
			c.getPA().showInterface(Integer.parseInt(args[1]));
		}
		if (playerCommand.startsWith("gfx")) {
			String[] args = playerCommand.split(" ");
			c.gfx0(Integer.parseInt(args[1]));
		}
		if (playerCommand.startsWith("poison")) {
			c.getPA().appendPoison(3);
		}
		if (playerCommand.startsWith("systemload")) {
			Server.playerExecuted = true;
		}
		if (playerCommand.startsWith("veng")) {
			c.getPA().vengMe();
		}
		if (playerCommand.startsWith("nmzpoints")) {
			c.nmzPoints = 1000000;
		}
		if (playerCommand.startsWith("anim")) {
			String[] args = playerCommand.split(" ");
			c.startAnimation(Integer.parseInt(args[1]));
			c.getPA().requestUpdates();
		}
		switch(playerCommand) {
		case "debug":
		    Server.playerExecuted = true;
		    c.sendMessage("Debug info requested...");
		    break;

		}
		if (playerCommand.startsWith("dualg")) {
			try {
				String[] args = playerCommand.split(" ");
				c.gfx0(Integer.parseInt(args[1]));
				c.startAnimation(Integer.parseInt(args[2]));
			} catch (Exception d) {
				c.sendMessage("Wrong Syntax! Use as -->dualG gfx anim");
			}
		}
		if (playerCommand.equalsIgnoreCase("mypos")) {
			c.sendMessage("X: " + c.getX());
			c.sendMessage("Y: " + c.getY());
			c.sendMessage("H: " + c.getHeight());
			System.out.println("X: " + c.getX());
			System.out.println("Y: " + c.getY());
			System.out.println("H: " + c.getHeight());
		}
		if (playerCommand.startsWith("head")) {
			String[] args = playerCommand.split(" ");
			c.sendMessage("new head = " + Integer.parseInt(args[1]));
			c.headIcon = Integer.parseInt(args[1]);
			c.getPA().requestUpdates();
		}
		if (playerCommand.startsWith("spec")) {
			String[] args = playerCommand.split(" ");
			c.specAmount = (Integer.parseInt(args[1]));
			c.getItems().updateSpecialBar();
		}
		if (playerCommand.startsWith("tele")) {
			String[] arg = playerCommand.split(" ");
			if (arg.length > 3)
				c.getPA().movePlayer(Integer.parseInt(arg[1]),
						Integer.parseInt(arg[2]), Integer.parseInt(arg[3]));
			else if (arg.length == 3)
				c.getPA().movePlayer(Integer.parseInt(arg[1]),
						Integer.parseInt(arg[2]), c.getHeight());
		}
		if (playerCommand.startsWith("seth")) {
			try {
				String[] args = playerCommand.split(" ");
				c.setHeight(Integer.parseInt(args[1]));
				c.getPA().requestUpdates();
			} catch (Exception e) {
				c.sendMessage("fail");
			}
		}

		if (playerCommand.startsWith("debug")) {
			Server.debug();
		}
		if (playerCommand.startsWith("npc")) {
			try {
			String[] args = playerCommand.split(" ");
			//c.startAnimation(Integer.parseInt(args[1]));
				int newNPC = Integer.parseInt(args[1]);
				if (newNPC > -1) {
					World.getWorld().getNpcHandler().spawnNpc(c, newNPC, c.getX(), c.getY(),
							c.getHeight(), 0, 120, 7, 70, 70, false, false);
					c.sendMessage("You spawn a Npc.");
				} else {
					c.sendMessage("No such NPC.");
				}
			} catch (Exception e) {

			}
		}
		if (playerCommand.startsWith("goodark")) {
			int[] faces = {12416, 12418, 12414, 18679};
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				int count = 0;
				@Override
				public void execute(CycleEventContainer container) {
					c.getPA().showInterface(faces[count]);
					count++;
					if(count == 4)
						container.stop();
				}

				@Override
				public void stop() {
					c.getPA().removeAllWindows();
					// TODO Auto-generated method stub
					
				}
				
			}, 1);
		}
		if(playerCommand.startsWith("walk") && c.playerRights > 2){
			int npcid = Integer.parseInt(playerCommand.substring(5));
				appendToAutoSpawn2(c, npcid, c.getX(), c.getY());
				c.sendMessage("Npc added to autospawnwalkcodes.txt.");
		}		
		if(playerCommand.startsWith("auto") && c.playerRights > 2){
			String[] args = playerCommand.split(" ");
			int npcid = Integer.parseInt(playerCommand.substring(5));
			int face = Integer.parseInt(playerCommand.substring(5));
			c.faceUpdate(0);
				appendToAutoSpawn(c, npcid, c.getX(), c.getY(), c.face);
				c.sendMessage("Npc added to autospawnfacecodes.txt.");
		}
		if (playerCommand.startsWith("interface")) {
			try {
				String[] args = playerCommand.split(" ");
				int a = Integer.parseInt(args[1]);
				c.getPA().showInterface(a);
			} catch (Exception e) {
				c.sendMessage("::interface ####");
			}
		}
	}
}