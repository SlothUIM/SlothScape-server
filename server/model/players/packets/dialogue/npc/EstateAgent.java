package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.skills.Skill;
import server.model.players.skills.construction.Construction;
import server.model.players.skills.construction.ConstructionData;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.construction.HouseLocation;
import server.model.players.skills.construction.util.*;

public class EstateAgent extends NPCDialogue {

	public EstateAgent(Player c) {
		super(c);
	}

	@Override
	public int[] getNPCIDs() {
		// 4247 = Varrock, 4248 = Ardougne, 4249 = Falador, 4250 = Seers', 4251 = Hosidius, 4252 = Prifddinas
		return new int[] { 3097, 4248, 4249, 4250, 4251, 4252 }; 
	}
    @Override
	public String getDialogueRange() {
		return "6700-6858";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (startDialogueId) {

		// --- Entry Point ---
		case 6700:
			npc(c, "Estate agent", Anim.CALM_1, "Hello. Welcome to the Gielinor Housing Agency! What", "can I do for you?");
			
			// Dynamically check if the player owns a house by looking for the center chunk (7, 7)
			boolean hasHouse = false;
			if (c.getHouseRooms() != null && c.getHouseRooms()[0][7][7] != null) {
				hasHouse = true;
			}
			
			if (!hasHouse) {
				c.nextChat = 6701; // Pre-house options
			} else {
				c.nextChat = 6800; // Post-house options
			}
			break;

		// ==============================================
		// MENU 1: NO HOUSE YET
		// ==============================================
		case 6701:
			options(c, 6701, "How can I get a house?", "Tell me about houses!");
			break;

		case 6705:
			player(c, Anim.CALM_1, "How can I get a house?");
			c.nextChat = 6706;
			break;
		case 6706:
			npc(c, "Estate agent", Anim.CALM_1, "I can sell you a starting house in Rimmington for 1000", "coins. As you increase your construction skill you will", "be able to have your house moved to other areas and", "redecorated in other styles.");
			c.nextChat = 6707;
			break;
		case 6707:
			npc(c, "Estate agent", Anim.CALM_1, "Do you want to buy a starter house?");
			c.nextChat = 6708;
			break;
		case 6708:
			options(c, 6708, "Yes please!", "No thanks.");
			break;
			
		// Buy House Branch
		case 6710:
			player(c, Anim.HAPPY, "Yes please!");
			c.nextChat = 6711;
			break;
		case 6711:
			if (c.getItems().playerHasItem(995, 1000)) {
				c.getItems().deleteItem(995, 1000);
				c.houseLocation = HouseLocation.RIMMINGTON;
				HouseData.newHouse(c);
				
				c.getItems().addItem(8463, 1); // Construction guide
				npc(c, "Estate agent", Anim.HAPPY, "Thank you. Go through the Rimmington house portal", "and you will find your house ready for you to start", "building in it.");
				c.nextChat = 6712;
			} else {
				player(c, Anim.SAD, "I haven't got 1,000 coins on me.");
				c.nextChat = 6713;
			}
			break;
		case 6712:
			item(c, 8463, "This book will help you to start building your house.", "(You receive a Construction guide.)");
			c.nextChat = -1;
			break;
		case 6713:
			npc(c, "Estate agent", Anim.CALM_1, "Well come back when you have it then.");
			c.nextChat = -1;
			break;

		// Reject House Branch
		case 6715:
			player(c, Anim.CALM_1, "No thanks.");
			c.nextChat = 6716;
			break;
		case 6716:
			npc(c, "Estate agent", Anim.ANGRY, "Well enjoy your player-owned cardboard box or", "wherever you're going to sleep tonight!");
			c.nextChat = -1;
			break;

		// ==============================================
		// LORE: TELL ME ABOUT HOUSES
		// ==============================================
		case 6720:
			player(c, Anim.CALM_1, "Tell me about houses!");
			c.nextChat = 6721;
			break;
		case 6721:
			npc(c, "Estate agent", Anim.CALM_1, "It all came out of the wizards' experiments. They found", "a way to fold space, so that they could pack many", "acres of land into an area only a foot across.");
			c.nextChat = 6722;
			break;
		case 6722:
			npc(c, "Estate agent", Anim.CALM_1, "They created several folded-space regions across", "Gielinor. Each one contains hundreds of small plots", "where people can build houses.");
			c.nextChat = 6723;
			break;
		case 6723:
			player(c, Anim.HAPPY, "Ah, so that's how everyone can have a house without", "them cluttering up the world!");
			c.nextChat = 6724;
			break;
		case 6724:
			npc(c, "Estate agent", Anim.CALM_1, "Quite. The wizards didn't want to get bogged down in", "the business side of things so they hired me to sell the", "houses. There are various other people across Gielinor", "who can help you furnish your house.");
			c.nextChat = 6725;
			break;
		case 6725:
			npc(c, "Estate agent", Anim.CALM_1, "You should start by buying planks from a sawmill", "operator. Sawmills are located in north-east Varrock,", "the Woodcutting Guild located in south-west Hosidius", "and north-east Prifddinas.");
			c.nextChat = -1;
			break;

		// ==============================================
		// MENU 2: ALREADY OWNS HOUSE
		// ==============================================
		case 6800:
			if (c.getSkills().getLevel(Skill.CONSTRUCTION) >= 99) {
				options(c, 6800, 
					"Can you move my house please?", 
					"Can you redecorate my house please?", 
					"Could I have a Construction guide book?", 
					"Tell me about houses!", 
					"Can you sell me a Skillcape of Construction?");
			} else {
				options(c, 6801, 
					"Can you move my house please?", 
					"Can you redecorate my house please?", 
					"Could I have a Construction guide book?", 
					"Tell me about houses!", 
					"Tell me about that skillcape you're wearing.");
			}
			break;

		// Relocation & Redecoration Hooks
		case 6810:
			player(c, Anim.CALM_1, "Can you move my house please?");
			c.nextChat = 6811;
			break;
		case 6811:
			npc(c, "Estate agent", Anim.CALM_1, "Where would you like it moved to?");
			c.nextChat = 6812;
			break;
		case 6812:
			openLocationInterface(c);
			c.nextChat = -1;
			break;

		case 6822:
			openStyleInterface(c);
			c.nextChat = -1;
			break;
		case 6820:
			player(c, Anim.CALM_1, "Can you redecorate my house please?");
			c.nextChat = 6821;
			break;
		case 6821:
			npc(c, "Estate agent", Anim.HAPPY, "Certainly. My magic can rebuild the house in a", "completely new style! What style would you like?");
			c.nextChat = 6822;
			break;

		// Guide Book
		case 6830:
			player(c, Anim.CALM_1, "Could I have a Construction guide book?");
			c.nextChat = 6831;
			break;
		case 6831:
			if (c.getItems().playerHasItem(8463)) {
				npc(c, "Estate agent", Anim.CALM_1, "You've already got one!");
				c.nextChat = -1;
			} else if (c.getItems().freeSlots() == 0) {
				npc(c, "Estate agent", Anim.CALM_1, "Not until you free up some inventory space.");
				c.nextChat = -1;
			} else {
				npc(c, "Estate agent", Anim.CALM_1, "Certainly.");
				c.nextChat = 6832;
			}
			break;
		case 6832:
			c.getItems().addItem(8463, 1);
			item(c, 8463, "(You receive a Construction guide.)");
			c.nextChat = -1;
			break;

		// ==============================================
		// SKILLCAPE LORE & PURCHASING
		// ==============================================
		case 6840:
			player(c, Anim.CALM_1, "Tell me about that skillcape you're wearing.");
			c.nextChat = 6841;
			break;
		case 6841:
			npc(c, "Estate agent", Anim.HAPPY, "As you may know, skillcapes are only available to", "masters in a skill. I have spent my entire life building", "houses and now I spend my time selling them! As a", "sign of my abilities I wear this Skillcape of Construction.");
			c.nextChat = 6842;
			break;
		case 6842:
			npc(c, "Estate agent", Anim.CALM_1, "If you ever have enough skill to build a demonic throne,", "come and talk to me and I'll sell you a skillcape like", "mine.");
			c.nextChat = 6843;
			break;
		case 6843:
			npc(c, "Estate agent", Anim.CALM_1, "The Cape of Construction also provides you with as", "many teleports as you like to any of the house portals", "or even directly inside!");
			c.nextChat = -1;
			break;

		case 6850:
			player(c, Anim.CALM_1, "Can you sell me a Skillcape of Construction?");
			c.nextChat = 6851;
			break;
		case 6851:
			npc(c, "Estate agent", Anim.HAPPY, "Alright, that'll be 99000 coins, please. A small price to", "pay for such an illustrious cape that can teleport you to", "any of the house portals or even directly inside your", "house!");
			c.nextChat = 6852;
			break;
		case 6852:
			options(c, 6852, "Certainly, that sounds fair.", "I'm not paying that!");
			break;
		
		case 6853:
			player(c, Anim.ANGRY, "I'm not paying that!");
			c.nextChat = 6854;
			break;
		case 6854:
			npc(c, "Estate agent", Anim.CALM_1, "Suit yourself. There are many other heroes who would", "love the opportunity to purchase one. You can find me", "here if you change your mind.");
			c.nextChat = -1;
			break;

		case 6855:
			player(c, Anim.HAPPY, "Certainly, that sounds fair.");
			c.nextChat = 6856;
			break;
		case 6856:
			if (!c.getItems().playerHasItem(995, 99000)) {
				player(c, Anim.SAD, "But, unfortunately, I don't have enough money with", "me.");
				c.nextChat = 6857;
			} else if (c.getItems().freeSlots() < 2) {
				npc(c, "Estate agent", Anim.CALM_1, "Unfortunately all Skillcapes are only available with a", "free hood, it's part of a skill promotion deal; buy one", "get one free, you know. So you'll need to free up some", "inventory space before I can sell you one.");
				c.nextChat = -1;
			} else {
				c.getItems().deleteItem(995, 99000);
				// 9789 = Construction Cape, 9791 = Construction Hood
				c.getItems().addItem(9789, 1); 
				c.getItems().addItem(9791, 1);
				item(c, 9789, "(You receive a Construction cape and hood.)");
				c.nextChat = 6858;
			}
			break;
		case 6857:
			npc(c, "Estate agent", Anim.CALM_1, "Well, come back and see me when you do.");
			c.nextChat = -1;
			break;
		case 6858:
			npc(c, "Estate agent", Anim.HAPPY, "Excellent! Wear that cape with pride my friend.");
			c.nextChat = -1;
			break;
		}
	}
	public static void openLocationInterface(Player c) {
		c.getPA().sendFrame126("Choose a Location:", 62398);
		c.getPA().sendFrame126("Rimmington (Lvl 1) - 5,000 gp", 62399);
		c.getPA().sendFrame126("Taverley (Lvl 10) - 5,000 gp", 62400);
		c.getPA().sendFrame126("Pollnivneach (Lvl 20) - 7,500 gp", 62401);
		c.getPA().sendFrame126("Hosidius (Lvl 25) - 8,750 gp", 62402);
		c.getPA().sendFrame126("Rellekka (Lvl 30) - 10,000 gp", 62403);
		c.getPA().sendFrame126("Brimhaven (Lvl 40) - 15,000 gp", 62404);
		c.getPA().sendFrame126("Yanille (Lvl 50) - 25,000 gp", 62405);
		c.getPA().sendFrame126("Prifddinas (Lvl 70) - 50,000 gp", 62406);
		
		// Clear remaining lines
		for (int i = 62407; i <= 62411; i++) {
			c.getPA().sendFrame126("", i);
		}
		
		c.dialogueAction = 100; // Flag so the server knows we are moving the house
		c.getPA().showInterface(62396);
	}

