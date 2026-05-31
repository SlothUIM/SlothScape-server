package server.content.clans;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import server.Config;
import server.model.items.Item;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.PlayerSave;
//import server.punishments.PunishmentType;
import server.util.Misc;
import server.world.World;

/**
 * This class stores all information about the clan. This includes active members, banned members, ranked members and their ranks, clan title, and clan founder. All clan joining,
 * leaving, and moderation/setup is also handled in this class.
 * 
 * @author Galkon
 * 
 */
public class Clan {

	public Player c;

	/**
	 * Adds a member to the clan.
	 * 
	 * @param player
	 */
	public void addMember(Player player) {
		if (isBanned(player.playerName)) {
			player.sendMessage("You are currently banned from this clan chat.");
			return;
		}
		if (whoCanJoin > Rank.ANYONE && !isFounder(player.playerName)) {
			if (getRank(player.playerName) < whoCanJoin) {
				player.sendMessage("Only " + getRankTitle(whoCanJoin) + "s+ may join this chat.");
				return;
			}
		}
		player.clan = this;
		player.setLastClanChat(getFounder());
		activeMembers.add(player.playerName);
		player.getPA().sendFrame126("Leave", 18135);
		player.getPA().sendFrame126("Talking in: " + Misc.capitalize(getTitle()) + "", 18139);
		player.sendMessage("Now talking in: " + Misc.capitalize(getTitle()) + ".");
		player.sendMessage("To talk, start each line of chat with the / symbol.");

		player.sendClan("System", player.playerName+ " has joined. Welcome to the chat.", getTitle(), getRank(player.playerName));
		updateMembers();
	}

	/**
	 * Removes the player from the clan.
	 * 
	 * @param player
	 */
	public void removeMember(Player player) {
		if(player == null)
			return;
		removeMember(player.playerName);
	}

	/**
	 * Removes the player from the clan.
	 *
	 */
	public void removeMember(String name) {
		boolean inClan = activeMembers.stream().anyMatch(playerName -> playerName.equals(name));
		if(inClan) {
			Optional<Player> playerOpt = PlayerHandler.getOptionalPlayer(name);
			playerOpt.ifPresent(player -> {
				player.clan = null;
				resetInterface(player);
			});
			activeMembers.remove(name);
			updateMembers();
		}
	}
	
	public void removeInactivePlayers() {
		List<String> filteredMembers = PlayerHandler.filterOffline(activeMembers);
		this.activeMembers.clear();
		this.activeMembers.addAll(filteredMembers);
	}

	/**
	 * Updates the members on the interface for the player.
	 * 
	 * @param player
	 */
	public void updateInterface(Player player) {
		player.getPA().sendFrame126("Talking in: " + Misc.capitalize(getTitle()) + "", 18139);
		player.getPA().sendFrame126(Misc.getWorldTime(), 18140);
		for (int index = 0; index < 100; index++) {
			if (index < activeMembers.size()) {
				player.getPA().sendFrame126("<clan=" + getRank(activeMembers.get(index)) + ">" + Misc.formatPlayerName(activeMembers.get(index)), 18665 + index);
				player.getPA().sendFrame126("World 1", 19307 + index);
				} else {
				player.getPA().sendFrame126("", 18665 + index);
				player.getPA().sendFrame126("", 19307 + index);
			}
		}
	}
	public void updateGroupingInterface(Player player) {
		player.getPA().sendFrame126("Talking in: " + Misc.capitalize(getTitle()) + "", 18139);
		player.getPA().sendFrame126(Misc.getWorldTime(), 18140);
		for (int index = 0; index < 100; index++) {
			if (index < activeMembers.size()) {
				player.getPA().sendFrame126("<clan=" + getRank(activeMembers.get(index)) + ">" + Misc.formatPlayerName(activeMembers.get(index)), 25433 + index);
				} else {
				player.getPA().sendFrame126("", 25433 + index);
			}
		}
	}
	/**
	 * Updates the interface for all members.
	 */
	public void updateMembers() {
		this.removeInactivePlayers();
		for (Player player : PlayerHandler.getPlayersForNames(activeMembers)) {
			updateInterface(player);	
		}
	}

