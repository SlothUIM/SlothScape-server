package server.model.players.packets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

public class GeValuesCache {

    // 1. Thread-safe map with 'volatile' keyword for instant cross-thread visibility
    public static volatile Map<Integer, Pair<Integer, Integer>> geValues = new ConcurrentHashMap<>();

    public static long lastPriceUpdate;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String latestPricesUrl = "https://prices.runescape.wiki/api/v1/osrs/latest";

    /**
     * Safely grabs a price pair for an item.
     */
    public static Pair<Integer, Integer> getPrice(int itemId) {
        return geValues.get(itemId);
    }

    public static void loadGePricesCache() {
        try {
            URL url = new URL(latestPricesUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 2. Change this to your actual contact details to prevent API bans!
            connection.setRequestProperty("User-Agent", "SlothScape GE Loader - Discord: @YourHandle");
            connection.setRequestMethod("GET");

            // 3. Timeouts prevent the background thread from hanging forever
            connection.setConnectTimeout(10000); // 10 seconds to connect
            connection.setReadTimeout(10000);    // 10 seconds to read data

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.err.println("GE Cache Download failed. HTTP Response Code: " + responseCode);
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonBuilder.append(line);
                }

                parseJsonPrices(jsonBuilder.toString());
                System.out.println("GE prices loaded from RuneLite API at " + dateFormat.format(new Date(lastPriceUpdate)));

            } catch (Exception e) {
                System.err.println("Error reading from RuneLite API:");
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error connecting to RuneLite API:");
            e.printStackTrace();
        }
    }

    private static void parseJsonPrices(String jsonText) {
        // 4. Populate a temporary map first. This keeps the live data intact while processing.
        Map<Integer, Pair<Integer, Integer>> tempMap = new ConcurrentHashMap<>();

        JSONObject root = new JSONObject(jsonText);
        JSONObject data = root.getJSONObject("data");

        for (String key : data.keySet()) {
            try {
                int itemId = Integer.parseInt(key);
                JSONObject itemData = data.getJSONObject(key);
                int high = itemData.optInt("high", -1);
                if (high > 0) {
                    tempMap.put(itemId, new Pair<>(high, 0));
                }
            } catch (Exception e) {
                System.err.println("Skipping item ID " + key + ": " + e.getMessage());
            }
        }

        // 5. ATOMIC SWAP: The live map reference changes instantly.
        // Players will experience zero downtime or half-loaded item errors.
        geValues = tempMap;
        lastPriceUpdate = System.currentTimeMillis();
    }
}