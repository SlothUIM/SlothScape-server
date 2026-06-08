package server;

import server.util.ServerProperties; // Make sure to import your new loader!

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ServerStatusWriter {

    // --- SECURE LOADING ---
    private static final String API_URL = ServerProperties.getString("API_URL");
    private static final String API_SECRET = ServerProperties.getString("API_SECRET");
    public static final int WORLD_ID = ServerProperties.getInt("WORLD_ID", 1);

    private static long startTime = System.currentTimeMillis();

    private static int peakPlayers = 0;
    private static long lastPeakReset = System.currentTimeMillis();

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void updateStatus(int playersOnline) {
        // Safety check to prevent crashing if the properties file is missing
        if (API_URL == null || API_SECRET == null) {
            System.err.println("[Web-Status] API URL or Secret is missing from server.properties!");
            return;
        }

        if (System.currentTimeMillis() - lastPeakReset > 86400000L) {
            peakPlayers = playersOnline;
            lastPeakReset = System.currentTimeMillis();
        }
        if (playersOnline > peakPlayers) {
            peakPlayers = playersOnline;
        }

        long uptimeMillis = System.currentTimeMillis() - startTime;
        long hours = (uptimeMillis / (1000 * 60 * 60));
        long minutes = (uptimeMillis / (1000 * 60)) % 60;
        String uptimeString = hours + "h " + minutes + "m";

        String jsonPayload = String.format(
                "{\"world_id\": %d, \"players\": %d, \"peak\": %d, \"uptime\": \"%s\"}",
                WORLD_ID, playersOnline, peakPlayers, uptimeString
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_SECRET)
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        System.err.println("[Web-Status] API Error: HTTP " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    System.err.println("[Web-Status] Could not reach Central API: " + e.getMessage());
                    return null;
                });
    }
}