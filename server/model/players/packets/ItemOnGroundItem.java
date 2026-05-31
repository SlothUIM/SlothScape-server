package server.model.players.packets;

import server.model.players.Player;
import server.model.players.skills.firemake.*;
import server.model.players.PacketType;
import server.util.Misc;
import server.world.World;
import server.*;

public class ItemOnGroundItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.getInStream().readSignedWordBigEndian();
		int itemUsed = c.getInStream().readSignedWordA();
		int groundItem = c.getInStream().readUnsignedWord();
		int gItemY = c.getInStream().readSignedWordA();
		int itemUsedSlot = c.getInStream().readSignedWordBigEndianA();
		int gItemX = c.getInStream().readUnsignedWord();
		if (!c.getItems().playerHasItem(itemUsed, 1, itemUsedSlot)) {
			return;
		}
		if (!World.getWorld().itemHandler.itemExists(groundItem, gItemX, gItemY)) {
			return;
		}
		Firemaking.lightFire(c, itemUsed, groundItem, gItemX, gItemY, true);
			
		switch (itemUsed) {

		default:
			if (c.playerRights == 3)
				Misc.println("ItemUsed " + itemUsed + " on Ground Item "
						+ groundItem);
			break;
		}
	}

}
