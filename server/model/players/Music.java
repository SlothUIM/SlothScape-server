package server.model.players;

import server.event.CycleEvent;
import server.event.CycleEventHandler;
import server.model.players.packets.CITY_MUSIC;
import server.event.CycleEventContainer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import server.util.Misc;
import server.world.Boundary;

public class Music {

    public final Set<Integer> unlockedSongIds = new HashSet<>();
    public final Set<Integer> favoriteSongIds = new HashSet<>();

    private final Player player;

    public Music(Player c) {
        this.player = c;
    }

    /** Add song to favorites by songId */
    public void addToFavorites(int songId) {
        if (!favoriteSongIds.contains(songId)) {
            favoriteSongIds.add(songId);
        }
    }

    /** Remove song from favorites by songId */
    public void removeFromFavorites(int songId) {
        favoriteSongIds.remove((Integer) songId);
    }

    /** Check if a song is in favorites */
    public boolean isFavorite(int songId) {
        return favoriteSongIds.contains(songId);
    }

    /** * Check if a song is unlocked.
     * FIX: Admins automatically return true here. This stops Region triggers
     * from repeatedly trying to unlock songs and spamming the chatbox.
     */
    public boolean isSongUnlocked(int songId) {
        if (player.playerRights >= 3) {
            return true;
        }
        return unlockedSongIds.contains(songId);
    }

    /** Add a song to the unlocked list */
    public void unlockSong(int songId) {
        // Because admins return true in isSongUnlocked, this safely ignores them
        if (!isSongUnlocked(songId)) {
            unlockedSongIds.add(songId);
        }
    }

    /** * NEW: Unlocks all tracks at once and refreshes the UI.
     */
    public void unlockAllTracks() {
        for (CITY_MUSIC song : CITY_MUSIC.values()) {
            if (song != null) {
                // Bypass the isSongUnlocked check here so even admins can 
                // physically add the IDs to their save file if they want to.
                unlockedSongIds.add(song.getMusic());
            }
        }
        player.sendMessage("@gre@You have successfully unlocked all music tracks!");
        refreshPlaylist(player, false);
    }

