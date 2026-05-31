package server.model.players.content;

import java.util.*;
import java.util.stream.Collectors;

import server.Config;
import server.model.items.Item;
import server.model.items.ItemValue;
import server.model.players.Player;
import server.model.players.packets.GeValuesCache;
import server.model.players.packets.Pair;
import server.util.Misc;

public class ItemsKeptOnDeath {

    private static final int INTERFACE_ID = 34400;
    private static final int KEPT_ITEM_FRAME = 10494;
    private static final int LOST_ITEM_FRAME = 10600;
    public static void handleDeathStorage(Player c) {
        List<Item> protectedItems = new ArrayList<>();
        List<Item> toRetrieve = new ArrayList<>();

        // 1. Scan Inventory
        for (int i = 0; i < c.playerItems.length; i++) {
            int itemId = c.playerItems[i] - 1;
            int amount = c.playerItemsN[i];
            if (itemId <= 0) continue;

            if (isProtected(itemId)) {
                protectedItems.add(new Item(itemId, amount));
            } else {
                toRetrieve.add(new Item(itemId, amount));
            }
        }

        // 2. Scan Equipment
        for (int i = 0; i < c.playerEquipment.length; i++) {
            int itemId = c.playerEquipment[i];
            int amount = c.playerEquipmentN[i];
            if (itemId <= 0) continue;

            if (isProtected(itemId)) {
                protectedItems.add(new Item(itemId, amount));
            } else {
                toRetrieve.add(new Item(itemId, amount));
            }
        }

        // 3. Wipe current items and store retrieval list
        c.getItems().deleteAllItems(); //
        c.deathItems.clear(); //
        c.deathItems.addAll(toRetrieve); //

        // 4. Give back protected items
        for (Item item : protectedItems) {
            c.getItems().addItem(item.id, item.amount); //
        }
    }

    /**
     * Checks if an item is in the UNDROPPABLE_ITEMS config.
     */
    private static boolean isProtected(int itemId) {
        for (int id : Config.UNDROPPABLE_ITEMS) {
            if (id == itemId) return true;
        }
        return false;
    }
    public static List<Item> getItemsForRetrieval(Player c) {
        List<ItemValue> allItems = new ArrayList<>();

        // 1. Gather all items (Same as your provided code)
        for (int i = 0; i < 28; i++) {
            if (c.playerItems[i] > 0) {
                allItems.add(new ItemValue(c.playerItems[i] - 1, i, c.playerItemsN[i], getGEValue(c.playerItems[i] - 1)));
            }
        }
        for (int i = 0; i < 14; i++) {
            if (c.playerEquipment[i] > 0) {
                allItems.add(new ItemValue(c.playerEquipment[i], i + 28, c.playerEquipmentN[i], getGEValue(c.playerEquipment[i])));
            }
        }

        // 2. Sort by value
        allItems.sort((a, b) -> Integer.compare(b.value, a.value));

        // 3. Determine keep count
        int keepCount = c.isSkulled ? 0 : 3;
        if (c.prayerActive[10]) keepCount++;

        List<Item> toRetrieve = new ArrayList<>();
        int itemsProcessedAsKept = 0;

        // 4. Distribution logic
        for (ItemValue item : allItems) {
            if (itemsProcessedAsKept < keepCount && !isAlwaysLost(item.id)) {
                // This item is KEPT by the player (add to inventory in giveLife)
                itemsProcessedAsKept++;
            } else {
                // This item is LOST (add to Retrieval Service)
                toRetrieve.add(new Item(item.id, item.amount));
            }
        }
        
        // 5. Add Looting Bag items if any (They are always lost)
        for (int i = 0; i < c.playerLootItems.length; i++) {
            if (c.playerLootItems[i] > 0) {
                toRetrieve.add(new server.model.items.Item(c.playerLootItems[i], c.playerLootItemsN[i]));
            }
        }

        return toRetrieve;
    }
    public static void open(Player c) {
        List<ItemValue> allItems = new ArrayList<>();

        // 1. Gather all items from inventory
        for (int i = 0; i < 28; i++) {
            if (c.playerItems[i] > 0) {
                int id = c.playerItems[i] - 1;
                allItems.add(new ItemValue(id, i, c.playerItemsN[i], getGEValue(id)));
            }
        }

        // 2. Gather all items from equipment
        for (int i = 0; i < 14; i++) {
            if (c.playerEquipment[i] > 0) {
                int id = c.playerEquipment[i];
                allItems.add(new ItemValue(id, i + 28, c.playerEquipmentN[i], getGEValue(id)));
            }
        }

        // 3. Sort by total value (price * amount) descending
        allItems.sort((a, b) -> Integer.compare(b.value, a.value));

        // 4. Determine keep count
        int keepCount = c.isSkulled ? 0 : 3;
        if (c.prayerActive[10]) keepCount++;

        List<ItemValue> kept = new ArrayList<>();
        List<ItemValue> lost = new ArrayList<>();

        // 5. Distribute items between Kept and Lost
        int itemsProcessedAsKept = 0;
        for (ItemValue item : allItems) {
            if (itemsProcessedAsKept < keepCount && !isAlwaysLost(item.id)) {
                kept.add(item);
                itemsProcessedAsKept++;
            } else {
                lost.add(item);
            }
        }

        updateInterface(c, kept, lost, keepCount);
    }

