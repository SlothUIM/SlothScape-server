package server.clip;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import server.util.compress.GZIPUtil;

/**
 * @author ReverendDread
 * Modified for unified cache loading
 */
@Slf4j @Data
public class MapIndexLoader {

    private static final List<RegionData> regions = Lists.newArrayList();

    private static final Map<RegionData, byte[]> cachedLandscape = Maps.newConcurrentMap();
    private static final Map<RegionData, byte[]> cachedObjects = Maps.newConcurrentMap();

    public static void load() {
        try {
            // Use the unified cache loader to get map_index data
            byte[] buffer = MapCacheLoader.get("map_index");
            if (buffer == null) {
                throw new IOException("map_index not found in cache!");
            }
            ByteStream in = new ByteStream(buffer);
            int size = in.readUnsignedWord();
            for (int i = 0; i < size; i++) {
                regions.add(new RegionData(in.readUnsignedWord(), in.readUnsignedWord(), in.readUnsignedWord()));
            }
        } catch (IOException e) {
            log.error("Failed to load map_index from cache!");
            e.printStackTrace();
        }
        Region.load();
    }

    public static Optional<RegionData> lookup(int regionId) {
        return regions.stream().filter(regionData -> regionData.getRegionHash() == regionId).findFirst();
    }

    public static Stream<RegionData> stream() {
        return regions.stream();
    }

    public static ByteStream getObjectData(Optional<RegionData> data) {
        if (!data.isPresent())
            return null;
        if (cachedObjects.containsKey(data.get())) {
            return new ByteStream(cachedObjects.get(data.get()));
        }
        // Use cache for objects
        String key = data.get().getObjects() + ".gz";
        byte[] zipped = MapCacheLoader.get(key);
        if (zipped != null) {
            try {
                byte[] unzipped = GZIPUtil.decompress(zipped);
                cachedObjects.put(data.get(), unzipped);
                return new ByteStream(unzipped);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ByteStream getLandscapeData(Optional<RegionData> data) {
        if (!data.isPresent())
            return null;
        if (cachedLandscape.containsKey(data.get())) {
            return new ByteStream(cachedLandscape.get(data.get()));
        }
        // Use cache for landscape
        String key = data.get().getLandscape() + ".gz";
        byte[] zipped = MapCacheLoader.get(key);
        if (zipped != null) {
            try {
                byte[] unzipped = GZIPUtil.decompress(zipped);
                cachedLandscape.put(data.get(), unzipped);
                return new ByteStream(unzipped);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}