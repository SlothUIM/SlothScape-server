package server.model.players.packets;

import server.model.players.Player;
import server.model.players.content.GodBookManager;
import server.model.players.skills.hunter.impling.Impling;
import server.model.players.PacketType;
import server.util.Misc;

/**
 * Item Click 2 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemOptionTwo implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();

		if (!c.getItems().playerHasItem(itemId, 1))
			return;

		switch (itemId) {

		case 13226:
			c.getHerbSack().withdrawAll();
			break;

		case 227:
			c.getItems().deleteItem(227, 1);
			c.getItems().addItem(229, 1);
			c.sendMessage("You have emptied the vial of water");
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

		case 11907:
		case 12899:
			int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
			if (charge <= 0) {
				if (itemId == 12899) {
					if (c.getToxicTridentCharge() == 0) {
						if (c.getItems().freeSlots() > 1) {
							c.getItems().deleteItem(12899, 1);
							c.getItems().addItem(12932, 1);
							c.getItems().addItem(11907, 1);
							c.sendMessage("You dismantle your Trident of the swamp.");
							return;
						} else {
							c.sendMessage("You need at least 2 inventory spaces to dismantle the trident.");
							return;
						}
					}
				} else {
					c.sendMessage("Your trident currently has no charge.");
					return;
				}
			}
			
			if (c.getItems().freeSlots() < 3) {
				c.sendMessage("You need at least 3 free slots for this.");
				return;
			}
			c.getItems().addItem(554, 5 * charge);
			c.getItems().addItem(560, 1 * charge);
			c.getItems().addItem(562, 1 * charge);
			
			if (itemId == 12899) {
				c.getItems().addItem(12934, 10 * charge);
			}
			
			if (itemId == 11907) {
				c.setTridentCharge(0);
			} else {
				c.setToxicTridentCharge(0);
			}
			c.sendMessage("You revoke " + charge + " charges from the trident.");
			break;
		case 12020:
			c.getGemBag().check();
			break;
			/*case 12926:
			if(c.DartType == 809)
				c.sendMessage("Your Toxic Blowpipe has "+ c.BlowpipeCharges + " scales and there are " + c.BlowpipeDarts + " Mithril Darts loaded into it.");
			if(c.DartType == 810)
				c.sendMessage("Your Toxic Blowpipe has "+ c.BlowpipeCharges + " scales and there are " + c.BlowpipeDarts + " Adamant Darts loaded into it.");
			if(c.DartType == 811)
				c.sendMessage("Your Toxic Blowpipe has "+ c.BlowpipeCharges + " scales and there are " + c.BlowpipeDarts + " Runite Darts loaded into it.");
			//if(c.DartType == 809)
				//c.sendMessage("Your Toxic Blowpipe has "+ c.BlowpipeCharges + " scales and there are " + c.BlowpipeDarts + " loaded into it.");
			break;*/

			case 12926:
				if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0) {
					c.sendMessage("You have no ammo in the pipe.");
					return;
				}
				if (c.getItems().addItem(c.getToxicBlowpipeAmmo(), c.getToxicBlowpipeAmmoAmount())) {
					c.setToxicBlowpipeAmmoAmount(0);
					c.sendMessage("You unload the pipe.");
				}
				break;

			case 19675:
				c.sendMessage("Your Arclight has " + c.getArcLightCharge() + " charges remaining.");
				break;
		case 11694:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(11690, 1);
			c.getItems().addItem(11702, 1);
			c.sendMessage("You dismantle the godsword blade from the hilt.");
			break;
		case 11696:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(11690, 1);
			c.getItems().addItem(11704, 1);
			c.sendMessage("You dismantle the godsword blade from the hilt.");
			break;
		case 11698:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(11690, 1);
			c.getItems().addItem(11706, 1);
			c.sendMessage("You dismantle the godsword blade from the hilt.");
			break;
		case 11700:
			c.getItems().deleteItem(itemId, 1);
			c.getItems().addItem(11690, 1);
			c.getItems().addItem(11708, 1);
			c.sendMessage("You dismantle the godsword blade from the hilt.");
			break;
		case 3839: // damaged holy book
		    int pages = GodBookManager.getPageCountForBook(c, 3839); // always use damagedBookId here
		    c.sendMessage("You have added " + pages + "/4 pages to your Holy Book.");
		    break;

		case 3840: // Holy Book (Saradomin)
		case 3842: // Unholy Book (Zamorak)
		case 3844: // Book of Balance (Guthix)
		    GodBookManager.openPreachMenu(c, itemId);
		    break;

			case 11941:
				for (int ITEM = 0; ITEM < 28; ITEM++) {
					c.getPA().sendFrame34a(43710, c.playerItems[ITEM] - 1, ITEM, c.playerItemsN[ITEM]);
				}
			c.setSidebarInterface(3, 43700);
			break;
			case 11238:
			case 11240:
			case 11242:
			case 11244:
			case 11246:
			case 11248:
			case 11250:
			case 11252:
			case 11254:
			case 11256:
			case 19732:
				Impling.getReward(c, itemId);
				break;
			/*
			 * Magic skillcape
			 */
		case 9762:
		case 9763:
			c.getPA().swapSpellBookOperate();
			break;
		case 13121:
		case 13122:
		case 13123:
		case 13124:
			c.getPA().handleArdyCloak();
			break;

		case 9810:
		case 9811:
				c.getSkillCapes().sendDialogue(5534, "Farming guild", "Catherby", "Ardougne", "Falador", "Morytania");
			break;
		default:
			if (c.playerRights == 3)
				Misc.println(c.playerName + " - Item2ndOption: " + itemId);
			break;
		}

	}

}
