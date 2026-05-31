package server.model.players.skills.agility;

//import valius.content.achievement.AchievementType;
//import valius.content.achievement.Achievements;
import server.event.Event;
import server.world.World;
import server.model.players.Player;
import server.model.items.Item;
//import valius.model.entity.player.combat.Hitmark;
import server.model.players.skills.Skill;
import server.model.players.skills.agility.impl.BarbarianAgility;
import server.model.players.skills.agility.impl.GnomeAgility;
import server.model.players.skills.agility.impl.Lighthouse;
import server.model.players.skills.agility.impl.Shortcuts;
import server.model.players.skills.agility.impl.WildernessAgility;
import server.model.players.skills.agility.impl.rooftop.RooftopArdougne;
import server.model.players.skills.agility.impl.rooftop.RooftopSeers;
import server.model.players.skills.agility.impl.rooftop.RooftopVarrock;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.pets.PetHandler.SkillPets;
//import valius.model.items.ItemUtility;
import server.util.Misc;

/**
 * AgilityHandler
 * * @author Andrew (I'm A Boss on Rune-Server and Mr Extremez on Mopar & Runelocus)
 * @editor Modernized with Event Architecture
 */
public class AgilityHandler {

	public boolean[] agilityProgress = new boolean[8];
	public boolean[][] RoofAgilityProgress = new boolean[9][8];
	public int lapBonus = 0;

	/**
	 * The last click with an object a player has made
	 */
	private long lastClick;

	/**
	 * The delay that is required in between object clicks
	 */
	private static final long OBJECT_CLICK_DELAY = 1_500L;

	public static final int LOG_EMOTE = 762,
			PIPES_EMOTE = 844,
			CLIMB_UP_EMOTE = 828,
			CLIMB_DOWN_EMOTE = 827,
			CLIMB_UP_MONKEY_EMOTE = 3487,
			WALL_EMOTE = 840,
			JUMP_EMOTE = 3067,
			FAIL_EMOTE = 770,
			CRAWL_EMOTE = 844,
			TUNNEL_1 = 2589,
			TUNNEL_2 = 2590,
			TUNNEL_3 = 2591;

	public int jumping, jumpingTimer = 0, agilityTimer = -1, moveHeight = -1, tropicalTreeUpdate = -1, zipLine = -1;

	public boolean barbarianRope = false, barbarianLog = false, barbarianNet = false, barbarianStairs = false, barbarianWallOne = false, barbarianWallTwo = false,
			barbarianWallThree = false;

	private int moveX, moveY, moveH;

	public void resetAgilityProgress() {
		for (int i = 0; i < 8; i++) {
			agilityProgress[i] = false;
		}
		for (int io = 0; io < 9; io++) {
			for (int i = 0; i < 8; i++) {
				RoofAgilityProgress[io][i] = false;
			}
		}
		lapBonus = 0;
	}

	/**
	 * Sets a specific emote to walk with
	 */
	public void walkToEmote(Player c, int id) {
		c.isRunning2 = false;
		c.playerWalkIndex = id;
		c.getPA().requestUpdates();
	}

	/**
	 * Resets the player animation and movement state
	 */
	public static void stopEmote(Player player) {
		player.isRunning2 = true;
		player.playerStandIndex = 0x328;
		player.playerTurnIndex = 0x337;
		player.playerWalkIndex = 0x333;
		player.playerTurn180Index = 0x334;
		player.playerTurn90CWIndex = 0x335;
		player.playerTurn90CCWIndex = 0x336;
		player.playerRunIndex = 0x338;
		if (player.playerEquipment[player.playerWeapon] != -1) {
			player.getCombat().getPlayerAnimIndex(player.getItems().getItemName(player.playerEquipment[player.playerWeapon]).toLowerCase());
		}
		player.resetWalkingQueue();
		player.getPA().requestUpdates();
	}

