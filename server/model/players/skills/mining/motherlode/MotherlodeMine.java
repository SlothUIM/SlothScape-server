package server.model.players.skills.mining.motherlode;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import server.clip.ObjectDef;
import server.clip.Region;
import server.clip.WorldObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCClipping;
import server.model.npcs.NPCDumbPathFinder;
import server.model.npcs.NPCHandler;
import server.model.objects.Object;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.Location;
import server.world.World;
import server.world.objects.GlobalObject;

public class MotherlodeMine {
    private static List<OreTile> allTiles = new ArrayList<>();
    private static List<VeinGroup> veins = new ArrayList<>();
    public static boolean leftWheelActive;
	public static boolean rightWheelActive;
	private static int maxOres;
    
    public static void process() {
        for (OreTile tile : allTiles) {
            // Process the tile timer
            tile.process();

            // If the tile is depleted and timer ran out, move the vein
            /*if (tile.objectId >= 26665 && tile.objectId <= 26668 && (tile.respawnTimer <= 0 || tile.getRespawnTicks() <= 0)) {
            	moveVeinToNewSpot(tile); // this method picks a new spot and respawns the ore
            }*/
            
            // Chance to spawn new veins on empty tiles (optional extra random spawning)
            /*if (!tile.occupied && Misc.random(50) < 5) { 
                VeinGroup vein = pickRandomVein(tile);
                if (vein != null) {
                if (canPlaceVeinAt(tile, vein)) {
                    placeVein(tile, vein);
                }
                }
            }*/
        }
    }

    public static NPC spawnPayDirtNpc(Player player) {
        int npcId = 6564;
        NPC payDirt = World.getWorld().npcHandler.spawnNpc(player, npcId, 3747, 5672, 0, 1, 0, 0, 0, 0, false, false);

        // FIX: Use the cached path instead of generating a new one
        payDirt.payDirtPath = PAY_DIRT_PATH;
        payDirt.payDirtTargetIndex = 0;
        payDirt.walkingHome = false;
        payDirt.setWalkQueue(PAY_DIRT_PATH);

        // FIX: Store the Index, not the Player object, to prevent permanent memory leaks
        payDirt.payDirtOwnerId = player.getIndex();

        return payDirt;
    }

    public static List<Location> getPayDirtPath() {
        return PAY_DIRT_PATH;
    }
    // FIX: Cache these globally so we don't allocate thousands of objects a second
    private static final int[][] CARDINAL_OFFSETS = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

    private static final List<Location> PAY_DIRT_PATH = Arrays.asList(
            new Location(3747, 5672, 0),
            new Location(3743, 5672, 0),
            new Location(3743, 5660, 0),
            new Location(3747, 5660, 0)
    );

    private static final OreStack[] PAY_DIRT_REWARDS = new OreStack[] {
            new OreStack(451, 2),  // Runite
            new OreStack(449, 10), // Adamant
            new OreStack(447, 20), // Mithril
            new OreStack(440, 50), // Iron
            new OreStack(453, 100) // Coal
    };
    public static OreTile getRandomVein() {
        List<OreTile> availableVeins = new ArrayList<>();
        
        for (OreTile tile : allTiles) {
            // Only include tiles that aren't empty or blocked
            if (tile.occupied && tile.objectId != tile.getDepletedId(tile.objectId)) { // 26656 = "empty"
                availableVeins.add(tile);
            }
        }

        if (availableVeins.isEmpty()) {
            return null; // No available veins
        }

        // Pick a random tile
        return availableVeins.get(Misc.random(availableVeins.size() - 1));
    }

