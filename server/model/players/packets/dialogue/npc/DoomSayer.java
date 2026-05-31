package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class DoomSayer extends NPCDialogue {

    public DoomSayer(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 6773; // The Doomsayer
    }
    @Override
 	public String getDialogueRange() {
 		return "5430-5483";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {

            // ==========================================
            // STANDARD DIALOGUE
            // ==========================================
            case 5430:
                npc(c, "Doomsayer", Anim.ANGRY, "Dooooom!");
                c.nextChat = 5431;
                break;
            case 5431:
                player(c, Anim.CONFUSED, "Where?");
                c.nextChat = 5432;
                break;
            case 5432:
                npc(c, "Doomsayer", Anim.CALM_1, "All around us! I can feel it in the air, hear it on the", "wind, smell it... also in the air!");
                c.nextChat = 5433;
                break;
            case 5433:
                player(c, Anim.CALM_1, "Is there anything we can do about this doom?");
                c.nextChat = 5434;
                break;
            case 5434:
                npc(c, "Doomsayer", Anim.CALM_1, "There is nothing you need to do my friend! I am the", "Doomsayer, although my real title could be something", "like the Danger Tutor.");
                c.nextChat = 5435;
                break;
            case 5435:
                player(c, Anim.CONFUSED, "Danger Tutor?");
                c.nextChat = 5436;
                break;
            case 5436:
                npc(c, "Doomsayer", Anim.HAPPY, "Yes! I roam the world sensing danger.");
                c.nextChat = 5437;
                break;
            case 5437:
                npc(c, "Doomsayer", Anim.CALM_1, "If you see the signs often enough, then you can turn", "them off; by that time you likely know what the", "area has in store for you.");
                c.nextChat = 5438;
                break;
            case 5438:
                npc(c, "Doomsayer", Anim.CALM_1, "If I find a dangerous area, then I put up warning", "signs that will tell you what is so dangerous", "about that area.");
                c.nextChat = 5439;
                break;
            case 5439:
                player(c, Anim.CALM_1, "But what if I want to see the warnings again?");
                c.nextChat = 5440;
                break;
            case 5440:
                npc(c, "Doomsayer", Anim.HAPPY, "That's why I'm waiting here!");
                c.nextChat = 5441;
                break;
            case 5441:
                npc(c, "Doomsayer", Anim.CALM_1, "If you want to see the warning messages again,", "I can turn them back on for you.");
                
                // Branch based on player's warning settings
                if (hasDisabledWarnings(c)) {
                    c.nextChat = 5444; 
                } else {
                    c.nextChat = 5442;
                }
                break;

            // --- Path: No warnings disabled ---
            case 5442:
                player(c, Anim.HAPPY, "Thanks, I'll remember that if I see any warning messages.");
                c.nextChat = 5443;
                break;
            case 5443:
                npc(c, "Doomsayer", Anim.HAPPY, "You're welcome!");
                c.nextChat = 0;
                break;

            // --- Path: Warnings disabled ---
            case 5444:
                npc(c, "Doomsayer", Anim.CALM_1, "Do you need to turn on any warnings right now?");
                c.nextChat = 5445;
                break;
            case 5445:
                options(c, 5445, "Yes, I do.", "Not right now.");
                break;
            case 5446:
                player(c, Anim.CALM_1, "Yes, I do.");
                c.nextChat = 5447;
                break;
            case 5447:
                // Open the Doomsayer warnings interface here
                // c.getPA().showInterface(YOUR_DOOMSAYER_INTERFACE);
                end(c);
                break;
            case 5448:
                player(c, Anim.CALM_1, "Not right now.");
                c.nextChat = 5449;
                break;
            case 5449:
                npc(c, "Doomsayer", Anim.CALM_1, "Ok, keep an eye out for the messages though!");
                c.nextChat = 5450;
                break;
            case 5450:
                player(c, Anim.CALM_1, "I will.");
                c.nextChat = 0;
                break;

            // ==========================================
            // TREASURE TRAILS (EASY)
            // ==========================================
            case 5460:
                npc(c, "Doomsayer", Anim.CALM_1, "You got lucky this time, next time doom will be upon you.");
                c.nextChat = 5461;
                break;
            case 5461:
                // Replace 2677 with the actual Easy Clue or Casket ID depending on the step
                item("You've been given another clue!", c, 2677, ""); 
                c.nextChat = 0;
                break;

            // ==========================================
            // TREASURE TRAILS (HARD)
            // ==========================================
            case 5470:
                npc(c, "Doomsayer", Anim.HAPPY, "Ah! Here you go!");
                c.nextChat = 5471;
                break;
            case 5471:
                player(c, Anim.CONFUSED, "What?");
                c.nextChat = 5472;
                break;
            case 5472:
                npc(c, "Doomsayer", Anim.CALM_1, "I need you to answer this for me.");
                c.nextChat = 5473;
                break;
            case 5473:
                item("The Doomsayer has given you a challenge scroll!", c, 12130, "");
                c.nextChat = 0;
                break;

            case 5480: // Route here when spoken to again while holding a challenge scroll
                npc(c, "Doomsayer", Anim.CALM_1, "Please enter the answer to the question.");
                // c.getPA().sendEnterAmountInterface(); // Or string input interface
                c.nextChat = 0; 
                break;
            
            case 5481: // Route here if answer is wrong
                npc(c, "Doomsayer", Anim.CONFUSED, "How can that be? Try again!");
                c.nextChat = 0;
                break;
            
            case 5482: // Route here if answer is correct
                npc(c, "Doomsayer", Anim.HAPPY, "Spot on!");
                c.nextChat = 5483;
                break;
            case 5483:
                item("The Doomsayer has given you another clue scroll!", c, 2722, "");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 5445) {
            if (buttonId == OPT2_FIRST) {
                next(c, 5446); // "Yes, I do"
            } else if (buttonId == OPT2_SECOND) {
                next(c, 5448); // "Not right now"
            }
        }
    }

    // --- PLACEHOLDERS ---
    
    private boolean hasDisabledWarnings(Player c) {
        // Return true if the player has toggled any warnings off
        // e.g. return !c.wildyWarning || !c.teleportWarning;
        return false; 
    }
}