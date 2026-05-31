package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class DoricsQuest extends QuestManager {

    public static final int QUEST_ID = 20; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Doric's Quest";

    public DoricsQuest(Player c) {
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
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return true; // Recommended 15 Mining, but not strictly required to start
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getSkills().addExperience(26000, Skill.MINING);
        c.getItems().addItem(995, 180); // 180 Coins
        
        c.getPA().sendFrame126("You have completed Doric's Quest!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("26,000 Mining XP", 12148);
        c.getPA().sendFrame126("180 Coins", 12149);
        c.getPA().sendFrame126("Use of Doric's Anvils", 12150);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] >= 3;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Doric north-west of Falador.";
            case 1:
                return "Doric has asked me to bring him some materials: 6 Clay, 4 Copper ore, and 2 Iron ore.";
            case 2:
                return "I have gathered the materials. I should return to Doric.";
            case 3:
                return "Quest complete! I helped Doric and can now use his anvils.";
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
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == 0) {
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Doric", line++);
            c.getPA().sendFrame126("North-west of @red@Falador@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            String mineColor = c.getSkills().getActualLevel(Skill.MINING) >= 15 ? "@str@" : "@red@";
            c.getPA().sendFrame126(mineColor + "Recommended Level: 15 Mining", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've talked to Doric.", line++);
            c.getPA().sendFrame126("He wants me to gather the following materials:", line++);
            c.getPA().sendFrame126("", line++);
            
            // Item tracking
            c.getPA().sendFrame126((c.getItems().playerHasItem(434, 6) ? "@str@" : "@red@") + "6 Clay", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(436, 4) ? "@str@" : "@red@") + "4 Copper Ore", line++);
            c.getPA().sendFrame126((c.getItems().playerHasItem(440, 2) ? "@str@" : "@red@") + "2 Iron Ore", line++);
            
            c.getPA().sendFrame126("", line++);
            if (c.getItems().playerHasItem(434, 6) && c.getItems().playerHasItem(436, 4) && c.getItems().playerHasItem(440, 2)) {
                c.getPA().sendFrame126("@gre@I have all the items! I should talk to Doric.", line++);
            } else {
                c.getPA().sendFrame126("I can mine these in the @red@Rimmington mine@bla@.", line++);
            }
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I talked to Doric.", line++);
            c.getPA().sendFrame126("@str@I gave Doric his items.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I should speak to @red@Doric @bla@to finish the quest.", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I talked to Doric.", line++);
            c.getPA().sendFrame126("@str@I gave him his items.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("26,000 Mining XP", line++);
            c.getPA().sendFrame126("180 Coins", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}