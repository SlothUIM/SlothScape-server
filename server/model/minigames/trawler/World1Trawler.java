package server.model.minigames.trawler;

import java.util.List;
import java.util.ArrayList;

import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

public class World1Trawler extends BaseFishingTrawler {

    // --- Object IDs ---
    private static final int LEAK_OBJ = 37351;
    private static final int REPAIRED_LEAK_OBJ = 37352;
    private static final int SWAMP_PASTE = 7833;
    private static final int BUCKET = 3727;
    private static final int WATER_OBJ = 2164;
    private static final int ROPE = 954;
    private static final int HAMMER = 2347;

    private static final int TORN_NET_OBJ = 2161;
    private static final int BROKEN_RAIL_OBJ = 2163;
    private static final int NORMAL_RAIL_OBJ = 2166;

    // --- Kraken NPC IDs ---
    private static final int TENTACLE_ACTIVE_1 = 5534;
    private static final int TENTACLE_ACTIVE_2 = 5535;
    private static final int TENTACLE_WHIRLPOOL = 5536;

    // Base coordinates for the DRY ship (State 0)
    private static final int[][] LEAK_COORDS = {
            {1884, 4827, 0}, {1885, 4823, 0},
            {1892, 4827, 2}, {1893, 4823, 2}
    };

    private static final int[][] RAILING_COORDS = {
            {1884, 4824, 0}, {1884, 4825, 0}, {1884, 4826, 0}, {1884, 4827, 0},
            {1891, 4824, 2}, {1891, 4825, 2}, {1891, 4826, 2}, {1891, 4827, 2}
    };

    // FIX: Track indices to avoid array memory reference traps
    private final List<Integer> activeLeakIndices = new ArrayList<>();
    private int currentKrakenSpotIndex = -1;

    // FIX: Store the NPC Index, NOT the NPC Object
    private int activeTentacleIndex = -1;

    @Override
    protected void onGameStart() {
        for (Player p : participants) {
            p.getPA().movePlayer(1887, 4825, 0);
        }
    }

    @Override
    protected void onGameEnd(boolean success) {
        int currentOffset = currentShipState * 43;

        // Clean up leaks safely
        for (int leakIndex : activeLeakIndices) {
            int[] spot = LEAK_COORDS[leakIndex];
            World.getWorld().getGlobalObjects().add(new GlobalObject(REPAIRED_LEAK_OBJ, spot[0] + currentOffset, spot[1], 0, spot[2], 10, -1, -1));
        }
        activeLeakIndices.clear();

        // FIX: Clean up Kraken instantly using our new NPCHandler method
        if (activeTentacleIndex != -1) {
            NPC tentacle = NPCHandler.npcs[activeTentacleIndex];
            if (tentacle != null) {
                World.getWorld().getNpcHandler().removeNpc(tentacle);
            }
            activeTentacleIndex = -1;
        }

        currentKrakenSpotIndex = -1;
        isNetBroken = false;
        krakenActive = false;
        railBroken = false;
    }

    @Override
    protected void handleRandomEvent() {
        int event = Misc.random(3);

        if (event == 0 && activeLeakIndices.size() < 5) {
            int spotIndex = Misc.random(LEAK_COORDS.length - 1);
            if (!activeLeakIndices.contains(spotIndex)) {
                activeLeakIndices.add(spotIndex);
                int[] spot = LEAK_COORDS[spotIndex];
                int actualX = spot[0] + (currentShipState * 43);

                World.getWorld().getGlobalObjects().add(new GlobalObject(LEAK_OBJ, actualX, spot[1], 0, spot[2], 10, -1, -1));
                for (Player p : participants) p.sendMessage("A new leak has sprung!");
            }
        }
        else if (event == 1 && !isNetBroken) {
            isNetBroken = true;
            for (Player p : participants) p.sendMessage("The trawler net has been torn!");
        }
        else if (event == 2 && !krakenActive && !railBroken) {
            krakenActive = true;
            currentKrakenSpotIndex = Misc.random(RAILING_COORDS.length - 1);
            int[] spot = RAILING_COORDS[currentKrakenSpotIndex];
            int actualX = spot[0] + (currentShipState * 43);

            // Spawn the active Slayer Tentacle and save its Index
            NPC spawnedTentacle = World.getWorld().getNpcHandler().spawnNpc(TENTACLE_ACTIVE_1, actualX, spot[1], 0, 0, 0, 0, 0, 0);
            if (spawnedTentacle != null) {
                activeTentacleIndex = spawnedTentacle.getIndex();
            }

            for (Player p : participants) p.sendMessage("A Kraken tentacle grabs the ship's railing!");
        }
    }

