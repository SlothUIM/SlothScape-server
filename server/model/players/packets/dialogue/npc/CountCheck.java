package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class CountCheck extends NPCDialogue {

	public CountCheck(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 7414; // Count Check NPC ID
	}
	@Override
	public String getDialogueRange() {
	    return "3600-3632";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (startDialogueId) {

		case 3600:
			npc(c, "Count Check", Anim.LAUGH_1, "Ahahahaha! I am Count Check, the renowned", "security expert. Would you like me to check your", "account to see if it is secure?");
			c.nextChat = 3601;
			break;

		case 3601:
			options(c, 3601, 
				"Check my account, Count Check!", 
				"Can you give me any advice?", 
				"Where can I learn more about security?", 
				"Who are you?",
				"I'll see you another time.");
			break;

		// --- OPTION 1: CHECK ACCOUNT ---
		case 3610:
			if (c.hasBankPin && c.hasAuthenticator) {
				if (!c.receivedCountPrize) {
					npc(c, "Count Check", Anim.CALM_1, "You have an Authenticator, and you have set a", "Bank PIN, so you pass my checks! As you've passed", "my check for the first time, I award you a prize.");
					c.getItems().addItem(11137, 1); // Antique Lamp
					c.receivedCountPrize = true;
				} else {
					npc(c, "Count Check", Anim.CALM_1, "You have an Authenticator, and you have set a", "Bank PIN, so you pass my checks! As you've passed", "my check before, there's no prize now.");
				}
				c.nextChat = 3601;
			} else {
				npc(c, "Count Check", Anim.CALM_1, "You do not have a Bank PIN or Authenticator,", "so you fail my checks! Please upgrade before", "we meet again.");
				c.nextChat = 0;
			}
			break;

		// --- OPTION 2: ADVICE ---
		case 3620:
			npc(c, "Count Check", Anim.CALM_1, "I can! Count with the Count as I count the steps", "to securing your account:");
			c.nextChat = 3621;
			break;
		case 3621:
			npc(c, "Count Check", Anim.CALM_1, "ONE... unique password! Never use the same", "password for different accounts. If it got leaked,", "all of your accounts would become vulnerable!");
			c.nextChat = 3622;
			break;
		case 3622:
			npc(c, "Count Check", Anim.CALM_1, "TWO... step verification! Good email providers", "let you set up two-step verification. Make sure", "you've enabled this.");
			c.nextChat = 3623;
			break;
		case 3623:
			npc(c, "Count Check", Anim.CALM_1, "THREE... Um... Use an Authenticator. I can see", "you already do this."); // Simplify for RSPS
			c.nextChat = 3624;
			break;
		case 3624:
			options(c, 3624, "Carry on.", "What's that got to do with THREE?");
			break;
		case 3625:
			npc(c, "Count Check", Anim.ANGRY, "Look, I'd got something for ONE, TWO and FOUR,", "and I like counting, okay? Don't interrupt my flow!");
			c.nextChat = 3626;
			break;
		case 3626:
			npc(c, "Count Check", Anim.CALM_1, "FOUR... digit Bank PIN! I can see you have set one.");
			c.nextChat = 3627;
			break;
		case 3627:
			npc(c, "Count Check", Anim.LAUGH_1, "Muhahahahhaa! Such fun it is to count!");
			// Lightning effect could be added here: c.getPA().stillGfx(188, c.getX(), c.getY(), 0, 0);
			c.nextChat = 3601;
			break;

		// --- OPTION 4: WHO ARE YOU ---
		case 3630:
			npc(c, "Count Check", Anim.CALM_1, "As you may see, I am a vampyre, from Morytania.", "I left there when I heard the terrible news and", "now I choose to spend my days warning others.");
			c.nextChat = 3631;
			break;
		case 3631:
			player(c, Anim.CALM_1, "The perils?");
			c.nextChat = 3632;
			break;
		case 3632:
			npc(c, "Count Check", Anim.CALM_1, "Yes, be warned! Accounts can be stolen, and when", "that happens one can lose everything. I've left", "my vampyric ways behind me to warn you.");
			c.nextChat = 3601;
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 3601) {
			switch (buttonId) {
			case OPT5_FIRST: next(c, 3610); break;
			case OPT5_SECOND: next(c, 3620); break;
			case OPT5_THIRD: npc(c, "Count Check", Anim.CALM_1, "The Stronghold of Security, in Barbarian Village.", "There is much to learn there."); break;
			case OPT5_FOURTH: next(c, 3630); break;
			case OPT5_FIFTH: next(c, 0); break;
			}
		} else if (c.dialogueAction == 3624) {
			switch (buttonId) {
			case OPT2_FIRST: next(c, 3626); break;
			case OPT2_SECOND: next(c, 3625); break;
			}
		}
	}
}