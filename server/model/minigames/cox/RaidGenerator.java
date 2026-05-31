package server.model.minigames.cox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import server.model.minigames.cox.ChambersOfXeric.CoxRoom;
import server.model.minigames.cox.ChambersOfXeric.RoomType;
import server.model.minigames.raids.RaidParty;

public class RaidGenerator {

    // This Set tracks which room "Families" have been spawned across the ENTIRE raid.
    private Set<String> usedFamilies = new HashSet<>();

    /**
     * Master generation method that checks the Party UI settings to determine layout.
     */
    public List<CoxRoom> generateFloor(int floorNumber, RaidParty party) {
        if (party.isChallengeMode() || party.getMapLayout().equalsIgnoreCase("Full")) {
            return generateChallengeFloor(floorNumber);
        } else if (party.getMapLayout().equalsIgnoreCase("Large")) {
            return generateLargeFloor(floorNumber);
        } else {
            return generateNormalFloor(floorNumber);
        }
    }

    /**
     * Standard Small Layout (7-8 Rooms per floor)
     */
    private List<CoxRoom> generateNormalFloor(int floorNumber) {
        List<CoxRoom> floorPlan = new ArrayList<>();

        // 1. Entrance (Floor 1 gets the actual door, others get the rope down)
        floorPlan.add(floorNumber == 1 ? CoxRoom.RAID_START_WEST : CoxRoom.FLOOR_START);

        // 2. Add 2 to 3 Combat/Puzzle rooms
        int combatPuzzleCount = (Math.random() > 0.5) ? 2 : 3;
        for (int i = 0; i < combatPuzzleCount; i++) {
            floorPlan.add(getRandomRoom(RoomType.COMBAT, RoomType.PUZZLE));
        }

        // 3. Add 1 to 2 Scavengers
        int scavCount = (Math.random() > 0.5) ? 1 : 2;
        for (int i = 0; i < scavCount; i++) {
            floorPlan.add(getRandomRoom(RoomType.SCAVENGER));
        }

        // 4. Add exactly 1 Resource Room
        floorPlan.add(getRandomRoom(RoomType.RESOURCE));

        // 5. Exit (The layout generator assigns the correct directional variant later)
        floorPlan.add(CoxRoom.FLOOR_END);

        // Shuffle the middle rooms so it's random, but keep Start at index 0 and End at the last index
        List<CoxRoom> middleRooms = floorPlan.subList(1, floorPlan.size() - 1);
        Collections.shuffle(middleRooms);

        return floorPlan;
    }

    /**
     * Large Layout (Always 8 Rooms, exactly 4 Combat/Puzzles)
     */
    private List<CoxRoom> generateLargeFloor(int floorNumber) {
        List<CoxRoom> floorPlan = new ArrayList<>();

        floorPlan.add(floorNumber == 1 ? CoxRoom.RAID_START_WEST : CoxRoom.FLOOR_START);

        // Exactly 4 combat/puzzle rooms
        for (int i = 0; i < 4; i++) {
            floorPlan.add(getRandomRoom(RoomType.COMBAT, RoomType.PUZZLE));
        }

        // Exactly 1 Scavenger and 1 Resource
        floorPlan.add(getRandomRoom(RoomType.SCAVENGER));
        floorPlan.add(getRandomRoom(RoomType.RESOURCE));

        floorPlan.add(CoxRoom.FLOOR_END);

        List<CoxRoom> middleRooms = floorPlan.subList(1, floorPlan.size() - 1);
        Collections.shuffle(middleRooms);

        return floorPlan;
    }

