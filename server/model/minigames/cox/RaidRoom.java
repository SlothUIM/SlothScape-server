package server.model.minigames.cox;

import server.model.players.Player;
import server.model.npcs.NPC;

public abstract class RaidRoom {

    protected RaidSession session;
    protected RaidNode node;
    protected boolean isCleared = false;

    public RaidRoom(RaidSession session, RaidNode node) {
        this.session = session;
        this.node = node;
    }

    /**
     * Called the moment the instance is created.
     * Use this to spawn the room's specific NPCs, Objects, and ground items.
     */
    public abstract void spawn();

    /**
     * Called every server tick while the raid is active.
     * Use this for boss mechanics, poison clouds, or puzzle timers.
     */
    public abstract void tick();

    /**
     * Called from NPCHandler when an NPC inside this specific room dies.
     * Use this to drop keys, track boss deaths, or unlock doors.
     */
    public abstract void handleDeath(NPC npc);

    /**
     * Called from ActionHandler when a player clicks an object in this room.
     * Returns true if the interaction was handled.
     */
    public abstract boolean handleObjectClick(Player player, int objectId, int x, int y, int clickType);

    /**
     * A helper method to quickly get absolute coordinates for spawning.
     */
    protected int[] getCoords(int localX, int localY) {
        return session.getAbsoluteCoordinates(node, localX, localY);
    }
}