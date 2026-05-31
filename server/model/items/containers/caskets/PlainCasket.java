package server.model.items.containers.caskets;

import server.model.players.Player;
import server.util.Misc;

public class PlainCasket {

    private static final int CASKET_ID = 405;

    public static void openCasket(Player c) {
        // 1. Check if they actually have the casket
        if (!c.getItems().playerHasItem(CASKET_ID)) {
            return;
        }

        // 2. Make sure they have at least 1 free inventory slot
        if (c.getItems().freeSlots() < 1) {
            c.sendMessage("You need at least 1 free inventory slot to open this.");
            return;
        }

        // 3. Delete the casket
        c.getItems().deleteItem(CASKET_ID, 1);

        // 4. Roll a number between 1 and 128
        // (Assuming your Misc.random(int) is 0-inclusive to max-exclusive. 
        // If your Misc is different, adjust to ensure a 1-128 range).
        int roll = Misc.random(127) + 1; 
        
        int itemToGive = -1;
        int amountToGive = 1;

        // --- COINS (Total 60/128) ---
        if (roll <= 10) { itemToGive = 995; amountToGive = 20; }
        else if (roll <= 20) { itemToGive = 995; amountToGive = 40; }
        else if (roll <= 30) { itemToGive = 995; amountToGive = 80; }
        else if (roll <= 40) { itemToGive = 995; amountToGive = 160; }
        else if (roll <= 50) { itemToGive = 995; amountToGive = 320; }
        else if (roll <= 60) { itemToGive = 995; amountToGive = 640; }

        // --- GEMS (Total 58/128) ---
        else if (roll <= 92) { itemToGive = 1623; } // Uncut sapphire (32 rolls)
        else if (roll <= 108) { itemToGive = 1621; } // Uncut emerald (16 rolls)
        else if (roll <= 116) { itemToGive = 1619; } // Uncut ruby (8 rolls)
        else if (roll <= 118) { itemToGive = 1617; } // Uncut diamond (2 rolls)

        // --- OTHER (Total 10/128) ---
        else if (roll <= 126) { itemToGive = 1454; } // Cosmic talisman (8 rolls)
        else if (roll == 127) { itemToGive = 987; }  // Loop half of key (1 roll)
        else if (roll == 128) { itemToGive = 985; }  // Tooth half of key (1 roll)

        // 5. Give the reward and send message
        if (itemToGive != -1) {
            c.getItems().addItem(itemToGive, amountToGive);
            c.sendMessage("You open the casket and find a reward inside!");
        }
    }
}