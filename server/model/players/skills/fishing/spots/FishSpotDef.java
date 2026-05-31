package server.model.players.skills.fishing.spots;

import java.util.*;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.players.skills.fishing.Fish;
import server.model.players.skills.fishing.SecondaryCatch;
import server.util.Misc;
import server.world.World;

public class FishSpotDef {
	
    public enum SpotCategory {
        ROD_LURE,
        ROD_BAIT,
        CAGE,
        HARPOON,
        BIG_NET,
        BIG_HARPOON,
        SMALL_NET,
        SMALL_NET_BAIT,
        TUTORIAL,
        PISCATORIS,
        BARBARIAN,
        ANGLERFISH;

    }


    // Check if an NPC is a fishing spot
    public static boolean isFishingSpot(int npcId) {
        return NPC_TO_AREA.containsKey(npcId);
    }
    
    public static SpotCategory getCategory(int npcId, int option) {
        FishingArea area = NPC_TO_AREA.get(npcId);
        if (area == null) return null;

        switch (area) {
            case EAST_OF_KOUREND_CASTLE, WEST_OF_GNOME_MAZE, NORTH_OF_ARDOUGNE_TRAINING_SOUTH_BAXTORIAN,
                 NORTH_OF_SEERS, SHILO_VILLAGE, LUMBRIDGE_RIVER, BARBARIAN_VILLAGE_RIVER,
                 EAST_OF_SHAYZIEN, EAST_OF_ARDOUGNE, SOUTH_OF_FARMING_GUILD:
                return (option == 1) ? SpotCategory.ROD_LURE : SpotCategory.ROD_BAIT;

            case FISHING_GUILD, CATHERBY, KARAMJA_DOCKS, MISCELLANIA_DOCKS,
                 RELLEKA_DOCKS_MIDDLE, JATIZSO_DOCKS_CAGE:
                return (option == 1) ? SpotCategory.CAGE : SpotCategory.HARPOON;

            case FISHING_GUILD_BIG, CATHERBY_BIG, RELLEKA_DOCKS_BIG, 
                 WEST_OF_FARMING_GUILD, JATIZSO_DOCKS_BIG:
                return (option == 1) ? SpotCategory.BIG_NET : SpotCategory.BIG_HARPOON;

            case CATHERBY_SMALL, KARAMJA_SMALL, PORT_SARIM_MUSA_POINT, DRAYNOR,
                 AL_KHARID, LUMBRIDGE_SWAMP, OUTSIDE_BARBARIAN_ASSAULT, RELLEKA_DOCKS_SMALL:
                return (option == 1) ? SpotCategory.SMALL_NET_BAIT : SpotCategory.SMALL_NET;

            case TUTORIAL:
                return SpotCategory.TUTORIAL;

            case PISCATORIS:
                return SpotCategory.PISCATORIS;

            case BARBARIAN_ASSAULT_SOUTH, CHAMBERS_OF_XERIC_SOUTH:
                return SpotCategory.BARBARIAN;

            case PISCARILIUS:
                return SpotCategory.ANGLERFISH;

            default:
                return null;
        }
    }



    public static FishingArea getFishingArea(int npcId) {
        return NPC_TO_AREA.get(npcId); // returns null if not a fishing spot
    }

 // Then map NPC IDs → FishingArea
    public static final Map<Integer, FishingArea> NPC_TO_AREA = new HashMap<>();

