package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class MasterClueLogRare implements CollectionLogData {

    private static final int[][] entries = {{12426, 0, 0},
			{12422, 1, 0},
			{12437, 2, 0},
			{12424, 3, 0},
			{10334, 4, 0},

			{10330, 5, 0},//full helm
			{10332, 6, 0},//platebody
			{10336, 7, 0},//platelegs
			{10342, 8, 0},//plateskirt

			{10338, 9, 0},//kiteshield
			{10340, 10, 0},//kiteshield
			{10344, 11, 0},//kiteshield
			{10350, 12, 0},//kiteshield

			{10348, 13, 0},//kiteshield
			{10346, 14, 0},//kiteshield
			{10352, 15, 0},//kiteshield
			{12389, 16, 0},//kiteshield
			{12391, 17, 0},//kiteshield
			{13578, 18, 0},//kiteshield
			{3486, 19, 0},//kiteshield
			{3481, 20, 0},//kiteshield
			{3483, 21, 0},//kiteshield
			{3485, 22, 0},//kiteshield
			{3488, 23, 0}//kiteshield
			// Add more Barrows items...
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