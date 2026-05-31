package server.model.players.skills.construction.util;

import server.model.players.skills.construction.Room;
/**
 * 
 * @author Owner Blade
 *
 */
public class RoomData {

	private int rotation, type, theme;
	private int x, y;
	private boolean[] doors;
	public RoomData(int rotation, int type, int theme)
	{
		this.rotation = rotation;
		this.type = type;
		this.theme = theme;
		getVarData();
	}
	private void getVarData()
	{
		Room rd = Room.forID(type);
		x = rd.getX();//use -64 or +64 to switch styles
		y = rd.getY();
		doors = rd.getRotatedDoors(rotation);
	}
	public boolean[] getDoors()
	{
		return doors;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public int getStyle()
	{
		return theme;
	}
	public int getType()
	{
		return type;
	}
	public int getRotation()
	{
		return rotation;
	}
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	public void setStyle(int newStyle) {
		this.theme = newStyle;
		
	}
}