package server.model.players.skills.agility.impl;

//import valius.content.achievement_diary.ardougne.ArdougneDiaryEntry;
//import valius.content.achievement_diary.falador.FaladorDiaryEntry;
//import valius.content.achievement_diary.kandarin.KandarinDiaryEntry;
//import valius.content.achievement_diary.morytania.MorytaniaDiaryEntry;
//import valius.content.achievement_diary.wilderness.WildernessDiaryEntry;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.Client;
import server.model.players.skills.Skill;
import server.model.players.skills.SkillAnims;
import server.model.players.skills.agility.AgilityHandler;
import server.util.Misc;

/**
 * Agility Shortcuts
 * @editor Modernized using Player.setMove() Engine
 */
public class Shortcuts {

	public static final int SLAYER_TOWER_CHAIN_UP = 16537, SLAYER_TOWER_CHAIN_DOWN = 16538,
			RELLEKKA_STRANGE_FLOOR = 16544, RELLEKKA_CREVICE = 16539, STEPPING_STONE = 16466, ARDOUGNE_LOG = 16548;

	public boolean agilityShortcuts(final Player c, final int objectId) {
		int x = c.getX();
		int y = c.getY();
		int z = c.getHeight();

		switch (objectId) {

			// ==========================================
			// MULTI-TILE WALL CRAWLS
			// ==========================================
			case 16528:
				// if (x == 2948 && y == 3313) // Example setup for future wall crawl
				break;

			case 16527:
				if (x == 2948 && y == 3309) {
					int[][] wallPath = { {3033, 3390}, {3033, 3391}, {3033, 3392}, {3033, 3393} };
					c.setWallCrawl(wallPath, "NORTH");
				}
				break;

			case 16520: // Yanille Wall Shortcut (South)
				if (x == 2575 && y == 3112) {
					int[][] wallPath = { {c.getX(), 3111}, {c.getX(), 3110}, {c.getX(), 3108}, {c.getX(), 3107} };
					c.setWallCrawl(wallPath, "SOUTH");
				}
				break;

			case 16519: // Yanille Wall Shortcut (North)
				if (x == 2575 && y == 3107) {
					int[][] wallPath = { {c.getX(), 3108}, {c.getX(), 3109}, {c.getX(), 3111}, {c.getX(), 3112} };
					c.setWallCrawl(wallPath, "NORTH");
				}
				break;

			// ==========================================
			// STRAIGHT-LINE FORCE MOVEMENT SHORTCUTS
			// ==========================================
			case 4469: // Falador Crumbling Wall
				if (x == 3089 && y >= 3533 && y <= 3534)
					c.setMove(new int[][]{{x + 1, y}}, "EAST", SkillAnims.CLIMB_WALL, -1, 20, 50, -1, -1, 1, 1, 1, z);
				else if (x == 3090 && y >= 3533 && y <= 3534)
					c.setMove(new int[][]{{x - 1, y}}, "WEST", SkillAnims.CLIMB_WALL, -1, 20, 50, -1, -1, 1, 1, 1, z);
				else if ((x >= 3093 && x <= 3094 || x >= 3103 && x <= 3104 || x >= 3085 && x <= 3086 || x >= 3075 && x <= 3076) && y == 3536)
					c.setMove(new int[][]{{x, y + 1}}, "NORTH", SkillAnims.CLIMB_WALL, -1, 20, 50, -1, -1, 1, 1, 1, z);
				else if ((x >= 3093 && x <= 3094 || x >= 3103 && x <= 3104 || x >= 3085 && x <= 3086 || x >= 3075 && x <= 3076) && y == 3537)
					c.setMove(new int[][]{{x, y - 1}}, "SOUTH", SkillAnims.CLIMB_WALL, -1, 20, 50, -1, -1, 1, 1, 1, z);
				return true;

			case 16510: // Taverley Spiked Blades
				if (c.getSkills().getLevel(Skill.AGILITY) < 80) {
					c.sendMessage("You must have an Agility level of at least 80 to cross here.");
					return false;
				}
				if (x == 2880 && y == 9813)
					c.setMove(new int[][]{{x - 2, y}}, "WEST", 3067, -1, 20, 45, -1, -1, 1, 80, 80, z);
				else if (x == 2878 && y == 9813)
					c.setMove(new int[][]{{x + 2, y}}, "EAST", 3067, -1, 20, 45, -1, -1, 1, 80, 80, z);
				return true;

			case 16509: // Taverley Obstacle Pipe
				if (c.getSkills().getLevel(Skill.AGILITY) < 70) {
					c.sendMessage("You must have an Agility level of at least 70 to squeeze-through the pipe.");
					return false;
				}
				if (x == 2886 && y == 9799)
					c.setMove(new int[][]{{x + 6, y}}, "EAST", SkillAnims.SQUEEZE_PIPE, -1, 10, 180, -1, -1, 1, 70, 70, z);
				else if (x == 2892 && y == 9799)
					c.setMove(new int[][]{{x - 6, y}}, "WEST", SkillAnims.SQUEEZE_PIPE, -1, 10, 180, -1, -1, 1, 70, 70, z);
				return true;

			case 23274: // Cross Balance Log
				if (c.getSkills().getLevel(Skill.AGILITY) < 20) {
					c.sendMessage("You must have an agility level of at least 20 to cross here.");
					return false;
				}
				if (c.getAgilityHandler().hotSpot(c, 2603, 3477))
					c.setMove(new int[][]{{2598, 3477}}, "WEST", 762, -1, 10, 150, -1, -1, 1, 20, 20, z);
				else if (c.getAgilityHandler().hotSpot(c, 2598, 3477))
					c.setMove(new int[][]{{2603, 3477}}, "EAST", 762, -1, 10, 150, -1, -1, 1, 20, 20, z);
				return true;

			case RELLEKKA_STRANGE_FLOOR:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (x == 2768) c.setMove(new int[][]{{x + 2, y}}, "EAST", 756, -1, 20, 60, -1, -1, 1, 81, 81, z);
				if (x == 2770) c.setMove(new int[][]{{x - 2, y}}, "WEST", 756, -1, 20, 60, -1, -1, 1, 81, 81, z);
				if (x == 2773) c.setMove(new int[][]{{x + 2, y}}, "EAST", 756, -1, 20, 60, -1, -1, 1, 81, 81, z);
				if (x == 2775) c.setMove(new int[][]{{x - 2, y}}, "WEST", 756, -1, 20, 60, -1, -1, 1, 81, 81, z);
				return true;

			case RELLEKKA_CREVICE:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (x <= 2730) c.setMove(new int[][]{{x + 5, y}}, "EAST", SkillAnims.SQUEEZE_CREVICE, -1, 20, 150, -1, -1, 1, 62, 62, z);
				else c.setMove(new int[][]{{x - 5, y}}, "WEST", SkillAnims.SQUEEZE_CREVICE, -1, 20, 150, -1, -1, 1, 62, 62, z);
				return true;

			case STEPPING_STONE:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (y < 2972) c.setMove(new int[][]{{2863, 2976}}, "NORTH", SkillAnims.STEPPING_STONE, -1, 20, 50, -1, -1, 1, 90, 90, z);
				else c.setMove(new int[][]{{2863, 2971}}, "SOUTH", SkillAnims.STEPPING_STONE, -1, 20, 50, -1, -1, 1, 90, 90, z);
				return true;

			case 29729:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (x <= 1610) c.setMove(new int[][]{{1614, 3570}}, "EAST", 3067, -1, 20, 50, -1, -1, 1, 1, 1, z);
				else c.setMove(new int[][]{{1610, 3570}}, "WEST", 3067, -1, 20, 50, -1, -1, 1, 1, 1, z);
				return true;

			case 29730:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (x <= 1603) c.setMove(new int[][]{{1607, 3571}}, "EAST", 3067, -1, 20, 50, -1, -1, 1, 1, 1, z);
				else c.setMove(new int[][]{{1603, 3571}}, "WEST", 3067, -1, 20, 50, -1, -1, 1, 1, 1, z);
				return true;

			case 10663: // Stepping stone near Zul-Andra
				int[][] stonePath = { {2157, 3072}, {2160, 3072} };
				int[][] stonePath2 = { {2157, 3072}, {2154, 3072} };
				if (x == 2160) c.setMove(stonePath2, "WEST", 3067, 2581, 30, 60, 3148, 3363, 2, 65, 99, z);
				else if (x == 2154) c.setMove(stonePath, "EAST", 3067, 2581, 30, 60, 3148, 3363, 2, 65, 99, z);
				break;

			case 16533: // Stepping stones between Varrock and Draynor Manor
				int[][] stonePath3 = { {x - 1, y} };
				int[][] stonePath4 = { {x + 1, y} };
				if (x > c.objectX) c.setMove(stonePath3, "WEST", 769, 2582, 30, 45, 3149, 3361, 2, 31, 35, z);
				if (x < c.objectX) c.setMove(stonePath4, "EAST", 769, 2581, 30, 45, 3149, 3361, 2, 31, 35, z);
				break;

			case ARDOUGNE_LOG:
			case 16546: // Ardy log other side
				if (c.getSkills().getLevel(Skill.AGILITY) < 32) {
					c.sendMessage("You must have an agility level of at least 32 to cross here.");
					return false;
				}
				int[][] logPath = { {2601, 3336}, {2598, 3336} };
				int[][] logPath2 = { {2599, 3336}, {2602, 3336} };
				if (c.getAgilityHandler().hotSpot(c, 2602, 3336))
					c.setMove(logPath, "WEST", 762, 2581, 15, 30, -1, -1, 2, 31, 35, z);
				else if (c.getAgilityHandler().hotSpot(c, 2598, 3336))
					c.setMove(logPath2, "EAST", 762, 2581, 15, 30, -1, -1, 2, 31, 35, z);
				break;

			// ==========================================
			// Z-AXIS ONLY SHORTCUTS (Climbs)
			// ==========================================
			case SLAYER_TOWER_CHAIN_UP:
				if (c.getAgilityHandler().checkLevel(c, objectId)) return false;

				if (z == 0 && y <= 3552) {
					if (c.getSkills().getLevel(Skill.AGILITY) < 60) {
						c.sendMessage("You need to have an agility level of at least 61 to climb this chain.");
						return false;
					}
					c.setMove(new int[][]{{3422, 3549}}, "NORTH", SkillAnims.CLIMB_UP, -1, 10, 40, -1, -1, 1, 60, 60, 1);
				} else if (z == 1 && y >= 3574) {
					if (c.getSkills().getLevel(Skill.AGILITY) < 70) {
						c.sendMessage("You need to have an agility level of at least 71 to climb this chain.");
						return false;
					}
					c.setMove(new int[][]{{3447, 3575}}, "NORTH", SkillAnims.CLIMB_UP, -1, 10, 40, -1, -1, 1, 70, 70, 2);
				}
				return true;

			case SLAYER_TOWER_CHAIN_DOWN:
				if (z == 1 && y <= 3552) {
					c.setMove(new int[][]{{3422, 3549}}, "SOUTH", 827, -1, 10, 40, -1, -1, 1, 1, 1, 0);
				} else if (z == 2 && y >= 3574) {
					c.setMove(new int[][]{{3447, 3575}}, "SOUTH", 827, -1, 10, 40, -1, -1, 1, 1, 1, 1);
				}
				return true;

			case 28857: // Redwood Climb Up
				c.setMove(new int[][]{{x > 1573 ? 1574 : 1567, y > 3490 ? 3493 : 3482}}, "NORTH", SkillAnims.CLIMB_UP, -1, 10, 40, -1, -1, 1, 1, 1, 1);
				return true;

			case 28858: // Redwood Climb Down
				c.setMove(new int[][]{{x > 1573 ? 1576 : 1565, y > 3490 ? 3493 : 3482}}, "SOUTH", 827, -1, 10, 40, -1, -1, 1, 1, 1, 0);
				return true;

			case 29681: // Redwood Climb Up (Upper)
				c.setMove(new int[][]{{x == 1570 ? 1570 : 1571, y > 3488 ? 3489 : 3486}}, "NORTH", SkillAnims.CLIMB_UP, -1, 10, 40, -1, -1, 1, 1, 1, 2);
				return true;

			case 29682: // Redwood Climb Down (Upper)
				c.setMove(new int[][]{{x == 1570 ? 1570 : 1571, y > 3488 ? 3489 : 3486}}, "SOUTH", 827, -1, 10, 40, -1, -1, 1, 1, 1, 1);
				return true;

			case 17050:
			case 17049: // Falador Grapple Wall
				if (c.getSkills().getLevel(Skill.AGILITY) < 11 || c.getSkills().getLevel(Skill.STRENGTH) < 37 || c.getSkills().getLevel(Skill.RANGED) < 19) {
					c.sendMessage("You must have a ranged level of 19, strength level of 37 and agility level of 11 to do this.");
					return false;
				}
				c.setMove(new int[][]{{3032, y == 3388 ? 3390 : 3388}}, "NORTH", SkillAnims.CLIMB_UP, -1, 10, 40, -1, -1, 1, 11, 11, 0);
				return true;


			// ==========================================
			// SCREEN-FADE TELEPORTS (Cave Entrances)
			// ==========================================
			case 20843:
				AgilityHandler.delayFade(c, "NONE", 3102, 3482, 0, "You enter the portal..", "and end up by the rowboat.", 3);
				return true;

			case 29082:
				c.startAnimation(2304);
				AgilityHandler.delayFade(c, "NONE", 2610, 4776, 0, "Abigail hits you while entering the boat..", "and you wake up in a random place.", 2);
				return true;

			case 26762: // Scorpia Entrance
				AgilityHandler.delayFade(c, "CRAWL", 3232, 10351, 0, "You crawl into the cavern..", "and end up at scorpia's cave.", 3);
				return true;

			case 26763: // Scorpia Exit
				AgilityHandler.delayFade(c, "CRAWL", 3233, 3950, 0, "You crawl out of scorpia's cave..", "and end up outside.", 3);
				return true;

			case 678: // Cave to Corp
				AgilityHandler.delayFade(c, "CRAWL", 2964, 4382, 2, "You crawl into the cave.", "and end up at corporeal beast lair.", 3);
				break;

			case 679: // Cave out from Corp
				AgilityHandler.delayFade(c, "CRAWL", 3206, 3681, 0, "You crawl into the cave.", "and end up outside corporeal beast lair.", 3);
				break;

			case 26567:
			case 26568: // Cerberus Cave Enter
			case 26569:
				AgilityHandler.delayFade(c, "CRAWL", 1310, 1237, 0, "You crawl into the cave", "and end up in a dark place.", 3);
				return true;

			case 26564:
			case 26565: // Cerberus Cave Exit
			case 26566:
				AgilityHandler.delayFade(c, "CRAWL", 2873, 9847, 0, "You crawl into the cave", "and end up on the outside.", 3);
				return true;

			case 537: // Kraken Cave
				c.getPA().movePlayer(2280, 10022, 0);
				return true;

			case 8729: // Wyvern top floor
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3060, 9558, 0, 2);
				return true;

			case 10596:
				AgilityHandler.delayEmote(c, "CRAWL", 3056, 9555, 0, 2);
				return true;

			case 10595:
				AgilityHandler.delayEmote(c, "CRAWL", 3056, 9562, 0, 2);
				return true;

			case 26766: // Wildy GWD Enter
				AgilityHandler.delayFade(c, "CRAWL", 3062, 10130, 0, "You crawl into the entrance", "And end up in the wilderness god wars dungeon..", 3);
				return true;

			case 26769: // Wildy GWD Exit
				AgilityHandler.delayFade(c, "CRAWL", 3017, 3740, 0, "You crawl into the crevice", "And end up outside the entrace..", 3);
				return true;
		}
		return false;
	}
}