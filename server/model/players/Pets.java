package server.model.players;

import server.Server;
import server.model.npcs.*;
import server.model.players.Player;
import server.world.World;

/**
 * 
 * @author Tokashi
 *
 */


public class Pets {

	public static void pickUp(Player c, int Type) {
		if (c.inWild()){
			c.sendMessage("How did you get a pet in the wilderness?");
		}
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] == null)
					continue;	
			}       
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] != null) {
				if (NPCHandler.npcs[i].npcType == Type) {
					if (NPCHandler.npcs[i].spawnedBy == c.playerId && NPCHandler.npcs[i].spawnedBy > 0) {
						NPCHandler.npcs[i].setX(0);
						NPCHandler.npcs[i].setY(0);
						NPCHandler.npcs[i] = null;
					break;					
					}
				}
			}			
		}
	}
	
		static int[][] Pets = { 
			{3505, 7583}, //Hell Kitten		
			{3506, 7584}, //Lazy Hellcat
			{766, 1560}, //Hell Kitten	
			{3507, 7585}, //Wily hellcat	
			{765, 1559}, //Pet Kitten
			{764, 1558}, //Pet Kitten
			{763, 1557}, //Pet Kitten	
			{762, 1556}, //Pet Kitten	
			{761, 1555}, //Pet Kitten
			{768, 1561}, //Pet Kitten	
			{769, 1562}, //Pet Kitten
			{770, 1563}, //Pet Kitten	
			{771, 1564}, //Pet Kitten	
			{772, 1565}, //Pet Kitten	
			{773, 1566}, //Pet Kitten
			{6626, 12643}, //Dag Sup
			{6627, 12644}, //Dag Pri
			{6630, 12645}, //Dag Rex
			{6631, 12649}, //Arma
			{6632, 12650}, //Bandos
			{6633, 12651}, //Sara
			{6634, 12652}, //Zammy
			{6635, 12646}, //mole
			{6636, 12653}, //Prince
			{6637, 12647}, //Kalphite
			{2055, 11995}, //chaos ele
			{6639, 12648}, //smoke devil
			{6640, 12655}, //kraken
			//{4008, 12921}, //snakeling
			//{6638, 13262}, //Abyssal orphan
			{6637, 13178}, //Callisto
			{3099, 13247}, //Hellpuppy
			{8492,22746}
			//{4008, 13225}, //Tzrek-jad
		};
	
	public static void pickUpClean(Player c, int id) {
		for (int i = 0; i < Pets.length; i++)
			if (Pets[i][0] == id)
				c.getItems().addItem(Pets[i][1], 1);
		for (int i = 0; i < NPCHandler.maxNPCs; i++) {
			if (NPCHandler.npcs[i] == null)
				continue;
			if (NPCHandler.npcs[i].npcType == id) {
				NPCHandler.npcs[i].setX(0);
				NPCHandler.npcs[i].setY(0);
			}
		}
		c.hasNpc = false;
	}
	
	public void dropPet(Player c, int itemId) { 
			if (!c.hasNpc && c.summonId < 1) {
				c.turnPlayerTo(c.getX(), c.getY()-1);
				World.getWorld().getNpcHandler().spawnNpc3(c, World.getWorld().getNpcHandler().summonItemId(itemId), c.getX(), c.getY()-1, c.getHeight(), 0, 120, 25, 200, 200, true, false, true);
				c.getPA().followPlayer();
				c.getItems().deleteItem(itemId, 1);
				c.hasNpc = true;
			} else {
				c.sendMessage("You already have a pet following you.");
				return;
	}
	}

	public static boolean isPet(int npcType) {
		for (int i = 0; i < Pets.length; i++) {
			if (Pets[i][0] == npcType)
					return true;
		}
		return false;
	}
}