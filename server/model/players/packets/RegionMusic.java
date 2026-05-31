package server.model.players.packets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.model.players.Music;
import server.model.players.Player;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegionMusic {

    // Key is RegionID, Value is the full Song info
    private static final Map<Integer, SongDefinition> regionMusicMap = new HashMap<>();

    public static void load() {
        try (FileReader reader = new FileReader("./Data/json/music_definitions.json")) {
            List<SongDefinition> songs = new Gson().fromJson(reader, new TypeToken<List<SongDefinition>>(){}.getType());
            
            for (SongDefinition song : songs) {
                for (int regionId : song.getRegionIds()) {
                    regionMusicMap.put(regionId, song);
                }
            }
            System.out.println("Loaded " + songs.size() + " music definitions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handle(Player c) {
        int regionId = ((c.getX() >> 6) << 8) + (c.getY() >> 6);
        SongDefinition song = regionMusicMap.get(regionId);

        if (song != null) {
            play(c, song);
        }
    }

    private static void play(Player c, SongDefinition song) {
        int musicId = song.getMusicId();
        
        if (c.RegionMusicOn && c.currentSong != musicId) {
            // Check if song is already unlocked in player's list
            if (!c.getMusic().isSongUnlocked(musicId) || c.playerRights >= 3) {
                c.getMusic().unlockSong(musicId);
                c.sendMessage("@red@You have unlocked a new track: " + song.getName());
                // You can also use song.getHint() here if the player clicks the song list
            }

            c.getMusic().playSong(c, musicId, true, true);
            c.currentSong = musicId;
            
            // Optional: Update the music tab interface text
            c.getPA().sendFrame126(song.getName(), 4439); 
        }
    }
}