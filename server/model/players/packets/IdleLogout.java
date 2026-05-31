package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;

public class IdleLogout implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		// if (!c.playerName.equalsIgnoreCase("Sanity"))
		 c.logout();
	}
}