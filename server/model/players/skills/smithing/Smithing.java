package server.model.players.skills.smithing;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.items.ItemAssistant;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.smithing.Smithing.SmithBar;
import server.model.players.Client;

public class Smithing {

	private static int addItem;
	private static int xp;
	private static int removeItem;
	private static int removeAmount;
	private static int makeTimes;
	
	private static boolean hasItem(Player player, int type, String string) {
		return (ItemAssistant.getItemName(type).contains(string));
	}

	public static void readInput(Player c, int levelReq, int type, int amountToMake) {
		if (hasItem(c, type, "Bronze")) {
			 removeItem = 2349;
		} else if (hasItem(c, type, "Iron")) {
			 removeItem = 2351;
		} else if (hasItem(c, type, "Steel")) {
			 removeItem = 2353;
		} else if (hasItem(c, type, "Mith")) {
			 removeItem = 2359;
		} else if (hasItem(c, type, "Adam") || hasItem(c, type, "Addy")) {
			 removeItem = 2361;
		} else if (hasItem(c, type, "Rune") || hasItem(c, type, "Runite")) {
			 removeItem = 2363;
		}
		checkBar(c, levelReq, amountToMake, type);
	}
	 
	private static void checkBar(Player player, int level, int amountToMake, int type) {
    	SmithingData item = SmithingData.forId(type);
		if (item != null) {
			if (player.getSkills().getLevel(Skill.SMITHING) >= item.getLvl()) {
				if (type == item.getId()) {
					addItem = item.getId();
					System.out.println("type = "+type);
					removeAmount = item.getAmount();
					makeTimes = amountToMake;
					xp = item.getXp();
			    	smithItem(player, addItem, removeItem, removeAmount, makeTimes, xp);
				}
			} else {
				 player.sendMessage("You don't have a high enough level to make this item.");
			}
		}
    }

