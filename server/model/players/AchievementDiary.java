package server.model.players;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Client;
import server.model.players.skills.agility.AgilityHandler;
import server.util.Misc;
import server.world.Boundary;
import server.world.World;
import server.Config;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;


public class AchievementDiary {

	Player c;

	// This holds the actual progress for THIS player only
	public Map<String, List<AchievementTask>> playerAchievementMap = new HashMap<>();

	public AchievementDiary(Player player) {
		this.c = player;
		// Initialize this player's map using the static templates
		for (Map.Entry<String, List<AchievementTask>> entry : achievementMap.entrySet()) {
			List<AchievementTask> playerTasks = new ArrayList<>();
			for (AchievementTask templateTask : entry.getValue()) {
				// Create a NEW task object for this player based on the template
				playerTasks.add(new AchievementTask(templateTask.getTask(), false));
			}
			playerAchievementMap.put(entry.getKey(), playerTasks);
		}
	}

	// Assuming you have the achievementMap and other necessary code already set up

	// This method updates the interface with achievement texts for all difficulty levels of a region
	public void updateAchievementInterface(String region) {
		int frame = 8145; // Starting frame for achievement texts

		c.getPA().sendFrame126(region + " Achievement Diaries", 8144);

		for (String difficulty : Arrays.asList("Easy", "Medium", "Hard", "Elite")) {
			String mapKey = region + difficulty;
			List<AchievementTask> achievementTasks = playerAchievementMap.get(mapKey);

			if (achievementTasks != null) {
				c.getPA().sendFrame126("@red@" + difficulty + " Diaries", frame);
				frame++;

				for (AchievementTask task : achievementTasks) {
					if (frame <= 8195) {
						// THE FIX: Wrap the text in the new <str> tags if done
						if (task.isDone()) {
							c.getPA().sendFrame126("<str=4>" + task.getTask() + "</str>", frame);
						} else {
							c.getPA().sendFrame126(task.getTask(), frame);
						}
						frame++;
					} else {
						break; 
					}
				}
			}

			if (frame <= 8195) {
				c.getPA().sendFrame126("", frame);
				frame++;
			} else {
				break; 
			}
		}

		for (int i = frame; i <= 8195 ; i++) {
			c.getPA().sendFrame126("", i);
		}

		c.getPA().showInterface(8134);
	}


	private static List<AchievementTask> generateTasks(boolean done, String... taskDescriptions) {
		List<AchievementTask> tasks = new ArrayList<>();
		for (String description : taskDescriptions) {
			tasks.add(new AchievementTask(description, done));
		}
		return tasks;
	}

	private static void markTaskAsDone(List<AchievementTask> tasks, int index) {
		if (index >= 1 && index <= tasks.size()) {
			tasks.get(index - 1).setDone(true);
		}
	}
	public static final Map<String, List<AchievementTask>> achievementMap = new HashMap<>();

