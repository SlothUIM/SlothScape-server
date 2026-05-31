package server.model.players.packets;

import server.model.players.Client;
import server.model.players.PacketType;
	
	public class WaterSources {
	
	private Client c;
			public int Wells[] = {26945};
			public int Pumps[] = {11661};
			public int Sinks[] = {874};
			public int fountains[] = {24265, 24214, 24161,879};
			public int containers[] = {229, 1925, 3732,3734,5331, 5332, 5333, 5336, 5337, 5338, 5339, 5340, 7668, 7728};
			
			public void getFromWaterSource(int itemId, int itemId2, int objectId, boolean vial) {
			if(c.getItems().playerHasItem(itemId, 1)) {
				if(objectId == Wells[objectId] && !vial){
					c.getItems().deleteItem(itemId, 1);
					c.getItems().addItem(itemId2, 1);
				} else if(objectId == Wells[objectId] && vial){
					c.sendMessage("You couldn't get it back if you tried!");
				}
				}
		}
	}