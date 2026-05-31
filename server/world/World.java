package server.world;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;

import lombok.Getter;
import lombok.Setter;

import server.clip.ObjectDef;
import server.HubConnector;
import server.ServerStatusWriter;
import server.clip.MapCacheLoader;
import server.clip.MapIndexLoader;
import server.clip.doors.DoorDefinition;
import server.clip.doors.DoorHandler;
import server.clip.doors.DoubleDoorDefinition;
import server.clip.doors.GateDefinition;
import server.server.data.*;
import server.util.Database;
import server.util.definitions.AnimationDefinition;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.content.clans.ClanManager;
import server.event.EventHandler;
import server.event.impl.WheatPortalEvent;
import server.model.instance.InstanceManager;
import server.model.npcs.NPCHandler;
import server.model.npcs.combat.CombatScriptHandler;
import server.model.npcs.combat.NPCCombatStats;
import server.model.npcs.drops.DropManager;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.PriceChecker;
import server.model.items.ItemDefinition;
import server.model.lobby.LobbyManager;
import server.model.minigames.FightPits;
import server.model.minigames.trawler.FishingTrawlerLobby;
import server.model.multiplayer_session.MultiplayerSessionListener;
import server.model.players.packets.RegionMusic;
import server.model.players.packets.dialogue.DialogueService;
import server.model.players.skills.fishing.spots.FishingSpawner;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.world.event.CyclicEventManager;
import server.world.map.VarBit;
import server.world.map.Varp;
import server.world.objects.GlobalObjects;

@Getter
public class World {

	public static World getWorld() {
		return singleton;
	}

	private static World singleton = new World();
	@Setter
	private boolean gameUpdating;

	private boolean localWorld, betaWorld;

	@Getter
	@Setter
	private boolean worldLoaded;

	@Getter
	@Setter
	private boolean worldPaused;

	private World() {
	}

	public void onShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			SYSTEM_SCHEDULER.shutdown(); // Updated name
			ApiClient.shutdown();
		}));
	}
	// Rename to reflect its new global background duties
	private static final ScheduledExecutorService SYSTEM_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

	public void init() throws Exception {
		checkForLocal();
		CombatScriptHandler.init();
		npcHandler.init();
		dropManager.read();
		MapCacheLoader.load();
		ItemDefinition.load();
		DoorDefinition.load();
		DoubleDoorDefinition.load();
		Database.initialize();
		GateDefinition.load();
		NPCCombatStats.load();
		ObjectDef.unpackConfig();
		AnimationDefinition.unpackConfig();
		VarBit.unpackConfig();
		Varp.unpackConfig();
		MapIndexLoader.load();
		RegionMusic.load();
		DialogueService.init();
		DoorHandler.rebuildOpenIndex();
		globalObjects.loadGlobalObjectFile();
		itemHandler.loadGlobalDrops();
		FishingSpawner.initializeAllFishingSpots();
		LobbyManager.initializeLobbies();
		clanManager.loadClanTitles();
		PriceChecker.loadcache();
		MotherlodeMine.spawnVeins();
		eventHandler.submit(new WheatPortalEvent());

		// Task A: The Player Status Writer (Every 10 seconds)
		SYSTEM_SCHEDULER.scheduleAtFixedRate(() -> {
			try {
				int activePlayers = 0;
				for (Player p : PlayerHandler.players) {
					if (p != null && p.isActive && p.playerName != null) {
						activePlayers++;
					}
				}
				ServerStatusWriter.updateStatus(activePlayers);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 10, 10, TimeUnit.SECONDS);

		// Task B: GLOBAL GRAND EXCHANGE CACHE RELOAD (Every 4 hours)
		SYSTEM_SCHEDULER.scheduleAtFixedRate(() -> {
			try {
				System.out.println("[System] Running background refresh of Grand Exchange prices...");
				server.model.players.packets.GeValuesCache.loadGePricesCache();
			} catch (Exception e) {
				System.err.println("[Error] Background GE price refresh failed!");
				e.printStackTrace();
			}
		}, 4, 4, TimeUnit.HOURS); // Initial delay of 4 hours, repeats every 4 hours

		ApiClient.startPmPollingLoop();
	}
	public void tick() {
		eventHandler.process();
		serverData.processQueue();
		playerHandler.process();
		npcHandler.process();
		objectManager.process();
		itemHandler.process();
		FishingTrawlerLobby.process();
		shopHandler.process();
		globalObjects.pulse();
		InstanceManager.tick();
		CycleEventHandler.getSingleton().addEvent(1, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				MotherlodeMine.process();
			}

			@Override
			public void stop() {}
		}, 10);
	}

	/**
	 * Contains data which is saved between sessions.
	 */
	private ServerData serverData = new ServerData();
	/**
	 * Handles global objects.
	 */
	public ObjectManager objectManager = new ObjectManager();
	/**
	 * Handles logged in players.
	 */
	private PlayerHandler playerHandler = new PlayerHandler();

	/**
	 * Handles global NPCs.
	 */
	public NPCHandler npcHandler = new NPCHandler();

	/**
	 * Handles global shops.
	 */
	public ShopHandler shopHandler = new ShopHandler();
	/**
	 * Handles global items.
	 */
	public ItemHandler itemHandler = new ItemHandler();
	/**
	 * Handles the fightpits minigame.
	 */
	private FightPits fightPits = new FightPits();
	/**
	 * Handles clan chats.
	 */
	public ClanManager clanManager = new ClanManager();
	/**
	 * Handles global drops.
	 */
	private DropManager dropManager = new DropManager();

	/**
	 * A class that will manage game events
	 */

	private EventHandler eventHandler = new EventHandler();
	/**
	 * Handles multiplayer sessions.
	 */
	private MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();
	/**
	 * Handles global objects.
	 */
	private GlobalObjects globalObjects = new GlobalObjects();
	/**
	 * Handles cyclic events.
	 */
	private CyclicEventManager cyclicEventManager = new CyclicEventManager();

	/**
	 * Reloads the shop handler.
	 */
	public void reloadShops() {
		shopHandler = new ShopHandler();
	}

	public void checkForLocal() {
		try (InputStream in = new URL("http://checkip.amazonaws.com").openStream()) {
			String ip = IOUtils.toString(in, Charsets.UTF_8).trim();
			InetAddress liveAddress = InetAddress.getByName("slothscape.com");
			InetAddress betaAddress = InetAddress.getByName("127.0.0.1");
			betaWorld = ip.equalsIgnoreCase(betaAddress.getHostAddress().trim());
			localWorld = !betaWorld && !ip.equalsIgnoreCase(liveAddress.getHostAddress().trim());
		} catch (Exception e) {
			System.err.println("[Warning] Could not reach AWS to check IP. Defaulting to Local World.");
			localWorld = true;
			betaWorld = false;
		}
	}
}
