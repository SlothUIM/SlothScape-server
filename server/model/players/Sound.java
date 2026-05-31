package server.model.players;

import server.model.npcs.NPCHandler;
import server.world.World;

public class Sound {

    public static enum SOUND_LIST {
        /**
         * Skilling / Objects
         */
        TREE_CUT_BEGIN(2605),      // Rustic "Chop" sound
        TREE_CUTTING(2735),        // Woodcutting chop
        TREE_EMPTY(2734),          // "Tree falls" creak
        DOOR(62),                  // Door Open
        GATE(69),                  // Metal Gate Open
        DOUBLEDOOR(62),            // Double Door (Same usually)
        CHESTOPEN(52),             // Chest creak
        RAKE_WEEDS(2500),          // Farming rake
        SMITHING_ANVIL(3771),      // Anvil clang
        MINING_ORE(3600),          // Pickaxe hit
        SMELTING_ORE(2725),        // Furnace hiss
        PROSPECTING(3599),         // Prospect tap
        RUNECRAFTING(2710),        // Runecrafting altar hum
        FILLCONTAINER(2609),       // Water fill
        COOK_ITEM(2577),           // Cooking sizzle
        SLASH_WEB(2500),           // Slash web (similar to rake)
        JUMPING_STONES(2461),      // Agility jump land
        PIN_PENDING(1040),
        PIN_BEEP(1041),
        PIN_CANCEL(1042),
        PIN_PENDING2(1043),

        BF_COLLECT_COKE(1049),
        BF_CONVEYOR_LOOP(1050),
        BF_COOLDOWN(1051),
        BF_CONVEYOR_LOOP_WITH_PEDALS(1052),
        BF_DRIVEBREAKS(1053),
        BF_OREINFURNACE(1054),
        BF_OVERHEAT(1055),
        BF_PEDAL_LOOP(1056),
        BF_PIPEBREAKS(1057),
        BF_PUMP_LOOP(1058),
        BF_REFUEL_STOVE(1059),
        BF_REPAIR(1060),
        BF_SMELT(1061),
        /**
         * MAGIC
         */
		EQUIP_MAGIC(226),
		SPELL_FAIL(227),
		ZAMORAK_FLAME(229),
		/*
		 * AIR
		 */
		WINDSTRIKE_CAST_AND_FIRE(220),
		WINDSTRIKE_HIT(221),
		WINDBOLT_CAST_AND_FIRE(218),
		WINDBOLT_HIT(219),
		WINDBLAST_CAST_AND_FIRE(216),
		WINDBLAST_HIT(217),
		WINDWAVE_CAST_AND_FIRE(222),
		WINDWAVE_HIT(223),
		/*
		 * WATER
		 */
		WATERSTRIKE_CAST_AND_FIRE(211),
		WATERSTRIKE_HIT(212),
		WATERBOLT_CAST_AND_FIRE(209),
		WATERBOLT_HIT(210),
		WATERBLAST_CAST_AND_FIRE(207),
		WATERBLAST_HIT(208),
		WATERWAVE_CAST_AND_FIRE(213),
		WATERWAVE_HIT(214),
		/*
		 * EARTH
		 */
		EARTHSTRIKE_CAST_AND_FIRE(132),
		EARTHSTRIKE_HIT(133),
		EARTHBOLT_CAST_AND_FIRE(130),
		EARTHBOLT_HIT(131),
		EARTHBLAST_CAST_AND_FIRE(128),
		EARTHBLAST_HIT(129),
		EARTHWAVE_CAST_AND_FIRE(134),
		EARTHWAVE_HIT(135),
		/*
		 * FIRE
		 */
		FIRESTRIKE_CAST_AND_FIRE(160),
		FIRESTRIKE_HIT(161),
		FIREBOLT_CAST_AND_FIRE(157),
		FIREBOLT_HIT(158),
		FIREBLAST_CAST_AND_FIRE(155),
		FIREBLAST_HIT(156),
		FIREWAVE_CAST_AND_FIRE(162),
		FIREWAVE_HIT(163),