	static {
		achievementMap.put("ArdougneEasy", generateTasks(
				false,
				"Have Wizard Cromperty teleport you to the Rune essence mine",
				"Steal a cake from the East Ardougne market stalls",
				"Sell silk to the Silk trader in East Ardougne for 60gp",
				"Use the altar in East Ardougne's church",
				"Go out fishing on the Fishing Trawler",
				"Enter the Combat Training Camp north of West Ardougne",
				"Have Tindel Marchant identify a rusty sword for you",
				"Use the Ardougne lever to teleport to the Wilderness",
				"View Aleck's Hunter Emporium in Yanille",
				"Check what pets you have insured with Probita in Ardougne"
				));        
		achievementMap.put("ArdougneMedium", generateTasks(
				false,
				"Enter the unicorn pen in Ardougne Zoo using Fairy rings",
				"Grapple over Yanille's south wall and jump off",
				"Harvest some strawberries from the Ardougne farming patch",
				"Cast the Ardougne Teleport spell",
				"Travel to Castle Wars by Hot Air Balloon",
				"Claim buckets of sand from Bert in Yanille",
				"Catch any fish on the Fishing Platform",
				"Pickpocket the master farmer north of East Ardougne",
				"Collect some cave nightshade from the Skavid caves",
				"Kill a swordchick in the Tower of Life",
				"Equip an Iban's upgraded staff or upgrade an Iban's staff",
				"Visit the island easy of the Necromancer Tower"
				));       
		achievementMap.put("ArdougneHard", generateTasks(
				false,
				"Recharge jewellery in the Legends' Guild",
				"Enter the Magic Guild",
				"Attempt to steal from a chest in Ardougne Castle",
				"Have a zookeeper put you in Ardougne Zoo's monkey cage",
				"Teleport to the Watchtower",
				"Catch a Red Salamander",
				"Check the health of a palm tree near Tree Gnome Village",
				"Pick some poison ivy berries south of East Ardougne",
				"Smith a Mithril platebody near Ardougne",
				"Enter your POH from Yanille",
				"Smith a Dragon sq shield in West Ardougne",
				"Craft some death runes from essence"
				));  
		achievementMap.put("ArdougneElite", generateTasks(
				false,
				"Brew a prayer potion in the Ranging Guild",
				"Loot Barrows at least 3 times from the Barrows Chest",
				"Defeat a Cerberus in the Taverley Dungeon",
				"Use the ardougne cloak to teleport to the Ardougne Monastery",
				"Pickpocket a gnome in the Tree Gnome Stronghold",
				"Chop and burn some magic logs on the Lava Maze island",
				"Create 100 soul runes at once",
				"Equip the Ardougne cloak 4"
				));

		achievementMap.put("DesertEasy", generateTasks(
				false,
				"Catch a golden warbler",
				"Mine five clay in the north-eastern desert",
				"Enter the Kalphite Lair",
				"Enter the Desert with a set of desert robes on",
				"Kill a vulture",
				"Have Zahur clean a herb for you",
				"Collect 5 potato cacti from the Kalphite Hive",
				"Sell an artefact to Simon Templeton",
				"Open the sarcophagus in the first room in Pyramid Plunder",
				"Cut a desert cactus open to fill a waterskin",
				"Travel from the Shantay Pass to Pollnivneach by carpet"
				));
		achievementMap.put("DesertMedium", generateTasks(
				false,
				"Pick a cactus spine from a cactus",
				"Mine 50 teak logs in the Uzer mine",
				"Fill a waterskin at the Desert well",
				"Steal a silk stall in Ardougne with Gloves of silence equipped",
				"Chop and burn some oak logs in Sophanem",
				"Craft a fire battlestaff in Al Kharid",
				"Complete a lap of the Agility Pyramid",
				"Craft some molten glass at the furnace in Al Kharid",
				"Wear a set of robes from the Shantay Pass shop",
				"Complete a lap of the Gnome Stronghold Agility Course",
				"Catch an orange salamander"
				));

		achievementMap.put("DesertHard", generateTasks(
				false,
				"Catch a granite lobster",
				"Craft 1000 blood runes simultaneously",
				"Obtain a pair of striped pirate leggings",
				"Open the sarcophagus on floor 4 of Pyramid Plunder",
				"Pray at the Elidinis Statuette with Smite and Protect from Melee active",
				"Loot the grand gold chest in the final room of Pyramid Plunder",
				"Defeat a Dust Devil in the desert",
				"Mine some granite in the Desert Quarry while wearing the Desert amulet 4",
				"Complete a lap of the Nardah Rooftop Course",
				"Defeat a Kalphite Queen"
				));
		achievementMap.put("DesertElite", generateTasks(
				false,
				"Harvest a potato cactus",
				"Bake a wild pie within the Kharidian Desert",
				"Create an adamant scimitar from scratch on a portable anvil",
				"Complete a lap of the Shifting Tombs",
				"Perform the granite body and head drop trick in the Desert\\n Slayer Dungeon",
				"",
				"Cook 5 rocktails in a row",
				"Bury 5 sets of bones at once at the Nardah Cactus patch",
				"Unlock the large floor room in the player-owned house in Pollnivneach",
				"Craft a shadow nihil pouch",
				"Defeat the Queen Black Dragon in the desert"
				));
		achievementMap.put("FremennikEasy", generateTasks(
				false,
				"Catch some raw fish at Rellekka",
				"Mine some iron ore in Rellekka",
				"Visit the Miscellania Castle",
				"Climb to the top of the Lighthouse",
				"Defeat the Dagannoth fledgling in the Lighthouse",
				"Catch a tuna in Rellekka",
				"Chop and burn some oak logs in the Fremennik Province",
				"Cook some food on the fire north-east of Rellekka",
				"Craft some water runes at the Water Altar",
				"Visit Peer the Seer"
				));
		achievementMap.put("FremennikMedium", generateTasks(
				false,
				"Catch some green salamanders in the Rellekka Hunter area",
				"Defeat the Dagannoth mother in the Lighthouse",
				"Chop and burn some willow logs in the Fremennik Province",
				"Kill a yak on Neitiznot",
				"Craft a blood rune in the Abyss",
				"Cook some sharks in Rellekka",
				"Fish for some monkfish in Rellekka",
				"Smith a mithril kiteshield in Neitiznot",
				"Craft some death runes",
				"Solve a medium Fremennik Diary puzzle box"
				));
		achievementMap.put("FremennikHard", generateTasks(
				false,
				"Fish for some karambwan in Rellekka",
				"Defeat the Dagannoth Supreme in the Lighthouse",
				"Chop and burn some maple logs in the Fremennik Province",
				"Complete the Fight Arena quest",
				"Craft 56 astral runes at once",
				"Complete a lap of the Rellekka Rooftop Course",
				"Kill a spiritual warrior in the God Wars Dungeon",
				"Craft 10 cosmic runes at once",
				"Mine some gold ore in the Lava Maze",
				"Bury 5 big bones on Neitiznot"
				));
		achievementMap.put("FremennikElite", generateTasks(
				false,
				"Craft some wrath runes",
				"Defeat the Dagannoth Rex in the Lighthouse",
				"Chop and burn some yew logs in the Fremennik Province",
				"Catch a black chinchompa in the Fremennik Province",
				"Use the fairy ring to teleport to the Waterbirth Island",
				"Complete a lap of the Trollheim Rooftop Course",
				"Defeat a spiritual mage in the God Wars Dungeon",
				"Complete the Fight Kiln minigame",
				"Craft 500 nature runes at once",
				"Chop some redwood logs in the Woodcutting Guild"
				));

		achievementMap.put("VarrockEasy", generateTasks(
				false,
				"Browse Thessalia's store",
				"Have Aubury teleport you to the essence mine",
				"Mine some iron in the south-east Varrock mine",
				"Make a normal plank at the Sawmill",
				"Enter the second level of the Stronghold of Security",
				"Jump over the fence south of Varrock",
				"Chop down a dying tree in the Lumber Yard",
				"Buy a newspaper",
				"Give a dog a bone!",
				"Spin a bowl on the pottery wheel and fire it in the oven\\n in Barbarian Village",
				"",
				"Speak to Haig Halen after obtaining at least 50 Kudos",
				"Craft some earth runes from Essence",
				"Catch some trout in the River Lum at Barbarian Village",
				"Steal from the tea stall in Varrock"
				));
		achievementMap.put("VarrockMedium", generateTasks(
				false,
				"Have the Apothecary in Varrock make you a Strength potion",
				"Enter the Champions' Guild",
				"Select a colour for your kitten",
				"Use the Spirit tree in the north-eastern corner of Grand Exchange",
				"Perform the 4 emotes from the Stronghold of Security",
				"Enter the Tolna dungeon after completing A Soul's Bane",
				"Teleport to the digsite using a Digsite pendant",
				"Cast the teleport to Varrock spell",
				"Get a Slayer task from Vannaka",
				"Make 20 mahogany planks in one go",
				"Pick a white tree fruit",
				"Use the balloon to travel from Varrock",
				"Complete a lap of the Varrock Rooftop Course"
				));
		achievementMap.put("VarrockHard", generateTasks(
				false,
				"Trade furs with the Fancy Dress Seller for a Spottier cape and equip it",
				"Speak to Orlando Smith when you have achieved 153 Kudos",
				"Make a Waka canoe near Edgeville",
				"Teleport to Paddewwa",
				"Teleport to Barbarian Village with a Skull sceptre",
				"Chop some yew logs in Varrock and burn them at the\\n top of the Varrock church",
				"",
				"Have the Varrock estate agent decorate your house with Fancy Stone",
				"Collect at least 2 yew roots from the tree patch in Varrock Palace",
				"Pray at the altar in Varrock Palace (2nd floor[US]) with Smite active",
				"Squeeze through the obstacle pipe in Edgeville Dungeon"
				));
		achievementMap.put("VarrockElite", generateTasks(
				false,
				"Create a Super combat potion in Varrock West Bank",
				"Use Lunar magic to make 20 mahogany planks in the \\nVarrock Lumber Yard",
				"",
				"Bake a summer pie in the Cooking Guild",
				"Smith and fletch ten rune darts within Varrock",
				"Craft 100 or more earth runes simultaneously from essence"
				));
		achievementMap.put("Lumbridge&DraynorEasy", generateTasks(
				false,
				"Slay a cave bug in the Lumbridge Swamp Caves",
				"Have Archmage Sedridor teleport you to the Rune essence mine",
				"Craft some water runes",
				"Learn your age from Hans in Lumbridge",
				"Pickpocket a man or woman in Lumbridge",
				"Chop and burn some oak logs in Lumbridge",
				"Kill a zombie in the Draynor Sewers",
				"Catch some anchovies in Al-Kharid",
				"Bake some bread on the Lumbridge castle kitchen range",
				"Mine some iron ore at the Al-Kharid mine",
				"Enter the H.A.M. Hideout"
				));
		achievementMap.put("Lumbridge&DraynorMedium", generateTasks(
				false,
				"Grapple across the River Lum",
				"Purchase an upgraded device from Ava",
				"Travel to the Wizards' Tower by Fairy ring",
				"Cast the Lumbridge Teleport spell",
				"Catch some salmon in Lumbridge",
				"Craft a coif (not cowl) in the Lumbridge cow pen",
				"Chop some willow logs in Draynor Village",
				"Pickpocket Martin the Master Gardener",
				"Get a Slayer task from Chaeldar",
				"Catch an essence or eclectic impling in Puro-Puro",
				"Craft some lava runes at the Fire Altar in Al Kharid"
				));
		achievementMap.put("Lumbridge&DraynorHard", generateTasks(
				false,
				"Cast Bones to Peaches in Al Kharid Palace",
				"Squeeze past the jutting wall on your way to the Cosmic Altar",
				"Craft 56 cosmic runes simultaneously from essence",
				"Travel from Lumbridge to Edgeville on a waka canoe",
				"Collect at least 100 Tears of Guthix  in one visit",
				"Take the train from Dorgesh-Kaan to Keldagrim",
				"Purchase some Barrows gloves from the Culinaromancer's Chest",
				"Pick some belladonna from the farming patch at Draynor Manor",
				"Light your mining helmet in the Lumbridge Castle basement",
				"Recharge your prayer at Emir's Arena with Smite activated",
				"Craft, string and enchant an amulet of power in Lumbridge"
				));
		achievementMap.put("Lumbridge&DraynorElite", generateTasks(
				false,
				"Steal from a Dorgesh-Kaan rich chest",
				"Pickpocket Movario on the Dorgesh-Kaan Agility Course",
				"Chop some magic logs at the Mage Training Arena",
				"Smith an adamant platebody down in Draynor Sewer",
				"Craft 140 or more water runes at once from essence",
				"Perform the Quest point cape emote in the Wise Old Man's house"
				));
		achievementMap.put("FaladorEasy", generateTasks(
				false,
				"Find out what your family crest is from Sir Renitee",
				"Climb over the western Falador wall",
				"Browse Sarah's Farming Shop",
				"Get a haircut from the Falador hairdresser",
				"Fill a bucket from the pump north of Falador west bank",
				"Kill a duck in Falador Park",
				"Make a mind tiara",
				"Take the boat to Entrana",
				"Repair a broken strut in the Motherlode Mine",
				"Claim a security book from Security upstairs at Port Sarim jail",
				"Smith some Blurite limbs on Doric's anvil"
				));
		achievementMap.put("FaladorMedium", generateTasks(
				false,
				"Light a bullseye lantern at the Chemist's in Rimmington",
				"Telegrab some Wine of zamorak at the Chaos Temple",
				"Unlock the crystal chest in Taverley",
				"Place a Scarecrow in the Falador farm flower patch",
				"Kill a Mogre at Mudskipper Point",
				"Visit the Port Sarim Rat Pits",
				"Grapple up and then jump off the north Falador wall",
				"Pickpocket a Falador guard",
				"Pray at the Altar of Guthix in Taverley wearing full Initiate",
				"Mine some gold ore at the Crafting Guild",
				"Squeeze through the crevice in the Dwarven Mines",
				"Chop and burn some willow logs in Taverley",
				"Craft a basket on the Falador farm loom",
				"Teleport to Falador"
				));
		achievementMap.put("FaladorHard", generateTasks(
				false,
				"Craft 140 mind runes simultaneously from essence",
				"Change your family crest to the Saradomin symbol",
				"Kill the Giant Mole beneath Falador Park",
				"Kill a Skeletal Wyvern in the Asgarnia Ice Dungeon",
				"Kill the blue dragon under the Heroes' Guild",
				"Crack a wall safe within Rogues' Den",
				"Recharge your Prayer in the Port Sarim church \\nwearing full Proselyte",
				"",
				"Enter the Warriors' Guild",
				"Equip a Dwarven helmet within the Dwarven Mines"
				));
		achievementMap.put("FaladorElite", generateTasks(
				false,
				"Craft 252 air runes simultaneously from essence",
				"Purchase a white 2h sword from Sir Vyvin.",
				"Find at least 3 magic roots at once when digging up your \\nmagic tree in Falador",
				"",
				"Perform a Skillcape or Quest cape emote at the top of Falador Castle",
				"Jump over the strange floor in Taverley Dungeon",
				"Mix a Saradomin brew in Falador east bank"
				));

		//*****Karamja Easy, medium, hard, elite*****//
		achievementMap.put("KaramjaEasy", generateTasks(
				false,
				"Pick 5 bananas from the plantation located east of the volcano",
				"Use the rope swing to travel to the Moss Giant Island \\nnorth-west of Karamja",
				"",
				"Mine some gold from the rocks on the north-west peninsula of Karamja",
				"Travel to Port Sarim via the dock, east of Musa Point",
				"Travel to Ardougne via the port near Brimhaven",
				"Explore Cairn Isle to the west of Karamja",
				"Use the fishing spots north of the banana plantation",
				"Collect 5 seaweed from anywhere on Karamja",
				"Attempt the TzHaar Fight Pits or Fight Cave",
				"Kill a jogre in the Pothole Dungeon"
				));        
		achievementMap.put("KaramjaMedium", generateTasks(
				false,
				"Claim a ticket from the Agility arena in Brimhaven",
				"Discover hidden wall in the dungeon below the volcano",
				"Visit the Isle of Crandor via the dungeon below the volcano",
				"Use Vigroy and Hajedy's cart service",
				"Earn 100% favour in the Tai Bwo Wannai Cleanup minigame",
				"Cook a Spider on stick",
				"Charter the Lady of the Waves from south of Cairn Isle to Port Khazard",
				"Cut a log from a teak tree",
				"Cut a log from a mahogany tree",
				"Catch a karambwan",
				"Exchange gems for a machete",
				"Use the gnome glider to travel to Karamja",
				"Grow a healthy fruit tree in the patch near Brimhaven",
				"Trap a Horned Graahk",
				"Chop the vines to gain deeper access to Brimhaven Dungeon",
				"Cross the lava using the stepping stones within Brimhaven Dungeon",
				"Climb the stairs within Brimhaven Dungeon",
				"Charter a ship from the shipyard in the far east of Karamja",
				"Mine a red topaz from a gem rock"
				));        
		achievementMap.put("KaramjaHard", generateTasks(
				false,
				"Become the champion of the Fight Pit",
				"Kill a Ket-Zek in the Fight Caves",
				"Eat an oomlie wrap",
				"Craft some nature runes from essence",
				"Cook a raw karambwan thoroughly",
				"Kill a deathwing in the dungeon under the Kharazi Jungle",
				"Use the crossbow shortcut south of the volcano",
				"Collect 5 palm leaves",
				"Be assigned a Slayer task by Duradel in Shilo Village",
				"Kill a metal dragon in Brimhaven Dungeon"
				));       
		achievementMap.put("KaramjaElite", generateTasks(
				false,
				"Craft 56 nature runes at once from essence",
				"Equip a fire cape in the Tzhaar city",
				"Check the health of a palm tree in Brimhaven",
				"Create an antivenom potion whilst standing in the horse shoe mine",
				"Check the health of your Calquat tree patch"
				));       	
		//kandarin easy, medium, hard, elite
		achievementMap.put("KandarinEasy", generateTasks(
				false,
				"Catch a mackerel at Catherby",
				"Buy a candle from the candle maker in Catherby",
				"Collect 5 flax from the Seers' Village flax field",
				"Play the Church organ in the Seers' Village church",
				"Plant jute seeds in the farming patch north of McGrubor's Wood",
				"Have Galahad make you a cup of tea",
				"Defeat one of each elemental in the Elemental Workshop",
				"Get a pet fish from Harry in Catherby",
				"Buy a stew from the Seers' Village pub",
				"Speak to Sherlock between the Sorcerer's Tower\\n and Keep Le Faye",
				"",
				"Cross the Coal truck log shortcut"
				));       	
		achievementMap.put("KandarinMedium", generateTasks(
				false,
				"Complete a lap of the Barbarian Agility course",
				"Create a Superantipoison potion from scratch in\\n the Seers/Catherby area",
				"",
				"Enter the Ranging Guild",
				"Use the grapple shortcut to get from the water \\nobelisk to Catherby shore",
				"",
				"Catch and cook a bass in Catherby",
				"Teleport to Camelot",
				"String a maple shortbow in Seers' Village bank",
				"Pick some limpwurt root from the farming patch in Catherby",
				"Create a mind helmet",
				"Kill a fire giant in the Waterfall Dungeon",
				"Complete a wave of Barbarian Assault",
				"Steal from the chest in Hemenster",
				"Travel to McGrubor's Wood by Fairy ring",
				"Mine some coal near the coal trucks"
				));     

		achievementMap.put("KandarinHard", generateTasks(
				false,
				"Catch a leaping sturgeon",
				"Complete a lap of the Seers' Village Rooftop Course",
				"Create a yew longbow from scratch around Seers' Village",
				"Enter the Seers' Village courthouse with Piety turned on",
				"Charge a water orb",
				"Burn some maple logs with a bow in Seers' Village",
				"Kill a shadow hound in the Shadow Dungeon",
				"Kill a Mithril dragon",
				"Purchase and equip a granite body from Barbarian Assault",
				"Have the Seers' Village estate agent decorate your house with Fancy Stone",
				"Smith an adamant spear at Otto's Grotto"
				));     

		achievementMap.put("KandarinElite", generateTasks(
				false,
				"Read the blackboard at Barbarian Assault after reaching \\nlevel 5 in every role",
				"",
				"Pick some dwarf weed from the herb patch at Catherby",
				"Fish and cook 5 sharks in Catherby",
				"Mix a Stamina mix on top of the Seers' Village bank",
				"Smith a rune hasta at Otto's Grotto",
				"Construct a pyre ship from magic logs (chewed bones must be used)",
				"Teleport to Catherby"
				));       
		//Morytania easy ,medium, hard, elite

		achievementMap.put("MorytaniaEasy", generateTasks(
				false,
				"Craft any snelm from scratch in Morytania",
				"Cook a thin snail on the Port Phasmatys range",
				"Get a Slayer task from Mazchna",
				"Kill a banshee in the Slayer Tower",
				"Have Sbott tan something for you",
				"Enter Mort Myre Swamp",
				"Kill a ghoul",
				"Place a Scarecrow in the Morytania flower patch",
				"Offer some Bonemeal at the Ectofuntus",
				"Kill a Werewolf in its human form using the Wolfbane dagger",
				"Restore your prayer points at the nature altar"
				));  
		achievementMap.put("MorytaniaMedium", generateTasks(
				false,
				"Catch a swamp lizard",
				"Complete a lap of the Canifis agility course",
				"Obtain some Bark from a Hollow tree",
				"Travel to Dragontooth Isle",
				"Kill a Terror Dog",
				"Complete a game of trouble brewing",
				"Board the Swampy boat at the Hollows",
				"Make a batch of cannonballs at the Port Phasmatys furnace",
				"Kill a Fever Spider on Braindeath Island",
				"Use an ectophial to return to Port Phasmatys",
				"Mix a Guthix Balance potion while in Morytania"
				));  
		achievementMap.put("MorytaniaHard", generateTasks(
				false,
				"Enter the Kharyrll portal in your POH",
				"Climb the advanced spike chain within Slayer Tower",
				"Harvest some Watermelon from the allotment patch on Harmony Island",
				"Chop and burn some mahogany logs on Mos Le'Harmless",
				"Complete a temple trek with a hard companion",
				"Kill a Cave Horror",
				"Harvest some Bittercap Mushrooms from the patch in Canifis",
				"Pray at the Altar of Nature with Piety activated",
				"Use the shortcut to get to the bridge over the Salve",
				"Mine some mithril ore in the Abandoned Mine"
				));  
		achievementMap.put("MorytaniaElite", generateTasks(
				false,
				"Catch a shark in Burgh de Rott with your bare hands",
				"Cremate any Shade remains on a Magic or Redwood pyre",
				"Fertilize the Morytania herb patch using Lunar Magic",
				"Craft a Black dragonhide body in the Canifis bank",
				"Kill an Abyssal demon in the Slayer Tower",
				"Loot the Barrows chest while wearing any complete barrows set"
				));    
		achievementMap.put("WildernessEasy", generateTasks(
				false,
				"Cast Low Level Alchemy on an item in the Wilderness",
				"Enter the Wilderness God Wars Dungeon",
				"Mine some Iron ore in the Wilderness",
				"Kill a Mammouth in the Wilderness",
				"Kill a Rogue in the Rogues' Castle",
				"Enter the Abyss",
				"Sell an item to the Bandit Duty Free shop",
				"Collect 5 spider eggs from the Wilderness",
				"Use the Lever to teleport to the Wilderness",
				"Visit the Ferox Enclave"
				));

		achievementMap.put("WildernessMedium", generateTasks(
				false,
				"Mine some Gold ore in the Wilderness",
				"Slay a Greater Demon in the Wilderness",
				"Catch a Green Salamander in the Wilderness",
				"Kill a Lava Dragon and bury its bones on the island",
				"Kill a Chaos Dwarf in the Wilderness",
				"Charge an Earth Orb at the Earth Altar",
				"Kill a Bloodveld in the Wilderness God Wars Dungeon",
				"Smith a Mithril Scimitar in the Resource Area",
				"Open a Muddy Chest in the Lava Maze",
				"Teleport to a destination using the Wilderness Obelisks"
				));

		achievementMap.put("WildernessHard", generateTasks(
				false,
				"Cast the Magic Dart spell on a Spiritual Mage in the GWD",
				"Kill a Scorpia in the Wilderness",
				"Kill a Crazy Archaeologist in the Wilderness",
				"Kill a Chaos Fanatic in the Wilderness",
				"Smith an Adamant Platebody in the Resource Area",
				"Catch a Black Salamander in the Wilderness",
				"Chop and burn some Mahogany logs in the Resource Area",
				"Kill a Green Dragon while wearing a Wilderness Cape",
				"Enter the Deep Wilderness Dungeon",
				"Collect some Red Dragonhide in the Wilderness"
				));

		achievementMap.put("WildernessElite", generateTasks(
				false,
				"Kill Callisto, Vet'ion, and Venenatis",
				"Teleport to the Ghorrock Teleport destination",
				"Smith a Rune Platebody in the Resource Area",
				"Catch a Black Chinchompa in the Wilderness",
				"Chop some Magic Logs in the Resource Area",
				"Cast the Ice Barrage spell on a player in the Wilderness",
				"Kill a Chaos Elemenetal",
				"Craft 100 or more Blood Runes at once in the Wilderness",
				"Equip a Wilderness Cape 3"
				));
		// Add more regions and their achievements as needed
	}
	public int[][] diaries = new int[12][4];

