package server.model.players.skills.farming;

import lombok.Getter;
import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;

import java.util.HashMap;
import java.util.Map;

public class Compost {

    private Player player;

    // OSRS uses Config 511 for the 4 main compost bins!
    private static final int MAIN_CONFIG_ID = 1057;

    public static final double COMPOST_EXP_RETRIEVE = 4.5;
    public static final double SUPER_COMPOST_EXP_RETRIEVE = 8.5;
    public static final double COMPOST_EXP_USE = 18;
    public static final double SUPER_COMPOST_EXP_USE = 26;
    public static final double ROTTEN_TOMATOES_EXP_RETRIEVE = 8.5;

    public static final int NORMAL = 0;
    public static final int SUPER = 1;
    public static final int TOMATOES = 2;

    public static final int COMPOST = 6032;
    public static final int SUPER_COMPOST = 6034;
    public static final int ROTTE_TOMATO = 2518;
    public static final int TOMATO = 1982;

    public static final int[] COMPOST_ORGANIC = { 6055, 1942, 1957, 1965, 5986,
            5504, 5982, 249, 251, 253, 255, 257, 2998, 259, 261, 263, 3000,
            265, 2481, 267, 269, 1951, 753, 2126, 247, 239, 6018 };

    public static final int[] SUPER_COMPOST_ORGANIC = { 2114, 5978, 5980, 5982,
            6004, 247, 6469 };

    // We track the 4 main bins
    private CompostBinState[] bins = new CompostBinState[4];

    public Compost(Player player) {
        this.player = player;
        for (int i = 0; i < bins.length; i++) {
            bins[i] = new CompostBinState();
        }
    }

    // --- ENUM PROPERLY SEPARATED ---
    public enum CompostBinLocations {
        NORTH_ARDOUGNE(0, 7839, 3, 2661, 3375),
        PHASMATYS(1, 7837, 1, 3610, 3522),
        FALADOR(2, 7837, 4, 3056, 3312),
        CATHERBY(3, 7837, 3, 2804, 3464);

        @Getter
        public int compostIndex;
        public int binObjectId;
        public int objectFace;
        public int x, y, z;
        public static Map<Integer, CompostBinLocations> bins = new HashMap<>();

        static {
            for (CompostBinLocations data : CompostBinLocations.values()) {
                bins.put(data.compostIndex, data);
            }
        }

        CompostBinLocations(int compostIndex, int binObjectId, int objectFace, int x, int y) {
            this.compostIndex = compostIndex;
            this.binObjectId = binObjectId;
            this.objectFace = objectFace;
            this.x = x;
            this.y = y;
        }

        public static CompostBinLocations forPosition(int x, int y) {
            for (CompostBinLocations loc : CompostBinLocations.values()) {
                if (loc.x == x && loc.y == y) {
                    return loc;
                }
            }
            return null;
        }

    }

    // --- COMPOST STATE DATA ---
    public static class CompostBinState {
        int produceCount = 0;
        int stage = 0;        // 0 = filling, 1 = closed/rotting, 2 = done rotting, 3 = opened for retrieval
        int currentType = NORMAL;

