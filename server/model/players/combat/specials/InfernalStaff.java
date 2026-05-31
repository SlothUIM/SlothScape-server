package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
import server.model.players.combat.range.RangeData;

/**
 * 
 * @author Divine | 10:28:27 a.m. | Nov. 28, 2019
 *
 */
public class InfernalStaff extends Special {

	public InfernalStaff() {
		super(2.5, 2.0, 1.80, new int[] { 33277, 33761 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
			player.startAnimation(1711);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		target.asNPC().gfx100(1676);
	}

}