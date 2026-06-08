package server.model.players.skills.agility.impl.rooftop;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.world.World;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A sleek Builder Pattern handler for advanced multi-state agility obstacles.
 */
public class AgilitySequence {
    private final Player c;
    private final int courseIndex;
    private final int obstacleIndex;
    private final int objectId;
    private int baseXp = 0; // Default to 0
    private boolean wasRunning = false;
    private boolean restoreRun = false;
    private boolean isFinish = false;
    private int finishXp;
    private int finishPet;

    private final Queue<Runnable> actions = new LinkedList<>();
    private final Queue<int[]> waitConditions = new LinkedList<>();

    private Runnable currentActionBuffer = () -> {};

    public AgilitySequence(Player c, int courseIndex, int obstacleIndex, int objectId) {
        this.c = c;
        this.courseIndex = courseIndex;
        this.obstacleIndex = obstacleIndex;
        this.objectId = objectId;
    }

    public static AgilitySequence create(Player c, int courseIndex, int obstacleIndex, int objectId) {
        return new AgilitySequence(c, courseIndex, obstacleIndex, objectId);
    }

    // --- BUILDER ACTIONS ---

    public AgilitySequence walk(int x, int y, int z) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.getMovementQueue().addStep(x, y, z); };
        return this;
    }

    public AgilitySequence face(int x, int y) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.turnPlayerTo(x, y); };
        return this;
    }

    public AgilitySequence anim(int animId) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.startAnimation(animId); };
        return this;
    }

    public AgilitySequence stopAnim() {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.stopAnimation(); };
        return this;
    }

    public AgilitySequence teleport(int x, int y, int z) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.getPA().movePlayer(x, y, z); };
        return this;
    }
    public AgilitySequence xp(int xp) {
        this.baseXp = xp;
        return this;
    }
    public AgilitySequence npcAnimation(int npcId, int zHeight, int animId) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> {
            prev.run();
            for (NPC npc : World.getWorld().npcHandler.npcs) {
                if (npc != null && npc.npcType == npcId && npc.getHeight() == zHeight) {
                    npc.startAnimation(animId);
                }
            }
        };
        return this;
    }
    public AgilitySequence hotSpot(int x, int y) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> {
            prev.run();
            c.getMovementQueue().stop(); // Kill the default object pathing
            c.getMovementQueue().addStep(x, y, c.getHeight()); // Force walk to the hotspot
        };
        waitUntil(x, y, -1); // Wait until they physically arrive at the hotspot to continue
        return this;
    }
    public AgilitySequence slide(int pathX, int pathY, int endX, int endY, int z, int offset, String dir, int anim, int spd1, int spd2) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.setMove(new int[][]{{pathX, pathY}}, dir, anim, -1, spd1, spd2, endX, endY, offset, 1, 1, z); };
        return this;
    }

    public AgilitySequence slide(int x, int y, int z, String dir, int anim, int spd1, int spd2) {
        return slide(x, y, x, y, z, 1, dir, anim, spd1, spd2);
    }

    // NEW: Multi-tile segmented slide method
    public AgilitySequence slide(int[][] path, int endX, int endY, int z, int offset, String dir, int anim, int spd1, int spd2) {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> { prev.run(); c.setMove(path, dir, anim, -1, spd1, spd2, endX, endY, offset, 1, 1, z); };
        return this;
    }
    public AgilitySequence walkOff() {
        Runnable prev = currentActionBuffer;
        currentActionBuffer = () -> {
            prev.run();
            if (c.isRunning()) {
                this.wasRunning = true; // Remember that they had run turned on
                c.isRunning = false;    // Turn it off for the jump
                c.getPA().sendFrame36(173, 0); // Visually toggle the client run orb OFF
            }
            this.restoreRun = true; // Flag the sequence to check this at the end
        };
        return this;
    }

    // --- TRIGGERS ---

    public AgilitySequence waitUntil(int x, int y, int z) {
        actions.add(currentActionBuffer);
        waitConditions.add(new int[]{x, y, z});
        currentActionBuffer = () -> {}; // Reset the buffer for the next state
        return this;
    }

    public AgilitySequence finish(int xp, int pet) {
        this.isFinish = true;
        this.finishXp = xp;
        this.finishPet = pet;
        return this;
    }

    // --- EXECUTION ---

    public void execute() {
        actions.add(currentActionBuffer);

        // Security check for sequence breaking
        int prevReq = Math.max(obstacleIndex - 1, 0);
        if (obstacleIndex > 0 && !c.getAgilityHandler().RoofAgilityProgress[courseIndex][prevReq]) {
            c.sendMessage("Apparently I skipped a gap, ouch..");
            return;
        }

        // Fire the initial state immediately
        if (!actions.isEmpty()) {
            actions.poll().run();
        }

        // If no wait conditions exist, trigger progress instantly
        if (waitConditions.isEmpty()) {
            triggerProgress();
            return;
        }

        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int[] req = waitConditions.peek();

                // If coordinates match (passing -1 ignores Z-height check)
                assert req != null;
                if (c.getX() == req[0] && c.getY() == req[1] && (req[2] == -1 || c.getHeight() == req[2])) {
                    waitConditions.poll(); // Clear requirement

                    if (!actions.isEmpty()) {
                        actions.poll().run(); // Fire next state
                    }

                    if (waitConditions.isEmpty()) {
                        triggerProgress();
                        container.stop();
                    }
                }
            }
            @Override
            public void stop() {}
        }, 1);
    }

    private void triggerProgress() {
        c.getAgilityHandler().RoofAgilityProgress[courseIndex][obstacleIndex] = true;
        if (isFinish) {
            c.getAgilityHandler().roofTopFinished(c, obstacleIndex, finishXp, finishPet, courseIndex);
        } else {
            c.getAgilityHandler().lapProgress(c, obstacleIndex, objectId, courseIndex, baseXp);
        }

        // NEW: Restore the player's run state if it was temporarily disabled
        if (restoreRun && wasRunning) {
            c.isRunning = true;
            c.getPA().sendFrame36(173, 1); // Visually toggle the client run orb back ON
        }
    }
}