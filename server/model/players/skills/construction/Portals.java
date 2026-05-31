package server.model.players.skills.construction;
import java.util.Iterator;

import server.Config;
import server.model.players.Player;
import server.model.players.skills.construction.util.Location;
import server.model.players.skills.construction.util.Portal;

public enum Portals
	{
		VARROCK(1, new Location(Config.VARROCK_X, Config.VARROCK_Y), 25,
				new int[][] {{563, 100}, {554, 100/*fire*/}, {556, 300 /*Air*/}}, new int[] {13615, 13622, 13629}),
		LUMBRIDGE(2, new Location(Config.LUMBY_X, Config.LUMBY_Y), 31,
				new int[][] {{563, 100}, {557, 100/*EARTH*/}, {556, 300}}, new int[] {13616, 13623, 13630}),
		FALADOR(3, new Location(Config.FALADOR_X, Config.FALADOR_Y), 37,
				new int[][] {{563, 100}, {555, 100/*water*/}, {556, 300}}, new int[] {13617, 13624, 13631}),
		CAMELOT(4, new Location(Config.CAMELOT_X, Config.CAMELOT_Y), 45,
				new int[][] {{563, 200}, {556, 500}}, new int[] {13618, 13625, 13632}),
		ARDOUGNE(5, new Location(Config.ARDOUGNE_X, Config.ARDOUGNE_Y), 51,
				new int[][] {{563, 200}, {555, 200}}, new int[] {13619, 13626, 13633}),
		YANILLE(6, new Location(Config.ARDOUGNE_X, Config.ARDOUGNE_Y), 58,
				new int[][] {{563, 200}, {557, 200}}, new int[] {13620, 13627, 13634}),
		KHARYLL(7, new Location(Config.KHARYRLL_X, Config.KHARYRLL_Y), 66,
				new int[][] {{563, 200}, {565, 100}}, new int[] {13621, 13628, 13635}),
		EMPTY(-1, null, -1, null, null),
				;
		private Location destination;
		private int[][] requiredItems;
		private int[] objects;
		private int magicLevel, type;
		private Portals(int id, Location destination, int magicLevel, int[][] requiredItems, int[] objects)
		{
			this.type = id;
			this.destination = destination;
			this.requiredItems = requiredItems;
			this.objects = objects;
			this.magicLevel = magicLevel;
		}
		public Location getDestination()
		{
			return destination;
		}
		public static Portals forType(int type)
		{
			for(Portals p : values())
				if(p.type == type)
					return p;
			return null;
		}
		public static Portals forObjectId(int objectId)
		{
			for(Portals p : values())
			{
				for(int i : p.objects)
					if(i == objectId)
						return p;
			}
			return null;
		}
		public int[] getObjects()
		{
			return objects;
		}
		public String canBuild(Player p)
		{
			if(requiredItems == null)
			{
				boolean found = false;
				int[] myTiles = Construction.getMyChunk(p);
				Iterator<Portal> it = p.portals.iterator();
				while(it.hasNext())
				{
					Portal portal = it.next();
					if(portal.getRoomX() == myTiles[0]-1
							&& portal.getRoomY() == myTiles[1]-1
							&& portal.getRoomZ() == (p.inDungeon() ? 4 : p.getHeight())
							&& portal.getId() == p.portalSelected)
					{
						it.remove();
						found = true;
						break;
					}
				}
				if(!found)
				{
					p.getPA().closeAllWindows();
					return "Can't remove that was doesn't exist."; 
				} else {
					p.getPA().removeObjects(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
					Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
					p.getPA().closeAllWindows();
					return null;
				}
			}
			for(int i = 0; i < requiredItems.length; i++)
			{
				if(!p.getItems().playerHasItem(requiredItems[i][0], requiredItems[i][1]))
					return "You don't have the required items to build this.";
			}
			if(p.playerLevel[6] < magicLevel)
				return "You need a magic level of "+magicLevel+" to build this";
			build(p);
			return null;
		}
		public void build(Player p)
		{
			for(int i = 0; i < requiredItems.length; i++)
			{
				p.getItems().deleteItem(requiredItems[i][0], requiredItems[i][1]);
			}
			int[] myTiles = Construction.getMyChunk(p);
			boolean found = false;
			for(Portal portal : p.portals)
			{
				if(portal.getRoomX() == myTiles[0]-1
						&& portal.getRoomY() == myTiles[1]-1
						&& portal.getRoomZ() == (p.inDungeon() ? 4 : p.getHeight())
						&& portal.getId() == p.portalSelected)
				{
					portal.setType(type);
					found = true;
					System.out.println(portal.getRoomX()+"/"+(myTiles[0]-1)+" "+portal.getId()+"/"+p.portalSelected);
				}
			}
			if(!found)
			{
				Portal portal = new Portal();
				portal.setId(p.portalSelected);
				portal.setRoomX(myTiles[0] - 1);
				portal.setRoomY(myTiles[1] - 1);
				portal.setRoomZ(p.inDungeon() ? 4 : p.getHeight());
				portal.setType(type);
				p.portals.add(portal);
			}
			p.getPA().removeObjects(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
			Construction.placeAllFurniture(p, myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
			p.getPA().closeAllWindows();
		}
	}