package server.model.players.packets;

import server.model.players.Player;
import server.model.players.PacketType;
import server.model.players.Sound;
import server.model.players.skills.LogData;
import server.model.players.skills.SkillHandler;
import server.world.World;

/**
 * Pickup Item
 **/
public class PickupItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.pItemY = c.getInStream().readSignedWordBigEndian();
		c.pItemId = c.getInStream().readUnsignedWord();
		c.pItemX = c.getInStream().readSignedWordBigEndian();

		if (Math.abs(c.getX() - c.pItemX) > 25 || Math.abs(c.getY() - c.pItemY) > 25) {
			c.resetWalkingQueue();
			return;
		}

		// --- FIX 1: Clear conflicting actions so pickup takes priority ---
		c.clickObjectType = 0;
		c.clickNpcType = 0;
		c.npcIndex = 0;
		c.playerIndex = 0;

		for (LogData logData : LogData.values()) {
			if (c.isFiremaking && c.pItemId == logData.getLogId()) {
				c.sendMessage("You can't do that!");
				c.stopFiremaking = true;
				return;
			}
		}

		for (LogData logData : LogData.values()) {
			if (c.pItemId == logData.getLogId()) {
				c.pickedUpFiremakingLog = true;
				break; // Stop looping once we found a match!
			}
		}

		SkillHandler.resetSkills(c);
		c.getCombat().resetPlayerAttack();

		// --- FIX 2: Allow picking up from 1 tile away (helps with clipped items) ---
		if (c.getX() == c.pItemX && c.getY() == c.pItemY || c.goodDistance(c.getX(), c.getY(), c.pItemX, c.pItemY, 1)) {
			c.getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 0, 8); // Door sound for items is funny, but kept it intact!
			World.getWorld().itemHandler.removeGroundItem(c, c.pItemId, c.pItemX, c.pItemY, true);
		} else {
			c.walkingToItem = true;
		}
	}
}