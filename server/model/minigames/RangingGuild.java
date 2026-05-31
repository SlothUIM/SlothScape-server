package server.model.minigames;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.util.Misc;

public class RangingGuild {

    // The 10 arrow sprites on the left of the interface (Ordered Top to Bottom, L to R)
    private static final int[] ARROW_MODELS = { 538, 557, 559, 560, 561, 562, 563, 564, 565, 566 };

    // The hit markers grouped by ring color so we can randomize where the arrow lands
    private static final int[] BULLSEYE_MARKERS = { 536 };
    private static final int[] YELLOW_MARKERS = { 539, 541 };
    private static final int[] RED_MARKERS = { 542, 544 };
    private static final int[] BLUE_MARKERS = { 540, 546 };
    private static final int[] BLACK_MARKERS = { 545, 547, 548 };

    // All markers for easy resetting
    private static final int[] ALL_MARKERS = { 536, 539, 540, 541, 542, 543, 544, 545, 546, 547, 548 };

    public static void fireAtTarget(Player c, int objectX, int objectY) {
        // 1. Validation Checks
    	if (c.getX() > 2670 && c.getX() < 2673 && c.getY() < 3420 && c.getY() > 3415) { 
            c.sendMessage("You must stand behind the hay bales to shoot at the target.");
            //return;
        }
        if (c.archeryGuildShots <= 0) {
            c.sendMessage("You need to pay the Competition Judge to get more shots.");
            return;
        }
        if (c.playerEquipment[c.playerWeapon] == -1 || !c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase().contains("bow")) {
            c.sendMessage("You must have a bow equipped to shoot at the target.");
            return;
        }
        if (c.playerEquipment[c.playerArrows] != 882) { // 877 is Bronze Arrows
            c.sendMessage("You must use the bronze arrows provided by the judge.");
            return;
        }
        
        // Prevent spam-clicking the target while an arrow is already in flight
        if (c.isForceMovementActive() || c.freezeTimer > 0) {
            return;
        }

        // 2. The Launch (Immediate)
        c.turnPlayerTo(objectX, objectY);
        c.getItems().deleteArrow();
        c.startAnimation(426); // Bow firing animation
        c.archeryGuildShots--;
        
        // Stop them from running away while shooting
        c.stopMovement();
        c.freezeTimer = 3; 

        // Send the Bronze Arrow projectile (Projectile ID 10)
        // You may need to tweak these specific timing/height integers depending on your base's exact method arguments
        int offsetY = (c.getX() - objectX) * -1;
        int offsetX = (c.getY() - objectY) * -1;
        c.getPA().createPlayersProjectile(c.getX(), c.getY(), offsetX, offsetY, 50, 70, 10, 43, 31, -1, 65);

        // 3. The Impact (Delayed by 3 Game Ticks)
        // 3 ticks = 1.8 seconds, which is standard for a ~15-20 tile bow shot.
        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                
                // Calculate Accuracy
            	int maxRoll = c.getCombat().calculateRangeAttack() + 150; 
                if (maxRoll < 1000) maxRoll = 1500;
                int shot = Misc.random(maxRoll);

                int points = 0;
                String feedback = "Missed!";
                
                // We will use ID 536 (The center bullseye arrow) and physically move it
                int xOffset = 0;
                int yOffset = 0;
                boolean missed = false;

                if (shot > 30000) {
                    points = 100;
                    feedback = "Bulls-Eye!";
                    // Random offset between -6 and +6 pixels (Stays in the yellow center)
                    xOffset = -6 + Misc.random(12);
                    yOffset = -6 + Misc.random(12);
                } else if (shot > 18000) {
                    points = 50;
                    feedback = "Yellow!";
                    // Random offset between -18 and +18 pixels
                    xOffset = -18 + Misc.random(36);
                    yOffset = -18 + Misc.random(36);
                } else if (shot > 9000) {
                    points = 30;
                    feedback = "Red!";
                    // Random offset between -35 and +35 pixels
                    xOffset = -35 + Misc.random(70);
                    yOffset = -35 + Misc.random(70);
                } else if (shot > 3500) {
                    points = 20;
                    feedback = "Blue!";
                    // Random offset between -55 and +55 pixels
                    xOffset = -55 + Misc.random(110);
                    yOffset = -55 + Misc.random(110);
                } else if (shot > 500) {
                    points = 10;
                    feedback = "Black!";
                    // Random offset between -75 and +75 pixels
                    xOffset = -75 + Misc.random(150);
                    yOffset = -75 + Misc.random(150);
                } else {
                    missed = true; // Arrow won't show on the target at all
                }

                c.archeryGuildScore += points;

                // Pass the offsets to the interface update instead of a marker ID
                updateInterface(c, feedback, xOffset, yOffset, missed);

                if (points > 0) {
                    c.getPA().addSkillXP((points / 2), c.playerRanged);
                }

                // Handle End of Game
                if (c.archeryGuildShots == 0) {
                    int tickets = c.archeryGuildScore / 10;
                    c.getItems().addItem(1464, tickets); 
                    c.sendMessage("You have finished your game! You scored " + c.archeryGuildScore + " points and received " + tickets + " tickets.");
                    c.archeryGuildScore = 0; 
                    c.targetMinigame = false;
                }
                
                // Stop the event so it doesn't loop!
                container.stop();
            }
        }, 6); // <--- THE 3 TICK DELAY
    }

    private static void updateInterface(Player c, String feedback, int xOffset, int yOffset, boolean missed) {
        // 1. OPEN THE INTERFACE FIRST

        // 2. Hide all of Jagex's old static markers to clear the board
        for (int id : ALL_MARKERS) {
            c.getPA().sendFrame171(1, id); 
        }

        // 3. Move and Show the Dynamic Arrow (ID 536)
        if (!missed) {
            // sendFrame70 shifts a child interface component (X offset, Y offset, Child ID)
            c.getPA().sendFrame171(0, 536); // Unhide our dynamic arrow
        }

        // 4. Update the "Shots Left" arrows using the Item Model Packet
        for (int i = 0; i < ARROW_MODELS.length; i++) {
            if (i < c.archeryGuildShots) {
                c.getPA().sendFrame171(1, ARROW_MODELS[i]); // Unhide our dynamic arrow
            } else {
                c.getPA().sendFrame171(0, ARROW_MODELS[i]); // Unhide our dynamic arrow
            }
        }

        // 5. Update the Text
        c.getPA().sendFrame126(feedback, 567);
        c.getPA().sendFrame126(Integer.toString(c.archeryGuildScore), 551);
        c.getPA().showInterface(446);
    }
}