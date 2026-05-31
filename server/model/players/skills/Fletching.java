package server.model.players.skills;

import server.Config;
import server.model.players.Player;
import java.util.HashMap;
import java.util.Map;

public final class Fletching {

    private static final int CHATBOX_IFACE_ID = 44720;
    private static final int FIRST_ITEM_CHILD = 44734;
    private static final int SLOTS = 8;
    private static final int CLEAR_ITEM_ID = 65535;
    private static final int ARROW_SHAFTS = 52;
    
    private static final int BTN_MAKE1_BASE = 44724; 
    private static final int BTN_MAKE5_BASE = 44721; 
    private static final int BTN_MAKE10_BASE = 44722;
    private static final int BTN_MAKE_ALL_BASE = 44723;
    
    private static final Map<Integer, FletchableLog> LOG_DATA = new HashMap<>();

    static {
        // Log ID, Shortbow(u), Longbow(u), Stock, Shield, Level Req (Short/Long/Stock/Shield), Exp, Shaft Amount
        register(new FletchableLog(1511, 50, 48, 9440, -1, new int[]{5, 10, 9, -1}, new int[]{5, 10, 6, 0}, 15));   // Normal
        register(new FletchableLog(1521, 54, 56, 9442, 22251, new int[]{20, 25, 24, 27}, new int[]{16, 25, 16, 25}, 30)); // Oak
        register(new FletchableLog(1519, 60, 58, 9444, 22254, new int[]{35, 40, 39, 42}, new int[]{33, 41, 22, 33}, 45)); // Willow
        register(new FletchableLog(1517, 64, 62, 9448, 22257, new int[]{50, 55, 54, 57}, new int[]{50, 58, 32, 50}, 60)); // Maple
        register(new FletchableLog(1515, 68, 66, 9452, 22260, new int[]{65, 70, 69, 72}, new int[]{67, 75, 45, 67}, 75)); // Yew
        register(new FletchableLog(1513, 72, 70, -1, 22263, new int[]{80, 85, -1, 87}, new int[]{83, 91, 0, 83}, 90));    // Magic
    }

    public static void open(Player c, int logId) {
        FletchableLog data = LOG_DATA.get(logId);
        if (data == null) return;

        for (int i = 0; i < SLOTS; i++) {
            c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, CLEAR_ITEM_ID);
            c.boltEnchantDisplayed[i] = 0;
        }

        int filled = 0;
        // Arrow Shafts are ALWAYS first [Slot 0]
        int[] options = {ARROW_SHAFTS, data.shortBow, data.longBow, data.stock, data.shield};
        
        for (int itemId : options) {
            if (itemId <= 0 || filled >= SLOTS) continue;
            c.getPA().sendFrame246(FIRST_ITEM_CHILD + filled, 190, itemId);
            c.boltEnchantDisplayed[filled] = itemId;
            filled++;
        }

        c.selectedLog = logId;
        c.boltEnchantCount = filled;
        c.activeAction = Player.ChatboxAction.FLETCHING;
        c.getPA().sendFrame126("What would you like to fletch?", 44732);
        c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
    }

    public static boolean handleButton(Player c, int buttonId) {
        int slotIndex = -1;
        int amount = 0;
        if (buttonId >= BTN_MAKE1_BASE && buttonId < BTN_MAKE1_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE1_BASE;
            if (slotIndex != -1 && slotIndex < c.boltEnchantCount) {
                int resultId = c.boltEnchantDisplayed[slotIndex];
                fletchInstant(c, c.selectedLog, resultId, c.toMake);
                return true;
            }
        } else if (buttonId == BTN_MAKE5_BASE) {
        	c.toMake = 1;
            return true;
        } else if (buttonId == BTN_MAKE10_BASE ) {
        	c.toMake = 10;
            return true;
        } else if (buttonId == BTN_MAKE_ALL_BASE) {
            c.toMake = 28;
            return true;
        }

       
        return false;
    }

    private static void fletchInstant(Player c, int logId, int resultId, int requestedAmount) {
        FletchableLog data = LOG_DATA.get(logId);
        if (data == null) return;

        int yieldMultiplier = 1;
        int levelReq = 1;
        double xpPerLog = 0;

        // Handle scaling Arrow Shafts
        if (resultId == ARROW_SHAFTS) {
            yieldMultiplier = data.shaftYield;
            levelReq = 1;
            xpPerLog = 5; // Standard XP for making shafts
        } 
        // Handle Bows/Stocks/Shields
        else if (resultId == data.shortBow) { levelReq = data.levels[0]; xpPerLog = data.exps[0]; }
        else if (resultId == data.longBow) { levelReq = data.levels[1]; xpPerLog = data.exps[1]; }
        else if (resultId == data.stock) { levelReq = data.levels[2]; xpPerLog = data.exps[2]; }
        else if (resultId == data.shield) { levelReq = data.levels[3]; xpPerLog = data.exps[3]; }

        if (c.getSkills().getLevel(Skill.FLETCHING) < levelReq) {
            c.sendMessage("You need a Fletching level of " + levelReq + " to fletch this.");
            return;
        }

        int logCount = c.getItems().getItemCount(logId, false);
        int actualToMake = Math.min(requestedAmount, logCount);

        if (actualToMake <= 0) {
            c.sendMessage("You have run out of logs.");
            return;
        }

        c.getItems().deleteItem(logId, actualToMake);
        c.getItems().addItem(resultId, actualToMake * yieldMultiplier);
        c.getSkills().addExperience((int)(xpPerLog * actualToMake * Config.FLETCHING_EXPERIENCE), Skill.FLETCHING);

        String itemName = c.getItems().getItemName(resultId);
        c.sendMessage("You fletch " + actualToMake + " logs into " + (actualToMake * yieldMultiplier) + " " + itemName + "s.");
        c.getPA().removeAllWindows();
    }

    private static void register(FletchableLog d) {
        LOG_DATA.put(d.logId, d);
    }

    private static final class FletchableLog {
        final int logId, shortBow, longBow, stock, shield, shaftYield;
        final int[] levels;
        final int[] exps;

        FletchableLog(int logId, int shortBow, int longBow, int stock, int shield, int[] levels, int[] exps, int shaftYield) {
            this.logId = logId;
            this.shortBow = shortBow;
            this.longBow = longBow;
            this.stock = stock;
            this.shield = shield;
            this.levels = levels;
            this.exps = exps;
            this.shaftYield = shaftYield;
        }
    }
}