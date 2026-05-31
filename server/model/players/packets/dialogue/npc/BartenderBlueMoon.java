package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class BartenderBlueMoon extends NPCDialogue {

	public BartenderBlueMoon(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 1312; 
    }


	private static final int BEER = 1917;
	private static final int COINS = 995;
	private static final int CASKET = 2802; // Standard Casket ID
	private static final int CLUE_SCROLL = 2677; // Example Clue ID
	private static final int NPCID = 1312;
    @Override
	public String getDialogueRange() {
		return "8500-8521";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		if(npcId == NPCID)
		switch (startDialogueId) {

			case 8500:
				npc(c, "Bartender", Anim.CALM_1, "What can I do yer for?");
				c.nextChat = 8501;
				break;

			case 8501:
				options(c, 8501, 
					"A glass of your finest ale please.", 
					"Can you recommend where an adventurer might make his fortune?", 
					"Do you know where I can get some good equipment?",
					"Bye!");
				break;

			// --- BRANCH 1: ALE ---
			case 8502:
				player(c, Anim.CALM_1, "A glass of your finest ale please.");
				c.nextChat = 8503;
				break;

			case 8503:
				npc(c, "Bartender", Anim.CALM_1, "No problemo. That'll be 2 coins.");
				c.nextChat = 8504;
				break;

			case 8504:
				if (c.getItems().playerHasItem(COINS, 2)) {
					c.getItems().deleteItem(COINS, 2);
					c.getItems().addItem(BEER, 1);
					item("", c, BEER, "You buy a pint of beer.");
					c.nextChat = 8522;
				} else {
					player(c, Anim.SAD, "Oh dear. I don't seem to have enough money.");
					c.nextChat = -1;
				}
				break;

			case 8522:
				c.getPA().removeAllWindows();
					c.nextChat = -1;
				break;
			// --- BRANCH 2: FORTUNE ---
			case 8505:
				player(c, Anim.CALM_1, "Can you recommend where an adventurer might make his fortune?");
				c.nextChat = 8506;
				break;

			case 8506:
				npc(c, "Bartender", Anim.CALM_1, "Ooh I don't know if I should be giving away", "information, makes the game too easy.");
				c.nextChat = 8507;
				break;

			case 8507:
				options(c, 8507, 
					"Oh ah well...", 
					"Game? What are you talking about?", 
					"Just a small clue?");
				break;

			case 8508: // Game?
				player(c, Anim.CALM_1, "Game? What are you talking about?");
				c.nextChat = 8509;
				break;

			case 8509:
				npc(c, "Bartender", Anim.CALM_1, "This world around us... is an online game...", "called Old School RuneScape.");
				c.nextChat = 8510;
				break;

			case 8510:
				player(c, Anim.CALM_1, "Nope, still don't understand what you are talking", "about. What does 'online' mean?");
				c.nextChat = 8511;
				break;

			case 8511:
				npc(c, "Bartender", Anim.CALM_1, "It's a sort of connection between magic boxes", "across the world, big boxes on people's desktops", "and little ones people can carry.");
				c.nextChat = 8512;
				break;

			case 8512:
				player(c, Anim.CALM_1, "I give up. You're obviously completely mad!");
				c.nextChat = -1;
				break;

			case 8513: // Just a small clue
				player(c, Anim.CALM_1, "Just a small clue?");
				c.nextChat = 8514;
				break;

			case 8514:
				npc(c, "Bartender", Anim.CALM_1, "Go and talk to the bartender at the Jolly Boar Inn,", "he doesn't seem to mind giving away clues.");
				c.nextChat = -1;
				break;

			// --- BRANCH 3: EQUIPMENT ---
			case 8515:
				player(c, Anim.CALM_1, "Do you know where I can get some good equipment?");
				c.nextChat = 8516;
				break;

			case 8516:
				npc(c, "Bartender", Anim.CALM_1, "Well, there's the sword shop across the road, or", "there's also all sorts of shops up around the market.");
				c.nextChat = -1;
				break;

			// --- TREASURE TRAILS ---
			case 8520:
				npc(c, "Bartender", Anim.CALM_1, "Nicely solved!");
				c.nextChat = 8521;
				break;

			case 8521:
				// You can toggle which one to show based on player state
				c.getItems().addItem(CASKET, 1);
				item("", c, CASKET, "You've obtained a casket!");
				c.nextChat = -1;
				break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 8501) {
			switch (buttonId) {
				case OPT4_FIRST: next(c, 8502); break;  // Ale
				case OPT4_SECOND: next(c, 8505); break; // Fortune
				case OPT4_THIRD: next(c, 8515); break;  // Equipment
				case OPT4_FOURTH: c.getPA().closeAllWindows(); break;
			}
		} else if (c.dialogueAction == 8507) {
			switch (buttonId) {
				case OPT3_FIRST: c.getPA().closeAllWindows(); break; // Oh ah well
				case OPT3_SECOND: next(c, 8508); break; // Game?
				case OPT3_THIRD: next(c, 8513); break;  // Just a clue
			}
		}
	}
}