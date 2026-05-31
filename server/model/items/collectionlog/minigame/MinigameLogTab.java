package server.model.items.collectionlog.minigame;

import java.util.Map;

import server.model.items.collectionlog.CollectionLogTab;

public class MinigameLogTab implements CollectionLogTab {

    private static final String[] names = {
    		"Barbarian Assault", 
				"Brimhaven Agility Arena",
				"Castlewars", 
				"Fishing Trawler",
				"Giants Foundry",
				"Gnome Restaurant",
				"Last Man Standing",
				"Magic Training Arena",
				"Mahogany Homes",
				"Pest Control",
				"Rogues' Den",
				"Shades of Mort'ton",
				"Soul Wars",
				"Temple Trekking",
				"Tithe Farm",
				"Volcanic Mine"
    };

    private static final Map<String, String> identifierMap = Map.ofEntries(
    	Map.entry("Barbarian Assault", "BarbAss"),
        Map.entry("Brimhaven Agility Arena", "BrimAgil"),
        Map.entry("Castlewars", "CastleWars"),
        Map.entry("Fishing Trawler", "Trawler"),
        Map.entry("Giants Foundry", "Foundry"),
        Map.entry("Gnome Restaurant", "GnomeRes"),
        Map.entry("Last Man Standing", "LastMan"),
        Map.entry("Magic Training Arena", "MageArena"),
        Map.entry("Mahogany Homes", "MahoHomes"),
        Map.entry("Pest Control", "PestControl"),
        Map.entry("Rogues' Den", "Rogues"),
        Map.entry("Shades of Mort'ton", "ShadesMort"),
        Map.entry("Soul Wars", "Soul"),
        Map.entry("Temple Trekking", "Trekking"),
        Map.entry("Tithe Farm", "Tithe"),
        Map.entry("Volcanic Mine", "Volcanic")
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
