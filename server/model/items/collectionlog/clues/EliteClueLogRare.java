package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class EliteClueLogRare implements CollectionLogData {

    private static final int[][] entries = {
			{22840, 0, 0},
			{22846, 1, 0},
			{22844, 2, 0},
			{22842, 3, 0},
			{22838, 4, 0},
			{13258, 5, 0},
			{13259, 6, 0},
			{13260, 7, 0},
			{13261, 8, 0}
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
    public int getMaxItems() {
        return entries.length;
    }

    @Override
    public int[][] getEntries() {
        return entries;
    }
}