package server.model.players.skills.crafting;

import server.Config;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;

public class Spinning {

    private final Player c;

    // Modern Interface Constants (Hijacked from Leather Crafting)
    private static final int CHATBOX_IFACE_ID = 44720;
    private static final int FIRST_ITEM_CHILD = 44734;
    private static final int CLEAR_ITEM_ID = 65535;
    private static final int BTN_MAKE_BASE = 44724; 

    private boolean isSpinningInterfaceOpen = false;

    public Spinning(Player c) {
        this.c = c;
    }

    public enum Spinnable {
        WOOL(1737, 1759, 2.5, 1),
        FLAX(1779, 1777, 15.0, 10),
        SINEW(9436, 9438, 15.0, 10),
        MAGIC_ROOTS(6051, 6038, 30.0, 19),
        YAK_HAIR(10814, 954, 25.0, 43);

        private final int rawItem, spunItem, levelReq;
        private final double xp;

        Spinnable(int rawItem, int spunItem, double xp, int levelReq) {
            this.rawItem = rawItem;
            this.spunItem = spunItem;
            this.xp = xp;
            this.levelReq = levelReq;
        }
        	
    }

    public void openSpinningInterface() {
        c.craftingHideId = -1; // Prevents LeatherCrafting from stealing the button clicks!
        
        // 1. Check if the player has AT LEAST ONE spinnable item in their inventory
        boolean hasAnyItem = false;
        for (Spinnable s : Spinnable.values()) {
            if (c.getItems().playerHasItem(s.rawItem)) {
                hasAnyItem = true;
                break; // We found at least one, stop checking!
            }
        }
        
        // If they have absolutely nothing, block the interface
        if (!hasAnyItem) {
            c.sendMessage("You do not have anything to spin.");
            return;
        }

        // 2. Clear all 8 slots first to remove any leftover leather models
        for (int i = 0; i < 8; i++) {
            c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, CLEAR_ITEM_ID);
        }

        // 3. Draw all the Spinnable products on the interface
        Spinnable[] options = Spinnable.values();
        for (int i = 0; i < options.length; i++) {
            c.getPA().sendFrame246(FIRST_ITEM_CHILD + i, 150, options[i].spunItem);
        }

        this.isSpinningInterfaceOpen = true;
        c.turnPlayerTo(c.objectX, c.objectY);
        c.getPA().sendFrame126("What would you like to spin?", 44732);
        c.activeAction = Player.ChatboxAction.SPINNING; 
        c.getPA().showChatboxInterface(CHATBOX_IFACE_ID);
    }

    public boolean handleButtons(int buttonId) {
        if (!isSpinningInterfaceOpen) {
            return false;
        }

        // Handle the item selection slots (0 through 4)
        if (buttonId >= BTN_MAKE_BASE && buttonId < BTN_MAKE_BASE + Spinnable.values().length) {
            int slotClicked = buttonId - BTN_MAKE_BASE;
            int amount = (c.toMake <= 0) ? 1 : c.toMake; // Uses your global quantity toggle!
            
            startSpinning(Spinnable.values()[slotClicked], amount);
            return true;
        }
        return false;
    }

    private void startSpinning(final Spinnable item, final int amountRequested) {
        isSpinningInterfaceOpen = false; // Reset the interface lock
        c.getPA().closeAllWindows();

        if (c.getSkills().getLevel(Skill.CRAFTING) < item.levelReq) {
            c.sendMessage("You need a Crafting level of " + item.levelReq + " to spin this.");
            return;
        }

        if (!c.getItems().playerHasItem(item.rawItem)) {
            c.sendMessage("You do not have any " + c.getItems().getItemName(item.rawItem).toLowerCase() + " to spin.");
            return;
        }

        c.isCrafting = true;
        c.startAnimation(896); 
        
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            int amountMade = 0;

            @Override
            public void execute(CycleEventContainer container) {
                if (!c.isCrafting || amountMade >= amountRequested || !c.getItems().playerHasItem(item.rawItem)) {
                    if (!c.getItems().playerHasItem(item.rawItem)) {
                        c.sendMessage("You have run out of " + c.getItems().getItemName(item.rawItem).toLowerCase() + ".");
                    }
                    container.stop();
                    return;
                }

                c.startAnimation(896);
                c.getItems().deleteItem(item.rawItem, 1);
                c.getItems().addItem(item.spunItem, 1);
                c.getSkills().addExperience((int)(item.xp * Config.CRAFTING_EXPERIENCE), Skill.CRAFTING);
                
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