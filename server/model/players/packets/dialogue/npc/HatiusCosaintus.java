package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class HatiusCosaintus extends NPCDialogue {

	public HatiusCosaintus(Player c) {
		super(c);
	}

    @Override
    public int getNPCID() {
        return 5523; 
    }

	// Ring IDs
	private static final int RING_1 = 13125;
	private static final int RING_2 = 13126;
	private static final int RING_3 = 13127;
	private static final int RING_4 = 13128;
	private static final int LAMP = 2528; // Standard XP Lamp
	private static final int NPCID = 5523;
	/*
	 * TODO: Connect these booleans to your actual DiaryManager.
	 * Example: return c.getDiaryManager().getLumbridge().isEasyCompleted();
	 */
	private boolean completedEasy(Player c) { 
		return false; 
		}
	private boolean completedMed(Player c) { 
		return false; 
		}
	private boolean completedHard(Player c) { 
		return false; 
		}
	private boolean completedElite(Player c) { 
		return false; 
		}

	// Helper to determine the best ring the player is eligible for
	private int getBestRingID(Player c) {
		if (completedElite(c)) return RING_4;
		if (completedHard(c)) return RING_3;
		if (completedMed(c)) return RING_2;
		if (completedEasy(c)) return RING_1;
		return -1;
	}

    @Override
	public String getDialogueRange() {
		return "8000-8405";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 5523:
			switch (startDialogueId) {

			// ==============================================================
			// ENTRY POINT (Checks for new completions first)
			// ==============================================================
			case 8000:
				// Logic to auto-start completion dialogue if they just finished a tier
				// You would ideally check a "hasClaimedReward" boolean here.
				// For this snippet, we default to the standard greeting.
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "Greetings, adventurer.");
				c.nextChat = 8001;
				break;

			// ==============================================================
			// MAIN MENU
			// ==============================================================
			case 8001:
				options(c, 8001, 
					"Can I have another ring?", 
					"Who are you?",
					"I have a question about my Achievement Diary", 
					"Bye!");
				break;

			// --------------------------------------------------------------
			// OPTION 1: "Can I have another ring?"
			// --------------------------------------------------------------
			case 8002:
				player(c, Anim.CALM_1, "Can I have another ring?");
				c.nextChat = 8003;
				break;

			case 8003:
				int ringId = getBestRingID(c);
				if (ringId > 0) {
					if (c.getItems().playerHasItem(ringId) || c.getItems().bankContains(ringId)) {
						npc(c, "Hatius Cosaintus", Anim.ANNOYED, "You already have a ring! Check your bank.");
						c.nextChat = 8001; // Back to main options
					} else {
						item("", c, ringId, "Hatius gives you another ring.");
						c.getItems().addItem(ringId, 1);
						c.nextChat = 8001; // Back to main options
					}
				} else {
					npc(c, "Hatius Cosaintus", Anim.CALM_1, "You haven't earned a ring yet.");
					c.nextChat = 8001;
				}
				break;

			// --------------------------------------------------------------
			// OPTION 2: "Who are you?"
			// --------------------------------------------------------------
			case 8005:
				player(c, Anim.CALM_1, "Who are you?");
				c.nextChat = 8006;
				break;

			case 8006:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"I am Hatius Cosaintus, the taskmaster for the", 
					"Lumbridge & Draynor Achievement Diary.");
				c.nextChat = 8007;
				break;

			case 8007:
				player(c, Anim.CALM_1, "What is the Achievement Diary?");
				c.nextChat = 8008;
				break;

			case 8008:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"It's a diary that helps you keep track of particular", 
					"achievements. In the Lumbridge and Draynor area it", 
					"can help you discover some quite useful things.");
				c.nextChat = 8009;
				break;

			case 8009:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"Eventually, with enough exploration, the inhabitants will",
					"reward you.",
					"You can see the list of tasks on the side-panel.");
				c.nextChat = 8001; // Back to Main Options
				break;

			// --------------------------------------------------------------
			// OPTION 3: "I have a question about my Diary"
			// --------------------------------------------------------------
			case 8010:
				player(c, Anim.CALM_1, "I have a question about my Achievement diary.");
				c.nextChat = 8011;
				break;

			case 8011: // Sub-Options Menu
				options(c, 8011, 
					"What is the Achievement Diary?", 
					"What are the rewards?",
					"How do I claim the rewards?", 
					"Bye!");
				break;

			// Sub-Opt 1: What is it? (Redirects to previous explanation)
			case 8012:
				c.getPA().closeAllWindows();
				dialogue(c, npcId, 8006); 
				break;

			// Sub-Opt 2: Rewards (Leads to Tier Menu)
			case 8013:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"Well, there are four different Explorer's rings, which match", 
					"up with the four levels of difficulty. Each has the same", 
					"rewards as the previous level and some additional");
				c.nextChat = 8014;
				break;

			case 8014:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"benefits too... which tier of rewards would you like to",
					"know more about?");
				c.nextChat = 8015;
				break;

			case 8015: // Rewards Tier Menu
				options(c, 8015, 
					"Easy Rewards.", 
					"Medium Rewards.", 
					"Hard Rewards.", 
					"Elite Rewards.");
				break;

				// --- Reward Descriptions ---
				case 8016: // Easy
					player(c, Anim.CALM_1, "Tell me more about the Easy rewards please!");
					c.nextChat = 8017;
					break;
				case 8017:
					npc(c, "Hatius Cosaintus", Anim.CALM_1, 
						"If you complete all of the easy tasks in Lumbridge and", 
						"Draynor, your ring can recharge half of your run energy", 
						"twice per day and cast low level alchemy without runes",
						"30 times per day.");
					c.nextChat = 8018;
					break;
				
				case 8019: // Medium
					player(c, Anim.CALM_1, "Tell me more about the Medium rewards please!");
					c.nextChat = 8020;
					break;
				case 8020:
					npc(c, "Hatius Cosaintus", Anim.CALM_1, 
						"In addition to the easy rewards, your ring can restore", 
						"half of your run energy and teleport you to the", 
						"Draynor cabbage patch three times per day.");
					c.nextChat = 8018;
					break;

				case 8022: // Hard
					player(c, Anim.CALM_1, "Tell me more about the Hard rewards please!");
					c.nextChat = 8023;
					break;
				case 8023:
					npc(c, "Hatius Cosaintus", Anim.CALM_1, 
						"In addition to the easy and medium benefits, the ring", 
						"will have unlimited cabbage teleports and four charges", 
						"to restore half your run energy. Collecting Tears of",
						"Guthix will provide increased experience.");
					c.nextChat = 8018;
					break;

				case 8025: // Elite
					player(c, Anim.CALM_1, "Tell me more about the Elite rewards please!");
					c.nextChat = 8026;
					break;
				case 8026:
					npc(c, "Hatius Cosaintus", Anim.CALM_1, 
						"In addition to the previous tiers, you can block an", 
						"additional slayer target, recharge all run energy 3x/day,", 
						"and cast high alchemy 30x/day. The Culinaromancer's",
						"chest is 20% cheaper."); // Split text if too long
					c.nextChat = 8027;
					break;
				case 8027:
					npc(c, "Hatius Cosaintus", Anim.CALM_1, 
						"You can use Fairy rings without a staff, and you'll",
						"receive double wire when thieving in Dorgesh-Kaan.");
					c.nextChat = 8018;
					break;

				case 8018: // Common "Thanks" exit for rewards
					player(c, Anim.CALM_1, "Thanks!");
					c.nextChat = 0;
					break;

			// Sub-Opt 3: "How do I claim?"
			case 8028:
				player(c, Anim.CALM_1, "How do I claim the rewards?");
				c.nextChat = 8029;
				break;
			case 8029:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"One should complete the tasks in Lumbridge and", 
					"Draynor so they're ticked off, then speak to me", 
					"to be rewarded.");
				c.nextChat = 8001; // Back to main options
				break;

			// --------------------------------------------------------------
			// OPTION 4: "Bye!"
			// --------------------------------------------------------------
			case 8030:
				player(c, Anim.CALM_1, "Bye!");
				c.nextChat = 8031;
				break;
			case 8031:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "Toodles.");
				c.nextChat = -1; // Back to Main Options (as per transcript)
				break;

			// ==============================================================
			// CLAIMING REWARDS (Trigger these via your DiaryManager)
			// ==============================================================

			// --- EASY COMPLETION ---
			case 8100:
				player(c, Anim.CALM_1, "I've completed all of the easy tasks in my", "Lumbridge & Draynor Achievement Diary!");
				c.nextChat = 8101;
				break;
			case 8101:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "Quite... You'll be wanting a reward then.");
				c.nextChat = 8102;
				break;
			case 8102:
				player(c, Anim.CALM_1, "Yes please!");
				c.nextChat = 8103;
				break;
			case 8103:
				// Give Ring 1 + Lamp
				c.getItems().addItem(RING_1, 1);
				c.getItems().addItem(LAMP, 1); 
				item("" ,c, RING_1, "Player receives Explorer's ring 1.");
				c.nextChat = 8104;
				break;
			case 8104:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"This ring is a symbol of your exploration of Lumbridge", 
					"and Draynor. It can recharge half of your run energy", 
					"twice per day and cast low level alchemy without runes",
					"30 times per day.");
				c.nextChat = 8105;
				break;
			case 8105:
				player(c, Anim.CALM_1, "Wow, thanks!");
				c.nextChat = 8106;
				break;
			case 8106:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "If you ever lose your ring, come back to me to reclaim it.");
				c.nextChat = 8001; // Back to main options
				break;

			// --- MEDIUM COMPLETION ---
			case 8200:
				player(c, Anim.CALM_1, "I've completed all of the medium tasks in my", "Lumbridge & Draynor achievement diary!");
				c.nextChat = 8201;
				break;
			case 8201:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "Quite... You'll be wanting a reward then.");
				c.nextChat = 8202;
				break;
			case 8202:
				player(c, Anim.CALM_1, "Yes please!");
				c.nextChat = 8203;
				break;
			case 8203:
				// Delete Ring 1, Give Ring 2 + Lamp
				if(c.getItems().playerHasItem(RING_1)) c.getItems().deleteItem(RING_1, 1);
				c.getItems().addItem(RING_2, 1);
				c.getItems().addItem(LAMP, 1);
				c.getDH().sendItemStatement("Player receives Explorer's ring 2.", RING_2);
				c.nextChat = 8204;
				break;
			case 8204:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"I've upgraded your Explorer's ring for you. It can", 
					"restore half of your run energy and teleport you to", 
					"the Draynor cabbage patch three times per day.");
				c.nextChat = 8205;
				break;
			case 8205:
				player(c, Anim.CALM_1, "Wow, thanks!");
				c.nextChat = 8001;
				break;

			// --- HARD COMPLETION ---
			case 8300:
				player(c, Anim.CALM_1, "I've completed all of the hard tasks in my", "Lumbridge & Draynor achievement diary!");
				c.nextChat = 8301;
				break;
			case 8301:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, "Quite... You'll be wanting a reward then.");
				c.nextChat = 8302;
				break;
			case 8302:
				player(c, Anim.CALM_1, "Yes please!");
				c.nextChat = 8303;
				break;
			case 8303:
				// Delete Ring 2, Give Ring 3 + Lamp
				if(c.getItems().playerHasItem(RING_2)) c.getItems().deleteItem(RING_2, 1);
				c.getItems().addItem(RING_3, 1);
				c.getItems().addItem(LAMP, 1);
				item("", c, RING_3, "Player receives Explorer's ring 3.");
				c.nextChat = 8304;
				break;
			case 8304:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"I've upgraded your Explorer's ring for you. It now", 
					"has unlimited cabbage teleports and four charges to", 
					"restore half your run energy. Collecting Tears of",
					"Guthix will now provide increased experience.");
				c.nextChat = 8305;
				break;
			case 8305:
				player(c, Anim.CALM_1, "Wow, thanks!");
				c.nextChat = 8001;
				break;

			// --- ELITE COMPLETION ---
			case 8400:
				player(c, Anim.CALM_1, "I've completed all of the elite tasks in my", "Lumbridge & Draynor achievement diary!");
				c.nextChat = 8401;
				break;
			case 8401:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"I can see that, well done! You've completed all of", 
					"the tasks in the Lumbridge & Draynor diary!", 
					"You've earned another reward.");
				c.nextChat = 8402;
				break;
			case 8402:
				// Delete Ring 3, Give Ring 4 + Lamp
				if(c.getItems().playerHasItem(RING_3)) c.getItems().deleteItem(RING_3, 1);
				c.getItems().addItem(RING_4, 1);
				c.getItems().addItem(LAMP, 1);
				item("", c, RING_4, "Player receives Explorer's ring 4.");
				c.nextChat = 8403;
				break;
			case 8403:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"I've upgraded your Explorer's ring for you. In addition", 
					"to the previous benefits you can now block an", 
					"additional slayer target, recharge all run energy 3x/day",
					"and cast high level alchemy thirty times per day.");
				c.nextChat = 8404;
				break;
			case 8404:
				npc(c, "Hatius Cosaintus", Anim.CALM_1, 
					"The Culinaromancer's chest is now 20% cheaper and", 
					"you'll receive double wire when thieving in", 
					"Dorgesh-Kaan.");
				c.nextChat = 8405;
				break;
			case 8405:
				player(c, Anim.CALM_1, "Wow, thanks!");
				c.nextChat = 8001;
				break;

			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		// MAIN MENU
		if (c.dialogueAction == 8001) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8002); break;  // Another Ring
			case OPT4_SECOND: next(c, 8005); break; // Who are you?
			case OPT4_THIRD: next(c, 8010); break;  // Question about Diary
			case OPT4_FOURTH: next(c, 8030); break; // Bye
			}
		}
		
		// SUB-MENU: QUESTIONS
		else if (c.dialogueAction == 8011) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8012); break;  // What is it?
			case OPT4_SECOND: next(c, 8013); break; // What are rewards?
			case OPT4_THIRD: next(c, 8028); break;  // How claim?
			case OPT4_FOURTH: next(c, 8030); break; // Bye
			}
		}
		
		// SUB-MENU: REWARDS TIERS
		else if (c.dialogueAction == 8015) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 8016); break;  // Easy
			case OPT4_SECOND: next(c, 8019); break; // Med
			case OPT4_THIRD: next(c, 8022); break;  // Hard
			case OPT4_FOURTH: next(c, 8025); break; // Elite
			}
		}
	}
}