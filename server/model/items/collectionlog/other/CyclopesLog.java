package server.model.items.collectionlog.other;

import server.model.items.collectionlog.CollectionLogData;

public class CyclopesLog implements CollectionLogData {

    private static final int[][] entries = {
            {8844, 0, 0},
            {8845, 1, 0},
            {8846, 2, 0},
            {8847, 3, 0},
            {8848, 4, 0},
            {8849, 5, 0},
            {8850, 6, 0},
            {12954, 7, 0}
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
    public void setAmount(int index, int amount) {
        if (index >= 0 && index < entries.length) {
        	entries[index][2] = amount;
        }
    }
    @Override
    public int[][] getEntries() {
        return entries;
    }
}