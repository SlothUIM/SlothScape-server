package server.model.players.skills.crafting;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;

public class LeatherCrafting {

	private final Player c;

	public static final int NEEDLE = 1733;
	public static final int THREAD = 1734;

	// Modern Interface Constants
	private static final int CHATBOX_IFACE_ID = 44720;
	private static final int FIRST_ITEM_CHILD = 44734;
	private static final int CLEAR_ITEM_ID = 65535;
	private static final int BTN_MAKE_BASE = 44724; // Buttons 44724 through 44731 (Slots 0-7)

	public LeatherCrafting(Player c) {
		this.c = c;
	}

	// ==============================================================
	// HIDE DATA (Supports up to 8 products per hide!)
	// ==============================================================
	public enum HideData {
		// Normal Leather (7 items) - Uses 1 hide for everything
		NORMAL(1741, 
			new int[]{1059, 1061, 1167, 1063, 1129, 1095, 1169}, // Gloves, Boots, Cowl, Vambs, Body, Chaps, Coif
			new int[]{1, 1, 1, 1, 1, 1, 1}, // Amount of hides needed
			new int[]{1, 7, 9, 11, 14, 18, 38}, // Levels
			new double[]{13.8, 16.3, 18.5, 22.0, 25.0, 27.0, 37.0}), // Experience

		// Dragonhides (3 items each)
		GREEN(1745, new int[]{1065, 1099, 1135}, new int[]{1, 2, 3}, new int[]{57, 60, 63}, new double[]{62, 124, 186}),
		BLUE(2505, new int[]{2487, 2493, 2499}, new int[]{1, 2, 3}, new int[]{66, 68, 71}, new double[]{70, 140, 210}),
		RED(2507, new int[]{2489, 2495, 2501}, new int[]{1, 2, 3}, new int[]{73, 75, 77}, new double[]{78, 156, 234}),
		BLACK(2509, new int[]{2491, 2497, 2503}, new int[]{1, 2, 3}, new int[]{79, 82, 84}, new double[]{86, 172, 258});

		private final int hideId;
		private final int[] products, amounts, levels;
		private final double[] exp;

		HideData(int hideId, int[] products, int[] amounts, int[] levels, double[] exp) {
			this.hideId = hideId;
			this.products = products;
			this.amounts = amounts; // How many hides this specific product costs
			this.levels = levels;
			this.exp = exp;
		}

		public static HideData forId(int id) {
			for (HideData h : values()) {
				if (h.hideId == id) return h;
			}
			return null;
		}
	}

	// ==============================================================
	// CORE LOGIC
	// ==============================================================

	public boolean isMakingLeather(int item1, int item2) {
		if (item1 == NEEDLE || item2 == NEEDLE) {
			int hideId = (item1 == NEEDLE) ? item2 : item1;
			HideData hide = HideData.forId(hideId);
			
			if (hide != null) {
				openLeatherInterface(hide);
				return true;
			}
		}
		return false;
	}

	private void openLeatherInterface(HideData hide) {
		c.craftingHideId = hide.hideId; 

		// Clear all 8 slots first
		for (int i = 0; i < 8; i++) {
			c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, CLEAR_ITEM_ID);
		}

		// Draw the products the hide can make
		for (int i = 0; i < hide.products.length; i++) {
			c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 200, hide.products[i]);
		}

		c.getPA().sendFrame126("What would you like to make?", 44732);
		c.activeAction = Player.ChatboxAction.LEATHERCRAFTING; // Reuse your state so the interface doesn't close
		c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
	}

	public boolean handleButtons(int buttonId) {
		// Quantity Toggle Buttons (Global for the interface)
		if (buttonId == 44721) { c.toMake = 1; return true; } // Fixed your Sawmill 5-button bug!
		if (buttonId == 44722) { c.toMake = 10; return true; }
		if (buttonId == 44723) { c.toMake = 28; return true; }

		// Item Selection Buttons
		if (buttonId >= BTN_MAKE_BASE && buttonId < BTN_MAKE_BASE + 8) {
			int slotClicked = buttonId - BTN_MAKE_BASE;
			HideData hide = HideData.forId(c.craftingHideId);
			
			if (hide != null && slotClicked < hide.products.length) {
				// If c.toMake is 0 (default), make 1
				int amount = (c.toMake <= 0) ? 1 : c.toMake;
				startCrafting(hide, slotClicked, amount);
			}
			return true;
		}
		return false;
	}

	private void startCrafting(HideData hide, int slotIndex, int amountRequested) {
		int reqLevel = hide.levels[slotIndex];
		double expAmount = hide.exp[slotIndex];
		int productId = hide.products[slotIndex];
		int hidesNeeded = hide.amounts[slotIndex];

		if (c.getSkills().getLevel(Skill.CRAFTING) < reqLevel) {
			c.sendMessage("You need a Crafting level of " + reqLevel + " to craft this.");
			return;
		}
		if (!c.getItems().playerHasItem(THREAD)) {
			c.sendMessage("You need some thread to sew this hide.");
			return;
		}

		c.getPA().closeAllWindows();
		c.isCrafting = true;

		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			int amountMade = 0;

			@Override
			public void execute(CycleEventContainer container) {
				if (!c.isCrafting || amountMade >= amountRequested 
					|| !c.getItems().playerHasItem(hide.hideId, hidesNeeded) 
					|| !c.getItems().playerHasItem(THREAD)) {
					
					if (!c.getItems().playerHasItem(THREAD) && c.getItems().playerHasItem(hide.hideId, hidesNeeded)) {
						c.sendMessage("You have run out of thread.");
					} else if (!c.getItems().playerHasItem(hide.hideId, hidesNeeded)) {
						c.sendMessage("You have run out of hides.");
					}
					container.stop();
					return;
				}

				// Consume materials
				c.getItems().deleteItem(hide.hideId, hidesNeeded);
				c.getItems().deleteItem(THREAD, 1); 
				
				// Give product and experience
				c.getItems().addItem(productId, 1);
				c.getSkills().addExperience((int)(expAmount * Config.CRAFTING_EXPERIENCE), Skill.CRAFTING);
				
				c.sendMessage("You make a " + c.getItems().getItemName(productId).toLowerCase() + ".");
				c.startAnimation(1249); 
				
				amountMade++;
			}

			@Override
			public void stop() {
				c.isCrafting = false;
				c.startAnimation(65535); 
			}
		}, 3); 
	}
}