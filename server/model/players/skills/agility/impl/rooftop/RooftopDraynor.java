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
				BALANCE_WALL = 11430, LEAP_TO_ROOF = 11630,
				JUMP_GAP = 11631, CLIMB_DOWN_CRATE = 11632;
		
		public static int[] DRAYNOR_OBJECTS = { ROUGH_WALL, TIGHT_ROPE, TIGHT_ROPE_2ND, BALANCE_WALL, LEAP_TO_ROOF, JUMP_GAP, CLIMB_DOWN_CRATE };

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
				c.setMove(new int[][]{{3102, 3279}}, "WEST", 828, -1, 30, 60, 3102, 3279, 2, 1, 1, 3);
				
				c.getAgilityHandler().RoofAgilityProgress[0][0] = true;
				c.getAgilityHandler().lapProgress(c, 0, ROUGH_WALL, 0);
				return true;

				case TIGHT_ROPE:
					if (AgilityHandler.failObstacle(c, 3095, 3277, 0)) {
						return false;
					}

					// Segmented path: Move 2 tiles at a time to prevent client tearing.
					// speed1: 0 (No pausing between segments)
					// speed2: 60 (Takes 2 ticks to cross each 2-tile segment = perfect walk speed)
					c.setMove(
							new int[][] { {3096, 3277}, {3094, 3277}, {3092, 3277} , {3091, 3277} },
							"WEST",
							762,
							-1,
							25,
							50,
							3090, 3277,
							0, 1, 1, c.getHeight()
					);
					return true;

				case TIGHT_ROPE_2ND:
					if (AgilityHandler.failObstacle(c, 3092, 3274, 0)) {
						return false;
					}

					// We removed the hotSpot check! As soon as they arrive at the object and click it,
					// it smoothly walks them South across the segmented path.
					c.setMove(
							new int[][] { {3092, 3276}},
							"EAST",
							c.playerWalkIndex,
							-1,
							0,   // speed1: 0 (No pauses between tiles)
							30,  // speed2: 60 (2 ticks per segment for smooth walking)
							3092, 3276,
							0, 1, 1, c.getHeight()
					);

					return true;

				case BALANCE_WALL:
					c.setMove(new int[][]{{3089, 3264}, {3089, 3262}}, "SOUTH", 756, -1, 0, 60, 3089, 3262, 2, 1, 1, c.getHeight());
					c.getAgilityHandler().RoofAgilityProgress[0][3] = true;
					c.getAgilityHandler().lapProgress(c, 3, BALANCE_WALL, 0);
					return true;
				
			case LEAP_TO_ROOF:
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
								c.getAgilityHandler().lapProgress(c, 4, LEAP_TO_ROOF, 2);
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
				
			case JUMP_GAP:
				c.setMove(new int[][]{{3215, 3399}}, "EAST", 3067, -1, 0, 28, 3215, 3399, 2, 1, 1, 3);
				c.getAgilityHandler().RoofAgilityProgress[0][5] = true;
				return true;
				
			case CLIMB_DOWN_CRATE:
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
