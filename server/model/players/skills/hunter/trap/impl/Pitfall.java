package server.model.players.skills.hunter.trap.impl;

import java.util.HashMap;
import java.util.Map;

import server.event.Event;
import server.model.npcs.NPCDumbPathFinder;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.skills.hunter.trap.PitfallData;
import server.world.World;
import server.util.Misc;

public class Pitfall {

    private static final Map<Integer, Player> ACTIVE_PITS = new HashMap<>();

    public static void updatePitfallState(Player player, int pitIndex, int state) {
        int varpId = (pitIndex <= 9) ? 917 : 918;
        int shift = (pitIndex <= 9) ? (pitIndex * 3) : ((pitIndex - 10) * 3);
        int mask = 7 << shift;

        if (varpId == 917) {
            player.pitfallVarp917 = (player.pitfallVarp917 & ~mask) | ((state << shift) & mask);
            player.getPA().sendConfig(917, player.pitfallVarp917);
        } else {
            player.pitfallVarp918 = (player.pitfallVarp918 & ~mask) | ((state << shift) & mask);
            player.getPA().sendConfig(918, player.pitfallVarp918);
        }
    }

    public static int getPitfallState(Player player, int pitIndex) {
        int varpId = (pitIndex <= 9) ? 917 : 918;
        int shift = (pitIndex <= 9) ? (pitIndex * 3) : ((pitIndex - 10) * 3);
        return (varpId == 917) ? ((player.pitfallVarp917 >> shift) & 7) : ((player.pitfallVarp918 >> shift) & 7);
    }

    public static void setupTrap(Player player, int objectId) {
        int pitIndex = (objectId - 19253) % 16;
        if (player.getSkills().getLevel(Skill.HUNTER) < 31) {
            player.sendMessage("You need a Hunter level of at least 31 to set a pitfall trap.");
            return;
        }
        if (!player.getItems().playerHasItem(1511) || !player.getItems().playerHasItem(946)) {
            player.sendMessage("You need logs and a knife to set this trap.");
            return;
        }
        if (ACTIVE_PITS.containsKey(pitIndex)) {
            player.sendMessage("This trap has already been set.");
            return;
        }
        player.startAnimation(5208);
        player.getItems().deleteItem(1511, 1);
        ACTIVE_PITS.put(pitIndex, player);
        updatePitfallState(player, pitIndex, 1);
    }

    public static void jumpTrap(final Player player, final int objectId, final int objectX, final int objectY) {
        final int pitIndex = (objectId - 19253) % 16;
        int state = getPitfallState(player, pitIndex);

        int targetX = player.getX();
        int targetY = player.getY();
        int walkX = 0, walkY = 0, faceDir = -1;

        // --- DYNAMIC BOUNDING BOX ---
        // Odd IDs = N/S (3 wide, 2 deep) | Even IDs = E/W (2 wide, 3 deep)
        boolean isNorthSouth = (objectId % 2 != 0);
        int sizeX = isNorthSouth ? 3 : 2;
        int sizeY = isNorthSouth ? 2 : 3;

        int minX = objectX;
        int maxX = objectX + sizeX - 1;
        int minY = objectY;
        int maxY = objectY + sizeY - 1;

        int pX = player.getX();
        int pY = player.getY();

        // Detect jump direction based on adjacency to the box
        if (pX == minX - 1 && pY >= minY && pY <= maxY) {
            targetX += (sizeX + 1); walkX = (sizeX + 1); faceDir = 1; // Jump East
        } else if (pX == maxX + 1 && pY >= minY && pY <= maxY) {
            targetX -= (sizeX + 1); walkX = -(sizeX + 1); faceDir = 3; // Jump West
        } else if (pY == minY - 1 && pX >= minX && pX <= maxX) {
            targetY += (sizeY + 1); walkY = (sizeY + 1); faceDir = 0; // Jump North
        } else if (pY == maxY + 1 && pX >= minX && pX <= maxX) {
            targetY -= (sizeY + 1); walkY = -(sizeY + 1); faceDir = 2; // Jump South
        }

        if (faceDir == -1) return;

        player.getMovementQueue().stop();
        player.forceMovementActive = true;
        player.startAnimation(3067);

        int baseRegionX = player.getLastKnownLocation().getRegionX() * 8;
        int baseRegionY = player.getLastKnownLocation().getRegionY() * 8;
        player.x1 = pX - baseRegionX;
        player.y1 = pY - baseRegionY;
        player.x2 = targetX - baseRegionX;
        player.y2 = targetY - baseRegionY;
        player.speed1 = 30;
        player.speed2 = 60;
        player.direction = faceDir;
        player.updateRequired = true;
        player.forceMovement = true;
        player.getPA().requestUpdates();

        final int landingX = targetX;
        final int landingY = targetY;
        final int finalWalkX = walkX;
        final int finalWalkY = walkY;

        // Use the mathematical center of the trap for the NPC/GFX target
        final int centerX = objectX + (isNorthSouth ? 1 : 0);
        final int centerY = objectY + (isNorthSouth ? 0 : 1);

        NPC foundAnimal = null;
        if (state == 1) {
            for (NPC npc : NPCHandler.npcs) {
                if (npc != null && !npc.isDead && PitfallData.forNpc(npc.npcType).isPresent()) {
                    if (isNpcLinedUp(npc, centerX, centerY, finalWalkX, finalWalkY, isNorthSouth)) {
                        foundAnimal = npc;
                        npc.underAttack = false;
                        npc.killerId = 0;
                        npc.facePlayer(0);
                        npc.randomWalk = false;
                        npc.getMovementQueue().stop();
                        break;
                    }
                }
            }
        }

        final NPC targetNpc = foundAnimal;

        // --- PLAYER EVENT ---
        World.getWorld().getEventHandler().submit(new Event<Player>("pitfall_player_jump", player, 2) {
            @Override
            public void execute() {
                attachment.getPA().movePlayer(landingX, landingY, attachment.getHeight());
                attachment.forceMovementActive = false;
                attachment.getMovementQueue().stop();
                // ROBUSTNESS: Trap no longer collapses when a player jumps it.
                super.stop();
            }
        });

        // --- NPC EVENT ---
        if (targetNpc != null) {
            World.getWorld().getEventHandler().submit(new Event<Player>("pitfall_npc_tracker", player, 1) {
                int timeout = 0;
                @Override
                public void execute() {
                    timeout++;
                    if (targetNpc.getX() == centerX && targetNpc.getY() == centerY) {
                        handleAnimalCinematic(attachment, targetNpc, pitIndex, centerX, centerY, finalWalkX, finalWalkY);
                        super.stop();
                        return;
                    }
                    if (timeout > 12) { super.stop(); return; }

                    int dirX = Integer.compare(centerX, targetNpc.getX());
                    int dirY = Integer.compare(centerY, targetNpc.getY());
                    targetNpc.moveX = dirX;
                    targetNpc.moveY = dirY;
                    targetNpc.getNextNPCMovement();
                    targetNpc.turnNpc(centerX, centerY);
                    targetNpc.updateRequired = true;
                }
            });
        }
    }

