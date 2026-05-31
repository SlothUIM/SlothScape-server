/**
 * 
 */
package server.model.npcs.combat.impl.general;

import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.combat.CombatScript;
import server.model.npcs.combat.Hit;
import server.model.npcs.combat.ScriptSettings;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;

/**
 * @author ReverendDread
 * Mar 9, 2019
 */
@ScriptSettings(
	npcNames = { "Cow" },
	npcIds = { }
)
public class ExampleScript extends CombatScript {
	
	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#attack(ethos.model.entity.npc.NPC, ethos.model.entity.Entity)
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		int damage = getRandomMaxHit(npc, target, CombatType.MELEE, 1);
		npc.startAnimation(5849);
		handleHit(npc, target, CombatType.MELEE, null, null, new Hit(Hitmark.HIT, damage, 2));
		return 4;
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#getAttackDistance(ethos.model.entity.npc.NPC)
	 */
	@Override
	public int getAttackDistance(NPC npc) {
		return 1;
	}

	/* (non-Javadoc)
	 * @see ethos.model.entity.npc.combat.CombatScript#ignoreProjectileClipping()
	 */
	@Override
	public boolean ignoreProjectileClipping() {
		return false;
	}

}
