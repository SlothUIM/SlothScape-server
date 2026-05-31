package server.model.players.packets;

import server.model.players.Player;

import java.util.Objects;

import server.Config;
import server.model.items.Item;
import server.model.items.containers.FancyBox;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.CollectionLogRegistry;
import server.model.items.containers.FancyDressBox;
import server.model.minigames.NMZRewards;
import server.model.multiplayer_session.MultiplayerSession;
import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.multiplayer_session.trade.TradeSession;
import server.model.players.PacketType;
import server.model.players.PriceChecker;
import server.world.World;

/**
 * Bank 5 Items
 **/
public class Bank5 implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readSignedWordBigEndianA();
		int removeId = c.getInStream().readSignedWordBigEndianA();
		int removeSlot = c.getInStream().readSignedWordBigEndian();

				System.out.println("Bank5 "+interfaceId+"]: "+removeId);
				boolean isFancyBoxWithdraw = (interfaceId >= 19521 && interfaceId <= 19542);
				boolean isArmourCaseWithdraw = (interfaceId >= 29239 && interfaceId <= 29840);

				if (isFancyBoxWithdraw && c.isInInterface == 19543) {
				    c.getFancyDressBox().handleWithdraw(c, removeId, interfaceId, 5);
				    return;
				}

				if (isArmourCaseWithdraw && c.isInInterface == 29236) {
				   // c.getArmourCase().handleWithdraw(c, removeId, interfaceId, c.getItems().itemAmount(removeId));
				    return;
				}
				if (interfaceId == 25546 || interfaceId == 25703 || interfaceId == 25705 || interfaceId == 25707 || interfaceId == 25709) {
		            NMZRewards.buyItem(c, interfaceId, removeId, 1);
		            return;
		        }
		switch (interfaceId) {

		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 1);
			break;

		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 1);
			break;
		case -23826: // interfaceId
			if (c.getRunePouch().removeRune(removeId)) {
				c.getRunePouch().syncToLegacyPouchFields(); // optional, keeps PouchRune1/2/3 up to date
				c.getRunePouch().sendLegacyRuneTypes();     // sends your packet (opcode 172)
				c.openRunePouch(); // refresh UI
			}
			break;

		case -23825:
		//if(c.addingToRP){
			//int[] RuneIDS = {554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566};
		for(int i = 0; i < 20; i++){
			if(removeId == c.RuneIDS[i]){
				c.getRunePouch().addRunesFromInventory(removeId, removeSlot, 5);

				//c.openRunePouch();
			break;
			}
			}
		//}
		break;
		case 5064:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the bank whilst trading.");
				return;
			}
			DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				if(removeId == 11941) {
					c.addingItemsToLootBag = true;
					for (int slot = 0; slot < c.playerLootItems.length; slot++) {
						if (c.playerLootItems[slot] > 0 && c.playerLootItemsN[slot] > 0) {
							System.out.println("Depositing lootbag items");
							c.getItems().bankLootItem(c.playerLootItems[slot] - 1, c.playerLootItemsN[slot]);
						}
					}

					c.addingItemsToLootBag = false;
					return;
				} 
				c.getItems().addToBank(removeId, 5, removeSlot, true);
			}
			if (c.isChecking) {
				PriceChecker.depositItem(c, removeId, 5);
				return;
			}
			if (c.inSafeBox) {
				/*if (!c.pkDistrict && removeId != 13307) {
					c.sendMessage("You cannot do this right now.");
					return;
				}
				c.getSafeBox().deposit(removeId, 5);*/
			}

			if (c.isInInterface == 19543) {
			    c.getFancyDressBox().deposit(c, removeId, 5);
			    return;
			}
			break;
		case 7423:
			c.getItems().addToBank(removeId, 5, removeSlot, true);
			c.getItems().resetItems(7423);
			break;
			case -21826:
				for (int i : Config.DESTROYABLE_ITEMS) {
					if (i == removeId) {
					c.sendMessage("You can't put untradable items in your looting bag.");
					return;
					} else
						c.getItems().addtoLootbagFromDeposit(removeId, removeSlot, 5);
				}
			break;
		case -21603:
			if (c.isChecking) {
				PriceChecker.withdrawItem(c, removeId, removeSlot, 5);
				return;
			}
			//PriceChecker.withdrawItem(c, removeId, removeSlot, 5);
			break;
		case -29722:
			int totalAmount = 0;
			int itemIdToCheck = removeId;

			for (CollectionLogData log : c.getCollectionLog().registry.getAllLogs()) {
			    int[][] entries = log.getEntries();
			    for (int[] entry : entries) {
			        if (entry[0] == itemIdToCheck) {
			            totalAmount += entry[2]; // add the collected amount
			        }
			    }
			}

			if (totalAmount > 0) {
			    c.sendMessage("You have received " + totalAmount + " " + c.getItems().getItemName(itemIdToCheck) + " across all collection logs.");
			} else {
			    c.sendMessage("You have not received any " + c.getItems().getItemName(itemIdToCheck) + " in your collection logs yet.");
			}

			break;
		case 56432:
		case 56434:
		case 5382:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot remove items from the bank whilst trading.");
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().removeItem(removeId, 5);
				return;
			}
			c.getItems().removeFromBank(removeId, 5, true);
			break;
		case 3322:
			MultiplayerSession session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession) {
				session.addItem(c, new Item(removeId, 5));
			}
			break;
		case 1688:
			//c.getSkillCapes().useOperate(removeId, removeSlot);
			c.getPA().useOperate(removeId, 1);
			break;
		case 3415:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 5));
			}
			break;

		case 6669:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 5));
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			c.getSmithing().readInput(c, c.playerLevel[c.playerSmithing], removeId, 5);
			break;

		}
	}

}
