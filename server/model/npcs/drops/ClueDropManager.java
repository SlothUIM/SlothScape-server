package server.model.npcs.drops;


import java.util.HashMap;
import java.util.Map;
import server.model.players.Player;
import server.model.players.Position;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.content.treasuretrails.types.ChallengeClue;
import server.model.npcs.NPC;
import server.world.Location;
import server.world.World;
import server.util.Misc;

public class ClueDropManager {

    // NPC ID -> { Tier, Drop Rate }
    // Tiers: 0=Beginner, 1=Easy, 2=Medium, 3=Hard, 4=Elite
    private static final Map<Integer, int[]> CLUE_DROPS = new HashMap<>();
    private static void addDrop(int[] npcIds, int tier, int rate) {
        int[] data = new int[]{ tier, rate };
        for (int id : npcIds) {
            CLUE_DROPS.put(id, data);
        }
    }
    static {
        // --- EASY CLUES (Tier 1) ---
    	// ==========================================
        // BEGINNER CLUES (Tier 0)
        // ==========================================
        addDrop(new int[]{ 3021, 3022, 3023, 3024, 3034, 3032, 3033, 3031, 3030, 3035, 3036, 3029, 3028}, 0, 60); // Goblins (1/60)
        addDrop(new int[]{ 2450, 2451, 2452 }, 0, 60);       // Minotaurs (1/60)
        addDrop(new int[]{ 1120, 1122, 1124 }, 0, 50);       // Hill Giants (1/50)
        addDrop(new int[]{ 112, 114 }, 0, 40);               // Moss Giants (1/40)
        addDrop(new int[]{ 3264, 3265, 3266 }, 0, 70);       // Barbarians (1/70)
        addDrop(new int[]{ 7277, 7278, 7279 }, 0, 70);       // Dark Wizards (1/70)

        // ==========================================
        // EASY CLUES (Tier 1)
        // ==========================================
        // Men & Women (1/128)
        addDrop(new int[]{ 3106, 3107, 3014, 3078, 3079, 3080 }, 1, 128); 
        addDrop(new int[]{ 3111, 3015, 3081, 3082, 3083 }, 1, 128); 
        addDrop(new int[]{ 3106, 3107, 3014, 3078, 3079, 3080 }, 0, 90); 
        addDrop(new int[]{ 3111, 3015, 3081, 3082, 3083 }, 0, 90); 
        
        // Classic Easy Clue Farmers
        CLUE_DROPS.put(2265, new int[]{ 1, 128 });             // Thug
        addDrop(new int[]{ 2540, 2541 }, 1, 50);               // H.A.M. Members (Male/Female)
        addDrop(new int[]{ 3021, 3022, 3023, 3024 }, 1, 128);  // Goblins also drop Easy!
        addDrop(new int[]{ 2450, 2451, 2452 }, 1, 101);        // Minotaurs

        // ==========================================
        // MEDIUM CLUES (Tier 2)
        // ==========================================
        // Falador Guards (Iconic Medium Clue farmers: 1/128)
        addDrop(new int[]{ 3108, 3110, 3269, 3270, 3271, 3272, 3010, 3011 }, 2, 128); 
        
        // Other Medium Farmers
        addDrop(new int[]{ 2266, 2267 }, 2, 128);              // Dagannoth
        CLUE_DROPS.put(414, new int[]{ 2, 128 });              // Banshee
        CLUE_DROPS.put(415, new int[]{ 2, 128 });              // Cockatrice
        CLUE_DROPS.put(433, new int[]{ 2, 128 });              // Pyrefiend
        addDrop(new int[]{ 111, 115, 116 }, 2, 128);           // Ice Warriors

        // ==========================================
        // HARD CLUES (Tier 3)
        // ==========================================
        // Hellhounds (Best Hard Clue farmers: 1/64)
        addDrop(new int[]{ 104, 105, 135, 7266 }, 3, 64);      
        
        // Slayer Monsters (Usually 1/128)
        CLUE_DROPS.put(3133, new int[]{ 3, 128 });             // Cave Horror
        CLUE_DROPS.put(414, new int[]{ 3, 128 });              // Bloodveld
        addDrop(new int[]{ 2, 3, 4 }, 3, 128);                 // Aberrant Spectres
        CLUE_DROPS.put(437, new int[]{ 3, 128 });              // Jelly
        CLUE_DROPS.put(423, new int[]{ 3, 128 });              // Dust Devil
        CLUE_DROPS.put(410, new int[]{ 3, 128 });              // Kurask
        CLUE_DROPS.put(412, new int[]{ 3, 128 });              // Gargoyle
        CLUE_DROPS.put(8, new int[]{ 3, 128 });                // Nechryael
        CLUE_DROPS.put(415, new int[]{ 3, 128 });              // Abyssal Demon

        // Dragons
        addDrop(new int[]{ 260, 261, 262, 263 }, 3, 128);      // Green Dragons
        addDrop(new int[]{ 265, 266, 267, 268 }, 3, 128);      // Blue Dragons

        // ==========================================
        // ELITE CLUES (Tier 4)
        // ==========================================
        // Bosses (Very high drop rates)
        addDrop(new int[]{ 2042, 2043, 2044 }, 4, 75);         // Zulrah (All phases)
        CLUE_DROPS.put(8026, new int[]{ 4, 65 });              // Vorkath
        CLUE_DROPS.put(5862, new int[]{ 4, 100 });             // Cerberus
        CLUE_DROPS.put(5886, new int[]{ 4, 180 });             // Abyssal Sire

        // God Wars Dungeon Bosses (1/250)
        CLUE_DROPS.put(2215, new int[]{ 4, 250 });             // General Graardor
        CLUE_DROPS.put(3162, new int[]{ 4, 250 });             // Kree'arra
        CLUE_DROPS.put(2205, new int[]{ 4, 250 });             // Commander Zilyana
        CLUE_DROPS.put(3129, new int[]{ 4, 250 });             // K'ril Tsutsaroth

        // High-level Dragons
        CLUE_DROPS.put(8091, new int[]{ 4, 250 });             // Lava Dragon
        CLUE_DROPS.put(8090, new int[]{ 4, 350 });             // Mithril Dragon
        CLUE_DROPS.put(7275, new int[]{ 4, 250 });             // Brutal Black Dragon
        
        // Add all your other specific OSRS drops here!
    }

