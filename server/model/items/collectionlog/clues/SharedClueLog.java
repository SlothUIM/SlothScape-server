package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class SharedClueLog implements CollectionLogData {

    private static final int[][] entries = {
			{3827, 0, 0},
			{3831, 1, 0},
			{3835, 2, 0},
			{3828, 3, 0},
			{3832, 4, 0},

			{3836, 5, 0},//
			{3829, 6, 0},//
			{3833, 7, 0},//
			{3837, 8, 0},//

			{3830, 9, 0},//
			{3834, 10, 0},//
			{3838, 11, 0},//

			{12402, 12, 0},//		
			{12403, 13, 0},//
			{12404, 14, 0},//
			{12405, 15, 0},//
			{12406, 16, 0},//
			{12407, 17, 0},//
			{12408, 18, 0},//
			{12409, 19, 0},//
			{12410, 20, 0},//
			{12411, 21, 0},//

			{7329, 22, 0},//
			{7330, 23, 0},//
			{7331, 24, 0},//
			{10326, 25, 0},//
			{10327, 26, 0},//
			{10476, 27, 0}
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