    @Override
    protected void onShipStateChange() {
        // Move Leaks
        for (int leakIndex : activeLeakIndices) {
            int[] spot = LEAK_COORDS[leakIndex];
            int newX = spot[0] + (currentShipState * 43);
            World.getWorld().getGlobalObjects().add(new GlobalObject(LEAK_OBJ, newX, spot[1], 0, spot[2], 10, -1, -1));
        }

        // Move Kraken NPC
        if (krakenActive && activeTentacleIndex != -1 && currentKrakenSpotIndex != -1) {
            NPC tentacle = NPCHandler.npcs[activeTentacleIndex];
            if (tentacle != null) {
                int[] spot = RAILING_COORDS[currentKrakenSpotIndex];
                int newX = spot[0] + (currentShipState * 43);
                tentacle.setX(newX);
                tentacle.updateRequired = true;
            }
        }

        // Move Broken Rail
        if (railBroken && currentKrakenSpotIndex != -1) {
            int[] spot = RAILING_COORDS[currentKrakenSpotIndex];
            int newX = spot[0] + (currentShipState * 43);
            World.getWorld().getGlobalObjects().add(new GlobalObject(BROKEN_RAIL_OBJ, newX, spot[1], 0, spot[2], 10, -1, -1));
        }
    }

    @Override
    protected int getActiveLeakCount() {
        return activeLeakIndices.size();
    }

    @Override
    public void handleObjectClick(Player p, int objectId) {
        if (objectId == WATER_OBJ) {
            if (p.getItems().playerHasItem(BUCKET)) {
                p.startAnimation(832);
                adjustWaterLevel(-1);
                addContribution(p, 2);
                p.sendMessage("You bail some water overboard.");
            } else {
                p.sendMessage("You need a bailing bucket to get rid of the water!");
            }
        }
    }

    @Override
    public void handleNpcClick(Player p, int npcId) {
        if (npcId == TENTACLE_ACTIVE_1 || npcId == TENTACLE_ACTIVE_2 || npcId == TENTACLE_WHIRLPOOL) {
            if (krakenActive && activeTentacleIndex != -1 && currentKrakenSpotIndex != -1) {

                // Assuming you migrated to the safer getSkills() format we noticed in your Player class
                if (p.getSkills().getLevel(Skill.WOODCUTTING) > 0) {
                    p.startAnimation(2876);

                    krakenActive = false;
                    railBroken = true;

                    // FIX: Delete the NPC safely and instantly
                    NPC tentacle = NPCHandler.npcs[activeTentacleIndex];
                    if (tentacle != null) {
                        World.getWorld().getNpcHandler().removeNpc(tentacle);
                    }
                    activeTentacleIndex = -1;

                    int[] spot = RAILING_COORDS[currentKrakenSpotIndex];
                    int actualX = spot[0] + (currentShipState * 43);
                    World.getWorld().getGlobalObjects().add(new GlobalObject(BROKEN_RAIL_OBJ, actualX, spot[1], 0, spot[2], 10, -1, -1));

                    addContribution(p, 10);
                    p.getSkills().addExperience(5, Skill.WOODCUTTING);
                    p.sendMessage("You chop the Kraken tentacle away, but the railing is smashed!");
                } else {
                    p.sendMessage("You need a woodcutting axe to chop the tentacle!");
                }
            }
        }
    }

