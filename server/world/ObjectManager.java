package server.world;

import java.util.ArrayList;
import java.util.Iterator;

import server.model.objects.Object;
import server.util.Misc;
import server.clip.WorldObject;
import server.model.players.Player;
import server.model.players.PlayerHandler;

/**
 * @author Sanity
 */

public class ObjectManager {

	public ArrayList<Object> objects = new ArrayList<Object>();
	public ArrayList<WorldObject> object = new ArrayList<WorldObject>();
	private ArrayList<Object> toRemove = new ArrayList<Object>();

	public void process() {
		Iterator<WorldObject> it = object.iterator();
	    while (it.hasNext()) {
	        WorldObject o = it.next();
	        if (o.ticks > 0) {
	            o.ticks--;
	            
	            if (o.ticks == 0) {
	                updateObjectForAll(o);
	                it.remove();
	            }
	        }
	    }
		for (Object o : objects) {
			if (o.tick > 0)
				o.tick--;
			else {
				
                    updateObject(o);
                toRemove.add(o);
			}
		}
		for (Object o : toRemove) {
			if (isObelisk(o.newId)) {
				int index = getObeliskIndex(o.newId);
				if (activated[index]) {
					activated[index] = false;
					teleportObelisk(index);
				}
			}
			objects.remove(o);
		}
		toRemove.clear();
	}

	public void removeObject(Object o) {
	    objects.remove(o);
	    for (Player p : PlayerHandler.players) {
	        if (p != null) {
	            p.getPA().object(-1, o.objectX, o.objectY, o.height, o.type);
	        }
	    }
	}

