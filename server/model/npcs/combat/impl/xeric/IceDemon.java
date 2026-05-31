/**
 * 
 */
package server.model.npcs.combat.impl.xeric;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Graphic;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.Projectile;
import server.model.npcs.combat.ScriptSettings;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.util.Misc;

/**
 * @author Patrity
 *
 */
@ScriptSettings(
		npcNames = { },
		npcIds = { 7585 }
	)
public class IceDemon extends CombatScript {

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#attack(ethos.model.entity.npc.NPC, ethos.model.entity.Entity)
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		int barrageChance = Misc.random(1, 100);
		boolean melee = target.distanceToPoint(npc.getX(), npc.getY()) <= 2;
		int damage = getRandomMaxHit(npc, target, melee ? CombatType.MELEE : CombatType.MAGE, melee ? 28 : 30);
		npc.startAnimation(64);
		if(barrageChance >= 90 || !melee) {
			npc.attackType1 = CombatType.MAGE;
			handleHit(npc, target, CombatType.MAGE, new Projectile(368, 0, 15, 0, 100, 0, 50), new Graphic (369, 0), new Hit(Hitmark.HIT, damage, 4));
			target.asPlayer().sendMessage("You have been frozen!");
			target.asPlayer().freezeTimer = 8;
		} else {
			handleHit(npc, target, CombatType.MELEE, new Hit(Hitmark.HIT, damage, 1));
		}
		return 5;
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#getAttackDistance(ethos.model.entity.npc.NPC)
	 */
	@Override
	public int getAttackDistance(NPC npc) {
		return npc.attackType1 == CombatType.MELEE ? 1 : 8;
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#ignoreProjectileClipping()
	 */
	@Override
	public boolean ignoreProjectileClipping() {
		return true;
	}
	
	@Override
	public void attackStyleChange(NPC npc, Entity target) {
		int distance = npc.distanceToPoint(target.getX(), target.getY());
		if (distance > 1) {
			npc.attackType1 = CombatType.MAGE;
		} else {
			npc.attackType1 = CombatType.MELEE;
		}
	}

}