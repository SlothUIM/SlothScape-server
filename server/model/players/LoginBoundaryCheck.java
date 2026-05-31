package server.model.players;

import server.model.players.skills.construction.ConstructionData;
import server.world.Boundary;
import server.world.Location;
public enum LoginBoundaryCheck {

    // Format: NAME(Forbidden_Boundary, Safe_Return_Location)

    CONSTRUCTION_INSTANCE(
            Boundary.of(ConstructionData.BASE_X, ConstructionData.BASE_Y,
                    ConstructionData.BASE_X + 128, ConstructionData.BASE_Y + 128),
            new Location(2893, 3464, 0)
    ),

    // Reusing the constants you already have in Boundary.java!
    FIGHT_CAVES(
            Boundary.FIGHT_CAVE,
            new Location(2438, 5168, 0)
    ),

    PEST_CONTROL_GAME(
            Boundary.PEST_CONTROL_AREA,
            new Location(2657, 2639, 0)
    ),

    RAIDS_CHAMBERS(
            Boundary.RAIDS,
            new Location(1246, 3559, 0) // Example Mount Quidamortem return
    );

    private final Boundary forbiddenZone;
    private final Location safeLocation;

    LoginBoundaryCheck(Boundary forbiddenZone, Location safeLocation) {
        this.forbiddenZone = forbiddenZone;
        this.safeLocation = safeLocation;
    }

    public Boundary getForbiddenZone() {
        return forbiddenZone;
    }

    public Location getSafeLocation() {
        return safeLocation;
    }
}