package server.model.players.packets.dialogue.npc.areas.kandarin.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;

public class LadyOfTheLake extends NPCDialogue {

    public LadyOfTheLake(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3530; // Lady of the Lake
    }

    @Override
    public String getDialogueRange() {
        return "85700-85799";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        String sirMadam = c.playerAppearance[0] == 0 ? "sir" : "madam";
        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];

        switch (startDialogueId) {
            case 85700:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "Good day to you " + sirMadam + ".");
                c.nextChat = 85701;
                break;
            case 85701:
                if (merlinsCrystalStage >= MerlinsCrystal.GATHERING_ITEMS && merlinsCrystalStage < MerlinsCrystal.COMPLETED) {
                    options3(c, 85701, "Who are you?", "Good day.", "I seek the sword Excalibur.");
                } else if (merlinsCrystalStage == MerlinsCrystal.COMPLETED && !c.getItems().playerHasItem(MerlinsCrystal.EXCALIBUR) && !c.getItems().bankContains(MerlinsCrystal.EXCALIBUR)) {
                    // Completed but lost Excalibur
                    options3(c, 85701, "Who are you?", "Good day.", "I seek the sword Excalibur.");
                } else {
                    options(c, 85701, "Who are you?", "Good day.");
                }
                break;

            case 85710:
                player(c, Anim.CONFUSED, "Who are you?");
                c.nextChat = 85711;
                break;
            case 85711:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "I am the Lady of the Lake.");
                c.nextChat = -1;
                break;

            case 85720:
                player(c, Anim.CALM_1, "Good day.");
                c.nextChat = -1;
                break;

            // ==========================================
            // QUEST ACTIVE: SEEKING EXCALIBUR
            // ==========================================
            case 85730:
                player(c, Anim.CALM_1, "I seek the sword Excalibur.");

                if (merlinsCrystalStage == MerlinsCrystal.COMPLETED) {
                    c.nextChat = 85750; // Re-claim lost Excalibur
                } else {
                    c.nextChat = 85731; // Active Quest
                }
                break;
            case 85731:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "Aye, I have that artefact in my possession.", "'Tis very valuable, and not an artefact to be given", "away lightly.");
                c.nextChat = 85732;
                break;
            case 85732:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "I would want to give it away only to one who is", "worthy and good.");
                c.nextChat = 85733;
                break;
            case 85733:
                player(c, Anim.CONFUSED, "And how am I meant to prove that?");
                c.nextChat = 85734;
                break;
            case 85734:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "I shall set a test for you.", "First I need you to travel to Port Sarim. Then go to the", "upstairs room of the jeweller's shop there.");
                c.nextChat = 85735;
                break;
            case 85735:
                player(c, Anim.CALM_1, "Ok. That seems easy enough.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST QUEST: RE-CLAIMING EXCALIBUR
            // ==========================================
            case 85750:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "Aye, I have that artefact in my possession.", "'Tis very valuable, and not an artefact to be given", "away lightly.");
                c.nextChat = 85751;
                break;
            case 85751:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "...But you have already proved thyself to be worthy of", "wielding it once already. I shall return it to you if you", "can prove yourself to still be worthy.");
                c.nextChat = 85752;
                break;
            case 85752:
                player(c, Anim.CONFUSED, "...And how can I do that?");
                c.nextChat = 85753;
                break;
            case 85753:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "Why, by proving yourself to be above material goods.", "500 gold coins ought to do it.");
                c.nextChat = 85754;
                break;
            case 85754:
                if (c.getItems().playerHasItem(995, 500)) {
                    c.nextChat = 85755;
                } else {
                    c.nextChat = 85760;
                }
                break;

            case 85755:
                player(c, Anim.CALM_1, "Ok, here you go.");
                c.nextChat = 85756;
                break;
            case 85756:
                c.getItems().deleteItem(995, 500);
                c.getItems().addItem(MerlinsCrystal.EXCALIBUR, 1);
                npc(c, "The Lady of the Lake", Anim.HAPPY, "You are still worthy to wield Excalibur! And thanks for", "the cash! I felt like getting a new haircut!");
                c.nextChat = -1;
                break;

            case 85760:
                player(c, Anim.SAD, "I don't have that kind of money...");
                c.nextChat = 85761;
                break;
            case 85761:
                npc(c, "The Lady of the Lake", Anim.CALM_1, "Well, come back when you do.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 85701) {
            if (buttonId == OPT3_FIRST || buttonId == OPT2_FIRST) next(c, 85710);
            if (buttonId == OPT3_SECOND || buttonId == OPT2_SECOND) next(c, 85720);
            if (buttonId == OPT3_THIRD) next(c, 85730);
        }
    }
}