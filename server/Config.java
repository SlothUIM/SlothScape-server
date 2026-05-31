package server;
import server.world.Location;
public class Config {
	public static final boolean SERVER_DEBUG = false;
	public static boolean TUTORIAL_ISLAND = true;
	public static final String SERVER_NAME = "SlothScape";
	public static double CLIENT_VERSION = 1;
	public static int MESSAGE_DELAY = 6000;
	public static final int ITEM_LIMIT = 35000;
	public static final int MAXITEM_AMOUNT = Integer.MAX_VALUE;
	public static final int BANK_SIZE = 550;
	public static final int MAX_PLAYERS = 1024;
	public static final int CONNECTION_DELAY = 100;
	public static final int MAX_INCOMING_PACKETS_PER_CYCLE = 90; // From 70
	public static final int IPS_ALLOWED = 3; // how many ips are allowed
	public static final boolean WORLD_LIST_FIX = false;
	public static final int[] ITEM_SELLABLE = { 9789, 13239, 21295, 19667, 995, 996, 997, 998, 999, 1000, 995, 9927,19730, 12020, 6713, 13307, 12013, 12014, 12015, 12016, 20008, 776, 10071, 13258, 132259,
			13260, 13261, 10933, 10939, 10940, 10941, 10945, 13640, 13642, 13644, 13646, 5553, 5554, 5555, 5556, 5557,
			20704, 20706, 20708, 20710, 13667, 13669, 13671, 13673, 13675, 13677, 21061, 21064, 21067, 21070, 21073, 21076, 20760, 20659, 20659, 20661, 20663,
			20665, 20667, 20669, 20671, 20673, 20675, 20677, 20679, 20681, 20683, 20685, 20687, 20689, 20691, 13133,
			13134, 13135, 13136, 13274, 13275, 13276, 13262, 13092, 20005, 20017, 19841, 13323, 13324, 13325, 13326,
			19722, 12647, 19835, 1409, 1410, 13121, 13122, 13123, 13124, 13141, 13142, 13143, 13144, 13117, 13118,
			13119, 13120, 13129, 13130, 13131, 13132, 13125, 13126, 13127, 13128, 13137, 13138, 13139, 13140, 11136,
			11138, 11140, 13103, 13112, 13113, 13114, 13115, 13104, 13105, 13106, 13107, 13141, 13142, 13143, 13144,
			13108, 13109, 13110, 13111, 1555, 1556, 1557, 1558, 1559, 1560, 12646, 13177, 13178, 11995, 13579, 13580,
			13581, 13582, 13583, 13584, 13585, 13586, 13587, 13588, 13589, 13590, 13591, 13592, 13593, 13594, 13595,
			13596, 13597, 13598, 13599, 13600, 13601, 13602, 13603, 13604, 13605, 13606, 13607, 13609, 13610, 13611,
			13612, 13613, 13614, 13615, 13616, 13617, 13618, 13619, 13620, 13621, 13622, 13623, 13624, 13625, 13626,
			13627, 13628, 13629, 13630, 13631, 13632, 13633, 13634, 13635, 13636, 13648, 13649, 13650, 12773, 12774,
			12904, 2415, 2416, 2417, 13199, 13197, 12810, 12811, 12812, 12813, 12814, 12815, 11919, 12956, 12957, 12958,
			12959, 11907, 12899, 12926, 12931, 12006, 12853, 611, 13320, 13321, 13322, 13226, 12816, 11941, 12791, 5733,
			13225, 13247, 8014, 8015, 9958, 9959, 5509, 5510, 5512, 11849, 12840, 775, 12648, 13116, 13069, 13070,
			13072, 13073, 12921, 12954, 11865, 11864, 15573, 8135, 1050, 1051, 1044, 1045, 1046, 1047, 1048, 1049, 1052,
			3839, 3840, 3841, 3842, 3843, 3844, 8844, 8845, 8846, 8847, 8848, 8849, 8850, 10551, 10548, 6570, 7462,
			7461, 7460, 7459, 7458, 7457, 7456, 7455, 7454, 9748, 9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808, 9784,
			9799, 9805, 9781, 9796, 9793, 9775, 9772, 9778, 9787, 9811, 9766, 9749, 9755, 9752, 9770, 9758, 9761, 9764,
			9803, 9809, 9785, 9800, 9806, 9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 9767, 9747, 9753, 9750, 9768,
			9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 2639, 2641, 2643, 10802,
			10804, 10806, 12321, 12323, 12325, 6746, 19675, 21003, 21004, 21012, 33117, 33114, 21013, 21000, 21001, 21009, 21010, 21006, 21007,
			21015, 21016, 21018, 21019, 21021, 21022, 21024, 21025,12650, 12649, 12651, 12652, 12644, 12645,
			12643, 11995, 15568, 12653, 12655, 13178, 12646, 13179, 13177, 12921, 13181, 12816, 12647,299,21992 };
	public static final int[] ITEM_TRADEABLE = { 3842, 3844, 3840, 8844, 8845,
			8846, 8847, 8848, 8849, 8850, 10551, 6570, 7462, 7461, 7460, 7459,
			7458, 7457, 7456, 7455, 7454, 8839, 8840, 8842, 11663, 11664,
			11665, 10499, 9748, 9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808,
			9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772, 9778, 9787, 9811,
			9766, 9749, 9755, 9752, 9770, 9758, 9761, 9764, 9803, 9809, 9785,
			9800, 9806, 9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 9767,
			9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798,
			9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 10498 };
	public static final int[] DESTROYABLE_ITEMS = {12020, 11941, 11942, 13121, 13123, 13124, 12791, 12926, 10551,
			10499, 10498, 11679, 11753, 11754, 11755, 11756, 11757, 11758, 9096, 9097, 9098, 9099, 9100, 9101, 9102, 9103, 9104, 9084};
	/**
	 * Items that cannot be dropped.
	 */
	public static final int[] UNDROPPABLE_ITEMS = { 6713, 19841, 11941, 12791, 11919, 12956, 12957, 12958, 12959, 12899, 11907, 12432,
			12369, 12365, 12363, 6822, 6824, 6826, 6828, 6830, 6832, 6834, 6836, 6838, 6840, 6842,
			6844, 6846, 6848, 6850, 19675,9927,299 };
	/**
	 * Items that are listed as fun weapons for duelling.
	 */
	public static final int[] FUN_WEAPONS = { 4151, 5698, 1231, 1215, 5680 };
			public static int[][] brokenBarrows = { { 4708, 4860 }, { 4710, 4866 },
			{ 4712, 4872 }, { 4714, 4878 }, { 4716, 4884 }, { 4720, 4896 },
			{ 4718, 4890 }, { 4720, 4896 }, { 4722, 4902 }, { 4732, 4932 },
			{ 4734, 4938 }, { 4736, 4944 }, { 4738, 4950 }, { 4724, 4908 },
			{ 4726, 4914 }, { 4728, 4920 }, { 4730, 4926 }, { 4745, 4956 },
			{ 4747, 4926 }, { 4749, 4968 }, { 4751, 4994 }, { 4753, 4980 },
			{ 4755, 4986 }, { 4757, 4992 }, { 4759, 4998 } };
	public static final boolean ADMIN_CAN_TRADE = false; // can admins trade?
	public static final boolean ADMIN_DROP_ITEMS = false;
	public static final boolean ADMIN_CAN_SELL_ITEMS = false;
	public static final int START_LOCATION_X = 3094; // start here
	public static final int START_LOCATION_Y = 3107;//3094,3107
	public static final int RESPAWN_X = 3222; // when dead respawn here
	public static final int RESPAWN_Y = 3219;
	public static final int EDGE_RESPAWN_X = 3087; // when dead respawn here
	public static final int EDGE_RESPAWN_Y = 3500;
	public static final int FALLY_RESPAWN_X = 2973; // when dead respawn here
	public static final int FALLY_RESPAWN_Y = 3343;
	public static final int DUELING_RESPAWN_X = 3362;
	public static final int DUELING_RESPAWN_Y = 3263;
	public static final int RANDOM_DUELING_RESPAWN = 5;
	public static final int NO_TELEPORT_WILD_LEVEL = 20; 
	public static final int SKULL_TIMER = 1200; 
	public static final int TELEBLOCK_DELAY = 20000;
	public static final boolean SINGLE_AND_MULTI_ZONES = true;
	public static final boolean COMBAT_LEVEL_DIFFERENCE = true;
	public static final boolean itemRequirements = true;
	public static int MELEE_EXP_RATE = 1; // damage * exp rate
	public static int RANGE_EXP_RATE = 1;
	public static int MAGIC_EXP_RATE = 1;
	public static final double SERVER_EXP_BONUS = 1.0;
	/**
	 * Cycle time.
	 */
	public static final int CYCLE_TIME = 600;
	/**
	 * Buffer size.
	 */
	public static final int BUFFER_SIZE = 512;
	/**
	 * How fast the special attack bar refills.
	 */
	public static final int INCREASE_SPECIAL_AMOUNT = 31000;

