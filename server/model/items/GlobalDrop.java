package server.model.items;

public class GlobalDrop {
    public int itemId, itemAmount, x, y, z, respawnTicks, currentTicks;
    public int objectId, restoreObjectId; // Add these!
    public boolean taken = false;
    public boolean isObject = false; // Flag to check type

    public GlobalDrop(int id, int amount, int x, int y, int z, int respawn, int objId, int restoreId) {
        this.itemId = id;
        this.itemAmount = amount;
        this.x = x;
        this.y = y;
        this.z = z;
        this.respawnTicks = respawn;
        this.objectId = objId;
        this.restoreObjectId = restoreId;
        this.isObject = (objId > 0);
    }
}