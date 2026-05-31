package server.model.players.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import server.Config;
import server.model.players.Player;
import server.model.players.skills.Skill;

/**
 * Handles Chambers of Xeric Herblore logic, including 3-tier potion production
 * and dynamic level-capping downgrades.
 * * @author Gemini
 */
public class CoxHerblore {

    // Placeholder Item ID Constants - Update these to match your server's cache!
    public static final int EMPTY_GOURD = 20800;
    public static final int WATER_GOURD = 20801;

    // Herbs
    public static final int GOLPAR = 20793;
    public static final int BUCHU = 20796;
    public static final int NOXIFER = 20799;

    // Secondaries
    public static final int STINKHORN = 20802;
    public static final int CICELY = 20803;
    public static final int ENDARKENED_JUICE = 20804;

    public enum CoxPotionData {
        ELDER(GOLPAR, STINKHORN, 47, 59, 70, 6.5, 10.0, 13.0, 20920, 20924, 20928, "Elder"),
        TWISTED(GOLPAR, CICELY, 47, 59, 70, 6.5, 10.0, 13.0, 20932, 20936, 20940, "Twisted"),
        KODAI(GOLPAR, ENDARKENED_JUICE, 47, 59, 70, 6.5, 10.0, 13.0, 20944, 20948, 20952, "Kodai"),
        REVITALISATION(BUCHU, STINKHORN, 52, 65, 78, 13.5, 20.0, 26.5, 20956, 20960, 20964, "Revitalisation"),
        PRAYER_ENHANCE(BUCHU, CICELY, 52, 65, 78, 13.5, 20.0, 26.5, 20968, 20972, 20976, "Prayer enhance"),
        XERICS_AID(BUCHU, ENDARKENED_JUICE, 52, 65, 78, 13.5, 20.0, 26.5, 20980, 20984, 20988, "Xeric's aid"),
        ANTIPOISON(NOXIFER, CICELY, 60, 75, 90, 13.5, 20.0, 26.5, 20992, 20996, 21000, "Antipoison");

        public final int herb, secondary, weakLvl, stdLvl, strongLvl;
        public final double weakXp, stdXp, strongXp;
        public final int weakId, stdId, strongId;
        public final String name;

        CoxPotionData(int herb, int secondary, int weakLvl, int stdLvl, int strongLvl,
                      double weakXp, double stdXp, double strongXp, int weakId, int stdId, int strongId, String name) {
            this.herb = herb;
            this.secondary = secondary;
            this.weakLvl = weakLvl;
            this.stdLvl = stdLvl;
            this.strongLvl = strongLvl;
            this.weakXp = weakXp;
            this.stdXp = stdXp;
            this.strongXp = strongXp;
            this.weakId = weakId;
            this.stdId = stdId;
            this.strongId = strongId;
            this.name = name;
        }
    }

    /**
     * Intercepts item on item combinations to check for CoX recipe validation.
     */
    public static boolean handleItemCombination(Player p, int item1, int item2) {
        if (p.getRaidSession() == null) return false;

        // Handle filling gourds at water source
        if ((item1 == EMPTY_GOURD && item2 == 227) || (item1 == 227 && item2 == EMPTY_GOURD)) {
            fillGourd(p);
            return true;
        }

        // Try standard recipes
        for (CoxPotionData pot : CoxPotionData.values()) {
            if (hasIngredients(item1, item2, WATER_GOURD, pot.herb)) {
                createUnfinishedPotion(p, pot);
                return true;
            }
            if (hasIngredients(item1, item2, pot.herb, pot.secondary)) {
                // If your system skips unf-potions and mixes directly into gourds
                if (p.getItems().playerHasItem(WATER_GOURD)) {
                    mixCoxPotion(p, pot);
                    return true;
                }
            }
        }

        // Handle Overload Mixing logic (Requires components of the same tier)
        if (isOverloadIngredient(item1) || isOverloadIngredient(item2)) {
            handleOverloadCreation(p, item1, item2);
            return true;
        }

        return false;
    }

    private static boolean hasIngredients(int i1, int i2, int req1, int req2) {
        return (i1 == req1 && i2 == req2) || (i1 == req2 && i2 == req1);
    }

    private static void fillGourd(Player p) {
        if (p.getItems().playerHasItem(EMPTY_GOURD)) {
            p.getItems().deleteItem(EMPTY_GOURD, 1);
            p.getItems().addItem(WATER_GOURD, 1);
            p.sendMessage("You fill the gourd vial with water.");
        }
    }

