package server.model.players.combat.effects.bolts;

import java.util.Optional;

import server.model.HealthStatus;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.DamageEffect;
import server.model.players.combat.range.RangeExtras;
import server.util.Misc;

public class EmeraldBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 752, true);
		defender.getHealth().proposeStatus(HealthStatus.POISON, 5, Optional.empty());
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() == null) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 752, true);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9241);
	}

}
