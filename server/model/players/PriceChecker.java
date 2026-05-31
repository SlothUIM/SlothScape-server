package server.model.players;

import server.Config;
import server.util.Misc;
import server.model.players.packets.GeValuesCache;
import server.model.players.packets.Pair;

public class PriceChecker {

	// Simplified framework tracker: index corresponds directly to item slot ID
	private static final int[] SLOT_FRAMES = {
			62550, 62553, 62556, 62559, 62562, 62565, 62568, 62571,
			62574, 62577, 62580, 62583, 62586, 62589, 62592, 62595,
			62598, 62601, 62604, 62607, 62610, 62613, 62616, 62619,
			62622, 62625, 62628, 62631
	};

	public static int arraySlot(Player c, int[] array, int target) {
		int spare = -1;
		for (int x = 0; x < array.length; x++) {
			if (array[x] == target && c.getItems().isStackable(target)) {
				return x;
			} else if (spare == -1 && array[x] <= 0) {
				spare = x;
			}
		}
		return spare;
	}

	public static void clearConfig(Player player) {
		for (int x = 0; x < player.price.length; x++) {
			if (player.price[x] > 0) {
				withdrawItem(player, player.price[x], x, player.priceN[x]);
			}
		}
		player.getItems().updateInventory = true;
		player.isChecking = false;
		player.getItems().resetItems(5064);
	}

	public static void depositItem(Player c, int id, int amount) {
		if (!c.isChecking) return;

		for (int j = 0; j < Config.ITEM_TRADEABLE.length; j++) {
			if (id == Config.ITEM_TRADEABLE[j]) {
				c.sendMessage("This item is untradeable.");
				return;
			}
		}

		int playerAmount = c.getItems().getItemAmount(id);
		if (playerAmount < amount) {
			amount = playerAmount;
		}

		if (amount <= 0) return;

		boolean stackable = c.getItems().isStackable(id) || c.getItems().isNoted(id);
		int itemValue = loadvalues(id);

		if (stackable) {
			int slot = arraySlot(c, c.price, id);
			if (slot == -1) {
				c.sendMessage("The price-checker is currently full.");
				return;
			}

			if (!c.getItems().playerHasItem(id, amount)) return;

			c.getItems().deleteItem2(id, amount);

			if (c.price[slot] != id) {
				c.price[slot] = id;
				c.priceN[slot] = amount;
			} else {
				c.priceN[slot] += amount;
			}

			c.total += itemValue * amount;
		} else {
			int added = 0;
			for (int i = 0; i < amount; i++) {
				int slot = arraySlot(c, c.price, -1);
				if (slot == -1) {
					c.sendMessage("The price-checker is currently full.");
					break;
				}

				if (!c.getItems().playerHasItem(id, 1)) break;

				c.getItems().deleteItem2(id, 1);
				c.price[slot] = id;
				c.priceN[slot] = 1;
				c.total += itemValue;
				added++;
			}

			if (added == 0) {
				c.sendMessage("No items could be added to the price checker.");
				return;
			}
		}

		updateChecker(c);
	}

	public static void loadcache() {
		GeValuesCache.loadGePricesCache();
	}

	public static int loadvalues(int itemId) {
		Pair<Integer, Integer> values = GeValuesCache.getPrice(itemId); // Thread-safe retrieval method
		return (values != null) ? values.getFirst() : 0;
	}

	public static void itemOnInterface(Player c, int frame, int slot, int id, int amount) {
		if (c.getOutStream() == null) return;
		c.outStream.createFrameVarSizeWord(34);
		c.outStream.writeWord(frame);
		c.outStream.writeByte(slot);
		c.outStream.writeWord(id + 1);
		c.outStream.writeByte(255);
		c.outStream.writeDWord(amount);
		c.outStream.endFrameVarSizeWord();
	}

	public static void open(Player c) {
		openUpChecker(c);
	}

	public static void openUpChecker(Player c) {
		if (c.getOutStream() != null) {
			c.getItems().resetItems(5064);
			c.isChecking = true;
			c.total = 0;
			c.getPA().sendFrame126(Misc.insertCommas(Integer.toString(c.total)), 62548);
			c.getPA().sendFrame126("Click on items in your inventory to check their values", 62549);
			updateChecker(c);
			resetFrames(c);

			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(43933);
			c.getOutStream().writeWord(5063);
			c.flushOutStream();
		}
	}

