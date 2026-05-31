package server.model.players.content;

import server.Server;
import server.model.players.Player;
import server.world.World;
import server.world.objects.GlobalObject;

public class CropPicking {

    private static final int PICK_ANIMATION = 827;

    public enum Crop {
        // --- Single Pick Crops (Disappear to -1) ---
        FLAX(new int[]{14896}, -1, 10, 1779, "flax", 15),
        CABBAGE(new int[]{1161}, -1, 10, 1965, "cabbage", 10),
        ONION(new int[]{3366}, -1, 10, 1957, "onion", 10),
        POTATO(new int[]{312}, -1, 10, 1942, "potato", 10),
        WHEAT(new int[]{15506, 15508, 15507}, -1, 10, 1947, "wheat", 10),
        SWEETCORN(new int[]{33695}, -1, 10, 5986, "sweetcorn", 15), 
        NETTLES(new int[]{1181}, -1, 10, 4241, "nettles", 15),
        
        // --- Multi-Pick Trees ---
        BANANA(new int[]{2073}, 2074, 10, 1963, "banana", 15),
        PINEAPPLE(new int[]{1408, 4827}, 4828, 10, 2114, "pineapple", 15),

        // --- Redberry Bushes (2 Berries -> 1 Berry -> Empty) ---
        REDBERRY_2(new int[]{23628}, 23629, 10, 1951, "redberries", 200),
        REDBERRY_1(new int[]{23629}, 23630, 10, 1951, "redberries", 200),
        REDBERRY_EMPTY(new int[]{23630}, -1, 10, -1, "empty", 0), // Item ID -1 triggers the empty message

        // --- Standard Cadava Bushes (2 Berries -> 1 Berry -> Empty) ---
        CADAVA_2(new int[]{23625}, 23626, 10, 753, "cadava berries", 200),
        CADAVA_2_ALT(new int[]{23626}, 23627, 10, 753, "cadava berries", 200),
        CADAVA_EMPTY(new int[]{23627}, -1, 10, -1, "empty", 0), // Item ID -1 triggers the empty message

        // --- Special Varp Cadava Bush (Romeo & Juliet) ---
        CADAVA_VARP(new int[]{33183}, -2, 10, 753, "cadava berries", 0);

        private final int[] objectId;
        private final int replacementId; 
        private final int itemId;
        private final int type;
        private final String name;
        private final int respawnTicks;

        Crop(int[] objectId, int replacementId, int type, int itemId, String name, int respawnTicks) {
            this.objectId = objectId;
            this.replacementId = replacementId;
            this.itemId = itemId;
            this.name = name;
            this.respawnTicks = respawnTicks;
            this.type = type;
        }

        public int[] getObjectId() { return objectId; }
        public int getReplacementId() { return replacementId; }
        public int getItemId() { return itemId; }
        public String getName() { return name; }
        public int getRespawnTicks() { return respawnTicks; }

        public static Crop forObjectId(int id) {
            for (Crop crop : values()) {
                for (int objId : crop.getObjectId()) {
                    if (objId == id) return crop;
                }
            }
            return null;
        }

		public int getType() {
			// TODO Auto-generated method stub
			return type;
		}
    }

    public static boolean pickCrop(Player c, int objectId, int obX, int obY, int type2, int face) {
    	Crop crop = Crop.forObjectId(objectId);
        
        if (crop == null) {
            return false; 
        }

        // --- Handle Empty Bushes ---
        if (crop.getItemId() == -1) {
            c.sendMessage("There are no berries on this bush at the moment.");
            return true;
        }

        if (System.currentTimeMillis() - c.lastAction < 1200) {
            return true;
        }

        if (c.getItems().freeSlots() == 0) {
            c.sendMessage("You don't have enough inventory space to pick this.");
            return true;
        }
        
        // --- Nettle Gloves Check ---
        if (crop == Crop.NETTLES) {
            int gloves = c.playerEquipment[c.playerHands];
            if (gloves != 10075 && gloves != 1059 && gloves != 24376) { 
                c.sendMessage("You have been stung by the nettles!");
                c.appendDamage(2, server.model.players.combat.Hitmark.HIT); 
                return true; 
            }
        }

        c.lastAction = System.currentTimeMillis();
        
        c.turnPlayerTo(obX, obY);
        c.startAnimation(PICK_ANIMATION);
        
        c.getItems().addItem(crop.getItemId(), 1);
        c.sendMessage("You pick some " + crop.getName() + ".");

        // --- Handle Object Spawning / Degrading ---
        if (crop.getReplacementId() == -2) {
            c.cadavaVarpStage++; 
            if (c.cadavaVarpStage > 3) {
                c.cadavaVarpStage = 3; 
            }
            c.getPA().sendConfig(1785, c.cadavaVarpStage);
        } else {
            // STANDARD CASE: Spawn the replacement object using the EXACT type and face!
            // Notice we swapped out the hardcoded '0' and '10' for 'face' and 'type'
            GlobalObject closed = new GlobalObject(crop.getReplacementId(), obX, obY, c.getHeight(), face, crop.getType(), crop.getRespawnTicks(), objectId);
            World.getWorld().getGlobalObjects().add(closed);
        }
        if(crop.getReplacementId() == -1)
        	c.getMovementQueue().addStep(obX, obY, 0);

        return true;
    }
}