	public void completeAchievement(String region, String title, int taskId) {
		// CHANGE: Get from playerAchievementMap
		List<AchievementTask> tasks = playerAchievementMap.get(region); 

		if (tasks != null && taskId >= 1 && taskId <= tasks.size()) {
			AchievementTask task = tasks.get(taskId - 1);

			if (!task.isDone()) {
				task.setDone(true); // Mark it done on the player's object
				c.sendMessage("You have completed the achievement: @whi@" + title);

				// Recalculate everything to ensure UI is perfect
				loadAchievementProgress(); 
			}
		}
	}
	public void openAllPouches() {
	    int pouchId = 22521;
	    int amount = c.getItems().getItemAmount(pouchId);
	    
	    if (amount <= 0) {
	        c.sendMessage("You don't have any coin pouches to open.");
	        return;
	    }

	    // Average OSRS gold per pouch (scales slightly or flat based on NPC)
	    // We'll do a flat 50-100 coins for this example
	    int totalCoins = 0;
	    for (int i = 0; i < amount; i++) {
	        totalCoins += Misc.random(50, 100);
	    }

	    c.getItems().deleteItem(pouchId, amount);
	    c.getItems().addItem(995, totalCoins);
	    c.sendMessage("You open " + amount + " pouches and find " + totalCoins + " coins.");
	}
	public void claimReward(String region, String difficulty) {
		if (!isTierFinished(region, difficulty)) {
			c.sendMessage("You haven't finished the " + region + " " + difficulty + " diary yet!");
			return;
		}

		int rewardItem = -1;
		// Example: Wilderness
		if (region.equals("Wilderness")) {
			if (difficulty.equals("Easy")) rewardItem = 13104; // Wilderness Sword 1
			else if (difficulty.equals("Medium")) rewardItem = 13105;
			else if (difficulty.equals("Hard")) rewardItem = 13106;
			else if (difficulty.equals("Elite")) rewardItem = 13107;
		}
		// Example: Ardougne
		if (region.equals("Ardougne")) {
			if (difficulty.equals("Easy")) rewardItem = 13121; // Ardougne Cloak 1
			// ... add others
		}

		if (rewardItem != -1) {
			if (c.getItems().playerHasItem(rewardItem) || c.getItems().bankContains(rewardItem)) {
				c.sendMessage("You already have this reward!");
			} else if (c.getItems().freeSlots() > 0) {
				c.getItems().addItem(rewardItem, 1);
				c.sendMessage("Congratulations! You've received the " + region + " " + difficulty + " reward.");
			} else {
				c.sendMessage("You need an empty inventory slot to claim your reward.");
			}
		}
	}
	public int getAreaIndex(String regionKey) {
		// Remove the difficulty suffix to get the base region name
		String base = regionKey.replaceAll("(Easy|Medium|Hard|Elite)", "");
		switch (base) {
		case "Ardougne": return 0;
		case "Desert": return 1;
		case "Falador": return 2;
		case "Fremennik": return 3;
		case "Kandarin": return 4;
		case "Karamja": return 5;
		case "Kourend": return 6;
		case "Lumbridge&Draynor": return 7;
		case "Morytania": return 8;
		case "Varrock": return 9;
		case "Western": return 10;
		case "Wilderness": return 11;
		default: return -1;
		}
	}

