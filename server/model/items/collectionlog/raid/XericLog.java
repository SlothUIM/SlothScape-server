package server.model.items.collectionlog.raid;

import server.model.items.collectionlog.CollectionLogData;

public class XericLog implements CollectionLogData {

    private static final int[][] entries = {
			{20851, 0, 0},
			{22386, 1, 0},
			{20997, 2, 0},
			{21003, 3, 0},
			{21043, 4, 0},
			{20784, 5, 0},
			{21018, 6, 0},
			{21021, 7, 0},
			{21024, 8, 0},
			{21034, 9, 0},
			{21079, 10, 0},
			
			{21012, 11, 0},
			{21000, 12, 0},
			
			{21047, 13, 0},
			{21027, 14, 0},
			{6573, 15, 0},
			{24670, 16, 0},
			{22388, 17, 0},
			{22390, 18, 0},
			{22392, 19, 0},
			{22394, 20, 0},
			{22396, 21, 0}
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
    public int getKillCount() {
        return 0;
    }
    @Override
    public void setKillCount(int kc) {

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