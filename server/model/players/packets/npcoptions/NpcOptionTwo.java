package server.model.players.packets.npcoptions;

import java.util.concurrent.TimeUnit;

import server.clip.Region;
//import server.content.PotionMixing;
//import server.content.achievement_diary.fremennik.FremennikDiaryEntry;
////import server.content.achievement_diary.karamja.KaramjaDiaryEntry;
//import server.content.achievement_diary.lumbridge_draynor.LumbridgeDraynorDiaryEntry;
//import server.content.achievement_diary.varrock.VarrockDiaryEntry;
//import server.content.achievement_diary.wilderness.WildernessDiaryEntry;
import server.model.items.shops.perdue.PerdueShop;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Pets;
//import server.model.npcs.pets.PetHandler;
//import server.model.npcs.pets.Probita;
import server.model.players.Player;
import server.model.players.PlayerAssistant;
import server.model.players.content.DeathRetrieval;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.npc.TindelMarchant;
//import server.model.players.PlayerAssistant.PointExchange;
import server.model.players.skills.Skill;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.sawmill.Sawmill;
import server.model.players.skills.fishing.Fishing;
import server.model.players.skills.fishing.spots.FishSpotDef;
import server.model.players.skills.fishing.spots.FishSpotDef.SpotCategory;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.thieving.Thieving.Pickpocket;
import server.world.Boundary;
import server.world.World;

/*
 * @author Matt
 * Handles all 2nd options on non playable characters.
 */

public class NpcOptionTwo {

	public static void handleOption(Player player, NPC npc) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;

		/*
		 * if(Fishing.fishingNPC(c, npcType)) { Fishing.fishingNPC(c, 2, npcType);
		 * return; }
		 */

		int npcType = npc.npcType;
		if (PetHandler.isPet(npcType)) {
			if (PetHandler.getOptionForNpcId(npcType) == "second") {
				if (PetHandler.pickupPet(player, npcType, true))
					return;
			}
		}
		//player.getQuestManager().onNpcClick(2, npc);
		//if (World.getWorld().getHolidayController().clickNpc(player, 2, npcType)) {
		//	return;
		//}
		if (FishSpotDef.NPC_TO_AREA.containsKey(npc.npcType)) {
		    System.out.println("Yep, that's a fishing spot!");
		   int option = 2;
		    SpotCategory category = FishSpotDef.getCategory(npc.npcType, option);
		    if (category != null) {
		        switch (category) {
		            case ROD_BAIT:
		                player.sendMessage("ROD_BAIT");
		                Fishing.attemptCatch(player, npc.npcType, option);// TODO: rod fishing logic here
		                break;
		            case HARPOON:
		                player.sendMessage("HARPOON");
		                // TODO: cage/harpoon fishing logic here
		                Fishing.attemptCatch(player, npc.npcType, option);// TODO: rod fishing logic here
		                break;
		            case BIG_HARPOON:
		                player.sendMessage("BIG_HARPOON");
		                // TODO: big net/harpoon logic here
		                Fishing.attemptCatch(player, npc.npcType, option);// TODO: rod fishing logic here
		                break;
		            case SMALL_NET_BAIT:
		                player.sendMessage("SMALL_NET_BAIT");
		                // TODO: small net/bait fishing logic here
		                Fishing.attemptCatch(player, npc.npcType, option);// TODO: rod fishing logic here
		                break;
		           /* case PISCATORIS:
		                player.sendMessage("PISCATORIS");
		                // TODO: piscatoris fishing logic here
		                Fishing.attemptCatch(player, npc.npcType);// TODO: rod fishing logic here
		                break;
		           /* case TUTORIAL:
		                player.sendMessage("TUTORIAL");
		                // TODO: tutorial fishing logic here
		                Fishing.attemptCatch(player, npc.npcType);// TODO: rod fishing logic here
		                break;
		            case BARBARIAN:
		                player.sendMessage("BARBARIAN");
		                // TODO: barbarian fishing logic here
		                Fishing.attemptCatch(player, npc.npcType);// TODO: rod fishing logic here
		                break;
		            case ANGLERFISH:
		                player.sendMessage("ANGLERFISH");
		                // TODO: anglerfish fishing logic here
		                Fishing.attemptCatch(player, npc.npcType);// TODO: rod fishing logic here
		                break;*/
		            default:
		                break;
		        }
		    }
		}

