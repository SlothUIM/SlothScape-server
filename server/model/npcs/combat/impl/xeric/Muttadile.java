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

@ScriptSettings(
	npcNames = {}, 
	npcIds = { 7563 }
)

/*
 * anims 7422 dragonfire 7421 range/bite? 7424 ground slam
 */
public class Muttadile extends CombatScript {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ethos.model.entity.npc.combat.CombatScript#attack(ethos.model.entity.npc.NPC,
	 * ethos.model.entity.Entity)
	 */
	@Override
	public int attack(NPC npc, Entity target) {

		boolean dragonFire = false;
		if (Misc.random(1, 100) >= 80)
			dragonFire = true;
		int damage = getRandomMaxHit(npc, target, dragonFire ? CombatType.DRAGON_FIRE : CombatType.MAGE,
				dragonFire ? 30 : 38);

		if (!dragonFire) {
			npc.startAnimation(7421);
			npc.attackType1 = CombatType.MAGE;
			handleHit(npc, target, CombatType.MAGE, new Projectile(1293, 20, 0, 0, 100, 0, 50), new Graphic(1294, 0),
					new Hit(Hitmark.HIT, damage, 4));

		} else {
			npc.startAnimation(7422);
			handleHit(npc, target, CombatType.DRAGON_FIRE, new Projectile(393, 20, 40, 0, 100, 0, 50),
					new Graphic(430, 0), new Hit(Hitmark.HIT, damage, 4));
			target.asPlayer().sendMessage("You've been hit by Muttadile's Dragonfire!");
		}
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ethos.model.entity.npc.combat.CombatScript#getAttackDistance(ethos.model.
	 * entity.npc.NPC)
	 */
	@Override
	public int getAttackDistance(NPC npc) {
		return 8;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ethos.model.entity.npc.combat.CombatScript#ignoreProjectileClipping()
	 */
	@Override
	public boolean ignoreProjectileClipping() {
		return true;
	}
}