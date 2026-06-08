package server.model.items;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.RandomUtils;

import server.Config;
import server.Server;
import server.model.items.bank.Bank;
import server.model.items.bank.BankItem;
import server.model.items.bank.BankTab;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSessionRules.Rule;
import server.model.multiplayer_session.duel.Duel;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
import server.model.players.Sound;
import server.model.players.combat.Degrade.DegradableItem;
import server.model.players.skills.Skill;
import server.model.shops.ShopAssistant;
import server.util.Misc;
import server.world.Boundary;
import server.world.ItemHandler;
import server.world.World;

public class ItemAssistant {

	private Player c;

	public ItemAssistant(Player client) {
		this.c = client;
	}

	/**
	 * Items
	 **/


	public void resetItems(int WriteFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(WriteFrame);
			c.getOutStream().writeWord(c.playerItems.length);
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItemsN[i] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
				} else {
					c.getOutStream().writeByte(c.playerItemsN[i]);
				}
				c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
			}

			PlayerSave.saveGame(c);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	/**
	 * Counts the number of items a player possesses with given item ID.
	 * 
	 * @param itemId The ID of the item we're looking for.
	 * @param includeCounterpart True in case the counterpart (noted/unnoted) should also be included in the count, false otherwise.
	 * @return The amount of items the player possesses.
	 */
	public int getItemCount(int itemId, boolean includeCounterpart) {
		int counter = 0;
		int counterpart = -1;
		if (includeCounterpart) {
			counterpart = World.getWorld().getItemHandler().getCounterpart(itemId);
		}
		/*for (Item item : c.getLootingBag().items) {
			if (item.getId() == itemId || counterpart > 0 && item.getId() == counterpart) {
				counter += item.getAmount();
			}
		}*/
		for (Item item : c.getZulrahLostItems()) {
			if (item.getId() == itemId || counterpart > 0 && item.getId() == counterpart) {
				counter += item.getAmount();
			}
		}
		/*for (Item item : c.getCerberusLostItems()) {
			if (item.getId() == itemId || counterpart > 0 && item.getId() == counterpart) {
				counter += item.getAmount();
			}
		}*/
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemId + 1 || counterpart > 0 && c.playerItems[i] == counterpart + 1) {
				counter += c.playerItemsN[i];
				if (Item.itemStackable[c.playerItems[i] - 1]) {
					break;
				}
			}
		}
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] == itemId || counterpart > 0 && c.playerEquipment[i] == counterpart) {
				counter += c.playerEquipmentN[i];
				if (Item.itemStackable[c.playerEquipment[i] - 1]) {
					break;
				}
			}
		}
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab == null) {
				continue;
			}
			for (BankItem item : tab.getItems()) {
				if (item.getId() == itemId + 1 || counterpart > 0 && item.getId() == counterpart + 1) {
					counter += item.getAmount();
					break;
				}
			}
		}
		return counter;
	}


	/**
	 * Gets the total count of (a) player's items.
	 * 
	 * @param itemID
	 * @return
	 */
	public int getTotalCount(int itemID) {
		int count = 0;
		for (int j = 0; j < c.playerItems.length; j++) {
			if (Item.itemIsNote[itemID + 1]) {
				if (itemID + 2 == c.playerItems[j])
					count += c.playerItemsN[j];
			}
			if (!Item.itemIsNote[itemID + 1]) {
				if (itemID + 1 == c.playerItems[j]) {
					count += c.playerItemsN[j];
				}
			}
		}
		for (int j = 0; j < c.bankItems.length; j++) {
			if (c.bankItems[j] == itemID + 1) {
				count += c.bankItemsN[j];
			}
		}
		return count;
	}



	/**
	 * delete all items
	 **/

	public void deleteAllItems() {
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			deleteEquipment(c.playerEquipment[i1], i1);
		}
		for (int i = 0; i < c.playerItems.length; i++) {
			deleteItem(c.playerItems[i] - 1, getItemSlot(c.playerItems[i] - 1),
					c.playerItemsN[i]);
		}
	}

	/**
	 * Drop all items for your killer
	 **/

	public void dropAllItems() {
		Player o = (Player) PlayerHandler.players[c.killerId];

		for (int i = 0; i < c.playerItems.length; i++) {
			if (o != null) {
				if (tradeable(c.playerItems[i] - 1)) {
					World.getWorld().getItemHandler().createGroundItem(o,
							c.playerItems[i] - 1, c.getX(), c.getY(), c.getHeight(),
							c.playerItemsN[i], c.killerId);
				} else {
					if (specialCase(c.playerItems[i] - 1))
						World.getWorld().getItemHandler().createGroundItem(o, 995, c.getX(),
								c.getY(),c.getHeight(),
								getUntradePrice(c.playerItems[i] - 1),
								c.killerId);
					World.getWorld().getItemHandler().createGroundItem(c,
							c.playerItems[i] - 1, c.getX(), c.getY(), c.getHeight(),
							c.playerItemsN[i], c.playerId);
				}
			} else {
				World.getWorld().getItemHandler().createGroundItem(c, c.playerItems[i] - 1,
						c.getX(), c.getY(), c.getHeight(), c.playerItemsN[i], c.playerId);
			}
		}
		for (int e = 0; e < c.playerEquipment.length; e++) {
			if (o != null) {
				if (tradeable(c.playerEquipment[e])) {
					World.getWorld().getItemHandler().createGroundItem(o,
							c.playerEquipment[e], c.getX(), c.getY(), c.getHeight(),
							c.playerEquipmentN[e], c.killerId);
				} else {
					if (specialCase(c.playerEquipment[e]))
						World.getWorld().getItemHandler().createGroundItem(o, 995, c.getX(),
								c.getY(),c.getHeight(),
								getUntradePrice(c.playerEquipment[e]),
								c.killerId);
					World.getWorld().getItemHandler().createGroundItem(c,
							c.playerEquipment[e], c.getX(), c.getY(),c.getHeight(),
							c.playerEquipmentN[e], c.playerId);
				}
			} else {
				World.getWorld().getItemHandler().createGroundItem(c, c.playerEquipment[e],
						c.getX(), c.getY(), c.getHeight(),c.playerEquipmentN[e], c.playerId);
			}
		}
		if (o != null) {
			World.getWorld().getItemHandler().createGroundItem(o, 526, c.getX(), c.getY(), c.getHeight(), 1,
					c.killerId);
		}
	}

	public int getUntradePrice(int item) {
		switch (item) {
		case 2518:
		case 2524:
		case 2526:
			return 100000;
		case 2520:
		case 2522:
			return 150000;
		}
		return 0;
	}

	public boolean specialCase(int itemId) {
		switch (itemId) {
		case 2518:
		case 2520:
		case 2522:
		case 2524:
		case 2526:
			return true;
		}
		return false;
	}

	public void handleSpecialPickup(int itemId) {
		// c.sendMessage("My " + getItemName(itemId) +
		// " has been recovered. I should talk to the void knights to get it back.");
		// c.getItems().addToVoidList(itemId);
	}

	public void addToVoidList(int itemId) {
		switch (itemId) {
		case 2518:
			c.voidStatus[0]++;
			break;
		case 2520:
			c.voidStatus[1]++;
			break;
		case 2522:
			c.voidStatus[2]++;
			break;
		case 2524:
			c.voidStatus[3]++;
			break;
		case 2526:
			c.voidStatus[4]++;
			break;
		}
	}

	public boolean tradeable(int itemId) {
		for (int j = 0; j < Config.ITEM_TRADEABLE.length; j++) {
			if (itemId == Config.ITEM_TRADEABLE[j])
				return false;
		}
		return true;
	}

	/**
	 * Add Item
	 **/
	public boolean addItem(int item, int amount) {
		// synchronized(c) {
		if (amount < 1) {
			amount = 1;
		}
		if (item <= 0) {
			return false;
		}
		if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && Item.itemStackable[item])
				|| ((freeSlots() > 0) && !Item.itemStackable[item])) {
			for (int i = 0; i < c.playerItems.length; i++) {
				if ((c.playerItems[i] == (item + 1))
						&& Item.itemStackable[item] && (c.playerItems[i] > 0)) {
					c.playerItems[i] = (item + 1);
					if (((c.playerItemsN[i] + amount) < Config.MAXITEM_AMOUNT)
							&& ((c.playerItemsN[i] + amount) > -1)) {
						c.playerItemsN[i] += amount;
					} else {
						c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
					}
					if (c.getOutStream() != null && c != null) {
						c.getOutStream().createFrameVarSizeWord(34);
						c.getOutStream().writeWord(3214);
						c.getOutStream().writeByte(i);
						c.getOutStream().writeWord(c.playerItems[i]);
						if (c.playerItemsN[i] > 254) {
							c.getOutStream().writeByte(255);
							c.getOutStream().writeDWord(c.playerItemsN[i]);
						} else {
							c.getOutStream().writeByte(c.playerItemsN[i]);
						}
						c.getOutStream().endFrameVarSizeWord();
						c.flushOutStream();
					}
					i = 30;
					return true;
				}
			}
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] <= 0) {
					c.playerItems[i] = item + 1;
					if ((amount < Config.MAXITEM_AMOUNT) && (amount > -1)) {
						c.playerItemsN[i] = 1;
						if (amount > 1) {
							c.getItems().addItem(item, amount - 1);
							return true;
						}
					} else {
						c.playerItemsN[i] = Config.MAXITEM_AMOUNT;
					}
					resetItems(3214);
					PlayerSave.saveGame(c);
					i = 30;
					return true;
				}
			}

			c.getRunePouch().sendLegacyRuneTypes();
			return false;
		} else {
			resetItems(3214);
			PlayerSave.saveGame(c);
			c.getPA().sendSound(1878, 0, c.EffectVolume);
			//World.getWorld().getItemHandler().createGroundItem(c, item, c.getX(), c.getY(), c.getHeight(), amount);
			c.sendMessage("Not enough space in your inventory.");
			return false;
		}
		
	}

	public String itemType(int item) {
		for (int i = 0; i < Item.capes.length; i++) {
			if (item == Item.capes[i])
				return "cape";
		}
		for (int i = 0; i < Item.hats.length; i++) {
			if (item == Item.hats[i])
				return "hat";
		}
		for (int i = 0; i < Item.boots.length; i++) {
			if (item == Item.boots[i])
				return "boots";
		}
		for (int i = 0; i < Item.gloves.length; i++) {
			if (item == Item.gloves[i])
				return "gloves";
		}
		for (int i = 0; i < Item.shields.length; i++) {
			if (item == Item.shields[i])
				return "shield";
		}
		for (int i = 0; i < Item.amulets.length; i++) {
			if (item == Item.amulets[i])
				return "amulet";
		}
		for (int i = 0; i < Item.arrows.length; i++) {
			if (item == Item.arrows[i])
				return "arrows";
		}
		for (int i = 0; i < Item.rings.length; i++) {
			if (item == Item.rings[i])
				return "ring";
		}
		for (int i = 0; i < Item.body.length; i++) {
			if (item == Item.body[i])
				return "body";
		}
		for (int i = 0; i < Item.legs.length; i++) {
			if (item == Item.legs[i])
				return "legs";
		}
		return "weapon";
	}

	/**
	 * Bonuses
	 **/

	public final String[] BONUS_NAMES = { 
	"Stab", 
	"Slash", 
	"Crush", 
	"Magic",
	"Range", 
	
	"Stab", 
	"Slash", 
	"Crush", 
	"Magic", 
	"Range", 
	
	"Strength",
	"Prayer",
	"Ranged STR",
	"Undead",
	"Slayer",
	"Base",
	"Actual"
	};

	public void resetBonus() {
		for (int i = 0; i < c.playerBonus.length; i++) {
			c.playerBonus[i] = 0;
		}
	}

	/**
	 * Determines if the player is wearing a specific item at a particular slot
	 * 
	 * @param itemId the item we're checking to see the player is wearing
	 * @param slot the slot the item should be detected in
	 * @return true if the item is being word
	 */
	public boolean isWearingItem(int itemId, int slot) {

		return slot>=0&&slot<=c.playerEquipment.length-1&&c.playerEquipment[slot]==itemId;
	}

	/**
	 * Check all slots and determine whether or not a slot is accompanied by that item
	 */
	public boolean isWearingItem(int itemID) {
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] == itemID) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines if the player is wearing any of the given items.
	 * 
	 * @param items the array of item id values.
	 * @return true if the player is wearing any of the optional items.
	 */
	public boolean isWearingAnyItem(int... items) {
		for (int equipmentId : c.playerEquipment) {
			for (int item : items) {
				if (equipmentId == item) {
					return true;
				}
			}
		}
		return false;
	}
	public void writeBonus() {
		calculateCustomBonuses();
	    // The text IDs corresponding to BONUS_NAMES indices 0-16
	    int[] textIds = { 
	        1675, 1676, 1677, 1678, 1679, // Attack (0-4)
	        1680, 1681, 1682, 1683, 1684, // Defense (5-9)
	        1686,                         // Strength (10)
	        1687,                         // Prayer (11)
	        60003,                        // Ranged STR (12)
	        60009,                        // Undead (13)
	        60010,                        // Slayer (14)
	        60011,                        // Base (15)
	        60012                         // Actual (16)
	    };

	    for (int i = 0; i < c.playerBonus.length; i++) {
	        // Safety check to prevent crashes if playerBonus is larger than our ID list
	        if (i >= textIds.length) break; 

	        String send;
	        if (c.playerBonus[i] >= 0) {
	            send = BONUS_NAMES[i] + ": +" + c.playerBonus[i];
	        } else {
	            send = BONUS_NAMES[i] + ": -" + java.lang.Math.abs(c.playerBonus[i]);
	        }

	        c.getPA().sendFrame126(send, textIds[i]);
	    }
	}
	public void calculateCustomBonuses() {
	    // Index 12: Ranged Strength
	    c.playerBonus[12] = getRangedStrength();

	    // Index 13: Undead Bonus (displayed as %)
	    c.playerBonus[13] = (int)(getUndeadMultiplier() * 100) - 100;

	    // Index 14: Slayer Bonus (displayed as %)
	    c.playerBonus[14] = (int)(getSlayerMultiplier() * 100) - 100;

	    // Index 15: Base Weapon Speed (Ticks)
	    c.playerBonus[15] = getBaseWeaponSpeed();

	    // Index 16: Actual Weapon Speed (Ticks - Rapid modifier)
	    c.playerBonus[16] = getActualWeaponSpeed();
	}
	public double getUndeadMultiplier() {
	    double multiplier = 1.0;
	    int amu = c.playerEquipment[c.playerAmulet];

	    if (amu == 10588) // Salve amulet (e)
	        multiplier += 0.20;
	    else if (amu == 4081) // Salve amulet
	        multiplier += 0.15;

	    return multiplier;
	}
	public int getBaseWeaponSpeed() {
	    int weapon = c.playerEquipment[c.playerWeapon];
	    String name = getItemName(weapon).toLowerCase();

	    if (weapon == -1) return 4; // Unarmed

	    // Fast (3 ticks - 1.8s)
	    if (name.contains("dart") || name.contains("knife") || name.contains("blowpipe"))
	        return 3;

	    // Standard Fast (4 ticks - 2.4s)
	    if (name.contains("dagger") || name.contains("scimitar") || name.contains("whip") 
	        || name.contains("sword") || name.contains("claw"))
	        return 4;

	    // Medium (5 ticks - 3.0s)
	    if (name.contains("longsword") || name.contains("mace") || name.contains("pickaxe") 
	        || name.contains("spear") || name.contains("staff") || name.contains("bow"))
	        return 5;

	    // Slow (6 ticks - 3.6s)
	    if (name.contains("godsword") || name.contains("2h") || name.contains("anchor") 
	        || name.contains("halberd") || name.contains("maul"))
	        return 6;

	    // Very Slow (7 ticks - 4.2s)
	    if (name.contains("dark bow") || name.contains("hand cannon"))
	        return 7;

	    return 4; // Default fallback
	}

	public int getActualWeaponSpeed() {
	    int base = getBaseWeaponSpeed();
	    int weapon = c.playerEquipment[c.playerWeapon];
	    String name = getItemName(weapon).toLowerCase();
	    
	    // Check for "Rapid" style (Usually fightMode 1 or 2 depending on your server)
	    // You'll need to check your fightMode variable. Rapid is usually the 2nd option.
	    if (c.fightMode == 2) { // Assuming 2 is Rapid
	        // Rapid usually decreases tick delay by 1 for ranged weapons
	        if (name.contains("bow") || name.contains("dart") || name.contains("knife") 
	            || name.contains("blowpipe") || name.contains("ballista")) {
	            return base - 1;
	        }
	    }
	    
	    return base;
	}
	public double getSlayerMultiplier() {
	    double multiplier = 1.0;
	    int helm = c.playerEquipment[c.playerHat];

	    // Black mask / Slayer helmet checks
	    if (helm == 11864 || helm == 11865 || helm == 8901 || helm == 19647) { // Slayer helm (i), etc.
	        // You might want to check if the player is actually ON a task here if you want it exact
	        // if (c.slayerTask > 0)
	        multiplier += 0.15; // 15% boost
	    }

	    return multiplier;
	}
	public int getRangedStrength() {
	    int bonus = 0;
	    
	    // 1. Check Weapon (Bows, Thrown weapons)
	    int weapon = c.playerEquipment[c.playerWeapon];
	    switch (weapon) {
	        case 20997: // Twisted bow
	            bonus += 20; break; // Example value
	        case 19481: // Heavy ballista
	            bonus += 15; break;
	        case 12926: // Toxic blowpipe
	            bonus += 20; break;
	        // Add other weapons here
	    }

	    // 2. Check Ammo (Arrows, Darts)
	    int arrow = c.playerEquipment[c.playerArrows];
	    switch (arrow) {
	        case 882: // Bronze arrow
	            bonus += 7; break;
	        case 884: // Iron arrow
	            bonus += 10; break;
	        case 890: // Adamant arrow
	            bonus += 31; break;
	        case 892: // Rune arrow
	            bonus += 49; break;
	        case 11212: // Dragon arrow
	            bonus += 60; break;
	        case 11230: // Dragon dart
	            bonus += 35; break;
	    }

	    // 3. Check Other Slots (Necklace, Gloves, etc.)
	    int neck = c.playerEquipment[c.playerAmulet];
	    if (neck == 19547) bonus += 15; // Anguish
	    
	    int hands = c.playerEquipment[c.playerHands];
	    if (hands == 12922) bonus += 5; // Barrows gloves (example)

	    return bonus;
	}
	public void getBonus() {
	    // Reset bonuses first to prevent infinite adding
	    for (int i = 0; i < c.playerBonus.length; i++) {
	        c.playerBonus[i] = 0;
	    }

	    for (int i = 0; i < c.playerEquipment.length; i++) {
	        if (c.playerEquipment[i] > -1) {
	            for (int j = 0; j < Config.ITEM_LIMIT; j++) {
	                if (World.getWorld().getItemHandler().ItemList[j] != null) {
	                    if (World.getWorld().getItemHandler().ItemList[j].itemId == c.playerEquipment[i]) {
	                        // CHANGED: k < 12 ensures we only load indices 0-11 
	                        // (Att, Def, Str, Prayer).
	                        // Indices 12+ (Ranged STR, Undead, Slayer, Base, Actual) are ignored.
	                        for (int k = 0; k < 12; k++) {
	                            // Safety check in case item definitions are shorter than 12
	                            if (k < World.getWorld().getItemHandler().ItemList[j].Bonuses.length) {
	                                c.playerBonus[k] += World.getWorld().getItemHandler().ItemList[j].Bonuses[k];
	                            }
	                        }
	                        break;
	                    }
	                }
	            }
	        }
	    }
	}

	/**
	 * Wear Item
	 **/

	public void sendWeapon(int Weapon, String WeaponName) {
		String WeaponName2 = WeaponName.replaceAll("Bronze", "");
		WeaponName2 = WeaponName2.replaceAll("Iron", "");
		WeaponName2 = WeaponName2.replaceAll("Steel", "");
		WeaponName2 = WeaponName2.replaceAll("Black", "");
		WeaponName2 = WeaponName2.replaceAll("Mithril", "");
		WeaponName2 = WeaponName2.replaceAll("Adamant", "");
		WeaponName2 = WeaponName2.replaceAll("Rune", "");
		WeaponName2 = WeaponName2.replaceAll("Granite", "");
		WeaponName2 = WeaponName2.replaceAll("Dragon", "");
		WeaponName2 = WeaponName2.replaceAll("Drag", "");
		WeaponName2 = WeaponName2.replaceAll("Crystal", "");
		WeaponName2 = WeaponName2.trim();
		/**
		 * Attack styles.
		 */
		if (WeaponName.equals("Unarmed")) {
			if(c.tutorialProgress > 5)
				c.setSidebarInterface(0, 5855); // punch, kick, block
			c.getPA().sendFrame126(WeaponName, 5857);
		} else if (WeaponName.endsWith("whip") || WeaponName.contains("tentacle")) {
			c.setSidebarInterface(0, 12290); // flick, lash, deflect
			c.getPA().sendFrame246(12291, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 12293);
		} else if (WeaponName.contains("bow of") || WeaponName.endsWith("bow") || WeaponName.contains("Craw") || WeaponName.contains("Seren")  || c.playerEquipment[c.playerWeapon] == 23901  || c.playerEquipment[c.playerWeapon] == 23902  || c.playerEquipment[c.playerWeapon] == 23903  || c.playerEquipment[c.playerWeapon] == 23983 || WeaponName.contains("oogie") || WeaponName.contains("imation") || WeaponName.equals("decimation") || WeaponName.equals("Craw's bow") || WeaponName.equals("Craw's bow (u)") || WeaponName.contains("shortbow (i)") || WeaponName.endsWith("10") || WeaponName.endsWith("bow full")
				|| WeaponName.equals("Seercull") || WeaponName.contains("blowpipe") || WeaponName.contains("ballista") || WeaponName.contains("thrownaxe") || WeaponName.contains("chinchompa")) {
			c.setSidebarInterface(0, 1764); // accurate, rapid, longrange
			c.getPA().sendFrame246(1765, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1767);
		} else if ((WeaponName.startsWith("Staff") ||WeaponName.contains("staff")  || WeaponName.contains("Thammaro") || WeaponName.equals("Thammaron's sceptre") || WeaponName.equals("Thammaron's sceptre (u)") || WeaponName.endsWith("staff") || WeaponName.equals("Slayer's staff (e)") || WeaponName.endsWith("of the seas") || WeaponName.endsWith("wand"))
				&& !WeaponName.contains("dead") || WeaponName.equals("Ancient staff")) {
			c.setSidebarInterface(0, 328);
			c.getPA().sendFrame246(329, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 331);
		} else if (WeaponName2.startsWith("dart")  || WeaponName.contains("chinchompa") || WeaponName.contains("dart") || WeaponName2.startsWith("knife") || WeaponName2.contains("throwing") || WeaponName2.endsWith("javelin") || WeaponName2.startsWith("javelin") || WeaponName.equalsIgnoreCase("toktz-xil-ul")) {
			c.setSidebarInterface(0, 4446); // accurate, rapid, longrange
			c.getPA().sendFrame246(4447, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4449);
		} else if (WeaponName2.startsWith("dagger") || WeaponName2.contains("anchor") || WeaponName2.contains("sword") || WeaponName2.contains("byssal dagger") || WeaponName.contains("rapier")) {
			c.setSidebarInterface(0, 2276); // stab, lunge, slash, block
			c.getPA().sendFrame246(2277, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2279);
		} else if (WeaponName2.startsWith("pickaxe") || WeaponName.endsWith("pickaxe")) {
			c.setSidebarInterface(0, 5570); // spike, impale, smash, block
			c.getPA().sendFrame246(5571, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 5573);
		} else if (WeaponName2.startsWith("axe") || WeaponName2.startsWith("battleaxe")) {
			c.setSidebarInterface(0, 1698); // chop, hack, smash, block
			c.getPA().sendFrame246(1699, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 1701);
		} else if (WeaponName2.startsWith("halberd")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName.equals("Staff of the dead") || WeaponName.equals("Toxic staff of the dead")) {
			c.setSidebarInterface(0, 28500);
			c.getPA().sendFrame246(28500, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 331);
		} else if (WeaponName2.startsWith("Scythe") || WeaponName.contains("Scythe")) {
			c.setSidebarInterface(0, 8460); // jab, swipe, fend
			c.getPA().sendFrame246(8461, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 8463);
		} else if (WeaponName2.startsWith("spear")  || 
				WeaponName2.toLowerCase().contains("zamorakian") || WeaponName2.toLowerCase().contains("zamorakian hasta")) {
			c.setSidebarInterface(0, 4679); // lunge, swipe, pound, block
			c.getPA().sendFrame246(4680, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 4682);
		} else if (WeaponName2.toLowerCase().endsWith("mace") || WeaponName2.contains("Viggora")) {
			c.setSidebarInterface(0, 3796);
			c.getPA().sendFrame246(3797, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 3799);
		} else if (WeaponName2.contains("warhammer") || c.playerEquipment[c.playerWeapon] == 4153 || c.playerEquipment[c.playerWeapon] == 13263 || c.playerEquipment[c.playerWeapon] == 12848 || c.playerEquipment[c.playerWeapon] == 13902) {
			c.setSidebarInterface(0, 425); // war hammer equip.
			c.getPA().sendFrame246(426, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 428);
		} else if (WeaponName2.equals("claws")) {
			c.setSidebarInterface(0, 7762);
			c.getPA().sendFrame246(7763, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 7765);
		} else {
			c.setSidebarInterface(0, 2423); // chop, slash, lunge, block
			c.getPA().sendFrame246(2424, 200, Weapon);
			c.getPA().sendFrame126(WeaponName, 2426);
		}
	}

	/**
	 * Weapon Requirements
	 **/

	public void getRequirements(String itemName, int itemId) {
		c.attackLevelReq = c.defenceLevelReq = c.strengthLevelReq = c.rangeLevelReq = c.magicLevelReq = 0;
		if (itemName.contains("mystic") || itemName.contains("nchanted")) {
			if (itemName.contains("staff")) {
				c.magicLevelReq = 20;
				c.attackLevelReq = 40;
			} else {
				c.magicLevelReq = 20;
				c.defenceLevelReq = 20;
			}
		}
		if (itemName.contains("infinity")) {
			c.magicLevelReq = 50;
			c.defenceLevelReq = 25;
		}
		if (itemName.contains("splitbark")) {
			c.magicLevelReq = 40;
			c.defenceLevelReq = 40;
		}
		if (itemName.contains("Green")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 40;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Blue")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 50;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Red")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 60;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("Black")) {
			if (itemName.contains("hide")) {
				c.rangeLevelReq = 70;
				if (itemName.contains("body"))
					c.defenceLevelReq = 40;
				return;
			}
		}
		if (itemName.contains("bronze")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("iron")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 1;
			}
			return;
		}
		if (itemName.contains("steel")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 5;
			}
			return;
		}
		if (itemName.contains("black")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")
					&& !itemName.contains("vamb") && !itemName.contains("chap")) {
				c.attackLevelReq = c.defenceLevelReq = 10;
			}
			return;
		}
		if (itemName.contains("mithril")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 20;
			}
			return;
		}
		if (itemName.contains("adamant")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")) {
				c.attackLevelReq = c.defenceLevelReq = 30;
			}
			return;
		}
		if (itemName.contains("rune")) {
			if (!itemName.contains("knife") && !itemName.contains("dart")
					&& !itemName.contains("javelin")
					&& !itemName.contains("thrownaxe")
					&& !itemName.contains("'bow")) {
				c.attackLevelReq = c.defenceLevelReq = 40;
			}
			return;
		}
		if (itemName.contains("dragon")) {
			if (!itemName.contains("nti-") && !itemName.contains("fire")) {
				c.attackLevelReq = c.defenceLevelReq = 60;
				return;
			}
		}
		if (itemName.contains("crystal")) {
			if (itemName.contains("shield")) {
				c.defenceLevelReq = 70;
			} else {
				c.rangeLevelReq = 70;
			}
			return;
		}
		if (itemName.contains("Toxic")) {
			if (itemName.contains("Blowpipe")) {
				c.rangeLevelReq = 75;
			} else {
				c.rangeLevelReq = 75;
			}
			return;
		}
		if (itemName.contains("ahrim")) {
			if (itemName.contains("staff")) {
				c.magicLevelReq = 70;
				c.attackLevelReq = 70;
			} else {
				c.magicLevelReq = 70;
				c.defenceLevelReq = 70;
			}
		}
		if (itemName.contains("karil")) {
			if (itemName.contains("crossbow")) {
				c.rangeLevelReq = 70;
			} else {
				c.rangeLevelReq = 70;
				c.defenceLevelReq = 70;
			}
		}
		if (itemName.contains("godsword")) {
			c.attackLevelReq = 75;
		}
		if (itemName.contains("3rd age") && !itemName.contains("amulet")) {
			c.defenceLevelReq = 60;
		}
		if (itemName.contains("Initiate")) {
			c.defenceLevelReq = 20;
		}
		if (itemName.contains("verac") || itemName.contains("guthan")
				|| itemName.contains("dharok") || itemName.contains("torag")) {

			if (itemName.contains("hammers")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("axe")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("warspear")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else if (itemName.contains("flail")) {
				c.attackLevelReq = 70;
				c.strengthLevelReq = 70;
			} else {
				c.defenceLevelReq = 70;
			}
		}

		switch (itemId) {
		case 8839:
		case 8840:
		case 8842:
		case 11663:
		case 11664:
		case 11665:
			c.attackLevelReq = 42;
			c.rangeLevelReq = 42;
			c.strengthLevelReq = 42;
			c.magicLevelReq = 42;
			c.defenceLevelReq = 42;
			return;
		case 10551:
		case 2503:
		case 2501:
		case 2499:
		case 1135:
			c.defenceLevelReq = 40;
			return;
		case 11235:
		case 6522:
			c.rangeLevelReq = 60;
			break;
		case 6524:
			c.defenceLevelReq = 60;
			break;
		case 11284:
			c.defenceLevelReq = 75;
			return;
		case 6889:
		case 6914:
			c.magicLevelReq = 60;
			break;
		case 861:
			c.rangeLevelReq = 50;
			break;
		case 10828:
			c.defenceLevelReq = 55;
			break;
		case 11724:
		case 11726:
		case 11728:
			c.defenceLevelReq = 65;
			break;
		case 3751:
		case 3749:
		case 3755:
			c.defenceLevelReq = 40;
			break;

		case 7462:
		case 7461:
			c.defenceLevelReq = 40;
			break;
		case 8846:
			c.defenceLevelReq = 5;
			break;
		case 8847:
			c.defenceLevelReq = 10;
			break;
		case 8848:
			c.defenceLevelReq = 20;
			break;
		case 8849:
			c.defenceLevelReq = 30;
			break;
		case 8850:
			c.defenceLevelReq = 40;
			break;

		case 7460:
			c.defenceLevelReq = 40;
			break;

		case 837:
			c.rangeLevelReq = 61;
			break;

		case 4151: // if you don't want to use names
			c.attackLevelReq = 70;
			return;

		case 6724: // seercull
			c.rangeLevelReq = 60; // idk if that is correct
			return;
		case 4153:
			c.attackLevelReq = 50;
			c.strengthLevelReq = 50;
			return;
		}
	}

	/**
	 * two handed weapon check
	 **/
	public boolean is2handed(String itemName, int itemId) {

		if (itemName.contains("godsword") || itemName.contains("crystal bo") || itemName.contains("aradomin sword")
				|| itemName.contains("2h") || itemName.contains("spear") || itemName.contains("halberd")
				|| itemName.contains("longbow") || itemName.contains("shortbow") || itemName.contains("ark bow")
				|| itemName.contains("karil") || itemName.contains("verac")
				|| itemName.contains("guthan") || itemName.contains("dharok") || itemName.contains("torag") || 
				itemName.contains("abyssal bludgeon") || itemName.contains("spade") || itemName.contains("casket") || 
				itemName.contains("clueless") || itemName.contains("ballista") || itemName.contains("hunting knife") 
				|| itemName.contains("elder maul") || itemName.contains("stunning hammer") || itemName.contains("bulwark") || itemName.contains("claws") || itemName.contains("vitur")) {
			return true;
		}
		switch (itemId) {
			case 12926:
			case 6724:
			case 11838:
			case 12809:
			case 14484:
			case 4153:
			case 12848:
			case 6528:
			case 10887:
			case 12424:
			case 20784:
			case 20997:
			case 22550:
			case 22547:
				return true;
		}
		return false;
	}

	/**
	 * Weapons special bar, adds the spec bars to weapons that require them and
	 * removes the spec bars from weapons which don't require them
	 **/

	public void addSpecialBar(int weapon) {
		switch (weapon) {
		case 14484: //Claw
		case 11791:
		case 22516:
		case 12904:
		case 20784:
			c.getPA().sendFrame171(0, 7800);
			specialAmount(weapon, c.specAmount, 7812);
			break;
		case 4151: // whip
		case 33526:
		case 12773:
		case 12774:
		case 12006:
			c.getPA().sendFrame171(0, 12323);
			specialAmount(weapon, c.specAmount, 12335);
			break;

		case 859: // magic bows
		case 861:
		case 11235:
		case 12765:
		case 12766:
		case 12767:
		case 12768:
		case 11785:
		case 12788:
		case 12926:
		case 19478:
		case 19481:
		case 20849:
			c.getPA().sendFrame171(0, 7549);
			specialAmount(weapon, c.specAmount, 7561);
			break;
			
		case 19675:
		case 4587: // dscimmy
			c.getPA().sendFrame171(0, 7599);
			specialAmount(weapon, c.specAmount, 7611);
			break;

		case 3204: // d hally
		case 13092:
			c.getPA().sendFrame171(0, 8493);
			specialAmount(weapon, c.specAmount, 8505);
			break;

		case 1377: // d battleaxe
			c.getPA().sendFrame171(0, 7499);
			specialAmount(weapon, c.specAmount, 7511);
			break;
		case 12848:
		case 4153: // gmaul
		case 13263:
		case 13576:
			c.getPA().sendFrame171(0, 7474);
			specialAmount(weapon, c.specAmount, 7486);
			break;

		case 1249: // dspear
		case 1263:
		case 21028:
		case 5716:
		case 5730:
		case 13905:
		case 11824:
		case 11889:
			c.getPA().sendFrame171(0, 7674);
			specialAmount(weapon, c.specAmount, 7686);
			break;

		case 1215:// dragon dagger
		case 1231:
		case 5680:
		case 5698:
		case 1305: // dragon long
		case 11802:
		case 11806:
		case 11808:
		case 11838:
		case 12809:
		case 11804:
		case 10887:
		case 13899:
		case 13265:
		case 13267:
		case 13269:
		case 13271:
		case 21009: //Dragon sword
			c.getPA().sendFrame171(0, 7574);
			specialAmount(weapon, c.specAmount, 7586);
			break;
			
		case 13902: // crystal hally
			c.getPA().sendFrame171(0, 7474);
			specialAmount(weapon, c.specAmount, 7486);
			break;
			
		case 1434: // dragon mace
		case 11061:
			c.getPA().sendFrame171(0, 7624);
			specialAmount(weapon, c.specAmount, 7636);
			break;

		default:
			c.getPA().sendFrame171(1, 7624); // mace interface
			c.getPA().sendFrame171(1, 7474); // hammer, gmaul
			c.getPA().sendFrame171(1, 7499); // axe
			c.getPA().sendFrame171(1, 7549); // bow interface
			c.getPA().sendFrame171(1, 7574); // sword interface
			c.getPA().sendFrame171(1, 7599); // scimmy sword interface, for most
			c.getPA().sendFrame171(1, 8493);
			c.getPA().sendFrame171(1, 12323); // whip interface
			break;
		}
	}

	/**
	 * Specials bar filling amount
	 **/

	public void specialAmount(int weapon, double specAmount, int barId) {
		c.specBarId = barId;
		c.getPA().sendFrame70(specAmount >= 10 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 9 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 8 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 7 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 6 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 5 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 4 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 3 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 2 ? 500 : 0, 0, (--barId));
		c.getPA().sendFrame70(specAmount >= 1 ? 500 : 0, 0, (--barId));
		updateSpecialBar();
		sendWeapon(weapon, getItemName(weapon));
	}

	/**
	 * Special attack text and what to highlight or blackout
	 **/

	public void updateSpecialBar() {
		String percent = Double.toString(c.specAmount);
		if (percent.contains(".")) {
			percent = percent.replace(".", "");
		}
		if (percent.startsWith("0") && !percent.equals("00")) {
			percent = percent.replace("0", "");
		}
		if (percent.startsWith("0") && percent.equals("00")) {
			percent = percent.replace("00", "0");
		}
		c.getPA().sendSpecialAttack(Integer.valueOf(percent), c.usingSpecial ? 1 : 0);
		c.getPA().sendFrame126(c.usingSpecial ? "@yel@Special Attack (" + percent + "%)" : "@bla@Special Attack (" + percent + "%)", c.specBarId);
	}

	/**
	 * Item kept on death
	 **/
	public void keepItem(int keepItem, boolean deleteItem) {
		int value = 0;
		int item = 0;
		int slotId = 0;
		boolean itemInInventory = false;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] - 1 > 0) {
				int inventoryItemValue = ShopAssistant.getItemShopValue(c.playerItems[i] - 1);
				if (inventoryItemValue > value && (!c.invSlot[i])) {
					value = inventoryItemValue;
					item = c.playerItems[i] - 1;
					slotId = i;
					itemInInventory = true;
				}
			}
		}
		for (int i1 = 0; i1 < c.playerEquipment.length; i1++) {
			if (c.playerEquipment[i1] > 0) {
				int equipmentItemValue = ShopAssistant.getItemShopValue(c.playerEquipment[i1]);
				if (equipmentItemValue > value && (!c.equipSlot[i1])) {
					value = equipmentItemValue;
					item = c.playerEquipment[i1];
					slotId = i1;
					itemInInventory = false;
				}
			}
		}
		if (itemInInventory) {
			c.invSlot[slotId] = true;
			if (deleteItem) {
				deleteItem(c.playerItems[slotId] - 1, getItemSlot(c.playerItems[slotId] - 1), 1);
			}
		} else {
			c.equipSlot[slotId] = true;
			if (deleteItem) {
				deleteEquipment(item, slotId);
			}
		}
		c.itemKeptId[keepItem] = item;
	}
			public void sendItemsKept() {
			if(c.getOutStream() != null && c != null ) {
				c.getOutStream().createFrameVarSizeWord(53);
				c.getOutStream().writeWord(6963);
				c.getOutStream().writeWord(c.itemKeptId.length);
				for (int i = 0; i < c.itemKeptId.length; i++) {
					if(c.playerItemsN[i] > 254) {
						c.getOutStream().writeByte(255); 
						c.getOutStream().writeDWord_v2(1);
					} else {
						c.getOutStream().writeByte(1);
					}
					if(c.itemKeptId[i] > 0) {
					   c.getOutStream().writeWordBigEndianA(c.itemKeptId[i]+1);
					} else {
						c.getOutStream().writeWordBigEndianA(0);
					}
				}
				c.getOutStream().endFrameVarSizeWord();   
				c.flushOutStream();
			}
    }
	/**
	* Reset items kept on death
	**/
	
	public void resetKeepItems() {
		for(int i = 0; i < c.itemKeptId.length; i++) {
			c.itemKeptId[i] = -1;
		}
		for(int i1 = 0; i1 < c.invSlot.length; i1++) {
			c.invSlot[i1] = false;
		}
		for(int i2 = 0; i2 < c.equipSlot.length; i2++) {
			c.equipSlot[i2] = false;
		}		
	}
	/**
	 * Wear Item
	 **/

	public boolean wearItem(int wearID, int slot) {
		if (!c.getItems().playerHasItem(wearID, 1, slot)) {
			// add a method here for logging cheaters(If you want)
			return false;
		}
		
		if (c.tutorialProgress < 22) {
			c.sendMessage("You'll be told how to equip items later.");
			return false;
		}

		if (c.tutorialProgress == 22) {
			c.getPA().chatbox(6180);
			c.getDH()
					.chatboxText(
							"Clothes, armour, weapons and many other items are equipped",
							"like this. You can unequip items by clicking on the item in the",
							"worn inventory. You can close this window by clicking on the",
							"small x. Speak to the Combat Instructor to continue.",
							"You're now holding your dagger");
			c.getPA().chatbox(6179);
			c.tutorialProgress = 23;
			// c.setSidebarInterface(0, -1);// worn

		} else if (c.tutorialProgress == 23) {
			c.getPA().chatbox(6180);
			c.getDH()
					.chatboxText(
							"",
							"Click on the flashing crossed swords icon to see the combat",
							"interface.", "", "Combat interface");
			c.getPA().chatbox(6179);
			c.getPA().flashSideBarIcon(0);
			// c.getPacketDispatcher().tutorialIslandInterface(50, 11);
		}

		// synchronized(c) {
		//c.getSkilling().stop();
		int targetSlot = 0;
		boolean canWearItem = true;
		if (c.playerItems[slot] == (wearID + 1)) {
			ItemDefinition item = ItemDefinition.forId(wearID);
			if (item == null) {
				/*if (wearID == 33508) {
					return false;
				}*/
				
				c.sendMessage("This item is currently unwearable.");
				return false;
			}
		for (int skillId = 0; skillId < item.getRequirements().length; skillId++) {
			if(skillId >= Skill.values().length) {
				continue;
			}
			if (c.getSkills().getActualLevel(Skill.forId(skillId)) < item.getRequirements()[skillId]) {
				c.sendMessage("You need an " + Config.SKILL_NAME[skillId] + " level of " + item.getRequirements()[skillId] + " to wear this item.");
				return false;
			}
		}
			boolean contains = IntStream.of(c.GRACEFUL).anyMatch(x -> x == wearID);
			if (contains) {
				c.graceSum();
			}
			getRequirements(c.getItems().getItemName(wearID).toLowerCase(), wearID);
					switch(wearID){
						//head slot
						case 10374:
						case 10382:
						case 10390:
						case 12931:
						case 12600:
						targetSlot=0;
						break;
						//cape slot
						case 12427:
						case 12437:
						case 13121:
						case 13122:
						case 13123:
						case 13124:
						case 21295:
						targetSlot=1;
						break;
						//necklace slot
						case 19499:
						case 19501:
						case 19707:
						targetSlot=2;
						break;
						//weapon slot
						case 12900:
						case 12902:
						case 14654:
						case 14656:
						case 13265:
						case 12904:
						case 22978:
						case 12799:
						case 11944:
						case 11946:
						case 13578:
						case 13576:
						case 12424:
						case 12422:
						case 12389:
						case 12426:
						case 12297:
						case 12006:
						case 12797:
						case 11920:
						case 11907:
						case 12926:
						case 14378:
						case 14380:
						case 13246:
						case 13248:
						case 13250:
						case 13252:
						targetSlot=3;
						break;
						//body slot
						case 10386:
						case 10378:
						case 10370:
						case 12381:
						case 12385:
						case 12205:
						case 12215:
						case 12225:
						case 12235:
						case 12277:
						case 12287:
						case 12327:
						case 12331:
						targetSlot=4;
						break;
						//shield slot
						case 12291:
						case 12281:
						case 12243:
						case 12233:
						case 12223:
						case 12213:
						case 12954:
						case 12806:
						case 12807:
						targetSlot=5;
						break;
						//leg slot
						case 10372:
						case 10380:
						case 10388:
						case 12333:
						case 12329:
						case 12295:
						case 12289:
						case 12285:
						case 12279:
						case 12239:
						case 12237:
						case 12229:
						case 12227:
						case 12219:
						case 12217:
						case 12209:
						case 12207:
						case 12387:
						case 12383:
						targetSlot=7;
						break;
						//boot slot
						case 12391:
						case 12598:
						case 13237:
						case 13239:
						case 13235:
						targetSlot=10;
						break;
						//ring slot
						case 12692:
						case 12601:
						case 12603:
						case 12691:
						targetSlot=12;
						break;
					}
			
					Optional<DegradableItem> degradable = DegradableItem.forId(wearID);
					if (degradable.isPresent()) {
						if (c.claimDegradableItem[degradable.get().ordinal()]) {
							c.sendMessage("A previous item similar to this has degraded. You must go to Perdu at the home area");
							c.sendMessage("in edgeville to claim this item.");
							return false;
						}
					}
					targetSlot = item.getSlot();
					if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
						DuelSession session = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
						if (!Objects.isNull(session)) {
							if (targetSlot == c.playerHat && session.getRules().contains(Rule.NO_HELM)) {
								c.sendMessage("Wearing helmets has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerAmulet && session.getRules().contains(Rule.NO_AMULET)) {
								c.sendMessage("Wearing amulets has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerArrows && session.getRules().contains(Rule.NO_ARROWS)) {
								c.sendMessage("Wearing arrows has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerChest && session.getRules().contains(Rule.NO_BODY)) {
								c.sendMessage("Wearing platebodies has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerFeet && session.getRules().contains(Rule.NO_BOOTS)) {
								c.sendMessage("Wearing boots has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerHands && session.getRules().contains(Rule.NO_GLOVES)) {
								c.sendMessage("Wearing gloves has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerCape && session.getRules().contains(Rule.NO_CAPE)) {
								c.sendMessage("Wearing capes has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerLegs && session.getRules().contains(Rule.NO_LEGS)) {
								c.sendMessage("Wearing platelegs has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerRing && session.getRules().contains(Rule.NO_RINGS)) {
								c.sendMessage("Wearing a ring has been disabled for this duel.");
								return false;
							}
							if (targetSlot == c.playerWeapon && session.getRules().contains(Rule.NO_WEAPON)) {
								c.sendMessage("Wearing weapons has been disabled for this duel.");
								return false;
							}
							if (session.getRules().contains(Rule.NO_SHIELD)) {
								if (targetSlot == c.playerShield || targetSlot == c.playerWeapon && is2handed(getItemName(wearID).toLowerCase(), wearID)) {
									c.sendMessage("Wearing shields and 2handed weapons has been disabled for this duel.");
									return false;
								}
							}
						}
					}

					if (targetSlot == 3) {
						c.spellId = 0;
						c.usingMagic = false;
						c.usingRangeWeapon = false;
						c.usingBow = false;
						c.autocasting = false;
						c.autocastId = 0;
						c.getPA().sendFrame36(108, 0);
						c.usingSpecial = false;
						addSpecialBar(wearID);
						c.getItems().updateSpecialBar();
						c.getPA().resetAutocast();
						if (wearID != 4153 && wearID != 12848) {
							c.getCombat().resetPlayerAttack();
						}
					}
					if (targetSlot == -1 || !item.isWearable()) {
						if (wearID >= 5509 && wearID <= 5512 || wearID == 21347 || wearID == 33508 || wearID == 11918 || wearID == 13656 || wearID == 7959 || wearID == 7960) {
							return false;
						} else {
						//c.sendMessage("This item cannot be worn.");
						return false;
						}
					}
			if (!canWearItem) {
				return false;
			}

			int wearAmount = c.playerItemsN[slot];
			if (wearAmount < 1) {
				return false;
			}

			if (slot >= 0 && wearID >= 0) {
				int toEquip = c.playerItems[slot];
				int toEquipN = c.playerItemsN[slot];
				int toRemove = c.playerEquipment[targetSlot];
				int toRemoveN = c.playerEquipmentN[targetSlot];
				boolean stackable = false;
				stackable=getItemName(toRemove).contains("javelin")||getItemName(toRemove).contains("dart")||getItemName(toRemove).contains("knife")
						||getItemName(toRemove).contains("bolt")||getItemName(toRemove).contains("arrow")||getItemName(toRemove).contains("Bolt")
						||getItemName(toRemove).contains("bolts")||getItemName(toRemove).contains("thrownaxe")||getItemName(toRemove).contains("throwing");
				if (toEquip == toRemove + 1 && Item.itemStackable[toRemove]) {
					deleteItem(toRemove, getItemSlot(toRemove), toEquipN);
					c.playerEquipmentN[targetSlot] += toEquipN;
				} else if (targetSlot != 5 && targetSlot != 3) {
					if (playerHasItem(toRemove, 1) &&stackable) {
						c.playerItems[slot] = 0;// c.playerItems[slot] =
												// toRemove + 1;
						c.playerItemsN[slot] = 0;// c.playerItemsN[slot] =
													// toRemoveN;
						if (toRemove > 0 && toRemoveN > 0) // c.playerEquipment[targetSlot]
															// = toEquip - 1;
							addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
															// = toEquipN;
					} else {
						c.playerItems[slot] = toRemove + 1;
						c.playerItemsN[slot] = toRemoveN;
					}
					c.playerEquipment[targetSlot] = toEquip - 1;
					c.playerEquipmentN[targetSlot] = toEquipN;
				} else if (targetSlot == 5) {
					boolean wearing2h = is2handed(getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase(), c.playerEquipment[c.playerWeapon]);
					if (wearing2h) {
						toRemove = c.playerEquipment[c.playerWeapon];
						toRemoveN = c.playerEquipmentN[c.playerWeapon];
						c.playerEquipment[c.playerWeapon] = -1;
						c.playerEquipmentN[c.playerWeapon] = 0;
						updateSlot(c.playerWeapon);
					}
					c.playerItems[slot] = toRemove + 1;
					c.playerItemsN[slot] = toRemoveN;
					c.playerEquipment[targetSlot] = toEquip - 1;
					c.playerEquipmentN[targetSlot] = toEquipN;
				} else if (targetSlot == 3) {
					boolean is2h = is2handed(getItemName(wearID).toLowerCase(), wearID);
					boolean wearingShield = c.playerEquipment[c.playerShield] > 0;
					boolean wearingWeapon = c.playerEquipment[c.playerWeapon] > 0;
					if (is2h) {
						if (wearingShield && wearingWeapon) {
							if (freeSlots() > 0) {
								if (playerHasItem(toRemove, 1) &&stackable) {
									c.playerItems[slot] = 0;// c.playerItems[slot]
															// = toRemove + 1;
									c.playerItemsN[slot] = 0;// c.playerItemsN[slot]
																// = toRemoveN;
									if (toRemove > 0 && toRemoveN > 0) // c.playerEquipment[targetSlot]
																		// =
																		// toEquip
																		// - 1;
										addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																		// =
																		// toEquipN;
								} else {
									c.playerItems[slot] = toRemove + 1;
									c.playerItemsN[slot] = toRemoveN;
								}
								c.playerEquipment[targetSlot] = toEquip - 1;
								c.playerEquipmentN[targetSlot] = toEquipN;
								removeItem(c.playerEquipment[c.playerShield], c.playerShield);
							} else {
								c.sendMessage("You do not have enough inventory space to do this.");
								return false;
							}
						} else if (wearingShield && !wearingWeapon) {
							c.playerItems[slot] = c.playerEquipment[c.playerShield] + 1;
							c.playerItemsN[slot] = c.playerEquipmentN[c.playerShield];
							c.playerEquipment[targetSlot] = toEquip - 1;
							c.playerEquipmentN[targetSlot] = toEquipN;
							c.playerEquipment[c.playerShield] = -1;
							c.playerEquipmentN[c.playerShield] = 0;
							updateSlot(c.playerShield);
						} else {
							if (playerHasItem(toRemove, 1) &&stackable) {
								c.playerItems[slot] = 0;// c.playerItems[slot] =
														// toRemove + 1;
								c.playerItemsN[slot] = 0;// c.playerItemsN[slot]
															// = toRemoveN;
								if (toRemove > 0 && toRemoveN > 0) // c.playerEquipment[targetSlot]
																	// = toEquip
																	// - 1;
									addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																	// =
																	// toEquipN;
							} else {
								c.playerItems[slot] = toRemove + 1;
								c.playerItemsN[slot] = toRemoveN;
							}
							c.playerEquipment[targetSlot] = toEquip - 1;
							c.playerEquipmentN[targetSlot] = toEquipN;
						}
					} else {
						if (playerHasItem(toRemove, 1) &&stackable) {
							c.playerItems[slot] = 0;// c.playerItems[slot] =
													// toRemove + 1;
							c.playerItemsN[slot] = 0;// c.playerItemsN[slot] =
														// toRemoveN;
							if (toRemove > 0 && toRemoveN > 0) // c.playerEquipment[targetSlot]
																// = toEquip -
																// 1;
								addItem(toRemove, toRemoveN);// c.playerEquipmentN[targetSlot]
																// = toEquipN;
						} else {
							c.playerItems[slot] = toRemove + 1;
							c.playerItemsN[slot] = toRemoveN;
						}
						c.playerEquipment[targetSlot] = toEquip - 1;
						c.playerEquipmentN[targetSlot] = toEquipN;
					}
				}
				//GameItem value = new GameItem(c.playerEquipment[targetSlot], c.playerEquipmentN[targetSlot]);
				//c.getEquipment().update(Slot.valueOf(targetSlot), value);
				resetItems(3214);
			}
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrameVarSizeWord(34);
				c.getOutStream().writeWord(1688);
				c.getOutStream().writeByte(targetSlot);
				c.getOutStream().writeWord(wearID + 1);

				if (c.playerEquipmentN[targetSlot] > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord(c.playerEquipmentN[targetSlot]);
				} else {
					c.getOutStream().writeByte(c.playerEquipmentN[targetSlot]);
				}

				c.getOutStream().endFrameVarSizeWord();
				c.flushOutStream();
			}
			sendWeapon(c.playerEquipment[c.playerWeapon], getItemName(c.playerEquipment[c.playerWeapon]));
			c.getPA().sendSound(Sound.getEquipSound(wearID), 0, 0, c.EffectVolume);
			resetBonus();
			getBonus();
			writeBonus();
			c.getCombat().getPlayerAnimIndex(getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
			c.getPA().requestUpdates();
			return true;
		} else {
			return false;
		}
	}
	public int getSoundforWearSlot(int i) {
		switch(i) {
		case 0:
			return 1789;
		}
		return 1789;
	}
	public void wearItem(int wearID, int wearAmount, int targetSlot) {
		// synchronized (c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(targetSlot);
			c.getOutStream().writeWord(wearID + 1);
			if (wearAmount > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(wearAmount);
			} else {
				c.getOutStream().writeByte(wearAmount);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipment[targetSlot] = wearID;
			c.playerEquipmentN[targetSlot] = wearAmount;
			c.getItems().sendWeapon(c.playerEquipment[c.playerWeapon], c.getItems().getItemName(c.playerEquipment[c.playerWeapon]));
			c.getItems().resetBonus();
			c.updateItems = true;
			c.getItems().getBonus();
			c.getItems().writeBonus();
			c.getCombat().getPlayerAnimIndex(c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
		// }
	}

	public void updateSlot(int slot) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(slot);
			c.getOutStream().writeWord(c.playerEquipment[slot] + 1);
			if (c.playerEquipmentN[slot] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(c.playerEquipmentN[slot]);
			} else {
				c.getOutStream().writeByte(c.playerEquipmentN[slot]);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}

	}

	/**
	 * Remove Item
	 **/
	public void removeItem(int wearID, int slot) {
		// synchronized(c) {
		//c.getSkilling().stop();
		//if(c.insidePost) {
		//	return;
		//}
		if (c.getOutStream() != null && c != null) {
			if (c.playerEquipment[slot] > -1) {
				if (addItem(c.playerEquipment[slot], c.playerEquipmentN[slot])) {
					c.playerEquipment[slot] = -1;
					c.playerEquipmentN[slot] = 0;
					sendWeapon(c.playerEquipment[c.playerWeapon], getItemName(c.playerEquipment[c.playerWeapon]));
					resetBonus();
					getBonus();
					writeBonus();
					boolean contains = IntStream.of(c.GRACEFUL).anyMatch(x -> x == wearID);
					if (contains) {
						c.graceSum();
					}
					switch (wearID) {
					case 10501:
						c.getPA().showOption(3, 0, "Null", 1);
						break;
					}
					//if (c.inGodwars()) {
					//	c.updateGodItems();
					//}
					c.getCombat().getPlayerAnimIndex(getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.getOutStream().createFrame(34);
					c.getOutStream().writeWord(6);
					c.getOutStream().writeWord(1688);
					c.getOutStream().writeByte(slot);
					c.getOutStream().writeWord(0);
					c.getOutStream().writeByte(0);
					c.flushOutStream();
					c.updateRequired = true;
					c.setAppearanceUpdateRequired(true);
					c.isFullHelm = Item.isFullHelm(c.playerEquipment[c.playerHat]);
					c.isFullMask = Item.isFullMask(c.playerEquipment[c.playerHat]);
					c.isFullBody = Item.isFullBody(c.playerEquipment[c.playerChest]);
				}
			}
		}
		// }
	}

			
	/**
	 * Items in your bank.
	 */
	public void rearrangeBank() {
		// This method is now obsolete because CopyOnWriteArrayList 
		// automatically shifts items and removes empty spaces.
		// Leaving this blank prevents legacy PI code from crashing the bank!
	}


	public void itemOnInterface(int id, int amount) {
		//synchronized(c) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(2274);
			c.getOutStream().writeWord(1);
			if (amount > 254){
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(amount);
			} else {
				c.getOutStream().writeByte(amount);
			}
			c.getOutStream().writeWordBigEndianA(id); 
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
	//}
	}
	
	/*public void resetBank() {
	    // Get the current tab from the Bank object
	    BankTab currentTab = c.getBank().getCurrentBankTab();

	    // Start writing the packet
	    c.getOutStream().createFrameVarSizeWord(53);
	    c.getOutStream().writeWord(5382); // Bank interface ID
	    c.getOutStream().writeWord(currentTab.size()); // Number of items in tab

	    for (BankItem item : currentTab.getItems()) {
	        int itemAmount = item.getAmount();
	        int itemId = item.getId() + 1;

	        if (itemAmount > 254) {
	            c.getOutStream().writeByte(255);
	            c.getOutStream().writeDWord_v2(itemAmount);
	        } else {
	            c.getOutStream().writeByte(itemAmount);
	        }

	        if (itemId > Config.ITEM_LIMIT || itemId < 0) {
	            itemId = Config.ITEM_LIMIT; // Safety fallback
	        }

	        c.getOutStream().writeWordBigEndianA(itemId);
	    }

	    c.getOutStream().endFrameVarSizeWord();
	    c.flushOutStream();
	}*/


	/**
	 * Reseting your bank.
	 */
	public void resetBank() {
		int tabId = c.getBank().getCurrentBankTab().getTabId();
		
		// 1. Properly shift all tabs repeatedly so there are NO empty gaps
		boolean shifted;
		do {
			shifted = false;
			for (int i = 1; i < c.getBank().getBankTab().length - 1; i++) {
				if (c.getBank().getBankTab()[i].size() == 0 && c.getBank().getBankTab()[i + 1].size() > 0) {
					for (BankItem item : c.getBank().getBankTab()[i + 1].getItems()) {
						c.getBank().getBankTab()[i].add(item);
					}
					c.getBank().getBankTab()[i + 1].getItems().clear();
					shifted = true;
				}
			}
		} while(shifted);

		// If the current tab became empty during a shift, safely fall back to Main Tab
		if (c.getBank().getCurrentBankTab().size() == 0 && tabId != 0) {
			c.getBank().setCurrentBankTab(c.getBank().getBankTab()[0]);
			tabId = 0;
		}

		c.getPA().sendFrame36(700, 0);
		c.getPA().sendFrame34a(10335, -1, 0, 0); // Main tab doesn't have an item icon
		
		int newSlot = -1;
		for (int i = 0; i < c.getBank().getBankTab().length; i++) {
			BankTab tab = c.getBank().getBankTab()[i];
			
			// Set the "Selected Tab" sprite
			c.getPA().sendFrame36(700 + i, (i == tabId) ? 1 : 0);

			if (tab.getTabId() != 0 && tab.size() > 0 && tab.getItem(0) != null) {
				// Tab has items: show the background and the first item as the icon
				c.getPA().sendFrame171(0, 58050 + i);
				c.getPA().sendFrame34a(10335 + i, tab.getItem(0).getId() - 1, 0, tab.getItem(0).getAmount());
			} else if (i != 0) {
				// Tab is empty
				if (newSlot == -1) {
					newSlot = i; // First empty tab becomes the "+" icon
					c.getPA().sendFrame34a(10335 + i, -1, 0, 0);
					c.getPA().sendFrame171(0, 58050 + i);
				} else {
					c.getPA().sendFrame34a(10335 + i, -1, 0, 0); // FIXED THE 10334 TYPO HERE!
					c.getPA().sendFrame171(1, 58050 + i);
				}
			}
			
			// Tell the client whether this tab has items. 
			// FIXED BUG: This was previously being skipped by a 'continue' keyword!
			c.getPA().sendFrame36(769 + i, tab.size() > 0 ? 1 : 0);
		}

		// Send main tab items
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(5382); 
		c.getOutStream().writeWord(Config.BANK_SIZE);
		BankTab current = c.getBank().getCurrentBankTab();
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			if (i > current.size() - 1) {
				c.getOutStream().writeByte(0);
				c.getOutStream().writeWordBigEndianA(0);
			} else {
				BankItem item = current.getItem(i);
				if (item == null) item = new BankItem(-1, 0);
				if (item.getAmount() < 1) item.setAmount(0);
				if (item.getId() > Config.ITEM_LIMIT || item.getId() < 0) item.setId(-1);
				
				if (item.getAmount() > 254) {
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(item.getAmount());
				} else {
					c.getOutStream().writeByte(item.getAmount());
				}
				c.getOutStream().writeWordBigEndianA(item.getId());
			}
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();

		// Cleaned up loop to smartly send all preview tabs
		if (tabId == 0) {
			for (int i = 1; i <= 8; i++) {
				if (c.getBank().getBankTab().length > i && c.getBank().getBankTab()[i].size() > 0) {
					c.getOutStream().createFrameVarSizeWord(53);
					c.getOutStream().writeWord(56430 + (i * 2)); // Automates IDs 56432, 56434, etc.
					c.getOutStream().writeWord(c.getBank().getBankTab()[i].size());
					for (BankItem item : c.getBank().getBankTab()[i].getItems()) {
						if (item.getAmount() > 254) {
							c.getOutStream().writeByte(255);
							c.getOutStream().writeDWord_v2(item.getAmount());
						} else {
							c.getOutStream().writeByte(item.getAmount());
						}
						c.getOutStream().writeWordBigEndianA(item.getId());
					}
					c.getOutStream().endFrameVarSizeWord();
					c.flushOutStream();
				}
			}
		}

		c.getPA().sendFrame126("" + Config.BANK_SIZE, 21025);
		c.getPA().sendFrame126("" + (c.getBank().getBankTab(0).size() + c.getBank().getBankTab(1).size() +
				c.getBank().getBankTab(2).size() + c.getBank().getBankTab(3).size() + c.getBank().getBankTab(4).size()), 21026);
		c.getPA().sendFrame126(""+Integer.toString(tabId), 5292);
	}


	public void resetDepositBox(){
		//synchronized(c) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] > -1) {
				itemCount = i;
			}
		}
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeWord(7423); // bank
			c.getOutStream().writeWord(itemCount + 1);
			for (int i=0; i< itemCount + 1; i++){
				if (c.playerItemsN[i] > 254){
					c.getOutStream().writeByte(255);
					c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
				} else {
					c.getOutStream().writeByte(c.playerItemsN[i]); 	
				}
				if (c.playerItemsN[i] < 1) {
					c.playerItems[i] = 0;
				}
				if (c.playerItems[i] > Config.ITEM_LIMIT || c.playerItems[i] < 0) {
					c.playerItems[i] = Config.ITEM_LIMIT;
				}
				c.getOutStream().writeWordBigEndianA(c.playerItems[i]); 
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
	//}
	}
	/**
	 * Resets temporary worn items. Used in minigames, etc
	 */
	public void resetTempItems() {
		// synchronized (c) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] > -1) {
				itemCount = i;
			}
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(5064);
		c.getOutStream().writeWord(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {

			//c.outStream.writeByte(0);
			if (c.playerItemsN[i] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
			} else {
				c.getOutStream().writeByte(c.playerItemsN[i]);
			}
			if (c.playerItems[i] > Config.ITEM_LIMIT || c.playerItems[i] < 0) {
				c.playerItems[i] = Config.ITEM_LIMIT;
			}
			c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}
	public void resetTempItems(int interfaceId) {
		// synchronized (c) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] > -1) {
				itemCount = i;
			}
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(interfaceId);
		c.getOutStream().writeWord(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {

			//c.outStream.writeByte(0);
			if (c.playerItemsN[i] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(c.playerItemsN[i]);
			} else {
				c.getOutStream().writeByte(c.playerItemsN[i]);
			}
			if (c.playerItems[i] > Config.ITEM_LIMIT || c.playerItems[i] < 0) {
				c.playerItems[i] = Config.ITEM_LIMIT;
			}
			c.getOutStream().writeWordBigEndianA(c.playerItems[i]);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}
		public void resetTempLootItems() {
		// synchronized(c) {
		int itemCount = 0;
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if (c.playerLootItems[i] > -1) {
				itemCount = i;
			}
		}
		c.getOutStream().createFrameVarSizeWord(53);
		c.getOutStream().writeWord(42710);
		c.getOutStream().writeWord(itemCount + 1);
		for (int i = 0; i < itemCount + 1; i++) {
			if (c.playerLootItemsN[i] > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord_v2(c.playerLootItemsN[i]);
			} else {
				c.getOutStream().writeByte(c.playerLootItemsN[i]);
			}
			if (c.playerLootItems[i] > Config.ITEM_LIMIT || c.playerLootItems[i] < 0) {
				c.playerLootItems[i] = Config.ITEM_LIMIT;
			}
			c.getOutStream().writeWordBigEndianA(c.playerLootItems[i]);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		// }
	}
		/**
		 * Banking your item.
		 * 
		 * @param itemId
		 * @param amount
		 * @return
		 */

		public boolean addToBank(int itemId, int amount, int slot, boolean updateView) {
			if (!c.isBanking)
				return false;

			if (!c.getItems().playerHasItem(itemId))
				return false;

			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return false;
			}

			if (c.getBankPin().requiresUnlock()) {
				resetBank();
				c.getBankPin().open(2);
				return false;
			}

			int inventoryItemId = itemId;
			int bankItemId = itemId;

			// Convert noted to unnoted
			if (Item.itemIsNote[itemId]) {
				bankItemId = itemId - 1; // Most noted items are ID+1 of unnoted
			}

			// Check how many we actually have
			int actualAmount = getItemAmount(inventoryItemId);
			if (amount > actualAmount)
				amount = actualAmount;

			if (amount <= 0)
				return false;

			// Bank stores ID + 1 (based on your code pattern)
			BankItem item = new BankItem(bankItemId + 1, amount);
			if (item.getId() <= 0) {
			    item.setId(1); // Fallback to ID 1 (Bronze dagger, safe default)
			}

			BankTab tab = c.getBank().getCurrentBankTab();

			// If item exists in another tab, switch tabs
			for (BankTab t : c.getBank().getBankTab()) {
				if (t != null && t.size() > 0) {
					for (BankItem i : t.getItems()) {
						if (i.getId() == item.getId()) {
							if (t.getTabId() != tab.getTabId()) {
								tab = t;
								break;
							}
						}
					}
				}
			}

			if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
				c.sendMessage("Your bank already holds the max amount of " + getItemName(bankItemId).toLowerCase() + ".");
				return false;
			}

			if (tab.freeSlots() == 0 && !tab.contains(item)) {
				c.sendMessage("Your current bank tab is full.");
				return false;
			}

			// Prevent integer overflow
			long combined = (long) tab.getItemAmount(item) + item.getAmount();
			if (combined >= Integer.MAX_VALUE) {
				int spaceLeft = Integer.MAX_VALUE - tab.getItemAmount(item);
				item.setAmount(spaceLeft);
				if(c.addingItemsToLootBag)
					deleteLootItem2(inventoryItemId, item.getAmount());
				else
					deleteItem2(inventoryItemId, slot, spaceLeft); // delete noted
			} else {
				if(c.addingItemsToLootBag)
					deleteLootItem2(inventoryItemId, item.getAmount());
				else if(item.getAmount() == 1)
					deleteItem(inventoryItemId, slot, item.getAmount()); // delete noted
				else if(item.getAmount() > 1)
					deleteItem2(inventoryItemId, item.getAmount()); // delete noted
			}

			// Add unnoted version to bank
			tab.add(item);
			//c.getPA().sendSound(1877, 0, c.EffectVolume);
			if (updateView) {
			    resetItems(42710);
				resetTempItems();
				resetBank(); // this is what updates the visible interface
			}

			return true;
		}



		



	
	public boolean updateInventory = false;
	
			public void addtoLootbagFromDeposit(int removeId, int removeSlot, int removeAmt) {
    int slotToUpdate = findSlotToUpdate(removeId);

    if (slotToUpdate != -1 && removeSlot != -1) {
        // Item already present in a slot
        if (c.getItems().isStackable(c.playerLootItems[slotToUpdate] - 1)) {
            // Item is stackable, increment the quantity
            c.playerLootItemsN[slotToUpdate] += removeAmt;
        } else {
            // Item is not stackable, find an empty slot
            slotToUpdate = findEmptySlot();
            if (slotToUpdate != -1) {
                c.playerLootItems[slotToUpdate] = removeId + 1;
                c.playerLootItemsN[slotToUpdate] = 1;
            } else {
                // Handle the case when there are no empty slots available
                // Display an error message or take appropriate action
            }
        }
    } else {
		if (c.getItems().isStackable(removeId)) {
            // Item is stackable, increment the quantity
            c.playerLootItems[slotToUpdate] = removeId + 1;
            c.playerLootItemsN[slotToUpdate] = removeAmt;
        } else {
        // Item not present in any slot, find an empty slot
        slotToUpdate = findEmptySlot();
        if (slotToUpdate != -1) {
            c.playerLootItems[slotToUpdate] = removeId + 1;
            c.playerLootItemsN[slotToUpdate] = 1;
        } else {
            // Handle the case when there are no empty slots available
            // Display an error message or take appropriate action
        }
		}
    }

    // Delete the used item
    if (c.getItems().playerHasItem(removeId, removeAmt)) {
        c.getItems().deleteItem(removeId, c.getItems().getItemSlot(removeId), removeAmt);
    } else if (c.getItems().getItemAmount(removeId) < removeAmt) {
        c.getItems().deleteItem(removeId, c.getItems().getItemSlot(removeId), c.getItems().getItemAmount(removeId));
    }
    //resetItems(42710);
    // Update the client's inventory display
    updateInventoryDisplay();
}

private int findSlotToUpdate(int removeId) {
    for (int ITEM = 0; ITEM < 28; ITEM++) {
        if (c.playerLootItems[ITEM] == (removeId + 1)) {
            return ITEM; // Item already present in a slot
        }
    }
    return -1; // Item not present in any slot
}

private int findEmptySlot() {
    for (int ITEM = 0; ITEM < 28; ITEM++) {
        if (c.playerLootItems[ITEM] == 0) {
            return ITEM; // Empty slot found
        }
    }
    return -1; // No empty slots available
}

private void updateInventoryDisplay() {
    // Update the client's inventory display
    for (int ITEM = 0; ITEM < 28; ITEM++) {
        c.getPA().sendFrame34a(43710, c.playerItems[ITEM] - 1, ITEM, c.playerItemsN[ITEM]);
    }
}

public boolean bankLootItem(int itemId, int amount) {
	int inventoryItemId = itemId;
	int bankItemId = itemId;

	// Convert noted to unnoted
	if (Item.itemIsNote[itemId]) {
		bankItemId = itemId - 1; // Most noted items are ID+1 of unnoted
	}

	// Check how many we actually have
	int actualAmount = getLootItemAmount(inventoryItemId);
	if (amount > actualAmount)
		amount = actualAmount;

	if (amount <= 0)
		return false;
	// Bank stores ID + 1 (based on your code pattern)
				BankItem item = new BankItem(bankItemId + 1, amount);
				if (item.getId() <= 0) {
				    item.setId(1); // Fallback to ID 1 (Bronze dagger, safe default)
				}

				BankTab tab = c.getBank().getCurrentBankTab();

				// If item exists in another tab, switch tabs
				for (BankTab t : c.getBank().getBankTab()) {
					if (t != null && t.size() > 0) {
						for (BankItem i : t.getItems()) {
							if (i.getId() == item.getId()) {
								if (t.getTabId() != tab.getTabId()) {
									tab = t;
									break;
								}
							}
						}
					}
				}

				if (tab.getItemAmount(item) == Integer.MAX_VALUE) {
					c.sendMessage("Your bank already holds the max amount of " + getItemName(bankItemId).toLowerCase() + ".");
					return false;
				}

				if (tab.freeSlots() == 0 && !tab.contains(item)) {
					c.sendMessage("Your current bank tab is full.");
					return false;
				}

				// Prevent integer overflow
				long combined = (long) tab.getItemAmount(item) + item.getAmount();
				if (combined >= Integer.MAX_VALUE) {
					int spaceLeft = Integer.MAX_VALUE - tab.getItemAmount(item);
					item.setAmount(spaceLeft);
					deleteLootItem(inventoryItemId, spaceLeft); // delete noted
				} else {
					deleteLootItem(inventoryItemId, item.getAmount()); // delete noted
				}

				// Add unnoted version to bank
				tab.add(item);
				resetTempItems();
		resetTempLootItems();
		resetBank(); // this is what updates the visible interface
		resetItems(42710);
	return true;
}


private int findLootSlot(int itemId) {
    for (int i = 0; i < c.playerLootItems.length; i++) {
        if (c.playerLootItems[i] == itemId) {
            return i;
        }
    }
    return -1;
}


	public void updateInventory() {
		updateInventory = false;
		resetItems(3214);
	}
	public int[] Nests = { 5291, 5292, 5293, 5294, 5295, 5296, 5297, 5298, 5299, 5300, 5301, 5302, 5303, 5304 };
	public void handleNests(int itemId) {
		int reward = Nests[Misc.random(14)];
		addItem(reward, 3 + Misc.random(5));
		deleteItem(itemId, 1);
		c.sendMessage("You search the nest");
	}
	/**
	 * Handles tradable items.
	 */
	public boolean isTradable(int itemId) {
		if (itemId == 12899 && c.getToxicTridentCharge() > 0 || itemId == 11907 && c.getTridentCharge() > 0 || itemId == 22323 && c.getSangStaffCharge() > 0 || itemId == 33673 && c.getSangStaffCharge() > 0) {
			c.sendMessage("You cannot trade your trident whilst it has a charge.");
			return false;
		}
		if (getItemName(itemId) != null && getItemName(itemId).contains("graceful")) {
			return false;
		}
		boolean CANNOT_SHARE = IntStream.of(Config.NOT_SHAREABLE).anyMatch(shareable -> shareable == itemId);
		if (CANNOT_SHARE && itemId != 13307) {
			return false;
		}
		if (itemId == 13307) {
			return true;
		}
		return true;
	}
	public int getWornItemSlot(int itemId) {
		for (int i = 0; i < c.playerEquipment.length; i++)
			if (c.playerEquipment[i] == itemId)
				return i;
		return -1;
	}
	public Map.Entry<BankTab, BankItem> findItemAndTab(int itemId) {
		for (BankTab tab : c.getBank().getBankTab()) {
			BankItem item = tab.getItemById(itemId + 1);
			if (item != null) return Map.entry(tab, item);
		}
		return null;
	}

	public void removeFromBank(int itemId, int itemAmount, boolean updateView) {
		if (!c.isBanking || itemAmount <= 0)
			return;

		if (c.getBankPin().requiresUnlock()) {
			resetBank();
			c.getBankPin().open(2);
			return;
		}

		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			c.getPA().removeAllWindows();
			return;
		}

		// Find item in *any* tab, not just current
		Map.Entry<BankTab, BankItem> result = findItemAndTab(itemId);
		if (result == null)
			return;

		BankTab tab = result.getKey();
		BankItem item = result.getValue();

		boolean noted = false;
		boolean stackable = isStackable(itemId);

		if (c.takeAsNote) {
			int unnotedId = itemId;
			int notedId = unnotedId+1;

			if (notedId > 0 && Item.itemIsNote[notedId]) {
				noted = true;
				itemId = notedId; // Use this ID for inventory operations below
			} else {
				c.sendMessage("This item cannot be taken out as noted.");
				return;
			}
		}



		// Inventory space check
		boolean hasSpace = freeSlots() > 0 || playerHasItem(itemId) || (noted && playerHasItem(itemId + 1));
		if (!hasSpace) {
			c.sendMessage("Not enough space in your inventory.");
			return;
		}
		int availableAmount = tab.getItemAmount(item);
		int removeAmount = Math.min(itemAmount, item.getAmount());

		if (!stackable && !noted && freeSlots() < removeAmount)
			removeAmount = freeSlots();

		if (removeAmount <= 0) {
			if (!c.placeHolderWarning) {
				c.lastPlaceHolderWarning = item.getId();
				c.sendMessage("@cr10@@red@Are you sure you want to release the placeholder of " + Item.getItemName(item.getId() - 1) + "?");
				c.sendMessage("@cr10@@red@If so, click the item once more.");
				c.placeHolderWarning = true;
				return;
			} else if (item.getId() != c.lastPlaceHolderWarning) {
				c.placeHolderWarning = false;
				return;
			}
			c.placeHolderWarning = false;
		}

		int invId = item.getId() - 1;
		int amountToAdd = removeAmount;

		addItem(itemId, amountToAdd);
		int type = (removeAmount <= 0) ? 1 : 0;
		tab.remove(new BankItem(item.getId(), removeAmount), type, c.placeHolders);
		if (tab.size() == 0 && tab == c.getBank().getCurrentBankTab()) {
			c.getBank().setCurrentBankTab(c.getBank().getBankTab(0));
		}

		if (updateView)
			resetBank();

		c.sendMessage("Withdrew " + getItemName(item.getId() - 1) + "");
		c.sendMessage("Withdrew " + (item.getId() - 1) + "");
		c.getItems().resetItems(5064);
	}


	
	public int itemAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				tempAmount += c.playerItemsN[i];
			}
		}
		return tempAmount;
	}
	public int itemLootAmount(int itemID) {
		int tempAmount = 0;
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if (c.playerLootItems[i] == itemID) {
				tempAmount += c.playerLootItemsN[i];
			}
		}
		return tempAmount;
	}
	public boolean isStackable(int itemID) {
		return Item.itemStackable[itemID];
	}

	/**
	 * Update Equip tab
	 **/

	public void setEquipment(int wearID, int amount, int targetSlot) {
		// synchronized(c) {
		c.getOutStream().createFrameVarSizeWord(34);
		c.getOutStream().writeWord(1688);
		c.getOutStream().writeByte(targetSlot);
		c.getOutStream().writeWord(wearID + 1);
		if (amount > 254) {
			c.getOutStream().writeByte(255);
			c.getOutStream().writeDWord(amount);
		} else {
			c.getOutStream().writeByte(amount);
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		c.playerEquipment[targetSlot] = wearID;
		c.playerEquipmentN[targetSlot] = amount;
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	
	public void moveItems(int from, int to, int moveWindow, boolean insertMode) {
	    // Inventory swapping
	    if (moveWindow == 3214) {
	        swapPlayerItems(from, to, 3214);
	        return;
	    }

	    // Deposit box swapping
	    if (moveWindow == 5064) {
	        swapPlayerItems(from, to, 5064);
	        return;
	    }

	    // Bank preview tab handling (interfaces 56432 to 56448, spaced by 2)
	    if (moveWindow >= 56432 && moveWindow <= 56448) {
	        int fromTabId = (moveWindow - 56432) / 2 + 1; // Adjust tab ID based on interface
	        
	        BankTab fromTab = c.getBank().getBankTab()[fromTabId];
	        if (fromTab == null || fromTab.size() == 0 || from >= fromTab.size()) return;

	        // 1. Preview-to-Preview tab move
	        if (to >= 3000) {
	            int toTabId = to - 3000;
	            if (!isValidTabId(fromTabId) || !isValidTabId(toTabId)) return;

	            BankTab toTab = c.getBank().getBankTab()[toTabId];
	            BankItem item = fromTab.getItem(from);
	            if (item == null) return;

	            fromTab.remove(item, from, false);
	            toTab.addAt(item, 0);
	            rearrangeBank();
	            resetBank();
	            return;
	        }

	        // 2. Preview-to-Main Top Icons move
	        if (to >= 1000 && to < 2000) {
	            int toTabId = to - 1000;
	            if (!isValidTabId(toTabId)) return;
	            
	            BankTab toTab = c.getBank().getBankTab()[toTabId];
	            BankItem item = fromTab.getItem(from);
	            if (item == null) return;

	            fromTab.remove(item, from, false);
	            toTab.addAt(item, 0);
	            rearrangeBank();
	            resetBank();
	            return;
	        }

	        // 3. Intra-Tab move (Moving item WITHIN the same preview tab)
	        if (to < 1000) {
	            if (to >= fromTab.size()) {
	                to = fromTab.size() - 1; // Cap to the last item
	            }
	            if (!insertMode) {
	                BankItem temp = fromTab.getItem(from);
	                fromTab.setItem(from, fromTab.getItem(to));
	                fromTab.setItem(to, temp);
	            } else {
	                insertShift(fromTab, from, to);
	            }
	            rearrangeBank();
	            resetBank();
	            return;
	        }
	        return;
	    }

	    // Bank main tab handling (interface 5382)
	    if (moveWindow == 5382) {
	        if (c.getBank().getBankSearch().isSearching()) {
	            c.getBank().getBankSearch().reset();
	            return;
	        }
	        if (c.getBankPin().requiresUnlock()) {
	            resetBank();
	            c.isBanking = false;
	            c.inSafeBox = false;
	            c.getBankPin().open(2);
	            return;
	        }

	        BankTab currentTab = c.getBank().getCurrentBankTab();

	        // Move item to another tab icon
	        if (to >= 1000) {
	            handleMoveToOtherTab(from, to, currentTab);
	            return;
	        }

	        // Move within same tab
	        if (from >= currentTab.size()) {
	            rearrangeBank();
	            resetBank();
	            return;
	        }
	        
	        // Prevent dragging to an empty space from aborting the drag completely
	        if (to >= currentTab.size()) {
	            to = currentTab.size() - 1; 
	        }

	        if (!insertMode) {
	            BankItem temp = currentTab.getItem(from);
	            currentTab.setItem(from, currentTab.getItem(to));
	            currentTab.setItem(to, temp);
	        } else {
	            insertShift(currentTab, from, to);
	        }
	        rearrangeBank();
	        resetBank();
	    }

	    resetTempItems();
	}

	private void swapPlayerItems(int from, int to, int interfaceId) {
	    int tempI = c.playerItems[from];
	    int tempN = c.playerItemsN[from];
	    c.playerItems[from] = c.playerItems[to];
	    c.playerItemsN[from] = c.playerItemsN[to];
	    c.playerItems[to] = tempI;
	    c.playerItemsN[to] = tempN;
	    resetItems(interfaceId);
	}

	private boolean isValidTabId(int tabId) {
	    return tabId >= 1 && tabId <= 8;
	}

	private void handleMoveToOtherTab(int from, int to, BankTab currentTab) {
	    if (to >= 3001 && to <= 3009) {
	        // main tab → preview tab
	        int tabId = to - 3000;
	        if (!isValidTabId(tabId)) return;

	        BankTab mainTab = c.getBank().getBankTab()[0];
	        if (from >= mainTab.size()) return;

	        BankItem item = mainTab.getItem(from);
	        if (item == null) return;

	        BankItem copy = new BankItem(item.getId(), item.getAmount());
	        mainTab.remove(copy, from, false);
	        c.getBank().getBankTab()[tabId].add(copy);
	        rearrangeBank();
	        resetBank();
	        return;
	    }

	    int tabId = to - 1000;
	    if (tabId < 0 || tabId >= c.getBank().getBankTab().length) {
	        resetBank();
	        return;
	    }
	    if (tabId == currentTab.getTabId()) {
	        c.sendMessage("You cannot add an item to the same tab.");
	        resetBank();
	        return;
	    }
	    if (from >= currentTab.size()) {
	        resetBank();
	        return;
	    }

	    BankItem item = currentTab.getItem(from);
	    if (item == null) {
	        resetBank();
	        return;
	    }

	    BankItem itemCopy = new BankItem(item.getId(), item.getAmount());
	    currentTab.remove(itemCopy, from, false);
	    c.getBank().getBankTab()[tabId].add(itemCopy);
        rearrangeBank();
	    resetBank();
	}

	private void insertShift(BankTab currentTab, int from, int to) {
	    java.util.concurrent.CopyOnWriteArrayList<BankItem> items = currentTab.getItems();

	    if (from < 0 || from >= items.size() || to < 0) {
	        return;
	    }

	    BankItem moving = items.get(from);
	    if (moving == null) {
	        resetBank();
	        return;
	    }

	    // Remove the item (Java automatically shifts everything else left)
	    items.remove(from);

	    // Re-insert the item (Java automatically shifts everything else right)
	    if (to >= items.size()) {
	        items.add(moving); // Append to the end
	    } else {
	        items.add(to, moving); // Squeeze it into the exact slot
	    }
	}


	public void swapBankItem(int from, int to) {
	    BankItem item = c.getBank().getCurrentBankTab().getItem(from);
	    BankItem item2 = c.getBank().getCurrentBankTab().getItem(to);
	    c.getBank().getCurrentBankTab().setItem(from, item2);
	    c.getBank().getCurrentBankTab().setItem(to, item);
	}


	/**
	 * delete Item
	 **/

	public void deleteEquipment(int i, int slot) {
		// synchronized (c) {
		if (PlayerHandler.players[c.getIndex()] == null) {
			return;
		}
		if (i < 0) {
			return;
		}

		c.playerEquipment[slot] = -1;
		c.playerEquipmentN[slot] = c.playerEquipmentN[slot] - 1;
		c.getOutStream().createFrame(34);
		c.getOutStream().writeWord(6);
		c.getOutStream().writeWord(1688);
		c.getOutStream().writeByte(slot);
		c.getOutStream().writeWord(0);
		c.getOutStream().writeByte(0);
		getBonus();
		if (slot == c.playerWeapon) {
			sendWeapon(-1, "Unarmed");
		}
		resetBonus();
		getBonus();
		writeBonus();
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
		// }
	}

	public void deleteItem(int id, int amount) {
		if (id <= 0 || amount <= 0)
			return;

		for (int j = 0; j < c.playerItems.length; j++) {
			if (c.playerItems[j] == id + 1) {
				if (c.playerItemsN[j] > amount) {
					c.playerItemsN[j] -= amount;
					break;
				} else {
					amount -= c.playerItemsN[j];
					c.playerItems[j] = 0;
					c.playerItemsN[j] = 0;
				}
			}
		}
		resetItems(3214);
	}

	public void deleteLootItem(int id, int amount) {
		if (id <= 0)
			return;
		for (int j = 0; j < c.playerLootItems.length; j++) {
			if (amount <= 0)
				break;
			if (c.playerLootItems[j] == id + 1) {
				c.playerLootItems[j] = 0;
				c.playerLootItemsN[j] = 0;
				amount--;
			}
		}
		resetItems(42710);
	}
	public void deleteItem(int id, int slot, int amount) {
		if (id <= 0 || slot < 0) {
			return;
		}
		if (c.playerItems[slot] == (id + 1)) {
			if (c.playerItemsN[slot] > amount) {
				c.playerItemsN[slot] -= amount;
			} else {
				c.playerItemsN[slot] = 0;
				c.playerItems[slot] = 0;
			}

			PlayerSave.saveGame(c);
			resetItems(3214);
		}
	}
	public void deleteItem2(int id, int slot, int amount) {
		int am = amount;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (am == 0) {
				break;
			}
			if (c.playerItems[i] == (id + 1)) {
				if (c.playerItemsN[i] > amount) {
					c.playerItemsN[i] -= amount;
					break;
				} else {
					c.playerItems[i] = 0;
					c.playerItemsN[i] = 0;
					am--;
				}
			}
		}
		PlayerSave.saveGame(c);
		resetItems(3214);
	}
	public void deleteLootItem2(int id, int amount) {
		int am = amount;
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if (am == 0) {
				break;
			}
			if (c.playerLootItems[i] == (id + 1)) {
				if (c.playerLootItemsN[i] >= amount) {
					c.playerLootItemsN[i] -= amount;
					break;
				} else {
					c.playerLootItems[i] = 0;
					c.playerLootItemsN[i] = 0;
					am--;
				}
			}
		}
		PlayerSave.saveGame(c);
		resetItems(42710);
	}
	public void deleteItem2(int id, int amount) {
		int am = amount;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (am == 0) {
				break;
			}
			if (c.playerItems[i] == (id + 1)) {
				if (c.playerItemsN[i] > amount) {
					c.playerItemsN[i] -= amount;
					break;
				} else {
					c.playerItems[i] = 0;
					c.playerItemsN[i] = 0;
					am--;
				}
			}
		}
		PlayerSave.saveGame(c);
		resetItems(3214);
	}

	/**
	 * Delete Arrows
	 **/
	/**
	 * Delete arrows.
	 **/
	public void deleteArrow() {
		if (c.getItems().isWearingItem(10499, c.playerCape) || c.getItems().isWearingItem(22109, c.playerCape)) {
			if (RandomUtils.nextInt(0, 15) > 1) {
				return;
			}
		}
		int arrow = c.playerEquipment[c.playerArrows];
		int stock = c.playerEquipmentN[c.playerArrows];
		int slot = c.playerArrows;

		if (stock > 1) {
			c.getItems().wearItem(arrow, stock - 1, slot);
		} else if (stock == 1) {
			c.getItems().wearItem(-1, 0, slot);
		}
	}

	public void deleteEquipment() {
		// synchronized(c) {
		if (c.playerEquipmentN[c.playerWeapon] == 1) {
			c.getItems().deleteEquipment(c.playerEquipment[c.playerWeapon],
					c.playerWeapon);
		}
		if (c.playerEquipmentN[c.playerWeapon] != 0) {
			c.getOutStream().createFrameVarSizeWord(34);
			c.getOutStream().writeWord(1688);
			c.getOutStream().writeByte(c.playerWeapon);
			c.getOutStream().writeWord(c.playerEquipment[c.playerWeapon] + 1);
			if (c.playerEquipmentN[c.playerWeapon] - 1 > 254) {
				c.getOutStream().writeByte(255);
				c.getOutStream().writeDWord(
						c.playerEquipmentN[c.playerWeapon] - 1);
			} else {
				c.getOutStream().writeByte(
						c.playerEquipmentN[c.playerWeapon] - 1);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
			c.playerEquipmentN[c.playerWeapon] -= 1;
		}
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}
	/**
	 * Adds an item to the players inventory, bank, or drops it. It will do this under any circumstance so if it cannot be added to the inventory it will next try to send it to the
	 * bank and if it cannot, it will drop it.
	 * 
	 * @param itemId the item
	 * @param amount the amount of said item
	 */
	public void addItemUnderAnyCircumstance(int itemId, int amount) {
		if (!addItem(itemId, amount)) {
			/*if (c.getMode().isUltimateIronman()) {
				World.getWorld().getItemHandler().createGroundItem(c, itemId, c.getX(), c.getY(), c.getHeight(), amount);
				c.sendMessage("@red@Your box has been dropped to the ground!");
				return;
			}*/
			sendItemToAnyTabOrDrop(new BankItem(itemId, amount), c.getX(), c.getY());
		}
	}

	public boolean bankContains(int itemId) {
		for (BankTab tab : c.getBank().getBankTab())
			if (tab.contains(new BankItem(itemId + 1)))
				return true;
		return false;
	}
	public boolean bankContains(int itemId, int itemAmount) {
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.containsAmount(new BankItem(itemId + 1, itemAmount))) {
				return true;
			}
		}
		return false;
	}
	public boolean isBankSpaceAvailable(BankItem item) {
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.contains(item)) {
				return tab.spaceAvailable(item);
			}
		}
		return false;
	}
	/**
	 * The x and y represents the possible x and y location of the dropped item if in fact it cannot be added to the bank.
	 */
	public void sendItemToAnyTabOrDrop(BankItem item, int x, int y) {
		item = new BankItem(item.getId() + 1, item.getAmount());
		if (Item.itemIsNote[item.getId()] && bankContains(item.getId() - 2)) {
			if (isBankSpaceAvailable(item)) {
				sendItemToAnyTab(item.getId() - 1, item.getAmount());
			} else {
				World.getWorld().getItemHandler().createGroundItem(c, item.getId() - 1, x, y, c.getHeight(), item.getAmount());
			}
		} else {
			sendItemToAnyTab(item.getId() - 1, item.getAmount());
		}
	}

	/**
	 * Sends an item to the bank in any tab possible.
	 * 
	 * @param itemId the item id
	 * @param amount the item amount
	 */
	public void sendItemToAnyTab(int itemId, int amount) {
		BankItem item = new BankItem(itemId, amount);
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.contains(item)) {
				c.getBank().setCurrentBankTab(tab);
				addItemToBank(itemId, amount);
				return;
			}
		}
		for (BankTab tab : c.getBank().getBankTab()) {
			if (tab.freeSlots() > 0) {
				c.getBank().setCurrentBankTab(tab);
				addItemToBank(itemId, amount);
				return;
			}
		}
	}
	public void addItemToBank(int itemId, int amount) {
		BankTab tab = c.getBank().getCurrentBankTab();
		BankItem item = new BankItem(itemId + 1, amount);
		if (Item.itemIsNote[itemId]) {
			item = new BankItem(World.getWorld().getItemHandler().getCounterpart(itemId) + 1, amount);
		}
		Iterator<BankTab> iterator = Arrays.asList(c.getBank().getBankTab()).iterator();
		outer: while (iterator.hasNext()) {
			BankTab t = iterator.next();
			if (t != null && t.size() > 0) {
				for (BankItem i : t.getItems()) {
					if (i.getId()==item.getId()) {
						if (t.getTabId()!=tab.getTabId()) {
							tab=t;
							break outer;
						}
					}
				}
			}
		}
		if (isNoted(itemId)) {
			item = new BankItem(World.getWorld().getItemHandler().ItemList[itemId].getCounterpartId() + 1, amount);
		}
		if (tab.freeSlots() == 0) {
			c.sendMessage("The item has been dropped on the floor.");
			World.getWorld().getItemHandler().createGroundItem(c, itemId, c.getX(), c.getY(), c.getHeight(), amount, c.getIndex());
			return;
		}
		long totalAmount = ((long) tab.getItemAmount(item) + (long) item.getAmount());
		if (totalAmount >= Integer.MAX_VALUE) {
			c.sendMessage("The item has been dropped on the floor.");
			World.getWorld().getItemHandler().createGroundItem(c, itemId, c.getX(), c.getY(), c.getHeight(), amount, c.getIndex());
			return;
		}
		tab.add(item);
		resetTempItems();
		if (c.isBanking) {
			resetBank();
		}
		c.sendMessage(getItemName(itemId) + " x" + item.getAmount() + " has been added to your bank.");
	}

	/**
	 * Check to see if an item is noted.
	 * 
	 * @param itemId The item ID of the item which is to be checked.
	 * @return True in case the item is noted, False otherwise.
	 */
	public boolean isNoted(int itemId) {

		if (itemId<0) {
			return false;
		}
		ItemList list=World.getWorld().getItemHandler().ItemList[itemId];
		return list!=null&&list.itemDescription!=null&&list.itemDescription.startsWith("Swap this note at any bank");

	}
	/**
	 * Dropping Arrows
	 * @param target 
	 **/

	public void dropArrowNpc(NPC target) {
		if (c.playerEquipment[c.playerCape] == 10499)
			return;
		int enemyX = NPCHandler.npcs[c.oldNpcIndex].getX();
		int enemyY = NPCHandler.npcs[c.oldNpcIndex].getY();
		int enemyH = NPCHandler.npcs[c.oldNpcIndex].getH();
		if (c.playerEquipment[c.playerWeapon] == 12926){
			if (Misc.random(10) >= 4) {
			if (World.getWorld().getItemHandler().itemAmount(c.DartType, enemyX, enemyY) == 0) {
				World.getWorld().getItemHandler().createGroundItem(c, c.DartType, enemyX,
						enemyY, enemyH, 1, c.getId());
			} else if (World.getWorld().getItemHandler().itemAmount(c.DartType, enemyX,
					enemyY) != 0) {
				int amount = World.getWorld().getItemHandler().itemAmount(c.DartType,
						enemyX, enemyY);
				World.getWorld().getItemHandler().removeGroundItem(c, c.DartType, enemyX,
						enemyY, false);
				World.getWorld().getItemHandler().createGroundItem(c, c.DartType, enemyX,
						enemyY, enemyH, amount + 1, c.getId());
			}
		}
		}
		if (Misc.random(10) >= 4) {
			if (World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
				World.getWorld().getItemHandler().createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, enemyH, 1, c.getId());
			} else if (World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed, enemyX,
					enemyY) != 0) {
				int amount = World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed,
						enemyX, enemyY);
				World.getWorld().getItemHandler().removeGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, false);
				World.getWorld().getItemHandler().createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, enemyH, amount + 1, c.getId());
			}
		}
	}

	public void dropArrowPlayer() {
		int enemyX = PlayerHandler.players[c.oldPlayerIndex].getX();
		int enemyY = PlayerHandler.players[c.oldPlayerIndex].getY();
		int enemyH = PlayerHandler.players[c.oldPlayerIndex].getHeight();
		if (c.playerEquipment[c.playerCape] == 10499)
			return;
		
		if (c.playerEquipment[c.playerWeapon] == 12926){
			if (Misc.random(10) >= 4) {
			if (World.getWorld().getItemHandler().itemAmount(c.DartType, enemyX, enemyY) == 0) {
				World.getWorld().getItemHandler().createGroundItem(c, c.DartType, enemyX,
						enemyY, enemyH, 1, c.getId());
			} else if (World.getWorld().getItemHandler().itemAmount(c.DartType, enemyX,
					enemyY) != 0) {
				int amount = World.getWorld().getItemHandler().itemAmount(c.DartType,
						enemyX, enemyY);
				World.getWorld().getItemHandler().removeGroundItem(c, c.DartType, enemyX,
						enemyY, false);
				World.getWorld().getItemHandler().createGroundItem(c, c.DartType, enemyX,
						enemyY, enemyH, amount + 1, c.getId());
			}
		}
		}
		if (Misc.random(10) >= 4) {
			if (World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
				World.getWorld().getItemHandler().createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, enemyH, 1, c.getId());
			} else if (World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed, enemyX,
					enemyY) != 0) {
				int amount = World.getWorld().getItemHandler().itemAmount(c.rangeItemUsed,
						enemyX, enemyY);
				World.getWorld().getItemHandler().removeGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, false);
				World.getWorld().getItemHandler().createGroundItem(c, c.rangeItemUsed, enemyX,
						enemyY, enemyH, amount + 1, c.getId());
			}
		}
	}

	public void removeAllItems() {
		for (int i = 0; i < c.playerItems.length; i++) {
			c.playerItems[i] = 0;
		}
		for (int i = 0; i < c.playerItemsN.length; i++) {
			c.playerItemsN[i] = 0;
		}
		PlayerSave.saveGame(c);
		resetItems(3214);
	}

	public int freeSlots() {
		int freeS = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] <= 0) {
				freeS++;
			}
		}
		return freeS;
	}

	public int findItem(int id, int[] items, int[] amounts) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if (((items[i] - 1) == id) && (amounts[i] > 0)) {
				return i;
			}
		}
		return -1;
	}
	public static int getItemIdByName(String name) {
	    name = name.toLowerCase();
	    for (int i = 0; i < World.getWorld().getItemHandler().ItemList.length; i++) {
	        ItemList item = World.getWorld().getItemHandler().ItemList[i];
	        if (item != null && item.itemName != null && item.itemName.toLowerCase().equals(name)) {
	            return item.itemId;
	        }
	    }
	    return -1;
	}

	public static String getItemName(int itemId) {
		if (itemId < 0) {
			return "Unarmed";
		}
		ItemList itemList = World.getWorld().getItemHandler().ItemList[itemId];
		if (itemList == null) {
			return "Unarmed";
		}
		return itemList.itemName;
	}
	public boolean isWearingItems() {
		return freeEquipmentSlots() < 14;
	}
	public int freeEquipmentSlots() {
		int slots = 0;
		for (int i = 0; i < c.playerEquipment.length; i++) {
			if (c.playerEquipment[i] <= 0) {
				slots++;
			}
		}
		return slots;
	}
	public int getItemId(String itemName) {
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (World.getWorld().getItemHandler().ItemList[i] != null) {
				if (World.getWorld().getItemHandler().ItemList[i].itemName
						.equalsIgnoreCase(itemName)) {
					return World.getWorld().getItemHandler().ItemList[i].itemId;
				}
			}
		}
		return -1;
	}

	public int getItemSlot(int ItemID) {
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}	
	public int getLootItemSlot(int ItemID) {
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if ((c.playerLootItems[i] - 1) == ItemID) {
				return i;
			}
		}
		return -1;
	}
	public int getLootItemAmount(int ItemID) {
		int itemCount = 0;
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if ((c.playerLootItems[i] - 1) == ItemID) {
				itemCount += c.playerLootItemsN[i];
			}
		}
		return itemCount;
	}
	public int getItemAmount(int ItemID) {
		int itemCount = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if ((c.playerItems[i] - 1) == ItemID) {
				itemCount += c.playerItemsN[i];
			}
		}
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if ((c.playerLootItems[i] - 1) == ItemID) {
				itemCount += c.playerLootItemsN[i];
			}
		}
		return itemCount;
	}

	public boolean playerHasItem(int itemID, int amt, int slot) {
		itemID++;
		int found = 0;
		if (c.playerItems[slot] == (itemID)) {
			for (int i = 0; i < c.playerItems.length; i++) {
				if (c.playerItems[i] == itemID) {
					if (c.playerItemsN[i] >= amt) {
						return true;
					} else {
						found++;
					}
				}
			}
			if (found >= amt) {
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean playerHasItem(int itemID) {
		itemID++;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID)
				return true;
		}
		for (int i = 0; i < c.playerLootItems.length; i++) {
			if (c.playerLootItems[i] == itemID && playerHasItem(12791, 1))
				return true;
		}
		return false;
	}

	public boolean playerHasItem(int itemID, int amt) {
		itemID++;
		int found = 0;
		for (int i = 0; i < c.playerItems.length; i++) {
			if (c.playerItems[i] == itemID) {
				if (c.playerItemsN[i] >= amt) {
					return true;
				} else {
					found++;
				}
			}
		}
		if (found >= amt) {
			return true;
		}
		return false;
	}

	public int getUnnotedItem(int ItemID) {
		int NewID = ItemID - 1;
		String NotedName = "";
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (World.getWorld().getItemHandler().ItemList[i] != null) {
				if (World.getWorld().getItemHandler().ItemList[i].itemId == ItemID) {
					NotedName = World.getWorld().getItemHandler().ItemList[i].itemName;
				}
			}
		}
		for (int i = 0; i < Config.ITEM_LIMIT; i++) {
			if (World.getWorld().getItemHandler().ItemList[i] != null) {
				if (World.getWorld().getItemHandler().ItemList[i].itemName == NotedName) {
					if (World.getWorld().getItemHandler().ItemList[i].itemDescription
							.startsWith("Swap this note at any bank for a") == false) {
						NewID = World.getWorld().getItemHandler().ItemList[i].itemId;
						break;
					}
				}
			}
		}
		return NewID;
	}

	/**
	 * Drop Item
	 **/

	public void createGroundItem(int itemID, int itemX, int itemY,
			int itemAmount) {
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC(itemY - 8 * c.getLastKnownLocation().getRegionY());
		c.getOutStream().writeByteC(itemX - 8 * c.getLastKnownLocation().getRegionX());
		c.getOutStream().createFrame(44);
		c.getOutStream().writeWordBigEndianA(itemID);
		c.getOutStream().writeDWord(itemAmount);
		c.getOutStream().writeByte(0);
		c.flushOutStream();
	}

	/**
	 * Pickup Item
	 **/

	public void removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
		if (c == null) {
			return;
		}
		c.getOutStream().createFrame(85);
		c.getOutStream().writeByteC(itemY - 8 * c.getLastKnownLocation().getRegionY());
		c.getOutStream().writeByteC(itemX - 8 * c.getLastKnownLocation().getRegionX());
		c.getOutStream().createFrame(156);
		c.getOutStream().writeByteS(0);
		c.getOutStream().writeWord(itemID);
		c.flushOutStream();
	}

	/**
	 * Checks if a player owns a cape.
	 * 
	 * @return
	 */
	public boolean ownsCape() {
		if (c.getItems().playerHasItem(2412, 1) || c.getItems().playerHasItem(2413, 1) || c.getItems().playerHasItem(2414, 1))
			return true;
		for (int j = 0; j < Config.BANK_SIZE; j++) {
			if (c.bankItems[j] == 2412 || c.bankItems[j] == 2413 || c.bankItems[j] == 2414)
				return true;
		}
		return c.playerEquipment[c.playerCape]==2413||c.playerEquipment[c.playerCape]==2414||c.playerEquipment[c.playerCape]==2415;
	}


	public boolean hasAllShards() {
		return playerHasItem(11712, 1) && playerHasItem(11712, 1)
				&& playerHasItem(11714, 1);
	}

	public void makeBlade() {
		deleteItem(11710, 1);
		deleteItem(11712, 1);
		deleteItem(11714, 1);
		addItem(11690, 1);
		c.sendMessage("You combine the shards to make a blade.");
	}

	public void makeGodsword(int i) {
		if (playerHasItem(11690) && playerHasItem(i)) {
			deleteItem(11690, 1);
			deleteItem(i, 1);
			addItem(i - 8, 1);
			c.sendMessage("You combine the hilt and the blade to make a godsword.");
		}
	}

	public boolean isHilt(int i) {
		return i >= 11702 && i <= 11708 && i % 2 == 0;
	}

	private static final int[] WEALTH_RINGS = { 2572, 11980, 11982, 11984, 11986, 11988, 12785, 20786, 20787, 20788, 20879, 20790, 33798, 33806, 33799, 33807, 33800, 33809 };
	
	public boolean isWearingLuckRing() {
		return IntStream.of(WEALTH_RINGS).anyMatch(ring -> isWearingItem(ring));
	}

	public boolean playerHasAllItems(int... items) {

		for (int item : items) {
			if (!playerHasItem(item))
				return false;
		}
		return true;
	}

}