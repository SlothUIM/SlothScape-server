package server.model.minigames;

import server.clip.WorldObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.Player.NMZMode;
import server.model.players.combat.Hitmark;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

public class NightmareZone {

    private static final int COFFER_FEE = 16000;

    /**
     * Opens the setup interface and dynamically grays out locked bosses
     */
    public static void openSetupInterface(Player c) {
        c.getPA().removeAllWindows();

        for (NMZBosses boss : NMZBosses.values()) {
            if (boss.isUnlocked(c)) {
                // Unlocked: Send standard orange text
                c.getPA().sendFrame126("<col=ff981f>" + boss.getName(), boss.getTextFrameId());
                
                // Tell the client the current saved toggle state (0 = Blocked/X, 1 = Active/Check)
                c.getPA().sendConfig(boss.getConfigId(), c.blockedNMZBosses[boss.ordinal()] ? 1 : 0);
            } else {
                // Locked: Send gray text
                c.getPA().sendFrame126("<col=808080>" + boss.getName(), boss.getTextFrameId());
                
                // Force it to be blocked in the server memory
                c.blockedNMZBosses[boss.ordinal()] = true; 
                // Force the visual to show the Red X (0)
                c.getPA().sendConfig(boss.getConfigId(), 0); 
            }
        }
        
        c.getPA().showInterface(60013);
    }
    /**
     * Handles depositing coins from inventory to the NMZ Coffer
     */
    public static void addToCoffer(Player c, int amount) {
        int invCoins = c.getItems().getItemAmount(995);
        
        // Prevent depositing more than they actually have
        if (amount > invCoins) {
            amount = invCoins;
        }
        
        if (amount <= 0) {
            c.sendMessage("You don't have any coins to deposit.");
            return;
        }

        // Prevent integer overflow (max cash)
        if ((long) c.nmzCoffer + amount > Integer.MAX_VALUE) {
            amount = (int) (Integer.MAX_VALUE - c.nmzCoffer);
        }

        c.getItems().deleteItem(995, amount);
        c.nmzCoffer += amount;
        c.sendMessage("You deposit " + amount + " coins into the coffer. Total: " + c.nmzCoffer);

		c.addingToNMZCoffer = false;
		c.withdrawFromNMZCoffer = false;
		c.withdrawFromNMZCoffer = false;
		c.dialogueAction = -1;
		c.xInterfaceId = -1;
		c.getPA().closeAllWindows();
    }

    /**
     * Handles withdrawing coins from the NMZ Coffer to inventory
     */
    public static void removeFromCoffer(Player c, int amount) {
        if (amount > c.nmzCoffer) {
            amount = c.nmzCoffer;
        }
        
        if (amount <= 0) {
            c.sendMessage("Your coffer is empty.");
            return;
        }

        // Ensure they have inventory space if they don't already have coins
        if (c.getItems().freeSlots() == 0 && !c.getItems().playerHasItem(995)) {
            c.sendMessage("You don't have enough inventory space to withdraw coins.");
            return;
        }

        c.nmzCoffer -= amount;
        c.getItems().addItem(995, amount);
        c.sendMessage("You withdraw " + amount + " coins from the coffer. Remaining: " + c.nmzCoffer);
		c.getPA().closeAllWindows();
		c.addingToNMZCoffer = false;
		c.withdrawFromNMZCoffer = false;
		c.dialogueAction = -1;
		c.xInterfaceId = -1;
    }

    /**
     * Validates the interface settings and starts the dream
     */
    /**
     * Validates the interface settings and starts the dream
     */
    public static void setupCustomisableRumble(Player c) {
        c.activeNMZBosses.clear();

        NMZBosses[] allBosses = NMZBosses.values();
        for (int i = 0; i < allBosses.length; i++) {
            if (!c.blockedNMZBosses[i] && allBosses[i].isUnlocked(c)) {
                c.activeNMZBosses.add(allBosses[i]);
            }
        }

        // --- DYNAMIC MODE CHECKS ---
        if (c.nmzMode == Player.NMZMode.PRACTICE) {
            if (c.activeNMZBosses.size() != 1) {
                c.sendMessage("You must select exactly ONE boss to fight in Practice mode.");
                return;
            }
        } else {
            if (c.activeNMZBosses.size() < 5) {
                c.sendMessage("You must select at least 5 bosses to start a Customisable Rumble.");
                return;
            }
        }

        // --- COFFER CHECK ---
        if (c.nmzCoffer < c.nmzFee) {
            c.sendMessage("You do not have enough coins in your coffer. You need " + c.nmzFee + ".");
            return;
        }

        // Deduct fee (Practice will subtract 0)
        c.nmzCoffer -= c.nmzFee;
        if (c.nmzFee > 0) {
            c.sendMessage(c.nmzFee + " coins have been deducted from your coffer.");
        }
        
        c.enduranceWave = 0;
        startRumble(c); 
    }

