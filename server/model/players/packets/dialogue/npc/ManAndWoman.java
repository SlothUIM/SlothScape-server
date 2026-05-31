package server.model.players.packets.dialogue.npc;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.util.Misc;
import server.world.World;

/**
 * Handles dialogues for generic Men and Women, including 
 * random generic responses and location-specific variants.
 */
public class ManAndWoman extends NPCDialogue {

    private static final int FLYER = 2205; // Standard Flyer item ID

    public ManAndWoman(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 
            // Men
            3106, 3107, 3108, 3109, 6815, 6818, 6987, 6988, 6989,
            // Women
            3111, 3112, 3113, 6990, 6991, 6992, 10728
        };
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        String name = getNPCName(npcId);
        c.talkingNpc = npcId;
        switch (startDialogueId) {
            
            // ==========================================
            // ENTRY POINT
            // ==========================================
            case 1000:
                // 1. Check for location-specific dialogues first
                if (isAtFaladorSEHouse(c)) {
                    player(c, Anim.CALM_1, "Hello.");
                    c.nextChat = 1100; // Fancily Clad Man
                    return;
                } else if (isAtFaladorNEHouse(c)) {
                    player(c, Anim.CALM_1, "Hello.");
                    c.nextChat = 1200; // Pink Cap Man
                    return;
                } else if (isAtInn(c)) {
                    player(c, Anim.CALM_1, "Hello.");
                    c.nextChat = 1300; // Suited/Drunk Man
                    return;
                } else if (isAtMusaPoint(c)) {
                    // Musa point backpack men have random overhead-style lines converted to chat
                    handleMusaPointRandoms(c, name);
                    return;
                }

                // 2. If no specific location, do the standard greeting
                player(c, Anim.CALM_1, "Hello, how's it going?");
                c.nextChat = 1001;
                break;

            case 1001://apples arent real in the winter - madalynn 1/20/26
                // Roll a random number between 0 and 20 for the 21 different dialogue trees
                int randomDialogue = Misc.random(21);
                
                switch (randomDialogue) {
                    case 0:
                        npc(c, name, Anim.CALM_1, "Not too bad, but I'm a little worried about the increase", "of goblins these days.");
                        c.nextChat = 2000;
                        break;
                    case 1:
                        npc(c, name, Anim.CALM_1, "How can I help you?");
                        c.nextChat = 2010; // The 3-Option Dialogue
                        break;
                    case 21:
                        npc(c, name, Anim.CALM_1, "Apples aren't real in the winter.");
                        c.nextChat = 0; // The 3-Option Dialogue
                        break;
                    case 2:
                        npc(c, name, Anim.ANNOYED, "Get out of my way, I'm in a hurry!");
                        c.nextChat = 0;
                        break;
                    case 3:
                        npc(c, name, Anim.CALM_1, "I'm fine, how are you?");
                        c.nextChat = 2020;
                        break;
                    case 4:
                        npc(c, name, Anim.HAPPY, "Hello there! Nice weather we've been having.");
                        c.nextChat = 0;
                        break;
                    case 5:
                        npc(c, name, Anim.CALM_1, "I'm very well thank you.");
                        c.nextChat = 0;
                        break;
                    case 6:
                        npc(c, name, Anim.CONFUSED, "Who are you?");
                        c.nextChat = 2030;
                        break;
                    case 7:
                        npc(c, name, Anim.ANNOYED, "Do I know you? I'm in a hurry!");
                        c.nextChat = 0;
                        break;
                    case 8:
                        npc(c, name, Anim.CALM_1, "I think we need a new king. The one we've got", "isn't very good.");
                        c.nextChat = 0;
                        break;
                    case 9:
                        npc(c, name, Anim.CALM_1, "Not too bad thanks.");
                        c.nextChat = 0;
                        break;
                    case 10:
                        npc(c, name, Anim.ANGRY, "Are you asking for a fight?");
                        c.nextChat = 2040; // Attack trigger
                        break;
                    case 11:
                        npc(c, name, Anim.ANNOYED, "I'm busy right now.");
                        c.nextChat = 0;
                        break;
                    case 12:
                        npc(c, name, Anim.CALM_1, "Hello.");
                        c.nextChat = 0;
                        break;
                    case 13:
                        npc(c, name, Anim.ANNOYED, "None of your business.");
                        c.nextChat = 0;
                        break;
                    case 14:
                        npc(c, name, Anim.CALM_1, "No I don't have any spare change.");
                        c.nextChat = 0;
                        break;
                    case 15:
                        npc(c, name, Anim.SAD, "I'm a little worried - I've heard there's lots of people", "going about, killing citizens at random.");
                        c.nextChat = 0;
                        break;
                    case 16:
                        npc(c, name, Anim.ANNOYED, "No, I don't want to buy anything!");
                        c.nextChat = 0;
                        break;
                    case 17:
                        npc(c, name, Anim.CALM_1, "That is classified information.");
                        c.nextChat = 0;
                        break;
                    case 18:
                        npc(c, name, Anim.CALM_1, "Have this flyer...");
                        c.nextChat = 2050; // Give Flyer
                        break;
                    case 19:
                        npc(c, name, Anim.HAPPY, "Yo, wassup!");
                        c.nextChat = 0;
                        break;
                    case 20: // The rare "Toilet" dialogue (Originally NPC Contact, but fun to add)
                        npc(c, name, Anim.ANGRY, "Excuse me!");
                        c.nextChat = 2060;
                        break;
                }
                break;

            // ==========================================
            // RANDOM CONTINUATIONS
            // ==========================================
            case 2000:
                player(c, Anim.CALM_1, "Don't worry, I'll kill them.");
                c.nextChat = 0;
                break;
            
            case 2010:
                options(c, 2010, "Do you want to trade?", "I'm in search of a quest.", "I'm in search of enemies to kill.");
                break;
            case 2011:
                player(c, Anim.CALM_1, "Do you want to trade?");
                c.nextChat = 2012;
                break;
            case 2012:
                npc(c, name, Anim.CALM_1, "No, I have nothing I wish to get rid of. If you want", "to do some trading, there are plenty of shops", "and market stalls around though.");
                c.nextChat = 0;
                break;
            case 2013:
                player(c, Anim.CALM_1, "I'm in search of a quest.");
                c.nextChat = 2014;
                break;
            case 2014:
                npc(c, name, Anim.CALM_1, "I'm sorry I can't help you there.");
                c.nextChat = 0;
                break;
            case 2015:
                player(c, Anim.CALM_1, "I'm in search of enemies to kill.");
                c.nextChat = 2016;
                break;
            case 2016:
                npc(c, name, Anim.CALM_1, "I've heard there are many fearsome creatures", "that dwell under the ground...");
                c.nextChat = 0;
                break;

            case 2020:
                player(c, Anim.CALM_1, "Very well thank you.");
                c.nextChat = 0;
                break;

            case 2030:
                player(c, Anim.HAPPY, "I'm a bold adventurer.");
                c.nextChat = 2031;
                break;
            case 2031:
                npc(c, name, Anim.HAPPY, "Ah, a very noble profession.");
                c.nextChat = 0;
                break;

            case 2040:
                // Trigger combat
                c.getPA().closeAllWindows();
                if (c.talkingNpc > 0) {
                	NPC npc = NPCHandler.getNpc(c.talkingNpc);
        			int index = npc.getIndex();
        			npc.facePlayer(c.getIndex());
        			npc.underAttack = true;
        			npc.killerId = c.getIndex();
        			npc.forceChat("Take this!");
        			npc.underAttackBy = npc.killerId;
                    // Assuming you have a method to make the NPC attack the player
                	//World.getWorld().npcHandler.attackPlayer(c, index);
                   // World.getWorld().npcHandler.npcs[c.talkingNpc].forceChat("Take this!");
                   // World.getWorld().npcHandler.npcs[c.talkingNpc].killerId = c.getIndex();
                    //World.getWorld().npcHandler.npcs[c.talkingNpc].underAttack = true;
                }
                c.nextChat = 0;
                break;

            case 2050:
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(FLYER, 1);
                    c.sendMessage("You receive a flyer.");
                    
                } else {
                    c.sendMessage("Your inventory is full.");
                }
                c.getPA().removeAllWindows();
                c.nextChat = 0;
                break;