    /**
     * Challenge Mode / Full Layout (Static Floors, no RNG)
     */
    private List<CoxRoom> generateChallengeFloor(int floorNumber) {
        List<CoxRoom> floorPlan = new ArrayList<>();

        // In CM, the layout is completely static. The sequence is identical every time.
        if (floorNumber == 1) {
            floorPlan.add(CoxRoom.RAID_START_WEST);
            floorPlan.add(CoxRoom.TEKTON_STRAIGHT);
            floorPlan.add(CoxRoom.CRABS_STRAIGHT);
            floorPlan.add(CoxRoom.SCAVENGER_EAST);
            floorPlan.add(CoxRoom.ICE_DEMON_STRAIGHT);
            floorPlan.add(CoxRoom.SHAMAN_STRAIGHT);
            floorPlan.add(CoxRoom.RESOURCE_EAST);
            floorPlan.add(CoxRoom.FLOOR_END);
        } else if (floorNumber == 2) {
            floorPlan.add(CoxRoom.FLOOR_START);
            floorPlan.add(CoxRoom.VANGUARDS_STRAIGHT);
            floorPlan.add(CoxRoom.THIEVING_STRAIGHT);
            floorPlan.add(CoxRoom.SCAVENGER_EAST);
            floorPlan.add(CoxRoom.VESPULA_STRAIGHT);
            floorPlan.add(CoxRoom.RESOURCE_EAST);
            floorPlan.add(CoxRoom.ROPE_STRAIGHT);
            floorPlan.add(CoxRoom.FLOOR_END);
        } else if (floorNumber == 3) {
            floorPlan.add(CoxRoom.FLOOR_START);
            floorPlan.add(CoxRoom.VASA_STRAIGHT);
            floorPlan.add(CoxRoom.SCAVENGER_EAST);
            floorPlan.add(CoxRoom.SKELE_MAGE_STRAIGHT);
            floorPlan.add(CoxRoom.MUTADILE_STRAIGHT);
            floorPlan.add(CoxRoom.RESOURCE_EAST);
            floorPlan.add(CoxRoom.FLOOR_END);
        }

        return floorPlan;
    }

    /**
     * Strips the directional suffix off a room name.
     * Example: "VASA_WEST" becomes "VASA". "ICE_DEMON_STRAIGHT" becomes "ICE_DEMON".
     */
    private String getRoomFamily(CoxRoom room) {
        String name = room.name();
        int lastDash = name.lastIndexOf('_');
        if (lastDash != -1 && !name.startsWith("FLOOR") && !name.startsWith("RAID_START")) {
            return name.substring(0, lastDash);
        }
        return name;
    }

    /**
     * Helper method to pick a random room from the enum based on its type.
     */
    private ChambersOfXeric.CoxRoom getRandomRoom(RoomType... allowedTypes) {
        List<ChambersOfXeric.CoxRoom> pool = new ArrayList<>();

        for (ChambersOfXeric.CoxRoom room : ChambersOfXeric.CoxRoom.values()) {
            for (RoomType type : allowedTypes) {
                if (room.getType() == type) {
                    // RULE: Don't add if we've already used this family!
                    if (!usedFamilies.contains(getRoomFamily(room))) {
                        pool.add(room);
                    }
                }
            }
        }

        // FALLBACK: If we somehow request a room type and everything is already used
        // (e.g., trying to spawn 5 Scavenger rooms but we only have 1 Scavenger family),
        // we drop the uniqueness rule to prevent the server from crashing.
        if (pool.isEmpty()) {
            System.out.println("[CoX] WARNING: Ran out of unique rooms for type. Allowing duplicate spawn.");
            for (ChambersOfXeric.CoxRoom room : ChambersOfXeric.CoxRoom.values()) {
                for (RoomType type : allowedTypes) {
                    if (room.getType() == type) {
                        pool.add(room);
                    }
                }
            }
        }

        // DOUBLE FALLBACK: If the enum is completely missing a RoomType somehow
        if (pool.isEmpty()) {
            System.out.println("[CoX] CRITICAL: No room found for requested type! Falling back to VASA_STRAIGHT.");
            return ChambersOfXeric.CoxRoom.VASA_STRAIGHT;
        }

        Collections.shuffle(pool);
        CoxRoom selectedRoom = pool.get(0);

        // Register this family so we don't pick it again later in the raid!
        usedFamilies.add(getRoomFamily(selectedRoom));

        return selectedRoom;
    }
}