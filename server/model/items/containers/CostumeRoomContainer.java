package server.model.items.containers;

import server.model.players.Player;

/**
 * Abstract class to handle all POH Costume Room storage logic.
 */
public abstract class CostumeRoomContainer {

    // These must be defined by each specific box (FancyDressBox, ArmourCase, etc.)
    public abstract String getContainerName();
    public abstract int getInterfaceId();
    public abstract int getTitleId();
    public abstract int getInventoryId();
    public abstract CostumeData[] getCostumeData();
    public abstract int getMaxSets(Player c);

    public interface CostumeData {
        int getLabelId();
        int getItemInterfaceId();
        int[] getItems();
        String getName();
        boolean contains(int itemId);
    }

    public void open(Player c) {
        for (CostumeData costume : getCostumeData()) {
            updateCostumeDisplay(c, costume);
        }
        updateProgress(c);
        c.getPA().showInterface(getInterfaceId());
        c.getOutStream().createFrame(248);
        c.getOutStream().writeWordA(getInterfaceId());
        c.getOutStream().writeWord(getInventoryId());
        c.flushOutStream();
        c.isInInterface = getInterfaceId();
        c.getItems().resetItems(5064);
    }
    public enum BoxTier {
        NONE(0), OAK(2), TEAK(4), MAHOGANY(8);

        public int limit;
        BoxTier(int limit) { this.limit = limit; }

        // Helper to convert the saved ID to the Enum
        public static BoxTier forId(int id) {
            switch(id) {
                case 1: return OAK;
                case 2: return TEAK;
                case 3: return MAHOGANY;
                default: return NONE;
            }
        }
    }

    public void updateCostumeDisplay(Player c, CostumeData costume) {
        boolean allStored = true;
        for (int i = 0; i < costume.getItems().length; i++) {
            int itemId = costume.getItems()[i];
            int amountStored = c.getFancyBox().getAmount(itemId); 
            c.getPA().sendFrame34a(costume.getItemInterfaceId(), itemId, i, amountStored);
            if (amountStored == 0) allStored = false;
        }
        String color = allStored ? "<col=FF981F>" : "<col=656565>";
        c.getPA().sendFrame126(color + costume.getName(), costume.getLabelId());
    }

    public void updateProgress(Player c) {
        int started = 0;
        for (CostumeData costume : getCostumeData()) {
            if (isSetStarted(c, costume)) started++;
        }
        c.getPA().sendFrame126(getContainerName() + " (" + started + " / " + getCostumeData().length + ")", getTitleId());
    }

    public void deposit(Player c, int itemId, int amount) {
        for (CostumeData costume : getCostumeData()) {
            if (costume.contains(itemId)) {
                if (!isSetStarted(c, costume) && getStartedCount(c) >= getMaxSets(c)) {
                    c.sendMessage("Your box can only hold " + getMaxSets(c) + " types of costumes.");
                    return;
                }
                c.getFancyBox().addStored(itemId, amount);
                c.getItems().deleteItem(itemId, amount);
                updateCostumeDisplay(c, costume);
                updateProgress(c);
                c.getItems().resetItems(5064);
                return;
            }
        }
    }

    public void handleWithdraw(Player c, int itemId, int interfaceId, int amount) {
        for (CostumeData costume : getCostumeData()) {
            if (costume.getItemInterfaceId() == interfaceId) {
                int stored = c.getFancyBox().getAmount(itemId);
                if (stored <= 0) return;

                int withdrawAmount = Math.min(amount, Math.min(stored, c.getItems().freeSlots()));

                if (withdrawAmount > 0) {
                    c.getFancyBox().removeStored(itemId, withdrawAmount);
                    c.getItems().addItem(itemId, withdrawAmount);
                    updateCostumeDisplay(c, costume);
                    updateProgress(c);
                    c.getItems().resetItems(5064);
                }
                return;
            }
        }
    }

    private int getStartedCount(Player c) {
        int count = 0;
        for (CostumeData costume : getCostumeData()) {
            if (isSetStarted(c, costume)) count++;
        }
        return count;
    }

    private boolean isSetStarted(Player c, CostumeData costume) {
        for (int id : costume.getItems()) {
            if (c.getFancyBox().getAmount(id) > 0) return true;
        }
        return false;
    }
}