package server.model.npcs.bosses.skotizo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import server.content.instances.SingleInstancedArea;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.world.Boundary;
import server.world.World;
import server.model.players.Player;
import server.model.minigames.rfd.DisposeTypes;
import server.util.Misc;
import server.world.objects.GlobalObject;

public class Skotizo extends SingleInstancedArea {

	// --- CONSTANTS ---
	private static final int START_X = 1700, START_Y = 9893;
	public static final int SPAWN_X = 1688, SPAWN_Y = 9880;

	public static final int SKOTIZO_ID = 7286;
	public static final int REANIMATED_DEMON = 7287;
	public static final int DARK_ANKOU = 7296;

	public static final int EMPTY_ALTAR_OBJ = 28924;
	public static final int AWAKENED_ALTAR_OBJ = 28923;

	// --- INSTANCE VARIABLES ---
	public boolean ankouSpawned = false;
	private boolean demonsSpawned = false;
	public boolean firstHit = true;

	public static final int AWAKENED_ALTAR_NORTH = 7288;
	public static final int AWAKENED_ALTAR_SOUTH = 7290;
	public static final int AWAKENED_ALTAR_WEST = 7292;
	public static final int AWAKENED_ALTAR_EAST = 7294;

	public void handleAltarDeath(int npcId) {
		Altar altar = Altar.getByNpcId(npcId);
		if (altar != null && activeAltars.contains(altar)) {
			World.getWorld().getGlobalObjects().remove(AWAKENED_ALTAR_OBJ, altar.x, altar.y, height);
			World.getWorld().getGlobalObjects().add(new GlobalObject(EMPTY_ALTAR_OBJ, altar.x, altar.y, height, altar.face, 10, -1, -1));
			player.getPA().sendChangeSprite(altar.spriteId, (byte) 0);
			activeAltars.remove(altar); // Safely removes it from the active count
		}
	}
	// Tracks currently active altars for easy cleanup and calculations
	private final List<Altar> activeAltars = new ArrayList<>();

	/**
	 * ENUM: Centralizes all altar data so we never have to write repetitive if/else blocks.
	 */
	public enum Altar {
		NORTH(1694, 9904, 2, 7288, 29232, "north"),
		SOUTH(1696, 9871, 0, 7290, 29233, "south"),
		WEST(1678, 9888, 1, 7292, 29234, "west"),
		EAST(1714, 9888, 3, 7294, 29235, "east");

		public final int x, y, face, npcId, spriteId;
		public final String name;

		Altar(int x, int y, int face, int npcId, int spriteId, String name) {
			this.x = x;
			this.y = y;
			this.face = face;
			this.npcId = npcId;
			this.spriteId = spriteId;
			this.name = name;
		}

		public static Altar getByNpcId(int npcId) {
			for (Altar altar : values()) {
				if (altar.npcId == npcId) return altar;
			}
			return null;
		}
	}

	public Skotizo(Player player, Boundary boundary, int height) {
		super(player, boundary, height);
	}

	/**
	 * Calculates damage reduction dynamically based on active altars.
	 * 0 Altars = 100% | 1 Altar = 75% | 2 Altars = 50% | 3 Altars = 25% | 4 Altars = 0%
	 */
	public int calculateSkotizoHit(Player attacker, int damage) {
		int altarCount = attacker.getSkotizo().activeAltars.size();

		if (altarCount >= 4) {
			if (attacker.debugMessage) attacker.sendMessage("0 hit - All altars active.");
			return 0;
		}

		double multiplier = 1.0 - (0.25 * altarCount);
		int finalDamage = (int) (damage * multiplier);

		if (attacker.debugMessage) {
			attacker.sendMessage("Altar count: " + altarCount + " | Multiplier: " + multiplier + " | Damage: " + finalDamage);
		}

		return finalDamage;
	}

	/**
	 * Triggers when Arclight hits an awakened altar.
	 */
	public void arclightEffect(NPC npc) {
		Altar altar = Altar.getByNpcId(npc.npcType);
		if (altar != null) {
			NPCHandler.kill(altar.npcId, height);
			// The actual removal from activeAltars should ideally happen in your NPC death handler,
			// but if it relies on this method, ensure it is removed:
			activeAltars.remove(altar);
		}
	}

