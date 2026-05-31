package server.model.minigames.cox;

import java.util.*;

import server.model.minigames.cox.ChambersOfXeric.ConnectionType;
import server.model.minigames.cox.ChambersOfXeric.CoxRoom;
import server.model.minigames.cox.ChambersOfXeric.Direction;
import server.model.minigames.cox.ChambersOfXeric.RoomType;

public class RaidLayoutGenerator {

    private final Random random = new Random();

    // Global tracker for the ENTIRE raid (persists across floors 1, 2, and 3)
    private final Set<String> globalUsedFamilies = new HashSet<>();

    // FIX 1: Cache the enum values ONCE so we don't allocate arrays thousands of times
    private static final CoxRoom[] CACHED_ROOMS = CoxRoom.values();

    public List<RaidNode> mapFloorLayout(List<CoxRoom> floorPlan, int targetZ) {
        List<RaidNode> layout = generateDynamicSnake(floorPlan, targetZ);

        if (layout == null) {
            System.out.println("[CoX] Dynamic generation trapped itself. Using guaranteed safe fallback.");
            return generateStaticFallback(floorPlan, targetZ);
        }
        return layout;
    }

    private List<RaidNode> generateDynamicSnake(List<CoxRoom> floorPlan, int targetZ) {
        // FIX 2: Instantiate heavy objects ONCE outside the loop
        List<RaidNode> layout = new ArrayList<>(floorPlan.size());
        boolean[][] visited = new boolean[5][5];
        Set<String> attemptFamilies = new HashSet<>();

        for (int attempt = 0; attempt < 100; attempt++) {
            // FIX 3: Reset the objects rather than abandoning them to the Garbage Collector
            layout.clear();
            for (int i = 0; i < 5; i++) {
                Arrays.fill(visited[i], false);
            }
            attemptFamilies.clear();
            attemptFamilies.addAll(globalUsedFamilies);

            int currentX = 2;
            int currentY = 2;
            boolean trapped = false;

            Direction currentExitDir = null;
            ConnectionType currentExitType = null;

            for (int i = 0; i < floorPlan.size(); i++) {
                RoomType requiredType = floorPlan.get(i).getType();
                CoxRoom chosenRoom = null;
                int placedRotation = 0;
                Direction roomExitDir = null;

                if (i == 0) {
                    Direction targetExit = Direction.values()[random.nextInt(4)];
                    chosenRoom = getStartRoomForExit(targetExit, requiredType);
                    placedRotation = 0;
                    roomExitDir = chosenRoom.getNativeExit();
                } else if (i == floorPlan.size() - 1) {
                    Direction requiredEntrance = currentExitDir.opposite();
                    chosenRoom = getEndRoom(requiredType);
                    placedRotation = (requiredEntrance.id - chosenRoom.getNativeEntrance().id + 4) % 4;
                    roomExitDir = Direction.NORTH;
                } else {
                    Direction requiredEntrance = currentExitDir.opposite();

                    List<Direction> validMoves = getValidMoves(currentX, currentY, visited);
                    if (validMoves.isEmpty()) {
                        trapped = true;
                        break;
                    }

                    Direction requiredExit = validMoves.get(random.nextInt(validMoves.size()));
                    int requiredDelta = (requiredExit.id - requiredEntrance.id + 4) % 4;

                    chosenRoom = getRoomForDelta(requiredType, requiredDelta, currentExitType, attemptFamilies);
                    attemptFamilies.add(getRoomFamily(chosenRoom));

                    placedRotation = (requiredEntrance.id - chosenRoom.getNativeEntrance().id + 4) % 4;
                    roomExitDir = requiredExit;
                }

                layout.add(new RaidNode(chosenRoom, currentX, currentY, targetZ, placedRotation));
                visited[currentX][currentY] = true;

                if (i == floorPlan.size() - 1) break;

                currentX += roomExitDir.dx;
                currentY += roomExitDir.dy;

                currentExitDir = roomExitDir;
                currentExitType = chosenRoom.getExitType();
            }

            if (!trapped) {
                System.out.println("[CoX] Flawless Vector layout generated on attempt " + (attempt + 1) + " for Floor Z:" + targetZ);
                globalUsedFamilies.addAll(attemptFamilies);
                return layout;
            }
        }
        return null;
    }

    private CoxRoom getStartRoomForExit(Direction targetExit, RoomType startType) {
        List<CoxRoom> pool = new ArrayList<>();
        // FIX: Use cached array
        for (CoxRoom room : CACHED_ROOMS) {
            if (room.getType() == startType && room.getNativeExit() == targetExit) {
                pool.add(room);
            }
        }
        Collections.shuffle(pool);
        return pool.isEmpty() ? (startType == RoomType.START ? CoxRoom.RAID_START_WEST : CoxRoom.FLOOR_START) : pool.get(0);
    }

    private CoxRoom getEndRoom(RoomType type) {
        List<CoxRoom> pool = new ArrayList<>();
        // FIX: Use cached array
        for (CoxRoom room : CACHED_ROOMS) {
            if (room.getType() == type) pool.add(room);
        }
        Collections.shuffle(pool);
        return pool.isEmpty() ? CoxRoom.FLOOR_END : pool.get(0);
    }

