package server.model.players;

import java.util.concurrent.TimeUnit;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.HealthStatus;
import server.model.players.Sound;
import server.model.players.combat.Hitmark;
import server.model.players.content.Skillcapes.SkillcapePerks;
import server.model.players.skills.Skill;
import server.world.Boundary;
/**
 * @author Sanity
 */

public class Potions {

	private Player c;

	public Potions(Player c) {
		this.c = c;
	}

	public void handlePotion(int itemId, int slot) {
		if (c.duelRule[5]) {
			c.sendMessage("You may not drink potions in this duel.");
			return;
		}
		if (System.currentTimeMillis() - c.potDelay >= 1500) {
			c.potDelay = System.currentTimeMillis();
			c.foodDelay = c.potDelay;
			c.getCombat().resetPlayerAttack();
			c.attackTimer++;
			// Near the top of handlePotion(int itemId, int slot) inside Potions.java:
			StandardPotionData standardPot = StandardPotionData.forId(itemId);

			if (standardPot != null) {
				c.startAnimation(829);
				int itemSlot = slot;

				// 1. Calculate and update container downgrade levels
				int nextDose = (itemId == standardPot.dose4) ? standardPot.dose3
						: (itemId == standardPot.dose3) ? standardPot.dose2
						  : (itemId == standardPot.dose2) ? standardPot.dose1
							: standardPot.empty;

				if (nextDose == standardPot.empty) {
					c.getItems().deleteItem(itemId, itemSlot, 1);

					// --- THE BARBARIAN VIAL SMASH CHECK ---
					if (c.smashVials) {
						c.sendMessage("You drink the final dose of your potion and smash the empty vial.");
						// Optional: Play a glassy crunch sound if your client has it cached
						// c.getPA().sendSound(SOUND_ID, 0, 0);
					} else {
						c.getItems().addItem(standardPot.empty, 1);
						c.sendMessage("You drink the final dose of your potion.");
					}
				} else {
					c.playerItems[itemSlot] = nextDose + 1; // Update item layer inside client cache
				}
				c.getItems().resetItems(3214);

				// 2. Route engine calculations directly by type category
				switch (standardPot.type) {
					case BOOST:
						for (Skill skill : standardPot.affectedSkills) {
							int boost = standardPot.flat + (int)(c.getSkills().getActualLevel(skill) * standardPot.percent);
							c.getSkills().increasLevelOrMax(boost, skill);
						}
						c.getSkills().sendRefresh();
						break;

					case DIVINE:
						int health = c.getHealth().getAmount();
						if (health <= 10) {
							c.sendMessage("You are too weak to survive the dark strain of a divine potion.");
							return;
						}
						c.appendDamage(10, Hitmark.HIT); // Inflict 10 damage natively
						for (Skill skill : standardPot.affectedSkills) {
							int boost = standardPot.flat + (int)(c.getSkills().getActualLevel(skill) * standardPot.percent);
							c.getSkills().setLevel(c.getSkills().getActualLevel(skill) + boost, skill);
						}
						c.getSkills().sendRefresh();
						c.divineBoostEndTime = System.currentTimeMillis() + (5 * 60 * 1000); // Lock stats for 5 mins
						break;

					case PRAYER:
						int currentPray = c.getSkills().getLevel(Skill.PRAYER);
						int actualPray = c.getSkills().getActualLevel(Skill.PRAYER);
						int restoredPray = currentPray + standardPot.flat + (int)(actualPray * standardPot.percent);
						c.getSkills().setLevelOrActual(restoredPray, Skill.PRAYER);
						c.getSkills().sendRefresh(Skill.PRAYER);
						break;

					case SUPER_RESTORE:
						Skill.stream().filter(s -> s != Skill.HITPOINTS).forEach(skill -> {
							int actual = c.getSkills().getActualLevel(skill);
							if (c.getSkills().getLevel(skill) < actual) {
								int restored = c.getSkills().getLevel(skill) + standardPot.flat + (int)(actual * standardPot.percent);
								c.getSkills().setLevelOrActual(restored, skill);
							}
						});
						c.getSkills().sendRefresh();
						break;

					case SARA_BREW:
						// Brew updates: Def boosted by 20%+2, HP boosted by 15%+2, other raw combat styles down by 10%
						int currentHp = c.getSkills().getLevel(Skill.HITPOINTS);
						int actualHp = c.getSkills().getActualLevel(Skill.HITPOINTS);
						int boostedHp = currentHp + standardPot.flat + (int)(actualHp * 0.15);
						c.getSkills().setLevelOrActual(boostedHp, Skill.HITPOINTS);

						int boostedDef = c.getSkills().getLevel(Skill.DEFENCE) + standardPot.flat + (int)(c.getSkills().getActualLevel(Skill.DEFENCE) * 0.20);
						c.getSkills().setLevelOrActual(boostedDef, Skill.DEFENCE);

						Skill[] toDrain = { Skill.ATTACK, Skill.STRENGTH, Skill.RANGED, Skill.MAGIC };
						for (Skill s : toDrain) {
							int drained = c.getSkills().getLevel(s) - (int)(c.getSkills().getActualLevel(s) * 0.10);
							c.getSkills().setLevel(Math.max(1, drained), s);
						}
						c.getSkills().sendRefresh();
						break;

					case STAMINA:
						c.staminaDelay = java.util.concurrent.TimeUnit.MINUTES.toMillis(2);
						c.setStaminaEffect(true);
						c.setRunEnergy(Math.min(100, c.getRunEnergy() + 20));
						c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
						break;

					case ENERGY:
						c.setRunEnergy(Math.min(100, c.getRunEnergy() + 20));
						c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
						break;

					case SUPER_ENERGY:
						c.setRunEnergy(Math.min(100, c.getRunEnergy() + 40));
						c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
						break;

					case ANTIPOISON:
						// Value stores base duration immunity ticks directly
						c.getHealth().resolveStatus(HealthStatus.POISON, standardPot.flat);
						break;

					case ANTIVENOM:
						c.getHealth().resolveStatus(HealthStatus.VENOM, standardPot.flat);
						break;

					case ANTIFIRE:
						c.antiFirePot = true;
						c.antifireDelay = standardPot.flat; // Sets 360 seconds (6 mins) or 720 seconds (12 mins)
						c.lastAntifirePotion = (int) (System.currentTimeMillis() / 1000); // Standard epoch anchor

						c.sendMessage("You drink some of your antifire potion.");
						c.sendMessage("You are now protected from dragonfire.");

						// Optional: If you use the gameplay timers engine we called earlier:
						// c.getPA().sendGameTimer(ClientGameTimer.ANTIFIRE, TimeUnit.SECONDS, standardPot.flat);
						break;

					case SUPER_ANTIFIRE:
						c.lastSuperAntifirePotion = System.currentTimeMillis();
						// Converts the seconds flat-value (180 or 360) directly into a millisecond timestamp block
						c.SuperantifireDelay = System.currentTimeMillis() + (standardPot.flat * 1000L);

						c.sendMessage("You drink some of your super antifire potion.");
						c.sendMessage("<col=7f007f>You are now completely immune to dragonfire!</col>");

						// Optional: If you want to render an active client timer layer:
						// c.getPA().sendGameTimer(ClientGameTimer.SUPER_ANTIFIRE, TimeUnit.SECONDS, standardPot.flat);
						break;
				}

				c.getPA().sendSound(server.model.players.Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
				return; // Complete execution block bypasses legacy legacy switches
			}
		}
	}
	public void handleDivinePotion(int itemId, int replaceItem, int slot, Skill[] skills, double percentage, int flat) {
		int currentHealth = c.getHealth().getAmount();
		if (currentHealth <= 10) {
			c.sendMessage("You are too weak to survive the dark power of a divine potion.");
			return;
		}

		c.startAnimation(829);
		c.appendDamage(10, Hitmark.HIT); // Inflicts 10 true damage immediately

		if (replaceItem == 229) {
			c.getItems().deleteItem(itemId, slot, 1);
			c.getItems().addItem(229, 1);
		} else {
			c.playerItems[slot] = replaceItem + 1;
		}
		c.getItems().resetItems(3214);

		for (Skill skill : skills) {
			int boost = flat + (int)(c.getSkills().getActualLevel(skill) * percentage);
			c.getSkills().setLevel(c.getSkills().getActualLevel(skill) + boost, skill);
		}
		c.getSkills().sendRefresh();

		// Lock stats for 5 minutes (500 Server Ticks)
		c.divineBoostEndTime = System.currentTimeMillis() + (5 * 60 * 1000);
		c.sendMessage("You drink the divine potion. Your combat stats are locked at maximum for 5 minutes.");
	}
	/**
	 * Custom drinking execution layout logic tailored explicitly for CoX combat vials.
	 * Caches dynamic percentages and shatters hollow containers on index -1.
	 */
	public void drinkCoxStatPot(int itemId, int replaceItem, int slot, int s1, int s2, int s3, double percentage, int flat) {
		c.startAnimation(829);
		if (replaceItem == -1) {
			c.getItems().deleteItem(itemId, slot, 1);
			c.sendMessage("Your gourd vial breaks as you drink the final dose.");
		} else {
			c.playerItems[slot] = replaceItem + 1;
		}
		c.getItems().resetItems(3214);

		int[] affectedSkills = { s1, s2, s3 };
		for (int skillId : affectedSkills) {
			Skill skill = Skill.forId(skillId);
			int bonus = flat + (int)(c.getSkills().getActualLevel(skill) * percentage);
			c.getSkills().increasLevelOrMax(bonus, skill);
		}
		c.getSkills().sendRefresh();
	}

	/**
	 * Triggers the continuous 5-minute cycle matrix.
	 */
	public void executeCoxOverload(int itemId, int replaceItem, int slot, double percent, int flat) {
		int currentHealth = c.getHealth().getAmount();
		if (currentHealth <= 50) {
			c.sendMessage("You need more than 50 Hitpoints to handle the strain of an Overload potion.");
			return;
		}

		c.startAnimation(829);
		if (replaceItem == -1) {
			c.getItems().deleteItem(itemId, slot, 1);
			c.sendMessage("Your gourd vial breaks as you drink the final dose.");
		} else {
			c.playerItems[slot] = replaceItem + 1;
		}
		c.getItems().resetItems(3214);

		// Run damage loop (10 dmg per tick across 5 ticks = 50 total damage)
		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			int ticks = 0;
			@Override
			public void execute(CycleEventContainer container) {
				if (c == null || ticks >= 5) {
					container.stop();
					return;
				}
				c.startAnimation(3170);
				c.appendDamage(10, Hitmark.HIT);
				ticks++;
			}
			@Override
			public void stop() {}
		}, 1);