    public static void smithItem(Player player, int addItem, int removeItem1, int removeItem2, int timesToMake, int XP) {
		player.doAmount = timesToMake;
		player.getPA().closeAllWindows();
		String name = ItemAssistant.getItemName(addItem);
		player.turnPlayerTo(player.objectX, player.objectY);
		if (player.getItems().playerHasItem(removeItem1, 1)) {
			if (!player.isSmithing) {
				player.isSmithing = true;
				player.startAnimation(898);
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (player.doAmount <= 0 || !player.getItems().playerHasItem(removeItem1, 1) 
								|| !player.isSmithing || player.isBanking 
								|| player.isSmelting) {
							container.stop();
						} else {
							player.getPA().sendSound(468, 100, 0);
							if (name.contains("ball")) {
								player.sendMessage("You make some " + name.toLowerCase() + "s.");
							} else if (name.charAt(name.length() -1) == 's') {
								player.sendMessage("You make some " + name.toLowerCase() + ".");
							} else {
								if (name.charAt(1) == 'a' || name.charAt(1) == 'e' || name.toLowerCase().charAt(1) == 'i' || name.charAt(1) == 'o' || name.charAt(1) == 'u') {
									player.sendMessage("You make an " + name.toLowerCase() + ".");
								} else {
									player.sendMessage("You make a " + name.toLowerCase() + ".");
								}
							}
							player.getItems().deleteItem(removeItem1, removeItem2);
							if (name.contains("bolt")) {
								player.getItems().addItem(addItem, 10);
							} else if (name.contains("dart tip")) {
								player.getItems().addItem(addItem, 10);
							} else if (name.contains("arrow") || name.contains("nail") || (name.contains("tip") && !name.contains("dart tip"))) {
								player.getItems().addItem(addItem, 15);
							} else if (name.contains("knife")) {
								player.getItems().addItem(addItem, 5);
							} else if (name.contains("ball")) {
								player.getItems().addItem(addItem, 4);
							} else {
								player.getItems().addItem(addItem, 1);
							}
							
							if(player.tutorialProgress == 20) {
								player.getDH().sendDialogues(3066, -1);
							}
							player.getPA().addSkillXP(XP, player.playerSmithing);
							player.doAmount--;
						}
					}

					@Override
					public void stop() {
						player.isSmithing = false;
					}
				}, addItem == 2 ? 10 : 3);
			}
		} else {
			player.sendMessage("You don't have enough bars to make this item!");
			player.isSmithing = false;
		}
	}
    public void handleAnvilClick(Player c) {

	    // Quick check: any bar at all?
	    if (!SmithBar.hasAnyBar(c)) {
	        c.sendMessage("You need metal bars to smith here.");
	        return;
	    }

	    SmithBar chosen = SmithBarSelector.choose(c);

	    if (chosen == null) {
	        // Has bars, but somehow not enough level (possible if using custom bars only above their level)
	        c.sendMessage("You don't have the level to smith any of the bars you carry.");
	        return;
	    }

	    // Open interface with chosen bar id (ensure showSmithInterface expects bar item id)
	    c.getSmithingInt().showSmithInterface(chosen.itemId);
	}
    public enum SmithBar {
        RUNE(2363, 85),
        ADAMANT(2361, 70),
        MITHRIL(2359, 50),
        STEEL(2353, 30),
        IRON(2351, 15),
        BRONZE(2349, 1);

        public final int itemId;
        public final int level;

        SmithBar(int itemId, int level) {
            this.itemId = itemId;
            this.level = level;
        }

        /**
         * Returns the highest bar the player can smith (has level + at least 1 bar).
         * Order is defined by enum declaration (highest first).
         */
        public static SmithBar bestAvailable(Player c) {
            int smithLevel = c.getSkills().getLevel(Skill.SMITHING); // adapt if you store differently
            for (SmithBar b : values()) {
                if (smithLevel >= b.level && c.getItems().playerHasItem(b.itemId)) {
                    return b;
                }
            }
            return null;
        }

        public static boolean hasAnyBar(Player c) {
            for (SmithBar b : values()) {
                if (c.getItems().playerHasItem(b.itemId)) return true;
            }
            return false;
        }

        public static SmithBar byItemId(int id) {
            for (SmithBar b : values()) if (b.itemId == id) return b;
            return null;
        }
    }
    private static final int CHATBOX_IFACE_ID = 44720;
    private static final int FIRST_ITEM_CHILD = 44734; // 44734..44741 (8 slots)
    private static final int SLOTS = 8;

    // Use 65535 to CLEAR a model on the client (matches your client 246 opcode handler)
    private static final int CLEAR_ITEM_ID = 65535;

    // Clickable button ID ranges
    private static final int BTN_DEFAULT_1 = 44721;
    private static final int BTN_DEFAULT_5 = 44722;
    private static final int BTN_DEFAULT_10 = 44723;

    private static final int BTN_MAKE1_BASE = 44724; // + i (0..7)
    private static final int BTN_MAKE5_BASE = 44744; // + i (0..7)
    private static final int BTN_MAKE10_BASE = 44754; // + i (0..7)
    private static final int[] BAR_IDS = {
            2349,2351, 2353, 2355, 2357, 2359, 2361, 2363
        };
    private static final int[] ORE_IDS = {
    		436, 440, 442, 444, 447, 449, 451
        };
    private static final List<BarData> BAR_DATA_LIST = new ArrayList<>();

    static {
        // Register all bars
        BAR_DATA_LIST.add(new BarData("Bronze", 436, 438, 1, 1, 2349, 1));
        BAR_DATA_LIST.add(new BarData("Iron", 440, -1, -1, 15, 2351, 15));
        BAR_DATA_LIST.add(new BarData("Steel", 440, 453, 2, 30, 2353, 1));
        BAR_DATA_LIST.add(new BarData("Silver", 442, -1, -1, 20, 2355, 1));
        BAR_DATA_LIST.add(new BarData("Gold", 444, -1, -1, 40, 2357, 1));
        BAR_DATA_LIST.add(new BarData("Mithril", 447, 453, 4, 55, 2359, 1));
        BAR_DATA_LIST.add(new BarData("Adamant", 449, 453, 6, 70, 2361, 1));
        BAR_DATA_LIST.add(new BarData("Runite", 451, 453, 8, 85, 2363, 1));
    }
    public void sendSmelting(Player c, boolean button) {
        if (button) {
            for (int i = 0; i < SLOTS; i++) {
                c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 350, CLEAR_ITEM_ID);
                c.barDisplayed[i] = 0;
            }
            c.barCount = 0;

            int filled = 0;

            // Loop through all BarData entries, treat each as unique
            for (BarData data : BAR_DATA_LIST) {
                if (filled >= SLOTS) break;
                // Must have at least one of this ore
                if (!c.getItems().playerHasItem(data.ore, 1))
                    continue;
                // Only check secondary if needed
                if (data.secondary != -1 && !c.getItems().playerHasItem(data.secondary, data.secondaryAmt))
                    continue;
                // Skill check
                if (c.getSkills().getLevel(Skill.SMITHING) < data.level)
                    continue;
                // Show this option
                c.getPA().sendFrame246(FIRST_ITEM_CHILD + filled, 150, data.bar);
                c.barDisplayed[filled] = data.bar; // Use unique bar ID, not ore ID
                filled++;
            }

            c.barCount = filled;

            if (filled == 0) {
                c.sendMessage("You don't have any ores to smelt.");
                return;
            }
        }
        c.getPA().sendFrame126("How many bars would you like to smelt?", 44732);
        c.activeAction = Player.ChatboxAction.SMITHING;
        c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
    }
    public boolean handleButton(Player c, int buttonId) {
        // Default quantity toggles
        if (buttonId == BTN_DEFAULT_1 || buttonId == BTN_DEFAULT_5 || buttonId == BTN_DEFAULT_10) {
            int sets = (buttonId == BTN_DEFAULT_1) ? 1 : (buttonId == BTN_DEFAULT_5 ? 5 : 10);
            c.amtToMake = sets;
            c.sendMessage("Default set amount: " + sets + (sets == 1 ? " bar" : " bars"));
            return true;
        }

        // Per-slot triplets
        final int setsExplicit;
        final int slotIndex;

        if (buttonId >= BTN_MAKE1_BASE && buttonId < BTN_MAKE1_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE1_BASE;
            setsExplicit = -1; // use default
        } else if (buttonId >= BTN_MAKE5_BASE && buttonId < BTN_MAKE5_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE5_BASE;
            setsExplicit = 5;
        } else if (buttonId >= BTN_MAKE10_BASE && buttonId < BTN_MAKE10_BASE + SLOTS) {
            slotIndex = buttonId - BTN_MAKE10_BASE;
            setsExplicit = 10;
        } else {
            return false; // not ours
        }

        if (slotIndex < 0 || slotIndex >= c.barCount) {
            c.sendMessage("That option isn't available right now.");
            return true;
        }

        int selectedBarId = c.barDisplayed[slotIndex];
        if (selectedBarId <= 0) {
            c.sendMessage("That option isn't available right now.");
            return true;
        }

        // Look up BarData by barId (not by ore!)
        BarData data = null;
        for (BarData bd : BAR_DATA_LIST) {
            if (bd.bar == selectedBarId) {
                data = bd;
                break;
            }
        }
        if (data == null) {
            c.sendMessage("That option isn't available right now.");
            return true;
        }

        int setsToMake = (setsExplicit > 0) ? setsExplicit : Math.max(1, Math.min(10, c.amtToMake)); // clamp 1..10

        startSmelt(c, data, setsToMake); // Pass BarData or pass barId as needed by your new startSmelt signature
        return true;
    }
    public static void startSmelt(final Player c, final BarData data, final int requestedSets) {
        if (data == null) return;
        c.getPA().removeAllWindows();

        if (c.getSkills().getLevel(Skill.SMITHING) < data.level) {
            c.sendMessage("You need a Smithing level of " + data.level + " to smelt " + data.name + " bars.");
            return;
        }

        int maxByOre = c.getItems().getItemCount(data.ore, false);
        int maxBySecondary = Integer.MAX_VALUE;
        if (data.secondary != -1) {
            maxBySecondary = c.getItems().getItemCount(data.secondary, false) / data.secondaryAmt;
        }
        int possibleSets = Math.min(maxByOre, maxBySecondary);
        possibleSets = Math.min(possibleSets, requestedSets);

        if (possibleSets <= 0) {
            c.sendMessage("You don't have enough materials to smelt any bars.");
            return;
        }
        // Smelting message (could move this into BarData too for simplicity)
        switch (data.name.toLowerCase()) {
            case "bronze":
                c.sendMessage("You smelt the copper and tin together in the furnace.");
                break;
            case "iron":
                c.sendMessage("You smelt the iron in the furnace.");
                break;
            case "steel":
                c.sendMessage("You place the iron and 2 heaps of coal in the furnace.");
                break;
            case "mithril":
                c.sendMessage("You place the Mithril ore and 4 heaps of coal in the furnace.");
                break;
            case "adamant":
                c.sendMessage("You place the Adamant ore and 6 heaps of coal in the furnace.");
                break;
            case "runite":
                c.sendMessage("You place the Rune ore and 8 heaps of coal in the furnace.");
                break;
            case "gold":
                c.sendMessage("You place a lump of " + c.getItems().getItemName(data.ore) + " in the furnace.");
                break;
            default:
                c.sendMessage("You place a lump of " + c.getItems().getItemName(data.ore) + " in the furnace.");
                break;
        }
        final int sets = possibleSets;
        c.turnPlayerTo(c.objectX, c.objectY);

        c.startAnimation(899); // Use the animation from BarData
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            int doneSets = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (doneSets >= sets) {
                    container.stop();
                    return;
                }
                if (!c.getItems().playerHasItem(data.ore, 1)) {
                    container.stop();
                    return;
                }
                if (data.secondary != -1 && !c.getItems().playerHasItem(data.secondary, data.secondaryAmt)) {
                    container.stop();
                    return;
                }
                if (c.disconnected || c.isDead || c.isMoving) {
                    container.stop();
                    return;
                }
                // Remove ingredients
                c.getItems().deleteItem(data.ore, 1);
                if (data.secondary != -1) {
                    c.getItems().deleteItem(data.secondary, data.secondaryAmt);
                }
                    	c.getItems().addItem(data.bar, 1);
                    	c.getSkills().addExperience(data.exp, Skill.SMITHING);
                        c.startAnimation(899); // Use the animation from BarData
                    	c.sendMessage("You retrieve a bar of " + data.name + ".");
                doneSets++;
            }
            @Override
            public void stop() {
                c.stopAnimation();
                c.getPA().removeAllWindows();
            }
        }, 5);
    }
	private static final class BarData {
        final String name;
        final int ore, secondary, bar;
        final int secondaryAmt;
        final int level;
        final int exp;

        BarData(String name, int ore, int secondary, int secondaryAmt, int level, int bar, int exp) {
            this.name = name;
            this.ore = ore;
            this.secondary = secondary;
            this.secondaryAmt = secondaryAmt;
            this.level = level;
            this.bar = bar;
            this.exp = exp;
        }

        int getBarForBase(int baseId) {
            if (baseId == ore) return bar;
            return -1;
        }
    }
}