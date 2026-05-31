package server.model.players.skills.pets;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;

import com.google.common.collect.ImmutableSet;

import lombok.Getter;
import server.clip.Region;
//import server.discord.DiscordBot;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.world.Boundary;
//import server.model.players.GlobalMessages;
import server.model.players.Player;
import server.model.players.PlayerSave;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.World;

public class PetHandler {

    /**
     * A {@link Set} of {@link Pets} that represent non-playable characters that a
     * player entity can drop and interact with.
     */
    private static final Set<Pets> PETS = Collections.unmodifiableSet(EnumSet.allOf(Pets.class));

    private static final ImmutableSet<Integer> PET_IDS = ImmutableSet.of(12650, 12649, 12651, 12652, 12644, 12645,
            12643, 11995, 15568, 12653, 12655, 13178, 12646, 13179, 13177, 12921, 13181, 12816, 12647);

    public static boolean ownsAll(Player player) {
        int amount = 0;
        for (int pets2 : PET_IDS) {
            if (player.getItems().getItemCount(pets2, false) > 0 || player.summonId == pets2) {
                amount++;
            }
            if (amount == PET_IDS.size()) {
                return true;
            }
        }
        return false;
    }

    public static enum Pets {
    	
		HYDRA_GREEN(22746, 8492, "AlchemicalHydra", 1000, "first"),

		HYDRA_BLUE(22748, 8493, "AlchemicalHydra", 1000, "first"),

		HYDRA_RED(22750, 8494, "AlchemicalHydra", 1000, "first"),

		HYDRA_BLACK(22752, 8495, "Alchemical Hydra", 1000, "first"),

		OLMLET(20851, 7519, "", 1000, "second"),

		GRAARDOR(12650, 6632, "General Graardor", 500, "second"), // item id, npc id, name, drop rate, pickup option
																	// (first click, second click option, etc)

		KREE(12649, 6643, "Kree Arra", 500, "second"),

		ZILLY(12651, 6633, "Commander Zilyana", 500, "second"),

		TSUT(12652, 6634, "Kril Tsutsaroth", 500, "second"),

		PRIME(12644, 6627, "Dagannoth Prime", 500, "second"),

		REX(12645, 6630, "Dagannoth Rex", 500, "second"),

		SUPREME(12643, 6628, "Dagannoth Supreme", 500, "second"),

		CHAOS(11995, 5907, "Chaos Elemental", 500, "first"),

		CHAOS_FANATIC(11995, 4444, "Chaos Fanatic", 500, "first"),

		KBD(12653, 6636, "King Black Dragon", 500, "second"),

		KRAKEN(12655, 6640, "Kraken", 500, "second"),

		CALLISTO(13178, 5558, "Callisto", 500, "second"),

		MOLE(12646, 6651, "Giant Mole", 500, "second"),

		VETION(13179, 5559, "Vetion", 500, "third"),

		VENENATIS(13177, 5557, "Venenatis", 500, "second"),

		DEVIL(12648, 6639, "Thermonuclear Smoke Devil", 500, "second"),

		ZULRAH(12921, 2130, "Zulrah", 600, "second"),

		TZREK_JAD(13225, 5892, "Tztok-Jad", 110, "second"),

		HELLPUPPY(13247, 3099, "Cerberus", 600, "second"),
		
		SKOTOS(21273, 425, "Skotizo", 700, "second"),
		
		ZULRAH2(12921, 2131, "", -1, "second"), 
		
		ZULRAH3(12921, 2132, "", -1, "second"),
		
		HELL_CAT(7582, 1625, "", -1, "first"), 
		
		VORKI(21992, 8029, "Vorkath", 500, "second"),
		
		DEATH_JR_RED(12840, 5568, "Zombie", 800, "first"), 
		
		DEATH_JR_BLUE(12840, 5570, "", -1, "first"),
		
		DEATH_JR_GREEN(12840, 5571, "", -1, "first"), 
		
		DEATH_JR_BLACK(12840, 5569, "", -1, "first"),
		
		POSTIE_PETIE(964, 3291, "", -1, "first"), 
		
		SANTA_JR(9958, 1047, "", -1, "first"),
		
