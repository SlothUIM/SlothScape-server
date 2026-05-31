package server.model.players.combat.effects.bolts;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;

import java.util.HashMap;
import java.util.Map;

public final class BoltEnchant {

    // Interface and child ids
    private static final int CHATBOX_IFACE_ID = 44720;
    private static final int FIRST_ITEM_CHILD = 44734; // 44734..44741 (8 slots)
    private static final int SLOTS = 8;

    // Use 65535 to CLEAR a model on the client (matches your client 246 opcode handler)
    private static final int CLEAR_ITEM_ID = 65535;

    // Clickable button ID ranges
    private static final int BTN_DEFAULT_1 = 44721;
    private static final int BTN_DEFAULT_5 = 44722;
    private static final int BTN_DEFAULT_10 = 44723;

    private static final int BTN_MAKE1_BASE = 44724; // + i (0..7)
    private static final int BTN_MAKE5_BASE = 44744; // + i (0..7)
    private static final int BTN_MAKE10_BASE = 44754; // + i (0..7)

    // Runes (adjust to your server IDs if needed)
    private static final int AIR_RUNE    = 556;
    private static final int WATER_RUNE  = 555;
    private static final int EARTH_RUNE  = 557;
    private static final int FIRE_RUNE   = 554;
    private static final int MIND_RUNE   = 558;
    private static final int NATURE_RUNE = 561;
    private static final int LAW_RUNE    = 563;
    private static final int COSMIC_RUNE = 564;
    private static final int BLOOD_RUNE  = 565;
    private static final int SOUL_RUNE   = 566;
    private static final int DEATH_RUNE  = 560;

    // Per-cast visuals (tune to your cache)
    private static final int ENCHANT_ANIM = 4462;  // generic magic cast
    private static final int ENCHANT_GFX  = 238;  // generic cast gfx

    // OSRS behavior: one "set" = 10 bolts
    private static final int BOLTS_PER_SET = 10;

    // Enchant data lookup by UNENCHANTED base ID
    private static final Map<Integer, EnchantData> DATA_BY_BASE = new HashMap<>();

