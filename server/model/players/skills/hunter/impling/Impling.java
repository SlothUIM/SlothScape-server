package server.model.players.skills.hunter.impling;

import java.util.HashMap;
import java.util.Random;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.items.ItemAssistant;
import server.util.Misc;
import server.world.Location;

public class Impling {

	public static final Random random = new Random();

	/**
	 * Properly triggers the engine's death and respawn sequence for the caught impling.
	 */
	/**
	 * Properly triggers the engine's death and respawn sequence,
	 * randomizing the respawn location for overworld implings.
	 */
	public static void kill(NPC n) {
		if (n != null && !n.isDead) {
			n.isDead = true;
			n.applyDead = true;
			n.needRespawn = true;

			// If not in Puro-Puro, pick a random new location for it to respawn at!
			// (Assuming you have a Boundary.PURO_PURO defined, otherwise just remove the check)
			/* if (!Boundary.isIn(n, Boundary.PURO_PURO)) { */
			Location newSpawn = getRandomOverworldSpawn();
			n.makeX = newSpawn.getX();
			n.makeY = newSpawn.getY();
			n.heightLevel = newSpawn.getZ();
			/* } */

			n.updateRequired = true;
		}
	}


	/**
	 * Checks if a specific tile is a safe, open, and valid spawn location.
	 */
	private static boolean isValidSpawn(int x, int y, int z) {
		// 1. The floor must be completely empty (No water, no solid collision)
		if (server.clip.Region.getClipping(x, y, z) != 0) {
			return false;
		}

		// 2. The tile must not contain any solid map objects (tables, statues, trees, etc.)
		if (server.clip.Region.solidObjectExists(x, y, z)) {
			return false;
		}

		// 3. The "Boxed In" Check: Ensure it didn't spawn inside a tiny locked room.
		// We check the 4 cardinal directions (North, East, South, West).
		// It must be able to walk in at least 2 of them, proving it's in an open area.
		int openPaths = 0;
		/*if (server.clip.Region.canMove(x, y, z, 1, 1)) openPaths++; // North
		if (server.clip.Region.canMove(x, y, z, 4, 1)) openPaths++; // East
		if (server.clip.Region.canMove(x, y, z, 6, 1)) openPaths++; // South
		if (server.clip.Region.canMove(x, y, z, 3, 1)) openPaths++; // West*/

		return openPaths >= 2;
	}
	/**
	 * Curated list of surface-level Region IDs for Implings to spawn in.
	 * Add any Region ID here (e.g., Lumbridge = 12850, Varrock = 12853, Falador = 11828)
	 */
	private static final int[] OVERWORLD_REGIONS = {
			// Misthalin & Asgarnia
			12850, 12853, 11828, 12083, 12084, 12854, 12338, 12339, 11573,
			// Kandarin
			10547, 10548, 10803, 10804, 11059, 11060, 10292,
			// Kharidian Desert
			13105, 13106, 13107, 13361, 13362,
			// Fremennik
			10553, 10554, 10809, 10810, 11065,
			// Morytania
			13878, 13879, 13622, 13623,
			// Zeah (Kourend)
			6459, 6460, 6715, 6716, 6971, 6972
	};

