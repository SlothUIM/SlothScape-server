package server.model.items.collectionlog.boss;

import server.model.items.collectionlog.CollectionLogTab;
import java.util.Map;
import java.util.HashMap;

public class BossLogTab implements CollectionLogTab {

    private static final String[] names = {
        "Abyssal Sire", "Barrows Chests", "Bryophyta", "Callisto", "Cerberus", "Chaos Elemental",
        "Chaos Fanatic", "Commander Zilyana", "Corporeal Beast", "Dagannoth Kings", "The Fight Caves",
        "General Graardor", "Giant Mole", "Kalphite Queen", "King Black Dragon", "Kraken", "Kree'arra",
        "K'ril Tsutsaroth", "Skotizo", "Zulrah"
    };

    private static final Map<String, String> identifierMap = Map.ofEntries(
    	Map.entry("Abyssal Sire", "Sire"),
        Map.entry("Barrows Chests", "Barrows"),
        Map.entry("Bryophyta", "Bryophyta"),
        Map.entry("Callisto", "Callisto"),
        Map.entry("Cerberus", "Cerberus"),
        Map.entry("Chaos Elemental", "ChaosEle"),
        Map.entry("Chaos Fanatic", "Fanatic"),
        Map.entry("Commander Zilyana", "CommanderZilyana"),
        Map.entry("Corporeal Beast", "Corp"),
        Map.entry("Dagannoth Kings", "DGK"),
        Map.entry("The Fight Caves", "fightCaves"),
        Map.entry("General Graardor", "GeneralGraardor"),
        Map.entry("Giant Mole", "GiantMole"),
        Map.entry("Kalphite Queen", "KQ"),
        Map.entry("King Black Dragon", "KingBlackDragon"),
        Map.entry("Kraken", "Kraken"),
        Map.entry("Kree'arra", "Kree'arra"),
        Map.entry("K'ril Tsutsaroth", "K'riltsutsaroth"),
        Map.entry("Skotizo", "Skotizo"),
        Map.entry("Zulrah", "Zulrah")
        );
    @Override
    public String[] getDisplayNames() {
        return names;
    }

    @Override
    public String getIdentifierFor(String displayName) {
        return identifierMap.getOrDefault(displayName, displayName);
    }
}
