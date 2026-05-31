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
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.farming.plants.FlowerData;
import server.util.Misc;

/**
 * Created by IntelliJ IDEA. User: vayken Date: 24/02/12 Time: 20:34 To change
 * this template use File | Settings | File Templates.
 */
public class Flowers { // todo scarecrow 6059

	private Player player;

	// set of global constants for Farming

	private static final double WATERING_CHANCE = 0.5;
	private static final double COMPOST_CHANCE = 0.9;
	private static final double SUPERCOMPOST_CHANCE = 0.7;
	private static final double CLEARING_EXPERIENCE = 4;
	public static final int SCARECROW = 6059;

	public Flowers(Player player2) {
		this.player = player2;
	}

	// Farming data
	public int[] flowerStages = new int[6];
	public int[] flowerSeeds = new int[6];
	public int[] flowerState = new int[6];
	public long[] flowerTimer = new long[6];
	public double[] diseaseChance = { 1, 1, 1, 1, 1, 1 };
	public boolean[] hasFullyGrown = { false, false, false, false, false, false };

	/* set of the constants for the patch */

	// states - 2 bits plant - 6 bits
	public static final int GROWING = 0x00;
	public static final int WATERED = 0x01;
	public static final int DISEASED = 0x02;
	public static final int DEAD = 0x03;

	public static final int FLOWER_PATCH_CONFIGS = 529;

	/* This is the enum data about the different patches */

	public enum FlowerFieldsData {
		ARDOUGNE(0, new Point[] { new Point(2666, 3374), new Point(2667, 3375) }),
		CATHERBY( 1, new Point[] { new Point(2809, 3463), new Point(2810, 3464) }), 
		FALADOR( 2, new Point[] { new Point(3054, 3307), new Point(3055, 3308) }),
		PHASMATYS( 3, new Point[] { new Point(3601, 3525), new Point(3602, 3526) }),
		KOUREND( 4, new Point[] { new Point(1734, 3554), new Point(1735, 3555) }),
		FARMGUILD( 5, new Point[] { new Point(1260, 3725), new Point(1261, 3726) });
		
		private int flowerIndex;
		private Point[] flowerPosition;

		FlowerFieldsData(int flowerIndex, Point[] flowerPosition) {
			this.flowerIndex = flowerIndex;
			this.flowerPosition = flowerPosition;
		}

		public static FlowerFieldsData forIdPosition(Point point) {
			for (FlowerFieldsData flowerFieldsData : FlowerFieldsData.values()) {
				if (FarmingConstants.inRangeArea(
						flowerFieldsData.getFlowerPosition()[0],
						flowerFieldsData.getFlowerPosition()[1], point)) {
					return flowerFieldsData;
				}
			}
			return null;
		}

		public int getFlowerIndex() {
			return flowerIndex;
		}

