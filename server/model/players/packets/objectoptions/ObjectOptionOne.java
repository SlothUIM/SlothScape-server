package server.model.players.packets.objectoptions;

import java.util.Objects;
import java.util.stream.IntStream;

import server.Config;
import server.clip.ObjectDef;
import server.clip.WorldObject;
import server.clip.doors.DoorDefinition;
import server.clip.doors.DoorHandler;
import server.clip.doors.DoubleDoorDefinition;
import server.clip.doors.GateHandler;
import server.clip.ladders.LadderHandler;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.model.content.STASH;
import server.model.content.StashUnit;
import server.model.instance.impl.HydraInstance;
import server.model.instance.impl.VoidChampionInstance;
import server.model.minigames.cox.ChambersOfXeric;
import server.model.minigames.cox.CoxObjectHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.npcs.bosses.EventBoss.EventBossChest;
import server.model.npcs.bosses.cerberus.Cerberus;
import server.model.npcs.combat.impl.custombosses.drops.NightmareDrops;
import server.model.npcs.combat.impl.eventboss.drop.EnragedGraardorDrops;
import server.model.objects.Object;
//import server.model.npcs.drops.DropManager;
import server.model.players.quests.*;
import server.model.players.skills.SkillAnims;
import server.model.players.skills.hunter.trap.impl.Pitfall;
import server.world.Boundary;
import server.world.ItemHandler;
import server.model.players.Player;
import server.model.players.Right;
import server.model.players.Sound;
import server.model.players.WildernessDitch;
import server.model.players.combat.Hitmark;
import server.model.players.content.Bookcase;
import server.model.players.content.Crates;
import server.model.players.content.CropPicking;
import server.model.players.content.treasuretrails.types.SearchClues;
//import server.model.players.skills.FlaxPicking;
import server.model.players.skills.Skill;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.farming.Farming;
import server.model.players.skills.hunter.Hunter;
import server.model.players.skills.hunter.impling.PuroPuro;
import server.model.players.skills.mining.Mining;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.model.players.skills.mining.motherlode.OreTile;
import server.model.players.skills.woodcutting.Tree;
import server.model.players.skills.woodcutting.Woodcutting;
import server.model.items.EquipmentSet;
import server.model.items.GlobalDrop;
import server.model.items.ItemAssistant;
import server.model.minigames.NMZRewards;
import server.model.minigames.NightmareZone;
import server.model.minigames.RangingGuild;
import server.model.minigames.rfd.DisposeTypes;
import server.model.minigames.warriors_guild.AnimatedArmour;
import server.model.lobby.LobbyManager;
import server.model.lobby.LobbyType;
import server.model.minigames.theatre.TheatreObjects;
import server.model.minigames.trawler.FishingTrawlerLobby;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.multiplayer_session.duel.DuelSessionRules.Rule;
import server.model.players.packets.objectoptions.impl.DarkAltar;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

/*
 * @author Matt
 * Handles all first options for objects.
 */

public class ObjectOptionOne {

	static int[] barType = { 2363, 2361, 2359, 2353, 2351, 2349 };
	public static void handleObjectPickup(Player c, int objectId, int x, int y) {
	    for (GlobalDrop gd : ItemHandler.globalDrops) {
	        if (gd.isObject && gd.objectId == objectId && gd.x == x && gd.y == y && !gd.taken) {
	            if (c.getItems().addItem(gd.itemId, gd.itemAmount)) {
	                gd.taken = true;
	                gd.currentTicks = gd.respawnTicks;
	                
	                // Swap the object to the "Empty" version using your GlobalObject system
	                GlobalObject empty = new GlobalObject(gd.restoreObjectId, gd.x, gd.y, gd.z, 0, 10, gd.respawnTicks, gd.objectId);
	                World.getWorld().getGlobalObjects().add(empty);
	                
	                c.sendMessage("You take the " + c.getItems().getItemName(gd.itemId) + ".");
	            }
	            return;
	        }
	    }
	}
	public static void handleOption(final Player c, WorldObject worldObject, int face) {
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		int objectId = worldObject.getId();
		int objectX = worldObject.getX();
		int objectY = worldObject.getY();
		c.getPA().resetVariables();
		c.clickObjectType = 0;
		c.turnPlayerTo(objectX, objectY);
		//c.getFarming().patchObjectInteraction(objectId, -1, objectX, objectY);
		//c.boneOnAltar = false;
		Tree tree = Tree.forObject(objectId);

		//RaidObjects.clickObject1(c, objectId, objectX, objectY);
		/*if(FallingStars.attemptMine(c, objectId, new Location(objectX, objectY, c.getHeight())))
			return;*/
		if (tree != null) {
		Woodcutting.getInstance().chop(c, objectId, objectX, objectY);
			return;
		}
		/*if (World.getWorld().getHolidayController().clickObject(c, 1, objectId, objectX, objectY)) {
			return;
		}*/
		NightmareZone.activatePowerUp(c, objectId, objectX, objectY);
		/*if (c.getGnomeAgility().gnomeCourse(c, objectId)) {
			return;
		}
		if (c.getWildernessAgility().wildernessCourse(c, objectId)) {
			return;
		}*/
		if (c.isInCoXRaid()) {
			if (CoxObjectHandler.handleObjectClick(c, objectId, objectX, objectY)) {
				return; // Handled by the raid engine
			}
		}
		if (LadderHandler.handleLadder(c, objectId, objectX, objectY)) {
		    return; // Exit if a ladder was handled
		}
		if(c.getAD().DiaryObjectClick(objectId, 1)) {
			//continue;
		}
		if (c.getBarbarianAgility().barbarianCourse(c, objectId)) {
			return;
		}
		if (c.getBarbarianAgility().barbarianCourse(c, objectId)) {
			return;
		}
		if (c.getAgilityShortcuts().agilityShortcuts(c, objectId)) {
			return;
		}
		if (c.getRoofTopSeers().execute(c, objectId)) {
			return;
		}
		if (c.getRoofTopFalador().execute(c, objectId)) {
			return;
		}
		if (c.getRoofTopVarrock().execute(c, objectId)) {
			return;
		}
		if (c.getRoofTopDraynor().execute(c, objectId)) {
			return;
		}
		if (c.getRoofTopArdougne().execute(c, objectId)) {
			return;
		}
		if (c.getRoofTopCanifis().execute(c, objectId)) {
			return;
		}
		if (c.getLighthouse().execute(c, objectId)) {
			return;
		}//26711
		if (objectId >= 19253 && objectId <= 19332) {
			Pitfall.jumpTrap(c, objectId, objectX, objectY);
			return;
		}
		ObjectDef def = ObjectDef.forID(objectId);
		if ((def != null ? def.name : null) != null && objectId != 10083 && def.name.toLowerCase().contains("bank") && !def.name.toLowerCase().contains("deposit box")) {
			c.getPA().openUpBank();
			return;
		}
		final int[] HUNTER_OBJECTS = new int[] { 9373, 9377, 9379, 9375, 9348, 9380, 9385, 9344, 9345, 9383, 721, 9382 };
		if (IntStream.of(HUNTER_OBJECTS).anyMatch(id -> objectId == id)) {
			GlobalObject object = new GlobalObject(objectId, objectX, objectY, c.getHeight());
			if (Hunter.pickup(c, object)) {
				return;
			}
			if (Hunter.claim(c, object)) {
				return;
			}
		}
		if(def.name != null && (def.name.equalsIgnoreCase("crate") ||  def.name.equalsIgnoreCase("boxes")))
			new Crates(objectId).startSearch(c);
		//c.getMining().mine(objectId, new Location(objectX, objectY, c.getHeight()));
		//Obelisks.get().activate(c, objectId);
		//Runecrafting.execute(c, objectId);
		if (objectId >= 26281 && objectId <= 26290) {
			//HalloweenRandomOrder.chooseOrder(c, objectId);
		}
		DoubleDoorDefinition ddd = DoorHandler.findDoubleDoorAt(worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getId());

		if (ddd != null) {
		    if (DoorHandler.clickDoor(c, null, worldObject)) { 
		    	return;
		    }
		}

		DoorDefinition door = DoorHandler.findDoorAt(worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getId());

		if (door != null) {
		    if (DoorHandler.clickDoor(c, door, worldObject)) {
		        return;
		    }
		}
		WorldObject clickedHalf = new WorldObject(objectId, objectX, objectY, c.getHeight(), 0, worldObject.getFace());
		if (GateHandler.clickGate(c, clickedHalf)) {
		    return;
		}
		if (c.getRaidsInstance() != null && c.getRaidsInstance().handleObjectClick(c, worldObject)) {
			return;
		}
		if (c.getTheatreInstance() != null && Boundary.isIn(c, Boundary.THEATRE)) {
			TheatreObjects.handleObjectClick(c, objectId);
		}
		if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
			if (Farming.harvest(c, objectX, objectY)) {
				return;
			}
			if (Farming.prepareCrop(c, 5341, objectId, objectX, objectY)) {
				return;
			}

			if (c.getCompost().handleObjectClick(objectId, objectX, objectY)) {
				return;
			}
		}
		if(c.getSecurity().handleStrongholdDoors(c, objectId)) {
			return;
		}
		if (SearchClues.handleObjectSearch(c, objectId, objectX, objectY)) {
            return; // Stops normal object clicking if a clue was successfully searched
        }

