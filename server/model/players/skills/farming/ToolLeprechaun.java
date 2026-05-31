/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server.model.players.skills.farming;

/**
 *
 * @author ArrowzFtw
 */

import java.util.HashMap;
import java.util.Map;

import server.model.items.Item;
import server.model.items.ItemData;
import server.model.players.Client;
import server.model.players.Player;
import server.Server;
import server.Config;

/**
 * Created by IntelliJ IDEA. User: vayken Date: 23/02/12 Time: 12:12 To change
 * this template use File | Settings | File Templates.
 */

public class ToolLeprechaun {

	private Player player;

	public ToolLeprechaun(Player player2) {
		this.player = player2;
	}

	public int[] tools = new int[18]; // Spawns an empty array of 0s!

	/* setting up the store item array and the player item array */

	public ItemData[] storeItems = { new ItemData(5341), new ItemData(5343), new ItemData(952),
			new ItemData(5329), new ItemData(5331), new ItemData(5325) };
	public ItemData[] storeItems2 = { new ItemData(1925), new ItemData(6032),
			new ItemData(6034) };
	public ItemData[] storeItemsClient = { new ItemData(5341), new ItemData(5343),
			new ItemData(952), new ItemData(5329), new ItemData(5331), new ItemData(5325) };
	public ItemData[] storeItems2Client = { new ItemData(1925), new ItemData(6032),
			new ItemData(6034) };

	/* setting up the main constant field */

	public static final int[] NOTABLE_ITEMS = { 225, 239, 247, 249, 251, 253,
			255, 257, 259, 261, 263, 265, 267, 269, 3000, 2481, 592, 1965,
			1967, 6004, 5980, 5976, 1955, 1963, 2108, 5972, 2114, 754, 2126,
			248, 1951, 240, 2367, 1942, 1957, 1965, 1982, 5986, 5504, 5982,
			6006, 5994, 5996, 5931, 5998, 6000, 6002, 6016, 6055 };

	public static final int LEPRECHAUN_INTERFACE = 15614;
	public static final int LEPRECHAUN_INTERFACE_CONTAINER = 15682;//
	public static final int LEPRECHAUN_INTERFACE_CONTAINER2 = 15683;//
	public static final int PLAYER_INTERFACE = 15593;
	public static final int PLAYER_INTERFACE_CONTAINER = 15594;//
	public static final int PLAYER_INTERFACE_CONTAINER2 = 15595;//
	public static final int TOOL_CONFIGS = 615;

	/* this enum store every tool data for the interfaces loading */

	public enum ToolStoreData {

		RAKE(0, 5341, 1, 1, 15596, 15597, "Rake"), SEED_DIBBER(1, 5343, 2, 1,
				15598, 15599, "Dibber"), SPADE(2, 952, 4, 1, 15600, 15601,
				"Spade"), SECATEURS(3, 5329, 8, 1, 15602, 15603, "Secateurs"), MAGIC_SECATEURS(
				4, 7409, 8, 1, 15602, 15603, "Secateurs"), WATERING_CAN_0(5,
				5331, 16, 1, 15604, 15605, "Watering Can"), WATERING_CAN_1(6,
				5333, 32, 1, 15604, 15605, "Watering Can"), WATERING_CAN_2(7,
				5334, 48, 1, 15604, 15605, "Watering Can"), WATERING_CAN_3(8,
				5335, 64, 1, 15604, 15605, "Watering Can"), WATERING_CAN_4(9,
				5336, 80, 1, 15604, 15605, "Watering Can"), WATERING_CAN_5(10,
				5337, 96, 1, 15604, 15605, "Watering Can"), WATERING_CAN_6(11,
				5338, 112, 1, 15604, 15605, "Watering Can"), WATERING_CAN_7(12,
				5339, 128, 1, 15604, 15605, "Watering Can"), WATERING_CAN_8(13,
				5340, 144, 1, 15604, 15605, "Watering Can"), GARDENING_TROWEL(
				14, 5325, 256, 1, 15606, 15607, "Trowel"), EMPTY_BUCKETS(15,
				1925, 512, 31, 15608, 15609, "Buckets"), COMPOST(16, 6032,
				16384, 255, 15610, 15611, "Compost"), SUPER_COMPOST(17, 6034,
				4194304, 255, 15612, 15613, "Super Compost");

