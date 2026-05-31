package server.model.players;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;

import server.Config;
import server.Server;
import server.model.Entity;
import server.model.items.ItemAssistant;
import server.model.items.ItemDefinition;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.path.RS317PathFinder;
import server.model.players.combat.AttackNPC;
import server.model.players.combat.AttackPlayer;
import server.model.players.combat.Damage;
import server.model.players.combat.Hitmark;
import server.model.players.combat.magic.MagicConfig;
import server.model.players.combat.magic.MagicData;
import server.model.players.combat.magic.MagicExtras;
import server.model.players.combat.magic.MagicMaxHit;
import server.util.Misc;
import server.world.Boundary;
import server.world.Location;
import server.world.World;
import server.model.players.combat.melee.MeleeSpecial;
import server.model.players.combat.magic.MagicRequirements;
import server.model.players.combat.melee.CombatPrayer;
import server.model.players.combat.melee.MeleeData;
import server.model.players.combat.melee.MeleeExtras;
import server.model.players.combat.melee.MeleeMaxHit;
import server.model.players.combat.melee.MeleeRequirements;
import server.model.players.combat.range.Bow;
import server.model.players.combat.range.RangeData;
import server.model.players.combat.range.RangeExtras;
import server.model.players.combat.range.RangeMaxHit;
import server.model.players.Sound;
import server.model.players.Music;
import server.clip.Region;

public class CombatAssistant {

	private Player c;

	public CombatAssistant(Player Client) {
		this.c = Client;
	}

	public double strBonus;
	public double strengthBonus;




	/**
	 * Attack Npcs
	 */
	public void attackNpc(int i) {
		AttackNPC.attackNpc(c, i);
	}


	public void fireProjectileNpc(int delay) {
		RangeData.fireProjectileNpc(c, delay);
	}

	/**
	 * Attack Players, same as npc tbh xD
	 **/

	public void attackPlayer(int i) {
		AttackPlayer.attackPlayer(c, i);
	}

	public boolean usingCrystalBow() {
		return (c.playerEquipment[c.playerWeapon] >= 4212 && c.playerEquipment[c.playerWeapon] <= 4223 
				|| c.playerEquipment[c.playerWeapon] == 23901 || c.playerEquipment[c.playerWeapon] == 23902 
				|| c.playerEquipment[c.playerWeapon] == 23903 || c.playerEquipment[c.playerWeapon] == 23983 );
	}

	public void appendVengeance(int otherPlayer, int damage) {
		MeleeExtras.appendVengeance(c, otherPlayer, damage);
	}

	public void playerDelayedHit(final Player c, final int i, final Damage damage) {
		AttackPlayer.playerDelayedHit(c, i, damage);
	}

	public boolean multis() {
		return MagicConfig.multiSpells(c);
	}

	public void appendVengeanceNPC(int damage, int i) {
		MeleeExtras.appendVengeanceNPC(c, damage, i);
	}

	public void applyRecoilNPC(int damage, int i) {
		MeleeExtras.applyRecoilNPC(c, damage, i);
	}
	public void appendMultiBarrage(int playerId, boolean splashed) {
		MagicExtras.appendMultiBarrage(c, playerId, splashed);
	}

	public void multiSpellEffect(int playerId, int damage) {
		MagicExtras.multiSpellEffect(c, playerId, damage);
	}

	public void applyPlayerMeleeDamage(int i, int damageMask, int damage, Hitmark hitmark) {
		AttackPlayer.applyPlayerMeleeDamage(c, i, damageMask, damage, hitmark);
	}

	public void applySmite(int index, int damage) {
		MeleeExtras.applySmite(c, index, damage);
	}

	public void fireProjectilePlayer(int delay) {
		RangeData.fireProjectilePlayer(c, delay);
	}

	public boolean usingDbow() {
		return c.playerEquipment[c.playerWeapon] == 11235 || c.playerEquipment[c.playerWeapon] == 12765 || c.playerEquipment[c.playerWeapon] == 12766
				|| c.playerEquipment[c.playerWeapon] == 12767 || c.playerEquipment[c.playerWeapon] == 12768;
	}

/**Prayer**/

	public void activatePrayer(int i) {
		CombatPrayer.activatePrayer(c, i);
	}

	/**
	 * Specials
	 **/

	public void activateSpecial(int weapon, int i) {
		MeleeSpecial.activateSpecial(c, weapon, i);
	}

	public boolean checkSpecAmount(int weapon) {
		return MeleeSpecial.checkSpecAmount(c, weapon);
	}

	public void resetPlayerAttack() {
		MeleeData.resetPlayerAttack(c);
	}

