package server.model.players;

import org.jboss.netty.channel.Channel;

import server.Config;
import server.model.items.ItemAssistant;
import server.model.items.containers.RunePouch;
import server.model.shops.ShopAssistant;
import server.util.Buffer;
import server.model.npcs.NPCHandler;
import server.model.players.skills.*;
import server.model.players.skills.mining.*;
import server.model.players.skills.smithing.*;
import server.model.players.skills.ProcessSkillGuides;
import server.model.players.skills.farming.Allotments;
import server.model.players.skills.farming.Bushes;
import server.model.players.skills.farming.Compost;
import server.model.players.skills.farming.Flowers;
import server.model.players.skills.farming.FruitTree;
import server.model.players.skills.farming.Herbs;
import server.model.players.skills.farming.Hops;
import server.model.players.skills.farming.ToolLeprechaun;
import server.model.players.skills.farming.SpecialPlantOne;
import server.model.players.skills.farming.SpecialPlantTwo;
import server.model.players.skills.farming.WoodTrees;
import server.model.players.skills.runecraft.Abyss;
import server.model.players.packets.ItemCharge;
import server.model.players.packets.dialogue.DialogueHandler;
import server.model.players.packets.*;
import server.model.players.movements.ladders;
import server.model.items.CollectionLog;
import server.model.players.skills.agility.impl.*;
import server.model.players.skills.agility.impl.rooftop.*;
import server.model.players.skills.crafting.Crafting;

public class Client extends Player {


	public Client(Channel s, int _playerId, String name) {
		super(_playerId, name, s);
		this.session = s;
		synchronized (this) {
			outStream = new Buffer(new byte[Config.BUFFER_SIZE]);
			outStream.currentOffset = 0;
		}
		inStream = new Buffer(new byte[Config.BUFFER_SIZE]);
		inStream.currentOffset = 0;
		buffer = new byte[Config.BUFFER_SIZE];
	}
	public byte buffer[] = null;
	public Buffer inStream = null, outStream = null;
	public Channel session;
	private ItemAssistant itemAssistant = new ItemAssistant(this);
	private ShopAssistant shopAssistant = new ShopAssistant(this);
	private TradeAndDuel tradeAndDuel = new TradeAndDuel(this);
	private PlayerAssistant playerAssistant = new PlayerAssistant(this);
	private CombatAssistant combatAssistant = new CombatAssistant(this);
	private ActionHandler actionHandler = new ActionHandler(this);
	private final BankPin bankPin = new BankPin(this);
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
	private DialogueHandler dialogueHandler = new DialogueHandler(this);
	private CollectionLog collectionLog = new CollectionLog(this);
	private ProcessSkillGuides SkillGuides = new ProcessSkillGuides(this);
	private NPCHandler npcHandler = new NPCHandler();
	private AchievementDiary achievementDiary = new AchievementDiary(this);
	private TeleTabs TeleTab = new TeleTabs(this);
	private ItemCharge itemcharge = new ItemCharge(this);
	private ladders ladder = new ladders(this);
	private Abyss abyss = new Abyss(this);
	private Potions potions = new Potions(this);
	private Pets pets = new Pets();
	private Food food = new Food(this);
	/**
	 * Skill instances
	 */
	 
	private GnomeAgility gnomeAgility = new GnomeAgility();
	private WildernessAgility wildernessAgility = new WildernessAgility();
	private Shortcuts shortcuts = new Shortcuts();

	private BarbarianAgility barbarianAgility = new BarbarianAgility();
	private Lighthouse lighthouse = new Lighthouse();
	
	private Cooking cooking = new Cooking(this);
	private Crafting crafting = new Crafting(this);
	private SmithingInterface smithInt = new SmithingInterface(this);
	private Mining mining = new Mining();
	private final Smithing smithing = new Smithing();
	public int lowMemoryVersion = 0;
	public int timeOutCounter = 0;
	public int returnCode = 2;
	//private Future<?> currentTask;




	/**
	 * Outputs a send packet which is built from the data
	 * params provided towards a connected user client channel.
	 * @param id The identification number of the sound.
	 * @param volume The volume amount of the sound (1-100)
	 * @param delay The delay (0 = immediately 30 = 1/2cycle 60=full cycle) before
	 * the sound plays.
	 */

	/**
	 * Outputs a send packet which is built from the data
	 * params provided towards a connected user client channel.
	 * @param id The identification number of the sound.
	 * @param volume The volume amount of the sound (1-100)
	 */
	public void sendSound(int id, int volume) {
		getPA().sendSound(id, 6, 0, volume);
		
	}

	/**
	 * Outputs a send packet which is built from the data
	 * params provided towards a connected user client channel.
	 * @param id The identification number of the sound.
	 */
	public void sendSound(int id) {
		sendSound(id, 10);//pretty sure it's 100 just double check
		// otherwise it will be 1
	}

	public void sendClan(String name, String message, String clan, int rights) {
		outStream.createFrameVarSizeWord(217);
		outStream.writeString(name);
		outStream.writeString(message);
		outStream.writeString(clan);
		outStream.writeWord(rights);
		outStream.endFrameVarSize();
	}



