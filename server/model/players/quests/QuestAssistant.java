package server.model.players.quests;

import server.model.players.Player;
import server.Config;

/**
 * Quest Assistant
 * Handles the Quest Tab list, colors, and button clicks.
 * @author Andrew (Mr Extremez) & Gemini
 */

public class QuestAssistant {

	public static final int MAXIMUM_QUESTPOINTS = 26;

	public static void sendStages(Player c) {
		c.getPA().sendFrame126("Completed: " + c.QuestsCompleted + "/" + Config.TOTAL_QUESTS, 642);
		c.getPA().sendFrame126("Quest Points: " + c.questPoints + " ", 643);
		for (Quests quests : Quests.values()) {
			updateQuestListColor(c, quests);
		}
	}

	private static void updateQuestListColor(Player c, Quests quest) {
		// If the quest hasn't been migrated (ID -1), keep it black
		if (quest.getQuestId() == -1) {
			c.getPA().sendFrame126("@or1@" + quest.getName(), quest.getStringId());
			return;
		}

		int stage = c.questStages[quest.getQuestId()];
		String color = "";
		
		if (stage <= 0) {
			color = "@red@"; 
		} else if (stage >= quest.getFinishStage()) {
			color = "@gre@"; 
		} else {
			color = "@yel@"; 
		}
		
		c.getPA().sendFrame126(color + quest.getName(), quest.getStringId());
	}

	public enum Quests {
		// ==========================================
		// F2P MIGRATED QUESTS
		// ==========================================
		BLACK_KNIGHT(28164, 7332, "Black Knights' Fortress", BlackKnightsFortress.QUEST_ID, 3), 
		COOKS_ASSISTANT(28165, 7333, "Cook's Assistant", CooksAssistant.QUEST_ID, 3), 
		DORICS_QUEST(28168, 7336, "Doric's Quest", DoricsQuest.QUEST_ID, 3), 
		IMP_CATCHER(28172, 7340, "Imp Catcher", ImpCatcher.QUEST_ID, 2), 
		KNIGHTS_SWORD(28178, 7346, "The Knight's Sword", KnightsSword.QUEST_ID, 9), 
		PIRATES_TREASURE(28173, 7341, "Pirate's Treasure", PiratesTreasure.QUEST_ID, 6), 
		RESTLESS_GHOST(28169, 7337, "Restless Ghost", RestlessGhost.QUEST_ID, 5), 
		ROMEO_JULIET(28175, 7343, "Romeo & Juliet", RomeoJuliet.QUEST_ID, 9), 
		RUNE_MYSTERIES(28167, 7335, "Rune Mysteries", RuneMysteries.QUEST_ID, 4), 
		SHEEP_SHEARER(28176, 7344, "Sheep Shearer", SheepShearer.QUEST_ID, 2), 
		SHIELD_OF_ARRAV(28177, 7345, "Shield of Arrav", ShieldArrav.QUEST_ID, 8),
		VAMPYRE_SLAYER(28179, 7347, "Vampyre Slayer", VampyreSlayer.QUEST_ID, 5), 
		WITCHS_POTION(28180, 7348, "Witch's Potion", WitchsPotion.QUEST_ID, 3), 
		GERTRUDES_CAT(28192, 7360, "Gertrude's Cat", GertrudesCat.QUEST_ID, 7),
		LOST_CITY(28199, 7367, "Lost City", LostCity.QUEST_ID, 3), 
		ELEMENTAL(29035, 7459, "Elemental Workshop I", EleWorkShop.QUEST_ID, 4),

