package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class TwoPints extends NPCDialogue {

	public TwoPints(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 5519; 
	}
    @Override
 	public String getDialogueRange() {
 		return "5000-5001, 5020-5026, 5030, 5031, 5040-5043, 5050, 5060, 5070, 5080, 5100, 5101, 5200, 5300, 5400";
 	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (startDialogueId) {

		// ==============================================================
		// ENTRY POINT (Check for rewards first)
		// ==============================================================
		case 5000:
			if (c.getAD().isTierFinished("Ardougne", "Elite") && !c.getItems().playerHasItem(13124)) {
				if (c.playerLevel[13] < 91) { // Smithing check
					npc(c, "Two-pints", Anim.DISORIENTED, "What, with your stats? I don't believe you.", "You'd need a Smithing level of 91. Ask me", "again when you've got your Smithing level up.");
					c.nextChat = 5001;
				} else {
					player(c, Anim.CALM_1, "I've completed all of the elite tasks in my", "Ardougne achievement diary!");
					c.nextChat = 5400;
				}
			} else if (c.getAD().isTierFinished("Ardougne", "Hard") && !c.getItems().playerHasItem(13123)) {
				player(c, Anim.CALM_1, "I've completed all of the hard tasks in my", "Ardougne achievement diary!");
				c.nextChat = 5300;
			} else if (c.getAD().isTierFinished("Ardougne", "Medium") && !c.getItems().playerHasItem(13122)) {
				player(c, Anim.CALM_1, "I've completed all of the medium tasks in my", "Ardougne achievement diary!");
				c.nextChat = 5200;
			} else if (c.getAD().isTierFinished("Ardougne", "Easy") && !c.getItems().playerHasItem(13121)) {
				player(c, Anim.CALM_1, "I've completed all of the easy tasks in my", "Ardougne achievement diary!");
				c.nextChat = 5100;
			} else {
				npc(c, "Two-pints", Anim.DISORIENTED, "'elo... *hic*");
				c.nextChat = 5001;
			}
			break;

		case 5001:
			options(c, 5001, 
				"Can I have another cloak?", 
				"Who are you?", 
				"What is the Achievement Diary?", 
				"I have a question about my Achievement Diary", 
				"Bye!");
			break;

		// ==============================================================
		// SUB-BRANCH: WHO ARE YOU?
		// ==============================================================
		case 5020:
			npc(c, "Two-pints", Anim.DISORIENTED, "They call me Two pints... I'm the taskmaster for the", "Ardougne Achievement Diary.");
			c.nextChat = 5021;
			break;
		case 5021:
			options(c, 5021, "Why do they call you Two pints?");
			break;
		case 5022:
			npc(c, "Two-pints", Anim.DISORIENTED, "I'm not too sure really... my memory isn't the", "greatest... *hic*");
			c.nextChat = 5023;
			break;
		case 5023:
			player(c, Anim.CALM_1, "Maybe you lost a game of Runelink and had to change", "your name as a forfeit?");
			c.nextChat = 5024;
			break;
		case 5024:
			npc(c, "Two-pints", Anim.DISORIENTED, "Perhaps... *hic* I know a great drinking game for that!");
			c.nextChat = 5025;
			break;
		case 5025:
			player(c, Anim.CALM_1, "Suddenly it all makes sense...");
			c.nextChat = 5026;
			break;
		case 5026:
			npc(c, "Two-pints", Anim.DISORIENTED, "*Two pints mutters incoherently and stumbles away.*");
			c.nextChat = 0;
			break;

		// ==============================================================
		// SUB-BRANCH: WHAT IS THE DIARY?
		// ==============================================================
		case 5030:
			npc(c, "Two-pints", Anim.DISORIENTED, "It's a diary that helps you keep track of particular", "achievements. In and around Ardougne it can help you", "discover some quite useful things.");
			c.nextChat = 5031;
			break;
		case 5031:
			npc(c, "Two-pints", Anim.DISORIENTED, "Eventually, with enough exploration, the inhabitants", "will reward you. You can see the list of tasks", "on the side-panel.");
			c.nextChat = 5001;
			break;

		// ==============================================================
		// SUB-BRANCH: QUESTIONS & REWARDS INFO
		// ==============================================================
		case 5040:
			options(c, 5040, "What is the Achievement Diary?", "What are the rewards?", "How do I claim the rewards?");
			break;
		case 5041:
			npc(c, "Two-pints", Anim.DISORIENTED, "Well, there are four different Ardougne cloaks, which", "match up with the four levels of difficulty. Each has the", "same rewards as the previous level and some", "additional benefits too...");
			c.nextChat = 5042;
			break;
		case 5042:
			npc(c, "Two-pints", Anim.DISORIENTED, "Which tier of rewards would you like to know more about?");
			c.nextChat = 5043;
			break;
		case 5043:
			options(c, 5043, "Easy Rewards", "Medium Rewards", "Hard Rewards", "Elite Rewards");
			break;

		// INFO: Easy
		case 5050:
			npc(c, "Two-pints", Anim.DISORIENTED, "Easy tasks: The Citizen in West Ardougne will buy", "cats for 200 death runes each, some drops in the", "Tower of Life will be noted and the cape can", "teleport you to the Monastery.");
			c.nextChat = 5001;
			break;
		// INFO: Medium
		case 5060:
			npc(c, "Two-pints", Anim.DISORIENTED, "Medium: 100 noted essence daily, 10% better chance", "at pickpocketing in Ardougne, 3 teleports to the", "farming patch, and you can hold up to 56 coin pouches.");
			c.nextChat = 5001;
			break;
		// INFO: Hard
		case 5070:
			npc(c, "Two-pints", Anim.DISORIENTED, "Hard: 150 noted essence, Watchtower teleport toggle,", "5 farming patch teleports, 10% better pickpocketing", "everywhere, and 84 coin pouches.");
			c.nextChat = 5001;
			break;
		// INFO: Elite
		case 5080:
			npc(c, "Two-pints", Anim.DISORIENTED, "Elite: 250 essence, 50% more Trawler fish, 25% more", "Marks of Grace, unlimited farm teleports, auto sand", "delivery, and 140 coin pouches.");
			c.nextChat = 5001;
			break;

		// ==============================================================
		// REWARD CLAIMING BRANCHES
		// ==============================================================
		case 5100: // Easy Reward
			npc(c, "Two-pints", Anim.CALM_1, "I can see that, well done! You'll be wanting your", "reward then!");
			c.nextChat = 5101;
			break;
		case 5101:
			c.getAD().claimReward("Ardougne", "Easy");
			npc(c, "Two-pints", Anim.CALM_1, "This cloak is a symbol of your exploration. If you ever", "lose it, come back to me to reclaim it.");
			c.nextChat = 0;
			break;

		case 5200: // Medium Reward
			npc(c, "Two-pints", Anim.CALM_1, "I've upgraded your Ardougne cloak for you. You now", "have essence, pickpocketing, and farming benefits!");
			c.getAD().claimReward("Ardougne", "Medium");
			c.nextChat = 0;
			break;

		case 5300: // Hard Reward
			npc(c, "Two-pints", Anim.CALM_1, "I've upgraded your cloak again. Your pickpocketing", "luck now extends across all of Gielinor!");
			c.getAD().claimReward("Ardougne", "Hard");
			c.nextChat = 0;
			break;

		case 5400: // Elite Reward
			npc(c, "Two-pints", Anim.CALM_1, "Well done! You've completed all of the tasks in Ardougne!", "Here's your reward!");
			c.getAD().claimReward("Ardougne", "Elite");
			c.nextChat = 0;
			break;

		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 5001) {
			switch (buttonId) {
			case OPT5_FIRST: // Reclaim cloak
				c.getAD().claimReward("Ardougne", "Easy"); // Logic handles best cloak
				c.getPA().removeAllWindows();
				break;
			case OPT5_SECOND: next(c, 5020); break; // Who are you
			case OPT5_THIRD: next(c, 5030); break; // What is diary
			case OPT5_FOURTH: next(c, 5040); break; // Question menu
			case OPT5_FIFTH: next(c, 0); break; // Bye
			}
		} else if (c.dialogueAction == 5021) {
			next(c, 5022);
		} else if (c.dialogueAction == 5040) {
			switch (buttonId) {
			case OPT3_FIRST: next(c, 5030); break;
			case OPT3_SECOND: next(c, 5041); break;
			case OPT3_THIRD: npc(c, "Two-pints", Anim.DISORIENTED, "Just complete the tasks in Ardougne so they're", "ticked off, then come and speak to me."); break;
			}
		} else if (c.dialogueAction == 5043) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 5050); break;
			case OPT4_SECOND: next(c, 5060); break;
			case OPT4_THIRD: next(c, 5070); break;
			case OPT4_FOURTH: next(c, 5080); break;
			}
		}
	}
}