package server.model.items;

import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;

import server.model.players.Player;
import server.model.players.content.Skillcapes.SkillcapePerks;
import server.model.players.packets.objectoptions.impl.DarkAltar;
import server.model.players.skills.farming.*;
import server.model.players.skills.firemake.Firemaking;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.model.players.quests.*;
import server.model.players.skills.Fletching;
import server.model.players.skills.Shearing;
import server.model.players.skills.cooking.*;
import server.model.players.packets.dialogue.npc.Bob;
import server.util.Misc;
import server.world.World;
import server.model.minigames.warriors_guild.AnimatedArmour;
import server.model.npcs.NPC;
import server.model.objects.Object;
import server.clip.ObjectDef;
import server.clip.Region;
import server.clip.WorldObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

/**
 * 
 * @author Ryan / Lmctruck30
 * 
 */

public class UseItem {

	public static void ItemonObject(Player c, int objectID, int objectX,
			int objectY, int itemId) {
		c.objectX = objectX;
		c.objectY = objectY;
		if (!c.getItems().playerHasItem(itemId, 1))
			return;
		Optional<WorldObject> existingObject = Region.getWorldObject(objectID, objectX, objectY, c.getLocation().getZ(), 10);
		WorldObject targetObject;
		if(!existingObject.isPresent()) {
			targetObject = new WorldObject(objectID, objectX, objectY, c.getLocation().getZ(), 10, /* face = */ 0);
		    if(c.debugMessage) {
				c.sendMessage("z: " + c.getLocation().getZ());
				List<WorldObject> existingObjs = Region.getWorldObjectsAt(objectX, objectY, c.getLocation().getZ());
				existingObjs.forEach(obj -> c.sendMessage("OBJ " + obj.id + " : " + obj.type + " : " + obj.face));
			}
			//return;
		} else {
		    targetObject = existingObject.get();
			if(c.debugMessage) {
				c.sendMessage("Exists " + existingObject.get().toString());
			}
		}
		ObjectDef objectDef = ObjectDef.forID(objectID);
		if(objectDef != null) {
		int direction = targetObject.getFace();
		int objectWidth;
		int objectLength;
		if (direction != 1 && direction != 3) {
			objectWidth = objectDef.width;
			objectLength = objectDef.length;
		} else {
			objectWidth = objectDef.length;
			objectLength = objectDef.width;
		}
		
		if (!c.goodDistance(objectX, objectY, c.getX(), c.getY(), objectWidth, objectLength))
			return;
		}
		//if (!c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1))
			//return;
			if (c.getRunecrafting().isTalisman(itemId))
				c.getRunecrafting().TalismanOnAltar(itemId, objectID);

		if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
			if (Farming.prepareCrop(c, itemId, objectID, objectX, objectY)) {
				return;
			}
		}
		if (c.getCompost().handleItemOnObject(itemId, objectID, objectX, objectY)) {
			return;
		}
		if (objectID == 24961) {
		    if (c.millHopperType > 0) {
		        String item = (c.millHopperType == 1) ? "grain" : "sweetcorn";
		        c.getDH().sendStatement("There is already " + item + " in the hopper.");
		        return;
		    }

		    if (itemId == 1947 || itemId == 5986) {
		        c.getItems().deleteItem(itemId, 1);
		        c.millHopperType = (itemId == 1947) ? 1 : 2;
		        c.getDH().sendStatement("You put the " + (itemId == 1947 ? "grain" : "sweetcorn") + " in the hopper.", 
		                                "You should now pull the lever nearby to operate the hopper.");
		    }
		}
		if (itemId == RestlessGhost.GHOSTS_SKULL && objectID == 15052) { 
            if (c.questStages[RestlessGhost.QUEST_ID] == RestlessGhost.GOT_SKULL) {
                c.getItems().deleteItem(RestlessGhost.GHOSTS_SKULL, 1);
                
                // Change the coffin back to the "With Skull" variant
                c.getPA().object(15053, objectX, objectY, c.HeightLevel, 0);
                
                c.sendMessage("You place the skull in the coffin.");
                
                // Finish the quest
                new RestlessGhost(c).setStage(RestlessGhost.COMPLETED);
                new RestlessGhost(c).giveRewards();
            }
        } else if (itemId == RestlessGhost.GHOSTS_SKULL && objectID == 2145) {
            c.sendMessage("Maybe I should open it first.");
        }
		switch (objectID) 
		{

		case 1781: // Flour Bin
			if(c.getItems().playerHasItem(1931) && itemId == 1931) {
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
		case 29166:
			if(itemId == 9756 && c.getItems().playerHasItem(9756, 1)) {
				c.getPA().object(29213, objectX, objectY, 2, 10);
				c.getItems().deleteItem(9756, 1);
			}
			break;
		case 1804:
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), 1, 1)) {
				if (objectX == 3115 && objectY == 3450 && itemId == 983 && c.getX() == 3115 && c.getY() == 3449) {
					c.sendMessage("You unlock the door..");
					c.getPA().object(objectID, objectX, objectY, 0, 0);
								c.getPA().walkTo(0, 1);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
								c.sendMessage("..and walk through.");
								container.stop();
						}
	
						@Override
						public void stop() {
	
						c.getPA().object(objectID, objectX, objectY, 3, 0);
						}
	
					}, 3);
				}
			}
			break;
		case 26674:
		    if (itemId == 12011) {
		        MotherlodeMine.addToHopper(c);
		    }
		    break;


		case 28900:
			switch (itemId) {
			case 19675:
				DarkAltar.handleRechargeArcLight(c);
				break;
			case 6746:
				DarkAltar.handleDarklightTransaction(c);
			}
			break;
			case 26115:
			if(c.questStages[EleWorkShop.QUEST_ID] > 1 && itemId == 2887 && c.getY() == objectY){
					//c.getPA().object(-1, objectX, objectY, 0, 0);
					//c.getPA().object(objectID, objectX, objectY+1, 2, 0);
					c.getPA().walkTo(0, 1);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
								container.stop();
						}

						@Override
						public void stop() {

						//c.getPA().object(-1, objectX, objectY+1, 0, 0);
						//c.getPA().object(objectID, objectX, objectY, 1, 0);
						}

					}, 2);
			}
			break;
			/*case 2452:
			if (c.getRunecrafting().isTalisman(itemId))
				c.getRunecrafting().TalismanOnAltar(itemId, objectID);
			break;*/
			case 3402: // Workbench
			    // Check for Elemental Bar (2893), Hammer (2347), and the Slashed Book (9715)
			    if (c.getItems().playerHasItem(2893, 1) && c.getItems().playerHasItem(2347, 1) && c.getItems().playerHasItem(9715, 1) && itemId == 2893) {
			        if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.SMELTED_BAR) {
			            c.getItems().deleteItem(2893, 1);
			            c.startAnimation(898);
			            c.sendMessage("Following the instructions in the book, you forge an elemental shield.");
			            
			            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			                @Override
			                public void execute(CycleEventContainer container) {
			                    container.stop();
			                }

			                @Override
			                public void stop() {
			                    c.getItems().addItem(2890, 1); // Elemental Shield
			                    new EleWorkShop(c).giveRewards(); // Call reward method
			                    c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.COMPLETED; // Set stage to 12
			                }
			            }, 3);
			        }
			    } else if (!c.getItems().playerHasItem(9715)) {
			        c.sendMessage("You need to have the slashed book with you to know how to forge this.");
			    }
			    break;

			case 3410: // Furnace
			    switch(itemId) { 
			        case 2889: // Bowl of lava
			            if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.FOUND_BOWL) {
			                c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.FURNACE_HEATED;
			                c.getItems().deleteItem(2889, 1);
			                c.getItems().addItem(2888, 1); // Empty bowl
			                c.sendMessage("You heat the furnace with the lava. It begins to glow orange.");
			                c.getPA().sendConfig(299, 215 << 3);
			                c.EleWorkshopWater_stage = 215; // Marking furnace as hot
			            }
			            break;
			            
			        case 453: // Coal
			        case 2892: // Elemental Ore
			            // Check if furnace is hot and player has 4 coal + 1 ore
			            if (c.getItems().playerHasItem(453, 4) && c.getItems().playerHasItem(2892, 1) && c.EleWorkshopWater_stage == 215) {
			                if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.OBTAINED_ORE) {
			                    c.sendMessage("You place the elemental ore and four heaps of coal into the furnace.");
			                    c.startAnimation(899);
			                    c.getItems().deleteItem(453, 4);
			                    c.getItems().deleteItem(2892, 1);
			                    
			                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			                        @Override
			                        public void execute(CycleEventContainer container) {
			                            c.getItems().addItem(2893, 1); // Elemental Bar
			                            c.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.SMELTED_BAR;
			                            c.sendMessage("You retrieve a bar of elemental metal.");
			                            container.stop();
			                        }
			                        @Override
			                        public void stop() {}
			                    }, 2);
			                }
			            } else if (c.EleWorkshopWater_stage != 215) {
			                c.sendMessage("The furnace isn't hot enough. You need to add lava first.");
			            }
			            break;
			    }
			    break;

			case 18519: case 18520: case 18521: case 18522: // Lava Trough
			    if (itemId == 2888) { // Stone bowl
			        if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.BELLOWS_PUMPING) {
			             // Technically they need to search the boxes first to get to stage 8
			             c.sendMessage("I should find a bowl before trying to scoop up lava.");
			        } else if (c.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.FOUND_BOWL) {
			            c.getItems().deleteItem(2888, 1);
			            c.getItems().addItem(2889, 1);
			            c.sendMessage("You fill the bowl with hot lava.");
			        }
			    }
			    break;
		case 12269:
		case 2732:
		case 114:
		case 2727:
		case 385:
		case 14919:
		case 2728:
		case 9682:
		case 5249:
		case 9736:
		case 26180:
		case 26185:
			if (c.getX() == 3014 && c.getY() > 3235 && c.getY() < 3238
					|| c.getX() == 3012 && c.getY() == 3239 || c.getX() == 3020
					&& c.getY() > 3236 && c.getY() < 3239 || c.getX() > 2805
					&& c.getX() < 2813 || c.getY() > 3437 && c.getY() < 3442) {
				return;
			}
			if (c.tutorialProgress < 36) {
				CookingTutorialIsland.cookThisFood(c, itemId, objectID);
			} else {
				c.getCooking().startCooking(c, itemId, objectID, objectX, objectY);
				//c.getCooking().itemOnObject(itemId);
			}
			break;
		case 10082:
			if (itemId == 438 || itemId == 436) {
				if (c.getItems().playerHasItem(438) && c.getItems().playerHasItem(436)) {
					if (c.tutorialProgress == 19) {
						c.startAnimation(899);
						c.getPA().sendSound(352, 100, 1);
						c.sendMessage("You smelt the copper and tin together in the furnace.");
						c.getItems().deleteItem(438, 1);
						c.getItems().deleteItem(436, 1);
						c.sendMessage("You retrieve a bar of bronze.");
						c.getItems().addItem(2349, 1);
						c.getDH().sendDialogues(3062, -1);
					} else if (c.tutorialProgress > 19) {
						c.startAnimation(899);
						c.getPA().sendSound(352, 100, 1);
						c.sendMessage("You smelt the copper and tin together in the furnace.");
						c.getItems().deleteItem(438, 1);
						c.getItems().deleteItem(436, 1);
						c.sendMessage("You retrieve a bar of bronze.");
						c.getItems().addItem(2349, 1);
					}
				}
			}
		break;
		case 24004: // west falador water pump
		    // List of fillable item IDs and their filled versions
		    int[][] fillables = {
		        {1925, 1929}, // bucket -> bucket of water
		        {229, 227},   // vial -> vial of water
		        {1935, 1937}, // jug -> jug of water
		        // add more pairs as needed
		    };

		    // Find the first fillable the player has
		    int fillSlot = -1, fillableId = -1, filledId = -1;
		    for (int[] pair : fillables) {
		        if (c.getItems().playerHasItem(pair[0])) {
		            fillableId = pair[0];
		            filledId = pair[1];
		            fillSlot = c.getItems().getItemSlot(pair[0]);
		            break;
		        }
		    }
		    if (fillableId == -1) {
		        c.sendMessage("You have nothing suitable to fill.");
		        return;
		    }

		    c.turnPlayerTo(objectX, objectY);

		    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
		        @Override
		        public void execute(CycleEventContainer container) {
		            boolean found = false;
		            for (int[] pair : fillables) {
		                if (c.getItems().playerHasItem(pair[0])) {
		                    found = true;
		                    c.getItems().deleteItem(pair[0], 1);
		                    c.getItems().addItem(pair[1], 1);
		                    c.startAnimation(832);
		                    if (pair[0] == 1925) {
		                        c.getAD().completeAchievement("FaladorEasy", "Fill a bucket from the pump north of Falador west bank", 5);
		                    }
		                    c.sendMessage("You fill your " + c.getItems().getItemName(pair[0]).toLowerCase() + " with water.");
		                    break; // Only fill one per tick
		                }
		            }
		            // Stop when none left
		            if (!found) container.stop();
		        }
		        @Override
		        public void stop() {
		            // Optional: cleanup
		        }
		    }, 2); // 2 ticks per fill (adjust as needed)
		    break;
		case 2097:
		case 2783:
			c.getSmithingInt().showSmithInterface(itemId);
			break;	
			case 29728:
				//c.getPA().object(29727, c.objectX, c.objectY, 0, 0);
					new Object(29729, c.objectX, c.objectY, c.getHeight(), 0,
							10, 29728, 15);
				break;

			case 23955:
				AnimatedArmour.itemOnAnimator(c, itemId);
				break;
		case 409:
			if (c.getPrayer().isBone(itemId))
				c.getPrayer().bonesOnAltar(itemId);
			break;
			
		default:
			if (c.playerRights == 3)
				Misc.println("Player At Object id: " + objectID
						+ " with Item id: " + itemId);
			break;
		}
		// Pirate's Treasure - Stashing Rum in Luthas' Crate
        if (itemId == PiratesTreasure.KARAMJAN_RUM && objectID == 2072) {
            if (c.questStages[PiratesTreasure.QUEST_ID] == PiratesTreasure.GATHERING_RUM) {
                if (!c.rumInCrate) {
                    c.getItems().deleteItem(PiratesTreasure.KARAMJAN_RUM, 1);
                    c.rumInCrate = true;
                    c.sendMessage("You stash the rum in the crate. Now you need to hide it with bananas.");
                } else {
                    c.sendMessage("There is already rum stashed in this crate.");
                }
            } else {
                c.sendMessage("You have no reason to put that in there.");
            }
            return;
        }
        
        // cow milking
        if (itemId == 1925 && objectID == 8689) {
                c.sendMessage("You milk the cow.");
                c.getItems().deleteItem(1925, 1);
                c.getItems().addItem(1927, 1);
        }
     // Leather on Bellows (3407)
        if (itemId == 1741 && objectID == 3407) {
            if (c.getItems().playerHasItem(1733) && c.getItems().playerHasItem(1734)) {
                if (c.questStages[EleWorkShop.QUEST_ID] == 5) {
                    c.questStages[EleWorkShop.QUEST_ID] = 6;
                    c.getItems().deleteItem(1741, 1);
                    c.sendMessage("You repair the hole in the bellows.");
                }
            }
        }
        if (itemId == PiratesTreasure.HECTORS_KEY && objectID == 2079) {
            if (c.questStages[PiratesTreasure.QUEST_ID] == PiratesTreasure.FIND_HECTORS_CHEST) {
                c.sendMessage("You unlock the chest and find a pirate message inside.");
                c.getItems().deleteItem(PiratesTreasure.HECTORS_KEY, 1);
                c.getItems().addItem(PiratesTreasure.PIRATE_MESSAGE, 1);
                new PiratesTreasure(c).setStage(PiratesTreasure.DIG_FOR_TREASURE);
            } else {
                c.sendMessage("You have no reason to open this chest right now.");
            }
            return;
        }
	}

	public static void ItemonItem(Player c, int itemUsed, int useWith) {
		if (c.tutorialProgress < 36) {
			if (itemUsed == 1929 && useWith == 1933 || itemUsed == 1933 && useWith == 1929) {
				c.getItems().deleteItem(1929, 1);
				c.getItems().deleteItem(1933, 1);
				c.getItems().addItem(2307, 1);
				c.getItems().addItem(1925, 1);
				c.getItems().addItem(1931, 1);
				if (c.tutorialProgress == 8) {
					c.getDH().sendDialogues(3026, 0);
				}
			}
		}
		if (itemUsed == 987 && useWith == 985 || itemUsed == 985 && useWith == 987) {
			c.getItems().deleteItem(987, 1);
			c.getItems().deleteItem(985, 1);
			c.getItems().addItem(989, 1);
	        c.sendMessage("You combine the two halves into a crystal key.");
		}
		if ((itemUsed == 946 && useWith == 2886 || itemUsed == 2886 && useWith == 946)) {
		    int stage = c.questStages[EleWorkShop.QUEST_ID];
		    if (stage >= 1 && !c.getItems().playerHasItem(2887)) {
		        c.sendMessage("You make a small cut in the spine of the book.");
		        c.getItems().deleteItem(2886, 1);
		        c.getItems().addItem(9715, 1); // Spine
		        c.getItems().addItem(2887, 1); // Battered Key
		        
		        // Advance to Stage 3 (Found the key)
		        if (stage < 3) {
		            c.questStages[EleWorkShop.QUEST_ID] = 3;
		        }
		        c.sendMessage("Inside you find a small, old, battered key.");
		    } else {
		        c.sendMessage("Now why would I do that?");
		    }
		}
				if (useWith == 11941 || useWith == 11942) {
					if(c.oneClickDeposit){
						int slotToUpdate = -1; // Initialize with an invalid value

						// Check if the item is already present in any slot
						for (int ITEM = 0; ITEM < 28; ITEM++) {
							if (c.playerLootItems[ITEM] == (itemUsed + 1)) {
								slotToUpdate = ITEM; // Store the slot index to update
								break;
							}
						}

						if (slotToUpdate != -1) {
							// Item already present in a slot
							if (c.getItems().isStackable(c.playerLootItems[slotToUpdate] - 1)) {
								// Item is stackable, increment the quantity
								c.playerLootItemsN[slotToUpdate]++;
							} else {
								// Item is not stackable, find an empty slot
								for (int ITEM = 0; ITEM < 28; ITEM++) {
									if (c.playerLootItems[ITEM] == 0) {
										slotToUpdate = ITEM; // Store the slot index to update
										break;
									}
								}

								if (slotToUpdate != -1) {
									c.playerLootItems[slotToUpdate] = itemUsed + 1;
									c.playerLootItemsN[slotToUpdate] = 1;
								} else {
									// Handle the case when there are no empty slots available
									// Display an error message or take appropriate action
								}
							}
						} else {
							// Item not present in any slot, find an empty slot
							for (int ITEM = 0; ITEM < 28; ITEM++) {
								if (c.playerLootItems[ITEM] == 0) {
									slotToUpdate = ITEM; // Store the slot index to update
									break;
								}
							}

							if (slotToUpdate != -1) {
								c.playerLootItems[slotToUpdate] = itemUsed + 1;
								c.playerLootItemsN[slotToUpdate] = 1;
							} else {
								// Handle the case when there are no empty slots available
								// Display an error message or take appropriate action
							}
						}
						// Delete the used item
						if(c.getItems().playerHasItem(itemUsed, 1)) {
							if (c.getItems().isStackable(itemUsed))
								c.getItems().deleteItem(itemUsed, c.getItems().getItemSlot(itemUsed), c.getItems().getItemAmount(itemUsed));
							else
								c.getItems().deleteItem(itemUsed, c.getItems().getItemSlot(itemUsed), 1);
						}
					} else {
						c.xRemoveId = itemUsed;
						c.xRemoveSlot = c.getItems().getItemSlot(itemUsed);
						c.getDH().sendOption4("Add 1", "Add 5", "Add 10", "Add X");
						c.dialogueAction = 420;
					}
					
				}

		if (itemUsed == 227 || useWith == 227)
			c.getHerblore().handlePotMaking(itemUsed, useWith);
		
		if (itemUsed == 12934 && (useWith == 12924 || useWith == 12926)){
				c.addingCharges = true;
				c.xRemoveId = itemUsed;
				c.getOutStream().createFrame(27);
			
		}
		if (itemUsed <= 811 && itemUsed >= 806 && (useWith == 12924 || useWith == 12926)){
				c.addingDarts = true;
				c.xRemoveId = itemUsed;
				c.getOutStream().createFrame(27);
			
		}
		// Added outer parentheses so BOTH rune types require the Rune Pouch!
				if (((itemUsed <= 566 && itemUsed >= 554) || (itemUsed <= 4699 && itemUsed >= 4694)) && useWith == 12791) {
					c.addingToRP = true;
					c.xRemoveId = itemUsed;
					c.xInterfaceId = 41711;
					c.xRemoveSlot = c.getItems().getItemSlot(itemUsed);
					
					// Send the packet, flush it, and RETURN so no other code runs!
					c.getOutStream().createFrame(27);
				}
		if (itemUsed == 946 || useWith == 946) {
		    int logId = (itemUsed == 946) ? useWith : itemUsed;
		    Fletching.open(c, logId);
		}
		/*if ((itemUsed == 946 && useWith == 2886 || itemUsed == 2886 && useWith == 946)){
			if (c.EleWorkShop >= 1 && !c.getItems().playerHasItem(2887)){
					c.sendMessage("You make a small cut in the spine of the book.");
					c.getItems().deleteItem(2886, 1);
					c.getItems().addItem(9715, 1);
					c.getItems().addItem(2887, 1);
					if (c.EleWorkShop == 2)
						c.EleWorkShop = 3;
					c.sendMessage("Inside you find a small, old, battered key.");
			} else {
				c.sendMessage("Now why would I do that?");
				return;
			}
		}*/
		if (itemUsed == 9075 && useWith == 12791){

				c.addingToRP = true;
				c.xRemoveId = itemUsed;
				c.getOutStream().createFrame(27);
			
		}

		
		if (itemUsed >= 11710 && itemUsed <= 11714 && useWith >= 11710
				&& useWith <= 11714) {
			if (c.getItems().hasAllShards()) {
				c.getItems().makeBlade();
			}
		}
		if (itemUsed == 2368 && useWith == 2366 || itemUsed == 2366//dragon square, 
				//need to update to use it on anvil instead of each other
				&& useWith == 2368) {
			c.getItems().deleteItem(2368, c.getItems().getItemSlot(2368), 1);
			c.getItems().deleteItem(2366, c.getItems().getItemSlot(2366), 1);
			c.getItems().addItem(1187, 1);
		}

		if (c.getItems().isHilt(itemUsed) || c.getItems().isHilt(useWith)) {
			int hilt = c.getItems().isHilt(itemUsed) ? itemUsed : useWith;
			int blade = c.getItems().isHilt(itemUsed) ? useWith : itemUsed;
			if (blade == 11690) {
				c.getItems().makeGodsword(hilt);
			}
		}
		switch(useWith) {
		
	case 590:
		Firemaking.lightFire(c, itemUsed, useWith, c.getX(), c.getY(), false);
		break;
		case 12773:
		case 12774:
			if (itemUsed == 3188) {
				c.getItems().deleteItem2(useWith, 1);
				c.getItems().addItem(4151, 1);
				c.sendMessage("You cleaned the whip.");
			}
			break;
		/**
		 * Light ballista
		 */
	case 19586:
		if (itemUsed == 19592) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19595, 1);
			c.sendMessage("You combined the two items and got an incomplete ballista.");
		}
		break;
		
		/**
		 * Heavy Ballista
		 */
	case 19589:
		if (itemUsed == 19592) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19598, 1);
			c.sendMessage("You combined the two items and got an incomplete ballista.");
		}
		break;
		/**
		 * Both heavy and light ballista
		 */
	case 19601:
		if (itemUsed == 19598) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19607, 1);
			c.sendMessage("You combined the two items and got an unstrung ballista.");
		}
		if (itemUsed == 19595) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19604, 1);
			c.sendMessage("You combined the two items and got an unstrung ballista.");
		}
		break;
	case 19610:
		if (itemUsed == 19607) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19481, 1);
			c.sendMessage("You combined the two items and got a heavy ballista.");
		}
		if (itemUsed == 19604) {
			c.getItems().deleteItem2(useWith, 1);
			c.getItems().deleteItem2(itemUsed, 1);
			c.getItems().addItem(19478, 1);
			c.sendMessage("You combined the two items and got a light ballista.");
		}
		break;
		case 13280:
			switch (itemUsed) {
			case 13124:
				SkillcapePerks.mixCape(c, "ARDOUGNE");
				break;
				
			case 6570:
				SkillcapePerks.mixCape(c, "FIRE");
				break;
				
			case 21295:
				SkillcapePerks.mixCape(c, "INFERNAL");
				break;

			case 10499:
				SkillcapePerks.mixCape(c, "AVAS");
				break;
				
			case 22109:
				SkillcapePerks.mixCape(c, "ASSEMBLER");
				break;

			case 2412:
				SkillcapePerks.mixCape(c, "SARADOMIN");
				break;

			case 2413:
				SkillcapePerks.mixCape(c, "GUTHIX");
				break;

			case 2414:
				SkillcapePerks.mixCape(c, "ZAMORAK");
				break;
				case 21791:
					SkillcapePerks.mixCape(c, "SARADOMINi");
					break;

				case 21793:
					SkillcapePerks.mixCape(c, "GUTHIXi");
					break;

				case 21795:
					SkillcapePerks.mixCape(c, "ZAMORAKi");
					break;
			}
			break;
		}
		
		switch (itemUsed) {

		case 590:
			Firemaking.lightFire(c, itemUsed, useWith, c.getX(), c.getY(), false);
			break;
			case 4151:
				if(useWith == 12004){
					c.getItems().deleteItem(4151, c.getItems().getItemSlot(4151), 1);
					c.getItems().deleteItem(12004, c.getItems().getItemSlot(12004), 1);
					c.getItems().addItem(12006, 1);
				}
			break;
			case 12004:
				if(useWith == 4151){
					c.getItems().deleteItem(4151, c.getItems().getItemSlot(4151), 1);
					c.getItems().deleteItem(12004, c.getItems().getItemSlot(12004), 1);
					c.getItems().addItem(12006, 1);
				}
			break;

		default:
			if (c.playerRights == 3)
				Misc.println("Player used Item id: " + itemUsed
						+ " with Item id: " + useWith);
			break;
		}
	}

	public static void ItemonNpc(Player c, int itemId, int npcId, int slot) {

		if (itemId == 1735 && npcId == 43) { // 1735 is Shears, 43 is Woolly Sheep
		    Shearing.shearSheep(c, npcId, World.getWorld().npcHandler.npcs[npcId].getIndex()); 
		}
		switch(npcId){
		case 2812: // Bob
		    // Logic to call the repair handler
		    Bob bobDialogue = new Bob(c);
		    bobDialogue.handleRepair(c, itemId);
		break;
		case 0:
			if (c.getFarmingTools().noteItem(itemId)) {
				return;
			}
			
			break;
		}
		switch (itemId) {

		default:
			if (c.playerRights == 3)
				Misc.println("Player used Item id: " + itemId
						+ " with Npc id: " + npcId + " With Slot : " + slot);
			break;
		}

	}

}
