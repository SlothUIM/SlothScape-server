package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class ChompyLog implements CollectionLogData {

    private static final int[][] entries = {
			{2978, 0, 0},
			{2979, 1, 0},
			{2980, 2, 0},
			{2981, 3, 0},
			{2982, 4, 0},
			{2983, 5, 0},
			{2984, 6, 0},
			{2985, 7, 0},
			{2986, 8, 0},
			{2987, 9, 0},
			{2988, 10, 0},
			{2989, 11, 0},
			{2990, 12, 0},
			{2991, 13, 0},
			{2992, 14, 0},
			{2993, 15, 0},
			{2994, 16, 0},
			{2995, 17, 0}
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
    public int getMaxItems() {
        return entries.length;
    }

    @Override
    public int[][] getEntries() {
        return entries;
    }
}