package server.model.players.packets;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import server.model.players.Client;
import server.model.players.Player;

import java.util.Map;

public class ItemCharge {

    private final Player client;
    private static final int MAX_SCALES = 16383;
    private static final Map<Integer, String> DART_INFO = ImmutableMap.<Integer, String>builder()
            .put(806, "Bronze")
            .put(807, "Iron")
            .put(808, "Steel")
            .put(809, "Mithril")
            .put(810, "Adamant")
            .put(811, "Runite")
            .build();

    private static final int MAX_RUNE_AMOUNT = 16000;
    private static final Map<Integer, String> RUNE_INFO = ImmutableMap.<Integer, String>builder()
            .put(554, "Fire")
            .put(555, "Water")
            .put(556, "Air")
            .put(557, "Earth")
            .put(558, "Mind")
            .put(559, "Body")
            .put(560, "Death")
            .put(561, "Nature")
            .put(562, "Chaos")
            .put(563, "Law")
            .put(564, "Cosmic")
            .put(565, "Blood")
            .put(566, "Soul")
            .put(9075, "Astral")
            .put(4694, "Steam")
            .put(4695, "Mist")
            .put(4696, "Dust")
            .put(4697, "Smoke")
            .put(4698, "Mud")
            .put(4699, "Lava")
            .build();

    public ItemCharge(Player player) {
        this.client = player;
    }

    public void addCharges(int xRemoveId, int xRemoveSlot, int xAmount) {
        if (client.addingCharges) {
            handleScaleCharges(xRemoveId, xAmount, xRemoveSlot);
        } else if (client.addingDarts) {
            handleDartCharges(xRemoveId, xAmount, xRemoveSlot);
        }
    }

    private void handleScaleCharges(int xRemoveId, int xAmount, int xRemoveSlot) {
        if (xRemoveId != 12934) return;

        int scales = client.getItems().getItemCount(12934, false);
        if (scales < xAmount) {
            client.sendMessage("You don't have that many scales");
            return;
        }

        int newCharges = Math.min(client.BlowpipeCharges + xAmount, MAX_SCALES);
        if (newCharges > MAX_SCALES) {
            client.sendMessage("Your Blowpipe can only hold " + (MAX_SCALES - client.BlowpipeCharges) + " more scales");
            return;
        }

        client.BlowpipeCharges = newCharges;
        client.getItems().deleteItem(12934, xRemoveSlot, xAmount);
        client.sendMessage("You have added " + xAmount + " scales to your Toxic Blowpipe.");
        updateBlowpipeStatus(newCharges, xAmount);

        client.addingCharges = false;
        client.addingDarts = false;
    }

    private void handleDartCharges(int xRemoveId, int xAmount, int xRemoveSlot) {
        if (!DART_INFO.containsKey(xRemoveId)) return;

        int currentDarts = client.BlowpipeDarts;
        int newDarts = Math.min(currentDarts + xAmount, MAX_SCALES);

        if (client.getItems().getItemCount(xRemoveId, false) < xAmount) {
            client.sendMessage("You don't have that many darts");
            return;
        }

        client.BlowpipeDarts = newDarts;
        client.getItems().deleteItem(xRemoveId, client.getItems().getItemSlot(xRemoveId), xAmount);
        String dartName = DART_INFO.get(xRemoveId);
        client.sendMessage("You have added " + xAmount + " " + dartName + " darts to your Toxic Blowpipe.");
        client.DartType = xRemoveId;

        if (client.getItems().playerHasItem(12924) && !client.getItems().playerHasItem(12926) && client.BlowpipeCharges > 0) {
            client.getItems().deleteItem(12924, client.getItems().getItemSlot(12924), 1);
            client.getItems().addItem(12926, 1);
        }

        client.addingDarts = false;
        client.addingCharges = false;
    }

    private void updateBlowpipeStatus(int newCharges, int xAmount) {
        double percentage = (newCharges * 100.0) / MAX_SCALES;
        client.sendMessage(String.format("Your Toxic Blowpipe is now at %.2f%%", percentage));

        if (percentage >= 25 && percentage < 50) {
            client.sendMessage("Your Toxic Blowpipe is now at 25%");
        } else if (percentage >= 50 && percentage < 75) {
            client.sendMessage("Your Toxic Blowpipe is now at 50%");
        } else if (percentage >= 75 && percentage < 100) {
            client.sendMessage("Your Toxic Blowpipe is now at 75%");
        } else if (percentage == 100) {
            client.sendMessage("Your Toxic Blowpipe is now at 100%");
        }
    }

    public void addRunes(int xRemoveId, int xRemoveSlot, int xAmount) {
        if (!client.addingToRP) return;

        if (!RUNE_INFO.containsKey(xRemoveId)) return;

        String runeName = RUNE_INFO.get(xRemoveId);
        int remainingAmount = addRunesToPouch(xRemoveId, xAmount);
        
        if (remainingAmount < xAmount) {
            client.getItems().deleteItem(xRemoveId, xRemoveSlot, xAmount - remainingAmount);
            client.sendMessage("You have added " + (xAmount - remainingAmount) + " " + runeName + " runes to your Rune Pouch.");
            client.getPA().sendRuneTypes(client.PouchRune1, client.PouchRune2, client.PouchRune3, client.PouchRune1Amt, client.PouchRune2Amt, client.PouchRune3Amt);
            //client.openRunePouch();
        } else {
            client.sendMessage("You don't have that many " + runeName + " runes.");
        }
    }

