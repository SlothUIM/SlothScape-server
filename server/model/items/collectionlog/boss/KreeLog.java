package server.model.items.collectionlog.boss;

import server.model.items.collectionlog.CollectionLogData;

public class KreeLog implements CollectionLogData {

    private static final int[][] entries = {

			{12649, 0, 0},
			{11826, 1, 0},
			{11828, 2, 0},
			{11830, 3, 0},
			{11810, 4, 0},
			{11818, 5, 0},
			{11820, 6, 0},
			{11822, 7, 0}
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
    public void setAmount(int index, int amount) {
        if (index >= 0 && index < entries.length) {
        	entries[index][2] = amount;
        }
    }
    @Override
    public int getMaxItems() {
        return entries.length;
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