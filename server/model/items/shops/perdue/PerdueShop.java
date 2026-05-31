package server.model.items.shops.perdue;

import server.model.players.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class PerdueShop {

    // The items and their exact rebuild costs
    private static final int[][] PERDUES_ITEMS = {
            {9748, 23000},  // Agility cape
            {12638, 14000}, // Book of law
            {13262, 200},   // Abyssal orphan
            {4153, 60000},  // Granite maul
            {11848, 22000}, // Verac helm
            {13235, 15000}, // Eternal boots
            {13265, 8000},  // Abyssal dagger
            {11785, 290000},// Armadyl crossbow
            {13237, 9800},  // Pegasian boots
            {9751, 1900},   // Quest cape
            {13337, 200},   // Bloodhound
            {11283, 200},   // DFS
            {13239, 200},   // Primordial boots
            {19710, 200},   // Avernic defender
            {19553, 200},   // Amulet of torture
            {19547, 200},   // Anguish
            {11832, 200},   // Bandos chestplate
            {11834, 200},   // Bandos tassets
            {12809, 200},   // Blessed Spirit Shield
            {13233, 200},   // Smouldering stone
            {13231, 200},   // Cerberus pet
            {6585, 200},    // Fury
            {11824, 200},   // Zamorakian spear
            {11826, 200}    // Armadyl helmet
    };

    /**
     * Opens and populates the custom Lost Property shop.
     */
    public static void openShop(Player c) {
        int mainInterfaceId = 63000;
        int itemContainerId = 63011;
        int textStartId = 63100;
        int interfaceCapacity = 48; // 8 rows * 6 columns from the client-side design

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

        // Loop through the maximum capacity of the interface
        for (int i = 0; i < interfaceCapacity; i++) {
            if (i < PERDUES_ITEMS.length) {
                // --- WE HAVE AN ITEM FOR THIS SLOT ---
                int itemId = PERDUES_ITEMS[i][0];
                int price = PERDUES_ITEMS[i][1];

                // Format price (e.g., 23000 -> 23,000)
                String priceStr = formatter.format(price);

                // Send the price text
                c.getPA().sendFrame126(priceStr, textStartId + i);

                // Send the item
                // Parameters: (itemID, slotIndex, interfaceID, amount)
                c.getPA().sendFrame34(itemId, i, itemContainerId, 1);

            } else {
                // --- EMPTY SLOT (Clear ghosts) ---

                // Clear the text
                c.getPA().sendFrame126("", textStartId + i);

                // Clear the item slot by sending -1
                c.getPA().sendFrame34(-1, i, itemContainerId, 0);
            }
        }

        // Display the custom interface
        c.getPA().showInterface(mainInterfaceId);
    }
}