package server.model.players;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

import server.model.minigames.cox.CoxButtonHandler;
import server.model.minigames.cox.RaidSession;
import server.model.minigames.raids.RaidParty;
import server.model.players.skills.*;
import server.model.players.skills.agility.impl.rooftop.*;
import server.util.Stopwatch;
import server.util.definitions.AnimationDefinition;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import com.google.common.collect.Queues;

import server.clip.WorldObject;
import server.clip.doors.DoorDefinition;
import server.clip.doors.DoorHandler;
import server.content.instances.InstancedAreaManager;
import server.net.Packet;
import server.net.Packet.Type;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import server.Config;
import server.model.content.achievement_diary.RechargeItems;
import server.model.items.CollectionLog;
import server.model.items.CollectionProgress;
import server.model.items.Item;
import server.model.npcs.NPC;
import server.model.npcs.NPCDeathTracker;
import server.model.Entity;
import server.model.HealthStatus;
import server.model.content.StashUnit;
import server.model.npcs.NPCHandler;
import server.model.npcs.bosses.cerberus.Cerberus;
import server.model.npcs.bosses.skotizo.Skotizo;
import server.model.npcs.bosses.zulrah.Zulrah;
import server.model.npcs.bosses.zulrah.ZulrahLostItems;
import server.model.npcs.drops.dropTables.GemRareDropTable;
import server.model.npcs.drops.dropTables.RareDropTable;
import server.model.npcs.drops.dropTables.SlayerRareDropTable;
import server.util.Misc;
import server.world.ApiClient;
import server.world.Boundary;
import server.world.Location;
import server.world.World;
import server.util.Buffer;
import server.util.ISAACCipher;
import server.model.players.combat.DamageQueueEvent;
import server.model.players.combat.Degrade;
import server.model.players.combat.Hitmark;
import server.model.players.combat.melee.QuickPrayers;
import server.model.players.content.BossLog;
import server.model.players.content.ItemsKeptOnDeath;
import server.model.players.content.Skillcapes.SkillCapes;
import server.model.players.content.Skillcapes.SkillcapePerks;
import server.model.players.content.Stronghold.StrongHold;
import server.model.players.packets.CITY_MUSIC;
import server.model.players.packets.GeValuesCache;
import server.model.players.packets.ItemCharge;
import server.model.players.packets.RegionMusic;
import server.model.players.packets.TeleTabs;
import server.model.players.packets.dialogue.DialogueHandler;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.packets.dialogue.npc.SilkMerchant;
import server.model.players.path.Direction;
import server.model.players.quests.QuestManager;
import server.model.players.skills.thieving.Thieving;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.agility.impl.BarbarianAgility;
import server.model.players.skills.agility.impl.GnomeAgility;
import server.model.players.skills.agility.impl.Lighthouse;
import server.model.players.skills.agility.impl.Shortcuts;
import server.model.players.skills.agility.impl.WildernessAgility;
import server.model.players.skills.farming.Allotments;
import server.model.players.skills.farming.Bushes;
import server.model.players.skills.farming.Compost;
import server.model.players.skills.farming.Farming;
import server.model.players.skills.farming.Flowers;
import server.model.players.skills.farming.FruitTree;
import server.model.players.skills.farming.Herbs;
import server.model.players.skills.farming.Hops;
import server.model.players.skills.farming.SpecialPlantOne;
import server.model.players.skills.farming.SpecialPlantTwo;
import server.model.players.skills.farming.ToolLeprechaun;
import server.model.players.skills.farming.WoodTrees;
import server.model.players.skills.mining.Mining;
import server.model.players.skills.mining.motherlode.OreStack;
import server.model.players.skills.mining.motherlode.OreTile;
import server.model.players.skills.slayer.Slayer;
import server.model.players.skills.smithing.Smithing;
import server.model.players.skills.smithing.SmithingInterface;
import server.model.players.skills.construction.*;
import server.model.players.skills.construction.util.HouseFurniture;
import server.model.players.skills.construction.util.Portal;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.skills.crafting.Crafting;
import server.model.shops.ShopAssistant;
import server.model.items.ItemAssistant;
import server.model.items.containers.RunePouch;
import server.content.barrows.Barrows;
import server.content.barrows.TunnelEvent;
import server.content.clans.Clan;
import server.model.minigames.rfd.DisposeTypes;
import server.model.minigames.warriors_guild.WarriorsGuild;
import server.model.items.containers.ArmourCase;
import server.model.items.containers.FancyDressBox;
import server.model.items.containers.FancyBox;
import server.model.items.containers.GemBag;
import server.model.items.containers.HerbSack;
import server.model.items.bank.Bank;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.Duel;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.multiplayer_session.trade.Trade;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.impl.RunEnergyEvent;
import server.event.impl.SkillRestorationEvent;
import server.event.Event;
import server.event.EventHandler;

@Slf4j
public class Player extends Entity {
	

	public int worldID = 2;
	
	/**
	 * Others
	 */
	public ArrayList<String> killedPlayers = new ArrayList<String>(), lastConnectedFrom = new ArrayList<String>();
	public ArrayList<Integer> attackedPlayers = new ArrayList<Integer>();

	public long lastButton, clickDelay = 0, lastMarkDropped;

	public ArrayList<String> lastKilledPlayers = new ArrayList<String>();

	public long lastCast = 0;
    @Getter
    public long lastDragonfireShieldAttack;

	public int[][] playerSkillProp = new int[20][15];
	public boolean[] playerSkilling = new boolean[20];
	public boolean stopPlayerSkill;
	public int doAmount;
	public boolean playerFletch;

	public long lastSilkTheftMs;
    // TODO Auto-generated method stub
    @Setter
    @Getter
    public int toxicStaffOfTheDeadCharge;
	public boolean initialized = false;
    public boolean disconnected = false;
    public boolean ruleAgreeButton = false;
    public boolean RebuildNPCList = false;
    public boolean isActive = false;
    public boolean isKicked = false;
    public boolean isSkulled = false;
    public boolean friendUpdate = false;
    public boolean newPlayer = false;
    public boolean hasMultiSign = false;
    public boolean saveCharacter = false;
    public boolean mouseButton = false;
    public boolean splitChat = false;
    public boolean chatEffects = true;
    public boolean isNpc = false;
    public boolean acceptAid = false;
    public boolean nextDialogue = false;
    public boolean autocasting = false;
    public boolean usedSpecial = false;
    public boolean mageFollow = false;
    public boolean dbowSpec = false;
    public boolean craftingLeather = false;
    public boolean properLogout = false;
    public boolean secDbow = false;
    public boolean maxNextHit = false;
    public boolean ssSpec = false;
    public boolean vengOn = false;
    public boolean canWalkTutorial;
    public boolean closeTutorialInterface;
    public boolean addStarter = false;
    public boolean accountFlagged = false;
    public boolean msbSpec = false;
    public boolean stopPlayerPacket;
    public boolean stopPlayer;
    public boolean wildernessWarning;
    public boolean isWoodcutting;
    public boolean playerIsFiremaking;
    public boolean isFiremaking;
    public boolean playerIsFletching;
    public boolean diedOnTut;
    public boolean playerIsCooking;
    public boolean isFletching;
    public boolean isSmithing;
    public boolean miningRock;
    public boolean isSmelting;
    public boolean isMining;
    public boolean ratdied2;
    @Getter
    public boolean dragonfireShieldActive;

	public int

	saveDelay;
    public int playerKilled;
    public int pkPoints;
    public int totalPlayerDamageDealt;
    public int killedBy;
    @Getter
    public int dragonfireShieldCharge;
    public int lastChatId = 1;
    public int privateChat;
    public int friendSlot = 0;
    public int dialogueId;
    public int randomCoffin;
    public int newLocation;
    public int specEffect;
    public int specBarId;
    public int attackLevelReq;
    public int defenceLevelReq;
    public int strengthLevelReq;
    public int rangeLevelReq;
    public int magicLevelReq;
    public int npcId2;
    public int followId;
    public int skullTimer;
    public int votingPoints;
    public int tutorialProgress;
    public int cookStage1 = 1;
    public int nextChat = 0;
    public int talkingNpc = -1;
    public int dialogueAction = 0;
    public int autocastId;
    public int followDistance;
    public int followId2;
    public int barrageCount = 0;
    public int delayedDamage = 0;
    public int delayedDamage2 = 0;
    public int pcPoints = 0;
    public int magePoints = 0;
    public int lastArrowUsed = -1;
    public int clanId = -1;
    public int autoRet = 0;
    public int pcDamage = 0;
    public int xInterfaceId = 0;
    public int xRemoveId = 0;
    public int xRemoveSlot = 0;
    public int tzhaarToKill = 0;
    public int tzhaarKilled = 0;
    public int waveId;
    public int frozenBy = 0;
    public int poisonDamage = 0;
    public int teleAction = 0;
    public int bonusAttack = 0;
    public int lastNpcAttacked = 0;
    public int killCount = 0;
    public int saraKC = 0;
    public int armadylKC = 0;
    public int bandosKC = 0;
    public int zammyKC = 0;
    public int treeX;
    public int treeY;
    public int rockX;
    public int rockY;
    public int smeltingItem;
    public int cookingItem;
    public int cookingObject;
    public int cookingObjectX;
    public int cookingObjectY;
    public int characterSummaryTab;
    public int currentSong;
    public int bookPage;
    public int hitCount;
    public int runEnergy = 100;
	
	
	public String clanName, properName;
	
    public int[] questStages = new int[100];
    public int woolHandedIn = 0;
    
    public boolean rumInCrate = false;
    public int bananasInCrate = 0;
    public int bananasPickedDiary = 0;
    public boolean gardenerSpawned = false;
    
    public int questPoints;
    public int QuestsCompleted;
    
    public int EleWorkshopWater_stage;
    
    /*
     * Archery Target minigame
     */
    public int archeryGuildScore = 0;
    public int archeryGuildShots = 0;
    
	public int[] voidStatus = new int[5];
	
	public boolean[] killedPheasant = new boolean[5];
	public boolean playerHasRandomEvent;
	public boolean canLeaveArea;
	public int pieSelect = 0, getPheasent, kebabSelect = 0, breadID, chocSelect = 0, bagelSelect = 0,
			triangleSandwich = 0, squareSandwich = 0, breadSelect = 0;
	
	public int miningAxe = -1, woodcuttingAxe = -1;
	
	public int[] itemKeptId = new int[4];
	
	public int[] pouches = new int[4];
	public final int[] POUCH_SIZE = { 3, 6, 9, 12 };
	
	public int[] inextinguishable_lightSources = {4550, 4700, 9064};
	public int[] lightSources = { 32, 33, 4531, 4534, 594, 20720, 4539};
	
	public boolean[] invSlot = new boolean[28], equipSlot = new boolean[14];
	
	public long friends[] = new long[200];
	
	public double specAmount = 0;
	public double specAccuracy = 1;
	public double specDamage = 1;
	public double prayerPoint = 1.0;
	
	public boolean hasNpc = false;
	/*
	 * clues
	 */
	public int beginnerClueStepsCompleted = 0;
	public int beginnerClueStepsTotal = 0;
	public int currentBeginnerClueStep = -1;
	
	public int charlieClueTask = 0;
	public int easyClueStepsCompleted = 0;
	public int easyClueStepsTotal = 0;

	public int mediumClueStepsCompleted = 0;
	public int mediumClueStepsTotal = 0;

	public int hardClueStepsCompleted = 0;
	public int hardClueStepsTotal = 0;

	public int eliteClueStepsCompleted = 0;
	public int eliteClueStepsTotal = 0;

	public int masterClueStepsCompleted = 0;
	public int masterClueStepsTotal = 0;

    public int[] currentPuzzle = new int[25];

	public int currentPuzzleId;
    public Stack<Integer> puzzleSolutionSteps = new Stack<>();
    public boolean killedClueWizard = false;
	public boolean[] stashBuilt = new boolean[StashUnit.values().length];
    public boolean[] stashFilled = new boolean[StashUnit.values().length];
	public int summonId;
	public int PouchRune1, PouchRune2, PouchRune3, PouchRune1Slot, PouchRune2Slot, PouchRune3Slot, PouchRune1Amt,
			PouchRune2Amt, PouchRune3Amt;
	public String runeTypes1, runeTypes, runeTypes2;
	public boolean addingToRP = false;
	public boolean openLootBag = false, openGembag = false;
	public boolean oneClickDeposit = false;

	public boolean stopFiremaking, pickedUpFiremakingLog, logLit;
	public int teleGrabItem, teleGrabX, teleGrabY, duelCount, underAttackBy, underAttackBy2, wildLevel, teleTimer,
			respawnTimer, saveTimer = 0, teleBlockLength, poisonDelay;
	public int poisonImmunity;
	public int remainingMinutes;
	public int remainingSeconds;
	public int DestroyID;
	public boolean onPlaylist = false;
	public long lastUpdateTime;
	public long VengTimer;
	public String timerString;
	public long remainingTime;
	public long lastPoison, lastPoisonSip, poisonImmune, lastSpear, lastProtItem, dfsDelay, lastVeng,
			lastYell, teleGrabDelay, protMageDelay, protMeleeDelay, protRangeDelay, lastAction, lastThieve,
			lastLockPick, alchDelay, duelDelay, teleBlockDelay, godSpellDelay,
			reduceStat, restoreStatsDelay, logoutDelay, buryDelay, foodDelay,
			potDelay, lastSkillingAction;
	public int specRestoreTicks;
	public long singleCombatDelay = System.currentTimeMillis(),singleCombatDelay2 = System.currentTimeMillis(), lastPlayerMove = System.currentTimeMillis();
	public boolean canChangeAppearance = false;
	public boolean mageAllowed;
	public byte poisonMask = 0;
	public final int[] BOWS = { 9185, 839, 845, 847, 851, 855, 859, 841, 843, 849, 853, 857, 861, 4212, 4214, 4215,
			11235, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 6724, 4734, 4934, 4935, 4936, 4937 };
	public final int[] ARROWS = { 882, 884, 886, 888, 890, 892, 4740, 11212, 9140, 9141, 4142, 9143, 9144, 9240, 9241,
			9242, 9243, 9244, 9245 };
	public final int[] NO_ARROW_DROP = { 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 4734, 4934,
			4935, 4936, 4937 };
	public final int[] OTHER_RANGE_WEAPONS = { 863, 864, 865, 866, 867, 868, 869, 806, 807, 808, 809, 810, 811, 825,
			826, 827, 828, 829, 830, 800, 801, 802, 803, 804, 805, 6522 };
	public int favoriteWorld1;
	public int favoriteWorld2;
	public boolean beesDocile;
	public boolean hasAskedForBlackCandle;
	public boolean isHardcoreIronman;
	public boolean isUltimateIronman;
	public boolean isIronman;

	public boolean isAutoButton(int button) {
		for (int j = 0; j < autocastIds.length; j += 2) {
			if (autocastIds[j] == button)
				return true;
		}
		return false;
	}

	private final QuickPrayers quick = new QuickPrayers();

	public QuickPrayers getQuick() {
		return quick;
	}

	@Override
	public int getSize() {
		return 1;
	}

	public Channel getSession() {
		return c.session;
	}

	public int[] autocastIds = { 51133, 32, 51185, 33, 51091, 34, 24018, 35, 51159, 36, 51211, 37, 51111, 38, 51069, 39,
			51146, 40, 51198, 41, 51102, 42, 51058, 43, 51172, 44, 51224, 45, 51122, 46, 51080, 47, 7038, 0, 7039, 1,
			7040, 2, 7041, 3, 7042, 4, 7043, 5, 7044, 6, 7045, 7, 7046, 8, 7047, 9, 7048, 10, 7049, 11, 7050, 12, 7051,
			13, 7052, 14, 7053, 15, 47019, 27, 47020, 25, 47021, 12, 47022, 13, 47023, 14, 47024, 15 };

	public String spellName = "Select Spell";
	public void assignAutocast(int button) {
		for (int j = 0; j < autocastIds.length; j++) {
			if (autocastIds[j] == button) {
				Player c = (Player) PlayerHandler.players[this.getIndex()];
				autocasting = true;
				autocastId = autocastIds[j + 1];
				c.getPA().sendFrame36(108, 1);
				c.setSidebarInterface(0, 328);
				spellName = getSpellName(autocastId);
				//spellName = spellName;
				c.getPA().sendFrame126(spellName, 354);
				c = null;
				break;
			}
		}
	}

	public String getSpellName(int id) {
		switch (id) {
		case 0:
			return "Air Strike";
		case 1:
			return "Water Strike";
		case 2:
			return "Earth Strike";
		case 3:
			return "Fire Strike";
		case 4:
			return "Air Bolt";
		case 5:
			return "Water Bolt";
		case 6:
			return "Earth Bolt";
		case 7:
			return "Fire Bolt";
		case 8:
			return "Air Blast";
		case 9:
			return "Water Blast";
		case 10:
			return "Earth Blast";
		case 11:
			return "Fire Blast";
		case 12:
			return "Air Wave";
		case 13:
			return "Water Wave";
		case 14:
			return "Earth Wave";
		case 15:
			return "Fire Wave";
		case 32:
			return "Shadow Rush";
		case 33:
			return "Smoke Rush";
		case 34:
			return "Blood Rush";
		case 35:
			return "Ice Rush";
		case 36:
			return "Shadow Burst";
		case 37:
			return "Smoke Burst";
		case 38:
			return "Blood Burst";
		case 39:
			return "Ice Burst";
		case 40:
			return "Shadow Blitz";
		case 41:
			return "Smoke Blitz";
		case 42:
			return "Blood Blitz";
		case 43:
			return "Ice Blitz";
		case 44:
			return "Shadow Barrage";
		case 45:
			return "Smoke Barrage";
		case 46:
			return "Blood Barrage";
		case 47:
			return "Ice Barrage";
		default:
			return "Select Spell";
		}
	}



	public int bankItems[] = new int[Config.BANK_SIZE];
	public int bankItemsN[] = new int[Config.BANK_SIZE];

	public int bankingTab = 0;// -1 = bank closed

	public String searchTerm = "";

	public boolean fullVoidRange() {
		return playerEquipment[playerHat] == 11664 && playerEquipment[playerLegs] == 8840
				&& playerEquipment[playerChest] == 8839 && playerEquipment[playerHands] == 8842;
	}

	public boolean fullVoidMage() {
		return playerEquipment[playerHat] == 11663 && playerEquipment[playerLegs] == 8840
				&& playerEquipment[playerChest] == 8839 && playerEquipment[playerHands] == 8842;
	}

	public boolean fullVoidMelee() {
		return playerEquipment[playerHat] == 11665 && playerEquipment[playerLegs] == 8840
				&& playerEquipment[playerChest] == 8839 && playerEquipment[playerHands] == 8842;
	}

	public int[][] barrowsNpcs = { { 2030, 0 }, // verac
			{ 2029, 0 }, // toarg
			{ 2028, 0 }, // karil
			{ 2027, 0 }, // guthan
			{ 2026, 0 }, // dharok
			{ 2025, 0 } // ahrim
	};
	public int[] barrowsMonsterNpcs = { 1678, 1679, 1680, 1681, 1682, 1683, 1684, 1685, 1686, 1687, 1688 };

	public int BarrowsRewardPotential;
	public int reduceSpellId;
	public final int[] REDUCE_SPELL_TIME = { 250000, 250000, 250000, 500000, 500000, 500000 }; // how long does the
																								// other player stay
																								// immune to
																								// the spell
	public long[] reduceSpellDelay = new long[6];
	public final int[] REDUCE_SPELLS = { 1153, 1157, 1161, 1542, 1543, 1562 };
	public boolean[] canUseReducingSpell = { true, true, true, true, true, true };

	public int slayerTask, taskAmount, slayerPoints;
	public boolean[] extend = new boolean[20];
	public int[] BlockID = new int[20];
	public boolean[] alreadyBlocked = new boolean[20];
	public int prayerId = -1;
	public int headIcon = -1;
	public int bountyIcon = 0;

	public long divineBoostEndTime = 0;
	public boolean antiFirePot = false;
	public int lastAntifirePotion;
	public int antifireDelay;
	public long lastSuperAntifirePotion;
	public long SuperantifireDelay;

