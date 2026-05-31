package server.model.players.content.treasuretrails.types;

import java.util.HashMap;
import java.util.Map;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;

public class NpcClues {

    // Maps the Clue Scroll Item ID to the required NPC ID
    public static final Map<Integer, Integer> NPC_CLUES = new HashMap<>();

    static {
        // Format: put(Clue_Item_ID, NPC_ID);

        // ==========================================
        // EASY NPC CLUES
        // ==========================================
        NPC_CLUES.put(2681, 3077); // Speak to Hans
        NPC_CLUES.put(2683, 527);  // Talk to Zeke in Al Kharid
        NPC_CLUES.put(2684, 3080); // Speak to Ellis in Al Kharid (Adjust ID if needed)
        NPC_CLUES.put(2686, 731);  // Speak to the bartender of the Blue Moon Inn
        NPC_CLUES.put(2693, 3637); // Talk to the Squire in the White Knights' castle
        NPC_CLUES.put(2696, 733);  // Talk to the bartender of the Rusty Anchor in Port Sarim
        NPC_CLUES.put(2697, 3088); // Speak to Ned in Draynor Village
        NPC_CLUES.put(2698, 3086); // Speak to Doric, north of Falador
        NPC_CLUES.put(2699, 3085); // Speak to Gaius in Taverley
        NPC_CLUES.put(2701, 3089); // Speak to Arhein in Catherby
        NPC_CLUES.put(2702, 3087); // Speak to Sir Kay in Camelot Castle
     // ==========================================
        // BEGINNER CLUES (Anagram & Cryptic NPCs)
        // ==========================================
        
        // Anagrams
        NPC_CLUES.put(23188, 546);  // Ranael (Al Kharid skirt shop)
        NPC_CLUES.put(23189, 5034); // Archmage Sedridor (Wizards' Tower)
        NPC_CLUES.put(23190, 5036); // Apothecary (Varrock)
        NPC_CLUES.put(23191, 3086); // Doric (North of Falador)
        NPC_CLUES.put(23192, 502);  // Brian (Port Sarim battleaxe shop)
        NPC_CLUES.put(23193, 3111); // Veronica (Outside Draynor Manor)
        NPC_CLUES.put(23194, 3280); // Gertrude (West of Varrock)
        NPC_CLUES.put(23195, 5013); // Hairdresser (Western Falador)
        NPC_CLUES.put(23196, 3894); // Fortunato (Draynor Village Market)

        // Cryptics
        NPC_CLUES.put(23197, 3077); // Hans (Lumbridge Castle)
        NPC_CLUES.put(23198, 4626); // Cook (Lumbridge Castle)
        NPC_CLUES.put(23199, 4096); // Hunding (Barbarian Village)
        NPC_CLUES.put(23201, 4644); // Shantay (Shantay Pass)
     // ==========================================
        // MEDIUM CLUES (Anagrams & Ciphers)
        // ==========================================
        NPC_CLUES.put(2843, 4626);  // OK CO -> Cook (Lumbridge)
        NPC_CLUES.put(2845, 243);   // EEK ZERO OP -> Zookeeper (Ardougne)
        NPC_CLUES.put(2847, 550);   // EL OW -> Lowe (Varrock Archery)
        NPC_CLUES.put(2849, 529);   // R AK MI -> Karim (Al Kharid Kebab)
        NPC_CLUES.put(2851, 125);   // ARE COL -> Oracle (Ice Mountain)
        NPC_CLUES.put(2856, 579);   // PEATY PERT -> Party Pete (Falador)
        NPC_CLUES.put(2857, 208);   // GOBLIN KERN -> King Bolren (Tree Gnome Village)
        NPC_CLUES.put(2858, 224);   // HALT US -> King Lathas (Ardougne)
        NPC_CLUES.put(12055, 2914); // GOBLETS ODD TOES -> Otto Godblessed
        NPC_CLUES.put(12057, 2881); // A BAKER -> Baraek (Varrock)
        NPC_CLUES.put(12056, 6797); // I EVEN -> Nieve (Gnome Stronghold)
        NPC_CLUES.put(12061, 3644); // A BASIC ANTI POT -> Captain Tobias (Port Sarim)
        NPC_CLUES.put(12063, 3282); // RATAI -> Taria (Rimmington)
        NPC_CLUES.put(12065, 3223); // LEAKEY -> Kaylee (Falador)
        NPC_CLUES.put(12067, 524);  // THICKNO -> Hickton (Catherby)
        NPC_CLUES.put(12069, 5066); // KAY SIR -> Sir Kay (Camelot)
        NPC_CLUES.put(12071, 3307); // HEORIC -> Eohric (Burthorpe)
        
        NPC_CLUES.put(19734, 6922); // PACINNG A TAIE -> Captain Ginea
        NPC_CLUES.put(19736, 1143); // I DOOM ICON INN -> Dominic Onion
        NPC_CLUES.put(19738, 6932); // LOW LAG -> Gallow
        NPC_CLUES.put(19740, 6936); // R SLICER -> Clerris
        NPC_CLUES.put(19742, 6982); // HIS PHOR -> Horphis
        NPC_CLUES.put(19744, 6902); // TAMED ROCKS -> Dockmaster
        NPC_CLUES.put(19746, 545);  // AREA CHEF TREK -> Father Aereck
        NPC_CLUES.put(19748, 3232); // SAND NUT -> Dunstan
        NPC_CLUES.put(19750, 3209); // ARMCHAIR THE PELT -> Charlie the Tramp
        NPC_CLUES.put(19752, 3201); // PEAK REFLEX -> Flax keeper
        NPC_CLUES.put(19754, 3217); // QUE SIR -> Squire
        NPC_CLUES.put(19756, 6940); // I AM SIR -> Marisi
        NPC_CLUES.put(19758, 6979); // A HEART -> Aretha
        
        // Ciphers (Medium)
        NPC_CLUES.put(19762, 7051); // QSPGFTTPS... -> Professor Gracklebone
        NPC_CLUES.put(19764, 5085); // XJABSE... -> Wizard Traiborn
        NPC_CLUES.put(19766, 6972); // ECRVCKP... -> Captain Khaled
        NPC_CLUES.put(19768, 3536); // BMJ UIF... -> Ali the Kebab seller
        NPC_CLUES.put(19770, 311);  // HQNM LZM... -> Ironman tutor
        NPC_CLUES.put(19772, 3393); // GUHCHO -> Drezel
        
        NPC_CLUES.put(7274, 3892);  // DT RUN B -> Brundt the Chieftain
        NPC_CLUES.put(7276, 4478);  // GOT A BOY -> Gabooty
        NPC_CLUES.put(7278, 3121);  // HICKJET -> Jethick
        NPC_CLUES.put(7280, 3183);  // ARC O LINE -> Caroline
        NPC_CLUES.put(7282, 3266);  // NOD MED -> Edmond
        NPC_CLUES.put(7284, 5122);  // LARK IN DOG -> King Roald
        
        NPC_CLUES.put(3611, 171);   // ME IF -> Femi
        NPC_CLUES.put(3612, 173);   // BAIL TRIMS -> Brimstail
        NPC_CLUES.put(3613, 174);   // A BAS -> Saba
        NPC_CLUES.put(3616, 175);   // AHA JAR -> Jaraah
        NPC_CLUES.put(3618, 176);   // ICY FE -> Fycie
        
        NPC_CLUES.put(23131, 8511); // CLASH ION -> Nicholas
        NPC_CLUES.put(23133, 7580); // CALAMARI MADE MUD -> Madame Caldarium
        // Reminder: Charlie the Tramp (23200) and Reldo (23202) are NOT here 
        // because they require fetching an item or giving a strange device! 
        // Handle them in your NPCDialogue system!
        // Add Medium, Hard, and Elite NPC clues here!
    }
 // Maps a Clue Scroll ID to the Challenge Scroll ID it should hand out
    public static final Map<Integer, Integer> CLUE_TO_CHALLENGE = new HashMap<>();

