package server.model.players.combat.range;

import java.util.stream.IntStream;

import server.Config;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.npcs.combat.CombatScript;
import server.world.Boundary;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.combat.Hitmark;
//import server.model.players.combat.monsterhunt.MonsterHunt.Npcs;
import server.model.players.skills.Skill;
import server.model.items.Item;
import server.util.Misc;

public class RangeMaxHit extends RangeData {

	public static int calculateRangeDefence(Player c) {
		int defenceLevel = c.getSkills().getLevel(Skill.DEFENCE) ;
		if (c.prayerActive[0]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.15;
		} else if (c.prayerActive[26]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.2;
		} else if (c.prayerActive[27]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.25;
		} else if (c.prayerActive[28]) {
			defenceLevel += c.getSkills().getLevel(Skill.DEFENCE) * 0.25;
		}
		return defenceLevel + (c.playerBonus[9] / 2);
	}

	public static int calculateRangeAttack(Player c) {
		int rangeLevel = c.getSkills().getLevel(Skill.RANGED);
		if (c.playerIndex > 0) {
			rangeLevel *= c.specAccuracy;
		}
		if (c.fullVoidRange()) {
			rangeLevel += c.getSkills().getLevel(Skill.RANGED) * 0.1;
		}
		/*if (c.fullVoidSupremeRange()) {
			rangeLevel += c.getSkills().getActualLevel(Skill.RANGED) * 0.15;
		}*/
		if (c.prayerActive[3]) {
			rangeLevel *= 1.05;
		} else if (c.prayerActive[11]) {
			rangeLevel *= 1.10;
		} else if (c.prayerActive[19]) {
			rangeLevel *= 1.15;
		} else if (c.prayerActive[27]) {
			rangeLevel *= 1.23;
		}
		if (c.npcIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcIndex];
			if (npc != null && c.getSlayer().getTask().get().matches(npc.getDefinition().getNpcName()) || npc.npcType == 7413) {
				boolean SLAYER_HELM = IntStream.of(c.IMBUED_SLAYER_HELMETS).anyMatch(i -> c.getItems().isWearingItem(i));
				if (!c.getItems().isWearingItem(4081) && SLAYER_HELM) {
					rangeLevel *= 1.15;
				}
			}
			if (c.getItems().isWearingItem(4081, c.playerAmulet)) {
				if (Misc.linearSearch(Config.UNDEAD_IDS, npc.npcType) != -1) {
					rangeLevel *= 1.15;
				}
			}
			if (c.getItems().isWearingItem(12018, c.playerAmulet)) {
				if (Misc.linearSearch(Config.UNDEAD_IDS, npc.npcType) != -1) {
					rangeLevel *= 1.20;
				}
			}

		}

