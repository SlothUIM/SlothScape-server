package server.model.players.skills.thieving;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.collect.Lists;

import server.model.players.content.Skillcapes.SkillcapePerks;
//import server.content.achievement.AchievementType;
//import server.content.achievement.Achievements;
//import server.content.achievement_diary.ardougne.ArdougneDiaryEntry;
//import server.content.achievement_diary.desert.DesertDiaryEntry;
//import server.content.achievement_diary.falador.FaladorDiaryEntry;
//import server.content.achievement_diary.lumbridge_draynor.LumbridgeDraynorDiaryEntry;
//import server.content.achievement_diary.varrock.VarrockDiaryEntry;
//import server.content.achievement_diary.western_provinces.WesternDiaryEntry;
//import server.content.dailytasks.DailyTasks;
//import server.content.dailytasks.DailyTasks.PossibleTasks;
import server.world.Location;
import server.world.World;
import server.world.objects.GlobalObject;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.objects.Object;
//import server.model.npcs.pets.PetHandler;
//import server.model.npcs.pets.PetHandler.SkillPets;
import server.world.Boundary;
//import server.model.players.GlobalMessages;
import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.combat.Hitmark;
import server.model.players.skills.Skill;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.pets.PetHandler.SkillPets;
import server.clip.Region;
import server.clip.WorldObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.items.Item;
import server.model.items.ItemDefinition;
import server.util.Misc;

/**
 * A representation of the thieving skill. Support for both object and npc actions will be supported.
 * 
 * @author Jason MacKeigan
 * @date Feb 15, 2015, 7:12:14 PM
 */
public class Thieving {
	
	private static int[] rogueOutfit = { 5553, 5554, 5555, 5556, 5557 };

	/**
	 * The managing player of this class
	 */
	private Player player;

	/**
	 * The last interaction that player made that is recorded in milliseconds
	 */
	private long lastInteraction;

	/**
	 * The constant delay that is required inbetween interactions
	 */
	private static final long INTERACTION_DELAY = 1_500L;

	/**
	 * The stealing animation
	 */
	private static final int ANIMATION = 881;

	/**
	 * Constructs a new {@link Thieving} object that manages interactions between players and stalls, as well as players and non playable characters.
	 * 
	 * @param player the visible player of this class
	 */
	public Thieving(final Player player) {
		this.player = player;
	}

	/**
	 * A method for stealing from a stall
	 * 
	 * @param stall the stall being stolen from
	 * @param objectId the object id value of the stall
	 * @param location the location of the stall
	 */
	
	
	public boolean getSuccess(Pickpocket npc) {
	    int level = player.getSkills().getLevel(Skill.THIEVING);
	    
	    // Check for the "100% Success" threshold from your table
	    boolean hasHardDiary = player.getAD().hasArdougneCloak3();
	    boolean hasMedDiaryInArdy = player.getAD().hasArdougneCloak2() && Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY);
	    
	    if (npc.failLevel != -1 && level >= npc.failLevel) {
	        if (hasHardDiary || hasMedDiaryInArdy) {
	            return true; // Guaranteed success per OSRS Wiki
	        }
	    }

	    // Wealthy Citizen Special: 0% fail rate when distracted (handled by a flag usually)
	    if (npc == Pickpocket.WEALTHY_CITIZEN && player.isWealthyCitizenDistracted) {
	        return true;
	    }

	    // Standard Success Formula
	    double chance = 0.55 + ((double) (level - npc.level) / 100.0);
	    
	    // Applying the +10% diary bonus correctly
	    if (hasHardDiary || hasMedDiaryInArdy) chance += 0.10;
	    if (player.getItems().isWearingItem(9777)) chance += 0.10; // Thieving Cape