    @Override
    public void handleItemOnObject(Player p, int itemId, int objectId, int objX, int objY) {
        if (itemId == SWAMP_PASTE && objectId == LEAK_OBJ) {
            int baseObjX = objX - (currentShipState * 43);
            int patchedIndex = -1;

            for (int i = 0; i < activeLeakIndices.size(); i++) {
                int[] spot = LEAK_COORDS[activeLeakIndices.get(i)];
                if (spot[0] == baseObjX && spot[1] == objY) {
                    patchedIndex = i;
                    break;
                }
            }

            if (patchedIndex != -1) {
                p.startAnimation(898);
                p.getItems().deleteItem(SWAMP_PASTE, 1);

                int leakSpotIndex = activeLeakIndices.remove(patchedIndex);
                int[] patchedSpot = LEAK_COORDS[leakSpotIndex];

                World.getWorld().getGlobalObjects().add(new GlobalObject(REPAIRED_LEAK_OBJ, objX, objY, 0, patchedSpot[2], 10, -1, -1));

                adjustWaterLevel(-2);
                addContribution(p, 5);
                p.getSkills().addExperience(5, Skill.CONSTRUCTION);
                p.sendMessage("You successfully patch the leak.");
            }
        }
        else if (itemId == ROPE && objectId == TORN_NET_OBJ) {
            if (isNetBroken) {
                p.startAnimation(898);
                p.getItems().deleteItem(ROPE, 1);

                int successChance = 50 + (p.getSkills().getLevel(Skill.CRAFTING) / 2);
                if (Misc.random(100) < successChance) {
                    isNetBroken = false;
                    addContribution(p, 7);
                    p.getSkills().addExperience(5, Skill.CRAFTING);
                    p.sendMessage("You successfully repair the net.");
                } else {
                    p.sendMessage("You fail to repair the net in the harsh conditions.");
                }
            }
        }
        else if (itemId == HAMMER && objectId == BROKEN_RAIL_OBJ) {
            if (railBroken && currentKrakenSpotIndex != -1) {
                p.startAnimation(898);
                railBroken = false;

                int[] spot = RAILING_COORDS[currentKrakenSpotIndex];
                int actualX = spot[0] + (currentShipState * 43);
                World.getWorld().getGlobalObjects().add(new GlobalObject(NORMAL_RAIL_OBJ, actualX, spot[1], 0, spot[2], 10, -1, -1));

                currentKrakenSpotIndex = -1;
                addContribution(p, 10);
                p.getSkills().addExperience(5, Skill.CONSTRUCTION);
                p.sendMessage("You repair the broken railing.");
            }
        }
    }

    @Override
    protected void giveRewards(Player p, int fishCut) {
        if (p.trawlerContribution < 50) {
            p.sendMessage("You did not contribute enough to receive a share of the catch.");
            return;
        }

        int actualCatch = fishCut;
        if (p.trawlerContribution == 255) {
            actualCatch *= 2;
        }

        p.sendMessage("Your share of the catch is " + actualCatch + " fish!");

        if (Misc.random(12) == 1) {
            p.sendMessage("@red@You find a piece of the Angler's outfit in the net!");
            // p.getItems().addItemUnderAnyCircumstance(ANGLER_ID, 1);
        }
    }

    @Override
    protected void updateInterface(Player p) {
        int minutesLeft = gameTimer / 100;
        p.getPA().sendFrame126(minutesLeft + " mins", 11938);

        if (isNetBroken) {
            p.getPA().sendFrame126("Ripped!", 11936);
            p.getPA().sendFrame126("", 11935);
        } else {
            p.getPA().sendFrame126("", 11936);
            p.getPA().sendFrame126("Okay", 11935);
        }

        int waterConfigValue = (int) ((waterLevel / 100.0) * 255.0);
        p.getPA().sendConfig(376, waterConfigValue);
    }
}