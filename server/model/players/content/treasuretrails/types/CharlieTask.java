package server.model.players.content.treasuretrails.types;

import server.model.players.Player;
import server.util.Misc;
import server.model.players.content.treasuretrails.TreasureTrails;

public class CharlieTask {

    public static final int CHARLIE_NPC_ID = 3208;
    public static final int CHARLIE_CLUE_ID = 23200; // Make sure this matches your Beginner Clue ID for Charlie!

    // The exact 8 items Charlie can ask for
    public static final int[] REQUEST_ITEMS = {
        333,  // Trout
        349,  // Pike
        1129, // Leather body
        1095, // Leather chaps
        331,  // Raw trout
        345,  // Raw herring
        440,  // Iron ore
        1203  // Iron dagger
    };

    /**
     * Intercepts First Click NPC for Charlie the Tramp.
     * Returns true if the clue system handled the click, false if normal dialogue should run.
     */
    public static boolean interact(Player c) {
        // If they don't have Charlie's specific clue, ignore this and do normal dialogue
        if (!c.getItems().playerHasItem(CHARLIE_CLUE_ID)) {
            return false; 
        }

        // 1. Player has the clue, but hasn't been assigned a task yet
        if (c.charlieClueTask == 0) {
            c.charlieClueTask = REQUEST_ITEMS[Misc.random(REQUEST_ITEMS.length - 1)];
            String itemName = c.getItems().getItemName(c.charlieClueTask);
            
            // Sends a basic dialogue box (adjust method based on your specific DialogueHandler)
            c.getDH().sendStatement("Charlie looks at your clue...", "I can help you with that, but first...", "Bring me: @blu@" + itemName + "@bla@.");
            c.nextChat = 0;
            return true;
        }

        // 2. Player HAS a task assigned. Let's check their inventory!
        String itemName = c.getItems().getItemName(c.charlieClueTask);
        
        if (c.getItems().playerHasItem(c.charlieClueTask)) {
            // SUCCESS! They brought the item.
            c.getItems().deleteItem(c.charlieClueTask, 1);
            c.charlieClueTask = 0; // Reset the task variable
            
            c.getDH().sendStatement("You give Charlie the " + itemName + ".", "He scribbles on your clue and hands it back.");
            c.nextChat = 0;
            
            // Progress the clue!
            TreasureTrails.progressClue(c, CHARLIE_CLUE_ID);
        } else {
            // FAILED! They forgot the item.
            c.getDH().sendStatement("You still need to bring Charlie:", "@blu@" + itemName + "@bla@.");
            c.nextChat = 0;
        }
        
        return true;
    }
}