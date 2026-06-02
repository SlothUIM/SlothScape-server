package server.model.players.skills.agility.impl.rooftop;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.agility.MarkOfGrace;

/**
 * Rooftop Agility Varrock
 * Optimized for OSRS-like timing and fluid movement transitions.
 */
public class RooftopVarrock {

	public static final int ROUGH_WALL = 14412,
			CLOTHES_LINE = 14413, LEAP_GAP = 14414,
			BALANCE_WALL_JUMP = 14832, LEAP_2ND_GAP = 14833,
			LEAP_3RD_GAP = 14834, LEAP_4TH_GAP = 14835,
			HURDLE_LEDGE = 14836, JUMP_OFF_EDGE = 14841;

	public static int[] VARROCK_OBJECTS = { ROUGH_WALL, CLOTHES_LINE, LEAP_GAP, BALANCE_WALL_JUMP, LEAP_2ND_GAP, LEAP_3RD_GAP, LEAP_4TH_GAP, HURDLE_LEDGE, JUMP_OFF_EDGE };

	public boolean execute(final Player c, final int objectId) {

		for (int id : VARROCK_OBJECTS) {
			if (c.getAgilityHandler().checkLevel(c, objectId)) {
				return false;
			}
			if (id == objectId) {
				MarkOfGrace.spawnMarks(c, "VARROCK");
			}
		}

		switch (objectId) {
		case ROUGH_WALL:
			c.setMove(new int[][]{{3221, 3414}}, "WEST", 828, -1, 60, 90, 3221, 3414, 2, 1, 1, 3);

			return true;

		case CLOTHES_LINE:
			if (AgilityHandler.failObstacle(c, 3212, 3414, 0)) {
				return false;
			}
			if (c.getAgilityHandler().hotSpot(c, 3214, 3414)) {
				// Speed 45 makes the leap across the line look consistent
				c.setMove(new int[][]{{3211, 3414}}, "WEST", 741, -1, 15, 30, 3211, 3414, 1, 1, 1, c.getHeight());
				c.getAgilityHandler().RoofAgilityProgress[2][1] = true;
				c.getAgilityHandler().lapProgress(c, 1, CLOTHES_LINE, 2);
			}
			return true;

		case LEAP_GAP:
			c.setMove(new int[][]{{3198, 3416}}, "WEST", 2586, -1, 0, 30, 3198, 3416, 2, 1, 1, 1);
			/*CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.getX() == 3198 && c.getY() == 3416) {
						c.setMove(new int[][]{{3197, 3416}}, "WEST", 2588, -1, 0, 20, 3197, 3416, 2, 1, 1, 1);
						container.stop();
					}
				}
				@Override
				public void stop() {}
			}, 2);*/
			c.getAgilityHandler().RoofAgilityProgress[2][2] = true;
			c.getAgilityHandler().lapProgress(c, 2, LEAP_GAP, 2);
			return true;

			case BALANCE_WALL_JUMP:
				// 1. Initial click starts the run-up.
				// The rest of the obstacle is caught by agilityProcess!
				c.setMove(new int[][]{{3193, 3416}}, "WEST", 1995, -1, 30, 60, 3193, 3416, 1, 1, 1, 1);
				return true;

		case LEAP_2ND_GAP:
			if (c.getAgilityHandler().RoofAgilityProgress[2][3]) {
				c.setMove(new int[][]{{c.getX(), 3399}}, "SOUTH", 2583, -1, 30, 60, c.getX(), 3399, 1, 1, 1, 2);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.getY() == 3399 && c.getHeight() == 2) {
							c.setMove(new int[][]{{c.getX(), 3399}}, "SOUTH", 2585, -1, 30, 60, c.getX(), 3399, 1, 1, 1, 3);
						} else if (c.getY() == 3399 && c.getHeight() == 3) {
							c.setMove(new int[][]{{c.getX(), 3398}}, "SOUTH", 1209, -1, 30, 60, c.getX(), 3398, 1, 1, 1, 3);
						} else if (c.getY() == 3398 && c.getHeight() == 3) {
							c.getAgilityHandler().RoofAgilityProgress[2][4] = true;
							c.getAgilityHandler().lapProgress(c, 4, LEAP_2ND_GAP, 2);
							container.stop();
						}
					}
					@Override
					public void stop() {}
				}, 2);
			} else {
				c.sendMessage("Apparently I skipped a gap, ouch..");
			}
			return true;

		case LEAP_3RD_GAP:
			c.setMove(new int[][]{{3215, 3399}}, "EAST", 3067, -1, 20, 60, 3215, 3399, 2, 1, 1, 3);
			c.getAgilityHandler().RoofAgilityProgress[2][5] = true;
			return true;

		case LEAP_4TH_GAP:
			if (c.getAgilityHandler().RoofAgilityProgress[2][5]) {
				c.setMove(new int[][]{{3236, 3403}}, "EAST", 2586, -1, 0, 28, 3236, 3403, 2, 1, 1, 3);
				c.getAgilityHandler().RoofAgilityProgress[2][6] = true;
			} else {
				c.sendMessage("Apparently I skipped a gap, ouch..");
			}
			return true;

		case HURDLE_LEDGE:
			// Ledge hurdles are quick
			c.setMove(new int[][]{{3236, 3410}}, "NORTH", 1603, -1, 30, 60, 3236, 3410, 2, 1, 1, 3);
			c.getAgilityHandler().RoofAgilityProgress[2][7] = true;
			return true;

		case JUMP_OFF_EDGE:
			c.getAgilityHandler().roofTopFinished(c, 7, 238, 8000, 2);
			// Height 0 dismount
			c.setMove(new int[][]{{3236, 3416}, {3236, 3417}}, "NORTH", 2586, -1, 30, 60, 3236, 3417, 2, 1, 1, 0);
			c.VarrockRooftopLapCount++;
			c.sendMessage("Your Varrock Rooftop lap count is: "+c.VarrockRooftopLapCount);
			c.getAD().completeAchievement("VarrockMedium", "Complete a lap of the Varrock Rooftop Course.", 13);
			return true;
		}
		return false;
	}
}