package server.model.players.quests;

import server.model.players.Player;

public class RestlessGhost extends QuestManager {

    public static final int QUEST_ID = 2; // Adjust to your quest system ID
    public static final String QUEST_NAME = "Restless Ghost";
    
    public static final int NOT_STARTED = 0;
    public static final int SPOKE_TO_AERECK = 1;
    public static final int GOT_AMULET = 2;
    public static final int SPOKE_TO_GHOST = 3;
    public static final int GOT_SKULL = 4;
    public static final int COMPLETED = 5;

    public static final int GHOSTSPEAK_AMULET = 552;
    public static final int GHOSTS_SKULL = 314;

    public RestlessGhost(Player c) {
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
        System.out.println("Restless Ghost Stage set to: " + stage);
    }

    public void updateQuestConfigs() {
        int configValue = 0;
        
        // If they are on the step to talk to the ghost, or get the skull
        if (c.questStages[RestlessGhost.QUEST_ID] == SPOKE_TO_GHOST) {
            // Set bit 2 to 1 (Turns Altar 2146 into Altar 15050 with Skull)
            configValue |= (1 << 2); 
        } 
        // If they already took the skull
        else if (c.questStages[RestlessGhost.QUEST_ID] >= GOT_SKULL) {
            // Set bit 2 to 1 (Altar state) AND Set bit 0 to 1 (Skeleton gone state)
            configValue |= (1 << 2);
            configValue |= (1 << 0);
        }
        
        // Send the Varp to the client (Packet 36)
        c.getPA().sendConfig(728, configValue);
    }

    @Override
    public int getTotalStages() {
        return 5;
    }

    @Override
    public boolean hasRequirements() {
        return true;
    }

    @Override
    public void giveRewards() {
        c.getPA().addSkillXP(1125, 5); // 1,125 Prayer XP
        c.questPoints += 1;
        c.getPA().sendFrame126("You have completed The Restless Ghost!", 12144);
        c.getPA().sendFrame126("1 Quest Point", 12147);
        c.getPA().sendFrame126("1,125 Prayer XP", 12148);
        c.getPA().sendFrame126("Ghostspeak Amulet", 12149);
        c.getPA().showInterface(12140);
    }

    @Override
    public boolean isCompleted() {
        return c.questStages[RestlessGhost.QUEST_ID] == COMPLETED;
    }

    @Override
    public boolean isStarted() {
        return c.questStages[RestlessGhost.QUEST_ID] > NOT_STARTED;
    }

    @Override
    public String getQuestJournalEntry() {
        switch (c.questStages[RestlessGhost.QUEST_ID]) {
            case NOT_STARTED:
                return "I can start this quest by speaking to Father Aereck in the Lumbridge Chapel.";
            case SPOKE_TO_AERECK:
                return "Father Aereck asked me to get rid of a ghost. I should speak to Father Urhney in the swamp.";
            case GOT_AMULET:
                return "Father Urhney gave me a Ghostspeak amulet. I should go talk to the ghost in the Lumbridge graveyard.";
            case SPOKE_TO_GHOST:
                return "The ghost told me a warlock stole his skull. I need to find it in the Wizards' Tower.";
            case GOT_SKULL:
                return "I found the ghost's skull! I should return it to the ghost's coffin in the graveyard.";
            case COMPLETED:
                return "Quest complete! I returned the skull and freed the Restless Ghost.";
            default:
                return "";
        }
    }

    @Override
    public void showQuestScroll(Player c) {
        for (int i = 8144; i < 8195; i++) {
            c.getPA().sendFrame126("", i);
        }
        c.getPA().sendFrame126("@dre@The Restless Ghost", 8144);
        
        int line = 8147;
        int stage = c.questStages[RestlessGhost.QUEST_ID];

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@Father Aereck", line++);
            c.getPA().sendFrame126("in the @dre@Lumbridge Chapel.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("None.", line++);
        } else if (stage == SPOKE_TO_AERECK) {
            c.getPA().sendFrame126("@str@I've talked to Father Aereck.", line++);
            c.getPA().sendFrame126("I should speak to @dre@Father Urhney@bla@ in the Lumbridge swamp.", line++);
        } else if (stage == GOT_AMULET) {
            c.getPA().sendFrame126("@str@I've talked to Father Urhney.", line++);
            c.getPA().sendFrame126("@str@He gave me a ghostspeak amulet.", line++);
            c.getPA().sendFrame126("I should speak to the @dre@Restless Ghost @bla@in the graveyard.", line++);
        } else if (stage == SPOKE_TO_GHOST) {
            c.getPA().sendFrame126("@str@I've talked to the Ghost.", line++);
            c.getPA().sendFrame126("I should travel to the Wizards' Tower and find his @dre@skull@bla@.", line++);
        } else if (stage == GOT_SKULL) {
            c.getPA().sendFrame126("@str@I found the ghost's skull.", line++);
            c.getPA().sendFrame126("I should travel back and put it in the ghost's @dre@coffin@bla@.", line++);
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I've set the skull in the coffin.", line++);
            c.getPA().sendFrame126("@str@I've freed the ghost.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("1 Quest Point", line++);
            c.getPA().sendFrame126("1,125 Prayer Exp", line++);
            c.getPA().sendFrame126("Ghostspeak Amulet", line++);
        }
        c.getPA().showInterface(8134);
    }
}