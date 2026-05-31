package server.model.players.content.treasuretrails;

import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.CollectionLogRegistry;
import server.model.players.Player;
import server.model.players.content.treasuretrails.types.CoordinateClues;
import server.model.players.content.treasuretrails.types.ChallengeClue;
import server.util.Misc;

import java.util.Map;
import java.util.HashMap;

public class TreasureTrails {
	public static final Map<Integer, String[]> CLUE_TEXTS = new HashMap<>();

    static {
        // ==========================================
        // EASY CLUES (Searches & NPCs)
        // ==========================================
        CLUE_TEXTS.put(2677, new String[]{ "Search the chest in the", "Duke of Lumbridge's bedroom." });
        CLUE_TEXTS.put(2678, new String[]{ "Search the crate in the", "left-hand tower of", "Lumbridge Castle." });
        CLUE_TEXTS.put(2679, new String[]{ "Search the boxes in the", "goblin house near Lumbridge." });
        CLUE_TEXTS.put(2680, new String[]{ "Search the chests upstairs", "in Al Kharid Palace." });
        CLUE_TEXTS.put(2681, new String[]{ "Speak to Hans to", "solve the clue." });
        CLUE_TEXTS.put(2682, new String[]{ "Search the crates in the most", "north-western house in Al Kharid." });
        CLUE_TEXTS.put(2683, new String[]{ "Talk to Zeke in", "Al Kharid." });
        CLUE_TEXTS.put(2684, new String[]{ "Speak to Ellis in", "Al Kharid." });
        CLUE_TEXTS.put(2685, new String[]{ "Search the boxes in the house", "near the south entrance", "to Varrock." });
        CLUE_TEXTS.put(2686, new String[]{ "Speak to the bartender of the", "Blue Moon Inn in Varrock." });
        CLUE_TEXTS.put(2687, new String[]{ "Search the drawers, upstairs", "in the bank to the East", "of Varrock." });
        CLUE_TEXTS.put(2688, new String[]{ "Search the crates in", "Horvik's armoury." });
        CLUE_TEXTS.put(2689, new String[]{ "Search the drawers in one", "of Gertrude's bedrooms." });
        CLUE_TEXTS.put(2690, new String[]{ "Search the crates in the", "Barbarian Village helmet shop." });
        CLUE_TEXTS.put(2691, new String[]{ "Search the drawers upstairs", "in Falador's shield shop." });
        CLUE_TEXTS.put(2692, new String[]{ "Search the boxes of", "Falador's general store." });
        CLUE_TEXTS.put(2693, new String[]{ "Talk to the Squire in the", "White Knights' castle." });
        CLUE_TEXTS.put(2694, new String[]{ "Search the drawers in", "Falador's chain mail shop." });
        CLUE_TEXTS.put(2695, new String[]{ "Search the crates in the", "Port Sarim Fishing shop." });
        CLUE_TEXTS.put(2696, new String[]{ "Talk to the bartender of the", "Rusty Anchor in Port Sarim." });
        CLUE_TEXTS.put(2697, new String[]{ "Speak to Ned in", "Draynor Village." });
        CLUE_TEXTS.put(2698, new String[]{ "Speak to Doric, who lives", "north of Falador." });
        CLUE_TEXTS.put(2699, new String[]{ "Speak to Gaius in", "Taverley." });
        CLUE_TEXTS.put(2700, new String[]{ "Search the drawers in", "Catherby's Archery shop." });
        CLUE_TEXTS.put(2701, new String[]{ "Speak to Arhein in", "Catherby." });
        CLUE_TEXTS.put(2702, new String[]{ "Speak to Sir Kay in", "Camelot Castle." });
        CLUE_TEXTS.put(2703, new String[]{ "Search the chest in the", "left-hand tower of", "Camelot Castle." });

        // ==========================================
        // EASY EMOTE CLUES
        // ==========================================
        CLUE_TEXTS.put(10216, new String[]{ "Blow a raspberry at the monkey cage", "in Ardougne Zoo.", "", "Equip a studded leather body,", "bronze platelegs, and a", "normal staff with no orb." });
        CLUE_TEXTS.put(10222, new String[]{ "Blow raspberries outside the entrance", "to Keep Le Faye.", "", "Equip a coif, an iron platebody", "and leather gloves." });
        CLUE_TEXTS.put(10232, new String[]{ "Bow in the office of the", "Emir's Arena.", "", "Equip an iron chain body,", "leather chaps and a coif." });
        CLUE_TEXTS.put(10188, new String[]{ "Bow outside the entrance", "to the Legends' Guild.", "", "Equip iron platelegs, an emerald", "amulet and an oak longbow." });
        CLUE_TEXTS.put(10210, new String[]{ "Cheer at the Druids' Circle.", "", "Equip a blue wizard hat, a bronze", "two-handed sword and HAM boots." });
        CLUE_TEXTS.put(10212, new String[]{ "Cheer at the games room.", "", "Have nothing equipped at all", "when you do." });
        CLUE_TEXTS.put(10192, new String[]{ "Cheer for the monks at Port Sarim.", "", "Equip a coif, steel plateskirt", "and a sapphire necklace." });
        CLUE_TEXTS.put(10228, new String[]{ "Clap in the main exam room", "in the Exam Centre.", "", "Equip a white apron, green", "gnome boots and leather gloves." });
        CLUE_TEXTS.put(10182, new String[]{ "Clap on the causeway to", "the Wizards' Tower.", "", "Equip an iron med helm, emerald ring", "and a white apron." });
        CLUE_TEXTS.put(10206, new String[]{ "Clap on the top level of the mill,", "north of East Ardougne.", "", "Equip a blue gnome robe top,", "HAM robe bottom and", "an unenchanted tiara." });
        CLUE_TEXTS.put(31268, new String[]{ "Dance a jig behind the bar", "on the Pandemonium.", "", "Equip a right eye patch and", "a bronze scimitar." });
        CLUE_TEXTS.put(10220, new String[]{ "Dance a jig by the entrance", "to the Fishing Guild.", "", "Equip an emerald ring, a sapphire", "amulet, and a bronze chain body." });
        CLUE_TEXTS.put(10200, new String[]{ "Dance at the crossroads", "north of Draynor.", "", "Equip an iron chain body, a", "sapphire ring and a longbow." });
        CLUE_TEXTS.put(19831, new String[]{ "Dance at the entrance to the", "Grand Exchange.", "", "Equip a pink skirt, pink robe top", "and a body tiara." });
        CLUE_TEXTS.put(10208, new String[]{ "Dance in the Party Room.", "", "Equip a steel full helm, steel", "platebody and an iron plateskirt." });
        CLUE_TEXTS.put(10180, new String[]{ "Dance in the shack in", "Lumbridge Swamp.", "", "Equip a bronze dagger, iron", "full helmet and a gold ring." });
        CLUE_TEXTS.put(19833, new String[]{ "Do a jig in Varrock's rune store.", "", "Equip an air tiara and", "a staff of water." });
        CLUE_TEXTS.put(10194, new String[]{ "Headbang in the mine", "north of Al Kharid.", "", "Equip a desert shirt, leather", "gloves and leather boots." });
        CLUE_TEXTS.put(10214, new String[]{ "Jump for joy at the beehives.", "", "Equip a desert shirt, green gnome", "robe bottoms and a steel axe." });
        CLUE_TEXTS.put(10226, new String[]{ "Laugh at the crossroads south", "of the Sinclair mansion.", "", "Equip a cowl, a blue wizard robe", "top and an iron scimitar." });
        CLUE_TEXTS.put(10186, new String[]{ "Panic in the Limestone Mine.", "", "Equip bronze platelegs, a steel", "pickaxe and a steel med helm." });
        CLUE_TEXTS.put(10224, new String[]{ "Panic on the pier where you", "catch the Fishing trawler.", "", "Have nothing equipped at all", "when you do." });
        CLUE_TEXTS.put(10202, new String[]{ "Shrug in the mine near Rimmington.", "", "Equip a gold necklace, a gold ring", "and a bronze spear." });
        CLUE_TEXTS.put(10218, new String[]{ "Spin at the crossroads", "north of Rimmington.", "", "Equip a green gnome hat, cream", "gnome top and leather chaps." });
        CLUE_TEXTS.put(10196, new String[]{ "Spin in Draynor Manor", "by the fountain.", "", "Equip an iron platebody, studded", "leather chaps and a bronze full helm." });
        CLUE_TEXTS.put(12162, new String[]{ "Spin in the Varrock Castle courtyard.", "", "Equip a black axe, a coif", "and a ruby ring." });
        CLUE_TEXTS.put(10230, new String[]{ "Wave along the south fence", "of the Lumber Yard.", "", "Equip a hard leather body,", "leather chaps and a bronze axe." });
        CLUE_TEXTS.put(12164, new String[]{ "Wave in the Falador gem store.", "", "Equip a Mithril pickaxe, Black", "platebody and an Iron Kiteshield." });
        CLUE_TEXTS.put(10190, new String[]{ "Wave on Mudskipper Point.", "", "Equip a black cape, leather chaps", "and a steel mace." });
        CLUE_TEXTS.put(10184, new String[]{ "Yawn in Draynor Marketplace.", "", "Equip studded leather chaps, an iron", "kiteshield and a steel longsword." });
        CLUE_TEXTS.put(28914, new String[]{ "Yawn in the Fortis Grand Museum.", "", "Equip an emerald necklace, blue skirt", "and turquoise gnome robe top." });
        CLUE_TEXTS.put(10204, new String[]{ "Yawn in the Varrock library.", "", "Equip a green gnome robe top,", "HAM robe bottom and", "an iron warhammer." });
        
        // ==========================================
        // MEDIUM CLUES
        // ==========================================
        CLUE_TEXTS.put(2827, new String[]{ "South of Draynor", "Village bank." });
        CLUE_TEXTS.put(2829, new String[]{ "To the east of the", "Ranging guild." });
        CLUE_TEXTS.put(2831, new String[]{ "Look for a locked chest", "in the town's chapel." });
        CLUE_TEXTS.put(2833, new String[]{ "In a town where the guards", "are armed with maces." });
        CLUE_TEXTS.put(2841, new String[]{ "Speak to Uglug Nar." });
        CLUE_TEXTS.put(2848, new String[]{ "Speak to Hajedy." });
        CLUE_TEXTS.put(2853, new String[]{ "Speak to a referee." });
        CLUE_TEXTS.put(2855, new String[]{ "Speak to Donovan,", "the Family Handyman." });
        
        //Medium Cryptic
        CLUE_TEXTS.put(19760, new String[]{ "A graceful man of many colours,", "his crates must be full of many delights."});
        CLUE_TEXTS.put(3610, new String[]{ "Find a crate close to the monks that like to","paaarty!"});
        CLUE_TEXTS.put(3609, new String[]{ "A town with a different sort of night-life is your destination.", "Search for some crates in one of the houses."});
        CLUE_TEXTS.put(3607, new String[]{ "Go to the village being attack by trolls,","search the drawers in one of the houses."});
        CLUE_TEXTS.put(3605, new String[]{ "Search the updates drawers of a house"," in a village where pirates are known","to have a good time."});
        CLUE_TEXTS.put(3604, new String[]{ "In a village made of bamboo","look for some crates under "," one of the houses."});
        CLUE_TEXTS.put(7296, new String[]{ "The dead, red dragon waters over this chest.","He must really dig the view."});
        CLUE_TEXTS.put(7298, new String[]{ "Go to this building to be illuminated,","and check the drawers while you are there."});
        CLUE_TEXTS.put(7300, new String[]{ "Try not to step on any aquatic nasties while searching this crate."});
        CLUE_TEXTS.put(7301, new String[]{ "Probably filled with wizards socks."});
        CLUE_TEXTS.put(7303, new String[]{ "This crate is mine, all mine,", "even if it is in the middle of the desert."});
        CLUE_TEXTS.put(7304, new String[]{ "This crate holds a better reward", "than a broken arrow."});
        CLUE_TEXTS.put(2831, new String[]{ "Look for a locked chest in the town's chapel"});
        CLUE_TEXTS.put(2833, new String[]{ "In a town where the guards are armed with maces"});
        CLUE_TEXTS.put(2835, new String[]{ "In a town where the thieves steal from stalls"});
        CLUE_TEXTS.put(2837, new String[]{ "In a town where everyone has perfect vision"});
        CLUE_TEXTS.put(2839, new String[]{ "In a town where wizards are known to gather"});
        CLUE_TEXTS.put(2841, new String[]{ "Speak to Uglug Nar"});
        CLUE_TEXTS.put(2853, new String[]{ "Speak to a referee"});
        CLUE_TEXTS.put(2855, new String[]{ "Speak to Donovan, the Family Handyman"});
         //Medium Map
        CLUE_TEXTS.put(7286, new String[]{ ""});
        CLUE_TEXTS.put(7288, new String[]{ ""});
        CLUE_TEXTS.put(7290, new String[]{ ""});
        CLUE_TEXTS.put(7292, new String[]{ ""});
        CLUE_TEXTS.put(7294, new String[]{ ""});
        CLUE_TEXTS.put(3596, new String[]{ ""});
        CLUE_TEXTS.put(3598, new String[]{ ""});
        CLUE_TEXTS.put(3599, new String[]{ ""});
        CLUE_TEXTS.put(3601, new String[]{ ""});
        CLUE_TEXTS.put(3602, new String[]{ ""});
        CLUE_TEXTS.put(2827, new String[]{ ""});
        CLUE_TEXTS.put(2829, new String[]{ ""});
        CLUE_TEXTS.put(2827, new String[]{ ""});
        CLUE_TEXTS.put(2827, new String[]{ ""});
        // Anagrams (Medium)
        CLUE_TEXTS.put(2843, new String[]{ "The anagram reveals", "who to speak to next:", "OK CO" });
        CLUE_TEXTS.put(2845, new String[]{ "The anagram reveals", "who to speak to next:","EEK ZERO OP" });
        CLUE_TEXTS.put(2847, new String[]{ "The anagram reveals", "who to speak to next:","EL OW" });
        CLUE_TEXTS.put(2849, new String[]{ "The anagram reveals", "who to speak to next:","R AK MI" });
        CLUE_TEXTS.put(2851, new String[]{ "The anagram reveals", "who to speak to next:","ARE COL" });
        CLUE_TEXTS.put(2856, new String[]{ "The anagram reveals", "who to speak to next:","PEATY PERT" });
        CLUE_TEXTS.put(2857, new String[]{ "The anagram reveals", "who to speak to next:","GOBLIN KERN" });
        CLUE_TEXTS.put(2858, new String[]{ "The anagram reveals", "who to speak to next:","HALT US" });        
        CLUE_TEXTS.put(12055, new String[]{ "The anagram reveals", "who to speak to next:","GOBLETS ODD TOES" });
        CLUE_TEXTS.put(12057, new String[]{ "The anagram reveals", "who to speak to next:","A BAKER" });
        CLUE_TEXTS.put(12056, new String[]{ "The anagram reveals", "who to speak to next:","I EVEN" });
        CLUE_TEXTS.put(12061, new String[]{ "The anagram reveals", "who to speak to next:","A BASIC ANTI POT" });
        CLUE_TEXTS.put(12063, new String[]{ "The anagram reveals", "who to speak to next:","RATAI" });
        CLUE_TEXTS.put(12065, new String[]{ "The anagram reveals", "who to speak to next:","LEAKEY" });
        CLUE_TEXTS.put(12067, new String[]{ "The anagram reveals", "who to speak to next:","THICKNO" });
        CLUE_TEXTS.put(12069, new String[]{ "The anagram reveals", "who to speak to next:","KAY SIR" });
        CLUE_TEXTS.put(12071, new String[]{ "The anagram reveals", "who to speak to next:","HEORIC" });
        CLUE_TEXTS.put(19734, new String[]{ "The anagram reveals", "who to speak to next:","PACINNG A TAIE" });
        CLUE_TEXTS.put(19736, new String[]{ "The anagram reveals", "who to speak to next:","I DOOM ICON INN" });
        CLUE_TEXTS.put(19738, new String[]{ "The anagram reveals", "who to speak to next:","LOW LAG" });
        CLUE_TEXTS.put(19740, new String[]{ "The anagram reveals", "who to speak to next:","R SLICER" });
        CLUE_TEXTS.put(19742, new String[]{ "The anagram reveals", "who to speak to next:","HIS PHOR" });
        CLUE_TEXTS.put(19744, new String[]{ "The anagram reveals", "who to speak to next:","TAMED ROCKS" });
        CLUE_TEXTS.put(19746, new String[]{ "The anagram reveals", "who to speak to next:","AREA CHEF TREK" });
        CLUE_TEXTS.put(19748, new String[]{ "The anagram reveals", "who to speak to next:","SAND NUT" });
        CLUE_TEXTS.put(19750, new String[]{ "The anagram reveals", "who to speak to next:","ARMCHAIR THE PELT" });
        CLUE_TEXTS.put(19752, new String[]{ "The anagram reveals", "who to speak to next:","PEAK REFLEX" });
        CLUE_TEXTS.put(19754, new String[]{ "The anagram reveals", "who to speak to next:","QUE SIR" });
        CLUE_TEXTS.put(19756, new String[]{ "The anagram reveals", "who to speak to next:","I AM SIR" });
        CLUE_TEXTS.put(19758, new String[]{ "The anagram reveals", "who to speak to next:","A HEART" });
        CLUE_TEXTS.put(19762, new String[]{ "The anagram reveals", "who to speak to next:","QSPGFTTPS HSBDLMFCPOF" });
        CLUE_TEXTS.put(19764, new String[]{ "The anagram reveals", "who to speak to next:","XJABSE USBJCPSO" });
        CLUE_TEXTS.put(19766, new String[]{ "The anagram reveals", "who to speak to next:","ECRVCKP MJCNGF" });
        CLUE_TEXTS.put(19768, new String[]{ "The anagram reveals", "who to speak to next:","BMJ UIF LFCBC TFMMFS" });
        CLUE_TEXTS.put(19770, new String[]{ "The anagram reveals", "who to speak to next:","HQNM LZM STSNQ" });
        CLUE_TEXTS.put(19772, new String[]{ "The anagram reveals", "who to speak to next:","GUHCHO" });        
        CLUE_TEXTS.put(7274, new String[]{ "The anagram reveals", "who to speak to next:","DT RUN B" });
        CLUE_TEXTS.put(7276, new String[]{ "The anagram reveals", "who to speak to next:","GOT A BOY" });
        CLUE_TEXTS.put(7278, new String[]{ "The anagram reveals", "who to speak to next:","HICKJET" });
        CLUE_TEXTS.put(7280, new String[]{ "The anagram reveals", "who to speak to next:","ARC O LINE" });
        CLUE_TEXTS.put(7282, new String[]{ "The anagram reveals", "who to speak to next:","NOD MED" });
        CLUE_TEXTS.put(7284, new String[]{ "The anagram reveals", "who to speak to next:","LARK IN DOG" });
        CLUE_TEXTS.put(3611, new String[]{ "The anagram reveals", "who to speak to next:","ME IF" });
        CLUE_TEXTS.put(3612, new String[]{ "The anagram reveals", "who to speak to next:","BAIL TRIMS" });
        CLUE_TEXTS.put(3613, new String[]{ "The anagram reveals", "who to speak to next:","A BAS" });
        CLUE_TEXTS.put(3616, new String[]{ "The anagram reveals", "who to speak to next:","AHA JAR" });
        CLUE_TEXTS.put(3618, new String[]{ "The anagram reveals", "who to speak to next:","ICY FE" });
        CLUE_TEXTS.put(23131, new String[]{ "The anagram reveals", "who to speak to next:","CLASH ION" });
        CLUE_TEXTS.put(23133, new String[]{ "The anagram reveals", "who to speak to next:","CALAMARI MADE MUD" });

        // ==========================================
        // MEDIUM CLUES (Coordinates)
        // ==========================================

        CLUE_TEXTS.put(3582, new String[]{ "11.03N 31.20E" });
        CLUE_TEXTS.put(3584, new String[]{ "07.05N 30.56E" });
        CLUE_TEXTS.put(3586, new String[]{ "11.41N 14.58E" });
        CLUE_TEXTS.put(3588, new String[]{ "00.13S 13.58E" });
        CLUE_TEXTS.put(3590, new String[]{ "00.18S 09.28E" });
        CLUE_TEXTS.put(3592, new String[]{ "08.33N 01.39W" });
        CLUE_TEXTS.put(3594, new String[]{ "11.05N 00.45W" });        
        CLUE_TEXTS.put(2801, new String[]{ "02.48N 22.30E" });
        CLUE_TEXTS.put(2803, new String[]{ "01.35S 07.28E" });
        CLUE_TEXTS.put(2805, new String[]{ "01.26N 08.01E" });
        CLUE_TEXTS.put(2807, new String[]{ "06.31N 01.46W" });
        CLUE_TEXTS.put(2809, new String[]{ "00.05S 01.13E" });
        CLUE_TEXTS.put(2811, new String[]{ "09.33N 02.15E" });
        CLUE_TEXTS.put(2813, new String[]{ "02.50N 06.20E" });
        CLUE_TEXTS.put(2815, new String[]{ "04.13N 12.45E" });
        CLUE_TEXTS.put(2817, new String[]{ "04.00N 12.46E" });
        CLUE_TEXTS.put(2819, new String[]{ "00.31S 17.43E" });
        CLUE_TEXTS.put(2821, new String[]{ "07.33N 15.00E" });
        CLUE_TEXTS.put(2823, new String[]{ "00.30N 24.16E" });
        CLUE_TEXTS.put(2825, new String[]{ "05.43N 23.05E" });        
        CLUE_TEXTS.put(23131, new String[]{ "17.39N 37.16W" });
        CLUE_TEXTS.put(23133, new String[]{ "02.16N 12.07E" });
        CLUE_TEXTS.put(23135, new String[]{ "23.05N 41.22E" });
        CLUE_TEXTS.put(19774, new String[]{ "12.39N 30.07W" });
        CLUE_TEXTS.put(12033, new String[]{ "08.11S 04.48E" });
        CLUE_TEXTS.put(12035, new String[]{ "02.43S 33.26E" });
        CLUE_TEXTS.put(12037, new String[]{ "12.28N 34.37E" });
        CLUE_TEXTS.put(12039, new String[]{ "15.22N 07.31E" });
        CLUE_TEXTS.put(12041, new String[]{ "03.07S 03.41W" });
        CLUE_TEXTS.put(12043, new String[]{ "06.58N 21.16E" });
        CLUE_TEXTS.put(12045, new String[]{ "09.35N 01.50W" });
        CLUE_TEXTS.put(12047, new String[]{ "11.33N 02.24W" });
        CLUE_TEXTS.put(12049, new String[]{ "10.45N 04.31E" });
        CLUE_TEXTS.put(12051, new String[]{ "06.41N 27.15E" });
        CLUE_TEXTS.put(12053, new String[]{ "11.18N 30.54E" });        
        CLUE_TEXTS.put(7305, new String[]{ "22.30N 03.01E" });
        CLUE_TEXTS.put(7307, new String[]{ "05.20S 04.28E" });
        CLUE_TEXTS.put(7309, new String[]{ "01.18S 14.15E" });
        CLUE_TEXTS.put(7311, new String[]{ "09.48N 17.39E" });
        CLUE_TEXTS.put(7313, new String[]{ "00.20S 23.15E" });
        CLUE_TEXTS.put(7315, new String[]{ "14.54N 09.13E" });
        CLUE_TEXTS.put(7317, new String[]{ "03.35S 13.35E" });
        // ==========================================
        // MEDIUM CLUES (EMOTE)
        // ==========================================

        CLUE_TEXTS.put(12021, new String[]{ "Dance in the dark caves","beneath Lumbridge Swamp" });
        CLUE_TEXTS.put(12023, new String[]{ "Shrug in Catherby bank" });
        CLUE_TEXTS.put(12025, new String[]{ "Clap in Seers court house" });
        CLUE_TEXTS.put(12027, new String[]{ "Cry on the shore of Catherby beach" });
        CLUE_TEXTS.put(12029, new String[]{ "Jump for joy in the TzHaar sword shop" });
        CLUE_TEXTS.put(12031, new String[]{ "Cheer in the Edgeville general store" });
        CLUE_TEXTS.put(10254, new String[]{ "Dance in the centre of Canifis" });
        CLUE_TEXTS.put(10256, new String[]{ "Panic by the mausoleum in Morytania" });
        CLUE_TEXTS.put(10258, new String[]{ "Spin on the bridge by the Barbarian Village" });
        CLUE_TEXTS.put(10260, new String[]{ "Beckon in Tai Bwo Wannai" });
        CLUE_TEXTS.put(10262, new String[]{ "Yawn in the Castle Wars Lobby" });
        CLUE_TEXTS.put(10264, new String[]{ "Cheer in the barbarian Agility Arena" });
        CLUE_TEXTS.put(10266, new String[]{ "Cry on top of the western tree in the Gnome Agility Arena" });
        CLUE_TEXTS.put(10268, new String[]{ "Jump for joy in Yanille bank" });
        CLUE_TEXTS.put(10270, new String[]{ "Think in the centre of the Observatory" });
        CLUE_TEXTS.put(10272, new String[]{ "Cheer in the Orgre Pen in the Training Camp" });
        CLUE_TEXTS.put(10274, new String[]{ "Beckon in the Digsite, near the eastern winch" });
        CLUE_TEXTS.put(10276, new String[]{ "Cry in the Catherby Ranging shop" });
        CLUE_TEXTS.put(10278, new String[]{ "Dance a jig under Shantay's Awning" });
        CLUE_TEXTS.put(19776, new String[]{ "Beckon in the Shayzien Combat Ring" });
        CLUE_TEXTS.put(19778, new String[]{ "Yawn in the centre of the Arceuus Library" });
        CLUE_TEXTS.put(19780, new String[]{ "Cry in the Draynor Village jail" });
        CLUE_TEXTS.put(23046, new String[]{ "Clap you hands north of Mount Karuulm" });
        // ==========================================
        // HARD CLUES (Coordinates & Riddles)
        // ==========================================
        CLUE_TEXTS.put(2722, new String[]{ "A crate in the", "Lumber Yard." });
        CLUE_TEXTS.put(2723, new String[]{ "22.35N 19.18E" });
        CLUE_TEXTS.put(2725, new String[]{ "25.03N 17.05E" });
        CLUE_TEXTS.put(2727, new String[]{ "24.56N 22.28E" });
        CLUE_TEXTS.put(2729, new String[]{ "25.03N 23.24E" });
        CLUE_TEXTS.put(2731, new String[]{ "22.45N 26.33E" });
        CLUE_TEXTS.put(2733, new String[]{ "20.05N 21.52E" });
        
        // Hard Riddles
        CLUE_TEXTS.put(2773, new String[]{ "You will need to under-cook", "to solve this one." });
        CLUE_TEXTS.put(2774, new String[]{ "Come to the evil ledge." });
        CLUE_TEXTS.put(2776, new String[]{ "I am a token of the", "greatest love." });
        CLUE_TEXTS.put(2778, new String[]{ "If a man carried my burden," }); // Usually continued on next line in OSRS
        CLUE_TEXTS.put(2780, new String[]{ "Aggie I see." });
        CLUE_TEXTS.put(2782, new String[]{ "My home is grey, and", "made of stone." });
        CLUE_TEXTS.put(2783, new String[]{ "The beasts to my east", "snap claws and tails." });
        CLUE_TEXTS.put(2785, new String[]{ "Four blades I have,", "yet draw no blood." });
        CLUE_TEXTS.put(2786, new String[]{ "I lie lonely and forgotten", "in mid wilderness." });
        CLUE_TEXTS.put(2788, new String[]{ "46 is my number." });
        CLUE_TEXTS.put(2790, new String[]{ "My giant guardians below", "the market streets." });

        // BEGINNER CLUES (Cryptics)
        CLUE_TEXTS.put(23202, new String[]{ "Buried beneath the ground,", "who knows where it's found.", "Lucky for you, A man called", "Reldo may have a clue." });
    }

