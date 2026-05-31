package server.model.players.combat.melee;

import java.util.stream.IntStream;

import server.Config;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;

public class MeleeMaxHit {

	/**
	 * Calculates the absolute maximum melee hit based on strict OSRS operations.
	 */
	public static int calculateMaxHit(Player c, boolean special) {
		// --- STEP 1: EFFECTIVE STRENGTH ---
		// Potion boosts are naturally handled by c.getSkills().getLevel() in most bases
		int strengthLevel = c.getSkills().getLevel(Skill.STRENGTH);
		double prayerMultiplier = getPrayerStr(c);
		int styleBonus = getStyleBonus(c);
		double voidMultiplier = hasVoid(c) ? 1.10 : 1.00;

		// The inner floor is critical for accurate OSRS math
		int baseStr = (int) Math.floor(strengthLevel * prayerMultiplier);
		int effectiveStrength = (int) Math.floor((baseStr + styleBonus + 8) * voidMultiplier);

		// --- STEP 2: BASE DAMAGE ---
		int strengthBonus = c.playerBonus[10];
		int baseDamage = (int) Math.floor(0.5 + ((effectiveStrength * (strengthBonus + 64)) / 640.0));

		// --- STEP 3: BONUS DAMAGE MULTIPLIERS ---
		double multiplier = 1.0;

		// Passive Effects (Additive/Multiplicative depending on era, usually grouped)
		if (hasObsidianEffect(c)) {
			multiplier += 0.30; // 1.3x for Obsidian armor + weapon + Berserker necklace
		}

		// Example: Salve / Slayer Helm integration
        /*
        if (c.getItems().isWearingItem(4081) && isUndead(c)) {
            multiplier += 0.1666; // 16.66% boost
        }
        */

		// Apply Special Attack Multiplier
		if (special) {
			multiplier *= getSpecialStr(c);
		}

		int finalMaxHit = (int) Math.floor(baseDamage * multiplier);

		// --- CUSTOM SERVER OVERRIDES ---
		// If you want a global buff to all melee damage, you would use a custom damage config here.
		// finalMaxHit = (int) Math.floor(finalMaxHit * Config.CUSTOM_DAMAGE_MULTIPLIER);

		if (c.inWild() && c.getItems().isWearingItem(22545)) {
			finalMaxHit = (int) Math.floor(finalMaxHit * 1.602);
		}
		if (c.summonId == 33930) {
			finalMaxHit = (int) Math.floor(finalMaxHit * 1.03);
		}

		return finalMaxHit;
	}

	/**
	 * OSRS Style Bonuses
	 */
	public static int getStyleBonus(Player c) {
		return switch (c.fightMode) {
			case 1 -> 3; // Aggressive
			case 2 -> 1; // Controlled
			default -> 0; // Accurate/Defensive
		};
	}

	/**
	 * OSRS Prayer Strength Multipliers
	 */
	public static double getPrayerStr(Player c) {
		if (c.prayerActive[1]) return 1.05;       // Burst of Strength
		else if (c.prayerActive[6]) return 1.10;  // Superhuman Strength
		else if (c.prayerActive[14]) return 1.15; // Ultimate Strength
		else if (c.prayerActive[24]) return 1.18; // Chivalry (Ensure ID matches your server)
		else if (c.prayerActive[25]) return 1.23; // Piety (Restored to accurate 1.23)
		return 1.0;
	}

	/**
	 * Updated Special Attack Multipliers matching exact OSRS Wiki values.
	 */
	public static final double[][] SPECIAL_MULTIPLIERS = {
			{ 5698, 1.15 }, { 5680, 1.15 }, { 1231, 1.15 }, { 1215, 1.15 }, // Dragon daggers
			{ 3204, 1.10 }, // Dragon halberd
			{ 1305, 1.25 }, // Dragon longsword
			{ 1434, 1.50 }, // Dragon mace
			{ 13576, 1.50}, // Dragon warhammer
			{ 11802, 1.375 }, // AGS
			{ 11804, 1.21 },  // BGS
			{ 11806, 1.10 },  // SGS
			{ 11808, 1.10 },  // ZGS
			{ 4151, 1.10 },   // Whip (not technically a damage boost in OSRS, but standard for RSPS)
			{ 10887, 1.2933 } // Barrelchest Anchor
	};

	public static double getSpecialStr(Player c) {
		int weapon = c.playerEquipment[3]; // c.playerWeapon
		for (double[] slot : SPECIAL_MULTIPLIERS) {
			if (weapon == (int) slot[0]) {
				return slot[1];
			}
		}
		return 1.0;
	}

	public static final int[] OBSIDIAN_WEAPONS = { 746, 747, 6523, 6525, 6526, 6527, 6528 };

