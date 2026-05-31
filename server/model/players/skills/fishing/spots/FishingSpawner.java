package server.model.players.skills.fishing.spots;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.world.World;

public class FishingSpawner {

    private static final Random random = new Random();
    private static final Map<FishingArea, List<Integer>> activeNPCs = new java.util.HashMap<>();
    public static void initializeAllFishingSpots() {
        for (int npcId : FishSpotDef.NPC_TO_AREA.keySet()) {
            spawnFishingSpot(npcId);
        }
    }

    public static void spawnFishingSpot(int npcType) {
        FishingArea area = FishSpotDef.NPC_TO_AREA.get(npcType);
        if (area == null) return; // safety check

        for (int i = 0; i < area.getSpawnCount(); i++) {
            int[][] coords = area.getCoords();
            int[] spawnCoord = coords[random.nextInt(coords.length)];

            newNPC(
                    npcType,
                    spawnCoord[0],
                    spawnCoord[1],
                    spawnCoord[2],
                    0,    // WalkingType (stationary)
                    100,  // HP
                    0,    // maxHit
                    0,    // attack
                    0     // defence
                );

            int timer = getRespawnTimer(area);
            scheduleFishingSpotMove(area, npcType, timer);
        }
    }



    private static int getRespawnTimer(FishingArea area) {
        // Default: normal fishing spots
        int min = 250, max = 530;

        switch (area) {
            case PISCARILIUS: // Anglerfish
                min = 8; 
                max = 830;
                break;
            /*case BIG_NET_HARPOON: // Big Net spot in Civitas illa Fortis (if you want immobile)
                min = max = Integer.MAX_VALUE;
                break;
            case SACRED_EEL: // Example for eels
            case INFERNAL_EEL:
                min = 100;
                max = 300;
                break;*/
                default:
                    min = 250; 
                    max = 530;
                break;
        }

        return min + random.nextInt(max - min + 1);
    }

    private static void scheduleFishingSpotMove(FishingArea area, int npcId, int timer) {
        CycleEventHandler.getSingleton().addEvent(npcId, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                NPC npc = NPCHandler.getNpc(npcId);
                if (npc == null) {
                    container.stop();
                    return;
                }

                // Pick a *different* random coordinate
                int[][] coords = area.getCoords();
                int[] newCoord;
                do {
                	npc.makeX = 0;
                	npc.makeY = 0;
                    // npc.needRespawn = false;
                    // npc.isDead = true; // Or use your NPCHandler.removeNPC if you have one
                     npc.updateRequired = true;
                    newCoord = coords[random.nextInt(coords.length)];
                } while (newCoord[0] == npc.absX && newCoord[1] == npc.absY);
                // Despawn first (hide for a short delay)

                int disappearDelay = 5; // ticks to wait before respawning (adjust as you like)
                final int newcoords[] = newCoord;
                // Schedule reappearance
                CycleEventHandler.getSingleton().addEvent(npcId, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer inner) {
                        NPC npc = NPCHandler.getNpc(npcId);
                        if (npc == null) {
                            inner.stop();
                            return;
                        }

                        npc.makeX = newcoords[0];
                        npc.makeY = newcoords[1];
                        npc.teleport(newcoords[0], newcoords[1], npc.getHeight());
                        npc.isDead = false; // reappear
                        npc.updateRequired = true;

                        if (Config.SERVER_DEBUG) {
                           // System.out.printf("Fishing spot %d moved to (%d, %d)%n", 
                                //npcId, newcoords[0], newcoords[1]);
                        }

                        // Reschedule next movement
                        int nextTimer = getRespawnTimer(area);
                        scheduleFishingSpotMove(area, npcId, nextTimer);

                        inner.stop();
                    }

                    @Override
                    public void stop() {}
                }, disappearDelay);

                container.stop();
            }

            @Override
            public void stop() {}
        }, timer);
    }



    private static void newNPC(int npcType, int x, int y, int heightLevel,
                        int WalkingType, int HP, int maxHit, int attack, int defence) {
    	World.getWorld().npcHandler.newNPC(npcType, x, y, heightLevel,
                WalkingType, HP, maxHit, attack, defence);
        // Call your server's method to spawn NPC
    }
}
