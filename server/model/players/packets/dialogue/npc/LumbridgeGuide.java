package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class LumbridgeGuide extends NPCDialogue {

	public LumbridgeGuide(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 306; // Standard OSRS Lumbridge Guide NPC ID
	}

	// ==============================================================
	// HELPER METHODS (Link these to your server's actual systems)
	// ==============================================================
	private boolean hasWeapon(Player c) {
		// Checks if a weapon is equipped (Slot 3)
		return c.playerEquipment[3] > 0; 
	}

	// Placeholder quest checks - update these to use your server's quest system!
	private boolean doneCooksAssistant(Player c) { return false; }
	private boolean doneRestlessGhost(Player c) { return false; }
	private boolean doneXMarksTheSpot(Player c) { return false; }
	private boolean doneSheepShearer(Player c) { return false; }
	private boolean doneMisthalinMystery(Player c) { return false; }
	private boolean doneRuneMysteries(Player c) { return false; }

	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 306:
			switch (startDialogueId) {

			// ==============================================================
			// ENTRY POINT
			// ==============================================================
			case 8600:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"Greetings, adventurer. I am Phileas, the Lumbridge", 
					"Guide. I am here to give information and directions", 
					"to new players. Is there anything I can help you with?");
				c.nextChat = 8601;
				break;

			// ==============================================================
			// MAIN MENU
			// ==============================================================
			case 8601:
				options(c, 8601, 
					"Where can I find a quest to go on?", 
					"What monsters should I fight?",
					"Where can I make money?", 
					"Where can I find more information?",
					"More options...");
				break;

			// --- COMMON RE-USABLE EXITS ---
			case 8602:
				npc(c, "Lumbridge Guide", Anim.CALM_1, "Is there anything else you need help with?");
				c.nextChat = 8603;
				break;
			case 8603:
				options(c, 8603, "Yes please.", "No thank you.");
				break;
			case 8604:
				player(c, Anim.CALM_1, "No thank you.");
				c.nextChat = 0;
				break;
			case 8605:
				player(c, Anim.CALM_1, "Yes please.");
				c.nextChat = 8601;
				break;

			// ==============================================================
			// OPTION 1: QUESTS
			// ==============================================================
			case 8610:
				player(c, Anim.CALM_1, "Where can I find a quest to go on?");
				c.nextChat = 8611;
				break;
			case 8611:
				if (!doneCooksAssistant(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I've heard my friend the cook is in need of a spot",
						"of help. He'll be in the kitchen of this here castle.",
						"Just talk to him and he'll set you off.");
				} else if (!doneRestlessGhost(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I heard that Father Aereck could use a hand. You'll",
						"find him in the church just here. Talk to him and he'll",
						"give you the details.");
				} else if (!doneXMarksTheSpot(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I heard that a chap called Veos needs help finding",
						"some treasure. Last I heard, he was looking for assistance",
						"in the pub just north of here. Talk to him and see if",
						"you can help.");
				} else if (!doneSheepShearer(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I heard that Farmer Fred could use some help.",
						"You'll find him on the farm just north of here. Talk to",
						"him and see if he needs a hand.");
				} else if (!doneMisthalinMystery(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I heard there was some commotion in the swamp to the",
						"south. Might be worth looking into.");
				} else if (!doneRuneMysteries(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"Well, I believe the Duke of Lumbridge requires an errand",
						"running for him. You can find him upstairs in the castle.",
						"Just talk to him to get the details.");
				} else {
					npc(c, "Lumbridge Guide", Anim.CALM_1,
						"I'm afraid I can't think of any more quests for you. Don't",
						"worry though, there'll be plenty more out there. You'll",
						"just need to find them.");
				}
				c.nextChat = 8612;
				break;
			case 8612:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"The minimap in the top right corner of the screen has",
					"various icons to show different points of interest. Look",
					"for the icon to the left to find quest start points.");
				c.nextChat = 8602; // Loops back to "Anything else?"
				break;

			// ==============================================================
			// OPTION 2: MONSTERS
			// ==============================================================
			case 8620:
				player(c, Anim.CALM_1, "What monsters should I fight?");
				c.nextChat = 8621;
				break;
			case 8621:
				if (!hasWeapon(c)) {
					npc(c, "Lumbridge Guide", Anim.CALM_1, 
						"It's going to be tough fighting monsters without a weapon.",
						"I suggest you find one of those first.");
					c.nextChat = 8622;
				} else {
					npc(c, "Lumbridge Guide", Anim.CALM_1, 
						"There's lots of beasts to fight in the woods around here,",
						"especially to the west. There are certainly some goblins",
						"and spiders that are pests and could do with being",
						"cleared out.");
					c.nextChat = 8625;
				}
				break;
			case 8622:
				player(c, Anim.CALM_1, "How can I get a weapon?");
				c.nextChat = 8623;
				break;
			case 8623:
				npc(c, "Lumbridge Guide", Anim.CALM_1, "Well you can either make them, find them or buy them.");
				c.nextChat = 8624;
				break;
			case 8624:
				options(c, 8624, 
					"Where can I find a weapon?", 
					"How do I make a weapon?", 
					"Where can I buy a weapon?",
					"Could I get a staff like yours?");
				break;

			case 8625:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"There's also a chicken farm or two up the road for some",
					"fairly easy pickings. Non-player characters usually",
					"appear as yellow dots on your minimap, although there",
					"are some that you won't be able to fight, like myself.");
				c.nextChat = 8626;
				break;
			case 8626:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"Remember, you will do better if you have better armour",
					"and weapons and it's always worth carrying a bit of",
					"food to heal yourself.");
				c.nextChat = 8602;
				break;

			// Sub-Weapon Options
			case 8627:
				player(c, Anim.CALM_1, "Where can I find a weapon?");
				c.nextChat = 8628;
				break;
			case 8628:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"If you visit the combat tutors south of the general store",
					"they may be able to help you with that. Failing that",
					"there are a few cheap weapons lying around in the world.");
				c.nextChat = 8602;
				break;

			case 8629:
				player(c, Anim.CALM_1, "How do I make a weapon?");
				c.nextChat = 8630;
				break;
			case 8630:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"The Smithing skill allows you to make armour and weapons.",
					"Talk to the boy who smelts metal in the furnace,",
					"I'm sure he can help.");
				c.nextChat = 8602;
				break;

			case 8631:
				player(c, Anim.CALM_1, "Where can I buy a weapon?");
				c.nextChat = 8632;
				break;
			case 8632:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"You can get a weapon free from the combat tutors if they",
					"think you need it. Failing that, the nearest shop that",
					"would sell you something is Bob's Brilliant Axes.");
				c.nextChat = 8602;
				break;

			case 8633:
				player(c, Anim.CALM_1, "Could I get a staff like yours?");
				c.nextChat = 8634;
				break;
			case 8634:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"There is no other staff like this in all the land.",
					"It's a very important staff. It shows who holds the job",
					"as the Lumbridge Guide, and that's me.");
				c.nextChat = 8602;
				break;

			// ==============================================================
			// OPTION 3: MONEY
			// ==============================================================
			case 8640:
				player(c, Anim.CALM_1, "Where can I make money?");
				c.nextChat = 8641;
				break;
			case 8641:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"There are many ways to make money in the game.",
					"I would suggest either killing monsters or doing a",
					"trade skill such as Smithing or Fishing.");
				c.nextChat = 8642;
				break;
			case 8642:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"Please don't try to get money by begging off other",
					"players. It will make you unpopular. Nobody likes a",
					"beggar. It is very irritating to have others asking",
					"for your cash.");
				c.nextChat = 8602;
				break;

			// ==============================================================
			// OPTION 4: INFORMATION (Wiki)
			// ==============================================================
			case 8650:
				player(c, Anim.CALM_1, "Where can I find more information?");
				c.nextChat = 8651;
				break;
			case 8651:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"What you'll want is the OSRS Wiki! I've taken the",
					"liberty of opening the useful links section of the",
					"account management tab for you.");
				c.nextChat = 8652;
				break;
			case 8652:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"Here you can find a link to the wiki. There's",
					"information and guides on almost everything, all easily",
					"searched and it's even written and maintained by players.");
				c.nextChat = 8602;
				break;

			// ==============================================================
			// OPTION 5: MORE OPTIONS (Page 2)
			// ==============================================================
			case 8660:
				options(c, 8660, 
					"I'd like to know more about security.", 
					"Where can I find a bank?",
					"I don't need any help.", 
					"Previous options...");
				break;

			// PAGE 2, OPT 1: SECURITY
			case 8661:
				player(c, Anim.CALM_1, "I'd like to know more about security.");
				c.nextChat = 8662;
				break;
			case 8662:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"First I must warn you to take every precaution to keep",
					"your password and PIN secure. The most important thing",
					"to remember is to never give your password to, or",
					"share your account with, anyone.");
				c.nextChat = 8663;
				break;
			case 8663:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"I can also tell you about password security, avoiding",
					"item scamming and in-game moderation. I can also tell",
					"you about a place called the Stronghold of Security.");
				c.nextChat = 8664;
				break;
			case 8664:
				options(c, 8664, 
					"I'd like to know about password security.", 
					"I'd like to know more about avoiding item scamming.",
					"I'd like to know more about in-game moderation.", 
					"I'd like to know about the Stronghold of Security.",
					"I'd like to know about something else.");
				break;

			case 8665:
				player(c, Anim.CALM_1, "I'd like to know about password security.");
				c.nextChat = 8666;
				break;
			case 8666:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"Well, the first thing to remember with password security,",
					"which I can't stress enough, is to never tell your",
					"password, bank PIN or authenticator details to anyone,",
					"not even if they claim to be staff.");
				c.nextChat = 8602;
				break;

			case 8667:
				player(c, Anim.CALM_1, "I'd like to know more about avoiding item scamming.");
				c.nextChat = 8668;
				break;
			case 8668:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"There are many nice and helpful players; unfortunately,",
					"as in real life, there are some who aren't so honest.",
					"Some people may try to trick you out of your items.");
				c.nextChat = 8602;
				break;

			case 8669:
				player(c, Anim.CALM_1, "I'd like to know more about in-game moderation.");
				c.nextChat = 8670;
				break;
			case 8670:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"You will, from time to time, see moderator characters",
					"in game. Remember: a real staff member will never",
					"ask for your password or any other personal information.");
				c.nextChat = 8602;
				break;

			case 8671:
				player(c, Anim.CALM_1, "I'd like to know about the Stronghold of Security.");
				c.nextChat = 8672;
				break;
			case 8672:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"An exciting new discovery has been made in the Barbarian",
					"Village. Soon after they started mining in the middle",
					"of the village, one of the miners got caught in a cave.");
				c.nextChat = 8602;
				break;

			case 8673:
				player(c, Anim.CALM_1, "I'd like to know about something else.");
				c.nextChat = 8601; // Back to main menu
				break;

			// PAGE 2, OPT 2: BANK
			case 8680:
				player(c, Anim.CALM_1, "Where can I find a bank?");
				c.nextChat = 8681;
				break;
			case 8681:
				npc(c, "Lumbridge Guide", Anim.CALM_1, 
					"You'll find a bank upstairs in Lumbridge Castle",
					"Just take the stairs right to the top.");
				c.nextChat = 8602;
				break;

			// PAGE 2, OPT 3: NO HELP
			case 8685:
				player(c, Anim.CALM_1, "No, I can find things myself thank you.");
				c.nextChat = 0;
				break;

			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		
		// "Is there anything else you need help with?" -> Yes / No
		if (c.dialogueAction == 8603) {
			switch (buttonId) {
			case OPT2_FIRST: next(c, 8605); break;  // Yes please
			case OPT2_SECOND: next(c, 8604); break; // No thank you
			}
		}

		// MAIN MENU (5 Options)
		else if (c.dialogueAction == 8601) {
			switch (buttonId) {
			case OPT5_FIRST: next(c, 8610); break;  // Quests
			case OPT5_SECOND: next(c, 8620); break; // Monsters
			case OPT5_THIRD: next(c, 8640); break;  // Money
			case OPT5_FOURTH: next(c, 8650); break; // Information
			case OPT5_FIFTH: next(c, 8660); break;  // More Options
			}
		}

		// MORE OPTIONS MENU (4 Options)
		else if (c.dialogueAction == 8660) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8661); break;  // Security
			case OPT4_SECOND: next(c, 8680); break; // Bank
			case OPT4_THIRD: next(c, 8685); break;  // No help
			case OPT4_FOURTH: next(c, 8601); break; // Previous options (Main menu)
			}
		}

		// WEAPONS MENU (4 Options)
		else if (c.dialogueAction == 8624) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8627); break;  // Where find
			case OPT4_SECOND: next(c, 8629); break; // How make
			case OPT4_THIRD: next(c, 8631); break;  // Where buy
			case OPT4_FOURTH: next(c, 8633); break; // Staff
			}
		}

		// SECURITY MENU (5 Options)
		else if (c.dialogueAction == 8664) {
			switch (buttonId) {
			case OPT5_FIRST: next(c, 8665); break;  // Password
			case OPT5_SECOND: next(c, 8667); break; // Scamming
			case OPT5_THIRD: next(c, 8669); break;  // Moderation
			case OPT5_FOURTH: next(c, 8671); break; // Stronghold
			case OPT5_FIFTH: next(c, 8673); break;  // Something else
			}
		}
	}
}