package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class MiscLog implements CollectionLogData {

    private static final int[][] entries = {
			{13576, 0, 0},
			{7991, 1, 0},
			{7993, 2, 0},
			{7989, 3, 0},
			{10976, 4, 0},
			{10977, 5, 0},
			{9044, 6, 0},
			{11338, 7, 0},
			{11335, 8, 0},
			{2366, 9, 0},
			{1249, 10, 0},
			{19707, 11, 0},
			{11015, 12, 0},
			{11017, 13, 0},
			{11018, 14, 0},
			{11016, 15, 0},

			{9007, 16, 0},
			{9008, 17, 0},
			{9010, 18, 0},
			{9011, 19, 0},
			{7536, 20, 0},
			{7538, 21, 0},
			{6571, 22, 0}
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
    public void setAmount(int index, int amount) {
        if (index >= 0 && index < entries.length) {
        	entries[index][2] = amount;
        }
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
    public int getMaxItems() {
        return entries.length;
    }

    @Override
    public int[][] getEntries() {
        return entries;
    }
}