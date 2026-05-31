package server.model.players.skills.hunter.trap;

import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import server.event.CycleEventContainer;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.items.Item;
import server.util.RandomGen;
import server.world.World;
import server.world.objects.GlobalObject;

/**
 * Represents a single trap on the world.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public abstract class Trap {

	public final RandomGen random = new RandomGen();
	private boolean smoked = false;
	private boolean baited = false;

	public boolean isSmoked() { return smoked; }
	public void setSmoked(boolean smoked) { this.smoked = smoked; }

	public boolean isBaited() { return baited; }
	public void setBaited(boolean baited) { this.baited = baited; }
	/**
	 * The owner of this trap.
	 */
	protected final Player player;
	
	/**
	 * The type of this trap.
	 */
	private final TrapType type;

	/**
	 * The state of this trap.
	 */
	private TrapState state;
	
	/**
	 * The global object spawned on the world.
	 */
	private GlobalObject object;
	
	/**
	 * Determines if this trap is abandoned.
	 */
	private boolean abandoned = false;
	
	/**
	 * Constructs a new {@link Trap}.
	 * @param player	{@link #player}.
	 * @param type		{@link #type}.
	 */
	public Trap(Player player, TrapType type) {
		this.player = player;
		this.type = type;
		this.state = TrapState.PENDING;
		this.object = new GlobalObject(type.objectId, player.getX(), player.getY(), player.getHeight());
	}
	
	/**
	 * Submits the trap task for this trap.
	 * @param player	the player to submit this trap task for.
	 */
	public void submit() {
		this.onSetup();
	}
	
	/**
	 * Attempts to trap the specified {@code npc} by checking the prerequisites and initiating the 
	 * abstract {@link #onCatch} method.
	 * @param npc	the npc to trap.
	 */
	public void trap(NPC npc) {
		if(!this.getState().equals(TrapState.PENDING) || !canCatch(npc) || this.isAbandoned()) {
			return;
		}
		onCatch(npc);
	}
	/**
	 * Determines if this trap type is meant for this specific NPC.
	 */
	public abstract boolean canAccept(NPC npc);

	/**
	 * The array containing every larupia item set.
	 */
	private static final int[] LARUPIA_SET = new int[]{10041, 10043, 10045}; 
	
	/**
	 * Determines fi the player has equiped any set that boosts the success formula.
	 * @return the amount of items the player is wearing.
	 */
	public boolean hasLarupiaSetEquipped() {
		return IntStream.range(0, LARUPIA_SET.length).allMatch(id -> player.getItems().isWearingItem(id));
	}
	
	/**
	 * Calculates the chance for the bird to be lured <b>or</b> trapped.
	 * @param npc		the npc being caught.
	 * @return the double value which defines the chance.
	 */
	/**
	 * Calculates the chance for the creature to be trapped based on Mod Ash's OSRS formulas.
	 * The returned value is rolled out of 255.
	 */
	public int successFormula(NPC npc) {
		Player player = this.getPlayer();
		if(player == null) {
			return 0;
		}

		int level = player.getSkills().getLevel(Skill.HUNTER);

		int low = 20;  // Default low for birds/ferrets if unmapped
		int high = 200; // Default high for birds/ferrets if unmapped

		// --- Mod Ash's Exact Chinchompa Values ---
		if (npc.npcType == 2910) { // Normal (Grey) Chinchompa
			low = 6;
			high = 268;
		} else if (npc.npcType == 2911 || npc.npcType == 2912) { // Red & Black Chinchompas
			low = -78;
			high = 228;
		}

		// The Official OSRS Linear Interpolation Math
		int chance = low + ((high - low) * (level - 1)) / 98;

		// Custom Server Buff: Larupia Set (Vanilla OSRS doesn't actually do this!)
		if (this.hasLarupiaSetEquipped()) {
			chance += 15;
		}
		if (this.isSmoked()) chance += 5;
		// 3% of 255 is ~8
		if (this.isBaited()) chance += 8;
		return chance;
	}
	
	/**
	 * Determines if the trap can catch.
	 * @param npc		the npc to check.
	 * @return {@code true} if the player can, {@code false} otherwise.
	 */
	public abstract boolean canCatch(NPC npc);
	
	/**
	 * The functionality that should be handled when the trap is picked up.
	 */
	public abstract void onPickUp();
	
	/**
	 * The functionality that should be handled when the trap is being set-up.
	 */
	public abstract void onSetup();
	
	/**
	 * The functionality that should be handled when the trap has catched.
	 * @param npc	the npc that was catched.
	 */
	public abstract void onCatch(NPC npc);
	

	/**
	 * The reward for this player.
	 * @param npcId		the npc that has been caught.
	 * return an array of items defining the reward.
	 */
	public abstract Item[] reward();
	
	/**
	 * The experience gained for catching this npc.
	 * @param npcId	the npc that has been caught.
	 * @return a numerical value defining the amount of experience gained.
	 */
	public abstract double experience();
	
	/**
	 * Determines if the trap can be claimed.
	 * @param object		the object that was interacted with.
	 * @return {@code true} if the trap can, {@code false} otherwise.
	 */
	public abstract boolean canClaim(GlobalObject object);
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the type
	 */
	public TrapType getType() {
		return type;
	}

	/**
	 * @return the state
	 */
	public TrapState getState() {
		return state;
	}

	/**
	 * @param state	the state to set.
	 */
	public void setState(TrapState state) {
		this.state = state;
	}
	
	/**
	 * @return the object
	 */
	public GlobalObject getObject() {
		return object;
	}
	
	/**
	 * Sets the object id.
	 * @param id	the id to set.
	 */
	public void setObject(int id) {
		if(!World.getWorld().getGlobalObjects().anyExists(getObject().getX(), getObject().getY(), getObject().getHeight())) {
			System.out.println("Hunter; No trap existed while attempting to catch");
			return;
		}
		this.object = new GlobalObject(id, this.getObject().getX(), this.getObject().getY(), this.getObject().getHeight());
	}

	/**
	 * @return the abandoned
	 */
	public boolean isAbandoned() {
		return abandoned;
	}

	/**
	 * @param abandoned the abandoned to set
	 */
	public void setAbandoned(boolean abandoned) {
		this.abandoned = abandoned;
	}

	/**
	 * The enumerated type whose elements represent a set of constants
	 * used to define the type of a trap.
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 */
	public enum TrapType {
		BOX_TRAP(9380, 10008),
		DISMANTLED_BOX_TRAP(9385, 10008),
		DISMANTLED_BIRD_SNARE(9344, 10006),
		BIRD_SNARE(9345, 10006);
		
		/**
		 * Caches our enum values.
		 */
		private static final ImmutableSet<TrapType> VALUES = Sets.immutableEnumSet(EnumSet.allOf(TrapType.class));
		
		/**
		 * The object id for this trap.
		 */
		private final int objectId;
		
		/**
		 * The item id for this trap.
		 */
		private final int itemId;
		
		/**
		 * Constructs a new {@link TrapType}.
		 * @param objectId	{@link #objectId}.
		 * @param itemId	{@link #itemId}.
		 */
		private TrapType(int objectId, int itemId) {
			this.objectId = objectId;
			this.itemId = itemId;
		}
		
		/**
		 * @return the object id
		 */
		public int getObjectId() {
			return objectId;
		}
		
		/**
		 * @return the item id
		 */
		public int getItemId() {
			return itemId;
		}
		
		/**
		 * Gets a trap dependent of the specified {@code objectId}.
		 * @param objectId	the id to get the trap type enumerator from.
		 * @return a {@link TrapType} wrapped in an optional, {@link Optional#empty} otherwise.
		 */
		public static Optional<TrapType> getTrapByObjectId(int objectId) {
			return VALUES.stream().filter(trap -> trap.objectId == objectId).findAny();
		}
		
		/**
		 * Gets a trap dependent of the specified {@code itemId}.
		 * @param itemId	the id to get the trap type enumerator from.
		 * @return a {@link TrapType} wrapped in an optional, {@link Optional#empty} otherwise.
		 */
		public static Optional<TrapType> getTrapByItemId(int itemId) {
			return VALUES.stream().filter(trap -> trap.itemId == itemId).findAny();
		}
	}
	
	/**
	 * The enumerated type whose elements represent a set of constants
	 * used to define the state of a trap.
	 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
	 */
	public enum TrapState {
		PENDING,
		TRIGGERING, // <--- Add this!
		CAUGHT,
		FALLEN;
	}
	
}