	/**
	 * Resets the clan interface.
	 * 
	 * @param player
	 */
	public void resetInterface(Player player) {
		player.getPA().sendFrame126("Join", 18135);
		player.getPA().sendFrame126("Not in channel", 18139);
		player.getPA().sendFrame126(Misc.getWorldTime(), 18140);
		for (int index = 0; index < 100; index++) {
			player.getPA().sendFrame126("", 18665 + index);
			player.getPA().sendFrame126("", 19307 + index);
		}
		resetGroupingInterface(player);
	}
	public void resetGroupingInterface(Player player) {
		player.getPA().sendFrame126("You are not currently in a\\n minigame chat-channel", 24551);
		player.getPA().sendFrame126("Join", 24432);
		for (int index = 0; index < 100; index++) {
			player.getPA().sendFrame126("", 25433 + index);
		}
	}
	public void sendChat(Player paramClient, String paramString) {
		if (getRank(paramClient.playerName) < this.whoCanTalk) {
			paramClient.sendMessage("Only " + getRankTitle(this.whoCanTalk) + "s+ may talk in this chat.");
			return;
		}
		if (System.currentTimeMillis() < paramClient.muteEnd){// || World.getWorld().getPunishments().contains(PunishmentType.NET_MUTE, paramClient.connectedFrom)) {
			paramClient.sendMessage("You are muted, you cannot talk in this chat.");
			return;
		}

		//DiscordBot.sendMessage("cc-logs", "["+getTitle()+"]"+ paramClient.playerName+": "+ paramString.substring(1, 2).toUpperCase() + paramString.substring(2));
		for (Player player : PlayerHandler.getPlayersForNames(activeMembers)) {
			String icon = paramClient.getRights().getPrimary().getValue() > 0 ? "<clan=" + (paramClient.getRights().getPrimary().getValue() - 1) + ">" : "";
			if(player.getIgnores().has(paramClient.getNameAsLong()))
				continue;
			//player.sendMessage("@bla@[@blu@" + Misc.capitalize(getTitle()) + "@bla@] " + icon + "@bla@" + Misc.optimizeText(paramClient.playerName) + ": @dre@"
						//+ paramString.substring(1, 2).toUpperCase() + paramString.substring(2));
			player.sendClan(paramClient.playerName, paramString.substring(1), getTitle(), getRank(paramClient.playerName));
		}
		
	}

	/**
	 * Sends a message to the clan.
	 *
	 */
	public void sendMessage(String message) {
		for (int index = 0; index < Config.MAX_PLAYERS; index++) {
			Player p = PlayerHandler.players[index];
			if (p != null) {
				if (activeMembers.contains(p.playerName)) {
					p.sendMessage(message);
					//DiscordBot.sendMessage("cc-logs", c.playerName+": "+message);
				}
			}
		}
	}

	/**
	 * Sets the rank for the specified name.
	 * 
	 * @param name
	 * @param rank
	 */
	public void setRank(String name, int rank) {
	    // Standardize name formatting (Spaces instead of underscores, etc.)
	    name = Misc.formatPlayerName(name);

	    if (rankedMembers.contains(name)) {
	        ranks.set(rankedMembers.indexOf(name), rank);
	    } else {
	        rankedMembers.add(name);
	        ranks.add(rank);
	    }
	    save();
	}

	/**
	 * Demotes the specified name.
	 */
	public void demote(String name) {
	    name = Misc.formatPlayerName(name);
	    
	    if (!rankedMembers.contains(name)) {
	        return;
	    }
	    
	    int index = rankedMembers.indexOf(name);
	    rankedMembers.remove(index);
	    ranks.remove(index);
	    save();
	}

	/**
	 * Gets the rank of the specified name.
	 * 
	 * @param name
	 * @return
	 */
	public int getRank(String name) {
		name = Misc.formatPlayerName(name);
		if (rankedMembers.contains(name)) {
			return ranks.get(rankedMembers.indexOf(name));
		}
		if (isFounder(name)) {
			return Rank.OWNER;
		}
		if (PlayerSave.isFriend(getFounder(), name)) {
			return Rank.FRIEND;
		}
		return -1;
	}

	/**
	 * Can they kick?
	 * 
	 * @param name
	 * @return
	 */
	public boolean canKick(String name) {
		if (isFounder(name)) {
			return true;
		}
		if (getRank(name) >= whoCanKick) {
			return true;
		}
		return false;
	}

