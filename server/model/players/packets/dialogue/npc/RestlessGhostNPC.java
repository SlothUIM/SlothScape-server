package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.RestlessGhost;

public class RestlessGhostNPC extends NPCDialogue {

    public RestlessGhostNPC(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 922 }; 
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        
        boolean wearingAmulet = c.playerEquipment[c.playerAmulet] == RestlessGhost.GHOSTSPEAK_AMULET;

        switch (startDialogueId) {
            case 9200:
                player(c, Anim.CALM_1, "Hello ghost, how are you?");
                if (wearingAmulet) {
                    c.nextChat = 9220; // Translated path
                } else {
                    c.nextChat = 9201; // Gibberish path
                }
                break;

            // ==========================================
            // GIBBERISH (NO AMULET)
            // ==========================================
            case 9201:
                npc(c, "Restless ghost", Anim.SAD, "Wooo wooo wooooo!");
                c.nextChat = 9202;
                break;
            case 9202:
                options3(c, 9202, "Sorry, I don't speak ghost.", "Ooh, THAT'S interesting.", "Any hints where I can find some treasure?");
                break;
            case 9203:
                player(c, Anim.SAD, "Sorry, I don't speak ghost.");
                c.nextChat = 9204;
                break;
            case 9204:
                npc(c, "Restless ghost", Anim.CONFUSED, "Woo woo?");
                c.nextChat = 9205;
                break;
            case 9205:
                player(c, Anim.CALM_1, "Nope, still don't understand you.");
                c.nextChat = 9206;
                break;
            case 9206:
                npc(c, "Restless ghost", Anim.ANGRY, "WOOOOOOOOO!");
                c.nextChat = 9207;
                break;
            case 9207:
                player(c, Anim.CALM_1, "Never mind.");
                c.nextChat = 0;
                break;
            case 9208:
                player(c, Anim.HAPPY, "Ooh, THAT'S interesting.");
                c.nextChat = 9209;
                break;
            case 9209:
                npc(c, "Restless ghost", Anim.SAD, "Woo wooo. Woooooooooooooooooo!");
                c.nextChat = 9210;
                break;
            case 9210:
                options3(c, 9210, "Did he really?", "My brother had EXACTLY the same problem.", "Goodbye. Thanks for the chat.");
                break;
            case 9211:
                player(c, Anim.CALM_1, "Goodbye. Thanks for the chat.");
                c.nextChat = 9212;
                break;
            case 9212:
                npc(c, "Restless ghost", Anim.CONFUSED, "Wooo wooo?");
                c.nextChat = 0;
                break;
            case 9213:
                player(c, Anim.HAPPY, "Any hints where I can find some treasure?");
                c.nextChat = 9214;
                break;
            case 9214:
                npc(c, "Restless ghost", Anim.SAD, "Wooooooo woo! Wooooo woo wooooo woowoowoo woo", "Woo wooo. Wooooo woo woo? Woooooooooooooooooo!");
                c.nextChat = 9215;
                break;
            case 9215:
                options(c, 9215, "Sorry, I don't speak ghost.", "Thank you. You've been very helpful.");
                break;
            case 9216:
                player(c, Anim.HAPPY, "Thank you. You've been very helpful.");
                c.nextChat = 9217;
                break;
            case 9217:
                npc(c, "Restless ghost", Anim.SAD, "Wooooooo.");
                c.nextChat = 0;
                break;

            // ==========================================
            // TRANSLATED (HAS AMULET)
            // ==========================================
            case 9220:
                if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_GHOST || c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.GOT_SKULL) {
                    npc(c, "Restless ghost", Anim.CALM_1, "How are you doing finding my skull?");
                    c.nextChat = (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.GOT_SKULL) ? 9240 : 9245;
                } else {
                    npc(c, "Restless ghost", Anim.SAD, "Not very good actually.");
                    c.nextChat = 9221;
                }
                break;
            case 9221:
                player(c, Anim.CONFUSED, "What's the problem then?");
                c.nextChat = 9222;
                break;
            case 9222:
                npc(c, "Restless ghost", Anim.CONFUSED, "Did you just understand what I said???");
                c.nextChat = 9223;
                break;
            case 9223:
                options(c, 9223, "Yep, now tell me what the problem is.", "No, you sound like you're speaking nonsense to me.", "Wow, this amulet works!");
                break;
            case 9224:
                player(c, Anim.HAPPY, "Yep, now tell me what the problem is.");
                c.nextChat = 9225;
                break;
            case 9225:
                npc(c, "Restless ghost", Anim.HAPPY, "WOW! This is INCREDIBLE! I didn't expect anyone to", "ever understand me again!");
                c.nextChat = 9226;
                break;
            case 9226:
                player(c, Anim.CALM_1, "Ok, Ok, I can understand you!", "But have you any idea WHY you're doomed to be a ghost?");
                c.nextChat = 9227;
                break;
            case 9227:
                npc(c, "Restless ghost", Anim.SAD, "Well, to be honest... I'm not sure.");
                c.nextChat = 9228;
                break;
            case 9228:
                player(c, Anim.CALM_1, "I've been told a certain task may need to be completed", "so you can rest in peace.");
                c.nextChat = 9229;
                break;
            case 9229:
                npc(c, "Restless ghost", Anim.CALM_1, "I should think it is probably because a warlock has come", "along and stolen my skull. If you look inside my", "coffin there, you'll find my corpse without a head on it.");
                c.nextChat = 9230;
                break;
            case 9230:
                player(c, Anim.CONFUSED, "Do you know where this warlock might be now?");
                c.nextChat = 9231;
                break;
            case 9231:
                npc(c, "Restless ghost", Anim.CALM_1, "I think it was one of the warlocks who lives in the big", "tower by the sea south-west from here.");
                c.nextChat = 9232;
                break;
            case 9232:
                player(c, Anim.HAPPY, "Ok. I will try and get the skull back for you, then", "you can rest in peace.");
                c.nextChat = 9233;
                break;
            case 9233:
                npc(c, "Restless ghost", Anim.HAPPY, "Ooh, thank you. That would be such a great relief!", "It is so dull being a ghost...");
                new RestlessGhost(c).setStage(RestlessGhost.SPOKE_TO_GHOST);
                c.nextChat = 0;
                break;

