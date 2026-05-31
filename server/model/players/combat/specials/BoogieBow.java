package server.model.players.combat.specials;


import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.combat.CombatType;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.combat.Special;
import server.model.players.combat.range.RangeData;
import server.util.Misc;

public class BoogieBow extends Special {

	public BoogieBow() {
	super(5.0, 1.5, 2.5, new int[] { 33531, 33536 });
	}
	
	private int PROJECTILE = 1111;

	public void activate(Player player, Entity target, Damage damage) {
			int max_hit = Misc.random(player.getCombat().rangeMaxHit());
			player.usingBow = true;
			player.bowSpecShot = 1;
			player.startAnimation(1074);
			//target.asNPC().startAnimation(435);
			player.projectileStage = 1;
			player.rangeItemUsed = player.playerEquipment[player.playerArrows];
			player.gfx100(player.getCombat().getRangeStartGFX());
		
		if (player.fightMode == 2) {
			player.attackTimer--;
	}
		
		if (target instanceof NPC && player.npcIndex > 0) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, PROJECTILE, 43, 31, 37, 10);
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, PROJECTILE, 43, 31, 53, 10);
			player.getDamageQueue().add(new Damage(target, max_hit, player.hitDelay + 1, player.playerEquipment, max_hit > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
			player.getDamageQueue().add(new Damage(target, max_hit / 2, player.hitDelay + 1, player.playerEquipment, max_hit / 2 > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));	
		} else if (target instanceof Player && player.playerIndex > 0) {//half dmg for shots fired by special vs players
	  		RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, PROJECTILE, 43, 31, 37, 10);
	  		RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, PROJECTILE, 43, 31, 53, 10);
			player.getDamageQueue().add(new Damage(target, max_hit / 2, player.hitDelay + 1, player.playerEquipment, max_hit / 2 > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));	
	  		player.getDamageQueue().add(new Damage(target, max_hit / 2, player.hitDelay + 1, player.playerEquipment, max_hit / 2 > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
	}
}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		
	}

}
