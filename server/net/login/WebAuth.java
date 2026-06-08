package server.net.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import server.net.db.DatabaseManager; // Import your new central database manager

public class WebAuth {

    /**
     * Authenticates against the XenForo/Custom PHP database.
     * @return 0 = Success, 1 = Not Registered, 2 = Wrong Password, 3 = Email Not Verified, 4 = DB Error
     */
    public static int authenticate(String username, String plainPassword) {
        System.out.println("[WebAuth-DEBUG] 1. Entered authenticate() for user: " + username);
        String query = "SELECT password_hash, is_verified FROM users WHERE username = ?";

        try {
            System.out.println("[WebAuth-DEBUG] 2. Attempting to connect to database via DatabaseManager...");

            // Using try-with-resources handles closing the connection automatically
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                System.out.println("[WebAuth-DEBUG] 3. Database connected successfully. Preparing statement.");
                stmt.setString(1, username);

                System.out.println("[WebAuth-DEBUG] 4. Executing query...");
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("[WebAuth-DEBUG] 5. Query executed. Checking result set.");
                    if (rs.next()) {
                        System.out.println("[WebAuth-DEBUG] 6. User found in database.");

                        // 1. Check if they verified their email
                        int isVerified = rs.getInt("is_verified");
                        System.out.println("[WebAuth-DEBUG] 7. is_verified status: " + isVerified);
                        if (isVerified == 0) {
                            System.out.println("[WebAuth-DEBUG] 8. User is NOT verified. Returning 3.");
                            return 3;
                        }

                        // 2. Verify the Bcrypt Password
                        String hash = rs.getString("password_hash");

                        // --- THE PHP BCRYPT FIX ---
                        // Replace the PHP-specific $2y$ prefix with the Java-compatible $2a$ prefix
                        if (hash != null && hash.startsWith("$2y$")) {
                            hash = "$2a$" + hash.substring(4);
                        }

                        System.out.println("[WebAuth-DEBUG] 9. Hash formatted for Java. Attempting BCrypt check...");

                        boolean passwordMatches = BCrypt.checkpw(plainPassword, hash);

                        System.out.println("[WebAuth-DEBUG] 10. BCrypt check complete. Match: " + passwordMatches);
                        if (passwordMatches) {
                            return 0; // Perfect Login!
                        } else {
                            return 2; // Wrong password
                        }
                    } else {
                        System.out.println("[WebAuth-DEBUG] 6. User NOT found in database. Returning 1.");
                        return 1; // Account does not exist in the database
                    }
                }

            } catch (SQLException e) {
                System.err.println("[WebAuth-ERROR] SQL Exception for " + username + ": " + e.getMessage());
                e.printStackTrace();
                return 4;
            }
        } catch (Throwable t) {
            // THIS CATCHES THE SILENT JBCRYPT CRASH
            System.err.println("=================================================");
            System.err.println("[WebAuth-FATAL] THREAD CRASHED INSIDE WEBAUTH!");
            System.err.println("Error Type: " + t.getClass().getName());
            System.err.println("Message: " + t.getMessage());
            System.err.println("=================================================");
            t.printStackTrace();
            return 4;
        }
    }
}