    private static boolean isNpcLinedUp(NPC npc, int centerX, int centerY, int walkX, int walkY, boolean isNS) {
        if (walkX != 0) { // East/West jump
            if (npc.getY() != centerY) return false;
            if (walkX > 0 && npc.getX() >= centerX) return false;
            if (walkX < 0 && npc.getX() <= centerX) return false;
            return Math.abs(npc.getX() - centerX) <= 4;
        } else if (walkY != 0) { // North/South jump
            if (npc.getX() != centerX) return false;
            if (walkY > 0 && npc.getY() >= centerY) return false;
            if (walkY < 0 && npc.getY() <= centerY) return false;
            return Math.abs(npc.getY() - centerY) <= 4;
        }
        return false;
    }

    private static void handleAnimalCinematic(Player player, NPC npc, int pitIndex, int objX, int objY, int walkX, int walkY) {
        final boolean caught = Misc.random(100) < 60;

        if (caught) {
            npc.startAnimation(5234); // Fall
            World.getWorld().getEventHandler().submit(new Event<Player>("animal_fall", player, 2) {
                @Override
                public void execute() {
                    npc.startAnimation(5235); // Lying
                    updatePitfallState(attachment, pitIndex, 3);
                    attachment.getPA().createPlayersStillGfx(933, objX, objY, attachment.getHeight(), 0);
                    attachment.sendMessage("The creature falls into your trap!");
                    cleanupNpc(npc, 3);
                    super.stop();
                }
            });
        } else {
            // FAILED CATCH: Trap collapses (State 2), NPC leaps over and continues attack
            updatePitfallState(player, pitIndex, 2);
            player.sendMessage("The creature managed to leap over the trap.");

            npc.startAnimation(5231); // Start Jump
            int dirX = Integer.compare(walkX, 0);
            int dirY = Integer.compare(walkY, 0);
            final int landX = objX + (dirX * 2);
            final int landY = objY + (dirY * 2);

            World.getWorld().getEventHandler().submit(new Event<Player>("animal_leap", player, 1) {
                @Override
                public void execute() {
                    npc.startAnimation(5232); // Mid-air
                    npc.moveX = dirX;
                    npc.moveY = dirY;
                    npc.getNextNPCMovement();
                    npc.setX(landX);
                    npc.setY(landY);
                    npc.getLocation().setX(landX);
                    npc.getLocation().setY(landY);
                    npc.turnNpc(landX + dirX, landY + dirY);
                    npc.updateRequired = true;

                    World.getWorld().getEventHandler().submit(new Event<Player>("animal_land", attachment, 1) {
                        @Override
                        public void execute() {
                            npc.startAnimation(5233); // Landed
                            // Re-enable aggro
                            npc.randomWalk = true;
                            npc.underAttack = true;
                            npc.killerId = attachment.getIndex();
                            super.stop();
                        }
                    });
                    super.stop();
                }
            });
        }
    }

    private static void cleanupNpc(NPC npc, int delay) {
        World.getWorld().getEventHandler().submit(new Event<NPC>("npc_cleanup", npc, delay) {
            @Override
            public void execute() {
                attachment.isDead = true;
                attachment.applyDead = true;
                attachment.needRespawn = true;
                attachment.updateRequired = true;
                super.stop();
            }
        });
    }

    public static void teaseAnimal(Player player, NPC npc) {
        if (!player.getItems().playerHasItem(10029) && player.playerEquipment[player.playerWeapon] != 10029) {
            player.sendMessage("You need a teasing stick to get the creature's attention.");
            return;
        }
        player.startAnimation(5243);
        npc.killerId = player.getIndex();
        npc.spawnedBy = player.getIndex();
        npc.underAttack = true;
        npc.facePlayer(player.getIndex());
        npc.forceChat("Grrr!");
    }

    public static void dismantleTrap(Player player, int objectId) {
        int pitIndex = (objectId - 19253) % 16;
        int state = getPitfallState(player, pitIndex);
        player.startAnimation(827);
        if (state == 3) {
            player.getItems().addItem(10095, 1);
            player.getPA().addSkillXP(276, Skill.HUNTER.getId());
            player.sendMessage("You retrieve your catch from the pitfall.");
        } else {
            player.sendMessage("You dismantle the trap and clear the ruined logs.");
        }
        updatePitfallState(player, pitIndex, 0);
        ACTIVE_PITS.remove(pitIndex);
    }
}