	public int getTierIndex(String regionKey) {
		if (regionKey.contains("Easy")) return 0;
		if (regionKey.contains("Medium")) return 1;
		if (regionKey.contains("Hard")) return 2;
		if (regionKey.contains("Elite")) return 3;
		return -1;
	}
	public boolean DiaryObjectClick(int objectId, int type) {
		switch (type) {
		case 2:
			switch (objectId) {
			case 34384:
				completeAchievement("ArdougneEasy", "Steal a cake from the East Ardougne market stalls", 2);
				return true;
			}
			return true;
		case 1:
			switch (objectId) {

			case 24222:
				if (c.getX() == 2936)
					AgilityHandler.delayEmote(c, "WALL_EMOTE", -2, 0, 3);
				else if (c.getX() == 2934)
					AgilityHandler.delayEmote(c, "WALL_EMOTE", 2, 0, 3);

				c.getAD().completeAchievement("FaladorEasy", "climb over the western Falador wall", 2);
				return true;
			case 1814:
				if (Boundary.isIn(c, Boundary.ARDOUGNE_BOUNDARY)) {
					completeAchievement("ArdougneEasy", "Use the Ardougne lever to teleport to the Wilderness", 8);
				}
				c.getPA().startLeverTeleport(3158, 3953, 0);
				return true;
			case 34616:
				// if (!easysCompleted[3]) {
				completeAchievement("ArdougneEasy", "Use the altar in East Ardougne's church", 4);
				// c.getPA().startTeleport(2897, 4849, 0, "modern");
				// }
				return true;
			case 9300:
				completeAchievement("VarrockEasy", "Jump over the fence south of Varrock", 6);
				return true;
			}
			return true;
		}
		return false;
	}
	public void DiaryNpcFirstClick(int NpcId) {
		int id = NpcId;
		switch(id) {
		case 8481:
			//if(!easysCompleted[0]){
			c.lastX = c.getX();
			c.lastY = c.getY();
			c.lastHeight = c.getHeight();
			completeAchievement("ArdougneEasy", "Have Wizard Cromperty teleport you to the Rune essence mine", 1);
			c.getPA().startTeleport(2897, 4849, 0, "modern");
			//}
			break;
		}
	}	
	public void DiaryNpcSecondClick(NPC npc) {
		int id = npc.npcType;
		int slot = -1;
		//NPC npc = NPCHandler.getNpc(id, npc.getX(), npc.getY());
		switch(id) {
		case 8481:
			//if(!easysCompleted[0]){
			c.lastX = c.getX();
			c.lastY = c.getY();
			c.lastHeight = c.getHeight();

			completeAchievement("ArdougneEasy", "Have Wizard Cromperty teleport you to the Rune essence mine", 1);
			npc.startAnimation(1818);
			c.getPA().startTeleport(2897, 4849, 0, "modern");
			//}
			break;
		}
	}
	public void loadAchievementProgress() {
		// 1. Reset counters
		for (int i = 0; i < 12; i++) {
			for (int j = 0; j < 4; j++) {
				diaries[i][j] = 0;
			}
		}

		// 2. Re-calculate counts from the PLAYER'S map
		int totalTasksDone = 0;
		int totalTasksPossible = 0;

		// We'll store the total possible per region in this array
		int[] totalPerRegion = new int[48]; // 12 regions * 4 difficulties

		String[] regions = {
				"ArdougneEasy", "ArdougneMedium", "ArdougneHard", "ArdougneElite",
				"DesertEasy", "DesertMedium", "DesertHard", "DesertElite",
				"FaladorEasy", "FaladorMedium", "FaladorHard", "FaladorElite",
				"FremennikEasy", "FremennikMedium", "FremennikHard", "FremennikElite",
				"KandarinEasy", "KandarinMedium", "KandarinHard", "KandarinElite",
				"KaramjaEasy", "KaramjaMedium", "KaramjaHard", "KaramjaElite",
				"KourendEasy", "KourendMedium", "KourendHard", "KourendElite",
				"Lumbridge&DraynorEasy", "Lumbridge&DraynorMedium", "Lumbridge&DraynorHard", "Lumbridge&DraynorElite",
				"MorytaniaEasy", "MorytaniaMedium", "MorytaniaHard", "MorytaniaElite",
				"VarrockEasy", "VarrockMedium", "VarrockHard", "VarrockElite",
				"WesternEasy", "WesternMedium", "WesternHard", "WesternElite",
				"WildernessEasy", "WildernessMedium", "WildernessHard", "WildernessElite"
		};

		for (int i = 0; i < regions.length; i++) {
			List<AchievementTask> tasks = playerAchievementMap.get(regions[i]);
			if (tasks != null) {
				int area = getAreaIndex(regions[i]);
				int tier = getTierIndex(regions[i]);

				totalPerRegion[i] = tasks.size();
				totalTasksPossible += tasks.size();

				for (AchievementTask task : tasks) {
					if (task.isDone()) {
						diaries[area][tier]++;
						totalTasksDone++;
					}
				}
			}
		}

		// 3. Update the "Grand Total" at the top of the interface
		c.getPA().sendFrame126(totalTasksDone + "/" + totalTasksPossible, 55797);

		// 4. Corrected Total Area Tasks Mapping
		// We calculate these by summing the 4 tiers (Easy, Med, Hard, Elite) for each region
		int[] totalAreaTasks = new int[12];
		for (int i = 0; i < 12; i++) {
			totalAreaTasks[i] = totalPerRegion[i*4] + totalPerRegion[i*4+1] + totalPerRegion[i*4+2] + totalPerRegion[i*4+3];
		}

		// 5. Update the individual counts (Easy, Med, Hard, Elite)
		// Using a loop here saves you 50 lines of code!
		int[] baseFrame = {54707, 54717, 54727, 54737, 54747, 54757, 54767, 54777, 54787, 54797, 54807, 54817};
		int[] textId = {54702, 54712, 54722, 54732, 54742, 54752, 54762, 54772, 54782, 54792, 54802, 54812};

		for (int f = 0; f < 12; f++) {
			int areaTotalDone = 0;
			for (int j = 0; j < 4; j++) {
				areaTotalDone += diaries[f][j];
				c.getPA().sendFrame126("" + diaries[f][j], baseFrame[f] + j);
				// This also handles the progress bars if your client supports them
				c.getPA().sendAchieveProgress(f, j, diaries[f][j]);
			}
			// Update the "Region Total" (e.g., 5/40)
			c.getPA().sendFrame126(areaTotalDone + "/" + totalAreaTasks[f], textId[f]);
		}
	}
	/**
	 * Checks if a specific task in a specific region and difficulty is completed.
	 * Use this for quest requirements or diary cape checks.
	 */
	public boolean isTaskDone(String region, String difficulty, int taskIndex) {
	    String key = region + difficulty;
	    // CHANGE: Get from playerAchievementMap, not the static achievementMap
	    List<AchievementTask> tasks = playerAchievementMap.get(key); 
	    if (tasks != null && taskIndex >= 0 && taskIndex < tasks.size()) {
	        return tasks.get(taskIndex).isDone();
	    }
	    return false;
	}

