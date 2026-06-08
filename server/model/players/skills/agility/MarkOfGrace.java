package server.model.players.skills.agility;

import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.World;

/**
 * Mark of grace & Graceful Shop Handler
 * Refactored for 100% Authentic OSRS Mechanics
 */
public class MarkOfGrace {

	public static final int MARK_OF_GRACE = 11849;
	public static final int AMYLASE_PACK = 12641;

	// --- GRACEFUL ITEM IDS (Standard) ---
	public static final int HOOD = 11850;
	public static final int CAPE = 11852;
	public static final int TOP = 11854;
	public static final int LEGS = 11856;
	public static final int GLOVES = 11858;
	public static final int BOOTS = 11860;

	// --- COURSE SPAWN COORDINATES ---
	private static final int[][] DRAYNOR_COORDINATES = { { 3099, 3277, 3 }, { 3089, 3274, 3 }, { 3090, 3266, 3 }, { 3088, 3258, 3 }, { 3093, 3255, 3 }, { 3100, 3258, 3 } };
	private static final int[][] AL_KHARID_COORDINATES = { { 3276, 3188, 3 }, { 3268, 3166, 3 }, { 3300, 3164, 3 }, { 3316, 3163, 1 }, { 3316, 3174, 2 } };
	private static final int[][] VARROCK_COORDINATES = { { 3219, 3418, 3 }, { 3202, 3417, 3 }, { 3195, 3416, 1 }, { 3196, 3404, 3 }, { 3193, 3393, 3 }, { 3205, 3403, 3 }, { 3218, 3395, 3 }, { 3240, 3411, 3 } };
	private static final int[][] CANIFIS_COORDINATES = { { 3506, 3492, 2 }, { 3501, 3505, 2 }, { 3487, 3499, 2 }, { 3478, 3493, 3 }, { 3478, 3483, 2 }, { 3489, 3476, 3 }, { 3510, 3476, 2 } };
	private static final int[][] FALADOR_COORDINATES = { { 3036, 3342, 3 }, { 3047, 3344, 3 }, { 3050, 3357, 3 }, { 3046, 3361, 3 }, { 3035, 3361, 3 }, { 3027, 3353, 3 }, { 3020, 3353, 3 }, { 3016, 3346, 3 } };
	private static final int[][] SEERS_COORDINATES = { { 2728, 3495, 3 }, { 2707, 3492, 2 }, { 2713, 3479, 2 }, { 2698, 3463, 2 } };
	private static final int[][] POLLNIVNEACH_COORDINATES = { { 3351, 2962, 1 }, { 3354, 2975, 1 }, { 3358, 2984, 1 }, { 3360, 2993, 1 }, { 3365, 2976, 1 }, { 3368, 2982, 1 } };
	private static final int[][] RELLEKKA_COORDINATES = { { 2622, 3676, 3 }, { 2617, 3662, 3 }, { 2628, 3652, 3 }, { 2643, 3650, 3 }, { 2649, 3659, 3 }, { 2658, 3670, 3 } };
	private static final int[][] ARDOUGNE_COORDINATES = { { 2671, 3303, 3 }, { 2663, 3318, 3 }, { 2655, 3318, 3 }, { 2653, 3312, 3 }, { 2651, 3307, 3 }, { 2653, 3302, 3 }, { 2656, 3297, 3 }, { 2668, 3297, 0 } };
	private static final int GRACE_SHOP_ID = 192;

	public static void spawnMarks(Player c, String location) {
		int[][] targetCoords = getCoordinates(location);
		if (targetCoords == null) return;

		// 1. Calculate Time Requirement (Default 3 mins)
		long requiredDelayMs = 180_000;

		// Diary Cooldown Reductions
		if (location.equalsIgnoreCase("POLLNIVNEACH") && hasDiary(c, 13135)) { // Desert Hard
			if (Misc.random(3) == 0) requiredDelayMs = 120_000;
		} else if (location.equalsIgnoreCase("RELLEKKA") && hasDiary(c, 13131)) { // Fremennik Hard
			if (Misc.random(3) == 0) requiredDelayMs = 120_000;
		} else if (location.equalsIgnoreCase("ARDOUGNE") && (hasDiary(c, 13124) || hasDiary(c, 20760))) { // Ardougne Elite
			if (Misc.random(2) == 0) requiredDelayMs = 120_000;
		}

		// Check if enough time has passed
		if (System.currentTimeMillis() - c.lastMarkDropped < requiredDelayMs) {
			return;
		}

		// 2. Base Spawn Chance Logic
		double spawnChance;
		if (location.equalsIgnoreCase("RELLEKKA")) {
			spawnChance = 2.0 / 5.0; // 40%
		} else if (location.equalsIgnoreCase("CANIFIS") || location.equalsIgnoreCase("ARDOUGNE")) {
			spawnChance = 2.0 / 3.0; // ~66%
		} else {
			spawnChance = 2.0 / 6.0; // ~33%
		}

		// 3. Level Penalty Check (80% reduction if 20 levels over req, EXCLUDING Canifis)
		int reqLevel = getCourseRequirement(location);
		int currentAgility = c.getSkills().getLevel(Skill.AGILITY); // Unboosted check

		if (currentAgility >= reqLevel + 20 && !location.equalsIgnoreCase("CANIFIS")) {
			spawnChance *= 0.20; // 80% reduction
		}

		// 4. Initial Roll
		boolean success = Math.random() < spawnChance;

		// 5. Diary Second-Rolls (If initial roll fails)
		if (!success) {
			if (location.equalsIgnoreCase("SEERS")) {
				if (hasDiary(c, 13139) && Math.random() < 0.15) { // Kandarin Hard (15%)
					success = true;
				} else if (hasDiary(c, 13138) && Math.random() < 0.10) { // Kandarin Med (10%)
					success = true;
				} else if (hasDiary(c, 13137) && Math.random() < 0.05) { // Kandarin Easy (5%)
					success = true;
				}
			} else if (location.equalsIgnoreCase("ARDOUGNE")) {
				if ((hasDiary(c, 13124) || hasDiary(c, 20760)) && Math.random() < 0.25) { // Ardougne Elite (25%)
					success = true;
				}
			}
		}

		// 6. Spawn the Mark
		if (success) {
			int i = Misc.random(targetCoords.length - 1);
			int x = targetCoords[i][0];
			int y = targetCoords[i][1];
			int z = targetCoords[i][2];

			//World.getWorld().itemHandler.createGroundItem(c, MARK_OF_GRACE, x, y, z, 1, c.playerId);
			World.getWorld().itemHandler.createGroundItem(c, MARK_OF_GRACE, x, y, z, 1);
			c.lastMarkDropped = System.currentTimeMillis();
			c.sendMessage("<col=005500>You notice a mark of grace on the roof.</col>");
		}
	}

