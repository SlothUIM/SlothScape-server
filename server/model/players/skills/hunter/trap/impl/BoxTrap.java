package server.model.players.skills.hunter.trap.impl;

import java.util.EnumSet;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCDumbPathFinder;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.hunter.trap.Trap;
import server.model.items.Item;
import server.world.World;
import server.world.objects.GlobalObject;

/**
 * The box trap implementation of the {@link Trap} class which represents a single box trap.
 */
public final class BoxTrap extends Trap {

	/**
	 * Constructs a new {@link BoxTrap}.
	 * @param player {@link #getPlayer()}.
	 */
	public BoxTrap(Player player) {
		super(player, TrapType.BOX_TRAP);
	}

	private Optional<NPC> trapped = Optional.empty();
	private Optional<CycleEvent> event = Optional.empty();

	private static final int FAILED_ID = 9385; // Box trap not shaking (unsuccessful)
	private static final int TRIGGERING_ID = 9381; // Box trap going closing (empty)

	private void kill(NPC npc) {
		npc.isDead = true;
		npc.applyDead = true;
		npc.updateRequired = true;
		trapped = Optional.of(npc);
	}

	/**
	 * Helper method to seamlessly transform the trap object for animations
	 */
	private void transform(int newId) {
		World.getWorld().getGlobalObjects().remove(getObject());
		World.getWorld().getGlobalObjects().remove(getObject().getObjectId(), getObject().getX(), getObject().getY(), getObject().getHeight());
		setObject(newId);
		World.getWorld().getGlobalObjects().add(getObject());
	}

	@Override
	public boolean canCatch(NPC npc) {
		Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByNpcId(npc.npcType);

		if(!data.isPresent()) {
			throw new IllegalStateException("Invalid box trap target id.");
		}
		if (System.currentTimeMillis() - player.lastPickup < 2500)
			return false;

		if(player.getSkills().getLevel(Skill.HUNTER) < data.get().requirement) {
			player.lastPickup = System.currentTimeMillis();
			player.sendMessage("You do not have the required level to catch these.");
			setState(TrapState.FALLEN);
			return false;
		}
		return true;
	}

	@Override
	public void onPickUp() {
		player.sendMessage("You pick up your box trap.");
	}

	@Override
	public void onSetup() {
		player.sendMessage("You set-up your box trap.");
	}

