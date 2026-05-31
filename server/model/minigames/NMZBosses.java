package server.model.minigames;

import server.model.players.Player;

public enum NMZBosses {

	TRAPPED_SOUL("Trapped Soul", 8529, 60052, 1000, 1500, "The Ascent of Arceuus"), // Placeholder ID
    COUNT_DRAYNOR("Count Draynor", 6332, 60055, 2000, 1501, "Vampyre Slayer"),
    CORSAIR_TRAITOR("Corsair Traitor", 2301, 60058, 1500, 1502, "The Corsair Curse"), // Placeholder ID
    SAND_SNAKE("Sand Snake", 7894, 60061, 1500, 1503, "The Depths of Despair"), // Placeholder ID
    CORRUPT_LIZARDMAN("Corrupt Lizardman", 5294, 60064, 2500, 1504, "Tale of the Righteous"), // Placeholder ID
    KING_ROALD("King Roald", 6328, 60067, 3000, 1505, "What Lies Below"),
    WITCHS_EXPERIMENT("Witch's experiment", 6333, 60070, 1500, 1506, "Witch's House"),
    THE_KENDAL("The Kendal", 6322, 60073, 2000, 1507, "Mountain Daughter"),
    ME("Me", 6320, 60076, 5000, 1508, "Lunar Diplomacy"),
    ELVARG("Elvarg", 6118, 60079, 6000, 1509, "Dragon Slayer"),
    MOSS_GUARDIAN("Moss Guardian", 6325, 60082, 3500, 1510, "Roving Elves"),
    SLAGILITH("Slagilith", 6324, 60085, 4000, 1511, "One Small Favour"),
    NAZASTAROOL("Nazastarool", 6337, 60088, 4500, 1512, "Shilo Village"),
    TREUS_DAYTH("Treus Dayth", 6298, 60091, 5000, 1513, "Haunted Mine"),
    SKELETON_HELLHOUND("Skeleton Hellhound", 6326, 60094, 3000, 1514, "In Search of the Myreque"),
    DAGANNOTH_MOTHER("Dagannoth mother", 9207, 60097, 7000, 1515, "Horror from the Deep"), // Placeholder ID
    AGRITH_NAAR("Agrith-Naar", 6327, 60100, 6500, 1516, "Shadow of the Storm"),
    TREE_SPIRIT("Tree spirit", 6319, 60103, 3500, 1517, "Lost City"),
    DAD("Dad", 6330, 60106, 5000, 1518, "Troll Stronghold"),
    TANGLEFOOT("Tanglefoot", 6291, 60109, 4500, 1519, "Fairytale I"),
    KHAZARD_WARLORD("Khazard warlord", 6329, 60112, 4000, 1520, "Tree Gnome Village"),
    BLACK_KNIGHT_TITAN("Black Knight Titan", 6299, 60115, 6000, 1521, "Holy Grail"),
    BOUNCER("Bouncer", 6293, 60118, 5500, 1522, "Fight Arena"),
    BLACK_DEMON("Black demon", 6295, 60121, 8000, 1523, "The Grand Tree"),
    JUNGLE_DEMON("Jungle Demon", 6321, 60124, 10000, 1524, "Monkey Madness I");

    private final String name;
    private final int npcId;
    private final int textFrameId;
    private final int basePoints;
    private final int configId; 
    private final String unlockReq;

    NMZBosses(String name, int npcId, int textFrameId, int basePoints, int configId, String unlockReq) {
        this.name = name;
        this.npcId = npcId;
        this.textFrameId = textFrameId;
        this.basePoints = basePoints;
        this.configId = configId;
        this.unlockReq = unlockReq;
    }

    public String getName() { return name; }
    public int getNpcId() { return npcId; }
    public int getTextFrameId() { return textFrameId; }
    public int getBasePoints() { return basePoints; }
    public int getConfigId() { return configId; }
    public String getUnlockRequirement() { return unlockReq; }

    /**
     * Checks if the player has unlocked this boss.
     */
    public boolean isUnlocked(Player c) {
        // TODO: Replace with your actual quest variables!
        // Example: if (this == ELVARG && c.dragonSlayer == 0) return false;
        return true; 
    }
}