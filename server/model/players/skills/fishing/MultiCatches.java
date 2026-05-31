package server.model.players.skills.fishing;

import java.util.*;

public class MultiCatches extends SecondaryCatch {
    public final List<CatchOption> options;

    public MultiCatches(int[] itemIds, int levelReq, int exp, int[] lows, int[] highs) {
        super(-1, levelReq, exp, -1, -1); // Not used directly
        options = new ArrayList<>();
        for (int i = 0; i < itemIds.length; i++) {
            options.add(new CatchOption(itemIds[i], levelReq, exp, lows[i], highs[i]));
        }
    }

    public int getRandomCatch(int playerLevel) {
        for (CatchOption opt : options) {
            if (Math.random() <= opt.getChance(playerLevel)) {
                return opt.itemId;
            }
        }
        return -1;
    }
}