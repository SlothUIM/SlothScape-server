package server.model.players.skills;

import server.model.players.Client;
import server.model.players.Player;
import server.util.Misc;
import server.event.*;
import server.Config;
import server.model.players.Sound;
import server.model.objects.Object;
import server.model.items.ItemAssistant;
import java.security.SecureRandom;

public class Cooking {

	Player c;
public static boolean COOKING = true;
	public static boolean view190 = false;
	private static SecureRandom cookingRandom = new SecureRandom(); // The random factor
	public Cooking(Player player) {
		this.c = player;
	}
	public static String getLine(Player c2) {
		return "\\n\\n\\n\\n\\n";
	}
	private static enum CookingItems {
		//raw, cooked, burnt, levelreq, exp, stopburn, stopburn w/gloves, name
		SHRIMP(317, 315, 323, 1, 30, 34, 30, "shrimp"), 
		SHRIMP_TUT(2514, 315, 7954, 1, 30, 34, 30, "shrimp"), 
				SARDINE(327, 325, 369, 1, 40, 38, 38, "sardine"), 
				HERRING(345, 347, 357, 5, 50, 41, 41, "herring"), 
				TROUT(335, 333, 343, 15, 70, 50, 50, "trout"), 
				TUNA(359, 361, 367, 30, 100, 64, 63, "tuna"), 
				ANCHOVIES(321, 319, 323, 5, 45, 34, 34, "anchovies"), 
				RAW_BEEF(2132, 2142, 2146, 1, 30, 33, 33, "raw beef"), 
				RAW_RAT(2134, 2142, 2146, 1, 30, 33, 33, "raw rat meat"), 
				BURNT_MEAT(2142, 2146, 2146, 1, 1, 100, 100, "cooked meat"),
				RAW_CHICKEN(2138, 2140, 2144, 1, 30, 33, 33, "raw chicken"), 
				RAW_BEAR_MEAT(2136, 2142, 2146, 1, 30, 33, 33, "raw bear meat"), 
				MACKERAL(353, 355, 357, 10, 60, 45, 45, "mackeral"), 
				SALMON(331, 329, 343, 25, 90, 58, 55, "salmon"),
				UNCOOKED_BERRY_PIE(2321, 2325, 2329, 10, 78, 50, 50, "uncooked pie"),
				PIKE(349, 351, 343, 20, 80, 59, 59, "pike"), 
				KARAMBWAN(3142, 3144, 3146, 1, 80, 20, 20, "karambwan"), 
				LOBSTER(377, 379, 381, 40, 120, 74, 68, "lobster"), 
				SWORDFISH(371, 373, 375, 50, 140, 86, 81, "swordfish"),
				MONKFISH(7944, 7946, 7948, 62, 150, 92, 90,	"monkfish"), 
				SHARK(383, 385, 387, 76, 210, 100, 94, "shark"), 
				MANTA_RAY(389, 391, 393, 91, 169, 100, 100, "manta ray"),
				SEAWEED(401, 1781, 1781, 1, 1, 1, 1, "sea weed"),
				CURRY(2009, 2011, 2013, 60, 280, 74, 74, "curry");

		int rawItem, cookedItem, burntItem, levelReq, xp, stopBurn, stopBurnGloves;
		String name;

		private CookingItems(int rawItem, int cookedItem, int burntItem, int levelReq, int xp, int stopBurn, int stopBurnGloves, String name) {
			this.rawItem = rawItem;
			this.cookedItem = cookedItem;
			this.burntItem = burntItem;
			this.levelReq = levelReq;
			this.xp = xp;
			this.stopBurn = stopBurn;
			this.name = name;
		}

		private int getRawItem() {
			return rawItem;
		}

		private int getCookedItem() {
			return cookedItem;
		}

		private int getBurntItem() {
			return burntItem;
		}

		private int getLevelReq() {
			return levelReq;
		}

		private int getXp() {
			return xp;
		}

		private int getStopBurn() {
			return stopBurn;
		}

		private int getStopBurnGloves() {
			return stopBurnGloves;
		}

		private String getName() {
			return name;
		}
	}

	public static CookingItems forId(int itemId) {
		for (CookingItems item : CookingItems.values()) {
			if (itemId == item.getRawItem()) {
				return item;
			}
		}
		return null;
	}
public static void makeBreadOptions(Player c, int item) {
		if (c.getItems().playerHasItem(1929) && c.getItems().playerHasItem(1933) && item == c.breadID) {
			c.getItems().deleteItem(1929, 1);
			c.getItems().deleteItem(1933, 1);
			c.getItems().addItem(1925, 1);
			c.getItems().addItem(1931, 1);
			c.getItems().addItem(item, 1);
			c.sendMessage("You mix the water and flour to make some " + ItemAssistant.getItemName(item) + ".");
		}
		c.getPA().removeAllWindows();
	}

	public static void pastryCreation(Player c, int itemID1, int itemID2, int giveItem, String message) {
		if (c.getItems().playerHasItem(itemID1) && c.getItems().playerHasItem(itemID2)) {
			c.getItems().deleteItem(itemID1, 1);
			c.getItems().deleteItem(itemID2, 1);
			c.getItems().addItem(giveItem, 1);
			if (message.equalsIgnoreCase("")) {
				c.sendMessage("You mix the two ingredients and get an " + ItemAssistant.getItemName(giveItem) + ".");
			} else {
				c.sendMessage(message);
			}
		}
	}

