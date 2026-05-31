package server.model.npcs.combat.impl.starterdungeon;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.Projectile;
import server.model.npcs.combat.ScriptSettings;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.util.Misc;

/**
 * 
 * @author Divine | 11:29:14 p.m. | Oct. 15, 2019
 *
 */

@ScriptSettings(npcIds = { 5126 })

//T1
public class MutatedRat extends CombatScript {
	
	@Override
	public int attack(NPC npc, Entity target) {
		int randomAttack = Misc.random(1, 4);
		
		if (randomAttack == 1) {
			npc.startAnimation(6514);
			handleHit(npc, target, CombatType.RANGE, new Projectile(1078, 50, 35, 0, 100, 1, 50), null, new Hit(Hitmark.HIT, 1, 3));
			return 4;
		} else {
			npc.startAnimation(6513);
			handleHit(npc, target, CombatType.MELEE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MELEE, 1), 2));
		}
		return 4;
	}

	@Override
	public void handleDeath(NPC npc, Entity entity) {
	}
	
	@Override
	public int getAttackDistance(NPC npc) {
		return 20;
	}
	
	@Override
	public boolean ignoreProjectileClipping() {
		return true;
	}

	@Override
	public int getFollowDistance(NPC npc) {
		return 20;
	}
	
	@Override
	public boolean ignoreCollision() {
		return false;
	}
	
	@Override
	public boolean isAggressive(NPC npc) {
		return false;
	}

	@Override
	public boolean followClose(NPC npc) {
		return true;
	}

}

