package server.model.players.packets;

import server.Server;
import server.model.players.Player;
import server.model.players.PacketType;
import server.model.players.quests.*;
import server.model.content.STASH;
import server.model.players.Music;
import server.model.players.skills.farming.Allotments;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.world.World;

/**
 * Change Regions
 */
public class ChangeRegions implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {

		//if(c.getHeight() != Server.objectManager.)
		World.getWorld().getGlobalObjects().updateRegionObjects(c);
		World.getWorld().objectManager.refreshObjects(c);
		MotherlodeMine.refreshObjects(c);
		STASH.refreshConfigs(c);
		World.getWorld().itemHandler.reloadItems(c);
		//c.getPA().castleWarsObjects();
		Player.RegionMusicLocations(c);
		//QuestAssistant.sendStages(c);
		c.getAllotment().updateFarmingStates();
		//c.getFlowers().updateFlowerStates();
		//c.getHerbs().updateHerbStates();
		c.saveFile = true;

		if (c.skullTimer > 0) {
			c.isSkulled = true;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
		}

		//c.getPA().sendMapRegion();
	}

}
