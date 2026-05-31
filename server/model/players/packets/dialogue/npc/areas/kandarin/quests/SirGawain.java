package server.model.players.packets.dialogue.npc.areas.kandarin.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;
import server.model.players.quests.QuestAssistant;

public class SirGawain extends NPCDialogue {

    public SirGawain(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3520; // Sir Gawain
    }

    @Override
    public String getDialogueRange() {
        return "85300-85399";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];
        // TODO: Replace with actual Holy Grail quest completion variable later
        boolean holyGrailDone = false;

        String sirMadam = c.playerAppearance[0] == 0 ? "sir" : "madam";

        switch (startDialogueId) {

            // --- Entry Point ---
            case 85300:
                npc(c, "Sir Gawain", Anim.CALM_1, "Good day to you " + sirMadam + "!");

                // Route options based on quest progress
                if (merlinsCrystalStage == MerlinsCrystal.NOT_STARTED) {
                    c.nextChat = 85301;
                } else if (merlinsCrystalStage == MerlinsCrystal.INVESTIGATING || merlinsCrystalStage == MerlinsCrystal.INFILTRATING_KEEP) {
                    c.nextChat = 85310;
                } else if (merlinsCrystalStage >= MerlinsCrystal.GATHERING_ITEMS && merlinsCrystalStage < MerlinsCrystal.COMPLETED) {
                    c.nextChat = 85320;
                } else if (merlinsCrystalStage == MerlinsCrystal.COMPLETED) {
                    c.nextChat = holyGrailDone ? 85330 : 85301;
                } else {
                    c.nextChat = 85301; // Fallback
                }
                break;

            // ==========================================
            // PRE-QUEST / STANDARD MENU
            // ==========================================
            case 85301:
                options(c, 85301, "Good day.", "Know you of any quests sir knight?");
                break;
            case 85302:
                player(c, Anim.CALM_1, "Good day.");
                c.nextChat = -1;
                break;
            case 85303:
                player(c, Anim.THINKING, "Know you of any quests sir knight?");
                c.nextChat = 85304;
                break;
            case 85304:
                npc(c, "Sir Gawain", Anim.CALM_1, "The king is the man to talk to if you want a quest.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: INVESTIGATING
            // ==========================================
            case 85310:
                options(c, 85310, "Good day.", "Any ideas on how to get Merlin out of that crystal?");
                break;
            case 85311:
                player(c, Anim.THINKING, "Any ideas on how to get Merlin out of that crystal?");
                c.nextChat = 85312;
                break;
            case 85312:
                npc(c, "Sir Gawain", Anim.SAD, "I'm a little stumped myself. We've tried opening it with", "anything and everything!");
                c.nextChat = 85313;
                break;
            case 85313:
                player(c, Anim.THINKING, "Do you know how Merlin got trapped?");
                c.nextChat = 85314;
                break;
            case 85314:
                npc(c, "Sir Gawain", Anim.ANGRY, "I would guess this is the work of the evil Morgan Le Faye!");
                c.nextChat = 85315;
                break;
            case 85315:
                player(c, Anim.THINKING, "And where could I find her?");
                c.nextChat = 85316;
                break;
            case 85316:
                npc(c, "Sir Gawain", Anim.CALM_1, "She lives in her stronghold to the south of here, guarded", "by some renegade knights led by Sir Mordred.");
                c.nextChat = 85317;
                break;
            case 85317:
                options(c, 85317, "Any idea how to get into Morgan Le Faye's stronghold?", "Thank you for the information.");
                break;
            case 85318:
                player(c, Anim.THINKING, "Any idea how to get into Morgan Le Faye's stronghold?");
                c.nextChat = 85319;

                // Advance quest stage if they just discovered Morgan Le Faye!
                if (c.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.INVESTIGATING) {
                    c.questStages[MerlinsCrystal.QUEST_ID] = MerlinsCrystal.INFILTRATING_KEEP;
                    QuestAssistant.sendStages(c);
                }
                break;
            case 85319:
                npc(c, "Sir Gawain", Anim.SAD, "No, you've got me stumped there...");
                c.nextChat = -1;
                break;
            case 85305:
                player(c, Anim.HAPPY, "Thank you for the information.");
                c.nextChat = 85306;
                break;
            case 85306:
                npc(c, "Sir Gawain", Anim.CALM_1, "It is the least I can do.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: SEARCHING FOR EXCALIBUR
            // ==========================================
            case 85320:
                player(c, Anim.THINKING, "Any ideas on finding Excalibur?");
                c.nextChat = 85321;
                break;
            case 85321:
                npc(c, "Sir Gawain", Anim.SAD, "Unfortunately not, adventurer.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-MERLIN / POST-HOLY GRAIL
            // ==========================================
            case 85330:
                options(c, 85330, "Good day.", "Know you of any quests sir knight?");
                break;
            case 85331:
                player(c, Anim.THINKING, "Know you of any quests sir knight?");
                c.nextChat = 85332;
                break;
            case 85332:
                npc(c, "Sir Gawain", Anim.CALM_1, "I think you've done the main quest we were on right now...");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        switch (c.dialogueAction) {
            case 85301: // Pre-quest menu
                if (buttonId == OPT2_FIRST) next(c, 85302);
                if (buttonId == OPT2_SECOND) next(c, 85303);
                break;
            case 85310: // Investigating menu
                if (buttonId == OPT2_FIRST) next(c, 85302);
                if (buttonId == OPT2_SECOND) next(c, 85311);
                break;
            case 85317: // Stronghold question
                if (buttonId == OPT2_FIRST) next(c, 85318);
                if (buttonId == OPT2_SECOND) next(c, 85305);
                break;
            case 85330: // Post-quest menu
                if (buttonId == OPT2_FIRST) next(c, 85302);
                if (buttonId == OPT2_SECOND) next(c, 85331);
                break;
        }
    }
}