    public static final Map<Integer, String[]> BEGINNER_CLUE_TEXTS = new HashMap<>();

    static {
        BEGINNER_CLUE_TEXTS.put(0, new String[]{ "Blow a raspberry at Aris", "in her tent.", "", "Equip a gold ring and", "a gold necklace." });
        BEGINNER_CLUE_TEXTS.put(1, new String[]{ "Bow to Brugsen Bursen at", "the Grand Exchange." });
        BEGINNER_CLUE_TEXTS.put(2, new String[]{ "Cheer at Iffie Nitter.", "", "Equip a chef's hat", "and a red cape." });
        BEGINNER_CLUE_TEXTS.put(3, new String[]{ "Clap at Bob's Brilliant Axes.", "", "Equip a bronze axe and", "leather boots." });
        BEGINNER_CLUE_TEXTS.put(4, new String[]{ "Panic at Al Kharid mine." });
        BEGINNER_CLUE_TEXTS.put(5, new String[]{ "Spin at Flynn's Mace Shop." });
        
        // --- ANAGRAMS ---
        BEGINNER_CLUE_TEXTS.put(6, new String[]{ "The anagram reveals", "who to speak to next:", "AN EARL" });
        BEGINNER_CLUE_TEXTS.put(7, new String[]{ "The anagram reveals", "who to speak to next:", "CHAR GAME DISORDER" });
        BEGINNER_CLUE_TEXTS.put(8, new String[]{ "The anagram reveals", "who to speak to next:", "CARPET AHOY" });
        BEGINNER_CLUE_TEXTS.put(9, new String[]{ "The anagram reveals", "who to speak to next:", "I CORD" });
        BEGINNER_CLUE_TEXTS.put(10, new String[]{ "The anagram reveals", "who to speak to next:", "IN BAR" });
        BEGINNER_CLUE_TEXTS.put(11, new String[]{ "The anagram reveals", "who to speak to next:", "RAIN COVE" });
        BEGINNER_CLUE_TEXTS.put(12, new String[]{ "The anagram reveals", "who to speak to next:", "RUG DETER" });
        BEGINNER_CLUE_TEXTS.put(13, new String[]{ "The anagram reveals", "who to speak to next:", "SIR SHARE RED" });
        BEGINNER_CLUE_TEXTS.put(14, new String[]{ "The anagram reveals", "who to speak to next:", "TAUNT ROOF" });
        
        // --- CRYPTICS ---
        BEGINNER_CLUE_TEXTS.put(15, new String[]{ "Always walking around the", "castle grounds and somehow", "knows everyone's age." });
        BEGINNER_CLUE_TEXTS.put(16, new String[]{ "In the place Duke Horacio", "calls home, talk to a man with", "a hat dropped by goblins." });
        BEGINNER_CLUE_TEXTS.put(17, new String[]{ "In a village of barbarians,", "I am the one who guards the", "village from up high." });
        BEGINNER_CLUE_TEXTS.put(18, new String[]{ "Talk to Charlie the Tramp", "in Varrock." });
        BEGINNER_CLUE_TEXTS.put(19, new String[]{ "Near the open desert I reside,", "to get past me you must abide.", "Go forward if you dare,", "for when you pass me, you'll", "be sweating by your hair." });
    }

