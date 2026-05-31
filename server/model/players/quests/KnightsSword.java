package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class KnightsSword extends QuestManager {

    public static final int QUEST_ID = 16; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "The Knight's Sword";

    public KnightsSword(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("The Knight's Sword Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 9;
    }

    @Override
    public boolean hasRequirements() {
        return c.getSkills().getActualLevel(Skill.MINING) >= 10;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        // This quest is famous for its 12,725 Smithing XP!
        c.getSkills().addExperience(12725, Skill.SMITHING);
        
        c.getPA().sendFrame126("You have completed The Knight's Sword!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("12,725 Smithing XP", 12148);
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
                return "I can start this quest by speaking to the Squire in Falador Castle courtyard.";
            case 1:
                return "The Squire lost Vyvin's sword. I should speak to Reldo in Varrock library.";
            case 2:
                return "Reldo told me to find an Imcando dwarf. I need a Red Berry Pie to make him talk.";
            case 3:
                return "I found Thurgo and gave him pie. I should ask if he'll make a new sword.";
            case 4:
                return "Thurgo needs a picture of the sword. I should ask the Squire about one.";
            case 5:
                return "The picture is in a cupboard in Sir Vyvin's room. I must be careful!";
            case 6:
                return "I have the picture. I should bring it to Thurgo.";
            case 7:
                return "Thurgo needs 2 iron bars and 1 blurite ore to forge the sword.";
            case 8:
                return "Thurgo made the sword! I should return it to the Squire in Falador.";
            case 9:
                return "Quest complete! I helped the Squire and gained massive Smithing experience.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to the @red@Squire", line++);
            c.getPA().sendFrame126("in the courtyard of the @red@White Knight's castle@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            String minColor = c.getSkills().getActualLevel(Skill.MINING) >= 10 ? "@str@" : "@red@";
            c.getPA().sendFrame126(minColor + "Requirement: 10 Mining", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@The squire has lost Sir Vyvin's sword.", line++);
            c.getPA().sendFrame126("He suggested that I start by speaking to @red@Reldo@bla@,", line++);
            c.getPA().sendFrame126("the librarian in @red@Varrock Castle@bla@.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I spoke to Reldo about the Imcando dwarves.", line++);
            c.getPA().sendFrame126("Reldo told me a dwarf lives near the Asgarnian peninsula.", line++);
            c.getPA().sendFrame126("I should bring him some @red@Red Berry Pie@bla@ to get", line++);
            c.getPA().sendFrame126("him to talk to me.", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I found Thurgo and gave him Red Berry Pie.", line++);
            c.getPA().sendFrame126("Now that he likes me, I should find out if", line++);
            c.getPA().sendFrame126("he will make the replacement sword for me.", line++);
        } else if (stage == 4) {
            c.getPA().sendFrame126("@str@Thurgo agreed to help me forge the sword.", line++);
            c.getPA().sendFrame126("Thurgo says he needs a @red@picture @bla@of the sword.", line++);
            c.getPA().sendFrame126("Maybe the @red@Squire @bla@will have one?", line++);
        } else if (stage == 5) {
            c.getPA().sendFrame126("@str@Thurgo needs a picture of the sword.", line++);
            c.getPA().sendFrame126("The Squire thinks Sir Vyvin keeps a picture in a", line++);
            c.getPA().sendFrame126("@red@cupboard @bla@in his room. I must not get caught!", line++);
        } else if (stage == 6) {
            c.getPA().sendFrame126("@str@I managed to find the sword portrait.", line++);
            c.getPA().sendFrame126("I should bring the picture back to @red@Thurgo@bla@.", line++);
        } else if (stage == 7) {
            c.getPA().sendFrame126("@str@Thurgo has the picture, but needs materials.", line++);
            c.getPA().sendFrame126("I need to bring him @red@2 Iron Bars @bla@and @red@1 Blurite Ore@bla@.", line++);
            c.getPA().sendFrame126("Blurite can be found in the icy caverns nearby.", line++);
        } else if (stage == 8) {
            c.getPA().sendFrame126("@str@Thurgo has forged the Blurite Sword!", line++);
            c.getPA().sendFrame126("I should bring it back to the @red@Squire @bla@for my reward.", line++);
        } else if (stage == 9) {
            c.getPA().sendFrame126("@str@I brought the replacement sword to the Squire.", line++);
            c.getPA().sendFrame126("@str@Sir Vyvin is none the wiser.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Reward: 1 Quest Point & 12,725 Smithing XP", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}