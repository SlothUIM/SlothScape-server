package server.model.minigames.cox;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.raids.RaidParty;
import server.model.players.Player;

public class ChambersOfXeric {

    public enum ConnectionType {
        HALLWAY, // Open dirt path
        PASSAGE  // Stone archway / Door
    }

    /**
     * Authentic Vector Definitions. Maps to 0=N, 1=E, 2=S, 3=W rotation matrix.
     */
    public enum Direction {
        NORTH(0, 0, 1),
        EAST(1, 1, 0),
        SOUTH(2, 0, -1),
        WEST(3, -1, 0);

        public final int id, dx, dy;

        Direction(int id, int dx, int dy) {
            this.id = id;
            this.dx = dx;
            this.dy = dy;
        }

        public Direction opposite() {
            return Direction.values()[(id + 2) % 4];
        }
    }

    public enum RoomType {
        START, EXIT, FLOORSTART, FLOOREND, COMBAT, PUZZLE, SCAVENGER, RESOURCE, BOSS;
    }



    public enum CoxRoom {
        // Format: regionId, chunkX, chunkY, sourceZ, size, RoomType, spawnX, spawnY, NativeEntrance, NativeExit, EnterType, ExitType

        // =====================================================================
        // PLANE 0 (GROUND FLOOR) - Base Regions: 13138, 13139, 13140
        // Note: Combat/Puzzle rooms typically require a PASSAGE on both sides.
        // =====================================================================

