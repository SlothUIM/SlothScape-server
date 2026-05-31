package server.model.players.combat.magic;

import org.apache.commons.lang3.RandomUtils;

import server.Config;
import server.world.Boundary;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.skills.Skill;
import server.model.items.ItemAssistant;

public class MagicRequirements extends MagicConfig {

	public static boolean hasRunes(Player c, int[] runes, int[] amount) {
		//if (c.getRunePouch().hasRunes(runes, amount)) {
		//	return true;
		//}
		for (int i = 0; i < runes.length; i++) {
			if (c.getItems().playerHasItem(runes[i], amount[i])) {
				return true;
			}
		}
		c.sendMessage("You don't have enough required runes to cast this spell!");
		return false;
	}

	public static void deleteRunes(Player c, int[] runes, int[] amount) {
		//if (c.getRunePouch().hasRunes(runes, amount)) {
		//	c.getRunePouch().deleteRunesOnCast(runes, amount);
		//	return;
		//}
		for (int i = 0; i < runes.length; i++) {
			c.getItems().deleteItem(runes[i], c.getItems().getItemSlot(runes[i]), amount[i]);
		}
	}

	public static boolean hasRequiredLevel(Player c, int i) {
		return c.getSkills().getLevel(Skill.MAGIC) >= i;
	}

	public static boolean wearingStaff(Player c, int runeId) {
		int wep = c.playerEquipment[c.playerWeapon];
		switch (runeId) {
		case FIRE:
			if (wep == 1387 || wep == 1393 || wep == 1401 || wep == 12796 || wep == 11789 || wep == 12000 || wep == 11998 || wep == 12795 || wep == 22335 || wep == 20714)
				return true;
			if(c.playerEquipment[c.playerShield] == 20714)
				return true;
			break;
		case WATER:
			if (wep == 1383 || wep == 1395 || wep == 12796 || wep == 11789 || wep == 6563 || wep == 1403 || wep == 21006 || wep == 20730  || wep == 12795)
				return true;
			break;
		case AIR:
			if (wep == 1381 || wep == 1397 || wep == 1405 || wep == 12000 || wep == 20736 || wep == 20736 || wep == 20730 || wep == 22335)
				return true;
			break;
			
		case EARTH:
			if (wep == 1385 || wep == 1399 || wep == 1407 || wep == 6563 || wep == 20736 | wep == 20736  || wep == 11998)
				return true;
			break;
		}
		return false;
	}

	public static boolean checkMagicReqs(Player c, int spell) {
		//System.out.println("Spell: " + spell + ", Spellid: " + c.spellId + ", Old Spellid: " + c.oldSpellId);

		if (!c.usingMagic) return true;

		int[] runeIds = new int[4];
		int[] runeAmounts = new int[4];
		for (int i = 0; i < 4; i++) {
			runeIds[i] = MagicData.MAGIC_SPELLS[spell][8 + i * 2];
			runeAmounts[i] = MagicData.MAGIC_SPELLS[spell][9 + i * 2];
		}

		if (!Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY)) {
			for (int i = 0; i < 4; i++) {
				int id = runeIds[i];
				int amt = runeAmounts[i];
				if (id <= 0 || amt <= 0) continue;
				if (!c.getRunePouch().hasRune(id, amt) && !c.getItems().playerHasItem(id, amt) && !wearingStaff(c, id)) {
					c.sendMessage("You don't have the required runes to cast this spell.");
					return false;
				}
			}
		}

		if (c.playerIndex > 0 && PlayerHandler.players[c.playerIndex] != null) {
			Player target = PlayerHandler.players[c.playerIndex];
			for (int r = 0; r < c.REDUCE_SPELLS.length; r++) {
				if (target.REDUCE_SPELLS[r] == MagicData.MAGIC_SPELLS[spell][0]) {
					c.reduceSpellId = r;
					boolean immune = (System.currentTimeMillis() - target.reduceSpellDelay[r]) <= target.REDUCE_SPELL_TIME[r];
					target.canUseReducingSpell[r] = !immune;
					break;
				}
			}
			if (!target.canUseReducingSpell[c.reduceSpellId]) {
				c.sendMessage("That player is currently immune to this spell.");
				c.usingMagic = false;
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return false;
			}
		}

		int staffRequired = getStaffNeeded(c);
		if (Config.RUNES_REQUIRED && staffRequired > 0 && c.playerEquipment[c.playerWeapon] != staffRequired) {
			c.sendMessage("You need a " + ItemAssistant.getItemName(staffRequired).toLowerCase() + " to cast this spell.");
			return false;
		}

		if (Config.MAGIC_LEVEL_REQUIRED && c.getSkills().getLevel(Skill.MAGIC) < MagicData.MAGIC_SPELLS[spell][1]) {
			c.sendMessage("You need to have a magic level of " + MagicData.MAGIC_SPELLS[spell][1] + " to cast this spell.");
			return false;
		}

		boolean runesNecessary = !(c.playerEquipment[c.playerWeapon] == 11791 || c.playerEquipment[c.playerWeapon] == 12904) || RandomUtils.nextInt(0, 100) >= 13;

		if (!Boundary.isIn(c, Boundary.FOUNTAIN_OF_RUNE_BOUNDARY) && Config.RUNES_REQUIRED && runesNecessary) {
			for (int i = 0; i < 4; i++) {
				int id = runeIds[i];
				int amt = runeAmounts[i];
				if (id <= 0 || amt <= 0 || wearingStaff(c, id)) continue;

				int fromPouch = Math.min(amt, c.getRunePouch().getCountInBag(id));
				if (fromPouch > 0)
					c.getRunePouch().consumeRune(id, fromPouch);
				c.getRunePouch().sendLegacyRuneTypes();
				int fromInventory = amt - fromPouch;
				if (fromInventory > 0)
					c.getItems().deleteItem(id, fromInventory);
			}


		}

		return true;
	}



	public static final int FIRE = 554;
	public static final int WATER = 555;
	public static final int AIR = 556;
	public static final int EARTH = 557;
	public static final int MIND = 558;
	public static final int BODY = 559;
	public static final int DEATH = 560;
	public static final int NATURE = 561;
	public static final int CHAOS = 562;
	public static final int LAW = 563;
	public static final int COSMIC = 564;
	public static final int BLOOD = 565;
	public static final int SOUL = 566;
	public static final int ASTRAL = 9075;
}