package server.model.items.containers;

import java.util.stream.IntStream;

import server.model.players.Player;
import server.model.players.PlayerSave;
import server.model.items.Item;
import server.model.items.ItemAssistant;

public class GemBag extends AdditionalInventory {
	/**
	 * Checks whether a player is allowed to configure the gem bag or not
	 * @return
	 */

	/**
	 * The gem bag id and boolean to set if we want to check if a player has one
	 */
	private static final int GEM_BAG_ID = 12020;

	/**
	 * The gem bag class
	 * @param player
	 */
	public GemBag(Player player) {
		this.player = player;
	}

	/**
	 * Handles players death with a gem bag in their inventory
	 * @param o
	 * @param entity
	 */
	public void onDeath(Player o, String entity) {
		if (o == null) {
			return;
		}
		handleDeath(o,entity);
		PlayerSave.saveGame(player);
	}
	
	/**
	 * Attempts to withdraw all gems from the gem bag
	 */
	public void withdrawAll() {
		withdrawItems();
	}

	/**
	 * The id's of the gems you are allowed to store in the gem bag
	 */
	private int[] uncutGems = new int[] { 1617, 1619, 1621, 1623, 1625, 1627, 1629, 1631, 6571, 19496 };
	
	/**
	 * Attempts to fill the bag with the gems a player has in their inventory
	 */
	public void fillBag() {

		for (int uncutGem : uncutGems) {
			if (player.getItems().playerHasItem(uncutGem, 1)) {
				addItemToGemBag(uncutGem, player.getItems().getItemAmount(uncutGem));
			}
		}
	}

	/**
	 * Attempts  to add the gems chosen to the gem bag
	 * @param id
	 * @param amount
	 */
	public void addItemToGemBag(int id, int amount) {
		boolean isUncut = IntStream.of(uncutGems).anyMatch(identification -> identification == id);
		boolean haveUncut = IntStream.of(uncutGems).anyMatch(identification -> player.getItems().playerHasItem(identification));
		if (!haveUncut) {
			player.sendMessage("You have nothing suitable for storing in the bag.");
			return;
		}
		if (!isUncut) {
			player.sendMessage("You can only store uncut gems in the gem bag.");
			return;
		}
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		if (player.getItems().isStackable(id)) {
			return;
		}
		if (amount >= 28) {
			amount = player.getItems().itemAmount(id);
		}
		if (id == GEM_BAG_ID) {
			player.sendMessage("Don't be silly.");
			return;
		}
		if (items.size() >= 61 && !(sackContainsItem(id) && player.getItems().isStackable(id))) {
			return;
		}
		if (id <= 0 || amount <= 0) {
			return;
		}
		if (countItems(id) + amount >= 61 || countItems(id) + amount <= 0) {
			player.sendMessage("You cannot store this many of this gem.");
			return;
		}
		player.sendMessage("Filled the gem bag with x" + amount + " " + ItemAssistant.getItemName(id));
		for (int amt = 0; amt < amount; amount--) {
			player.getItems().deleteItem(id, 1);
			addItemToList(id + 1, 1);
		}
	}
	
	/**
	 * Checks the amount and of what gem you have stored in the sack
	 */
	public void check() {
		int frame = 8149;
		int totalAmount = 0;
		if (totalAmount == 0) {
			player.sendMessage("Your gem bag is empty.");
		}
		for (int i = 0; i < 14; i++) {
			int id = 0;
			int amt = 0;

			if (i < items.size()) {
				Item item = items.get(i);
				if (item != null) {
					id = item.getId();
					amt = item.getAmount();
				}
				totalAmount += amt;
				player.sendMessage("@blu@"+ItemAssistant.getItemName(id)+": @bla@[@red@"+amt+"@bla@]");
			}
		}
	}

}