	public static void cookingAddon(Player c, int itemID1, int itemID2, int giveItem, int requiredLevel, int expGained) {
		if (c.playerLevel[7] >= requiredLevel) {
			if (c.getItems().playerHasItem(itemID1) && c.getItems().playerHasItem(itemID2)) {
				c.getItems().deleteItem(itemID1, 1);
				c.getItems().deleteItem(itemID2, 1);
				c.getItems().addItem(giveItem, 1);
				c.getPA().addSkillXP(expGained, 7);
				c.sendMessage("You create a " + ItemAssistant.getItemName(giveItem) + ".");
			}
		} else {
			c.sendMessage("You don't have the required level to make an " + ItemAssistant.getItemName(giveItem));
		}
	}
		public static void setCooking(Player c2, boolean isCooking) {
		c2.playerIsCooking = isCooking;
		c2.stopPlayerSkill = isCooking;
	}
		private static void viewCookInterface(Player c, int item) {
		c.getPA().showChatboxInterface(1743);
		c.getPA().sendFrame246(13716, view190 ? 190 : 170, item);
		c.getPA().sendFrame126(getLine(c) + "" + ItemAssistant.getItemName(item) + "", 13717);
	}
	public static boolean startCooking(Player c, int itemId, int objectId, int objectX, int objectY) {
		CookingItems item = forId(itemId);
		if (item != null) {
			if (c.getSkills().getLevel(Skill.COOKING) < item.getLevelReq()) {
				c.getPA().removeAllWindows();
				c.getDH().sendStatement("You need a Cooking level of " + item.getLevelReq() + " to cook this.");
				c.nextChat = 0;
				return false;
			}
			if (c.playerIsCooking) {
				c.getPA().removeAllWindows();
				return false;
			}
			if (!COOKING) {
				c.sendMessage("This skill is currently disabled.");
				return false;
			}
			// save the id of the item and object for the cooking interface.
			c.cookingItem = itemId;
			c.cookingObject = objectId;
			c.cookingObjectX = objectX;
			c.cookingObjectY = objectY;
			viewCookInterface(c, item.getRawItem());
			return true;
		}
		return false;
	}
	private static boolean getSuccess(Player c, int burnBonus, int levelReq, int stopBurn) {
		if (c.playerLevel[c.playerCooking] >= stopBurn) {
			return true;
		}
		double burn_chance = 55.0 - burnBonus;
		double cook_level = c.playerLevel[c.playerCooking];
		double lev_needed = levelReq;
		double burn_stop = stopBurn;
		double multi_a = burn_stop - lev_needed;
		double burn_dec = burn_chance / multi_a;
		double multi_b = cook_level - lev_needed;
		burn_chance -= multi_b * burn_dec;
		double randNum = cookingRandom.nextDouble() * 100.0;
		return burn_chance <= randNum;
	}
	public static void cookItem(final Player player, final int itemId, final int amount, final int objectId) {
		CycleEventHandler.getSingleton().stopEvents(player, "cookingEvent".hashCode());
		final CookingItems item = forId(itemId);
		if (item != null) {
			setCooking(player, true);
			//RandomEventHandler.addRandom(player);
			player.getPA().removeAllWindows();
			player.doAmount = amount;
			if (player.doAmount > player.getItems().getItemAmount(itemId)) {
				player.doAmount = player.getItems().getItemAmount(itemId);
			}
			if (objectId > 0) {
				player.startAnimation(objectId == 2732 ? 897 : 896);
			}
			player.turnPlayerTo(player.cookingObjectX, player.cookingObjectY);
			CycleEventHandler.getSingleton().addEvent("cookingEvent".hashCode(), player, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (!player.playerIsCooking) {
						setCooking(player, false);
						container.stop();
						return;
					}
					if (!player.getItems().playerHasItem(item.getRawItem(), 1)) {
						player.sendMessage("You have run out of " + item.getName() + " to cook.");
						setCooking(player, false);
						container.stop();
						return;
					}
					boolean burn;
					if (player.playerEquipment[player.playerHands] == 775) {
						burn = !getSuccess(player, 3, item.getLevelReq(), item.getStopBurnGloves());
					} else {
						burn = !getSuccess(player, 3, item.getLevelReq(), item.getStopBurn());
					}
					player.getItems().deleteItem(item.getRawItem(),
							player.getItems().getItemSlot(itemId), 1);
					if (!burn) {
						player.sendMessage("You successfully cook the " + item.getName().toLowerCase() + ".");
						//if (GameConstants.SOUND) {
							//player.getPA().sendSound(SoundList.COOK_ITEM, 100, 0);
						//}
						player.getPA().addSkillXP(item.getXp(), player.playerCooking);
						player.getItems().addItem(item.getCookedItem(), 1);
					} else {
						player.sendMessage(
								"Oops! You accidentally burnt the "
										+ item.getName().toLowerCase() + "!");
						player.getItems().addItem(item.getBurntItem(), 1);
					}
					player.doAmount--;
					if (player.disconnected) {
						container.stop();
						return;
					}
					if (objectId < 0) {
						container.stop();
						return;
					}
					if (player.playerIsCooking && !Misc.goodDistance(player.objectX, player.objectY, player.absX, player.absY, 2)) {
						container.stop();
						return;
					}
					if (player.doAmount > 0) {
						if (objectId > 0) {
							player.startAnimation(objectId == 2732 ? 897 : 896);
						}
					} else if (player.doAmount == 0) {
						setCooking(player, false);
						container.stop();
					}
				}

				@Override
				public void stop() {
					
				}
			}, 4);
		}
	}
}