    public static void readClue(Player c, int itemId) {
        String[] textLines = null;

        if (CoordinateClues.COORD_CLUES.containsKey(itemId)) {
            textLines = CoordinateClues.COORD_CLUES.get(itemId).hintText;
        } else if (itemId == 23182) {
            if (c.currentBeginnerClueStep == -1) {
                c.currentBeginnerClueStep = Misc.random(BEGINNER_CLUE_TEXTS.size() - 1);
            }
            textLines = BEGINNER_CLUE_TEXTS.get(c.currentBeginnerClueStep);
            if (c.currentBeginnerClueStep == 18 && c.charlieClueTask > 0) {
                String itemName = c.getItems().getItemName(c.charlieClueTask);
                textLines = new String[]{ "Talk to Charlie the Tramp", "in Varrock.", "", "He wants: @blu@" + itemName };
            }
        } else {
            textLines = CLUE_TEXTS.get(itemId);
            if (itemId == 23200 && c.charlieClueTask > 0) {
                String itemName = c.getItems().getItemName(c.charlieClueTask);
                textLines = new String[]{ "Talk to Charlie the Tramp", "in Varrock.", "", "He wants: @blu@" + itemName };
            }
        }

        if (textLines == null) {
            c.sendMessage("The text on this clue is faded... (Missing text for Clue ID/Step: " + (itemId == 23182 ? c.currentBeginnerClueStep : itemId) + ")");
            return;
        }

        for (int i = 0; i < 8; i++) c.getPA().sendFrame126("", 6968 + i);

        int offset = (8 - textLines.length) / 2;
        if (offset < 0) offset = 0;
        for (int i = 0; i < textLines.length; i++) c.getPA().sendFrame126(textLines[i], 6968 + offset + i); 

        c.getPA().showInterface(6965);
    }

