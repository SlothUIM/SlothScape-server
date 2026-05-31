package server.model.minigames;

import server.model.players.Player;

public class NMZRewards {

    // --- IMBUES: {Base Item, Imbued Item, Point Cost} ---
    public static final int[][] IMBUABLES = {
        {8921, 11784, 1250000}, // Black mask -> Black mask (i)
        {11864, 11865, 1250000}, // Slayer helmet -> Slayer helmet (i)
        {4081, 12017, 800000},  // Salve amulet -> Salve amulet (i)
        {10588, 12018, 800000}, // Salve amulet (e) -> Salve (ei)
        {6737, 11773, 650000},  // Berserker ring -> Berserker ring (i)
        {6739, 11771, 650000},  // Warrior ring -> Warrior ring (i)
        {6733, 11772, 650000},  // Archers ring -> Archers ring (i)
        {6731, 11770, 650000}   // Seers ring -> Seers ring (i)
    };

    /**
     * Tries to imbue an item from the Upgrades Tab
     */
    public static void handleImbue(Player c, int clickedItemId) {
        for (int[] imbue : IMBUABLES) {
            if (imbue[0] == clickedItemId) {
                if (!c.getItems().playerHasItem(imbue[0])) {
                    c.sendMessage("You do not have this item in your inventory to imbue.");
                    return;
                }
                if (c.nmzPoints < imbue[2]) {
                    c.sendMessage("You need " + String.format("%,d", imbue[2]) + " points to imbue this.");
                    return;
                }

                // Deduct points, replace item!
                c.nmzPoints -= imbue[2];
                c.getItems().deleteItem(imbue[0], 1);
                c.getItems().addItem(imbue[1], 1);
                c.sendMessage("You have successfully imbued your " + c.getItems().getItemName(imbue[0]) + ".");
                
                // Refresh the interface so the item disappears from the screen
                refreshUpgradesTab(c); 
                return;
            }
        }
    }

    /**
     * Scans inventory and sends valid items to the Upgrades Tab container
     */
    public static void refreshUpgradesTab(Player c) {
        // Find which imbuables the player actually owns
        java.util.ArrayList<Integer> itemsToDisplay = new java.util.ArrayList<>();
        
        for (int[] imbue : IMBUABLES) {
            if (c.getItems().playerHasItem(imbue[0])) {
                itemsToDisplay.add(imbue[0]);
            }
        }

        if (itemsToDisplay.isEmpty()) {
            c.getPA().sendFrame126("Click on an item to imbue it.", 25601);    
            c.getPA().sendFrame126("There are no items in your inventory that can be imbued.", 25603); // Replace with your text ID
            c.getPA().sendFrame126("Dominic can imbue crystal bows, crystal shields, black masks \\\\nand certein rings found around Waterbirth Island.", 25604); // Replace with your text ID
        } else {
            c.getPA().sendFrame126("Click on an item to imbue it.", 25601);    
            c.getPA().sendFrame126("", 25603); // Replace with your text ID
            c.getPA().sendFrame126("", 25604); // Replace with your text ID
            
        }

        // Send the items to the ItemContainer on the interface
        // Replace 25555 with the actual ID of your ItemContainer in RSInterface!
        c.getOutStream().createFrameVarSizeWord(53);
        c.getOutStream().writeWord(25602); 
        c.getOutStream().writeWord(itemsToDisplay.size());
        
        for (int itemId : itemsToDisplay) {
            c.getOutStream().writeByte(1); // Amount
            c.getOutStream().writeWordBigEndianA(itemId + 1); // Item ID
        }
        c.getOutStream().endFrameVarSizeWord();
        
        // Update Points Text
        c.getPA().sendFrame126("Reward points: " + String.format("%,d", c.nmzPoints), 25536);
    }

    /**
     * Handles buying a dose from the Benefits Tab
     */
    public static void buyBenefit(Player c, int potionType) {
        int cost = 0;
        int maxDoses = 255;

        switch (potionType) {
            case 1: cost = 1000; break; // Absorption
            case 2: cost = 1500; break; // Overload
            case 3: cost = 250; break;  // Super Magic
            case 4: cost = 250; break;  // Super Ranging
        }

        if (c.nmzPoints < cost) {
            c.sendMessage("You need " + String.format("%,d", cost) + " points to buy a dose of this potion.");
            return;
        }

        // Add the dose to their barrels
        switch (potionType) {
            case 1:
                if (c.nmzAbsorptionDoses >= maxDoses) { c.sendMessage("Your barrel is full."); return; }
                c.nmzAbsorptionDoses++;
                break;
            case 2:
                if (c.nmzOverloadDoses >= maxDoses) { c.sendMessage("Your barrel is full."); return; }
                c.nmzOverloadDoses++;
                break;
            case 3:
                if (c.nmzSuperMagicDoses >= maxDoses) { c.sendMessage("Your barrel is full."); return; }
                c.nmzSuperMagicDoses++;
                break;
            case 4:
                if (c.nmzSuperRangingDoses >= maxDoses) { c.sendMessage("Your barrel is full."); return; }
                c.nmzSuperRangingDoses++;
                break;
        }

        c.nmzPoints -= cost;
        
        // Refresh the text showing how many doses they have stored!
        // Replace these IDs with the actual text lines below the potions on your interface
        c.getPA().sendFrame126("(" + c.nmzAbsorptionDoses + ")", 25560); 
        c.getPA().sendFrame126("(" + c.nmzOverloadDoses + ")", 25561);
        c.getPA().sendFrame126("(" + c.nmzSuperMagicDoses + ")", 25562);
        c.getPA().sendFrame126("(" + c.nmzSuperRangingDoses + ")", 25563);
        
        c.getPA().sendFrame126("Reward points: " + String.format("%,d", c.nmzPoints), 25536);
    }
 // The exact items in the Resources Tab
    public static final int[] RESOURCES = { 1779, 1783, 401, 6470, 556, 555, 557, 554, 1436, 7936, 11738, 227, 11740 };
    
