package server.model.players.packets;

/**
 * @author Ryan / Lmctruck30
 */

import server.model.items.UseItem;
import server.model.players.Player;
import server.model.players.content.GodBookManager;
import server.model.players.PacketType;
import server.model.players.skills.CoxHerblore;

public class ItemOnItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int usedWithSlot = c.getInStream().readUnsignedWord();
		int itemUsedSlot = c.getInStream().readUnsignedWordA();
		System.out.println("ItemOnItem: used: "+usedWithSlot+" with "+itemUsedSlot);
		int useWith = c.playerItems[usedWithSlot] - 1;
		int itemUsed = c.playerItems[itemUsedSlot] - 1;
		if (!c.getItems().playerHasItem(useWith, 1, usedWithSlot)
				|| !c.getItems().playerHasItem(itemUsed, 1, itemUsedSlot)) {
			return;
		}
		boolean checkCoxHerblore = CoxHerblore.handleItemCombination(c, itemUsed, useWith);
		if (checkCoxHerblore) {
			return;
		}
		if (GodBookManager.handleBookPageUse(itemUsed, useWith, c)) {
		    return;
		}
		if (c.getCrafting().handleItemOnItem(itemUsed, useWith)) {
		    return;
		}
		UseItem.ItemonItem(c, itemUsed, useWith);
	}

}