        // --- VASA NISTIRIO ---
        VASA_WEST(13138, 0, 4, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VASA_STRAIGHT(13138, 4, 4, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VASA_EAST(13394, 0, 4, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- LIZARDMAN SHAMANS ---
        SHAMAN_WEST(13138, 0, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        SHAMAN_STRAIGHT(13138, 4, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        SHAMAN_EAST(13394, 0, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- ICE DEMON ---
        ICE_DEMON_WEST(13139, 0, 4, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ICE_DEMON_STRAIGHT(13139, 4, 4, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ICE_DEMON_EAST(13395, 0, 4, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- VANGUARDS ---
        VANGUARDS_WEST(13139, 0, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VANGUARDS_STRAIGHT(13139, 4, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VANGUARDS_EAST(13395, 0, 0, 0, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- THIEVING PUZZLE ---
        THIEVING_WEST(13140, 0, 0, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        THIEVING_STRAIGHT(13140, 4, 0, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        THIEVING_EAST(13396, 0, 0, 0, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        FLOOR_START(13145, 0, 0, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.WEST, null, ConnectionType.HALLWAY),
        FLOOR_START2(13145, 4, 0, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.NORTH,  null, ConnectionType.HALLWAY),
        FLOOR_START3(13145, 0, 4, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.WEST,  null, ConnectionType.HALLWAY),
        FLOOR_START4(13145, 4, 4, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.NORTH,  null, ConnectionType.HALLWAY),
        FLOOR_START5(13401, 0, 0, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.EAST,  null, ConnectionType.HALLWAY),
        FLOOR_START6(13401, 0, 4, 0, 4, RoomType.FLOORSTART, 16, 16, null, Direction.EAST,  null, ConnectionType.HALLWAY),

        // --- END FLOOR---
        FLOOR_END(13136, 0, 4, 0, 4, RoomType.FLOOREND, 16, 16, Direction.SOUTH, null, ConnectionType.PASSAGE, null),
        FLOOR_END2(13136, 4, 4, 0, 4, RoomType.FLOOREND, 16, 16, Direction.SOUTH, null, ConnectionType.PASSAGE, null),
        FLOOR_END3(13136, 0, 4, 0, 4, RoomType.FLOOREND, 16, 16, Direction.SOUTH, null, ConnectionType.PASSAGE, null),

        // =====================================================================
        // PLANE 1 (MIDDLE FLOOR) - Base Regions: 13137, 13138, 13139, 13141
        // Note: Resources and Scavengers use HALLWAYS.
        // =====================================================================

        // --- START ROOMS ---
        // Note: Starts have no Entrance, only an Exit. We'll mark the Entrance as HALLWAY to be safe.
        RAID_START_WEST(13137, 0, 0, 0, 4, RoomType.START, 15, 3, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        RAID_START_NORTH(13137, 4, 0, 0, 4, RoomType.START, 3, 4, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        RAID_START_EAST(13393, 0, 0, 0, 4, RoomType.START, 3, 4, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),

        // --- RESOURCES ---
        RESOURCE_WEST(13141, 0, 0, 1, 4, RoomType.RESOURCE, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        RESOURCE_STRAIGHT(13141, 4, 0, 1, 4, RoomType.RESOURCE, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        RESOURCE_EAST(13397, 0, 0, 1, 4, RoomType.RESOURCE, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),

        // --- ROPE PUZZLE ---
        ROPE_WEST(13139, 0, 4, 1, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ROPE_STRAIGHT(13139, 4, 4, 1, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ROPE_EAST(13395, 0, 4, 1, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- MUTTADILE ---
        MUTADILE_WEST(13139, 0, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        MUTADILE_STRAIGHT(13139, 4, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        MUTADILE_EAST(13395, 0, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- SCAVENGERS ---
        SCAVENGER_WEST(13137, 0, 4, 1, 4, RoomType.SCAVENGER, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        SCAVENGER_STRAIGHT(13137, 4, 4, 1, 4, RoomType.SCAVENGER, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.HALLWAY),
        SCAVENGER_EAST(13393, 0, 4, 1, 4, RoomType.SCAVENGER, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.HALLWAY),

        // --- SKELETAL MYSTICS ---
        SKELE_MAGE_WEST(13138, 0, 4, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.PASSAGE, ConnectionType.PASSAGE),
        SKELE_MAGE_STRAIGHT(13138, 4, 4, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.PASSAGE, ConnectionType.PASSAGE),
        SKELE_MAGE_EAST(13394, 0, 4, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.PASSAGE, ConnectionType.PASSAGE),

        // --- TEKTON ---
        TEKTON_WEST(13138, 0, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        TEKTON_STRAIGHT(13138, 4, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        TEKTON_EAST(13394, 0, 0, 1, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),


        // =====================================================================
        // PLANE 2 (TOP FLOOR) - Base Regions: 13138, 13139
        // =====================================================================

        // --- CRAB PUZZLE ---
        CRABS_WEST(13139, 0, 4, 2, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        CRABS_STRAIGHT(13139, 4, 4, 2, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        CRABS_EAST(13395, 0, 4, 2, 4, RoomType.PUZZLE, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- VESPULA ---
        VESPULA_WEST(13138, 0, 4, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VESPULA_STRAIGHT(13138, 4, 4, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        VESPULA_EAST(13394, 0, 4, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),

        // --- DEATHLY RANGERS/MAGERS ---
        ARCHERS_WEST(13138, 0, 0, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.WEST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ARCHERS_STRAIGHT(13138, 4, 0, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.NORTH, ConnectionType.HALLWAY, ConnectionType.PASSAGE),
        ARCHERS_EAST(13394, 0, 0, 2, 4, RoomType.COMBAT, 16, 16, Direction.SOUTH, Direction.EAST, ConnectionType.HALLWAY, ConnectionType.PASSAGE),


        // =====================================================================
        // THE GREAT OLM
        // =====================================================================
        OLM_CHAMBER(12889, 0, 0, 0, 8, RoomType.BOSS, 31, 23, Direction.SOUTH, Direction.NORTH, ConnectionType.PASSAGE, ConnectionType.PASSAGE);

        private final int regionId, chunkX, chunkY;
        @Getter private final int sourceZ, size;
        @Getter private final RoomType type;
        @Getter private final int spawnX, spawnY;
        @Getter private final Direction nativeEntrance, nativeExit;
        @Getter private final ConnectionType enterType, exitType;

        CoxRoom(int regionId, int chunkX, int chunkY, int sourceZ, int size, RoomType type, int spawnX, int spawnY, Direction nativeEntrance, Direction nativeExit, ConnectionType enterType, ConnectionType exitType) {
            this.regionId = regionId;
            this.chunkX = chunkX;
            this.chunkY = chunkY;
            this.sourceZ = sourceZ;
            this.size = size;
            this.type = type;
            this.spawnX = spawnX;
            this.spawnY = spawnY;
            this.nativeEntrance = nativeEntrance;
            this.nativeExit = nativeExit;
            this.enterType = enterType;
            this.exitType = exitType;
        }

        public int getAbsoluteSourceX() { return (((regionId >> 8) << 3) + chunkX) * 8; }
        public int getAbsoluteSourceY() { return (((regionId & 0xFF) << 3) + chunkY) * 8; }
    }

    private int[] getSafeSpawn(RaidNode startNode) {
        int baseGridX = startNode.getGridX() * 32;
        int baseGridY = startNode.getGridY() * 32;

        int localX = startNode.getRoom().getSpawnX();
        int localY = startNode.getRoom().getSpawnY();

        int rotatedX = localX;
        int rotatedY = localY;

        switch (startNode.getRotation()) {
            case 0: break;
            case 1: rotatedX = localY; rotatedY = 31 - localX; break;
            case 2: rotatedX = 31 - localX; rotatedY = 31 - localY; break;
            case 3: rotatedX = 31 - localY; rotatedY = localX; break;
        }
        return new int[] { baseGridX + rotatedX, baseGridY + rotatedY };
    }

    /**
     * Prints a clean, sequential map of the generated raid to the server console.
     */
    /**
     * Prints a clean, sequential map of the generated raid to the server console.
     */
    public void debugLayout(List<RaidNode> fullLayout) {
        System.out.println("\n=========================================");
        System.out.println("      CHAMBERS OF XERIC MAP GENERATED    ");
        System.out.println("=========================================");

        int currentZ = -1;
        for (int i = 0; i < fullLayout.size(); i++) {
            RaidNode node = fullLayout.get(i);

            // Print a Floor Header whenever the Z-level changes
            if (node.getGridZ() != currentZ) {
                currentZ = node.getGridZ();

                // Calculate the actual physical height the player will be at
                int physicalHeight = currentZ * 4;

                // Dynamically check if this floor is Olm, regardless of Challenge Mode or Normal Mode
                boolean isOlm = node.getRoom().getType() == ChambersOfXeric.RoomType.BOSS;

                String floorName = isOlm ? "THE GREAT OLM" : "FLOOR " + (currentZ + 1);

                System.out.println("\n--- " + floorName + " (Physical Height: Base + " + physicalHeight + ") ---");
            }

            // Print the room details
            String roomName = node.getRoom().name();
            int gX = node.getGridX();
            int gY = node.getGridY();
            int rot = node.getRotation();

            System.out.printf(" -> %-20s [Grid: %d, %d] [Rot: %d]\n", roomName, gX, gY, rot);
        }
        System.out.println("=========================================\n");
    }
    /**
     * Called when the Leader clicks "Start Raid" at the Mount Quidamort door.
     * Generates a 3D instance with 3 distinct floors and the Olm chamber.
     */
    public void startRaid(RaidParty party) {
        if (party == null || party.getMembers().isEmpty()) return;

        // 1. LOCK THE LOBBY
        party.setRaidActive(true);
        party.setAdvertised(false);

        // 2. INITIALIZE GENERATORS
        RaidGenerator generator = new RaidGenerator();
        RaidLayoutGenerator mapper = new RaidLayoutGenerator();

        // This will hold the entire assembled raid (All 3 floors + Olm)
        List<RaidNode> fullLayout = new ArrayList<>();

        // 3. ASSEMBLE THE 3D MAP
        // Floor 1 -> Instance Height + 0
        List<CoxRoom> floor1 = generator.generateFloor(1, party);
        fullLayout.addAll(mapper.mapFloorLayout(floor1, 0));

        // Floor 2 -> Instance Height + 1
        List<CoxRoom> floor2 = generator.generateFloor(2, party);
        fullLayout.addAll(mapper.mapFloorLayout(floor2, 1));

        // Floor 3 -> Instance Height + 2
        List<CoxRoom> floor3 = generator.generateFloor(3, party);
        fullLayout.addAll(mapper.mapFloorLayout(floor3, 2));

        // The Great Olm -> Instance Height + 3
        // Placed safely in the center of the grid (X:2, Y:2) with 0 rotation
        fullLayout.add(new RaidNode(CoxRoom.OLM_CHAMBER, 2, 2, 3, 0));
        debugLayout(fullLayout);
        // 4. CREATE THE INSTANCE
        RaidSession session = new RaidSession(party, fullLayout);

        // 5. PULL THE WHOLE TEAM IN
        for (Player p : party.getMembers()) {
            if (p != null) {
                p.setRaidSession(session);
                p.getPA().closeAllWindows();
                session.add(p); // Registers them to your Instance manager

                // Teleport them to the starting floor (Instance Base Height + 0)
                buildFloor(p, fullLayout, session.getHeight());
            }
        }
    }

    /**
     * Teleports the player to the void, sends the map packet, and safely drops them at the start
     */
    public void buildFloor(final Player player, final List<RaidNode> layout, final int instanceHeight) {
        final int baseX = 1280;
        final int baseY = 5248;

        // Move them to the shared instance height level!
        player.getPA().movePlayer(baseX, baseY, instanceHeight);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int ticks = 0;
            @Override
            public void execute(CycleEventContainer container) {
                ticks++;
                if (ticks == 2) {
                    // Send the dynamically built POH palette to the client
                    player.getRaidSession().refreshRaidMap(player);
                }
                else if (ticks == 4) {
                    // Find the START room to get the exact spawn coordinates
                    RaidNode startNode = null;
                    for (RaidNode node : layout) {
                        if (node.getRoom().getType() == RoomType.START) {
                            startNode = node;
                            break;
                        }
                    }

                    if (startNode != null) {
                        int[] safeCoords = getSafeSpawn(startNode);
                        player.getPA().movePlayer(baseX + safeCoords[0], baseY + safeCoords[1], instanceHeight);
                        player.sendMessage("<col=A349A4>The raid has begun!</col>");
                    }
                    container.stop();
                }
            }
            @Override
            public void stop() {}
        }, 1);
    }
}