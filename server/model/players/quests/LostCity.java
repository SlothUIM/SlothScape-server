package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class LostCity extends QuestManager {

    public static final int QUEST_ID = 15; // Change this to match your questStages array index!
    public static final String QUEST_NAME = "Lost City";

    public LostCity(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Lost City Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 3;
    }

    @Override
    public boolean hasRequirements() {
        return c.getSkills().getActualLevel(Skill.CRAFTING) >= 31 && 
               c.getSkills().getActualLevel(Skill.WOODCUTTING) >= 36;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 3;
        // Access to Zanaris is usually handled by checking (isCompleted()) in the Shed door logic
        
        c.getPA().sendFrame126("You have completed Lost City!", 12144);
        c.getPA().sendFrame126("3 Quest Points", 12147);
        c.getPA().sendFrame126("Access to Zanaris", 12148);
        c.getPA().sendFrame126("Ability to wield Dragon Longswords", 12149);
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
                return "I can start this quest by speaking to the four adventurers west of Lumbridge swamp.";
            case 1:
                return "I've talked to the adventurers. I need to find the Leprechaun hiding in the trees nearby.";
            case 2:
                return "The Leprechaun told me to go to Entrana and cut a branch from the Dramen tree to make a staff.";
            case 3:
                return "Quest complete! I have gained access to the hidden city of Zanaris.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to the @red@four adventurers", line++);
            c.getPA().sendFrame126("west of @red@Lumbridge swamp.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            
            // Skill Requirements check
            String craftColor = c.getSkills().getActualLevel(Skill.CRAFTING) >= 31 ? "@str@" : "@red@";
            String wcColor = c.getSkills().getActualLevel(Skill.WOODCUTTING) >= 36 ? "@str@" : "@red@";
            
            c.getPA().sendFrame126(craftColor + "31 Crafting", line++);
            c.getPA().sendFrame126(wcColor + "36 Woodcutting", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I must be able to defeat a @red@high-level tree spirit", line++);
            c.getPA().sendFrame126("without the use of weapons or armour.", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("@str@I've talked to the adventurers who told me", line++);
            c.getPA().sendFrame126("@str@that there is a leprechaun hiding in the trees nearby", line++);
            c.getPA().sendFrame126("@str@who knows how to locate the lost city.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I need to find and catch that @red@Leprechaun@bla@.", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I talked to the leprechaun who told me that", line++);
            c.getPA().sendFrame126("@str@Zanaris can be accessed through the shed in lumbridge", line++);
            c.getPA().sendFrame126("@str@Swamp through the use of a Dramen staff.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I need to travel to @red@Entrana @bla@and defeat the @red@Tree Spirit@bla@.", line++);
            c.getPA().sendFrame126("Then, I must cut a branch from the @red@Dramen Tree@bla@.", line++);
            c.getPA().sendFrame126("Remember: Entrana allows @red@no weapons or armour!", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I talked to the leprechaun.", line++);
            c.getPA().sendFrame126("@str@I defeated the Spirit Tree on Entrana.", line++);
            c.getPA().sendFrame126("@str@I crafted a Dramen staff.", line++);
            c.getPA().sendFrame126("@str@I used the staff to enter the Lumbridge shed.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("3 Quest Points", line++);
            c.getPA().sendFrame126("Access to Zanaris", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}