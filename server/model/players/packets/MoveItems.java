package server.model.players.packets;

import server.model.players.Player;
import server.model.players.content.DeathRetrieval;
import server.world.World;

import java.util.Objects;

import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.players.PacketType;

/**
 * Move Items
 **/
public class MoveItems implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readUnsignedWordBigEndianA();
		boolean insertMode = c.getInStream().readUnsignedByteC() == 1;
		int from = c.getInStream().readUnsignedWordBigEndianA();
		int to = c.getInStream().readUnsignedWordBigEndian();
		System.out.println("Swapping from=" + from + " to=" + to + " mode=" + interfaceId + " insert=" + insertMode);
		c.slot = to;
		/*if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}*/
		/*if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}*/
		/*if (c.getTutorial().isActive()) {
			c.getTutorial().refresh();
			return;
		}*/
		/*if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}*/
		switch(interfaceId) {

		case 51710:
			DeathRetrieval.destroyItem(c, c.slot);
			break;
		}
		if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			c.sendMessage("You cannot move items whilst trading.");
			return;
		}
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("You cannot move items right now.");
			return;
		}
		c.getItems().moveItems(from, to, interfaceId, insertMode);
	}
}
