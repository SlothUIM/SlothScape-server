package server.model.npcs.drops;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import server.content.SkillcapePerks;
//import server.content.cluescroll.ClueScrollRiddle;
//import server.content.gauntlet.TheGauntlet;
//import server.content.godwars.Godwars;
import server.world.Location;
import server.model.npcs.NPC;
import server.model.npcs.NPCDefinitions;
import server.model.npcs.NPCHandler;
import server.world.Boundary;
import server.model.players.Godwars;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.Right;
import server.model.players.skills.slayer.SlayerMaster;
import server.model.players.skills.slayer.Task;
import server.model.items.Item;
import server.model.items.ItemAssistant;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.CollectionLogRegistry;
import server.util.Misc;
import server.world.World;

public class DropManager {

	
	public static int AMOUNT_OF_TABLES = 0;

	private static final Comparator<Integer> COMPARE_NAMES =(o1, o2) -> {
		String name1 = NPCDefinitions.get(o1).getNpcName();
		String name2 = NPCDefinitions.get(o2).getNpcName();
		return name1.compareToIgnoreCase(name2);
	};

	private Map<Integer, TableGroup> groups = new HashMap<>();


	private List<Integer> ordered = new ArrayList<>();

	@SuppressWarnings("unchecked")
	public void read() {
		JSONParser parser = new JSONParser();
		try {
			fileReader = new FileReader("./Data/json/npc_droptable.json");
			JSONArray data = (JSONArray) parser.parse(fileReader);
			for (Object aData : data) {
				JSONObject drop=(JSONObject) aData;
				List<Integer> npcIds=new ArrayList<>();
				if (drop.get("npc_id") instanceof JSONArray) {
					JSONArray idArray=(JSONArray) drop.get("npc_id");
					idArray.forEach(id -> npcIds.add(((Long) id).intValue()));
				} else {
					npcIds.add(((Long) drop.get("npc_id")).intValue());
				}
				TableGroup group=new TableGroup(npcIds);
				for (TablePolicy policy : TablePolicy.POLICIES) {
					if (!drop.containsKey(policy.name().toLowerCase())) {
						continue;
					}
					JSONObject dropTable=(JSONObject) drop.get(policy.name().toLowerCase());
					//Table table=new Table(policy, ((Long) dropTable.get("accessibility")).intValue());
					
					int rolls = dropTable.containsKey("rolls")
						    ? ((Long) dropTable.get("rolls")).intValue()
						    : 1;

					Table table = new Table(policy, rolls);


						JSONArray tableItems = (JSONArray) dropTable.get("items");
						for (Object tableItem : tableItems) {
							JSONObject item = (JSONObject) tableItem;
							int id = ((Long) item.get("item")).intValue();
							int minimumAmount = ((Long) item.get("minimum")).intValue();
							int maximumAmount = ((Long) item.get("maximum")).intValue();

							int weight = item.containsKey("weight")
								    ? ((Long) item.get("weight")).intValue()
								    : 0; // default weight can be 0 or 1 depending on your logic

							table.add(new Drop(npcIds, id, minimumAmount, maximumAmount, weight));
						}

					group.add(table);
				}
				for (int npcId : npcIds) {
				    groups.put(npcId, group);
				}

			}
			ordered.clear();

			for (TableGroup group : groups.values()) {
				if (group.getNpcIds().size() == 1) {
					ordered.add(group.getNpcIds().get(0));
					continue;
				}
				for (int id : group.getNpcIds()) {
					String name = NPCDefinitions.get(id).getNpcName();
					if (ordered.stream().noneMatch(i -> NPCDefinitions.get(i).getNpcName().equals(name))) {
						ordered.add(id);
					}
				}
			}

			ordered.sort(COMPARE_NAMES);
			Misc.println("Loaded " + ordered.size() + " drop tables.");
			AMOUNT_OF_TABLES = ordered.size();
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Attempts to create a drop for a player after killing a non-playable character
	 * 
	 * @param player the player receiving a possible drop
	 * @param npc the npc dropping the items
	 */
	static boolean test = false;
	
	public void testOpen(Player player) {
		for(int i = 0; i < 100; i++) {
			player.getPA().sendFrame126("", (33008  + i));
		}
		for (int index = 0; index < ordered.size(); index++) {
			player.getPA().sendFrame126(StringUtils.capitalize(NPCDefinitions.get(ordered.get(index)).getNpcName().toLowerCase().replaceAll("_", " ")), 33008 + index);
		}

		player.getPA().showInterface(33000);
	}
	private int getAccessibility(Player player) {
	    if (player.getRights().contains(Right.PLAYER)) return 1;
	    if (player.getRights().contains(Right.OWNER)) return 1;
	    return 1; // default accessibility
	}

	public void create(Player player, NPC npc, Location location, int repeats) {
		TableGroup group = groups.get(npc.npcType);

		
		
		/*
		 * Misc Drops & Points
		 */
		int npcLevel = npc.getDefinition().getNpcCombat();		
		Points.applyPvmPoints(player, npc, location);
		Points.applyBossPoints(player, npc, npcLevel);
		OtherDrops.applyOtherDrops(player, location, npc, npcLevel);

		//System.out.println("creating drop for "+npc.npcType);
		if (group != null) {
			double modifier = getModifier(player);
			List<Item> allDrops = group.access(player, modifier, repeats);
			List<Item> drops = new ArrayList<>();
			for (Item item : allDrops)
			       drops.add(item);

			for (Item item : drops) {
				boolean drop = true;
				if (item.getId() == 536) {
					if (player.getRechargeItems().hasItem(13111) && player.inWild()) {
						item.changeDrop(537, item.getAmount());
					}
				}

				if(item.getId() == 2366 || item.getId() == 1249 || item.getId() == 1247)
					return;
				if (item.getId() == 6529) {
					if (player.getRechargeItems().hasItem(11136)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.20));
					}
					if (player.getRechargeItems().hasItem(11138)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.50));
					}
					if (player.getRechargeItems().hasItem(11140)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.70));
					}
					if (player.getRechargeItems().hasItem(13103)) {
						item.changeDrop(6529, (int) (item.getAmount() * 1.90));
					}
				}
				if (item.getId() == 6729 && player.getRechargeItems().hasItem(13132)) {
					item.changeDrop(6730, item.getAmount());
				}
				if (item.getId() == 13233 && !Boundary.isIn(player, Boundary.CERBERUS_BOSSROOMS)) {
					//player.sendMessage("@red@Something hot drops from the body of your vanquished foe");
				}
				if (IntStream.of(Points.bosses).anyMatch(id -> id == npc.npcType)) {
					//if (player.getInstance() != null && player.getInstance() instanceof TheGauntlet) {
					//	return;
					//}
					item.getDefinition().ifPresent(itemList -> {
						if(itemList.ShopValue > 50000) {
							PlayerHandler.nonNullStream()
							.filter(p -> p.distanceToPoint(player.getX(), player.getY()) < 10 && p.getHeight() == player.getHeight())
							.forEach(p -> {
								if (item.getAmount() > 1) {
									p.sendMessage("@red@[Loot Bot] " + Misc.formatPlayerName(player.playerName) + " received a drop: " + Misc.format(item.getAmount()) + " x " + Item.getItemName(item.getId()) + ".");
								} else {
									p.sendMessage("@red@[Loot Bot] " + Misc.formatPlayerName(player.playerName) + " received a drop: " + Item.getItemName(item.getId()) + ".");
								}
							});
						}
					});
				}
				
				if (item.getId() == 995 && player.getItems().isWearingLuckRing()) {
					player.getItems().addItem(item.getId(), item.getId());
					drop = false;
				}
				int droppedItemId = item.getId();

				boolean itemLogged = false;
				for (CollectionLogData log : player.getCollectionLog().registry.getAllLogs()) {
					int[][] entries = log.getEntries();

					for (int i = 0; i < entries.length; i++) {
						if (entries[i][0] == droppedItemId) {
							log.addItem(droppedItemId, 1);  // update the log
							player.getPA().CollLogPopUp(entries, droppedItemId, 1);
							itemLogged = true;
							break;  // exit inner loop
						}
					}
					if (itemLogged) {
						break; // exit outer loop so it stops searching other bosses!
					}
				}
				if (drop) {
					//System.out.println("Item: "+item.getId());
					World.getWorld().getItemHandler().createGroundItem(player, item.getId(), location.getX(), location.getY(), player.getHeight(), item.getAmount(), player.getIndex());
				}
			}
			
			if (npc.npcType == 8028) {
				player.vorkathKillCount += 1;
				if (player.vorkathKillCount != 0 && (player.vorkathKillCount % 50) == 0) {
					player.getItems().addItemUnderAnyCircumstance(21907, 1);
					player.sendMessage("You receive Vorkaths head!");
					player.vorkathKillCount = 0;
				}
			}
			
			/**
			 * Looting bag and rune pouch
			 */
			if (npc.inWild()) {
				switch (Misc.random(60)) {
				case 2:
					if (player.getItems().getItemCount(11941, true) < 1) {
						World.getWorld().getItemHandler().createGroundItem(player, 11941, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					}
					break;
					
				case 8:
					if (player.getItems().getItemCount(12791, true) < 1) {
						World.getWorld().getItemHandler().createGroundItem(player, 12791, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					}
					break;
				}
			}
			/**
			 * Slayer's staff enchantment and Emblems
			 */
			Optional<Task> task = player.getSlayer().getTask();
			Optional<SlayerMaster> myMaster = SlayerMaster.get(player.getSlayer().getMaster());
			task.ifPresent(t -> {
			String name = npc.getDefinition().getNpcName().toLowerCase().replaceAll("_", " ");
			
				if (name.equals(t.getPrimaryName()) || ArrayUtils.contains(t.getNames(), name)) {
					myMaster.ifPresent(m -> {
						if (npc.inWild() && m.getId() == 7663) {
							int slayerChance = 650;
							int emblemChance = 100;
							if (Misc.random(emblemChance) == 1) {
								World.getWorld().getItemHandler().createGroundItem(player, 12746, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
							}
							if (Misc.random(slayerChance) == 1) {
								World.getWorld().getItemHandler().createGroundItem(player, 21257, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
							}
						}
					});
				}
			});
			
				
			/**
			 * Clue scrolls
			 */
			/**
			 * Clue scrolls (Beginner, Easy, Medium, Hard, Elite)
			 * Handled via OSRS-accurate Drop Manager
			 */
			ClueDropManager.handleClueDrop(player, npc, location);
			
			/**
			 * Master cluescroll pieces
			 */
			int masterPiece = Misc.random(1, 500);
			final int[] MASTER_PIECES = {19837, 19838, 19839};
			
			// Checks if it's a boss AND if the 1/500 roll hit
			if (masterPiece == 5 && !IntStream.of(Points.bosses).noneMatch(bossId -> npc.npcType == bossId)) {
				player.sendMessage("<col=800000>You sense a Master clue scroll piece being dropped to the ground.</col>");
				World.getWorld().getItemHandler().createGroundItem(player, Misc.randomElementOf(MASTER_PIECES), location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
			}
			
			
			/**
			 * Runecrafting pouches
			 
			if (Misc.random(100) == 10) {
				if (npc.getDefinition().getNpcCombat() >= 70 && npc.getDefinition().getNpcCombat() <= 100 && player.getItems().getItemCount(5509, true) == 1 && player.getItems().getItemCount(5510, true) != 1) {
					World.getWorld().getItemHandler().createGroundItem(player, 5510, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					//player.sendMessage("@pur@You sense an upgraded Runecrafting Pouch!");
				} else if (npc.getDefinition().getNpcCombat() > 100 && player.getItems().getItemCount(5510, true) == 1 && player.getItems().getItemCount(5512, true) != 1) {
					World.getWorld().getItemHandler().createGroundItem(player, 5512, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
					//player.sendMessage("@pur@You sense an upgraded Runecrafting Pouch!");
				}
			}*/

			/**
			 * Crystal keys
			 */
			int CKeyChance = Misc.random(30);
			
			if (CKeyChance == 1) {
				player.sendMessage("You sense a crystal key loop being dropped to the ground.");
				World.getWorld().getItemHandler().createGroundItem(player, 987, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
			} else if (CKeyChance == 2) { 
				player.sendMessage("You sense a crystal key tooth being dropped to the ground.");
				World.getWorld().getItemHandler().createGroundItem(player, 985, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
			}
			
			if (player.kbdCount == 1000) {
				player.getItems().addItemUnderAnyCircumstance(33132, 1);
				//GlobalMessages.send("@red@" + player.playerName + " has received the KBD cape for 1000 kills!", GlobalMessages.MessageType.LOOT);
			}
			
			
			
			/**
			 * Ecumenical Keys
			 */
			
			
			/**
			 * Dark Light
			 */
			if (Boundary.isIn(npc, Boundary.CATACOMBS)) {
				if (Misc.random(1000) == 1) {
					World.getWorld().getItemHandler().createGroundItem(player, 6746, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
				}
			}
			
			/**
			 * Dark totem Pieces
			 */
			if (Boundary.isIn(npc, Boundary.CATACOMBS)) {
				switch (Misc.random(40)) {
				case 1:
					if (player.getItems().getItemCount(19679, false) < 1) {
						World.getWorld().getItemHandler().createGroundItem(player, 19679, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
						player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
					}
					break;
					
				case 2:
					if (player.getItems().getItemCount(19681, false) < 1) {
						World.getWorld().getItemHandler().createGroundItem(player, 19681, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
						player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
					}
					break;
				
				
				case 3:
					if (player.getItems().getItemCount(19683, false) < 1) {
						World.getWorld().getItemHandler().createGroundItem(player, 19683, location.getX(), location.getY(), location.getZ(), 1, player.getIndex());
						player.sendMessage("@red@A surge of dark energy fills your body as you notice something on the ground.");
					}
					break;
				}
			}
		}
	}

	private double getModifier(Player player) {
		double modifier = 1.0;
		//+ 2% for every $50 spent passed $1000
		if(player.getRights().contains(Right.EXTREME)){
			modifier -= .05;
		}
		if(player.getRights().contains(Right.CLASSIC) || player.getRights().contains(Right.ELITE)) {
			modifier -= .10;
		}
		if(player.getRights().contains(Right.IRONMAN)) {
			modifier -= .03;
		}
		if(player.getRights().contains(Right.ULTIMATE_IRONMAN)) {
			modifier -= .10;
		}
		if(player.getRights().contains(Right.HC_IRONMAN)) {
			modifier -= .05;
		}
		if (player.getItems().isWearingAnyItem(33033, 33034, 33035, 33036, 33037, 33038, 33039, 33040, 33041, 33042, 33043, 33044, 33045, 33046, 33047,
											   33048, 33049, 33050, 33051, 33052, 33053, 33054, 33055)) {
			modifier -= .05;
		}
		if (player.getItems().isWearingAnyItem(13280, 20760, 13329, 13337, 21898, 13331, 13333, 13335, 21285, 21776, 21784, 21781)) {
			modifier -= .07;
		}
		if (player.getItems().isWearingItem(2572)) {
			modifier -= .03;
		} else if (player.getItems().isWearingItem(12785)) {
			modifier -= .05;
		} else if (player.getItems().isWearingItem(773)) {
			modifier -= 500;
		}

		return modifier;
	}
	public static double getModifier1(Player player) {
		int modifier = 0;		
		if (player.getItems().isWearingItem(2572)) {
			modifier += 3;
		} 
		if (player.getItems().isWearingAnyItem(21776, 21780, 21784, 33033, 33034, 33035, 33036, 33037, 33038, 33039, 33040, 33041, 33042, 33043, 33044, 33045, 33046, 33047,
				   33048, 33049, 33050, 33051, 33052, 33053, 33054, 33055)) {
			modifier += 5;
		}
		if (player.getItems().isWearingAnyItem(13280, 20760, 13329, 13337, 21898, 13331, 13333, 13335, 21285, 21776, 21784, 21781)) {
			modifier += 7;
		}
		if (player.getItems().isWearingAnyItem(33822, 33823, 33824, 33825, 33826, 33827, 33828, 33829, 33830)) {//starter dungeon armor
			modifier += 2;
		}
		if (player.getRights().contains(Right.IRONMAN)) {
			modifier += 3;
		}
		if (player.getRights().contains(Right.ULTIMATE_IRONMAN)) {
			modifier += 10;
		}
		if (player.getRights().contains(Right.HC_IRONMAN)) {
			modifier += 5;
		}
		if (player.getRights().contains(Right.GROUP_IRONMAN)) {
			modifier += 5;
		}
		
		
		return modifier;
	}




	static int amountt = 0;

	private FileReader fileReader;

	/**
	 * Testing droptables of chosen npcId
	 * @param player		The player who is testing the droptable
	 * @param npcId			The npc who of which the player is testing the droptable from
	 * @param amount		The amount of times the player want to grab a drop from the npc droptable
	 */
	public void test(Player player, int npcId, int amount) {
		Optional<TableGroup> group = groups.values().stream().filter(g -> g.getNpcIds().contains(npcId)).findFirst();

		amountt = amount;

		while (amount-- > 0) {
			group.ifPresent(g -> {
				List<Item> drops = g.access(player, 1.0, 1);

				for (Item item : drops) {
					player.getItems().addItemToBank(item.getId(), item.getAmount());
				}
			});
		}
		player.sendMessage("Completed " + amountt + " drops from " + World.getWorld().getNpcHandler().getNpcName(npcId) + ".");
	}


}