	/**
	 * Initiates a forced walk event, locking the player until they reach the destination
	 */
	public void move(Player c, int EndX, int EndY, int Emote, int endingAnimation) {
		if (c.getItems().isWearingItem(4084)) {
			c.sendMessage("It would seem to be dangerous doing this.. on a sled.. right?");
			return;
		}
		c.getPlayerAction().setAction(true);
		walkToEmote(c, Emote);
		c.getMovementQueue().walkStep(EndX, EndY);
		destinationReached(c, EndX, EndY, endingAnimation);
	}

	/**
	 * Calculates the time it takes to walk to the destination and fires an Event to clean up
	 */
	public void destinationReached(final Player c, int x2, int y2, final int endingEmote) {
		int delay = 0;
		if (x2 >= 0 && y2 >= 0 && x2 != y2) delay = x2 + y2;
		else if (x2 == y2) delay = x2;
		else if (x2 < 0) delay = -x2 + y2;
		else if (y2 < 0) delay = x2 - y2;

		World.getWorld().getEventHandler().submit(new Event<Player>("agility_walk", c, delay) {
			@Override
			public void execute() {
				if (attachment.disconnected) {
					super.stop();
					return;
				}
				if (moveHeight >= 0) {
					attachment.getPA().movePlayer(attachment.getX(), attachment.getY(), moveHeight);
					moveHeight = -1;
				}
				stopEmote(attachment);
				if (endingEmote != -1) {
					attachment.startAnimation(endingEmote);
				}
				attachment.getPlayerAction().setAction(false);
				super.stop();
			}

			@Override
			public void stop() {
				if (attachment != null && !attachment.disconnected) {
					stopEmote(attachment);
					attachment.getPlayerAction().setAction(false);
				}
				super.stop();
			}
		});
	}

	public double getXp(int objectId) {
		switch (objectId) {
			case RooftopVarrock.ROUGH_WALL: return 13;
			case RooftopVarrock.CLOTHES_LINE: return 23;
			case RooftopVarrock.LEAP_GAP: return 19;
			case RooftopVarrock.BALANCE_WALL_JUMP: return 28;
			case RooftopVarrock.LEAP_2ND_GAP: return 10;
			case RooftopVarrock.LEAP_3RD_GAP: return 24;
			case RooftopVarrock.LEAP_4TH_GAP: return 5;
			case RooftopVarrock.HURDLE_LEDGE: return 4;
			case RooftopVarrock.JUMP_OFF_EDGE: return 144;
			case GnomeAgility.TREE_OBJECT:
			case GnomeAgility.TREE_BRANCH_OBJECT: return 5;
			case GnomeAgility.LOG_OBJECT:
			case GnomeAgility.PIPES1_OBJECT:
			case GnomeAgility.PIPES2_OBJECT:
			case GnomeAgility.NET2_OBJECT:
			case GnomeAgility.NET1_OBJECT:
			case GnomeAgility.ROPE_OBJECT: return 7.5;
			case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT:
			case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
			case BarbarianAgility.BARBARIAN_NET_OBJECT:
			case BarbarianAgility.BARBARIAN_LEDGE_OBJECT:
			case BarbarianAgility.BARBARIAN_LADDER_OBJECT:
			case BarbarianAgility.BARBARIAN_WALL_OBJECT: return 14;
			case WildernessAgility.WILDERNESS_PIPE_OBJECT: return 12;
			case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
			case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
			case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT: return 20;
			case WildernessAgility.WILDERNESS_ROCKS_OBJECT: return 0;
		}
		return -1;
	}

