package server.model.players.packets.dialogue.npc.areas.asgarnia.quests.RuneMysteries;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.quests.RuneMysteries;

public class ArchmageSedridor extends NPCDialogue {

	public ArchmageSedridor(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 5034; 
	}
	@Override
	public String getDialogueRange() {
		return "4100-4248";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 5034:
			switch (startDialogueId) {

			// --- Entry Point ---
			case 4100:
				int rmStage = c.questStages[RuneMysteries.QUEST_ID];

				// 1. Check for Beginner Clue Scroll (Anagram: Archmage Sedridor)
				if (c.getItems().playerHasItem(23189)) {
					npc(c, "Archmage Sedridor", Anim.HAPPY, "Well done.");
					c.nextChat = 4150;
				} 
				// 2. Stage 1: Player has the Air Talisman
				else if (rmStage == 1) {
					if (c.getItems().playerHasItem(1438)) {
						player(c, Anim.CALM_1, "Are you Sedridor?");
						c.nextChat = 4200;
					} else {
						npc(c, "Archmage Sedridor", Anim.CALM_1, "Welcome back, adventurer. Do you have that", "talisman now?");
						c.nextChat = 4220; // Lost Talisman
					}
				}
				// 3. Stage 2: Player has the Research Package
				else if (rmStage == 2) {
					if (c.getItems().playerHasItem(1436)) {
						npc(c, "Archmage Sedridor", Anim.CALM_1, "Hello again, adventurer. Please, take this package of", "research notes to Aubury in Varrock. He runs a rune", "shop in the south east of the city.");
						c.nextChat = -1;
					} else {
						npc(c, "Archmage Sedridor", Anim.CALM_1, "Hello again, adventurer. Did you take that", "package to Aubury?");
						c.nextChat = 4230; // Lost Package
					}
				}
				// 4. Stage 3: Player returns from Aubury
				else if (rmStage == 3) {
					npc(c, "Archmage Sedridor", Anim.HAPPY, "Ah, " + c.playerName + ". How goes your quest? Have you", "delivered my research to Aubury yet?");
					c.nextChat = 4240;
				}
				// 5. Post-Quest Options
				else if (rmStage >= 4) {
					npc(c, "Archmage Sedridor", Anim.CALM_1, "Hello again, " + c.playerName + ". What can I do for you?");
					c.nextChat = 4110;
				} 
				// 6. Pre-Quest Dialogue
				else {
					npc(c, "Archmage Sedridor", Anim.CALM_1, "Welcome adventurer, to the world renowned Wizards'", "Tower, home to the Order of Wizards. How may I", "help you?");
					c.nextChat = 4101;
				}
				break;

			// ==============================================
			// QUEST STAGE 1: Turning in the Talisman
			// ==============================================
			case 4200:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Sedridor? What is it you want with him?");
				c.nextChat = 4201;
				break;
			case 4201:
				player(c, Anim.CALM_1, "The Duke of Lumbridge sent me to find him. I have this", "talisman he found. He said Sedridor would be", "interested in it.");
				c.nextChat = 4202;
				break;
			case 4202:
				npc(c, "Archmage Sedridor", Anim.HAPPY, "Did he now? Well hand it over then, and we'll see", "what all the hubbub is about.");
				c.nextChat = 4203;
				break;
			case 4203:
				player(c, Anim.CALM_1, "Okay, here you are.");
				c.nextChat = 4204;
				break;
			case 4204:
				// Take the talisman, trigger the cutscene-like text
				c.getItems().deleteItem(1438, 1);
				item(c, 1438, "You hand the talisman to Sedridor.");
				c.nextChat = 4205;
				break;
			case 4205:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Hmm... Doesn't seem to be anything too special. Just", "a normal air talisman by the looks of things. Still,", "looks can be deceiving. Let me take a closer look...");
				c.nextChat = 4206;
				break;
			case 4206:
				npc(c, "Archmage Sedridor", Anim.CALM_2, "How interesting... It would appear I spoke too soon.", "There's more to this talisman than meets the eye. In", "fact, it may well be the last piece of the puzzle.", "This talisman may be key to finding the forgotten essence mine!");
				c.nextChat = 4207;
				break;
			case 4207:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "It is critical I share this discovery with my associate,", "Aubury, as soon as possible. He's not much of a", "wizard, but he's an expert on runecrafting, and his", "insight will be essential.");
				c.nextChat = 4208;
				break;
			case 4208:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Would you be willing to visit him for me? I would go", "myself, but I wish to study this talisman some more.");
				c.nextChat = 4209;
				break;
			case 4209:
				options(c, 4209, "Yes, certainly.", "No, I'm busy.");
				break;
			case 4210:
				player(c, Anim.HAPPY, "Yes, certainly.");
				c.nextChat = 4211;
				break;
			case 4211:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "He runs a rune shop in the south east of Varrock.", "Please, take this package of research notes to him. If", "all goes well, the secrets of the essence mine may", "soon be ours once more!");
				c.nextChat = 4212;
				break;
			case 4212:
				c.getItems().addItem(1436, 1);
				new RuneMysteries(c).setStage(2); // Set Quest Stage to 2
				item(c, 1436, "Sedridor hands you a package.");
				c.nextChat = 4213;
				break;
			case 4213:
				npc(c, "Archmage Sedridor", Anim.HAPPY, "Best of luck, " + c.playerName + ".");
				c.nextChat = -1;
				break;

