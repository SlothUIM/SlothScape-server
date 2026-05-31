package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;

public class AbyssalBludgeon extends Special {

	public AbyssalBludgeon() {
		super(5.0, 2.10, 1.30, new int[] { 13263 });
	}

	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(3299);
		if (target instanceof Player) {
			((Player) target).gfx0(1284);
		}
		if (target instanceof NPC) {
			((NPC) target).gfx0(1284);
		}
	}

	public void hit(Player player, Entity target, Damage damage) {

	}

}