        HIGH_ALCHEMY(97),              // 
        LOW_ALCHEMY(98),              // 
        BIND_IMPACT(116),              // 
        BIND_ALL(116),              // 
        BIND_CAST(116),              // 
        BONES_TO_BANANAS_ALL(114),              // 
        CHARGE_EARTH_ORB(115),              //
        CHARGE_AIR_ORB(116),              //
        CHARGE_FIRE_ORB(117),              //
        CHARGE_WATER_ORB(118),              //
        CONFUSE_CAST_AND_FIRE(119), //
        CONFUSE_ALL(120),//
        CONFUSE_HIT(121),//
        CRUMBLE_CAST_AND_FIRE(122),//    
        CRUMBLE_ALL(123),//
        CRUMBLE_HIT(124),//
        CURSE_ALL(125),// 
        CURSE_HIT(126),//
        CURSE_CAST_AND_FIRE(116),              //
        ENCHANT_SAPPHIRE_AMULET(136),              // 
        ENCHANT_DIAMOND_AMULET(137),              // 
        ENCHANT_DIAMOND_RING(138),              // 
        ENCHANT_DRAGON_AMULET(139),              // 
        ENCHANT_DRAGON_RING(140),              // 
        ENCHANT_EMERALD_AMULET(141),            // 
        ENCHANT_EMERALD_RING(142),            // 
        ENCHANT_ONYX_AMULET(143),            // 
        ENCHANT_ONYX_RING(144),            // 
        ENCHANT_RUBY_AMULET(145),            // 
        ENCHANT_RUBY_RING(146),            // 
        ENCHANT_SAPPHIRE_RING(147), 
        ENFEEBLE_ALL(148), 
        ENFEEBLE_HIT(149), 
        ENFEEBLE_CAST_AND_FIRE(150), 
        ENTANGLE_CAST_AND_FIRE(151), 
        ENTANGLE_ALL(152), 
        ENTANGLE_HIT(153), 
        MAGIC_DART_HIT(174),              // 
        SNARE_ALL(186),              // 
        STUN_ALL(187),              // 
        SUPERHEAT_ALL(190),              // 
        SUPERHEAT_FAIL(191),              // 
        TELEGRAB_ALL(192),              // 
        AIDE_TELEPORT_CHALK(193),              // 
        AIDE_TELEPORT_BOOK(194),              // 
        AIDE_TELEPORT_PORTAL(195),              // 
        AIDE_TELEPORT_SITDOWN(196),              // 
        BLOCK_TELEPORT(197),              // 
        QUICK_TELEPORT(198),              // 
        TELE_OTHER_CAST(199),              // 
        TELEPORT(200),
        TELEPORT_END(201),
        TELEPORT_BLOCK_CAST(202),
		TELEPORT_BLOCK_IMPACT(203),
		WILDERNESS_TELEPORT(204),
        CHARGE(1651),              // 
        CLAWS_OF_GUTHIX_FAIL(1652),              //
        CLAWS_OF_GUTHIX(1653),              //
        FLAMES_OF_ZAMORAK_FAIL(1654),              //
        FLAMES_OF_ZAMORAK(1655),             //
        SARADOMIN_STRIKE_FAIL(1656),              //
        SARADOMIN_STRIKE(1659),              //
        DARKLIGHT_WEAKEN(225), 
        /*
         * Miscellaneous
         */
        FOOD_EAT(2393),            // Generic "Eat" sound
        TWOHAND_SLASH(2503),
        TWOHAND_STAB(2504),
        TWOHAND_CRUSH(2502),
        TWOHAND_BLOCK(2502),
		TREE_FALL(2734), 
		WOODCHOP(2735),           // Chisel cut
        ITEM_DROP(2739),           // Generic drop
        ITEM_COOK(2577),
        PICK(2581),         // Generic pickup
        PICK2(2582),         // Generic pickup 2
        POUR_TEA(2583),
        SAND_BUCKET(2584),
        ATTACH_ORB(2585),
        CHISEL(2586), 
        LOOM_WEAVE(2587), 
        POTTERY(2588), 
        SMASH_GEM(2589), 
        SPINNING(2590), 
        STICHING(2591), 
        STITCHING(2592), 
        STRINGING(2593),
        STRIKE_AND_LIGHT(2594),
        BUNSEN_BURNER(2595),
        FIRE_LIT(2596),
        FLINT(2597),
        FUSE_LIGHT(2598),
        TINDERBOX_STRIKE(2599),
        FISHING_CAST(2600), 
        FISH_SWIM(2601), 
        LAVACAST(2602), 
        NET(2603), 
        FLETCH_ONCE(2604),  
        FLETCH(2605), 
        STRING_BOW(2606), 
        DRAGON_POTION_FINISHED(2607), 
        GRIND(2608),   
        TAP_FILL(2609),
        VIAL_EMPTY(2610), 
		VIAL_MIX(2611), 
		VIAL_MIX_SMOKEPUFF(2612), 
		VIAL_POUR(2613),      
		VIALPOUR(2614),      
		WELL_FILL(2615),      
		EAGLE_FLAP_LOOP(2616),      
		CROW_1(2617),        
		EAGLE_CRY(2618),        
		HUNTING_BIGCAT_JUMP(2619),      
        BONE_BURY(2738),        
        LIQUID(2401),              // Fletching cut
        DEATH(5183),               // Player death "Oh dear"
        STUNNED(2727),             // Stun sound
        CHURN(2574),
		PUSH_THROUGH_WHEAT(3728),
        /**
         * Prayer
         */
        NO_PRAY(2672), 
        PRAY_OFF(2673), 
        CANCEL_PRAYER(2663),
		THICK_SKIN(2690),
		BURST_OF_STRENGTH(2688),
		CLARITY_OF_THOUGHT(2664),
		SHARP_EYE(2685),
		MYSTIC_WILL(2670),
		ROCK_SKIN(2684),
		SUPERHUMAN_STRENGTH(2689),
		IMPROVED_REFLEXES(2662),
		RAPID_RESTORE(2679),
		RAPID_HEAL(2678),
		PROTECT_ITEMS(1982),
		HAWK_EYE(2666),
		MYSTIC_LORE(2668),
		STEEL_SKIN(2687),
		ULTIMATE_STRENGTH(2691),
		INCREDIBLE_REFLEXES(2690),
        PROTECT_MELEE(2676),
        PROTECT_MAGIC(2675),
        PROTECT_RANGE(2677),
        EAGLE_EYE(2665),
        MYSTIC_MIGHT(2669),
        RETRIBUTION(2682),
        REDEMPTION(2680),
        SMITE(2686),
        CHIVALRY(3826),
        PIETY(3825),
        
