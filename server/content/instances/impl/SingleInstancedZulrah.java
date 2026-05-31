package server.content.instances.impl;

import server.content.instances.SingleInstancedArea;
import server.model.npcs.NPCHandler;
import server.model.npcs.bosses.zulrah.Zulrah;
import server.world.Boundary;
import server.world.World;
import server.model.players.Player;

public class SingleInstancedZulrah extends SingleInstancedArea {

	public SingleInstancedZulrah(Player player, Boundary boundary, int height) {
		super(player, boundary, height);
	}

	@Override
	public void onDispose() {
		//Zulrah zulrah = player.getZulrahEvent();
		//if (zulrah.getNpc() != null) {
			//NPCHandler.kill(zulrah.getNpc().npcType, height);
		//}
		World.getWorld().getGlobalObjects().remove(11700, height);
		NPCHandler.kill(Zulrah.SNAKELING, height);
	}

}