	public long stopPrayerDelay, prayerDelay;
	public boolean usingPrayer;
	public final int[] PRAYER_LEVEL_REQUIRED = { 1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 37, 40, 43,
			44, 45, 46, 49, 52, 55, 60, 70, 74, 77};
	public final int[] PRAYER = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 
			24, 25, 26, 27, 28 };
	public final String[] PRAYER_NAME = { "Thick Skin", "Burst of Strength", "Clarity of Thought", "Sharp Eye",
			"Mystic Will", "Rock Skin", "Superhuman Strength", "Improved Reflexes", "Rapid Restore", "Rapid Heal",
			"Protect Item", "Hawk Eye", "Mystic Lore", "Steel Skin", "Ultimate Strength", "Incredible Reflexes",
			"Protect from Magic", "Protect from Missiles", "Protect from Melee", "Eagle Eye", "Mystic Might",
			"Retribution","Preserve", "Redemption", "Smite", "Chivalry", "Piety","Rigour", "Augury" };
	public final int[] PRAYER_GLOW = { 83, 84, 85, 601, 602, 86, 87, 88, 89, 90, 91, 603, 604, 92, 93, 94, 95, 96, 97,
			605, 606, 98, 99, 100, 830, 607, 608, 832, 831 };
	public final int[] PRAYER_HEAD_ICONS = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0,
			-1, -1, 3, 5, 4, -1, -1, -1, -1, -1 };

	public boolean[] prayerActive = { 
			false, false, false, false, false, 
			false, false, false, false, false, 
			false, false, false, false, false, 
			false, false, false, false, false, 
			false, false, false, false, false, 
			false, false, false, false };

	public int duelTimer, duelTeleX, duelTeleY, duelSlot, duelSpaceReq, duelOption, duelingWith, duelStatus;
	public int headIconPk = -1, headIconHints;
	public boolean duelRequested;
	public boolean[] duelRule = new boolean[22];
	public final int[] DUEL_RULE_ID = { 1, 2, 16, 32, 64, 128, 256, 512, 1024, 4096, 8192, 16384, 32768, 65536, 131072,
			262144, 524288, 2097152, 8388608, 16777216, 67108864, 134217728 };

	public boolean doubleHit, usingSpecial, npcDroppingItems, usingRangeWeapon, usingBow, usingMagic, castingMagic;
	public int specMaxHitIncrease, freezeDelay, freezeTimer = -6, killerId, playerIndex, oldPlayerIndex, lastWeaponUsed,
			projectileStage, crystalBowArrowCount, playerMagicBook, teleGfx, teleEndGfx, teleEndAnimation, teleHeight, teleX, teleY,
			rangeItemUsed, killingNpcIndex, totalDamageDealt, oldNpcIndex, fightMode, attackTimer, npcIndex,
			npcClickIndex, npcType, castingSpellId, oldSpellId, spellId, hitDelay;
	public boolean magicFailed, oldMagicFailed;
	public int bowSpecShot, clickNpcType, clickObjectType, objectId, objectX, objectY, objectXOffset, objectYOffset,
			objectDistance;
	public int pItemX, pItemY, pItemId;
	public boolean isMoving, walkingToItem;
	public boolean isShopping, updateShop;
	public int myShopId;
	public int tradeStatus, tradeWith;
	public boolean forcedChatUpdateRequired, inDuel, tradeAccepted, goodTrade, inTrade, tradeRequested,
			tradeResetNeeded, tradeConfirmed, tradeConfirmed2, canOffer, acceptTrade, acceptedTrade;
	public int attackAnim, animationRequest = -1, animationWaitCycles;
	public int[] playerBonus = new int[17];
	public boolean isRunning2 = true;
	public boolean takeAsNote;
	@Getter
	public int combatLevel;
	public boolean saveFile = false;
	public boolean toggle = false;
	public int playerAppearance[] = new int[13];
	public int apset;
	public int actionID;
	public int wearItemTimer, wearId, wearSlot, interfaceId;
	public int XremoveSlot, XinterfaceID, XremoveID, Xamount;
	public int respawnLocation = 0;
	public boolean attackOptions = true;
	/* Achievement diary */
	public boolean usingCloak = false;
	public int ArdyDiaryEasy = 0, ArdyDiaryMed = 0, ArdyDiaryHard = 0, ArdyDiaryElite = 0;
	/*					*/
	//
	// area 0-11, tier 0-3, task 0-15
	public boolean[][][] achievementCompleted = new boolean[12][4][20];
	// hunter
	public boolean success, success2, success3, success4, success5 = false;
	public int implingCatches = 0;
	/* Music Areas */
	public boolean MusicOn = false;
	public boolean RegionMusicOn = true;
	public int lastSong = -1;
	public boolean MeleeTutorClaimed = false, MagicTutorClaimed = false, RangedTutorClaimed = false;
	public long LastRangedTutorClaimed = 0, LastMagicTutorClaimed = 0, LastMeleeTutorClaimed = 0;
	/** Zulrah Item charges **/
	public int BlowpipeCharges = 0, BlowpipeDarts = 0, DartType = 0, SerpHelmCharges = 0, ToxicStaffCharges = 0,
			TridentCharges = 0, ArcLightCharges = 0;
	public boolean addingCharges = false, addingDarts = false;
	/***********************/
	public boolean tempBoolean;
	public boolean GodwarsRope = false;
	public int[] price = new int[28];
	public int[] priceN = new int[28];
	public int total, playerBankPin, recoveryDelay = 3, attemptsRemaining = 3, lastPinSettings = -1, setPinDate = -1,
			changePinDate = -1, deletePinDate = -1, firstPin, secondPin, thirdPin, fourthPin, bankPin1, bankPin2,
			bankPin3, bankPin4, pinDeleteDateRequested;
	public boolean isChecking;
	public boolean hasBankPin, enterdBankpin, firstPinEnter, requestPinDelete, secondPinEnter, thirdPinEnter,
			fourthPinEnter, hasBankpin, isBanking;
	public int tutorial = 15;
	public boolean usingGlory = false;
	public int[] mining = new int[3];
	public int miningTimer = 0;
	public int smeltType; // 1 = bronze, 2 = iron, 3 = steel, 4 = gold, 5 =
							// mith, 6 = addy, 7 = rune
	public int smeltAmount;
	public int smeltTimer = 0;
	public boolean smeltInterface;
	public boolean patchCleared;
	public int[] farm = new int[2];
	public int TrawlerStatus = 0;
	public boolean TrawlerInPort = true;
	public int homeTele = 0;
	public int homeTeleX;
	public int homeTeleY;
	public int homeTeleH;
	public int homeTeleWaitTimer = 0;
	public long LasthomeTele = 0;
	/**
	 * Castle Wars
	 */
	public int castleWarsTeam;
	public boolean inCwGame;
	public boolean inCwWait;
	public boolean sameInstance(Player player) {
	    return true;
	}
	/**
	 * Fight Pits
	 */
	public boolean inPits = false;
	public int pitsStatus = 0;
	/**
	 * SouthWest, NorthEast, SouthWest, NorthEast
	 */

	public boolean isInTut() {
        return getX() >= 2625 && getX() <= 2687 && getY() >= 4670 && getY() <= 4735;
    }

	public boolean inBarrows() {
        return getX() > 3520 && getX() < 3598 && getY() > 9653 && getY() < 9750;
    }

	public boolean inArea(int x, int y, int x1, int y1) {
        return getX() > x && getX() < x1 && getY() < y && getY() > y1;
    }

	public boolean inWild() {
		// if (inCw()) {
		// return true;
		// }
		if ((getX() > 2941 && getX() < 3392 && getY() > 3518 && getY() < 3966
				|| getX() > 2941 && getX() < 3392 && getY() > 9918 && getY() < 10366) && !Boundary.isIn(this, Boundary.FEROX_ENCLAVE)) {
			if (!wildernessWarning) {
				resetWalkingQueue();
				wildernessWarning = true;
				c.getPA().sendFrame126("WARNING!", 6940);
				c.getPA().showInterface(1908);
			}
			return true;
		} else if (Boundary.isIn(c, Boundary.FEROX_ENCLAVE)) {
			return false;
		}
		return false;
	}

	public boolean arenas() {
		if (getX() > 3331 && getX() < 3391 && getY() > 3242 && getY() < 3260) {
			return true;
		}
		return false;
	}

	public int getArcLightCharge() {
		return ArcLightCharges;
	}

	public void setArcLightCharge(int chargeArc) {
		this.ArcLightCharges = chargeArc;
	}

	public int getToxicBlowpipeCharge() {
		return BlowpipeCharges;
	}

	public void setToxicBlowpipeCharge(int charge) {
		this.BlowpipeCharges = charge;
	}

	public int getToxicBlowpipeAmmo() {
		return DartType;
	}

	public int getToxicBlowpipeAmmoAmount() {
		return BlowpipeDarts;
	}

	public void setToxicBlowpipeAmmoAmount(int amount) {
		this.BlowpipeDarts = amount;
	}

	public void setToxicBlowpipeAmmo(int ammo) {
		this.DartType = ammo;
	}

	public int getSerpentineHelmCharge() {
		return this.SerpHelmCharges;
	}

	public void setSerpentineHelmCharge(int charge) {
		this.SerpHelmCharges = charge;
	}

	public int getTridentCharge() {
		return TridentCharges;
	}

	public void setTridentCharge(int tridentCharge) {
		this.TridentCharges = tridentCharge;
	}

	public int getToxicTridentCharge() {
		return TridentCharges;
	}

	public void setToxicTridentCharge(int toxicTridentCharge) {
		this.TridentCharges = toxicTridentCharge;
	}

	public boolean inDuelArena() {
		if ((getX() > 3322 && getX() < 3394 && getY() > 3195 && getY() < 3291)
				|| (getX() > 3311 && getX() < 3323 && getY() > 3223 && getY() < 3248)) {
			return true;
		}
		return false;
	}

	public boolean inMulti() {
		if ((getX() >= 3136 && getX() <= 3327 && getY() >= 3519 && getY() <= 3607)
				|| (getX() >= 3190 && getX() <= 3327 && getY() >= 3648 && getY() <= 3839)
				|| (getX() >= 3200 && getX() <= 3390 && getY() >= 3840 && getY() <= 3967)
				|| (getX() >= 2992 && getX() <= 3007 && getY() >= 3912 && getY() <= 3967)
				|| (getX() >= 2946 && getX() <= 2959 && getY() >= 3816 && getY() <= 3831)
				|| (getX() >= 3008 && getX() <= 3199 && getY() >= 3856 && getY() <= 3903)
				|| (getX() >= 3008 && getX() <= 3071 && getY() >= 3600 && getY() <= 3711)
				|| (getX() >= 3072 && getX() <= 3327 && getY() >= 3608 && getY() <= 3647)
				|| (getX() >= 2624 && getX() <= 2690 && getY() >= 2550 && getY() <= 2619)
				|| (getX() >= 2371 && getX() <= 2422 && getY() >= 5062 && getY() <= 5117)
				|| (getX() >= 2896 && getX() <= 2927 && getY() >= 3595 && getY() <= 3630)
				|| (getX() >= 2892 && getX() <= 2932 && getY() >= 4435 && getY() <= 4464)
				|| (getX() >= 2256 && getX() <= 2287 && getY() >= 4680 && getY() <= 4711)
				|| (getX() >= 2862 && getX() <= 2876 && getY() >= 5351 && getY() <= 5369)
				|| (getX() >= 2918 && getX() <= 2936 && getY() <= 5331 && getY() >= 5318)
				|| (getX() >= 2842 && getX() <= 2889 && getY() <= 5296 && getY() >= 5258)
				|| (getX() >= 2907 && getX() <= 2889 && getY() <= 5276 && getY() >= 5258)
				|| (getX() <= 2943 && getX() >= 2815 && getY() <= 5375 && getY() >= 5246)
				|| (getX() <= 2959 && getX() >= 2914 && getY() <= 4406 && getY() >= 4357)
				|| (getX() <= 2434 && getX() >= 2365 && getY() <= 3138 && getY() >= 3069) || inBandosGWD()
				|| Boundary.isIn(this, Boundary.CATACOMBS) || Boundary.isIn(this, Boundary.ZEAH_BOUNDARY)) {
			return true;
		}
		return false;
		//if (Boundary.isIn(this, Barrows.TUNNEL)) {
	}

	public boolean inZammyGWD() {
		if (getX() <= 2918 && getX() >= 2936 && getY() >= 5331 && getY() <= 5318) {
			return true;
		}
		return false;
	};

	public boolean inArmadylGWD() {
		if (getX() <= 2842 && getX() >= 2824 && getY() >= 5296 && getY() <= 5308) {
			return true;
		}
		return false;
	}

	public boolean inBandosGWD() {
		if (getX() >= 2864 && getX() <= 2876 && getY() >= 5351 && getY() <= 5369) {
			return true;
		}
		return false;
	}

	public boolean inSaraGWD() {
		if (getX() <= 2907 && getX() >= 2889 && getY() <= 5276 && getY() >= 5258) {
			return true;
		}
		return false;
	}

	public boolean inGodWars() {
		if (getX() <= 2953 && getX() >= 2816 && getY() <= 5368 && getY() >= 5248) {
			return true;
		}
		return false;
	}

	public boolean inFightCaves() {
		return getX() >= 2360 && getX() <= 2445 && getY() >= 5045 && getY() <= 5125;
	}

	public boolean inPirateHouse() {
		return getX() >= 3038 && getX() <= 3044 && getY() >= 3949 && getY() <= 3959;
	}

	public String connectedFrom = "";
	public String globalMessage = "";
    @Getter
    private PlayerAction playerAction = new PlayerAction(this);
	
	// Inside Player.java or Friends.java
	@Getter
    private List<String> clanSetupList = new ArrayList<>();

    public String[] lastSentSongNames = new String[373];
	public int[] lastSentSongConfigs = new int[373];
	public int lastSentSongCount = -1;
	public void initialize() {
		try {
			// 1. Setup the core engine stats
			AccountLogin.setupPlayerEnvironment(this);

			// 2. THIS MUST STAY HERE! Updates the player model to the client
			World.getWorld().getPlayerHandler().updatePlayer(this, outStream);
			World.getWorld().getPlayerHandler().updateNPC(this, outStream);
			flushOutStream();
			getMovementQueue().handleRegionChange();

			// 3. Move them if they are in an illegal zone
			AccountLogin.enforceLoginBoundaryCheck(this);
			checkCoXLogin();
			// 4. Load all the UI and Equipment data
			AccountLogin.loadInterfacesAndGameData(this);
			AccountLogin.loadEquipment(this);

			// 5. Finalize the login (This fixes your Rune Pouch bug!)
			AccountLogin.finalizeLogin(this);

		} catch (Exception e) {
			log.warn("Error while initializing player " + this.getName(), e);
		}
	}

	public void addEvents() {
		World.getWorld().getEventHandler().submit(new SkillRestorationEvent(this));
		World.getWorld().getEventHandler().submit(new RunEnergyEvent(this, 1));
		CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.PLAYER_COMBAT_DAMAGE, this, damageQueue, 1, true);
	}

	@Getter
	private SkillExperience skills = new SkillExperience(this);

	public int getPrivateChat() {
		return privateChat;
	}

	public Friends getFriends() {
		return friend;
	}

	public Ignores getIgnores() {
		return ignores;
	}

	private Friends friend = new Friends(this);
	private Ignores ignores = new Ignores(this);

	public void setPrivateChat(int option) {
		this.privateChat = option;
	}

	public void loadSlayerInterfaceText() {
		if (c.slayerTask > 0)
			c.getPA().sendFrame126(World.getWorld().npcHandler.getNpcListName(c.slayerTask) + "  x" + c.taskAmount, 48809);
		else
			c.getPA().sendFrame126("You have no task to block", 48809);
		for (int i = 0; i < 6; i++)
			c.getPA().sendFrame126(World.getWorld().npcHandler.getNpcListName(c.BlockID[i]), 48818 + i);
		if (c.slayerPoints >= 50) {
			c.getPA().sendFrame126("will be a bigger task @gre@(50 points)", 57718);
		}
		if (c.slayerPoints >= 75) {
			c.getPA().sendFrame126("be a bigger task @gre@(75 points)", 57732);
		}
		if (c.slayerPoints >= 120) {
			c.getPA().sendFrame126("will be a bigger task @gre@(120 points)", 57722);
		}
		if (c.slayerPoints >= 100) {
			c.getPA().sendFrame126("will be a bigger task @gre@(100 points)", 57712);
			c.getPA().sendFrame126("a bigger task @gre@(100 points)", 57714);
			c.getPA().sendFrame126("a bigger task @gre@(100 points)", 57716);
			c.getPA().sendFrame126("Dragon task, it will be bigger. @gre@(100 points)", 57720);
			c.getPA().sendFrame126("task, it will a bigger task @gre@(100 points)", 57724);
			c.getPA().sendFrame126("be a bigger task @gre@(100 points)", 57726);
			c.getPA().sendFrame126("it will be a bigger task @gre@(100 points)", 57728);
			c.getPA().sendFrame126("will be a bigger task @gre@(100 points)", 57730);
			c.getPA().sendFrame126("task, it will be a bigger task @gre@(100 points)", 57734);
			c.getPA().sendFrame126("will be a bigger task @gre@(100 points)", 57736);
			c.getPA().sendFrame126("a bigger task @gre@(100 points)", 57738);
			c.getPA().sendFrame126("it will be a bigger task @gre@(100 points)", 57740);
			c.getPA().sendFrame126("be a bigger task @gre@(100 points)", 57742);
			c.getPA().sendFrame126("be a bigger task @gre@(100 points)", 57744);
			c.getPA().sendFrame126("it will be a bigger task @gre@(100 points)", 57746);
		} else if (c.slayerPoints < 100) {
			c.getPA().sendFrame126("will be a bigger task @red@(100 points)", 57712);
			c.getPA().sendFrame126("a bigger task @red@(100 points)", 57714);
			c.getPA().sendFrame126("a bigger task @red@(100 points)", 57716);
			c.getPA().sendFrame126("Dragon task, it will be bigger. @red@(100 points)", 57720);
			c.getPA().sendFrame126("task, it will a bigger task @red@(100 points)", 57724);
			c.getPA().sendFrame126("be a bigger task @red@(100 points)", 57726);
			c.getPA().sendFrame126("it will be a bigger task @red@(100 points)", 57728);
			c.getPA().sendFrame126("will be a bigger task @red@(100 points)", 57730);
			c.getPA().sendFrame126("task, it will be a bigger task @red@(100 points)", 57734);
			c.getPA().sendFrame126("will be a bigger task @red@(100 points)", 57736);
			c.getPA().sendFrame126("a bigger task @red@(100 points)", 57738);
			c.getPA().sendFrame126("it will be a bigger task @red@(100 points)", 57740);
			c.getPA().sendFrame126("be a bigger task @red@(100 points)", 57742);
			c.getPA().sendFrame126("be a bigger task @red@(100 points)", 57744);
			c.getPA().sendFrame126("it will be a bigger task @red@(100 points)", 57746);
		}
		if (c.slayerPoints < 50) {
			c.getPA().sendFrame126("will be a bigger task @red@(50 points)", 57718);
		}
		if (c.slayerPoints < 120) {
			c.getPA().sendFrame126("will be a bigger task @red@(120 points)", 57722);
		}
		if (c.slayerPoints < 75) {
			c.getPA().sendFrame126("be a bigger task @red@(75 points)", 57732);
		}
		c.getPA().sendFrame126("Whenever you get an Ankou task, it will be", 57713);
		c.getPA().sendFrame126("Whenever you get a Suqah task, it will be", 57715);
		c.getPA().sendFrame126("Whenever you get a Black Dragon task, it", 57717);
		c.getPA().sendFrame126("Whenever you get a Bronze, Iron or steel", 57719);
		c.getPA().sendFrame126("Whenever you get a Mithril Dragon task, it", 57721);
		c.getPA().sendFrame126("Whenever you get a Spiritual Creature", 57723);
		c.getPA().sendFrame126("Whenever you get an Aviansie task, it will", 57725);
		c.getPA().sendFrame126("Whenever you get a Greater Demon task,", 57727);
		c.getPA().sendFrame126("Whenever you get a Black Demon task, it", 57729);
		c.getPA().sendFrame126("Whenever you get a Bloodveld task, it will", 57731);
		c.getPA().sendFrame126("Whenever you get an Aberrant Spectre", 57733);
		c.getPA().sendFrame126("Whenever you get a Cave Horror task, it", 57735);
		c.getPA().sendFrame126("Whenever you get a Dusk Devil task, it will", 57737);
		c.getPA().sendFrame126("Whenever you get a Skeletal Wyvern task,", 57739);
		c.getPA().sendFrame126("Whenever you get a Gargoyle task, it will", 57741);
		c.getPA().sendFrame126("Whenever you get a Nechryael task, it will", 57743);
		c.getPA().sendFrame126("Whenever you get an Abyssal Demon task,", 57745);
		c.getPA().sendFrame126("Whenever you get a Cave Kraken task, it", 57747);
		c.getPA().sendFrame126("" + c.slayerPoints, 48505);
	}

	public void update() {
		World.getWorld().getPlayerHandler().updatePlayer(this, outStream);
		World.getWorld().getPlayerHandler().updateNPC(this, outStream);
		flushOutStream();

	}
	public int lastRegionX, lastRegionY;

	public int playerId = -1;
	public String playerName = null;
	public String playerName2 = null;
	public String playerPass = null;
	public int playerRights;
	public PlayerHandler handler = null;
	public List<Integer> fancyBoxItems = new ArrayList<>();

	private FancyBox fancyBox = new FancyBox(this);

	public FancyBox getFancyBox() {
	    return fancyBox;
	}
	public int playerItems[] = new int[28];
	public int playerItemsN[] = new int[28];
	public int playerLootItems[] = new int[28];
	public int playerLootItemsN[] = new int[28];
	public int playerLogItems[] = new int[43];
	public int playerLogItemsAmt[] = new int[43];

	public int CollectionLog = 0;
	public boolean bankNotes = false;


	public int[] SLAYER_HELMETS = { 11864, 11865, 19639, 19641, 19643, 19645, 19647, 19649, 21888, 21890, 21264, 21266 };
	public int[] IMBUED_SLAYER_HELMETS = { 11865, 19641, 19645, 19649, 21890, 21266 };

	public int[] GRACEFUL = { 11850, 11852, 11854, 11856, 11858, 11860, 13579, 13581, 13583, 13585, 13587, 13589, 13591,
			13593, 13595, 13597, 13599, 13601, 13603, 13605, 13607, 13609, 13611, 13613, 13615, 13617, 13619, 13621,
			13623, 13625, 13627, 13629, 13631, 13633, 13635, 13637, 13667, 13669, 13671, 13673, 13675, 13677, 21061,
			21064, 21067, 21070, 21073, 21076 };

	public boolean wearingGrace() {
		return getItems().isWearingAnyItem(GRACEFUL);
	}

	public int graceSum = 0;

	public void graceSum() {
		graceSum = 0;
		for (int grace : GRACEFUL) {
			if (getItems().isWearingItem(grace)) {
				graceSum++;
			}
		}
		if (SkillcapePerks.AGILITY.isWearing(this) || SkillcapePerks.isWearingMaxCape(this)) {
			graceSum++;
		}
	}
	public boolean Area(final int x1, final int x2, final int y1, final int y2) {
		return (getX() >= x1 && getX() <= x2 && getY() >= y1 && getY() <= y2);
	}

	public boolean inBank() {
		return Area(3090, 3099, 3487, 3500) || Area(3089, 3090, 3492, 3498) || Area(3248, 3258, 3413, 3428)
				|| Area(3179, 3191, 3432, 3448) || Area(2944, 2948, 3365, 3374) || Area(2942, 2948, 3367, 3374)
				|| Area(2944, 2950, 3365, 3370) || Area(3008, 3019, 3352, 3359) || Area(3017, 3022, 3352, 3357)
				|| Area(3203, 3213, 3200, 3237) || Area(3212, 3215, 3200, 3235) || Area(3215, 3220, 3202, 3235)
				|| Area(3220, 3227, 3202, 3229) || Area(3227, 3230, 3208, 3226) || Area(3226, 3228, 3230, 3211)
				|| Area(3227, 3229, 3208, 3226) || Area(3025, 3032, 3374, 3384) || Area(3088, 3101, 3507, 3516);
	}
	public int[] degradableItem = new int[Degrade.MAXIMUM_ITEMS];
	public boolean[] claimDegradableItem = new boolean[Degrade.MAXIMUM_ITEMS];
	public int timer = 10;
	public int playerStandIndex = 0x328;
	public int playerTurnIndex = 0x337;
	public int playerWalkIndex = 0x333;
	public int playerTurn180Index = 0x334;
	public int playerTurn90CWIndex = 0x335;
	public int playerTurn90CCWIndex = 0x336;
	public int playerRunIndex = 0x338;

	public int playerHat = 0;
	public int playerCape = 1;
	public int playerAmulet = 2;
	public int playerWeapon = 3;
	public int playerChest = 4;
	public int playerShield = 5;
	public int playerLegs = 7;
	public int playerHands = 9;
	public int playerFeet = 10;
	public int playerRing = 12;
	public int playerArrows = 13;

	public int playerAttack = 0;
	public int playerDefence = 1;
	public int playerStrength = 2;
	public int playerHitpoints = 3;
	public int playerRanged = 4;
	public int playerPrayer = 5;
	public int playerMagic = 6;
	public int playerCooking = 7;
	public int playerWoodcutting = 8;
	public int playerFletching = 9;
	public int playerFishing = 10;
	public int playerFiremaking = 11;
	public int playerCrafting = 12;
	public int playerSmithing = 13;
	public int playerMining = 14;
	public int playerHerblore = 15;
	public int playerAgility = 16;
	public int playerThieving = 17;
	public int playerSlayer = 18;
	public int playerFarming = 19;
	public int playerRunecrafting = 20;
	public int playerHunter = 21;
	public int playerConstruction = 22;

	public int[] playerEquipment = new int[14];
	public int[] playerEquipmentN = new int[14];
	public int[] playerLevel = new int[25];
	public int[] playerXP = new int[25];
	public Thieving getThieving() {
		return thieving;
	}
	private Thieving thieving = new Thieving(this);
	public void updateshop(int i) {
		Player p = (Player) PlayerHandler.players[getIndex()];
		p.getShops().resetShop(i);
	}

	public void println_debug(String str) {
		System.out.println("[player-" + playerId + "]: " + str);
	}

	public void println(String str) {
		System.out.println("[player-" + playerId + "]: " + str);
	}

	public void setMale() {
		playerAppearance[0] = 0; // gender
		playerAppearance[1] = 0;
		playerAppearance[2] = 18;
		playerAppearance[3] = 26;
		playerAppearance[4] = 33;
		playerAppearance[5] = 36;
		playerAppearance[6] = 42; // feet
		playerAppearance[7] = 10; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 0; // torso colour
		playerAppearance[10] = 0; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0;
	}
