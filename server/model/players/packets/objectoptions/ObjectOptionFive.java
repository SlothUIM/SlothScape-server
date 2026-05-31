package server.model.players.packets.objectoptions;

import server.clip.WorldObject;
import server.model.players.Player;
import server.model.players.Right;
import server.model.players.skills.construction.RoomObject;
import server.world.World;

public class ObjectOptionFive {
	
	public static void handleOption(Player c, WorldObject worldObject, int face) {
		int objectId = worldObject.getId();
		int objectX = worldObject.getX();
		int objectY = worldObject.getY();
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		//c.resetInteractingObject();
		if (c.getRights().isOrInherits(Right.OWNER))
			c.sendMessage("Clicked Object Option 5:  "+objectId+"");
		//RoomObject.handleRemoveClick(objectX, objectY, objectId, c);

		if(RoomObject.handleSpaceClick(objectX, objectY, objectId, c))
			return;
		if(RoomObject.handleRemoveClick(objectX, objectY, objectId, c))
			return;

		if(RoomObject.handleObjectFourthOption(objectX, objectY, objectId, c))
		return;
		switch (objectId) {	
		
		}
	}

}