		player.getAD().DiaryNpcSecondClick(npc);
		switch (npcType) {
			case 7456:
				PerdueShop.openShop(player);
				break;
		case 1358:
			if(player.getItems().playerHasItem(TindelMarchant.RUSTY_SCIMITAR, 1)) {
                TindelMarchant.identifyWeapon(player, TindelMarchant.RUSTY_SCIMITAR);
            } else if(player.getItems().playerHasItem(TindelMarchant.RUSTY_SWORD, 1)) {
                TindelMarchant.identifyWeapon(player, TindelMarchant.RUSTY_SWORD);
            } else {
                player.sendMessage("Tindel only knows how to identify rusty swords and scimitars.");
            }
			break;
		case 8685:
			player.getShops().openShop(70);
			break;
		case 3199:
			player.getShops().openShop(183);
			break;
		case 2890:
			player.getShops().openShop(47);
			break;
		case 7204:
			player.getShops().openShop(130);
			player.sendMessage("@blu@ Please type ::donate to go to the donation store. 1 Donator Token is $1. ");
			player.sendMessage("@blu@ Type @pur@::dperks</col> to go to the donation store. 1 Donator Token is $1. ");
			player.sendMessage("@blu@Anything above at once will have a 100% chance to receive a @pur@Mystery Box</col>!");
			player.sendMessage("@blu@Anything above $25 at once will have a 100% chance to receive a @pur@Valius Box</col>!");
			player.sendMessage("@blu@Anything above $50 at once will have a 100% chance to receive a @pur@Ultra Mystery Box</col>!");
			player.sendMessage("@blu@You will receive 20% bonus exp for certain amount of time depening on the amount spent");
			break;
			
		case 3819://limited time shops
			player.getShops().openShop(137);
			player.sendMessage("@blu@ Please type ::donate to go to the donation store. 1 Donator Token is $1. ");
			player.sendMessage("@blu@ Type @pur@::dperks</col> to go to the donation store. 1 Donator Token is $1. ");
			player.sendMessage("@blu@Anything above at once will have a 100% chance to receive a @pur@Mystery Box</col>!");
			player.sendMessage("@blu@Anything above $25 at once will have a 100% chance to receive a @pur@Valius Box</col>!");
			player.sendMessage("@blu@Anything above $50 at once will have a 100% chance to receive a @pur@Ultra Mystery Box</col>!");
			player.sendMessage("@blu@You will receive 20% bonus exp for certain amount of time depening on the amount spent");
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
		case 7690:
			//player.getInfernoMinigame().gamble();
			break;
		case 1909:
			player.getDH().sendDialogues(901, 1909);
			break;
		case 5449:
			//PotionMixing.decantInventory(player);
			//player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.POTION_DECANT);
			break;
		case 2989:
			player.getShops().openShop(120);
			break;
			
		case 3413:
			player.getShops().openShop(140);
			break;

		case 3307:
			player.getPA().showInterface(37700);
			player.sendMessage("Set different colors for specific items for easier looting!");
			break;

		case 4321:
			int totalBlood = player.getItems().getItemAmount(13307);
			if (totalBlood >= 1) {
				//player.getPA().exchangeItems(PointExchange.BLOOD_POINTS, 13307, totalBlood);
			}
			break;

		case 822:
			player.getShops().openShop(81);
			break;
		case 2033:
			DeathRetrieval.open(player);
			break;
		case 7520:
			player.getDH().sendDialogues(855, 7520);
			break;

		case 6774:
			player.getShops().openShop(117);
			break;
		case 5314:
			if (player.lastTeleportX == 0) {
				player.sendMessage("You haven't teleported anywhere recently.");
			} else {
				player.getPA().startTeleport(player.lastTeleportX, player.lastTeleportY, player.lastTeleportZ, "modern");
			}
			break;
		case 3254:
			player.getPA().showInterface(65000);
			/*
			 * player.getPA().showInterface(62100); int startId = 62107; for (final
			 * teleports t : teleports.values()) { for(int i = 0; i <
			 * t.getTeleports().length; i++) { TeleportOption tele = t.getTeleports()[i];
			 * player.getPA().sendFrame126("<shad=-1>"+tele.getName(), startId); startId++;
			 * } } for(int i = startId; i < 62197; i++) { player.getPA().sendFrame126("",
			 * i); break;}
			 */
		case 6773:
			//if (!player.pkDistrict) {
			//	player.sendMessage("You cannot do this right now.");
			//	return;
			//}
			//if (player.inClanWarsSafe()) {
			//	player.getSafeBox().openSafeBox();
			//}
			break;

		case 4407:
			player.getShops().openShop(19);
			break;

		case 2040:
			if (player.deathItems.size() > 0) {
				player.getDH().sendDialogues(642, 2040);
				player.nextChat = -1;
			} else {
				if (player.getZulrahEvent().isActive()) {
					player.getDH().sendStatement("It seems that a zulrah instance for you is already created.",
							"If you think this is wrong then please re-log.");
					player.nextChat = -1;
					return;
				}
				player.getZulrahEvent().initialize();
			}
			break;
		case 3688:
			player.getShops().openShop(111);
			break;
		case 3689:
			player.getShops().openShop(112);
			break;
		case 17: // Rug merchant - Bedabin Camp
			/*if (!player.getDiaryManager().getDesertDiary().hasCompleted("HARD")) {
				player.getDH().sendNpcChat1(
						"You must have completed all hard diaries here in the desert \\n to use this location.", 17,
						"Rug Merchant");
				return;
			}*/
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3180, 3043, 0, "You step on the carpet and take off...",
					"at last you end up in bedabin camp.", 3);
			break;

