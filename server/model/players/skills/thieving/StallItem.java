package server.model.players.skills.thieving;

import server.model.items.Item;

public class StallItem {
    private final Item item;
    private final double chanceWeight; // Higher means more common

    public StallItem(Item item, double chanceWeight) {
        this.item = item;
        this.chanceWeight = chanceWeight;
    }

    public Item getItem() {
        return item;
    }

    public double getChanceWeight() {
        return chanceWeight;
    }
}
