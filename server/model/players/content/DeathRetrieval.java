package server.model.players.content;

import server.model.items.Item;
import server.model.players.Player;
import server.util.Misc;
import java.util.ArrayList;
import java.util.Iterator;

public class DeathRetrieval {

    public static final int ITEM_INTERFACE = 51710;

    public static void open(Player c) {
        refresh(c);
        c.getPA().showInterface(51700);
    }

    public static void refresh(Player c) {
        // Clear slots first
        for (int i = 0; i < 80; i++) {
            c.getPA().sendFrame34a(ITEM_INTERFACE, -1, i, 0);
        }

        long totalValue = 0;
        int totalCount = 0;

        // deathItems should be a List<Item> in your Player class
        for (int i = 0; i < c.deathItems.size(); i++) {
            Item item = c.deathItems.get(i);
            if (item == null || item.id <= 0) continue;

            c.getPA().sendFrame34a(ITEM_INTERFACE, item.id, i, item.amount);
            totalValue += (long) ItemsKeptOnDeath.getGEValue(item.id) * item.amount;
            totalCount += item.amount;
        }

        c.getPA().sendFrame126("Stack count: @whi@" + totalCount, 51720);
        c.getPA().sendFrame126("Guide value: @whi@" + Misc.insertCommas(Long.toString(totalValue)) + "@or1@ (approximate)", 51722);
    }
    public static void takeAll(Player c) {
        if (c.deathItems.isEmpty()) {
            c.sendMessage("You have no items to retrieve.");
            return;
        }
        Iterator<Item> it = c.deathItems.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            if (c.getItems().addItem(item.id, item.amount)) {
                it.remove();
            } else {
                c.sendMessage("Inventory full! Could not take all items.");
                break;
            }
        }
        refresh(c);
    }
    public static void destroyAll(Player c) {
        if (c.deathItems.isEmpty()) {
            c.sendMessage("You have no items to destroy.");
            return;
        }
        Iterator<Item> it = c.deathItems.iterator();
        while (it.hasNext()) {
            Item item = it.next();
            c.deathItems.remove(it.next());
        }
        c.sendMessage("The items have been permanently destroyed.");
        refresh(c);
    }
    public static void destroyItem(Player c, int slot) {
        if (slot >= 0 && slot < c.deathItems.size()) {
            c.deathItems.remove(slot);
            c.sendMessage("The item has been permanently destroyed.");
            refresh(c);
        }
    }
    /**
     * Handles clicking a specific item in the list
     */
    public static void withdrawItem(Player c, int slot) {
        if (slot < 0 || slot >= c.deathItems.size()) return;
        
        Item item = c.deathItems.get(slot);
        if (c.getItems().addItem(item.id, item.amount)) {
            c.deathItems.remove(slot);
            refresh(c);
        } else {
            c.sendMessage("You don't have enough inventory space.");
        }
    }
}