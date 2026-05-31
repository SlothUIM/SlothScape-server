package server.model.players.quests;

import java.util.List;

import server.model.players.Player;

import java.util.ArrayList;

/**
 * Base class for all quests. Extend this class to implement custom quest logic.
 */
public abstract class QuestManager {

    protected String questName;
    protected Player c; // Add this globally for all quests

    /**
     * Constructor for base quest. Set the quest name.
     *
     * @param questName The name of the quest.
     */
    public QuestManager(String questName, Player c) {
        this.questName = questName;
        this.c = c; // Now 'c' is available in every quest subclass
    }

    /**
     * Get the name of the quest.
     * 
     * @return The name of the quest.
     */
    public String getQuestName() {
        return questName;
    }

    /**
     * Called when the quest is started. Override to implement custom start logic.
     */
    public void startQuest() {
        // Default: do nothing
    }

    /**
     * Called every time the quest is updated (e.g., progress is made).
     * Override to add custom update logic.
     */
    public void updateQuest() {
        // Default: do nothing
    }

    /**
     * Called when the quest is completed. Override to implement custom complete logic.
     */
    public void completeQuest() {
        // Default: do nothing
    }

    /**
     * Returns whether the quest is completed.
     * Must be implemented by subclasses.
     */
    public abstract boolean isCompleted();

    /**
     * Reset quest progress. Override to implement custom reset logic.
     */
    public void resetQuest() {
        // Default: do nothing
    }

    /**
     * Returns a message or description of the current quest stage.
     * Override to provide custom stage text.
     */
    public String getCurrentStageDescription() {
        return "No description available.";
    }
    // Stages
    public abstract int getCurrentStage();
    public abstract void setStage(int stage);
    public abstract int getTotalStages();
    public void advanceStage() {
        setStage(getCurrentStage() + 1);
    }
    public void handleDialogue(Player c, int dialogueId) {}
    // Requirements and Rewards
    public abstract boolean hasRequirements();
    public abstract void giveRewards();
    public List<String> getRequirementsDescription() { return new ArrayList<>(); }
    public boolean handleDialogueAction(Player c, int actionButtonId) {return false;}
    // Persistence
    public void saveProgress() { /* optional, override if needed */ }
    public void loadProgress() { /* optional, override if needed */ }

    public void showQuestScroll(Player c) {}
    // Event hooks
    public void onNpcInteraction(int npcId) { }
    public void onItemUsed(int itemId) { }
    public void onObjectInteract(int objectId) { }

    // UI
    public String getQuestJournalEntry() { return ""; }
    public String getCompletionMessage() { return ""; }

    // State queries
    public boolean isStarted() { return getCurrentStage() > 0; }
    public boolean isRepeatable() { return false; }
}