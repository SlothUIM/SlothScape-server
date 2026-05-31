package server.model.objects;

import server.Server;
import server.world.World;
import server.clip.Region;
import server.clip.WorldObject;

public class Object {

	public int objectId;
	public int objectX;
	public int objectY;
	public int height;
	public int face;
	public int type;
	public int newId;
	public int tick;

	public Object(int id, int x, int y, int height, int face, int type,
			int newId, int ticks) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = newId;
		this.tick = ticks;
	    World.getWorld().objectManager.addObject(this);
	}
	public int closedFace;
	public Object(int id, int x, int y, int height, int face, int type, int newId, int ticks, int closedFace) {
	    this.objectId = id;
	    this.objectX = x;
	    this.objectY = y;
	    this.height = height;
	    this.face = face;
	    this.type = type;
	    this.newId = newId;
	    this.tick = ticks;
	    this.closedFace = closedFace;
	    WorldObject worldObject = new WorldObject(objectId, objectX, objectY, height, type, face);
	    Region.addWorldObject(worldObject);
	    //World.getWorld().objectManager.addObject(this);
	}

	public Object(int id, int x, int y, int height, int face, int type) {
		this.objectId = id;
		this.objectX = x;
		this.objectY = y;
		this.height = height;
		this.face = face;
		this.type = type;
		this.newId = id+1;
		this.tick = 120;
	    WorldObject worldObject = new WorldObject(objectId, objectX, objectY, height, type, face);
	    Region.addWorldObject(worldObject);
	}

	public int getX() {
		// TODO Auto-generated method stub
		return this.objectX;
	}
	public int getY() {
		// TODO Auto-generated method stub
		return this.objectY;
	}
	public int getH() {
		// TODO Auto-generated method stub
		return this.height;
	}
	public int getId() {
		// TODO Auto-generated method stub
		return this.objectId;
	}
}