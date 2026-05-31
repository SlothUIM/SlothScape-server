package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.ShieldArrav;

public class Baraek extends NPCDialogue {

	public Baraek(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 2881; 
	}
    @Override
	public String getDialogueRange() {
		return "2700-2785, 27131, 2881, 27141-27145,27241,27251";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 2881:
			switch (startDialogueId) {

			case 2700: // Entry Logic
				if (c.getItems().playerHasItem(958)) { // Grey Wolf Fur check bypasses menu
					player(c, Anim.CALM_1, "Would you like to buy my grey wolf fur?");
					c.nextChat = 2760;
				} else {
					// Check if quest is started but not at the 'Information Obtained' stage
					if (c.questStages[ShieldArrav.QUEST_ID] >= 1 && c.questStages[ShieldArrav.QUEST_ID] < 4) {
						options(c, 2701, 
							"Can you tell me where I can find the Phoenix Gang?", 
							"Can you sell me some furs?", 
							"Hello. I am in search of a quest.", 
							"Would you like to buy my fur?");
					} else {
						options(c, 2702, 
							"Can you sell me some furs?", 
							"Hello. I am in search of a quest.", 
							"Would you like to buy my fur?");
					}
				}
				break;

			// --- Phoenix Gang / Bribery Branch ---
			case 2710:
				player(c, Anim.CALM_1, "Can you tell me where I can find the Phoenix Gang?");
				c.nextChat = 2711;
				break;
			case 2711:
				npc(c, "Baraek", Anim.SCARED, "Sh sh sh, not so loud! You don't want to get me", "in trouble!");
				c.nextChat = 2712;
				break;
			case 2712:
				player(c, Anim.CALM_1, "So DO you know where they are?");
				c.nextChat = 2713;
				break;
			case 2713:
				npc(c, "Baraek", Anim.CALM_1, "I may do.", "But I don't want to get into trouble for", "revealing their hideout.");
				c.nextChat = 27131;
				break;
			case 27131:
				npc(c, "Baraek", Anim.CALM_1, "Of course, if I was, say 20 gold coins richer I may", "happen to be more inclined to take that sort of risk...");
				c.nextChat = 2714;
				break;
			case 2714:
				options(c, 2714, "Okay. Have 20 gold coins.", "No, I don't like things like bribery.", "Yes. I'd like to be 20 gold coins richer too.");
				break;
			
			// Bribery - No coins
			case 27141:
				player(c, Anim.SAD, "Uh....oops. My mistake. I don't have 20 coins. Silly me.");
				c.nextChat = -1;
				break;
			// Bribery - Success
			case 27142:
				player(c, Anim.CALM_1, "Okay. Have 20 gold coins.");
				c.nextChat = 27143;
				break;
			case 27143:
				npc(c, "Baraek", Anim.CALM_1, "Ok, to get to the gang hideout, enter Varrock through", "the south gate. Then, if you take the first turning east,", "somewhere along there is an alleyway to the south.", "The door at the end of there is the entrance to the Phoenix");
				c.nextChat = 27144;
				break;
			case 27144:
				npc(c, "Baraek", Anim.CALM_1, "Gang. They're operating there under the name of the", "VTAM Corporation. Be careful. The Phoenixes ain't the", "types to be messed about.");
				c.nextChat = 27145;
				break;
			case 27145:
				player(c, Anim.HAPPY, "Thanks!");
				c.nextChat = -1;
				break;

			// Bribery - Rejections
			case 2715: 
				player(c, Anim.CALM_1, "No. I don't like things like bribery.");
				c.nextChat = 2716;
				break;
			case 2716:
				npc(c, "Baraek", Anim.CALM_1, "Heh. If you wanna deal with the Phoenix Gang they're", "involved in much worse than a bit of bribery.");
				c.nextChat = -1;
				break;
			case 2717:
				player(c, Anim.CALM_1, "Yes. I'd like to be 20 gold coins richer too.");
				c.nextChat = -1;
				break;

			// --- Buying Furs FROM Baraek ---
			case 2720:
				player(c, Anim.CALM_1, "Can you sell me some furs?");
				c.nextChat = 2721;
				break;
			case 2721:
				npc(c, "Baraek", Anim.CALM_1, "Yeah, sure. They're 20 gold coins each.");
				c.nextChat = 2722;
				break;
			case 2722:
				options(c, 2722, "Yeah, okay, here you go.", "20 gold coins? That's an outrage!", "No thanks, I'll leave it.");
				break;
			case 27221: // Doesn't have 20 coins
				player(c, Anim.SAD, "Oh dear, I don't have enough money!");
				c.nextChat = 2723;
				break;
			case 2723: // Discount
				npc(c, "Baraek", Anim.CALM_1, "Well, my best price is 18 coins.");
				c.nextChat = 2724;
				break;
			case 2724:
				options(c, 2724, "OK, here you go.", "No thanks, I'll leave it.");
				break;
			case 27241: // Doesn't have 18 coins
				player(c, Anim.SAD, "Oh dear, I don't have that either.");
				c.nextChat = 2725;
				break;
			case 2725: // Feed family
				npc(c, "Baraek", Anim.CALM_1, "Well, I can't go any cheaper than that mate.", "I've got a family to feed.");
				c.nextChat = 27251;
				break;
			case 27251:
				player(c, Anim.SAD, "Oh well, never mind.");
				c.nextChat = -1;
				break;
			case 2726: // Outrage
				player(c, Anim.ANGRY, "20 gold coins? That's an outrage!");
				c.nextChat = 2725;
				break;
			case 2728: // Leave it
				player(c, Anim.CALM_1, "No thanks, I'll leave it.");
				c.nextChat = 2729;
				break;
			case 2729:
				npc(c, "Baraek", Anim.CALM_1, "It's your loss mate.");
				c.nextChat = -1;
				break;

			// --- Quest Dialogue ---
			case 2740:
				player(c, Anim.CALM_1, "Hello! I am in search of a quest.");
				c.nextChat = 2741;
				break;
			case 2741:
				npc(c, "Baraek", Anim.CONFUSED, "Sorry kiddo, I'm a fur trader not a damsel in distress.");
				c.nextChat = -1;
				break;

			// --- Selling Grey Wolf Furs TO Baraek ---
			case 2760:
				npc(c, "Baraek", Anim.TALKING_ALOT, "GREY WOLF FUR??? NOW you're talking!");
				c.nextChat = 2761;
				break;
			case 2761:
				npc(c, "Baraek", Anim.HAPPY, "Grey wolf fur is something of a desirable item to me.", "I'll take all you have for 120 gold pieces per fur,", "does that sound fair?");
				c.nextChat = 2763;
				break;
			case 2763:
				options(c, 2763, "Yep, sounds fine.", "No! I almost got my throat torn out by a wolf to get this!");
				break;
			case 2764:
				player(c, Anim.HAPPY, "Yep, that sounds fine.");
				c.nextChat = 2765;
				break;
			case 2765:
				player(c, Anim.HAPPY, "Thanks!");
				c.nextChat = -1;
				break;
			case 2766:
				player(c, Anim.ANGRY, "No! I almost got my throat torn out by a wolf to get this!");
				c.nextChat = -1;
				break;

			// --- Selling Normal/Bear Furs TO Baraek ---
			case 2770:
				player(c, Anim.CALM_1, "Would you like to buy my fur?");
				c.nextChat = 2771;
				break;
			case 2771:
				npc(c, "Baraek", Anim.CALM_1, "Let's have a look at it.");
				c.nextChat = 2772;
				break;
			case 2772:
				int bearAmt = c.getItems().getItemAmount(948);
				int furAmt = c.getItems().getItemAmount(6814);
				String type = bearAmt > 0 ? "bear fur" : "fur";
				String condition = bearAmt > 0 ? "It's not in the best condition." : "It's not in the best of condition.";
				String price = (bearAmt + furAmt) > 1 ? "give you 12 coins for each one." : "give you 12 coins for it.";
				
				npc(c, "Baraek", Anim.CALM_1, "You hand Baraek your " + type + " to look at.", condition, "I guess I could " + price);
				c.nextChat = 2773;
				break;
			case 2773:
				options(c, 2773, "Yeah, that'll do.", "I think I'll keep hold of it actually.");
				break;
			case 2774:
				player(c, Anim.CALM_1, "Yeah, that'll do.");
				c.nextChat = 2765; // Loops back to the "Thanks!" player text
				break;
			case 2776:
				player(c, Anim.CALM_1, "I think I'll keep hold of it actually!");
				c.nextChat = 2777;
				break;
			case 2777:
				npc(c, "Baraek", Anim.CALM_1, "Oh ok. Didn't want it anyway!");
				c.nextChat = -1;
				break;
				// --- Treasure Trails / Clue Scrolls ---
			case 2881: // Baraek
				if (c.getItems().playerHasItem(2835)) {
					c.getDH().sendDialogues(2780, c.talkingNpc); // Gives challenge scroll
				} else if (c.getItems().playerHasItem(2842)) {
					c.getDH().sendDialogues(2782, c.talkingNpc); // Asks for the answer
				} else {
					c.getDH().sendDialogues(2700, c.talkingNpc); // Standard dialogue
				}
				break;
				// Initial Clue Scroll interaction
				// --- Treasure Trails / Clue Scrolls ---
				
				// Initial Clue Scroll interaction
				// --- Treasure Trails / Clue Scrolls ---
				
				// Initial Clue Scroll interaction
				case 2780:
					npc(c, "Baraek", Anim.CALM_1, "I must ask you something...");
					c.nextChat = 2781;
					break;
				case 2781:
					c.getItems().deleteItem(2835, 1); // Delete the Clue Scroll
					c.getItems().addItem(2842, 1);    // Give Baraek's Challenge Scroll
					c.sendMessage("Baraek has given you a challenge scroll!");
					c.nextChat = -1; 
					break;
					
				// When talking to him again while holding the challenge scroll
				case 2782:
					npc(c, "Baraek", Anim.CALM_1, "Please enter the answer to the question.");
					c.nextChat = 27821;
					break;
				case 27821:
					// Triggers the "Enter Amount" input box
					c.getOutStream().createFrame(27);
					c.answeringChallengeNpc = "Baraek"; // Set the specific NPC name!
					c.nextChat = -1;
					break;

				// Wrong Answer
				case 2783: 
					npc(c, "Baraek", Anim.ANGRY, "Wrong! Try again.");
					c.nextChat = -1;
					break;

				// Correct Answer -> Calls your god class!
				case 2784: 
					npc(c, "Baraek", Anim.CALM_1, "Correct.");
					c.nextChat = 2785;
					break;
				case 2785:
					// Let the god class do the heavy lifting
					//TreasureTrails.progressClue(c, CHALLENGE_SCROLL_ID);
					c.nextChat = -1;
					break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		
		// 2701: Menu with Quest Option
		if (c.dialogueAction == 2701) {
			switch (buttonId) {
				case OPT4_FIRST: next(c, 2710); break;
				case OPT4_SECOND: next(c, 2720); break;
				case OPT4_THIRD: next(c, 2740); break;
				case OPT4_FOURTH: handleSellCheck(c); break;
			}
		}

		// 2702: Menu WITHOUT Quest Option
		else if (c.dialogueAction == 2702) {
			switch (buttonId) {
				case OPT3_FIRST: next(c, 2720); break;
				case OPT3_SECOND: next(c, 2740); break;
				case OPT3_THIRD: handleSellCheck(c); break;
			}
		}

		// 2714: Bribery Response Logic
		else if (c.dialogueAction == 2714) {
			switch (buttonId) {
				case OPT3_FIRST: // Pay 20
					if (c.getItems().playerHasItem(995, 20)) {
						c.getItems().deleteItem(995, 20);
						new ShieldArrav(c).setStage(4);
						next(c, 27142); // Send the "Okay. Have 20 gold coins." text -> leads to directions
					} else {
						next(c, 27141); // Send the "Oops, I don't have 20 coins" text
					}
					break;
				case OPT3_SECOND: // "I don't like bribery"
					next(c, 2715); 
					break;
				case OPT3_THIRD: // "I'd like to be richer too"
					next(c, 2717);
					break;
			}
		}

		// 2722 & 2724: Purchase from Baraek
		else if (c.dialogueAction == 2722 || c.dialogueAction == 2724) {
			int price = (c.dialogueAction == 2722) ? 20 : 18;
			switch (buttonId) {
				case OPT3_FIRST: 
				case OPT2_FIRST:
					if (c.getItems().playerHasItem(995, price)) {
						c.getItems().deleteItem(995, price);
						c.getItems().addItem(6814, 1);
						c.getPA().removeAllWindows();
						c.sendMessage("Baraek sells you a fur.");
					} else {
						if (price == 20) {
							next(c, 27221); // Player: "Oh dear..." -> leads to 18g discount
						} else {
							next(c, 27241); // Player: "Oh dear, I don't have that either"
						}
					}
					break;
				case OPT3_SECOND: // Outrage (only exists on 2722)
					next(c, 2726);
					break;
				case OPT3_THIRD: // Leave it (2722)
				case OPT2_SECOND: // Leave it (2724)
					next(c, 2728);
					break;
			}
		}

		// 2763: Finalizing Selling Grey Wolf Fur to Baraek
		else if (c.dialogueAction == 2763) {
			switch (buttonId) {
				case OPT2_FIRST: // "Yep, sounds fine."
					int wolfAmt = c.getItems().getItemAmount(958);
					if (wolfAmt > 0) {
						c.getItems().deleteItem(958, wolfAmt);
						c.getItems().addItem(995, wolfAmt * 120);
					}
					next(c, 2764); 
					break;
				case OPT2_SECOND: // "No!"
					next(c, 2766);
					break;
			}
		}

		// 2773: Finalizing Selling Regular/Bear Fur to Baraek
		else if (c.dialogueAction == 2773) {
			switch (buttonId) {
				case OPT2_FIRST: // "Yeah that'll do"
					int bearAmt = c.getItems().getItemAmount(948);
					int furAmt = c.getItems().getItemAmount(6814);
					if (bearAmt > 0) c.getItems().deleteItem(948, bearAmt);
					if (furAmt > 0) c.getItems().deleteItem(6814, furAmt);
					c.getItems().addItem(995, (bearAmt + furAmt) * 12);
					next(c, 2774);
					break;
				case OPT2_SECOND: // "I think I'll keep hold of it actually."
					next(c, 2776);
					break;
			}
		}
	}

	private void handleSellCheck(Player c) {
		if (c.getItems().playerHasItem(948) || c.getItems().playerHasItem(6814)) {
			next(c, 2770);
		} else {
			npc(c, "Baraek", Anim.CALM_1, "You don't have any furs to sell me!");
			c.nextChat = -1;
		}
	}
}