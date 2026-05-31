package server.model.players.packets;

public class SongDefinition {
    private String name;
    private String hint;
    private int musicId;
    private int slotId;
    private int duration;
    private int[] regionIds;

    // Getters
    public String getName() { return name; }
    public String getHint() { return hint; }
    public int getMusicId() { return musicId; }
    public int getSlotId() { return slotId; }
    public int[] getRegionIds() { return regionIds; }
}