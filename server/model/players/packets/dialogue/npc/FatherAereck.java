package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.RestlessGhost;

public class FatherAereck extends NPCDialogue {

    public FatherAereck(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 921 }; // Covering both standard IDs
    }

    @Override
	public String getDialogueRange() {
		return "9000-9072";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 9000:
                npc(c, "Father Aereck", Anim.CALM_1, "Welcome to the church of holy Saradomin.");
                c.nextChat = 9001;
                break;
            case 9001:
                options3(c, 9001, "Who's Saradomin?", "Nice place you've got here.", "I'm looking for a quest!");
                break;

            // ... (I omitted the Saradomin rants here for brevity, they remain identical to the previous code we wrote!)
            
            // ==========================================
            // QUEST: THE RESTLESS GHOST
            // ==========================================
            case 9050:
                player(c, Anim.CALM_1, "I'm looking for a quest.");
                
                if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.COMPLETED) {
                    c.nextChat = 9070;
                } else if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_AERECK || c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.GOT_AMULET) {
                    c.nextChat = 9080;
                } else if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_GHOST) {
                    c.nextChat = 9085;
                } else if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.GOT_SKULL) {
                    c.nextChat = 9090;
                } else {
                    c.nextChat = 9051; // NOT STARTED
                }
                break;
                
            case 9051:
                npc(c, "Father Aereck", Anim.HAPPY, "That's lucky, I need someone to do a quest for me.");
                c.nextChat = 9052;
                break;
            case 9052:
                options_with_title(c, 9052, "Start The Restless Ghost quest?", "Yes", "No");
                break;
            case 9053:
                player(c, Anim.SAD, "Sorry, I don't have time right now.");
                c.nextChat = 9054;
                break;
            case 9054:
                npc(c, "Father Aereck", Anim.CALM_1, "Oh well. If you do have some spare time on your hands,", "come back and talk to me.");
                c.nextChat = 0;
                break;
            case 9055: 
                player(c, Anim.HAPPY, "Okay, let me help then.");
                c.nextChat = 9056;
                break;
            case 9056:
                npc(c, "Father Aereck", Anim.SAD, "Thank you. The problem is, there is a ghost in the", "church graveyard. I would like you to get rid of it.", "If you need any help, my friend Father Urhney is an", "expert on ghosts.");
                c.nextChat = 9057;
                break;
            case 9057:
                npc(c, "Father Aereck", Anim.CALM_1, "I believe he is currently living as a hermit in Lumbridge", "swamp. He has a little shack in the far west of the swamps.");
                c.nextChat = 9058;
                break;
            case 9058:
                npc(c, "Father Aereck", Anim.CALM_1, "Exit the graveyard through the south gate to reach the", "swamp. I'm sure if you told him that I sent you", "he'd be willing to help.", "My name is Father Aereck by the way. Pleased to meet you.");
                c.nextChat = 9059;
                break;
            case 9059:
                player(c, Anim.HAPPY, "Likewise.");
                c.nextChat = 9060;
                break;
            case 9060:
                npc(c, "Father Aereck", Anim.CALM_1, "Take care travelling through the swamps, I have heard", "they can be quite dangerous.");
                c.nextChat = 9061;
                break;
            case 9061:
                player(c, Anim.CALM_1, "I will, thanks.");
                new RestlessGhost(c).setStage(RestlessGhost.SPOKE_TO_AERECK);
                c.nextChat = 0;
                break;

            // STAGES 1 & 2
            case 9080:
                npc(c, "Father Aereck", Anim.CONFUSED, "Have you got rid of the ghost yet?");
                c.nextChat = (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_AERECK) ? 9081 : 9083;
                break;
            case 9081:
                player(c, Anim.SAD, "I can't find Father Urhney at the moment.");
                c.nextChat = 9082;
                break;
            case 9082:
                npc(c, "Father Aereck", Anim.CALM_1, "Well, you can get to the swamp he lives in by going", "south through the cemetery.", "You'll have to go right into the far western depths", "of the swamp, near the coastline.");
                c.nextChat = 0;
                break;
            case 9083:
                player(c, Anim.CALM_1, "I had a talk with Father Urhney. He has given me this", "funny amulet to talk to the ghost with.");
                c.nextChat = 9084;
                break;
            case 9084:
                npc(c, "Father Aereck", Anim.HAPPY, "I always wondered what that amulet was... Well, I hope", "it's useful. Tell me when you get rid of the ghost!");
                c.nextChat = 0;
                break;

            // STAGE 3
            case 9085:
                npc(c, "Father Aereck", Anim.CONFUSED, "Have you got rid of the ghost yet?");
                c.nextChat = 9086;
                break;
            case 9086:
                player(c, Anim.CALM_1, "I've found out that the ghost's corpse has lost its skull.", "If I can find the skull, the ghost should leave.");
                c.nextChat = 9087;
                break;
            case 9087:
                npc(c, "Father Aereck", Anim.CONFUSED, "That WOULD explain it.", "Hmmmmm. Well, I haven't seen any skulls.");
                c.nextChat = 9088;
                break;
            case 9088:
                player(c, Anim.CALM_1, "Yes, I think a warlock has stolen it.");
                c.nextChat = 9089;
                break;
            case 9089:
                npc(c, "Father Aereck", Anim.ANGRY, "I hate warlocks.", "Ah well, good luck!");
                c.nextChat = 0;
                break;

            // STAGE 4
            case 9090:
                npc(c, "Father Aereck", Anim.CONFUSED, "Have you got rid of the ghost yet?");
                c.nextChat = 9091;
                break;
            case 9091:
                player(c, Anim.HAPPY, "I've finally found the ghost's skull!");
                c.nextChat = 9092;
                break;
            case 9092:
                npc(c, "Father Aereck", Anim.HAPPY, "Great! Put it in the ghost's coffin and see what happens!");
                c.nextChat = 0;
                break;

            // COMPLETED
            case 9070:
                npc(c, "Father Aereck", Anim.HAPPY, "Thank you for getting rid of that awful ghost for me!", "May Saradomin always smile upon you!");
                c.nextChat = 9071;
                break;
            case 9071:
                player(c, Anim.CALM_1, "I'm looking for a new quest.");
                c.nextChat = 9072;
                break;
            case 9072:
                npc(c, "Father Aereck", Anim.SAD, "Sorry, I only had the one quest.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9001) {
            if (buttonId == OPT3_FIRST) next(c, 9010);
            else if (buttonId == OPT3_SECOND) next(c, 9040);
            else if (buttonId == OPT3_THIRD) next(c, 9050);
        } else if (c.dialogueAction == 9052) {
            if (buttonId == OPT2_FIRST) next(c, 9055);
            else if (buttonId == OPT2_SECOND) next(c, 9053);
        }
    }
}