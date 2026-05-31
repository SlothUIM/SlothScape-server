package server.model.players.packets;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.firemake.Firemaking;
import server.world.World;

public class GroundItemClick implements server.model.players.PacketType {
    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        int itemX = player.getInStream().readSignedWordBigEndian();
        int itemY = player.getInStream().readSignedWordBigEndianA();
        int itemId = player.getInStream().readSignedWordA();

        if (itemId == -1) {
            return;
        }
        if (Firemaking.lightGroundLog(player, itemId, itemX, itemY)) {
            return; // Firemaking took control, stop the packet!
        }
        if (itemId == 10006) { // Bird Snare
            // 1. Walk to the trap
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getX() == itemX && player.getY() == itemY) {
                        // 2. Pick it up instantly
                        World.getWorld().getItemHandler().removeGroundItem(player, itemId, itemX, itemY, false);
                        player.getItems().addItem(itemId, 1);

                        // 3. Try to lay it again
                        server.model.players.skills.hunter.Hunter.lay(player, new server.model.players.skills.hunter.trap.impl.BirdSnare(player));
                        container.stop();
                    } else if (!player.goodDistance(player.getX(), player.getY(), itemX, itemY, 4)) {
                        container.stop();
                    }
                }
                @Override
                public void stop() {}
            }, 1);
            return; // Stop the packet!
        }
        if (itemId == 10008) { // Bird Snare
            // 1. Walk to the trap
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getX() == itemX && player.getY() == itemY) {
                        // 2. Pick it up instantly
                        World.getWorld().getItemHandler().removeGroundItem(player, itemId, itemX, itemY, false);
                        player.getItems().addItem(itemId, 1);

                        // 3. Try to lay it again
                        server.model.players.skills.hunter.Hunter.lay(player, new server.model.players.skills.hunter.trap.impl.BoxTrap(player));
                        container.stop();
                    } else if (!player.goodDistance(player.getX(), player.getY(), itemX, itemY, 4)) {
                        container.stop();
                    }
                }
                @Override
                public void stop() {}
            }, 1);
            return; // Stop the packet!
        }
        player.sendMessage("Ground item click: itemId=" + itemId + ", x=" + itemX + ", y=" + itemY);
    }
}