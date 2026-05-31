/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model.players.skills.farming;
/**
 *
 * @author ArrowzFtw
 */
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;

/**
 * Created by IntelliJ IDEA. User: vayken Date: 24/02/12 Time: 20:34 To change
 * this template use File | Settings | File Templates.
 */
public class Herbs {

	private Player player;

	// set of global constants for Farming

	private static final int START_HARVEST_AMOUNT = 3;
	private static final int END_HARVEST_AMOUNT = 18;

	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;

	public Herbs(Player player2) {
		this.player = player2;
	}

	// Farming data
	public int[] herbStages = new int[6];
	public int[] herbSeeds = new int[6];
	public int[] herbHarvest = new int[6];
	public int[] herbState = new int[6];
	public long[] herbTimer = new long[6];
	public double[] diseaseChance = { 1, 1, 1, 1, 1, 1, 1 };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;

	public static final int MAIN_HERB_LOCATION_CONFIG = 529;

	/* This is the enum holding the seeds info */

	public enum HerbData {
		 GUAM(5291, 199, 9, 80, 0.25, 11, 12.5, 4, 8),         // 4–11
		    MARRENTILL(5292, 201, 14, 80, 0.25, 13.5, 15, 11, 15),   // 12–19
		    TARROMIN(5293, 203, 19, 80, 0.25, 16, 18, 18, 22),       // 20–27
		    HARRALANDER(5294, 205, 26, 80, 0.25, 21.5, 24, 25, 29),  // 28–35
		    RANARR(5295, 207, 32, 80, 0.20, 27, 30.5, 32, 36),       // 36–43
		    TOADFLAX(5296, 3049, 38, 80, 0.20, 34, 38.5, 39, 43),    // 44–51
		    IRIT(5297, 209, 44, 80, 0.20, 43, 48.5, 46, 50),         // 52–59
		    AVANTOE(5298, 211, 50, 80, 0.20, 54.5, 61.5, 53, 57),    // 60–67
		    KUARM(5299, 213, 56, 80, 0.20, 69, 78, 0x44, 72),          // 68–75
		    SNAPDRAGON(5300, 3051, 62, 80, 0.15, 87.5, 98.5, 75, 79),// 76–83
		    CADANTINE(5301, 215, 67, 80, 0.15, 106.5, 120, 82, 86),  // 84–91
		    LANTADYME(5302, 2485, 73, 80, 0.15, 134.5, 151.5, 89, 93),// 92–99
		    DWARF(5303, 217, 79, 80, 0.15, 170.5, 192, 96, 100),      // 100–107
		    TORSOL(5304, 219, 85, 80, 0.15, 199.5, 224.5, 103, 107);   // 108–115

		private int seedId;
		private int harvestId;
		private int levelRequired;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, HerbData> seeds = new HashMap<Integer, HerbData>();

