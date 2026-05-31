package server.model.players.combat.effects.bolts;

import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.DamageEffect;
import server.model.players.combat.range.RangeExtras;
import server.util.Misc;

public class OpalBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount() * 1.25));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 749, false);
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() == null) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount() * 1.25));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 749, false);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9236);
	}

}
