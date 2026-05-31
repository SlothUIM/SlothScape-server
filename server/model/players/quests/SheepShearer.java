package server.model.players.quests;

import server.model.players.Player;

public class SheepShearer extends QuestManager {

    public static final int QUEST_ID = 10;
    public static final String QUEST_NAME = "Sheep Shearer";
    
    // Quest Stages
    public static final int NOT_STARTED = 0;
    public static final int GATHERING_WOOL = 1;
    public static final int COMPLETED = 2;

    // Item IDs
    public static final int BALL_OF_WOOL = 1759;
    public static final int SHEARS = 1735;
    
    // Config
    public static final int WOOL_REQUIRED = 20;

    public SheepShearer(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        QuestAssistant.sendStages(c);
        System.out.println("Sheep Shearer Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 2;
    }

    @Override
    public boolean hasRequirements() {
        return true;
    }

    @Override
    public void giveRewards() {
        c.getPA().addSkillXP(150, c.playerCrafting); // 150 Crafting XP
        c.getItems().addItem(995, 60); // 60 Coins
        c.questPoints += 1;
        
        c.getPA().sendFrame126("You have completed Sheep Shearer!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("150 Crafting XP", 12148);
        c.getPA().sendFrame126("60 Coins", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] == COMPLETED;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > NOT_STARTED;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case NOT_STARTED:
                return "I can start this quest by speaking to Fred the Farmer, north of Lumbridge Castle.";
            case GATHERING_WOOL:
                int remaining = WOOL_REQUIRED - c.woolHandedIn;
                return "Fred asked me to bring him 20 balls of wool. I still need to bring him " + remaining + " more.";
            case COMPLETED:
                return "Quest complete! I brought Fred 20 balls of wool and he paid me for my hard work.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@Sheep Shearer", 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@Fred", line++);
            c.getPA().sendFrame126("at his farm just north of @dre@Lumbridge Castle@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("None.", line++);
        } else if (stage == GATHERING_WOOL) {
            c.getPA().sendFrame126("@str@I've talked to Fred the Farmer.", line++);
            c.getPA().sendFrame126("I've agreed to get him 20 balls of wool.", line++);
            c.getPA().sendFrame126("", line++);
            
            // Show exactly how many they have handed in
            c.getPA().sendFrame126("Balls of wool handed in: @dre@" + c.woolHandedIn + " / " + WOOL_REQUIRED, line++);
            
            // Show if they have enough in their inventory to finish it right now
            if (c.getItems().getItemAmount(BALL_OF_WOOL) + c.woolHandedIn >= WOOL_REQUIRED) {
                c.getPA().sendFrame126("@gre@I have enough wool to finish the quest!", line++);
                c.getPA().sendFrame126("@gre@I should talk to Fred.", line++);
            } else {
                c.getPA().sendFrame126("@red@I need to shear sheep and spin the wool", line++);
                c.getPA().sendFrame126("@red@on a spinning wheel.", line++);
            }
            
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I've talked to Fred the Farmer.", line++);
            c.getPA().sendFrame126("@str@I gave Fred his wool.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("150 Crafting Exp", line++);
            c.getPA().sendFrame126("60 Coins", line++);
        }
        c.getPA().showInterface(8134);
    }
}