		public Point[] getFlowerPosition() {
			return flowerPosition;
		}
	}
	public int getFlowerIndex(Player player) {
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

	/* update all the patch states */
	public void updateFlowerStates() {
	    // Bit shifts in config 529
	    int FLOWER_SHIFT = 16;
	    int FLOWER_SHIFT2 = 8;
	    int configid = 529;
	    // Determine patch index layout per region
	    int flowerIndex = getFlowerIndex(player);

	    if (flowerIndex == -1)
	    	return;
	    // Build config values
	    int[] flowerConfigValues = new int[player.getFlowers().flowerStages.length];
	    
	    for (int i = 0; i < flowerConfigValues.length; i++) {
	        flowerConfigValues[i] = player.getFlowers().getConfigValue(
	            player.getFlowers().flowerStages[i],
	            player.getFlowers().flowerSeeds[i],
	            player.getFlowers().flowerState[i], i);
	    }


	    // Combine values using the region-specific index map
	    int configValue = 0;
	    int configValue2 = 0;
	    if (flowerIndex == 5) {
		    configid = 1057;
	    }else
	    	configid = 529;
	    if(configid == 529) {
		    if (flowerConfigValues.length > flowerIndex)
		        configValue |= flowerConfigValues[flowerIndex] << FLOWER_SHIFT;
		    	player.getPA().sendConfig(529, configValue);
	
	    } else if(configid == 1057) {
		    if (flowerConfigValues.length > flowerIndex)
		        configValue2 |= flowerConfigValues[flowerIndex] << FLOWER_SHIFT2;
		    	player.getPA().sendConfig(1057, configValue2);
	
	
	    }
	    System.out.println("Config 529 value = " + configValue);
	    System.out.println("Flower patches: " + Arrays.toString(flowerConfigValues));

	    // Send to client
	    
	}	

	/* getting the different config values */

	public int getConfigValue(int flowerStage, int seedId, int plantState,
			int index) {
		if (flowerSeeds[index] >= 0x21 && flowerSeeds[index] <= 0x24
				&& flowerStages[index] > 3) {
			return (GROWING << 6) + flowerSeeds[index];
		}
		FlowerData flowerData = FlowerData.forId(seedId);
		switch (flowerStage) {
		case 0:// weed
			return (GROWING << 6) + 0x00;
		case 1:// weed cleared
			return (GROWING << 6) + 0x01;
		case 2:
			return (GROWING << 6) + 0x02;
		case 3:
			return (GROWING << 6) + 0x03;
		}
		if (flowerData == null) {
			return -1;
		}
		if (flowerData.getEndingState() == flowerData.getStartingState()
				+ flowerStage - 2) {
			hasFullyGrown[index] = true;
		}
		return (getPlantState(plantState) << 6) + flowerData.getStartingState()
				+ flowerStage - 4;
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
		for (int i = 0; i < flowerSeeds.length; i++) {
			if (flowerStages[i] > 0 && flowerStages[i] <= 3
					&& Server.getMinutesCounter() - flowerTimer[i] >= 5) {
				flowerStages[i]--;
				flowerTimer[i] = Server.getMinutesCounter();
				player.getAllotment().updateFarmingStates();
			}
			if (Server.getMinutesCounter() - flowerTimer[i] >= 5
					&& flowerSeeds[i] > 0x21 && flowerSeeds[i] <= 0x24) {
				flowerSeeds[i]--;
				player.getAllotment().updateFarmingStates();
				return;
			}

			FlowerData flowerData = FlowerData.forId(flowerSeeds[i]);
			if (flowerData == null) {
				continue;
			}
			long difference = Server.getMinutesCounter() - flowerTimer[i];
			long growth = flowerData.getGrowthTime();
			int nbStates = flowerData.getEndingState()
					- flowerData.getStartingState();
			int state = (int) (difference * nbStates / growth);
			if (flowerState[i] == 3 || flowerSeeds[i] == 0x21
					|| flowerTimer[i] == 0 || state > nbStates) {
				continue;
			}

			if (4 + state != flowerStages[i]) {
				flowerStages[i] = 4 + state;
				doStateCalculation(i);
				player.getAllotment().updateFarmingStates();
			}
		}
	}

	/* calculations about the diseasing chance */

	public void doStateCalculation(int index) {
		if (flowerState[index] == 3) {
			return;
		}
		// if the patch is diseased, it dies, if its watched by a farmer, it
		// goes back to normal
		if (flowerState[index] == 2) {
			flowerState[index] = 3;
		}

		if (flowerState[index] == 1 || flowerState[index] == 5
				&& flowerStages[index] != 3) {
			diseaseChance[index] *= 2;
			flowerState[index] = 0;
		}
		if (flowerState[index] == 0 && flowerStages[index] >= 5
				&& !hasFullyGrown[index]) {
			FlowerData flowerData = FlowerData.forId(flowerSeeds[index]);
			if (flowerData == null) {
				return;
			}
			double chance = diseaseChance[index]
					* flowerData.getDiseaseChance();
			int maxChance = (int) (chance * 100);

			if (Misc.random(100) <= maxChance) {
				flowerState[index] = 2;
			}
		}
	}

	/* watering the patch */

	public boolean waterPatch(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		FlowerData flowerData = FlowerData.forId(flowerSeeds[flowerFieldsData
				.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (flowerState[flowerFieldsData.getFlowerIndex()] == 1
				|| flowerStages[flowerFieldsData.getFlowerIndex()] <= 1
				|| flowerStages[flowerFieldsData.getFlowerIndex()] == flowerData
						.getEndingState() - flowerData.getStartingState() + 4) {
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
				diseaseChance[flowerFieldsData.getFlowerIndex()] *= WATERING_CHANCE;
				flowerState[flowerFieldsData.getFlowerIndex()] = 1;
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

	/* clearing the patch with a rake of a spade */

	public boolean clearPatch(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		int finalAnimation;
		int finalDelay;
		if (flowerFieldsData == null
				|| (itemId != FarmingConstants.RAKE && itemId != FarmingConstants.SPADE)) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			return true;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 3) {
			if (!player.getItems().playerHasItem(FarmingConstants.RAKE)) {
				player.getDH().sendStatement(
						"You need a rake to clear this path.");
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
				if (flowerStages[flowerFieldsData.getFlowerIndex()] <= 2) {
					flowerStages[flowerFieldsData.getFlowerIndex()]++;
					player.getItems().addItem(6055, 1);
				} else {
					flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
					container.stop();
				}
				player.getPA().addSkillXP((int)CLEARING_EXPERIENCE, player.playerFarming);
				flowerTimer[flowerFieldsData.getFlowerIndex()] = Server.getMinutesCounter();
				
				player.getAllotment().updateFarmingStates();
				if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
					container.stop();
					return;
				}
			}

			@Override
			public void stop() {
				resetFlowers(flowerFieldsData.getFlowerIndex());
				player.sendMessage("You clear the patch.");
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, finalDelay);
		return true;
	}

	/* planting the seeds */

	public boolean plantSeed(int objectX, int objectY, final int seedId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		final FlowerData flowerData = FlowerData.forId(seedId);
		if (flowerFieldsData == null || flowerData == null) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
			player.getDH().sendStatement(
					"You can't plant a seed here.");
			return false;
		}
		if (flowerData.getLevelRequired() > player.getSkills().getLevel(Skill.FARMING)) {
			player.getDH().sendStatement( "You need a farming level of "
							+ flowerData.getLevelRequired()
							+ " to plant this seed.");
			return true;
		}
		if (!player.getItems().playerHasItem(FarmingConstants.SEED_DIBBER)) {
			player.getDH().sendStatement(
					"You need a seed dibber to plant seed here.");
			return true;
		}

		player.startAnimation(FarmingConstants.SEED_DIBBING);
		flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
		player.getItems().deleteItem(seedId, 1);

		player.stopPlayer(true);
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
				flowerState[flowerFieldsData.getFlowerIndex()] = 0;
				flowerSeeds[flowerFieldsData.getFlowerIndex()] = seedId;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = Server.getMinutesCounter();
				player.getPA().addSkillXP((int)flowerData.getPlantingXp(), player.playerFarming);
				container.stop();
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
				player.stopPlayer(false);
			}
		}, 3);
		return true;
	}

