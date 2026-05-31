/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */package server.model.players.skills.farming;

/**
 *
 * @author ArrowzFtw
 */

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.util.Misc;
import server.world.Boundary;
import server.world.map.VarBit;
/**
 * Created by IntelliJ IDEA. User: vayken Date: 24/02/12 Time: 20:34 To change
 * this template use File | Settings | File Templates.
 */
public class Allotments {

	private Player player;

	// set of global constants for Farming

	private static final int START_HARVEST_AMOUNT = 3;
	private static final int END_HARVEST_AMOUNT = 56;

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;

	public Allotments(Player player2) {
		this.player = player2;
	}

	// Farming data
	public int[] allotmentStages = new int[12];
	public int[] allotmentSeeds = new int[12];
	public int[] allotmentHarvest = new int[12];
	public int[] allotmentState = new int[12];
	public long[] allotmentTimer = new long[12];
	public double[] diseaseChance = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	public boolean[] allotmentWatched = { false, false, false, false, false,
			false, false, false, false, false, false, false };
	public boolean[] hasFullyGrown = { false, false, false, false, false,
			false, false, false, false, false, false, false };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;
	public static final int DEAD = 0x03;

	/* This is the enum holding the seeds info */

	public enum AllotmentData {

		POTATO(5318, 1942, 5096, 3, 1, new int[] { 6032, 2 }, 4, 0.30, 8, 9.5,
				0x06, 0x0c), ONION(5319, 1957, 5096, 3, 5,
				new int[] { 5438, 1 }, 5, 0.30, 9.5, 10.5, 0x0d, 0x13), CABBAGE(
				5324, 1965, 5097, 3, 7, new int[] { 5458, 1 }, 5, 0.25, 10,
				11.5, 0x14, 0x1a), TOMATO(5322, 1982, 5096, 3, 12, new int[] {
				5478, 2 }, 5, 0.25, 12.5, 14, 0x1b, 0x21), SWEETCORN(5320,
				5986, 6059, 3, 20, new int[] { 5931, 10 }, 5, 0.20, 17, 19,
				0x22, 0x2a), STRAWBERRY(5323, 5504, -1, 3, 31, new int[] {
				5386, 1 }, 6, 0.20, 26, 29, 0x2b, 0x33), WATERMELON(5321, 5982,
				5098, 3, 47, new int[] { 5970, 10 }, 7, 0.20, 48.5, 54.5, 0x34,
				0x3e);

		private int seedId;
		private int harvestId;
		private int flowerProtect;
		private int seedAmount;
		private int levelRequired;
		private int[] paymentToWatch;
		private int growthTime;
		private double diseaseChance;
		private double plantingXp;
		private double harvestXp;
		private int startingState;
		private int endingState;

		private static Map<Integer, AllotmentData> seeds = new HashMap<Integer, AllotmentData>();

		static {
			for (AllotmentData data : AllotmentData.values()) {
				seeds.put(data.seedId, data);
			}
		}

		AllotmentData(int seedId, int harvestId, int flowerProtect,
				int seedAmount, int levelRequired, int[] paymentToWatch,
				int growthTime, double diseaseChance, double plantingXp,
				double harvestXp, int startingState, int endingState) {
			this.seedId = seedId;
			this.harvestId = harvestId;
			this.flowerProtect = flowerProtect;
			this.seedAmount = seedAmount;
			this.levelRequired = levelRequired;
			this.paymentToWatch = paymentToWatch;
			this.growthTime = growthTime;
			this.diseaseChance = diseaseChance;
			this.plantingXp = plantingXp;
			this.harvestXp = harvestXp;
			this.startingState = startingState;
			this.endingState = endingState;
		}

		public static AllotmentData forId(int seedId) {
			return seeds.get(seedId);
		}

		public int getSeedId() {
			return seedId;
		}

		public int getHarvestId() {
			return harvestId;
		}

		public int getFlowerProtect() {
			return flowerProtect;
		}