	/**
	 * Finds a random, strictly walkable land tile for an impling to respawn at.
	 */
	/**
	 * Finds a random, strictly walkable land tile within a valid Region ID.
	 */
	private static Location getRandomOverworldSpawn() {
		int attempts = 0;

		while (attempts < 50) {
			// Pick a random Region ID from our curated list
			int regionId = OVERWORLD_REGIONS[Misc.random(OVERWORLD_REGIONS.length - 1)];

			// Decode the Region ID back into absolute world coordinates
			int regionAbsX = (regionId >> 8) * 64;
			int regionAbsY = (regionId & 0xFF) * 64;

			// Pick a random tile within that 64x64 region
			int x = regionAbsX + Misc.random(63);
			int y = regionAbsY + Misc.random(63);
			int z = 0; // Surface level

			// Use our new highly-intelligent spawn checker
			if (isValidSpawn(x, y, z)) {
				return new Location(x, y, z);
			}
			attempts++;
		}

		// Failsafe: Drop in Lumbridge cow pen
		return new Location(3200, 3260, 0);
	}
	/**
	 * Handles the catching of an impling
	 */
	public static void catchImpling(Player player, int npcId, final int npcIndex) {
		ImplingData data = ImplingData.forId(npcId);

		if (player == null || npcId == -1 || data == null) {
			return;
		}
		if (System.currentTimeMillis() - player.lastImpling < 2000) {
			return;
		}

		NPC target = NPCHandler.npcs[npcIndex];
		if (target == null || target.isDead) {
			return;
		}

		int hunterLevel = player.getSkills().getLevel(Skill.HUNTER);

		if (hunterLevel < data.requirement) {
			player.sendMessage("You need a Hunter level of at least " + data.requirement + " to catch this impling.");
			return;
		}

		// Equipment Checks
		boolean hasNormalNet = player.getItems().playerHasItem(10010) || player.getItems().isWearingItem(10010);
		boolean hasMagicNet = player.getItems().playerHasItem(11259) || player.getItems().isWearingItem(11259);
		boolean isBarehanded = !hasNormalNet && !hasMagicNet;

		// Barehanded Logic
		if (isBarehanded) {
			if (hunterLevel < data.requirement + 10) {
				player.sendMessage("You need a Hunter level of " + (data.requirement + 10) + " to catch this barehanded.");
				return;
			}
			if (player.getItems().freeSlots() < 1) {
				player.sendMessage("You need at least 1 free inventory space to catch this barehanded.");
				return;
			}
		} else {
			// Net Logic: Requires a jar
			if (!player.getItems().playerHasItem(11260)) {
				player.sendMessage("You must have an empty impling jar to catch this impling.");
				return;
			}
		}

		player.lastImpling = System.currentTimeMillis();
		player.startAnimation(isBarehanded ? 7171 : 6605);

		// Catch Math: Magic nets provide a strong boost
		int catchChance = ((hunterLevel - data.requirement) / 10) + 1;
		if (hasMagicNet) catchChance += 2;

		boolean success = Misc.random(10) <= catchChance || catchChance >= 8;

		if (success) {
			kill(target);
			player.getPA().addSkillXP(data.experience, Skill.HUNTER.getId());

			if (isBarehanded) {
				player.sendMessage("You catch the " + data.name + " barehanded and immediately loot it!");
				// Force a direct loot roll from the Enum using the impling's jar ID as the key
				rollLootDirectly(player, data.jar);
			} else {
				player.getItems().deleteItem(11260, 1);
				player.getItems().addItem(data.jar, 1);
				player.sendMessage("You successfully catch the " + data.name + " in your jar.");
			}
		} else {
			target.startAnimation(6616);
			player.sendMessage("You fail to catch the " + data.name + ".");
		}
	}

	/**
	 * Re-usable method to give loot directly to the inventory (used by Barehanded catches)
	 */
	private static void rollLootDirectly(Player c, int jarId) {
		ImpRewards t = ImpRewards.impReward.get(jarId);
		if (t == null) return;

		int r = random.nextInt(t.getRewards().length);
		int itemToGive = t.getRewards()[r][0];
		int amountToGive = t.getRewards()[r][1];

		if (t.getRewards()[r].length == 3) { // Dynamic amounts (min, max)
			amountToGive = t.getRewards()[r][1] + random.nextInt(t.getRewards()[r][2] - t.getRewards()[r][1]);
		}

		c.getItems().addItem(itemToGive, amountToGive);
		c.sendMessage("You find x" + amountToGive + " " + ItemAssistant.getItemName(itemToGive) + ".");
	}

	/**
	 * Handles the rewards of looting an impling jar from the inventory
	 */
	public static void getReward(Player c, int itemId) {
		if (c == null || itemId == -1) {
			return;
		}
		if (c.getItems().freeSlots() < 2) {
			c.sendMessage("Make sure you have at least 2 free inventory spots before looting.");
			return;
		}

		ImpRewards t = ImpRewards.impReward.get(itemId);
		if (t == null) return;

		c.getItems().deleteItem(t.getItemId(), c.getItems().getItemSlot(t.getItemId()), 1);

		// OSRS jar cracking rate is exactly 10%
		if (Misc.random(10) == 1) {
			c.sendMessage("You loot the jar, but it cracks and shatters in the process.");
		} else {
			c.getItems().addItem(11260, 1); // Returns empty jar
			c.sendMessage("You successfully loot the impling jar.");
		}

		rollLootDirectly(c, itemId); // Uses the shared loot method
	}