//Delayed Hit
	public void setFemale() {
		playerAppearance[0] = 1; // gender
		playerAppearance[1] = 47;// hair
		playerAppearance[2] = 56;// torso
		playerAppearance[3] = 61;// arms
		playerAppearance[4] = 67;// hands
		playerAppearance[5] = 71;// legs
		playerAppearance[6] = 79; // feet
		playerAppearance[7] = 255; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 1; // torso colour
		playerAppearance[10] = 1; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0;
	}

	public Player(int _playerId, String name, Channel channel) {
		super(_playerId, name);
		this.session = channel;
		playerRights = 0;

		for (int i = 0; i < playerItems.length; i++) {
			playerItems[i] = 0;
		}
		for (int i = 0; i < playerLootItems.length; i++) {
			playerLootItems[i] = 0;
		}
		for (int i = 0; i < playerItemsN.length; i++) {
			playerItemsN[i] = 0;
		}
		for (int i = 0; i < playerLootItemsN.length; i++) {
			playerLootItemsN[i] = 0;
		}
		for (int i = 0; i < Config.BANK_SIZE; i++) {
			bankItems[i] = 0;
		}

		for (int i = 0; i < Config.BANK_SIZE; i++) {
			bankItemsN[i] = 0;
		}

		playerAppearance[0] = 0; // gender
		playerAppearance[1] = 0; // head
		playerAppearance[2] = 18;// Torso
		playerAppearance[3] = 26; // arms
		playerAppearance[4] = 33; // hands
		playerAppearance[5] = 36; // legs
		playerAppearance[6] = 42; // feet
		playerAppearance[7] = 10; // beard
		playerAppearance[8] = 0; // hair colour
		playerAppearance[9] = 0; // torso colour
		playerAppearance[10] = 0; // legs colour
		playerAppearance[11] = 0; // feet colour
		playerAppearance[12] = 0; // skin colour

		apset = 0;
		actionID = 0;

		playerEquipment[playerHat] = -1;
		playerEquipment[playerCape] = -1;
		playerEquipment[playerAmulet] = -1;
		playerEquipment[playerChest] = -1;
		playerEquipment[playerShield] = -1;
		playerEquipment[playerLegs] = -1;
		playerEquipment[playerHands] = -1;
		playerEquipment[playerFeet] = -1;
		playerEquipment[playerRing] = -1;
		playerEquipment[playerArrows] = -1;
		playerEquipment[playerWeapon] = -1;

		setX(Config.START_LOCATION_X);
		setY(Config.START_LOCATION_Y);
		setHeight(0);
		setLastKnownLocation(new Location(-1, -1, -1));
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		resetWalkingQueue();
		npcsWithPayDirt = new ArrayList<>();
		outStream = new Buffer(new byte[Config.BUFFER_SIZE]);
		outStream.currentOffset = 0;

		inStream = new Buffer(new byte[Config.BUFFER_SIZE]);
		inStream.currentOffset = 0;
		buffer = new byte[Config.BUFFER_SIZE];
	}

	private Channel session;

	private RareDropTable rareDropTable = new RareDropTable(this);
	private GemRareDropTable gemRareDropTable = new GemRareDropTable(this);
	private SlayerRareDropTable slayerRareDropTable = new SlayerRareDropTable(this);
	public RareDropTable getRareDropTable() {
		return rareDropTable;
	}
	
	public GemRareDropTable getGemRareDropTable() {
		return gemRareDropTable;
	}
	
	public SlayerRareDropTable getSlayerRareDropTable() {
		return slayerRareDropTable;
	}
	
	public void destruct() {
		if (session == null) {
			return;
		}
		if (underAttackBy > 0 || underAttackBy2 > 0)
			return;
		server.world.ApiClient.updatePlayerStatus(playerName, 0);
		/*if (disconnected == true) {
			saveCharacter = true;

		}*/
		//if (getXeric() != null) {
			//getXeric().removePlayer(this);
		//}
		if (zulrah.getInstancedZulrah() != null) {
			InstancedAreaManager.getSingleton().disposeOf(zulrah.getInstancedZulrah());
		}
		if (skotizo != null) {
			InstancedAreaManager.getSingleton().disposeOf(skotizo);
		}
		if (cerberus != null) {
			InstancedAreaManager.getSingleton().disposeOf(cerberus);
		}
		if (disconnected == true) {
			Misc.println("[DEREGISTERED]: " + playerName + "");
			saveCharacter = true;
		}
		World.getWorld().getMultiplayerSessionListener().removeOldRequests(this);
		/*
		 * if (clan != null) { clan.removeMember(playerName); }
		 */

		World.getWorld().getEventHandler().stop(this);
		CycleEventHandler.getSingleton().stopEvents(this);
		getFriends().notifyFriendsOfUpdate();
		PriceChecker.clearConfig(this);
		Construction.saveHouse(this);
		queuedPackets.clear();
		songConfigMap.clear();
		collectionProgress.clear();
		spawnedNmzBosses.clear();
		npcsWithPayDirt.clear();
		setInHouse(false);
		disconnected = true;
		// logoutDelay = Long.MAX_VALUE;
		session.close();
		session = null;
		inStream = null;
		outStream = null;
		isActive = false;
		
		buffer = null;
		playerListSize = 0;
		for (int i = 0; i < maxPlayerListSize; i++)
			playerList[i] = null;
		mapRegionX = mapRegionY = -1;
		currentX = currentY = 0;
		resetWalkingQueue();
	}

	public int getDistanceRequired() {
		return !this.usingMagic && !this.usingBallista && !this.usingRangeWeapon && !usingBow && !this.autocasting
				? (getMovementQueue().isMoving() ? 3 : 1)
				: 9;
	}

	public void setSidebarInterface(int menuId, int form) {
		// synchronized (this) {
		if (getOutStream() != null) {
			outStream.createFrame(71);
			outStream.writeWord(form);
			outStream.writeByteA(menuId);
		}

	}

	public byte buffer[] = null;
	public static final int maxPlayerListSize = Config.MAX_PLAYERS;
	public Player playerList[] = new Player[maxPlayerListSize];
	public int playerListSize = 0;

	public byte playerInListBitmap[] = new byte[(Config.MAX_PLAYERS + 7) >> 3];

	public static final int maxNPCListSize = NPCHandler.maxNPCs;
	public static final long PM_POLL_INTERVAL_MS = 60;
	public NPC npcList[] = new NPC[maxNPCListSize];
	public int npcListSize = 0;

	public byte npcInListBitmap[] = new byte[(NPCHandler.maxNPCs + 7) >> 3];



	public int distanceToPoint(int pointX, int pointY) {
		return (int) Math.sqrt(Math.pow(getX() - pointX, 2) + Math.pow(getY() - pointY, 2));
	}

	public int mapRegionX, mapRegionY;
	public int absX, absY;
	public int currentX, currentY;
	public int lastX, lastY, lastHeight;

	//public int heightLevel;
	public int playerSE = 0x328;
	public int playerSEW = 0x333;
	public int playerSER = 0x334;

	public boolean updateRequired = true;

	public final int walkingQueueSize = 50;
	public int walkingQueueX[] = new int[walkingQueueSize], walkingQueueY[] = new int[walkingQueueSize];
	public int wQueueReadPtr = 0;
	public int wQueueWritePtr = 0;
	public boolean isRunning = true;
	public int teleportToX = -1, teleportToY = -1;

	public Buffer getInStream() {
		return inStream;
	}

	public Buffer getOutStream() {
		return outStream;
	}

	public Buffer inStream = null, outStream = null;

	public void resetWalkingQueue() {
		/*
		 * wQueueReadPtr = wQueueWritePtr = 0;
		 * 
		 * for (int i = 0; i < walkingQueueSize; i++) { walkingQueueX[i] = currentX;
		 * walkingQueueY[i] = currentY; }
		 */
		getMovementQueue().reset();
	}

	public void addToWalkingQueue(int x, int y) {
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int distance) {
		return Misc.goodDistance(objectX, objectY, playerX, playerY, distance);
	}

	public boolean goodDistance(int objectX, int objectY, int playerX, int playerY, int width, int length) {
		return Misc.goodDistance(objectX, objectY, playerX, playerY, width, length);
	}

	public static void deleteTime(Player c) {
		c.doAmount--;
	}

	public void updateWalkEntities() {
		if (Boundary.isIn(this, Barrows.TUNNEL)) {
			if (!World.getWorld().getEventHandler().isRunning(this, "barrows_tunnel")) {
				World.getWorld().getEventHandler().submit(new TunnelEvent("barrows_tunnel", this, 1));
			}
			getPA().setMinimapState(2);
		} else {
			if (World.getWorld().getEventHandler().isRunning(this, "barrows_tunnel")) {
				World.getWorld().getEventHandler().stop(this, "barrows_tunnel");
			}
		}
		if (Boundary.isIn(this, Boundary.WILDERNESS) && !Boundary.isIn(this, Boundary.FEROX_ENCLAVE)) {
			int modY = getY() > 6400 ? getY() - 6400 : getY();
			wildLevel = (((modY - 3520) / 8) + 1);
			getPA().walkableInterface(197);
			if (Config.SINGLE_AND_MULTI_ZONES) {
				if (Boundary.isIn(this, Boundary.MULTI)) {
					getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
				} else {
					getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
				}
			} else {
				getPA().multiWay(-1);
				getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
			}
			if (attackOptions)
				getPA().showOption(3, 0, "Attack", 1);
		} else if (Boundary.isIn(this, Boundary.DUEL_ARENA)) {
			getPA().walkableInterface(201);
			if (duelStatus == 5) {
				getPA().showOption(3, 0, "Attack", 1);
			} else {
				getPA().showOption(3, 0, "Challenge", 1);
			}
		} else if (Boundary.isIn(this, Boundary.BANDOS_GODWARS) || Boundary.isIn(this, Boundary.ARMADYL_GODWARS)
				|| Boundary.isIn(this, Boundary.ZAMORAK_GODWARS) || Boundary.isIn(this, Boundary.SARADOMIN_GODWARS)) {
			//getPA().sendFrame126("" + armadylKC, 47656);// Armadyl Kills
			//getPA().sendFrame126("" + bandosKC, 47657);// bandos Kills:
			//getPA().sendFrame126("" + saraKC, 47658);// sara kills
			//getPA().sendFrame126("" + zammyKC, 47659);// zam kills
			//getPA().walkableInterface(47650);
		} else if (barrows.inBarrows()) {
			barrows.drawInterface();
			getPA().walkableInterface(27500);
		} else if (inCwGame || inPits) {
			getPA().showOption(3, 0, "Attack", 1);
		} else if (Boundary.isIn(this, Boundary.SKOTIZO_BOSSROOM)) {
			getPA().walkableInterface(29230);
            
        // ==========================================
        // ADDED FISHING TRAWLER HERE
        // ==========================================
		} else if (Boundary.isIn(this, Boundary.FISHING_TRAWLER)) {
			getPA().walkableInterface(11908);
            
		} else if (getPA().inPitsWait()) {
			getPA().showOption(3, 0, "Null", 1);
		} else if(Construction.enteringHouse) {
			getPA().setMinimapState(2);
		} else {
			getPA().setMinimapState(0);
			getPA().walkableInterface(-1);
			getPA().showOption(3, 0, "Null", 1);
		}
	}

	public void prelogout() {
		if (getInstance() != null) {
			getInstance().destroy();
		}
	}

	public boolean didTeleport = false;
	public boolean mapRegionDidChange = false;
	public int dir1 = -1, dir2 = -1;
	public boolean createItems = false;
	public int poimiX = 0, poimiY = 0;

	public void getNextPlayerMovement() {
		getMovementQueue().process();
	}
	public void updateThisPlayerMovement(Buffer str) {
		if (isNeedsPlacement()) {
			getMovementQueue().handleRegionChange();
		}

		str.createFrameVarSizeWord(81);
		str.initBitAccess();

		if (isNeedsPlacement()) {
			str.writeBits(1, 1);
			str.writeBits(2, 3);
			str.writeBits(2, getHeight());
			str.writeBits(1, 1);
			str.writeBits(1, (updateRequired) ? 1 : 0);
			str.writeBits(7, getLocation().getLocalY(getLastKnownLocation()));
			str.writeBits(7, getLocation().getLocalX(getLastKnownLocation()));
			return;
		}

		if (getWalkingDirection().getId() == -1) {
			if (updateRequired) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else {
				str.writeBits(1, 0);
			}
		} else {
			str.writeBits(1, 1);
			if (getRunningDirection().getId() == -1) {
				str.writeBits(2, 1);
				str.writeBits(3, getWalkingDirection().getId());
				str.writeBits(1, updateRequired ? 1 : 0);
			} else {
				str.writeBits(2, 2);
				str.writeBits(3, getWalkingDirection().getId());
				str.writeBits(3, getRunningDirection().getId());
				str.writeBits(1, updateRequired ? 1 : 0);
			}
		}

	}

	public boolean stopPlayer(boolean stop) {

		return stopPlayerPacket == stop;
	}

	public void updatePlayerMovement(Buffer str) {
		// synchronized(this) {
		if (getWalkingDirection().getId() == -1) {
			if (updateRequired || isChatTextUpdateRequired()) {
				str.writeBits(1, 1);
				str.writeBits(2, 0);
			} else
				str.writeBits(1, 0);
		} else if (getRunningDirection().getId() == -1) {
			str.writeBits(1, 1);
			str.writeBits(2, 1);
			str.writeBits(3, getWalkingDirection().getId());
			str.writeBits(1, (updateRequired || isChatTextUpdateRequired()) ? 1 : 0);
		} else {

			str.writeBits(1, 1);
			str.writeBits(2, 2);
			str.writeBits(3, getWalkingDirection().getId());
			str.writeBits(3, getRunningDirection().getId());
			str.writeBits(1, (updateRequired || isChatTextUpdateRequired()) ? 1 : 0);
		}

	}

	public byte cachedPropertiesBitmap[] = new byte[(Config.MAX_PLAYERS + 7) >> 3];

	Player c = (Player) this;

	public int getLootBagWealth() {
	    int total = 0;

	    for (int i = 0; i < c.playerLootItems.length; i++) {
	        if (c.playerLootItems[i] > 0) {
	            int itemId = c.playerLootItems[i];
	            int geValue = ItemsKeptOnDeath.getGEValue(itemId);
	            total += geValue * c.playerLootItemsN[i];
	        }
	    }

	    return total;
	}
	public void addNewNPC(NPC npc, Buffer str, Buffer updateBlock) {
		int id = npc.getIndex();
		npcInListBitmap[id >> 3] |= 1 << (id & 7);
		npcList[npcListSize++] = npc;

		if(npcListSize >= 16383)
			return;
		str.writeBits(14, id);

		int z = npc.getY() - getY();
		if (z < 0)
			z += 64;
		str.writeBits(6, z);
		z = npc.getX() - getX();
		if (z < 0)
			z += 64;
		str.writeBits(6, z);

		str.writeBits(1, 0);
		str.writeBits(16, npc.npcType);

		boolean savedUpdateRequired = npc.updateRequired;
		npc.updateRequired = true;
		npc.appendNPCUpdateBlock(updateBlock);
		npc.updateRequired = savedUpdateRequired;
		str.writeBits(1, 1);
	}

	public void addNewPlayer(Player plr, Buffer str, Buffer updateBlock) {
		int id = plr.getIndex();
		playerInListBitmap[id >> 3] |= 1 << (id & 7);
		playerList[playerListSize++] = plr;
		str.writeBits(11, id);
		str.writeBits(1, 1);
		boolean savedFlag = plr.isAppearanceUpdateRequired();
		boolean savedUpdateRequired = plr.updateRequired;
		plr.setAppearanceUpdateRequired(true);
		plr.updateRequired = true;
		plr.isDead = false;
		plr.appendPlayerUpdateBlock(updateBlock);
		plr.setAppearanceUpdateRequired(savedFlag);
		plr.updateRequired = savedUpdateRequired;
		str.writeBits(1, 1);
		int z = plr.getY() - getY();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
		z = plr.getX() - getX();
		if (z < 0)
			z += 32;
		str.writeBits(5, z);
	}


	private final int[] configValues = new int[4000];
	public int getConfigValue(int id) { return configValues[id]; }
	public void setConfigValue(int id, int value) { configValues[id] = value; }

	public boolean withinDistance(Player otherPlr) {
		if (getHeight() != otherPlr.getHeight())
			return false;
		int deltaX = otherPlr.getX() - getX(), deltaY = otherPlr.getY() - getY();
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}
	public boolean withinDistance(int x1, int y1, int x2, int y2, int distance) {
	    return Math.abs(x1 - x2) <= distance && Math.abs(y1 - y2) <= distance;
	}
	public boolean withinDistance(NPC npc) {//brb
		if (getHeight() != npc.getHeight())
			return false;
		if (npc.needRespawn == true)
			return false;
		int deltaX = npc.getX() - getX(), deltaY = npc.getY() - getY();
		//return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16; //  original render
		return deltaX <= currentRender-1 && deltaX >= -currentRender && deltaY <= currentRender-1 && deltaY >= -currentRender;
	}
	public int DirectionCount = 0;
	private boolean appearanceUpdateRequired = true;
	protected int hitDiff2;
	private int hitDiff = 0;
	protected boolean hitUpdateRequired2;
	private boolean hitUpdateRequired = false;
	public boolean isDead = false;
	private long nameAsLong;
	private String lastClanChat = "", revertOption = "";

	protected static Buffer playerProps;
	static {
		playerProps = new Buffer(new byte[100]);
	}

	protected void appendPlayerAppearance(Buffer str) {
		playerProps.currentOffset = 0;

		playerProps.writeByte(playerAppearance[0]);

		playerProps.writeByte(headIcon);
		playerProps.writeByte(headIconPk);
		// playerProps.writeByte(headIconHints);
		// playerProps.writeByte(bountyIcon);
		if (!isNpc) {

			if (playerEquipment[playerHat] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerHat]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerCape] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerCape]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerAmulet] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerAmulet]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerWeapon] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerWeapon]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerChest] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerChest]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[2]);
			}

			if (playerEquipment[playerShield] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerShield]);
			} else {
				playerProps.writeByte(0);
			}

			if (!Item.isFullBody(playerEquipment[playerChest])) {
				playerProps.writeWord(0x100 + playerAppearance[3]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerLegs] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerLegs]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[5]);
			}

			if (!Item.isFullHelm(playerEquipment[playerHat]) && !Item.isFullMask(playerEquipment[playerHat])) {
				playerProps.writeWord(0x100 + playerAppearance[1]);
			} else {
				playerProps.writeByte(0);
			}

			if (playerEquipment[playerHands] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerHands]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[4]);
			}

			if (playerEquipment[playerFeet] > 1) {
				playerProps.writeWord(0x200 + playerEquipment[playerFeet]);
			} else {
				playerProps.writeWord(0x100 + playerAppearance[6]);
			}

			if (playerAppearance[0] != 1 && !Item.isFullMask(playerEquipment[playerHat])) {
				playerProps.writeWord(0x100 + playerAppearance[7]);
			} else {
				playerProps.writeByte(0);
			}
		} else {// send npc data
			playerProps.writeWord(-1);// Tells client that were being a npc
			playerProps.writeWord(npcId2);// send NpcID
		}
		playerProps.writeByte(playerAppearance[8]);
		playerProps.writeByte(playerAppearance[9]);
		playerProps.writeByte(playerAppearance[10]);
		playerProps.writeByte(playerAppearance[11]);
		playerProps.writeByte(playerAppearance[12]);
		playerProps.writeWord(playerStandIndex); // standAnimIndex
		playerProps.writeWord(playerTurnIndex); // standTurnAnimIndex
		playerProps.writeWord(playerWalkIndex); // walkAnimIndex
		playerProps.writeWord(playerTurn180Index); // turn180AnimIndex
		playerProps.writeWord(playerTurn90CWIndex); // turn90CWAnimIndex
		playerProps.writeWord(playerTurn90CCWIndex); // turn90CCWAnimIndex
		playerProps.writeWord(playerRunIndex); // runAnimIndex
		playerProps.writeQWord(Misc.playerNameToInt64(playerName));
		combatLevel = calculateCombatLevel();
		playerProps.writeByte(combatLevel); // combat level
		playerProps.writeWord(0);
		str.writeByteC(playerProps.currentOffset);
		str.writeBytes(playerProps.buffer, playerProps.currentOffset, 0);
	}


	public String getLastClanChat() {
		return lastClanChat;
	}

	public void setLastClanChat(String founder) {
		lastClanChat = founder;
	}

	public long getNameAsLong() {
		return nameAsLong;
	}

	public void setNameAsLong(long hash) {
		this.nameAsLong = hash;
	}

	public boolean isStopPlayer() {
		return stopPlayer;
	}

	public void setStopPlayer(boolean stopPlayer) {
		this.stopPlayer = stopPlayer;
	}

	public int getFace() {
		return this.getIndex() + '\u8000';
	}

	public int getLockIndex() {
		return -this.getIndex() - 1;
	}

	public boolean isDead() {

		return getHealth().getAmount() <= 0 || this.isDead;
	}

	public void healPlayer(int heal) {
		getHealth().increase(heal);
	}

	int maxLevel() {
		return 99;
	}

	private String macAddress;

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getIpAddress() {
		return connectedFrom;
	}

	public void setIpAddress(String ipAddress) {
		this.connectedFrom = ipAddress;
	}

	public PlayerAssistant getPA() {
		return playerAssistant;
	}

	public CombatAssistant getCombat() {
		return combatAssistant;
	}

	private PlayerAssistant playerAssistant = new PlayerAssistant(this);
	private CombatAssistant combatAssistant = new CombatAssistant(this);
	/**
	 * 
	 * @param skillId
	 * @param amount
	 */
	public void replenishSkill(int skillId, int amount) {
		if (skillId < 0 || skillId > Skill.length() - 1) {
			return;
		}
		int current = skills.getLevel(Skill.forId(skillId));
		skills.setLevelOrActual(current++, Skill.forId(skillId));
		skills.sendRefresh();
	}
	public int calculateCombatLevel() {
		int j = skills.getActualLevel(Skill.ATTACK);
		int k = skills.getActualLevel(Skill.DEFENCE);
		int l = skills.getActualLevel(Skill.STRENGTH);
		int i1 = skills.getActualLevel(Skill.HITPOINTS);
		int j1 = skills.getActualLevel(Skill.PRAYER);
		int k1 = skills.getActualLevel(Skill.RANGED);
		int l1 = skills.getActualLevel(Skill.MAGIC);
		int combatLevel = (int) (((k + i1) + Math.floor(j1 / 2)) * 0.24798D) + 1;
		double d = (j + l) * 0.32500000000000001D;
		double d1 = Math.floor(k1 * 1.5D) * 0.32500000000000001D;
		double d2 = Math.floor(l1 * 1.5D) * 0.32500000000000001D;
		if (d >= d1 && d >= d2) {
			combatLevel += d;
		} else if (d1 >= d && d1 >= d2) {
			combatLevel += d1;
		} else if (d2 >= d && d2 >= d1) {
			combatLevel += d2;
		}
		return combatLevel;
	}

	public int getLevelForXP(int exp) {
		if (exp > 13034430) {
			return 99;
		} else {
			int points = 0;
			for (int lvl = 1; lvl <= 99; ++lvl) {
				points = (int) ((double) points
						+ Math.floor((double) lvl + 300.0D * Math.pow(2.0D, (double) lvl / 7.0D)));
				int var5 = (int) Math.floor((double) (points / 4));
				if (var5 >= exp) {
					return lvl;
				}
			}

			return 99;
		}
	}

	private boolean chatTextUpdateRequired = false;
	private byte chatText[] = new byte[4096];
	private byte chatTextSize = 0;
	private int chatTextColor = 0;
	private int chatTextEffects = 0;

	protected void appendPlayerChatText(Buffer str) {
		str.writeWordBigEndian(((getChatTextColor() & 0xFF) << 8) + (getChatTextEffects() & 0xFF));
		str.writeByte(playerRights);
		str.writeByteC(getChatTextSize());
		str.writeBytes_reverse(getChatText(), getChatTextSize(), 0);
	}

	public void forcedChat(String text) {
		forcedText = text;
		forcedChatUpdateRequired = true;
		updateRequired = true;
		setAppearanceUpdateRequired(true);
	}

	public String forcedText = "null";

	public void appendForcedChat(Buffer str) {
		str.writeString(forcedText);
	}

	/**
	 * Graphics
	 **/

	public int mask100var1 = 0;
	public int mask100var2 = 0;
	protected boolean mask100update = false;

	public void appendMask100Update(Buffer str) {
		str.writeWordBigEndian(mask100var1);
		str.writeDWord(mask100var2);
	}
	public void appendMask400Update(Buffer str) {
	    str.writeByte(x1);
	    str.writeByte(y1);
	    str.writeByte(x2);
	    str.writeByte(y2);
	    str.writeWord(speed1);
	    str.writeWord(speed2);
	    str.writeByte(direction);
	    //str.writeWord(startHeight); // New
	    //str.writeWord(endHeight);   // New
	}
	public void gfx(int gfx, int height) {
		mask100var1 = gfx;
		mask100var2 = 65536 * height;
		mask100update = true;
		updateRequired = true;
	}

	public void gfx100(int gfx) {
		mask100var1 = gfx;
		mask100var2 = 6553600;
		mask100update = true;
		updateRequired = true;
	}

	public void gfx0(int gfx) {
		updateRequired = true;
		mask100var2 = 65536;
		mask100var1 = gfx;
		mask100update = true;
	}

	public boolean wearing2h() {
		Player c = (Player) this;
		String s = ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]);
		if (s.contains("2h"))
			return true;
		if (s.contains("bulwark") || s.contains("elder maul") || s.contains("stunning hammer"))
			return true;
		if (s.contains("godsword") || s.contains("k'ril"))
			return true;

		return false;
	}

	/**
	 * Animations
	 **/
	public void startAnimation(int animId) {
		// if (wearing2h() && animId == 829)
		// return;
		animationRequest = animId;
		animationWaitCycles = 0;
		updateRequired = true;
	}

	public void startAnimation(int animId, int time) {
		animationRequest = animId;
		animationWaitCycles = time;
		updateRequired = true;
	}

	public void appendAnimationRequest(Buffer str) {
		str.writeWordBigEndian((animationRequest == -1) ? 65535 : animationRequest);
		str.writeByteC(animationWaitCycles);
	}

	/**
	 * Face Update
	 **/

	protected boolean faceUpdateRequired = false;
	public int face = -1;
	public int FocusPointX = -1, FocusPointY = -1;

	public void faceUpdate(int index) {
		face = index;
		faceUpdateRequired = true;
		updateRequired = true;
	}

	public void appendFaceUpdate(Buffer str) {
		str.writeWordBigEndian(face);
	}
	public int pitfallVarp917 = 0;
	public int pitfallVarp918 = 0;
	public boolean falconryUnlocked = false;
	public void turnPlayerTo(int pointX, int pointY) {
		FocusPointX = 2 * pointX + 1;
		FocusPointY = 2 * pointY + 1;
		updateRequired = true;
	}
	/**
	 * Turns the player to face the exact center of an object based on its dimensions.
	 */
	/**
	 * Turns the player to face the exact center of an object based on its cache dimensions.
	 */
	public void turnToObject(int objectX, int objectY, int width, int length) {
		this.FocusPointX = (objectX * 2) + width;
		this.FocusPointY = (objectY * 2) + length;
		this.updateRequired = true;
	}
	private void appendSetFocusDestination(Buffer str) {
		str.writeWordBigEndianA(FocusPointX);
		str.writeWordBigEndian(FocusPointY);
	}

	/**
	 * Hit Update
	 **/

	@Override
	protected void appendHitUpdate(Buffer str) {
		str.writeByte(hitDiff);
		if (hitmark1 == null) {
			str.writeByteA(0);
		} else {
			str.writeByteA(hitmark1.getId());
		}
		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		str.writeByteC(getHealth().getAmount());
		str.writeByte(getHealth().getMaximum());
	}

	@Override
	protected void appendHitUpdate2(Buffer str) {
		str.writeByte(hitDiff2);
		if (hitmark2 == null) {
			str.writeByteS(0);
		} else {
			str.writeByteS(hitmark2.getId());
		}
		if (getHealth().getAmount() <= 0) {
			isDead = true;
		}
		str.writeByte(getHealth().getAmount());
		str.writeByteC(getHealth().getMaximum());
	}

	/**
	 * 0 North 1 East 2 South 3 West
	 */
	public int x1 = -1, y1 = -1, x2 = -1, y2 = -1, speed1 = -1, speed2 = -1, direction = -1;
	public int xOffsetWalk, yOffsetWalk;
	public boolean forceMovement;
	public double yanilleBalanceLedgeSuccessChance(int agilityLevel) {
	    // Requires 40 Agility
	    if (agilityLevel < 40) return 0.0;
	    if (agilityLevel >= 64) return 1.0;
	    // Linear from 71% at 40 -> 100% at 64
	    double slope = (1.0 - 0.71) / (64.0 - 40.0); // 0.29 / 24 ≈ 0.0120833 per level
	    return 0.71 + (agilityLevel - 40) * slope;
	}
	public boolean roll(double p) { // p in [0,1]
	    return Math.random() < p;
	}
	public void setForceMovement(int xOffset, int yOffset, int speedOne, int speedTwo, String directionSet,
			int animation) {
		if (isForceMovementActive() || forceMovement) {
			return;
		}
	    startAnimation(animation); // Climb Up
		forceMovementActive = true;
		isRunning2 = false;
		stopMovement();
	    // 2. COORDINATE SYNC
	    final int startX = getX();
	    final int startY = getY();
	    final int targetX = xOffset - getX();
	    final int targetY = yOffset - getY();

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    x1 = startX - baseRegionX;
	    y1 = startY - baseRegionY;
	    x2 = targetX - baseRegionX;
	    y2 = targetY - baseRegionY;
		updateRequired = true;
		forceMovement = true;
		speed1 = speedOne;
		speed2 = speedTwo;
		//setAppearanceUpdateRequired(true);
		//getPA().requestUpdates();

		World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, 0) {

			@Override
			public void execute() {
				if (attachment == null || attachment.disconnected) {
					super.stop();
					return;
				}
				switch (directionSet.toUpperCase()) {
			    case "NORTH": attachment.direction = 0; break;
			    case "EAST":  attachment.direction = 1; break;
			    case "SOUTH": attachment.direction = 2; break;
			    case "WEST":  attachment.direction = 3; break;
			    default:      attachment.direction = 0; break;
			}
				super.stop();
			}
		});
		World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, Math.abs(xOffsetWalk) + Math.abs(yOffsetWalk)) {
		    @Override
		    public void execute() {
		        if (attachment == null || attachment.disconnected) {
		            super.stop();
		            return;
		        }
		        attachment.startAnimation(animation);
				/*attachment.playerStandIndex = animation;
				attachment.playerRunIndex = animation;
				attachment.playerWalkIndex = animation;*/
				setAppearanceUpdateRequired(true);
				getPA().requestUpdates();
		        attachment.forceMovement = false;
		        super.stop();
		    }
		});
		World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, Math.abs(xOffsetWalk) + Math.abs(yOffsetWalk)+3) {
		    @Override
		    public void execute() {
		        if (attachment == null || attachment.disconnected) {
		            super.stop();
		            return;
		        }

	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.getPA().requestUpdates();
		        attachment.getCombat().getPlayerAnimIndex(Item.getItemName(attachment.playerEquipment[attachment.playerWeapon]).toLowerCase());


		        super.stop();
		    }
		});

	}
	
	public void setWallCrawl(int[][] path, String direction) {
	    if (isForceMovementActive()) return;

	    getMovementQueue().stop();
	    forceMovementActive = true;
	    isRunning2 = false;

	    // Start the process at index 0
	    processWallStep(0, path, direction);
	}

	private void processWallStep(int index, int[][] path, String direction) {
	    if (index >= path.length) {
	        finalizeMove(); 
	        return;
	    }

	    // 1. TRIGGER ANIMATIONS BY STAGE
	    if (index == 0) {
	        startAnimation(2589); // Climb Up
	    } else if (index == 1) {
	        startAnimation(2590); // Crawl Loop
	    } else if (index == path.length - 1) {
	        startAnimation(2591); // Climb Down
	    }

	    // 2. COORDINATE SYNC
	    final int startX = getX();
	    final int startY = getY();
	    final int targetX = path[index][0];
	    final int targetY = path[index][1];

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    this.x1 = startX - baseRegionX;
	    this.y1 = startY - baseRegionY;
	    this.x2 = targetX - baseRegionX;
	    this.y2 = targetY - baseRegionY;

	    // Adjust speed: Crawling is usually slower than jumping stones
	    this.speed1 = 25; 
	    this.speed2 = 70; 
	    
	    switch (direction.toUpperCase()) {
	        case "NORTH": this.direction = 0; break;
	        case "EAST":  this.direction = 1; break;
	        case "SOUTH": this.direction = 2; break;
	        case "WEST":  this.direction = 3; break;
	    }

	    this.updateRequired = true;
	    this.forceMovement = true;
	    this.getPA().requestUpdates();

	    // 3. EVENT DELAY (Matches the speed of the crawl)
	    // Wall crawling is slow, so we use a 3-tick delay per tile
	    World.getWorld().getEventHandler().submit(new Event<Player>("wall_crawl", this, 3) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.getPA().requestUpdates();

	            attachment.processWallStep(index + 1, path, direction);
	            super.stop();
	        }
	    });
	}
	
	/**
	 * Handles picking a lock and forced entry through a door.
	 * @param obX The door's X coordinate.
	 * @param obY The door's Y coordinate.
	 * @param targetX The tile the player will land on.
	 * @param targetY The tile the player will land on.
	 * @param faceDir The direction to face during the walk.
	 */
	public void handleDoorEntry(int obX, int obY, int targetX, int targetY, String faceDir, WorldObject worldObject) {
	    if (isForceMovementActive()) return;

	    // 1. Start the Picking Lock Animation
	    startAnimation(881); // Standard picklock animation
	    getMovementQueue().stop();
	    forceMovementActive = true;
	    DoorDefinition door = DoorHandler.findDoorAt(objectX, objectY, this.getHeight(), worldObject.id);
	    World.getWorld().getEventHandler().submit(new Event<Player>("door_entry", this, 2) {
	        @Override
	        public void execute() {
	            // Find and Open the Door
	            if (door != null) {
	                attachment.sendMessage("You successfully pick the lock and walk through.");
	                attachment.isPickingLock = false;
	                attachment.lockIsPicked = true;
	                startAnimation(820);
	                attachment.getPA().requestUpdates();
	                attachment.setAppearanceUpdateRequired(true);
	            }

	            // Execute the single Force Movement step
	            	DoorHandler.openSingleDoor(attachment, door, 0, 1, 3);
	            attachment.forceWalkTo(targetX, targetY, faceDir);
	            super.stop();
	        }
	    });
	}

	/**
	 * A single-step force walk specifically for walking through objects.
	 */
	private void forceWalkTo(int targetX, int targetY, String faceDir) {
	    int startX = getX();
	    int startY = getY();

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    this.x1 = startX - baseRegionX;
	    this.y1 = startY - baseRegionY;
	    this.x2 = targetX - baseRegionX;
	    this.y2 = targetY - baseRegionY;

	    this.speed1 = 5; // Start immediately
	    this.speed2 = 35; // Walk speed
	    
	    switch (faceDir.toUpperCase()) {
	        case "NORTH": this.direction = 0; break;
	        case "EAST":  this.direction = 1; break;
	        case "SOUTH": this.direction = 2; break;
	        case "WEST":  this.direction = 3; break;
	    }
        startAnimation(820);
	    this.updateRequired = true;
	    this.forceMovement = true;
	    this.getPA().requestUpdates();


	    //setAppearanceUpdateRequired(true);
	    // Finalize the position after 1 tick (standard walk time)
	    World.getWorld().getEventHandler().submit(new Event<Player>("door_sync", this, 2) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.finalizeMove(); // Cleans up flags
	            super.stop();
	        }
	    });
	}
	public void setMove(int[][] path, String direction, int anim, int failAnim, int speed1, int speed2, int endX, int endY, int offset, int agilReq, int noFailLvl, int height) {
		if (isForceMovementActive()) return;

		// 1. Kill existing pathing from the initial click
		getMovementQueue().stop();

		forceMovementActive = true;
		isRunning2 = false;
		setAppearanceUpdateRequired(true);
		this.stopMovement();

		processNextMove(0, path, direction, anim, failAnim, speed1, speed2, endX, endY, offset, agilReq, noFailLvl, height);
	}

	private int getForceMoveDirection(int startX, int startY, int targetX, int targetY) {
		// MUST check diagonals first!
		if (targetX > startX && targetY > startY) return 4; // NORTHEAST
		if (targetX > startX && targetY < startY) return 5; // SOUTHEAST
		if (targetX < startX && targetY < startY) return 6; // SOUTHWEST
		if (targetX < startX && targetY > startY) return 7; // NORTHWEST

		// Cardinal directions fallback
		if (targetY > startY) return 0; // NORTH
		if (targetX > startX) return 1; // EAST
		if (targetY < startY) return 2; // SOUTH
		if (targetX < startX) return 3; // WEST
		return 1; // Default
	}

	private void processNextMove(int index, int[][] path, String direction, int anim, int failAnim, int speed1, int speed2, int endX, int endY, int offset, int agilReq, int noFailLvl, int height) {
		if (index >= path.length) {
			finalizeMove();
			return;
		}

		int reqLevel = agilReq;
		int noFail = noFailLvl;
		final int startX = getX();
		final int startY = getY();
		final int targetX = path[index][0];
		final int targetY = path[index][1];

		int baseRegionX = getLastKnownLocation().getRegionX() * 8;
		int baseRegionY = getLastKnownLocation().getRegionY() * 8;

		this.x1 = startX - baseRegionX;
		this.y1 = startY - baseRegionY;
		this.x2 = targetX - baseRegionX;
		this.y2 = targetY - baseRegionY;

		startAnimation(anim);

		if(anim == 756 || anim == 762 || anim == 1122 || anim == 1122) {
			this.playerStandIndex = anim;
			this.playerWalkIndex = anim;
			this.playerRunIndex = anim;
			this.playerTurn180Index = anim;
			this.playerTurn90CCWIndex = anim;
			this.playerTurn90CWIndex = anim;
			this.playerTurnIndex = anim;
			this.setAppearanceUpdateRequired(true);
			this.getPA().requestUpdates();
		}

		// Set the speeds strictly to what we pass in
		this.speed1 = speed1;
		this.speed2 = speed2;

		switch (direction.toUpperCase()) {
			case "NORTH": this.direction = 0; break;
			case "EAST":  this.direction = 1; break;
			case "SOUTH": this.direction = 2; break;
			case "WEST":  this.direction = 3; break;
			case "NORTHEAST": this.direction = 4; break;
			case "SOUTHEAST": this.direction = 5; break;
			case "SOUTHWEST": this.direction = 6; break;
			case "NORTHWEST": this.direction = 7; break;
		}

		this.updateRequired = true;
		this.forceMovement = true;
		this.getPA().requestUpdates();
		this.setHeight(height);

		// Calculate dynamic server ticks (30 client cycles = 1 tick)
		int tickDelay = Math.max(1, this.speed2 / 30);

		// The physical sync event now perfectly matches the client's visual sliding time
		World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, tickDelay) {
			@Override
			public void execute() {
				attachment.setX(targetX);
				attachment.setY(targetY);
				attachment.getLocation().setZ(height);
				attachment.getLocation().setX(targetX);
				attachment.getLocation().setY(targetY);
				attachment.getPA().requestUpdates();

				if (index < path.length && !attachment.agilitySuccess(reqLevel, noFail)) {
					attachment.handleSwimmingFailure(endX, endY, offset);
					this.stop();
					return;
				}

				attachment.processNextMove(index + 1, path, direction, anim, failAnim, speed1, speed2, endX, endY, offset, agilReq, noFailLvl, height);
				super.stop();
			}
		});
	}
	public boolean agilitySuccess(int reqLevel, int noFailLevel) {
	    if (this.getSkills().getLevel(Skill.AGILITY) >= noFailLevel) {
	        return true; // "Sure-foot" reached
	    }
	    
	    // Calculate chance: Start at 60% success at Req, 100% at NoFail
	    int currentLevel = this.getSkills().getLevel(Skill.AGILITY);
	    double chance = 0.6 + (0.4 * (double)(currentLevel - reqLevel) / (noFailLevel - reqLevel));
	    return Math.random() < chance;
	}
	private List<int[]> generateSwimPath(int startX, int startY, int shoreX, int shoreY, int swimOffset) {
	    List<int[]> path = new ArrayList<>();
	    
	    // 1. Calculate the 'Clearance' line (The middle of the water)
	    // If the shore is North/South, we offset Y. If East/West, we'd offset X.
	    int clearanceY = startY + (shoreY > startY ? swimOffset : -swimOffset);
	    
	    int currentX = startX;
	    int currentY = startY;

	    // Step A: Move into the water to the clearance depth
	    while (currentY != clearanceY) {
	        currentY += (clearanceY > currentY) ? 1 : -1;
	        path.add(new int[]{currentX, currentY});
	    }

	    // Step B: Swim horizontally until aligned with the shore
	    while (currentX != shoreX) {
	        currentX += (shoreX > currentX) ? 1 : -1;
	        path.add(new int[]{currentX, currentY});
	    }

	    // Step C: Swim to the final shore tile
	    while (currentY != shoreY) {
	        currentY += (shoreY > currentY) ? 1 : -1;
	        path.add(new int[]{currentX, currentY});
	    }

	    return path;
	}
	public void setGenericMove(int[][] path, int[] anims, String direction, int speed1, int speed2, int tickDelay, int height) {
	    if (isForceMovementActive()) return;

	    getMovementQueue().stop();
	    forceMovementActive = true;
	    isRunning2 = false;
	    
	    // Set height immediately to prevent walking through rooftop walls
	    this.setHeight(height); 
	    this.getLocation().setZ(height);

	    processGenericStep(0, path, anims, direction, speed1, speed2, tickDelay, height);
	}

	private void processGenericStep(int index, int[][] path, int[] anims, String direction, int speed1, int speed2, int tickDelay, int height) {
	    if (index >= path.length) {
	        finalizeMove();
	        return;
	    }

	    if (index < anims.length && anims[index] != -1) {
	        startAnimation(anims[index]);
	    }

	    final int startX = getX();
	    final int startY = getY();
	    final int targetX = path[index][0];
	    final int targetY = path[index][1];

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    this.x1 = startX - baseRegionX;
	    this.y1 = startY - baseRegionY;
	    this.x2 = targetX - baseRegionX;
	    this.y2 = targetY - baseRegionY;

	    this.speed1 = speed1; 
	    this.speed2 = speed2; 
	    
	    switch (direction.toUpperCase()) {
	        case "NORTH": this.direction = 0; break;
	        case "EAST":  this.direction = 1; break;
	        case "SOUTH": this.direction = 2; break;
	        case "WEST":  this.direction = 3; break;
	    }

	    this.updateRequired = true;
	    this.forceMovement = true;
	    this.getPA().requestUpdates();

	    World.getWorld().getEventHandler().submit(new Event<Player>("generic_move", this, tickDelay) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            // Ensure height stays locked during movement
	            attachment.setHeight(height);
	            attachment.getPA().requestUpdates();

	            attachment.processGenericStep(index + 1, path, anims, direction, speed1, speed2, tickDelay, height);
	            super.stop();
	        }
	    });
	}
	public void handleSwimmingFailure(int shoreX, int shoreY, int swimOffset) {
	    this.forceMovement = false; 
	    startAnimation(2582); 
	    
	    // Set Swimming Anims
	    this.playerStandIndex = 772;
	    this.playerWalkIndex = 772;
	    this.playerRunIndex = 772;
	    this.playerTurn180Index = 772;
	    this.playerTurn90CCWIndex = 772;
	    this.playerTurn90CWIndex = 772;
	    this.playerTurnIndex = 772;
	    this.setAppearanceUpdateRequired(true);

	    // Generate path using the offset (e.g., 2 tiles deep into the water)
	    List<int[]> swimPath = generateSwimPath(getX(), getY(), shoreX, shoreY, swimOffset);

	    processNextSwimStep(0, swimPath);
	}

	private void processNextSwimStep(int index, List<int[]> path) {
	    if (index >= path.size()) {
	        finalizeMove();
	        sendMessage("You pull yourself out of the water.");
	        return;
	    }

	    int startX = getX();
	    int startY = getY();
	    int targetX = path.get(index)[0];
	    int targetY = path.get(index)[1];

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    // Force Movement Visuals
	    this.x1 = startX - baseRegionX;
	    this.y1 = startY - baseRegionY;
	    this.x2 = targetX - baseRegionX;
	    this.y2 = targetY - baseRegionY;
	    
	    // Dynamic Direction: Character faces the tile they are heading to
	    this.direction = getForceMoveDirection(startX, startY, targetX, targetY);

	    this.speed1 = 20; 
	    this.speed2 = 30; 
	    this.forceMovement = true;
	    this.updateRequired = true;
	    this.getPA().requestUpdates();

	    World.getWorld().getEventHandler().submit(new Event<Player>("swim_step", this, 2) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.getPA().requestUpdates();

	            attachment.processNextSwimStep(index + 1, path);
	            super.stop();
	        }
	    });
	}

	public void finalizeMove() {
	    this.forceMovement = false;
	    this.forceMovementActive = false;
	    this.setNeedsPlacement(true);
		if(Item.getItemName(this.playerEquipment[this.playerWeapon]) != null)
	    	getCombat().getPlayerAnimIndex(server.model.items.Item.getItemName(this.playerEquipment[this.playerWeapon]).toLowerCase());
		else {
			c.playerStandIndex = 0x328;
			c.playerTurnIndex = 0x337;
			c.playerWalkIndex = 0x333;
			c.playerTurn180Index = 0x334;
			c.playerTurn90CWIndex = 0x335;
			c.playerTurn90CCWIndex = 0x336;
			c.playerRunIndex = 0x338;
		}
	    setAppearanceUpdateRequired(true);
	    this.getPA().requestUpdates();
	}
	/**
	 * Advanced Force Movement for multi-stage animations (Start -> Move/Slide -> End)
	 */
	/**
	 * Slower Multi-Stage Shortcut (Start -> Crawl -> End)
	 */
	public void setShortCutMovement(int xOffset, int yOffset, int speedOne, int speedTwo, 
	                                String direction, int startAnim, int middleAnim, int endAnim) {
	    if (isForceMovementActive() || forceMovement) {
	        return;
	    }
	    
	    forceMovementActive = true;
	    isRunning2 = false;
	    stopMovement();
	    
	    int targetX = xOffset;
	    int targetY = yOffset;
	    xOffsetWalk = xOffset - getX();
	    yOffsetWalk = yOffset - getY();
	    
	    // Increased moveTicks to allow for a slower visual slide
	    // SpeedTwo / 30 is a rough estimate of client-side ticks
	    int slideDurationTicks = 3; 

	    // STAGE 1: Starting Animation (Plays for 2 ticks before sliding)
	    startAnimation(startAnim);

	    // STAGE 2: Start Force Movement (Delayed by 2 ticks for a slower "get ready" feel)
	    World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, 2) {
	        @Override
	        public void execute() {
	            if (attachment == null || attachment.disconnected) { super.stop(); return; }
	            
	            attachment.playerStandIndex = middleAnim;
	            attachment.playerRunIndex = middleAnim;
	            attachment.playerWalkIndex = middleAnim;
	            
	            attachment.updateRequired = true;
	            attachment.forceMovement = true;
	            attachment.x1 = getLocalX();
	            attachment.y1 = getLocalY();
	            attachment.x2 = getLocalX() + xOffsetWalk;
	            attachment.y2 = getLocalY() + yOffsetWalk;
	            
	            // speedOne: Delay before move. speedTwo: Total move time (Higher = Slower)
	            attachment.speed1 = speedOne; 
	            attachment.speed2 = speedTwo; 
	            
	            switch (direction.toUpperCase()) {
	                case "NORTH": attachment.direction = 0; break;
	                case "EAST":  attachment.direction = 1; break;
	                case "SOUTH": attachment.direction = 2; break;
	                case "WEST":  attachment.direction = 3; break;
	            }
	            attachment.getPA().requestUpdates();
	            super.stop();
	        }
	    });

	    // STAGE 3: Landing / End Animation (Starts after the slide is finished)
	    World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, 2 + slideDurationTicks) {
	        @Override
	        public void execute() {
	            if (attachment == null || attachment.disconnected) { super.stop(); return; }
	            
	            attachment.forceMovement = false;
	            attachment.getPA().movePlayer(targetX, targetY, attachment.HeightLevel);
	            
	            // Play the "Ending" animation (e.g., standing up)
	            startAnimation(endAnim);
	            super.stop();
	        }
	    });

	    // STAGE 4: Final Reset (Wait 2 more ticks for the stand-up animation to finish)
	    World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, 2 + slideDurationTicks + 2) {
	        @Override
	        public void execute() {
	            if (attachment == null || attachment.disconnected) { super.stop(); return; }
	            
	            forceMovementActive = false;
	            // Reset to weapon/default animations
	            attachment.getCombat().getPlayerAnimIndex(Item.getItemName( 
	                attachment.playerEquipment[attachment.playerWeapon]).toLowerCase());
	            
	            attachment.getPA().requestUpdates();
	            super.stop();
	        }
	    });
	}
	public void appendPlayerUpdateBlock(Buffer str) {
		if (!updateRequired && !isChatTextUpdateRequired())
			return; // nothing required
		int updateMask = 0;
		if (forceMovement) {
			updateMask |= 0x400;
		}
		if (mask100update) {
			updateMask |= 0x100;
		}
		if (animationRequest != -1) {
			updateMask |= 8;
		}
		if (forcedChatUpdateRequired) {
			updateMask |= 4;
		}
		if (isChatTextUpdateRequired()) {
			updateMask |= 0x80;
		}
		if (isAppearanceUpdateRequired()) {
			updateMask |= 0x10;
		}
		if (faceUpdateRequired) {
			updateMask |= 1;
		}
		if (FocusPointX != -1) {
			updateMask |= 2;
		}
		if (hitUpdateRequired) {
			updateMask |= 0x20;
		}

		if (hitUpdateRequired2) {
			updateMask |= 0x200;
		}

		if (updateMask >= 0x100) {
			updateMask |= 0x40;
			str.writeByte(updateMask & 0xFF);
			str.writeByte(updateMask >> 8);
		} else {
			str.writeByte(updateMask);
		}

		// now writing the various update blocks itself - note that their
		// order crucial

		if (forceMovement) {
			appendMask400Update(str);
		}
		if (mask100update) {
			appendMask100Update(str);
		}
		if (animationRequest != -1) {
			appendAnimationRequest(str);
		}
		if (forcedChatUpdateRequired) {
			appendForcedChat(str);
		}
		if (isChatTextUpdateRequired()) {
			appendPlayerChatText(str);
		}
		if (faceUpdateRequired) {
			appendFaceUpdate(str);
		}
		if (isAppearanceUpdateRequired()) {
			appendPlayerAppearance(str);
		}
		if (FocusPointX != -1) {
			appendSetFocusDestination(str);
		}
		if (hitUpdateRequired) {
			appendHitUpdate(str);
		}

		if (hitUpdateRequired2) {
			appendHitUpdate2(str);
		}
	}

	public void clearUpdateFlags() {
		updateRequired = false;
		setChatTextUpdateRequired(false);
		setAppearanceUpdateRequired(false);
		setWalkingDirection(Direction.NONE);
		setRunningDirection(Direction.NONE);
		setNeedsPlacement(false);
		hitUpdateRequired = false;
		hitUpdateRequired2 = false;
		forcedChatUpdateRequired = false;
		mask100update = false;
		animationRequest = -1;
		FocusPointX = -1;
		FocusPointY = -1;
		faceUpdateRequired = false;
		forceMovement = false;
		face = 65535;
	}

	public boolean protectingRange() {
		return this.prayerActive[17];
	}

	public boolean protectingMagic() {
		return this.prayerActive[16];
	}

	public boolean protectingMelee() {
		return this.prayerActive[18];
	}

	public ItemAssistant getItems() {
		return itemAssistant;
	}

	private Bank bank;

	public Bank getBank() {
		if (bank == null)
			bank = new Bank(this);
		return bank;
	}

	private BankPin pin;

	public BankPin getBankPin() {
		if (pin == null)
			pin = new BankPin(c);
		return pin;
	}

	public void sendMessage(String s) {
		//synchronized (this) {
			if (getOutStream() != null) {
				outStream.createFrameVarSize(253);
				outStream.writeString(s);
				outStream.endFrameVarSize();
			}
		//}
	}

	public Player getClient(int id) {
		return PlayerHandler.players[id];
	}
	public void sendClan(String name, String message, String clan, int rights) {
		outStream.createFrameVarSizeWord(217);
		outStream.writeString(name);
		outStream.writeString(message);
		outStream.writeString(clan);
		outStream.writeWord(rights);
		outStream.endFrameVarSize();
	}

	public void flushOutStream() {
		if (!session.isConnected() || disconnected || outStream.currentOffset == 0)
			return;

		byte[] temp = new byte[outStream.currentOffset];
		System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
		Packet packet = new Packet(-1, Type.FIXED, ChannelBuffers.wrappedBuffer(temp));
		session.write(packet);
		outStream.currentOffset = 0;
	}

	public Slayer getSlayer() {
		if (slayer == null) {
			slayer = new Slayer(this);
		}
		return slayer;
	}

	public void flushOutput() {

	}

	private ItemAssistant itemAssistant = new ItemAssistant(this);

	public void stopMovement() {
		getMovementQueue().reset();
		getMovementQueue().resetFollowing();
	}
	public boolean getRingOfLifeEffect() {
		return maxCape[0];
	}

	public boolean maxCape[] = new boolean[5];
	public boolean setRingOfLifeEffect(boolean effect) {
		return maxCape[0] = effect;
	}

	public boolean getFishingEffect() {
		return maxCape[1];
	}

	public boolean setFishingEffect(boolean effect) {
		return maxCape[1] = effect;
	}

	public boolean getMiningEffect() {
		return maxCape[2];
	}

	public boolean setMiningEffect(boolean effect) {
		return maxCape[2] = effect;
	}

	public boolean getWoodcuttingEffect() {
		return maxCape[3];
	}

	public boolean setWoodcuttingEffect(boolean effect) {
		return maxCape[3] = effect;
	}
	private int newWalkCmdX[] = new int[walkingQueueSize];
	private int newWalkCmdY[] = new int[walkingQueueSize];
	public int newWalkCmdSteps = 0;
	private boolean newWalkCmdIsRunning = false;
	protected int travelBackX[] = new int[walkingQueueSize];
	protected int travelBackY[] = new int[walkingQueueSize];
	protected int numTravelBackSteps = 0;

	protected int packetsReceived;
	public int packetSize = 0, packetType = -1;

	private Queue<Packet> queuedPackets = new LinkedList<Packet>();

	public void preProcessing() {
		newWalkCmdSteps = 0;
	}

	private long cacheExpirationTime = 4 * 60 * 60 * 1000; // 4 hours in milliseconds
	public int donatorPoints = 0;
	private int timeOutCounter;
	private long lastTargeted;
	public int ACCURATE = 0;
	public int AGGRESSIVE = 2;
	public int CONTROLLED = 3;
	public int DEFENSIVE = 1;
	public int MusicVolume = 50;
	public int EffectVolume = 50;
	public int AreaVolume = 50;
	 public Integer preferredSmithBarId = null;

		public int servantCharges, servantItemFetchId;

