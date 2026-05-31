package server.model.players.packets;

import server.content.clans.Clan;
import server.model.players.Player;
import server.model.players.PlayerSave;
import server.model.players.PacketType;
import server.util.Misc;
import server.world.World;

public class ReceiveString implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		String text = player.getInStream().readString();
		int index = text.indexOf(",");
		int id = Integer.parseInt(text.substring(0, index));
		String string = text.substring(index + 1);
		System.out.println("Received string: identifier=" + id + ", string=" + string);
		switch (id) {
		case 0:
			if (player.clan != null) {
				player.clan.removeMember(player);
				player.setLastClanChat("");
			}
			break;
		case 1:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 15) {
				string = string.substring(0, 15);
			}
			Clan clan = player.getPA().getClan();
			if (clan == null) {
				World.getWorld().getClanManager().create(player);
				clan = player.getPA().getClan();
			}
			if (clan != null) {
				clan.setTitle(string);
				player.sendMessage("Changes will take effect on your clan channel in the next 60 seconds.");
				player.getPA().sendFrame126(clan.getTitle(), 18306);
				player.getPA().setClanData();
				clan.save();
			}
			break;
		case 2:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			//if (string.equalsIgnoreCase(player.playerName)) {
				//break;
			//}
			/*if (string.equalsIgnoreCase(player.clanName)) {
				break;
			}*/
			if (!PlayerSave.playerExists(string)) {
				player.sendMessage("This player doesn't exist!");
				break;
			}
			clan = player.getPA().getClan();
			if (clan != null) {
				if (clan.isBanned(string)) {
					player.sendMessage("You cannot promote a banned member.");
					break;
				}
				clan.setRank(Misc.formatPlayerName(string), 2);
				player.getPA().setClanData();
				clan.save();
			}
			break;
		case 3:
			if (string.length() == 0) {
				break;
			} else if (string.length() > 12) {
				string = string.substring(0, 12);
			}
			if (string.equalsIgnoreCase(player.playerName)) {
				break;
			}
			if (!PlayerSave.playerExists(string)) {
				player.sendMessage("This player doesn't exist!");
				break;
			}
			clan = player.getPA().getClan();
			if (clan.isRanked(string)) {
				player.sendMessage("You cannot ban a ranked member.");
				break;
			}
			if (clan != null) {
				clan.banMember(Misc.formatPlayerName(string));
				player.getPA().setClanData();
				clan.save();
			}
			break;
			
		default:
			System.out.println("Received string: identifier=" + id + ", string=" + string);
			break;
		}
	}

}