		// ==========================================
		// PLACEHOLDERS / PENDING MIGRATION
		// ==========================================
		DEMON_SLAYER(28166, 7334, "Demon Slayer", -1, 0),
		DRAGON_SLAYER(28215, 7383, "Dragon Slayer", -1, 0),
		ERNEST(28171, 7339, "Ernest the Chicken", -1, 0), 
		GOBLIN(28170, 7338, "Goblin Diplomacy", -1, 0), 
		PRINCE_RESCUE(28174, 7342, "Prince Ali Rescue", -1, 0), 
		BETWEEN_A_ROCK(49228, 12772, "Between A Rock", -1, 0), 
		CHOMPY(2161, 673, "Big Chompy Bird Hunting", -1, 0), 
		BIOHAZARD(28124, 7352, "Biohazard", -1, 0), 
		CABIN(68102, 17510, "Cabin Fever", -1, 0), 
		CLOCK(28185, 7353, "Clock Tower", -1, 0), 
		DEATH(32246, 8438, "Death Plateau", -1, 0), 
		CREATURE(47097, 12129, "Creature of Fenkenstrain", -1, 0), 
		DESERT_TREASURE(50052, 12852, "Desert Treasure", -1, 0), 
		DRUDIC_RITUAL(28187, 7355, "Drudic Ritual", -1, 0), 
		DWARF_CANNON(28188, 7356, "Dwarf Cannon", -1, 0), 
		EADGARS_RUSE(33231, 8679, "Eadgars Ruse", -1, 0), 
		DEVIOUS(61225, 15841, "Devious Minds", -1, 0), 
		DIGSITE(28186, 7354, "Digsite Quest", -1, 0), 
		ENAKHRA(63021, 16149, "Enakhra's Lamet", -1, 0), 
		FAIRY1(27075, 6987, "A Fairy Tale Pt. 1", -1, 0), 
		FAMILYCREST(28189, 7357, "Family Crest", -1, 0), 
		FEUD(50036, 12836, "The Feud", -1, 0), 
		FIGHT_ARENA(28190, 7358, "Fight Arena", -1, 0), 
		FISHING_CONTEST(28191, 7359, "Fishing Contest", -1, 0),
		FORGETTABLE_TABLE(50089, 14169, "Forgettable Tale...", -1, 0), 
		FREMMY_TRIALS(39131, 10115, "The Fremennik Trials", -1, 0), 
		GARDEN(57012, 14604, "Garden of Tranquillity", -1, 0),
		GHOSTS(47250, 12282, "Ghosts Ahoy", -1, 0), 
		GIANT_DWARF(53009, 13577, "The Giant Dwarf", -1, 0), 
		GOLEM(50039, 12839, "The Golem", -1, 0), 
		GRAND_TREE(28193, 7361, "The Grand Tree", -1, 0), 
		HAND_IN_THE_SAND(63000, 16128, "The Hand in the Sand", -1, 0), 
		HAUNTED_MINE(46081, 11857, "Haunted Mine", -1, 0), 
		HAZEEL(28194, 7362, "Hazeel Cult", -1, 0), 
		HEROES(28195, 7363, "Heroes Quest", -1, 0), 
		HOLY(28196, 7364, "Holy Grail", -1, 0), 
		HORROR(39151, 10135, "Horror from the Deep", -1, 0), 
		ITCHLARIN(17156, 4508, "Itchlarin's Little Helper", -1, 0), 
		AID_OF_MYREQUE(72085, 18517, "In Aid of the Myreque", -1, 0), 
		SEARCH_OF_MYREQUE(46131, 11907, "In Search of the Myreque", -1, 0), 
		JUNGLE_POTION(28197, 7365, "Jungle Potion", -1, 0), 
		LEGENDS_QUEST(28198, 7366, "Legends Quest", -1, 0), 
		LOST_TRIBE(52077, 13389, "The Lost Tribe", -1, 0), 
		MAKING_HISTORY(60127, 15487, "Making History", -1, 0), 
		MONKEY_MADNESS(43124, 11132, "Monkey Madness", -1, 0), 
		MERLINS_CRYSTAL(28200, 7368, "Merlins Crystal", MerlinsCrystal.QUEST_ID, MerlinsCrystal.COMPLETED),
		MONKS_FRIEND(28201, 7369, "Monks Friend", -1, 0), 
		MOUNTAIN_DAUGHTER(48101, 12389, "Mountain Daughter", -1, 0), 
		MOURNINGS_END_1(54150, 13974, "Mourning's Ends Part 1", -1, 0), 
		MOURNINGS_END_2(23139, 6027, "Mourning's Ends Part 2", -1, 0), 
		MURDER_MYSTERY(28202, 7370, "Murder Mystery", -1, 0), 
		NATURE_SPIRIT(31201, 8137, "Nature Spirit", -1, 0), 
		OBSERVATORY(28203, 7371, "Observatory Quest", -1, 0), 
		ONE_SMALL_FAVOUR(48057, 12345, "One Small Favour", -1, 0), 
		PLAGUE_CITY(28204, 7372, "Plague City", -1, 0), 
		PRIEST_IN_PERIL(31179, 8115, "Priest in Peril", -1, 0), 
		RAG_AND_BONE_MAN(72252, 18684, "Rag and Bone Man", -1, 0), 
		RAT_CATCHERS(60139, 15499, "Rat Catchers", -1, 0), 
		RECIPE(71130, 18306, "Recipe for Disaster", -1, 0), 
		RECRUITMENT_DRIVE(2156, 668, "Recruitment Drive", -1, 0), 
		REGICIDE(33128, 8576, "Regicide", -1, 0),
		ROVING_ELVES(47017, 12139, "Roving Elves", -1, 0), 
		RUM_DEAL(58064, 14912, "Rum Deal", -1, 0), 
		SCORPION_CATCHER(28205, 7373, "Scorpion Catcher", -1, 0), 
		SEA_SLUG(28206, 7374, "Sea Slug Quest", -1, 0), 
		SHADES_OF_MORTON(35009, 8969, "Shades of Mort'ton", -1, 0), 
		SHADOW_OF_THE_STORM(59248, 15352, "Shadow of the Storm", -1, 0), 
		SHEEP_HERDER(28207, 7375, "Sheep Herder", -1, 0), 
		SHILO_VILLAGE(28208, 7376, "Shilo Village", -1, 0), 
		SOULS_BANE(28250, 15098, "A Soul's Bane", -1, 0),
		SPIRITS_OF_THE_ELID(60232, 15592, "Spirits of The Elid", -1, 0), 
		SWAN_SONG(249, 249, "Swan Song", -1, 0), 
		TAI_BWO(6204, 1740, "Tai Bwo Wannai Trio", -1, 0), 
		TWO_CATS(59131, 15235, "A Tail of Two Cats", -1, 0), 
		TEARS_OF_GUTHIX(12206, 3278, "Tears of Guthix", -1, 0), 
		TEMPLE_OF_IKOV(28210, 7378, "Temple of Ikov", -1, 0), 
		THRONE_OF_MISCELLANIA(25118, 6518, "Throne of Miscellania", -1, 0), 
		TOURIST_TRAP(28211, 7379, "The Tourist Trap", -1, 0), 
		TREE_GNOME_VILLAGE(28212, 7380, "Tree Gnome Village", -1, 0), 
		TRIBAL_TOTEM(28213, 7381, "Tribal Totem", -1, 0), 
		TROLL_ROMANCE(46082, 11858, "Troll Romance", -1, 0), 
		TROLL_STRONGHOLD(191, 191, "Troll Stronghold", -1, 0), 
		UNDERGROUND_PASS(38199, 9927, "Underground Pass", -1, 0), 
		WANTED(23136, 6024, "Wanted", -1, 0), 
		WATCHTOWER(28181, 7349, "Watch Tower", -1, 0), 
		WATERFALL(28182, 7350, "Waterfall Quest", -1, 0),
		WITCH(28183, 7351, "Witch's House", -1, 0), 
		ZOGRE(52044, 13356, "Zogre Flesh Eaters", -1, 0);

