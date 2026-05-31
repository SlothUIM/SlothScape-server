package server.model.players.quests;

import server.model.players.Player;

public class MerlinsCrystal extends QuestManager {

    public static final int QUEST_ID = 21; // Adjust this ID to match your next available quest array slot
    public static final String QUEST_NAME = "Merlin's Crystal";

    // Quest Stages
    public static final int NOT_STARTED = 0;
    public static final int INVESTIGATING = 1;
    public static final int INFILTRATING_KEEP = 2;
    public static final int DEFEATED_MORDRED = 3;
    public static final int GATHERING_ITEMS = 4;
    public static final int SUMMONED_THRANTAX = 5;
    public static final int FREED_MERLIN = 6;
    public static final int COMPLETED = 7;

    // Item IDs
    public static final int EXCALIBUR = 35;
    public static final int BLACK_CANDLE_LIT = 328;
    public static final int BAT_BONES = 530;
    public static final int BREAD = 2309;
    public static final int BUCKET_OF_WAX = 32;

    public MerlinsCrystal(Player c) {
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
        return 7;
    }

    @Override
    public boolean hasRequirements() {
        // Merlin's Crystal has no hard stat requirements, but requires combat capability
        return true;
    }

    @Override
    public void giveRewards() {
        c.questPoints += 6;
        // Optionally give Excalibur if not in inventory/bank
        if (!c.getItems().playerHasItem(EXCALIBUR)) {
            c.getItems().addItem(EXCALIBUR, 1);
        }

        c.getPA().sendFrame126("You have completed Merlin's Crystal!", 12144);
        c.getPA().sendFrame126("6 Quest Points", 12147);
        c.getPA().sendFrame126("Excalibur", 12148);
        c.getPA().sendFrame126("Honorary Knight of the Round Table", 12149);
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
                return "Speak to King Arthur in Camelot to begin this quest.";
            case INVESTIGATING:
                return "I should speak to Sir Gawain and Sir Lancelot about how to rescue Merlin.";
            case INFILTRATING_KEEP:
                return "Sir Lancelot said deliveries arrive by boat from Catherby. I should hide in Arhein's crates.";
            case DEFEATED_MORDRED:
                return "I must defeat Sir Mordred in Keep Le Faye to force Morgan Le Faye to help me.";
            case GATHERING_ITEMS:
                return "I need a lit Black Candle, Bat Bones, Excalibur, and the magic words from a Chaos Altar.";
            case SUMMONED_THRANTAX:
                return "I have everything I need. I must perform the summoning ritual outside Camelot castle.";
            case FREED_MERLIN:
                return "I have Thrantax's boon. I must climb the Camelot tower and shatter the crystal with Excalibur.";
            case COMPLETED:
                return "I successfully freed Merlin from his crystal prison and was made an Honorary Knight!";
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

        if (stage == NOT_STARTED) {
            c.getPA().sendFrame126("I can start this quest by speaking to @dre@King Arthur", line++);
            c.getPA().sendFrame126("at his castle in @dre@Camelot@bla@.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("Minimum Requirements:", line++);
            c.getPA().sendFrame126("Ability to defeat a Level 39 Sir Mordred.", line++);
        } else if (stage == INVESTIGATING) {
            c.getPA().sendFrame126("@str@I agreed to help rescue Merlin for King Arthur.", line++);
            c.getPA().sendFrame126("I need to figure out how to rescue him.", line++);
            c.getPA().sendFrame126("I should ask the other @dre@Knights of the Round Table", line++);
            c.getPA().sendFrame126("for advice. @dre@Sir Gawain@bla@ and @dre@Sir Lancelot@bla@ might know.", line++);
        } else if (stage == INFILTRATING_KEEP) {
            c.getPA().sendFrame126("@str@I discovered Morgan Le Faye trapped Merlin.", line++);
            c.getPA().sendFrame126("I need to infiltrate @dre@Keep Le Faye@bla@.", line++);
            c.getPA().sendFrame126("Sir Lancelot mentioned a merchant in @dre@Catherby", line++);
            c.getPA().sendFrame126("delivers cargo to the keep by boat. Maybe I can hide.", line++);
        } else if (stage == DEFEATED_MORDRED) {
            c.getPA().sendFrame126("@str@I successfully infiltrated Keep Le Faye.", line++);
            c.getPA().sendFrame126("I need to find a way to get information from", line++);
            c.getPA().sendFrame126("@dre@Morgan Le Faye@bla@. I should search the top floor.", line++);
        } else if (stage == GATHERING_ITEMS) {
            c.getPA().sendFrame126("@str@I defeated Mordred and spared his life.", line++);
            c.getPA().sendFrame126("Morgan Le Faye told me how to free Merlin.", line++);
            c.getPA().sendFrame126("To summon the spirit @dre@Thrantax@bla@, I need:", line++);

            if (c.getItems().playerHasItem(BAT_BONES)) {
                c.getPA().sendFrame126("@str@Bat Bones", line++);
            } else {
                c.getPA().sendFrame126("@red@Bat Bones", line++);
            }

            if (c.getItems().playerHasItem(BLACK_CANDLE_LIT) || c.getItems().playerHasItem(326)) { // 326 = unlit
                c.getPA().sendFrame126("@str@Black Candle", line++);
            } else {
                c.getPA().sendFrame126("@red@Black Candle (Requires a bucket of wax)", line++);
            }

            if (c.getItems().playerHasItem(EXCALIBUR)) {
                c.getPA().sendFrame126("@str@Excalibur", line++);
            } else {
                c.getPA().sendFrame126("@red@Excalibur (Held by the Lady of the Lake in Taverley)", line++);
            }

            c.getPA().sendFrame126("@red@The summoning words from the Zamorak Chaos Altar.", line++);
        } else if (stage == SUMMONED_THRANTAX) {
            c.getPA().sendFrame126("@str@I gathered the items and the incantation.", line++);
            c.getPA().sendFrame126("I must stand in the summoning circle outside of", line++);
            c.getPA().sendFrame126("Camelot, light my @dre@Black Candle@bla@, drop the", line++);
            c.getPA().sendFrame126("@dre@Bat Bones@bla@, and chant the incantation.", line++);
        } else if (stage == FREED_MERLIN) {
            c.getPA().sendFrame126("@str@I successfully summoned Thrantax the Mighty.", line++);
            c.getPA().sendFrame126("The spirit granted my boon. I just need to climb", line++);
            c.getPA().sendFrame126("the Camelot tower and shatter the crystal", line++);
            c.getPA().sendFrame126("using @dre@Excalibur@bla@.", line++);
        } else if (stage == COMPLETED) {
            c.getPA().sendFrame126("@str@I spoke to King Arthur to begin the quest.", line++);
            c.getPA().sendFrame126("@str@I infiltrated Keep Le Faye and defeated Sir Mordred.", line++);
            c.getPA().sendFrame126("@str@I gathered the ritual items and retrieved Excalibur.", line++);
            c.getPA().sendFrame126("@str@I summoned Thrantax and freed Merlin.", line++);
            c.getPA().sendFrame126("", line++);
            c.getPA().sendFrame126("@gre@QUEST COMPLETE", line++);
            c.getPA().sendFrame126("Rewards:", line++);
            c.getPA().sendFrame126("6 Quest Points", line++);
            c.getPA().sendFrame126("Excalibur", line++);
            c.getPA().sendFrame126("Honorary Knight Status", line++);
        }
        c.getPA().showInterface(8134);
    }
}