        /**
         * farming
         */
        SECATEURS(1106),
        FARMING_AMULET(2430),
        FARMING_EMPTYBARREL(2431),
        FARMING_DIBBING(2432),
        FARMING_FILLPLANTPOT(2433),
        FARMING_FILL(2434),
        FARMING_PLANT_SCARECROW(2435),
        FARMING_FILLPOT(2436),
        FARMING_PICK(2437),
        FARMING_PLANTCURE(2438),
        FARMING_POURPINT(2439),
        FARMING_PRUNE(2440),
        FARMING_PUTIN(2441),
        FARMING_RAKING(2442),
        FARMING_SCOOP(2443),
        FARMING_SPRINKLE(2444),
        FARMING_VALVE(2445),
        FARMING_WATERING(2446),
        /**
         * Magic
         */
        TELEGRAB(3006),
        EARTH_BOLT(127),           // Strike cast
        EARTH_STRIKE(132),         // Strike hit
        EARTH_BOLT_HIT(133),       // Bolt hit
        EARTH_BLAST_HIT(138),      // Blast hit
        EARTH_STRIKE_HIT(132),     // Strike hit rep
        EARTH_WAVE_HIT(143),       // Wave hit
        WEAKEN(119),
        BIND(100),
        SNARE(3003),
        ENTANGLE(3004),
        ALCHEMY_LOW(224),          // Low Alch
        ALCHEMY_HIGH(223),         // High Alch
        BLITZ_ICE(169),            // Ice Blitz

		FAIRY_TELEPORT(1098),
		
		//Appearance
		FEMALE_TO_MALE(1099),
		MALE_TO_FEMALE(1102),
		HUMAN_INTO_CHICKEN(1100),
		HUMAN_INTO_PIGLET(1101),
		PIGLET_INTO_HUMAN(1103),
		CHICKEN_INTO_HUMAN(1096),
        /*
		 * Combat - Weapons
		 */

		WEAPON_MISS(2521),
		// WHIP
		WHIP(4151, 2720),    
		SPECIAL_WHIP(4151, 2727),    
		
		// DRAGON DAGGER (DDS)
		ATTACK_DDS(5698, 2537),    
		SPECIAL_DDS(5698, 2537),    
		
		// SCIMITARS
		ATTACK_SCIMITAR(2500),       
		ATTACK_SCIMITAR_STAB(2501),  
		
		// LONGSWORDS
		HACKSWORD_CRUSH(2499),     
		HACKSWORD_SLASH(2500),        
		HACKSWORD_STAB(2501),

		STABSWORD_SLASH(2548),           
		STABSWORD_STAB(2549),     
		// MACES
		MACE_CRUSH(2508),
		MACE_STAB(2509),
		SPECIAL_MACE(2541),         
		
		// BATTLEAXES
		BAXE_CRUSH(2497),       
		BAXE_SLASH(2498),          
		SPECIAL_DBA(2537),   

		STAFF_CRUSH(2556),        
		STAFF_DOUBLE(2557),       
		TZHAAR_STAFF_CRUSH(2558),       
		STAFF_FASTER(2559),       
		STAFF_HIT(2560),       
		STAFF_LUNGE(2561),       
		STAFF_STAB(2562),  
		WARHAMMER_CRUSH(2567),       
		// SPEARS
		ATTACK_SPEAR(2544),          // Poke
		SPECIAL_DSPEAR(2542),        // Shove spec
		
		// HAMMERS / MAULS
		GRANITE_MAUL(4153, 2714),    // Granite Maul whack
		QUICKSMASH(4153, 2715),   // Quick smash spec
		
