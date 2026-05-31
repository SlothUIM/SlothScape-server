package server.model.players.content.treasuretrails.tiers;

import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.util.Misc;

public class EasyClue {

    public static final int CASKET_ID = 20546;

    // =========================================================================
    // CLUE SCROLL IDs
    // =========================================================================
    
    public static final int[] CRYPTIC_CLUES = { 2677, 2678, 2679, 2680, 2681, 2682, 2683, 2684, 2685, 2686, 2687, 2688, 2689, 2690, 2691, 2692, 2693, 2694, 2695, 2696, 2697, 2698, 2699, 2700, 2701, 2702, 2703, 2704, 2705, 2706, 2707, 2708, 2709, 2710, 2711, 2712, 2713, 2716, 2719 };
    public static final int[] EMOTE_CLUES = {
            10216, 10222, 10232, 10188, 10210, 10212, 10192, 10228, 10182, 10206, 
            31268, 10220, 10200, 19831, 10208, 10180, 19833, 10194, 10214, 10226, 
            10186, 10224, 10202, 10218, 10196, 12162, 10230, 12164, 10190, 10184, 
            28914, 10204
        };
    public static final int[] MAP_CLUES = { /* IDs handled in CoordinateClues.java */ };

    public static int[] getEasyCluePool() {
        int length = CRYPTIC_CLUES.length + EMOTE_CLUES.length + MAP_CLUES.length;
        int[] pool = new int[length];
        int pos = 0;
        for (int id : CRYPTIC_CLUES) { pool[pos++] = id; }
        for (int id : EMOTE_CLUES) { pool[pos++] = id; }
        for (int id : MAP_CLUES) { pool[pos++] = id; }
        return pool;
    }

    // =========================================================================
    // CASKET REWARD TABLES
    // =========================================================================

    // --- UNIQUES (Trimmed Armours, Elegant, Fun Weapons, etc.) ---
    private static final int[] EASY_UNIQUES = {
        // Bronze (t) & (g)
        11314, 11316, 11318, 11320, 11322, 11324, 11326, 11328, 11330, 11332,
        // Iron (t) & (g)
        11304, 11306, 11308, 11310, 11312, 11294, 11296, 11298, 11300, 11302,
        // Steel (t) & (g)
        11284, 11286, 11288, 11290, 11292, 11274, 11276, 11278, 11280, 11282,
        // Black (t) & (g)
        2587, 2589, 2591, 3472, 2593, 2595, 2597, 2599, 3473, 2605,
        // Black Heraldic (h1 - h5) Helms & Shields
        10392, 10394, 10396, 10398, 10400, 10402, 10404, 10406, 10408, 10410,
        // Wizard Robes (t) & (g)
        7386, 7388, 7390, 7392, 7394, 7396,
        // Leather (g) & Studded (t)/(g)
        7362, 7364, 7366, 7368, 13099, 13101,
        // Elegant Clothing (Blue, Green, Red)
        10400, 10402, 10404, 10406, 10408, 10410, 10412, 10414, 
        // Hats (Berets, Highwayman, Beanie, Sleeping cap, Pantaloons)
        2633, 2635, 2637, 12247, 2631, 12245, 10398, 10392,
        // Emote Enhancers & Fun Items
        20110, // Ham joint
        20113, // Rain bow
        13097, // Black cane
        12297, // Black pickaxe
        23225, // Large spade
        20120, // Cape of skulls
        20211, 20214, 20217, // Team capes (i, x, zero)
        // Amulets (t)
        10364, // Amulet of magic (t)
        23354, // Amulet of power (t)
        // Gold monk robes & Chef gear
        20171, 20173, // Monk's robe top/bottom (g)
        20146, 20149, // Golden chef hat, golden apron
        // Bob shirts
        10366, 10368, 10370, 10372, 10374
    };

    // --- MASTER BLESSINGS (1/12 chance when rolling a unique) ---
    private static final int[] BLESSINGS = {
        20220, 20223, 20226, 20229, 20232, 21326
    };

