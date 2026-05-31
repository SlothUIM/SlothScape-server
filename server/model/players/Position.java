package server.model.players;

/**
 * Position
 * @author Andrew (I'm A Boss on Rune-Server, Mr Extremez on Moparscape & Runelocus)
 */

public class Position {
	
	public static boolean checkPosition(Player c, int x, int y, int h) {
		return c.getX() == x && c.getY() == y && c.getHeight() == h;
	}
	
	public static boolean checkPlayerX(Player client, int x, int h) {
		return client.getX() == x && client.getHeight() == h;
	}
	
	public static boolean checkPlayerY(Player player, int y, int h) {
		return player.getY() == y && player.getHeight() == h;
	}
	
	public static boolean checkPlayerH(Player client, int h) {
		return client.getHeight() == h;
	}
	
	public static boolean checkObject(Player client, int x, int y, int h) {
		return client.objectX == x && client.objectY == y && client.getHeight() == h;
	}
	
	public static boolean checkObjectX(Player client, int x, int h) {
		return client.objectX == x && client.getHeight() == h;
	}
	
	public static boolean checkObjectY(Player client, int y, int h) {
		return client.objectY == y && client.getHeight() == h;
	}
}