		public int getSeedAmount() {
			return seedAmount;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int[] getPaymentToWatch() {
			return paymentToWatch;
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

	public enum AllotmentFieldsData {

		CATHERBY_NORTH(0, new Point[] { new Point(2805, 3466),
				new Point(2806, 3468), new Point(2805, 3467),
				new Point(2814, 3468) }, 2324),

		CATHERBY_SOUTH(1, new Point[] { new Point(2805, 3459),
				new Point(2806, 3461), new Point(2805, 3459),
				new Point(2814, 3460) }, 2324),

		ARDOUGNE_NORTH(2, new Point[] { new Point(2662, 3377),
				new Point(2663, 3379), new Point(2662, 3378),
				new Point(2671, 3379) }, 2325),

		ARDOUGNE_SOUTH(3, new Point[] { new Point(2662, 3370),
				new Point(2663, 3372), new Point(2662, 3370),
				new Point(2671, 3371) }, 2325),

		FALADOR_NORTH_WEST(4, new Point[] { new Point(3050, 3307),
				new Point(3051, 3312), new Point(3050, 3311),
				new Point(3054, 3312) }, 2323),

		FALADOR_SOUTH_EAST(5, new Point[] { new Point(3055, 3303),
				new Point(3059, 3304), new Point(3058, 3303),
				new Point(3059, 3308) }, 2323),

		PHASMATYS_NORTH_WEST(6, new Point[] { new Point(3597, 3525),
				new Point(3598, 3530), new Point(3597, 3529),
				new Point(3601, 3530) }, 2326),

		PHASMATYS_SOUTH_EAST(7, new Point[] { new Point(3602, 3521),
				new Point(3606, 3522), new Point(3605, 3521),
				new Point(3606, 3526) }, 2326),
		
		ZEAH_NORTH(8, new Point[] { new Point(1738, 3554),
				new Point(1739, 3559), new Point(1733, 3558),
				new Point(1739, 3559) }, 8535),

		ZEAH_SOUTH(9, new Point[] { new Point(1730, 3550),
				new Point(1735, 3551), new Point(1730, 3550),
				new Point(1731, 3555) }, 8535),
		
		GUILD_NORTH(10, new Point[] { new Point(1267, 3732),
				new Point(1272, 3733), new Point(1267, 3732),
				new Point(1268, 3736) }, 8535),

		GUILD_SOUTH(11, new Point[] { new Point(1267, 3723),
				new Point(1268, 3727), new Point(1267, 3726),
				new Point(1272, 3727) }, 8535);
		
		private int allotmentIndex;
		private Point[] allotmentPosition;
		private int farmerBelonging;

		AllotmentFieldsData(int allotmentIndex, Point[] allotmentPosition,
				int farmerBelonging) {
			this.allotmentIndex = allotmentIndex;
			this.allotmentPosition = allotmentPosition;
			this.farmerBelonging = farmerBelonging;
		}

		public static AllotmentFieldsData forIdPosition(int x, int y) {
			for (AllotmentFieldsData allotmentFieldsData : AllotmentFieldsData
					.values()) {
				if (FarmingConstants.inRangeArea(
						allotmentFieldsData.getAllotmentPosition()[0],
						allotmentFieldsData.getAllotmentPosition()[1], x, y)
						|| FarmingConstants.inRangeArea(
								allotmentFieldsData.getAllotmentPosition()[2],
								allotmentFieldsData.getAllotmentPosition()[3],
								x, y)) {
					return allotmentFieldsData;
				}
			}
			return null;
		}

		public static ArrayList<Integer> listIndexProtected(int npcId) {
			ArrayList<Integer> array = new ArrayList<Integer>();
			for (AllotmentFieldsData allotmentFieldsData : AllotmentFieldsData
					.values()) {
				if (allotmentFieldsData.getFarmerBelonging() == npcId)
					array.add(allotmentFieldsData.allotmentIndex);
			}
			return array;

		}

		public int getAllotmentIndex() {
			return allotmentIndex;
		}

		public Point[] getAllotmentPosition() {
			return allotmentPosition;
		}

		public int getFarmerBelonging() {
			return farmerBelonging;
		}
	}
	    /*public static int getConfigValue(Player player) {
	        int[] stages = player.getFarming().allotmentStages;
	        int[] seeds = player.getFarming().allotmentSeeds;
	        int[] states = player.getFarming().allotmentState;

	        int value = 0;

	        if (player.withinDistance(2807, 3464, 50)) { // Catherby
	            value |= getConfigValue(stages, seeds, states, 1) << 8;
	            value |= getConfigValue(stages, seeds, states, 0);
	        } else if (player.withinDistance(2663, 3374, 50)) { // Ardougne
	            value |= getConfigValue(stages, seeds, states, 3) << 8;
	            value |= getConfigValue(stages, seeds, states, 2);
	        } else if (player.withinDistance(3056, 3309, 50)) { // Falador
	            value |= getConfigValue(stages, seeds, states, 5) << 8;
	            value |= getConfigValue(stages, seeds, states, 4);
	        } else if (player.withinDistance(3603, 3527, 50)) { // Morytania
	            value |= getConfigValue(stages, seeds, states, 7) << 8;
	            value |= getConfigValue(stages, seeds, states, 6);
	        } else if (player.withinDistance(1736, 3553, 150)) { // Zeah
	            value |= getConfigValue(stages, seeds, states, 9) << 8;
	            value |= getConfigValue(stages, seeds, states, 8);
	        } else if (player.withinDistance(1248, 3728, 150)) { // Farming Guild (Config 1057)
	            value |= getConfigValue(stages, seeds, states, 10) << 16;
	            value |= getConfigValue(stages, seeds, states, 11) << 24;
	        }

	        return value;
	    }

	    private static int getConfigValue(int[] stages, int[] seeds, int[] states, int index) {
	        if (index >= stages.length) return 0;
	        return getConfigValue(stages[index], seeds[index], states[index]);
	    }*/
		public int[] getAllotmentIndices(Player player) {
			// Returns the actual enum IDs: {Patch for Shift 0, Patch for Shift 8}
			if (player.withinDistance(2807, 3464, player.getX(), player.getY(), 50)) {
				return new int[]{0, 1}; // Catherby (Fixed: Was {1, 0} causing them to swap!)
			}
			if (player.withinDistance(2663, 3374, player.getX(), player.getY(), 50)) {
				return new int[]{2, 3}; // Ardougne (Fixed: Was {0, 1} cloning Catherby's crops)
			}
			if (player.withinDistance(3056, 3309, player.getX(), player.getY(), 50)) {
				return new int[]{4, 5}; // Falador
			}
			if (player.withinDistance(3603, 3527, player.getX(), player.getY(), 50)) {
				return new int[]{6, 7}; // Morytania / Phasmatys
			}
			if (player.withinDistance(1736, 3553, player.getX(), player.getY(), 50)) {
				return new int[]{8, 9}; // Zeah / Kourend
			}
			if (player.withinDistance(1248, 3728, player.getX(), player.getY(), 150)) {
				return new int[]{10, 11}; // Farming Guild
			}
			// Default fallback to Catherby to prevent out-of-bounds errors
			return new int[]{0, 1};
		}

	    // Determine patch index layout per region
	public int flowerIndex = 0, herbIndex = 0;
	public int allotmentNorthIndex = 0, allotmentSouthIndex = 1;
	public void updateFarmingStates() {
		// OSRS strictly standardizes these shifts across all farming regions
		int ALLOTMENT_SHIFT_1 = 0;
		int ALLOTMENT_SHIFT_2 = 8;
		int FLOWER_SHIFT = 16;
		int HERB_SHIFT = 24;

		// Determine which Config ID to send to the client based on region
		int configId = 529;
		if (player.withinDistance(1248, 3728, player.getX(), player.getY(), 150)) {
			configId = 1057; // Farming Guild uses a different config, but the EXACT same shifts!
		}

		// Get the correct array indices for the region the player is standing in
		int flowerIndex = player.getFlowers().getFlowerIndex(player);
		int herbIndex = player.getHerbs().getHerbIndex(player);
		int[] allotmentIndices = getAllotmentIndices(player);
		int patch1 = allotmentIndices[0];
		int patch2 = allotmentIndices[1];

		int configValue = 0;

		// 1. Calculate Allotment 1 (Shift 0)
		if (patch1 < allotmentStages.length) {
			int state1 = getConfigValue(allotmentStages[patch1], allotmentSeeds[patch1], allotmentState[patch1], patch1);
			configValue |= (state1 << ALLOTMENT_SHIFT_1);
		}

		// 2. Calculate Allotment 2 (Shift 8)
		if (patch2 < allotmentStages.length) {
			int state2 = getConfigValue(allotmentStages[patch2], allotmentSeeds[patch2], allotmentState[patch2], patch2);
			configValue |= (state2 << ALLOTMENT_SHIFT_2);
		}

		// 3. Calculate Flowers (Shift 16)
		if (flowerIndex < player.getFlowers().flowerStages.length) {
			int flowerState = player.getFlowers().getConfigValue(player.getFlowers().flowerStages[flowerIndex], player.getFlowers().flowerSeeds[flowerIndex], player.getFlowers().flowerState[flowerIndex], flowerIndex);
			configValue |= (flowerState << FLOWER_SHIFT);
		}

		// 4. Calculate Herbs (Shift 24)
		if (herbIndex < player.getHerbs().herbStages.length) {
			int herbState = player.getHerbs().getConfigValue(player.getHerbs().herbStages[herbIndex], player.getHerbs().herbSeeds[herbIndex], player.getHerbs().herbState[herbIndex], herbIndex);
			configValue |= (herbState << HERB_SHIFT);
		}

		// Send the final compiled bit-mask to the client!
		player.getPA().sendConfig(configId, configValue);
		player.getCompost().updateAllBinConfigs();
	}
	

	public void setVarbit(Player player, int varbitId, int value) {
	    VarBit bit = VarBit.cache[varbitId];
	    if (bit == null) return;

	    int configId = bit.setting;
	    int low = bit.low;
	    int high = bit.high;
	    int mask = (1 << (high - low + 1)) - 1;

	    int oldConfig = player.getConfigValue(configId); // You need to store sent configs per player
	    int newConfig = (oldConfig & ~(mask << low)) | ((value & mask) << low);

	    player.getPA().sendConfig(configId, newConfig);
	    player.setConfigValue(configId, newConfig); // Save the new value so you can read it later
	}

public int getConfig() {
   // if (isEmpty() || needsWeeding()) {
      //  return stage.ordinal();
   // }
    return (allotmentStages[0] << 6);
}
	/* getting the different config values */

	public int getConfigValue(int allotmentStage, int seedId, int plantState,
			int index) {
		AllotmentData allotmentData = AllotmentData.forId(seedId);
		switch (allotmentStage) {
		case 0:// weed
			return (GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (GROWING << 6) + 0x01;
		case 2:
			return (GROWING << 6) + 0x02;
		case 3:
			return (GROWING << 6) + 0x03;
		}
		if (allotmentData == null) {
			return -1;
		}
		if (allotmentData.getEndingState() == allotmentData.getStartingState()
				+ allotmentStage - 1) {
			hasFullyGrown[index] = true;
		}

		return (getPlantState(plantState) << 6)
				+ allotmentData.getStartingState() + allotmentStage - 4;
	}

	/* getting the plant states */

	public int getPlantState(int plantState) {
		switch (plantState) {
		case 0:
			return GROWING;
		case 1:
			return WATERED;
		case 2:
			return DISEASED;
		case 3:
			return DEAD;
		}
		return -1;
	}

	/* calculating the disease chance and making the plant grow */

	public void doCalculations() {
		for (int i = 0; i < allotmentSeeds.length; i++) {
			AllotmentData allotmentData = AllotmentData
					.forId(allotmentSeeds[i]);
			if (allotmentData == null) {
				continue;
			}
			/**
			 * Grow back weeds
			 */
			if (allotmentStages[i] > 0 && allotmentStages[i] <= 3
					&& Server.getMinutesCounter() - allotmentTimer[i] >= 5) {
				allotmentStages[i]--;
				allotmentTimer[i] = Server.getMinutesCounter();
				updateFarmingStates();
			}

			int nbStates = allotmentData.getEndingState()
					- allotmentData.getStartingState();
			long growth = allotmentData.getGrowthTime();
			//double cycleTime = (double) (growth / nbStates);
			long difference = Server.getMinutesCounter()
					- allotmentTimer[i];
			int state = (int) (difference * nbStates / growth);
			if (allotmentTimer[i] == 0 || allotmentState[i] == 3
					|| state > nbStates) {
				continue;
			}
			if (4 + state != allotmentStages[i]) {
				allotmentStages[i] = 4 + state;
				if (allotmentStages[i] <= 4 + state)
					for (int j = allotmentStages[i]; j <= 4 + state; j++)
						doStateCalculation(i);
				updateFarmingStates();
			}
		}
	}

	public void modifyStage(int i) {
		AllotmentData bushesData = AllotmentData.forId(allotmentSeeds[i]);
		if (bushesData == null)
			return;
		long difference = Server.getMinutesCounter() - allotmentTimer[i];
		long growth = bushesData.getGrowthTime();
		int nbStates = bushesData.getEndingState()
				- bushesData.getStartingState();
		int state = (int) (difference * nbStates / growth);
		allotmentStages[i] = 4 + state;
		updateFarmingStates();

	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
		if (allotmentState[index] == 3) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (allotmentState[index] == 2) {
			if (allotmentWatched[index]) {
				allotmentState[index] = 0;
				AllotmentData allotmentData = AllotmentData
						.forId(allotmentSeeds[index]);
				if (allotmentData == null)
					return;
				int difference = allotmentData.getEndingState()
						- allotmentData.getStartingState();
				int growth = allotmentData.getGrowthTime();
				allotmentTimer[index] += (growth / difference);
				modifyStage(index);
			} else {
				allotmentState[index] = 3;
			}
		}

		if (allotmentState[index] == 1) {
			diseaseChance[index] *= 2;
			allotmentState[index] = 0;
		}

		if (allotmentState[index] == 5 && allotmentStages[index] != 3) {
			allotmentState[index] = 0;
		}

		if (allotmentState[index] == 0 && allotmentStages[index] >= 5
				&& !hasFullyGrown[index]) {
			// handleFlowerProtection(index);
		}
	}

	/* watering the patch */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null) {
			return false;
		}
		AllotmentData allotmentData = AllotmentData
				.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		if (allotmentData == null) {
			return false;
		}
		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 1
				|| allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 1
				|| allotmentStages[allotmentFieldsData.getAllotmentIndex()] == allotmentData
						.getEndingState()
						- allotmentData.getStartingState()
						+ 4) {
			player.sendMessage("This patch doesn't need watering.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(itemId == 5333 ? itemId - 2 : itemId - 1, 1);

		if (!player.getItems().playerHasItem(FarmingConstants.RAKE)) {
			player.getDH().sendStatement(
					"You need a seed dibber to plant seed here.");
			return true;
		}
		player.sendMessage("You water the patch.");
		player.startAnimation(
				FarmingConstants.WATERING_CAN_ANIM);

		player.stopPlayer(true);
		   CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= WATERING_CHANCE;
				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 1;
				container.stop();
			}

			@Override
			public void stop() {
				updateFarmingStates();
				player.stopPlayer(false);
				player.getPA().resetAnimation();
				
			}
		}, 5);
		return true;
	}

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		int finalAnimation;
		int finalDelay;
		if (allotmentFieldsData == null
				|| (itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE)) {
			return false;
		}
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
			return true;
		}
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 3) {
			if (!player.getItems().playerHasItem(FarmingConstants.RAKE)) {
				player.getDH().sendStatement("You need a rake to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.RAKING_ANIM;
				finalDelay = 5;
			}
		} else {
			if (!player.getItems().playerHasItem(FarmingConstants.SPADE)) {
				player.getDH().sendStatement("You need a spade to clear this path.");
				return true;
			} else {
				finalAnimation = FarmingConstants.SPADE_ANIM;
				finalDelay = 3;
			}
		}
		final int animation = finalAnimation;
		player.stopPlayer(true);
		player.startAnimation(animation);
		   CycleEventHandler.getSingleton().addEvent(10016, player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				player.startAnimation(animation);
				if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] <= 2) {
					allotmentStages[allotmentFieldsData.getAllotmentIndex()]++;
					player.getItems().addItem(6055, 1);
				} else {
					allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
					container.stop();
				}
				player.getPA().addSkillXP((int)CLEARING_EXPERIENCE, player.playerFarming);
				allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Server.getMinutesCounter();
				updateFarmingStates();
				if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				resetAllotments(allotmentFieldsData.getAllotmentIndex());
				player.sendMessage("You clear the patch.");
				player.stopPlayer(false);
				player.getPA().resetAnimation();
				
			}
		}, finalDelay);
		return true;

	}

	/* planting the seeds */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		final AllotmentData allotmentData = AllotmentData.forId(seedId);
		if (allotmentFieldsData == null || allotmentData == null) {
			return false;
		}
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3) {
			player.sendMessage("You can't plant a seed here.");
			return false;
		}
		int io = allotmentData.getSeedAmount();
		if (!player.getItems().playerHasItem(FarmingConstants.SEED_DIBBER)) {
			player.getDH().sendStatement(
					"You need a seed dibber to plant seed here.");
			return true;
		}
		if (player.getItems().getItemAmount(allotmentData.getSeedId()) < allotmentData
				.getSeedAmount()) {
			player.getDH().sendStatement( "You need atleast "
					+ allotmentData.getSeedAmount() + " seeds to plant here.");
			return true;
		}
		player.startAnimation(FarmingConstants.SEED_DIBBING);
		allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 4;

		player.stopPlayer(true);
		  CycleEventHandler.getSingleton().addEvent(10016, player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;
				allotmentSeeds[allotmentFieldsData.getAllotmentIndex()] = seedId;
				allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Server.getMinutesCounter();
				player.getPA().addSkillXP((int)allotmentData.getPlantingXp(), player.playerFarming);
				player.getItems().deleteItem2(seedId, io);
				//System.out.println("Seed:" + seedId + " amt: " +io);
				container.stop();
			}

			@Override
			public void stop() {
				updateFarmingStates();
				player.stopPlayer(false);
			}
		}, 3);
		return true;
	}

	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null) {
			return false;
		}
		final AllotmentData allotmentData = AllotmentData
				.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		if (allotmentData == null) {
			return false;
		}
		if (!player.getItems().playerHasItem(FarmingConstants.SPADE)) {
			player.getDH().sendStatement("You need a spade to harvest here.");
			return true;
		}
		player.startAnimation(FarmingConstants.SPADE_ANIM);
		  CycleEventHandler.getSingleton().addEvent(10016, player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 0) {
					allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] = 1 + (START_HARVEST_AMOUNT + Misc
							.random(END_HARVEST_AMOUNT - START_HARVEST_AMOUNT)) * (1);
				}
				if (allotmentHarvest[allotmentFieldsData.getAllotmentIndex()] == 1) {
					resetAllotments(allotmentFieldsData.getAllotmentIndex());
					allotmentStages[allotmentFieldsData.getAllotmentIndex()] = 3;
					allotmentTimer[allotmentFieldsData.getAllotmentIndex()] = Server
							.getMinutesCounter();
					container.stop();
					return;
				}
				if (player.getItems().freeSlots()  <= 0) {
					container.stop();
					return;
				}
				allotmentHarvest[allotmentFieldsData.getAllotmentIndex()]--;
				player.startAnimation(
						FarmingConstants.SPADE_ANIM);
				player.sendMessage("You harvest the crop, and get some vegetables.");
				player.getItems().addItem(allotmentData.getHarvestId(), 1);
				player.getPA().addSkillXP((int)allotmentData.getHarvestXp(), player.playerFarming);
			}

			@Override
			public void stop() {
				updateFarmingStates();
				player.getPA().resetAnimation();
			}
		}, 2);
		return true;
	}

	/* putting compost onto the plant */

	public boolean putCompost(int objectX, int objectY, final int itemId) {
		if (itemId != 6032 && itemId != 6034) {
			return false;
		}
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null) {
			return false;
		}
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] != 3
				|| allotmentState[allotmentFieldsData.getAllotmentIndex()] == 5) {
			player.sendMessage("This patch doesn't need compost.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(1925, 1);

		player.sendMessage(
				"You pour some " + (itemId == 6034 ? "super" : "")
						+ "compost on the patch.");
		player.startAnimation(FarmingConstants.PUTTING_COMPOST);
		player.getPA().addSkillXP((int)(itemId == 6034 ? Compost.SUPER_COMPOST_EXP_USE : Compost.COMPOST_EXP_USE), player.playerFarming);
		player.stopPlayer(true);
		CycleEventHandler.getSingleton().addEvent(10016, player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
				diseaseChance[allotmentFieldsData.getAllotmentIndex()] *= itemId == 6032 ? COMPOST_CHANCE
						: SUPERCOMPOST_CHANCE;
				allotmentState[allotmentFieldsData.getAllotmentIndex()] = 5;
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
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData
				.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		final AllotmentData allotmentData = AllotmentData
				.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 2) {
			player.getDH().sendStatement("This plant is diseased. Use a plant cure on it to cure it, ",
							"or clear the patch with a spade.");
			return true;
		} else if (allotmentState[allotmentFieldsData.getAllotmentIndex()] == 3) {
			player.getDH().sendStatement("This plant is dead. You did not cure it while it was diseased.",
							"Clear the patch with a spade.");
			return true;
		}
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 0) {
			player.getDH().sendStatement("This is an allotment patch. The soil has not been treated.",
							"The patch needs weeding.");
		} else if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3) {
			player.getDH().sendStatement("This is an allotment patch. The soil has not been treated.",
							"The patch is empty and weeded.");
		} else if (inspectData != null && allotmentData != null) {
			player.sendMessage(
					"You bend down and start to inspect the patch...");

			player.startAnimation(1331);
			player.stopPlayer(true);
			 CycleEventHandler.getSingleton().addEvent(10017, player, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
					if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] - 4 < inspectData
							.getMessages().length - 2) {
						player.getDH().sendStatement2(inspectData.getMessages()[allotmentStages[allotmentFieldsData
										.getAllotmentIndex()] - 4]);
					} else if (allotmentStages[allotmentFieldsData
							.getAllotmentIndex()] < allotmentData
							.getEndingState()
							- allotmentData.getStartingState() + 2) {
						player.getDH().sendStatement2(inspectData.getMessages()[inspectData.getMessages().length - 2]);
					} else {
						player.getDH().sendStatement2(inspectData.getMessages()[inspectData.getMessages().length - 1]);
					}
					container.stop();
				}

				@Override
				public void stop() {
					player.startAnimation(1332);
					player.stopPlayer(false);
					// player.reset();
				}
			}, 5);
		}
		return true;
	}

	/* opening the corresponding guide about the patch */

	public boolean guide(int objectX, int objectY) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null) {
			return false;
		}
		//if (!SkillConstants.getEnabled(player.playerFarming)) {
			//player.sendMessage("This skill is currently disabled.");
			//return true;
		//}
		//player.getSkillInterfaces().farmingComplex(1);
		//player.getSkillInterfaces().selected = 20;
		return true;
	}

	/* protects the patch with the flowers */

	public void handleFlowerProtection(int index) {
		AllotmentData allotmentData = AllotmentData
				.forId(allotmentSeeds[index]);
		if (allotmentData == null) {
			return;
		}
		double chance = diseaseChance[index] * allotmentData.getDiseaseChance();
		@SuppressWarnings("unused")
		int maxChance = (int) chance * 100;
		int indexGiven = 0;
		if (!allotmentWatched[index]) {// Misc.random(100) <= maxChance) {
			switch (index) {
			case 0:
			case 1:
				indexGiven = 3;
				break;
			case 2:
			case 3:
				indexGiven = 2;
				break;
			case 4:
			case 5:
				indexGiven = 1;
				break;
			case 6:
			case 7:
				indexGiven = 0;
				break;

			}
			if (player.getFlowers().flowerSeeds[indexGiven] >= 0x21
					&& player.getFlowers().flowerSeeds[indexGiven] <= 0x24) {
				if (allotmentData.getFlowerProtect() == Flowers.SCARECROW) {
					return;
				}
			}
			if (player.getFlowers().flowerState[indexGiven] != 3
					&& player.getFlowers().hasFullyGrown[indexGiven]
					&& player.getFlowers().flowerSeeds[indexGiven] == allotmentData
							.getFlowerProtect()) {
				player.getFlowers().flowerState[indexGiven] = 3;
				updateFarmingStates();
			} else {
				allotmentState[index] = 2;
			}
		}

	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null || itemId != 6036) {
			return false;
		}
		final AllotmentData allotmentData = AllotmentData
				.forId(allotmentSeeds[allotmentFieldsData.getAllotmentIndex()]);
		if (allotmentData == null) {
			return false;
		}
		//if (!SkillConstants.getEnabled(player.playerFarming)) {
		//	player.sendMessage("This skill is currently disabled.");
			//return true;
		//}
		if (allotmentState[allotmentFieldsData.getAllotmentIndex()] != 2) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(229, 1);
		player.startAnimation(FarmingConstants.CURING_ANIM);
		player.stopPlayer(true);
		allotmentState[allotmentFieldsData.getAllotmentIndex()] = 0;
		  CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				player.sendMessage(
						"You cure the plant with a plant cure.");
				container.stop();
			}

			@Override
			public void stop() {
				updateFarmingStates();
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, 7);

		return true;

	}

	/* reseting the patches */

	private void resetAllotments(int index) {
		allotmentSeeds[index] = 0;
		allotmentState[index] = 0;
		diseaseChance[index] = 1;
		allotmentHarvest[index] = 0;
		allotmentWatched[index] = false;
		hasFullyGrown[index] = false;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
		final AllotmentFieldsData allotmentFieldsData = AllotmentFieldsData
				.forIdPosition(objectX, objectY);
		if (allotmentFieldsData == null)
			return false;
		if (allotmentStages[allotmentFieldsData.getAllotmentIndex()] == 3)
			return true;
		return false;
	}

}
