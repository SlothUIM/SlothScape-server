package server.model.players.content;

import server.model.players.Player;
import server.util.Misc;
import java.util.Map;

public class BossLog {

    private static final int NAME_START_ID = 32472;
    private static final int KILLS_START_ID = 32512;
    private static final int STREAK_START_ID = 32552;

    private static final String[] BOSS_NAMES = {
        "Abyssal Sire", "Alchemical Hydra", "Barrows Brothers", "Bryophyta",
        "Callisto", "Cerberus", "Chaos Elemental", "Chaos Fanatic",
        "Commander Zilyana", "Corporeal Beast", "Crazy Archaeologist", "Dagannoth Prime",
        "Dagannoth Rex", "Dagannoth Supreme", "Deranged Archaeologist", "General Graardor",
        "Giant Mole", "Guardian Ancients", "Hespori", "Kalphite Queen",
        "King Black Dragon", "Kraken", "Kree'Arra", "K'ril Tsutsaroth",
        "Mimic", "Nex", "Nightmare", "Obor",
        "Phantom Muspah", "Sarachnis", "Scorpia", "Skotizo",
        "Tempoross", "The Gauntlet", "The Mimic", "Thermonuclear Smoke Devil",
        "Venenatis", "Vorkath", "Wintertodt", "Zulrah"
    };

    public static void open(Player c) {
        // Access the tracker map from your NPCDeathTracker class
        Map<String, Integer> killsMap = c.getNpcDeathTracker().getTracker();

        for (int i = 0; i < 40; i++) {
            if (i < BOSS_NAMES.length) {
                String name = BOSS_NAMES[i];
                
                // Get kills from your existing tracker
                // Using getOrDefault to avoid null issues if they haven't killed the boss yet
                int kills = killsMap.getOrDefault(name.toLowerCase(), 0);
                
                // If you haven't implemented streaks yet, we'll just show "N/A" or 0
                int streak = 0; 

                c.getPA().sendFrame126(name, NAME_START_ID + i);
                c.getPA().sendFrame126(kills + "", KILLS_START_ID + i);
                c.getPA().sendFrame126(streak + "", STREAK_START_ID + i);
            } else {
                // Clear extra rows if your list is shorter than 40
                c.getPA().sendFrame126("", NAME_START_ID + i);
                c.getPA().sendFrame126("", KILLS_START_ID + i);
                c.getPA().sendFrame126("", STREAK_START_ID + i);
            }
        }
        
        c.getPA().showInterface(32460);
    }
}