	/**
	 * Checks if an entire tier (e.g., Ardougne Easy) is fully completed.
	 */
	public boolean isTierFinished(String region, String difficulty) {
		List<AchievementTask> tasks = playerAchievementMap.get(region + difficulty);
		if (tasks == null) return false;
		for (AchievementTask task : tasks) {
			if (!task.isDone()) return false;
		}
		return true;
	}

	/**
	 * Checks if the player has finished all difficulties for a specific region.
	 */
	public boolean isRegionFinished(String region) {
		return isTierFinished(region, "Easy") && 
				isTierFinished(region, "Medium") && 
				isTierFinished(region, "Hard") && 
				isTierFinished(region, "Elite");
	}

	// --- Region Specific Shortcuts ---

	public boolean hasArdougneCloak1() { return isTierFinished("Ardougne", "Easy"); }
	public boolean hasArdougneCloak2() { return isTierFinished("Ardougne", "Medium"); }
	public boolean hasArdougneCloak3() { return isTierFinished("Ardougne", "Hard"); }
	public boolean hasArdougneCloak4() { return isTierFinished("Ardougne", "Elite"); }

	public boolean hasDesertAmulet1() { return isTierFinished("Desert", "Easy"); }
	public boolean hasDesertAmulet2() { return isTierFinished("Desert", "Medium"); }
	public boolean hasDesertAmulet3() { return isTierFinished("Desert", "Hard"); }
	public boolean hasDesertAmulet4() { return isTierFinished("Desert", "Elite"); }

