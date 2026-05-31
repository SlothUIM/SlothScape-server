package server.model.players.packets;

import java.util.Objects;

import server.Config;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.world.Boundary;
import server.model.players.PacketType;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int tradeId = c.getInStream().readSignedWordBigEndian();
		if(tradeId < 0 || tradeId >= PlayerHandler.players.length) {
			return;
		}
		Player requested = PlayerHandler.players[tradeId];
		if(requested == null) {
			return;
		}
		c.getPA().resetFollow();
		if (c.disconnected) {
			c.tradeStatus = 0;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			c.sendMessage("You cannot trade whilst inside the duel arena.");
			return;
		}

		if (Objects.equals(requested, c)) {
			c.sendMessage("You cannot trade yourself.");
			return;
		}
		if (c.playerRights == 2 && !Config.ADMIN_CAN_TRADE) {
			c.sendMessage("Trading as an admin has been disabled.");
			return;
		}
		if (c.getTrade().requestable(requested)) {
			c.getTrade().request(requested);
			return;
		}
	}

}
