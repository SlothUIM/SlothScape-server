package server.model.players.packets.dialogue.npc.areas.asgarnia.quests.RuneMysteries;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.RuneMysteries;

public class Aubury extends NPCDialogue {

	public Aubury(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 2886; 
	}
	@Override
	public String getDialogueRange() {
	    return "4000-4062";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 2886:
			switch (startDialogueId) {

			case 4000:
				int rmStage = c.questStages[RuneMysteries.QUEST_ID];

				// Stage 2: Player arrives with Research Package
				if (rmStage == 2) {
					npc(c, "Aubury", Anim.CALM_1, "Do you want to buy some runes?");
					c.nextChat = 4050;
				} 
				// Stage 3: Player has Translation Notes
				else if (rmStage == 3) {
					if (c.getItems().playerHasItem(1437)) {
						npc(c, "Aubury", Anim.CALM_1, "Don't take too long. He'll be eager to see if this is", "indeed the breakthrough we were hoping for.");
						c.nextChat = -1;
					} else {
						npc(c, "Aubury", Anim.CALM_1, "Hello. Did you take those notes back to Sedridor?");
						c.nextChat = 4060; // Lost Notes
					}
				}
				// Standard Shop/Teleport Dialogue
				else {
					npc(c, "Aubury", Anim.CALM_1, "Do you want to buy some runes?");
					c.nextChat = 4001;
				}
				break;

			case 4001:
				if (c.questStages[RuneMysteries.QUEST_ID] >= 4) {
					options(c, 4001, 
						"Can you tell me about your cape?", 
						"Yes please!", 
						"Oh, it's a rune shop. No thank you, then.", 
						"Can you teleport me to the Rune Essence?");
				} else {
					options(c, 4002, 
						"Can you tell me about your cape?", 
						"Yes please!", 
						"Oh, it's a rune shop. No thank you, then.");
				}
				break;

			// ==============================================
			// QUEST STAGE 2: Deliver Package to Aubury
			// ==============================================
			case 4050:
				options(c, 4050, "Yes please!", "I've been sent here with a package for you.");
				break;
			case 4051:
				if (c.getItems().playerHasItem(1436)) {
					player(c, Anim.CALM_1, "I've been sent here with a package for you.");
					c.nextChat = 4052;
				} else {
					player(c, Anim.SAD, "Uh... yeah... about that... I kind of don't", "have it with me...");
					c.nextChat = 4057; // Tries to hand in package but doesn't have it
				}
				break;
			case 4052:
				npc(c, "Aubury", Anim.TALKING_ALOT, "A package? From who?");
				c.nextChat = 4053;
				break;
			case 4053:
				player(c, Anim.CALM_1, "From Sedridor at the Wizards' Tower.");
				c.nextChat = 4054;
				break;
			case 4054:
				npc(c, "Aubury", Anim.TALKING_ALOT, "From Sedridor? But... surely, he can't have? Please,", "let me have it. It must be extremely important for", "him to have sent a stranger.");
				c.nextChat = 4055;
				break;
			case 4055:
				c.getItems().deleteItem(1436, 1);
				item(c, 1436, "You hand the package to Aubury.");
				c.nextChat = 4056;
				break;
			case 4056:
				npc(c, "Aubury", Anim.HAPPY, "This... this is incredible. My gratitude to you adventurer", "for bringing me these research notes. Thanks to you,", "I think we finally have it.");
				c.nextChat = 4058;
				break;
			case 4058:
				npc(c, "Aubury", Anim.HAPPY, "Here, take these notes back to Sedridor. They should", "hopefully give him everything he needs.");
				c.nextChat = 4059;
				break;
			case 4059:
				c.getItems().addItem(1437, 1);
				new RuneMysteries(c).setStage(3); // Set Quest Stage to 3
				item(c, 1437, "Aubury hands you some research notes.");
				c.nextChat = -1;
				break;

			// Lost Items (Stage 2 & 3)
			case 4057:
				npc(c, "Aubury", Anim.ANGRY, "What kind of person says they have a delivery for me,", "but not with them? Honestly. Come back when", "you have it.");
				c.nextChat = -1;
				break;
			case 4060:
				player(c, Anim.SAD, "Sorry, but I lost them.");
				c.nextChat = 4061;
				break;
			case 4061:
				npc(c, "Aubury", Anim.CALM_1, "Well, luckily I have duplicates. It's a good thing they", "are written in code. I wouldn't want the wrong kind of", "person to get access to the information contained within.");
				c.nextChat = 4062;
				break;
			case 4062:
				c.getItems().addItem(1437, 1);
				item(c, 1437, "Aubury hands you some research notes.");
				c.nextChat = -1;
				break;

			// --- Standard Shop Dialogues ---
			case 4010:
				player(c, Anim.CALM_1, "Can you tell me about your cape?");
				c.nextChat = 4011;
				break;
			case 4011:
				npc(c, "Aubury", Anim.HAPPY, "Certainly! Skillcapes are a symbol of achievement. Only", "people who have mastered a skill and reached level 99", "can get their hands on them and gain the benefits", "they carry.");
				c.nextChat = 4012;
				break;
			case 4012:
				npc(c, "Aubury", Anim.CALM_1, "The Cape of Runecrafting has been upgraded with", "each talisman, allowing you to access all", "Runecrafting altars. Is there anything else I", "can help you with?");
				c.nextChat = 4001; 
				break;
			case 4020:
				player(c, Anim.CALM_1, "Oh, it's a rune shop. No thank you, then.");
				c.nextChat = 4021;
				break;
			case 4021:
				npc(c, "Aubury", Anim.CALM_1, "Well, if you find someone who does want runes,", "please send them my way.");
				c.nextChat = -1;
				break;
			case 4030:
				player(c, Anim.CALM_1, "Can you teleport me to the Rune Essence?");
				c.nextChat = 4031;
				break;
			case 4031:
				npc(c, "Aubury", Anim.HAPPY, "Of course. By the way, if you end up making any", "runes from the essence you mine, I'll happily", "buy them from you.");
				c.nextChat = 4032;
				break;
			case 4032:
				npc(c, "Aubury", Anim.AGGRO_HEAD_BANG, "Senventior Disthine Molenko!");
				c.nextChat = 4033;
				break;
			case 4033:
				c.getPA().movePlayer(2911, 4832, 0); 
				c.nextChat = -1;
				break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		
		if (c.dialogueAction == 4001) {
			switch (buttonId) {
				case OPT4_FIRST: next(c, 4010); break;
				case OPT4_SECOND: c.getShops().openShop(4); break; 
				case OPT4_THIRD: next(c, 4020); break;
				case OPT4_FOURTH: next(c, 4030); break;
			}
		} else if (c.dialogueAction == 4002) {
			switch (buttonId) {
				case OPT3_FIRST: next(c, 4010); break;
				case OPT3_SECOND: c.getShops().openShop(4); break; 
				case OPT3_THIRD: next(c, 4020); break;
			}
		} else if (c.dialogueAction == 4050) {
			switch (buttonId) {
				case OPT2_FIRST: c.getShops().openShop(4); break; // Yes please!
				case OPT2_SECOND: next(c, 4051); break; // I have a package...
			}
		}
	}
}