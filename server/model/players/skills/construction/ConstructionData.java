package server.model.players.skills.construction;

import server.clip.ObjectDef;

public class ConstructionData {
	public static final String[] HANGMANWORDS = new String[] 
			{
				"DENNISISNOOB",
				"ABYSSAL","ADAMANTITE","ALKHARID","ARDOUGNE","ASGARNIA","AVANTOE","BASILISK","BANSHEE","BARROWS","BLOODVELD","BOBTHECAT",
				"BRIMHAVEN","BURTHORPE","CADANTINE","CAMELOT","CANIFIS","CATHERBY","CHAOSDRUID","CHAOSDWARF","CHOMPYBIRD","COCKATRICE",
				"CRANDOR","CROMADIURE","DAGANNOTH","DORGESHUUN","DRAGON","DRAYNOR","DUSTDEVIL","DWARFWEED","EDGEVILLE","ENTRANA",
				"FALADOR","FELDIP","FIREGIANT","FREMENNIK","GARGOYLE","GOBLIN","GRANDTREE","GUAMLEAF","GUTANOTH","GUTHIX","HILLGIANT",
				"HELLHOUND","HIGHWAYMAN","HOBGOBLIN","ICEGIANT","ICEQUEEN","ICEWARRIOR","ICEWOLF","ICETROLL","IRITLEAF","ISAFDAR",
				"JOGRE","KALPHITE","KANDARIN","KARAMJA","KELDAGRIM","KHAZARD","KWUARM","LANTADYME","LLETYA","LUMBRIDGE","NECHRYAEL",
				"MARRENTILL","MENAPHOS","MISTHALIN","MITHRIL","MOGRE","MORTTON","MORYTANIA","MOSSGIANT","NIGHTSHADE","PALADIN",
				"PHASMATYS","PORTSARIM","PRIFDDINAS","PYREFIEND","RANARRWEED","RELLEKKA","RIMMINGTON","RUNESCAPE","RUNITE",
				"SARADOMIN","SKELETON","SNAPDRAGON","SNAPEGRASS","SOPHANEM","SOULLESS","SPIRITTREE","TARROMIN","TAVERLEY",
				"TERRORBIRD","TIRANNWN","TOADFLAX","TORSTOL","UGTHANKI","UNICORN","VARROCK","WHIP","YANILLE","ZAMORAK"};
			
	public static final int
	EMPTY = 0, 
	BUILDABLE = 1, 
	GARDEN = 2, 
	PARLOUR = 3,
	KITCHEN = 4, 
	DINING_ROOM = 5, 
	WORKSHOP = 6, 
	BEDROOM = 7,
	SKILL_ROOM = 8, 
	QUEST_HALL_DOWN = 9, 
	SKILL_HALL_DOWN = 10,
	GAMES_ROOM = 11, 
	COMBAT_ROOM = 12, 
	QUEST_ROOM = 13, 
	LEAGUE_HALL = 14,
	STUDY = 15, 
	COSTUME_ROOM = 16, 
	CHAPEL = 17,
	PORTAL_ROOM = 18, 
	FORMAL_GARDEN = 19, 
	THRONE_ROOM = 20, 
	OUBLIETTE = 21,
	PIT = 22, 
	DUNGEON_STAIR_ROOM = 23, 
	TREASURE_ROOM = 24,
	CORRIDOR = 25, 
	JUNCTION = 26, 
	ROOF_SINGLE = 27, 
	ROOF_3_WAY = 28, 
	ROOF_4_WAY = 29, 
	DUNGEON_EMPTY = 30, 
	EMPTY2 = 31, 
	PORTAL_NEXUS = 32,
	MENAGERIE = 33,
	INDOOR_MENAGERIE=34, 
	SUPERIOR_GARDEN = 35, 
	ACHIEVEMENT_GALLERY = 36, 
	BASE_X = 4032, BASE_Y = 5696, MIDDLE_X = 4088, MIDDLE_Y = 5752;
	
	public static final int LIT_CANDLE = 34, PLANK = 960, OAK_PLANK = 8778, TEAK_PLANK = 8780,
			MAHOGANY_PLANK = 8782, GOLD_LEAF = 8784, MARBLE_BLOCK = 8786,
			MAGIC_STONE = 8788, CLOTH = 8790, CLOCKWORK = 8792, SAW = 8794,
			NAILS = 1539, MOLTEN_GLASS = 1775, SOFT_CLAY = 1761,
			LIMESTONE_BRICK = 3420, STEEL_BAR = 2353, IRON_BAR = 2351,
			ROPE = 954, AIR_RUNE = 556, WATER_RUNE = 555, EARTH_RUNE = 557,
			FIRE_RUNE = 554,BSF = 8457, WATER_CAN = 5331, BM = 8459, BR = 8461,
			BRM = 8451, BD = 13438, BBB = 8455, BTH = 8437, BNH = 8419, SBH = 8441,
			TH = 8443, FH = 8445, TFH = 8447, TBH = 8449, BONES = 526 ,SKULLS = 964,
			COIN = 995, BOW = 1929, RD = 1763;
	public static final int[] DOORSPACEIDS = new int[] {15308, 15307, 15314, 15313, 15305, 15306,15317};

