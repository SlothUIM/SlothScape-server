package server.model.players.packets;

import server.model.players.Player;
import server.model.content.STASH;
import server.model.players.PacketType;
import server.model.players.skills.farming.Allotments;
import server.Server;

public class ChangeRegion implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {

		STASH.refreshConfigs(c);
		// Server.objectManager.loadObjects(c);
		//Server.objectHandler.updateObjects(c);
		//c.getAllotment().updateAllotmentsStates();
	}

}
