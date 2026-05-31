package server.model.items.collectionlog.other;

import java.util.Map;

import server.model.items.collectionlog.CollectionLogTab;

public class OtherLogTab implements CollectionLogTab {

    private static final String[] names = {
    		"Aerial Fishing", 
    		"All pets", 
    		"Champion's Challenge",
			"Chaos Druids",
			"Chompy Bird Hunting",
			"Creature Creation",
			"Cyclopes",
			"Miscellaneous",
			"Random Events"
    };

    private static final Map<String, String> identifierMap = Map.ofEntries(
    	Map.entry("Aerial Fishing", "AerialFish"),
        Map.entry("All pets", "pets"),
        Map.entry("Champion's Challenge", "ChampsChallenge"),
        Map.entry("Chaos Druids", "ChaosDruid"),
        Map.entry("Chompy Bird Hunting", "chompy"),
        Map.entry("Creature Creation", "creation"),
        Map.entry("Cyclopes", "Cyclopes"),
        Map.entry("Miscellaneous", "Misc"),
        Map.entry("Random Events", "RandomEvent")
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