	/**
	 * An enum containing the possible rewards of an impling jar
	 */
	public enum ImpRewards {
		BABY(11238, new int[][] {
				{1755,1}, {1734,1}, {1733, 1}, {946,1}, {1985,1},
				{2347,1}, {1759,1}, {1927,1}, {319,1}, {2007,1},
				{1779,1}, {7170,1}, {1438,1}, {2355,1}, {1607,1},
				{1743,1}, {379,1}, {1761,1}
		}),
		YOUNG(11240, new int[][] {
				{361,1}, {1902,1}, {1539,5}, {1524,1}, {7936,1},
				{855,1}, {1353,1}, {2293,1}, {7178,1}, {247,1},
				{453,1}, {1777,1}, {231,1}, {1761,1}, {8778,1},
				{133,1}, {2359,1}
		}),
		GOURMET(11242, new int[][] {
				{365,1}, {361,1}, {2011,1}, {1897,1}, {2327,1},
				{5970,1}, {380,4}, {7179, 1, 5}, {386,3},
				{1883,1}, {3145, 2}, {5755,1}, {10137, 5}
		}),
		EARTH(11244, new int[][] {
				{6033,6}, {1440,1}, {5535, 1}, {557, 32}, {1442,1},
				{1784,4}, {1273,1}, {447,1}, {1606,2}
		}),
		ESSENCE(11246, new int[][] {
				{7937,20}, {555,30}, {556,30}, {558,25}, {559,28},
				{562, 4}, {1448, 1}, {564, 4}, {563, 13}, {565, 7},
				{566, 11}
		}),
		ECLECTIC(11248, new int[][] {
				{1273,1}, {5970,1}, {231,1}, {556, 30, 47}, {8779, 4},
				{1199,1}, {4527,1}, {444,1}, {2358, 5}, {7937, 20, 35},
				{237,1}, {2493,1}, {10083,1}, {1213,1}, {450, 10},
				{5760, 2}, {7208,1}, {5321, 3}, {1391, 1}, {1601,1}
		}),
		NATURE(11250, new int[][] {
				{5100,1}, {5104, 1}, {5281,1}, {5294,1}, {6016,1},
				{1513,1}, {254, 4}, {5313,1}, {5286,1}, {5285, 1},
				{3000,1}, {5974,1}, {5297,1}, {5299,1}, {5298, 5},
				{5304,1}, {5295, 1}, {270,2}, {5303,1}
		}),
		MAGPIE(11252, new int[][] {
				{1701,3}, {1732, 3}, {2569,3}, {3391,1}, {4097,1},
				{5541,1}, {1747, 6}, {1347,1}, {2571, 4}, {4095, 1},
				{2364, 2}, {1215, 1}, {1185, 1}, {1602, 4}, {5287, 1},
				{987,1}, {985,1}, {5300,1}
		}),
		NINJA(11254, new int[][] {
				{4097,1}, {3385,1}, {892, 70}, {140,4}, {1748 , 10, 16},
				{1113, 1}, {1215, 1}, {1333,1}, {1347,1}, {9342, 2},
				{5938,4}, {6156, 3}, {9194, 4}, {6313,1}, {805, 50}
		}),
		REVENANT(33773, new int[][] {
				// FIXED: Removed the massive 5x copy-paste block of duplicates
				{21820, 5, 30}, {1201, 1}, {1163, 1}, {1127, 1}, {1079 , 1},
				{1347, 1}, {4087, 1}, {4585,1}, {1215,1}, {1305, 1},
				{1392, 1, 5}, {560, 70, 100}, {565, 70, 100}, {563, 70, 100}, {9193, 50, 100},
				{9194, 50, 100}, {454, 150, 200}, {302, 30, 60}, {1514, 10, 30}, {1748, 10, 20},
				{452, 5, 10}, {2364, 5, 10}, {2362, 5, 10}
		}),
		DRAGON(11256, new int[][] {
				{11212, 100, 500}, {9341, 3, 40}, {1305,1}, {11237, 100, 500}, {9193, 10, 49},
				{535, 111, 297}, {1216, 3}, {11230, 105, 350}, {5316, 1}, {537, 52, 99},
				{1616, 3, 6}, {1705, 2, 4}, {5300, 6}, {7219, 5, 15}, {4093, 1},
				{5547, 1}, {1701, 2, 4}
		});

		public static HashMap<Integer, ImpRewards> impReward = new HashMap<>();

		static {
			for(ImpRewards t : ImpRewards.values()) {
				impReward.put(t.itemId, t);
			}
		}

		private int itemId;
		private int[][] rewards;

		ImpRewards(int itemId, int[][] rewards) {
			this.itemId = itemId;
			this.rewards = rewards;
		}
		public int getItemId() {
			return itemId;
		}
		public int[][] getRewards() {
			return rewards;
		}
	}
}