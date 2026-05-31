package server.model.players.content.teleports;


import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.path.MovementQueue;

/**
 * Handles the server-side logic for the OSRS Fairy Ring System.
 */
public class FairyRings {

    // Verified OSRS Codes
    public static final String[] CODES = {
        "aiq", "air", "ajp", "ajq", "ajr", "ajs", "akp", "akq", 
        "akr", "aks", "alp", "alq", "alr", "als", "bip", "biq", 
        "bis", "bjp", "bjr", "bjs", "bkp", "bkq", "bkr", "bks", 
        "blp", "blq", "blr", "bls", "cip", "ciq", "cir", "cis", 
        "cjr", "ckp", "ckr", "cks", "clp", "clq", "clr", "cls", 
        "dip", "diq", "dir", "dis", "djp", "djr", "dkp", "dkr", 
        "dks", "dlq", "dlr", "dls"
    };

    // Corresponding Locations
    public static final String[] LOCATIONS = {
        "Asgarnia: Mudskipper Point", "Islands: South-east of Ardougne", "Varlamore: Avium Savannah", "Dungeons: Cave south of Dorgesh-Kaan", 
        "Kandarin: Slayer cave (Rellekka)", "Islands: Penguins (Miscellania)", "Desert: Necropolis", "Kandarin: Piscatoris Hunter area",
        "Great Kourend: Hosidius Vinery", "Feldip Hills: Feldip Hunter area", "Islands: Lighthouse", "Morytania: Haunted Woods (Canifis)", 
        "Other Realms: Abyssal Area", "Kandarin: McGrubor's Wood", "Islands: South-west of Mort Myre", "Desert: Near Kalphite Hive",
        "Kandarin: Ardougne Zoo (Unicorns)", "Islands: Isle of Souls", "Other Realms: Fisher King Realm", "Islands: Near Zul-Andra",
        "Feldip Hills: South of Castle Wars", "Other Realms: Enchanted Valley", "Morytania: Mort Myre Swamp", "Other Realms: Zanaris",
        "Dungeons: TzHaar Area", "Other Realms: Yu'biusk", "Kandarin: Legends' Guild", "Kebos Lowlands: Mount Quidamortem",
        "Islands: Miscellania", "Kandarin: North-west of Yanille", "Kebos Lowlands: South of Mount Karuulm", "Great Kourend: Arceuus Library",
        "Kandarin: Sinclair Mansion", "Other Realms: Cosmic Entity Plane", "Karamja: South of Tai Bwo Wannai", "Morytania: Canifis",
        "Islands: South of Draynor", "Islands: Ape Atoll", "Islands: Hazelmere's Home", "Islands: Spiders near Yanille",
        "Other Realms: Abyssal Nexus", "POH: Superior Garden", "Other Realms: Gorak Plane", "Misthalin: Wizards' Tower",
        "Kandarin: Tower of Life", "Kandarin: Sinclair Mansion (West)", "Karamja: Musa Point", "Misthalin: Edgeville",
        "Fremennik: Polar Hunter Area", "Desert: North of Nardah", "Islands: Poison Waste", "Dungeons: Myreque Hideout"
    };

