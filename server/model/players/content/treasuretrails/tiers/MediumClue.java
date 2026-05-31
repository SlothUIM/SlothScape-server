package server.model.players.content.treasuretrails.tiers;

import server.model.players.Player;

public class MediumClue {

    public static final int CASKET_ID = 20545;

    public static final int[] ANAGRAM_CLUES = { /* Add IDs here */ };
    public static final int[] CHALLENGE_CLUES = { /* Add IDs here */ };
    public static final int[] CIPHER_CLUES = { /* Add IDs here */ };
    public static final int[] COORD_CLUES = { /* Add IDs here */ };
    public static final int[] CRYPTIC_CLUES = { /* Add IDs here */ };
    public static final int[] EMOTE_CLUES = { /* Add IDs here */ };
    public static final int[] MAP_CLUES = { /* Add IDs here */ };

    public static int[] getMediumCluePool() {
        int length = ANAGRAM_CLUES.length + CHALLENGE_CLUES.length + CIPHER_CLUES.length + 
                     COORD_CLUES.length + CRYPTIC_CLUES.length + EMOTE_CLUES.length + MAP_CLUES.length;
        int[] pool = new int[length];
        int pos = 0;
        
        for (int id : ANAGRAM_CLUES) { pool[pos++] = id; }
        for (int id : CHALLENGE_CLUES) { pool[pos++] = id; }
        for (int id : CIPHER_CLUES) { pool[pos++] = id; }
        for (int id : COORD_CLUES) { pool[pos++] = id; }
        for (int id : CRYPTIC_CLUES) { pool[pos++] = id; }
        for (int id : EMOTE_CLUES) { pool[pos++] = id; }
        for (int id : MAP_CLUES) { pool[pos++] = id; }
        
        return pool;
    }

    public static void openCasket(Player c) {
        if (!c.getItems().playerHasItem(CASKET_ID)) return;
        if (c.getItems().freeSlots() < 4) {
            c.sendMessage("You need at least 4 free inventory slots to open this casket.");
            return;
        }
        c.getItems().deleteItem(CASKET_ID, 1);
        
        // TODO: Add Medium drop table logic here
        
        c.sendMessage("You open the casket and find some loot!");
    }
}