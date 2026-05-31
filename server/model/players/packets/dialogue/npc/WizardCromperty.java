package server.model.players.packets.dialogue.npc;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class WizardCromperty extends NPCDialogue {

	public WizardCromperty(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 8481;
	}

	// Item IDs
	private static final int PURE_ESSENCE = 7936;
	private static final int TORN_CLUE = 19835; // Master clue scroll torn piece

	/*
	 * TODO: Connect these to your daily claim manager and diary reward tiers.
	 */
	private boolean hasClaimedDailyEssence(Player c) { return false; }
	private int getEssenceAmount(Player c) { return 50; } // Adjust based on Diary tier
	private void claimDailyEssence(Player c) { 
		// Set claimed variable here
	}
	   @Override
	 	public String getDialogueRange() {
	 		return "8500-8537";
	 	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 8481:
			switch (startDialogueId) {

			// ==============================================================
			// ENTRY POINT
			// ==============================================================
			case 8500:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Hello there. My name is Cromperty. I am a Wizard,", "and an inventor.");
				c.nextChat = 8501;
				break;

			// ==============================================================
			// MAIN MENU
			// ==============================================================
			case 8501:
				options(c, 8501, 
					"Claim free pure essence.", 
					"Chat.",
					"Can you teleport me to the Rune Essence?", 
					"Treasure Trails (Master)");
				break;

			// --------------------------------------------------------------
			// OPTION 1: "Claim free pure essence."
			// --------------------------------------------------------------
			case 8502:
				if (hasClaimedDailyEssence(c)) {
					npc(c, "Wizard Cromperty", Anim.CALM_1, "I've already given you your free allowance for today.", "Come back to me tomorrow for some more.");
					c.nextChat = 0;
				} else {
					int amount = getEssenceAmount(c);
					c.getItems().addItem(PURE_ESSENCE, amount);
					claimDailyEssence(c);
					item("", c, PURE_ESSENCE, "Player receives " + amount + " pure essence.");
					c.nextChat = 8503;
				}
				break;

			case 8503:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "There you go, " + getEssenceAmount(c) + " free pure essence.", "Return tomorrow for some more.");
				c.nextChat = 0;
				break;

			// --------------------------------------------------------------
			// OPTION 2: "Chat." -> Leads into branching conversations
			// --------------------------------------------------------------
			case 8504:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"You must be " + c.playerName + ". My good friend",
					"Sedridor has told me about you. As both wizard and",
					"inventor, he has aided me in my great invention!");
				c.nextChat = 8505;
				break;

			case 8505:
				options(c, 8505, 
					"Two jobs? That's got to be tough.", 
					"So what have you invented?",
					"Well, I shall leave you to your inventing.");
				break;

			// Chat Branch 1: Two jobs
			case 8506:
				player(c, Anim.CALM_1, "Two jobs? That's got to be tough.");
				c.nextChat = 8507;
				break;
			case 8507:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Not when you combine them it isn't!", "I invent MAGIC things!");
				c.nextChat = 8508;
				break;
			case 8508:
				options(c, 8508, 
					"So what have you invented?", 
					"Well, I shall leave you to your inventing.");
				break;

			// Chat Branch 2 (Common Exit): Leave you to your inventing
			case 8509:
				player(c, Anim.CALM_1, "Well, I shall leave you to your inventing.");
				c.nextChat = 8510;
				break;
			case 8510:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Thanks for dropping by! Stop again anytime!");
				c.nextChat = 0;
				break;

			// Chat Branch 3: What have you invented?
			case 8511:
				player(c, Anim.CALM_1, "So what have you invented?");
				c.nextChat = 8512;
				break;
			case 8512:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"Ah! My latest invention is my patent pending",
					"teleportation block! It emits a low level magical",
					"signal, that will allow me to locate it anywhere in the",
					"world, and teleport anything");
				c.nextChat = 8513;
				break;
			case 8513:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"directly to it! I hope to revolutionise the entire",
					"teleportation system! Don't you think I'm great? Uh,",
					"I mean it's great?");
				c.nextChat = 8514;
				break;

			case 8514:
				options(c, 8514, 
					"So where is the other block?", 
					"Can I be teleported please?",
					"Well done, that's very clever.");
				break;

			// Invented Branch 1: Where is the other block?
			case 8515:
				player(c, Anim.CALM_1, "So where is the other block?");
				c.nextChat = 8516;
				break;
			case 8516:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"Well... Hmm. I would guess somewhere between here",
					"and the Wizards' Tower in Misthalin. All I know is",
					"that it hasn't got there yet as the wizards there",
					"would have contacted me.");
				c.nextChat = 8517;
				break;
			case 8517:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"I'm using the GPDT for delivery. They assured me",
					"it would be delivered promptly.");
				c.nextChat = 8518;
				break;

			case 8518:
				options(c, 8518, 
					"Who are the GPDT?", 
					"Can I be teleported please?");
				break;

			// Sub-Branch: Who are the GPDT?
			case 8519:
				player(c, Anim.CALM_1, "Who are the GPDT?");
				c.nextChat = 8520;
				break;
			case 8520:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"The Gielinor Parcel Delivery Team. They come very",
					"highly recommended. Their motto is: 'We aim to deliver",
					"your stuff at some point after you have paid us!'");
				c.nextChat = 0;
				break;

			// Invented Branch 2: Can I be teleported please?
			case 8521:
				player(c, Anim.CALM_1, "Can I be teleported please?");
				c.nextChat = 8522;
				break;
			case 8522:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"By all means! I'm afraid I can't give you any",
					"specifics as to where you will come out however.",
					"Presumably wherever the other block is located.");
				c.nextChat = 8523;
				break;
			case 8523:
				options(c, 8523, 
					"Yes, that sounds good. Teleport me!", 
					"That sounds dangerous. Leave me here.");
				break;

			case 8524:
				player(c, Anim.CALM_1, "Yes, that sounds good. Teleport me!");
				c.nextChat = 8525;
				break;
			case 8525:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Okey dokey! Ready?");
				c.nextChat = 8526;
				break;
			case 8526:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Hmmm.... that's odd... I can't seem to get a signal...");
				c.nextChat = 8527;
				break;
			case 8527:
				player(c, Anim.CALM_1, "Oh well, never mind.");
				c.nextChat = -1;
				break;

			case 8528:
				player(c, Anim.CALM_1, "That sounds dangerous. Leave me here.");
				c.nextChat = 8529;
				break;
			case 8529:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "As you wish.");
				c.nextChat = 0;
				break;

			// Invented Branch 3: Well done, that's very clever
			case 8530:
				player(c, Anim.CALM_1, "Well done, that's very clever.");
				c.nextChat = 8531;
				break;
			case 8531:
				npc(c, "Wizard Cromperty", Anim.CALM_1, 
					"Yes it is isn't it? Forgive me for feeling a little",
					"smug, this is a major breakthrough in the field",
					"of teleportation!");
				c.nextChat = 0;
				break;

			// --------------------------------------------------------------
			// OPTION 3: "Can you teleport me to the Rune Essence?"
			// --------------------------------------------------------------
			case 8532:
				player(c, Anim.CALM_1, "Can you teleport me to the Rune Essence?");
				c.nextChat = 8533;
				break;
			case 8533:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Seventior disthine molenko!");
				c.nextChat = 8534;
				break;
			case 8534:
				NPC npc = NPCHandler.getNpc(8481);
				c.getPA().closeAllWindows();
				c.lastX = c.getX();
				c.lastY = c.getY();
				c.lastHeight = c.getHeight();
				
				c.getAD().completeAchievement("ArdougneEasy", "Have Wizard Cromperty teleport you to the Rune essence mine", 1);
				npc.startAnimation(1818);
				c.getPA().startTeleport(2897, 4849, 0, "modern");
				break;

			// --------------------------------------------------------------
			// OPTION 4: Treasure Trails (Master)
			// --------------------------------------------------------------
			case 8535:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Here you go.");
				c.nextChat = 8536;
				break;
			case 8536:
				c.getItems().addItem(TORN_CLUE, 1); 
				item("", c, TORN_CLUE, "You are handed a torn part of a clue scroll.");
				c.nextChat = 8537;
				break;
			case 8537:
				npc(c, "Wizard Cromperty", Anim.CALM_1, "Your magical prowess... it is most impressive.");
				c.nextChat = 0;
				break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		// MAIN MENU
		if (c.dialogueAction == 8501) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8502); break;  // Claim essence
			case OPT4_SECOND: next(c, 8504); break; // Chat
			case OPT4_THIRD: next(c, 8532); break;  // Teleport to Essence
			case OPT4_FOURTH: next(c, 8535); break; // Master Clue
			}
		}
		
		// CHAT SUB-MENU 1 (Initial Chat)
		else if (c.dialogueAction == 8505) {
			switch (buttonId) {
			case OPT3_FIRST: next(c, 8506); break;  // Two jobs
			case OPT3_SECOND: next(c, 8511); break; // What have you invented?
			case OPT3_THIRD: next(c, 8509); break;  // Leave
			}
		}

		// CHAT SUB-MENU 2 (Post-Two Jobs)
		else if (c.dialogueAction == 8508) {
			switch (buttonId) {
			case OPT2_FIRST: next(c, 8511); break;  // What have you invented?
			case OPT2_SECOND: next(c, 8509); break; // Leave
			}
		}

		// CHAT SUB-MENU 3 (Post-Invention)
		else if (c.dialogueAction == 8514) {
			switch (buttonId) {
			case OPT3_FIRST: next(c, 8515); break;  // Where is the other block?
			case OPT3_SECOND: next(c, 8521); break; // Can I be teleported please?
			case OPT3_THIRD: next(c, 8530); break;  // Well done, that's clever.
			}
		}

		// CHAT SUB-MENU 4 (Post-Where is block)
		else if (c.dialogueAction == 8518) {
			switch (buttonId) {
			case OPT2_FIRST: next(c, 8519); break;  // Who are the GPDT?
			case OPT2_SECOND: next(c, 8521); break; // Can I be teleported please?
			}
		}

		// CHAT SUB-MENU 5 (Post-Can I be teleported)
		else if (c.dialogueAction == 8523) {
			switch (buttonId) {
			case OPT2_FIRST: next(c, 8524); break;  // Yes, teleport me
			case OPT2_SECOND: next(c, 8528); break; // No, dangerous
			}
		}
	}
}