    private static void updateInterface(Player c, List<ItemValue> kept, List<ItemValue> lost, int maxKeep) {
        // Clear frames
        for (int i = 0; i < 4; i++) c.getPA().sendFrame34a(KEPT_ITEM_FRAME, -1, i, 0);
        for (int i = 0; i < 40; i++) c.getPA().sendFrame34a(LOST_ITEM_FRAME, -1, i, 0);

        // Send Kept Items
        for (int i = 0; i < kept.size(); i++) {
            c.getPA().sendFrame34a(KEPT_ITEM_FRAME, kept.get(i).id, i, 1);
        }

        // Send Lost Items
        for (int i = 0; i < lost.size(); i++) {
            c.getPA().sendFrame34a(LOST_ITEM_FRAME, lost.get(i).id, i, lost.get(i).amount);
        }

        // Information text
        long carriedWealth = kept.stream().mapToLong(i -> (long)i.value * i.amount).sum() 
                           + lost.stream().mapToLong(i -> (long)i.value * i.amount).sum();
        long riskedWealth = lost.stream().mapToLong(i -> (long)i.value * i.amount).sum();

        c.getPA().sendFrame126("Max items kept on death :", 34407);
        c.getPA().sendFrame126("~ " + maxKeep + " ~", 34408);
        c.getPA().sendFrame126("Items you will keep on death if not skulled:", 34404);
        c.getPA().sendFrame126("Items you will lose on death if not skulled:", 34405);
        c.getPA().sendFrame126("Value of lost items: @yel@", 34411);
        c.getPA().sendFrame126(Misc.format((int)riskedWealth)+" gp", 34412);

        // Descriptions based on skull/prayer
        String info = c.isSkulled ? "You are skulled" : "You have no factors affecting";
        String info2 = c.isSkulled ? "" : "the items you keep.";
        if (c.prayerActive[10]) 
        	info += "Protect Item is active (+1).";
        c.getPA().sendFrame126("The normal amount of items", 34568);
        c.getPA().sendFrame126("kept is three.", 34569);
        c.getPA().sendFrame126(""+info, 34571);
        c.getPA().sendFrame126(""+info2, 34572);
        c.getPA().sendFrame126("Items with a @whi@white outline@or1@ will", 34574);
        c.getPA().sendFrame126("always be lost.", 34575);

        c.getPA().showInterface(INTERFACE_ID);
    }

	public static int getGEValue(int itemId) {
	    Pair<Integer, Integer> values = GeValuesCache.geValues.get(itemId);
	    return values != null ? values.getFirst() : 0;
	}
    private static boolean isAlwaysLost(int id) {
        // Handle items that never stay on death (Lootbag, etc)
        return id == 11941;
    }
}