	    return (Misc.random(100) < (int)(chance * 100));
	}
	
	private boolean hasFullRogue() {
        for (int piece : rogueOutfit) {
            if (!player.getItems().isWearingItem(piece)) {
                return false;
            }
        }
        return true;
    }
	/**
     * The timestamp of when the player's stun will wear off
     */
    private long stunTimer;
    private void handlePickpocketDiary(Pickpocket npc) {
    	switch (npc) {
        case MAN:
        case WOMAN:
            if (Boundary.isIn(player, Boundary.LUMRIDGE_BOUNDARY))
                player.getAD().completeAchievement("Lumbridge&DraynorEasy", "Pickpocket a man or woman in Lumbridge", 5);
            break;
        case MASTER_FARMER:
            if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY))
                player.getAD().completeAchievement("ArdougneMedium", "Pickpocket the master farmer north of East Ardougne", 8);
            else if (Boundary.isIn(player, Boundary.DRAYNOR_BOUNDARY))
                player.getAD().completeAchievement("Lumbridge&DraynorMedium", "Pickpocket Martin the Master Gardener", 8);
            break;
        case GUARD:
            if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY))
                player.getAD().completeAchievement("FaladorMedium", "Pickpocket a Falador guard", 8);
            break;
        case GNOME:
            player.getAD().completeAchievement("ArdougneElite", "Pickpocket a gnome in the Tree Gnome Stronghold", 5);
            break;
        case HERO:
            player.getAD().completeAchievement("KandarinElite", "Successfully pickpocket a Hero", 4);
            break;
        case VYRE:
            // Custom Task or generic tracking
            break;
        case ELF:
            // Custom Task or generic tracking
            break;
		case AL_KHARID_WARRIOR:
			break;
		case BEARDED_POLLNIVNIAN_BANDIT:
			break;
		case CAVE_GOBLIN:
			break;
		case DESERT_BANDIT:
			break;
		case FARMER:
			break;
		case FREMENNIK_CITIZEN:
			break;
		case HAM_FEMALE:
			break;
		case HAM_MALE:
			break;
		case KNIGHT_ARDOUGNE:
			break;
		case MENAPHITE_THUG:
			break;
		case PALADIN:
			break;
		case POLLNIVNIAN_BANDIT:
			break;
		case ROGUE:
			break;
		case TZHAAR_HUR:
			break;
		case VILLAGER:
			break;
		case WARRIOR:
			break;
		case WATCHMAN:
			break;
		case WEALTHY_CITIZEN:
			break;
		default:
			break;
    }
    }
    public void steal(Pickpocket pickpocket, NPC npc) {
        if (System.currentTimeMillis() - lastInteraction < 1200L) return;
        if (System.currentTimeMillis() < stunTimer) {
            player.sendMessage("You are stunned!");
            return;
        }
        
        player.turnPlayerTo(npc.getX(), npc.getY());
        player.startAnimation(ANIMATION);
        lastInteraction = System.currentTimeMillis();

        if (getSuccess(pickpocket)) {
            // --- SUCCESS ---
            handleCoinPouch(pickpocket);
            player.getPA().addSkillXP((int) pickpocket.experience, Skill.THIEVING.getId());
            
            // Trigger Diary Task if applicable
            handlePickpocketDiary(pickpocket);
            
        } else {
            // --- FAILURE ---
            if (hasDodgyNecklace() && Misc.random(3) == 0) { // 25% chance
                handleDodgyNecklace();
                player.sendMessage("You fail to pick the pocket, but your necklace protects you!");
                return; // Exit before stun/damage happens
            }

            // Standard Failure
            npc.turnNpc(player.getX(), player.getY());
            npc.startAnimation(422); 
            player.gfx100(80);
            player.appendDamage(pickpocket.stunDamage, Hitmark.HIT);
            stunTimer = System.currentTimeMillis() + 5400L;
            player.sendMessage("You fail to pick the " + pickpocket.name + "'s pocket.");
        }
    }
    public void applyNumbFingers() {
        int current = player.getSkills().getLevel(Skill.THIEVING);
        player.getSkills().setLevel(current - 1, Skill.THIEVING);
        player.sendMessage("Your fingers have gone numb from the cold metal...");
        
        // Add a task to restore the point after 60 seconds
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                player.getSkills().setLevel( player.getSkills().getLevel(Skill.THIEVING) + 1, Skill.THIEVING);
                container.stop();
            }
        }, 100); // ~60 seconds
    }
	private enum Rarity {
		ALWAYS(0), COMMON(5), UNCOMMON(10), RARE(15), VERY_RARE(25);

		/**
		 * The rarity
		 */
		private final int rarity;

		/**
		 * Creates a new rarity
		 * 
		 * @param rarity the rarity
		 */
		Rarity(int rarity) {
			this.rarity = rarity;
		}
	}
	public enum Pickpocket {
	    // Name, Level, XP, StunDamage, FailLevel, PetChance
	    MAN("Man", 1, 8.0, 1, 51, 10000),
	    WOMAN("Woman", 1, 8.0, 1, 51, 10000),
	    FARMER("Farmer", 10, 14.5, 1, 90, 9800),
	    HAM_FEMALE("Female H.A.M. Member", 15, 18.5, 3, 91, 9500),
	    HAM_MALE("Male H.A.M. Member", 20, 22.5, 3, 91, 9500),
	    WARRIOR("Warrior", 25, 26.0, 2, 93, 10000),
	    AL_KHARID_WARRIOR("Al-Kharid Warrior", 25, 26.0, 2, 93, 10000),
	    VILLAGER("Villager", 30, 8.0, 2, -1, 10000),
	    ROGUE("Rogue", 32, 36.5, 2, 94, 10000),
	    CAVE_GOBLIN("Cave goblin", 36, 40.0, 1, -1, 10000),
	    MASTER_FARMER("Master Farmer", 38, 43.0, 3, 94, 9500),
	    GUARD("Guard", 40, 46.8, 2, 95, 10000),
	    FREMENNIK_CITIZEN("Fremennik Citizen", 45, 65.0, 2, -1, 10000),
	    BEARDED_POLLNIVNIAN_BANDIT("Bearded Pollnivnian Bandit", 45, 65.0, 5, -1, 10000),
	    WEALTHY_CITIZEN("Wealthy Citizen", 50, 96.0, 3, -1, 10000), // Note: 0% fail when distracted
	    DESERT_BANDIT("Desert Bandit", 53, 79.5, 3, 95, 10000),
	    KNIGHT_ARDOUGNE("Knight of Ardougne", 55, 84.3, 3, 95, 10000),
	    POLLNIVNIAN_BANDIT("Pollnivnian Bandit", 55, 84.3, 5, -1, 10000),
	    WATCHMAN("Yanille Watchman", 65, 137.5, 3, -1, 10000),
	    MENAPHITE_THUG("Menaphite Thug", 65, 137.5, 5, -1, 10000),
	    PALADIN("Paladin", 70, 151.8, 3, -1, 10000),
	    GNOME("Gnome", 75, 133.3, 1, -1, 8500),
	    HERO("Hero", 80, 273.3, 4, -1, 7000),
	    VYRE("Vyre", 82, 306.9, 5, -1, 6500),
	    ELF("Elf", 85, 353.3, 5, -1, 6000),
	    TZHAAR_HUR("TzHaar-Hur", 90, 103.5, 4, -1, 5000);

	    public final String name;
	    public final int level, stunDamage, failLevel, petChance;
	    public final double experience;

	    Pickpocket(String name, int level, double experience, int stunDamage, int failLevel, int petChance) {
	        this.name = name;
	        this.level = level;
	        this.experience = experience;
	        this.stunDamage = stunDamage;
	        this.failLevel = failLevel;
	        this.petChance = petChance;
	    }
	}
	public static boolean isWatcher(int npcType) {
	    switch (npcType) {
	        case 3297: // Ardy Knight
	        case 8725: case 8724: // Ardy Bakers
	        case 8728: // Ardy Silk Trader
	        case 5418: // Ardy Market Guard
	        case 3294: // Paladin
	        case 2235: // Standard Guard
	            return true;
	        
	        // Future: Sorceress's Garden Elementals
	        // case ELEMENTAL_ID: return true;
	        
	        default:
	            return false;
	    }
	}	public static boolean isGuard(int npcType) {
	    switch (npcType) {
        case 3297: // Ardy Knight
        case 5418: // Ardy Market Guard
        case 3294: // Paladin
        case 2235: // Standard Guard
            return true;
        
        // Future: Sorceress's Garden Elementals
        // case ELEMENTAL_ID: return true;
        
        default:
            return false;
    }
}
	public boolean isLookingAt(NPC npc, int targetX, int targetY) {
	    int dx = targetX - npc.getX();
	    int dy = targetY - npc.getY();
	    int dir = npc.face; // 0=N, 1=NE, 2=E, 3=SE, 4=S, 5=SW, 6=W, 7=NW

	    // If you are adjacent (1 tile away), the NPC will always spot you 
	    // UNLESS you are directly behind their back tile.
	    if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
	        return !isDirectlyBehind(npc, targetX, targetY);
	    }

	    switch (dir) {
	        case 0: return dy > 0;  // Facing North: Sees anything with a higher Y
	        case 4: return dy < 0;  // Facing South: Sees anything with a lower Y
	        case 2: return dx > 0;  // Facing East: Sees anything with a higher X
	        case 6: return dx < 0;  // Facing West: Sees anything with a lower X
	        
	        // Diagonal vision (Sees the entire 180 degree half-map in that direction)
	        case 1: return dx > 0 || dy > 0; // NE
	        case 7: return dx < 0 || dy > 0; // NW
	        case 3: return dx > 0 || dy < 0; // SE
	        case 5: return dx < 0 || dy < 0; // SW
	    }
	    return false;
	}

	/**
	 * Specifically checks if the player is in the 1-tile "Blind Spot" 
	 * directly behind the NPC.
	 */
	private boolean isDirectlyBehind(NPC npc, int targetX, int targetY) {
	    int dx = targetX - npc.getX();
	    int dy = targetY - npc.getY();
	    int dir = npc.face;

	    switch (dir) {
	        case 0: return dy < 0 && dx == 0; // NPC North, Player South
	        case 4: return dy > 0 && dx == 0; // NPC South, Player North
	        case 2: return dx < 0 && dy == 0; // NPC East, Player West
	        case 6: return dx > 0 && dy == 0; // NPC West, Player East
	        default: return false; 
	    }
	}
	public boolean isWatched(WorldObject stall) {
	    for (NPC npc : NPCHandler.npcs) {
	        if (npc == null || npc.isDead || npc.heightLevel != player.HeightLevel) continue;

	        if (isWatcher(npc.npcType)) {
	            // Rule 1: Same tile is 100% safe (OSRS mechanic)
	            if (npc.getX() == player.getX() && npc.getY() == player.getY()) continue;

	            // Rule 2: Vision distance (Stalls: 5 tiles, Sq'irk Garden: ~7 tiles)
	            if (player.goodDistance(npc.getX(), npc.getY(), player.getX(), player.getY(), 5)) {
	                
	                // Rule 3: Line of Sight (Checks Region flags for walls/objects)
	                if (Region.canProjectileMove(npc.getX(), npc.getY(), player.getX(), player.getY(), npc.heightLevel, 1, 1)) {
	                    
	                    // Rule 4: Peripheral Vision (180 degree check)
	                    if (isLookingAt(npc, player.getX(), player.getY())) {
	                        npc.turnNpc(player.getX(), player.getY());
	                        npc.forceChat("Hey! Get your hands off there!");
	                        if(npc.npcType == 5418 || npc.npcType == 3297) {
	                        	npc.underAttack = true;
	                			npc.killerId = player.getIndex();
	                			player.underAttackBy = npc.getIndex();
	                			player.underAttackBy2 = npc.getIndex();
	                        }
	                        return true;
	                    }
	                }
	            }
	        }
	    }
	    return false;
	}
	public void steal(Stall stall, WorldObject worldObject) {
		int pieces = 1;
		int objectId = worldObject.id;
		for (int aRogueOutfit : rogueOutfit) {
			if (player.getItems().isWearingItem(aRogueOutfit)) {
				pieces += 1;
			}
		}
		if (System.currentTimeMillis() - lastInteraction < INTERACTION_DELAY) {
			//player.sendMessage("You must wait a few more seconds before you can steal again.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You need at least one free slot to steal from this.");
			return;
		}/*
		if (!World.getWorld().getGlobalObjects().exists(objectId, location.getX(), location.getY()) || World.getWorld().getGlobalObjects().exists(4797, location.getX(), location.getY())) {
			player.sendMessage("The stall has been depleted.");
			return;
		}*/
		if (isWatched(worldObject)) {
	        return; 
	    }
		if (player.getSkills().getLevel(Skill.THIEVING) < stall.level) {
			player.sendMessage("You need a thieving level of " + stall.level + " to steal from this.");
			return;
		}
		/*if (Misc.random(100) == 0 && player.getInterfaceEvent().isExecutable()) {
			player.getInterfaceEvent().execute();
			return;
		}*/
		switch (stall) {
		case Tea:
			//player.getDiaryManager().getVarrockDiary().progress(VarrockDiaryEntry.TEA_STALL);
			break;
		case Bakery:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				player.getAD().completeAchievement("ArdougneEasy", "Steal a cake from the East Ardougne market stalls", 2);
			}
			player.lastBakerTheft = System.currentTimeMillis();
			break;
		case Magic:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				//player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_GEM_ARD);
			}
			if (Boundary.isIn(player, Boundary.FALADOR_BOUNDARY)) {
				//player.getDiaryManager().getFaladorDiary().progress(FaladorDiaryEntry.STEAL_GEM_FAL);
			}
			break;
			
		case Silver:
			//DailyTasks.increase(player, PossibleTasks.SILVER_SICKLES);
			break;
		case Scimitar:
			break;
		case Fur:
			if (Boundary.isIn(player, Boundary.ARDOUGNE_BOUNDARY)) {
				//player.getAD().completeAchievement("ArdougneEasy", "Steal a cake from the East Ardougne market stalls", 0, 0, 2);
				//player.getDiaryManager().getArdougneDiary().progress(ArdougneDiaryEntry.STEAL_FUR);
			}
			break;
		default:
			break;
		}
		if(player.goodDistance(worldObject.getX(), worldObject.getY(), player.getX(), player.getY(), 2)) {
			player.turnPlayerTo(worldObject.getX(), worldObject.getY());
			if(objectId == 28823) {
			GlobalObject stallDeplete = new GlobalObject(6944, worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getFace(), 10, stall.getRespawnTicks(), objectId);
				World.getWorld().getGlobalObjects().add(stallDeplete);
			} else {
			GlobalObject stallDeplete = new GlobalObject(634, worldObject.getX(), worldObject.getY(), worldObject.getHeight(), worldObject.getFace(), 10, stall.getRespawnTicks(), objectId);
				World.getWorld().getGlobalObjects().add(stallDeplete);
				
			}
				Item item = stall.getRandomItem();
				ItemDefinition definition = ItemDefinition.forId(item.getId());
		
		 if (Misc.random(stall.petChance) == 1 && !SkillPets.THIEVING.hasPet(player)) {
			 PetHandler.skillPet(player, SkillPets.THIEVING);
		 }
		 int experience = (int) stall.experience;
			player.startAnimation(ANIMATION);

			player.getPA().sendSound(Sound.SOUND_LIST.PICK.getSound(), 0, player.EffectVolume);
			player.getItems().addItem(item.getId(), item.getAmount());
			player.getPA().addSkillXP((int) experience + (experience / 20 * pieces)  , Skill.THIEVING.getId());
			player.sendMessage("You steal a " + definition.getName() + " from the stall.");
			lastInteraction = System.currentTimeMillis();
		}
	}
	public boolean hasDodgyNecklace() {
	    return player.getItems().isWearingItem(19915);
	}

	public void handleDodgyNecklace() {
	    player.dodgyNecklaceCharges--;
	    if (player.dodgyNecklaceCharges <= 0) {
	        player.getItems().deleteEquipment(19915, player.playerAmulet);
	        player.sendMessage("Your dodgy necklace crumbles to dust.");
	        player.dodgyNecklaceCharges = 10; // Reset for the next one
	    } else {
	        player.sendMessage("Your dodgy necklace protects you. It has " + player.dodgyNecklaceCharges + " charges left.");
	    }
	}
	public enum Stall {
	    Veg(Arrays.asList(
	        new StallItem(new Item(1982), 1),   // Tomato
	        new StallItem(new Item(1957), 1)    // Onion
	    ), 1, 16, 1, 10000),

	    Bakery(Arrays.asList(
	        new StallItem(new Item(1891), 13),  // Cake
	        new StallItem(new Item(2309), 5),   // Bread
	        new StallItem(new Item(1901), 2)    // Chocolate slice
	    ), 1, 16, 4, 10000),

	    Crafting(Arrays.asList(
	        new StallItem(new Item(1755), 1),   // Chisel
	        new StallItem(new Item(1592), 1)    // Ring mould
	    ), 1, 16, 5, 10000),

	    Food(Arrays.asList(
	        new StallItem(new Item(315), 1),    // Cooked meat
	        new StallItem(new Item(2142), 1)    // Raw beef
	    ), 1, 16, 4, 10000),

	    General(Arrays.asList(
	        new StallItem(new Item(1931), 1),   // Pot
	        new StallItem(new Item(1925), 1)    // Bucket
	    ), 1, 16, 5, 10000),

	    Tea(Arrays.asList(
	        new StallItem(new Item(712), 1)     // Cup of tea
	    ), 25, 30, 2, 9500),

	    Silk(Arrays.asList(
	        new StallItem(new Item(950), 1)     // Silk
	    ), 20, 24, 5, 9700),

	    Wine(Arrays.asList(
	        new StallItem(new Item(1993), 1),   // Jug of wine
	        new StallItem(new Item(1987), 1)    // Grapes
	    ), 22, 26, 5, 9700),

	    Fruit(Arrays.asList(
	        new StallItem(new Item(1963), 1),   // Banana
	        new StallItem(new Item(1955), 1)    // Apple
	    ), 5, 18, 2, 9900),

	    Seed(Arrays.asList(
	        new StallItem(new Item(5318), 1),   // Potato seed
	        new StallItem(new Item(5319), 1)    // Onion seed
	    ), 27, 32, 2, 9600),

	    Fur(Arrays.asList(
	        new StallItem(new Item(6814), 1),   // Fur
	        new StallItem(new Item(958), 1)     // Spade
	    ), 50, 54, 7, 7500),

	    Fish(Arrays.asList(
	        new StallItem(new Item(331), 1),    // Raw salmon
	        new StallItem(new Item(335), 1)     // Raw trout
	    ), 40, 50, 7, 9000),

	    Crossbow(Arrays.asList(
	        new StallItem(new Item(9174), 1),   // Bronze bolts
	        new StallItem(new Item(9450), 1)    // Mithril bolts
	    ), 55, 58, 5, 8800),

	    Silver(Arrays.asList(
	        new StallItem(new Item(2355), 3),   // Silver bar
	        new StallItem(new Item(442), 2),    // Silver ore
	        new StallItem(new Item(5525), 1)    // Silver sickle
	    ), 50, 204, 19, 9000),

	    Spice(Arrays.asList(
	        new StallItem(new Item(2007), 1),   // Spice
	        new StallItem(new Item(946), 1)     // Knife
	    ), 30, 36, 6, 9300),

	    Magic(Arrays.asList(
	        new StallItem(new Item(1613), 3),   // Sapphire
	        new StallItem(new Item(1615), 1),   // Diamond
	        new StallItem(new Item(1617), 0.5)  // Dragonstone
	    ), 75, 80, 7, 8500),

	    Scimitar(Arrays.asList(
	        new StallItem(new Item(1323), 2),   // Iron scimitar
	        new StallItem(new Item(1325), 1),   // Steel scimitar
	        new StallItem(new Item(1993), 1)    // Jug of wine (filler)
	    ), 90, 100, 19, 8000),

	    Gem(Arrays.asList(
	        new StallItem(new Item(1623), 2),   // Uncut sapphire
	        new StallItem(new Item(1621), 1),   // Uncut emerald
	        new StallItem(new Item(1619), 0.5)  // Uncut ruby
	    ), 65, 75, 60, 8700),

	    Ore(Arrays.asList(
	        new StallItem(new Item(438), 1),    // Tin ore
	        new StallItem(new Item(440), 1),    // Iron ore
	        new StallItem(new Item(453), 0.5)   // Coal
	    ), 45, 60, 30, 8900);

	    private final List<StallItem> items;
	    private final double experience;
	    private final int respawnTicks;
	    private final int level;
	    private final int petChance;

	    Stall(List<StallItem> items, int level, double experience, int respawnTicks, int petChance) {
	        this.items = items;
	        this.level = level;
	        this.experience = experience;
	        this.respawnTicks = respawnTicks;
	        this.petChance = petChance;
	    }

	    public double getExperience() {
	        return experience;
	    }

	    public int getRespawnTicks() {
	        return respawnTicks;
	    }

	    public int getLevel() {
	        return level;
	    }

	    public int getPetChance() {
	        return petChance;
	    }

	    /** Weighted random item selection. */
	    public Item getRandomItem() {
	        double totalWeight = items.stream().mapToDouble(StallItem::getChanceWeight).sum();
	        double random = ThreadLocalRandom.current().nextDouble() * totalWeight;
	        double current = 0;
	        for (StallItem stallItem : items) {
	            current += stallItem.getChanceWeight();
	            if (random <= current) {
	                return stallItem.getItem();
	            }
	        }
	        return items.get(0).getItem(); // fallback
	    }
	}
	public enum Chest {
	    TEN_COIN(13, 7.8, 3, 11732, false, new Item(995, 10)),
	    NATURE_RUNE(28, 25.0, 8, 11734, true, new Item(561, 1), new Item(995, 3)),
	    FIFTY_COIN(43, 125.0, 45, 11731, true, new Item(995, 50)),
	    STEEL_ARROWTY(47, 150.0, 75, 11735, true, new Item(9460, 10)),
	    BLOOD_RUNE(59, 250.0, 120, 11736, true, new Item(565, 2)),
	    ARDOUGNE_CASTLE(72, 500.0, 240, 11737, true, new Item(995, 1000), new Item(1601, 1));

	    public final int level, respawn, objectId;
	    public final double xp;
	    public final boolean trapped;
	    public final Item[] rewards;

	    Chest(int level, double xp, int respawn, int objectId, boolean trapped, Item... rewards) {
	        this.level = level;
	        this.xp = xp;
	        this.respawn = respawn;
	        this.objectId = objectId;
	        this.trapped = trapped;
	        this.rewards = rewards;
	    }
	}

	public void lootChest(Chest chest, WorldObject worldObject, boolean searched) {
	    if (player.getSkills().getLevel(Skill.THIEVING) < chest.level) {
	        player.sendMessage("You need a Thieving level of " + chest.level + " to thieve this.");
	        return;
	    }

	    if (chest.trapped && !searched) {
	        int damage = (int) (player.getHealth().getMaximum() * 0.10); // 10% damage
	        player.appendDamage(damage, Hitmark.HIT);
	        player.sendMessage("You triggered a trap!");
	        return;
	    }

	    player.startAnimation(ANIMATION);
	    // Replace with empty chest
	    GlobalObject empty = new GlobalObject(6439, worldObject.getX(), worldObject.getY(), 
	                         worldObject.getHeight(), worldObject.getFace(), 10, chest.respawn, worldObject.id);
	    World.getWorld().getGlobalObjects().add(empty);

	    for (Item item : chest.rewards) {
	        player.getItems().addItem(item.getId(), item.getAmount());
	    }
	    player.getPA().addSkillXP((int) chest.xp, Skill.THIEVING.getId());
	}
	public void crackSafe(WorldObject worldObject) {
	    if (player.getSkills().getLevel(Skill.THIEVING) < 50) {
	        player.sendMessage("You need a Thieving level of 50 to crack wall safes.");
	        return;
	    }
	    
	    player.startAnimation(ANIMATION);
	    
	    // OSRS Success Formula: Success is more likely with higher Thieving/Agility
	    int successChance = 10 + (player.getSkills().getLevel(Skill.THIEVING) / 2);
	    
	    if (Misc.random(100) < successChance) {
	        int[] gems = {1623, 1621, 1619, 1617}; // Sapphire, Emerald, Ruby, Diamond
	        int gem = gems[Misc.random(gems.length - 1)];
	        player.getItems().addItem(gem, 1);
	        player.getPA().addSkillXP(70, Skill.THIEVING.getId());
	        player.sendMessage("You successfully crack the safe and find a gem!");
	    } else {
	        // Failure: Stun and damage
	        player.appendDamage(Misc.random(2, 5), Hitmark.HIT);
	        player.freezeTimer = 5;
	        player.sendMessage("You trigger a trap and take some damage!");
	    }
	}
	public int getMaxPouches() {
	    if (player.getAD().hasArdougneCloak4()) return 140;
	    if (player.getAD().hasArdougneCloak3()) return 84;
	    if (player.getAD().hasArdougneCloak2()) return 56;
	    return 28; // Default OSRS limit
	}
	public void openAllPouches() {
	    int pouchId = 22521;
	    int amount = player.getItems().getItemAmount(pouchId);
	    
	    if (amount <= 0) {
	        player.sendMessage("You don't have any coin pouches to open.");
	        return;
	    }

	    if (player.getItems().freeSlots() < 1 && !player.getItems().playerHasItem(995)) {
	        player.sendMessage("You need at least one free inventory slot for the coins.");
	        return;
	    }

	    // OSRS logic: The gold inside depends on which NPC was pickpocketed.
	    // If your server doesn't track the 'source' of the pouch, 
	    // we use a weighted average based on the player's Thieving level.
	    int minGold = 10;
	    int maxGold = 50;
	    
	    int level = player.getSkills().getLevel(Skill.THIEVING);
	    
	    if (level >= 80) { // Hero/Elf level
	        minGold = 200; maxGold = 350;
	    } else if (level >= 55) { // Knight level
	        minGold = 50; maxGold = 100;
	    } else if (level >= 25) { // Warrior level
	        minGold = 20; maxGold = 40;
	    }

	    int totalCoins = 0;
	    for (int i = 0; i < amount; i++) {
	        totalCoins += Misc.random(minGold, maxGold);
	    }

	    player.getItems().deleteItem(pouchId, amount);
	    player.getItems().addItem(995, totalCoins);
	    
	    player.startAnimation(3550); // OSRS "Opening Pouch" animation (optional)
	    player.sendMessage("You open " + amount + " coin pouches and find " + totalCoins + " coins.");
	}
	public void handleCoinPouch(Pickpocket pickpocket) {
	    int currentPouches = player.getItems().getItemAmount(22521); // OSRS Pouch ID
	    int max = getMaxPouches();

	    if (currentPouches >= max) {
	        player.sendMessage("You have too many coin pouches. Open them before pickpocketing again!");
	        return;
	    }
	    
	    // 100% chance for 1 pouch, but Rogue Outfit (which you have in your class) 
	    // should double the actual loot inside or the pouches themselves.
	    player.getItems().addItem(22521, 1); 
	}
	public boolean pickDoor(int doorId) {
	    int levelReq = 1;
	    double xp = 0;
	    
	    // Mapping the data you provided
	    switch(doorId) {
	        case 11726: // Ardougne 10-coin door
	            levelReq = 1; xp = 3.8; break;
	        case 1600: // H.A.M. Jail
	            levelReq = 14; xp = 4; break;
	        case 11727: // Nature Rune house
	            levelReq = 16; xp = 15; break;
	        case 11728: // Ardougne Castle door
	            levelReq = 61; xp = 50; break;
	        default: return false; // Not a thievable door
	    }

	    if (player.getSkills().getLevel(Skill.THIEVING) < levelReq) {
	        player.sendMessage("You need a Thieving level of " + levelReq + " to pick this lock.");
	        return false;
	    }

	    player.startAnimation(ANIMATION);
	    if (Misc.random(5) == 0) { // Random chance to fail and get "numb fingers"
	        player.getSkills().setLevel(player.getSkills().getLevel(Skill.THIEVING) - 1, Skill.THIEVING);
	        player.sendMessage("You fumble with the lock and your fingers go numb.");
	        return false;
	    }

	    player.getPA().addSkillXP((int) xp, Skill.THIEVING.getId());
	    player.sendMessage("You successfully pick the lock.");
	    return true; // DoorHandler will now process the forceWalk
	}
}
