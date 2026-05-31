package server.model.players.skills.cooking;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
//import com.rebotted.game.content.music.sound.SoundList;
//import com.rebotted.game.content.skills.SkillHandler;
import server.model.items.ItemAssistant;
import server.model.players.Player;
import server.model.players.Client;
import server.util.Misc;
import server.model.players.Sound;

public class CookingTutorialIsland {

	public static void cookThisFood(Player p, int i, int object) {
		switch (i) {
		case 2514:
			cookFish(p, i, 30, 1, 323, 315, object);
			break;
		default:
			p.sendMessage("Nothing interesting happens.");
			break;
		}
	}

	private static void cookFish(Player c, int itemID, int xpRecieved,
			int levelRequired, int burntFish, int cookedFish, int object) {
		//if (!COOKING) {
		//	c.getPacketSender().sendMessage(
		//			"Cooking is currently disabled.");
			//return;
		//}
		/*if (!hasRequiredLevel(c, 7, levelRequired, "cooking", "cook this")) {
			return;
		}*/
		int chance = c.playerLevel[7];
		//if (c.playerEquipment[c.playerHands] == 775) {
		//	chance = c.playerLevel[7] + 8;
		//}
		if (chance <= 0) {
			chance = Misc.random(5);
		}
		c.playerSkillProp[7][0] = itemID;
		c.playerSkillProp[7][1] = xpRecieved;
		c.playerSkillProp[7][2] = levelRequired;
		c.playerSkillProp[7][3] = burntFish;
		c.playerSkillProp[7][4] = cookedFish;
		c.playerSkillProp[7][5] = object;
		c.playerSkillProp[7][6] = chance;
		c.stopPlayerSkill = false;
		int item = c.getItems().getItemAmount(c.playerSkillProp[7][0]);
		if (item == 1) {
			c.doAmount = 1;
			cookTutFish(c);
			return;
		}
		viewCookInterface(c, itemID);
	}

	public static void getAmount(Player c, int amount) {
		int item = c.getItems().getItemAmount(317);
		if (amount > item) {
			amount = item;
		}
		c.doAmount = amount;
		cookTutFish(c);
	}

	public static void resetCooking(Player c) {
		c.playerSkilling[7] = false;
		c.stopPlayerSkill = false;
		for (int i = 0; i < 6; i++) {
			c.playerSkillProp[7][i] = -1;
		}
	}

	private static void viewCookInterface(Player c, int item) {
		c.getPA().showChatboxInterface(1743);
		c.getPA().sendFrame246(13716, 190, item);
		c.getPA().sendFrame126("" + c.getItems().getItemName(item) + "", 13717);
	}

	private static void cookTutFish(final Player c) {
		if (c.playerSkilling[7]) {
			return;
		}
		int item = 2514;
		if (c.tutorialProgress == 6) {
			c.playerSkilling[7] = true;
			c.stopPlayerSkill = true;
			c.getPA().closeAllWindows();
			if (c.playerSkillProp[7][5] > 0) {
				 c.startAnimation(c.playerSkillProp[7][5] == 2732 ? 897 :
				 896);
				c.startAnimation(897);
				//if (GameConstants.SOUND) {
					//c.getPA().sendSound(SoundList.COOK_ITEM, 100, 0);
					c.getPA().sendSound(Sound.SOUND_LIST.COOK_ITEM.getSound(), 0, 8);
				//}

			}
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					c.getItems().deleteItem(item, c.getItems().getItemSlot(item), 1);
					if (c.cookStage1 == 1) {
						c.getPA().chatbox(6180);
						c.getDH().sendStartInfo(
										"You have just burned your first shrimp. This is normal. As you",
										"get more experience in Cooking, you will burn stuff less often.",
										"Let's try cooking without burning it this time. First catch some",
										"more shrimp then use them on a fire.",
										"Burning your shrimp.");
						c.getPA().chatbox(6179);
						c.cookStage1 = 0;
						c.getItems().addItem(7954, 1);
					} else {
						c.getPA().chatbox(6180);
						c.getDH().sendStartInfo(
										"If you'd like a recap on anything you've learnt so far, speak to",
										"the Survival Expert. You can now move on to the next",
										"instructor. Click on the gate shown and follow the path.",
										"Remember, you can move the camera with the arrow keys.",
										"Well done, you've just cooked your first RuneScape meal");
						c.getPA().chatbox(6179);
						c.getPA().createObjectHints(3089, 3092, c.getHeight(), 2);
						c.getPA().addSkillXP(15, 7);
						c.getItems().addItem(315, 1);
						c.tutorialProgress = 7;
					}
					//deleteTime(c);
					if (!c.getItems().playerHasItem(317, 1) || c.doAmount <= 0) {
						container.stop();
					}
					if (!c.stopPlayerSkill) {
						container.stop();
					}
				}

				@Override
				public void stop() {
					resetCooking(c);
				}
			}, 4);
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

				@Override
				public void execute(CycleEventContainer container) {
					if (c.playerSkillProp[7][5] > 0) {
						// c.getPacketSender().sendSound(357, 100, 1); //
					c.getPA().sendSound(Sound.SOUND_LIST.COOK_ITEM.getSound(), 0, 8);
						// cook sound
						c.startAnimation(897);
					}
					if (!c.stopPlayerSkill) {
						container.stop();
					}
				}

				@Override
				public void stop() {

				}
			}, 4);
			return;
		}
	}
}