    static {
        // Example mapping based on your previous ID list:
        // CLUE_TO_CHALLENGE.put(Clue_ID, Challenge_ID);
        CLUE_TO_CHALLENGE.put(2835, 2842); // Baraek's Clue -> Baraek's Challenge
        
        // (You'll pair up the rest of the anagram clues to their specific Challenge Scroll IDs here)
    }
    /**
     * Call this at the very top of your FirstClickNpc packet!
     */
    public static boolean handleNpc(Player c, int npcId) {
        for (Map.Entry<Integer, Integer> entry : NPC_CLUES.entrySet()) {
            int clueId = entry.getKey();
            int requiredNpcId = entry.getValue();

            // Check if they clicked the exact NPC and are holding the specific clue
            if (npcId == requiredNpcId && c.getItems().playerHasItem(clueId)) {
                
                // FIXED ISSUE: Check if this clue is supposed to give a Challenge Scroll!
                if (CLUE_TO_CHALLENGE.containsKey(clueId)) {
                    int challengeScrollId = CLUE_TO_CHALLENGE.get(clueId);
                    
                    c.getItems().deleteItem(clueId, 1);
                    c.getItems().addItem(challengeScrollId, 1);
                    
                    // Trigger the first dialogue node (e.g., "I must ask you something...")
                    c.getDH().sendDialogues(2780, npcId); 
                    return true;
                }

                // If it DOESN'T require a challenge scroll, progress normally
                c.sendMessage("You speak to the NPC and they hand you your next step.");
                TreasureTrails.progressClue(c, clueId); 
                return true; 
            }
        }
        return false;
    }
}