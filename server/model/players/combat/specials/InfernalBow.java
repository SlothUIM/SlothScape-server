package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
import server.model.players.combat.range.RangeData;

/**
 * 
 * @author Divine | 10:28:27 a.m. | Nov. 28, 2019
 *
 */
public class InfernalBow extends Special {

	public InfernalBow() {
		super(2.5, 2.0, 1.80, new int[] { 33281, 33763 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.bowSpecShot = 1;
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
		player.usingBow = true;
		player.rangeItemUsed = player.playerEquipment[player.playerArrows];
		if (player.playerIndex > 0 && target instanceof Player) {
			player.startAnimation(426);
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 1611, 43, 31, 37, 10);
		} else if (player.npcIndex > 0 && target instanceof NPC) {
			player.startAnimation(426);
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 1611, 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		target.asNPC().gfx100(1676);
	}

}