    // Build the data table
    static {
        // Normal gem-tipped bolts (your list; no opal/pearl normal)
        register(new EnchantData("Jade",        9335,  -1, 9237,  -1,  14, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,2)));
        register(new EnchantData("Topaz",       9336,  -1, 9239,  -1,  29, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,2)));
        register(new EnchantData("Sapphire",    9337,  -1, 9240,  -1,   7, rc(COSMIC_RUNE,1), rc(WATER_RUNE,1), rc(MIND_RUNE,1)));
        register(new EnchantData("Emerald",     9338,  -1, 9241,  -1,  27, rc(COSMIC_RUNE,1), rc(AIR_RUNE,1), rc(NATURE_RUNE,1)));
        register(new EnchantData("Ruby",        9339,  -1, 9242,  -1,  49, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,5), rc(BLOOD_RUNE,1)));
        register(new EnchantData("Diamond",     9340,  -1, 9243,  -1,  57, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,10), rc(LAW_RUNE,2)));
        register(new EnchantData("Dragonstone", 9341,  -1, 9244,  -1,  68, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,15), rc(WATER_RUNE,15)));
        register(new EnchantData("Onyx",        9342,  -1, 9245,  -1,  87, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,20), rc(DEATH_RUNE,1), rc(SOUL_RUNE,1)));

        // Dragon gem-tipped bolts (verify enchanted IDs in your cache)
        register(new EnchantData("Opal (dragon)",       -1, 21955, -1, 21926,  4, rc(COSMIC_RUNE,1), rc(AIR_RUNE,2)));
        register(new EnchantData("Jade (dragon)",       -1, 21957, -1, 21928, 14, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,2)));
        register(new EnchantData("Pearl (dragon)",      -1, 21959, -1, 21930, 24, rc(COSMIC_RUNE,1), rc(WATER_RUNE,2)));
        register(new EnchantData("Topaz (dragon)",      -1, 21961, -1, 21932, 29, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,2)));
        register(new EnchantData("Sapphire (dragon)",   -1, 21963, -1, 21934,  7, rc(COSMIC_RUNE,1), rc(WATER_RUNE,1), rc(MIND_RUNE,1)));
        register(new EnchantData("Emerald (dragon)",    -1, 21965, -1, 21936, 27, rc(COSMIC_RUNE,1), rc(AIR_RUNE,1), rc(NATURE_RUNE,1)));
        register(new EnchantData("Ruby (dragon)",       -1, 21967, -1, 21938, 49, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,5), rc(BLOOD_RUNE,1)));
        register(new EnchantData("Diamond (dragon)",    -1, 21969, -1, 21940, 57, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,10), rc(LAW_RUNE,2)));
        register(new EnchantData("Dragonstone (dragon)",-1, 21971, -1, 21942, 68, rc(COSMIC_RUNE,1), rc(EARTH_RUNE,15), rc(WATER_RUNE,15)));
        register(new EnchantData("Onyx (dragon)",       -1, 21973, -1, 21944, 87, rc(COSMIC_RUNE,1), rc(FIRE_RUNE,20), rc(DEATH_RUNE,1), rc(SOUL_RUNE,1)));
    }

    // The order you want to show on the interface (max 8 shown)
    private static final int[] ENCHANTABLE_BOLT_IDS = {
        9335,9336,9337,9338,9339,9340,9341,9342, // normal
        21955,21957,21959,21961,21963,21965,21967,21969,21971,21973 // dragon
    };
    private static final int[] ENCHANTED_BOLT_IDS = {
            9235,9236,9237,9238,9239,9240,9241,9242 // normal
           // 21955,21957,21959,21961,21963,21965,21967,21969,21971,21973 // dragon
        };
    public static void setSpellConfig(Player c) {

		c.getPA().sendConfig(618, 1);
    }
    // Open the interface and remember which 0..(k-1) IDs are shown
    public static void open(Player c, boolean button) {
        // Clear all 8 item slots
    	if(button) {
	        for (int i = 0; i < SLOTS; i++) {
	            c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 225, CLEAR_ITEM_ID);
	            c.boltEnchantDisplayed[i] = 0;
	        }
	        c.boltEnchantCount = 0;
	
	        int filled = 0;
	
	        // Populate consecutive slots with types the player BOTH has and has runes for
	        for (int boltId : ENCHANTABLE_BOLT_IDS) {
	            if (filled >= SLOTS) break;
	
	            // Has at least one of this bolt type?
	            if (!c.getItems().playerHasItem(boltId, 1))
	                continue;
	
	            // Must be a known enchantable type with rune data
	            EnchantData data = DATA_BY_BASE.get(boltId);
	            if (data == null)
	                continue;
	
	            // Optional: require magic level now (hide options you can't use)
	            if (c.getSkills().getLevel(Skill.MAGIC) < data.level)
	                continue;
	
	            // Check runes for one cast (one "set" of bolts)
	            boolean hasRunes = true;
	            for (RuneCost rc : data.runes) {
	                // If you support infinite runes via staves, account for it here:
	                // int infinite = getInfiniteRuneAmount(c, rc.id);
	                // int need = Math.max(0, rc.count - infinite);
	                int need = rc.count;
	                if (!c.getItems().playerHasItem(rc.id, need)) {
	                    hasRunes = false;
	                    break;
	                }
	            }
	            if (!hasRunes)
	                continue;
	
	            // Show this option
		        for (int EnchantedBoltId : ENCHANTED_BOLT_IDS)
	            c.getPA().sendFrame246(FIRST_ITEM_CHILD + filled, 225, EnchantedBoltId);
	            c.boltEnchantDisplayed[filled] = boltId;
	            filled++;
	        }
	
	        c.boltEnchantCount = filled;
	
	        if (filled == 0) {
	            c.sendMessage("You don't have any bolts to enchant.");
	            return;
	        }
    	c.getPA().sendFrame126("How many sets of bolts to enchant?", 44732);
    	c.activeAction = Player.ChatboxAction.BOLT_ENCHANT;
        c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
    	}

    		c.getPA().sendConfig(618, 1);
    }

    // Handle all bolt-enchant interface buttons. Return true if handled.
    public static boolean handleButton(Player c, int buttonId) {
        // Default quantity toggles
        if (buttonId == BTN_DEFAULT_1 || buttonId == BTN_DEFAULT_5 || buttonId == BTN_DEFAULT_10) {
            int sets = (buttonId == BTN_DEFAULT_1) ? 1 : (buttonId == BTN_DEFAULT_5 ? 5 : 10);
            c.amtToMake = sets; // your existing field
            c.sendMessage("Default set amount: " + sets + (sets == 1 ? " (10 bolts)" : " (" + (sets * 10) + " bolts)"));
            return true;
        }

        // Per-slot triplets
        final int setsExplicit;
        final int slotIndex;

        if (buttonId >= BTN_MAKE1_BASE && buttonId < BTN_MAKE1_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE1_BASE;
            setsExplicit = -1; // use default
        } else if (buttonId >= BTN_MAKE5_BASE && buttonId < BTN_MAKE5_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE5_BASE;
            setsExplicit = 5;
        } else if (buttonId >= BTN_MAKE10_BASE && buttonId < BTN_MAKE10_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE10_BASE;
            setsExplicit = 10;
        } else {
            return false; // not ours
        }

        if (slotIndex < 0 || slotIndex >= c.boltEnchantCount) {
            c.sendMessage("That option isn't available right now.");
            return true;
        }

        int baseBoltId = c.boltEnchantDisplayed[slotIndex];
        if (baseBoltId <= 0) {
            c.sendMessage("That option isn't available right now.");
            return true;
        }

        int setsToMake = (setsExplicit > 0) ? setsExplicit : Math.max(1, Math.min(10, c.amtToMake)); // clamp 1..10 for safety
        startEnchant(c, baseBoltId, setsToMake);
        return true;
    }

    // Begin enchanting in cycles; each cycle makes one "set" (10 bolts)
    public static void startEnchant(final Player c, final int baseBoltId, final int requestedSets) {
        final EnchantData data = DATA_BY_BASE.get(baseBoltId);
        if (data == null) {
            c.sendMessage("You can't enchant that.");
            return;
        }
        c.getPA().removeAllWindows();

        final int resultId = data.getEnchantedForBase(baseBoltId);
        if (resultId <= 0) {
            c.sendMessage("You can't enchant that.");
            return;
        }

        if (c.getSkills().getLevel(Skill.MAGIC) < data.level) {
            c.sendMessage("You need a Magic level of " + data.level + " to enchant these bolts.");
            return;
        }

        // Inventory count of the base bolts we’re enchanting
        final int invCount = c.getItems().getItemCount(baseBoltId, false);

        if (invCount <= 0) {
            c.sendMessage("You don't have any of that bolt.");
            return;
        }

        // Allow partial for Make-1: if you have < 10, enchant what you have (consumes 1 cast of runes)
        final boolean partialForMake1 = (requestedSets == 1) && invCount < BOLTS_PER_SET;

        final int possibleSets;
        final int partialAmount; // 0 unless doing partial (first cycle only)

        if (partialForMake1) {
            // Need only 1 cast worth of runes
            if (!hasRunesForSets(c, data.runes, 1)) {
                c.sendMessage("You don't have the required runes.");
                return;
            }
            possibleSets = 1;
            partialAmount = invCount; // enchant all remaining (<10)
        } else {
            // Full sets logic (10 per set)
            int invSets = invCount / BOLTS_PER_SET;
            int runeSets = maxSetsFromRunes(c, data.runes);
            possibleSets = Math.min(requestedSets, Math.min(invSets, runeSets));
            partialAmount = 0;

            if (possibleSets <= 0) {
                if (invSets <= 0) {
                    c.sendMessage("You need at least " + BOLTS_PER_SET + " of that bolt to enchant that amount.");
                } else {
                    c.sendMessage("You don't have the required runes.");
                }
                return;
            }
        }

        c.sendMessage("You begin enchanting bolts...");
        c.startAnimation(ENCHANT_ANIM);
        c.gfx100(ENCHANT_GFX);

        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            int doneSets = 0;

            @Override
            public void execute(CycleEventContainer container) {
                if (doneSets >= possibleSets) {
                    container.stop();
                    return;
                }

                if (c.disconnected || c.isDead || c.isMoving) {
                	//c.sendMessage("isMoving? "+c.isMoving);
                    container.stop();
                    return;
                }

                // Determine amount to convert this cycle
                int thisSetAmount = (partialAmount > 0 && doneSets == 0) ? partialAmount : BOLTS_PER_SET;

                // Verify supplies right before consuming
                if (!hasRunesForSets(c, data.runes, 1) || c.getItems().getItemCount(baseBoltId, false) < thisSetAmount) {
                    c.sendMessage("You run out of supplies.");
                    container.stop();
                    return;
                }

                // Consume runes for one cast (even if partial)
                for (RuneCost rc : data.runes) {
                    removeRune(c, rc.id, rc.count);
                }

                // Convert bolts
                c.getItems().deleteItem2(baseBoltId, thisSetAmount);
                c.getItems().addItem(resultId, thisSetAmount);

                // Visuals per set
                c.startAnimation(ENCHANT_ANIM);
                c.gfx100(ENCHANT_GFX);

                 c.sendMessage("You enchant " + BOLTS_PER_SET + " bolts.");
                doneSets++;

                if (doneSets >= possibleSets) {
                    c.sendMessage("You finish enchanting.");
                    container.stop();
                }
            }

            @Override
            public void stop() {
                c.stopAnimation();
                c.getPA().removeAllWindows();
                // Optionally refresh the interface:
                // BoltEnchant.open(c);
            }
        }, 3); // every 3 ticks; adjust speed as desired
    }

    /* ==================== Helpers and data types ==================== */

    private static int maxSetsFromRunes(Player c, RuneCost[] costs) {
        int max = Integer.MAX_VALUE;
        for (RuneCost rc : costs) {
            int available = getRuneCountConsideringStaff(c, rc.id);
            max = Math.min(max, available / rc.count);
        }
        return max == Integer.MAX_VALUE ? 0 : max;
    }

    private static boolean hasRunesForSets(Player c, RuneCost[] costs, int sets) {
        for (RuneCost rc : costs) {
            if (getRuneCountConsideringStaff(c, rc.id) < rc.count * sets) return false;
        }
        return true;
    }

    // Remove runes, honoring infinite staff runes if you support them
    private static void removeRune(Player c, int runeId, int amount) {
        int infinite = getInfiniteRuneAmount(c, runeId);
        int need = Math.max(0, amount - infinite);
        if (need > 0) {
            c.getItems().deleteItem2(runeId, need);
        }
    }

    // Count available runes, treating staff-provided as infinite
    private static int getRuneCountConsideringStaff(Player c, int runeId) {
        int infinite = getInfiniteRuneAmount(c, runeId);
        if (infinite > 0) return Integer.MAX_VALUE / 4;
        return c.getItems().getItemCount(runeId, false);
    }

    // Hook to your staff system; return how many are “free” this cast (typically infinite if wielding a relevant staff)
    private static int getInfiniteRuneAmount(Player c, int runeId) {
        // Example if you have helpers like c.getCombat().hasStaff(int runeId) or similar.
        // if (c.getCombat().hasInfiniteRune(runeId)) return 1000000;
        return 0;
    }

    private static void register(EnchantData d) {
        if (d.baseNormal > 0) DATA_BY_BASE.put(d.baseNormal, d);
        if (d.baseDragon > 0) DATA_BY_BASE.put(d.baseDragon, d);
    }

    private static RuneCost rc(int runeId, int count) {
        return new RuneCost(runeId, count);
    }

    private static final class RuneCost {
        final int id, count;
        RuneCost(int id, int count) { this.id = id; this.count = count; }
    }

    private static final class EnchantData {
        final String name;
        final int baseNormal, baseDragon;
        final int enchNormal, enchDragon;
        final int level;
        final RuneCost[] runes;

        EnchantData(String name, int baseNormal, int baseDragon, int enchNormal, int enchDragon, int level, RuneCost... runes) {
            this.name = name;
            this.baseNormal = baseNormal;
            this.baseDragon = baseDragon;
            this.enchNormal = enchNormal;
            this.enchDragon = enchDragon;
            this.level = level;
            this.runes = runes;
        }

        int getEnchantedForBase(int baseId) {
            if (baseId == baseNormal) return enchNormal;
            if (baseId == baseDragon) return enchDragon;
            return -1;
        }
    }
}