    public static NPC spawnNpc(int x, int y) {
        int npcId = 6566;
        int startX = x;
        int startY = y;
        int height = 0;
        NPC payDirt = World.getWorld().npcHandler.spawnNpc2(npcId, startX, startY, height, 1, 0, 0, 0, 0);
        OreTile targetVein = MotherlodeMine.getRandomVein();
        if (targetVein != null) {
            List<Location> path = NPCDumbPathFinder.findPath(payDirt, targetVein.getX(), targetVein.getY());
            payDirt.npcPath = path;
            payDirt.npcForceWalkIndex = 0;
            payDirt.setWalkQueue(payDirt.npcPath);
        }
        return payDirt;
    }
    public static OreTile getVeinAt(int x, int y) {
        for (OreTile tile : allTiles) {
            if (tile.getX() == x && tile.getY() == y && tile.occupied) {
                return tile;
            }
        }
        return null;
    }
    public static boolean waterWheelsActive() {
        return leftWheelActive || rightWheelActive;
    }

    // Random double 0.0 - 1.0
    public static double randomDouble() {
        return Math.random();
    }
 // Example boundary for Motherlode Mine (update coords as needed)
    public static boolean isInMotherlodeMine(Player p) {
        return p.getX() >= 3710 && p.getX() <= 3770 && p.getY() >= 5630 && p.getY() <= 5690;
    }

    public static void refreshObjects(Player c) {
        for (OreTile tile : allTiles) {
                //new Object(tile.objectId, tile.x, tile.y, 0, tile.face, 0, -1, 0);
        	if(c.distanceToPoint(tile.x, tile.y) <= 60)
                new WorldObject(tile.objectId, tile.x, tile.y, 0, 0, tile.face);
         }
}
    public static OreStack rollPayDirtOre(Player player) {
        int miningLevel = player.getSkills().getLevel(Skill.MINING);
        double roll = randomDouble() * 100;

        // FIX: Read from the static array instead of creating a new one every pickaxe swing
        for (OreStack ore : PAY_DIRT_REWARDS) {
            if ((ore.id == 451 && miningLevel >= 85 && roll < ore.chancePercent) ||
                    (ore.id == 449 && miningLevel >= 70 && roll < ore.chancePercent) ||
                    (ore.id == 447 && miningLevel >= 55 && roll < ore.chancePercent) ||
                    (ore.id == 440 && miningLevel >= 30 && roll < ore.chancePercent) ||
                    (ore.id == 453)) {
                return new OreStack(ore.id, 1);
            }
        }
        return null;
    }