		// GREAT AXES (Dharok)
		ATTACK_GREATAXE(4718, 2500), // Slash
		ATTACK_GREATAXE_CRUSH(2501),
		
		// HALBERDS
		ATTACK_HALBERD(2524),
		SPECIAL_DHALBERD(2520),      // Sweep spec
		
		// BOWS
		ARROWLAUNCH2(2693),             // Magic Shortbow shot
		SHOOT_CROSSBOW(2695),        // Rune C'bow
		DART(2696),
		JAVELIN(2699),
		LONGBOW(2700),
		OUT_OF_AMMO(2701),           // Magic Shortbow Spec
		SHORTBOW(2702),
		SLAYER_ARROW(2703),
		THROWING_AXE(2706),
		THROWING_KNIFE(2707),
		THROWN(2708),
		
		//Sycthe
		SCYTHE_DOUBLE(2522),
		SCYTHE_HIT(2523),
		SCYTHE_SLASH(2524),
		SCYTHE_STAB(2525),
		
		//DARKBOW
		DARKBOW_FIRE(3734),
		DARKBOW_IMPACT(3735),
		DARKBOW_DOUBLEFIRE(3731),
		DARKBOW_DRAGON_ATTACK(3733),
		// SHIELDS
		BLOCK_SHIELD(13),          // Metal shield ding
		BLOCK_DEFENDER(28),        // Defender parry
		PICKAXE_ATTACK(1611),
		// GENERIC
		ATTACK_UNARMED(2555),        // Punch
		ATTACK_KICK(2556), 
		ATTACK_DAGGER(12), 
		SPEAR_ATTACK(2504), 
		ATTACK_STAB(2549),//STABSWORD_STAB
		/*
		 * EQUIP SOUNDS
		 */
        EQUIP_HELMET(2240),
		EQUIP_STAFF(2247),
		EQUIP_SWORD(2248),
        EQUIP_BOLT(2235),
        EQUIP_AXE(2229),
        EQUIP_ELEMENTAL_STAFF(2230),
        EQUIP_BANNER(2231),
		EQUIP_METAL_BODY(2239),
		EQUIP_LEGS(2242),
		EQUIP_BAXE(2232),
		EQUIP_FUN(2238),
		EQUIP_LEATHER(2241),
		EQUIP_RANGED(2244),
		EQUIP_FEET(2237),
		EQUIP_WHIP(2249),
		EQUIP_SPIKED(2246),
		EQUIP_WOOD(2250),
		EQUIP_HANDS(2236),
		EQUIP_DARKBOW(3738),
		EQUIP_SALAMANDER(732),
		
		SALAMANDER_ATTACK(733),
		SALAMANDER_BELCH(734),
		SALAMANDER_BLAST(735),
		SALAMANDER_BLAZE(736),
		SALAMANDER_DEATH(737),
		SALAMANDER_FLAME(738),
		SALAMANDER_HIT(739),
		SALAMANDER_SCORCH(740),
		
		RUBBER_CHICKEN_ATTACK(2257),
		PLATE_BREAK(2251),
		PLATE_SPIN(2255),
		SWORDCLASH3_1(2831),
		SWORDCLASH3_2(2832),
		SWORDCLASH3_3(2833),
		SWORDCLASH3_4(2834),
		BLADE4(1979),
		UNARMED_PUNCH(2566),
		UNARMED_KICK(2565),
		TOWN_CRIER_SCRATCH_HEAD(3816),
		TOWN_CRIER_RING_BELL_DOWN(3817),
		TOWN_CRIER_RING_BELL_UP(3813),
		INTERFACE_SELECT(2866),
		GENIE_APPEAR(2301),
		SMOKEPUFF(1930),
		CANNON_FIRE(1667),
		CANNON_SETUP(2878),
		CANNON_TURN(2877),
		GORILLA_INTO_HUMAN(1674),
		MONKEY_INTO_HUMAN(1681),
		HUMAN_INTO_GORILLA(1676),
		HUMAN_INTO_MONKEY(1677),    
		
		SMALL_MONKEY_INTO_HUMAN(1683),
		HUMAN_INTO_SMALL_MONKEY(1671),
		ZOMBIE_MONKEY_INTO_HUMAN(1690), 
		HUMAN_INTO_ZOMBIE_MONKEY(1680)   // Kick (Tink)

    	/*herblore*/,
		HERBLORE_CLEAN_HERB_1(3920), 
		HERBLORE_CLEAN_HERB_2(3921), 
		HERBLORE_CLEAN_HERB_3(3922), 
		HERBLORE_CLEAN_HERB_4(3923), 
		