    /**
     * Teleports the player to the location based on current dial settings.
     */
    public static void teleport(Player p) {
        String code = getCodeFromOptions(p);
        int configValue = getConfigForCode(code); // Uses the map we made from your dump
        int x = 0, y = 0, h = 0;

        switch (code) {
        // --- A Series ---
        case "aiq": x = 2992; y = 3116; break; // Asgarnia: Mudskipper Point
        case "air": x = 2700; y = 3247; break; // Islands: South-east of Ardougne
        case "ajq": x = 2735; y = 5221; break; // Dungeons: Cave south of Dorgesh-Kaan
        case "ajr": x = 2780; y = 3613; break; // Kandarin: Slayer cave south-east of Rellekka
        case "ajs": x = 2500; y = 3896; break; // Islands: Penguins near Miscellania
        case "akp": x = 3280; y = 2703; break; // Kharidian Desert: Necropolis
        case "akq": x = 2319; y = 3619; break; // Kandarin: Piscatoris Hunter area
        case "akr": x = 1823; y = 3501; break; // Great Kourend: Hosidius Vinery
        case "aks": x = 2571; y = 2956; break; // Feldip Hills: Feldip Hunter area
        case "alp": x = 2503; y = 3636; break; // Islands: Lighthouse
        case "alq": x = 3597; y = 3495; break; // Morytania: Haunted Woods east of Canifis
        case "alr": x = 3059; y = 4875; break; // Other Realms: Abyssal Area
        case "als": x = 2644; y = 3495; break; // Kandarin: McGrubor's Wood

        // --- B Series ---
        case "bip": x = 3410; y = 3324; break; // Islands: South-west of Mort Myre
        case "biq": x = 3251; y = 3095; break; // Kharidian Desert: Near Kalphite Hive
        case "bis": x = 2635; y = 3266; break; // Kandarin: Ardougne Zoo - Unicorns
        case "bjp": x = 2147; y = 3067; break; // Islands: Isle of Souls
        case "bjr": x = 2650; y = 4730; break; // Other Realms: Realm of the Fisher King
        case "bjs": x = 2150; y = 3070; break; // Islands: Near Zul-Andra
        case "bkp": x = 2385; y = 3035; break; // Feldip Hills: South of Castle Wars
        case "bkq": x = 3041; y = 4532; break; // Other Realms: Enchanted Valley
        case "bkr": x = 3469; y = 3431; break; // Morytania: Mort Myre Swamp, south of Canifis
        case "bks": x = 2412; y = 4456; break; // Other Realms: Zanaris
        case "blp": x = 4622; y = 5147; break; // Dungeons: TzHaar area
        case "blq": x = 2229; y = 4244; h = 1; break; // Other Realms: Yu-biusk
        case "blr": x = 2740; y = 3351; break; // Kandarin: Legends' Guild
        case "bls": x = 1258; y = 3564; break; // Kebos Lowlands: South of Mount Quidamortem

        // --- C Series ---
        case "cip": x = 2513; y = 3884; break; // Islands: Miscellania
        case "ciq": x = 2528; y = 3127; break; // Kandarin: North-west of Yanille
        case "cir": x = 1240; y = 3500; break; // Kebos Lowlands: South of Mount Karuulm
        case "cis": x = 1638; y = 3690; break; // Great Kourend: Arceuus Library
        case "cjr": x = 2705; y = 3576; break; // Kandarin: Sinclair Mansion
        case "ckp": x = 2075; y = 4848; break; // Other Realms: Cosmic entity's plane
        case "ckr": x = 2801; y = 3003; break; // Karamja: South of Tai Bwo Wannai Village
        case "cks": x = 3447; y = 3470; break; // Morytania: Canifis
        case "clp": x = 3082; y = 3206; break; // Islands: South of Draynor Village
        case "clr": x = 2735; y = 2742; break; // Island: Ape Atoll
        case "cls": x = 2682; y = 3081; break; // Islands: Hazelmere's home

        // --- D Series ---
        case "dip": x = 3037; y = 4763; break; // Other Realms: Abyssal Nexus
        case "diq": x = 0; y = 0; break; // POH: Superior Garden (Requires special handling if in house)
        case "dir": x = 3038; y = 5348; break; // Other Realms: Gorak's Plane
        case "dis": x = 3108; y = 3149; break; // Misthalin: Wizards' Tower
        case "djp": x = 2658; y = 3230; break; // Kandarin: Tower of Life
        case "djr": x = 1455; y = 3658; break; // Great Kourend: Chasm of Fire
        case "dkp": x = 2900; y = 3111; break; // Karamja: South of Musa Point
        case "dkq": x = 4183; y = 5726; break; // Dungeons: Glacor Cave
        case "dkr": x = 3129; y = 3496; break; // Misthalin: Edgeville
        case "dks": x = 2744; y = 3719; break; // Fremennik: Polar Hunter area
        case "dlq": x = 3423; y = 3016; break; // Kharidian Desert: North of Nardah
        case "dlr": x = 2213; y = 3099; break; // Islands: Poison Waste south of Isafdar
        case "dls": x = 3501; y = 9821; break; // Dungeons: Myreque hideout
            default:
                p.sendMessage("The ring doesn't seem to lead anywhere.");
                return;
        }
        p.lastFairyRingX = x;
        p.lastFairyRingY = y;
        p.getPA().removeAllWindows();
        CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
	        int tick = 0;

	        @Override
	        public void execute(CycleEventContainer container) {
	            switch (tick) {
	                case 0: // Tick 1
	                    p.getMovementQueue().addStep(p.objectX, p.objectY, 0);
	                    break;
	                case 2:

	                    if (configValue != -1) {
	                        p.getPA().sendConfig(1423, configValue);
	                    }
	    	            p.getPA().startTeleport(p.lastFairyRingX, p.lastFairyRingY, 0, "fairy");
	    	            
	                	container.stop();
	                	break;
	            }
	            tick++;
	        }

	        @Override
	        public void stop() {
	            // Optional: cleanup after transformation ends
	        }
	    }, 1); // Runs every tick
    }

    /**
     * Converts current dial options into a 3-letter string code.
     */
    private static String getCodeFromOptions(Player p) {
        char c1 = (char) ('a' + p.fairyRingOption1);
        char c2 = getCharFromDial2(p.fairyRingOption2);
        char c3 = getCharFromDial3(p.fairyRingOption3);
        return "" + c1 + c2 + c3;
    }

    private static char getCharFromDial2(int i) {
        switch (i) { case 0: return 'i'; case 1: return 'j'; case 2: return 'k'; case 3: return 'l'; default: return 'i'; }
    }

    private static char getCharFromDial3(int i) {
        switch (i) { case 0: return 'p'; case 1: return 'q'; case 2: return 'r'; case 3: return 's'; default: return 'p'; }
    }
    /**
     * Maps the 3-letter code to the Config 1423 value based on your dump.
     */
    public static int getConfigForCode(String code) {
        switch (code.toLowerCase()) {
            case "aip": return 0;  case "ais": return 1;  case "air": return 2;  case "aiq": return 3;
            case "alp": return 4;  case "als": return 5;  case "alr": return 6;  case "alq": return 7;
            case "akp": return 8;  case "aks": return 9;  case "akr": return 10; case "akq": return 11;
            case "ajp": return 12; case "ajs": return 13; case "ajr": return 14; case "ajq": return 15;
            case "dip": return 16; case "dis": return 17; case "dir": return 18; case "diq": return 19;
            case "dlp": return 20;
            // Follow the pattern for the rest (B, C, and remaining D codes)
            default: return -1;
        }
    }
    /**
     * Opens the interface and sets up visibility configs.
     */
    public static void open(Player p) {
    	if(p.getItems().playerHasItem(772) || p.getItems().isWearingItem(772)) {
			p.getPA().showInterface(49430);
			p.getPA().sendFrame248(49430, 63460);
	        // Reset search visibility to all-on by default
	        for (int i = 0; i < LOCATIONS.length; i++) {
	            p.getPA().sendConfig(925 + i, 1);
	        }
	        updateDialConfigs(p);
    	} else {
    		p.sendMessage("The fairy rings only work for those who wield fairy magic.");
    	}
    }
    public static void handleLogButtons(Player p, int buttonId) {
    	switch(buttonId) {
    	case 247238://
            p.getPA().sendConfig(1423, 0);
    		break;
    	case 247239:
            p.getPA().sendConfig(1423, 1);
    		break;
    	case 247240:
            p.getPA().sendConfig(1423, 2);
    		break;
    	case 247241:
            p.getPA().sendConfig(1423, 3);
    		break;
    	case 247242:
            p.getPA().sendConfig(1423, 4);
    		break;
    	case 247243:
            p.getPA().sendConfig(1423, 5);
    		break;
    	case 247244:
            p.getPA().sendConfig(1423, 6);
    		break;
    	}
    }
    public static void updateDialConfigs(Player p) {
        p.getPA().sendConfig(756, p.fairyRingOption1);
        p.getPA().sendConfig(757, p.fairyRingOption2);
        p.getPA().sendConfig(758, p.fairyRingOption3);
    }
    public static void updateObject(Player p, int i) {
        p.getPA().sendConfig(1423, i);
    	
    }
}