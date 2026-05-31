package server.model.items.containers;

import java.util.Optional;

import server.model.items.Item;
import server.model.items.ItemAssistant;
import server.model.players.Player;
import server.model.players.PlayerSave;

public class RunePouch extends AdditionalInventory {

	public static final int RUNE_POUCH_ID = 12791;
	private static final boolean CHECK_FOR_POUCH = true;

	private static final int START_RUNE_INVENTORY_INTERFACE = 41710;
	private static final int END_RUNE_INVENTORY_INTERFACE = 29910;
	private static final int START_BAG_INVENTORY_INTERFACE = 41711;
	private static final int END_BAG_INVENTORY_INTERFACE = 29907;

	private int enterAmountItem = -1;
	private int enterAmountInterface = -1;

	public RunePouch(Player player) {
		this.player = player;
	}

	public void onDeath(Player o, String entity) {
		if (o == null) return;
		handleDeath(player, entity);
		sendPouchRuneInventory();
		PlayerSave.saveGame(player);
	}

	public boolean handleButton(int buttonId) {
		if (buttonId == 29877) {
			closePouchInterface();
			return true;
		}
		return false;
	}

	public void openRunePouch() {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH) return;
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		onClose();
		sendUpdates();
		player.getPA().showInterface(41700);
		player.viewingRunePouch = true;
	}

	public boolean addRunesFromInventory(int id, int amount) {
		if (id <= 0 || amount <= 0 || id == RUNE_POUCH_ID) return false;
		if (!configurationPermitted()) return false;
		if (!(id >= 554 && id <= 566) && id != 9075) return false;
		if (items.size() >= 3 && getItemInBag(id).isEmpty()) return false;
		int invCount = player.getItems().getItemCount(id, false);
		amount = Math.min(amount, invCount);
		final int fin = amount;
		player.getItems().deleteItem(id, amount);
		getItemInBag(id).ifPresentOrElse(
			item -> item.incrementAmount(fin),
			() -> items.add(new Item(id, fin))
		);
		sendUpdates();
		return true;
	}

	/**
	 * Checks if the pouch contains at least the given amount of the rune.
	 */
	public boolean hasRune(int runeId, int amount) {
		return getCountInBag(runeId) >= amount;
	}


	/**
	 * Attempts to consume a given amount of rune from the pouch.
	 * Returns true if successful, false if not enough.
	 */
	public boolean consumeRune(int runeId, int amount) {
		if (!hasRune(runeId, amount))
			return false;
		removeRunesFromBag(runeId, amount);
		return true;
	}
	public boolean hasRuneInPouch(int... runeIds) {
		for (int i = 0; i < 3; i++) {
			if (i >= items.size()) continue;
			int storedId = items.get(i).getId();
			for (int runeId : runeIds) {
				if (storedId == runeId) return true;
			}
		}
		return false;
	}
	public void updatePouchRuneCount(int id1, int amt1, int id2, int amt2, int id3, int amt3) {
		int[] ids = { id1, id2, id3 };
		int[] amts = { amt1, amt2, amt3 };
		for (int i = 0; i < 3; i++) {
			if (i >= items.size()) continue;
			Item item = items.get(i);
			for (int j = 0; j < 3; j++) {
				if (item.getId() == ids[j]) {
					item.incrementAmount(-amts[j]);
					if (item.getAmount() <= 0) {
						items.remove(i);
					}
					break;
				}
			}
		}
		sendUpdates();
	}
	public void sendLegacyRuneTypes() {
		int type1 = 0, type2 = 0, type3 = 0;
		int amt1 = 0, amt2 = 0, amt3 = 0;

		if (items.size() > 0) {
			type1 = items.get(0).getId();
			amt1 = items.get(0).getAmount();
		}
		if (items.size() > 1) {
			type2 = items.get(1).getId();
			amt2 = items.get(1).getAmount();
		}
		if (items.size() > 2) {
			type3 = items.get(2).getId();
			amt3 = items.get(2).getAmount();
		}
			player.getPA().sendRuneTypes(type1, type2, type3, amt1, amt2, amt3);
	}

	public void syncToLegacyPouchFields() {
		player.PouchRune1 = 0;
		player.PouchRune2 = 0;
		player.PouchRune3 = 0;
		player.PouchRune1Amt = 0;
		player.PouchRune2Amt = 0;
		player.PouchRune3Amt = 0;

		for (int i = 0; i < items.size(); i++) {
			Item item = items.get(i);
			switch (i) {
				case 0 -> {
					player.PouchRune1 = item.getId();
					player.PouchRune1Amt = item.getAmount();
				}
				case 1 -> {
					player.PouchRune2 = item.getId();
					player.PouchRune2Amt = item.getAmount();
				}
				case 2 -> {
					player.PouchRune3 = item.getId();
					player.PouchRune3Amt = item.getAmount();
				}
			}
		}
	}

	/**
	 * Convenience method to consume multiple runes at once.
	 * Returns true if all were consumed successfully.
	 */
	public boolean consumeRunes(int[] runeIds, int[] amounts) {
		if (!player.getItems().playerHasItem(RUNE_POUCH_ID) && CHECK_FOR_POUCH)
			return false;
		for (int i = 0; i < runeIds.length; i++) {
			if (runeIds[i] <= 0 || amounts[i] <= 0)
				continue;
			if (!hasRune(runeIds[i], amounts[i]))
				return false;
		}
		for (int i = 0; i < runeIds.length; i++) {
			if (runeIds[i] <= 0 || amounts[i] <= 0)
				continue;
			consumeRune(runeIds[i], amounts[i]);
		}
		return true;
	}

	public int getCountInBag(int itemId) {
		return getItemInBag(itemId).map(Item::getAmount).orElse(0);
	}

	public int getTotalRuneCount(int itemId) {
		return getCountInBag(itemId) + player.getItems().getItemCount(itemId, false);
	}

	private Optional<Item> getItemInBag(int id) {
		return items.stream().filter(i -> i.getId() == id).findFirst();
	}

	private boolean removeRunesFromBag(int id, int amount) {
		Optional<Item> item = getItemInBag(id);
		if (item.isPresent()) {
			item.get().incrementAmount(-amount);
			if (item.get().getAmount() <= 0) items.remove(item.get());
			sendPouchRuneInventory();
			return true;
		}
		return false;
	}

	public void setEnterAmountVariables(int itemId, int interfaceId) {
		this.enterAmountItem = itemId;
		this.enterAmountInterface = interfaceId;
	}

	public boolean finishEnterAmount(int amount) {
		return handleClickItem(enterAmountItem, amount, enterAmountInterface);
	}

	public boolean handleClickItem(int id, int amount, int interfaceId) {
		if (!player.viewingRunePouch || !configurationPermitted()) return false;
		if (interfaceId >= START_RUNE_INVENTORY_INTERFACE && interfaceId <= END_RUNE_INVENTORY_INTERFACE) {
			return withdrawRunesFromBag(id, amount);
		} else if (interfaceId >= START_BAG_INVENTORY_INTERFACE && interfaceId <= END_BAG_INVENTORY_INTERFACE) {
			return addRunesFromInventory(id, amount);
		}
		return false;
	}
	public void syncFromLegacyPouch(int id1, int amt1, int id2, int amt2, int id3, int amt3) {
		items.clear();
			if (id1 > 0) items.add(new Item(id1, amt1));
			if (id2 > 0) items.add(new Item(id2, amt2));
			if (id3 > 0) items.add(new Item(id3, amt3));
			sendUpdates(); // optional if you want UI to match this
	}

	private boolean withdrawRunesFromBag(int id, int amount) {
		if (!player.getItems().playerHasItem(id) && player.getItems().freeSlots() <= 0) return false;
		int inBag = getCountInBag(id);
		amount = Math.min(amount, inBag);
		Optional<Item> item = getItemInBag(id);
		if (item.isPresent()) {
			item.get().incrementAmount(-amount);
			if (item.get().getAmount() <= 0) items.remove(item.get());
			player.getItems().addItem(id, amount);
			sendUpdates();
			return true;
		}
		return false;
	}
	public boolean removeRune(int runeId) {
		Optional<Item> item = getItemInBag(runeId);
		if (item.isEmpty())
			return false;

		player.getItems().addItem(runeId, item.get().getAmount());
		items.remove(item.get());
		sendUpdates(); // redraws both inventory and pouch
		return true;
	}


	public boolean addRunesFromInventory(int itemId, int inventorySlot, int amount) {
		if (!(itemId >= 554 && itemId <= 566) && itemId != 9075)
			return false;

		// Find if this rune already exists in the pouch
		Optional<Item> existing = getItemInBag(itemId);
		if (items.size() >= 3 && existing.isEmpty())
			return false;

		int available = player.getItems().getItemCount(itemId, false);
		int toAdd = Math.min(amount, available);

		if (toAdd <= 0)
			return false;

		if (existing.isPresent()) {
			existing.get().incrementAmount(toAdd);
		} else {
			items.add(new Item(itemId, toAdd));
		}

		player.getItems().deleteItem(itemId, inventorySlot, toAdd);

		player.sendMessage("You add " + toAdd + " " + ItemAssistant.getItemName(itemId) + " runes to your pouch.");

		syncToLegacyPouchFields();
		sendUpdates();
		sendLegacyRuneTypes();

		return true;
	}

	private void sendPouchRuneInventory() {
		StringBuilder sendSpells = new StringBuilder("#");
		for (int i = 0; i < 3; i++) {
			int id = -1, amt = 0;
			if (i < items.size()) {
				Item item = items.get(i);
				id = item.getId();
				amt = item.getAmount();
			}
			player.getPA().sendFrame34a(41710,id, i, amt);
			sendSpells.append((id == -1 ? 0 : id)).append(":").append(amt);
			if (i < 2) sendSpells.append("-");
		}
		sendSpells.append("$");
		player.getPA().sendFrame126(sendSpells.toString(), 49999); // or whatever frame you use
	}

	private void sendRunePouchInventory() {
		for (int i = 0; i < 28; i++) {
			int itemId = player.playerItems[i];
			int itemAmount = player.playerItemsN[i];
			if (itemId <= 0) {
				player.getPA().sendFrame34a(41711, -1, i, 0);
			} else {
				player.getPA().sendFrame34a(41711, itemId - 1, i, itemAmount);
			}
		}
	}


	private void sendSidebarInventory() {
		player.getItems().resetItems(3214);
		player.getPA().requestUpdates();
	}

	private void sendUpdates() {
			sendSidebarInventory();
			sendRunePouchInventory();
			sendPouchRuneInventory();
	}

	private void closePouchInterface() {
		onClose();
	}

	private void onClose() {
		player.viewingRunePouch = false;
		player.getPA().closeAllWindows();
	}

	public void onLogin() {
		sendPouchRuneInventory();
	}
}
