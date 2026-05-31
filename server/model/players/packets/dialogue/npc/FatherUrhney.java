package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.RestlessGhost;

public class FatherUrhney extends NPCDialogue {

    public FatherUrhney(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 923 }; 
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 9100:
                npc(c, "Father Urhney", Anim.ANGRY, "Go away! I'm meditating!");
                c.nextChat = 9101;
                break;
            case 9101:
                if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_AERECK) {
                    options3(c, 9101, "Well, that's friendly.", "Father Aereck sent me to talk to you.", "I've come to repossess your house.");
                } else if (c.questStages[RestlessGhost.QUEST_ID] >= RestlessGhost.GOT_AMULET && !c.getItems().playerHasItem(RestlessGhost.GHOSTSPEAK_AMULET) && c.playerEquipment[c.playerAmulet] != RestlessGhost.GHOSTSPEAK_AMULET) {
                    options3(c, 9101, "Well, that's friendly.", "I've lost the Amulet of Ghostspeak.", "I've come to repossess your house.");
                } else {
                    options(c, 9101, "Well, that's friendly.", "I've come to repossess your house.");
                }
                break;

            case 9110:
                player(c, Anim.CALM_1, "Well, that's friendly.");
                c.nextChat = 9111;
                break;
            case 9111:
                npc(c, "Father Urhney", Anim.ANGRY, "I SAID go AWAY.");
                c.nextChat = 9112;
                break;
            case 9112:
                player(c, Anim.SAD, "Okay, okay... sheesh, what a grouch.");
                c.nextChat = 0;
                break;

            case 9120:
                player(c, Anim.CALM_1, "I've come to repossess your house.");
                c.nextChat = 9121;
                break;
            case 9121:
                npc(c, "Father Urhney", Anim.ANGRY, "Under what grounds???");
                c.nextChat = 9122;
                break;
            case 9122:
                options(c, 9122, "Repeated failure on mortgage repayments.", "I don't know, I just wanted this house.");
                break;
            case 9123:
                player(c, Anim.CALM_1, "Repeated failure on mortgage repayments.");
                c.nextChat = 9124;
                break;
            case 9124:
                npc(c, "Father Urhney", Anim.CONFUSED, "What?", "But... I don't have a mortgage! I built this house myself!");
                c.nextChat = 9125;
                break;
            case 9125:
                player(c, Anim.SAD, "Sorry. I must have got the wrong address. All the houses", "look the same around here.");
                c.nextChat = 9126;
                break;
            case 9126:
                npc(c, "Father Urhney", Anim.ANGRY, "What? What houses? What ARE you talking about???");
                c.nextChat = 9127;
                break;
            case 9127:
                player(c, Anim.CALM_1, "Never mind.");
                c.nextChat = 0;
                break;
            case 9128:
                player(c, Anim.CALM_1, "I don't know. I just wanted this house...");
                c.nextChat = 9129;
                break;
            case 9129:
                npc(c, "Father Urhney", Anim.ANGRY, "Oh... go away and stop wasting my time!");
                c.nextChat = 0;
                break;

            // QUEST ADVANCEMENT
            case 9130:
                player(c, Anim.CALM_1, "Father Aereck sent me to talk to you.");
                c.nextChat = 9131;
                break;
            case 9131:
                npc(c, "Father Urhney", Anim.ANNOYED, "I suppose I'd better talk to you then. What problems", "has he got himself into this time?");
                c.nextChat = 9132;
                break;
            case 9132:
                options(c, 9132, "He's got a ghost haunting his graveyard.", "You mean he gets himself into lots of problems?");
                break;
            case 9133:
                player(c, Anim.CALM_1, "You mean he gets himself into lots of problems?");
                c.nextChat = 9134;
                break;
            case 9134:
                npc(c, "Father Urhney", Anim.CALM_1, "Yeah. For example, when we were trainee priests he", "kept on getting stuck up bell ropes.", "Anyway. I don't have time for chitchat. What's his", "problem THIS time?");
                c.nextChat = 9135;
                break;
            case 9135:
                player(c, Anim.CALM_1, "He's got a ghost haunting his graveyard.");
                c.nextChat = 9136;
                break;
            case 9136:
                npc(c, "Father Urhney", Anim.ANNOYED, "Oh, the silly fool.", "I leave town for just five months, and ALREADY he", "can't manage.", "(sigh)");
                c.nextChat = 9137;
                break;
            case 9137:
                npc(c, "Father Urhney", Anim.CALM_1, "Well, I can't go back and exorcise it. I vowed not to", "leave this place. Until I had done a full two years", "of prayer and meditation.");
                c.nextChat = 9138;
                break;
            case 9138:
                npc(c, "Father Urhney", Anim.CALM_1, "Tell you what I can do though; take this amulet.");
                c.nextChat = 9139;
                break;
            case 9139:
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(RestlessGhost.GHOSTSPEAK_AMULET, 1);
                    item("Father Urhney hands you an amulet.", c, RestlessGhost.GHOSTSPEAK_AMULET, "");
                    new RestlessGhost(c).setStage(RestlessGhost.GOT_AMULET);
                    c.nextChat = 9140;
                } else {
                    npc(c, "Father Urhney", Anim.ANGRY, "You don't have enough inventory space to take this!");
                    c.nextChat = 0;
                }
                break;
            case 9140:
                npc(c, "Father Urhney", Anim.CALM_1, "It is an Amulet of Ghostspeak.", "So called, because when you wear it you can speak", "to ghosts. A lot of ghosts are doomed to be ghosts", "because they have left some important task uncompleted.");
                c.nextChat = 9141;
                break;
            case 9141:
                npc(c, "Father Urhney", Anim.CALM_1, "Maybe if you know what this task is, you can get rid of", "the ghost. I'm not making any guarantees mind you, but", "it is the best I can do right now.");
                c.nextChat = 9142;
                break;
            case 9142:
                player(c, Anim.HAPPY, "Thank you. I'll give it a try!");
                c.nextChat = 0;
                break;

            // LOST AMULET RECLAIM
            case 9150:
                player(c, Anim.SAD, "I've lost the Amulet of Ghostspeak.");
                c.nextChat = 9151;
                break;
            case 9151:
                if (c.getItems().bankContains(RestlessGhost.GHOSTSPEAK_AMULET)) {
                    npc(c, "Father Urhney", Anim.ANGRY, "You come here wasting my time... Has it even occurred", "to you that you've got it stored somewhere? Now GO AWAY!");
                    c.nextChat = 0;
                } else if (c.getItems().freeSlots() == 0) {
                    npc(c, "Father Urhney", Anim.ANGRY, "How careless can you get? Those things aren't easy to", "come by you know! Now clear some space in your", "inventory and I'll give you another one.");
                    c.nextChat = 0;
                } else {
                    npc(c, "Father Urhney", Anim.ANNOYED, "How careless can you get? Those things aren't easy to", "come by you know! It's a good job I've got a spare.");
                    c.nextChat = 9152;
                }
                break;
            case 9152:
                c.getItems().addItem(RestlessGhost.GHOSTSPEAK_AMULET, 1);
                item("Father Urhney hands you an amulet.", c, RestlessGhost.GHOSTSPEAK_AMULET, "");
                c.nextChat = 9153;
                break;
            case 9153:
                npc(c, "Father Urhney", Anim.ANNOYED, "Be more careful this time.");
                c.nextChat = 9154;
                break;
            case 9154:
                player(c, Anim.SAD, "Okay, I'll try to be.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9101) {
            if (buttonId == OPT3_FIRST || buttonId == OPT2_FIRST) next(c, 9110); // Friendly
            else if (buttonId == OPT3_SECOND && c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_AERECK) next(c, 9130); // Quest
            else if (buttonId == OPT3_SECOND && c.questStages[RestlessGhost.QUEST_ID] >= RestlessGhost.GOT_AMULET) next(c, 9150); // Lost Amulet
            else if (buttonId == OPT3_THIRD || buttonId == OPT2_SECOND) next(c, 9120); // Repossess
        } else if (c.dialogueAction == 9122) {
            if (buttonId == OPT2_FIRST) next(c, 9123);
            else if (buttonId == OPT2_SECOND) next(c, 9128);
        } else if (c.dialogueAction == 9132) {
            if (buttonId == OPT2_FIRST) next(c, 9135); // Direct to ghost haunting
            else if (buttonId == OPT2_SECOND) next(c, 9133); // Sidetrack then ghost haunting
        }
    }
}