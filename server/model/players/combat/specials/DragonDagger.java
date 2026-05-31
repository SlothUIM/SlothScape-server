package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.AttackNPC;
import server.model.players.combat.AttackPlayer;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;

public class DragonDagger extends Special {

	public DragonDagger() {
		super(2.5, 1.50, 1.10, new int[] { 1215, 1231, 5680, 5698 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx100(252);
		player.startAnimation(1062);
		if (target instanceof NPC) {
			AttackNPC.calculateCombatDamage(player, (NPC) target, CombatType.MELEE, null);
		} else if (target instanceof Player) {
			AttackPlayer.calculateCombatDamage(player, (Player) target, CombatType.MELEE, null);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
