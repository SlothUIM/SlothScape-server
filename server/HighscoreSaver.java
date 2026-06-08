package server;

import server.model.players.Player;
import server.model.players.skills.Skill;
import server.net.db.DatabaseManager; // --- NEW: Import the central DB manager! ---

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HighscoreSaver {

    // Reusable thread pool (Prevents OutOfMemory errors from spamming 'new Thread()')
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // Boss identifiers mapped EXACTLY to the order of the SQL columns
    private static final String[] BOSS_LOG_KEYS = {
            "barrows", "bryophyta", "callisto", "cerberus", "chaosele", "corp", "dgk",
            "fanatic", "fightcaves", "giantmole", "generalgraardor", "kingblackdragon",
            "kq", "kraken", "kree'arra", "k'riltsutsaroth", "sire", "skotizo",
            "commanderzilyana", "zulrah"
    };

    private static String decodeBase37(long l) {
        if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L) return "invalid_name";
        if (l % 37L == 0L) return "invalid_name";

        int length = 0;
        char[] chars = new char[12];

        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            int c = (int) (l1 - l * 37L);
            if (c == 0) chars[11 - length++] = ' ';
            else if (c < 27) chars[11 - length++] = (char) (c + 96);
            else chars[11 - length++] = (char) (c + 21);
        }

        String name = new String(chars, 12 - length, length);
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formatted.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return formatted.toString().trim();
    }

    public static void savePlayer(Player p) {
        EXECUTOR.execute(() -> {
            long totalXp = 0;
            int totalLevel = 0;

            for (int i = 0; i < Skill.length(); i++) {
                totalXp += p.getSkills().getExperience(Skill.forId(i));
                totalLevel += p.getSkills().getLevel(Skill.forId(i));
            }

            int clTotal = p.getCollectionLog().getUnlockedLogCount();

            String hsQuery = "INSERT INTO highscores (username, rights, ironman_status, overall_xp, overall_level, " +
                    "attack_xp, defence_xp, strength_xp, hitpoints_xp, ranged_xp, prayer_xp, magic_xp, " +
                    "cooking_xp, woodcutting_xp, fletching_xp, fishing_xp, firemaking_xp, crafting_xp, " +
                    "smithing_xp, mining_xp, herblore_xp, agility_xp, thieving_xp, slayer_xp, farming_xp, " +
                    "runecrafting_xp, hunter_xp, construction_xp, " +
                    "cl_total, kc_barrows, kc_bryo, kc_callisto, kc_cerberus, kc_chaosele, kc_corp, kc_dgk, " +
                    "kc_fanatic, kc_fightcaves, kc_giantmole, kc_graardor, kc_kbd, kc_kq, kc_kraken, kc_kree, " +
                    "kc_kril, kc_sire, kc_skotizo, kc_zilyana, kc_zulrah) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "rights = VALUES(rights), ironman_status = VALUES(ironman_status), overall_xp = VALUES(overall_xp), " +
                    "overall_level = VALUES(overall_level), attack_xp = VALUES(attack_xp), defence_xp = VALUES(defence_xp), " +
                    "strength_xp = VALUES(strength_xp), hitpoints_xp = VALUES(hitpoints_xp), ranged_xp = VALUES(ranged_xp), " +
                    "prayer_xp = VALUES(prayer_xp), magic_xp = VALUES(magic_xp), cooking_xp = VALUES(cooking_xp), " +
                    "woodcutting_xp = VALUES(woodcutting_xp), fletching_xp = VALUES(fletching_xp), fishing_xp = VALUES(fishing_xp), " +
                    "firemaking_xp = VALUES(firemaking_xp), crafting_xp = VALUES(crafting_xp), smithing_xp = VALUES(smithing_xp), " +
                    "mining_xp = VALUES(mining_xp), herblore_xp = VALUES(herblore_xp), agility_xp = VALUES(agility_xp), " +
                    "thieving_xp = VALUES(thieving_xp), slayer_xp = VALUES(slayer_xp), farming_xp = VALUES(farming_xp), " +
                    "runecrafting_xp = VALUES(runecrafting_xp), hunter_xp = VALUES(hunter_xp), construction_xp = VALUES(construction_xp), " +
                    "cl_total = VALUES(cl_total), kc_barrows = VALUES(kc_barrows), kc_bryo = VALUES(kc_bryo), " +
                    "kc_callisto = VALUES(kc_callisto), kc_cerberus = VALUES(kc_cerberus), kc_chaosele = VALUES(kc_chaosele), " +
                    "kc_corp = VALUES(kc_corp), kc_dgk = VALUES(kc_dgk), kc_fanatic = VALUES(kc_fanatic), " +
                    "kc_fightcaves = VALUES(kc_fightcaves), kc_giantmole = VALUES(kc_giantmole), kc_graardor = VALUES(kc_graardor), " +
                    "kc_kbd = VALUES(kc_kbd), kc_kq = VALUES(kc_kq), kc_kraken = VALUES(kc_kraken), kc_kree = VALUES(kc_kree), " +
                    "kc_kril = VALUES(kc_kril), kc_sire = VALUES(kc_sire), kc_skotizo = VALUES(kc_skotizo), " +
                    "kc_zilyana = VALUES(kc_zilyana), kc_zulrah = VALUES(kc_zulrah)";

            // --- CRITICAL CHANGE: Grab the connection from our central DatabaseManager ---
            try (Connection conn = DatabaseManager.getConnection()) {

                // 1. SAVE HIGHSCORES
                try (PreparedStatement stmt = conn.prepareStatement(hsQuery)) {
                    int index = 1;
                    stmt.setString(index++, p.playerName);
                    stmt.setInt(index++, p.playerRights);
                    stmt.setInt(index++, 0); // TODO: Replace with p.getIronmanStatus() if applicable
                    stmt.setLong(index++, totalXp);
                    stmt.setInt(index++, totalLevel);

                    // Dynamic Skills Loop
                    for (int i = 0; i < Skill.length(); i++) {
                        stmt.setInt(index++, p.getSkills().getExperience(Skill.forId(i)));
                    }

                    stmt.setInt(index++, clTotal);

                    // Dynamic Boss KC Loop
                    for (String bossKey : BOSS_LOG_KEYS) {
                        stmt.setInt(index++, p.getCollectionLog().getBossKillCount(bossKey));
                    }

                    stmt.executeUpdate();
                }

                // 2. SYNC FRIENDS LIST (With SQL Transactions)
                int webUserId = -1;
                try (PreparedStatement getUserId = conn.prepareStatement("SELECT id FROM users WHERE username = ?")) {
                    getUserId.setString(1, p.playerName);
                    try (ResultSet rs = getUserId.executeQuery()) {
                        if (rs.next()) {
                            webUserId = rs.getInt("id");
                        }
                    }
                }

                if (webUserId != -1 && !p.getFriends().getFriends().isEmpty()) {
                    boolean autoCommitState = conn.getAutoCommit();
                    conn.setAutoCommit(false); // Begin Transaction to protect data wipe

                    try {
                        try (PreparedStatement deleteFriends = conn.prepareStatement("DELETE FROM friends WHERE user_id = ?")) {
                            deleteFriends.setInt(1, webUserId);
                            deleteFriends.executeUpdate();
                        }

                        try (PreparedStatement insertFriend = conn.prepareStatement("INSERT IGNORE INTO friends (user_id, friend_long, friend_name) VALUES (?, ?, ?)")) {
                            for (Long friendLong : p.getFriends().getFriends()) {
                                if (friendLong != 0) {
                                    insertFriend.setInt(1, webUserId);
                                    insertFriend.setLong(2, friendLong);
                                    insertFriend.setString(3, decodeBase37(friendLong));
                                    insertFriend.addBatch();
                                }
                            }
                            insertFriend.executeBatch();
                        }

                        conn.commit(); // Save changes only if both statements succeed
                    } catch (SQLException e) {
                        conn.rollback(); // Undo the DELETE if the INSERT fails
                        System.err.println("[Highscores] Transaction rolled back for " + p.playerName + ": " + e.getMessage());
                    } finally {
                        conn.setAutoCommit(autoCommitState); // Restore default DB state
                    }
                }

            } catch (SQLException e) {
                System.err.println("[Highscores] Database connection error for " + p.playerName + " - " + e.getMessage());
            }
        });
    }
}