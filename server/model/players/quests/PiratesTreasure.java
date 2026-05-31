package server.model.players.quests;

import server.model.players.Player;

public class PiratesTreasure extends QuestManager {

    public static final int QUEST_ID = 7;
    public static final String QUEST_NAME = "Pirate's Treasure";

    // Quest Stages
    public static final int NOT_STARTED = 0;
    public static final int GATHERING_RUM = 1;
    public static final int FIND_HECTORS_CHEST = 2;
    public static final int DIG_FOR_TREASURE = 3;
    public static final int COMPLETED = 4;

    // Item IDs
    public static final int KARAMJAN_RUM = 431;
    public static final int HECTORS_KEY = 432;
    public static final int PIRATE_MESSAGE = 433;
    public static final int SPADE = 952;
    public static final int CASKET = 405; 

    public PiratesTreasure(Player c) {
        super(QUEST_NAME, c);
    }

    @Override
    public int getCurrentStage() {
        return c.questStages[QUEST_ID];
    }

    @Override
    public void setStage(int stage) {
        c.questStages[QUEST_ID] = stage;
        System.out.println("Pirate's Treasure Stage set to: " + stage);
    }

    @Override
    public int getTotalStages() {
        return 4;
    }

    @Override
    public boolean hasRequirements() {
        return true; // No requirements!
    }

    @Override
    public void giveRewards() {
        c.questPoints += 2;
        
        // The contents of One-Eyed Hector's Treasure
        c.getItems().addItem(1635, 1); // Gold ring
        c.getItems().addItem(1605, 1); // Emerald
        c.getItems().addItem(995, 450); // 450 Coins
        
        c.getPA().sendFrame126("You have completed Pirate's Treasure!", 12144);
        c.getPA().sendFrame126("2 Quest Points", 12147);
        c.getPA().sendFrame126("One-Eyed Hector's Treasure", 12148);
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
                return "I can start this quest by speaking to Redbeard Frank in Port Sarim.";
            case GATHERING_RUM:
                return "Redbeard Frank wants me to bring him some Karamjan Rum. I need to figure out how to smuggle it off Karamja.";
            case FIND_HECTORS_CHEST:
                return "I gave Frank the rum. He gave me a key to One-Eyed Hector's chest in the Blue Moon Inn in Varrock.";
            case DIG_FOR_TREASURE:
                return "I read the pirate message. I need to dig in Falador park to find the buried treasure.";
            case COMPLETED:
                return "Quest complete! I dug up One-Eyed Hector's buried treasure.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@Pirate's Treasure", 8144);
        
        int line = 8147;
        int stage = c.questStages[QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@Redbeard Frank", line++);
            c.getPA().sendFrame126("on the docks in @dre@Port Sarim@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("None.", line++);
        } else if (stage == GATHERING_RUM) {
            c.getPA().sendFrame126("@str@I've talked to Redbeard Frank.", line++);
            c.getPA().sendFrame126("He told me he will share his treasure with me if I bring", line++);
            c.getPA().sendFrame126("him a bottle of @dre@Karamjan rum@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I will need to figure out a way to smuggle it off the island,", line++);
            c.getPA().sendFrame126("as the customs officers will confiscate it if I try to take", line++);
            c.getPA().sendFrame126("it on the ship. I also can't teleport with it!", line++);
            
            if (c.getItems().playerHasItem(KARAMJAN_RUM)) {
                c.getPA().sendFrame126("", line++);
                c.getPA().sendFrame126("@gre@I have the rum! I should take it to Frank.", line++);
            }
            
        } else if (stage == FIND_HECTORS_CHEST) {
            c.getPA().sendFrame126("@str@I've talked to Redbeard Frank.", line++);
            c.getPA().sendFrame126("@str@I brought Frank the Karamjan rum.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Frank gave me a key to One-Eyed Hector's chest.", line++);
            c.getPA().sendFrame126("I should go to the @dre@Blue Moon Inn @bla@in @dre@Varrock", line++);
            c.getPA().sendFrame126("and open the chest upstairs.", line++);
            
        } else if (stage == DIG_FOR_TREASURE) {
            c.getPA().sendFrame126("@str@I've talked to Redbeard Frank.", line++);
            c.getPA().sendFrame126("@str@I brought Frank the Karamjan rum.", line++);
            c.getPA().sendFrame126("@str@I opened One-Eyed Hector's chest.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I found a pirate message in the chest. It says:", line++);
            c.getPA().sendFrame126("@dre@'Visit the city of the White Knights. In the park,", line++);
            c.getPA().sendFrame126("@dre@Saradomin points to the X which marks the spot.'", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("I should take a @dre@spade @bla@and dig where it points.", line++);
            
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I spoke to Redbeard Frank.", line++);
            c.getPA().sendFrame126("@str@I smuggled him some Karamjan rum.", line++);
            c.getPA().sendFrame126("@str@I opened One-Eyed Hector's chest.", line++);
            c.getPA().sendFrame126("@str@I dug up the buried treasure.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("2 Quest Points", line++);
            c.getPA().sendFrame126("A Gold Ring", line++);
            c.getPA().sendFrame126("An Emerald", line++);
            c.getPA().sendFrame126("450 Coins", line++);
        }
        c.getPA().showInterface(8134);
    }
}