    public static final int BEGINNER_CASKET = 23245;
    public static final int EASY_CASKET = 20546;
    public static final int MEDIUM_CASKET = 20545;
    public static final int HARD_CASKET = 20544;
    public static final int ELITE_CASKET = 20543;
    public static final int MASTER_CASKET = 19836;
    
    public static final int[] BEGINNER_CLUES = { 23182 };
    public static final int[] EASY_CLUES = { 2677, 2678, 2679, 12162 };
    public static final int[] MEDIUM_CLUES = {
            2803, 2804, 2805, 2806, 2801, 2807, 2809, 2811, 2813, 2815, 2817, 2819, 2821, 2823, 2825,
            3582, 3584, 3586, 3588, 3590, 3592, 3594, 7305, 7307, 7309, 7311, 7313, 7315, 7317, 12033, 12035, 12037, 12039, 12041, 12043, 12045,
            12047, 12049, 12051, 12053, 19774, 23135, 23136, 23137, 28909, 31275, 2827, 2829, 2831, 2833, 2835, 2837, 2839, 2841, 2843, 2845, 2847,
            2848, 2849, 2851, 2853, 2855, 2856, 2857, 2858, 3596, 3598, 3599, 3601, 3602, 3604, 3605, 3607, 3609, 3610, 3611, 3612, 3613, 3614,
            3615, 3616, 3617, 3618, 7274, 7276, 7278, 7280, 7282, 7284, 7286, 7288, 7290, 7292, 7294, 7296, 7298, 7300, 7301, 7303, 7304, 10254,
            10256, 10258, 10260, 10262, 10264, 10266, 10268, 10270, 10272, 10274, 10276, 10278, 12021, 12023, 12025, 12027, 12029, 12031, 12055, 12057,
            12059, 12061, 12063, 12065, 12067, 12069, 12071, 19734, 19736, 19738, 19740, 19742, 19744, 19746, 19748, 19750, 19752, 19754, 19756, 19758,
            19760, 19762, 19764, 19766, 19768, 19770, 19772, 19776, 19778, 19780, 23046, 23131, 23133, 23138, 23139, 23140, 23141, 23142, 23143, 25783,
            25784, 28907, 28908, 29857, 29858, 30933, 30935, 31274
        };
    public static final int[] HARD_CLUES = { 2677, 2678, 2679 }; // Needs filling out later
    public static final int[] ELITE_CLUES = { 2677, 2678, 2679 }; // Needs filling out later
    public static final int[] MASTER_CLUES = { 2677, 2678, 2679 }; // Needs filling out later

