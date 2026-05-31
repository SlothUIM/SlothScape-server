package server.model.players;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import server.ServerStatusWriter;
import server.world.ApiClient;

/**
 * A more improved system for handling a user's friends. Takes into account of the privacy setting
 * * @author Mack
 */
public class Friends {

	public static final int ONLINE = 0;
	public static final int FRIENDS = 1;
	public static final int OFFLINE = 2;

	private Player client;
	@Getter
    private List<Long> friends = new ArrayList<Long>();

	// Dedicated thread pool for checking friend statuses without lagging the main game tick
	private static final ExecutorService FRIEND_STATUS_THREAD_POOL = Executors.newFixedThreadPool(2);

	public Friends(Player client) {
		this.client = client;
	}

	private boolean canSeeMe(Player other) {
		if (other.getIgnores().has(client.getNameAsLong())) return false;
		if (client.getPrivateChat() == ONLINE) return true;
		if (client.getPrivateChat() == FRIENDS) return client.getFriends().has(other.getNameAsLong());
		return false;
	}

	public void remove(long name) {
		if (!has(name)) {
			client.sendMessage("That player is not on your friends list.");
			return;
		}
		friends.remove(name);
		if (client.getPrivateChat() == FRIENDS) {
			Player friend = PlayerHandler.getPlayerByLongName(name);
			if (friend != null && friend.getFriends().has(name)) {
				friend.getPA().loadPM(client.getNameAsLong(), 0); // They can no longer see us
			}
		}
	}

	public void sendPrivateMessage(long name, byte[] packed) {
		boolean send = true;
		Player friend = PlayerHandler.getPlayerByLongName(name);
		if (friend != null) {
			if (friend.getPrivateChat() == OFFLINE) {
				send = false;
			} else if (friend.getPrivateChat() == FRIENDS && !friend.getFriends().has(client.getNameAsLong())) {
				send = false;
			} else if (friend.getIgnores().has(client.getNameAsLong())) {
				send = false;
			}
			if (send) {
				friend.getPA().createPlayerPM(client.getNameAsLong(), (byte) client.playerRights, packed);
			}
		} else {
			send = false;
		}
		if (!send) {
			client.sendMessage("That player is currently offline.");
		}
	}

	public void notifyFriendsOfUpdate() {
		// 1. Notify Local Players
		for (Player plr : PlayerHandler.players) {
			if (plr == null || plr == client) continue;

			if (plr.getFriends().has(client.getNameAsLong())) {
				if (client.getIgnores().has(plr.getNameAsLong())) continue;

				boolean online = true;
				if (client.disconnected || client.getPrivateChat() == OFFLINE) {
					online = false;
				} else if (client.getPrivateChat() == FRIENDS && !client.getFriends().has(plr.getNameAsLong())) {
					online = false;
				}

				// Assuming you have a way to know this server's World ID (e.g. Config.WORLD_ID)
				int worldId = online ? ServerStatusWriter.WORLD_ID : 0;
				plr.getPA().loadPM(client.getNameAsLong(), worldId);
			}
		}
	}

	public void sendList() {
		for (long friendLong : friends) {
			boolean online = false;
			int worldId = 0;

			Player friend = PlayerHandler.getPlayerByLongName(friendLong);

			// 1. If they are on this exact server (Respects Privacy Settings):
			if (friend != null && !friend.getIgnores().has(client.getNameAsLong())) {
				if (friend.getPrivateChat() == ONLINE) {
					online = true;
				} else if (friend.getPrivateChat() == FRIENDS && friend.getFriends().has(client.getNameAsLong())) {
					online = true;
				}
				if (online) worldId = server.ServerStatusWriter.WORLD_ID;
			}
			// 2. If they are cross-world (Instant Cache Lookup!):
			else {
				worldId = ApiClient.globalOnlinePlayers.getOrDefault(friendLong, 0);
			}

			client.getPA().loadPM(friendLong, worldId);
		}
	}

	public void add(long name) {
		if (friends.size() >= 200) {
			client.sendMessage("Friends list is currently full.");
			return;
		}
		if (has(name)) {
			client.sendMessage("That player is already on your friends list.");
			return;
		}

		friends.add(name);

		Player localFriend = PlayerHandler.getPlayerByLongName(name);
		if (localFriend != null && localFriend.isActive) {
			if (canSeeMe(localFriend)) {
				client.getPA().loadPM(name, server.ServerStatusWriter.WORLD_ID);
			} else {
				client.getPA().loadPM(name, 0);
			}
		} else {
			// Instant Cache Lookup! No threads required.
			int worldId = ApiClient.globalOnlinePlayers.getOrDefault(name, 0);
			client.getPA().loadPM(name, worldId > 0 ? worldId : 0);
		}
	}

	public List<Long> getList() {
		return friends;
	}

	public boolean has(long name) {
		return friends.contains(name);
	}

	public Player getPlayer() {
		return client;
	}

}