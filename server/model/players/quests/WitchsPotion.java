package server.model.players.quests;

import server.model.players.Player;

public class WitchsPotion extends QuestManager {

    public static final int QUEST_ID = 17;
    public static final String QUEST_NAME = "Witch's Potion";
    
    // Quest Stages
    public static final int NOT_STARTED = 0;
    public static final int GATHERING_ITEMS = 1;
    public static final int DRINK_FROM_CAULDRON = 2;
    public static final int COMPLETED = 3;

    // Item IDs
    public static final int RATS_TAIL = 300;
    public static final int BURNT_MEAT = 2146;
    public static final int EYE_OF_NEWT = 221;
    public static final int ONION = 1957;

    public WitchsPotion(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Witch's Potion Stage set to: " + stage);
        QuestAssistant.sendStages(c);
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return true; // No requirements for this quest
    }

    @Override
    public void giveRewards() {
        c.getPA().addSkillXP(325, c.playerMagic); // 325 Magic XP
        c.questPoints += 1;
        c.getPA().sendFrame126("You have completed Witch's Potion!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("325 Magic XP", 12148);
        c.getPA().sendFrame126("", 12149); // Blank line
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
                return "I can start this quest by speaking to Hetty in Rimmington.";
            case GATHERING_ITEMS:
                return "Hetty needs a rat's tail, burnt meat, eye of newt, and an onion.";
            case DRINK_FROM_CAULDRON:
                return "I gave Hetty the ingredients. I just need to drink from the cauldron!";
            case COMPLETED:
                return "Quest complete! I drank the potion and gained some magic experience.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@Witch's Potion", 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@Hetty", line++);
            c.getPA().sendFrame126("in her house in @dre@Rimmington@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("None.", line++);
        } else if (stage == GATHERING_ITEMS) {
            c.getPA().sendFrame126("@str@I've talked to Hetty.", line++);
            c.getPA().sendFrame126("I need to bring her the following ingredients:", line++);
            
            // Strike out items the player already has in their inventory
            boolean hasTail = c.getItems().playerHasItem(RATS_TAIL);
            boolean hasMeat = c.getItems().playerHasItem(BURNT_MEAT);
            boolean hasEye = c.getItems().playerHasItem(EYE_OF_NEWT);
            boolean hasOnion = c.getItems().playerHasItem(ONION);
            
            c.getPA().sendFrame126((hasTail ? "@str@" : "@dre@") + "A rat's tail", line++);
            c.getPA().sendFrame126((hasMeat ? "@str@" : "@dre@") + "A piece of burnt meat", line++);
            c.getPA().sendFrame126((hasEye ? "@str@" : "@dre@") + "An eye of newt", line++);
            c.getPA().sendFrame126((hasOnion ? "@str@" : "@dre@") + "An onion", line++);
            
        } else if (stage == DRINK_FROM_CAULDRON) {
            c.getPA().sendFrame126("@str@I gave Hetty all the ingredients.", line++);
            c.getPA().sendFrame126("I should drink from the @dre@cauldron @bla@to finish the potion.", line++);
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I've talked to Hetty.", line++);
            c.getPA().sendFrame126("@str@I drank from the cauldron.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("325 Magic Exp", line++);
        }
        c.getPA().showInterface(8134);
    }
}