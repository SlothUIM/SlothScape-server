package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class RandomEventLog implements CollectionLogData {

    private static final int[][] entries = {
			{6654, 0, 0},
			{6655, 1, 0},
			{6656, 2, 0},

			{6180, 3, 0},
			{6181, 4, 0},
			{6182, 5, 0},

			{7594, 6, 0},
			{7592, 7, 0},
			{7593, 8, 0},
			{7595, 9, 0},
			{7596, 10, 0},

			{3057, 11, 0},
			{3058, 12, 0},
			{3059, 13, 0},
			{3060, 14, 0},
			{3061, 15, 0},
			{6183, 16, 0},

			{6345, 17, 0},
			{6341, 18, 0},
			{6343, 19, 0},
			{6347, 20, 0},
			{6349, 21, 0}
    };
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
    public int[][] getEntries() {
        return entries;
    }
}