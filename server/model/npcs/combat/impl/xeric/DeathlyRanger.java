/**
 * 
 */
package server.model.npcs.combat.impl.xeric;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.Projectile;
import server.model.npcs.combat.ScriptSettings;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;

/**
 * @author Patrity
 *
 */
@ScriptSettings(
	npcNames = { },
	npcIds = {7559}
)
public class DeathlyRanger extends CombatScript {

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#attack(ethos.model.entity.npc.NPC, ethos.model.entity.Entity)
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		int damage = getRandomMaxHit(npc, target, CombatType.RANGE, 20);
		npc.startAnimation(426);
		handleHit(npc, target, CombatType.MAGE, new Projectile(1120, 40, 40, 0, 100, 0, 50), null, new Hit(Hitmark.HIT, damage, 4));		
		return 5;
		
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#getAttackDistance(ethos.model.entity.npc.NPC)
	 */
	@Override
	public int getAttackDistance(NPC npc) {
		// TODO Auto-generated method stub
		return 8;
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#ignoreProjectileClipping()
	 */
	@Override
	public boolean ignoreProjectileClipping() {
		// TODO Auto-generated method stub
		return true;
	}

}
