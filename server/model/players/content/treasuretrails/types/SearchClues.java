package server.model.players.content.treasuretrails.types;

import java.util.HashMap;
import java.util.Map;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;

public class SearchClues {

    // A structured object to hold the data for each search clue
    public static class SearchData {
        int objectId, objectX, objectY;
        
        public SearchData(int objectId, int objectX, int objectY) {
            this.objectId = objectId;
            this.objectX = objectX;
            this.objectY = objectY;
        }
    }

    // Maps the Clue Scroll Item ID to the Object it belongs to
    public static final Map<Integer, SearchData> SEARCH_CLUES = new HashMap<>();

    static {
    	// ==========================================
        // EASY CLUES (Tier 1)
        // ==========================================
        SEARCH_CLUES.put(2678, new SearchData(357, 3228, 3212)); // Crate in left-hand tower of Lumbridge Castle
        SEARCH_CLUES.put(2679, new SearchData(359, 3245, 3245)); // Crates in the Goblin House in Lumbridge
        SEARCH_CLUES.put(2687, new SearchData(7189, 3251, 3420)); // Drawers, upstairs in the bank to the East of Varrock
        SEARCH_CLUES.put(2677, new SearchData(378, 3209, 3218)); // Chest in the duke of Lumbridge's room

        // ==========================================
        // MEDIUM CLUES (Tier 2)
        // ==========================================
        // Chests
        SEARCH_CLUES.put(2831, new SearchData(375, 2854, 3350)); // Locked chest in the town's chapel (Entrana)
        SEARCH_CLUES.put(2833, new SearchData(375, 2640, 3311)); // Guards armed with maces (East Ardougne Guardhouse Chest)
        SEARCH_CLUES.put(7296, new SearchData(375, 2843, 3241)); // Dead, red dragon watches over this chest (Crandor)
        
        // Drawers
        SEARCH_CLUES.put(3607, new SearchData(349, 2929, 3558)); // Village being attacked by trolls (Burthorpe Drawers)
        SEARCH_CLUES.put(3605, new SearchData(349, 2753, 3158)); // Village where pirates are known to have a good time (Brimhaven Drawers)
        SEARCH_CLUES.put(7298, new SearchData(349, 2509, 3514)); // Building to be illuminated (Lighthouse Drawers)
        SEARCH_CLUES.put(7301, new SearchData(349, 3110, 3163)); // Filled with wizards socks (Wizards' Tower Drawers)
        
        // Crates
        SEARCH_CLUES.put(3610, new SearchData(3395, 2834, 3345)); // Monks that like to paaarty (Entrana Crate)
        SEARCH_CLUES.put(3609, new SearchData(2436, 3498, 3507)); // Different sort of night-life (Canifis Clothing Store Crate)
        SEARCH_CLUES.put(3604, new SearchData(3395, 2796, 3084)); // Village made of bamboo (Tai Bwo Wannai Crate)
        SEARCH_CLUES.put(7300, new SearchData(3395, 2779, 3273)); // Aquatic nasties (Fishing Platform Crate)
        SEARCH_CLUES.put(7303, new SearchData(3395, 3180, 2911)); // Mine, all mine in the desert (Bandit Camp Quarry Crate)
        SEARCH_CLUES.put(7304, new SearchData(3395, 2664, 3444)); // Better reward than a broken arrow (Ranging Guild Crate)
        SEARCH_CLUES.put(19760, new SearchData(12229, 1470, 3564)); // Graceful man of many colours (Osten in Shayzien Crate)
        // You will slowly fill this out using your list!
    }

    /**
     * Call this in your Object Click packet (ClickObject.java or ActionHandler.java)
     */
    public static boolean handleObjectSearch(Player c, int objectId, int objectX, int objectY) {
        for (Map.Entry<Integer, SearchData> entry : SEARCH_CLUES.entrySet()) {
            int clueId = entry.getKey();
            SearchData data = entry.getValue();

            // Check if they clicked the exact object at the exact location
            if (objectId == data.objectId && objectX == data.objectX && objectY == data.objectY) {
                // Check if they actually have the required clue in their inventory!
                if (c.getItems().playerHasItem(clueId)) {
                    // Success! Progress the clue based on tier.
                    // (You would add logic here to check which tier it is, e.g. Easy)
                   TreasureTrails.progressClue(c, clueId); 
                    return true; // We handled the click, stop other object logic
                } else {
                    c.sendMessage("You find nothing of interest.");
                    return true; 
                }
            }
        }
        return false;
    }
}