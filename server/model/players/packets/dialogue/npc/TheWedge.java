package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class TheWedge extends NPCDialogue {

	public TheWedge(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 5517; // The 'Wedge' NPC ID
	}
    @Override
 	public String getDialogueRange() {
 		return "3500-3550";
 	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (startDialogueId) {

		// ==============================================================
		// ENTRY POINT (Check for rewards first)
		// ==============================================================
		case 3500:
			if (c.getAD().isTierFinished("Kandarin", "Elite") && !c.getItems().playerHasItem(13139)) {
				player(c, Anim.CALM_1, "I've completed all of the elite tasks in my", "Kandarin achievement diary!");
				c.nextChat = 3540; // Elite Claim
			} else if (c.getAD().isTierFinished("Kandarin", "Hard") && !c.getItems().playerHasItem(13138)) {
				player(c, Anim.CALM_1, "I've completed all of the hard tasks in my", "Kandarin achievement diary!");
				c.nextChat = 3535; // Hard Claim
			} else if (c.getAD().isTierFinished("Kandarin", "Medium") && !c.getItems().playerHasItem(13137)) {
				player(c, Anim.CALM_1, "I've completed all of the medium tasks in my", "Kandarin achievement diary!");
				c.nextChat = 3530; // Medium Claim
			} else if (c.getAD().isTierFinished("Kandarin", "Easy") && !c.getItems().playerHasItem(13136)) {
				player(c, Anim.CALM_1, "I've completed all of the easy tasks in my", "Kandarin Achievement Diary!");
				c.nextChat = 3525; // Easy Claim
			} else {
				npc(c, "The 'Wedge'", Anim.CALM_1, "Hi there!");
				c.nextChat = 3501;
			}
			break;

		case 3501:
			options(c, 3501, 
				"Can I have some more headgear?", 
				"Who are you?", 
				"What is the Achievement Diary?", 
				"I have a question about my Achievement Diary", 
				"Bye!");
			break;

		// --- OPTION: WHO ARE YOU? / WHAT IS DIARY? ---
		case 3502:
			npc(c, "The 'Wedge'", Anim.CALM_1, "Call me The 'Wedge', I'm the taskmaster for the", "Achievement Diary here in Kandarin.");
			c.nextChat = 3503;
			break;
		case 3503:
			player(c, Anim.CALM_1, "What is the Achievement Diary?");
			c.nextChat = 3504;
			break;
		case 3504:
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"It's a diary that helps you keep track of particular", 
				"achievements. In and around Kandarin it can help you", 
				"discover some quite useful things.");
			c.nextChat = 3505;
			break;
		case 3505:
			npc(c, "The 'Wedge'", Anim.CALM_1, "Eventually, with enough exploration, the inhabitants", "will reward you. You can see the list of tasks", "on the side-panel.");
			c.nextChat = 3501;
			break;

		// --- OPTION: I HAVE A QUESTION ---
		case 3506:
			options(c, 3506, "What is the Achievement Diary?", "What are the rewards?", "How do I claim the rewards?");
			break;

		case 3507:
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"Well, there are four different Kandarin headgears, which", 
				"match up with the four levels of difficulty. Which tier", 
				"of rewards would you like to know more about?");
			c.nextChat = 3508;
			break;
		case 3508:
			options(c, 3508, "Easy Rewards", "Medium Rewards", "Hard Rewards", "Elite Rewards");
			break;

		// --- REWARD INFO BRANCHES ---
		case 3510: // Easy Info
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"Easy: Headgear is a light source, chance of extra logs,", 
				"convert 30 flax daily, coal trucks hold 140, and 5%", 
				"more marks of grace on Seers' rooftop.");
			c.nextChat = 3501;
			break;
		case 3511: // Medium Info
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"Medium: 10% more maple XP, 33% faster spinning,", 
				"convert 60 flax, 5% more Catherby crops, 280 coal", 
				"truck capacity, and a permanent rope at Baxtorian falls.");
			c.nextChat = 3501;
			break;
		case 3512: // Hard Info
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"Hard: Cheaper battlestaves, Seers' bank teleport toggle,", 
				"15% rooftop marks, 120 flax, 10% more BA points,", 
				"Sherlock teleport, and 10% better enchanted bolt specs.");
			c.nextChat = 3501;
			break;
		case 3513: // Elite Info
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"Elite: Thormac is cheaper, Otto hastae discount,", 
				"unlimited Sherlock teleports, 15% herb yield, 250 flax,", 
				"and auto-banking coal from trucks.");
			c.nextChat = 3501;
			break;

		// ==============================================================
		// REWARD CLAIMING BRANCHES
		// ==============================================================
		case 3525: // Easy Claim
			npc(c, "The 'Wedge'", Anim.CALM_1, "I can see that, well done! You'll be wanting your", "reward then!");
			c.nextChat = 3526;
			break;
		case 3526:
			player(c, Anim.CALM_1, "Yes please!");
			c.nextChat = 3527;
			break;
		case 3527:
			c.getAD().claimReward("Kandarin", "Easy");
			npc(c, "The 'Wedge'", Anim.CALM_1, 
				"This headgear is a symbol of your exploration of Kandarin.", 
				"It functions as a light source, provides extra logs, flax", 
				"conversion, and Seers' agility benefits.");
			c.nextChat = 3528;
			break;
		case 3528:
			player(c, Anim.CALM_1, "Wow, thanks!");
			c.nextChat = 3529;
			break;
		case 3529:
			npc(c, "The 'Wedge'", Anim.CALM_1, "If you ever lose your headgear, come back to me", "to reclaim it.");
			c.nextChat = 3501;
			break;

		case 3530: // Medium Claim
			npc(c, "The 'Wedge'", Anim.CALM_1, "I've upgraded your Kandarin headgear for you. You", "now have maple, spinning, and Waterfall benefits!");
			c.getAD().claimReward("Kandarin", "Medium");
			c.nextChat = 0;
			break;

		case 3535: // Hard Claim
			npc(c, "The 'Wedge'", Anim.CALM_1, "I've upgraded your headgear again. You can now use the", "Seers' bank teleport and Sherlock shortcuts!");
			c.getAD().claimReward("Kandarin", "Hard");
			c.nextChat = 0;
			break;

		case 3540: // Elite Claim
			npc(c, "The 'Wedge'", Anim.CALM_1, "Well done! You've completed all of the tasks in Kandarin!", "Here's your reward!");
			c.getAD().claimReward("Kandarin", "Elite");
			c.nextChat = 0;
			break;

		case 3550: // Bye
			npc(c, "The 'Wedge'", Anim.CALM_1, "See you around.");
			c.nextChat = 0;
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		if (c.dialogueAction == 3501) {
			switch (buttonId) {
			case OPT5_FIRST: c.getAD().claimReward("Kandarin", "Easy"); c.getPA().removeAllWindows(); break;
			case OPT5_SECOND: next(c, 3502); break;
			case OPT5_THIRD: next(c, 3503); break;
			case OPT5_FOURTH: next(c, 3506); break;
			case OPT5_FIFTH: next(c, 3550); break;
			}
		} else if (c.dialogueAction == 3506) {
			switch (buttonId) {
			case OPT3_FIRST: next(c, 3504); break;
			case OPT3_SECOND: next(c, 3507); break;
			case OPT3_THIRD: npc(c, "The 'Wedge'", Anim.CALM_1, "Just complete the tasks in Kandarin so they're", "ticked off, then come and speak to me."); break;
			}
		} else if (c.dialogueAction == 3508) {
			switch (buttonId) {
			case OPT4_FIRST: next(c, 3510); break;
			case OPT4_SECOND: next(c, 3511); break;
			case OPT4_THIRD: next(c, 3512); break;
			case OPT4_FOURTH: next(c, 3513); break;
			}
		}
	}
}