package server.model.players.packets.npcoptions;

import server.Config;
//import server.content.achievement_diary.ardougne.ArdougneDiaryEntry;
//import server.content.achievement_diary.desert.DesertDiaryEntry;
//import server.content.achievement_diary.falador.FaladorDiaryEntry;
//import server.content.achievement_diary.fremennik.FremennikDiaryEntry;
//import server.content.achievement_diary.varrock.VarrockDiaryEntry;
import server.model.npcs.NPC;
//import server.model.npcs.pets.PetHandler;
import server.world.Boundary;
import server.model.players.Pets;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.Herblore;
import server.world.World;

/*
 * @author Matt
 * Handles all 3rd options on non playable characters.
 */

public class NpcOptionThree {

	public static void handleOption(Player player, NPC npc) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;
//		if (npcType != 2130 || npcType != 2131 || npcType != 2132) {
//			if (PetHandler.talktoPet(c, npcType))
//				return;
//		}

		int npcType = npc.npcType;
		if (PetHandler.isPet(npcType)) {
			if (PetHandler.getOptionForNpcId(npcType) == "third") {
				if (PetHandler.pickupPet(player, npcType, true))
					return;
			}
		}

		//player.getQuestManager().onNpcClick(3, npc);
		//if (World.getWorld().getHolidayController().clickNpc(player, 3, npcType)) {
		//	return;
		//}
		switch (npcType) {
		case 3097:
			player.getPA().sendFrame126("1: Basic wood - 5,000 coins", 62399);
			player.getPA().sendFrame126("2: Basic stone - 5,000 coins", 62400);
			player.getPA().sendFrame126("3: Whitewashed stone - 7,500 coins", 62401);
			player.getPA().sendFrame126("4: Fremennik-style wood - 10,000 coins", 62402);
			player.getPA().sendFrame126("5: Tropical wood - 15,000 coins", 62403);
			player.getPA().sendFrame126("6: Fancy stone - 25,000 coins", 62404);
			player.getPA().sendFrame126("7: Deathly mansion - 35,000 coins", 62405);
			player.getPA().sendFrame126("", 62406);
			player.getPA().sendFrame126("", 62407);
			player.getPA().sendFrame126("", 62408);
			player.getPA().sendFrame126("", 62409);
			player.getPA().sendFrame126("", 62410);
			player.getPA().sendFrame126("", 62411);
			player.getPA().showInterface(62396);
			break;
		case 1305:
			player.getPA().showInterface(2653);
			break;
		case 1428:
			//player.getPrestige().openShop();
			break;
		case 1909:
			player.getDH().sendDialogues(903, 1909);
			break;
		case 2989:
			//player.getPrestige().openShop();
			break;
		case 2886:
			player.lastX = player.getX();
			player.lastY = player.getY();
			player.getPA().startTeleport(2929, 4813, 0, "modern");
			break;
		case 4321:
			player.getShops().openShop(119);
			//player.sendMessage("You currently have @red@"+player.bloodPoints+" @bla@Blood Money Points!");
			break;
		case 7520:
			player.getShops().openShop(118);
			//player.sendMessage("You currently have @red@"+player.getRaidPoints()+" @bla@Raid Points!");
			break;
		case 6773:
			player.isSkulled = true;
			//player.skullTimer = Config.EXTENDED_SKULL_TIMER;
			player.headIconPk = 0;
			player.getPA().requestUpdates();
			player.sendMessage("@cr10@@blu@You are now skulled.");
			break;
		case 2200:
			player.getPA().openUpBank();
			break;
		case 1306:
			if (player.getItems().isWearingItems()) {
				player.sendMessage("You must remove your equipment before changing your appearance.");
				player.canChangeAppearance = false;
			} else {
				player.getPA().showInterface(3559);
				player.canChangeAppearance = true;
			}
			break;
		case 17: //Rug merchant - Nardah
			//if (!player.getDiaryManager().getDesertDiary().hasCompleted("EASY")) {
			//	player.getDH().sendNpcChat1("You must have completed all easy diaries here in the desert \\n to use this location.", 17, "Rug Merchant");
			//	return;
			//}
			//player.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CAST_BARRAGE);
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3402, 2916, 0, "You step on the carpet and take off...", "at last you end up in nardah.", 3);
			break;
		
		case 3936:
			AgilityHandler.delayFade(player, "NONE", 2310, 3782, 0, "You board the boat...", "And end up in Neitiznot", 3);
			//player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.TRAVEL_NEITIZNOT);
			break;
			
		case 402:
		case 401:
		case 405:
		case 6797:
		case 7663:
			player.getShops().openShop(44);
			player.sendMessage("I currently have @blu@" + player.getSlayer().getPoints() + " @bla@slayer points.");
			break;
		case 315:
			player.getDH().sendDialogues(548, 315);
			break;
		case 403:
			player.getDH().sendDialogues(12001, -1);
			break;
		case 1599:
			player.getShops().openShop(10);
			player.sendMessage("You currently have @red@" + player.slayerPoints + " @bla@slayer points.");
			break;
		case 836:
			player.getShops().openShop(103);
			break;
		case 5449:
			//Herblore.cleanAll(player);
			break;
		case 2580:
			if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
			//	player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TELEPORT_ESSENCE_VAR);
			}
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
			//	player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.TELEPORT_ESSENCE_ARD);
			}
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
			//	player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.TELEPORT_ESSENCE_FAL);
			}
			player.getPA().startTeleport(2929, 4813, 0, "modern");
			break;
		}
	}

	
	
}
