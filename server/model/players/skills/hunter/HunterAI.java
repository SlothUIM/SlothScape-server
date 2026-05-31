package server.model.players.skills.hunter;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

import server.model.npcs.NPC;
import server.model.npcs.NPCDumbPathFinder;
import server.model.players.Player;
import server.model.players.skills.hunter.trap.Trap;
import server.model.players.skills.hunter.trap.Trap.TrapState;
import server.util.Misc;

public class HunterAI {

    // Tracks which NPC is interacting with which trap
    private static final Map<NPC, Trap> NPC_TARGETS = new HashMap<>();

    // Tracks NPCs that got "bored" so they don't instantly re-target the same trap
    private static final Map<NPC, Long> COOLDOWNS = new HashMap<>();

    // O(1) Fast Lookup for Hunter NPCs
    private static final ImmutableSet<Integer> HUNTER_NPCS = ImmutableSet.of(
            5549, 5551, 5552, 5550, 5548, // Birds
            1505, 2910, 2911, 2912        // Chinchompas/Ferrets
    );

    public static boolean isHunterNpc(int npcId) {
        return HUNTER_NPCS.contains(npcId);
    }

    public static void processNpc(NPC npc) {
        // Cleanup memory if the NPC dies or despawns
        if (npc == null || npc.isDead || npc.getHealth().getAmount() <= 0) {
            NPC_TARGETS.remove(npc);
            COOLDOWNS.remove(npc);
            return;
        }

        // If the NPC recently ignored a trap, let them wander for a bit
        if (COOLDOWNS.containsKey(npc) && System.currentTimeMillis() < COOLDOWNS.get(npc)) {
            return;
        }

        Trap targetTrap = NPC_TARGETS.get(npc);

        // 1. SCENE SCANNING
        if (targetTrap == null) {
            for (Player player : Hunter.GLOBAL_TRAPS.keySet()) {
                for (Trap trap : Hunter.GLOBAL_TRAPS.get(player).getTraps()) {

                    if (trap.getState() == TrapState.PENDING && !trap.isAbandoned() && trap.getObject().getHeight() == npc.getHeight()) {

                        // Check if it accepts the NPC, is within 5 tiles, AND isn't already being targeted by another animal
                        if (trap.canAccept(npc)
                                && Misc.distanceToPoint(npc.getX(), npc.getY(), trap.getObject().getX(), trap.getObject().getY()) <= 5
                                && !NPC_TARGETS.containsValue(trap)) {

                            NPC_TARGETS.put(npc, trap);
                            npc.randomWalk = false;
                            return;
                        }
                    }
                }
            }
            return; // No traps found, keep roaming normally
        }

        // 2. TRAP VALIDATION
        if (targetTrap.isAbandoned() || targetTrap.getState() == TrapState.FALLEN || targetTrap.getState() == TrapState.CAUGHT) {
            resetNpc(npc, false);
            return;
        }

        // NEW: If the trap is in the middle of the catch sequence, let the Trap control the NPC!
        if (targetTrap.getState() == TrapState.TRIGGERING) {
            return;
        }

        // 3. MOVEMENT & INTERACTION
        int distance = Misc.distanceToPoint(npc.getX(), npc.getY(), targetTrap.getObject().getX(), targetTrap.getObject().getY());

        // Keep walking until they are exactly ON the trap
        if (distance > 0) {
            NPCDumbPathFinder.walkTowards(npc, targetTrap.getObject().getX(), targetTrap.getObject().getY());
        }
        // 4. THE DECISION PHASE: They are standing ON the trap!
        else if (distance == 0) {
            // Stop moving
            npc.moveX = 0;
            npc.moveY = 0;
            npc.updateRequired = true;

            NPC_TARGETS.remove(npc); // Clear the target

            // The Wiki Standing Penalty:
            // If the player is standing exactly on the trap, the animal gets spooked and leaves!
            if (targetTrap.getPlayer().getX() == targetTrap.getObject().getX() && targetTrap.getPlayer().getY() == targetTrap.getObject().getY()) {
                resetNpc(npc, true);
                npc.forceChat("Squeak!");
                return;
            }

            // The animal has reached the trap. The trap's formula will decide if it catches or snaps empty!
            targetTrap.trap(npc);
        }
    }

    /**
     * Resets the NPC back to normal behavior.
     * @param bored If true, puts the NPC on a 5-second cooldown so it walks away.
     */
    private static void resetNpc(NPC npc, boolean bored) {
        NPC_TARGETS.remove(npc);
        npc.randomWalk = true;

        if (bored) {
            // Put on a 5-second cooldown
            COOLDOWNS.put(npc, System.currentTimeMillis() + 5000);

            // Force them to take a step away so they don't just stand on the trap
            int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
            int[] dir = directions[Misc.random(3)];
            npc.moveX = dir[0];
            npc.moveY = dir[1];
            npc.getNextNPCMovement();
            npc.updateRequired = true;
        }
    }
}