package server.model.items;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import server.Config;
import server.Server;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.CollectionLogRegistry;
import server.model.items.collectionlog.CollectionLogTab;
import server.model.items.collectionlog.clues.ClueLogTab;
import server.model.items.collectionlog.clues.EasyClueLog;
import server.model.items.collectionlog.boss.BarrowsLog;
import server.model.items.collectionlog.boss.BossLogTab;
import server.model.items.collectionlog.raid.RaidLogTab;
import server.model.items.collectionlog.minigame.MinigameLogTab;
import server.model.items.collectionlog.other.OtherLogTab;
import server.model.items.collectionlog.boss.SireLog;
import server.model.items.collectionlog.boss.BarrowsLog;
import server.model.items.collectionlog.boss.GraardorLog;
import server.model.items.collectionlog.boss.ZilyanaLog;
import server.model.items.collectionlog.boss.ZulrahLog;
import server.model.items.collectionlog.boss.KrilLog;
import server.model.items.collectionlog.boss.CorpLog;
import server.model.items.collectionlog.boss.KreeLog;
import server.model.items.collectionlog.boss.KrakenLog;
import server.model.items.collectionlog.boss.CallistoLog;
import server.model.items.collectionlog.boss.KBDLog;
import server.model.items.collectionlog.boss.CerberusLog;
import server.model.items.collectionlog.boss.DGKLog;
import server.model.items.collectionlog.boss.BryoLog;
import server.model.items.collectionlog.boss.FcLog;
import server.model.items.collectionlog.boss.FanaticLog;
import server.model.items.collectionlog.boss.ChaosEleLog;
import server.model.items.collectionlog.boss.GiantMoleLog;
import server.model.items.collectionlog.boss.KQLog;
import server.model.items.collectionlog.boss.SkotizoLog;
import server.model.items.collectionlog.other.AerialFishingLog;
import server.model.items.collectionlog.other.CreatureCreationLog;
import server.model.items.collectionlog.other.CyclopesLog;
import server.model.items.collectionlog.other.RandomEventLog;
import server.model.items.collectionlog.other.ChompyLog;
import server.model.items.collectionlog.other.MiscLog;
import server.model.items.collectionlog.other.PetLog;
import server.model.items.collectionlog.other.ChaosDruidLog;
import server.model.items.collectionlog.other.ChampsLog;
import server.model.items.collectionlog.clues.BeginnerClueLog;
import server.model.items.collectionlog.clues.EasyClueLog;
import server.model.items.collectionlog.clues.MediumClueLog;
import server.model.items.collectionlog.clues.HardClueLog;
import server.model.items.collectionlog.clues.HardClueLogRare;
import server.model.items.collectionlog.clues.EliteClueLog;
import server.model.items.collectionlog.clues.EliteClueLogRare;
import server.model.items.collectionlog.clues.MasterClueLog;
import server.model.items.collectionlog.clues.MasterClueLogRare;
import server.model.items.collectionlog.clues.SharedClueLog;
import server.model.items.collectionlog.raid.XericLog;
import server.model.items.collectionlog.raid.BloodLog;
import server.model.items.collectionlog.raid.AmascutLog;
import server.model.npcs.NPCHandler;
import server.model.players.PlayerAssistant;
import server.util.Misc;

public class CollectionLog {

	private Player c;

	// Give every player their own unique registry instance!
	public final CollectionLogRegistry registry = new CollectionLogRegistry();

	public CollectionLog(Player player){
		this.c = player;
	}
	private SireLog sireLog = new SireLog();
	private BarrowsLog barrowsLog = new BarrowsLog();
	private GraardorLog graardorLog = new GraardorLog();
	private ZilyanaLog zilyanaLog = new ZilyanaLog();
	private KrilLog krilLog = new KrilLog();
	private CorpLog corpLog = new CorpLog();
	private KreeLog kreeLog = new KreeLog();
	private KrakenLog krakenLog = new KrakenLog();
	private CallistoLog callistoLog = new CallistoLog();
	private KBDLog kbdLog = new KBDLog();
	private CerberusLog cerberusLog = new CerberusLog();
	private DGKLog dgkLog = new DGKLog();
	private BryoLog bryoLog = new BryoLog();
	private FcLog fcLog = new FcLog();
	private FanaticLog fanaticLog = new FanaticLog();
	private ChaosEleLog chaosEleLog = new ChaosEleLog();
	private KQLog KalphiteLog = new KQLog();
	private SkotizoLog skotizoLog = new SkotizoLog();
	private GiantMoleLog giantMoleLog = new GiantMoleLog();
	private ZulrahLog zulrahLog = new ZulrahLog();

	private XericLog xericLog = new XericLog();
	private BloodLog bloodLog = new BloodLog();
	private AmascutLog amascutLog = new AmascutLog();
	
