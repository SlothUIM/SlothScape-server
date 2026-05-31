/**
 * 
 */
package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.world.Boundary;
import server.model.players.Player;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.combat.Special;
import server.model.players.combat.range.RangeData;
import server.util.Misc;

/**
 * @author ReverendDread
 * Apr 24, 2019
 */
public class Dawnbringer extends Special {

	/**
	 * @param cost
	 * @param accuracy
	 * @param damageModifier
	 * @param weapon
	 */
	public Dawnbringer() {
		super(3.5, 1.0, 5.0, new int[] { 22516 });
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.player.combat.Special#activate(ethos.model.entity.player.Player, ethos.model.entity.Entity, ethos.model.entity.player.combat.Damage)
	 */
	@Override
	public void activate(Player player, Entity target, Damage damage) {
		if (Boundary.isIn(player, Boundary.VERZIK)) {
			player.gfx100(1546);
			player.startAnimation(1167);
			if (target.isNPC()) {
				RangeData.fireProjectileNpc(player, target.asNPC(), 50, 70, 1547, 35, 10, 37, 10);
			}
			damage.setAmount(Misc.random(150));
		} else {
			player.getItems().deleteEquipment(-1, player.playerWeapon);
		}
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.player.combat.Special#hit(ethos.model.entity.player.Player, ethos.model.entity.Entity, ethos.model.entity.player.combat.Damage)
	 */
	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
