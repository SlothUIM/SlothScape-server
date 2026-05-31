package server.model.players.skills.fishing;

public class SecondaryCatch {
    public final CatchOption option;

    public SecondaryCatch(int itemId, int levelReq, int exp, int low, int high) {
        this.option = new CatchOption(itemId, levelReq, exp, low, high);
    }
}