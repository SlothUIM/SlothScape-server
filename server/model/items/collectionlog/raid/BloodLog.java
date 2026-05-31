package server.model.items.collectionlog.raid;

import server.model.items.collectionlog.CollectionLogData;

public class BloodLog implements CollectionLogData {

    private static final int[][] entries = {
			{22473, 0, 0},
			{22325, 1, 0},
			{22324, 2, 0},
			{22481, 2, 0},
			{22419, 2, 0},
			{22420, 2, 0},
			{22421, 2, 0},
			{22477, 2, 0},
			{22446, 2, 0},
			
			{22494, 2, 0},
			{22496, 2, 0},
			{22498, 2, 0},
			{22500, 2, 0},
			{22502, 2, 0}
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