	private int getLevelRequired(int objectId) {
		switch (objectId) {
			case WildernessAgility.WILDERNESS_PIPE_OBJECT:
			case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
			case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT:
			case WildernessAgility.WILDERNESS_ROCKS_OBJECT:
			case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT: return 52;
			case Lighthouse.BASALT_ROCK: return 40;
			case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT:
			case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
			case BarbarianAgility.BARBARIAN_NET_OBJECT:
			case BarbarianAgility.BARBARIAN_LEDGE_OBJECT:
			case BarbarianAgility.BARBARIAN_LADDER_OBJECT:
			case BarbarianAgility.BARBARIAN_WALL_OBJECT: return 35;
			case RooftopSeers.WALL: return 60;
			case RooftopVarrock.ROUGH_WALL: return 30;
			case RooftopArdougne.WOODEN_BEAMS: return 90;
			case Shortcuts.SLAYER_TOWER_CHAIN_UP: return 61;
			case Shortcuts.RELLEKKA_STRANGE_FLOOR: return 81;
			case Shortcuts.RELLEKKA_CREVICE: return 62;
			case Shortcuts.STEPPING_STONE: return 90;
		}
		return -1;
	}

	public int getAnimation(int objectId) {
		switch (objectId) {
			case GnomeAgility.LOG_OBJECT:
			case WildernessAgility.WILDERNESS_LOG_BALANCE_OBJECT:
			case BarbarianAgility.BARBARIAN_LOG_BALANCE_OBJECT:
			case GnomeAgility.ROPE_OBJECT:
			case 2332: return LOG_EMOTE;
			case 154:
			case 4084:
			case 9330:
			case 9228:
			case 5100:
			case WildernessAgility.WILDERNESS_PIPE_OBJECT: return PIPES_EMOTE;
			case WildernessAgility.WILDERNESS_SWING_ROPE_OBJECT:
			case BarbarianAgility.BARBARIAN_SWING_ROPE_OBJECT: return 3067;
			case WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT: return 1604;
			case WildernessAgility.WILDERNESS_ROCKS_OBJECT: return 1148;
			case BarbarianAgility.BARBARIAN_LEDGE_OBJECT: return 756;
			case BarbarianAgility.BARBARIAN_WALL_OBJECT: return 839;
		}
		return -1;
	}

	public static void delayFade(final Player c, String emote, final int moveX, final int moveY, final int moveH, String message, String endMessage, int time) {
		if (emote.equals("CLIMB_DOWN")) c.startAnimation(CLIMB_DOWN_EMOTE);
		if (emote.equals("CLIMB_UP")) c.startAnimation(CLIMB_UP_EMOTE);
		if (emote.equals("JUMP")) c.startAnimation(JUMP_EMOTE);
		if (emote.equals("FAIL")) c.startAnimation(FAIL_EMOTE);
		if (emote.equals("CRAWL")) c.startAnimation(CRAWL_EMOTE);

		c.getPlayerAction().setAction(true);
		c.getMovementQueue().setBlockMovement(true);
		c.sendMessage(message);

		World.getWorld().getEventHandler().submit(new Event<Player>("agility_fade", c, time + 2) {
			@Override
			public void execute() {
				if (attachment.disconnected) { stop(); return; }
				attachment.getPlayerAction().setAction(false);
				attachment.getMovementQueue().setBlockMovement(false);
				attachment.getPA().movePlayer(moveX, moveY, moveH);
				attachment.sendMessage("..." + endMessage);
				super.stop();
			}
			@Override
			public void stop() {
				if (attachment != null && !attachment.disconnected) {
					attachment.getPlayerAction().setAction(false);
					attachment.getMovementQueue().setBlockMovement(false);
				}
				super.stop();
			}
		});
	}

	public static boolean failedObstacle = false;

	public static boolean failObstacle(final Player c, int x, int y, int z) {
		failedObstacle = false;
		int chance = 10 + c.getSkills().getLevel(Skill.AGILITY) / 13;
		if (Misc.random(chance) == 1) {
			failedObstacle = true;
			AgilityHandler.delayEmote(c, "FAIL", x, y, z, 2);
			c.faceUpdate(0);
			c.sendMessage("You slipped and hurt yourself.");
			c.getAgilityHandler().resetAgilityProgress();
			return true;
		}
		return false;
	}