	public static void resetFrames(Player c) {
		for (int x = 0; x < 20; x++) {
			if (c.price[x] <= 1) {
				setFrame(c, x, SLOT_FRAMES[x], c.price[x], c.priceN[x], false);
			}
		}
	}

	private static void setFrame(Player player, int slotId, int frameId, int itemId, int amount, boolean store) {
		if (!store) {
			player.getPA().sendFrame126("", frameId);
			player.getPA().sendFrame126("", frameId + 1);
			player.getPA().sendFrame126("", frameId + 2);
			return;
		}

		int itemValue = loadvalues(itemId);
		long totalAmount = (long) itemValue * amount; // Prevent internal bit overflows on stackables
		String totalStr = Misc.insertCommas(Long.toString(totalAmount));

		if (player.getItems().isStackable(itemId)) {
			player.getPA().sendFrame126(amount + " x", frameId);
			player.getPA().sendFrame126(Misc.insertCommas(Integer.toString(itemValue)) + " =", frameId + 1);

			if (totalAmount > Integer.MAX_VALUE)
				player.getPA().sendFrame126("Lots!", frameId + 2);
			else
				player.getPA().sendFrame126(totalStr, frameId + 2);
		} else {
			player.getPA().sendFrame126(Misc.insertCommas(Integer.toString(itemValue)), frameId);
			player.getPA().sendFrame126("", frameId + 1);
			player.getPA().sendFrame126("", frameId + 2);
		}
	}

	public static void updateChecker(Player c) {
		c.getItems().resetItems(5064);
		for (int x = 0; x < 28; x++) {
			if (c.priceN[x] <= 0) {
				itemOnInterface(c, 58246, x, -1, 0);
			} else {
				itemOnInterface(c, 58246, x, c.price[x], c.priceN[x]);
				c.getPA().sendFrame126("", 62549);
				setFrame(c, x, SLOT_FRAMES[x], c.price[x], c.priceN[x], true); // Direct O(1) array access mapping
			}
		}

		if (c.total > Integer.MAX_VALUE || c.total < 0) {
			c.getPA().sendFrame126("Lots!", 62548);
		} else {
			c.getPA().sendFrame126(Misc.insertCommas(Integer.toString(c.total)), 62548);
		}
	}

	public static void withdrawItem(Player c, int removeId, int slot, int amount) {
		if (!c.isChecking) return;
		if (c.price[slot] != removeId || amount <= 0) return;

		boolean stackable = c.getItems().isStackable(removeId) || c.getItems().isNoted(removeId);
		int itemValue = loadvalues(removeId);

		if (stackable) {
			if (amount > c.priceN[slot]) amount = c.priceN[slot];

			if (c.getItems().freeSlots() < 1 && !c.getItems().playerHasItem(removeId)) {
				c.sendMessage("Not enough inventory space.");
				return;
			}

			c.getItems().addItem(removeId, amount);
			c.priceN[slot] -= amount;
			if (c.priceN[slot] <= 0) {
				c.price[slot] = 0;
				c.priceN[slot] = 0;
				setFrame(c, slot, SLOT_FRAMES[slot], 0, 0, false);
			} else {
				setFrame(c, slot, SLOT_FRAMES[slot], c.price[slot], c.priceN[slot], true);
			}

			c.total -= itemValue * amount;
		} else {
			int removed = 0;

			for (int i = 0; i < amount; i++) {
				if (c.getItems().freeSlots() < 1) break;

				int foundSlot = -1;
				for (int s = 0; s < c.price.length; s++) {
					if (c.price[s] == removeId && c.priceN[s] == 1) {
						foundSlot = s;
						break;
					}
				}

				if (foundSlot == -1) break;

				c.getItems().addItem(removeId, 1);
				c.price[foundSlot] = 0;
				c.priceN[foundSlot] = 0;
				c.total -= itemValue;
				removed++;

				setFrame(c, foundSlot, SLOT_FRAMES[foundSlot], 0, 0, false);
			}

			if (removed == 0) {
				c.sendMessage("No items withdrawn.");
				return;
			}
		}

		updateChecker(c);
	}
}