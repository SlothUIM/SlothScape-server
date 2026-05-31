package server.model.players.skills.fishing;

public class CatchOption {
    public final int itemId;
    public final int levelReq;
    public final int exp;
    public final int low;
    public final int high;

    public CatchOption(int itemId, int levelReq, int exp, int low, int high) {
        this.itemId = itemId;
        this.levelReq = levelReq;
        this.exp = exp;
        this.low = low;
        this.high = high;
    }

    // OSRS Skilling Success Formula
    public double getChance(int playerLevel) {
        double chance = Math.floor(
            (low * (99 - playerLevel) / 98.0) +
            (high * (playerLevel - 1) / 98.0) +
            0.5
        ) / 256.0;
        return Math.max(0.0, Math.min(1.0, chance));
    }
}