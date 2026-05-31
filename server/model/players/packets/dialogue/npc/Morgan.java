package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.VampyreSlayer;

public class Morgan extends NPCDialogue {

    public Morgan(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 755 }; 
    }
    @Override
 	public String getDialogueRange() {
 		return "9400-9430";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int stage = c.questStages[VampyreSlayer.QUEST_ID];

        switch (startDialogueId) {
            case 9400:
                if (stage == VampyreSlayer.COMPLETED) {
                    c.nextChat = 9430;
                } else if (stage == VampyreSlayer.SPOKE_TO_MORGAN || stage == VampyreSlayer.GATHERING_EQUIPMENT) {
                    c.nextChat = 9420;
                } else {
                    npc(c, "Morgan", Anim.SCARED, "Please please help us, bold adventurer!");
                    c.nextChat = 9401;
                }
                break;

            // ==========================================
            // STARTING OUT
            // ==========================================
            case 9401:
                player(c, Anim.CONFUSED, "What's the problem?");
                c.nextChat = 9402;
                break;
            case 9402:
                npc(c, "Morgan", Anim.SCARED, "Our little village has been dreadfully ravaged by an", "evil vampyre! He lives in the basement of the manor", "to the north, we need someone to get rid of him", "once and for all!");
                c.nextChat = 9403;
                break;
            case 9403:
                // Warning if their combat level is below 20
                if (c.combatLevel < 20) {
                    c.getDH().sendStatement("Before starting this quest, be aware that your combat", "level is lower than the recommended level of 20.");
                    c.nextChat = 9404;
                } else {
                    c.nextChat = 9404;
                    dialogue(c, npcId, c.nextChat); // Skip directly to options if level 20+
                }
                break;
            case 9404:
                options(c, 9404, "Start the Vampyre Slayer quest?", "Yes.", "No.");
                break;
            case 9405:
                player(c, Anim.SCARED, "No, vampyres are scary!");
                c.nextChat = 9406;
                break;
            case 9406:
                npc(c, "Morgan", Anim.SAD, "I don't blame you.");
                c.nextChat = 0;
                break;
            case 9407:
                player(c, Anim.HAPPY, "Okay, I'm up for an adventure.");
                c.nextChat = 9408;
                break;
            case 9408:
                npc(c, "Morgan", Anim.CALM_1, "I think first you should seek help. I have a friend who", "is a retired vampyre hunter, his name is Dr. Harlow.", "He may be able to give you some tips. He can normally", "be found in the Blue Moon Inn in Varrock, he's a bit");
                c.nextChat = 9409;
                break;
            case 9409:
                npc(c, "Morgan", Anim.CALM_1, "of an old soak these days. Mention his old friend Morgan,", "I'm sure he wouldn't want me killed by a vampyre.");
                c.nextChat = 9410;
                break;
            case 9410:
                player(c, Anim.CALM_1, "I'll look him up then.");
                new VampyreSlayer(c).setStage(VampyreSlayer.SPOKE_TO_MORGAN);
                c.nextChat = 0;
                break;

            // ==========================================
            // MID-QUEST
            // ==========================================
            case 9420:
                npc(c, "Morgan", Anim.SCARED, "How are you doing with the quest?");
                c.nextChat = 9421;
                break;
            case 9421:
                player(c, Anim.CALM_1, "I'm still working on it.");
                c.nextChat = 9422;
                break;
            case 9422:
                npc(c, "Morgan", Anim.SCARED, "Please hurry! Every day we live in fear that we", "will be the vampyre's next victim!");
                c.nextChat = 0;
                break;

            // ==========================================
            // COMPLETED
            // ==========================================
            case 9430:
                npc(c, "Morgan", Anim.HAPPY, "Thank you so much for slaying that horrible vampyre!", "You are a true hero to Draynor Village!");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9404) {
            if (buttonId == OPT2_FIRST) next(c, 9407);
            else if (buttonId == OPT2_SECOND) next(c, 9405);
        }
    }
}