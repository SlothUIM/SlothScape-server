package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class FinancialAdvisor extends NPCDialogue {

    public FinancialAdvisor(Player c) { super(c); }

    @Override
    public int getNPCID() { return 947; }

    @Override
	public String getDialogueRange() {
		return "3078-3088";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3078:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Follow the path and you will come to the front of the building.", "This is the Bank of SlothScape, where you can store all your", "most valued items. To open your bank box just right click on an", "open booth indicated and select 'use'.", "Banking");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3122, 3124, c.getHeight(), 2);
                c.nextChat = 0;
                break;
            case 3079:
                player(c, Anim.CALM_1, "Hello. Who are you?");
                c.nextChat = 3080;
                break;
            case 3080:
                npc2(c, "Financial Advisor", Anim.CALM_1, "I'm the Financial Advisor. I'm here to tell people how to", "make money.");
                c.nextChat = 3081;
                break;
            case 3081:
                player(c, Anim.CALM_1, "Okay. How can I make money then?");
                c.nextChat = 3082;
                break;
            case 3082:
                npc(c, "Financial Advisor", Anim.CALM_1, "How you can make money? Quite.");
                c.nextChat = 3083;
                break;
            case 3083:
                npc3(c, "Financial Advisor", Anim.CALM_1, "Well there are three basic ways of making money here:", "combat, quests, and trading. I will talk you through each", "of them very quickly.");
                c.nextChat = 3084;
                break;
            case 3084:
                npc3(c, "Financial Advisor", Anim.CALM_1, "Let's start with combat as it is probably still fresh in", "your mind. Many enemies, both human and monster,", "will drop items when they die.");
                c.nextChat = 3085;
                break;
            case 3085:
                npc3(c, "Financial Advisor", Anim.CALM_1, "Now, the next way to earn money quickly is by quests.", "Many people on SlothScape have things they need", "doing, which they will reward you for.");
                c.nextChat = 3086;
                break;
            case 3086:
                npc3(c, "Financial Advisor", Anim.CALM_1, "By getting a high level in skills such as Cooking, Mining,", "Smithing or Fishing, you can create or catch your own", "items and sell them for pure profit.");
                c.nextChat = 3087;
                break;
            case 3087:
                npc2(c, "Financial Advisor", Anim.CALM_1, "Well that about covers it. Come back if you'd like to go", "over this again.");
                c.nextChat = 3088;
                break;
            case 3088:
                c.getPA().closeAllWindows();
                c.tutorialProgress = 28;
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Continue through the next door.", "", "", "");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3129, 3124, c.getHeight(), 2);
                c.getPA().createPlayerHints(1, 8);
                c.nextChat = 0;
                break;
        }
    }
}