	public static void delayEmote(final Player player, String emote, final int moveX, final int moveY, final int moveH, int time) {
		int endAnimation = -1;
		switch (emote) {
			case "OPEN_STRONGHOLD_DOOR": player.startAnimation(4282); break;
			case "CLIMB_DOWN": player.startAnimation(CLIMB_DOWN_EMOTE); break;
			case "CLIMB_UP": player.startAnimation(CLIMB_UP_EMOTE); break;
			case "FAIL": player.startAnimation(FAIL_EMOTE); break;
			case "JUMP": player.startAnimation(JUMP_EMOTE); break;
			case "TUNNEL_1": player.startAnimation(TUNNEL_1); endAnimation = TUNNEL_2; break;
			case "JUMP_GRAB": player.startAnimation(5039); break;
			case "BALANCE": player.startAnimation(756); break;
			case "HANG": player.startAnimation(3060); break;
			case "JUMP_DOWN": player.startAnimation(2586); break;
			case "CLIMB_UP_WALL": player.startAnimation(737); break;
			case "HANG_ON_POST": player.startAnimation(1119); break;
			case "PRAY": player.startAnimation(1651); break;
			case "CRAWL": player.startAnimation(CRAWL_EMOTE); break;
			case "GWDBoulder": player.startAnimation(6983); break;
		}

		final int end = endAnimation;
		player.getPlayerAction().setAction(true);
		player.getMovementQueue().setBlockMovement(true);

		World.getWorld().getEventHandler().submit(new Event<Player>("agility_delay_xyz", player, 2) {
			@Override
			public void execute() {
				if (attachment.disconnected) { stop(); return; }
				attachment.getPlayerAction().setAction(false);
				attachment.getMovementQueue().setBlockMovement(false);
				attachment.getPA().movePlayer(moveX, moveY, moveH);
				attachment.getAgilityHandler().stopEmote(attachment);

				if (end != -1) {
					attachment.startAnimation(TUNNEL_3);
				} else {
					attachment.startAnimation(-1);
				}
				super.stop();
			}
			@Override
			public void stop() {
				if (attachment != null && !attachment.disconnected) {
					attachment.getAgilityHandler().stopEmote(attachment);
					attachment.getPlayerAction().setAction(false);
					attachment.getMovementQueue().setBlockMovement(false);
				}
				super.stop();
			}
		});
	}

	public static void delayEmote(final Player player, String emote, final int walkX, final int walkY, int time) {
		int anim = 0x333;
		switch (emote) {
			case "CLIMB_DOWN": player.startAnimation(CLIMB_DOWN_EMOTE); break;
			case "CLIMB_UP": player.startAnimation(CLIMB_UP_EMOTE); break;
			case "FAIL": player.startAnimation(FAIL_EMOTE); break;
			case "JUMP": player.startAnimation(JUMP_EMOTE); break;
			case "JUMP_GRAB": player.startAnimation(5039); break;
			case "BALANCE": player.startAnimation(752); anim = 754; break;
			case "HANG": player.startAnimation(3060); break;
			case "JUMP_DOWN": player.startAnimation(2586); break;
			case "CLIMB_UP_WALL": player.startAnimation(737); break;
			case "HANG_ON_POST": player.startAnimation(1119); break;
			case "PRAY": player.startAnimation(1651); break;
			case "WALL_EMOTE": anim = WALL_EMOTE; break;
			case "CRAWL": player.startAnimation(CRAWL_EMOTE); break;
			default: anim = 0x333; break;
		}

		final int yu = anim;
		player.isRunning = false;
		player.isRunning2 = false;
		player.getPlayerAction().setAction(true);
		player.postProcessing();
		player.getPA().walkTo(walkX, walkY);
		player.getAgilityHandler().walkToEmote(player, yu);

		World.getWorld().getEventHandler().submit(new Event<Player>("agility_walk_delay", player, 2) {
			@Override
			public void execute() {
				if (attachment.disconnected) { stop(); return; }
				attachment.getPlayerAction().setAction(false);
				attachment.getMovementQueue().setBlockMovement(false);
				attachment.playerWalkIndex = yu;
				attachment.playerRunIndex = yu;
				super.stop();
			}
			@Override
			public void stop() {
				if (attachment != null && !attachment.disconnected) {
					if (attachment.playerEquipment[attachment.playerWeapon] == -1) {
						attachment.isRunning2 = true;
					} else {
						attachment.getCombat().getPlayerAnimIndex(Item.getItemName(attachment.playerEquipment[attachment.playerWeapon]).toLowerCase());
					}
					attachment.getPlayerAction().setAction(false);
					attachment.getMovementQueue().setBlockMovement(false);
				}
				super.stop();
			}
		});
	}