            case 2060:
                player(c, Anim.CONFUSED, "Oh sorry, what did I do?");
                c.nextChat = 2061;
                break;
            case 2061:
                npc(c, name, Anim.ANGRY, "Can't you see I'm on the toilet?");
                c.nextChat = 2062;
                break;
            case 2062:
                player(c, Anim.CONFUSED, "Wait a minute... a toilet?");
                c.nextChat = 2063;
                break;
            case 2063:
                npc(c, name, Anim.ANGRY, "Yeah. A toilet.");
                c.nextChat = 2064;
                break;
            case 2064:
                player(c, Anim.SAD, "Riiiight. Oh, man, you didn't wipe your hands?");
                c.nextChat = 2065;
                break;
            case 2065:
                npc(c, name, Anim.ANNOYED, "Get over it.");
                c.nextChat = 0;
                break;

            // ==========================================
            // SPECIFIC NPC CONTINUATIONS
            // ==========================================
            
            // Fancily Clad Man (Falador SE)
            case 1100:
                npc(c, name, Anim.CONFUSED, "What are you doing in my house?");
                c.nextChat = 1101;
                break;
            case 1101:
                player(c, Anim.CALM_1, "I was just exploring.");
                c.nextChat = 1102;
                break;
            case 1102:
                npc(c, name, Anim.CONFUSED, "You're exploring my house?");
                c.nextChat = 1103;
                break;
            case 1103:
                player(c, Anim.CALM_1, "You don't mind, do you?");
                c.nextChat = 1104;
                break;
            case 1104:
                npc(c, name, Anim.CONFUSED, "But... why are you exploring in my house?");
                c.nextChat = 1105;
                break;
            case 1105:
                player(c, Anim.CALM_1, "Oh, I don't know, I just wandered in, saw you", "and thought it'd be fun to speak to you.");
                c.nextChat = 1106;
                break;
            case 1106:
                npc(c, name, Anim.SAD, "... you are very strange...");
                c.nextChat = 1107;
                break;
            case 1107:
                player(c, Anim.CALM_1, "Perhaps I should go now.");
                c.nextChat = 1108;
                break;
            case 1108:
                npc(c, name, Anim.CALM_1, "Yes, please go away now.");
                // Optional: Make NPC step back
                c.nextChat = 0;
                break;

