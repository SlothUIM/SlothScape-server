package server.model.players.quests;

import server.model.players.Player;

public class RuneMysteries extends QuestManager {

    public static final int QUEST_ID = 12; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Rune Mysteries";

    public RuneMysteries(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Rune Mysteries Stage set to: " + stage);
        QuestAssistant.sendStages(c);
    }

    @Override
    public int getTotalStages() {
        return 4;
    }

    @Override
    public boolean hasRequirements() {
        return true; // No requirements
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getItems().addItem(1438, 1); // Air talisman (Standard ID: 1438)
        
        c.getPA().sendFrame126("You have completed Rune Mysteries!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("An Air Talisman", 12148);
        c.getPA().sendFrame126("Runecrafting Skill", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] == 4;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Duke Horacio on the 2nd floor of Lumbridge Castle.";
            case 1:
                return "I've talked to the Duke. I need to take the Air Talisman to the Head Wizard in the Wizards' Tower.";
            case 2:
                return "I gave the talisman to Sedridor. He gave me some research notes to deliver to Aubury in Varrock.";
            case 3:
                return "I gave the notes to Aubury. He gave me some translation notes to take back to Sedridor.";
            case 4:
                return "Quest complete! I unlocked the Runecrafting skill and received an Air Talisman.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Duke Horacio", line++);
            c.getPA().sendFrame126("who is located on the 2nd floor of the @red@Lumbridge Castle.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("There are no minimum requirements.", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've talked to the duke", line++);
            c.getPA().sendFrame126("I should take the talisman to the @red@Head Wizard.", line++);
            c.getPA().sendFrame126("He can be found in the @red@Wizards' Tower.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I've talked to Sedridor", line++);
            c.getPA().sendFrame126("@str@I gave him the talisman", line++);
            c.getPA().sendFrame126("I should bring the notes to @red@Aubury @bla@in @red@Varrock.", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I've talked to Aubury.", line++);
            c.getPA().sendFrame126("@str@I gave him the notes", line++);
            c.getPA().sendFrame126("I should go back to the @red@Wizards' Tower", line++);
            c.getPA().sendFrame126("and give Aubury's notes to @red@Sedridor.", line++);
        } else if (stage == 4) {
            c.getPA().sendFrame126("@str@I talked to Sedridor", line++);
            c.getPA().sendFrame126("@str@I gave him his items.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("As a reward, I gained 1 Quest point", line++);
            c.getPA().sendFrame126("And an air talisman.", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}