	/**
	 * If you need more than one prayer point to use prayer.
	 */
	public static final boolean PRAYER_POINTS_REQUIRED = true;

	/**
	 * If you need a certain prayer level to use a certain prayer.
	 */
	public static final boolean PRAYER_LEVEL_REQUIRED = true;

	/**
	 * If you need a certain magic level to use a certain spell.
	 */
	public static final boolean MAGIC_LEVEL_REQUIRED = true;

	/**
	 * How long the god charge spell lasts.
	 */
	public static final int GOD_SPELL_CHARGE = 300000;

	/**
	 * If you need runes to use magic spells.
	 */
	public static final boolean RUNES_REQUIRED = true;

	/**
	 * If you need correct arrows to use with bows.
	 */
	public static final boolean CORRECT_ARROWS = true;

	/**
	 * If the crystal bow degrades.
	 */
	public static final boolean CRYSTAL_BOW_DEGRADES = true;
	

	/**
	 * How often the server saves data.
	 */
	public static final int SAVE_TIMER = 120; // save every 1 minute
	
	public static final int NPC_RANDOM_WALK_DISTANCE = 5;
	public static final int NPC_FOLLOW_DISTANCE = 10;
	public static final int[] UNDEAD_NPCS = { 90, 91, 92, 93, 94, 103, 104, 73,
			74, 75, 76, 77 };
	/**
	 * Barrows Reward
	 */
	/**
	 * Glory
	 */
	public static final int EDGEVILLE_X = 3087;
	public static final int EDGEVILLE_Y = 3500;
	public static final int AL_KHARID_X = 3293;
	public static final int AL_KHARID_Y = 3174;
	public static final int KARAMJA_X = 2814;
	public static final int KARAMJA_Y = 3182;
	public static final int MAGEBANK_X = 2538;
	public static final int MAGEBANK_Y = 4716;
	public static final int[] CAT_ITEMS 	= 	{
	1555,1556,1557,1558,1559,
	1560,1561,1562,1563,1564,
	1565,7585,7584,12653,12650,
	12651,12643,12644,12645,11995,
	13178,13247,12646};

