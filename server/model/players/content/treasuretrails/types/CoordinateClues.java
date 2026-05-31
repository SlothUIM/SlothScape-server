package server.model.players.content.treasuretrails.types;

import java.util.HashMap;
import java.util.Map;

import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;

public class CoordinateClues {

	public static class CoordinateData {
        int digX, digY, tier;
        public String[] hintText; // Stores the exact text to draw on the clue scroll!
        
        // Constructor 1: Used for Beginner/Map Clues where we already know the exact X/Y
        public CoordinateData(int digX, int digY, int tier) {
            this.digX = digX;
            this.digY = digY;
            this.tier = tier;
            this.hintText = new String[] { "Follow the map to find", "the hidden treasure." };
        }

        // Constructor 2: Used for Coordinate Clues (Calculates X/Y AND formats the text!)
        public CoordinateData(int degY, int minY, String dirY, int degX, int minX, String dirX, int tier) {
            this.digX = calculateX(degX, minX, dirX);
            this.digY = calculateY(degY, minY, dirY);
            this.tier = tier;
            
            // This formats it exactly like OSRS: "01 degrees 35 minutes South,"
            // The %02d ensures that single digits get a leading zero (e.g. 5 becomes 05)
            String line1 = String.format("%02d degrees %02d minutes %s,", degY, minY, dirY);
            String line2 = String.format("%02d degrees %02d minutes %s", degX, minX, dirX);
            
            this.hintText = new String[] { line1, line2 };
        }

        private int calculateX(int degrees, int minutes, String direction) {
            int tiles = (degrees * 32) + Math.round((minutes * 32.0f) / 60.0f);
            return direction.equalsIgnoreCase("East") ? (2440 + tiles) : (2440 - tiles);
        }

        private int calculateY(int degrees, int minutes, String direction) {
            int tiles = (degrees * 32) + Math.round((minutes * 32.0f) / 60.0f);
            return direction.equalsIgnoreCase("North") ? (3161 + tiles) : (3161 - tiles);
        }
    }

    public static final Map<Integer, CoordinateData> COORD_CLUES = new HashMap<>();