		ANTI_SANTA_JR(9959, 1048, "Anti-Santa", 500, "first"), 
		
		SCORPIA(13181, 5561, "Scorpia", 500, "second"),
		
		DARK_CORE(12816, 388, "Corporeal beast", 500, "second"),
		
		KALPHITE_PRINCESS(12647, 6637, "Kalphite Queen", 500, "third"),
		
		KALPHITE_PRINCESS_TWO(12647, 6638, "", -1, "third"), 
		
		HERON(13320, 6715, "", -1, "second"),
		
		ROCK_GOLEM(13321, 7439, "", -1, "second"), 
		
		ROCK_GOLEM_TIN(21187, 7440, "", -1, "second"),
		
		ROCK_GOLEM_COPPER(21188, 7441, "", -1, "second"), 
		
		ROCK_GOLEM_IRON(21189, 7442, "", -1, "second"),
		
		ROCK_GOLEM_COAL(21192, 7445, "", -1, "second"), 
		
		ROCK_GOLEM_GOLD(21193, 7446, "", -1, "second"),
		
		ROCK_GOLEM_MITHRIL(21194, 7447, "", -1, "second"),
		
		ROCK_GOLEM_ADAMANT(21196, 7449, "", -1, "second"),
		
		ROCK_GOLEM_RUNE(21197, 7450, "", -1, "second"), 
		
		BEAVER(13322, 6717, "", -1, "second"),
		
		KITTEN(1555, 5591, "", -1, "first"),
		
		KITTEN_ONE(1556, 5592, "", -1, "first"),
		
		KITTEN_TWO(1557, 5593, "", -1, "first"), 
		
		KITTEN_THREE(1558, 5594, "", -1, "first"),
		
		KITTEN_FOUR(1559, 5595, "", -1, "first"), 
		
		KITTEN_FIVE(1560, 5596, "", -1, "first"),
		
		RED_CHINCHOMPA(13323, 6718, "", -1, "second"), 
		
		GRAY_CHINCHOMPA(13324, 6719, "", -1, "second"),
		
		BLACK_CHINCHOMPA(13325, 6720, "", -1, "second"), 
		
		GOLD_CHINCHOMPA(13326, 6721, "", -1, "second"),
		
		GIANT_SQUIRREL(20659, 7351, "", -1, "second"), 
		
		TANGLEROOT(20661, 7352, "", -1, "second"),
		
		ROCKY(20663, 7353, "", -1, "second"), 
		
		RIFT_GUARDIAN_FIRE(20665, 7354, "", -1, "second"),
		
		RIFT_GUARDIAN_AIR(20667, 7355, "", -1, "second"), 
		
		RIFT_GUARDIAN_MIND(20669, 7356, "", -1, "second"),
		
		RIFT_GUARDIAN_WATER(20671, 7357, "", -1, "second"), 
		
		RIFT_GUARDIAN_EARTH(20673, 7358, "", -1, "second"),
		
		RIFT_GUARDIAN_BODY(20675, 7359, "", -1, "second"), 
		
		RIFT_GUARDIAN_COSMIC(20677, 7360, "", -1, "second"),
		
		RIFT_GUARDIAN_CHAOS(20679, 7361, "", -1, "second"), 
		
		RIFT_GUARDIAN_NATURE(20681, 7362, "", -1, "second"),
		
		RIFT_GUARDIAN_LAW(20683, 7363, "", -1, "second"), 
		
		RIFT_GUARDIAN_DEATH(20685, 7364, "", -1, "second"),
		
		RIFT_GUARDIAN_SOUL(20687, 7365, "", -1, "second"),
		
		RIFT_GUARDIAN_ASTRAL(20689, 7366, "", -1, "second"),
		
		RIFT_GUARDIAN_BLOOD(20691, 7367, "", -1, "second"), 
		
		ABYSSAL_ORPHAN(13262, 5883, "", -1, "second"),
		
		BLOODHOUND(19730, 6296, "", -1, "second"), 
		
		PHOENIX(20693, 7368, "", -1, "second"),
		
		PUPPADILE(22376, 8201, "", -1, "second"), 
		
		TEKTINY(22378, 8202, "", -1, "second"),
		
