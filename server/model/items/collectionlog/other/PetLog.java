package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class PetLog implements CollectionLogData {

    private static final int[][] entries = {
			{13262, 0, 0},
			{13178, 1, 0},
			{13247, 2, 0},
			{11995, 3, 0},
			{12651, 4, 0},
			{12643, 5, 0},
			{12644, 6, 0},
			{12645, 7, 0},
			{13225, 8, 0},
			{12650, 9, 0},
			{12646, 10, 0},
			{12647, 11, 0},
			{12653, 12, 0},
			{12655, 13, 0},
			{12649, 14, 0},
			{12652, 15, 0},
			{12648, 16, 0},
			{12921, 17, 0}
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