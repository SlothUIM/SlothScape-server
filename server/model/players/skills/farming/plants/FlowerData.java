package server.model.players.skills.farming.plants;

import java.util.HashMap;
import java.util.Map;

import server.model.players.skills.farming.InspectData;

public enum FlowerData {

	MARIGOLD(5096, 6010, 2, 20, 0.35, 8.5, 47, 0x08, 0x0c,InspectData.MARIGOLD),
	ROSEMARY(5097, 6014, 11, 20, 0.32, 12, 66.5, 0x0d, 0x11,InspectData.ROSEMARY), 
	NASTURTIUM(5098, 6012, 24, 20, 0.30, 19.5, 111, 0x12, 0x16,InspectData.NASTURTIUM), 
	WOAD(5099, 1793, 25, 20, 0.27, 20.5, 115.5, 0x17, 0x1b,InspectData.WOAD), 
	LIMPWURT(5100, 225, 26, 25, 21.5, 8.5, 120, 0x1c, 0x20,InspectData.LIMPWURT), ;

	private int seedId;
	private int harvestId;
	private int levelRequired;
	private int growthTime;
	private double diseaseChance;
	private double plantingXp;
	private double harvestXp;
	private int startingState;
	private int endingState;
    private final InspectData inspect;

	private static Map<Integer, FlowerData> seeds = new HashMap<Integer, FlowerData>();

	static {
		for (FlowerData data : FlowerData.values()) {
			seeds.put(data.seedId, data);
		}
	}

	FlowerData(int seedId, int harvestId, int levelRequired,
			int growthTime, double diseaseChance, double plantingXp,
			double harvestXp, int startingState, int endingState, InspectData inspect) {
		this.seedId = seedId;
		this.harvestId = harvestId;
		this.levelRequired = levelRequired;
		this.growthTime = growthTime;
		this.diseaseChance = diseaseChance;
		this.plantingXp = plantingXp;
		this.harvestXp = harvestXp;
		this.startingState = startingState;
		this.endingState = endingState;
		this.inspect = inspect;
	}

	public static FlowerData forId(int seedId) {
		return seeds.get(seedId);
	}

	public int getSeedId() {
		return seedId;
	}

	public int getHarvestId() {
		return harvestId;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public int getGrowthTime() {
		return growthTime;
	}

	public double getDiseaseChance() {
		return diseaseChance;
	}

	public double getPlantingXp() {
		return plantingXp;
	}

	public double getHarvestXp() {
		return harvestXp;
	}

	public int getStartingState() {
		return startingState;
	}

	public int getEndingState() {
		return endingState;
	}
	 public String[][] getInspectMessages() {
	        return inspect.getMessages();
	    }
}