package server.model.minigames.trawler;

import java.util.ArrayList;
import java.util.List;
import server.model.players.Player;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.util.Misc;

public abstract class BaseFishingTrawler {

    protected static final int MAX_WATER_LEVEL = 100;
    protected static final int TICK_RATE = 1; // 600ms per tick
    
    protected List<Player> participants = new ArrayList<>();
    protected int waterLevel = 0;
    protected int gameTimer = 500; // 500 ticks = 5 minutes of Trawling
    protected boolean isRunning = false;
    
    // Ship Shifting State (0 = Dry, 1 = Shallow, 2 = Deep, 3 = Flooded)
    protected int currentShipState = 0;

    // --- Wiki-Accurate States ---
    protected boolean isNetBroken = false;
    protected boolean krakenActive = false;
    protected boolean railBroken = false;
    protected int totalFishCaught = 0; // The shared pool of fish

    public void start() {
        if (isRunning) return;
        isRunning = true;
        totalFishCaught = 0;
        waterLevel = 0;
        currentShipState = 0;
        onGameStart();

        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!isRunning) {
                    container.stop();
                    return;
                }

                processGameTick();

                if (waterLevel >= MAX_WATER_LEVEL) {
                    endGame(false); // Lost (Sunk)
                    container.stop();
                } else if (gameTimer <= 0) {
                    endGame(true); // Won (Time's up)
                    container.stop();
                }
            }

            @Override
            public void stop() {
                isRunning = false;
            }
        }, TICK_RATE);
    }

    protected void processGameTick() {
        gameTimer--;
        
        // 1. Water rises based on active leaks
        int activeLeaks = getActiveLeakCount();
        if (activeLeaks > 0) {
            // Slower rise to make it playable
            if (gameTimer % 3 == 0) { 
                adjustWaterLevel(activeLeaks); 
            }
        }

        // 2. Fish Catching Logic
        if (!isNetBroken) {
            int catchRate = krakenActive ? 10 : 5; // Ticks per fish addition
            if (gameTimer % catchRate == 0) {
                totalFishCaught += participants.size(); // Scales with players
            }
        }

        // 3. Random Events
        int eventChance = Math.max(10, 50 - (participants.size() * 2));
        if (Misc.random(eventChance) == 1) {
            handleRandomEvent();
        }

        // 4. Update UI
        for (Player p : participants) {
            updateInterface(p);
        }
    }

    public void adjustWaterLevel(int amount) { 
        this.waterLevel = Math.max(0, Math.min(MAX_WATER_LEVEL, this.waterLevel + amount)); 
        
        int expectedState = currentShipState;

        // "Buffer Zone" Logic to prevent teleporting back and forth every tick!
        switch (currentShipState) {
            case 0: // Dry Ship
                if (waterLevel >= 30) expectedState = 1; // Shifts up at 30%
                break;
            case 1: // Shallow Ship
                if (waterLevel >= 55) expectedState = 2; // Shifts up at 55%
                else if (waterLevel <= 15) expectedState = 0; // Must bail down to 15% to return to Dry
                break;
            case 2: // Deep Ship
                if (waterLevel >= 80) expectedState = 3; // Shifts up at 80%
                else if (waterLevel <= 40) expectedState = 1; // Must bail down to 40% to return to Shallow
                break;
            case 3: // Flooded Ship
                if (waterLevel <= 65) expectedState = 2; // Must bail down to 65% to return to Deep
                break;
        }

        // If the state officially changed, teleport the players!
        if (expectedState != currentShipState) {
            int stateDifference = expectedState - currentShipState;
            int xOffset = stateDifference * 43; // The ships are exactly 43 X-tiles apart

            for (Player p : participants) {
                p.getPA().movePlayer(p.getX() + xOffset, p.getY(), p.getHeight());
            }
            
            currentShipState = expectedState;
            onShipStateChange(); // Tell World1Trawler to move the leaks and kraken!
        }
    }

    public void endGame(boolean success) {
        isRunning = false;
        
        int playerCut = participants.isEmpty() ? 0 : (totalFishCaught / participants.size());

        for (Player p : participants) {
            if (success) {
                p.sendMessage("The ship returns safely! Check the net for your catch.");
                giveRewards(p, playerCut);
            } else {
                p.sendMessage("The boat has sunk! You wash up on shore.");
                p.appendDamage(Misc.random(1, 2), server.model.players.combat.Hitmark.HIT); 
            }
            
            p.trawlerContribution = 0; 
            teleportOut(p, success);
            p.getPA().removeAllWindows(); 
        }
        participants.clear();
        onGameEnd(success);
    }

    public void addPlayer(Player p) {
        if (!participants.contains(p)) {
            participants.add(p);
            p.trawlerContribution = 0; 
            p.sendMessage("You board the Fishing Trawler.");
        }
    }

    public void addContribution(Player p, int amount) {
        p.trawlerContribution = Math.min(255, p.trawlerContribution + amount);
    }

    protected void teleportOut(Player p, boolean success) {
        if (success) {
            p.getPA().movePlayer(2662, 3158, 0); // Port Khazard docks
        } else {
            p.getPA().movePlayer(2668, 3164, 0); // Washed up north of Port Khazard
        }
    }

    // --- Abstract Methods ---
    protected abstract void onGameStart();
    protected abstract void onGameEnd(boolean success);
    protected abstract void handleRandomEvent();
    protected abstract int getActiveLeakCount();
    protected abstract void onShipStateChange();
    protected abstract void updateInterface(Player p);
    public abstract void handleObjectClick(Player p, int objectId);
    public abstract void handleItemOnObject(Player p, int itemId, int objectId, int objX, int objY);
    public abstract void handleNpcClick(Player p, int npcId);
    protected abstract void giveRewards(Player p, int fishCut);
}