		private int toolIndex;
		private int toolId;
		private int toolConfig;
		private int toolMaxQuantity;
		private int toolFrameId;
		private int toolCountFrameId;
		private String toolName;

		private static Map<Integer, ToolStoreData> tools = new HashMap<Integer, ToolStoreData>();
		private static Map<Integer, ToolStoreData> indexes = new HashMap<Integer, ToolStoreData>();

		public static ToolStoreData forId(int toolId) {
			return tools.get(toolId);
		}

		public static ToolStoreData forIndex(int index) {
			return indexes.get(index);
		}

		static {
			for (ToolStoreData data : ToolStoreData.values()) {
				tools.put(data.toolId, data);
				indexes.put(data.toolIndex, data);
			}
		}

		ToolStoreData(int toolIndex, int toolId, int toolConfig,
				int toolMaxQuantity, int toolFrameId, int toolCountFrameId,
				String toolName) {
			this.toolIndex = toolIndex;
			this.toolId = toolId;
			this.toolConfig = toolConfig;
			this.toolMaxQuantity = toolMaxQuantity;
			this.toolFrameId = toolFrameId;
			this.toolCountFrameId = toolCountFrameId;
			this.toolName = toolName;
		}

		public int getToolIndex() {
			return toolIndex;
		}

		public int getToolId() {
			return toolId;
		}

		public int getToolConfig() {
			return toolConfig;
		}

		public int getToolMaxQuantity() {
			return toolMaxQuantity;
		}

		public int getToolFrameId() {
			return toolFrameId;
		}

		public int getToolCountFrameId() {
			return toolCountFrameId;
		}

		public String getToolName() {
			return toolName;
		}
	}

