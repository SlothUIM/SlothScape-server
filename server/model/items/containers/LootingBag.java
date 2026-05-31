package server.model.items.containers;

import server.model.players.Player;

public class LootingBag {

	public static boolean isLootingBag(Player c, int itemId) {
		// TODO Auto-generated method stub
		if(itemId == 11941 || itemId == 11942)
			return true;
		else
			return false;
	}

}
