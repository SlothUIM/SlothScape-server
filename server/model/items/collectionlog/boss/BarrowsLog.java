package server.model.items.collectionlog.boss;

import server.model.items.collectionlog.CollectionLogData;

public class BarrowsLog implements CollectionLogData {

    private static final int[][] entries = {
            {4732, 0, 0},
            {4708, 1, 0},
            {4716, 2, 0},
            {4724, 3, 0},
            {4745, 4, 0},
            {4753, 5, 0},
            {4736, 6, 0},
            {4712, 7, 0},
            {4720, 8, 0},
            {4728, 9, 0},
            {4749, 10, 0},
            {4757, 11, 0},
            {4738, 12, 0},
            {4714, 13, 0},
            {4722, 14, 0},
            {4730, 15, 0},
            {4751, 16, 0},
            {4759, 17, 0},
            {4734, 18, 0},
            {4710, 19, 0},
            {4718, 20, 0},
            {4726, 21, 0},
            {4747, 22, 0},
            {4755, 23, 0},
            {4740, 24, 0}
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
    private int killCount = 0;

    public void incrementKillCount() {
        killCount++;
    }

    @Override
    public int getKillCount() {
        return killCount;
    }

    @Override
    public void setKillCount(int kc) {
        killCount = kc;

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
