package server.model.players.packets.npcoptions;

import java.util.Objects;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.skills.hunter.trap.impl.Pitfall;
import server.world.Location;
import server.world.World;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.content.treasuretrails.types.CharlieTask;
import server.model.players.packets.dialogue.DialogueService;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.packets.dialogue.npc.HatiusCosaintus;
import server.model.players.packets.dialogue.npc.SilkMerchant;
import server.model.players.quests.EleWorkShop;
import server.model.players.skills.Shearing;
import server.model.players.skills.Skill;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.hunter.impling.Impling;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.fishing.Fishing;
import server.model.players.skills.fishing.*;
import server.model.players.skills.fishing.spots.*;
import server.model.players.skills.fishing.spots.FishSpotDef.SpotCategory;

/*
 * @author Matt NIGGER
 * Handles all first options on non playable characters.
 */
public class NpcOptionOne {

	public static void handleOption(Player player, NPC npc) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		
		player.clickNpcType = 0;
		player.rememberNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;
		if (PetHandler.isPet(npc.npcType)) {
			if (Objects.equals(PetHandler.getOptionForNpcId(npc.npcType), "first")) {
				if (PetHandler.pickupPet(player, npc.npcType, true))
					return;
			}
		}
		if (FishSpotDef.NPC_TO_AREA.containsKey(npc.npcType)) {
		    int option = 1;
		    SpotCategory category = FishSpotDef.getCategory(npc.npcType, option);
		    if (category != null) {
		        switch (category) {
		            case ROD_LURE:
		            case CAGE:
		            case BIG_NET:
		            case SMALL_NET_BAIT:
		            case PISCATORIS:
		            case TUTORIAL:
		            case BARBARIAN:
		            case ANGLERFISH:
		                Fishing.attemptCatch(player, npc.npcType, option);// TODO: rod fishing logic here
		                break;
		            default:
		                break;
		        }
		    }
		}