		HUNTING_BIRDCAUGHT(2625), 
		HUNTING_BIRDTRAP(2626), 
		HUNTING_BOXTRAP(2627),
		/*
		 * NPC Sounds
		 */
		BANSHEE_ATTACK(282), 
		BANSHEE_ATTACK_EARMUFFS(283), 
		BANSHEE_ATTACK_NORMAL(284), 
		BANSHEE_DEATH(285), 
		BANSHEE_HIT(286), 
		BASILISK_ATTACK(287), 
		BASILISK_ATTACK2(288), 
		BASILISK_DEATH(289), 
		BASILISK_HIT(290), 
		FIREBAT_ATTACK(291), 
		BAT_ATTACK(292), 
		BAT_DEATH(293), 
		BAT_HIT(294), 
		FIREBAT_DEATH(295), 
		FIREBAT_HIT(296), 
		ANGER_BEAR_ATTACK(297), 
		ANGER_BEAR_DEATH(298), 
		ANGER_BEAR_HIT(299), 
		
		BEAR_ATTACK(300), 
		BEAR_DEATH(301), 
		BEAR_HIT(302), 
		BEARMAN_ATTACK(303), 
		BEARMAN_DEATH(304), 
		BEARMAN_HIT(305), 
		BIRD_ATTACK(306), 
		BIRD_DEATH(307), 
		BIRD_HIT(308), 
		SEAGULL_ATTACK(309), 
		SEAGULL_DEATH(310), 
		SEAGULL_HIT(311), 
		BLOODVELD_ATTACK(312), 
		BLOODVELD_DEATH(313), 
		BLOODVELD_HIT(314), 
		
		CHICKEN_ATTACK(355), 
		CHICKEN_DEATH(356), 
		CHICKEN_HIT(357), 
		LAY_EGG(358),
		CAVE_CRAWLER_ATTACK(341), 
		CAVE_CRAWLER_DEATH(342), 
		CAVE_CRAWLER_HIT(343), 
		COCKATRICE_ATTACK(363), 
		COCKATRICE_DEATH(364), 
		COCKATRICE_HIT(365), 
		CALF_ATTACK(366), 
		CALF_DEATH(367), 
		CALF_HIT(368),  
		COW_ATTACK(369), 
		COW_DEATH(370), 
		COW_HIT(371),  
		MILK_COW(372), 
		MILK_COW2(373) ,  
		SMALL_CRAB_ATTACK(374), 
		SMALL_CRAB_DEATH(375), 
		SMALL_CRAB_HIT(376) ,  
		HAND_ATTACK(377), 
		HAND_DEATH(378), 
		HAND_HIT(379) ,  
		CROCODILE_ATTACK(386), 
		CROCODILE_DEATH(387), 
		CROCODILE_HIT(388) ,  
		DARK_BEAST_ATTACK(389), 
		DARK_BEAST_DEATH(390), 
		DARK_BEAST_HIT(391) , 
		DEMON_CHAMPION_ATTACK(396),  
		BLACK_DEMON_ATTACK(397), 
		BLACK_DEMON_DEATH(398), 
		BLACK_DEMON_HIT(399),  
		DEMON_ATTACK(400), 
		DEMON_CHAMPION_DEATH(401), 
		DEMON_CHAMPION_HIT(402),
		DEMON_DEATH(403), 
		DEMON_HIT(404), 
		BABYDRAGON_ATTACK(405),
		BABYDRAGON_DEATH(406), 
		BABYDRAGON_HIT(407), 
		DRAGON_ATTACK(408),
		DRAGON_DEATH(409), 
		DRAGON_HIT(410),    
		DUCK_DEATH(411),   
		DUCK_HIT(412),   
		QUACK(413),   
		DUST_DEVIL_ATTACK(414),   
		DUST_DEVIL_DEATH(415),  
		DUST_DEVIL_HIT(416),
		DWARF_ATTACK(417),   
		DWARF_DEATH(418),  
		DWARF_HIT(419),
		DWARF_WORK(420),   
		ELF_ATTACK(425),  
		ELF_DEATH(426),
		ELF_HIT(427),   
		GARGOYLE_ATTACK(428),  
		GARGOYLE_DEATH(429), 
		GARGOYLE_HIT(430), 
		GHAST_DISAPPEAR(431),  
		GHAST_APPEAR(432),  
		GHAST_ATTACK(433),  
		GHAST_DEATH(434),
		GHAST_HIT(435),   
		GHOST_ATTACK(436),  
		GHOST_ATTACK2(437),
		GHOST_DEATH(438),   
		GHOST_HIT(439),  
		GHOST_HIT2(440),
		GHOUL_CHAMPION_ATTACK(441),   
		GHOUL_ATTACK(442),  
		GHOUL_DEATH(443),
		GHOUL_HIT(444), 
		GIANT_CHAMPION_ATTACK(445), 
		EARTH_GIANT_ATTACK(446), 
		FIRE_GIANT_ATTACK(447), 
		GIANT_ATTACK(448),  
		MOSS_GIANT_ATTACK(449), 
		GIANT_DEATH(450), 
		GIANT_HIT(451), 
		GNOME_GLIDER_ATTACK(452), 
		GNOME_ATTACK(453), 
		GNOME_DEATH(454), 
		GNOME_HIT(455), 
		GNOME_TORTOISE_MOUNTED_ATTACK(456), 
		GNOME_TORTOISE_ATTACK(457), 
		GNOME_TORTOISE_DEATH(458), 
		GNOME_TORTOISE_HIT(459), 
		GNOME_TORTOISE_MOUNTED_DEATH(460), 
		GNOME_TORTOISE_MOUNTED_HIT(461), 
		ANGER_GOBLIN_ATTACK(462), 
		ANGER_GOBLIN_DEATH(463), 
		ANGER_GOBLIN_HIT(464), 
		CAVE_GOBLIN_ATTACK(465), 
		CAVE_GOBLIN_DEATH(466), 
		CAVE_GOBLIN_HIT(467), 
		GOBLIN_ARMED(468),
		GOBLIN_ATTACK(469), 
		GOBLIN_CHAMPION_ATTACK(470), 
		GOBLIN_DEATH(471), 
		GOBLIN_HIT(472), 
		HOBGOBLIN_CHAMPION_HIT(473),

