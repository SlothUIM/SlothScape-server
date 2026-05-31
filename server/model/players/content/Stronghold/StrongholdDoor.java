package server.model.players.content.Stronghold;

public class StrongholdDoor {
    public final int x, y;
    public final int dialogueId; // use -1 if it's just a movement
    public final int destX, destY;
    public final boolean horizontal;

    public StrongholdDoor(int x, int y, int dialogueId, int destX, int destY, boolean horizontal) {
        this.x = x;
        this.y = y;
        this.dialogueId = dialogueId;
        this.destX = destX;
        this.destY = destY;
        this.horizontal = horizontal;
    }
}