	/* loading the interfaces */
	public void refreshContainers() {
		// --- 1. Dynamic Item Calculation ---
		// Left Side (Leprechaun)
		int lepSecateursId = tools[4] > 0 ? 7409 : 5329;
		int lepSecateursAmt = tools[4] > 0 ? tools[4] : tools[3];
		int lepWateringCanId = 5331;
		int lepWateringCanAmt = 0;
		for (int i = 13; i >= 5; i--) {
			if (tools[i] > 0) { lepWateringCanId = ToolStoreData.forIndex(i).getToolId(); break; }
		}
		for (int i = 5; i <= 13; i++) lepWateringCanAmt += tools[i];

		// Right Side (Player)
		int playerSecateursId = player.getItems().playerHasItem(7409) ? 7409 : 5329;
		int playerSecateursAmt = player.getItems().getItemAmount(playerSecateursId);
		int playerWateringCanId = 5331;
		int playerCanAmt = 0;
		for (int i = 5340; i >= 5331; i--) {
			if (player.getItems().playerHasItem(i)) {
				if (playerWateringCanId == 5331) playerWateringCanId = i;
			}
			playerCanAmt += player.getItems().getItemAmount(i);
		}

		// --- 2. Populate the Leprechaun's Storage (Left Side) ---
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 0, 5341, tools[0]); // Rake
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 1, 5343, tools[1]); // Seed Dibber
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 2, 952, tools[2]); // Spade
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 3, lepSecateursId, lepSecateursAmt); // Secateurs
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 4, lepWateringCanId, lepWateringCanAmt); // Watering Can
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER, 5, 5325, tools[14]); // Trowel

		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER2, 0, 1925, tools[15]); // Buckets
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER2, 1, 6032, tools[16]); // Compost
		player.getPA().itemOnInterface(player, LEPRECHAUN_INTERFACE_CONTAINER2, 2, 6034, tools[17]); // Super Compost

		// --- 3. Populate the Player's Inventory (Right Side) ---
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 0, 5341, player.getItems().getItemAmount(5341));
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 1, 5343, player.getItems().getItemAmount(5343));
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 2, 952, player.getItems().getItemAmount(952));
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 3, playerSecateursId, playerSecateursAmt);
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 4, playerWateringCanId, playerCanAmt);
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER, 5, 5325, player.getItems().getItemAmount(5325));

		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER2, 0, 1925, player.getItems().getItemAmount(1925));
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER2, 1, 6032, player.getItems().getItemAmount(6032));
		player.getPA().itemOnInterface(player, PLAYER_INTERFACE_CONTAINER2, 2, 6034, player.getItems().getItemAmount(6034));
	}
	public void loadInterfaces() {
		if (player.getOutStream() != null) {
			player.getOutStream().createFrame(248);
			player.getOutStream().writeWordA(LEPRECHAUN_INTERFACE); // 15614
			player.getOutStream().writeWord(15593);                 // 15593
			player.flushOutStream();
		}

		// This will natively call refreshContainers()!
		updateStore();
	}

	/* handling watering can things */

	public void handleAdditionalTools() {
		int item;
		int i = 5340;
		while (!player.getItems().playerHasItem(i)
				&& i >= 5330) {
			i--;
		}
		item = i;
		if (item == 5330) {
			return;
		}
		storeItemsClient[4] = new ItemData(item);

		if (player.getItems().playerHasItem(7409)) {
			storeItemsClient[3] = new ItemData(7409);
		} else {
			storeItemsClient[3] = new ItemData(5329);
		}

	}

	public void checkWateringCanQuantity() {
		int counter = 0;
		int counter2 = 0;
		for (int i = 5; i <= 13; i++) {
			ToolStoreData toolStoreData = ToolStoreData.forIndex(i);
			if (player.getItems().playerHasItem(toolStoreData.getToolId())) {
				counter2++;
			}
			if (tools[i] == 1) {
				counter++;
			}
		}
		if (counter == 0) {
			storeItems[4] = new ItemData(5331);
		}
		if (counter2 == 0) {
			storeItemsClient[4] = new ItemData(5331);
		}

	}

	public boolean hasWateringCanInStore() {
		int counter = 0;
		for (int i = 5; i <= 13; i++) {
			if (tools[i] == 1) {
				counter++;
			}
		}
		if (counter == 0) {
			return false;
		}
		return true;

	}

	/* updating the store state and player state */

	public void updateStore() {
		int configValue = 0;

		int playerSecateursAmt = player.getItems().getItemAmount(5329) + player.getItems().getItemAmount(7409);
		int playerCanAmt = 0;
		for (int i = 5331; i <= 5340; i++) {
			playerCanAmt += player.getItems().getItemAmount(i);
		}

		for (int i = 0; i < tools.length; i++) {
			ToolStoreData toolStoreData = ToolStoreData.forIndex(i);
			if (toolStoreData == null) {
				continue;
			}
			configValue += toolStoreData.getToolConfig() * tools[i];

			int amountToDisplay = player.getItems().getItemAmount(toolStoreData.getToolId());
			if (i == 3 || i == 4) amountToDisplay = playerSecateursAmt;
			if (i >= 5 && i <= 13) amountToDisplay = playerCanAmt;

			updateClientInterface(toolStoreData, amountToDisplay, i);
		}

		player.getPA().sendConfig(TOOL_CONFIGS, configValue);

		// --- THE FIX ---
		// Natively redraws the 0 quantity as an opaque silhouette every time you click!
		refreshContainers();
	}
	public void updateClientInterface(ToolStoreData toolStoreData, int count,
			int index) {
		player.tempBoolean = false;
		if (count > 0) {
			if (index >= 5 && index <= 13) {
				player.tempBoolean = true;
			}
			player.getPA().sendFrame126("@gre@" + toolStoreData.getToolName(),
					toolStoreData.getToolFrameId());
			player.getPA().sendFrame126("@gre@" + count,
					toolStoreData.getToolCountFrameId());
		} else {
			// watering can doses
			if (index >= 5 && index <= 13 && player.tempBoolean) {
				return;
			}
			// secateurs
			if ((index == 3 || index == 4)
					&& (player.getItems().playerHasItem(7409) || player
							.getItems().playerHasItem(5329))) {
				return;
			}

			player.getPA().sendFrame126("" + toolStoreData.getToolName(),
					toolStoreData.getToolFrameId());
			player.getPA().sendFrame126("" + count,
					toolStoreData.getToolCountFrameId());

		}
	}

	/* store any item with id and amount provided */

	public void storeItems(int itemId, int amount) {
		ToolStoreData toolStoreData = ToolStoreData.forId(itemId);
		if (toolStoreData == null) {
			return;
		}

		int storeAmount = tools[toolStoreData.getToolIndex()];
		int finalAmount = amount;
		if (!player.getItems().playerHasItem(itemId))
			return;
		if (toolStoreData.getToolMaxQuantity() == storeAmount
				|| (itemId == 7409 || itemId == 5329)
				&& (tools[3] == 1 || tools[4] == 1) || hasWateringCanInStore()
				&& toolStoreData.getToolId() != 5332 && itemId >= 5340
				&& itemId <= 5331) {
			player.sendMessage("You can't store any more of those.");
			return;
		}
		if (player.getItems().getItemAmount(itemId) <= 0) {
			player.sendMessage("You aren't carrying any of those.");
			return;
		}
		if (player.getItems().getItemAmount(itemId) < amount) {
			finalAmount = player.getItems().getItemAmount(itemId);
		}

		player.getItems().deleteItem(itemId, finalAmount);
		tools[toolStoreData.getToolIndex()] += finalAmount;
		updateStore();

	}

	/* withdraw any item with item id and amount provided */

	public void withdrawItems(int itemId, int amount) {
		ToolStoreData toolStoreData = ToolStoreData.forId(itemId);
		if (toolStoreData == null) {
			return;
		}

		if (player.getItems().freeSlots()  <= 0) {
			player.sendMessage("Not enough space in your inventory.");
			return;
		}
		if (tools[toolStoreData.getToolIndex()] <= 0) {
			player.sendMessage(
					"You haven't got any of those stored in here.");
			return;
		}
		int finalAmount;
		if (amount > tools[toolStoreData.getToolIndex()]) {
			finalAmount = tools[toolStoreData.getToolIndex()];
		} else {
			finalAmount = amount;
		}
		if (finalAmount > player.getItems().freeSlots() ) {
			finalAmount = player.getItems().freeSlots() ;
		}

		tools[toolStoreData.getToolIndex()] -= finalAmount;
		player.getItems().addItem(itemId, finalAmount);
		updateStore();
	}

	/* note any item with the item id provided */

	public boolean noteItem(int itemId) {

		if (Item.itemIsNote[itemId]) {
		player.getDH().sendNpcChat1("That is a banknote!", 3021, "Tool Leprechaun" , 9810);
			return true;
		}
		for (int item : NOTABLE_ITEMS) {
			if (itemId == item) {
				int count = player.getItems().getItemAmount(itemId);
				player.getItems().deleteItem(itemId, count);
				player.getItems().addItem(itemId + 1, count);
				player.getDH().sendStatement(
						"The tool leprechaun notes those items for you.");

				return true;
			} 
		} 
				player.getDH().sendNpcChat1("Nay, I've got no banknotes to exchange for that item.", 3021, "Tool Leprechaun" , 9810);
		return true;
	}

}
