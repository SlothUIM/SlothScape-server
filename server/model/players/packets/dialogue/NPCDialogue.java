package server.model.players.packets.dialogue;

import server.model.players.Player;

/**
 * Base NPC dialogue class.
 * Subclasses should override dialogue(Player) to render the current step.
 * Optionally override onOpen/onOption/onClose to manage lifecycle and option routing.
 */
public abstract class NPCDialogue {

	protected final Player c; 
	public NPCDialogue(Player c) {
	    this.c = c;
	}
    // Common option button IDs (adjust if your client differs)
    public static final int OPT2_FIRST  = 9157;
    public static final int OPT2_SECOND = 9158;

    public static final int OPT3_FIRST  = 9167;
    public static final int OPT3_SECOND = 9168;
    public static final int OPT3_THIRD  = 9169;

    public static final int OPT4_FIRST  = 9178;
    public static final int OPT4_SECOND = 9179;
    public static final int OPT4_THIRD  = 9180;
    public static final int OPT4_FOURTH = 9181;
    

    public static final int OPT5_FIRST  = 9190;
    public static final int OPT5_SECOND = 9191;
    public static final int OPT5_THIRD  = 9192;
    public static final int OPT5_FOURTH = 9193;
    public static final int OPT5_FIFTH = 9194;
    /** Returns a single NPC ID. Used by legacy/standard dialogues. */
    public int getNPCID() { 
        return -1; 
    }
    
    /** Returns multiple NPC IDs. Used for generic dialogues like Men/Women. */
    public int[] getNPCIDs() { 
        return new int[0]; 
    }
    /**
     * Called when opening this dialogue at a specific dialogueId.
     * Default sets c.dialogueId and calls dialogue(c).
     */
    public void onOpen(Player c, int startDialogueId, int npcId) {
       // c.dialogueId = startDialogueId;
        dialogue(c, npcId, startDialogueId);
    }

    /**
     * Render/handle the current dialogue step.
     * Implement this with your switch (c.dialogueId) logic.
     */
    public abstract void dialogue(Player c, int npcId, int startDialogueId);

    /**
     * Handle an option button click for this dialogue.
     * Default no-op; override in subclasses to route based on c.dialogueAction and buttonId.
     */
    public void onOption(Player c, int buttonId) {
        // no-op by default
    }

    /**
     * Called when the dialogue is closed. Default no-op.
     */
    public void onClose(Player c) {
        // no-op
    }
    /** Returns the starting dialogue ID for documentation/dumping. */
    /** Returns a string representing the dialogue IDs handled by this class. */
    public String getDialogueRange() { 
        return "???"; 
    }
    // ----------------- Convenience helpers -----------------

    /** Jump to another dialogue step for the current talking NPC. */
    protected final void next(Player c, int dialogueId) {
        c.getDH().sendDialogues(dialogueId, c.talkingNpc);
    }

    /** End the dialogue and close interfaces. */
    protected final void end(Player c) {
        c.nextChat = 0;
        c.dialogueAction = 0;
        c.getPA().closeAllWindows();
        onClose(c);
    }

    /** Set a dialogueAction and show a 2-option menu. */
    protected final void options(Player c, int dialogueAction, String... line) {
        c.getDH().sendOptions(line);
        c.dialogueAction = dialogueAction;
    }
    protected final void options_with_title(Player c, int dialogueAction, String title, String... line) {
        c.getDH().sendOptionsTitle(title, line);
        c.dialogueAction = dialogueAction;
    }
    /** Set a dialogueAction and show a 3-option menu. */
    protected final void options3(Player c, int dialogueAction, String o1, String o2, String o3) {
        c.getDH().sendOptions(o1, o2, o3);
        c.dialogueAction = dialogueAction;
    }

    /** Set a dialogueAction and show a 4-option menu. */
    protected final void options4(Player c, int dialogueAction, String o1, String o2, String o3, String o4) {
        c.getDH().sendOptions(o1, o2, o3, o4);
        c.dialogueAction = dialogueAction;
    }

    // Optional chat wrappers if you want them (you can keep using c.getDH() directly)

    protected final void npc(Player c, String npcName, Anim anim, String... l1) {
        c.getDH().npcChat( c.talkingNpc,  npcName, anim.getAnimationId(), l1);
    }

    protected final void npc2(Player c, String npcName, Anim anim, String l1, String l2) {
        c.getDH().npcChat(c.talkingNpc, npcName, anim.getAnimationId(), l1, l2);
    }

    protected final void npc3(Player c, String npcName, Anim anim, String l1, String l2, String l3) {
        c.getDH().npcChat(c.talkingNpc, npcName, anim.getAnimationId(), l1, l2, l3);
    }
    protected final void item(String header, Player c, int itemId, String... l1) {
    	c.getDH().sendItemStatement(header, itemId, 200, l1);
    }
    protected final void item(Player c, int itemId, String... l1) {
    	c.getDH().sendItemStatement("", itemId, 200, l1);
    }
    protected final void npc4(Player c, String npcName, Anim anim, String l1, String l2, String l3, String l4) {
        c.getDH().npcChat(c.talkingNpc, npcName, anim.getAnimationId(), l1, l2, l3, l4);
    }

    protected final void player(Player c, Anim anim, String... l1) {
        c.getDH().playerChat(anim.getAnimationId(), l1);
    }

    protected final void player2(Player c, Anim anim, String l1, String l2) {
        c.getDH().playerChat(anim.getAnimationId(), l1, l2);
    }

    protected final void player3(Player c, Anim anim, String l1, String l2, String l3) {
        c.getDH().playerChat(anim.getAnimationId(), l1, l2, l3);
    }

    protected final void player4(Player c, Anim anim, String l1, String l2, String l3, String l4) {
        c.getDH().playerChat(anim.getAnimationId(), l1, l2, l3, l4);
    }
}