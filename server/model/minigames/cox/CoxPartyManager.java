package server.model.minigames.cox;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.raids.RaidParty;
import server.model.players.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CoxPartyManager {

    // The global list of every active CoX party on the server
    public static List<RaidParty> activeParties = new ArrayList<>();

    /**
     * Called when a player clicks "Make Party"
     */
    public static void createParty(Player player) {
        if (player.coxParty != null) {
            player.sendMessage("You are already in a raiding party!");
            return;
        }

        RaidParty newParty = new RaidParty(player);
        player.coxParty = newParty;
        activeParties.add(newParty);

        player.sendMessage("You have created a new Raiding Party.");
        // Open the Party Interface and refresh it
        player.getPA().setInterfaceVisible(51300, true);
        player.getPA().showInterface(51000);
        newParty.refreshPartyInterface();
    }

    /**
     * Called when the leader clicks "Disband" or logs out
     */
    public static void disbandParty(RaidParty party) {
        if (party == null || !activeParties.contains(party)) return;

        for (Player member : party.getMembers()) {
            if (member != null) {
                member.coxParty = null;
                member.sendMessage("The raiding party has been disbanded.");
                member.getPA().closeAllWindows();
            }
        }

        party.getMembers().clear();
        activeParties.remove(party);
    }

    /**
     * Pushes the global list of active parties to the Recruitment Board (52000)
     */
    /**
     * Pushes the global list of active parties to the Recruitment Board (52000)
     */
    public static void refreshRecruitmentBoard(Player player) {
        // 1. Dynamic Button Text
        if (player.coxParty != null) {
            player.getPA().sendFrame126("My Party", 52021);
        } else {
            player.getPA().sendFrame126("Make party", 52021);
        }

        int maxRows = 40;
        int rowCount = 0;

        // Loop through active parties and send data to the client
        for (RaidParty party : activeParties) {
            if (!party.isAdvertised()) continue;

            if (rowCount >= maxRows) break;

            int rowBase = 52200 + (rowCount * 15);

            // --- FORMAT LEADER NAME ---
            String leaderName = party.getLeader().playerName;
            if (leaderName != null && leaderName.length() > 0) {
                // Capitalize first letter, lowercase the rest
                leaderName = leaderName.substring(0, 1).toUpperCase() + leaderName.substring(1).toLowerCase();
            } else {
                leaderName = "Unknown";
            }

            // --- FORMAT TIME WAITING ---
            long elapsedMillis = System.currentTimeMillis() - party.getStartTime();
            long totalSeconds = elapsedMillis / 1000;
            long minutes = totalSeconds / 60;
            long seconds = totalSeconds % 60;
            String timeFormatted = String.format("%d:%02d", minutes, seconds); // Outputs like "0:05" or "12:09"

            // Send the 7 columns of data
            player.getPA().sendFrame126(String.valueOf(party.getMemberCount()), rowBase + 2); // Size
            player.getPA().sendFrame126(party.getPreferredCombat() == 0 ? "-" : String.valueOf(party.getPreferredCombat()), rowBase + 3);
            player.getPA().sendFrame126(leaderName, rowBase + 4); // Capitalized Leader Name
            player.getPA().sendFrame126(party.getPreferredTotal() == 0 ? "-" : String.valueOf(party.getPreferredTotal()), rowBase + 5);
            player.getPA().sendFrame126(party.isChallengeMode() ? "Yes" : "-", rowBase + 6);

            // Icon layout logic would send a config here for rowBase + 7

            player.getPA().sendFrame126(timeFormatted, rowBase + 8); // Live Time waiting

            rowCount++;
        }

        // Clear out any remaining rows so old data doesn't visually stick around
        for (int i = rowCount; i < maxRows; i++) {
            int rowBase = 52200 + (i * 15);
            player.getPA().sendFrame126("", rowBase + 2);
            player.getPA().sendFrame126("", rowBase + 3);
            player.getPA().sendFrame126("", rowBase + 4);
            player.getPA().sendFrame126("", rowBase + 5);
            player.getPA().sendFrame126("", rowBase + 6);
            player.getPA().sendFrame126("", rowBase + 8);
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                player.getPA().sendFrame126("Refresh", 52020);
                container.stop();
            }
            @Override
            public void stop() {
            }
        }, 2);
    }
}