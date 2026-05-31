package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;

public class Arclight extends Special {

	public Arclight() {
		super(5.0, 1.10, 1.1, new int[] { 19675 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.startAnimation(2890);
		player.gfx100(483);
		player.getPA().sendSound(225, 0, 3, player.EffectVolume);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (target instanceof NPC) {
			((NPC) target).gfx100(341);
		} else if (target instanceof Player) {
			((Player) target).gfx100(341);
			
			if (damage.getAmount() > 0) {
				if (((Player) target).getRunEnergy() > 0) {
					((Player) target).setRunEnergy(((Player) target).getRunEnergy() - 10);
				}
				if (!(player.getRunEnergy() > 89)) {
					player.setRunEnergy(player.getRunEnergy() + 10);
				}
			}
		}
	}

}