	/**
	 * Handles Skotizo's special attacks (Awakening altars, spawning minions)
	 */
	public void skotizoSpecials() {
		NPC SKOTIZO = NPCHandler.getNpc(SKOTIZO_ID, height);
		if (SKOTIZO == null || SKOTIZO.isDead) return;

		int random = Misc.random(11);

		// 1. AWAKEN AN ALTAR
		if (random == 1) {
			// Find all altars that are NOT currently active
			List<Altar> inactiveAltars = Arrays.stream(Altar.values())
					.filter(a -> !activeAltars.contains(a))
					.collect(Collectors.toList());

			if (inactiveAltars.isEmpty()) {
				player.sendMessage("<col=FF8A5B>Your hits do not effect Skotizo... Maybe I should kill some of the altars...</col>");
				return;
			}

			// Shuffle the inactive altars and pick the first one (Replaces the dangerous while-loop)
			Collections.shuffle(inactiveAltars);
			Altar toAwaken = inactiveAltars.get(0);

			// Awaken the chosen altar
			player.sendMessage("<col=FF8A5B>The " + toAwaken.name + " altar has just awakened!</col>");
			player.getPA().sendChangeSprite(toAwaken.spriteId, (byte) 1);

			World.getWorld().getGlobalObjects().remove(EMPTY_ALTAR_OBJ, toAwaken.x, toAwaken.y, height);
			World.getWorld().getGlobalObjects().add(new GlobalObject(AWAKENED_ALTAR_OBJ, toAwaken.x, toAwaken.y, height, toAwaken.face, 10, -1, -1));
			World.getWorld().getNpcHandler().spawnNpc(player, toAwaken.npcId, toAwaken.x, toAwaken.y, height, 0, 100, 10, 200, 200, false, false);

			activeAltars.add(toAwaken);
		}

		// 2. SPAWN DEMONS
		else if (random == 2 || random == 3) {
			if (SKOTIZO.getHealth().getAmount() < 225 && !demonsSpawned) {
				SKOTIZO.forceChat("Gar mulno ful taglo!");
				World.getWorld().getNpcHandler().spawnNpc(player, REANIMATED_DEMON, player.getX() + 1, player.getY(), height, 0, 85, 8, 350, 300, true, false);
				World.getWorld().getNpcHandler().spawnNpc(player, REANIMATED_DEMON, player.getX() - 1, player.getY(), height, 0, 85, 8, 350, 300, true, false);
				World.getWorld().getNpcHandler().spawnNpc(player, REANIMATED_DEMON, player.getX(), player.getY() + 1, height, 0, 85, 8, 350, 300, true, false);
				demonsSpawned = true;
			}
		}

		// 3. SPAWN ANKOU
		else if (random == 4 && Misc.random(5) == 0) {
			if (SKOTIZO.getHealth().getAmount() < 150 && !ankouSpawned) {
				World.getWorld().getNpcHandler().spawnNpc(player, DARK_ANKOU, player.getX(), player.getY() - 1, height, 0, 60, 8, 350, 300, true, false);
				ankouSpawned = true;
			}
		}
	}

	/**
	 * Constructs the content by creating the event
	 */
	public void init() {
		World.getWorld().getNpcHandler().spawnNpc(player, SKOTIZO_ID, SPAWN_X, SPAWN_Y, height, 0, 450, 38, 500, 600, true, false);
		player.getPA().movePlayer(START_X, START_Y, height);

		for (Altar altar : Altar.values()) {
			player.getPA().sendChangeSprite(altar.spriteId, (byte) 0);
			World.getWorld().getGlobalObjects().add(new GlobalObject(EMPTY_ALTAR_OBJ, altar.x, altar.y, height, altar.face, 10, -1, -1));
		}
	}

	/**
	 * Disposes of the content by moving the player and removing left over content.
	 */
	public final void end(DisposeTypes dispose) {
		if (player == null) return;

		// Cleanup Minions
		if (demonsSpawned) {
			for (int i = 0; i < 3; i++) NPCHandler.kill(REANIMATED_DEMON, height);
		}
		if (ankouSpawned) {
			NPCHandler.kill(DARK_ANKOU, height);
		}

		// Cleanup Skotizo if incomplete
		if (dispose == DisposeTypes.INCOMPLETE) {
			NPCHandler.kill(SKOTIZO_ID, height);
			player.getPA().movePlayer(1665, 10046, 0); // Move player out
		}

		// Cleanup Altars (Iterates through the Enum safely)
		for (Altar altar : Altar.values()) {
			if (activeAltars.contains(altar)) {
				// If it was awake, remove the awakened object & kill the NPC
				World.getWorld().getGlobalObjects().remove(AWAKENED_ALTAR_OBJ, altar.x, altar.y, height);
				NPCHandler.kill(altar.npcId, height);
				player.getPA().sendChangeSprite(altar.spriteId, (byte) 0);
			} else {
				// If it was asleep, just remove the empty object
				World.getWorld().getGlobalObjects().remove(EMPTY_ALTAR_OBJ, altar.x, altar.y, height);
			}

			// Failsafe empty replacement for incomplete disposals to reset the room state
			if (dispose == DisposeTypes.INCOMPLETE && activeAltars.contains(altar)) {
				World.getWorld().getGlobalObjects().add(new GlobalObject(EMPTY_ALTAR_OBJ, altar.x, altar.y, height, altar.face, 10, -1, -1));
			}
		}

		// Final safety sweeps
		activeAltars.clear();
		World.getWorld().getGlobalObjects().remove(EMPTY_ALTAR_OBJ, height);
		World.getWorld().getGlobalObjects().remove(AWAKENED_ALTAR_OBJ, height);
	}

	@Override
	public void onDispose() {
		end(DisposeTypes.INCOMPLETE);
	}

	public int getHeight() {
		return height;
	}
}