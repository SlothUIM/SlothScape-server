package server.model.items.containers;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.Lists;

import lombok.Getter;
import server.Server;
import server.model.Entity;
import server.model.players.Client;
import server.model.players.Player;
import server.world.World;
import server.model.items.Item;
import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;



public abstract class AdditionalInventory {
	@Getter
	protected Player player;
	
	@Getter
	protected List<Item> items = Lists.newArrayList();
	
	public void handleDeath(Player player2, String entity){
		Entity killer = player2.getKiller();
		for (Iterator<Item> iterator=items.iterator(); iterator.hasNext();) {
			Item item = iterator.next();

			if (item == null) {
				continue;
			}
			if (item.getId() <= 0 || item.getAmount() <= 0) {
				continue;
			}
			if (entity.equals("PVP")) {
				if (killer != null && killer instanceof Player) {
					Player playerKiller = (Player) killer;
					//if (playerKiller.getMode().isItemScavengingPermitted(null, null)) {
						World.getWorld().getItemHandler().createGroundItem(playerKiller, item.getId(), player.getX(), player.getY(), player.getHeight(), item.getAmount(), player.killerId);
					//} else {
						//World.getWorld().getItemHandler().createUnownedGroundItem(item.getId(), player.getX(), player.getY(), player.getHeight(), item.getAmount());
					//}
				}
			} else {
				World.getWorld().getItemHandler().createGroundItem(player2, item.getId(), player2.getX(), player2.getY(),
				                                    player2.getHeight(), item.getAmount(), player2.getIndex());
			}
			iterator.remove();
		}
	}

	public int countItems(int id) {
		int count = 0;
		for (Item item : items) {
			if (item.getId() == id + 1) {
				count += item.getAmount();
			}
		}
		return count;
	}

	public void withdrawItems(){
		if (!configurationPermitted()) {
			player.sendMessage("You cannot do this right now.");
			return;
		}
		for (Iterator<Item> iterator = items.iterator(); iterator.hasNext();) {
			Item item = iterator.next();
			if (!player.getItems().addItem(item.getId(), item.getAmount())) {
				break;
			}
			iterator.remove();
		}
	}

	public boolean sackContainsItem(int id) {
		for (Item item : items) {
			if (item.getId() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean addItemToList(int id, int amount) {
		for (Item item : items) {
			if (item.getId() == id) {
				if (item.getAmount() + amount >= 61) {
					return false;
				}
				if (player.getItems().isStackable(id)) {
					item.incrementAmount(amount);
					return false;
				}
			}
		}
		items.add(new Item(id, amount));
		return true;
	}

	public boolean configurationPermitted() {
		if (player.inDuelArena() || player.inPcGame() || player.inPcBoat() || player.isDead || player.viewingLootBag || player.addingItemsToLootBag) {
			return false;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return false;
		}
		/*if (player.getTutorial().isActive()) {
			player.getTutorial().refresh();
			return false;
		}*/
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			player.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return false;
		}
		if (World.getWorld().getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
			player.sendMessage("You must decline the trade to start walking.");
			return false;
		}
		if (player.isStuck) {
			player.isStuck = false;
			player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return false;
		}
		return true;
	}

}
