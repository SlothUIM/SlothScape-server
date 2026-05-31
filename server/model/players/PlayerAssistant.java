package server.model.players;

import server.Config;
import server.ServerStatusWriter;
import server.model.npcs.NPCHandler;
import server.model.npcs.NPC;
import server.util.Buffer;
import server.util.Misc;
import server.event.DelayEvent;
import server.model.players.combat.Degrade.DegradableItem;
import server.model.players.combat.magic.MagicData;
import server.model.players.combat.magic.NonCombatSpells;
import server.model.players.content.ItemsKeptOnDeath;
import server.model.players.skills.Skill;
import server.model.players.skills.construction.util.POHPalette;
import server.model.players.skills.construction.util.POHPalette.POHPaletteTile;
import server.model.shops.ShopAssistant;
import server.clip.*;
import server.content.clans.Clan;
import server.content.instances.InstancedAreaManager;
import server.model.objects.Object;
import server.model.path.RS317PathFinder;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import server.model.Entity;
import server.model.items.EquipmentSet;
import server.model.items.ItemAssistant;
import server.model.items.ItemDefinition;
import server.model.items.ItemList;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.containers.LootingBag;
import server.model.items.containers.RunePouch;
import server.model.map.Palette;
import server.model.map.PaletteTile;
import server.model.minigames.NightmareZone;
import server.world.*;

import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Charsets;

import java.util.ArrayList;
import java.util.List;

import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;

public class PlayerAssistant {

	private Player c;

	public PlayerAssistant(Player Client) {
		this.c = Client;
	}

	public int CraftInt, Dcolor, FletchInt;

	/**
	 * MulitCombat icon
	 * 
	 * @param i1
	 *            0 = off 1 = on
	 */
	public void multiWay(int i1) {
		// synchronized(c) {
		c.outStream.createFrame(61);
		c.outStream.writeByte(i1);
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}
	/**
	 * Changes the main displaying sprite on an interface. The index represents the
	 * location of the new sprite in the index of the sprite array.
	 * 
	 * @param componentId
	 *            the interface
	 * @param index
	 *            the index in the array
	 */
	public void sendChangeSprite(int componentId, byte index) {
		if (c == null || c.getOutStream() == null) {
			return;
		}
		Buffer stream = c.getOutStream();
		stream.createFrame(7);
		stream.writeDWord(componentId);
		stream.writeByte(index);
		c.flushOutStream();
	}
	/*
	 * Vengeance
	 */
	public void castVeng() {
		if (c.playerLevel[6] < 94) {
			c.sendMessage("You need a magic level of 94 to cast this spell.");
			return;
		}
		if (c.playerLevel[1] < 40) {
			c.sendMessage("You need a defence level of 40 to cast this spell.");
			return;
		}
		if (!c.getItems().playerHasItem(9075, 4)
				|| !c.getItems().playerHasItem(557, 10)
				|| !c.getItems().playerHasItem(560, 2)) {
			c.sendMessage("You don't have the required runes to cast this spell.");
			return;
		}
		if (System.currentTimeMillis() - c.lastCast < 30000) {
			c.sendMessage("You can only cast vengeance every 30 seconds.");
			return;
		}
		if (c.vengOn) {
			c.sendMessage("You already have vengeance casted.");
			return;
		}
		//c.startAnimation(905);
		//c.gfx100(666);// Just use c.gfx100
		c.getItems().deleteItem2(9075, 4);
		c.getItems().deleteItem2(557, 10);// For these you need to change to
		// deleteItem(item, itemslot,
		// amount);.
		c.getItems().deleteItem2(560, 2);
		addSkillXP(112, 6);
		refreshSkill(6);
		c.vengOn = true;
		c.lastCast = System.currentTimeMillis();
	}


	public void resetAutocast() {
		c.autocastId = 0;
		c.autocasting = false;
		c.getPA().sendFrame36(108, 0);
	}

	public void sendSong(int id) {
		if (c.getOutStream() != null && c != null && id != -1) {
			c.getOutStream().createFrame(74);
			c.getOutStream().writeWordBigEndian(id);
			c.flushOutStream();
		}
	}



	public void musicManager(String action, int songID) {
		if (c.getOutStream() != null && c != null) {
			if (action == "PLAY") {
				c.getOutStream().createFrame(74);
				c.getOutStream().writeWordBigEndian(songID);
				c.flushOutStream();
			}
			if (action == "STOP") {
				c.getOutStream().createFrame(74);
				c.getOutStream().writeWordBigEndian(65535);
				c.flushOutStream();
			}
			c.updateRequired = true;
			c.getPA().requestUpdates();
		}
	}
	public void sendQuickSong(int id, int songDelay) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(121);
			c.getOutStream().writeWordBigEndian(id);
			c.getOutStream().writeWordBigEndian(songDelay);
			c.flushOutStream();
		}
	}
	public void sendSound(int id, int delay, int volume) {
		sendSound(id, 0, delay, volume);
	}
	public void sendSound(int id, int type, int delay, int volume) {
		if(c.getOutStream() != null && c != null && id != -1) {
			c.getOutStream().createFrame(174);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeByte(type);
			c.getOutStream().writeWord(delay);
			c.getOutStream().writeWord(volume);
			c.flushOutStream();
		}
	}
	public void sendAreaSound(int id, int delay, int volume) {
		sendNPCSound(id, 0, delay, volume);
	}
	public void sendNPCSound(int id, int type, int delay, int volume) {
		if(c.getOutStream() != null && c != null && id != -1) {
			c.getOutStream().createFrame(179);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeByte(type);
			c.getOutStream().writeWord(delay);
			c.getOutStream().writeWord(volume);
			c.flushOutStream();
		}
	}
	public void sendScrollMax(int scrollMax, int interfaceId) { 
		if (c.getOutStream() != null && c != null && scrollMax > 0) {
			c.getOutStream().createFrame(183);
			c.getOutStream().writeWord(scrollMax);
			c.getOutStream().writeWord(interfaceId); 
			c.flushOutStream();
		}
	}
	public void sendLink(String s) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(187);
			c.getOutStream().writeString(s);
		}
	}

	public void setSkillLevel(int skillNum, int currentLevel, int XP) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(134);
			c.getOutStream().writeByte(skillNum);
			c.getOutStream().writeDWord_v1(XP);
			c.getOutStream().writeByte(currentLevel);
			c.flushOutStream();
		}
	}

	public void sendFrame106(int sideIcon) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(106);
			c.getOutStream().writeByteC(sideIcon);
			c.flushOutStream();
			requestUpdates();
		}
	}

	public void sendFrame107() {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(107);
			c.flushOutStream();
		}
	}

	public void sendFrame178(int id, int state, int x, int y) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(178);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeWord(state);
			c.getOutStream().writeWord(x);
			c.getOutStream().writeWord(y);

			c.flushOutStream();
		}
	}

	public void sendConfig(int id, int value) {
		if (c.getOutStream() != null && c != null) {
			if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
				c.getOutStream().createFrame(87);
				c.getOutStream().writeWordBigEndian_dup(id);
				c.getOutStream().writeDWord_v1(value);
				c.flushOutStream();
			} else {
				c.getOutStream().createFrame(36);
				c.getOutStream().writeWordBigEndian(id);
				c.getOutStream().writeByte(value);
				c.flushOutStream();
			}
		}
	}
	public void sendFrame36(int id, int state) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(36);
			c.getOutStream().writeWordBigEndian(id);
			c.getOutStream().writeByte(state);
			c.flushOutStream();
		}
	}
	public void sendFrame185(int Frame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(185);
			c.getOutStream().writeWordBigEndianA(Frame);
		}
	}

	public void showInterface(int interfaceid) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(97);
			c.getOutStream().writeWord(interfaceid);
			c.flushOutStream();
		}
		c.setInterfaceOpen(interfaceid);
	}

	public void sendFrame248(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame246(int MainFrame, int SubFrame, int SubFrame2) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(246);
			c.getOutStream().writeWordBigEndian(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.getOutStream().writeWord(SubFrame2);
			c.flushOutStream();
		}
	}

	public void sendFrame171(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(171);
			c.getOutStream().writeByte(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}
	public void setInterfaceVisible(int interfaceId, boolean visible) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(171);
			c.getOutStream().writeByte(visible ? 1 : 0);
			c.getOutStream().writeWord(interfaceId);
		}
	}
	public void sendFrame200(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(200);
			c.getOutStream().writeWord(MainFrame);
			c.getOutStream().writeWord(SubFrame);
			c.flushOutStream();
		}
	}

	public void sendFrame70(int i, int o, int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(70);
			c.getOutStream().writeWord(i);
			c.getOutStream().writeWordBigEndian(o);
			c.getOutStream().writeWordBigEndian(id);
			c.flushOutStream();
		}
	}

	public void sendFrame75(int MainFrame, int SubFrame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(75);
			c.getOutStream().writeWordBigEndianA(MainFrame);
			c.getOutStream().writeWordBigEndianA(SubFrame);
			c.flushOutStream();
		}
	}

	public void showChatboxInterface(int Frame) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(164);
			c.getOutStream().writeWordBigEndian_dup(Frame);
			c.flushOutStream();
		}
	}

	public void setPrivateMessaging(int i) { // friends and ignore list status
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(221);
			c.getOutStream().writeByte(i);
			c.flushOutStream();
		}
	}

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(206);
			c.getOutStream().writeByte(publicChat);
			c.getOutStream().writeByte(privateChat);
			c.getOutStream().writeByte(tradeBlock);
			c.flushOutStream();
		}
	}

	public void sendFrame87(int id, int state) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(87);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.getOutStream().writeDWord_v1(state);
			c.flushOutStream();
		}
	}

	public void sendPM(long name, int rights, byte[] chatmessage,
			int messagesize) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSize(196);
			c.getOutStream().writeQWord(name);
			c.getOutStream().writeDWord(c.lastChatId++);
			c.getOutStream().writeByte(rights);
			c.getOutStream().writeBytes(chatmessage, messagesize, 0);
			c.getOutStream().endFrameVarSize();
			c.flushOutStream();
			Misc.textUnpack(chatmessage, messagesize);
			Misc.longToPlayerName(name);
		}
	}
	public void displayIncomingPM(long fromName, int rights, byte[] chatMessage, int size) {
	    c.getOutStream().createFrameVarSize(196);
	    c.getOutStream().writeQWord(fromName);
	    c.getOutStream().writeDWord(new Random().nextInt());
	    c.getOutStream().writeByte(rights);
	    c.getOutStream().writeBytes(chatMessage, size, 0);
        c.sendMessage("Private message sent from " + fromName + ".");
	    c.getOutStream().endFrameVarSize();
	}

	public void sendPM(String targetName, String message) {
	    long targetLong = Misc.playerNameToInt64(targetName);
	    Player target = PlayerHandler.getPlayerByName(targetName);

	    byte[] chatMessage = new byte[256];
	    int messageSize = Misc.textPack(message, chatMessage);

	    // CASE 1: Target is online on same world
	    if (target != null && target.isActive) {
	        target.getPA().displayIncomingPM(Misc.playerNameToInt64(c.playerName), c.playerRights, chatMessage, messageSize);
	        c.sendMessage("Private message sent to " + targetName + ".");
	        return;
	    }

	    // CASE 2: Target is on another world, queue the message
	    try {
	        Path pmQueue = Paths.get("C:/Users/Sasqu/Dropbox/public/pm_queue.txt");
	        List<String> lines = new ArrayList<>();
	        if (Files.exists(pmQueue)) {
	            lines = Files.readAllLines(pmQueue, Charsets.UTF_8);
	        }

	        String packedMessage = Base64.getEncoder().encodeToString(Arrays.copyOf(chatMessage, messageSize));
	        String newLine = c.playerName + "|" + c.playerRights + "|" + targetName + "|" + packedMessage;

	        lines.add(newLine);
	        Files.write(pmQueue, lines, Charsets.UTF_8);
	        c.sendMessage("Private message queued for " + targetName + ".");
	    } catch (IOException e) {
	        c.sendMessage("Failed to send cross-world PM.");
	        e.printStackTrace();
	    }
	}

	/**
	 * Flashes Sidebar Icon
	 */

	public void flashSideBarIcon(int i1) {
		// Makes the sidebar Icons flash
		// Usage: i1 = 0 through -12 inorder to work
		if (c.getOutStream() != null) {
			c.outStream.createFrame(24);
			c.outStream.writeByteA(i1);
		}
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}
	public void sendSpecialAttack(int amount, int specialEnabled) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(204);
			c.getOutStream().writeByte(amount);
			c.getOutStream().writeByte(specialEnabled);
			c.flushOutStream();
		}
	}
	public void sendWorldHop(int targetPort) {
		// 1. --- COMBAT & SAFETY CHECKS ---
		if (!c.isIdle && c.underAttackBy2 > 0) {
			c.sendMessage("You can't hop worlds until 10 seconds after the end of combat.");
			return;
		}
		if (c.underAttackBy > 0) {
			c.sendMessage("You can't hop worlds until 10 seconds after the end of combat.");
			return;
		}

		// (Optional) Add your specific Duel Arena / Minigame checks here
		// Example: if (c.inDuelArena()) { c.sendMessage("You can't hop during a duel!"); return; }

		// 2. --- THE HOP EXECUTION ---
		if (System.currentTimeMillis() - c.logoutDelay > 10000) {

			// Tell Bridge API they are offline temporarily during the hop so the Friends List updates
			ApiClient.updatePlayerStatus(c.playerName, 0);

			// 3. Fire Packet 182 with the new port!
			c.getOutStream().createFrame(182);
			c.getOutStream().writeWord(targetPort);
			c.flushOutStream();

			// Clean up active events to prevent ghosting
			server.event.CycleEventHandler.getSingleton().stopEvents(c);

			// 4. Safely save and kill the socket
			c.properLogout = true;
			c.disconnected = true;

		} else {
			c.sendMessage("You must wait a few seconds before hopping worlds.");
		}
	}
	public void createPlayerHints(int type, int id) {
		if (c.getOutStream() == null) return;
		if (c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(type);
			c.getOutStream().writeWord(id);
			c.getOutStream().write3Byte(0);
			c.flushOutStream();
		}
	}

	public void createObjectHints(int x, int y, int height, int pos) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(254);
			c.getOutStream().writeByte(pos);
			c.getOutStream().writeWord(x);
			c.getOutStream().writeWord(y);
			c.getOutStream().writeByte(height);
			c.flushOutStream();
		}
	}

	/*public void loadPM(long playerName, int world) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (world != 0) {
				world += 9;
			} else if (!Config.WORLD_LIST_FIX) {
				world += 1;
			}
			c.getOutStream().createFrame(50);
			c.getOutStream().writeQWord(playerName);
			c.getOutStream().writeByte(world);
			c.flushOutStream();
		}
	}*/
	public void updateWorldTabCounts() {
		int totalWorlds = 20; // Must match the client's totalWorlds loop!
		int basePlayerId = 55054; // The exact ID for World 1's player text

		c.getPA().sendFrame126("Current World - "+ ServerStatusWriter.WORLD_ID, 55001);
		for (int i = 0; i < totalWorlds; i++) {
			int worldNum = i + 1;
			int targetTextId = basePlayerId + (i * 6);

			// Call your Bridge API to get the live count for this world
			// (Adjust this method call to match your actual API structure)
			int playersOnline = ApiClient.getPlayersOnWorld(worldNum);

			if (playersOnline >= 0) {
				c.getPA().sendFrame126(playersOnline + "", targetTextId);
			} else {
				c.getPA().sendFrame126("-", targetTextId);
			}
			//bottom text thats shown if no world favorites "Right-click on worlds to\\nset them as Favourites."
		}

		// --- UPDATING THE FAVORITES BAR ---
		// Note: You will eventually want to check the player's saved favorite worlds here.
		// For now, we will just update them as World 323 and 340 like your client has hardcoded.
		// --- UPDATING THE FAVORITES BAR DYNAMICALLY ---
		if (c.favoriteWorld1 > 0) {
			int fav1Players = ApiClient.getPlayersOnWorld(c.favoriteWorld1);
			c.getPA().sendFrame126(c.favoriteWorld1 + "", 55402); // Update World Num
			c.getPA().sendFrame126(fav1Players >= 0 ? "" + fav1Players : "-", 55404); // Update Players
			c.getPA().sendFrame126("Favorite", 55405); // Update Activity
		} else {
			c.getPA().sendFrame126("-", 55402);
			c.getPA().sendFrame126("-", 55404);
			c.getPA().sendFrame126("-", 55405);
		}

		if (c.favoriteWorld2 > 0) {
			int fav2Players = ApiClient.getPlayersOnWorld(c.favoriteWorld2);
			c.getPA().sendFrame126(c.favoriteWorld2 + "", 55412); // Update World Num
			c.getPA().sendFrame126(fav2Players >= 0 ? "" + fav2Players : "-", 55414); // Update Players
			c.getPA().sendFrame126("Favorite", 55415); // Update Activity
		} else {
			c.getPA().sendFrame126("-", 55412);
			c.getPA().sendFrame126("-", 55414);
			c.getPA().sendFrame126("-", 55415);
		}

		if(c.favoriteWorld1 <= 0 && c.favoriteWorld2 <= 0) {
			c.getPA().sendFrame126("Right-click on worlds to\\nset them as Favourites", 55002);
			c.getPA().sendFrame126("", 55402);
			c.getPA().sendFrame126("", 55404);
			c.getPA().sendFrame126("", 55405);
			c.getPA().sendFrame126("", 55412);
			c.getPA().sendFrame126("", 55414);
			c.getPA().sendFrame126("", 55415);
		}
	}
	public void loadPM(long friend, int world) {//sendfriend
		c.getOutStream().createFrame(50);
		c.getOutStream().writeQWord(friend);
		c.getOutStream().writeByte(world);

	}
	public void removeAllWindows() {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(219);
			c.flushOutStream();
		}
		resetVariables();
	}
	public void shakeCamera(int parameter, int jitter, int amplitude, int frequency) {
	    c.getOutStream().createFrame(35);
	    c.getOutStream().writeByte(parameter);
	    c.getOutStream().writeByte(jitter);
	    c.getOutStream().writeByte(amplitude);
	    c.getOutStream().writeByte(frequency);
	}
	public void closeAllWindows() {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(219);
			c.flushOutStream();
			resetVariables();
			c.getItems().updateInventory();
			//c.getMakeWidget().reset();
		}
	}

	public void sendFrame34(int id, int slot, int column, int amount) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.outStream.createFrameVarSizeWord(34); // init item to smith
			// screen
			c.outStream.writeWord(column); // Column Across Smith Screen
			c.outStream.writeByte(4); // Total Rows?
			c.outStream.writeDWord(slot); // Row Down The Smith Screen
			c.outStream.writeWord(id + 1); // item
			c.outStream.writeByte(amount); // how many there are?
			c.outStream.endFrameVarSizeWord();
		}
	}
	public void movePlayer(Location coord) {
		movePlayer(coord.getX(), coord.getY(), coord.getZ());
	}
	public void sendFrame34a(int frame, int item, int slot, int amount) {
		c.outStream.createFrameVarSizeWord(34);
		c.outStream.writeWord(frame);
		c.outStream.writeByte(slot);
		c.outStream.writeWord(item + 1);
		c.outStream.writeByte(255);
		c.outStream.writeDWord(amount);
		c.outStream.endFrameVarSizeWord();
	}
	public void walkableInterface(int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(208);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.flushOutStream();
		}
	}	
	public void walkableInterface2(int id) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(209);
			c.getOutStream().writeWordBigEndian_dup(id);
			c.flushOutStream();
		}
	}

	public int mapStatus = 0;

	public void setMinimapState(int state) { // used for disabling map
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (mapStatus != state) {
				mapStatus = state;
				c.getOutStream().createFrame(99);
				c.getOutStream().writeByte(state);
				c.flushOutStream();
			}
		}
	}

	public void sendCrashFrame() { // used for crashing cheat clients
		//synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(123);
			c.flushOutStream();
		}
		//}
	}

	/**
	 * Reseting animations for everyone
	 **/

	public int getX() {
		return absX;
	}

	public int getY() {
		return absY;
	}
	private int absX;
	private int absY;
	public void frame1() {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Player person = (Player) PlayerHandler.players[i];
				if (person != null) {
					if (person.getOutStream() != null && !person.disconnected) {
						if (c.distanceToPoint(person.getX(), person.getY()) <= 25) {
							person.getOutStream().createFrame(1);
							person.flushOutStream();
							person.getPA().requestUpdates();
						}
					}
				}
			}
		}
	}

	/**
	 * Creating projectile
	 **/
	public void createProjectile(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getLastKnownLocation().getRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getLastKnownLocation().getRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(16);
			c.getOutStream().writeByte(64);
			c.flushOutStream();
		}
	}
	public void createProjectile2(int x, int y, int offX, int offY, int angle,
			int speed, int gfxMoving, int startHeight, int endHeight,
			int lockon, int time, int slope) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC((y - (c.getLastKnownLocation().getRegionY() * 8)) - 2);
			c.getOutStream().writeByteC((x - (c.getLastKnownLocation().getRegionX() * 8)) - 3);
			c.getOutStream().createFrame(117);
			c.getOutStream().writeByte(angle);
			c.getOutStream().writeByte(offY);
			c.getOutStream().writeByte(offX);
			c.getOutStream().writeWord(lockon);
			c.getOutStream().writeWord(gfxMoving);
			c.getOutStream().writeByte(startHeight);
			c.getOutStream().writeByte(endHeight);
			c.getOutStream().writeWord(time);
			c.getOutStream().writeWord(speed);
			c.getOutStream().writeByte(slope);
			c.getOutStream().writeByte(64);
			c.flushOutStream();
		}
	}
	/**
	 * Sends the map region to the player.
	 */
	public PlayerAssistant sendMapRegion() {
		/*if(c.getInstance() != null && c.getInstance() instanceof TheGauntlet) {
			TheGauntlet gauntlet = (TheGauntlet) c.getInstance();
			gauntlet.getDungeon().generateDungeonPalette();
			sendConstructedMap(gauntlet.getDungeon().getLayout());
			return this;
		}*/
		if (c.isInCoXRaid()) {
			// 1. Update the server's tracking of the player's region
			c.setLastKnownLocation(c.getLocation().copy());

			// 2. Dynamically rebuild and send the map window
			c.getRaidSession().refreshRaidMap(c);

			// 3. Stop normal map loading
			return this;
		}
		c.setLastKnownLocation(c.getLocation().copy());
		/*if (c.isInsideHouse()) {
	        // Use generatePalette (which only updates data) 
	        // instead of createPalette (which teleports/re-instances)
	        Construction.generatePalette(c);

	        // Send the map packet directly
	        // This re-centers the 104x104 grid on your CURRENT feet
	        sendConstructedMapPOH(c.getMapInstance().getPalette());
	        
	        // Re-sync the furniture/objects for the new region view
	        Construction.placeAllFurniture(c, c.getLocation().getZ());
	        return this;
	    }*/
		c.getOutStream().createFrame(73);
		c.getOutStream().writeWordA(c.getLastKnownLocation().getRegionX() + 6);
		c.getOutStream().writeWord(c.getLastKnownLocation().getRegionY() + 6);
		c.flushOutStream();
		return this;
	}

	public PlayerAssistant sendConstructedMap(Palette palette) {
		c.setLastKnownLocation(c.getLocation().copy());
		c.getOutStream().createFrameVarSizeWord(241);
		c.getOutStream().writeWordA(c.getLastKnownLocation().getRegionY() + 6);
		c.getOutStream().writeWord(c.getLastKnownLocation().getRegionX() + 6);
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < 13; x++) {
				for (int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					c.getOutStream().writeWord(tile != null ? 5 : 0);
					if (tile != null) {
						long val = (tile.isVisible() ? 1L : 0L) << 32;
						val |= tile.getX() << 14;
						val |= tile.getY() << 3;
						val |= tile.getZ() << 24;
						val |= tile.getRotation() << 1;
						c.getOutStream().writeQWord(val);
					}
				}
			}
		}
		c.getOutStream().endFrameVarSizeWord();
		c.flushOutStream();
		return this;
	}
	public void commandFrame(int i) {
		c.outStream.createFrame(28);
		c.outStream.writeByte(i);
	}