	// --- GRACEFUL SHOP MECHANICS ---

	public static void openGraceShop(Player c) {
		//c.sendMessage("Trade Grace in the Rogues' Den to access her shop interface.");
		c.getShops().openShop(GRACE_SHOP_ID);
	}

	public static boolean buyGracefulItem(Player c, int itemId) {
		int cost = getGracefulCost(itemId);
		if (cost == -1) return false;

		if (!c.getItems().playerHasItem(MARK_OF_GRACE, cost)) {
			c.sendMessage("You need " + cost + " Marks of Grace to buy this.");
			return true;
		}

		c.getItems().deleteItem(MARK_OF_GRACE, cost);
		c.getItems().addItem(itemId, 1);
		c.sendMessage("You have purchased an item from Grace.");
		return true;
	}

	public static boolean recolorGracefulItem(Player c, int currentItemId, int recolorId) {
		if (!isGracefulPiece(currentItemId)) {
			c.sendMessage("You can only recolor Graceful pieces.");
			return false;
		}

		int cost = 15; // 15 marks per piece
		if (!c.getItems().playerHasItem(MARK_OF_GRACE, cost)) {
			c.sendMessage("You need " + cost + " Marks of Grace to have Osten recolor this.");
			return true;
		}

		c.getItems().deleteItem(currentItemId, 1);
		c.getItems().deleteItem(MARK_OF_GRACE, cost);
		c.getItems().addItem(recolorId, 1);
		c.sendMessage("Osten successfully dyes your Graceful piece.");
		return true;
	}

	// --- UTILITY METHODS ---

	public static int getGracefulCost(int itemId) {
        return switch (itemId) {
            case HOOD -> 35;
            case CAPE, BOOTS -> 40;
            case TOP -> 55;
            case LEGS -> 60;
            case GLOVES -> 30;
            case AMYLASE_PACK -> 10;
            default -> -1;
        };
	}

	public static boolean isGracefulPiece(int itemId) {
		// Base Graceful
        return itemId == HOOD || itemId == CAPE || itemId == TOP || itemId == LEGS || itemId == GLOVES || itemId == BOOTS;
		// You would add colored graceful IDs here as well
    }

	private static int getCourseRequirement(String location) {
        return switch (location.toUpperCase()) {
            case "DRAYNOR" -> 1;
            case "AL_KHARID" -> 20;
            case "VARROCK" -> 30;
            case "CANIFIS" -> 40;
            case "FALADOR" -> 50;
            case "SEERS" -> 60;
            case "POLLNIVNEACH" -> 70;
            case "RELLEKKA" -> 80;
            case "ARDOUGNE" -> 90;
            default -> 1;
        };
	}

	private static int[][] getCoordinates(String location) {
        return switch (location.toUpperCase()) {
            case "DRAYNOR" -> DRAYNOR_COORDINATES;
            case "AL_KHARID" -> AL_KHARID_COORDINATES;
            case "VARROCK" -> VARROCK_COORDINATES;
            case "CANIFIS" -> CANIFIS_COORDINATES;
            case "FALADOR" -> FALADOR_COORDINATES;
            case "SEERS" -> SEERS_COORDINATES;
            case "POLLNIVNEACH" -> POLLNIVNEACH_COORDINATES;
            case "RELLEKKA" -> RELLEKKA_COORDINATES;
            case "ARDOUGNE" -> ARDOUGNE_COORDINATES;
            default -> null;
        };
	}

	private static boolean hasDiary(Player c, int itemId) {
		return c.getItems().playerHasItem(itemId) || c.getItems().isWearingItem(itemId);
	}
}