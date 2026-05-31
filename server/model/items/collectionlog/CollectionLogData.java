package server.model.items.collectionlog;

public interface CollectionLogData {
    int getTotalUnlocked();
    int getMaxItems();
    int[][] getEntries();
    void setAmount(int index, int amount); // ✅ Add this for player save support
	void addItem(int i, int j);

    int getKillCount();

    void setKillCount(int kc);
}