		BARBARIAN_FEMALE_HIT(501),
		BARBARIAN_DEATH(502),
		BARBARIAN_HIT(504),
		FEMALE_DEATH(505),
		FEMALE_HIT(505),
		FEMALE_HIT1(505),
		FEMALE_HIT2(505),
		FEMALE_HIT_1(505),
		FEMALE_HIT_2(505),
		HUMAN_DEATH(512),
		HUMAN_HIT(513),
		HUMAN_HIT2(514),
		HUMAN_HIT3(515),
		HUMAN_HIT4(516),
		HUMAN_HIT5(517),
		HUMAN_HIT_1(518),
		HUMAN_HIT_2(519),
		HUMAN_HIT_3(520),
		HUMAN_HIT_4(521),
		HUMAN_HIT_6(522),
		HUMAN_HIT_7(523),

		ICEFIEND_ATTACK(531),
		ICEFIEND_DEATH(532),
		ICEFIEND_HIT(533),

		JELLY_ATTACK(548),
		JELLY_DEATH(549),
		JELLY_HIT(550),

		KALPHITE_FLYINGQUEEN_ATTACK(551),
		KALPHITE_CRACK_OPEN(552),
		KALPHITE_FLYINGQUEEN_DEATH(553),
		KALPHITE_FLYINGQUEEN_HIT(554),
		KALPHITE_QUEEN_SPINES(555),
		KALPHITE_LIGHTING(557),
		KALPHITE_LIGHTING_GLOW(558),
		KALPHITE_LIGHTING_IMPACT(559),
		KALPHITE_QUEEN_ATTACK(563),
		KALPHITE_QUEEN_DEATH(564),
		KALPHITE_QUEEN_HIT(565),
		KALPHITE_QUEEN_HIT2(566),
		KALPHITE_SOLDIER_ATTACK(567),
		KALPHITE_SOLDIER_DEATH(568),
		KALPHITE_SOLDIER_HIT(569),
		KALPHITE_SQUEAL(570),
		KALPHITE_WORKER_ATTACK(571),
		KALPHITE_WORKER_DEATH(572),
		KALPHITE_WORKER_HIT(573),

		IMP_ATTACK(534),
		IMP_DEATH(535),
		IMP_HIT(536),
		RAT_ATTACK(710),
		RAT_DEATH(711),
		RAT_HIT(713),
		SMALL_ROCK_CRAB_ATTACK(717),
		ROCK_CRAB_ATTACK(718),
		ROCK_CRAB_DEATH(719),
		ROCK_CRAB_HIT(720),
		SMALL_ROCK_CRAB_DEATH(722),
		SMALL_ROCK_CRAB_HIT(723),
		ARMED_SKELETON_HIT(774),
		ARMED_SKELETON(775),
		SKELETON_ATTACK(776),
		SKELETON_DEATH(777),
		SKELETON_HISS(778),
		SKELLY_HIT(779),
		ZOMBIE_ATTACK(918),
		ZOMBIE2(919),
		ZOMBIE_ATMOSPHERIC1(920),
		ZOMBIE_CHAMPION_ATTACK(921),
		ZOMBIE_DEATH(922),
		ZOMBIE_HIT(923),
		ZOMBIE_PIRATE_ATTACK(924),
		ZOMBIE_PIRATE_DEATH(925),
		ZOMBIE_PIRATE_HIT(926),
		ZOMBIE_PIRATE_LIMBFALL(927),
		ZOMBIE_PIRATE_PUNCH(928), 
		ABYSSAL_DEMON_ATTACK(276),
		ABYSSAL_DEMON_BLOCK(278),
		ABYSSAL_DEMON_DEATH(277)
    	;
        public int weapon, weaponSound, sound;

