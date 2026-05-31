package server.model.items.collectionlog.boss;

import server.model.items.collectionlog.CollectionLogData;

public class SkotizoLog implements CollectionLogData {

    private static final int[][] entries = {
			{21273, 0, 0},
			{19701, 1, 0},
			{21275, 2, 0},
			{19685, 3, 0},
			{6571, 4, 0},
			{19677, 5, 0}
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
    public int getMaxItems() {
        return entries.length;
    }
    @Override
    public void setAmount(int index, int amount) {
        if (index >= 0 && index < entries.length) {
        	entries[index][2] = amount;
        }
    }
    private int killCount = 0;

    public void incrementKillCount() {
        killCount++;
    }
    @Override
    public void setKillCount(int kc) {
        killCount = kc;

    }
    @Override
    public int getKillCount() {
        return killCount;
    }

	
    @Override
    public int[][] getEntries() {
        return entries;
    }
}