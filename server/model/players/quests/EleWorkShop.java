package server.model.players.quests;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class EleWorkShop extends QuestManager {

    public static final int QUEST_ID = 19; 
    public static final String QUEST_NAME = "Elemental Workshop I";

    // --- Quest Stages ---
    public static final int NOT_STARTED = 0;
    public static final int FOUND_BOOK = 1;
    public static final int FOUND_KEY = 2;
    public static final int ENTERED_WORKSHOP = 3;
    public static final int VALVES_TURNED = 4;
    public static final int WHEEL_SPINNING = 5;
    public static final int BELLOWS_REPAIRED = 6;
    public static final int BELLOWS_PUMPING = 7;
    public static final int FOUND_BOWL = 8;
    public static final int FURNACE_HEATED = 9;
    public static final int OBTAINED_ORE = 10;
    public static final int SMELTED_BAR = 11;
    public static final int COMPLETED = 12;

    public EleWorkShop(Player c) {
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
    public boolean hasRequirements() {
        return c.getSkills().getActualLevel(Skill.MINING) >= 20 && 
               c.getSkills().getActualLevel(Skill.SMITHING) >= 20 &&
               c.getSkills().getActualLevel(Skill.CRAFTING) >= 20;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getSkills().addExperience(5000, Skill.SMITHING);
        c.getSkills().addExperience(5000, Skill.CRAFTING);
        
        c.getPA().sendFrame126("You have completed Elemental Workshop I!", 12144);
        c.getPA().sendFrame246(12141, 200, 2890); // 1891 is Cake
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("5,000 Smithing XP", 12148);
        c.getPA().sendFrame126("5,000 Crafting XP", 12149);
        c.getPA().sendFrame126("Ability to make Elemental Shields", 12150);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] >= COMPLETED;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > NOT_STARTED;
    }

    @Override
    public int getTotalStages() {
        return COMPLETED;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case NOT_STARTED:     return "I can start this quest by searching the bookcases in Seers' Village.";
            case FOUND_BOOK:      return "I found a battered book. I should use a knife on the book to find a key.";
            case FOUND_KEY:       return "I found a key in the book. I should use the key on the strange wall in the anvil house.";
            case ENTERED_WORKSHOP:return "I have entered the workshop. I should turn the water valves to the North.";
            case VALVES_TURNED:   return "The valves are open. I should try to start the water wheel by pulling the lever.";
            case WHEEL_SPINNING:  return "The water wheel is spinning. Now I need leather, a needle, and thread to fix the bellows.";
            case BELLOWS_REPAIRED:return "The bellows are repaired. I should pull the lever next to them to start them up.";
            case BELLOWS_PUMPING: return "The machinery is powered. I need to find a stone bowl in the boxes to the NE.";
            case FOUND_BOWL:      return "I have a stone bowl. I should fill it with lava and use it to heat the furnace.";
            case FURNACE_HEATED:  return "The furnace is hot. Now I need to mine some Elemental Ore from the West cavern.";
            case OBTAINED_ORE:    return "I have the ore. I should smelt it with 4 coal in the furnace to get an Elemental Bar.";
            case SMELTED_BAR:     return "I have the bar. I must forge the shield at the workbench while carrying my slashed book.";
            case COMPLETED:       return "Quest complete! I have rediscovered the Elemental Workshop and can forge elemental equipment.";
            default:              return "";
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

        // --- Progress History ---
        if (stage >= FOUND_BOOK)      c.getPA().sendFrame126("@str@I found a battered book in Seers' Village.", line++);
        if (stage >= FOUND_KEY)       c.getPA().sendFrame126("@str@I extracted a battered key from the book.", line++);
        if (stage >= ENTERED_WORKSHOP) c.getPA().sendFrame126("@str@I found the entrance to the hidden workshop.", line++);
        if (stage >= WHEEL_SPINNING)  c.getPA().sendFrame126("@str@I have fixed and started the water wheel.", line++);
        if (stage >= BELLOWS_PUMPING) c.getPA().sendFrame126("@str@I have repaired and started the bellows.", line++);
        if (stage >= FURNACE_HEATED)  c.getPA().sendFrame126("@str@I have used lava to heat the furnace.", line++);
        if (stage >= SMELTED_BAR)     c.getPA().sendFrame126("@str@I have smelted a bar of elemental metal.", line++);
        
        c.getPA().sendFrame126("", line++); 

        // --- Current Objective ---
        if (stage < COMPLETED) {
            c.getPA().sendFrame126("@yel@Current Objective:", line++);
            c.getPA().sendFrame126(getQuestJournalEntry(), line++); 
        } else {
            c.getPA().sendFrame126("@red@     QUEST COMPLETE", line++);
            c.getPA().sendFrame126("REWARDS: 1 QP, 5k Smithing & Crafting XP.", line++);
        }
        
        c.getPA().showInterface(8134);
    }
    public static void readBatteredBook(Player c, int page) {
        c.getPA().sendFrame126("Book of the elemental shield", 903);
        
        if (page == 1) {
            c.getPA().sendFrame126("Within the pages of this", 843);
            c.getPA().sendFrame126("book you will find the", 844);
            c.getPA().sendFrame126("secret to working the", 845);
            c.getPA().sendFrame126("very elements themselves.", 846);
            c.getPA().sendFrame126("Early in the fifth age, a", 847);
            c.getPA().sendFrame126("new ore was discovered.", 848);
            c.getPA().sendFrame126("This ore has a unique", 849);
            c.getPA().sendFrame126("property of absorbing,", 850);
            c.getPA().sendFrame126("transforming or focusing ", 851);
            c.getPA().sendFrame126("elemental energy. A", 852);
            c.getPA().sendFrame126("workshop was erected", 853);
            c.getPA().sendFrame126("close by to work this new", 854);
            c.getPA().sendFrame126("material. The workshop", 855);
            c.getPA().sendFrame126("was set up for artisans", 856);
            c.getPA().sendFrame126("and inventors to be able", 857);
            c.getPA().sendFrame126("to come and create", 858);
            c.getPA().sendFrame126("devices made from the", 859);
            c.getPA().sendFrame126("unique ore, found only in.", 860);
            c.getPA().sendFrame126("the village of the Seers.", 861);
        } else if (page == 2) {
            c.getPA().sendFrame126("After some time of", 843);
            c.getPA().sendFrame126("successful industry the", 844);
            c.getPA().sendFrame126("true power of this ore", 845);
            c.getPA().sendFrame126("became apparent, as", 846);
            c.getPA().sendFrame126("greater and more", 847);
            c.getPA().sendFrame126("powerful weapons were", 848);
            c.getPA().sendFrame126("created. realising the", 849);
            c.getPA().sendFrame126("threat this posed, the magi", 850);
            c.getPA().sendFrame126("of the time closed down", 851);
            c.getPA().sendFrame126("the workshop and bound", 852);
            c.getPA().sendFrame126("it under lock and key,", 853);
            c.getPA().sendFrame126("also trying to destroy all", 854);
            c.getPA().sendFrame126("Knowledge of", 855);
            c.getPA().sendFrame126("manufacturing processes.", 856);
            c.getPA().sendFrame126("Yet this book remains and", 857);
            c.getPA().sendFrame126("you may still find a way", 858);
            c.getPA().sendFrame126("to enter the workshop", 859);
            c.getPA().sendFrame126("within this leather bound", 860);
            c.getPA().sendFrame126("volume.", 861);
            if (c.questStages[QUEST_ID] == 0) {
                c.questStages[QUEST_ID] = 1; 
            }
        }

        // Always clear the footer frames
        c.getPA().sendFrame126("", 862);
        c.getPA().sendFrame126("", 863);
        c.getPA().sendFrame126("", 864);
        
        c.getPA().showInterface(837);
        c.bookPage = page;
    }
    // (Keep your readBatteredBook method below here)
}