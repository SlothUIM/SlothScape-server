package server.model.shops;

import server.Config;
import server.Server;
import server.model.items.Item;
import server.model.items.ItemAssistant;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.skills.agility.MarkOfGrace;
import server.world.ShopHandler;
import server.world.World;

public class ShopAssistant {

	private Player c;

	public ShopAssistant(Player player) {
		this.c = player;
	}

	public boolean shopSellsItem(int itemID) {
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Shops
	 **/

	public void openShop(int ShopID) {
		c.getItems().resetItems(3823);
		resetShop(ShopID);
		c.isShopping = true;
		c.myShopId = ShopID;
		c.getPA().sendFrame248(3824, 3822);
		c.getPA().sendFrame126(ShopHandler.ShopName[ShopID], 3901);
	}

	public void updatePlayerShop() {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].isShopping
						&& PlayerHandler.players[i].myShopId == c.myShopId
						&& i != c.playerId) {
					PlayerHandler.players[i].updateShop = true;
				}
			}
		}
	}
	public void resetShop(int ShopID) {
		//synchronized (c) {
			int TotalItems = 0;
			for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
				if (ShopHandler.ShopItems[ShopID][i] > 0) {
					TotalItems++;
				}
			}
			if (TotalItems > ShopHandler.MaxShopItems) {
				TotalItems = ShopHandler.MaxShopItems;
			}
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(3900);
			c.getOutStream().writeWord(TotalItems);
			int TotalCount = 0;
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[ShopID][i] > 0
						|| i <= ShopHandler.ShopItemsStandard[ShopID]) {
					if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord_v2(
								ShopHandler.ShopItemsN[ShopID][i]);
					} else {
						c.getOutStream().writeByte(
								ShopHandler.ShopItemsN[ShopID][i]);
					}
					if (ShopHandler.ShopItems[ShopID][i] > Config.ITEM_LIMIT
							|| ShopHandler.ShopItems[ShopID][i] < 0) {
						ShopHandler.ShopItems[ShopID][i] = Config.ITEM_LIMIT;
					}
					c.getOutStream().writeWordBigEndianA(
							ShopHandler.ShopItems[ShopID][i]);
					TotalCount++;
				}
				if (TotalCount > TotalItems) {
					break;
				}
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
	}
	public static int getItemShopValue(int itemId) {
        return getBaseItemValue(itemId);
	}

	/**
	 * buy item from shop (Shop Price)
	 **/

	public void buyFromShopPrice(int removeId, int removeSlot) {
		int shopId = c.myShopId;
		if (shopId == 17 || shopId == 18) {
			c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " points.");
			return;
		}if (shopId == 192) {
			c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " Marks of grace.");
			return;
		}
		if (shopId == 15) {
			c.sendMessage("This item currently costs " + c.getItems().getUntradePrice(removeId) + " coins.");
			return;
		}
		int finalPrice = getDynamicBuyPrice(removeId, removeSlot);
		String shopAdd = "";

		if (finalPrice >= 1000000) {
			shopAdd = " (" + (finalPrice / 1000000) + " million)";
		} else if (finalPrice >= 1000) {
			shopAdd = " (" + (finalPrice / 1000) + "K)";
		}

		c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + finalPrice + " coins" + shopAdd);
	}
	public int getDynamicBuyPrice(int itemId, int shopSlot) {
		int baseValue = getBaseItemValue(itemId);
		int shopId = c.myShopId;
		if (shopSlot < 0 || shopSlot >= ShopHandler.ShopItemsN[shopId].length) {
			return baseValue;
		}
		int currentStock = ShopHandler.ShopItemsN[shopId][shopSlot];
		int defaultStock = ShopHandler.ShopItemsSN[shopId][shopSlot];
		int stockDifference = currentStock - defaultStock;
		double multiplier = 1.0 - (stockDifference * 0.02);
		if (multiplier < 0.10) {
			multiplier = 0.10;
		}
		return (int) Math.max(1, baseValue * multiplier);
	}
	public int getSpecialItemValue(int id) {
		if(MarkOfGrace.isGracefulPiece(id)) {
			return MarkOfGrace.getGracefulCost(id);
		}
        return switch (id) {
            case 6889, 6914, 11694 -> 200;
            case 4151, 8848, 10551, 10499 -> 20;
            case 6916, 6918, 6920, 6922, 6924, 10548 -> 50;
            case 11663, 11664, 11665, 8842 -> 30;
            case 8839, 8840 -> 75;
            case 8845 -> 5;
            case 8846, 4712, 4714 -> 10;
            case 8847, 7462 -> 15;
            case 8849, 8850 -> 25;
            case 11696, 11698, 11700 -> 100;
            case 11235 -> 40;
            default -> 0;
        };
    }
	/**
	 * Sell item to shop (Shop Price)
	 **/
	public void sellToShopPrice(int removeId, int removeSlot) {
		for (int i : Config.ITEM_SELLABLE) {
			if (i == removeId) {
				c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
				return;
			}
		}

		boolean IsIn = false;
		if (ShopHandler.ShopSModifier[c.myShopId] > 1) { // Specialty Shop
			for (int j = 0; j <= ShopHandler.ShopItemsStandard[c.myShopId]; j++) {
				if (removeId == (ShopHandler.ShopItems[c.myShopId][j] - 1)) {
					IsIn = true;
					break;
				}
			}
		} else {
			IsIn = true; // General Store
		}

		if (!IsIn) {
			c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + " to this store.");
		} else {
			int shopSlot = getShopSlot(c.myShopId, removeId);
			int shopValue = getDynamicSellPrice(removeId, c.myShopId, shopSlot);

			String shopAdd = "";
			if (shopValue >= 1000000) {
				shopAdd = " (" + (shopValue / 1000000) + " million)";
			} else if (shopValue >= 1000) {
				shopAdd = " (" + (shopValue / 1000) + "K)";
			}
			c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + shopValue + " coins" + shopAdd);
		}
	}

	public boolean sellItem(int itemID, int fromSlot, int amount) {
		if(itemID == 995){
			c.sendMessage("You can't sell coins.");
			return false;
		}
		if (c.myShopId == 14) return false;

		for (int i : Config.ITEM_SELLABLE) {
			if (i == itemID) {
				c.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + ".");
				return false;
			}
		}
		if (c.playerRights == 2 && !Config.ADMIN_CAN_SELL_ITEMS) {
			c.sendMessage("Selling items as an admin has been disabled.");
			return false;
		}

		if (amount > 0 && itemID == (c.playerItems[fromSlot] - 1)) {
			if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
				boolean IsIn = false;
				for (int i = 0; i <= ShopHandler.ShopItemsStandard[c.myShopId]; i++) {
					if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
						IsIn = true;
						break;
					}
				}
				if (!IsIn) {
					c.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + " to this store.");
					return false;
				}
			}

			// Cap amount to what the player actually has
			if (amount > c.playerItemsN[fromSlot] && (Item.itemIsNote[(c.playerItems[fromSlot] - 1)] || Item.itemStackable[(c.playerItems[fromSlot] - 1)])) {
				amount = c.playerItemsN[fromSlot];
			} else if (amount > c.getItems().getItemAmount(itemID) && !Item.itemIsNote[(c.playerItems[fromSlot] - 1)] && !Item.itemStackable[(c.playerItems[fromSlot] - 1)]) {
				amount = c.getItems().getItemAmount(itemID);
			}

			// OSRS BULK PRICING CALCULATOR
			int totalValue = 0;
			int shopSlot = getShopSlot(c.myShopId, itemID);
			int simStock = (shopSlot != -1) ? ShopHandler.ShopItemsN[c.myShopId][shopSlot] : 0;
			int defStock = (shopSlot != -1) ? ShopHandler.ShopItemsSN[c.myShopId][shopSlot] : 0;
			int baseVal = getBaseItemValue(itemID);
			double baseMultiplier = (ShopHandler.ShopSModifier[c.myShopId] > 1) ? 0.60 : 0.40;

			for (int i = 0; i < amount; i++) {
				int diff = Math.max(0, simStock - defStock);
				double mult = baseMultiplier - (diff * 0.02);
				if (mult < 0.10) mult = 0.10;
				totalValue += (int) Math.max(1, baseVal * mult);
				simStock++; // Stock increases per item sold, dropping the price for the next loop!
			}

			// Added a check: If the item is unstackable, selling it will clear a slot for the coins!
			if (c.getItems().freeSlots() > 0 || c.getItems().playerHasItem(995) || !Item.itemStackable[itemID]) {

				// Safely delete items FIRST so the slot frees up
				if (Item.itemStackable[itemID] || Item.itemIsNote[itemID]) {
					c.getItems().deleteItem(itemID, fromSlot, amount);
				} else {
					for (int i = 0; i < amount; i++) {
						c.getItems().deleteItem(itemID, c.getItems().getItemSlot(itemID), 1);
					}
				}

				// NOW add the coins
				c.getItems().addItem(995, totalValue);
				addShopItem(itemID, amount);

				c.getItems().resetItems(3823);
				resetShop(c.myShopId);
				updatePlayerShop();
				return true;
			} else {
				c.sendMessage("You don't have enough space in your inventory.");
				return false;
			}
		}
		return true;
	}

	public boolean addShopItem(int itemID, int amount) {
		boolean Added = false;
		if (amount <= 0) {
			return false;
		}
		if (Item.itemIsNote[itemID]) {
			itemID = c.getItems().getUnnotedItem(itemID);
		}
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if ((ShopHandler.ShopItems[c.myShopId][i] - 1) == itemID) {
				ShopHandler.ShopItemsN[c.myShopId][i] += amount;
				Added = true;
			}
		}
		if (!Added) {
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[c.myShopId][i] == 0) {
					ShopHandler.ShopItems[c.myShopId][i] = (itemID + 1);
					ShopHandler.ShopItemsN[c.myShopId][i] = amount;
					ShopHandler.ShopItemsDelay[c.myShopId][i] = 0;
					break;
				}
			}
		}
		return true;
	}

	public boolean buyItem(int itemID, int fromSlot, int amount) {
		if (!shopSellsItem(itemID) && c.myShopId != 50 && c.myShopId != 60) return false;

		if (c.myShopId == 183) {
			if(itemID == 36 && amount > 0) c.getAD().completeAchievement("KandarinEasy", "Buy a candle from the candle maker in Catherby", 2);
		}

		if (c.myShopId == 17 || c.myShopId == 18 || c.myShopId == 50 || c.myShopId == 60 || c.myShopId == 192) {
			handleOtherShop(itemID);
			return false;
		}

		if (amount > 0) {
			if (amount > ShopHandler.ShopItemsN[c.myShopId][fromSlot]) {
				amount = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
			}

			int currencyId = 995; // Default: Coins
			if (c.myShopId == 52 || c.myShopId == 53 || c.myShopId == 54) {
				currencyId = 6529; // Tokkul
			}

			int availableCurrency = c.getItems().getItemAmount(currencyId);

			// OSRS BULK BUYING CALCULATOR
			int totalCost = 0;
			int affordableAmount = 0;
			int simStock = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
			int defStock = ShopHandler.ShopItemsSN[c.myShopId][fromSlot];
			int baseVal = getBaseItemValue(itemID);

			for (int i = 0; i < amount; i++) {
				int diff = simStock - defStock;
				double mult = 1.0 - (diff * 0.02);
				if (mult < 0.10) mult = 0.10;
				int price = (int) Math.max(1, baseVal * mult);

				if (totalCost + price > availableCurrency) {
					break; // Can't afford any more!
				}
				totalCost += price;
				simStock--; // Stock drops per item bought, raising the price for the next loop!
				affordableAmount++;
			}

			if (affordableAmount == 0) {
				c.sendMessage("You don't have enough " + c.getItems().getItemName(currencyId).toLowerCase() + ".");
				return false;
			}

			// Inventory Space Check
			// Inventory Space Check
			if (!Item.itemStackable[itemID]) { // Removed the <= 1 check!
				int freeSlots = c.getItems().freeSlots();
				if (affordableAmount > freeSlots) {
					affordableAmount = freeSlots;
					// Recalculate cost for the reduced inventory space
					totalCost = 0;
					simStock = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
					for (int i = 0; i < affordableAmount; i++) {
						int diff = simStock - defStock;
						double mult = 1.0 - (diff * 0.02);
						if (mult < 0.10) mult = 0.10;
						totalCost += (int) Math.max(1, baseVal * mult);
						simStock--;
					}
					c.sendMessage("You only have enough inventory space to buy " + affordableAmount + ".");
				}
			} else if (c.getItems().freeSlots() == 0 && Item.itemStackable[itemID] && !c.getItems().playerHasItem(itemID)) {
				// Changed <= 1 to == 0. If they have 1 slot, they CAN buy a new stack of arrows!
				c.sendMessage("You don't have enough space in your inventory.");
				return false;
			}

			// Process Transaction
			c.getItems().deleteItem(currencyId, c.getItems().getItemSlot(currencyId), totalCost);
			c.getItems().addItem(itemID, affordableAmount);

			ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= affordableAmount;
			ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;

			// Remove item from shop entirely if player sold it to them and bought it all back
			if (ShopHandler.ShopItemsN[c.myShopId][fromSlot] <= 0 && (fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
				ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
			}

			c.getItems().resetItems(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return false;
	}
	public static int getBaseItemValue(int itemId) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (World.getWorld().getItemHandler().ItemList[i] != null) {
				if (World.getWorld().getItemHandler().ItemList[i].itemId == itemId) {
					return (int) World.getWorld().getItemHandler().ItemList[i].ShopValue;
				}
			}
		}
		return 0;
	}
	public int getShopSlot(int shopId, int itemId) {
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if ((ShopHandler.ShopItems[shopId][i] - 1) == itemId) {
				return i;
			}
		}
		return -1;
	}
	public int getDynamicSellPrice(int itemId, int shopId, int shopSlot) {
		int baseValue = getBaseItemValue(itemId);
		double baseMultiplier = (ShopHandler.ShopSModifier[shopId] > 1) ? 0.60 : 0.40;

		int currentStock = (shopSlot != -1) ? ShopHandler.ShopItemsN[shopId][shopSlot] : 0;
		int defaultStock = (shopSlot != -1) ? ShopHandler.ShopItemsSN[shopId][shopSlot] : 0;

		int stockDifference = Math.max(0, currentStock - defaultStock);
		double multiplier = baseMultiplier - (stockDifference * 0.02);

		if (multiplier < 0.10) multiplier = 0.10; // 10% Floor

		return (int) Math.max(1, baseValue * multiplier);
	}
	public void handleOtherShop(int itemID) {
		if (c.myShopId == 17) {
			if (c.magePoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.magePoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough points to buy this item.");
			}
		} else if (c.myShopId == 18) {
			if (c.pcPoints >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.pcPoints -= getSpecialItemValue(itemID);
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough points to buy this item.");
			}
		} else if (c.myShopId == 192) {
			if (c.getItems().getItemAmount(11849) >= getSpecialItemValue(itemID)) {
				if (c.getItems().freeSlots() > 0) {
					c.getItems().deleteItem(11849, c.getItems().getItemSlot(11849), MarkOfGrace.getGracefulCost(itemID));
					c.getItems().addItem(itemID, 1);
					c.getItems().resetItems(3823);
				}
			} else {
				c.sendMessage("You do not have enough Marks of grace to buy this item.");
			}
		}
	}
}