	public boolean hasVarrockArmor1() { return isTierFinished("Varrock", "Easy"); }
	public boolean hasVarrockArmor2() { return isTierFinished("Varrock", "Medium"); }
	public boolean hasVarrockArmor3() { return isTierFinished("Varrock", "Hard"); }
	public boolean hasVarrockArmor4() { return isTierFinished("Varrock", "Elite"); }

	public boolean hasMorytaniaLegs1() { return isTierFinished("Morytania", "Easy"); }
	public boolean hasMorytaniaLegs2() { return isTierFinished("Morytania", "Medium"); }
	public boolean hasMorytaniaLegs3() { return isTierFinished("Morytania", "Hard"); }
	public boolean hasMorytaniaLegs4() { return isTierFinished("Morytania", "Elite"); }

	public boolean hasFaladorShield1() { return isTierFinished("Falador", "Easy"); }
	public boolean hasFaladorShield2() { return isTierFinished("Falador", "Medium"); }
	public boolean hasFaladorShield3() { return isTierFinished("Falador", "Hard"); }
	public boolean hasFaladorShield4() { return isTierFinished("Falador", "Elite"); }

	public boolean hasFremennikBoots1() { return isTierFinished("Fremennik", "Easy"); }
	public boolean hasFremennikBoots2() { return isTierFinished("Fremennik", "Medium"); }
	public boolean hasFremennikBoots3() { return isTierFinished("Fremennik", "Hard"); }
	public boolean hasFremennikBoots4() { return isTierFinished("Fremennik", "Elite"); }

	public boolean hasKandarinHeadgear1() { return isTierFinished("Kandarin", "Easy"); }
	public boolean hasKandarinHeadgear2() { return isTierFinished("Kandarin", "Medium"); }
	public boolean hasKandarinHeadgear3() { return isTierFinished("Kandarin", "Hard"); }
	public boolean hasKandarinHeadgear4() { return isTierFinished("Kandarin", "Elite"); }

	public boolean hasLumbridgeRing1() { return isTierFinished("Lumbridge&Draynor", "Easy"); }
	public boolean hasLumbridgeRing2() { return isTierFinished("Lumbridge&Draynor", "Medium"); }
	public boolean hasLumbridgeRing3() { return isTierFinished("Lumbridge&Draynor", "Hard"); }
	public boolean hasLumbridgeRing4() { return isTierFinished("Lumbridge&Draynor", "Elite"); }

	public boolean hasWildernessSword1() { return isTierFinished("Wilderness", "Easy"); }
	public boolean hasWildernessSword2() { return isTierFinished("Wilderness", "Medium"); }
	public boolean hasWildernessSword3() { return isTierFinished("Wilderness", "Hard"); }
	public boolean hasWildernessSword4() { return isTierFinished("Wilderness", "Elite"); }
}
