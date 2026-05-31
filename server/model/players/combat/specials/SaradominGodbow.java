package server.model.players.combat.specials;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.combat.Special;
import server.model.players.combat.range.RangeData;
import server.util.Misc;

/**
 * 
 * @author Divine | 11:52:46 a.m. | Nov. 28, 2019
 *
 */

public class SaradominGodbow extends Special {

	public SaradominGodbow() {
		super(5.0, 1.5, 1.50, new int[] { 33140 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.bowSpecShot = 1;
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
		int damage2 = player.getCombat().rangeMaxHit();
		player.usingBow = true;
		player.rangeItemUsed = player.playerEquipment[player.playerArrows];
		if (player.playerIndex > 0 && target instanceof Player) {
			player.startAnimation(426);
			player.getDamageQueue().add(new Damage(target, damage2, 1, player.playerEquipment, damage2 > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 301, 43, 31, 37, 10);
		} else if (player.npcIndex > 0 && target instanceof NPC) {
			player.startAnimation(426);
			player.getDamageQueue().add(new Damage(target, damage2, 1, player.playerEquipment, damage2 > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 301, 43, 31, 37, 10);
		}
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}