    public static void addToPlayerSack(int ownerIndex, NPC npc) {
        // FIX: Safely retrieve the player using their index
        if (ownerIndex < 0 || ownerIndex >= PlayerHandler.players.length) return;
        Player player = PlayerHandler.players[ownerIndex];

        // If the player logged out or disconnected while the dirt was traveling, abort safely
        if (player == null || !player.isActive || player.disconnected) {
            npc.payDirtPending.clear();
            return;
        }

        int maxSack = 100;
        int spaceLeft = maxSack - player.payDirtSackAmt;
        int toAdd = Math.min(spaceLeft, npc.payDirtPending.size());
        if (toAdd <= 0) return;

        for (int i = 0; i < toAdd; i++) {
            player.getPayDirtSack().add(npc.payDirtPending.get(i));
        }

        player.payDirtSackAmt += toAdd;
        player.sendMessage("Your pay-dirt sack now contains " + player.payDirtSackAmt + " pay-dirt.");
        player.getPA().sendConfig(375, 1<<15);
        npc.payDirtPending.clear();
    }
    public static void updateAllVeinVisuals() {
        for (OreTile tile : allTiles) {

            // Skip pure empty walls
            if (!tile.occupied && tile.objectId == 26656) {
                continue;
            }

            boolean hasLeft = false;
            boolean hasRight = false;

            int leftX = tile.x, leftY = tile.y;
            int rightX = tile.x, rightY = tile.y;

            // Proper orientation for 317 RSPS Wall Faces
            switch (tile.face) {
                case 0: // East Wall
                    leftY++; rightY--; break;

                case 1: // North Wall (FLIPPED to account for client model rotation)
                    leftX++; rightX--; break;

                case 2: // West Wall
                    leftY--; rightY++; break;

                case 3: // South Wall (FLIPPED to account for client model rotation)
                    leftX--; rightX++; break;
            }

            // Neighbors count as connected if they exist, aren't base walls, and share the face!
            OreTile leftTile = getTileAt(leftX, leftY);
            if (leftTile != null && leftTile.objectId != 26656 && leftTile.face == tile.face) {
                hasLeft = true;
            }

            OreTile rightTile = getTileAt(rightX, rightY);
            if (rightTile != null && rightTile.objectId != 26656 && rightTile.face == tile.face) {
                hasRight = true;
            }

            // Determine correct Base ID based on neighbors
            int newId;
            if (hasLeft && hasRight) newId = 26663; // Middle
            else if (hasRight && !hasLeft) newId = 26662; // Left Edge
            else if (hasLeft && !hasRight) newId = 26664; // Right Edge
            else newId = 26661; // Single

            // If this tile is currently depleted, shift it to the depleted version of that shape
            if (!tile.occupied || tile.respawnTimer > 0) {
                newId = tile.getDepletedId(newId);
            }

            // Instantly visually update the rock!
            if (tile.objectId != newId) {
                tile.objectId = newId;
                tile.updateObject();
            }
        }
    }
    public static void fixStrut(Player c, int objectX, int objectY) {
    	double chance = 0.12 + (c.getSkills().getLevel(Skill.SMITHING) - 1) * 0.153;
    	if(!c.getItems().playerHasItem(2347))
    		return;
		c.startAnimation(3676);
		c.sendMessage("You successfully fix the strut!");
	    if (Math.random() < chance) { // success
			c.getSkills().addExperience((int)(c.getSkills().getActualLevel(Skill.SMITHING)*1.5), Skill.SMITHING);
			if (objectX == 3742 && objectY == 5669) {
                World.getWorld().getGlobalObjects().add(new GlobalObject(26671, 3743, 5668, 0, 0, 10, -1));
                World.getWorld().getGlobalObjects().add(new GlobalObject(26669, objectX, objectY, 0, 0, 10, -1));
                MotherlodeMine.leftWheelActive = true;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
                        World.getWorld().getGlobalObjects().add(new GlobalObject(26670, objectX, objectY, 0, 0, 10, -1));
                        World.getWorld().getGlobalObjects().add(new GlobalObject(26672, 3743, 5668, 0, 0, 10, -1));
                        MotherlodeMine.leftWheelActive = false;
					}
				}, Misc.random(15, 25));
			} else if (objectX == 3742 && objectY == 5663) {
					new Object(26671, 3743, 5662, 0, 0, 10, -1, 0);
					new Object(26669, objectX, objectY, 0, 0, 10, -1, 0);
					MotherlodeMine.rightWheelActive = true;
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}

						@Override
						public void stop() {
							new Object(26670, objectX, objectY, 0, 0, 10, -1, 0);
							new Object(26672, 3743, 5662, 0, 0, 10, -1, 0);
							MotherlodeMine.rightWheelActive = false;
						}
					}, Misc.random(15, 25));
			}
		} else {

		    c.sendMessage("You fail to fix the strut.");
		}
    }
    public static void addToHopper(Player player) {
        int hopperAmount = player.payDirtHopperAmt;
        int inInventory = player.getItems().getItemAmount(12011);
        int spaceLeft = 108 - hopperAmount;
        int toAdd = Math.min(spaceLeft, inInventory);
        if (toAdd <= 0) {
            player.sendMessage("The hopper is full or you have no pay-dirt.");
            return;
        }

        NPC npc = MotherlodeMine.spawnPayDirtNpc(player);

        List<OreStack> toMove = new ArrayList<>();
        for (int i = 0; i < toAdd && i < player.payDirtPending.size(); i++) {
            toMove.add(player.payDirtPending.get(i));
        }
        player.addNpcWithPayDirt(npc);

        npc.payDirtPending.addAll(toMove);
        player.payDirtPending.removeAll(toMove);

        player.getItems().deleteItem(12011, toAdd);
        player.payDirtHopperAmt += toAdd;
        player.sendMessage("You add " + toAdd + " pay-dirt to the hopper.");
    }


    public static void processPayDirtSack(Player player) {
        if (player.payDirtSackAmt <= 0 || player.getPayDirtSack().isEmpty()) return;

        int goldenNuggets = 0;

        for (OreStack ore : player.getPayDirtSack()) {
            if (ore.id == 12012) {
                int addNuggets = Math.min(ore.amount, 3 - goldenNuggets);
                if (addNuggets > 0) {
                    player.getItems().addItem(12012, addNuggets);
                    goldenNuggets += addNuggets;
                }
            } else {
                player.getItems().addItem(ore.id, ore.amount);
                player.getSkills().addExperience(ore.xp * ore.amount, Skill.MINING);
            }
        }

        player.getPayDirtSack().clear();
        player.payDirtSackAmt = 0;
        player.sendMessage("You process your pay-dirt and receive your ores and experience!");
        player.getPA().sendConfig(375, 0 << 15);
    }


    public static void spawnVeins() {
        allTiles.clear();
        veins.clear();

        initAllTiles();
        // Define vein groups (templates for max size)
        VeinGroup horizontalTriple = new VeinGroup(new int[]{26662, 26663, 26664}, new int[][]{{0,0},{1,0},{2,0}});
        VeinGroup verticalTriple   = new VeinGroup(new int[]{26662, 26663, 26664}, new int[][]{{0,0},{0,1},{0,2}});

        veins.add(horizontalTriple);
        veins.add(verticalTriple);

        // Reset all tiles
        for (OreTile t : allTiles) {
        	//ObjectDef def = ObjectDef.forID(t.objectId);
        	//Region.getObjectIdAt(def.type, t.x, t.y);
            t.occupied = false;
            t.objectId = t.getDepletedId(t.objectId); // depleted
        }

        int activeCount = 0;

        // Shuffle all tiles for random starting positions
        List<OreTile> tiles = new ArrayList<>(allTiles);
        Collections.shuffle(tiles);

        for (OreTile tile : tiles) {
            if (tile.occupied || activeCount >= MAX_ACTIVE_TILES) continue;

            VeinGroup vein = pickRandomVein(tile);

            if (vein != null && canPlaceVeinAt(tile, vein)) {
                if(tile.objectId != 26661 || vein.ids[0] != 26661)
                    placeVein(tile, vein);
                activeCount += vein.ids.length;
            } else {
                placeVeinPiece(tile, 26661);
                activeCount++;
            }
        }

        // --- ADD THIS HERE ---
        // Overwrite any awkward spawns with perfectly aligned shapes!
        updateAllVeinVisuals();

        init();
    }
    public static void init() {

    	spawnNpc(3761, 5678);
    	spawnNpc(3719, 5657);
    	spawnNpc(3720, 5661);
    	spawnNpc(3718, 5647);
    	spawnNpc(3734, 5640);
    }
    private static void initAllTiles() {
    	/*
    	 * Single upper level veins
    	 */
        allTiles.add(new OreTile(3758, 5675, 0));
        allTiles.add(new OreTile(3753, 5680, 0));//upper level western hall right side vein 
        allTiles.add(new OreTile(3754, 5678, 0));
        allTiles.add(new OreTile(3755, 5677, 3));
        /*
         * Upper level eastern veins
         */
        allTiles.add(new OreTile(3762, 5673, 0));
        allTiles.add(new OreTile(3762, 5672, 0));
        allTiles.add(new OreTile(3762, 5671, 0));
        allTiles.add(new OreTile(3762, 5670, 0));
        /*
         * Upper level north east veins
         */
        allTiles.add(new OreTile(3754, 5682, 0));
        allTiles.add(new OreTile(3754, 5683, 0));
        allTiles.add(new OreTile(3761, 5675, 1));
        allTiles.add(new OreTile(3762, 5675, 1));
        /*
         * Upper level western hall veins
         */
        allTiles.add(new OreTile(3751, 5680, 2));
        allTiles.add(new OreTile(3751, 5681, 2));
        /*
         * Upper level northern west veins 
         */
        allTiles.add(new OreTile(3756, 5678, 2));
        allTiles.add(new OreTile(3756, 5679, 2));
        /*
         * Upper level northern west veins
         */
        allTiles.add(new OreTile(3755, 5681, 2));
        allTiles.add(new OreTile(3755, 5682, 2));
        allTiles.add(new OreTile(3755, 5683, 2));
        /*
         * Upper level south north wall veins
         */
        allTiles.add(new OreTile(3759, 5674, 3));
        allTiles.add(new OreTile(3760, 5674, 3));
        allTiles.add(new OreTile(3761, 5674, 3));
        
        allTiles.add(new OreTile(3760, 5680, 3));
        allTiles.add(new OreTile(3761, 5680, 3));
        allTiles.add(new OreTile(3762, 5680, 3));
        allTiles.add(new OreTile(3756, 5684, 3));
        
        allTiles.add(new OreTile(3757, 5684, 3));
        /*
         * Upper level northern veins
         */
        allTiles.add(new OreTile(3752, 5684, 3));
        allTiles.add(new OreTile(3753, 5684, 3));
        
        allTiles.add(new OreTile(3764, 5677, 1));
        allTiles.add(new OreTile(3765, 5677, 1));
        allTiles.add(new OreTile(3761, 5681, 1));
        allTiles.add(new OreTile(3763, 5681, 2));
        allTiles.add(new OreTile(3763, 5682, 2));
        allTiles.add(new OreTile(3748, 5682, 1));
        allTiles.add(new OreTile(3749, 5682, 1));
        allTiles.add(new OreTile(3750, 5682, 1));
        allTiles.add(new OreTile(3758, 5685, 2));
        allTiles.add(new OreTile(3762, 5682, 0));
        allTiles.add(new OreTile(3762, 5683, 0));
        allTiles.add(new OreTile(3759, 5682, 1));
        
        /*
         * Lower Level start
         */
        /*
         * Behind water wheels
         */
        allTiles.add(new OreTile(3733, 5667, 0));
        allTiles.add(new OreTile(3732, 5665, 0));
        allTiles.add(new OreTile(3733, 5663, 0));

        allTiles.add(new OreTile(3718, 5653, 0));
        allTiles.add(new OreTile(3718, 5652, 0));
        allTiles.add(new OreTile(3718, 5651, 0));

        allTiles.add(new OreTile(3715, 5645, 2));
        allTiles.add(new OreTile(3715, 5646, 2));
        allTiles.add(new OreTile(3715, 5647, 2));
        
        allTiles.add(new OreTile(3727, 5638, 1));

        allTiles.add(new OreTile(3713, 5638, 2));
        allTiles.add(new OreTile(3713, 5639, 2));
        allTiles.add(new OreTile(3713, 5640, 2));
        
        allTiles.add(new OreTile(3723, 5659, 0));
        allTiles.add(new OreTile(3727, 5661, 0));
        
        allTiles.add(new OreTile(3722, 5664, 0));
        allTiles.add(new OreTile(3722, 5663, 0));
        allTiles.add(new OreTile(3722, 5662, 0));

        allTiles.add(new OreTile(3719, 5654, 1));
        allTiles.add(new OreTile(3720, 5654, 1));
        
        allTiles.add(new OreTile(3721, 5652, 2));
        allTiles.add(new OreTile(3721, 5653, 2));
        
        allTiles.add(new OreTile(3721, 5642, 2));
        allTiles.add(new OreTile(3721, 5641, 2));
        allTiles.add(new OreTile(3721, 5640, 2));
    }

    public static OreTile getAdjacentVein(int x, int y) {
        // FIX: Use the cached cardinal offsets instead of generating a new matrix
        for (int[] off : CARDINAL_OFFSETS) {
            int checkX = x + off[0];
            int checkY = y + off[1];

            OreTile vein = MotherlodeMine.getVeinAt(checkX, checkY);
            if (vein != null && vein.occupied && vein.objectId != vein.getDepletedId(vein.objectId)) {
                return vein;
            }
        }
        return null;
    }
    public static void handleMiningNPC(NPC npc) {
        if (npc.npcType != 6566 && npc.npcType != 6567) return;

        // Mining
        // Mining
        if (npc.mining) {
            // --- THE FIX: Live Depletion Check ---
            // Every single tick they are mining, check if the rock is STILL active.
            // If a player mined it, or it vanished, abort immediately!
            if (npc.targetVein == null || !npc.targetVein.occupied || npc.targetVein.objectId == npc.targetVein.getDepletedId(npc.targetVein.objectId)) {
                npc.mining = false;
                npc.targetVein = null;
                npc.animNumber = -1;
                npc.animUpdateRequired = true;
                npc.updateRequired = true;
                npc.miningAnimStarted = false;
                npc.npcPath = null; // Force them to find a new rock next tick!
                return;
            }
            // -------------------------------------

            if (npc.targetVein != null) {
                npc.turnNpc(npc.targetVein.getX(), npc.targetVein.getY());
            }

            if (npc.miningAnimCooldown <= 0) {
                npc.animNumber = (npc.npcType == 6566 ? 7139 : 4021);
                npc.animUpdateRequired = true;
                npc.updateRequired = true;
                npc.miningAnimCooldown = 11;
            } else {
                npc.miningAnimCooldown--;
            }

            npc.miningTimer--;
            if (npc.miningTimer <= 0) {
                npc.mining = false;
                npc.targetVein = null;
                npc.animNumber = -1;
                npc.animUpdateRequired = true;
                npc.updateRequired = true;
                npc.miningAnimStarted = false;
                npc.npcPath = null; // Force them to find a new rock next tick!
            }
            return;
        }

        // Pathfinding
        if (npc.npcPath == null || npc.npcPath.isEmpty() || npc.npcForceWalkIndex >= npc.npcPath.size()) {
            Pair<OreTile, List<Location>> pick = pickClosestReachableVeinAndPath(npc);
            if (pick != null) {
                npc.npcPath = pick.getRight();
                npc.npcForceWalkIndex = 0;
                npc.stuckTicks = 0;
            } else {
                npc.npcPath = Collections.emptyList();
                return;
            }
        }

        // Walk along path
        while (npc.npcForceWalkIndex < npc.npcPath.size()) {
            Location step = npc.npcPath.get(npc.npcForceWalkIndex);
            int dx = step.getX() - npc.getX();
            int dy = step.getY() - npc.getY();

            if (dx == 0 && dy == 0) {
                npc.npcForceWalkIndex++;
                continue;
            }

            int moveX = Integer.signum(dx);
            int moveY = Integer.signum(dy);
            Location nextStep = new Location(npc.getX() + moveX, npc.getY() + moveY, npc.getHeight());

            if (Region.canMove(new Location(npc.getX(), npc.getY(), npc.getHeight()), nextStep, npc.getSize(), npc.getSize())) {
                npc.moveX = moveX;
                npc.moveY = moveY;
                npc.getNextNPCMovement();
                npc.updateRequired = true;
                npc.stuckTicks = 0;
                break; // move one tile per tick
            } else {
                npc.stuckTicks++;
                if (npc.stuckTicks >= 2) { // try repath only after 2 ticks
                    Pair<OreTile, List<Location>> pick = pickClosestReachableVeinAndPath(npc);
                    if (pick != null) {
                        npc.npcPath = pick.getRight();
                        npc.npcForceWalkIndex = 0;
                    } else {
                        npc.npcPath = Collections.emptyList();
                    }
                    npc.stuckTicks = 0;
                }
                break;
            }
        }

        // Mine if adjacent
        OreTile vein = MotherlodeMine.getAdjacentVein(npc.getX(), npc.getY());
        if (vein != null && vein.occupied && vein.objectId != vein.getDepletedId(vein.objectId)) {
            npc.mining = true;
            npc.miningTimer = 50;
            npc.targetVein = vein;
        }
    }

 // Pick the closest vein that actually yields a non-empty BFS path to a usable adjacent tile.
    private static Pair<OreTile, List<Location>> pickClosestReachableVeinAndPath(NPC npc) {
        OreTile bestVein = null;
        List<Location> bestPath = null;
        double bestDist = Double.MAX_VALUE;

        for (OreTile vein : MotherlodeMine.allTiles) {
            if (!vein.occupied || vein.objectId == vein.getDepletedId(vein.objectId)) continue;

            // Try all four cardinal adjacent tiles
            int[] dx = {0, 0, -1, 1};
            int[] dy = {-1, 1, 0, 0};
            for (int i = 0; i < 4; i++) {
                int adjX = vein.getX() + dx[i];
                int adjY = vein.getY() + dy[i];
                Location adj = new Location(adjX, adjY, npc.getHeight());

                if (!Region.canMove(new Location(npc.getX(), npc.getY(), npc.getHeight()), adj, npc.getSize(), npc.getSize()))
                    continue;

                List<Location> path = NPCDumbPathFinder.findPath(npc, adjX, adjY);
                if (path != null && !path.isEmpty()) {
                    double dist = Math.hypot(npc.getX() - vein.getX(), npc.getY() - vein.getY());
                    if (dist < bestDist) {
                        bestDist = dist;
                        bestVein = vein;
                        bestPath = path;
                    }
                    break; // stop checking other adj tiles
                }
            }
        }

        if (bestVein == null || bestPath == null) return null;
        return Pair.of(bestVein, bestPath);
    }


    // Prefer the vein's face; if blocked/unreachable, try the other 3 sides.
    public static Location getBestAdjacentForVein(NPC npc, OreTile vein) {
        int vx = vein.getX(), vy = vein.getY(), vz = npc.getHeight();

        int[][] dirs = { {0,-1}, {0,1}, {-1,0}, {1,0} }; // S, N, W, E
        int start = (vein.face >= 0 && vein.face <= 3) ? vein.face : 0;

        for (int k = 0; k < 4; k++) {
            int idx = (start + k) % 4;
            int nx = vx + dirs[idx][0];
            int ny = vy + dirs[idx][1];
            Location adj = new Location(nx, ny, vz);

            // quick sanity: try a tiny path probe from adj to adj (cheap) or just rely on BFS later
            // We rely on BFS from NPC to adj; don't pre-filter with Region.canMove(npc->adj)
            // because that rejects valid detours. Just return this candidate.
            return adj; // we return at face immediately; caller will try path and, if empty, rotate further
        }
        return null;
    }

    // Keep original "faced" helper if you still need it somewhere else
    public static Location getAdjacentTileForVein(OreTile vein, NPC npc) {
        int vx = vein.getX(), vy = vein.getY(), vz = npc.getHeight();
        switch (vein.face) {
            case 0: return new Location(vx, vy - 1, vz); // south
            case 1: return new Location(vx, vy + 1, vz); // north
            case 2: return new Location(vx - 1, vy, vz); // west
            case 3: return new Location(vx + 1, vy, vz); // east
            default: return new Location(vx, vy - 1, vz);
        }
    }

    private static void placeVeinPiece(OreTile tile, int objectId) {
        if (tile == null || tile.occupied) return;

        tile.occupied = true;
        tile.objectId = objectId;

        // Let the OreTile handle the GlobalObject queue!
        tile.updateObject();
    }

    public static OreTile getTileAt(int x, int y) {
        for (OreTile t : allTiles) {
            if (t.x == x && t.y == y) {
                return t;
            }
        }
        return null;
    }

    public static void moveVeinToNewSpot(OreTile tile) {
        // Remember what vein group this tile was part of
        VeinGroup group = tile.veinGroup;

        // Clear old vein
        if (group != null) {
            for (int i = 0; i < group.ids.length; i++) {
                int dx = group.offsets[i][0];
                int dy = group.offsets[i][1];

                int tx = tile.x;
                int ty = tile.y;

                switch (tile.face) {
                    case 0: tx += dx; ty += dy; break; // east
                    case 1: tx += dx; ty += dy; break; // north
                    case 2: tx -= dx; ty += dy; break; // west
                    case 3: tx += dx; ty -= dy; break; // south
                }

                OreTile t = getTileAt(tx, ty);
                if (t != null) {
                    t.occupied = false;
                    t.objectId = t.getDepletedId(t.objectId); // empty
                    t.veinGroup = null;
                    t.updateObject();
                }
            }
        } else {
            // If single tile, just clear it
            tile.occupied = false;
            tile.objectId = tile.getDepletedId(tile.objectId);
            tile.updateObject();
        }

        // Find free tiles
        List<OreTile> freeTiles = new ArrayList<>();
        for (OreTile t : allTiles) {
            if (!t.occupied && t.objectId == t.getDepletedId(t.objectId)) {
                freeTiles.add(t);
            }
        }
        if (freeTiles.isEmpty()) return;

        // Pick a new random starting tile
        OreTile newTile = freeTiles.get(Misc.random(freeTiles.size() - 1));

//System.out.println("Moving vein to newTile x=" + newTile.x + " y=" + newTile.y);
        // Place the same vein group at the new location
        if (group != null && canPlaceVeinAt(newTile, group)) {
            placeVein(newTile, group);
        } else {
            // fallback single-tile vein
            placeVeinPiece(newTile, 26661);
        }
        updateAllVeinVisuals();
    }



