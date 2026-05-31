package server.model.npcs.combat.impl.general;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.bosses.wildernessboss.WildernessBossHandler;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Graphic;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.Projectile;
import server.model.npcs.combat.ScriptSettings;
import server.world.Boundary;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.util.Misc;

/**
 * 
 * @author Created by Crank Mar 17, 2019
 * 12:08:07 AM
 */

@ScriptSettings(
		npcNames = { "Derwen" },
		npcIds = { 7859 }
)

public class Derwen extends CombatScript {
	
	NPC DERWEN = WildernessBossHandler.getActiveNPC();

	@Override
	public int attack(NPC npc, Entity target) {
		int chance = Misc.random(1, 5);
		if (chance > 3) {
			if (Boundary.isIn(target, Boundary.PURSUIT_AREAS)) {
				int damage = getRandomMaxHit(npc, target, CombatType.MAGE, 43);
				npc.startAnimation(7849);
				handleHit(npc, target, CombatType.MAGE, new Projectile(-1, 30, 30, 15, 40, 0, 16), new Graphic(1511, 0),
						new Hit(Hitmark.HIT, damage, 2));
			}
		} else {
			int damage = getRandomMaxHit(npc, target, CombatType.MELEE, 26);
			npc.startAnimation(7848);
			handleHit(npc, target, CombatType.MELEE, null, null, new Hit(Hitmark.HIT, damage, 1));
		}
		return 4;
	}
	
	public static void DerwenDeath() {
		WildernessBossHandler.giveRewards();
	}

	@Override
	public int getAttackDistance(NPC npc) {
		return npc.attackType1 == CombatType.MELEE ? 4 : 20;
	}

	@Override
	public boolean ignoreProjectileClipping() {
		return true;
	}
	
	@Override
	public int getFollowDistance(NPC npc) {
		return 10;
	}
	
	@Override
	public boolean followClose(NPC npc) {
		return false;
	}

}