    static {
        // ==========================================
        // HARD CLUES (Tier 3)
        // ==========================================
        COORD_CLUES.put(2723, new CoordinateData(22, 35, "North", 19, 18, "East", 3)); // 22.35N 19.18E (Wilderness)
        COORD_CLUES.put(2725, new CoordinateData(25, 3, "North", 17, 5, "East", 3)); // 25.03N 17.05E (Wilderness)
        COORD_CLUES.put(3526, new CoordinateData(16, 3, "North", 14, 7, "East", 3)); // 16.03N 14.07E (Shipyard)

        // ==========================================
        // MEDIUM CLUES (Tier 2)
        // ==========================================
        COORD_CLUES.put(2801, new CoordinateData(2, 48, "North", 22, 30, "East", 2)); // 02.48N 22.30E
        COORD_CLUES.put(2803, new CoordinateData(1, 35, "South", 7, 28, "East", 2));  // 01.35S 07.28E
        COORD_CLUES.put(2805, new CoordinateData(1, 26, "North", 8, 1, "East", 2));   // 01.26N 08.01E
        COORD_CLUES.put(2807, new CoordinateData(6, 31, "North", 1, 46, "West", 2));  // 06.31N 01.46W
        COORD_CLUES.put(2809, new CoordinateData(0, 5, "South", 1, 13, "East", 2));   // 00.05S 01.13E
        COORD_CLUES.put(2811, new CoordinateData(9, 33, "North", 2, 15, "East", 2));  // 09.33N 02.15E
        COORD_CLUES.put(2813, new CoordinateData(2, 50, "North", 6, 20, "East", 2));  // 02.50N 06.20E
        COORD_CLUES.put(2815, new CoordinateData(4, 13, "North", 12, 45, "East", 2)); // 04.13N 12.45E
        COORD_CLUES.put(2817, new CoordinateData(4, 0, "South", 12, 46, "East", 2));  // 04.00S 12.46E (Wiki typo said North)
        COORD_CLUES.put(2819, new CoordinateData(0, 31, "South", 17, 43, "East", 2)); // 00.31S 17.43E
        COORD_CLUES.put(2821, new CoordinateData(7, 33, "North", 15, 0, "East", 2));  // 07.33N 15.00E
        COORD_CLUES.put(2823, new CoordinateData(0, 30, "North", 24, 16, "East", 2)); // 00.30N 24.16E
        COORD_CLUES.put(2825, new CoordinateData(5, 43, "North", 23, 5, "East", 2));  // 05.43N 23.05E
        
        COORD_CLUES.put(3582, new CoordinateData(11, 3, "North", 31, 20, "East", 2)); // 11.03N 31.20E
        COORD_CLUES.put(3584, new CoordinateData(7, 5, "North", 30, 56, "East", 2));  // 07.05N 30.56E
        COORD_CLUES.put(3586, new CoordinateData(11, 41, "North", 14, 58, "East", 2)); // 11.41N 14.58E
        COORD_CLUES.put(3588, new CoordinateData(0, 13, "South", 13, 58, "East", 2)); // 00.13S 13.58E
        COORD_CLUES.put(3590, new CoordinateData(0, 18, "South", 9, 28, "East", 2));  // 00.18S 09.28E
        COORD_CLUES.put(3592, new CoordinateData(8, 33, "North", 1, 39, "West", 2));  // 08.33N 01.39W
        COORD_CLUES.put(3594, new CoordinateData(11, 5, "North", 0, 45, "West", 2));  // 11.05N 00.45W
        
        COORD_CLUES.put(7305, new CoordinateData(22, 30, "North", 3, 1, "East", 2));  // 22.30N 03.01E
        COORD_CLUES.put(7307, new CoordinateData(5, 20, "South", 4, 28, "East", 2));  // 05.20S 04.28E
        COORD_CLUES.put(7309, new CoordinateData(1, 18, "South", 14, 15, "East", 2)); // 01.18S 14.15E
        COORD_CLUES.put(7311, new CoordinateData(9, 48, "North", 17, 39, "East", 2)); // 09.48N 17.39E
        COORD_CLUES.put(7313, new CoordinateData(0, 20, "South", 23, 15, "East", 2)); // 00.20S 23.15E
        COORD_CLUES.put(7315, new CoordinateData(14, 54, "North", 9, 13, "East", 2)); // 14.54N 09.13E
        COORD_CLUES.put(7317, new CoordinateData(3, 35, "South", 13, 35, "East", 2)); // 03.35S 13.35E
        
        COORD_CLUES.put(12033, new CoordinateData(8, 11, "South", 4, 48, "East", 2)); // 08.11S 04.48E
        COORD_CLUES.put(12035, new CoordinateData(2, 43, "South", 33, 26, "East", 2)); // 02.43S 33.26E
        COORD_CLUES.put(12037, new CoordinateData(12, 28, "North", 34, 37, "East", 2)); // 12.28N 34.37E
        COORD_CLUES.put(12039, new CoordinateData(15, 22, "North", 7, 31, "East", 2)); // 15.22N 07.31E
        COORD_CLUES.put(12041, new CoordinateData(3, 7, "South", 3, 41, "West", 2));  // 03.07S 03.41W
        COORD_CLUES.put(12043, new CoordinateData(6, 58, "North", 21, 16, "East", 2)); // 06.58N 21.16E
        COORD_CLUES.put(12045, new CoordinateData(9, 35, "North", 1, 50, "West", 2));  // 09.35N 01.50W
        COORD_CLUES.put(12047, new CoordinateData(11, 33, "North", 2, 24, "West", 2)); // 11.33N 02.24W
        COORD_CLUES.put(12049, new CoordinateData(10, 45, "North", 4, 31, "East", 2)); // 10.45N 04.31E
        COORD_CLUES.put(12051, new CoordinateData(6, 41, "North", 27, 15, "East", 2)); // 06.41N 27.15E
        COORD_CLUES.put(12053, new CoordinateData(11, 18, "North", 30, 54, "East", 2)); // 11.18N 30.54E
        
        COORD_CLUES.put(19774, new CoordinateData(12, 39, "North", 30, 7, "West", 2)); // 12.39N 30.07W
        
        COORD_CLUES.put(23131, new CoordinateData(17, 39, "North", 37, 16, "West", 2)); // 17.39N 37.16W
        COORD_CLUES.put(23133, new CoordinateData(2, 16, "North", 12, 7, "East", 2));   // 02.16N 12.07E
        COORD_CLUES.put(23135, new CoordinateData(23, 5, "North", 41, 22, "East", 2));  // 23.05N 41.22E
        
        COORD_CLUES.put(28909, new CoordinateData(1, 33, "South", 24, 24, "West", 2)); // 01.33S 24.24W
        COORD_CLUES.put(31275, new CoordinateData(22, 7, "South", 23, 13, "East", 2)); // 22.07S 23.13E

    }

    public static boolean handleDigging(Player c) {
        for (Map.Entry<Integer, CoordinateData> entry : COORD_CLUES.entrySet()) {
            int clueId = entry.getKey();
            CoordinateData coords = entry.getValue();

            // If the player has the clue, check their distance to the calculated spot
            if (c.getItems().playerHasItem(clueId)) {
                
                int distanceX = Math.abs(c.getX() - coords.digX);
                int distanceY = Math.abs(c.getY() - coords.digY);
                
                // Allow a 2-tile radius so they don't have to guess the exact pixel
                if (distanceX <= 2 && distanceY <= 2) {
                    c.startAnimation(830); 
                    
                    // WIZARD SPAWN CHECK (Hard Clues)
                    if (coords.tier == 3 && !c.killedClueWizard) {
                        // Spawn Zamorak Wizard (NPC 3129)
                        NPCHandler.spawn(3129, c.getX() + 1, c.getY(), c.getLocation().getZ(), 1, 100, 15, 100, 100, true);
                        c.sendMessage("A Zamorak Wizard appears and attacks you!");
                        return true; 
                    }
                    TreasureTrails.progressClue(c, clueId);
                    c.killedClueWizard = false; 
                    return true; 
                }
            }
        }
        return false;
    }
    
}