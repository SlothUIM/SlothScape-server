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

public final class BirdSnare extends Trap {

	public BirdSnare(Player player) {
		super(player, TrapType.BIRD_SNARE);
	}

	private Optional<NPC> trapped = Optional.empty();
	private Optional<CycleEvent> event = Optional.empty();

	// Trap Object IDs
	private static final int FAILED_ID = 9344;
	private static final int TRIGGERING_ID = 9346; // The animation of the trap snapping empty

	// Universal Bird Animations
	private static final int ANIM_START_LAND = 6780;
	private static final int ANIM_FALL_OVER = 6781;
	private static final int ANIM_FAIL = 6783;

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
	public boolean canAccept(NPC npc) {
		return BirdData.getBirdDataByNpcId(npc.npcType).isPresent();
	}

	@Override
	public boolean canCatch(NPC npc) {
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.npcType);

		if(!data.isPresent()) {
			return false;
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
		player.sendMessage("You pick up your bird snare.");
	}

	@Override
	public void onSetup() {
		player.sendMessage("You set-up your bird snare.");
	}

	@Override
	public void onCatch(NPC npc) {
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(npc.npcType);

		if(!data.isPresent() || event.isPresent()) {
			return;
		}

		BirdData bird = data.get();

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
					// TICK 1: Turn around to face the bird snare
					npc.turnNpc(getObject().getX(), getObject().getY());
				}
				else if (sequence == 2) {
					npc.moveX = getObject().getX() - npc.getX();
					npc.moveY = getObject().getY() - npc.getY();
					npc.getNextNPCMovement();
					npc.updateRequired = true;
					// TICK 2: Make the catch decision and play animations
					int count = random.inclusive(255); // Mod Ash's Official Catch Roll
					int formula = successFormula(npc);

					// --- FAIL SEQUENCE ---
					if(count > formula) {
						// Bird lands and gets ready to fly away
						npc.startAnimation(ANIM_FAIL);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							int failTicks = 0;
							@Override
							public void execute(CycleEventContainer c) {
								if (failTicks == 1) {
									transform(TRIGGERING_ID); // Trap snaps shut empty
								} else if (failTicks == 3) {
									setState(TrapState.FALLEN); // Sets to FAILED_ID
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
						// Bird starts landing on the trap
						npc.startAnimation(ANIM_START_LAND);

						CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
							int catchTicks = 0;
							@Override
							public void execute(CycleEventContainer c) {
								if (catchTicks == 0) {
									npc.startAnimation(ANIM_FALL_OVER); // Bird falls over
									transform(bird.landingId); // Swap trap to the "Bird Landing" animated object
								} else if (catchTicks == 1) {
									kill(npc); // Make the physical NPC disappear
									transform(bird.objectId); // Swap trap to the final "Bird Hanging" object
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
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(trapped.get().npcType);
		if(!data.isPresent()) throw new IllegalStateException("Invalid trap target id.");
		return data.get().reward;
	}

	@Override
	public double experience() {
		if(!trapped.isPresent()) throw new IllegalStateException("No npc is trapped.");
		Optional<BirdData> data = BirdData.getBirdDataByNpcId(trapped.get().npcType);
		if(!data.isPresent()) throw new IllegalStateException("Invalid trap target id.");
		return data.get().experience;
	}

	@Override
	public boolean canClaim(GlobalObject object) {
		if(!trapped.isPresent()) return false;
		BirdData data = BirdData.getBirdDataByNpcId(trapped.get().npcType).orElse(null);
		return data != null;
	}

	@Override
	public void setState(TrapState state) {
		if(state.equals(TrapState.PENDING)) throw new IllegalArgumentException("Cannot set trap state back to pending.");

		if(state.equals(TrapState.FALLEN)) {
			transform(FAILED_ID);
		} else if (state.equals(TrapState.CAUGHT)) {
			player.sendMessage("You caught something!");
		} else if (state.equals(TrapState.TRIGGERING)) {
			// Do nothing, just lock the state
		} else {
			player.sendMessage("Your trap has been triggered by something...");
		}
		super.setState(state);
	}

	public enum BirdData {
		// npcId, hangingId, landingId, requirement, experience, rewards
		CRIMSON_SWIFT(5549, 9373, 9349, 1, 34, new Item(526), new Item(10088), new Item(9978)),
		GOLDEN_WARBLER(5551, 9377, 9376, 5, 47, new Item(526), new Item(10090), new Item(9978)),
		COPPER_LONGTAIL(5552, 9379, 9378, 9, 61, new Item(526), new Item(10091), new Item(9978)),
		CERULEAN_TWITCH(5550, 9375, 9374, 11, 64.5, new Item(526), new Item(10089), new Item(9978)),
		TROPICAL_WAGTAIL(5548, 9348, 9347, 19, 95, new Item(526), new Item(10087), new Item(9978));

		private static final ImmutableSet<BirdData> VALUES = Sets.immutableEnumSet(EnumSet.allOf(BirdData.class));

		private final int npcId;
		private final int objectId; // Hanging ID
		private final int landingId; // The transition animation ID
		private final int requirement;
		private final double experience;
		private final Item[] reward;

		private BirdData(int npcId, int objectId, int landingId, int requirement, double experience, Item... reward) {
			this.npcId = npcId;
			this.objectId = objectId;
			this.landingId = landingId;
			this.requirement = requirement;
			this.experience = experience;
			this.reward = reward;
		}

		public int getNpcId() { return npcId; }

		public static Optional<BirdData> getBirdDataByNpcId(int id) {
			return VALUES.stream().filter(bird -> bird.npcId == id).findAny();
		}

		public static Optional<BirdData> getBirdDataByObjectId(int id) {
			return VALUES.stream().filter(bird -> bird.objectId == id).findAny();
		}
	}
}