public boolean resetChairAnim;
		public int houseServant;
		public int jesterEmotes, lastFairyDistance;
public int combatRingType;
public MapInstance mapInstance;

public boolean inBuildingMode;
public int buildFurnitureId, buildFurnitureX, buildFurnitureY;
public ArrayList<HouseFurniture> houseFurniture = new ArrayList<HouseFurniture>();
private ArrayList<Portal> housePortals = new ArrayList<>();
public RoomData[][][] Rooms = new RoomData[5][13][13];
public RoomData[][][] getHouseRooms() {
	return Rooms;
}
public int pendingRoomType = -1;
public int pendingRoomX = -1;
public int pendingRoomY = -1;
public int pendingRoomZ = -1;
public int pendingRoomRot = 0;
public int pendingRoomDoorDir = -1; // The door you clicked on (LEFT, DOWN, etc)
public ArrayList<HouseFurniture> getHouseFurniture() {
	return houseFurniture;
}
public ArrayList<Portal> getHousePortals() {
	return housePortals;
}
public MapInstance getMapInstance() {
	// TODO Auto-generated method stub
	return mapInstance;
}
public void setConstructionCoords(int[] constructionCoords) {
	this.toConsCoords = constructionCoords;
}
public void setMapInstance(MapInstance mapInstance) {
	this.mapInstance = mapInstance;
}
public int[] getConstructionCoords() {
	// TODO Auto-generated method stub
	return toConsCoords;
}
public int rooms;
public int[] toConsCoords;
public int MaxRooms;
public int glowColor;
public int portalSelected;
public ArrayList<Portal> portals = new ArrayList<Portal>();