/*
 * public int getRegionX() {
		return (this.x >> 3) - 6;
	}
 */
	public void sendConstructedMapPOH(POHPalette palette) {
	    c.setLastKnownLocation(c.getLocation().copy());
	    c.getOutStream().createFrameVarSizeWord(241);
	    c.getOutStream().writeWordA(c.getLocation().getRegionY() + 6);
	    c.getOutStream().writeWord(c.getLocation().getRegionX() + 6);

	    for (int z = 0; z < 4; z++) {
	        for (int x = 0; x < 13; x++) {
	            for (int y = 0; y < 13; y++) {
	                POHPaletteTile tile = palette.getTile(x, y, z);
	                c.getOutStream().writeWord(tile != null ? 5 : 0); // Client sees '5' and reads QWord
	                if (tile != null) {
	                    long val = (tile.isVisible() ? 1L : 0L) << 32;
	                    val |= (long)tile.getX() << 14;
	                    val |= (long)tile.getY() << 3;
	                    val |= (long)tile.getZ() << 24;
	                    val |= (long)tile.getRotation() << 1;
	                    
	                    c.getOutStream().writeQWord(val);
	                } 
	            }
	        }
	    }
	    c.getOutStream().endFrameVarSizeWord();
	    c.flushOutStream();
	}
	public void removeObjects(int chunkX, int chunkY, int height) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(153);
			c.getOutStream().writeByte(chunkX);
			c.getOutStream().writeByte(chunkY);
			c.getOutStream().writeByte(height);
			c.flushOutStream();
		}
	}
	public void createProjectile3(int casterY, int casterX, int offsetY, int offsetX, int gfxMoving, int StartHeight,
			int endHeight, int speed, int AtkIndex) {
		for (int i = 1; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				Player p = PlayerHandler.players[i];
				if (p.withinDistance(c.getX(), c.getY(), p.getX(), p.getY(), 60)) {
					if (p.getHeight() == c.getHeight()) {
						if (PlayerHandler.players[i] != null && !PlayerHandler.players[i].disconnected) {
							c.getOutStream().createFrame(85);
							c.getOutStream().writeByteC((casterY - (p.getLastKnownLocation().getRegionY() * 8)) - 2);
							c.getOutStream().writeByteC((casterX - (p.getLastKnownLocation().getRegionX() * 8)) - 3);
							c.getOutStream().createFrame(117);
							c.getOutStream().writeByte(50);
							c.getOutStream().writeByte(offsetY);
							c.getOutStream().writeByte(offsetX);
							c.getOutStream().writeWord(AtkIndex);
							c.getOutStream().writeWord(gfxMoving);
							c.getOutStream().writeByte(StartHeight);
							c.getOutStream().writeByte(endHeight);
							c.getOutStream().writeWord(51);
							c.getOutStream().writeWord(speed);
							c.getOutStream().writeByte(16);
							c.getOutStream().writeByte(64);
						}
					}
				}
			}
		}
	}
	// projectiles for everyone within 25 squares
	public void createPlayersProjectile(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time) {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = (Player) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							if (p.getHeight() == c.getHeight())
								person.getPA().createProjectile(x, y, offX,
										offY, angle, speed, gfxMoving,
										startHeight, endHeight, lockon, time);
						}
					}
				}
			}
		}
	}

	public void createPlayersProjectile2(int x, int y, int offX, int offY,
			int angle, int speed, int gfxMoving, int startHeight,
			int endHeight, int lockon, int time, int slope) {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = (Player) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							person.getPA().createProjectile2(x, y, offX, offY,
									angle, speed, gfxMoving, startHeight,
									endHeight, lockon, time, slope);
						}
					}
				}
			}
		}
	}

	/**
	 ** GFX
	 **/
	public void stillGfx(int id, int x, int y, int height, int time) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(y - (c.getLastKnownLocation().getRegionY() * 8));
			c.getOutStream().writeByteC(x - (c.getLastKnownLocation().getRegionX() * 8));
			c.getOutStream().createFrame(4);
			c.getOutStream().writeByte(0);
			c.getOutStream().writeWord(id);
			c.getOutStream().writeByte(height);
			c.getOutStream().writeWord(time);
			c.flushOutStream();
		}
	}

	// creates gfx for everyone
	public void createPlayersStillGfx(int id, int x, int y, int height, int time) {
		// synchronized(c) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			Player p = PlayerHandler.players[i];
			if (p != null) {
				Player person = (Player) p;
				if (person != null) {
					if (person.getOutStream() != null) {
						if (person.distanceToPoint(x, y) <= 25) {
							person.getPA().stillGfx(id, x, y, height, time);
						}
					}
				}
			}
		}
	}
	public void sendObjectAnimation(Player player, GameObject object, int anim) {
		sendPosition(player, object.getPosition());
	    player.getOutStream().createFrame(160);
	    player.getOutStream().writeByteS(0); // Local chunk coords
	    player.getOutStream().writeByteS((object.type() << 2) + (object.getFace() & 3));
	    player.getOutStream().writeWordA(anim);
	}
	public void sendPosition(Player player, Location location){
		final Location other = player.getLocation();
		int localX = location.getX() - other.getRegionX() * 8;
	    int localY = location.getY() - other.getRegionY() * 8;

	    player.getOutStream().createFrame(85);
	    player.getOutStream().writeByteC(localY);
	    player.getOutStream().writeByteC(localX);
	}
	public void sendPlayerObjectAnimation(Player player, int x, int y, int animation, int type, int orientation, int height) {
		if (player == null || player.getHeight() != height)
			return;

		// Local tile offsets
		int localX = x - (player.getLocation().getRegionX() * 8);
		int localY = y - (player.getLocation().getRegionY() * 8);

		player.getOutStream().createFrame(85);
		player.getOutStream().writeByteC(localY);
		player.getOutStream().writeByteC(localX);

		player.getOutStream().createFrame(160);
		player.getOutStream().writeByteS(0); // Local chunk coords
		player.getOutStream().writeByteS((type << 2) + (orientation & 3));
		player.getOutStream().writeWordA(animation);
	}
	public void objectAnim(Player player, int x, int y, int animation, int type, int orientation, int height) {
		if (player == null || player.getHeight() != height)
			return;

		// Local tile offsets
		int localX = x - (player.getLastKnownLocation().getRegionX() * 8);
		int localY = y - (player.getLastKnownLocation().getRegionY() * 8);

		player.getOutStream().createFrame(85);
		player.getOutStream().writeByteC(localY);
		player.getOutStream().writeByteC(localX);

		player.getOutStream().createFrame(160);
		player.getOutStream().writeByteS((x & 7) << 4 | (y & 7)); // Local chunk coords
		player.getOutStream().writeByteS((type << 2) + (orientation & 3));
		player.getOutStream().writeWordA(animation);
	}
	public void sendObject_cons(int objectX, int objectY, int objectId,
			int face, int objectType, int height) {
		if (c.getOutStream() != null && c != null) {

		    int localX = objectX - (c.getLastKnownLocation().getRegionX() * 8);
		    int localY = objectY - (c.getLastKnownLocation().getRegionY() * 8);
			// packet 85
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(localY);
			c.getOutStream().writeByteC(localX);
			if (objectId != -1) { // removing
				c.getOutStream().createFrame(152);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
				c.getOutStream().writeByte(height);
			}
			//c.flushOutStream();
		}
	}
	/**
	 * Objects, add and remove
	 **/
	public void object(int objectId, int objectX, int objectY, int face, int objectType) {
		 //synchronized(c) {
			if (c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(85);
				c.getOutStream().writeByteC(objectY - (c.getLastKnownLocation().getRegionY() * 8));
				c.getOutStream().writeByteC(objectX - (c.getLastKnownLocation().getRegionX() * 8));
				c.getOutStream().createFrame(101);
				c.getOutStream().writeByteC((objectType << 2) + (face & 3));
				c.getOutStream().writeByte(0);

				if (objectId != -1) { // removing
					c.getOutStream().createFrame(151);
					c.getOutStream().writeByteS(0);
					c.getOutStream().writeWordBigEndian(objectId);
					c.getOutStream().writeByteS((objectType << 2) + (face & 3));
				}
				c.flushOutStream();
			}
		 //}
	}

	public void checkObjectSpawn(int objectId, int objectX, int objectY,
			int face, int objectType) {
		Location loc = Location.of(objectX, objectY);
		loc.getRegion().addWorldObject(objectId, objectX, objectY, c.getHeight(), objectType, face);
		if (c.distanceToPoint(objectX, objectY) > 60)
			return;
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(85);
			c.getOutStream().writeByteC(objectY - (c.getLocation().getRegionY() * 8));
			c.getOutStream().writeByteC(objectX - (c.getLocation().getRegionX() * 8));
			c.getOutStream().createFrame(101);
			c.getOutStream().writeByteC((objectType << 2) + (face & 3));
			c.getOutStream().writeByte(0);

			if (objectId != -1) { // removing
				c.getOutStream().createFrame(151);
				c.getOutStream().writeByteS(0);
				c.getOutStream().writeWordBigEndian(objectId);
				c.getOutStream().writeByteS((objectType << 2) + (face & 3));
			}
			c.flushOutStream();
		}
	}
	/**
	 * Show option, attack, trade, follow etc
	 **/
	public String optionType = "null";

	public void showOption(int i, int l, String s, int a) {
		// synchronized(c) {
		if (c.getOutStream() != null && c != null) {
			if (!optionType.equalsIgnoreCase(s)) {
				optionType = s;
				c.getOutStream().createFrameVarSize(104);
				c.getOutStream().writeByteC(i);
				c.getOutStream().writeByteA(l);
				c.getOutStream().writeString(s);
				c.getOutStream().endFrameVarSize();
				c.flushOutStream();
			}
		}
	}
	public void openUpBank() {
		//if (c.getTutorial().isActive()) {
		//	c.getTutorial().refresh();
		//	return;
		//}
		//c.getPA().sendChangeSprite(58014, c.placeHolders ? (byte) 1 : (byte) 0);
		if (c.viewingLootBag || c.addingItemsToLootBag || c.viewingRunePouch) {
			c.sendMessage("You should stop what you are doing before opening the bank.");
			return;
		}
		resetVariables();
		/*if (c.getBankPin().isLocked() && c.getBankPin().getPin().trim().length() > 0) {
			c.getBankPin().open(2);
			c.isBanking = false;
			c.inSafeBox = false;
			return;
		}*/

		if (c.takeAsNote) {
			//c.takeAsNote = false;
			sendFrame36(115, 1);
		} else {
			//c.takeAsNote = true;
			sendFrame36(115, 0);
		}

		if (c.inWild() && !(c.getRights().isOrInherits(Right.ADMINISTRATOR))) {
			c.sendMessage("You can't bank in the wilderness!");
			return;
		}
		//if (!c.getMode().isBankingPermitted()) {
		//	c.sendMessage("Your game mode prohibits use of the banking system.");
		//	return;
		//}
		if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			} else {
				c.sendMessage("You cannot bank whilst dueling.");
				return;
			}
		}
		//if (c.getBank().getBankSearch().isSearching()) {
		//	c.getBank().getBankSearch().reset();
		//}
		//c.getPA().sendScrollMax(700, 5382);
		//c.getPA().sendFrame126("Search", 58063);
		if (c.getOutStream() != null && c != null) {
			c.isBanking = true;
			c.getItems().resetItems(5064);
			c.getItems().rearrangeBank();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			c.getOutStream().createFrame(248);
			c.getOutStream().writeWordA(5292);// ok perfect
			c.getOutStream().writeWord(5063);
			c.getItems().resetItems(3214);
			c.flushOutStream();
		}
	}

	public void otherBank(Player c, Player o) {
		/*if(o == c || o == null || c == null)
		{
		return;
		}

		int [] bankTabItems = new int[Config.BANK_SIZE*9];
		System.arraycopy( o.bankItems, 0, bankTabItems, 0, o.bankItems.length);
		System.arraycopy( o.bankItems1, 0, bankTabItems, o.bankItems.length, o.bankItems1.length );
		System.arraycopy( o.bankItems2, 0, bankTabItems, o.bankItems1.length, o.bankItems2.length );
		System.arraycopy( o.bankItems3, 0, bankTabItems, o.bankItems2.length, o.bankItems3.length );
		System.arraycopy( o.bankItems4, 0, bankTabItems, o.bankItems3.length, o.bankItems4.length );
		System.arraycopy( o.bankItems5, 0, bankTabItems, o.bankItems4.length, o.bankItems5.length );
		System.arraycopy( o.bankItems6, 0, bankTabItems, o.bankItems5.length, o.bankItems6.length );
		System.arraycopy( o.bankItems7, 0, bankTabItems, o.bankItems6.length, o.bankItems7.length );
		System.arraycopy( o.bankItems8, 0, bankTabItems, o.bankItems7.length, o.bankItems8.length );

		int [] bankTabItemsN = new int[Config.BANK_SIZE*9];
		System.arraycopy( o.bankItemsN, 0, bankTabItemsN, 0, o.bankItemsN.length);
		System.arraycopy( o.bankItems1N, 0, bankTabItemsN, o.bankItemsN.length, o.bankItems1N.length );
		System.arraycopy( o.bankItems2N, 0, bankTabItemsN, o.bankItems1N.length, o.bankItems2N.length );
		System.arraycopy( o.bankItems3N, 0, bankTabItemsN, o.bankItems2N.length, o.bankItems3N.length );
		System.arraycopy( o.bankItems4N, 0, bankTabItemsN, o.bankItems3N.length, o.bankItems4N.length );
		System.arraycopy( o.bankItems5N, 0, bankTabItemsN, o.bankItems4N.length, o.bankItems5N.length );
		System.arraycopy( o.bankItems6N, 0, bankTabItemsN, o.bankItems5N.length, o.bankItems6N.length );
		System.arraycopy( o.bankItems7N, 0, bankTabItemsN, o.bankItems6N.length, o.bankItems7N.length );
		System.arraycopy( o.bankItems8N, 0, bankTabItemsN, o.bankItems7N.length, o.bankItems8N.length );

		/* backup player A main tab */
		//int[] backupItems = Arrays.copyOf(c.bankItems, c.bankItems.length);
		//int[] backupItemsN = Arrays.copyOf(c.bankItemsN, c.bankItemsN.length);
		/* fill player A's main tab with player B's main tab */
		//c.bankItems = Arrays.copyOf(bankTabItems, bankTabItems.length);
		//c.bankItemsN = Arrays.copyOf(bankTabItemsN, bankTabItemsN.length);
		openUpBank();
		/* restore player A main tab */
		//c.bankItems =  Arrays.copyOf(backupItems, backupItems.length);
		//c.bankItemsN =  Arrays.copyOf(backupItemsN, backupItemsN.length);*/

	}
	public int tempItems[] = new int[Config.BANK_SIZE];
	public int tempItemsN[] = new int[Config.BANK_SIZE];
	public int tempItemsT[] = new int[Config.BANK_SIZE];
	public int tempItemsS[] = new int[Config.BANK_SIZE];
	public void setScrollPos(int interfaceId, int scrollPos)
	{
		if (c.getOutStream() != null && c != null)
		{
			c.outStream.createFrame(79);
			c.outStream.writeWordBigEndian(interfaceId);
			c.outStream.writeWordA(scrollPos);
		}
	}




	public int getInterfaceModel(int slot, int[] array, int[] arrayN) {
		int model = array[slot]-1;
		if (model == 995) {
			if (arrayN[slot] > 9999) {
				model = 1004;
			} else if (arrayN[slot] > 999) {
				model = 1003;
			} else if (arrayN[slot] > 249) {
				model = 1002;
			} else if (arrayN[slot] > 99) {
				model = 1001;
			}  else if (arrayN[slot] > 24) {
				model = 1000;
			} else if (arrayN[slot] > 4) {
				model = 999;
			} else if (arrayN[slot] > 3) {
				model = 998;
			} else if (arrayN[slot] > 2) {
				model = 997;
			} else if (arrayN[slot] > 1) {
				model = 996;
			}
		}
		return model;
	}
	public void createPlayerPM(long nameHash, int size, byte[] message) {
		c.getOutStream().createFrameVarSize(196);
		c.getOutStream().writeQWord(nameHash);
		c.getOutStream().writeDWord(new Random().nextInt());
		c.getOutStream().writeByte(size);
		c.getOutStream().writeBytes(message, message.length, 0);
		c.getOutStream().endFrameVarSize();
	}
	
	/**
	 * Private Messaging
	 **/
	public void logIntoPM() {
	    setPrivateMessaging(2);

	    // Tell all players in this world to refresh their PM status with me
	    for (int i = 1; i < Config.MAX_PLAYERS; i++) {
	        Player p = PlayerHandler.players[i];
	        if (p != null && p.isActive) {
	            p.getPA().updatePM(c.playerId, 1);
	        }
	    }

	    // Now load the friend list and determine where each friend is
	    for (int i = 0; i < c.friends.length; i++) {
	        long friendLong = c.friends[i];
	        if (friendLong == 0) continue;

	        boolean foundLocal = false;

	        // First, check this world
	        for (int j = 1; j < Config.MAX_PLAYERS; j++) {
	            Player p = PlayerHandler.players[j];
	            if (p != null && p.isActive && Misc.playerNameToInt64(p.playerName) == friendLong) {
	                // PM rules (same as your original logic)
	                if (c.playerRights >= 2 || p.privateChat == 0 ||
	                    (p.privateChat == 1 && p.getPA().isInPM(Misc.playerNameToInt64(c.playerName)))) {
	                    loadPM(friendLong, 1); // this world
	                } else {
	                    loadPM(friendLong, 0); // friend is on but ignoring
	                }
	                foundLocal = true;
	                break;
	            }
	        }

	        if (!foundLocal) {
	            // Friend not in this world, try reading their world from file
	            int worldId = PlayerHandler.getWorldIdOfPlayer(friendLong);
	            if (worldId > 0) {
	                loadPM(friendLong, worldId); // cross-world PM
	            } else {
	                loadPM(friendLong, 0); // offline
	            }
	        }
	    }
	}


	public void updatePM(int pID, int world) { // used for private chat updates
		Player p = PlayerHandler.players[pID];
		if (p == null || p.playerName == null || p.playerName.equals("null")) {
			return;
		}
		Player o = (Player) p;
		long l = Misc
				.playerNameToInt64(PlayerHandler.players[pID].playerName);

		if (p.privateChat == 0) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						loadPM(l, world);
						return;
					}
				}
			}
		} else if (p.privateChat == 1) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i]) {
						if (o.getPA().isInPM(
								Misc.playerNameToInt64(c.playerName))) {
							loadPM(l, world);
							return;
						} else {
							loadPM(l, 0);
							return;
						}
					}
				}
			}
		} else if (p.privateChat == 2) {
			for (int i = 0; i < c.friends.length; i++) {
				if (c.friends[i] != 0) {
					if (l == c.friends[i] && c.playerRights < 2) {
						loadPM(l, 0);
						return;
					}
				}
			}
		}
	}

	public boolean isInPM(long l) {
		for (int i = 0; i < c.friends.length; i++) {
			if (c.friends[i] != 0) {
				if (l == c.friends[i]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Drink AntiPosion Potions
	 * 
	 * @param itemId
	 *            The itemId
	 * @param itemSlot
	 *            The itemSlot
	 * @param newItemId
	 *            The new item After Drinking
	 * @param healType
	 *            The type of poison it heals
	 */

	public void potionPoisonHeal(int itemId, int itemSlot, int newItemId,
			int healType) {
		c.attackTimer = c.getCombat().getAttackDelay(
				c.getItems().getItemName(c.playerEquipment[c.playerWeapon])
				.toLowerCase());
		if (c.duelRule[5]) {
			c.sendMessage("Potions has been disabled in this duel!");
			return;
		}
		if (!c.isDead && System.currentTimeMillis() - c.foodDelay > 2000) {
			if (c.getItems().playerHasItem(itemId, 1, itemSlot)) {
				c.sendMessage("You drink the "
						+ c.getItems().getItemName(itemId).toLowerCase() + ".");
				c.foodDelay = System.currentTimeMillis();
				// Actions
				if (healType == 1) {
					// Cures The Poison
				} else if (healType == 2) {
					// Cures The Poison + protects from getting poison again
				}
				c.startAnimation(0x33D);
				c.getItems().deleteItem(itemId, itemSlot, 1);
				c.getItems().addItem(newItemId, 1);
				requestUpdates();
			}
		}
	}

	/**
	 * Magic on items
	 **/
	public void alchemy(int itemId, String alch) {

		/*if (c.inClanWars() || c.inClanWarsSafe()) {
			c.sendMessage("@cr10@There is no need to do this here.");
			return;
		}*/

		switch (alch) {
		case "low":
			if (System.currentTimeMillis() - c.alchDelay > 1000) {
				for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
					if (Misc.linearSearch(items, itemId) > -1) {
						c.sendMessage("You cannot alch iron man amour.");
						return;
					}
				}
				for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
					if (Misc.linearSearch(items, itemId) > -1) {
						c.sendMessage("You cannot alch ultimate iron man amour.");
						return;
					}
				}
				if (LootingBag.isLootingBag(c, itemId) || itemId == RunePouch.RUNE_POUCH_ID) {
					c.sendMessage("This kind of sorcery cannot happen.");
					return;
				}
				if (!c.getCombat().checkMagicReqs(49)) {
					return;
				}
				if (!c.getItems().playerHasItem(itemId, 1) || itemId == 995) {
					return;
				}
				if (Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
					//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.LOW_ALCH);
				}

				try {
					//PlayerLogging.write(LogType.ALCH, c, "Low alch item: " + itemId);
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				c.getItems().deleteItem(itemId, 1);
				ItemList newItemList = World.getWorld().itemHandler.getItemList(itemId);
				c.getItems().addItem(995, (int)newItemList.HighAlch);
				c.startAnimation(MagicData.MAGIC_SPELLS[49][2]);
				c.gfx100(MagicData.MAGIC_SPELLS[49][3]);
				c.alchDelay = System.currentTimeMillis();
				sendFrame106(6);
				addSkillXP(MagicData.MAGIC_SPELLS[49][7], 6);
				refreshSkill(6);
			}
			break;

		case "high":
			if (System.currentTimeMillis() - c.alchDelay > 2000) {
				for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
					if (Misc.linearSearch(items, itemId) > -1) {
						c.sendMessage("You cannot alch iron man amour.");
						break;
					}
				}
				if (System.currentTimeMillis() - c.alchDelay > 2000) {
					/*for (int[] items : EquipmentSet.GROUP_IRON_MAN_ARMOUR.getEquipment()) {
						if (Misc.linearSearch(items, itemId) > -1) {
							c.sendMessage("You cannot alch iron man amour.");
							break;
						}
					}
					if (System.currentTimeMillis() - c.alchDelay > 2000) {
						for (int[] items : EquipmentSet.HC_IRON_MAN_ARMOUR.getEquipment()) {
							if (Misc.linearSearch(items, itemId) > -1) {
								c.sendMessage("You cannot alch iron man amour.");
								break;
							}
						}*/
					for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
						if (Misc.linearSearch(items, itemId) > -1) {
							c.sendMessage("You cannot alch ultimate iron man amour.");
							break;
						}
					}
					if (LootingBag.isLootingBag(c, itemId) || itemId == RunePouch.RUNE_POUCH_ID) {
					c.sendMessage("This kind of sorcery cannot happen.");
					break;
				}
					if (!c.getCombat().checkMagicReqs(50)) {
						break;
					}
					if (!c.getItems().playerHasItem(itemId, 1) || itemId == 995) {
						break;
					}

					try {
						//PlayerLogging.write(LogType.ALCH, c, "High alch item: " + itemId);
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					c.getItems().deleteItem(itemId, 1);
					c.getItems().addItem(995, (int) (ShopAssistant.getItemShopValue(itemId) * .75));
					c.startAnimation(MagicData.MAGIC_SPELLS[50][2]);
					c.gfx100(MagicData.MAGIC_SPELLS[50][3]);
					c.alchDelay = System.currentTimeMillis();
					c.droppedItem = -1;
					c.droppingItem = false;
					sendFrame106(6);
					addSkillXP(MagicData.MAGIC_SPELLS[50][7], 6);
					refreshSkill(6);
				}
				break;
			}
		}
	}
	/**
	 * Magic on items
	 **/

	public void magicOnItems(int slot, int itemId, int spellId) {
		if (!c.getItems().playerHasItem(itemId, 1, slot) && c.PouchRune1 != itemId && c.PouchRune2 != itemId && c.PouchRune3 != itemId) {
			return;
		}

		switch (spellId) {

		case 1173:
			NonCombatSpells.superHeatItem(c, itemId);
			break;

		case 1162: // low alch
			c.getPA().sendSound(Sound.SOUND_LIST.LOW_ALCHEMY.getSound(), 0, c.EffectVolume);
			alchemy(itemId, "low");
			break;

		case 1178: // high alch
			c.droppedItem = itemId;
			c.droppingItem = false;
			if (c.showDropWarning()) {
				c.getDH().destroyInterface("alch");
				return;
			}
			c.getPA().sendSound(Sound.SOUND_LIST.HIGH_ALCHEMY.getSound(), 0, c.EffectVolume);
			alchemy(itemId, "high");
			break;

		case 1155: // Lvl-1 enchant sapphire
		case 1165: // Lvl-2 enchant emerald
		case 1176: // Lvl-3 enchant ruby
		case 1180: // Lvl-4 enchant diamond
		case 1187: // Lvl-5 enchant dragonstone
		case 6003: // Lvl-6 enchant onyx
		case 23649: //Lvl-7 enchant zenyte
			//Enchantment.getSingleton().enchantItem(c, itemId, spellId);
			//enchantBolt(spellId, itemId, 28);
			break;
		}
	}

	/**
	 * Dieing
	 **/

	public void applyDead() {
		if(c.getInstance() != null && c.getInstance().onDeath(c)) {
			return;
		}
		c.getPA().requestUpdates();
		c.respawnTimer = 15;
		c.isDead = false;

		c.graceSum = 0;
		c.freezeTimer = 1;
		c.recoilHits = 0;
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession)
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			duelSession = null;
		}
		c.faceUpdate(0);
		c.npcIndex = 0;
		c.playerIndex = 0;
		c.stopMovement();
		if (c.duelStatus <= 4) {
			c.sendMessage("Oh dear you are dead!");
			c.getPA().sendSound(Sound.SOUND_LIST.DEATH.getSound(), 0, 8);
		} else if (c.duelStatus != 6) {
			c.sendMessage("You have lost the duel!");
		}
		//resetDamageDone();
		c.specAmount = 10;
		c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
		c.lastVeng = 0;
		c.vengOn = false;
		resetFollowers();
		c.attackTimer = 10;
		removeAllWindows();
		c.tradeResetNeeded = true;
	}

	public void vengMe() {
		if (System.currentTimeMillis() - c.lastVeng > 30000) {
			if (c.getItems().playerHasItem(557, 10)
					&& c.getItems().playerHasItem(9075, 4)
					&& c.getItems().playerHasItem(560, 2)) {
				c.vengOn = true;
				c.lastVeng = System.currentTimeMillis();

				c.getPA().sendGameTimer(ClientGameTimer.VENGEANCE, TimeUnit.MINUTES, 0.5);
				c.startAnimation(4410);
				c.gfx100(726);
				c.getItems().deleteItem(557, c.getItems().getItemSlot(557), 10);
				c.getItems().deleteItem(560, c.getItems().getItemSlot(560), 2);
				c.getItems()
				.deleteItem(9075, c.getItems().getItemSlot(9075), 4);
			} else {
				c.sendMessage("You do not have the required runes to cast this spell. (9075 for astrals)");
			}
		} else {
			c.sendMessage("You must wait 30 seconds before casting this again.");
		}
	}

	public void resetTb() {
		c.teleBlockLength = 0;
		c.teleBlockDelay = 0;
	}

	private void resetFollowers() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].followId == c.getIndex()) {
					Player c = PlayerHandler.players[j];
					c.getPA().resetFollow();
				}
			}
		}
	}

	public void giveLife() {
		// --- NIGHTMARE ZONE SAFE DEATH ---
        if (c.inNMZ) {
            c.isDead = false;
            c.faceUpdate(0);
            c.npcIndex = 0;
            c.playerIndex = 0;
            c.stopMovement();

    		for (int i = 0; i < 23; i++) {
    			c.playerLevel[i] = c.getSkills().getActualLevel(Skill.forId(i));
    			
    			//c.getPA().refreshSkill(i);
    			c.getSkills().sendRefresh();
    		}
    		updateLife();
            
            // Wipe the poison/venom if they have it
            c.poisonDamage = 0;
            c.venomDamage = 0;
            
            // Trigger the leave method we wrote earlier
            NightmareZone.leaveDream(c);
            c.sendMessage("You wake up from the nightmare...");
            
            return; // CRITICAL: This stops the rest of the death method so items don't drop!
        }
		c.isDead = false;
		c.faceUpdate(-1);
		c.freezeTimer = 1;
		if (c.duelStatus <= 4 && !c.getPA().inPitsWait()) {
			if (!c.inPits && !c.inFightCaves()) {
				c.getItems().resetKeepItems();
				if (c.playerRights != 2 || Config.ADMIN_DROP_ITEMS) {
					if (!c.isSkulled) { // what items to keep
						c.getItems().keepItem(0, true);
						c.getItems().keepItem(1, true);
						c.getItems().keepItem(2, true);
					}
					if (c.prayerActive[10]
							&& System.currentTimeMillis() - c.lastProtItem > 700) {
						c.getItems().keepItem(3, true);
					}
					if(Boundary.isIn(c, Boundary.ZULRAH)) {
						c.deathItems.clear();
		                ItemsKeptOnDeath.handleDeathStorage(c);
					} else {
						c.getItems().dropAllItems(); // drop all items
					}
					c.getItems().deleteAllItems(); // delete all items

					if (!c.isSkulled) { // add the kept items once we finish
						// deleting and dropping them
						for (int i1 = 0; i1 < 3; i1++) {
							if (c.itemKeptId[i1] > 0) {
								c.getItems().addItem(c.itemKeptId[i1], 1);
							}
						}
					}
					if (c.prayerActive[10]) { // if we have protect items
						if (c.itemKeptId[3] > 0) {
							c.getItems().addItem(c.itemKeptId[3], 1);
						}
					}
				}
				c.getItems().resetKeepItems();
			} else if (c.inPits) {
				//Server.fightPits.removePlayerFromPits(c.playerId);
				c.pitsStatus = 1;
			}
		}
		c.getCombat().resetPrayers();
		for (int i = 0; i < 23; i++) {
			c.playerLevel[i] = c.getSkills().getActualLevel(Skill.forId(i));
			
			//c.getPA().refreshSkill(i);
			c.getSkills().sendRefresh();
		}
		if (c.pitsStatus == 1) {
			movePlayer(2399, 5173, 0);
		} else if (c.duelStatus <= 4) { // if we are not in a duel repawn to
			// wildy
			if(c.respawnLocation == 0)
				movePlayer(Config.RESPAWN_X, Config.RESPAWN_Y, 0);
			else if(c.respawnLocation == 1)
				movePlayer(Config.FALLY_RESPAWN_X, Config.FALLY_RESPAWN_Y, 0);
			else if(c.respawnLocation == 2)
				movePlayer(Config.EDGE_RESPAWN_X, Config.EDGE_RESPAWN_Y, 0);
			c.isSkulled = false;
			c.skullTimer = 0;
			c.attackedPlayers.clear();
		} else if (c.inFightCaves()) {
			c.getPA().resetTzhaar();
		} else { // we are in a duel, respawn outside of arena
			Player o = (Player) PlayerHandler.players[c.duelingWith];
			if (o != null) {
				o.getPA().createPlayerHints(10, -1);
				if (o.duelStatus == 6) {
					//o.getTradeAndDuel().duelVictory();
				}
			}
			movePlayer(
					Config.DUELING_RESPAWN_X
					+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)),
					Config.DUELING_RESPAWN_Y
					+ (Misc.random(Config.RANDOM_DUELING_RESPAWN)), 0);
			if (c.duelStatus != 6) { // if we have won but have died, don't
				// reset the duel status.
				//c.getTradeAndDuel().resetDuel();
			}
		}
		c.getCombat().resetPlayerAttack();
		resetAnimation();
		c.startAnimation(65535);
		updateLife();
		c.damageTaken = new int[Config.MAX_PLAYERS];
		c.getPA().requestUpdates();
		removeAllWindows();
		c.tradeResetNeeded = true;
	}
	private void updateLife() {
		PlayerSave.saveGame(c);
		c.resetDamageTaken();
		c.getCombat().resetPlayerAttack();
		frame1();
		resetTb();
		c.isSkulled = false;
		c.attackedPlayers.clear();
		c.headIconPk = -1;
		c.skullTimer = -1;
		c.getHealth().reset();
		c.getHealth().removeAllStatuses();
		c.getHealth().removeAllImmunities();
		requestUpdates();
		c.tradeResetNeeded = true;
	}
	/**
	 * Location change for digging, levers etc
	 **/

	public void changeLocation() {
		switch (c.newLocation) {
		case 1:
			setMinimapState(2);
			movePlayer(3578, 9706, -1);
			break;
		case 2:
			setMinimapState(2);
			movePlayer(3568, 9683, -1);
			break;
		case 3:
			setMinimapState(2);
			movePlayer(3557, 9703, -1);
			break;
		case 4:
			setMinimapState(2);
			movePlayer(3556, 9718, -1);
			break;
		case 5:
			setMinimapState(2);
			movePlayer(3534, 9704, -1);
			break;
		case 6:
			setMinimapState(2);
			movePlayer(3546, 9684, -1);
			break;
		}
		c.newLocation = 0;
	}


	/**
	 * Teleporting
	 **/
	public void spellTeleport(int x, int y, int height) {
		startTeleport(x, y, height, c.playerMagicBook == 1 ? "ancient" : "modern");
	}
	public static Location findNearestWalkableTile(int x, int y, int height, int size) {
		final int maxRadius = 5; // You can increase this if needed

		for (int radius = 0; radius <= maxRadius; radius++) {
			for (int dx = -radius; dx <= radius; dx++) {
				for (int dy = -radius; dy <= radius; dy++) {
					int newX = x + dx;
					int newY = y + dy;
					if (Region.canMove(newX, newY, newX, newY, height, size, size)) {
						return new Location(newX, newY, height);
					}
				}
			}
		}
		// If no walkable tile found, return the original anyway
		return new Location(x, y, height);
	}

	public void startTeleport(int x, int y, int height, String teleportType) {

		if (c.isDead) {
			return;
		}
		if(c.isInCoXRaid())
			c.setRaidSession(null);
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}
		if (c.inWild() && c.wildLevel > Config.NO_TELEPORT_WILD_LEVEL) {
			c.sendMessage("You can't teleport above level "
					+ Config.NO_TELEPORT_WILD_LEVEL + " in the wilderness.");
			return;
		}
		if (c.tutorialProgress < 36) {
			c.sendMessage("You can't teleport from tutorial island!");
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			
			removeAllWindows();

			// ==========================================
			// PIRATE'S TREASURE: RUM BREAKING LOGIC
			// ==========================================
			if (c.getItems().playerHasItem(server.model.players.quests.PiratesTreasure.KARAMJAN_RUM)) {
				int rumAmount = c.getItems().getItemAmount(server.model.players.quests.PiratesTreasure.KARAMJAN_RUM);
				for (int i = 0; i < rumAmount; i++) {
					c.getItems().deleteItem(server.model.players.quests.PiratesTreasure.KARAMJAN_RUM, 1);
				}
				c.sendMessage("The sudden jolt of the teleport shatters your Karamjan rum!");
			}

			Location safeLocation = findNearestWalkableTile(x, y, height, 1);
			c.teleX = safeLocation.getX();
			c.teleY = safeLocation.getY();
			if (safeLocation.getX() != x || safeLocation.getY() != y) {
				c.sendMessage("Your teleport destination was adjusted to a nearby safe tile.");
			}

			c.teleHeight = safeLocation.getZ();

			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			
			if (teleportType.equalsIgnoreCase("modern") || teleportType.equals("glory")) {
				sendSound(Sound.SOUND_LIST.TELEPORT.getSound(), 5, c.EffectVolume);
				c.startAnimation(714);
				c.teleTimer = 11;
				c.teleGfx = 308;
				c.teleEndGfx = 1299;
				c.teleEndAnimation = 715;
			} else if (teleportType.equalsIgnoreCase("ancient")) {
				sendSound(Sound.SOUND_LIST.TELEPORT.getSound(), 5, c.EffectVolume);
				c.startAnimation(1979);
				c.teleGfx = 212;
				c.teleTimer = 9;
				c.teleEndAnimation = 0;
				c.gfx0(392);
			} else if (teleportType.equalsIgnoreCase("teletab")) { 
				c.startAnimation(4731);
				c.gfx0(678);
				c.teleTimer = 9;
				c.teleEndAnimation = 0;
				
			// ==========================================
			// HOME TELEPORT SEQUENCE (CYCLE EVENT)
			// ==========================================
			} else if (teleportType.equalsIgnoreCase("home")) {
				c.teleTimer = 999; 
				
				// Step 1: Start
				c.startAnimation(4847); // Animation: Draws circle
				c.gfx0(800);            // GFX: Start drawing circle

				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					int time = 0;

					@Override
					public void execute(CycleEventContainer container) {
						// --- THE INTERRUPT ---
						if (c == null || c.isDead) {
                            assert c != null;
                            c.sendMessage("Your teleport was interrupted!");
							c.startAnimation(4852); // Stand up cancel
							c.gfx0(-1);             // Clear the active teleport GFX
							container.stop();
							return;
						}
						// --- THE SEQUENCE (1-tick cycle) ---
						if (time == 5) { 
							c.startAnimation(4850); // Step 2: Kneels and continues drawing
							c.gfx0(801);            // GFX: Circle (This persists through the next steps)
						} else if (time == 9) { 
							c.startAnimation(4851); // Step 3: Finishes drawing circle
							c.gfx0(801);            // GFX: Circle (This persists through the next steps)
						} else if (time == 12) { 
							c.startAnimation(4853); // Step 4: Takes out the book
							c.gfx0(802);            // GFX: Takes out book
						} else if (time == 16) { 
							c.startAnimation(4855); // Step 5: Sitting cross-legged reading book
							c.gfx0(803);            // GFX: Book & circle visible, starting to tele
						} else if (time == 21) { 
							c.startAnimation(4857); // Step 6: Starting to disappear
							c.gfx0(804);            // GFX: Teleport effect right before moving
						} else if (time == 25) { 
							// --- TELEPORT ---
							// We gave 804 exactly 4 ticks (2.4s) to suck the player into the portal!
							c.getPA().movePlayer(c.teleX, c.teleY, c.teleHeight);
							
							// Step 7: Landing & standing up
							c.startAnimation(4852); 
						} else if (time == 28) { 
							container.stop();
						}
						time++;
					}

					@Override
					public void stop() {
						c.teleTimer = 0; 
						c.startAnimation(65535); // Clear the animation block
					}
				}, 1); // 1-tick cycle
			} else if (teleportType.equals("obelisk")) {
				c.startAnimation(1816);
				c.teleTimer = 11;
				c.teleGfx = 661;
				c.teleEndAnimation = 65535;
			} else if (teleportType.equals("puropuro")) {
				c.startAnimation(6724);
				c.gfx0(1118);
				c.teleTimer = 13;
				c.teleEndAnimation = 65535;
			} else if (teleportType.equals("interface")) {
				c.startAnimation(6724);
				c.gfx0(1424);
				c.teleTimer = 13;
				c.teleEndAnimation = 65535;			
			} else if (teleportType.equalsIgnoreCase("Dorgesh")) {
				c.getPA().sendSound(202, 0, 8);
				c.teleGfx = 0;
				c.gfx0(1034);
				c.teleTimer = 15;
				c.teleEndAnimation = 0;
			} else if (teleportType.equalsIgnoreCase("fairy")) {
				c.startAnimation(3265);
				c.teleGfx = 0;
				c.gfx0(569);
				c.teleTimer = 9;
				c.teleEndAnimation = 0;
			} else if (teleportType.equalsIgnoreCase("ArdougneCloak")) {
				sendSound(Sound.SOUND_LIST.TELEPORT.getSound(), 5, c.EffectVolume);
				c.startAnimation(714);
				c.teleTimer = 11;
				c.teleGfx = 308;
				c.teleEndAnimation = 0;
			} else if (teleportType.equalsIgnoreCase("lunar")) {
				sendSound(Sound.SOUND_LIST.TELEPORT.getSound(), 5, c.EffectVolume);
				c.startAnimation(1816);
				c.teleTimer = 11;
				c.teleGfx = 747;
				c.teleEndAnimation = 715;
			} else if (teleportType.equalsIgnoreCase("scroll")) {
				c.startAnimation(3864);
				c.teleTimer = 11;
				c.teleGfx = 1039;
				c.teleEndAnimation = 0;
			} else if (teleportType.equalsIgnoreCase("lever")) {
				c.startAnimation(2140);
				c.teleTimer = 11;
				c.teleGfx = 0; 
				c.teleEndAnimation = 0;
			} 
		}
	}
	public void startLeverTeleport(int x, int y, int height) {
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (Boundary.isIn(c, Boundary.ICE_PATH) || Boundary.isIn(c, Boundary.ICE_PATH_TOP) && c.getHeight() > 0) {
			c.sendMessage("The cold from the ice-path is preventing you from teleporting.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (Boundary.isIn(c, Boundary.RAIDS)) {
			c.getPotions().resetOverload();
		}
		if (c.getSlayer().superiorSpawned) {
			c.getSlayer().superiorSpawned = false;
		}
		if (!c.isDead && c.teleTimer == 0 && c.respawnTimer == -6) {
			if (c.playerIndex > 0 || c.npcIndex > 0)
				c.getCombat().resetPlayerAttack();
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.startAnimation(2140);
			c.teleTimer = 8;
			c.setGlodDamageCounter(0);
			c.setIceQueenDamageCounter(0);
			c.setSkeletalMysticDamageCounter(0);
			c.sendMessage("You pull the lever..");
		}
		//c.getSkilling().stop();
		if (World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			World.getWorld().getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
		}
		if (c.getSkotizo() != null) {
			InstancedAreaManager.getSingleton().disposeOf(c.getSkotizo());
		}
	}
	public void startTeleport2(int x, int y, int height) {
		if (c.duelStatus == 5) {
			c.sendMessage("You can't teleport during a duel!");
			return;
		}

		if (c.isDead) {
			return;
		}
		if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
			c.sendMessage("You are teleblocked and can't teleport.");
			return;
		}
		if (c.tutorialProgress < 36) {
			c.sendMessage(
					"You can't teleport from tutorial island!");
			return;
		}
		if (!c.isDead && c.teleTimer == 0) {
			c.stopMovement();
			removeAllWindows();
			c.teleX = x;
			c.teleY = y;
			c.npcIndex = 0;
			c.playerIndex = 0;
			c.faceUpdate(0);
			c.teleHeight = height;
			c.startAnimation(714);
			c.teleTimer = 9;
			c.teleGfx = 308;
			c.teleEndAnimation = 715;

		}
	}

	public void processTeleport() {
		if (c.isDead) {
			return;
		}
		c.setX(c.teleX);
		c.setY(c.teleY);
		c.setHeight(c.teleHeight);
		c.setNeedsPlacement(true);
		if (c.teleEndAnimation > 0) {
			c.startAnimation(c.teleEndAnimation);
		}
		if(c.teleEndGfx > 0) {
			c.gfx100(c.teleEndGfx);
		}
	}

	public void movePlayerUnconditionally(int x, int y, int h) {
		c.resetWalkingQueue();
		c.setX(x);
		c.setY(y);
		c.setHeight(h);
		c.setNeedsPlacement(true);
		c.teleTimer = 4;
		requestUpdates();
		c.lastMove = System.currentTimeMillis();
	}
	/*public void movePlayer(int x, int y, int h) {
	    if (c.inDuelArena() || World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
	        if (!c.isDead) {
	            return;
	        }
	    }

	    c.stopMovement();
	    c.getCombat().resetPlayerAttack();

	    c.setX(x);
	    c.setY(y);
	    c.setHeight(h);
	    c.setNeedsPlacement(true);
	    c.getMovementQueue().handleRegionChange();
	    //c.HeightLevel = h;
	    c.teleTimer = 2;
	    requestUpdates();
	}*/
	public void movePlayer(int x, int y, int h) {
		/*if (c.inDuelArena() || World.getWorld().getMultiplayerSessionListener().inAnySession(c)) {
			if (!c.isDead) {
				return;
			}
		}*/
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (System.currentTimeMillis() - c.lastSpear < 3000) {
			c.sendMessage("You're trying to move too fast.");
			return;
		}
		/*if (c.morphed) {
			c.sendMessage("You cannot do this now.");
			return;
		}*/
		if (c.getSlayer().superiorSpawned) {
			c.getSlayer().superiorSpawned = false;
		}
		
		c.stopMovement();
		c.getCombat().resetPlayerAttack();
		c.setX(x);
		c.setY(y);	
		c.setHeight(h);
		c.setNeedsPlacement(true);
		
		c.teleTimer = 2;
		requestUpdates();
	}
	public void movePlayer(int x, int y) {
		movePlayer(x, y, c.getHeight());
	}
	/**
	 * Following
	 **/

	public void followPlayer() {
		if (World.getWorld().getPlayerHandler().players[c.followId] == null
				|| World.getWorld().getPlayerHandler().players[c.followId].isDead) {
			c.followId = 0;
			return;
		}
		if (c.freezeTimer > 0) {
			return;
		}
		if (c.isDead || c.playerLevel[3] <= 0)
			return;

		int otherX = World.getWorld().getPlayerHandler().players[c.followId].getX();
		int otherY = World.getWorld().getPlayerHandler().players[c.followId].getY();
		boolean withinDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 2);
		c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 1);
		boolean hallyDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 2);
		boolean bowDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 8);
		boolean rangeWeaponDistance = c.goodDistance(otherX, otherY, c.getX(),
				c.getY(), 4);
		boolean sameSpot = c.getX() == otherX && c.getY() == otherY;
		if (!c.goodDistance(otherX, otherY, c.getX(), c.getY(), 25)) {
			c.followId = 0;
			return;
		}
		/*if (c.goodDistance(otherX, otherY, c.getX(), c.getY(), 1)) {
			if (otherX != c.getX() && otherY != c.getY()) {
				stopDiagonal(otherX, otherY);
				return;
			}
		}*/

		if ((c.usingBow || c.mageFollow || (c.playerIndex > 0 && c.autocastId > 0))
				&& bowDistance && !sameSpot) {
			return;
		}

		if (c.getCombat().usingHally() && hallyDistance && !sameSpot) {
			return;
		}

		if (c.usingRangeWeapon && rangeWeaponDistance && !sameSpot) {
			return;
		}

		c.faceUpdate(c.followId + 32768);
		if (otherX == c.getX() && otherY == c.getY()) {
			int r = Misc.random(3);
			switch (r) {
			case 0:
				walkTo(0, -1);
				break;
			case 1:
				walkTo(0, 1);
				break;
			case 2:
				walkTo(1, 0);
				break;
			case 3:
				walkTo(-1, 0);
				break;
			}
		} else if (c.isRunning2 && !withinDistance) {
			if (otherY > c.getY() && otherX == c.getX()) {
				walkTo(0,
						getMove(c.getY(), otherY - 1)
						+ getMove(c.getY(), otherY - 1));
			} else if (otherY < c.getY() && otherX == c.getX()) {
				walkTo(0,
						getMove(c.getY(), otherY + 1)
						+ getMove(c.getY(), otherY + 1));
			} else if (otherX > c.getX() && otherY == c.getY()) {
				walkTo(getMove(c.getX(), otherX - 1)
						+ getMove(c.getX(), otherX - 1), 0);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1)
						+ getMove(c.getX(), otherX + 1), 0);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1)
						+ getMove(c.getX(), otherX + 1),
						getMove(c.getY(), otherY + 1)
						+ getMove(c.getY(), otherY + 1));
			} else if (otherX > c.getX() && otherY > c.getY()) {
				walkTo(getMove(c.getX(), otherX - 1)
						+ getMove(c.getX(), otherX - 1),
						getMove(c.getY(), otherY - 1)
						+ getMove(c.getY(), otherY - 1));
			} else if (otherX < c.getX() && otherY > c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1)
						+ getMove(c.getX(), otherX + 1),
						getMove(c.getY(), otherY - 1)
						+ getMove(c.getY(), otherY - 1));
			} else if (otherX > c.getX() && otherY < c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1)
						+ getMove(c.getX(), otherX + 1),
						getMove(c.getY(), otherY - 1)
						+ getMove(c.getY(), otherY - 1));
			}
		} else {
			if (otherY > c.getY() && otherX == c.getX()) {
				walkTo(0, getMove(c.getY(), otherY - 1));
			} else if (otherY < c.getY() && otherX == c.getX()) {
				walkTo(0, getMove(c.getY(), otherY + 1));
			} else if (otherX > c.getX() && otherY == c.getY()) {
				walkTo(getMove(c.getX(), otherX - 1), 0);
			} else if (otherX < c.getX() && otherY == c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1), 0);
			} else if (otherX < c.getX() && otherY < c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1),
						getMove(c.getY(), otherY + 1));
			} else if (otherX > c.getX() && otherY > c.getY()) {
				walkTo(getMove(c.getX(), otherX - 1),
						getMove(c.getY(), otherY - 1));
			} else if (otherX < c.getX() && otherY > c.getY()) {
				walkTo(getMove(c.getX(), otherX + 1),
						getMove(c.getY(), otherY - 1));
			} else if (otherX > c.getX() && otherY < c.getY()) {
				walkTo(getMove(c.getX(), otherX - 1),
						getMove(c.getY(), otherY + 1));
			}
		}
		c.faceUpdate(c.followId + 32768);
	}
	/*OBJECT TYPES:

0	- straight walls, fences etc
1	- diagonal walls corner, fences etc connectors
2	- entire walls, fences etc corners
3	- straight wall corners, fences etc connectors
4	- straight inside wall decoration
5	- straight outside wall decoration
6	- diagonal outside wall decoration
7	- diagonal inside wall decoration
8	- diagonal in wall decoration
9	- diagonal walls, fences etc
10	- all kinds of objects, trees, statues, signs, fountains etc etc
11	- ground objects like daisies etc
12	- straight sloped roofs
13	- diagonal sloped roofs
14	- diagonal slope connecting roofs
15	- straight sloped corner connecting roofs
16	- straight sloped corner roof
17	- straight flat top roofs
18	- straight bottom egde roofs
19	- diagonal bottom edge connecting roofs
20	- straight bottom edge connecting roofs
21	- straight bottom edge connecting corner roofs
22	- ground decoration + map signs (quests, water fountains, shops etc)*/
	public void openTrapdoor(int oldtrapdoorid, int newtrapdoorid, int type, int face){
		new Object(newtrapdoorid, c.objectX, c.objectY, 0, face, type, oldtrapdoorid, 100);
	}	
	public void PassGate(int oldGateId, int newGateId, int type, int face, int objX, int objY){
		new Object(newGateId, objX, objY, 0, face, type, oldGateId, 5);
	}
	public void followNpc() {
		if (NPCHandler.npcs[c.followId2] == null || NPCHandler.npcs[c.followId2].isDead) {
			c.followId2 = 0;
			return;
		}
		if (c.freezeTimer > 0) {
			return;
		}
		if (c.isDead || c.getSkills().getLevel(Skill.HITPOINTS) <= 0)
			return;
		NPC npc = NPCHandler.npcs[c.followId2];
		c.getMovementQueue().follow(npc);

	}
	public void sendEntityTarget(int state, Entity entity) {
		/*if (c.disconnected || c.getOutStream() == null) {
			return;
		}
		c.outStream.createFrameVarSize(222);
		c.outStream.writeByte(state);
		System.out.println("state :"+state);
		/*if (state != 0) {
			stream.writeWord(entity.getIndex());
			stream.writeWord(entity.getHealth().getAmount());
			stream.writeWord(entity.getHealth().getMaximum());
		}
		c.outStream.endFrameVarSize();*/
	}
	public void createPlayersProjectile(int x, int y, int offX, int offY, int angle, int speed, int gfxMoving,
			int startHeight, int endHeight, int lockon, int time, int delay) {
		if (delay <= 0) {
			createPlayersProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon, time);
		} else {
			World.getWorld().getEventHandler().submit(new DelayEvent(delay) {
				@Override
				public void onExecute() {
					createPlayersProjectile(x, y, offX, offY, angle, speed, gfxMoving, startHeight, endHeight, lockon,
							time);
				}
			});
		}
	}

	public void playerWalk(int x, int y) {
		Location location = Location.of(x, y);//PathGenerator.getBasicPath(c, Location.of(x, y));
		RS317PathFinder.findPath(this.c, location.getX(), location.getY(), false, 16, 16);
		//		PathFinder.getPathFinder().findRoute(c, x, y, true, 1, 1);
	}
	public int getRunningMove(int i, int j) {
		if (j - i > 2)
			return 2;
		else if (j - i < -2)
			return -2;
		else
			return j - i;
	}

	public void resetFollow() {
		c.followId = 0;
		c.followId2 = 0;
		c.mageFollow = false;
		c.underAttackBy = 0;
		c.underAttackBy2 = 0;
		c.getMovementQueue().resetFollowing();
	}
	public void LoginInfo(int DaysSinceRecovChange, int unreadMessages, int members, int i, int daysSinceLastLogin, int EmailRegistered) {
		c.outStream.createFrame(176);
		c.outStream.writeByteC(DaysSinceRecovChange);
		c.outStream.writeWordBigEndian(unreadMessages);
		c.outStream.writeByte(members);
		c.outStream.writeDWord(i);
		c.outStream.writeWord(daysSinceLastLogin);
		c.outStream.writeWord(EmailRegistered);
	}
	public void sendRuneTypes(int runeType1, int runeType2, int runeType3, int runeAMT1, int runeAMT2, int runeAMT3) {

		c.outStream.createFrame(172);
		c.outStream.writeWord(runeType1);
		c.outStream.writeWord(runeType2);
		c.outStream.writeWord(runeType3);
		c.outStream.writeWord(runeAMT1);
		c.outStream.writeWord(runeAMT2);
		c.outStream.writeWord(runeAMT3);
	}
	public void sendAchieveProgress(int area, int tier, int updated) {
		c.outStream.createFrame(173);

		// Write the area and tier
		c.outStream.writeByte(area);
		c.outStream.writeByte(tier);

		// Write the updated status
		c.outStream.writeByte(updated);
	}


	public void walkTo(int i, int j) {
		c.getMovementQueue().walkStep(i, j);
	}

	public void walkTo2(int i, int j) {
		c.getMovementQueue().walkStep(i, j);
	}
	public void walkTo3(int i, int j) {
		c.newWalkCmdSteps = 0;
		if (++c.newWalkCmdSteps > 50)
			c.newWalkCmdSteps = 0;
		int k = c.getX() + i;
		k -= c.mapRegionX * 8;
		c.getNewWalkCmdX()[0] = c.getNewWalkCmdY()[0] = 0;
		int l = c.getY() + j;
		l -= c.mapRegionY * 8;

		for (int n = 0; n < c.newWalkCmdSteps; n++) {
			c.getNewWalkCmdX()[n] += k;
			c.getNewWalkCmdY()[n] += l;
		}
		c.getMovementQueue().walkStep(i, j);
	}

	public void stopDiagonal(int otherX, int otherY) {

	}

	public void walkToCheck(int i, int j) {
		c.getMovementQueue().walkStep(i, j);
	}

	public int getMove(int place1, int place2) {
		if (System.currentTimeMillis() - c.lastSpear < 4000)
			return 0;
		if ((place1 - place2) == 0) {
			return 0;
		} else if ((place1 - place2) < 0) {
			return 1;
		} else if ((place1 - place2) > 0) {
			return -1;
		}
		return 0;
	}

	public boolean fullVeracs() {
		return c.playerEquipment[c.playerHat] == 4753
				&& c.playerEquipment[c.playerChest] == 4757
				&& c.playerEquipment[c.playerLegs] == 4759
				&& c.playerEquipment[c.playerWeapon] == 4755;
	}

	public boolean fullGuthans() {
		return c.playerEquipment[c.playerHat] == 4724
				&& c.playerEquipment[c.playerChest] == 4728
				&& c.playerEquipment[c.playerLegs] == 4730
				&& c.playerEquipment[c.playerWeapon] == 4726;
	}
	public void chatbox(int i1) {
		if (c.getOutStream() != null && c != null) {
			c.outStream.createFrame(218);
			c.outStream.writeWordBigEndianA(i1);
			c.updateRequired = true;
			c.setAppearanceUpdateRequired(true);
		}
	}
	/**
	 * reseting animation
	 **/
	public void resetAnimation() {
		c.getCombat().getPlayerAnimIndex(
				ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
		c.startAnimation(c.playerStandIndex);
		requestUpdates();
	}
	public void setAnimationBack() {
		c.isRunning = true;
		//sendFrame36(173, c.isRunning() ? 1 : 0);
		c.getCombat().getPlayerAnimIndex(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
		requestUpdates();
	}
	public void requestUpdates() {
		c.updateRequired = true;
		c.setAppearanceUpdateRequired(true);
	}

	public void handleAlt(int id) {
		if (!c.getItems().playerHasItem(id)) {
			c.getItems().addItem(id, 1);
		}
	}
	public void firstTimeTutorial() {
		if (Config.TUTORIAL_ISLAND && c.tutorialProgress == 0) {
			c.getItems().deleteAllItems();
			c.getPA().hideAllSideBars();
			c.getPA().movePlayer(3094, 3107, 0);
			//c.getDH().sendDialogues(3000, -1);
			c.tutorialProgress = 0;
			c.isRunning2 = false;
			c.autoRet = 1;
			sendAutoRetalitate();
			ChangeAppearance();
			//LightSources.saveBrightness(player);
		} else if (c.tutorialProgress == 0 && !Config.TUTORIAL_ISLAND) {
			c.sendSidebars();
			c.getPA().removeHintIcon(c);
			c.getPA().walkableInterface(-1);
			c.getPA().chatbox(-1);
			c.getItems().deleteAllItems();
			c.tutorialProgress = 36;
			//c.getItems().clearBank();
			addStarter();
			c.getPA().movePlayer(3233, 3229, 0);
			c.sendMessage("Welcome to @blu@" + Config.SERVER_NAME + "@bla@ - we are currently in Server Stage v@blu@" + Config.CLIENT_VERSION + "@bla@.");
			//c.getDH().sendDialogues(3115, 2224);
			c.isRunning2 = false;
			c.autoRet = 1;
			c.EffectVolume = 1;
			c.AreaVolume = 1;
			c.MusicVolume = 1;
			sendAutoRetalitate();
			ChangeAppearance();
			//LightSources.saveBrightness(player);
			//if (!player.hasBankpin) {
			c.sendMessage("You do not, have a bank pin it is highly recommended you set one.");
			//}
		}
	}		
	public void removeHintIcon(Player c2) {
		c2.getPA().drawHeadicon(0, 0, 0, 0);
	}
	public void sendAutoRetalitate() {
		c.getPA().sendConfig(172, c.autoRet == 1 ? 1 : 0);
	}
	public void sendConfigStates() {
		int state = 0;
		if(c.getPOHDoor() == 3)
			state = 1;
		else if(c.getPOHDoor() == 2)
			state = 0;
		else if(c.getPOHDoor() == 1)
			state = 2;
		c.getPA().sendConfig(455, c.teleportInsidePOH ? 1 : 0);
		c.getPA().sendConfig(456, c.defaultBuildMode ? 1 : 0);
		c.getPA().sendConfig(457, state);
		c.getPA().sendConfig(270, 0);
		c.getPA().sendConfig(173, c.isRunning ? 1 : 0);
		c.getPA().sendConfig(427, c.acceptAid ? 1 : 0);
		for(int i = 0; i < c.emoteUnlock.length; i++) {
			c.getPA().sendConfig(700 + i, c.emoteUnlock[i] ? 1 : 0);
		}
		c.getPA().sendConfig(707, 1);
	}
	public void hideAllSideBars() {
		for (int i = 0; i < 17; i++) {
			c.setSidebarInterface(i, -1);
		}
		c.setSidebarInterface(10, 2449);
	}
	DecimalFormat formatter = new DecimalFormat("#,###");
	public void levelUp(int skill) {

		int totalLevel = c.getSkills().getTotalLevel();
		//int p = c.playerXP[0] + c.playerXP[1] + c.playerXP[2] + c.playerXP[3] + c.playerXP[4] + c.playerXP[5] + c.playerXP[6] + c.playerXP[7] + 
				//c.playerXP[8] + c.playerXP[9] + c.playerXP[10] + c.playerXP[11] + c.playerXP[12] + c.playerXP[13] + c.playerXP[14] + c.playerXP[15] + 
				//c.playerXP[16] + c.playerXP[17] + c.playerXP[18] + c.playerXP[19] + c.playerXP[20] + c.playerXP[21] + c.playerXP[22];
		c.totalEXP = c.getSkills().getTotalExperience();
		// Create a DecimalFormat instance with comma formatting

		// Format the totalEXP with commas
		String formattedTotalEXP = formatter.format(c.totalEXP);
		sendFrame126(formattedTotalEXP, 55799);
		sendFrame126("" + c.combatLevel, 55804);
		sendFrame126(Integer.toString(totalLevel), 55806);//c.combatLevel
		sendFrame126("Total level:\\n " + totalLevel, 27127);

		switch (skill) {
		case 0:
			sendFrame126("Congratulations! You've just advanced a Attack level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.ATTACK)+"!", 4269);
			c.sendMessage("Congratulations! You've just advanced a attack level.");	
			showChatboxInterface(6247);
			break;

		case 1:
			sendFrame126("Congratulations! You've just advanced a Defence level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.DEFENCE)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Defence level.");
			showChatboxInterface(6253);
			break;

		case 2:
			sendFrame126("Congratulations! You've just advanced a Strength level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.STRENGTH)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Strength level.");
			showChatboxInterface(6206);
			break;

		case 3:
			sendFrame126("Congratulations! You've just advanced a Hitpoints level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.HITPOINTS)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Hitpoints level.");
			showChatboxInterface(6216);
			break;

		case 4:
			sendFrame126("Congratulations! You've just advanced a Ranged level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.RANGED)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Ranging level.");
			showChatboxInterface(4443);
			break;

		case 5:
			sendFrame126("Congratulations! You've just advanced a Prayer level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.PRAYER)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Prayer level.");
			showChatboxInterface(6242);
			sendFrame126("" +c.playerLevel[5]+"/"+getLevelForXP(c.playerXP[5])+"", 687);//Prayer frame
			break;

		case 6:
			sendFrame126("Congratulations! You've just advanced a Magic level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.MAGIC)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Magic level.");
			showChatboxInterface(6211);
			break;

		case 7:
			sendFrame126("Congratulations! You've just advanced a Cooking level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.COOKING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Cooking level.");
			showChatboxInterface(6226);
			break;

		case 8:
			sendFrame126("Congratulations! You've just advanced a Woodcutting level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.WOODCUTTING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Woodcutting level.");
			showChatboxInterface(4272);
			break;

		case 9:
			sendFrame126("Congratulations! You've just advanced a Fletching level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.FLETCHING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Fletching level.");
			showChatboxInterface(6231);
			break;

		case 10:
			sendFrame126("Congratulations! You've just advanced a Fishing level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.FISHING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Fishing level.");
			showChatboxInterface(6258);
			break;

		case 11:
			sendFrame126("Congratulations! You've just advanced a Firemaking level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.FIREMAKING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Firemaking level.");
			showChatboxInterface(4282);
			break;

		case 12:
			sendFrame126("Congratulations! You've just advanced a Crafting level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.CRAFTING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Crafting level.");
			showChatboxInterface(6263);
			break;

		case 13:
			sendFrame126("Congratulations! You've just advanced a Smithing level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.SMITHING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Smithing level.");
			showChatboxInterface(6221);
			break;

		case 14:
			sendFrame126("Congratulations! You've just advanced a Mining level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.MINING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Mining level.");
			showChatboxInterface(4416);
			break;

		case 15:
			sendFrame126("Congratulations! You've just advanced a Herblore level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.HERBLORE)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Herblore level.");
			showChatboxInterface(6237);
			break;

		case 16:
			sendFrame126("Congratulations! You've just advanced a Agility level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.AGILITY)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Agility level.");
			showChatboxInterface(4277);
			break;

		case 17:
			sendFrame126("Congratulations! You've just advanced a Thieving level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.THIEVING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Thieving level.");
			showChatboxInterface(4261);
			break;

		case 18:
			sendFrame126("Congratulations! You've just advanced a Slayer level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.SLAYER)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Slayer level.");
			showChatboxInterface(12122);
			break;

		case 19:
			sendFrame126("Congratulations! You've just advanced a Farming level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.FARMING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Farming level.");
			showChatboxInterface(5267);
			break;

		case 20:
			sendFrame126("Congratulations! You've just advanced a Runecrafting level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.RUNECRAFTING)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Runecrafting level.");
			showChatboxInterface(4267);
			break;
		case 21:
			sendFrame126("Congratulations! You've just advanced a Construction level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.CONSTRUCTION)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Construction level.");
			showChatboxInterface(7267);
			break;
		case 22:
			sendFrame126("Congratulations! You've just advanced a Hunter level!", 4268);
			sendFrame126("You have now reached level "+c.getSkills().getActualLevel(Skill.HUNTER)+".", 4269);
			c.sendMessage("Congratulations! You've just advanced a Hunter level.");
			showChatboxInterface(8267);
			break;
		}
		c.dialogueAction = 0;
		c.nextChat = 0;
	}
	/**
	 * Packet 27: Prompts the client to open the "Enter amount:" chatbox interface.
	 */
	public void sendEnterAmount(String message) {
		if (c.getOutStream() != null && c != null) {
			// Optional: Some modern clients let you change the text above the input box
			// c.getPA().sendFrame126(message, 3214);

			c.getOutStream().createFrame(27);
			c.flushOutStream();
		}
	}
	public void resetlogCross(final Player c) {
		c.playerStandIndex = 0x328;
		c.playerTurnIndex = 0x337;
		c.playerWalkIndex = 0x333;
		c.getPA().requestUpdates();
	}
	public void refreshSkill(int i) {

		c.combatLevel = c.calculateCombatLevel();
		int totalLevel = c.getSkills().getTotalLevel();
		c.totalEXP = c.getSkills().getTotalExperience();
		// Create a DecimalFormat instance with comma formatting
		DecimalFormat formatter = new DecimalFormat("#,###");

		// Format the totalEXP with commas
		String formattedTotalEXP = formatter.format(c.totalEXP);
		sendFrame126(formattedTotalEXP, 55799);
		sendFrame126("" + c.combatLevel, 55804);
		sendFrame126(Integer.toString(totalLevel), 55806);//c.combatLevel
		sendFrame126("Total level:\\n " + totalLevel, 27127);

		int firstLine, thirdLine;
		Skill skill = Skill.forId(i);
		switch (i) {
		case 0://attack
			firstLine = 27159;
			thirdLine = 4044;
			requestUpdates();
			break;

		case 1://defense
			firstLine = 27161;
			thirdLine = 4056;
			break;

		case 2://strength
			firstLine = 27163;
			thirdLine = 4050;
			break;

		case 3://hitpoints
			firstLine = 27175;
			thirdLine = 4080;
			break;

		case 4://ranged

			firstLine = 27165;
			thirdLine = 4062;
			break;

		case 5://prayer
			firstLine = 27167;
			thirdLine = 4068;
			break;

		case 6://magic
			firstLine = 27171;
			thirdLine = 4074;
			break;

		case 7://cooking
			firstLine = 27197;
			thirdLine = 4134;
			break;

		case 8://woodcut
			firstLine = 27201;
			thirdLine = 4146;
			break;

		case 9://fletching
			firstLine = 27185;
			thirdLine = 4110;
			break;

		case 10://fishing
			firstLine = 27195;
			thirdLine = 4128;
			break;

		case 11://firemaking
			firstLine = 27199;
			thirdLine = 4140;
			break;

		case 12://crafting
			firstLine = 27183;
			thirdLine = 4104;
			break;

		case 13://smithing
			firstLine = 27193;
			thirdLine = 4122;
			break;

		case 14://mining
			firstLine = 27191;
			thirdLine = 4116;
			break;

		case 15://herblore
			firstLine = 27179;
			thirdLine = 4092;
			break;

		case 16://agility
			firstLine = 27177;
			thirdLine = 4086;
			break;

		case 17://theiving
			firstLine = 27181;
			thirdLine = 4098;
			break;

		case 18://slayer
			firstLine = 27187;
			thirdLine = 12171;
			break;

		case 19://farming
			firstLine = 27189;
			thirdLine = 13921;
			break;

		case 20:
			firstLine = 27173;
			thirdLine = 4157;
			break;
		case 21:
			firstLine = 27157;
			thirdLine = 4157;
			break;
		case 22:
			firstLine = 27169;
			thirdLine = 4157;
			break;
		default:
			firstLine = 1;
			thirdLine = 1;
			break;
		}
		sendFrame126("" + c.getSkills().getLevel(skill), firstLine);
		sendFrame126("" + c.getSkills().getActualLevel(skill) + "", firstLine + 1);
		sendFrame126("" + c.getSkills().getExperience(skill) + "", thirdLine);
		sendFrame126("" + SkillExperience.getExperienceForLevel(c.getSkills().getActualLevel(skill)) + "", thirdLine + 1);

		sendFrame126("" + c.getSkills().getLevel(Skill.PRAYER) + "/"+c.getSkills().getActualLevel(Skill.PRAYER), 25104);
	}

	public int getXPForLevel(int level) {
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++) {
			points += (int) Math.floor((double) lvl + 300.0
					* Math.pow(2.0, (double) lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int) Math.floor((double) points / 4);
		}
		return 0;
	}

	public int getLevelForXP(int exp) {
		int points = 0;
		int output = 0;
		if (exp > 13034430)
			return 99;
		for (int lvl = 1; lvl <= 99; lvl++) {
			points += (int) Math.floor((double) lvl + 300.0
					* Math.pow(2.0, (double) lvl / 7.0));
			output = (int) Math.floor((double) points / 4);
			if (output >= exp) {
				return lvl;
			}
		}
		return 0;
	}

	public boolean addSkillXP(int amount, int skillId) {
		Skill skill = Skill.forId(skillId);
		if (Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
			return false;
		}
		if (amount + c.getSkills().getExperience(skill) < 0) {
			return false;
		}

		int orn_amt_skilling = Misc.random(1, 5);
		int chance_for_orn = Misc.random(1, 4);
		/*if (skillId > 6) {
			if (c.summonId == 33965 && chance_for_orn <= 3 || c.summonId == 33964 && chance_for_orn <= 3) {
				if (c.getItems().isWearingItem(33956)) {
					c.getItems().addItemUnderAnyCircumstance(33962, 1);
				}
				if (c.getItems().isWearingItem(33957)) {
					c.getItems().addItemUnderAnyCircumstance(33962, 1);
				}
				if (c.getItems().isWearingItem(33111)) {
					c.getItems().addItemUnderAnyCircumstance(33962, Misc.random(1, 3));
				}
				c.getItems().addItemUnderAnyCircumstance(33962, orn_amt_skilling);
			} else if (chance_for_orn <= 2) {
				if (c.getItems().isWearingItem(33956)) {
					c.getItems().addItemUnderAnyCircumstance(33962, 1);
				}
				if (c.getItems().isWearingItem(33957)) {
					c.getItems().addItemUnderAnyCircumstance(33962, 1);
				}
				if (c.getItems().isWearingItem(33111)) {
					c.getItems().addItemUnderAnyCircumstance(33962, Misc.random(1, 3));
				}
				c.getItems().addItemUnderAnyCircumstance(33962, orn_amt_skilling);
			}
		}*/


		int oldLevel = c.getSkills().getActualLevel(skill);
		int oldExperience = c.getSkills().getExperience(skill);

		if (oldExperience + amount > 200000000) {
			c.getSkills().setExperience(200000000, skill);
		} else {
			c.getSkills().addExperience(amount, skill);
		}
		if (oldLevel < c.getSkills().getActualLevel(skill)) {
			if (c.getSkills().getLevel(skill) < c.getSkills().getActualLevel(skill) && skillId != 3 && skillId != 5)
				c.getSkills().setLevel(c.getSkills().getActualLevel(skill), skill);
			c.combatLevel = c.calculateCombatLevel();
			c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 55804);
			levelUp(skillId);
			if (c.getSkills().getActualLevel(skill) == 99) {
				// TODO Skill Activity feed
			}
			requestUpdates();
		}
		c.getSkills().sendRefresh();
		return true;
	}
	public int getSkillCurrentLevel(int skill) {
		return c.playerLevel[skill];
	}
	public int getXP(int skill) {
		return c.playerXP[skill];
	}
	public void resetBarrows() {
		c.barrowsNpcs[0][1] = 0;
		c.barrowsNpcs[1][1] = 0;
		c.barrowsNpcs[2][1] = 0;
		c.barrowsNpcs[3][1] = 0;
		c.barrowsNpcs[4][1] = 0;
		c.barrowsNpcs[5][1] = 0;
		c.barrowsKillCount = 0;
		c.randomCoffin = Misc.random(3) + 1;
	}
	public static void displayReward(Player c, int item, int amount, int item2, int amount2, int item3, int amount3, int item4, int amount4, int item5, int amount5) {
		int[] items = {
				item, item2, item3, item4, item5
		};
		int[] amounts = {
				amount, amount2, amount3,	amount4,	amount5
		};
		c.outStream.createFrameVarSizeWord(53);
		c.outStream.writeWord(6963);
		c.outStream.writeWord(items.length);
		for(int i = 0; i < items.length; i++) {
			if(c.playerItemsN[i] > 254) {
				c.outStream.writeByte(255);
				c.outStream.writeDWord_v2(amounts[i]);
			} else {
				c.outStream.writeByte(amounts[i]);
			}
			if(items[i] > 0) {
				c.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				c.outStream.writeWordBigEndianA(0);
			}
		}
		c.outStream.endFrameVarSizeWord();
		c.flushOutStream();
		c.getItems().addItem(item, amount);
		c.getItems().addItem(item2, amount2);
		c.getItems().addItem(item3, amount3);
		c.getPA().showInterface(6960);
	}	
	public static void displayChestReward(Player c, int item, int amount, int item2, int amount2, int item3, int amount3, int item4, int amount4, int item5, int amount5) {
		int[] items = {
				item, item2, item3, item4, item5
		};
		int[] amounts = {
				amount, amount2, amount3,	amount4,	amount5
		};
		c.outStream.createFrameVarSizeWord(53);
		c.outStream.writeWord(6963);
		c.outStream.writeWord(items.length);
		for(int i = 0; i < items.length; i++) {
			if(c.playerItemsN[i] > 254) {
				c.outStream.writeByte(255);
				c.outStream.writeDWord_v2(amounts[i]);
			} else {
				c.outStream.writeByte(amounts[i]);
			}
			if(items[i] > 0) {
				c.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				c.outStream.writeWordBigEndianA(0);
			}
		}
		c.outStream.endFrameVarSizeWord();
		c.flushOutStream();
		c.getItems().addItem(item, amount);
		c.getItems().addItem(item2, amount2);
		c.getItems().addItem(item3, amount3);
		c.getPA().showInterface(6960);
	}	
	public void displayItemOnInterface(int frame, int slot, int id, int amount) {

		itemOnInterface(c, frame, slot, id, amount);
	}
	public void itemOnInterface(Player player, int frame, int slot, int id,
			int amount) {
		player.outStream.createFrameVarSizeWord(34);
		player.outStream.writeWord(frame);
		player.outStream.writeByte(slot);
		player.outStream.writeWord(id + 1);
		player.outStream.writeByte(255);
		player.outStream.writeDWord(amount);
		player.outStream.endFrameVarSizeWord();
	}
	/*public static void sendUpdateItems(Player c, int frame, int[] items) {
		c.outStream.createFrameVarSizeWord(53);
		c.outStream.writeWord(frame);
		c.outStream.writeWord(items.length);
		int[] var6 = items;
		for (int i = 0; i < items.length; i++) {
			int item = var6[i];
				if (c.getItems().getItemAmount(item) > 254) {
					c.outStream.writeByte(255);
					c.outStream.writeDWord_v2(1);
				} else {
					c.outStream.writeByte(1);
				}

				c.outStream.writeWordBigEndianA(item);
		}
	}*/
	/**
	 * Dynamically displays ANY number of clue scroll rewards on the interface.
	 */
	public static void displayReward(Player c, int[] items, int[] amounts) {
		c.outStream.createFrameVarSizeWord(53);
		c.outStream.writeWord(6963); // The clue reward container ID
		c.outStream.writeWord(items.length); // Dynamic length!
		
		for(int i = 0; i < items.length; i++) {
			if(amounts[i] > 254) {
				c.outStream.writeByte(255);
				c.outStream.writeDWord_v2(amounts[i]);
			} else {
				c.outStream.writeByte(amounts[i]);
			}
			if(items[i] > 0) {
				c.outStream.writeWordBigEndianA(items[i] + 1);
			} else {
				c.outStream.writeWordBigEndianA(0);
			}
		}
		c.outStream.endFrameVarSizeWord();
		c.flushOutStream();
		
		// Add all the items to the player's actual inventory
		for(int i = 0; i < items.length; i++) {
			if (items[i] > 0) {
				c.getItems().addItem(items[i], amounts[i]);
			}
		}
		
		c.getPA().showInterface(6960);
	}
	public static int getRandomItemFromLog(CollectionLogData log) {
	    int[][] entries = log.getEntries();
	    if (entries.length == 0) return -1;

	    int index = Misc.random(entries.length - 1); // assuming you have a Misc.random()
	    return entries[index][0]; // the item ID
	}

	public static int Barrows[] = { 4708, 4710, 4712, 4714, 4716, 4718, 4720,
			4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747,
			4749, 4751, 4753, 4755, 4757, 4759 };
	public static int Bandos[] = { 11832, 11834, 11836, 11812, 11818, 11820, 11822};
	public static int Kraken[] = { 12004, 11905, 12007};
	public static int sharedClueRewards[] = {3827,3828,3829,3830,3831,3832,3833,3834,3835,3836,3837,3838,3839,10476,995,7329,7330,7333 };
	public static int commonEasyClueRewards[] = {1165,1125,1089,1077,1133,1169,1097,1367,1361,1381,1297,1269,1273,1217,847,849,10366,10280 };
	public static int EasyClue[] = {
			2587,
			2583,
			2585, 
			3668, 
			2589,
			2595,
			2591,
			2593,
			3669, 
			2597,
			10665, 
			10668,
			10671,
			10674,
			10677,
			10699, 
			10700, 
			10701, 
			10702, 
			10703, 
			7362, 
			7366, 
			7364,
			7368,
			7394, 
			7390,
			7386, 
			7396,
			7392,
			7388, 
			10458, 
			10464, 
			10462, 
			10466,
			10460, 
			10468, 
			10316,
			10318,
			10320,
			10322,
			10324, 
			2631,
			2633,
			2635, 
			2637,
			10392,
			10398,
			10396,
			10404,
			10424,
			10406,
			10426,
			10412,
			10432, 
			10414,
			10434,
			10408,
			10428,
			10410,
			10430,
			10366,
			10280 };
	public static int Runes[] = { 4740, 558, 560, 565 };
	public static int Pots[] = {};

	public int randomBarrows() {
		return Barrows[(int) (Math.random() * Barrows.length)];
	}	
	public int randomBandos() {
		return Bandos[(int) (Math.random() * Bandos.length)];
	}
	public int randomKraken() {
		return Kraken[(int) (Math.random() * Kraken.length)];
	}
	public int randomEasyItem() {
		return EasyClue[(int) (Math.random() * EasyClue.length)];
	}

	public int randomCommonEasyItem() {
		return commonEasyClueRewards[(int) (Math.random() * commonEasyClueRewards.length)];
	}
	public int randomSharedItem() {
		return sharedClueRewards[(int) (Math.random() * sharedClueRewards.length)];
	}
	public int randomRunes() {
		return Runes[(int) (Math.random() * Runes.length)];
	}

	public int randomPots() {
		return Pots[(int) (Math.random() * Pots.length)];
	}
	public void CollLogPopUp(int[][] bossCollectionLog, int itemID, int amount) {
		String itemName = ItemAssistant.getItemName(itemID);
		if(c.getCollectionLog().getCollectionLogItemAmount(bossCollectionLog, itemID) == 0){
			sendCollectionLogPopUp(itemID, 8);
			c.sendMessage("New item added to your collection log: @blu@" + itemName);
		}
	}				

	public void sendCollectionLogPopUp(int itemId, int durationSeconds) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrame(216);
			c.getOutStream().writeWord(itemId);
			c.getOutStream().writeWord(durationSeconds);
			c.flushOutStream();
		}
	}

	public void sendMusicFavorite(String name, int textId, int configID) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(220);
			c.getOutStream().writeString(name);
			c.getOutStream().writeWord(textId);
			c.getOutStream().writeWord(configID);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}
	public void sendFrame126(String s, int id) {
		if (c.getOutStream() != null && c != null) {
			c.getOutStream().createFrameVarSizeWord(126);
			c.getOutStream().writeString(s);
			c.getOutStream().writeWordA(id);
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}
	/**
	 * Dynamically changes the atActionType of an interface on the client.
	 * 0 = Unclickable/Disabled, 1 = Standard Click, etc.
	 */
	public void sendInterfaceActionState(int interfaceId, int actionType) {
		synchronized(c) {
			if(c.getOutStream() != null && c != null) {
				c.getOutStream().createFrame(141);
				c.getOutStream().writeWord(interfaceId);
				c.getOutStream().writeByte(actionType);
				c.flushOutStream();
			}
		}
	}
	public void sendGameTimer(ClientGameTimer timer, TimeUnit unitOfTime, double duration) {
		if (c == null || c.disconnected) {
			return;
		}
		Buffer stream = c.getOutStream();
		if (stream == null) {
			return;
		}
		long seconds = Math.round(unitOfTime.toSeconds((long)(duration * 1000)) / 1000.0);
		seconds = Math.min(seconds, 65535);

		stream.createFrame(223);
		stream.writeByte(timer.getTimerId());
		stream.writeDWord((int) seconds);
		c.flushOutStream();
	}

	public void CollLogPopUp() {
		if (c.CollLogTimer > 0) {
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - c.remainingTime;

			if (elapsedTime >= 1000) {
				c.CollLogTimer -= elapsedTime;
				c.remainingTime = currentTime;

				if (c.CollLogTimer < 0) {
					c.CollLogTimer = 0;
					c.newCollItem = false;
				}

				int secondsLeft = c.CollLogTimer / 1000;
				sendFrame126("" + secondsLeft, 28985);

				if (secondsLeft <= 0) {
					c.CollLogTimer = 0;
					sendFrame126("", 28984);
					sendFrame126("0", 28985);
				}
			}
		}
	}

	/**
	 * Show an arrow icon on the selected player.
	 * 
	 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
	 * @Param j - The player/Npc that the arrow will be displayed above.
	 * @Param k - Keep this set as 0
	 * @Param l - Keep this set as 0
	 */
	public void drawHeadicon(int i, int j, int k, int l) {
		// synchronized(c) {
		c.outStream.createFrame(254);
		c.outStream.writeByte(i);

		if (i == 1 || i == 10) {
			c.outStream.writeWord(j);
			c.outStream.writeWord(k);
			c.outStream.writeByte(l);
		} else {
			c.outStream.writeWord(k);
			c.outStream.writeWord(l);
			c.outStream.writeByte(j);
		}
	}

	public int getNpcId(int id) {
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] != null) {
				if (NPCHandler.npcs[i].getIndex() == id) {
					return i;
				}
			}
		}
		return -1;
	}

	public void removeObject(int x, int y) {
		object(-1, x, x, 10, 10);
	}

	public void handleGlory(int gloryId) {
		c.getDH().sendOption4("Edgeville", "Al Kharid", "Karamja", "Draynor");
		c.usingGlory = true;
	}
	public void handleArdyCloak() {
		if(c.ArdyDiaryEasy == 0) {
			c.sendMessage("You must complete the Ardougne easy achievement diaries to teleport!");	
		} else if(c.ArdyDiaryEasy == 10 && c.ArdyDiaryMed == 10) {
			c.getDH().sendOption2("Ardougne Monastery", "Cancel");
		} else
			c.getDH().sendOption2("Ardougne Monastery", "Ardougne farm");

		c.usingCloak = true;
	}
	public void handleObjectRegion(int objectId, int minX, int minY, int maxX, int maxY) {
		for (int i = minX; i < maxX+1; i++) {
			for (int j = minY; j < maxY+1; j++) {
				c.getPA().object(objectId, i, j, -1, 10);
			}
		}
	}

	public boolean itemUsedInRegion(int minX, int maxX, int minY, int maxY) {
		return (c.objectX >= minX && c.objectX <= maxX) && (c.objectY >= minY && c.objectY <= maxY);
	}
	public void resetVariables() {
		if (c.isBanking) {
			c.isBanking = false;
		}
		if (c.isChecking) {
			c.isChecking = false;
		}
		c.usingGlory = false;
		c.smeltInterface = false;
		c.openLootBag = false;
		c.addingToRP = false;
		c.addingCharges = false;

		c.viewingLootBag = false;
		c.addingItemsToLootBag = false;
		c.viewingRunePouch = false;
		c.placeHolderWarning = false;
		
		c.smeltType = 0;
		c.smeltAmount = 0;
		c.nextChat = -1;
		c.activeAction = Player.ChatboxAction.NONE;
		if (c.viewingLootBag || c.addingItemsToLootBag)
			c.viewingLootBag = false;
		if (c.dialogueAction > -1) {
			c.dialogueAction = -1;
		}
		if (c.teleAction > -1) {
			c.teleAction = -1;
		}
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.BONE_ON_ALTAR);
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.farming);
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.mineOre);
	}

	public boolean inPitsWait() {
		return c.getX() <= 2404 && c.getX() >= 2394 && c.getY() <= 5175
				&& c.getY() >= 5169;
	}

	public void castleWarsObjects() {
		object(-1, 2373, 3119, -3, 10);
		object(-1, 2372, 3119, -3, 10);
	}

	public void removeFromCW() {
		if (c.castleWarsTeam == 1) {
			if (c.inCwWait) {
				//Server.castleWars.saradominWait
				//		.remove(Server.castleWars.saradominWait
				//				.indexOf(c.playerId));
			} else {
				//Server.castleWars.saradomin.remove(Server.castleWars.saradomin
				//		.indexOf(c.playerId));
			}
		} else if (c.castleWarsTeam == 2) {
			if (c.inCwWait) {
				//Server.castleWars.zamorakWait
				//	.remove(Server.castleWars.zamorakWait
				//			.indexOf(c.playerId));
			} else {
				//Server.castleWars.zamorak.remove(Server.castleWars.zamorak
				//	.indexOf(c.playerId));
			}
		}
	}

	public int antiFire() {
		int toReturn = 0;
		if (c.antiFirePot)
			toReturn++;
		if (c.playerEquipment[c.playerShield] == 1540 || c.prayerActive[12]
				|| c.playerEquipment[c.playerShield] == 11284)
			toReturn++;
		return toReturn;
	}

	public boolean checkForFlags() {
		int[][] itemsToCheck = { { 995, 100000000 }, { 35, 5 }, { 667, 5 }, { 2402, 5 }, { 746, 5 }, { 4151, 150 },
				{ 565, 100000 }, { 560, 100000 }, { 555, 300000 }, { 11235, 10 } };
		for (int[] anItemsToCheck : itemsToCheck) {
			if (anItemsToCheck[1] < c.getItems().getTotalCount(anItemsToCheck[0]))
				return true;
		}
		return false;
	}

	public void ChangeAppearance() {
		c.getPA().showInterface(3559);
		c.canChangeAppearance = true;
		//c.onTutorialIsland = true;
	}
	private static final int[][] STARTER_ITEMS = { { 1351, 1 }, { 590, 1 },
			{ 303, 1 }, { 315, 1 }, { 1925, 1 }, { 1931, 1 }, { 2309, 1 },
			{ 1265, 1 }, { 1205, 1 }, { 1277, 1 }, { 1171, 1 }, { 841, 1 },
			{ 882, 25 }, { 556, 25 }, { 558, 15 }, { 555, 6 }, { 557, 4 },
			{ 559, 2 } };

	public void addStarter() {
		for (int[] element : STARTER_ITEMS) {
			int item = element[0];
			int amount = element[1];
			c.getItems().addItem(item, amount);
		}
	}
	public void swapSpellBookOperate() {
		if(c.swapCount >= 3 && c.playerRights < 3) {
			c.sendMessage("You've used up your daily swap limit.");
			return;
		}
		if (c.inWild()) {
			return;
		}
		if (c.playerMagicBook == 0) {
			c.playerMagicBook = 1;
			c.setSidebarInterface(6, 12855);
			c.autocasting = false;
			c.sendMessage("An ancient wisdomin fills your mind.");
			c.getPA().resetAutocast();
		} else if (c.playerMagicBook == 1) {
			c.sendMessage("You switch to the lunar spellbook.");
			c.setSidebarInterface(6, 29999);
			c.playerMagicBook = 2;
			c.autocasting = false;
			c.autocastId = -1;
			c.getPA().resetAutocast();
		} else if (c.playerMagicBook == 2) {
			c.setSidebarInterface(6, 1151);
			c.playerMagicBook = 0;
			c.autocasting = false;
			c.sendMessage("You feel a drain on your memory.");
			c.autocastId = -1;
			c.getPA().resetAutocast();
		}
			c.swapCount += 1;
			c.sendMessage("Daily spell book swap: "+c.swapCount+"/3");
	}
	public void useOperate(int itemId, int option) {
		ItemDefinition def = ItemDefinition.forId(itemId);
		Optional<DegradableItem> d = DegradableItem.forId(itemId);
		
			c.getSkillCapes().handleCape(itemId, option);
			
		switch(option) {
		case 0:
			switch (itemId) {
			case 12904:
				c.sendMessage("The toxic staff of the dead has " + c.getToxicStaffOfTheDeadCharge() + " charges remaining.");
				break;
			case 13199:
			case 13197:
				c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charges remaining.");
				break;
			case 11907:
			case 12899:
				int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
				c.sendMessage("The " + def.getName() + " has " + charge + " charges remaining.");
				break;

			case 12926:
				def = ItemDefinition.forId(c.getToxicBlowpipeAmmo());
				c.sendMessage("The blowpipe has " + c.getToxicBlowpipeAmmoAmount() + " " + def.getName() + " and "
						+ c.getToxicBlowpipeCharge() + " charge remaining.");
				break;
			case 12931:
				def = ItemDefinition.forId(itemId);
				if (def == null) {
					return;
				}
				c.sendMessage("The " + def.getName() + " has " + c.getSerpentineHelmCharge() + " charge remaining.");
				break;
			case 1712:
			case 1710:
			case 1708:
			case 1706:
			case 19707:
				if (c.wildLevel > 30) {
					c.sendMessage("@cr10@You can not teleport above 30 wilderness.");
					return;
				}
				handleGlory(itemId);
				break;
			case 11283:
			case 11284:
				if (c.playerIndex > 0) {
					c.getCombat().handleDfs();
				} else if (c.npcIndex > 0) {
					c.getCombat().handleDfsNPC();
				}
				break;
			}
			break;
		case 1:
			switch(itemId) {

			case 2572://ring of wealth show boss log
				//c.getPA().showInterface(32460);
				c.getLog().open(c);
				break;
			}
			break;
		case 2:
			switch(itemId) {

			case 13124:
			c.getPA().startTeleport(Config.MONASTERYX, Config.MONASTERYY, 0, "modern");
			break;
			case 19675:
				c.sendMessage("Your Arclight has " + c.getArcLightCharge() + " charges remaining.");
				break;
			}
			break;
		case 3:
			switch(itemId) {
			case 13124:
				startTeleport(2663, 3374, 0, "ArdougneCloak");
				break;
			}
			break;
		case 4, 5:
			switch(itemId) {
			
			}
			break;
        }
	}

	public void getSpeared(int otherX, int otherY, int i) {
		int x = c.getX() - otherX;
		int y = c.getY() - otherY;
		if (x > 0)
			x = 1;
		else if (x < 0)
			x = -1;
		if (y > 0)
			y = 1;
		else if (y < 0)
			y = -1;
		moveCheck(x, y);
		c.lastSpear = System.currentTimeMillis();
	}

	public void moveCheck(int xMove, int yMove) {
		movePlayer(c.getX() + xMove, c.getY() + yMove, c.getHeight());
	}
	public void movePlayerDuel(int x, int y, int h) {
		DuelSession session = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(session) && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION
				&& Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			return;
		}
		c.resetWalkingQueue();
		c.setX(x);
		c.setY(y);
		c.setHeight(h);
		c.setNeedsPlacement(true);
		requestUpdates();
	}

	public void resetTzhaar() {
		c.waveId = -1;
		c.tzhaarToKill = -1;
		c.tzhaarKilled = -1;
		c.getPA().movePlayer(2438, 5168, 0);
	}

	public void appendPoison(int damage) {
		if (System.currentTimeMillis() - c.lastPoisonSip > c.poisonImmune) {
			c.sendMessage("You have been poisoned.");
			c.poisonDamage = damage;

			c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.MINUTES, 1);
		}
	}

	public void fillPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > c.getItems().getItemAmount(1436)) {
			toAdd = c.getItems().getItemAmount(1436);
		}
		if (toAdd > c.POUCH_SIZE[i] - c.pouches[i])
			toAdd = c.POUCH_SIZE[i] - c.pouches[i];
		if (toAdd > 0) {
			c.getItems().deleteItem(1436, toAdd);
			c.pouches[i] += toAdd;
		}
	}

	public void emptyPouch(int i) {
		if (i < 0)
			return;
		int toAdd = c.pouches[i];
		if (toAdd > c.getItems().freeSlots()) {
			toAdd = c.getItems().freeSlots();
		}
		if (toAdd > 0) {
			c.getItems().addItem(1436, toAdd);
			c.pouches[i] -= toAdd;
		}
	}

	public void handleWeaponStyle() {
		if (c.fightMode == 0) {
			c.getPA().sendFrame36(43, c.fightMode);
		} else if (c.fightMode == 1) {
			c.getPA().sendFrame36(43, 3);
		} else if (c.fightMode == 2) {
			c.getPA().sendFrame36(43, 1);
		} else if (c.fightMode == 3) {
			c.getPA().sendFrame36(43, 2);
		}
	}
	public Clan getClan() {
	    // If the player object already has a reference, use it. It's faster and safer.
	    if (c.clan != null) {
	        return c.clan;
	    }
	    // Fallback: search for the founder file
	    if (c.clanName != null && World.getWorld().getClanManager().clanExists(c.clanName)) {
	        return World.getWorld().getClanManager().getClan(c.clanName);
	    }
	    return null;
	}
	public void clearClanChat() {
	    c.clan = null;
	    c.clanName = null;
	    c.getPA().sendFrame126("Join Chat", 18135); // Reset button text
	    c.getPA().sendFrame126("Not in channel", 18139);
	    for (int j = 18665; j < 18764; j++) {
	        c.getPA().sendFrame126("", j);
	    }
	}
	public void setClanData() {
		boolean exists = World.getWorld().getClanManager().clanExists(c.playerName);
		if (!exists || c.clan == null) {
			sendFrame126("Join", 18135);
			sendFrame126("Not in channel", 18139);
		}
		if (!exists) {
			sendFrame126("Chat Disabled", 58306);
			String title = "";
			for (int id = 58307; id < 58317; id += 3) {
				if (id == 58307) {
					title = "Anyone";
				} else if (id == 58310) {
					title = "Anyone";
				} else if (id == 58313) {
					title = "General+";
				} else if (id == 58316) {
					title = "Only Me";
				}
				sendFrame126(title, id + 2);
			}
			for (int index = 0; index < 100; index++) {
				sendFrame126("", 58323 + index);
			}
			for (int index = 0; index < 100; index++) {
				sendFrame126("", 58424 + index);
			}
			return;
		}
		Clan clan = World.getWorld().getClanManager().getClan(c.playerName);
		if (clan == null) return;
		sendFrame126(Misc.capitalize(clan.getTitle()), 58306);
		String title = "";
		for (int id = 58307; id < 58317; id += 3) {
			if (id == 58307) {
				title = clan.getRankTitle(clan.whoCanJoin)
						+ (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 58310) {
				title = clan.getRankTitle(clan.whoCanTalk)
						+ (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 58313) {
				title = clan.getRankTitle(clan.whoCanKick)
						+ (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
			} else if (id == 58316) {
				title = clan.getRankTitle(clan.whoCanBan)
						+ (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
			}
			sendFrame126(title, id + 2);
		}
		// Logic for Friends list inside setClanData
		if (c.getFriends().getFriends() != null) {
	        // Clear and rebuild the cache snapshot
	        java.util.List<String> setupList = c.getClanSetupList();
	        setupList.clear();

	        java.util.LinkedHashSet<String> hybridSet = new java.util.LinkedHashSet<>();

	        // 1. Add Yourself
	        hybridSet.add(Misc.formatPlayerName(c.playerName).toLowerCase());

	        // 2. Add Ranked Members
	        if (clan.rankedMembers != null) {
	            for (String ranked : clan.rankedMembers) {
	                if (ranked != null && !ranked.isEmpty()) {
	                    hybridSet.add(Misc.formatPlayerName(ranked).toLowerCase());
	                }
	            }
	        }

	        // 3. Add Friends
	        for (long friendLong : c.getFriends().getFriends()) {
	            if (friendLong != 0) {
	                String friendName = Misc.longToPlayerName2(friendLong);
	                hybridSet.add(Misc.formatPlayerName(friendName).toLowerCase());
	            }
	        }

	        // 4. Verification Filter: Remove ghosts
	        hybridSet.removeIf(name -> {
	            if (name.equalsIgnoreCase(c.playerName)) return false;
	            if (clan.getRank(name) >= 0) return false;
	            if (c.getFriends().has(Misc.playerNameToLong2(name))) return false;
	            return true;
	        });

	        // Store the final ordered names in the player's cache
	        setupList.addAll(hybridSet);

	        // 5. Populate the interface using the cached list
	        for (int i = 0; i < 100; i++) {
	            if (i < setupList.size()) {
	                String name = setupList.get(i);
	                int rank = clan.getRank(name);
	                
	                String displayName = Misc.formatPlayerName(name);
	                String icon = (rank >= 0) ? "<clan=" + rank + ">" : "";
	                
	                String rankText;
	                if (rank >= 0) {
	                    rankText = clan.getRankTitle(rank);
	                } else if (c.getFriends().has(Misc.playerNameToLong2(name)) && rank < 0) {
	                    rankText = "@gre@Not in Clan";
	                } else if (c.getFriends().has(Misc.playerNameToLong2(name)) && rank == 0) {
	                    rankText = "@gre@Friend";
	                } else {
	                    rankText = "@gre@Not in clan";
	                }
	                
	                sendFrame126(icon + displayName, 58323 + i);
	                sendFrame126(rankText, 58424 + i);
	            } else {
	                sendFrame126("", 58323 + i);
	                sendFrame126("", 58424 + i);
	            }
	        }
	    }
	}
	public void tutorialIslandInterface(int i, int j) {
		// TODO Auto-generated method stub
		
	}


}