    static {
        // ---- ROD ----
        NPC_TO_AREA.put(394, FishingArea.EAST_OF_KOUREND_CASTLE);
        NPC_TO_AREA.put(1506, FishingArea.WEST_OF_GNOME_MAZE);
        NPC_TO_AREA.put(1507, FishingArea.WEST_OF_GNOME_MAZE);
        NPC_TO_AREA.put(1508, FishingArea.NORTH_OF_ARDOUGNE_TRAINING_SOUTH_BAXTORIAN);
        NPC_TO_AREA.put(1509, FishingArea.NORTH_OF_ARDOUGNE_TRAINING_SOUTH_BAXTORIAN); // North of west Ardy
        NPC_TO_AREA.put(1513, FishingArea.NORTH_OF_SEERS);
        NPC_TO_AREA.put(1515, FishingArea.SHILO_VILLAGE);
        NPC_TO_AREA.put(1527, FishingArea.LUMBRIDGE_RIVER); // Lumbridge x2
        NPC_TO_AREA.put(1526, FishingArea.BARBARIAN_VILLAGE_RIVER); // Barbarian village x2
        NPC_TO_AREA.put(7464, FishingArea.EAST_OF_SHAYZIEN); // West of Forthos ruins
        NPC_TO_AREA.put(7468, FishingArea.EAST_OF_ARDOUGNE); // Hosidius river
        NPC_TO_AREA.put(8524, FishingArea.SOUTH_OF_FARMING_GUILD);

        // ---- CAGE_HARPOON ----
        NPC_TO_AREA.put(1510, FishingArea.FISHING_GUILD);
        NPC_TO_AREA.put(1519, FishingArea.CATHERBY);
        NPC_TO_AREA.put(1522, FishingArea.KARAMJA_DOCKS);
        NPC_TO_AREA.put(3657, FishingArea.MISCELLANIA_DOCKS);
        NPC_TO_AREA.put(3914, FishingArea.RELLEKA_DOCKS_MIDDLE);
        NPC_TO_AREA.put(5821, FishingArea.JATIZSO_DOCKS_CAGE);

        // ---- BIG_NET_HARPOON ----
        NPC_TO_AREA.put(1511, FishingArea.FISHING_GUILD_BIG);
        NPC_TO_AREA.put(1520, FishingArea.CATHERBY_BIG);
        NPC_TO_AREA.put(3915, FishingArea.RELLEKA_DOCKS_BIG);
        NPC_TO_AREA.put(4476, FishingArea.WEST_OF_FARMING_GUILD);
        NPC_TO_AREA.put(4477, FishingArea.WEST_OF_FARMING_GUILD);
        NPC_TO_AREA.put(5820, FishingArea.JATIZSO_DOCKS_BIG);
        NPC_TO_AREA.put(8526, FishingArea.WEST_OF_FARMING_GUILD);
        NPC_TO_AREA.put(8527, FishingArea.WEST_OF_FARMING_GUILD);

        // ---- SMALL_NET_BAIT ----
        NPC_TO_AREA.put(1518, FishingArea.CATHERBY_SMALL);
        NPC_TO_AREA.put(1521, FishingArea.KARAMJA_SMALL);
        NPC_TO_AREA.put(1523, FishingArea.PORT_SARIM_MUSA_POINT);
        NPC_TO_AREA.put(1525, FishingArea.DRAYNOR);
        NPC_TO_AREA.put(1528, FishingArea.AL_KHARID);
        NPC_TO_AREA.put(1530, FishingArea.LUMBRIDGE_SWAMP);
        NPC_TO_AREA.put(1544, FishingArea.OUTSIDE_BARBARIAN_ASSAULT);
        NPC_TO_AREA.put(3913, FishingArea.RELLEKA_DOCKS_SMALL);

        // ---- TUTORIAL ----
        NPC_TO_AREA.put(3317, FishingArea.TUTORIAL);
        NPC_TO_AREA.put(9478, FishingArea.TUTORIAL);

        // ---- PISCATORIS ----
        NPC_TO_AREA.put(4316, FishingArea.PISCATORIS);

        // ---- BARBARIAN ----
        NPC_TO_AREA.put(1542, FishingArea.BARBARIAN_ASSAULT_SOUTH);
        NPC_TO_AREA.put(7323, FishingArea.CHAMBERS_OF_XERIC_SOUTH);

        // ---- ANGLERFISH ----
        NPC_TO_AREA.put(6825, FishingArea.PISCARILIUS);
    }
}
