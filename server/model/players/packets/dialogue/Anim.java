package server.model.players.packets.dialogue;

import java.util.HashMap;
import java.util.Map;

/**
 * Chat Animations
 * Represents the head animations used in dialogue interfaces (frames 969, 4883, etc).
 */
public enum Anim {
    // --- Happy / Neutral ---
    HAPPY(588),          // Joyful/Happy
    CALM_1(589),         // Calm, minimal movement
    CALM_2(590),         // Standard calm talking
    DEFAULT(591),        // The default fallback animation
    TALKING_ALOT(592),        // The default fallback animation
    
    // --- Negative / Sad ---
    WORRIED(597),        // Distressed
    SCARED(598),         // Fearful
    SAD(599),            // Standard sad
    DEPRESSED(602),      // Weary/depressed
    CRYING(610),         // Crying/Very Sad

    // --- Angry / Aggressive ---
    EVIL(592),           // Evil/Scheming
    ANNOYED(613),        // Grumpy
    ANGRY(614),          // Standard Angry
    ANGRY_ARMS_CROSSED(615), // "What the crap" / Arms crossed
    AGGRO_HEAD_BANG(616), // Aggressive head bob
    TOUGH(617),          // Tough guy/Stern

    // --- Specific Actions ---
    DISORIENTED(600),    // Drunk/Confused (Head swaying)
    SLEEPY(601),         // Dozing off/Falling asleep
    CONFUSED(603),       // Questioning/Confused
    THINKING(604),       // Hand on chin
    LOOK_DOWN(611),      // Looking at weapon/hands
    
    // --- Social / Interaction ---
    SNOBBY(595),         // Looking down on player
    SHAKE_NO(596),       // Shaking head "No" (Refusal)
    EVIL_LAUGH(609),     // Maniacal laughter
    
    // --- Laughter Variants (Intensity) ---
    LAUGH_1(605),        // Chuckle
    LAUGH_2(606),        // Laughing
    LAUGH_3(607),        // Hard Laughing
    LAUGH_4(608), 		// Belly Laugh
    ;        

    private final int animationId;

    // Cache for fast lookup
    private static final Map<Integer, Anim> ID_MAP = new HashMap<>();

    static {
        for (Anim anim : values()) {
            ID_MAP.put(anim.animationId, anim);
        }
    }

    Anim(int animationId) {
        this.animationId = animationId;
    }

    public int getAnimationId() {
        return animationId;
    }

    /**
     * Gets the Enum constant by its integer ID.
     * Useful if loading dialogue data from external JSON/XML files.
     */
    public static Anim forId(int id) {
        return ID_MAP.getOrDefault(id, DEFAULT);
    }
    
    /**
     * Helper to determine if an ID is a "negative" emotion.
     * Useful for automated responses or logic.
     */
    public boolean isNegative() {
        return this == SAD || this == CRYING || this == WORRIED || this == SCARED || this == DEPRESSED;
    }
}