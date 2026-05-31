package server.model.players.content.treasuretrails.types;

import java.util.HashMap;
import java.util.Map;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;

public class EmoteClues {

    // A list of common Emote Button IDs (Adjust these if your server's ClickingButtons are different)
	public static final int RASPBERRY = 13367;
    public static final int BOW = 164;
    public static final int CHEER = 171;
    public static final int CLAP = 13362;
    public static final int DANCE = 166;
    public static final int JIG = 13363;
    public static final int HEADBANG = 13365;
    public static final int JUMP_FOR_JOY = 13366;
    public static final int LAUGH = 170;
    public static final int PANIC = 2155;
    public static final int SHRUG = 13368;
    public static final int SPIN = 13364;
    public static final int WAVE = 163;
    public static final int YAWN = 13369;

    public static class EmoteData {
        int emoteButtonId;
        int[] requiredEquipment; // Item IDs the player MUST be wearing
        boolean requiresEmpty;   // True if the clue says "Have nothing equipped at all"
        int minX, maxX, minY, maxY; // The bounding box of the area

        public EmoteData(int emoteButtonId, int[] requiredEquipment, boolean requiresEmpty, int minX, int maxX, int minY, int maxY) {
            this.emoteButtonId = emoteButtonId;
            this.requiredEquipment = requiredEquipment;
            this.requiresEmpty = requiresEmpty;
            this.minX = minX;
            this.maxX = maxX;
            this.minY = minY;
            this.maxY = maxY;
        }
    }

    public static final Map<Integer, EmoteData> EMOTE_CLUES = new HashMap<>();

