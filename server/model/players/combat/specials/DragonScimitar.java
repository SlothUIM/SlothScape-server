package server.model.players.combat.specials;

import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
//import server.model.players.combat.melee.CombatPrayer;

public class DragonScimitar extends Special {

	public DragonScimitar() {
		super(5.5, 1.00, 1.00, new int[] { 4587 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx100(347);
		player.startAnimation(1872);
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		
		if (target instanceof Player) {
			if (damage.getAmount() > 0) {
				//CombatPrayer.resetOverHeads((Client) target);
			}
		}

	}

}
