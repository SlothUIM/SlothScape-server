package server.model.minigames.cox;

import server.clip.Region;
import server.model.players.Player;

public class CoxObjectHandler {

    private static final int RAID_BASE_X = 1280;
    private static final int RAID_BASE_Y = 5248;

    /**
     * Master method called from ObjectOptionOne/Two/etc.
     */
    public static boolean handleObjectClick(Player c, int objectId, int objectX, int objectY) {
        if (c.getRaidSession() == null) {
            return false;
        }

        // 1. Find which room node the object belongs to
        RaidNode currentNode = getNodeFromCoords(c, objectX, objectY);

        if (currentNode == null) {
            return false;
        }

        // 2. Handle Universal Objects (Like Passages)
        if (objectId == 29789) { // CoX Passage
            handleBoundaryDoor(c, objectX, objectY);
            return true;
        }

        if (objectId == 29778) { // CoX stairs out
            c.setRaidSession(null);
            c.getPA().movePlayer(1234, 3569, 0);
            return true;
        }
        switch(objectId) {
            case 29734:
            case 29735:// CoX Floor Transition Hole
                if (c.getRaidSession() != null) {
                    c.getRaidSession().descendFloor(c);
                }
                break;
        }
        // 3. Handle Room-Specific Objects
        // 3. Handle Room-Specific Objects dynamically!
        RaidRoom activeRoom = c.getRaidSession().getRoomAt(objectX, objectY);

        if (activeRoom != null) {
            // Pass the object click directly to the specific room's script.
            // (Note: The '1' at the end is for Option 1. If you call this from ObjectOptionTwo, pass a '2'!)
            boolean handled = activeRoom.handleObjectClick(c, objectId, objectX, objectY, 1);

            if (handled) {
                return true; // The room successfully intercepted and handled the object
            }
        }

        return false; // Not a CoX object, let the normal server handle it
    }

    /**
     * Resolves a global X/Y coordinate back to the specific 5x5 grid node.
     */
    private static RaidNode getNodeFromCoords(Player c, int objectX, int objectY) {
        // Calculate the 5x5 grid index (0-4) based on the absolute coordinates
        int gridX = (objectX - RAID_BASE_X) / 32;
        int gridY = (objectY - RAID_BASE_Y) / 32;

        for (RaidNode node : c.getRaidSession().getLayoutNodes()) {
            if (node.getGridX() == gridX && node.getGridY() == gridY) {
                return node;
            }
        }
        return null;
    }

    private static void handleBoundaryDoor(Player c, int objectX, int objectY) {
        RaidSession session = c.getRaidSession();
        if (session != null) {
            RaidRoom currentRoom = session.getRoomAt(c.getX(), c.getY());
            if (currentRoom != null && !currentRoom.isCleared) {
                c.sendMessage("@red@You must clear this room before you can proceed!");
                return;
            }
        }

        // 1. Shift focus to the center of the doorway to prevent corner-bias
        int centerX = objectX + 1;
        int centerY = objectY + 1;

        int dx = c.getX() - centerX;
        int dy = c.getY() - centerY;

        int targetX = c.getX();
        int targetY = c.getY();

        // 2. Perform the rough leap across the passage
        if (Math.abs(dx) > Math.abs(dy)) {
            targetX += (dx < 0) ? 3 : -3; // East/West jump
        } else {
            targetY += (dy < 0) ? 3 : -3; // North/South jump
        }

        // 3. THE COLLISION SCANNER
        // Check if the tile we landed on is solid using your Region maps
        if (isTileBlocked(targetX, targetY, c.getHeight())) {
            boolean foundSafeTile = false;

            // Scan a 3x3 grid around the landing spot to find the open doorway gap
            for (int offsetX = -1; offsetX <= 1; offsetX++) {
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    // If this adjacent tile is NOT blocked, we claim it as our safe landing spot
                    if (!isTileBlocked(targetX + offsetX, targetY + offsetY, c.getHeight())) {
                        targetX += offsetX;
                        targetY += offsetY;
                        foundSafeTile = true;
                        break;
                    }
                }
                if (foundSafeTile) break;
            }
        }

        c.getPA().movePlayer(targetX, targetY, c.getHeight());
        c.sendMessage("You pass through the passage.");
    }

    /**
     * Helper method to check your specific collision masks.
     * Make sure to adjust 'Region.getClipping' to whatever class your engine uses.
     */
    private static boolean isTileBlocked(int x, int y, int z) {
        // We check for standard unwalkable tiles AND the solid object flag (0x200000) we fixed earlier.
        int clip = Region.getClipping(x, y, z); // Update import path if necessary

        // 0x1280120 = Standard walls/unwalkable
        // 0x200000 = Solid objects (like crystals/thieving chests)
        return (clip & 0x1280120) != 0 || (clip & 0x200000) != 0;
    }
}