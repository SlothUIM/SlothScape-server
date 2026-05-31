package server.model.players;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import server.Config;
import server.Server;
import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.world.ApiClient;
import server.world.Boundary;
import server.world.World;
import server.util.Buffer;

@Slf4j
public class PlayerHandler {

	public static Player players[] = new Player[Config.MAX_PLAYERS];
	public static String messageToAll = "";
	public static int playerCount = 0;
	public static String playersCurrentlyOn[] = new String[Config.MAX_PLAYERS];
	public static boolean updateAnnounced;
	public static boolean updateRunning;
	public static int updateSeconds;
	public static long updateStartTime;
	public static Object lock = new Object();
	public static boolean debug = false;
	private boolean kickAllPlayers = false;

	static {
		for (int i = 0; i < Config.MAX_PLAYERS; i++)
			players[i] = null;
	}





	public static int getPlayerCount() {
		int count = 0;
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null) {
				count++;
			}
		}
		return (count + Config.PLAYERMODIFIER);
	}

	public static int getRealPlayerCount() {
		int online = (int) ((double)PlayerHandler.getPlayers().size() * 1.3333);
		return online;
	}
	public static Player getPlayerByLongName(long name) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] == null)
				continue;
			if (PlayerHandler.players[i].getNameAsLong() == name)
				return PlayerHandler.players[i];
		}
		return null;
	}
	public static List<Player> getPlayers() {
		return nonNullStream().collect(Collectors.toList());
	}
	public static Stream<Player> nonNullStream() {
		return Arrays.stream(players).filter(Objects::nonNull);
	}
	public static boolean isPlayerOn(String playerName) {
		for (int i = 0; i < Config.MAX_PLAYERS; i++) {
			if (players[i] != null) {
				if (players[i].playerName.equalsIgnoreCase(playerName)) {
					return true;
				}
			}
		}
		return false;
	}

	public void process() {
		synchronized (lock) {
			if (kickAllPlayers) {
				log.info("Kicking all players!");
				nonNullStream().forEach(player -> player.disconnected = true);
			}

			long startTime = System.currentTimeMillis();
			Random rng = new Random(startTime);

			// Heap Allocation Fix: Extract, filter, and shuffle active entities exactly ONCE per cycle
			List<Player> activePlayers = nonNullStream()
					.filter(player -> player != null && player.initialized && player.isActive)
					.collect(Collectors.toList());

			Collections.shuffle(activePlayers, rng);

			// PHASE 1: Process Packet Ingestion, Disconnects, and Movements
			activePlayers.forEach(player -> {
				try {
					boolean logoutDelayPassed = (System.currentTimeMillis() - player.logoutDelay > 90000);
					boolean inDuel = Boundary.isIn(player, Boundary.DUEL_ARENA) && World.getWorld().getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.DUEL);

					if (player.disconnected && (logoutDelayPassed || player.properLogout || kickAllPlayers || inDuel)) {
						try {
							if (World.getWorld().getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
								World.getWorld().getMultiplayerSessionListener().finish(player, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						}
						try {
							DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
							if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
								if (duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
									duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
								} else {
									Player winner = duelSession.getOther(player);
									duelSession.setWinner(winner);
									duelSession.finish(MultiplayerSessionFinalizeType.GIVE_ITEMS);
								}
							}
						} catch(Exception ex) {
							ex.printStackTrace();
						}

						player.prelogout();
						if (PlayerSave.saveGame(player)) {
							log.info("{} [{}] just disconnected", player.playerName, player.connectedFrom);
						} else {
							log.info("Could not save for {}", player.playerName);
						}

						removePlayer(player);
						for (int i = 0; i < players.length; i++) {
							if (players[i] == player) {
								players[i] = null;
							}
						}
						return;
					}

					// Core Player Logic Cycles
					player.processQueuedPackets();
					player.process();
					player.getNextPlayerMovement();

				} catch (Exception e) {
					log.error("Exception handling main process for player: " + player.playerName, e);
					player.disconnected = true;
				}
			});

			activePlayers.forEach(player -> {
				// FIX: Skip players who were disconnected or destroyed in Phase 1
				if (player == null || player.disconnected || !player.isActive || player.getOutStream() == null) return;
				try {
					player.update();
				} catch (Exception e) {
					log.error("Exception creating client update blocks for player: " + player.playerName, e);
				}
			});

			// PHASE 3: Flag Resets for Subsequent Tick Synchronization
			activePlayers.forEach(player -> {
				// FIX: Skip destroyed players
				if (player == null || player.disconnected || !player.isActive) return;
				try {
					player.clearUpdateFlags();
				} catch (Exception e) {
					log.error("Exception resetting mask update flags for player: " + player.playerName, e);
				}
			});

			// System Maintainer Updates
			if (updateRunning && !updateAnnounced) {
				updateAnnounced = true;
				Server.UpdateServer = true;
			}

			if (updateCancelled) {
				updateCancelled = false;
				Server.UpdateServer = false;
			}

			if (updateRunning && (System.currentTimeMillis() - updateStartTime > (updateSeconds * 1000))) {
				kickAllPlayers = true;
				if (nonNullStream().count() == 0) {
					System.exit(0);
				}
			}
		}
	}

	public void updateNPC(Player plr, Buffer str) {
		//synchronized (plr) {
		updateBlock.currentOffset = 0;

		str.createFrameVarSizeWord(65);
		str.initBitAccess();

		str.writeBits(8, plr.npcListSize);
		int size = plr.npcListSize;
		if(plr.currentRender < plr.maxRender) {
			plr.currentRender += 4;
		}
		plr.npcListSize = 0;
		for (int i = 0; i < size; i++) {
			NPC npc = plr.npcList[i];
			if(npc == null)
				continue;
			if (!plr.rebuildNPCList && plr.withinDistance(npc)) {
				npc.updateNPCMovement(str);
				npc.appendNPCUpdateBlock(updateBlock);
				plr.npcList[plr.npcListSize++] = npc;
			} else {
				//System.out.println("Removing NPC " + npc.npcType + " from list for " + plr.playerName + " at dist: " + npc.getX() + "," + npc.getY());
				int id = npc.getIndex();
				plr.npcInListBitmap[id >> 3] &= (byte) ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			NPC npc = NPCHandler.npcs[i];
			if (npc != null) {
				int id = npc.getIndex();
				if (!plr.RebuildNPCList
						&& (plr.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) {

				} else if (!plr.withinDistance(npc)) {

				} else {
					//if(!npc.sameInstance(plr))
					//	continue;

					/*System.out.println("Rebuild: " + plr.RebuildNPCList +
						    ", InList: " + ((plr.npcInListBitmap[id >> 3] & (1 << (id & 7))) != 0) +
						    ", Distance: " + plr.withinDistance(npc) +
						    ", SameInst: " + npc.sameInstance(plr));*/
					plr.addNewNPC(npc, str,
							updateBlock);
				}
			}
		}

		plr.RebuildNPCList = false;

		if (updateBlock.currentOffset > 0) {
			str.writeBits(14, 16383);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else {
			str.finishBitAccess();
		}
		str.endFrameVarSizeWord();
		//}
	}
	private Buffer updateBlock = new Buffer(new byte[Config.BUFFER_SIZE]);
	private boolean updateCancelled;
	public static Player getPlayer(String name) {
		for (int d = 0; d < Config.MAX_PLAYERS; d++) {
			if (PlayerHandler.players[d] != null) {
				Player o = PlayerHandler.players[d];
				if (o.playerName.equalsIgnoreCase(name)) {
					return o;
				}
			}
		}
		return null;
	}
	public static Player getPlayerByName(String playerName) {
		if (playerName == null) return null;
		for (Player p : players) {
			if (p == null) continue;
			if (p.isActive && p.playerName != null && p.playerName.equalsIgnoreCase(playerName)) {
				return p;
			}
		}
		return null; // Not found
	}
	public static int getWorldIdOfPlayer(long nameAsLong) {
		// You can first check local players:
		Player localPlayer = getPlayerByLongName(nameAsLong);
		if (localPlayer != null) {
			return localPlayer.worldID; // Your local world's ID (e.g. 1)
		}
		// If not found locally, query the API:
		return ApiClient.getWorldIdOfPlayer(nameAsLong);
	}




	public void updatePlayer(Player plr, Buffer str) {
		updateBlock.currentOffset = 0;
		if (updateRunning && !plr.updateAnnounced) {
			plr.updateAnnounced = true;
			str.createFrame(114);
			str.writeWordBigEndian(updateSeconds * 50 / 30);
		}
		if (updateCancelled && plr.updateAnnounced) {
			str.createFrame(114);
			str.writeWordBigEndian(0);
			plr.updateAnnounced = false;
		}
		plr.updateThisPlayerMovement(str);
		boolean saveChatTextUpdate = plr.isChatTextUpdateRequired();
		plr.setChatTextUpdateRequired(false);
		plr.appendPlayerUpdateBlock(updateBlock);
		plr.setChatTextUpdateRequired(saveChatTextUpdate);
		str.writeBits(8, plr.playerListSize);
		int size = plr.playerListSize;
		if (size > 200)
			size = 200;
		plr.playerListSize = 0;
		for (int i = 0; i < size; i++) {
			Player otherPlr = plr.playerList[i];
			boolean activeAndSame = otherPlr != null && players[otherPlr.getIndex()] != null && players[otherPlr.getIndex()] == plr.playerList[i];			
			if (activeAndSame && !otherPlr.isNeedsPlacement() && plr.withinDistance(otherPlr) && otherPlr.sameInstance(plr)) {
				plr.playerList[i].updatePlayerMovement(str);
				plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
				plr.playerList[plr.playerListSize++] = plr.playerList[i];
			} else {
				int id = plr.playerList[i].getIndex();
				plr.playerList[i] = null;
				plr.playerInListBitmap[id >> 3] &= ~(1 << (id & 7));
				str.writeBits(1, 1);
				str.writeBits(2, 3);
			}
		}

		for (int i = 0; i < Config.MAX_PLAYERS; i++) { // memory leak fix
			// if(updateBlock.currentOffset >= 4000)
			// break;
			if (players[i] == null || !players[i].isActive || players[i] == plr)
				continue;
			int id = players[i].getIndex();
			if ((plr.playerInListBitmap[id >> 3] & (1 << (id & 7))) != 0)
				continue;
			if (!plr.withinDistance(players[i]))
				continue;
			if(!plr.sameInstance(players[i]))
				continue;
			plr.addNewPlayer(players[i], str, updateBlock);
		}

		if (updateBlock.currentOffset > 0) {
			str.writeBits(11, 2047);
			str.finishBitAccess();
			str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
		} else
			str.finishBitAccess();

		str.endFrameVarSizeWord();
	}

	private void removePlayer(Player plr) {
		plr.destruct();
	}

	/**
	 * The next available slot between 1 and {@link Config#MAX_PLAYERS}.
	 * 
	 * @return the next slot
	 */
	public int nextSlot() {
		for (int index = 1; index < Config.MAX_PLAYERS; index++) {
			if (players[index] == null) {
				return index;
			}
		}
		return 1;
	}

	public void add(Player player) {
		players[player.getIndex()] = player;
		players[player.getIndex()].isActive = true;
		System.out.println(player.getName() +" has just logged in.");
		/*if (Config.SERVER_STATE == ServerState.PUBLIC_PRIMARY) {
			log.info("{} [{}] just logged in.", player.getName(), player.connectedFrom);
		}*/
	}
	public static Optional<Player> getPlayerByIndex(int index) {
		return nonNullStream().filter(plr -> plr.getIndex() == index).findFirst();
	}
	public static Optional<Player> getOptionalPlayer(String name) {
		return getPlayers().stream().filter(Objects::nonNull).filter(client -> client.playerName.equalsIgnoreCase(name)).findFirst();
	}
	public static List<Player> getPlayersForNames(List<String> playerNames) {
		return playerNames
				.stream()
				.distinct()
				.filter(Objects::nonNull)
				.filter(name -> !name.isEmpty())
				.map(PlayerHandler::getPlayer)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
		public static List<String> filterOffline(List<String> playerNames){
		return playerNames
				.stream()
				.distinct()
				.filter(Objects::nonNull)
				.filter(name -> !name.isEmpty())
				.map(PlayerHandler::getPlayer)
				.filter(Objects::nonNull)
				.map(plr -> plr.getName())
				.collect(Collectors.toList());
	}

		

		public static Map<Long, Player> onlinePlayers = new java.util.concurrent.ConcurrentHashMap<>();
			
		public static Player getOnlinePlayer(long name) {
		    return onlinePlayers.get(name);
		}

}
