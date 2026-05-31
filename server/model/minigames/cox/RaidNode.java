package server.model.minigames.cox;

import lombok.Getter;
import lombok.Setter;
import server.model.minigames.cox.ChambersOfXeric.CoxRoom;

@Getter
@Setter
public class RaidNode {
    private ChambersOfXeric.CoxRoom room;
    private int gridX; // 0 to 2
    private int gridY; // 0 to 2
    private int gridZ; // 0 to 2
    private int rotation; // 0 to 3

    // Update your RaidNode constructor to store Z!
    public RaidNode(CoxRoom room, int gridX, int gridY, int gridZ, int rotation) {
        this.room = room;
        this.gridX = gridX;
        this.gridY = gridY;
        this.gridZ = gridZ; // <--- ADD THIS
        this.rotation = rotation;
    }

    // Multiply by 4 because each room takes up 4 chunks on the palette
    public int getPaletteStartX() { return gridX * 4; }
    public int getPaletteStartY() { return gridY * 4; }

}