package server.model.players.combat;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.RandomUtils;

import server.Config;
//import server.content.barrows.brothers.Brother;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.Entity;
import server.model.HealthStatus;
import server.model.npcs.NPC;
import server.model.npcs.NPCDefinitions;
import server.model.npcs.NPCHandler;
import server.model.npcs.bosses.CorporealBeast;
import server.model.npcs.bosses.Scorpia;
import server.model.npcs.bosses.EventBoss.impl.AntiSanta;
import server.model.npcs.bosses.raids.Tekton;
import server.model.npcs.bosses.skotizo.Skotizo;
import server.model.npcs.bosses.vorkath.Vorkath;
import server.model.npcs.bosses.wildypursuit.Glod;
import server.model.npcs.bosses.wildypursuit.IceQueen;
import server.world.Boundary;
import server.world.World;
import server.model.players.Player;
//import server.model.players.Right;
//import server.model.players.Equipment.Slot;
import server.model.players.combat.effects.ToxicBlowpipeEffect;
import server.model.players.combat.effects.ToxicStaffOfTheDeadEffect;
import server.model.players.combat.effects.TridentOfTheSwampEffect;
import server.model.players.combat.magic.MagicData;
//import server.model.players.combat.monsterhunt.MonsterHunt.Npcs;
import server.model.players.combat.range.RangeData;
import server.model.players.combat.range.RangeExtras;
import server.model.players.combat.range.RangeMaxHit;
import server.model.players.skills.Skill;
import server.model.players.skills.slayer.SlayerMaster;
//import server.model.players.skills.herblore.PoisonedWeapon;
//import server.model.players.skills.herblore.PoisonedWeapon.PoisonLevel;
//import server.model.players.skills.mining.Pickaxe;
import server.model.players.skills.slayer.Task;
//import server.model.holiday.HolidayController;
import server.model.items.EquipmentSet;
import server.model.items.ItemAssistant;
import server.model.items.Item;
//import server.model.minigames.lighthouse.DagannothMother;
//import server.model.minigames.pest_control.PestControl;
//import server.model.minigames.raids.Raids;
//import server.model.minigames.rfd.RecipeForDisaster;
//import server.model.minigames.warriors_guild.WarriorsGuild;
import server.util.Misc;
import server.clip.PathChecker;

public class AttackNPC {
	
	private static int perkOn = 0;