		case 3077:
			long milliseconds = (long) player.playTime * 600;
			long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
			long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
			String time = days + " days and " + hours + " hours.";
			player.getDH().sendNpcChat1("You've been playing Valius for " + time, 3077, "Hans");
			//player.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.HANS);
			break;
		case 3680:
			AgilityHandler.delayFade(player, "NONE", 2674, 3274, 0, "The sailor brings you onto the ship.",
					"and you end up in ardougne.", 3);
			//player.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.SAIL_TO_ARDOUGNE);
			break;
		case 8686://nurmof's pickaxe
			player.getShops().openShop(20);
			break;
		case 5904://Dwarven mine general shop
			player.getShops().openShop(55);
			break;
		case 2785://huras crossbow shop
			player.getShops().openShop(57);
			break;
		case 2886://aubury
			player.getShops().openShop(25);
			break;
		case 5034:
			player.lastX = player.getX();
			player.lastY = player.getY();
			player.getPA().startTeleport(2929, 4813, 0, "modern");
			//player.getDiaryManager().getLumbridgeDraynorDiary()
			//		.progress(LumbridgeDraynorDiaryEntry.TELEPORT_ESSENCE_LUM);
			break;

		case 5906:
			//Probita.cancellationOfPreviousPet(player);
			break;
		case 2147:
			if(player.getX() > 1821 && player.getX() < 1827 && 
					player.getY() > 3688 && player.getY() < 3691)
						player.getPA().movePlayer(3054, 3246, 0);
			break;
		case 8630:
			player.getPA().movePlayer(1825, 3690, 0);
			break;
		case 2180:
			player.getDH().sendDialogues(70, 2180);
			break;

