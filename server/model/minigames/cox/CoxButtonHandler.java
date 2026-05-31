package server.model.minigames.cox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import server.model.minigames.raids.RaidParty;
import server.model.players.Player;

public class CoxButtonHandler {
    public enum SortColumn {
        SIZE, COMBAT, LEADER, TOTAL, CHALLENGE, LAYOUT, TIME, ROLE, NONE
    }
    public static boolean handleButton(Player player, int buttonId) {
        // =================================================================
        // JOINING A PARTY FROM THE RECRUITMENT BOARD
        // Row triggers start at 52201 and go up by 15 for 40 rows (Max 52786)
        // =================================================================
        if (buttonId >= 52201 && buttonId <= 52786 && (buttonId - 52201) % 15 == 0) {

            // 1. Safety Check
            if (player.coxParty != null) {
                player.sendMessage("You must leave your current party before joining another.");
                return true;
            }

            // 2. Figure out exactly which row index they clicked (0 through 39)
            int rowIndex = (buttonId - 52201) / 15;

            // 3. Rebuild the exact list of advertised parties the player is currently seeing
            List<RaidParty> displayedParties = new ArrayList<>();
            for (RaidParty party : CoxPartyManager.activeParties) {
                if (party.isAdvertised()) {
                    displayedParties.add(party);
                }
            }

            // [!] IMPORTANT NOTE: If you ever uncomment your sorting logic for the Recruitment Board,
            // you MUST sort this 'displayedParties' list right here using that exact same logic
            // so the rowIndex perfectly matches the sorted visual list on their screen!

            // 4. Check if the row they clicked actually contains a party
            if (rowIndex < displayedParties.size()) {
                RaidParty targetParty = displayedParties.get(rowIndex);

                // 5. Add them to the party!
                // (Your RaidParty.java class already safely handles the Full, Banned, and Raid Active checks!)
                targetParty.addMember(player);

                // 6. Transition the UI to the Party lobby
                player.getPA().showInterface(51000);

                // Force a refresh for the player joining so their UI populates instantly
                targetParty.refreshPartyInterface();
            }
            return true;
        }
        switch (buttonId) {

            /* =========================================
               COX RECRUITMENT BOARD (52000)
               ========================================= */

            // --- SORT ARROWS ---
            case 52010: handleSortClick(player, SortColumn.SIZE, 1510); return true;
            case 52011: handleSortClick(player, SortColumn.COMBAT, 1511); return true;
            case 52012: handleSortClick(player, SortColumn.LEADER, 1512); return true;
            case 52013: handleSortClick(player, SortColumn.TOTAL, 1513); return true;
            case 52014: handleSortClick(player, SortColumn.CHALLENGE, 1514); return true;
            case 52015: handleSortClick(player, SortColumn.LAYOUT, 1515); return true;
            case 52016: handleSortClick(player, SortColumn.TIME, 1516); return true;

            // --- BOTTOM BUTTONS ---
            case 52020: // Refresh Board
                // Lock the button visually
                player.getPA().sendConfig(1517, 1);
                player.getPA().sendFrame126("---", 52020); // Set button text to ---

                // Pull the live data using the manager
                CoxPartyManager.refreshRecruitmentBoard(player);

                return true;

            case 52021: // Make Party Button
                if (player.coxParty != null) {
                    player.getPA().showInterface(51000);
                    player.coxParty.refreshPartyInterface();
                    return true;
                }

                // Otherwise, create a brand new one
                CoxPartyManager.createParty(player);
                return true;


            /* =========================================
               COX RAIDING PARTY MENU (51000)
               ========================================= */

            // --- SORT ARROWS ---
            case 51151: handleSortClick(player, SortColumn.LEADER, 1501); return true; // Reusing Leader enum for "Name"
            case 51152: handleSortClick(player, SortColumn.COMBAT, 1502); return true;
            case 51153: handleSortClick(player, SortColumn.TOTAL, 1503); return true;
            case 51154: handleSortClick(player, SortColumn.ROLE, 1504); return true;

            // --- BOTTOM BUTTONS ---
            case 51015: // Advertise Button
                if (player.coxParty != null && player.coxParty.getLeader() == player) {
                    player.coxParty.setAdvertised(true);
                    player.sendMessage("Your party is now visible on the Recruitment Board.");
                } else {
                    player.sendMessage("Only the party leader can advertise the group.");
                }
                return true;

            case 51017: // Disband / Leave
                if (player.coxParty != null) {
                    if (player.coxParty.getLeader() == player) {
                        // Leader clicked it: Kill the whole party
                        CoxPartyManager.disbandParty(player.coxParty);
                    } else {
                        // Normal member clicked it: Just remove them
                        player.coxParty.removeMember(player);
                        player.getPA().closeAllWindows();
                        player.sendMessage("You have left the raiding party.");
                    }
                }
                return true;

            case 51019: // Back
                CoxPartyManager.refreshRecruitmentBoard(player);

                // 2. Open the Recruitment Board interface
                player.getPA().showInterface(52000);
                return true;

            case 51026: // Refresh Party Interface
                player.getPA().sendConfig(1508, 2);
                player.getPA().sendFrame126("---", 51026); // Set button text to ---
                if (player.coxParty != null) {
                    player.coxParty.refreshPartyInterface();
                }
                return true;


            // --- SETTINGS (Leader Only) ---
            case 51011: // Toggle Challenge Mode Checkbox
                if (player.coxParty != null && player.coxParty.getLeader() == player) {
                    boolean isCM = !player.coxParty.isChallengeMode();
                    player.coxParty.setChallengeMode(isCM);

                    // Update the checkbox visual for EVERYONE in the party
                    for (Player p : player.coxParty.getMembers()) {
                        p.getPA().sendConfig(1500, isCM ? 1 : 0);
                    }
                } else {
                    player.sendMessage("Only the party leader can change raid settings.");
                }
                return true;

            // Text Input Triggers
            case 51006: // Size
            case 51007: // Combat
            case 51008: // Total
            case 51009: // Scaling
                if (player.coxParty != null && player.coxParty.getLeader() != player) {
                    player.sendMessage("Only the party leader can change raid settings.");
                    return true;
                }

                if (buttonId == 51006) player.setChatInputType("COX_SIZE");
                if (buttonId == 51007) player.setChatInputType("COX_COMBAT");
                if (buttonId == 51008) player.setChatInputType("COX_TOTAL");
                if (buttonId == 51009) player.setChatInputType("COX_SCALING");

                player.getPA().sendEnterAmount("Set the target amount (or 0 to clear it):");
                return true;

            case 51010: // Map layout
                if (player.coxParty != null && player.coxParty.getLeader() == player) {
                    player.getDH().sendOptionsTitle("How large of a layout to generate?", "Small", "Large", "Full");
                    // Set your dialogue action here
                } else {
                    player.sendMessage("Only the party leader can change raid settings.");
                }
                return true;
        }
        return false;
    }

    /**
     * Handles the universal sorting logic for the UI
     */
    private static void handleSortClick(Player player, SortColumn clickedColumn, int configId) {
        if (player.activeCoxSort == clickedColumn) {
            player.coxSortDescending = !player.coxSortDescending;
        } else {
            player.activeCoxSort = clickedColumn;
            player.coxSortDescending = true;
        }

        player.getPA().sendConfig(configId, player.coxSortDescending ? 1 : 0);

        // Optional: clear other active sort arrows based on the interface
        // You'd check if configId is in the 1510-1516 range (Board) or 1501-1504 range (Party)

        // Refresh the appropriate interface
        if (configId >= 1510) {
            CoxPartyManager.refreshRecruitmentBoard(player);
        } else if (player.coxParty != null) {
            player.coxParty.refreshPartyInterface();
        }
    }
}