		// Run the 15-second re-application processor loop for 5 minutes total (500 Ticks)
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_BOOST_ID, c, new CycleEvent() {
			int executionCyclesRemaining = 20; // 20 iterations * 15 seconds = 5 minutes

			@Override
			public void execute(CycleEventContainer container) {
				if (c == null || c.isDead || executionCyclesRemaining <= 0 || c.getRaidSession() == null) {
					container.stop();
					return;
				}

				// Re-apply stat matrices calculation formulas
				int[] combatSkills = { 0, 1, 2, 4, 6 }; // Att, Def, Str, Range, Mage
				for (int skillId : combatSkills) {
					Skill skill = Skill.forId(skillId);
					int calculatedBoost = flat + (int)(c.getSkills().getActualLevel(skill) * percent);
					c.getSkills().setLevel(c.getSkills().getActualLevel(skill) + calculatedBoost, skill);
				}
				c.getSkills().sendRefresh();

				executionCyclesRemaining--;
				if (executionCyclesRemaining == 0) {
					container.stop();
				}
			}

			@Override
			public void stop() {
				if (c != null) {
					// Normalize back to base levels
					int[] combatSkills = { 0, 1, 2, 4, 6 };
					for (int s : combatSkills) {
						c.getSkills().resetToActualLevel(Skill.forId(s));
					}
					c.getSkills().sendRefresh();

					// Restore the 50 HP taken at the beginning if they survived
					if (!c.isDead) {
						c.getHealth().increase(50);
					}
					c.sendMessage("The extreme effects of the raid overload potion fade away.");
				}
			}
		}, 25); // 25 server ticks = exactly 15 seconds
	}
	public void drinkAntiPoison(int itemId, int replaceItem, int slot, int duration) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.getPA().sendSound(Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
		c.getHealth().resolveStatus(HealthStatus.POISON, duration);
		c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.SECONDS, (int) (duration * .6));
		c.getPA().requestUpdates();
	}
	public void drinkAntidote(int itemId, int replaceItem, int slot, int duration) {
		boolean venom = c.getHealth().getStatus().isVenomed();
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.getPA().sendSound(Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
		c.getPA().requestUpdates();

		if (venom) {
			c.getHealth().resolveStatus(HealthStatus.VENOM, duration);
			c.getPA().sendGameTimer(ClientGameTimer.ANTIVENOM, TimeUnit.SECONDS, (int) (duration * .6));
			c.sendMessage("Venom");
		} else {
			c.getHealth().resolveStatus(HealthStatus.POISON, duration);
			c.getPA().sendGameTimer(ClientGameTimer.ANTIPOISON, TimeUnit.SECONDS, (int) (duration * .6));
		}
	}
	public void curePoison(long duration) {
		c.sendMessage("You have cured yourself of the poison.");
		c.getPA().requestUpdates();
	}
	public static void drinkAbsorption(Player c, int itemId, int replaceItem, int slot) {
		if(!c.inNMZ) {
			c.sendMessage("You can only drink this in Nightmare Zone.");
			return;
		}
        c.getItems().deleteItem(itemId, slot, 1);
        c.getItems().addItem(replaceItem > 0 ? replaceItem : 229, 1); // 229 is Empty Vial
        
        c.nmzAbsorption += 50;
        if (c.nmzAbsorption > 1000) {
            c.nmzAbsorption = 1000;
        }

        c.getPA().sendFrame126(""+c.nmzAbsorption, 48681);
        c.startAnimation(829);
        c.sendMessage("You drink some of the absorption potion. You can now absorb " + c.nmzAbsorption + " damage.");
    }

    // Inside your switch statement for drinking potions:
    /*
        case 11734: drinkAbsorption(c, itemId, 11735, slot); break; // 4 dose
        case 11735: drinkAbsorption(c, itemId, 11736, slot); break; // 3 dose
        case 11736: drinkAbsorption(c, itemId, 11737, slot); break; // 2 dose
        case 11737: drinkAbsorption(c, itemId, 229, slot); break;   // 1 dose
    */
	public void drinkStatPotion(int itemId, int replaceItem, int slot,
			int stat, boolean sup) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.getPA().sendSound(Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
		enchanceStat(stat, sup);
	}
	public void drinkStatPotion(int itemId, int replaceItem, int slot, int stat, int amount) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		Skill skill = Skill.forId(stat);
		c.getSkills().increasLevelOrMax(amount, skill);
		c.getSkills().sendRefresh();
	}
	public void drinkPrayerPot(int itemId, int replaceItem, int slot) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		int newLevel = (int) (c.getSkills().getLevel(Skill.PRAYER) + (c.getSkills().getActualLevel(Skill.PRAYER) * .33));
		if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
			newLevel += 5;
		if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
			//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
		}
		c.getSkills().setLevelOrActual(newLevel, Skill.PRAYER);
		c.getSkills().sendRefresh(Skill.PRAYER);
	}
	public void drinkStamina(int replaceItem, int slot, long duration) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.staminaDelay = duration;
		c.setStaminaEffect(true);
		int total = c.getRunEnergy() + 20;
		if (total > 100) {
			c.setRunEnergy(100);
			c.getPA().sendFrame126(Integer.toString(100), 149);
		} else {
			c.getPA().sendFrame126(Integer.toString(c.getRunEnergy()), 149);
			c.setRunEnergy(c.getRunEnergy() + 20);
		}
		c.getPA().sendGameTimer(ClientGameTimer.STAMINA, TimeUnit.MILLISECONDS, (int) (duration));
	}
	public void drinkSuperRestorePot(int itemId, int replaceItem, int slot) {
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.getPA().sendSound(Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
		Skill.stream().filter(skill -> skill != Skill.HITPOINTS).forEach(skill -> {
			if(c.getSkills().getLevel(skill) > c.getSkills().getActualLevel(skill))
				return;
			int actualLevel = c.getSkills().getActualLevel(skill);
			int newLevel = (int) Math.ceil(c.getSkills().getLevel(skill) + 8 + (0.25D * actualLevel));

			if (SkillcapePerks.PRAYER.isWearing(c) || SkillcapePerks.isWearingMaxCape(c))
				newLevel += 5;

			c.getSkills().setLevelOrActual(newLevel, skill);
		});

		c.getSkills().sendRefresh();
		if (Boundary.isIn(c, Boundary.DEMONIC_RUINS_BOUNDARY)) {
			//c.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.DEMONIC_RUINS);
		}
	}
	public void restoreStats() {
		for (int j = 0; j <= 6; j++) {
			if (j == 5 || j == 3)
				continue;
			if (c.playerLevel[j] < c.getLevelForXP(c.playerXP[j])) {
				c.playerLevel[j] += (c.getLevelForXP(c.playerXP[j]) * .33);
				if (c.playerLevel[j] > c.getLevelForXP(c.playerXP[j])) {
					c.playerLevel[j] = c.getLevelForXP(c.playerXP[j]);
				}
				c.getPA().refreshSkill(j);
				c.getPA().setSkillLevel(j, c.playerLevel[j], c.playerXP[j]);
			}
		}
	}

	public void doTheBrew(int itemId, int replaceItem, int slot) {
		if (c.duelRule[6]) {
			c.sendMessage("You may not eat in this duel.");
			return;
		}
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.getPA().sendSound(Sound.SOUND_LIST.LIQUID.getSound(), 0, 8);
		int[] toDecrease = { 0, 2, 4, 6 };

		for (int tD : toDecrease) {
			c.playerLevel[tD] -= getBrewStat(tD, .10);
			if (c.playerLevel[tD] < 0)
				c.playerLevel[tD] = 1;
			c.getPA().refreshSkill(tD);
			c.getPA().setSkillLevel(tD, c.playerLevel[tD], c.playerXP[tD]);
		}
		c.playerLevel[1] += getBrewStat(1, .20);
		if (c.playerLevel[1] > (c.getLevelForXP(c.playerXP[1]) * 1.2 + 1)) {
			c.playerLevel[1] = (int) (c.getLevelForXP(c.playerXP[1]) * 1.2);
		}
		c.getPA().refreshSkill(1);

		c.playerLevel[3] += getBrewStat(3, .15);
		if (c.playerLevel[3] > (c.getLevelForXP(c.playerXP[3]) * 1.17 + 1)) {
			c.playerLevel[3] = (int) (c.getLevelForXP(c.playerXP[3]) * 1.17);
		}
		c.getPA().refreshSkill(3);
	}

	public void enchanceStat(int skillID, boolean sup) {
		c.getSkills().increaseLevel(getBoostedStat(skillID, sup), Skill.forId(skillID));
		c.getPA().refreshSkill(skillID);
	}

	public int getBrewStat(int skill, double amount) {
		return (int) (c.getSkills().getActualLevel(Skill.forId(skill)) * amount);
	}

	public int getBoostedStat(int skill, boolean sup) {
		int increaseBy = 0;
		if (sup)
			increaseBy = (int) (c.getSkills().getActualLevel(Skill.forId(skill)) * .20);
		else
			increaseBy = (int) (c.getSkills().getActualLevel(Skill.forId(skill)) * .13) + 1;
		if (c.getSkills().getLevel(Skill.forId(skill)) + increaseBy > c.getSkills().getActualLevel(Skill.forId(skill)) + increaseBy + 1) {
			return c.getSkills().getActualLevel(Skill.forId(skill)) + increaseBy - c.getSkills().getLevel(Skill.forId(skill));
		}
		return increaseBy;
	}
	public void doOverloadBoost() {
		if(!c.inNMZ) {
			return;
		}
		int[] toIncrease = { 0, 1, 2, 4, 6 };
		int boost;
		for (int i = 0; i < toIncrease.length; i++) {
			Skill skill = Skill.forId(i);
			boost = (int) (getOverloadBoost(toIncrease[i]));
			c.getSkills().increaseLevel(boost, skill);
			if (c.getSkills().getLevel(skill) > c.getSkills().getActualLevel(skill) + boost)
				c.getSkills().setLevel(c.getSkills().getActualLevel(skill) + boost, skill);
			c.getPA().refreshSkill(toIncrease[i]);
		}
	}
	public void doOverload(int itemId, int replaceItem, int slot) {
		if(!c.inNMZ) {
			c.sendMessage("You can only drink this in Nightmare Zone.");
			return;
		}
		int health = c.getHealth().getAmount();
		if (health <= 50) {
			c.sendMessage("I should get some more lifepoints before using this!");
			return;
		}
		c.getPA().sendGameTimer(ClientGameTimer.OVERLOAD, TimeUnit.MINUTES, 5);
		//String timerString = String.format("%02d:%02d", 5, 0);
		//c.getPA().sendFrame126(timerString, 48670);
		c.hasOverloadBoost = false;
		c.startAnimation(829);
		c.playerItems[slot] = replaceItem + 1;
		c.getItems().resetItems(3214);
		c.hasOverloadBoost = true;
		createOverloadDamageEvent();
		doOverloadBoost();
		handleOverloadTimers();
		c.getPA().refreshSkill(0);
		c.getPA().refreshSkill(1);
		c.getPA().refreshSkill(2);
		c.getPA().refreshSkill(3);
		c.getPA().refreshSkill(4);
		c.getPA().refreshSkill(6);
	}
	public void handleOverloadTimers() {
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_BOOST_ID);
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_BOOST_ID, c, new CycleEvent() {

			@Override
			public void execute(CycleEventContainer b) {
				if (c == null) {
					b.stop();
					return;
				}
				resetOverload();
			}

			@Override
			public void stop() {

			}
		}, 500); // 5 minutes
	}
	private void createOverloadDamageEvent() {
		CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.OVERLOAD_HITMARK_ID);
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.OVERLOAD_HITMARK_ID, c, new CycleEvent() {
			int time = 5;

			@Override
			public void execute(CycleEventContainer b) {
				if (c == null) {
					b.stop();
					return;
				}
				if (time <= 0) {
					b.stop();
					return;
				}
				if (time > 0) {
					if (c.getHealth().getAmount() <= 10) {
						b.stop();
						return;
					}
					time--;
					c.startAnimation(3170);
					c.appendDamage(10, Hitmark.HIT);
				}
			}

			@Override
			public void stop() {

			}
		}, 1);
	}
	public void resetOverload() {
		if (!c.hasOverloadBoost)
			return;
		c.hasOverloadBoost = false;
		int[] toNormalise = { 0, 1, 2, 4, 6 };
		for (int i = 0; i < toNormalise.length; i++) {
			c.getSkills().resetToActualLevel(Skill.forId(i));
		}
		c.getSkills().sendRefresh();
		c.sendMessage("The effects of the potion have worn off...");
	}
	public double getOverloadBoost(int skill) {
		double boost = 1;
		switch (skill) {
		case 0:
		case 1:
		case 2:
			boost = 5 + (c.getSkills().getActualLevel(Skill.forId(skill)) * .22);
			break;
		case 4:
			boost = 3 + (c.getSkills().getActualLevel(Skill.forId(skill)) * .22);
			break;
		case 6:
			boost = 7;
			break;
		}
		return boost;
	}
	public boolean isPotion(int itemId) {
		String name = c.getItems().getItemName(itemId);
		if(name != null)
		return name.contains("(4)") || name.contains("(3)")
				|| name.contains("(2)") || name.contains("(1)");
		else
			return false;
	}

	public enum StandardPotionData {
		// Melee Boosts
		ATTACK(PotionType.BOOST, 2428, 121, 123, 125, 229, 0.10, 3, Skill.ATTACK),
		SUPER_ATTACK(PotionType.BOOST, 2436, 145, 147, 149, 229, 0.15, 5, Skill.ATTACK),
		STRENGTH(PotionType.BOOST, 113, 115, 117, 119, 229, 0.10, 3, Skill.STRENGTH),
		SUPER_STRENGTH(PotionType.BOOST, 2440, 157, 159, 161, 229, 0.15, 5, Skill.STRENGTH),
		DEFENCE(PotionType.BOOST, 2432, 133, 135, 137, 229, 0.10, 3, Skill.DEFENCE),
		SUPER_DEFENCE(PotionType.BOOST, 2442, 163, 165, 167, 229, 0.15, 5, Skill.DEFENCE),
		COMBAT(PotionType.BOOST, 9739, 9741, 9743, 9745, 229, 0.10, 3, Skill.ATTACK, Skill.STRENGTH),
		SUPER_COMBAT(PotionType.BOOST, 12695, 12697, 12699, 12701, 229, 0.15, 5, Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),

		// Ranged & Magic
		RANGING(PotionType.BOOST, 2444, 169, 171, 173, 229, 0.13, 4, Skill.RANGED),
		MAGIC(PotionType.BOOST, 3040, 3042, 3044, 3046, 229, 0.00, 4, Skill.MAGIC),
		BASTION(PotionType.BOOST, 20984, 20987, 20990, 20993, 229, 0.15, 5, Skill.RANGED, Skill.DEFENCE), // Note: Range 10%+4, Def 15%+5 natively
		BATTLEMAGE(PotionType.BOOST, 20996, 20999, 21002, 21005, 229, 0.15, 5, Skill.MAGIC, Skill.DEFENCE),

		// Divine Variants (Combat Stat Lockers)
		DIVINE_SUPER_ATTACK(PotionType.DIVINE, 23697, 23699, 23702, 23705, 229, 0.15, 5, Skill.ATTACK),
		DIVINE_SUPER_STRENGTH(PotionType.DIVINE, 23707, 23710, 23713, 23716, 229, 0.15, 5, Skill.STRENGTH),
		DIVINE_SUPER_DEFENCE(PotionType.DIVINE, 23718, 23721, 23724, 23727, 229, 0.15, 5, Skill.DEFENCE),
		DIVINE_RANGING(PotionType.DIVINE, 23738, 23741, 23744, 23747, 229, 0.13, 4, Skill.RANGED),
		DIVINE_MAGIC(PotionType.DIVINE, 23749, 23752, 23755, 23758, 229, 0.00, 4, Skill.MAGIC),
		DIVINE_SUPER_COMBAT(PotionType.DIVINE, 23685, 23688, 23691, 23694, 229, 0.15, 5, Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),

		// Restoration & Energy
		PRAYER(PotionType.PRAYER, 2434, 139, 141, 143, 229, 0.25, 7, Skill.PRAYER),
		RESTORE(PotionType.RESTORE, 2430, 127, 129, 131, 229, 0.30, 10), // Restores combat stats
		SUPER_RESTORE(PotionType.SUPER_RESTORE, 3024, 3026, 3028, 3030, 229, 0.25, 8), // Restores all stats except HP
		STAMINA(PotionType.STAMINA, 12625, 12627, 12629, 12631, 229, 0.00, 0, Skill.AGILITY),
		ENERGY(PotionType.ENERGY, 3008, 3010, 3012, 3014, 229, 0.00, 0),
		SUPER_ENERGY(PotionType.SUPER_ENERGY, 3016, 3018, 3020, 3022, 229, 0.00, 0),

		// Complex / Hybrid Brews
		SARADOMIN_BREW(PotionType.SARA_BREW, 6685, 6687, 6689, 6691, 229, 0.15, 2, Skill.HITPOINTS, Skill.DEFENCE),
		ZAMORAK_BREW(PotionType.ZAM_BREW, 2450, 189, 191, 193, 229, 0.12, 2, Skill.ATTACK, Skill.STRENGTH),

		// Antidotes & Cleansers
		ANTIPOISON(PotionType.ANTIPOISON, 2446, 175, 177, 179, 229, 0.00, 90), // Value holds base immunity duration seconds
		SUPERANTIPOISON(PotionType.ANTIPOISON, 2448, 181, 183, 185, 229, 0.00, 360),
		ANTIDOTE_P(PotionType.ANTIPOISON, 5943, 5945, 5947, 5949, 229, 0.00, 540), // Antidote+
		ANTIDOTE_PP(PotionType.ANTIPOISON, 5952, 5954, 5956, 5958, 229, 0.00, 720), // Antidote++
		ANTI_VENOM(PotionType.ANTIVENOM, 12905, 12907, 12909, 12911, 229, 0.00, 60),
		ANTI_VENOM_P(PotionType.ANTIVENOM, 12913, 12915, 12917, 12919, 229, 0.00, 180), // Anti-venom+

		// Fire Protection
		ANTIFIRE(PotionType.ANTIFIRE, 2452, 2454, 2456, 2458, 229, 0.00, 360),
		EXTENDED_ANTIFIRE(PotionType.ANTIFIRE, 11951, 11953, 11955, 11957, 229, 0.00, 720),
		SUPER_ANTIFIRE(PotionType.SUPER_ANTIFIRE, 21978, 21981, 21984, 21987, 229, 0.00, 180),
		EXT_SUPER_ANTIFIRE(PotionType.SUPER_ANTIFIRE, 22209, 22212, 22215, 22218, 22219, 0.00, 360); // Empty: 22219

		public enum PotionType {
			BOOST, DIVINE, PRAYER, RESTORE, SUPER_RESTORE, SARA_BREW, ZAM_BREW, STAMINA, ENERGY, SUPER_ENERGY, ANTIPOISON, ANTIVENOM, ANTIFIRE, SUPER_ANTIFIRE
		}

		public final PotionType type;
		public final int dose4, dose3, dose2, dose1, empty;
		public final double percent;
		public final int flat;
		public final Skill[] affectedSkills;

		StandardPotionData(PotionType type, int d4, int d3, int d2, int d1, int empty, double percent, int flat, Skill... skills) {
			this.type = type;
			this.dose4 = d4;
			this.dose3 = d3;
			this.dose2 = d2;
			this.dose1 = d1;
			this.empty = empty;
			this.percent = percent;
			this.flat = flat;
			this.affectedSkills = skills;
		}

		public static StandardPotionData forId(int itemId) {
			for (StandardPotionData pot : values()) {
				if (itemId == pot.dose4 || itemId == pot.dose3 || itemId == pot.dose2 || itemId == pot.dose1) {
					return pot;
				}
			}
			return null;
		}
	}
}