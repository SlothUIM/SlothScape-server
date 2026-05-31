package server.model.players.skills.construction.sawmill;

import server.model.players.Player;
import server.world.World;

import java.util.HashMap;
import java.util.Map;

public final class Sawmill {

    private static final int CHATBOX_IFACE_ID = 44720;
    private static final int FIRST_ITEM_CHILD = 44734;
    private static final int SLOTS = 8;
    private static final int CLEAR_ITEM_ID = 65535;

    private static final int BTN_MAKE1_BASE = 44724; 
    private static final int BTN_MAKE5_BASE = 44721; 
    private static final int BTN_MAKE10_BASE = 44722;
    private static final int BTN_MAKE_ALL_BASE = 44723; // Check if your interface has "All" buttons here

    private static final int COINS = 995;

    private static final Map<Integer, SawmillData> DATA_BY_LOG = new HashMap<>();

    static {
        register(new SawmillData("Wood",      1511, 960,  100));
        register(new SawmillData("Oak",       1521, 8778, 250));
        register(new SawmillData("Teak",      6333, 8780, 500));
        register(new SawmillData("Mahogany",  6332, 8782, 1500));
    }

    private static final int[] SHOWABLE_LOGS = { 1511, 1521, 6333, 6332 };

    public static void open(Player c) {
        for (int i = 0; i < SLOTS; i++) {
            c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, CLEAR_ITEM_ID);
            c.boltEnchantDisplayed[i] = 0;
        }

        int filled = 0;
        for (int logId : SHOWABLE_LOGS) {
            if (filled >= SLOTS) break;
            if (!c.getItems().playerHasItem(logId, 1)) continue;

            c.getPA().sendFrame246(FIRST_ITEM_CHILD + filled, 225, logId);
            c.boltEnchantDisplayed[filled] = logId;
            filled++;
        }

        if (filled == 0) {
            c.sendMessage("You don't have any logs to turn into planks.");
            return;
        }

        c.boltEnchantCount = filled;
        c.getPA().sendFrame126("What would you like to make?", 44732);
        c.activeAction = Player.ChatboxAction.SAWMILL;
        c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
    }

    public static boolean handleButton(Player c, int buttonId) {
        int slotIndex = -1;
        int amount = 0;
        if (buttonId >= BTN_MAKE1_BASE && buttonId < BTN_MAKE1_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE1_BASE;
            if (slotIndex != -1 && slotIndex < c.boltEnchantCount) {
                int resultId = c.boltEnchantDisplayed[slotIndex];
                if(c.toMake <= c.getItems().getItemAmount(amount))
                	convertInstant(c, resultId, c.toMake);
                else
                	convertInstant(c, resultId, c.getItems().getItemAmount(resultId));
                return true;
            }
        } else if (buttonId == BTN_MAKE5_BASE) {
        	c.toMake = 1;
            return true;
        } else if (buttonId == BTN_MAKE10_BASE ) {
        	c.xInterfaceId = buttonId;
            return true;
        } else if (buttonId == BTN_MAKE_ALL_BASE) {
            c.toMake = 28;
            return true;
        }

       
        return false;
    }

    /**
     * Instantly replaces logs with planks in the inventory
     */
    public static void convertInstant(Player c, int logId, int requestedAmount) {
        SawmillData data = DATA_BY_LOG.get(logId);
        if (data == null) return;

        int logCount = c.getItems().getItemCount(logId, false);
        int coinCount = c.getItems().getItemCount(COINS, false);
        
        // Calculate the maximum the player can afford
        int canAfford = coinCount / data.cost;
        
        // The final amount to convert
        int actualToMake = Math.min(requestedAmount, Math.min(logCount, canAfford));

        if (actualToMake <= 0) {
            if (logCount <= 0) {
                c.sendMessage("You have run out of " + data.name + " logs.");
            } else {
                c.sendMessage("You need " + data.cost + " coins per plank. You can't afford any.");
            }
            return;
        }

        // Perform the instant replacement
        c.getItems().deleteItem(logId, actualToMake);
        c.getItems().deleteItem(COINS, (data.cost * actualToMake));
        c.getItems().addItem(data.plankId, actualToMake);
        if(actualToMake > 2)
        	World.getWorld().getItemHandler().createGroundItem(c, 9468, c.getX(), c.getY(), c.getHeight(), 3, c.getIndex());
        else
        	World.getWorld().getItemHandler().createGroundItem(c, 9468, c.getX(), c.getY(), c.getHeight(), actualToMake, c.getIndex());
        c.getPA().removeAllWindows();
    }

    private static void register(SawmillData d) {
        DATA_BY_LOG.put(d.logId, d);
    }

    private static final class SawmillData {
        final String name;
        final int logId, plankId, cost;

        SawmillData(String name, int logId, int plankId, int cost) {
            this.name = name;
            this.logId = logId;
            this.plankId = plankId;
            this.cost = cost;
        }
    }
}