    /** Format song name nicely */
    public String formatSongName(String name) {
        String[] words = name.split("_");
        StringBuilder formattedName = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            formattedName.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
            if (i < words.length - 1) {
                formattedName.append(" ");
            }
        }
        return formattedName.toString();
    }

    /** Play a song by its music ID */
    public void playSong(Player c, int songId, boolean musicOn, boolean regionMusic) {
        CITY_MUSIC song = CITY_MUSIC.getByMusicId(songId);
        if (song != null) {
            String formatted = formatSongName(song.getName());
            if (isSongUnlocked(songId)) { // Removed playerRights check (handled centrally now)
                c.MusicOn = musicOn;
                c.RegionMusicOn = regionMusic;
                c.getPA().musicManager("PLAY", songId);
                c.currentSong = songId;
                c.getPA().sendFrame126(formatted, 5450);
            } else {
                c.MusicOn = false;
                c.sendMessage("You have not unlocked " + formatted + ".");
            }
        } else {
            c.MusicOn = false;
            c.sendMessage("Invalid song ID.");
        }
    }

    public void nextSong(Player c) {
        if (c.sortedSongs == null || c.sortedSongs.length == 0)
            return;

        int currentIndex = -1;
        for (int i = 0; i < c.sortedSongs.length; i++) {
            if (c.sortedSongs[i].getMusic() == c.currentSong) {
                currentIndex = i;
                break;
            }
        }

        int nextIndex = (currentIndex == -1 || currentIndex + 1 >= c.sortedSongs.length) ? 0 : currentIndex + 1;
        CITY_MUSIC nextSong = c.sortedSongs[nextIndex];
        playSong(c, nextSong.getMusic(), true, false);
    }

    public void prevSong(Player c) {
        if (c.sortedSongs == null || c.sortedSongs.length == 0)
            return;

        int currentIndex = -1;
        for (int i = 0; i < c.sortedSongs.length; i++) {
            if (c.sortedSongs[i].getMusic() == c.currentSong) {
                currentIndex = i;
                break;
            }
        }

        int prevIndex = (currentIndex == -1 || currentIndex - 1 < 0) ? c.sortedSongs.length - 1 : currentIndex - 1;
        CITY_MUSIC prevSong = c.sortedSongs[prevIndex];
        playSong(c, prevSong.getMusic(), true, false);
    }

    public void shufflePlaylist(Player c) {
        if (c.sortedSongs == null || c.sortedSongs.length == 0)
            return;

        List<CITY_MUSIC> shuffled = new ArrayList<>(Arrays.asList(c.sortedSongs));
        Collections.shuffle(shuffled);
        c.shuffleMode = true;

        c.sortedSongs = shuffled.toArray(new CITY_MUSIC[0]);

        CITY_MUSIC randomSong = c.sortedSongs[0];
        playSong(c, randomSong.getMusic(), true, false);
        refreshPlaylist(c, false);
        c.sendMessage("Playlist shuffled.");
    }

    public void setText(Player c, int listSize) {
        List<Integer> sortedList = new ArrayList<>(c.onPlaylist ? favoriteSongIds : unlockedSongIds);
        Collections.sort(sortedList);

        int frameBase = 50473;

        if (c.lastSentSongNames == null) {
            c.lastSentSongNames = new String[374];
            Arrays.fill(c.lastSentSongNames, "");
        }

        for (int i = 0; i < 374; i++) {
            String nameToSend = "";

            if (i < listSize && i < sortedList.size()) {
                int musicId = sortedList.get(i);
                CITY_MUSIC song = CITY_MUSIC.getByMusicId(musicId);
                if (song != null) {
                    nameToSend = formatSongName(song.getName());
                }
            }

            if (!nameToSend.equals(c.lastSentSongNames[i])) {
                c.getPA().sendFrame126(nameToSend, frameBase + i);
                c.lastSentSongNames[i] = nameToSend;
            }
        }
    }

    public void updateSingleSong(Player c, int songId, int index) {
        boolean isFavorite = favoriteSongIds.contains(songId);
        c.getPA().sendFrame36(800 + index, isFavorite ? 1 : 0);
        c.lastSentSongConfigs[index] = isFavorite ? 1 : 0;
    }
    /** * Safely grabs favorites and sorts them alphabetically to match the UI
     */
    public List<Integer> getSortedFavorites() {
        List<Integer> list = new ArrayList<>(favoriteSongIds);
        list.sort((id1, id2) -> {
            CITY_MUSIC s1 = CITY_MUSIC.getByMusicId(id1);
            CITY_MUSIC s2 = CITY_MUSIC.getByMusicId(id2);
            String n1 = s1 != null ? formatSongName(s1.getName()) : "";
            String n2 = s2 != null ? formatSongName(s2.getName()) : "";
            return n1.compareTo(n2);
        });
        return list;
    }
    /**
     * Handles all music tab button clicks.
     * Returns true if the button was a music button, false otherwise.
     */
    public boolean handleMusicButton(int actionButtonId) {
        int uiIndex = -1;
        boolean isStarToggle = false;

        // --- 1. Map the Action Button to the UI Index (0 to 373) ---

        // Play Button: Top Half (Indexes 0 to 214)
        if (actionButtonId >= 197041 && actionButtonId <= 197255) {
            uiIndex = actionButtonId - 197041;
        }
        // Play Button: Bottom Half (Indexes 215 to 373)
        else if (actionButtonId >= 198000 && actionButtonId <= 198158) {
            uiIndex = (actionButtonId - 198000) + 215;
        }

        // Star Button: Top Half (Indexes 0 to 255)
        else if (actionButtonId >= 196000 && actionButtonId <= 196255) {
            uiIndex = actionButtonId - 196000;
            isStarToggle = true;
        }
        // Star Button: Bottom Half (Indexes 256 to 373)
        else if (actionButtonId >= 197000 && actionButtonId <= 197117) {
            uiIndex = (actionButtonId - 197000) + 256;
            isStarToggle = true;
        }

        // If it wasn't any of our mapped music buttons, ignore it
        if (uiIndex == -1) {
            return false;
        }

        // --- 2. Resolve the actual Song ID ---
        int targetSongId = -1;

        if (player.onPlaylist) {
            // Pull from the alphabetical favorites list
            List<Integer> favorites = getSortedFavorites();
            if (uiIndex >= 0 && uiIndex < favorites.size()) {
                targetSongId = favorites.get(uiIndex);
            }
        } else {
            // Pull from the master sorted list
            if (player.sortedSongs != null && uiIndex >= 0 && uiIndex < player.sortedSongs.length) {
                targetSongId = player.sortedSongs[uiIndex].getMusic();
            }
        }

        // If they clicked an empty line at the bottom of the list, ignore it
        if (targetSongId == -1) {
            return true;
        }

        // --- 3. Execute the Action ---
        if (isStarToggle) {
            toggleFavorite(targetSongId, uiIndex);
        } else {
            playSong(player, targetSongId, true, false);
        }

        return true;
    }

    private void toggleFavorite(int songId, int uiIndex) {
        CITY_MUSIC song = CITY_MUSIC.getByMusicId(songId);
        if (song == null) return;

        String formatted = formatSongName(song.getName());
        int configId = 800 + uiIndex;

        if (isFavorite(songId)) {
            removeFromFavorites(songId);
            player.sendMessage("You have removed: " + formatted + " from your favourites playlist.");
            player.getPA().sendFrame36(configId, 0);

            // If they removed it while viewing the playlist, refresh the UI so it disappears
            if (player.onPlaylist) {
                refreshPlaylist(player, true);
            }
        } else {
            addToFavorites(songId);
            player.sendMessage("You have added: " + formatted + " to your favourites playlist.");
            player.getPA().sendFrame36(configId, 1);
        }
    }
    /** Refresh playlist for the client */
    public void refreshPlaylist(Player c, boolean onPlaylist) {
        int configIdBase = 800;
        int frameTextBase = 50473;

        if (c.sortedSongs == null || c.shuffleMode) {
            // Fast, zero-allocation sorting
            c.sortedSongs = Arrays.stream(CITY_MUSIC.values())
                    .sorted(Comparator.comparing(CITY_MUSIC::getFormattedName))
                    .toArray(CITY_MUSIC[]::new);
            c.shuffleMode = false;
        }

        CITY_MUSIC[] allSongs = c.sortedSongs;
        int totalSongs = allSongs.length;

        String countString = onPlaylist ? String.valueOf(favoriteSongIds.size()) : unlockedSongIds.size() + "/" + totalSongs;
        c.getPA().sendFrame126(countString, 50023);

        c.songConfigMap.clear();

        // FIX: Cache the favorites list ONCE outside the loop to prevent memory/CPU leaks
        List<Integer> cachedFavs = new ArrayList<>(favoriteSongIds);

        for (int i = 0; i < 373; i++) {
            String nameToSend = "";
            int configToSend = 0;

            if (onPlaylist) {
                if (i < cachedFavs.size()) {
                    int favId = cachedFavs.get(i); // Now pulls safely from the cached list
                    CITY_MUSIC song = CITY_MUSIC.getByMusicId(favId);
                    if (song != null) {
                        nameToSend = "@gre@" + formatSongName(song.getName());
                        configToSend = 1;
                        c.songConfigMap.put(favId, configIdBase + i);
                    }
                }
            } else {
                if (i < totalSongs) {
                    CITY_MUSIC song = allSongs[i];
                    int songId = song.getMusic();
                    boolean unlocked = isSongUnlocked(songId);
                    boolean isFavorite = favoriteSongIds.contains(songId);

                    nameToSend = (unlocked ? "@gre@" : "") + formatSongName(song.getName());
                    configToSend = isFavorite ? 1 : 0;
                    c.songConfigMap.put(songId, configIdBase + i);
                }
            }

            if (!nameToSend.equals(c.lastSentSongNames[i])) {
                c.getPA().sendFrame126(nameToSend, frameTextBase + i);
                c.lastSentSongNames[i] = nameToSend;
            }

            if (configToSend != c.lastSentSongConfigs[i]) {
                c.getPA().sendFrame36(configIdBase + i, configToSend);
                c.lastSentSongConfigs[i] = configToSend;
            }
        }
    }
}