package server.model.players.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.Config;

public class CooksAssistant extends QuestManager {
    public static final int QUEST_ID = 1; // Adjust based on your quest list
    public static final String QUEST_NAME = "Cook's Assistant";
    
    public static final int NOT_STARTED = 0, STARTED = 1, ITEMS_GATHERED = 2, COMPLETED = 3;
    
    // Ingredient IDs
    private static final int EGG = 1944;
    private static final int MILK = 1927;
    private static final int FLOUR = 1933;

    public CooksAssistant(Player c) {
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
        System.out.println("Cook's Assistant Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return true; // Novice quest, no requirements
    }
    public void clearInterface() {

        c.getPA().sendFrame126("", 12144);
        c.getPA().sendFrame246(12148, 200, -1); // 1891 is Cake
        c.getPA().sendFrame126("", 12147);
        c.getPA().sendFrame126("", 12148);
        c.getPA().sendFrame126("", 12149);
    }
    @Override
    public void giveRewards() {
    	clearInterface();
        c.getPA().addSkillXP(300, 7); // 300 Cooking XP
        c.questPoints += 1;
        c.getPA().sendFrame126("You have completed Cook's Assistant!", 12144);
        c.getPA().sendFrame246(12148, 200, 1891); // 1891 is Cake
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("300 Cooking XP", 12148);
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
                return "I can start this quest by speaking to the Cook in the Lumbridge Castle kitchen.";
            case STARTED:
                return "The Cook needs an egg, a bucket of milk, and a pot of flour for the Duke's cake.";
            case ITEMS_GATHERED:
                return "I have all the ingredients! I should return them to the Cook.";
            case COMPLETED:
                return "Quest complete! I helped the Cook bake the Duke's birthday cake.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@Cook's Assistant", 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to the @dre@Cook", line++);
            c.getPA().sendFrame126("in the @dre@Lumbridge Castle kitchen.", line++);
        } else if (stage == STARTED) {
            c.getPA().sendFrame126("@str@I have spoken to the Cook.", line++);
            c.getPA().sendFrame126("He needs the following ingredients:", line++);
            line++;
            c.getPA().sendFrame126((c.getItems().playerHasItem(MILK) ? "@str@" : "@red@") + "One bucket of milk", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(EGG) ? "@str@" : "@red@") + "One egg", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(FLOUR) ? "@str@" : "@red@") + "One pot of flour", line++);
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@gre@QUEST COMPLETE!", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I have helped the Cook bake the Duke's cake.", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("300 Cooking XP", line++);
        }
        c.getPA().showInterface(8134);
    }
    
    // This is the implementation of the dialogue logic within the quest class
    @Override
    public void handleDialogue(Player c, int dialogueId) {
        // Logic handled in the dedicated Cook NPC class below for cleanliness
    }
}