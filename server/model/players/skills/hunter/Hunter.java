package server.model.players.skills.hunter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import server.clip.Region;
//import server.content.dailytasks.DailyTasks;
//import server.content.dailytasks.DailyTasks.PossibleTasks;
import server.event.CycleEventHandler;
import server.event.CycleEventContainer;
import server.event.CycleEvent;
//import server.model.npcs.pets.PetHandler;
//import server.model.npcs.pets.PetHandler.SkillPets;
import server.world.Boundary;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.hunter.trap.Trap;
import server.model.players.skills.hunter.trap.TrapProcessor;
import server.model.players.skills.hunter.trap.TrapTask;
import server.model.players.skills.hunter.trap.Trap.TrapState;
import server.model.players.skills.hunter.trap.Trap.TrapType;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

/**
 * The class which holds static functionality for the hunter skill.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class Hunter {

	/**
	 * The mappings which contain each trap by player on the world.
	 */
	public static final Map<Player, TrapProcessor> GLOBAL_TRAPS = new HashMap<>();

	/**
	 * Retrieves the maximum amount of traps a player can lay.
	 * @param player	the player to lay a trap down for.
	 * @return a numerical value determining the amount a player can lay.
	 */
	private static int getMaximumTraps(Player player) {
		int level = player.getSkills().getLevel(Skill.HUNTER);
		return player.inWild() ? level / 20 + 2 : level / 20 + 1;

	}

	/**
	 * Attempts to abandon the specified {@code trap} for the player.
	 * @param trap		the trap that was abandoned.
	 * @param logout	if the abandon was due to the player logging out.
	 */
	public static void abandon(Player player, Trap trap, boolean logout) {
		if(GLOBAL_TRAPS.get(player) == null) {
			return;
		}
		
		if(logout) {
			GLOBAL_TRAPS.get(player).getTraps().forEach(t -> {
				t.setAbandoned(true);
				World.getWorld().getGlobalObjects().remove(t.getObject());
				World.getWorld().getGlobalObjects().remove(t.getObject().getObjectId(), t.getObject().getX(), t.getObject().getY(), t.getObject().getHeight());
				World.getWorld().getItemHandler().createGroundItem(player, t.getType().getItemId(), t.getObject().getX(), t.getObject().getY(), t.getObject().getHeight(), 1, player.getIndex());
			});
			GLOBAL_TRAPS.get(player).getTraps().clear();
		} else {
			GLOBAL_TRAPS.get(player).getTraps().remove(trap);
			trap.setAbandoned(true);
			World.getWorld().getGlobalObjects().remove(trap.getObject());
			World.getWorld().getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());
			World.getWorld().getItemHandler().createGroundItem(player, trap.getType().getItemId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight(), 1, player.getIndex());
			player.sendMessage("You have abandoned your trap...");
		}

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}
	}

	/**
	 * Attempts to lay down the specified {@code trap} for the specified {@code player}.
	 * @param player	the player to lay the trap for.
	 * @param trap		the trap to lay down for the player.
	 * @return {@code true} if the trap was laid, {@code false} otherwise.
	 */
	public static boolean lay(Player player, Trap trap) {
		if(!player.last_trap_layed.elapsed(1200)) {
			return false;
		}

		// --- 1. Pre-checks ---
		if(GLOBAL_TRAPS.get(player) != null && GLOBAL_TRAPS.get(player).getTraps().size() >= getMaximumTraps(player)) {
			player.sendMessage("You cannot lay more then " + getMaximumTraps(player) + " traps with your hunter level.");
			return false;
		}

		if(World.getWorld().getGlobalObjects().anyExists(player.getX(), player.getY(), player.getHeight())) {
			player.sendMessage("You can't lay down your trap here.");
			return false;
		}

		player.last_trap_layed.reset();

		// --- 2. Drop the item and start animation ---
		int itemId = trap.getType().getItemId();
		player.getItems().deleteItem(itemId, 1);
		World.getWorld().getItemHandler().createGroundItem(player, itemId, player.getX(), player.getY(), player.getHeight(), 1, player.getIndex());

		player.startAnimation(827);

		// Tell the server we are currently setting up a trap
		player.getSkilling().setSkill(Skill.HUNTER);

		// --- 3. The Setup Delay ---
		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int ticks = 0;

			@Override
			public void execute(CycleEventContainer container) {
				// If the player clicked away or got attacked, cancel the setup!
				// The item safely stays on the floor.
				if (player.getSkilling().getSkill() != Skill.HUNTER) {
					container.stop();
					return;
				}

				// It takes about 3 ticks (1.8 seconds) to set up a trap
				if (ticks++ >= 2) {
					// 1. Remove the ground item we dropped earlier
					World.getWorld().getItemHandler().removeGroundItem(player, itemId, player.getX(), player.getY(), false);

					// 2. Register the trap into the active world processor
					GLOBAL_TRAPS.putIfAbsent(player, new TrapProcessor());
					if(!GLOBAL_TRAPS.get(player).getTask().isPresent()) {
						GLOBAL_TRAPS.get(player).setTask(new TrapTask(player));
						CycleEventHandler.getSingleton().addEvent(player, GLOBAL_TRAPS.get(player).getTask().get(), 10);
					}
					GLOBAL_TRAPS.get(player).getTraps().add(trap);
					trap.submit();
					World.getWorld().getGlobalObjects().add(trap.getObject());
					CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
						int aliveTicks = 0;
						@Override
						public void execute(CycleEventContainer expiryContainer) {
							// Stop counting if the trap caught something or was picked up
							if (trap.getState() != TrapState.PENDING || trap.isAbandoned()) {
								expiryContainer.stop();
								return;
							}

							// 100 Ticks = 60 Seconds
							if (aliveTicks++ >= 100) {
								// The abandon method safely removes the object, clears memory, and drops the item!
								Hunter.abandon(player, trap, false);
								player.sendMessage("Your trap has dismantled itself.");
								expiryContainer.stop();
							}
						}
						@Override
						public void stop() {}
					}, 1);
					// 3. Step away from the trap
					if (Region.getClipping(player.getX() - 1, player.getY(), player.getHeight(), -1, 0)) {
						player.getPA().walkTo2(-1, 0);
					} else if (Region.getClipping(player.getX() + 1, player.getY(), player.getHeight(), 1, 0)) {
						player.getPA().walkTo2(1, 0);
					} else if (Region.getClipping(player.getX(), player.getY() - 1, player.getHeight(), 0, -1)) {
						player.getPA().walkTo2(0, -1);
					} else if (Region.getClipping(player.getX(), player.getY() + 1, player.getHeight(), 0, 1)) {
						player.getPA().walkTo2(0, 1);
					}

					// Finish the skilling action
					player.getSkilling().stop();
					container.stop();
				}
			}

			@Override
			public void stop() {}
		}, 1);

		return true;
	}

	/**
	 * Attempts to pick up the trap for the specified {@code player}.
	 * @param player	the player to pick this trap up for.
	 * @param id		the object id that was clicked.
	 * @return {@code true} if the trap was picked up, {@code false} otherwise.
	 */
	/**
	 * Attempts to pick up the trap for the specified {@code player}.
	 */
	public static boolean pickup(Player player, GlobalObject object) {
		Optional<TrapType> type = TrapType.getTrapByObjectId(object.getObjectId());

		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;

		if(!type.isPresent()) return false;

		Trap trap = getTrap(player, object).orElse(null);
		if(trap == null) return false;

		if(trap.getPlayer() == null) {
			player.sendMessage("You can't pickup someone elses trap...");
			return false;
		}

		if(trap.getState().equals(TrapState.CAUGHT)) {
			return false;
		}

		// --- THE FIX: Kill the background expiration timer! ---
		trap.setAbandoned(true);

		GLOBAL_TRAPS.get(player).getTraps().remove(trap);

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}

		trap.onPickUp();
		World.getWorld().getGlobalObjects().remove(trap.getObject());
		World.getWorld().getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());

		// Prevent losing the trap if inventory is full
		if (player.getItems().freeSlots() > 0) {
			player.getItems().addItem(trap.getType().getItemId(), 1);
		} else {
			World.getWorld().getItemHandler().createGroundItem(player, trap.getType().getItemId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight(), 1, player.getIndex());
			player.sendMessage("Your inventory was full, so the trap dropped to the floor.");
		}

		player.startAnimation(827);
		player.lastPickup = System.currentTimeMillis();
		return true;
	}


	/**
	 * Attempts to claim the rewards of this trap.
	 * @param player		the player attempting to claim the items.
	 * @param object		the object being interacted with.
	 * @return {@code true} if the trap was claimed, {@code false} otherwise.
	 */
	/**
	 * Attempts to claim the rewards of this trap.
	 */
	public static boolean claim(Player player, GlobalObject object) {
		Trap trap = getTrap(player, object).orElse(null);

		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;

		if(trap == null) {
			player.sendMessage("You can't pickup someone elses trap...");
			return false;
		}

		if(!trap.canClaim(object)) return false;

		if(trap.getPlayer() == null) {
			player.sendMessage("You can't claim the rewards of someone elses trap...");
			return false;
		}

		if(!trap.getState().equals(TrapState.CAUGHT)) return false;

		// --- THE FIX: Ensure all background tasks are dead ---
		trap.setAbandoned(true);

		double percentOfXp = (trap.experience() / 100) * 2.5;

		// Check if they have space for rewards + the trap itself
		if (player.getItems().freeSlots() >= (trap.reward().length + 1)) {
			Arrays.stream(trap.reward()).forEach(reward -> player.getItems().addItem(reward.getId(), reward.getAmount()));
			player.getItems().addItem(trap.getType().getItemId(), 1);
		} else {
			// Drop everything to the floor if full
			Arrays.stream(trap.reward()).forEach(reward -> World.getWorld().getItemHandler().createGroundItem(player, reward.getId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight(), reward.getAmount(), player.getIndex()));
			World.getWorld().getItemHandler().createGroundItem(player, trap.getType().getItemId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight(), 1, player.getIndex());
			player.sendMessage("Your inventory was full, so your loot dropped to the floor.");
		}

		player.getPA().addSkillXP((int) ((int) trap.experience() + (player.getItems().isWearingItem(10071) ? percentOfXp : 0)), 21);

		GLOBAL_TRAPS.get(player).getTraps().remove(trap);

		if(GLOBAL_TRAPS.get(player).getTraps().isEmpty()) {
			GLOBAL_TRAPS.get(player).setTask(Optional.empty());
			GLOBAL_TRAPS.remove(player);
		}

		World.getWorld().getGlobalObjects().remove(trap.getObject());
		World.getWorld().getGlobalObjects().remove(trap.getObject().getObjectId(), trap.getObject().getX(), trap.getObject().getY(), trap.getObject().getHeight());

		player.startAnimation(827);
		player.lastPickup = System.currentTimeMillis();

		// Put your pet logic / daily tasks back here if needed!

		return true;
	}


	/**
	 * Gets a trap for the specified global object given.
	 * @param player	the player to return a trap for.
	 * @param object	the object to compare.
	 * @return a trap wrapped in an optional, {@link Optional#empty()} otherwise.
	 */
	public static Optional<Trap> getTrap(Player player, GlobalObject object) {
		return !GLOBAL_TRAPS.containsKey(player) ? Optional.empty() : GLOBAL_TRAPS.get(player).getTraps().stream().filter(trap -> trap.getObject().getObjectId() == object.getObjectId() && trap.getObject().getX() == object.getX() && trap.getObject().getY() == object.getY() && trap.getObject().getHeight() == object.getHeight()).findAny();
	}
}
