package server.model.players.packets;

import server.model.items.UseItem;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.packets.dialogue.npc.TindelMarchant;
import server.model.players.PacketType;
import server.model.players.skills.hunter.trap.impl.Pitfall;

public class ItemOnNpc implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int itemId = c.getInStream().readSignedWordA();
		int i = c.getInStream().readSignedWordA();
		int slot = c.getInStream().readSignedWordBigEndian();
		int npcId = NPCHandler.npcs[i].npcType;
		NPC npc = NPCHandler.npcs[i];
		if (!c.getItems().playerHasItem(itemId, 1, slot)) {
			return;
		}
		if (npcId == 3838) { // Tindel Marchant
            if (itemId == TindelMarchant.RUSTY_SWORD 
             || itemId == TindelMarchant.RUSTY_SCIMITAR) {
                TindelMarchant.identifyWeapon(c, itemId);
            } else {
                c.sendMessage("Tindel only knows how to identify rusty swords and scimitars.");
            }
            return;
        }
		// Teasing Stick on Pitfall Animals
		if (itemId == 10029) {
			if (npc.npcType == 2908 || npc.npcType == 2907 || npc.npcType == 2909) {
				Pitfall.teaseAnimal(c, npc);
				return;
			}
		}
		switch(npcId){
			case 2693:
			case 2694:
			case 2695:
			case 2696:
			case 2697:
			case 2698:
			case 2699:
			case 2786:
				NPCHandler.npcs[i].shearSheep(c, 1735, 1737, 893, npcId, 2692, 50);
				break;
		}
		UseItem.ItemonNpc(c, itemId, npcId, slot);
	}
}