	public int getCombatDifference(int combat1, int combat2) {
		return MeleeRequirements.getCombatDifference(combat1, combat2);
	}


public void handlePrayerDrain() {
	CombatPrayer.handlePrayerDrain(c);
}

public void reducePrayerLevel() {
	CombatPrayer.reducePrayerLevel(c);
}

public void resetPrayers() {
	CombatPrayer.resetPrayers(c);
}

	/**
	 * Wildy and duel info
	 **/

public boolean checkReqs() {
	return MeleeRequirements.checkReqs(c);
}

public boolean checkMultiBarrageReqs(int i) {
	return MagicExtras.checkMultiBarrageReqs(c, i);
}

	
	public boolean usingHally() {
		return MeleeData.usingHally(c);
	}
	public void getPlayerAnimIndex(String weaponName) {
		MeleeData.getPlayerAnimIndex(c, weaponName);
	}

	public int getWepAnim(String weaponName) {
		return MeleeData.getWepAnim(c, weaponName);
	}

	public int getBlockEmote() {
		return MeleeData.getBlockEmote(c);
	}
	/**
	 * Gets the appropriate attack sound for the player {@link #c}.
	 * @return A sound id which can be used in calling {@link Client#sendSound}.
	 */
	public int getAttackSound(int weaponID) {
		return MeleeData.getAttackSound(c, weaponID);
	}
	public int getDefenceSound(int weaponID) {
		return MeleeData.getDefSound(c, weaponID);
	}
	public void appendMultiBarrageNPC(int npcId, boolean splashed) {
		MagicExtras.appendMultiBarrageNPC(c, npcId, splashed);
	}

	public void appendMultiScytheNPC(int npcId) {
		MeleeExtras.appendMultiScytheNPC(c, npcId); 
	}
	public int getAttackDelay(String s) {
		return MeleeData.getAttackDelay(c, s);
	}

	public void appendMultiChinchompa(int npcId) {
		RangeExtras.appendMultiChinchompa(c, npcId);
	}
	public int getHitDelay(int i, String weaponName) {
		return MeleeData.getHitDelay(c, i, weaponName);
	}
	public int getRequiredDistance() {
		return MeleeRequirements.getRequiredDistance(c);
	}
	/**
	 * Melee
	 **/
	public static final int[][] MUTAGEN_HELMETS = { { 12931, 12929 }, { 13199, 13198 }, { 13197, 13196 } };

