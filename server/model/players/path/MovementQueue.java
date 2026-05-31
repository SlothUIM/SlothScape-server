/**
 *
 */
package server.model.players.path;

import java.util.ArrayDeque;
import java.util.Deque;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import server.clip.Region;
import server.event.CycleEventHandler;
import server.world.Location;
import server.model.Entity;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.construction.Construction;
import server.util.Misc;
import server.Server;

/**
 * @author ReverendDread
 * Jun 1, 2019
 */
@RequiredArgsConstructor @Slf4j
public class MovementQueue {

	/**
	 * The maximum size of the queue. If any additional steps are added, they are
	 * discarded.
	 */
	private static final int MAXIMUM_SIZE = 100;

	/**
	 * The event id of {@link EntityFollowEvent}
	 */
	private static final int FOLLOWING_EVENT_ID = Byte.MAX_VALUE;

	/**
	 * The entity whos movement queue this is.
	 */
	private final Entity entity;

	/**
	 * The {@link CycleEvent} which handles following.
	 */
	private EntityFollowEvent followingEvent;

	/**
	 * The queue of directions.
	 */
	private final Deque<Point> points = new ArrayDeque<Point>();

	/**
	 * The current {@link MovementStatus}.
	 */
	private boolean blockMovement = false;

	/**
	 * Are we currently moving?
	 */
	@Getter private boolean isMoving = false;

	/**
	 * Checks if we can walk from one position to another.
	 *
	 * @param from
	 * @param to
	 * @param size
	 * @return
	 */
	public static boolean canWalk(Location from, Location to, int size) {
		//System.out.println("[canWalk-");
		return Region.canMove(from, to, size, size);
	}

	/**
	 * Steps away from a Gamecharacter
	 *
	 * @param character The gamecharacter to step away from
	 */
	public static void clippedStep(Entity character) {
		if (character.getMovementQueue().canWalk(-1, 0)) {
			character.getMovementQueue().walkStep(-1, 0);
		} else if (character.getMovementQueue().canWalk(1, 0)) {
			character.getMovementQueue().walkStep(1, 0);
		} else if (character.getMovementQueue().canWalk(0, -1)) {
			character.getMovementQueue().walkStep(0, -1);
		} else if (character.getMovementQueue().canWalk(0, 1)) {
			character.getMovementQueue().walkStep(0, 1);
		}
	}

	/**
	 * Adds the first step to the queue, attempting to connect the server and client
	 * position by looking at the previous queue.
	 *
	 * @param clientConnectionPosition The first step.
	 * @return {@code true} if the queues could be connected correctly,
	 *         {@code false} if not.
	 */
	public boolean addFirstStep(Location clientConnectionPosition) {
		reset();
		addStep(clientConnectionPosition);
		return true;
	}

	/**
	 * Adds a step to walk to the queue.
	 *
	 * @param x       X to walk to
	 * @param y       Y to walk to
	 * @param clipped Can the step walk through objects?
	 */
	public void walkStep(int x, int y) {
		Location position = this.entity.getLocation().copy();
		position.setX(position.getX() + x);
		position.setY(position.getY() + y);
		addStep(position);
	}