		static {
			for (HerbData data : HerbData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		HerbData(int seedId, int harvestId, int levelRequired, int growthTime,
				double diseaseChance, double plantingXp, double harvestXp,
				int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.levelRequired = levelRequired;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
		}

		public static HerbData forId(int seedId) {
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
	}

	/* This is the enum data about the different patches */

	public enum HerbFieldsData {
		ARDOUGNE(0, new Point[] { new Point(2670, 3374), new Point(2671, 3375)}),
		FARMINGGUILD(5, new Point[] { new Point(1238, 3726), new Point(1239, 3727)}),
		ZEAH(4, new Point[] { new Point(1738, 3550), new Point(1739, 3551)}), 
		PHASMATYS(3, new Point[] { new Point(3605, 3529), new Point(3606, 3530)}),  
		FALADOR(2, new Point[] { new Point(3058, 3311), new Point(3059, 3312)}), 
		CATHERBY(1, new Point[] { new Point(2813, 3463), new Point(2814, 3464)});
		private int herbIndex;
		private Point[] herbPosition;

		HerbFieldsData(int herbIndex, Point[] herbPosition) {
			this.herbIndex = herbIndex;
			this.herbPosition = herbPosition;
		}

		public static HerbFieldsData forIdPosition(int x, int y) {
			for (HerbFieldsData herbFieldsData : HerbFieldsData.values()) {
				if (FarmingConstants.inRangeArea(
						herbFieldsData.getHerbPosition()[0],
						herbFieldsData.getHerbPosition()[1], x, y)) {
					return herbFieldsData;
				}
			}
			return null;
		}

		public int getHerbIndex() {
			return herbIndex;
		}

		public Point[] getHerbPosition() {
			return herbPosition;
		}
	}

	/* This is the enum that hold the different data for inspecting the plant */

	public enum InspectData {

		GUAM(5291, new String[][] { { "The seed has only just been planted." },
				{ "The herb is now ankle height." },
				{ "The herb is now knee height." },
				{ "The herb is now mid-thigh height." },
				{ "The herb is fully grown and ready to harvest." } }), MARRENTILL(
				5292, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), TARROMIN(
				5293, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), HARRALANDER(
				5294, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), GOUT_TUBER(
				6311, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), RANARR(
				5295, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), TOADFLAX(
				5296, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), IRIT(
				5297, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), AVANTOE(
				5298, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), KUARM(
				5299, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), SNAPDRAGON(
				5300, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), CADANTINE(
				5301, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), LANTADYME(
				5302, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), DWARF(
				5303, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } }), TORSOL(
				5304, new String[][] {
						{ "The seed has only just been planted." },
						{ "The herb is now ankle height." },
						{ "The herb is now knee height." },
						{ "The herb is now mid-thigh height." },
						{ "The herb is fully grown and ready to harvest." } })

		;
		private int seedId;
		private String[][] messages;

		private static Map<Integer, InspectData> seeds = new HashMap<Integer, InspectData>();

		static {
			for (InspectData data : InspectData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		InspectData(int seedId, String[][] messages) {
			this.seedId = seedId;
			this.messages = messages;
		}

		public static InspectData forId(int seedId) {
			return seeds.get(seedId);
		}

		public int getSeedId() {
			return seedId;
		}

		public String[][] getMessages() {
			return messages;
		}
	}

	/* update all the patch states */
	public void updateHerbStates() {
	    int HERB_SHIFT = 24;
	    int HERB_SHIFT2 = 0;
	    int configid = 529;
	    int herbIndex = getHerbIndex(player);
	    if (herbIndex == -1)
	    	return;
	    int[] herbConfigValues = new int[herbStages.length];
	    for (int i = 0; i < herbConfigValues.length; i++) {
	    	herbConfigValues[i] = getConfigValue(herbStages[i], herbSeeds[i], herbState[i], i);
	    }
	    int configValue = 0;
	    int configValue2 = 0;
	    if (herbIndex == 5) {
		    configid = 1057;
	    } else
	    	configid = 529;
	    if(configid == 529) {
		    if (herbConfigValues.length > herbIndex) {
		        configValue |= herbConfigValues[herbIndex] << HERB_SHIFT;
		    }
		    	player.getPA().sendConfig(529, configValue);
		    	
	
	    } else if(configid == 1057) {
		    if (herbConfigValues.length > herbIndex) {
		        configValue2 |= herbConfigValues[herbIndex] << HERB_SHIFT2;
		    }
		    	player.getPA().sendConfig(1057, configValue2);
	    }
	    System.out.println("Config 529 value = " + configValue);
	    System.out.println("Flower patches: " + Arrays.toString(herbConfigValues));

	}	

	public int getHerbIndex(Player player) {
		if (player.withinDistance(2663, 3374, player.getX(), player.getY(), 50)) {
	        return 0; // Ardougne
	    }
	    if (player.withinDistance(2807, 3464, player.getX(), player.getY(), 50)) {
	        return 1; // Catherby
	    }
	    if (player.withinDistance(3056, 3309, player.getX(), player.getY(), 50)) {
	        return 2; // Falador
	    }
	    if (player.withinDistance(3603, 3527, player.getX(), player.getY(), 50)) {
	        return 3; // Canifis
	    }
	    if (player.withinDistance(1736, 3553, player.getX(), player.getY(), 50)) {
	        return 4; // Kourend
	    }
	    if (player.withinDistance(1248, 3728, player.getX(), player.getY(), 150)) {
	        return 5; // Farming Guild
	    }
	    return 0;
	}

	/* getting the different config values */

	public int getConfigValue(int herbStage, int seedId, int plantState,
			int index) {
		HerbData herbData = HerbData.forId(seedId);
		switch (herbStage) {
		case 0:// weed
			return (GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (GROWING << 6) + 0x01;
		case 2:
			return (GROWING << 6) + 0x02;
		case 3:
			return (GROWING << 6) + 0x03;
		}
		if (herbData == null) {
			return -1;
		}
		if (herbSeeds[index] == 6311) {
			if (plantState == 1) {
				return herbStages[index] + 0xc1;
			} else if (plantState == 2) {
				return herbStages[index] + 0xc3;
			}
		}
		return (plantState == 2 ? herbStages[index] + 0x9e
				: plantState == 1 ? herbStages[index] + 0x9a
						: getPlantState(plantState) << 6)
				+ herbData.getStartingState() + herbStage - 4;
	}

	/* getting the plant states */

	public int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return GROWING;
		}
		return -1;
	}

	/* calculating the disease chance and making the plant grow */

	public void doCalculations() {
		for (int i = 0; i < herbSeeds.length; i++) {
			if (herbStages[i] > 0 && herbStages[i] <= 3
					&& Server.getMinutesCounter() - herbTimer[i] >= 5) {
				herbStages[i]--;
				herbTimer[i] = Server.getMinutesCounter();
				player.getAllotment().updateFarmingStates();
			}
			HerbData herbData = HerbData.forId(herbSeeds[i]);
			if (herbData == null) {
				continue;
			}

			long difference = Server.getMinutesCounter() - herbTimer[i];
			long growth = herbData.getGrowthTime();
			int nbStates = herbData.getEndingState()
					- herbData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (herbTimer[i] == 0 || herbState[i] == 2 || state > nbStates) {
				continue;
			}
			if (4 + state != herbStages[i]) {
				herbStages[i] = 4 + state;
				doStateCalculation(i);
				player.getAllotment().updateFarmingStates();
			}
		}
	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
		if (herbState[index] == 2) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (herbState[index] == 1) {
			herbState[index] = 2;
		}

		if (herbState[index] == 4 && herbStages[index] != 3) {
			herbState[index] = 0;
		}

		if (herbState[index] == 0 && herbStages[index] >= 4
				&& herbStages[index] <= 7) {
			HerbData herbData = HerbData.forId(herbSeeds[index]);
			if (herbData == null) {
				return;
			}
			double chance = diseaseChance[index] * herbData.getDiseaseChance();
			int maxChance = (int) chance * 100;
			if (Misc.random(100) <= maxChance) {
				herbState[index] = 1;
			}
		}
	}

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		int finalAnimation;
		int finalDelay;
		if (herbFieldsData == null
				|| (itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE)) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] <= 3) {
			if (!player.getItems().playerHasItem(FarmingConstants.RAKE)) {
				player.getDH().sendStatement("You need a rake to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.RAKING_ANIM;
				finalDelay = 5;
			}
		} else {
			if (!player.getItems().playerHasItem(FarmingConstants.SPADE)) {
				player.getDH().sendStatement(
						"You need a spade to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.SPADE_ANIM;
				finalDelay = 3;
			}
		}
		final int animation = finalAnimation;
		player.stopPlayer(true);
		player.startAnimation(animation);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				player.startAnimation(animation);
				if (herbStages[herbFieldsData.getHerbIndex()] <= 2) {
					herbStages[herbFieldsData.getHerbIndex()]++;
					player.getItems().addItem(6055, 1);
				} else {
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					container.stop();
				}
				player.getPA().addSkillXP((int)CLEARING_EXPERIENCE, player.playerFarming);
				herbTimer[herbFieldsData.getHerbIndex()] = Server.getMinutesCounter();
				player.getAllotment().updateFarmingStates();
				if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				resetHerbs(herbFieldsData.getHerbIndex());
				player.sendMessage("You clear the patch.");
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, finalDelay);
		return true;

	}