public boolean smashVials = false; // Toggled via Barbarian Guard or a custom settings tab
public boolean boughtHouse = true;
public int houseType = 0;
public HouseData house = new HouseData(this);
public HouseData getHouse() {
	return house;
}

public boolean inDungeon()
{
	return mapInstance instanceof HouseDungeon;
}
	// Replace your old long variables with these:
	public int lastFightMode = -1;
	public int statRestoreTicks = 100;
	public int lastVisualSeconds = -1;
	public int singleCombatTicks = 0;
	public int singleCombatTicks2 = 0;
	public void putInCombat(int attacker) {
		this.underAttackBy = attacker;
		this.singleCombatTicks = 8; // Cooldown lasts 8 game ticks
		this.logoutDelay = System.currentTimeMillis(); // Fix this next
	}
	public void process() {
		// 1. Core Event Queue Execution
		eventQueue.forEach(Runnable::run);
		eventQueue.clear();

		// 2. Ground Item Interaction Movement Check
		if (walkingToItem) {
			if (getX() == pItemX && getY() == pItemY || goodDistance(getX(), getY(), pItemX, pItemY, 1)) {
				getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 0, 8);
				World.getWorld().itemHandler.removeGroundItem(this, pItemId, pItemX, pItemY, true);
				walkingToItem = false;
			}
		}

		// 3. Status Timers & Buff Counters
		if (staminaDelay > 0)
			staminaDelay--;
		if (respawnTimer > -6)
			respawnTimer--;
		if (teleTimer > 0)
			handleTeleportTimer();
		if (freezeTimer > -6)
			handleFreezeTimer();
		if (skullTimer > 0)
			handleSkullTimer();

		if (antifireDelay > 0) {
			antifireDelay--;
			if (antifireDelay == 0) {
				antiFirePot = false;
				sendMessage("<col=ef1010>Your resistance to dragonfire has expired!</col>");
			}
		}

		if (SuperantifireDelay > 0 && System.currentTimeMillis() > SuperantifireDelay) {
			SuperantifireDelay = 0;
			sendMessage("<col=ef1010>Your immunity to super dragonfire has expired!</col>");
		}

		// PACKET FILTER: Only send combat style updates when they actually alter
		if (fightMode != lastFightMode) {
			if (fightMode == ACCURATE)
				getPA().sendFrame126("Accurate", 27004);
			else if (fightMode == AGGRESSIVE)
				getPA().sendFrame126("Aggressive", 27004);
			else if (fightMode == CONTROLLED)
				getPA().sendFrame126("Controlled", 27004);
			else if (fightMode == DEFENSIVE)
				getPA().sendFrame126("Defensive", 27004);
			lastFightMode = fightMode;
		}

		// 4. Energy Management
		if (isRunning() && runEnergy <= 0) {
			setRunning(false);
		}

		// 5. Special Attack Regeneration
		if (specRestoreTicks > 0) {
			specRestoreTicks--;
		} else {
			if (specAmount < 10) {
				specAmount += 1;
				getItems().addSpecialBar(playerEquipment[playerWeapon]);
			}
			specRestoreTicks = 50;
		}
		long lastRestore = System.currentTimeMillis();

		if (System.currentTimeMillis() - restoreStatsDelay > 60000) {
			restoreStatsDelay = lastRestore;

			for (int level = 0; level < getSkills().levels.length; level++) {
				if (getSkills().levels[level] < getSkills().getLevelForExp(getSkills().experience[level])) {
					if (level != 5) { // prayer doesn't restore
						getSkills().levels[level] += 1;
						getPA().setSkillLevel(level, getSkills().levels[level],
								getSkills().experience[level]);
						getSkills().sendRefresh();

					} else if(level == 3) {
						getHealth().increase(1);
					}
				} else if (getSkills().levels[level] > getSkills().getLevelForExp(getSkills().experience[level])) {
					getSkills().levels[level] -= 1;
					getPA().setSkillLevel(level, getSkills().levels[level],
							getSkills().experience[level]);
					getSkills().sendRefresh();
				}
			}
		}
		if (getHealth().getAmount() <= 0) {
			isDead = true;
			sendMessage("Oh dear, you are dead!");
			getPA().giveLife();
		}

		// 6. Preaching / Auto-Chat
		if (isPreaching) {
			if (preachTicks-- <= 0) {
				if (!preachQueue.isEmpty()) {
					forcedChat(preachQueue.poll());
					preachTicks = 3;
				} else {
					isPreaching = false;
				}
			}
		}

		// 7. Handlers & External Content Modules
		ApiClient.pollPrivateMessages(this);
		getAgilityHandler().agilityProcess(this);

		if (hitDelay > 0) {
			hitDelay--;
		}
		getCombat().handlePrayerDrain();
		Farming.processCalc(c);

		if (getTheatreInstance() != null) {
			getTheatreInstance().process(this);
		}

		// 8. NMZ Combat Buff Multipliers
		processNmzBuffs();

		// 9. Location-Based Interface & Environment Sweeps
		RegionMusicLocations(c);
		updateMultiSign();
		updateWalkEntities();

		// TICK REFACTOR & PACKET FILTER: Replaces continuous System.currentTimeMillis() string updates
		if (statRestoreTicks > 0) { // not for server stat restore, but for client info boxes updating
			statRestoreTicks--;
		} else {
			statRestoreTicks = 100; // Reset 1 minute cycle
		}

		int visualSeconds = (statRestoreTicks * 600) / 1000;
		if (this.lastVisualSeconds != visualSeconds) {
			c.getPA().sendFrame126(String.format("%02d", visualSeconds), 48668);
			this.lastVisualSeconds = visualSeconds;
		}

		// 10. Boss Instances (Zulrah Venom Checks)
		if (isDead && respawnTimer == -6) {
			getPA().applyDead();
		}

		if (respawnTimer == 7) {
			respawnTimer = -6;
			getPA().giveLife();
		} else if (respawnTimer == 12) {
			respawnTimer--;
			startAnimation(0x900);
			poisonDamage = -1;
		}
		if (Boundary.isIn(this, Zulrah.BOUNDARY) && getZulrahEvent().isInToxicLocation()) {
			appendDamage(1 + Misc.random(3), Hitmark.VENOM);
		}

		// 11. Combat Target Resetting (Pure Ticks Refactor)
		if (singleCombatTicks > 0) {
			singleCombatTicks--;
			if (singleCombatTicks == 0) underAttackBy = 0;
		}
		if (singleCombatTicks2 > 0) {
			singleCombatTicks2--;
			if (singleCombatTicks2 == 0) underAttackBy2 = 0;
		}

		if (targeted != null && distanceToPoint(targeted.getX(), targeted.getY()) > 16) {
			getPA().sendEntityTarget(0, targeted);
			targeted = null;
		}
		if (attackTimer > 0) {
			attackTimer--;
		}

		// 12. Auto-Retaliate & Attack Queueing
		if (attackTimer <= 0) {
			if (npcIndex > 0 && clickNpcType == 0)
				getCombat().attackNpc(npcIndex);
			if (playerIndex > 0)
				getCombat().attackPlayer(playerIndex);
		}
	}

