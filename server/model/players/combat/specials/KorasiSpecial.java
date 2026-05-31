package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.combat.Special;
import server.util.Misc;

public class KorasiSpecial extends Special {

	public KorasiSpecial() {
		super(5.0, 1.50, 1.50, new int[] { 33370 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(1711);
		if (damage.getAmount() > 0) {
			player.getDamageQueue().add(new Damage(target, player.getCombat().magicMaxHit() + (1 + Misc.random(30)), 2, player.playerEquipment, Hitmark.HIT, CombatType.MAGE));
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		target.asNPC().gfx0(1196);
	}

}