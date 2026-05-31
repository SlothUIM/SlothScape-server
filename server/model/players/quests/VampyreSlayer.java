package server.model.players.quests;

import server.model.players.Player;

public class VampyreSlayer extends QuestManager {

    public static final int QUEST_ID = 18; // Make sure this doesn't conflict with other quests!
    public static final String QUEST_NAME = "Vampyre Slayer";
    
    // Quest Stages
    public static final int NOT_STARTED = 0;
    public static final int SPOKE_TO_MORGAN = 1;
    public static final int GATHERING_EQUIPMENT = 2;
    public static final int COMPLETED = 3;

    // Item IDs
    public static final int BEER = 1917;
    public static final int STAKE = 1549;
    public static final int HAMMER = 2347;
    public static final int GARLIC = 1550;

    public VampyreSlayer(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Vampyre Slayer Stage set to: " + stage);
        QuestAssistant.sendStages(c);
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return true;
    }

    @Override
    public void giveRewards() {
        c.getPA().addSkillXP(4825, c.playerAttack); // 4,825 Attack XP
        c.questPoints += 3; // Wiki states 3 QP reward
        c.getPA().sendFrame126("You have completed Vampyre Slayer!", 12144);
        c.getPA().sendFrame126("3 Quest Points", 12147);
        c.getPA().sendFrame126("4,825 Attack XP", 12148);
        c.getPA().sendFrame126("", 12149);
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
                return "I can start this quest by speaking to Morgan in Draynor Village.";
            case SPOKE_TO_MORGAN:
                return "Morgan asked me to slay a vampyre. I should find Dr. Harlow in the Blue Moon Inn in Varrock for advice.";
            case GATHERING_EQUIPMENT:
                return "Dr. Harlow told me how to kill the vampyre. I need to go to Draynor Manor and kill Count Draynor.";
            case COMPLETED:
                return "Quest complete! I defeated Count Draynor and saved Draynor Village.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@Vampyre Slayer", 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@Morgan@bla@ in", line++);
            c.getPA().sendFrame126("@dre@Draynor Village@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("Be able to defeat a level 34 Vampyre.", line++);
        } else if (stage == SPOKE_TO_MORGAN) {
            c.getPA().sendFrame126("@str@I've talked to Morgan.", line++);
            c.getPA().sendFrame126("I should speak to @dre@Dr. Harlow@bla@ at the @dre@Blue Moon Inn", line++);
            c.getPA().sendFrame126("in @dre@Varrock@bla@ to learn how to kill the vampyre.", line++);
            c.getPA().sendFrame126("Morgan mentioned he likes beer.", line++);
        } else if (stage == GATHERING_EQUIPMENT) {
            c.getPA().sendFrame126("@str@I've talked to Dr. Harlow.", line++);
            c.getPA().sendFrame126("I need to go to the basement of @dre@Draynor Manor", line++);
            c.getPA().sendFrame126("and kill @dre@Count Draynor@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            
            // Dynamic inventory checks!
            boolean hasStake = c.getItems().playerHasItem(STAKE);
            boolean hasHammer = c.getItems().playerHasItem(HAMMER);
            boolean hasGarlic = c.getItems().playerHasItem(GARLIC);
            
            c.getPA().sendFrame126("I must have these items in my inventory to finish him:", line++);
            c.getPA().sendFrame126((hasStake ? "@str@" : "@dre@") + "A stake", line++);
            c.getPA().sendFrame126((hasHammer ? "@str@" : "@dre@") + "A hammer", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126((hasGarlic ? "@str@" : "@dre@") + "Garlic (Optional, but weakens him)", line++);
            
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I spoke to Morgan.", line++);
            c.getPA().sendFrame126("@str@I spoke to Dr. Harlow.", line++);
            c.getPA().sendFrame126("@str@I killed Count Draynor.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("3 Quest Points", line++);
            c.getPA().sendFrame126("4,825 Attack Exp", line++);
        }
        c.getPA().showInterface(8134);
    }
}