	public static int getGuardId(int objectId)
	{
		switch(objectId)
		{
			case 13331:
				return 3580;
			case 13373:
				return 3594;
			
			case 13366:
				return 3581;
			case 13367:
				return 3582;
			case 13368:
				return 3583;
			case 13372:
				return 3588;
			case 13370:
				return 3585;
			case 13369:
				return 3584;
			case 2715:
				return 3586;

			case 39260:
				return 11562;
			case 39261:
				return 11563;
			case 39262:
				return 11564;
			case 39263:
				return 11565;
			case 39264:
				return 11566;
			case 39265:
				return 11567;

			case 13378:
				return 3593;
			case 13374:
				return 3589;
			case 13377:
				return 3592;
			case 13376:
				return 3591;
			case 13375:
				return 3590;
		}
		return -1;
	}
	public static boolean isDungeonRoom(int roomType)
	{
		return roomType == DUNGEON_STAIR_ROOM
				|| roomType == CORRIDOR
				|| roomType == JUNCTION
				|| roomType == OUBLIETTE
				|| roomType == PIT
				|| roomType == TREASURE_ROOM;
	}
	public static boolean isGardenRoom(int roomType)
	{
		return roomType == GARDEN
				|| roomType == FORMAL_GARDEN || roomType == SUPERIOR_GARDEN|| roomType == MENAGERIE;
	}
	public static int getXOffsetForObjectId(int objectId, int hsId, int rotation) {
		HotSpots hs = HotSpots.forObjectId(hsId);
		if (hs == null)
			return 0;
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.length, hs.xOffset, hs.yOffset, objectDef.width, hs.getRotation(0));
		return finalXTile;
	}

	public static int getYOffsetForObjectId(int objectId, int hsId, int rotation) {
		HotSpots hs = HotSpots.forObjectId(hsId);
		if (hs == null)
			return 0;
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(hs.yOffset,
				objectDef.length, rotation, objectDef.width, hs.xOffset, hs.getRotation(0));
		return finalYTile;
	}
	public static int getYOffsetForObjectId(int objectId, int offsetX, int offsetY, int rotation, int objectRot) {
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(offsetY,
				objectDef.length, rotation, objectDef.width, offsetX, objectRot);
		return finalYTile;
	}
	public static int getXOffsetForObjectId(int objectId, int offsetX, int offsetY,
			int rotation, int objectRot) {
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.length, offsetX, offsetY,
				objectDef.width, objectRot);
		return finalXTile;
	}
	
	public static int getXOffsetForObjectId(int objectId, HotSpots hs,
			int rotation) {
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalXTile = 0 + getRotatedLandscapeChunkX(rotation,
				objectDef.length, hs.getXOffset(), hs.getYOffset(),
				objectDef.width, hs.getRotation(0));
		return finalXTile;
	} 

	public static int getYOffsetForObjectId(int objectId, HotSpots hs,
			int rotation) {
		ObjectDef objectDef = ObjectDef.forID(objectId);
		int finalYTile = 0 + getRotatedLandscapeChunkY(hs.getYOffset(),
				objectDef.length, rotation, objectDef.width, hs.getXOffset(), hs.getRotation(0));
		return finalYTile;
	}
	public static int getRotatedLandscapeChunkX(int rotation, int objectSizeY, int x, int y, int objectSizeX, int objectRot) {
	    rotation &= 3;
	    // We must determine the ACTUAL width/length based on the object's OWN orientation
	    int realWidth = (objectRot == 1 || objectRot == 3) ? objectSizeY : objectSizeX;
	    int realLength = (objectRot == 1 || objectRot == 3) ? objectSizeX : objectSizeY;

	    switch (rotation) {
	        case 0: return x;
	        case 1: return y;
	        case 2: return 7 - x - (realWidth - 1);
	        case 3: return 7 - y - (realLength - 1);
	        default: return x;
	    }
	}

	public static int getRotatedLandscapeChunkY(int y, int objectSizeY, int rotation, int objectSizeX, int x, int objectRot) {
	    rotation &= 3;
	    int realWidth = (objectRot == 1 || objectRot == 3) ? objectSizeY : objectSizeX;
	    int realLength = (objectRot == 1 || objectRot == 3) ? objectSizeX : objectSizeY;

	    switch (rotation) {
	        case 0: return y;
	        case 1: return 7 - x - (realWidth - 1);
	        case 2: return 7 - y - (realLength - 1);
	        case 3: return x;
	        default: return y;
	    }
	}
}
