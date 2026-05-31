package server.clip.ladders;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.quests.*;

public class LadderHandler {

    // Specific overrides: map "X_Y_H" to unique destination
    private static final Map<String, LadderData> coordinateOverrides = new HashMap<>();
    
    // Global defaults: map "ObjectID" to a simple height change
    private static final Map<Integer, GlobalLadderData> globalDefaults = new HashMap<>();

    private static class LadderData {
        int targetX, targetY, targetHeight;
        String animation;
        Predicate<Player> requirement;
        boolean isStatic;

        // Static Teleport (Specific Coords)
        LadderData(int x, int y, int h, String anim, Predicate<Player> req) {
            this.targetX = x;
            this.targetY = y;
            this.targetHeight = h;
            this.animation = anim;
            this.requirement = req;
            this.isStatic = true;
        }

        // Dynamic Teleport (Same X/Y, New H)
        LadderData(int h, String anim, Predicate<Player> req) {
            this.targetHeight = h;
            this.animation = anim;
            this.requirement = req;
            this.isStatic = false;
        }
    }

    private static class GlobalLadderData {
        int heightOffset; // e.g., +1 for up, -1 for down
        String animation;
        Predicate<Player> requirement;

        GlobalLadderData(int offset, String anim) {
            this.heightOffset = offset;
            this.animation = anim;
        }
        GlobalLadderData(int offset, String anim, Predicate<Player> req) {
            this.heightOffset = offset;
            this.animation = anim;
            this.requirement = req;
        }
    }

    public static void addOverride(int objX, int objY, int objHeight, int dX, int dY, int dH, String anim, Predicate<Player> req) {
        coordinateOverrides.put(objX + "_" + objY + "_" + objHeight, new LadderData(dX, dY, dH, anim, req));
    }

    public static void addGlobal(int objectId, int heightOffset, String anim) {
        globalDefaults.put(objectId, new GlobalLadderData(heightOffset, anim));
    }
    public static void addGlobal(int objectId, int heightOffset, String anim, Predicate<Player> req) {
        globalDefaults.put(objectId, new GlobalLadderData(heightOffset, anim, req));
    }

    public static boolean handleLadder(Player c, int objectId, int x, int y) {
        // 1. Check for specific coordinate override first (Dungeons, special teleports)
        LadderData override = coordinateOverrides.get(x + "_" + y + "_" + c.getHeightLevel);
        
        if (override != null) {
            if (override.requirement != null && !override.requirement.test(c)) return true;
            if (c.distanceToPoint(x, y) <= 1) {
                int finalX = override.isStatic ? override.targetX : c.getLocation().getX();
                int finalY = override.isStatic ? override.targetY : c.getLocation().getY();
                AgilityHandler.delayEmote(c, override.animation, finalX, finalY, override.targetHeight, 2);
                return true;
            }
        }

        // 2. Fallback to Global Defaults (Generic height changes)
        GlobalLadderData global = globalDefaults.get(objectId);
        if (global != null) {
            // --- FIX: Added the missing requirement check for global ladders ---
            if (global.requirement != null && !global.requirement.test(c)) {
                return true;
            }

            if (c.distanceToPoint(x, y) <= 1) {
                AgilityHandler.delayEmote(c, global.animation, c.getLocation().getX(), c.getLocation().getY(), c.getLocation().getZ() + global.heightOffset, 2);
                return true;
            }
        }
        
        return false;
    }

    static {
        // --- GLOBAL DEFAULTS ---
        // Register standard "Up" and "Down" IDs once for the whole world
        addGlobal(16683, 1, "CLIMB_UP"); 
        addGlobal(16679, -1, "CLIMB_DOWN");

        addGlobal(17122, -1, "CLIMB_DOWN");
        
        addGlobal(2796, 1, "CLIMB_UP"); 
        addGlobal(2797, -1, "CLIMB_DOWN");
        addGlobal(12964, 1, "CLIMB_UP");
        addGlobal(12966, -1, "CLIMB_DOWN");
        
        addGlobal(2833, 1, "CLIMB_UP", c -> {
			QuestManager questManager = new WatchTower(c);
        	if(questManager.getCurrentStage() > 0) {
				c.getDH().npcChat(4405, "Watchtower Guard", 1, "It is the wizards' helping hand - let 'em up");
				return true;
			} else {
				return false;
			}
        }); 
        // --- COORDINATE OVERRIDES ---
        // This ladder at these coords doesn't just change height; it changes regions
       // addOverride(3096, 3511, 0, 3096, 9511, 0, "CLIMB_DOWN", c -> true); 
    }
}