		switch (npc.npcType) {//taverly
			// --- FALCONRY: Alive Kebbits ---
			case 5531: // Spotted kebbit
			case 5532: // Dark kebbit
			case 5533: // Dashing kebbit
				server.model.players.skills.hunter.falconry.Falconry.attemptCatch(player, npc);
				break;

			// --- FALCONRY: Caught Kebbits ---
			case 1342: // Caught spotted kebbit
			case 1343: // Caught dark kebbit
			case 1344: // Caught dashing kebbit
				server.model.players.skills.hunter.falconry.Falconry.retrieveCatch(player, npc);
				break;
			case 2908:
			case 2907:
			case 2909:
				Pitfall.teaseAnimal(player, npc);
				break;
			case 0:
				player.getFarmingTools().loadInterfaces();
				break;
		case 6070:
			if (player.getItems().playerHasItem(995, 200) && !player.targetMinigame) {
				player.getItems().deleteItem(995, 200);
			    player.getItems().addItem(882, 10); // Give 10 Bronze Arrows
			    player.archeryGuildShots = 10;
			    player.archeryGuildScore = 0;
			    player.targetMinigame = true;
			    player.sendMessage("You pay the judge 200 coins and receive 10 bronze arrows.");
			} else {
				player.sendMessage("You need 200 coins to play.");
			}
			break;
		case 534:
			player.getShops().openShop(188);//thessalias shop
			break;
		case 3097:
			player.getDH().sendDialogues(6700, npc.npcType);
			break;
			case 1340:
			case 1341:
				player.getDH().sendDialogues(11000, npc.npcType);
				break;
		case 9476: // runescape guide
			if (player.tutorialProgress == 0) {
				player.getDH().sendDialogues(3001, npc.npcType);
			}
			if (player.tutorialProgress == 1) {
				player.getDH().sendDialogues(3008, npc.npcType);
			}
			if (player.tutorialProgress >= 2) {
				player.getDH().sendNpcChat1("You should move on now.", npc.npcType, "Runescape Guide", 9861);
			}
			break;
			case 3519://sir lancelot
				player.getDH().sendDialogues(85400, npc.npcType);
				break;
			case 3520://sir gawain
				player.getDH().sendDialogues(85300, npc.npcType);
				break;
			case 3531://king arther
				player.getDH().sendDialogues(85000, npc.npcType);
				break;
			case 3522://sir belvedier
				player.getDH().sendDialogues(85200, npc.npcType);
				break;
			case 3199://candlemaker
				player.getDH().sendDialogues(86000, npc.npcType);
				break;
			case 3200://arhein
				player.getDH().sendDialogues(85500, npc.npcType);
				break;
			case 3528://Morgan la faye
				player.getDH().sendDialogues(85900, npc.npcType);
				break;
			case 3530://lady of the lake
				player.getDH().sendDialogues(85900, npc.npcType);
				break;

		case 5607:
			player.getDH().sendDialogues(40500, npc.npcType);
			break;
		case 9477:// survival
			if (player.tutorialProgress == 2) {
				player.getDH().sendDialogues(3012, npc.npcType);
			}
		if (player.tutorialProgress == 5) {
			player.getDH().sendDialogues(3017, npc.npcType);
		}
		break;
		case 1120:
			player.getDH().sendDialogues(10000, npc.npcType);
			break;
		case 3305: // master chef
			if (player.tutorialProgress == 7) {
				player.getDH().sendDialogues(3021, npc.npcType);
			} else if (player.tutorialProgress >= 8) {
				player.getDH().sendDialogues(3025, npc.npcType);
			}
			break;

		case 9480: // quest guide
			if (player.tutorialProgress == 12) {
				player.getDH().sendDialogues(3043, npc.npcType);
			}
			if (player.tutorialProgress == 13) {
				player.getDH().sendDialogues(3045, npc.npcType);
			}
			break;
		case 9481: // mining tutor
			if (player.tutorialProgress == 14) {
				player.getDH().sendDialogues(3052, npc.npcType);
			}
			if (player.tutorialProgress == 16) {
				player.getDH().sendDialogues(3056, npc.npcType);
			}
			if (player.tutorialProgress == 20) {
				player.getDH().sendDialogues(3063, npc.npcType);
			}
			break;
			
		case 3307: // Combat deud
			if (player.tutorialProgress == 21) {
				player.getDH().sendDialogues(3067, npc.npcType);
			} else if (player.tutorialProgress == 23
					&& !player.getItems().playerHasItem(1171)
					&& !player.getItems().playerHasItem(1277)) {
				player.getDH().sendDialogues(3072, npc.npcType);
			} else if (player.getItems().playerHasItem(1171)
					&& player.getItems().playerHasItem(1277) && player.tutorialProgress == 23) {
				player.sendMessage(
						"I already gave you a sword and shield.");
				player.nextChat = 0;
				player.getDH()
						.chatboxText(
								"In your worn inventory panel, right click on the dagger and",
								"select the remove option from the drop down list. After you've",
								"unequipped the dagger, wield the sword and shield. As you",
								"pass the mouse over an item you will see its name.",
								"Unequipping items");
				player.getPA().removeHintIcon(player);
			} else if (player.tutorialProgress == 25) {
				player.getDH().sendDialogues(3074, npc.npcType);
			}
			break;

		case 3310: // fiancial dude
			if (player.tutorialProgress == 27) {
				player.getDH().sendDialogues(3079, npc.npcType);
			}
			// c.getPacketDispatcher().createArrow(1, 7);
			break;
			
		case 9485: // prayer dude
			if (player.tutorialProgress == 28) {
				player.getDH().sendDialogues(3089, npc.npcType);
			}
			if (player.tutorialProgress == 29) {
				player.getDH().sendDialogues(3092, npc.npcType);
			}
			if (player.tutorialProgress == 31) {
				player.getDH().sendDialogues(3097, npc.npcType);
			}
			break;
		case 5523:

			player.getDH().sendDialogues(8000, npc.npcType);
			break;
		case 3106: 
		case 3107:
		case 6988:
		case 3108:
		case 3109:
		case 6989:
		case 6815:
		case 6818:
		case 6987:
		case 3111: 
		case 3112:
		case 3113: 
		case 6990: 
		case 6991: 
		case 6992: 
		case 10728:
			player.getDH().sendDialogues(1000, npc.npcType);
			break;
		case 6773:
			player.getDH().sendDialogues(5430, npc.npcType);
			break;

		case 6774:
			player.getDH().sendDialogues(5430, 6773);
			break;
		case 1312:
			player.getDH().sendDialogues(8500, npc.npcType);
			break;
		case 6817:
		case 6816:
			player.getDH().sendDialogues(7000, npc.npcType);
			break;
		case 921:
			player.getDH().sendDialogues(9000, npc.npcType);
			break;
			
		case 5517:
			player.getDH().sendDialogues(3500, npc.npcType);
			break;
		case 1368: // The "Rock" NPC
		    // Ensure the player is far enough in the quest to need the ore
		    if (player.questStages[EleWorkShop.QUEST_ID] < EleWorkShop.FURNACE_HEATED) {
		    	player.sendMessage("You have no idea what this strange rock is for.");
		        return;
		    }

		    // Standard OSRS "Mining" animation before it wakes up
		    player.startAnimation(625); 
		    player.sendMessage("You swing your pick at the rock...");

		    // Short delay so the transformation doesn't look instant/glitchy
		    CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
		        @Override
		        public void execute(CycleEventContainer container) {
		            NPC npc = NPCHandler.npcs[player.rememberNpcIndex];
		            if (npc != null) {
		                npc.requestTransform(1366); // Transform into the Golem
		                npc.killerId = player.getIndex();
		                npc.getHealth().setMaximum(25);
		                npc.getHealth().reset();
		                npc.underAttack = true;
		                npc.underAttackBy = player.getIndex();
		                player.sendMessage("The rock springs to life! A Rock Golem attacks!");
		            }
		            container.stop();
		        }
		        @Override
		        public void stop() {}
		    }, 2);
		    break;
		case 922:
			player.getDH().sendDialogues(9200, npc.npcType);
			break;
		case 2881:
			player.getDH().sendDialogues(2700, npc.npcType);
			break;
		case 815://duke horacio
			player.getDH().sendDialogues(3200, npc.npcType);
			break;
		case 7414:
			player.getDH().sendDialogues(3600, npc.npcType);
			break;
		case 923:
			player.getDH().sendDialogues(9100, npc.npcType);
			break;
		case 2886://aubury
			player.getDH().sendDialogues(4000, npc.npcType);
			break;
		case 5034://aubury
			player.getDH().sendDialogues(4100, npc.npcType);
			break;
		case 2812:
			player.getDH().sendDialogues(8700, npc.npcType);
			break;
		case 2890:
			player.getDH().sendDialogues(9770, npc.npcType);
			break;
		case 2813:
		case 2814:
			player.getDH().sendDialogues(6000, npc.npcType);
			break;
		case 43: // Woolly Sheep
		    Shearing.shearSheep(player, npc.npcType, npc.getIndex()); // 'i' is the npc index
		    break;
		case 9487:// mage
			if (player.tutorialProgress == 32) {
				player.getDH().sendDialogues(3105, npc.npcType);
			}
			if (player.tutorialProgress == 33) {
				player.getDH().sendDialogues(3108, npc.npcType);
			}
			if (player.tutorialProgress == 34) {
				player.getDH().sendDialogues(3110, npc.npcType);
			}
			if (player.tutorialProgress == 35) {
				player.getDH().sendDialogues(3112, npc.npcType);
			}
			break;
		case 2147:
			if(player.getX() > 1821 && player.getX() < 1827 && 
					player.getY() > 3688 && player.getY() < 3691)
						player.getPA().movePlayer(1501, 3403, 0);
			break;
		case 1635:
		case 1636:
		case 1637:
		case 1638:
		case 1639:
		case 1640:
		case 1641:
		case 1642:
		case 1643:
		case 1654:
		case 7302:
			Impling.catchImpling(player, npc.npcType, player.rememberNpcIndex);
			break;
			case 3821:
				//StarSprite.startDialogue(player, 3821);
				break;
				
			case 3396:
				//StarterDungeonGuide.startDialogue(player, 3396);
				break;
				
			case 8045:
				//SirTiffy.startDialogue(player, 8045);
				break;
				
			case 279:
				//TownCrier.startDialogue(player, 279);
				break;
				
			case 9170:
				//CrystalWeaponElf.startDialogue(player, 9170);
				break;
				
			case 6020:
				//Scoop.startDialogue(player, 6020);
				break;

			case 1603:
				player.getDH().sendDialogues(150, npc.npcType);
				break;
				// inside your switch on npcId
			case 8728: // Ardougne Silk Merchant
				player.getDH().sendDialogues(200, npc.npcType);
			    //DialogueService.open(player, SilkMerchant.INSTANCE, 200); // start at 200
			    return;
			case CharlieTask.CHARLIE_NPC_ID: // Charlie the Tramp
		            if (CharlieTask.interact(player)) {
		                return; // The clue system handled it!
		            }
			    break;
			case 8480:
			case 8481:
				player.getDH().sendDialogues(8500, npc.npcType);
				break;
			case 1358:
				player.getDH().sendDialogues(8600, npc.npcType);
				break;
			case 4626:
				player.getDH().sendDialogues(50, npc.npcType);
				break;
			case 2873:
				player.getDH().sendDialogues(16, npc.npcType);
				break;
			case 3413:
				//Santa.startDialogue(player, 3413);
				break;
				
			case 7204:
				player.getDH().sendDialogues(357, npc.npcType);
				break;
				
			case 3819:
				player.getDH().sendDialogues(359, npc.npcType);
				break;
				
			case 7690:
			//	player.createInfernoInstance();
				//player.getInfernoMinigame().create(1);
				//player.getInfernoMinigame().getPlayer();
				break;
			case 8369: //Verzik vitur starting boss
				//player.getDH().sendDialogues(23682763, npc.npcType);
				npc.requestTransform(8370);
				break;
			case 3818:
				player.getDH().sendDialogues(354, 3585);
				break;
			case 1909:
				player.getDH().sendDialogues(900, 1909);
				break;
			case 2989:
				player.getDH().sendDialogues(1427, 2989);
				break;
			case 3306:
				player.getDH().sendDialogues(1577, -1);
				break;
			case 7520:
				player.getDH().sendDialogues(850, 7520);
				break;
			// Zeah Throw Aways
			case 3189:
				player.getDH().sendDialogues(11929, 3189);
				break;
			case 4062:
				player.getDH().sendDialogues(55875, 4062);
				break;
			case 4321:
				player.getDH().sendDialogues(145, 4321);
				break;
			case 7041:
				player.getDH().sendDialogues(500, 7041);
				break;
			case 5519:
				player.getDH().sendDialogues(5000, npc.npcType);
				break;
			case 8724://Ardougne Bakers
			case 8725:
				player.getDH().sendDialogues(5300, npc.npcType);
				break;
			case 822:
				if (player.getAD().hasWildernessSword1()) {
					player.getDH().sendDialogues(702, 822);
				} else {
					if (player.getItems().playerHasItem(11286) && player.getItems().playerHasItem(1540)
							&& player.getItems().playerHasItem(995, 5_000_000)) {
						player.getItems().deleteItem(11286, 1);
						player.getItems().deleteItem(1540, 1);
						player.getItems().deleteItem(995, 500_000);
						player.getItems().addItem(11283, 1);
						//player.votePoints -= 5;
						//player.refreshQuestTab(2);
						player.getDH().sendItemChat1("", "Oziach successfully bound your dragonfire shield.", 11283, 200);
					} else {
						player.getDH().sendStatement("Come back with a shield, visage and 5M Gold!");
					}
				}
				break;
			case 306:
				player.getDH().sendDialogues(8600, 306);
				break;
			case 3214:
				player.getDH().sendDialogues(9750, npc.npcType);
				break;
	
			case 2914:
				player.getDH().sendNpcChat2("Use a Zamorakian Spear on me to turn", "it into a Hasta! Or Vice Versa.", 2914,
						"Otto Godblessed");
				break;
	
	
			case 5036:
				if (player.getItems().playerHasItem(225) || player.getItems().playerHasItem(223)) {
					player.sendMessage("The Apothecary takes your ingredients and creates a strength potion.");
					player.getItems().deleteItem(225, 1);
					player.getItems().deleteItem(223, 1);
					player.getItems().addItem(115, 1);
					//player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.APOTHECARY_STRENGTH);
				} else {
					player.sendMessage("You must have limpwurt root and red spiders' eggs to do this.");
					return;
				}
				break;
	
			case 5906:
				//Probita.hasInvalidPet(player);
				break;
	
			case 3500:
				player.getDH().sendDialogues(64, npc.npcType);
				break;
	
			case 5870:
				//if (player.getCerberusLostItems().size() > 0) {
				//	player.getDH().sendDialogues(640, 5870);
				//	return;
				//}
				player.getDH().sendDialogues(105, npc.npcType);
				break;
	
			case 7283:
				//if (player.getSkotizoLostItems().size() > 0) {
				//	player.getDH().sendDialogues(640, 7283);
				//	return;
				//}
				player.getDH().sendDialogues(105, npc.npcType);
				break;
	
			//case 3307: // Combat instructor
				//player.getDH().sendDialogues(1390, npc.npcType);
				//break;
	
			case 5513: // Elite void knight
				player.getDH().sendDialogues(79, npc.npcType);
				break;

			case 1305:
				player.getDH().sendDialogues(1111, npc.npcType);
				break;
			case 5527: // Achievement cape
				//player.getAchievements().claimCape();
				break;
			case 1027:
				player.getDH().sendDialogues(669, npc.npcType);
				break;
			case 311:
				player.getDH().sendDialogues(650, npc.npcType);
				break;
	
			// Noting Npc At Skill Area
			case 905:
				player.talkingNpc = 905;
				player.getDH().sendStatement("Hello there, I can note your resources.",
						"I charge @red@25%@bla@ of the yield, this @red@does not apply to donators@bla@.",
						"Use any resource item obtained in this area on me.");
				player.nextChat = -1;
				break;
				//zulrah npcs
			case 2039:
				//ZulrahDismantler.startDialogue(player, 2039);
				break;
			case 2040:
				player.getDH().sendDialogues(637, npc.npcType);
				break;
				
			case 2184:
				player.getShops().openShop(29);
				break;
	
			case 6866:
				player.getShops().openShop(82);
			//	player.sendMessage("You currently have @red@" + player.getShayPoints() + " @bla@Assault Points!");
				break;
	
			case 6601:
				NPC golem = NPCHandler.npcs[player.rememberNpcIndex];
				if (golem != null) {
					//player.getMining().mine(golem, Mineral.RUNE,
					//		new Location(golem.getX(), golem.getY(), golem.getHeight()));
				}
				break;
			case 1850:
				player.getShops().openShop(112);
				break;
			case 2580:
				player.getDH().sendDialogues(629, npc.npcType);
				break;
			case 3894:
				player.getDH().sendDialogues(628, npc.npcType);
				break;
			case 3220:
				player.getShops().openShop(25);
				break;
			case 637:
				player.getShops().openShop(6);
				break;
				case 6875:
					//player.specRestore = 120;
					player.specAmount = 10.0;
					player.setRunEnergy(100);
					player.getItems().addSpecialBar(player.playerEquipment[player.playerWeapon]);
					player.getSkills().resetToActualLevel(Skill.PRAYER);
					player.getHealth().removeAllStatuses();
					player.getHealth().reset();
					player.getPA().refreshSkill(5);
					player.getDH().sendItemChat1("", "Restored your HP, Prayer, Run Energy, and Spec", 4049, 200);
					player.nextChat =  -1;
					break;
				case 732:
					player.getDH().sendDialogues(9600, npc.npcType);
				break;
				case 4397:
					player.getDH().sendDialogues(7000, npc.npcType);
					break;
			case 3219:
				player.getShops().openShop(113);
				break;
			case 2949:
				//player.getPestControlRewards().showInterface();
				break;
			case 7663:
				player.getDH().sendDialogues(3299, npc.npcType);
				break;
			case 402:// slayer
				if(player.combatLevel<20){
					player.getDH().sendNpcChat2("Do not waste my time peasent.","You need a Combat level of 20.",402,"Mazchna");
					return;
				}
				player.getDH().sendDialogues(3300, npc.npcType);
				break;
			case 401:
				player.getDH().sendDialogues(3300, npc.npcType);
				break;
			case 405:
				if(player.combatLevel<100){
					player.getDH().sendNpcChat2("Do not waste my time peasent.","You need a Combat level of at least 100.",402,"Duradel");
					return;
				}
				if (player.getSkills().getLevel(Skill.SLAYER) < 50) {
					player.getDH().sendNpcChat1("You must have a slayer level of at least 50 weakling.", 490, "Duradel");
					return;
				}
				player.getDH().sendDialogues(3300, npc.npcType);
				break;
			case 6797: // Nieve
				if (player.getSkills().getLevel(Skill.SLAYER) < 90) {
					player.getDH().sendNpcChat1("You must have a slayer level of at least 90 weakling.", 490, "Nieve");
					return;
				} else {
					player.getDH().sendDialogues(3300, npc.npcType);
				}
				break;
			case 315:
				player.getDH().sendDialogues(550, npc.npcType);
				break;
	
			case 1308:
				player.getDH().sendDialogues(538, npc.npcType);
				break;
			case 7456:
				player.getDH().sendDialogues(619, npc.npcType);
				break;
			case 4306:
				player.getShops().openShop(17);
				break;
			case 3341:
				player.getDH().sendDialogues(2603, npc.npcType);
				break;
			case 5919:
				player.getDH().sendDialogues(14400, npc.npcType);
				break;
			case 6599:
				player.getShops().openShop(12);
				break;
			case 2578:
				player.getDH().sendDialogues(2401, npc.npcType);
				break;
			case 3789:
				player.getShops().openShop(75);
				break;
			
	
			case 559:
				player.getShops().openShop(16);
				break;
			case 5809:
				//Tanning.sendTanningInterface(player);
				break;
	
			case 2913:
				player.getShops().openShop(22);
				break;
			case 403:
				player.getDH().sendDialogues(2300, npc.npcType);
				break;
			case 1599:
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
			case 567: // Banker
			case 766: // Banker
			case 1036: // Banker
			case 1360: // Banker
			case 2163: // Banker
			case 2164: // Banker
			case 2354: // Banker
			case 2355: // Banker
			case 2568: // Banker
			case 2569: // Banker
			case 2570: // Banker
			case 1613:
			case 1618:
				player.getDH().sendDialogues(1013, player.npcType);
				break;
			case 1986:
				player.getDH().sendDialogues(2244, player.npcType);
				break;
	
			case 5792:
				player.getDH().sendDialogues(4005, player.npcType);
				// player.getShops().openShop(9);
				break;
			case 6747:
				player.getShops().openShop(77);
				break;
			case 3218:// magic supplies
				player.getShops().openShop(6);
				break;
			case 4423:
				player.getShops().openShop(122);
				player.sendMessage("@red@ You must fill the books with pages found in clue scrolls or purchased from players.");
				break;
			case 8278:// range supplies
				player.getShops().openShop(4);
				break;
			case 8280://master capes
				player.getShops().openShop(121);
				break;
			case 1785:
				player.getShops().openShop(8);
				break;
	
			case 1860:
				player.getShops().openShop(47);
				break;
	
			case 519:
				player.getShops().openShop(48);
				break;
	
			case 548:
				player.getDH().sendDialogues(69, player.npcType);
				break;
	
			case 2258:
				player.getDH().sendOption2("Teleport me to Runecrafting Abyss.", "I want to stay here, thanks.");
				player.dialogueAction = 2258;
				break;
	
			case 532:
				player.getShops().openShop(47);
				break;
	
			case 3216:// melee supplies
				player.getShops().openShop(8);
				break;
	
	
			
		}
	}
	
}