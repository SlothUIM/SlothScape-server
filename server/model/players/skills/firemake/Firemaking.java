package server.model.players.skills.firemake;

import server.clip.Region;
//import server.content.achievement.AchievementType;
//import server.content.achievement.Achievements;
//import server.content.achievement_diary.lumbridge_draynor.LumbridgeDraynorDiaryEntry;
//import server.content.dailytasks.DailyTasks;
//import server.content.dailytasks.DailyTasks.PossibleTasks;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
//import server.model.npcs.pets.PetHandler;
//import server.model.npcs.pets.PetHandler.SkillPets;
import server.world.Boundary;
import server.model.items.ItemAssistant;
import server.model.objects.Object;
import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.skills.firemake.LogData;
//import server.model.players.mode.ModeType;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

public class Firemaking {
	
	public static int[] pyromancerOutfit = { 20704, 20706, 20708, 20710 };
	public static void stopFiremaking(Player c) {
		c.startAnimation(65535);
		c.lastSkillingAction = System.currentTimeMillis();
		c.isFiremaking = false;
		//Cooking.setCooking(c, false);
		c.logLit = false;
	}

	/**
	 * Attempts to light a log directly from the ground.
	 * @return true if the item was a log and we have a tinderbox (intercepts pickup), false otherwise.
	 */
	public static boolean lightGroundLog(Player c, int itemId, int itemX, int itemY) {
		boolean isLog = false;
		for (LogData l : LogData.values()) {
			if (l.getLogId() == itemId) {
				isLog = true;
				break;
			}
		}

		// 590 is the standard tinderbox. You can expand this array if you have custom tinderboxes.
		if (!isLog || !c.getItems().playerHasItem(590)) {
			return false;
		}

		CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				// Wait until they arrive exactly on the log
				if (c.getX() == itemX && c.getY() == itemY) {
					lightFire(c, 590, itemId, itemX, itemY, true);
					container.stop();
				}
				// Cancel if they walk away or teleport
				else if (!c.goodDistance(c.getX(), c.getY(), itemX, itemY, 4)) {
					container.stop();
				}
			}
			@Override
			public void stop() {}
		}, 1);

		return true;
	}

		public static void lightFire(final Player c, final int itemUsed, final int usedWith, final int x, final int y, final boolean groundObject) {
			int firemakingItems[] = {590, 7404, 7405, 7406,  10328, 10329};
			for (int i = 0; i < firemakingItems.length; i++) {
			if (c.pickedUpFiremakingLog) {
				c.sendMessage("You can't do that!");
				c.pickedUpFiremakingLog = false;
				return;
			}
			if (c.isFiremaking && c.logLit == false) {
				return;
			}
			if(World.getWorld().getGlobalObjects().exists(26185, x, y) || 
					World.getWorld().getGlobalObjects().exists(26576, x, y) || 
					World.getWorld().getGlobalObjects().exists(26186, x, y)|| 
					World.getWorld().getGlobalObjects().exists(26575, x, y)
			|| World.getWorld().getGlobalObjects().exists(20000, x, y) ||
					World.getWorld().getGlobalObjects().exists(20001, x, y)) {
				c.sendMessage("You cannot light a fire here.");
				return;
			}
			if(Boundary.isIn(c, Boundary.NO_FIREMAKING)) {
				c.sendMessage("You cannot light a fire here.");
				return;
			}
			/*if (!SkillHandler.FIREMAKING) {
				c.getPacketSender().sendMessage("This skill is currently disabled.");
				return;
			}*/
			for (final LogData l : LogData.values()) {
				final int logId = usedWith == firemakingItems[i] ? itemUsed : usedWith;
				if (logId == l.getLogId()) {
					if (c.getSkills().getLevel(Skill.FIREMAKING) < l.getLevelRequirement()) {
						c.sendMessage("You need a firemaking level of " + l.getLevelRequirement() + " to light " + ItemAssistant.getItemName(logId));
						return;
					}
					if (Boundary.isIn(c, Boundary.EDGE_BANK) || Boundary.isIn(c, Boundary.LIGHTHOUSE)) {
						c.sendMessage("You cannot light a fire here.");
						return;
					}
					//if (Server.objectManager.objectExists(c.absX, c.absY, c.heightLevel)) {
						//c.sendMessage("You cannot light a fire here.");
						//return;
					//}
					c.isFiremaking = true;
					c.logLit = false;
					boolean notInstant = System.currentTimeMillis() - c.lastSkillingAction > 2500;
					int cycle = 3;
					if (notInstant) {
							c.getPA().sendSound(Sound.SOUND_LIST.FIRE_LIT.getSound(), 0, 8);
						if (!notInstant) {
							c.getPA().sendSound(Sound.SOUND_LIST.STRIKE_AND_LIGHT.getSound(), 0, 8);
						}
						if (c.tutorialProgress == 4) {
							c.getPA().chatbox(6180);
							c.getDH().chatboxText("", "Your character is now attempting to light the fire.", "This should only take a few seconds.", "", "Please wait");
							c.getPA().chatbox(6179);
						} else 
							c.sendMessage("You attempt to light a fire.");
						if (groundObject == false) {
							c.getItems().deleteItem(logId, c.getItems().getItemSlot(logId), 1);
							World.getWorld().itemHandler.createGroundItem(c, logId, c.getX(), c.getY(), c.getHeight(), 1, c.getIndex());
						}
						cycle = 3 + Misc.random(6);
					} else {
						if (groundObject == false) {
							c.getItems().deleteItem(logId, c.getItems().getItemSlot(logId), 1);
							World.getWorld().itemHandler.createGroundItem(c, logId, c.getX(), c.getY(), c.getHeight(), 1, c.getIndex());
						}
					}
					final boolean walk;
					if (Region.getClipping(x - 1, y, c.getHeight(), -1, 0)) {
						walk = true;
					} else {
						walk = false;
					}
					c.startAnimation(733);
					c.stopFiremaking = false;
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

						@Override
						public void execute(CycleEventContainer container) {
							if (c.stopFiremaking) {
								c.stopFiremaking = false;
								return;
							}
							if (c.isWoodcutting || c.playerIsFletching || c.isFletching || c.isMoving) {
								container.stop();
							}
							if (c.isFiremaking) {
								World.getWorld().itemHandler.removeGroundItem(c, logId, x, y, false);
								c.getPA().sendSound(Sound.SOUND_LIST.FIRE_LIT.getSound(), 0, 8);
								if (itemUsed == 7404 || usedWith == 7404)
									World.getWorld().getGlobalObjects().add(new GlobalObject(26186, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));
								else if (itemUsed == 7405 || usedWith == 7405)
									World.getWorld().getGlobalObjects().add(new GlobalObject(26575, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));
								else if (itemUsed == 7406 || usedWith == 7406)
									World.getWorld().getGlobalObjects().add(new GlobalObject(26576, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));
								else if (itemUsed == 10328 || usedWith == 10328)
									World.getWorld().getGlobalObjects().add(new GlobalObject(20000, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));//white
								else if (itemUsed == 10329 || usedWith == 10329)
									World.getWorld().getGlobalObjects().add(new GlobalObject(20001, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));//purple
								else
									World.getWorld().getGlobalObjects().add(new GlobalObject(26185, x, y, c.getHeight(), 0, 10, 100+Misc.random(100), -1));
								if (c.tutorialProgress == 4) {
									c.getDH().sendDialogues(3016, 9477);
								} else
									c.sendMessage("The fire catches and the log beings to burn.");
								c.getPA().addSkillXP((int) l.getExperience(), 11);
								c.getPA().walkTo(walk ? -1 : 1, 0);
								CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {

											@Override
											public void execute(CycleEventContainer container) {
												c.turnPlayerTo(walk ? x + 1 : x - 1, y);
												c.logLit = true;
												stopFiremaking(c);
												container.stop();
											}

											@Override
											public void stop() {
											}
										}, 1);
								container.stop();
							} else {
								return;
							}
						}

						@Override
						public void stop() {
							stopFiremaking(c);
						}
					}, cycle);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							//if (c.playerIsCooking) {
							//	Cooking.setCooking(c, false);
							//}
							World.getWorld().itemHandler.createGroundItem(c, 592, x, y, c.getHeight(), 1, c.getIndex());
							container.stop();
						}
						@Override
						public void stop() {

						}
						}, 60);
					}
				}
			}
		}
	}

