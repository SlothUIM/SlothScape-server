package server.model.players.content.treasuretrails.tiers;

import server.model.players.Player;
import server.util.Misc;

public class BeginnerClue {

    public static final int CASKET_ID = 23245; // Beginner casket ID

    // =========================================================================
    // CLUE SCROLL IDs CATEGORIZED BY TYPE
    // As per OSRS, Beginners only have Cryptic, Emote, Map, and Hot/Cold clues.
    // =========================================================================

 // =========================================================================
    // CLUE SCROLL IDs CATEGORIZED BY TYPE (BEGINNER TIER)
    // =========================================================================

    public static final int[] CRYPTIC_CLUES = {
        // --- Anagrams ---
        23188, // Ranael
        23189, // Archmage Sedridor
        23190, // Apothecary
        23191, // Doric
        23192, // Brian
        23193, // Veronica
        23194, // Gertrude
        23195, // Hairdresser
        23196, // Fortunato
        
        // --- Cryptics ---
        23197, // Hans
        23198, // Cook
        23199, // Hunding
        23200, // Charlie the Tramp (Task)
        23201, // Shantay
        23202  // Reldo (Starts Hot/Cold)
    };
    public static final int[] ANAGRAM_CLUES = {
            23188, // Ranael
            23189, // Archmage Sedridor
            23190, // Apothecary
            23191, // Doric
            23192, // Brian
            23193, // Veronica
            23194, // Gertrude
            23195, // Hairdresser
            23196  // Fortunato
        };
    public static final int[] EMOTE_CLUES = {
        // --- Emote Clues ---
        23182, // Blow a raspberry at Aris
        23183, // Bow to Brugsen Bursen
        23184, // Cheer at Iffie Nitter
        23185, // Clap at Bob's Brilliant Axes
        23186, // Panic at Al Kharid mine
        23187  // Spin at Flynn's Mace Shop
    };

    public static final int[] MAP_CLUES = {
        // --- Map Clues (Digging spots) ---
        23203, // West of the Champions' Guild
        23204, // Standing stones north of Falador
        23205, // South of Draynor Village bank
        23206, // South-east Varrock mine
        23207  // Directly behind the Wizards' Tower
    };

    public static final int[] HOT_COLD_CLUES = {
        // --- Hot/Cold Clues ---
        // These are the specific clue IDs you receive AFTER talking to Reldo
        // and using the Strange Device. (Using placeholders 23208-23210 here)
        23208, 23209, 23210 
    };

    /**
     * Combines all categorized Beginner clue arrays into a single pool.
     * Used by the ClueDropManager to drop a random Beginner clue.
     */
    public static int[] getBeginnerCluePool() {
        int length = CRYPTIC_CLUES.length + EMOTE_CLUES.length + MAP_CLUES.length + HOT_COLD_CLUES.length + ANAGRAM_CLUES.length;
        int[] pool = new int[length];
        int pos = 0;
        
        for (int id : CRYPTIC_CLUES) { pool[pos++] = id; }
        for (int id : EMOTE_CLUES) { pool[pos++] = id; }
        for (int id : MAP_CLUES) { pool[pos++] = id; }
        for (int id : HOT_COLD_CLUES) { pool[pos++] = id; }
        for (int id : ANAGRAM_CLUES) { pool[pos++] = id; }
        
        return pool;
    }

    // =========================================================================
    // CASKET REWARD TABLES
    // =========================================================================

    // --- UNIQUES (15 items) ---
    private static final int[] UNIQUES = {
        23039, // Mole slippers
        23037, // Frog slippers
        23041, // Bear feet
        23043, // Demon feet
        23249, // Jester cape
        23252, // Shoulder parrot
        23270, // Monk's robe top (t)
        23273, // Monk's robe (t)
        23298, // Amulet of defence (t)
        23171, // Sandwich lady hat
        23174, // Sandwich lady top
        23177, // Sandwich lady bottom
        23330, // Rune scimitar ornament kit (guthix)
        23332, // Rune scimitar ornament kit (saradomin)
        23334  // Rune scimitar ornament kit (zamorak)
    };

    // --- BLACK ITEMS (18 items) ---
    private static final int[] BLACK_ITEMS = {
        1309, 1361, 1375, 314, 1217, 1165, 1195, 1301, 1424, 
        1143, 1223, 1125, 1089, 1077, 1179, 1327, 1281, 1339
    };

    // --- STANDARD ITEMS (40 items: [0]=ItemID, [1]=Min, [2]=Max) ---
    private static final int[][] STANDARD_ITEMS = {
        // Weapons & Armour
        {841, 1, 1}, {839, 1, 1}, {843, 1, 1}, {845, 1, 1}, {1265, 1, 1}, {1381, 1, 1}, 
        {1383, 1, 1}, {1385, 1, 1}, {1387, 1, 1}, {1167, 1, 1}, {1115, 1, 1}, {1067, 1, 1}, 
        {1081, 1, 1}, {1293, 1, 1}, {1205, 1, 1}, {1353, 1, 1}, {1365, 1, 1}, {1169, 1, 1}, 
        {1129, 1, 1}, {1095, 1, 1}, {1063, 1, 1}, {1131, 1, 1}, {1013, 1, 1}, {1011, 1, 1}, 
        {579, 1, 1}, {581, 1, 1},
        // Runes & Ammo
        {556, 15, 35}, {558, 15, 35}, {555, 15, 35}, {557, 15, 35}, {554, 15, 35}, {559, 15, 35}, 
        {562, 2, 7}, {561, 2, 9}, {563, 2, 7}, {882, 10, 25}, {884, 7, 25},
        // Food (Noted)
        {316, 5, 14}, {328, 5, 17}, {346, 5, 9}
    };

    public static void openCasket(Player c) {
        if (!c.getItems().playerHasItem(CASKET_ID)) {
            return;
        }

        if (c.getItems().freeSlots() < 3) {
            c.sendMessage("You need at least 3 free inventory slots to open this casket.");
            return;
        }

        c.getItems().deleteItem(CASKET_ID, 1);
        
        // Beginner clues give 1 to 3 rewards
        int totalRolls = Misc.random(1, 3);
        
        for (int i = 0; i < totalRolls; i++) {
            rollReward(c);
        }
        
        c.sendMessage("You open the casket and find some loot!");
    }

    private static void rollReward(Player c) {
        // Base roll of 492 aligns all fractions perfectly
        int roll = Misc.random(1, 492);

        // 1. Unique / Cabbage Sub-table (41/492 = exactly 1/12)
        if (roll <= 41) {
            if (Misc.random(1, 2) == 1) {
                // 50% chance for Cabbage (Noted)
                c.getItems().addItem(1968, Misc.random(5, 9)); 
            } else {
                // 50% chance for Unique
                int unique = UNIQUES[Misc.random(UNIQUES.length - 1)];
                c.getItems().addItem(unique, 1);
            }
        } 
        // 2. Black Item Table (11/492)
        else if (roll <= 52) { // 41 + 11
            int blackItem = BLACK_ITEMS[Misc.random(BLACK_ITEMS.length - 1)];
            c.getItems().addItem(blackItem, 1);
        } 
        // 3. Standard Item Table (440/492)
        else {
            int[] standard = STANDARD_ITEMS[Misc.random(STANDARD_ITEMS.length - 1)];
            int itemId = standard[0];
            int amount = (standard[1] == standard[2]) ? standard[1] : Misc.random(standard[1], standard[2]);
            c.getItems().addItem(itemId, amount);
        }
    }
}