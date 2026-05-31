package server.model.items.containers;

import java.util.HashMap;
import java.util.Map;

import server.model.items.containers.CostumeRoomContainer.BoxTier;
import server.model.players.Player;

public class FancyBox {

    private Player c;
    
    // Use a Map instead of a giant array to prevent login lag
    public Map<Integer, Integer> storage = new HashMap<>();

    public int dressBoxTier = 0;
    public FancyBox(Player c) {
        this.c = c;
    }

    public int getDressBoxTier() {
        return dressBoxTier; // Reference the tier from the Player object
    }

    public void addStored(int itemId, int amount) {
        if (!c.fancyBoxItems.contains(itemId)) {
            c.fancyBoxItems.add(itemId);
        }
        // Get current amount and add the new amount
        int currentAmount = getAmount(itemId);
        storage.put(itemId, currentAmount + amount);
    }

    public void removeStored(int itemId, int amount) {
        int currentAmount = getAmount(itemId);
        if (currentAmount >= amount) {
            int newAmount = currentAmount - amount;
            if (newAmount <= 0) {
                storage.remove(itemId);
                c.fancyBoxItems.remove(Integer.valueOf(itemId));
            } else {
                storage.put(itemId, newAmount);
            }
        }
    }

    public int getAmount(int itemId) {
        // Returns the value if it exists, otherwise 0
        return storage.getOrDefault(itemId, 0);
    }

    public BoxTier getTier() {
        switch (getDressBoxTier()) {
            case 1: return BoxTier.OAK;      // Stores 2 types
            case 2: return BoxTier.TEAK;     // Stores 4 types
            case 3: return BoxTier.MAHOGANY; // Stores all types
            default: return BoxTier.NONE;
        }
    }

    public void setStored(int itemId, int state) {
        if (state == 1) {
            addStored(itemId, 1);
        } else {
            removeStored(itemId, getAmount(itemId));
        }
    }
}