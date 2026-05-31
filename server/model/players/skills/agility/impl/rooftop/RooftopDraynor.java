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
public class RooftopDraynor {

		public static final int ROUGH_WALL = 11404, 
				TIGHT_ROPE = 11405, TIGHT_ROPE_2ND = 11406, 
				BALANCE_WALL_JUMP = 14832, LEAP_2ND_GAP = 14833, 
				LEAP_3RD_GAP = 14834, LEAP_4TH_GAP = 14835,
				HURDLE_LEDGE = 14836, JUMP_OFF_EDGE = 14841;
		
		public static int[] DRAYNOR_OBJECTS = { ROUGH_WALL, TIGHT_ROPE, TIGHT_ROPE_2ND, BALANCE_WALL_JUMP, LEAP_2ND_GAP, LEAP_3RD_GAP, LEAP_4TH_GAP, HURDLE_LEDGE, JUMP_OFF_EDGE };

		public boolean execute(final Player c, final int objectId) {
			
			for (int id : DRAYNOR_OBJECTS) {
				if (c.getAgilityHandler().checkLevel(c, objectId)) {
					return false;
				}
				if (id == objectId) {
					MarkOfGrace.spawnMarks(c, "DRAYNOR");
				}
			}
			
			switch (objectId) {
			case ROUGH_WALL:
				c.setMove(new int[][]{{3102, 3279}}, "WEST", 828, -1, 28, 0, 3102, 3279, 2, 1, 1, 3);
				
				c.getAgilityHandler().RoofAgilityProgress[0][0] = true;
				c.getAgilityHandler().lapProgress(c, 0, ROUGH_WALL, 0);
				return true;
				
			case TIGHT_ROPE:
				if (AgilityHandler.failObstacle(c, 3095, 3277, 0)) {
					return false;
				}
				if (c.getAgilityHandler().hotSpot(c, 3099, 3277)) {
					// Speed 45 makes the leap across the line look consistent
					c.setMove(new int[][]{{3097, 3277},{3094, 3277}, {3091, 3277}}, "WEST", 762, -1, 20, 50, 3091, 3277, 2, 1, 1, c.getHeight());
					c.getAgilityHandler().RoofAgilityProgress[0][1] = true;
					c.getAgilityHandler().lapProgress(c, 1, TIGHT_ROPE, 0);
				}
				return true;
				
			case TIGHT_ROPE_2ND:
				if (AgilityHandler.failObstacle(c, 3092, 3274, 0)) {
					return false;
				}
				if (c.getAgilityHandler().hotSpot(c, 3092, 3276)) {
					// Speed 45 makes the leap across the line look consistent
					c.setMove(new int[][]{{3092, 3274},{3092, 3271}, {3092, 3270}, {3092, 3267}}, "SOUTH", 762, -1, 20, 50, 3092, 3267, 2, 1, 1, c.getHeight());
					c.getAgilityHandler().RoofAgilityProgress[0][2] = true;
					c.getAgilityHandler().lapProgress(c, 2, TIGHT_ROPE_2ND, 0);
				}
				return true;
				
			case BALANCE_WALL_JUMP:
				// Run-up
				c.setMove(new int[][]{{3193, 3416}}, "WEST", 1995, -1, 10, 25, 3193, 3416, 1, 1, 1, 1);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (c.getX() == 3193 && c.getY() == 3416) {
							// Leap to Wall
							c.setMove(new int[][]{{3190, 3414}}, "WEST", 2583, -1, 10, 0, 3190, 3414, 1, 1, 1, 1);
						} else if (c.getX() == 3190 && c.getY() == 3414) {
							// The slow wall shimmy
							c.setMove(new int[][]{{3190, 3411}}, "WEST", 1122, -1, 0, 28, 3190, 3411, 1, 1, 1, 1);
						} else if (c.getX() == 3190 && c.getY() == 3411) {
							// The Reach (Anim 1124)
							c.setMove(new int[][]{{3190, 3410}}, "WEST", 1124, -1, 0, 30, 3190, 3410, 1, 1, 1, 1);
						} else if (c.getX() == 3190 && c.getY() == 3410) {
							// Final slide
							c.setMove(new int[][]{{3190, 3407}}, "SOUTH", 756, -1, 0, 0, 3190, 3407, 1, 1, 1, 1);
						} else if (c.getX() == 3190 && c.getY() == 3407) {
							c.turnPlayerTo(3192, 3405);
							c.setMove(new int[][]{{3192, 3405}}, "EAST", 3067, -1, 10, 28, 3192, 3405, 1, 1, 1, 3);
							c.getAgilityHandler().RoofAgilityProgress[0][3] = true;
							c.getAgilityHandler().lapProgress(c, 3, BALANCE_WALL_JUMP, 2);
							container.stop();
						}
					}
					@Override
					public void stop() {}
				}, 2); // 2 Ticks for shimmy logic feels much smoother
				return true;
				
			case LEAP_2ND_GAP:
				if (c.getAgilityHandler().RoofAgilityProgress[0][3]) {
					c.setMove(new int[][]{{c.getX(), 3399}}, "SOUTH", 2583, -1, 0, 30, c.getX(), 3399, 1, 1, 1, 2);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							if (c.getY() == 3399 && c.getHeight() == 2) {
								c.setMove(new int[][]{{c.getX(), 3399}}, "SOUTH", 2585, -1, 0, 28, c.getX(), 3399, 1, 1, 1, 3);
							} else if (c.getY() == 3399 && c.getHeight() == 3) {
								c.setMove(new int[][]{{c.getX(), 3398}}, "SOUTH", 1209, -1, 0, 28, c.getX(), 3398, 1, 1, 1, 3);
							} else if (c.getY() == 3398 && c.getHeight() == 3) {
								c.getAgilityHandler().RoofAgilityProgress[0][4] = true;
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
				c.setMove(new int[][]{{3215, 3399}}, "EAST", 3067, -1, 0, 28, 3215, 3399, 2, 1, 1, 3);
				c.getAgilityHandler().RoofAgilityProgress[0][5] = true;
				return true;
				
			case LEAP_4TH_GAP:
				if (c.getAgilityHandler().RoofAgilityProgress[0][5]) {
					c.setMove(new int[][]{{3236, 3403}}, "EAST", 2586, -1, 0, 28, 3236, 3403, 2, 1, 1, 3);
					c.getAgilityHandler().RoofAgilityProgress[0][6] = true;
				} else {
					c.sendMessage("Apparently I skipped a gap, ouch..");
				}
				return true;
				
			case HURDLE_LEDGE:
				// Ledge hurdles are quick
				c.setMove(new int[][]{{3236, 3410}}, "NORTH", 1603, -1, 10, 35, 3236, 3410, 2, 1, 1, 3);
				c.getAgilityHandler().RoofAgilityProgress[0][7] = true;
				return true;
				
			case JUMP_OFF_EDGE:
				c.getAgilityHandler().roofTopFinished(c, 7, 238, 8000, 2);
				// Height 0 dismount
				c.setMove(new int[][]{{3236, 3416}, {3236, 3417}}, "NORTH", 2586, -1, 10, 40, 3236, 3417, 2, 1, 1, 0);
				c.VarrockRooftopLapCount++;
				c.sendMessage("Your Varrock Rooftop lap count is: "+c.VarrockRooftopLapCount);
				c.getAD().completeAchievement("VarrockMedium", "Complete a lap of the Varrock Rooftop Course.", 13);
				return true;
			}
			return false;
		}
	}