	public static void delayEmote(final Player c, int emote, final int moveX, final int moveY, final int moveH, int time) {
		c.startAnimation(emote);
		c.getPlayerAction().setAction(true);
		c.getMovementQueue().setBlockMovement(true);

		World.getWorld().getEventHandler().submit(new Event<Player>("agility_int_delay", c, time) {
			@Override
			public void execute() {
				if (attachment.disconnected) { stop(); return; }
				attachment.getPlayerAction().setAction(false);
				attachment.getMovementQueue().setBlockMovement(false);
				attachment.getPA().movePlayer(moveX, moveY, moveH);
				attachment.getAgilityHandler().stopEmote(attachment);
				attachment.startAnimation(-1);
				super.stop();
			}
			@Override
			public void stop() {
				if (attachment != null && !attachment.disconnected) {
					attachment.getAgilityHandler().stopEmote(attachment);
					attachment.getPlayerAction().setAction(false);
					attachment.getMovementQueue().setBlockMovement(false);
				}
				super.stop();
			}
		});
	}

	public boolean hotSpot(Player c, int hotX, int hotY) {
		return c.getX() == hotX && c.getY() == hotY;
	}

	public void lapProgress(Player c, int progress, int obj) {
		if (System.currentTimeMillis() - lastClick < OBJECT_CLICK_DELAY) {
			return;
		}
		if(agilityProgress[progress]) {
			double exp = getXp(obj) * 5;
			c.getSkills().addExperience((int)exp, Skill.AGILITY);
			lastClick = System.currentTimeMillis();
		}
	}

	public void lapProgress(Player c, int progress, int obj, int area) {
		if (System.currentTimeMillis() - lastClick < OBJECT_CLICK_DELAY) {
			return;
		} else if(agilityProgress[progress]) {
			double exp = getXp(obj) * 5;
			c.getSkills().addExperience((int)exp, Skill.AGILITY);
			lastClick = System.currentTimeMillis();
		} else if(RoofAgilityProgress[area][progress]) {
			double exp = getXp(obj) * 5;
			c.getSkills().addExperience((int)exp, Skill.AGILITY);
			lastClick = System.currentTimeMillis();
		}
	}


	public void lapFinished(Player c, int progress, int experience, int petChance) {
		if (agilityProgress[progress]) {
			resetAgilityProgress();
			c.sendMessage("You received some XP for completing the track!");
			c.getSkills().addExperience(experience, Skill.AGILITY);
			if (Misc.random(10) == 0) {
				int sPoints = Misc.random(10);
				c.sendMessage("@pur@You receive " + sPoints + " Skill Points.");
			}
			if (Misc.random(petChance) == 1 && !SkillPets.AGILITY.hasPet(c)) {
				PetHandler.skillPet(c, SkillPets.AGILITY);
			}
		} else {
			c.sendMessage("You must complete the full course to gain experience.");
		}
	}

	public void roofTopFinished(Player c, int progress, int experience, int petChance, int area) {
		if (agilityProgress[progress] || RoofAgilityProgress[area][progress]) {
			resetAgilityProgress();
			c.sendMessage("You received some XP for completing the track!");
			c.getSkills().addExperience(experience, Skill.AGILITY);
			if (Misc.random(10) == 0) {
				int sPoints = Misc.random(10);
				c.sendMessage("@pur@You receive " + sPoints + " Skill Points.");
			}
			if (Misc.random(petChance) == 1 && !SkillPets.AGILITY.hasPet(c)) {
				PetHandler.skillPet(c, SkillPets.AGILITY);
			}
		} else {
			c.sendMessage("You must complete the full course to gain experience.");
		}
	}

