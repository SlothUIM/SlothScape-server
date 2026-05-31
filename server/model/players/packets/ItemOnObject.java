package server.model.players.packets;

/**
 * @author Ryan / Lmctruck30
 */

import server.model.items.UseItem;
import server.model.players.Player;
import server.model.players.PacketType;
import server.model.items.*;
import server.model.players.combat.Hitmark;
import server.model.players.quests.MerlinsCrystal;
import server.model.players.skills.Skill;
import server.model.players.skills.hunter.trap.impl.Pitfall;

public class ItemOnObject implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		/*
		 * a = ? b = ?
		 */

		int interfaceId = c.getInStream().readUnsignedWord();
		int objectId = c.getInStream().readInt();
		int objectY = c.getInStream().readSignedWordBigEndianA();
		int itemSlot = c.getInStream().readUnsignedWordBigEndian();
		int objectX = c.getInStream().readSignedWordBigEndianA();
		int itemId = c.getInStream().readUnsignedWord();
		int objectType = c.getInStream().readUnsignedByte();
		System.out.println("objectId: " + objectId);
		System.out.println("objectY: " + objectY);
		System.out.println("objectX: " + objectX);
		System.out.println("itemSlot: " + itemSlot);
		System.out.println("itemId: " + itemId);
		System.out.println("objectType: " + objectType);
		c.objectX = objectX;
		c.objectY = objectY;
		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		// Using Logs (1511) on an Empty Pit (19227)
		if (itemId == 1511 && objectId >= 19253 && objectId <= 19332) {
			Pitfall.setupTrap(c, objectId);
			return;
		}
		UseItem.ItemonObject(c, objectId, objectX, objectY, itemId);
		if (objectId == 9380 || objectId == 9345) { // Box trap or Bird Snare
			// Fetch the trap from the world
			server.world.objects.GlobalObject globalTrap = new server.world.objects.GlobalObject(objectId, objectX, objectY, c.getHeight());
			server.model.players.skills.hunter.trap.Trap trap = server.model.players.skills.hunter.Hunter.getTrap(c, globalTrap).orElse(null);

			if (trap != null && trap.getState() == server.model.players.skills.hunter.trap.Trap.TrapState.PENDING) {

				// Smoking (Torch)
				if (itemId == 596) {
					if (trap.isSmoked()) {
						c.sendMessage("This trap has already been smoked.");
						return;
					}
					c.startAnimation(827);
					trap.setSmoked(true);
					c.sendMessage("You smoke the trap, removing your scent.");
					return;
				}

				// Baiting (Spicy Tomato - Add your other baits here!)
				if (itemId == 9986) {
					if (trap.isBaited()) {
						c.sendMessage("This trap is already baited.");
						return;
					}
					c.getItems().deleteItem(itemId, 1);
					c.startAnimation(827);
					trap.setBaited(true);
					c.sendMessage("You place the bait inside the trap.");
					return;
				}
			}
		}
		switch(objectId){
			case 68: // Beehive
				if (itemId == 28) { // Insect repellent
					c.sendMessage("You spray the beehive with the insect repellent. The bees seem docile.");
					c.beesDocile = true; // Add a 'public boolean beesDocile;' to Player.java
				} else if (itemId == 1925) { // Empty Bucket
					// 10918 is the Beekeeper's hat, add other pieces if you have the full set
					if (c.beesDocile || c.getItems().isWearingItem(10918)) {
						c.getItems().deleteItem(1925, 1);
						c.getItems().addItem(MerlinsCrystal.BUCKET_OF_WAX, 1);
						c.sendMessage("You retrieve a bucket of wax from the beehive.");
					} else {
						c.sendMessage("The bees swarm you! You need to calm them down first.");
						c.appendDamage(2, Hitmark.HIT); // Deal 2 damage
					}
				} else {
					c.sendMessage("Nothing interesting happens.");
				}
				break;
			case 9736:
				if (itemId == 2307)
					if(c.tutorialProgress >= 8) {
					c.startAnimation(896);
					c.getDH().sendDialogues(3037, 0);
					// c.getPA().requestUpdates();
					c.getItems().deleteItem(2307, 1);
					c.getItems().addItem(2309, 1);
				}
				break;
		case 3044:
			if (itemId == 438 || itemId == 436) {
				if (c.getItems().playerHasItem(438) && c.getItems().playerHasItem(436)) {
					if (c.tutorialProgress == 19) {
						c.startAnimation(899);
						c.getPA().sendSound(352, 100, 1);
						c.sendMessage("You smelt the copper and tin together in the furnace.");
						c.getItems().deleteItem(438, 1);
						c.getItems().deleteItem(436, 1);
						c.sendMessage("You retrieve a bar of bronze.");
						c.getItems().addItem(2349, 1);
						c.getDH().sendDialogues(3062, -1);
					} else if (c.tutorialProgress > 19) {
						c.startAnimation(899);
						c.getPA().sendSound(352, 100, 1);
						c.sendMessage("You smelt the copper and tin together in the furnace.");
						c.getItems().deleteItem(438, 1);
						c.getItems().deleteItem(436, 1);
						c.sendMessage("You retrieve a bar of bronze.");
						c.getItems().addItem(2349, 1);
					}
				}
			}
		break;
		}
	}

}
