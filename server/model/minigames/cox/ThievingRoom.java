package server.model.minigames.cox.rooms;

import server.Server;
import server.model.minigames.cox.RaidRoom;
import server.model.minigames.cox.RaidSession;
import server.model.minigames.cox.RaidNode;
import server.model.players.Player;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.util.Misc;
import server.world.Location;
import server.world.World;

import java.util.*;

public class ThievingRoom extends RaidRoom {

    private int hungerLevel = 0;
    private final int MAX_HUNGER;
    private NPC scavenger;

    // Chest Management
    private Map<Location, Long> openChests = new HashMap<>();
    private Set<Location> poisonChests = new HashSet<>();
    private Location batChest;
    private boolean batsLooted = false;

    public ThievingRoom(RaidSession session, RaidNode node) {
        super(session, node);
        // Scale hunger based on party size
        this.MAX_HUNGER = 20 + (session.getParty().getMembers().size() * 10);
    }

    @Override
    public void spawn() {
        int[] npcCoords = getCoords(6, 14);
        int z = session.getHeight() + (node.getGridZ() * 4);

        // --- STANDARD PI NPC SPAWNING ---
        // spawnNpc(player, npcType, x, y, heightLevel, WalkingType, HP, maxHit, attack, defence, attackBonus, defenceBonus)
        World.getWorld().npcHandler.spawnNpc(session.getParty().getLeader(), 7602, npcCoords[0], npcCoords[1], z, 0, 200, 0, 0, 0, false, false);

        // Grab the NPC object we just spawned so we can transform/forceChat it later
        for (NPC npc : NPCHandler.npcs) {
            if (npc != null && npc.npcType == 7602 && npc.getX() == npcCoords[0] && npc.getY() == npcCoords[1] && npc.heightLevel == z) {
                this.scavenger = npc;
                break;
            }
        }

        // TODO: Scan the room for 29742 objects to set up poisonChests and batChest
    }

    @Override
    public void tick() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Location, Long>> it = openChests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Location, Long> entry = it.next();
            if (now > entry.getValue()) {
                session.getParty().getMembers().forEach(p ->
                        p.getPA().checkObjectSpawn(29742, entry.getKey().getX(), entry.getKey().getY(), 0, 10));
                it.remove();
            }
        }
    }

    @Override
    public boolean handleObjectClick(Player player, int objectId, int x, int y, int clickType) {
        Location loc = new Location(x, y, player.getHeight());

        switch (objectId) {
            case 29742: // Closed Chest
                handleChestOpen(player, loc);
                return true;

            case 29746: // Trough
                handleDeposit(player);
                return true;

            case 29875: // Dead Keeper
                if (clickType == 5) { // Search option
                    player.sendMessage("You find some basic supplies...");
                    // player.getItems().addItem(itemId, amount);
                }
                return true;
        }
        return false;
    }

    private void handleChestOpen(Player player, Location loc) {
        if (openChests.containsKey(loc)) return;

        // --- PI INVENTORY CHECKS ---
        boolean hasLockpick = player.getItems().playerHasItem(1523) || player.getItems().playerHasItem(11686);
        int thievingLevel = player.playerLevel[17]; // Assuming standard PI skill array

        player.startAnimation(536);

        // Note: You'll want to wrap the below logic in a CycleEvent or Task for the delay,
        // using your hasLockpick boolean to determine if the delay is 2 ticks or 4 ticks.

        if (loc.equals(batChest) && !batsLooted) {
            batsLooted = true;
            replaceChestTemporarily(loc, 29743);
        } else if (poisonChests.contains(loc)) {
            // Note: You may need to adjust the appendDamage parameters based on your specific combat system's hitmarks
            player.appendDamage(Misc.random(1, 3), server.model.players.combat.Hitmark.POISON);
            player.sendMessage("You are sprayed with poison gas!");
            replaceChestTemporarily(loc, 29743);
        } else {
            int amount = (thievingLevel > 50 && Misc.random(10) > 7) ? 2 : 1;
            player.getItems().addItem(20885, amount); // --- PI ADD ITEM ---
            replaceChestTemporarily(loc, 29744);
        }
    }

    private void replaceChestTemporarily(Location loc, int newId) {
        session.getParty().getMembers().forEach(p ->
                p.getPA().checkObjectSpawn(newId, loc.getX(), loc.getY(), 0, 10));

        openChests.put(loc, System.currentTimeMillis() + 5000);
    }

    private void handleDeposit(Player player) {
        int grubs = player.getItems().getItemAmount(20885); // --- PI GET ITEM AMOUNT ---
        if (grubs <= 0) {
            player.sendMessage("You don't have any cavern grubs to deposit.");
            return;
        }

        player.getItems().deleteItem(20885, grubs); // --- PI DELETE ITEM ---
        hungerLevel += grubs;

        if (scavenger != null) {
            scavenger.forceChat("Mmmm... grubs...");
        }

        if (hungerLevel >= MAX_HUNGER) {
            clearRoom();
        }
    }

    private void clearRoom() {
        isCleared = true;
        if (scavenger != null) {
            scavenger.requestTransform(7603); // Sleepy Scavenger
        }
        session.getParty().getMembers().forEach(p ->
                p.sendMessage("@pur@The scavenger is full and moves out of the way!"));
    }

    @Override public void handleDeath(NPC npc) {}
}