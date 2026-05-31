package server.model.players.combat.effects.bolts;

import server.model.npcs.NPC;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.DamageEffect;
import server.model.players.combat.range.RangeExtras;
import server.model.players.skills.Skill;
import server.util.Misc;

public class SapphireBoltSpecial implements DamageEffect {

	@Override
	public void execute(Player attacker, Player defender, Damage damage) {
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 751, false);

		if (attacker.playerIndex > 0) {
			//defender.getPA().decreaseLevelOrMin(2, 0, Skill.PRAYER);
			defender.getPA().refreshSkill(5);
			defender.sendMessage("Your prayer has been lowered!");
			//attacker.getPA().increasLevelOrMax(2, Skill.PRAYER);
			attacker.getPA().refreshSkill(5);
		}
	}

	@Override
	public void execute(Player attacker, NPC defender, Damage damage) {
		if (defender.getDefinition().getNpcName() == null) {
			return;
		}
		int change = Misc.random((int) (damage.getAmount()));
		damage.setAmount(change);
		RangeExtras.createCombatGraphic(attacker, defender, 751, false);
	}

	@Override
	public boolean isExecutable(Player operator) {
		return RangeExtras.boltSpecialAvailable(operator, 9240);
	}

}