			// Lost Items (Stage 1 & 2)
			case 4220:
				player(c, Anim.SAD, "Not yet.");
				c.nextChat = 4221;
				break;
			case 4221:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Well come back when you have it.");
				c.nextChat = -1;
				break;
			case 4230:
				player(c, Anim.SAD, "I lost it. Could I have another?");
				c.nextChat = 4231;
				break;
			case 4231:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Well it's a good job I have copies of everything.");
				c.nextChat = 4232;
				break;
			case 4232:
				c.getItems().addItem(1436, 1);
				item(c, 1436, "Sedridor hands you a package.");
				c.nextChat = -1;
				break;

			// ==============================================
			// QUEST STAGE 3: Turning in Aubury's Notes
			// ==============================================
			case 4240:
				if (c.getItems().playerHasItem(1437)) {
					player(c, Anim.HAPPY, "Yes, I have. He gave me some notes to give to you.");
					c.nextChat = 4241;
				} else {
					player(c, Anim.SAD, "Err, you're not going to believe this...", "I don't have them.");
					c.nextChat = 4247;
				}
				break;
			case 4241:
				npc(c, "Archmage Sedridor", Anim.HAPPY, "Wonderful! Let's have a look then.");
				c.nextChat = 4242;
				break;
			case 4242:
				c.getItems().deleteItem(1437, 1);
				item(c, 1437, "You hand the notes to Sedridor.");
				c.nextChat = 4243;
				break;
			case 4243:
				npc(c, "Archmage Sedridor", Anim.HAPPY, "Alright, let's see what Aubury has for us...", "Yes, this is it! The lost incantation!");
				c.nextChat = 4244;
				break;
			case 4244:
				player(c, Anim.HAPPY, "So you'll be able to access that essence mine now?");
				c.nextChat = 4245;
				break;
			case 4245:
				npc(c, "Archmage Sedridor", Anim.HAPPY, "That's right! Because of you, our order finally has a", "proper source of rune essence again! Thank you, friend.", "If you ever want to access the essence mine yourself,", "just let me know. It's the least I can do.");
				c.nextChat = 4246;
				break;
			case 4246:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Oh, and you can have this air talisman back as well.", "I have no further need of it, and I'm sure you will", "find it useful.");
				c.nextChat = 4248;
				break;
			case 4248:
				// COMPLETE THE QUEST!
				RuneMysteries quest = new RuneMysteries(c);
				quest.setStage(4);
				quest.giveRewards(); 
				c.nextChat = -1;
				break;

			// Stage 3: Lost Notes
			case 4247:
				npc(c, "Archmage Sedridor", Anim.ANGRY, "Right... You're rather careless aren't you. I suggest", "you go and speak to Aubury once more. With luck", "he will have made copies.");
				c.nextChat = -1;
				break;

			// --- Pre/Post Quest Default Dialogues ---
			case 4101:
				player(c, Anim.CALM_1, "I'm just looking around.");
				c.nextChat = 4102;
				break;
			case 4102:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Well, take care adventurer. You stand on the ruins of", "the old destroyed Wizards' Tower. Strange and", "powerful magicks lurk here.");
				c.nextChat = -1;
				break;
			case 4110:
				options(c, 4110, 
					"Can you teleport me to the Rune Essence Mine?", 
					"Who else knows the teleport to the Rune Essence Mine?", 
					"Could you tell me about the old Wizards' Tower?", 
					"Nothing thanks, I'm just looking around.");
				break;
			case 4120:
				player(c, Anim.CALM_1, "Can you teleport me to the Rune Essence Mine?");
				c.nextChat = 4121;
				break;
			case 4121:
				npc(c, "Archmage Sedridor", Anim.AGGRO_HEAD_BANG, "Seventior disthine molenko!");
				c.nextChat = 4122;
				break;
			case 4122:
				c.getPA().movePlayer(2911, 4832, 0); 
				c.nextChat = -1;
				break;
			case 4130:
				player(c, Anim.CALM_1, "Who else knows the teleport to the Rune Essence Mine?");
				c.nextChat = 4131;
				break;
			case 4131:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Apart from myself, there's also Aubury in Varrock,", "Wizard Cromperty in East Ardougne, Brimstail in the", "Tree Gnome Stronghold and Wizard Distentor in", "Yanille's Wizards' Guild.");
				c.nextChat = 4110; 
				break;
			case 4140:
				player(c, Anim.CALM_1, "Could you tell me about the old Wizards' Tower?");
				c.nextChat = 4141;
				break;
			case 4141:
				npc(c, "Archmage Sedridor", Anim.CALM_1, "Of course. The first Wizards' Tower was built at the", "same time the Order of Wizards was founded.");
				c.nextChat = 4143;
				break;
			case 4143:
				npc(c, "Archmage Sedridor", Anim.SAD, "Alas, that openness is what ultimately led to disaster.", "The wizards who served Zamorak, the evil god of chaos,", "tried to claim our magical discoveries in his name.", "They failed, but in retaliation, they burnt the entire tower down.");
				c.nextChat = 4110;
				break;
			case 4160:
				player(c, Anim.CALM_1, "Nothing thanks, I'm just looking around.");
				c.nextChat = 4102; 
				break;

			// --- Treasure Trails Action ---
			case 4150:
				TreasureTrails.progressClue(c, 23189); 
				c.nextChat = -1;
				break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 4110) {
			switch (buttonId) {
				case OPT4_FIRST: next(c, 4120); break;
				case OPT4_SECOND: next(c, 4130); break;
				case OPT4_THIRD: next(c, 4140); break;
				case OPT4_FOURTH: next(c, 4160); break;
			}
		}
		else if (c.dialogueAction == 4209) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 4210); break; // Yes, certainly.
				case OPT2_SECOND: c.getPA().closeAllWindows(); break; // No, I'm busy.
			}
		}
	}
}