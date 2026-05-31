package server.model.npcs.combat.impl.general;

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
 * 
 * @author Divine | 1:47:41 p.m. | Sep. 20, 2019
 *
 */

/*
 * Handles combat for 3rd age dragons
 */

@ScriptSettings(
		npcNames = { "3rd age dragon" },
		npcIds = { 3838 }
	)

public class ThirdAgeDragon extends CombatScript {
	//Magic attack gfx
	private Projectile MAGIC = new Projectile(396, 100, 25, 0, 100, 250, 50);
	private Graphic MAGIC_END = new Graphic(428);
	//dragonfire gfx
	private Projectile DRAGON_FIRE = new Projectile(1155, 80, 10, 0, 100, 0, 50);
	private Graphic DRAGON_FIRE_END = new Graphic(1154);
	
	@Override
	public int attack(NPC npc, Entity target) {
		int attackChance = Misc.random(1, 10);
		npc.startAnimation(7871);
		
		/*
		 * The magic attack
		 */
		if (attackChance <= 8) {
			handleHit(npc, target, CombatType.MAGE, MAGIC, MAGIC_END, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MAGE, 35), 8));
			return 8;
		} 
		//The dragon fire breathe attack
		else {
			handleHit(npc, target, CombatType.DRAGON_FIRE, DRAGON_FIRE, DRAGON_FIRE_END, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.DRAGON_FIRE, 55), 4));
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
		return 10;
	}
	
	@Override
	public boolean ignoreCollision() {
		return false;
	}
	
	@Override
	public boolean isAggressive(NPC npc) {
		return true;
	}

	@Override
	public boolean followClose(NPC npc) {
		return true;
	}

}
