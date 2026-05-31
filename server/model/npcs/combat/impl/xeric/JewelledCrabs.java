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

/**
 * @author Patrity
 *
 */
@ScriptSettings(
	npcIds = {7576, 7577, 7578, 7579}
)
public class JewelledCrabs extends CombatScript {

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#attack(ethos.model.entity.npc.NPC, ethos.model.entity.Entity)
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		int projId = 0;
		int endGfx = 0;
		switch (npc.npcType) {
			case 7576://air
				projId = 159;
				endGfx = 160;
				break;
			case 7577://fire
				projId = 156;
				endGfx = 160;
				break;
			case 7579://water
				projId = 162;
				endGfx = 160;
				break;
			case 7578://earth
				projId = 165;
				endGfx = 160;
				break;
		}
		npc.startAnimation(2368);
		boolean melee = target.distanceToPoint(npc.getX(), npc.getY()) == 1;
		int damage = getRandomMaxHit(npc, target, melee ? CombatType.MELEE : CombatType.MAGE, melee ? 12 : 20);
		if (!melee) {
			npc.attackType1 = CombatType.MAGE;
			handleHit(npc, target, CombatType.MAGE, new Projectile(projId, 10, 40, 0, 100, 0, 50), new Graphic (endGfx, 0), new Hit(Hitmark.HIT, damage, 4));
		} else {
			npc.attackType1 = CombatType.MELEE;
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
