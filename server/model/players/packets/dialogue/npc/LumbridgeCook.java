package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.CooksAssistant;

public class LumbridgeCook extends NPCDialogue {

    private static final int EGG = 1944;
    private static final int MILK = 1927;
    private static final int FLOUR = 1933;
    private static final int EMPTY_POT = 1931;
    private static final int EMPTY_BUCKET = 1925;

    public LumbridgeCook(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 4626; 
    }
    @Override
	public String getDialogueRange() {
		return "50-77";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {

            // ==========================================
            // ENTRY POINT - ID 50
            // ==========================================
            case 50:
                if (c.questStages[CooksAssistant.QUEST_ID] == CooksAssistant.COMPLETED) {
                    npc(c, "Cook", Anim.HAPPY, "Hello friend! The Duke loved his cake.", "Thank you again for your help.");
                    c.nextChat = 0;
                } else if (c.questStages[CooksAssistant.QUEST_ID] == CooksAssistant.STARTED) {
                    npc(c, "Cook", Anim.CALM_1, "How are you getting on with finding the ingredients?");
                    c.nextChat = 80; // Go to "Check Progress" logic
                } else {
                    npc(c, "Cook", Anim.SAD, "What am I to do?");
                    c.nextChat = 51;
                }
                break;

            case 51:
                options(c, 51, "What's wrong?", "Nice hat!", "You don't look very happy.");
                break;

            // ------------------------------------------
            // PATH: "What's wrong?"
            // ------------------------------------------
            case 52:
                player(c, Anim.CALM_1, "What's wrong?");
                c.nextChat = 53;
                break;

            case 53:
                npc(c, "Cook", Anim.SAD, "Oh dear, I'm in a terrible mess! It's the Duke's", 
                    "birthday today, and I should be making him a cake.", 
                    "I've forgotten the ingredients! Would you help me?");
                c.nextChat = 54;
                break;

            case 54:
                options(c, 54, "Yes, I'll help you.", "No, I don't feel like it.");
                break;

            case 55:
                player(c, Anim.HAPPY, "Yes, I'll help you.");
                if (hasAllItems(c)) {
                    c.nextChat = 57; // Quick completion
                } else {
                    c.nextChat = 56;
                }
                break;

            case 56:
            	c.questStages[CooksAssistant.QUEST_ID] = CooksAssistant.STARTED;
                npc(c, "Cook", Anim.HAPPY, "Oh thank you! I need a bucket of milk, an egg,", 
                    "and a pot of flour. If you need help finding", "them, just ask!");
                c.nextChat = 0;
                break;

            // ------------------------------------------
            // PATH: DELIVERY / COMPLETION
            // ------------------------------------------
            case 57:
                player(c, Anim.CALM_1, "I have all of those ingredients on me already!");
                c.nextChat = 58;
                break;

            case 58:
                npc(c, "Cook", Anim.CALM_1, "That's an odd coincidence... Were you planning", "on making a cake too?");
                c.nextChat = 59;
                break;

            case 59:
                player(c, Anim.CALM_1, "Not exactly. Lucky guess I suppose.");
                c.nextChat = 60;
                break;

            case 60:
                npc(c, "Cook", Anim.HAPPY, "You've brought me everything I need! I am saved!", "Thank you!");
                c.nextChat = 61;
                break;

            case 61:
                player(c, Anim.CALM_1, "So do I get to go to the Duke's Party?");
                c.nextChat = 62;
                break;

            case 62:
                npc(c, "Cook", Anim.CALM_1, "I'm afraid not, only the big cheeses get to", "dine with the Duke.");
                c.nextChat = 63;
                break;

            case 63:
                // Final Completion
                c.getItems().deleteItem(EGG, 1);
                c.getItems().deleteItem(MILK, 1);
                c.getItems().deleteItem(FLOUR, 1);
                new CooksAssistant(c).setStage(CooksAssistant.COMPLETED);
                new CooksAssistant(c).giveRewards();
                c.nextChat = 0;
                break;

            // ------------------------------------------
            // PATH: ADVICE (Where to find items)
            // ------------------------------------------
            case 70:
                options(c, 70, "Where do I find flour?", "How about milk?", "And eggs?", "I've got all the info I need.");
                break;

            case 71: // Flour advice
                npc(c, "Cook", Anim.CALM_1, "There is a Mill fairly close, go North and then West.", 
                    "Mill Lane Mill is just off the road to Draynor.");
                c.nextChat = c.getItems().playerHasItem(EMPTY_POT) ? 72 : 73;
                break;

            case 72: // Has pot
                npc(c, "Cook", Anim.CALM_1, "Talk to Millie, she'll help. Make sure you take", "a pot with you. You've got one already!");
                c.nextChat = 70;
                break;

            case 73: // No pot
                npc(c, "Cook", Anim.CALM_1, "Talk to Millie, she'll help. Make sure you take", "a pot. There should be one on the table here.");
                c.nextChat = 70;
                break;

            case 74: // Milk advice
                npc(c, "Cook", Anim.CALM_1, "There is a cattle field on the other side of the river,", "just across the road from the Groats' Farm.");
                c.nextChat = c.getItems().playerHasItem(EMPTY_BUCKET) ? 75 : 76;
                break;

            case 75: // Has bucket
                npc(c, "Cook", Anim.CALM_1, "You'll need a bucket for the milk. Luckily,", "I see you have one with you already!");
                c.nextChat = 70;
                break;

            case 76: // No bucket
                npc(c, "Cook", Anim.CALM_1, "You'll need a bucket. The general store north", "of the castle will sell you one.");
                c.nextChat = 70;
                break;

            case 77: // Egg advice
                npc(c, "Cook", Anim.CALM_1, "I normally get my eggs from the Groats' farm,", "on the other side of the river. Any chicken", "should lay eggs.");
                c.nextChat = 70;
                break;

            // ------------------------------------------
            // CHECK PROGRESS (Started Stage)
            // ------------------------------------------
            case 80:
                if (hasAllItems(c)) {
                    c.nextChat = 60; // Go to completion
                } else {
                    options(c, 80, "I'm still looking for them.", "Where do I find these things again?");
                }
                break;
        }
    }

    private boolean hasAllItems(Player c) {
        return c.getItems().playerHasItem(EGG) && 
               c.getItems().playerHasItem(MILK) && 
               c.getItems().playerHasItem(FLOUR);
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 51) {
            if (buttonId == OPT3_FIRST) next(c, 52); // What's wrong?
            // Add "Nice hat" (stage 50+) or "Not happy" here if you want those paths
        } else if (c.dialogueAction == 54) {
            if (buttonId == OPT2_FIRST) next(c, 55); // Yes
            else if (buttonId == OPT2_SECOND) {
                npc(c, "Cook", Anim.ANNOYED, "Fine. I always knew you adventurers were callous!");
                c.nextChat = 0;
            }
        } else if (c.dialogueAction == 70) {
            if (buttonId == OPT4_FIRST) next(c, 71); // Flour
            else if (buttonId == OPT4_SECOND) next(c, 74); // Milk
            else if (buttonId == OPT4_THIRD) next(c, 77); // Eggs
            else if (buttonId == OPT4_FOURTH) {
                player(c, Anim.CALM_1, "I've got all the information I need. Thanks.");
                c.nextChat = 0;
            }
        } else if (c.dialogueAction == 80) {
            if (buttonId == OPT2_FIRST) {
                player(c, Anim.SAD, "I haven't got any of them yet, I'm still looking.");
                c.nextChat = 0;
            } else if (buttonId == OPT2_SECOND) {
                next(c, 70); // Show help menu
            }
        }
    }
}