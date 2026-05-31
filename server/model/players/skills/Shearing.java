package server.model.players.skills; // Change to match your package!

import server.model.players.Player;
import server.model.players.quests.SheepShearer;
import server.world.World;
import server.event.Event; // Using Jason's Event System
import server.Server;

public class Shearing {

    private static final int WOOLLY_SHEEP = 43; // Standard 317 Sheep ID
    private static final int SHAVED_SHEEP = 42; // Standard 317 Shaved Sheep ID
    private static final int RAW_WOOL = 1737;

    public static void shearSheep(Player c, int npcId, int npcIndex) {
        if (npcId != WOOLLY_SHEEP) {
            c.sendMessage("You can't shear that.");
            return;
        }

        if (!c.getItems().playerHasItem(SheepShearer.SHEARS)) {
            c.sendMessage("You need a pair of shears to do this.");
            return;
        }

        if (c.getItems().freeSlots() == 0) {
            c.sendMessage("Your inventory is full.");
            return;
        }

        // Face the sheep
        c.turnPlayerTo(World.getWorld().npcHandler.npcs[npcIndex].getX(), World.getWorld().npcHandler.npcs[npcIndex].getY());
        
        c.startAnimation(893); // Shearing animation
        c.getItems().addItem(RAW_WOOL, 1);
        c.sendMessage("You shear the sheep.");

        // Transform the sheep
        World.getWorld().npcHandler.npcs[npcIndex].requestTransform(SHAVED_SHEEP);

        // Set an event to turn it back into a woolly sheep after 60 seconds (100 ticks)
        World.getWorld().getEventHandler().submit(new Event<Object>("regrow_wool", World.getWorld().npcHandler.npcs[npcIndex], 100) {
            @Override
            public void execute() {
                if (World.getWorld().npcHandler.npcs[npcIndex] != null) {
                	World.getWorld().npcHandler.npcs[npcIndex].requestTransform(WOOLLY_SHEEP);
                }
                super.stop();
            }
        });
    }
}