package server.model.players.skills;

import server.model.players.Player;
import server.Config;
import server.Server;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.model.players.packets.ClickingButtons;

public class ProcessSkillGuides {

	private Player c;
	public ProcessSkillGuides(Player c){
		this.c = c;
	}

	public String[][] SideBarTexts = {
			{"Attack", "Weapons", "Armour", "Salamanders"},
			{"Strength", "Weapons", "Armour", "Shortcuts", "Areas", "Barbarian"},
			{"Defence", "Armour", "Penance", "Prayers", "Shields"},
			{"Ranged", "Bows", "Thrown", "Crossbows", "Armour", "Miscellaneous", "Shortcuts", "Salamanders"},
			{"Prayer", "Standard Prayers", "Equipment", "Other"},
			{"Magic", "Normal Spells", "Ancient Magicks", "Lunar Spells", "Armour", "Bolts", "Weapons", "Equipment", "Salamanders"},
			{"RuneCraft", "Runes", "Multiple Runes", "Rune Pouches", "Infusing", "Other"},
			{"Construction", "Rooms", "Skills", "Surfaces", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Servants", "House Size"},

			{"Hitpoints", "Hitpoints", "Healing", "Equipment"},
			{"Agility", "Courses", "Areas", "Shortcuts", "Barbarian", "Other"},
			{"Herblore", "Potions", "Herbs", "Barbarian Potions", "Other"},
			{"Theiving", "Pickpocket", "Stalls", "Chests", "Other"},
			{"Crafting", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Other"},
			{"Fletching", "Arrows", "Bows", "Bolts", "Darts", "Crossbows", "Javelins", "Shields", "Other"},
			{"Slayer", "Monsters", "Equipment", "Slayer Masters"},
			{"Hunter", "Tracking", "Birds", "Butterflies", "Implings", "Deadfall", "Box Trap", "Net Trap", "Pitfalls", "Aeriel", "Traps", "Clothing", "Other"},

			{"Mining", "Rocks", "Equipment", "Areas", "Shooting Stars"},
			{"Smithing", "Smelting", "Bronze", "Blurite", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Other"},
			{"Fishing", "Small Net", "Big Net", "Rod", "Harpoon", "Aerial", "Cage", "Barbarian", "Equipment", "Other"},
			{"Cooking", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Vegetable", "Dairy", "Gnome"},
			{"Firemaking", "Burning", "Barbarian", "Equipment", "Miscellaneous"},
			{"Woodcutting", "Trees", "Axes", "Other"},
			{"Farming", "Allotments", "Hops", "Trees", "Fruit Trees", "Bushes", "Flowers", "Herbs", "Special", "Scarecrows", "Multiple roots", "Other"}
	}; 			
	public String[][] FirstitemTexts = {
			{"Bronze", "Iron", "Steel", "Black", "Members: White", "Mithril", "Adamant", "Members: Battlestaves\\n(with 30 magic)", "Rune", "Members: Brine Sabre", "Members: Mystic staffs\\n(with 40 magic)", "Members: Granite Maul\\n(with 50 Strength)", "Members: Keris", "Members: Ancient Staff", "Members: Dragon", "Members: Barrelchest Anchor\\n(with 40 Strength)", "Members: Obsidian", "Members: 3rd Age Weapons", "Members: Saradomin Sword", "Members: Zamorakian Spear", "Members: Abyssal Whip and Dagger", "Members: Ahrims Staff", "Members: Dharoks Greataxe", "Members: Torags Hammers", "Members: Veracs Flail", "Members: Guthans Warspear", "Members: Godswords", "Members: Abyssal Tentacle", "Members: Dragonhunter Lance", "Members: Scythe of Vitur\\n(with 90 Strength)"},
			{"Bronze warhammer", "Iron warhammer", "Steel warhammer", "Members: Black halberd\\n(with 10 Attack)", "Members: White halberd\\n(with 10 Attack)", "Black warhammer", "Members: White warhammer\\n(with 10 Prayer)", "Members: Mithril halberd\\n(with 20 Attack)", "Members: Adamant halberd\\n(with 30 Attack)", "Mithril warhammer", "Members: Rune halberd\\n(with 40 Attack)", "Adamant warhammer", "Members: Dragon halberd\\n(with 60 Attack)", "Rune warhammer", "Members: Barrelchest Anchor\\n(with 60 Attack)", "Members: Granite Maul\\n(with 50 Attack)", "Members: TzHaar-Ket-Om", "Members: Dragon warhammer", "Members: Dharok's greataxe\\n(with 70 Attack)", "Members: Torag's hammers\\n(with 70 Attack)", "Members: Scythe of Vitur\\n(with 80 Attack)"},
			{"Bronze", "Iron", "Steel", "Black", "Members: White", "Members: Slayer helm", "Mithril", "Members: Yak-hide", "Members: Initiate armour\\n(after Recruitment Drive, with 10\\nPrayer)", "Adamantite", "Members: Proselyte armour\\n(after Slug Menace, with 20 Prayer)", "Rune", "Members: Rock-shell armour\\n(after Fremennik Trials)", "Members: Void Knight equipment\\n(with 42 combat stats and 22 Prayer)", "Members: Fremennik helmets\\n(after Fremennik Trials)", "Members: Granite\\n(with 50 Strength)", "Members: Helm of neitiznot\\n(after Fremennik Isles)", "Members: Dragon", "Members: Bandos armour", "Members: 3rd age fighter armour", "Members: Barrows armour", "Members: Armadyl armour\\n(with 70 Ranged)", "Members: Crystal shield\\n(with 50 Agility)", "Members: Primordial boots\\n(with 75 Strength)", "Members: Eternal boots\\n(with 75 Magic)", "Members: Pegasian boots\\n(with 75 Ranged)"},
			{"Standard bows\\n Ammo: Arrows up to iron", "Oak bows\\n Ammo: Arrows up to steel", "Willow bows\\n Ammo: Arrows up to mithril", "Maple bows\\n Ammo: Arrows up to adamant", "Members: Ogre composite bows\\n Ammo: 'Brutal' Arrows up to rune", "Members: Yew bows\\n Ammo: Arrows up to rune","Members: Magic bows\\n Ammo: Arrows up to amethyst","Members: Seerculls\\n Ammo: Arrows up to amethyst","Members: Dark bows\\n Ammo: Arrows up to dragon","Members: 3rd age bow\\n Ammo: Arrows up to dragon","Members: Crystal bows(with 50 Agility)\\n Ammo: None","Members: Twisted bow\\n Ammo: Arrows up to dragon"},
			{"Thick Skin", "Burst of Strength","Clarity of Thought","Sharp Eye","Mystic Will","Rock Skin","Superhuman Strength","Improved Reflexes","Rapid Restore","Rapid Heal","Protect Item","Hawk Eye","Mystic Lore","Steel Skin","Ultimate Strength","Incredible Reflexes","Protect from Magic","Protect from Missiles","Protect from Melee","Eagle Eye","Mystic Might","Members: Retribution","Members: Redemption","Members: Smite","Members: Preserve","Members: Chivalry(with 65 Defence, after King's Ransom)","Members: Piety(with 70 Defence, after King's Ransom)","Members: Rigour(with 70 Defence, from Chambers of Xeric)","Members: Augury(with 70 Defence, from Chambers of Xeric)"},
			{"Home teleport", "Wind Strike", "Confuse", "Water Strike", "Level 1 enchant", "Earth Strike", "Weaken", "Fire Strike", "Bones to bananas","Wind Bolt","Curse","Bind","Low level alchemy","Water Bolt","Varrock teleport","Level 2 enchant","Earth Bolt","Lumbridge teleport","Telekinetic grab","Fire Bolt","Falador teleport","Crumble undead","Wind Blast","Superheat item","Camelot teleport","Water Blast","Kourend Castle teleport","Level 3 enchant","Iban blast","Snare","Magic dart","Ardougne teleport","Earth Blast","High level alchemy","Charge water orb","Level 4 enchant","Watertower teleport","Fire Blast","Charge earth orb","Bones to peaches","Claws of Guthix","Flames of Zamorak","Saradomin Strike","Trollheim teleport","Wind Wave","Charge fire orb","Ape Atoll teleport","Water Wave","Charge air orb","Vulnerability","Level 5 enchant","Earth Wave","Enfeeble","Teleother Lumbridge","Fire Wave","Entangle","Stun","Charge","Teleother Falador","Tele block","Level 6 enchant","Teleother Camelot","Level 7 enchant"},
			{"RuneCraft", "Runes", "Multiple Runes", "Rune Pouches", "Infusing", "Other"},
			{"Construction", "Rooms", "Skills", "Surfaces", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Servants", "House Size"},

			{"Hitpoints are used to tell you how\\nhealthy your character is. A character\\nwho reaches 0 Hitpoints has died, but\\nwill reappear in their chosen respawn\\nlocation(normally Lumbridge).", "", "if your see any red 'hit splats' during\\ncombat, the number shown corresponds\\nto the number of Hitpoints lost as a\\nresult of that strike.", "", "Blue hit splats mean no damage has\\nbeen dealt.", "Green hit splats are poison damage.", "Teal hit splats are venom damage.\\n(Members)", "Orange hit splats are disease damage.\\n(Members)"},
			{"Gnome Stronghold Agility Course", "Gnomeball game","Low-level Agility Arena obstacles","Shayzien Basic Course","Draynor Village Rooftop Course","Al Kharid Rooftop Course","Medium-level Agility Arean obstacles","Werewolf Skullball game","Agility Pyramid","Varrock Rooftop Course","Penguin Agility Course","Barbarian Outpost Agility Course","Canifis Rooftop Course","High-level Agility Arena obstacles","Shayzien Advanced Course","Ape Atoll Agility Course","Falador Rooftop Course","Wilderness Course","Werewolf Agility Course","Seers' Village Rooftop Course","Caves south of Dorgesh-Kaan","Pollnivneach Rooftop Course","Rellekka Rooftop Course","Ardougne Rooftop Course"},
			{"Attack potion\\nGuam leaf & eye of newt", "Anti-poison potion\\nMarrentill & ground unicorn horn", "Relicym's balm", "Strength potion\\nTarromin & limpwurt root","Serum 207\\nTarromin & ashes","Guam tar\\nGuam leaf & swamp tar","Compost potion\\nHarralander & volcanic ash","Stat restore potion\\nHarralander & red spiders' eggs","Energy potion\\nHarralander & chocolate dust","Defence potion\\nRanarr weed & white berries","Marrentill tar\\nMarrentill & swamp tar","Agility potion\\nToadflax & toad legs","Combat potion\\nHarralander & ground desert goat horn","Prayer restore potion\\nRanarr weed & snape grass","Tarromin tar\\nTarromin & swamp tar","Harralander tar\\nHarralander & swamp tar","Super attack potion\\nIrit leaf & eye of newt","Super anti-poison potion\\nIrit leaf & ground unicorn horn","Fishing potion\\nAvantoe & snape grass","Super energy potion\\nAvantoe & Mort Myre fungi","Hunting potion - Avantoe & ground\\nsabre-toothed kebbit teeth","Super strength potion\\nKwuarm & limpwurt root","Magic essence potion\\nStar flower & ground gorak's claw","Weapon poison\\nKwuarm & ground blue dragon scale","Super restore potion\\nSnapdragon & red spiders' eggs","Super defence potion\\nCadantine & white berries","Antidote+\\nCoconut milk, toadflax & yew roots","Anti-firebreath potion\\nLantadyme & ground blue dragon scale","Ranging potion\\nDwarf weed & wine of Zamorak","Weapon poison(+)\\nCoconut milk, cactus spine & red\\nspiders' eggs","Magic potion\\nLantadyme & potato cactus", "Stamina potion\\nAmylase & super energy potion", "Zamorak brew\\nTorstol & jangerberries","Antidote++\\nCoconut milk, irit leaf & magic tree\\nroots", "Saradomin brew\\nToadflax & crushed birdnest", "Weapon poison(++)\\nCoconut milk, nightshade & poison ivy\\nberries","Extended antifire potion\\nAntifire potion & lava scale shards", "Anti-venom\\nAntidote++ & Zulrah's scales","Super combat potion\\nSuper attack potion, super defence\\npotion, super strength potion & torstol","Super antifire potion\\nAntifire potion & crushed superior\\ndragon bones","Anti-venom+\\nAnti-venom & Torstol"},
			{"Theiving", "Pickpocket", "Stalls", "Chests", "Other"},
			{"Crafting", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Other"},

			{"15 Arrow shafts(Logs)", "Bronze Arrows", "Bronze 'brutal' arrows", "30 Arrow shafts(Oak logs)", "Iron arrows", "Iron 'brutal' arrows", "45 Arrow shafts (Willow logs)", "Steel arrows", "Steel 'brutal' arrows", "60 Arrow shafts(Maple logs)", "Mithril arrows","Mithril 'brutal' arrows", "Broad arrows\\n(after purchasing the ability with 300 Slayer points)", "Adamant arrows", "75 Arrow shafts(Yew logs)", "Adamant 'brutal' arrows", "90 Arrow shafts(Magic logs)", "Rune arrows","Rune 'brutal' arrows"},

			{"Slayer", "Monsters", "Equipment", "Slayer Masters"},
			{"Hunter", "Tracking", "Birds", "Butterflies", "Implings", "Deadfall", "Box Trap", "Net Trap", "Pitfalls", "Aeriel", "Traps", "Clothing", "Other"},

			{"Rune essence\\n(after Rune Mysteries)","Clay","Copper ore","Tin ore", "Blurite ore", "Members: Limestone","Iron ore","Silver ore","Members: Volcanic ash","Coal","Members: Pure essence\\n(after Rune Mysteries)","Members: Motherlode mine (lower level)","Members: Sandstone","Members: Dense essence","Gold","Members: Gem rocks","Members: Volcanic sulphur","Members: Lovakengj Blast Mine","Members: Granite","Members: Volcanic Mine","Mithril ore","Members: Motherlode mine (upper level)","Members: Lovakite ore","Adamanite ore","Members: Soft clay","Members: ?? Salt","Members: Efh Salt","Members: Urt Salt","Members: Basalt","Runeite ore","Members: Amethyst"},
			{"Smithing", "Smelting", "Bronze", "Blurite", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Other"},
			{"Fishing", "Small Net", "Big Net", "Rod", "Harpoon", "Aerial", "Cage", "Barbarian", "Equipment", "Other"},
			{"Cooking", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Vegetable", "Dairy", "Gnome"},
			{"Firemaking", "Burning", "Barbarian", "Equipment", "Miscellaneous"},
			{"Woodcutting", "Trees", "Axes", "Other"},
			{"Potatoes\\nPayment: Compost x2", "Onions\\nPayment: Potatoes(10)", "Cabbages\\nPayment: Onions(10)", "Tomatoes\\nPayment: Cabbages(10) x2", "Sweetcorn\\nPayment: Jute fibre x10", "Strawberries\\nPayment: Apples(5)", "Watermelons\\nPayment: Curry leaf x10", "Snape grass\\nPayment: Jangerberries x5"}
	}; 			
	public String[][] SeconditemTexts = {
			{"Void Knight equipment\\n(with 42 combat stats and 22 Prayer)"},
			{"Void Knight equipment\\n(with 42 combat stats and 22 Prayer)", "Granite armour\\n(with 50 defence)", "Primordial boots\\n(with 75 Defence)"},
			{"Fighter torso", "Runner boots", "Penance gloves", "Penance skirt\\n(with 60 Ranged)", "Fighter hat", "Ranger hat", "Healer hat", "Runner hat"},
			{"Bronze items", "Iron items", "Steel items", "Black items", "Mithril items", "Adamant items", "Rune items", "Chinchompas", "Amethyst darts", "Carnivorous chinchompas", "Dragon darts", "Dragon knives", "TokTz-Xil-Ul", "Dragon thrownaxes", "Toxic Blowpipe"},
			{"Initiate armour\\n (after Recruitment Drive, with 20\\n Defence)", "Proselyte armour\\n (after Slug Menace, with 30 Defence)", "Vestment robe top", "Vestment robe legs", "Void Knight equipment\\n (with 42 combat stats)", "Holy sandals", "Vestment cloak", "Vestment mitre", "Use completed prayer books\\n to bless holy and unholy symbols", "Spirit Shield\\n (with 45 Defence)", "Vestment stole", "Crozier", "Blessed spirit shield\\n (with 70 Defence)","Arcane & Spectral spirit shields\\n (with 75 Defence and 65 Magic)", "Elysian spirit shield\\n (with 75 Defence)"},
			{"Magic", "Normal Spells", "Ancient Magicks", "Lunar Spells", "Armour", "Bolts", "Weapons", "Equipment", "Salamanders"},
			{"RuneCraft", "Runes", "Multiple Runes", "Rune Pouches", "Infusing", "Other"},
			{"Construction", "Rooms", "Skills", "Surfaces", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Servants", "House Size"},

			{"Hitpoints", "Hitpoints", "Healing", "Equipment"},
			{"Rope-swing to Moss Giant Island","Stepping stones in Karamja Dungeon","Monkey bars under Edgeville","Pipe contortion in Karamja Dungeon","Stepping stone in south-eastern\\nKaramja","Pipe contortion in Karamja Dungeon","Elf area log balance","Contortion in Yanille Dungeon small room","Access the God Wars Dungeon area via\\nthe Agility route","Yanilla Dungeon's rubble climb","Enter the Saradomin area of the God\\nWars Dungeon"},
			{"Guam leaf", "Rogue's pure", "Snake weed", "Marrentill", "Tarromin", "Harralander", "Ranarr weed", "Toadflax", "Irit leaf", "Avantoe", "Kwuarm", "Snapdragon", "Cadantine", "Lantadyme", "Dwarf weed", "Torstol"},
			{"Theiving", "Pickpocket", "Stalls", "Chests", "Other"},
			{"Crafting", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Other"},
			{"Shortbow", "Longbow", "Oak shortbow", "Oak longbow", "Composite bows", "Willow shortbow", "Willow longbow", "Maple shortbow", "Maple longbow", "Yew shortbow", "Yew longbow", "Magic shortbow", "Magic longbow"},
			{"Slayer", "Monsters", "Equipment", "Slayer Masters"},
			{"Hunter", "Tracking", "Birds", "Butterflies", "Implings", "Deadfall", "Box Trap", "Net Trap", "Pitfalls", "Aeriel", "Traps", "Clothing", "Other"},

			{"Bronze pickaxe", "Iron pickaxe", "Steel pickaxe", "Black pickaxe", "Members: Mining gloves","Mithril pickaxe", "Adamant pickaxe","Rune pickaxe","Gilded pickaxe", "Members: Superior mining gloves", "Members: Dragon pickaxe","Members: 3rd age pickaxe","Members: Infernal pickaxe\\n(with 85 Smithing)","Members: Expert mining gloves","Members: Crystal pickaxe"},
			{"Smithing", "Smelting", "Bronze", "Blurite", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Other"},
			{"Fishing", "Small Net", "Big Net", "Rod", "Harpoon", "Aerial", "Cage", "Barbarian", "Equipment", "Other"},
			{"Cooking", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Vegetable", "Dairy", "Gnome"},
			{"Firemaking", "Burning", "Barbarian", "Equipment", "Miscellaneous"},
			{"Woodcutting", "Trees", "Axes", "Other"},
			{"Barley\\nPayment: Compost x3",  "Hammerstone hops\\nPayment: Marigolds",  "Asgarnian hops\\nPayment: Onions(10)",  "Jute plants\\nPayment: Barley malt x6",  "Yanillian hops\\nPayment: Tomatoes(5)",  "Krandorian hops\\nPayment: Cabbages(10) x3",  "Wildblood hops\\nPayment: Nasturtiums", }
	}; 			
	public String[][] ThirditemTexts = {
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{"Cross the River Lum to Al Kharid\\n(with 8 Agility and 37 Ranged)", "Karamja\\n(with 53 Agility and 42 Ranged)","Escape from the water obelisk island\\nwith 36 Agility and 39 Ranged)", "Scale the Observatory cliff\\n(with 23 Agility and 24 Ranged, after\\ncompleting the Observatory quest)", "Scale the Catherby cliff\\n(with 32 Agility and 35 Ranged)","Scale Falador wall\\n(with 11 Agility and 19 Ranged)","Scale Yanille wall\\n(with 39 Agility and 21 Ranged)","Cross cave, south of Dorgesh-Kaan\\n(with 70 Agility and 70 Strength, after\\ncompleteing Death to the Dorgeshuun"},
			{"Chivalry\\n(with 60 Prayer, after King's Ransom)", "Piety\\n(with 70 Prayer, after King's Ransom)", "Rigour\\n(with 74 Prayer, from Chambers of Xeric)", "Augury\\n(with 77 Prayer, from Chambers of Xeric)"},
			{"Crossbow\\n Ammo: Bronze crossbow bolts", "Phoenix Crossbow\\n Ammo: Bronze crossbow bolts", "Bronze crossbow\\n Ammo: Bronze crossbow bolts", "Blurite crossbow\\n Ammo: Bolts up to blurite", "Iron crossbow\\n Ammo: Bolts up to iron", "Dorgeshuun Crossbow\\n Ammo: Bolts up to iron", "Steel crossbow\\n Ammo: Bolts up to steel", "Mithril crossbow\\n Ammo: Bolts up to mithril", "Adamant crossbow\\n Ammo: Bolts up to adamant", "Hunter crossbow\\n Ammo: Kebbit bolts", "Runite crossbow\\n Ammo: Bolts up to runite", "Karil's crossbow\\n Ammo: Bolt racks", "Armadyl crossbow\\n Ammo: Bolts up to dragon"},
			{"Use superior dragon bones"},
			{"Magic", "Normal Spells", "Ancient Magicks", "Lunar Spells", "Armour", "Bolts", "Weapons", "Equipment", "Salamanders"},
			{"Small pouch: Holds 3 extra essence","Medium pouch: Holds 6 extra essence","Large pouch: Holds 9 extra essence","Giant pouch: Holds 12 extra essence"},
			{"Construction", "Rooms", "Skills", "Surfaces", "Decorative", "Trophies", "Games", "Garden", "Dungeon", "Chapel", "Other", "Servants", "House Size"},

			{"Hitpoints", "Hitpoints", "Healing", "Equipment"},

			{"Falador Agility shortcut","Cross the River Lum to Al Kharid\\n(with 19 Strength and 37 Ranged)","Corsair Cove shortcut","Scale Falador wall\\n(with 37 Strength and 19 Ranged)","Jump fence south of Varrock","Scale Goblin village wall","Corsair Cove Dungeon shortcut\\n(after Dragon Slayer)","Yanille Agility shortcut","Kourend Catacombs Agility shortcut","Slayer Tower Banshee shortcut","Coal Truck log balance","Grand Exchange Agility shortcut","Scale the Observatory cliff\\n(with 28 Strength and 24 Ranged, after\\ncompleting the Observatory quest)","Eagles' Peak Agility shortcut","Falador Agility shortcut","Kourend Catacombs pillar jump shortcut","Mount Karuulm lower cliffside climb","Corsair Cove Resource Area shortcut\\n(after Dragon Slayer)","Draynor Manor stones to Champions'\\nGuild","Scale the Catherby cliff\\n(with 35 Strength and 35 Ranged)","Shilo Village river crossing","Ardougne log balance shortcut","Kourend Catacombs Agility shortcut","Varrock Palace Garden trellis shortcut\\n(after Garden of Tranquillity)","Escape from the water obelisk island\\n(with 22 Strength and 39 Ranged)","Gnome Stronghold shortcut","Al Kharid Mining pit cliffside scramble","Scale Yanille wall\\n(with 38 Strength and 21 Ranged)","Hosidius lake isle jump","Trollheim easy cliffside scramble","Dwarven Mine narrow crevice","Draynor narrow tunnle","Trollheim medium cliffside scramble","Fremennik Slayer Dungeon spiked\\nblades jump","Trollheim advanced cliffside scramble","Hosidius river jump","Cosmic Temple - medium narrow walkway","Deep Wilderness - narrow tunnel","Trollheim hard cliffside scramble","Log balance to Fremennik Province","Arceuus essence mine boulder leap","Stepping stone into Morytania near the\\nNature Grotto","Pipe from Edgeville dungeon to Varrock\\nSewers","Arceuus essence mine eastern\\nscramble","Karmaja crossing, south of volcano\\n(with 21 Strength and 42 Ranged)","Motherlode Mine dark tunnel","Stepping stone by Miscellania docks","Rock climb in the Temple of the Eye","Brimhaven Dungeon eastern stepping\\nstones","Rellekka east fence shortcut","Port Phasmatys ectopool shortcut","Elven overpass easy cliffside scramble","Wilderness from the God Wars Dungeon\\narea climb","Estuary crossing on Mos Le'Harmless","Pillars in the Wintertodt's Prison","Asgarnian Ice Dungeon frozen throne tunnel","Slayer Tower medium spiked chain climb","Fremennik Slayer Dungeon narrow\\ncrevice","Mount Karuulm upper cliffside climb","Taverley dungeon lesser demon fence shortcut","Darkmeyer wall climb","Forthos Dungeon spiked blades jump","Trollheim Wilderness route","Rope to the Fossil Island volcano","Temple on the Salve to Morytania\\nshortcut","Revenant cave jump(easy)","Cosmic Temple advanced narrow walkway","Lumbridge Swamp to the Desert","Taverley wall shortcut","Heroes' Guild tunnel","Elven overpass medium cliffside\\nscramble","Ice Mountain western scramble","Arceuus essence mine northern\\nscramble","Mausoleum bridge jump\\n(with 59 Construction and after\\nCreature of Fenkenstrain)","Taverly Dungeon shortcuts to blue\\ndragons","Fossil island hardwood shortcut","Al Kharid Palace southern window","Slayer Tower advanced spiked chain\\nclimb","Gu'Tanoth wall climb\\n(after completing the Watchtower\\nquest)","Pollnivneach stepping stone","Stronghold Slayer Cave narrow tunnel","Basic Asgarnian Ice Dungeon wyvern\\ntunnel","Chaos temple stepping Stone","Barrows wall-jump","Troll Stronghold wall-climb","Arceuus essence mine western descent","Chasm of Fire platforms","Lava Dragon Isle jump","Fossil Island zipline","Meiyerditch Laboratory tunnels","Revenant cave jump(medium)","Island crossing near Zul-Andra","Wilderness Slayer Dungeon Crevices","Iorwerth Dungeon northern shortcut","Shilo village rock climb","Kharazi Jungle vine climb\\n(with Legends' Guild access)","Taverley Dungeon spiked blades jump","Waterbirth Dungeon crevice","Slayer Tower ivy climb","Lava maze northern jump","Adept Asgarnian Ice Dungeon wyvern\\ntunnel","Chasm of Fire Chain","Iorwerth Dungeon southern shortcut","Crandor rock-climb","Elven overpass advanced cliffside\\nscramble","Waterbirth Island rock-climb","Kalphite Lair shortcut","Darkmeyer wall jump","Brimhaven Dungeon vine to baby green\\ndragons","Karuulm Dungeon pipe","Revenant cave jump(hard)","Meiyerditch Laboratories advanced\\nshortcut"},

			{"Attack mix","Antipoison mix","Relicym's mix","Strength mix","Restore mix","Energy mix","Defence mix","Agility mix","Combat mix","Prayer mix","Super attack mix","Anti-poison supermix","Fishing mix","Super energy mix","Hunting mix","Super strength mix","Magic essence mix","Super restore mix","Super defence mix","Antidote+ mix","Antifire mix","Ranging mix","Magic mix","Zamorak mix","Stamina mix","Extended antifire mix","Super antifire mix","Extended super antifire mix"},
			{"Theiving", "Pickpocket", "Stalls", "Chests", "Other"},
			{"Crafting", "Weaving", "Armour", "Spinning", "Pottery", "Glass", "Jewellery", "Weaponry", "Other"},
			{"Bronze bolts", "Opal-tipped bronze bolts", "Blurite bolts", "Iron bolts", "Pearl-tipped iron bolts", "Silver bolts", "Steel bolts", "Red topaz-tipped steel bolts", "Barbed-tipped bronze bolts","Mithril bolts","Broad bolts","Sapphire-tipped mithril bolts", "Emerald-tipped mithril bolts", "Adamant bolts","Ruby-tipped adamant bolts","Diamond-tipped adamant bolts","Rune bolts","Dragonstone-tipped runite bolts","Onyx-tipped runite bolts"},
			{"Slayer", "Monsters", "Equipment", "Slayer Masters"},
			{"Hunter", "Tracking", "Birds", "Butterflies", "Implings", "Deadfall", "Box Trap", "Net Trap", "Pitfalls", "Aeriel", "Traps", "Clothing", "Other"},

			{"Members: Motherlode Mine", "Members: Motherlode Mine upper area", "Mining Guild"},
			{"Smithing", "Smelting", "Bronze", "Blurite", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Other"},
			{"Fishing", "Small Net", "Big Net", "Rod", "Harpoon", "Aerial", "Cage", "Barbarian", "Equipment", "Other"},
			{"Cooking", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Vegetable", "Dairy", "Gnome"},
			{"Firemaking", "Burning", "Barbarian", "Equipment", "Miscellaneous"},
			{"Woodcutting", "Trees", "Axes", "Other"},
			{"Oak trees\\nPayment: Tomatoes(5)", "Willow trees\\nPayment: Apples(5)", "Maple trees\\nPayment: Oranges(5)", "Yew trees\\nPayment: Cactus spine x10", "Magic trees\\nPayment: Coconut x25", }
	}; 		
	public String[][] FourthitemTexts = {
			{""},
			{"Access the God Wars Dungeon via the\\nStrength route", "Enter the Bandos area of the God Wars\\nDungeon"},
			{"Members: Oak shield", "Members: Willow shield", "Members: Maple shield", "Members: Yew shield", "Members: Magic shield", "Members: Redwood shield", "Members: Spirit shield\\n(with 55 Prayer)", "Members: Toktz-Ket-Xil", "Members: Odium & Malediction wards", "Members: Elf crystal (with 50 Agility)", "Members: Blessed spirit shield\\n(with 60 Prayer)", "Members: Spirit Dragonfire shield", "Members: Dragonfire ward\\n(with 70 Ranged)", "Members: Ancient Wyvern shield\\n(with 70 Magic)", "Members: Elysian spirit shield\\n(with 75 Prayer)", "Members: Arcane & Spectral spirit\\n shields\\n(with 70 Prayer and 65 Magic)","Members: Twisted buckler\\n(with 75 Ranged)", "Members: Dinh's bulwark\\n(with 75 Attack)"},
			{"Plain leather items", "Hard leather body\\n (with 10 Defence)", "Studded leather body\\n (with 20 Defence)", "Studded leather chaps", "Coif", "Members: Frog-leather\\n (with 25 Defence)", "Members: Snakeskin armour\\n (with 30 Defence)", "Members: Ava's attractor\\n (After Animal Magnetism)", "Members: Ranger boots", "Members: Robin hood hat", "Members: Spined armour\\n (after The Fremennik Trials, with 40\\n Defence)", "Green dragonhide vambraces", "Green dragonhide chaps", "Members: Green dragonhide body", "Members: Void Knight equipment\\n (with 42 combat stats and 22 prayer)", "Members: Ava's accumulator\\n (after Animal Magnetism)", "Members: Blue dragonhide vambraces", "Members: Blue dragonhide chaps", "Members: Blue dragonhide body\\n (with 40 Defence)", "Members: Penance skirt\\n (with 40 Defence)", "Members: Red dragonhide vambraces", "Members: Red dragonhide chaps", "Members: Red dragonhide body\\n (with 40 Defence)", "Members: 3rd age range armour\\n (with 45 Defence)", "Members: Black dragonhide vambraces", "Members: Black dragonhide chaps", "Members: Black dragonhide body\\n (with 40 Defence)", "Members: God dragonhide armour\\n (with 45 Defence)", "Members: Armadyl armour\\n (with 70 Defence)", "Members: Karil's leather armour\\n (with 70 Defence)", "Members: Pegasian boots\\n (with 75 Defence)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Harvest herbs from the Herbiboar on\\nFossil Island(with 80 Hunter).","Use the Herb Sack reward item,\\nperchased from the Tithe Farm or Slayer Masters."},
			{"Theiving"},
			{"Crafting"},
			{"Bronze darts", "Iron darts", "Steel darts", "Mithril darts", "Toxic blowpipe", "Adamant darts", "Rune darts", "Dragon darts"},
			{"Slayer", "Monsters", "Equipment", "Slayer Masters"},
			{"Hunter", "Tracking", "Birds", "Butterflies", "Implings", "Deadfall", "Box Trap", "Net Trap", "Pitfalls", "Aeriel", "Traps", "Clothing", "Other"},

			{"Mining", "Rocks", "Equipment", "Areas", "Shooting Stars"},
			{"Smithing", "Smelting", "Bronze", "Blurite", "Iron", "Steel", "Mithril", "Adamant", "Rune", "Other"},
			{"Fishing", "Small Net", "Big Net", "Rod", "Harpoon", "Aerial", "Cage", "Barbarian", "Equipment", "Other"},
			{"Cooking", "Meats", "Bread", "Pies", "Stews", "Pizzas", "Cakes", "Wine", "Hot Drinks", "Brewing", "Vegetable", "Dairy", "Gnome"},
			{"Firemaking", "Burning", "Barbarian", "Equipment", "Miscellaneous"},
			{""},
			{"Apple trees\\nPayment: Sweetcorn x9", "Banana trees\\nPayment: Apples(5) x4", "Orange trees\\nPayment: Strawberries(5) x3", "Curry trees\\nPayment: Bananas(5) x5", "Pineapple trees\\nPayment: Watermelon x10", "Papaya trees\\nPayment: Pineapple x10", "Palm trees\\nPayment: Papaya fruit x15"}
	}; 		
	public String[][] FifthitemTexts = {
			{""},
			{"Leaping trout\\n(with 15 Agility & 48 Fishing)" ,"Leaping salmon\\n(with 30 Agility & 58 Fishing)" ,"Tuna\\n(with 55 Fishing)" ,"Harpoonfish\\n(with 55 Fishing)" ,"Leaping sturgeon\\n(with 45 Agility & 70 Fishing)" ,"Swordfish\\n(with 70 Fishing)" ,"Shark\\n(with 96 Fishing)"},
			{""},
			{"Void Knight equipment\\n (with 42 combat stats and 22 Prayer)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Bronze/wooden crossbow", "Blurite/oak crossbow\\n(after Knight's Sword quest)", "Iron/willow crossbow", "Steel/teak crossbow", "Mithril/maple crossbow", "Adamant/mahogany crossbow", "Rune/yew crossbow","Dragon/magic crossbows"},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cooking"},
			{"Firemaking"},
			{""},
			{"Redberry bushes\\nPayment: Cabbages(10) x4", "Cadavaberry bushes\\nPayment: Tomatoes(5) x3", "Dwellberry bushes\\nPayment: Strawberries(5) x3", "Jangerberry bushes\\nPayment: Watermelon x6", "White berry bushes\\nPayment: Mushroom x8", "Poison ivy bushes", }
	}; 
	public String[][] SixthitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Scale Falador wall\\n(with 11 Agility and 37 Strength)", "Scale Yanille wall\\n(with 39 Agility and 38 Strength)", "Scale Observatory cliff (after\\nObservatory quest)", "Scale the Catherby cliff\\n(with 32 Agility and 35 Strength)", "Cross the River Lum to Al Kharid\\n(with 8 Agility and 19 Strength)", "Escape from the water obelisk island\\n(with 36 Agility and 22 Strength)", "Karamja, south of the volcano\\n(with 53 Agility and 21 Strength)", "Hallowed Sepulchre - Grapple swing", "Cross cave south of Dorgesh-Kaan\\n(with 70 Agility and 70 Strength)"},
			{"Prayer"},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Bronze javelins","Iron javelins","Steel javelins","Light ballistae","Mithril javelins","Adamant javelins","Heavy ballistae","Rune javelins","Amethyst javelins","Dragon javelins"},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cake","Chocolate cake","To make a cake: ","1.Mix flour,eggs and milk together in a\\ncake tin.","2.Cook the cake by using it with a stove.","3.Optional: Buy some chocolate and add\\nit to the cake to make a chocolate cake."},
			{"Firemaking"},
			{""},
			{"Marigolds\\nProtects onions, tomatoes and\\npotatoes", "Rosemary\\nProtects cabbages from disease", "Make and place a scarecrow\\nProtects sweetcorn from birds", "Nasturtiums\\nProtects watermelons from disease", "Woad", "Limpwurt plants", }
	}; 	
	public String[][] SeventhitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cooking"},
			{"Firemaking"},
			{""},
			{"Guam leaf", "Marrentill", "Tarromin", "Harralander", "Goutweed", "Ranarr weed", "Toadflax", "Irit leaf", "Avantoe", "Kwuarm", "Snapdragon", "Cadantine", "Lantadyme", "Dwarf weed", "Torstol"}
	}; 

	public String[][] EighthitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Nettle tea","To make tea:","1.Fill a bowl with water.","2.Add nettles to the bowl of water.","3.Boil the water on a range or a fire","4.Transfer the bowl of tea into a cup.","5.If desired, add a bucket\\nof milk to the tea."},
			{"Firemaking"},
			{""},
			{"Giant Seaweed\\nPayment: Numulites x200","Teak trees\\nPayment: Limpwurt root x15","Grapes","Bittercap mushrooms","Mahogany trees\\nPayment: Yanillian hops x25","Cacti\\nPayment: Cadava berries x6","Belladonna","Potato Cacti\\nPayment: Snape grass x8","Hespori","Calquat trees\\nPayment: Poison ivy berries x8","Crystal trees"}
	}; 

	public String[][] NinthitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cooking"},
			{"Firemaking"},
			{""},
			{"Able to make and place a scarecrow","Scarecrows ward sweetcorn from being\\nattacked by birds, which helps to prevent\\ndisease.","How to make a scarecrow:","1.Fill an empty sack with straw\\nfrom a bale.","2.Drive the hay sack onto\\na bronze spear.","3.Place a watermelon on top as a head.","4.Stand the scarecrow in an empty \\nflower patch."}
	}; 

	public String[][] TenthitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Baked potato","Spicy sauce(topping ingredient)","Chilli con carne(topping)","Scrambled egg(topping ingredient)","Scrambled egg and tomato(topping)","Sweetcorn","Baked potato with butter","Baked potato with chilli con carne","Fried onion(topping ingredient)","Fried mushroom(topping ingredient)","Baked potato with butter and cheese","Baked potato with egg and tomato","Fried mushroom and onion(topping)","Baked potato with mushroom and onion","Tuna and sweetcorn(topping)","Baked potato with tuna and sweetcorn","To make baked potatoes with toppings:","1.Bake the potato on a range.","2.Add some butter.","3.If needed, combine topping\\ningredients by chopping them into a bowl.","Ingredients for toppings:","1.Chilli con carne: meat & spicy sauce\\n(made from garlic and gnome spice)","2.Egg and tomato: scrambled egg and\\ntomato"},
			{"Firemaking"},
			{""},
			{"Giant Seaweed\\nPayment: Numulites x200","Teak trees\\nPayment: Limpwurt root x15","Grapes","Bittercap mushrooms","Mahogany trees\\nPayment: Yanillian hops x25","Cacti\\nPayment: Cadava berries x6","Belladonna","Potato Cacti\\nPayment: Snape grass x8","Hespori","Calquat trees\\nPayment: Poison ivy berries x8","Crystal trees"}
	}; 

	public String[][] EleventhitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cooking"},
			{"Firemaking"},
			{""},
			{"Giant Seaweed\\nPayment: Numulites x200","Teak trees\\nPayment: Limpwurt root x15","Grapes","Bittercap mushrooms","Mahogany trees\\nPayment: Yanillian hops x25","Cacti\\nPayment: Cadava berries x6","Belladonna","Potato Cacti\\nPayment: Snape grass x8","Hespori","Calquat trees\\nPayment: Poison ivy berries x8","Crystal trees"}
	}; 

	public String[][] TwelfthitemTexts = {
			{""},
			{"Bronze warhammer"},
			{""},
			{"Swamp lizard\\n(with 30 Attack, 30 Ranged & 30 Magic)", "Orange salamander\\n(with 50 Attack, 50 Ranged & 50 Magic)", "Red salamander\\n(with 60 Attack, 60 Ranged & 60 Magic)", "Black salamander\\n(with 70 Attack, 70 Ranged & 70 Magic)"},
			{""},
			{"Magic"},
			{"RuneCraft"},
			{"Construction"},

			{""},
			{"Agility"},
			{"Herblore"},
			{"Theiving"},
			{"Crafting"},
			{"Oak shield","Willow shield","Maple shield","Yew shield","Magic shield","Redwood shield",},
			{"Slayer"},
			{"Hunter"},

			{"Mining"},
			{"Smithing"},
			{"Fishing"},
			{"Cooking"},
			{"Firemaking"},
			{""},
			{"Giant Seaweed\\nPayment: Numulites x200","Teak trees\\nPayment: Limpwurt root x15","Grapes","Bittercap mushrooms","Mahogany trees\\nPayment: Yanillian hops x25","Cacti\\nPayment: Cadava berries x6","Belladonna","Potato Cacti\\nPayment: Snape grass x8","Hespori","Calquat trees\\nPayment: Poison ivy berries x8","Crystal trees"}
	}; 
	public int[][] LevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{1, 5, 20, 30, 30, 40, 50, 50, 60, 65, 70, 85},//range
			{1, 4, 7 , 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43, 44, 45, 46, 49, 52, 60, 70, 0, 0},//prayer
			{-1,1,3,5,7,9,11,13,15,17,19,20,21,23,25,27,29,31,33,35,37,39,41,43,45,47,48,49,50,50,50,51,53,55,56,57,58,59,60,60,60,60,60,61,62,63,64,65,66,66,68,70,73,74,75,79,80,80,82,85,87,90,93},//magic
			{1, 2, 5, 6, 9, 10, 13, 14, 15, 19, 20, 23, 27, 35, 40, 44, 54, 65, 77, 90},//runecraft
			{1, 1, 5, 10, 15, 20, 25, 30, 32, 35, 37, 40, 42, 45, 50, 55, 60, 65, 70, 75},//construction

			{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},//hitpoints
			{1,1,1,1,1,20,20,25,30,30,30,35,40,40,45,48,50,52,60,60,70,70,80,90},//agil
			{3,5,8,12,15,19,22,22,26,30,31,34,36,38,39,44,45,48,50,52,53,55,57,60,63,66,68,69,72,73,76,77,78,79,81,82,84,87,90,92,94},//herb
			{1},//theif
			{1},//craft
			{1,1,7,15,15,18,30,30,33,45,45,49,52,60,60,62,75,75,77},//fletch
			{1},//slayer
			{1},//hunter

			{1,1,1,1,10,10,15,20,22,30,30,30,35,38,40,40,42,43,45,50,55,57,65,70,70,72,72,72,72,85,92},//mining
			{1},
			{1},
			{1},
			{1},
			{1},
			{1, 5, 7, 12, 20, 31, 47, 61}//farming
	}; 		
	public int[][] SecondLevelTexts = {
			{42},//attk
			{42, 50, 75},//str
			{40, 40, 40, 40, 45, 45, 45, 45},//def
			{1,1,5,10,20,30,40,45,50,55,60,60,60,61,75},//range
			{10,20,20,20,22,31,40,40,50,55,60,60,60,70,75},//prayer
			{1},//magic
			{1, 2, 5, 6, 9, 10, 13, 14, 15, 19, 20, 23, 27, 35, 40, 44, 54, 65, 77, 90},//runecraft
			{1, 1, 5, 10, 15, 20, 25, 30, 32, 35, 37, 40, 42, 45, 50, 55, 60, 65, 70, 75},//construction

			{0,0,0,0,0,0,0,0,0,0},//hitpoints
			{10,12,15,22,30,34,45,49,60,67,70},//agil
			{3,3,3,5,11,20,25,30,40,48,54,59,65,67,70,75},//herb
			{1},//theif
			{1},//craft
			{5,10,20,25,30,35,40,50,55,65,70,80,85},//fletch
			{1},
			{1},

			{1,1,6,11,20,21,31,41,41,55,61,61,61,70,71},//mining
			{1},//smithing
			{1},//fishing
			{1,58},//cooking
			{1},//firemaking
			{1},//woodcutting
			{3, 4, 8, 13, 16, 21, 28}//farming
	}; 		
	public int[][] ThirdLevelTexts = {
			{30, 50, 60, 70},//attk
			{19, 21, 22, 28, 35, 37, 38, 70},//str
			{65, 70, 70, 70},//def
			{1, 1, 1, 16, 26, 28, 31, 36, 46, 50, 61, 70, 70},//range
			{70},//prayer
			{1},//magic
			{1,25,50,75},//runecraft
			{1},//construction

			{0},//hitpoints
			{5,8,10,11,13,14,15,16,17,18,20,21,23,25,26,28,29,30,31,32,32,33,34,35,36,37,38,39,40,51,42,42,43,43,44,45,46,46,47,48,49,50,51,52,53,54,55,56,56,57,58,59,60,60,60,60,61,61,62,63,63,63,64,64,65,65,66,66,67,68,69,69,70,70,70,71,71,71,72,72,72,72,73,73,74,74,74,75,76,77,78,79,79,80,81,81,82,82,83,84,84,85,85,86,86,87,88,89,93},//agil
			{4,6,9,14,24,29,33,37,40,42,47,51,53,56,58,59,61,67,71,74,75,80,83,85,86,91,98,99},//herb
			{1},//theif
			{1},//craft
			{9, 11, 24,39,41,43,46,48,51,54,55,56,58,61,63,65,69,71,73},//fletch
			{1},//slayer
			{1},//hunter

			{30, 57, 60},//mining
			{1},//smithing
			{1},//fishing
			{1},//cooking
			{1},//firemaking
			{1},//woodcutting
			{15,30,45,60,75}//farming
	}; 			
	public int[][] FourthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{60, 70},//str
			{10, 30, 40, 40, 40, 40, 45, 60,60,70,70,75,75,75,75,75,75,75},//def
			{1,1,20,20,20,25,30,30,40,40,40,40,40,40,42,50,50,50,50,60,60,60,60,65,70,70,70,70,70,70,75},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{31,58},//herb
			{1},//theif
			{1},//craft
			{10,22,37,52,67,81,95},//fletch
			{1},//slayer
			{1},//hunter

			{1},//mining
			{1},//sminting
			{1},//fishing
			{25,60},//cooking
			{1},//firemaking
			{1},//woodcutingg
			{27,33,39,42,51,57,68}//farming
	}; 			
	public int[][] FifthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{15, 30 ,35 ,35 ,45 ,50 ,76},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{42},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{9,24,39,46,54,61,69,78},//fletch
			{1},//slayer
			{1},//hunter

			{1},//mining
			{1},//smithing
			{1},//fishing
			{35,45,55,65},//cooking
			{1},//firemaking
			{1},//woodcutting
			{10,22,36,48,59,70}//farming
	}; 		
	public int[][] SixthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{19,21,24,35,37,39,42,62,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{3,17,32,47,47,62,72,77,84,92},//fletch
			{1},//slayer
			{1},//hunter

			{1},//mining
			{1},//fishing
			{1},//fishing
			{40, 50},//cooking
			{1},
			{1},
			{2,11,23,24,25,26}
	}; 	
	public int[][] SeventhLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},//mining
			{1},//smithing
			{1},//fishing
			{35,65},//cooking
			{1},//firemaking
			{1},//woodcutting
			{9,14,19,26,29,32,38,44,50,56,62,67,73,79,85}//farming
	}; 		
	public int[][] EighthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{23,35,36,53,55,55,63,64,65,72,74}
	}; 	
	public int[][] NinthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{23}
	}; 		
	public int[][] TenthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{23,31,38,39,46,53,54,61,68,69,76,83,84,91,99}
	}; 		
	public int[][] EleventhLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},//mining
			{1},//smithing
			{1},//fishing
			{21,38,48},//cooking
			{1},//firemaking
			{1},//woodcutting
			{45,50,65,85}//farming
	}; 		
	public int[][] TwelfthLevelTexts = {
			{1, 1, 5, 10, 10, 20, 30, 30, 40, 40, 40, 50, 50, 50, 60, 60, 60, 65, 70, 70, 70, 70, 70, 70, 70, 70, 75, 75, 75, 80},//attk
			{1, 1, 5, 5, 5, 10, 10, 10, 15, 20, 20, 30, 30, 40, 40, 50, 60, 60, 70, 70, 90},//str
			{1, 1, 5, 10, 10, 10, 20, 20, 20, 30, 30, 40, 40, 42, 45, 50, 55, 60, 65, 65, 70, 70, 70, 75,75,75},//def
			{30,50,60,70},//range
			{1},//prayer
			{1},//magic
			{1},//runecraft
			{1},//construction

			{0},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{27,42,57,72,87,92},//fletch
			{1},//slyer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{23}
	}; 		
	public int[][] itemIds = {
			{1205, 1203, 1207, 1217, 6591, 1209, 1211, 1391, 1213, 11037, 1405, 4153, 10581, 4675, 1215, 10887, 6523, 12426, 11730, 11716, 4151, 4710, 4718, 4747, 4755, 4726, 11700, 12006, 22978, 11944},
			{1337, 1335, 1339, 3196, 6599, 1341, 6613, 3198, 3200, 1343, 3202, 1345, 3204, 1347, 10887, 4153, 6527, 13576, 4718, 4747, 11944},
			{1139, 1137, 1141, 1151, 6621, 13263, 1143, 10822, 5574, 1145, 9672, 1147, 6128, 8839, 3751, 3122, 10828, 1149, 11832, 10350, 4745, 11718, 4224, 13239, 13235, 13237},
			{841, 843, 849, 851, 4827, 855, 861, 6724, 11235, 12424, 4212 ,11946},//range
			{-1},
			{-1},
			{-1},
			{-1},

			{-1, 6548},//hitpoints
			{2150,751,2996,13359,11849,11849,2996,1061,6970,11849,10595,1365,11849,2996,13379,4024,11849,964,4179,11849,4550,11849,11849,11849},//agil
			{221, 235, 4840, 225, 592, 10142, 1502, 223, 1550, 1975, 239, 10143, 2152, 9736, 231, 10144, 10145, 221, 235, 231, 2970,10109,225,9016,241,223, 239, 6049, 241, 245, 6016, 3138, 3138, 247, 6051, 6693, 6018, 6018, 12934, 269, 269},//herb
			{1},//theif
			{1},//craft
			{52,882,4773,52,884,4778,52,886,4783,52,888, 4793, 4172,890,52,4798, 52, 892, 4803},//fletching
			{1},//slayer
			{1},//hunter

			{1436,434,436,438,668,3211,440,442,21622,453,7936,12011,6977,13445,444,1629,13571,13573,6983,21532,447,12011,13356,449,1761,22593,22595,22597,22603,451,21347},
			{-1},
			{-1},
			{-1},
			{-1},
			{-1},
			{1942, 1957, 1965, 1982, 5986, 5504,5982, 231}
	}; 
	public int[][] SeconditemIds = {
			{8839},
			{8839, 4153, 13239},
			{10551, 10552, 10553, 10555, 10548, 10550, 10547, 10549},
			{864,863,865,869,866,867,868,9976,812,9977,11230,11230,6522,805,12926},//range
			{5574, 9672, 10458, 10464, 8839, 12598, 10446, 10452, 1718, 13734, 10470, 10440, 13736, 13744, 13742},
			{-1},//magic
			{-1},//runecraft
			{-1},//con

			{-1, 6548},//hp
			{6518,6518,6518,6520,6518,6520,6519,6520,6520,6521,6521},//agil
			{249,1534,1526,251,253,255,257,2998,259,261,263,3000,265,2481,267,269},//herb
			{-1},
			{-1},
			{841,839,843,845,4827,849,847,853,851,857,855,861,859},
			{1},
			{1},

			{1265,1267,1269,12297,21343,1273,1271,1275,23276,21345,11920,20014,13243,21392,23680,},//mining
			{1},//smithing
			{1},//fishing
			{1},//cooking
			{1},
			{1},
			{6006, 5994, 5996, 5931, 5998, 6000, 6002}
	}; 
	public int[][] ThirditemIds = {
			{10149, 10146, 10147, 10148},//attk
			{6515, 6515, 6515, 6515, 6517, 6517, 6517, 6515},//str
			{},
			{837, 767, 9174, 9176, 9177, 8880, 9179, 9181, 9183, 2883, 9185, 4734,11785},//range
			{22124},
			{-1},
			{1},
			{1},

			{-1, 6548},//hp
			{6517,6515,6517,6517,6514,6515,6514,6516,6516,6514,6515,6516,6517,6516,6517,6516,6515,6517,6517,6515,6517,6514,6515,6516,6516,6517,6517,6514,6517,6517,6517,6514,6515,617,6517,6516,6516,6517,6517,6514,6517,6514,6515,6517,6516,6516,6515,6517,6517,6515,6514,6514,6516,6517,6517,6516,6517,6514,6517,6514,6517,6514,6517,6517,6516,6517,6514,6514,6514,6516,6517,6516,6517,6514,6517,6517,6514,6517,6517,6514,6517,6514,6516,6514,6516,6516,6517,6517,6517,6514,6516,6516,6516,6517,6517,6517,6517,6516,6514,6514,6517,6517,6514,6514,6516},//agil
			{11429,11433,11437,11443,11449,11453,11457,11461,11445,11465,11469,11473,11477,11481,11517,11485,11489,11493,11497,11501,11505,11509,11513,11521,12633,11960,21994,22221},//herb
			{1},//theif
			{1},//craft
			{877, 879, 9139, 9140, 880, 9145, 9141, 9336, 881, 9142, 13280, 9337, 9338, 9143, 9339, 9340, 9144,9341,9342},//fletch
			{1},//slayer
			{1},//hunter

			{12011,12011,447},//mining
			{1},//smithing
			{1},//fishing
			{1},//cooking
			{1},
			{1},
			{1521, 1519, 1517, 1515, 1513}
	}; 
	public int[][] FourthitemIds = {
			{-1},
			{11793, 11793},
			{22251, 22254, 22257, 22260, 22263, 22266, 12829, 6524, 11926, 4224, 12831, 11283, 22002, 21633, 12817, 12825, 21000, 21015},
			{1129, 1131, 1133, 1097, 1169, 10954, 6322, 10498, 2577, 2581, 6133, 1065, 1099, 1135, 8839, 10499, 2487, 2493, 2499, 10555, 2489, 2495, 2501, 10330, 2491, 2497, 2503, 10370, 11718, 4736, 13237},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{21511,13226},//herb
			{1},//theif
			{1},//craft
			{806,807,808,809,810,12926, 811, 11230},//fletching
			{1},//slayer
			{1},//hunter

			{25532,25532,25532,25532,25532,25532,25532,25532,25532},//mining
			{1},//smithing
			{1},//fising
			{1},//cooking
			{1},//firemake
			{1},//woodcutting
			{1955, 1963, 2108, 5970, 2114, 5972, 5974}//farming
	}; 
	public int[][] FifthitemIds = {
			{-1},
			{11328,11330,359, 359, 11332, 371, 383},
			{1},
			{8841},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{9440,9442,9444,9446,9448,9450,9452,21952},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{1951, 753, 2126, 247, 239,6018}
	}; 
	public int[][] SixthitemIds = {
			{-1},
			{1},
			{1},
			{6517,6517,6517,6517,6515,6515,6515,6514,6515},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{825,826,827,19478,828,829,19481,830,21318,19484},//fletching
			{1},//slayer
			{1},//hunter

			{1},//mining
			{1},//smithing
			{1},//fishing
			{1891,1897},//cooking
			{1},
			{1},
			{6010, 6014, 6059, 6012, 5738, 225}
	}; 
	public int[][] SeventhitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{249,251,253,255,3261,257, 2998, 259, 261,263, 3000, 265, 2481, 267, 269}
	};
	public int[][] EighthitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{21504,6333,1987,6004,6332,6016,2398,3138,5980,23962}
	}; 
	public int[][] NinthitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{21504,6333,1987,6004,6332,6016,2398,3138,5980,23962}
	}; 
	public int[][] TenthitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{21504,6333,1987,6004,6332,6016,2398,3138,5980,23962}
	}; 
	public int[][] EleventhitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{21504,6333,1987,6004,6332,6016,2398,3138,5980,23962}
	}; 
	public int[][] TwelfthitemIds = {
			{-1},
			{1},
			{1},
			{10149,10146,10147,10148},//range
			{-1},
			{-1},
			{1},
			{1},

			{-1, 6548},//hitpoints
			{1},//agil
			{1},//herb
			{1},//theif
			{1},//craft
			{22251,22254,22257,22260,22263,22266},//fletching
			{1},//slayer
			{1},//hunter

			{1},
			{1},
			{1},
			{1},
			{1},
			{1},
			{21504,6333,1987,6004,6332,6016,2398,3138,5980,23962}
	}; 
	public void LoadSidebarText(int skillId, int slot) {
		int maxElements = Math.min(SideBarTexts[skillId].length, 13);

		for (int i = 1; i < maxElements; i++) {
			if (SideBarTexts[skillId][i] != null)
				c.getPA().sendFrame126("" + SideBarTexts[skillId][i], 48922 + i);
		}

		if (maxElements >= 0 && slot >= 0) {
			c.getPA().sendFrame126("" + SideBarTexts[skillId][0], 8716); // title
			c.getPA().sendFrame126("" + SideBarTexts[skillId][1], 8720); // subtitle
		}

		c.getPA().showInterface(8714);
	}
	public static int skillid = 0;
	public static boolean open = false;
	public void clearInterface(){
		for(int i = 0; i < 151; i++){
			c.getPA().sendFrame126("" , 48726+i);
			c.getPA().sendFrame126("" , 38836+i);
			c.getPA().sendFrame34a(8724, -1, i, 1);
		}
		for(int i = 0; i < 14; i++){
			c.getPA().sendFrame126("", 48923 + i);
		}
	}
	public void LoadMainInterface(int skillId, int slot, boolean open) {
		c.getPA().sendFrame126("" + SideBarTexts[skillId][0], 8719); // title
		LoadSidebarText(skillId, slot);
		LoadSkillItems(skillId, slot);
		LoadSkillLevels(skillId, slot);
		LoadItemText(skillId, slot);
		skillid = skillId;
		open = open;
		c.getPA().showInterface(8714);
	}
	public void LoadSkillItems(int skillId, int slot) {
		int maxElements = 150;
		if(slot == 0){
			maxElements = Math.min(itemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				if(skillId == 13)
					c.getPA().sendFrame34a(8724, itemIds[skillId][i], i, 5);
				else
					c.getPA().sendFrame34a(8724, itemIds[skillId][i], i, 1);
			}
		} else if(slot == 1){
			maxElements = Math.min(SeconditemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame34a(8724, SeconditemIds[skillId][i], i, 1);
		} else if(slot == 2){
			maxElements = Math.min(ThirditemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				if(skillId == 13)
					c.getPA().sendFrame34a(8724, ThirditemIds[skillId][i], i, 1);
				else
					c.getPA().sendFrame34a(8724, ThirditemIds[skillId][i], i, 1);
			}
		}  else if(slot == 3){
			maxElements = Math.min(FourthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, FourthitemIds[skillId][i], i, 1);
			}
		}  else if(slot == 4){
			maxElements = Math.min(FifthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, FifthitemIds[skillId][i], i, 1);
			}
		}  else if(slot == 5){
			maxElements = Math.min(SixthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, SixthitemIds[skillId][i], i, 1);
			}
		}   else if(slot == 6){
			maxElements = Math.min(SeventhitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, SeventhitemIds[skillId][i], i, 1);
			}
		}   else if(slot == 7){
			maxElements = Math.min(EighthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, EighthitemIds[skillId][i], i, 1);
			}
		} else if(slot == 8){
			maxElements = Math.min(NinthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, NinthitemIds[skillId][i], i, 1);
			}
		} else if(slot == 9){
			maxElements = Math.min(TenthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, TenthitemIds[skillId][i], i, 1);
			}
		} else if(slot == 10){
			maxElements = Math.min(EleventhitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, EleventhitemIds[skillId][i], i, 1);
			}
		} else if(slot == 11){
			maxElements = Math.min(TwelfthitemIds[skillId].length, 150);
			for(int i = 0; i < maxElements; i++){
				c.getPA().sendFrame34a(8724, TwelfthitemIds[skillId][i], i, 1);
			}
		} 
		c.getPA().sendFrame126("" + SideBarTexts[skillId][slot+1], 8720); // title
	}

	public void LoadSkillLevels(int skillId, int slot) {
		int maxElements = 150;
		if(slot == 0){
			maxElements = Math.min(LevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(LevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + LevelTexts[skillId][i], 48726 + i);
				else
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 1){
			maxElements = Math.min(SecondLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(SecondLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + SecondLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 2){
			maxElements = Math.min(ThirdLevelTexts[skillId].length, 150);
			System.out.println(ThirdLevelTexts[skillId].length);
			for (int i = 0; i < maxElements; i++) {
				if(ThirdLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + ThirdLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 3){
			maxElements = Math.min(FourthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(FourthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + FourthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 4){
			maxElements = Math.min(FifthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(FifthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + FifthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 5){
			maxElements = Math.min(SixthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(SixthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + SixthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		} else if(slot == 6){
			maxElements = Math.min(SeventhLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(SeventhLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + SeventhLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}else if(slot == 7){
			maxElements = Math.min(EighthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(EighthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + EighthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}else if(slot == 8){
			maxElements = Math.min(NinthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(NinthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + NinthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}else if(slot == 9){
			maxElements = Math.min(TenthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(TenthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + TenthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}else if(slot == 10){
			maxElements = Math.min(EleventhLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(EleventhLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + EleventhLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}else if(slot == 11){
			maxElements = Math.min(TwelfthLevelTexts[skillId].length, 150);
			for (int i = 0; i < maxElements; i++) {
				if(TwelfthLevelTexts[skillId][i] > 0)
					c.getPA().sendFrame126("" + TwelfthLevelTexts[skillId][i], 48726 + i);
				else 
					c.getPA().sendFrame126("", 48726 + i);
			}
		}
	}

	public void LoadItemText(int skillId, int slot){
		int maxElements = 150;
		int id = 38836;
		if(slot == 0) {
			maxElements = Math.min(FirstitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + FirstitemTexts[skillId][i], id+i); // title
		} else if(slot == 1) {
			maxElements = Math.min(SeconditemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + SeconditemTexts[skillId][i], id+i); // title
		} else if(slot == 2) {
			maxElements = Math.min(ThirditemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + ThirditemTexts[skillId][i], id+i); // title
		} else if(slot == 3) {
			maxElements = Math.min(FourthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + FourthitemTexts[skillId][i], id+i); // title
		} else if(slot == 4) {
			maxElements = Math.min(FifthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + FifthitemTexts[skillId][i], id+i); // title
		} else if(slot == 5) {
			maxElements = Math.min(SixthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + SixthitemTexts[skillId][i], id+i); // title
		} else if(slot == 6) {
			maxElements = Math.min(SeventhitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + SeventhitemTexts[skillId][i], id+i); // title
		} else if(slot == 7) {
			maxElements = Math.min(EighthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + EighthitemTexts[skillId][i], id+i); // title
		} else if(slot == 8) {
			maxElements = Math.min(NinthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + NinthitemTexts[skillId][i], id+i); // title
		} else if(slot == 9) {
			maxElements = Math.min(TenthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + TenthitemTexts[skillId][i], id+i); // title
		} else if(slot == 10) {
			maxElements = Math.min(EleventhitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + EleventhitemTexts[skillId][i], id+i); // title
		}else if(slot == 11) {
			maxElements = Math.min(TwelfthitemTexts[skillId].length, 150);
			for(int i = 0; i < maxElements; i++)
				c.getPA().sendFrame126("" + TwelfthitemTexts[skillId][i], id+i); // title
		}
	}

}
	/*End*/