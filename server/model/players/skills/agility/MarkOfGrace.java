package server.model.players.skills.agility;

import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.World;

/**
 * Mark of grace
 * * @author Matt
 * Refactored for global rooftop support.
 */
public class MarkOfGrace {

	private static final int MARK_OF_GRACE = 11849;

	// --- COURSE SPAWN COORDINATES ---
	private static final int[][] DRAYNOR_COORDINATES = {
			{ 3099, 3277, 3 }, { 3089, 3274, 3 }, { 3090, 3266, 3 }, { 3088, 3258, 3 }, { 3093, 3255, 3 }, { 3100, 3258, 3 }
	};
	private static final int[][] AL_KHARID_COORDINATES = {
			{ 3276, 3188, 3 }, { 3268, 3166, 3 }, { 3300, 3164, 3 }, { 3316, 3163, 1 }, { 3316, 3174, 2 }
	};
	private static final int[][] VARROCK_COORDINATES = {
			{ 3219, 3418, 3 }, { 3202, 3417, 3 }, { 3195, 3416, 1 }, { 3196, 3404, 3 },
			{ 3193, 3393, 3 }, { 3205, 3403, 3 }, { 3218, 3395, 3 }, { 3240, 3411, 3 }
	};
	private static final int[][] CANIFIS_COORDINATES = {
			{ 3506, 3492, 2 }, { 3501, 3505, 2 }, { 3487, 3499, 2 }, { 3478, 3493, 3 }, { 3478, 3483, 2 }, { 3489, 3476, 3 }, { 3510, 3476, 2 }
	};
	private static final int[][] FALADOR_COORDINATES = {
			{ 3036, 3342, 3 }, { 3047, 3344, 3 }, { 3050, 3357, 3 }, { 3046, 3361, 3 }, { 3035, 3361, 3 }, { 3027, 3353, 3 }, { 3020, 3353, 3 }, { 3016, 3346, 3 }
	};
	private static final int[][] SEERS_COORDINATES = {
			{ 2728, 3495, 3 }, { 2707, 3492, 2 }, { 2713, 3479, 2 }, { 2698, 3463, 2 }
	};
	private static final int[][] POLLNIVNEACH_COORDINATES = {
			{ 3351, 2962, 1 }, { 3354, 2975, 1 }, { 3358, 2984, 1 }, { 3360, 2993, 1 }, { 3365, 2976, 1 }, { 3368, 2982, 1 }
	};
	private static final int[][] RELLEKKA_COORDINATES = {
			{ 2622, 3676, 3 }, { 2617, 3662, 3 }, { 2628, 3652, 3 }, { 2643, 3650, 3 }, { 2649, 3659, 3 }, { 2658, 3670, 3 }
	};
	private static final int[][] ARDOUGNE_COORDINATES = {
			{ 2671, 3303, 3 }, { 2663, 3318, 3 }, { 2655, 3318, 3 }, { 2653, 3312, 3 },
			{ 2651, 3307, 3 }, { 2653, 3302, 3 }, { 2656, 3297, 3 }, { 2668, 3297, 0 }
	};

	public static void spawnMarks(Player c, String location) {
		int divisor = 30; // Default Drop Rate Divisor
		int[][] targetCoords = null;

		// 1. Assign the correct coordinate array and calculate Diary drop boosts
		switch (location.toUpperCase()) {
			case "DRAYNOR":
				targetCoords = DRAYNOR_COORDINATES;
				divisor = getDiaryDivisor(c, 13560, 13561, 13562, 13125, -1); // Lumbridge Diary
				break;
			case "AL_KHARID":
				targetCoords = AL_KHARID_COORDINATES;
				divisor = getDiaryDivisor(c, 13133, 13134, 13135, 13136, -1); // Desert Diary
				break;
			case "VARROCK":
				targetCoords = VARROCK_COORDINATES;
				divisor = getDiaryDivisor(c, 13104, 13105, 13106, 13107, -1); // Varrock Diary
				break;
			case "CANIFIS":
				targetCoords = CANIFIS_COORDINATES;
				divisor = getDiaryDivisor(c, 13113, 13114, 13115, 13116, -1); // Morytania Diary
				break;
			case "FALADOR":
				targetCoords = FALADOR_COORDINATES;
				divisor = getDiaryDivisor(c, 13117, 13118, 13119, 13120, -1); // Falador Diary
				break;
			case "SEERS":
				targetCoords = SEERS_COORDINATES;
				divisor = getDiaryDivisor(c, 13137, 13138, 13139, 13140, -1); // Kandarin Diary
				break;
			case "POLLNIVNEACH":
				targetCoords = POLLNIVNEACH_COORDINATES;
				divisor = getDiaryDivisor(c, 13133, 13134, 13135, 13136, -1); // Desert Diary
				break;
			case "RELLEKKA":
				targetCoords = RELLEKKA_COORDINATES;
				divisor = getDiaryDivisor(c, 13129, 13130, 13131, 13132, -1); // Fremennik Diary
				break;
			case "ARDOUGNE":
				targetCoords = ARDOUGNE_COORDINATES;
				divisor = getDiaryDivisor(c, 13121, 13122, 13123, 13124, 20760); // Ardougne Diary (Includes Max Cape 20760)
				break;
			default:
				return; // Invalid location string passed
		}

		// Safety check
		if (targetCoords == null) return;

		// 2. Roll for the drop based on the player's level and diary divisor
		int chance = c.getSkills().getLevel(Skill.AGILITY) / divisor;

		if (Misc.random(chance) == 0) {
			if (System.currentTimeMillis() - c.lastMarkDropped < 3000) {
				return;
			}

			// 3. Pick a random spawn point from the selected course array
			int i = Misc.random(targetCoords.length - 1);
			int x = targetCoords[i][0];
			int y = targetCoords[i][1];
			int z = targetCoords[i][2];

			World.getWorld().itemHandler.createGroundItem(c, MARK_OF_GRACE, x, y, z, 1, c.playerId);
			c.lastMarkDropped = System.currentTimeMillis();
		}
	}

	/**
	 * Helper method to calculate drop rate boosts cleanly.
	 * Higher divisor = Better drop chance.
	 */
	private static int getDiaryDivisor(Player c, int easy, int med, int hard, int elite, int extraElite) {
		if (extraElite != -1 && c.getItems().playerHasItem(extraElite)) return 60;
		if (elite != -1 && c.getItems().playerHasItem(elite)) return 60;
		if (hard != -1 && c.getItems().playerHasItem(hard)) return 50;
		if (med != -1 && c.getItems().playerHasItem(med)) return 45;
		if (easy != -1 && c.getItems().playerHasItem(easy)) return 40;
		return 30; // Base rate (No diary completed)
	}
}