	public void degradeVenemousItems(Entity killer) {
		for (int[] helmets : MUTAGEN_HELMETS) {
			int charged = helmets[0];
			int uncharged = helmets[1];
			if (c.getItems().isWearingItem(charged, c.playerHat) || c.getItems().playerHasItem(charged)) {
				if (c.getSerpentineHelmCharge() > 0) {
					Player owner = killer == null || killer instanceof NPC ? c : (Player) killer;
					World.getWorld().getItemHandler().createGroundItem(owner, 12934, c.getX(), c.getY(), c.getHeight(), c.getSerpentineHelmCharge(), owner.getIndex());
				}
				if (c.getItems().isWearingItem(charged, c.playerHat)) {
					c.getItems().wearItem(uncharged, 1, c.playerHat);
				} else if (c.getItems().playerHasItem(charged)) {
					c.getItems().deleteItem2(charged, 1);
					c.getItems().addItem(uncharged, 1);
				}
				c.sendMessage("The " + ItemDefinition.forId(charged).getName() + " has been dropped on the floor.");
				c.setSerpentineHelmCharge(0);
			}
		}
		
		if (c.getItems().isWearingItem(12904, c.playerWeapon) || c.getItems().playerHasItem(12904)) {
			if (c.getToxicStaffOfTheDeadCharge() > 0) {
				if (c.getItems().isWearingItem(12904, c.playerWeapon)) {
					c.getItems().wearItem(12902, 1, c.playerWeapon);
				} else if (c.getItems().playerHasItem(12904)) {
					c.getItems().deleteItem2(12904, 1);
					c.getItems().addItem(12902, 1);
				}
				Player owner = killer == null || killer instanceof NPC ? c : (Player) killer;
				World.getWorld().getItemHandler().createGroundItem(owner, 12934, c.getX(), c.getY(), c.getHeight(), c.getToxicStaffOfTheDeadCharge(), owner.getIndex());
				c.setToxicStaffOfTheDeadCharge(0);
				c.sendMessage("Your toxic staff of the dead has lost all charge, the scales are on the floor.");
			}
		}
		
		if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == c.playerWeapon
				|| c.getItems().playerHasItem(12926)) {
			if (c.getToxicBlowpipeAmmo() > 0 && c.getToxicBlowpipeAmmoAmount() > 0 && c.getToxicBlowpipeCharge() > 0) {
				if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == c.playerWeapon) {
					c.getItems().wearItem(12924, 1, c.playerWeapon);
				} else {
					c.getItems().deleteItem2(12926, 1);
					c.getItems().addItem(12924, 1);
				}
				Player owner = killer == null || killer instanceof NPC ? c : (Player) killer;
				World.getWorld().getItemHandler().createGroundItem(owner, 12934, c.getX(), c.getY(), c.getHeight(), c.getToxicBlowpipeCharge(), owner.getIndex());
				World.getWorld().getItemHandler().createGroundItem(owner, c.getToxicBlowpipeAmmo(), c.getX(), c.getY(), c.getHeight(), c.getToxicBlowpipeAmmoAmount(), owner.getIndex());
				c.setToxicBlowpipeAmmo(0);
				c.setToxicBlowpipeAmmoAmount(0);
				c.setToxicBlowpipeCharge(0);
				c.sendMessage("Your blowpipe has been dropped on the floor. You lost the ammo, pipe, and charge.");
			}
		}
	}
	public void checkDemonItems() {
		if (c.getItems().isWearingItem(19675, c.playerWeapon)) {
			c.setArcLightCharge(c.getArcLightCharge() - 1);
			if (c.getArcLightCharge() <= 0) {
				c.setArcLightCharge(0);
				c.sendMessage("Your arclight has lost all charge.");
				c.getItems().wearItem(-1, 0, c.playerWeapon);
				c.getItems().addItemUnderAnyCircumstance(19675, 1);
			}
		}
	}
	public void applyPlayerHit(Player c, final int i, final Damage damage) {
		AttackPlayer.applyPlayerHit(c, i, damage);
	}
	public void checkVenomousItems() {
		if (c.getItems().isWearingItem(12926) && c.getItems().getWornItemSlot(12926) == c.playerWeapon) {
			/*if (c.getItems().isWearingItem(10499) ||c.getItems().isWearingItem(22109) || SkillcapePerks.RANGING.isWearing(c) || SkillcapePerks.isWearingMaxCape(c)) {
				if (RandomUtils.nextInt(0, 15) > 1) {
					return;correctBowAndArrows
				}
			}*/
			c.setToxicBlowpipeAmmoAmount(c.getToxicBlowpipeAmmoAmount() - 1);
			c.setToxicBlowpipeCharge(c.getToxicBlowpipeCharge() - 1);
			if (c.getToxicBlowpipeAmmoAmount() % 500 == 0 && c.getToxicBlowpipeAmmoAmount() > 0) {
				c.sendMessage("<col=255>You have " + c.getToxicBlowpipeAmmoAmount() + " ammo in your blow pipe remaining.</col>");
			}
			if (c.getToxicBlowpipeAmmoAmount() <= 0 && c.getToxicBlowpipeCharge() <= 0) {
				c.sendMessage("Your toxic blowpipe has lost all charge.");
				c.getItems().wearItem(-1, 0, 3);
				c.getItems().addItemUnderAnyCircumstance(12924, 1);
				c.setToxicBlowpipeAmmo(0);
				c.setToxicBlowpipeAmmoAmount(0);
				c.setToxicBlowpipeCharge(0);
			}
		}
		if (c.getItems().isWearingItem(12904, c.playerWeapon)) {
			c.setToxicStaffOfTheDeadCharge(c.getToxicStaffOfTheDeadCharge() - 1);
			if (c.getToxicStaffOfTheDeadCharge() <= 0) {
				c.setToxicStaffOfTheDeadCharge(0);
				c.sendMessage("Your toxic staff of the dead has lost all charge.");
				c.getItems().wearItem(-1, 0, c.playerWeapon);
				c.getItems().addItemUnderAnyCircumstance(12902, 1);
			}
		}
		for (int[] helmets : MUTAGEN_HELMETS) {
			int charged = helmets[0];
			int uncharged = helmets[1];
			if (c.getItems().isWearingItem(charged) && c.getItems().getWornItemSlot(charged) == c.playerHat) {
				c.setSerpentineHelmCharge(c.getSerpentineHelmCharge() - 1);
				if (c.getSerpentineHelmCharge() % 500 == 0 && c.getSerpentineHelmCharge() != 0) {
					c.sendMessage("<col=255>You have " + c.getSerpentineHelmCharge() + " charges remaining in your serpentine helm.</col>");
				}
				if (c.getSerpentineHelmCharge() <= 0) {
					c.sendMessage("Your serpentine helm has lost all of it's charge.");
					c.getItems().wearItem(-1, 0, c.playerHat);
					c.getItems().addItemUnderAnyCircumstance(uncharged, 1);
					c.setSerpentineHelmCharge(0);
				}
			}
		}
	}
	public int calculateMeleeAttack() {
		return MeleeMaxHit.calculateMeleeAttack(c);
	}

	public int bestMeleeAtk() {
		return MeleeMaxHit.bestMeleeAtk(c);
	}

	public int calculateMeleeMaxHit() {
		return (int) MeleeMaxHit.calculateMaxHit(c, c.usingSpecial);
	}

	public int calculateMeleeDefence() {
		return MeleeMaxHit.calculateMeleeDefence(c);
	}


	public int bestMeleeDef() {
		return MeleeMaxHit.bestMeleeDef(c);
	}

	/**
	 * Range
	 **/

	public int calculateRangeAttack() {
		return RangeMaxHit.calculateRangeAttack(c);
	}

	public int calculateRangeDefence() {
		return RangeMaxHit.calculateRangeDefence(c);
	}

	public boolean usingBolts(int i) {
		return (i >= 9139 && i <= 9145) || (i >= 9236 && i <= 9245) || (i >= 9286 && i <= 9306) || i == 9341 || i == 9340 || i == 11875 || i == 21316;
	}
	public boolean properJavelins() {
		return usingJavelins(c.playerEquipment[c.playerArrows]);
	}
	public boolean usingJavelins(int i) {
		return (i >= 825 && i <= 830) || i == 19484 || i == 21318;
	}
	public int rangeMaxHit() {
		return RangeMaxHit.maxHit(c);
	}

	public int getRangeStr(int i) {
		return RangeData.getRangeStr(i);
	}


	public int mageAtk() {
		return MagicMaxHit.mageAttack(c);
	}

	public int mageDef() {
		return MagicMaxHit.mageDefence(c);
	}

	public int magicMaxHit() {
		return MagicMaxHit.magiMaxHit(c);
	}
	public boolean properBolts() {
		return usingBolts(c.playerEquipment[c.playerArrows]);
	}

	public boolean correctBowAndArrows() {
		return Bow.canUseArrow(c.playerEquipment[c.playerWeapon], c.playerEquipment[c.playerArrows]);
	}

	public int getRangeStartGFX() {
		return RangeData.getRangeStartGFX(c);
	}

	public int getRangeProjectileGFX() {
		return RangeData.getRangeProjectileGFX(c);
	}

	public int getProjectileSpeed() {
		return RangeData.getProjectileSpeed(c);
	}

	public int getProjectileShowDelay() {
		return RangeData.getProjectileShowDelay(c);
	}

	public boolean wearingStaff(int runeId) {
		return MagicRequirements.wearingStaff(c, runeId);
	}

	public boolean checkMagicReqs(int spell) {
		return MagicRequirements.checkMagicReqs(c, spell);
	}

	public int getFreezeTime() {
		switch (MagicData.MAGIC_SPELLS[c.oldSpellId][0]) {
		case 1572:
		case 12861: // ice rush
			return 10;

		case 1582:
		case 12881: // ice burst
			return 17;

		case 1592:
		case 12871: // ice blitz
			return 25;

		case 12891: // ice barrage
			return 33;

		default:
			return 0;
		}
	}

	public void freezePlayer(int i) {

	}

	public int getStartHeight() {
		return MagicConfig.getStartHeight(c);
	}

	public int getEndHeight() {
		return MagicConfig.getEndHeight(c);
	}

	public int getStartDelay() {
		return MagicConfig.getStartDelay(c);
	}

	public int getStaffNeeded() {
		return MagicConfig.getStaffNeeded(c);
	}
	public boolean godSpells() {
		return MagicConfig.godSpells(c);
	}

	public boolean airSpells() {
		return MagicConfig.airSpells(c);
	}

	public boolean shadowSpells() {
		return MagicConfig.shadowSpells(c);
	}

	public boolean waterSpells() {
		return MagicConfig.waterSpells(c);
	}

	public boolean fireSpells() {
		return MagicConfig.fireSpells(c);
	}

	public int npcDefenceAnim(int i) {
		return MeleeData.npcDefenceAnim(i);
	}
	public int npcDefenceSound(int i) {
		return MeleeData.npcDefenceSound(i);
	}
	public boolean earthSpells() {
		return MagicConfig.earthSpells(c);
	}

	public int getEndGfxHeight() {
		return MagicConfig.getEndGfxHeight(c);
	}

	public int getStartGfxHeight() {
		return MagicConfig.getStartGfxHeight(c);
	}

	public void handleDfs() {
		if (System.currentTimeMillis() - c.dfsDelay > 30000) {
			if (c.playerIndex > 0
					&& PlayerHandler.players[c.playerIndex] != null) {
				int damage = Misc.random(15) + 5;
				c.startAnimation(2836);
				c.gfx0(600);
				PlayerHandler.players[c.playerIndex].playerLevel[3] -= damage;
				PlayerHandler.players[c.playerIndex].hitDiff2 = damage;
				PlayerHandler.players[c.playerIndex].hitUpdateRequired2 = true;
				PlayerHandler.players[c.playerIndex].updateRequired = true;
				c.dfsDelay = System.currentTimeMillis();
			} else {
				c.sendMessage("I should be in combat before using this.");
			}
		} else {
			c.sendMessage("My shield hasn't finished recharging yet.");
		}
	}

	public void handleDfsNPC() {
		if (System.currentTimeMillis() - c.dfsDelay > 30000) {
			if (c.npcIndex > 0 && NPCHandler.npcs[c.npcIndex] != null) {
				int damage = Misc.random(15) + 5;
				c.startAnimation(2836);
				c.gfx0(600);
				NPCHandler.npcs[c.npcIndex].HP -= damage;
				NPCHandler.npcs[c.npcIndex].hitDiff2 = damage;
				NPCHandler.npcs[c.npcIndex].hitUpdateRequired2 = true;
				NPCHandler.npcs[c.npcIndex].updateRequired = true;
				c.dfsDelay = System.currentTimeMillis();
			} else {
				c.sendMessage("I should be in combat before using this.");
			}
		} else {
			c.sendMessage("My shield hasn't finished recharging yet.");
		}
	}

	public void applyRecoil(int damage, int i) {
		MeleeExtras.applyRecoil(c, damage, i);
	}

	public int getBonusAttack(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 2883:
			return Misc.random(50) + 30;
		case 2026:
		case 2027:
		case 2029:
		case 2030:
			return Misc.random(50) + 30;
		}
		return 0;
	}

	public void handleGmaulPlayer() {
		MeleeExtras.graniteMaulSpecial(c);
	}


	/**
	 * @param entity
	 * @param following
	 * @return
	 */
	public static boolean canReach(Entity entity, Entity following) {
		
		if (entity.isPlayer()) {
			
			Location attacker = entity.getLocation();
			Location target = following.getLocation();
			boolean inDistance = false;
			int distanceRequired = entity.asPlayer().getDistanceRequired();
			Boundary targetBoundary = following.getBoundary();
			
			//Check if we're inside of the target
			if (Boundary.isIn(attacker, targetBoundary)) {
				return false;
			}
			
			// Get target's occupied tiles
			List<Location> tilesOccupied = new ArrayList<Location>();
			for (int bX = following.getLocation().getX(); bX <= targetBoundary.getMaximumX(); bX++) {
				for (int bY = targetBoundary.getMinimumY(); bY <= targetBoundary.getMaximumY(); bY++) {
					tilesOccupied.add(new Location(bX, bY, target.getZ()));
				}
			}
			
			// Make sure we're in distance with at least one of
			// the occupied tiles
			for (Location loc : tilesOccupied) {
				if (attacker.getDistance(loc) <= distanceRequired) {
					inDistance = true;
					break;
				}
			}
			
			if (!inDistance) {
				return false;
			}
			
			// Don't allow diagonal attacks for smaller entities
			if (entity.asPlayer().usingMelee && entity.getSize() == 1 && following.getSize() == 1) {
				if (RS317PathFinder.isInDiagonalBlock(attacker, target)) {
					return false;
				}
			}
			
			// Make sure we the path is clear for projectiles..
			if (!Region.lineOfSight(entity, following)) {
				return false;
			}
			
			return true;
			
		}
		
		return false;
	}
	public void absorbDragonfireDamage() {
		int shieldId = c.playerEquipment[c.playerShield];
		String shieldName = ItemAssistant.getItemName(shieldId).toLowerCase();
		if (shieldName.contains("dragonfire")) {
			int charges = c.getDragonfireShieldCharge();
			if (charges < 50) {
				c.setDragonfireShieldCharge(charges++);
				if (charges == 50) {
					c.sendMessage("<col=255>Your dragonfire shield has completely finished charging.");
				}
				c.startAnimation(6695);
				c.gfx0(1164);
				c.setDragonfireShieldCharge(charges);
				return;
			}
		}
		return;
	}
}
