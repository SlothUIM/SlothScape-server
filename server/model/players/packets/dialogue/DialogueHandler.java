package server.model.players.packets.dialogue;

import server.util.Misc;
import server.world.World;
import server.Config;
import server.model.items.ItemAssistant;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.combat.effects.bolts.BoltEnchant;
import server.model.players.packets.dialogue.npc.BartenderBlueMoon;
import server.model.players.packets.dialogue.npc.HatiusCosaintus;
import server.model.players.packets.dialogue.npc.SilkMerchant;
import server.model.players.quests.*;
public class DialogueHandler {

	private Player c;

	public DialogueHandler(Player player) {
		this.c = player;
	}
	/**
	 * Handles all talking
	 * 
	 * @param dialogue
	 *            The dialogue you want to use
	 * @param npcId
	 *            The npc id that the chat will focus on during the chat
	 */
	public void sendDialogues(int dialogue, int npcId) {
		if(npcId != -1)
			c.talkingNpc = npcId;
		c.getSecurity().doorDialogues(dialogue, npcId);
		if(npcId == 4397) {
			QuestManager questManager = new WatchTower(c);
			questManager.handleDialogue(c, dialogue);// start at 200
			return;
		}
		switch (dialogue) {

		case 70300:
			sendOption2("Teleport to Catacombs of Kourend", "Nevermind");
			c.dialogueAction = 70300;
			c.nextChat = -1;
			break;
		case 468:
			sendOptions("Up","Down","Cancel","");
			c.dialogueAction = 468;
			break;
		
		case 642:
			sendOptions("Rotate clockwise",
					"Rotate counter-clockwise", "Remove","Cancel");
			c.dialogueAction = dialogue;
			break;
		/** Hairdresser dialogue */
		case 1111:
			//npcChat("Good afternoon, sir. In need of a haircut or shave, are", "we?", 598, "Hairdresser", 590);
			npcChat(1305, "Hairdresser", 590, "Good afternoon, sir. In need of a haircut or shave, are",  "we?");
			c.nextChat = 1112;
			break;

		case 1112:
			sendOptions("A haircut, please.", "A shave, please.", "No, thank you.");
			c.dialogueAction = 1112;
			break;

		case 1113:
			playerChat(Anim.CALM_1.getAnimationId(), "A haircut, please.");
			c.nextChat = 1114;
			break;

		case 1114:
			npcChat(1305, "Hairdresser", 590, "Certainly, sir. The fee will be 2,000 coins.");
			c.nextChat = 1115;
			break;

		case 1115:
			if (c.getItems().playerHasItem(995, 2000)) {
				npcChat(1305, "Hairdresser", 590, "Please select a hairstyle you would", "like from this brochure.");
				//npcTalk2("Please select a beard and color you would", "like from this brochure.", 598, "Hairdresser", 590);
				c.nextChat = 1116;
			} else {
				npcChat(1305, "Hairdresser", 610,"It looks like you don't have 2,000 coins,", "please revisit when you do.");
				c.nextChat = 0;
			}
			break;

		case 1116:
			c.getPA().showInterface(2653); // hairstyle interface
			break;

		case 1117:
			playerChat(Anim.CALM_1.getAnimationId(), "No, thank you.");
			c.nextChat = 1118;
			break;

		case 1118:
			npcChat(1305, "Hairdresser", 590, "Very well. Come back if you change your mind.");
			c.nextChat = 0;
			break;
			
		case 1119:
			if (c.getItems().playerHasItem(995, 2000)) {
				npcChat(1305, "Hairdresser", 590, "Please select a beard and color you would", "like from this brochure.");
				c.nextChat = 1120;
			} else {
				npcChat(1305, "Hairdresser", 610, "It looks like you don't have 2,000 coins,", "please revisit when you do.");
				c.nextChat = 0;
			}
			break;
			
		case 1120:
			c.getPA().showInterface(2007); // hair/beard interface
			break;
			
		case 1121:
			playerChat(Anim.CALM_1.getAnimationId(), "A shave, please.");
			c.nextChat = 1122;
			break;
			
		case 1122:
			npcChat(1305, "Hairdresser", 590, "Certainly, sir. The fee will be 2,000 coins.");
			c.nextChat = 1119;
			break;
			/** End of Hairdresser dialogue */
		case 0:
			c.talkingNpc = -1;
			c.getPA().removeAllWindows();
			c.nextChat = 0;
			break;
		case 1:
			sendPlayerChat1("Hello, how's it going?", Anim.CALM_1.getAnimationId());
			if(Misc.random(4) == 1)
			c.nextChat = 2;
			else
			c.nextChat = 3;
		break;
		case 2:
			sendNpcChat2("Not too bad, ", "But Im a little worried about the increase of goblins these days", c.talkingNpc, "Man", Anim.CALM_1.getAnimationId());
			c.nextChat = 0;
		break;
		case 3:
			npcChat(c.talkingNpc, "Man", Anim.CALM_1.getAnimationId(),  "How can I help you?");
			c.nextChat = 4;
		break;
		case 4:
			sendOption3("Do you want to trade?", "I'm in search of a quest.", "I'm in search of enemies to kill.");
			//c.nextChat = 1504;
			c.dialogueAction = 4;
		break;
			
		//Silk Trader dialogue
		case 16:
			sendNpcChat1("Do you want to buy any fine silks?", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
			c.nextChat = 17;
			break;
				case 17:
					sendOption2("How much are they?", "No. Silk doesn't suit me.");
					//Family Crest Dialogue
					//sendOption3("How much are they?", "No. Silk doesn't suit me.", "I'm in search of a man named Avan Fitzharmon");
					c.dialogueAction = 17;
					break;
					case 18:
						sendPlayerChat1("How much are they?", Anim.CALM_1.getAnimationId());
						c.nextChat = 19;
						break;
						case 19:
							sendNpcChat1("3 gp.", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
							c.nextChat = 20;
							break;
							case 20:
								sendOption2("No. That's too much for me.", "Okay, that sounds good.");
								c.dialogueAction = 20;
								break;
								case 21:
									sendPlayerChat1("No. That's too much for me", Anim.CALM_1.getAnimationId());
									c.nextChat = 22;
									break;
									case 22:
										sendNpcChat1("2 gp and that's as low as I'll go.", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
										c.nextChat = 23;
										break;
									case 23:
										sendNpcChat2("I'm not selling it for any less", "You'll probably go and sell it in Varrock for a profit, anyway.", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
										c.nextChat = 24;
										break;
										case 24:
											sendOption2("2 gp sounds good.", "No, really. I don't want it.");
											c.dialogueAction = 24;
											break;
											case 25:
												sendPlayerChat1("Oh dear. I don't have enough money.", Anim.CONFUSED.getAnimationId());
												c.nextChat = 26;
												break;
												case 26:
													sendNpcChat1("Well, come back when you do have some money!", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
													c.nextChat = 0;
													break;
											case 27:
												sendPlayerChat1("No, really. I don't want it.", Anim.CONFUSED.getAnimationId());
												c.nextChat = 28;
												break;
												case 28:
													sendNpcChat1("Okay, but that's the best price you're going to get.", c.talkingNpc, "Silk Trader", Anim.CALM_1.getAnimationId());
													c.nextChat = 0;
												break;
							case 29:
								sendPlayerChat1("Okay, that sounds good.", Anim.CALM_1.getAnimationId());
								c.nextChat = 30;
								break;	
								case 30:
									if(c.getItems().playerHasItem(995, 2)){
										c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 2);
										c.getItems().addItem(950, 1);
									c.nextChat = 0;
									c.dialogueAction = 0;
									c.getPA().closeAllWindows();
									} else
									c.nextChat = 25;
								break;
							case 31:
								sendPlayerChat1("No. Silk doesn't suit me.", Anim.CALM_1.getAnimationId());
								c.nextChat = 0;
							break;
							 case 3014:
					                sendItemChat(1511, 200, "", "You got some logs");
					                c.getPA().removeHintIcon(c);
					                c.nextChat = 3015;
					                break;
					            case 3015:
					                c.getPA().closeAllWindows();
					                c.getDH().chatboxText("Well done! You managed to cut some logs from the tree! Next,", "use the tinderbox in your inventory to light the logs.", "First click on the tinderbox to use it.", "Then click on the logs in your inventory to light them.", "Making a fire");
					                c.tutorialProgress = 4;
					                c.nextChat = 0;
					                break;

			/** Bank Settings **/
			case 1013:
				sendNpcChat1("Good day. How may I help you?", c.talkingNpc, "Banker", Anim.TALKING_ALOT.getAnimationId());
				c.nextChat = 1014;
				break;
			case 1014:// bank open done, this place done, settings done, to do
				// delete pin
				sendOption3("I'd like to access my bank account, please.", "I'd like to check my my P I N settings.", "What is this place?");
				c.dialogueAction = 251;
				break;
			/** What is this place? **/
			case 1015:
				sendPlayerChat1("What is this place?", Anim.TALKING_ALOT.getAnimationId());
				c.nextChat = 1016;
				break;
			case 1016:
				sendNpcChat2("This is the bank of " + Config.SERVER_NAME + ".", "We have many branches in many towns.", c.talkingNpc, "Banker", Anim.TALKING_ALOT.getAnimationId());
				c.nextChat = 0;
				break;
			/**
			 * Note on P I N. In order to check your "Pin Settings. You must have
			 * enter your Bank Pin first
			 **/
			/** I don't know option for Bank Pin **/
			case 1017:
				sendStartInfo("Since you don't know your P I N, it will be deleted in @red@3 days@bla@. If you", "wish to cancel this change, you may do so by entering your P I N", "correctly next time you attempt to use your bank.", "", "");
				c.nextChat = 0;
				break;
			
			
			case 2900:
				sendStatement("You've found a hidden tunnel, do you want to enter?");
				c.nextChat = 2905;
				c.dialogueAction = 29;
				break;
			case 2905:
				sendOption2("Yeah, I'm fearless!", "No way, that looks scary!");
				c.nextChat = 0;
				break;
			case 7286:
				c.getDH().sendOption2_withTitle("Yes, I understand I must buy my items for 500k on death.",
						"I need to prepare some more.", "@red@Fight Skotizo?" );
				c.dialogueAction = 7286;
				break;
			case 27419:
				sendOption3("Up", "Down", "Nevermind");
				c.dialogueAction = 27419;
				//c.nextChat = 0;
				break;

			case 278:
				if (c.getItems().playerHasItem(995, 2)) {
					sendPlayerChat1("Yes I would love a beer.", Anim.CALM_1.getAnimationId());
					c.getItems().deleteItem(995, 2);
					c.getItems().addItem(1917, 1);
					c.nextChat = 0;
				} else {
					sendPlayerChat1("I don't have enough coins to buy a beer.", Anim.CALM_1.getAnimationId());
					c.nextChat = 0;
				}
				break;
			case 6754:
				sendItemChat(1004, 200, "", "You have " + c.nmzCoffer + " coins stored in the coffer.");
				//sendPlayerChat1("I don't have enough coins to buy a beer.", Anim.CALM_1.getAnimationId());
				c.nextChat = 6755;
				break;
			case 6755:
				sendOptionsTitle("Dominic's Coffer("+c.nmzCoffer+")", "Deposit money.","Withdraw money.","Cancel.");
				c.dialogueAction = 6755;
				c.nextChat = -1;
				break;
		}
		DialogueService.open(c, npcId, dialogue);
	}		
	public void sendStatement(String... line) {
		switch (line.length) {
			case 1:
				c.getPA().sendFrame126(line[0], 357);
				c.getPA().showChatboxInterface(356);
			break;
			case 2:
				c.getPA().sendFrame126(line[0], 360);
				c.getPA().sendFrame126(line[1], 361);
				c.getPA().showChatboxInterface(359);
			break;
			case 3:
				c.getPA().sendFrame126(line[0], 364);
				c.getPA().sendFrame126(line[1], 365);
				c.getPA().sendFrame126(line[2], 366);
				c.getPA().showChatboxInterface(363);
			break;
			case 4:
				c.getPA().sendFrame126(line[0], 369);
				c.getPA().sendFrame126(line[1], 370);
				c.getPA().sendFrame126(line[2], 371);
				c.getPA().sendFrame126(line[3], 372);
				c.getPA().showChatboxInterface(368);
			break;
		}
	}
	public void sendItemChat(int item, int zoom, String header, String... line) {
		switch (line.length) {
			case 1:
				c.getPA().sendFrame246(4883, zoom, item);
				c.getPA().sendFrame126(header, 4884);
				c.getPA().sendFrame126(line[0], 4885);
				c.getPA().showChatboxInterface(4882);
			break;
			case 2:
				c.getPA().sendFrame246(4888, zoom, item);
				c.getPA().sendFrame126(header, 4889);
				c.getPA().sendFrame126(line[0], 4890);
				c.getPA().sendFrame126(line[1], 4891);
				c.getPA().showChatboxInterface(4887);
			break;
			case 3:
				c.getPA().sendFrame246(4894, zoom, item);
				c.getPA().sendFrame126(header, 4895);
				c.getPA().sendFrame126(line[0], 4896);
				c.getPA().sendFrame126(line[1], 4897);
				c.getPA().sendFrame126(line[2], 4898);
				c.getPA().showChatboxInterface(4893);
			break;
			case 4:
				c.getPA().sendFrame246(4901, zoom, item);
				c.getPA().sendFrame126(header, 4902);
				c.getPA().sendFrame126(line[0], 4903);
				c.getPA().sendFrame126(line[1], 4904);
				c.getPA().sendFrame126(line[2], 4905);
				c.getPA().sendFrame126(line[3], 4906);
				c.getPA().showChatboxInterface(4900);
			break;
		}
	}
	public void sendStatement2(String... line) {
		switch (line.length) {
			case 1:
				c.getPA().sendFrame126(line[0], 357);
				c.getPA().showChatboxInterface(356);
			break;
			case 2:
				c.getPA().sendFrame126(line[0], 360);
				c.getPA().sendFrame126(line[1], 361);
				c.getPA().showChatboxInterface(359);
			break;
			case 3:
				c.getPA().sendFrame126(line[0], 364);
				c.getPA().sendFrame126(line[1], 365);
				c.getPA().sendFrame126(line[2], 366);
				c.getPA().showChatboxInterface(363);
			break;
			case 4:
				c.getPA().sendFrame126(line[0], 369);
				c.getPA().sendFrame126(line[1], 370);
				c.getPA().sendFrame126(line[2], 371);
				c.getPA().sendFrame126(line[3], 372);
				c.getPA().showChatboxInterface(368);
			break;
		}
	}		
	public void chatboxText(String text, String text1, String text2,
			String text3, String title) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
	}
	public void clearChatBoxText(Player c2) {
		c2.getPA().sendFrame126("", 6180);
		c2.getPA().sendFrame126("", 6181);
		c2.getPA().sendFrame126("", 6182);
		c2.getPA().sendFrame126("", 6183);
		c2.getPA().sendFrame126("", 6184);
	}


	/*
	 * Information Box
	 */

	public void sendStartInfo(String text, String text1, String text2,
			String text3, String title) {
		c.getPA().sendFrame126(title, 6180);
		c.getPA().sendFrame126(text, 6181);
		c.getPA().sendFrame126(text1, 6182);
		c.getPA().sendFrame126(text2, 6183);
		c.getPA().sendFrame126(text3, 6184);
		c.getPA().showChatboxInterface(6179);
	}
	public void destroyInterface(String config) {// Destroy item created by Remco
		int itemId = c.droppedItem;
		String itemName = ItemAssistant.getItemName(c.droppedItem);
		String[][] info = { // The info the dialogue gives
				{ "Are you sure you want to " + config + " this item?", "44706" }, { "Yes.", "44708" },
				{ "No.", "44709" }, { "You can receive a replacement from Diango", "44707" }, { itemName, "44711" } };
		c.getPA().sendFrame34a(44710, itemId, 0, 1);
		for (String[] anInfo : info)
			c.getPA().sendFrame126(anInfo[0], Integer.parseInt(anInfo[1]));
		c.getPA().showChatboxInterface(44700);
	}
	public void boltEnchantInterface() {// Destroy item created by Remco
		BoltEnchant.open(c, true);
	
	}
	public void sendDestroy(String s, String s1, int itemID) {
		c.getPA().sendFrame126(s, 44711);
		c.getPA().sendFrame126(s1, 44707);
		c.getPA().showChatboxInterface(44700);
		c.getPA().sendFrame34a(44710, itemID, 0, 1);
	}	
	public String sendDestroyDialogue(int itemID) {
		if(itemID == 11941)
			return "You will have to buy another from a slayer master.";
		if(itemID >= 9096 && itemID <= 9104)
			return "The Oneiromancer might be able to help you get another.";
		
		return "You can receive a replacement from Diango.";
	}
	/*
	 * Options
	 */
	public void sendOptions(String... line) {
		switch (line.length) {
			case 2:
				c.getPA().sendFrame126("Select an Option", 2460);
				c.getPA().sendFrame126(line[0], 2461);
				c.getPA().sendFrame126(line[1], 2462);
				c.getPA().showChatboxInterface(2459);
			break;
			case 3:
				c.getPA().sendFrame126("Select an Option", 2470);
				c.getPA().sendFrame126(line[0], 2471);
				c.getPA().sendFrame126(line[1], 2472);
				c.getPA().sendFrame126(line[2], 2473);
				c.getPA().showChatboxInterface(2469);
			break;
			case 4:
				c.getPA().sendFrame126("Select an Option", 2481);
				c.getPA().sendFrame126(line[0], 2482);
				c.getPA().sendFrame126(line[1], 2483);
				c.getPA().sendFrame126(line[2], 2484);
				c.getPA().sendFrame126(line[3], 2485);
				c.getPA().showChatboxInterface(2480);
			break;
			case 5:
				c.getPA().sendFrame126("Select an Option", 2493);
				c.getPA().sendFrame126(line[0], 2494);
				c.getPA().sendFrame126(line[1], 2495);
				c.getPA().sendFrame126(line[2], 2496);
				c.getPA().sendFrame126(line[3], 2497);
				c.getPA().sendFrame126(line[4], 2498);
				c.getPA().showChatboxInterface(2492);
			break;
		}
	}		
	public void sendOptionsTitle(String title, String... line) {
		switch (line.length) {
			case 2:
				c.getPA().sendFrame126(title, 2460);
				c.getPA().sendFrame126(line[0], 2461);
				c.getPA().sendFrame126(line[1], 2462);
				c.getPA().showChatboxInterface(2459);
			break;
			case 3:
				c.getPA().sendFrame126(title, 2470);
				c.getPA().sendFrame126(line[0], 2471);
				c.getPA().sendFrame126(line[1], 2472);
				c.getPA().sendFrame126(line[2], 2473);
				c.getPA().showChatboxInterface(2469);
			break;
			case 4:
				c.getPA().sendFrame126(title, 2481);
				c.getPA().sendFrame126(line[0], 2482);
				c.getPA().sendFrame126(line[1], 2483);
				c.getPA().sendFrame126(line[2], 2484);
				c.getPA().sendFrame126(line[3], 2485);
				c.getPA().showChatboxInterface(2480);
			break;
			case 5:
				c.getPA().sendFrame126(title, 2493);
				c.getPA().sendFrame126(line[0], 2494);
				c.getPA().sendFrame126(line[1], 2495);
				c.getPA().sendFrame126(line[2], 2496);
				c.getPA().sendFrame126(line[3], 2497);
				c.getPA().sendFrame126(line[4], 2498);
				c.getPA().showChatboxInterface(2492);
			break;
		}
	}		
	public void sendOption2(String s, String s1) {
		c.getPA().sendFrame126("Select an Option", 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().showChatboxInterface(2459);
	}
	public void sendOption2_withTitle(String s, String s1, String title) {
		c.getPA().sendFrame126(title, 2460);
		c.getPA().sendFrame126(s, 2461);
		c.getPA().sendFrame126(s1, 2462);
		c.getPA().showChatboxInterface(2459);
	}

	public void sendOption3(String s, String s1, String s2) {
		c.getPA().sendFrame126("Select an Option", 2470);
		c.getPA().sendFrame126(s, 2471);
		c.getPA().sendFrame126(s1, 2472);
		c.getPA().sendFrame126(s2, 2473);
		c.getPA().showChatboxInterface(2469);
	}

	public void sendOption4(String s, String s1, String s2, String s3) {
		c.getPA().sendFrame126("Select an Option", 2481);
		c.getPA().sendFrame126(s, 2482);
		c.getPA().sendFrame126(s1, 2483);
		c.getPA().sendFrame126(s2, 2484);
		c.getPA().sendFrame126(s3, 2485);
		c.getPA().showChatboxInterface(2480);
	}

	public void sendOption5(String s, String s1, String s2, String s3, String s4) {
		c.getPA().sendFrame126("Select an Option", 2493);
		c.getPA().sendFrame126(s, 2494);
		c.getPA().sendFrame126(s1, 2495);
		c.getPA().sendFrame126(s2, 2496);
		c.getPA().sendFrame126(s3, 2497);
		c.getPA().sendFrame126(s4, 2498);
		c.getPA().showChatboxInterface(2492);
	}

	/*
	 * Statements
	 */
	public void sendStatement(String header, String one) {
		c.getPA().sendFrame246(4883, 0, -1);
		c.getPA().sendFrame126(header, 4884);
		c.getPA().sendFrame126(one, 4885);
		c.getPA().showChatboxInterface(4882);
	}
	public void sendStatement(String s) { // 1 line click here to continue chat
											// box interface
		c.getPA().sendFrame126(s, 357);
		c.getPA().showChatboxInterface(356);
	}

	/*
	 * Npc Chatting
	 */

			public void npcChat(int ChatNpc, String name, int animation, String... line) {
				switch (line.length) {
					case 1:
						c.getPA().sendFrame200(4883, animation);
						c.getPA().sendFrame126(name, 4884);
						c.getPA().sendFrame126(line[0], 4885);
						c.getPA().sendFrame75(ChatNpc, 4883);
						c.getPA().showChatboxInterface(4882);
					break;
					case 2:
						c.getPA().sendFrame200(4888, animation);
						c.getPA().sendFrame126(name, 4889);
						c.getPA().sendFrame126(line[0], 4890);
						c.getPA().sendFrame126(line[1], 4891);
						c.getPA().sendFrame75(ChatNpc, 4888);
						c.getPA().showChatboxInterface(4887);
					break;
					case 3:
						c.getPA().sendFrame200(4894, animation);	//Was 591
						c.getPA().sendFrame126(name, 4895);
						c.getPA().sendFrame126(line[0], 4896);
						c.getPA().sendFrame126(line[1], 4897);
						c.getPA().sendFrame126(line[2], 4898);
						c.getPA().sendFrame75(ChatNpc, 4894);
						c.getPA().showChatboxInterface(4893);
					break;
					case 4:
						c.getPA().sendFrame200(4901, animation);
						c.getPA().sendFrame126(name, 4902);
						c.getPA().sendFrame126(line[0], 4903);
						c.getPA().sendFrame126(line[1], 4904);
						c.getPA().sendFrame126(line[2], 4905);
						c.getPA().sendFrame126(line[3], 4906);
						c.getPA().sendFrame75(ChatNpc, 4901);
						c.getPA().showChatboxInterface(4900);
					break;
				}
			}
			public void playerChat(int animation, String... line) {
				switch (line.length) {
						case 1:
							c.getPA().sendFrame200(969, animation);
							c.getPA().sendFrame126(c.playerName, 970);
							c.getPA().sendFrame126(line[0], 971);
							c.getPA().sendFrame185(969);
							c.getPA().showChatboxInterface(968);
						break;
						case 2:
							c.getPA().sendFrame200(974, animation);
							c.getPA().sendFrame126(c.playerName, 975);
							c.getPA().sendFrame126(line[0], 976);
							c.getPA().sendFrame126(line[1], 977);
							c.getPA().sendFrame185(974);
							c.getPA().showChatboxInterface(973);
						break;
						case 3:
							c.getPA().sendFrame200(980, animation);
							c.getPA().sendFrame126(c.playerName, 981);
							c.getPA().sendFrame126(line[0], 982);
							c.getPA().sendFrame126(line[1], 983);
							c.getPA().sendFrame126(line[2], 984);
							c.getPA().sendFrame185(980);
							c.getPA().showChatboxInterface(979);
						break;
						case 4:
							c.getPA().sendFrame200(987, animation);
							c.getPA().sendFrame126(c.playerName, 988);
							c.getPA().sendFrame126(line[0], 989);
							c.getPA().sendFrame126(line[1], 990);
							c.getPA().sendFrame126(line[2], 991);
							c.getPA().sendFrame126(line[3], 992);
							c.getPA().sendFrame185(987);
							c.getPA().showChatboxInterface(986);
						break;
				}
			}
			public void sendNpcChat1(String s, int ChatNpc, String name, int animation) {
				c.getPA().sendFrame200(4883, 591);
				c.getPA().sendFrame126(name, 4884);
				c.getPA().sendFrame126(s, 4885);
				c.getPA().sendFrame75(ChatNpc, 4883);
				c.getPA().showChatboxInterface(4882);
			}
			public void sendNpcChat1(String s, int ChatNpc, String name) {
				c.getPA().sendFrame200(4883, 591);
				c.getPA().sendFrame126(name, 4884);
				c.getPA().sendFrame126(s, 4885);
				c.getPA().sendFrame75(ChatNpc, 4883);
				c.getPA().showChatboxInterface(4882);
			}
			public void sendNpcChat2(String s, String s1, int ChatNpc, String name, int animation) {
				c.getPA().sendFrame200(4883, 591);
				c.getPA().sendFrame126(name, 4889);
				c.getPA().sendFrame126(s, 4890);
				c.getPA().sendFrame126(s1, 4891);
				c.getPA().sendFrame75(ChatNpc, 4888);
				c.getPA().showChatboxInterface(4882);
			}
			public void sendNpcChat2(String s, String s1, int ChatNpc, String name) {
				c.getPA().sendFrame200(4883, 591);
				c.getPA().sendFrame126(name, 4889);
				c.getPA().sendFrame126(s, 4890);
				c.getPA().sendFrame126(s1, 4891);
				c.getPA().sendFrame75(ChatNpc, 4888);
				c.getPA().showChatboxInterface(4882);
			}
	
		public void sendNpcChat3(String s, String s1, String s2, int ChatNpc, String name, int animation) {
			c.getPA().sendFrame200(4883, 591);	//Was 591
			c.getPA().sendFrame126(name, 4895);
			c.getPA().sendFrame126(s, 4896);
			c.getPA().sendFrame126(s1, 4897);
			c.getPA().sendFrame126(s2, 4898);
			c.getPA().sendFrame75(ChatNpc, 4894);
			c.getPA().showChatboxInterface(4893);
		}
		public void sendNpcChat3(String s, String s1, String s2, int ChatNpc, String name) {
			c.getPA().sendFrame200(4883, 591);	//Was 591
			c.getPA().sendFrame126(name, 4895);
			c.getPA().sendFrame126(s, 4896);
			c.getPA().sendFrame126(s1, 4897);
			c.getPA().sendFrame126(s2, 4898);
			c.getPA().sendFrame75(ChatNpc, 4894);
			c.getPA().showChatboxInterface(4893);
		}
	
		public void sendNpcChat4(String s, String s1, String s2, String s3, int ChatNpc, String name, int animation) {
			c.getPA().sendFrame200(4883, 591);
			c.getPA().sendFrame126(name, 4902);
			c.getPA().sendFrame126(s, 4903);
			c.getPA().sendFrame126(s1, 4904);
			c.getPA().sendFrame126(s2, 4905);
			c.getPA().sendFrame126(s3, 4906);
			c.getPA().sendFrame75(ChatNpc, 4901);
			c.getPA().showChatboxInterface(4900);
		}
		public void sendNpcChat4(String s, String s1, String s2, String s3, int ChatNpc, String name) {
			c.getPA().sendFrame200(4883, 591);
			c.getPA().sendFrame126(name, 4902);
			c.getPA().sendFrame126(s, 4903);
			c.getPA().sendFrame126(s1, 4904);
			c.getPA().sendFrame126(s2, 4905);
			c.getPA().sendFrame126(s3, 4906);
			c.getPA().sendFrame75(ChatNpc, 4901);
			c.getPA().showChatboxInterface(4900);
		}
	//endregion

	/*
	 * Player Chating Back
	 */
	 
	//region send player chat methods
	public void sendPlayerChat1(String s, int animationId) {
		c.getPA().sendFrame200(969, 591);
		c.getPA().sendFrame126(c.playerName, 970);
		c.getPA().sendFrame126(s, 971);
		c.getPA().sendFrame185(969);
		c.getPA().showChatboxInterface(968);
	}

	public void sendPlayerChat2(String s, String s1, int animationId) {
		c.getPA().sendFrame200(974, 591);
		c.getPA().sendFrame126(c.playerName, 975);
		c.getPA().sendFrame126(s, 976);
		c.getPA().sendFrame126(s1, 977);
		c.getPA().sendFrame185(974);
		c.getPA().showChatboxInterface(973);
	}

	public void sendPlayerChat3(String s, String s1, String s2, int animationId) {
		c.getPA().sendFrame200(980, 591);
		c.getPA().sendFrame126(c.playerName, 981);
		c.getPA().sendFrame126(s, 982);
		c.getPA().sendFrame126(s1, 983);
		c.getPA().sendFrame126(s2, 984);
		c.getPA().sendFrame185(980);
		c.getPA().showChatboxInterface(979);
	}

	public void sendPlayerChat4(String s, String s1, String s2, String s3, int animationId) {
		c.getPA().sendFrame200(987, 591);
		c.getPA().sendFrame126(c.playerName, 988);
		c.getPA().sendFrame126(s, 989);
		c.getPA().sendFrame126(s1, 990);
		c.getPA().sendFrame126(s2, 991);
		c.getPA().sendFrame126(s3, 992);
		c.getPA().sendFrame185(987);
		c.getPA().showChatboxInterface(986);
	}
	public void sendItemStatement(String text, int item) {
		c.getPA().sendFrame126(text, 308);
		c.getPA().sendFrame246(307, 200, item);
		c.getPA().showChatboxInterface(306);
	}
	public void sendItemStatement(String header, int item, int zoom, String... text) {
		switch(text.length) {
		case 1:
		c.getPA().sendFrame246(4883, zoom, item);
		c.getPA().sendFrame126(header, 4884);
		c.getPA().sendFrame126(text[0], 4885);
		c.getPA().showChatboxInterface(4882);
		break;
		case 2:
			c.getPA().sendFrame246(4888, zoom, item);
			c.getPA().sendFrame126(header, 4889);
			c.getPA().sendFrame126(text[0], 4890);
			c.getPA().sendFrame126(text[1], 4891);
			c.getPA().showChatboxInterface(4887);
			break;
		case 3:
			c.getPA().sendFrame246(4894, zoom, item);
			c.getPA().sendFrame126(header, 4895);
			c.getPA().sendFrame126(text[0], 4896);
			c.getPA().sendFrame126(text[1], 4897);
			c.getPA().sendFrame126(text[2], 4898);
			c.getPA().showChatboxInterface(4893);
			break;
		case 4:
			c.getPA().sendFrame246(4901, zoom, item);
			c.getPA().sendFrame126(header, 4902);
			c.getPA().sendFrame126(text[0], 4903);
			c.getPA().sendFrame126(text[1], 4904);
			c.getPA().sendFrame126(text[2], 4905);
			c.getPA().sendFrame126(text[3], 4906);
			c.getPA().showChatboxInterface(4900);
			break;
		
		}
	}
	//endregion
	 	//region send item chat methods
	public void sendItemChat1(String header, String one, int item, int zoom) {
		c.getPA().sendFrame246(4883, zoom, item);
		c.getPA().sendFrame126(header, 4884);
		c.getPA().sendFrame126(one, 4885);
		c.getPA().showChatboxInterface(4882);
	}

	public void sendItemChat2(String header, String one, String two, int item,
			int zoom) {
		c.getPA().sendFrame246(4888, zoom, item);
		c.getPA().sendFrame126(header, 4889);
		c.getPA().sendFrame126(one, 4890);
		c.getPA().sendFrame126(two, 4891);
		c.getPA().showChatboxInterface(4887);
	}

	public void sendItemChat3(String header, String one, String two,
			String three, int item, int zoom) {
		c.getPA().sendFrame246(4894, zoom, item);
		c.getPA().sendFrame126(header, 4895);
		c.getPA().sendFrame126(one, 4896);
		c.getPA().sendFrame126(two, 4897);
		c.getPA().sendFrame126(three, 4898);
		c.getPA().showChatboxInterface(4893);
	}

	public void sendItemChat4(String header, String one, String two,
			String three, String four, int item, int zoom) {
		c.getPA().sendFrame246(4901, zoom, item);
		c.getPA().sendFrame126(header, 4902);
		c.getPA().sendFrame126(one, 4903);
		c.getPA().sendFrame126(two, 4904);
		c.getPA().sendFrame126(three, 4905);
		c.getPA().sendFrame126(four, 4906);
		c.getPA().showChatboxInterface(4900);
	}
	//endregion
}
