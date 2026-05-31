package server.model.players.packets;

import server.model.players.Player;
import server.model.players.content.treasuretrails.TreasureTrails;
import server.model.players.content.treasuretrails.tiers.EasyClue;
import server.model.players.content.treasuretrails.types.CoordinateClues;
import server.model.players.quests.PiratesTreasure;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.hunter.Hunter;
import server.model.players.skills.hunter.trap.impl.BirdSnare;
import server.model.players.skills.hunter.trap.impl.BoxTrap;
import server.world.Boundary;
import server.world.World;
import server.model.items.ItemPacks;
import server.model.items.containers.caskets.PlainCasket;
import server.model.players.PacketType;
import server.Server;
import server.content.barrows.Barrows;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

/**
 * Clicking an item, bury bone, eat food etc
 **/
public class ItemOptionOne implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int interfaceId = c.getInStream().readSignedWordBigEndianA();
		int itemSlot = c.getInStream().readUnsignedWordA();
		int itemId = c.getInStream().readUnsignedWordBigEndian();
		c.sendMessage("@red@Item ID: " + itemId + ", Slot: " + itemSlot);
		if (itemId != c.playerItems[itemSlot] - 1) {
			return;
		}
		if (ItemPacks.openPack(c, itemId)) {
		    return; // Handled opening a pack
		}
		if (TreasureTrails.isClueScroll(itemId)) {
            TreasureTrails.readClue(c, itemId); // We can build the interface opener next!
            return;
        }
		switch(itemId){
		case 22521:
			c.getThieving().openAllPouches();
			break;
		case 20546:
			EasyClue.openCasket(c);
			break;
        case 13226:
            c.getHerbSack().fillSack();
            break;

        case 12020:
            c.getGemBag().fillBag();
            break;
			case 11941:
				c.openLootBag = true;
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(22586, 1);
				break;
			case 22586:
				c.openLootBag = false;
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(11941, 1);
			break;
			case 9715:
			case 2886:
				c.getPA().sendFrame126("Book of the elemental shield", 903);
				c.getPA().sendFrame126("Within the pages of this", 843);
				c.getPA().sendFrame126("book you will find the", 844);
				c.getPA().sendFrame126("secret to working the", 845);
				c.getPA().sendFrame126("very elements themselves.", 846);
				c.getPA().sendFrame126("Early in the fifth age, a", 847);
				c.getPA().sendFrame126("new ore was discovered.", 848);
				c.getPA().sendFrame126("This ore has a unique", 849);
				c.getPA().sendFrame126("property of absorbing,", 850);
				c.getPA().sendFrame126("transforming or focusing ", 851);
				c.getPA().sendFrame126("elemental energy. A", 852);
				c.getPA().sendFrame126("workshop was erected", 853);
				c.getPA().sendFrame126("close by to work this new", 854);
				c.getPA().sendFrame126("material. The workshop", 855);
				c.getPA().sendFrame126("was set up for artisans", 856);
				c.getPA().sendFrame126("and inventors to be able", 857);
				c.getPA().sendFrame126("to come and create", 858);
				c.getPA().sendFrame126("devices made from the", 859);
				c.getPA().sendFrame126("unique ore, found only in.", 860);
				c.getPA().sendFrame126("the village of the Seers.", 861);
				c.getPA().sendFrame126("", 862);
				c.getPA().sendFrame126("", 863);
				c.getPA().sendFrame126("", 864);
				c.getPA().sendFrame126("", 14165);
				c.getPA().sendFrame126("", 14166);
				c.getPA().sendFrame126("", 839);
				c.getPA().sendFrame126("", 841);
				c.getPA().showInterface(837);
				c.bookPage = 1;
				c.sendMessage("The book has two parts: an introduction and an instruction section.");
				c.sendMessage("You flip the book open to the introduction and start reading.");
			break;
			/*case 11942:
				c.openLootBag = true;
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(11941, 100);
				break;*/
				case 8007:
				case 8008:
				case 8009:
				case 8010:
				case 8011:
				case 8012:
				case 8013:
				c.getTabs().breakTablet(itemId);
				break;
				case 405:
					PlainCasket.openCasket(c);
					break;
			case 12791:
				c.addingToRP = true;
				c.getRunePouch().openRunePouch();
			break;
			case 12728:
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(556, 100);
				break;
			case 12730://water
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(555, 100);
				break;
			case 12732://earth
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(557, 100);
				break;
			case 12734://fire
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(554, 100);
				break;
			case 12736://mind
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(558, 100);
				break;
			case 12738://chaos
				c.getItems().deleteItem(itemId, c.getItems().getItemSlot(itemId), 1);
				c.getItems().addItem(562, 100);
				break;

			case 19675:
				c.sendMessage("Your Arclight has " + c.getArcLightCharge() + " charges remaining.");
				break;

            case 13249:
                if (!c.getSlayer().isCerberusRoute()) {
                    c.sendMessage("You have no clue how to navigate in here, you should find a slayer master to learn.");
                    return;
                }
                AgilityHandler.delayFade(c, "", 1310, 1237, 0, "You teleport into the cave", "and end up at the main room.", 3);
                c.getItems().deleteItem(13249, 1);
                break;
			case 952: //Spade
                int x = c.getX();
                int y = c.getY();
                       
                c.startAnimation(831, 1);
                
                CycleEventHandler.getSingleton().addEvent(c, (container) -> {
                	c.stopAnimation();
                	container.stop();
                }, 1);
                
                if (CoordinateClues.handleDigging(c)) {
                    return; // If we found a clue, stop the code here!
                }
                if (Boundary.isIn(c, Barrows.GRAVEYARD)) {
                    c.getBarrows().digDown();
                }
                
                if (x == 3005 && y == 3376 || x == 2999 && y == 3375 || x == 2996 && y == 3377) {
                	for(int i = 0; i < c.inextinguishable_lightSources.length; i++)
                		if(c.getItems().playerHasItem(c.inextinguishable_lightSources[i])) {
                			c.getPA().movePlayer(1760, 5163, 0);
                			c.sendMessage("You dig and fall into a cavern below.");
                		} else
                			c.getDH().sendStatement("You need a light source that can not be extinguished");
                }
                if (c.absX >= 2997 && c.absX <= 3001 && c.absY >= 3381 && c.absY <= 3385) {
                    if (c.questStages[PiratesTreasure.QUEST_ID] == PiratesTreasure.DIG_FOR_TREASURE) {
                        
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                if (!c.gardenerSpawned) {
                                    c.sendMessage("An angry gardener appears!");
                                    // Spawns the Gardener (ID 1205) right next to the player and makes him aggressive
                                    World.getWorld().npcHandler.spawnNpc(c, 1205, c.absX, c.absY - 1, c.HeightLevel, 0, 4, 4, 40, 40, true, true);
                                    c.gardenerSpawned = true;
                                } else {
                                    c.sendMessage("You unearth a casket!");
                                    c.getItems().deleteItem(PiratesTreasure.PIRATE_MESSAGE, 1);
                                    
                                    // Quest Complete!
                                    PiratesTreasure quest = new PiratesTreasure(c);
                                    quest.setStage(PiratesTreasure.COMPLETED);
                                    quest.giveRewards(); 
                                }
                                container.stop();
                            }
                            @Override
                            public void stop() {}
                        }, 2); // 2 tick delay for the digging animation to play out
                        return;
                    }
                }
                break;
		}
		// Pirate's Treasure - Pirate Message
        if (itemId == PiratesTreasure.PIRATE_MESSAGE) {
            for (int i = 8144; i < 8195; i++) {
                c.getPA().sendFrame126("", i);
            }
            c.getPA().sendFrame126("@dre@Pirate Message", 8144);
            c.getPA().sendFrame126("Visit the city of the White Knights.", 8147);
            c.getPA().sendFrame126("In the park, Saradomin points to the", 8148);
            c.getPA().sendFrame126("X which marks the spot.", 8149);
            c.getPA().showInterface(8134);
            return;
        }
		// Import these at the top:
// import server.model.players.skills.hunter.Hunter;
// import server.model.players.skills.hunter.trap.impl.BirdSnare;

		if (itemId == 10006) { // 10006 is the Bird snare item ID
			Hunter.lay(c, new BirdSnare(c));
			return;
		}
		if (itemId == 10008) { // 10006 is the Bird snare item ID
			Hunter.lay(c, new BoxTrap(c));
			return;
		}
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			int a = itemId;
			if (a == 5509)
				pouch = 0;
			if (a == 5510)
				pouch = 1;
			if (a == 5512)
				pouch = 2;
			if (a == 5514)
				pouch = 3;
			c.getPA().fillPouch(pouch);
			return;
		}
		if (c.getHerblore().isUnidHerb(itemId))
			c.getHerblore().handleHerbClick(itemId);
		if (c.getFood().isFood(itemId))
			c.getFood().eat(itemId, itemSlot);
		// ScriptManager.callFunc("itemClick_"+itemId, c, itemId, itemSlot);
		if (c.getPotions().isPotion(itemId))
			c.getPotions().handlePotion(itemId, itemSlot);
		if (c.getPrayer().isBone(itemId))
			c.getPrayer().buryBone(itemId, itemSlot);
	}

}