    private static boolean contains(int[] array, int id) {
        for (int i : array) {
            if (i == id) return true;
        }
        return false;
    }

    /**
     * Checks if the item is ANY type of clue scroll or challenge scroll.
     */
    public static boolean isClueScroll(int itemId) {
        return contains(BEGINNER_CLUES, itemId) || contains(EASY_CLUES, itemId)
       		 || contains(MEDIUM_CLUES, itemId) || contains(HARD_CLUES, itemId)
    		 || contains(ELITE_CLUES, itemId) || contains(MASTER_CLUES, itemId)
             || contains(ChallengeClue.MEDIUM_CHALLENGES, itemId) // <-- ADDED
             || contains(ChallengeClue.HARD_CHALLENGES, itemId);  // <-- ADDED
    }

    /**
     * Returns the tier 0-5. Identifies Challenge Scrolls seamlessly!
     */
    public static int getClueTier(int itemId) {
        if (contains(BEGINNER_CLUES, itemId)) return 0;
        if (contains(EASY_CLUES, itemId)) return 1;
        if (contains(MEDIUM_CLUES, itemId) || contains(ChallengeClue.MEDIUM_CHALLENGES, itemId)) return 2; // <-- ADDED
        if (contains(HARD_CLUES, itemId) || contains(ChallengeClue.HARD_CHALLENGES, itemId)) return 3;    // <-- ADDED
        if (contains(ELITE_CLUES, itemId)) return 4;
        if (contains(MASTER_CLUES, itemId)) return 5;
        return -1;
    }