	public static void calculateCombatDamage(Player attacker, NPC defender, CombatType combatType, Special special) {
		int maximumAccuracy = 0;
		int maximumDamage = 0;
		int damage = 0;
		int damage2 = -1;
		int damage3 = -1;
		int maximumDefence = Misc.random(defender.defence + getBonusDefence(attacker, defender, combatType)/*defender.getStats().getDefenceForStyle(defender, combatType)*/);
		Hitmark hitmark1 = null;
		Hitmark hitmark2 = null;
		Hitmark hitmark3 = null;
		Hitmark hitmark4 = null;
		int accuracy = 0;
		if (Objects.nonNull(attacker)) {
			if (combatType.equals(CombatType.MELEE)) {
				maximumDamage = attacker.getCombat().calculateMeleeMaxHit();
				maximumAccuracy = attacker.getCombat().calculateMeleeAttack();
				if (attacker.debugMessage)
					attacker.sendMessage("Max Melee hit: "+maximumDamage);
				attacker.getPA().sendFrame126(""+maximumDamage, 27003);
				if (special != null) {
					maximumAccuracy *= special.getAccuracy();
					maximumDamage *= special.getDamageModifier();
				}
				damage = Misc.random(maximumDamage);
				accuracy = Misc.random(maximumAccuracy);


				switch (defender.npcType) {
				}

				if (attacker.doubleHit) {
					int maxHit = attacker.getCombat().calculateMeleeMaxHit();
					damage2 = Misc.random(maxHit);
				}
				if (defender.npcType == Skotizo.SKOTIZO_ID) {
					damage = attacker.getSkotizo().calculateSkotizoHit(attacker, damage);
				}
				if (defender.getHealth().getAmount() - damage < 0) {
					damage = defender.getHealth().getAmount();
				}
				if (damage2 > 0) {
					if (damage == defender.getHealth().getAmount() && defender.getHealth().getAmount() - damage2 > 0) {
						damage2 = 0;
					}
				}
				if (defender.getHealth().getAmount() - damage - damage2 < 0) {
					damage2 = defender.getHealth().getAmount() - damage;
				}
				if (damage < 0) {
					damage = 0;
				}
				if (damage2 < 0 && damage2 != -1) {
					damage2 = 0;
				}
				if (damage3 < 0 && damage3 != -1) {
					damage3 = 0;
				}
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;
				if (defender.npcType != 7413) {
					AttackPlayer.addCombatXP(attacker, CombatType.MELEE, damage + (damage2 > 0 ? 0 : 0));
				}


			} else if (combatType.equals(CombatType.CANNON)) {
				CombatType playerCombat = checkCombatType(attacker);
				maximumDamage = 30;
				if (playerCombat == CombatType.RANGE) {
					maximumAccuracy = attacker.getCombat().calculateRangeAttack();
				} else {
					maximumAccuracy = attacker.getCombat().calculateMeleeAttack();
				}


				maximumDefence = Misc.random(defender.defence + getBonusDefence(attacker, defender, playerCombat));
				
				damage = Misc.random(maximumDamage);
				accuracy = Misc.random(maximumAccuracy);
				

				if (Misc.random(maximumDefence) > accuracy && !attacker.ignoreDefence) {
					damage = 0;
				}
				
				if (defender.getHealth().getAmount() - damage < 0) {
					damage = defender.getHealth().getAmount();
				}
				if (damage2 > 0) {
					if (damage == defender.getHealth().getAmount() && defender.getHealth().getAmount() - damage2 > 0) {
						damage2 = 0;
					}
				}
				if (defender.getHealth().getAmount() - damage - damage2 < 0) {
					damage2 = defender.getHealth().getAmount() - damage;
				}
				if (damage < 0)
					damage = 0;
				if (damage2 < 0 && damage2 != -1)
					damage2 = 0;
				
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				if (defender.npcType != 7413) {
					AttackPlayer.addCombatXP(attacker, CombatType.RANGE, damage + (damage2 > 0 ? damage2 : 0));
				}
				
				int delay = 3;
				Damage hit1 = new Damage(defender, damage, delay, attacker.playerEquipment, hitmark1, combatType, special);
				attacker.getDamageQueue().add(hit1);
				
				return;
				/**
				 * Ranged attack style
				 */
			} else if (combatType.equals(CombatType.RANGE)) {
			    // 1. Calculate Max Hit
			    // CHANGED: rangeMaxHit() -> maxHit(attacker). 
			    // This method now includes the Special Attack damage multiplier automatically.
			    maximumDamage = RangeMaxHit.maxHit(attacker);

			    // 2. Calculate Accuracy
			    // This method now includes the Special Attack accuracy multiplier automatically.
			    maximumAccuracy = attacker.getCombat().calculateRangeAttack();

			    if (attacker.debugMessage)
			        attacker.sendMessage("Max Range hit: " + maximumDamage);

			    // REMOVED: if (special != null) block. 
			    // Reason: Prevents Double-Damage and Double-Accuracy bugs.

			    damage = Misc.random(maximumDamage);
			    accuracy = Misc.random(maximumAccuracy);

			    // 3. Accuracy Check
			    if (Misc.random(maximumDefence) > accuracy && !attacker.ignoreDefence) {
			        damage = 0;
			    }

			    // 4. Bolt Specials (Diamond/Ruby/Dragonstone)
			    // We run this even if damage is 0, because some bolts (like Diamond) 
			    // might force a hit through defense.
			    if (RangeExtras.wearingCrossbow(attacker) && RangeExtras.wearingBolt(attacker)) {
			        damage = RangeExtras.executeBoltSpecial(attacker, defender, new Damage(damage));
			    }

			    // 5. Venom (Blowpipe)
			    DamageEffect venomEffect = new ToxicBlowpipeEffect();
			    if (venomEffect.isExecutable(attacker)) {
			        venomEffect.execute(attacker, defender, new Damage(6));
			    }

			    // 6. Cap Damage at Enemy Health
			    if (defender.getHealth().getAmount() - damage < 0) {
			        damage = defender.getHealth().getAmount();
			    }

			    // 7. Skotizo / Custom Boss Logic
			    if (defender.npcType == Skotizo.SKOTIZO_ID) {
			        damage = attacker.getSkotizo().calculateSkotizoHit(attacker, damage);
			    }

			    // 8. Handle Multi-Hits (Dark Bow, etc)
			    if (damage2 > 0) {
			        if (damage == defender.getHealth().getAmount() && defender.getHealth().getAmount() - damage2 > 0) {
			            damage2 = 0;
			        }
			    }
			    if (defender.getHealth().getAmount() - damage - damage2 < 0) {
			        damage2 = defender.getHealth().getAmount() - damage;
			    }

			    // Safety Clamps
			    if (damage < 0) damage = 0;
			    if (damage2 < 0 && damage2 != -1) damage2 = 0;
			    if (damage3 < 0 && damage3 != -1) damage3 = 0;

			    // 9. Set Hitmarks
			    hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
			    hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
			    hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;

			    if (defender.npcType != 7413) {
			        AttackPlayer.addCombatXP(attacker, CombatType.RANGE, damage + (damage2 > 0 ? damage2 : 0));
			    }
			} else if (combatType.equals(CombatType.MAGE)) {
				maximumDamage = attacker.getCombat().magicMaxHit();
				maximumAccuracy = attacker.getCombat().mageAtk();
				if (attacker.debugMessage)
					attacker.sendMessage("Max Magic hit: "+maximumDamage);
				if (special != null) {
					maximumAccuracy *= special.getAccuracy();
					maximumDamage *= special.getDamageModifier();
				}

				damage = Misc.random(maximumDamage);
				accuracy = Misc.random(maximumAccuracy);
				if (attacker.getCombat().godSpells()) {
					if (System.currentTimeMillis() - attacker.godSpellDelay < Config.GOD_SPELL_CHARGE) {
						damage += 10;
					}
				}
				if (Misc.random(maximumDefence) > accuracy) {
					damage = 0;
					attacker.magicFailed = true;
				} else if (defender.npcType == 2881 || defender.npcType == 2882) {
					damage = 0;
					attacker.magicFailed = true;
				} else {
					attacker.magicFailed = false;
				}
				if (attacker.magicFailed) {
					damage = 0;
				}
				if (defender.npcType == Skotizo.SKOTIZO_ID) {
					damage = attacker.getSkotizo().calculateSkotizoHit(attacker, damage);
				}
				if (defender.getHealth().getAmount() - damage < 0) {
					damage = defender.getHealth().getAmount();
				}
				if (attacker.magicDef) {
					if (defender.npcType != 7413) {
						attacker.getPA().addSkillXP((damage * 3 / 3), 1);
						attacker.getPA().refreshSkill(1);
					}
				}
				if (defender.npcType == 7413) {
					damage = attacker.getCombat().magicMaxHit();
				}
				hitmark1 = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark2 = damage2 > 0 ? Hitmark.HIT : Hitmark.MISS;
				hitmark3 = damage3 > 0 ? Hitmark.HIT : Hitmark.MISS;
				
				
				
				if (defender.npcType != 7413) {
					AttackPlayer.addCombatXP(attacker, CombatType.MAGE, damage + (damage2 > 0 ? damage2 : 0));
					attacker.getPA().refreshSkill(6);
				}
			}
		}
		
		attacker.attackTimer = attacker.getCombat().getAttackDelay(ItemAssistant.getItemName(attacker.playerEquipment[attacker.playerWeapon]).toLowerCase());
		Optional<Integer> optional = PoisonedWeapon.getOriginal(attacker.playerEquipment[attacker.playerWeapon]);
		if ((optional.isPresent() && optional.get() == 1249 || attacker.getItems().isWearingItem(1249, attacker.playerWeapon)) && attacker.usingSpecial) {
			return;
		}
		int delay = attacker.hitDelay;

		Damage hit1 = new Damage(defender, damage, delay, attacker.playerEquipment, hitmark1, combatType, special);
		attacker.getDamageQueue().add(hit1);
		if (special != null) {
			special.activate(attacker, defender, hit1);
		}
		if (damage2 > -1) {
			attacker.getDamageQueue().add(new Damage(defender, damage2, delay, attacker.playerEquipment, hitmark2, combatType));
		}
		if (damage3 > -1) {
			attacker.getDamageQueue().add(new Damage(defender, damage3, delay, attacker.playerEquipment, hitmark3, combatType));
		}
		// --- NMZ RECURRENT DAMAGE POWER-UP ---
        // Note: We use delay + 1 so the red recurrent hitsplat visually pops up 
        // one tick AFTER your main hit, just like in OSRS!
        if (attacker.inNMZ && attacker.recurrentDamageTimer > 0) {
            
            if (damage > 0) {
                int extra1 = (int) (damage * 0.75);
                if (extra1 > 0) {
                    attacker.getDamageQueue().add(new Damage(defender, extra1, delay + 1, attacker.playerEquipment, Hitmark.HIT, combatType));
                }
            }
            
            if (damage2 > 0) {
                int extra2 = (int) (damage2 * 0.75);
                if (extra2 > 0) {
                    attacker.getDamageQueue().add(new Damage(defender, extra2, delay + 1, attacker.playerEquipment, Hitmark.HIT, combatType));
                }
            }
            
            if (damage3 > 0) {
                int extra3 = (int) (damage3 * 0.75);
                if (extra3 > 0) {
                    attacker.getDamageQueue().add(new Damage(defender, extra3, delay + 1, attacker.playerEquipment, Hitmark.HIT, combatType));
                }
            }
        }
	}