	public static final int TOTAL_QUESTS = 20;
	
	/**
	*	Diary teleports
	**/
	//ardy cloak
	public static final int MONASTERYX = 2606;
	public static final int MONASTERYY = 3220;
	/**
	 * Teleport Spells
	 **/
	// modern
	public static final int VARROCK_X = 3211;
	public static final int VARROCK_Y = 3423;
	public static final int GRAND_EXCHANGE_X = 3163;
	public static final int GRAND_EXCHANGE_Y = 3478;
	public static final String VARROCK = "";
	public static final int LUMBY_X = 3222;
	public static final int LUMBY_Y = 3218;
	public static final String LUMBY = "";
	public static final int FALADOR_X = 2964;
	public static final int FALADOR_Y = 3378;
	public static final String FALADOR = "";
	public static final int CAMELOT_X = 2756;
	public static final int CAMELOT_Y = 3476;
	public static final int SEERS_X = 2724;
	public static final int SEERS_Y = 3484;
	public static final String CAMELOT = "";
	public static final int ARDOUGNE_X = 2662;
	public static final int ARDOUGNE_Y = 3305;
	public static final String ARDOUGNE = "";
	public static final int WATCHTOWER_X = 2549;
	public static final int WATCHTOWER_Y = 3113;
	public static final String WATCHTOWER = "";
	public static final int TROLLHEIM_X = 2891;
	public static final int TROLLHEIM_Y = 3678;
	public static final String TROLLHEIM = "";
	public static final int KOUREND_X = 1639;
	public static final int KOUREND_Y = 3673;
	public static final String KOUREND = "";
	public static final int FARMING_GUILD_X = 1248;
	public static final int FARMING_GUILD_Y = 3718;
	public static final int CATHERBY_FARM_X = 2816;
	public static final int CATHERBY_FARM_Y = 3460;
	public static final int ARDY_FARM_X = 2639;
	public static final int ARDY_FARM_Y = 3362;
	public static final int FALADOR_FARM_X = 3033;
	public static final int FALADOR_FARM_Y = 3286;
	// ancient	CATHERBY_PATCH
	public static final int PADDEWWA_X = 3098;
	public static final int PADDEWWA_Y = 9884;
	public static final int SENNTISTEN_X = 3322;
	public static final int SENNTISTEN_Y = 3336;
	public static final int KHARYRLL_X = 3492;
	public static final int KHARYRLL_Y = 3471;
	public static final int LASSAR_X = 3006;
	public static final int LASSAR_Y = 3471;
	public static final int DAREEYAK_X = 3161;
	public static final int DAREEYAK_Y = 3671;
	public static final int CARRALLANGAR_X = 3156;
	public static final int CARRALLANGAR_Y = 3666;
	public static final int ANNAKARL_X = 3288;
	public static final int ANNAKARL_Y = 3886;
	public static final int GHORROCK_X = 2977;
	public static final int GHORROCK_Y = 3873;
	public static final int TIMEOUT = 60;

