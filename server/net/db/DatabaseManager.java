package server.net.db;

import server.util.ServerProperties; // Import your new loader

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseManager {

    // --- SECURE LOADING ---
    private static final String DB_URL = ServerProperties.getString("DB_URL");
    private static final String DB_USER = ServerProperties.getString("DB_USER");
    private static final String DB_PASS = ServerProperties.getString("DB_PASS");

    static {
        try {
            // Explicitly load the MySQL driver to ensure compatibility across various Java versions
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseManager-FATAL] MySQL JDBC Driver not found in classpath!");
            e.printStackTrace();
        }

        // Quick safety check to warn you if you forgot to add them to the properties file
        if (DB_URL == null || DB_USER == null || DB_PASS == null) {
            System.err.println("[DatabaseManager-WARNING] Database credentials are missing from server.properties!");
        }
    }

    /**
     * Vends a fresh, configured connection to the database.
     * The caller is responsible for closing this connection (ideally via try-with-resources).
     * @return Connection object
     * @throws SQLException if a connection failure occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}