	public void agilityProcess(Player c) {
		if (jumping > 0 && jumpingTimer == 0) {
			move(c, -1, 0, getAnimation(WildernessAgility.WILDERNESS_STEPPING_STONE_OBJECT), -1);
			jumping--;
			jumpingTimer = 2;
		}

		if (jumpingTimer > 0) {
			jumpingTimer--;
		}

		if (hotSpot(c, 3215, 3399)) {
			c.setMove(new int[][]{{3218, 3399}}, "EAST", JUMP_EMOTE, -1, 30, 60, 3218, 3399, 3, 1, 1, 3);
		}
		if (hotSpot(c, 3211, 3414)) {
			c.setMove(new int[][]{{3208, 3414}}, "WEST", 741, -1, 15, 30, 3208, 3414, 1, 1, 1, c.getHeight());
		}
		if (hotSpot(c, 3198, 3416)) {
			c.setMove(new int[][]{{3197, 3416}}, "WEST", 2588, -1, 0, 20, 3197, 3416, 2, 1, 1, 1);
		}
		if (hotSpot(c, 3253, 3180)) {
			delayEmote(c, "JUMP", 3259, 3179, 0, 2);
		}
// =========================================================
		// VARROCK ROOFTOP: BALANCE WALL JUMP (Chain Reaction)
		// =========================================================

		// Step 1: End of Run-up -> Leap to Wall
		if (hotSpot(c, 3193, 3416)) {
			c.setMove(new int[][]{{3190, 3414}}, "WEST", 2583, -1, 30, 60, 3190, 3414, 1, 1, 1, 1);
		}

		// Step 2: Land on wall -> Start slow shimmy
		else if (hotSpot(c, 3190, 3414)) {
			c.setMove(new int[][]{{3190, 3413}, {3190, 3411}}, "WEST", 1122, -1, 30, 60, 3190, 3411, 1, 1, 1, 1);
		}

		// Step 3: End of shimmy -> The Reach (Anim 1124)
		else if (hotSpot(c, 3190, 3411)) {
			c.setMove(new int[][]{{3190, 3410}}, "WEST", 1124, -1, 30, 60, 3190, 3410, 1, 1, 1, 1);
		}

		// Step 4: After reach -> Final slide down the edge
		else if (hotSpot(c, 3190, 3410)) {
			c.setMove(new int[][]{{3190, 3407}}, "SOUTH", 756, -1, 30, 60, 3190, 3407, 1, 1, 1, 1);
		}

		// Step 5: End of slide -> Turn & Leap to final roof
		else if (hotSpot(c, 3190, 3407)) {
			c.turnPlayerTo(3192, 3405);
			c.setMove(new int[][]{{3192, 3405}}, "EAST", 3067, -1, 30, 60, 3192, 3405, 1, 1, 1, 3);

			// Finalize the obstacle progress
			c.getAgilityHandler().RoofAgilityProgress[2][3] = true;
			c.getAgilityHandler().lapProgress(c, 3, RooftopVarrock.BALANCE_WALL_JUMP, 2);
		}
		if (agilityTimer > 0) {
			agilityTimer--;
		}

		if (agilityTimer == 0) {
			c.getPA().movePlayer(moveX, moveY, moveH);
			moveX = -1;
			moveY = -1;
			moveH = 0;
			agilityTimer = -1;
		}
	}

	public boolean checkLevel(Player c, int objectId) {
		if (getLevelRequired(objectId) > c.getSkills().getActualLevel(Skill.AGILITY)) {
			c.sendMessage("You need an agility level of atleast " + getLevelRequired(objectId) + " to do this.");
			return true;
		}
		return false;
	}

	static int changeObjectTimer = 10;
	static int rndChance;
	static int newObjectX, newObjectY;
}