		case 401:
		case 402:
		case 405:
		case 7663:
			player.getDH().sendDialogues(3304, npcType);
			break;
		case 6797: // Nieve
			if (player.getSkills().getLevel(Skill.SLAYER) < 90) {
				player.getDH().sendNpcChat1("You must have a slayer level of atleast 90 weakling.", 6797, "Nieve");
				return;
			} else {
				player.getDH().sendDialogues(3304, player.npcType);
			}
			break;
		case 5919: // Grace
			player.getShops().openShop(18);
			break;
		case 311:
			player.getDH().sendDialogues(661, 311);
			break;
		case 4423: // Jossik
			player.getShops().openShop(13);
			break;
		case 6747:
			player.getShops().openShop(77);
			break;
		case 2184:
			player.getShops().openShop(29);
			break;
		case 2580:
			player.getPA().startTeleport(3039, 4835, 0, "modern");
			//player.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.ABYSS_TELEPORT);
			player.dialogueAction = -1;
			player.teleAction = -1;
			break;
		case 3936:
			AgilityHandler.delayFade(player, "NONE", 2421, 3781, 0, "You board the boat...", "And end up in Jatizso",
					3);
			//player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.TRAVEL_JATIZSO);
			break;
		case 3894:
			player.getShops().openShop(26);
			break;
			// --- MEN / WOMEN ---
					case 3108:
					case 3107:
					case 3106:
					case 3078:
					case 3080:
					case 3083:
					case 3085:
					case 3223: // Woman
					case 3225: // Woman
					case 3112:
						player.getThieving().steal(Pickpocket.MAN, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- H.A.M. MEMBERS ---
					case 2540: // Female
					case 2541: // Female
						player.getThieving().steal(Pickpocket.HAM_FEMALE, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 2542: // Male
					case 2543: // Male
						player.getThieving().steal(Pickpocket.HAM_MALE, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- FARMERS ---
					case 3114:
					case 3086:
					case 3087:
					case 3088:
						player.getThieving().steal(Pickpocket.FARMER, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					case 5730:
					case 5731: // Martin the Master Gardener
						player.getThieving().steal(Pickpocket.MASTER_FARMER, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- WARRIORS ---
					case 3260: // Al-Kharid Warrior
					case 3103: // Warrior (Varrock)
						player.getThieving().steal(Pickpocket.WARRIOR, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- ROGUES ---
					case 3182:
					case 3183:
						player.getThieving().steal(Pickpocket.ROGUE, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- GUARDS ---
					case 3269:
					case 3271:
					case 3272:
					case 3273:
					case 3274:
					case 5418:
					case 7016:
						player.getThieving().steal(Pickpocket.GUARD, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- BANDITS / THUGS ---
					case 3550: // Menaphite Thug
						player.getThieving().steal(Pickpocket.MENAPHITE_THUG, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 3546: // Bearded Pollnivnian Bandit
						player.getThieving().steal(Pickpocket.BEARDED_POLLNIVNIAN_BANDIT, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 3544: // Desert Bandit
						player.getThieving().steal(Pickpocket.DESERT_BANDIT, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- KNIGHTS / PALADINS / HEROES ---
					case 3297: // Knight of Ardougne
					case 3298:
						player.getThieving().steal(Pickpocket.KNIGHT_ARDOUGNE, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 3294: // Paladin
						player.getThieving().steal(Pickpocket.PALADIN, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 3295: // Hero
						player.getThieving().steal(Pickpocket.HERO, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- HIGH LEVEL (Gnome, Vyre, Elf) ---
					case 6094:
					case 6095:
					case 6096:
						player.getThieving().steal(Pickpocket.GNOME, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 9633: // Vyre
					case 9634:
						player.getThieving().steal(Pickpocket.VYRE, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 5297: // Elf
					case 5298:
						player.getThieving().steal(Pickpocket.ELF, NPCHandler.npcs[player.rememberNpcIndex]);
						break;

					// --- MISC ---
					case 3090: // Cave Goblin
						player.getThieving().steal(Pickpocket.CAVE_GOBLIN, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
					case 5440: // TzHaar-Hur
						player.getThieving().steal(Pickpocket.TZHAAR_HUR, NPCHandler.npcs[player.rememberNpcIndex]);
						break;
		case 3257:
			//if(player.getMode().isIronman() || player.getMode().isUltimateIronman() || player.getMode().isHcIronman() || player.getMode().isGroupIronman()){
			//	player.sendMessage("You cannot open this shop as an Ironman player.");
			//}else{
				player.getShops().openShop(16);
			//}
			break;
		case 637:
			player.getShops().openShop(6);
			break;
		case 3219:
			player.getShops().openShop(113);
			break;
		case 534:
			if (Boundary.isIn(player, Boundary.VARROCK_BOUNDARY)) {
			//	player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.DRESS_FOR_SUCESS);
			}
			player.getShops().openShop(77);
			break;
		case 732:
			player.getShops().openShop(16);
			break;
		case 5809:
			player.getShops().openShop(20);
			break;
		case 315:
			player.getShops().openShop(80);
			//player.sendMessage("@blu@ You have @bla@" + player.getBH().getBounties() + "</col> Bounty hunter points.");
			break;
		case 6599:
			player.getShops().openShop(79);
			break;
		case 3341:
			//PlayerAssistant.refreshSpecialAndHealth(player);
			break;
		case 403:
			player.getDH().sendDialogues(12001, -1);
			break;
		case 3216:
			player.getShops().openShop(8);
			break;
		case 2578:
			player.getDH().sendDialogues(2400, -1);
			break;
			/*
			 * FISHING
			 *    startFishing(fishing spot id,fishing tool item id);
			 */
			case 3657://cage and harpoon
				//player.getFishing().startFishing(npcType,311);
				break;
			case 3417://lure&bait
				//player.getFishing().startFishing(npcType,307);
				break;
			case 3913://smallnet
			//player.getFishing().startFishing(npcType,307);
			break;
			case 4712://manta
			//	player.getFishing().startFishing(npcType, 305);
				break;
			case 1520://shark
				//player.getFishing().startFishing(npcType,311);
				break;
		case 532:
			player.getShops().openShop(47);
			break;
		case 1599:
			player.getShops().openShop(10);
			player.sendMessage("You currently have @red@" + player.getSlayer().getPoints() + " @bla@slayer points.");
			break;
		case 953: // Banker
		case 2574: // Banker
		case 166: // Gnome Banker
		case 1702: // Ghost Banker
		case 494: // Banker
		case 495: // Banker
		case 496: // Banker
		case 497: // Banker
		case 498: // Banker
		case 499: // Banker
		case 394:
		case 567: // Banker
		case 766:
		case 1036: // Banker
		case 1360: // Banker
		case 2163: // Banker
		case 2164: // Banker
		case 2354: // Banker
		case 2355: // Banker
		case 2568: // Banker
		case 2569: // Banker
		case 2570: // Banker
		case 2200:
			player.getPA().openUpBank();
			break;

		case 2819:
			player.getShops().openShop(53);//falador shop keeper
			break;
		case 2820:
			player.getShops().openShop(53);//falador shop keeper
			break;
		case 2813:
			player.getShops().openShop(54);//lumbridge shop keeper
			break;
		case 2814:
			player.getShops().openShop(54);//lumbridge shop keeper
			break;
		case 2816:
			player.getShops().openShop(1);//varrock shop keeper
			break;
		case 2815:
			player.getShops().openShop(1);//varrock shop keeper
			break;
		case 8726:
			player.getShops().openShop(36);//ardy spice stall
			break;
		case 8723:
			player.getShops().openShop(32);//ardy gem stall
			break;
		case 8724:
			boolean recentlyStolen = (System.currentTimeMillis() - player.lastBakerTheft < 180000);

			if (recentlyStolen) {
				if (player.rememberNpcIndex > 0) {
					NPC baker = NPCHandler.npcs[player.rememberNpcIndex];
					if (baker != null) {
						baker.forceChat("You're the one who stole something from me!");
					}
				}
				boolean guardFound = false;
				for (NPC guard : NPCHandler.npcs) {
					if (guard != null && !guard.isDead && guard.heightLevel == player.HeightLevel) {
						
						// Use your new method to ensure it's actually a combat-capable guard/knight!
						if (player.getThieving().isGuard(guard.npcType)) { 
							
							// Check if the guard is within 15 tiles
							if (player.goodDistance(player.getX(), player.getY(), guard.getX(), guard.getY(), 15)) {
								
								// FACING CHECK: Is the guard actually looking in the player's direction?
								if (player.getThieving().isLookingAt(guard, player.getX(), player.getY())) {
									
									// LINE OF SIGHT: Make sure there isn't a wall between the guard and the player
									if (Region.canProjectileMove(guard.getX(), guard.getY(), player.getX(), player.getY(), guard.heightLevel, 1, 1)) {
										
										guard.turnNpc(player.getX(), player.getY());
										guard.forceChat("Stop right there!");
										
										// Trigger the combat aggro 
										guard.underAttack = true;
										guard.killerId = player.getIndex();
										player.underAttackBy = guard.getIndex();
										player.underAttackBy2 = guard.getIndex();
										
										guardFound = true;
										break; // We only need one guard to jump them!
									}
								}
							}
						}
					}
				}
				return;
			} else 
			player.getShops().openShop(59);//baker stall 2
			break;
		case 8725:
			boolean recentlyStolen2 = (System.currentTimeMillis() - player.lastBakerTheft < 180000);

			if (recentlyStolen2) {
				if (player.rememberNpcIndex > 0) {
					NPC baker = NPCHandler.npcs[player.rememberNpcIndex];
					if (baker != null) {
						baker.forceChat("You're the one who stole something from me!");
					}
				}
				return;
			} else 
				player.getShops().openShop(48);//baker stall 1
			break;
		case 2821:
		case 2822:
			player.getShops().openShop(52);//edgeville gen store
			break;
		case 2882:
			player.getShops().openShop(35);//horvik
			break;
		case 2280:
			player.getShops().openShop(35);//zaffs staffs
			break;
		case 2880:
			player.getShops().openShop(19);//zaffs staffs
			break;
	/*
	 * warriors guild
	 */
		case 3101:
			Sawmill.open(player);
			break;
		case 2469:
			player.getShops().openShop(49);//lidio
			break;
		case 2470:
			player.getShops().openShop(57);//lilly
			break;
		case 2471:
			player.getShops().openShop(58);//anton
			break;
	/*
	 * end warriors
	 */
		case 2884:
		case 2885:
			player.getShops().openShop(28);//varrock swordshop
			break;
		case 5896:
			player.getShops().openShop(33);//flynns maces
			break;
		case 3214:
			player.getShops().openShop(27);//cassies shields
			break;
		case 6529:
			player.getShops().openShop(56);//herquins gemss
			break;
		}
	}

}