	public void removeObject(int x, int y) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = (Player) PlayerHandler.players[j];
				c.getPA().object(-1, x, y, 0, 10);
			}
		}
	}
	public void replaceObject(Object o) {
	    removeObject(o.objectX, o.objectY, o.height);
	    addObject(o);
	}

	public void removeObject(int x, int y, int height) {
	    Object toRemove = null;
	    for (Object o : objects) {
	        if (o.objectX == x && o.objectY == y && o.height == height) {
	            toRemove = o;
	            break;
	        }
	    }
	    if (toRemove != null) {
	        objects.remove(toRemove);
	    }
	}
	private int getOpenFace(int closedFace, boolean hingeOnLeft) {
	    return hingeOnLeft ? (closedFace + 1) % 4 : (closedFace + 3) % 4;
	}

	public void updateDoor(Object o, boolean open, boolean hingeOnLeft) {
	    int face = open ? getOpenFace(o.face, hingeOnLeft) : o.face;
	    int id = open ? o.newId : o.objectId;

	    for (int j = 0; j < PlayerHandler.players.length; j++) {
	        if (PlayerHandler.players[j] != null) {
	            Player c = (Player) PlayerHandler.players[j];
	            c.getPA().object(id, o.objectX, o.objectY, face, o.type);
	        }
	    }
	}
	public void updateObject(Object o) {
	    for (int j = 0; j < PlayerHandler.players.length; j++) {
	        if (PlayerHandler.players[j] != null) {
	            Player c = (Player) PlayerHandler.players[j];
	            c.getPA().object(o.objectId, o.objectX, o.objectY, o.face, o.type);
	        }
	    }
	}

	public void refreshObjects(Player c) {
	    if (c == null)
	        return;

	    for (Object o : objects) {
	        if (loadForPlayer(o, c)) {
	            // Re-send object to player
	            c.getPA().object(o.objectId, o.objectX, o.objectY, o.face, o.type);
	        }
	    }

	}	
	public void updateObjectForAll(WorldObject o) {
	    for (Player p : PlayerHandler.players) {
	        if (p == null) continue;
	        
	        // Check if player is within viewing distance (usually 60 tiles)
	        if (p.distanceToPoint(o.x, o.y) <= 60) {
	            // Re-send the specific object ID to its coordinates
	            p.getPA().checkObjectSpawn(o.id, o.x, o.y, o.face, o.type);
	        }
	    }
	}
	public void placeObject(Object o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = (Player) PlayerHandler.players[j];
				if (c.distanceToPoint(o.objectX, o.objectY) <= 60)
					c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
							o.type);
			}
		}
	}
	public void placeObject(WorldObject o) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = (Player) PlayerHandler.players[j];
					c.getPA().object(o.id, o.x, o.y, o.face,
							o.type);
			}
		}
	}
	public void addObject(Object o) {
	    // Ensure we are checking the EXACT tile before adding
	    Object existing = getObject(o.objectX, o.objectY, o.height);
	    if (existing != null) {
	        // If we found a door at this spot, we MUST remove it from the list 
	        // to prevent the process() loop from double-processing it.
	        objects.remove(existing); 
	    }
	    objects.add(o);
	    placeObject(o);
	}
	public void addObject(WorldObject o) {
	    // Ensure we are checking the EXACT tile before adding
	    Object existing = getObject(o.x, o.y, o.height);
	    if (existing != null && existing.type == o.type) {
	        // If we found a door at this spot, we MUST remove it from the list 
	        // to prevent the process() loop from double-processing it.
	        objects.remove(existing); 
	    }
		if (getObject(o.x, o.y, o.height) == null ) {
			object.add(o);
			placeObject(o);
		}
	}
	public Object getObject(int x, int y, int height) {
		for (Object o : objects) {
			if (o.objectX == x && o.objectY == y && o.height == height)
				return o;
		}
		return null;
	}

	public Object getObject(int id, int x, int y, int height) {
		for (Object o : objects) {
			if (o.objectId == id && o.objectX == x && o.objectY == y && o.height == height)
				return o;
		}
		return null;
	}
	public Object getObject(int id, int x, int y, int height, int type) {
		for (Object o : objects) {
			if (o.objectId == id && o.objectX == x && o.objectY == y && o.height == height && o.type == type)
				return o;
		}
		return null;
	}
	public void loadObjects(Player c) {
		if (c == null)
			return;
		for (Object o : objects) {
			if (loadForPlayer(o, c))
				c.getPA().object(o.objectId, o.objectX, o.objectY, o.face,
						o.type);
		}
		for (WorldObject o : object) {
			if (loadForPlayer(o, c))
				c.getPA().object(o.id, o.x, o.y, o.face,
						o.type);
		}
	}

	public final int IN_USE_ID = 14825;

	public boolean isObelisk(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return true;
		}
		return false;
	}

	public int[] obeliskIds = { 14829, 14830, 14827, 14828, 14826, 14831 };
	public int[][] obeliskCoords = { { 3154, 3618 }, { 3225, 3665 },
			{ 3033, 3730 }, { 3104, 3792 }, { 2978, 3864 }, { 3305, 3914 } };
	public boolean[] activated = { false, false, false, false, false, false };

	public void startObelisk(int obeliskId) {
		int index = getObeliskIndex(obeliskId);
		if (index >= 0) {
			if (!activated[index]) {
				activated[index] = true;
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1], 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0],
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16));
				addObject(new Object(14825, obeliskCoords[index][0] + 4,
						obeliskCoords[index][1] + 4, 0, -1, 10, obeliskId, 16));
			}
		}
	}

	public int getObeliskIndex(int id) {
		for (int j = 0; j < obeliskIds.length; j++) {
			if (obeliskIds[j] == id)
				return j;
		}
		return -1;
	}

	public void teleportObelisk(int port) {
		int random = Misc.random(5);
		while (random == port) {
			random = Misc.random(5);
		}
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = (Player) PlayerHandler.players[j];
				int xOffset = c.getX() - obeliskCoords[port][0];
				int yOffset = c.getY() - obeliskCoords[port][1];
				if (c.goodDistance(c.getX(), c.getY(),
						obeliskCoords[port][0] + 2, obeliskCoords[port][1] + 2,
						1)) {
					c.getPA().startTeleport2(
							obeliskCoords[random][0] + xOffset,
							obeliskCoords[random][1] + yOffset, 0);
				}
			}
		}
	}

	public boolean loadForPlayer(Object o, Player c) {
		if (o == null || c == null)
			return false;
		return c.distanceToPoint(o.objectX, o.objectY) <= 60
				&& c.getHeight() == o.height;
	}
	public boolean loadForPlayer(WorldObject o, Player c) {
		if (o == null || c == null)
			return false;
		return c.distanceToPoint(o.x, o.y) <= 60
				&& c.getHeight() == o.height;
	}

}