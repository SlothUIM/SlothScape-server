package server.model.players.skills.construction;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;





import server.Config;
import server.Server;
import server.clip.WorldObject;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.skills.construction.Butlers;
import server.model.items.ItemAssistant;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.skills.construction.Servant;
import server.model.objects.Object;
import server.model.players.Player;
import server.model.players.Position;
import server.model.players.MapInstance;
import server.model.players.MapInstance.RegionInstanceType;
import server.model.players.skills.construction.House;
import server.model.players.skills.construction.HouseDungeon;
import server.model.players.combat.Hitmark;
import server.model.players.skills.construction.Room;
import server.model.players.skills.construction.furniture.Furniture;
import server.model.players.skills.construction.util.HouseFurniture;
import server.model.players.skills.construction.util.POHPalette;
import server.model.players.skills.construction.util.Portal;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.skills.construction.util.POHPalette.POHPaletteTile;
import server.model.players.skills.construction.RoomObject;
import server.util.Misc;
import server.world.World;

public class HouseData {

	private static final String houseLocation = "./Data/houses/";

	public static final int MAX_DIMENSION = 13;

	public LinkedList<RoomObject> objects = new LinkedList<RoomObject>();
	public LinkedList<Player> guests = new LinkedList<Player>();

	private boolean guestsAllowed = true;

	private Player host;

	public HouseData(Player host) {
		this(host, true);
	}

	public HouseData(Player host, boolean guestsAllowed) {
		this.host = host;
		this.guestsAllowed = guestsAllowed;
	}

	public boolean allowingGuests() {
		return guestsAllowed;
	}

	public void allowGuests(boolean allow) {
		this.guestsAllowed = allow;
	}

	public LinkedList<Player> getGuests() {
		return guests;
	}

