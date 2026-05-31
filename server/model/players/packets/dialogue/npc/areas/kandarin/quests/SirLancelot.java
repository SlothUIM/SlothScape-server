package server.model.players.packets.dialogue.npc.areas.kandarin.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;

public class SirLancelot extends NPCDialogue {

    public SirLancelot(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3519; // Sir Lancelot
    }

    @Override
    public String getDialogueRange() {
        return "85400-85499";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];
        // TODO: Replace with actual King's Ransom quest completion variable later
        boolean kingsRansomDone = false;

        switch (startDialogueId) {

            // --- Entry Point ---
            case 85400:
                npc(c, "Sir Lancelot", Anim.CALM_1, "Greetings! I am Sir Lancelot, the greatest Knight in the", "land! What do you want?");

                // Route based on quest state
                if (kingsRansomDone) {
                    c.nextChat = 85450;
                } else if (merlinsCrystalStage == MerlinsCrystal.COMPLETED) {
                    c.nextChat = 85440;
                } else if (merlinsCrystalStage == MerlinsCrystal.INVESTIGATING) {
                    c.nextChat = 85420;
                } else if (merlinsCrystalStage == MerlinsCrystal.INFILTRATING_KEEP) {
                    c.nextChat = 85430;
                } else {
                    c.nextChat = 85401; // Not started / General Fallback
                }
                break;

            // ==========================================
            // PRE-QUEST / STANDARD MENU
            // ==========================================
            case 85401:
                options(c, 85401, "You're a little full of yourself aren't you?", "I seek a quest!");
                break;

            case 85402:
                player(c, Anim.THINKING, "You're a little full of yourself aren't you?");
                c.nextChat = 85403;
                break;
            case 85403:
                npc(c, "Sir Lancelot", Anim.HAPPY, "I have every right to be proud of myself.");
                c.nextChat = 85404;
                break;
            case 85404:
                npc(c, "Sir Lancelot", Anim.HAPPY, "My prowess in battle is world renowned!");
                c.nextChat = -1;
                break;

            case 85405:
                player(c, Anim.HAPPY, "I seek a quest!");
                c.nextChat = 85406;
                break;
            case 85406:
                npc(c, "Sir Lancelot", Anim.CALM_1, "Leave questing to the professionals.");
                c.nextChat = 85407;
                break;
            case 85407:
                npc(c, "Sir Lancelot", Anim.HAPPY, "Such as myself.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: INVESTIGATING
            // ==========================================
            case 85420:
                options3(c, 85420,
                        "You're a little full of yourself aren't you?",
                        "I seek a quest!",
                        "I want to get Merlin out of the crystal.");
                break;
            case 85421:
                player(c, Anim.CALM_1, "I want to get Merlin out of the crystal.");
                c.nextChat = 85422;
                break;
            case 85422:
                npc(c, "Sir Lancelot", Anim.CALM_1, "Well, if the Knights of the Round Table can't manage it,", "I can't see how a commoner like you could succeed", "where we have failed.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: INFILTRATING KEEP
            // ==========================================
            case 85430:
                options3(c, 85430,
                        "You're a little full of yourself aren't you?",
                        "I seek a quest!",
                        "Any ideas on how to get into Morgan Le Faye's stronghold?");
                break;
            case 85431:
                player(c, Anim.THINKING, "Any ideas on how to get into Morgan Le Faye's stronghold?");
                c.nextChat = 85432;
                break;
            case 85432:
                npc(c, "Sir Lancelot", Anim.CALM_1, "That stronghold is built in a strong defensive position.", "It's on a big rock sticking out into the sea.");
                c.nextChat = 85433;
                break;
            case 85433:
                npc(c, "Sir Lancelot", Anim.CALM_1, "There are two ways in that I know of, the large heavy", "front doors, and the sea entrance, only penetrable by", "boat. They get all their deliveries by boat from Catherby.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-MERLIN'S CRYSTAL
            // ==========================================
            case 85440:
                npc(c, "Sir Lancelot", Anim.HAPPY, "Sir Knight! Many thanks for your assistance in restoring", "Merlin to his former freedom!");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-KING'S RANSOM
            // ==========================================
            case 85450:
                npc(c, "Sir Lancelot", Anim.SAD, "Humph. You are indeed a better knight than I first", "suspected.");
                c.nextChat = 85451;
                break;
            case 85451:
                player(c, Anim.HAPPY, "Gee, it was nothing saving you all from the clutches of", "Morgan Le Faye and freeing your King.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        switch (c.dialogueAction) {
            case 85401: // Standard Pre-Quest Menu
                if (buttonId == OPT2_FIRST) next(c, 85402);
                if (buttonId == OPT2_SECOND) next(c, 85405);
                break;
            case 85420: // Investigating Menu
                if (buttonId == OPT3_FIRST) next(c, 85402);
                if (buttonId == OPT3_SECOND) next(c, 85405);
                if (buttonId == OPT3_THIRD) next(c, 85421); // Merlin question
                break;
            case 85430: // Infiltrating Menu
                if (buttonId == OPT3_FIRST) next(c, 85402);
                if (buttonId == OPT3_SECOND) next(c, 85405);
                if (buttonId == OPT3_THIRD) next(c, 85431); // Stronghold question
                break;
        }
    }
}