package server.model.minigames.cox;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.world.World;
import server.world.objects.GlobalObject;

public class TektonRoom extends RaidRoom {

    // Standard OSRS Tekton IDs
    private static final int TEKTON_SLEEPING = 7544;
    private static final int TEKTON_ACTIVE = 7541;
    // private static final int TEKTON_SMITHING = 7543;

    private static final int ANVIL_OBJECT_ID = 30021;

    private NPC tekton;
    private TektonState state = TektonState.SLEEPING;

    private enum TektonState {
        SLEEPING, ENGAGING, COMBAT, RETREATING, SMITHING, DEAD
    }

    public TektonRoom(RaidSession session, RaidNode node) {
        super(session, node);
    }

    @Override
    public void spawn() {
        // 1. Calculate where the Anvil goes
        int[] anvilCoords = getCoords(16, 22);

        int z = session.getHeight() + (node.getGridZ() * 4);
        // Calculate the correct face/rotation for the anvil!
        // In 317: 0=West, 1=North, 2=East, 3=South
        // You may need to add a base offset depending on how the anvil model faces natively.
        int baseAnvilFace = 1;
        int rotatedFace = (baseAnvilFace + node.getRotation()) % 4;

        // 2. Spawn the Anvil globally for the whole instance
        // Assuming your 7-parameter constructor is: (id, x, y, height, face, type, ticksRemaining)
        // We use -1 or 0 for ticksRemaining so it never despawns!
        GlobalObject anvil = new GlobalObject(ANVIL_OBJECT_ID, anvilCoords[0], anvilCoords[1], z, rotatedFace, 10, -1);
        World.getWorld().getGlobalObjects().add(anvil);

        // 3. Calculate Tekton's spawn (Right next to the anvil)
        int[] tektonCoords = getCoords(16, 20);
        // Spawn him as SLEEPING initially
        tekton = World.getWorld().npcHandler.spawnNpc2(TEKTON_SLEEPING, tektonCoords[0], tektonCoords[1], z, 1, 300, 52, 390, 205);

        System.out.println("[CoX] Tekton spawned asleep at Grid [" + node.getGridX() + "][" + node.getGridY() + "]");
    }

    @Override
    public void tick() {
        if (tekton == null || state == TektonState.DEAD) return;

        switch (state) {
            case SLEEPING:
                // Check if any party member has walked close enough to wake him up
                for (Player p : session.getParty().getMembers()) {
                    if (p != null && p.distanceToPoint(tekton.getX(), tekton.getY()) <= 12) {
                        wakeUpTekton();
                        break;
                    }
                }
                break;

            case COMBAT:
                // TODO: Add logic here to check if he should return to the anvil
                // (e.g., if he hasn't hit anyone in 10 ticks, change state to RETREATING)
                break;

            case RETREATING:
                // TODO: Make him walk back to the anvil coords.
                // Once he reaches them, change state to SMITHING.
                break;

            case SMITHING:
                // TODO: Play smithing animation, heal him, shoot sparks.
                break;
        }
    }

    private void wakeUpTekton() {
        state = TektonState.ENGAGING;

        // Transform the NPC into the attackable/active version
        tekton.requestTransform(TEKTON_ACTIVE);
        tekton.forceChat("Rargh!");
        tekton.gfx100(1200); // Dust/debris graphic

        state = TektonState.COMBAT;
    }

    @Override
    public void handleDeath(NPC npc) {
        if (npc == tekton) {
            state = TektonState.DEAD;
            isCleared = true;

            for (Player p : session.getParty().getMembers()) {
                if (p != null) p.sendMessage("@red@Tekton has been defeated!");
            }

            // TODO: Delete the crystal blocks blocking the exit door to the next room!
        }
    }

    @Override
    public boolean handleObjectClick(Player player, int objectId, int x, int y, int clickType) {
        // If we have custom objects to click in here, handle them.
        return false;
    }
}