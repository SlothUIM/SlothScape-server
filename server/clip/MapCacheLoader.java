package server.clip;

import java.io.*;
import java.util.*;

public class MapCacheLoader {
    private static final Map<String, byte[]> cache = new HashMap<>();

    public static void load() throws IOException {
        cache.clear();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("./Data/world/map_cache.dat/")))) {
            int count = in.readInt();
            for (int i = 0; i < count; i++) {
                int nameLen = in.readInt();
                byte[] nameBytes = new byte[nameLen];
                in.readFully(nameBytes);
                String name = new String(nameBytes, "UTF-8");
                int dataLen = in.readInt();
                byte[] data = new byte[dataLen];
                in.readFully(data);
                cache.put(name, data);
            }
        }
        //System.out.println("Loaded " + cache.size() + " entries from " + cachePath);
    }

    public static byte[] get(String name) {
        return cache.get(name);
    }

    public static Set<String> keys() {
        return cache.keySet();
    }
}