        SOUND_LIST(int weapon, int weaponSound) {
            this.weapon = weapon;
            this.weaponSound = weaponSound;
        }

        SOUND_LIST(int sound) {
            this.sound = sound;
        }

        public int getSound() {
            return sound;
        }

        public int getWeaponSound(int weapon) {
            if (weapon == this.weapon)
                return weaponSound;
            else
                return sound;
        }
    }

    public static int handleBlockSound(int i) {
        if (NPCHandler.npcs[i] == null) return 0;
        int npcType = NPCHandler.npcs[i].npcType;
        String name = World.getWorld().npcHandler.getNpcListName(npcType);
        if(name.equalsIgnoreCase("skeleton"))
        	return SOUND_LIST.SKELETON_HISS.getSound();
        if(name.equalsIgnoreCase("zombie"))
        	return SOUND_LIST.ZOMBIE_HIT.getSound();
        
        switch(npcType) {
            case 3269:
                return SOUND_LIST.BLOCK_SHIELD.getSound();

            case 1173: case 1174: // Chickens
                return SOUND_LIST.CHICKEN_HIT.getSound();

            case 3029: case 3030: case 3031: case 3032: case 3033: // Goblins
                return SOUND_LIST.GOBLIN_HIT.getSound();

            case 2790: case 2791: case 2792: case 2793: // Cows
            case 5087:
                return SOUND_LIST.COW_HIT.getSound();
            case 7248:
            case 7247:
            case 7242: // Demons
            case 7243:
            case 7244:
            case 7245:
            case 7246:
            	return SOUND_LIST.DEMON_HIT.getSound();
            case 7241: // Abyssal Demon
                return SOUND_LIST.ABYSSAL_DEMON_BLOCK.getSound();
            case 7253:
            case 7254:
    		case 7273:
    		case 7274:
    		case 7275:
                return SOUND_LIST.DRAGON_HIT.getSound();
            case 7277:
            	return 550;
            case 2090: case 2091: case 2092: case 2093: // Giants
                return SOUND_LIST.GIANT_HIT.getSound();

            case 7263: // Ghosts
                return SOUND_LIST.GHOST_HIT.getSound();

            case 10: case 11: case 12: // Rats
                return SOUND_LIST.RAT_HIT.getSound();

            case 101: case 102: // Rock Crabs
                return SOUND_LIST.ROCK_CRAB_HIT.getSound();

            case 7249:
            	return SOUND_LIST.DUST_DEVIL_HIT.getSound();
            default:
                return SOUND_LIST.HUMAN_HIT.getSound(); // Generic 511/513
        }
    }

    public static int handleDeathSound(int i) {
        if (NPCHandler.npcs[i] == null) return 0;
        int npcType = NPCHandler.npcs[i].npcType;
        String name = World.getWorld().npcHandler.getNpcListName(npcType);
        if(name.equalsIgnoreCase("skeleton"))
        	return SOUND_LIST.SKELETON_DEATH.getSound();
        if(name.equalsIgnoreCase("zombie"))
        	return SOUND_LIST.ZOMBIE_DEATH.getSound();
        switch(npcType) {
            case 1173: case 1174: // Chickens
                return SOUND_LIST.CHICKEN_DEATH.getSound();

            case 3029: case 3030: case 3031: case 3032: case 3033: case 3034: // Goblins
                return SOUND_LIST.GOBLIN_DEATH.getSound();

            case 2790: case 2791: case 2792: case 2793: // Cows
                return SOUND_LIST.COW_DEATH.getSound();

            case 7248:
            case 7247:
            	case 7242: // Demons
                case 7243:
                case 7244:
                case 7245:
                case 7246:
            	return SOUND_LIST.DEMON_DEATH.getSound();
            case 7241:
                return SOUND_LIST.ABYSSAL_DEMON_DEATH.getSound();

            case 7253:
            case 7254:
    		case 7273:
    		case 7274:
    		case 7275:
                return SOUND_LIST.DRAGON_DEATH.getSound();
            case 7277:
            	return 549;
            case 2090: case 2091: case 2092: case 2093: // Giants
                return SOUND_LIST.GIANT_DEATH.getSound();

            case 7263: // Ghosts
                return SOUND_LIST.GHOST_DEATH.getSound();

            case 10: case 11: case 12: // Rats
                return SOUND_LIST.RAT_DEATH.getSound();

            case 101: case 102: // Rock Crabs
                return SOUND_LIST.ROCK_CRAB_DEATH.getSound();

            case 7249:
            	return SOUND_LIST.DUST_DEVIL_DEATH.getSound();
            default:
                return SOUND_LIST.HUMAN_DEATH.getSound(); // Generic 512
        }
    }