    public static void handleClueDrop(Player player, NPC npc, Location location) {
        int npcId = npc.npcType;

        // 1. If the NPC is not in our specific map, they do not drop clues at all.
        if (!CLUE_DROPS.containsKey(npcId)) {
            return; 
        }

        int tierToDrop = CLUE_DROPS.get(npcId)[0];
        int dropChance = CLUE_DROPS.get(npcId)[1];

        // 2. Apply player ring/item boosts to the base chance
        dropChance = applyPlayerBoosts(player, dropChance);

        // 3. Roll the drop!
        if (Misc.random(dropChance) == 0) {
            dropClueScroll(player, tierToDrop, location);
        }
    }

    private static void dropClueScroll(Player player, int tier, Location location) {
        int[] cluePool;
        String tierName;

        switch (tier) {
            case 0: cluePool = TreasureTrails.BEGINNER_CLUES; tierName = "Beginner"; break;
            case 1: cluePool = TreasureTrails.EASY_CLUES; tierName = "Easy"; break;
            case 2: cluePool = TreasureTrails.MEDIUM_CLUES; tierName = "Medium"; break;
            case 3: cluePool = TreasureTrails.HARD_CLUES; tierName = "Hard"; break;
            case 4: cluePool = TreasureTrails.ELITE_CLUES; tierName = "Elite"; break;
            case 5: cluePool = TreasureTrails.MASTER_CLUES; tierName = "Master"; break;
            default: return;
        }

        // 1. Base check: Do they have a standard clue scroll of this tier?
        boolean hasClue = TreasureTrails.hasClueOfTier(player, cluePool);

        // 2. Exploit check: Do they have a Challenge/Puzzle scroll of this tier?
        if (tier == 2) {
            hasClue |= TreasureTrails.hasClueOfTier(player, ChallengeClue.MEDIUM_CHALLENGES);
        } else if (tier == 3) {
            hasClue |= TreasureTrails.hasClueOfTier(player, ChallengeClue.HARD_CHALLENGES);
            // If you added Puzzle Box (2800), you'd check it here too!
            hasClue |= player.getItems().playerHasItem(2800) || player.getItems().bankContains(2800);
        }

        // OSRS Limits you to 1 active clue per tier! (Caskets CAN stack though)
        if (hasClue) {
        	player.sendMessage("You feel like a clue would have dropped.");
            return; 
        }

        // Pick a random specific clue ID from that tier's array
        int clueIdToDrop;

        // Modern System: Universal ID + Player Variable
        if (tier == 0) {
            clueIdToDrop = 23182; // The ONLY Beginner Clue ID
            
            // Assign them a random text step if they don't already have one
            if (player.currentBeginnerClueStep == -1) {
                player.currentBeginnerClueStep = Misc.random(TreasureTrails.BEGINNER_CLUE_TEXTS.size() - 1);
            }
        } 
        // Legacy System: Specific Item IDs
        else {
            clueIdToDrop = cluePool[Misc.random(cluePool.length - 1)];
        }

        // Initialize the total steps if this is a fresh clue trail!
        switch (tier) {
            case 0:
                if (player.beginnerClueStepsTotal == 0) {
                    player.beginnerClueStepsTotal = TreasureTrails.generateSteps(0);
                    player.beginnerClueStepsCompleted = 0;
                }
                break;
            case 1:
                if (player.easyClueStepsTotal == 0) {
                    player.easyClueStepsTotal = TreasureTrails.generateSteps(1);
                    player.easyClueStepsCompleted = 0;
                }
                break;
            case 2:
                if (player.mediumClueStepsTotal == 0) {
                    player.mediumClueStepsTotal = TreasureTrails.generateSteps(2);
                    player.mediumClueStepsCompleted = 0;
                }
                break;
            case 3:
                if (player.hardClueStepsTotal == 0) {
                    player.hardClueStepsTotal = TreasureTrails.generateSteps(3);
                    player.hardClueStepsCompleted = 0;
                }
                break;
            case 4:
                if (player.eliteClueStepsTotal == 0) {
                    player.eliteClueStepsTotal = TreasureTrails.generateSteps(4);
                    player.eliteClueStepsCompleted = 0;
                }
                break;
        }
        
        // Drop the actual item on the ground
        World.getWorld().getItemHandler().createGroundItem(player, clueIdToDrop, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
        player.sendMessage("<col=800000>You sense a " + tierName + " clue scroll being dropped to the ground.</col>");
    }

    private static int applyPlayerBoosts(Player player, int baseChance) {
        // Your custom ring logic 
        if (player.getRechargeItems().hasItem(13118)) return (int)(baseChance * 0.94); // Example: 6% boost
        if (player.getRechargeItems().hasItem(13119)) return (int)(baseChance * 0.90); // Example: 10% boost
        if (player.getRechargeItems().hasItem(13120)) return (int)(baseChance * 0.80); // Example: 20% boost
        return baseChance;
    }
}