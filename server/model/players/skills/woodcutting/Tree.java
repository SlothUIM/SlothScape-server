package server.model.players.skills.woodcutting;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum Tree {
	NORMAL(new int[] { 1276, 1278, 1279, 9730,1284,1286,1283}, 1342, 1511, 1, 5, 20, 25, 15, 12000),
	EVERGREEN_1(new int[] { 2092 }, 1355, 1511, 1, 5, 20, 25, 15, 12000),
	EVERGREEN_2(new int[] { 2091 }, 1342, 1511, 1, 5, 20, 25, 15, 12000),
	DEAD_1(new int[] { 1282}, 1347, 1511, 1, 5, 20, 25, 15, 12000), 
	DEAD_2(new int[] { 1286}, 1351, 1511, 1, 5, 20, 25, 15, 12000), 
	DEAD_3(new int[] { 1289}, 1353, 1511, 1, 5, 20, 25, 15, 12000), 
	OAK(new int[] { 10820, 11756 }, 1356, 1521, 15, 8, 50, 38, 25, 11500), 
	WILLOW(new int[] {10819, 1758, 11759, 11761, 11763, 11755}, 9711, 1519, 30, 10, 60, 68, 35, 11000), 
	WILLOW_2(new int[] { 10829, 10833, 10831}, 9471, 1519, 30, 10, 60, 68, 35, 11000), 
	TEAK(new int[] { 9036 }, 1356, 6333, 35, 10, 65, 68, 35, 10500), 
	MAPLE(new int[] { 4674, 11762, 10832 }, 9712, 1517, 45, 13, 75, 100, 45, 10000), 
	ARCTIC_PINE(new int[] { 3037 }, 1356, 10810, 54, 14, 85, 100, 50, 90400),
	YEW(new int[] { 10822, 10823, 11758, 27255 }, 1356, 1515, 60, 15, 100, 175, 60, 9000), 
	MAGIC(new int[] { 10834, 11764 }, 9713, 1513, 75, 20, 125, 250, 75, 8600), 
	REDWOOD(new int[] { 29668, 29670 }, 29669, 19669, 90, 35, 150, 380, 150, 8400),
	SAPLING(new int[] { 29763 }, 29764, 20799, 65, 13, 75, 25, 15, 100000);

	@Getter
    private int[] treeIds;
	@Getter
    private int stumpId;
    @Getter
    private int wood;
    @Getter
    private int levelRequired;
    @Getter
    private int chopsRequired;
    private int deprecationChance;
    private int respawn;
    @Getter
    private int petChance;
	@Getter
    private double experience;

	private Tree(int[] treeIds, int stumpId, int wood, int levelRequired, int chopsRequired, int deprecationChance, double experience, int respawn, int petChance) {
		this.treeIds = treeIds;
		this.stumpId = stumpId;
		this.wood = wood;
		this.levelRequired = levelRequired;
		this.experience = experience;
		this.deprecationChance = deprecationChance;
		this.chopsRequired = chopsRequired;
		this.respawn = respawn;
		this.petChance = petChance;
	}

    public int getChopdownChance() {
		return deprecationChance;
	}

    public int getRespawnTime() {
		return respawn;
	}

	private static final Map<Integer, Tree> TREE_CACHE = new HashMap<>();

	static {
		for (Tree tree : values()) {
			for (int id : tree.getTreeIds()) {
				TREE_CACHE.put(id, tree);
			}
		}
	}

	public static Tree forObject(int objectId) {
		return TREE_CACHE.get(objectId);
	}

}
