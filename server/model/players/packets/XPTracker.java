package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;

/**
 * Move Items
 **/
	public class XPTracker implements PacketType {

		@Override
		public void processPacket(Player c, int packetType, int packetSize) {
			int somejunk = c.getInStream().readUnsignedWord(); // junk
			int somejunk2 = c.getInStream().readUnsignedWord(); // junk
			System.out.println("Skill: "+ somejunk +" Exp: "+ somejunk2);
		}
		
	}