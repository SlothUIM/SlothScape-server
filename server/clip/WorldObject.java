package server.clip;

import java.util.Objects;

import lombok.Data;
import lombok.Getter;
import server.Server;
import server.world.Location;
import server.world.World;
import server.world.objects.GlobalObject;

@Data
public class WorldObject {

	@Getter
	public int x, y, height, id, type, face, ticks, newId;

	public WorldObject(int id, int x, int y, int height) {
	    this.id = id;
	    this.x = x;
	    this.y = y;
	    this.height = height;
	}


	public WorldObject(int id, int x, int y, int height, int face) {
		this(id, x, y, height);
		this.face = face;
		this.type = 10;
	}

	public WorldObject(int id, int x, int y, int height, int type, int face) {
		this(id, x, y, height);
		this.face = face;
		this.type = type;
	}
	
	public WorldObject(int id, int x, int y, int height, int type, int face, int ticks) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.height = height;
        this.type = type;
        this.face = face;
        this.ticks = ticks;
    }	
	public WorldObject(int id, int x, int y, int height, int type, int face, int ticks, int newId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.height = height;
        this.type = type;
        this.face = face;
        this.ticks = ticks;
	    this.newId = newId;
		World.getWorld().objectManager.addObject(this);
    }
	public Location getLocation() {
		return Location.of(x, y, height);
	}
	
	public WorldObject setId(int id) {
	    this.id = id;
	    return this;
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null || getClass() != obj.getClass()) return false;
	    WorldObject other = (WorldObject) obj;
	    return id == other.id &&
	           x == other.x &&
	           y == other.y &&
	           height == other.height &&
	           type == other.type &&
	           face == other.face;
	}

	@Override
	public int hashCode() {
	    return Objects.hash(id, x, y, height, type, face);
	}


	public WorldObject copy() {
		WorldObject worldObject = new WorldObject(id, x, y, height, type, face);
		return worldObject;
	}
	
	public WorldObject copy(Location newLocation) {
		WorldObject worldObject = new WorldObject(id, newLocation.getX(), newLocation.getY(), newLocation.getZ(), type, face);
		return worldObject;
	}

	public GlobalObject toGlobalObject() {
		return new GlobalObject(id, x, y, height, face, type);
	}
	

}