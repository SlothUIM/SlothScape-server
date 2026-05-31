package server.model.npcs.bosses.cerberus;

import java.util.ArrayList;

import server.model.players.Client;
import server.world.World;
import server.model.items.Item;
import server.model.items.ItemAssistant;
import server.model.items.bank.BankItem;
import server.Server;

@SuppressWarnings("serial")
public class CerberusLostItems extends ArrayList<Item> {

	/**
	 * The player that has lost items
	 */
	private final Client player;

	/**
	 * Creates a new class for managing lost items by a single player
	 * 
	 * @param player the player who lost items
	 */
	public CerberusLostItems(final Client player) {
		this.player = player;
	}

	/**
	 * Stores the players items into a list and deletes their items
	 */
	public void store() {
		for (int i = 0; i < player.playerItems.length; i++) {
			if (player.playerItems[i] < 1) {
				continue;
			}
			add(new Item(player.playerItems[i] - 1, player.playerItemsN[i]));
		}
		for (int i = 0; i < player.playerEquipment.length; i++) {
			if (player.playerEquipment[i] < 1) {
				continue;
			}
			add(new Item(player.playerEquipment[i], player.playerEquipmentN[i]));
		}
		player.getItems().deleteEquipment();
		player.getItems().deleteAllItems();
	}

	public void retain() {
		int price = 500_000;
		if (!player.getItems().playerHasItem(995, price)) {
			player.talkingNpc = 5870;
			player.getDH().sendStatement2("You need at least 500,000GP to claim your items.");
			return;
		}
		for (Item item : this) {
			/*if (player.getMode().isUltimateIronman()) {
				if (!player.getItems().addItem(item.getId(), item.getAmount())) {
					player.sendMessage("<col=CC0000>1x " + ItemAssistant.getItemName(item.getId()) + " has been dropped on the ground.</col>");
					World.getWorld().getItemHandler().createGroundItem(player, item.getId(), player.getX(), player.getY(), player.getHeight(), item.getAmount());
				}
			*///} else {
				player.getItems().sendItemToAnyTabOrDrop(new BankItem(item.getId(), item.getAmount()), player.getX(), player.getY());
			//}
		}
		clear();
		player.getItems().deleteItem2(995, price);
		player.talkingNpc = 5870;
		//if (player.getMode().isUltimateIronman()) {
		//	player.getDH().sendStatement2("You have retained all of your lost items for 500,000GP.", "Your items are in your inventory.",
		//			"@red@If there was not enough space, they were dropped.");
		//} else {
			player.getDH().sendStatement2("You have retained all of your lost items for 500,000GP.", "Your items are in your bank.");
		//}
		player.nextChat = -1;
	}

}
