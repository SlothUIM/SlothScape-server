package server.world;

import java.util.List;

public class Shop {
    public int shopId;
    public String name;
    public int sellModifier;
    public int buyModifier;
    public List<ShopItem> items;

    public static class ShopItem {
        public int id;
        public int amount;

        public ShopItem(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }
    }
}