    /**
     * Fills the Resources Container (Tab 1)
     */
    public static void refreshResourcesTab(Player c) {
        c.getOutStream().createFrameVarSizeWord(53);
        c.getOutStream().writeWord(25546); // The ItemContainer ID
        c.getOutStream().writeWord(RESOURCES.length);
        for (int itemId : RESOURCES) {
            c.getOutStream().writeByte(1); // Amount
            c.getOutStream().writeWordBigEndianA(itemId + 1); 
        }
        c.getOutStream().endFrameVarSizeWord();
        c.getPA().sendFrame126("Reward points: " + String.format("%,d", c.nmzPoints), 25536);
        c.getPA().sendConfig(6546, 0);
    }

    /**
     * Fills the Benefits Potions (Tab 3)
     */
    public static void refreshBenefitsTab(Player c) {
        // Send the 4 potions directly into the 1x1 containers
        c.getPA().sendFrame34a(25709, 11734, 0, 1); // Absorption
        c.getPA().sendFrame34a(25707, 11730, 0, 1); // Overload
        c.getPA().sendFrame34a(25705, 11726, 0, 1); // Super Magic
        c.getPA().sendFrame34a(25703, 11722, 0, 1); // Super Range

        c.getPA().sendFrame126("(" + c.nmzAbsorptionDoses + ")", 25718); 
        c.getPA().sendFrame126("(" + c.nmzOverloadDoses + ")", 25717);
        c.getPA().sendFrame126("(" + c.nmzSuperMagicDoses + ")", 25716);
        c.getPA().sendFrame126("(" + c.nmzSuperRangingDoses + ")", 25715);
        c.getPA().sendFrame126("Reward points: " + String.format("%,d", c.nmzPoints), 25536);
    }
    public static int getPrice(int itemId) {
        switch (itemId) {
            // Benefits (Potions)
            case 11734: return 1000; // Absorb
            case 11730: return 1500; // Overload
            case 11726: return 250;  // Super Magic
            case 11722: return 250;  // Super Ranging
            
            // Resources
            case 1779: return 75;    // Flax
            case 1783: return 200;   // Sand
            case 401: return 200;    // Seaweed
            case 6470: return 5000;  // Compost
            case 556: case 555: case 557: case 554: return 25; // Runes
            case 1436: return 60;    // Rune ess
            case 7936: return 70;    // Pure ess
            case 11738: return 9500; // Herb box
            case 227: return 145;    // Vial of water
            case 11740: return 775;  // Scroll of redirection
        }
        return 0;
    }

