package server.model.players.quests;

import server.model.players.Player;

public class ShieldArrav extends QuestManager {

    public static final int QUEST_ID = 11; // Change this to match your questStages array index if needed!
    public static final String QUEST_NAME = "Shield of Arrav";

    public ShieldArrav(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Shield of Arrav Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 8;
    }

    @Override
    public boolean hasRequirements() {
        return true;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 1;
        c.getItems().addItem(995, 1200); // 1,200 Coins
        
        c.getPA().sendFrame126("You have completed Shield of Arrav!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("1,200 Coins", 12148);
        c.getPA().sendFrame126("", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[QUEST_ID] == 8;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[QUEST_ID] > 0;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[QUEST_ID]) {
            case 0:
                return "I can start this quest by speaking to Reldo in Varrock's Palace Library, or Charlie the Tramp.";
            case 1:
                return "I need to find 'The Shield of Arrav' book in Varrock's Palace Library.";
            case 2:
                return "I found 'The Shield of Arrav' book. I should speak to Reldo.";
            case 3:
                return "I've spoken with Reldo. I should go speak with Baraek.";
            case 4:
                return "Baraek told me the Phoenix Gang hideout is in South Eastern Varrock. I should check it out.";
            case 5:
                return "I found the hideout. I need to go to the Blue Moon Inn and obtain the intelligence report.";
            case 6:
                return "I obtained the intelligence report. I need to find the Shield and take it to Curator Haig Halen.";
            case 7:
                return "I turned in the shield. I need to take this certificate to King Roald for my reward.";
            case 8:
                return "Quest complete! I received 1,200 coins for finding the Shield of Arrav.";
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
            c.getPA().sendFrame126("I can start this quest by speaking to @red@Reldo @bla@in @red@Varrock's", line++);
            c.getPA().sendFrame126("@red@Palace Library@bla@, or by speaking to @red@Charlie the Tramp @bla@near", line++);
            c.getPA().sendFrame126("the @red@Blue Moon Inn @bla@in @red@Varrock.", line++);
            c.getPA().sendFrame126("I will need a friend to help me and some combat experience", line++);
            c.getPA().sendFrame126("may be an advantage.", line++);
        } else if (stage == 1) {
            c.getPA().sendFrame126("I need to find @red@'The Shield of Arrav' @bla@book", line++);
            c.getPA().sendFrame126("in @red@Varrock's Palace Library", line++);
        } else if (stage == 2) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("I should speak to @red@Reldo", line++);
        } else if (stage == 3) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++); // Fixed typo from Raldo
            c.getPA().sendFrame126("Lets go speak with @red@Baraek", line++);
        } else if (stage == 4) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++);
            c.getPA().sendFrame126("@str@I've spoken with Baraek", line++);
            c.getPA().sendFrame126("He told me where I can find the Phoenix Gang hideout,", line++);
            c.getPA().sendFrame126("He said it was located in the South Eastern side of Varrock,", line++);
            c.getPA().sendFrame126("I should check it out.", line++);
        } else if (stage == 5) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++);
            c.getPA().sendFrame126("@str@I've spoken with Baraek", line++);
            c.getPA().sendFrame126("@str@I found the hideout", line++);
            c.getPA().sendFrame126("I need to goto the @red@Blue Moon Inn", line++);
            c.getPA().sendFrame126("and obtain the intelligence report.", line++);
        } else if (stage == 6) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++);
            c.getPA().sendFrame126("@str@I've spoken with Baraek", line++);
            c.getPA().sendFrame126("@str@I found the hideout", line++);
            c.getPA().sendFrame126("@str@I obtained the intelligence report", line++);
            c.getPA().sendFrame126("I need to find the Shield", line++);
            c.getPA().sendFrame126("and take it to @red@Curator Haig Halen", line++);
            c.getPA().sendFrame126("he should be at @red@Varrock Museum", line++);
        } else if (stage == 7) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++);
            c.getPA().sendFrame126("@str@I've spoken with Baraek", line++);
            c.getPA().sendFrame126("@str@I found the hideout", line++);
            c.getPA().sendFrame126("@str@I obtained the intelligence report", line++);
            c.getPA().sendFrame126("@str@I turned in the shield", line++);
            c.getPA().sendFrame126("I need to take this certificate", line++);
            c.getPA().sendFrame126("to @red@King Roald @bla@for my reward", line++);
        } else if (stage == 8) {
            c.getPA().sendFrame126("@str@I found 'The Shield of Arrav' book", line++);
            c.getPA().sendFrame126("@str@I've spoken with Reldo", line++);
            c.getPA().sendFrame126("@str@I've spoken with Baraek", line++);
            c.getPA().sendFrame126("@str@I found the hideout", line++);
            c.getPA().sendFrame126("@str@I obtained the intelligence report", line++);
            c.getPA().sendFrame126("@str@I turned in the shield", line++);
            c.getPA().sendFrame126("@str@I got a certificate and", line++);
            c.getPA().sendFrame126("@str@turned it in for my reward", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@red@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("REWARDS:", line++);
            c.getPA().sendFrame126("1,200 coins", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
        }
        
        c.getPA().showInterface(8134);
    }
}