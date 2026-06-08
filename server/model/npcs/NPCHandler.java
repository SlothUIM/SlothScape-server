package server.model.npcs;

import java.util.Map;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import server.model.minigames.cox.RaidRoom;
import server.model.players.packets.dialogue.DialogueService;
import server.model.players.quests.MerlinsCrystal;
import server.world.Location;
import server.clip.*;
import server.clip.Region;
import server.content.barrows.Barrows;
import server.content.barrows.brothers.Brother;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.Config;
import server.model.players.combat.Damage;
import server.model.npcs.animations.AttackAnimation;
import server.model.npcs.animations.BlockAnimation;
import server.model.npcs.animations.DeathAnimation;
import server.model.players.Player;
import server.model.Entity;
import server.model.HealthStatus;
import server.model.instance.Instance;
import server.model.players.skills.Skill;
import server.model.players.skills.hunter.impling.PuroPuro;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.model.players.skills.mining.motherlode.OreTile;
import server.model.minigames.rfd.DisposeTypes;
import server.model.minigames.warriors_guild.AnimatedArmour;
import server.model.players.PlayerHandler;
import server.model.players.Sound;
import server.model.players.skills.construction.Butlers;
import server.model.players.skills.construction.Servant;
import server.model.players.combat.CombatType;
import server.model.players.combat.Hitmark;
import server.model.players.combat.Special;
import server.model.players.combat.Specials;
import server.model.players.content.Skillcapes.SkillcapePerks;
import server.model.players.quests.EleWorkShop;
import server.model.players.quests.VampyreSlayer;
import server.util.Misc;
import server.model.npcs.bosses.CorporealBeast;
import server.model.npcs.bosses.Scorpia;
import server.model.npcs.bosses.EventBoss.EventBossHandler;
import server.model.npcs.bosses.EventBoss.impl.Tarn;
import server.model.npcs.bosses.cerberus.Cerberus;
import server.model.npcs.bosses.raids.Tekton;
import server.model.npcs.bosses.skotizo.Skotizo;
import server.model.npcs.bosses.vorkath.Vorkath;
import server.model.npcs.bosses.wildernessboss.WildernessBossHandler;
import server.model.npcs.bosses.wildypursuit.Glod;
import server.model.npcs.bosses.wildypursuit.IceQueen;
import server.model.npcs.bosses.zulrah.Zulrah;
import server.model.npcs.combat.CombatScript;
import server.world.Boundary;
import server.world.World;
import server.world.objects.GlobalObject;

public class NPCHandler {
	public static int maxNPCs = 30000;
	public static int maxListedNPCs = 20000;
	public static int maxNPCDrops = 20000;
	public static NPC npcs[] = new NPC[maxNPCs];
	// Active index tracker for O(1) loop processing
	private static final List<Integer> activeIndices = new ArrayList<>();

	// Immutable shout configurations to stop allocation thrashing
	private static final String[] DRILL_SHOUTS = {"Let's go, maggots!", "Hustle!", "Keep moving!"};
	private static final String[] TOWN_CRIER_SHOUTS = {"Chambers of Xeric is here!", "Speak with Mod Sloth if you wish to contribute."};
	private static final String[] SHANOMI_QUOTES = {
			"Those things which cannot be seen, perceive them.",
			"Do nothing which is of no use.",
			"Think not dishonestly.",
			"The Way in training is.",
			"Gain and loss between you must distinguish.",
			"Trifles pay attention even to.",
			"Way of the warrior this is.",
			"Acquainted with every art become.",
			"Ways of all professions know you.",
			"Judgment and understanding for everything develop you must."
	};
	public static boolean projectileClipping = true;
	private static NPCDef[] npcDef = new NPCDef[maxListedNPCs];
	public static String tektonAttack;

	private static final String CONFIG_FILE = "./Data/cfg/npc_config.cfg";
	private static final String SPAWN_FILE = "./Data/json/npc_spawns.json";
	private static final String SIZE_FILE = "./Data/cfg/npc_sizes.txt";

	public void init() {
		activeIndices.clear();
		for (int i = 0; i < maxNPCs; i++) {
			npcs[i] = null;
			NPCDefinitions.getDefinitions()[i] = null;
		}

		loadNPCList(CONFIG_FILE);
		NPCSpawns.loadNPCSpawns();
		NPCSpawns.getSpawns().forEach(this::spawnNPC);
		loadNPCSizes(SIZE_FILE);
		startGame();
	}

	private void spawnNPC(NPCSpawns s) {
	//	System.out.println("Spawning NPC ID " + s.getNpcId() + " at (" + s.getXPos() + ", " + s.getYPos() + 
		//		" HP:"+s.getHealth()+" MaxHit: "+s.getMaxHit()+")");
		newNPC(
			s.getNpcId(),
			s.getXPos(),
			s.getYPos(),
			s.getHeight(),
			s.getWalkType(),
			s.getHealth(),
			s.getMaxHit(),
			s.getAttack(),
			s.getDefence(),
			null
		);
	}

	private void loadNPCSizes(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			throw new RuntimeException("ERROR: " + fileName + " does not exist.");
		}
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				int npcId, size;
				try {
					npcId = Integer.parseInt(line.split("\t")[0]);
					size = Integer.parseInt(line.split("\t")[1]);
					if (npcId > -1 && size > -1) {
						if (NPCDefinitions.getDefinitions()[npcId] == null) {
							NPCDefinitions.create(npcId, "None", 0, 0, size);
						} else {
							NPCDefinitions.getDefinitions()[npcId].setSize(size);
						}
					}
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static NPCDef[] getNpcDef() {
		return npcDef;
	}
	public void startGame() {
		for (int i = 0; i < PuroPuro.IMPLINGS.length; i++) {
			newNPC(PuroPuro.IMPLINGS[i][0], PuroPuro.IMPLINGS[i][1], PuroPuro.IMPLINGS[i][2], 0, 1, -1, -1, -1, -1, null);
		}

		/**
		 * Random spawns
		 */
		int random_spawn = Misc.random(2);
		int x = 0;
		int y = 0;
		switch (random_spawn) {
		case 0:
			x = 2620;
			y = 4347;
			break;

		case 1:
			x = 2607;
			y = 4321;
			break;

		case 2:
			x = 2589;
			y = 4292;
			break;
		}
		newNPC(7302, x, y, 0, 1, -1, -1, -1, -1, null);
	}
	public void newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, Instance instance) {
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1) return;

		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.resetDamageTaken();
		newNPC.setInstance(instance);

