package server.model.items.collectionlog.raid;

import server.model.items.collectionlog.CollectionLogTab;
import java.util.Map;
import java.util.HashMap;

public class RaidLogTab implements CollectionLogTab {

    private static final String[] names = {
    		"Chambers of Xeric", "Theatre of Blood", "Tombs of Amascut"
    };

    private static final Map<String, String> identifierMap = Map.ofEntries(
    	Map.entry("Chambers of Xeric", "ChambersofXeric"),
    	Map.entry("Theatre of Blood", "TheatreofBlood"),
    	Map.entry("Tombs of Amascut", "TombsofAmascut")
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
