package server.model.players.quests;

import server.model.players.Player;

public class RomeoJuliet extends QuestManager {

    public static final int QUEST_ID = 14; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Romeo & Juliet";

    public RomeoJuliet(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Romeo & Juliet Stage set to: " + stage);
        QuestAssistant.sendStages(c);
    }

    @Override
    public int getTotalStages() {
        return 9;
    }

    @Override
    public boolean hasRequirements() {
        return true; // No requirements!
    }

    @Override
    public void giveRewards() {
        c.questPoints += 5;
        
        c.getPA().sendFrame126("You have completed Romeo & Juliet!", 12144);
        c.getPA().sendFrame126("5 Quest Points", 12147);
        c.getPA().sendFrame126("", 12148);
        c.getPA().sendFrame126("", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] >= 9;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Romeo in Varrock Square.";
            case 1:
                return "Romeo has asked me to find Juliet and figure out why she isn't replying to him.";
            case 2:
                return "I spoke to Juliet. She gave me a message to deliver back to Romeo.";
            case 3:
            case 4:
                return "I gave Romeo the message. I should speak to him again to find out what to do next.";
            case 5:
                return "Romeo told me to go see a witch named Winelda.";
            case 6:
                return "Winelda needs me to bring her 1 rat's tail, 1 bone, and 1 vial of water.";
            case 7:
                return "I brought Winelda the ingredients and got a potion. I need to take it to Juliet.";
            case 8:
                return "Juliet drank the potion. I should go tell Romeo the plan.";
            case 9:
                return "Quest complete! I helped Romeo and Juliet and received 5 Quest Points.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@" + QUEST_NAME, 8144);
        c.getPA().sendFrame126("", 8145);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == 0) {
            c.getPA().sendFrame126("To start the quest, you should talk with @red@Romeo", line++);
            c.getPA().sendFrame126("found in @red@Varrock Square.", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@To start the quest, you should talk with Romeo", line++);
            c.getPA().sendFrame126("@str@found in Varrock Square.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Romeo has asked you to speak to @red@Juliet @bla@for him", line++);
            c.getPA().sendFrame126("and return to him, as she hasn't been responding to any", line++);
            c.getPA().sendFrame126("of his letters lately.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@To start the quest, you should talk with Romeo", line++);
            c.getPA().sendFrame126("@str@found in Varrock Square.", line++);
            c.getPA().sendFrame126("@str@Romeo has asked you to speak to Juliet for him", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("You have spoken to Juliet who's been acting strange.", line++);
            c.getPA().sendFrame126("She gave you a message and asked you to leave.", line++);
            c.getPA().sendFrame126("You should return this message to @red@Romeo.", line++);
        } else if (stage == 3 || stage == 4) {
            c.getPA().sendFrame126("@str@Romeo has asked you to speak to Juliet for him", line++);
            c.getPA().sendFrame126("@str@You have spoken to Juliet who's been acting strange", line++);
            c.getPA().sendFrame126("@str@She gave you a message and asked you to leave", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("You have spoken to Romeo and given him the message.", line++);
            c.getPA().sendFrame126("You should try to talk to @red@Romeo @bla@again.", line++);
        } else if (stage == 5) {
            c.getPA().sendFrame126("@str@You have spoken to Juliet who's been acting strange", line++);
            c.getPA().sendFrame126("@str@You have spoken to Romeo and given him the message", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Romeo says you should see a witch called @red@Winelda.", line++);
        } else if (stage == 6) {
            c.getPA().sendFrame126("@str@You have spoken to Romeo and given him the message", line++);
            c.getPA().sendFrame126("@str@Romeo says you should see a witch called Winelda", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Winelda needs me to bring her the following:", line++);
            c.getPA().sendFrame126("@red@1 rat's tail", line++);
            c.getPA().sendFrame126("@red@1 bone", line++);
            c.getPA().sendFrame126("@red@1 vial of water", line++);
        } else if (stage == 7) {
            c.getPA().sendFrame126("@str@Romeo says you should see a witch called Winelda", line++);
            c.getPA().sendFrame126("@str@Winelda needs me to bring her 1 rats tail, 1 bone", line++);
            c.getPA().sendFrame126("@str@and 1 vial of water", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I brought Winelda the ingredients and she gave me a potion.", line++);
            c.getPA().sendFrame126("I should go speak to @red@Juliet.", line++);
        } else if (stage == 8) {
            c.getPA().sendFrame126("@str@Winelda needs me to bring her 1 rats tail, 1 bone", line++);
            c.getPA().sendFrame126("@str@and 1 vial of water", line++);
            c.getPA().sendFrame126("@str@I brought Winelda the ingredients and got a potion", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I gave Juliet the potion and she drank it.", line++);
            c.getPA().sendFrame126("I should go speak to @red@Romeo @bla@to tell him the plan.", line++);
        } else if (stage == 9) {
            c.getPA().sendFrame126("@str@I brought Winelda the ingredients and got a potion", line++);
            c.getPA().sendFrame126("@str@I gave Juliet the potion and she drank it", line++);
            c.getPA().sendFrame126("@str@I have spoken to Romeo, he's thankful for our help.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("5 Quest Points", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}