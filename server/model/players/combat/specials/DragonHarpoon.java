package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
import server.model.players.skills.Skill;

public class DragonHarpoon extends Special {
	public DragonHarpoon() {
		super(10.0, 2.0, 2.0, new int[] { 21028 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.forcedChat("Here fishy fishies!");
		player.gfx0(246);
		//player.getPA().increaseLevel(3, Skill.FISHING);
		player.getPA().refreshSkill(Skill.FISHING.getId());
	}


	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}
}
