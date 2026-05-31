package server.model.players.skills.crafting;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.crafting.LeatherCrafting.HideData;

public class GlassBlowing {

	private final Player c;

	public static final int PIPE = 1785;
	public static final int MOLTEN_GLASS = 1775;

	// Modern Interface Constants
	private static final int CHATBOX_IFACE_ID = 44720;
	private static final int FIRST_ITEM_CHILD = 44734;
	private static final int CLEAR_ITEM_ID = 65535;
	private static final int BTN_MAKE_BASE = 174180;

	public GlassBlowing(Player c) {
		this.c = c;
	}

	// ==============================================================
	// GLASS DATA (Exactly 8 items!)
	// ==============================================================
	public enum GlassData {
		BEER_GLASS(1919, 1, 17.5),
		CANDLE_LANTERN(4527, 4, 19.0),
		OIL_LAMP(4522, 12, 25.0),
		VIAL(229, 33, 35.0),
		FISHBOWL(6667, 42, 42.5),
		UNPOWERED_ORB(567, 46, 52.5),
		LANTERN_LENS(4542, 49, 55.0),
		LIGHT_ORB(10973, 87, 70.0);

		private final int productId, levelReq;
		private final double exp;

		GlassData(int productId, int levelReq, double exp) {
			this.productId = productId;
			this.levelReq = levelReq;
			this.exp = exp;
		}
	}

	// ==============================================================
	// CORE LOGIC
	// ==============================================================

	public boolean isBlowingGlass(int item1, int item2) {
		if ((item1 == PIPE && item2 == MOLTEN_GLASS) || (item1 == MOLTEN_GLASS && item2 == PIPE)) {
			openGlassInterface();
			return true;
		}
		return false;
	}

    private boolean isBlowingInterfaceOpen = false;
	private void openGlassInterface() {
		// Populate all 8 slots with the glass products
        this.isBlowingInterfaceOpen = true;
		GlassData[] items = GlassData.values();
		for (int i = 0; i < 8; i++) {
			if (i < items.length) {
				c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, items[i].productId);
			} else {
				c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, CLEAR_ITEM_ID);
			}
		}

		c.getPA().sendFrame126("What would you like to blow?", 44732);
		c.activeAction = Player.ChatboxAction.GLASSBLOWING; // Reuse interface state
		c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
	}


	public boolean handleButtons(int buttonId) {
        if (!isBlowingInterfaceOpen) {
            return false;
        }
		// Quantity Toggle Buttons (Global for the interface)
		if (buttonId == 44721) { 
			c.toMake = 1; 
		return true;
		} // Fixed your Sawmill 5-button bug!
		if (buttonId == 44722) { c.toMake = 10; return true; }
		if (buttonId == 44723) { c.toMake = 28; return true; }

		// Item Selection Buttons
		if (buttonId >= BTN_MAKE_BASE && buttonId < BTN_MAKE_BASE + 8) {
			int slotClicked = buttonId - BTN_MAKE_BASE;
			GlassData[] items = GlassData.values();
			
			if (slotClicked < items.length) {
				// If c.toMake is 0 (default), make 1
				int amount = (c.toMake <= 0) ? 1 : c.toMake;
				startCrafting(items[slotClicked], amount);
			}
			return true;
		} else
			System.out.println(buttonId);
		return false;
	}
	private void startCrafting(GlassData glass, int amountRequested) {
		if (c.getSkills().getLevel(Skill.CRAFTING) < glass.levelReq) {
			c.sendMessage("You need a Crafting level of " + glass.levelReq + " to make this.");
			return;
		}

		isBlowingInterfaceOpen = false; // Reset the interface lock
		c.getPA().closeAllWindows();
		//c.isCrafting = true;

		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			int amountMade = 0;

			@Override
			public void execute(CycleEventContainer container) {
				if (!c.isCrafting || amountMade >= amountRequested || !c.getItems().playerHasItem(MOLTEN_GLASS)) {
					if (!c.getItems().playerHasItem(MOLTEN_GLASS)) {
						c.sendMessage("You have run out of molten glass.");
					}
					container.stop();
					return;
				}

				c.getItems().deleteItem(MOLTEN_GLASS, 1);
				c.getItems().addItem(glass.productId, 1);
				c.getSkills().addExperience((int)(glass.exp * Config.CRAFTING_EXPERIENCE), Skill.CRAFTING);
				
				c.sendMessage("You make a " + c.getItems().getItemName(glass.productId).toLowerCase() + ".");
				c.startAnimation(884); // OSRS Glassblowing animation
				
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