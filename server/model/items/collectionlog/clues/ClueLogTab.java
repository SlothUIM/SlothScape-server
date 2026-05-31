package server.model.items.collectionlog.clues;

import java.util.Map;

import server.model.items.collectionlog.CollectionLogTab;

public class ClueLogTab implements CollectionLogTab {
    private static final String[] names = {
        "Beginner Treasure Trails", "Easy Treasure Trails", "Medium Treasure Trails",
        "Hard Treasure Trails", "Elite Treasure Trails", "Master Treasure Trails",
        "Hard Treasure Trails(Rare)", "Elite Treasure Trails(Rare)",
        "Master Treasure Trails(Rare)", "Shared Treasure Trails Rewards"
    };

    private static final Map<String, String> identifierMap = Map.ofEntries(
        Map.entry("Beginner Treasure Trails", "BeginnerClue"),
        Map.entry("Easy Treasure Trails", "EasyClue"),
        Map.entry("Medium Treasure Trails", "MediumClue"),
        Map.entry("Hard Treasure Trails", "HardClue"),
        Map.entry("Elite Treasure Trails", "EliteClue"),
        Map.entry("Master Treasure Trails", "MasterClue"),
        Map.entry("Hard Treasure Trails(Rare)", "HardRareClue"),
        Map.entry("Elite Treasure Trails(Rare)", "EliteRareClue"),
        Map.entry("Master Treasure Trails(Rare)", "MasterRareClue"),
        Map.entry("Shared Treasure Trails Rewards", "SharedClue")
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