		private final int button, string, questId, finishStage;
		private final String name;

		private Quests(final int button, final int string, final String name, final int questId, final int finishStage) {
			this.button = button;
			this.name = name;
			this.string = string;
			this.questId = questId;
			this.finishStage = finishStage;
		}
		
		public int getStringId() { return string; }
		public int getButton() { return button; }
		public String getName() { return name; }
		public int getQuestId() { return questId; }
		public int getFinishStage() { return finishStage; }

		public static Quests forButton(int button) {
			for (Quests q : Quests.values()) {
				if (q.getButton() == button) return q;
			}
			return null;
		}
	}

	public static void questButtons(Player player, int buttonId) {
		QuestManager quest = null;

		switch (buttonId) {
			case 28165: quest = new CooksAssistant(player); break;
			case 28169: quest = new RestlessGhost(player); break;
			case 28173: quest = new PiratesTreasure(player); break;
			case 28180: quest = new WitchsPotion(player); break;
			case 28179: quest = new VampyreSlayer(player); break;
			case 28175: quest = new RomeoJuliet(player); break;
			case 28168: quest = new DoricsQuest(player); break;
			case 28172: quest = new ImpCatcher(player); break;
			case 28167: quest = new RuneMysteries(player); break;
			case 28176: quest = new SheepShearer(player); break;
			case 28192: quest = new GertrudesCat(player); break;
			case 28178: quest = new KnightsSword(player); break;
			case 28164: quest = new BlackKnightsFortress(player); break;
			case 28177: quest = new ShieldArrav(player); break;
			case 28199: quest = new LostCity(player); break;
			case 29035: quest = new EleWorkShop(player); break;
			case 28200: quest = new MerlinsCrystal(player); break;
			default:
				Quests q = Quests.forButton(buttonId);
				if (q != null) {
					player.sendMessage("The quest " + q.getName() + " is currently under development.");
				}
				return;
		}
		
		if (quest != null) {
			quest.showQuestScroll(player);
		}
	}
}