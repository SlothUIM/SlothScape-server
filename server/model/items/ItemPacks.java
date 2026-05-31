package server.model.items;

import server.model.players.Player;

public class ItemPacks {
	
	public enum ItemPack {
	    SOFT_CLAY_1(24851, 1762, 100, "Soft clay"),
	    SOFT_CLAY_2(12009, 1762, 100, "Soft clay"),
	    EMPTY_BUCKET(22660, 1926, 100, "Empty bucket"),
	    EMPTY_JUG(20742, 1936, 100, "Empty jug"),
	    COMPOST(19704, 6033, 100, "Compost"),
	    BONE_BOLT(13193, 8882, 100, "Bone bolt"),
	    BIRD_SNARE(12740, 10007, 100, "Bird snare"),
	    BOX_TRAP(12742, 10009, 100, "Box trap"),
	    MAGIC_IMP_BOX(12744, 10026, 100, "Magic imp box"),
	    OLIVE_OIL(12857, 3425, 100, "Olive oil"),
	    EYE_OF_NEWT(12859, 222, 100, "Eye of newt"),
	    PLANT_POT(13250, 5357, 100, "Plant pot"),
	    PRODUCE_SACK(13252, 5419, 100, "Produce sack"),
	    PRODUCE_BASKET(13254, 5377, 100, "Produce basket"),
	    EMPTY_VIAL(11877, 230, 100, "Empty vial"),
	    WATER_FILLED_VIAL(11879, 228, 100, "Water filled vial"),
	    FEATHER(11881, 314, 100, "Feather"),
	    BAIT(11883, 313, 100, "Bait"),
	    BROAD_ARROWHEAD(11885, 11874, 100, "Broad arrowhead"),
	    UNFINISHED_BROAD_BOLT(11887, 11876, 100, "Unfinished broad bolt"),
	    RUNE_ARROW(20607, 892, 100, "Rune arrow");

	    private final int packItemId;
	    private final int givenItemId;
	    private final int amount;
	    private final String name;

	    ItemPack(int packItemId, int givenItemId, int amount, String name) {
	        this.packItemId = packItemId;
	        this.givenItemId = givenItemId;
	        this.amount = amount;
	        this.name = name;
	    }

	    public int getPackItemId() {
	        return packItemId;
	    }

	    public int getGivenItemId() {
	        return givenItemId;
	    }

	    public int getAmount() {
	        return amount;
	    }

	    public String getName() {
	        return name;
	    }

	    public static ItemPack getByPackItemId(int packItemId) {
	        for (ItemPack pack : values()) {
	            if (pack.packItemId == packItemId) {
	                return pack;
	            }
	        }
	        return null;
	    }
	}
	/**
     * Checks if the given item is a pack and opens it for the player.
     * @param c The player.
     * @param packItemId The ID of the pack clicked.
     * @return true if it was a valid pack and was opened, false otherwise.
     */
    public static boolean openPack(Player c, int packItemId) {
        ItemPack pack = ItemPack.getByPackItemId(packItemId);
        if (pack == null) {
            return false; // Not a valid pack
        }

        // Remove the pack from inventory
        if (!c.getItems().playerHasItem(packItemId)) {
            return false; // Player doesn't actually have it
        }
        c.getItems().deleteItem(packItemId, 1);

        // Add the items inside the pack
        c.getItems().addItem(pack.getGivenItemId(), pack.getAmount());

        // Optionally send a message
        c.sendMessage("You open the pack of " + formatName(pack.getName()) + "s and receive " 
            + pack.getAmount() + " " + formatName(c.getItems().getItemName(pack.getGivenItemId())) + "s.");

        return true;
    }

    private static String formatName(String enumName) {
        return enumName.toLowerCase().replace("_", " ");
    }
}
