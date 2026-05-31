package server.util.definitions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import server.util.Buffer;

public final class AnimationDefinition {

    public static AnimationDefinition[] cache;
    public static int[] streamIndices;
    private static Buffer stream;
    private static int cacheIndex;

    public int id;
    public int frameCount;
    public int[] durations;
    public int[] primaryFrames;
    public int animatingPrecedence = -1;
    public int walkingPrecedence = -1;
    public int replayMode = 1;

    public static void unpackConfig() throws IOException {
        // Loading from folder just like your ObjectDef
        byte[] dat = Files.readAllBytes(new File("./Data/world/animations/seq.dat").toPath());
        stream = new Buffer(dat);
        
        int totalAnims = stream.readUnsignedWord();
        System.out.println("Loaded: " + totalAnims + " animation definitions.");
        
        streamIndices = new int[totalAnims];
        int offset = 2;
        for (int i = 0; i < totalAnims; i++) {
            streamIndices[i] = offset;
            // In seq.dat, the index usually stores the length of each config block
            // This logic depends on how your seq.idx or seq.dat header is packed.
            // If you don't have a seq.idx, you may need to pre-parse the file.
        }

        cache = new AnimationDefinition[50]; // Larger cache for animations
        for (int i = 0; i < 50; i++) {
            cache[i] = new AnimationDefinition();
        }
    }

    public static AnimationDefinition forID(int i) {
        if (i < 0 || i >= streamIndices.length) return null;

        for (int j = 0; j < 50; j++) {
            if (cache[j].id == i) return cache[j];
        }

        cacheIndex = (cacheIndex + 1) % 50;
        AnimationDefinition def = cache[cacheIndex];
        stream.currentOffset = streamIndices[i];
        def.id = i;
        def.readValues(stream);
        return def;
    }

    private void readValues(Buffer stream) {
        while (true) {
            int opcode = stream.readUnsignedByte();
            if (opcode == 0) break;
            if (opcode == 1) {
                frameCount = stream.readUnsignedWord();
                durations = new int[frameCount];
                primaryFrames = new int[frameCount];
                for (int j = 0; j < frameCount; j++) durations[j] = stream.readUnsignedWord();
                for (int j = 0; j < frameCount; j++) primaryFrames[j] = stream.readUnsignedWord();
                for (int j = 0; j < frameCount; j++) primaryFrames[j] += stream.readUnsignedWord() << 16;
            } else if (opcode == 2) {
                stream.readUnsignedWord(); // loopOffset
            } else if (opcode == 3) {
                int k = stream.readUnsignedByte();
                for (int l = 0; l < k; l++) stream.readUnsignedByte();
            } else if (opcode == 4) {
                // stretches = true;
            } else if (opcode == 5) {
                stream.readUnsignedByte();
            } else if (opcode == 6) {
                stream.readUnsignedWord();
            } else if (opcode == 7) {
                stream.readUnsignedWord();
            } else if (opcode == 8) {
                stream.readUnsignedByte();
            } else if (opcode == 9) {
                animatingPrecedence = stream.readUnsignedByte();
            } else if (opcode == 10) {
                walkingPrecedence = stream.readUnsignedByte();
            } else if (opcode == 11) {
                replayMode = stream.readUnsignedByte();
            } else if (opcode == 12) {
                int len = stream.readUnsignedByte();
                for (int i = 0; i < len; i++) stream.readUnsignedWord();
                for (int i = 0; i < len; i++) stream.readUnsignedWord();
            } else if (opcode == 13) {
                int count = stream.readUnsignedByte();
                for (int i = 0; i < count; i++) {
                    stream.readUnsignedByte(); // read24Int placeholder
                    stream.readUnsignedByte();
                    stream.readUnsignedByte();
                }
            }
        }
    }

    /**
     * Total length of the animation in client ticks (30ms approx).
     * Used to calculate speed2 in setGenericMove.
     */
    public int getDurationTicks() {
        if (durations == null) return 0;
        int total = 0;
        for (int d : durations) {
            total += d;
        }
        return total;
    }
}