    /**
     * Teleports the player to the arena and starts the NMZ Director
     */
    /**
     * Teleports the player to the arena and starts the NMZ Director
     */
    public static void startRumble(Player c) {
        c.getPA().removeAllWindows();
        
        c.nmzInstanceHeight = c.getIndex() * 4; 
        c.inNMZ = true;
        c.spawnedNmzBosses.clear();

        c.getPA().movePlayer(2269, 4698, c.nmzInstanceHeight);
        
        GlobalObject exitPortal = new GlobalObject(4150, 2273, 4681, c.nmzInstanceHeight, 0, 10);
        World.getWorld().getGlobalObjects().add(exitPortal);
        
        // --- DYNAMIC ITEM SPAWNING ---
        boolean needsRunes = false;
        
        for (NMZBosses boss : c.activeNMZBosses) {
            int dropX = 2269 + server.util.Misc.random(2);
            int dropY = 4698 + server.util.Misc.random(2);

            switch (boss) {
                case ELVARG:
                    World.getWorld().getItemHandler().createGroundItem(c, 11710, dropX, dropY, c.nmzInstanceHeight, 1); // Anti-dragon shield
                    break;
                case TANGLEFOOT:
                    World.getWorld().getItemHandler().createGroundItem(c, 11711, dropX, dropY, c.nmzInstanceHeight, 1); // Magic secateurs
                    break;
                case SLAGILITH:
                    World.getWorld().getItemHandler().createGroundItem(c, 11719, dropX, dropY, c.nmzInstanceHeight, 1); // Rune pickaxe
                    break;
                case DAGANNOTH_MOTHER:
                    needsRunes = true; // Flag that we need to drop the elemental/combat runes
                    break;
                default:
                    break;
            }
        }

        // Drop the 1000x runes if a magic-based boss was selected
        if (needsRunes) {
            int[] nmzRunes = {11712, 11713, 11714, 11715, 11716, 11717, 11718};
            for (int rune : nmzRunes) {
                World.getWorld().getItemHandler().createGroundItem(c, rune, 2269 + server.util.Misc.random(2), 4698 + server.util.Misc.random(2), c.nmzInstanceHeight, 1000);
            }
        }
        c.getPA().sendFrame126("1", 48682);
        c.sendMessage("You drink the potion and fall into a deep sleep...");
        c.sendMessage("You wake up in a nightmare!");
     // Reset the timer when they first enter the arena so the first wave spawns instantly
        c.nmzSpawnDelay = 0; 

        // Change the tick speed to 2 (1.2 seconds) to make the delay math smoother
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c == null || c.isDead || !c.inNMZ) {
                    container.stop();
                    return;
                }
             // --- RANDOM POWER-UP SPAWNER ---
                // 1 in 40 chance to spawn a power-up every cycle (adjust for frequency)
                if (server.util.Misc.random(40) == 1) {
                    int spawnX = 2269 + (-7 + server.util.Misc.random(14));
                    int spawnY = 4698 + (-7 + server.util.Misc.random(14));
                    
                    // Randomly pick one of the 4 power-up Object IDs
                    // TODO: Replace these 4 IDs with the actual Object IDs for the Orbs in your cache!
                    int[] powerUpIds = { 26257, 26264, 26265, 26266 }; 
                    int randomOrb = powerUpIds[server.util.Misc.random(powerUpIds.length - 1)];
                    
                    // Spawn the object for the player (lasts for 1 minute before vanishing)
                    c.getPA().object(randomOrb, spawnX, spawnY, 0, 10);
                    c.sendMessage("@red@A power-up has spawned!");
                }
                c.spawnedNmzBosses.removeIf(npc -> npc == null || npc.isDead || npc.getHealth().getAmount() <= 0);