		if ((newNPC.npcType >= 1635 && newNPC.npcType <= 1643) || newNPC.npcType == 1654 || newNPC.npcType == 7302) {
			newNPC.setGlobalRoaming(true);
		}
		if (newNPC.npcType == 7206 || newNPC.npcType == 5935) {
			for (Player c : PlayerHandler.players) {
				if (c != null && c.withinDistance(c.getX(), c.getY(), newNPC.getX(), newNPC.getY(), 2)) {
					//newNPC.requestTransform(newNPC.npcType == 7206 ? 7207 : 5936);
					newNPC.killerId = c.getIndex();
					newNPC.underAttack = true;
					newNPC.underAttackBy = newNPC.killerId;
					newNPC.walkingType = 1;
				}
			}
		}
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		} // Save active entity to cache list
	}
	public void multiAttackGfx(int i, int gfx) {
		if (npcs[i].projectileId < 0)
			return;
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = (Player) PlayerHandler.players[j];
				if (c.getHeight() != npcs[i].getHeight())
					continue;
				if (PlayerHandler.players[j].goodDistance(c.getX(),
						c.getY(), npcs[i].getX(), npcs[i].getY(), 15)) {
					int nX = NPCHandler.npcs[i].getX() + offset(i);
					int nY = NPCHandler.npcs[i].getY() + offset(i);
					int pX = c.getX();
					int pY = c.getY();
					int offX = (nY - pY) * -1;
					int offY = (nX - pX) * -1;
					c.getPA().createPlayersProjectile(nX, nY, offX, offY, 50,
							getProjectileSpeed(i), npcs[i].projectileId, 43,
							31, -c.getId() - 1, 65);
				}
			}
		}
	}
	public static void kill(int minHeight, int maxHeight, int... npcType) {
		nonNullStream()
		.filter(n -> IntStream.of(npcType).anyMatch(type -> type == n.npcType) && n.getHeight() >= minHeight && n.getHeight() <= maxHeight)
		.forEach(npc -> npc.isDead = true);
	}
	public static void kill(int npcType, int height) {
		nonNullStream()
		.filter(n -> n.npcType == npcType && n.getHeight() == height)
		.filter(npc -> !npc.isDead)
		.forEach(npc -> npc.isDead = true);
	}
	public static Stream<NPC> nonNullStream() {
		return Arrays.stream(npcs).filter(Objects::nonNull);
	}
	public boolean switchesAttackers(int i) {
		switch (npcs[i].npcType) {
		case 7249:
			return true;

		}

		return false;
	}
	/**
	 * Resets players in combat
	 */
	public static NPC getNpc(int npcType) {
		for (NPC npc : npcs)
			if (npc != null && npc.npcType == npcType)
				return npc;
		return null;
	}
	public static NPC getNpc(int npcType, int x, int y) {
		for (NPC npc : npcs)
			if (npc != null && npc.npcType == npcType && npc.getX() == x && npc.getY() == y)
				return npc;
		return null;
	}
	public static void destroy(NPC npc) {
		IntStream.range(0, npcs.length).forEach(index -> {
			if (npcs[index] != null && npcs[index] == npc) {
				npcs[index].setInstance(null);
				npcs[index] = null;
			}
		});
	}
	public static NPC getNpc(int npcType, int x, int y, int height) {
		for (NPC npc : npcs) {
			if (npc != null && npc.npcType == npcType && npc.getX() == x && npc.getY() == y && npc.getHeight() == height) {
				return npc;
			}
		}
		return null;
	}

	public static NPC getNpc(int npcType, int height) {
		for (NPC npc : npcs) {
			if (npc != null && npc.npcType == npcType && npc.getHeight() == height) {
				return npc;
			}
		}
		return null;
	}
	/**
	 * Summon npc, barrows, etc
	 **/
	/*public NPC spawnNpc(final Player c, int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit,
			int attack, int defence, boolean attackPlayer, boolean headIcon) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc");
			return null;
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		final NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getIndex();
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (attackPlayer) {
			newNPC.underAttack = true;
			newNPC.killerId = c.getIndex();
			c.underAttackBy = slot;
			c.underAttackBy2 = slot;
		}
		npcs[slot] = newNPC;
		if (newNPC.npcType == 1605) {
			newNPC.forceChat("You must prove yourself... now!");
			newNPC.gfx100(86);
		}
		if (newNPC.npcType == 1606) {
			newNPC.forceChat("This is only the beginning, you can't beat me!");
			newNPC.gfx100(86);
		}
		if (newNPC.npcType == 1607) {
			newNPC.forceChat("Foolish mortal, I am unstoppable.");
		}
		if (newNPC.npcType == 1608) {
			newNPC.forceChat("Now you feel it... The dark energy.");
		}
		if (newNPC.npcType == 1609) {
			newNPC.forceChat("Aaaaaaaarrgghhhh! The power!");
		}
		return newNPC;
	}*/
	public static NPC spawn(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack,
			int defence, boolean attackPlayer) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc - line 2287");
			return null;
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
		return newNPC;
	}
	public static NPC spawnNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit,
			int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc - line 2287");
			return null;
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		//System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc - line 2287");
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		//newNPC.getHealth().setAmount(HP);
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
		return newNPC;
	}

	private ArrayList<int[]> vetionSpellCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> archSpellCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> hydraPoisonCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> fanaticSpellCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> corpSpellCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> olmSpellCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> explosiveSpawnCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> cerberusGroundCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> DragonGroundCoordinates = new ArrayList<>(3);
	private ArrayList<int[]> vasaRockCoordinates = new ArrayList<>(1);
	public void multiAttackDamage(int i) {
		int damage = Misc.random(getMaxHit(i));
		Hitmark hitmark = damage > 0 ? Hitmark.HIT : Hitmark.MISS;
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				Player c = PlayerHandler.players[j];
				if (c.isDead || c.getHeight() != npcs[i].getHeight())
					continue;
				if (PlayerHandler.players[j].isInvisible()) {
					continue;
				}
				if (PlayerHandler.players[j].goodDistance(c.getX(), c.getY(), npcs[i].getX(), npcs[i].getY(),
						multiAttackDistance(npcs[i]))) {
					if (npcs[i].attackType1 == CombatType.SPECIAL) {
						if (npcs[i].npcType == 5862) {
							if (cerberusGroundCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						if (npcs[i].npcType == 6618) {
							if (archSpellCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						
						if (npcs[i].npcType == 8609) {
							if (hydraPoisonCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						
						if (npcs[i].npcType == 7566) {
							if (vasaRockCoordinates.parallelStream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						if (npcs[i].npcType == 8030) {
							if (DragonGroundCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						if (npcs[i].npcType == 6766) {
							if (explosiveSpawnCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}

						if (Boundary.isIn(npcs[i], Boundary.CORPOREAL_BEAST_LAIR)) {
							if (!Boundary.isIn(c, Boundary.CORPOREAL_BEAST_LAIR)) {
								return;
							}
						}
						if (npcs[i].npcType == 6619) {
							if (fanaticSpellCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						if (npcs[i].npcType == 6611 || npcs[i].npcType == 6612) {
							if (!(c.getX() > npcs[i].getX() - 5 && c.getX() < npcs[i].getX() + 5 && c.getY() > npcs[i].getY() - 5
									&& c.getY() < npcs[i].getY() + 5)) {
								continue;
							}
							c.sendMessage(
									"Vet'ion pummels the ground sending a shattering earthquake shockwave through you.");
							//createVetionEarthquake(c);
						}
						if (npcs[i].npcType == 319) {
							if (corpSpellCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						c.appendDamage(damage, hitmark);
					} else if (npcs[i].attackType1 == CombatType.DRAGON_FIRE) {
						int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(33115) || c.getItems().isWearingItem(11283)
								|| c.getItems().isWearingItem(11284) ? 1 : 0;
						if (System.currentTimeMillis() - c.lastAntifirePotion < c.antifireDelay) {
							resistance++;
						}
					//	c.sendMessage("Resistance: " + resistance);
						if (resistance == 0) {
							damage = Misc.random(getMaxHit(i));
							c.sendMessage("You are badly burnt by the dragon fire!");
						} else if (resistance == 1)
							damage = Misc.random(15);
						else if (resistance == 2)
							damage = 0;
						if (c.getHealth().getAmount() - damage < 0)
							damage = c.getHealth().getAmount();
						c.gfx100(npcs[i].endGfx);
						c.appendDamage(damage, hitmark);
					} else if (npcs[i].attackType1 == CombatType.MAGE) {
						if (npcs[i].npcType == 6611 || npcs[i].npcType == 6612) {
							if (vetionSpellCoordinates.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								continue;
							}
						}
						if (npcs[i].npcType == 3162) {
							damage /= 3;
						}
						if (!c.protectingMagic()) {
							if (Misc.random(500) + 200 > Misc.random(c.getCombat().mageDef())) {
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else {
								c.appendDamage(0, Hitmark.MISS);
							}
						} else {
							switch (npcs[i].npcType) {
							case 1046:
							case 3162:
							case 6610:
							case 6611:
							case 6612:
								damage *= .5;
								break;
								
							case 7554:
								if (c.protectingMagic())
									damage /= 2;
								break;

							case 319:
								if (c.protectingMagic())
									damage /= 2;
								break;
							case 6477:
								if (c.protectingMagic())
									damage /= 2;
							default:
								damage = 0;
								break;
							}
							c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
						}
					} else if (npcs[i].attackType1 == CombatType.RANGE) {
						if (!c.protectingRange()) {
							if (Misc.random(500) + 200 > Misc.random(c.getCombat().calculateRangeDefence())) {
								c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
							} else {
								c.appendDamage(0, Hitmark.MISS);
							}
							if (npcs[i].npcType == 2215) {
								damage /= 2;
							}
							if (npcs[i].npcType == 5462) {
								damage /= 2;
							}
						} else {
							switch (npcs[i].npcType) {
							default:
								damage = 0;
								break;
							}
							c.appendDamage(damage, Hitmark.MISS);
						}
					}
					if (npcs[i].endGfx > 0) {
						c.gfx0(npcs[i].endGfx);
					}
					c.getCombat().appendVengeanceNPC(damage, i);
				}
			}
		}
	}

	public int getClosePlayer(int i) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (j == npcs[i].spawnedBy)
					return j;
				if (goodDistance(PlayerHandler.players[j].getX(),
						PlayerHandler.players[j].getY(), npcs[i].getX(),
						npcs[i].getY(), 2 + distanceRequired(i)
								+ followDistance(i))
						|| isFightCaveNpc(i)) {
					if ((PlayerHandler.players[j].underAttackBy <= 0 && PlayerHandler.players[j].underAttackBy2 <= 0)
							|| PlayerHandler.players[j].inMulti())
						if (PlayerHandler.players[j].getHeight() == npcs[i].getHeight())
							return j;
				}
			}
		}
		return 0;
	}

	public int getCloseRandomPlayer(int i) {
		ArrayList<Integer> players = new ArrayList<>();
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null) {
				if (Boundary.isIn(npcs[i], Boundary.CORPOREAL_BEAST_LAIR)) {
					if (!Boundary.isIn(PlayerHandler.players[j], Boundary.CORPOREAL_BEAST_LAIR)) {
						npcs[i].killerId = 0;
						continue;
					}
				}
				/**
				 * Skips attacking a player if mode set to invisible
				 */
				if (PlayerHandler.players[j].isInvisible()) {
					continue;
				}
				if (Boundary.isIn(npcs[i], Boundary.GODWARS_BOSSROOMS)) {
					if (!Boundary.isIn(PlayerHandler.players[j], Boundary.GODWARS_BOSSROOMS)) {
						npcs[i].killerId = 0;
						continue;
					}
				}
				if (goodDistance(PlayerHandler.players[j].getX(), PlayerHandler.players[j].getY(), npcs[i].getX(),
						npcs[i].getY(), distanceRequired(i) + followDistance(i)) || isFightCaveNpc(i)) {
					if ((PlayerHandler.players[j].underAttackBy <= 0 && PlayerHandler.players[j].underAttackBy2 <= 0)
							|| PlayerHandler.players[j].inMulti())
						if (PlayerHandler.players[j].getHeight() == npcs[i].getHeight())
							players.add(j);
				}
			}
		}
		if (players.size() > 0)
			return players.get(Misc.random(players.size() - 1));
		else
			return 0;
	}
    public static int getNpcSize(int npcId) {
        return NPCDefinition.forId(npcId).getSize();
    }
	/**
	 * Updated to support the new drop table checker
	 * 
	 * @param i
	 * @param searching
	 * @return
	 */
	public boolean isAggressive(int i, boolean searching) {

		if(npcs[i] != null && npcs[i].getCombatScript() != null)
            return npcs[i].getCombatScript().isAggressive(npcs[i]);
		if (searching) {
			
			switch (i) {
				case 5935:
					return true;
				case 7206:
					return true;
			// Barrows tunnel monsters
			case 7249:
				return true;
			case 5001://event bosses
			case 6477:
				return true;
			case 7283://lillia
				return false;
			case 5916:
			case 690:
			case 963:
			case 965:
			case 955:
			case 957:
			case 6524:
			case 959:
			case 5867:
			case 5868:
			case 5869:
			case 2042:
			case 239:
			case 7413:
			case 1739:
			case 1740:
			case 1741:
			case 1742:
			case 2044:
			case 2043:
			case 465:
			case 6475:
			case 8062:// vorkath crab aggressive
				case 7706:
			//case Zulrah.SNAKELING:
			case 5054:
			case 6611:
			case 6612:
			case 6610:
			case 494:
			case 5535:
			case 2550:
			case 2551:
			case 50:
			case 28:
			case 2552:
			case 6609:
			case 2553:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case 2265:
			case 2266:
			case 2267:
			case 2035:
			case 5779:
			case 291:
			case 435:
			case 135:
			case 7276:
			case 5944: // Rock lobster

				// Godwars
			case 3138:
			case 2205:
			case 2206:
			case 2207:
			case 2208:
			case 2209:
			case 2211:
			case 2212:
			case 2215:
			case 5462:
			case 2216:
			case 2217:
			case 2218:
			case 2233:
			case 2234:
			case 2235:
			case 2237:
			case 2242:
			case 2243:
			case 2244:
			case 2245:
			case 3129:
			case 3130:
			case 3131:
			case 3132:
			case 3133:
			case 3134:
			case 3135:
			case 3137:
			case 3139:
			case 3140:
			case 3141:
			case 3159:
			case 3160:
			case 3161:
			case 3162:
			case 3163:
			case 3164:
			case 3165:
			case 3166:
			case 3167:
			case 3168:
			case 3174:

				// Barrows tunnel monsters
			case 1678:
			case 1679:
			case 1683:
			case 1684:
			case 1685:

			case Skotizo.SKOTIZO_ID:
			case Skotizo.REANIMATED_DEMON:
			case Skotizo.DARK_ANKOU:
				// GWD
			case 6230:
			case 6231:
			case 6229:
			case 6232:
			case 6240:
			case 6241:
			case 6242:
			case 6233:
			case 6234:
			case 6243:
			case 6244:
			case 6245:
			case 6246:
			case 6238:
			case 6239:
			case 6625:
			case 122:// Npcs That Give BandosKC
			case 6278:
			case 6277:
			case 6276:
			case 6283:
			case 6282:
			case 6281:
			case 6280:
			case 6279:
			case 6271:
			case 6272:
			case 6273:
			case 6274:
			case 6269:
			case 6270:
			case 6268:
			case 6221:
			case 6219:
			case 6220:
			case 6217:
			case 6216:
			case 6215:
			case 6214:
			case 6213:
			case 6212:
			case 6211:
			case 6218:
			case 6275:
			case 6257:// Npcs That Give SaraKC
			case 6255:
			case 6256:
			case 6259:
			case 6254:
			case 1689:
			case 1694:
			case 1699:
			case 1704:
			case 1709:
			case 1714:
			case 1724:
			case 1734:
			case 6914: // Lizardman, Lizardman brute
			case 6915:
			case 6916:
			case 6917:
			case 6918:
			case 6919:
			case 6766:
			case 7573:
			case 7617: // Tekton magers
			case 7544: // Tekton
			case 7604: // Skeletal mystic
			case 7605: // Skeletal mystic
			case 7606: // Skeletal mystic
			case 7585: //
			case 7554: //great olm
			case 7563: // muttadiles
			case 5129:
			case 4922:
				
			case 7547: //xeric mini raid
			case 7548:
			case 7559:
			case 7560:
			case 7597:
			case 7596:
			case 7577:
			case 7578:
			case 7579:
			case 7586:
			case 7538:
			case 7531://xeric mini raid bosses
			case 7543:
			case 7566:
				return true;
			case 8030:// Addy dragon
			case 8031:// Rune dragon
				return true;
			case 1524:
			case 6600:
			case 6601:
			case 7553:
			case 7555:
			case 6602:
			case 1049:
			case 6617:
			case 6620:
				return false;
			}
		} else {
			if(npcs[i] != null)
			switch (npcs[i].npcType) {
			case 5916:
			case 690:
			case 963:
			case 965:
			case 6475:
			case 6477:
			case 955:
			case 957:
			case 959:
			case 5867:
			case 5868:
			case 5869:
			case 2042:
			case 239:
			case 7413:
			case 1739:
			case 1740:
			case 1741:
			case 1742:
			case 2044:
			case 2043:
			case 465:
			//case Zulrah.SNAKELING:
			case 5054:
			case 6611:
			case 6612:
			case 6610:
			case 494:
			case 5535:
			case 2550:
			case 2551:
			case 50:
			case 28:
			case 2552:
			case 6609:
			case 2553:
			case 2562:
			case 2563:
			case 2564:
			case 2565:
			case 2265:
			case 2266:
			case 2267:
			case 2035:
			case 5779:
			case 291:
			case 435:
			case 135:
			case 484:
			case 7276:
			case 5944: // Rock lobster

				// Godwars
			case 3138:
			case 2205:
			case 2206:
			case 2207:
			case 2208:
			case 2209:
			case 2211:
			case 2212:
			case 2215:
			case 5462:
			case 2216:
			case 2217:
			case 2218:
			case 2233:
			case 2234:
			case 2235:
			case 2237:
			case 2242:
			case 2243:
			case 2244:
			case 2245:
			case 3129:
			case 3130:
			case 3131:
			case 3132:
			case 3133:
			case 3134:
			case 3135:
			case 3137:
			case 3139:
			case 3140:
			case 3141:
			case 3159:
			case 3160:
			case 3161:
			case 3162:
			case 3163:
			case 3164:
			case 3165:
			case 3166:
			case 3167:
			case 3168:
			case 3174:

			case Skotizo.SKOTIZO_ID:
			case Skotizo.REANIMATED_DEMON:
			case Skotizo.DARK_ANKOU:

				// Barrows tunnel monsters
			case 1678:
			case 1679:
			case 1683:
			case 1684:
			case 1685:
				// GWD
			case 6230:
			case 6231:
			case 6229:
			case 6232:
			case 6240:
			case 6241:
			case 6242:
			case 6233:
			case 6234:
			case 6243:
			case 6244:
			case 6245:
			case 6246:
			case 6238:
			case 6239:
			case 6625:
			case 122:// Npcs That Give BandosKC
			case 6278:
			case 6277:
			case 6276:
			case 6283:
			case 6282:
			case 6281:
			case 6280:
			case 6279:
			case 6271:
			case 6272:
			case 6273:
			case 6274:
			case 6269:
			case 6270:
			case 6268:
			case 6221:
			case 6219:
			case 6220:
			case 6217:
			case 6216:
			case 6215:
			case 6214:
			case 6213:
			case 6212:
			case 6211:
			case 6218:
			case 6275:
			case 6257:// Npcs That Give SaraKC
			case 6255:
			case 6256:
			case 6259:
			case 6254:
			case 1689:
			case 1694:
			case 1699:
			case 1704:
			case 1709:
			case 1714:
			case 1724:
			case 1734:
			case 6914: // Lizardman, Lizardman brute
			case 6915:
			case 6916:
			case 6917:
			case 6918:
			case 6919:
			case 6766:
			case 7573:
			case 7617: // Tekton magers
			case 7544: // Tekton
			case 7604: // Skeletal mystic
			case 7605: // Skeletal mystic
			case 7606: // Skeletal mystic
			case 5129:
			case 4922:
			case 7388: // Start of superior
			case 7389:
			case 7390:
			case 7391:
			case 7392:
			case 7393:
			case 7394:
			case 7395:
			case 7396:
			case 7397:
			case 7398:
			case 7399:
			case 7400:
			case 7401:
			case 7402:
			case 7403:
			case 7404:
			case 7405:
			case 7406:
			case 7407:
			case 7409:
			case 7410:
			case 7411: // end of superior
				return true;
			case 1524:
			case 6600:
			case 6601:
			case 6602:
			case 1049:
			case 6617:
			case 6620:
				return false;

			case 8028:
				return true;
			}
			if (npcs[i].inWild() && npcs[i].getHealth().getMaximum() > 0)
				return true;
			//if (npcs[i].inRaids() && npcs[i].getHealth().getMaximum() > 0)
			//	return true;
			//if (npcs[i].inXeric() && npcs[i].getHealth().getMaximum() > 0)
			//	return true;
			return isFightCaveNpc(i);
		}
		return false;
	}
	public static boolean isSkotizoNpc(int i) {
		if (npcs[i] == null)
			return false;
		switch (npcs[i].npcType) {
		case Skotizo.SKOTIZO_ID:
		case Skotizo.AWAKENED_ALTAR_NORTH:
		case Skotizo.AWAKENED_ALTAR_SOUTH:
		case Skotizo.AWAKENED_ALTAR_WEST:
		case Skotizo.AWAKENED_ALTAR_EAST:
		case Skotizo.REANIMATED_DEMON:
		case Skotizo.DARK_ANKOU:
			return true;
		}
		return false;
	}
	public static boolean isFightCaveNpc(int i) {
		switch (npcs[i].npcType) {
		case 2627:
		case 2630:
		case 2631:
		case 2741:
		case 2743:
		case 2745:
			return true;
		}
		return false;
	}
	public boolean isLogNpc(int i) {
		if(i <= 8366 && i >= 8349)
			return true;
		switch (npcs[i].npcType) {
		case 2215:
		case 6247:
		case 6222:
		case 6203:
		case 2025:
		case 2026:
		case 2027:
		case 2028:
		case 2029:
		case 2030:
		case 50:
		case 1160:
		case 3200:
		case 3340:
		case 8133:
		case 2881:
		case 2882:
		case 2883:
			return true;
		}
		return false;
	}
	/**
	 * Summon npc, barrows, etc
	 **/
	public NPC spawnNpc(final Player c, int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence,
			boolean attackPlayer, boolean headIcon) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc");
			return null; // no free slot found
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		final NPC newNPC = new NPC(slot, npcType, definition);
		//System.out.println("Cannot find any available slots to spawn npc into + npchandler @spawnNpc");
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		newNPC.getHealth().setAmount(HP);
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = c.getIndex();
		if (headIcon)
			c.getPA().drawHeadicon(1, slot, 0, 0);
		if (attackPlayer) {
			newNPC.underAttack = true;
			newNPC.killerId = c.getIndex();
			c.underAttackBy = slot;
			c.underAttackBy2 = slot;
		}
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
		if (newNPC.npcType == 1605) {
			newNPC.forceChat("You must prove yourself... now!");
			newNPC.gfx100(86);
		}
		if (newNPC.npcType == 1606) {
			newNPC.forceChat("This is only the beginning, you can't beat me!");
			newNPC.gfx100(86);
		}
		if (newNPC.npcType == 1607) {
			newNPC.forceChat("Foolish mortal, I am unstoppable.");
		}
		if (newNPC.npcType == 1608) {
			newNPC.forceChat("Now you feel it... The dark energy.");
		}
		if (newNPC.npcType == 1609) {
			newNPC.forceChat("Aaaaaaaarrgghhhh! The power!");
		}
		return newNPC;
	}

	public NPC spawnNpc2(int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null; // no free slot found
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		newNPC.getHealth().setAmount(HP);
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.setNoRespawn(true);
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
		return newNPC;
	}
	public static Servant spawnNpc(Player c, int npcType, int x, int y,
			int heightLevel) {
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return null;
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		if (definition != null) {
			Servant newNPC = new Servant(slot, npcType, Butlers.forId(npcType).getInventory(), definition);
			newNPC.setX(x);
			newNPC.setY(y);
			newNPC.makeX = x;
			newNPC.makeY = y;
			newNPC.setHeight(heightLevel);
			newNPC.walkingType = 1;
			newNPC.summonedBy = c.getId();
			npcs[slot] = newNPC;
			if (!activeIndices.contains(slot)) {
				activeIndices.add(slot);
			}
			if(newNPC.walkingType == 1){
				//addRandomWalkEvent(slot);
			}
			return newNPC;
		} else
		return null;
	}
	public void spawnNpc3(Player player, int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer, boolean headIcon, boolean summonFollow) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}
		if(slot == -1) {
			//Misc.println("No Free Slot");
			return;		// no free slot found
		}
		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.setHeight(heightLevel);
		newNPC.walkingType = WalkingType;
		//newNPC.getHealth().setAmount(HP);
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.spawnedBy = player.getIndex();
		newNPC.facePlayer(player.getIndex());
		if(headIcon) 
			player.getPA().drawHeadicon(1, slot, 0, 0);
		if (summonFollow) {
			newNPC.summoner = true;
			newNPC.summonedBy = player.getIndex();
			player.summonId = npcType;
			player.hasNpc = true;
		}
		if(attackPlayer) {
			newNPC.underAttack = true;
			newNPC.killerId = player.getIndex();
		}
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
	}
	
	public int summonItemId(int itemId) {
		if(itemId == 1555) return 761;
		if(itemId == 1556) return 762;
		if(itemId == 1557) return 763;
		if(itemId == 1558) return 764;
		if(itemId == 1559) return 765;
		if(itemId == 1560) return 766;
		if(itemId == 1561) return 768;
		if(itemId == 1562) return 769;
		if(itemId == 1563) return 770;
		if(itemId == 1564) return 771;
		if(itemId == 1565) return 772;
		if(itemId == 1566) return 773;
		if(itemId == 7585) return 3507;
		if(itemId == 7584) return 3506;
		if(itemId == 7583) return 3505;
		if(itemId == 22746) return 8492; //pet hydra
		if(itemId == 11995) return 2055; //ele
		if(itemId == 13178) return 6637; //Callisto
		if(itemId == 13247) return 6630; //Hellpuppy
		if(itemId == 12643) return 6626; //dag sup
		if(itemId == 12644) return 6627; //dag prime
		if(itemId == 12645) return 6630; //dag rex
		if(itemId == 12646) return 6635; //baby mole
		if(itemId == 12647) return 6637; //kalphite princess
		if(itemId == 12648) return 6639; //pet smoke devil
		if(itemId == 12649) return 6631; //pet kree'arra
		if(itemId == 12650) return 6632; //bandos
		if(itemId == 12651) return 6633; //sara
		if(itemId == 12652) return 6634; //kril
		if(itemId == 12653) return 6636; //prince
		if(itemId == 12655) return 6640; //pet kraken
		if(itemId == 19730) return 6296; //bloodhound
		if(itemId == 12816) return 388; //dark core
		return 0;
	}
			public int[] zamorak = {
				6218, 6257, 6256, 6255, 6229, 6230, 6231,
				6270, 6216, 6214, 6211, 6221, 6276, 6277,
				6219
			};

			public int[] saradomin = {
				6257, 6256, 6259, 6258, 6254, 6255, 6229, 6230, 
				6231, 6221, 6276, 6277, 6219
			};

			public int[] armadyl = { 
				6246, 6233, 6257, 6256, 6255, 6229, 6230, 6231,
				6232, 6234, 6235, 6236, 6237, 6238, 6239, 6240,
				6241, 6242, 6243, 6246, 6244, 6221, 6276, 6277,
				6219
			};

			public int[] bandos = {
				6269, 6210, 6212, 6257, 6256, 6255, 6229, 6230, 
				6231, 6268, 6274, 6283, 6282, 6275, 6221, 6273,
				6276, 6272, 6213, 6277, 6219
				
			};

			/**
			 * Ice demon variables
			 */
			NPC ICE_DEMON = NPCHandler.getNpc(7584);

			/**
			 * Skeletal mystics
			 */
			NPC SKELE_MYSTIC_ONE = NPCHandler.getNpc(7604);
			NPC SKELE_MYSTIC_TWO = NPCHandler.getNpc(7605);
			NPC SKELE_MYSTIC_THREE = NPCHandler.getNpc(7606);

			/**
			 * Glod variables
			 */
			public static String glodAttack = "MELEE";
			public static Boolean glodWalking = false;
			NPC GLOD = NPCHandler.getNpc(5219);
			
			/**
			 * Tarn variables
			 */
			public static String TarnAttack = "MAGIC";
			public static String TarnAttack2 = "MELEE";
			public static Boolean TarnWalking = false;
			NPC TARN = NPCHandler.getNpc(6477);
			public static String queenAttack = "MAGIC";
			public static Boolean queenWalking = false;
			NPC QUEEN = NPCHandler.getNpc(4922);
			NPC GRAARDOR = NPCHandler.getNpc(5462);
			public void appendKillCount(int i) {
				Player c = (Player)World.getWorld().getPlayerHandler().players[npcs[i].killedBy];
				if(c != null) {
					for(int kl = 0; kl < zamorak.length; kl++) {
						if(npcs[i].npcType == zamorak[kl]) {
							c.zammyKC++;
						}
					}
					for(int kl = 0; kl < saradomin.length; kl++) {
						if(npcs[i].npcType == saradomin[kl]) {
							c.saraKC++;
						}
					}
					for(int kl = 0; kl < armadyl.length; kl++) {
						if(npcs[i].npcType == armadyl[kl]) {
							c.armadylKC++;
						}
					}
					for(int kl = 0; kl < bandos.length; kl++) {
						if(npcs[i].npcType == bandos[kl]) {
							c.bandosKC++;
						}
					}
					/*String logName = npcIdToLogName.get(npcs[i].npcType);
					if (logName != null) {
					    c.getCollectionLog().addKillCount(logName);
					}*/

				}	
			}
			// Somewhere central, like CollectionLogRegistry or a new LogNpcMapper class
			private static final Map<Integer, String> npcIdToLogName = new HashMap<>();

			static {
			    // General Graardor
			    npcIdToLogName.put(2215, "GeneralGraardor"); // boss

			    // Commander Zilyana
			    npcIdToLogName.put(2205, "CommanderZilyana");

			    // K'ril Tsutsaroth
			    npcIdToLogName.put(3129, "K'riltsutsaroth");

			    // Add others:
			    npcIdToLogName.put(7286, "Skotizo");
			    npcIdToLogName.put(2042, "Zulrah");
			    npcIdToLogName.put(3127, "fightCaves");
			    npcIdToLogName.put(494, "Kraken");

			    // Add all remaining mappings similarly...
			}

			public static int getWalkRadius(int i) {
				switch(i) {
					default:
					return 5;
				}
			}
	/**
	 * Attack animations
	 * 
	 * @param i
	 *            the npc to perform the animation.
	 * @return the animation to be performed.
	 */
	public static int getAttackEmote(int i) {
		return AttackAnimation.handleEmote(i);
	}
	public static int getAttackSound(int i) {
		return Sound.handleAttackSound(i);
	}
	/**
	 * Death animations
	 * 
	 * @param i
	 *            the npc to perform the animation.
	 * @return the animation to be performed.
	 */
	public int getDeadEmote(int i) {
		return DeathAnimation.handleEmote(i);
	}
	public static int getDeathSound(int i) {
		return Sound.handleDeathSound(i);
	}
	public static int getBlockEmote(int i) {
		return BlockAnimation.handleEmote(i);
	}
	/**
	 * Attack delays
	 **/
	public int getNpcDelay(int i) {
		switch (npcs[i].npcType) {
		case 5086:
		case 5087://dark wizard lvl 7
		case 5088://dark wizard lvl 20
			return 6;
		case 499:
			return 4;
		case 498:
			return 7;
		case 6611:
		case 6612:
			return npcs[i].attackType1 == CombatType.RANGE ? 6 : 5;
		case 319:
			return npcs[i].attackType1 == CombatType.RANGE ? 7 : 6;
		case 7554:
			return npcs[i].attackType1 == CombatType.RANGE ? 4 : 6;
		case 2025:
		case 2028:
		case 963:
		case 965:
			return 7;
		case 6475:
		case 6477:
			return 4;
		case 8030:
		case 8031:
			return 5;
		case 3127:
			case 7700:
			return 8;
		case 2205:
			return 4;
		/*case Brother.AHRIM:
			return 6;
		case Brother.DHAROK:
			return 7;
		case Brother.GUTHAN:
			return 5;
		case Brother.KARIL:
			return 4;
		case Brother.TORAG:
			return 5;
		case Brother.VERAC:
			return 5;*/
		case 7261:
			return 6;
		case 3167:
			return 6;
		// saradomin gw boss
		case 7597:
		case 7547:
			return 2;
		case 3162:
			return 7;
		case 3269:
		case 3271:
		case 3272:
			return 4;
		default:
			return 4;
		}
	}

	/**
	 * Hit delays
	 **/
	public int getHitDelay(int i) {
		switch (npcs[i].npcType) {
		case 5086:
		case 5087:
		case 5088:
			return 5;
		case 7706:
		return 7;
		case 1605:
		case 1606:
		case 1607:
		case 1608:
		case 1609:
		case 499:
			return 4;
		case 7261:
			return 3;
		case 498:
			return 4;
		case 6611:
		case 6612:
			return npcs[i].attackType1 == CombatType.MAGE ? 3 : 2;
		case 1672:
		case 1675:
		case 1046:
		case 1049:
		case 6610:
		case 2265:
		case 2266:
		case 2054:
		case 2892:
		case 2894:
		case 3125:
		case 3121:
		case 2167:
		case 2209:
		case 2211:
		case 2218:
		case 2242:
		case 2244:
		case 3160:
		case 3163:
		case 3167:
		case 3174:
		case 2028:
			return 3;
		case 2212:
		case 2217:
		case 3161:
		case 3162:
		case 3164:
		case 3168:
		case 6914: // Lizardman, Lizardman brute
		case 6915:
		case 6916:
		case 6917:
		case 6918:
		case 6919:
		case 2025:
			return 4;
		case 3127:
			case 7700:
			if (npcs[i].attackType1 == CombatType.RANGE || npcs[i].attackType1 == CombatType.MAGE) {
				return 5;
			} else {
				return 2;
			}

		default:
			return 2;
		}
	}

	/**
	 * Npc respawn time
	 **/
		public int getRespawnTime(int i) {
		switch (npcs[i].npcType) {
		case 6600:
		case 6601:
		case 6602:
		case 320:
		case 1049:
		case 6617:
		case 3118:
		case 3120:
		case 6768:
		case 5862:
		case 5054:
		case 2402:
		case 2401:
		case 2400:
		case 2399:
		case 5916:
		case 7604:
		case 7605:
		case 7606:
		case 7585:
		case 5129:
		case 4922:
		case 7563:
		case 7573:
		case 7544:
		case 7566:
		case 7559:
			case 7553:
			case 7554:
			case 7555:
		case 7560://Trials of Xeric
		case 7527:
		case 7528:
		case 7529:
		case 8679://Colossal Chicken
		case 6014://Void Knight Champion
			return -1;
		case 5001://anti-santa
		case 6477:
		case 5462:
		case 7858:
		case 7859:
		case 7860:
		case 3383:
			return -1;
			
		case 3833:
		case 3845:
			return 300;
			
		case 3407:
			return 600;
			
		case 3842:
			return 180;

		case 963:
		case 965:
			return 10;
			
		case 6475:
			return 25;
			
			/* hydra */
		case 8609:
			return 40;

		case 6618:
		case 6619:
		case 319:
		case 5890:
			return 30;

		case 1046:
		case 465:
			return 60;

		case 6609:
		case 2265:
		case 2266:
		case 2267:
			return 70;

		case 6611:
		case 6612:
		case 492:
			return 90;

		case 2562:
		case 2563:
		case 2564:
		case 2205:
		case 2206:
		case 2207:
		case 2208:
		case 2215:
		case 2216:
		case 2217:
		case 2218:
		case 3129:
		case 3130:
		case 3131:
		case 3132:
		case 3162:
		case 3163:
		case 3164:
		case 3165:
		case 1641:
		case 1642:
			return 100;

		case 1643:
			return 180;

		case 1654:
			return 250;

		case 3777:
		case 3778:
		case 3779:
		case 3780:
		case 7302:
			return 500;
		default:
			return 25;
		}
	}
		public boolean isTileOccupied(int x, int y, int height, NPC self) {
			for (NPC other : npcs) {
				if (other == null || other == self || other.isDead)
					continue;
				if (other.getX() == x && other.getY() == y && other.getHeight() == height) {
					return true;
				}
			}
			return false;
		}
		public boolean isAreaOccupied(int x, int y, int height, NPC self) {
		    int selfSize = self.getSize(); // Assuming you have a getSize() method returning npc size (1, 2, 3, etc.)
		    
		    for (NPC other : npcs) {
		        if (other == null || other == self || other.isDead)
		            continue;

		        if (other.getHeight() != height)
		            continue;

		        int otherSize = other.getSize();
		        
		        // Use AABB (Axis-Aligned Bounding Box) logic for efficiency
		        // This checks if the two square areas overlap anywhere
		        if (x < other.getX() + otherSize &&
		            x + selfSize > other.getX() &&
		            y < other.getY() + otherSize &&
		            y + selfSize > other.getY()) {
		            return true;
		        }
		    }
		    return false;
		}
	public void newNPC(int npcType, int x, int y, int heightLevel,
			int WalkingType, int HP, int maxHit, int attack, int defence) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 1; i < maxNPCs; i++) {
			if (npcs[i] == null) {
				slot = i;
				break;
			}
		}

		if (slot == -1)
			return; // no free slot found

		NPCDefinitions definition = NPCDefinitions.get(npcType);
		NPC newNPC = new NPC(slot, npcType, definition);
		newNPC.setX(x);
		newNPC.setY(y);
		newNPC.makeX = x;
		newNPC.makeY = y;
		newNPC.heightLevel = heightLevel;
		newNPC.walkingType = WalkingType;
		//newNPC.getHealth().setAmount(HP);
		newNPC.getHealth().setMaximum(HP);
		newNPC.getHealth().reset();
		newNPC.maxHit = maxHit;
		newNPC.attack = attack;
		newNPC.defence = defence;
		newNPC.resetDamageTaken();
		npcs[slot] = newNPC;
		if (!activeIndices.contains(slot)) {
			activeIndices.add(slot);
		}
	}

	public void newNPCList(int npcType, String npcName, int combat, int HP) {
		NPCDefinitions newNPCList = new NPCDefinitions(npcType);
		newNPCList.setNpcName(npcName.replaceAll("_", " ").toLowerCase());
		newNPCList.setNpcCombat(combat);
		newNPCList.setNpcHealth(HP);
		NPCDefinitions.getDefinitions()[npcType] = newNPCList;
	}
	private boolean isImpling(NPC npc) {
		return (npc.npcType >= 1635 && npc.npcType <= 1643) || npc.npcType == 1654 || npc.npcType == 7302;
	}
	private void handleRoamingImpling(NPC npc) {
		// Reached target, or doesn't have one
		if (npc.getRoamTargetX() == -1 ||
				(npc.getX() == npc.getRoamTargetX() && npc.getY() == npc.getRoamTargetY())) {

			// OSRS Implings drift in localized zones.
			// Pick a random tile within a 20-tile radius of their current spot.
			int newX = npc.getX() + (Misc.random(40) - 20);
			int newY = npc.getY() + (Misc.random(40) - 20);
			npc.setRoamTarget(newX, newY);
			return;
		}

		int bestDir = -1;
		int bestDist = Integer.MAX_VALUE;

		// Greedy pathfinding: Find the direction that gets us closest to the target
		for (int dir = 0; dir < 8; dir++) {
			int dx = NPCClipping.DIR[dir][0];
			int dy = NPCClipping.DIR[dir][1];
			int stepX = npc.getX() + dx;
			int stepY = npc.getY() + dy;

			// Check if the Impling can fly or walk there
			if (!canImplingFlyOver(npc, dir)) {
				continue;
			}

			int dist = Math.abs(stepX - npc.getRoamTargetX()) + Math.abs(stepY - npc.getRoamTargetY());

			if (dist < bestDist) {
				bestDist = dist;
				bestDir = dir;
			}
		}

		if (bestDir != -1) {
			// Success: Apply the step natively to the engine queue
			npc.moveX = NPCClipping.DIR[bestDir][0];
			npc.moveY = NPCClipping.DIR[bestDir][1];
			npc.getNextNPCMovement();
			npc.updateRequired = true;
		} else {
			// Stuck: Abandon target so it picks a completely new random direction next tick
			npc.setRoamTarget(-1, -1);
		}
	}

	/**
	 * Simulates OSRS flying mechanics using Projectile Collision.
	 */
	private boolean canImplingFlyOver(NPC npc, int dir) {
		int x = npc.getX();
		int y = npc.getY();
		int z = npc.getHeight();

		// 1. Check normal walking first (if the path is perfectly clear, take it)
		if (NPCDumbPathFinder.canMoveTo(npc, dir)) {
			return true;
		}

		// 2. THE FLYING HACK: If walking is blocked, check if we can shoot a projectile over it!
		// This natively allows Implings to drift over water, low walls, tables, and fences.
		return Region.canShoot(x, y, z, dir);
	}

	private List<Location> generateRandomWalkPath(NPC npc, int maxSteps) {
	    List<Location> path = new ArrayList<>();
	    int height = npc.getHeight();

	    int destX = npc.makeX + (Misc.random(Config.NPC_RANDOM_WALK_DISTANCE * 2) - Config.NPC_RANDOM_WALK_DISTANCE);
	    int destY = npc.makeY + (Misc.random(Config.NPC_RANDOM_WALK_DISTANCE * 2) - Config.NPC_RANDOM_WALK_DISTANCE);

	    int currentX = npc.getX();
	    int currentY = npc.getY();

	    for (int i = 0; i < maxSteps; i++) {
	        if (currentX == destX && currentY == destY) {
	            break; 
	        }

	        int dx = Integer.compare(destX, currentX);
	        int dy = Integer.compare(destY, currentY);
	        
	        int dir = NPCClipping.getDirection(currentX, currentY, currentX + dx, currentY + dy);

	        // FIX: Pass currentX and currentY so it simulates collision accurately!
	        if (dir != -1 && NPCDumbPathFinder.canMoveTo(npc, currentX, currentY, dir)) {
	            currentX += dx;
	            currentY += dy;
	            path.add(new Location(currentX, currentY, height));
	        } else {
	            break;
	        }
	    }

	    return path;
	}

	public void removeNpc(NPC npc) {
		if (npc == null) return;
		int index = npc.getIndex();
		if (index < 0 || index >= maxNPCs) return;

		npcs[index] = null;
		activeIndices.remove(Integer.valueOf(index)); // Remove index layer track references

		npc.setX(0);
		npc.setY(0);
		npc.setHeight(0);
		npc.walkingType = 0;
		npc.payDirtPath = null;
		npc.updateRequired = true;
	}
	public static void triggerNPCBlock(Player c, int i) {
	    NPC npc = npcs[i];
	    if (npc == null || npc.isDead) return;

	    // Filter out NPCs that shouldn't block (Zulrah, etc.)
	    if (npc.npcType != 2042 && npc.npcType != 2043 && npc.npcType != 2044 && npc.npcType != 3127 && npc.npcType != 319) {
	        int defAnim = c.getCombat().npcDefenceAnim(i);
	        int defSound = c.getCombat().npcDefenceSound(i);
	        
	        if (defAnim != -1) {
	            startAnimation(defAnim, i);
	        }
	        if (defSound > 0) {
	            // Volume 9 is OSRS standard, 0 delay for instant impact sound
	            c.getPA().sendNPCSound(defSound, 0, 0, 9);
	        }
	    }
	}


	public void process() {
		// FIX: Loop backward! This allows nested methods (like processDeath) to safely
		// remove an NPC from the activeIndices list without skipping the next NPC in line.
		for (int i = activeIndices.size() - 1; i >= 0; i--) {
			int index = activeIndices.get(i);
			NPC npc = npcs[index];

			if (npc == null || index == -1) {
				activeIndices.remove(i);
				continue;
			}

			npc.clearUpdateFlags();

			// Core logic routing executions

			if(npc != null) {
				processNpcTimers(npc, index);
				processSummons(npc);
				processCustomNpcLogic(npc, index);
				processBossMechanics(npc, index);
				processCombatAndAggression(npc, index);
				processMovement(npc, index);
			}

			if (npc.isDead) {
				processDeath(npc, index);
			}
		}
	}
	// ─── 1. TIMERS ─────────────────────────────────────────────────────────────
	private void processNpcTimers(NPC npc, int index) {
		if (npc.actionTimer > 0) npc.actionTimer--;
		if (npc.freezeTimer > 0) npc.freezeTimer--;
		if (npc.attackTimer > 0) npc.attackTimer--;
		if (npc.targetingDelay > 0) npc.targetingDelay--;

		if (npc.hitDelayTimer > 0) {
			npc.hitDelayTimer--;
			if (npc.hitDelayTimer == 0) {
				applyDamage(index);
			}
		}
	}

	// ─── 2. SUMMONING ──────────────────────────────────────────────────────────
	private void processSummons(NPC npc) {
		if (npc.summoner) {
			Player slaveOwner = PlayerHandler.players[npc.summonedBy];
			if (slaveOwner == null) {
				npc.setX(0);
				npc.setY(0);
			} else if (slaveOwner.hasNpc && (!slaveOwner.goodDistance(npc.getX(), npc.getY(), slaveOwner.getX(), slaveOwner.getY(), 15) || slaveOwner.getHeight() != npc.getHeight())) {
				npc.setX(slaveOwner.getX());
				npc.setY(slaveOwner.getY());
				npc.setHeight(slaveOwner.getHeight());
			}
		}

		if (npc.spawnedBy > 0) {
			Player spawnedBy = PlayerHandler.players[npc.spawnedBy];
			if (spawnedBy == null || spawnedBy.getHeight() != npc.getHeight() || spawnedBy.respawnTimer > 0
					|| !spawnedBy.goodDistance(npc.getX(), npc.getY(), spawnedBy.getX(), spawnedBy.getY(),
					isFightCaveNpc(npc.getIndex()) ? 80 : isSkotizoNpc(npc.getIndex()) ? 60 : 20)) {
				removeNpc(npc);
			}
		}
	}

	// ─── 3. CUSTOM NPC LOGIC ───────────────────────────────────────────────────
	private void processCustomNpcLogic(NPC npc, int index) {
		if (npc.npcType == 279 && Misc.random(1, 10) == 4) {
			npc.updateRequired = true;
			npc.startAnimation(6865);
			npc.forceChat(TOWN_CRIER_SHOUTS[Misc.random(TOWN_CRIER_SHOUTS.length - 1)]);
		}

		if ((npc.npcType == 43 || npc.npcType == 1765) && Misc.random(35) == 4) {
			npc.forceChat("Baa!");
		}

		if (npc.npcType == 4086 && Misc.random(20) == 4) {
			npc.startAnimation(1209);
			npc.forceChat("March!");
		}

		if (npc.npcType == 2462 && Misc.random(20) == 0) {
			npc.forceChat(SHANOMI_QUOTES[Misc.random(SHANOMI_QUOTES.length - 1)]);
		}

		if (npc.npcType == 6645) {
			npc.animNumber = 7139;
			npc.animUpdateRequired = true;
		}

		MotherlodeMine.handleMiningNPC(npc);
		if (npc.npcType == 6564 && npc.payDirtPath != null && !npc.payDirtPath.isEmpty()) {
			if (!MotherlodeMine.waterWheelsActive()) {
				npc.paused = true;
				npc.moveX = 0;
				npc.moveY = 0;
				npc.getNextNPCMovement();
				npc.updateRequired = true;
				return;
			} else {
				npc.paused = false;
				if (MotherlodeMine.rightWheelActive) {
					npc.getNextNPCMovement();
					npc.updateRequired = true;
				}

				Location target = npc.payDirtPath.get(npc.payDirtTargetIndex);
				npc.moveX = Integer.signum(target.getX() - npc.getX());
				npc.moveY = Integer.signum(target.getY() - npc.getY());
				npc.getNextNPCMovement();
				npc.updateRequired = true;

				if (npc.getX() == target.getX() && npc.getY() == target.getY()) {
					npc.payDirtTargetIndex++;
					if (npc.payDirtTargetIndex >= npc.payDirtPath.size()) {
						MotherlodeMine.addToPlayerSack(npc.payDirtOwnerId, npc);
						npc.payDirtPending.clear();
						removeNpc(npc);
					}
				}
			}
		}

		if (server.model.players.skills.hunter.HunterAI.isHunterNpc(npc.npcType)) {
			server.model.players.skills.hunter.HunterAI.processNpc(npc);
		}

		if (npc.npcType == 4084) {
			if (Misc.random(10) == 0 && !npc.hasWalkSteps()) {
				int destX = npc.makeX - 2 + Misc.random(5);
				int destY = npc.makeY - 2 + Misc.random(5);
				npc.turnNpc(destX, destY);
				NPCDumbPathFinder.walkTowards(npc, destX, destY);
				npc.forceChat(DRILL_SHOUTS[Misc.random(DRILL_SHOUTS.length - 1)]);
			}
		}
	}

	// ─── 4. BOSS MECHANICS & TRANSFORMS ────────────────────────────────────────
	private void processBossMechanics(NPC npc, int index) {
		if (npc.npcType == 6615) {
			if (npc.walkingHome) npc.getHealth().setAmount(200);
			Scorpia.spawnHealer();
		}
		if (npc.npcType == 319) {
			if (npc.walkingHome) npc.getHealth().setAmount(2000);
			CorporealBeast.checkCore();
		}

		if (npc.npcType >= 2042 && npc.npcType <= 2044 && npc.getHealth().getAmount() > 0) {
			Player player = PlayerHandler.players[npc.spawnedBy];
			if (player != null && player.getZulrahEvent().getNpc() != null && npc.equals(player.getZulrahEvent().getNpc())) {
				int stage = player.getZulrahEvent().getStage();
				if (npc.npcType == 2042 && (stage == 0 || stage == 1 || stage == 4 || (stage == 9 && npc.totalAttacks >= 20) || (stage == 11 && npc.totalAttacks >= 5))) return;
				if (npc.npcType == 2044 && ((stage == 5 || stage == 8) && npc.totalAttacks >= 5)) return;
			}
		}

		if (npc.npcType == 8026 || npc.npcType == 8027 || npc.npcType == 7413) npc.setFacePlayer(false);
		if (npc.npcType == 8028) npc.setFacePlayer(true);

		if (npc.npcType == 7207 || npc.npcType == 5936) {
			for (Player c : PlayerHandler.players) {
				if (c != null && c.withinDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), 2)) {
					npc.requestTransform(npc.npcType == 7207 ? 7206 : 5935);
					npc.killerId = c.getIndex();
					npc.underAttack = true;
					npc.underAttackBy = npc.killerId;
					npc.walkingType = 1;
					break;
				}
			}
		}
	}

	// ─── 5. COMBAT & AGGRESSION ────────────────────────────────────────────────
	private void processCombatAndAggression(NPC npc, int index) {
		if (isAggressive(index, false) && !npc.underAttack && npc.killerId <= 0 && !npc.isDead
				&& !switchesAttackers(index) && npc.inMulti() && !Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)
				&& !Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR) && !Boundary.isIn(npc, Boundary.CATACOMBS)) {

			Player closestPlayer = null;
			int closestDistance = 15;
			for (Player player : PlayerHandler.players) {
				if (player == null) continue;
				int distance = Misc.distanceToPoint(npc.getX(), npc.getY(), player.getX(), player.getY());
				if (distance < closestDistance && distance <= distanceRequired(index) + followDistance(index)) {
					closestDistance = distance;
					closestPlayer = player;
				}
			}
			if (closestPlayer != null) {
				npc.killerId = closestPlayer.getIndex();
				closestPlayer.underAttackBy = npc.getIndex();
				closestPlayer.underAttackBy2 = npc.getIndex();
			}
		} else if (isAggressive(index, false) && !npc.underAttack && !npc.isDead && switchesAttackers(index)) {
			if (System.currentTimeMillis() - npc.lastRandomlySelectedPlayer > 10000) {
				int player = getCloseRandomPlayer(index);
				if (player > 0) {
					npc.killerId = player;
					PlayerHandler.players[player].underAttackBy = index;
					PlayerHandler.players[player].underAttackBy2 = index;
					npc.lastRandomlySelectedPlayer = System.currentTimeMillis();
				}
			}
		}

		if (System.currentTimeMillis() - npc.lastDamageTaken > 5000 && !npc.underAttack) {
			npc.underAttackBy = 0;
			npc.lastRandomlySelectedPlayer = 0;
			npc.underAttack = false;
			npc.randomWalk = true;
		}

		CombatScript script = npc.getCombatScript();
		if (script != null) {
			script.process(npc, PlayerHandler.players[npc.killerId]);
		}

		if ((npc.killerId > 0 || npc.underAttack) && !npc.walkingHome && retaliates(npc.npcType) && !npc.isDead) {
			Player c = PlayerHandler.players[npc.killerId];
			if (c != null) {
				if (!npc.summoner) {
					if (script == null)
						followPlayer(index, c.getIndex());
					if (npc.npcType == 8610)
						npc.requestTransform(8611);
					if (npc.attackTimer == 0)
						attackPlayer(c, index);
				} else {
					if (c.getX() == npc.getX() && c.getY() == npc.getY()) {
						stepAway(index);
						npc.randomWalk = false;
						npc.faceEntity(c.getIndex());
					} else {
						followPlayer(index, c.getIndex());
					}
				}
			} else {
				if (npc.npcType == 8611) npc.requestTransform(8610);
				npc.killerId = 0;
				npc.lastRandomlySelectedPlayer = 0;
				npc.underAttack = false;
				npc.faceEntity(0);
			}
		}
	}

	// ─── 6. MOVEMENT ───────────────────────────────────────────────────────────
	private void processMovement(NPC npc, int index) {
		if (npc.lastX != npc.getX() || npc.lastY != npc.getY()) {
			npc.lastX = npc.getX();
			npc.lastY = npc.getY();
		}

		if ((!npc.underAttack || npc.walkingHome) && npc.randomWalk && !npc.isDead) {
			npc.faceEntity(0);
			npc.killerId = 0;

			if (npc.spawnedBy == 0) {
				if ((npc.getX() > npc.makeX + Config.NPC_RANDOM_WALK_DISTANCE)
						|| (npc.getX() < npc.makeX - Config.NPC_RANDOM_WALK_DISTANCE)
						|| (npc.getY() > npc.makeY + Config.NPC_RANDOM_WALK_DISTANCE)
						|| (npc.getY() < npc.makeY - Config.NPC_RANDOM_WALK_DISTANCE)
						&& npc.npcType != 1635 && npc.npcType != 1636 && npc.npcType != 1637
						&& npc.npcType != 1638 && npc.npcType != 1639 && npc.npcType != 1640
						&& npc.npcType != 1641 && npc.npcType != 1642 && npc.npcType != 1643
						&& npc.npcType != 1654 && npc.npcType != 7302) {
					npc.walkingHome = true;
				}
			}

			if (npc.walkingType >= 0 && npc.faceToUpdateRequired) {
				switch(npc.walkingType) {
					case 5: npc.turnNpc(npc.getX()-1, npc.getY()); break;
					case 4: npc.turnNpc(npc.getX()+1, npc.getY()); break;
					case 3: npc.turnNpc(npc.getX(), npc.getY()-1); break;
					case 2: npc.turnNpc(npc.getX(), npc.getY()+1); break;
				}
			}

			if (npc.walkingType == 1 && !npc.underAttack && !npc.walkingHome && npc.npcType != 6564 && npc.npcType != 6566) {
				if (npc.hasWalkSteps()) {
					Location step = npc.pollWalkStep();
					int dx = step.getX() - npc.getX();
					int dy = step.getY() - npc.getY();
					int dir = NPCClipping.getDirection(npc.getX(), npc.getY(), step.getX(), step.getY());

					if (NPCDumbPathFinder.canMoveTo(npc, dir) && !isAreaOccupied(step.getX(), step.getY(), npc.getHeight(), npc)) {
						npc.moveX = dx;
						npc.moveY = dy;
						npc.getNextNPCMovement();
						npc.updateRequired = true;
						return;
					} else {
						npc.getWalkQueue().clear();
					}
				} else if (System.currentTimeMillis() > npc.getWalkPauseTime()) {
					if (isImpling(npc) && Boundary.isIn(npc, Boundary.PURO_PURO)) {
						handleRoamingImpling(npc);
					} else {
						int walkLength = 3 + Misc.random(4);
						List<Location> randomPath = generateRandomWalkPath(npc, walkLength);
						if (!randomPath.isEmpty()) {
							npc.setWalkQueue(randomPath);
							long timeSpentWalking = randomPath.size() * 600L;
							long pauseDelay = java.util.concurrent.TimeUnit.SECONDS.toMillis(3 + Misc.random(4));
							npc.setWalkPauseTime(System.currentTimeMillis() + timeSpentWalking + pauseDelay);
						} else {
							npc.setWalkPauseTime(System.currentTimeMillis() + 2000);
						}
					}
				}
			}
		}

		if (!npc.neverWalkHome && npc.walkingHome) {
			if (!npc.isDead) {
				NPCDumbPathFinder.walkTowards(npc, npc.makeX, npc.makeY);
				if (npc.moveX == 0 && npc.moveY == 0) {
					npc.teleport(npc.makeX, npc.makeY, npc.getHeight());
				}
				if (npc.getX() == npc.makeX && npc.getY() == npc.makeY) {
					npc.walkingHome = false;
				}
			} else {
				npc.walkingHome = false;
			}
		}
	}

	// ─── 7. DEATH ──────────────────────────────────────────────────────────────
	private void processDeath(NPC npc, int index) {
		Player player = PlayerHandler.players[npc.spawnedBy];

		if (npc.actionTimer == 0 && !npc.applyDead && !npc.needRespawn) {

			if (npc.npcType == 6618)
				npc.forceChat("Ow!");


			// Inside NPCHandler.java -> processDeath (or where an NPC's HP hits 0)
			if (npc.npcType == 241) { // Sir Mordred
				Player p = server.model.players.PlayerHandler.players[npc.killedBy];
				if (p != null && p.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.DEFEATED_MORDRED) {

					// Prevent Mordred from dying/dropping loot immediately
					npc.isDead = false;
					npc.HP = npc.MaxHP;
					npc.underAttack = false;
					p.underAttackBy = 0;

					// Spawn Morgan Le Faye (NPC 242)
					NPC morgan = spawnNpc(p, 242, npc.getX() + 1, npc.getY(), npc.heightLevel, 0, 0, 0, 0, 0, false, false);
					if (morgan != null) {
						morgan.forceChat("STOP! Please... spare my son.");
						morgan.facePlayer(p.getIndex());
						// Force open Morgan's dialogue
						DialogueService.open(p, 242, 85900);
					}
					return; // Stop the rest of the death method
				}
			}

			if (npc.npcType == 1368 || npc.npcType == 1366) {
				Player killer = PlayerHandler.players[npc.killerId];
				if (killer != null) {
					if (killer.questStages[EleWorkShop.QUEST_ID] == EleWorkShop.FURNACE_HEATED) {
						killer.questStages[EleWorkShop.QUEST_ID] = EleWorkShop.OBTAINED_ORE;
					}
					if (killer.getItems().freeSlots() > 0) {
						killer.getItems().addItem(2892, 1);
						killer.sendMessage("You find some elemental ore amongst the rubble of the golem.");
					} else {
						World.getWorld().itemHandler.createGroundItem(killer, 2892, npc.absX, npc.absY, npc.heightLevel, 1, killer.getIndex());
						killer.sendMessage("Your inventory is full! The ore has dropped to the floor.");
					}
					npc.requestTransform(1368);
				}
			}
			if (npc.npcType == 7206) { npc.requestTransform(7207); npc.walkingType = 0; }

			if (npc.npcType == 34) {
				Player killer = PlayerHandler.players[npc.killerId];
				if (killer != null && killer.questStages[VampyreSlayer.QUEST_ID] == VampyreSlayer.GATHERING_EQUIPMENT) {
					boolean hasStake = killer.getItems().playerHasItem(VampyreSlayer.STAKE);
					boolean hasHammer = killer.getItems().playerHasItem(VampyreSlayer.HAMMER);

					if (hasStake && hasHammer) {
						killer.sendMessage("You hammer the stake into the vampyre's chest!");
						new VampyreSlayer(killer).setStage(VampyreSlayer.COMPLETED);
						new VampyreSlayer(killer).giveRewards();
					} else {
						killer.sendMessage(hasStake ? "You're unable to push the stake far enough in!" : "The vampyre seems to regenerate!");
						npc.getHealth().reset();
						npc.isDead = false;
						npc.updateRequired = true;
						return;
					}
				}
			}

			if (npc.npcType == 6611) {
				npc.requestTransform(6612);
				npc.getHealth().reset();
				npc.isDead = false;
				npc.spawnedMinions = false;
				npc.forceChat("Do it again!!");
			} else {
				if (npc.npcType == 6612) {
					npc.npcType = 6611;
					npc.spawnedMinions = false;
				}

				if (npc.npcType == 1605) {
					server.event.CycleEventHandler.getSingleton().addEvent(player, new server.event.CycleEvent() {
						@Override
						public void execute(server.event.CycleEventContainer container) {
							spawnNpc(player, 1606, 3106, 3934, 0, 1, 30, 24, 70, 60, true, true);
							container.stop();
						}
						@Override
						public void stop() {}
					}, 5);
				}

				npc.updateRequired = true;
				npc.faceEntity(0);
				Entity killer = npc.calculateKiller();

				if (npc.getCombatScript() != null) {
					npc.getCombatScript().handleDeath(npc, killer);
				}

				if (killer != null) {
					npc.killedBy = killer.getIndex();
				} else {
					npc.killedBy = npc.killerId;
				}

				Player pl = PlayerHandler.players[npc.killerId];
				if (pl != null) {
					if (npc.npcType == 1839 || npc.npcType == 1838) pl.getAD().completeAchievement("FaladorEasy", "Kill a duck in Falador Park.", 6);
					pl.getPA().sendNPCSound(getDeathSound(index), 0, 6, pl.EffectVolume);

					if (pl.getRaidSession() != null) {
						pl.getRaidSession().handleNpcDeath(npc);
					}
				}
				if (npc.npcType == 8611)
					npc.requestTransform(8610);

				if (npc.npcType == 963) {
					npc.isDead = false;
					npc.npcType = 965;
					npc.requestTransform(965);
					npc.getHealth().reset();
				} else if (npc.npcType == 6475) {
					npc.isDead = false;
					npc.npcType = 6477;
					npc.requestTransform(6477);
					npc.getHealth().reset();
				} else {
					npc.animNumber = getDeadEmote(index);
					npc.animUpdateRequired = true;
				}

				if (pl != null && pl.inNMZ) {
					server.model.minigames.NightmareZone.handleBossDeath(pl, npc);
				}

				npc.freezeTimer = 0;
				npc.applyDead = true;

				if (npc.npcType == 3118) {
					spawnNpc(3120, npc.getX(), npc.getY(), player.getHeight(), 10, 2, 15, 15, 0);
					spawnNpc(3120, npc.getX(), npc.getY() + 1, player.getHeight(), 10, 2, 15, 15, 0);
				}
				killedBarrow(index);
				npc.actionTimer = getDeathDelay(index);
				resetPlayersInCombat(index);
			}

		} else if (npc.actionTimer == 0 && npc.applyDead && !npc.needRespawn) {

			if (npc.getInstance() != null && npc.getInstance().onDeath(npc)) return;

			int killerIndex = npc.killedBy;
			npc.needRespawn = true;
			npc.actionTimer = getRespawnTime(index);
			dropItems(index);

			if (killerIndex < PlayerHandler.players.length - 1) {
				Player target = PlayerHandler.players[npc.killedBy];
				if (target != null) target.getSlayer().killTaskMonster(npc);
			}

			appendKillCount(index);
			npc.setX(npc.makeX);
			npc.setY(npc.makeY);
			npc.getHealth().reset();
			npc.animNumber = 0x328;
			npc.updateRequired = true;
			npc.animUpdateRequired = true;

			switch (npc.npcType) {
				case 7206: case 7207:
					npc.requestTransform(7207);
					npc.walkingType = 0;
					break;
				case 5935: case 5936:
						npc.requestTransform(5936);
						npc.walkingType = 0;
						npc.actionTimer = 0;
						break;
				case 3316:
					Player c = (Player) PlayerHandler.players[npcs[index].killedBy];
					if (c != null && c.tutorialProgress == 33 && c.getCombat().airSpells()) {
						c.getPA().chatbox(6180);
						c.getDH().chatboxText("", "All you need to do is move on to the mainland. Just speak", "with Terrova and he'll teleport you to Lumbridge Castle.", "", "You have almost completed the tutorial!");
						c.getPA().chatbox(6179);
						c.tutorialProgress = 34;
						c.getPA().createPlayerHints(1, 9);
					}
					break;
				case 3313:
					Player player2 = (Player) PlayerHandler.players[npcs[index].killedBy];
					if (player2 != null) {
						if (player2.tutorialProgress == 24) handleratdeath(index);
						else if (player2.tutorialProgress == 25 && player2.ratdied2) handleratdeath2(index);
					}
					break;
				case 5862:
					if (player != null && player.getCerberus() != null) player.getCerberus().end(DisposeTypes.COMPLETE);
					break;
				case Skotizo.SKOTIZO_ID:
					if (player != null && player.getSkotizo() != null) player.getSkotizo().end(DisposeTypes.COMPLETE);
					break;
				case Skotizo.AWAKENED_ALTAR_NORTH:
				case Skotizo.AWAKENED_ALTAR_SOUTH:
				case Skotizo.AWAKENED_ALTAR_WEST:
				case Skotizo.AWAKENED_ALTAR_EAST:
					if (player.getSkotizo() != null) {
						player.getSkotizo().handleAltarDeath(npc.npcType);
					}
					break;
				case Skotizo.DARK_ANKOU:
					if (player != null && player.getSkotizo() != null) player.getSkotizo().ankouSpawned = false;
					break;
				case 6615:
					Scorpia.stage = 0;
					break;
				case 319:
					CorporealBeast.stage = 0;
					break;
				case 6600:
					spawnNpc(6601, npc.getX(), npc.getY(), 0, 0, 0, 0, 0, 0);
					break;
				case 6601:
					spawnNpc(6600, npc.getX(), npc.getY(), 0, 0, 0, 0, 0, 0);
					removeNpc(npcs[index]);
					NPC golem = getNpc(6600);
					if (golem != null) golem.actionTimer = 150;
					return;
			}

		} else if (npc.actionTimer == 0 && npc.needRespawn && npc.npcType != 1739 && npc.npcType != 1740 && npc.npcType != 1741 && npc.npcType != 1742) {
			if (player != null || npc.isNoRespawn()) {
				removeNpc(npcs[index]);
			} else {
				int newType = npc.npcType;
				int newX = npc.makeX;
				int newY = npc.makeY;
				int newH = npc.getHeight();
				int newWalkingType = npc.walkingType;
				int newHealth = npc.getHealth().getMaximum();
				int newMaxHit = npc.maxHit;
				int newAttack = npc.attack;
				int newDefence = npc.defence;
				removeNpc(npcs[index]);
				newNPC(newType, newX, newY, newH, newWalkingType, newHealth, newMaxHit, newAttack, newDefence, npc.getInstance());
			}
		}
	}
   private void handleratdeath(int i) {
        final Player c = (Player) PlayerHandler.players[npcs[i].killedBy];
        if (c != null) {
            c.getPA().chatbox(6180);
            c.getDH().chatboxText( "",
                            "Pass through the gate and talk to the Combat Instructor, he",
                            "will give you your next task.", "",
                            "Well done, you've made your first kill!");
            c.getPA().chatbox(6179);
            c.getPA().drawHeadicon(1, 6, 0, 0); // draws
            // headicon to
            // combat ude
            c.tutorialProgress = 25;
        }
    }

    private void handleratdeath2(int i) {
    	Player c = (Player) PlayerHandler.players[npcs[i].killedBy];
        if (c != null) {
            c.getPA().chatbox(6180);
            c.getDH().chatboxText( "You have completed the tasks here. To move on, click on the",
                            "ladder shown. If you need to go over any of what you learnt",
                            "here, just talk to the Combat Instructor and he'll tell you what",
                            "he can.", "Moving on");
            c.getPA().chatbox(6179);
            c.tutorialProgress = 26;
            c.getPA().createObjectHints(3111, 9525, c.getHeight(), 2); // send
            // hint
            // to
            // furnace
        }
    }
	public boolean getsPulled(int i) {
		switch (npcs[i].npcType) {
		case 2216:
			if (npcs[i].firstAttacker > 0)
				return false;
			break;
		}
		return true;
	}

	public boolean multiAttacks(int i) {
		switch (npcs[i].npcType) {
			case 7554:
				return npcs[i].attackType1 == CombatType.SPECIAL || npcs[i].attackType1 == CombatType.MAGE || 
				npcs[i].attackType1 == CombatType.RANGE;
		case 6611:
		case 6612:
		case 6618:
		case 6619:
		case 319:
		case 6766:
		case 7617:
			return npcs[i].attackType1 == CombatType.SPECIAL || npcs[i].attackType1 == CombatType.MAGE;
			
		case 8609:
			return npcs[i].attackType1 == CombatType.SPECIAL;

		case 7604:
		case 7605:
		case 7606:
			return npcs[i].attackType1 == CombatType.SPECIAL;

		case 1046:
			return npcs[i].attackType1 == CombatType.MAGE
					|| npcs[i].attackType1 == CombatType.SPECIAL && Misc.random(3) == 0;
		case 6610:
			return npcs[i].attackType1 == CombatType.MAGE;
		case 2558:
			return true;
		case 2562:
		case 6477:
			if (npcs[i].attackType1 == CombatType.MAGE)
				return true;
		case 2215:
		case 5462:
			return npcs[i].attackType1 == CombatType.RANGE;
		case 3162:
			return npcs[i].attackType1 == CombatType.MAGE;
		case 963:
		case 965:
			return npcs[i].attackType1 == CombatType.MAGE || npcs[i].attackType1 == CombatType.RANGE;
		default:
			return false;
		}

	}

	/**
	 * Npc killer id?
	 **/

	public int getNpcKillerId(int npcId) {
		int oldDamage = 0;
		int killerId = 0;
		for (int p = 1; p < Config.MAX_PLAYERS; p++) {
			if (PlayerHandler.players[p] != null) {
				if (PlayerHandler.players[p].lastNpcAttacked == npcId) {
					if (PlayerHandler.players[p].totalDamageDealt > oldDamage) {
						oldDamage = PlayerHandler.players[p].totalDamageDealt;
						killerId = p;
					}
					PlayerHandler.players[p].totalDamageDealt = 0;
				}
			}
		}
		return killerId;
	}

	/**
	 * 
	 */
	private void killedBarrow(int i) {
		if(npcs[i] == null)
			return;
		Player player = PlayerHandler.players[npcs[i].killedBy];
		if (player != null && player.getBarrows() != null) {
			Optional<Brother> brother = player.getBarrows().getBrother(npcs[i].npcType);
			if (brother.isPresent()) {
				brother.get().handleDeath();
			} else if (Boundary.isIn(npcs[i], Barrows.TUNNEL)) {
				if (player.getBarrows().getKillCount() < 25) {
					player.getBarrows().increaseMonsterKilled();
				}
			}
		}
	}
	
	/**
	 * Death delay
	 * 
	 *
	 * the npc whom were setting the delay to
	 * the delay were setting
	 */
	public int getDeathDelay(int i) {
		if(npcs[i] == null)
			return 4;
		switch (npcs[i].npcType) {
		case 8612:
		case 8613:
			return 6;
		case 8610:
		case 8611:
			return 2;
		case 8028:
			return 8;
		case 5548:
		case 5549:
		case 5550:
		case 5551:
		case 5552:
		case 1505:
		case 2910:
		case 2911:
		case 2912:
		case 484:
		case 7276:
		case 3138:
		case 1635:
		case 1636:
		case 1637:
		case 1638:
		case 1639:
		case 1640:
		case 1641:
		case 1642:
		case 1643:
		case 1654:
		case 7302:
			return 1;
		case 2209:
		case 2211:
		case 2212:
		case 2233:
		case 2234:
		case 435:
		case 3137:
		case 3139:
		case 3140:
		case 3159:
		case 3160:
		case 3161:
		case 2241:
			return 2;
		case 3134:
		case 3141:
			return 3;
		case 3166:
		case 3167:
		case 3168:
		case 3174:
			return 5;
		case 3129:
		case 3130:
		case 3131:
		case 3132:
			return 6;
		case 2237:
		case 2242:
		case 2243:
		case 2244:
		case 3135:
			return 7;
		default:
			return 3;
		}
	}
	public void handleJadDeath(int i) {
		Player c = (Player) PlayerHandler.players[npcs[i].spawnedBy];
		c.getItems().addItem(6570, 1);
		c.sendMessage("Congratulations on completing the fight caves minigame!");
		c.getPA().resetTzhaar();
		c.waveId = 300;
	}

	/**
	 * Drop Items
	 **/

	public void dropItems(int i) {
		Player c = PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			if (c.getTargeted() != null && npcs[i].equals(c.getTargeted())) {
				c.setTargeted(null);
				c.getPA().sendEntityTarget(0, npcs[i]);
			}
			if (EventBossHandler.getActiveBoss() != null) {
				if (npcs[i].npcType == EventBossHandler.getActiveBoss().getNpcId() && npcs[i].getHealth().getAmount() <= 0) {
					EventBossHandler.destroyBoss();
				}
			}
			if (WildernessBossHandler.getActiveBoss() != null) {
				if (npcs[i].npcType == WildernessBossHandler.getActiveBoss().getNpcId() && npcs[i].getHealth().getAmount() <= 0) {
					WildernessBossHandler.destroyBoss();
					int randomPkp = Misc.random(15) + 10;
					c.pkp += randomPkp;
				}
			}
			int dropX = npcs[i].getX();
			int dropY = npcs[i].getY();
			int dropHeight = npcs[i].getHeight();
			
			 if (npcs[i].npcType == 494) { 
				 if (Boundary.isIn(c, Boundary.KRAKEN_CAVE)) {
				 dropX = c.absX; 
				 dropY = c.absY; 
			 } else { 
				 dropX = 1770; 
				 dropY = 3426; 
				 } 
			}
			 
			if (npcs[i].npcType == 492 || npcs[i].npcType == 494) {
				dropX = c.getX();
				dropY = c.getY();
			}
			if (npcs[i].npcType == 2042 || npcs[i].npcType == 2043 || npcs[i].npcType == 2044
					|| npcs[i].npcType == 6720) {
				dropX = 2268;
				dropY = 3069;
				c.getZulrahEvent().stop();
			}
			if (npcs[i].npcType == 8028) {// VORKATH drop locations
				dropX = Vorkath.lootCoordinates[0];
				dropY = Vorkath.lootCoordinates[1];
			}
			/**
			 * Warriors guild
			 */
			c.getWarriorsGuild().dropDefender(npcs[i].getX(), npcs[i].getY());
			if (AnimatedArmour.isAnimatedArmourNpc(npcs[i].npcType)) {

				if (npcs[i].getX() == 2851 && npcs[i].getY() == 3536) {
					dropX = 2851;
					dropY = 3537;
					AnimatedArmour.dropTokens(c, npcs[i].npcType, npcs[i].getX(), npcs[i].getY() + 1);
				} else if (npcs[i].getX() == 2857 && npcs[i].getY() == 3536) {
					dropX = 2857;
					dropY = 3537;
					AnimatedArmour.dropTokens(c, npcs[i].npcType, npcs[i].getX(), npcs[i].getY() + 1);
				} else {
					AnimatedArmour.dropTokens(c, npcs[i].npcType, npcs[i].getX(), npcs[i].getY());
				}
			}
			
			Location location = new Location(dropX, dropY, dropHeight);
			World.getWorld().getDropManager().create(c, npcs[i], location, 1);

			if (NPCDefinitions.get(npcs[i].npcType).getNpcCombat() >= 100) {
				c.getNpcDeathTracker().add(NPCDefinitions.get(npcs[i].npcType).getNpcName());
			}
		}
	}


	// id of bones dropped by npcs
	public int boneDrop(int type) {
		switch (type) {
		case 1:// normal bones
		case 9:
		case 100:
		case 1770:
		case 12:
		case 17:
		case 803:
		case 18:
		case 81:
		case 101:
		case 41:
		case 19:
		case 90:
		case 75:
		case 86:
		case 78:
		case 912:
		case 913:
		case 914:
		case 1648:
		case 1643:
		case 1618:
		case 1624:
		case 181:
		case 119:
		case 49:
		case 26:
		case 1341:
			return 526;
		case 117:
			return 532;// big bones
		case 50:// drags
		case 53:
		case 54:
		case 55:
		case 941:
		case 1590:
		case 1591:
		case 1592:
			return 536;
		case 84:
		case 1615:
		case 1613:
		case 82:
		case 3200:
			return 592;
			
		case 2215:
		return 4834;
		default:
			return 526;
		}
	}

	public int getStackedDropAmount(int itemId, int npcId) {
		switch (itemId) {
		case 995:
			switch (npcId) {
			case 1:
				return 50 + Misc.random(50);
			case 9:
				return 133 + Misc.random(100);
			case 1624:
				return 1000 + Misc.random(300);
			case 1618:
				return 1000 + Misc.random(300);
			case 1643:
				return 1000 + Misc.random(300);
			case 1610:
				return 1000 + Misc.random(1000);
			case 1613:
				return 1500 + Misc.random(1250);
			case 1615:
				return 3000;
			case 18:
				return 500;
			case 101:
				return 60;
			case 913:
			case 912:
			case 914:
				return 750 + Misc.random(500);
			case 1612:
				return 250 + Misc.random(500);
			case 1648:
				return 250 + Misc.random(250);
			case 90:
				return 200;
			case 82:
				return 1000 + Misc.random(455);
			case 52:
				return 400 + Misc.random(200);
			case 49:
				return 1500 + Misc.random(2000);
			case 1341:
				return 1500 + Misc.random(500);
			case 26:
				return 500 + Misc.random(100);
			case 20:
				return 750 + Misc.random(100);
			case 21:
				return 890 + Misc.random(125);
			case 117:
				return 500 + Misc.random(250);
			case 2607:
				return 500 + Misc.random(350);
			}
			break;
		case 11212:
			return 10 + Misc.random(4);
		case 565:
		case 561:
			return 10;
		case 560:
		case 563:
		case 562:
			return 15;
		case 555:
		case 554:
		case 556:
		case 557:
			return 20;
		case 892:
			return 40;
		case 886:
			return 100;
		case 6522:
			return 6 + Misc.random(5);

		}

		return 1;
	}

	/**
	 * Slayer Experience
	 **/
	public void appendSlayerExperience(int i) {
		Player c = (Player) PlayerHandler.players[npcs[i].killedBy];
		if (c != null) {
			if (c.slayerTask == npcs[i].npcType) {
				c.taskAmount--;
				c.getPA().addSkillXP(npcs[i].getHealth().getMaximum() * Config.SLAYER_EXPERIENCE,
						18);
				if (c.taskAmount <= 0) {
					c.getPA().addSkillXP(
							(npcs[i].getHealth().getMaximum() * 8) * Config.SLAYER_EXPERIENCE, 18);
					c.slayerTask = -1;
					c.sendMessage("You completed your slayer task. Please see a slayer master to get a new one.");
				}
			}
		}
	}

	/**
	 * Resets players in combat
	 */

	public void resetPlayersInCombat(int i) {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (PlayerHandler.players[j] != null)
				if (PlayerHandler.players[j].underAttackBy2 == i)
					PlayerHandler.players[j].underAttackBy2 = 0;
		}
	}

	/**
	 * Npc Follow Player
	 **/

	public int GetMove(int Place1, int Place2) {
		if ((Place1 - Place2) == 0) {
			return 0;
		} else if ((Place1 - Place2) < 0) {
			return 1;
		} else if ((Place1 - Place2) > 0) {
			return -1;
		}
		return 0;
	}
	


	public boolean followPlayer(int i) {
		switch (npcs[i].npcType) {
			case 309:
			case 310:
			case 311:
			case 312:
			case 313:
			case 314:
			case 315:
			case 316:
			case 317:
			case 318:
			case 319:
			case 320:
			case 321:
			case 322:
			case 323:
			case 324:
			case 325:
			case 326:
			case 327:
			case 328:
			case 329:
			case 330:
			case 331:
			case 332:
			case 333:
			case 334:
			return false;
		}
		return true;
	}

	public void followPlayer(int i, int playerId) {
	    NPC npc = npcs[i];
	    Player player = PlayerHandler.players[playerId];

	    if (player == null || player.respawnTimer > 0 || npc.isDead) {
	        npc.faceEntity(0);
	        npc.randomWalk = true;
	        npc.underAttack = false;
	        return;
	    }

	    // Region and NPC specific checks
	    if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR) && !Boundary.isIn(player, Boundary.CORPOREAL_BEAST_LAIR)) {
	        npc.killerId = 0;
	        return;
	    }
	    if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
	        npc.killerId = 0;
	        return;
	    }
	    if (Boundary.isIn(npc, Zulrah.BOUNDARY) && ((npc.npcType >= 2042 && npc.npcType <= 2044) || npc.npcType == 6720)) {
	        return;
	    }
	    if ((npc.npcType >= 1739 && npc.npcType <= 1742) || npc.npcType == 7413 || (npc.npcType >= 7288 && npc.npcType <= 7294)) {
	        return;
	    }

	    int playerX = player.getX();
	    int playerY = player.getY();
	    npc.randomWalk = false;
	    int followDistance = followDistance(i);
	    int npcSize = npc.getSize();

	    // 1. Distance Calculation (Radius adjusted for large NPCs)
	    double distance = distanceRequired(i); 
	    if (npcSize > 1) {
	        distance += (double)npcSize / 2.0; 
	    }

	    npc.faceEntity(playerId);

	    // 2. Same-tile Overlap Check
	    if (playerX >= npc.getX() && playerX < npc.getX() + npcSize &&
	        playerY >= npc.getY() && playerY < npc.getY() + npcSize) {
	        stepAway(i);
	        return;
	    }

	    // 3. Stop if within required distance
	    if (npc.getDistance(playerX, playerY) <= distance) {
	        return;
	    }

	    boolean withinFollowArea = (npc.spawnedBy > 0) ||
	        ((npc.getX() < npc.makeX + followDistance) &&
	         (npc.getX() > npc.makeX - followDistance) &&
	         (npc.getY() < npc.makeY + followDistance) &&
	         (npc.getY() > npc.makeY - followDistance));

	    if (withinFollowArea && npc.getHeight() == player.getHeight()) {
	        
	        // 4. Directional Logic (Manual offset calculation)
	        int dir = NPCClipping.getDirection(npc.getX(), npc.getY(), playerX, playerY);
	        int moveX = 0;
	        int moveY = 0;

	        // Northwestern mapping (Adjust cases based on your NPCClipping.getDirection returns)
	        if (dir == 0 || dir == 1 || dir == 2) moveY = 1;
	        if (dir == 5 || dir == 6 || dir == 7) moveY = -1;
	        if (dir == 2 || dir == 4 || dir == 7) moveX = 1;
	        if (dir == 0 || dir == 3 || dir == 5) moveX = -1;

	        int nextX = npc.getX() + moveX;
	        int nextY = npc.getY() + moveY;

	        // 5. Collision Logic with Player Override
	        // Check if area is occupied by another NPC
	        boolean occupiedByNpc = isAreaOccupied(nextX, nextY, npc.getHeight(), npc);
	        
	        // Check if the target player is occupying that same restricted space
	        boolean playerOnBlockingTile = (playerX >= nextX && playerX < nextX + npcSize &&
	                                        playerY >= nextY && playerY < nextY + npcSize);

	        // Move if the path is clear OR if the player is occupying the blocking NPC's spot
	        if (!occupiedByNpc || playerOnBlockingTile) {
	            NPCDumbPathFinder.follow(npc, player);
	        } else {
	            // Path is blocked and player isn't there to override; stop to prevent clipping
	            npc.getWalkQueue().clear();
	        }
	    } else {
	        npc.faceEntity(0);
	        npc.randomWalk = true;
	        npc.underAttack = false;
	    }
	}

	public void stepAway(int i) {
		int[][] points = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

		for (int[] k : points) {
			int dir = NPCClipping.getDirection(k[0], k[1]);
			if (NPCDumbPathFinder.canMoveTo(npcs[i], dir)) {
				NPCDumbPathFinder.walkTowards(npcs[i], npcs[i].getX() + NPCClipping.DIR[dir][0], npcs[i].getY() + NPCClipping.DIR[dir][1]);
				break;
			}
		}
	}




	/**
	 * load spell
	 **/
	public void loadSpell2(int i) {
		npcs[i].attackType1 = CombatType.MAGE;
		int random = Misc.random(3);
		if (random == 0) {
			npcs[i].projectileId = 393; // red
			npcs[i].endGfx = 430;
		} else if (random == 1) {
			npcs[i].projectileId = 394; // green
			npcs[i].endGfx = 429;
		} else if (random == 2) {
			npcs[i].projectileId = 395; // white
			npcs[i].endGfx = 431;
		} else if (random == 3) {
			npcs[i].projectileId = 396; // blue
			npcs[i].endGfx = 428;
		}
	}

	public void loadSpell(Player player, int i) {
		int chance = 0;
		switch (npcs[i].npcType) {
		case 5086:
		case 5087:
		case 5088:
			npcs[i].attackType1 = CombatType.MAGE;
			if(player.getSkills().getLevel(Skill.ATTACK) >= player.getSkills().getActualLevel(Skill.ATTACK)) {
				npcs[i].gfx0(102);
				npcs[i].projectileId = 103;
				npcs[i].endGfx = 104;
				npcs[i].maxHit = 0;
				player.getSkills().decreaseLevel((int)(0.05 * player.getSkills().getActualLevel(Skill.ATTACK)), Skill.ATTACK);
			} else {
				npcs[i].gfx0(93);
				npcs[i].projectileId = 94;
				npcs[i].endGfx = 95;
				npcs[i].maxHit = 2;
			}
		break;
			
			case 7931:
			case 7932:
			case 7933:
			case 7934:
			case 7935:
			case 7936:
			case 7937:
			case 7938:
			case 7939:
			case 7940:
				if (player != null) {
					if (npcs[i].getHealth().getAmount() < npcs[i].getHealth().getMaximum() /2) {
						 
						int healchance = Misc.random(100);
//						if (healchance > 75) {
//							npcs[i].gfx0(1196);
//							npcs[i].getHealth().setAmount(npcs[i].getHealth().getAmount() + (npcs[i].getHealth().getMaximum() / 4));
//							Player killer = PlayerHandler.players[npcs[i].underAttackBy];
//							killer.sendMessage("The revenant drains power from within and heals.");
//							return;
//						}
					}
					
					int randomHit = Misc.random(15);
					boolean distance = !player.goodDistance(npcs[i].getX(), npcs[i].getY(), player.getX(), player.getY(), 5);
					if (randomHit <= 5 && !distance) {
						npcs[i].attackType1 = CombatType.MELEE;
						npcs[i].projectileId = -1;
						npcs[i].endGfx = -1;
					} else if (randomHit >5 && randomHit <= 10 || distance) {
						npcs[i].attackType1 = CombatType.MAGE;
						npcs[i].projectileId = 1415;
						npcs[i].endGfx =-1;
					} else if (randomHit >10 &&randomHit <= 15 || distance) {
						npcs[i].attackType1 = CombatType.RANGE;
						npcs[i].projectileId = 1415;
						npcs[i].endGfx =-1;
					}
				}

				break;
		case 1605:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 78;
			break;
		case 5890:

			if (npcs[i].attackType1 != null) {
				switch (npcs[i].attackType1) {
				case MAGE:
					npcs[i].endGfx = -1;
					npcs[i].projectileId = 1274;
					break;
				case MELEE:
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].endGfx = -1;
					npcs[i].projectileId = -1;
					break;
				case SPECIAL:
					npcs[i].attackType1 = CombatType.SPECIAL;
					groundAttack(npcs[i], player, -1, 1284, -1, 5);
					npcs[i].attackTimer = 8;
					npcs[i].hitDelayTimer = 5;
					npcs[i].endGfx = -1;
					npcs[i].projectileId = -1;
					break;

				default:
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].endGfx = -1;
					npcs[i].projectileId = -1;
					break;

				}
			}
			break;
			/*
			 * sets vorkaths attack details
			 */
		case 8028:// VORKATH
			//player.getVorkath().attacking = true;//checks if vorkath is executing a current attack or not
			
			/*
			 * If Vorkath is less than 50% health and venom stage has not happened yet it will trigger
			 */
			//if(npcs[i].getHealth().getAmount() <= npcs[i].getHealth().getMaximum()/2 && player.getVorkath().venomStageCompleted == false && player.getVorkath().venomStage == false) {
			//	player.getVorkath().startVenomStage();//starts venom stage
			//	return;
			//}
			
			/*
			 * while in venom/spitfire stage is shoots these fast projectiles
			 */
			/*if(player.getVorkath().venomStage == true && player.getVorkath().venomStageCompleted == false) {
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1482;
			npcs[i].attackTimer = 2;
			player.getVorkath().playerX = player.getX();
			player.getVorkath().playerY = player.getY();
			return;
			}*/
			
			/*switch (player.getVorkath().attackType) {//vorkaths regular attacks
			case 0://regular fireball no effect
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 5;
				npcs[i].projectileId = 393;

				break;
			case 1://venom attack
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 5;
				npcs[i].projectileId = 394;
				player.getHealth().proposeStatus(HealthStatus.POISON, 15, Optional.of(npcs[i]));
				break;
			case 2://blue fire ball no effect
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 5;
				npcs[i].projectileId = 1479;
				break;
			case 3://kamakaze crab
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 5;
				npcs[i].projectileId = 395;
				break;
			case 4://pink fire ball. turns off prayer
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 5;
				npcs[i].projectileId = 1471;
				break;
			case 5://spike ball that deals damage no matter what
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].hitDelayTimer = 6;
				npcs[i].projectileId = 1477;
				break;
			case 6://big fire ball that insta kills if player does not move
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 6;
				npcs[i].projectileId = 1481;
				player.getVorkath().playerX = player.getX();
				player.getVorkath().playerY = player.getY();
				break;
			}*/
			break;
			//end of vorkath
			case 7706:
					npcs[i].attackType1 = CombatType.RANGE;
					npcs[i].hitDelayTimer = 5;
					npcs[i].projectileId = 1375;
					npcs[i].endGfx = -1;

				break;
			/**
			 * Demonic Gorillas attack
			 */
			case 7144:
			case 7145:
			case 7146:
				if (npcs[i].attackType1 != null)
					switch (npcs[i].attackType1) {
						case MAGE:
							npcs[i].attackType1 = CombatType.MAGE;
							npcs[i].endGfx = 1304;
							npcs[i].projectileId = 1305;
							break;

						case MELEE:
							npcs[i].attackType1 = CombatType.MELEE;
							npcs[i].endGfx = -1;
							npcs[i].projectileId = -1;
							break;

						case RANGE:
							npcs[i].attackType1 = CombatType.RANGE;
							npcs[i].endGfx = 1303;
							npcs[i].projectileId = 1302;
							break;

						case SPECIAL:
							npcs[i].attackType1 = CombatType.SPECIAL;
							groundAttack(npcs[i], player, 304, 303, 305, 5);
							npcs[i].attackTimer = 8;
							npcs[i].hitDelayTimer = 5;
							npcs[i].endGfx = -1;
							npcs[i].projectileId = -1;
							break;

						default:
							break;
					}
				break;

		case 320:
			if (npcs[i].getHealth().getStatus() == HealthStatus.POISON) {
				npcs[i].attackTimer *= 2;
			}
			break;
		case 498:
		case 499:
			npcs[i].projectileId = 642;
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].endGfx = -1;
			break;

		// Zilyana
		case 2205:
			if (Misc.random(3) == 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = -1;
			} else {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].endGfx = -1;
				npcs[i].projectileId = -1;
			}
			break;
		// Growler
		case 2207:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1203;
			break;
		// Bree
		case 2208:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 9;
			break;
		// Saradomin priest
		case 2209:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 76;
			break;
		// Saradomin ranger
		case 2211:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 20;
			npcs[i].endGfx = -1;
			break;
		// Saradomin mage
		case 2212:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 162;
			npcs[i].endGfx = 163;
			npcs[i].setProjectileDelay(2);
			break;

		case 3428:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 249;
			npcs[i].endGfx = -1;
			break;
		// Steelwill
		case 2217:
			npcs[i].projectileId = 1217;
			npcs[i].endGfx = 1218;
			npcs[i].attackType1 = CombatType.MAGE;
			break;
		// Grimspike
		case 2218:
			npcs[i].projectileId = 1193;
			npcs[i].endGfx = -1;
			npcs[i].attackType1 = CombatType.RANGE;
			break;
		// Bandos ranger
		case 2242:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 1197;
			npcs[i].endGfx = -1;
			break;
		// Bandos mage
		case 2244:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 165;
			npcs[i].endGfx = 166;
			break;
		// Saradomin ranger
		case 3160:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 20;
			npcs[i].endGfx = -1;
			break;
		// Zammorak mage
		case 3161:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 156;
			npcs[i].endGfx = 157;
			npcs[i].setProjectileDelay(2);
			break;
		// Armadyl boss
		case 3162:
			if (Misc.random(2) == 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1200;
			} else {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 1199;
			}
			npcs[i].setProjectileDelay(1);
			break;
		// Skree
		case 3163:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1192;
			npcs[i].endGfx = -1;
			break;
		// Geerin
		case 3164:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 1192;
			npcs[i].endGfx = -1;
			break;
		// Armadyl ranger
		case 3167:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 1192;
			npcs[i].endGfx = -1;
			break;
		// Armadyl mage
		case 3168:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 159;
			npcs[i].endGfx = 160;
			break;
		// Aviansie
		case 3174:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 1193;
			npcs[i].endGfx = -1;
			break;

		case 1672: // Ahrim
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 156;
			npcs[i].endGfx = Math.random() <= Barrows.SPECIAL_CHANCE ? 400 : 157;
			break;

		case 1675: // Karil
			npcs[i].projectileId = 27;
			npcs[i].attackType1 = CombatType.RANGE;
			break;

		case 2042:
			chance = 1;
			if (player != null) {
				if (player.getZulrahEvent().getStage() == 9) {
					chance = 2;
				}
			}
			chance = Misc.random(chance);
			npcs[i].setFacePlayer(true);
			if (chance < 2) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 97;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 3;
				npcs[i].attackTimer = 4;
			} else {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 156;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 3;
				npcs[i].attackTimer = 4;
			}
			break;

		case 1610:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 78;
			break;

		case 1611:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 76;
			break;

		case 1612:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 77;
			break;

		case 2044:
			npcs[i].setFacePlayer(true);
			if (Misc.random(3) > 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1046;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 3;
				npcs[i].attackTimer = 4;
			} else {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 1044;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 3;
				npcs[i].attackTimer = 4;
			}
			break;

		case 983: // Dagannoth mother Air
		case 6373:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 159;
			npcs[i].endGfx = 160;
			break;

		case 984: // Dagannoth mother Water
		case 6375:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 162;
			npcs[i].endGfx = 163;
			break;

		case 985: // Dagannoth mother Fire
		case 6376:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 156;
			npcs[i].endGfx = 157;
			break;

		case 6378: // Mother Earth
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 165;
			npcs[i].endGfx = 166;
			break;

		case 987: // Dagannoth mother range
		case 6377:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 996;
			npcs[i].endGfx = -1;
			break;

		case 6371: // Karamel
			if (Misc.random(10) > 6) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].attackTimer = 6;
				npcs[i].endGfx = 369;
				npcs[i].forceChat("Semolina-Go!");
			} else {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].attackTimer = 3;
				npcs[i].endGfx = -1;
			}
			break;

		case 6372: // Dessourt
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			npcs[i].projectileId = 866;
			npcs[i].endGfx = 865;
			if (Misc.random(10) > 6) {
				npcs[i].forceChat("Hssssssssss");
			}
			break;

		case 6368: // Culinaromancer
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].attackTimer = 4;
			break;

		case 2043:
			npcs[i].setFacePlayer(false);
			npcs[i].turnNpc(player.getX(), player.getY());
			npcs[i].targetedLocation = new Location(player.getX(), player.getY(), player.getHeight());
			npcs[i].attackType1 = CombatType.MELEE;
			npcs[i].attackTimer = 9;
			npcs[i].hitDelayTimer = 6;
			npcs[i].projectileId = -1;
			npcs[i].endGfx = -1;
			break;

		case 6611:
		case 6612:
			chance = Misc.random(100);
			int distanceToVet = player.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			if (distanceToVet < 3) {
				if (chance < 25) {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].attackTimer = 7;
					npcs[i].hitDelayTimer = 4;
					groundSpell(npcs[i], player, 280, 281, "vetion", 4);
				} else if (chance > 90 && System.currentTimeMillis() - npcs[i].lastSpecialAttack > 15_000) {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].attackTimer = 5;
					npcs[i].hitDelayTimer = 2;
					npcs[i].lastSpecialAttack = System.currentTimeMillis();
				} else {
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].attackTimer = 5;
					npcs[i].hitDelayTimer = 2;
				}
			} else {
				if (chance < 71) {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].attackTimer = 7;
					npcs[i].hitDelayTimer = 4;
					groundSpell(npcs[i], player, 280, 281, "vetion", 4);
				} else if (System.currentTimeMillis() - npcs[i].lastSpecialAttack > 15_000) {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].attackTimer = 5;
					npcs[i].hitDelayTimer = 2;
					npcs[i].lastSpecialAttack = System.currentTimeMillis();
				} else {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].attackTimer = 7;
					npcs[i].hitDelayTimer = 4;
					groundSpell(npcs[i], player, 280, 281, "vetion", 4);
				}
			}
			break;

		case 6914: // Lizardman, Lizardman brute
		case 6915:
		case 6916:
		case 6917:
		case 6918:
		case 6919:
			int randomAtt = Misc.random(1);
			if (randomAtt == 1) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1291;
				npcs[i].endGfx = -1;
				if (Misc.random(10) == 5) {
					player.getHealth().proposeStatus(HealthStatus.POISON, 3, Optional.of(npcs[i]));
				}
			} else {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;

		/**
		 * Lizardman Shaman<
		 */
		case 6766:
			int randomAttack3 = Misc.random(100);
			if (randomAttack3 > 9 && randomAttack3 < 90) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].attackTimer = 5;
				npcs[i].hitDelayTimer = 2;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else if (randomAttack3 > 89) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 3;
				npcs[i].projectileId = 1293;
				npcs[i].endGfx = 1294;

				if (Misc.random(5) == 5) {
					player.getHealth().proposeStatus(HealthStatus.POISON, 10, Optional.of(npcs[i]));
				}
			} else {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].attackTimer = 10;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 8;
				groundSpell(npcs[i], player, -1, 1295, "spawns", 10);
			}
			break;
			

		/**
		 * Crazy Archaeologist
		 */
		case 6618:
			int randomAttack = Misc.random(10);
			String[] shout = { "I'm Bellock - respect me!", "Get off my site!", "No-one messes with Bellock's dig!",
					"These ruins are mine!", "Taste my knowledge!", "You belong in a museum!" };
			String randomShout = (shout[new Random().nextInt(shout.length)]);

			if (player.distanceToPoint(npcs[i].getX(), npcs[i].getY()) < 2) {
				npcs[i].forceChat(randomShout);
				if (randomAttack > 2 && randomAttack < 7) {
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].attackTimer = 5;
					npcs[i].hitDelayTimer = 2;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
				} else if (randomAttack > 6) {
					npcs[i].attackType1 = CombatType.RANGE;
					npcs[i].hitDelayTimer = 3;
					npcs[i].projectileId = 1259;
					npcs[i].endGfx = 140;
				} else {
					npcs[i].forceChat("Rain of knowledge!");
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					groundSpell(npcs[i], player, 1260, 131, "archaeologist", 4);
				}
			} else {
				if (randomAttack > 3) {
					npcs[i].forceChat(randomShout);
					npcs[i].attackType1 = CombatType.RANGE;
					npcs[i].projectileId = 1259;
					npcs[i].endGfx = 140;
				} else {
					npcs[i].forceChat("Rain of knowledge!");
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					groundSpell(npcs[i], player, 1260, 131, "archaeologist", 4);
				}
			}
			break;

		/**
		 * Chaos fanatic
		 */
		case 6619:
			int randomAttack2 = Misc.random(10);
			String[] shout_chaos = { "Burn!", "WEUGH!", "Develish Oxen Roll!",
					"All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH",
					"I shall call him squidgy and he shall be my squidgy!" };
			String randomShoutChaos = (shout_chaos[new Random().nextInt(shout_chaos.length)]);

			npcs[i].forceChat(randomShoutChaos);

			if (player.distanceToPoint(npcs[i].getX(), npcs[i].getY()) < 2) {
				if (randomAttack2 > 2) {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].hitDelayTimer = 3;
					npcs[i].projectileId = 1044;
					npcs[i].endGfx = 140;
				} else {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					groundSpell(npcs[i], player, 1045, 131, "fanatic", 4);
				}
			} else {
				if (randomAttack2 > 3) {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 1044;
					npcs[i].endGfx = 140;
				} else {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
					npcs[i].hitDelayTimer = 3;
					groundSpell(npcs[i], player, 1045, 131, "fanatic", 4);
				}
			}
			break;

		case 465:
			boolean distanceToWyvern = player.goodDistance(npcs[i].getX(), npcs[i].getY(), player.getX(), player.getY(), 3);
			int newRandom = Misc.random(10);
			if (newRandom >= 2) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 258;
				npcs[i].endGfx = -1;
			} else if (distanceToWyvern && newRandom == 1) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			}
			break;
			
			/*
			 * Hydra combat
			 */
			
		case 8609:
			
		npcs[i].attack = 150;
		npcs[i].defence = 450;
		
			
			if (player.hydraAttackCount == 12) {
				player.hydraAttackCount = 0;
				//player.sendMessage("Hydra attack count reset to 0");
			}
			
			if (player.hydraAttackCount <= 6 && player.countUntilPoison != 20) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1663;
				npcs[i].endGfx = -1;
				npcs[i].hitDelayTimer = 4;
				player.hydraAttackCount++;
				player.countUntilPoison++;
				//player.sendMessage("count until poison hits: " + player.countUntilPoison + " [MAGE]");
				//player.sendMessage("hydra attack counter: " + player.hydraAttackCount + "[MAGE]");
			}
			if (player.hydraAttackCount > 6 && player.hydraAttackCount <= 12 && player.countUntilPoison != 20) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 1662;
				npcs[i].endGfx = -1;
				npcs[i].maxHit = 22;
				player.hydraAttackCount++;
				player.countUntilPoison++;
				//player.sendMessage("count until poison hits: " + player.countUntilPoison + " [RANGE]");
				//player.sendMessage("hydra attack counter: " + player.hydraAttackCount + " [RANGE]");
			}
			if (player.countUntilPoison == 20) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				player.countUntilPoison = 0;
				groundSpell(npcs[i], player, 1660, 1655, "hydra", 4);
				player.getHealth().proposeStatus(HealthStatus.POISON, Misc.random(3, 10), Optional.of(npcs[i]));
				player.sendMessage("You have been poisoned.");
			}
			break;
			
		case 70:
			npcs[i].attackType1 = CombatType.MELEE;
			npcs[i].getStats().getAttack();
			npcs[i].getStats().getStrength();
			npcs[i].getStats().getStabAttack();
			npcs[i].getStats().getSlashAttack();
			npcs[i].getStats().getDefence();
			npcs[i].maxHit = 6;
			break;
		case 2098:
			npcs[i].attackType1 = CombatType.MELEE;
			npcs[i].getStats().getAttack();
			npcs[i].getStats().getStrength();
			npcs[i].getStats().getDefence();
			npcs[i].getStats().getStabAttack();
			npcs[i].getStats().getSlashAttack();
			npcs[i].getStats().getCrushAttack();
			npcs[i].maxHit = 4;
			break;
			
		/*
		 * Gets the npc id for the Rune Dragon & addy dragon
		 */
		case 8031:
		case 8030:
			boolean distanceToDragon = player.goodDistance(npcs[i].getX(), npcs[i].getY(), player.getX(), player.getY(), 3);

			int randomAttack1 = Misc.random(12);
			int damage = 0;
			damage = Misc.random(getMaxHit(i));
			int hit = damage;
			int hp = npcs[i].getHealth().getAmount();
			int maxHp = npcs[i].getHealth().getMaximum();
			
			/*
			 * Handles the main/range attack if any number 4 and above is rolled
			 * for randomAttack
			 */
			if (randomAttack1 >= 5 && randomAttack1 <= 7) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 258;
				npcs[i].endGfx = -1;
				npcs[i].maxHit = 18;

				/*
				 * Handles the melee attack if the player is within 1 tile and 1
				 * is rolled for randomAttack
				 */
			} else if (distanceToDragon && randomAttack1 == 1) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				npcs[i].maxHit = 20;

				/*
				 * Handles the magic attack if 2 is rolled for randomAttack
				 */
			} else if (randomAttack1 >= 8 && randomAttack1 <= 10) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				npcs[i].maxHit = 30;
			} 
			
			/*
			 * Handles the first special attack if a 2 is rolled for
			 * randomAttack + adds 100% dmg dealt to the Rune Dragons hp
			 */
			else if (randomAttack1 == 3) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].projectileId = 1183;
				npcs[i].endGfx = 1363;
				npcs[i].maxHit = 20;
				// TODO REAL Special gfx / special 1 / healing isnt working
				if (hp >= maxHp) {
					return;
				} else if (npcs[i] != null && hp < maxHp) {
					npcs[i].getHealth().increase(hit / 2);
					player.sendMessage("You feel the dragon leeching your life force.");
				}

				/*
				 * Handles the second special attack if a 3 is rolled for the
				 * randomAttack + does dmg per tick if hit 
				 * Projectile hits at the players coordinates TODO
				 * 5x5 square radius for tick dmg TODO
				 */
			} else if (randomAttack1 == 4) {
				
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].singleCombatDelay = 5;
				npcs[i].projectileId = 1198;
				npcs[i].endGfx = 1196;

				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					int ticks = 0;
					@Override
					public void execute(CycleEventContainer container) {
						if (player.disconnected) {
							stop();
							return;
						}
						switch (ticks++) {
						case 0:
							npcs[i].attackType1 = CombatType.SPECIAL;
							npcs[i].projectileId = -1;
							npcs[i].endGfx = -1;
							npcs[i].maxHit = 7;
							//groundSpell(npcs[i], player, 1198, 1196, "Dragon", 4);
							//player.sendMessage("@red@Lightening attack 1/5");
							break;

						case 1:
							npcs[i].attackType1 = CombatType.SPECIAL;
							npcs[i].projectileId = -1;
							npcs[i].endGfx = -1;
							npcs[i].maxHit = 7;
							//player.sendMessage("@red@Lightening attack 2/5");
							break;

						case 2:
							npcs[i].attackType1 = CombatType.SPECIAL;
							npcs[i].projectileId = -1;
							npcs[i].endGfx = -1;
							npcs[i].maxHit = 7;
							//player.sendMessage("@red@Lightening attack 3/5");
							container.stop();
							break;
						}
					}

					@Override
					public void stop() {

					}
				}, 1); //handles delay between ticks
				/*
				 * handles the dragonfire breathe
				 */
			} else {
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			}
			break;

		case 1046:
		case 1049:
			if (Misc.random(10) > 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].gfx100(194);
				npcs[i].projectileId = 195;
				npcs[i].endGfx = 196;
			} else {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].gfx100(194);
				npcs[i].projectileId = 195;
				npcs[i].endGfx = 576;

			}
			break;
		case 6610:
			if (Misc.random(15) > 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].gfx100(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			} else {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].gfx0(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			}
			break;

		case 6609:
			if (player != null) {
				int randomHit = Misc.random(20);
				boolean distance = !player.goodDistance(npcs[i].getX(), npcs[i].getY(), player.getX(), player.getY(), 5);
				if (randomHit < 15 && !distance) {
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
				} else if (randomHit >= 15 && randomHit < 20 || distance) {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 395;
					npcs[i].endGfx = 431;
				} else {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
				}
			}
			break;
		case 5535:
		case 494:
		case 492:
			npcs[i].attackType1 = CombatType.MAGE;
			if (Misc.random(5) > 0 && npcs[i].npcType == 494 || npcs[i].npcType == 5535) {
				npcs[i].gfx0(161);
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			} else {
				npcs[i].gfx0(155);
				npcs[i].projectileId = 156;
				npcs[i].endGfx = 157;
			}
			break;
		case 2892:
			npcs[i].projectileId = 94;
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 95;
			break;
		case 2894:
			npcs[i].projectileId = 298;
			npcs[i].attackType1 = CombatType.RANGE;
			break;
		case 264:
		case 259:
		case 247:
		case 268:
		case 270:
		case 274:
		case 6593:
		case 273:
		case 2919:
		case 2918:
			int random2 = Misc.random(2);
			if (random2 == 0) {
				npcs[i].projectileId = 393;
				npcs[i].endGfx = 430;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			} else {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;
			
			/*
			 * brutal black dragons
			 */
		case 7275:
			int bbdrandom2 = Misc.random(5);
			int distanceToBrutal = player.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			
			if (bbdrandom2 <= 4) {
				npcs[i].projectileId = 396;
				npcs[i].endGfx = 428;
				npcs[i].attackType1 = CombatType.MAGE;
			} else if (distanceToBrutal <= 4) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].projectileId = 393;
				npcs[i].endGfx = 430;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			}
			break;
			/*
			 * brutal black dragons
			 */
		case 7274:
			int bReddrandom2 = Misc.random(5);
			int distanceToBrutalR = player.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			
			if (bReddrandom2 <= 4) {
				npcs[i].projectileId = 396; 
				npcs[i].endGfx = 428;
				npcs[i].attackType1 = CombatType.MAGE;
			} else if (distanceToBrutalR <= 4) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].projectileId = 393;
				npcs[i].endGfx = 430;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			}
			break;
			/*
			 * brutal blue dragons
			 */
		case 7273:
			int bblueDrandom2 = Misc.random(5);
			int distanceToBrutalB = player.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			
			if (bblueDrandom2 <= 4) {
				npcs[i].projectileId = 396;
				npcs[i].endGfx = 428;
				npcs[i].attackType1 = CombatType.MAGE;
			} else if (distanceToBrutalB <= 4) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			} else {
				npcs[i].projectileId = 393;
				npcs[i].endGfx = 430;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			}
			break;
		case 239:
			int random = Misc.random(100);
			int distance = player.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			if (random >= 60 && random < 65) {
				npcs[i].projectileId = 394; // green
				npcs[i].endGfx = 429;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			} else if (random >= 65 && random < 75) {
				npcs[i].projectileId = 395; // white
				npcs[i].endGfx = 431;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			} else if (random >= 75 && random < 80) {
				npcs[i].projectileId = 396; // blue
				npcs[i].endGfx = 428;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			} else if (random >= 80 && distance <= 4) {
				npcs[i].projectileId = -1; // melee
				npcs[i].endGfx = -1;
				npcs[i].attackType1 = CombatType.MELEE;
			} else {
				npcs[i].projectileId = 393; // red
				npcs[i].endGfx = 430;
				npcs[i].attackType1 = CombatType.DRAGON_FIRE;
			}
			break;
		// arma npcs
		// sara npcs
		case 2562: // sara
			random = Misc.random(1);
			if (random == 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].endGfx = 1224;
				npcs[i].projectileId = -1;
			} else if (random == 1)
				npcs[i].attackType1 = CombatType.MELEE;
			break;
		case 2563: // star
			npcs[i].attackType1 = CombatType.MELEE;
			break;
		case 2564: // growler
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1203;
			break;
		case 2565: // bree
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 9;
			break;
		case 2551:
			npcs[i].attackType1 = CombatType.MELEE;
			break;
		case 2552:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1203;
			break;
		case 2553:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 1206;
			break;
		case 2025:
			npcs[i].attackType1 = CombatType.MAGE;
			int r = Misc.random(3);
			if (r == 0) {
				npcs[i].gfx100(158);
				npcs[i].projectileId = 159;
				npcs[i].endGfx = 160;
			}
			if (r == 1) {
				npcs[i].gfx100(161);
				npcs[i].projectileId = 162;
				npcs[i].endGfx = 163;
			}
			if (r == 2) {
				npcs[i].gfx100(164);
				npcs[i].projectileId = 165;
				npcs[i].endGfx = 166;
			}
			if (r == 3) {
				npcs[i].gfx100(155);
				npcs[i].projectileId = 156;
			}
			break;
		case 2265:// supreme
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 298;
			break;

		case 2266:// prime
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 162;
			npcs[i].endGfx = 477;
			break;

		case 2028:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 27;
			break;

		case 2054:
			int r2 = Misc.random(1);
			if (r2 == 0) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].gfx100(550);
				npcs[i].projectileId = 551;
				npcs[i].endGfx = 552;
			} else {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].gfx100(553);
				npcs[i].projectileId = 554;
				npcs[i].endGfx = 555;
			}
			break;
		case 6257:// saradomin strike
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 76;
			break;
		case 6221:// zamorak strike
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].endGfx = 78;
			break;
		case 6231:// arma
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1199;
			break;


			case 7692:
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 1382;
				break;
			case 7693:
				int r3 = Misc.random(1);
				if (r3 == 0) {
					npcs[i].attackType1 = CombatType.MELEE;
				} else {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 1380;

				}
				break;
			case 7694:
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1381;
				break;
			case 7702:
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].projectileId = 1378;
				break;
			case 7699:
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 449;
				break;
			case 7708:
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 660;
				break;



			// sara npcs
		case 3129:
			random = Misc.random(15);
			if (random > 0 && random < 7) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
			} else if (random >= 7) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1211;
			} else if (random == 0) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].projectileId = -1;
			}
			break;
		case 1047:// cave horror
			random = Misc.random(3);
			if (random == 0 || random == 1) {
				npcs[i].attackType1 = CombatType.MELEE;
			} else {
				npcs[i].attackType1 = CombatType.MAGE;
			}
			break;
		case 3127:
		case 7700:
			int r23 = 0;
			if (goodDistance(npcs[i].getX(), npcs[i].getY(), PlayerHandler.players[npcs[i].spawnedBy].getX(),
					PlayerHandler.players[npcs[i].spawnedBy].getY(), 1)) {
				r23 = Misc.random(2);
			} else {
				r23 = Misc.random(1);
			}
			if (r23 == 0) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 448;
				npcs[i].endGfx = 157;
				npcs[i].hitDelayTimer = 6;
				npcs[i].attackTimer = 9;
			} else if (r23 == 1) {
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].endGfx = 451;
				npcs[i].projectileId = -1;
				npcs[i].hitDelayTimer = 6;
				npcs[i].attackTimer = 9;
			} else if (r23 == 2) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;
		case 3125:
			if (player.distanceToPoint(npcs[i].getX(), npcs[i].getY()) > 2) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 445;
				npcs[i].endGfx = 446;
			} else {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
			}
			break;

		case 3121:
		case 2167:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].projectileId = 443;
			break;
		case 1678:
		case 1679:
		case 1680:
		case 1683:
		case 1684:
		case 1685:
			npcs[i].attackType1 = CombatType.MELEE;
			npcs[i].attackTimer = 4;
			break;

		case 319:
			int corpRandom = Misc.random(15);
			if (corpRandom >= 12) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 3;
				npcs[i].projectileId = Misc.random(1) == 0 ? 316 : 314;
				npcs[i].endGfx = -1;
			}
			if (corpRandom >= 3 && corpRandom <= 11) {
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].hitDelayTimer = 2;
				npcs[i].endGfx = -1;
			}
			if (corpRandom <= 2) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].hitDelayTimer = 3;
				groundSpell(npcs[i], player, 315, 317, "corp", 4);
			}
			break;

		/**
		 * Kalphite Queen Stage One
		 */
		case 963:
		case 965:
			int kqRandom = Misc.random(2);
			switch (kqRandom) {
			case 0:
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].hitDelayTimer = 3;
				npcs[i].projectileId = 280;
				npcs[i].endGfx = 281;
				break;
			case 1:
				npcs[i].attackType1 = CombatType.RANGE;
				npcs[i].hitDelayTimer = 3;
				npcs[i].projectileId = 473;
				npcs[i].endGfx = 281;
				break;
			case 2:
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				break;
			}
			break;
		/**
		 * Tekton
		 */
		case 7544:
			if (Objects.equals(tektonAttack, "MELEE")) {
				npcs[i].attackType1 = CombatType.MELEE;
			} else if (Objects.equals(tektonAttack, "SPECIAL")) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				Tekton.tektonSpecial(player);
				tektonAttack = "MELEE";
				npcs[i].hitDelayTimer = 4;
				npcs[i].attackTimer = 8;
			}
			break;
		/**
		 * Glod
		 */
		case 5129:
			if (Objects.equals(glodAttack, "MELEE")) {
				npcs[i].attackType1 = CombatType.MELEE;
			} else if (Objects.equals(glodAttack, "SPECIAL")) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				Glod.glodSpecial(player);
				glodAttack = "MELEE";
				npcs[i].hitDelayTimer = 4;
				npcs[i].attackTimer = 8;
			}
			break;
			
			/*
			 * Tarn Combat
			 */
		/**
		 * Ice Queen
		 */
		case 4922:
			if (Objects.equals(queenAttack, "MAGIC")) {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = 367;
				npcs[i].hitDelayTimer = 5;
			} else if (Objects.equals(queenAttack, "SPECIAL")) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				IceQueen.queenSpecial(player);
				queenAttack = "MAGIC";
				npcs[i].hitDelayTimer = 4;
				npcs[i].attackTimer = 8;
			}
			break;

		/**
		 * Tekton magers
		 */
		case 7617:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1348;
			npcs[i].endGfx = 1345;
			npcs[i].hitDelayTimer = 5;
			npcs[i].attackTimer = 15;
			break;
			case 7529:
				if (Misc.random(10) == 5) {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = 1348;
					npcs[i].endGfx = 1345;
					npcs[i].hitDelayTimer = 3;
				} else {
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 1348;
					npcs[i].endGfx = 1345;
					npcs[i].hitDelayTimer = 3;
				}
				break;

			case 7566:
				if (Misc.random(10) <= 8) {
				  npcs[i].attackType1 = CombatType.MAGE;
				  npcs[i].projectileId = 1327;
				  npcs[i].endGfx = 1328;
				  npcs[i].hitDelayTimer = 3;
				  npcs[i].maxHit = 35;
				} else {
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = 1329;
					npcs[i].endGfx = 1330;
					npcs[i].maxHit = 48;
					npcs[i].hitDelayTimer = 3;
					break;
				}
				break;			
			
			case 7554://great olm
				int randomStyle1 = Misc.random(12);

				switch (randomStyle1) {
				case 0://mage
				case 1:
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 1339;
					npcs[i].endGfx = 1353;
					npcs[i].maxHit = 30;
					npcs[i].hitDelayTimer = 2;
					break;
					
				case 2://range
				case 3:
				case 4:
				case 5:
				case 6:
					npcs[i].attackType1 = CombatType.RANGE;
					npcs[i].projectileId = 1340;
					npcs[i].endGfx = 1353;
					npcs[i].maxHit = 30;
					npcs[i].hitDelayTimer = 2;
					break;
					
				case 7://acid
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = 1354;
					npcs[i].endGfx = 1358;
					npcs[i].maxHit = 40;
					npcs[i].hitDelayTimer = 2;
					player.getHealth().proposeStatus(HealthStatus.POISON, Misc.random(3, 10), Optional.of(npcs[i]));
					player.sendMessage("You have been poisoned by Olm's acid attack!");
					break;
					
				case 8://dragon fire
				case 9:
					npcs[i].attackType1 = CombatType.DRAGON_FIRE;
					npcs[i].projectileId = 393;
					npcs[i].endGfx = 430;
					npcs[i].maxHit = 52;
					npcs[i].hitDelayTimer = 3;
					break;
					
				case 10://burn
				case 11:
					npcs[i].attackType1 = CombatType.SPECIAL;
					npcs[i].projectileId = 1349;
					npcs[i].endGfx = -1;
					npcs[i].maxHit = 40;
					npcs[i].hitDelayTimer = 2;
					break;
				}
				break;
				
		case 7604:
		case 7605:
		case 7606:
			if (Misc.random(10) == 5) {
				npcs[i].attackType1 = CombatType.SPECIAL;
				npcs[i].forceChat("RAA!");
				npcs[i].projectileId = 1348;
				npcs[i].endGfx = 1345;
				npcs[i].hitDelayTimer = 3;
			} else {
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1348;
				npcs[i].endGfx = 1345;
				npcs[i].hitDelayTimer = 3;
			}
			break;

		/**
		 * Cerberus
		 */
		case 5862:
			if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "GROUND_ATTACK")) {
				startAnimation(4492, i);
				npcs[i].forceChat("Grrrrrrrrrrrrrr");
				npcs[i].attackType1 = CombatType.SPECIAL; 
				npcs[i].hitDelayTimer = 4;
				groundSpell(npcs[i], player, -1, 1246, "cerberus", 4);
				player.CERBERUS_ATTACK_TYPE = "MELEE";
			}
			if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "GHOST_ATTACK")) {
				startAnimation(4494, i);
				// npcs[i].forceChat("Aaarrrooooooo");
				player.CERBERUS_ATTACK_TYPE = "MELEE";
			}
			if (Objects.equals(player.CERBERUS_ATTACK_TYPE, "FIRST_ATTACK")) {
				startAnimation(4493, i);
				npcs[i].attackTimer = 5;
				player.CERBERUS_ATTACK_TYPE = "MELEE";
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
					int ticks = 0;

					@Override
					public void execute(CycleEventContainer container) {
						if (player.disconnected) {
							stop();
							return;
						}
						switch (ticks++) {
						case 0:
							npcs[i].attackType1 = CombatType.MELEE;
							npcs[i].projectileId = -1;
							npcs[i].endGfx = -1;
							break;

						case 2:
							npcs[i].attackType1 = CombatType.RANGE;
							npcs[i].projectileId = 1245;
							npcs[i].endGfx = 1244;
							break;

						case 4:
							npcs[i].attackType1 = CombatType.MAGE;
							npcs[i].projectileId = 1242;
							npcs[i].endGfx = 1243;
							container.stop();
							break;
						}
						// System.out.println("Ticks - cerb " + ticks);
					}

					@Override
					public void stop() {

					}
				}, 2);
			} else {
				int randomStyle = Misc.random(2);

				switch (randomStyle) {
				case 0:
					npcs[i].attackType1 = CombatType.MELEE;
					npcs[i].projectileId = -1;
					npcs[i].endGfx = -1;
					break;

				case 1:
					npcs[i].attackType1 = CombatType.RANGE;
					npcs[i].projectileId = 1245;
					npcs[i].endGfx = 1244;
					break;

				case 2:
					npcs[i].attackType1 = CombatType.MAGE;
					npcs[i].projectileId = 1242;
					npcs[i].endGfx = 1243;
					break;
				}
			}
			break;

		case Skotizo.AWAKENED_ALTAR_NORTH:
		case Skotizo.AWAKENED_ALTAR_SOUTH:
		case Skotizo.AWAKENED_ALTAR_WEST:
		case Skotizo.AWAKENED_ALTAR_EAST:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].projectileId = 1242;
			npcs[i].endGfx = 1243;
			break;

		case Skotizo.SKOTIZO_ID:
			int randomStyle;
			if (player.getSkotizo().firstHit) {
				randomStyle = 1;
				player.getSkotizo().firstHit = false;
			} else {
				randomStyle = Misc.random(1);
			}
			switch (randomStyle) {
			case 0:
				npcs[i].attackType1 = CombatType.MELEE;
				npcs[i].projectileId = -1;
				npcs[i].endGfx = -1;
				break;

			case 1:
				npcs[i].attackType1 = CombatType.MAGE;
				npcs[i].projectileId = 1242;
				npcs[i].endGfx = 1243;
				break;
			}
			break;

		case 5867:
			npcs[i].attackType1 = CombatType.RANGE;
			npcs[i].hitDelayTimer = 3;
			npcs[i].projectileId = 1230;
			npcs[i].attackTimer = 15;
			break;

		case 5868:
			npcs[i].attackType1 = CombatType.MAGE;
			npcs[i].hitDelayTimer = 3;
			npcs[i].projectileId = 127;
			npcs[i].attackTimer = 15;
			break;

		case 5869:
			npcs[i].attackType1 = CombatType.MELEE;
			npcs[i].hitDelayTimer = 3;
			npcs[i].projectileId = 1248;
			npcs[i].attackTimer = 15;
			break;
		}
	}
	private ArrayList<int[]> gorillaBoulder = new ArrayList<>(1);
	private void groundAttack(NPC npc, Player player, int startGfx, int endGfx, int explosionGfx, int time) {
		if (player == null) return;

		player.totalMissedGorillaHits = 3;

		// FIX: Create a local, isolated list for this specific attack instance.
		// This guarantees zero cross-talk between different players/bosses.
		final List<int[]> activeCoords = new ArrayList<>();
		int x = player.getX();
		int y = player.getY();
		activeCoords.add(new int[] { x, y });

		for (int[] point : activeCoords) {
			int nX = npc.getX() + 2;
			int nY = npc.getY() + 2;
			int x1 = point[0] + 1;
			int y1 = point[1] + 2;
			int offY = (nX - x1) * -1;
			int offX = (nY - y1) * -1;
			if (startGfx > 0) {
				player.getPA().createPlayersProjectile(nX, nY, offX, offY, 40, getProjectileSpeed(npc.getIndex()), startGfx, 31, 0, -1, 5);
			}
		}

		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				for (int[] point : activeCoords) {
					if (endGfx > 0)
						player.getPA().createPlayersStillGfx(endGfx, point[0], point[1], player.getHeight(), 5);
				}
				container.stop();
			}
		}, 3);

		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				for (int[] point : activeCoords) {
					if (explosionGfx > 0)
						player.getPA().createPlayersStillGfx(explosionGfx, point[0], point[1], player.getHeight(), 5);
				}
				npc.attackType1 = CombatType.getRandom(CombatType.MELEE, CombatType.RANGE, CombatType.MAGE);
				container.stop();
			}
		}, time);
	}

	private void groundSpell(NPC npc, Player player, int startGfx, int endGfx, String type, int time) {
		if (player == null) return;

		// FIX: Fresh, isolated list so multiple boss instances don't overwrite each other
		final List<int[]> activeCoords = new ArrayList<>();

		if (Objects.equals(type, "spawns")) {
			// Changed to standard loop/stream to avoid modifying global collections
			if (Arrays.stream(NPCHandler.npcs).filter(Objects::nonNull).anyMatch(n -> n.npcType == 6768 && !n.isDead)) {
				return;
			}
		}

		int x = player.getX();
		int y = player.getY();
		activeCoords.add(new int[] { x, y });

		for (int i = 0; i < 2; i++) {
			activeCoords.add(new int[] { (x - 1) + Misc.random(3), (y - 1) + Misc.random(3) });
		}

		for (int[] point : activeCoords) {
			int nX = npc.getX() + 2;
			int nY = npc.getY() + 2;
			int x1 = point[0] + 1;
			int y1 = point[1] + 2;
			int offY = (nX - x1) * -1;
			int offX = (nY - y1) * -1;

			if (startGfx > 0) {
				player.getPA().createPlayersProjectile(nX, nY, offX, offY, 40, getProjectileSpeed(npc.getIndex()), startGfx, 31, 0, -1, 5);
			}
			if (Objects.equals(type, "spawns")) {
				spawnNpc(6768, point[0], point[1], 0, 0, -1, -1, -1, -1);
			}
		}

		if (Objects.equals(type, "spawns")) {
			CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					kill(6768, 0);
					container.stop();
				}
			}, 7);
		}

		CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
			@Override
			public void execute(CycleEventContainer container) {
				for (int[] point : activeCoords) {
					if (endGfx > 0) {
						player.getPA().createPlayersStillGfx(endGfx, point[0], point[1], player.getHeight(), 5);
					}
					if (Objects.equals(type, "cerberus")) {
						player.getPA().createPlayersStillGfx(1247, point[0], point[1], player.getHeight(), 5);
					}
				}
				container.stop();
			}
		}, time);
	}

	private boolean isZamorakNpc(int i) {
		switch (npcs[i].npcType) {
            case 6203:
            case 6204:
            case 6206:
            case 6208:
            case 6219:
            case 6218:
            case 6212:
            case 3248:
            case 6220:
            case 6221:
			return true;
		}
		return false;
	}
	
	private boolean isSaradominNpc(int i) {
		switch(npcs[i].npcType) {
            case 6247:
            case 6248:
            case 6250:
            case 6254:
            case 6252:
            case 6257:
            case 6255:
            case 6256:
            case 6258:
			return true;
		}
		return false;
	}

    private boolean isBandosNpc(int i) {
        switch(npcs[i].npcType) {
            case 6277:
            case 6269:
            case 6270:
            case 3247:
            case 6276:
            case 6272:
            case 6274:
            case 6278:
                return true;
        }
        return false;
    }   
	private boolean isBandosBossNpc(int i) {
        switch(npcs[i].npcType) {
            case 2215:
            case 2216:
            case 2217:
            case 2218:
                return true;
        }
        return false;
    }

    private boolean isArmadylNpc(int i) {
        switch(npcs[i].npcType) {
            case 6222:
            case 6223:
            case 6225:
            case 6230:
            case 6239: // Aviansie
            case 6227:
            case 6232:
            case 6229:
            case 6233:
            case 6231:
                return true;
        }
        return false;
    }
	/**
	 * Distanced required to attack
	 **/
    /**
	 * Distanced required to attack
	 **/
	public int distanceRequired(int i) {
		switch (npcs[i].npcType) {

		case Skotizo.SKOTIZO_ID:
			return npcs[i].attackType1 == CombatType.MAGE ? 15 : 2;
		case 7706:
		return 80;
		case 5087:
			return npcs[i].attackType1 == CombatType.MAGE ? 7 : 2;
			
		case 8028:
			return 10;
			
			/* Hydra */
		case 8609:
			return 6;
			
			/*xeric monsters*/
			case 7576://crabs
			case 7577:
			case 7578:
			case 7579:
				return npcs[i].attackType1 == CombatType.MELEE ? 1 : 2;
			case 7586://ice fiend
				return npcs[i].attackType1 == CombatType.MELEE ? 1 : 2;
			case 7585://ice demon
				return npcs[i].attackType1 == CombatType.MELEE ? 1 : 2;
		/**
		 * Cerberus
		 */
		case 5862:
		case 6766:
		case 7144:
		case 7145:
		case 7146:
			return npcs[i].attackType1 == CombatType.MELEE ? 1 : 7;

		case 5867:
		case 5868:
		case 5869:
		case 7617:
			return 30;

		case 7559:
		case 7560:
		return 10;
		
		case 5001:
			if (npcs[i].attackType1 == CombatType.MAGE) {
			return 6;
			} else 
				return 3;
		case 6477: //tarn
		case 5462:
			return 10;
			
		case 7604: // Skeletal mystic
		case 7605: // Skeletal mystic
		case 7606: // Skeletal mystic
		case 4922:
			return 8;

		case 319:
			return npcs[i].attackType1 == CombatType.MAGE ? 10 : 7;

		case 5890:
		case 7544:
		case 5129:
			return npcs[i].attackType1 == CombatType.MELEE ? 3 : 7;

		case 6914: // Lizardman, Lizardman brute
		case 6915:
		case 6916:
		case 6917:
		case 6918:
		case 6919:
			return npcs[i].attackType1 == CombatType.MAGE ? 4 : 1;

		case 6618:
			return npcs[i].attackType1 == CombatType.RANGE || npcs[i].attackType1 == CombatType.SPECIAL ? 4 : 1;

		case 465:
			return npcs[i].attackType1 == CombatType.RANGE || npcs[i].attackType1 == CombatType.SPECIAL ? 6 : 2;

		case 6615: // Scorpia
		case 6619: // Chaos fanatic
			return 4;

		case 6367:
		case 6368:
		case 6369:
		case 6371:
		case 6372:
		case 6373:
		case 6374:
		case 6375:
		case 6376:
		case 6377:
		case 6378:
			if (npcs[i].attackType1 == CombatType.MAGE || npcs[i].attackType1 == CombatType.RANGE)
				return 8;
			else
				return 4;

		case 6370:
			return 10;

		case 498:
		case 499:
			return 6;
		case 1672: // Ahrim
		case 1675: // Karil
		case 983: // Dagannoth mother
		case 984:
		case 985:
		case 987:
			return 8;

		case 986:
		case 988:
			return 3;

		case 2209:
		case 2211:
		case 2212:

		case 2242:
		case 2244:
		case 3160:
		case 3161:
		case 3162:
		case 3167:
		case 3168:
		case 3174:
			return 4;

		case 1610:
		case 1611:
		case 1612:
			return 4;

		case 2205:
			return npcs[i].attackType1 == CombatType.MAGE ? 3 : 2;
		case Zulrah.SNAKELING:
			return 2;
		case 2208:
			return 8;
		case 2217:
			return 9;
		case 2218:
			return 6;
		case 2042:
		case 2043:
		case 2044:
			case 7554:
			return 25;
		case 3163:
			return 8;
		case 3164:
		case 1049:
			return 5;
		case 6611:
		case 6612:
			return npcs[i].attackType1 == CombatType.SPECIAL || npcs[i].attackType1 == CombatType.MAGE ? 12 : 3;
		case 1046:
		case 6610:
			return 8;
		case 494:
		case 492:
		case 6609:
		case 5535:
			return 10;
		case 2025:
		case 2028:
			return 6;
		case 2562:
		case 3131:
		case 3132:
		case 3130:
		case 2206:
		case 2207:
		case 2267:
			return 2;
		case 2054:// chaos ele
		case 3125:
		case 3121:
		case 2167:
		case 3127:
			case 7700:
			return 8;
		case 3129:
			return 5;
		case 2265:// dag kings
		case 2266:
			return 4;
		case 239:
		case 8030:
		case 8031:
			return npcs[i].attackType1 == CombatType.DRAGON_FIRE ? 18 : 4;
		// things around dags
		case 2892:
		case 2894:
			return 10;
		default:
			return 1;
		}
	}

	public int followDistance(int i) {
		switch (npcs[i].npcType) {
		case 2550:
		case 2551:
		case 2562:
		case 2563:
			return 8;
		case 2883:
			return 4;
		case 2881:
		case 2882:
			return 1;
		case 2215:
		case 2216:
		case 2217:
		case 2218:
		return 5;
		}
		return 15;

	}

	public int getProjectileSpeed(int i) {
		//lower number = faster, higher = slower
		switch (npcs[i].npcType) {
		case 2881:
		case 2882:
		case 3200:
			return 85;

		case 2745:
			return 130;

		case 50:
			return 90;

		case 2025:
			return 85;

		case 172:
		case 174:
				return 110;
		case 2028:
			return 80;
		case 6263:
		case 6265:
		case 2215:
		return 120;
		default:
			return 85;
		}
	}

	/**
	 * NPC Attacking Player
	 **/
	public boolean goodDistanceNpc(int i, int x2, int y2, int distance) {
		for (int x = npcs[i].getX(); x <= npcs[i].getX() + getNpcSize(i); x++) {
			for (int y = npcs[i].getY(); y <= npcs[i].getY() + getNpcSize(i); y++) {
				if (Misc.goodDistance(x, y, x2, y2, distance)) {
					return true;
				}
			}
		}
		return false;
	}	
	private boolean prayerProtectionIgnored(int npcId) {
		switch (npcs[npcId].npcType) {
		case 1610:
		case 1611:
		case 1612:
		case 2205:
		case 2206:
		case 2207:
		case 2208:
		case 2215:
		case 5462:
		case 2216:
		case 2217:
		case 2218:
		case 3129:
		case 3130:
		case 3131:
		case 3132:
		case 3162:
		case 3163:
		case 3164:
		case 3165:
		case 7617:
			return true;
		case 1672:
			return false;
		case 6611:
		case 6612:
		case 6609:
			return npcs[npcId].attackType1 == CombatType.MAGE || npcs[npcId].attackType1 == CombatType.SPECIAL;
		case 7706:
			return npcs[npcId].attackType1 == CombatType.RANGE;
		case 465:
			return npcs[npcId].attackType1 == CombatType.DRAGON_FIRE;
		}
		return false;
	}
	public void attackPlayer(Player c, int i) {
		boolean face = true;
		NPC npc = npcs[i];
		CombatScript script = npc.getCombatScript();
		if (script != null) {
			if (script.handleAttack(npc, c))
				return;
		}
		if (npcs[i].lastX != npcs[i].getX() || npcs[i].lastY != npcs[i].getY()) {
			return;
		}
		if (npcs[i] != null) {
			if (npcs[i].isDead)
				return;
			if (!npcs[i].inMulti() && npcs[i].underAttackBy > 0 && npcs[i].underAttackBy != c.getIndex()) {
				npcs[i].killerId = 0;
				npcs[i].faceEntity(0);
				npcs[i].underAttack = false;
				npcs[i].randomWalk = true;
				return;
			}
			if (!npcs[i].inMulti() && ((c.underAttackBy > 0 && c.underAttackBy2 != i)
					|| (c.underAttackBy2 > 0 && c.underAttackBy2 != i))) {
				npcs[i].killerId = 0;
				npcs[i].faceEntity(0);
				npcs[i].underAttack = false;
				npcs[i].randomWalk = true;
				return;
			}
			if (npcs[i].getHeight() != c.getHeight()) {
				npcs[i].killerId = 0;
				npcs[i].faceEntity(0);
				npcs[i].underAttack = false;
				npcs[i].randomWalk = true;
				return;
			}
			npcs[i].faceEntity(c.getIndex());

			int distance = c.distanceToPoint(npcs[i].getX(), npcs[i].getY());
			boolean hasDistance = npcs[i].getDistance(c.getX(), c.getY()) <= ((double) distanceRequired(i)) + (npcs[i].getSize() > 1 ? 0.5 : 0.0);
			/**
			 * Npc's who will ignore projectile clipping
			 */
			if (ignoresProjectile(i)) {
				if (distance < 10) {
					c.getPA().removeAllWindows();
					npcs[i].oldIndex = c.getIndex();
					c.underAttackBy2 = i;
					c.singleCombatDelay2 = System.currentTimeMillis();
					npcs[i].attackTimer = getNpcDelay(i);
					npcs[i].hitDelayTimer = getHitDelay(i);
					loadSpell(c, i);
					//System.out.println("[player-");
					startAnimation(getAttackEmote(i), i);
					c.getPA().sendNPCSound(getAttackSound(i), 0, 6, c.EffectVolume);
				}
			}
			if (hasDistance) {
				if (projectileClipping) {
					if (npcs[i].attackType1 == CombatType.MAGE || npcs[i].attackType1 == CombatType.RANGE) {
						int x1 = npcs[i].getX();
						int y1 = npcs[i].getY();
						int z = npcs[i].getHeight();
						if (!PathChecker.isProjectilePathClear(x1, y1, z, c.getX(), c.getY())
								&& !PathChecker.isProjectilePathClear(c.getX(), c.getY(), z, x1, y1)) {
							return;
						}
					}
				}

				if (c.respawnTimer <= 0) {
					//System.out.println("[player-");
					/**
					 * Npcs who follow projectile clipping
					 */
					npcs[i].attackTimer = getNpcDelay(i);
					npcs[i].hitDelayTimer = getHitDelay(i);
					if (npcs[i].attackType1 == null) {
						npcs[i].attackType1 = CombatType.MELEE;
					}
					loadSpell(c, i);
					npcs[i].oldIndex = c.getIndex();
					c.underAttackBy2 = i;
					c.singleCombatDelay2 = System.currentTimeMillis();
					startAnimation(getAttackEmote(i), i);
					c.getPA().sendNPCSound(getAttackSound(i), 0, 3, c.EffectVolume);
					c.getPA().removeAllWindows();
					if (npcs[i].attackType1 == CombatType.DRAGON_FIRE) {
						npcs[i].hitDelayTimer += 2;
						c.getCombat().absorbDragonfireDamage();
					}
					if (multiAttacks(i)) {
						startAnimation(getAttackEmote(i), i);
						multiAttackGfx(i, npcs[i].projectileId);
						npcs[i].oldIndex = c.getIndex();
						return;
					}
					if (npcs[i].projectileId > 0) {
						/*if(npcs[i].npcType == 7706) {
							NPC glyph = getNpc(7707,c.getHeight());
							if (glyph == null){
								return;
							}

							//if (c.getInferno().isBehindGlyph()) {
							//	c.getInferno().behindGlyph=true;
								//c.sendMessage("is behind glyph");
							//}
						}*/
						int nX = NPCHandler.npcs[i].getX() + offset(i);
						int nY = NPCHandler.npcs[i].getY() + offset(i);
						int pX = c.getX();
						int pY = c.getY();
						//c.sendMessage(pX + " "+ pY);
						int offX = (nX - pX) * -1;
						int offY = (nY - pY) * -1;
						int centerX = nX + npcs[i].getSize() / 2;
						int centerY = nY + npcs[i].getSize() / 2;
						/*if (npcs[i].npcType == 8028 && c.getVorkath().attackType == 6) {// VORKATH BIG FIRE ATTACK
							int endCoordX = c.getVorkath().playerX;
							int endCoordY = c.getVorkath().playerY;
							int targetY = (nX - (int) endCoordX) * -1;
							int targetX = (nY - (int) endCoordY) * -1;
							c.getPA().createPlayersProjectile2(centerX, centerY, targetX, targetY, 50, 150,
									npcs[i].projectileId,
									getProjectileStartHeight(npcs[i].npcType, npcs[i].projectileId),
									getProjectileEndHeight(npcs[i].npcType, npcs[i].projectileId), -1,
									65, npcs[i].getProjectileDelay()-100);
							return;
						}*/
							/*if (npcs[i].npcType == 8028 &&c.getVorkath().attackType == 7) {// VORKATH quick FIRE ATTACK
								int endCoordX = c.getVorkath().playerX;
								int endCoordY = c.getVorkath().playerY;
								int targetY = (nX - (int) endCoordX) * -1;
								int targetX = (nY - (int) endCoordY) * -1;
								c.getPA().createPlayersProjectile2(centerX, centerY, targetX, targetY, 50, 80,
										npcs[i].projectileId,
										getProjectileStartHeight(npcs[i].npcType, npcs[i].projectileId),
										getProjectileEndHeight(npcs[i].npcType, npcs[i].projectileId), -1,
										65, npcs[i].getProjectileDelay());
						
								return;
						}*/
							c.getPA().createPlayersProjectile(centerX, centerY, offX, offY, 50, getProjectileSpeed(i),
									npcs[i].projectileId, getProjectileStartHeight(npcs[i].npcType, npcs[i].projectileId),
									getProjectileEndHeight(npcs[i].npcType, npcs[i].projectileId), -c.getIndex() - 1, 65,
									npcs[i].getProjectileDelay());
					}
					if (c.teleporting) {
						c.startAnimation(65535);
						c.teleporting = false;
						c.isRunning = false;
						c.gfx0(-1);
						c.startAnimation(-1);
					}
				}
			}
		}
	}
	/**
	 * Npcs who ignores projectile clipping to ensure no safespots
	 * 
	 * @param i
	 * @return true is the npc is using range, mage or special
	 */
	public static boolean ignoresProjectile(int i) {
		if (npcs[i] == null)
			return false;
		switch (npcs[i].npcType) {
		case 6611:
		case 6612:
		case 319:
		case 6618:
		case 6766:
		case 5862:
		case 963:
		case 965:
		case 7706:
		case 7144:
		case 8028:
		case 7145:
		case 7146:
		case 5890:
		case 8609:
		case 7566:
		case 7563:
		case 7585:
		case 7544:
		case 7554:
			return true;
		}
		return false;
	}
	/**
	 * Projectile start height
	 * 
	 * @param npcType
	 *            the npc to perform the projectile
	 * @param projectileId
	 *            the projectile to be performed
	 * @return
	 */
	private int getProjectileStartHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 2044:
			return 60;
		case 3162:
			return 0;
		case 3127:
		case 3163:
			case 7700:
		case 3164:
		case 3167:
		case 3174:
			return 110;
		case 6610:
			switch (projectileId) {
			case 1998:
			case 165:
				return 20;
			}
			break;
		}
		return 43;
	}

	/**
	 * Projectile end height
	 * 
	 * @param npcType
	 *            the npc to perform the projectile
	 * @param projectileId
	 *            the projectile to be performed
	 * @return
	 */
	private int getProjectileEndHeight(int npcType, int projectileId) {
		switch (npcType) {
		case 1605:
			return 0;
		case 3162:
			return 15;
		case 6610:
			switch (projectileId) {
			case 165:
				return 30;
			case 1996:
				return 0;
			}
			break;
		}
		return 31;
	}
	public int offset(int i) {
		switch (npcs[i].npcType) {
			case 2215:
			return 2;
		case 50:
			return 2;
		
		}
		return 0;
	}

	public boolean specialCase(Player c, int i) { // responsible for npcs that
													// much
		if (goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(), c.getY(), 8)
				&& !goodDistance(npcs[i].getX(), npcs[i].getY(), c.getX(),
						c.getY(), distanceRequired(i)))
			return true;
		return false;
	}

	public boolean retaliates(int npcType) {
		return npcType < 3777 || npcType > 3780
				&& !(npcType >= 2440 && npcType <= 2446);
	}

	public void applyDamage(int i) {
		if (npcs[i] != null) {
			if (PlayerHandler.players[npcs[i].oldIndex] == null) {
				return;
			}
			if (npcs[i].isDead)
				return;
			if (npcs[i].npcType >= 1739 && npcs[i].npcType <= 1742 || npcs[i].npcType == 7413) {
				return;
			}
			Player c = (Player) PlayerHandler.players[npcs[i].oldIndex];
			if (multiAttacks(i)) {
				multiAttackDamage(i);
				return;
			}
			if (c.playerIndex <= 0 && c.npcIndex <= 0) {
				if (c.autoRet == 1 && !c.getMovementQueue().hasSteps()) {
					c.npcIndex = i;
				}
			}
			if (c.attackTimer <= 4) {
				if (!NPCHandler.isFightCaveNpc(i) && npcs[i].npcType != 319) {
					c.startAnimation(c.getCombat().getBlockEmote());
					c.getPA().sendSound(c.getCombat().getDefenceSound(c.playerEquipment[c.playerShield]), 0, 0, c.EffectVolume);
				}
			}
			npcs[i].totalAttacks++;
			boolean protectionIgnored = prayerProtectionIgnored(i);
			if (c.respawnTimer <= 0) {
				int damage = 0;
				int secondDamage = -1;

				Optional<Brother> activeBrother = c.getBarrows().getActive();
				if (npcs[i].attackType1 != null) {
					switch (npcs[i].attackType1) {
					case MELEE:
					damage = Misc.random(getMaxHit(i));
					/**
					 * Calculate defence
					 */
					if (10 + Misc.random(c.getCombat().calculateMeleeDefence()) > Misc
							.random(NPCHandler.npcs[i].attack)) {
						damage = 0;
					}

					if (npcs[i].npcType == 5869) {
						damage = !c.protectingMelee() ? 30 : 0;
						c.getSkills().decreaseLevelOrMin(c.protectingMelee() && !c.getItems().isWearingItem(12821) ? 30
								: c.protectingMelee() && c.getItems().isWearingItem(12821) ? 15 : 0, 0, Skill.PRAYER);
						c.getSkills().sendRefresh();
					}

					/**
					 * Zulrah
					 */
					if (npcs[i].npcType == 2043 && c.getZulrahEvent().getNpc() != null
							&& c.getZulrahEvent().getNpc().equals(npcs[i])) {
						Boundary boundary = new Boundary(npcs[i].targetedLocation.getX(),
								npcs[i].targetedLocation.getY(), npcs[i].targetedLocation.getX(),
								npcs[i].targetedLocation.getY());
						if (!Boundary.isIn(c, boundary)) {
							return;
						}
						damage = 20 + Misc.random(25);
					}

					/**
					 * Special attacks
					 */
					if (activeBrother.isPresent() && activeBrother.get().getId() == npcs[i].npcType) {
						double random = Math.random();
						if (random <= Barrows.SPECIAL_CHANCE) {
							switch (activeBrother.get().getId()) {
							case Brother.DHAROK:
								double healthRatio = Math.round(
										(npcs[i].getHealth().getAmount() / npcs[i].getHealth().getMaximum()) * 10)
										/ 10d;
								healthRatio = Double.max(0.1, healthRatio);
								damage *= -2 * healthRatio + 3;
								break;
							case Brother.GUTHAN:
								int addedHealth = c.protectingMelee() ? 0
										: Integer.min(damage,
												npcs[i].getHealth().getMaximum() - npcs[i].getHealth().getAmount());
								if (addedHealth > 0) {
									c.gfx0(398);
									npcs[i].getHealth().increase(addedHealth);
								}
								break;

							case Brother.TORAG:
								c.gfx0(399);
								break;

							case Brother.VERAC:
								protectionIgnored = true;
								damage /= 2;
								break;

							}
						}
					}

					/**
					 * Protection prayer
					 */
					if (c.protectingMelee() && !protectionIgnored) {
						if (npcs[i].npcType == 5890)
							damage /= 3;
						else if (npcs[i].npcType == 963 || npcs[i].npcType == 965 || npcs[i].npcType == 8349
								|| npcs[i].npcType == 8133 || npcs[i].npcType == 6342 || npcs[i].npcType == 2054
								|| npcs[i].npcType == 239
								|| npcs[i].npcType == 998 || npcs[i].npcType == 999
								|| npcs[i].npcType == 1000 || npcs[i].npcType == 7554 || npcs[i].npcType == 319
								|| npcs[i].npcType == 320 || npcs[i].npcType == 6615 || npcs[i].npcType == 5916
								|| npcs[i].npcType == 7544 || npcs[i].npcType == 5129)
							damage /= 2;
						else
							damage = 0;
					} else if (c.protectingMelee() && protectionIgnored) {
						damage /= 2;
					}
					
					if (c.protectingRange() && !protectionIgnored) {
						if (npcs[i].npcType == 7706)
							damage /= 4;
						if (npcs[i].npcType == 7554)
							damage /= 2;
					}

					/**
					 * Specials and defenders
					 */
					if (World.getWorld().getEventHandler().isRunning(c, "staff_of_the_dead")) {
						Special special = Specials.STAFF_OF_THE_DEAD.getSpecial();
						Damage d = new Damage(damage);
						special.hit(c, npcs[i], d);
						damage = d.getAmount();
					}
					if (c.playerEquipment[c.playerShield] == 12817) {//ely
						if (Misc.random(100) > 30 && damage > 0) {
							damage *= .75;
							c.gfx100(321);
						}
					}
					
					
					
					

							
					//Bracelet of ethereum
					 if (c.getItems().isWearingItem(21816)) {
						if (c.ethereumCharge <= 0) {
							c.getItems().deleteEquipment(21816, 9);
							c.getItems().wearItem(21817, 1, 9);
						} else if (Misc.linearSearch(Config.REV_IDS, npcs[i].npcType) != -1) {
							c.ethereumCharge--;
							c.appendDamage(0, Hitmark.MISS);
							return;
						}
					}
					
					/*
					 * justiciar set def buff
					 */
					
					  if (c.playerEquipment[c.playerChest] == 22327 && c.playerEquipment[c.playerHat] == 22326 && c.playerEquipment[c.playerLegs] == 22328) {
                                damage *= .80;
                        }
					if (c.getHealth().getAmount() - damage < 0) {
						damage = c.getHealth().getAmount();
					}
					break;
					case RANGE: // range
						//Bracelet of ethereum
						 if (c.getItems().isWearingItem(21816)) {
							if (c.ethereumCharge <= 0) {
								c.getItems().deleteEquipment(21816, 9);
								c.getItems().wearItem(21817, 1, 9);
							} else if (Misc.linearSearch(Config.REV_IDS, npcs[i].npcType) != -1) {
								c.ethereumCharge--;
								c.appendDamage(0, Hitmark.MISS);
								return;
							}
						}
						damage = Misc.random(npcs[i].maxHit);
						switch (npcs[i].npcType) {
						case 6377:
							secondDamage = Misc.random(getMaxHit(i));
							break;
							
						case 8028:// VORKATH range hits will hit you no matter what
							//c.getVorkath().executeAttack(c, 5);// executes vorkaths range attack
							return;
							
						case 6371: //Karamel ranged drain
							int[] skills = {0, 1, 2, 4, 6};
							int skill = skills[Misc.random(skills.length - 1)];
							int drain = Misc.random((int) (getMaxHit(i) / 2));
							c.getSkills().decreaseLevelOrMin(drain, Skill.forId(skill));
							c.getPA().refreshSkill(skill);
							break;

						/**
						 * Summoned soul range
						 */
						// case 5867:
						// damage = !c.protectingRange() ? 30 : 0;
						// player.getSkills().getLevel(Skill.PRAYER) -= c.protectingRange() || c.protectingRange() &&
						// c.getItems().isWearingItem(12821) ? 30 : 0;
						// if (player.getSkills().getLevel(Skill.PRAYER) < 0) {
						// player.getSkills().getLevel(Skill.PRAYER) = 0;
						// }
						// break;
						}

						/**
						 * Range defence
						 */
						if (10 + Misc.random(c.getCombat().calculateRangeDefence()) > Misc.random(NPCHandler.npcs[i].attack)) {
							damage = 0;
						}

						if (npcs[i].npcType == 5867) {
							damage = !c.protectingRange() ? 30 : 0;
							c.getSkills().decreaseLevelOrMin(c.protectingMelee() && !c.getItems().isWearingItem(12821) ? 30
									: c.protectingMelee() && c.getItems().isWearingItem(12821) ? 15 : 0, 0, Skill.PRAYER);
						
						}

						/**
						 * Special attacks
						 */
						if (activeBrother.isPresent() && activeBrother.get().getId() == npcs[i].npcType) {
							double random = Math.random();
							if (random <= Barrows.SPECIAL_CHANCE) {
								switch (activeBrother.get().getId()) {
								case Brother.KARIL:
									c.getSkills().setLevelOrMin((int) (c.getSkills().getLevel(Skill.AGILITY) * 0.8), Skill.AGILITY);
									c.getPA().refreshSkill(Skill.AGILITY.getId());
									c.gfx0(401);
									break;
								}
							}
						}
						/**
						 * Protection prayer
						 */
						if (c.protectingRange() && !protectionIgnored) {
							if (npcs[i].npcType == 963 || npcs[i].npcType == 965 || npcs[i].npcType == 8349
									|| npcs[i].npcType == 8133 || npcs[i].npcType == 6342 || npcs[i].npcType == 2054
									|| npcs[i].npcType == 7554 || npcs[i].npcType == 239 || npcs[i].npcType == 8027 
									|| npcs[i].npcType == 319
									|| npcs[i].npcType == 499) {
								damage /= 2;
							} else {
								damage = 0;
							}
							if (c.getHealth().getAmount() - damage < 0) {
								damage = c.getHealth().getAmount();
							}
						} else if (c.protectingRange() && protectionIgnored) {
							damage /= 2;
						}
						if (npcs[i].npcType == 2042 || npcs[i].npcType == 2044) {
							c.getHealth().proposeStatus(HealthStatus.VENOM, 6, Optional.of(npcs[i]));
						}
						if (npcs[i].endGfx > 0 || npcs[i].npcType == 3127) {
							c.gfx100(npcs[i].endGfx);
						}
						if (npcs[i].endGfx > 0 || npcs[i].npcType == 7700) {
							c.gfx100(npcs[i].endGfx);
						}
						break;
					case MAGE:// magic
						if (c.getItems().isWearingItem(21816)) {
							if (c.ethereumCharge <= 0) {
								c.getItems().deleteEquipment(21816, 9);
								c.getItems().wearItem(21817, 1, 9);
							} else if (Misc.linearSearch(Config.REV_IDS, npcs[i].npcType) != -1) {
								c.ethereumCharge--;
								c.appendDamage(0, Hitmark.MISS);
								return;
							}
						}
						if(npcs[i].npcType == 6477) {
							Tarn.performFreeze();
							return;
						}
						damage = Misc.random(npcs[i].maxHit);
						boolean magicFailed = false;
						/**
						 * Attacks
						 */
						switch (npcs[i].npcType) {
						case 6373:
						case 6375:
						case 6376:
						case 6378:
							secondDamage = Misc.random(getMaxHit(i));
							break;
						case 6477:
							Tarn.performFreeze();
							break;
						case 8028:// if player is using protect from mage or not against vorkath instant damage
							if (c.protectingMagic() == true) {//protects players from vorkaths magic attacks
								damage = 0;
							}
							//c.getVorkath().executeAttack(c, c.getVorkath().attackType);//exectues effects
							break;

						/**
						 * Summoned soul mage
						 */
						// case 5868:
						// damage = !c.protectingMagic() ? 30 : 0;
						// player.getSkills().getLevel(Skill.PRAYER) -= c.protectingMagic() || c.protectingMagic() &&
						// c.getItems().isWearingItem(12821) ? 30 : 0;
						// if (player.getSkills().getLevel(Skill.PRAYER) < 0) {
						// player.getSkills().getLevel(Skill.PRAYER) = 0;
						// }
						// break;

						case 6371: // Karamel
							c.freezeTimer = 4;
							break;

						case 2205:
							secondDamage = Misc.random(27);
							break;

						case 6609:
							c.sendMessage("Callisto's fury sends an almighty shockwave through you.");
							break;
						}

						/**
						 * Magic defence
						 */
						if (10 + Misc.random(c.getCombat().mageDef()) > Misc.random(NPCHandler.npcs[i].attack)) {
							damage = 0;
							if (secondDamage > -1) {
								secondDamage = 0;
							}
							magicFailed = true;
						}

						if (npcs[i].npcType == 5868) {
							damage = !c.protectingMagic() ? 30 : 0;
							c.getSkills().decreaseLevelOrMin(c.protectingMagic() && !c.getItems().isWearingItem(12821) ? 30
									: c.protectingMagic() && c.getItems().isWearingItem(12821) ? 15 : 0, 0, Skill.PRAYER);
							
						}
						/**
						 * Protection prayer
						 */
						if (c.protectingMagic() && !protectionIgnored) {
							switch (npcs[i].npcType) {
							case 494:
							case 492:
							case 5535:
								int max = npcs[i].npcType == 494 ? 2 : 0;
								if (Misc.random(2) == 0) {
									damage = 1 + Misc.random(max);
								} else {
									damage = 0;
									if (secondDamage > -1) {
										secondDamage = 0;
									}
								}
								break;

							case 1677:
							case 963:
							case 965:
							case 8349:
							case 8133:
							case 6342:
							case 2054:
							case 239:
							case 8027:
							case 1046:
							case 319:
							case 7554:
							case 7604: // Skeletal mystic
							case 7605: // Skeletal mystic
							case 7606: // Skeletal mystic
							case 7617:
							case 4922:
								damage /= 2;
								break;
								
							case 3822:
								if (Misc.random(1, 5) == 3) {
									damage /= 3;
								}

							default:
								damage = 0;
								if (secondDamage > -1) {
									secondDamage = 0;
								}
								magicFailed = true;
								break;

							}

						} else if (c.protectingMagic() && protectionIgnored) {
							damage /= 2;
						}
						if (c.getHealth().getAmount() - damage < 0) {
							damage = c.getHealth().getAmount();
						}
						if (npcs[i].endGfx > 0 && (!magicFailed || isFightCaveNpc(i))) {
							c.gfx100(npcs[i].endGfx);
						} else {
							c.gfx100(85);
						}
						c.getCombat().appendVengeance(damage + (secondDamage > 0 ? secondDamage : 0), i);
						
						break;
					case ACID:
						break;
					case CANNON:
						break;
						/**
						 * Handles npcs who are dealing dragon fire based attacks
						 */
						case DRAGON_FIRE:
							int resistance = c.getItems().isWearingItem(1540) || c.getItems().isWearingItem(33115) || c.getItems().isWearingItem(11283)
									|| c.getItems().isWearingItem(11284) ? 1 : 0;
							if (System.currentTimeMillis() - c.lastAntifirePotion < c.antifireDelay) {
								resistance++;
							}
							if (System.currentTimeMillis() - c.lastSuperAntifirePotion < c.SuperantifireDelay) {
								resistance = 2;
							}
							if (resistance == 0) {
								damage = Misc.random(getMaxHit(i));
								if (npcs[i].npcType == 465 || npcs[i].npcType == 7795 || npcs[i].npcType == 7794 || npcs[i].npcType == 7793 || npcs[i].npcType == 7792) {
									c.sendMessage("You are badly burnt by the cold breeze!");
								} else {
									c.sendMessage("You are badly burnt by the dragon fire!");
								}
							} else if (resistance == 1) {
								damage = Misc.random(15);
							} else if (resistance == 2) {
								damage = 0;
							}
							if (npcs[i].endGfx != 430 && resistance == 2) {
								damage = 5 + Misc.random(5);
							}

							/**
							 * Attacks
							 */
							switch (npcs[i].endGfx) {
							case 429:
								c.getHealth().proposeStatus(HealthStatus.POISON, 6, Optional.of(npcs[i]));
								break;

							case 163:
								c.freezeTimer = 15;
								c.sendMessage("You have been frozen to the ground.");
								break;

							case 428:
								c.freezeTimer = 10;
								break;

							case 431:
								c.lastSpear = System.currentTimeMillis();
								break;
							}
							if (c.getHealth().getAmount() - damage < 0)
								damage = c.getHealth().getAmount();
							c.gfx100(npcs[i].endGfx);

							c.getCombat().appendVengeanceNPC(damage + 0, i);
							break;
					case SPECIAL:
						damage = Misc.random(getMaxHit(i));

						/**
						 * Attacks
						 */
						switch (npcs[i].npcType) {
						case 3129:
							int prayerReduction = c.getSkills().getLevel(Skill.PRAYER) / 2;
							if (prayerReduction < 1) {
								break;
							}
							c.getSkills().decreaseLevelOrMin(prayerReduction, 0, Skill.PRAYER);
							c.getPA().refreshSkill(5);
							c.sendMessage(
									"K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
							break;
						case 1046:
						case 1049:
							prayerReduction = c.getSkills().getLevel(Skill.PRAYER) / 10;
							if (prayerReduction < 1) {
								break;
							}
							c.getSkills().decreaseLevelOrMin(prayerReduction, 0, Skill.PRAYER);
							c.getPA().refreshSkill(5);
							c.sendMessage("Your prayer has been drained drastically.");
							break;
						case 6609:
							damage = 3;
							c.gfx0(80);
							c.lastSpear = System.currentTimeMillis();
							c.getPA().getSpeared(npcs[i].getX(), npcs[i].getY(), 3);
							c.sendMessage("Callisto's roar sends your backwards.");
							break;
						case 6610:
							if (c.protectingMagic()) {
								damage *= .7;
							}
							secondDamage = Misc.random(getMaxHit(i));
							if (secondDamage > 0) {
								c.gfx0(80);
							}
							break;

						case 465:
							c.freezeTimer = 15;
							c.sendMessage("You have been frozen.");
							break;

						case 7144:
						case 7145:
						case 7146:
							if (gorillaBoulder.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								return;
							}
							break;

						case 5890:
							if (damage > 0 && Misc.random(2) == 0) {
								if (npcs[i].getHealth().getStatus() == HealthStatus.POISON) {
									c.getHealth().proposeStatus(HealthStatus.POISON, 15, Optional.of(npcs[i]));
								}
							}
							if (gorillaBoulder.stream().noneMatch(p -> p[0] == c.getX() && p[1] == c.getY())) {
								return;
							}
							break;
						}
						break;
					}

					if (npcs[i].npcType == 320) {
						int distanceFromTarget = c.distanceToPoint(npcs[i].getX(), npcs[i].getY());

						if (distanceFromTarget <= 1) {
							NPC corp = NPCHandler.getNpc(319);
							Damage heal = new Damage(
									damage + Misc.random(15 + 5) + (secondDamage > 0 ? secondDamage : 0));
							if (corp != null && corp.getHealth().getAmount() < 2000) {
								corp.getHealth().increase(heal.getAmount());
							}
						}
					}
					if (npcs[i].npcType == 6617 || npcs[i].npcType == 6616 || npcs[i].npcType == 6615) {
						int distanceFromTarget = c.distanceToPoint(npcs[i].getX(), npcs[i].getY());

						List<NPC> healer = Arrays.asList(NPCHandler.npcs);

						if (distanceFromTarget <= 1 && Scorpia.stage > 0 && healer.stream().filter(Objects::nonNull)
								.anyMatch(n -> n.npcType == 6617 && !n.isDead && n.getHealth().getAmount() > 0)) {
							NPC scorpia = NPCHandler.getNpc(6615);
							Damage heal = new Damage(
									damage + Misc.random(20 + 5) + (secondDamage > 0 ? secondDamage : 0));
							if (scorpia != null && scorpia.getHealth().getAmount() < 150) {
								scorpia.getHealth().increase(heal.getAmount());
							}
						}
					}

					if (npcs[i].endGfx > 0) {
						c.gfx100(npcs[i].endGfx);
					}
					int poisonDamage = getPoisonDamage(npcs[i]);
					if (poisonDamage > 0 && Misc.random(10) == 1) {
						c.getHealth().proposeStatus(HealthStatus.POISON, poisonDamage, Optional.of(npcs[i]));
					}
					if (damage < npcs[i].minHit) {
						damage = npcs[i].minHit;
					}
					playerDamage(c, i, damage, secondDamage);
					switch (npcs[i].npcType) {
					// Abyssal sire
					case 5890:
						int health = npcs[i].getHealth().getAmount();
						c.sireHits++;
						int randomAmount = Misc.random(5);
						switch (c.sireHits) {
						case 10:
						case 20:
						case 30:
						case 40:
							for (int id = 0; id < randomAmount; id++) {
								int x = npcs[i].getX() + Misc.random(2);
								int y = npcs[i].getY() - Misc.random(2);
								newNPC(5916, x, y, 0, 0, 15, 15, 100, 0, null);
							}
							break;

						case 45:
							c.sireHits = 0;
							break;

						}
						if (health < 400 && health > 329 || health < 100) {
							npcs[i].attackType1 = CombatType.MELEE;
						}
						if (health < 330 && health > 229) {
							npcs[i].attackType1 = CombatType.MAGE;
						}
						if (health < 230 && health > 99) {
							npcs[i].attackType1 = CombatType.SPECIAL;
							npcs[i].getHealth().increase(6);
						}
						break;

					/**
					 * Demonic Gorillas attack
					 */

					/*
					 * case 7554: npcs[i].attackType = CombatType.MAGE; npcs[i].projectileId = 970;
					 * npcs[i].endGfx = 971; npcs[i].hitDelayTimer = 3; break;
					 */
					case 7144:
					case 7145:
					case 7146:
						if (damage == 0) {
							if (c.totalMissedGorillaHits >= 6) {
								c.totalMissedGorillaHits = 0;
							}
							c.totalMissedGorillaHits++;
						}
						if (c.totalMissedGorillaHits == 6) {
							c.totalMissedGorillaHits = 0;

							switch (npcs[i].attackType1) {
							case MELEE:
								switch (Misc.random(2)) {
								case 0:
									npcs[i].attackType1 = CombatType.MAGE;
									break;
								case 1:
									npcs[i].attackType1 = CombatType.SPECIAL;
									break;
								case 2:
									npcs[i].attackType1 = CombatType.RANGE;
									break;
								}
								break;
							case MAGE:
								switch (Misc.random(2)) {
								case 0:
									npcs[i].attackType1 = CombatType.MELEE;
									break;
								case 1:
									npcs[i].attackType1 = CombatType.SPECIAL;
									break;
								case 2:
									npcs[i].attackType1 = CombatType.RANGE;
									break;
								}
								break;
							case RANGE:
								switch (Misc.random(2)) {
								case 0:
									npcs[i].attackType1 = CombatType.MAGE;
									break;
								case 1:
									npcs[i].attackType1 = CombatType.SPECIAL;
									break;
								case 2:
									npcs[i].attackType1 = CombatType.MELEE;
									break;
								}
								break;
							case SPECIAL:
								switch (Misc.random(2)) {
								case 0:
									npcs[i].attackType1 = CombatType.MAGE;
									break;
								case 1:
									npcs[i].attackType1 = CombatType.MELEE;
									break;
								case 2:
									npcs[i].attackType1 = CombatType.RANGE;
									break;
								}
								break;

							default:
								break;
							}
							break;
						}
						c.updateRequired = true;
					}
				
				}
			}

		}
	}
	private int getPoisonDamage(NPC npc) {
		switch (npc.npcType) {
		case 3129:
			return 16;

		case 3021:
			return 5;

		case 957:
			return 4;

		case 959:
			return 6;

		case 6615:
			return 10;
		}
		return 0;
	}
	public void playerDamage(Player c, int i,  int damage, int secondDamage) {
		if (c.getHealth().getAmount() - damage < 0
				|| secondDamage > -1 && c.getHealth().getAmount() - secondDamage < 0) {
			damage = c.getHealth().getAmount();
			if (secondDamage > -1) {
				secondDamage = 0;
			}
		}
		handleSpecialEffects(c, i, damage);
		c.logoutDelay = System.currentTimeMillis();
		if (damage > -1) {
			//System.out.println("NPC hit player for: " + damage + " -> Hitmark: " + (damage > 0 ? "HIT" : "MISS"));

			c.appendDamage(damage, damage > 0 ? Hitmark.HIT : Hitmark.MISS);
			c.addDamageTaken(npcs[i], damage);
		}
		if (secondDamage > -1) {
			c.appendDamage(secondDamage, secondDamage > 0 ? Hitmark.HIT : Hitmark.MISS);
			c.addDamageTaken(npcs[i], secondDamage);
		}
		if (damage > 0 || secondDamage > 0) {
			c.getCombat().appendVengeanceNPC(damage + (secondDamage > 0 ? secondDamage : 0), i);
			c.getCombat().applyRecoilNPC(damage + (secondDamage > 0 ? secondDamage : 0), i);
		}
		int rol = c.getHealth().getAmount() - damage;
		if (rol > 0 && rol < c.getHealth().getMaximum() / 10) {
			ringOfLife(c);
		}
	}
	public void handleSpecialEffects(Player c, int i, int damage) {
		if (npcs[i].npcType == 2892 || npcs[i].npcType == 2894) {
			if (damage > 0) {
				if (c != null) {
					if (c.getSkills().getLevel(Skill.PRAYER) > 0) {
						c.getSkills().decreaseLevelOrMin(1, 0, Skill.PRAYER);
						c.getPA().refreshSkill(5);
					}
				}
			}
		}

	}
	public static boolean ringOfLife(Player c) {
		boolean defenceCape = SkillcapePerks.DEFENCE.isWearing(c);
		boolean maxCape = SkillcapePerks.isWearingMaxCape(c);
		if (c.getItems().isWearingItem(2570) || defenceCape || (maxCape && c.getRingOfLifeEffect())) {
			if (System.currentTimeMillis() - c.teleBlockDelay < c.teleBlockLength) {
				c.sendMessage("The ring of life effect does not work as you are teleblocked.");
				return false;
			}
			if (defenceCape || maxCape) {
				c.sendMessage("Your cape activated the ring of life effect and saved you!");
			} else {
				c.getItems().deleteEquipment(2570, c.playerRing);
				c.sendMessage("Your ring of life saved you!");
			}
			c.getPA().spellTeleport(3087, 3499, 0);
			return true;
		}
		return false;
	}

	private int multiAttackDistance(NPC npc) {
		if (npc == null) {
			return 0;
		}
		switch (npc.npcType) {
		case 239:
		case 8031:
		case 8030:
			return 35;
		case 7554:
			return 30;
		}
		return 15;
	}

	public static void startAnimation(int animId, int i) {
		npcs[i].animNumber = animId;
		npcs[i].animUpdateRequired = true;
		npcs[i].updateRequired = true;
	}

	public boolean goodDistance(int objectX, int objectY, int playerX,
			int playerY, int distance) {
		for (int i = 0; i <= distance; i++) {
			for (int j = 0; j <= distance; j++) {
				if ((objectX + i) == playerX
						&& ((objectY + j) == playerY
								|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if ((objectX - i) == playerX
						&& ((objectY + j) == playerY
								|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				} else if (objectX == playerX
						&& ((objectY + j) == playerY
								|| (objectY - j) == playerY || objectY == playerY)) {
					return true;
				}
			}
		}

		return false;
	}
	public boolean inNpc(int i, int x2, int y2) {
		if (offset(i) < 1) {
			if (x2 == NPCHandler.npcs[i].getX() && y2 == NPCHandler.npcs[i].getY()) {
				return true;
			}
		} else {
			for (int x = NPCHandler.npcs[i].getX(); x <= NPCHandler.npcs[i].getX() + getNpcSize(i); x++) {
				for (int y = NPCHandler.npcs[i].getY(); y <= NPCHandler.npcs[i].getY() + getNpcSize(i); y++) {
					if (x2 == x && y2 == y) {
						return true;
					}
				}
			}
		}
		return false;
	}
	public int getMaxHit(int i) {
		switch (npcs[i].npcType) {
		case 2558:
			if (npcs[i].attackType1 == CombatType.RANGE)
				return 28;
			else
				return 68;
		case 2562:
			return 31;
		case 2550:
			return 36;
		case Skotizo.SKOTIZO_ID:
			return 38;
		case Skotizo.AWAKENED_ALTAR_NORTH:
		case Skotizo.AWAKENED_ALTAR_SOUTH:
		case Skotizo.AWAKENED_ALTAR_WEST:
		case Skotizo.AWAKENED_ALTAR_EAST:
			return 15;
		case Skotizo.REANIMATED_DEMON:
		case Skotizo.DARK_ANKOU:
			return 8;
		}
		return npcs[i].maxHit;
	}

	

	public int getNpcListHP(int npcId) {
		if (npcId <= -1) {
			return 0;
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return 0;
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcHealth();

	}


	public String getNpcListName(int npcId) {
		if (npcId <= -1) {
			return "?";
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return "?";
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcName();
	}
	/*public int getNpcListAttackAnimation(int npcId) {
		for (int i = 0; i < maxListedNPCs; i++) {
			if (NpcList[i] != null) {
				if (NpcList[i].npcId == npcId) {
					return NpcList[i].npcAttackAnimation;
				}
			}
		}
		return -1;
	}*/
	/**
	 * Npc names
	 **/

	public String getNpcName(int npcId) {
		if (npcId <= -1) {
			return "None";
		}
		if (NPCDefinitions.getDefinitions()[npcId] == null) {
			return "None";
		}
		return NPCDefinitions.getDefinitions()[npcId].getNpcName();
	}
public boolean loadNPCList(String fileName) {
	String line = "";
	String token = "";
	String token2 = "";
	String token2_2 = "";
	String[] token3 = new String[10];
	File file = new File("./" + fileName);
	if (!file.exists()) {
		throw new RuntimeException("ERROR: NPC Configuration file does not exist.");
	}
	try (BufferedReader characterfile = new BufferedReader(new FileReader("./" + fileName))) {
		while ((line = characterfile.readLine()) != null && !line.equals("[ENDOFNPCLIST]")) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("npc")) {
					newNPCList(Integer.parseInt(token3[0]), token3[1], Integer.parseInt(token3[2]),
							Integer.parseInt(token3[3]));
				}
			}
		}
	} catch (IOException ioe) {
		ioe.printStackTrace();
		return false;
	}
	return true;
}
		public NPC[] getNPCs() {
		return npcs;
	}



}
