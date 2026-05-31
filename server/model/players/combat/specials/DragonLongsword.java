package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;

public class DragonLongsword extends Special {

	public DragonLongsword() {
		super(2.5, 1.25, 1.15, new int[] { 1305 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx100(248);
		player.startAnimation(1058);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}