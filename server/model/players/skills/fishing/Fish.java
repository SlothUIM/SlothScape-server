package server.model.players.skills.fishing;


public enum Fish {
    SHRIMP(317, 1, 10), // itemId, levelReq, xp
    ANCHOVY(321, 15, 30),
    TROUT(335, 20, 50),
    SALMON(331, 30, 70),
    LOBSTER(377, 40, 90),
    SWORDFISH(373, 50, 100),
    ANGLERFISH(13439, 82, 200);

    private final int itemId;
    private final int levelReq;
    private final int xp;

    Fish(int itemId, int levelReq, int xp) {
        this.itemId = itemId;
        this.levelReq = levelReq;
        this.xp = xp;
    }

    public int getItemId() { return itemId; }
    public int getLevelReq() { return levelReq; }
    public int getXp() { return xp; }
}
