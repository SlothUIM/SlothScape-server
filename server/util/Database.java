package server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {

    // This creates a file called trackers.db in your /data/ folder
    private static final String DB_URL = "jdbc:sqlite:./data/trackers.db";

    /**
     * Gets a connection to the SQLite database file.
     */

	public static final ExecutorService SAVE_SERVICE = Executors.newFixedThreadPool(4);
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC"); // This loads the JAR driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Call this once when the server starts (Server.java main method)
     * to ensure the tracker table is ready.
     */
    public static void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS boss_tracker (" +
                     "username TEXT, " +
                     "npc_name TEXT, " +
                     "kills INTEGER, " +
                     "PRIMARY KEY (username, npc_name));";
     // Inside Database.initialize()
        String settingsTable = "CREATE TABLE IF NOT EXISTS house_settings (" +
                               "username TEXT PRIMARY KEY, style INTEGER, teleportInside INTEGER, " +
                               "buildMode INTEGER, doorStatus INTEGER);";

        String portalsTable = "CREATE TABLE IF NOT EXISTS poh_portals (" +
                              "username TEXT, id INTEGER, x INTEGER, y INTEGER, z INTEGER, type INTEGER);";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
         // Create Rooms Table
            stmt.execute("CREATE TABLE IF NOT EXISTS poh_rooms (" +
                         "username TEXT, x INTEGER, y INTEGER, z INTEGER, " +
                         "rotation INTEGER, type INTEGER, " +
                         "PRIMARY KEY (username, x, y, z));");

            // Create Furniture Table
            stmt.execute("CREATE TABLE IF NOT EXISTS poh_furniture (" +
                         "username TEXT, roomX INTEGER, roomY INTEGER, roomZ INTEGER, " +
                         "hotspot INTEGER, furnitureId INTEGER, xOff INTEGER, yOff INTEGER);");

            // Create Portals Table
            stmt.execute("CREATE TABLE IF NOT EXISTS poh_portals (" +
                         "username TEXT, id INTEGER, x INTEGER, y INTEGER, z INTEGER, type INTEGER);");

            // Create Settings Table
            stmt.execute("CREATE TABLE IF NOT EXISTS house_settings (" +
                         "username TEXT PRIMARY KEY, style INTEGER, teleportInside INTEGER, " +
                         "buildMode INTEGER, doorStatus INTEGER);");

            System.out.println("[Database] Construction tables verified.");   
            System.out.println("[Database] Boss Tracker table initialized.");
        } catch (SQLException e) {
            System.out.println("[Database] Error initializing table: " + e.getMessage());
        }
    }
}