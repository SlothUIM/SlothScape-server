package server.model.items.collectionlog;

import java.util.Collection;
import java.util.Map;

import server.model.items.collectionlog.boss.SireLog;
import server.model.items.collectionlog.boss.BarrowsLog;
import server.model.items.collectionlog.boss.GraardorLog;
import server.model.items.collectionlog.boss.ZilyanaLog;
import server.model.items.collectionlog.boss.KrilLog;
import server.model.items.collectionlog.boss.CorpLog;
import server.model.items.collectionlog.boss.KreeLog;
import server.model.items.collectionlog.boss.KrakenLog;
import server.model.items.collectionlog.boss.CallistoLog;
import server.model.items.collectionlog.boss.KBDLog;
import server.model.items.collectionlog.boss.CerberusLog;
import server.model.items.collectionlog.boss.SkotizoLog;
import server.model.items.collectionlog.boss.DGKLog;
import server.model.items.collectionlog.boss.BryoLog;
import server.model.items.collectionlog.boss.FcLog;
import server.model.items.collectionlog.boss.FanaticLog;
import server.model.items.collectionlog.boss.ChaosEleLog;
import server.model.items.collectionlog.boss.GiantMoleLog;
import server.model.items.collectionlog.boss.KQLog;
import server.model.items.collectionlog.boss.ZulrahLog;
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

public class CollectionLogRegistry {

    public final Map<String, CollectionLogData> logs = Map.ofEntries(
        Map.entry("Sire", new SireLog()),
        Map.entry("Barrows", new BarrowsLog()),
        Map.entry("GeneralGraardor", new GraardorLog()),
        Map.entry("CommanderZilyana", new ZilyanaLog()),
        Map.entry("K'riltsutsaroth", new KrilLog()),
        Map.entry("Corp", new CorpLog()),
        Map.entry("Kree'arra", new KreeLog()),
        Map.entry("Kraken", new KrakenLog()),
        Map.entry("Callisto", new CallistoLog()),
        Map.entry("KingBlackDragon", new KBDLog()),
        Map.entry("Cerberus", new CerberusLog()),
        Map.entry("Bryophyta", new BryoLog()),
        Map.entry("fightCaves", new FcLog()),
        Map.entry("Fanatic", new FanaticLog()),
        Map.entry("ChaosEle", new ChaosEleLog()),
        Map.entry("GiantMole", new GiantMoleLog()),
        Map.entry("KQ", new KQLog()),
        Map.entry("DGK", new DGKLog()),
        Map.entry("Skotizo", new SkotizoLog()),
        Map.entry("Zulrah", new ZulrahLog()),
        
        Map.entry("creation", new CreatureCreationLog()),
        Map.entry("AerialFish", new AerialFishingLog()),
        Map.entry("Cyclopes", new CyclopesLog()),
        Map.entry("RandomEvent", new RandomEventLog()),
        Map.entry("chompy", new ChompyLog()),
        Map.entry("pets", new PetLog()),
        Map.entry("Misc", new MiscLog()),
        Map.entry("ChaosDruid", new ChaosDruidLog()),
        Map.entry("ChampsChallenge", new ChampsLog()),
        
        Map.entry("BeginnerClue", new BeginnerClueLog()),
        Map.entry("EasyClue", new EasyClueLog()),
        Map.entry("MediumClue", new MediumClueLog()),
        Map.entry("HardClue", new HardClueLog()),
        Map.entry("EliteClue", new EliteClueLog()),
        Map.entry("MasterClue", new MasterClueLog()),
        Map.entry("HardRareClue", new HardClueLogRare()),
        Map.entry("EliteRareClue", new EliteClueLogRare()),
        Map.entry("MasterRareClue", new MasterClueLogRare()),
        Map.entry("SharedClue", new SharedClueLog()),
        
        Map.entry("Chambers Of Xeric", new XericLog()),
        Map.entry("Theatre of Blood", new BloodLog()),
        Map.entry("Tombs of Amascut", new AmascutLog())
        // Add more...
    );
    public Collection<CollectionLogData> getAllLogs() {
        return logs.values();
    }

    public CollectionLogData getLog(String id) {
        return logs.get(id);
    }
}
