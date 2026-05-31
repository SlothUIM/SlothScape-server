package server.model.items.collectionlog.clues;

import server.model.items.collectionlog.CollectionLogData;

public class HardClueLog implements CollectionLogData {

    private static final int[][] entries = {
    		{2581, 0, 0},

			{2627, 1, 0},//full helm
			{2623, 2, 0},//platebody
			{2625, 3, 0},//platelegs
			{3477, 4, 0},//plateskirt
			{2629, 5, 0},//kiteshield

			{2619, 6, 0},//full helm
			{2615, 7, 0},//platebody
			{2617, 8, 0},//platelegs
			{3478, 9, 0},//plateskirt
			{2621, 10, 0},//kiteshield

			{2657, 11, 0},//full helm
			{2653, 12, 0},//platebody
			{2655, 13, 0},//platelegs
			{3478, 14, 0},//plateskirt
			{2659, 15, 0},//kiteshield

			{2673, 16, 0},//full helm
			{2669, 17, 0},//platebody
			{2671, 18, 0},//platelegs
			{3480, 19, 0},//plateskirt
			{2675, 20, 0},//kiteshield

			{2665, 21, 0},//full helm
			{2661, 22, 0},//platebody
			{2663, 23, 0},//platelegs
			{3479, 24, 0},//plateskirt
			{2667, 25, 0},//kiteshield

			{10667, 26, 0},//h1
			{10670, 27, 0},//h2
			{10673, 28, 0},//h3
			{10676, 29, 0},//h4
			{10679, 30, 0},//h5

			{10286, 31, 0},//h1
			{10288, 32, 0},//h2
			{10290, 33, 0},//h3
			{10292, 34, 0},//h4
			{10294, 35, 0},//h5

			//sara dhide
			{10390, 36, 0},//coif
			{10386, 37, 0},//body
			{10388, 38, 0},//legs
			{10384, 39, 0},//vambs
			//guthix dhide
			{10382, 40, 0},//coif
			{10378, 41, 0},//body
			{10380, 42, 0},//legs
			{10376, 43, 0},//vambs

			//zam dhide
			{10374, 44, 0},//coif
			{10370, 45, 0},//body
			{10372, 46, 0},//legs
			{10368, 47, 0},//vambs

			//red dhide(t)
			{12331, 48, 0},//body
			{12333, 49, 0},//chaps

			//red dhide(g)
			{12327, 50, 0},//body
			{12329, 51, 0},//chaps

			//blue dhide(t)
			{7376, 52, 0},//body
			{7384, 53, 0},//chaps
			//blue dhide(g)
			{7374, 54, 0},//body
			{7382, 55, 0},//chaps
			//enchanged
			{7400, 56, 0},//hat
			{7399, 57, 0},//top
			{7398, 58, 0},//bottoms
			//stoles
			{10470, 59, 0},//sara
			{10472, 60, 0},//guthix
			{10474, 61, 0},//zamorak
			//croziers
			{10440, 62, 0},//sara
			{10442, 63, 0},//guthix
			{10444, 64, 0},//zamorak
			//pirates hat
			{2651, 65, 0},//sara
			//cavs
			{12323, 66, 0},//red
			{12321, 67, 0},//white
			{12325, 68, 0},//blue
			{2639, 69, 0},//tan
			{2641, 70, 0},//dark
			{2643, 71, 0},//black

			{10354, 72, 0},//Amulet_of_glory(t4)

			{10284, 73, 0}//Magic_comp_bow
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