private static VeinGroup pickRandomVein(OreTile startTile) {
    List<VeinGroup> candidates = new ArrayList<>();
    for (VeinGroup vein : veins) {
        if (canPlaceVeinAt(startTile, vein)) {
            long occupiedCount = allTiles.stream().filter(t -> t.occupied).count();
            if (occupiedCount + vein.ids.length <= MAX_ACTIVE_TILES) {
                candidates.add(vein);
            }
        }
    }
    if (candidates.isEmpty()) return null;
    return candidates.get(Misc.random(candidates.size() - 1));
}


private static boolean canPlaceVeinAt(OreTile startTile, VeinGroup vein) {
    for (int i = 0; i < vein.offsets.length; i++) {
        int nx = startTile.x + vein.offsets[i][0];
        int ny = startTile.y + vein.offsets[i][1];

        switch (startTile.face) {
            case 0: nx = startTile.x + vein.offsets[i][0]; ny = startTile.y + vein.offsets[i][1]; break; // east
            case 1: nx = startTile.x + vein.offsets[i][0]; ny = startTile.y + vein.offsets[i][1]; break; // north
            case 2: nx = startTile.x - vein.offsets[i][0]; ny = startTile.y + vein.offsets[i][1]; break; // west
            case 3: nx = startTile.x + vein.offsets[i][0]; ny = startTile.y - vein.offsets[i][1]; break; // south
        }

        OreTile t = getTileAt(nx, ny);
        if (t == null || t.occupied) return false;
    }
    return true;
}


    private static final int MAX_ACTIVE_TILES = 50; // max number of tiles that can be occupied at once

    private static void placeVein(OreTile startTile, VeinGroup vein) {
        long occupiedCount = allTiles.stream().filter(t -> t.occupied).count();
        if (occupiedCount + vein.ids.length > MAX_ACTIVE_TILES) return;

        for (int i = 0; i < vein.ids.length; i++) {
            int nx = startTile.x;
            int ny = startTile.y;

            switch (startTile.face) {
                case 0: nx += vein.offsets[i][0]; ny += vein.offsets[i][1]; break; // east
                case 1: nx += vein.offsets[i][0]; ny += vein.offsets[i][1]; break; // north
                case 2: nx -= vein.offsets[i][0]; ny += vein.offsets[i][1]; break; // west
                case 3: nx += vein.offsets[i][0]; ny -= vein.offsets[i][1]; break; // south
            }

            OreTile t = getTileAt(nx, ny);
            if (t == null) continue;
            t.occupied = true;
            t.objectId = vein.ids[i];
            t.veinGroup = vein;

            // Let the OreTile handle the GlobalObject queue!
            t.updateObject();
        }
    }


}
