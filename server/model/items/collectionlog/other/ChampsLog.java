package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class ChampsLog implements CollectionLogData {

    private static final int[][] entries = {
			{6798, 0, 0},
			{6799, 1, 0},
			{6800, 2, 0},
			{6801, 3, 0},
			{6802, 4, 0},
			{6803, 5, 0},
			{6804, 6, 0},
			{6805, 7, 0},
			{6806, 8, 0},
			{6807, 9, 0},
			{6808, 10, 0},
			{21439,11, 0}
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