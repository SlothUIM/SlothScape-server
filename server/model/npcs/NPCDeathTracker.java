package server.model.npcs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.model.players.Player;
import server.util.Database;
import server.util.Misc;

public class NPCDeathTracker {

	/**
	 * The player this is relative to
	 */
	private Player player;

	/**
	 * A mapping of npcs names with their corresponding kill count
	 */
	private Map<String, Integer> tracker = new HashMap<>();

	/**
	 * Creates a new {@link NPCDeathTracker} object for a singular player
	 * 
	 * @param player
	 *            the player
	 */
	public NPCDeathTracker(Player player) {
		this.player = player;
	}

	/**
	 * Attempts to add a kill to the total amount of kill for a single npc
	 * 
	 * @param name
	 *            the name of the npc
	 */
	public void add(String name) {
		if (name == null) {
			return;
		} else {
			int kills = (tracker.get(name) == null ? 0 : tracker.get(name)) + 1;
			tracker.put(name, kills);
			if (name.equalsIgnoreCase("none")) {
				return;
			}System.out.println("NPC Killed: [" + name + "]");
			player.sendMessage("Your " + Misc.capitalizeJustFirst(name.replaceAll("_", " "))
					+ " kill count is: <col=FF0000>" + kills + "</col>.");
			save();
		}
	}

	/*
	 * Saving
	 * 
	 * 
	 */

	public void save() {
	    final String name = player.playerName.toLowerCase();
	    // Copy the map to avoid ConcurrentModificationException
	    final Map<String, Integer> dataSnapshot = new HashMap<>(this.tracker);

	    Database.SAVE_SERVICE.execute(() -> {
	        String sql = "INSERT OR REPLACE INTO boss_tracker (username, npc_name, kills) VALUES (?, ?, ?)";
	        
	        try (Connection conn = Database.getConnection();
	             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
	            
	            conn.setAutoCommit(false); // Huge speed boost for 5,000 players
	            
	            for (Map.Entry<String, Integer> entry : dataSnapshot.entrySet()) {
	                pstmt.setString(1, name);
	                pstmt.setString(2, entry.getKey());
	                pstmt.setInt(3, entry.getValue());
	                pstmt.addBatch();
	            }
	            
	            pstmt.executeBatch();
	            conn.commit();
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    });
	}

	public void load() {
	    String sql = "SELECT npc_name, kills FROM boss_tracker WHERE username = ?";
	    
	    try (Connection conn = Database.getConnection();
	         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        
	        pstmt.setString(1, player.playerName.toLowerCase());
	        java.sql.ResultSet rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            tracker.put(rs.getString("npc_name"), rs.getInt("kills"));
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * Determines the total amount of kills
	 * 
	 * @return the total kill count
	 */
	public long getTotal() {
		return tracker.values().stream().mapToLong(Integer::intValue).sum();
	}

	/**
	 * A mapping of npcs with their corresponding kill count
	 * 
	 * @return the map
	 */
	public Map<String, Integer> getTracker() {
		return tracker;
	}
}
