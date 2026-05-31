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

@ScriptSettings(
	npcNames = { "Deathly Mage" },
	npcIds = { 7560 }
)

public class DeathlyMage extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		int damage = getRandomMaxHit(npc, target, CombatType.MAGE, 35);
		npc.startAnimation(7855);
		handleHit(npc, target, CombatType.MAGE, new Projectile(1465, 40, 40, 0, 100, 0, 50), new Graphic (1028, 0), new Hit(Hitmark.HIT, damage, 4));
		return 5;
	}

	@Override
	public int getAttackDistance(NPC npc) {
		return 8;
	}
	
	@Override
	public boolean ignoreProjectileClipping() {
		return true;
	}

}
