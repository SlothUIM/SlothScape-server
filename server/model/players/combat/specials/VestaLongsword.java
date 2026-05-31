package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;

public class VestaLongsword extends Special {

	public VestaLongsword() {
		super(2.5, 1.3, 1.15, new int[] { 13899 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7295);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
