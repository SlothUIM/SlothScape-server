package server.model.players.skills.agility.impl;

//import valius.content.achievement_diary.kandarin.KandarinDiaryEntry;
import server.model.players.Player;
import server.model.players.skills.SkillAnims;
import server.model.players.skills.agility.AgilityHandler;

/**
 * Barbarian Agility Course
 * @editor Adapted to use native Player.setMove() Force Movement
 */
public class BarbarianAgility {

	public static final int BARBARIAN_SWING_ROPE_OBJECT = 23131;
	public static final int BARBARIAN_LOG_BALANCE_OBJECT = 23144;
	public static final int BARBARIAN_NET_OBJECT = 20211;
	public static final int BARBARIAN_LEDGE_OBJECT = 23547;
	public static final int BARBARIAN_LADDER_OBJECT = 16682;
	public static final int BARBARIAN_WALL_OBJECT = 1948;

	public boolean barbarianCourse(final Player c, final int objectId) {
		if (c.getAgilityHandler().checkLevel(c, objectId)) {
			return false;
		}

		switch (objectId) {

			case BARBARIAN_SWING_ROPE_OBJECT:
				c.getAgilityHandler().resetAgilityProgress();

				c.getPA().objectAnim(c, 2551, 3550, 1750, 10, 0, 0);
				if (c.getAgilityHandler().hotSpot(c, 2551, 3554)) {
					// Uses 751 (SkillAnims.ROPE_SWING) instead of jumping!
					c.setMove(new int[][]{{2551, 3552},{2551, 3549}}, "SOUTH", SkillAnims.ROPE_SWING, -1, 20, 40, 2551, 3549, 1, 35, 1, 0);

					c.getAgilityHandler().agilityProgress[0] = true;
					c.getAgilityHandler().lapProgress(c, 0, objectId);
				}
				return true;

			case BARBARIAN_LOG_BALANCE_OBJECT:
				if (c.getAgilityHandler().hotSpot(c, 2551, 3546) || c.getAgilityHandler().hotSpot(c, 2550, 3546)) {
					// Snap to the exact starting tile if slightly off
					if (c.getY() != 3546) {
						c.getPA().movePlayer(2550, 3546, 0);
					}

					// Uses 762 (AgilityHandler.LOG_EMOTE) with a long duration (200) for the walk
					c.setMove(new int[][]{{2549, 3546},{2546, 3546},{2543, 3546},{2542, 3546},{2541, 3546}}, "WEST", AgilityHandler.LOG_EMOTE, -1, 15, 60, 2541, 3546, 1, 35, 1, 0);

					if (c.getAgilityHandler().agilityProgress[0]) {
						c.getAgilityHandler().lapProgress(c, 0, objectId);
						c.getAgilityHandler().agilityProgress[1] = true;
					}
				}
				return true;

			case BARBARIAN_NET_OBJECT:
				// Climbs UP to Height level 1
				c.setMove(new int[][]{{2538, c.getY()}}, "WEST", SkillAnims.CLIMB_UP, -1, 20, 40, 2538, c.getY(), 1, 35, 1, 1);

				if (c.getAgilityHandler().agilityProgress[1]) {
					c.getAgilityHandler().lapProgress(c, 1, objectId);
					c.getAgilityHandler().agilityProgress[2] = true;
				}
				return true;

			case BARBARIAN_LEDGE_OBJECT:
				if (c.getAgilityHandler().hotSpot(c, 2536, 3547)) {
					// Uses 756 for ledge balance, maintaining Height Level 1
					c.setMove(new int[][]{{2534, 3547}, {2532, 3547}}, "WEST", 756, -1, 0, 28, 2532, 3547, 1, 35, 1, 1);

					if (c.getAgilityHandler().agilityProgress[2]) {
						c.getAgilityHandler().lapProgress(c, 2, objectId);
						c.getAgilityHandler().agilityProgress[3] = true;
					}
				}
				return true;

			case BARBARIAN_LADDER_OBJECT:
				// Climbs DOWN to Height level 0
				c.setMove(new int[][]{{c.getX(), c.getY()}}, "SOUTH", 827, -1, 20, 40, c.getX(), c.getY(), 1, 35, 1, 0);

				if (c.getAgilityHandler().agilityProgress[3]) {
					c.getAgilityHandler().lapProgress(c, 3, objectId);
					c.getAgilityHandler().agilityProgress[4] = true;
				}
				return true;

			case BARBARIAN_WALL_OBJECT:
				if (c.getX() >= 2543) {
					return false; // Already finished the walls
				}

				// Wall 1
				if (c.getAgilityHandler().hotSpot(c, 2535, 3553) && c.getAgilityHandler().agilityProgress[4]) {
					c.setMove(new int[][]{{2537, 3553}}, "EAST", SkillAnims.CLIMB_WALL, -1, 20, 50, 2537, 3553, 1, 35, 1, 0);
					c.getAgilityHandler().agilityProgress[5] = true;
					c.getAgilityHandler().lapProgress(c, 5, objectId);
				}
				// Wall 2
				else if (c.getAgilityHandler().hotSpot(c, 2538, 3553) && c.getAgilityHandler().agilityProgress[5]) {
					c.setMove(new int[][]{{2540, 3553}}, "EAST", SkillAnims.CLIMB_WALL, -1, 20, 50, 2540, 3553, 1, 35, 1, 0);
					c.getAgilityHandler().agilityProgress[6] = true;
					c.getAgilityHandler().lapProgress(c, 6, objectId);
				}
				// Wall 3 (Finish Lap)
				else if (c.getAgilityHandler().hotSpot(c, 2541, 3553) && c.getAgilityHandler().agilityProgress[6]) {
					c.setMove(new int[][]{{2543, 3553}}, "EAST", SkillAnims.CLIMB_WALL, -1, 20, 50, 2543, 3553, 1, 35, 1, 0);
					c.getAgilityHandler().lapFinished(c, 6, 154, 4000);
					//c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.BARBARIAN_AGILITY);
				}
				// Fallback: If clicked without lap progress, just hop over naturally
				else {
					c.setMove(new int[][]{{c.getX() + 2, c.getY()}}, "EAST", SkillAnims.CLIMB_WALL, -1, 20, 50, c.getX() + 2, c.getY(), 1, 35, 1, 0);
				}
				return true;
		}
		return false;
	}
}