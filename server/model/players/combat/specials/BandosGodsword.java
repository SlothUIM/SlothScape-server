package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.Client;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
import server.model.players.skills.Skill;

public class BandosGodsword extends Special {

	public BandosGodsword() {
		super(5.0, 1.35, 1.21, new int[] { 11804 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(7642);
		player.gfx0(1212);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (target instanceof Player) {
			Player pTarget = (Player) target;
			if (damage.getAmount() > 0) {
				if (pTarget.getSkills().getLevel(Skill.DEFENCE) > 0)
					pTarget.getSkills().decreaseLevelOrMin(pTarget.getSkills().getLevel(Skill.DEFENCE) / 3, Skill.DEFENCE);
					//pTarget.getSkills().refreshSkill(1);
			}
		}
	}

}
