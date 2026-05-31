package server.util.log;

import server.model.players.PacketType;
import server.model.players.Player;
import server.util.Misc;

public class ReportAbuse implements PacketType {

    @Override
    public void processPacket(Player player, int packetType, int packetSize) {
        // 1. Read the player name (QWord/Long)
        long nameLong = player.getInStream().readQWord();
        String reportedPlayer = Misc.longToPlayerName(nameLong);

        // 2. Read the rule ID (WordBigEndian)
        // Client sends: j - 601
        int ruleId = player.getInStream().readUnsignedWord();

        // 3. Read the mute flag (WordBigEndian)
        // Client sends: canMute ? 1 : 0
        int mutePlayer = player.getInStream().readUnsignedWord();

        // Optional: Debug print to verify it works
        // System.out.println(player.playerName + " reported " + reportedPlayer + " for rule: " + ruleId);

        // Logic for handling the report
        handleReport(player, reportedPlayer, ruleId, mutePlayer == 1);
    }

    private void handleReport(Player player, String reported, int rule, boolean mute) {
        // Your logic here: Save to file, notify staff, or perform the mute
    	player.sendMessage("Thank you for submitting your abuse report");
    }
}