    public static int handleAttackSound(int i) {
        if (NPCHandler.npcs[i] == null) return 0;
        int npcType = NPCHandler.npcs[i].npcType;
        String name = World.getWorld().npcHandler.getNpcListName(npcType);
        if(name.equalsIgnoreCase("skeleton"))
        	return SOUND_LIST.SKELETON_ATTACK.getSound();
        if(name.equalsIgnoreCase("zombie"))
        	return SOUND_LIST.ZOMBIE_ATTACK.getSound();
        switch(npcType) {
        case 7249:
        	return SOUND_LIST.DUST_DEVIL_ATTACK.getSound();
            case 1173: case 1174: // Chickens
                return SOUND_LIST.CHICKEN_ATTACK.getSound();

            case 3269: case 3270: 
                return SOUND_LIST.STABSWORD_SLASH.getSound();

            case 3271: case 5418:
                return SOUND_LIST.BAXE_CRUSH.getSound();

            case 3029: case 3030: case 3031: case 3032: case 3033: case 3034: // Goblins
                return SOUND_LIST.GOBLIN_ATTACK.getSound();

            case 2790: case 2791: case 2792: case 2793: // Cows
                return SOUND_LIST.COW_ATTACK.getSound();

            case 5087: // Dark Wizard (Using Spell Cast Sound)
                return 120; 

            case 7277:
            	return 547;
            case 7248:
            case 7247:
            case 7242:
            case 7243:
            case 7244:
            case 7245:
            case 7246:
            	return SOUND_LIST.DEMON_ATTACK.getSound();
            case 7241:  // abyssal Demons
                return SOUND_LIST.ABYSSAL_DEMON_ATTACK.getSound();

            case 7253:
            case 7254:
    		case 7273:
    		case 7274:
    		case 7275:
                return SOUND_LIST.DRAGON_ATTACK.getSound();
            case 7263: // Ghosts
                return SOUND_LIST.GHOST_ATTACK.getSound();

            case 2090: case 2091: case 2092: case 2093: // Giants
                return SOUND_LIST.GIANT_ATTACK.getSound();

            case 10: case 11: case 12: // Rats
                return SOUND_LIST.RAT_ATTACK.getSound();

            case 101: case 102: // Rock Crabs
                return SOUND_LIST.ROCK_CRAB_ATTACK.getSound();

            default:
                return 417; // Generic attack swing
        }
    }
    /**
     * Gets the correct OSRS Equip Sound based on the item's name.
     * @param itemId The ID of the item being equipped.
     * @return The sound ID (2238=Soft, 2239=Metal, 2240=Weapon, 2241=Shield, 2242=Ammo)
     */
    public static int getEquipSound(int itemId) {
        String name = server.model.items.Item.getItemName(itemId).toLowerCase();

        // 1. WEAPONS
        if (name.contains("scimitar") || name.contains("sword") || name.contains("dagger") || 
                name.contains("mace") || name.contains("axe") || name.contains("maul") || 
                name.contains("bow") || name.contains("staff") || name.contains("wand") || 
                name.contains("claws") || name.contains("halberd") || 
                name.contains("spear") || name.contains("dart") || name.contains("knife") || 
                name.contains("javelin") || name.contains("blowpipe")) {
                return 2240;
            }
        if (name.contains("whip")) {
                    return 2249;
                }

        // 2. SHIELDS
        if (name.contains("shield") || name.contains("defender") || name.contains("book") || 
            name.contains("toktz-ket")) {
            return 2241;
        }

        // 3. AMMO (Arrows/Bolts)
        if (name.contains("arrow") || name.contains("bolt") || name.contains("rack")) {
            return 2242;
        }

        // 4. METAL ARMOR (Plate, Chain, Helms)
        // We check for "metal" words. If it's a "med helm" or "full helm" it's usually metal.
        if (name.contains("plate") || name.contains("chain") || name.contains("helm") || 
            name.contains("bronze") || name.contains("iron") || name.contains("steel") || 
            name.contains("mithril") || name.contains("adamant") || name.contains("rune") || 
            name.contains("dragon") || name.contains("barrows") || name.contains("torag") || 
            name.contains("dharok") || name.contains("guthan") || name.contains("verac")) {
            
            // Exception: Dragonhide is NOT metal
            if (name.contains("hide") || name.contains("leather")) {
                return 2238; 
            }
            return 2239;
        }

        // 5. DEFAULT / SOFT ARMOR (Robes, Leather, Capes, Gloves, Boots)
        // 2238 is the "Thump" sound for cloth/leather.
        return 2238; 
    }
}