    public static void checkSteps(Player c, int itemId) {
        if (contains(BEGINNER_CLUES, itemId)) c.sendMessage("You have completed " + c.beginnerClueStepsCompleted + " steps on this beginner clue scroll.");
        else if (contains(EASY_CLUES, itemId)) c.sendMessage("You have completed " + c.easyClueStepsCompleted + " steps on this easy clue scroll.");
        else if (contains(MEDIUM_CLUES, itemId) || contains(ChallengeClue.MEDIUM_CHALLENGES, itemId)) c.sendMessage("You have completed " + c.mediumClueStepsCompleted + " steps on this medium clue scroll.");
        else if (contains(HARD_CLUES, itemId) || contains(ChallengeClue.HARD_CHALLENGES, itemId)) c.sendMessage("You have completed " + c.hardClueStepsCompleted + " steps on this hard clue scroll.");
        else if (contains(ELITE_CLUES, itemId)) c.sendMessage("You have completed " + c.eliteClueStepsCompleted + " steps on this elite clue scroll.");
        else if (contains(MASTER_CLUES, itemId)) c.sendMessage("You have completed " + c.masterClueStepsCompleted + " steps on this master clue scroll.");
        else c.sendMessage("You haven't completed any steps on this clue scroll yet.");
    }

    private static boolean isItemInLog(CollectionLogData log, int itemId) {
        if (log == null || log.getEntries() == null) return false;
        for (int[] entry : log.getEntries()) {
            if (entry[0] == itemId) return true;
        }
        return false;
    }

