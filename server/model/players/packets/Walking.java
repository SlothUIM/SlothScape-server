package server.model.players.packets;

import server.model.players.Player;

import java.awt.Point;

import server.Server;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.model.players.PriceChecker;
import server.model.players.skills.*;
import server.model.players.skills.farming.Allotments.AllotmentFieldsData;
import server.model.players.skills.farming.Farming;
import server.model.players.skills.farming.FarmingPatch;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.world.Boundary;
import server.world.Location;
import server.model.players.*;

/**
 * Walking packet
 **/
public class Walking implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {

		c.nextChat = 0;
		c.dialogueAction = 0;
		if(c.getSkilling().isSkilling()){
			c.getSkilling().stop();
		}
		if (!c.inWild() && c.teleBlockLength > 0) {
			c.getPA().resetTb();
		}
		if (c.isDead || c.getHealth().getAmount() <= 0) {
			c.sendMessage("You are dead you cannot walk." +c.getHealth().getAmount());
			return;
		}
		/*if (c.isNpc) {
			c.sendMessage("You cannot do this now.");
			return;
		}*/
		if(Farming.harvest(c, c.objectX, c.objectX))
				return;
		if (Boundary.isIn(c, Boundary.ICE_PATH)) {
			if (c.isRunning || c.isRunning2) {
				c.isRunning = false;
				c.isRunning2 = false;
				c.getPA().sendConfig(173, 0);
				return;
			}
		}
		if (c.canChangeAppearance) {
			c.canChangeAppearance = false;
		}
		if (!c.getPlayerAction().canWalk()) {
			return;
		}
		if (c.isForceMovementActive() || c.forceMovement) {
	        c.getMovementQueue().stop();
	        return;
	    }
		/*if(FarmingPatch.isAtAnyPatch(c))
			c.getAllotment().updateFarmingStates();
		if(FarmingPatch.isAtChampsGuild(c))
			c.getBushes().updateBushesStates();*/
		c.getPA().resetVariables();
		if (c.teleporting) {
			c.startAnimation(65535);
			c.teleporting = false;
			c.isRunning = false;
			c.gfx0(-1);
			c.startAnimation(-1);
		}
		c.clickNpcType = 0;
		c.resetInteractingObject();
		if (c.isBanking)
			c.isBanking = false;
		if (c.tradeStatus >= 0) {
			c.tradeStatus = 0;
		}
		//SkillHandler.resetSkills(c);
		if(packetType == 248 || packetType == 164) {
			c.clickNpcType = 0;
			c.faceUpdate(0);
			c.npcIndex = 0;
			c.playerIndex = 0;
			if (c.followId > 0 || c.followId2 > 0)
				c.getPA().resetFollow();
		}
		c.getPA().removeAllWindows();
		if (c.duelRule[1] && c.duelStatus == 5) {
			if (PlayerHandler.players[c.duelingWith] != null) {
				if (!c.goodDistance(c.getX(), c.getY(),
						PlayerHandler.players[c.duelingWith].getX(),
						PlayerHandler.players[c.duelingWith].getY(), 1)
						|| c.attackTimer == 0) {
					c.sendMessage("Walking has been disabled in this duel!");
				}
			}
			c.playerIndex = 0;
			return;
		}
		if (c.xInterfaceId == 43933 || c.isChecking) {
			PriceChecker.clearConfig(c);
		}
		if (c.teleTimer > 0) {
			if (PlayerHandler.players[c.playerIndex] != null
					&& c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[c.playerIndex].getX(), PlayerHandler.players[c.playerIndex].getY(), 1) && packetType != 98) {
				c.playerIndex = 0;
			} else {
				if (packetType != 98) {
					c.playerIndex = 0;
				}

			}
			return;
		}

		if (c.freezeTimer > 0) {
			if (PlayerHandler.players[c.playerIndex] != null
					&& c.goodDistance(c.getX(), c.getY(), PlayerHandler.players[c.playerIndex].getX(), PlayerHandler.players[c.playerIndex].getY(), 1) && packetType != 98) {
				c.playerIndex = 0;
			} else {
				c.sendMessage("A magical force stops you from moving.");
				if (packetType != 98) {
					c.playerIndex = 0;
				}

			}
			return;
		}

		if (System.currentTimeMillis() - c.lastSpear < 4000) {
			c.sendMessage("You have been stunned.");
			c.playerIndex = 0;
			return;
		}

		if (packetType == 98) {
			c.mageAllowed = true;
		}

		if (c.respawnTimer > 3) {
			return;
		}
		if (c.inTrade) {
			return;
		}
		if ((c.duelStatus >= 1 && c.duelStatus <= 4) || c.duelStatus == 6) {
			if (c.duelStatus == 6) {
				//c.getTradeAndDuel().claimStakedItems();
			}
			return;
		}
		c.isMoving = true;
		if (!c.wildernessWarning && c.wildLevel > 0) {
			c.resetWalkingQueue();
			c.wildernessWarning = true;
			c.getPA().sendFrame126("WARNING!", 6940);
			c.getPA().showInterface(1908);
		}
		if (packetType == 248) {
			packetSize -= 14;
		}
		boolean teleportable = c.getRights().isOrInherits(Right.ADMINISTRATOR, Right.OWNER, Right.GAME_DEVELOPER);
		c.getMovementQueue().stop();
		c.walkingToItem = false;
		//Player.RegionMusicLocations(c);
		//MotherlodeMine.refreshObjects();
		final int steps = (packetSize - 5) / 2;
		if (steps < 0) {
			return;
		}
		final int firstStepX = c.getInStream().readSignedWordBigEndianA();// - c.getLocation().getRegionX() * 8;
		final int[][] path = new int[steps][2];
		for (int i = 0; i < steps; i++) {
			path[i][0] = c.getInStream().readSignedByte();
			path[i][1] = c.getInStream().readSignedByte();
		}
		final int firstStepY = c.getInStream().readSignedWordBigEndian();// - c.getLocation().getRegionY() * 8;
		final boolean teleport = c.getInStream().readSignedByteC() == 1 && teleportable;
		final Location[] locations = new Location[steps + 1];
		locations[0] = new Location(firstStepX, firstStepY, c.getLocation().getZ());
		for (int i = 0; i < steps; i++) {
			locations[i + 1] = new Location(path[i][0] + firstStepX, path[i][1] + firstStepY, c.getLocation().getZ());
		}
		
		//The ending location
		Location end = locations[locations.length - 1];
		
		if (!teleport) {
			if (c.getLocation().getDistance(end) >= 64) {
				System.out.println("Invalid walk distance: " + c.getLocation().getDistance(end));
				return;
			}	
			if (c.getMovementQueue().addFirstStep(locations[0])) {
				for (int i = 1; i < locations.length; i++) {
					c.getMovementQueue().addStep(locations[i]);
				}
				c.isMoving = false;
			}
		} else {
			if (c.getRights().isOrInherits(Right.ADMINISTRATOR, Right.OWNER, Right.GAME_DEVELOPER)) {
				c.setX(end.getX());
				c.setY(end.getY());
				c.getMovementQueue().handleRegionChange();
			}
		}
	}

}
