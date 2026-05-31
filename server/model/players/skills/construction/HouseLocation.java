package server.model.players.skills.construction;

public enum HouseLocation {
    RIMMINGTON(2954, 3224, "Rimmington", 1, 5000),
    TAVERLY(2894, 3465, "Taverley", 10, 5000),
    POLLNIVNEACH(3340, 3004, "Pollnivneach", 20, 7500), // Update coords if yours are different
    HOSIDIUS(1743, 3517, "Hosidius", 25, 8750),
    RELLEKKA(2670, 3632, "Rellekka", 30, 10000),
    BRIMHAVEN(2758, 3178, "Brimhaven", 40, 15000),
    YANILLE(2601, 3104, "Yanille", 50, 25000),
    PRIFDDINAS(3240, 6066, "Prifddinas", 70, 50000); // Update coords if yours are different

    private final int x, y;
    private final String name;
    private final int levelReq;
    private final int cost;

    HouseLocation(int x, int y, String name, int levelReq, int cost) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.levelReq = levelReq;
        this.cost = cost;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public int getLevelReq() { return levelReq; }
    public int getCost() { return cost; }
}