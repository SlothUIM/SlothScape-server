package server.model.content;

import server.model.players.Player;

public class STASH {

    // Material Item IDs
    public static final int HAMMER = 2347;
    public static final int NAILS = 4819;
    public static final int IRON_NAILS = 4820;
    public static final int STEEL_NAILS = 1539;
    public static final int PLANK = 960;
    public static final int OAK_PLANK = 8778;
    public static final int TEAK_PLANK = 8780;
    public static final int MAHOG_PLANK = 8782;

    /**
     * Attempts to build the STASH unit when a player uses a plank on it, or clicks it.
     */
    public static void buildStash(Player c, int objectID) {
        StashUnit unit = StashUnit.forObjectId(objectID);
        if (unit == null) return;

        int index = unit.ordinal();

        if (c.stashBuilt[index]) {
            c.sendMessage("You have already built this STASH unit.");
            return;
        }

        // Determine materials based on the Stash Unit's Tier
        int requiredPlank = PLANK;
        int requiredNail = NAILS;
        
        if (unit.getTier() == 2) { 
            requiredPlank = OAK_PLANK; 
            requiredNail = IRON_NAILS; 
        } else if (unit.getTier() == 3) { 
            requiredPlank = TEAK_PLANK; 
            requiredNail = STEEL_NAILS; 
        } else if (unit.getTier() >= 4) { 
            requiredPlank = MAHOG_PLANK; 
            requiredNail = STEEL_NAILS; 
        }

        if (!c.getItems().playerHasItem(HAMMER)) {
            c.sendMessage("You need a hammer to build this STASH unit.");
            return;
        }
        if (c.getItems().getItemAmount(requiredPlank) < 3 || c.getItems().getItemAmount(requiredNail) < 10) {
            c.sendMessage("You need 3 " + c.getItems().getItemName(requiredPlank) + " and 10 " + c.getItems().getItemName(requiredNail) + " to build this.");
            return;
        }

        // Take materials and build
        c.getItems().deleteItem2(requiredPlank, 3);
        c.getItems().deleteItem2(requiredNail, 10);
        c.startAnimation(898);
        
        c.stashBuilt[index] = true;
        refreshConfigs(c);
        c.sendMessage("You build a new STASH unit.");
    }

    /**
     * Handles clicking on a STASH unit to deposit or withdraw items.
     */
    public static void interactStash(Player c, int objectID) {
        StashUnit unit = StashUnit.forObjectId(objectID);
        if (unit == null) return;

        int index = unit.ordinal();

        if (!c.stashBuilt[index]) {
            c.sendMessage("You must build this STASH unit before you can use it.");
            return;
        }

        // If it already has items inside -> Take them out
        if (c.stashFilled[index]) {
            if (c.getItems().freeSlots() < unit.getRequiredItems().length) {
                c.sendMessage("You don't have enough inventory space to take these items.");
                return;
            }
            for (int item : unit.getRequiredItems()) {
                c.getItems().addItem(item, 1);
            }
            c.stashFilled[index] = false;
           // refreshConfigs(c); // Update visual to filled!
            c.sendMessage("You take the items out of your STASH.");
        } 
        // If it's empty -> Put items in
        else {
            boolean hasAllItems = true;
            for (int item : unit.getRequiredItems()) {
                if (!c.getItems().playerHasItem(item)) {
                    hasAllItems = false;
                    break;
                }
            }

            if (!hasAllItems) {
                c.sendMessage("You don't have all the required items to store here.");
                return;
            }

            for (int item : unit.getRequiredItems()) {
                c.getItems().deleteItem2(item, 1);
            }
            c.stashFilled[index] = true;
           // refreshConfigs(c); // Update visual to filled!
            c.sendMessage("You securely store the items in your STASH.");
        }
    }

    /**
     * Restores the visual configs when the player logs in.
     */
    /**
     * Calculates the combined Varp values for all STASH units and sends them.
     */
    public static void refreshConfigs(Player c) {
        // Map to hold the total accumulated value for each Config ID
        java.util.Map<Integer, Integer> configValues = new java.util.HashMap<>();

        for (StashUnit unit : StashUnit.values()) {
            int index = unit.ordinal();
            int configId = unit.getConfigId();
            
            // Get the current accumulated value for this Config ID (or 0 if it's the first one)
            int currentValue = configValues.getOrDefault(configId, 0);

            // STRICTLY 1 OR 0. Do not use 2!
            int state = c.stashBuilt[index] ? 1 : 0;

            // Bitwise OR (|) the state into the correct position so they don't overwrite each other
            currentValue |= (state << unit.getBitShift());

            // Save it back to our map
            configValues.put(configId, currentValue);
        }

        // Finally, loop through our combined values and send them to the client!
        for (java.util.Map.Entry<Integer, Integer> entry : configValues.entrySet()) {
            c.getPA().sendConfig(entry.getKey(), entry.getValue());
        }
    }
}