	private AerialFishingLog aerialFishingLog = new AerialFishingLog();
	private CreatureCreationLog creatureCreationLog = new CreatureCreationLog();
	private CyclopesLog cyclopesLog = new CyclopesLog();
	private RandomEventLog randomEventLog = new RandomEventLog();
	private ChompyLog chompyLog = new ChompyLog();
	private MiscLog miscLog = new MiscLog();
	private PetLog petLog = new PetLog();
	private ChaosDruidLog chaosDruidLog = new ChaosDruidLog();
	private ChampsLog champsLog = new ChampsLog();

	private BeginnerClueLog beginnerClueLog = new BeginnerClueLog();
	private EasyClueLog easyClueLog = new EasyClueLog();
	private MediumClueLog mediumClueLog = new MediumClueLog();
	private HardClueLog hardClueLog = new HardClueLog();
	private HardClueLogRare hardClueLogRare = new HardClueLogRare();
	private EliteClueLog eliteClueLog = new EliteClueLog();
	private EliteClueLogRare eliteClueLogRare = new EliteClueLogRare();
	private MasterClueLog masterClueLog = new MasterClueLog();
	private MasterClueLogRare masterClueLogRare = new MasterClueLogRare();
	private SharedClueLog sharedClueLog = new SharedClueLog();

	public List<CollectionLogData> getAllLogs() {
	    List<CollectionLogData> logs = new ArrayList<>();
	    
	    logs.add(sireLog);
	    logs.add(barrowsLog);
	    logs.add(graardorLog);
	    logs.add(zilyanaLog);
	    logs.add(krilLog);
	    logs.add(corpLog);
	    logs.add(kreeLog);
	    logs.add(krakenLog);
	    logs.add(callistoLog);
	    logs.add(kbdLog);
	    logs.add(cerberusLog);
	    logs.add(dgkLog);
	    logs.add(bryoLog);
	    logs.add(fcLog);
	    logs.add(fanaticLog);
	    logs.add(chaosEleLog);
	    logs.add(KalphiteLog);
	    logs.add(giantMoleLog);
	    logs.add(skotizoLog);
	    

	    logs.add(xericLog);
	    logs.add(bloodLog);
	    logs.add(amascutLog);
	    
	    logs.add(aerialFishingLog);
	    logs.add(creatureCreationLog);
	    logs.add(cyclopesLog);
	    logs.add(randomEventLog);
	    logs.add(chompyLog);
	    logs.add(miscLog);
	    logs.add(petLog);
	    logs.add(chaosDruidLog);
	    logs.add(champsLog);
	    
	    logs.add(beginnerClueLog);
	    logs.add(easyClueLog);
	    logs.add(mediumClueLog);
	    logs.add(hardClueLog);
	    logs.add(hardClueLogRare);
	    logs.add(eliteClueLog);
	    logs.add(eliteClueLogRare);
	    logs.add(masterClueLog);
	    logs.add(masterClueLogRare);
	    logs.add(sharedClueLog);
	    
	    return logs;
	}

	public int getItemFromSlot(int slot) {
		return (c.playerLogItems[slot]);
	}
	public int getItemAmt(int slot) {
		return (c.playerLogItemsAmt[slot]);
	}
	public int getUnlockedLogCount() {
		return registry.getAllLogs().stream()
				.mapToInt(CollectionLogData::getTotalUnlocked)
				.sum();
	}

	public int getCollectionMaxAmount(String id) {
	    CollectionLogData log = registry.getLog(id);
	    return (log != null) ? log.getMaxItems() : -1;
	}
	public int getCollectionTotalAmount(String id) {
		CollectionLogData log = registry.getLog(id);
		return (log != null) ? log.getTotalUnlocked() : -1;
	}
	public void sendLogEntry(String bossId, String displayName, int frameId) {
		int total = c.getCollectionLog().getCollectionTotalAmount(bossId);
		int max = c.getCollectionLog().getCollectionMaxAmount(bossId);

		String name = (total >= max && max > 0) ? "@gre@" + displayName : displayName;
		c.getPA().sendFrame126(name, frameId);
	}



	Map<Integer, CollectionLogTab> tabs = Map.of(
			0, new BossLogTab(),
			1, new RaidLogTab(),
			2, new ClueLogTab(),
			3, new MinigameLogTab(),
			4, new OtherLogTab()
			);

	public void openLog(int log) {
		CollectionLogTab tab = tabs.get(log);
		if (tab == null) return;
		c.CollLogOpen = log;
		int entryStart = 35714;
		int slotIndex = 0;

		// Clear all entries
		for (int i = entryStart; i <= 35813; i++) {
			c.getPA().sendFrame126("", i);
		}

		for (String name : tab.getDisplayNames()) {
			String id = tab.getIdentifierFor(name);
			int amount = getCollectionTotalAmount(id);
			int max = getCollectionMaxAmount(id);
			boolean completed = amount >= max;
			c.getPA().sendFrame126(completed ? "@gre@" + name : name, entryStart + slotIndex++);
		}

		c.getPA().sendScrollMax(Math.max(248, 16 * slotIndex), 35713);
	}

