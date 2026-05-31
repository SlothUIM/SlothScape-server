package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.WitchsPotion;

public class Hetty extends NPCDialogue {

    public Hetty(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 307 }; // Standard 317 ID for Hetty
    }
    @Override
	public String getDialogueRange() {
		return "9300-9352";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {

            case 9300:
                int stage = c.questStages[WitchsPotion.QUEST_ID];
                if (stage == WitchsPotion.COMPLETED) {
                    c.nextChat = 9350;
                } else if (stage == WitchsPotion.DRINK_FROM_CAULDRON) {
                    c.nextChat = 9340;
                } else if (stage == WitchsPotion.GATHERING_ITEMS) {
                    c.nextChat = 9320;
                } else {
                    npc(c, "Hetty", Anim.CONFUSED, "What could you want with an old woman like me?");
                    c.nextChat = 9301;
                }
                break;

            // ==========================================
            // PRE-QUEST / NOT STARTED
            // ==========================================
            case 9301:
                options(c, 9301, "I am in search of a quest.", "I've heard that you are a witch.");
                break;
                
            case 9302:
                player(c, Anim.CALM_1, "I've heard that you are a witch.");
                c.nextChat = 9303;
                break;
            case 9303:
                npc(c, "Hetty", Anim.CALM_1, "Yes it does seem to be getting fairly common knowledge.", "I fear I may be getting a visit from the witch", "hunters of Falador before long.");
                c.nextChat = 9304;
                break;
            case 9304:
                options(c, 9304, "I am in search of a quest.", "Goodbye.");
                break;
            case 9305:
                player(c, Anim.CALM_1, "Goodbye.");
                c.nextChat = 0;
                break;

            case 9306:
                player(c, Anim.CALM_1, "I am in search of a quest.");
                c.nextChat = 9307;
                break;
            case 9307:
                npc(c, "Hetty", Anim.CALM_1, "Hmmm... Maybe I can think of something for you.", "Would you like to become more proficient in the dark arts?");
                c.nextChat = 9308;
                break;
            case 9308:
                options(c, 9308, "Start the Witch's Potion quest?", "Yes.", "No.");
                break;
            case 9309:
                player(c, Anim.CALM_1, "No, I have my principles and honour.");
                c.nextChat = 9310;
                break;
            case 9310:
                npc(c, "Hetty", Anim.CALM_1, "Suit yourself, but you're missing out.");
                c.nextChat = 0;
                break;
            case 9311:
                player(c, Anim.HAPPY, "Yes, help me become one with my darker side.");
                c.nextChat = 9312;
                break;
            case 9312:
                npc(c, "Hetty", Anim.CALM_1, "Okay, I'm going to make a potion to help bring out", "your darker self.", "You will need certain ingredients.");
                c.nextChat = 9313;
                break;
            case 9313:
                player(c, Anim.CONFUSED, "What do I need?");
                c.nextChat = 9314;
                break;
            case 9314:
                npc(c, "Hetty", Anim.CALM_1, "You need an eye of newt, a rat's tail, an onion...", "Oh and a piece of burnt meat.");
                c.nextChat = 9315;
                break;
            case 9315:
                player(c, Anim.HAPPY, "Great, I'll go and get them.");
                new WitchsPotion(c).setStage(WitchsPotion.GATHERING_ITEMS);
                c.nextChat = 0;
                break;

            // ==========================================
            // GATHERING ITEMS
            // ==========================================
            case 9320:
                player(c, Anim.CALM_1, "I've been looking for those ingredients.");
                c.nextChat = 9321;
                break;
            case 9321:
                npc(c, "Hetty", Anim.CALM_1, "So what have you found so far?");
                c.nextChat = 9322;
                break;
            case 9322:
                boolean hasTail = c.getItems().playerHasItem(WitchsPotion.RATS_TAIL);
                boolean hasMeat = c.getItems().playerHasItem(WitchsPotion.BURNT_MEAT);
                boolean hasOnion = c.getItems().playerHasItem(WitchsPotion.ONION);
                boolean hasNewt = c.getItems().playerHasItem(WitchsPotion.EYE_OF_NEWT);
                
                if (hasTail && hasMeat && hasOnion && hasNewt) {
                    player(c, Anim.HAPPY, "In fact I have everything!");
                    c.nextChat = 9330;
                } else if (!hasTail && !hasMeat && !hasOnion && !hasNewt) {
                    player(c, Anim.SAD, "I'm afraid I don't have any of them yet.");
                    c.nextChat = 9323;
                } else {
                    String line1 = (hasTail ? "I have the rat's tail (ewww), " : "I don't have a rat's tail, ") +
                                   (hasMeat ? "I have the burnt meat," : "I don't have any burnt meat,");
                    String line2 = (hasOnion ? "I have an onion, " : "I don't have an onion, ") +
                                   (hasNewt ? "and I have the eye of newt, yum!" : "and I don't have an eye of newt.");
                    
                    player(c, Anim.CALM_1, line1, line2);
                    c.nextChat = 9324;
                }
                break;
            case 9323:
                npc(c, "Hetty", Anim.ANNOYED, "Well I can't make the potion without them! Remember...", "You need an eye of newt, a rat's tail, an onion,", "and a piece of burnt meat. Off you go dear!");
                c.nextChat = 0;
                break;
            case 9324:
                npc(c, "Hetty", Anim.HAPPY, "Great, but I'll need the other ingredients as well.");
                c.nextChat = 0;
                break;

            // ==========================================
            // HANDING IN ITEMS
            // ==========================================
            case 9330:
                npc(c, "Hetty", Anim.HAPPY, "Excellent, can I have them then?");
                c.nextChat = 9331;
                break;
            case 9331:
                c.getItems().deleteItem(WitchsPotion.RATS_TAIL, 1);
                c.getItems().deleteItem(WitchsPotion.BURNT_MEAT, 1);
                c.getItems().deleteItem(WitchsPotion.ONION, 1);
                c.getItems().deleteItem(WitchsPotion.EYE_OF_NEWT, 1);
                
                c.getDH().sendStatement("You pass the ingredients to Hetty and she puts them all into her", "cauldron. Hetty closes her eyes and begins to chant. The", "cauldron bubbles mysteriously.");
                c.nextChat = 9332;
                break;
            case 9332:
                player(c, Anim.CONFUSED, "Well, is it ready?");
                c.nextChat = 9333;
                break;
            case 9333:
                npc(c, "Hetty", Anim.HAPPY, "Ok, now drink from the cauldron.");
                new WitchsPotion(c).setStage(WitchsPotion.DRINK_FROM_CAULDRON);
                c.nextChat = 0;
                break;

            // ==========================================
            // DRINK FROM CAULDRON REMINDER
            // ==========================================
            case 9340:
                npc(c, "Hetty", Anim.CONFUSED, "Well are you going to drink the potion or not?");
                c.nextChat = 9341;
                break;
            case 9341:
                player(c, Anim.CALM_1, "Yes, I will.");
                c.nextChat = 0;
                break;

            // ==========================================
            // COMPLETED
            // ==========================================
            case 9350:
                npc(c, "Hetty", Anim.HAPPY, "How's your magic coming along?");
                c.nextChat = 9351;
                break;
            case 9351:
                player(c, Anim.HAPPY, "I'm practicing and slowly getting better.");
                c.nextChat = 9352;
                break;
            case 9352:
                npc(c, "Hetty", Anim.HAPPY, "Good, good.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9301) {
            if (buttonId == OPT2_FIRST) next(c, 9306);
            else if (buttonId == OPT2_SECOND) next(c, 9302);
        } else if (c.dialogueAction == 9304) {
            if (buttonId == OPT2_FIRST) next(c, 9306);
            else if (buttonId == OPT2_SECOND) next(c, 9305);
        } else if (c.dialogueAction == 9308) {
            if (buttonId == OPT2_FIRST) next(c, 9311);
            else if (buttonId == OPT2_SECOND) next(c, 9309);
        }
    }
}