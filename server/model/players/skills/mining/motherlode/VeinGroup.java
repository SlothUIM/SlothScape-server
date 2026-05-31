package server.model.players.skills.mining.motherlode;

public class VeinGroup {
    int[] ids;       // left/middle/right or single
    int[][] offsets; // relative offsets from starting tile

    public VeinGroup(int[] ids, int[][] offsets) {
        this.ids = ids;
        this.offsets = offsets;
    }
}
