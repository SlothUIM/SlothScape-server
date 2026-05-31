package server.model.npcs.drops;

import java.util.List;
import com.google.common.base.Preconditions;

public class Drop {

    private final List<Integer> npcIds;
    private final int itemId;
    private final int minimumAmount;
    private final int maximumAmount;
    private final int weight; // new field

    public Drop(List<Integer> npcIds, int itemId, int minimumAmount, int maximumAmount, int weight) {
        Preconditions.checkArgument(minimumAmount <= maximumAmount, "Minimum amount must be <= maximum.");
        this.npcIds = npcIds;
        this.itemId = itemId;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
        this.weight = weight;
    }

    public List<Integer> getNpcIds() { return npcIds; }
    public int getItemId() { return itemId; }
    public int getMinimumAmount() { return minimumAmount; }
    public int getMaximumAmount() { return maximumAmount; }
    public int getWeight() { return weight; }
}
