package server.model.players.skills.crafting;

import server.model.players.Player;
import server.model.players.Player.ChatboxAction;

public class Crafting {

    private final Player c;
    
    // Sub-modules
    private final GemCutting gemCutting;
    private final LeatherCrafting leatherCrafting;
    private final GlassBlowing glassBlowing;
    private final Spinning spinning; // <--- ADDED THIS

    public Crafting(Player player) {
        this.c = player;
        this.gemCutting = new GemCutting(player);
        this.leatherCrafting = new LeatherCrafting(player);
        this.glassBlowing = new GlassBlowing(player); 
        this.spinning = new Spinning(player); // <--- ADDED THIS
    }

    /**
     * Routes Item on Item actions (e.g., Chisel on Gem, Needle on Leather)
     */
    public boolean handleItemOnItem(int itemUsed, int usedWith) {
        if (gemCutting.isCuttingGem(itemUsed, usedWith)) {
            return true;
        }
        if (leatherCrafting.isMakingLeather(itemUsed, usedWith)) {
            return true;
        }
        if (glassBlowing.isBlowingGlass(itemUsed, usedWith)) {
            return true;
        }
        return false; 
    }

    /**
     * Routes button clicks from Crafting interfaces
     */
    public boolean handleButtons(int buttonId) {
        if (leatherCrafting.handleButtons(buttonId) && c.activeAction == ChatboxAction.LEATHERCRAFTING) {
            return true;
        }
        if (glassBlowing.handleButtons(buttonId) && c.activeAction == ChatboxAction.GLASSBLOWING) {
        	return true;
        }
        if (spinning.handleButtons(buttonId) && c.activeAction == ChatboxAction.SPINNING) { // <--- ADDED THIS
            return true;
        }
        return false; 
    }
    
    // Getters
    public LeatherCrafting getLeather() {
        return leatherCrafting;
    }

    public Spinning getSpinning() { // <--- ADDED THIS
        return spinning;
    }
}