		if(CropPicking.pickCrop(c, objectId, objectX, objectY, worldObject.type, worldObject.face))
			return;
		if (Mining.rockExists(objectId))
            c.getMining().startMining(c, objectId, objectX, objectY, c.clickObjectType);
		if (SearchClues.handleObjectSearch(c, objectId, objectX, objectY)) {
            return; // The clue system handled it, stop running standard object code!
        }
		if (StashUnit.forObjectId(objectId) != null) {
		    if (!c.stashBuilt[StashUnit.forObjectId(objectId).ordinal()]) {
		        STASH.buildStash(c, objectId);
		    } else {
		        STASH.interactStash(c, objectId);
		    }
		    return; // Stops normal object clicking
		}
		switch (objectId) {
			case 29776: // Raiding Parties Recruitment Board
				// 1. Refresh the board with the live global list of advertised parties
				server.model.minigames.cox.CoxPartyManager.refreshRecruitmentBoard(c);

				// 2. Open the Recruitment Board interface
				c.getPA().showInterface(52000);
				break;

			case 32544: // Chambers of Xeric Scoreboard
				// 1. Hide all bracket highlights by default
				for (int i = 0; i < 13; i++) {
					c.getPA().setInterfaceVisible(53305 + (i * 2), true);
				}

				// 2. Unhide the "Solo" highlight as the default selection
				c.getPA().setInterfaceVisible(53305, false);

				// 3. TODO: Load the top 5 times from your database/save file and sendFrame126 them to the 53160 block

				// 4. Open the Challenge Mode Board interface
				c.getPA().showInterface(53150);
				break;
		case 4977: // Port Khazard Gangplank entering ship
		case 4978: // Port Khazard Gangplank leaving ship
		    if (c.getSkills().getActualLevel(Skill.FISHING) < 15) {
		        c.sendMessage("You need a Fishing level of at least 15 to board the trawler.");
		        break; // Stop them from boarding!
		    }
		    
		    // Check if they are on the dock (X > 2674) boarding the boat
		    if (c.getX() > 2674) {
		        // Move them onto the actual boat deck at Port Khazard
		        c.getPA().movePlayer(2672, 3170, 1); 
		        
		        // Add them to the Lobby manager
		        FishingTrawlerLobby.joinLobby(c);
		    } 
		    // If they are already on the boat and want to leave before it starts
		    else {
		        // Move them back to the dock
		        c.getPA().movePlayer(2676, 3170, 0);
		        
		        // Remove them from the Lobby manager
		        FishingTrawlerLobby.leaveLobby(c);
		        c.sendMessage("You step off the boat.");
		    }
		    break;
		case 26273:
            NMZRewards.refreshResourcesTab(c);
            c.getPA().showInterface(25533);
			break;
		case 26292:
			c.getDH().sendDialogues(6754, -1);
			break;
		case 26291:
			NightmareZone.startRumble(c);
			break;
		case 11663:
			RangingGuild.fireAtTarget(c, objectX, objectY);
		    break;
		case 26277: // Super Ranging Barrel
        case 26278: // Super Magic Barrel
        case 26279: // Overload Barrel
        case 26280: // Absorption Barrel
            NMZRewards.handleBarrel(c, objectId, 1);
            break;
		case 2402:
		case 380:
		case 26113:
		case 13597:
		    new Bookcase(objectId).startSearch(c);
		    break;
		case 26115: // Odd looking wall
		    // Check if player is on the stage where they have the key
		    if(c.questStages[EleWorkShop.QUEST_ID] >= EleWorkShop.FOUND_KEY && c.getItems().playerHasItem(2887, 1)) {
		        if(c.getY() == objectY) {
		            c.getPA().object(-1, objectX, objectY, 0, 0);
		            c.getPA().object(objectId, objectX, objectY+1, 2, 0);
		            c.getPA().walkTo(0, 1);
		            
		            // Advance stage if they are just entering for the first time
		            if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.FOUND_KEY) {
		                c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.ENTERED_WORKSHOP;
		            }

		            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		                @Override
		                public void execute(CycleEventContainer container) { container.stop(); }
		                @Override
		                public void stop() {
		                    c.getPA().object(-1, objectX, objectY+1, 0, 0);
		                    c.getPA().object(objectId, objectX, objectY, 1, 0);
		                }
		            }, 2);
		        } else if(c.getY() == objectY+1) {
		            c.getPA().object(-1, objectX, objectY, 0, 0);
		            c.getPA().object(objectId, objectX, objectY+1, 2, 0);
		            c.getPA().walkTo(0, -1);
		            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		                @Override
		                public void execute(CycleEventContainer container) { container.stop(); }
		                @Override
		                public void stop() {
		                    c.getPA().object(-1, objectX, objectY+1, 0, 0);
		                    c.getPA().object(objectId, objectX, objectY, 1, 0);
		                }
		            }, 2);
		        }
		    } else {
		        c.sendMessage("The wall has a strange, needle-like keyhole.");
		    }
		    break;

		case 3415://stairs down to the work shop
			AgilityHandler.delayEmote(c, "", 2716, 9888, 0, 1);
			c.getDH().sendDialogues(3551, 0);
		break;
		case 3403: // Water Valve 1 (East)
		   
		    if (c.questStages[EleWorkShop.QUEST_ID] < EleWorkShop.ENTERED_WORKSHOP) {
		        c.sendMessage("I should find a way into the workshop before messing with these.");
		        return;
		    }
		    if (c.EleWorkshopWater_stage == 0) {
		        c.getPA().sendConfig(299, 1 << 4);
		        c.EleWorkshopWater_stage = 1; // Stage 1: First valve turned
		        c.sendMessage("You turn the handle to the left. You hear water beginning to flow.");
		    } else {
		        c.getPA().sendConfig(299, 0);
		        c.EleWorkshopWater_stage = 0;
		        c.sendMessage("You turn the handle back to the right.");
		        // If they reset the valve, we should reset the quest stage back to 3 if they were on 4
		        if (c.questStages[EleWorkShop.QUEST_ID] == 4) {
		            c.questStages[EleWorkShop.QUEST_ID] = 3;
		        }
		    }
		    break;
		
		case 3404: // Water Valve 2 (West)
		    int stage3404 = c.questStages[EleWorkShop.QUEST_ID];
		    
		    if (c.EleWorkshopWater_stage < 1) {
		        c.sendMessage("This valve won't budge. Maybe I should turn the other one first?");
		        return;
		    }

		    if (c.EleWorkshopWater_stage == 1) {
		        c.getPA().sendConfig(299, 3 << 3);
		        c.EleWorkshopWater_stage = 2; // Stage 2: Both valves turned
		        
		        // Progress quest to Stage 4: Ready to pull the wheel lever
		        if (stage3404 == 3) {
		            c.questStages[EleWorkShop.QUEST_ID] = 4;
		        }
		        c.sendMessage("You turn the handle to the left. The water pressure increases.");
		    } else {
		        c.getPA().sendConfig(299, 1 << 4);
		        c.EleWorkshopWater_stage = 1;
		        c.sendMessage("You turn the handle back to the right.");
		        if (c.questStages[EleWorkShop.QUEST_ID] == 4) {
		            c.questStages[EleWorkShop.QUEST_ID] = 3;
		        }
		    }
		    break;

		case 3406: // Water Lever
			if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.VALVES_TURNED) {
		        c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.WHEEL_SPINNING;
		        c.getPA().sendConfig(299, 7 << 3); 
		        c.sendMessage("You pull the lever and the water wheel starts spinning.");
		    }
		    break;

		case 3407: // The Bellows (Clicking)
		    int stage3407 = c.questStages[EleWorkShop.QUEST_ID];
		    
		    if (stage3407 < 5) {
		        c.sendMessage("The wheel needs to be spinning before you can fix the bellows.");
		        return;
		    }
		    
		    if (stage3407 >= 6) {
		        c.sendMessage("The bellows appear to be in good working order.");
		        return;
		    }

		    // Check for Leather (1741), Needle (1733), and Thread (1734)
		    if (c.getItems().playerHasItem(1741) && c.getItems().playerHasItem(1733) && c.getItems().playerHasItem(1734)) {
		        c.getPA().sendConfig(299, 55 << 3); // Visual update
		        c.getItems().deleteItem(1741, 1); // Delete leather
		        c.getItems().deleteItem(1734, 1); // Delete 1 thread
		        
		        c.questStages[EleWorkShop.QUEST_ID] = 6; // Move to 'Fixed, pull lever' stage
		        c.sendMessage("You stitch the leather over the hole in the bellows.");
		    } else {
		        c.sendMessage("There appears to be a hole in the bellows. I'll need leather, a needle and some thread.");
		    }
		    break;

		case 3409: // Bellows Lever
			if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.BELLOWS_REPAIRED) {
		        c.getPA().sendConfig(299, 199 << 3); 
		        c.sendMessage("The bellows pump air down the pipe.");
		        c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.BELLOWS_PUMPING;
		    }
		    break;
		case 3408: // Elemental Ore Rock
		    // Requirement Check
			if (c.questStages[EleWorkShop.QUEST_ID] < EleWorkShop.FURNACE_HEATED) { 
		        c.sendMessage("I should get the furnace ready before I mine this ore.");
		        return;
		    }
		    if (c.getSkills().getActualLevel(Skill.MINING) < 20) {
		        c.sendMessage("You need a Mining level of 20 to mine this ore.");
		        return;
		    }
		    
		    c.startAnimation(625); // Standard Mining Animation
		    c.sendMessage("You swing your pick at the rock...");

		    // OSRS Wiki: "The rock springs to life, yells, and attacks you."
		    if (Misc.random(4) == 0) { // 25% chance to spawn Golem
		        // NPC ID 160 is the Rock Golem
		        World.getWorld().npcHandler.spawnNpc(c, 160, c.getX(), c.getY(), c.HeightLevel, 1, 50, 2, 10, 10, true, false);
		        c.sendMessage("The rock springs to life! A Rock Golem attacks!");
		    } else {
		        // Successful Mining without a fight
		        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            @Override
		            public void execute(CycleEventContainer container) {
		                if (c.getItems().freeSlots() > 0) {
		                    c.getItems().addItem(2892, 1); // Elemental Ore
		                    c.getPA().addSkillXP(50, Skill.MINING.getId());
		                    c.sendMessage("You manage to mine some elemental ore.");
		                } else {
		                    c.sendMessage("You don't have enough inventory space.");
		                }
		                container.stop();
		            }
		            @Override
		            public void stop() {
		            	if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.FURNACE_HEATED) {
		                    c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.OBTAINED_ORE;
		                }
		            }
		        }, 3);
		    }
		    break;
		case 15042:
			handleObjectPickup(c, 15042, objectX, objectY);
			break;
		case 25818:
			if(c.MusicVolume > 0) {
				if(Misc.random(1) == 0)
					c.getMusic().playSong(c, 40, true, false);
				else
					c.getMusic().playSong(c, 39, true, false);
				c.getAD().completeAchievement("KandarinEasy", "Play the Church organ in the Seers' Village church", 4);
			} 
			
			break;
		case 2145:
		    // 1. Swap the coffin to the "Open (Without Skull)" state
		    GlobalObject coffin = new GlobalObject(15052, objectX, objectY, c.getHeight(), worldObject.face, 10, -1, 2145);
		    World.getWorld().getGlobalObjects().add(coffin);
		    
		    if (c.questStages[RestlessGhost.QUEST_ID] >= RestlessGhost.SPOKE_TO_AERECK && c.questStages[RestlessGhost.QUEST_ID] < RestlessGhost.COMPLETED) {
		        
		        final int spawnX = objectX;
		        final int spawnY = objectY - 1;
		        
		        int offX = (spawnY - objectY) * -1;
		        int offY = (spawnX - objectX) * -1;
		        
		        // Fire the projectile (Using 106 for the large air wave!)
		        c.getPA().createPlayersProjectile(objectX, objectY, offX, offY, 50, 75, 106, 20, 31, 0, 50, 0);
		        
		        // Submit the event to Jason's EventHandler
		        World.getWorld().getEventHandler().submit(new Event<Player>("restless_ghost_spawn", c, 2) {
		            @Override
		            public void execute() {
		                // Spawn the NPC
		                NPCHandler.spawn(922, spawnX, spawnY, c.getHeight(), 1, 0, 0, 0, 0, false);
		                super.stop(); 
		            }
		        });
		    } else if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.COMPLETED) {
		        GlobalObject completedCoffin = new GlobalObject(15053, objectX, objectY, c.getHeight(), worldObject.face, 10, -1, 2145);
		        World.getWorld().getGlobalObjects().add(completedCoffin);
		    }
			break;
		case 2146:
        case 15050: 
        case 15051:
            if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.SPOKE_TO_GHOST) {
                if (c.getItems().freeSlots() == 0) {
                    c.sendMessage("You don't have enough inventory space to take the skull.");
                    return;
                }
                
                c.getItems().addItem(RestlessGhost.GHOSTS_SKULL, 1);
                c.sendMessage("You find a skull on the altar.");
                
                // This updates the Varp, switching the altar to empty & despawning the skeleton!
                new RestlessGhost(c).setStage(RestlessGhost.GOT_SKULL);

                c.getDH().sendStatement("The skeleton in the corner suddenly comes to life!");
                c.nextChat = 0;

                NPCHandler.spawn(924, 3113, 3161, c.getHeight(), 1, 15, 2, 10, 10, true);
            } else if (c.questStages[RestlessGhost.QUEST_ID] >= RestlessGhost.GOT_SKULL) {
                c.sendMessage("There is nothing else on the altar.");
            } else {
                c.sendMessage("You search the altar but find nothing of interest.");
            }
            break;
		case 375:
		    GlobalObject chest = new GlobalObject(378, objectX, objectY, c.getHeight(), worldObject.face, 10, -1);
		    World.getWorld().getGlobalObjects().add(chest);
			break;
		case 881:
		    c.stopMovement();
		    GlobalObject openManhole = new GlobalObject(882, objectX, objectY, c.getHeight(), worldObject.face, 10, -1);
		    World.getWorld().getGlobalObjects().add(openManhole);
		    break;

		case 882:
			c.stopMovement();
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3237, 9858, 0, 1);
			break;
		case 11806:
			c.turnPlayerTo(objectX+1, objectY);
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3237, 9859-6400, 0, 1);
			break;
		case 24964: // Hopper Controls / Lever
		    if (c.millHopperType == 0) {
		        c.getDH().sendStatement("The hopper is empty.");
		        break;
		    }
		    
		    if (c.flourBinAmount >= 30) {
		        c.getDH().sendStatement("The flour bin is full. You should empty it first.");
		        break;
		    }

		    c.startAnimation(832); // Lever pulling animation
		    c.sendMessage("You operate the hopper controls.");
		    
		    // Process hopper to bin
		    c.flourBinAmount++;
		    c.millHopperType = 0; 
		    c.sendMessage("The grain is ground into flour and falls into the bin below.");
		    break;
		case 5792: // Flour Bin
			if(c.getItems().playerHasItem(1931)) {
			    if (c.flourBinAmount <= 0) {
			        c.getDH().sendStatement("The bin is empty.");
			        return;
			    }
	
			    if (!c.getItems().playerHasItem(1931)) { // Empty Pot
			        c.getDH().sendStatement("You need an empty pot to collect the flour.");
			        return;
			    }
	
			    // Collection loop (optional: you can make it collect all if they have pots)
			    c.getItems().deleteItem(1931, 1);
			    c.getItems().addItem(1933, 1); // Pot of Flour
			    c.flourBinAmount--;
			    c.startAnimation(832);
			    c.sendMessage("You fill a pot with flour from the bin.");
			}
		    break;
		case 24077:
			if(objectX == 3018 && objectY == 3343)
				c.getPA().movePlayer(3021, 3344, 1);
			break;
		case 24078:
			if(objectX == 3019 && objectY == 3343)
				c.getPA().movePlayer(3017, 3344, 0);
			break;
		case 11798:
			if(objectX == 3266 && objectY == 3452)
				c.getPA().movePlayer(3266, 3455, 1);
			break;
		case 16664:
			if (c.getX() == 3061 && (c.getY() == 3376 || c.getY() == 3377))
				AgilityHandler.delayEmote(c, "", 3058, c.getY()+6400, 0, 2);
			if ((c.getX() == 2569 || c.getX() == 2570) && c.getY() == 3121)
				AgilityHandler.delayEmote(c, "", c.getX(), c.getY()+6404, 0, 2);
			if (c.getX() == 2606 && (c.getY() == 3078 || c.getY() == 3079))
				AgilityHandler.delayEmote(c, "", 2602, c.getY()+6400, 0, 2);
			break;
		case 16665:
			if (c.getX() == 2602 && (c.getY() == 9478 || c.getY() == 9479))
				AgilityHandler.delayEmote(c, "", 2606, c.getY()-6400, 0, 2);
			if (c.getX() == 3061 && (c.getY() == 3376 || c.getY() == 3377))
				AgilityHandler.delayEmote(c, "", 3058, c.getY()+6400, 0, 2);
			if ((c.getX() == 2569 || c.getX() == 2570) && c.getY() == 9525)
				AgilityHandler.delayEmote(c, "", 2569, c.getY()-6404, 0, 2);
			break;

		case 412:
			if(c.getX() == 2572 && c.getY() == 9499) {
				c.sendMessage("The Altar releases a poison gas!");
				c.getPA().appendPoison(3);
				c.lastPoison = 5000;
                c.getPA().movePlayer(2597, 9568); // if this is the correct pit location
			}
			
			break;
		case 20056:
			if(c.questStages[WatchTower.QUEST_ID] == 0) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2548, 3118, 1, 1);
			}
			break;
		case 14889:
		case 2644: // Lumbridge Spinning Wheel
		case 4309: // Seers Spinning Wheel
		    c.getCrafting().getSpinning().openSpinningInterface();
		    break;
		case 2084: // Karamja Gangplank
            if (c.getItems().playerHasItem(PiratesTreasure.KARAMJAN_RUM)) {
                // Delete all rum they have!
                int rumAmount = c.getItems().getItemAmount(PiratesTreasure.KARAMJAN_RUM);
                for (int i = 0; i < rumAmount; i++) {
                    c.getItems().deleteItem(PiratesTreasure.KARAMJAN_RUM, 1);
                }
                c.getDH().sendStatement("The Customs Officer finds Karamjan Rum in your bags and confiscates it!");
                c.nextChat = 0;
                return;
            }
             c.getPA().movePlayer(3029, 3217, 0); // Port Sarim docks
            break;
		case 2072: // Luthas' Crate (Karamja)
            if (c.bananasInCrate < 10) {
                if (c.getItems().playerHasItem(1963)) { // Banana ID
                    c.startAnimation(832);
                    c.getItems().deleteItem(1963, 1);
                    c.bananasInCrate++;
                    if (c.bananasInCrate == 10) {
                        c.sendMessage("You pack the last banana. The crate is now full.");
                    } else {
                        c.sendMessage("You pack a banana into the crate. (" + c.bananasInCrate + "/10)");
                    }
                } else {
                    c.sendMessage("The crate is partially filled with bananas. You need " + (10 - c.bananasInCrate) + " more.");
                }
            } else {
                c.sendMessage("The crate is completely full of bananas. You should talk to Luthas.");
            }
            break;
		case 2073: // Banana Tree (With Bananas)
            if (c.getItems().freeSlots() > 0) {
                c.startAnimation(832); // Standard picking animation
                c.getItems().addItem(1963, 1); // Gives 1 Banana
                if(c.bananasPickedDiary < 5)
                	c.bananasPickedDiary += 1;
                if(c.bananasPickedDiary == 5)
                	c.getAD().completeAchievement("KaramjaEasy", "Pick 5 bananas from the plantation located east of the volcano", 0);
                c.sendMessage("You pick a banana from the tree.");
                
                // Optional: If your server has a GlobalObject system to handle object respawning, 
                // you can temporarily turn the tree into an empty tree (ID 2074) here!
                 Example: World.getWorld().getGlobalObjects().add(new GlobalObject(2074, c.objectX, c.objectY, c.HeightLevel, 0, 10, 20, 2073));
            } else {
                c.sendMessage("You don't have enough inventory space to pick a banana.");
            }
            break;
        case 2071: // Wydin's Crate (Port Sarim Backroom)
            if (c.rumInCrate && c.bananasInCrate >= 10) { // It shipped successfully!
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(server.model.players.quests.PiratesTreasure.KARAMJAN_RUM, 1);
                    c.rumInCrate = false;
                    c.bananasInCrate = 0; // Reset the puzzle
                    c.sendMessage("You rummage through the bananas and find your stashed rum!");
                } else {
                    c.sendMessage("You don't have enough inventory space to grab the rum.");
                }
            } else {
                c.sendMessage("You search the crate but find nothing of interest.");
            }
            break;
		case 2614: // Count Draynor's Coffin
            if (c.questStages[VampyreSlayer.QUEST_ID] == VampyreSlayer.GATHERING_EQUIPMENT) {
                // Prevent spawning him 50 times if they spam click the coffin
                if (World.getWorld().npcHandler.getNpc(34, c.absX, c.absY, c.HeightLevel) != null) {
                    c.sendMessage("Count Draynor is already out of his coffin!");
                    return;
                }

                // Spawn Count Draynor (NPC 34) next to the coffin
                NPC count = World.getWorld().npcHandler.spawnNpc(c, 34, c.absX, c.absY - 1, c.HeightLevel, 1, 35, 4, 30, 30, true, true);
                
                // The Garlic Effect!
                if (c.getItems().playerHasItem(VampyreSlayer.GARLIC)) {
                    c.sendMessage("The vampyre seems to weaken.");
                    
                    // Deduct 10 HP instantly
                    count.getHealth().setAmount(25); // Max is 35, so 35 - 10 = 25
                    
                    // Heavily nerf his stats just like OSRS
                    count.maxHit = 1;
                    count.attack = 5;
                    count.defence = 5;
                } else {
                    c.sendMessage("Count Draynor awakens!");
                }
            } else if (c.questStages[VampyreSlayer.QUEST_ID] == VampyreSlayer.COMPLETED) {
                c.sendMessage("The vampyre is dead. He will not be bothering anyone anymore.");
            } else {
                c.sendMessage("You have no reason to open this coffin.");
            }
            break;
		case 18772:
			c.getFancyBox().dressBoxTier = 1;
			c.getFancyDressBox().open(c);
			break;
		case 18774:
			c.getFancyBox().dressBoxTier = 2;
			c.getFancyDressBox().open(c);
			break;
		case 18776:
			c.getFancyBox().dressBoxTier = 3;
			c.getFancyDressBox().open(c);
			break;
		case 18782:
			c.getFancyBox().dressBoxTier = 3;
			c.getArmourCase().open(c);
			break;
		case 733:// slash webs
			for(int io = 0; io < Config.CUT_ITEMS.length; io++) {
				if(c.getItems().playerHasItem(Config.CUT_ITEMS[io]) || ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).contains("sword")
						 || ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).contains("axe") || ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).contains("scimitar")
						 || ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).contains("dagger") || ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).contains("Arclight")) {
					int chance = c.getRechargeItems().hasAnyItem(13108, 13109, 13110, 13111) ? 0 : 1;
					c.startAnimation(390);
					c.getPA().sendSound(Sound.SOUND_LIST.SLASH_WEB.getSound(), 0, 6, c.EffectVolume);
					if (Misc.random(chance) == 0) {
							c.sendMessage("You slash the web apart.");
						    GlobalObject go2 = new GlobalObject(734, worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getFace(), 10, 20, 733);
					        World.getWorld().getGlobalObjects().add(go2);
							/*CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
								@Override
								public void execute(CycleEventContainer container) {
									container.stop();
								}

								@Override
								public void stop() {
									new Object(733, worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getFace() , 10, 734, -1);
								}
							}, 20);*/
							//return;
					} else {
						c.sendMessage("You fail to cut through it.");
						return;
					}
				} else {

					c.sendMessage("You need a sharp tool or weapon to cut it.");
				}
			}
			break;
		case 23562:
			if ((c.getX() == 2578 || c.getX() == 2579) && c.getY() == 9583)
				AgilityHandler.delayEmote(c, "", 2567, 9524, 0, 2);
				break;
		case 23548:
		    // Read the player's agility level (replace with your accessor if needed)
		    // Example options:
		    // int agilityLevel = c.playerLevel[ServerConstants.AGILITY];
		    // int agilityLevel = c.getPA().getLevelForXP(c.playerXP[ServerConstants.AGILITY]);
		    int agilityLevel = c.getSkills().getLevel(Skill.AGILITY); // Commonly 16 = Agility; replace if different

		    if (agilityLevel < 40) {
		        c.sendMessage("You need an Agility level of 40 to cross this ledge.");
		        break;
		    }

		    // Attempt from north to south (9520 -> 9512)
		    if (c.getX() == 2580 && c.getY() == 9520) {
		        final boolean willSucceed = c.roll(c.yanilleBalanceLedgeSuccessChance(agilityLevel));

		        c.startAnimation(752);
		        c.playerStandIndex = 755;
		        c.playerTurnIndex = 754;
		        c.playerWalkIndex = 754;
		        c.playerTurn180Index = 754;
		        c.playerTurn90CWIndex = 754;
		        c.playerTurn90CCWIndex = 754;
		        c.playerRunIndex = 754;
		        c.getPA().requestUpdates();

		        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            boolean run = c.isRunning2;
		            boolean fail = false;
		            int ticks = 9;
		            @Override
		            public void execute(CycleEventContainer container) {
		                if (c.isRunning2) c.isRunning2 = false;

		                if (ticks > 0) {
		                    ticks--;
		                    if (!fail) c.getPA().walkTo(0, -1);

		                    // Midpoint tile where we decide the fail outcome for this attempt
		                    if (c.getX() == 2580 && c.getY() == 9516) {
		                        if (!willSucceed) {
		                            c.stopMovement();
		                            c.startAnimation(771);
		                            c.getPA().requestUpdates();
		                            c.sendMessage("You slip...");
		                            fail = true;
		                            ticks = 0; // stop the traversal
		                        }
		                    }

		                    if (c.getX() == 2580 && c.getY() == 9512) {
		                        c.startAnimation(758);
		                        c.playerWalkIndex = 758;
		                        c.playerTurnIndex = 758;
		                        c.playerStandIndex = 758;
		                        c.playerTurn180Index = 758;
		                        c.playerTurn90CWIndex = 758;
		                        c.playerTurn90CCWIndex = 758;
		                        c.playerRunIndex = 758;
		                        c.getPA().requestUpdates();
		                    }
		                }

		                if (ticks == 0) container.stop();
		            }

		            @Override
		            public void stop() {

	                    if(!fail) {
	                    	//c.getSkills().addExperience(15, Skill.AGILITY);
	                    	c.getPA().addSkillXP(15, 16);
	                    }
		                if (fail) {
		                     c.getPA().movePlayer(2597, 9568); // if this is the correct pit location
		                        c.startAnimation(767);
		                    c.sendMessage("..and fall into a poison spider pit!");
		                    // Optional: apply fall damage up to 15
		                    int dmg = (int) Math.floor(Math.random() * 16); // 0..15
		                    if (dmg > 0) {
		                        // Replace with your server's damage application
		                        // c.appendDamage(dmg, Hitmark.HIT);
		                        c.appendDamage(dmg, Hitmark.HIT);; // or your equivalent
		                    }
		                    // Optional: move the player to the lower level tile (set the correct coords for your map)
		                }

		                c.playerStandIndex = 0x328;
		                c.playerTurnIndex = 0x337;
		                c.playerWalkIndex = 0x333;
		                c.playerTurn180Index = 0x334;
		                c.playerTurn90CWIndex = 0x335;
		                c.playerTurn90CCWIndex = 0x336;
		                c.playerRunIndex = 0x338;
		                c.isRunning2 = run;
		                c.getPA().requestUpdates();
		                c.getMovementQueue().setBlockMovement(false);
		            }
		        }, 1);

		    // Attempt from south to north (9512 -> 9520)
		    } else if (c.getX() == 2580 && c.getY() == 9512) {
		        final boolean willSucceed = c.roll(c.yanilleBalanceLedgeSuccessChance(agilityLevel));

		        c.startAnimation(753);
		        c.playerStandIndex = 757;
		        c.playerTurnIndex = 756;
		        c.playerWalkIndex = 756;
		        c.playerTurn180Index = 756;
		        c.playerTurn90CWIndex = 756;
		        c.playerTurn90CCWIndex = 756;
		        c.playerRunIndex = 756;
		        c.getPA().requestUpdates();

		        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		            boolean run = c.isRunning2;
		            boolean fail = false;
		            int ticks = 9;

		            @Override
		            public void execute(CycleEventContainer container) {
		                if (c.isRunning2) c.isRunning2 = false;

		                if (ticks > 0) {
		                    ticks--;
		                    if (!fail) c.getPA().walkTo(0, 1);

		                    if (c.getX() == 2580 && c.getY() == 9516) {
		                        if (!willSucceed) {
		                            c.stopMovement();
		                            c.sendMessage("You slip...");
		                            fail = true;
		                            c.startAnimation(770);
		                            c.getPA().requestUpdates();
		                            ticks = 0;
		                        }
		                    }
		                    if (c.getX() == 2580 && c.getY() == 9520) {
		                        c.startAnimation(759);
		                        c.playerStandIndex = 759;
		                        c.playerTurnIndex = 759;
		                        c.playerWalkIndex = 759;
		                        c.playerTurn180Index = 759;
		                        c.playerTurn90CWIndex = 759;
		                        c.playerTurn90CCWIndex = 759;
		                        c.playerRunIndex = 759;
		                    }
		                }

		                if (ticks == 0) container.stop();
		            }

		            @Override
		            public void stop() {
	                    if(!fail)
	                    	c.getPA().addSkillXP(15, 16);
		                if (fail) {
		                     c.getPA().movePlayer(2597, 9568); // if this is the correct pit location
		                        c.startAnimation(767);
		                    c.sendMessage("..and fall into a poison spider pit!");
		                    int dmg = (int) Math.floor(Math.random() * 16); // 0..15
		                    if (dmg > 0) {
		                        // c.appendDamage(dmg, Hitmark.HIT);
		                        c.appendDamage(dmg, Hitmark.HIT);; // or your equivalent
				                c.getPA().requestUpdates();
		                    }
		                    // c.getPA().movePlayer(2597, 9568); // if this is the correct pit location
		                }

		                c.playerStandIndex = 0x328;
		                c.playerTurnIndex = 0x337;
		                c.playerWalkIndex = 0x333;
		                c.playerTurn180Index = 0x334;
		                c.playerTurn90CWIndex = 0x335;
		                c.playerTurn90CCWIndex = 0x336;
		                c.playerRunIndex = 0x338;
		                c.isRunning2 = run;
		                c.getPA().requestUpdates();
		            }
		        }, 1);
		    }
		    break;
		 case 9717:
         case 9718:
             if (c.diedOnTut && (c.getY() == 9502 || c.getY() == 9503)) {
                 c.getDH()
                         .sendStatement(
                                 "You have died so now all you need to do is continue",
                                 "onto the next step.");
                 c.getPA().createObjectHints(3111, 9518,
                         c.getHeight(), 2);
             } else if (c.diedOnTut == false && c.tutorialProgress >= 21 && (c.getY() == 9502 || c.getY() == 9503)) {
                 c.getPA().chatbox(6180);
                 c.getDH().chatboxText(
                                 "In this area you will find out about combat with swords and",
                                 "bows. Speak to the guide and he will tell you all about it.",
                                 "", "", "Combat");
                 c.getPA().chatbox(6179);
                 c.getPA().object(-1, 3094, 9502, 0, 0);
                 c.getPA().object(9708, 3095, 9502, 7, 0);

                 c.getPA().object(-1, 3094, 9503, 0, 0);
                 c.getPA().object(9708, 3095, 9503, 1, 0);

                 c.getPA().walkTo(1, 0);
                 CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                     @Override
                     public void execute(CycleEventContainer container) {
                        
                         // headicon
                         // to
                         // combat dude

                         container.stop();
                     }

                     @Override
                     public void stop() {
                    	 c.getPA().object(9717, 3094, 9503,
                                 2, 0);
                         c.getPA().object(9718, 3094, 9502,
                                 2, 0);
                         // others
                         c.getPA().object(-1, 3095, 9502, 0,
                                 0);
                         c.getPA().object(-1, 3095, 9503, 0,
                                 0);
                         c.getPA().createPlayerHints(1, 6); // draws
                     }
                 }, 2);
             }
             break;
         case 10083:
             if (c.tutorialProgress == 26) {
                 c.getPA().openUpBank();
                 // client.getPacketDispatcher().tutorialIslandInterface(60,
                 // 13);
                 c.getPA().chatbox(6180);
                 c.getDH().chatboxText(
                                 "You can store stuff here for safekeeping. If you die anything",
                                 "in your bank will be saved. To deposit something, rich click it",
                                 "and select 'store'. Once you've had a good look, close the",
                                 "window and move on through the door indicated.",
                                 "This is your bank box");
                 c.getPA().chatbox(6179);
                 c.tutorialProgress = 27;
                 c.getPA().createObjectHints(3125, 3124, 0, 2);
                 c.getPA().createPlayerHints(1, 7);
             } else if (c.tutorialProgress >= 27) {
                 c.getPA().createObjectHints(3125, 3124, 0, 2);
                 c.getDH().sendDialogues(1013, 494);
             }
             break;	
        	 
         case 9727:
             if (c.tutorialProgress == 26) {
 				AgilityHandler.delayEmote(c, "CLIMB_UP", 3111, 3127, 0, 2);
					//c.getLaddersAndStairs().climbLadderorStair(obX, obY, 3111, 3127, 0, true, false);
                 c.getDH().sendDialogues(3078, -1);
                 //c.startAnimation(828);
             } else if (c.tutorialProgress > 35) {
                 //UseOther.useUp(player, objectType);
             }
             break;
			case 3416:
				AgilityHandler.delayEmote(c, "", 2709, 3498, 0, 2);
				//c.getLaddersAndStairs().climbLadderorStair(obX, obY, 2709, 3498, 0, false, false);
			break;
         case 9719:
         case 9720:
             if (c.tutorialProgress >= 24
                     && (c.getY() == 9519 || c.getY() == 9518)
                     || c.diedOnTut) {
                 if (c.diedOnTut) {
                     c.getDH()
                             .sendStatement("Be more careful this time",
                                     "now continue to kill the rat and talk to the guide.");
                 }
                 c.getPA().chatbox(6180);
                 c.getDH()
                         .chatboxText(
                                 "",
                                 "To attack the rat, right click it and select the attack option. you",
                                 "will then walk over to it and start hitting it.",
                                 "", "Attacking");
                 c.getPA().chatbox(6179);
                 c.getPA().object(-1, 3111, 9518, 0, 0);
                 c.getPA().object(9719, 3110, 9518, 7, 0);

                 c.getPA().object(-1, 3111, 9519, 0, 0);
                 c.getPA().object(9720, 3110, 9519, 1, 0);

                 CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                     @Override
                     public void execute(CycleEventContainer container) {

                         c.getPA().object(9719, 3111, 9518,
                                 0, 0);
                         c.getPA().object(9720, 3111, 9519,
                                 0, 0);
                         // others
                         c.getPA().object(-1, 3110, 9518, 7,
                                 0);
                         c.getPA().object(-1, 3110, 9519, 1,
                                 0);
                         c.getPA().createPlayerHints(1, 6); // draws
                         // headicon
                         // to combat ude

                         container.stop();
                     }

                     @Override
                     public void stop() {

                     }
                 }, 4);
             } else if (c.tutorialProgress >= 25
                     && (c.getY() == 9519 || c.getY() == 9518)) {
                 c.getPA().object(-1, 3111, 9518, 0, 0);
                 c.getPA().object(3022, 3110, 9518, 7, 0);

                 c.getPA().object(-1, 3111, 9519, 0, 0);
                 c.getPA().object(3023, 3110, 9519, 1, 0);

                 CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                     @Override
                     public void execute(CycleEventContainer container) {

                         c.getPA().object(3022, 3111, 9518,
                                 0, 0);
                         c.getPA().object(3023, 3111, 9519,
                                 0, 0);
                         // others
                         c.getPA().object(-1, 3110, 9518, 7,
                                 0);
                         c.getPA().object(-1, 3110, 9519, 1,
                                 0);

                         container.stop();
                     }

                     @Override
                     public void stop() {

                     }
                 }, 4);
             }
             break;
        case 9726:
            if (c.tutorialProgress >= 14){
                    c.getDH().sendDialogues(3051, -1);
    				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3088, 9520, 0, 2);
                } else {
                    c.sendMessage("You aren't on this part yet.");
                    return;
                }
            break;
        case 9736:
            if (c.getItems().playerHasItem(2307) && c.tutorialProgress >= 8) {
                c.startAnimation(896);
				c.getDH().sendDialogues(3037, 0);
               // c.getPA().requestUpdates();
                c.getItems().deleteItem(2307, 1);
                c.getItems().addItem(2309, 1);
            }
            break;
		case 16543:
			c.isRunning = false;
			c.isRunning2 = false;
			c.sendMessage("You squeeze into the crevice...");

			// Set crawling animation
			int crawlAnim = 844;
			c.playerStandIndex = crawlAnim;
			c.playerTurnIndex = crawlAnim;
			c.playerWalkIndex = crawlAnim;
			c.playerTurn180Index = crawlAnim;
			c.playerTurn90CWIndex = crawlAnim;
			c.playerTurn90CCWIndex = crawlAnim;
			c.playerRunIndex = crawlAnim;
			c.getPA().requestUpdates();

			// Phase: 0 = moving, 1 = done
			final int[] phase = {0};

			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (phase[0] == 0) {
						// Check which side we're starting from
						if (c.getX() == 3028 && c.getY() == 9806) {
							// Going east
							c.getPA().walkTo(7, 0);
							phase[0] = 1;
						} 
						else if (c.getX() == 3035 && c.getY() == 9806) {
							// Going west
							c.getPA().walkTo(-7, 0);
							phase[0] = 1;
						}
					} 
					else if (phase[0] == 1) {
						// Arrived at other side — stop event
						if ((c.getX() == 3035 && c.getY() == 9806) || 
								(c.getX() == 3028 && c.getY() == 9806)) {
							phase[0] = 2;
						}
					}
					else if (phase[0] == 2) {
						// Arrived at other side — stop event
						if ((c.getX() == 3035 && c.getY() == 9806) || 
								(c.getX() == 3028 && c.getY() == 9806)) {
							phase[0] = 1;
							container.stop();
						}
					}
				}

				@Override
				public void stop() {
					c.sendMessage("...and come out the other side.");
					// Restore normal animations
					c.getCombat().getPlayerAnimIndex(
							c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase()
							);
				}
			}, 1); // Runs every tick
			break;
			case 26415: // GWD Boulder
				if (c.isForceMovementActive()) {
					break;
				}

				boolean isSouth = c.getY() <= 3715;
				int targetY = isSouth ? 3719 : 3715;
				String direction = isSouth ? "NORTH" : "SOUTH";

				// 6983 pushing North, 6984 pushing South
				int playerAnim = isSouth ? 6983 : 6984;

				// We pass ONLY the final destination, no stutter steps
				c.setMove(
						new int[][] { {2898, targetY} },
						direction,
						playerAnim,
						-1,
						200,  // speed1: Delay before sliding starts
						340,  // speed2: Total duration of the action
						2898, targetY,
						0,
						60,
						100,
						0
				);

				// The Object Timeline Event
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					int ticks = 0;

					@Override
					public void execute(CycleEventContainer container) {
						ticks++;

						if (ticks == 2) {
							// TICK 2: The player finishes bending down and pushes up.
							// Play the rock lifting animation NOW so it matches the player's hands!
							c.getPA().sendPlayerObjectAnimation(c, 2898, 3716, 6985, 10, 0, 0);

						} else if (ticks == 6) {
							// TICK 4: The rock is up, player starts sliding.
							// Swap to the hollow collision object so they don't clip.
							c.getPA().checkObjectSpawn(26416, 2898, 3716, 0, 10);

						} else if (ticks == 13) {
							// TICK 12: Player arrives and sets the rock down.
							// Revert the collision object and play the rock-dropping animation.
							c.getPA().sendPlayerObjectAnimation(c, 2898, 3716, 6986, 10, 0, 0);
						}else if (ticks == 14) {
							// TICK 12: Player arrives and sets the rock down.
							// Revert the collision object and play the rock-dropping animation.
							c.getPA().checkObjectSpawn(26415, 2898, 3716, 0, 10);
							container.stop();
						}
					}
				}, 1);
				break;
			case 26832://
				break;
		case 19044:
			if (c.getX() == 3755 && c.getY() == 5672) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3755, c.getY()+3, 0, 2);
				c.MLMUpperOrLower = true;
			}
			if (c.getX() == 3755 && c.getY() == 5675) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3755, c.getY()-3, 0, 2);
				c.MLMUpperOrLower = false;
			}//0 east, 2 west, 1 south, 3 north
			//MotherlodeMine.refreshObjects();
			//MotherlodeMine.spawnVeins();
			
			
			break;
		case 26674:
			MotherlodeMine.addToHopper(c);
			break;
		case 26688:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if(c.payDirtSackAmt > 0) {
					MotherlodeMine.processPayDirtSack(c);
				} else
					c.sendMessage("The sack is empty.");
			}
			break;
		case 26661:
		case 26662:
		case 26663:
		case 26664:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
			OreTile tile = MotherlodeMine.getTileAt(objectX, objectY);
			if (tile != null) {
			    tile.mine(c);
			}
			}
			break;
		case 26670:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				MotherlodeMine.fixStrut(c, objectX, objectY);
			}
			break;
		case 10047:
			if(objectX == 3760 && objectY == 5670)
				AgilityHandler.delayEmote(c, "", c.getX()+6, 5671, 0, 2);
			if(objectX == 3764 && objectY == 5671)
				AgilityHandler.delayEmote(c, "", c.getX()-6, 5670, 0, 2);
			break;
		case 26680:
		case 26679:
			c.startAnimation(625);
			if(Misc.random(5) != 5) {
				new Object(-1, objectX, objectY, 0, 0, 10, -1, 0);
				new WorldObject(-1, objectX, objectY, c.getHeight(), 10, 0);
				//c.startAnimation(-1);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}

					@Override
					public void stop() {
						new Object(objectId, objectX, objectY, 0, 0, 10, objectId, 20);
						new WorldObject(objectId, objectX, objectY, c.getHeight(), 10, 0);
					}
				}, 20);
			}
			// new Object(-1, closedDef.getX(), closedDef.getY(), closedDef.getH(), closedDef.getFace(), 0, -1, 0);
			break;

		case 23969:
			if (c.getX() == 3058 && (c.getY() == 9776 || c.getY() == 9777))
				AgilityHandler.delayEmote(c, "", 3061, c.getY()-6400, 0, 2);
			break;
		case 26654:
			if ((c.getX() == 3060 || c.getX() == 3061) && c.getY() == 9766)
				AgilityHandler.delayEmote(c, "", 3728, 5692, 0, 2);
			break;
		case 26655:
			if ((c.getX() == 3728) && c.getY() == 5692)
				AgilityHandler.delayEmote(c, "", 3060, 9766, 0, 2);
			break;
		case 24072:
			if (c.getX() == 2955 && c.getY() == 3337)
				AgilityHandler.delayEmote(c, "", 2956, 3338, 1, 2);
			if (c.getX() == 2960 && c.getY() == 3340 && c.getHeight() == 1)
				AgilityHandler.delayEmote(c, "", 2959, 3339, 2, 2);
			if (c.getX() == 2958 && c.getY() == 3337 && c.getHeight() == 2)
				AgilityHandler.delayEmote(c, "", 2959, 3338, 3, 2);
			break;
		case 24428:
			if (c.getX() == 3258 && (c.getY() == 3451 || c.getY() == 3452 || c.getY() == 3453) && c.getHeight() == 0)
				AgilityHandler.delayEmote(c, "", 1758, 4958, 0, 2);
			if (c.getX() == 1761 && (c.getY() == 4963 || c.getY() == 4964 || c.getY() == 4965) && c.getHeight() == 0)
				AgilityHandler.delayEmote(c, "", 1630, 4957, 0, 2);
			break;
		case 24427:
			if ((c.getX() == 1758 || c.getX() == 1759 || c.getX() == 1760) && c.getY() == 4958 && c.getHeight() == 0)
				AgilityHandler.delayEmote(c, "", 3258, 3451+Misc.random(2), 0, 2);
			if ((c.getX() == 1630 || c.getX() == 1631 || c.getX() == 1632) && c.getY() == 4957 && c.getHeight() == 0)
				AgilityHandler.delayEmote(c, "", 1761, 4963, 0, 2);
			break;
		case 24074:
			if (c.getX() == 2956 && c.getY() == 3338)
				AgilityHandler.delayEmote(c, "", 2955, 3337, 0, 2);
			if (c.getX() == 2959 && c.getY() == 3339 && c.getHeight() == 2)
				AgilityHandler.delayEmote(c, "", 2960, 3340, 1, 2);
			if (c.getX() == 2959 && c.getY() == 3338 && c.getHeight() == 3)
				AgilityHandler.delayEmote(c, "", 2958, 3337, 2, 2);
			break;
		case 24071:
			if (c.getX() == 2993 && c.getY() == 3341 && c.getHeight() == 1)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2993, 3341, 0, 2);
			if (c.getX() == 2995 && c.getY() == 3341 && c.getHeight() == 0)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2995, 3341, 1, 2);
			if (c.getX() == 2995 && c.getY() == 3341 && c.getHeight() == 3)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2995, 3341, 2, 2);
			break;
		case 24070:
			if (c.getX() == 2993 && c.getY() == 3341)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2993, 3341, 1, 2);
			if (c.getX() == 2995 && c.getY() == 3341 && c.getHeight() == 1)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2995, 3341, 2, 2);
			if (c.getX() == 2995 && c.getY() == 3341 && c.getHeight() == 2)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2995, 3341, 3, 2);
			break;
		case 6434:
			if(objectX == 3084 && objectY == 3272) {
				//if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				c.getPA().object(-1, objectX, objectY, 0, 10);
				c.getPA().object(6435, objectX, objectY, 0, 10);
				//}
			}
			if(objectX == 3118 && objectY == 3244) {
				//if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				c.getPA().object(-1, objectX, objectY, 0, 10);
				c.getPA().object(6435, objectX, objectY, 0, 10);
				//}
			}
			break;

		case 6435:
			if(objectX == 3118 && objectY == 3244)
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3118, c.getY()+6400, 0, 2);
		else if(objectX == 3084 && objectY == 3272)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3084, c.getY()+6400, 0, 2);
			break;
		case 34825:
			c.getPA().movePlayer(c.lastX, c.lastY, 0);
			break;
		case 16670:
			if (c.getX() == 3089 && c.getY() == 3251)
				AgilityHandler.delayEmote(c, "", 3093, 3251, 1, 2);
			break;
		case 16669:
			if (c.getX() == 3093 && c.getY() == 3251)
				AgilityHandler.delayEmote(c, "", 3089, 3251, 0, 2);
			break;
		case 5581:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				c.getPA().object(5582, objectX, objectY, 0, 10);
				c.getItems().addItem(1351, 1);
				c.sendMessage("You pull a bronze axe out of the stump.");
				CycleEventHandler.getSingleton().addEvent(15432, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						c.getPA().object(-1, objectX, objectY, 0, 10);
						container.stop();
					}

					@Override
					public void stop() {
						c.getPA().object(5581, objectX, objectY, 0, 10);
					}
				}, 100);
			}
			break;
		case 16529:
			c.getPA().walkTo(1, 0);
			c.postProcessing();
			CycleEventHandler.getSingleton().addEvent(15432, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					AgilityHandler.delayEmote(c, "TUNNEL_1", 3142, 3513, 0, 1);
					container.stop();
				}

				@Override
				public void stop() {
				}
			}, 1);
			break;
		case 16530:
			c.getPA().walkTo(-1, 0);
			c.postProcessing();
			CycleEventHandler.getSingleton().addEvent(15432, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					AgilityHandler.delayEmote(c, "TUNNEL_1", 3137, 3516, 0, 1);
					container.stop();
				}

				@Override
				public void stop() {
				}
			}, 1);
			break;
		case 11794:
			if (objectX == 3202 && objectY == 3434)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3202, 3435, 1, 2);
			if (objectX == 3202 && objectY == 3416)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3202, 3417, 1, 2);
			if (objectX == 3223 && objectY == 3387)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3222, 3387, 1, 2);
			if (objectX == 3233 && objectY == 3424)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3234, 3424, 1, 2);
			if (objectX == 3214 && objectY == 3410)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3214, 3411, 1, 2);
			break;
		case 11795:
			if (objectX == 3202 && objectY == 3434)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3202, 3435, 0, 2);
			if (objectX == 3202 && objectY == 3416)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3202, 3417, 0, 2);
			if (objectX == 3233 && objectY == 3424)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3234, 3424, 0, 2);
			if (objectX == 3214 && objectY == 3410)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3214, 3411, 0, 2);
			break;
		case 11796:
			if (objectX == 3227 && objectY == 3393)
				AgilityHandler.delayEmote(c, "", 3230, 3393, 1, 2);
			break;
		case 11797:
			if (objectX == 3188 && objectY == 3355)
				AgilityHandler.delayEmote(c, "", 3189, 3354, 1, 1);
			break;
		case 11799:

			if(c.getX() == 1614 && (c.getY() == 3680 || c.getY() == 3681))
				c.getPA().movePlayer(1618, c.getY(), 0);
			else if(c.getX() == 1614 && (c.getY() == 3665 || c.getY() == 3666))
				c.getPA().movePlayer(1618, c.getY(), 0);
			else if(objectX == 3266 && objectY == 3454)
				c.getPA().movePlayer(3266, 3451, 0);
			else if (objectX == 3228 && objectY == 3393)
				AgilityHandler.delayEmote(c, "", 3226, 3393, 0, 1);
			else if (objectX == 3188 && objectY == 3355)
				AgilityHandler.delayEmote(c, "", 3189, 3358, 0, 1);
			break;

			//Home stair Objects
		case 24079:
			if(c.getY() == 3369 && (c.getX() == 2972 || c.getX() == 2971))
				c.getPA().movePlayer(c.getX(), 3373, 1);
			break;
		case 24080:
			if(c.getY() == 3373 && (c.getX() == 2972 || c.getX() == 2971))
				c.getPA().movePlayer(c.getX(), 3369, 0);
			break;
		case 11807:
			if(c.getX() == 1618 && (c.getY() == 3680 || c.getY() == 3681))
				c.getPA().movePlayer(1614, c.getY(), 1);
			if(c.getX() == 1618 && (c.getY() == 3665 || c.getY() == 3666))
				c.getPA().movePlayer(1614, c.getY(), 1);
			break;
		case 12536:
			if(c.getX() == 1615 && c.getY() == 3687)
				c.getPA().movePlayer(1616, 3686, 2);
			if(c.getX() == 1618 && c.getY() == 3659)
				c.getPA().movePlayer(1617, 3660, 2);
			break;
		case 12538:
			if(c.getX() == 1616 && c.getY() == 3686)
				c.getPA().movePlayer(1615, 3687, 1);
			if(c.getX() == 1617 && c.getY() == 3660)
				c.getPA().movePlayer(1618, 3659, 1);
			break;
		case 11801:
			if (objectX == 3204 && objectY == 3389)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3203, 3389, 1, 1);
			break;

		case 11802:
			if (objectX == 3223 && objectY == 3387)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3222, 3387, 0, 2);
			if (objectX == 3204 && objectY == 3389)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3203, 3389, 0, 2);
			break;
		/*case 16683://up ladder
			if (objectX == 3229 && objectY == 3224) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3229, 3223, 1, 2);
			} else if (objectX == 3229 && objectY == 3213) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3229, 3214, 1, 2);
			}else if (objectX == 2597 && objectY == 3107) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), 1, 2);
			}
			break;*/
		case 16681:
			//c.getPA().sendMapRegion();
			if (objectX == 2597 && objectY == 3107) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2597, 3106, 0, 2);
			}
				break;
		case 16684:
			//c.getPA().sendMapRegion();
			if (objectX == 3229 && objectY == 3224) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3229, 3223, 0, 2);
			} else if (objectX == 3229 && objectY == 3213) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3229, 3214, 0, 2);
			}
			break;
		case 14880:
			//System.out.println("handleOption1");
			if (c.getX() == 3210 && c.getY() == 3216) {
				//c.getPA().movePlayer(3208, 9616, 0);
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3208, 9616, 0, 2);
			} 
			break;
		case 29336:
			//c.getPA().sendString("https://valius.net/community/index.php?/forum/55-custom-donations/", 12000);
			break;

		case 37340:
			if(c.hasNpc) {
				c.sendMessage("You can't take pets in with you.");
				return;
			}
			//if(c.getItems().hasAnyItems()) {
			//	c.sendMessage("You can't take any items into The Gauntlet!");
			//	return;
			//}
			/*if (!c.optionalInstance().isPresent()) {			
				c.setInstance(new TheGauntlet(c));
			}*/
			break;

		case 37341:
			//GauntletRewards.openChest(c);
			break;

		case 36082:
			c.startAnimation(1719);
			c.getPA().startTeleport2(3263, 6077, 0);
			break;

		case 11726:
			if (c.getX() >= 3189 && c.getX() <= 3191 && c.getY() == 3957) {
				c.getPA().movePlayer(3190, 3958, 0);	
			} else if (c.getX() >= 3189 && c.getX() <= 3191 && c.getY() == 3958) {
				c.getPA().movePlayer(3190, 3957, 0);
			}
			break;

		case 23644:
			if (c.getSkills().getLevel(Skill.AGILITY) <= 70) {
				c.getDH().sendStatement("You need 70 Agility to cross this log.");
				c.sendMessage("You need 70 Agility to cross this log.");
				break;
			}
			if (objectX == 2907) {
				AgilityHandler.delayEmote(c, "BALANCE", 2910, 3049, 0, 2);
			} else if (objectX == 2909) {
				AgilityHandler.delayEmote(c, "BALANCE", 2906, 3049, 0, 2);
			}
			break;



		case 29338:
			/*if (c.getItems().playerHasAllItems(33793, 33794, 33795)) {
				c.getItems().deleteItems(33793, 33794, 33795);
				c.sendMessage("You summon the Shadow Lord! Prepare yourself.");
				NPCHandler.spawnNpc(3383, 3110, 3697, 0, 1, 1600, 50, 250, 350);
			} else {
				c.getDH().sendStatement("You need the 3 Shadow lord armor pieces dropped by ", "@blu@ Calisto, Vet'ion and Venenatis.");
			}*/
			break;

		case 29337:
			//WildernessChest.searchChest(c);
			break;

		case 11701:
			c.getPA().startTeleport(2202, 3056, 0, "modern");
			break;
		case 10529:
		case 10527:
		case 6948:// deposit
		case 25937:
			c.isBanking = true;
			c.getPA().sendFrame126("The Bank of Runescape - Deposit Box", 7421);
			c.getPA().sendFrame248(4465, 197);// 197 just because you can't
			// see it =\
			c.getItems().resetItems(7423);
			c.isBanking = true;
			break;
		case 9398:
			break;
		case 6450:// Basic training ladder
			c.getPA().movePlayer(1644, 3673, 0);
			break;
		case 32153:// rune dragon barrier entry
			if (objectX == 1574 && (objectY <= 5077 && (objectY >= 5072 && c.getX() <= 1573))) {
				AgilityHandler.delayEmote(c, -1, 1575, c.getY(), 0, 4);
				c.sendMessage("@pur@You have entered the Rune dragon room.");
				return;
			} else if (objectX == 1574 && (objectY <= 5077 && (objectY >= 5072 && c.getX() >= 1575))) {
				AgilityHandler.delayEmote(c, -1, 1573, c.getY(), 0, 4);
				c.sendMessage("@pur@You have left the Rune dragon room.");
				return;
				//Adamant dragon barrier entry
			} else if (objectX == 1561 && (objectY <= 5077 && (objectY >= 5072 && c.getX() >= 1562))) {
				AgilityHandler.delayEmote(c, -1, 1560, c.getY(), 0, 4);
				c.sendMessage("@pur@You have entered the Adamant dragon room.");
				return;
			} else if (objectX == 1561 && (objectY <= 5077 && (objectY >= 5072 && c.getX() <= 1560))) {
				AgilityHandler.delayEmote(c, -1, 1562, c.getY(), 0, 4);
				c.sendMessage("@pur@You have left the Adamant dragon room.");
				return;
			}
			break;
		case 26709:// strongholdslayer cave
			c.getPA().movePlayer(2429, 9825, 0);
			c.sendMessage("Welcome to the Stronghold slayer cave, you can find many slayer monsters here!");
			break;
		case 26710:// strongholdslayer caveexit
		case 27258:
			c.getPA().movePlayer(2430, 3425, 0);
			break;
		case 28892:// catacomb agility
			if (c.getSkills().getLevel(Skill.AGILITY) < 34) {
				c.sendMessage("You need an Agility level of 34 to pass this.");
				return;
			}
			if (c.getX() == 1648) {
				AgilityHandler.delayEmote(c, "CRAWL", 1646, 10000, 0, 2);
			} else if (c.getX() == 1716) {
				AgilityHandler.delayEmote(c, "CRAWL", 1706, 10078, 0, 2);
			} else if (c.getX() == 1706) {
				AgilityHandler.delayEmote(c, "CRAWL", 1716, 10056, 0, 2);
			} else if (c.getX() == 1646) {
				AgilityHandler.delayEmote(c, "CRAWL", 1648, 10009, 0, 2);
			}
			break;
		case 30175:// Stronghold short
			if (c.getSkills().getLevel(Skill.AGILITY) < 72) {
				c.sendMessage("You need an Agility level of 72 to pass this.");
				return;
			}
			if (c.getX() == 2429) {
				AgilityHandler.delayEmote(c, "CRAWL", 2435, 9806, 0, 2);
			} else if (c.getX() == 2435) {
				AgilityHandler.delayEmote(c, "CRAWL", 2429, 9806, 0, 2);
			}
			break;

		case 536:// Smoke Devil Exit
			if (c.getX() == 2376) {
				AgilityHandler.delayEmote(c, "CRAWL", 2379, 9452, 0, 2);
			}
			break;


		case 1738:// tav entrance
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2884, 9798, 0, 2);
			break;
		case 2123:// relleka entrance
			AgilityHandler.delayFade(c, "CRAWL", 2808, 10002, 0, "You crawl into the entrance.",
					"and you end up in a dungeon.", 3);
			c.sendMessage("Welcome to the Relleka slayer dungeon, find many slayer tasks here.");
			break;
		case 2268:// ice dung exit
			AgilityHandler.delayEmote(c, "CLIMB_UP", 1651, 3619, 0, 2);
			break;
		case 2141:// relleka exit
			c.getPA().movePlayer(1259, 3502, 0);
			break;
			/*
			 * case 29734://dgorillas if (objectX == 1349 && objectY == 3591) {
			 * c.getPA().movePlayer(2130, 5646, 0); c.
			 * sendMessage("Welcome to the Demonic Gorilla's Dungeon, try your luck for a heavy frame!"
			 * ); } break;
			 */
		case 28687:// dgexit
			c.getPA().movePlayer(1348, 3590, 0);
			break;
		case 4153:// corpexit
			c.getPA().movePlayer(1547, 3571, 0);
			break;
		case 2544:// daggentrence
			c.getPA().movePlayer(2446, 10147, 0);
			break;
		case 8966:// dagexit
			c.getPA().movePlayer(1547, 3571, 0);
			break;
		case 2823:// mdragsentrance
			AgilityHandler.delayFade(c, "CRAWL", 1746, 5323, 0, "You crawl into the entrance.",
					"and you end up in a dungeon.", 3);
			c.sendMessage("Welcome to the Mith Dragons Cave, try your luck for a visage or d full helm!");
			break;
		case 25337:// mdragsexit
			c.getPA().movePlayer(1792, 3709, 0);
			break;
		case 4151:// barrows
			c.getPA().movePlayer(3565, 3308, 0);
			c.sendMessage("Welcome to Barrows, good luck with your rewards!");
			break;
		case 25016:
		case 25017:
		case 25018:
		case 25029:
			PuroPuro.magicalWheat(c);
			break;

		case 29334:
			//CompCapeRequirements.executeRequirements(c);
			break;

			/*case DwarfCannon.COLLAPSED_CANNON_ID:
			c.cannon.pickupCannon(object);
			break;

		case DwarfCannon.CANNON_OBJECT_ID:
			c.cannon.addAmmo(object);
			break;*/

		case 34548: //Hydra boss rocks
			if (c.getY() < 10251) {
				c.setInstance(new HydraInstance());
			} else if (c.getY() > 10251) {
				AgilityHandler.delayEmote(c, "JUMP", c.getX(), c.getY() - 2, 0, 2);
				if (c.getInstance() != null) {
					c.getInstance().leave(c);
				}
			}
			break;

		case 28449://Void champion boss cross
			c.startAnimation(1651);
			c.setInstance(new VoidChampionInstance());
			c.getDH().sendStatement("You have awakened the Void Knight Champion from his Slumber.");
			break;


		case 9369:
		case 18532:
			//c.getFishing().startFishing(18532, 22842);
			break;

		case 34553:
		case 34554: //Hydra boss door
			if (c.getX() <= 1355)
				c.getPA().movePlayer(c.getX() + 1, c.getY(), c.getHeight());
			else
				c.sendMessage("The door is locked securly from the other side.");
			break;


		case 29333:
			//c.sendMessage("Trading post has been temporarily disabled!");
			// Listing.openPost(c, false, true);
			break;

		case 29709: //santa's table
			c.getShops().openShop(128);
			break;

			//Wyvern Cave Stuff
		case 30844: //rope back home
			AgilityHandler.delayEmote(c, "CLIMB_UP", Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, 2);
			break;
		case 31485:
			if (c.getX() <= 3603) {
				AgilityHandler.delayEmote(c, "JUMP", 3607, 10290, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 3603, 10290, 0, 2);				
			}
			break;
		case 30849:
			AgilityHandler.delayEmote(c, "JUMP", 3633, 10264, 0, 2);
			break;
		case 30847:
			if (c.getY() >= 10259) {
				AgilityHandler.delayEmote(c, "JUMP", 3633, 10260, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 3633, 10264, 0, 2);
			}
			break;

		case 31990://VORKATH
			if (c.getY() == 4054) {
				//	c.getVorkath().exit(c);
			} else if (c.getY() == 4052) {
				//c.getVorkath().enterInstance(c, 10);
			}
			break;
		case 27288:
			c.getShops().openShop(79);
			break;

		case 31561:
			// south jump north
			if(c.getY() == objectY - 2) {
				c.getPA().walkTo2(objectX, objectY-2);
				c.turnPlayerTo(objectX, objectY);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY, 0, 2);
				c.startAnimation(3067);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY+2, 0, 4);

			}
			//north jump south
			if(c.getY() == objectY + 2) {
				c.getPA().walkTo2(objectX, objectY+2);
				c.turnPlayerTo(objectX, objectY);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY, 0, 2);
				c.getPlayerAction().setAction(true);
				c.startAnimation(3067);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY-2, 0, 4);
			}
			//east jump west
			if(c.getX() == objectX + 2) {
				c.getPA().walkTo2(objectX, objectX+2);
				c.turnPlayerTo(objectX, objectY);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY, 0, 2);
				c.getPlayerAction().setAction(true);
				c.startAnimation(3067);
				AgilityHandler.delayEmote(c, "JUMP", objectX-2, objectY, 0, 4);
			}
			//west jump east
			if(c.getX() == objectX - 2) {
				c.getPA().walkTo2(objectX, objectX-2);
				c.turnPlayerTo(objectX, objectY);
				AgilityHandler.delayEmote(c, "JUMP", objectX, objectY, 0, 2);
				c.getPlayerAction().setAction(true);
				c.startAnimation(3067);
				AgilityHandler.delayEmote(c, "JUMP", objectX+2, objectY, 0, 4);
			}
			break;

		case 11790:
			c.getPA().movePlayer(c.getX(), c.getY(), c.getHeight()+1);
			break;
		case 11793:
			c.getPA().movePlayer(c.getX(), c.getY(), c.getHeight()-1);
			break;

		case 23271:
			//if(WildernessEscape.eventActive == true && WildernessEscape.currentCheckpoint == 7) {//if host gets all 7 checkpoints and crosses ditch they win
			//	WildernessEscape.hostWins();
			//}
			c.objectDistance = 2;
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.getY() == 3520) {
						WildernessDitch.wildernessDitchEnter(c);
						container.stop();
					} 
					if (c.getY() >= 3522) {
						WildernessDitch.wildernessDitchLeave(c);
						container.stop();
					}
				}

				@Override
				public void stop() {
				}
			}, 1);
			break;
			case 29777: // Chambers of Xeric Entrance
				if (c.coxParty == null) {
					c.sendMessage("You must be in a raiding party to enter.");
					return;
				}
				if (c.coxParty.getLeader() != c) {
					c.sendMessage("Only the party leader can start the raid.");
					return;
				}

				// Start it!
				new ChambersOfXeric().startRaid(c.coxParty);
				break;
		case 16680:
			if (objectX == 3088) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3088, 9970, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY()+6400, 0, 2);
			}
			break;

			/*
		case 29150:
			int spellBook = c.playerMagicBook == 0 ? 1 : (c.playerMagicBook == 1 ? 2 : 0);
			int interfaceId = c.playerMagicBook == 0 ? 838 : (c.playerMagicBook == 1 ? 29999 : 938);
			String type = c.playerMagicBook == 0 ? "ancient" : (c.playerMagicBook == 1 ? "lunar" : "normal");

			c.sendMessage("You switch spellbook to " + type + " magic.");
			c.setSidebarInterface(6, interfaceId);
			c.playerMagicBook = spellBook;
			c.autocasting = false;
			c.autocastId = -1;
			c.getPA().resetAutocast();
			return;*/
		case 31858:
			int spellBook = c.playerMagicBook == 0 ? 1 : (c.playerMagicBook == 1 ? 2 : 0);
			int interfaceId = c.playerMagicBook == 0 ? 838 : (c.playerMagicBook == 1 ? 29999 : 938);
			String type = c.playerMagicBook == 0 ? "ancient" : (c.playerMagicBook == 1 ? "lunar" : "normal");

			c.sendMessage("You switch spellbook to " + type + " magic.");
			c.setSidebarInterface(6, interfaceId);
			c.playerMagicBook = spellBook;
			c.autocasting = false;
			c.autocastId = -1;
			c.getPA().resetAutocast();
			return;
		case 29241:
			if(!c.getRights().isOrInherits(Right.ADMINISTRATOR)) {
				/*if (c.amDonated == 0) {
					c.sendMessage("@red@You need to be a donator to use this feature.");
					return;
				}

				if (c.specRestore > 0) {
					int seconds = ((int) Math.floor(c.specRestore * 0.6));
					c.sendMessage("You have to wait another " + seconds + " seconds to use this altar.");
					return;
				}*/
			}

			c.startAnimation(645);
			//c.specRestore = 120;
			c.getHealth().removeAllStatuses();
			c.getHealth().reset();
			c.specAmount = 10.0;
			c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
			c.getSkills().resetToActualLevel(Skill.PRAYER);
			c.getHealth().removeAllStatuses();
			c.getHealth().reset();
			c.getPA().refreshSkill(5);
			c.sendMessage("You feel rejuvinated.");
			break;
		case 6150:

			if (c.getItems().playerHasItem(barType[0])) {
				c.getSmithingInt().showSmithInterface(barType[0]);
			} else if (c.getItems().playerHasItem(barType[1])) {
				c.getSmithingInt().showSmithInterface(barType[1]);
			} else if (c.getItems().playerHasItem(barType[2])) {
				c.getSmithingInt().showSmithInterface(barType[2]);
			} else if (c.getItems().playerHasItem(barType[3])) {
				c.getSmithingInt().showSmithInterface(barType[3]);
			} else if (c.getItems().playerHasItem(barType[4])) {
				c.getSmithingInt().showSmithInterface(barType[4]);
			} else if (c.getItems().playerHasItem(barType[5])) {
				c.getSmithingInt().showSmithInterface(barType[5]);
			} else {
				c.sendMessage("You don't have any bars.");
			}
			break;
		case 11846:
			if (c.combatLevel >= 100) {
				if (c.getY() > 5175) {
					//Highpkarena.addPlayer(c);
				} else {
					//Highpkarena.removePlayer(c, false);
				}
			} else if (c.combatLevel >= 80) {
				if (c.getY() > 5175) {
					//	Lowpkarena.addPlayer(c);
				} else {
					//Lowpkarena.removePlayer(c, false);
				}
			} else {
				c.sendMessage("You must be at least level 80 to compete in events.");
			}
			break;

		case 11845:
			if (c.combatLevel >= 100) {
				if (c.getY() < 5169) {
					//Highpkarena.removePlayer(c, false);
				}
			} else if (c.combatLevel >= 80) {
				if (c.getY() < 5169) {
					//Lowpkarena.removePlayer(c, false);
				}
			} else {
				c.sendMessage("You must be at least level 80 to compete in events.");
			}

			break;
		case 22472:
			c.getPA().showInterface(65000);
			break;
		case 31621: //tp interface home portal
		case 29344: //dzone teleporter
		case 33393:
			//c.getPortalTeleports().openInterface();
			break;
		case 15615:
			c.turnPlayerTo(objectX, objectY);
			c.startAnimation(5067);
			c.setLastContainerSearch(System.currentTimeMillis());
			c.getItems().addItem(10501, 1);
			c.sendMessage("You successfully made a snowball.");
			break;
		case 10068:
			if (c.deathItems.size() > 0) {
				c.getDH().sendDialogues(642, 2040);
				c.nextChat = -1;
			} else {
				if (c.getZulrahEvent().isActive()) {
					c.getDH().sendStatement("It seems that a zulrah instance for you is already created.",
							"If you think this is wrong then please re-log.");
					c.nextChat = -1;
					return;
				}
				c.getZulrahEvent().initialize();
			}
			break;
		
		case 12941:
			//PlayerAssistant.refreshSpecialAndHealth(c);
			break;
			/*
			 * case 29747: //Ice Demon Brazziers if (c.getItems().playerHasItem(20799, 1)) {
			 * World.getWorld().getGlobalObjects().replace(new GlobalObject(29747, obX, obY,
			 * c.heightLevel, 0, 10, 50, -1), new GlobalObject(29748, obX, obY,
			 * c.heightLevel, 0, 10, 0, -1)); c.getItems().deleteItem(20799, 1); } else {
			 * c.sendMessage("You need some kindling to light this brazier!"); } break; case
			 * 29748: if (c.getItems().playerHasItem(20799, 1)) { //addBrazzierVariable
			 * c.sendMessage("You add a piece of kindling to the brazier.");
			 * c.getItems().deleteItem(20799, 1); } else {
			 * c.sendMessage("You need some kindling to light this brazier!"); } break;
			 */
		case 26811:
			c.getShops().openShop(77);
			//c.sendMessage("@red@ You have</col> @blu@" + c.votePoints + "</col> @red@Vote points");
			//c.sendMessage("@red@ Please type in ::vote to go to the site. And type ::reward to receive them!");
			//c.sendMessage("@red@ Thank you for supporting Valius!");
			break;
		case 31556:
			GlobalObject object = new GlobalObject(objectId, objectX, objectY, c.getHeight());
			if (object.getLocation().getY() == 5361) {
				c.getPA().movePlayer(3011, 3927, 0);
			} else if (object.getLocation().getY() == 3926) {
				c.getPA().movePlayer(1645, 5365, 0);
				c.sendMessage("You enter the Deep Wilderness Revenant Dungeon. Beware!");
			} else {
				c.getPA().movePlayer(3241, 10234, 0);
				c.sendMessage("@blu@You enter the Revenant Dungeon. You can upgrade your Wilderness weapons");
				c.sendMessage("@blu@by using Shards dropped by the Revenants in the Deep wilderness");
				c.sendMessage("@blu@Revenant dungeon located East of the Wilderness agility course!");
			}
			break;

		case 31558:
			c.getPA().movePlayer(3126, 3833, 0);
			break;

		case 7811:
			//if (!c.inClanWarsSafe()) {
			//	return;
			//}
			c.getShops().openShop(116);
			break;
		case 4150:
			if(objectX == 2273 && objectY == 4681)
				NightmareZone.leaveDream(c);
			break;
		case 23115:// from bobs
			c.getPA().spellTeleport(3094, 3500, 0);
			break;
		case 10251:
			c.getPA().spellTeleport(2525, 4776, 0);
			break;
		case 26756:

			break;

		case 27057:
			//Overseer.handleBludgeon(c);
			break;

		case 14918:
			//if (!c.getDiaryManager().getWildernessDiary().hasDoneAll()) {
			c.sendMessage("You must have completed the whole wilderness diary to use this shortcut.");
			//	return;
			//}

			if (c.getY() > 3808) {
				AgilityHandler.delayEmote(c, "JUMP", 3201, 3807, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 3201, 3810, 0, 2);
			}
			break;
		case 7527:
			if (c.getX() == 3042) {
				AgilityHandler.delayEmote(c, "WALL_EMOTE", 3, 0, 2);
			} else if (c.getX() == 3045) {
				AgilityHandler.delayEmote(c, "WALL_EMOTE", -3, 0, 2);
			}
			break;
		case 993:
			if (c.getX() == 2637) {
				//c.getPA().walkTo(1, 0);
				AgilityHandler.delayEmote(c, "WALL_EMOTE", 3, 0, 2);
			} else if (c.getX() == 2640) {
				//c.getPA().walkTo(-1, 0);
				AgilityHandler.delayEmote(c, "WALL_EMOTE", -3, 0, 2);
			} else if (c.getX() == 2654) {
				//c.getPA().walkTo(1, 0);
				AgilityHandler.delayEmote(c, "WALL_EMOTE", 3, 0, 2);
			} else if (c.getX() == 2657) {
				//c.getPA().walkTo(-1, 0);
				AgilityHandler.delayEmote(c, "WALL_EMOTE", -3, 0, 2);
			}
			break;
		case 29728:
			if (c.getY() > 3508) {
				AgilityHandler.delayEmote(c, "JUMP", 1722, 3507, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 1722, 3512, 0, 2);
			}
			break;

		case 28893:
			if (c.getSkills().getLevel(Skill.AGILITY) < 54) {
				c.sendMessage("You need an Agility level of 54 to pass this.");
				return;
			}
			if (c.getY() > 10064) {
				AgilityHandler.delayEmote(c, "JUMP", 1610, 10062, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 1613, 10069, 0, 2);
			}
			break;

		case 27987: // scorpia
			if (c.getX() == 1774) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1769, 3849, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1774, 3849, 0, 2);
			}
			break;

		case 27988: // scorpia
			if (c.getX() == 1774) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1769, 3849, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1774, 3849, 0, 2);
			}
			break;

		case 27985:
			if (c.getY() > 3872) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1761, 3871, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1761, 3874, 0, 2);
			}
			break;

		case 27984:
			if (c.getY() > 3872) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1761, 3871, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1761, 3874, 0, 2);
			}
			break;

		case 29730:
			if (c.getX() > 1604) {
				AgilityHandler.delayEmote(c, "JUMP", 1603, 3571, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "JUMP", 1607, 3571, 0, 2);
			}
			break;

		case 25014:
			if (Boundary.isIn(c, Boundary.PURO_PURO)) {
				c.getPA().startTeleport(2525, 2916, 0, "puropuro");
			} else {
				c.getPA().startTeleport(2592, 4321, 0, "puropuro");
			}
			break;

			/*
			 * Hydra Dungeon Rocks/Entrances & Drake + Wyrms
			 * TODO: movment to drake area, movement to wyrm area, dmg when switching boots inside the dungeon
			 */
		case 34359:
			c.getPA().movePlayer(1312, 10188, 0);
			break;
		case 34514:
			c.getPA().movePlayer(1313, 3807, 0);
			break;
		case 34544:		
			if (c.getY() == 10205 || c.getY() == 10206) { //going west on west rocks
				AgilityHandler.delayEmote(c, 839, (c.getX() == 1303 || c.getX() == 1322) ? c.getX() - 2 : c.getX() + 2, c.getY(), 0, 2);
			} else if (c.getY() == 10214 || c.getY() == 10216) {
				AgilityHandler.delayEmote(c, 839, c.getX(), c.getY() == 10214 ? c.getY() + 2 : c.getY() - 2, 0, 2);
			}
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {

					if (c.disconnected || c.getHealth().getAmount() <= 0 || !Boundary.isIn(c, Boundary.HYDRA_ROOMS)) {
						container.stop();
						return;
					}

					if (Boundary.isIn(c, Boundary.HYDRA_ROOMS)) {
						if (c.getItems().isWearingItem(23037) || c.getItems().isWearingItem(22951) || c.getItems().isWearingItem(21643)) {
							return;
						}
						c.appendDamage(1, Hitmark.HIT);
					}
				}
			}, 3); // handles delay between dmg (in ticks | 600ms)
			break;

		case 34530:// drake stairs
			c.getPA().movePlayer(1334, 10205, 1);
			break;
		case 34531:// drake stairs
			c.getPA().movePlayer(1329, 10205, 0);
			break;

		case 4154:// lizexit
			c.getPA().movePlayer(1465, 3687, 0);
			break;

		case 30366:// Mining Guild Entrance
			if (c.getX() == 3043 && c.getY() == 9730) {
				if (c.getSkills().getLevel(Skill.MINING) >= 60) {
					c.getPA().movePlayer(3043, 9729, 0);
				} else {
					c.sendMessage("You must have a mining level of 60 to enter.");
				}
			} else if (c.getX() == 3043 && c.getY() == 9729) {
				c.getPA().movePlayer(3043, 9730, 0);
			}
			break;

		case 30365:// Mining Guild Entrance
			if (c.getX() == 3019 && c.getY() == 9733) {
				if (c.getSkills().getLevel(Skill.MINING) >= 60) {
					c.getPA().movePlayer(3019, 9732, 0);
				} else {
					c.sendMessage("You must have a mining level of 60 to enter.");
				}
			} else if (c.getX() == 3019 && c.getY() == 9732) {
				c.getPA().movePlayer(3019, 9733, 0);
			}
			break;

		case 8356:
			c.getDH().sendDialogues(55874, 2200);
			break;

		case 4004:
			//int InterfaceId = 38000;
			//Wogw.open(c, InterfaceId);
			break;

		case 1727:
		case 1728: // Kbd gates
			if (c.getX() == 3007) {
				c.getPA().walkTo(+1, 0);
			} else if (c.getX() == 3008) {
				c.getPA().walkTo(-1, 0);
			} else if (c.getX() == 2816) {
				c.getPA().walkTo(-1, 0);				
			} else if (c.getX() == 2815) {
				c.getPA().walkTo(+1, 0);				
			}
			break;

		case 10439:
		case 7814:
			//PlayerAssistant.refreshHealthWithoutPenalty(c);
			break;
		case 2670:
			if (!c.getItems().playerHasItem(1925) || !c.getItems().playerHasItem(946)) {
				c.sendMessage("You must have an empty bucket and a knife to do this.");
				return;
			}
			c.getItems().deleteItem(1925, 1);
			c.getItems().addItem(1929, 1);
			c.sendMessage("You cut the cactus and pour some water into the bucket.");
			//c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CUT_CACTUS);
			break;
			// Carts Start
		case 7029:
			//TrainCart.handleInteraction(c);
			break;
		case 28837:
			c.getDH().sendDialogues(193193, -1);
			break;
			// Carts End
		case 10321:
			c.getPA().spellTeleport(1752, 5232, 0);
			c.sendMessage("Welcome to the Giant Mole cave, try your luck for a granite maul.");
			break;
		case 1294:
			//c.getDH().tree = "stronghold";
			//c.getDH().sendDialogues(65, -1);
			break;

		case 1293:
			//c.getDH().tree = "village";
			//c.getDH().sendDialogues(65, -1);
			break;

		case 1295:
			//c.getDH().tree = "grand_exchange";
			//c.getDH().sendDialogues(65, -1);
			break;

		case 20877:
			AgilityHandler.delayFade(c, "CRAWL", 2712, 9564, 0, "You crawl into the entrance.",
					"and you end up in a dungeon.", 3);
			//c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.ENTER_BRIMHAVEN_DUNGEON);
			break;
		case 20878:
			AgilityHandler.delayFade(c, "CRAWL", 1571, 3659, 0, "You crawl into the entrance.",
					"and you end up in a dungeon.", 3);
			//c.getDiaryManager().getKaramjaDiary().progress(KaramjaDiaryEntry.ENTER_BRIMHAVEN_DUNGEON);
			break;
		case 16675:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 2445, 3416, 1, 2);
			break;
		case 16677:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 2445, 3416, 0, 2);
			break;

			//case 6434:
			//	AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3118, 9644, 0, 2);
			//break;

		case 11441:
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2856, 9570, 0, 2);
			break;

		case 18969:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 2857, 3167, 0, 2);
			break;

		case 11835:
			AgilityHandler.delayFade(c, "CRAWL", 2480, 5175, 0, "You crawl into the entrance.",
					"and you end up in Tzhaar City.", 3);
			break;
		case 11836:
			AgilityHandler.delayFade(c, "CRAWL", 1212, 3540, 0, "You crawl into the entrance.",
					"and you end up back on Mt. Quidamortem.", 3);
			break;

		case 155:
			AgilityHandler.delayEmote(c, "BALANCE", 3096, 3359, 0, 2);
			break;
		case 160:
			AgilityHandler.delayEmote(c, 2140, 3098, 3357, 0, 2);
			break;

		case 23568:
			c.getPA().movePlayer(2704, 3205, 0);
			break;

		case 23569:
			c.getPA().movePlayer(2709, 3209, 0);
			break;

		case 17068:
			if (c.getSkills().getLevel(Skill.AGILITY) < 8 || c.getSkills().getLevel(Skill.STRENGTH) < 19
					|| c.getSkills().getLevel(Skill.RANGED) < 37) {
				c.sendMessage(
						"You need an agility level of 8, strength level of 19 and ranged level of 37 to do this.");
				return;
			}
			AgilityHandler.delayEmote(c, "JUMP", 3253, 3180, 0, 2);
			//c.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.RIVER_LUM_SHORTCUT);
			break;

		case 16465:
			/*if (!c.getDiaryManager().getDesertDiary().hasCompletedSome("ELITE")) {
				c.sendMessage("You must have completed all tasks in the desert diary to do this.");
				return;
			}*/
			if (c.getSkills().getLevel(Skill.AGILITY) < 82) {
				c.sendMessage("You need an agility level of at least 82 to squeeze through here.");
				return;
			}
			c.sendMessage("You squeeze through the crevice.");
			if (c.getX() == 3506 && c.getY() == 9505)
				c.getPA().movePlayer(3500, 9510, 2);
			else if (c.getX() == 3500 && c.getY() == 9510)
				c.getPA().movePlayer(3506, 9505, 2);
			break;
		case 36691:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1))
				c.getPA().movePlayer(3226, 6046, 0);
			break;
		case 36690:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 3, 3))
				c.getPA().movePlayer(3225, 12445, 0);
			break;
		case 2147:
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3104, 9576, 0, 2);
			break;
		case 2148:
			AgilityHandler.delayEmote(c, "CLIMB_UP", 3105, 3162, 0, 2);
			break;
		case 1579:
			new WorldObject(1581, objectX, objectY, 0, 22, worldObject.face);
			//if(objectX == 3097 && objectY == 3468)
				//AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3097, 9868, 0, 2);
			break;

		case 1581:
			if(objectX == 3097 && objectY == 3468)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3097, 9868, 0, 2);
			break;
		case 20790:
			if(objectX == 3081 && objectY == 3420) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1860, 5244, 0, 2);
				c.getDH().sendStatement2("You squeeze through the hole and find a ladder a few feet down","leading into the Stronghold of Security.");
			}
				//new Object(20791, objectX, objectY, 0, face, 0, -1, 0);
				//AgilityHandler.delayEmote(c, "CLIMB_UP", 2728, 3492, 1, 2);
			break;
		case 20784:
			if(objectX == 1859 && objectY == 5244)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3081, 3421, 0, 2);
			if(objectX == 1913 && objectY == 5226)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 3081, 3421, 0, 2);
				//new Object(20791, objectX, objectY, 0, face, 0, -1, 0);
				//AgilityHandler.delayEmote(c, "CLIMB_UP", 2728, 3492, 1, 2);
			break;
		case 23921:
			if(objectX == 1913 && objectY == 5226)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1860, 5244, 0, 2);
			break;
		case 23732:
			if(objectX == 2350 && objectY == 5215)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1860, 5244, 0, 2);
			break;
		case 23705:
			if(objectX == 2123 && objectY == 5251)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1860, 5244, 0, 2);
			break;
		case 19004:
			if(objectX == 2026 && objectY == 5218)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2123, 5252, 0, 2);
			break;
		case 12230:
			if(objectX == 1752 && objectY == 5136)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2999, 3375, 0, 2);
			break;
		case 20785:
			if(objectX == 1902 && objectY == 5222)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2042, 5245, 0, 2);
			break;
		case 20788:	
			if(!c.getItems().playerHasItem(9004)) {
				c.getDH().sendStatement("You rummage around in the dead explorer's bag.....");
				c.nextChat = 45000;
				c.getItems().addItem(9004, 1);
			} else
				c.sendMessage("You don't find anything.");
			break;
		case 19003:
			if(objectX == 2042 && objectY == 5246)
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1859, 5244, 0, 2);
			break;
		case 23706:
			if(objectX == 2148 && objectY == 5284)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2358, 5215, 0, 2);
			
			break;
		case 23707:
			if(objectX == 2120 && objectY == 5258 && c.emoteUnlock[6])
				AgilityHandler.delayEmote(c, "", 2148, 5278, 0, 2);
			break;
		case 20786:
			if(objectX == 1863 && objectY == 5238 && c.emoteUnlock[8])
				AgilityHandler.delayEmote(c, "", 1910, 5222, 0, 2);
			break;
		case 19005:
			if(objectX == 2039 && objectY == 5240 && c.emoteUnlock[9])
				AgilityHandler.delayEmote(c, "", 2018, 5212, 0, 2);
			break;
		case 23922:
			if(objectX == 2365 && objectY == 5212 && c.emoteUnlock[7])
				AgilityHandler.delayEmote(c, "", 2349, 5214, 0, 2);
			break;
		case 16685:
			if(objectX == 3096 && objectY == 3433)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), 0, 2);
			if(objectX == 3097 && objectY == 3433)
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), 1, 2);
			break;
		case 17026:
			if(objectX == 3096 && objectY == 3433)
				AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), 1, 2);
			if(objectX == 3097 && objectY == 3433)
				AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), 2, 2);
				//new Object(20791, objectX, objectY, 0, face, 0, -1, 0);
				//AgilityHandler.delayEmote(c, "CLIMB_UP", 2728, 3492, 1, 2);
			break;
		case 25938:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if(objectX == 2728 && objectY == 3491)
					AgilityHandler.delayEmote(c, "CLIMB_UP", 2728, 3492, 1, 2);
				if(objectX == 2715 && objectY == 3470)
					AgilityHandler.delayEmote(c, "CLIMB_UP", 2715, 3471, 1, 1);
			}
			break;
		case 26118:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if(objectX == 2715 && objectY == 3472)
					AgilityHandler.delayEmote(c, "CLIMB_UP", 2714, 3472, 3, 1);
			}
			break;
		case 26119:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if(objectX == 2715 && objectY == 3472)
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2714, 3472, 1, 1);
			}
			break;
		case 25939:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if(objectX == 2728 && objectY == 3491)
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2728, 3492, 0, 2);
				if(objectX == 2715 && objectY == 3470)
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2715, 3471, 0, 2);
			}
			break;
		case 12266:
			boolean open = false;
			if(!open) {
				c.startAnimation(827);
				c.getPA().sendConfig(680, 1 << 22);
				open = true;
			} 
			if(open) {
				if(c.getX() == 3078 && c.getY() == 3493)
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", objectX, objectY+6400, 0, 2);

			}
			break;
		case 15079: // Flax patch
		case 15084: // Herb patch
		    if (c.isRaking) return;
		    
		    final boolean isFlax = (c.objectId == 15079);
		    
		    // Check if already clean (using your cycle limits)
		    if ((isFlax && c.flaxPatchState >= 2) || (!isFlax && c.herbPatchState >= 6)) {
		        c.sendMessage("This patch is already perfectly raked.");
		        return;
		    }

		    c.isRaking = true;
		    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		        @Override
		        public void execute(CycleEventContainer container) {
		            // Stop if we hit the "Clean" state
		            c.startAnimation(2273);
		            if (isFlax && c.flaxPatchState >= 3) {
			           // c.startAnimation(65535);
		                c.sendMessage("The flax patch is now clean.");
		                container.stop();
		                return;
		            }
		            if (!isFlax && c.herbPatchState >= 6) {
			            //c.startAnimation(65535);
		                c.sendMessage("The herb patch is now clean.");
		                container.stop();
		                return;
		            }

		            
		            // Your math: Flax adds 1, Herb adds 2
		            if (isFlax) {
		                c.flaxPatchState += 1;
		            } else {
		                c.herbPatchState += 2;
		            }
		            
		            c.updateMiscellania(c.flaxPatchState, c.herbPatchState);
		        }

		        @Override
		        public void stop() {
		            c.isRaking = false;
		        }
		    }, 3); // 1 tick per rake
		    break;
		case 12265:
			if(c.getX() == 3077 && c.getY() == 9893)
				AgilityHandler.delayEmote(c, "", objectX+2, objectY-6400, 0, 2);
			break;
		case 17385:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if (objectX == 3088) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3088, 3572, 0, 2);
				} else if (objectX == 3209) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3210, 3216, 0, 2);
				} else if (objectX == 3097 && objectY == 9867) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3096, 3468, 0, 2);
				} else if (objectX == 3116 && objectY == 9852) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3116, c.getY()-6400, 0, 2);
				} else if (objectX == 3084 && objectY == 9672) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3084, c.getY()-6400, 0, 2);
				} else if (objectX == 2884 && objectY == 9797) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 2884, c.getY()-6400, 0, 2);
				}
			}
			break;
			case 6436:
				if (objectX == 3118 && objectY == 9643) {
					AgilityHandler.delayEmote(c, "CLIMB_UP", 3118, c.getY()-6400, 0, 2);
				}
				break;
		case 1804:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if (objectX == 3115 && objectY == 3450) {
					if(c.getX() == 3115 && c.getY() == 3450) {
						c.getPA().object(1804, objectX, objectY, 0, 0);
						c.getPA().walkTo(0, -1);
						CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								c.sendMessage("The door locks behind you.");
								container.stop();
							}

							@Override
							public void stop() {

								c.getPA().object(1804, objectX, objectY, 3, 0);
							}

						}, 3);
					} else {
						c.sendMessage("The door is locked.");
					}
				}
			}
			break;
		case 17384:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if (objectX == 3116 && objectY == 3452) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3116, c.getY()+6400, 0, 2);
				}
				c.sendMessage("you climb down the ladder.");
			}
			break;
		case 27785:
			c.getDH().sendDialogues(70300, 1);
			break;
		case 30266:
			/*if (c != null) {
				c.sendMessage("The Inferno is currently under construction.");
				return;
			}*/
			c.getPA().movePlayer(2495, 5174, 0);
			break;
		case 28894:
		case 28895:
		case 28898:
		case 28897:
		case 28896: // catacomb exits
			c.getPA().movePlayer(1639, 3673, 0);
			c.sendMessage("You return to the statue.");
			break;
		case 27777:
			c.getPA().movePlayer(1781, 3412, 0);
			c.sendMessage("Welcome to the CrabClaw Isle, try your luck for a tentacle or Trident of the Seas!.");
			break;
		case 3828:
			c.getPA().movePlayer(3484, 9510, 2);
			c.sendMessage("Welcome to the Kalphite Lair, try your luck for a dragon chain or uncut onyx!.");
			break;

		case 3829:
			c.getPA().movePlayer(1845, 3809, 0);
			c.sendMessage("You find the light of day outside of the tunnel!");
			break;
		case 3832:
			c.getPA().movePlayer(3510, 9496, 2);
			break;

		case 4031:
			if (c.getY() == 3117) {
				if (EquipmentSet.DESERT_ROBES.isWearing(c)) {
					//c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PASS_GATE_ROBES);
				} else {
					//c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PASS_GATE);
				}
				c.getPA().movePlayer(c.getX(), 3115, 0);
			} else {
				c.getPA().movePlayer(c.getX(), 3117, 0);
			}
			break;

		case 7122:
			if (c.getX() == 2564 && c.getY() == 3310)
				c.getPA().movePlayer(2563, 3310, 0);
			else if (c.getX() == 2563 && c.getY() == 3310)
				c.getPA().movePlayer(2564, 3310, 0);
			break;
		case 538:
			c.getPA().movePlayer(2280, 10016, 0);
			break;

		case 537:
			c.getPA().movePlayer(2280, 10022, 0);
			break;

		case 6462: // Ice gate
		case 6461:
			c.getPA().movePlayer(2852, 3809, 2);
			break;

		case 6456: // Ice ledge
			c.getPA().movePlayer(2855, c.getY(), 1);
			break;

		case 6455: // Ice ledge (Bottom)
			if (c.getY() >= 3804)
				c.getPA().movePlayer(2837, 3803, 1);
			else
				c.getPA().movePlayer(2837, 3805, 0);
			break;

		case 677:	
			if (c.getX() <= 2970)
				c.getPA().movePlayer(2974, 4384, 2);
			else if (c.getX() >= 2979)
				c.getPA().movePlayer(2970, 4384, 2);		
			break;

		case 13641: // Teleportation Device
			c.getDH().sendDialogues(63, -1);
			break;
		case 23104:
			if (System.currentTimeMillis() - c.cerbDelay > 5000) {
				Cerberus cerb = c.createCerberusInstance();
				if (!c.debugMessage)
					if (!c.getSlayer().getTask().isPresent() && c.playerRights != 3) {
						c.sendMessage("You must have an active cerberus or hellhound task to enter this cave...");
						return;
					}
				/*if (!c.debugMessage)
					if (!c.getSlayer().getTask().get().getPrimaryName().equals("cerberus")
							&& !c.getSlayer().getTask().get().getPrimaryName().equals("hellhound")) {
						c.sendMessage("You must have an active cerberus or hellhound task to enter this cave...");
						return;
					}*/
				/*if (c.getCerberusLostItems().size() > 0) {
					c.getDH().sendDialogues(642, 5870);
					c.nextChat = -1;
					return;
				}*/

				if (cerb == null) {
					c.sendMessage("We are unable to allow you in at the moment.");
					c.sendMessage("Too many players.");
					return;
				}

				if (World.getWorld().getEventHandler().isRunning(c, "cerb")) {
					c.sendMessage("You're about to fight start the fight, please wait.");
					return;
				}
				c.getCerberus().init();
				c.cerbDelay = System.currentTimeMillis();
			} else {
				c.sendMessage("Please wait a few seconds between clicks.");
			}
			break;

		case 21772:
			if (!Boundary.isIn(c, Boundary.BOSS_ROOM_WEST)) {
				return;
			}
			Cerberus cerb = c.getCerberus();

			if (cerb != null) {
				cerb.end(DisposeTypes.INCOMPLETE);
			} else {
				c.getPA().movePlayer(1309, 1250, 0);
			}
			break;
		case 27555:
			World.getWorld().getGlobalObjects().add(new GlobalObject(27556, objectX, objectY, 0, 3, 10, 6, 27555)); // North - Awakened Altar
			break;
		case 28900:
			DarkAltar.handleDarkTeleportInteraction(c);
			break;
		case 28925:
			DarkAltar.handlePortalInteraction(c);
			break;

		case 23105:
			c.appendDamage(5, Hitmark.HIT);
			if (c.getY() == 1241) {
				c.getPA().walkTo(0, +2);
			} else {
				if (c.getCerberus() != null) {
					c.getCerberus().end(DisposeTypes.INCOMPLETE);
					c.getPA().movePlayer(1309, 1250, 0);
				}
			}
			break;

		case 12355:
			/*RecipeForDisaster rfd = c.createRecipeForDisasterInstance();

			if (c.rfdChat == 1) {
				if (rfd == null) {
					c.sendMessage("We are unable to allow you to start the minigame.");
					c.sendMessage("Too many players.");
					return;
				}

				if (World.getWorld().getEventHandler().isRunning(c, "rfd")) {
					c.sendMessage("You're about to fight start the minigame, please wait.");
					return;
				}
				c.getrecipeForDisaster().init();
			} else {
				c.getDH().sendDialogues(58, 4847);
			}*/
			break;

		case 12356: // Rfd Portal
			/*if (!Boundary.isIn(c, Boundary.RFD)) {
				return;
			}
			rfd = c.getrecipeForDisaster();

			if (rfd != null) {
				rfd.end(DisposeTypes.INCOMPLETE);
			} else {
				c.getPA().movePlayer(3218, 9622, 0);
			}*/
			break;

		case 4383:
			/*DagannothMother mother = c.createDagannothMotherInstance();

			if (mother == null) {
				c.sendMessage("We are unable to allow you to fight the mother.");
				c.sendMessage("She is already fighting too many players.");
				return;
			}

			if (World.getWorld().getEventHandler().isRunning(c, "dagannoth_mother")) {
				c.sendMessage("You're about to fight the mother, please wait.");
				return;
			}

			c.getDagannothMother().init();*/
			break;

		case 4577: // Lighthouse door
			if (c.getY() >= 3636)
				c.getPA().movePlayer(2509, 3635, 0);
			else
				c.getPA().movePlayer(2509, 3636, 0);
			break;

		case 30364: // mining guild door
			if (c.getY() == 9756) {
				c.getPA().movePlayer(3046, 9757, 0);
			}
			else if (c.getY() == 9757) {
				c.getPA().movePlayer(3046, 9756, 0);
			}
			break;

		case 4413:
			if (!Boundary.isIn(c, Boundary.LIGHTHOUSE)) {
				return;
			}
			/*mother = c.getDagannothMother();

			if (mother != null) {
				c.getDagannothMother().end(DisposeType.INCOMPLETE);
			} else {*/
			c.getPA().movePlayer(2509, 3639, 0);
			//}
			break;

		case 13642: // Lectern
			c.getDH().sendDialogues(10, -1);
			break;

		case 8930:
			c.getPA().movePlayer(1975, 4409, 3);
			break;

		case 10177: // Dagganoth kings ladder
			c.getPA().movePlayer(2900, 4449, 0);
			break;

		case 10193:
			c.getPA().movePlayer(2545, 10143, 0);
			break;

		case 10195:
			c.getPA().movePlayer(1809, 4405, 2);
			break;

		case 10196:
			c.getPA().movePlayer(1807, 4405, 3);
			break;

		case 10197:
			c.getPA().movePlayer(1823, 4404, 2);
			break;

		case 10198:
			c.getPA().movePlayer(1825, 4404, 3);
			break;

		case 10199:
			c.getPA().movePlayer(1834, 4388, 2);
			break;

		case 10200:
			c.getPA().movePlayer(1834, 4390, 3);
			break;

		case 10201:
			c.getPA().movePlayer(1811, 4394, 1);
			break;

		case 10202:
			c.getPA().movePlayer(1812, 4394, 2);
			break;

		case 10203:
			c.getPA().movePlayer(1799, 4386, 2);
			break;

		case 10204:
			c.getPA().movePlayer(1799, 4388, 1);
			break;

		case 10205:
			c.getPA().movePlayer(1796, 4382, 1);
			break;

		case 10206:
			c.getPA().movePlayer(1796, 4382, 2);
			break;

		case 10207:
			c.getPA().movePlayer(1800, 4369, 2);
			break;

		case 10208:
			c.getPA().movePlayer(1802, 4370, 1);
			break;

		case 10209:
			c.getPA().movePlayer(1827, 4362, 1);
			break;

		case 10210:
			c.getPA().movePlayer(1825, 4362, 2);
			break;

		case 10211:
			c.getPA().movePlayer(1863, 4373, 2);
			break;

		case 10212:
			c.getPA().movePlayer(1863, 4371, 1);
			break;

		case 10213:
			c.getPA().movePlayer(1864, 4389, 1);
			break;

		case 10214:
			c.getPA().movePlayer(1864, 4387, 2);
			break;

		case 10215:
			c.getPA().movePlayer(1890, 4407, 0);
			break;

		case 10216:
			c.getPA().movePlayer(1890, 4406, 1);
			break;

		case 10217:
			c.getPA().movePlayer(1957, 4373, 1);
			break;

		case 10218:
			c.getPA().movePlayer(1957, 4371, 0);
			break;

		case 10219:
			c.getPA().movePlayer(1824, 4379, 3);
			break;

		case 10220:
			c.getPA().movePlayer(1824, 4381, 2);
			break;

		case 10221:
			c.getPA().movePlayer(1838, 4375, 2);
			break;

		case 10222:
			c.getPA().movePlayer(1838, 4377, 3);
			break;

		case 10223:
			c.getPA().movePlayer(1850, 4386, 1);
			break;

		case 10224:
			c.getPA().movePlayer(1850, 4387, 2);
			break;

		case 10225:
			c.getPA().movePlayer(1932, 4378, 1);
			break;

		case 10226:
			c.getPA().movePlayer(1932, 4380, 2);
			break;

		case 10227:
			if (c.getX() == 1961 && c.getY() == 4392)
				c.getPA().movePlayer(1961, 4392, 2);
			else
				c.getPA().movePlayer(1932, 4377, 1);
			break;

		case 10228:
			c.getPA().movePlayer(1961, 4393, 3);
			break;

		case 10229:
			c.getPA().movePlayer(1912, 4367, 0);
			break;

			/**
			 * Dagannoth king entrance
			 */
		case 10230:
			if (c.getRights().isOrInherits(Right.IRONMAN) || c.getRights().isOrInherits(Right.ULTIMATE_IRONMAN) || c.getRights().isOrInherits(Right.HC_IRONMAN)) {
				c.getPA().movePlayer(2899, 4449, 4);
			} else {
				c.getPA().movePlayer(2899, 4449, 0);
			}
			break;

		case 8958:
			if (c.getX() <= 2490)
				c.getPA().movePlayer(2492, 10163, 0);
			if (c.getX() >= 2491)
				c.getPA().movePlayer(2490, 10163, 0);
			break;
		case 8959:
			if (c.getX() <= 2490)
				c.getPA().movePlayer(2492, 10147, 0);
			if (c.getX() >= 2491)
				c.getPA().movePlayer(2490, 10147, 0);
			break;
		case 8960:
			if (c.getX() <= 2490)
				c.getPA().movePlayer(2492, 10131, 0);
			if (c.getX() >= 2491)
				c.getPA().movePlayer(2490, 10131, 0);
			break;
			//
		case 26724:
			if (c.getSkills().getLevel(Skill.AGILITY) < 72) {
				c.sendMessage("You need an agility level of 72 to cross over this mud slide.");
				return;
			}
			if (c.getX() == 2427 && c.getY() == 9767) {
				c.getPA().movePlayer(2427, 9762, 0);
			} else if (c.getX() == 2427 && c.getY() == 9762) {
				c.getPA().movePlayer(2427, 9767, 0);
			}
			break;
		case 535:
			if (objectX == 3722 && objectY == 5798) {
				//	if (c.getMode().isIronman() || c.getMode().isUltimateIronman() || c.getMode().isHcIronman() || c.getMode().isGroupIronman()) {
				//c.getPA().movePlayer(3677, 5775, 4);
				//} else {
				c.getPA().movePlayer(3677, 5775, 0);
				//}
			}
			break;



		case 26720:
			if (objectX == 2427 && objectY == 9747) {
				if (c.getX() == 2427 && c.getY() == 9748) {
					c.getPA().movePlayer(2427, 9746, 0);
				} else if (c.getX() == 2427 && c.getY() == 9746) {
					c.getPA().movePlayer(2427, 9748, 0);
				}
			} else if (objectX == 2420 && objectY == 9750) {
				if (c.getX() == 2420 && c.getY() == 9751) {
					c.getPA().movePlayer(2420, 9749, 0);
				} else if (c.getX() == 2420 && c.getY() == 9749) {
					c.getPA().movePlayer(2420, 9751, 0);
				}
			} else if (objectX == 2418 && objectY == 9742) {
				if (c.getX() == 2418 && c.getY() == 9741) {
					c.getPA().movePlayer(2418, 9743, 0);
				} else if (c.getX() == 2418 && c.getY() == 9743) {
					c.getPA().movePlayer(2418, 9741, 0);
				}
			} else if (objectX == 2357 && objectY == 9778) {
				if (c.getX() == 2358 && c.getY() == 9778) {
					c.getPA().movePlayer(2356, 9778, 0);
				} else if (c.getX() == 2356 && c.getY() == 9778) {
					//c.getPA().movePlayer(2358, 9778);
				}
			} else if (objectX == 2388 && objectY == 9740) {
				if (c.getX() == 2389 && c.getY() == 9740) {
					//c.getPA().movePlayer(2387, 9740);
				} else if (c.getX() == 2387 && c.getY() == 9740) {
					//c.getPA().movePlayer(2389, 9740);
				}
			} else if (objectX == 2379 && objectY == 9738) {
				if (c.getX() == 2380 && c.getY() == 9738) {
					//c.getPA().movePlayer(2378, 9738);
				} else if (c.getX() == 2378 && c.getY() == 9738) {
					//c.getPA().movePlayer(2380, 9738);
				}
			}
			break;

		case 26721:
			if (objectX == 2358 && objectY == 9759) {
				if (c.getX() == 2358 && c.getY() == 9758) {
					//c.getPA().movePlayer(2358, 9760);
				} else if (c.getX() == 2358 && c.getY() == 9760) {
					//c.getPA().movePlayer(2358, 9758);
				}
			}
			if (objectX == 2380 && objectY == 9750) {
				if (c.getX() == 2381 && c.getY() == 9750) {
					//c.getPA().movePlayer(2379, 9750);
				} else if (c.getX() == 2379 && c.getY() == 9750) {
					//c.getPA().movePlayer(2381, 9750);
				}
			}
			break;

		case 154:
			if (objectX == 2356 && objectY == 9783) {
				if (c.getSkills().getLevel(Skill.SLAYER) < 93) {
					c.sendMessage("You need a slayer level of 93 to enter into this crevice.");
					return;
				}
				c.getPA().movePlayer(3748, 5761, 0);
			}
			break;

		case 534:
			if (objectX == 3748 && objectY == 5760) {
				c.getPA().movePlayer(2356, 9782, 0);
			}
			break;
		case 9706:
			//if(c.maRound !=2){
			c.sendMessage("@blu@Please talk to Kolodion before having access to this magical arena. He is located in Mage Bank towards the east.");
			//return;
			//}
			if (objectX == 3104 && objectY == 3956) {
				//c.getPA().startLeverTeleport(3105, 3951, 0);
			}
			break;

		case 9707:
			if (objectX == 3105 && objectY == 3952) {
				//c.getPA().startLeverTeleport(3105, 3956, 0);
			}
			break;
		case 3610:
			if (objectX == 3550 && objectY == 9695) {
				//c.getPA().startTeleport(3565, 3308, 0, "modern", false);
			}
			break;
		case 26561:
			if (objectX == 2913 && objectY == 5300) {
				c.getPA().movePlayer(2914, 5300, 1);
			}
			break;
		case 26562:
			if (objectX == 2920 && objectY == 5274) {
				c.getPA().movePlayer(2920, 5274, 0);
			}
			break;
		case 26504:
			if (objectX == 2908 && objectY == 5265) {
				//c.getGodwars().enterBossRoom(God.SARADOMIN);
			}
			break;
		case 26518:
			if (objectX == 2885 && objectY == 5333) {
				//c.getPA().movePlayer(2885, 5344);
			} else if (objectX == 2885 && objectY == 5344) {
				//c.getPA().movePlayer(2885, 5333);
			}
			break;
		case 26505:
			if (objectX == 2925 && objectY == 5332) {
				//c.getGodwars().enterBossRoom(God.ZAMORAK);
			}
		case 26503:
			if (objectX == 2863 && objectY == 5354) {
				//c.getGodwars().enterBossRoom(God.BANDOS);
			}
			break;
		case 26380:
			if (objectX == 2871 && objectY == 5270) {
				if (c.getY() == 5279) {
					c.getPA().movePlayer(2872, 5269, 0);
				} else if (c.getY() == 5269) {
					c.getPA().movePlayer(2872, 5279, 0);
				}
			}
			break;
		case 21578: // Stairs up
		case 10:
			AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3096, 9867, 0, 2);
			break;
		case 26502:
			if (objectX == 2839 && objectY == 5295) {
				//c.getGodwars().enterBossRoom(God.ARMADYL);
			}
			break;
		case 7674:
			//c.getFarming().farmPoisonBerry();
			break;
		case 172:
		case 170:
			//CrystalChest.searchChest(c);
			break;
		case 33114:
			if (c.getItems().playerHasItem(13305)) {
				//c.getItems().removeFromBank(13305, 1000, true);
				c.getItems().deleteItem(13305, 1000);
				c.startAnimation(829);
				//c.getDH().sendItemStatement("You get the urge to eat your key and without thinking, you eat it?", 15);
				c.addDamageTaken(c, 10);
				return;
			}
			if (c.getItems().playerHasItem(13303, 1)) {
				EventBossChest.execute(c);
				return;
			}
			if (c.getItems().playerHasItem(13302, 1)) {
				EnragedGraardorDrops.execute(c);
				return;
			}
			break;
		case 29335:
			if (c.getItems().playerHasItem(33592, 1)) {
				NightmareDrops.execute(c);
				return;
			}
			break;
		case 23319:
			//InfernalChest.searchChest(c);
			break;
		case 17205:
			//SlayerChest.searchChest(c);
			break;
		case 4873:
		case 26761:
			//c.getPA().startLeverTeleport(3158, 3953, 0);
			//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_LEVER);
			break;
		case 7813:
		case 3840: // Compost Bin
			//c.getFarming().handleCompostAdd();
			break;
		case 2492:
		case 15638:
		case 7479:
			c.getPA().startTeleport(3088, 3504, 0, "modern");
			break;
		case 11803:
			if (c.getRights().isOrInherits(Right.RUBY)) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 3577, 9927, 0, 2);
				c.sendMessage("<img=4> Welcome to the donators only slayer cave.");
			}
			break;
		case 17387:
			if (c.getRights().isOrInherits(Right.RUBY)) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 2125, 4913, 0, 2);
			}
			break;
		case 25824:
			c.turnPlayerTo(objectX, objectY);
			c.getDH().sendDialogues(40, -1);
			break;

		case 5097:
		case 21725:
			c.getPA().movePlayer(2636, 9510, 2);
			break;
		case 5098:
		case 21726:
			c.getPA().movePlayer(2636, 9517, 0);
			break;
		case 5094:
		case 21722:
			c.getPA().movePlayer(2643, 9594, 2);
			break;
		case 5096:
		case 21724:
			c.getPA().movePlayer(2649, 9591, 0);
			break;
		case 2320:
		case 23566:
			if (objectX == 3119 && objectY == 9964 || objectX == 3121 && objectY == 9963|| objectX == 3120 && objectY == 9963) {
				c.getPA().movePlayer(3120, 9970, 0);
			} else if (objectX == 3119 && objectY == 9969 || objectX == 3120 && objectY == 9970 ||  objectX == 3121 && objectY == 9970||  objectX == 3121 && objectY == 9969) {
				c.getPA().movePlayer(3120, 9963, 0);
			}
			break;
		case 26760:
			if (c.getX() == 3184 && c.getY() == 3945) {
				c.getDH().sendDialogues(631, -1);
			} else if (c.getX() == 3184 && c.getY() == 3944) {
				c.getPA().movePlayer(3184, 3945, 0);
			}
			break;
		case 9326:
			if (c.getSkills().getLevel(Skill.AGILITY) < 62) {
				c.sendMessage("You need an Agility level of 62 to pass this.");
				return;
			}
			if (c.getX() < 2769) {
				c.getPA().movePlayer(2775, 10003, 0);
			} else {
				c.getPA().movePlayer(2768, 10002, 0);
			}
			break;
		case 4496:
		case 4494:
			if (c.getHeight() == 2) {
				c.getPA().movePlayer(3412, 3540, 1);
			} else if (c.getHeight() == 1) {
				c.getPA().movePlayer(3418, 3540, 0);
			}
			break;
		case 9319:
			if (c.getHeight() == 0)
				c.getPA().movePlayer(c.getX(), c.getY(), 1);
			else if (c.getHeight() == 1)
				c.getPA().movePlayer(c.getX(), c.getY(), 2);
			break;

		case 9320:
			if (c.getHeight() == 1)
				c.getPA().movePlayer(c.getX(), c.getY(), 0);
			else if (c.getHeight() == 2)
				c.getPA().movePlayer(c.getX(), c.getY(), 1);
			break;
		case 4493:
			if (c.getHeight() == 0) {
				c.getPA().movePlayer(c.getX() - 5, c.getY(), 1);
			} else if (c.getHeight() == 1) {
				c.getPA().movePlayer(c.getX() + 5, c.getY(), 2);
			}
			break;

		case 4495:
			if (c.getHeight() == 1 && c.getY() > 3538 && c.getY() < 3543) {
				c.getPA().movePlayer(c.getX() + 5, c.getY(), 2);
			} else {
				c.sendMessage("I can't reach that!");
			}
			break;
		case 2623:
			if (c.getX() == 2924 && c.getY() == 9803) {
				c.getPA().movePlayer(c.getX() - 1, c.getY(), 0);
			} else if (c.getX() == 2923 && c.getY() == 9803) {
				c.getPA().movePlayer(c.getX() + 1, c.getY(), 0);
			}
			break;
		case 23955:
			AnimatedArmour.itemOnAnimator(c, 1123);
			break;
			/*case 15644:
		case 15641:
		case 24306:
		case 24309:
			if (c.getHeight() == 2) {
				// if(Boundary.isIn(c, WarriorsGuild.WAITING_ROOM_BOUNDARY) &&
				// c.heightLevel == 2) {
				//c.getWarriorsGuild().handleDoor();
				return;
				// }
			}
			if (c.getHeight() == 0) {
				if (c.getX() == 2855 || c.getX() == 2854) {
					if (c.getY() == 3546)
						c.getPA().movePlayer(c.getX(), c.getY() - 1, 0);
					else if (c.getY() == 3545)
						c.getPA().movePlayer(c.getX(), c.getY() + 1, 0);
					c.turnPlayerTo(objectX, objectY);
				}
			}
			break;*/
		case 15653:
			if (c.getY() == 3546) {
				if (c.getX() == 2877)
					c.getPA().movePlayer(c.getX() - 1, c.getY(), 0);
				else if (c.getX() == 2876)
					c.getPA().movePlayer(c.getX() + 1, c.getY(), 0);
				c.turnPlayerTo(objectX, objectY);
			}
			break;

		case 18987: // Kbd ladder
			c.getPA().movePlayer(3069, 10255, 0);
			break;
		case 1817:
			//c.getPA().startLeverTeleport(3093, 3500, 0);
			break;

		case 18988:
			c.getPA().movePlayer(3017, 3850, 0);
			break;
		case 24303:
			//c.getPA().sendMapRegion();
			if(objectX == 2840 && objectY == 3538)
				c.getPA().movePlayer(2840, 3539, 1);
			break;
		case 13523:
			c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0, "modern");
			break;
		case 4525:
			c.setInHouse(false);
			c.getPA().movePlayer(c.getMapInstance().getLeaveX(), c.getMapInstance().getLeaveY(), 0);
			break;
		case 15477://house portal
			//if(HouseData.hasHouse(c))
			//if(c.HouseLocation == "Taverly")
				HouseData.enterHouse(c, c, false);
			break;
			
		case 28822://house portal
			//if(HouseData.hasHouse(c))
			//if(c.HouseLocation == "Kourend")
				HouseData.enterHouse(c, c, false);
			break;
			
		case 26417:
			if(c.getItems().playerHasItem(954, 1)) {
			    GlobalObject go2 = new GlobalObject(26418, worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getFace() & 255, 10);
		        World.getWorld().getGlobalObjects().add(go2);
		        c.getItems().deleteItem(954, c.getItems().getItemSlot(954), 1);
			}
			break;
		case 26418:
			AgilityHandler.delayEmote(c, "", c.getX(), c.getY()-6400, 2, 2);
			break;
		case 16671:
			//c.setHeight(c.getHeight()+1);
			if(objectX == 3204 && objectY == 3207)
				AgilityHandler.delayEmote(c, "", 3205, 3209, 1, 1);
			else if(objectX == 3204 && c.getX() == 3206 && c.getY() == 3209)
				AgilityHandler.delayEmote(c, "", 3205, 3209, 1, 1);
			else if(objectX == 3092 && objectY == 3104)
				AgilityHandler.delayEmote(c, "", 3093, 3106, 1, 1);
			else if(objectX == 2839 && objectY == 3537)
				AgilityHandler.delayEmote(c, "", 2840, 3539, 1, 1);
			break;

		case 16672:
			if(objectX == 3205)
				c.getDH().sendDialogues(27419, -1);
			else if(objectX == 3092 && objectY == 3104)
				c.getDH().sendDialogues(27419, -1);
			else if(objectX == 2839 && objectY == 3537) {
				c.getDH().sendOptions("Up","Down","Cancel");
				c.dialogueAction = 16672;
				c.dialogueId = 16672;
			}
			//c.getPA().sendMapRegion();
			break;
		case 16673:
			if(objectX == 3205)
				AgilityHandler.delayEmote(c, "", 3205, 3209, 1, 2);
			else if(objectX == 3093 && objectY == 3105)
				AgilityHandler.delayEmote(c, "", 3093, 3106, 1, 2);
			break;
			case 26106:
				if(objectX == 2750 && objectY == 3509)
					AgilityHandler.delayEmote(c, "", 2750, 3513, 1, 0);
				break;
			case 25604:
				if(objectX == 2750 && objectY == 3511)
					AgilityHandler.delayEmote(c, "", 2750, 3508, 0, 0);
				break;
			case 15645: // Stairs in Keep Le Faye
				// Move the player upstairs
				c.getPA().movePlayer(2779, 3395, 2);

				if (c.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.INFILTRATING_KEEP) {
					// Update stage so they don't repeatedly trigger the initial ambush
					c.questStages[MerlinsCrystal.QUEST_ID] = MerlinsCrystal.DEFEATED_MORDRED;
					QuestAssistant.sendStages(c);

					// Spawn Sir Mordred (NPC 241) and force him to attack
					NPC mordred = World.getWorld().npcHandler.spawnNpc(c, 241, 2779, 3396, 2, 1, 39, 4, 40, 40, true, true);
					if (mordred != null) {
						mordred.forceChat("You DARE to invade MY stronghold?!?! Have at thee knave!!!");
						mordred.underAttack = true;
						mordred.killerId = c.getIndex();
						c.underAttackBy = mordred.getIndex();
					}
				}
				break;
			case 63: // Arhein's Crate in Catherby
				if (c.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.INFILTRATING_KEEP) {
					// We will map the crate sequence to ID 85600 in Arhein's dialogue class
					server.model.players.packets.dialogue.DialogueService.open(c, 1039, 85600);
				} else {
					c.sendMessage("It's just an empty crate.");
				}
				break;
		case 2643:
		case 14888:

			//JewelryMaking.mouldInterface(c);
			break;
		case 878:
			c.getDH().sendDialogues(613, -1);
			break;
		case 1733:
			if (c.getY() > 3920 && c.inWild())
				c.getPA().movePlayer(3045, 10323, 0);
			break;
		case 1734:
			if (c.getY() > 9000 && c.inWild())
				c.getPA().movePlayer(3044, 3927, 0);
			break;
		case 2466:
			if (c.getY() > 3920 && c.inWild())
				c.getPA().movePlayer(1622, 3673, 0);
			break;
		case 2467:
			c.getPA().spellTeleport(2604, 3154, 0);
			c.sendMessage("This is the dicing area. Place a bet on designated hosts.");
			break;
		case 28851:// wcgate
			if (c.getSkills().getLevel(Skill.WOODCUTTING) < 60) {
				c.sendMessage("You need a Woodcutting level of 60 to enter the Woodcutting Guild.");
				return;
			} else {
				c.getPA().movePlayer(1657, 3505, 0);
			}
			break;
		case 28852:// wcgate
			if (c.getSkills().getLevel(Skill.WOODCUTTING) < 60) {
				c.sendMessage("You need a Woodcutting level of 60 to enter the Woodcutting Guild.");
				return;
			} else {
				c.getPA().movePlayer(1657, 3504, 0);
			}
			break;
		case 2309:
			if (c.getX() == 2998 && c.getY() == 3916) {
				//c.getAgility().doWildernessEntrance(c, 2998, 3916, false);
			}
			if (c.getX() == 2998 && c.getY() == 3917) {
				c.getPA().movePlayer(2998, 3916, 0);
			}
			break;
		case 1766:
			if (c.inWild() && c.getX() == 3069 && c.getY() == 10255) {
				c.getPA().movePlayer(3017, 3850, 0);
			}
			break;
		case 1765:
			if (c.inWild() && c.getY() >= 3847 && c.getY() <= 3860) {
				c.getPA().movePlayer(3069, 10255, 0);
			}
			break;

		case 2118:
			if (Boundary.isIn(c, new Boundary(3433, 3536, 3438, 3539))) {
				c.getPA().movePlayer(3438, 3537, 0);
			}
			break;

		case 2114:
			if (Boundary.isIn(c, new Boundary(3433, 3536, 3438, 3539))) {
				c.getPA().movePlayer(3433, 3537, 1);
			}
			break;


		case 7108:
		case 7111:
			if (c.getX() == 2907 || c.getX() == 2908) {
				if (c.getY() == 9698) {
					c.getPA().walkTo(0, -1);
				} else if (c.getY() == 9697) {
					c.getPA().walkTo(0, +1);
				}
			}
			break;

		case 2119:
			if (c.getHeight() == 1) {
				if (c.getX() == 3412 && (c.getY() == 3540 || c.getY() == 3541)) {
					c.getPA().movePlayer(3417, c.getY(), 2);
				}
			}
			break;

		case 2120:
			if (c.getHeight() == 2) {
				if (c.getX() == 3417 && (c.getY() == 3540 || c.getY() == 3541)) {
					c.getPA().movePlayer(3412, c.getY(), 1);
				}
			}
			break;

		case 2102:
		case 2104:
			if (c.getHeight() == 1) {
				if (c.getX() == 3426 || c.getX() == 3427) {
					if (c.getY() == 3556) {
						c.getPA().walkTo(0, -1);
					} else if (c.getY() == 3555) {
						c.getPA().walkTo(0, +1);
					}
				}
			}
			break;

		case 1597:
		case 1596:
			// case 7408:
			// case 7407:
			if (c.getY() < 9000) {
				if (c.getY() > 3903) {
					c.getPA().movePlayer(c.getX(), c.getY() - 1, 0);
				} else {
					c.getPA().movePlayer(c.getX(), c.getY() + 1, 0);
				}
			} else if (c.getY() > 9917) {
				c.getPA().movePlayer(c.getX(), c.getY() - 1, 0);
			} else {
				c.getPA().movePlayer(c.getX(), c.getY() + 1, 0);
			}
			break;
			
			  
			

		case 24600:
			c.getDH().sendDialogues(500, -1);
			break;

		case 20973:
			c.getBarrows().useChest();
			break;

		case 20720:
		case 20721:
		case 20722:
		case 20770:
		case 20771:
		case 20772:
			c.getBarrows().spawnBrother(objectId);
			break;

		case 14315:
			//PestControl.addToLobby(c);
			break;

		case 14314:
			//PestControl.removeFromLobby(c);
			break;

		case 14235:
		case 14233:
			if (objectX == 2670) {
				if (c.getX() <= 2670) {
					c.setX(2671);
				} else {
					c.setX(2670);
				}
			}
			if (objectX == 2643) {
				if (c.getX() >= 2643) {
					c.setX(2642);
				} else {
					c.setX(2643);
				}
			}
			if (c.getX() <= 2585) {
				c.setY(c.getY() + 1);
			} else {
				c.setY(c.getY() - 1);
			}
			c.getPA().movePlayer(c.getX(), c.getY(), 0);
			break;

		case 245:
			c.getPA().movePlayer(c.getX(), c.getY() + 2, 2);
			break;
		case 246:
			c.getPA().movePlayer(c.getX(), c.getY() - 2, 1);
			break;
		case 272:
			c.getPA().movePlayer(c.getX(), c.getY(), 1);
			break;
		case 273:
			c.getPA().movePlayer(c.getX(), c.getY(), 0);
			break;
			/* Godwars Door */
			/*
			 * case 26426: // armadyl if (c.absX == 2839 && c.absY == 5295) {
			 * c.getPA().movePlayer(2839, 5296, 2);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2839, 5295, 2); } break; case 26425: // bandos if
			 * (c.absX == 2863 && c.absY == 5354) { c.getPA().movePlayer(2864, 5354, 2);
			 * c.sendMessage( "@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2863, 5354, 2); } break; case 26428: // bandos if
			 * (c.absX == 2925 && c.absY == 5332) { c.getPA().movePlayer(2925, 5331, 2);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2925, 5332, 2); } break; case 26427: // bandos if
			 * (c.absX == 2908 && c.absY == 5265) { c.getPA().movePlayer(2907, 5265, 0);
			 * c.sendMessage("@blu@May the gods be with you."); } else {
			 * c.getPA().movePlayer(2908, 5265, 0); } break;
			 */

		case 5960:
			//if (!c.leverClicked) {
			//c.getDH().sendDialogues(114, 9985);
			//c.leverClicked = true;
			//} else {
			//c.getPA().startLeverTeleport(3090, 3956, 0);
			//}
			break;
		case 5959:
			//c.getPA().startLeverTeleport(2539, 4712, 0);
			break;
		case 1815:
			c.getPA().startLeverTeleport(3087, 3500, 0);
			break;
		case 1816:
			c.getPA().startLeverTeleport(2271, 4680, 0);
			//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.KBD_LAIR);
			break;
			/* Start Brimhavem Dungeon */
		case 2879:
			c.getPA().movePlayer(2542, 4718, 0);
			break;
		case 2878:
			c.getPA().movePlayer(2509, 4689, 0);
			break;
		case 5083:
			c.getPA().movePlayer(2713, 9564, 0);
			c.sendMessage("You enter the dungeon.");
			break;

		case 5103:
			if (c.getX() == 2691 && c.getY() == 9564) {
				c.getPA().movePlayer(2689, 9564, 0);
			} else if (c.getX() == 2689 && c.getY() == 9564) {
				c.getPA().movePlayer(2691, 9564, 0);
			}
			break;

		case 5106:
		case 21734:
			if (c.getX() == 2674 && c.getY() == 9479) {
				c.getPA().movePlayer(2676, 9479, 0);
			} else if (c.getX() == 2676 && c.getY() == 9479) {
				c.getPA().movePlayer(2674, 9479, 0);
			}
			break;
		case 5105:
		case 21733:
			if (c.getX() == 2672 && c.getY() == 9499) {
				c.getPA().movePlayer(2674, 9499, 0);
			} else if (c.getX() == 2674 && c.getY() == 9499) {
				c.getPA().movePlayer(2672, 9499, 0);
			}
			break;

		case 5107:
		case 21735:
			if (c.getX() == 2693 && c.getY() == 9482) {
				c.getPA().movePlayer(2695, 9482, 0);
			} else if (c.getX() == 2695 && c.getY() == 9482) {
				c.getPA().movePlayer(2693, 9482, 0);
			}
			break;

		case 21731:
			if (c.getX() == 2691) {
				c.getPA().movePlayer(2689, 9564, 0);
			} else if (c.getX() == 2689) {
				c.getPA().movePlayer(2691, 9564, 0);
			}
			break;

		case 5104:
		case 21732:
			if (c.getX() == 2683 && c.getY() == 9568) {
				c.getPA().movePlayer(2683, 9570, 0);
			} else if (c.getX() == 2683 && c.getY() == 9570) {
				c.getPA().movePlayer(2683, 9568, 0);
			}
			break;

		case 5100:
			if (c.getY() <= 9567) {
				c.getPA().movePlayer(2655, 9573, 0);
			} else if (c.getY() >= 9572) {
				c.getPA().movePlayer(2655, 9566, 0);
			}
			break;
		case 21728:
			if (c.getSkills().getLevel(Skill.AGILITY) < 34) {
				c.sendMessage("You need an Agility level of 34 to pass this.");
				return;
			}
			if (c.getY() == 9566) {
				AgilityHandler.delayEmote(c, "CRAWL", 2655, 9573, 0, 2);
			} else {
				AgilityHandler.delayEmote(c, "CRAWL", 2655, 9566, 0, 2);
			}
			break;
		case 5099:
		case 21727:
			if (c.getSkills().getLevel(Skill.AGILITY) < 34) {
				c.sendMessage("You need an Agility level of 34 to pass this.");
				return;
			}
			if (objectX == 2698 && objectY == 9498) {
				c.getPA().movePlayer(2698, 9492, 0);
			} else if (objectX == 2698 && objectY == 9493) {
				c.getPA().movePlayer(2698, 9499, 0);
			}
			break;
		case 5088:
		case 20882:
			if (c.getSkills().getLevel(Skill.AGILITY) < 30) {
				c.sendMessage("You need an Agility level of 30 to pass this.");
				return;
			}
			c.getPA().movePlayer(2687, 9506, 0);
			break;
		case 5090:
		case 20884:
			if (c.getSkills().getLevel(Skill.AGILITY) < 30) {
				c.sendMessage("You need an Agility level of 30 to pass this.");
				return;
			}
			c.getPA().movePlayer(2682, 9506, 0);
			break;

		case 16511:
			if (c.getSkills().getLevel(Skill.AGILITY) < 51) {
				c.sendMessage("You need an agility level of at least 51 to squeeze through.");
				return;
			}
			if (c.getX() == 3149 && c.getY() == 9906) {
				//c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.OBSTACLE_PIPE);
				c.getPA().movePlayer(3155, 9906, 0);
			} else if (c.getX() == 3155 && c.getY() == 9906) {
				c.getPA().movePlayer(3149, 9906, 0);
			}
			break;

		case 5110:
		case 21738:
			if (c.getSkills().getLevel(Skill.AGILITY) < 12) {
				c.sendMessage("You need an Agility level of 12 to pass this.");
				return;
			}
			c.getPA().movePlayer(2647, 9557, 0);
			break;
		case 5111:
		case 21739:
			if (c.getSkills().getLevel(Skill.AGILITY) < 12) {
				c.sendMessage("You need an Agility level of 12 to pass this.");
				return;
			}
			c.getPA().movePlayer(2649, 9562, 0);
			break;
		//case 24222:
			//if (c.getX() == 2936)
			//	AgilityHandler.delayEmote(c, "WALL_EMOTE", -2, 0, 3);
			//else if (c.getX() == 2934)
			//	AgilityHandler.delayEmote(c, "WALL_EMOTE", 2, 0, 3);

			//c.getAD().completeAchievement("FaladorEasy", "climb over the western Falador wall", 2, 0, 2);
			//break;
		case 27362:// lizardmen
			if (c.getY() > 3688) {
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1454, 3690, 0, 2);
				c.sendMessage("You climb down into Shayzien Assault.");
			} else
				AgilityHandler.delayEmote(c, "CLIMB_DOWN", 1477, 3690, 0, 2);
			c.sendMessage("You climb down into Lizardman Camp.");
			break;
		case 4155:// zulrah
			c.getPA().movePlayer(2200, 3055, 0);
			c.sendMessage("You climb down.");
			break;
		case 5084:
			c.getPA().movePlayer(2744, 3151, 0);
			c.sendMessage("You exit the dungeon.");
			break;
			/* End Brimhavem Dungeon */
		case 6481:
			c.getPA().movePlayer(3233, 9315, 0);
			break;

			/*
			 * case 17010: if (c.playerMagicBook == 0) {
			 * c.sendMessage("You switch spellbook to lunar magic.");
			 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting =
			 * false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
			 * (c.playerMagicBook == 1) {
			 * c.sendMessage("You switch spellbook to lunar magic.");
			 * c.setSidebarInterface(6, 29999); c.playerMagicBook = 2; c.autocasting =
			 * false; c.autocastId = -1; c.getPA().resetAutocast(); break; } if
			 * (c.playerMagicBook == 2) { c.setSidebarInterface(6, 1151); c.playerMagicBook
			 * = 0; c.autocasting = false;
			 * c.sendMessage("You feel a drain on your memory."); c.autocastId = -1;
			 * c.getPA().resetAutocast(); break; } break;
			 */

		case 1551:
			if (c.getX() == 3252 && c.getY() == 3266) {
				c.getPA().movePlayer(3253, 3266, 0);
			}
			if (c.getX() == 3253 && c.getY() == 3266) {
				c.getPA().movePlayer(3252, 3266, 0);
			}
			break;
		case 1553:
			if (c.getX() == 3252 && c.getY() == 3267) {
				c.getPA().movePlayer(3253, 3266, 0);
			}
			if (c.getX() == 3253 && c.getY() == 3267) {
				c.getPA().movePlayer(3252, 3266, 0);
			}
			break;
		case 3044:
		case 24009:
		case 26300:
		case 16469:
		case 11010:
		case 14838:
		case 2030:
			c.getSmithing().sendSmelting(c, true);
			break;

		case 2097: // Anvil object
		case 2783:
		    c.getSmithing().handleAnvilClick(c);
		    break;
			/*
			 * case 2030: if (c.absX == 1718 && c.absY == 3468) {
			 * c.getSmithing().sendSmelting(); } else { c.getSmithing().sendSmelting(); }
			 * break;
			 */

			/* AL KHARID */
		case 2883:
		case 2882:
			c.getDH().sendDialogues(1023, 925);
			break;
			// case 2412:
			// Sailing.startTravel(c, 1);
			// break;
			// case 2414:
			// Sailing.startTravel(c, 2);
			// break;
			// case 2083:
			// Sailing.startTravel(c, 5);
			// break;
			// case 2081:
			// Sailing.startTravel(c, 6);
			// break;
			// case 14304:
			// Sailing.startTravel(c, 14);
			// break;
			// case 14306:
			// Sailing.startTravel(c, 15);
			// break;

		case 2213:
		case 24101:
		case 3045:
		case 14367:
		case 3193:
		case 10517:
		case 11402:
		case 26972:
		case 4483:
		case 25808:
		case 11744:
		case 10060:
		case 12309:
		case 10058:
		case 2693:
		case 21301:
		case 6943:
		case 3194:
		case 10661:
		case 10355:
		case 20950:
			c.getPA().openUpBank();
			break;

		case 21305:
			if (c.getItems().playerHasItem(10810, 5)) {
				c.getItems().deleteItem2(10810, 5);
				c.getItems().addItem(10826, 1);
				//c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.FREMENNIK_SHIELD);
			} else {
				c.sendMessage("You need 5 arctic pine logs to create a fremennik shield.");
				return;
			}
			break;

		case 21505:
		case 21507:
			if (c.getX() <= 2328) {
				c.getPA().movePlayer(2329, c.getY(), 0);
			} else if (c.getX() >= 2329) {
				c.getPA().movePlayer(2328, c.getY(), 0);
			}
			break;
		case 3506:
		case 3507:
			if (c.getY() == 3458) {
				c.getPA().movePlayer(c.getX(), 3457, 0);
				//c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.MORYTANIA_SWAMP);
			} else if (c.getY() == 3457) {
				c.getPA().movePlayer(c.getX(), 3458, 0);
			}
			break;

		case 11665:
			if (c.getX() == 2658) {
				c.getPA().movePlayer(2659, 3437, 0);
				//c.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.RANGING_GUILD);
			} else if (c.getX() == 2659) {
				c.getPA().movePlayer(2657, 3439, 0);
			}
			break;

			/**
			 * Entering the Fight Caves.
			 */
		case 11833:
			if (Boundary.entitiesInArea(Boundary.FIGHT_CAVE) >= 50) {
				c.sendMessage("There are too many people using the fight caves at the moment. Please try again later");
				return;
			}
			c.getDH().sendDialogues(633, -1);
			break;
		case 32653:
			if (Boundary.isIn(c, Boundary.THEATRE_LOBBY_ENTRANCE)) {
				LobbyManager.get(LobbyType.THEATRE_OF_BLOOD)
				.ifPresent(lobby -> lobby.attemptJoin(c));
				break;
			}
			break;
		case 30396: //Raids Lobbies
			if (Boundary.isIn(c, Boundary.XERIC_LOBBY_ENTRANCE)) {
				LobbyManager.get(LobbyType.TRIALS_OF_XERIC)
				.ifPresent(lobby -> lobby.attemptJoin(c));
				break;
			}
			if  (Boundary.isIn(c, Boundary.XERIC_LOBBY)) {
				LobbyManager.get(LobbyType.TRIALS_OF_XERIC)
				.ifPresent(lobby -> lobby.attemptLeave(c));
				break;			
			}
			if (Boundary.isIn(c, Boundary.RAIDS_LOBBY_ENTRANCE)) {
				LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC)
				.ifPresent(lobby -> lobby.attemptJoin(c));
				break;
			}
			if  (Boundary.isIn(c, Boundary.RAIDS_LOBBY)) {
				LobbyManager.get(LobbyType.CHAMBERS_OF_XERIC)
				.ifPresent(lobby -> lobby.attemptLeave(c));
				break;			
			}
			if (Boundary.isIn(c, Boundary.THEATRE_LOBBY_ENTRANCE)) {
				LobbyManager.get(LobbyType.THEATRE_OF_BLOOD)
				.ifPresent(lobby -> lobby.attemptJoin(c));
				break;
			}
			if (Boundary.isIn(c, Boundary.THEATRE_LOBBY)) {
				LobbyManager.get(LobbyType.THEATRE_OF_BLOOD)
				.ifPresent(lobby -> lobby.attemptLeave(c));
				break;
			}
			System.out.println("LOBBY OBJECT JOIN FAILURE! NO CONDITION MET!");
			c.sendMessage("This Lobby is not yet in use! New minigame coming soon!");
			break;


			/*case 29879:		
		if (!Boundary.isIn(c, Boundary.RAIDS_LOBBY)) {
			c.getPA().movePlayer((3040+Misc.random(-3,3)), (9936+Misc.random(-3,3)));
			c.getRaids();
			Raids.joinRaidLobby(c);
			break;
		}
		if  (Boundary.isIn(c, Boundary.RAIDS_LOBBY)) {
			Player.move(c, 0, 2);
			XericLobby.removePlayer(c);
			c.sendMessage("You have left the Raids lobby");		
			break;	
		}*/

		case 20667:
		case 20668:
		case 20669:
		case 20670:
		case 20671:
		case 20672:
			c.getBarrows().moveUpStairs(objectId);
			break;

			/**
			 * Clicking on the Ancient Altar.
			 */
		case 6552:
			if (c.inWild()) {
				return;
			}
			c.autocasting = false;
			c.autocastId = -1;
			c.getPA().resetAutocast();
			if (c.getY() == 9312) {
				//c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.ACTIVATE_ANCIENT);
			}
			//PlayerAssistant.switchSpellBook(c);
			break;

			/**
			 * c.setSidebarInterface(6, 1151); Recharing prayer points.
			 */
		case 27501:
		case 20377:
			if (c.inWild()) {
				return;
			}
			if (c.getSkills().getLevel(Skill.PRAYER) < c.getSkills().getActualLevel(Skill.PRAYER)) {

				if(c.getSkills().getActualLevel(Skill.PRAYER) >= 85) {
					//c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.PRAY_SOPHANEM);
				}
				c.startAnimation(645);
				c.getSkills().resetToActualLevel(Skill.PRAYER);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().refreshSkill(5);
			} else {
				c.sendMessage("You already have full prayer points.");
			}
			break;
		case 61:
			if (c.inWild()) {
				return;
			}
			if (c.getY() >= 3508 && c.getY() <= 3513) {
				if (c.getSkills().getLevel(Skill.PRAYER) < c.getSkills().getActualLevel(Skill.PRAYER)) {
					if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)
							/*&& c.getDiaryManager().getVarrockDiary().hasCompleted("HARD")*/) {
						if (c.prayerActive[25]) {
							//	c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_PIETY);
						}
					}
					c.startAnimation(645);
					c.getSkills().resetToActualLevel(Skill.PRAYER);
					c.sendMessage("You recharge your prayer points.");
					c.getPA().refreshSkill(5);
				} else {
					c.sendMessage("You already have full prayer points.");
				}
			}
			break;

		case 410:
			if (c.inWild()) {
				return;
			}
			if (c.getSkills().getLevel(Skill.PRAYER) == c.getSkills().getActualLevel(Skill.PRAYER)) {
				c.sendMessage("You already have full prayer points.");
				return;
			}
			if (Boundary.isIn(c, Boundary.TAVERLY_BOUNDARY)) {
				if (c.getItems().isWearingItem(5574) && c.getItems().isWearingItem(5575)
						&& c.getItems().isWearingItem(5576)) {
					//c.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.ALTAR_OF_GUTHIX);
				}
			}
			c.startAnimation(645);
			c.getSkills().resetToActualLevel(Skill.PRAYER);
			c.sendMessage("You recharge your prayer points.");
			c.getPA().refreshSkill(5);
			break;

		case 409:
			if (c.inWild()) {
				return;
			}
			if (c.getSkills().getLevel(Skill.PRAYER) == c.getSkills().getActualLevel(Skill.PRAYER)) {
				c.sendMessage("You already have full prayer points.");
				return;
			}
			if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
				if (c.prayerActive[23]) {
					//c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_SMITE);
				}
			}
			if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
    			c.getAD().completeAchievement("ArdougneEasy", "Use the altar in East Ardougne's church", 4);
			}
			c.startAnimation(645);
			c.getSkills().resetToActualLevel(Skill.PRAYER);
			c.sendMessage("You recharge your prayer points.");
			c.getPA().refreshSkill(5);
			break;
		case 6817:
		case 14860:
			if (c.inWild()) {
				return;
			}
			if (c.getSkills().getLevel(Skill.PRAYER) == c.getSkills().getActualLevel(Skill.PRAYER)) {
				c.sendMessage("You already have full prayer points.");
				return;
			}
			if (Boundary.isIn(c, Boundary.VARROCK_BOUNDARY)) {
				if (c.prayerActive[23]) {
					//c.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.PRAY_WITH_SMITE);
				}
			}
			if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
				if (c.prayerActive[25]) {
					/*if (!c.getDiaryManager().getArdougneDiary().hasCompleted("MEDIUM")) {
						c.sendMessage("You must have completed all the medium tasks in the ardougne diary to do this.");
						return;
					}
					c.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.PRAY_WITH_CHIVALRY);
					 */}
			}
			c.startAnimation(645);
			c.getSkills().resetToActualLevel(Skill.PRAYER);
			c.sendMessage("You recharge your prayer points.");
			c.getPA().refreshSkill(5);
			break;

		case 411:
			if (c.getSkills().getLevel(Skill.PRAYER) < c.getSkills().getActualLevel(Skill.PRAYER)) {
				if (c.inWild()) {
					//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.WILDERNESS_ALTAR);
				}
				c.startAnimation(645);
				c.getSkills().resetToActualLevel(Skill.PRAYER);
				c.sendMessage("You recharge your prayer points.");
				c.getPA().refreshSkill(5);
			} else {
				c.sendMessage("You already have full prayer points.");
			}
			break;

		case 14896:
			c.turnPlayerTo(objectX, objectY);
			//FlaxPicking.getInstance().pick(c, new Location(objectX, objectY, c.getHeight()));
			break;


		case 26366: // Godwars altars
		case 26365:
		case 26364:
		case 26363:
			if (c.inWild()) {
				return;
			}
			/*if (c.gwdAltar > 0) {
				int seconds = ((int) Math.floor(c.gwdAltar * 0.6));
				c.sendMessage("You have to wait another " + seconds + " seconds to use this altar.");
				return;
			}*/
			if (c.getSkills().getLevel(Skill.PRAYER) < c.getSkills().getActualLevel(Skill.PRAYER)) {
				c.startAnimation(645);
				c.getSkills().resetToActualLevel(Skill.PRAYER);
				c.sendMessage("You recharge your prayer points.");
				//c.gwdAltar = 600;
				c.getPA().refreshSkill(5);
			} else {
				c.sendMessage("You already have full prayer points.");
			}
			break;

			/**
			 * Aquring god capes.
			 */
		case 2873:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Saradomin blesses you with a cape.");
				c.getItems().addItem(2412, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;
		case 2875:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Guthix blesses you with a cape.");
				c.getItems().addItem(2413, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;
		case 2874:
			if (!c.getItems().ownsCape()) {
				c.startAnimation(645);
				c.sendMessage("Zamorak blesses you with a cape.");
				c.getItems().addItem(2414, 1);
			} else {
				c.sendMessage("You already have a cape");
			}
			break;

			/**
			 * Oblisks in the wilderness.
			 */
		case 14829:
		case 14830:
		case 14827:
		case 14828:
		case 14826:
		case 14831:

			break;

			/**
			 * Clicking certain doors.
			 */
		case 6749:
			if (objectX == 3562 && objectY == 9678) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (objectX == 3558 && objectY == 9677) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;

		case 6730:
			if (objectX == 3558 && objectY == 9677) {
				c.getPA().object(3562, 9678, 6749, -3, 0);
				c.getPA().object(3562, 9677, 6730, -1, 0);
			} else if (objectX == 3558 && objectY == 9678) {
				c.getPA().object(3558, 9677, 6749, -1, 0);
				c.getPA().object(3558, 9678, 6730, -3, 0);
			}
			break;

		case 6727:
			if (objectX == 3551 && objectY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;

		case 6746:
			if (objectX == 3552 && objectY == 9684) {
				c.sendMessage("You cant open this door..");
			}
			break;

		case 6748:
			if (objectX == 3545 && objectY == 9678) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (objectX == 3541 && objectY == 9677) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;

		case 6729:
			if (objectX == 3545 && objectY == 9677) {
				c.getPA().object(3545, 9678, 6748, -3, 0);
				c.getPA().object(3545, 9677, 6729, -1, 0);
			} else if (objectX == 3541 && objectY == 9678) {
				c.getPA().object(3541, 9677, 6748, -1, 0);
				c.getPA().object(3541, 9678, 6729, -3, 0);
			}
			break;

		case 6726:
			if (objectX == 3534 && objectY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (objectX == 3535 && objectY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;

		case 6745:
			if (objectX == 3535 && objectY == 9684) {
				c.getPA().object(3534, 9684, 6726, -4, 0);
				c.getPA().object(3535, 9684, 6745, -2, 0);
			} else if (objectX == 3534 && objectY == 9688) {
				c.getPA().object(3535, 9688, 6726, -2, 0);
				c.getPA().object(3534, 9688, 6745, -4, 0);
			}
			break;

		case 6743:
			if (objectX == 3545 && objectY == 9695) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (objectX == 3541 && objectY == 9694) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;

		case 6724:
			if (objectX == 3545 && objectY == 9694) {
				c.getPA().object(3545, 9694, 6724, -1, 0);
				c.getPA().object(3545, 9695, 6743, -3, 0);
			} else if (objectX == 3541 && objectY == 9695) {
				c.getPA().object(3541, 9694, 6724, -1, 0);
				c.getPA().object(3541, 9695, 6743, -3, 0);
			}
			break;

		case 1516:
		case 1519:
			if (objectY == 9698) {
				if (c.getY() >= objectY)
					c.getPA().walkTo(0, -1);
				else
					c.getPA().walkTo(0, 1);
				break;
			}

		case 11737:
			if (!c.getRights().isOrInherits(Right.SAPPHIRE)) {
				return;
			}
			c.getPA().movePlayer(3365, 9641, 0);
			break;

			// case 12355:
			// if (!c.getRights().isOrInherits(Right.CONTRIBUTOR)) {
			// return;
			// }
			// c.getPA().movePlayer(3577, 9927, 0);
			// break;

		case 5126:
		case 2100:
			if (c.getY() == 3554)
				c.getPA().walkTo(0, 1);
			else
				c.getPA().walkTo(0, -1);
			break;

		case 1759:
			if (objectX == 2884 && objectY == 3397)
				c.getPA().movePlayer(c.getX(), c.getY() + 6400, 0);
			break;
		case 1557:
		case 7169:
			if ((objectX == 3106 || objectX == 3105) && objectY == 9944) {
				if (c.getY() > objectY)
					c.getPA().walkTo(0, -1);
				else
					c.getPA().walkTo(0, 1);
			} else {
				if (c.getX() > objectX)
					c.getPA().walkTo(-1, 0);
				else
					c.getPA().walkTo(1, 0);
			}
			break;
		case 2558:
			c.sendMessage("This door is locked.");
			break;



		case 9294:
			if (c.getX() < objectX) {
				c.getPA().movePlayer(objectX + 1, c.getY(), 0);
			} else if (c.getX() > objectX) {
				c.getPA().movePlayer(objectX - 1, c.getY(), 0);
			}
			break;

		case 9293:
			if (c.getX() < objectX) {
				c.getPA().movePlayer(2892, 9799, 0);
			} else {
				c.getPA().movePlayer(2886, 9799, 0);
			}
			break;

		case 7407:
			GlobalObject gate1;
			gate1 = new GlobalObject(objectId, objectX, objectY, c.getHeight(), 2, 0, 50, 7407);
			World.getWorld().getGlobalObjects().add(gate1);
			break;

		case 7408:
			GlobalObject secondGate;
			secondGate = new GlobalObject(objectId, objectX, objectY, c.getHeight(), 0, 0, 50, 7408);
			World.getWorld().getGlobalObjects().add(secondGate);
			break;

		case 11766://boneman
			GlobalObject boneman_gate;
			boneman_gate = new GlobalObject(objectId, objectX, objectY, c.getHeight(), 1, 0, 50, 11766);
			World.getWorld().getGlobalObjects().add(boneman_gate);
			break;

		case 11767:
			GlobalObject second_boneman_gate;
			second_boneman_gate = new GlobalObject(objectId, objectX, objectY, c.getHeight(), 3, 0, 50, 11767);
			World.getWorld().getGlobalObjects().add(second_boneman_gate);
			break;

			/**
			 * Forfeiting a duel.
			 */
		case 3203:
			DuelSession session = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (Objects.isNull(session)) {
				return;
			}
			if (!Boundary.isIn(c, Boundary.DUEL_ARENA)) {
				return;
			}
			if (session.getRules().contains(Rule.FORFEIT)) {
				c.sendMessage("You are not permitted to forfeit the duel.");
				return;
			}
			break;

		}
	}

}