package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.VampyreSlayer;

public class DrHarlow extends NPCDialogue {

    public DrHarlow(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 756 }; 
    }
    @Override
 	public String getDialogueRange() {
 		return "9500-9542";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int stage = c.questStages[VampyreSlayer.QUEST_ID];

        switch (startDialogueId) {
            case 9500:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Buy me a drrink pleassh...");
                c.nextChat = 9501;
                break;

            case 9501:
                if (stage == VampyreSlayer.SPOKE_TO_MORGAN) {
                    options(c, 9501, "No, you've had enough.", "Morgan needs your help!");
                } else if (stage == VampyreSlayer.GATHERING_EQUIPMENT && !c.getItems().playerHasItem(VampyreSlayer.STAKE) && !c.getItems().bankContains(VampyreSlayer.STAKE)) {
                    // Let the player ask for a new stake if they lost theirs
                    options(c, 9501, "No, you've had enough.", "I lost my stake, can I have another?");
                } else {
                    options(c, 9501, "No, you've had enough.", "Actually, I think I will buy you a beer."); // Generic filler option
                }
                break;

            case 9502:
                player(c, Anim.ANGRY, "No, you've had enough.");
                c.nextChat = 0;
                break;
                
            // ==========================================
            // QUEST: ASKING FOR HELP (STAGE 1)
            // ==========================================
            case 9510:
                player(c, Anim.CALM_1, "Morgan needs your help!");
                c.nextChat = 9511;
                break;
            case 9511:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Morgan you shhay..?");
                c.nextChat = 9512;
                break;
            case 9512:
                player(c, Anim.CALM_1, "His village is being terrorised by a vampyre! He told", "me to ask you about how I can stop it.");
                c.nextChat = 9513;
                break;
            case 9513:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Buy me a beer... then I'll teash you what", "you need to know...");
                c.nextChat = 9514;
                break;
            case 9514:
                player(c, Anim.ANNOYED, "But this is your friend Morgan we're talking about!");
                c.nextChat = 9515;
                break;
            case 9515:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Buy ush a drink anyway...");
                c.nextChat = 9516;
                break;
            case 9516:
                if (c.getItems().playerHasItem(VampyreSlayer.BEER)) {
                    player(c, Anim.CALM_1, "Here you go.");
                    c.nextChat = 9518;
                } else {
                    player(c, Anim.SAD, "I'll just go and buy one.");
                    c.nextChat = 0;
                }
                break;

            case 9518:
                c.getItems().deleteItem(VampyreSlayer.BEER, 1);
                item("You give a beer to Dr Harlow.", c, VampyreSlayer.BEER, "");
                c.nextChat = 9519;
                break;
            case 9519:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Cheersh matey...");
                c.nextChat = 9520;
                break;
            case 9520:
                player(c, Anim.CALM_1, "So tell me how to kill vampyres then.");
                c.nextChat = 9521;
                break;
            case 9521:
                npc(c, "Dr Harlow", Anim.CALM_1, "Yesh Yesh vampyres, I was very good at killing em once...");
                c.nextChat = 9522;
                break;
            case 9522:
                c.getDH().sendStatement("Dr Harlow appears to sober up slightly.");
                c.nextChat = 9523;
                break;
            case 9523:
                if (c.getItems().playerHasItem(VampyreSlayer.STAKE)) {
                    npc(c, "Dr Harlow", Anim.CALM_1, "Don't forget to take your stake with you, otherwise", "he'll just regenerate. Yes, you must have a stake", "to finish it off... I'd give you a stake but", "you've got one in your inventory.");
                    c.nextChat = 9526;
                } else if (c.getItems().bankContains(VampyreSlayer.STAKE)) {
                    npc(c, "Dr Harlow", Anim.CALM_1, "Don't forget to take your stake with you, otherwise", "he'll just regenerate. Yes, you must have a stake", "to finish it off... I'd give you a stake but", "you've already got one stored somewhere.");
                    c.nextChat = 9526;
                } else {
                    npc(c, "Dr Harlow", Anim.CALM_1, "Well, you're going to need a stake, otherwise he'll", "just regenerate. Yes, you must have a stake to", "finish it off... I just happen to have one with me.");
                    c.nextChat = 9524;
                }
                break;
            case 9524:
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(VampyreSlayer.STAKE, 1);
                    item("Dr Harlow hands you a stake.", c, VampyreSlayer.STAKE, "");
                    c.nextChat = 9526;
                } else {
                    npc(c, "Dr Harlow", Anim.ANNOYED, "You don't have enough room in your inventory", "for this stake. Make some space and come back!");
                    c.nextChat = 0;
                }
                break;
            case 9526:
                npc(c, "Dr Harlow", Anim.CALM_1, "You'll need a hammer as well, to drive it in properly,", "your everyday general store hammer will do. One last", "thing... It's wise to carry garlic with you, vampyres are", "somewhat weakened if they can smell garlic. Morgan");
                c.nextChat = 9527;
                break;
            case 9527:
                npc(c, "Dr Harlow", Anim.CALM_1, "always liked garlic, you should try his house. But", "remember, a vampyre is still a dangerous foe!");
                c.nextChat = 9528;
                break;
            case 9528:
                player(c, Anim.HAPPY, "Thank you very much!");
                if (c.questStages[VampyreSlayer.QUEST_ID] == VampyreSlayer.SPOKE_TO_MORGAN) {
                    new VampyreSlayer(c).setStage(VampyreSlayer.GATHERING_EQUIPMENT);
                }
                c.nextChat = 0;
                break;

            // ==========================================
            // RECLAIM LOST STAKE (EXTRA LOGIC)
            // ==========================================
            case 9530:
                player(c, Anim.SAD, "I lost my stake, can I have another?");
                c.nextChat = 9531;
                break;
            case 9531:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Buy me anuzzer beer first...");
                c.nextChat = 9516; // Loops back to the beer checking logic, which cleanly flows to handing a stake out!
                break;
                
            // ==========================================
            // GENERIC (NO QUEST ACTIVE)
            // ==========================================
            case 9540:
                player(c, Anim.CALM_1, "Actually, I think I will buy you a beer.");
                c.nextChat = 9541;
                break;
            case 9541:
                if (c.getItems().playerHasItem(VampyreSlayer.BEER)) {
                    c.getItems().deleteItem(VampyreSlayer.BEER, 1);
                    item("You give a beer to Dr Harlow.", c, VampyreSlayer.BEER, "");
                    c.nextChat = 9542;
                } else {
                    player(c, Anim.SAD, "Oh wait, I don't have one on me.");
                    c.nextChat = 0;
                }
                break;
            case 9542:
                npc(c, "Dr Harlow", Anim.DISORIENTED, "Cheersh matey...");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9501) {
            if (buttonId == OPT2_FIRST) next(c, 9502);
            else if (buttonId == OPT2_SECOND) {
                int stage = c.questStages[VampyreSlayer.QUEST_ID];
                if (stage == VampyreSlayer.SPOKE_TO_MORGAN) {
                    next(c, 9510);
                } else if (stage == VampyreSlayer.GATHERING_EQUIPMENT && !c.getItems().playerHasItem(VampyreSlayer.STAKE) && !c.getItems().bankContains(VampyreSlayer.STAKE)) {
                    next(c, 9530);
                } else {
                    next(c, 9540);
                }
            }
        }
    }
}