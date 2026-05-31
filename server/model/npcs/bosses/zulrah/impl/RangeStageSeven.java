package server.model.npcs.bosses.zulrah.impl;

import server.event.CycleEventContainer;
import server.model.npcs.bosses.zulrah.Zulrah;
import server.model.npcs.bosses.zulrah.ZulrahLocation;
import server.model.npcs.bosses.zulrah.ZulrahStage;
import server.model.players.Player;
import server.model.players.combat.CombatType;

public class RangeStageSeven extends ZulrahStage {

	public RangeStageSeven(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 5) {
			player.getZulrahEvent().changeStage(8, CombatType.MAGE, ZulrahLocation.SOUTH);
			zulrah.getNpc().totalAttacks = 0;
			container.stop();
			return;
		}
	}

}
