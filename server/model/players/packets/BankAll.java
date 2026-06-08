package server.model.players.packets;

import server.model.items.GameItem;
import server.model.items.Item;
import server.model.items.bank.BankItem;
import server.model.items.bank.BankTab;
import server.model.items.containers.FancyDressBox;
import server.model.multiplayer_session.MultiplayerSession;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.multiplayer_session.trade.TradeSession;
import server.model.players.Player;
import server.model.players.PacketType;
import server.model.players.PriceChecker;
import server.world.World;

import java.util.Objects;

/**
 * Bank All Items
 **/
public class BankAll implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int removeSlot = c.getInStream().readUnsignedWordA();
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWordA();

				System.out.println("BankAll "+interfaceId+"]: "+removeId);
				boolean isFancyBoxWithdraw = (interfaceId >= 19521 && interfaceId <= 19542);
				boolean isArmourCaseWithdraw = (interfaceId >= 29239 && interfaceId <= 29840);

				if (isFancyBoxWithdraw && c.isInInterface == 19543) {
				    c.getFancyDressBox().handleWithdraw(c, removeId, interfaceId, c.getItems().itemAmount(removeId));
				    return;
				}

				if (isArmourCaseWithdraw && c.isInInterface == 29236) {
				   // c.getArmourCase().handleWithdraw(c, removeId, interfaceId, c.getItems().itemAmount(removeId));
				    return;
				}
		switch (interfaceId) {
		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 10);
			break;
			
		case 41711:
		//if(c.addingToRP){
		for(int i = 0; i < 20; i++){
			if(removeId == c.RuneIDS[i]){
				c.getRunePouch().addRunesFromInventory(removeId, removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
				//c.openRunePouch();
				break;
			}
		}
		break;
		
			case -21826:
		case 43710:
		c.getItems().addtoLootbagFromDeposit(removeId, removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
		break;
		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 10);
			break;
		case 5064:
			if (c.inTrade) {
				c.sendMessage("You can't store items while trading!");
				return;
			}
			if (c.isChecking) {
				PriceChecker.depositItem(c, removeId,
						c.getItems().itemAmount(c.playerItems[removeSlot]));
				return;
			}

			if (c.isBanking) {
				c.getItems().addToBank(removeId, c.getItems().itemAmount(c.playerItems[removeSlot]), removeSlot, true);
			}

			if (c.isInInterface == 19543) {
		        c.getFancyDressBox().deposit(c, removeId, c.getItems().itemAmount(c.playerItems[removeSlot]));
		        return;
		    }
		    
		    // Armour Case Deposit
		    if (c.isInInterface == 29236) {
		        //c.getArmourCase().deposit(c, removeId, c.getItems().itemAmount(c.playerItems[removeSlot]));
		        return;
		    }
			break;
		case 56432:
		case 56434:
		case 5382:
			if (c.isBanking) {
				c.getItems().removeFromBank(removeId, c.getBank().getCurrentBankTab().getItemAmount(new BankItem(removeId + 1)), true);
				}
			break;

		case 7423:
				//c.getItems().bankItem(c.playerItems[removeSlot] , removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
		break;

		case 43933:
			if(c.isChecking)
				PriceChecker.withdrawItem(c, removeId, removeSlot, c.getItems().itemAmount(c.priceN[removeSlot]));
		break;
			case 3322:
				MultiplayerSession session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
				if (Objects.isNull(session)) {
					return;
				}
				if (session instanceof TradeSession || session instanceof DuelSession) {
					session.addItem(c, new Item(removeId, c.getItems().itemAmount(c.playerItems[removeSlot])));
				}
				break;

			case 3415:
				session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
				if (Objects.isNull(session)) {
					return;
				}
				if (session instanceof TradeSession) {
					session.removeItem(c, removeSlot, new Item(removeId, c.getItems().itemAmount(c.playerItems[removeSlot])));
				}
				break;

		case 1688:
			
			c.getPA().useOperate(removeId, 3);
			break;
		case 6669:
			/*if (Item.itemStackable[removeId] || Item.itemIsNote[removeId]) {
				for (GameItem item : c.getTradeAndDuel().stakedItems) {
					if (item.id == removeId) {
						c.getTradeAndDuel()
								.fromDuel(
										removeId,
										removeSlot,
										c.getTradeAndDuel().stakedItems
												.get(removeSlot).amount);
					}
				}

			} else {
				c.getTradeAndDuel().fromDuel(removeId, removeSlot, 28);
			}*/
			break;

		}
	}

}
