package server.model.players.packets.dialogue;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import server.model.players.Player;

public final class DialogueService {
    private DialogueService() {}
    private static final Map<Integer, Class<? extends NPCDialogue>> dialogueMap = new HashMap<>();
    
    public static void init() {
        try {
            System.out.println("[DialogueService] Scanning for Dialogue classes...");
            
            // 1. Tell the Reflections library exactly which package to scan
            Reflections reflections = new Reflections("server.model.players.packets.dialogue.npc");
            
            // 2. Automatically find EVERY class that extends NPCDialogue
            Set<Class<? extends NPCDialogue>> classes = reflections.getSubTypesOf(NPCDialogue.class);

            if (classes.isEmpty()) {
                System.err.println("[DialogueService] CRITICAL: No dialogue classes found! Check your package path.");
                return;
            }

            // 3. Loop through them and register them, just like your old code
            for (Class<? extends NPCDialogue> clazz : classes) {
                // Skip abstract classes or interfaces
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    try {
                        // Create a temporary instance using your specific constructor
                        NPCDialogue temp = clazz.getConstructor(Player.class).newInstance((Player) null);
                        
                        // Load Single ID
                        int singleId = temp.getNPCID();
                        if (singleId != -1) {
                            dialogueMap.put(singleId, clazz);
                        }
                        
                        // Load Multiple IDs (For Men/Women, Guards, etc.)
                        int[] multipleIds = temp.getNPCIDs();
                        if (multipleIds != null && multipleIds.length > 0) {
                            for (int id : multipleIds) {
                                dialogueMap.put(id, clazz);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("[DialogueService] Failed to load dialogue class: " + clazz.getSimpleName());
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println("[DialogueService] Successfully loaded " + classes.size() + " NPC Dialogues!");
            
        } catch (Exception e) {
            System.err.println("[DialogueService] Error during automated class loading:");
            e.printStackTrace();
        }
    }

    public static void open(Player c, int npcId, int startId) {
        Class<? extends NPCDialogue> clazz = dialogueMap.get(npcId);
        
        if (clazz != null && npcId != -1) {
            try {
                NPCDialogue dialogue = clazz.getConstructor(Player.class).newInstance(c);
                c.currentDialogue = dialogue;
                dialogue.onOpen(c, startId, npcId);
            } catch (Exception e) {
                System.err.println("Error opening dialogue for NPC " + npcId);
                e.printStackTrace();
            }
        } 
    }

    public static boolean handleOption(Player c, int buttonId) {
        if (c.currentDialogue != null) {
            c.currentDialogue.onOption(c, buttonId);
            return true;
        }
        return false;
    }

    public static void close(Player c) {
        if (c.currentDialogue != null) {
            c.currentDialogue.onClose(c);
            c.currentDialogue = null;
        }
    }
    
    // ==========================================
    // THE DUMP METHOD
    // ==========================================
    /**
     * Call this from an admin command (e.g., ::dumpdialogues)
     * to see exactly what IDs are successfully loaded into memory.
     */
    public static void dumpRegisteredDialogues(Player c) {
        c.sendMessage("@blu@[DialogueService] @bla@Dumping " + dialogueMap.size() + " mappings to console...");
        
        System.out.println("\n===================================================================================================");
        System.out.println(String.format("%-8s | %-25s | %-60s", "NPC ID", "DIALOGUE CLASS", "ID RANGES (HANDLED CASES)"));
        System.out.println("---------------------------------------------------------------------------------------------------");

        // Sort or just iterate through the map
        for (Map.Entry<Integer, Class<? extends NPCDialogue>> entry : dialogueMap.entrySet()) {
            try {
                // Instantiate with null player just to peek at the metadata methods
                NPCDialogue temp = entry.getValue().getConstructor(Player.class).newInstance((Player) null);
                
                String npcId = String.valueOf(entry.getKey());
                String className = entry.getValue().getSimpleName();
                String ranges = temp.getDialogueRange();
 
                System.out.println(String.format("%-8s | %-25s | %-60s", npcId, className, ranges));

            } catch (Exception e) {
                // Fallback if the class doesn't have the expected constructor or fails to init
                System.out.println(String.format("%-8d | %-25s | %-60s", 
                    entry.getKey(), entry.getValue().getSimpleName(), "ERROR: Could not read ranges"));
            }
        }
        System.out.println("===================================================================================================\n");
    }
}