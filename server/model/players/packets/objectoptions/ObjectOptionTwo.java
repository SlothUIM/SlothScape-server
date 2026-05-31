package server.model.players.packets.objectoptions;

import server.Config;
import server.clip.ObjectDef;
import server.clip.WorldObject;
//import server.content.cannon.DwarfCannon;
import server.model.players.skills.hunter.trap.impl.Pitfall;
import server.world.Location;
import server.model.content.STASH;
import server.model.content.StashUnit;
import server.model.minigames.NMZRewards;
import server.model.players.Player;
import server.model.players.Right;
import server.model.players.content.CropPicking;
import server.model.players.content.teleports.FairyRings;
import server.model.players.content.treasuretrails.types.SearchClues;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.teleports.JewelleryBox;
import server.model.players.skills.farming.Farming;
import server.model.players.skills.mining.Mining;
import server.model.players.skills.thieving.Thieving.Stall;
//import server.model.players.skills.FlaxPicking;
//import server.model.players.skills.thieving.Thieving.Stall;
//import server.net.packet.impl.objectoptions.impl.CompCapeRack;
//import server.net.packet.impl.objectoptions.impl.DarkAltar;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

/*
 * @author Matt
 * Handles all 2nd options for objects.
 */

public class ObjectOptionTwo {

