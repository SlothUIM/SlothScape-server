package server.model.players.skills;

import server.model.players.Player;
import server.model.players.skills.Cooking;
import server.model.players.skills.mining.Mining;
import server.world.World;

public class SkillHandler {

	public static void resetSkills(Player c) {

		// --- THE MODERN SYSTEM ---
		// This single block will handle Woodcutting, and eventually Mining, Fishing, etc.
		if (c.getSkilling().isSkilling()) {

			// 1. Kill the background loop in the global event handler
			World.getWorld().getEventHandler().stop(c, "skilling");

			// 2. Wipe the active skill state
			c.getSkilling().stop();

			// 3. Stop the physical animation (pickaxe, hatchet, etc.)
			c.startAnimation(65535);
		}

		// --- THE LEGACY SYSTEM ---
		// You keep these here ONLY until you convert them to use player.getSkilling().setSkill(...)
		if (c.isMining) {
			Mining.resetMining(c);
		} else if (c.playerIsFletching) {
			c.playerIsFletching = false;
		} else if (c.playerIsCooking) {
			Cooking.setCooking(c, false);
		} else if (c.isSmelting) {
			c.isSmelting = false;
		}

		// Notice we entirely deleted the 'isWoodcutting' block because the modern system handles it!
	}
}