	public static final int TAV_HOUSE_X = 2893;
	public static final int TAV_HOUSE_Y = 3463;
	public static final int DRAYNOR_X = 3080;
	public static final int DRAYNOR_Y = 3250;
	public static final int MOONCLAN_X = 2112;
	public static final int MOONCLAN_Y = 3904;
	public static final int BARBOUTPOST_X = 2543;
	public static final int BARBOUTPOST_Y = 3570;
	public static final int CASTLEWARS_X = 2440;
	public static final int CASTLEWARS_Y = 3087;
	public static final int BLAST_FURNACE_X = 2929;
	public static final int BLAST_FURNACE_Y = 10192;
	public static final int BURTHORPE_GAMES_ROOM_X = 2207;
	public static final int BURTHORPE_GAMES_ROOM_Y = 4959;
	/**
	 * Slayer Variables
	 */
	public static final int[][] SLAYER_TASKS = { 
			{ 1, 87, 90, 4, 5 },//low tasks
			{ 6, 7, 8, 9, 10 }, // med tasks
			{ 11, 12, 13, 14, 15 }, // high tasks
			{ 1, 1, 15, 20, 25 }, // low reqs
			{ 30, 35, 40, 45, 50 }, // med reqs
			{ 60, 75, 80, 85, 90 } }; // high reqs
	/**
	 * Skill Experience Multipliers
	 */
	public static final int WOODCUTTING_EXPERIENCE = 1;
	public static final int MINING_EXPERIENCE = 1;
	public static final int SMITHING_EXPERIENCE = 1;
	public static final int FARMING_EXPERIENCE = 1;
	public static final int FIREMAKING_EXPERIENCE = 1;
	public static final int HERBLORE_EXPERIENCE = 1;
	public static final int FISHING_EXPERIENCE = 1;
	public static final int AGILITY_EXPERIENCE = 1;
	public static final int PRAYER_EXPERIENCE = 1;
	public static final int RUNECRAFTING_EXPERIENCE = 1;
	public static final int CRAFTING_EXPERIENCE = 1;
	public static final int THIEVING_EXPERIENCE = 1;
	public static final int SLAYER_EXPERIENCE = 1;
	public static final int COOKING_EXPERIENCE = 1;
	public static final int FLETCHING_EXPERIENCE = 1;

	public static final String COMBAT_SCRIPT_DIR = "server.model.npcs.combat.impl";
	/*
	 * Items to not announce when dropped even though they are on an NPC's rare drop table.
	 */
	public static int[] notRare = new int[] {
			1739,//cowhide
			1740,//cowhide
			18,//Magic Gold Feather
			314,//Feather
			1583,//Fire Feather
			2950,//Golden Feather
			4621,//Phoenix Feather
			10087,//Stripy Feather
			44,//Rune Arrowtips
			892,//Rune Arrows
			11212,//Dragon Arrows
			11237,//Dragon Arrowtips
			21326,//Amethyst Arrow
			21350,//Amethyst Arrowtips
			1185,//Rune Sq Shield
			1187,//Dragon Sq Shield
			20152,//Gilded Sq Shield
			1347,//Rune Warhammer
			1373,//Rune Battleaxe
			405,//Casket
			442,//Silver Ore
			443,//Silver Ore
			1247,//Rune Spear
			554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566 //runes
			
	};

	public static boolean theatreDisabled;
	
