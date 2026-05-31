package server.model.players.packets;

import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.world.World;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.players.PacketType;

/**
 * Wear Item
 **/
public class WearItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.wearId = c.getInStream().readUnsignedWord();
		c.wearSlot = c.getInStream().readUnsignedWordA();
		c.interfaceId = c.getInStream().readUnsignedWordA();
		c.alchDelay = System.currentTimeMillis();
		c.nextChat = 0;
		c.dialogueAction = 0;

		c.sendMessage("ID: " + c.wearId + "Slot: " +c.wearSlot);
		int wearId = c.wearId;
		if (!c.getItems().playerHasItem(c.wearId, 1, c.wearSlot)) {
			return;
		}
		if ((c.playerIndex > 0 || c.npcIndex > 0) && wearId != 4153 && wearId != 12848 && !c.usingMagic && !c.usingBow && !c.usingOtherRangeWeapons && !c.usingCross && !c.usingBallista)
			c.getCombat().resetPlayerAttack();
		if (c.canChangeAppearance) {
			c.sendMessage("You can't wear an item while changing appearence.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if(c.wearId == 11941) {
			if(c.playerLootItems != null) {
			for (int ITEM = 0; ITEM < 28; ITEM++) {
				c.getPA().sendFrame34a(42710, c.playerLootItems[ITEM] - 1, ITEM, c.playerLootItemsN[ITEM]);
			}
			c.getPA().sendFrame126("Value: " + String.format("%,d", c.getLootBagWealth()), 42720);
			c.viewingLootBag = true;
				//c.getPA().sendFrame126("Value: "+c.getLootBagWealth(), 42720);
		c.setSidebarInterface(3, 42700);
			return;
			}
		}
		if (TreasureTrails.isClueScroll(c.wearId)) {
            TreasureTrails.checkSteps(c, c.wearId);
            return; // Stop the code! Don't let the server try to wear the paper!
        }
		if(c.wearId == 22586) {
			for (int ITEM = 0; ITEM < 28; ITEM++) {
				c.getPA().sendFrame34a(42710, c.playerLootItems[ITEM] - 1, ITEM, c.playerLootItemsN[ITEM]);
			}
			c.getPA().sendFrame126("Value: " + String.format("%,d", c.getLootBagWealth()), 42720);

			c.viewingLootBag = true;
				//c.getPA().sendFrame126("Value: "+c.getLootBagWealth(), 42720);
			c.setSidebarInterface(3, 42700);
			return;
		}
		if (c.wearId >= 5509 && c.wearId <= 5515) {
			int pouch = -1;
			int a = c.wearId;
			if (a == 5509)
				pouch = 0;
			if (a == 5510)
				pouch = 1;
			if (a == 5512)
				pouch = 2;
			if (a == 5514)
				pouch = 3;
			c.getPA().emptyPouch(pouch);
			return;
		}
		switch(wearId) {
		case 12020:
			c.openGembag = true;
			c.getItems().deleteItem(wearId, c.getItems().getItemSlot(wearId), 1);
			c.getItems().addItem(24481, 1);
			c.sendMessage("You open your gem bag, ready to fill it.");
			break;
		case 24481:
			c.openGembag = false;
			c.getItems().deleteItem(wearId, c.getItems().getItemSlot(wearId), 1);
			c.getItems().addItem(12020, 1);
			c.sendMessage("You close your gem bag.");
		break;
		}
		int currentWeapon = c.playerEquipment[c.playerWeapon];

		// 1. Block wearing shields (slot 5) or gloves (slot 9) if the falcon is equipped
		if (currentWeapon == 10024 || currentWeapon == 10023) {
			if (c.wearSlot == 5 || c.wearSlot == 9) {
				c.sendMessage("Both of your hands must be free to handle the falcon.");
				return; // Stop the equip process
			}
		}

		// 2. If equipping the falcon, force unequip the shield and gloves automatically
		if (wearId == 10024 || wearId == 10023) {
			if (c.playerEquipment[c.playerShield] > 0) {
				c.getItems().removeItem(c.playerEquipment[c.playerShield], 5);
			}
			if (c.playerEquipment[c.playerHands] > 0) {
				c.getItems().removeItem(c.playerEquipment[c.playerHands], 9);
			}
			// Notice: We don't need to manually remove the weapon, because
			// c.getItems().wearItem natively replaces the weapon slot!
		}
		if (!World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.getItems().wearItem(wearId, c.wearSlot);
		}
	}

}
