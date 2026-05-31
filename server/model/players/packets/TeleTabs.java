package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;
	
	public class TeleTabs {
	
	private Player c;
	
		public TeleTabs(Player client){
			this.c = client;
		}
			public int tablets[] = {8007,8008,8009,8010,8011,8012,8013};
			public int locations[][] ={
			{3212,3423},//varrock
			{3227,3219},//lumbridge
			{3212,3423},//falador
			{3212,3423},//camelot
			{3212,3423},//ardougne
			{3212,3423},//watchtower
			{3212,3423}//POH
			};
			
		public void breakTablet(int itemId) {
			for(int i = 0; i < tablets.length; i++) {
				if(itemId == tablets[i]) {
					c.getItems().deleteItem(itemId, 1);
					c.getPA().startTeleport(locations[i][0], locations[i][1], 0, "teleTab");
				}
			}
		}
	}