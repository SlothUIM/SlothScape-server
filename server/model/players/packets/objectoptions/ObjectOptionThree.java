package server.model.players.packets.objectoptions;

import server.Config;
import server.clip.WorldObject;
import server.model.minigames.NMZRewards;
//import server.content.cannon.DwarfCannon;
import server.model.players.skills.hunter.trap.impl.Pitfall;
import server.model.players.Player;
import server.model.players.Right;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.mining.Mining;
import server.world.World;
import server.world.objects.GlobalObject;

/*
 * @author Matt
 * Handles all 3rd options for objects.
 */

public class ObjectOptionThree {

	public static void handleOption(final Player c, int id, int x, int y) {
		int objectId = id;
		int objectX = x;
		int objectY = y;
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.resetInteractingObject();
		c.clickObjectType = 0;
		// c.sendMessage("Object type: " + objectType);
		//if (World.getWorld().getHolidayController().clickObject(c, 3, objectId, objectX, objectY)) {
		//	return;
		//}

		GlobalObject object = new GlobalObject(objectId, objectX, objectY, c.getHeight());
		if (c.getRights().isOrInherits(Right.OWNER))
			c.sendMessage("Clicked Object Option 3:  "+objectId+"");

		if (objectId >= 19253 && objectId <= 19332) {
			Pitfall.setupTrap(c, objectId);
			return;
		}

		switch (objectId) {

		case 26277: 
        case 26278: 
        case 26279: 
        case 26280: 
            NMZRewards.handleBarrel(c, objectId, 3);
            break;
		case 29495:
			c.getPA().startTeleport(c.lastFairyRingX, c.lastFairyRingY, 0, "fairy");
			break;

		case 12965:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3164, 3306, 0, 1);
			break;
		case 13523:
			c.getPA().startTeleport(Config.DRAYNOR_X, Config.DRAYNOR_Y, 0, "modern");
			break;
		case 15477:
			c.setLastKnownLocation(c.getLocation());
			HouseData.enterHouse(c, c, true);
			break;
		case 16672:
			if(objectX == 3204)
				AgilityHandler.delayEmote(c, "", 3205, 3209, 0, 2);
			if(objectX == 3092 && objectY == 3104)
				AgilityHandler.delayEmote(c, "", 3093, 3106, 0, 2);
			else if(objectX == 2839 && objectY == 3537)
				AgilityHandler.delayEmote(c, "", 2840, 3539, 0, 2);
			break;
		case 10080:
            Mining.prospectRock(c, "tin ore");
            break;
		//case DwarfCannon.CANNON_OBJECT_ID:
		//	c.cannon.emptyFuel(object);
		//	break;
		case 22472:
			c.getPA().showInterface(36000);
			//c.getAchievements().drawInterface(0);
			break;
		case 24101://Opens Trading Post with bank booth
		case 6943:
			//c.sendMessage("Trading post has been temporarily disabled!");
	           // Listing.openPost(c, false, true);
	            break;
		case 8356://streexerics
			c.getPA().movePlayer(1311, 3614, 0);
			break;
		case 7811:
			//if (!c.inClanWarsSafe()) {
			//	return;
			//}
			c.getDH().sendDialogues(818, 6773);
			break;
		}
	}


}
