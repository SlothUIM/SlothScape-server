package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemOptionThree implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();
		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}

		switch (itemId) {
		case 1929:
			c.getItems().deleteItem(1929, c.getItems().getItemSlot(itemId),1);
			c.getItems().addItem(1925, 1);
			c.sendMessage("You empty your bucket.");
			break;
		case 19675:
			c.sendMessage("Your Arclight has " + c.getArcLightCharge() + " charges remaining.");
			break;
		case 13128:
			if (c.getRunEnergy() < 100) {
				if (c.getRechargeItems().useItem(itemId)) {
					c.getRechargeItems().replenishRun(100);
				}
			} else {
				c.sendMessage("You already have full run energy.");
				return;
			}
			break;
			
		case 13226:
			c.getHerbSack().check();
			break;
			
		case 12020:
			c.getGemBag().withdrawAll();
			break;
		case 12791:
		c.getCharges().emptyRunePouch();
		break;

		case 11283:
		case 33115:
			if (c.getDragonfireShieldCharge() == 0) {
				c.sendMessage("Your dragonfire shield has no charge.");
				return;
			}
			c.setDragonfireShieldCharge(0);
			c.sendMessage("Your dragonfire shield has been emptied.");
			break;
		case 1712:
			c.getPA().handleGlory(itemId);
			break;
		case 12926:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(12924, 1);
			c.getItems().addItem(12934, c.BlowpipeCharges);
			c.getItems().addItem(c.DartType, c.BlowpipeDarts);
			c.BlowpipeCharges = 0;
			c.sendMessage("You unload your blowpipe");
			break;
		default:
			if (c.playerRights == 3)
				Misc.println(c.playerName + " - Item3rdOption: " + itemId
						+ " : " + itemId11 + " : " + itemId1);
			break;
		}

	}

}