	public Player getHost() {
		return host;
	}
	public static boolean hasHouse(Player player) {
		return new File(houseLocation + player.playerName + ".room").exists();
	}
	public static void enterHouse(final Player me, final Player houseOwner,
			final boolean buildingMode) {
		if (me.getMapInstance() == null
				|| !(me.getMapInstance().getType() == RegionInstanceType.CONSTRUCTION_HOUSE)
						&& !(me.getMapInstance().getType() == RegionInstanceType.CONSTRUCTION_DUNGEON)) 
{
			Construction.createPalette(me);
			return;
		}
		if(buildingMode == true)
			houseOwner.inBuildingMode = true;
		Construction.enteringHouse = true;
		me.getPA().setMinimapState(2);
		me.getPA().sendConfig(8005, me.getHouseStyle());
		if (buildingMode) {
			
			me.getPA().sendConfig(8000, 0);
			me.getPA().sendConfig(8001, 0);
		} else {
			me.getPA().sendConfig(8000, 1);
			me.getPA().sendConfig(8001, 1);
		}
		me.sendMessage("Building mode is "+(!me.inBuildingMode ? "off" : "on"));
		me.getPA().sendConfig(8010, me.inBuildingMode ? 0 : 1);
		me.getPA().movePlayer(ConstructionData.MIDDLE_X,
				ConstructionData.MIDDLE_Y, 0);
		me.getPA().showInterface(37460);
		CycleEventHandler.getSingleton().addEvent(houseOwner, new CycleEvent() {
			int ticks = 0, x = -1, y = -1;
			@Override
			public void execute(CycleEventContainer container) {
				ticks++;
				if (ticks == 1) {
					//me.getPA().sendConstructedMapPOH(houseOwner.mapInstance.getPalette());
					me.getPA().sendConstructedMapPOH(((House) houseOwner.getMapInstance()).getPalette());
				}
				if (ticks == 2) {

					Construction.placeAllFurniture(houseOwner, 0);
					Construction.placeAllFurniture(houseOwner, 1);
					if (me.getConstructionCoords() != null) {
						me.getPA().movePlayer(me.toConsCoords[0], me.toConsCoords[1], 0);
						System.out.println("Stage 1.5: Going to X-"+me.toConsCoords[0]+" Y-"+me.toConsCoords[1]);
						me.toConsCoords = null;
					} else {
						HouseFurniture portal = Construction.findNearestPortal(me);
						if (portal == null) {
						    // If no portal is found nearby, stop the execution instead of crashing
						    return; 
						}
						x = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
						y = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);

					}
				}
				if(ticks == 4) {
					
					if (x != -1 && y != -1)
						me.getPA().movePlayer(x+2, y+2, 0);
					me.getPA().setMinimapState(2);
				}
				if(ticks == 5) {
					me.getPA().closeAllWindows();
					me.getPA().setMinimapState(0);
					Construction.enteringHouse = false;
					//((House)me.getMapInstance()).greet(me);
					me.setInHouse(true);
					container.stop();
				}
			}
			@Override
			public void stop() {	
			}
		}, 1);

	}
	public static void newHouse(Player p) {
		p.setHouseStyle(1);
		if (p.getHouseRooms()[0][0][0] != null)
			return;
			for (int x = 0; x < MAX_DIMENSION; x++) {
				for (int y = 0; y < MAX_DIMENSION; y++) {
					p.getHouseRooms()[0][x][y] = new RoomData(0, ConstructionData.EMPTY, 0);
				}
			}
			p.getHouseRooms()[0][7][8] = new RoomData(0, ConstructionData.PARLOUR, 0);
			p.getHouseRooms()[0][7][7] = new RoomData(0, ConstructionData.GARDEN, 0);

			p.getHouseRooms()[1][7][8] = new RoomData(0, ConstructionData.ROOF_SINGLE, 0);
		HouseFurniture pf = new HouseFurniture(7, 7, 0, HotSpots.CENTREPIECE.getHotSpotId(),
				Furniture.EXIT_PORTAL.getFurnitureId(), HotSpots.CENTREPIECE.getXOffset(),
				HotSpots.CENTREPIECE.getYOffset());
		p.getHouseFurniture().add(pf);
		Construction.saveHouse(p);
	}
	public static void createDungeonPalette(Player p) {
		POHPalette palette = new POHPalette();
		for (int x = 0; x < MAX_DIMENSION; x++) {
			for (int y = 0; y < MAX_DIMENSION; y++) {
				POHPaletteTile tile = null;
				if (p.getHouseRooms()[4][x][y] == null) {
					tile = new POHPaletteTile(
							Room.DUNGEON_EMPTY.getX(),
							Room.DUNGEON_EMPTY.getY(), 0,
							0);
				} else {
					tile = new POHPaletteTile(
							p.getHouseRooms()[4][x][y].getX(),
							p.getHouseRooms()[4][x][y].getY(),
							p.getHouseRooms()[4][x][y].getStyle(),
							p.getHouseRooms()[4][x][y].getRotation());
				}
				palette.setTile(x, y, 0, tile);
			}
		}

		p.mapInstance.setSecondaryPalette(palette);
	}
	public static void doFurniturePlace(HotSpots s, Furniture f,
			ArrayList<HotSpots> hsses, int[] myTiles, int actualX, int actualY,
			int roomRot, Player p, boolean placeBack, int height) {
		int portalId = -1;
			
		if(s.getHotSpotId() == 72)
		{
			if(s.getXOffset() == 0)
			{
				for(Portal portal : p.getMapInstance().getOwner().portals)
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 0)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}
			}
			if(s.getXOffset() == 3)
			{
				for(Portal portal : p.getMapInstance().getOwner().getHousePortals())
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 1)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}

			}
			if(s.getXOffset() == 7)
			{
				for(Portal portal : p.getMapInstance().getOwner().getHousePortals())
				{
					if(portal.getRoomX() == myTiles[0]-1 &&
							portal.getRoomY() == myTiles[1]-1 &&
							portal.getRoomZ() == height && portal.getId() == 2)
					{
						if(Portals.forType(portal.getType()).getObjects() != null)
							portalId = Portals.forType(portal.getType()).getObjects()[f.getFurnitureId()-13636];

					}
				}
			}
		}
		if (height == 4)
			height = 0;

		if (s.getHotSpotId() == 92) {//dungeon doors 2
			int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
			int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			System.out.println("objectId "+s.getObjectId()+" placed");
			if (s.getObjectId() == 15329 || s.getObjectId() == 15328) {
				p.getPA().sendObject_cons(
						actualX,
						actualY,
						s.getObjectId() == 15328 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
								0, height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15329 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				p.getPA().sendObject_cons(
						offsetX,
						offsetY,
						s.getObjectId() == 15329 ? (placeBack ? 15328 : f
								.getFurnitureId()) : (placeBack ? 15329 : f
										.getFurnitureId() + 1), s.getRotation(roomRot),
								0, height);

			}
			if (s.getObjectId() == 15326 || s.getObjectId() == 15327) { // dungeon doors 1
				p.getPA().sendObject_cons(
						actualX,
						actualY,
						s.getObjectId() == 15327 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
								height);
				offsetX += ConstructionData.getXOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				offsetY += ConstructionData.getYOffsetForObjectId(
						f.getFurnitureId(), s.getXOffset()
						+ (s.getObjectId() == 15326 ? 1 : -1),
						s.getYOffset(), roomRot, s.getRotation(0));
				p.getPA().sendObject_cons(
						offsetX,
						offsetY,
						s.getObjectId() == 15326 ? (placeBack ? 15327 : f
								.getFurnitureId() + 1) : (placeBack ? 15326 : f
										.getFurnitureId()), s.getRotation(roomRot), 0,
								height);

			}
		} else if (s.getHotSpotId() == 85) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;
			int type = 22, leftObject = 0, rightObject = 0, upperObject = 0, downObject = 0, middleObject = 0, veryMiddleObject = 0, cornerObject = 0;
			if (f.getFurnitureId() == 13331) {
				leftObject = rightObject = upperObject = downObject = 13332;
				middleObject = 13331;
				cornerObject = 13333;
			}
			if (f.getFurnitureId() == 13334) {
				leftObject = rightObject = upperObject = downObject = 13335;
				middleObject = 13334;
				cornerObject = 13336;
			}
			if (f.getFurnitureId() == 13337) {
				leftObject = rightObject = upperObject = downObject = middleObject = cornerObject = 13337;
				type = 10;
			}
			if (f.getFurnitureId() == 13373) {
				veryMiddleObject = 13373;
				leftObject = rightObject = upperObject = downObject = middleObject = 6951;
			}
			if (placeBack || f.getFurnitureId() == 13337) {
				for (int x = 0; x < 4; x++) {
					for (int y = 0; y < 4; y++) {
						p.getPA().sendObject_cons(actualX + x, actualY + y,
								6951, 0, 10, height);
						p.getPA().sendObject_cons(actualX + x, actualY + y,
								6951, 0, 22, height);
					}
				}

			}
			p.getPA().sendObject_cons(actualX, actualY,
					placeBack ? 15348 : cornerObject, 1, type, height);
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15348 : leftObject, 1, type, height);
			p.getPA().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15348 : leftObject, 1, type, height);
			p.getPA().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15348 : cornerObject, 2, type, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15348 : upperObject, 2, type, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15348 : cornerObject, 3, type, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15348 : rightObject, 3, type, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15348 : rightObject, 3, type, height);
			p.getPA().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15348 : cornerObject, 0, type, height);
			p.getPA().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			p.getPA().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15348 : downObject, 0, type, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 1,
					placeBack ? 15348 : middleObject, 0, type, height);
			if (veryMiddleObject != 0)
				p.getPA().sendObject_cons(actualX + 1, actualY + 2,
						veryMiddleObject, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 2,
					placeBack ? 15348 : middleObject, 0, type, height);

		} else if (s.getHotSpotId() == 86) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 2;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 2;

			p.getPA().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 2, 2, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 2, 0, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId() + 1, 2, 0, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 2, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15352 : f.getFurnitureId(), 0, 2, height);
			p.getPA().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15352 : f.getFurnitureId(), 0, 0, height);
			p.getPA().sendObject_cons(actualX, actualY,
					placeBack ? 15352 : f.getFurnitureId(), 3, 2, height);

		} else if (s.getHotSpotId() == 78) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// south walls
			p.getPA().sendObject_cons(actualX, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 2, height);
			p.getPA().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 6, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 3, 0, height);
			p.getPA().sendObject_cons(actualX + 7, actualY,
					placeBack ? 15369 : f.getFurnitureId(), 2, 2, height);
			// north walls
			p.getPA().sendObject_cons(actualX, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 0, 2, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX + 6, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 0, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 7,
					placeBack ? 15369 : f.getFurnitureId(), 1, 2, height);
			// left walls
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 0, 0, height);
			// right walls
			p.getPA().sendObject_cons(actualX + 7, actualY + 1,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 2,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 5,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 6,
					placeBack ? 15369 : f.getFurnitureId(), 2, 0, height);
		} else if (s.getHotSpotId() == 77) {
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			// left down corner
			p.getPA().sendObject_cons(actualX, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 3, 10, height);
			p.getPA().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			p.getPA().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// right down corner
			p.getPA().sendObject_cons(actualX + 7, actualY,
					placeBack ? 15372 : f.getFurnitureId() + 1, 2, 10, height);
			p.getPA().sendObject_cons(actualX + 6, actualY,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 1,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 2,
					placeBack ? 15370 : f.getFurnitureId(), 3, 10, height);
			// upper left corner
			p.getPA().sendObject_cons(actualX, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 0, 10, height);
			p.getPA().sendObject_cons(actualX, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 1, 10, height);
			p.getPA().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
			// upper right corner
			p.getPA().sendObject_cons(actualX + 7, actualY + 7,
					placeBack ? 15372 : f.getFurnitureId() + 1, 1, 10, height);
			p.getPA().sendObject_cons(actualX + 6, actualY + 7,
					placeBack ? 15371 : f.getFurnitureId() + 2, 0, 10, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 7,
					placeBack ? 15370 : f.getFurnitureId(), 2, 10, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 6,
					placeBack ? 15371 : f.getFurnitureId() + 2, 3, 10, height);
			p.getPA().sendObject_cons(actualX + 7, actualY + 5,
					placeBack ? 15370 : f.getFurnitureId(), 1, 10, height);
		} else if (s.getHotSpotId() == 44) {
			int combatringStrings = 6951;
			int combatringFloorsCorner = 6951;
			int combatringFloorsOuter = 6951;
			int combatringFloorsInner = 6951;
			actualX = ConstructionData.BASE_X + (myTiles[0] * 8) + 1;
			actualY = ConstructionData.BASE_Y + (myTiles[1] * 8) + 1;
			if (!placeBack) {
				if (f.getFurnitureId() == 13126) {
					combatringStrings = 13132;
					combatringFloorsCorner = 13126;
					combatringFloorsOuter = 13128;
					combatringFloorsInner = 13127;
				}
				if (f.getFurnitureId() == 13133) {
					combatringStrings = 13133;
					combatringFloorsCorner = 13135;
					combatringFloorsOuter = 13134;
					combatringFloorsInner = 13136;
				}
				if (f.getFurnitureId() == 13137) {
					combatringStrings = 13137;
					combatringFloorsCorner = 13138;
					combatringFloorsOuter = 13139;
					combatringFloorsInner = 13140;
				}
			}

			p.getPA().sendObject_cons(actualX + 2, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 3,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 2,
					placeBack ? 15292 : combatringFloorsInner, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 1,
					placeBack ? 15291 : combatringFloorsOuter, 3, 22, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 4,
					placeBack ? 15291 : combatringFloorsOuter, 1, 22, height);
			p.getPA().sendObject_cons(actualX + 4, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			p.getPA().sendObject_cons(actualX + 4, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 2, 22, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 3,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 2,
					placeBack ? 15291 : combatringFloorsOuter, 0, 22, height);
			p.getPA().sendObject_cons(actualX + 4, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 3, 22, height);
			p.getPA().sendObject_cons(actualX + 4, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 2, 22, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 4,
					placeBack ? 15289 : combatringFloorsCorner, 1, 22, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 1,
					placeBack ? 15289 : combatringFloorsCorner, 0, 22, height);
			p.getPA().sendObject_cons(actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			p.getPA().sendObject_cons(actualX + 1, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPA().sendObject_cons(actualX + 2, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPA().sendObject_cons(actualX + 3, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPA().sendObject_cons(actualX + 4, actualY,
					placeBack ? 15277 : combatringStrings, 1, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY,
					placeBack ? 15277 : combatringStrings, 0, 3, height);
			p.getPA().sendObject_cons(actualX + 1, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 2, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 3, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 4, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 5,
					placeBack ? 15277 : combatringStrings, 3, 3, height);
			p.getPA().sendObject_cons(actualX, actualY + 5,
					placeBack ? 15277 : combatringStrings, 2, 3, height);
			p.getPA().sendObject_cons(actualX, actualY,
					placeBack ? 15277 : combatringStrings, 1, 3, height);
			p.getPA().sendObject_cons(actualX, actualY + 4,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 3,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 2,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPA().sendObject_cons(actualX, actualY + 1,
					placeBack ? 15277 : combatringStrings, 2, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 4,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 3,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 2,
					placeBack ? 15277 : combatringStrings, 0, 0, height);
			p.getPA().sendObject_cons(actualX + 5, actualY + 1,
					placeBack ? 15277 : combatringStrings, 0, 0, height);

			if (f.getFurnitureId() == 13145) {
				p.getPA().sendObject_cons(actualX + 1, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPA().sendObject_cons(actualX + 2, actualY + 1,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPA().sendObject_cons(actualX + 1, actualY,
						placeBack ? 6951 : 13145, 1, 0, height);
				p.getPA().sendObject_cons(actualX + 1, actualY + 2,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					p.getPA().sendObject_cons(actualX + 1, actualY + 1, 13147,
							0, 22, height);

				p.getPA().sendObject_cons(actualX + 3, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPA().sendObject_cons(actualX + 4, actualY + 3,
						placeBack ? 6951 : 13145, 0, 0, height);
				p.getPA().sendObject_cons(actualX + 3, actualY + 2,
						placeBack ? 6951 : 13145, 1, 0, height);
				p.getPA().sendObject_cons(actualX + 3, actualY + 4,
						placeBack ? 6951 : 13145, 3, 0, height);
				if (!placeBack)
					p.getPA().sendObject_cons(actualX + 3, actualY + 3, 13147,
							0, 22, height);
			}
			if (f.getFurnitureId() == 13142 && !placeBack) {
				p.getPA().sendObject_cons(actualX + 2, actualY + 2, 13142, 0,
						22, height);
				p.getPA().sendObject_cons(actualX + 2, actualY + 1, 13143, 0,
						22, height);
				p.getPA().sendObject_cons(actualX + 2, actualY + 3, 13144, 1,
						22, height);

			}
		} else if (s.getCarpetDim() != null) {
			for (int x = 0; x < s.getCarpetDim().getWidth() + 1; x++) {
				for (int y = 0; y < s.getCarpetDim().getHeight() + 1; y++) {
					boolean isEdge = (x == 0 && y == 0 || x == 0
							&& y == s.getCarpetDim().getHeight() || y == 0
							&& x == s.getCarpetDim().getWidth() || x == s
							.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight());
					boolean isWall = ((x == 0 || x == s.getCarpetDim()
							.getWidth())
							&& (y != 0 && y != s.getCarpetDim().getHeight()) || (y == 0 || y == s
							.getCarpetDim().getHeight())
							&& (x != 0 && x != s.getCarpetDim().getWidth()));
					int rot = 0;
					if (x == 0 && y == s.getCarpetDim().getHeight() && isEdge)
						rot = 0;
					if (x == s.getCarpetDim().getWidth()
							&& y == s.getCarpetDim().getHeight() && isEdge)
						rot = 1;
					if (x == s.getCarpetDim().getWidth() && y == 0 && isEdge)
						rot = 2;
					if (x == 0 && y == 0 && isEdge)
						rot = 3;
					if (y == 0 && isWall)
						rot = 2;
					if (y == s.getCarpetDim().getHeight() && isWall)
						rot = 0;
					if (x == 0 && isWall)
						rot = 3;
					if (x == s.getCarpetDim().getWidth() && isWall)
						rot = 1;
					int offsetX = ConstructionData.BASE_X + (myTiles[0] * 8);
					int offsetY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					offsetX += ConstructionData.getXOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					offsetY += ConstructionData.getYOffsetForObjectId(
							f.getFurnitureId(), s.getXOffset() + x - 1,
							s.getYOffset() + y - 1, roomRot,
							s.getRotation(roomRot));
					if (isEdge)
						p.getPA().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + 2 : f
										.getFurnitureId(),
										HotSpots.getRotation_2(rot, roomRot), 22,
										height);
					else if (isWall)
						p.getPA().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() + 1 : f
										.getFurnitureId() + 1,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
					else
						p.getPA().sendObject_cons(
								offsetX,
								offsetY,
								placeBack ? s.getObjectId() : f
										.getFurnitureId() + 2,
										HotSpots.getRotation_2(rot, roomRot),
										s.getObjectType(), height);
				}
			}
		} else if (s.isMutiple()) {

			RoomData room = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0] - 1][myTiles[1] - 1];
			for (HotSpots find : hsses) {
				if (find.getObjectId() != s.getObjectId())
					continue;
				System.out.println(room.getType() +" "+find.getRoomType());
				if (room != null)
					if (room.getType() != find.getRoomType())
						continue;
				int actualX1 = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX1 += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY1 = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY1 += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);

				p.getPA()
				.sendObject_cons(
						actualX1,
						actualY1,
						placeBack ? s.getObjectId() : f
								.getFurnitureId(),
								find.getRotation(roomRot),
								find.getObjectType(), height);
			}
		} else {		
				p.getPA().sendObject_cons(actualX, actualY, (portalId != -1 ? portalId : placeBack ? s.getObjectId() : f.getFurnitureId()),
					s.getRotation(roomRot), s.getObjectType(), height);
				
		}

	}

	public static void handleThirdObjectClick(final int obX, final int obY,
			int objectId, final Player p) {
		switch(objectId)
		{
		case 13326:
		case 13323:
		case 13320:
		case 13317:
		case 13314:
			int[] myTiles = Construction.getMyChunk(p);
			if(myTiles == null)
				return;
			RoomData r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.OUBLIETTE)
				return;
			break;
		}
	}
	public static RoomData getCurrentRoom(Player p)
	{
		Player owner = p.getMapInstance().getOwner();
		int[] myTiles = Construction.getMyChunk(p);
		return owner.getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1];
	}
	public static void handleFirstObjectClick(final int obX, final int obY,
			final int objectId, final Player p) {

		for(Portals ps : Portals.values())
		{
			if(ps == Portals.EMPTY)
				continue;
			for(int i : ps.getObjects())
			{
				if(i == objectId)
				{
					if(getCurrentRoom(p).getType() == ConstructionData.PORTAL_ROOM)
					{
						p.mapInstance.removePlayer(p);
						p.getPA().movePlayer(ps.getDestination().getX(), ps.getDestination().getY(), 0);
						return;
					}
				}
			}
		}
		switch (objectId) {
		case 15477:
			p.getDH().sendDialogues(442, -1);
			break;
		case 13640:
		case 13641:
		case 13639:
			if(getCurrentRoom(p).getType() != ConstructionData.PORTAL_ROOM)
				break;
			p.getDH().sendDialogues(645, 0);
			break;
		case 13523:
			if(getCurrentRoom(p).getType() != ConstructionData.QUEST_ROOM
			&& getCurrentRoom(p).getType() != ConstructionData.QUEST_HALL_DOWN)
				break;
			p.getDH().sendDialogues(644, 0);
			break;
		case 13481:
			if(p.mapInstance.getOwner() == p){
				int random = Misc.random(2);
				if(random == 0)
					p.getDH().sendDialogues(632, 4226);
				if(random == 1)
					p.getDH().sendDialogues(634, 4226);
				if(random == 2)
					p.getDH().sendDialogues(637, 4226);
			}else
				p.getDH().sendDialogues(639, 4226);
			break;
		case 13482:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(607, 4227);
			else
				p.getDH().sendDialogues(630, 4227);
			break;
		case 13483:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(568, 4228);
			else
				p.getDH().sendDialogues(605, 4228);
			break;
		case 13484:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(512, 4229);
			else
				p.getDH().sendDialogues(566, 4229);
			break;
		case 13485:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(490, 4230);
			else
				p.getDH().sendDialogues(510, 4230);
			break;
		case 13486:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(458, 4232);
			else
				p.getDH().sendDialogues(466, 4232);
			break;
		case 13487:
			if(p.mapInstance.getOwner() == p)
				p.getDH().sendDialogues(476, 4234);
			else
				p.getDH().sendDialogues(487, 4234);
			break;
		case 13405:
			if(p.mapInstance == null)
				return;
			if(!(p.mapInstance instanceof House))
				return;
			MapInstance mi = p.mapInstance;
			p.properLogout = true;
			if(mi.getOwner() == p)
			{
				mi.destroy();
			} else {
				mi.removePlayer(p);
			}
			p.properLogout = false;
			break;
		case 13307:
		case 13308:
		case 13309:
			final Servant s = ((House)p.getMapInstance()).getButler();
			if(s == null)
			{
				p.sendMessage("Ding says the bell.");
				return;
			}
			s.setX(p.getLocation().getX());
			s.setY(p.getLocation().getY());
			final MapInstance mi_ = p.getMapInstance();
			s.mapInstance = null;
			s.randomWalk = false;

			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					s.mapInstance = mi_;
					container.stop();
				}

				@Override
				public void stop() {

				}
			}, 3);
			break;

		case 13326:
		case 13323:
		case 13320:
		case 13317:
		case 13314:
			int[] myTiles =  Construction.getMyChunk(p);
			if(myTiles == null)
				return;
			RoomData r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.OUBLIETTE)
				return;
			p.sendMessage("It's locked.");
			p.getPA().movePlayer(ConstructionData.BASE_X+(myTiles[0]*8) + 6, 
					ConstructionData.BASE_Y+(myTiles[1]*8) + 4 , p.getLocation().getZ());
			break;
		case 15478:
			p.getDH().sendDialogues(442, 1);
			break;

		case 13381:
			p.getDH().sendDialogues(439, 1);
			break;
		case 13382:
			p.getDH().sendDialogues(440, 1);
			break;
		case 13383:
			p.getDH().sendDialogues(441, 1);
			break;
			/**
			 * Dungeon doors
			 */
		case 13347:
		case 13346:
		case 13344:
		case 13345:
		case 13348:
		case 13349:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[4][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.CORRIDOR
					&& r.getType() != ConstructionData.DUNGEON_STAIR_ROOM
					&& r.getType() != ConstructionData.TREASURE_ROOM)
				break;
			p.sendMessage("It's locked");
			break;
		case 13132:
		case 13133:
		case 13137:
			myTiles =  Construction.getMyChunk(p);
			if(myTiles == null)
				return;
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.COMBAT_ROOM)
				return;
			boolean canGoIn = true;
			if(objectId == 13132)
			{
				if(p.playerEquipment[p.playerHat] != -1
						|| p.playerEquipment[p.playerCape] != -1
						|| p.playerEquipment[p.playerAmulet] != -1
						|| p.playerEquipment[p.playerArrows] != -1
						|| p.playerEquipment[p.playerChest] != -1
						|| p.playerEquipment[p.playerShield] != -1
						|| p.playerEquipment[p.playerLegs] != -1
						|| p.playerEquipment[p.playerHands] != -1
						|| p.playerEquipment[p.playerFeet] != -1
						|| p.playerEquipment[p.playerRing] != -1
						)
					canGoIn = false;
				if((p.playerEquipment[p.playerWeapon] != 7671
						&& p.playerEquipment[p.playerWeapon] != 7673)
						&& p.playerEquipment[p.playerWeapon] != -1)
					canGoIn = true;
				{

				}
			}
			if(objectId == 13133)
			{
				if(p.playerEquipment[p.playerHat] != -1
						|| p.playerEquipment[p.playerCape] != -1
						|| p.playerEquipment[p.playerAmulet] != -1
						|| p.playerEquipment[p.playerArrows] != -1
						|| p.playerEquipment[p.playerChest] != -1
						|| p.playerEquipment[p.playerShield] != -1
						|| p.playerEquipment[p.playerLegs] != -1
						|| p.playerEquipment[p.playerHands] != -1
						|| p.playerEquipment[p.playerFeet] != -1
						|| p.playerEquipment[p.playerRing] != -1
						)
					canGoIn = false;
			}
			if(!canGoIn)
			{
				p.sendMessage("You can't wear this equipment.");
				return;
			}
			int xOnTile =  Construction.getXTilesOnTile(myTiles, p);
			int yOnTile =  Construction.getYTilesOnTile(myTiles, p);
			if((xOnTile >= 2 && xOnTile <= 5) && yOnTile == 1)
			{
				p.getPA().movePlayer(p.getX(), p.getY()+1, p.getLocation().getZ());
				p.combatRingType = objectId;
			}
			if((xOnTile >= 2 && xOnTile <= 5) && yOnTile == 2)
			{
				p.getPA().movePlayer(p.getX(), p.getY()-1, p.getLocation().getZ());
				p.combatRingType = 0;
			}

			if((xOnTile >= 2 && xOnTile <= 5) && yOnTile == 6)
			{
				p.getPA().movePlayer(p.getX(), p.getY()-1, p.getLocation().getZ());
				p.combatRingType = objectId;
			}
			if((xOnTile >= 2 && xOnTile <= 5) && yOnTile == 5)
			{
				p.getPA().movePlayer(p.getX(), p.getY()+1, p.getLocation().getZ());
				p.combatRingType = 0;
			}


			if((yOnTile >= 2 && yOnTile <= 5) && xOnTile == 1)
			{
				p.getPA().movePlayer(p.getX()+1, p.getY(), p.getLocation().getZ());
				p.combatRingType = objectId;
			}
			if((yOnTile >= 2 && yOnTile <= 5) && xOnTile == 2)
			{
				p.getPA().movePlayer(p.getX()-1, p.getY(), p.getLocation().getZ());
				p.combatRingType = 0;
			}

			if((yOnTile >= 2 && yOnTile <= 5) && xOnTile == 6)
			{
				p.getPA().movePlayer(p.getX()-1, p.getY(), p.getLocation().getZ());
				p.combatRingType = objectId;
			}
			if((yOnTile >= 2 && yOnTile <= 5) && xOnTile == 5)
			{
				p.getPA().movePlayer(p.getX()+1, p.getY(), p.getLocation().getZ());
				p.combatRingType = 0;
			}

			break;
		case 13399:
		case 13400:
		case 13401:
		case 13402:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1];
			p.resetChairAnim = false;
			//p.stopMovement();

			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				int ticks, ticks1 = 0;
				int succes = 0;
				@Override
				public void execute(CycleEventContainer container) {
					if(ticks == 0)
						p.resetChairAnim = false;
					if(p.resetChairAnim) {
						container.stop();
						return;
					}
					boolean usingBow = false;
					boolean usingArrows = false;
					boolean usingOtherRangeWeapons = false;
					boolean usingCross = p.playerEquipment[p.playerWeapon] == 9185
							|| p.playerEquipment[p.playerWeapon] == 18357;
					for (int bowId : p.BOWS) {
						if (p.playerEquipment[p.playerWeapon] == bowId) {
							usingBow = true;
							for (int arrowId : p.ARROWS) {
								if (p.playerEquipment[p.playerArrows] == arrowId) {
									usingArrows = true;
								}
							}
						}
					}
					for (int otherRangeId : p.OTHER_RANGE_WEAPONS) {
						if (p.playerEquipment[p.playerWeapon] == otherRangeId) {
							usingOtherRangeWeapons = true;
						}
					}
					if(objectId == 13402)
					{
						if(!usingBow && !usingOtherRangeWeapons)
						{
							p.sendMessage("You need a ranged weapon to do this");
							container.stop();
							return;
						}
						if(!usingArrows && (usingOtherRangeWeapons || usingBow))
						{
							p.sendMessage("You need arrows to do this");
							container.stop();
							return;
						}

						if (usingCross && !p.getCombat().properBolts()) {
							p.sendMessage("You must use bolts with a crossbow.");
							//p.stopMovement();
							container.stop();
							return;
						}
					}
					if(objectId == 13400)
					{
						if(!usingOtherRangeWeapons)
						{
							p.sendMessage("You need darts or knives to do this.");
							container.stop();
							return;
						}
					}
					if(ticks1 == 1)
					{
						if(objectId == 13399)
							p.startAnimation(3602);
						if(objectId == 13400)
						{
							p.startAnimation(3605);
						}
						if(objectId == 13402)
						{
							int anim = p.getCombat().getWepAnim(ItemAssistant.getItemName(p.playerEquipment[p.playerWeapon]).toLowerCase());
							p.startAnimation(anim);
						}
						if(objectId == 13400 || objectId == 13402)
							p.gfx0(p.getCombat().getRangeStartGFX());
					}
					if(ticks1 == 2) {
						boolean succes = (Misc.random(100) <= p.playerLevel[4]) || Misc.random(5) == 1;
						if(succes)
							this.succes++;
						int offY = (p.getX() - obX) * -1;
						int offX = (p.getY() - obY) * -1;

						int gfx = (objectId == 13399 ? 612 : p.getCombat().getRangeProjectileGFX());
						p.getPA().createPlayersProjectile(p.getX(), p.getY(), offX, offY, 50, 85,  gfx, 15, 40, -p.getId() - 1, 65);	
					}
					if(ticks == 10)
					{
						container.stop();
					}
					if(ticks1 == 2)
					{
						ticks1 = 0;
						ticks++;
					} else
						ticks1++;
				}
				@Override
				public void stop()
				{
					p.resetChairAnim = false;
					p.sendMessage("Targets hit: "+succes +"/"+ticks);
					p.getPA().resetAnimation();
				}
			}, 1);
			break;

		case 13379:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			ArrayList<HouseFurniture> pfs = ((House) p.getMapInstance()).getActivatedObject(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getLocation().getZ());
			for(HouseFurniture pp : pfs)
			{
				if(pp.getFurnitureId() == objectId)
				{
					p.sendMessage("You cant reactivate");
					return;
				}
			}
			if(r.getType() == ConstructionData.GAMES_ROOM)
			{
				HouseFurniture pf = new HouseFurniture(myTiles[0], myTiles[1], p.inDungeon() ? 4 : p.getLocation().getZ(), 0, objectId, 0, 0);
				((House) p.mapInstance).getFurnitureActivated().add(pf);
				ArrayList<int[]> possibleRooms = new ArrayList<int[]>();
				for(int z = 0; z < 2; z++)
				{
					for(int x = 0; x < MAX_DIMENSION; x++)
					{
						for(int y = 0; y < MAX_DIMENSION; y++)
						{
							RoomData rr = p.getMapInstance().getOwner().getHouseRooms()[z][x][y];
							if(rr == null) 
								continue;
							if(rr.getType() == ConstructionData.EMPTY)
								continue;
							possibleRooms.add(new int[] {ConstructionData.BASE_X+(x*8), ConstructionData.BASE_Y+(y*8), z});
						}
					}
				}
				int[] coord = possibleRooms.get(Misc.random(possibleRooms.size()));
				final NPC npc = World.getWorld().npcHandler.spawnNpc(p, 3954, coord[0]+2, coord[1]+2, coord[2], 0, 100, 0, 0, 0, false, false);
				p.getMapInstance().addNPC(npc);
				npc.mapInstance = null;
				for(Player p1 : p.getMapInstance().members)
				{
					int[] hisTiles =  Construction.getMyChunk(p1);
					if(hisTiles[0] == myTiles[0] && hisTiles[1] == myTiles[1] && p.getLocation().getZ() == p1.getLocation().getZ())
					{
						p1.getItems().addItem(7677, 1);
						p1.sendMessage("These magic stones will guide you to me!");
					}
				}
			}
			break;
		case 13404:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1];
			pfs = ((House) p.mapInstance).getActivatedObject(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getLocation().getZ());
			for(HouseFurniture pp : pfs)
			{
				if(pp.getFurnitureId() == objectId)
				{
					p.sendMessage("You cant reactivate");
					return;
				}
			}
			if(r.getType() == ConstructionData.GAMES_ROOM)
			{
				HouseFurniture pf = new HouseFurniture(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getLocation().getZ()
						, 0, objectId, 0, 0);
				((House) p.getMapInstance()).getFurnitureActivated().add(pf);
				final NPC npc = World.getWorld().npcHandler.spawnNpc(p, 3944, obX, obY, p.getLocation().getZ(), 0, 100, 0, 0, 0, false, false);
				p.mapInstance.addNPC(npc);
				String word = ConstructionData.HANGMANWORDS[Misc.random(ConstructionData.HANGMANWORDS.length-1)];
				npc.hangManAnswer = word;
				npc.currentHangMan = "";
				npc.hangManStatus = 3944;
				for(int i = 0; i < word.length(); i++)
				{
					npc.currentHangMan += "-";
				}
			}
			break;


		case 13390:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			pfs = ((House) p.mapInstance).getActivatedObject(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
			for(HouseFurniture pp : pfs)
			{
				if(pp.getFurnitureId() == objectId)
				{
					p.sendMessage("You cant reactivate");
					return;
				}
			}
			if(r.getType() == ConstructionData.GAMES_ROOM)
			{
				HouseFurniture pf = new HouseFurniture(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight()
						, 0, objectId, 0, 0);
				((House) p.mapInstance).getFurnitureActivated().add(pf);
				p.getPA().stillGfx(610, ConstructionData.BASE_X+(myTiles[0]*8)+3, ConstructionData.BASE_Y+(myTiles[1]*8)+3, 0, 0);
				final NPC npc = World.getWorld().npcHandler.spawnNpc(p, 3955, ConstructionData.BASE_X+(myTiles[0]*8)+3, ConstructionData.BASE_Y+(myTiles[1]*8)+3, p.getHeight(), 0, 100, 0, 0, 0, false, false);

				CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {

						p.mapInstance.addNPC(npc);
						container.stop();

					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub
						//stop();

					}
				}, 3);

				CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if(npc == null)
							container.stop();
						if(npc.isDead)
							container.stop();
						int anim = EMOTES[Misc.random(EMOTES.length-1)];
						npc.animNumber = anim;
						npc.animUpdateRequired = true;
						npc.jesterAnim = anim;
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub
						//stop();

					}
				}, 15);
			}
			break;

		case 13395:
		case 13396:
		case 13397:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			pfs = ((House) p.mapInstance).getActivatedObject(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
			for(HouseFurniture pp : pfs)
			{
				if(pp.getFurnitureId() == objectId)
				{
					p.sendMessage("You cant reactivate");
					return;
				}
			}
			if(r.getType() == ConstructionData.GAMES_ROOM)
			{
				HouseFurniture pf = new HouseFurniture(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight()
						, 0, objectId, 0, 0);
				((House) p.getMapInstance()).getFurnitureActivated().add(pf);
				final NPC npc = World.getWorld().npcHandler.spawnNpc(p, getEleBalance(objectId), obX, obY, p.getHeight(), 0, 100, 0, 0, 0, false, false);
				p.getMapInstance().addNPC(npc);
				npc.animNumber = 3583;
				npc.animUpdateRequired = true;
				CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						npc.animNumber = 3584;
						npc.animUpdateRequired = true;
						container.stop();
					}

					@Override
					public void stop() {
						// TODO Auto-generated method stub

					}
				}, 3);
			}
			break;

		case 13392:
		case 13393:
		case 13394:
			myTiles =  Construction.getMyChunk(p);
			r = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			pfs = ((House) p.getMapInstance()).getActivatedObject(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight());
			for(HouseFurniture pp : pfs)
			{
				if(pp.getFurnitureId() == objectId)
				{
					p.sendMessage("You cant resetup");
					return;
				}
			}
			if(r.getType() == ConstructionData.GAMES_ROOM)
			{
				HouseFurniture pf = new HouseFurniture(myTiles[0]-1, myTiles[1]-1, p.inDungeon() ? 4 : p.getHeight()
						, 0, objectId, 0, 0);
				((House) p.getMapInstance()).getFurnitureActivated().add(pf);
				NPC npc = World.getWorld().npcHandler.spawnNpc(p, getAttackStone(objectId), obX, obY, p.getHeight(), 0, 100, 0, 0, 0, false, false);
				p.getMapInstance().addNPC(npc);
			}
			break;
		case 13200:
		case 13202:
		case 13204:
		case 13206:
		case 13208:
		case 13210:
		case 13212: {

			myTiles =  Construction.getMyChunk(p);
			if (!p.getItems().playerHasItem(590, 1)) {
				p.sendMessage("You need a tinderbox to do that");
				return;
			}
			if (!p.getItems().playerHasItem(251, 1)) {
				p.sendMessage("You need a marrentil to do that");
				return;
			}
			for (Player p1 : p.mapInstance.members) {
				p1.getPA().sendObject_cons(obX, obY, objectId + 1, 0, 10,
						p.getHeight());
			}
			final HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
					myTiles[1] - 1, (p.inDungeon() ? 4 : p.getHeight()), 1,
					objectId, 1, 1);
			((House) (p.mapInstance)).getLitBurners().add(pf);
			final House h = (House) p.mapInstance;
			p.getItems().deleteItem(251, 1);
			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					for (Player p1 : h.members) {
						p1.getPA().sendObject_cons(obX, obY,
								pf.getFurnitureId(), 0, 10,
								pf.getRoomZ() == 4 ? 0 : pf.getRoomZ());
						h.getLitBurners().remove(pf);
						container.stop();
					}

				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}
			}, 217);
		}
		break;
		case 13672:
		case 13673:
		case 13674:
			myTiles =  Construction.getMyChunk(p);
			if (myTiles == null)
				return;
			RoomData below = p.getMapInstance().getOwner().getHouseRooms()[4][myTiles[0] - 1][myTiles[1] - 1];
			if(below == null)
				return;
			if(below.getType() != ConstructionData.OUBLIETTE)
				return;
			for (Player p1 : p.getMapInstance().members) {
				int[] hisTiles = Construction.getMyChunk(p1);
				if (myTiles[0] == hisTiles[0] && myTiles[1] == hisTiles[1]
						&& p.getHeight() == p1.getHeight() && !p.inDungeon()) {
					xOnTile = Construction.getXTilesOnTile(myTiles, p1);
					yOnTile = Construction.getYTilesOnTile(myTiles, p1);
					if (xOnTile >= 3 && xOnTile <= 4 && yOnTile >= 3
							&& yOnTile <= 4) {
						p.toConsCoords = new int[] {3, 3};
						((House)p1.getMapInstance()).getDungeon().addMember(p1);

					}
				}
			}
			break;
		case 13328:
		case 13329:
		case 13330:
			myTiles =  Construction.getMyChunk(p);
			if (myTiles == null)
				return;
			RoomData above = p.getMapInstance().getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1];
			if(above.getType() != ConstructionData.OUBLIETTE)
				return;
			p.toConsCoords = new int[] {3, 3};
			((HouseDungeon) p.getMapInstance()).removePlayer(p);		

			break;
		case 13565:
			p.getDH().sendDialogues(433, 1);
			break;
		case 13566:
			p.getDH().sendDialogues(434, 1);
			break;
		case 13567:
			p.getDH().sendDialogues(435, 1);
			break;
		case 13545:
			p.getDH().sendDialogues(423, 1);
			break;
		case 13546:
			p.getDH().sendDialogues(424, 1);
			break;
		case 13547:
			p.getDH().sendDialogues(425, 1);
			break;
		case 13548:
			p.getDH().sendDialogues(426, 1);
			break;
		case 13549:
			p.getDH().sendDialogues(427, 1);
			break;
		case 13550:
			p.getDH().sendDialogues(429, 1);
			break;
		case 13551:
			p.getDH().sendDialogues(431, 1);
			break;

		case 13609:
		case 13611:
		case 13613:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			int rotation = p.getMapInstance().getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			HotSpots fireplace = null;
			RoomData myRoom = p.getMapInstance().getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1];
			System.out.println(myRoom.getType());
			if (myRoom.getType() == ConstructionData.PARLOUR)
				fireplace = HotSpots.PARLOUR_FIREPLACE;
			if (myRoom.getType() == ConstructionData.DINING_ROOM)
				fireplace = HotSpots.DINING_FIREPLACE;
			if (myRoom.getType() == ConstructionData.BEDROOM)
				fireplace = HotSpots.BEDROOM_FIREPLACE;
			p.getPA().sendObject_cons(obX, obY, objectId + 1,
					fireplace.getRotation(rotation), 10, p.getHeight());
			break;
		case 13678:
		case 13679:
		case 13680:
			myTiles =  Construction.getMyChunk(p);
			RoomData below_ = p.mapInstance.getOwner().Rooms[4][myTiles[0] - 1][myTiles[1] - 1];
			if (below_ == null) {
				if (p.inBuildingMode)
					p.getDH().sendDialogues(437, 1);
				else
					p.sendMessage("This room leads nowhere");
				break;
			}
			if (below_.getType() != ConstructionData.OUBLIETTE) {
				p.sendMessage("This trapdoor leads to a room without a trapdoor.");
				break;
			}

			p.toConsCoords = new int[] {1, 5};
			((House) p.mapInstance).getDungeon().addMember(p);
			break;
		case 13675:
		case 13676:
		case 13677:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			HotSpots hs = HotSpots.THRONE_ROOM_TRAPDOOR;
			int object = (objectId == 13675 ? 13678 : 13679);
			if (objectId == 13677)
				object = 13680;
			p.getPA().sendObject_cons(obX, obY, object,
					hs.getRotation(rotation), 22, p.getHeight());
			break;
			/**
			 * Throne room thrones
			 */
		case 13665:
		case 13666:
		case 13667:
		case 13668:
		case 13669:
		case 13670:
		case 13671:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();

			p.getPA().movePlayer(obX, obY, p.getHeight());

			Furniture f = Furniture.forFurnitureId(objectId);
			if (f == null)
				break;
			RoomData room = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1];
			int anim = 0;
			if (f == Furniture.OAK_THRONE) {
				anim = 4111;
			}
			if (f == Furniture.TEAK_THRONE) {
				anim = 4112;
			}
			if (f == Furniture.MAHOGANY_THRONE) {
				anim = 4113;
			}
			if (f == Furniture.GILDED_THRONE) {
				anim = 4114;
			}
			if (f == Furniture.SKELETON_THRONE) {
				anim = 4115;
			}
			if (f == Furniture.CRYSTAL_THRONE) {
				anim = 4116;
			}
			if (f == Furniture.DEMONIC_THRONE) {
				anim = 4117;
			}
			ArrayList<HotSpots> hsses = HotSpots
					.forObjectId_3(f.getHotSpotId());
			if (hsses.isEmpty())
				break;

			hs = null;
			if (hsses.size() == 1)
				hs = hsses.get(0);
			else {
				for (HotSpots find : hsses) {
					int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
					actualX += ConstructionData.getXOffsetForObjectId(
							find.getObjectId(), find, rotation);
					int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					actualY += ConstructionData.getYOffsetForObjectId(
							find.getObjectId(), find, rotation);
					if (obX == actualX && obY == actualY) {
						hs = find;
						break;
					}
				}
			}
			System.out.println(hs.toString());
			if (rotation == 0) {
				p.turnPlayerTo(obX, obY - 1);
			}
			if (rotation == 1) {
				p.turnPlayerTo(obX - 1, obY);
			}
			if (rotation == 2) {
				p.turnPlayerTo(obX, obY + 1);
			}
			if (rotation == 3) {
				p.turnPlayerTo(obX + 1, obY);
			}
			final int finalAnim2 = anim;
			p.resetChairAnim = false;
			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					p.startAnimation(finalAnim2);
					p.getPA().requestUpdates();
					if (p.resetChairAnim) {
						p.resetChairAnim = false;
						p.getPA().resetAnimation();
						p.getPA().requestUpdates();
						container.stop();
					}
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}
			}, 1);
			break;

		case 13300:
		case 13301:
		case 13302:
		case 13303:
		case 13304:
		case 13305:
		case 13306:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();

			p.getPA().movePlayer(obX, obY, p.getHeight());

			f = Furniture.forFurnitureId(objectId);
			if (f == null)
				break;
			room = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1];
			anim = 0;
			if (f == Furniture.WOODEN_BENCH) {
				anim = 4089;
			}
			if (f == Furniture.OAK_BENCH) {
				anim = 4091;
			}
			if (f == Furniture.CARVED_OAK_BENCH) {
				anim = 4093;
			}
			if (f == Furniture.TEAK_DINING_BENCH) {
				anim = 4095;
			}
			if (f == Furniture.CARVED_TEAK_DINING_BENCH) {
				anim = 4097;
			}
			if (f == Furniture.MAHOGANY_BENCH) {
				anim = 4099;
			}
			if (f == Furniture.GILDED_BENCH) {
				anim = 4101;
			}
			hsses = HotSpots.forObjectId_3(f.getHotSpotId());
			if (hsses.isEmpty())
				break;

			hs = null;
			if (hsses.size() == 1)
				hs = hsses.get(0);
			else {
				for (HotSpots find : hsses) {
					int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
					actualX += ConstructionData.getXOffsetForObjectId(
							find.getObjectId(), find, rotation);
					int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					actualY += ConstructionData.getYOffsetForObjectId(
							find.getObjectId(), find, rotation);
					if (obX == actualX && obY == actualY) {
						hs = find;
						break;
					}
				}
			}
			System.out.println(hs.toString());
			boolean b = room.getType() == ConstructionData.DINING_ROOM ? (hs == HotSpots.DINING_SEATING_1
					|| hs == HotSpots.DINING_SEATING_2
					|| hs == HotSpots.DINING_SEATING_3 || hs == HotSpots.DINING_SEATING_4)
					: (hs == HotSpots.THRONE_BENCH_1
					|| hs == HotSpots.THRONE_BENCH_2
					|| hs == HotSpots.THRONE_BENCH_3
					|| hs == HotSpots.THRONE_BENCH_4 || hs == HotSpots.THRONE_BENCH_5);
			if (rotation == 0) {
				if (b)
					p.turnPlayerTo(
							obX
							+ (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0),
							obY
							+ (room.getType() == ConstructionData.PARLOUR ? 1
									: 0));
				else
					p.turnPlayerTo(
							obX
							- (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0),
							obY
							- (room.getType() == ConstructionData.PARLOUR ? 1
									: 0));
			}
			if (rotation == 1) {
				if (b)
					p.turnPlayerTo(
							obX
							- (room.getType() == ConstructionData.PARLOUR ? 1
									: 0),
							obY
							- (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0));
				else
					p.turnPlayerTo(
							obX
							+ (room.getType() == ConstructionData.PARLOUR ? 1
									: 0),
							obY
							+ (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0));
			}
			if (rotation == 2) {
				if (b)
					p.turnPlayerTo(
							obX
							- (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0),
							obY
							- (room.getType() == ConstructionData.PARLOUR ? 1
									: 0));
				else
					p.turnPlayerTo(
							obX
							+ (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0),
							obY
							+ (room.getType() == ConstructionData.PARLOUR ? 1
									: 0));
			}
			if (rotation == 3) {
				if (b)
					p.turnPlayerTo(
							obX
							+ (room.getType() == ConstructionData.PARLOUR ? 1
									: 0),
							obY
							+ (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0));
				else
					p.turnPlayerTo(
							obX
							- (room.getType() == ConstructionData.PARLOUR ? 1
									: 0),
							obY
							- (room.getType() == ConstructionData.THRONE_ROOM ? 1
									: 0));
			}
			final int finalAnim1 = anim;
			p.resetChairAnim = false;
			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					p.startAnimation(finalAnim1);
					p.getPA().requestUpdates();
					if (p.resetChairAnim) {
						p.resetChairAnim = false;
						p.getPA().resetAnimation();
						p.getPA().requestUpdates();
						container.stop();
					}
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}
			}, 1);
			break;

		case 13610:
		case 13612:
		case 13614:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			fireplace = null;
			myRoom = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1];
			if (myRoom.getType() == ConstructionData.PARLOUR)
				fireplace = HotSpots.PARLOUR_FIREPLACE;
			if (myRoom.getType() == ConstructionData.DINING_ROOM)
				fireplace = HotSpots.DINING_FIREPLACE;
			if (myRoom.getType() == ConstructionData.BEDROOM)
				fireplace = HotSpots.BEDROOM_FIREPLACE;
			p.getPA().sendObject_cons(obX, obY, objectId + 1,
					fireplace.getRotation(rotation), 10, p.getHeight());
			break;
		case 13581:
		case 13582:
		case 13583:
		case 13584:
		case 4517:
		case 4515:
		case 4516:
			myTiles =  Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();

			p.getPA().movePlayer(obX, obY, p.getHeight());

			f = Furniture.forFurnitureId(objectId);
			if (f == null)
				break;
			int roomRot = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			room = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1];
			hsses = HotSpots.forObjectId_3(f.getHotSpotId());
			if (hsses.isEmpty())
				break;

			hs = null;
			if (hsses.size() == 1)
				hs = hsses.get(0);
			else {
				for (HotSpots find : hsses) {
					int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
					actualX += ConstructionData.getXOffsetForObjectId(
							find.getObjectId(), find, roomRot);
					int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
					actualY += ConstructionData.getYOffsetForObjectId(
							find.getObjectId(), find, roomRot);
					if (obX == actualX && obY == actualY) {
						hs = find;
						break;
					}
				}
			}

			anim = 0;
			if (f == Furniture.CRUDE_WOODEN_CHAIR) {
				anim = 4073;
			}
			if (f == Furniture.WOODEN_CHAIR) {
				anim = 4075;
			}
			if (f == Furniture.ROCKING_CHAIR) {
				anim = 4079;
			}
			if (f == Furniture.OAK_CHAIR) {
				anim = 4081;
			}
			if (f == Furniture.OAK_ARMCHAIR) {
				anim = 4083;
			}
			if (f == Furniture.TEAK_ARMCHAIR) {
				anim = 4085;
			}
			if (f == Furniture.MAHOGANY_ARMCHAIR) {
				anim = 4087;
			}
			System.out.println(hs.toString());
			if (hs == HotSpots.PARLOUR_CHAIR_2) {
			} else {
				anim++;
			}
			if (rotation == 0) {
				if (hs == HotSpots.PARLOUR_CHAIR_1)
					p.turnPlayerTo(obX, obY + 1);
				if (hs == HotSpots.PARLOUR_CHAIR_2)
					p.turnPlayerTo(obX, obY + 1);
				if (hs == HotSpots.PARLOUR_CHAIR_3)
					p.turnPlayerTo(obX - 1, obY + 1);
			}
			if (rotation == 1) {
				if (hs == HotSpots.PARLOUR_CHAIR_1)
					p.turnPlayerTo(obX + 1, obY);
				if (hs == HotSpots.PARLOUR_CHAIR_2)
					p.turnPlayerTo(obX + 1, obY);
				if (hs == HotSpots.PARLOUR_CHAIR_3)
					p.turnPlayerTo(obX + 1, obY);
			}
			if (rotation == 2) {
				if (hs == HotSpots.PARLOUR_CHAIR_1)
					p.turnPlayerTo(obX, obY - 1);
				if (hs == HotSpots.PARLOUR_CHAIR_2)
					p.turnPlayerTo(obX, obY - 1);
				if (hs == HotSpots.PARLOUR_CHAIR_3)
					p.turnPlayerTo(obX, obY - 1);
			}
			if (rotation == 3) {
				if (hs == HotSpots.PARLOUR_CHAIR_1)
					p.turnPlayerTo(obX + 1, obY);
				if (hs == HotSpots.PARLOUR_CHAIR_2)
					p.turnPlayerTo(obX + 1, obY);
				if (hs == HotSpots.PARLOUR_CHAIR_3)
					p.turnPlayerTo(obX + 1, obY);
			}
			final int finalAnim = anim;
			p.resetChairAnim = false;
			CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					p.startAnimation(finalAnim);
					p.getPA().requestUpdates();
					if (p.resetChairAnim) {
						p.resetChairAnim = false;
						p.getPA().resetAnimation();
						p.getPA().requestUpdates();
						container.stop();
					}
				}

				@Override
				public void stop() {
					// TODO Auto-generated method stub

				}
			}, 1);
			break;
		case 13597:
		case 13598:
		case 13599:
			p.sendMessage("You find no interesting books. And you wond really want to read do ya :D");
			break;
		case 15306:
		case 15307:
			myTiles = Construction.getMyChunkFor(obX, obY);
			xOnTile = Construction.getXTilesOnTile(myTiles, p);
			yOnTile = Construction.getYTilesOnTile(myTiles, p);
			int direction = 0;
			final int LEFT = 0,
					DOWN = 1,
					RIGHT = 2,
					UP = 3;
			if (xOnTile == 0)
				direction = LEFT;
			if (yOnTile == 0)
				direction = DOWN;
			if (xOnTile == 7)
				direction = RIGHT;
			if (yOnTile == 7)
				direction = UP;
			if (direction == LEFT || direction == RIGHT) {
				p.getPA().sendObject_cons(obX, obY, objectId,
						direction == LEFT ? 3 : 1, 0, p.getHeight());
				if (objectId == (direction == LEFT ? 15306 : 15305)) {
					p.getPA().sendObject_cons(obX, obY - 1, objectId,
							direction == LEFT ? 3 : 1, 0, p.getHeight());
				} else {
					p.getPA().sendObject_cons(obX, obY + 1,
							objectId + (objectId == 15306 ? -1 : 0),
							direction == LEFT ? 3 : 1, 0, p.getHeight());
				}
			}
			if (direction == UP || direction == DOWN) {
				p.getPA().sendObject_cons(obX, obY, objectId,
						direction == UP ? 0 : 2, 0, p.getHeight());
				if (objectId == (direction == UP ? 15306 : 15305)) {
					p.getPA().sendObject_cons(obX + 1, obY, objectId,
							direction == UP ? 2 : 0, 0, p.getHeight());
				} else {
					p.getPA().sendObject_cons(obX - 1, obY,
							objectId + (objectId == 15306 ? -1 : 0),
							direction == UP ? 2 : 0, 0, p.getHeight());
				}
			}
			break;
		case 13409:
			myTiles =  Construction.getMyChunk(p);
			room = p.mapInstance.getOwner().Rooms[4][myTiles[0] - 1][myTiles[1] - 1];
			if (room == null) {
				p.getDH().sendDialogues(418, 1);
			} else if (room.getType() == ConstructionData.DUNGEON_STAIR_ROOM) {

				p.toConsCoords = new int[] {2, 3};
				(p.mapInstance instanceof House ? ((House)p.mapInstance).getDungeon() : ((HouseDungeon)p.mapInstance)).addMember(p);
			} else {
				p.getDH().sendDialogues(420, 1);
			}
			break;
		case 13497:
		case 13499:
		case 13501:
		case 13503:
		case 13505:
			myTiles =  Construction.getMyChunk(p);
			if (!p.inDungeon()) {
				room = p.mapInstance.getOwner().Rooms[1][myTiles[0] - 1][myTiles[1] - 1];
				if (room == null) {
					p.getDH().sendDialogues(413, 1);
				} else if (room.getType() == ConstructionData.SKILL_HALL_DOWN
						|| room.getType() == ConstructionData.QUEST_HALL_DOWN) {

					int[] converted = Construction.getConvertedCoords(3, 5, myTiles, room);
					p.getPA().movePlayer(converted[0], converted[1], 1);
				} else {
					p.getDH().sendDialogues(417, 1);
				}
			} else {
				room = p.mapInstance.getOwner().Rooms[myTiles[0] - 1][myTiles[1] - 1][0];
				if (room.getType() != ConstructionData.GARDEN
						|| room.getType() != ConstructionData.FORMAL_GARDEN) {

					((HouseDungeon) p.mapInstance).removePlayer(p);
				} else {
					p.getDH().sendDialogues(417, 1);
				}
			}
			break;
		case 13498:
		case 13500:
		case 13502:
		case 13504:
		case 13506:
			myTiles =  Construction.getMyChunk(p);
			room = p.mapInstance.getOwner().Rooms[myTiles[0] - 1][myTiles[1] - 1][0];
			if (room.getType() == ConstructionData.EMPTY) {
				p.getDH().sendDialogues(415, 1);
			} else if (room.getType() == ConstructionData.SKILL_ROOM
					|| room.getType() == ConstructionData.QUEST_ROOM) {

				int[] converted = Construction.getConvertedCoords(3, 2, myTiles, room);
				p.getPA().movePlayer(converted[0], converted[1], 0);
			} else {
				p.getDH().sendDialogues(417, 1);
			}
			break;
		}
	}

	public static void handleSecondObjectClick(final int obX, final int obY,
			int objectId, final Player p) {
		switch (objectId) {

		case 13326:
		case 13323:
		case 13320:
		case 13317:
		case 13314:
			int[] myTiles = Construction.getMyChunk(p);
			if(myTiles == null)
				return;
			RoomData r = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.OUBLIETTE)
				return;
			p.sendMessage("You attempt to pick-lock the door...");

			break;
		case 13347:
		case 13346:
		case 13344:
		case 13345:
		case 13348:
		case 13349:
			myTiles = Construction.getMyChunk(p);
			r = p.mapInstance.getOwner().Rooms[4][myTiles[0]-1][myTiles[1]-1];
			if(r.getType() != ConstructionData.CORRIDOR
					&& r.getType() != ConstructionData.DUNGEON_STAIR_ROOM
					&& r.getType() != ConstructionData.TREASURE_ROOM)
				break;
			p.sendMessage("It's locked");

			break;
		case 13405:
			if(p.mapInstance == null)
				return;
			if(!(p.mapInstance instanceof House))
				return;
			House mi = (House)p.mapInstance;
			if(mi.getOwner() == p)
			{
				mi.setLocked(!mi.isLocked());
				p.sendMessage("House "+(mi.isLocked() ? "" : "un")+"locked");
			} else {
				p.sendMessage("You need to be the owner of this house to lock it");
			}
			break;
		case 13678:
		case 13679:
		case 13680:
			myTiles = Construction.getMyChunkFor(obX, obY);
			if (myTiles == null)
				break;
			int rotation = p.mapInstance.getOwner().Rooms[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
					.getRotation();
			HotSpots hs = HotSpots.THRONE_ROOM_TRAPDOOR;
			int object = (objectId == 13677 ? 13675 : 13676);
			if (objectId == 13677)
				object = 13679;
			p.getPA().sendObject_cons(obX, obY, object,
					hs.getRotation(rotation), 22, p.getHeight());
			break;
		}
	}

	public static void handleFirstItemClick(Player p, int itemId)
	{
		if(itemId == 7677)
		{
			if(p.mapInstance == null || !(p.mapInstance instanceof House))
			{
				p.getItems().deleteItem(7677, 1);
				return;
			}
		}
	}
	public static boolean handleFirstNpcClick(Player p, int npcId, int x, int y)
	{
		NPC npc = World.getWorld().npcHandler.npcs[p.npcClickIndex];
		if(World.getWorld().npcHandler.npcs[p.npcClickIndex] == null)
			return false;
		int npcX = x;	
		int npcY = y;
		Butlers b = Butlers.forId(npcId);
		if(b != null)
		{
			if(!(p.mapInstance instanceof House || p.mapInstance instanceof HouseDungeon))
				p.getDH().sendDialogues(443, b.getNpcId());
			else{
				p.getDH().sendDialogues((p.servantCharges == 0 && p.mapInstance.getOwner() == p)  ? 456 : 451, b.getNpcId());
				if(p.mapInstance.getOwner() == p)
					((Servant) npc).giveItems(p);
			}
		}
		switch(npcId)
		{
		case 3944:
			if(npc.hangManStatus == 3953)
			{
				npc.requestTransform(3944);
				npc.hangManAnswer = ConstructionData.HANGMANWORDS[Misc.random(ConstructionData.HANGMANWORDS.length-1)];
				npc.hangManStatus = 3944;
				npc.currentHangMan = "";
				for(int i = 0; i < npc.hangManAnswer.length(); i++)
				{
					npc.currentHangMan += "-";
				}
			} else {
				//p.getPA().commandFrame(1);
			}
			return true;
		case 4097:
		case 3957:
		case 4162:
			if(p == npc.lastAttackerStone && p.mapInstance.members.size() > 1)
			{
				p.sendMessage("You can't attack the stone twice in a row");
				return true;
			}
			int anim = p.getCombat().getWepAnim(p
					.getItems()
					.getItemName(p.playerEquipment[p.playerWeapon])
					.toLowerCase());
			p.startAnimation(anim);
			//p.getCombat().applyPlayerMeleeDamage(int i, int damageMask, int damage, Hitmark hitmark);
			//p.getCombat().appendHit(npc, 0+Misc.random(4), CombatType.MELEE, 2);
			npc.lastAttackerStone = p;
			return true;

		case 3955:
			House house = (House)(npc.mapInstance);
			final int myTiles[] = Construction.getMyChunkFor(npcX, npcY);
			ArrayList<HouseFurniture> pfs = house.getActivatedObject(myTiles[0]-1, myTiles[1]-1, npc.inDungeon() ? 4 : npc.getHeight());
			for(HouseFurniture pf : pfs)
			{
				if(pf.getFurnitureId() == 13390)
					house.getFurnitureActivated().remove(pf);
			}

			return true;
		default:

			return false;
		}
	}
	private static int getAttackStone(int object)
	{
		switch(object)
		{
		case 13392:
			return 4097;
		case 13393:
			return 3957;
		case 13394:
			return 4162;
		}
		return -1;
	}
	private static int getEleBalance(int object)
	{
		switch(object)
		{
		case 13395:
			return 4021;
		case 13396:
			return 4046;
		case 13397:
			return 4071;
		}
		return -1;
	}
	public static void handleSecondNpcClick(final NPC npc, final Player p)
	{
		int npcId = npc.npcType;
		int npcX = npc.getX();
		int npcY = npc.getY();
		if(npcId == 3944)
		{
			House house = (House)(npc.mapInstance);
			int myTiles[] = Construction.getMyChunkFor(npcX, npcY);
			ArrayList<HouseFurniture> pfs = house.getActivatedObject(myTiles[0]-1, myTiles[1]-1, npc.inDungeon() ? 4 : npc.getHeight());
			for(HouseFurniture pf : pfs)
			{
				if(pf.getFurnitureId() == 13404)
					house.getFurnitureActivated().remove(pf);
			}
		}
		if(npcId >= 4021 && npcId <= 4095)
		{
			House house = (House)(npc.mapInstance);
			int myTiles[] = Construction.getMyChunkFor(npcX, npcY);
			ArrayList<HouseFurniture> pfs = house.getActivatedObject(myTiles[0]-1, myTiles[1]-1, npc.inDungeon() ? 4 : npc.getHeight());
			for(HouseFurniture pf : pfs)
			{
				if(pf.getFurnitureId() == 13395 || pf.getFurnitureId() == 13396
						|| pf.getFurnitureId() == 13397)
					house.getFurnitureActivated().remove(pf);
			}

		}
	}
	private final static int[] EMOTES = new int[] 
			{
					855, 856, 857, 858, 859, 860, 861, 862, 864,
					865, 866, 2105, 2106, 2107, 2108, 2109, 2110, 2111,
					2112, 2113, 1374, 0x46b, 0x46a, 0x469, 0x468,
					0x84F, 0x850, 2836, 3544, 3543, 6111
			};
	public static void handleJester(Player p, int anim)
	{
		int[] myTiles = Construction.getMyChunk(p);
		if(myTiles == null)
			return;
		if(myTiles[0] == -1 || myTiles[1] == -1)
			return;
		RoomData r = p.mapInstance.getOwner().Rooms[myTiles[0]-1][myTiles[1]-1][p.inDungeon() ? 4 : p.getHeight()];
		if(r == null)
			return;
		if(r.getType() != ConstructionData.GAMES_ROOM)
			return;
		NPC npc = null;
		for(NPC npc_1 : p.mapInstance.npcs)
		{
			if(npc_1 == null)
				continue;
			int[] tiles = Construction.getMyChunkFor(npc_1.getX(), npc_1.getY());
			if(tiles == null)
				return;
			if(tiles[1] == myTiles [1] && tiles[0] == myTiles[0] && p.getHeight() == npc_1.getHeight()
					&& npc_1.npcType == 3955)
			{
				npc = npc_1;
				break;
			}
		}
		if(npc == null)
			return;
		if(anim == npc.jesterAnim)
		{
			if(p.jesterEmotes == 9)
			{
				p.jesterEmotes = 0;
				npc.forceChat(p.playerName+" won!");
			} else {
				p.jesterEmotes++;
			}
		}
	}
	public static void handleHangman(Player p, String s)
	{

		int[] myTiles = Construction.getMyChunk(p);
		RoomData r = p.mapInstance.getOwner().Rooms[myTiles[0]-1][myTiles[1]-1][p.inDungeon() ? 4 : p.getHeight()];
		if(r.getType() != ConstructionData.GAMES_ROOM)
			return;

		NPC npc = null;
		for(NPC npc_1 : p.mapInstance.npcs)
		{
			int[] tiles = Construction.getMyChunkFor(npc_1.getX(), npc_1.getY());
			if(tiles[1] == myTiles [1] && tiles[0] == myTiles[0] && p.getHeight() == npc_1.getHeight()
					&& npc_1.npcType == 3944)
			{
				npc = npc_1;
				break;
			}
		}
		if(npc == null)
			return;
		p.sendMessage(s);
		s = s.toLowerCase();
		String answer = npc.hangManAnswer.toLowerCase();
		String ss = "";
		int correct = 0;
		for(int i = 0; i < answer.length(); i++)
		{
			if(!npc.currentHangMan.substring(i, i+1).equals("-"))
			{
				ss += npc.currentHangMan.charAt(i);
			} else
				if(s.equals(answer.substring(i, i+1)))
				{
					ss += s;
					correct++;
				} else {
					ss += "-";
				}
		}
		p.sendMessage(npc.currentHangMan);
		npc.currentHangMan = ss;
		if(correct == 0)
		{
			if(npc.hangManStatus == 3953)
			{
				npc.forceChat("Fail you lost lol");
				return;
			}
			npc.requestTransform(npc.hangManStatus+1);
			npc.hangManStatus++;
		} else {
			if(!npc.currentHangMan.contains("-"))
			{
				npc.forceChat("You won!");
				npc.requestTransform(3944);
				npc.hangManAnswer = ConstructionData.HANGMANWORDS[Misc.random(ConstructionData.HANGMANWORDS.length-1)];
				npc.hangManStatus = 3944;
				npc.currentHangMan = "";
				for(int i = 0; i < npc.hangManAnswer.length(); i++)
				{
					npc.currentHangMan += "-";
				}
				return;
			}
		}
		npc.forceChat(npc.currentHangMan);
	}//house.objects.add(new RoomObject(objId, objX, objY, objZ, objType, objFace));


}