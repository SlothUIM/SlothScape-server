package server.model.players.content;

import server.model.players.Player;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.util.Misc;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchObject {

	
	/*
	 * 
	 * 
	case 299: // Haystack
	    new SearchObject() {
	        @Override
	        public String getSearchMessage() { return "You rummage through the hay..."; }
	        @Override
	        public void handleResults(Player c) {
	            List<Loot> hayLoot = new java.util.ArrayList<>();
	            hayLoot.add(new Loot(1733, 1, 5)); // 5% chance for a needle
	            rollLoot(c, hayLoot, "You find nothing but straw.");
	        }
	    }.startSearch(c);
    break;
	 * 
	 * 
	 * 
	 */
    public void startSearch(Player c) {
        if (!canSearch(c)) return;

        // OSRS Detail: Face the object
        c.getPA().playerWalk(c.getX(), c.getY()); 
        
        if (getAnimationId() > 0) {
            c.startAnimation(getAnimationId());
        }

        // OSRS Detail: Some objects show overhead text while searching
        if (getOverheadText() != null) {
            c.forcedChat(getOverheadText());
        }

        if (getSearchMessage() != null) {
            c.sendMessage(getSearchMessage());
        }

        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                handleResults(c);
                container.stop();
            }
            @Override
            public void stop() { }
        }, getDelay());
    }

    // --- Overridable Defaults ---
    public boolean canSearch(Player c) { return true; }
    public int getAnimationId() { return 881; } // Standard search
    public int getDelay() { return 2; }
    public String getOverheadText() { return null; }
    
    // --- Abstract Methods ---
    public abstract String getSearchMessage();
    public abstract void handleResults(Player c);

    // --- Loot Table Logic ---
    public static class Loot {
        int id, amount, chance;
        public Loot(int id, int amount, int chance) {
            this.id = id; this.amount = amount; this.chance = chance;
        }
    }

    public void rollLoot(Player c, List<Loot> table, String failMsg) {
        for (Loot loot : table) {
            if (Misc.random(100) <= loot.chance) {
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(loot.id, loot.amount);
                    c.sendMessage("You find " + (loot.amount > 1 ? loot.amount + " " : "a ") + 
                                 c.getItems().getItemName(loot.id).toLowerCase() + ".");
                    return;
                }
            }
        }
        c.sendMessage(failMsg);
    }
}