	@SuppressWarnings("unused")
	public void displayAll() {
		for (int i = 0; i < flowerStages.length; i++) {
			System.out.println("index : " + i);
			System.out.println("state : " + flowerState[i]);
			System.out.println("seeds : " + flowerSeeds[i]);
			System.out.println("level : " + flowerStages[i]);
			System.out.println("timer : " + flowerTimer[i]);
			System.out.println("disease chance : " + diseaseChance[i]);
			System.out
					.println("-----------------------------------------------------------------");
		}
	}

	/* harvesting the plant resulted */

	public boolean harvest(int objectX, int objectY) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		final FlowerData flowerData = FlowerData
				.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (!player.getItems().playerHasItem(FarmingConstants.SPADE)) {
			player.getDH().sendStatement(
				"You need a spade to harvest here.");
			return true;
		}

		player.startAnimation(FarmingConstants.SPADE_ANIM);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				resetFlowers(flowerFieldsData.getFlowerIndex());
				flowerStages[flowerFieldsData.getFlowerIndex()] = 3;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = Server.getMinutesCounter();
				player.startAnimation(
						FarmingConstants.SPADE_ANIM);
				player.sendMessage(
						"You harvest the crop, and get some vegetables.");
				player.getItems().addItem(flowerData.getHarvestId(), flowerData.getHarvestId() == 5099 || flowerData.getHarvestId() == 5100 ? 3 : 1);
				player.getPA().addSkillXP((int)flowerData.getHarvestXp(), player.playerFarming);
				container.stop();
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
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
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3
				|| flowerState[flowerFieldsData.getFlowerIndex()] == 5) {
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
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
				diseaseChance[flowerFieldsData.getFlowerIndex()] *= itemId == 6032 ? COMPOST_CHANCE
						: SUPERCOMPOST_CHANCE;
				flowerState[flowerFieldsData.getFlowerIndex()] = 5;
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
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		final InspectData inspectData = InspectData
				.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		final FlowerData flowerData = FlowerData
				.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerState[flowerFieldsData.getFlowerIndex()] == 2) {
			player.getDH().sendStatement("This plant is diseased. Use a plant cure on it to cure it, ",
							"or clear the patch with a spade.");
			return true;
		} else if (flowerState[flowerFieldsData.getFlowerIndex()] == 3) {
			player.getDH().sendStatement("This plant is dead. You did not cure it while it was diseased.",
						"Clear the patch with a spade.");
			return true;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 0) {
			player.getDH().sendStatement(
					"This is a flower patch. The soil has not been treated.",
					"The patch needs weeding.");
		} else if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3) {
			player.getDH().sendStatement(
					"This is a flower patch. The soil has not been treated.",
					"The patch is empty and weeded.");
		} else if (inspectData != null && flowerData != null) {
			player.sendMessage(
					"You bend down and start to inspect the patch...");

			player.startAnimation(1331);
			player.stopPlayer(true);
			CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
					if (flowerStages[flowerFieldsData.getFlowerIndex()] - 4 < inspectData
							.getMessages().length - 2) {
						player.getDH().sendStatement2( inspectData
								.getMessages()[flowerStages[flowerFieldsData
								.getFlowerIndex()] - 4]);
					} else if (flowerStages[flowerFieldsData.getFlowerIndex()] < flowerData
							.getEndingState()
							- flowerData.getStartingState()
							+ 4) {
						player.getDH().sendStatement2(
								inspectData.getMessages()[inspectData
										.getMessages().length - 2]);
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
					// player.reset();
				}
			}, 5);
		}
		return true;
	}

	/* opening the corresponding guide about the patch */

	public boolean guide(int objectX, int objectY) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null) {
			return false;
		}
		//player.getSkillInterfaces().farmingComplex(6);
		//player.getSkillInterfaces().selected = 20;
		return true;
	}

	/* Curing the plant */

	public boolean curePlant(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null || itemId != 6036) {
			return false;
		}
		final FlowerData flowerData = FlowerData
				.forId(flowerSeeds[flowerFieldsData.getFlowerIndex()]);
		if (flowerData == null) {
			return false;
		}
		if (flowerState[flowerFieldsData.getFlowerIndex()] != 2) {
			player.sendMessage("This plant doesn't need to be cured.");
			return true;
		}
		player.getItems().deleteItem(itemId, 1);
		player.getItems().addItem(229, 1);
		player.startAnimation(FarmingConstants.CURING_ANIM);
		flowerState[flowerFieldsData.getFlowerIndex()] = 0;
		player.stopPlayer(true);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				player.sendMessage(
						"You cure the plant with a plant cure.");
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

	/* Planting scarecrow to push off the birds */

	public boolean plantScareCrow(int objectX, int objectY, int itemId) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null || itemId != SCARECROW) {
			return false;
		}
		if (flowerStages[flowerFieldsData.getFlowerIndex()] != 3) {
			player.sendMessage(
					"You need to clear the patch before planting a scarecrow");
			return false;
		}
		player.getItems().deleteItem(SCARECROW, 1);
		player.startAnimation(832);
		player.stopPlayer(true);
		 CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
	            @Override
	            public void execute(CycleEventContainer container) {
				player.sendMessage("You put a scarecrow on the flower patch, and some weeds start to grow around it.");
				flowerSeeds[flowerFieldsData.getFlowerIndex()] = 0x24;
				flowerStages[flowerFieldsData.getFlowerIndex()] = 4;
				flowerTimer[flowerFieldsData.getFlowerIndex()] = Server.getMinutesCounter();
				container.stop();
			}

			@Override
			public void stop() {
				player.getAllotment().updateFarmingStates();
				player.stopPlayer(false);
				player.getPA().resetAnimation();
			}
		}, 2);
		return true;
	}

	/* reseting the patches */

	private void resetFlowers(int index) {
		flowerSeeds[index] = 0;
		flowerState[index] = 0;
		diseaseChance[index] = 1;
	}

	/* checking if the patch is raked */

	public boolean checkIfRaked(int objectX, int objectY) {
		final FlowerFieldsData flowerFieldsData = FlowerFieldsData
				.forIdPosition(new Point(objectX, objectY));
		if (flowerFieldsData == null)
			return false;
		if (flowerStages[flowerFieldsData.getFlowerIndex()] == 3)
			return true;
		return false;
	}

}