    public static void buyItem(Player c, int interfaceId, int itemId, int amount) {
        int cost = getPrice(itemId);
        if (cost == 0) return; // Not a valid item
        
        long totalCost = (long) cost * amount;
        if (c.nmzPoints < totalCost) {
            c.sendMessage("You need " + String.format("%,d", totalCost) + " points to buy this.");
            return;
        }

        // --- BENEFITS TAB ---
        if (interfaceId == 25703 || interfaceId == 25705 || interfaceId == 25707 || interfaceId == 25709) {
            int maxDoses = 255;
            switch (itemId) {
                case 11734: 
                    if (c.nmzAbsorptionDoses + amount > maxDoses) { c.sendMessage("Your barrel is full."); return; }
                    c.nmzAbsorptionDoses += amount; 
                    break;
                case 11730: 
                    if (c.nmzOverloadDoses + amount > maxDoses) { c.sendMessage("Your barrel is full."); return; }
                    c.nmzOverloadDoses += amount; 
                    break;
                case 11726: 
                    if (c.nmzSuperMagicDoses + amount > maxDoses) { c.sendMessage("Your barrel is full."); return; }
                    c.nmzSuperMagicDoses += amount; 
                    break;
                case 11722: 
                    if (c.nmzSuperRangingDoses + amount > maxDoses) { c.sendMessage("Your barrel is full."); return; }
                    c.nmzSuperRangingDoses += amount; 
                    break;
            }
            c.nmzPoints -= totalCost;
            refreshBenefitsTab(c); // Instantly update the white text on screen!
            return;
        }

        // --- RESOURCES TAB ---
        if (interfaceId == 25546) {
            if (c.getItems().freeSlots() == 0 && !c.getItems().playerHasItem(itemId)) {
                c.sendMessage("You do not have enough inventory space.");
                return;
            }
            c.nmzPoints -= totalCost;
            c.getItems().addItem(itemId, amount);
            refreshResourcesTab(c); // Update their point total
        }
    }
    /**
     * Handles all interactions with the NMZ Potion Barrels
     * @param clickType 1 = Check, 2 = Take, 3 = Store
     */
    public static void handleBarrel(Player c, int objectId, int clickType) {
        String potionName = "";
        int currentDoses = 0;
        int[] potionIds = new int[4]; // [1-dose, 2-dose, 3-dose, 4-dose]

        // 1. Identify which barrel they clicked
        switch (objectId) {
            case 26277: // Super Ranging
                potionName = "Super ranging";
                currentDoses = c.nmzSuperRangingDoses;
                potionIds = new int[]{ 11725, 11724, 11723, 11722 };
                break;
            case 26278: // Super Magic
                potionName = "Super magic";
                currentDoses = c.nmzSuperMagicDoses;
                potionIds = new int[]{ 11729, 11728, 11727, 11726 };
                break;
            case 26279: // Overload
                potionName = "Overload";
                currentDoses = c.nmzOverloadDoses;
                potionIds = new int[]{ 11733, 11732, 11731, 11730 };
                break;
            case 26280: // Absorption
                potionName = "Absorption";
                currentDoses = c.nmzAbsorptionDoses;
                potionIds = new int[]{ 11737, 11736, 11735, 11734 };
                break;
            default:
                return;
        }

        // ==========================================
        // OPTION 1: CHECK
        // ==========================================
        if (clickType == 1) {
            c.sendMessage("You currently have " + currentDoses + " doses of " + potionName + " stored in the barrel.");
            return;
        }

        // ==========================================
        // OPTION 2: TAKE
        // ==========================================
        if (clickType == 2) {
            if (currentDoses <= 0) {
                c.sendMessage("You don't have any " + potionName + " doses stored in this barrel.");
                return;
            }
            if (c.getItems().freeSlots() == 0) {
                c.sendMessage("Your inventory is too full to take a potion.");
                return;
            }

            // Figure out what size potion to give them (up to a 4-dose)
            int dosesToGive = Math.min(4, currentDoses);
            int itemToGive = potionIds[dosesToGive - 1]; // Array is 0-indexed!

            // Give item and deduct doses
            c.getItems().addItem(itemToGive, 1);
            int newTotal = currentDoses - dosesToGive;

            // Save the new dose count back to the player
            switch (objectId) {
                case 26277: c.nmzSuperRangingDoses = newTotal; break;
                case 26278: c.nmzSuperMagicDoses = newTotal; break;
                case 26279: c.nmzOverloadDoses = newTotal; break;
                case 26280: c.nmzAbsorptionDoses = newTotal; break;
            }
            
            c.sendMessage("You take a dose of " + potionName + " from the barrel.");
            return;
        }

        // ==========================================
        // OPTION 3: STORE
        // ==========================================
        if (clickType == 3) {
            int dosesStored = 0;
            int maxDoses = 255;

            // Loop through inventory to find any doses of this specific potion
            for (int i = 0; i < c.playerItems.length; i++) {
                int id = c.playerItems[i] - 1; // 317 inventories are offset by 1
                
                int doseValue = 0;
                if (id == potionIds[3]) doseValue = 4;
                else if (id == potionIds[2]) doseValue = 3;
                else if (id == potionIds[1]) doseValue = 2;
                else if (id == potionIds[0]) doseValue = 1;

                // If they have this potion and the barrel isn't full...
                if (doseValue > 0) {
                    if (currentDoses + dosesStored + doseValue <= maxDoses) {
                        c.getItems().deleteItem(id, 1);
                        dosesStored += doseValue;
                    } else {
                        c.sendMessage("The barrel cannot hold any more doses!");
                        break; // Stop looping if barrel is full
                    }
                }
            }

            if (dosesStored > 0) {
                int newTotal = currentDoses + dosesStored;
                switch (objectId) {
                    case 26277: c.nmzSuperRangingDoses = newTotal; break;
                    case 26278: c.nmzSuperMagicDoses = newTotal; break;
                    case 26279: c.nmzOverloadDoses = newTotal; break;
                    case 26280: c.nmzAbsorptionDoses = newTotal; break;
                }
                c.sendMessage("You pour " + dosesStored + " doses of " + potionName + " into the barrel.");
            } else {
                c.sendMessage("You don't have any " + potionName + " potions to store.");
            }
        }
    }
}