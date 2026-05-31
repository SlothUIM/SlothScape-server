package server.model.players.content;

import server.model.players.Player;
import server.model.players.content.SearchObject;
import server.model.players.quests.EleWorkShop;

/**
 * A universal handler for all crates in the game.
 * Handles both generic flavor-text crates and specific quest crates.
 */
public class Crates extends SearchObject {

    private final int objectId;

    public Crates(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public boolean canSearch(Player c) {
        // Here we handle "Gates" (Requirements to even start the search)
        switch (objectId) {
            case 3394: // Leather Crate
            case 3395: // Needle Crate
                if (c.questStages[EleWorkShop.QUEST_ID] < 3) {
                    c.sendMessage("You find nothing of interest.");
                    return false;
                }
                break;
            case 3397: // Stone Bowl Crate
                if (c.questStages[EleWorkShop.QUEST_ID] < 7) {
                    c.sendMessage("The boxes are locked tight.");
                    return false;
                }
                break;
        }
        
        if (c.getItems().freeSlots() < 1) {
            c.sendMessage("You need some inventory space to search this.");
            return false;
        }
        return true;
    }

    @Override
    public String getSearchMessage() {
        return "You search the crate...";
    }

    @Override
    public void handleResults(Player c) {
        // This is where the rewards happen
        switch (objectId) {
            
            case 3394: // Leather Crate
                handleItemFind(c, 1741, "You find a piece of leather.");
                break;

            case 3395: // Needle Crate
                handleItemFind(c, 1733, "You find a needle.");
                break;

            case 3397: // Stone Bowl Crate
            	if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.BELLOWS_PUMPING) {
                    c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.FOUND_BOWL;
                }
                handleQuestItem(c, 2888, "You find a stone bowl.");
                break;

            default:
                // Generic OSRS Crate Message
                c.sendMessage("The crate is empty.");
                break;
        }
    }
    private void handleQuestItem(Player c, int id, String message) {
        if (c.getItems().playerHasItem(id)) {
            c.sendMessage("You find nothing new in the crate.");
        } else {
            c.getItems().addItem(id, 1);
            c.sendMessage(message);
            
            // Auto-progress stage if they now have the required "Set" for the bellows
            if (id == 1741 || id == 1733) {
                if (c.getItems().playerHasItem(1741) && c.getItems().playerHasItem(1733) 
                    && c.questStages[EleWorkShop.QUEST_ID] == 5) {
                    c.questStages[EleWorkShop.QUEST_ID] = 6;
                    c.sendMessage("@blu@You now have the tools to repair the bellows.");
                }
            }
        }
    }
    /**
     * Helper to prevent duplicate quest items.
     */
    private void handleItemFind(Player c, int id, String message) {
        if (c.getItems().playerHasItem(id)) {
            c.sendMessage("You find nothing new in the crate.");
        } else {
            c.getItems().addItem(id, 1);
            c.sendMessage(message);
        }
    }
}