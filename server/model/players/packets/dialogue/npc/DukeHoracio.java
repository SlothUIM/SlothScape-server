package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
// TODO: Import your quest classes here!
// import server.model.players.quests.RuneMysteries;
// import server.model.players.quests.DragonSlayer;
import server.model.players.quests.DragonSlayer;
import server.model.players.quests.RuneMysteries;

public class DukeHoracio extends NPCDialogue {

	public DukeHoracio(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 815; // Duke Horacio's standard NPC ID
	}
	   @Override
		public String getDialogueRange() {
			return "3200-3251, 32161-32381";
		}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		String manWoman = c.playerAppearance[0] == 0 ? "man" : "woman";
		String hisHer = c.playerAppearance[0] == 0 ? "his" : "her";

		switch (npcId) {
		case 815:
			switch (startDialogueId) {

			// --- Entry Point ---
			case 3200:
				npc(c, "Duke Horacio", Anim.CALM_1, "Greetings. Welcome to my castle.");
				c.nextChat = 3201;
				break;
			case 3201:
				// Only show the shield option if they don't have it equipped or in their inventory
				boolean needsShield = !c.getItems().playerHasItem(1540) && !c.getItems().isWearingItem(1540);
				
				if (needsShield) {
					options(c, 3201, 
						"I seek a shield that will protect me from dragonbreath.", 
						"Have you any quests for me?", 
						"Where can I find money?");
				} else {
					options(c, 3202, 
						"Have you any quests for me?", 
						"Where can I find money?");
				}
				break;

			// ==============================================
			// BRANCH 1: ANTI-DRAGON SHIELD
			// ==============================================
			case 3210:
				player(c, Anim.CALM_1, "I seek a shield that will protect me from dragonbreath.");
				c.nextChat = 3211;
				break;
			case 3211:
				npc(c, "Duke Horacio", Anim.CALM_1, "A knight going on a dragon quest, hmm?", "What dragon do you intend to slay?");
				c.nextChat = 3212;
				break;
			case 3212:
				// TODO: Replace '1' with your Dragon Slayer started stage
				if (c.questStages[DragonSlayer.QUEST_ID] >= 1) { 
					options(c, 3212, "Elvarg, the dragon of Crandor island!", "Oh, no dragon in particular.");
				} else {
					// Skips directly to "No dragon in particular" if quest hasn't started
					next(c, 3220); 
				}
				break;

			// Elvarg Path
			case 3213:
				player(c, Anim.CALM_1, "Elvarg, the dragon of Crandor island!");
				c.nextChat = 3214;
				break;
			case 3214:
				npc(c, "Duke Horacio", Anim.LAUGH_4, "Elvarg? Are you sure?", "Well, are you sure?");
				c.nextChat = 3215;
				break;
			case 3215:
				options(c, 3215, "Yes.", "I'd better leave that dragon alone.", "So, are you going to give me the shield or not?", "No.");
				break;
			
			// Elvarg: "Yes" Sub-branch
			case 3216:
				player(c, Anim.CALM_1, "Yes.");
				c.nextChat = 32161;
				break;
			case 32161:
				npc(c, "Duke Horacio", Anim.CALM_1, "Well, you're a braver " + manWoman + " than I!");
				c.nextChat = 32162;
				break;
			case 32162:
				player(c, Anim.CALM_1, "Why is everyone so scared of this dragon?");
				c.nextChat = 32163;
				break;
			case 32163:
				npc(c, "Duke Horacio", Anim.CALM_1, "Back in my father's day, Crandor was an important", "city-state. Politically it was as important as Falador and", "Varrock and its ships traded with every port.");
				c.nextChat = 32164;
				break;
			case 32164:
				npc(c, "Duke Horacio", Anim.CALM_1, "But, one day, when I was little, all contact was lost.", "The trading ships and the diplomatic envoys just", "stopped coming.");
				c.nextChat = 32165;
				break;
			case 32165:
				npc(c, "Duke Horacio", Anim.SAD, "I remember my father being very scared. He posted", "lookouts on the roof to warn if the dragon was approaching.", "All the city rulers worried that Elvarg would", "devastate the whole continent.");
				c.nextChat = 3215; // Loops back to the options so they can finally ask for the shield!
				break;

			// Elvarg: Rejections
			case 3217:
				player(c, Anim.CALM_1, "I'd better leave that dragon alone.");
				c.nextChat = 32171;
				break;
			case 32171:
				npc(c, "Duke Horacio", Anim.HAPPY, "That's a relief. I would hate to see such a promising", "adventurer cut down in " + hisHer + " prime.");
				c.nextChat = -1;
				break;
			case 3218:
				player(c, Anim.CALM_1, "No.");
				c.nextChat = 32181;
				break;
			case 32181:
				npc(c, "Duke Horacio", Anim.CALM_1, "Very wise. There are some monsters that are", "best left alone.");
				c.nextChat = -1;
				break;

			// Elvarg: "Give me the shield" Success
			case 3219:
				player(c, Anim.ANGRY, "So, are you going to give me the shield or not?");
				c.nextChat = 32191;
				break;
			case 32191:
				npc(c, "Duke Horacio", Anim.CALM_1, "If you really think you're up to it then perhaps you", "are the one who can kill this dragon.");
				c.nextChat = 32192;
				break;
			case 32192:
				c.getItems().addItem(1540, 1);
				item(c, 1540, "The Duke hands you a heavy orange shield.");
				c.nextChat = 32193;
				break;
			case 32193:
				npc(c, "Duke Horacio", Anim.CALM_1, "Take care out there. If you kill it...", "If you kill it, for Saradomin's sake make sure", "it's really dead!");
				c.nextChat = -1;
				break;

			// No Dragon Path
			case 3220:
				player(c, Anim.CALM_1, "Oh, no dragon in particular. I just feel like", "killing a dragon.");
				// TODO: Replace '5' with your completed quest stage value
				if (c.questStages[DragonSlayer.QUEST_ID] >= 5) {
					c.nextChat = 3223;
				} else {
					c.nextChat = 3221;
				}
				break;
			case 3221:
				npc(c, "Duke Horacio", Anim.CALM_1, "I don't have an infinite supply of these shields, you", "know. I'll only give one for a truly worthy cause.");
				c.nextChat = -1;
				break;
			case 3223:
				npc(c, "Duke Horacio", Anim.HAPPY, "Of course. Now you've slain Elvarg, you've earned the", "right to call the shield your own!");
				c.nextChat = 3224;
				break;
			case 3224:
				c.getItems().addItem(1540, 1);
				item(c, 1540, "The Duke hands you the shield.");
				c.nextChat = -1;
				break;

			// ==============================================
			// BRANCH 2: RUNE MYSTERIES
			// ==============================================
			case 3230:
				player(c, Anim.CALM_1, "Have you any quests for me?");
				c.nextChat = 3231;
				break;
			case 3231:
				int rmStage = c.questStages[RuneMysteries.QUEST_ID];

				if (rmStage == 0) {
					next(c, 3232); // Quest not started
				} else if (rmStage == 1 && c.getItems().playerHasItem(1438)) {
					next(c, 3238); // Quest started, HAS talisman
				} else if (rmStage == 1 && !c.getItems().playerHasItem(1438) && !c.getItems().bankContains(1438)) {
					next(c, 3239); // Quest started, LOST talisman
				} else {
					next(c, 3243); // Quest progressed past this step or completed
				}
				break;

			// RM: Quest Not Started
			case 3232:
				npc(c, "Duke Horacio", Anim.CALM_1, "Well, it's not really a quest but I recently discovered", "this strange talisman. It seems to be mystical and I", "have never seen anything like it before. Would you", "take it to the head wizard at");
				c.nextChat = 3233;
				break;
			case 3233:
				npc(c, "Duke Horacio", Anim.CALM_1, "the Wizards' Tower for me? It's just south-west of", "here and should not take you very long at all.", "I would be awfully grateful.");
				c.nextChat = 3234;
				break;
			case 3234:
				options(c, 3234, "Sure, no problem.", "Not right now.");
				break;
			case 3235:
				player(c, Anim.HAPPY, "Sure, no problem.");
				c.nextChat = 32351;
				break;
			case 32351:
				npc(c, "Duke Horacio", Anim.HAPPY, "Thank you very much, stranger. I am sure the head", "wizard will reward you for such an interesting find.");
				c.nextChat = 32352;
				break;
			case 32352:
				c.getItems().addItem(1438, 1);
				// TODO: Set quest stage to started!
				new RuneMysteries(c).setStage(1);
				item(c, 1438, "The Duke shows you a talisman.");
				c.nextChat = -1;
				break;
			case 3236:
				player(c, Anim.CALM_1, "Not right now.");
				c.nextChat = 32361;
				break;
			case 32361:
				npc(c, "Duke Horacio", Anim.CALM_1, "As you wish, stranger, although I have this strange", "feeling that it is important. Unfortunately, I cannot", "leave my castle unattended.");
				c.nextChat = -1;
				break;

			// RM: Started, Has Talisman
			case 3238:
				npc(c, "Duke Horacio", Anim.CALM_1, "The only task remotely approaching a quest is the", "delivery of that talisman I gave you to the head", "wizard of the Wizards' Tower, south-west of here.");
				c.nextChat = 32381;
				break;
			case 32381:
				npc(c, "Duke Horacio", Anim.CALM_1, "I suggest you deliver it to him as soon as possible.", "I have the oddest feeling that it is important...");
				c.nextChat = -1;
				break;

			// RM: Started, Lost Talisman
			case 3239:
				npc(c, "Duke Horacio", Anim.CALM_1, "Did you speak to the head wizard for me yet,", "adventurer?");
				c.nextChat = 3240;
				break;
			case 3240:
				player(c, Anim.SAD, "No, I lost that air talisman that you gave me.");
				c.nextChat = 3241;
				break;
			case 3241:
				npc(c, "Duke Horacio", Anim.CALM_1, "Ah, that would explain it. One of my servants found", "this outside, and it seemed too much of a coincidence", "that more than one strange object would appear on", "my land in such a short period of time.");
				c.nextChat = 3242;
				break;
			case 3242:
				npc(c, "Duke Horacio", Anim.CALM_1, "Please take this to the head wizard at the Wizards'", "Tower, south-west of here, and don't lose it this time.");
				c.nextChat = 32421;
				break;
			case 32421:
				c.getItems().addItem(1438, 1);
				item(c, 1438, "The Duke hands you the talisman.");
				c.nextChat = -1;
				break;

			// RM: Quest past Sedridor Stage
			case 3243:
				npc(c, "Duke Horacio", Anim.CALM_1, "The only job I had was the delivery of that talisman,", "so I'm afraid not. No, all is well for me.");
				c.nextChat = -1;
				break;

			// ==============================================
			// BRANCH 3: MONEY
			// ==============================================
			case 3250:
				player(c, Anim.CALM_1, "Where can I find money?");
				c.nextChat = 3251;
				break;
			case 3251:
				npc(c, "Duke Horacio", Anim.CALM_1, "I've heard that the blacksmiths are prosperous", "amongst the peasantry. Maybe you could try your", "hand at that?");
				c.nextChat = -1;
				break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		
		// 3201: Main Menu WITH Shield Option
		if (c.dialogueAction == 3201) {
			switch (buttonId) {
				case OPT3_FIRST: next(c, 3210); break;
				case OPT3_SECOND: next(c, 3230); break;
				case OPT3_THIRD: next(c, 3250); break;
			}
		}

		// 3202: Main Menu WITHOUT Shield Option
		else if (c.dialogueAction == 3202) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 3230); break;
				case OPT2_SECOND: next(c, 3250); break;
			}
		}

		// 3212: Dragon Slayer options
		else if (c.dialogueAction == 3212) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 3213); break;
				case OPT2_SECOND: next(c, 3220); break;
			}
		}

		// 3215: "Are you sure?" Elvarg branches
		else if (c.dialogueAction == 3215) {
			switch (buttonId) {
				case OPT4_FIRST: next(c, 3216); break;  // "Yes" -> Lore
				case OPT4_SECOND: next(c, 3217); break; // "Leave it alone"
				case OPT4_THIRD: next(c, 3219); break;  // "Give me the shield"
				case OPT4_FOURTH: next(c, 3218); break; // "No"
			}
		}

		// 3234: Will you deliver the talisman?
		else if (c.dialogueAction == 3234) {
			switch (buttonId) {
				case OPT2_FIRST: next(c, 3235); break; // Sure
				case OPT2_SECOND: next(c, 3236); break; // Not right now
			}
		}
	}
}