            case 9240:
                player(c, Anim.HAPPY, "I have found it!");
                c.nextChat = 9241;
                break;
            case 9241:
                npc(c, "Restless ghost", Anim.HAPPY, "Hurrah! Now I can stop being a ghost! You just need", "to put it in my coffin there, then I'll be free!");
                c.nextChat = 0;
                break;
                
            case 9245:
                player(c, Anim.SAD, "Sorry, I can't find it at the moment.");
                c.nextChat = 9246;
                break;
            case 9246:
                npc(c, "Restless ghost", Anim.CALM_1, "Ah well. Keep on looking.", "I'm pretty sure it's somewhere in the tower south-west", "from here. There's a lot of levels to the tower, though.", "I suppose it might take a little while to find.");
                c.nextChat = 0;
                break;

            // Extra branches that just loop back for simplicity
            case 9250:
                player(c, Anim.CALM_1, "No, you sound like you're speaking nonsense to me.");
                c.nextChat = 9251;
                break;
            case 9251:
                npc(c, "Restless ghost", Anim.SAD, "Oh that's a pity. You got my hopes up there.");
                c.nextChat = 9252;
                break;
            case 9252:
                player(c, Anim.SAD, "Yeah, it is a pity. Sorry about that.");
                c.nextChat = 9253;
                break;
            case 9253:
                npc(c, "Restless ghost", Anim.CONFUSED, "Hang on a second... you CAN understand me!");
                c.nextChat = 9254;
                break;
            case 9254:
                options(c, 9254, "No I can't.", "Yep, clever aren't I?");
                break;
            case 9255:
                player(c, Anim.CALM_1, "No I can't.");
                c.nextChat = 9256;
                break;
            case 9256:
                npc(c, "Restless ghost", Anim.ANNOYED, "Great. The first person I can speak to in ages...", "and they're a moron.");
                c.nextChat = 0;
                break;
            case 9257:
                player(c, Anim.HAPPY, "Yep, clever aren't I?");
                c.nextChat = 9258;
                break;
            case 9258:
                npc(c, "Restless ghost", Anim.CALM_1, "I'm impressed. You must be very powerful. I don't", "suppose you can stop me being a ghost?");
                c.nextChat = 9259;
                break;
            case 9259:
                options(c, 9259, "Yes, ok. Do you know WHY you're a ghost?", "No, you're scary!");
                break;
            case 9260:
                player(c, Anim.CALM_1, "Yes, ok. Do you know WHY you're a ghost?");
                c.nextChat = 9227; // Loops to main path
                break;
            case 9261:
                player(c, Anim.SCARED, "No, you're scary!");
                c.nextChat = 9262;
                break;
            case 9262:
                npc(c, "Restless ghost", Anim.SAD, "Great.", "The first person I can speak to in ages...", "..and they're an idiot.");
                c.nextChat = 0;
                break;

            case 9270:
                player(c, Anim.HAPPY, "Wow, this amulet works!");
                c.nextChat = 9271;
                break;
            case 9271:
                npc(c, "Restless ghost", Anim.HAPPY, "Oh! It's your amulet that's doing it! I did wonder.", "I don't suppose you can help me? I don't like being a ghost.");
                c.nextChat = 9259; // Loops to options
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9202) {
            if (buttonId == OPT3_FIRST) next(c, 9203);
            else if (buttonId == OPT3_SECOND) next(c, 9208);
            else if (buttonId == OPT3_THIRD) next(c, 9213);
        } else if (c.dialogueAction == 9210) {
            if (buttonId == OPT3_FIRST) next(c, 9211); // Did he really (short loop)
            else if (buttonId == OPT3_SECOND) next(c, 9211); // Brother problem
            else if (buttonId == OPT3_THIRD) next(c, 9211);
        } else if (c.dialogueAction == 9215) {
            if (buttonId == OPT2_FIRST) next(c, 9203);
            else if (buttonId == OPT2_SECOND) next(c, 9216);
        } else if (c.dialogueAction == 9223) {
            if (buttonId == OPT3_FIRST) next(c, 9224);
            else if (buttonId == OPT3_SECOND) next(c, 9250);
            else if (buttonId == OPT3_THIRD) next(c, 9270);
        } else if (c.dialogueAction == 9254) {
            if (buttonId == OPT2_FIRST) next(c, 9255);
            else if (buttonId == OPT2_SECOND) next(c, 9257);
        } else if (c.dialogueAction == 9259) {
            if (buttonId == OPT2_FIRST) next(c, 9260);
            else if (buttonId == OPT2_SECOND) next(c, 9261);
        }
    }
}