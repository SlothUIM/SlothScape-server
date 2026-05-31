package server.model.players.skills.construction;

import server.event.Events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import server.Server;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.event.EventHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.skills.construction.Servant;
import server.model.players.skills.construction.House;
import server.model.players.skills.construction.HouseDungeon;
import server.model.players.MapInstance.RegionInstanceType;
import server.model.players.Player;
//import server.model.npcs.Butlers;
import server.model.players.skills.construction.furniture.*;
import server.model.players.skills.construction.util.HouseFurniture;
import server.model.players.skills.construction.util.POHPalette;
import server.model.players.skills.construction.util.Portal;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.skills.construction.util.POHPalette.POHPaletteTile;
import server.util.Database;
import server.util.Misc;
import server.world.World;

public class Construction {
	public static boolean enteringHouse = false;;
	public static HouseFurniture findNearestPortal(Player p) {
		Player owner = p.mapInstance.getOwner();
		for (HouseFurniture pf : owner.houseFurniture) {
			if (pf.getFurnitureId() != 4525)
				continue;
			if (pf.getRoomZ() != 0)
				continue;
				System.out.println("Portal found for "+pf.getFurnitureId());
			return pf;
		}
		return null;
	}
	public static void rotateRoom(int wise, Player p)
	{
		if (p.getMapInstance() == null || p.getMapInstance().getOwner() != p)
			return;
		int[] myTiles = getMyChunk(p);
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		RoomData r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][chunkX][chunkY];
		Room rd = Room.forID(r.getType());
		int toRot = (wise == 0 ? Room.getNextEligibleRotationClockWise(rd, direction, r.getRotation()) :
			Room.getNextEligibleRotationCounterClockWise(rd, direction, r.getRotation()));

		// Change this:
		p.getPA().removeObjects(chunkX, chunkY, p.getLocation().getZ());

