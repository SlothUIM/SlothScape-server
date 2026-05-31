package server.model.npcs.combat.impl.general;


import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Graphic;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.Projectile;
import server.model.npcs.combat.ScriptSettings;
import server.model.players.Player;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.util.Misc;

/**
 * 
 * @author Divine | 1:12:30 a.m. | Nov. 23, 2019
 *
 */

@ScriptSettings(
		npcNames = { "Chaos druid" },
		npcIds = { 512 }
	)

public class ChaosDruid extends CombatScript {

	private static final Projectile BIND = new Projectile(178, 20, 5, 0, 100, 0, 10);
	private static final Graphic BIND_END = new Graphic(179);

	@Override
	public int attack(NPC npc, Entity target) {
		
		int attackChance = Misc.random(1,10);
		
		if (attackChance <= 8) {
			handleHit(npc, target, CombatType.MELEE, null, null, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MELEE, Misc.random(2)), 0));
		} else {
			npc.gfx100(177);
			npc.startAnimation(710);
			handleHit(npc, target, CombatType.MAGE, BIND, BIND_END, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MAGE, 0), 2));
			target.asPlayer().freezeTimer = 5;
		}
		
		return 3;
	}

	public void handleDeath(NPC npc, Entity entity, Player player) {
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