    public static void handleCollectionLog(Player c, String tierName, int itemId, int amount) {
        String rareTierName = tierName.replace("Clue", "RareClue");
        String[] logsToCheck = new String[] { tierName, rareTierName, "SharedClue" };

        for (String logName : logsToCheck) {
            CollectionLogData log = c.getCollectionLog().registry.getLog(logName);
            if (log != null && isItemInLog(log, itemId)) {
                log.addItem(itemId, amount);
                c.getPA().CollLogPopUp(log.getEntries(), itemId, amount);
                break; 
            }
        }
    }

    public static int generateSteps(int tier) {
        switch (tier) {
            case 0: 
                int roll = Misc.random(1, 100);
                if (roll <= 10) return 1;
                else if (roll <= 55) return 2;
                else return 3;
            case 1: return 2 + Misc.random(2); 
            case 2: return 3 + Misc.random(2);
            case 3: return 4 + Misc.random(2);
            case 4: return 5 + Misc.random(2);
            case 5: return 6 + Misc.random(2);
            default: return 1;
        }
    }

    public static void progressClue(Player c, int currentClueId) {
        if (!c.getItems().playerHasItem(currentClueId)) return;

        int tier = getClueTier(currentClueId);
        if (tier == -1) return; 

        c.getItems().deleteItem(currentClueId, 1);

        int casketId = -1;
        int[] cluePool = null;
        boolean finished = false;

        switch (tier) {
            case 0: 
                c.beginnerClueStepsCompleted++;
                if (c.beginnerClueStepsCompleted >= c.beginnerClueStepsTotal) {
                    finished = true;
                    c.beginnerClueStepsCompleted = 0; c.beginnerClueStepsTotal = 0;
                    casketId = BEGINNER_CASKET;
                }
                cluePool = BEGINNER_CLUES;
                break;
                
            case 1: 
                c.easyClueStepsCompleted++;
                if (c.easyClueStepsCompleted >= c.easyClueStepsTotal) {
                    finished = true;
                    c.easyClueStepsCompleted = 0; c.easyClueStepsTotal = 0;
                    casketId = EASY_CASKET;
                }
                cluePool = EASY_CLUES;
                break;
                
            case 2: 
                c.mediumClueStepsCompleted++;
                if (c.mediumClueStepsCompleted >= c.mediumClueStepsTotal) {
                    finished = true;
                    c.mediumClueStepsCompleted = 0; c.mediumClueStepsTotal = 0;
                    casketId = MEDIUM_CASKET;
                }
                cluePool = MEDIUM_CLUES; // <-- UNCOMMENTED SO IT WORKS!
                break;
                
            case 3: 
                c.hardClueStepsCompleted++;
                if (c.hardClueStepsCompleted >= c.hardClueStepsTotal) {
                    finished = true;
                    c.hardClueStepsCompleted = 0; c.hardClueStepsTotal = 0;
                    casketId = HARD_CASKET;
                }
                cluePool = HARD_CLUES; // <-- UNCOMMENTED SO IT WORKS!
                break;
                
            case 4: 
                c.eliteClueStepsCompleted++;
                if (c.eliteClueStepsCompleted >= c.eliteClueStepsTotal) {
                    finished = true;
                    c.eliteClueStepsCompleted = 0; c.eliteClueStepsTotal = 0;
                    casketId = ELITE_CASKET;
                }
                cluePool = ELITE_CLUES; // <-- UNCOMMENTED SO IT WORKS!
                break;
                
            case 5: 
                c.masterClueStepsCompleted++;
                if (c.masterClueStepsCompleted >= c.masterClueStepsTotal) {
                    finished = true;
                    c.masterClueStepsCompleted = 0; c.masterClueStepsTotal = 0;
                    casketId = MASTER_CASKET;
                }
                cluePool = MASTER_CLUES; // <-- UNCOMMENTED SO IT WORKS!
                break;
        }

        if (finished && casketId != -1) {
            c.getItems().addItem(casketId, 1);
            c.sendMessage("<col=800000>You've found a casket!</col>");
        } 
        else if (cluePool != null) {
            int newClueId = cluePool[Misc.random(cluePool.length - 1)];
            
            while (newClueId == currentClueId && cluePool.length > 1) {
                newClueId = cluePool[Misc.random(cluePool.length - 1)];
            }
            
            c.getItems().addItem(newClueId, 1);
            c.sendMessage("You've found another clue!");
        }
    }

    public static boolean hasClueOfTier(Player c, int[] tierArray) {
        for (int clueId : tierArray) {
            if (c.getItems().playerHasItem(clueId) || c.getItems().bankContains(clueId)) {
                return true;
            }
        }
        return false;
    }
}