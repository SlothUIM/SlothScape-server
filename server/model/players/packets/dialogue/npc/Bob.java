package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class Bob extends NPCDialogue {

	public Bob(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 2812;
	}

	// This method handles the actual gold/item swap
	public void handleRepair(Player c, int itemId) {
		int repairCost = 60000; // Standard Barrows Repair Cost
		int repairedVersion = getRepairedId(itemId);

		if (repairedVersion == -1) {
			next(c, 8740); // "I can't do anything with that"
			return;
		}

		if (!c.getItems().playerHasItem(995, repairCost)) {
			npc(c, "Bob", Anim.CALM_1, "You'll need " + repairCost + " coins to repair that!");
			return;
		}

		c.getItems().deleteItem(995, repairCost);
		c.getItems().deleteItem(itemId, 1);
		c.getItems().addItem(repairedVersion, 1);
		next(c, 8742); // "Happy doing business"
	}

	private int getRepairedId(int broken) {
		// Example: Torag's Hammers 0 -> Torag's Hammers (Fully repaired)
		switch (broken) {
		case 4958: return 4745; // Torag's Helm
		case 4964: return 4749; // Torag's Plate
		case 4970: return 4751; // Torag's Legs
		case 4976: return 4747; // Torag's Hammers
		// Add your server's other broken IDs here...
		}
		return -1;
	}
    @Override
	public String getDialogueRange() {
		return "8700-8742";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (startDialogueId) {
		case 8700:
			npc(c, "Bob", Anim.CALM_1, "Greetings! Welcome to Bob's Brilliant Axes.", "Can I help you with anything?");
			c.nextChat = 8701;
			break;
		case 8701:
			options(c, 8701, "Give me a quest!", "Have you anything to sell?", "Can you repair my items for me?", "Bye!");
			break;
		case 8710:
			player(c, Anim.CALM_1, "Give me a quest!");
			c.nextChat = 8711;
			break;
		case 8711:
			npc(c, "Bob", Anim.ANGRY, "Get yer own!");
			c.nextChat = 0;
			break;
		case 8720:
			player(c, Anim.CALM_1, "Have you anything to sell?");
			c.nextChat = 8721;
			break;
		case 8721:
			npc(c, "Bob", Anim.CALM_1, "Yes! I buy and sell axes! Take your pick (or axe)!");
			c.getShops().openShop(1);
			break;
		case 8730:
			npc(c, "Bob", Anim.CALM_1, "Of course I'll repair it, though the materials may cost", "you. Just hand me the item and I'll have a look.");
			c.nextChat = 0;
			break;
		case 8740:
			npc(c, "Bob", Anim.CALM_1, "Sorry friend, but I can't do anything with that.");
			break;
		case 8742:
			npc(c, "Bob", Anim.CALM_1, "There you go, happy doing business with you!");
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 8701) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8710); break;
			case OPT4_SECOND: next(c, 8720); break;
			case OPT4_THIRD: next(c, 8730); break;
			case OPT4_FOURTH: next(c, 0); break;
			}
		}
	}
}