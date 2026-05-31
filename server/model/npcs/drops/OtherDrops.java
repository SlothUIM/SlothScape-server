package server.model.npcs.drops;
import java.util.stream.IntStream;

//import server.content.gauntlet.TheGauntlet;
import server.world.Location;
import server.model.npcs.NPC;
//import server.model.players.GlobalMessages;
//import server.model.players.GlobalMessages.MessageType;
import server.model.players.Player;
import server.util.Misc;
import server.world.World;

/*
 * @author Patrity
 */

public class OtherDrops {
	
	
	public static void applyOtherDrops(Player player, Location location, NPC npc, int npcLevel) {
		
		 
		int rareDropTable = Misc.random(1, 150);
		int gemRareDropTable = Misc.random(1, 100);
		
		//if (player.getInstance() != null && player.getInstance() instanceof TheGauntlet) {
		//	return;
		//}

		/*
		 * Rare Drop Tables
		 */
		if (rareDropTable == 5) {
			player.getRareDropTable().getDrop();
		}
		if (gemRareDropTable == 5) {
				player.getGemRareDropTable().getDrop();
			
			if (Misc.random(600) == 5) {// Mystery Box
				World.getWorld().getItemHandler().createGroundItem(player, 6199, location.getX(), location.getY(),
						location.getZ(), 1, player.getIndex());
				player.sendMessage("@mag@The monster drops a Mystery Box!");
				//GlobalMessages.send(
				//		player.playerName + " has just received a Mystery Box from " + npc.getName() + "!",
				//		GlobalMessages.MessageType.LOOT);
			}
			if (Misc.random(100) == 5) { // Blood Money
				World.getWorld().getItemHandler().createGroundItem(player, 13307, location.getX(), location.getY(),
						location.getZ(), Misc.random(50, 250), player.getIndex());
				player.sendMessage("@mag@The boss drops some Blood Money.");
			}
		}
		
		

	}

}