		if (c.fullVoidRange() && c.specAccuracy > 1.15) {
			rangeLevel *= 1.75;
		}
		return (int) (rangeLevel + (c.playerBonus[4] * 1.95));
	}
	
	public static boolean wearingCrystalBow(Player c) {
		return c.playerEquipment[c.playerWeapon] == 23901 || c.playerEquipment[c.playerWeapon] == 23902 || c.playerEquipment[c.playerWeapon] == 23903 || c.playerEquipment[c.playerWeapon] == 23983;
	}

	public static int maxHit(Player c) {
	    // -----------------------------------------------------------
	    // Step 1: Effective Ranged Strength (Invisible Levels)
	    // -----------------------------------------------------------
	    int rangeLevel = c.getSkills().getLevel(Skill.RANGED);
	    
	    // Prayer Bonuses
	    double prayerBonus = 1.0;
	    if (c.prayerActive[3])       prayerBonus = 1.05; // Sharp Eye
	    else if (c.prayerActive[11]) prayerBonus = 1.10; // Hawk Eye
	    else if (c.prayerActive[19]) prayerBonus = 1.15; // Eagle Eye
	    else if (c.prayerActive[27]) prayerBonus = 1.23; // Rigour

	    // Calculate Effective Level (Level * Prayer)
	    // We floor it immediately, just like OSRS
	    int effectiveLevel = (int) Math.floor(rangeLevel * prayerBonus);

	    // Style Bonus (+3 for Accurate/Standard, +0 for Longrange)
	    // Assuming fightMode 0 is Accurate
	    if (c.fightMode == 0) {
	        effectiveLevel += 3;
	    }
	    effectiveLevel += 8; // Constant +8 for Ranged

	    // Void Bonus (Applied to Effective Level)
	    if (c.fullVoidRange()) {
	        // If you have Elite void, change 1.10 to 1.125
	        effectiveLevel = (int) Math.floor(effectiveLevel * 1.10);
	    }

	    // -----------------------------------------------------------
	    // Step 2: Base Max Hit
	    // -----------------------------------------------------------
	    // We use playerBonus[12] which calculates Ranged STR from items (Anguish, Pegs, Ammo)
	    int rangedStrength = c.playerBonus[12];
	    
	    // The OSRS Formula: floor(0.5 + (EffLevel * (StrBonus + 64)) / 640)
	    int maxHit = (int) Math.floor(0.5 + ((double)(effectiveLevel * (rangedStrength + 64)) / 640.0));

	    // -----------------------------------------------------------
	    // Step 3: Gear Bonuses (Multipliers)
	    // -----------------------------------------------------------
	    double gearBonus = 1.0;
	    
	    NPC npc = (c.npcIndex > 0 && c.npcIndex < NPCHandler.npcs.length) ? NPCHandler.npcs[c.npcIndex] : null;

	    if (npc != null) {
	        // Definitions
	        boolean isUndead = Misc.linearSearch(Config.UNDEAD_IDS, npc.npcType) != -1;
	        boolean isSlayerTask = c.getSlayer().getTask().isPresent() && 
	                               (c.getSlayer().getTask().get().matches(npc.getDefinition().getNpcName()) || npc.npcType == 7413);
	        
	        boolean wearingSalveI = c.getItems().isWearingItem(4081, c.playerAmulet);
	        boolean wearingSalveEI = c.getItems().isWearingItem(12018, c.playerAmulet);
	        boolean wearingBlackMask = IntStream.of(c.IMBUED_SLAYER_HELMETS).anyMatch(i -> c.getItems().isWearingItem(i));

	        // Priority Logic: Salve (ei) > Salve (i) > Slayer Helm (They do NOT stack)
	        if (isUndead && wearingSalveEI) {
	            gearBonus *= 1.20; // 20%
	        } else if (isUndead && wearingSalveI) {
	            gearBonus *= 1.1667; // ~16.67% (7/6)
	        } else if (isSlayerTask && wearingBlackMask) {
	            gearBonus *= 1.15; // 15%
	        }
	    }

	    // Twisted Bow (Custom Raids logic from your snippet)
	    if (c.getItems().isWearingItem(20997)) {
	        if (Boundary.isIn(c, Boundary.RAIDROOMS) || Boundary.isIn(c, Boundary.XERIC)) {
	            gearBonus *= 1.37;
	        }
	    }
	    
	    // Wilderness Weapons (Craw's Bow)
	    // Using 1.5x as per instructions
	    if (c.getItems().isWearingItem(22550) && c.inWild()) {
	        gearBonus *= 1.5;
	    }

	    // Apply the Gear Multiplier
	    maxHit = (int) Math.floor(maxHit * gearBonus);

	    // -----------------------------------------------------------
	    // Step 4: Special Attacks
	    // -----------------------------------------------------------
	    if (c.usingSpecial) {
	        if (c.getItems().isWearingItem(11235, c.playerWeapon)) { // Dark Bow
	            // Dragon arrows give 1.5x, others 1.3x
	            if (Arrow.matchesMaterial(c.lastArrowUsed, Arrow.DRAGON)) {
	                maxHit = (int)(maxHit * 1.50);
	            } else {
	                maxHit = (int)(maxHit * 1.30);
	            }
	        } else {
	            // Standard spec damage (e.g. MSB is 1.0, Ballista might be 1.25)
	            maxHit = (int)(maxHit * c.specDamage);
	        }
	    }

	    return maxHit;
	}
}