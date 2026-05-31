package server.model.npcs.bosses.zulrah;

import server.event.CycleEvent;
import server.model.players.Player;

public abstract class ZulrahStage implements CycleEvent {

	protected Zulrah zulrah;

	protected Player player;

	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
