package server.model.players.packets.dialogue;

import server.model.npcs.bosses.skotizo.Skotizo;
//import server.model.players.skills.crafting;
//import server.model.items.impl.Flowers;
//import server.model.items.impl.Teles;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.quests.QuestManager;
import server.model.players.quests.WatchTower;
import server.model.players.skills.Skill;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.RoomObject;
import server.world.World;
import server.Config;
import server.content.barrows.RoomLocation;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

/**
 * Dialogue Options
 * @author Andrew (Mr Extremez)
 */

public class DialogueOptions {
	
	public static void handleDialogueOptions(Player c, int buttonId) {

		QuestManager questManager = new WatchTower(c);
		if(questManager.handleDialogueAction(c, buttonId))
			return;
		switch (buttonId) {
			/*
			 *
			 * Destroy item options
			 *
			 */
		case 174160:
		case 174164:
		if(c.getItems().playerHasItem(c.DestroyID, 1)){
			c.getItems().deleteItem(c.DestroyID, c.getItems().getItemSlot(c.DestroyID), 1);
			c.DestroyID = -1;
			c.getPA().removeAllWindows();
		}
		break;
		case 174161:
		case 174165:
			c.getPA().removeAllWindows();
		break;
			/*
			 *
			 * 2 option dialogue
			 *
			 */
		case 9157://1st option button id
			
			if(c.getSecurity().dialogOption(c, buttonId))
				return;
			if(DialogueService.handleOption(c, buttonId))
				return;
			if (c.dialogueAction == 7286) {
				if (System.currentTimeMillis() - c.cerbDelay > 5000) {
					Skotizo skotizo = c.createSkotizoInstance();

					/*
					 * if (c.getSkotizoLostItems().size() > 0) { c.getDH().sendDialogues(642, 5870);
					 * c.nextChat = -1; return; }
					 */

					if (skotizo == null) {
						c.sendMessage("We are unable to allow you in at the moment.");
						c.sendMessage("Too many players.");
						return;
					}

					if (World.getWorld().getEventHandler().isRunning(c, "skotizo")) {
						c.sendMessage("You're about to fight start the fight, please wait.");
						return;
					}
					c.getSkotizo().init();
					c.getItems().deleteItem(19685, 1);
					c.getPA().closeAllWindows();
					c.nextChat = -1;
					c.cerbDelay = System.currentTimeMillis();
				} else {
					c.sendMessage("Please wait a few seconds between clicks.");
				}
			}
			if (c.dialogueAction == 29) {
				c.dialogueAction = -1;
				c.getPA().movePlayer(RoomLocation.getRandomSpawn());
				c.getPA().removeAllWindows();
				return;
			}
			switch (c.dialogueAction) {

			case 70300:
				c.getPA().movePlayer(1664, 10050, 0);
				c.sendMessage("Welcome to the Catacombs of Kourend.");
				c.getPA().closeAllWindows();
				break;
				case 55:
				c.getDH().sendDialogues(91, c.talkingNpc);
				return;
			case 56:
				c.getDH().sendDialogues(96, c.talkingNpc);
				return;
				case 17:
					c.getDH().sendDialogues(18, c.talkingNpc);
				return;
				case 20:
					c.getDH().sendDialogues(21, c.talkingNpc);
				return;
				case 24:
					if(c.getItems().playerHasItem(995, 2)){
							c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 2);
							c.getItems().addItem(950, 1);
									c.nextChat = 0;
									c.dialogueAction = 0;
									c.getPA().closeAllWindows();
					} else 
						c.getDH().sendDialogues(25, c.talkingNpc);
				return;
				case 106:
					c.getDH().sendDialogues(107, 463);
				return;
				case 127:
					c.getDH().sendDialogues(128, 463);
				return;
				case 3111:
					c.getDH().sendDialogues(3112, 946);
				return;
				case 57:
					c.getDH().sendDialogues(57, c.talkingNpc);
				return;
				/*Black Knights' Fortress Dialogue options*/
				case 1000://I seek a quest
					c.getDH().sendDialogues(3533, c.talkingNpc);
				return;
				case 3505://I laugh in the face of danger
					c.getDH().sendDialogues(3506, c.talkingNpc);
				return;
				case 3519://accept quest
					c.getDH().sendDialogues(3520, c.talkingNpc);
				return;
				case 4011:
				c.getDH().sendDialogues(4012, c.talkingNpc);
				return;
				case 4021:
				c.getDH().sendDialogues(4022, c.talkingNpc);
				return;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;

		case 9158:

			if(DialogueService.handleOption(c, buttonId))
				return;
			if (c.dialogueAction == 29) {
				c.dialogueAction = -1;
				c.getPA().removeAllWindows();
				return;
			}
			if(c.getSecurity().dialogOption(c, buttonId))
				return;
			switch (c.dialogueAction) {
			
				case 55:
				c.getDH().sendDialogues(92, c.talkingNpc);
				return;
				case 56:
				c.getDH().sendDialogues(95, c.talkingNpc);
				return;
				case 106:
					c.getDH().sendDialogues(109, 463);
				return;
				case 127:
					c.getDH().sendDialogues(130, 463);
				return;
				case 17:
					c.getDH().sendDialogues(31, c.talkingNpc);
				return;
				case 20:
					c.getDH().sendDialogues(29, c.talkingNpc);
				return;
				case 24:
					c.getDH().sendDialogues(27, c.talkingNpc);
				return;
				case 3111:
					c.getDH().sendDialogues(3117, 9487);
				return;
				case 57:
					c.getDH().sendDialogues(58, c.talkingNpc);
				return;
				/*Black Knights' Fortress Dialogue options*/
				case 3504:
				case 1000://I don't im just looking around
					c.getDH().sendDialogues(3531, c.talkingNpc);
				return;
				case 3505://I cower in danger
					c.getDH().sendDialogues(3525, c.talkingNpc);
				return;
				case 3519://decline quest
					c.getDH().sendDialogues(3523, c.talkingNpc);
				return;
				case 4011:
				c.getDH().sendDialogues(4913, c.talkingNpc);
				return;
			
			}			
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
			/*End*/
		
			/*
			 *
			 * 3 option dialogue
			 *
			 */

		case 9167:
			if(c.dialogueAction == 6755) {
				c.addingToNMZCoffer = true;
				c.xRemoveId = 995;
				c.xInterfaceId = 60013;
				c.xRemoveSlot = c.getItems().getItemSlot(995);
				c.getOutStream().createFrame(27);
				return;
			}
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}

			if(DialogueService.handleOption(c, buttonId))
				return;
			switch (c.dialogueAction) {

			case 16672:
				AgilityHandler.delayEmote(c, "", 2840, 3539, 2, 2);
				break;
			case 1112:
				c.getDH().sendDialogues(1113,  -1);
				return;
			case 30002: // Security question 1
	            // Correct answer is option 1 (button id 9167)
				if(c.getX() <= c.objectX && c.getY() == c.objectY) {
					c.getSecurity().strongholdDoorMove(c, c.getX()+1, c.getY());
				} else if(c.getX() >= c.objectX && c.getY() == c.objectY) {
					c.getSecurity().strongholdDoorMove(c, c.getX()-1, c.getY());
		            
				} else if(c.getY() >= c.objectY && c.getX() == c.objectX) {
					c.getSecurity().strongholdDoorMove(c, c.getX(), c.getY()-1);
				} else if(c.getY() >= c.objectY && c.getX() == c.objectX) {
					c.getSecurity().strongholdDoorMove(c, c.getX(), c.getY()+1);
				}
	            break;
				case 774:
					//c.getLaddersAndStairs().climbLadderorStair(c.objectX, c.objectY, 3205, 3209, 2, false, false);
				return;
				case 251:
					c.getPA().openUpBank();
					c.nextChat = 0;
				return;
				case 4001:
					c.getDH().sendDialogues(4002, c.talkingNpc);
				return;
				case 27419:
					AgilityHandler.delayEmote(c, "", 3205, 3209, 2, 2);
					c.getPA().closeAllWindows();
				return;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;

		case 9168:
			if(c.dialogueAction == 6755) {
				c.withdrawFromNMZCoffer = true;
				c.xRemoveId = 995;
				c.xInterfaceId = 60013;
				c.xRemoveSlot = c.getItems().getItemSlot(995);
				c.getOutStream().createFrame(27);
				return;
			}
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}


			switch (c.dialogueAction) {
			case 16672:
				AgilityHandler.delayEmote(c, "", 2840, 3539, 0, 2);
				break;
			case 1112:
				c.getDH().sendDialogues(1121, -1);
				return;
			case 30002:
				c.sendMessage("Incorrect!");
				return;
			case 27419:
				AgilityHandler.delayEmote(c, "", 3205, 3209, 0, 2);
				c.getPA().closeAllWindows();
			return;
				case 4001:
					c.getDH().sendDialogues(4003, -1);
				return;
				case 774:
					//c.getLaddersAndStairs().climbLadderorStair(c.objectX, c.objectY, 3205, 3209, 0, false, false);
				return;
				case 251:
					c.getBankPin().bankPinSettings();
					c.nextChat = 0;
				return;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;

		case 9169:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}

			if(c.dialogueAction == 6755) {
				c.addingToNMZCoffer = false;
				c.withdrawFromNMZCoffer = false;
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			switch (c.dialogueAction) {

			case 16672:
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				break;
			case 1112:
				c.getDH().sendDialogues(1117, -1);
				return;
			case 27419:
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
			return;
				case 4001:
					c.getDH().sendDialogues(4004, c.talkingNpc);
				return;
				case 251:
					c.getDH().sendDialogues(1015, 494);
				return;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
			/*End*/

		
			/*
			 *
			 * 4 option dialogue
			 *
			 */
		case 9178:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if (RoomObject.handleButtons(c, buttonId)) {
				return;
			}
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			if(c.usingCloak){
				c.getPA().startTeleport(Config.MONASTERYX, Config.MONASTERYY, 0, "modern");
				return;
			}
			if (c.usingGlory){
				c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
				return;
			}
			switch (c.dialogueAction) {
				case 420:
					c.getItems().addtoLootbagFromDeposit(c.xRemoveId, c.xRemoveSlot, 1);
					c.getPA().removeAllWindows();
				return;
				case 3:
					c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
				return;
				case 52:
				return;
			
			}		
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();	
			break;

		case 9179:

			if(DialogueService.handleOption(c, buttonId))
				return;
			if (RoomObject.handleButtons(c, buttonId)) {
				return;
			}
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			if (c.usingGlory){
				c.getPA().startTeleport(Config.AL_KHARID_X, Config.AL_KHARID_Y, 0, "modern");
				return;
			}
			switch (c.dialogueAction) {
				case 420:
					c.getItems().addtoLootbagFromDeposit(c.xRemoveId, c.xRemoveSlot, 5);
					c.getPA().removeAllWindows();
				return;
			
			}	
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();		
			break;

		case 9180:

			if(DialogueService.handleOption(c, buttonId))
				return;
			if (RoomObject.handleButtons(c, buttonId)) {
				return;
			}
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			if (c.usingGlory){
				c.getPA().startTeleport(Config.KARAMJA_X, Config.KARAMJA_Y, 0,"modern");
				return;
			}
			switch (c.dialogueAction) {
				case 420:
					c.getItems().addtoLootbagFromDeposit(c.xRemoveId, c.xRemoveSlot, 10);
					c.getPA().removeAllWindows();
				return;
				case 52:
					c.getDH().sendDialogues(65, c.talkingNpc);
				return;
			
			}	
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();		
			break;

		case 9181:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			if (c.usingGlory){
				c.getPA().startTeleport(Config.MAGEBANK_X, Config.MAGEBANK_Y,0, "modern");
				return;
			}
			switch (c.dialogueAction) {
				case 420:
					c.getItems().addtoLootbagFromDeposit(c.xRemoveId, c.xRemoveSlot, c.Xamount);
					//player.getPA().removeAllWindows();
				return;
				case 52:
					c.getDH().sendDialogues(63, c.talkingNpc);
				return;
			
			}	
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();		
			break;
			/*End*/
						/*
			 *
			 * 5 option dialogue
			 *
			 */
		case 9190:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			switch (c.dialogueAction) {
			case 5534:
					c.getPA().startTeleport(Config.FARMING_GUILD_X, Config.FARMING_GUILD_Y, 0, "modern");
					c.setDailyCapeLimit(Skill.FARMING.getId(), 1);
					c.sendMessage("You teleport to the Farming Guild entrance.");
				break;
				case 505:
					c.getDH().sendDialogues(506, 705);
				return;
				
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
		case 9191:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			switch (c.dialogueAction) {

			case 5534:
					c.getPA().startTeleport(Config.CATHERBY_FARM_X, Config.CATHERBY_FARM_Y, 0, "modern");
					c.setDailyCapeLimit(Skill.FARMING.getId(), 1);
					c.sendMessage("You teleport to Catherby.");
				break;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
		case 9192:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			switch (c.dialogueAction) {

			case 5534:
					c.getPA().startTeleport(Config.ARDY_FARM_X, Config.ARDY_FARM_Y, 0, "modern");
					c.setDailyCapeLimit(Skill.FARMING.getId(), 1);
					c.sendMessage("You teleport to the Ardougne farm.");
				break;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
		case 9193:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId)) {
				c.dialogueAction = 0;
				c.getPA().closeAllWindows();
				return;
			}
			switch (c.dialogueAction) {

			case 5534:
					c.getPA().startTeleport(Config.FALADOR_FARM_X, Config.FALADOR_FARM_Y, 0, "modern");
					c.setDailyCapeLimit(Skill.FARMING.getId(), 1);
					c.sendMessage("You teleport to the South Falador Farm.");
				break;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
		case 9194:
			if(DialogueService.handleOption(c, buttonId))
				return;
			if(c.getSecurity().dialogOption(c, buttonId))
				return;
			switch (c.dialogueAction) {

			case 5534:
					c.getPA().startTeleport(Config.FARMING_GUILD_X, Config.FARMING_GUILD_Y, 0, "modern");
					c.setDailyCapeLimit(Skill.FARMING.getId(), 1);
					c.sendMessage("You teleport to the undead farm.");
				break;
			}
			c.dialogueAction = 0;
			c.getPA().closeAllWindows();
			break;
			/*End*/
		}
	}

}