	public static boolean hasObsidianEffect(Player c) {
		// Requires Berserker Necklace
		if (c.playerEquipment[2] != 11128) return false;

		for (int weapon : OBSIDIAN_WEAPONS) {
			if (c.playerEquipment[3] == weapon) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasVoid(Player c) {
		return (c.playerEquipment[c.playerHat] == 11665 && c.playerEquipment[c.playerLegs] == 8840) ||
				(c.playerEquipment[c.playerLegs] == 13073 && c.playerEquipment[c.playerChest] == 8839) ||
				(c.playerEquipment[c.playerChest] == 13072 && c.playerEquipment[c.playerHands] == 8842);
	}

	public static int bestMeleeDef(Player c) {
		if (c.playerBonus[5] > c.playerBonus[6] && c.playerBonus[5] > c.playerBonus[7]) {
			return 5;
		}
		if (c.playerBonus[6] > c.playerBonus[5] && c.playerBonus[6] > c.playerBonus[7]) {
			return 6;
		}
		return c.playerBonus[7] <= c.playerBonus[5] || c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	public static int calculateMeleeDefence(Player c) {
		int defenceLevel = c.playerLevel[1];
		int i = c.playerBonus[bestMeleeDef(c)];
		if (c.prayerActive[0]) {
			defenceLevel += c.getSkills().getActualLevel(Skill.DEFENCE) * 0.05;
		} else if (c.prayerActive[5]) {
			defenceLevel += c.getSkills().getActualLevel(Skill.DEFENCE) * 0.1;
		} else if (c.prayerActive[13]) {
			defenceLevel += c.getSkills().getActualLevel(Skill.DEFENCE) * 0.15;
		} else if (c.prayerActive[25]) {
			defenceLevel += c.getSkills().getActualLevel(Skill.DEFENCE) * 0.2;
		} //else if (c.prayerActive[26]) {
		//	defenceLevel += c.getLevelForXP(c.playerXP[c.playerDefence]) * 0.25;
		//}
		return (int) (defenceLevel + (defenceLevel * 0.15) + (i + i * 0.05));
	}

	public static int bestMeleeAtk(Player c) {
		if (c.playerBonus[0] > c.playerBonus[1] && c.playerBonus[0] > c.playerBonus[2]) {
			return 0;
		}
		if (c.playerBonus[1] > c.playerBonus[0] && c.playerBonus[1] > c.playerBonus[2]) {
			return 1;
		}
		return c.playerBonus[2] <= c.playerBonus[1] || c.playerBonus[2] <= c.playerBonus[0] ? 0 : 2;
	}

	public static int calculateMeleeAttack(Player c) {
		int attackLevel = c.getSkills().getLevel(Skill.ATTACK);
		if (c.prayerActive[2]) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.05;
		} else if (c.prayerActive[7]) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.1;
		} else if (c.prayerActive[15]) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
		} else if (c.prayerActive[25]) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
		}// else if (c.prayerActive[26]) {
			//attackLevel += c.getLevelForXP(c.playerXP[c.playerAttack]) * 0.2;
		//}
		if (c.playerEquipment[c.playerWeapon] == 4153 || c.playerEquipment[c.playerWeapon] == 12848) {
			attackLevel -= c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
		}
		if (c.playerEquipment[c.playerWeapon] == 4718 && c.playerEquipment[c.playerHat] == 4716 && c.playerEquipment[c.playerChest] == 4720
				&& c.playerEquipment[c.playerLegs] == 4722) {
			attackLevel -= c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
		}
		if (c.fullVoidMelee()) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.10;
		}
		if (c.fullVoidSupremeMelee()) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
		}
		if (c.getItems().isWearingItem(20784)) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
		}
		if (c.getItems().isWearingItem(22545)) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
		}
		if (c.getItems().isWearingItem(33778)) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
		}
		if (c.getItems().isWearingItem(33780)) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
		}
		if (c.getItems().isWearingItem(33779)) {
			attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
		}


		if (c.debugMessage)
			c.sendMessage("Accuracy from weapon: "+ attackLevel);
		if (c.getItems().isWearingItem(19675, c.playerWeapon) && c.getArcLightCharge() > 0) {
			//if (c.debugMessage)
					c.sendMessage("Accuracy on reg: "+ attackLevel);
			NPC npc = NPCHandler.npcs[c.npcIndex];
			if (Misc.linearSearch(Config.DEMON_IDS, npc.npcType) != -1) {
				attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.80;
				if (c.debugMessage)
					c.sendMessage("Accuracy on demon: "+ attackLevel);
			}
		}
		if (c.npcIndex > 0 && c.getSlayer().getTask().isPresent()) {
			NPC npc = NPCHandler.npcs[c.npcIndex];
			if (npc != null && c.getSlayer().getTask().get().matches(npc.getDefinition().getNpcName()) || npc.npcType == 7413) {
				boolean SLAYER_HELM = IntStream.of(c.SLAYER_HELMETS).anyMatch(i -> c.getItems().isWearingItem(i));
				if (!c.getItems().isWearingItem(4081) && SLAYER_HELM || c.getItems().isWearingItem(8901)) {
					attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
				}
			}
			if (c.getItems().isWearingItem(4081, c.playerAmulet)) {
				if (Misc.linearSearch(Config.UNDEAD_IDS, npc.npcType) != -1) {
					attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.15;
				}
			}
			if (c.getItems().isWearingItem(12018, c.playerAmulet)) {
				if (Misc.linearSearch(Config.UNDEAD_IDS, npc.npcType) != -1) {
					attackLevel += c.getSkills().getActualLevel(Skill.ATTACK) * 0.20;
				}
			}
		}
		attackLevel *= c.specAccuracy;
		int i = c.playerBonus[bestMeleeAtk(c)];
		i += c.bonusAttack;
		if (hasObsidianEffect(c) || c.fullVoidMelee()) {
			i *= 1.10;
		}
		if (c.fullVoidSupremeMelee()) {
			i *= 1.20;
		}
		return (int) (attackLevel + (attackLevel * 0.20) + (i + i * 0.10));
	}
}