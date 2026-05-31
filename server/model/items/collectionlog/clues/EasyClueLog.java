package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class EasyClueLog implements CollectionLogData {

    private static final int[][] entries = {
			{2587, 0, 0},
			{2583, 1, 0},
			{2585, 2, 0},
			{3472, 3, 0},
			{2589, 4, 0},
			{2595, 5, 0},
			{2591, 6, 0},
			{2593, 7, 0},
			{3473, 8, 0},
			{2597, 9, 0},
			{10665, 10, 0},
			{10668, 11, 0},
			{10671, 12, 0},
			{10674, 13, 0},
			{10677, 14, 0},
			{10699, 15, 0},
			{10700, 16, 0},
			{10701, 17, 0},
			{10702, 18, 0},
			{10703, 19, 0},

			{12221, 20, 0},
			{12215, 21, 0},
			{12217, 22, 0},
			{12219, 23, 0},
			{12223, 24, 0},

			{12211, 25, 0},
			{12205, 26, 0},
			{12207, 27, 0},
			{12209, 28, 0},
			{12213, 29, 0},

			{12231, 30, 0},
			{12225, 31, 0},
			{12227, 32, 0},
			{12229, 33, 0},
			{12233, 34, 0},

			{12241, 35, 0},
			{12235, 36, 0},
			{12237, 37, 0},
			{12239, 38, 0},
			{12243, 39, 0},

			{7362, 40, 0},
			{7366, 41, 0},
			{7364, 42, 0},
			{7368, 43, 0},
			{7394, 44, 0},
			{7390, 45, 0},
			{7386, 46, 0},
			{7396, 47, 0},
			{7392, 48, 0},
			{7388, 49, 0},
			{10458, 50, 0},
			{10464, 51, 0},
			{10462, 52, 0},
			{10466, 53, 0},
			{10460, 54, 0},
			{10468, 55, 0},
			{10316, 56, 0},
			{10318, 57, 0},
			{10320, 58, 0},
			{10322, 59, 0},
			{10324, 60, 0},
			{2631, 61, 0},
			{2633, 62, 0},
			{2635, 63, 0},
			{2637, 64, 0},
			{12247, 65, 0},
			{10392, 66, 0},
			{10398, 67, 0},
			{10394, 68, 0},
			{10396, 69, 0},
			{10404, 70, 0},
			{10424, 71, 0},
			{10406, 72, 0},
			{10426, 73, 0},
			{10412, 74, 0},
			{10432, 75, 0},
			{10414, 76, 0},
			{10434, 77, 0},
			{10408, 78, 0},
			{10428, 79, 0},
			{10410, 80, 0},
			{10430, 81, 0},
			{10366, 82, 0},
			{12297, 83, 0},
			{10280, 84, 0}
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