	public void destruct() {
		/*if (session == null)
			return;
		// PlayerSaving.getSingleton().requestSave(playerId);
		
		getPA().removeFromCW();
		if (inPits)
			Server.fightPits.removePlayerFromPits(playerId);
		if (clanId >= 0)
			Server.clanChat.leaveClan(playerId, clanId);
		Misc.println("[DEREGISTERED]: " + playerName + "");
		HostList.getHostList().remove(session);
		CycleEventHandler.getSingleton().stopEvents(this);
		PriceChecker.clearConfig(this);
		disconnected = true;
		session.close();
		session = null;
		inStream = null;
		outStream = null;
		isActive = false;
		buffer = null;
		super.destruct();*/
	}

	public void sendMessage(String s) {
		synchronized (this) {
			if (getOutStream() != null) {
				outStream.createFrameVarSize(253);
				outStream.writeString(s);
				outStream.endFrameVarSize();
			}
		}
	}

		public static int THICK_SKIN = 0, BURST_OF_STRENGTH = 1, CLARITY_OF_THOUGHT = 2, SHARP_EYE = 3, MYSTIC_WILL = 4, ROCK_SKIN = 5,
		SUPERHUMAN_STRENGTH = 6, IMPROVED_REFLEXES = 7, RAPID_RESTORE = 8, RAPID_HEAL = 9, PROTECT_ITEM = 10, HAWK_EYE = 11,
		MYSTIC_LORE = 12, STEEL_SKIN = 13, ULTIMATE_STRENGTH = 14, INCREDIBLE_REFLEXES = 15, PROTECT_FROM_MAGIC = 16,
		PROTECT_FROM_MISSLES = 17, PROTECT_FROM_MELEE = 18, EAGLE_EYE = 19, MYSTIC_MIGHT = 20, RETRIBUTION = 21, REDEMPTION = 22,
		SMITE = 23, CHIVALRY = 24, PIETY = 25, RAPID_RENEWAL = 26, RIGOUR = 27, AUGURY = 28;
	public void setSidebarInterface(int menuId, int form) {
		synchronized (this) {
			if (getOutStream() != null) {
				outStream.createFrame(71);
				outStream.writeWord(form);
				outStream.writeByteA(menuId);
			}
		}
	}

	public void setDragonfireShieldCharge(int charge) {
		this.dragonfireShieldCharge = charge;
	}

	public int getDragonfireShieldCharge() {
		return dragonfireShieldCharge;
	}

	public void setLastDragonfireShieldAttack(long lastAttack) {
		this.lastDragonfireShieldAttack = lastAttack;
	}

	public long getLastDragonfireShieldAttack() {
		return lastDragonfireShieldAttack;
	}

	public boolean isDragonfireShieldActive() {
		return dragonfireShieldActive;
	}
	public int getToxicStaffOfTheDeadCharge() {
		return toxicStaffOfTheDeadCharge;
	}

	public void setToxicStaffOfTheDeadCharge(int toxicStaffOfTheDeadCharge) {
		this.toxicStaffOfTheDeadCharge = toxicStaffOfTheDeadCharge;
	}


	public synchronized Buffer getInStream() {
		return inStream;
	}

	public synchronized int getPacketType() {
		return packetType;
	}

	public synchronized int getPacketSize() {
		return packetSize;
	}

	public synchronized Buffer getOutStream() {
		return outStream;
	}
	public Mining getMining() {
		return mining;
	}
	public ItemAssistant getItems() {
		return itemAssistant;
	}

	public PlayerAssistant getPA() {
		return playerAssistant;
	}

	public CollectionLog getCollectionLog() {
		return collectionLog;
	}
	public ProcessSkillGuides getSkillGuide() {
		return SkillGuides;
	}
	
	public BankPin getBankPin() {
		return bankPin;
	}
	public DialogueHandler getDH() {
		return dialogueHandler;
	}
	public NPCHandler getNH() {
		return npcHandler;
	}
	public AchievementDiary getAD() {
		return achievementDiary;
	}

	public RunePouch getRunePouch() {
		return runePouch;
	}
 
	private RunePouch runePouch = new RunePouch(this);
	public void setRunePouch(RunePouch runePouch) {
		this.runePouch = runePouch;
	}
	public TeleTabs getTabs() {
		return TeleTab;
	}
	public Abyss getAbyss() {
		return abyss;
	}
	public ItemCharge getCharges() {
		return itemcharge;
	}
	public ladders getLaddersAndStairs() {
		return ladder;
	}

	public ShopAssistant getShops() {
		return shopAssistant;
	}

	public TradeAndDuel getTradeAndDuel() {
		return tradeAndDuel;
	}

	public CombatAssistant getCombat() {
		return combatAssistant;
	}
	public ActionHandler getActions() {
		return actionHandler;
	}


	public Potions getPotions() {
		return potions;
	}

	public Pets getPets() {
		return pets;
	}

	public Food getFood() {
		return food;
	}

	/**
	 * Skill Constructors
	 */

	public GnomeAgility getGnomeAgility() {
		return gnomeAgility;
	}
	public WildernessAgility getWildernessAgility() {
		return wildernessAgility;
	}

	public Shortcuts getAgilityShortcuts() {
		return shortcuts;
	}



	public Lighthouse getLighthouse() {
		return lighthouse;
	}

	public BarbarianAgility getBarbarianAgility() {
		return barbarianAgility;
	}

	public Cooking getCooking() {
		return cooking;
	}

	public Crafting getCrafting() {
		return crafting;
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


	public SmithingInterface getSmithingInt() {
		return smithInt;
	}

	public Smithing getSmithing() {
		return smithing;
	}


}
