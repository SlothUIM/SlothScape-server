package server.model.players.packets.objectoptions;

import server.Config;
import server.clip.WorldObject;
import server.model.players.Player;
import server.model.players.Right;
import server.model.players.skills.construction.RoomObject;
import server.world.World;

public class ObjectOptionFour {
	
	public static void handleOption(final Player c, WorldObject worldObject, int face) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		int objectId = worldObject.getId();
		int objectX = worldObject.getX();
		int objectY = worldObject.getY();
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.resetInteractingObject();
		c.clickObjectType = 0;
		if (c.getRights().isOrInherits(Right.OWNER))
			c.sendMessage("Clicked Object Option 4:  "+objectId);
		if(RoomObject.handleObjectFourthOption(objectX, objectY, objectId, c) && c.mapInstance != null)
			return;
		switch (objectId) {
		case 29213:
			if(c.getItems().freeSlots() > 1) {
				c.getPA().object(29166, objectX, objectY, 0, 10);
				c.getItems().addItem(9756, 1);
			}
			break;
		case 13523:
			c.getPA().startTeleport(Config.AL_KHARID_X, Config.AL_KHARID_Y, 0, "modern");
			break;
		case 8356://streehosidius
			c.getPA().movePlayer(1679, 3541, 0);
			break;
		}
	}

}