	/**
	 * Adds a step.
	 *
	 * @param x           The x coordinate of this step.
	 * @param y           The y coordinate of this step.
	 * @param heightLevel
	 * @param flag
	 */
	public void addStep(int x, int y, int heightLevel) {
		//System.out.println("[player-");
		if (!canMove()) {
			return;
		}

		if (this.points.size() >= MAXIMUM_SIZE) {
			return;
		}

		final Point last = getLast();
		final int deltaX = x - last.position.getX();
		final int deltaY = y - last.position.getY();
		final Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE) {
			this.points.add(new Point(new Location(x, y, heightLevel), direction));
		}
	}

	/**
	 * Adds a step to the queue.
	 *
	 * @param step The step to add.
	 * @oaram flag
	 */
	public void addStep(Location step) {

		if (!canMove()) {
			return;
		}

		final Point last = getLast();
		final int x = step.getX();
		final int y = step.getY();
		int deltaX = x - last.position.getX();
		int deltaY = y - last.position.getY();
		final int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
		for (int i = 0; i < max; i++) {
			if (deltaX < 0) {
				deltaX++;
			} else if (deltaX > 0) {
				deltaX--;
			}
			if (deltaY < 0) {
				deltaY++;
			} else if (deltaY > 0) {
				deltaY--;
			}
			addStep(x - deltaX, y - deltaY, step.getZ());
		}
	}

	public boolean canMove() {
		if (this.entity.isNeedsPlacement()) {
			return false;
		}

		// If we are in the middle of a hop, we only return true if we have
		// force-injected steps to process.
		if (this.entity.isPlayer() && (this.entity.asPlayer().isForceMovementActive() || this.entity.asPlayer().forceMovement)) {
			return hasSteps(); // Only true if forceStep() was just called
		}

		if (this.entity.isPlayer() && this.entity.asPlayer().freezeDelay > 0 || this.blockMovement) {
			return false;
		}
		return true;
	}
	public void forceStep(int x, int y, int z) {
		// This bypasses the canMove() check to inject a specific tile
		final Point last = getLast();
		final int deltaX = x - last.position.getX();
		final int deltaY = y - last.position.getY();
		final Direction direction = Direction.fromDeltas(deltaX, deltaY);
		if (direction != Direction.NONE) {
			this.points.add(new Point(new Location(x, y, z), direction));
		}
	}
	/**
	 * Checks if the entity can walk to the given coordinates.
	 * @param deltaX
	 * @param deltaY
	 * @return
	 */
	public boolean canWalk(int deltaX, int deltaY) {
		if (!canMove()) {
			return false;
		}
		final Location to = new Location(this.entity.getLocation().getX() + deltaX, this.entity.getLocation().getY() + deltaY, this.entity.getLocation().getZ());
		if (this.entity.getLocation().getZ() == -1 && to.getZ() == -1) {
			return true;
		}
		return canWalk(this.entity.getLocation(), to, this.entity.getSize());
	}

	/**
	 * Handles region change from walking into a new region.
	 */
	public void handleRegionChange() {

		Player player = (Player) this.entity;

		final int diffX = this.entity.getLocation().getX() - player.getLastKnownLocation().getRegionX() * 8;
		final int diffY = this.entity.getLocation().getY() - player.getLastKnownLocation().getRegionY() * 8;
		boolean regionChanged = false;

		if (diffX < 16) {
			regionChanged = true;
		} else if (diffX >= 88) {
			regionChanged = true;
		}
		if (diffY < 16) {
			regionChanged = true;
		} else if (diffY >= 88) {
			regionChanged = true;
		}

		boolean heightChanged = player.HeightLevel != player.getLocation().getZ();

		if (regionChanged || heightChanged) {
			player.getPA().sendMapRegion();
			player.HeightLevel = player.getLocation().getZ();
			player.setHeight(player.getLocation().getZ());
		}


	}

	private void drainRunEnergy() {
		Player player = (Player) this.entity;
		if (player.isRunning() && player.runningDistanceTravelled > (player.wearingGrace() ? 0.05 + player.graceSum : player.staminaDelay != -1 ? 0.15 + (player.getSkills().getLevel(Skill.AGILITY) / 60)
																													  : 0.15 + (player.getSkills().getLevel(Skill.AGILITY) / 60))) {
			player.setRunEnergy(player.getRunEnergy() - 1);
			player.runningDistanceTravelled = 0;
			if (player.getRunEnergy() <= 0) {
				player.setRunEnergy(0);
				player.setRunning(false);
				player.getPA().sendConfig(173, 0);
			}
			player.getPA().sendFrame126(Integer.toString(player.getRunEnergy()), 149);
		}
	}
	public void drainRunEnergy2() {
		Player player = (Player) this.entity;
		if (!player.isRunning()) return;

		int weight = (int) player.getWeight(); // get total weight: equipped + inventory
		int agility = player.getSkills().getLevel(Skill.AGILITY);
		boolean stamina = player.hasStaminaEffect(); // true if stamina potion is active
		boolean ringOfEndurance = player.hasChargedRingOfEndurance(); // true if equipped with 500+ charges

		// Clamp weight to [0, 64]
		weight = Math.max(0, Math.min(weight, 64));

		// Base units lost per tick
		double baseUnits = 60 + (67.0 * weight / 64.0);
		baseUnits = Math.floor(baseUnits);
		double unitsLost = baseUnits * (1.0 - ((double) agility / 300.0));
		unitsLost = Math.floor(unitsLost);

		// Modifiers
		if (stamina)
			unitsLost = Math.floor(unitsLost * 0.3);
		if (ringOfEndurance)
			unitsLost = Math.floor(unitsLost * 0.85);

		if (unitsLost < 1) unitsLost = 1; // always lose at least 1

		int runEnergyUnits = player.getRunEnergy(); // 0..10,000
		int drain = (int) Math.ceil(unitsLost / 100.0);
		//System.out.println(drain);
		// If run energy < unitsLost, only drain what's left and set to 0
		if (runEnergyUnits < drain)
			drain = runEnergyUnits;
		// player.runEnergy -= drain;
		player.setRunEnergy(player.getRunEnergy() - drain);

		// If run energy hits 0, turn off running
		if (player.getRunEnergy() <= 0) {
			player.setRunEnergy(0);
			player.setRunning(false);
			player.getPA().sendConfig(173, 0);
		}
		player.getPA().sendFrame126(Integer.toString(player.getRunEnergy()), 149);
	}
	/**
	 * Gets the last point.
	 *
	 * @return The last point.
	 */
	private Point getLast() {
		final Point last = this.points.peekLast();
		if (last == null) {
			return new Point(this.entity.getLocation(), Direction.NONE);
		}
		return last;
	}

	/**
	 * Checks if the entity has steps.
	 * @return
	 */
	public boolean hasSteps() {
		return !this.points.isEmpty();
	}

	/**
	 * Processes the movement queue.
	 *
	 * Polls through the queue of steps and handles them.
	 *
	 */
	public void process() {

		// Make sure movement isnt restricted..
		if (!canMove()) {
			reset();
			return;
		}

		// Poll through the actual movement queue and
		// begin moving.
		Point walkPoint = null;
		Point runPoint = null;

		walkPoint = this.points.poll();

		if (isRunToggled()) {
			runPoint = this.points.poll();
		}

		Location previousPosition = this.entity.getLocation();
		boolean moved = false;

		if (walkPoint != null && walkPoint.direction != Direction.NONE) {
			Location next = walkPoint.position;
			// Only process the step if it's NOT our current location
			if (!this.entity.getLocation().equals(next) && validateStep(next)) {
				this.entity.setX(next.getX());
				this.entity.setY(next.getY());
				this.entity.setWalkingDirection(walkPoint.direction);
				moved = true;
			}
		}

		if (runPoint != null && runPoint.direction != Direction.NONE) {
			Location next = runPoint.position;
			previousPosition = next;
			if (validateStep(next)) {
				this.entity.setX(next.getX());
				this.entity.setY(next.getY());
				this.entity.setRunningDirection(runPoint.direction);
				if (this.entity.isPlayer()) {
					this.entity.asPlayer().runningDistanceTravelled++;
				}
				moved = true;
			} else {
				reset();
				return;
			}
		}

		// Handle movement-related events such as
		// region change and energy drainage.
		if (this.entity.isPlayer()) {
			if (moved) {
				handleRegionChange();
				drainRunEnergy2();
				this.entity.asPlayer().setPreviousLocation(previousPosition);
			}
		}

		//log.info("X {}, Y {}", this.entity.getLocation().getLocalX(), this.entity.getLocation().getLocalY());

		this.isMoving = moved;
	}
	public void stop() {
		this.points.clear();
		this.isMoving = false;
		// This ensures the current processing tick stops immediately
		this.entity.setWalkingDirection(Direction.NONE);
		this.entity.setRunningDirection(Direction.NONE);
	}
	private boolean validateStep(Location next) {
		if (this.followingEvent != null && next.equals(this.followingEvent.getFollowing().getLocation())) {
			return false;
		}
		return true;
	}

	/**
	 * Stops the movement.
	 */
	public MovementQueue reset() {
		this.points.clear();
		//this.isMoving = false;
		return this;
	}

	/**
	 * Starts a new {@link CharacterFollowTask} which starts following the given
	 * {@link Actor}.
	 *
	 * @param follow
	 */
	public void follow(Entity follow) {
		if (follow == null) {
			resetFollowing();
			return;
		}
		if (this.followingEvent == null || !CycleEventHandler.getSingleton().isAlive(this.entity, FOLLOWING_EVENT_ID)) {
			this.followingEvent = new EntityFollowEvent(this.entity, follow);
			CycleEventHandler.getSingleton().addEvent(FOLLOWING_EVENT_ID, this.entity, this.followingEvent, 1);
		} else {
			this.followingEvent.setFollowing(follow);
		}
	}

	/**
	 * Checks if we're currently following the given {@link Actor}.
	 *
	 * @param character
	 * @return
	 */
	public boolean isFollowing(Entity character) {
		if (this.followingEvent != null) {
			return this.followingEvent.getFollowing().equals(character);
		}
		return false;
	}

	/**
	 * Stops any following which might be active.
	 */
	public void resetFollowing() {
		if (this.followingEvent != null) {
			CycleEventHandler.getSingleton().stopEvents(this.entity, FOLLOWING_EVENT_ID);
			if (this.entity.isPlayer()) {
				this.entity.asPlayer().followId = 0;
				this.entity.asPlayer().followId2 = 0;
				this.entity.asPlayer().mageFollow = false;
			}
		}
		this.followingEvent = null;
	}

	/**
	 * Returns the entitys running state.
	 * @return
	 */
	public boolean isRunToggled() {
		return this.entity.isPlayer() && entity.asPlayer().isRunning();
	}

	/**
	 * Sets the movement block state.
	 * @param blockMovement
	 * @return
	 */
	public MovementQueue setBlockMovement(boolean blockMovement) {
		this.blockMovement = blockMovement;
		return this;
	}

	/**
	 * Gets the size of the queue.
	 *
	 * @return The size of the queue.
	 */
	public int size() {
		return this.points.size();
	}

	@RequiredArgsConstructor
	public static final class Point {

		private final Location position;
		private final Direction direction;

		@Override
		public String toString() {
			return Point.class.getName() + " [direction=" + this.direction + ", position=" + this.position + "]";
		}

	}

}