		VANGUARD(22380, 8203, "", -1, "second"),
		
		VASA_MINIRO(22382, 8204, "", -1, "second"),
		
		VESPINA(22384, 8200, "", -1, "second"), 
		
		LIL_ZIK(22473, 8337, "", -1, "second")
		
		//MINI_JUSTICIAR(33802, 3379, "", -1, "first"),
		
		//MINI_DERWEN(33801, 3385, "", -1, "first"),
        
		//MINI_PORAZDIR(33803, 3380, "", -1, "first"),
		//
		//SHADOW_BEAST(33804, 3381, "Shadow Beast", 500, "first"),
		
		//EDDY(33873, 3389, "", -1, "first"),
		
		//SUNERWATT(33874, 3387, "", -1, "first"),
		
		//ELDER_TROLL(33885, 3391, "", -1, "first"),
		
		//MUTATED_RAT(33886, 3392, "", -1, "first"),
		
		//UNDEAD_WARRIOR(33887, 3393, "", -1, "first"),
		
		//SHADED_BEAST(33888, 3394, "", -1, "first"),
		
		//DARK_CENTUAR(33889, 3395, "", -1, "first")
		
        ;

        private final int itemId;

        private final int npcId;

        private final String parent;

        private final int droprate;

        private final String pickupOption;

        private Pets(int itemId, int npcId, String parent, int droprate, String pickupOption) {
            this.itemId = itemId;
            this.npcId = npcId;
            this.parent = parent;
            this.droprate = droprate;
            this.pickupOption = pickupOption;
        }

        public int getNpcId() {
        	return npcId;
        }
        
    }

    public static Pets forItem(int id) {
        for (Pets t : Pets.values()) {
            if (t.itemId == id) {
                return t;
            }
        }
        return null;
    }

    public static Pets forNpc(int id) {
        for (Pets t : Pets.values()) {
            if (t.npcId == id) {
                return t;
            }
        }
        return null;
    }

    public static boolean isPet(int npcId) {
        for (Pets t : Pets.values()) {
            if (t.npcId == npcId) {
                return true;
            }
        }
        return false;
    }

    public static String getOptionForNpcId(int npcId) {
        return forNpc(npcId).pickupOption;
    }

    public static int getItemIdForNpcId(int npcId) {
        return forNpc(npcId).itemId;
    }

    public static int getNPCIdForItemId(int itemId) {
        return forItem(itemId).npcId;
    }

    public static boolean spawnable(Player player, Pets pet, boolean ignore) {
        if (pet == null) {
            return false;
        }

        if (player.hasNpc && !ignore) {
            return false;
        }

        if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            player.sendMessage("You cannot drop your pet here.");
            return false;
        }