	public static int salveDamage(Player c) {
		int damage = Misc.random(c.getCombat().calculateMeleeMaxHit());
		return damage *= 1.15;
	}

	private static int getBonusDefence(Player player, NPC npc, CombatType type) {
		if (type.equals(CombatType.MELEE)) {
			switch (npc.npcType) {
			//case 965:
				//return EquipmentSet.VERAC.isWearing(player) || EquipmentSet.CVERACS.isWearing(player) ? +500 : 5000;
			case 5890:
			case 7144:
			case 7145:
			case 7146:
			case 7604:
			case 7605:
			case 7606:
				return 500;

			case 7544:
				return 400;
			case 5129:
				return 500;
			case 4922:
				return 500;
			}
		} else if (type.equals(CombatType.MAGE)) {
			switch (npc.npcType) {
			case 2042:
				return -150;
			case 319:
				return +80;
			case 2044:
			case 7544:
				return 1550;
			case 963:
				return +7000;
			case 965:
			case 5129:
				return 300;
			case 4922:
				return 500;
			case 7144:
			case 7145:
			case 7146:
			case 5890:
			case 7604:
			case 7605:
			case 7606:
				return 500;
			}
		} else if (type.equals(CombatType.RANGE)) {
			switch (npc.npcType) {
			case 492:
				return 50;
			case 2042:
			case 2043:
			case 5890:
			case 7544:
				return 1500;
			case 5129:
				return 500;
			case 4922:
				return 300;
			case 6766:
				return 280;
			case 319:
				return 80;
			case 2044:
				return -150;
			case 963:
				return +7000;
			case 965:
				return 300;
			case 7144:
			case 7145:
			case 5862:
			case 7146:
			case 7604:
			case 7605:
			case 7606:
				return 500;
			}
		}
		return 0;
	}

