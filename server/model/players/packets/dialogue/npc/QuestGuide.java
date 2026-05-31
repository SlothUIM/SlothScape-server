package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class QuestGuide extends NPCDialogue {

    public QuestGuide(Player c) { super(c); }

    @Override
    public int getNPCID() { return 9480; }
	public String getDialogueRange() {
		return "3042-3050";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3042:
                c.getPA().createPlayerHints(1, 4);
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Talk with the Quest Guide.", "", "He will tell you all about quests.", "", "");
                c.getPA().chatbox(6179);
                c.tutorialProgress = 12;
                c.nextChat = 0;
                break;
            case 3043:
                npc2(c, "Quest Guide", Anim.CALM_1, "Ah. Welcome, adventurer. I'm here to tell you all about", "quests. Let's start by opening the quest side panel.");
                c.nextChat = 3044;
                break;
            case 3044:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Open the Quest Journal.", "", "Click on the flashing icon next to your inventory.", "", "");
                c.getPA().chatbox(6179);
                c.setSidebarInterface(2, 638);
                c.getPA().flashSideBarIcon(-2);
                c.nextChat = 0;
                break;
            case 3045:
                npc3(c, "Quest Guide", Anim.TALKING_ALOT, "Now you have the journal open. I'll tell you a bit about", "it. At the moment all the quests shown in red which", "means you have not started them yet.");
                c.nextChat = 3046;
                break;
            case 3046:
                npc4(c, "Quest Guide", Anim.TALKING_ALOT, "When you start a quest it will change colour to yellow", "and to green when you've finished. This is so you can", "easily see what's complete, what's started, and what's left", "to begin.");
                c.nextChat = 3047;
                break;
            case 3047:
                npc3(c, "Quest Guide", Anim.TALKING_ALOT, "The start of quests are easy to find. Look out for the", "star icons on the minimap, just like the one you should", "see marking my house.");
                c.nextChat = 3048;
                break;
            case 3048:
                npc4(c, "Quest Guide", Anim.TALKING_ALOT, "The quests themselves can vary greatly from collecting", "beads to hunting down dragons. Generally quests are", "started by talking to a non-player character like me,", "and will involve a series of tasks.");
                c.nextChat = 3049;
                break;
            case 3049:
                npc4(c, "Quest Guide", Anim.TALKING_ALOT, "There's not a lot more I can tell you about questing.", "You have to experience the thrill of it yourself to fully", "understand. You may find some adventure in the caves", "under my house.");
                c.nextChat = 3050;
                break;
            case 3050:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "It's time to enter some caves. Click on the ladder to go down to", "the next area.", "", "Moving on");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3088, 3119, c.getHeight(), 2);
                c.nextChat = 0;
                c.tutorialProgress = 14;
                break;
        }
    }
}