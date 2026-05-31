package server.model.minigames.trawler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import server.model.players.Player;

public class FishingTrawlerLobby {

    private static final List<Player> waitingRoom = new ArrayList<>();

    // Use a constant so you only have to change the wait time in one place
    private static final int WAIT_TIME_TICKS = 100;
    private static int timer = WAIT_TIME_TICKS;

    // Put this in your main server process() loop!
    public static void process() {
        if (waitingRoom.isEmpty()) {
            timer = WAIT_TIME_TICKS;
            return;
        }

        // 1. SAFE SCRUB: Remove any players who logged out, crashed, or disconnected
        Iterator<Player> it = waitingRoom.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p == null || p.disconnected || !p.isActive) {
                it.remove();
            }
        }

        // Check again in case the list is now empty after the scrub
        if (waitingRoom.isEmpty()) {
            timer = WAIT_TIME_TICKS;
            return;
        }

        timer--;

        // Optional: Update an interface here so players know how long is left
        /*
        int secondsLeft = (timer * 600) / 1000;
        for (Player p : waitingRoom) {
            p.getPA().sendFrame126("Time till departure: " + secondsLeft + "s", SOME_INTERFACE_ID);
        }
        */

        if (timer <= 0) {
            startTrip();
        }
    }

    public static void joinLobby(Player p) {
        if (!waitingRoom.contains(p)) {
            waitingRoom.add(p);
            int secondsLeft = (timer * 600) / 1000;
            p.sendMessage("You board the Fishing Trawler. The boat leaves in " + secondsLeft + " seconds.");
        }
    }

    public static void leaveLobby(Player p) {
        waitingRoom.remove(p);
    }

    private static void startTrip() {
        World1Trawler game = new World1Trawler();

        // Because of the safe scrub in process(), we know 100% of these players are online
        for (Player p : waitingRoom) {
            game.addPlayer(p);
        }

        waitingRoom.clear();
        timer = WAIT_TIME_TICKS;
        game.start();
    }
}