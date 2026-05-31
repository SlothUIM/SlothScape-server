package server.model.players.skills.construction;
public enum Room
	{
		EMPTY(1864, 5696, ConstructionData.EMPTY, 1, 0, new boolean[] {true, true, true, true}),
		BUILDABLE(1864, 5696, ConstructionData.BUILDABLE, 1, 0, new boolean[] {true, true, true, true}),
		GARDEN(1856, 5704, ConstructionData.GARDEN, 1, 1000, new boolean[] {true, true, true, true}),
		PARLOUR(1856, 5752, ConstructionData.PARLOUR, 1, 1000, new boolean[] {true, false, true, true}),
		KITCHEN(1872, 5752, ConstructionData.KITCHEN, 5, 5000, new boolean[] {true, false, false, true}),
		DINING_ROOM(1888, 5752,ConstructionData.DINING_ROOM, 10, 5000, new boolean[] {true, false, true, true}),
		WORKSHOP(1856, 5736, ConstructionData.WORKSHOP, 15, 10000, new boolean[] {false, true, false, true}),
		BEDROOM(1904, 5752, ConstructionData.BEDROOM, 20, 10000, new boolean[] {false, false, true, true}),
		SKILL_ROOM(1880, 5744, ConstructionData.SKILL_ROOM, 25, 15000, new boolean[] {true, true, true, true}),
		QUEST_HALL_DOWN(1912, 5744, ConstructionData.QUEST_HALL_DOWN, 35, 0, new boolean[] {true, true, true, true}),
		SKILL_HALL_DOWN(1864, 5744, ConstructionData.SKILL_HALL_DOWN, 25, 0, new boolean[] {true, true, true, true}),
		GAMES_ROOM(1896, 5728, ConstructionData.GAMES_ROOM, 30, 25000, new boolean[] {true, false, false, true}),
		COMBAT_ROOM(1880, 5728, ConstructionData.COMBAT_ROOM, 32, 25000, new boolean[] {true, false, true, true}),
		QUEST_ROOM(1896, 5744, ConstructionData.QUEST_ROOM, 35, 25000, new boolean[] {true, true, true, true}),
		LEAGUE_HALL(1896, 5760, ConstructionData.LEAGUE_HALL, 37, 30000, new boolean[] {true, true, true, true}),
		STUDY(1888, 5736, ConstructionData.STUDY, 40, 50000, new boolean[] {true, false, true, true}),
		CUSTOME_ROOM(1904, 5704, ConstructionData.COSTUME_ROOM, 42, 50000, new boolean[] {false, false, false, true}),
		CHAPEL(1872, 5736, ConstructionData.CHAPEL, 45, 50000, new boolean[] {false, false, true, true}),
		PORTAL_ROOM(1864, 5728, ConstructionData.PORTAL_ROOM, 50, 100000, new boolean[] {false, false, false, true}),
		FORMAL_GARDEN(1872, 5704, ConstructionData.FORMAL_GARDEN, 55, 75000, new boolean[] {true, true, true, true}),
		THRONE_ROOM(1904, 5736, ConstructionData.THRONE_ROOM, 60, 150000, new boolean[] {false, true, false, false}),
		OUBLIETTE(1904, 5720, ConstructionData.OUBLIETTE, 65, 150000, new boolean[] {true, true, true, true}),
		PIT(1896, 5672, ConstructionData.PIT, 70, 10000, new boolean[] {true, true, true, true}),
		DUNGEON_STAIR_ROOM(1872, 5720, ConstructionData.DUNGEON_STAIR_ROOM, 70, 7500, new boolean[] {true, true, true, true}),
		TREASURE_ROOM(1912, 5728, ConstructionData.TREASURE_ROOM, 75, 250000, new boolean[] {false, true, false, false}),
		CORRIDOR(1888, 5720, ConstructionData.CORRIDOR, 70, 7500, new boolean[] {false, true, false, true}),
		JUNCTION(1856, 5720, ConstructionData.JUNCTION, 70, 7500, new boolean[] {true, true, true, true}),
		ROOF_SINGLE(1864, 5712, ConstructionData.ROOF_SINGLE, 0, 0, new boolean[] {true, true, true, true}),
		ROOF_3_WAY(1880, 5712, ConstructionData.ROOF_3_WAY, 0, 0, new boolean[] {false, false, false, false}),
		ROOF_4_WAY(1896, 5712, ConstructionData.ROOF_4_WAY, 0, 0, new boolean[] {false, false, false, false}),
		DUNGEON_EMPTY(1880, 5736, ConstructionData.DUNGEON_EMPTY, 0, 0, new boolean[] {true, true, true, true}),
		EMPTY2(1856, 5760, ConstructionData.EMPTY2, 1, 0, new boolean[] {true, true, true, true}),
		PORTAL_NEXUS(1880, 5760, ConstructionData.PORTAL_NEXUS, 1, 0, new boolean[] {true, true, true, true}),
		MENAGERIE(1912, 5696, ConstructionData.MENAGERIE, 1, 0, new boolean[] {true, true, true, true}),
		INDOOR_MENAGERIE(1912, 5712, ConstructionData.INDOOR_MENAGERIE, 1, 0, new boolean[] {true, true, true, true}),
		SUPERIOR_GARDEN(1896, 5696, ConstructionData.SUPERIOR_GARDEN, 1, 0, new boolean[] {true, true, true, true}),
		ACHIEVEMENT_GALLERY(1864, 5760, ConstructionData.ACHIEVEMENT_GALLERY, 1, 0, new boolean[] {false, true, false, true}),
		;
		
		public static Room forID(int id) {
			for (Room rd : values()) {
				if (rd.id == id)
					return rd;
			}
			return null;
		}

		private int x, y, cost, levelToBuild, id;
		private boolean[] doors;

		private Room(int x, int y, int id, int levelToBuild, int cost,
				boolean[] doors) {
			this.x = x;
			this.y = y;
			this.id = id;
			this.levelToBuild = levelToBuild;
			this.cost = cost;
			this.doors = doors;
		}
		
		public boolean[] getDoors() {
			return doors;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getCost() {
			return cost;
		}

		public int getLevelToBuild() {
			return levelToBuild;
		}

		public int getId() {
			return id;
		}

		public static int getFirstElegibleRotation(Room rd, int from) {
			for (int rot = 0; rot < 4; rot++) {
				boolean[] door = rd.getRotatedDoors(rot);
				if (from == 0 && door[2])
					return rot;
				if (from == 1 && door[3])
					return rot;
				if (from == 2 && door[0])
					return rot;
				if (from == 3 && door[1])
					return rot;
			}
			return -1;
		}
		public static int getNextEligibleRotationClockWise(Room rd, int from, int currentRot) {
			for (int rot = currentRot+1; rot < currentRot+4; rot++) {
				int rawt = (rot > 3 ? (rot - 4) : rot);
				boolean[] door = rd.getRotatedDoors(rawt);
				if (from == 0 && door[2])
					return rawt;
				if (from == 1 && door[3])
					return rawt;
				if (from == 2 && door[0])
					return rawt;
				if (from == 3 && door[1])
					return rawt;
			}
			return currentRot;
		}
		public static int getNextEligibleRotationCounterClockWise(Room rd, int from, int currentRot) {
			for (int rot = currentRot-1; rot > currentRot-4; rot--) {
				int rawt = (rot < 0 ? (rot + 4) : rot);
				boolean[] door = rd.getRotatedDoors(rawt);
				if (from == 0 && door[2])
					return rawt;
				if (from == 1 && door[3])
					return rawt;
				if (from == 2 && door[0])
					return rawt;
				if (from == 3 && door[1])
					return rawt;
			}
			return -1;
		}

		public boolean[] getRotatedDoors(int rotation) {
			if (rotation == 0)
				return doors;
			if (rotation == 1) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[3] = true;
				if (doors[1])
					newDoors[0] = true;
				if (doors[2])
					newDoors[1] = true;
				if (doors[3])
					newDoors[2] = true;
				return newDoors;
			}
			if (rotation == 2) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[2] = true;
				if (doors[1])
					newDoors[3] = true;
				if (doors[2])
					newDoors[0] = true;
				if (doors[3])
					newDoors[1] = true;
				return newDoors;
			}
			if (rotation == 3) {
				boolean[] newDoors = new boolean[4];
				if (doors[0])
					newDoors[1] = true;
				if (doors[1])
					newDoors[2] = true;
				if (doors[2])
					newDoors[3] = true;
				if (doors[3])
					newDoors[0] = true;
				return newDoors;
			}
			return null;
		}
	}