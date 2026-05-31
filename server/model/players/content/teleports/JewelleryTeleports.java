package server.model.players.content.teleports;

import server.model.players.Player;
import java.util.Optional;
import java.util.Arrays;

/**
 * Global data and logic for all Jewellery-related teleports (Box and Items).
 * @author Gemini
 */
public enum JewelleryTeleports {

    // Ring of Dueling
    DUEL_ARENA(243214, 3315, 3235, 0, "Duel Arena", 2552),
    CASTLE_WARS(243215, 2441, 3091, 0, "Castle Wars", 2552),
    FEROX_ENCLAVE(243216, 3134, 3628, 0, "Ferox Enclave", 2552),

    // Games Necklace
    BURTHORPE(243223, 2898, 3552, 0, "Burthorpe", 3853),
    BARBARIAN_OUTPOST(243224, 2520, 3571, 0, "Barbarian Outpost", 3853),
    CORP_BEAST(243225, 2967, 4384, 2, "Corporeal Beast", 3853),
    TEARS_GUTHIX(243226, 3244, 9500, 2, "Tears of Guthix", 3853),
    WINTERTODT(243227, 1627, 3941, 0, "Wintertodt", 3853),

    // Combat Bracelet
    WARRIORS_GUILD(243232, 2867, 3546, 0, "Warriors' Guild", 11118),
    CHAMPIONS_GUILD(243233, 3191, 3363, 0, "Champions' Guild", 11118),
    MONASTERY(243234, 3052, 3491, 0, "Monastery", 11118),
    RANGING_GUILD(243235, 2655, 3441, 0, "Ranging Guild", 11118),

    // Skills Necklace
    FISHING_GUILD(243241, 2611, 3390, 0, "Fishing Guild", 11105),
    MINING_GUILD(243242, 3016, 3339, 0, "Motherlode Mine", 11105),
    CRAFTING_GUILD(243243, 2933, 3290, 0, "Crafting Guild", 11105),
    COOKING_GUILD(243244, 3143, 3443, 0, "Cooking Guild", 11105),
    WOODCUTTING_GUILD(243245, 1662, 3505, 0, "Woodcutting Guild", 11105),
    FARMING_GUILD(243246, 1248, 3726, 0, "Farming Guild", 11105),

    // Ring of Wealth
    MISCELLANIA(243250, 2512, 3860, 0, "Miscellania", 11980),
    GRAND_EXCHANGE(243251, 3163, 3481, 0, "Grand Exchange", 11980),
    FALADOR_PARK(243252, 2995, 3375, 0, "Falador Park", 11980),
    DONDAKAN_ROCK(243253, 2828, 10166, 0, "Dondakan's Rock", 11980),

    // Amulet of Glory
    EDGEVILLE(244003, 3087, 3496, 0, "Edgeville", 11978),
    KARAMJA(244004, 2918, 3176, 0, "Karamja", 11978),
    DRAYNOR(244005, 3105, 3251, 0, "Draynor Village", 11978),
    AL_KHARID(244006, 3293, 3176, 0, "Al Kharid", 11978);

    private final int buttonId, x, y, height, itemId;
    private final String name;

    JewelleryTeleports(int buttonId, int x, int y, int height, String name, int itemId) {
        this.buttonId = buttonId;
        this.x = x;
        this.y = y;
        this.height = height;
        this.name = name;
        this.itemId = itemId;
    }

    public int getButtonId() { return buttonId; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHeight() { return height; }
    public String getName() { return name; }
    public int getItemId() { return itemId; }

    /**
     * Finds the teleport data based on the interface button clicked.
     */
    public static Optional<JewelleryTeleports> getByButton(int buttonId) {
        return Arrays.stream(values()).filter(data -> data.buttonId == buttonId).findFirst();
    }

    /**
     * Finds the teleport data by the name (useful for dialogues).
     */
    public static Optional<JewelleryTeleports> getByName(String name) {
        return Arrays.stream(values()).filter(data -> data.name.equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Executes the teleport logic.
     * @param player The player teleporting.
     * @param buttonId The button ID from ClickingButtons.
     * @return true if the button was handled by this class.
     */
    public static boolean handleButton(Player player, int buttonId) {
        Optional<JewelleryTeleports> tele = getByButton(buttonId);
        
        if (tele.isPresent()) {
            JewelleryTeleports data = tele.get();
            
            // Basic safety check
            if (player.inWild() && player.wildLevel > 20) {
                player.sendMessage("You cannot teleport above level 20 wilderness.");
                return true;
            }

            // Start the teleport
            player.getPA().startTeleport(data.x, data.y, data.height, "glory");
            player.sendMessage("You have been teleported to " + data.name + ".");
            player.getPA().removeAllWindows();
            return true;
        }
        return false;
    }
}