        public int getConfigValue() {
            if (produceCount == 0 && stage == 0) return 0;

            if (stage == 0) {
                int value = produceCount;
                if (currentType == SUPER) value |= 1 << 5;
                else if (currentType == TOMATOES) value |= 1 << 7;
                return value;
            } else if (stage == 1) {
                return 31; // Visually Closed
            } else if (stage == 2 || stage == 3) { // 2 = Done Rotting, 3 = Opened
                if (currentType != TOMATOES)
                    return (1 << 5) | (1 << 4) | (produceCount - 1);
                else
                    return 144 | (produceCount - 1);
            }
            return 0;
        }
    }
    // --- CORE MECHANICS ---
    public void fillCompostBin(int x, int y, final int organicItemUsed) {
        final CompostBinLocations loc = CompostBinLocations.forPosition(x, y);
        if (loc == null) return;

        final int index = loc.getCompostIndex();
        final CompostBinState bin = bins[index];

        int type = -1;
        for (int normalCompost : COMPOST_ORGANIC) if (organicItemUsed == normalCompost) type = NORMAL;
        for (int superCompost : SUPER_COMPOST_ORGANIC) if (organicItemUsed == superCompost) type = SUPER;
        if (organicItemUsed == TOMATO) type = TOMATOES;

        if (type == -1) {
            player.sendMessage("You need to put organic items into the compost bin in order to make compost.");
            return;
        }
        if (bin.stage != 0) {
            player.sendMessage("You can't add anything to a closed or rotting compost bin.");
            return;
        }
        if (bin.produceCount > 0 && bin.currentType != type) {
            player.sendMessage("You can't mix different types of compost in the bin.");
            return;
        }

        final int compostType = type;
        player.turnPlayerTo(x, y);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (bin.produceCount >= 15) {
                    player.sendMessage("The compost bin is already full.");
                    container.stop();
                    return;
                }
                if (!player.getItems().playerHasItem(organicItemUsed)) {
                    container.stop();
                    return;
                }

                player.startAnimation(832);
                bin.currentType = compostType;
                bin.produceCount++;
                bin.stage = 0;

                player.getItems().deleteItem(organicItemUsed, 1);
                updateAllBinConfigs(); // Global visual update!
            }
            @Override
            public void stop() {}
        }, 2);
    }

    public void closeCompostBin(final int index) {
        final CompostBinState bin = bins[index];

        if (bin.produceCount == 0) {
            player.sendMessage("You can't close an empty compost bin.");
            return;
        }

        bin.stage = 1;
        player.startAnimation(835);
        updateAllBinConfigs();
        player.sendMessage("You close the compost bin, and its content starts to rot.");

        int ticks = 100; // Time it takes to rot (100 = approx 60 seconds)

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (bin.stage == 1) {
                    bin.stage = 2;
                    updateAllBinConfigs(); // Visually turns the bin into "Done Rotting"
                    player.sendMessage("The compost has finished rotting and is ready for collection.");
                }
                container.stop();
            }
            @Override
            public void stop() {}
        }, ticks);
    }

    public void openCompostBin(int index) {
        CompostBinState bin = bins[index];

        if (bin.stage != 2) {
            player.sendMessage("The compost bin isn't finished rotting.");
            return;
        }

        bin.stage = 3;
        player.startAnimation(834);
        updateAllBinConfigs();
        player.sendMessage("You open the compost bin.");
    }

    public void retrieveCompost(int index) {
        if(!player.getItems().playerHasItem(1925, 1)){
            player.sendMessage("You need an empty bucket to retrieve compost.");
            return;
        }
        CompostBinState bin = bins[index];
        if (bin.stage != 3) {
            player.sendMessage("The compost bin must be opened first.");
            return;
        }
        if (bin.produceCount == 0) {
            player.sendMessage("There is no compost left to retrieve.");
            return;
        }

        int itemId = 0;
        switch (bin.currentType) {
            case NORMAL: itemId = COMPOST; break;
            case SUPER: itemId = SUPER_COMPOST; break;
            case TOMATOES: itemId = ROTTE_TOMATO; break;
        }
        if (itemId == 0) return;

        player.startAnimation(832);
        player.getItems().deleteItem(1925, 1);
        player.getItems().addItem(itemId, 1);

        bin.produceCount--;

        // If it was the last bucket, reset the bin to completely empty!
        if (bin.produceCount <= 0) {
            bin.stage = 0;
            bin.currentType = NORMAL;
            bin.produceCount = 0;
            player.sendMessage("You empty the compost bin.");
        }

        updateAllBinConfigs();
    }

    // --- OBJECT HANDLING ---
    public boolean handleItemOnObject(int itemUsed, int objectId, int objectX, int objectY) {
        switch (objectId) {
            case 7839:
            case 7838:
            case 7837:
            case 7836:
                fillCompostBin(objectX, objectY, itemUsed);
                return true;
        }
        return false;
    }

    public boolean handleObjectClick(int objectId, int objectX, int objectY) {
        if(objectId == 7839 || objectId == 7838 || objectId == 7837 || objectId == 7836) {
            CompostBinLocations loc = CompostBinLocations.forPosition(objectX, objectY);
            if (loc == null) {
                return false;
            }

            int index = loc.getCompostIndex();
            CompostBinState bin = bins[index];

            if (bin.stage == 0 && bin.produceCount > 0) {
                closeCompostBin(index);
                return true;
            } else if (bin.stage == 2) {
                openCompostBin(index);
                return true;
            } else if (bin.stage == 3 && bin.produceCount > 0) {
                retrieveCompost(index);
                return true;
            } else if (bin.stage == 0 && bin.produceCount == 0) {
                player.sendMessage("This compost bin is empty.");
                return true;
            }
        }

        return false;
    }

    // --- THE VISUAL FIX ---
    // --- THE REGIONAL FIX ---
    // --- THE VISUAL FIX ---
    // --- THE REGIONAL FIX ---
    public void updateAllBinConfigs() {
        for (CompostBinLocations loc : CompostBinLocations.values()) {
            // Check which bin the player is standing near (within 50 tiles)
            if (player.withinDistance(loc.x, loc.y, player.getX(), player.getY(), 50)) {
                int index = loc.getCompostIndex();
                int configValue = bins[index].getConfigValue();

                // Send the raw value exactly as you originally had it, NO bit-shifts!
                player.getPA().sendConfig(MAIN_CONFIG_ID, configValue);
                return; // We found the local bin and updated it, so we stop searching!
            }
        }
    }
    /*
    kept for reference
     */
    private void updateBinConfigs(int index) {

        int configValue = bins[index].getConfigValue();

        player.getPA().sendConfig(MAIN_CONFIG_ID, configValue);

    }
}