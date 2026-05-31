package server.model.items.containers;

import server.model.players.Player;

public class FancyDressBox extends CostumeRoomContainer {

    public enum FancyCostume implements CostumeData {
        BEEKEEPER(19520, 19521, new int[]{9629, 9628, 9630, 9631, 10665}, "Beekeeper's Outfit"),
        CAMO(19523, 19524, new int[]{6656, 6654, 6655}, "Camo outfit"),
        FROG(19526, 19527, new int[]{6188, 6184, 6185, 6186, 6187}, "Frog costume"),
        LEDERHOSEN(19529, 19530, new int[]{6182, 6180, 6181}, "Lederhosen outfit"),
        MIME(19532, 19533, new int[]{3057, 3058, 3059, 3060, 3061}, "Mime costume"),
        SHADE(19535, 19536, new int[]{546, 548}, "Shade robes"),
        BAGUETTE(19538, 19539, new int[]{20590}, "Stale baguette"),
        ZOMBIE(19541, 19542, new int[]{7594, 7592, 7593, 7595, 7596}, "Zombie outfit");

        private final int labelId, itemInterfaceId;
        private final int[] items;
        private final String name;

        FancyCostume(int labelId, int itemInterfaceId, int[] items, String name) {
            this.labelId = labelId;
            this.itemInterfaceId = itemInterfaceId;
            this.items = items;
            this.name = name;
        }

        @Override public int getLabelId() { return labelId; }
        @Override public int getItemInterfaceId() { return itemInterfaceId; }
        @Override public int[] getItems() { return items; }
        @Override public String getName() { return name; }
        @Override public boolean contains(int itemId) {
            for (int id : items) if (id == itemId) return true;
            return false;
        }
    }

    @Override public String getContainerName() { return "Fancy Dress Box"; }
    @Override public int getInterfaceId() { return 19543; }
    @Override public int getTitleId() { return 19544; }
    @Override public int getInventoryId() { return 5063; }
    @Override public CostumeData[] getCostumeData() { return FancyCostume.values(); }

    @Override
    public int getMaxSets(Player c) {
        // Enforces POH material limits
        switch(c.getFancyBox().getDressBoxTier()) {
            case 1: return 2; // Oak
            case 2: return 4; // Teak
            case 3: return 8; // Mahogany
            default: return 0;
        }
    }
}