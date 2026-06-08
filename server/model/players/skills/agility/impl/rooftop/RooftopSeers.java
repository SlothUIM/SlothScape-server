package server.model.players.skills.agility.impl.rooftop;

import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.agility.MarkOfGrace;

/**
 * Rooftop Agility Seers
 * Upgraded to the AgilitySequence Builder Pattern.
 * Mapped 1:1 using exact OSRS coordinates from debug footage.
 */
public class RooftopSeers {

	public static final int WALL = 14927, JUMP_GAP = 14928, TIGHT_ROPE = 14932,
			JUMP_2ND_GAP = 14929, JUMP_3RD_GAP = 14930, JUMP_EDGE = 14931;

	public static int[] SEERS_OBJECTS = { WALL, JUMP_GAP, TIGHT_ROPE, JUMP_2ND_GAP, JUMP_3RD_GAP, JUMP_EDGE };

	public boolean execute(final Player c, final int objectId) {

		for (int id : SEERS_OBJECTS) {
			// Re-added your 3-second fail timer requirement
			if (System.currentTimeMillis() - c.lastObstacleFail < 3000) {
				return false;
			}
			if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
			if (id == objectId) MarkOfGrace.spawnMarks(c, "SEERS");
		}

		switch (objectId) {
			case WALL:
				AgilitySequence.create(c, 5, 0, WALL)
						.face(2729,3489)
						.walk(2729, 3489, 0)
						.waitUntil(2729, 3489, 0)
						.slide(2729, 3489, 1, "NORTH", 737, 0, 0)
						//.anim(737)
						.waitUntil(2729, 3489, 1)
						.teleport(2729, 3488, 1)
						.waitUntil(2729, 3488, 1)
						.slide(2729, 3488, 1, "NORTH", 1118, 30, 45) // Pull up to the roof
						.waitUntil(2729, 3488, 1)
						.stopAnim()
						.teleport(2729, 3491, 3)
						.waitUntil(2729, 3491, 3)
						.xp(45)
						.execute();
				return true;

			case JUMP_GAP:
				AgilitySequence.create(c, 5, 1, JUMP_GAP)
						.walkOff()
						.slide(2721, 3494, 3, "WEST", 2586, 0, 30)
						.waitUntil(2721, 3494, 3)
						.anim(2588)
						.teleport(2719, 3495, 2)
						.waitUntil(2719, 3495, 2)
						.face(2713, 3494)
						.slide(2719, 3495, 2, "WEST", 2586, 20, 45)
						.waitUntil(2719, 3495, 2)
						.stopAnim()
						.anim(2588) // Jump across to next roof
						.teleport(2713, 3494, 2)
						.waitUntil(2713, 3494, 2)
						.xp(20)
						.execute();
				return true;

			case TIGHT_ROPE:
				AgilitySequence.create(c, 5, 2, TIGHT_ROPE)
						.hotSpot(2710, 3489)
						.walkOff()
						.slide(new int[][]{{2710, 3485}, {2710, 3483}, {2710, 3481} }, 2710, 3481, 2, 1, "SOUTH", 762, 30, 80)
						.waitUntil(2710, 3481, 2)
						.walk(2710, 3480, 2)
						.xp(20)
						.execute();
				return true;

			case JUMP_2ND_GAP:
				AgilitySequence.create(c, 5, 3, JUMP_2ND_GAP)
						.hotSpot(c.getX(), 3477)
						.walkOff()
						.face(c.getX(), 3472)
						.slide(c.getX(), 3477, 2, "SOUTH", 2583, 0, 30)
						.waitUntil(c.getX(), 3477, 2)
						.stopAnim()
						.teleport(c.getX(), 3474, 3)
						.slide(c.getX(), 3472, 3, "SOUTH", 2585, 0, 60) // Jump UP to Z: 3
						.waitUntil(c.getX(), 3472, 3)
						.stopAnim()
						.teleport(c.getX(), 3472, 3)
						.xp(15)
						.execute();
				return true;

			case JUMP_3RD_GAP:
				AgilitySequence.create(c, 5, 4, JUMP_3RD_GAP)
						.face(c.getX(), 3465)
						.walkOff()
						.slide(2700, 3469, 2, "SOUTH", 2586, 0, 0)
						.waitUntil(2700, 3469, 2)
						.anim(2588)
						.teleport(2700, 3465, 2)
						.waitUntil(2700, 3465, 2)
						.xp(15)
						.execute();
				return true;

			case JUMP_EDGE:
				AgilitySequence.create(c, 5, 4, JUMP_3RD_GAP)
						.npcAnimation(5921, 2, 863)
						//.hotSpot(2700, 3470) // Fixed your old code's massive coordinate error here!
						.walkOff()
						.slide(2703, 3464, 2, "EAST", 2586, 0, 30)
						//.anim(2586)
						.waitUntil(2703, 3464, 2)
						.anim(2588)
						.teleport(2704, 3464, 0)
						.waitUntil(2704, 3464, 0)
						.finish(6000, 570) // Kept your custom 6000 XP & 1/570 pet chance values
						.execute();

				// Add your Kandarin Diary / Daily Task progress here if applicable:
				// c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.SEERS_AGILITY);
				// DailyTasks.increase(c, PossibleTasks.SEERS_COURSE);
				return true;
		}
		return false;
	}
}