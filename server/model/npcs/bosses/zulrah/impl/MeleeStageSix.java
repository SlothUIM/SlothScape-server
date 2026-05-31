package server.model.npcs.bosses.zulrah.impl;

import server.event.CycleEventContainer;
import server.model.npcs.bosses.zulrah.Zulrah;
import server.model.npcs.bosses.zulrah.ZulrahLocation;
import server.model.npcs.bosses.zulrah.ZulrahStage;
import server.model.players.Player;
import server.model.players.combat.CombatType;

public class MeleeStageSix extends ZulrahStage {

	public MeleeStageSix(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		if (zulrah.getNpc().totalAttacks > 1 && zulrah.getNpc().attackTimer == 9) {
			player.getZulrahEvent().changeStage(7, CombatType.RANGE, ZulrahLocation.EAST);
			zulrah.getNpc().totalAttacks = 0;
			zulrah.getNpc().setFacePlayer(true);
			container.stop();
			return;
		}
	}

}