	public static boolean includes(int id) {
		for (int index : notRare) {
			if (index == id)
				return true;
		}
		return false;
	}
	/**
	 * NPCs that are of the undead kind
	 */
	public static final int[] UNDEAD_IDS = { 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
			45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71,
			72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 120,
			130, 414, 448, 449, 450, 451, 452, 453, 454, 455, 456, 457, 717, 720, 721, 722, 723, 724, 725, 726, 727,
			728, 866, 867, 868, 869, 870, 871, 873, 874, 875, 876, 877, 880, 920, 924, 945, 946, 949, 950, 951, 952,
			953, 1538, 1539, 1541, 1685, 1686, 1687, 1688, 1784, 1785, 1786, 2501, 2502, 2503, 2504, 2505, 2506, 2507,
			2508, 2509, 2514, 2515, 2516, 2517, 2518, 2519, 2520, 2521, 2522, 2523, 2524, 2525, 2526, 2527, 2528, 2529,
			2530, 2531, 2532, 2533, 2534, 2992, 2993, 2999, 3516, 3565, 3584, 3617, 3625, 3969, 3970, 3971, 3972, 3973,
			3974, 3975, 3976, 3977, 3978, 3979, 3980, 3981, 4421, 5237, 5342, 5343, 5344, 5345, 5346, 5347, 5348, 5349,
			5350, 5351, 5370, 5506, 5507, 5568, 5571, 5574, 5583, 5622, 5623, 5624, 5625, 5626, 5627, 5633, 5647, 6441,
			6442, 6443, 6444, 6445, 6446, 6447, 6448, 6449, 6450, 6451, 6452, 6453, 6454, 6455, 6456, 6457, 6458, 6459,
			6460, 6461, 6462, 6463, 6464, 6465, 6466, 6467, 6468, 6596, 6597, 6598, 6608, 6611, 6612, 6740, 6741, 8062,
			7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940, 1672, 1673, 1674, 1675, 1676, 1677, 465, 466, 467, 468,
			7792, 7793, 7794, 7795, 5862, 5863};

	/*
	 * Corrupt monsters
	 */
	public static int[] CORRUPT_IDS = { 8250, 8338, 8339, 8340, 8341, 8356, 8357, 8359, 8360, 8361, 8362, 8363, 8364, 8365, 8366, 
			8369, 8370, 8371, 8372, 8373, 8374, 8375, 8387, 8388, 7540, 7541, 7542, 7545, 7550, 7551, 7552, 7553, 7554, 7555, 7561,
			7562, 7563, 7573, 7574, 7584, 7585, 7604, 7605, 7606, 7530, 7525, 7526, 7527, 7528, 7529, 7531, 7532, 2054, 6609, 6610,
			6611, 6612, 6615, 6618, 6619, 7858, 7859, 7860, 4005, 124, 415, 416, 5886, 5887, 5888, 5889, 5890, 5891, 7144, 7145, 7146, 
			7152, 3833, 3822};
	public static boolean superiorSlayerActivated;
	public static int[] CUT_ITEMS = {946};

	/**
	 * NPCs that represent demons for the Arclight
	 */
	public static final int[] DEMON_IDS = { 1531, 319, 3134, 2006, 2026, 7244, 1432, 415, 7410, 135, 3133, 484, 1619, 7276,
			3138, 7397, 7398, 11, 7278, 7144, 7145, 7146, 3129, 3132, 3130, 3131, 7286, 5890 };

	/*
	 * Revenant ID's
	 */
	public static final int[] REV_IDS = {
		7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940, 3372, 3373, 3374, 3375, 3386, 3377, 3378
	};
	public static final int PLAYERMODIFIER = 0;
	public static final int[] NOT_SHAREABLE = {};
	public static final int PACKET_SIZES[] = { 
			0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
			0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
			0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
			0, 0, 0, 0, 0, 11, 4, 0, 0, 2, // 30
			2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
			0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
			8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
			7, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
			0, 0, 0, 8, 0, 1, 4, 6, 0, 0, // 80
			0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
			0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
			0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
			1, 0, 6, 0, 0, 0, -1, -1, 2, 6, // 120
			0, 4, 7, 8, 4, 6, 0, 0, 6, 2, // 130
			0, 3, 0, 0, 0, 6, 0, 0, 0, 0, // 140
			0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
			0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
			0, 8, 0, 3, 0, 2, 0, 0, 8, 1, // 180
			0, 0, 15, 0, 0, 0, 0, 0, 0, 0, // 190
			2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
			4, 0, 0, 4, 7, 8, 0, 0, 12, 0, // 210
			0, 0, 0, 0, 0, 0, -1, 0, 7, 0, // 220
			1, 0, 0, 0, 7, 0, 6, 8, 1, 0, // 230
			0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
			0, 0, 7, 6, 0, 0, 0 // 250
	};
	public static final String CHARACTER_SAVE_DIRECTORY = "C:/Users/Administrator/Desktop/SlothScape Worlds/World Characters";
	public static final String CHARACTER_SAVE_DIRECTORY_BACKUP = "C:/Users/Administrator/Desktop/SlothScape Worlds/World Characters Back-up";
	public static final Location DEFAULT_POSITION = new Location(3041, 2855);
	/**
	 * Skill names and id's
	 */
	public static final String[] SKILL_NAME = { 
			"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", 
			"Crafting", "Smithing", "Mining","Herblore", "Agility", "Thieving", 
			"Slayer", "Farming", "Runecrafting", "Hunter" };

}