package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class MediumClueLog implements CollectionLogData {

    private static final int[][] entries = {
			{2577, 0, 0},
			{2579, 1, 0},
			{12598, 2, 0},

			{2605, 3, 0},
			{2599, 4, 0},
			{2601, 5, 0},
			{3474, 6, 0},
			{2603, 7, 0},

			{2613, 8, 0},
			{2607, 9, 0},
			{2609, 10, 0},
			{3475, 11, 0},
			{2611, 12, 0},


			{10666, 13, 0},
			{10669, 14, 0},
			{10672, 15, 0},
			{10675, 16, 0},
			{10678, 17, 0},

			{10709, 18, 0},
			{10710, 19, 0},
			{10711, 20, 0},
			{10712, 21, 0},
			{10713, 22, 0},

			{12293, 23, 0},
			{12287, 24, 0},
			{12289, 25, 0},
			{12295, 26, 0},
			{12291, 27, 0},

			{12283, 28, 0},
			{12277, 29, 0},
			{12279, 30, 0},
			{12285, 31, 0},
			{12281, 32, 0},

			{7370, 33, 0},
			{7372, 34, 0},
			{7378, 35, 0},
			{7380, 36, 0},

			{10452, 37, 0},
			{10446, 38, 0},

			{10454, 39, 0},
			{10448, 40, 0},

			{10456, 41, 0},
			{10450, 42, 0},

			{7319, 43, 0},
			{7323, 44, 0},
			{7321, 45, 0},
			{7327, 46, 0},
			{7325, 47, 0},
			{12309, 48, 0},
			{12311, 49, 0},
			{12313, 50, 0},

			{2645, 51, 0},
			{2647, 52, 0},
			{2649, 53, 0},
			{12299, 54, 0},
			{12301, 55, 0},
			{12303, 56, 0},
			{12305, 57, 0},
			{12307, 58, 0},

			{10416, 59, 0},
			{10436, 60, 0},
			{10418, 61, 0},
			{10438, 62, 0},

			{10400, 63, 0},
			{10420, 64, 0},
			{10402, 65, 0},
			{10422, 66, 0},

			{10364, 67, 0},
			{10282, 68, 0}
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