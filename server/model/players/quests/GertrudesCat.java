package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class GertrudesCat extends QuestManager {

    public static final int QUEST_ID = 18; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Gertrude's Cat";

    public GertrudesCat(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Gertrude's Cat Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 7;
    }

    @Override
    public boolean hasRequirements() {
        return c.getSkills().getActualLevel(Skill.FISHING) >= 5;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getSkills().addExperience(1525, Skill.COOKING);
        c.getSkills().addExperience(100, Skill.FISHING);
        c.getItems().addItem(1555, 1); // A kitten! (Standard ID: 1555)
        c.getItems().addItem(1897, 1); // Chocolate Cake
        c.getItems().addItem(1891, 1); // Bowl of Stew
        
        c.getPA().sendFrame126("You have completed Gertrude's Cat!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("1,525 Cooking XP", 12148);
        c.getPA().sendFrame126("100 Fishing XP", 12149);
        c.getPA().sendFrame126("A Kitten!", 12150);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] >= 7;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Gertrude in Varrock.";
            case 1:
                return "I need to speak to Wilough and Shilop in Varrock square about Gertrude's cat.";
            case 2:
                return "I gave the boys 100 coins. I should find Gertrude's cat, Fluffs, in the Lumberyard.";
            case 3:
                return "Fluffs seemed thirsty. I gave her a bucket of milk, but she still seems unhappy.";
            case 4:
                return "Fluffs was hungry. I gave her some seasoned salmon. I should check on her again.";
            case 5:
                return "Fluffs is upset because her kittens are missing. I should check the Lumberyard for them.";
            case 6:
                return "I found Fluffs' kittens and returned them. I should head back to Gertrude.";
            case 7:
                return "Quest complete! I helped Gertrude find her cat and kittens.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Gertrude", line++);
            c.getPA().sendFrame126("at her house west of @red@Varrock@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            String fishColor = c.getSkills().getActualLevel(Skill.FISHING) >= 5 ? "@str@" : "@red@";
            c.getPA().sendFrame126(fishColor + "Minimum Requirement: 5 Fishing", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've talked to Gertrude.", line++);
            c.getPA().sendFrame126("I should speak to @red@Wilough @bla@and @red@Shilop@bla@.", line++);
            c.getPA().sendFrame126("They are playing in @red@Varrock Square@bla@.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I've talked to Wilough and Shilop.", line++);
            c.getPA().sendFrame126("@str@I gave them 100 coins for information.", line++);
            c.getPA().sendFrame126("I should find Gertrude's cat, @red@Fluffs@bla@,", line++);
            c.getPA().sendFrame126("at the @red@Lumberyard@bla@ north-east of Varrock.", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I found Fluffs in the Lumberyard.", line++);
            c.getPA().sendFrame126("@str@I gave her a bucket of milk.", line++);
            c.getPA().sendFrame126("The cat still seems unhappy. Maybe she is @red@hungry@bla@?", line++);
        } else if (stage == 4) {
            c.getPA().sendFrame126("@str@I gave Fluffs a bucket of milk.", line++);
            c.getPA().sendFrame126("@str@I gave her some seasoned salmon.", line++);
            c.getPA().sendFrame126("I should check on @red@Fluffs @bla@again to see if she is okay.", line++);
        } else if (stage == 5) {
            c.getPA().sendFrame126("@str@Fluffs is no longer hungry or thirsty.", line++);
            c.getPA().sendFrame126("She won't leave because she can't find her @red@kittens@bla@.", line++);
            c.getPA().sendFrame126("I should search the @red@Lumberyard @bla@for them.", line++);
        } else if (stage == 6) {
            c.getPA().sendFrame126("@str@I found the kittens and returned them to Fluffs.", line++);
            c.getPA().sendFrame126("Fluffs has returned home. I should go see @red@Gertrude@bla@.", line++);
        } else if (stage == 7) {
            c.getPA().sendFrame126("@str@I helped Gertrude find Fluffs and her kittens.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("1,525 Cooking XP", line++);
            c.getPA().sendFrame126("100 Fishing XP", line++);
            c.getPA().sendFrame126("A Kitten!", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}