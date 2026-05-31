package server.model.players.skills.construction.util;
public class Location {

	private final int absX;
	private final int absY;
	private final int plane;
	
	public Location(int absX, int absY) {
		this.absX = absX;
		this.absY = absY;
		this.plane = 0;
	}
	public Location(int absX, int absY, int plane)
	{
		this.absX = absX;
		this.absY = absY;
		this.plane = plane;
	}

	public static Location create(int absX, int absY) {
		return new Location(absX, absY);
	}

	public int getX() {
		return this.absX;
	}

	public int getY() {
		return this.absY;
	}
	public int getPlane()
	{
		return this.plane;
	}

}
