package server.model.players.packets;

import server.model.players.Player;

import java.util.Objects;

import server.Config;
import server.model.items.Item;
import server.model.items.containers.CostumeRoomContainer;
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
import server.model.players.content.DeathRetrieval;
import server.model.players.content.treasuretrails.types.PuzzleBox;
import server.model.players.skills.construction.RoomObject;
import server.model.players.skills.smithing.Smithing;
import server.world.World;

/**
 * Remove Item
 **/
public class RemoveItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();
		int removeId = c.getInStream().readUnsignedWordA();

		c.sendMessage("Bank 1: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);
		if (c.isInInterface == 19543 || c.isInInterface == 29236) {
		    
		    // Identify which container instance to use
		    CostumeRoomContainer container = (c.isInInterface == 19543) 
		                                     ? c.getFancyDressBox() 
		                                     : c.getArmourCase();

		    // Use the universal withdrawal/deposit logic from the parent class
		    // This replaces your manual toggle logic
		    container.handleWithdraw(c, removeId, interfaceId, 1);
		}
		if (interfaceId == 25546 || interfaceId == 25703 || interfaceId == 25705 || interfaceId == 25707 || interfaceId == 25709) {
            c.sendMessage(c.getItems().getItemName(removeId) + ": costs " + NMZRewards.getPrice(removeId) + " NMZ points.");
            return;
        }
		switch (interfaceId) {
			case 15682:
				case 15683:
				c.getFarmingTools().withdrawItems(removeId, 1);
				break;
			case 15595:
			case 15594:
				c.getFarmingTools().storeItems(removeId, 1);
				break;
		case 6980: // Treasure Trails Sliding Puzzle Container
			System.out.println("sliding piece");
            PuzzleBox.clickTile(c, removeId, removeSlot);
            break;
		case 43710:
			for (int i : Config.DESTROYABLE_ITEMS) {
				if (i == removeId) {
					c.sendMessage("You can't put untradable items in your looting bag.");
					return;
				}
			}
					c.getItems().addtoLootbagFromDeposit(removeId, removeSlot, 1);
			break;

		//case 53080:
		case 403:
			RoomObject.handleItemClick(removeId, c);
			return;
		case 51710:
			DeathRetrieval.withdrawItem(c, removeSlot);
			return;
		case 1688:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot remove items whilst trading, trade declined.");
				return;
			}
			DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("Your actions have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			c.getItems().removeItem(removeId, removeSlot);
			break;

		case 5064:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the bank whilst trading.");
				return;
			}
			duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(removeId, 1, removeSlot, true);
			}
			if (c.inSafeBox) {
				//if (!c.pkDistrict && removeId != 13307) {
				//	c.sendMessage("You cannot do this right now.");
				//	return;
				//}
				//c.getSafeBox().deposit(removeId, 1);
			}

			if (c.isChecking) {
				PriceChecker.depositItem(c, removeId, 1);
				return;
			} 
			if (c.isInInterface == 19543) {
				c.getFancyDressBox().deposit(c, removeId, 1);
			    return;
			}
			if (c.isInInterface == 29236) {
				c.getArmourCase().deposit(c, removeId, 1);
			    return;
			}
			break;
		
		case 41711:
			for(int i = 0; i < 20; i++){
				if(removeId == c.RuneIDS[i]){
					c.getRunePouch().addRunesFromInventory(removeId, removeSlot, 1);
				}
			}
			break;
		case 43933:
			if (c.isChecking)
				PriceChecker.withdrawItem(c, removeId, removeSlot, 1);
			break;
		case 56432:
		case 56434:
		case 5382:
			if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot remove items from the bank whilst trading.");
				return;
			}
			duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().removeItem(removeId, 1);
				return;
			}
			c.getItems().removeFromBank(removeId, 1, true);
			break;

		case 3900:
			c.getShops().buyFromShopPrice(removeId, removeSlot);
			break;
		case 7423://deposit box
			c.getItems().addToBank(removeId, 1, removeSlot, true);
			c.getItems().resetItems(7423);
			break;
		case 3823:
			c.getShops().sellToShopPrice(removeId, removeSlot);
			break;

		case 3322:
			MultiplayerSession session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession) {
				session.addItem(c, new Item(removeId, 1));
			}
			break;

		case 3415:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 1));
			}
			break;

		case 6669:
			session = World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, removeSlot, new Item(removeId, 1));
			}
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			Smithing.readInput(c, c.playerLevel[c.playerSmithing], removeId, 1);
			break;
		}
	}

}
