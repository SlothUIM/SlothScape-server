package server.model.players.content;

import server.model.players.Player;
import server.model.players.quests.EleWorkShop;
import server.model.players.quests.ShieldArrav;
import server.util.Misc;

/**
 * Handles the interaction with all bookcases in the game.
 * Now fully integrated with the SearchObject base class.
 */
public class Bookcase extends SearchObject {

    private final int objectId;

    public Bookcase(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public String getSearchMessage() {
        // We can randomize the initial search message too
        String[] starts = {"You search the books...", "You look through the library...", "You examine the shelf..."};
        return starts[Misc.random(starts.length - 1)];
    }

    @Override
    public void handleResults(Player c) {
        // 1. POH Interface Check
        if (isPOHBookcase(objectId)) {
            openPOHInterface(c);
            return;
        }

        // 2. Quest Book Logic
        if (handleQuestBooks(c, objectId)) {
            return;
        }

        // 3. Fallback Flavor Text (The second message after the delay)
        String[] ends = {
            "You find nothing of interest.",
            "Most of these books are too dusty to read.",
            "It's just a collection of old, boring history books.",
            "You find a book titled 'How to avoid being thieved', but it's empty."
        };
        c.sendMessage(ends[Misc.random(ends.length - 1)]);
    }

    private boolean handleQuestBooks(Player c, int id) {
        switch (id) {
            case 2402: // Shield of Arrav
                if (c.questStages[ShieldArrav.QUEST_ID] == 1) {
                    c.getItems().addItem(267, 1);
                    c.sendMessage("You find an old book about the Shield of Arrav.");
                    return true;
                }
                break;

            case 26113: // Elemental Workshop
                if (c.questStages[EleWorkShop.QUEST_ID] == 0 && !hasItem(c, 2886) && !hasItem(c, 9715)) {
                    c.getItems().addItem(2886, 1);
                    c.sendMessage("You find an old battered up book.");
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean isPOHBookcase(int id) {
        return id == 13597 || id == 13598 || id == 13599;
    }

    private void openPOHInterface(Player c) {
        c.sendMessage("You open the POH Bookcase interface.");
        // c.getPA().showInterface(63000); 
    }

    public static boolean hasItem(Player c, int itemID) {
        return c.getItems().playerHasItem(itemID) || c.getItems().bankContains(itemID);
    }
}