package server.model.players.content.treasuretrails.tiers;

import server.model.players.Player;

public class MasterClue {

    public static final int CASKET_ID = 19836;

    // Master clues do not use Maps, Challenge Scrolls, or Ciphers.
    // They do introduce Hot/Cold and Light Boxes.
    public static final int[] ANAGRAM_CLUES = { /* Add IDs here */ };
    public static final int[] COORD_CLUES = { /* Add IDs here */ };
    public static final int[] CRYPTIC_CLUES = { /* Add IDs here */ };
    public static final int[] EMOTE_CLUES = { /* Add IDs here */ };
    public static final int[] HOT_COLD_CLUES = { /* Add IDs here */ };
    public static final int[] LIGHT_BOX_CLUES = { /* Add IDs here */ };
    public static final int[] PUZZLE_CLUES = { /* Add IDs here */ };

    public static int[] getMasterCluePool() {
        int length = ANAGRAM_CLUES.length + COORD_CLUES.length + CRYPTIC_CLUES.length + 
                     EMOTE_CLUES.length + HOT_COLD_CLUES.length + LIGHT_BOX_CLUES.length + PUZZLE_CLUES.length;
        int[] pool = new int[length];
        int pos = 0;
        
        for (int id : ANAGRAM_CLUES) { pool[pos++] = id; }
        for (int id : COORD_CLUES) { pool[pos++] = id; }
        for (int id : CRYPTIC_CLUES) { pool[pos++] = id; }
        for (int id : EMOTE_CLUES) { pool[pos++] = id; }
        for (int id : HOT_COLD_CLUES) { pool[pos++] = id; }
        for (int id : LIGHT_BOX_CLUES) { pool[pos++] = id; }
        for (int id : PUZZLE_CLUES) { pool[pos++] = id; }
        
        return pool;
    }

    public static void openCasket(Player c) {
        if (!c.getItems().playerHasItem(CASKET_ID)) return;
        if (c.getItems().freeSlots() < 5) {
            c.sendMessage("You need at least 5 free inventory slots to open this casket.");
            return;
        }
        c.getItems().deleteItem(CASKET_ID, 1);
        
        // TODO: Add Master drop table logic here
        
        c.sendMessage("You open the casket and find some loot!");
    }
}