	public static void handleOption(final Player c, WorldObject worldObject, int face) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}

		int objectId = worldObject.getId();
		int objectX = worldObject.getX();
		int objectY = worldObject.getY();
		//c.resetInteractingObject();
		//c.getFarming().patchObjectInteraction(objectId, -1, objectX, objectY);
		//if (World.getWorld().getHolidayController().clickObject(c, 2, objectId, objectX, objectY)) {
		//	return;
		//}
		c.worldObject = worldObject;
		Location location = new Location(objectX, objectY, c.getHeight());
		ObjectDef def = ObjectDef.forID(objectId);
		if ((def!=null ? def.name : null)!= null && def.name.toLowerCase().contains("bank")) {
			c.getPA().openUpBank();
		}
		if (c.getRights().isOrInherits(Right.OWNER))
			c.sendMessage("Clicked Object Option 2:  "+objectId+"");

		GlobalObject object = new GlobalObject(objectId, objectX, objectY, c.getHeight()); 
		if (Farming.inspectObject(c, objectX, objectY)) {
           // return;
        }				
		if (SearchClues.handleObjectSearch(c, objectId, objectX, objectY)) {
            return; // The clue system handled it, stop running standard object code!
        }
		if (StashUnit.forObjectId(objectId) != null) {
		    if (!c.stashBuilt[StashUnit.forObjectId(objectId).ordinal()]) {
		        STASH.buildStash(c, objectId);
		    } else {
		        STASH.interactStash(c, objectId);
		    }
		    return; // Stops normal object clicking
		}
		if(CropPicking.pickCrop(c, objectId, objectX, objectY, worldObject.type, worldObject.face))
			return;
		if (objectId >= 19253 && objectId <= 19332) {
			Pitfall.dismantleTrap(c, objectId);
			return;
		}
		switch (objectId) {
		case 26277: 
        case 26278: 
        case 26279: 
        case 26280: 
            NMZRewards.handleBarrel(c, objectId, 2);
            break;
		/*case 15506:
		case 7465:
		case 7464:
		case 7463:
		case 5585:
		case 5584:
		case 5583:
		case 313:
			c.getItems().addItem(1947, 1);
			break;*/
		case 12965:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3164, 3306, 2, 1);
			break;
		case 29005://edgeville bush stash

			c.getPA().sendConfig(1366, 0 << 18);
			break;
		case 37492:
			JewelleryBox.openTeleportMenu(c, 0);
			break;
		case 37501:
			JewelleryBox.openTeleportMenu(c, 1);
			break;
		case 37520:
			JewelleryBox.openTeleportMenu(c, 2);
			break;
		case 29495:
			FairyRings.open(c);
			//c.setSidebarInterface(15, 63460);
			break;
		case 13523:
			c.getPA().startTeleport(Config.KARAMJA_X, Config.KARAMJA_Y, 0, "modern");
			break;
		//case DwarfCannon.CANNON_OBJECT_ID:
		//	c.cannon.pickupCannon(object);
		//	break;
		case 2090:
        case 2091:
        case 10079:
            Mining.prospectRock(c, "copper ore");
            break;
        case 2094:
        case 2095:
        case 10080:
            Mining.prospectRock(c, "tin ore");
            break;
        case 2110:
            Mining.prospectRock(c, "blurite ore");
            break;
        case 2092:
        case 2093:
            Mining.prospectRock(c, "iron ore");
            break;
        case 2100:
        case 2101:
            Mining.prospectRock(c, "silver ore");
            break;
        case 2098:
        case 2099:
            Mining.prospectRock(c, "gold ore");
            break;
        case 2096:
        case 2097:
            Mining.prospectRock(c, "coal");
            break;
        case 2102:
        case 2103:
            Mining.prospectRock(c, "mithril ore");
            break;
        case 2104:
        case 2105:
            Mining.prospectRock(c, "adamantite ore");
            break;
        case 2106:
        case 2107:
            Mining.prospectRock(c, "runite ore");
            break;
        case 10947:
            Mining.prospectRock(c, "granite");
            break;
        case 10946:
            Mining.prospectRock(c, "sandstone");
            break;
        case 2111:
            Mining.prospectRock(c, "gem rocks");
            break;
		case 16672:
			if(objectX == 3204)
				AgilityHandler.delayEmote(c, "", 3205, 3209, 2, 2);
			if(objectX == 3092 && objectY == 3104)
				AgilityHandler.delayEmote(c, "", 3093, 3106, 2, 2);
			else if(objectX == 2839 && objectY == 3537)
				AgilityHandler.delayEmote(c, "", 2840, 3539, 2, 2);
			break;
			
		case 12309:
			c.getShops().openShop(14);
			break;
		case 31621:
		case 29344:
		case 33393:
			if (c.lastTeleportX == 0) {
				c.sendMessage("You haven't teleported anywhere recently.");
			} else {
				c.getPA().startTeleport(c.lastTeleportX, c.lastTeleportY, c.lastTeleportZ, "modern");
			}
			break;
		case 22472: // npc drop tables on valius database
			c.getPA().showInterface(39500);
			break;
			
		/* PROSPECTING ORES */
		case 1294:
			//c.getDH().tree = "stronghold";
			c.getDH().sendDialogues(65, -1);
			break;

		case 1293:
			//c.getDH().tree = "village";
			c.getDH().sendDialogues(65, -1);
			break;
		case 1295:
			//c.getDH().tree = "grand_exchange";
			c.getDH().sendDialogues(65, -1);
			break;
		case 11388:
		case 11389:
			c.sendMessage("You carefully examine the rock... It's Amethyst!");
			break;
		case 9030:
			c.sendMessage("You carefully examine the rock... It's Gems!");
			break;
		case 11376:
		case 11377:
			c.sendMessage("You carefully examine the rock... It's Runite Ore!");
			break;
		case 11374:
		case 11375:
			c.sendMessage("You carefully examine the rock... It's Adamantite Ore!");
			break;
		case 11372:
		case 11373:
			c.sendMessage("You carefully examine the rock... It's Mithril Ore!");
			break;
		case 11370:
		case 11371:
			c.sendMessage("You carefully examine the rock... It's Gold Ore!");
			break;
		case 4676:
		case 11366:
		case 11367:
			c.sendMessage("You carefully examine the rock... It's Coal Ore!");
			break;
		case 11365:
		case 11364:
			c.sendMessage("You carefully examine the rock... It's Iron Ore!");
			break;
		case 11360:
		case 11361:
			c.sendMessage("You carefully examine the rock... It's Tin Ore!");
			break;
		case 10943:
		case 11161:
			c.sendMessage("You carefully examine the rock... It's Copper Ore!");
			break;
		case 4437:
		case 4438:
			c.sendMessage("You carefully examine the rock... It's Clay!");
			break;
			/* END OF SCUFFED PROSPECTING */
			

			
		case 27288:
			c.getPA().startTeleport2(3522, 3211, 0);
			break;
			
		case 11010:
			//c.getSmithing().sendSmelting();
			break;
			
		case 29334:
			//CompCapeRack.handleCapeRackInteraction(c);
		break;
			
		case 29778:
			c.sendMessage("hello");
			break;
		case 28900:
			//DarkAltar.handleRechargeInteraction(c);
			break;
		
		case 7811:
			//if (!c.inClanWarsSafe()) {
			//	return;
			//}
			c.getShops().openShop(115);
			break;
		/**
		 * Iron Winch - peek
		 */
		case 23104:
			//c.getDH().sendDialogues(110, 5870);
			break;
			
		case 2118:
			c.getPA().movePlayer(3434, 3537, 0);
			break;

		case 2114:
			c.getPA().movePlayer(3433, 3537, 1);
			break;
		case 26260:
			c.getDH().sendDialogues(55874, -1);
			break;
		case 14896:
			c.turnPlayerTo(objectX, objectY);
			//FlaxPicking.getInstance().pick(c, new Location(objectX, objectY, c.getHeight()));
			break;
		case 3840: // Compost Bin
			//c.getFarming().handleCompostRemoval();
		break;
		case 11728: // Yanille Agility door
		    // If player is outside (West), walk East. If inside (East), walk West.
			if(c.getX() == 2601 && c.getY() == 9481) {
				c.isPickingLock = true;
			}
				c.handleDoorEntry(objectX, objectY, objectX, objectY, "NORTH", worldObject);
		    break;

		case 882:// man hole close
			c.stopMovement();
	        new WorldObject(881, objectX, objectY, 0, 10, worldObject.face);
			break;
		case 1581:
			if(objectX == 3097 && objectY == 3468)
			new WorldObject(1579, objectX, objectY, 0, 22, worldObject.face);
			break;
		case 4874:
			// --- ARDOUGNE / COMMON STALLS ---
	    case 11730: // Bakery
	        c.getThieving().steal(Stall.Bakery, worldObject);
	        break;
	    case 11729: // Silk
	        c.getThieving().steal(Stall.Silk, worldObject);
	        break;
	    case 11731: // Gem
	        c.getThieving().steal(Stall.Gem, worldObject);
	        break;
	    case 11732: // Fur
	        c.getThieving().steal(Stall.Fur, worldObject);
	        break;
	    case 11733: // Spice
	        c.getThieving().steal(Stall.Spice, worldObject);
	        break;
	    case 11734: // Silver
	        c.getThieving().steal(Stall.Silver, worldObject);
	        break;
	    case 25824:
		case 14889:
		case 2644: // Lumbridge Spinning Wheel
		case 4309: // Seers Spinning Wheel
		    c.getCrafting().getSpinning().openSpinningInterface();
		    break;
	    // --- OTHER REGIONS ---
	    case 4875: // Tea (Varrock)
	        c.getThieving().steal(Stall.Tea, worldObject);
	        break;
	    case 7053: // Seed (Draynor)
	        c.getThieving().steal(Stall.Seed, worldObject);
	        break;
	    case 14011: // Wine (Kourend)
	        c.getThieving().steal(Stall.Wine, worldObject);
	        break;
	    case 28823: // Fruit (Kourend/Hosidius)
	        c.getThieving().steal(Stall.Fruit, worldObject);
	        break;
	    case 4876: // General (Don't forget this for low levels!)
	        c.getThieving().steal(Stall.General, worldObject);
	        break;
	    case 4877: // Magic
	        c.getThieving().steal(Stall.Magic, worldObject);
	        break;
	    case 4878: // Scimitar
	        c.getThieving().steal(Stall.Scimitar, worldObject);
	        break;
		case 23609:
			c.getPA().movePlayer(3507, 9494, 0);
			break;

		case 12266:
			boolean open = true;
				if(open) {
					c.startAnimation(827);
					c.getPA().sendConfig(680, 0);
					open = false;
				} 
			break;
		case 2558:
		case 8356://streequid
			c.getPA().movePlayer(1255, 3568, 0);
			break;
		case 2557:
			if (System.currentTimeMillis() - c.lastLockPick < 1000 || c.freezeTimer > 0) {
				return;
			}
			c.lastLockPick = System.currentTimeMillis();
			if (c.getItems().playerHasItem(1523, 1)) {

				if (Misc.random(10) <= 2) {
					c.sendMessage("You fail to pick the lock.");
					break;
				}
				if (objectX == 3044 && objectY == 3956) {
					if (c.getX() == 3045) {
						c.getPA().walkTo(-1, 0);
					} else if (c.getX() == 3044) {
						c.getPA().walkTo(1, 0);
					}

				} else if (objectX == 3038 && objectY == 3956) {
					if (c.getX() == 3037) {
						c.getPA().walkTo(1, 0);
					} else if (c.getX() == 3038) {
						c.getPA().walkTo(-1, 0);
					}
				} else if (objectX == 3041 && objectY == 3959) {
					if (c.getY() == 3960) {
						c.getPA().walkTo(0, -1);
					} else if (c.getY() == 3959) {
						c.getPA().walkTo(0, 1);
					}
				} else if (objectX == 3191 && objectY == 3963) {
					if (c.getY() == 3963) {
						c.getPA().walkTo(0, -1);
					} else if (c.getY() == 3962) {
						c.getPA().walkTo(0, 1);
					}
				} else if (objectX == 3190 && objectY == 3957) {
					if (c.getY() == 3957) {
						c.getPA().walkTo(0, 1);
					} else if (c.getY() == 3958) {
						c.getPA().walkTo(0, -1);
					}
				}
			} else {
				c.sendMessage("I need a lockpick to pick this lock.");
			}
			break;
		case 7814:
			if (c.playerMagicBook == 0) {
				c.playerMagicBook = 1;
				c.setSidebarInterface(6, 838);
				c.sendMessage("An ancient wisdomin fills your mind.");
			} else if (c.playerMagicBook == 1) {
				c.sendMessage("You switch to the lunar spellbook.");
				c.setSidebarInterface(6, 29999);
				c.playerMagicBook = 2;
			} else if (c.playerMagicBook == 2) {
				c.setSidebarInterface(6, 938);
				c.playerMagicBook = 0;
				c.sendMessage("You feel a drain on your memory.");
			}
			break;
		case 17010:
			if (c.playerMagicBook == 0) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 838);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 1) {
				c.sendMessage("You switch spellbook to lunar magic.");
				c.setSidebarInterface(6, 29999);
				c.playerMagicBook = 2;
				c.autocasting = false;
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			if (c.playerMagicBook == 2) {
				c.setSidebarInterface(6, 938);
				c.playerMagicBook = 0;
				c.autocasting = false;
				c.sendMessage("You feel a drain on your memory.");
				c.autocastId = -1;
				c.getPA().resetAutocast();
				break;
			}
			break;
		/*
		 * One stall that will give different amount of money depending on your thieving level, also different amount of xp.
		 */

		case 3044:
		case 24009:
		case 26300:
		case 16469:
		case 14838:
		case 2030:
			c.getSmithing().sendSmelting(c, true);
			break;
			
			/**
		 * Opening the bank.
		 */
		case 6943:
		case 10355:
		case 24101:
		case 14367:
		case 11758:
		case 10517:
		case 26972:
		case 25808:
		case 11744:
		case 11748:
		case 10060:
		case 24347:
		case 16700:
			c.getPA().openUpBank();
			break;

		}
	}
}