	/**
	 * Can they ban?
	 * 
	 * @param name
	 * @return
	 */
	public boolean canBan(String name) {
		if (isFounder(name)) {
			return true;
		}
		if (getRank(name) >= whoCanBan) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified name is the founder.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isFounder(String name) {
		if (getFounder().equalsIgnoreCase(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified name is a ranked user.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isRanked(String name) {
		name = Misc.formatPlayerName(name);
		if (rankedMembers.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified name is banned.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isBanned(String name) {
		name = Misc.formatPlayerName(name);
		if (bannedMembers.contains(name)) {
			return true;
		}
		return false;
	}

	/**
	 * Kicks the name from the clan chat.
	 * 
	 * @param name
	 */
	public void kickMember(String name) {
		if (!activeMembers.contains(name)) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		removeMember(name);
		Player player = PlayerHandler.getPlayer(name);
		if (player != null) {
			player.sendMessage("You have been kicked from the clan chat.");
		}
		sendMessage("@blu@[Attempting to kick/ban @dre@'" + Misc.formatPlayerName(name) + "'" + " @blu@from this friends chat]");
	}

	/**
	 * Bans the name from entering the clan chat.
	 * 
	 * @param name
	 */
	public void banMember(String name) {
		name = Misc.formatPlayerName(name);
		if (bannedMembers.contains(name)) {
			return;
		}
		if (name.equalsIgnoreCase(getFounder())) {
			return;
		}
		if (isRanked(name)) {
			return;
		}
		removeMember(name);
		bannedMembers.add(name);
		save();
		Optional<Player> playerOpt = PlayerHandler.getOptionalPlayer(name);
		
		playerOpt.ifPresent(player -> player.sendMessage("You have been kicked from the clan chat."));
		
		sendMessage("@blu@[Attempting to kick/ban @dre@'" + Misc.formatPlayerName(name) + "'" + " @blu@from this friends chat]");
	}

	/**
	 * Unbans the name from the clan chat.
	 * 
	 * @param name
	 */
	public void unbanMember(String name) {
		name = Misc.formatPlayerName(name);
		if (bannedMembers.contains(name)) {
			bannedMembers.remove(name);
			save();
		}
	}

	/**
	 * Saves the clan.
	 */
	public void save() {
		World.getWorld().getClanManager().save(this);
		updateMembers();
	}

	/**
	 * Deletes the clan.
	 */
	public void delete() {
		for (String name : activeMembers) {
			removeMember(name);
			Optional<Player> playerOpt = PlayerHandler.getOptionalPlayer(name);
			playerOpt.ifPresent(player -> player.sendMessage("The clan you were in has been deleted."));
		}
		if(this != null)
		World.getWorld().getClanManager().delete(this);
	}

	/**
	 * Creates a new clan for the specified player.
	 * 
	 * @param player
	 */
	public Clan(Player player) {
		setTitle(player.clanName + "");
		setFounder(player.playerName.toLowerCase());
	}

	/**
	 * Creates a new clan for the specified title and founder.
	 * 
	 * @param title
	 * @param founder
	 */
	public Clan(String title, String founder) {
		setTitle(title);
		setFounder(founder);
	}

	/**
	 * Gets the founder of the clan.
	 * 
	 * @return
	 */
	public String getFounder() {
		return founder;
	}

	/**
	 * Sets the founder.
	 * 
	 * @param founder
	 */
	public void setFounder(String founder) {
		this.founder = founder;
	}

	/**
	 * Gets the title of the clan.
	 * 
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title
	 * @return
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The title of the clan.
	 */
	public String title;

	/**
	 * The founder of the clan.
	 */
	public String founder;

	/**
	 * The active clan members.
	 */
	public LinkedList<String> activeMembers = new LinkedList<String>();

	/**
	 * The banned members.
	 */
	public LinkedList<String> bannedMembers = new LinkedList<String>();

	/**
	 * The ranked clan members.
	 */
	public LinkedList<String> rankedMembers = new LinkedList<String>();

	/**
	 * The clan member ranks.
	 */
	public LinkedList<Integer> ranks = new LinkedList<Integer>();

	/**
	 * The clan ranks.
	 * 
	 * @author Galkon
	 * 
	 */
	public static class Rank {
		public final static int ANYONE = -1;
		public final static int FRIEND = 0;
		public final static int RECRUIT = 1;
		public final static int CORPORAL = 2;
		public final static int SERGEANT = 3;
		public final static int LIEUTENANT = 4;
		public final static int CAPTAIN = 5;
		public final static int GENERAL = 6;
		public final static int OWNER = 7;
	}

	/**
	 * Gets the rank title as a string.
	 * 
	 * @param rank
	 * @return
	 */
	public String getRankTitle(int rank) {
		switch (rank) {
		case -1:
			return "Anyone";
		case 0:
			return "Friends";
		case 1:
			return "Recruit";
		case 2:
			return "Corporal";
		case 3:
			return "Sergeant";
		case 4:
			return "Lieutenant";
		case 5:
			return "Captain";
		case 6:
			return "General";
		case 7:
			return "Founder";
		}
		return "";
	}

	/**
	 * Sets the minimum rank that can join.
	 * 
	 * @param rank
	 */
	public void setRankCanJoin(int rank) {
		whoCanJoin = rank;
	}

	/**
	 * Sets the minimum rank that can talk.
	 * 
	 * @param rank
	 */
	public void setRankCanTalk(int rank) {
		whoCanTalk = rank;
	}

	/**
	 * Sets the minimum rank that can kick.
	 * 
	 * @param rank
	 */
	public void setRankCanKick(int rank) {
		whoCanKick = rank;
	}

	/**
	 * Sets the minimum rank that can ban.
	 * 
	 * @param rank
	 */
	public void setRankCanBan(int rank) {
		whoCanBan = rank;
	}

	/**
	 * The ranks privileges require (joining, talking, kicking, banning).
	 */
	public int whoCanJoin = Rank.ANYONE;
	public int whoCanTalk = Rank.ANYONE;
	public int whoCanKick = Rank.GENERAL;
	public int whoCanBan = Rank.OWNER;

}