		// To this (clearing the full 8x8 area):
		int baseTileX = chunkX * 8;
		int baseTileY = chunkY * 8;
		for(int x = 0; x < 8; x++) {
		    for(int y = 0; y < 8; y++) {
		        p.getPA().checkObjectSpawn(-1, baseTileX + x, baseTileY + y, 0, 10);
		    }
		}
		POHPaletteTile tile = new POHPaletteTile(rd.getX(), rd.getY(), 0, toRot);
		House house = p.getMapInstance() instanceof House ? (House) p.getMapInstance() : ((HouseDungeon)p.getMapInstance()).getHouse();
		if (p.inDungeon()) {
			house.getSecondaryPalette().setTile(chunkX, chunkY, 0, tile);
		} else {
			house.getPalette().setTile(chunkX, chunkY, p.getLocation().getZ(), tile);
		}
		p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][chunkX][chunkY].setRotation(toRot);

		p.setConstructionCoords(new int[] { p.getX(), p.getY() });
		p.getMapInstance().destroy();
		Construction.createPalette(p);
		placeAllFurniture(p, chunkX, chunkY, p.inDungeon() ? 4 : p.getHeight());
		p.getPA().closeAllWindows();

	    saveHouse(p);
		
	}
	public static void loadFullHouse(Player player) {
	    String name = player.playerName.toLowerCase();

	    // 1. Reset current house state to avoid ghost objects
	    for (int z = 0; z < 5; z++) {
	        for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
	            for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
	                player.Rooms[z][x][y] = null;
	            }
	        }
	    }
	    player.houseFurniture.clear();
	    player.portals.clear();

	    try (Connection conn = Database.getConnection()) {
	        
	        // --- A. LOAD SETTINGS ---
	    	// --- A. LOAD SETTINGS ---
	        String settingsSql = "SELECT * FROM house_settings WHERE username = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(settingsSql)) {
	            pstmt.setString(1, name);
	            ResultSet rs = pstmt.executeQuery();
	            if (rs.next()) {
	                player.houseType = rs.getInt("style");
	                player.teleportInsidePOH = rs.getInt("teleportInside") == 1;
	                player.defaultBuildMode = rs.getInt("buildMode") == 1;
	                player.setPOHDoor(rs.getInt("doorStatus"));
	                
	                // NEW: Load the location string and parse it back into the Enum
	                try {
	                	player.houseLocation = HouseLocation.valueOf(rs.getString("location"));
	                } catch (Exception e) {
	                	// Fallback in case the column is null or corrupted
	                	player.houseLocation = HouseLocation.RIMMINGTON;
	                }
	            }
	        }

	        // --- B. LOAD ROOMS ---
	        String roomSql = "SELECT x, y, z, rotation, type FROM poh_rooms WHERE username = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(roomSql)) {
	            pstmt.setString(1, name);
	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                int x = rs.getInt("x");
	                int y = rs.getInt("y");
	                int z = rs.getInt("z");
	                player.Rooms[z][x][y] = new RoomData(rs.getInt("rotation"), rs.getInt("type"), player.getHouseStyle());
	            }
	        }

	     // --- C. LOAD FURNITURE ---
	        String furnSql = "SELECT roomX, roomY, roomZ, hotspot, furnitureId, xOff, yOff FROM poh_furniture WHERE username = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(furnSql)) {
	            pstmt.setString(1, name);
	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                // Constructor Order: x, y, z, hotspot, furniture, xOff, yOff
	                HouseFurniture pf = new HouseFurniture(
	                    rs.getInt("roomX"),
	                    rs.getInt("roomY"),
	                    rs.getInt("roomZ"),
	                    rs.getInt("hotspot"),
	                    rs.getInt("furnitureId"),
	                    rs.getInt("xOff"),
	                    rs.getInt("yOff")
	                );
	                player.houseFurniture.add(pf);
	            }
	        }

	     // --- D. LOAD PORTALS ---
	        String portalSql = "SELECT id, x, y, z, type FROM poh_portals WHERE username = ?";
	        try (PreparedStatement pstmt = conn.prepareStatement(portalSql)) {
	            pstmt.setString(1, name);
	            ResultSet rs = pstmt.executeQuery();
	            while (rs.next()) {
	                // Match your old logic: Create object then use setters
	                Portal portal = new Portal();
	                portal.setId(rs.getInt("id"));
	                portal.setRoomX(rs.getInt("x"));
	                portal.setRoomY(rs.getInt("y"));
	                portal.setRoomZ(rs.getInt("z"));
	                portal.setType(rs.getInt("type"));
	                
	                player.portals.add(portal);
	            }
	        }
	        
	        System.out.println("[Database] Construction data fully loaded for: " + name);

	    } catch (SQLException e) {
	        System.err.println("[Database] Error loading house for " + name);
	        e.printStackTrace();
	    }
	}
	public static void saveHouse(Player player) {
	    String name = player.playerName.toLowerCase();

	    Database.SAVE_SERVICE.execute(() -> {
	        try (Connection conn = Database.getConnection()) {
	            conn.setAutoCommit(false); // Optimization for batch saving

	            // 1. Save Basic Settings
	         // 1. Save Basic Settings
	            // NOTE: We added a 6th parameter (?) for the location!
	            String setSql = "INSERT OR REPLACE INTO house_settings VALUES (?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(setSql)) {
	                pstmt.setString(1, name);
	                pstmt.setInt(2, player.houseType);
	                pstmt.setInt(3, player.teleportInsidePOH ? 1 : 0);
	                pstmt.setInt(4, player.defaultBuildMode ? 1 : 0);
	                pstmt.setInt(5, player.getPOHDoor());
	                
	                // NEW: Save the location name (e.g., "RIMMINGTON")
	                pstmt.setString(6, player.houseLocation != null ? player.houseLocation.name() : "RIMMINGTON"); 
	                
	                pstmt.executeUpdate();
	            }

	            // 2. Clear and Save Rooms
	            conn.createStatement().execute("DELETE FROM poh_rooms WHERE username = '" + name + "'");
	            String roomSql = "INSERT INTO poh_rooms VALUES (?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(roomSql)) {
	                for (int z = 0; z < 5; z++) {
	                    for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
	                        for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
	                            if (player.Rooms[z][x][y] == null) continue;
	                            pstmt.setString(1, name);
	                            pstmt.setInt(2, x); 
	                            pstmt.setInt(3, y); 
	                            pstmt.setInt(4, z);
	                            pstmt.setInt(5, player.Rooms[z][x][y].getRotation());
	                            pstmt.setInt(6, player.Rooms[z][x][y].getType());
	                            pstmt.addBatch();
	                            if (player.Rooms[z][x][y] != null) {
	                                //System.out.println("SQL SAVING ROOM: " + player.Rooms[z][x][y].getType() + " at " + x + "," + y);
	                            }
	                        }
	                    }
	                }
	                pstmt.executeBatch();
	            }

	         // 3. Clear and Save Furniture
	            conn.createStatement().execute("DELETE FROM poh_furniture WHERE username = '" + name + "'");
	            // Table structure: (username, roomX, roomY, roomZ, hotspot, furnitureId, xOff, yOff)
	            String furnSql = "INSERT INTO poh_furniture VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(furnSql)) {
	                for (HouseFurniture pf : player.houseFurniture) {
	                    pstmt.setString(1, name);
	                    pstmt.setInt(2, pf.getRoomX()); 
	                    pstmt.setInt(3, pf.getRoomY()); 
	                    pstmt.setInt(4, pf.getRoomZ());
	                    pstmt.setInt(5, pf.getHotSpotId());    // index 5
	                    pstmt.setInt(6, pf.getFurnitureId());  // index 6
	                    pstmt.setInt(7, pf.getStandardXOff()); // index 7
	                    pstmt.setInt(8, pf.getStandardYOff()); // index 8
	                    pstmt.addBatch();
	                }
	                pstmt.executeBatch();
	            }

	         // --- 4. Clear and Save Portals ---
	            conn.createStatement().execute("DELETE FROM poh_portals WHERE username = '" + name + "'");
	            String portSql = "INSERT INTO poh_portals (username, id, x, y, z, type) VALUES (?, ?, ?, ?, ?, ?)";
	            try (PreparedStatement pstmt = conn.prepareStatement(portSql)) {
	                int savedCount = 0;
	                for (Portal portal : player.portals) {
	                    if (portal == null) continue;
	                    
	                    pstmt.setString(1, name);
	                    pstmt.setInt(2, portal.getId());
	                    pstmt.setInt(3, portal.getRoomX());
	                    pstmt.setInt(4, portal.getRoomY());
	                    pstmt.setInt(5, portal.getRoomZ());
	                    pstmt.setInt(6, portal.getType());
	                    pstmt.addBatch();
	                    savedCount++;
	                }
	                pstmt.executeBatch();
	                // System.out.println("Saved " + savedCount + " portals for " + name);
	            }

	            conn.commit(); // Finalize all changes at once
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    });
	    
	}
	public static void placeNPCs(Player p)
	{

		if(p.houseServant > 0) {
			HouseFurniture portal = findNearestPortal(p);
			int toX = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
			int toY = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);
			Servant npc = NPCHandler.spawnNpc(p, p.houseServant, toX+3, toY+1, p.getHeight());
			((House)p.getMapInstance()).addNPC(npc);
		}
		if(p.inBuildingMode)
		{
			return;
		}
		for(HouseFurniture pf : p.houseFurniture)
		{
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			int npcId = ConstructionData.getGuardId(f.getFurnitureId());
			if(pf.getRoomZ() != 4)
				continue;
			if(npcId == -1)
				continue;
			RoomData room = p.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			NPC npc = World.getWorld().npcHandler.spawnNpc(p, npcId, actualX, actualY, 0, 1, 100, 0, 0, 0, false, false);
			((House)p.getMapInstance()).getDungeon().addNPC(npc);
			
		}
	} 	
	public static void sendServant(final Player p, int amount)
	{
		p.servantCharges--;
		p.getPA().closeAllWindows();
		final Servant servant = (p.getMapInstance() instanceof House ? (House) p.getMapInstance() : ((HouseDungeon)p.getMapInstance()).getHouse()).getButler();
		servant.setFetching(true);
		servant.mapInstance = null;
		servant.takeItemsFromBank(p, p.servantItemFetchId, amount);
		/*EventManager.getSingleton().addEvent(new Event() {
				@Override
				public void execute(EventContainer container) {
					servant.mapInstance = p.mapInstance;
					if(servant.freeSlots() == servant.getInventory().length-1)
					p.sendMessage("Could not locate item");
						//p.getDH().sendNpcChat2(p, p.talkingNpc, "Servant", DialogueMethods.DEPRESSED, "I'm sorry but i could not find this item on thou bank.");
					servant.giveItems(p);
					servant.setFetching(false);
					container.stop();
					
				}
			}, (int)Butlers.forId(servant.npcType).getTripSeconds());*/
	}
	public static void placeAllFurniture(Player p, int x, int y, int z) {
		Player owner = p.getMapInstance().getOwner();
		// First, iterate through the rooms to handle door visibility
	    
		for (HouseFurniture pf : owner.getHouseFurniture()) {
			if (pf.getRoomZ() != z)
				continue;
			if(pf.getRoomX() != x || pf.getRoomY() != y)
				continue;
			
			RoomData room = owner.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(),
					pf.getStandardXOff(), pf.getStandardYOff(), room);
			if (hs == null)
				System.out.println("Hotspot is null");

			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(
					pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
				HouseData.doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), p, false, z);
			
				
			//System.out.println("placeAllFurniture(Player p, int x, int y, int z)");
		}
	}
	public static boolean isHotspot(int id) {
	    for (HotSpots hs : HotSpots.values()) {
	        if (hs.getObjectId() == id) {
	            return true;
	        }
	    }
	    return false;
	}
	public static void placeAllFurniture(Player p, int heightLevel) {
		Player owner = p.getMapInstance().getOwner();
		
		for (HouseFurniture pf : p.getHouseFurniture()) {
				if (pf.getRoomZ() != heightLevel) {
					continue;
					
				}
			RoomData room = owner.getHouseRooms()[pf.getRoomZ()][pf.getRoomX()][pf.getRoomY()];
			if (room == null){
			return;
			}
			
			HotSpots hs = HotSpots.forHotSpotIdAndCoords(pf.getHotSpotId(), pf.getStandardXOff(), pf.getStandardYOff(), room);
	        
			if (hs == null){
				return;
				}	
			int actualX = ConstructionData.BASE_X + (pf.getRoomX() + 1) * 8;
			actualX += ConstructionData.getXOffsetForObjectId(pf.getFurnitureId(), hs, room.getRotation());
			int actualY = ConstructionData.BASE_Y + (pf.getRoomY() + 1) * 8;
			actualY += ConstructionData.getYOffsetForObjectId(pf.getFurnitureId(), hs, room.getRotation());
			Furniture f = Furniture.forFurnitureId(pf.getFurnitureId());
			ArrayList<HotSpots> hsses = HotSpots.forObjectId_3(f.getHotSpotId());
			//hsses.clear();
			//hsses.removeAll(hsses);
			HouseData.doFurniturePlace(hs, f, hsses, getMyChunkFor(actualX, actualY),
					actualX, actualY, room.getRotation(), owner, false, heightLevel);
			for (int x2 = 0; x2 < HouseData.MAX_DIMENSION; x2++) {
		        for (int y2 = 0; y2 < HouseData.MAX_DIMENSION; y2++) {
		            RoomData room2 = owner.getHouseRooms()[heightLevel][x2][y2];
		            if(room2 == null)
		            	System.out.println("room2 is null");
		            if (room2 != null && room2.getType() != ConstructionData.EMPTY) {
		                handleDoorVisibility(p, room2, x2, y2, heightLevel);
		            }
		        }
		    }
			}
	}
	public static void updatePalette(Player p) {
		POHPalette palette = p.mapInstance.getPalette();
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
				for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
					if (p.getHouseRooms()[z][y][x] == null)
						continue;
					if (p.getHouseRooms()[z][y][x].getX() == 0)
						continue;
					POHPaletteTile tile = new POHPaletteTile(
							p.getHouseRooms()[z][y][x].getX(),
							p.getHouseRooms()[z][y][x].getY(),
							p.getHouseRooms()[z][y][x].getStyle(),
							p.getHouseRooms()[z][y][x].getRotation());
					palette.setTile(x, y, z, tile);
				}
			}
		}
	}
	public static int[] getMyChunk(Player p) {
		for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
			for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (p.getX() >= minX && p.getX() <= maxX && p.getY() >= minY
						&& p.getY() <= maxY) {
					return new int[] { x, y };
				}
			}
		}
		return null;
	}

	public static int[] getConvertedCoords(int tileX, int tileY, int[] myTiles,
			RoomData room) {
		int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
		actualX += ConstructionData.getXOffsetForObjectId(1, tileX, tileY,
				room.getRotation(), 0);
		int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
		actualY += ConstructionData.getYOffsetForObjectId(1, tileX, tileY,
				room.getRotation(), 0);
		return new int[] { actualX, actualY };
	}
	public static int[] getMyChunkFor(int xx, int yy) {
		for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
			for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
				int minX = ((ConstructionData.BASE_X) + (x * 8));
				int maxX = ((ConstructionData.BASE_X + 7) + (x * 8));
				int minY = ((ConstructionData.BASE_Y) + (y * 8));
				int maxY = ((ConstructionData.BASE_Y + 7) + (y * 8));
				if (xx >= minX && xx <= maxX && yy >= minY && yy <= maxY) {
					return new int[] { x, y };
				}
			}
		}
		return null;
	}

	public static int getXTilesOnTile(int[] tile, Player p) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return p.getX() - baseX;
	}

	public static int getYTilesOnTile(int[] tile, Player p) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return p.getY() - baseY;
	}

	public static int getXTilesOnTile(int[] tile, int myX) {
		int baseX = ConstructionData.BASE_X + (tile[0] * 8);
		return myX - baseX;
	}

	public static int getYTilesOnTile(int[] tile, int myY) {
		int baseY = ConstructionData.BASE_Y + (tile[1] * 8);
		return myY - baseY;
	}
	public static boolean buildingHouse(Player player) {
		return player.getMapInstance() != null
				&& player.getMapInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE
				&& player.isBuildingMode();
	}
	public static void updateHouseStyle(Player p, int newStyle) {
	    p.setHouseStyle(newStyle);
	    for (int z = 0; z < 4; z++) {
	        for (int x = 0; x < HouseData.MAX_DIMENSION; x++) {
	            for (int y = 0; y < HouseData.MAX_DIMENSION; y++) {
	                if (p.getHouseRooms()[z][x][y] != null) {
	                    p.getHouseRooms()[z][x][y].setStyle(newStyle);
	                }
	            }
	        }
	    }
	    //createPalette(p); // Rebuild the house with the new source X and Height
	   // saveHouse(p);
	}
	public static void deleteRoom(Player p, int toHeight) {
	    int[] myTiles = getMyChunk(p);
	    int xOnTile = getXTilesOnTile(myTiles, p);
	    int yOnTile = getYTilesOnTile(myTiles, p);
	    
	    int direction = 0;
	    final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3;
	    
	    if (xOnTile == 0) direction = LEFT;
	    if (yOnTile == 0) direction = DOWN;
	    if (xOnTile == 7) direction = RIGHT;
	    if (yOnTile == 7) direction = UP;

	    int xOff = 0, yOff = 0;
	    if (direction == LEFT) xOff = -1;
	    if (direction == DOWN) yOff = -1;
	    if (direction == RIGHT) xOff = 1;
	    if (direction == UP) yOff = 1;

	    int chunkX = (myTiles[0] - 1) + xOff;
	    int chunkY = (myTiles[1] - 1) + yOff;

	    // 1. Structural Support Check
	    // Check if there is a room on the plane directly above the one being deleted
	    if (toHeight < 2) { 
	        RoomData roomAbove = p.getMapInstance().getOwner().getHouseRooms()[toHeight + 1][chunkX][chunkY];
	        // If a room exists above (and it's not just a roof), prevent deletion
	        if (roomAbove != null && roomAbove.getType() != ConstructionData.EMPTY && roomAbove.getType() != ConstructionData.ROOF_SINGLE) {
	            p.sendMessage("You cannot delete this room; it is supporting a room on the floor above!");
	            p.getPA().closeAllWindows();
	            return;
	        }
	    }

	    // 2. Garden Requirement Check
	    RoomData r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][chunkX][chunkY];
	    if (r.getType() == ConstructionData.GARDEN || r.getType() == ConstructionData.FORMAL_GARDEN) {
	        int gardenAmt = 0;
	        for (int z = 0; z < 4; z++) {
	            for (int x = 0; x < 13; x++) {
	                for (int y = 0; y < 13; y++) {
	                    RoomData r1 = p.getMapInstance().getOwner().getHouseRooms()[z][x][y];
	                    if (r1 != null && (r1.getType() == ConstructionData.GARDEN || r1.getType() == ConstructionData.FORMAL_GARDEN)) {
	                        gardenAmt++;
	                    }
	                }
	            }
	        }
	        if (gardenAmt < 2) {
	            p.sendMessage("You need at least 1 garden or formal garden to keep your house accessible.");
	            p.getPA().closeAllWindows();
	            return;
	        }
	    }

	    // 3. Update Palette and Data
	    House house = p.getMapInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE
	            ? (House) p.getMapInstance()
	            : ((HouseDungeon) p.getMapInstance()).getHouse();

	    int roomType = p.inDungeon() ? ConstructionData.DUNGEON_EMPTY : ConstructionData.EMPTY;
	    POHPaletteTile emptyTile = new POHPaletteTile(0, roomType, 0, 0);

	    // Set current tile to empty
	    house.getPalette().setTile(chunkX, chunkY, toHeight, emptyTile);
	    p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][chunkX][chunkY] = new RoomData(0, roomType, 0);

	    // 4. THE ROOF FIX: Remove roof orphan on the plane above
	    if (toHeight < 3) {
	        RoomData roofData = p.getMapInstance().getOwner().getHouseRooms()[toHeight + 1][chunkX][chunkY];
	        if (roofData != null && roofData.getType() == ConstructionData.ROOF_SINGLE) {
	            house.getPalette().setTile(chunkX, chunkY, toHeight + 1, null);
	            p.getMapInstance().getOwner().getHouseRooms()[toHeight + 1][chunkX][chunkY] = null;
	        }
	    }

	    // 5. Cleanup Furniture and Portals
	    p.getHouseFurniture().removeIf(pf -> pf.getRoomX() == chunkX && pf.getRoomY() == chunkY && pf.getRoomZ() == toHeight);
	    
	    // Corrected Portal Iterator
	    Iterator<Portal> portals = p.getHousePortals().iterator();
	    while (portals.hasNext()) {
	        Portal port = portals.next();
	        if (port.getRoomX() == chunkX && port.getRoomY() == chunkY && port.getRoomZ() == toHeight) {
	            portals.remove(); 
	        }
	    }

	    // 6. Reload Map
	    p.setConstructionCoords(new int[] { p.getX(), p.getY() });
	    p.getMapInstance().destroy();
	    createPalette(p);

		saveHouse(p);
	    p.getPA().closeAllWindows();
	}
	public static void createRoom(int roomType, Player p, int toHeight) {
		Room rd = Room.forID(roomType);
		if (rd == null) {
			p.sendMessage("You can only build this room on the surface");
			return;
		}
		if (!p.getItems().playerHasItem(995, rd.getCost())) {
			p.sendMessage("You need " + rd.getCost() + " coins to build this");
			return;
		}
		boolean isDungeonRoom = ConstructionData.isDungeonRoom(roomType);
		if (!p.inDungeon()) {
			if (isDungeonRoom && toHeight != 15 && toHeight != 103) {
				p.sendMessage("You can only build this room in your dungeon.");
				return;
			}
		} else {
			if (!isDungeonRoom) {
				p.sendMessage("You can only build this room on the surface");
				return;
			}
		}
		int[] myTiles = getMyChunk(p);
		if(myTiles == null) {
			return;
			
		}
		int xOnTile = getXTilesOnTile(myTiles, p);
		int yOnTile = getYTilesOnTile(myTiles, p);
		int direction = 0;
		final int LEFT = 0, DOWN = 1, RIGHT = 2, UP = 3, SAME = 4;
		if (xOnTile == 0)
			direction = LEFT;
		if (yOnTile == 0)
			direction = DOWN;
		if (xOnTile == 7)
			direction = RIGHT;
		if (yOnTile == 7)
			direction = UP;
		int rotation = Room.getFirstElegibleRotation(rd,
				direction);
		if (toHeight == 100) {
			/** Create room from stair **/
			direction = SAME;
			toHeight = 1;
			rotation = p.getMapInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			int stairId = 0;
			for (HouseFurniture furn : p.getHouseFurniture()) {
				if (furn.getRoomX() == myTiles[0] - 1
						&& furn.getRoomY() == myTiles[1] - 1
						&& furn.getRoomZ() == 0) {
					if (furn.getStandardXOff() == 3
							&& furn.getStandardYOff() == 3) {
						stairId = furn.getFurnitureId() + 1;
					}
				}
			}
			HouseData.doFurniturePlace(HotSpots.SKILL_HALL_STAIRS_1,
					Furniture.forFurnitureId(stairId), null, myTiles,
					ConstructionData.BASE_X + (myTiles[0] * 8) + 3,
					ConstructionData.BASE_Y + (myTiles[1] * 8) + 3, rotation,
					p, false, 1);
			HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
					myTiles[1] - 1, 1, 37, stairId, 3, 3);
			p.houseFurniture.add(pf);
		}
		if (toHeight == 101) {
			direction = SAME;
			toHeight = 0;
			rotation = p.getMapInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			int stairId = 0;
			for (HouseFurniture furn : p.houseFurniture) {
				if (furn.getRoomX() == myTiles[0] - 1
						&& furn.getRoomY() == myTiles[1] - 1
						&& furn.getRoomZ() == 1) {
					if (furn.getStandardXOff() == 3
							&& furn.getStandardYOff() == 3) {
						stairId = furn.getFurnitureId() + 1;
					}
				}
			}
			HouseData.doFurniturePlace(HotSpots.SKILL_HALL_STAIRS,
					Furniture.forFurnitureId(stairId), null, myTiles,
					ConstructionData.BASE_X + (myTiles[0] * 8) + 3,
					ConstructionData.BASE_Y + (myTiles[1] * 8) + 3, rotation,
					p, false, 1);
			HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
					myTiles[1] - 1, 0, 36, stairId, 3, 3);
			p.houseFurniture.add(pf);
		}
	
		/**
		 * Create dungeon room from entrance
		 */
		if (toHeight == 102 || toHeight == 103) {
			direction = SAME;
			rotation = p.getMapInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			HouseFurniture pf = null;
			if (toHeight == 102) {
				int stairId = 13497;
				pf = new HouseFurniture(myTiles[0] - 1, myTiles[1] - 1, 4, 36,
						stairId, 3, 3);
			} else {
				pf = new HouseFurniture(myTiles[0] - 1, myTiles[1] - 1, 4, 88,
						13328, 1, 6);
			}
			//toHeight = 4;
			p.houseFurniture.add(pf);
		}
	
		RoomData room = new RoomData(rotation, roomType, p.getHouseStyle());
		POHPaletteTile tile = new POHPaletteTile(room.getX(), room.getY(),
				room.getStyle(), room.getRotation());
	
		int xOff = 0, yOff = 0;
		if (direction == LEFT) {
			xOff = -1;
		}
		if (direction == DOWN) {
			yOff = -1;
		}
		if (direction == RIGHT) {
			xOff = 1;
		}
		if (direction == UP) {
			yOff = 1;
		}
	
		if (toHeight == 1) {
			RoomData r = p.getMapInstance().getOwner().getHouseRooms()[0][(myTiles[0] - 1) + xOff][(myTiles[1] - 1)
			                                                                     + yOff];
			if (r.getType() == ConstructionData.EMPTY
					|| r.getType() == ConstructionData.BUILDABLE
					|| r.getType() == ConstructionData.GARDEN
					|| r.getType() == ConstructionData.FORMAL_GARDEN) {
				p.sendMessage("You need a foundation to build there");
				return;
			}
		}
		House house = p.getMapInstance() instanceof House ? (House) p.getMapInstance() : ((HouseDungeon)p.getMapInstance()).getHouse();
		if (toHeight == 4 || p.inDungeon()) {
			house.getSecondaryPalette().setTile(
					(myTiles[0] - 1) + xOff, (myTiles[1] - 1) + yOff, 0, tile);
		} else {
			house.getPalette().setTile((myTiles[0] - 1) + xOff,
					(myTiles[1] - 1) + yOff, toHeight, tile);
		}
		house.getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][(myTiles[0] - 1) + xOff][(myTiles[1] - 1)
		                                                + yOff] = new RoomData(rotation, roomType, p.getHouseStyle());
	
		p.setConstructionCoords(new int[] { p.getX(), p.getY() });
		//p.getMapInstance().destroy();
		int targetX = (myTiles[0] - 1) + xOff;
		int targetY = (myTiles[1] - 1) + yOff;

		/*if(!p.isBuildingMode()) {
			p.getPA().removeObjects(targetX, targetY, 10);
		}*/
		RoomData rr = house.getOwner().getHouseRooms()[0][(myTiles[0] - 1) + xOff][(myTiles[1] - 1)
		         			                                                                     + yOff];
		
		if (toHeight < 1) { // Only place roofs if we are on the ground floor
		    int roofType = calculateRoofType(p, targetX, targetY, toHeight);
		    if (rr.getType() == ConstructionData.EMPTY
					|| rr.getType() == ConstructionData.BUILDABLE
					|| rr.getType() == ConstructionData.GARDEN
					|| rr.getType() == ConstructionData.FORMAL_GARDEN
					|| rr.getType() == ConstructionData.MENAGERIE
					|| rr.getType() == ConstructionData.SUPERIOR_GARDEN) {
				return;
			}
		    // Set the roof on Height 1
		   // p.getHouseRooms()[1][targetX][targetY] = new RoomData(rotation, roofType, 0);

			house.getOwner().getHouseRooms()[1][targetX][targetY] = new RoomData(rr.getRotation(), ConstructionData.ROOF_SINGLE, rr.getStyle());
		    // Update the palette tile for the roof
		    //RoomData roofData = p.getHouseRooms()[1][targetX][targetY];
		   // POHPaletteTile roofTile = new POHPaletteTile(roofData.getX(), roofData.getY(), 2, rotation);
		    //house.getPalette().setTile(targetX, targetY, 1, roofTile);
		}
		Construction.createPalette(p);
		if (p.inDungeon()) {
			p.getPA().sendConstructedMapPOH(
					house.getSecondaryPalette());
		} else {
			p.getPA().sendConstructedMapPOH(house.getPalette());
		}
		// Logic to auto-place/update roof above the new room


		p.getPA().closeAllWindows();
	    saveHouse(p);
	
	}
	public static int calculateRoofType(Player p, int x, int y, int z) {
	    RoomData room = p.getHouseRooms()[z][x][y];
	    if (room == null) return ConstructionData.EMPTY;

	    // Standard OSRS logic: Most indoor rooms use the same roof ID 
	    // but the cache chunk itself handles the "wall" connectivity.
	    // If your cache has different IDs for different door counts:
	    
	    int doorCount = 0;
	    // Check neighbors to see where connections are
	    if (p.getHouseRooms()[z][x+1][y].getType() != ConstructionData.EMPTY) doorCount++;
	    if (p.getHouseRooms()[z][x-1][y].getType() != ConstructionData.EMPTY) doorCount++;
	    if (p.getHouseRooms()[z][x][y+1].getType() != ConstructionData.EMPTY) doorCount++;
	    if (p.getHouseRooms()[z][x][y-1].getType() != ConstructionData.EMPTY) doorCount++;

	    if (doorCount <= 1) return ConstructionData.ROOF_3_WAY;
	    if (doorCount == 2) return ConstructionData.ROOF_4_WAY;
	    
	    return ConstructionData.ROOF_SINGLE; 
	}
	public static void generatePalette(Player host) {
	    POHPalette palette = new POHPalette();
	    // Build the palette based on the current room layout
	    for(int z = 0; z < 4; z++) {
	        for(int x = 0; x < HouseData.MAX_DIMENSION; x++) {
	            for(int y = 0; y < HouseData.MAX_DIMENSION; y++) {
	                RoomData room = host.getHouseRooms()[z][x][y];
	                if (room == null || room.getX() == 0) continue;

	                // Create the tile reference for the client
	                POHPaletteTile tile = new POHPaletteTile( room.getX(), room.getY(), z, room.getRotation());
	                palette.setTile(x, y, z, tile);
	            }
	        }
	    }
	    
	    // Update the existing instance without teleporting the player
	    if (host.getMapInstance() instanceof House) {
	        ((House) host.getMapInstance()).setPalette(palette);
	    }
	}
	public static void createPalette(Player host) {
		POHPalette palette = new POHPalette();
			for(int z = 0; z < 4; z++) {
				for(int x = 0; x < HouseData.MAX_DIMENSION; x++) {
					for(int y = 0; y < HouseData.MAX_DIMENSION; y++) {
						RoomData room = host.getHouseRooms()[z][x][y];
		                if (room == null || room.getX() == 0) continue;
		                
		                int styleId = room.getStyle(); 
		                
		                // Height stays 0-3 for every style group
		                int sourceHeight = styleId % 4; 
		                
		                // Shift X by 64 for every group of 4 styles
		                int xShift = (styleId / 4) * 64; 

		                POHPaletteTile tile = new POHPaletteTile(
		                        room.getX() + xShift, 
		                        room.getY(),
		                        sourceHeight, // Correctly uses 0-3
		                        room.getRotation());
						palette.setTile(x, y, z, tile);
					}
				}
			}
			int leaveX = host.houseLocation != null ? host.houseLocation.getX() : 2954;
			int leaveY = host.houseLocation != null ? host.houseLocation.getY() : 3224;
			House mapInstance = new House(leaveX, leaveY, true);
			mapInstance.addMember(host);
			mapInstance.setOwner(host);
			mapInstance.setPalette(palette);
			HouseData.createDungeonPalette(host);
			HouseData.enterHouse(host, host, host.inBuildingMode);
			placeNPCs(host);
			//p.getPA().removeObjects(targetX * 8, targetY * 8, 8);
	
	}
	public static void handleDoorVisibility(Player p, RoomData roomData, int chunkX, int chunkY, int z) {
	    Room rd = Room.forID(roomData.getType());
	    if (rd == null) return;
	    
	    boolean isGarden = ConstructionData.isGardenRoom(roomData.getType());
	    boolean[] rawDoors = rd.getDoors(); 

	    int[][][] internalWallCoords = {
	        { {0, 3}, {0, 4} }, // West (0)
	        { {3, 7}, {4, 7} }, // North (1)
	        { {7, 4}, {7, 3} }, // East (2)
	        { {4, 0}, {3, 0} }  // South (3)
	    };

	    for (int internalWall = 0; internalWall < 4; internalWall++) {
	        if (!rawDoors[internalWall]) continue;

	        int worldFacing = (internalWall + roomData.getRotation()) & 3;

	        RoomData neighbor = null;
	        if (worldFacing == 0 && chunkX > 0) 
	            neighbor = p.getHouseRooms()[z][chunkX - 1][chunkY];
	        else if (worldFacing == 1 && chunkY + 1 < HouseData.MAX_DIMENSION) 
	            neighbor = p.getHouseRooms()[z][chunkX][chunkY + 1];
	        else if (worldFacing == 2 && chunkX + 1 < HouseData.MAX_DIMENSION)
	            neighbor = p.getHouseRooms()[z][chunkX + 1][chunkY];
	        else if (worldFacing == 3 && chunkY > 0) 
	            neighbor = p.getHouseRooms()[z][chunkX][chunkY - 1];

	        // DYNAMIC VISIBILITY:
	        // 1. If Closed (2): Always render from the Room side.
	        // 2. If Open (3): Always render from the Garden side.
	        boolean doorIsVisible = false;
	        int doorState = p.getPOHDoor();

	        if (p.inBuildingMode) {
	        	if (!isGarden) {
	                if (neighbor == null || neighbor.getType() == ConstructionData.EMPTY || ConstructionData.isGardenRoom(neighbor.getType()) || ConstructionData.isGardenRoom(roomData.getType())) {
	                    doorIsVisible = true;
	                }
	            } else if (isGarden) {
	                if (neighbor == null || neighbor.getType() == ConstructionData.EMPTY || ConstructionData.isGardenRoom(neighbor.getType()) || ConstructionData.isGardenRoom(roomData.getType())) {
	                    doorIsVisible = true;
	                }
	            }
	        } 
	        // OPEN STATE (3): Render ONLY from the Garden/Empty space perspective
	        else if (doorState == 3 && !p.inBuildingMode) {
	            if (isGarden) {
	                // If we are the Garden, and we are looking at a house room, show the 'open' swing
	                if (neighbor != null && !ConstructionData.isGardenRoom(neighbor.getType()) && neighbor.getType() != ConstructionData.EMPTY) {
	                    doorIsVisible = true;
	                }
	            }
	            if (neighbor.getType() == ConstructionData.EMPTY && !isGarden) {
	                    doorIsVisible = true;
	                }
	        } 
	        // CLOSED STATE (2): Render ONLY from the House Room perspective
	        else if (doorState == 2 && !p.inBuildingMode) {
	            if (!isGarden) {
	                // If we are the Room, and we are looking at a Garden/Empty, show the 'closed' model
	                if (neighbor == null || neighbor.getType() == ConstructionData.EMPTY || ConstructionData.isGardenRoom(neighbor.getType())) {
	                    doorIsVisible = true;
	                }
	                
	            }
	        }

	        for (int side = 0; side < 2; side++) {
	            int[] coord = internalWallCoords[internalWall][side];
	            
	            // For placement, we always use the current room's data.
	            // This prevents "doubles" because only one room (either Room or Garden)
	            // will ever have doorIsVisible = true at one time.
	            int rotX = ConstructionData.getRotatedLandscapeChunkX(roomData.getRotation(), 1, coord[0], coord[1], 1, 0);
	            int rotY = ConstructionData.getRotatedLandscapeChunkY(coord[1], 1, roomData.getRotation(), 1, coord[0], 0);

	            int worldX = ConstructionData.BASE_X + (chunkX + 1) * 8 + rotX;
	            int worldY = ConstructionData.BASE_Y + (chunkY + 1) * 8 + rotY;
	            
	            int finalFace = worldFacing;

	            if (doorIsVisible) {
	                int doorId = 15313; 
	                if (!p.inBuildingMode && doorState == 3) { // OPEN
	                    if(p.getHouseStyle() == 0 || p.getHouseStyle() == 2) doorId = (side == 0) ? 13102 : 13103; 
	                    else if(p.getHouseStyle() == 1) doorId = (side == 0) ? 13095 : 13097; 
	                    else if(p.getHouseStyle() == 3) doorId = (side == 0) ? 15308 : 15307;

	                    // When Open in Garden: Flip face to pivot against the House wall
	                    finalFace = (side == 0) ? (finalFace + 3) & 3 : (finalFace + 1) & 3;
	                } else if (!p.inBuildingMode && doorState == 2) { // CLOSED
	                    if(p.getHouseStyle() == 0 || p.getHouseStyle() == 2) doorId = (side == 0) ? 13101 : 13100; 
	                    else if(p.getHouseStyle() == 1) doorId = (side == 0) ? 13095 : 13097; 
	                    else if(p.getHouseStyle() == 3) doorId = (side == 0) ? 15308 : 15307;
	                    // finalFace stays worldFacing (Room side)
	                } else { // BUILDING
	                    if(p.getHouseStyle() == 0 || p.getHouseStyle() == 2) doorId = (side == 0) ? 15314 : 15313;
	                    else if(p.getHouseStyle() == 1) doorId = (side == 0) ? 15308 : 15307;
	                    else if(p.getHouseStyle() == 3) doorId = (side == 0) ? 15312 : 15311;
	                }
	                p.getPA().sendObject_cons(worldX, worldY, doorId, finalFace, 0, z);
	            } else {
	                // This clears the threshold in the room when open, 
	                // and in the garden when closed.
	                p.getPA().sendObject_cons(worldX, worldY, 6951, 0, 0, z); 
	            }
	        }
	    }
	}
}