    private int addRunesToPouch(int xRemoveId, int amount) {
        int remainingAmount = amount;

        if (client.PouchRune1 == xRemoveId) {
            remainingAmount = addRunesToSlot(1, remainingAmount);
        } else if (client.PouchRune2 == xRemoveId) {
            remainingAmount = addRunesToSlot(2, remainingAmount);
        } else if (client.PouchRune3 == xRemoveId) {
            remainingAmount = addRunesToSlot(3, remainingAmount);
        } else {
            remainingAmount = addRunesToEmptySlot(xRemoveId, remainingAmount);
        }

        return remainingAmount;
    }

    private int addRunesToSlot(int slot, int amount) {
        int maxAmount = getMaxRune(slot);
        int currentAmount = getPouchRuneAmt(slot);
        int spaceAvailable = maxAmount - currentAmount;
        int addedAmount = Math.min(amount, spaceAvailable);

        setPouchRune(slot, getPouchRune(slot), currentAmount + addedAmount);
        return amount - addedAmount;
    }

    private int addRunesToEmptySlot(int runeId, int amount) {
        if (client.PouchRune1 == 0) {
            client.PouchRune1 = runeId;
            client.PouchRune1Amt = Math.min(amount, MAX_RUNE_AMOUNT);
            return amount - client.PouchRune1Amt;
        } else if (client.PouchRune2 == 0) {
            client.PouchRune2 = runeId;
            client.PouchRune2Amt = Math.min(amount, MAX_RUNE_AMOUNT);
            return amount - client.PouchRune2Amt;
        } else if (client.PouchRune3 == 0) {
            client.PouchRune3 = runeId;
            client.PouchRune3Amt = Math.min(amount, MAX_RUNE_AMOUNT);
            return amount - client.PouchRune3Amt;
        }
        return amount;
    }

    private int getMaxRune(int slot) {
        return MAX_RUNE_AMOUNT;
    }

    private int getPouchRuneAmt(int slot) {
        switch (slot) {
            case 1:
                return client.PouchRune1Amt;
            case 2:
                return client.PouchRune2Amt;
            case 3:
                return client.PouchRune3Amt;
            default:
                return 0;
        }
    }
    private int getPouchRune(int slot) {
        switch (slot) {
            case 1:
                return client.PouchRune1;
            case 2:
                return client.PouchRune2;
            case 3:
                return client.PouchRune3;
            default:
                return 0;
        }
    }

    private void setPouchRune(int slot, int runeId, int amount) {
        switch (slot) {
            case 1:
                client.PouchRune1 = runeId;
                client.PouchRune1Amt = amount;
                break;
            case 2:
                client.PouchRune2 = runeId;
                client.PouchRune2Amt = amount;
                break;
            case 3:
                client.PouchRune3 = runeId;
                client.PouchRune3Amt = amount;
                break;
        }
    }

    public void emptyRunePouch() {
        if (client.PouchRune1 == 0 && client.PouchRune2 == 0 && client.PouchRune3 == 0) {
            client.sendMessage("Your Rune Pouch is already empty.");
            return;
        }

        if (client.getItems().freeSlots() < 3) {
            client.sendMessage("You need at least 3 free inventory slots to empty your Rune Pouch.");
            return;
        }

        emptyRuneSlot(1, client.PouchRune1, client.PouchRune1Amt);
        emptyRuneSlot(2, client.PouchRune2, client.PouchRune2Amt);
        emptyRuneSlot(3, client.PouchRune3, client.PouchRune3Amt);

        shiftRuneSlots();
        client.sendMessage("Your Rune Pouch has been emptied.");
    }

    private void emptyRuneSlot(int slot, int runeId, int amount) {
        if (runeId != 0) {
            client.getItems().addItem(runeId, amount);
            setPouchRune(slot, 0, 0);
            client.getPA().sendFrame34a(41710, -1, slot - 1, 1);
        }
    }

    private void shiftRuneSlots() {
        if (client.PouchRune2 == 0 && client.PouchRune3 != 0) {
            client.PouchRune2 = client.PouchRune3;
            client.PouchRune2Amt = client.PouchRune3Amt;
            client.PouchRune3 = 0;
            client.PouchRune3Amt = 0;
            client.getPA().sendFrame34a(41710, -1, 2, 1);
        }
        if (client.PouchRune1 == 0 && client.PouchRune2 != 0) {
            client.PouchRune1 = client.PouchRune2;
            client.PouchRune1Amt = client.PouchRune2Amt;
            client.PouchRune2 = 0;
            client.PouchRune2Amt = 0;
            client.getPA().sendFrame34a(41710, -1, 1, 1);
        }
    }

    public void update() {
        // Implement the update logic here
    }
}
