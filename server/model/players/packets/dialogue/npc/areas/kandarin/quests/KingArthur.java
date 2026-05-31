package server.model.players.packets.dialogue.npc.areas.kandarin.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;
import server.model.players.quests.QuestAssistant;

public class KingArthur extends NPCDialogue {

    public KingArthur(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3531; // King Arthur
    }

    @Override
    public String getDialogueRange() {
        return "85000-85100";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {

        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];
        // TODO: Replace these booleans with your actual quest tracking system when implemented
        boolean holyGrailDone = false;
        boolean kingsRansomDone = false;

        switch (startDialogueId) {
            // --- Entry Point ---
            case 85000:
                if (kingsRansomDone) {
                    next(c, 85100);
                } else if (holyGrailDone) {
                    next(c, 85090);
                } else if (merlinsCrystalStage == MerlinsCrystal.COMPLETED) {
                    next(c, 85050);
                } else {
                    next(c, 85001); // Works for both Not Started and In Progress
                }
                break;

            // ==========================================
            // STANDARD GREETING (Pre-Quest & Active Quest)
            // ==========================================
            case 85001:
                npc(c, "King Arthur", Anim.CALM_1, "Welcome to my court. I am King Arthur of the", "Knights of the Round Table.");
                c.nextChat = 85002;
                break;
            case 85002:
                options3(c, 85002,
                        "I want to become a Knight of the Round Table!",
                        "So what are you doing in Gielinor?",
                        "Thank you very much.");
                break;

            // Option 1: Become a Knight
            case 85010:
                player(c, Anim.HAPPY, "I want to become a Knight of the Round Table!");

                // If the quest is already active, jump to the active dialogue
                if (c.questStages[MerlinsCrystal.QUEST_ID] > MerlinsCrystal.NOT_STARTED && c.questStages[MerlinsCrystal.QUEST_ID] < MerlinsCrystal.COMPLETED) {
                    c.nextChat = 85015;
                } else {
                    c.nextChat = 85011;
                }
                break;

            // (Quest Active but Not Finished)
            case 85015:
                npc(c, "King Arthur", Anim.CALM_1, "Well then you must complete your quest to rescue Merlin.", "Talk to my knights if you need any help.");
                c.nextChat = -1;
                break;

            // (Quest Not Started)
            case 85011:
                npc(c, "King Arthur", Anim.CALM_1, "Really? Well then you will need to go on a quest to prove", "yourself worthy. My knights all appreciate a good quest.");
                c.nextChat = 85012;
                break;
            case 85012:
                npc(c, "King Arthur", Anim.SAD, "Unfortunately, our current quest is to rescue Merlin. He", "recently got himself trapped in some sort of magical", "crystal. We've moved him from the cave we found him in", "and now he's upstairs in his tower.");
                c.nextChat = 85013;
                break;
            case 85013:
                options_with_title(c, 85013, "Start the Merlin's Crystal quest?", "Yes.", "No.");
                break;
            case 85020:
                player(c, Anim.CALM_1, "I will see what I can do then.");
                c.nextChat = 85021;
                // Hook the quest start here!
                c.questStages[MerlinsCrystal.QUEST_ID] = MerlinsCrystal.INVESTIGATING;
                QuestAssistant.sendStages(c);
                break;
            case 85021:
                npc(c, "King Arthur", Anim.CALM_1, "Talk to my knights if you need any help.");
                c.nextChat = -1;
                break;
            case 85025:
                player(c, Anim.CALM_1, "I may come back and try that later.");
                c.nextChat = 85026;
                break;
            case 85026:
                npc(c, "King Arthur", Anim.CALM_1, "Be sure that you come speak to me soon then.");
                c.nextChat = -1;
                break;

            // Option 2: What are you doing in Gielinor?
            case 85030:
                player(c, Anim.THINKING, "So what are you doing in Gielinor?");
                c.nextChat = 85031;
                break;
            case 85031:
                npc(c, "King Arthur", Anim.CALM_1, "Well legend says we will return to Britain in its time", "of greatest need. But that's not for quite a while yet.");
                c.nextChat = 85032;
                break;
            case 85032:
                npc(c, "King Arthur", Anim.CALM_1, "So we've moved the whole outfit here for now.");
                c.nextChat = 85033;
                break;
            case 85033:
                npc(c, "King Arthur", Anim.HAPPY, "We're passing the time in Gielinor!");
                c.nextChat = -1;
                break;

            // Option 3: Thank you
            case 85040:
                player(c, Anim.CALM_1, "Thank you very much.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-MERLIN'S CRYSTAL (Holy Grail Hook)
            // ==========================================
            case 85050:
                player(c, Anim.HAPPY, "Now I am a knight of the round table, do you have any", "more quests for me?");
                c.nextChat = 85051;
                break;
            case 85051:
                npc(c, "King Arthur", Anim.HAPPY, "Aha! I'm glad you are here! I am sending out various", "knights on an important quest. I was wondering if you", "too would like to take up this quest?");
                c.nextChat = 85052;
                break;
            case 85052:
                options(c, 85052, "Tell me of this quest.", "I am weary of questing for the time being...");
                break;

            // Tell me of this quest
            case 85060:
                player(c, Anim.CALM_1, "Tell me of this quest.");
                c.nextChat = 85061;
                break;
            case 85061:
                npc(c, "King Arthur", Anim.CALM_1, "Well, we recently found out that the Holy Grail has", "passed into Gielinor.");
                c.nextChat = 85062;
                break;
            case 85062:
                npc(c, "King Arthur", Anim.HAPPY, "This is most fortuitous!");
                c.nextChat = 85063;
                break;
            case 85063:
                npc(c, "King Arthur", Anim.CALM_1, "None of my knights ever did return with it last time.", "Now we have the opportunity to give it another go,", "maybe this time we will have more luck!");
                c.nextChat = c.combatLevel < 50 ? 85065 : 85066;
                break;
            case 85065:
                npc(c, "King Arthur", Anim.CALM_1, "Before starting this quest, be aware that your", "combat level is lower than the recommended level of 50.");
                c.nextChat = 85066;
                break;
            case 85066:
                options_with_title(c, 85066, "Start the Holy Grail quest?", "Yes.", "No.");
                break;

            // Start Holy Grail: Yes
            case 85070:
                player(c, Anim.HAPPY, "I'd enjoy trying that.");
                c.nextChat = 85071;
                // TODO: Start Holy Grail Quest here
                break;
            case 85071:
                npc(c, "King Arthur", Anim.CALM_1, "Go speak to Merlin. He may be able to give a better", "clue as to where it is now you have freed him from", "that crystal.");
                c.nextChat = 85072;
                break;
            case 85072:
                npc(c, "King Arthur", Anim.CALM_1, "He has set up his workshop in the room next to the", "library.");
                c.nextChat = -1;
                break;

            // Start Holy Grail: No
            case 85075:
                player(c, Anim.CALM_1, "I may come back and try that later.");
                c.nextChat = 85076;
                break;
            case 85076:
                npc(c, "King Arthur", Anim.CALM_1, "Be sure that you come speak to me soon then.");
                c.nextChat = -1;
                break;

            // I am weary
            case 85080:
                player(c, Anim.SAD, "I am weary of questing for the time being...");
                c.nextChat = 85081;
                break;
            case 85081:
                npc(c, "King Arthur", Anim.CALM_1, "Maybe later then?");
                c.nextChat = 85082;
                break;
            case 85082:
                player(c, Anim.CALM_1, "Maybe so.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-HOLY GRAIL
            // ==========================================
            case 85090:
                npc(c, "King Arthur", Anim.HAPPY, "Thank you for retrieving the Grail! You shall be long", "remembered as one of the greatest heroes amongst the", "Knights of the Round Table!");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-KING'S RANSOM
            // ==========================================
            case 85100:
                npc(c, "King Arthur", Anim.HAPPY, "Welcome, brave knight. I am always happy to see you", "back at my court. Please feel free to roam the castle", "and make use of the training grounds.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        switch (c.dialogueAction) {
            case 85002:
                if (buttonId == OPT3_FIRST) {
                    next(c, 85010);
                } else if (buttonId == OPT3_SECOND) {
                    next(c, 85030);
                } else if (buttonId == OPT3_THIRD) {
                    next(c, 85040);
                }
                break;
            case 85013:
                if (buttonId == OPT2_FIRST) {
                    next(c, 85020);
                } else if (buttonId == OPT2_SECOND) {
                    next(c, 85025);
                }
                break;
            case 85052:
                if (buttonId == OPT2_FIRST) {
                    next(c, 85060);
                } else if (buttonId == OPT2_SECOND) {
                    next(c, 85080);
                }
                break;
            case 85066:
                if (buttonId == OPT2_FIRST) {
                    next(c, 85070);
                } else if (buttonId == OPT2_SECOND) {
                    next(c, 85075);
                }
                break;
        }
    }
}