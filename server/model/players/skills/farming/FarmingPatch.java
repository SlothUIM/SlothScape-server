package server.model.players.skills.farming;

import server.model.players.Player;
import server.model.players.Position;

public class FarmingPatch {
	

    public enum Type { FLOWER, HERB, FRUIT_TREE, ALLOTMENT }

    public final Type type;
    public final int index;
    public final int shift;
    public final Position location;
	
    public FarmingPatch(Player player2, Type type, int index, int shift, Position location) {
        this.type = type;
        this.index = index;
        this.shift = shift;
        this.location = location;
    }
    public static boolean isAtCatherby(Player player) {
	    return player.withinDistance(2807, 3464, player.getX(), player.getY(), 50);
	}

	// Ardougne: east patch
    public static boolean isAtArdougne(Player player) {
	    return player.withinDistance(2663, 3374, player.getX(), player.getY(), 50);
	}

	// Falador: south of bank, west patch
    public static boolean isAtFalador(Player player) {
	    return player.withinDistance(3056, 3309, player.getX(), player.getY(), 50);
	}

	// Morytania: east of Canifis
    public static boolean isAtMorytania(Player player) {
	    return player.withinDistance(3603, 3527, player.getX(), player.getY(), 50);
	}

	// Hosidius (Zeah): long distance check
    public static boolean isAtHosidius(Player player) {
	    return player.withinDistance(1736, 3553, player.getX(), player.getY(), 150);
	}

	// Farming Guild: far southwest corner
    public static  boolean isAtFarmingGuild(Player player) {
	    return player.withinDistance(1248, 3728, player.getX(), player.getY(), 150);
	}
    public static  boolean isAtChampsGuild(Player player) {
	    return player.withinDistance(3182, 3359, player.getX(), player.getY(), 20);
	}
    public static boolean isAtAnyPatch(Player player) {
    	return isAtCatherby(player) || isAtArdougne(player) || isAtArdougne(player) || isAtMorytania(player) || isAtHosidius(player) || isAtFarmingGuild(player);
    }
}
