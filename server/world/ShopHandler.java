package server.world;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.world.Shop; // Import the model we just made

import server.Config;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * Shops
 **/

public class ShopHandler {

	public static int MaxShops = 200;
	public static int MaxShopItems = 200;
	public static int MaxInShopItems = 40;
	public static int MaxShowDelay = 10;
	public static int MaxSpecShowDelay = 60;
	public static int TotalShops = 0;
	public static int[][] ShopItems = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsN = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsDelay = new int[MaxShops][MaxShopItems];
	public static int[][] ShopItemsSN = new int[MaxShops][MaxShopItems];
	public static int[] ShopItemsStandard = new int[MaxShops];
	public static String[] ShopName = new String[MaxShops];
	public static int[] ShopSModifier = new int[MaxShops];
	public static int[] ShopBModifier = new int[MaxShops];

	public ShopHandler() {
		for (int i = 0; i < MaxShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				ResetItem(i, j);
				ShopItemsSN[i][j] = 0;
			}
			ShopItemsStandard[i] = 0;
			ShopSModifier[i] = 0;
			ShopBModifier[i] = 0;
			ShopName[i] = "";
		}
		TotalShops = 0;
		loadShops("shops.json");
	}

	public static void shophandler() {
		Misc.println("Shop Handler class successfully loaded");
	}

	public void process() {
		boolean DidUpdate = false;
		for (int i = 1; i <= TotalShops; i++) {
			for (int j = 0; j < MaxShopItems; j++) {
				if (ShopItems[i][j] > 0) {
					if (ShopItemsDelay[i][j] >= MaxShowDelay) {
						if (j <= ShopItemsStandard[i]
								&& ShopItemsN[i][j] <= ShopItemsSN[i][j]) {
							if (ShopItemsN[i][j] < ShopItemsSN[i][j]) {
								ShopItemsN[i][j] += 1;
								DidUpdate = true;
								ShopItemsDelay[i][j] = 1;
								ShopItemsDelay[i][j] = 0;
								DidUpdate = true;
							}
						} else if (ShopItemsDelay[i][j] >= MaxSpecShowDelay) {
							DiscountItem(i, j);
							ShopItemsDelay[i][j] = 0;
							DidUpdate = true;
						}
					}
					ShopItemsDelay[i][j]++;
				}
			}
			if (DidUpdate == true) {
				for (int k = 1; k < Config.MAX_PLAYERS; k++) {
					if (PlayerHandler.players[k] != null) {
						if (PlayerHandler.players[k].isShopping == true
								&& PlayerHandler.players[k].myShopId == i) {
							PlayerHandler.players[k].updateShop = true;
							DidUpdate = false;
							PlayerHandler.players[k].updateshop(i);
						}
					}
				}
				DidUpdate = false;
			}
		}
	}

	public void DiscountItem(int ShopID, int ArrayID) {
		ShopItemsN[ShopID][ArrayID] -= 1;
		if (ShopItemsN[ShopID][ArrayID] <= 0) {
			ShopItemsN[ShopID][ArrayID] = 0;
			ResetItem(ShopID, ArrayID);
		}
	}

	public void ResetItem(int ShopID, int ArrayID) {
		ShopItems[ShopID][ArrayID] = 0;
		ShopItemsN[ShopID][ArrayID] = 0;
		ShopItemsDelay[ShopID][ArrayID] = 0;
	}

	public boolean loadShops(String fileName) {
	    try (FileReader reader = new FileReader("./Data/JSON/" + fileName)) {
	        Gson gson = new Gson();
	        List<Shop> loadedShops = gson.fromJson(reader, new TypeToken<List<Shop>>(){}.getType());

	        for (Shop s : loadedShops) {
	            int id = s.shopId;
	            ShopName[id] = s.name;
	            ShopSModifier[id] = s.sellModifier;
	            ShopBModifier[id] = s.buyModifier;
	            
	            for (int i = 0; i < s.items.size(); i++) {
	                Shop.ShopItem item = s.items.get(i);
	                // +1 is kept to match your existing logic (0 = empty)
	                ShopItems[id][i] = item.id + 1;
	                ShopItemsN[id][i] = item.amount;
	                ShopItemsSN[id][i] = item.amount;
	                ShopItemsStandard[id]++;
	            }
	            TotalShops++;
	        }
	        Misc.println("Loaded " + TotalShops + " shops from JSON.");
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}
