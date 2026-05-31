package server.model.npcs.combat.impl.revenants;

import java.util.Objects;

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
 * @author Divine | 4:47:06 a.m. | Oct. 20, 2019
 *
 */

@ScriptSettings(npcIds = { 3377 })

public class RevenantDagannoth extends CombatScript {
	
	private int heal = 0;
	private Projectile MAGE_PROJECTILE = new Projectile(1415, 50, 25, 0, 100, 1, 50);
	private Projectile RANGE_PROJECTILE = new Projectile(1415, 25, 25, 0, 100, 1, 50);
	
	@Override
	public int attack(NPC npc, Entity target) {
			int randomAttack = Misc.random(1, 2);
			
		/*
		 *  chance to heal, maximum of 2 times when below 50% hp
		 */
		if (npc.getHealth().getPercentage() <= 50 && heal < 2) {
			if (Misc.random(1, 6) == 5) {
				heal++;
				npc.gfx100(1196);
				npc.getHealth().setAmount(npc.getHealth().getAmount() + (npc.getHealth().getMaximum() / 4));
			}
		}
		
		/*
		 * If the player is praying melee
		 */
		if (target.asPlayer().protectingMelee()) {
			switch (randomAttack) {
				case 1:
					handleHit(npc, target, CombatType.MAGE, MAGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MAGE, 33), 3));
					break;
				case 2:
					handleHit(npc, target, CombatType.RANGE, RANGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.RANGE, 33), 3));
					break;
			}
		} 
		
		/*
		 * if the player if praying magic
		 */
		else if (target.asPlayer().protectingMagic()) {
			switch (randomAttack) {
				case 1:
					handleHit(npc, target, CombatType.MELEE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MELEE, 33), 1));
					break;
				case 2:
					handleHit(npc, target, CombatType.RANGE, RANGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.RANGE, 33), 3));
					break;
			}
		} 
		
		/*
		 * if the player is praying range
		 */
		else if (target.asPlayer().protectingRange()) {
			switch (randomAttack) {
				case 1:
					handleHit(npc, target, CombatType.MELEE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MELEE, 33), 1));
					break;
				case 2:
					handleHit(npc, target, CombatType.MAGE, MAGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MAGE, 33), 3));
					break;
			}
		}
		
		/*
		 * if the player is not praying
		 */
		else {
			switch (randomAttack) {
				case 1:
					handleHit(npc, target, CombatType.MELEE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MELEE, 33), 1));
					break;
				case 2:
					handleHit(npc, target, CombatType.MAGE, MAGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.MAGE, 33), 3));
					break;
				case 3:
					handleHit(npc, target, CombatType.RANGE, RANGE_PROJECTILE, new Hit(Hitmark.HIT, getRandomMaxHit(npc, target, CombatType.RANGE, 33), 3));
					break;
			}
		}
		return 4;
	}

	@Override
	public void handleDeath(NPC npc, Entity entity) {
		heal = 0;
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
