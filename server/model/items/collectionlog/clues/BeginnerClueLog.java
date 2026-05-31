package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class BeginnerClueLog implements CollectionLogData {

    private static final int[][] entries = {
			{10836, 0, 0},
			{10837, 1, 0},
			{10838, 2, 0},
			{10839, 3, 0},
			{10840, 4, 0},
			{12297, 5, 0}
    };

    @Override
    public int getTotalUnlocked() {
        int unlocked = 0;
        for (int[] item : entries) {
            if (item[2] > 0)
                unlocked++;
        }
        return unlocked;
    }

    @Override
    public void setAmount(int index, int amount) {
        if (index >= 0 && index < entries.length) {
        	entries[index][2] = amount;
        }
    }
    @Override
    public int getMaxItems() {
        return entries.length;
    }
    @Override
    public void addItem(int itemId, int amount) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i][0] == itemId) {
                entries[i][2] += amount; // increase the amount collected
                return;
            }
        }
        // Optionally, handle item not found in the log
    }
    @Override
    public void setKillCount(int kc) {

    }
    @Override
    public int getKillCount() {
        return 0;
    }

    @Override
    public int[][] getEntries() {
        return entries;
    }
}