	public static void delayedHit(final Player c, final int i, Damage damage) {
		if (i > NPCHandler.npcs.length) {
			return;
		}
		NPC npc = NPCHandler.npcs[i];
		if (npc == null || npc.isDead) {
			return;
		}
		if (npc.getHealth().getAmount() <= 0) {
			if (npc.getHealth().getAmount() <= 0) {
				npc.isDead = true;
			}
			return;
		}
		if (NPCHandler.npcs[i] != null) {
			if (NPCHandler.npcs[i].isDead) {
				c.npcIndex = 0;
				return;
			}
			/**
			 * Damage applied and maybe changed
			 */
			//System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc");
			switch (npc.npcType) {
			/**
			 * Melee only
			 */
			case 986:
			case 988:
			case 6374:
				if (!c.usingMelee) {
					damage.setAmount(0);
				}
				break;

			/**
			 * Range only
			 */
			case 987:
			case 6377:
				if (!c.usingBow && !c.usingCross && !c.usingOtherRangeWeapons && !c.usingBallista) {
					damage.setAmount(0);
				}
				break;
			case 320:
				if (!Boundary.isIn(c, Boundary.CORPOREAL_BEAST_LAIR)) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You cannot do this from here.");
					return;
				}
				break;

			/**
			 * No melee
			 */
			case 2042: // Zulrah
			case 2043:
			case 2044:
				if (c.usingMelee) {
					damage.setAmount(0);
				}
				break;
			case 5862:
				if (Boundary.isIn(c, Boundary.WITHIN_BOUNDARY_CERB)) {
					c.getCerberus().cerberusSpecials();
				} else {
					damage.setAmount(0);
					c.sendMessage("@red@You should keep yourself in the middle so you don't get burned.");
				}
				break;
			case Skotizo.SKOTIZO_ID:
					c.getSkotizo().skotizoSpecials();
				break;
				
			case Skotizo.AWAKENED_ALTAR_NORTH:
			case Skotizo.AWAKENED_ALTAR_SOUTH:
			case Skotizo.AWAKENED_ALTAR_WEST:
			case Skotizo.AWAKENED_ALTAR_EAST:
					if (c.playerEquipment[c.playerWeapon] == 19675) {
						c.getSkotizo().arclightEffect(npc);
						return;
					}
				break;
			}
			boolean rejectsFaceUpdate = false;
			if (npc.npcType >= 2042 && npc.npcType <= 2044 || npc.npcType == 6720) {
				if (c.getZulrahEvent().getNpc() != null && c.getZulrahEvent().getNpc().equals(npc)) {
					if (c.getZulrahEvent().getStage() == 1) {
						rejectsFaceUpdate = true;
					}
				}
				if (c.getZulrahEvent().isTransforming()) {
					return;
				}
			}
			if (npc.getCombatScript() != null && !npc.getCombatScript().isCanAttack()) {
				rejectsFaceUpdate = true;
			}
			if (!rejectsFaceUpdate) {
				NPCHandler.npcs[i].faceEntity(c.getIndex());
			}
			if (NPCHandler.npcs[i].underAttackBy > 0 && World.getWorld().getNpcHandler().getsPulled(i) && NPCHandler.npcs[i].getTargetingDelay() == 0) {
				NPCHandler.npcs[i].killerId = npc.getCombatScript() != null && !npc.getCombatScript().isCanAttack() ? 0 : c.getIndex();
				NPCHandler.npcs[i].setTargetingDelay(2);
			} else if (NPCHandler.npcs[i].underAttackBy < 0 && !World.getWorld().getNpcHandler().getsPulled(i)) {
				NPCHandler.npcs[i].killerId = npc.getCombatScript() != null && !npc.getCombatScript().isCanAttack() ? 0 : c.getIndex();
			}

			if (damage.getSpecial() != null) {
				damage.getSpecial().hit(c, NPCHandler.npcs[i], damage);
			}

			c.lastNpcAttacked = i;
			if (damage.getCombatType() != null) {
				switch (damage.getCombatType()) {
				case MELEE:
					NPCHandler.triggerNPCBlock(c, i); // Trigger block on impact
					NPCHandler.npcs[i].appendDamage(c, damage);
					if (c.playerEquipment[3] == 22325 || c.playerEquipment[3] == 33380) {
						if (multiAttackScythe(c, i)) {
							return;
						}
					}
					break;
				case CANNON:

					NPCHandler.triggerNPCBlock(c, i); // Trigger block on impact
					NPCHandler.npcs[i].appendDamage(c, damage);
					break;

				case RANGE:
					if (c.dbowSpec) {
						c.dbowSpec = false;
					}
					boolean dropArrows = true;
					// Check for Crystal Bows
					if (c.lastWeaponUsed >= 4212 && c.lastWeaponUsed <= 4223) {
						dropArrows = false;
					}

// Check for Custom/Charge-based Bows (Craw's Bow, etc) that don't drop ammo
					if (c.playerEquipment[c.playerWeapon] == 22550 || c.playerEquipment[c.playerWeapon] == 22547 ||
							c.playerEquipment[c.playerWeapon] == 33781 || c.playerEquipment[c.playerWeapon] == 33782 ||
							c.playerEquipment[c.playerWeapon] == 33783 || c.playerEquipment[c.playerWeapon] == 23901 ||
							c.playerEquipment[c.playerWeapon] == 23902 || c.playerEquipment[c.playerWeapon] == 23903 ||
							c.playerEquipment[c.playerWeapon] == 23983) {
						dropArrows = false;
					}
					for (int noArrowId : c.NO_ARROW_DROP) {
						if (c.lastWeaponUsed == noArrowId) {
							dropArrows = false;
							break;
						}
					}
					if (dropArrows) {
						c.getItems().dropArrowNpc(NPCHandler.npcs[i]);
						if (c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766 || c.playerEquipment[3] == 12767
								|| c.playerEquipment[3] == 12768) {
							c.getItems().dropArrowNpc(NPCHandler.npcs[i]);
						}
					}
					if (NPCHandler.npcs[i].attackTimer > 3) {
						if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044 && npc.npcType != 3127 && npc.npcType != 319) {

							int defAnim = c.getCombat().npcDefenceAnim(i);
							int defSound = c.getCombat().npcDefenceSound(i);
							if(defAnim != -1) {
								NPCHandler.startAnimation(defAnim, i);
								if(defSound > 0)
									c.getPA().sendNPCSound(defSound, 0, 6, 9);
							}
						}
					}
					c.rangeEndGFX = RangeData.getRangeEndGFX(c);
					c.ignoreDefence = false;
					c.multiAttacking = false;

					if (c.playerEquipment[3] == 10034 || c.playerEquipment[3] == 10033
							|| c.playerEquipment[3] == 11959) {
						if (multiAttackRange(c, i)) {
							return;
						}
					}
					if (c.rangeEndGFX > 0) {
						if (c.rangeEndGFXHeight) {
							NPCHandler.npcs[i].gfx100(c.rangeEndGFX);
						} else {
							NPCHandler.npcs[i].gfx0(c.rangeEndGFX);
						}
					}
					if (c.killingNpcIndex != c.oldNpcIndex) {
						c.totalDamageDealt = 0;
					}

					NPCHandler.triggerNPCBlock(c, i); // Trigger block on impact
					NPCHandler.npcs[i].appendDamage(c, damage);
					break;

				case MAGE:
					if (c.spellSwap) {
						c.spellSwap = false;
						c.setSidebarInterface(6, 16640);
						c.playerMagicBook = 2;
						c.gfx0(-1);
					}
					c.usingMagic = true;
					if (c.getCombat().getEndGfxHeight() == 100 && damage.getAmount() > 0) { // end
						// GFX
						NPCHandler.npcs[i].gfx100(MagicData.MAGIC_SPELLS[c.oldSpellId][5]);
						if (NPCHandler.npcs[i].attackTimer > 3) {
							if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044 && npc.npcType != 3127 && npc.npcType != 7413) {
								NPCHandler.startAnimation(c.getCombat().npcDefenceAnim(i), i);
							}
						}
					} else if (damage.getAmount() > 0) {
						NPCHandler.npcs[i].gfx0(MagicData.MAGIC_SPELLS[c.oldSpellId][5]);
					}
					if (damage.getAmount() == 0) {
						if (World.getWorld().getNpcHandler().getNPCs()[i].attackTimer > 3) {
							if (npc.npcType != 2042 && npc.npcType != 2043 & npc.npcType != 2044) {

								NPCHandler.triggerNPCBlock(c, i); // Trigger block on impact
							}
						}
						NPCHandler.npcs[i].gfx100(85);
					}
					if (multiAttackMagic(c, i)) {
						return;
					}
					if (damage.getAmount() > 0) {
						int freezeDelay = c.getCombat().getFreezeTime();// freeze
						if (freezeDelay > 0 && NPCHandler.npcs[i].freezeTimer == 0 && isFreezable(NPCHandler.npcs[i])) {
							NPCHandler.npcs[i].freezeTimer = freezeDelay;
						}
						switch (MagicData.MAGIC_SPELLS[c.oldSpellId][0]) {
						case 12901:
						case 12919: // blood spells
						case 12911:
						case 12929:
							int heal = Misc.random(damage.getAmount() / 2);
							c.getHealth().increase(heal);
							c.getPA().refreshSkill(3);
							break;
						}
							
						/*case 12891:
							if (Boundary.isIn(c, Boundary.DESERT_BOUNDARY)) {
								c.getDiaryManager().getDesertDiary().progress(DesertDiaryEntry.CAST_BARRAGE);
							}
						}*/
						if (damage.getAmount() > 0) {
							
							NPCHandler.npcs[i].appendDamage(c, damage);
						}
					}
					break;

				default:
					break;
				}
			}
		}
		
		c.multiAttacking = false;
		c.killingNpcIndex = c.oldNpcIndex;
		NPCHandler.npcs[i].updateRequired = true;
		c.usingMagic = false;
		c.oldSpellId = 0;
		c.getCombat().checkVenomousItems();
		c.getCombat().checkDemonItems();
		Degrade.degrade(c);
		if (c.bowSpecShot <= 0) {
			c.oldNpcIndex = 0;
			c.projectileStage = 0;
			c.doubleHit = false;
			c.lastWeaponUsed = 0;
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot >= 2) {
			c.bowSpecShot = 0;
		}
		if (c.bowSpecShot == 1) {
			c.hitDelay = 2;
			c.bowSpecShot = 0;
		}
		c.specAccuracy = 1.0;
		c.specDamage = 1.0;
	}

	private static boolean isFreezable(NPC npc) {
		switch (npc.npcType) {
		case 2042:
		case 2043:
		case 2044:
		case 7544:
		case 5129:
		case 4922:
		case 2205:
		case 3129:
		case 2215:
		case 3162:
			return false;
		}
		return true;
	}

	private static boolean multiAttackMagic(Player player, int i) {
		boolean found = false;
		for (int j = 0; j < NPCHandler.npcs.length; j++) {
			if (NPCHandler.npcs[j] != null && NPCHandler.npcs[j].getHealth().getMaximum() > 0) {
				if (NPCHandler.npcs[j].getHeight() != player.getHeight()) {
					continue;
				}
				int nX = NPCHandler.npcs[j].getX();
				int nY = NPCHandler.npcs[j].getY();
				int pX = NPCHandler.npcs[i].getX();
				int pY = NPCHandler.npcs[i].getY();
				if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
					if (player.getCombat().multis() && NPCHandler.npcs[i].inMulti() && NPCHandler.npcs[j].getHeight() == NPCHandler.npcs[i].getHeight()) {
						player.getCombat().appendMultiBarrageNPC(j, player.magicFailed);
						found = true;
					}
				}
			}
		}
		return found;
	}
	
	private static boolean multiAttackScythe(Player player, int i) {
		boolean found = false;
		for (int j = 0; j < NPCHandler.npcs.length; j++) {
			if (NPCHandler.npcs[j] != null && NPCHandler.npcs[j].getHealth().getMaximum() > 0) {
				if (NPCHandler.npcs[j].getHeight() != player.getHeight()) {
					continue;
				}
				int nX = NPCHandler.npcs[j].getX();
				int nY = NPCHandler.npcs[j].getY();
				int pX = NPCHandler.npcs[i].getX();
				int pY = NPCHandler.npcs[i].getY();
				if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
					if (NPCHandler.npcs[j].getHeight() == NPCHandler.npcs[i].getHeight()) {
						player.getCombat().appendMultiScytheNPC(j);
						found = true;
					}
				}
			}
		}
		return found;
	}
	

	private static boolean multiAttackRange(Player player, int i) {
		boolean found = false;
		for (int j = 0; j < NPCHandler.npcs.length; j++) {
			if (NPCHandler.npcs[j] != null && NPCHandler.npcs[j].getHealth().getMaximum() > 0) {
				if (NPCHandler.npcs[j].getHeight() != player.getHeight()) {
					continue;
				}
				int nX = NPCHandler.npcs[j].getX();
				int nY = NPCHandler.npcs[j].getY();
				int pX = NPCHandler.npcs[i].getX();
				int pY = NPCHandler.npcs[i].getY();
				if ((nX - pX == -1 || nX - pX == 0 || nX - pX == 1) && (nY - pY == -1 || nY - pY == 0 || nY - pY == 1)) {
					if (NPCHandler.npcs[i].inMulti() && NPCHandler.npcs[j].getHeight() == NPCHandler.npcs[i].getHeight()) {
						player.getCombat().appendMultiChinchompa(j);
						found = true;
					}
				}
			}
		}
		return found;
	}

	public static boolean armaNpc(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 6229:
		case 6230:
		case 6231:
		case 6232:
		case 6233:
		case 6234:
		case 6222:
		case 3162:
		case 3163:
		case 3164:
		case 3165:
		case 3166:
		case 3167:
		case 3168:
		case 3169:
		case 3174:
		case 6235:
		case 6236:
		case 6237:
		case 6238:
		case 6239:
		case 6240:
		case 6241:
		case 6242:
		case 6243:
		case 6244:
		case 6245:
		case 6246:
			return true;
		}
		return false;
	}
	public static boolean xericRanged(int i) {
		switch (NPCHandler.npcs[i].npcType) {
		case 7531:
		case 7538:
			return true;
		}
		return false;
	}

	public static void resetSpells(Player c) {
		if (c.playerMagicBook == 0) {
			c.setSidebarInterface(6, 1151); // modern
		}
		if (c.playerMagicBook == 1) {
			c.setSidebarInterface(6, 12855); // ancient
		}
		if (c.playerMagicBook == 2) {
			c.setSidebarInterface(6, 29999); // lunar
		}
	}

	public static boolean isAttackable(Player player, int i) {
		if (!NPCHandler.npcs[i].inMulti() && NPCHandler.npcs[i].npcType != 7563 && NPCHandler.npcs[i].npcType != 5890 && NPCHandler.npcs[i].npcType != 7563) {
			if (!Boundary.isIn(player, Boundary.OLM) && !Boundary.isIn(player, Boundary.RAIDS)) {
				if (NPCHandler.npcs[i].underAttackBy > 0 && NPCHandler.npcs[i].underAttackBy != player.getIndex()) {
					player.npcIndex = 0;
					player.sendMessage("This monster is already in combat.");
					return false;
				}
			}
			
		}
		if (NPCHandler.npcs[i].npcType != 5890 && NPCHandler.npcs[i].npcType != 7563 && NPCHandler.npcs[i].npcType != 5916 && NPCHandler.npcs[i].npcType != 7554 && NPCHandler.npcs[i].npcType != 7555 && NPCHandler.npcs[i].npcType != 7553) {
			if ((player.underAttackBy > 0 || player.underAttackBy2 > 0) && player.underAttackBy2 != NPCHandler.npcs[i].npcType && !player.inMulti()) {
				player.getCombat().resetPlayerAttack();
				player.sendMessage("I am already under attack.");
				return false;
			}
		}
		return true;
	}

	public static void attackNpc(Player c, int i) {
		if (NPCHandler.npcs[i] == null) {
			return;
		}
		if (NPCDefinitions.get(NPCHandler.npcs[i].npcType).getNpcCombat() < 0)
			return;
		if (c.playerEquipment[c.playerWeapon] == 12904 && c.usingSpecial) {
			c.usingSpecial=false;
			c.getItems().updateSpecialBar();
			c.getCombat().resetPlayerAttack();
			return;
		}
		NPC npc = NPCHandler.npcs[i];
		resetSpells(c);
		if (NPCHandler.npcs[i] != null) {
			c.getCombat().strBonus = c.playerBonus[10];
			if (npc.isDead || npc.getHealth().getMaximum() <= 0) {
				c.usingMagic = false;
				c.faceUpdate(0);
				c.npcIndex = 0;
				return;
			}
			
			if (c.teleTimer > 0) {
				return;
			}
			
			if (c.respawnTimer > 0) {
				c.npcIndex = 0;
				return;
			}

			c.followId2 = NPCHandler.npcs[i].getIndex();
			//c.getPA().followNpc();
			
			if (c.playerEquipment[c.playerWeapon] == 4734 && c.playerEquipment[c.playerArrows] != 4740) {
				c.sendMessage("You must use bolt racks with the karil's crossbow.");
				c.npcIndex = 0;
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (NPCHandler.npcs[i].npcType == 6611 || NPCHandler.npcs[i].npcType == 6612) {
				List<NPC> minion = Arrays.asList(NPCHandler.npcs);
				if (minion.stream().filter(Objects::nonNull).anyMatch(n -> n.npcType == 5054 && !n.isDead && n.getHealth().getAmount() > 0)) {
					c.sendMessage("You must kill Vet'ions minions before attacking him.");
					c.npcIndex = 0;
					return;
				}
			}
			if (NPCHandler.npcs[i].npcType != 5890 && NPCHandler.npcs[i].npcType != 5916) {
				if ((c.underAttackBy > 0 || c.underAttackBy2 > 0) && c.underAttackBy2 != i && !c.inMulti()) {
						c.getCombat().resetPlayerAttack();
						c.sendMessage("I am already under attack.");
						//System.out.println("Attacked");
						return;
				}
			}
			if (NPCHandler.npcs[i].spawnedBy != c.getIndex() && NPCHandler.npcs[i].spawnedBy > 0 && !Boundary.isIn(c, Boundary.XERIC)) {
				c.getCombat().resetPlayerAttack();
				c.sendMessage("This monster was not spawned for you.");
				return;
			}
			if (c.getX() == NPCHandler.npcs[i].getX() && c.getY() == NPCHandler.npcs[i].getY()) {
				c.getPA().walkTo(0, 1);
			}
			if (Boundary.isIn(NPCHandler.npcs[i], Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(c, Boundary.GODWARS_BOSSROOMS)) {
				c.getCombat().resetPlayerAttack();
				c.sendMessage("You cannot attack that npc from outside the room.");
				return;
			}
			int npcType = NPCHandler.npcs[i].npcType;
			c.followId2 = i;
			c.followId = 0;
			if (c.attackTimer <= 0) {
				c.usingBow = false;
				c.usingArrows = false;
				c.usingOtherRangeWeapons = false;
				c.usingCross = c.playerEquipment[c.playerWeapon] == 4734 || c.playerEquipment[c.playerWeapon] == 21902 || c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 33117 || c.playerEquipment[c.playerWeapon] == 33578 || c.playerEquipment[c.playerWeapon] == 11785 || c.playerEquipment[c.playerWeapon] == 33124 || c.playerEquipment[c.playerWeapon] == 33094 || c.playerEquipment[c.playerWeapon] == 21012 || c.playerEquipment[c.playerWeapon] == 33114;
				c.usingBallista = c.playerEquipment[c.playerWeapon] == 19481 || c.playerEquipment[c.playerWeapon] == 19478;
				c.bonusAttack = 0;
				c.rangeItemUsed = 0;
				c.projectileStage = 0;
				c.usingRangeWeapon = false;
				c.usingMelee = false;
				c.usingMagic = false;
				CombatType combatType;
				if (c.autocasting) {
					c.spellId = c.autocastId;
					c.usingMagic = true;
				}
				if (c.spellId > 0) {
					c.usingMagic = true;
					c.usingRangeWeapon = false;
					c.usingArrows = false;
					c.usingOtherRangeWeapons = false;
					c.usingCross = false;
					c.usingBallista = false;
					c.usingBow = false;
				}
				/**
				 * Cancel out any other style operation
				 */
				if (c.usingMagic) {
					c.usingCross = false;
					c.usingBallista = false;
				}
				
				switch (c.playerEquipment[c.playerWeapon]) {
	
				}
				c.attackTimer = c.getCombat().getAttackDelay(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
				c.specAccuracy = 1.0;
				c.specDamage = 1.0;
				if (!c.usingMagic) {
					for (int bowId : c.BOWS) {
						if (c.playerEquipment[c.playerWeapon] == bowId && System.currentTimeMillis() - c.switchDelay >= 600) {
							c.usingBow = true;
							if (bowId == 19481 || bowId == 19478) {
								c.usingBow = false;
								c.usingBallista = true;
							}
							c.rangeDelay = 3;
							for (int arrowId : c.ARROWS) {
								if (c.playerEquipment[c.playerArrows] == arrowId) {
									c.usingArrows = true;
								}
							}
						}
					}

					for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
						if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
							c.usingOtherRangeWeapons = true;
						}
					}
				}
				//System.out.println("inStream.currentOffset = " +c.getOutStream().currentOffset);
				if (!c.usingMagic && !c.usingCross && !c.usingBallista && !c.usingBow && !c.usingOtherRangeWeapons) {
					c.usingMelee = true;
				}
				if (armaNpc(i) && !c.usingBallista && !c.usingCross && !c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You need to use ranged weapons to attack this monster!");
					return;
				}
				if (xericRanged(i) && !c.usingBallista && !c.usingCross && !c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons) {
					c.getCombat().resetPlayerAttack();
					c.sendMessage("You need to use ranged weapons to attack this monster!");
					return;
				}
				
				NPC theNPC = NPCHandler.npcs[i];
				double distanceToNpc = theNPC.getDistance(c.getX(), c.getY());
				int distance = 1;
				if(Vorkath.inVorkath(c) == true && !c.usingOtherRangeWeapons && !c.usingBallista && !c.usingBow && !c.usingMagic) {
					distance = 4;
				}
				if (c.getCombat().usingHally() && !c.usingOtherRangeWeapons && !c.usingBallista && !c.usingBow && !c.usingMagic)
					distance = 2;
				if (c.usingOtherRangeWeapons && !c.usingBow && !c.usingMagic)
					distance = 4;
				if (c.usingBallista)
					distance = 6;
				if (c.usingBow || c.usingMagic || c.autocasting || c.playerEquipment[c.playerWeapon] == 11907 || c.playerEquipment[c.playerWeapon] == 12899)
					distance = 8;
				if (distanceToNpc > distance + 2) {
					c.attackTimer = 1;
					return;
				}
				if(NPCHandler.npcs[i].npcType == 7706 && c.usingBow || c.usingMagic || c.autocasting ||c.playerEquipment[c.playerWeapon] == 11907 || c.playerEquipment[c.playerWeapon] == 12899){
					distance = 20;
				}
				boolean isSkotizoAltar = Skotizo.Altar.getByNpcId(theNPC.npcType) != null;

				if (!PathChecker.isProjectilePathClear(c.getX(), c.getY(), c.getHeight(), theNPC.getX(), theNPC.getY())
						&& !Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
						&& !isSkotizoAltar
						&& theNPC.npcType != 7559
						&& theNPC.npcType != 7560) {

					c.attackTimer = 1;
					return;
				}
				if (c.playerEquipment[c.playerWeapon] != 22550 && c.playerEquipment[c.playerWeapon] != 22547 &&
						c.playerEquipment[c.playerWeapon] != 23901 && c.playerEquipment[c.playerWeapon] != 23902 && c.playerEquipment[c.playerWeapon] != 23903 &&
						c.playerEquipment[c.playerWeapon] != 23983) {
				if (!c.usingBallista && !c.usingCross && !c.usingArrows && c.usingBow && (c.playerEquipment[c.playerWeapon] < 4212 || c.playerEquipment[c.playerWeapon] > 4223)) {
					c.sendMessage("You have run out of arrows!");
					c.stopMovement();
					c.npcIndex = 0;
					return;
					}
				}

				if (c.getHealth().getAmount() > 0 && !c.isDead && NPCHandler.npcs[i].getHealth().getMaximum() > 0) {
					if (!c.usingMagic) {
						c.startAnimation(c.getCombat().getWepAnim(ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase()));
						c.getPA().sendSound(c.getCombat().getAttackSound(c.playerEquipment[c.playerWeapon]), 0, 6, c.EffectVolume);

					} else {
						c.startAnimation(MagicData.MAGIC_SPELLS[c.spellId][2]);
					}
				}
				if (c.getCombat().usingCrystalBow() && c.usingArrows) {
					c.sendMessage("You cannot use ammo with a crystal bow.");
					return;
				}
				if (!c.getCombat().correctBowAndArrows() && Config.CORRECT_ARROWS && c.usingBow && !c.getCombat().usingCrystalBow() && c.playerEquipment[c.playerWeapon] != 9185
						&& c.playerEquipment[c.playerWeapon] != 4734 && c.playerEquipment[c.playerWeapon] != 21902 && c.playerEquipment[c.playerWeapon] != 22550 && 
						c.playerEquipment[c.playerWeapon] != 11785 && c.playerEquipment[c.playerWeapon] != 21012 && c.playerEquipment[c.playerWeapon] != 12926 && 
						c.playerEquipment[c.playerWeapon] != 19478 && c.playerEquipment[c.playerWeapon] != 19481) {
					c.sendMessage("You can't use " + ItemAssistant.getItemName(c.playerEquipment[c.playerArrows]).toLowerCase() + "'s with a "
							+ ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase() + ".");
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}
				if (c.playerEquipment[c.playerWeapon] == 9185 && !c.getCombat().properBolts() || 
						c.playerEquipment[c.playerWeapon] == 11785 && !c.getCombat().properBolts()  || 
						c.playerEquipment[c.playerWeapon] == 21902 && !c.getCombat().properBolts() ||  
						c.playerEquipment[c.playerWeapon] == 21012 && !c.getCombat().properBolts()) {
					c.sendMessage("You must use bolts with a crossbow.");
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.playerEquipment[c.playerWeapon] == 19478 && !c.getCombat().properJavelins() || c.playerEquipment[c.playerWeapon] == 19481 && !c.getCombat().properJavelins()) {
					c.sendMessage("You must use javelins with a ballista.");
					c.stopMovement();
					c.getCombat().resetPlayerAttack();
					return;
				}
				if (c.usingBow || c.usingMagic || c.usingOtherRangeWeapons || c.usingBallista
						|| (c.getCombat().usingHally() || c.playerEquipment[c.playerWeapon] == 11907 || c.playerEquipment[c.playerWeapon] == 12899 && distanceToNpc <= 2)) {
					c.stopMovement();
				}
				if (!c.getCombat().checkMagicReqs(c.spellId)) {
					c.stopMovement();
					c.npcIndex = 0;
					return;
				}
				if (c.usingBow || c.usingOtherRangeWeapons || c.usingCross || c.usingBallista) {
					combatType = CombatType.RANGE;
				} else if (c.usingMagic) {
					combatType = CombatType.MAGE;
				} else {
					combatType = CombatType.MELEE;
				}
				
				if (c.usingMagic && !c.autocasting) {
					c.followId2 = 0;
					c.stopMovement();
				}

				c.faceUpdate(i);
				NPCHandler.npcs[i].underAttackBy = c.getIndex();
				NPCHandler.npcs[i].lastDamageTaken = System.currentTimeMillis();
				NPCHandler.npcs[i].underAttack = true;			
				if (c.getTargeted() == null || !c.getTargeted().equals(npc)) {
					c.setTargeted(NPCHandler.npcs[i]);
					c.getPA().sendEntityTarget(1, npc);
				}
				
				if (c.usingSpecial && (c.playerEquipment[c.playerWeapon] == 22516 || !c.usingMagic)) {
					c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
					c.lastArrowUsed = c.playerEquipment[c.playerArrows];
					Special special = Specials.forWeaponId(c.playerEquipment[c.playerWeapon]);
					if (special == null) {
						return;
					}
					if (special.getRequiredCost() > c.specAmount) {
						c.sendMessage("You don't have enough power left.");
						c.usingSpecial = false;
						c.getItems().updateSpecialBar();
						c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
						c.npcIndex = 0;
						return;
					}
					c.doubleHit = false;
					c.specEffect = 0;
					c.projectileStage = 0;
					c.specMaxHitIncrease = 2;
					c.logoutDelay = System.currentTimeMillis();
					c.oldNpcIndex = i;
					c.specAmount -= special.getRequiredCost();
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					calculateCombatDamage(c, NPCHandler.npcs[i], combatType, special);
					if (c.usingOtherRangeWeapons || c.usingBow) {
						if (c.fightMode == 2) {
							c.attackTimer--;
						}
					}
					c.usingSpecial = false;
					c.getItems().updateSpecialBar();
					c.getItems().addSpecialBar(c.playerEquipment[c.playerWeapon]);
					return;
				}
				c.specMaxHitIncrease = 0;
				c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
				c.lastArrowUsed = c.playerEquipment[c.playerArrows];
				if (!c.usingBow && !c.usingMagic && !c.usingOtherRangeWeapons && !c.usingBallista) { // melee hit delay
					c.followId2 = NPCHandler.npcs[i].getIndex();
					//c.getPA().followNpc();
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.projectileStage = 0;
					c.oldNpcIndex = i;
				}

				if (c.usingBow && !c.usingOtherRangeWeapons && !c.usingMagic || c.usingCross || c.usingBallista) { //ranged hit delay
					if (c.usingCross)
						c.usingBow = true;
					if (c.fightMode == 2)
						c.attackTimer--;
					c.lastArrowUsed = c.playerEquipment[c.playerArrows];
					c.lastWeaponUsed = c.playerEquipment[c.playerWeapon];
					c.gfx100(c.getCombat().getRangeStartGFX());
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					
					
					
					if (c.playerEquipment[c.playerWeapon] >= 4212 && c.playerEquipment[c.playerWeapon] <= 4223) {
						c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
						c.crystalBowArrowCount++;
						c.lastArrowUsed = 0;
						c.getCombat().fireProjectileNpc(0);
					} else if (c.playerEquipment[c.playerWeapon] == 12926) {
						c.getCombat().fireProjectileNpc(0);
					} else {
						c.rangeItemUsed = c.playerEquipment[c.playerArrows];
						c.getItems().deleteArrow();
						if (c.playerEquipment[3] == 11235 || c.playerEquipment[3] == 12765 || c.playerEquipment[3] == 12766 || c.playerEquipment[3] == 12767
								|| c.playerEquipment[3] == 12768) {
							c.getItems().deleteArrow();
						}
						c.getCombat().fireProjectileNpc(0);
					}
				}

				if (c.usingOtherRangeWeapons && !c.usingMagic && !c.usingBow) {
					c.usingRangeWeapon = true;
					c.rangeItemUsed = c.playerEquipment[c.playerWeapon];
					c.getItems().deleteEquipment();
					c.gfx100(c.getCombat().getRangeStartGFX());
					c.lastArrowUsed = 0;
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.projectileStage = 1;
					c.oldNpcIndex = i;
					c.getCombat().fireProjectileNpc(0);
				}
				if (c.usingMagic) { // magic hit delay
					int pX = c.getX();
					int pY = c.getY();
					int nX = NPCHandler.npcs[i].getX();
					int nY = NPCHandler.npcs[i].getY();
					int offX = (pY - nY) * -1;
					int offY = (pX - nX) * -1;
					c.projectileStage = 2;
					c.stopMovement();
					if (MagicData.MAGIC_SPELLS[c.spellId][3] > 0) {
						if (c.getCombat().getStartGfxHeight() == 100) {
							c.gfx100(MagicData.MAGIC_SPELLS[c.spellId][3]);
						} else {
							c.gfx0(MagicData.MAGIC_SPELLS[c.spellId][3]);
						}
					}
					if (MagicData.MAGIC_SPELLS[c.spellId][4] > 0) {
						c.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, 78, MagicData.MAGIC_SPELLS[c.spellId][4], c.getCombat().getStartHeight(), c.getCombat().getEndHeight(),
								i + 1, 50);
					}
					c.hitDelay = c.getCombat().getHitDelay(i, ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase());
					c.oldNpcIndex = i;
					c.oldSpellId = c.spellId;
					c.spellId = 0;
					if (!c.autocasting)
						c.npcIndex = 0;
				}

				if (System.currentTimeMillis() - c.lastDamageCalculation > 1000) {
					calculateCombatDamage(c, NPCHandler.npcs[i], combatType, null);
					c.lastDamageCalculation = System.currentTimeMillis();
				}
				if (c.usingOtherRangeWeapons || c.usingBow) {
					if (c.fightMode == 2)
						c.attackTimer--;
				}

				if (c.usingBow && Config.CRYSTAL_BOW_DEGRADES) { // crystal bow
																	// degrading
					if (c.playerEquipment[c.playerWeapon] == 4212) { // new
																		// crystal
																		// bow
																		// becomes
																		// full
																		// bow
																		// on
																		// the
																		// first
																		// shot
						c.getItems().wearItem(4214, 1, 3);
					}

					if (c.crystalBowArrowCount >= 250) {
						switch (c.playerEquipment[c.playerWeapon]) {

						case 4223: // 1/10 bow
							c.getItems().wearItem(-1, 1, 3);
							c.sendMessage("Your crystal bow has fully degraded.");
							if (!c.getItems().addItem(4207, 1)) {
								World.getWorld().getItemHandler().createGroundItem(c, 4207, c.getX(), c.getY(), c.getHeight(), 1);
							}
							c.crystalBowArrowCount = 0;
							break;

						default:
							c.getItems().wearItem(++c.playerEquipment[c.playerWeapon], 1, 3);
							c.sendMessage("Your crystal bow degrades.");
							c.crystalBowArrowCount = 0;
							break;
						}
					}
				}
			}
		}
	}
	
	public static CombatType checkCombatType(Player c) {
		boolean usingBow = false;
		boolean usingOtherRangeWeapons = false;
		boolean usingCross = c.playerEquipment[c.playerWeapon] == 4734 || c.playerEquipment[c.playerWeapon] == 21902  || c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 11785 || c.playerEquipment[c.playerWeapon] == 33124 || c.playerEquipment[c.playerWeapon] == 33578 || c.playerEquipment[c.playerWeapon] == 33094 || c.playerEquipment[c.playerWeapon] == 21012 || c.playerEquipment[c.playerWeapon] == 33117 || c.playerEquipment[c.playerWeapon] == 33114;
		boolean usingBallista = c.playerEquipment[c.playerWeapon] == 19481 || c.playerEquipment[c.playerWeapon] == 19478;
		
		boolean usingRangeWeapon = false;
		boolean usingMelee = false;
		boolean usingMagic = false;
		
		if (c.autocasting) {
			usingMagic = true;
		}
		if (c.spellId > 0) {
			usingMagic = true;
			usingRangeWeapon = false;
			usingOtherRangeWeapons = false;
			usingCross = false;
			usingBallista = false;
			usingBow = false;
		}
		/**
		 * Cancel out any other style operation
		 */
		if (usingMagic) {
			usingCross = false;
			usingBallista = false;
		}
		switch (c.playerEquipment[c.playerWeapon]) {
			case 11907:
				if (c.autocasting) {
					if (c.getTridentCharge() <= 0) {
						return CombatType.MELEE;
					}
					usingMagic = true;
				}
				break;
	
			case 12899:
				if (c.autocasting) {
					if (c.getToxicTridentCharge() <= 0) {
						return CombatType.MELEE;
					}
					usingMagic = true;
				}
				break;
				
			case 22516:
				usingMagic = true;
				break;
		}
		
		if (!usingMagic) {
			for (int bowId : c.BOWS) {
				if (c.playerEquipment[c.playerWeapon] == bowId && System.currentTimeMillis() - c.switchDelay >= 600) {
					usingBow = true;
					if (bowId == 19481 || bowId == 19478) {
						usingBow = false;
						usingBallista = true;
					}
				}
			}

			for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
				if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
					usingOtherRangeWeapons = true;
				}
			}
		}
		if (c.getItems().isWearingItem(12926)) {
			if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0 || c.getToxicBlowpipeCharge() == 0) {
				return CombatType.MELEE;
			}
			usingBow = true;
		}
		if (!usingMagic && !usingCross && !usingBallista && !usingBow && !usingOtherRangeWeapons) {
			usingMelee = true;
		}
		
		if(usingMagic) {
			return CombatType.MAGE;
		} else if(usingRangeWeapon || usingBow || usingCross || usingBallista) {
			return CombatType.RANGE;
		} else {
			return CombatType.MELEE;
		}
	}

	public static int getPerkOn() {
		return perkOn;
	}

	public static void setPerkOn(int perkOn) {
		AttackNPC.perkOn = perkOn;
	}
}
