package server.model.players.skills.hunter.trap;

import java.util.Arrays;
import java.util.Optional;
import server.model.items.Item;

public enum PitfallData {
    LARUPIA(2908, 31, 276, new Item(10095), 939, 940),
    GRAAHK(2909, 41, 348, new Item(10097), 941, 942),
    KYATT(2907, 55, 450, new Item(10099), 937, 938);

    private final int npcId;
    private final int requirement;
    private final double experience;
    private final Item reward;
    private final int fallGfx;
    private final int jumpGfx;

    PitfallData(int npcId, int requirement, double experience, Item reward, int fallGfx, int jumpGfx) {
        this.npcId = npcId;
        this.requirement = requirement;
        this.experience = experience;
        this.reward = reward;
        this.fallGfx = fallGfx;
        this.jumpGfx = jumpGfx;
    }

    public int getNpcId() { return npcId; }
    public int getRequirement() { return requirement; }
    public double getExperience() { return experience; }
    public Item getReward() { return reward; }
    public int getFallGfx() { return fallGfx; }
    public int getJumpGfx() { return jumpGfx; }

    public static Optional<PitfallData> forNpc(int id) {
        return Arrays.stream(values()).filter(p -> p.getNpcId() == id).findFirst();
    }
}