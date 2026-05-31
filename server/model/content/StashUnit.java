package server.model.content;

public enum StashUnit {
    // Format: NAME (Tier, ObjectID, ConfigID, BitShift, Required_Items)
    // Tiers: 0=Beginner, 1=Easy, 2=Medium, 3=Hard, 4=Elite/Master

    // --- BEGINNER ---
	VARROCK_SQUARE_BUSH_GYPSY(0, 34736, 2202, 0, new int[]{1635, 1654}),
	VARROCK_SQUARE_BUSH_THESSALIA(0, 34737, 2202, 1, new int[]{1949, 1007}),
    LUMBRIDGE_BUSH_BOBS_AXE(0, 34738, 2202, 2, new int[]{1061, 1351}),
    LUMBRIDGE_SWAMP_BUSH(1, 34736, 1367, 1, new int[]{2583, 1079, 1704}),
    
    // --- EASY ---
    LUMBRIDGE_SWAMP_HOLE_SHED(1, 28958, 1365, 0, new int[]{1205, 1153, 1635}),
    DRAYNOR_MARKET_HOLE(1, 28960, 1365, 2, new int[]{1097, 1191, 1295}),
    
    RIMMINGTON_MINE_ROCK(1, 28969, 1365, 11, new int[]{1097, 1191, 1295}),
    
    VARROCK_PALACE_BUSH(1, 28983, 1365, 25, new int[]{1361, 1169, 1641}),
    GRAND_EXCHANGE_BUSH(1, 28985, 1365, 27, new int[]{11978, 11862, 995}),
    FALADOR_PARK_BUSH(1, 28985, 1368, 14, new int[]{2579, 2955, 882}),
    FALADOR_GEM_BUSH(1, 28984, 1365, 26, new int[]{1273, 1125, 1191}),
    FALADOR_PARTY_ROOM_BUSH(1, 28972, 1365, 14, new int[]{1157, 1119, 1081}),
    
    // --- MEDIUM ---
    EDGEVILLE_MONASTERY_BUSH(2, 29005, 1366, 18, new int[]{1712, 1712, 1179}),
    CATHERBY_BEACH_BUSH(2, 29001, 1366, 14, new int[]{11840, 6585, 6737}),

    // --- HARD ---
    FREMENNIK_PROVINCE_HOLE(3, 28936, 1368, 8, new int[]{11840, 6585, 6737}),

    // --- Master ---
    DRAYNOR_VILLAGE_BUSH(5, 29059, 1369, 19, new int[]{4151, 1052, 6135});
    // You can now easily copy-paste your other 100+ units here!

    private final int tier;
    private final int objectId;
    private final int configId;
    private final int bitShift;
    private final int[] requiredItems;

    StashUnit(int tier, int objectId, int configId, int bitShift, int[] requiredItems) {
        this.tier = tier;
        this.objectId = objectId;
        this.configId = configId;
        this.bitShift = bitShift;
        this.requiredItems = requiredItems;
    }

    public int getTier() { return tier; }
    public int getObjectId() { return objectId; }
    public int getConfigId() { return configId; }
    public int getBitShift() { return bitShift; }
    public int[] getRequiredItems() { return requiredItems; }

    // Helper to find the Enum by the Object ID a player clicked
    public static StashUnit forObjectId(int id) {
        for (StashUnit stash : values()) {
            if (stash.getObjectId() == id) {
                return stash;
            }
        }
        return null;
    }
}