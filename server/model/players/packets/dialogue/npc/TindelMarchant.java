package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.util.Misc;

public class TindelMarchant extends NPCDialogue {

	public TindelMarchant(Player c) {
		super(c);
	}

	@Override
	public int getNPCID() {
		return 1358;
	}

	// 5-Option Button IDs (Since NPCDialogue only goes up to 4)
	public static final int OPT5_FIRST  = 9190;
	public static final int OPT5_SECOND = 9191;
	public static final int OPT5_THIRD  = 9192;
	public static final int OPT5_FOURTH = 9193;
	public static final int OPT5_FIFTH  = 9194;

	public static final int RUSTY_SWORD = 686;
	public static final int RUSTY_SCIMITAR = 6721;
	public static final int COINS = 995;
	public static final int FEE = 100;

	private static final int[] SWORD_REWARDS = {
		1205, 1291, 1203, 1293, 1207, 1295, 1209, 1297, 1201, 1299
	};

	private static final int[] SCIMITAR_REWARDS = {
		1321, 1323, 1325, 1327, 1329
	};
    @Override
 	public String getDialogueRange() {
 		return "8600-8613";
 	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		switch (npcId) {
		case 1358:
			switch (startDialogueId) {

			// ==============================================================
			// ENTRY POINT
			// ==============================================================
			case 8600:
				npc(c, "Tindel Marchant", Anim.CALM_1, "Hello there! Welcome to my special antiques boutique.");
				c.nextChat = 8601;
				break;

			// ==============================================================
			// MAIN MENU
			// ==============================================================
			case 8601:
				options(c, 8601, 
					"What do you do here?", 
					"What's involved?",
					"What do I get from this?", 
					"Ok, I'll give it a go!",
					"Ok, thanks.");
				break;

			// --------------------------------------------------------------
			// OPTION 1: "What do you do here?"
			// --------------------------------------------------------------
			case 8602:
				player(c, Anim.CALM_1, "What do you do here?");
				c.nextChat = 8603;
				break;
			case 8603:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"I'm a specialist at identifying rare and antique",
					"weapons, specifically swords, but I plan to branch out.",
					"If you have any old and rusty weapons that you want",
					"me to check out, just show them to me, pay me 100 Gold");
				c.nextChat = 8604;
				break;
			case 8604:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"and I'll see if you have an antique on your hands.",
					"I can also repair some antique weapons and armours,",
					"just show me the item and I'll let you know if I can",
					"repair it and for how much.");
				c.nextChat = 8601; // Loops back to options
				break;

			// --------------------------------------------------------------
			// OPTION 2: "What's involved?"
			// --------------------------------------------------------------
			case 8605:
				player(c, Anim.CALM_1, "What's involved?");
				c.nextChat = 8606;
				break;
			case 8606:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"Well, pay me 100 Gold and I'll see if any rusty swords",
					"that you've found are actually worth anything. Some of",
					"them might be worth some money! If it's an antique item",
					"though, I reserve the right to purchase it");
				c.nextChat = 8607;
				break;
			case 8607:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"immediately for adding to my own personal collection.",
					"I'll give you fair price for it.");
				c.nextChat = 8601; // Loops back to options
				break;

			// --------------------------------------------------------------
			// OPTION 3: "What do I get from this?"
			// --------------------------------------------------------------
			case 8608:
				player(c, Anim.CALM_1, "What do I get from this?");
				c.nextChat = 8609;
				break;
			case 8609:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"If I can reclaim the sword with my own specialist skills,",
					"I'll return it to you in peak condition. If it's an",
					"antique, I'll just give you what I think it's worth and",
					"I generally pay quite well. However, if it's just a piece");
				c.nextChat = 8610;
				break;
			case 8610:
				npc(c, "Tindel Marchant", Anim.CALM_1, 
					"of junk, I'll simply give you the bad news and get",
					"rid of the item for you.");
				c.nextChat = 8601; // Loops back to options
				break;

			// --------------------------------------------------------------
			// OPTION 4: "Ok, I'll give it a go!" (Check Items)
			// --------------------------------------------------------------
			case 8611:
				player(c, Anim.CALM_1, "Ok, I'll give it a go!");
				c.nextChat = 8612;
				break;
			case 8612:
				boolean hasSword = c.getItems().playerHasItem(RUSTY_SWORD);
				boolean hasScimitar = c.getItems().playerHasItem(RUSTY_SCIMITAR);
				boolean hasMoney = c.getItems().playerHasItem(COINS, FEE);

				if (!hasSword && !hasScimitar) {
					npc(c, "Tindel Marchant", Anim.SAD, "Sorry my friend, but you don't seem to have", "any swords that need to be identified.");
					c.nextChat = 0;
				} else if (!hasMoney) {
					npc(c, "Tindel Marchant", Anim.SAD, "Sorry my friend, but you don't seem to have", "enough money to cover my fee!");
					c.nextChat = 0;
				} else if (hasSword && hasScimitar) {
					options(c, 8612, "The sword", "The scimitar");
				} else if (hasSword) {
					identifyWeapon(c, RUSTY_SWORD);
				} else {
					identifyWeapon(c, RUSTY_SCIMITAR);
				}
				break;

			// --------------------------------------------------------------
			// OPTION 5: "Ok, thanks."
			// --------------------------------------------------------------
			case 8613:
				player(c, Anim.CALM_1, "Ok, thanks.");
				c.nextChat = 0;
				break;
			}
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		// MAIN MENU 
		if (c.dialogueAction == 8601) {
			switch (buttonId) {
			case OPT5_FIRST: next(c, 8602); break;  
			case OPT5_SECOND: next(c, 8605); break; 
			case OPT5_THIRD: next(c, 8608); break;  
			case OPT5_FOURTH: next(c, 8611); break; 
			case OPT5_FIFTH: next(c, 8613); break;  
			}
		}
		// SWORD OR SCIMITAR CHOICE
		else if (c.dialogueAction == 8612) {
			switch (buttonId) {
			case OPT2_FIRST: identifyWeapon(c, RUSTY_SWORD); break;
			case OPT2_SECOND: identifyWeapon(c, RUSTY_SCIMITAR); break;
			}
		}
	}

	/**
	 * Exposed globally so it can be triggered by ItemOnNpc!
	 * We use c.getDH().npcChat directly here to ensure the NPC ID forces correctly 
	 * even if the player didn't initiate a dialogue first.
	 */
	public static void identifyWeapon(Player c, int itemId) {
		if (!c.getItems().playerHasItem(COINS, FEE)) {
			c.getDH().npcChat(1358, "Tindel Marchant", Anim.SAD.getAnimationId(), 
				"Sorry my friend, but you don't seem to have", "enough money to cover my fee!");
			c.nextChat = 0;
			return;
		}

		if (!c.getItems().playerHasItem(itemId)) {
			return;
		}
		
		c.getItems().deleteItem(COINS, FEE);
		c.getItems().deleteItem(itemId, 1);
		
		String weaponName = (itemId == RUSTY_SWORD) ? "sword" : "scimitar";
		c.sendMessage("You hand Tindel 100 coins plus the rusty " + weaponName + ".");

		// 10% chance it is junk
		if (Misc.random(10) == 0) {
			c.getDH().npcChat(1358, "Tindel Marchant", Anim.SAD.getAnimationId(), 
				"Sorry my friend, but the item wasn't", "worth anything. I've disposed of it for you.");
			c.nextChat = 0;
			return;
		}

		// 5% chance it is a rare antique (Coins reward)
		if (Misc.random(20) == 0) {
			int coinReward = 1500 + Misc.random(3500); 
			c.getItems().addItem(COINS, coinReward);
			c.getDH().npcChat(1358, "Tindel Marchant", Anim.HAPPY.getAnimationId(), 
				"Wow! The item you gave me was a very rare antique...",
				"no real use...but hey, it's worth some money!",
				"That's a fair price for the item! I'll add it to my collection!");
			c.sendMessage("Tindel gives you " + coinReward + " coins.");
			c.nextChat = 0;
			return;
		}

		// Otherwise, give a cleaned weapon
		int rewardId = (itemId == RUSTY_SWORD) 
			? SWORD_REWARDS[Misc.random(SWORD_REWARDS.length - 1)] 
			: SCIMITAR_REWARDS[Misc.random(SCIMITAR_REWARDS.length - 1)];

		String rewardName = c.getItems().getItemName(rewardId).toLowerCase();
		c.getItems().addItem(rewardId, 1);
		c.getDH().npcChat(1358, "Tindel Marchant", Anim.CALM_1.getAnimationId(), 
			"There you go my friend, it turned out to", "be a " + rewardName + ".");
		c.getAD().completeAchievement("ArdougneEasy", "Have Tindel Marchant identify a rusty sword for you", 7);
		
		c.sendMessage("Tindel gives you a " + rewardName + ".");
		c.nextChat = 0;
	}
}