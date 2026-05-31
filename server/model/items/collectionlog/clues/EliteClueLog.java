package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class EliteClueLog implements CollectionLogData {

    private static final int[][] entries = {
			{13101, 0, 0},

			{13107, 1, 0},//full helm
			{13109, 2, 0},//platebody
			{13111, 3, 0},//platelegs
			{13113, 4, 0},//plateskirt

			{12385, 5, 0},//kiteshield
			{12387, 6, 0},//kiteshield
			{12381, 7, 0},//kiteshield
			{12383, 8, 0},//kiteshield

			{13095, 9, 0},//kiteshield
			{13097, 10, 0},//kiteshield
			{13099, 11, 0},//kiteshield
			{13006, 12, 0},//kiteshield
			{13763, 13, 0}//kiteshield
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
    public void setKillCount(int kc) {

    }
    @Override
    public int getKillCount() {
        return 0;
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