    /**
     * Processes creation with the dynamic level threshold down-capping rule.
     */
    private static void mixCoxPotion(Player p, CoxPotionData pot) {
        int herbloreLvl = p.getSkills().getLevel(Skill.HERBLORE);

        if (herbloreLvl < pot.weakLvl) {
            p.sendMessage("You need a Herblore level of at least " + pot.weakLvl + " to mix this potion.");
            return;
        }

        p.getItems().deleteItem(pot.herb, 1);
        p.getItems().deleteItem(pot.secondary, 1);
        p.getItems().deleteItem(WATER_GOURD, 1);
        p.startAnimation(828);

        int finalProduct;
        double finalXp;
        String tierSignifier;

        // Apply Downgrade Threshold verification rules
        if (herbloreLvl >= pot.strongLvl) {
            finalProduct = pot.strongId;
            finalXp = pot.strongXp;
            tierSignifier = " (+)";
        } else if (herbloreLvl >= pot.stdLvl) {
            finalProduct = pot.stdId;
            finalXp = pot.stdXp;
            tierSignifier = "";
        } else {
            finalProduct = pot.weakId;
            finalXp = pot.weakXp;
            tierSignifier = " (-)";
        }

        p.getItems().addItem(finalProduct, 1);
        p.getSkills().addExperience((int) (finalXp * Config.HERBLORE_EXPERIENCE), Skill.HERBLORE);
        p.sendMessage("You mix a " + pot.name + " potion" + tierSignifier + ".");
    }

    private static boolean isOverloadIngredient(int id) {
        for (CoxPotionData pot : CoxPotionData.values()) {
            if (id == pot.weakId || id == pot.stdId || id == pot.strongId) return true;
        }
        return id == NOXIFER;
    }

    /**
     * Confirms all 3 sub-potions match tier brackets before processing an Overload synthesis request.
     */
    private static void handleOverloadCreation(Player p, int item1, int item2) {
        int herbloreLvl = p.getSkills().getLevel(Skill.HERBLORE);
        if (herbloreLvl < 60) {
            p.sendMessage("You need a Herblore level of at least 60 to brew an Overload.");
            return;
        }

        int nX = p.getItems().getItemSlot(NOXIFER);
        if (nX == -1) return;

        // Identify operational tier bracket matching inventory assets
        int tier = 0; // 1 = Weak, 2 = Standard, 3 = Strong
        if (p.getItems().playerHasItem(CoxPotionData.ELDER.strongId) && p.getItems().playerHasItem(CoxPotionData.TWISTED.strongId) && p.getItems().playerHasItem(CoxPotionData.KODAI.strongId)) {
            tier = 3;
        } else if (p.getItems().playerHasItem(CoxPotionData.ELDER.stdId) && p.getItems().playerHasItem(CoxPotionData.TWISTED.stdId) && p.getItems().playerHasItem(CoxPotionData.KODAI.stdId)) {
            tier = 2;
        } else if (p.getItems().playerHasItem(CoxPotionData.ELDER.weakId) && p.getItems().playerHasItem(CoxPotionData.TWISTED.weakId) && p.getItems().playerHasItem(CoxPotionData.KODAI.weakId)) {
            tier = 1;
        }

        if (tier == 0) {
            p.sendMessage("You need an Elder, Twisted, and Kodai potion of the same tier alongside a Noxifer to mix an Overload.");
            return;
        }

        // Apply Downgrade Threshold Rule to Overloads
        int targetTier = tier;
        if (targetTier == 3 && herbloreLvl < 90) targetTier = 2;
        if (targetTier == 2 && herbloreLvl < 75) targetTier = 1;

        // Delete inventory items
        p.getItems().deleteItem(NOXIFER, 1);
        p.getItems().deleteItem(tier == 3 ? CoxPotionData.ELDER.strongId : tier == 2 ? CoxPotionData.ELDER.stdId : CoxPotionData.ELDER.weakId, 1);
        p.getItems().deleteItem(tier == 3 ? CoxPotionData.TWISTED.strongId : tier == 2 ? CoxPotionData.TWISTED.stdId : CoxPotionData.TWISTED.weakId, 1);
        p.getItems().deleteItem(tier == 3 ? CoxPotionData.KODAI.strongId : tier == 2 ? CoxPotionData.KODAI.stdId : CoxPotionData.KODAI.weakId, 1);

        int product = 0;
        double xp = 0;
        String name = "";

        if (targetTier == 3) {
            product = 20904; // Overload (+) ID placeholder
            xp = 66.5;
            name = "Overload (+)";
        } else if (targetTier == 2) {
            product = 20900; // Overload ID placeholder
            xp = 50.0;
            name = "Overload";
        } else {
            product = 20896; // Overload (-) ID placeholder
            xp = 33.5;
            name = "Overload (-)";
        }

        p.getItems().addItem(product, 1);
        p.getSkills().addExperience((int) (xp * Config.HERBLORE_EXPERIENCE), Skill.HERBLORE);
        p.sendMessage("You successfully assemble an " + name + ".");
    }

    private static void createUnfinishedPotion(Player p, CoxPotionData pot) {
        // Fallback or hook point if you use dynamic unfinished item codes inside your raid cycle
    }

    /**
     * Logic hook for ObjectOptionOne tree harvesting execution
     */
    public static void harvestGourdTree(Player p, boolean pickLots) {
        p.startAnimation(832);
        int freeSlots = p.getItems().freeSlots();
        if (freeSlots == 0) {
            p.sendMessage("Your inventory is too full to carry any gourds.");
            return;
        }
        int count = pickLots ? freeSlots : 1;
        for (int i = 0; i < count; i++) {
            p.getItems().addItem(EMPTY_GOURD, 1);
        }
        p.sendMessage("You pick " + (pickLots ? "masses of gourd vials" : "a gourd vial") + " from the tree.");
    }
}