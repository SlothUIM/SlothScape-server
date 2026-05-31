package server.model.players.packets.dialogue.npc;

import server.model.minigames.NightmareZone;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class DominicOnion extends NPCDialogue {

    public DominicOnion(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 1120 }; // Replace with your Dom Onion ID
    }
	   @Override
		public String getDialogueRange() {
			return "10000-10505";
		}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            // ==========================================
            // MAIN MENU
            // ==========================================
            case 10000:
                npc(c, "Dominic Onion", Anim.CALM_1, "Welcome to the Nightmare Zone!", "Would you like me to create a dream for you?");
                c.nextChat = 10001;
                break;
            case 10001:
                options(c, 10001, "Who are you?", "What is this place?", "Can you tell me about the dreams?", "Can I choose a dream?", "I'll come back another time.");
                break;

            // ==========================================
            // OPTION 1: WHO ARE YOU?
            // ==========================================
            case 10010:
                player(c, Anim.CALM_1, "Who are you?");
                c.nextChat = 10011;
                break;
            case 10011:
                npc(c, "Dominic Onion", Anim.CALM_1, "My name's Dominic Onion but you can call me Dom,", "or Mr Onion.");
                c.nextChat = 10012;
                break;
            case 10012:
                player(c, Anim.CONFUSED, "Mr Oni...Dom, how did you get that name?");
                c.nextChat = 10013;
                break;
            case 10013:
                npc(c, "Dominic Onion", Anim.CALM_1, "Well that's a long story...but I can tell you", "that it involved my parents, 3 blurberry specials,", "and a chinchompa.");
                c.nextChat = 10014;
                break;
            case 10014:
                player(c, Anim.CONFUSED, "...and a chinchompa?");
                c.nextChat = 10015;
                break;
            case 10015:
                npc(c, "Dominic Onion", Anim.LAUGH_2, "Oh yes, Nigel played a crucial role in the whole debacle.", "It's all very amusing now that I think back to it.");
                c.nextChat = 10016;
                break;
            case 10016:
                player(c, Anim.CALM_1, "Right...");
                c.nextChat = 10017;
                break;
            case 10017:
                npc(c, "Dominic Onion", Anim.HAPPY, "Good times...");
                c.nextChat = 10001;
                break;

            // ==========================================
            // OPTION 2: WHAT IS THIS PLACE?
            // ==========================================
            case 10020:
                player(c, Anim.CALM_1, "What is this place?");
                c.nextChat = 10021;
                break;
            case 10021:
                npc(c, "Dominic Onion", Anim.HAPPY, "This is the Nightmare Zone!");
                c.nextChat = 10022;
                break;
            case 10022:
                player(c, Anim.SCARED, "Wwwwhat?!");
                c.nextChat = 10023;
                break;
            case 10023:
                npc(c, "Dominic Onion", Anim.CALM_1, "Don't worry, it's not as bad as it sounds...", "most of the time.");
                c.nextChat = 10024;
                break;
            case 10024:
                player(c, Anim.WORRIED, "That's comforting.");
                c.nextChat = 10025;
                break;
            case 10025:
                npc(c, "Dominic Onion", Anim.CALM_1, "Truth be told, that name wasn't my first choice,", "it's what the local folk have been calling it", "and so it sort of stuck.");
                c.nextChat = 10026;
                break;
            case 10026:
                npc(c, "Dominic Onion", Anim.CALM_1, "My original plan came to me in a vivid dream when I", "was younger you see, I saw myself running a successful", "business from a great structure in a strange land.");
                c.nextChat = 10027;
                break;
            case 10027:
                npc(c, "Dominic Onion", Anim.CALM_1, "Well, I say strange, but I'd never left Lunar Isle at", "the time and this place was very hot and dry,", "unlike anything I'd seen before.");
                c.nextChat = 10028;
                break;
            case 10028:
                npc(c, "Dominic Onion", Anim.CALM_1, "When I told my father about the dream he laughed and", "said I should become a banker like him and his father", "before him but in that moment I knew that", "it wasn't the life for me.");
                c.nextChat = 10029;
                break;
            case 10029:
                npc(c, "Dominic Onion", Anim.CALM_1, "I had to find out what this dream meant and if it was", "a vision of the future or not.", "Growing up, I studied hard to find out more about magic,", "especially oneiromancy and the interpretation of dreams.");
                c.nextChat = 10030;
                break;
            case 10030:
                npc(c, "Dominic Onion", Anim.CALM_1, "Having learned as much as I could on Lunar Isle I", "set out to find and learn from other great wizards", "I'd heard of in stories as a child.", "I set off to travel the world...");
                c.nextChat = 10031;
                break;
            case 10031:
                npc(c, "Dominic Onion", Anim.CALM_1, "...discovering more about dreams to enable me to", "understand the dream I'd had as a child.", "If so I knew I had to try and find this location", "and build the huge tower I saw.");
                c.nextChat = 10032;
                break;
            case 10032:
                npc(c, "Dominic Onion", Anim.CALM_1, "I was going to call it 'Dom Onion's Tower',", "but I've not found this strange place or gathered", "enough money to build it just yet.");
                c.nextChat = 10033;
                break;
            case 10033:
                npc(c, "Dominic Onion", Anim.HAPPY, "That's why I've setup this small business venture in the", "mean time selling dreams to people. I just need a big", "enough crowd to kickstart my business.", "It's all part of my five year plan!");
                c.nextChat = 10001; 
                break;

            // ==========================================
            // OPTION 3: TELL ME ABOUT DREAMS
            // ==========================================
            case 10040:
                player(c, Anim.CALM_1, "Can you tell me about the dreams?");
                c.nextChat = 10041;
                break;
            case 10041:
                npc(c, "Dominic Onion", Anim.CALM_1, "Certainly. I can delve into your memories and search", "for the key events that have affected you on", "your adventures.");
                c.nextChat = 10042;
                break;
            case 10042:
                npc(c, "Dominic Onion", Anim.CALM_1, "I then take the essence of these memories and put", "that in the vial you see on the plinth.", "I imbue a bit of magic, then give the vial a bit of a", "shake, and when you drink it you'll be able to re-live...");
                c.nextChat = 10043;
                break;
            case 10043:
                npc(c, "Dominic Onion", Anim.HAPPY, "...an encounter close to the original.", "Don't worry though, when you're in a dream, you're", "perfectly safe even if it does seem very real.");
                c.nextChat = 10044;
                break;
            case 10044:
                npc(c, "Dominic Onion", Anim.CALM_1, "If you sucessfully overcome the dream or die trying,", "you'll leave the dream state and return to normal", "with your grey matter and appendages intact.");
                c.nextChat = 10045;
                break;
            case 10045:
                player(c, Anim.CONFUSED, "Are all dreams the same or are there different options?");
                c.nextChat = 10046;
                break;
            case 10046:
                npc(c, "Dominic Onion", Anim.CALM_1, "Oh no, they can vary wildly! You can choose from", "one of three main options.", "Firstly, I can create a one-on-one encounter with", "an adversary you've defeated in your adventures.");
                c.nextChat = 10047;
                break;
            case 10047:
                npc(c, "Dominic Onion", Anim.CALM_1, "People like to practice in these dreams.", "Then there's a sort of Endurance dream, where you", "face enemies in encounter after encounter, and try", "to survive to the end.");
                c.nextChat = 10048;
                break;
            case 10048:
                npc(c, "Dominic Onion", Anim.HAPPY, "And finally there's something I like to call a Rumble,", "where you face multiple adversaries at the same time.", "I imbue a bit more magic and shake the", "vial harder to create that one!");
                c.nextChat = 10049;
                break;
            case 10049:
                npc(c, "Dominic Onion", Anim.CALM_1, "You can invite friends to fight alongside you in a Rumble.", "Once I've set up the dream for you, step into the", "enclosure and invite up to 4 friends.");
                c.nextChat = 10050;
                break;
            case 10050:
                npc(c, "Dominic Onion", Anim.CALM_1, "For the Endurance and Rumble dreams, I charge a fee.", "I don't like handling the money directly, so instead", "I've set up a secure coffer. After you've asked me", "to set up a dream, put some money in my coffer.");
                c.nextChat = 10051;
                break;
            case 10051:
                npc(c, "Dominic Onion", Anim.CALM_1, "I'll deduct the fee from the coffer when you start.", "There's a red vial on the other plinth. Your friends", "can drink from it to enter your dream as a spectator", "to observe your progress.");
                c.nextChat = 10052;
                break;
            case 10052:
                npc(c, "Dominic Onion", Anim.CALM_1, "Finally here's one other option you get, and that's", "how difficult the encounters are. As well as the normal", "dreams, the bravest adventurers can choose a", "harder version*.");
                c.nextChat = 10053;
                break;
            case 10053:
                npc(c, "Dominic Onion", Anim.CALM_1, "*Terms and conditions apply: Mr Onion, henceforth known", "as Dom, cannot be held liable for accidental loss of", "limbs (yours or otherwise) and or psychological damage", "as a result of entering the Nightmare Zone.");
                c.nextChat = 10054;
                break;
            case 10054:
                npc(c, "Dominic Onion", Anim.CALM_1, "Please note there is a strict no-refunds policy,", "and all dream purchases are final.");
                c.nextChat = 10001; 
                break;

            // ==========================================
            // OPTION 4: CAN I CHOOSE A DREAM?
            // ==========================================
            case 10060:
                player(c, Anim.CALM_1, "Can I choose a dream?");
                c.nextChat = 10061;
                break;
            case 10061:
                npc(c, "Dominic Onion", Anim.CALM_1, "Certainly. You can practice for free, but the larger", "dreams require more skill to imbue the potions", "and so there's a fee for those.");
                c.nextChat = 10062;
                break;
            case 10062:
                options(c, 10062, "Practice", "Endurance", "Rumble", "Previous", "Cancel");
                break;

            // --- PRACTICE BRANCH ---
            case 10100:
                options(c, 10100, "Normal", "Hard");
                break;

            // --- ENDURANCE BRANCH ---
            case 10200:
                options(c, 10200, "Normal", "Hard");
                break;
            case 10201: // Endurance Normal
                npc(c, "Dominic Onion", Anim.CALM_1, "For an Endurance dream, normal mode, I'll want 1,000", "coins. I'll deduct the money from the coffer when you", "start the dream.");
                c.nextChat = 10202;
                break;
            case 10202:
                options(c, 10202, "Yes", "No");
                break;
            case 10203: // Endurance Hard
                npc(c, "Dominic Onion", Anim.CALM_1, "For an Endurance dream, hard mode, I'll want 5,000", "coins. I'll deduct the money from the coffer when you", "start the dream.");
                c.nextChat = 10204;
                break;
            case 10204:
                options(c, 10204, "Yes", "No");
                break;

            // --- RUMBLE BRANCH ---
            case 10300:
                // Using standard options4 since Rumble has 4 main difficulties
                // Assuming you have an options4 method!
                c.getPA().sendFrame126("Select an Option", 2481);
                c.getPA().sendFrame126("Normal", 2482);
                c.getPA().sendFrame126("Hard", 2483);
                c.getPA().sendFrame126("Customisable - normal", 2484);
                c.getPA().sendFrame126("Customisable - hard", 2485);
                c.getPA().showChatboxInterface(2480);
                c.dialogueAction = 10300;
                break;
            
            // Rumble Normal
            case 10301: 
                npc(c, "Dominic Onion", Anim.CALM_1, "For a Rumble dream, normal mode, I'll want 2,000 coins.", "I'll deduct the money from the coffer when you", "start the dream.");
                c.nextChat = 10302;
                break;
            case 10302:
                options(c, 10302, "Yes", "No");
                break;

            // Rumble Hard
            case 10303: 
                npc(c, "Dominic Onion", Anim.CALM_1, "For a Rumble dream, hard mode, I'll want 6,000 coins.", "I'll deduct the money from the coffer when you", "start the dream.");
                c.nextChat = 10304;
                break;
            case 10304:
                options(c, 10304, "Yes", "No");
                break;

            // Customisable Normal
             // Customisable Normal
            case 10305: 
                // Assuming max quest points on your server is something like 250
                int normalCost = (c.questPoints >= 250) ? 12000 : 22000;
                npc(c, "Dominic Onion", Anim.CALM_1, "For a customisable Rumble dream, normal mode, I'll", "want " + normalCost + " coins. I'll deduct the money from the", "coffer when you start the dream.");
                c.nextChat = 10306;
                break;
            case 10306:
                options(c, 10306, "Yes", "No");
                break;

            // Customisable Hard (THE COMPLETED ONE)
            case 10307: 
                int hardCost = (c.questPoints >= 250) ? 16000 : 26000;
                npc(c, "Dominic Onion", Anim.CALM_1, "For a customisable Rumble dream, hard mode, I'll", "want " + hardCost + " coins. I'll deduct the money from the", "coffer when you start the dream.");
                c.nextChat = 10308;
                break;
            case 10308:
                options(c, 10308, "Yes", "No");
                break;
            case 10309:
                npc(c, "Dominic Onion", Anim.HAPPY, "I've prepared your dream. Select the bosses you", "wish to fight, then drink from the vial to begin.");
                c.nextChat = 10310;
                break;
            case 10310:
                c.getPA().removeAllWindows();
                // Opens our working Customisable Hard Interface
                
                NightmareZone.openSetupInterface(c);
                c.nextChat = 0;
                break;

            // ==========================================
            // CANCEL & PREVIOUS OPTIONS
            // ==========================================
            case 10090:
                player(c, Anim.CALM_1, "I'll come back another time.");
                c.nextChat = 0;
                break;

            // ==========================================
            // HAS DREAM PREPARED / RIGHT CLICK 'DREAM'
            // ==========================================
            case 10400: // Started via regular Talk-to
                npc(c, "Dominic Onion", Anim.CALM_1, "You haven't started that dream I created for you.", "Can I help you with something?");
                c.nextChat = 10401;
                break;
            case 10401:
                options(c, 10401, "I want to change my dream.", "Who are you?", "What is this place?", "Can you tell me about the dreams?", "I'll come back another time.");
                break;
            case 10402: // Wants to change
                player(c, Anim.CALM_1, "I want to change my dream.");
                c.nextChat = 10403;
                break;
            case 10403:
                npc(c, "Dominic Onion", Anim.CALM_1, "Okay, I've emptied the vial. I have not taken any", "money from you for that dream, since you", "never started it.", "Which dream would you like to experience?");
                c.nextChat = 10062; // Route back to dream selection
                break;

            case 10500: // Started via Right-Click "Dream"
                npc(c, "Dominic Onion", Anim.CALM_1, "I've already created a dream for you. Do you want", "me to cancel it?");
                c.nextChat = 10501;
                break;
            case 10501:
                options(c, 10501, "Yes, please cancel it.", "No, don't cancel it.");
                break;
            case 10502:
                player(c, Anim.CALM_1, "Yes, please cancel it.");
                c.nextChat = 10503;
                break;
            case 10503:
                npc(c, "Dominic Onion", Anim.CALM_1, "Okay, I've emptied the vial. I have not taken any", "money from you for that dream, since you", "never started it.");
                // c.hasDreamPrepared = false; // Remember to clear their dream variable here later!
                c.nextChat = 10001; // Route back to main menu
                break;
            case 10504:
                player(c, Anim.CALM_1, "No, don't cancel it.");
                c.nextChat = 10505;
                break;
            case 10505:
                npc(c, "Dominic Onion", Anim.CALM_1, "Okay. Drink from the vial when you're ready to", "start the dream.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 10001) { 
            if (buttonId == OPT5_FIRST) next(c, 10010);
            else if (buttonId == OPT5_SECOND) next(c, 10020);
            else if (buttonId == OPT5_THIRD) next(c, 10040);
            else if (buttonId == OPT5_FOURTH) next(c, 10060);
            else if (buttonId == OPT5_FIFTH) next(c, 10090);
            
        } else if (c.dialogueAction == 10062) { 
            if (buttonId == OPT5_FIRST) next(c, 10100); 
            else if (buttonId == OPT5_SECOND) next(c, 10200); 
            else if (buttonId == OPT5_THIRD) next(c, 10300); 
            else if (buttonId == OPT5_FOURTH) next(c, 10001); 
            else if (buttonId == OPT5_FIFTH) next(c, 10090); 
            
        } else if (c.dialogueAction == 10100) { // PRACTICE
            c.nmzMode = Player.NMZMode.PRACTICE;
            c.nmzFee = 0; // Practice is free!
            
            if (buttonId == OPT2_FIRST) {
                c.nmzHardMode = false;
                next(c, 10309); // Routes straight to the Interface Prep dialogue!
            } else if (buttonId == OPT2_SECOND) {
                c.nmzHardMode = true;
                next(c, 10309); 
            }
            
        } else if (c.dialogueAction == 10200) { // ENDURANCE SELECTION
            if (buttonId == OPT2_FIRST) next(c, 10201); 
            else if (buttonId == OPT2_SECOND) next(c, 10203); 
            
        } else if (c.dialogueAction == 10202) { // ENDURANCE NORMAL CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.ENDURANCE;
                c.nmzHardMode = false;
                NightmareZone.startStandardDream(c, 1000);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
            
        } else if (c.dialogueAction == 10204) { // ENDURANCE HARD CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.ENDURANCE;
                c.nmzHardMode = true;
                NightmareZone.startStandardDream(c, 5000);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
            
        } else if (c.dialogueAction == 10300) { // RUMBLE SELECTION
            if (buttonId == 9178) next(c, 10301); 
            else if (buttonId == 9179) next(c, 10303); 
            else if (buttonId == 9180) next(c, 10305); 
            else if (buttonId == 9181) next(c, 10307); 
            
        } else if (c.dialogueAction == 10302) { // RUMBLE NORMAL CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.RUMBLE;
                c.nmzHardMode = false;
                NightmareZone.startStandardDream(c, 2000);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
            
        } else if (c.dialogueAction == 10304) { // RUMBLE HARD CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.RUMBLE;
                c.nmzHardMode = true;
                NightmareZone.startStandardDream(c, 6000);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
            
        } else if (c.dialogueAction == 10306) { // CUST NORMAL CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.RUMBLE;
                c.nmzHardMode = false;
                c.nmzFee = (c.questPoints >= 250) ? 12000 : 22000; // <--- Dynamic Fee
                next(c, 10309);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
            
        } else if (c.dialogueAction == 10308) { // CUST HARD CONFIRM
            if (buttonId == OPT2_FIRST) {
                c.nmzMode = Player.NMZMode.RUMBLE;
                c.nmzHardMode = true;
                c.nmzFee = (c.questPoints >= 250) ? 16000 : 26000; // <--- Dynamic Fee
                next(c, 10309);
            } else if (buttonId == OPT2_SECOND) next(c, 10090);
        } else if (c.dialogueAction == 10401) { 
            if (buttonId == OPT5_FIRST) next(c, 10402);
            else if (buttonId == OPT5_SECOND) next(c, 10010);
            else if (buttonId == OPT5_THIRD) next(c, 10020);
            else if (buttonId == OPT5_FOURTH) next(c, 10040);
            else if (buttonId == OPT5_FIFTH) next(c, 10090);
            
        } else if (c.dialogueAction == 10501) { 
            if (buttonId == OPT2_FIRST) next(c, 10502);
            else if (buttonId == OPT2_SECOND) next(c, 10504);
        }
    }
}