// --- HELPER METHODS TO KEEP PROCESS CLEAN ---

private void processNmzBuffs() {
    if (powerSurgeTimer > 0) {
        powerSurgeTimer--;
        if (specAmount < 10.0) {
            specAmount += 2.0; 
            if (specAmount > 10.0) specAmount = 10.0;
            getItems().updateSpecialBar();
        }
        if (powerSurgeTimer == 0) sendMessage("@yel@Your power surge has ended.");
    }

    if (recurrentDamageTimer > 0) {
        recurrentDamageTimer--;
        if (recurrentDamageTimer == 0) sendMessage("@red@Your recurrent damage has ended.");
    }

    if (zapperTimer > 0) {
        zapperTimer--;
        if (zapperTimer % 4 == 0) {
            boolean zappedSomething = false;
            for (NPC boss : spawnedNmzBosses) {
                if (boss == null || boss.isDead || boss.getHealth().getAmount() <= 0) continue;
                if (boss.npcType == 9207 || boss.npcType == 6291) continue; 
                
                if (withinDistance(boss, 4)) {
                    int zapDamage = Math.min(1 + Misc.random(7), boss.getHealth().getAmount());
                    boss.appendDamage(zapDamage, Hitmark.HIT);
                    boss.gfx0(119); 
                    zappedSomething = true;
                }
            }
            if (zappedSomething) startAnimation(3170); 
        }
        if (zapperTimer == 0) sendMessage("@mag@Your zapper has ended.");
    }
}
private void handleTeleportTimer() {
	teleTimer--;
	if (!isDead) {
		if (teleTimer == 1) {
			teleTimer = 0;
		}
		if (teleTimer == 5) {
			getPA().processTeleport();
		}
		if (teleTimer == 9 && teleGfx > 0) {
			gfx100(teleGfx);
		}
	} else {
		teleTimer = 0;
	}
}

private void handleFreezeTimer() {
	freezeTimer--;
	getPA().sendFrame126("00:" + (freezeTimer > 0 ? freezeTimer : "00"), 48662);
	if (frozenBy > 0) {
		if (PlayerHandler.players[frozenBy] == null) {
			freezeTimer = -1;
			frozenBy = -1;
		} else if (!goodDistance(getX(), getY(), PlayerHandler.players[frozenBy].getX(),
				PlayerHandler.players[frozenBy].getY(), 20)) {
			freezeTimer = -1;
			frozenBy = -1;
		}
	}
}

