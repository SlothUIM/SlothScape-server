package server.model.players.quests;

import server.model.players.Player;

public class BlackKnightsFortress extends QuestManager {

    public static final int QUEST_ID = 13; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Black Knights' Fortress";

    public BlackKnightsFortress(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Black Knights' Fortress Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return c.questPoints >= 12; // Enforcing the 12 QP requirement!
    }

    @Override
    public void giveRewards() {
        c.questPoints += 3;
        c.getItems().addItem(995, 2500); // 2,500 Coins
        
        c.getPA().sendFrame126("You have completed Black Knights' Fortress!", 12144);
        c.getPA().sendFrame126("3 Quest Points", 12147);
        c.getPA().sendFrame126("2,500 Coins", 12148);
        c.getPA().sendFrame126("", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] == 3;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Sir Amik Varze in Falador Castle.";
            case 1:
                return "I need to kill 30 Black Knights and collect their notes for Sir Amik Varze.";
            case 2:
                return "I've killed the knights and gathered the notes. I need to speak to Sir Amik Varze.";
            case 3:
                return "Quest complete! I received 2,500 coins and 3 Quest Points.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Sir Amik Varze @bla@in", line++);
            c.getPA().sendFrame126("@red@Falador Castle.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Quest Requirements:", line++);
            
            if (c.questPoints >= 12) {
                c.getPA().sendFrame126("@str@12 Quest Points", line++);
            } else {
                c.getPA().sendFrame126("@red@12 Quest Points", line++);
            }
            
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've talked with Sir Amik Varze", line++);
            c.getPA().sendFrame126("He wants me to kill 30 Black Knights and", line++);
            c.getPA().sendFrame126("collect their notes.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@30 Black Knight notes", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I talked to Sir Amik Varze.", line++);
            c.getPA().sendFrame126("@str@I've killed 30 Black Knights", line++);
            c.getPA().sendFrame126("@str@and given Sir Amik Varze his items.", line++);
            c.getPA().sendFrame126("I should go speak to @red@Sir Amik Varze.", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I talked to Sir Amik Varze.", line++);
            c.getPA().sendFrame126("@str@I've killed 30 Black Knights", line++);
            c.getPA().sendFrame126("@str@and given Sir Amik Varze his items.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("2,500 coins", line++);
            c.getPA().sendFrame126("3 Quest Points", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}