    static {
        // Format: put(Clue_Item_ID, new EmoteData(Emote_Button_ID, new int[]{ Required_Gear }, requiresEmpty, minX, maxX, minY, maxY));

        // ==========================================
        // BEGINNER EMOTE CLUES
        // ==========================================

        // Blow a raspberry at Aris in her tent. Equip a gold ring and a gold necklace.
        EMOTE_CLUES.put(23182, new EmoteData(RASPBERRY, new int[]{ 1635, 1654 }, false, 3200, 3206, 3422, 3427));

        // Bow to Brugsen Bursen at the Grand Exchange. (No items required)
        EMOTE_CLUES.put(23183, new EmoteData(BOW, new int[]{}, false, 3160, 3170, 3485, 3495));

     // ==========================================
        // EASY EMOTE CLUES (Data mappings)
        // Format: put(Clue_ID, new EmoteData(Button_ID, new int[]{ Gear_IDs }, requiresEmpty, minX, maxX, minY, maxY));
        // ==========================================

        EMOTE_CLUES.put(10216, new EmoteData(RASPBERRY, new int[]{ 1133, 1087, 1379 }, false, 2594, 2605, 3270, 3282)); // Ardougne Zoo Monkey Cage
        EMOTE_CLUES.put(10222, new EmoteData(RASPBERRY, new int[]{ 1169, 1115, 1059 }, false, 2748, 2760, 3396, 3405)); // Keep Le Faye Entrance
        EMOTE_CLUES.put(10232, new EmoteData(BOW, new int[]{ 1101, 1095, 1169 }, false, 3310, 3320, 3235, 3245)); // Emir's Arena Office
        EMOTE_CLUES.put(10188, new EmoteData(BOW, new int[]{ 1073, 1696, 845 }, false, 2726, 2732, 3345, 3352)); // Legends' Guild Entrance
        EMOTE_CLUES.put(10210, new EmoteData(CHEER, new int[]{ 579, 1307, 4310 }, false, 2920, 2928, 3480, 3488)); // Druids' Circle
        EMOTE_CLUES.put(10212, new EmoteData(CHEER, new int[]{ }, true, 2190, 2210, 4930, 4950)); // Games Room Basement (Empty)
        EMOTE_CLUES.put(10192, new EmoteData(CHEER, new int[]{ 1169, 1083, 1656 }, false, 3040, 3050, 3234, 3238)); // Port Sarim Monks
        EMOTE_CLUES.put(10228, new EmoteData(CLAP, new int[]{ 1005, 9431, 1059 }, false, 3350, 3360, 3340, 3350)); // Exam Centre
        EMOTE_CLUES.put(10182, new EmoteData(CLAP, new int[]{ 1137, 1639, 1005 }, false, 3102, 3110, 3185, 3195)); // Wizards' Tower Causeway
        EMOTE_CLUES.put(10206, new EmoteData(CLAP, new int[]{ 9401, 4306, 5525 }, false, 2642, 2650, 3366, 3374)); // Top of East Ardougne Mill
        EMOTE_CLUES.put(31268, new EmoteData(JIG, new int[]{ 1025, 1321 }, false, 1, 10000, 1, 10000)); // Pandemonium (Placeholder coords)
        EMOTE_CLUES.put(10220, new EmoteData(JIG, new int[]{ 1639, 1694, 1103 }, false, 2598, 2605, 3410, 3420)); // Fishing Guild Entrance
        EMOTE_CLUES.put(10200, new EmoteData(DANCE, new int[]{ 1101, 1637, 839 }, false, 3105, 3115, 3288, 3296)); // Crossroads North of Draynor
        EMOTE_CLUES.put(19831, new EmoteData(DANCE, new int[]{ 1014, 1016, 5533 }, false, 3160, 3170, 3485, 3495)); // Grand Exchange Entrance
        EMOTE_CLUES.put(10208, new EmoteData(DANCE, new int[]{ 1157, 1119, 1081 }, false, 2732, 2744, 3467, 3478)); // Party Room
        EMOTE_CLUES.put(10180, new EmoteData(DANCE, new int[]{ 1205, 1153, 1635 }, false, 3196, 3204, 3165, 3172)); // Lumbridge Swamp Shed
        EMOTE_CLUES.put(19833, new EmoteData(JIG, new int[]{ 5527, 1383 }, false, 3251, 3255, 3399, 3403)); // Varrock Rune Store
        EMOTE_CLUES.put(10194, new EmoteData(HEADBANG, new int[]{ 6301, 1059, 1061 }, false, 3294, 3305, 3298, 3316)); // Mine North of Al Kharid
        EMOTE_CLUES.put(10214, new EmoteData(JUMP_FOR_JOY, new int[]{ 6301, 9407, 1353 }, false, 2755, 2765, 3440, 3450)); // Beehives Catherby
        EMOTE_CLUES.put(10226, new EmoteData(LAUGH, new int[]{ 1167, 581, 1323 }, false, 2740, 2750, 3545, 3555)); // Crossroads south of Sinclair Mansion
        EMOTE_CLUES.put(10186, new EmoteData(PANIC, new int[]{ 1087, 1269, 1141 }, false, 3365, 3380, 3365, 3380)); // Limestone Mine
        EMOTE_CLUES.put(10224, new EmoteData(PANIC, new int[]{ }, true, 2673, 2679, 3165, 3175)); // Fishing Trawler Pier (Empty)
        EMOTE_CLUES.put(10202, new EmoteData(SHRUG, new int[]{ 1654, 1635, 1237 }, false, 2975, 2985, 3230, 3245)); // Rimmington Mine
        EMOTE_CLUES.put(10218, new EmoteData(SPIN, new int[]{ 9423, 9413, 1095 }, false, 2975, 2985, 3230, 3245)); // Crossroads north of Rimmington
        EMOTE_CLUES.put(10196, new EmoteData(SPIN, new int[]{ 1115, 1097, 1155 }, false, 3085, 3095, 3330, 3340)); // Draynor Manor Fountain
        EMOTE_CLUES.put(12162, new EmoteData(SPIN, new int[]{ 1361, 1169, 1641 }, false, 3208, 3218, 3455, 3465)); // Varrock Castle Courtyard
        EMOTE_CLUES.put(10230, new EmoteData(WAVE, new int[]{ 1131, 1095, 1351 }, false, 3300, 3315, 3485, 3495)); // South Fence Lumber Yard
        EMOTE_CLUES.put(12164, new EmoteData(WAVE, new int[]{ 1273, 1125, 1191 }, false, 2943, 2948, 3380, 3385)); // Falador Gem Store
        EMOTE_CLUES.put(10190, new EmoteData(WAVE, new int[]{ 1019, 1095, 1424 }, false, 2995, 3005, 3105, 3125)); // Mudskipper Point
        EMOTE_CLUES.put(10184, new EmoteData(YAWN, new int[]{ 1097, 1191, 1295 }, false, 3075, 3085, 3245, 3255)); // Draynor Marketplace
        EMOTE_CLUES.put(28914, new EmoteData(YAWN, new int[]{ 1658, 1011, 9415 }, false, 1, 10000, 1, 10000)); // Fortis Grand Museum (Placeholder coords)
        EMOTE_CLUES.put(10204, new EmoteData(YAWN, new int[]{ 9403, 4306, 1335 }, false, 3208, 3215, 3490, 3497)); // Varrock Library
        // Cheer at Iffie Nitter. Equip a chef's hat and a red cape.
        EMOTE_CLUES.put(23184, new EmoteData(CHEER, new int[]{ 1949, 1007 }, false, 3203, 3208, 3413, 3418));

        // Clap at Bob's Brilliant Axes. Equip a bronze axe and leather boots.
        EMOTE_CLUES.put(23185, new EmoteData(CLAP, new int[]{ 1351, 1061 }, false, 3228, 3233, 3199, 3205));
     // ==========================================
        // MEDIUM EMOTE CLUES (Tier 2)
        // Format: put(Clue_ID, new EmoteData(Button_ID, new int[]{ Gear_IDs }, requiresEmpty, minX, maxX, minY, maxY));
        // ==========================================

        // Dance in the dark caves beneath Lumbridge Swamp (No gear required)
        EMOTE_CLUES.put(12021, new EmoteData(DANCE, new int[]{}, false, 3140, 3180, 9535, 9600)); 
        
        // Shrug in Catherby bank
        EMOTE_CLUES.put(12023, new EmoteData(SHRUG, new int[]{}, false, 2806, 2812, 3438, 3442)); 
        
        // Clap in Seers court house
        EMOTE_CLUES.put(12025, new EmoteData(CLAP, new int[]{}, false, 2728, 2738, 3467, 3476)); 
        
        // Cry on the shore of Catherby beach
        EMOTE_CLUES.put(12027, new EmoteData(168, new int[]{}, false, 2790, 2855, 3430, 3436)); // 168 is usually the Cry emote ID
        
        // Jump for joy in the TzHaar sword shop
        EMOTE_CLUES.put(12029, new EmoteData(JUMP_FOR_JOY, new int[]{}, false, 2475, 2485, 5142, 5150)); 
        
        // Cheer in the Edgeville general store
        EMOTE_CLUES.put(12031, new EmoteData(CHEER, new int[]{}, false, 3078, 3083, 3493, 3498)); 
        
        // Dance in the centre of Canifis
        EMOTE_CLUES.put(10254, new EmoteData(DANCE, new int[]{}, false, 3490, 3496, 3487, 3494)); 
        
        // Panic by the mausoleum in Morytania
        EMOTE_CLUES.put(10256, new EmoteData(PANIC, new int[]{}, false, 3498, 3504, 3564, 3568)); 
        
        // Spin on the bridge by the Barbarian Village
        EMOTE_CLUES.put(10258, new EmoteData(SPIN, new int[]{}, false, 3105, 3113, 3426, 3430)); 
        
        // Beckon in Tai Bwo Wannai
        EMOTE_CLUES.put(10260, new EmoteData(167, new int[]{}, false, 2775, 2800, 3075, 3095)); // 167 is usually Beckon
        
        // Yawn in the Castle Wars Lobby
        EMOTE_CLUES.put(10262, new EmoteData(YAWN, new int[]{}, false, 2435, 2445, 3080, 3095)); 
        
        // Cheer in the barbarian Agility Arena
        EMOTE_CLUES.put(10264, new EmoteData(CHEER, new int[]{}, false, 2530, 2545, 3540, 3555)); 
        
        // Cry on top of the western tree in the Gnome Agility Arena
        EMOTE_CLUES.put(10266, new EmoteData(168, new int[]{}, false, 2471, 2475, 3436, 3439)); 
        
        // Jump for joy in Yanille bank
        EMOTE_CLUES.put(10268, new EmoteData(JUMP_FOR_JOY, new int[]{}, false, 2609, 2614, 3089, 3095)); 
        
        // Think in the centre of the Observatory
        EMOTE_CLUES.put(10270, new EmoteData(162, new int[]{}, false, 2440, 2446, 3161, 3167)); // 162 is usually Think
        
        // Cheer in the Ogre Pen in the Training Camp
        EMOTE_CLUES.put(10272, new EmoteData(CHEER, new int[]{}, false, 2515, 2530, 3328, 3338)); 
        
        // Beckon in the Digsite, near the eastern winch
        EMOTE_CLUES.put(10274, new EmoteData(167, new int[]{}, false, 3307, 3313, 3374, 3378)); 
        
        // Cry in the Catherby Ranging shop
        EMOTE_CLUES.put(10276, new EmoteData(168, new int[]{}, false, 2821, 2826, 3440, 3445)); 
        
        // Dance a jig under Shantay's Awning
        EMOTE_CLUES.put(10278, new EmoteData(JIG, new int[]{}, false, 3300, 3306, 3120, 3125)); 
        
        // Beckon in the Shayzien Combat Ring
        EMOTE_CLUES.put(19776, new EmoteData(167, new int[]{}, false, 1558, 1572, 3614, 3626)); 
        
        // Yawn in the centre of the Arceuus Library
        EMOTE_CLUES.put(19778, new EmoteData(YAWN, new int[]{}, false, 1625, 1637, 3804, 3814)); 
        
        // Cry in the Draynor Village jail
        EMOTE_CLUES.put(19780, new EmoteData(168, new int[]{}, false, 3121, 3130, 3241, 3246)); 
        
        // Clap your hands north of Mount Karuulm
        EMOTE_CLUES.put(23046, new EmoteData(CLAP, new int[]{}, false, 1307, 1315, 3840, 3845));
    }

