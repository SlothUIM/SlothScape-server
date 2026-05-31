package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class ImpCatcher extends QuestManager {

    public static final int QUEST_ID = 17; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Imp Catcher";

    public ImpCatcher(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
    }

    @Override
    public int getTotalStages() {
        return 2;
    }

    @Override
    public boolean hasRequirements() {
        return true; // No requirements
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getSkills().addExperience(875, Skill.MAGIC);
        c.getItems().addItem(1470, 1); // Amulet of Accuracy
        
        c.getPA().sendFrame126("You have completed Imp Catcher!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("875 Magic XP", 12148);
        c.getPA().sendFrame126("Amulet of Accuracy", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] >= 2;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Wizard Mizgog in the Wizards' Tower.";
            case 1:
                return "Wizard Mizgog has asked me to retrieve four beads stolen by imps: Red, Yellow, Black, and White.";
            case 2:
                return "Quest complete! I returned the beads to Mizgog and received an Amulet of Accuracy.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8295; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@" + QUEST_NAME, 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == 0) {
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Wizard Mizgog", line++);
            c.getPA().sendFrame126("who is in the @red@Wizards' Tower.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("There are no requirements for this quest.", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've spoken to Wizard Mizgog.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Mizgog needs the following beads from Imps:", line++);
            
            // Highlight green if the player has the item in inventory
            c.getPA().sendFrame126((c.getItems().playerHasItem(1470) ? "@gre@" : "@red@") + "Red bead", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(1472) ? "@gre@" : "@red@") + "Yellow bead", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(1474) ? "@gre@" : "@red@") + "Black bead", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(1476) ? "@gre@" : "@red@") + "White bead", line++);
            
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I should take them all to him at the @red@Wizards' Tower.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I've spoken to Wizard Mizgog.", line++);
            c.getPA().sendFrame126("@str@I have collected all four beads.", line++);
            c.getPA().sendFrame126("@str@I returned them to the Wizards' Tower.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("875 Magic XP", line++);
            c.getPA().sendFrame126("Amulet of Accuracy", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}