            // Pink Cap Man (Falador NE)
            case 1200:
                String title = c.playerEquipment[c.playerHat] == -1 ? "sir" : "madam"; // Rough check, you might have c.getGender()
                npc(c, name, Anim.HAPPY, "Good day, " + title + ". What brings you to this end of town?");
                c.nextChat = 1201;
                break;
            case 1201:
                player(c, Anim.CALM_1, "Well, what is there to do around here?");
                c.nextChat = 1202;
                break;
            case 1202:
                npc(c, name, Anim.CALM_1, "If you're into Mining, plenty! The dwarves have one", "of the largest mines in the world just under our feet.", "There's an entrance in the building just", "north-east of my house.");
                c.nextChat = 1203;
                break;
            case 1203:
                npc(c, name, Anim.HAPPY, "If you'd rather enjoy yourself, there's the party room", "just north of here! Or you might like to climb", "up onto the city wall - you can get a good", "view of Falador from there.");
                c.nextChat = 1204;
                break;
            case 1204:
                player(c, Anim.HAPPY, "Thanks.");
                c.nextChat = 0;
                break;

            // Drunk / Suited Man (Inns)
            case 1300:
                npc(c, name, Anim.DISORIENTED, "... whassup?");
                c.nextChat = 1301;
                break;
            case 1301:
                player(c, Anim.CONFUSED, "Are you alright?");
                c.nextChat = 1302;
                break;
            case 1302:
                npc(c, name, Anim.DISORIENTED, "... see... two of you... why there two of you?");
                c.nextChat = 1303;
                break;
            case 1303:
                player(c, Anim.CALM_1, "There's only one of me, friend.");
                c.nextChat = 1304;
                break;
            case 1304:
                npc(c, name, Anim.DISORIENTED, "... no, two of you... you can't count...", "... maybe you drunk too much...");
                c.nextChat = 1305;
                break;
            case 1305:
                player(c, Anim.CALM_1, "Whatever you say, friend.");
                c.nextChat = 1306;
                break;
            case 1306:
                npc(c, name, Anim.DISORIENTED, "... giant hairy cabbages...");
                c.nextChat = 0;
                break;
        }
    }

    @Override
	public String getDialogueRange() {
		return "1300-1306, 1200-1204, 1100-1108, 2000-2065, 1000, 1001";
	}
    
    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 2010) {
            switch (buttonId) {
                case OPT3_FIRST:
                    next(c, 2011); // Trade
                    break;
                case OPT3_SECOND:
                    next(c, 2013); // Quest
                    break;
                case OPT3_THIRD:
                    next(c, 2015); // Kill
                    break;
            }
        }
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================
    
    private String getNPCName(int npcId) {
        // Return Woman for female IDs, Man for male IDs
        int[] women = {3111, 3112, 3113, 6990, 6991, 6992, 10728};
        for (int i : women) {
            if (npcId == i) return "Woman";
        }
        return "Man";
    }

    private void handleMusaPointRandoms(Player c, String name) {
        int r = Misc.random(6);
        switch (r) {
            case 0: npc(c, name, Anim.ANGRY, "Molten lava! MOLTEN LAVA!"); break;
            case 1: npc(c, name, Anim.SAD, "Snakes! Snakes! Ohhhh, there are snakes!"); break;
            case 2: npc(c, name, Anim.CONFUSED, "Why don't I have a 'Teleport Home to Lumbridge'", "button like everyone else?"); break;
            case 3: npc(c, name, Anim.ANGRY, "Too many monkeys - I can't stand it!"); break;
            case 4: npc(c, name, Anim.SAD, "I should have stayed in Port Sarim."); break;
            case 5: npc(c, name, Anim.CONFUSED, "So... many... trees..."); break;
            case 6: npc(c, name, Anim.SAD, "I can't take the heat!"); break;
        }
        c.nextChat = 0;
    }

    // Coordinates are approximate based on OSRS map. Adjust to your server's exact regions.
    private boolean isAtFaladorSEHouse(Player c) {
        return c.getX() >= 3036 && c.getX() <= 3043 && c.getY() >= 3336 && c.getY() <= 3343;
    }

    private boolean isAtFaladorNEHouse(Player c) {
        return c.getX() >= 3028 && c.getX() <= 3033 && c.getY() >= 3369 && c.getY() <= 3374;
    }

    private boolean isAtInn(Player c) {
        // Blue Moon Inn (Varrock) or Jolly Boar Inn
        boolean blueMoon = c.getX() >= 3218 && c.getX() <= 3229 && c.getY() >= 3392 && c.getY() <= 3404;
        boolean jollyBoar = c.getX() >= 3273 && c.getX() <= 3283 && c.getY() >= 3486 && c.getY() <= 3496;
        return (blueMoon || jollyBoar) && c.getHeight() > 0; // Top floors
    }

    private boolean isAtMusaPoint(Player c) {
        return c.getX() >= 2875 && c.getX() <= 2936 && c.getY() >= 3133 && c.getY() <= 3182;
    }
}