    private String getRoomFamily(CoxRoom room) {
        String name = room.name();
        int lastDash = name.lastIndexOf('_');
        if (lastDash != -1 && !name.startsWith("FLOOR") && !name.startsWith("RAID_START")) {
            return name.substring(0, lastDash);
        }
        return name;
    }

    private CoxRoom getRoomForDelta(RoomType type, int requiredDelta, ConnectionType previousExitType, Set<String> usedFamilies) {
        List<CoxRoom> pool = new ArrayList<>();

        // FIX: Use cached array
        for (CoxRoom room : CACHED_ROOMS) {
            if (room.getType() == type) {
                if (room.getNativeEntrance() == null || room.getNativeExit() == null) continue;
                if (usedFamilies.contains(getRoomFamily(room))) continue;

                int roomDelta = (room.getNativeExit().id - room.getNativeEntrance().id + 4) % 4;

                if (roomDelta == requiredDelta) {
                    boolean isValidConnection = false;
                    if (previousExitType == ConnectionType.HALLWAY) {
                        isValidConnection = (room.getEnterType() == ConnectionType.PASSAGE);
                    } else if (previousExitType == ConnectionType.PASSAGE) {
                        isValidConnection = (room.getEnterType() == ConnectionType.HALLWAY);
                    }
                    if (isValidConnection) {
                        pool.add(room);
                    }
                }
            }
        }

        if (pool.isEmpty()) {
            System.out.println("[CoX] Warning: Connection mismatch for " + type + ". Dropping connection rules.");
            for (CoxRoom room : CACHED_ROOMS) {
                if (room.getType() == type && !usedFamilies.contains(getRoomFamily(room))) {
                    if (room.getNativeEntrance() == null || room.getNativeExit() == null) continue;
                    int roomDelta = (room.getNativeExit().id - room.getNativeEntrance().id + 4) % 4;
                    if (roomDelta == requiredDelta) pool.add(room);
                }
            }
        }

        if (pool.isEmpty()) {
            System.out.println("[CoX] CRITICAL: Map generation blocked. Forcing duplicate room for " + type);
            for (CoxRoom room : CACHED_ROOMS) {
                if (room.getType() == type) {
                    if (room.getNativeEntrance() == null || room.getNativeExit() == null) continue;
                    int roomDelta = (room.getNativeExit().id - room.getNativeEntrance().id + 4) % 4;
                    if (roomDelta == requiredDelta) pool.add(room);
                }
            }
        }

        Collections.shuffle(pool);
        return pool.isEmpty() ? CoxRoom.VASA_STRAIGHT : pool.get(0);
    }

    private List<Direction> getValidMoves(int x, int y, boolean[][] visited) {
        List<Direction> moves = new ArrayList<>();
        if (y + 1 < 5 && !visited[x][y + 1]) moves.add(Direction.NORTH);
        if (x + 1 < 5 && !visited[x + 1][y]) moves.add(Direction.EAST);
        if (y - 1 >= 0 && !visited[x][y - 1]) moves.add(Direction.SOUTH);
        if (x - 1 >= 0 && !visited[x - 1][y]) moves.add(Direction.WEST);
        return moves;
    }

    private List<RaidNode> generateStaticFallback(List<CoxRoom> floorPlan, int targetZ) {
        List<RaidNode> layout = new ArrayList<>();

        Direction N = Direction.NORTH;
        Direction E = Direction.EAST;
        Direction S = Direction.SOUTH;

        Object[][] safePath = {
                {2, 0, N}, {2, 1, N}, {2, 2, N}, {2, 3, E},
                {3, 3, E}, {4, 3, S}, {4, 2, S}, {4, 1, S},
                {4, 0, S}
        };

        Direction currentExitDir = null;
        ConnectionType currentExitType = null;

        for (int i = 0; i < Math.min(floorPlan.size(), safePath.length); i++) {
            CoxRoom room = floorPlan.get(i);
            int gridX = (int) safePath[i][0];
            int gridY = (int) safePath[i][1];
            Direction requiredExit = (Direction) safePath[i][2];
            int placedRotation = 0;

            if (i == 0) {
                placedRotation = (requiredExit.id - room.getNativeExit().id + 4) % 4;
            } else if (i == floorPlan.size() - 1) {
                Direction requiredEntrance = currentExitDir.opposite();
                room = getEndRoom(room.getType());
                placedRotation = (requiredEntrance.id - room.getNativeEntrance().id + 4) % 4;
            } else {
                Direction requiredEntrance = currentExitDir.opposite();
                int requiredDelta = (requiredExit.id - requiredEntrance.id + 4) % 4;
                room = getRoomForDelta(room.getType(), requiredDelta, currentExitType, globalUsedFamilies);
                globalUsedFamilies.add(getRoomFamily(room));
                placedRotation = (requiredEntrance.id - room.getNativeEntrance().id + 4) % 4;
            }

            layout.add(new RaidNode(room, gridX, gridY, targetZ, placedRotation));
            currentExitDir = requiredExit;
            currentExitType = room.getExitType();
        }

        return layout;
    }
}