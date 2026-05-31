package server.world;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.json.JSONArray;

import java.io.*;
import com.google.gson.*;

import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.util.Misc;

public class ApiClient {
    private static final String API_BASE_URL = "https://slothscape.com/api/slothscape/bridge";
    private static final Gson gson = new Gson();

    private static ScheduledExecutorService pmExecutor;
    public static final java.util.concurrent.ConcurrentHashMap<Long, Integer> globalOnlinePlayers = new java.util.concurrent.ConcurrentHashMap<>();

    public static void syncGlobalPlayerList() {
        try {
            URL url = new URL(API_BASE_URL + "/list");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONArray arr = new JSONArray(response.toString());

                globalOnlinePlayers.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    long nameAsLong = Misc.playerNameToLong2(obj.getString("name"));
                    globalOnlinePlayers.put(nameAsLong, obj.getInt("worldId"));
                }
            }
        } catch (Exception e) {
        }
    }
    public static int getPlayersOnWorld(int worldId) {
        int count = 0;
        for (Integer wId : globalOnlinePlayers.values()) {
            if (wId != null && wId == worldId) {
                count++;
            }
        }
        return count;
    }
    public static void updatePlayerStatus(String playerName, int worldId) {
        try {
            URL url = new URL(API_BASE_URL + "/update-status");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json");

            String json = String.format("{\"name\":\"%s\", \"worldId\":%d}", playerName, worldId);
            try (OutputStream os = con.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int code = con.getResponseCode();
            if (code == 200) {
            } else {
            }
        } catch (IOException e) {
            System.err.println("[ApiClient] Status Update Error: " + e.getMessage());
        }
    }

    public static boolean sendPMToAPI(String fromName, String toName, int fromUID, int fromRights, byte[] packed) {
        try {
            URL url = new URL(API_BASE_URL + "/pm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            JSONObject obj = new JSONObject();
            obj.put("from", fromName);
            obj.put("fromUID", fromUID);
            obj.put("fromRights", fromRights);
            obj.put("to", toName.toLowerCase());
            obj.put("message", Base64.getEncoder().encodeToString(packed));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(obj.toString().getBytes(StandardCharsets.UTF_8));
            }

            int responseCode = conn.getResponseCode();
            return (responseCode == 200);
        } catch (Exception e) {
            System.err.println("[ApiClient] Error sending PM to Bridge: " + e.getMessage());
            return false;
        }
    }

    public static void startPmPollingLoop() {
        if (pmExecutor != null && !pmExecutor.isShutdown()) {
            return;
        }

        pmExecutor = Executors.newSingleThreadScheduledExecutor();
        pmExecutor.scheduleAtFixedRate(() -> {
            try {
                syncGlobalPlayerList();


                for (Player p : PlayerHandler.players) {
                    if (p != null && p.isActive && p.getFriends() != null) {

                        pollPrivateMessages(p);
                        updatePlayerStatus(p.playerName, server.ServerStatusWriter.WORLD_ID);

                        p.getFriends().sendList();
                    }
                }
            } catch (Exception e) {
                System.err.println("[ApiClient] Polling loop caught an error but stayed alive!");
                e.printStackTrace();
            }
        }, 2, 2, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        if (pmExecutor != null && !pmExecutor.isShutdown()) {
            pmExecutor.shutdown();
            System.out.println("[ApiClient] PM Polling loop shut down gracefully.");
        }
    }

    public static void pollPrivateMessages(Player player) {
        try {
            URL url = new URL(API_BASE_URL + "/pm/poll");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();
            body.put("recipient", player.playerName.toLowerCase());

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            if (conn.getResponseCode() == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    JSONArray arr = new JSONArray(response.toString());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject msg = arr.getJSONObject(i);
                        byte[] decoded = Base64.getDecoder().decode(msg.getString("message"));
                        long fromLong = Misc.playerNameToLong2(msg.getString("from"));
                        int senderRights = msg.getInt("fromRights");
                        player.getPA().createPlayerPM(fromLong, senderRights, decoded);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ApiClient] Error polling PMs: " + e.getMessage());
        }
    }

    public static int getWorldIdOfPlayer(long nameAsLong) {
        String name = Misc.longToPlayerName2(nameAsLong);
        try {
            URL url = new URL(API_BASE_URL + "/get-status/" + URLEncoder.encode(name, "UTF-8"));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (InputStream is = con.getInputStream();
                     InputStreamReader reader = new InputStreamReader(is)) {
                    PlayerStatus status = gson.fromJson(reader, PlayerStatus.class);
                    return status.worldId;
                }
            } else {
                return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    static class PlayerStatus {
        String name;
        int worldId;
        long lastSeen;
    }
}
