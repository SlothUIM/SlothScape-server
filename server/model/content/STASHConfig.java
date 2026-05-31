package server.model.content;

public class STASHConfig {

    /* Old School RuneScape STASH Unit Item IDs */

    // Beginner Bush Items
    public static final int[] BEGINNER_BUSH_VARROCK_PALACE_ENTRANCE = {1361, 1169, 1641}; // Example items for Varrock palace entrance bush
    public static final int[] BEGINNER_BUSH_LUMBRIDGE_SWAMP = {2583, 1079, 1704}; // Example items for Lumbridge swamp
    public static final int[] BEGINNER_BUSH_DRAYNOR_MANOR = {1127, 1201, 1712}; // Example items for Draynor manor

    // Easy Bush Items
    public static final int[] EASY_BUSH_GRAND_EXCHANGE = {11978, 11862, 995}; // Spade, Cut Sapphire, Coins
    public static final int[] EASY_BUSH_VARROCK_PALACE = {11978, 1725, 1731}; // Spade, Amulet of power, Leather gloves
    public static final int[] EASY_BUSH_EDGEVILLE_MONASTERY = {1712, 1712, 1179}; // Amulet of glory, Dwarven helmet, Bronze axe
    public static final int[] EASY_BUSH_DRAYNOR_VILLAGE = {430, 2138, 1935}; // Raw salmon, Raw chicken, Jug
    public static final int[] EASY_BUSH_LUMBRIDGE_SWAMP = {960, 4819, 2347}; // Plank, Nails, Hammer
    public static final int[] EASY_BUSH_FALADOR_PARK = {2579, 2955, 882}; // White apron, Silk, Bronze arrow
    public static final int[] EASY_BUSH_VARROCK_SQUARE = {1040, 560, 995}; // Blue partyhat, Law rune, Coins
    public static final int[] EASY_BUSH_WIZARDS_TOWER = {1694, 1381, 579}; // Amulet of magic, Staff of air, Wizards hat
    public static final int[] EASY_BUSH_AL_KHARID_PALACE = {1731, 1153, 7947}; // Leather gloves, Iron helm, Baked potato
    public static final int[] EASY_BUSH_PORT_SARIM = {1351, 1931, 229}; // Bronze axe, Jug, Cadava potion

    // Medium Bush Items
    public static final int[] MEDIUM_BUSH_CATHERBY_BEACH = {11840, 6585, 6737}; // Example items for Catherby beach

    
    // Elite Bush Items
    public static final int[] ELITE_BUSH_TZHAAR_CITY = {11840, 6585, 6737}; // Example items for TzHaar city

    // Easy Hole Items
    public static final int[] EASY_HOLE_VARROCK_MINE = {2583, 1079, 1704}; // Example items for Varrock mine
    public static final int[] EASY_HOLE_RIMMINGTON = {1127, 1201, 1712}; // Example items for Rimmington

    // Medium Hole Items
    public static final int[] MEDIUM_HOLE_DIGSITE = {4131, 1333, 1725}; // Example items for Digsite

    
    // Elite Hole Items
    public static final int[] ELITE_HOLE_FREMENNIK_PROVINCE = {11840, 6585, 6737}; // Example items for Fremennik province

    // Easy Rock Items
    public static final int[] EASY_ROCK_BARBARIAN_VILLAGE = {2583, 1079, 1704}; // Example items for Barbarian Village

    // Medium Rock Items
    public static final int[] MEDIUM_ROCK_SHILO_VILLAGE = {1127, 1201, 1712}; // Example items for Shilo Village

    
    // Elite Rock Items
    public static final int[] ELITE_ROCK_ZULANDRA = {11840, 6585, 6737}; // Example items for Zul-Andra

    // Easy Crate Items
    public static final int[] EASY_CRATE_DRAYNOR_VILLAGE = {2583, 1079, 1704}; // Example items for Draynor Village

    // Medium Crate Items
    public static final int[] MEDIUM_CRATE_RELLEKKA = {1127, 1201, 1712}; // Example items for Rellekka

    
    // Elite Crate Items
    public static final int[] ELITE_CRATE_MENAPHOS = {11840, 6585, 6737}; // Example items for Menaphos

    /* Other Configuration Variables */

    /* STASH Taken and Stored States */
    public static boolean[][] taken = {
        new boolean[3], // beginner bush
        new boolean[10], // easy bush
        new boolean[7], // medium bush
        new boolean[0], // hard bush
        new boolean[2], // elite bush
        new boolean[7], // easy hole
        new boolean[4], // medium hole
        new boolean[5], // hard hole
        new boolean[4], // elite hole
        new boolean[7], // easy rock
        new boolean[4], // medium rock
        new boolean[4], // hard rock
        new boolean[5], // elite rock
        new boolean[6], // easy crate
        new boolean[8], // medium crate
        new boolean[6], // hard crate
        new boolean[7], // elite crate
    };

    public static boolean[][] Stored = {
        new boolean[3], // beginner bush
        new boolean[10], // easy bush
        new boolean[7], // medium bush
        new boolean[0], // hard bush
        new boolean[2], // elite bush
        new boolean[7], // easy hole
        new boolean[4], // medium hole
        new boolean[5], // hard hole
        new boolean[4], // elite hole
        new boolean[7], // easy rock
        new boolean[4], // medium rock
        new boolean[4], // hard rock
        new boolean[5], // elite rock
        new boolean[6], // easy crate
        new boolean[8], // medium crate
        new boolean[6], // hard crate
        new boolean[7], // elite crate
    };
	
    // Object IDs
    public static final int[] BEGINNER_BUSH = {15943, 34736};
    public static final int[] EASY_BUSH = {28983, 28984, 28985, 29005};
    public static final int[] MEDIUM_BUSH = {16018};
    public static final int[] HARD_BUSH = {};
    public static final int[] ELITE_BUSH = {28942};

    public static final int[] EASY_HOLE = {15944, 29038};
    public static final int[] MEDIUM_HOLE = {16020};
    public static final int[] HARD_HOLE = {28936};
    public static final int[] ELITE_HOLE = {28944};

    public static final int[] EASY_ROCK = {16014};
    public static final int[] MEDIUM_ROCK = {16022};
    public static final int[] HARD_ROCK = {28938};
    public static final int[] ELITE_ROCK = {28946};

    public static final int[] EASY_CRATE = {16016};
    public static final int[] MEDIUM_CRATE = {16024};
    public static final int[] HARD_CRATE = {28940};
    public static final int[] ELITE_CRATE = {28948};

    // Item requirements for building a stash
    public static final int HAMMER = 2347;
    public static final int NAILS = 4819;
    public static final int IRON_NAILS = 4820;
    public static final int STEEL_NAILS = 1539;
    public static final int PLANK = 960;
    public static final int OAK_PLANK = 8778;
    public static final int TEAK_PLANK = 8780;
    public static final int MAHOG_PLANK = 8782;

}
