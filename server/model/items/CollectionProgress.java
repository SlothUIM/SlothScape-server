package server.model.items;

import server.model.players.Player;

public class CollectionProgress {
    public final int current;
    public final int total;

    public CollectionProgress(int current, int total) {
        this.current = current;
        this.total = total;
    }

    public boolean isComplete() {
        return current >= total;
    }
}
