package server.model.minigames.raids;

import lombok.Getter;
import lombok.Setter;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.minigames.cox.CoxButtonHandler;
import server.model.minigames.cox.CoxPartyManager;
import server.model.players.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class RaidParty {

    private Player leader;
    private List<Player> members;

    // Party Preferences
    private int preferredSize = 0;
    private int preferredCombat = 0;
    private int preferredTotal = 0;
    private int scaling = 0;
    private boolean challengeMode = false;
    private boolean raidActive = false;
    private boolean advertised = false;
    private List<String> bannedPlayers = new ArrayList<>();
    private String mapLayout = "Small"; // Small, Large, Full
    private long startTime;

    public RaidParty(Player leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
        this.startTime = System.currentTimeMillis(); // <--- ADD THIS
    }

    // --- Core Methods ---
    /**
     * Calculates the true scale of the raid based on party size vs manual UI scaling.
     */
    public int getEffectiveScale() {
        int actualSize = members.size();
        return Math.max(actualSize, scaling);
    }
    public void addMember(Player player) {
        if (raidActive) {
            player.sendMessage("This raiding party has already begun.");
            return;
        }

        if (members.size() >= 40) {
            player.sendMessage("This raiding party is currently full.");
            return;
        }

        if (bannedPlayers.contains(player.playerName.toLowerCase())) {
            player.sendMessage("You have been banned from joining this party.");
            return;
        }

        if (!members.contains(player)) {
            members.add(player);
            player.coxParty = this;
            refreshPartyInterface();
        }
    }

    public void removeMember(Player player) {
        if (members.contains(player)) {
            members.remove(player);
            player.coxParty = null;

            // Leader left or disconnected
            if (player == leader) {
                if (members.isEmpty() || raidActive) {
                    // If the raid is already active, we don't promote, we just let the raid continue or fail
                    // If it's empty, nuke it.
                    CoxPartyManager.disbandParty(this);
                } else {
                    // Promote the next person in the lobby
                    leader = members.get(0);
                    for (Player p : members) {
                        if (p != null) {
                            p.sendMessage("@blu@" + leader.playerName + " has been promoted to party leader.");
                        }
                    }
                    refreshPartyInterface();
                }
            } else {
                refreshPartyInterface();
            }
        }
    }

    // Pushes the live data to the 51000 interface for EVERYONE in the party
    // Pushes the live data to the 51000 interface for EVERYONE in the party
    public void refreshPartyInterface() {
        for (Player p : members) {
            if (p != null) {
                updateInterfaceFor(p);
            }
        }
    }

    // Handles the individual interface update for a specific player
    private void updateInterfaceFor(Player p) {
        // 1. Top Title
        String leaderName = leader.playerName;
        if (leaderName != null && leaderName.length() > 0) {
            // Capitalize first letter, lowercase the rest
            leaderName = leaderName.substring(0, 1).toUpperCase() + leaderName.substring(1).toLowerCase();
        } else {
            leaderName = "Unknown";
        }
        p.getPA().sendFrame126("Raiding Party of " + leaderName + " (" + members.size() + ")", 51002);

        boolean isLeader = (p == leader);

        // 1. Toggle the clickability of the buttons based on rank!
        p.getPA().sendInterfaceActionState(51006, isLeader ? 1 : 0); // Size
        p.getPA().sendInterfaceActionState(51007, isLeader ? 1 : 0); // Combat
        p.getPA().sendInterfaceActionState(51008, isLeader ? 1 : 0); // Total
        p.getPA().sendInterfaceActionState(51009, isLeader ? 1 : 0); // Scaling

        // 2. Send the text. If they aren't the leader, turn the text grey so they KNOW it's disabled!
        String color = isLeader ? "@or1@" : "@gry@";

        p.getPA().sendFrame126(color + "Preferred party size: " + (preferredSize == 0 ? "---" : preferredSize), 51006);
        p.getPA().sendFrame126(color + "Preferred combat level: " + (preferredCombat == 0 ? "---" : preferredCombat), 51007);
        p.getPA().sendFrame126(color + "Preferred skill total: " + (preferredTotal == 0 ? "---" : preferredTotal), 51008);
        p.getPA().sendFrame126(color + "Scaling: " + (scaling == 0 ? "---" : scaling), 51009);
        p.getPA().sendFrame126("Map Layout: " + mapLayout, 51010);

        // 3. Challenge Mode Checkbox Visual
        p.getPA().sendConfig(1500, challengeMode ? 1 : 0);

        // 4. Dynamic Button Text (Button 51017 is the button, 51018 is its text)
        if (p == leader) {
            p.getPA().sendFrame126("Disband", 51018);
            // Optional: The server can flicker this to "---" during an auto-refresh cycle
        } else {
            p.getPA().sendFrame126("Leave", 51018);
        }

        // 5. Create a copy of the member list and sort it based on THIS player's preference
        List<Player> sortedMembers = new ArrayList<>(members);
        sortMembers(sortedMembers, p.activeCoxSort, p.coxSortDescending);

        p.getPA().sendInterfaceActionState(51400, 0); // Size
        // 6. Populate the 40 rows
        int maxPlayers = 40;
        for (int i = 0; i < maxPlayers; i++) {
            int nameId = 51400 + i;
            int combatId = 51440 + i;
            int totalId = 51480 + i;
            int roleId = 51520 + i;
            int triggerId = 51600 + i;
            if (i < sortedMembers.size()) {
                // We have a player for this row, send their actual data
                Player member = sortedMembers.get(i);
                String targetName = member.playerName;
                if (targetName != null && targetName.length() > 0) {
                    // Capitalize first letter, lowercase the rest
                    targetName = targetName.substring(0, 1).toUpperCase() + targetName.substring(1).toLowerCase();
                } else {
                    targetName = "Unknown";

                }
                p.getPA().setInterfaceVisible(triggerId, true);
                // Highlight the player's own name in white, and others based on OSRS colors if desired
                p.getPA().sendFrame126(targetName, nameId);
                p.getPA().sendInterfaceActionState(nameId, 8); // Size
                p.getPA().sendFrame126(String.valueOf(member.combatLevel), combatId);

                // NOTE: Change 'member.totalLevel' to whatever your server's total level variable/method is
                p.getPA().sendFrame126(String.valueOf(member.getSkills().getTotalLevel()), totalId);

                // Role is left blank unless you build a custom role selector!
                p.getPA().sendFrame126("-", roleId);
            } else {

                p.getPA().setInterfaceVisible(triggerId, false);
                // No player for this row, clear the text completely so it's blank
                p.getPA().sendFrame126("", nameId);
                p.getPA().sendFrame126("", combatId);
                p.getPA().sendFrame126("", totalId);
                p.getPA().sendInterfaceActionState(triggerId, 0); // Size
                p.getPA().sendFrame126("", roleId);
            }
        }
        CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                p.getPA().sendConfig(1508, 1);
                p.getPA().sendFrame126("@or1@Refresh", 51026);
                container.stop();
            }
            @Override
            public void stop() {
            }
        }, 2);
    }
    public void kickMember(Player target) {
        if (members.contains(target)) {
            bannedPlayers.add(target.playerName.toLowerCase());
            removeMember(target);
        }
    }
    // The core sorting logic
    public void sortMembers(List<Player> list, CoxButtonHandler.SortColumn sort, boolean desc) {
        if (sort == CoxButtonHandler.SortColumn.NONE) return;

        Collections.sort(list, (p1, p2) -> {
            int result = 0;
            switch (sort) {
                case LEADER: // Reusing the LEADER enum for the "Name" column
                    result = p1.playerName.compareToIgnoreCase(p2.playerName);
                    break;
                case COMBAT:
                    result = Integer.compare(p1.combatLevel, p2.combatLevel);
                    break;
                case TOTAL:
                    result = Integer.compare(p1.getSkills().getTotalLevel(), p2.getSkills().getTotalLevel());
                    break;
                case ROLE:
                    // If you add Roles later, you'd compare them here
                    result = 0;
                    break;
            }
            // Flip the result if the player wants it descending
            return desc ? -result : result;
        });
    }

    // --- Getters & Setters ---
    public Player getLeader() { return leader; }
    public List<Player> getMembers() { return members; }
    public int getMemberCount() { return members.size(); }

    public int getPreferredSize() { return preferredSize; }
    public void setPreferredSize(int preferredSize) { this.preferredSize = preferredSize; }

    public int getPreferredCombat() { return preferredCombat; }
    public void setPreferredCombat(int preferredCombat) { this.preferredCombat = preferredCombat; }

    public int getPreferredTotal() { return preferredTotal; }
    public void setPreferredTotal(int preferredTotal) { this.preferredTotal = preferredTotal; }

    public boolean isChallengeMode() { return challengeMode; }
    public void setChallengeMode(boolean challengeMode) { this.challengeMode = challengeMode; }

    public int getScaling() { return scaling; }
    public void setScaling(int scaling) { this.scaling = scaling; }
}