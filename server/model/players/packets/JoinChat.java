package server.model.players.packets;

import server.util.Misc;
import server.world.World;
import server.model.players.Player;
import server.model.players.PacketType;
import server.content.clans.Clan;

/**
 * Chat
 **/
public class JoinChat implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		String input = Misc.longToPlayerName2(player.getInStream().readQWord()).replaceAll("_", " ");
		if (input == null || input.length() <= 0) return;

	    if (player.clan == null) {
	        // 1. Check if the input is a Title in our map
	        String founderName = World.getWorld().getClanManager().titleToFounder.get(input);
	        
	        // 2. Determine who we are actually looking for
	        // If it was a title, use the founderName from the map. Otherwise, use the raw input.
	        String targetFounder = (founderName != null) ? founderName : input;
	        player.clanName = targetFounder;
	        Clan clan = World.getWorld().getClanManager().getClan(targetFounder);

	        if (clan != null) {
	            clan.addMember(player);
	        } else {
	            // Creation logic
	            if (input.equalsIgnoreCase(player.playerName)) {
	                World.getWorld().getClanManager().create(player);
	            } else {
	                player.sendMessage("No clan found with the name or founder: " + Misc.formatPlayerName(input));
	            }
	        }
	    } else {
	        player.sendMessage("You are already in a clan chat.");
	    }
	}

}