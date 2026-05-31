package server.model.players.packets.dialogue.npc.areas.kandarin.quests.MerlinsCrystal;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;
import server.model.players.quests.QuestAssistant;

public class MorganLeFaye extends NPCDialogue {

    public MorganLeFaye(Player c) {
        super(c);
    }
    @Override
    public int[] getNPCIDs() {
        return new int[] { 4320, 3528 };
    }
    @Override
    public String getDialogueRange() {
        return "85900-85999";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {

        switch (startDialogueId) {
            // --- Entry Point ---
            case 85900:
                npc(c, "Morgan Le Faye", Anim.SAD, "STOP! Please... spare my son.");
                c.nextChat = 85901;
                break;
            case 85901:
                options(c, 85901, "Tell me how to untrap Merlin and I might.", "No. He deserves to die.");
                break;

            // ==========================================
            // PATH 1: KILL HIM (Fails sequence)
            // ==========================================
            case 85910:
                player(c, Anim.ANGRY, "No. He deserves to die.");
                c.nextChat = 85911;
                break;
            case 85911:
                c.getDH().sendStatement("You kill Mordred.", "Morgan Le Faye vanishes.");
                c.nextChat = -1;
                // TODO: Delete the spawned Morgan Le Faye NPC here,
                // and allow the player to kill Mordred normally next time.
                break;

            // ==========================================
            // PATH 2: SPARE HIM (Quest Progress)
            // ==========================================
            case 85920:
                player(c, Anim.CALM_1, "Tell me how to untrap Merlin and I might.");
                c.nextChat = 85921;
                break;
            case 85921:
                npc(c, "Morgan Le Faye", Anim.SAD, "You have guessed correctly that I'm responsible for that.", "I suppose I can live with that fool Merlin being loose", "for the sake of my son.");
                c.nextChat = 85922;
                break;
            case 85922:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "Setting him free won't be easy though.");
                c.nextChat = 85923;
                break;
            case 85923:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "You will need to find a magic symbol as close to the", "crystal as you can find.");
                c.nextChat = 85924;
                break;
            case 85924:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "You will then need to drop some bats' bones on the", "magic symbol while holding a lit black candle.");
                c.nextChat = 85925;
                break;
            case 85925:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "This will summon a mighty spirit named Thrantax.", "You will need to bind him with magic words.");
                c.nextChat = 85926;
                break;
            case 85926:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "Then you will need the sword Excalibur with which the", "spell was bound in order to shatter the crystal.");
                c.nextChat = 85927;
                break;

            // Loopable Options Hub
            case 85927:
                options3(c, 85927, "So where can I find Excalibur?", "What are the magic words?", "OK I will do all that.");
                break;

            // Option: Excalibur
            case 85930:
                player(c, Anim.THINKING, "So where can I find Excalibur?");
                c.nextChat = 85931;
                break;
            case 85931:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "The lady of the lake has it. I don't know if she'll give it", "to you though, she can be rather temperamental.");
                c.nextChat = 85927; // Loop back
                break;

            // Option: Magic Words
            case 85940:
                player(c, Anim.THINKING, "What are the magic words?");
                c.nextChat = 85941;
                break;
            case 85941:
                npc(c, "Morgan Le Faye", Anim.CALM_1, "You will find the magic words at the base of one of the", "chaos altars.", "Which chaos altar I cannot remember.");
                c.nextChat = 85927; // Loop back
                break;

            // Option: Accept & Advance Quest
            case 85950:
                player(c, Anim.CALM_1, "Ok, I will go do all that.");
                c.nextChat = 85951;
                break;
            case 85951:
                c.getDH().sendStatement("Morgan Le Faye vanishes.");
                c.nextChat = -1;

                // Advance Quest
                if (c.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.DEFEATED_MORDRED) {
                    c.questStages[MerlinsCrystal.QUEST_ID] = MerlinsCrystal.GATHERING_ITEMS;
                    QuestAssistant.sendStages(c);
                }

                // TODO: Delete the spawned Morgan Le Faye NPC from the world here.
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        switch (c.dialogueAction) {
            case 85901:
                if (buttonId == OPT2_FIRST) next(c, 85920); // Spare
                if (buttonId == OPT2_SECOND) next(c, 85910); // Kill
                break;
            case 85927:
                if (buttonId == OPT3_FIRST) next(c, 85930); // Excalibur
                if (buttonId == OPT3_SECOND) next(c, 85940); // Words
                if (buttonId == OPT3_THIRD) next(c, 85950); // Finish
                break;
        }
    }
}