    // --- STANDARD ITEMS (Format: {ItemID, MinAmount, MaxAmount}) ---
    // Contains basic Black gear, Salmon/Trout, Runes, and Bows
    private static final int[][] STANDARD_EASY = {
        // Armour & Weapons
        {1169, 1, 1}, {1129, 1, 1}, {1095, 1, 1}, // Leather/Coif
        {1165, 1, 1}, {1195, 1, 1}, {1125, 1, 1}, {1077, 1, 1}, {1089, 1, 1}, {1101, 1, 1}, // Black armour
        {1327, 1, 1}, {1341, 1, 1}, {1361, 1, 1}, {1295, 1, 1}, // Black weapons
        {1269, 1, 1}, // Steel pickaxe
        {847, 1, 1}, {849, 1, 1}, {851, 1, 1}, // Bows (Willow/Yew)
        
        // Runes & Ammo
        {556, 30, 50}, {558, 30, 50}, {555, 30, 50}, {557, 30, 50}, {554, 30, 50}, {559, 30, 50}, 
        {562, 5, 15}, {560, 5, 15}, {563, 5, 15}, 
        
        // Food (Noted)
        {330, 6, 12}, // Salmon
        {334, 6, 12}  // Trout
    };

    public static void openCasket(Player c) {
        if (!c.getItems().playerHasItem(CASKET_ID)) {
            return;
        }

        // Easy clues give 2 to 4 rolls
        int totalRolls = Misc.random(2, 4);

        if (c.getItems().freeSlots() < totalRolls) {
            c.sendMessage("You need at least " + totalRolls + " free inventory slots to open this casket.");
            return;
        }

        c.getItems().deleteItem(CASKET_ID, 1);
        
        // Arrays to hold our rolled items before we send them to the interface
        int[] tempItems = new int[totalRolls];
        int[] tempAmounts = new int[totalRolls];
        int uniqueItemCount = 0;
        
        for (int i = 0; i < totalRolls; i++) {
            // rollReward now RETURNS an array of { itemId, amount } instead of adding it directly!
            int[] reward = rollReward(c); 
            int itemId = reward[0];
            int amount = reward[1];
            
            // Check if we already rolled this exact item so we can stack them!
            boolean stacked = false;
            for (int j = 0; j < uniqueItemCount; j++) {
                if (tempItems[j] == itemId) {
                    tempAmounts[j] += amount;
                    stacked = true;
                    break;
                }
            }
            
            // If it's a new item, add it to the next open slot
            if (!stacked) {
                tempItems[uniqueItemCount] = itemId;
                tempAmounts[uniqueItemCount] = amount;
                uniqueItemCount++;
            }
        }
        
        // We trim the arrays down to the exact number of unique items we got 
        // (so we don't send empty slots to the interface)
     // We trim the arrays down to the exact number of unique items we got 
        int[] finalItems = new int[uniqueItemCount];
        int[] finalAmounts = new int[uniqueItemCount];
        System.arraycopy(tempItems, 0, finalItems, 0, uniqueItemCount);
        System.arraycopy(tempAmounts, 0, finalAmounts, 0, uniqueItemCount);
        
        // --- COLLECTION LOG HOOK ---
        for (int i = 0; i < finalItems.length; i++) {
            // This will automatically check "EasyClue", "EasyRareClue" (if it existed), and "SharedClue"
            TreasureTrails.handleCollectionLog(c, "EasyClue", finalItems[i], finalAmounts[i]);
        }
        
        // Send it to your brand new dynamic interface!
        c.getPA().displayReward(c, finalItems, finalAmounts);
        c.sendMessage("You open the casket and find some loot!");
    }

    private static int[] rollReward(Player c) {
        // Returns { Item_ID, Amount }
        if (Misc.random(1, 12) == 1) {
            if (Misc.random(1, 10) == 1) {
                return new int[] { BLESSINGS[Misc.random(BLESSINGS.length - 1)], 1 };
            } else {
                return new int[] { EASY_UNIQUES[Misc.random(EASY_UNIQUES.length - 1)], 1 };
            }
        } else {
            int[] standard = STANDARD_EASY[Misc.random(STANDARD_EASY.length - 1)];
            int itemId = standard[0];
            int amount = (standard[1] == standard[2]) ? standard[1] : Misc.random(standard[1], standard[2]);
            
            if (itemId == 329) itemId = 330; // Note Salmon
            if (itemId == 333) itemId = 334; // Note Trout

            return new int[] { itemId, amount };
        }
    }
}