	/* planting the seeds */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		final HerbData herbData = HerbData.forId(seedId);
		if (herbFieldsData == null || herbData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}
		if (herbData.getLevelRequired() > player.getSkills().getLevel(Skill.FARMING)) {
			player.getDH().sendStatement("You need a farming level of "
							+ herbData.getLevelRequired()
							+ " to plant this seed.");
			return true;
		}
		if (!player.getItems().playerHasItem(FarmingConstants.SEED_DIBBER)) {
			player.getDH().sendStatement(
					"You need a seed dibber to plant seed here.");
			return true;
		}
		player.startAnimation(FarmingConstants.SEED_DIBBING);
		player.getItems().deleteItem(seedId, 1);

		player.stopPlayer(true);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				herbState[herbFieldsData.getHerbIndex()] = 0;
				herbStages[herbFieldsData.getHerbIndex()] = 4;
				herbSeeds[herbFieldsData.getHerbIndex()] = seedId;
				herbTimer[herbFieldsData.getHerbIndex()] = Server.getMinutesCounter();
				player.getPA().addSkillXP((int)herbData.getPlantingXp(), player.playerFarming);
				container.stop();
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, 3);
		return true;
	}
	@SuppressWarnings("unused")
	public void displayAll() {
		for (int i = 0; i < herbStages.length; i++) {
			System.out.println("index : " + i);
			System.out.println("state : " + herbState[i]);
			System.out.println("seeds : " + herbSeeds[i]);
			System.out.println("level : " + herbStages[i]);
			System.out.println("timer : " + herbTimer[i]);
			System.out
		.println("-----------------------------------------------------------------");
		}
	}
	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData
				.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (!player.getItems().playerHasItem(FarmingConstants.SPADE)) {
			player.getDH().sendStatement(
					"You need a spade to harvest here.");
			return true;
		}

		player.startAnimation(
				FarmingConstants.PICKING_VEGETABLE_ANIM);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				if (herbHarvest[herbFieldsData.getHerbIndex()] == 0) {
					herbHarvest[herbFieldsData.getHerbIndex()] = 1 + (START_HARVEST_AMOUNT + Misc
							.random(END_HARVEST_AMOUNT - START_HARVEST_AMOUNT)) * (1);
				}

				if (herbHarvest[herbFieldsData.getHerbIndex()] == 1) {
					resetHerbs(herbFieldsData.getHerbIndex());
					herbStages[herbFieldsData.getHerbIndex()] = 3;
					herbTimer[herbFieldsData.getHerbIndex()] = Server.getMinutesCounter();
					container.stop();
					return;
				}
				if (player.getItems().freeSlots()  <= 0) {
					container.stop();
					return;
				}
				herbHarvest[herbFieldsData.getHerbIndex()]--;
				player.startAnimation(
						FarmingConstants.PICKING_HERB_ANIM);
				player.sendMessage(
						"You harvest the crop, and get some herbs.");
				player.getItems().addItem(herbData.getHarvestId(), 1);
				player.getPA().addSkillXP((int)herbData.getHarvestXp(), player.playerFarming);
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
				player.getPA().resetAnimation();
			}
		}, 3);
		return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] != 3
				|| herbState[herbFieldsData.getHerbIndex()] == 4) {
			player.sendMessage("This patch doesn't need compost.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(1925, 1);

		player.sendMessage(
				"You pour some " + (itemId == 6034 ? "super" : "")
						+ "compost on the patch.");
		player.startAnimation(FarmingConstants.PUTTING_COMPOST);
		player.getPA().addSkillXP((int)(itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE
				: Compost.COMPOST_EXP_USE), player.playerFarming);
		player.stopPlayer(true);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
				diseaseChance[herbFieldsData.getHerbIndex()] *= itemId == 6032 ? COMPOST_CHANCE
						: SUPERCOMPOST_CHANCE;
				herbState[herbFieldsData.getHerbIndex()] = 4;
				container.stop();
			}

			@Override
			public void stop() {
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, 7);
		return true;
	}

	/* inspecting a plant */

	public boolean inspect(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData
				.forId(herbSeeds[herbFieldsData.getHerbIndex()]);
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData
				.getHerbIndex()]);
		if (herbState[herbFieldsData.getHerbIndex()] == 1) {
			player.getDH().sendStatement("This plant is diseased. Use a plant cure on it to cure it, ",
							"or clear the patch with a spade.");
			return true;
		} else if (herbState[herbFieldsData.getHerbIndex()] == 2) {
			player.getDH().sendStatement("This plant is dead. You did not cure it while it was diseased.",
							"Clear the patch with a spade.");
			return true;
		}
		if (herbStages[herbFieldsData.getHerbIndex()] == 0) {
			player.getDH().sendStatement(
					"This is an herb patch. The soil has not been treated.",
					"The patch needs weeding.");
		} else if (herbStages[herbFieldsData.getHerbIndex()] == 3) {
			player.getDH().sendStatement(
					"This is an herb patch. The soil has not been treated.",
					"The patch is empty and weeded.");
		} else if (inspectData != null && herbData != null) {
			player.sendMessage(
					"You bend down and start to inspect the patch...");

			player.startAnimation(1331);
			player.stopPlayer(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
					if (herbStages[herbFieldsData.getHerbIndex()] - 4 < inspectData
							.getMessages().length - 2) {
						player.getDH().sendStatement2(inspectData
								.getMessages()[herbStages[herbFieldsData
								.getHerbIndex()] - 4]);
					} else if (herbStages[herbFieldsData.getHerbIndex()] < herbData
							.getEndingState() - herbData.getStartingState() + 2) {
						player.getDH().sendStatement2(inspectData.getMessages()[inspectData.getMessages().length - 2]);
					} else {
						player.getDH().sendStatement2(
								inspectData.getMessages()[inspectData
										.getMessages().length - 1]);
					}
					container.stop();
				}

				@Override
				public void stop() {
					player.startAnimation(1332);
					player.stopPlayer(false);
					player.getPA().resetAnimation();
				}
			}, 5);
		}
		return true;
	}

	/* opening the corresponding guide about the patch */

	public boolean guide(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null) {
			return false;
		}
		//player.getSkillInterfaces().farmingComplex(7);
		//player.getSkillInterfaces().selected = 20;
		return true;
	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null || itemId != 6036) {
			return false;
		}
		final HerbData herbData = HerbData.forId(herbSeeds[herbFieldsData
				.getHerbIndex()]);
		if (herbData == null) {
			return false;
		}
		if (herbState[herbFieldsData.getHerbIndex()] != 1) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(229, 1);
		player.startAnimation(FarmingConstants.CURING_ANIM);
		player.stopPlayer(true);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
				player.sendMessage(
						"You cure the plant with a plant cure.");
				herbState[herbFieldsData.getHerbIndex()] = 0;
				container.stop();
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, 7);

		return true;

	}

	@SuppressWarnings("unused")
	private void resetHerbs() {
		for (int i = 0; i < herbStages.length; i++) {
			herbSeeds[i] = 0;
			herbState[i] = 0;
			diseaseChance[i] = 0;
			herbHarvest[i] = 0;
		}
	}

	/* reseting the patches */

	private void resetHerbs(int index) {
		herbSeeds[index] = 0;
		herbState[index] = 0;
		diseaseChance[index] = 1;
		herbHarvest[index] = 0;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
		final HerbFieldsData herbFieldsData = HerbFieldsData.forIdPosition(
				objectX, objectY);
		if (herbFieldsData == null)
			return false;
		if (herbStages[herbFieldsData.getHerbIndex()] == 3)
			return true;
		return false;
	}

}
