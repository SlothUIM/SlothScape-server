package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import server.Config;
import server.model.items.GroundItem;
import server.model.items.Item;
import server.model.items.ItemList;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.packets.GeValuesCache;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.world.objects.GlobalObject;
import server.model.items.GlobalDrop;

/**
 * Handles ground items
 **/

public class ItemHandler {
	
	public static List<GlobalDrop> globalDrops = new ArrayList<GlobalDrop>();
	public static List<GroundItem> items = new ArrayList<GroundItem>();
	public static final int HIDE_TICKS = 100;    // 2 minutes



	// In your process method, logic stays the same but now uses these values.

	public ItemHandler() {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			ItemList[i] = null;
		}
		loadItemList("item_config.cfg");
		loadItemPrices();
		
	}
	private Player c;
	public ItemHandler(Player c) {
		if(c != null)
			this.c = c;
	}
	/**
	 * Adds item to list
	 **/
	public void addItem(GroundItem item) {
		items.add(item);
	}

	/**
	 * Removes item from list
	 **/
	public void removeItem(GroundItem item) {
		items.remove(item);
	}

	/**
	 * Item amount
	 **/
	public int itemAmount(int itemId, int itemX, int itemY) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX
					&& i.getItemY() == itemY) {
				return i.getItemAmount();
			}
		}
		return 0;
	}

	/**
	 * Item exists
	 **/
	public boolean itemExists(int itemId, int itemX, int itemY) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX
					&& i.getItemY() == itemY) {
				return true;
			}
		}
		return false;
	}
	public void loadGlobalDrops() {
	    String line = "";
	    int dropsLoaded = 0;
	    try (BufferedReader file = new BufferedReader(new FileReader("./Data/cfg/global_drops.cfg"))) {
	        while ((line = file.readLine()) != null) {
	            line = line.trim();
	            
	            // 1. Skip strictly empty lines or lines starting with comments
	            if (line.isEmpty() || line.startsWith("//") || line.startsWith("[END")) {
	                continue;
	            }

	            if (line.startsWith("item =")) {
	                // 2. Remove any "end-of-line" comments before parsing numbers
	                // This turns "item = 960 1 3094 3473 0 50 // Plank" into "item = 960 1 3094 3473 0 50"
	                if (line.contains("//")) {
	                    line = line.split("//")[0].trim();
	                }

	                String[] parts = line.split("=");
	                if (parts.length < 2) continue;
	                
	                String[] data = parts[1].trim().split("\\s+"); 
	                
	                if (data.length >= 6) {
	                    try {
	                        int id = Integer.parseInt(data[0]);
	                        int amt = Integer.parseInt(data[1]);
	                        int x = Integer.parseInt(data[2]);
	                        int y = Integer.parseInt(data[3]);
	                        int z = Integer.parseInt(data[4]);
	                        int respawn = Integer.parseInt(data[5]);
	                        
	                        int objId = -1;
	                        int restoreId = -1;
	                        int face = -1;

	                        if (data.length >= 8) {
	                            objId = Integer.parseInt(data[6]);
	                            restoreId = Integer.parseInt(data[7]);
	                            face = Integer.parseInt(data[8]);
	                        }

	                        GlobalDrop drop = new GlobalDrop(id, amt, x, y, z, respawn, objId, restoreId);
	                        globalDrops.add(drop);
	                        
	                        if (objId == -1) {
	                            GroundItem item = new GroundItem(id, x, y, z, amt, 0, "Global");
	                            items.add(item);
	                            createGlobalItem(item);
	                        } else {
	                            // Note: We use -1 for the internal timer of the starting object 
	                            // because it shouldn't revert until someone clicks it.
	                            GlobalObject startingObj = new GlobalObject(objId, x, y, z, face, 10, -1, -1);
	                            World.getWorld().getGlobalObjects().add(startingObj);
	                        }
	                        dropsLoaded++;
	                    } catch (NumberFormatException nfe) {
	                        System.out.println("Error parsing numeric data on line: " + line);
	                    }
	                }
	            }
	        }
	        System.out.println("Loaded " + dropsLoaded + " persistent world drops.");
	    } catch (Exception e) {
	        System.out.println("Critical error in global drops loader.");
	        e.printStackTrace();
	    }
	}
	/**
	 * Reloads any items if you enter a new region
	 **/
	public void reloadItems(Player player) {
		for (GroundItem i : items) {
			if (player != null) {
				if (player.getItems().tradeable(i.getItemId())
						|| i.getName().equalsIgnoreCase(player.playerName)) {
					if (player.distanceToPoint(i.getItemX(), i.getItemY()) <= 60) {
						if (i.hideTicks > 0
								&& i.getName().equalsIgnoreCase(player.playerName)) {
							player.getItems().removeGroundItem(i.getItemId(),
									i.getItemX(), i.getItemY(),
									i.getItemAmount());
							player.getItems().createGroundItem(i.getItemId(),
									i.getItemX(), i.getItemY(),
									i.getItemAmount());
						}
						if (i.hideTicks == 0) {
							player.getItems().removeGroundItem(i.getItemId(),
									i.getItemX(), i.getItemY(),
									i.getItemAmount());
							player.getItems().createGroundItem(i.getItemId(),
									i.getItemX(), i.getItemY(),
									i.getItemAmount());
						}
					}
				}
			}
		}
	}
	public static int timer;
	public static int id;
	public static int index = 0; // Assuming MAX_ITEMS is the maximum number of items

	public void process() {
	    ArrayList<GroundItem> toRemove = new ArrayList<GroundItem>();
	    
	    // Handle standard ground item timers (Visibility and Despawn)
	    for (int j = 0; j < items.size(); j++) {
	        GroundItem i = items.get(j);
	        if (i != null) {
	            // 1. Handle "Private" items becoming "Global"
	            if (i.hideTicks > 0) {
	                i.hideTicks--;
	                if (i.hideTicks == 0) {
	                    createGlobalItem(i);
	                    i.removeTicks = HIDE_TICKS;
	                }
	            }
	            
	            // 2. Handle item despawning (Standard items only!)
	            // We skip this if the name is "Global" so spawns don't disappear!
	            if (!i.getName().equals("Global")) {
	                if (i.removeTicks > 0) {
	                    i.removeTicks--;
	                    if (i.removeTicks == 0) {
	                        toRemove.add(i);
	                    }
	                }
	            }
	        }
	    }

	    // 3. Handle Global Respawn Timers
	    for (GlobalDrop gd : globalDrops) {
	        if (gd.taken) {
	            if (gd.currentTicks > 0) {
	                gd.currentTicks--;
	            } else {
	                gd.taken = false;
	                // Create the item with the "Global" name tag
	                GroundItem item = new GroundItem(gd.itemId, gd.x, gd.y, gd.z, gd.itemAmount, 0, "Global");
	                items.add(item);
	                createGlobalItem(item);
	            }
	        }
	    }

	    // 4. Clean up despawned items
	    for (GroundItem i : toRemove) {
	        removeGlobalItem(i, i.getItemId(), i.getX(), i.getY(), i.getItemAmount());
	    }
	}
	/**
	 * Creates the ground item
	 **/
	
	public void createGroundItem(Player c, int itemId, int itemX, int itemY, int height,
			int itemAmount, int playerId) {
		if (playerId < 0 || playerId > PlayerHandler.players.length - 1) {
			return;
		}
		Player owner = c;
		if (owner == null) {
			return;
		}
		if (itemId > 0 && itemAmount > 0) {
			if (itemId >= 2412 && itemId <= 2414) {
				c.sendMessage("The cape vanishes as it touches the ground.");
				return;
			}
			if (itemId > 4705 && itemId < 4760) {
				for (int j = 0; j < Config.brokenBarrows.length; j++) {
					if (Config.brokenBarrows[j][0] == itemId) {
						itemId = Config.brokenBarrows[j][1];
						break;
					}
				}
			}
			if (!Item.itemStackable[itemId] && itemAmount > 0) {
				if (itemAmount > 28) {
					itemAmount = 28;
				}
				for (int j = 0; j < itemAmount; j++) {
					c.getItems().createGroundItem(itemId, itemX, itemY, 1);
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, 1, HIDE_TICKS, owner.playerName);
					if(owner.getInstance() != null) {
						item.setInstance(owner.getInstance());
					}
					items.add(item);
				}
			} else {
				c.getItems().createGroundItem(itemId, itemX, itemY, itemAmount);
				GroundItem item = new GroundItem(itemId, itemX, itemY, height, itemAmount, HIDE_TICKS,
						owner.playerName);
				if(owner.getInstance() != null) {
					item.setInstance(owner.getInstance());
				}
				items.add(item);
			}
					id = itemId; // or whatever your actual despawn time is

					timer = 600;
					c.getPA().sendFrame178(id, timer, itemX, itemY);
		}
	}
	
	public void createGroundItem(Player player, int itemId, int itemX, int itemY, int height, int itemAmount) {
		if (itemId > 0 && itemAmount > 0) {
//			if (itemId >= 2412 && itemId <= 2414) {
//				player.sendMessage("The cape vanishes as it touches the ground.");
//				return;
//			}
			if (!Item.itemStackable[itemId] && itemAmount > 0) {
				if (itemAmount > 28) {
					itemAmount = 28;
				}
				for (int j = 0; j < itemAmount; j++) {
					player.getItems().createGroundItem(itemId, itemX, itemY, 1);
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, 1, HIDE_TICKS, player.playerName);
					if(player.getInstance() != null) {
						item.setInstance(player.getInstance());
					}
					items.add(item);
				}
			} else {
				if (itemId != 11849 && !Boundary.isIn(player, Boundary.ROOFTOP_COURSES))
					player.getItems().createGroundItem(itemId, itemX, itemY, itemAmount);
					GroundItem item = new GroundItem(itemId, itemX, itemY, height, itemAmount, HIDE_TICKS, player.playerName);
					if(player.getInstance() != null) {
						item.setInstance(player.getInstance());
					}
					items.add(item);
			}
			id = itemId;
			timer = 600;
			player.getPA().sendFrame178(id, timer, itemX, itemY);
		}
	}
	/**
	 * Shows items for everyone who is within 60 squares
	 **/
	public void createGlobalItem(GroundItem i) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player person = p;
				if (!person.playerName.equalsIgnoreCase(i.getItemController())) {
					if (!person.getItems().isTradable(i.getItemId())) {
						continue;
					}
					if (person.distanceToPoint(i.getX(), i.getY()) <= 60 && person.getHeight() == i.getHeight()) {
						person.getItems().createGroundItem(i.getItemId(), i.getX(), i.getY(), i.getItemAmount());
					}
				}
			}
		}
	}
	public void createGlobalItem(int id) {
		GroundItem i = items.get(id);
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player person = p;
				if (!person.playerName.equalsIgnoreCase(i.getItemController())) {
					if (!person.getItems().isTradable(i.getItemId())) {
						continue;
					}
					if (person.distanceToPoint(i.getX(), i.getY()) <= 60 && person.getHeight() == i.getHeight()) {
						person.getItems().createGroundItem(i.getItemId(), i.getX(), i.getY(), i.getItemAmount());
					}
				}
			}
		}
	}
	public void removeGroundItem(Player c, int itemId, int itemX, int itemY, int height, boolean add) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getX() == itemX && i.getY() == itemY && i.getHeight() == height) {
				if (i.hideTicks > 0 && i.getName().equalsIgnoreCase(c.playerName)) {
					if (add) {
						if (c.getItems().addItem(i.getItemId(), i.getItemAmount())) {
							removeControllersItem(i, c, i.getItemId(), i.getX(), i.getY(), i.getItemAmount());
							break;
						}
					} else {
						if(i.getInstance() != null) {
							if(i.getInstance().groundItemsPersistent()) {
								return;
							}
						}
						removeControllersItem(i, c, i.getItemId(), i.getX(), i.getY(), i.getItemAmount());
						break;
					}
				} else if (i.hideTicks <= 0) {
					if (add) {
						int itemToAdd = i.getItemId();
				        
				        // If picking up the 'Ground/Wall' Apron, give the 'Inventory' Apron instead
				        if (itemToAdd == 7957) {
				            itemToAdd = 1005;
				        }
						if (c.getItems().addItem(itemToAdd, i.getItemAmount())) { 
							for (GlobalDrop gd : globalDrops) {
								if (gd.itemId == i.getItemId() && gd.x == i.getX() && gd.y == i.getY() && gd.z == i.getHeight()) {
									gd.taken = true;
									gd.currentTicks = gd.respawnTicks;
									break;
			                	}
							}
							removeGlobalItem(i, i.getItemId(), i.getX(), i.getY(), i.getHeight(), i.getItemAmount());
							break;
						}
					} else {
						if(i.getInstance() != null) {
							if(i.getInstance().groundItemsPersistent()) {
								return;
							}
						}
						removeGlobalItem(i, i.getItemId(), i.getX(), i.getY(), i.getHeight(), i.getItemAmount());
						break;
					}
				}
			}
		}
	}
	/**
	 * Removing the ground item
	 **/

	public void removeGroundItem(Player c2, int itemId, int itemX, int itemY,
			boolean add) {
		for (GroundItem i : items) {
			if (i.getItemId() == itemId && i.getItemX() == itemX
					&& i.getItemY() == itemY) {
				if (i.hideTicks > 0 && i.getName().equalsIgnoreCase(c2.playerName)) {
					if (add) {
						if (!c2.getItems().specialCase(itemId)) {
								if(c2.getItems().playerHasItem(11942, 1)){
									c2.getItems().addtoLootbagFromDeposit(i.getItemId(), 1, i.getItemAmount());
										removeControllersItem(i, c2, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
												return;
								} else {
									if (c2.getItems().addItem(i.getItemId(), i.getItemAmount())) {
										removeControllersItem(i, c2, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
										break;
									}
								}
						} else {
							c2.getItems().handleSpecialPickup(itemId);
							removeControllersItem(i, c2, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							break;
						}
					} else {
						removeControllersItem(i, c2, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						break;
					}
				} else if (i.hideTicks <= 0) {
					if (add) {
						int itemToAdd = i.getItemId();
				        
				        // If picking up the 'Ground/Wall' Apron, give the 'Inventory' Apron instead
				        if (itemToAdd == 7957) {
				            itemToAdd = 1005;
				        }
						if (c2.getItems().addItem(itemToAdd, i.getItemAmount())) {
							for (GlobalDrop gd : globalDrops) {
								if (gd.itemId == i.getItemId() && gd.x == i.getX() && gd.y == i.getY() && gd.z == i.getHeight()) {
									gd.taken = true;
									gd.currentTicks = gd.respawnTicks;
									break;
			                	}
							}
							removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
							break;
						}
					} else {
						removeGlobalItem(i, i.getItemId(), i.getItemX(), i.getItemY(), i.getItemAmount());
						break;
					}
				}

			}
		}
	}

	/**
	 * Remove item for just the item controller (item not global yet)
	 **/

	public void removeControllersItem(GroundItem i, Player c2, int itemId,
			int itemX, int itemY, int itemAmount) {
		c2.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
		removeItem(i);
	}

	/**
	 * Remove item for everyone within 60 squares
	 **/

	public void removeGlobalItem(GroundItem i, int itemId, int itemX,
			int itemY, int itemAmount) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player person = (Player) p;
				if (person != null) {
					if (person.distanceToPoint(itemX, itemY) <= 60) {
						person.getItems().removeGroundItem(itemId, itemX,
								itemY, itemAmount);
					}
				}
			}
		}
		
		removeItem(i);
	}
	public void removeGlobalItem(GroundItem i, int itemId, int itemX, int itemY, int height, int itemAmount) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Player person = p;
				if (person.distanceToPoint(itemX, itemY) <= 60 && person.getHeight() == height) {
					person.getItems().removeGroundItem(itemId, itemX, itemY, itemAmount);
				}
			}
		}
		items.remove(i);
	}
	/**
	 * Item List
	 **/

	/**
	 * The counterpart of the item whether it is the noted or un noted version
	 * 
	 * @param itemId the item id we're finding the counterpart of
	 * @return the note or unnoted version or -1 if none exists
	 */
	public int getCounterpart(int itemId) {
		if (itemId < 0) {
			return -1;
		}

		ItemList unnoted = ItemList[itemId];

		if (unnoted == null) {
			return -1;
		}

		int counterpart = unnoted.getCounterpartId();

		if (counterpart == -1) {
			return -1;
		}

		if (ItemList[counterpart].getCounterpartId() == itemId) {
			return counterpart;
		}

		return -1;
	}
	
	public int getCounterpartOrSelf(int itemId) {
		if (itemId < 0) {
			return itemId;
		}

		ItemList unnoted = ItemList[itemId];

		if (unnoted == null) {
			return itemId;
		}

		int counterpart = unnoted.getCounterpartId();

		if (counterpart == -1) {
			return itemId;
		}

		if (ItemList[counterpart].getCounterpartId() == itemId) {
			return counterpart;
		}

		return itemId;
	}
	public ItemList ItemList[] = new ItemList[Config.ITEM_LIMIT];

	/*public void newItemList(int ItemId, String ItemName,
			String ItemDescription, double ShopValue, double LowAlch,
			double HighAlch, int Bonuses[]) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 0; i < 30000; i++) {
			if (ItemList[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found
		ItemList newItemList = new ItemList(ItemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		newItemList.LowAlch = LowAlch;
		newItemList.HighAlch = HighAlch;
		newItemList.Bonuses = Bonuses;
		ItemList[slot] = newItemList;
	}*/
	public void newItemList(int itemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int Bonuses[]) {
		ItemList newItemList = new ItemList(itemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		newItemList.LowAlch = LowAlch;
		newItemList.HighAlch = HighAlch;
		newItemList.Bonuses = Bonuses;
		ItemList[itemId] = newItemList;
	}
	public void loadItemPrices() {
        GeValuesCache.loadGePricesCache();

}

	public ItemList getItemList(int itemId) {
		if (itemId < 0 || itemId > ItemList.length) {
			return null;
		}
		return ItemList[itemId];
	}

	public boolean loadItemList(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		ItemList = new ItemList[Config.ITEM_LIMIT];
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			ItemList[i] = null;
		}
		try (BufferedReader file = new BufferedReader(new FileReader("./Data/cfg/" + FileName))) {
			while ((line = file.readLine()) != null && !line.equals("[ENDOFITEMLIST]")) {
				line = line.trim();
				int spot = line.indexOf("=");
				if (spot > -1) {
					token = line.substring(0, spot);
					token = token.trim();
					token2 = line.substring(spot + 1);
					token2 = token2.trim();
					token2_2 = token2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token2_2 = token2_2.replaceAll("\t\t", "\t");
					token3 = token2_2.split("\t");
					if (token.equals("item")) {
						int[] Bonuses = new int[12];
						for (int i = 0; i < 12; i++)
							if (token3[(6 + i)] != null) {
								Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
							} else {
								break;
							}
						newItemList(Integer.parseInt(token3[0]), 
								token3[1].replaceAll("_", " "), 
								token3[2].replaceAll("_", " "), 
								Double.parseDouble(token3[3]),
								Double.parseDouble(token3[4]), 
								Double.parseDouble(token3[5]), Bonuses);
					}
				}
			}
		} catch (FileNotFoundException fileex) {
			Misc.println(FileName + ": file not found.");
			return false;
		} catch (IOException ioexception) {
			Misc.println(FileName + ": error loading file.");
			return false;
		}
		try {
			List<String> stackableData = Files.readAllLines(Paths.get("./Data/", "data", "note_ids.dat"));
			for (String data : stackableData) {
				int id = Integer.parseInt(data.split("\t")[0]);
				int counterpart = Integer.parseInt(data.split("\t")[1]);
				if (ItemList[id] == null) {
					ItemList[id] = new ItemList(id);
				}
				ItemList[id].setCounterpartId(counterpart);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