    /**
     * Called when a player clicks an emote button.
     */
    public static boolean handleEmote(Player c, int buttonId) {
        for (Map.Entry<Integer, EmoteData> entry : EMOTE_CLUES.entrySet()) {
            int clueId = entry.getKey();
            EmoteData data = entry.getValue();

            // 1. Check if the player actually has this clue in their inventory
            if (!c.getItems().playerHasItem(clueId)) {
                continue;
            }

            // 2. Check if they clicked the correct emote for this clue
            if (buttonId != data.emoteButtonId) {
                continue;
            }

            // 3. Check if they are standing in the correct area boundaries
            if (c.getX() < data.minX || c.getX() > data.maxX || c.getY() < data.minY || c.getY() > data.maxY) {
                continue; // Not in the right spot
            }

            // 4. Check for "Have nothing equipped at all"
            if (data.requiresEmpty) {
                if (c.getItems().isWearingItems()) {
                    c.sendMessage("You must have nothing equipped at all to complete this clue.");
                    return true;
                }
            } 
            // 5. Check if they are wearing the specific required items
            else {
                boolean missingItem = false;
                for (int reqItem : data.requiredEquipment) {
                    if (!c.getItems().isWearingItem(reqItem)) {
                        missingItem = true;
                        break;
                    }
                }
                if (missingItem) {
                    c.sendMessage("You are not wearing the correct equipment to solve this clue.");
                    return true;
                }
            }

            // --- SUCCESS! ---
            c.sendMessage("You've found another clue!");
            // You will need to route this to the correct tier progression
            TreasureTrails.progressClue(c, clueId); 
            return true;
        }
        return false;
    }
}