	@Override
	public void onCatch(NPC npc) {
		Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByNpcId(npc.npcType);

		if(!data.isPresent() || event.isPresent()) {
			return;
		}

		BoxTrapData target = data.get();

		// IMMEDIATELY lock the trap so the AI doesn't double-trigger it!
		this.setState(TrapState.TRIGGERING);

		event = Optional.of(new CycleEvent() {
			int sequence = 0;

			@Override
			public void execute(CycleEventContainer container) {

				if (sequence == 0) {
					// TICK 0: Step backwards off the trap
					int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
					int[] dir = dirs[random.inclusive(3)];

					npc.moveX = dir[0];
					npc.moveY = dir[1];
					npc.getNextNPCMovement();
					npc.updateRequired = true;
				}
				else if (sequence == 1) {
					// TICK 1: Turn around to face the box trap
					npc.turnNpc(getObject().getX(), getObject().getY());
				}
				else if (sequence == 2) {
					npc.moveX = getObject().getX() - npc.getX();
					npc.moveY = getObject().getY() - npc.getY();
					npc.getNextNPCMovement();
					npc.updateRequired = true;
					// TICK 2: Make the catch decision and play the sniffing animation
					int count = random.inclusive(255);
					int formula = successFormula(npc);

					// --- FAIL SEQUENCE ---
					if(count > formula) {
						if (target.failAnim != -1) npc.startAnimation(target.failAnim);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							int failTicks = 0;
							@Override
							public void execute(CycleEventContainer c) {
								if (failTicks == 2) {
									transform(TRIGGERING_ID); // Trap snaps shut empty
								} else if (failTicks == 3) {
									setState(TrapState.FALLEN);
									c.stop();
								}
								failTicks++;
							}
							@Override
							public void stop() {}
						}, 1);
					}
					// --- SUCCESS SEQUENCE ---
					else {
						if (target.catchAnim != -1) npc.startAnimation(target.catchAnim);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							int catchTicks = 0;
							@Override
							public void execute(CycleEventContainer c) {
								if (catchTicks == 0) {
									transform(target.landingId); // Box snaps shut over the animal
								} else if (catchTicks == 1) {
									kill(npc);
									transform(target.objectId); // Box starts shaking
									setState(TrapState.CAUGHT);
									c.stop();
								}
								catchTicks++;
							}
							@Override
							public void stop() {}
						}, 1);
					}

					// The 3-step movement sequence is finished, stop this container!
					container.stop();
				}
				sequence++;
			}

			@Override
			public void stop() {
				event = Optional.empty();
			}
		});

		CycleEventHandler.getSingleton().addEvent(player, event.get(), 1);
	}


	@Override
	public Item[] reward() {
		if(!trapped.isPresent()) throw new IllegalStateException("No npc is trapped.");
		Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByObjectId(getObject().getObjectId());
		if(!data.isPresent()) throw new IllegalStateException("Invalid object id.");
		return data.get().reward;
	}

	@Override
	public double experience() {
		if(!trapped.isPresent()) throw new IllegalStateException("No npc is trapped.");
		Optional<BoxTrapData> data = BoxTrapData.getBoxTrapDataByObjectId(getObject().getObjectId());
		if(!data.isPresent()) throw new IllegalStateException("Invalid object id.");
		return data.get().experience;
	}

	@Override
	public boolean canClaim(GlobalObject object) {
		if(!trapped.isPresent()) return false;
		BoxTrapData data = BoxTrapData.getBoxTrapDataByObjectId(object.getObjectId()).orElse(null);
		return data != null;
	}

	@Override
	public void setState(TrapState state) {
		if(state.equals(TrapState.PENDING)) throw new IllegalArgumentException("Cannot set trap state back to pending.");

		if(state.equals(TrapState.FALLEN)) {
			transform(FAILED_ID);
		} else if (state.equals(TrapState.CAUGHT)) {
			player.sendMessage("You caught something!");
		} else {
			player.sendMessage("Your trap has been triggered by something...");
		}
		super.setState(state);
	}
	@Override
	public boolean canAccept(NPC npc) {
		return BoxTrapData.getBoxTrapDataByNpcId(npc.npcType).isPresent();
	}
	public enum BoxTrapData {
		// npcId, shakingId, landingId, requirement, experience, catchAnim, failAnim, reward
		FERRET(1505, 9382, 9394, 27, 115, 5191, 5192, new Item(10092)), // Use -1 if you don't have ferret anims yet
		CHINCHOMPA(2910, 9383, 9386, 53, 198.4, 5184, 5185, new Item(10033)),
		RED_CHINCHOMPA(2911, 9384, 9391, 63, 265, 5184, 5185, new Item(10034)),
		BLACK_CHINCHOMPA(2912, 9385, 9393, 73, 315, 5184, 5185, new Item(11959));

		private static final ImmutableSet<BoxTrapData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(BoxTrapData.class));

		private final int npcId;
		private final int objectId;
		private final int landingId;
		private final int requirement;
		private final double experience;
		private final int catchAnim; // <--- NEW
		private final int failAnim;  // <--- NEW
		private final Item[] reward;

		private BoxTrapData(int npcId, int objectId, int landingId, int requirement, double experience, int catchAnim, int failAnim, Item... reward) {
			this.npcId = npcId;
			this.objectId = objectId;
			this.landingId = landingId;
			this.requirement = requirement;
			this.experience = experience;
			this.catchAnim = catchAnim;
			this.failAnim = failAnim;
			this.reward = reward;
		}

		public int getNpcId() { return npcId; }

		public static Optional<BoxTrapData> getBoxTrapDataByNpcId(int id) {
			return VALUES.stream().filter(trap -> trap.npcId == id).findAny();
		}

		public static Optional<BoxTrapData> getBoxTrapDataByObjectId(int id) {
			return VALUES.stream().filter(trap -> trap.objectId == id).findAny();
		}
	}
}