                // --- SPAWN TIMER LOGIC ---
                // If the delay is active, count it down and freeze the spawner
                if (c.nmzSpawnDelay > 0) {
                    c.nmzSpawnDelay--;
                    return; 
                }

                // --- RUMBLE MODE ---
                if (c.nmzMode == Player.NMZMode.RUMBLE) {
                    if (c.spawnedNmzBosses.size() < 4) {
                        spawnBoss(c, null); 
                        
                        // Set delay for the NEXT spawn (4 cycles = ~4.8 seconds delay between spawns)
                        // This makes the initial 4 bosses spawn sequentially, and delays respawns!
                        c.nmzSpawnDelay = 4; 
                    }
                } 
                // --- ENDURANCE MODE ---
                else if (c.nmzMode == Player.NMZMode.ENDURANCE) {
                    if (c.spawnedNmzBosses.size() < 1) { 
                        if (c.enduranceWave < c.activeNMZBosses.size()) {
                            spawnBoss(c, c.activeNMZBosses.get(c.enduranceWave));
                            c.enduranceWave++;
                            
                            // 5 cycles = ~6 seconds to loot before the next wave starts
                            c.nmzSpawnDelay = 5; 
                        } else {
                            c.sendMessage("You have defeated all bosses in Endurance mode!");
                            leaveDream(c);
                            container.stop();
                        }
                    }
                } 
                // --- PRACTICE MODE ---
                else if (c.nmzMode == Player.NMZMode.PRACTICE) {
                    if (c.spawnedNmzBosses.size() < 1 && c.enduranceWave == 0) {
                        spawnBoss(c, c.activeNMZBosses.get(0)); 
                        c.enduranceWave++; 
                    }
                }
            }
        }, 2); // Set to 2 ticks
    }
    
    /**
     * Instantly starts Standard Rumble or Endurance mode with all unlocked bosses.
     */
    public static void startStandardDream(Player c, int fee) {
        if (c.nmzCoffer < fee) {
            c.sendMessage("You need " + fee + " coins in your coffer to start this dream.");
            c.getPA().closeAllWindows();
            return;
        }

        c.activeNMZBosses.clear();
        for (NMZBosses boss : NMZBosses.values()) {
            if (boss.isUnlocked(c)) {
                c.activeNMZBosses.add(boss);
            }
        }

        if (c.activeNMZBosses.size() < 5) {
            c.sendMessage("You must have unlocked at least 5 bosses to play this mode.");
            c.getPA().closeAllWindows();
            return;
        }

        c.nmzCoffer -= fee;
        c.sendMessage(fee + " coins have been deducted from your coffer.");
        c.enduranceWave = 0; // Reset endurance wave just in case
        
        startRumble(c); 
    }
    /**
     * Called from NPCHandler when an NPC dies. 
     * Awards points and removes the boss from the active arena list.
     */
    public static void handleBossDeath(Player c, NPC npc) {
        if (!c.inNMZ || npc == null) {
            return;
        }

        // Loop through our NMZ bosses to see if the dead NPC matches one
        for (NMZBosses boss : NMZBosses.values()) {
            if (npc.npcType == boss.getNpcId()) {
                
                // Award points
                c.nmzPoints += boss.getBasePoints();
                c.getPA().sendFrame126(""+c.nmzPoints, 48680);
                c.sendMessage("<col=00ff00>You have been awarded " + boss.getBasePoints() + " Nightmare Zone points!</col>");
                
                // Remove it from the tracker so the Director spawns a new one
                c.spawnedNmzBosses.remove(npc);
                return;
            }
        }
    }
    /**
     * Handles clicking a Power-up orb
     */
    public static void activatePowerUp(Player c, int objectId, int obX, int obY) {
        if (!c.inNMZ) return;

        // Delete the orb from the ground
        c.getPA().object(-1, obX, obY, 0, 10);
        c.startAnimation(832); // Generic swipe/grab animation

        switch (objectId) {
            case 26257: // POWER SURGE (Yellow)
                c.powerSurgeTimer = 75; // 45 seconds
                c.specAmount = 10;
                c.getItems().updateSpecialBar();
                c.sendMessage("@yel@You feel a surge of power! Your special attack will constantly regenerate.");
                break;

            case 26264: // RECURRENT DAMAGE (Red)
                c.recurrentDamageTimer = 75; // 45 seconds
                c.sendMessage("@red@Your attacks will now deal recurrent damage!");
                break;

            case 26265: // ZAPPER (Purple)
                c.zapperTimer = 100; // 60 seconds
                c.sendMessage("@mag@A magical force starts zapping nearby enemies!");
                break;

            case 26266: // ULTIMATE FORCE (White)
                c.sendMessage("@whi@You unleash the Ultimate Force!");
                // Instantly kill all spawned bosses without awarding points
                for (NPC boss : c.spawnedNmzBosses) {
                    if (boss != null && !boss.isDead) {
                        // We flag them with a special variable so the death handler knows NOT to give points
                        boss.hasUltimateForceDamage = true; 
                        boss.appendDamage(boss.getHealth().getAmount(), Hitmark.HIT);
                        boss.isDead = true;
                    }
                }
                c.spawnedNmzBosses.clear();
                break;
        }
    }
    /**
     * Spawns a specific boss. If specificBoss is null, it picks a random unique boss.
     */
    private static void spawnBoss(Player c, NMZBosses specificBoss) {
        if (c.activeNMZBosses.isEmpty() && specificBoss == null) {
            return; 
        }

        NMZBosses bossToSpawn = null;

        // 1. Determine which boss to spawn
        if (specificBoss == null) {
            // --- RUMBLE MODE (PREVENT DUPLICATES) ---
            java.util.ArrayList<NMZBosses> availableToSpawn = new java.util.ArrayList<>();
            
            for (NMZBosses boss : c.activeNMZBosses) {
                boolean alreadyAlive = false;
                
                // Check if this boss is currently in the arena
                for (NPC spawned : c.spawnedNmzBosses) {
                    if (spawned.npcType == boss.getNpcId()) {
                        alreadyAlive = true;
                        break;
                    }
                }
                
                // If they aren't in the arena, add them to the random pool
                if (!alreadyAlive) {
                    availableToSpawn.add(boss);
                }
            }

            // If the player only selected 4 bosses, and all 4 are alive, we can't spawn anything!
            if (availableToSpawn.isEmpty()) {
                return; 
            }

            int randomIndex = Misc.random(availableToSpawn.size() - 1);
            bossToSpawn = availableToSpawn.get(randomIndex);
        } else {
            // --- ENDURANCE / PRACTICE MODE ---
            bossToSpawn = specificBoss;
        }

        // 2. Calculate Spawn Coordinates
        int spawnX = 2269 + (-5 + Misc.random(10));
        int spawnY = 4698 + (-5 + Misc.random(10));

        // 3. Base Stats
        int hp = 100; 
        int maxHit = 15;
        int attack = 80;
        int defence = 80; 

        // 4. Hard Mode Scaling
        if (c.nmzHardMode) {
            hp = (int) (hp * 2.5);         
            maxHit = (int) (maxHit * 1.5);   
            attack = (int) (attack * 1.5);   
        }

        // 5. Spawn the NPC
        NPC spawnedNpc = World.getWorld().npcHandler.spawnNpc(
            c, bossToSpawn.getNpcId(), spawnX, spawnY, c.nmzInstanceHeight, 1, hp, maxHit, attack, defence, true, false
        );

        if (spawnedNpc != null) {
            c.spawnedNmzBosses.add(spawnedNpc);
        }
    }

    /**
     * Handles gracefully leaving the dream
     */
    public static void leaveDream(Player c) {
        c.inNMZ = false;
        c.spawnedNmzBosses.clear(); // We don't have to delete the NPCs, they just stay behind in the dead instance

        c.getPA().sendFrame126("0", 48682);
        // Teleport back to Dom Onion
        c.getPA().movePlayer(2606, 3114, 0); 
        c.sendMessage("You wake up from the dream.");
    }
}