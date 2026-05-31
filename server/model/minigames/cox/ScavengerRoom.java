package server.model.minigames.cox;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.world.World;

public class ScavengerRoom extends RaidRoom {

    // Scavenger Beast NPC IDs (Usually 7548 or 7549 in OSRS)
    private static final int SCAVENGER_BEAST_ID = 7548;

    public ScavengerRoom(RaidSession session, RaidNode node) {
        super(session, node);
    }

    @Override
    public void spawn() {
        // Scavengers usually have 2-4 beasts roaming around.
        // We pick specific 'local' coordinates within the 32x32 room chunk.
        // The getCoords() method automatically handles the rotation and grid offsets!

        int[] spawn1 = getCoords(14, 8);
        int[] spawn2 = getCoords(20, 21);
        int[] spawn3 = getCoords(16, 23);
        int z = session.getHeight() + (node.getGridZ() * 4);
        // Parameters: npcType, x, y, heightLevel, WalkingType (1=roam), HP, maxHit, attack, defence
        World.getWorld().npcHandler.spawnNpc2(SCAVENGER_BEAST_ID, spawn1[0], spawn1[1], z, 1, 150, 10, 100, 100);
        World.getWorld().npcHandler.spawnNpc2(SCAVENGER_BEAST_ID, spawn2[0], spawn2[1], z, 1, 150, 10, 100, 100);
        World.getWorld().npcHandler.spawnNpc2(SCAVENGER_BEAST_ID, spawn3[0], spawn3[1], z, 1, 150, 10, 100, 100);

        System.out.println("[CoX] Spawned Scavenger Room at Grid [" + node.getGridX() + "][" + node.getGridY() + "]");
    }

    @Override
    public void tick() {
        // Scavengers don't have complex puzzle mechanics, so this stays empty!
    }

    @Override
    public void handleDeath(NPC npc) {
        if (npc.npcType == SCAVENGER_BEAST_ID) {
            // Find who killed it
            Player killer = server.model.players.PlayerHandler.players[npc.killerId];
            if (killer == null) killer = session.getParty().getLeader(); // Failsafe if killer logged out

            int dropX = npc.getX();
            int dropY = npc.getY();
            int height = session.getHeight();

            // CoX Supply IDs: {ItemID, Amount}
            // 20849 = Mallignum root plank, 20885 = Cicely, 20881 = Stinkhorn, 20883 = Endarkened, 20889 = Cave worms
            int[][] possibleDrops = {
                    {20849, 1},
                    {20885, 1},
                    {20881, 1},
                    {20883, 1},
                    {20889, 5}
            };

            // Drop 1 to 3 random supplies!
            int dropsToRoll = 1 + server.util.Misc.random(2);
            for (int i = 0; i < dropsToRoll; i++) {
                int[] drop = possibleDrops[server.util.Misc.random(possibleDrops.length - 1)];

                // Calls your ItemHandler method directly!
               World.getWorld().itemHandler.createGroundItem(killer, drop[0], dropX, dropY, height, drop[1]);
            }

            killer.sendMessage("The scavenger beast drops some supplies.");
        }
    }

    @Override
    public boolean handleObjectClick(Player player, int objectId, int x, int y, int clickType) {
        // Scavenger rooms have troughs you can search for grubs!
        if (objectId == 29749) { // Example trough ID
            player.sendMessage("You search the trough...");
            // Add grubs to inventory
            return true;
        }
        return false;
    }
}