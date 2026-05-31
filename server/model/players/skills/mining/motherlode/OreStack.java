package server.model.players.skills.mining.motherlode;


// OreStack data class
public class OreStack {
    public int id;
    public int amount;
    public int xp;
    public double chancePercent;
    public String name;

    public OreStack(int id, int chancePercent) {
        this.id = id;
        this.amount = 1;
        this.xp = calculateOreXP(id);
        this.chancePercent = chancePercent;
        this.name = getOreName(id);
    }
    public static int calculateOreXP(int oreId) {
        switch (oreId) {
            case 451: return 125; // Runite
            case 449: return 80;  // Adamant
            case 447: return 50;  // Mithril
            case 440: return 35;  // Iron
            case 453: return 17;  // Coal
            case 12012: return 0; // Golden nugget
            default: return 0;
        }
    }
    public static String getOreName(int id) {
        switch (id) {
            case 451: return "Runite";
            case 449: return "Adamantite";
            case 447: return "Mithril";
            case 440: return "Iron";
            case 453: return "Coal";
            case 12012: return "Golden nugget";
            default: return "Unknown ore";
        }
    }
}