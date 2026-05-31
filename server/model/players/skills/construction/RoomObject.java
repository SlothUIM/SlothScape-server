package server.model.players.skills.construction;
import server.Config;
import server.Server;
import server.model.items.Item;
import server.model.items.ItemAssistant;
import server.model.items.ItemList;
import server.model.players.skills.construction.Butlers;
import server.model.npcs.NPC;
import server.model.players.skills.construction.Servant;
import server.model.objects.Object;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.construction.House;
import server.model.players.skills.construction.HouseDungeon;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.objects.Object;
import server.world.ItemHandler;
import server.world.ObjectHandler;
import server.world.World;
import server.model.players.skills.construction.furniture.Furniture;
import server.model.players.skills.construction.util.HouseFurniture;
import server.model.players.skills.construction.util.POHPalette;
import server.model.players.skills.construction.util.Portal;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.skills.construction.util.POHPalette.POHPaletteTile;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.Iterator;

public class RoomObject {


			

	public static boolean roomExists(Player p) {
		int[] myTiles = Construction.getMyChunk(p);
		int xOnTile = Construction.getXTilesOnTile(myTiles, p);
		int yOnTile = Construction.getYTilesOnTile(myTiles, p);
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
		RoomData room = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]
				- 1 + xOff][myTiles[1] - 1 + yOff];
		if (room == null)
			return false;
		if (room.getType() == ConstructionData.BUILDABLE
				|| room.getType() == ConstructionData.EMPTY
				|| room.getType() == ConstructionData.DUNGEON_EMPTY)
			return false;
		return true;
	}
	public static boolean handleSpaceClick(int obX, int obY, int objectId,
			Player p) {

		int[] myTiles = Construction.getMyChunk(p);
		int roomRot = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0] - 1][myTiles[1] - 1].getRotation();

		ArrayList<HotSpots> hsses = HotSpots
				.forObjectId_2(objectId);
		if (hsses.isEmpty())
			return false;

		p.buildFurnitureX = obX;
		p.buildFurnitureY = obY;

		//System.out.println("p.buildFurnitureX: "+p.buildFurnitureX);
		//System.out.println("p.buildFurnitureY: "+p.buildFurnitureY);
		p.buildFurnitureId = objectId;
		HotSpots hs = null;
		int myRoom = p.getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0]-1][myTiles[1]-1].getType();
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
				System.out.println("handleSpaceClick-X: "+actualX);
				System.out.println("handleSpaceClick-Y: "+actualY);
				if (p.buildFurnitureX == actualX
						&& p.buildFurnitureY == actualY
						&& myRoom == find.getRoomType()
						|| find.getCarpetDim() != null && myRoom == find.getRoomType()) {
					hs = find;
					break;
				}
			}
		}
		if (hs == null)
			return true;
		ArrayList<Furniture> f = Furniture.getForHotSpotId(hs
				.getHotSpotId());
		//System.out.println("p.getHotSpotId: "+hs
				//.getHotSpotId());
		if (f == null)
			return false;
		handleInterfaceItems(f, p);
		handleInterfaceCrosses(f, p, hs);
		p.getPA().showInterface(403);
		return true;
	}
	
	public static boolean handleObjectFourthOption(int obX, int obY, int objectId,
			Player p) {

		if (p.getMapInstance().getOwner() != p)
			return false;
		if (!p.isBuildingMode())
			return false;
		if (handleSpaceClick(obX, obY, objectId, p))
			return true;
		if (handleRemoveClick(obX, obY, objectId, p))
			return true;
		for (int i : ConstructionData.DOORSPACEIDS) {
			if (objectId == i) {
				if (!roomExists(p)) {
					BuildARoom(p);
					return true;
				} else {
					p.getDH().sendDialogues(642, 1);
				}
			}
		}
		return false;
	}
	public static void handleObjectFifthOption(Player player, int objectId, int objx, int objy){

		for (int i : ConstructionData.DOORSPACEIDS) {
			if (objectId == i) {
				if (!roomExists(player)) {
					BuildARoom(player);
					return;
				} else {
					player.getDH().sendDialogues(642, 1);
				}
			}
		}

		if (player.getMapInstance().getOwner() != player)
			return;
		if (!player.isBuildingMode())
			return;
		if (handleSpaceClick(objx, objy, objectId, player))
			return;
		if (handleRemoveClick(objx, objy, objectId, player))
			return;
		switch(objectId){
		case 13497:
		case 13499:
		case 13501:
		case 13503:
		case 13505:
			int myTiles[] = Construction.getMyChunk(player);
			RoomData room = player.getMapInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
			if (room == null) {
				player.sendMessage("These stairs lead nowhere.");
			} else {
				player.getMapInstance().getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1] = null;
				Construction.updatePalette(player);
				player.getPA().sendConstructedMapPOH(player.getMapInstance().getPalette());
			}
			
			break;
		/*case 15390:
			/*int myTiles1[] = Construction.getMyChunk(player);
			 room = player.mapInstance.getOwner().Rooms[myTiles1[0] - 1][myTiles1[1] - 1][1];
			 RoomData room_1 = player.mapInstance.getOwner().Rooms[myTiles1[0] - 1][myTiles1[1] - 1][0];
				if (room != null) {
			player.sendMessage("You did something retarded and now there is an above you for some reason.");
			player.getPA().closeAllWindows();
		} else {
			HouseData.createRoom(
					room_1.getType() == ConstructionData.SKILL_ROOM ? ConstructionData.SKILL_HALL_DOWN
							: ConstructionData.QUEST_HALL_DOWN, player, 100);
			player.getPA().closeAllWindows();
		}

		break;*/
		//case 15366:
	
		default:


			Furniture spot = Furniture.forFurnitureId(objectId);
			//HotSpots spot2 = HotSpots.forObjectId(objectId);
			if (spot == null)
				break;
			if (player.getMapInstance().getOwner() != player)
				break;
			if (!player.isBuildingMode())
				break;
			HotSpots hs = HotSpots.forHotSpotId(spot.getHotSpotId());
			handleSpaceClick(objx, objy, hs.getObjectId(), player);
			System.out.println("HotSpot " +spot.getHotSpotId());

			/*Furniture f2 = Furniture.forFurnitureId(objectId);
			if (f2 == null)
				break;
			if (player.mapInstance.getOwner() != player)
				break;
			if (!player.inBuildingMode)
				break;
			handleRemoveClick(objx, objy, objectId, player);*/
			break;
		}
	}
	public static boolean handleRemoveClick(int obX, int obY, int objectId,
			Player p) {
		if (objectId == 13126 || objectId == 13127 || objectId == 13128
				|| objectId == 13132)
			objectId = 13126;
		if (objectId == 13133 || objectId == 13134 || objectId == 13135
				|| objectId == 13136)
			objectId = 13133;
		if (objectId == 13137 || objectId == 13138 || objectId == 13139
				|| objectId == 13140)
			objectId = 13137;
		if (objectId == 13145 || objectId == 13147)
			objectId = 13145;
		if (objectId == 13142 || objectId == 13143 || objectId == 13144)
			objectId = 13142;
		if (objectId == 13588 || objectId == 13589 || objectId == 13590)
			objectId = 13588;
		if (objectId == 13591 || objectId == 13592 || objectId == 13593)
			objectId = 13591;
		if (objectId == 13594 || objectId == 13595 || objectId == 13596)
			objectId = 13594;
		if (objectId > 13456 && objectId <= 13476)
			objectId = 13456;
		if (objectId > 13449 && objectId <= 13455)
			objectId = 13449;
		if (objectId > 13331 && objectId <= 13337 || objectId == 13373)
			objectId = 13331;
		if (objectId > 13313 && objectId <= 13327)
			objectId = 13313;

		Furniture f = Furniture.forFurnitureId(objectId);
		if (f == null)
			return false;
		if (f == Furniture.EXIT_PORTAL || f == Furniture.EXIT_PORTAL_) {
			int portalAmt = 0;
			for (HouseFurniture pf : p.houseFurniture) {
				Furniture ff = Furniture.forFurnitureId(pf.getFurnitureId());
				if (ff == Furniture.EXIT_PORTAL || ff == Furniture.EXIT_PORTAL_)
					portalAmt++;
			}
			if (portalAmt < 2) {
				p.sendMessage("You need atleast 1 exit portal in your house");
				return true;
			}
		}
		int[] myTiles = Construction.getMyChunk(p);
		int roomRot = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0] - 1][myTiles[1] - 1].getRotation();
		RoomData room = p.getMapInstance().getOwner().getHouseRooms()[p.inDungeon() ? 4 : p.getLocation().getZ()][myTiles[0] - 1][myTiles[1] - 1];
		ArrayList<HotSpots> hsses = HotSpots.forObjectId_3(f
				.getHotSpotId());
		if (hsses.isEmpty())
			return false;

		HotSpots hs = null;
		if (hsses.size() == 1)
			hs = hsses.get(0);
		else {
			for (HotSpots find : hsses) {
			    // Standard Chunk calculation
			    int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
			    int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
			    
			    // Add the rotated offset
			    actualX += ConstructionData.getXOffsetForObjectId(find.getObjectId(), find.xOffset, find.yOffset, roomRot, find.getRotation(0));
			    actualY += ConstructionData.getYOffsetForObjectId(find.getObjectId(), find.xOffset, find.yOffset, roomRot, find.getRotation(0));
			    
			    if (obX == actualX && obY == actualY) {
			        hs = find;
			        break;
			    }
			}
		}
		if (objectId == 13331) {
			hs = HotSpots.OUBLIETTE_FLOOR_1;
		}
		if (objectId == 13313) {
			hs = HotSpots.OUBLIETTE_CAGE_1;
		}
		if (objectId == 13126 || objectId == 13127 || objectId == 13128
				|| objectId == 13132 || objectId == 13133 || objectId == 13134
				|| objectId == 13135 || objectId == 13136 || objectId == 13137
				|| objectId == 13138 || objectId == 13139 || objectId == 13140
				|| objectId == 13145 || objectId == 13147 || objectId == 13142
				|| objectId == 13143 || objectId == 13144) {
			hs = HotSpots.COMBAT_RING_1;
		}
		if (objectId == 13456)
			if (room.getType() == ConstructionData.FORMAL_GARDEN)
				hs = HotSpots.FORMAL_HEDGE_1;
		if (objectId == 13449)
			if (room.getType() == ConstructionData.FORMAL_GARDEN)
				hs = HotSpots.FORMAL_FENCE;
		if (objectId == 15272 || objectId == 15273 || objectId == 15274
				|| objectId >= 6759 && objectId <= 6767) {
			if (room.getType() == ConstructionData.CHAPEL)
				hs = HotSpots.CHAPEL_RUG_2;
			if (room.getType() == ConstructionData.PARLOUR)
				hs = HotSpots.PARLOUR_RUG_3;
			if (room.getType() == ConstructionData.SKILL_ROOM
					|| room.getType() == ConstructionData.SKILL_HALL_DOWN
					|| room.getType() == ConstructionData.QUEST_ROOM
					|| room.getType() == ConstructionData.QUEST_HALL_DOWN
					|| room.getType() == ConstructionData.DUNGEON_STAIR_ROOM
					|| room.getType() == ConstructionData.SKILL_HALL_DOWN)
				hs = HotSpots.SKILL_HALL_RUG_3;
			if (room.getType() == ConstructionData.BEDROOM)
				hs = HotSpots.BEDROOM_RUG_3;
		}
		HouseData.doFurniturePlace(hs, f, hsses, myTiles, obX, obY, roomRot, p, true,
				p.getHeight());
		p.startAnimation(3685);
		Iterator<HouseFurniture> iterator = p.getHouseFurniture().iterator();
		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() != myTiles[0] - 1
					|| pf.getRoomY() != myTiles[1] - 1
					|| pf.getRoomZ() != (p.inDungeon() ? 4 : p.getLocation().getZ()))
				continue;
			if (pf.getStandardXOff() == hs.getXOffset()
					&& pf.getStandardYOff() == hs.getYOffset())
				iterator.remove();
		}
		return true;
	}
	public static String hasReqs(Player p, Furniture f, HotSpots hs)
	{

		if(p.playerRights == 3)
			return null;
		if(p.getSkills().getLevel(Skill.CONSTRUCTION) < f.getLevel())
		{
			return "You need a construction level of "+f.getLevel()+" to build this.";
		}
		for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
			if (!p.getItems().playerHasItem(f.getRequiredItems()[i1][0],
					f.getRequiredItems()[i1][1])) {
				return "You need "+f.getRequiredItems()[i1][1]+" x "+p.getItems().getItemName(f.getRequiredItems()[i1][0]);
			}
		}
		

		if (f.getAdditionalSkillRequirements() != null) {
			for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
				if (p.playerLevel[f.getAdditionalSkillRequirements()[ii][0]] < f
						.getAdditionalSkillRequirements()[ii][1]) {
					return "You need a "+Config.SKILL_NAME[f.getAdditionalSkillRequirements()[ii][0]]+" of "+f.getAdditionalSkillRequirements()[ii][1]+""
							+ " to build this";
				}
			}
		}

		if (f.getFurnitureRequired() != -1) {
			Furniture fur = Furniture.forFurnitureId(f.getFurnitureRequired());
			int[] myTiles = Construction.getMyChunk(p);
			for (HouseFurniture pf : p.houseFurniture) {
				if (pf.getRoomX() == myTiles[0] - 1 && pf.getRoomY() == myTiles[1] - 1) {
					if (pf.getHotSpot(p.getHouseRooms()[p.inDungeon() ? 4 : p.getHeight()][myTiles[0] - 1][myTiles[1] - 1]
							.getRotation()) == hs) {
						if (pf.getFurnitureId() != fur.getFurnitureId()) {
							return "This is an upgradeable piece of furniture. (build the furniture before this first)";
						}
					}
				}
			}
		}
		
		return null;
	}
	public static boolean buildActions(Player p, Furniture f, HotSpots hs)
	{
		String s = hasReqs(p, f, hs);
		if(s != null) {
			p.sendMessage(s);
			return false;
		}
		for (int i = 0; i < f.getRequiredItems().length; i++) {
			ItemList item = World.getWorld().itemHandler.getItemList(f.getRequiredItems()[i][0]);
			if(item.stackable)
				p.getItems().deleteItem(f.getRequiredItems()[i][0], f.getRequiredItems()[i][1]);
			else {
				for(int a = 0; a < f.getRequiredItems()[i][1]; a++)
				{
					p.getItems().deleteItem(f.getRequiredItems()[i][0], 1);
				}
			}
		}
		p.getPA().addSkillXP(f.getXP(), Skill.CONSTRUCTION.getId());
		return true;
	}
	public static void handleItemClick(int itemID, Player p) {
		if (p.getMapInstance().getOwner() != p)
			return;
		if (!p.isBuildingMode()) {
			p.sendMessage("You are not in building mode!");
			return;
		}
		Furniture f = Furniture.forItemId(itemID);
		if (f == null)
			return;

		ArrayList<HotSpots> hsses = HotSpots
				.forObjectId_2(p.buildFurnitureId);
		if (hsses.isEmpty()) {
			p.sendMessage("Empty!");
			return;
		}

		int[] myTiles = Construction.getMyChunk(p);
		int toHeight = (p.getMapInstance() instanceof HouseDungeon ? 4 : p.getLocation().getZ());
		int roomRot = p.getMapInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1]
				.getRotation();
		int myRoomType = p.getMapInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1].getType();
		HotSpots s = null;
		if (hsses.size() == 1) {
			s = hsses.get(0);
		} else {
			for (HotSpots find : hsses) {
				int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
				actualX += ConstructionData.getXOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
				actualY += ConstructionData.getYOffsetForObjectId(
						find.getObjectId(), find, roomRot);
				//System.out.println("actualX"+actualX);
				//System.out.println("actualY"+actualY);
				if (p.buildFurnitureX == actualX
						&& p.buildFurnitureY == actualY
						&& myRoomType == find.getRoomType()
						|| find.getCarpetDim() != null && myRoomType == find.getRoomType()) {
					s = find;
					break;
				}
			}
		}
		if (s == null) {
			p.sendMessage("hotspot is null!");
			return;
		}
		if(!buildActions(p, f, s))
				return;
		int actualX = ConstructionData.BASE_X + (myTiles[0] * 8);
		actualX += ConstructionData.getXOffsetForObjectId(f.getFurnitureId(),
				s.getObjectId(), p.getMapInstance().getOwner().getHouseRooms()[toHeight][myTiles[0] - 1][myTiles[1] - 1]
						.getRotation());
		int actualY = ConstructionData.BASE_Y + (myTiles[1] * 8);
		actualY += ConstructionData.getYOffsetForObjectId(f.getFurnitureId(),
				s.getObjectId(), roomRot);
		//System.out.println("actualX-"+actualX);
		//System.out.println("actualY"+actualY);
		if(s.getRoomType() != myRoomType && s.getCarpetDim() == null)
		{
			System.out.println(s.getRoomType()+" "+myRoomType
					+" "+toHeight);
			p.sendMessage("You can't build this furniture in this room.");
			return;
		}
		HouseData.doFurniturePlace(s, f, hsses, myTiles, actualX, actualY, roomRot, p,
				false, p.getLocation().getZ());
		HouseFurniture pf = new HouseFurniture(myTiles[0] - 1,
				myTiles[1] - 1, toHeight, s.getHotSpotId(), f.getFurnitureId(),
				s.getXOffset(), s.getYOffset());
		p.getHouseFurniture().add(pf);
		p.getPA().closeAllWindows();
		p.startAnimation(3684);

	    Construction.saveHouse(p);
	}
	public static String capitalize(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
						s.substring(1));
			}
			if (!Character.isLetterOrDigit(s.charAt(i))) {
				if (i + 1 < s.length()) {
					s = String.format("%s%s%s", s.subSequence(0, i + 1),
							Character.toUpperCase(s.charAt(i + 1)),
							s.substring(i + 2));
				}
			}
		}
		return s;
	}
	public static void handleInterfaceCrosses(ArrayList<Furniture> items,
			Player c, HotSpots hs) {
		int i = 834;
		String s = "";
		String s1 = "";
		for (Furniture f : items) {
			s = f.toString().replaceAll("_", " ").toLowerCase();
			c.getPA().sendFrame126(""+capitalize(s), 53082 + (i - 834) * 5);
			c.getPA().sendFrame126("Lvl " + f.getLevel(), 53130 + (i - 834) * 1);
			int i2 = 0;
			boolean canMake = true;
			for (int i1 = 0; i1 < f.getRequiredItems().length; i1++) {
				String itemName = ItemAssistant.getItemName(f.getRequiredItems()[i1][0]).toLowerCase();

				if (itemName.contains("nails")) {
				    // Find where the word "nails" starts
				    int nailsIndex = itemName.indexOf("nails");
				    // Substring from that index to the end of the string
				    String trimmedName = itemName.substring(nailsIndex);
				    // Capitalize the result (e.g., "nails" -> "Nails")
				    s1 = capitalize(trimmedName);
				} else {
				    s1 = capitalize(itemName);
				}
				c.getPA().sendFrame126("" + s1+": "+f.getRequiredItems()[i1][1], (53083 + i2) + (i - 834) * 5);
				if (!c.getItems().playerHasItem(f.getRequiredItems()[i1][0], f.getRequiredItems()[i1][1])) {
					i2++;
					canMake = false;
					continue;
				}
				i2++;
			}
			if (f.getAdditionalSkillRequirements() != null) {
				for (int ii = 0; ii < f.getAdditionalSkillRequirements().length; ii++) {
					c.getPA().sendFrame126(Config.SKILL_NAME[f.getAdditionalSkillRequirements()[ii][0]]
											+ " " + f.getAdditionalSkillRequirements()[ii][1],(53083 + (i2++)) + (i - 834) * 5);
					if (c.playerLevel[f.getAdditionalSkillRequirements()[ii][0]] < f.getAdditionalSkillRequirements()[ii][1]) {
						canMake = false;
					}
				}
			}
			if (f.getFurnitureRequired() != -1) {
				Furniture fur = Furniture.forFurnitureId(f
						.getFurnitureRequired());
				c.getPA().sendFrame126(
						fur.toString().toLowerCase().replaceAll("_", " "),
						(53083 + (i2++)) + (i - 834) * 5);
				if (canMake) {
					canMake = false;
					int[] myTiles = Construction.getMyChunk(c);
					for (HouseFurniture pf : c.houseFurniture) {
						if (pf.getRoomX() == myTiles[0] - 1 && pf.getRoomY() == myTiles[1] - 1) {
							if (pf.getHotSpot(c.getHouseRooms()[c.inDungeon() ? 4 : c.getLocation().getZ()][myTiles[0] - 1][myTiles[1] - 1].getRotation()) == hs) {
								if (pf.getFurnitureId() != fur.getFurnitureId()) {
									canMake = false;
								} else {
									canMake = true;
								}
							}
						}
					}
				}
			}
			if (canMake) {
				c.getPA().sendFrame36(i, 1);
				c.getPA().sendFrame36(841, 1);
			} else {
				c.getPA().sendFrame36(i, 1);
				c.getPA().sendFrame36(841, 1);
			}
			for (i2 = i2; i2 < 4; i2++) {
				c.getPA().sendFrame126("", (53083 + i2) + (i - 834) * 5);

			}
			i++;
		}
		for (i = i; i < 842; i++) {
			c.getPA().sendFrame126("", 53082 + (i - 834) * 5);
			c.getPA().sendFrame126("", 53083 + (i - 834) * 5);
			c.getPA().sendFrame126("", 53084 + (i - 834) * 5);
			c.getPA().sendFrame126("", 53085 + (i - 834) * 5);
			c.getPA().sendFrame126("", 53086 + (i - 834) * 5);
			c.getPA().sendFrame126("", 53130 + (i - 834) * 1);
			//c.getPA().sendFrame36(i, 0);
		}
	}
	
	public static boolean BuildARoom(Player player) {		
	//if(player.getHouse().hasHouse(player)) {
	player.getPA().showInterface(402);	
			player.getPA().sendFrame126("Parlour: Lvl 1", 54029);player.getPA().sendFrame126("1,000 coins", 54056);
			player.getPA().sendFrame126("Garden: Lvl 1", 54030);player.getPA().sendFrame126("1,000 coins", 54057);
			player.getPA().sendFrame126("Kitchen: Lvl 5", 54031);player.getPA().sendFrame126("5,000 coins", 54058);
			player.getPA().sendFrame126("Dining room: Lvl 10", 54032);player.getPA().sendFrame126("5,000 coins", 54059);
			player.getPA().sendFrame126("Workshop: Lvl 15", 54033);player.getPA().sendFrame126("10,000 coins", 54060);
			player.getPA().sendFrame126("Bedroom: Lvl 20", 54034);player.getPA().sendFrame126("10,000 coins", 54061);
			player.getPA().sendFrame126("Hall - Skill Trophies: Lvl 25", 54035);player.getPA().sendFrame126("15,000 coins", 54062);
			player.getPA().sendFrame126("League hall: Lvl 27", 54036);player.getPA().sendFrame126("15,0000 coins", 54063);
			player.getPA().sendFrame126("Games room: Lvl 30", 54037);player.getPA().sendFrame126("25,000 coins", 54064);
			player.getPA().sendFrame126("Combat room: Lvl 32", 54038);player.getPA().sendFrame126("25,000 coins", 54065);
			player.getPA().sendFrame126("Quest hall: Lvl 35", 54039);player.getPA().sendFrame126("25,000 coins", 54066);
			player.getPA().sendFrame126("Menagerie(outdoors): Lvl 37", 54040);player.getPA().sendFrame126("30,000 coins", 54067);
			player.getPA().sendFrame126("Menagerie(indoors): Lvl 37", 54041);player.getPA().sendFrame126("30,000 coins", 54068);
			player.getPA().sendFrame126("Study: Lvl 40", 54042);player.getPA().sendFrame126("50,000 coins", 54069);
			player.getPA().sendFrame126("Costume room: Lvl 42", 54043);player.getPA().sendFrame126("50,000 coins", 54070);
			player.getPA().sendFrame126("Chapel: Lvl 45", 54044);player.getPA().sendFrame126("50,000 coins", 54071);
			player.getPA().sendFrame126("Portal chamber: Lvl 50", 54045);player.getPA().sendFrame126("100,000 coins", 54072);
			player.getPA().sendFrame126("Formal garden: Lvl 55", 54046);player.getPA().sendFrame126("75,000 coins", 54073);
			player.getPA().sendFrame126("Throne room: Lvl 60", 54047);player.getPA().sendFrame126("150,000 coins", 54074);
			player.getPA().sendFrame126("Oubliette: Lvl 65", 54048);player.getPA().sendFrame126("150,000 coins", 54075);
			player.getPA().sendFrame126("Superior garden: Lvl 65", 54049);player.getPA().sendFrame126("75,000 coins", 54076);
			player.getPA().sendFrame126("Dungeon - Corridor: Lvl 70", 54050);player.getPA().sendFrame126("7,500 coins", 54077);
			player.getPA().sendFrame126("Dungeon - Junction: Lvl 70", 54051);player.getPA().sendFrame126("7,500", 54078);
			player.getPA().sendFrame126("Dungeon - Stairs: Lvl 70", 54052);player.getPA().sendFrame126("7,500", 54079);
			player.getPA().sendFrame126("Portal Nexus: Lvl 72", 54053);player.getPA().sendFrame126("7,500", 54080);
			player.getPA().sendFrame126("Treasure room: Lvl 75", 54054);player.getPA().sendFrame126("7,500", 54081);
			player.getPA().sendFrame126("Achievement gallery: Lvl 80", 54055);player.getPA().sendFrame126("7,500", 54082);
		return true;
	//}
	//return false;
	}
	public static void deleteRoom(Player p, int toHeight) {

		int[] myTiles = Construction.getMyChunk(p);
		int xOnTile = Construction.getXTilesOnTile(myTiles, p);
		int yOnTile = Construction.getYTilesOnTile(myTiles, p);
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

		int roomType = p.inDungeon() ? ConstructionData.DUNGEON_EMPTY
				: ConstructionData.EMPTY;
		RoomData room = new RoomData(0, roomType, 0);
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
		int chunkX = (myTiles[0] - 1) + xOff;
		int chunkY = (myTiles[1] - 1) + yOff;
		RoomData r = p.mapInstance.getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][chunkX][chunkY];
		if (r.getType() == ConstructionData.GARDEN
				|| r.getType() == ConstructionData.FORMAL_GARDEN) {
			int gardenAmt = 0;
				for (int z = 0; z < p.mapInstance.getOwner().getHouseRooms().length; z++) {
					for (int x = 0; x < p.mapInstance.getOwner().getHouseRooms()[z].length; x++) {
						for (int y = 0; y < p.mapInstance.getOwner().getHouseRooms()[z][x].length; y++) {
						RoomData r1 = p.mapInstance.getOwner().getHouseRooms()[z][x][y];
						if (r1 == null)
							continue;
						if (r1.getType() == ConstructionData.GARDEN
								|| r1.getType() == ConstructionData.FORMAL_GARDEN) {
							gardenAmt++;
						}
					}
				}
			}
			if (gardenAmt < 2) {
				p.sendMessage("You need atleast 1 garden or formal garden");
				p.getPA().closeAllWindows();
				return;
			}
		}
		p.getPA().removeObjects(chunkX, chunkY, p.getHeight());
		House house = p.mapInstance instanceof House ? (House) p.mapInstance : ((HouseDungeon)p.mapInstance).getHouse();
		if (p.getHeight() == 0) {
			if (p.inDungeon()) {
				house.getSecondaryPalette().setTile(chunkX, chunkY, 0,
						tile);
			} else {
				house.getPalette().setTile(chunkX, chunkY, toHeight,
						tile);
			}
			p.mapInstance.getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][chunkX][chunkY] = new RoomData(
					0, roomType, 0);
		} else {
			if (p.inDungeon()) {
				house.getSecondaryPalette().setTile(chunkX, chunkY, 0,
						null);
			} else {
				house.getPalette().setTile(chunkX, chunkY, toHeight,
						null);
			}
			p.mapInstance.getOwner().getHouseRooms()[p.inDungeon() ? 4 : toHeight][chunkX][chunkY] = null;
		}

		if (p.inDungeon()) {
			p.getPA().sendConstructedMapPOH(
					house.getSecondaryPalette());
		} else {
			p.getPA().sendConstructedMapPOH(house.getPalette());
		}
		p.getPA().closeAllWindows();

		Iterator<HouseFurniture> iterator = p.houseFurniture.iterator();
		while (iterator.hasNext()) {
			HouseFurniture pf = iterator.next();
			if (pf.getRoomX() == chunkX
					&& pf.getRoomY() == chunkY
					&& pf.getRoomZ() == toHeight)
				iterator.remove();
		}
		Iterator<Portal> portals = p.portals.iterator();
		while(portals.hasNext())
		{
			Portal port = portals.next();
			if (port.getRoomX() == chunkX
					&& port.getRoomY() == chunkY
					&& port.getRoomZ() == toHeight)
				iterator.remove();
		}
	}
	public static boolean handleButtons(Player player, int actionButtonId) {
		int locX = player.getLocation().getX() % 8;
		int locY = player.getLocation().getY() % 8;
		switch(actionButtonId) {
		case 211013:
				Construction.createRoom(ConstructionData.PARLOUR, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211014:
			Construction.createRoom(ConstructionData.GARDEN, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();
			return true;
		case 211015:
			Construction.createRoom(ConstructionData.KITCHEN, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211016:
			Construction.createRoom(ConstructionData.DINING_ROOM, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211017:
			Construction.createRoom(ConstructionData.WORKSHOP, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();
			return true;
		case 211018:
				Construction.createRoom(ConstructionData.BEDROOM, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211019:
			player.getDH().sendDialogues(468, 1);
			return true;

		case 211020:
			Construction.createRoom(ConstructionData.LEAGUE_HALL, player, player.getLocation().getZ());
		player.dialogueAction = -1;
		player.getPA().removeAllWindows();return true;
		case 211021:
				Construction.createRoom(ConstructionData.GAMES_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
			
		case 211022:
				Construction.createRoom(ConstructionData.COMBAT_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211023:
				Construction.createRoom(ConstructionData.QUEST_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211024:
				Construction.createRoom(ConstructionData.MENAGERIE, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211025:
				Construction.createRoom(ConstructionData.INDOOR_MENAGERIE, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211026:
				Construction.createRoom(ConstructionData.STUDY, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211027:
				Construction.createRoom(ConstructionData.COSTUME_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211028:
				Construction.createRoom(ConstructionData.CHAPEL, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211029:
				Construction.createRoom(ConstructionData.PORTAL_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211030:
				Construction.createRoom(ConstructionData.FORMAL_GARDEN, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;

		case 211031:
				Construction.createRoom(ConstructionData.THRONE_ROOM, player, player.getLocation().getZ());
				player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211032:
				Construction.createRoom(ConstructionData.OUBLIETTE, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211033:
				Construction.createRoom(ConstructionData.SUPERIOR_GARDEN, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;

		case 211034://corridor
				Construction.createRoom(ConstructionData.CORRIDOR, player, player.getLocation().getZ());
				player.dialogueAction = -1;
				player.getPA().removeAllWindows();return true;
		case 211035://junction
				Construction.createRoom(ConstructionData.JUNCTION, player, player.getLocation().getZ());
				player.dialogueAction = -1;
				player.getPA().removeAllWindows();return true;
		case 211036:
				Construction.createRoom(ConstructionData.DUNGEON_STAIR_ROOM, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211037:
				Construction.createRoom(ConstructionData.PORTAL_NEXUS, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;

		case 211038:
			Construction.createRoom(ConstructionData.TREASURE_ROOM, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 211039:
			Construction.createRoom(ConstructionData.ACHIEVEMENT_GALLERY, player, player.getLocation().getZ());
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();return true;
		case 9178://first option

			if (player.dialogueAction == 419) {

				int myTiles[] = Construction.getMyChunk(player);
				RoomData room = player.mapInstance.getOwner().getHouseRooms()[4][myTiles[0] - 1][myTiles[1] - 1];
				if (room != null) {
					player.sendMessage("You did something retarded and now there is a under you for some reason.");
					player.getPA().closeAllWindows();
				} else {
					Construction.createRoom(ConstructionData.DUNGEON_STAIR_ROOM, player, 15);
					player.getPA().closeAllWindows();
				}
				return true;
			}
			if(player.dialogueAction == 642)
			{
				/**
				 * Counter room clockwise
				 */
				player.sendMessage("rotating");
				Construction.rotateRoom(1, player);
				return true;
			}
			if(player.dialogueAction == 468)
			{
				Construction.createRoom(ConstructionData.SKILL_ROOM, player, player.getLocation().getZ());
				return true;
			}
			if(player.dialogueAction == 457)
			{
				Butlers b = Butlers.forId(player.houseServant);
				if(player.getItems().playerHasItem(995, b.getLoanCost())) {
					player.servantCharges = 8;
					player.getItems().deleteItem(995, b.getLoanCost());
				} else {
					player.sendMessage("You need "+b.getLoanCost()+" coins to do that.");
				}
				player.getPA().closeAllWindows();
				return true;
				
			}
			if (player.dialogueAction == 414) {

				int myTiles[] = Construction.getMyChunk(player);
				RoomData room = player.mapInstance.getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
				RoomData room_1 = player.mapInstance.getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1];
				if (room != null) {
					player.sendMessage("You did something retarded and now there is a room above you for some reason.");
					player.getPA().closeAllWindows();
				} else {
					Construction.createRoom(
							room_1.getType() == ConstructionData.SKILL_ROOM ? ConstructionData.SKILL_HALL_DOWN
									: ConstructionData.QUEST_HALL_DOWN, player, 100);
					player.getPA().closeAllWindows();
				}
			}
			if (player.dialogueAction == 438) {

				int myTiles1[] = Construction.getMyChunk(player);
				RoomData room = player.mapInstance.getOwner().getHouseRooms()[4][myTiles1[0] - 1][myTiles1[1] - 1];
				if (room != null) {
					player.sendMessage("You did something retarded and now there is a under you for some reason.");
					player.getPA().closeAllWindows();
				} else {
					Construction.createRoom(ConstructionData.OUBLIETTE, player, 103);
					player.getPA().closeAllWindows();
				}
				return true;
			}
			if(player.dialogueAction == 1205) {
				if(player.getHouseRooms()[0][0][0] == null)
				{
					player.sendMessage("You don't own a house");
					return true;
				}
				player.inBuildingMode = false;
				Construction.createPalette(player);
			}
		if(player.dialogueAction == 1203) {
			player.getDH().sendDialogues(1204, 4247);
		}
		if(player.dialogueAction == 1208) {
			if(player.getItems().playerHasItem(995, 1000000)){
				player.getItems().deleteItem(995, 1000000);
				player.boughtHouse = true;
				//player.getDH().sendItemchat3("You just bought a house!", "You can enter your house in Taverly", "you can also change your house location", "by speaking with a Real Estate Agent", 8794, 1500);
				player.houseType = 0;
			} else if(!player.getItems().playerHasItem(995, 1000000)){
				//player.getMoneyPouch().removeMoney(1000000, player);
				player.boughtHouse = true;
				//player.getDH().sendItemchat3("You just bought a house!", "You can enter your house in Taverly", "you can also change your house location", "by speaking with a Real Estate Agent", 8794, 1500);
				player.houseType = 0;
			} else if(!player.getItems().playerHasItem(995, 1000000)){
				player.sendMessage("You do not have enough!");
				player.boughtHouse = false;
			} else if(player.getItems().playerHasItem(995, 500000)){
				player.getItems().deleteItem2(995, 500000);
				//player.getMoneyPouch().removeMoney(500000, player);	
				player.boughtHouse = true;
				//player.getDH().sendItemchat3("You just bought a house!", "You can enter your house in Taverly", "you can also change your house location", "by speaking with a Real Estate Agent", 8794, 250);
				player.nextChat = -1;
				player.dialogueAction = -1;
				player.houseType = 0;
			}
		}
		if(player.dialogueAction == 453)
		{
			player.servantItemFetchId = ConstructionData.PLANK;
			player.xInterfaceId = 28643;
			player.getOutStream().createFrame(27);
			return true;
		}
		if(player.dialogueAction == 454)
		{
			player.servantItemFetchId = ConstructionData.SOFT_CLAY;
			player.xInterfaceId = 28643;
			player.getOutStream().createFrame(27);
			return true;
		}
		if(player.dialogueAction == 455)
		{
			player.servantItemFetchId = ConstructionData.CLOTH;
			player.xInterfaceId = 28643;
			player.getOutStream().createFrame(27);
			return true;
		}
			return true;
		case 9179://second option
			if(player.dialogueAction == 441)
			{
				player.getItems().addItem(7673, 1);
				return true;
			}
			if (player.dialogueAction == 425) {
				player.getItems().addItem(7702, 1);
				return true;
			}
			if (player.dialogueAction == 426 || player.dialogueAction == 427) {
				player.getItems().addItem(7714, 1);
				return true;
			}
			if (player.dialogueAction == 429 || player.dialogueAction == 431
					|| player.dialogueAction == 429) {
				player.getItems().addItem(7726, 1);
				return true;
			}
			if (player.dialogueAction == 432) {
				player.getItems().addItem(2313, 1);
				return true;
			}
			if (player.dialogueAction == 435) {
				player.getItems().addItem(1927, 1);
				return true;
			}
			if (player.dialogueAction == 436) {
				player.getItems().addItem(1550, 1);
				return true;
			}
			if(player.dialogueAction == 453)
			{
				player.servantItemFetchId = ConstructionData.OAK_PLANK;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 454)
			{
				player.servantItemFetchId = ConstructionData.LIMESTONE_BRICK;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 455)
			{
				player.servantItemFetchId = ConstructionData.GOLD_LEAF;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 452)
			{
				if(player.mapInstance.getOwner() == player)
				{
					player.getDH().sendDialogues(453, player.talkingNpc);
				} else
					//player.getDH().sendDialogues(643, player.talkingNpc);
				return true;
			}
			if(player.dialogueAction == 468)
			{
				Construction.createRoom(ConstructionData.SKILL_HALL_DOWN, player, player.getLocation().getZ());
			}
			if(player.dialogueAction == 642)
			{
				/**
				 * Counter room clockwise
				 */
				Construction.rotateRoom(0, player);
				return true;
			}
			if (player.dialogueAction == 1206) {

				int myTiles[] = Construction.getMyChunk(player);
				RoomData room = player.mapInstance.getOwner().getHouseRooms()[0][myTiles[0] - 1][myTiles[1] - 1];
				RoomData room_1 = player.mapInstance.getOwner().getHouseRooms()[1][myTiles[0] - 1][myTiles[1] - 1];
				if (room.getType() != ConstructionData.EMPTY) {
					player.sendMessage("You did something retarded and now there is a under above you for some reason.");
					player.getPA().closeAllWindows();
				} else {
					Construction.createRoom(
							room_1.getType() == ConstructionData.SKILL_HALL_DOWN ? ConstructionData.SKILL_ROOM
									: ConstructionData.QUEST_ROOM, player, 101);
					player.getPA().closeAllWindows();
				}
				return true;
			}
		if(player.dialogueAction == 1203) {
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();
		}
		if(player.dialogueAction == 1205) {
			if(player.getHouseRooms()[0][0][0] == null)
			{
				player.sendMessage("You don't own a house");
				return true;
			}
			player.inBuildingMode = true;
			player.houseServant = 4243;
			Construction.createPalette(player);
		}
		return true;
		case 9180://third option
			if(player.dialogueAction == 453)
			{
				player.servantItemFetchId = ConstructionData.TEAK_PLANK;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 454)
			{
				player.servantItemFetchId = ConstructionData.STEEL_BAR;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 455)
			{
				player.servantItemFetchId = ConstructionData.MARBLE_BLOCK;
				player.xInterfaceId = 402;
				player.getOutStream().createFrame(27);
				return true;
			}
			if(player.dialogueAction == 452)
			{
				if(player.mapInstance.getOwner() == player) {
					House house = player.mapInstance instanceof House ? (House) player.mapInstance : ((HouseDungeon)player.mapInstance).getHouse();
					Servant butler = house.getButler();
					butler.randomWalk = true;
					butler.summonedBy = 0;
					butler.setGreetVisitors(true);
					player.getPA().closeAllWindows();
				} else {
					player.getDH().sendDialogues(643, player.talkingNpc);
				}
				return true;
				
			}
			if(player.dialogueAction == 642)
			{
				/**
				 * Remove room
				 */
				//if (player.getLocation().getZ() == 0 && !player.inDungeon())
					deleteRoom(player, 0);
				//if (player.inDungeon())
					//deleteRoom(player, 4);
				//if (player.getHeight() == 1) {
				//	deleteRoom(player, 1);
				//}
				return true;
			}

			if(player.dialogueAction == 442)
			{
				player.getPA().removeAllWindows();
				player.getPA().commandFrame(2);
				return true;
			}
		return true;
		case 43145://fourth option
			if(player.dialogueAction == 452)
			{
				House house = player.mapInstance instanceof House ? (House) player.mapInstance : ((HouseDungeon)player.mapInstance).getHouse();
				NPC butler = house.getButler();
				if(butler.summonedBy != player.getId()){
					butler.summonedBy = player.getId();
				} else {
					butler.summonedBy = 0;
				}
				player.getPA().closeAllWindows();
				return true;
			}
		if(player.dialogueAction == 1205) {
			player.dialogueAction = -1;
			player.getPA().removeAllWindows();
		}
		return true;
		}
		return false;
	}


	public static void handleInterfaceItems(ArrayList<Furniture> items, Player c) {
	    if (c.getOutStream() != null && c != null) {
	        // Create a fixed-size array to hold the reordered items (interface 403 usually holds 8)
	        Furniture[] reordered = new Furniture[8];
	        
	        // Map original positions to your specific 1,5,2,6 pattern
	        int[] layout = {0, 2, 4, 6, 1, 3, 5, 7};
	        
	        for (int i = 0; i < items.size() && i < layout.length; i++) {
	            reordered[layout[i]] = items.get(i);
            	System.out.println("i: "+i);
	            //if (reordered[i] != null)
            	System.out.println("Item: "+items.get(i));
	        }
	        c.getOutStream().createFrameVarSizeWord(53);
	        c.getOutStream().writeWord(53080);
	        // We send 8 slots to ensure the grid is fully updated
	        c.getOutStream().writeWord(8); 
	        
	        for (int i = 0; i < 8; i++) {
	            if (reordered[i] != null) {
	            	//System.out.println("Item: "+reordered[i].getItemId());
	            	//System.out.println("Item: "+items.get(i));
	                c.getOutStream().writeByte(1);
	                c.getOutStream().writeWordBigEndianA(reordered[i].getItemId() + 1);
	            } else {
	                // Fill empty slots so the interface doesn't glitch
	                c.getOutStream().writeByte(0);
	                c.getOutStream().writeWordBigEndianA(0);
	            }
	        }
	        c.getOutStream().endFrameVarSizeWord();
	        c.flushOutStream();
	    }
	}
}