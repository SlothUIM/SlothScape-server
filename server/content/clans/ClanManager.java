package server.content.clans;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedList;

import server.model.players.Player;
import server.model.players.PlayerHandler;

public class ClanManager {
	public LinkedList<Clan> clans;

	public ClanManager() {
		clans = new LinkedList<Clan>();
	}

	public int getActiveClans() {
		return this.clans.size();
	}

	public int getTotalClans() {
		File localFile = new File("/Data/clan/");
		return localFile.listFiles().length;
	}

	public void create(Player paramClient) {
		if (paramClient.clan != null) {
			paramClient.sendMessage("@red@You must leave your current clan-chat before making your own.");
			return;
		}
		if (paramClient.inArdiCC) {
			return;
		}
		Clan localClan = new Clan(paramClient);
		this.clans.add(localClan);
		titleToFounder.put(localClan.getTitle().toLowerCase(), localClan.getFounder().toLowerCase());
		localClan.addMember(paramClient);
		localClan.save();
		paramClient.getPA().setClanData();
		paramClient.sendMessage("@red@You may change your clan settings by clicking the 'Clan Setup' button.");
	}
	// Inside ClanManager.java
	public HashMap<String, String> titleToFounder = new HashMap<>();

	// Add this method to load all titles into the map when the server starts
	public void loadClanTitles() {
	    File folder = new File("Data/clan/");
	    if (!folder.exists()) return;
	    
	    File[] files = folder.listFiles();
	    if (files == null) return;

	    for (File file : files) {
	        if (file.getName().endsWith(".cla")) {
	            String founderName = file.getName().replace(".cla", "");
	            Clan temp = read(founderName); // Read the file once to get the title
	            if (temp != null) {
	                titleToFounder.put(temp.getTitle().toLowerCase(), founderName.toLowerCase());
	            }
	        }
	    }
	    System.out.println("Loaded " + titleToFounder.size() + " clan titles into memory.");
	}
	public Clan getClan(String paramString) {
		for (int i = 0; i < this.clans.size(); i++) {
			if (this.clans.get(i).getFounder().equalsIgnoreCase(paramString)) {
				return this.clans.get(i);
			}

		}

		Clan localClan = read(paramString);
		if (localClan != null) {
			this.clans.add(localClan);
			return localClan;
		}
		return null;
	}

	/**
	 * Returns the Help clan or creates it if it doesn't exist yet.
	 * 
	 * @return The Help clan.
	 */
	public Clan getHelpClan() {
		for (int i = 0; i < this.clans.size(); i++) {
			if (clans.get(i).getFounder().equalsIgnoreCase("Help")) {
				return clans.get(i);
			}
		}

		Clan localClan = read("Help");
		if (localClan != null) {
			clans.add(localClan);
			return localClan;
		}
		localClan = new Clan("Help", "Help");
		clans.add(localClan);
		localClan.save();
		return localClan;
	}

	public void delete(Clan paramClan) {
		if (paramClan == null) {
			return;
		}
		File localFile = new File("Data/clan/" + paramClan.getFounder() + ".cla");
		if (localFile.delete()) {
			Player localClient = PlayerHandler.getPlayer(paramClan.getFounder());
			if (localClient != null) {
				localClient.sendMessage("@red@Your clan has been deleted.");
			}
			this.clans.remove(paramClan);
		}
	}

	public void save(Clan paramClan) {
	    if (paramClan == null) {
	        return;
	    }
	    File clanFolder = new File("Data/clan/");
	    File localFile = new File("Data/clan/" + paramClan.getFounder() + ".cla");
	    clanFolder.mkdirs();
	    
	    try (RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd")) {
	        localRandomAccessFile.writeUTF(paramClan.getTitle());
	        localRandomAccessFile.writeByte(paramClan.whoCanJoin);
	        localRandomAccessFile.writeByte(paramClan.whoCanTalk);
	        localRandomAccessFile.writeByte(paramClan.whoCanKick);
	        localRandomAccessFile.writeByte(paramClan.whoCanBan);

	        if ((paramClan.rankedMembers != null) && (paramClan.rankedMembers.size() > 0)) {
	            localRandomAccessFile.writeShort(paramClan.rankedMembers.size());
	            for (int i = 0; i < paramClan.rankedMembers.size(); i++) {
	                localRandomAccessFile.writeUTF(paramClan.rankedMembers.get(i));
	                localRandomAccessFile.writeShort(paramClan.ranks.get(i).intValue());
	            }
	        } else {
	            localRandomAccessFile.writeShort(0);
	        }

	        // IMPORTANT: Shrink the file to the current pointer to remove deleted members' data
	        localRandomAccessFile.setLength(localRandomAccessFile.getFilePointer());
	        
	        titleToFounder.put(paramClan.getTitle().toLowerCase(), paramClan.getFounder().toLowerCase());
	    } catch (IOException localIOException) {
	        localIOException.printStackTrace();
	    }
	}

	private Clan read(String paramString) {
		File localFile = new File("Data/clan/" + paramString + ".cla");
		if (!localFile.exists()) {
			return null;
		}
		try {
			RandomAccessFile localRandomAccessFile = new RandomAccessFile(localFile, "rwd");

			Clan localClan = new Clan(localRandomAccessFile.readUTF(), paramString);
			localClan.whoCanJoin = localRandomAccessFile.readByte();
			localClan.whoCanTalk = localRandomAccessFile.readByte();
			localClan.whoCanKick = localRandomAccessFile.readByte();
			localClan.whoCanBan = localRandomAccessFile.readByte();
			int i = localRandomAccessFile.readShort();
			if (i != 0) {
				for (int j = 0; j < i; j++) {
					localClan.rankedMembers.add(localRandomAccessFile.readUTF());
					localClan.ranks.add(Integer.valueOf(localRandomAccessFile.readShort()));
				}
			}
			localRandomAccessFile.close();

			return localClan;
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return null;
	}

	/**
	 * Attempt to rejoin the last Clan channel upon login.
	 * 
	 * @param client
	 */
	public void joinOnLogin(Player client) {
		String lastChannel = client.getLastClanChat();
		if (lastChannel != null && lastChannel.length() > 0) {
			client.sendMessage("Attempting to join " + lastChannel + "...");
			Clan localClan = getClan(lastChannel);
			if (localClan != null) {
				localClan.addMember(client);
			} else {
				client.sendMessage(lastChannel + " no longer exists.");
			}
		}
	}

	public boolean clanExists(String paramString) {
		File localFile = new File("Data/clan/" + paramString + ".cla");
		return localFile.exists();
	}

	public LinkedList<Clan> getClans() {
		return this.clans;
	}
}