	public String getFormattedLogName(CollectionLogTab tab, String displayName) {
		if (tab == null || displayName == null)
			return displayName;

		String internalId = tab.getIdentifierFor(displayName);
		if (internalId == null)
			return displayName;

		int total = getCollectionTotalAmount(internalId);
		int max = getCollectionMaxAmount(internalId);
		return (total >= max && max > 0) ? "@gre@" + displayName : displayName;
	}


	public int getCollectionLogItemAmount(int[][] bossCollectionLog, int itemId) {
		for (int i = 0; i < bossCollectionLog.length; i++) {
			int itemID = bossCollectionLog[i][0];
			int amount = bossCollectionLog[i][2];

			if (itemID == itemId) {
				return amount; // Return the amount if item ID matches
			}
		}

		return 0; // Return 0 if item ID is not found in the collection log
	}	
	public void showLog(Player c, int[][] log, String title, String logKey, String killLabel, String killCount) {
		c.getCollectionLog().openCollectionLog(log, logKey);
		c.getPA().sendFrame126(title, 35703);
		c.getPA().sendFrame126(killLabel + killCount, 35706);
		c.getPA().sendFrame126("", 35704);
		c.getPA().sendFrame126("", 35707);
	}
	public CollectionLogData getLogBySaveKey(String key) {
		// We now ask THIS player's personal registry, not the static global class
		return registry.getLog(key);
	}

	/**
	 * Gets the kill count for a specific boss/log entry to send to the Highscores.
	 */
	public int getBossKillCount(String logKey) {
		CollectionLogData log = getLogBySaveKey(logKey);
		return (log != null) ? log.getKillCount() : 0;
	}
	public void openCollectionLog(int[][] bossCollectionLog, String logKey) {
		refreshLog(bossCollectionLog, logKey);
	}
	public void refreshLog(int[][] log, String logKey) {
	    // Clear all item slots
	    for (int i = 0; i < 89; i++) {
	        c.getPA().sendFrame34a(35814, -1, i, 0);
	    }

	    // Display each collected item
	    for (int i = 0; i < log.length; i++) {
	        int itemID = log[i][0];
	        int slot = log[i][1];
	        int currentAmt = log[i][2];
	        c.getPA().sendFrame34a(35814, itemID, slot, currentAmt);
	    }

	    // Update total unlocked items (top title bar)
	    c.getPA().sendFrame126("Collection Log - " + getUnlockedLogCount() + "/447", 35702);

	    // Update obtained item count for this specific log
	    c.getPA().sendFrame126("Obtained: @whi@" + getCollectionTotalAmount(logKey) + "/" + log.length, 35705);
	}


	public void addKillCount(String identifier) {
	    switch (identifier.toLowerCase()) {
	        case "sire":
	            sireLog.incrementKillCount();
	            break;
	        case "barrows":
	            barrowsLog.incrementKillCount();
	            break;
	        case "generalgraardor":
	            graardorLog.incrementKillCount();
	            break;
	        case "commanderzilyana":
	            zilyanaLog.incrementKillCount();
	            break;
	        case "k'riltsutsaroth":
	            krilLog.incrementKillCount();
	            break;
	        case "corp":
	            corpLog.incrementKillCount();
	            break;
	        case "kree'arra":
	            kreeLog.incrementKillCount();
	            break;
	        case "kraken":
	            krakenLog.incrementKillCount();
	            break;
	        case "callisto":
	            callistoLog.incrementKillCount();
	            break;
	        case "kingblackdragon":
	            kbdLog.incrementKillCount();
	            break;
	        case "cerberus":
	            cerberusLog.incrementKillCount();
	            break;
	        case "bryophyta":
	            bryoLog.incrementKillCount();
	            break;
	        case "fightcaves":
	            fcLog.incrementKillCount();
	            break;
	        case "fanatic":
	            fanaticLog.incrementKillCount();
	            break;
	        case "chaosele":
	            chaosEleLog.incrementKillCount();
	            break;
	        case "giantmole":
	            giantMoleLog.incrementKillCount();
	            break;
	        case "kq":
	        	KalphiteLog.incrementKillCount();
	            break;
	        case "dgk":
	            dgkLog.incrementKillCount();
	            break;
	        case "skotizo":
	            skotizoLog.incrementKillCount();
	            break;
	        case "zulrah":
	        	zulrahLog.incrementKillCount();
	            break;
	        // Add any other boss logs...
	    }
	}


	public void addBossLogItems(int[][] bossLog, int itemId, int amount) {
		for (int[] entry : bossLog) {
			if (entry[0] == itemId) {
				entry[2] += amount;
				c.getPA().sendFrame34a(35814, itemId, entry[1], entry[2]);
				c.getPA().sendFrame126(getUnlockedLogCount() + "/447", 55810);
				return;
			}
		}
	}

}