        if (!player.getItems().playerHasItem(pet.itemId) && !ignore) {
            return false;
        }
        return true;
    }

    public static void spawn(Player player, Pets pet, boolean ignore, boolean ignoreAll) {
        if (!ignoreAll) {
            if (!spawnable(player, pet, ignore)) {
                return;
            }
        }
        int offsetX = 0;
        int offsetY = 0;
        if (Region.getClipping(player.getX() - 1, player.getY(), player.getHeight(), -1, 0)) {
            offsetX = -1;
        } else if (Region.getClipping(player.getX() + 1, player.getY(), player.getHeight(), 1, 0)) {
            offsetX = 1;
        } else if (Region.getClipping(player.getX(), player.getY() - 1, player.getHeight(), 0, -1)) {
            offsetY = -1;
        } else if (Region.getClipping(player.getX(), player.getY() + 1, player.getHeight(), 0, 1)) {
            offsetY = 1;
        }

        if (pet.itemId == 12840 && !ignore) {
            player.getItems().deleteItem2(pet.itemId, 1);
            player.hasNpc = true;
            player.summonId = pet.itemId;
            PlayerSave.saveGame(player);
            int randomDeath = Misc.random(3);
            switch (randomDeath) {
                case 0:
                    World.getWorld().getNpcHandler().spawnNpc3(player, 5568, player.getX() + offsetX, player.getY() + offsetY,
                            player.getHeight(), 0, 0, 0, 0, 0, true, false, true);
                    break;

                case 1:
                    World.getWorld().getNpcHandler().spawnNpc3(player, 5569, player.getX() + offsetX, player.getY() + offsetY,
                            player.getHeight(), 0, 0, 0, 0, 0, true, false, true);
                    break;

                case 2:
                    World.getWorld().getNpcHandler().spawnNpc3(player, 5570, player.getX() + offsetX, player.getY() + offsetY,
                            player.getHeight(), 0, 0, 0, 0, 0, true, false, true);
                    break;

                case 3:
                    World.getWorld().getNpcHandler().spawnNpc3(player, 5571, player.getX() + offsetX, player.getY() + offsetY,
                            player.getHeight(), 0, 0, 0, 0, 0, true, false, true);
                    break;
            }
        } else {
            if (!ignoreAll) {
                player.getItems().deleteItem2(pet.itemId, 1);
            }
            player.hasNpc = true;
            player.summonId = pet.itemId;
            PlayerSave.saveGame(player);
            World.getWorld().getNpcHandler().spawnNpc3(player, pet.npcId, player.getX() + offsetX, player.getY() + offsetY,
                    player.getHeight(), 0, 0, 0, 0, 0, true, false, true);
            if (!ignore) {
            }
        }
}

    public static boolean pickupPet(Player player, int npcId, boolean item) {
        Pets pets = forNpc(npcId);
        if (pets != null) {
            int itemId = pets.itemId;
            if (!item) {
                NPCHandler.npcs[player.rememberNpcIndex].setX(0);
                NPCHandler.npcs[player.rememberNpcIndex].setY(0);
                NPCHandler.npcs[player.rememberNpcIndex] = null;
                player.summonId = -1;
                player.hasNpc = false;
                return true;
            } else {
                if (NPCHandler.npcs[player.rememberNpcIndex].spawnedBy == player.getIndex()) {
                    if (player.getItems().freeSlots() > 0) {
                        NPCHandler.npcs[player.rememberNpcIndex].setX(0);
                        NPCHandler.npcs[player.rememberNpcIndex].setY(0);
                        NPCHandler.npcs[player.rememberNpcIndex] = null;
                        player.startAnimation(827);
                        player.getItems().addItem(itemId, 1);
                        player.summonId = -1;
                        player.hasNpc = false;
                        player.sendMessage("You pick up your pet.");
                        return true;
                    } else {
                        player.sendMessage("You do not have enough inventory space to do this.");
                        return false;
                    }
                } else {
                    player.sendMessage("This is not your pet.");
                    return false;
                }
            }
        }
        return false;
    }

	public static void skillPet(Player player, SkillPets pet) {
		Skill skill = pet.getSkill();
		String petName = pet.getPetName();
		int petID = pet.getItemId();

		//GlobalMessages.send(player.playerName + " has received the " + skill + " skill pet, " + petName, GlobalMessages.MessageType.LOOT);
		player.getItems().addItemUnderAnyCircumstance(petID, 1);

	}
	@Getter
    public enum SkillPets {
    	AGILITY(Skill.AGILITY, "Squirrel", 20659),
    	FARMING(Skill.FARMING, "Tangleroot", 20661),
    	FIREMAKING(Skill.FIREMAKING,"Phoenix",20693),
    	HUNTERGREY(Skill.HUNTER, "Grey Chinchompa", 13324),
    	HUNTERRED(Skill.HUNTER, "Red Chinchompa", 13325),
    	HUNTERBLACK(Skill.HUNTER, "Black Chinchompa", 13325),
    	HUNTERGOLD(Skill.HUNTER, "Golden Chinchompa", 13326),
    	MINING(Skill.MINING, "Rock Golem", 13321),
    	RUNECRAFTING(Skill.RUNECRAFTING, "Rift Guardian", 20667),
    	THIEVING(Skill.THIEVING, "Rocky", 20663),
    	WOODCUTTING(Skill.WOODCUTTING,"Beaver", 13322)
    	;
    	
    	private SkillPets (Skill skill, String petName, int petID) {
    		this.skill = skill;
    		this.petName = petName;
    		this.itemId = petID;
    	}
    	private Skill skill;
    	private String petName;
    	private int itemId;
    	
		public boolean hasPet(Player player) {
			return player.getItems().getItemCount(itemId, false) > 0 || player.summonId == itemId;
		}
    	
    }

    public static void receive(Player player, NPC npc) {
        if (npc == null) {
            return;
        }

        Optional<Pets> pet = PETS.stream().filter(p -> p.parent.equalsIgnoreCase(npc.getDefinition().getNpcName()))
                .findFirst();

        pet.ifPresent(p -> {
            if (player.getItems().getItemCount(p.itemId, false) > 0 || player.summonId == p.itemId) {
                return;
            }

            int rights = player.getRights().getPrimary().getValue() - 1;
            if (RandomUtils.nextInt(0, p.droprate) == 1) {
                player.getItems().addItemUnderAnyCircumstance(p.itemId, 1);
                spawn(player, p, false, false);
                //GlobalMessages.send(Misc.formatPlayerName(player.playerName) + " has received a pet drop from " + p.parent + ".", GlobalMessages.MessageType.LOOT);
                //DiscordBot.sendMessage("valius-bot", "[Loot Bot] " + Misc.formatPlayerName(player.playerName)
                 //       + " has received a pet drop from " + p.parent + ".");
            }
        });

    }

    /**
     * Handles metamorphosis of the npc of choice
     *
     * @param player the player performing the metamorphosis
     * @param npcId  the npc to metamorphose
     */
    public static void metamorphosis(Player player, int npcId) {
        Pets pets = forNpc(npcId);
        if (npcId < 1) {
            return;
        }
        if (pets != null) {
            if (NPCHandler.npcs[player.npcClickIndex].spawnedBy != player.getIndex()) {
                player.sendMessage("This is not your pet.");
                return;
            }
            switch (npcId) {
                case 2130:
                case 2131:
                case 2132:
                    NPCHandler.npcs[player.npcClickIndex].requestTransform(npcId == 2132 ? npcId - 2 : npcId + 1);
                    break;

                case 6637:
                case 6638:
                    NPCHandler.npcs[player.npcClickIndex].requestTransform(npcId == 6638 ? 6637 : 6638);
                    break;

                // case 6718:
                // case 6719:
                // case 6720:
                // case 6721:
                // int randomGold = Misc.random(15_000);
                // NPCHandler.npcs[player.npcClickIndex].requestTransform(randomGold == 15 ?
                // 6721 : npcId == 6721 ? 6718 : npcId == 6720 ? npcId - 2 : npcId + 1);
                // break;
            }
        }
    }

	/*
     * public static void recolor(Player player, int npcId, int itemId) { Pets pets
	 * = forNpc(npcId); if (npcId < 1) { return; } if (pets != null) { if
	 * (NPCHandler.npcs[player.npcClickIndex].spawnedBy != player.getIndex()) {
	 * player.sendMessage("This is not your pet."); return; } switch (npcId) { case
	 * 7439: switch (itemId) { case 438:
	 * NPCHandler.npcs[player.npcClickIndex].requestTransform(7440); break; } break;
	 * 
	 * /*case 6637: NPCHandler.npcs[player.npcClickIndex].requestTransform(npcId ==
	 * 6638 ? 6637 : 6638); break;
	 * 
	 * } } }
	 */

    public static boolean talktoPet(Player c, int npcId) {
        Pets pets = forNpc(npcId);
        if (pets != null) {
            if (NPCHandler.npcs[c.rememberNpcIndex].spawnedBy == c.getIndex()) {
                if (npcId == 4441) {
                    c.getDH().sendDialogues(14000, 3200);
                }
                if (npcId == 4439) {
                    c.getDH().sendDialogues(14003, 3200);
                }
                if (npcId == 4440) {
                    c.getDH().sendDialogues(14006, 3200);
                }
                if (npcId == 4446) {
                    c.getDH().sendDialogues(14009, 3200);
                }
                if (npcId == 4442) {
                    c.getDH().sendDialogues(14011, 3200);
                }
                if (npcId == 4438) {
                    c.getDH().sendDialogues(14014, 3200);
                }
            } else {
                c.sendMessage("This is not your pet.");
            }
            return true;
        } else {
            return false;
        }
    }

}