	public static void openStyleInterface(Player c) {
		c.getPA().sendFrame126("Choose a Style:", 62398);
		c.getPA().sendFrame126("Basic wood - 5,000 gp", 62399);
		c.getPA().sendFrame126("Basic stone - 5,000 gp", 62400);
		c.getPA().sendFrame126("Whitewashed stone - 7,500 gp", 62401);
		c.getPA().sendFrame126("Fremennik-style wood - 10,000 gp", 62402);
		c.getPA().sendFrame126("Tropical wood - 15,000 gp", 62403);
		c.getPA().sendFrame126("Fancy stone - 25,000 gp", 62404);
		
		// Clear remaining lines
		for (int i = 62405; i <= 62411; i++) {
			c.getPA().sendFrame126("", i);
		}
		
		c.dialogueAction = 101; // Flag so the server knows we are changing style
		c.getPA().showInterface(62396);
	}
	@Override
	public void onOption(Player c, int buttonId) {
		
		// 6701: Pre-house menu
		if (c.dialogueAction == 6701) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 6705); break; // How can I get a house?
				case OPT2_SECOND: next(c, 6720); break; // Tell me about houses!
			}
		}
		// 6708: Buy Starter House
		else if (c.dialogueAction == 6708) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 6710); break; // Yes please!
				case OPT2_SECOND: next(c, 6715); break; // No thanks.
			}
		}
		// 6800: Post-house menu (Level 99)
		else if (c.dialogueAction == 6800) {
			switch (buttonId) {
				case OPT5_FIRST: next(c, 6810); break; // Move house
				case OPT5_SECOND: next(c, 6820); break; // Redecorate
				case OPT5_THIRD: next(c, 6830); break; // Guide Book
				case OPT5_FOURTH: next(c, 6720); break; // Lore
				case OPT5_FIFTH: next(c, 6850); break; // Buy Skillcape
			}
		}
		// 6801: Post-house menu (NOT Level 99)
		else if (c.dialogueAction == 6801) {
			switch (buttonId) {
				case OPT5_FIRST: next(c, 6810); break; // Move house
				case OPT5_SECOND: next(c, 6820); break; // Redecorate
				case OPT5_THIRD: next(c, 6830); break; // Guide Book
				case OPT5_FOURTH: next(c, 6720); break; // Lore
				case OPT5_FIFTH: next(c, 6840); break; // Skillcape Lore
			}
		}
		// 6822: Redecorate Options (Dialogue implementation)
		else if (c.dialogueAction == 6822) {
			switch (buttonId) {
				case OPT5_FIRST: // Basic Wood
					if (c.getItems().playerHasItem(995, 5000)) {
						c.getItems().deleteItem(995, 5000);
						server.model.players.skills.construction.Construction.updateHouseStyle(c, 0); 
						server.model.players.skills.construction.Construction.saveHouse(c);
						c.sendMessage("Your house has been redecorated to Basic Wood.");
						c.getPA().closeAllWindows();
					} else {
						c.sendMessage("You need 5,000 coins to redecorate.");
						c.getPA().closeAllWindows();
					}
					break;
				case OPT5_SECOND: // Basic Stone
					if (c.getItems().playerHasItem(995, 5000)) {
						c.getItems().deleteItem(995, 5000);
						server.model.players.skills.construction.Construction.updateHouseStyle(c, 1); 
						server.model.players.skills.construction.Construction.saveHouse(c);
						c.sendMessage("Your house has been redecorated to Basic Stone.");
						c.getPA().closeAllWindows();
					} else {
						c.sendMessage("You need 5,000 coins to redecorate.");
						c.getPA().closeAllWindows();
					}
					break;
				case OPT5_THIRD: // Whitewashed Stone
					if (c.getItems().playerHasItem(995, 7500)) {
						c.getItems().deleteItem(995, 7500);
						server.model.players.skills.construction.Construction.updateHouseStyle(c, 2); 
						server.model.players.skills.construction.Construction.saveHouse(c);
						c.sendMessage("Your house has been redecorated to Whitewashed Stone.");
						c.getPA().closeAllWindows();
					} else {
						c.sendMessage("You need 7,500 coins to redecorate.");
						c.getPA().closeAllWindows();
					}
					break;
				case OPT5_FOURTH: // Fremennik-style wood
					if (c.getItems().playerHasItem(995, 10000)) {
						c.getItems().deleteItem(995, 10000);
						server.model.players.skills.construction.Construction.updateHouseStyle(c, 3); 
						server.model.players.skills.construction.Construction.saveHouse(c);
						c.sendMessage("Your house has been redecorated to Fremennik-style wood.");
						c.getPA().closeAllWindows();
					} else {
						c.sendMessage("You need 10,000 coins to redecorate.");
						c.getPA().closeAllWindows();
					}
					break;
				case OPT5_FIFTH: 
					c.getPA().closeAllWindows(); 
					break;
			}
		}
		// 6852: Buy Skillcape Options
		else if (c.dialogueAction == 6852) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 6855); break; // Yes, that sounds fair
				case OPT2_SECOND: next(c, 6853); break; // Not paying that
			}
		}
	}
}