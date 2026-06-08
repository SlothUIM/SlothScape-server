package server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ServerProperties {

    private static final Properties props = new Properties();

    static {
        try (FileInputStream in = new FileInputStream("./server/server.properties")) {
            props.load(in);
            System.out.println("[Security] Successfully loaded server.properties");
        } catch (IOException e) {
            System.err.println("[Security-FATAL] Could not find server.properties!");
            e.printStackTrace();
        }
    }

    /**
     * Grabs a text string from the properties file.
     */
    public static String getString(String key) {
        return props.getProperty(key);
    }

    /**
     * Grabs a number from the properties file (with a fallback default).
     */
    public static int getInt(String key, int defaultValue) {
        String val = props.getProperty(key);
        if (val == null) return defaultValue;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}