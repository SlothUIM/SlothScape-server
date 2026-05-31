package server.model.players.packets;

import server.model.players.Player;

import java.util.Objects;

import server.model.items.Item;
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
 * Bank 10 Items
 **/
public class Bank10 implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readUnsignedWordBigEndian();
		int removeId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();

				System.out.println("Bank10 "+interfaceId+"]: "+removeId);
				if (interfaceId == 25546 || interfaceId == 25703 || interfaceId == 25705 || interfaceId == 25707 || interfaceId == 25709) {
		            NMZRewards.buyItem(c, interfaceId, removeId, 5);
		            return;
		        }
		switch (interfaceId) {
		case 1688:
			
			c.getPA().useOperate(removeId, 2);
			break;
			
			case -21826:
			case 43710:
				c.getItems().addtoLootbagFromDeposit(removeId, removeSlot, 10);
			break;
		case 41711:
		//if(c.addingToRP){
		for(int i = 0; i < 20; i++){
			if(removeId == c.RuneIDS[i]){
				c.getRunePouch().addRunesFromInventory(removeId, removeSlot, 10);
			break;
			}
		}
		//}
		break;
		
		case 7423:
			c.getItems().addToBank(removeId, 10, removeSlot, true);
			c.getItems().resetItems(7423);
		break;
			case 43933:
			PriceChecker.withdrawItem(c, removeId, removeSlot, 1);
			break;
		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 5);
			break;

		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 5);
			break;

		case 5064:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot do this whilst trading.");
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
				c.getItems().addToBank(removeId, 10, removeSlot, true);
			}
			if (c.isChecking) {
				PriceChecker.depositItem(c, removeId,
						10);
				return;
			}
			if (c.inSafeBox) {
				/*if (!c.pkDistrict && removeId != 13307) {
					c.sendMessage("You cannot do this right now.");
					return;
				}
				c.getSafeBox().deposit(removeId, 10);*/
			}

			if (c.isInInterface == 19543) {
				c.getFancyDressBox().deposit(c, removeId, 10);
			    return;
			}
			break;
		case 19524: case 19527: case 19530: case 19533: case 19536: case 19539: case 19542:
		    if (c.isInInterface == 19543) {
		    	c.getFancyDressBox().handleWithdraw(c, removeId, interfaceId, 10);
		        return;
		    }
		    break;
		case 56432:
		case 56434:
		case 5382:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot do this whilst trading.");
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().removeItem(removeId, 10);
				return;
			}
			c.getItems().removeFromBank(removeId, 10, true);
			break;

		case 3322:
			MultiplayerSession session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession) {
				session.addItem(c, new Item(removeId, 10));
			}
			break;

		case 3415:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 10));
			}
			break;

		case 6669:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 10));
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			c.getSmithing().readInput(c, c.playerLevel[c.playerSmithing], removeId, 10);
			break;
		}
	}

}
