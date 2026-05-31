package server.model.players.packets;

import server.model.players.Player;
import server.world.ApiClient;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;

/**
 * Private messaging, friends etc
 **/
public class PrivateMessaging implements PacketType {

	public final int ADD_FRIEND = 188, SEND_PM = 126, REMOVE_FRIEND = 215,
			CHANGE_PM_STATUS = 95, REMOVE_IGNORE = 59, ADD_IGNORE = 133;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		switch (packetType) {

			case ADD_FRIEND:
				long friendToAdd = c.getInStream().readQWord();
				String friendName = Misc.longToPlayerName2(friendToAdd);

				System.out.println("[PM-DEBUG] " + c.playerName + " clicked Add Friend for: " + friendName);

				try {
					c.getFriends().add(friendToAdd);
					System.out.println("[PM-DEBUG] Successfully passed " + friendName + " to the Friends.add() method.");
				} catch (Exception e) {
					System.err.println("[PM-DEBUG] CRASH inside Friends.add() for " + friendName + "!");
					e.printStackTrace();
				}
				break;

			case SEND_PM:
          /*if (System.currentTimeMillis() < c.muteEnd) {
             c.sendMessage("You are muted for breaking a rule.");
             return;
          }*/

				c.muteEnd = 0;
				final long recipient = c.getInStream().readQWord();
				int pm_message_size = packetSize - 8;
				final byte pm_chat_message[] = new byte[pm_message_size];
				c.getInStream().readBytes(pm_chat_message, pm_message_size, 0);

				Player target = PlayerHandler.getPlayerByLongName(recipient);
				String recipientname = Misc.longToPlayerName2(recipient);

				if (target != null) {
					// Same-world delivery
					c.getFriends().sendPrivateMessage(recipient, pm_chat_message);
				} else {
					// Cross-world delivery
					boolean delivered = ApiClient.sendPMToAPI(c.playerName, recipientname, c.getIndex(), c.playerRights, pm_chat_message);

					if (!delivered) {
						// The Bridge API returned 404 (Player not found/offline)
						c.sendMessage("That player is currently offline.");
						return; // Stop execution so we don't log a failed message
					}
				}

				// --- FIXED LOGGING ---
				// Notice we use 'recipientname' here instead of getting the player object, preventing a cross-world NullPointerException!

				// System.out.println(c.playerName + " PM: " + Misc.decodeMessage(pm_chat_message, pm_chat_message.length));

				// PlayerLogging.write(LogType.PRIVATE_CHAT, c, "Recipient = " + recipientname + ", message = " + Misc.decodeMessage(pm_chat_message, pm_chat_message.length));

				// DiscordBot.sendMessage("pm-logs", "["+c.playerName+" -> "+recipientname+"] : " + Misc.decodeMessage(pm_chat_message, pm_chat_message.length));

				break;

		case REMOVE_FRIEND:
			c.getFriends().remove(c.getInStream().readQWord());
			break;

		case REMOVE_IGNORE:
			c.getIgnores().remove(c.getInStream().readQWord());
			break;

		case CHANGE_PM_STATUS:
			c.getInStream().readUnsignedByte();
			c.setPrivateChat(c.getInStream().readUnsignedByte());
			c.getInStream().readUnsignedByte();
			c.getFriends().notifyFriendsOfUpdate();
			break;

		case ADD_IGNORE:
			c.getIgnores().add(c.getInStream().readQWord());
			break;

		}

	}
}
