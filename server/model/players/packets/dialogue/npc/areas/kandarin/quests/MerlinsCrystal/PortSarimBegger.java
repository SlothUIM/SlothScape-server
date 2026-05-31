package server.model.players.packets.dialogue.npc.areas.kandarin.quests.MerlinsCrystal;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;

public class PortSarimBegger extends NPCDialogue {

    public PortSarimBegger(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3532; // Beggar in Port Sarim
    }

    @Override
    public String getDialogueRange() {
        return "85800-85899";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];

        switch (startDialogueId) {
            case 85800:
                // Normal Beggar vs Quest Beggar
                if (merlinsCrystalStage >= MerlinsCrystal.GATHERING_ITEMS && merlinsCrystalStage < MerlinsCrystal.COMPLETED && !c.getItems().playerHasItem(MerlinsCrystal.EXCALIBUR)) {
                    npc(c, "Beggar", Anim.SAD, "Please, stranger... my family and I are starving...", "Could you find it in your heart to spare me a simple", "loaf of bread?");
                    c.nextChat = 85801;
                } else {
                    npc(c, "Beggar", Anim.SAD, "Spare some change gov'ner?");
                    c.nextChat = -1;
                }
                break;

            case 85801:
                options(c, 85801, "Yes certainly.", "No I don't have any bread with me.");
                break;

            case 85810: // Yes
                if (c.getItems().playerHasItem(MerlinsCrystal.BREAD)) {
                    player(c, Anim.HAPPY, "Yes, certainly.");
                    c.nextChat = 85811;
                } else {
                    player(c, Anim.SAD, "Yes, certainly.", "... except I don't have any bread on me at the moment...");
                    c.nextChat = 85821;
                }
                break;

            case 85811:
                c.getItems().deleteItem(MerlinsCrystal.BREAD, 1);
                c.getDH().sendStatement("You give the bread to the beggar.");
                c.nextChat = 85812;
                break;
            case 85812:
                npc(c, "Beggar", Anim.HAPPY, "Thank you very much!");
                c.nextChat = 85813;
                break;
            case 85813:
                c.getDH().sendStatement("The beggar has turned into the Lady of the Lake!");
                c.nextChat = 85814;
                break;
            case 85814:
                // Temporarily morph the dialogue name to Lady of the Lake
                npc(c, "The Lady of the Lake", Anim.HAPPY, "Well done. You have passed my test.", "Here is Excalibur, guard it well.");
                c.getItems().addItem(MerlinsCrystal.EXCALIBUR, 1);
                c.nextChat = -1;
                break;

            case 85820: // No
                player(c, Anim.SAD, "No, I don't have any bread with me.");
                c.nextChat = 85821;
                break;
            case 85821:
                npc(c, "Beggar", Anim.CALM_1, "Well, if you get some, you know where to come.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 85801) {
            if (buttonId == OPT2_FIRST) next(c, 85810);
            if (buttonId == OPT2_SECOND) next(c, 85820);
        }
    }
}