private void handleSkullTimer() {
	skullTimer--;
	if (skullTimer == 1) {
		isSkulled = false;
		attackedPlayers.clear();
		headIconPk = -1;
		skullTimer = -1;
		getPA().requestUpdates();
	}
}
private void updateMultiSign() {
    boolean inMultiZone = Boundary.isIn(this, Boundary.MULTI) || inMulti();
    if (hasMultiSign && !inMultiZone) {
        getPA().multiWay(-1);
    } else if (!hasMultiSign && inMultiZone) {
        getPA().multiWay(1);
    }
}
	public static void RegionMusicLocations(Player c) {
	    // Example: Varrock region
		//if (c.RegionMusicOn) {
		    RegionMusic.handle(c);
		//}


	    // Add more region checks here...
	}

	@Getter @Setter
	private Optional<WorldObject> interactingObject = Optional.empty();
	public void resetInteractingObject() {
		interactingObject = Optional.empty();
	}
	public boolean isTargetableBy(NPC npc) {
		return !npc.isDead && World.getWorld().getNpcHandler().isAggressive(npc.getIndex(), false) && !npc.underAttack
				&& npc.killerId <= 0 && npc.getHeight() == getHeight();
	}
	@Getter
	private ArrayDeque<Runnable> eventQueue = Queues.newArrayDeque();
	public boolean processQueuedPackets() {
		Packet p = null;
		int processed = 0;
		packetsReceived = 0;
		while ((p = queuedPackets.poll()) != null) {
			if (processed > Config.MAX_INCOMING_PACKETS_PER_CYCLE) {
				break;
			}
			inStream.currentOffset = 0;
			packetType = p.getOpcode();
			packetSize = p.getLength();
			inStream.buffer = p.getPayload().array();
			if (packetType > 0) {
				PacketHandler.processPacket(this, packetType, packetSize);
				processed++;
			}
		}
		return true;
	}
	public Skotizo createSkotizoInstance() {
		Boundary boundary = Boundary.SKOTIZO_BOSSROOM;

		int height = InstancedAreaManager.getSingleton().getNextOpenHeightCust(boundary, 4);

		skotizo = new Skotizo(this, boundary, height);

		return skotizo;
	}
	public void setNewWalkCmdX(int newWalkCmdX[]) {
	this.newWalkCmdX = newWalkCmdX;
}
//
public int[] getNewWalkCmdX() {
	return newWalkCmdX;
}
//
public void setNewWalkCmdY(int newWalkCmdY[]) {
	this.newWalkCmdY = newWalkCmdY;
}
//
public int[] getNewWalkCmdY() {
	return newWalkCmdY;
}
//
public void setNewWalkCmdIsRunning(boolean newWalkCmdIsRunning) {
	this.newWalkCmdIsRunning = newWalkCmdIsRunning;
}
//
public boolean isNewWalkCmdIsRunning() {
	return newWalkCmdIsRunning;
}
	public void postProcessing() {
		if (this.newWalkCmdSteps > 0) {
			int firstX = this.getNewWalkCmdX()[0];
			int firstY = this.getNewWalkCmdY()[0];
			boolean found = false;
			this.numTravelBackSteps = 0;
			int ptr = this.wQueueReadPtr;
			int dir = Misc.direction(this.currentX, this.currentY, firstX, firstY);
			if (dir != -1 && (dir & 1) != 0) {
				do {
					int var13 = dir;
					--ptr;
					if (ptr < 0) {
						ptr = 49;
					}

					this.travelBackX[this.numTravelBackSteps] = this.walkingQueueX[ptr];
					this.travelBackY[this.numTravelBackSteps++] = this.walkingQueueY[ptr];
					dir = Misc.direction(this.walkingQueueX[ptr], this.walkingQueueY[ptr], firstX, firstY);
					if (var13 != dir) {
						found = true;
						break;
					}
				} while (ptr != this.wQueueWritePtr);
			} else {
				found = true;
			}

			if (found) {
				this.wQueueWritePtr = this.wQueueReadPtr;
				this.addToWalkingQueue(this.currentX, this.currentY);
				int i;
				if (dir != -1 && (dir & 1) != 0) {
					for (i = 0; i < this.numTravelBackSteps - 1; ++i) {
						this.addToWalkingQueue(this.travelBackX[i], this.travelBackY[i]);
					}

					i = this.travelBackX[this.numTravelBackSteps - 1];
					int wayPointY2 = this.travelBackY[this.numTravelBackSteps - 1];
					int wayPointX1;
					int wayPointY1;
					if (this.numTravelBackSteps == 1) {
						wayPointX1 = this.currentX;
						wayPointY1 = this.currentY;
					} else {
						wayPointX1 = this.travelBackX[this.numTravelBackSteps - 2];
						wayPointY1 = this.travelBackY[this.numTravelBackSteps - 2];
					}

					dir = Misc.direction(wayPointX1, wayPointY1, i, wayPointY2);
					if (dir != -1 && (dir & 1) == 0) {
						dir >>= 1;
						found = false;
						int x = wayPointX1;
						int y = wayPointY1;

						while (x != i || y != wayPointY2) {
							x += Misc.directionDeltaX[dir];
							y += Misc.directionDeltaY[dir];
							if ((Misc.direction(x, y, firstX, firstY) & 1) == 0) {
								found = true;
								break;
							}
						}

						if (!found) {
							this.println_debug("Fatal: Internal error: unable to determine connection vertex!  wp1=("
									+ wayPointX1 + ", " + wayPointY1 + "), wp2=(" + i + ", " + wayPointY2 + "), "
									+ "first=(" + firstX + ", " + firstY + ")");
						} else {
							this.addToWalkingQueue(wayPointX1, wayPointY1);
						}
					} else {
						this.println_debug("Fatal: The walking queue is corrupt! wp1=(" + wayPointX1 + ", " + wayPointY1
								+ "), " + "wp2=(" + i + ", " + wayPointY2 + ")");
					}
				} else {
					for (i = 0; i < this.numTravelBackSteps; ++i) {
						this.addToWalkingQueue(this.travelBackX[i], this.travelBackY[i]);
					}
				}

				for (i = 0; i < this.newWalkCmdSteps; ++i) {
					this.addToWalkingQueue(this.getNewWalkCmdX()[i], this.getNewWalkCmdY()[i]);
				}
			}

			this.isRunning = this.isNewWalkCmdIsRunning() || this.isRunning2;
		}
	}

	public int getMapRegionX() {
	    return (this.getX() >> 6); // Shifts to the 64-tile region
	}

	public int getMapRegionY() {
	    return (this.getY() >> 6); // Shifts to the 64-tile region
	}

	public int getLocalX() {
		return getX() - 8 * getMapRegionX();
	}

	public int getLocalY() {
		return getY() - 8 * getMapRegionY();
	}

	public int getId() {
		return getIndex();
	}

	public boolean inPcBoat() {
		return getX() >= 2660 && getX() <= 2663 && getY() >= 2638 && getY() <= 2643;
	}

	public boolean inPcGame() {
		return getX() >= 2624 && getX() <= 2690 && getY() >= 2550 && getY() <= 2619;
	}

	public void setHitDiff(int hitDiff) {
		this.hitDiff = hitDiff;
	}

	public void setHitDiff2(int hitDiff2) {
		this.hitDiff2 = hitDiff2;
	}

	public int getHitDiff() {
		return hitDiff;
	}

	public void setHitUpdateRequired(boolean hitUpdateRequired) {
		this.hitUpdateRequired = hitUpdateRequired;
	}

	public void setHitUpdateRequired2(boolean hitUpdateRequired2) {
		this.hitUpdateRequired2 = hitUpdateRequired2;
	}

	public boolean isHitUpdateRequired() {
		return hitUpdateRequired;
	}

	public boolean getHitUpdateRequired() {
		return hitUpdateRequired;
	}

	public boolean getHitUpdateRequired2() {
		return hitUpdateRequired2;
	}

	public void setAppearanceUpdateRequired(boolean appearanceUpdateRequired) {
		this.appearanceUpdateRequired = appearanceUpdateRequired;
	}

	public boolean isAppearanceUpdateRequired() {
		return appearanceUpdateRequired;
	}

	public void setChatTextEffects(int chatTextEffects) {
		this.chatTextEffects = chatTextEffects;
	}

	public int getChatTextEffects() {
		return chatTextEffects;
	}

	public void setChatTextSize(byte chatTextSize) {
		this.chatTextSize = chatTextSize;
	}

	public byte getChatTextSize() {
		return chatTextSize;
	}

	public void setChatTextUpdateRequired(boolean chatTextUpdateRequired) {
		this.chatTextUpdateRequired = chatTextUpdateRequired;
	}

	public boolean isChatTextUpdateRequired() {
		return chatTextUpdateRequired;
	}

	public void setChatText(byte chatText[]) {
		this.chatText = chatText;
	}

	public byte[] getChatText() {
		return chatText;
	}

	public void setChatTextColor(int chatTextColor) {
		this.chatTextColor = chatTextColor;
	}

	public int getChatTextColor() {
		return chatTextColor;
	}


	public void setInStreamDecryption(ISAACCipher inCipher) {
	}

	public void setOutStreamDecryption(ISAACCipher outCipher) {
	}

	public boolean samePlayer() {
		for (int j = 0; j < PlayerHandler.players.length; j++) {
			if (j == getIndex())
				continue;
			if (PlayerHandler.players[j] != null) {
				if (PlayerHandler.players[j].playerName.equalsIgnoreCase(playerName)) {
					disconnected = true;
					return true;
				}
			}
		}
		return false;
	}

	public void dealDamage(int damage) {
		if (teleTimer <= 0)
			playerLevel[3] -= damage;
		else {
			if (hitUpdateRequired)
				hitUpdateRequired = false;
			if (hitUpdateRequired2)
				hitUpdateRequired2 = false;
		}

	}

	public int[] damageTaken = new int[Config.MAX_PLAYERS];
	public int teleotherType;
	public int NightmareDamage, JackokrakenDamage, EventBossDamage;
	public boolean viewingRunePouch, magicDef, usingMelee, usingCross, usingOtherRangeWeapons, usingBallista;
	public int SolakDamage;
	private int sangStaffCharge;

	public void handleHitMask(int damage) {
		if (!hitUpdateRequired) {
			hitUpdateRequired = true;
			hitDiff = damage;
		} else if (!hitUpdateRequired2) {
			hitUpdateRequired2 = true;
			hitDiff2 = damage;
		}
		updateRequired = true;
	}

	public int getSangStaffCharge() {
		return sangStaffCharge;
	}

	public void setSangStaffCharge(int sangStaffCharge) {
		this.sangStaffCharge = sangStaffCharge;
	}


	public DialogueHandler getDH() {
		return dialogueHandler;
	}	
	public BossLog getLog() {
		return bossLog;
	}

	public ShopAssistant getShops() {
		return shopAssistant;
	}

	public void correctCoordinates() {
		if (inPcGame()) {
			getPA().movePlayer(2657, 2639, 0);
		}
		if (inFightCaves()) {
			getPA().movePlayer(getX(), getY(), playerId * 4);
			sendMessage("Your wave will start in 10 seconds.");
			

		}

	}

	public void sendSidebars() {
		setSidebarInterface(0, 2423);//attack style tab
		setSidebarInterface(1, 27101);
		if (characterSummaryTab == 0)
			setSidebarInterface(2, 55790);
		else if (characterSummaryTab == 1)
			setSidebarInterface(2, 638);
		else if (characterSummaryTab == 2)
			setSidebarInterface(2, 54670);
		else
			setSidebarInterface(2, 54670);
		setSidebarInterface(3, 3213);
		setSidebarInterface(4, 1644);//equipment tab
		setSidebarInterface(5, 5608);
		if (playerMagicBook == 0) {
			setSidebarInterface(6, 1151); // modern
		} else {
			if (playerMagicBook == 2) {
				setSidebarInterface(6, 29999); // lunar
			} else {
				setSidebarInterface(6, 12855); // ancient
			}
		}
		correctCoordinates();
		setSidebarInterface(7, 18128);
		setSidebarInterface(8, 5065);
		setSidebarInterface(9, 5715);
		setSidebarInterface(10, 2449);// logout
		setSidebarInterface(11, 904); // settings tab
		setSidebarInterface(12, 147); // emote tab
		setSidebarInterface(13, 50020); // music
		c.getMusic().refreshPlaylist(this, false);
		getPA().sendFrame36(208, characterSummaryTab);
		// getPA().sendFrame36(287, isFixed);
		setSidebarInterface(14, -1);
		setSidebarInterface(15, -1);
		setSidebarInterface(16, -1);
	}
	public void sendSidebars_tut() {
		if(c.tutorialProgress >= 23)
			setSidebarInterface(0, 2423);//attack style tab
			setSidebarInterface(1, -1);
			
		if(c.tutorialProgress >= 4)
			setSidebarInterface(1, 27101);
		
		if(c.tutorialProgress >= 3)
			setSidebarInterface(3, 3213);
		
		if(c.tutorialProgress >= 21)
			setSidebarInterface(4, 1644);//equipment tab
		
		if(c.tutorialProgress >= 1)
			setSidebarInterface(5, 5608);
		
		if(c.tutorialProgress >= 1) {
			if (playerMagicBook == 0) {
				setSidebarInterface(6, 1151); // modern
			} else {
				if (playerMagicBook == 2) {
					setSidebarInterface(6, 29999); // lunar
				} else {
					setSidebarInterface(6, 12855); // ancient
				}
			}
		}
		correctCoordinates();
		if(c.tutorialProgress >= 1)
			setSidebarInterface(7, 18128);
		
		if(c.tutorialProgress >= 1)
			setSidebarInterface(8, 5065);
		
		if(c.tutorialProgress >= 1)
			setSidebarInterface(9, 5715);
		
		if(c.tutorialProgress >= 1)
			setSidebarInterface(10, 2449);// logout
		if(c.tutorialProgress >= 1)
			setSidebarInterface(11, 904); // settings tab
		if(c.tutorialProgress >= 11)
			setSidebarInterface(12, 147); // emote tab
		if(c.tutorialProgress >= 10)
			setSidebarInterface(13, 50020); // music
		 //Music.refreshPlaylist(this, false);
		if(c.tutorialProgress >= 13){
			if (characterSummaryTab == 0)
				setSidebarInterface(2, 55790);
			else if (characterSummaryTab == 1)
				setSidebarInterface(2, 638);
			else if (characterSummaryTab == 2)
				setSidebarInterface(2, 54670);
			else
				setSidebarInterface(2, 54670);
		}
		getPA().sendFrame36(208, characterSummaryTab);
		// getPA().sendFrame36(287, isFixed);
		setSidebarInterface(15, -1);
	}
	private RooftopSeers rooftopSeers = new RooftopSeers();
	private RooftopFalador rooftopFalador = new RooftopFalador();
	private RooftopVarrock rooftopVarrock = new RooftopVarrock();
	private RooftopArdougne rooftopArdougne = new RooftopArdougne();
	private RooftopCanifis rooftopCanifis = new RooftopCanifis();
	private DamageQueueEvent damageQueue = new DamageQueueEvent(this);

	private DialogueHandler dialogueHandler = new DialogueHandler(this);
	private BossLog bossLog = new BossLog();
	private ShopAssistant shopAssistant = new ShopAssistant(this);

	private CollectionLog collectionLog = new CollectionLog(this);
	private Duel duelSession = new Duel(this);
	private AchievementDiary achievementDiary = new AchievementDiary(this);
	private Compost compost = new Compost(this);
	private Allotments allotment = new Allotments(this);
	private Flowers flower = new Flowers(this);
	private Herbs herb = new Herbs(this);
	private Hops hops = new Hops(this);
	private Bushes bushes = new Bushes(this);
	private WoodTrees trees = new WoodTrees(this);
	private FruitTree fruitTrees = new FruitTree(this);
	private SpecialPlantOne specialPlantOne = new SpecialPlantOne(this);
	private SpecialPlantTwo specialPlantTwo = new SpecialPlantTwo(this);
	private ToolLeprechaun toolLeprechaun = new ToolLeprechaun(this);
	private Slayer slayer;
	public boolean protectItem;
	private boolean running;

	public double runningDistanceTravelled;
	public int SlaughterKills;
	public int recoilHits;
	public boolean multiAttacking;
	public int CollLogOpen = 0;
	public int cHallyCount;
	public int cShieldCount;
	public int saeldorCount;
	public int cBowArrowCount;
	public int viggoraCharge;
	public int thammaronCharge;
	public int crawCharge;
	public int serenCharge;
	public long lastDamageCalculation;
	public boolean ignoreDefence;
	public int rangeEndGFX;
	public boolean addingItemsToLootBag;
	public boolean viewingLootBag;
	public String CERBERUS_ATTACK_TYPE;
	public boolean rangeEndGFXHeight;
	public boolean isStuck;
	public long buySlayerTimer;
	public long playTime;
	private boolean trading;

	public DamageQueueEvent getDamageQueue() {
		return damageQueue;
	}

	public CollectionLog getCollectionLog() {
		return collectionLog;
	}

	public AchievementDiary getAD() {
		return achievementDiary;
	}


	public Compost getCompost() {
		return compost;
	}

	public Allotments getAllotment() {
		return allotment;
	}

	public Flowers getFlowers() {
		return flower;
	}

	public Herbs getHerbs() {
		return herb;
	}

	public Hops getHops() {
		return hops;
	}

	public ToolLeprechaun getFarmingTools() {
		return toolLeprechaun;
	}

	public Bushes getBushes() {
		return bushes;
	}

	public WoodTrees getTrees() {
		return trees;
	}

	public FruitTree getFruitTrees() {
		return fruitTrees;
	}

	public SpecialPlantOne getSpecialPlantOne() {
		return specialPlantOne;
	}

	public SpecialPlantTwo getSpecialPlantTwo() {
		return specialPlantTwo;
	}

	public boolean isRunning() {
		return isRunning || (isRunning2 && getMovementQueue().isMoving());
	}

	// supreme void
	public boolean fullVoidSupremeMelee() {
		if (getItems().isWearingItem(11665) && getItems().isWearingItem(33368) && getItems().isWearingItem(33369)
				&& getItems().isWearingItem(8842)) {
			return true;
		}
		if (getItems().isWearingItem(33367) && getItems().isWearingItem(33368) && getItems().isWearingItem(33369)
				&& getItems().isWearingItem(8842)) {
			return true;
		}
		return false;
	}

	public boolean fullVoidSupremeRange() {
		if (getItems().isWearingItem(33367) && getItems().isWearingItem(33368) && getItems().isWearingItem(33369)
				&& getItems().isWearingItem(8842)) {
			return true;
		}
		return false;
	}

	public boolean fullVoidSupremeMage() {
		if (getItems().isWearingItem(33367) && getItems().isWearingItem(33368) && getItems().isWearingItem(33369)
				&& getItems().isWearingItem(8842)) {
			return true;
		}
		return false;
	}

	public void setRunning(boolean running) {
		this.running = running;
		this.isRunning = running;
		this.isRunning2 = running;
		//getPA().sendConfig(173, running ? 1 : 0);
	}

	public int getRunEnergy() {
		return runEnergy;
	}
	/**
	 * Drains run energy based on OSRS formula.
	 * Call this once per tick if the player moves a tile while running.
	 */
	
	public boolean hasChargedRingOfEndurance() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasStaminaEffect() {
		// TODO Auto-generated method stub
		if(hasDrankStam)
			return true;
		else 
			return false;
	}
	public void setStaminaEffect(boolean drank) {
		hasDrankStam = drank;
	}
	public int getWeight() {
		// TODO Auto-generated method stub
		return weight;
	}
	public int weight = 0;
	public void setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
	}

	public Smithing getSmithing() {
		return smithing;
	}
	private final Smithing smithing = new Smithing();
	public void setTrading(boolean trading) {
		this.trading = trading;
	}

	public int counters[] = new int[20], raidsDamageCounters[] = new int[10];
	public int CollLogTimer;
	public boolean newCollItem;
	@Setter
    private long lastContainerSearch;
	public boolean isSelectingQuickprayers;
	public boolean spellSwap;
	public boolean usingArrows;
	public boolean isIdle = false;
	public long switchDelay;
	public int rangeDelay;

	public int getDuelWinsCounter() {
		return counters[5];
	}

	public void setDuelWinsCounter(int counters) {
		this.counters[5] = counters;
	}

	public int getDuelLossCounter() {
		return counters[6];
	}

	public void setDuelLossCounter(int counters) {
		this.counters[6] = counters;
	}

	public boolean isTrading() {
		return this.trading;
	}

	public Duel getDuel() {
		return duelSession;
	}

	public long getLastContainerSearch() {
		return lastContainerSearch;
	}

	public int[] RuneIDS = { 554, 555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 9075, 4694, 4695, 4696,
			4697, 4698, 4699, 7936 };
	private ItemCharge itemcharge = new ItemCharge(this);

	public ItemCharge getCharges() {
		return itemcharge;
	}

	public ActionHandler getActions() {
		return actionHandler;
	}

	private ActionHandler actionHandler = new ActionHandler(this);
	@Getter
    private Runecrafting runecrafting = new Runecrafting(this);

	public void startCurrentTask(int ticksBetweenExecution, CycleEvent event) {
		endCurrentTask();
		currentTask = CycleEventHandler.getSingleton().addEvent(this, event, ticksBetweenExecution);
	}

	@Getter
    private AgilityHandler agilityHandler = new AgilityHandler();

    public void endCurrentTask() {
		if (currentTask != null && currentTask.isRunning()) {
			currentTask.stop();
			currentTask = null;
		}
	}


    /**
     * -- SETTER --
     *  Modifies the current interface open
     *
     *
     * -- GETTER --
     *  The interface that is opened
     *
     @param interfaceOpen the interface id
      * @return the interface id
     */
    @Getter
    @Setter
    private int interfaceOpen;

    @Getter
    private CycleEventContainer currentTask;

	public void openRunePouch() {
		c.getPA().sendFrame34a(41710, -1, 0, 1);
		c.getPA().sendFrame34a(41710, -1, 1, 1);
		c.getPA().sendFrame34a(41710, -1, 2, 1);
		if (c.PouchRune1 > 0)
			c.getPA().sendFrame34a(41710, c.PouchRune1, 0, c.PouchRune1Amt);
		if (c.PouchRune2 > 0)
			c.getPA().sendFrame34a(41710, c.PouchRune2, 1, c.PouchRune2Amt);
		if (c.PouchRune3 > 0)
			c.getPA().sendFrame34a(41710, c.PouchRune3, 2, c.PouchRune3Amt);
		int[] shiftedItems = new int[28];
		int[] shiftedItemsN = new int[28];
		int emptySlot = -1;

		for (int ITEM = 0; ITEM < 28; ITEM++) {
			if (c.playerItems[ITEM] - 1 > 0) {
				shiftedItems[ITEM] = c.playerItems[ITEM];
				shiftedItemsN[ITEM] = c.playerItemsN[ITEM];
				c.getPA().sendFrame34a(41711, -1, ITEM, 1);
			}

			c.getPA().sendFrame34a(41711, shiftedItems[ITEM] - 1, ITEM, shiftedItemsN[ITEM]);
			c.getPA().sendFrame34a(42710, shiftedItems[ITEM] - 1, ITEM, shiftedItemsN[ITEM]);
		}
		c.getRunePouch().syncFromLegacyPouch(c.PouchRune1, c.PouchRune1Amt, c.PouchRune2, c.PouchRune2Amt, c.PouchRune3, c.PouchRune3Amt);

		c.getPA().showInterface(41700);
	}

    public void queueMessage(Packet arg1) {
		packetsReceived++;
		queuedPackets.add(arg1);
	}
	public void lobbyLogout() {
		// --- SAME STRICT COMBAT/DUEL CHECKS ---
		if (!isIdle && underAttackBy2 > 0) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (underAttackBy > 0) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener()
				.getMultiplayerSession(this, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() >= MultiplayerSessionStage.FURTHER_INTERATION) {
				sendMessage("You are not permitted to logout during a duel. If you forcefully logout you will");
				sendMessage("lose all of your staked items, if any, to your opponent.");
				return;
			}
		}

		// --- THE LOBBY DISCONNECT LOGIC ---
		if (System.currentTimeMillis() - logoutDelay > 10000) {
			ApiClient.updatePlayerStatus(playerName, 0); // 0 = offline (they are transitioning)
			PriceChecker.clearConfig(this);

			// SEND CUSTOM PACKET 180 INSTEAD OF 109!
			outStream.createFrame(180);
			flushOutStream();

			if (skotizo != null)
				skotizo.end(DisposeTypes.INCOMPLETE);
			CycleEventHandler.getSingleton().stopEvents(this);
			properLogout = true;
			disconnected = true;
			ConnectedFrom.addConnectedFrom(this, connectedFrom);
		}
	}
	public void logout() {
		if (!isIdle && underAttackBy2 > 0) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (underAttackBy > 0) {
			sendMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener()
				.getMultiplayerSession(this, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST) {
			if (duelSession.getStage().getStage() >= MultiplayerSessionStage.FURTHER_INTERATION) {
				sendMessage("You are not permitted to logout during a duel. If you forcefully logout you will");
				sendMessage("lose all of your staked items, if any, to your opponent.");
				return;
			}
		}
		if (System.currentTimeMillis() - logoutDelay > 10000) {
			ApiClient.updatePlayerStatus(playerName, 0); // 0 = offline
			PriceChecker.clearConfig(this);
			outStream.createFrame(109);
			flushOutStream();
			if (skotizo != null)
				skotizo.end(DisposeTypes.INCOMPLETE);
			CycleEventHandler.getSingleton().stopEvents(this);
			properLogout = true;
			disconnected = true;
			ConnectedFrom.addConnectedFrom(this, connectedFrom);
		}
	}
	public boolean isWithinDistance(Location other, int dist) {
		int deltaX = other.getX() - getX(), deltaY = other.getY() - getY();
		return deltaX <= dist && deltaX >= -dist && deltaY <= dist && deltaY >= -dist;
	}
	public int setPacketsReceived(int packetsReceived) {
		return packetsReceived;
	}

	public int getSkeletalMysticDamageCounter() {
		return raidsDamageCounters[0];
	}
	public int getRaidPoints() {
		return raidPoints;
	}
	public void setRaidPoints(int raidPoints) {
		this.raidPoints = raidPoints;
	}
	public void setSkeletalMysticDamageCounter(int damage) {
		this.raidsDamageCounters[0] = damage;
	}
	public int getPacketsReceived() {
		return packetsReceived;
	}

    @Setter
    @Getter
    private RunePouch runePouch = new RunePouch(this);

	@Getter
    private HerbSack herbSack = new HerbSack(this);
	@Getter
    private GemBag gemBag = new GemBag(this);

    public void setHerbSack(HerbSack herbSack) {
		this.herbSack = herbSack;
	}

    public void setGemBag(GemBag gemBag) {
		this.gemBag = gemBag;
	}
	public void setDragonfireShieldCharge(int charge) {
		this.dragonfireShieldCharge = charge;
	}

	public EventHandler getEventHandler() {
		return currentEvent;
	}

	private EventHandler currentEvent;

    public Shortcuts getAgilityShortcuts() {
		return shortcuts;
	}

	private GnomeAgility gnomeAgility = new GnomeAgility();
	private WildernessAgility wildernessAgility = new WildernessAgility();
	private Shortcuts shortcuts = new Shortcuts();
	private RooftopDraynor rooftopDraynor = new RooftopDraynor();
	private BarbarianAgility barbarianAgility = new BarbarianAgility();
	private Herblore herblore = new Herblore(this);
	private Lighthouse lighthouse = new Lighthouse();
	private Cooking cooking = new Cooking(this);
	private Crafting crafting = new Crafting(this);
	private Prayer prayer = new Prayer(this);


	private ProcessSkillGuides SkillGuides = new ProcessSkillGuides(this);

	public ProcessSkillGuides getSkillGuide() {
		return SkillGuides;
	}

	public Herblore getHerblore() {
		return herblore;
	}

	public Potions getPotions() {
		return potions;
	}

	private Potions potions = new Potions(this);

	public TeleTabs getTabs() {
		return TeleTab;
	}

	private TeleTabs TeleTab = new TeleTabs(this);

	public BarbarianAgility getBarbarianAgility() {
		return barbarianAgility;
	}

	public Lighthouse getLighthouse() {
		return lighthouse;
	}

	public Food getFood() {
		return food;
	}

	private Food food = new Food(this);

	public Crafting getCrafting() {
		return crafting;
	}

	public Cooking getCooking() {
		return cooking;
	}

	public Prayer getPrayer() {
		return prayer;
	}


	public RooftopSeers getRoofTopSeers() {
		return rooftopSeers;
	}

	public RooftopFalador getRoofTopFalador() {
		return rooftopFalador;
	}

	public RooftopVarrock getRoofTopVarrock() {
		return rooftopVarrock;
	}
	public RooftopDraynor getRoofTopDraynor() {
		return rooftopDraynor;
	}

	public RooftopArdougne getRoofTopArdougne() {
		return rooftopArdougne;
	}
	public RooftopCanifis getRoofTopCanifis() {
		return rooftopCanifis;
	}

	public boolean checkCombatDistance(Player attacker, Player target) {
		int distance = Misc.distanceBetween(attacker, target);
		int required_distance = this.getDistanceRequired();
		return (this.usingMagic || this.usingRangeWeapon || this.usingBow || this.autocasting || this.usingBallista)
                && distance <= required_distance || (this.usingMelee && getMovementQueue().isMoving() && distance <= required_distance ? true
                : distance == 1 && (this.freezeTimer <= 0 || this.getX() == target.getX()
                                    || this.getY() == target.getY()));
	}


    @Getter
    private RechargeItems rechargeItems = new RechargeItems(this);

	public void handleTeleportRunes(int runeId1, int runeId2, int runeId3, int amt1, int amt2, int amt3, int teleX, int teleY) {
		int[] ids = { runeId1, runeId2, runeId3 };
		int[] amts = { amt1, amt2, amt3 };

		c.getRunePouch().sendLegacyRuneTypes();
		// Step 1: Check if we have enough across pouch + inventory
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			int required = amts[i];
			if (id <= 0 || required <= 0)
				continue;

			int total = c.getItems().getItemAmount(id);
			if(getItems().playerHasItem(RunePouch.RUNE_POUCH_ID, 1))
				total = c.getRunePouch().getCountInBag(id) + c.getItems().getItemAmount(id);
			else
				total = c.getItems().getItemAmount(id);
			if (total < required) {
				c.sendMessage("You don't have the required runes to teleport.");
				return;
			}
		}

		// Step 2: Consume from pouch first, then inventory
		for (int i = 0; i < ids.length; i++) {
			int id = ids[i];
			int required = amts[i];
			if (id <= 0 || required <= 0)
				continue;

			int fromPouch = Math.min(required, c.getRunePouch().getCountInBag(id));
			if (fromPouch > 0)
				c.getRunePouch().consumeRune(id, fromPouch);

			int remaining = required - fromPouch;
			if (remaining > 0)
				c.getItems().deleteItem(id, remaining);
		}

		c.getPA().startTeleport(teleX, teleY, 0, "modern");
	}
	public void setMove(int[][] path, String direction, int[] anims, int failAnim, int speed1, int endX, int endY, int offset, int agilReq, int noFailLvl, int[] heights, int tickDelay) {
	    if (isForceMovementActive()) return;

	    getMovementQueue().stop();
	    forceMovementActive = true;
	    isRunning2 = false;
	    setAppearanceUpdateRequired(true);
	    this.stopMovement();

	    processNextMove(0, path, direction, anims, failAnim, speed1, endX, endY, offset, agilReq, noFailLvl, heights, tickDelay);
	}

	private void processNextMove(int index, int[][] path, String direction, int[] anims, int failAnim, int speed1, int endX, int endY, int offset, int agilReq, int noFailLvl, int[] heights, int tickDelay) {
	    if (index >= path.length) {
	        finalizeMove();
	        return;
	    }

	    // Determine literal floor plane (Z)
	    int currentPlane = (index < heights.length) ? heights[index] : heights[heights.length - 1];
	    this.setHeight(currentPlane);

	    final int startX = getX();
	    final int startY = getY();
	    final int targetX = path[index][0];
	    final int targetY = path[index][1];

	    int baseRegionX = getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = getLastKnownLocation().getRegionY() * 8;

	    this.x1 = startX - baseRegionX;
	    this.y1 = startY - baseRegionY;
	    this.x2 = targetX - baseRegionX;
	    this.y2 = targetY - baseRegionY;

	    // Load Animation and Auto-Calculate Visual Duration (speed2)
	    int calculatedSpeed2 = 0; 
	    if (index < anims.length && anims[index] != -1) {
	        startAnimation(anims[index]);
	        AnimationDefinition def = AnimationDefinition.forID(anims[index]);
	        if (def != null) {
	            calculatedSpeed2 = def.getDurationTicks(); 
	        }
	    }

	    this.speed1 = speed1;
	    this.speed2 = calculatedSpeed2;
	    
	    // Direction calculation
	    switch (direction.toUpperCase()) {
	        case "NORTH": this.direction = 0; break;
	        case "EAST":  this.direction = 1; break;
	        case "SOUTH": this.direction = 2; break;
	        case "WEST":  this.direction = 3; break;
	    }
	    
	    // Note: ensure startHeight and endHeight are set in the obstacle case 
	    // before the first call or reset here for subsequent steps.

	    this.updateRequired = true;
	    this.forceMovement = true;
	    this.setHeight(heights[index]);
	    this.getPA().requestUpdates();

	    World.getWorld().getEventHandler().submit(new Event<Player>("force_movement", this, tickDelay) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.getLocation().setZ(currentPlane);
	            attachment.getPA().requestUpdates();

	            if (index < path.length && !attachment.agilitySuccess(agilReq, noFailLvl)) {
	                attachment.handleSwimmingFailure(endX, endY, offset);
	                this.stop();
	                return;
	            }

	            attachment.processNextMove(index + 1, path, direction, anims, failAnim, speed1, endX, endY, offset, agilReq, noFailLvl, heights, tickDelay);
	            super.stop();
	        }
	    });
	}
	@Getter
	public Music music = new Music(this);
	@Getter
	public Skilling skilling = new Skilling(this);

	private RaidSession raidSession;

	public RaidSession getRaidSession() {
		return raidSession;
	}

	public void setRaidSession(RaidSession raidSession) {
		this.raidSession = raidSession;
	}
	/**
	 * Chambers of Xeric Login Failsafe
	 */
	public void checkCoXLogin() {
		// Check if the player's coordinates are inside the massive CoX instance boundary
		// Your base is 1280, 5248. This covers the maximum possible grid span.
		if (getX() >= 1280 && getX() <= 2600 && getY() >= 5248 && getY() <= 6500) {

			// If they are in the void, but don't have a valid session...
			if (this.getRaidSession() == null) {
				this.sendMessage("@red@Your raid party has disbanded or the session expired.");

				// Teleport them to the Mount Quidamortem exit (using the coords you set earlier)
				this.getPA().movePlayer(1234, 3569, 0);
			}
		}
	}
	public boolean isInCoXRaid() {
		return raidSession != null;
	}
	public String chatInputType = "";
	public CoxButtonHandler.SortColumn activeCoxSort = CoxButtonHandler.SortColumn.NONE;
	public boolean coxSortDescending = true;
	public RaidParty coxParty = null;
	public void setChatInputType(String coxTotal) {
		chatInputType = coxTotal;
	}

	public enum ChatboxAction {
	    NONE, BOLT_ENCHANT, SAWMILL, SMITHING, FLETCHING, CRAFTING, GLASSBLOWING, SPINNING, LEATHERCRAFTING
	}

	public ChatboxAction activeAction = ChatboxAction.NONE;

	public Zulrah zulrah = new Zulrah(this);

	public long setBestZulrahTime(long bestZulrahTime) {
		return this.bestZulrahTime = bestZulrahTime;
	}

	private ZulrahLostItems lostItemsZulrah;
	private long bestZulrahTime;

	public long getBestZulrahTime() {
		return bestZulrahTime;
	}

	public ZulrahLostItems getZulrahLostItems() {
		if (lostItemsZulrah == null) {
			lostItemsZulrah = new ZulrahLostItems(this);
		}
		return lostItemsZulrah;
	}

	/**
	 * The zulrah event
	 * 
	 * @return event
	 */
	public Zulrah getZulrahEvent() {
		return zulrah;
	}

	@Getter
    private Entity targeted;
	public int getHeightLevel;
	public boolean debugMessage;
	public boolean usingClaws;
	public boolean inSpecMode;
	public int previousDamage;
	public int totalEXP;
	public int lastPlaceHolderWarning;
	public boolean placeHolderWarning;
	public boolean placeHolders;
	public boolean ScrollWheel;
	public boolean transChat;
	public boolean smoothshading;
	public boolean fogToggle;
	public boolean dataorbs;
	public boolean hideRoofs;
	public boolean rebuildNPCList;
	public int WildyEventBossDamage;
	public boolean invincible;
	public long lastAttacked;
	public boolean updateAnnounced;
	public int varp729 = 0;
	public int flaxPatchState = 0;
	public int herbPatchState = 0;
	public void updateMiscellania(int flaxState, int herbState) {
	    // flaxState (0-1), herbState (0-1)
	    varp729 = (flaxState << 16) | (herbState << 17);
	    getPA().sendConfig(729, varp729);
	}
	public void setTargeted(Entity targeted) {
		this.targeted = targeted;
	}

	@Override
	public void appendDamage(int damage, Hitmark h) {
		lastAttacked = System.currentTimeMillis();
		if (damage <= 0) {
			damage = 0;
			h = Hitmark.MISS;
		}
		// --- ABSORPTION POTION INTERCEPT ---
        if (this.nmzAbsorption > 0 && damage > 0) {
            if (damage <= this.nmzAbsorption) {
                // Shield takes all the damage
                this.nmzAbsorption -= damage;

                c.getPA().sendFrame126(""+c.nmzAbsorption, 48681);
                //c.sendMessage("You");
                damage = 0; 
            } else {
                // Shield breaks, remaining damage hits the player
                damage -= this.nmzAbsorption;
                this.nmzAbsorption = 0;
            }
            // Optional: Update an interface here if you have a custom NMZ overlay!
        }
		if (getHealth().getAmount() - damage < 0) {
			damage = getHealth().getAmount();
		}
		if (teleTimer <= 0) {
			if (!invincible)
				getHealth().reduce(damage);
			if (!hitUpdateRequired) {
				hitUpdateRequired = true;
				hitDiff = damage;
				hitmark1 = h;
			} else if (!hitUpdateRequired2) {
				hitUpdateRequired2 = true;
				hitDiff2 = damage;
				hitmark2 = h;
			}
		} else {
			if (hitUpdateRequired) {
				hitUpdateRequired = false;
			}
			if (hitUpdateRequired2) {
				hitUpdateRequired2 = false;
			}
		}
		updateRequired = true;
	}

	public boolean susceptibleTo(HealthStatus status) {
		if (getItems().isWearingItem(12931, playerHat) || getItems().isWearingItem(13199, playerHat)
				|| getItems().isWearingItem(13197, playerHat)) {
			return false;
		}
		return true;
	}

	public Skotizo getSkotizo() {
		return skotizo;
	}
	private Skotizo skotizo = null;
	protected RightGroup rights;
	public long lastWheatPass;
	public int currentRender = 0;
	private boolean Invisible = false;
	public int maxRender = 30;
	public int corpDamage;
	public int theatrePoints;
	public boolean teleporting;
	public int rememberNpcIndex;
	public int lastTeleportX, lastTeleportY, lastTeleportZ;
	public boolean forceMovementActive;
	public boolean updateItems;
	public boolean isFullHelm;
	public boolean isFullMask;
	public boolean isFullBody;
	public long lastMove;
	/**
	 * @return the forceMovement
	 */
	public boolean isForceMovementActive() {
		return forceMovementActive;
	}

	public SmithingInterface getSmithingInt() {
		return smithInt;
	}
	private SmithingInterface smithInt = new SmithingInterface(this);
	public int currentTab;
	public boolean inSafeBox;
	public boolean insertMode = false;
	public int preachingBook;
	public Queue<String> preachQueue = new LinkedList<>();
	public int preachTicks = 0;
	public boolean isPreaching = false;
	public long lastFire;

	/**
	protected RightGroup rights;
	 * Retrieves the rights for this player.
	 * 
	 * @return the rights
	 */
	public RightGroup getRights() {
		if (rights == null) {
			rights = new RightGroup(this, Right.PLAYER);
		}
		return rights;
	}
	/**
	 * Returns the single instance of the {@link NPCDeathTracker} class for this
	 * player.
	 * 
	 * @return the tracker clas
	 */
	private WarriorsGuild warriorsGuild = new WarriorsGuild(this);
	/**
	 * The single {@link WarriorsGuild} instance for this player
	 * 
	 * @return warriors guild
	 */
	public WarriorsGuild getWarriorsGuild() {
		return warriorsGuild;
	}
	private NPCDeathTracker npcDeathTracker = new NPCDeathTracker(this);
	public NPCDeathTracker getNpcDeathTracker() {
		return npcDeathTracker;
	}
	public boolean isInvisible() {
		return Invisible;
	}
	public SkillCapes skillCapes = new SkillCapes(this);
	public long lastStrengthBoost, lastMagicBoost;

	public int swapCount = 0;
	public int pvmPoints;
	public int bossPoints;
	public long lastDropTableSearch;
	public int kbdCount;
	public int vorkathKillCount;
	public List<Integer> searchList;
	public long lastDropTableSelected;
	public int dropSize;
	public int pkp;
	public int dreamTime;
	public boolean isAnimatedArmourSpawned;
	public int lastClickedItem;
	public long lastImpling;
	public long lastPickup;
	public Map<Integer, Integer> songConfigMap = new HashMap<>();

	public final Stopwatch last_trap_layed = new Stopwatch();
	public CITY_MUSIC[] sortedSongs;
	public boolean shuffleMode;
	public boolean hasOverloadBoost;
	public int HeightLevel = 0;
	public long staminaDelay;
	public long lastOverloadBoost;
	public int droppedItem;
	public boolean droppingItem;
	public boolean dropWarning;
	public long cerbDelay;
	public SkillCapes getSkillCapes() {
		return skillCapes;
	}
	public Cerberus createCerberusInstance() {
		Boundary boundary = Boundary.BOSS_ROOM_WEST;

		int height = InstancedAreaManager.getSingleton().getNextOpenHeightCust(boundary, 4);

		cerberus = new Cerberus(this, boundary, height);

		return cerberus;
	}
	public boolean showDropWarning() {
		return dropWarning;
	}
	public boolean inRaidLobby() {//checks to see if player is in the raid lobby
		if (Boundary.isIn(this, Boundary.RAIDS_LOBBY))  {
			return true;
		}
		return false;
	}
	private Trade trade = new Trade(this);
	public Trade getTrade() {
		return trade;
	}
	public long lastTradeRequest;
	private Cerberus cerberus = null;
	private int raidPoints;
	public int diaryAmount;
	public int totalTheatreFinished;
	public int totalRaidsFinished;
	public boolean hasClaimedRaidChest;
	public int[][] raidReward ={{0,0}};
	public int raidCount;
	public int specRestore;
	public long infernoLeaveTimer;
	public int safeBoxSlots;
	public Cerberus getCerberus() {
		return cerberus;
	}
	public Map<String, CollectionProgress> collectionProgress = new HashMap<>();
	public int callistoKills;
	public int KBDKills;
	public int KrakenKills;
	public int KreeKills;
	public int KrilKills;
	public int GraadorKills;
	public int ZilyanaKills;
	public int CorpKills;

	public int barrowsKillCount;
	public int BarrowsKC, BandosKC, bryophytaKills;
	private int openInterface;
	/**
	 * Change whether a warning will be shown when dropping items.
	 * 
	 * @param shown
	 *            True in case a warning must be shown, False otherwise.
	 */
	public void setDropWarning(boolean shown) {
		dropWarning = shown;
	}

	public Barrows getBarrows() {
		return barrows;
	}
	public OreTile getTile() {
		return tile;
	}
	private OreTile tile = new OreTile();
	public int getBarrowsChestCounter() {
		return counters[4];
	}

	public int getGlodDamageCounter() {
		return raidsDamageCounters[3];
	}

	public int getIceQueenDamageCounter() {
		return raidsDamageCounters[4];
	}

	public void setIceQueenDamageCounter(int damage) {
		this.raidsDamageCounters[4] = damage;
	}
	public void setGlodDamageCounter(int damage) {
		this.raidsDamageCounters[3] = damage;
	}
	private Barrows barrows = new Barrows(this);
	public int ethereumCharge;
	public int sireHits;
	public int totalMissedGorillaHits;
	public int hydraAttackCount;
	public int countUntilPoison;
	public ArrayList<int[]> coordinates;
	public long muteEnd;
	public void setBarrowsChestCounter(int counters) {
		this.counters[4] = counters;
	}

	public long lastFarmingStateUpdate;
	public int[] dailyCapeTeleport = new int[23];
	public void setDailyCapeLimit(int skillId, int value) {
			dailyCapeTeleport[skillId] += value;
	}
	public int getDailyCapeLimit(int skillId) {
		return dailyCapeTeleport[skillId];
	}
	public int getWorldId() {
	    return worldID;
	}

	public void setWorldId(int id) {
	    this.worldID = id;
	}
	public String registeredEmail = "";
	public int unreadMessages = 0;
	public boolean isEmailRegistered = false;
	public int daysSinceLastLogin = 0; 
	public int daysSinceRecovChange = 15;
	
	public long lastLoginDate = 0;
	public long accountCreationDate = 0;
		/**
		 * Calculates Account Age and Days Since Last Login based on System Time.
		 */
		public void calculateAccountDates() {
			long currentTime = System.currentTimeMillis();
			long daysInMs = 86400000L; // Milliseconds in a 24-hour day

			// 1. If this is a brand new account, stamp the creation date!
			if (accountCreationDate == 0) {
				accountCreationDate = currentTime;
			}

			// 2. Calculate days since last login
			if (lastLoginDate > 0) {
				daysSinceLastLogin = (int) ((currentTime - lastLoginDate) / daysInMs);
			} else {
				daysSinceLastLogin = 0; // First time logging in
			}

			// 3. Stamp the current time for the NEXT time they log in!
			lastLoginDate = currentTime;
		}

		/**
		 * Use this for Hans in Lumbridge or a ::age command!
		 */
		public int getAccountAgeDays() {
			if (accountCreationDate == 0) return 0;
			return (int) ((System.currentTimeMillis() - accountCreationDate) / 86400000L);
		}
	public boolean unlockedSuperHopper, unlockedRestrictedMine, unlockedBigSack;
	public int payDirtHopperAmt;
	public int payDirtSackAmt = 0; // total count
	//public List<Map<Integer, Integer>> payDirtSackList = new ArrayList<>(); // exact ores
	
	public List<OreStack> payDirtSackList = new ArrayList<>();
	public List<OreStack> payDirtPending = new ArrayList<>();

	public List<OreStack> getPayDirtSack() {
	    return payDirtSackList;
	}	
	public void setPayDirtSack(List<OreStack> ore) {
	    payDirtSackList = ore;
	}
	public boolean MLMUpperOrLower = false;
	public void setEmail(String email) {
		this.registeredEmail = email;
	}
	public String getEmail() {
		return registeredEmail;
	}
	public void addNpcWithPayDirt(NPC npc) {
        if (!npcsWithPayDirt.contains(npc)) {
            npcsWithPayDirt.add(npc);
        }
    }

    public void removeNpcWithPayDirt(NPC npc) {
        npcsWithPayDirt.remove(npc);
    }

    public NPC getNpcByIndex(int index) {
        for (NPC npc : npcsWithPayDirt) {
            if (npc.getIndex() == index) return npc;
        }
        return null;
    }
    public List<NPC> npcsWithPayDirt = new ArrayList<>();
	private StrongHold strongSecure = new StrongHold(this);
	public boolean[] emoteUnlock = new boolean[26];
	public long lastPMPollTime;
	public int skillPoints;
	public NPCDialogue currentDialogue;
	public void stopAnimation() {
		animationRequest = 65535;
		animationWaitCycles = 0;
		updateRequired = true;
	}

	SilkMerchant silk = new SilkMerchant(this);
	public int amtToMake;
	public int[] boltEnchantDisplayed = new int[8]; // unenchanted bolt IDs in order (0..count-1)
	public int boltEnchantCount = 0;                 // how many are shown right now (<= 8)
	private boolean hasDrankStam;
	public SilkMerchant Silk() {
		return silk;
	}
	public void performUriTransform(final Player c) {
	    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
	        int tick = 0;

	        @Override
	        public void execute(CycleEventContainer container) {
	            switch (tick) {
	                case 0: // Tick 1
	                    c.gfx0(86); // high GFX
	                    c.npcId2 = 7311;
	                    c.isNpc = true;
	                    c.updateRequired = true;
	                    c.setAppearanceUpdateRequired(true);
	                    break;
	                case 1: // Tick 2
	                    c.gfx0(1306);
	                    c.startAnimation(7278);
	                    c.npcId2 = 7313;
	                    c.isNpc = true;
	                    c.updateRequired = true;
	                    c.setAppearanceUpdateRequired(true);
	                    break;
	                case 11: // Tick 12
	                    c.startAnimation(4069);
	                    break;
	                case 12: // Tick 13
	                    c.gfx0(678);
	                    c.startAnimation(4071);
	                    break;
	                case 14: // Tick 15
	                    c.gfx0(86); // high GFX
	                    c.startAnimation(65535); // reset animation
	                    c.npcId2 = -1;
	                    c.isNpc = false;
	                    c.updateRequired = true;
	                    c.setAppearanceUpdateRequired(true);
	                    container.stop(); // End the event
	                    break;
	            }
	            tick++;
	        }

	        @Override
	        public void stop() {
	            // Optional: cleanup after transformation ends
	        }
	    }, 1); // Runs every tick
	}
	public StrongHold getSecurity() {
		// TODO Auto-generated method stub
		return strongSecure ;
	}

	public Mining getMining() {
		// TODO Auto-generated method stub
		return mine;
	}
	public Mining mine = new Mining();
	public int barCount = 0;
	public int[] barDisplayed = new int[8];
	public long lastObstacleFail;
	public boolean teleportInsidePOH = false;
	public boolean defaultBuildMode = false;
	public boolean inHouse = false;
	public int objectType;
	public int lastMapRegionX;
	public int lastMapRegionY;
	public boolean updatePOH;
	private int POH_Door_Status;
	public int fairyRingOption1 = 0;
	public int fairyRingOption2 = 0;
	public int fairyRingOption3 = 0;
	public int lastFairyRingX;
	public int lastFairyRingY;
	public int selectedLog;
	public int toMake;
	public List<Item> deathItems = new ArrayList<>();
	public int slot;
	public WorldObject worldObject;
	public boolean isPickingLock;
	public boolean lockIsPicked;
	public int VarrockRooftopLapCount = 0;
	public boolean isRaking;
	public boolean inArdiCC;
	private long lastTimeUpdate;
	public int isInInterface;
	public boolean isBuildingMode() {
		// TODO Auto-generated method stub
		return inBuildingMode;
	}

	public Clan clan;

	public boolean isInsideHouse() {
		// TODO Auto-generated method stub
		return inHouse;
	}
	public void setInHouse(boolean house) {
		if(!house)
			c.setSidebarInterface(11, 904);
		this.inHouse = house;
	}

	public void setHouseStyle(int i) {
		this.houseType = i;
		
	}

	public int getHouseStyle() {
		// TODO Auto-generated method stub
		
		return houseType;
	}

	public void setPOHDoor(int i) {
			this.POH_Door_Status = i;
	}
	public int getPOHDoor() {
		return POH_Door_Status;
	}

	public FancyDressBox getFancyDressBox() {
		// TODO Auto-generated method stub
		return fancyDressBox;
	}
	private final FancyDressBox fancyDressBox = new FancyDressBox();
	private final ArmourCase armourCase = new ArmourCase();
	public boolean seersTeleportUnlocked;
	public boolean seersTeleportDefault;
	public boolean GETeleportUnlocked;
	public boolean GETeleportDefault;
	public boolean isCrafting;
	public long lastBakerTheft = 0;
	public int craftingHideId = -1;
	public ArmourCase getArmourCase() { 
		return armourCase; }
	public QuestManager questManager;
	public boolean millHasGrain;
	public boolean flourInBin;
	public int millHopperType;
	public int flourBinAmount;
	public boolean isWealthyCitizenDistracted;
	public int dodgyNecklaceCharges;
	public boolean hasAuthenticator;
	public boolean receivedCountPrize;
	public String answeringChallengeNpc = "";
    public HouseLocation houseLocation = HouseLocation.RIMMINGTON; // Defaults to Rimmington
	public int cadavaVarpStage;
	public boolean targetMinigame;
	
	
	public boolean addingToNMZCoffer;
	public boolean withdrawFromNMZCoffer;
	public int nmzCoffer = 0; // Money stored in Dom Onion's barrel
    public int nmzPoints = 0; // Reward points for the shop
    
    // Tracks the bosses for their current/next dream
    public java.util.ArrayList<server.model.minigames.NMZBosses> activeNMZBosses = new java.util.ArrayList<>();
	public boolean[] blockedNMZBosses = new boolean[25];
	public boolean inNMZ = false;
	public boolean nmzHardMode = false;
    public int nmzInstanceHeight = 0;
 // NMZ Variables
    public enum NMZMode { PRACTICE, ENDURANCE, RUMBLE }
    public NMZMode nmzMode = NMZMode.RUMBLE; // Defaults to rumble
    public int nmzSpawnDelay = 0; // Pauses the Director to create a respawn delay
    public int enduranceWave = 0; // Tracks which boss to spawn next in Endurance
    public int nmzAbsorption = 0; // Tracks your Absorption shield (Cap: 1000)
    // Tracks the actual NPCs currently alive in the arena
    public java.util.ArrayList<server.model.npcs.NPC> spawnedNmzBosses = new java.util.ArrayList<>();
	public int venomDamage;
	public int nmzFee = 16000;
	// NMZ Power-up Timers (Measured in server ticks: 45s = 75 ticks, 60s = 100 ticks)
    public int powerSurgeTimer = 0;
    public int recurrentDamageTimer = 0;
    public int zapperTimer = 0;
 // NMZ Barrel Storage (Max 255 doses each)
    public int nmzAbsorptionDoses = 0;
    public int nmzOverloadDoses = 0;
    public int nmzSuperMagicDoses = 0;
    public int nmzSuperRangingDoses = 0;
	public int groupingOption = 0;
	public boolean hasTalkedToMurphy;
	public int trawlerContribution = 0;
	public QuestManager getQuestManager() {
		// TODO Auto-generated method stub
		return questManager;
	}

	public boolean hasPetFollowing() {
		// TODO Auto-generated method stub
		return summonId > 0 ? true : false;
	}


	public boolean inLobby = false;

}