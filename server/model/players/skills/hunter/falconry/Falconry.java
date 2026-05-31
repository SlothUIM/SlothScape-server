package server.model.players.skills.hunter.falconry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import server.event.Event;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.world.World;
import server.util.Misc;

public class Falconry {

    public static final int FALCON_GLOVE_WITH_BIRD = 10024;
    public static final int FALCON_GLOVE_EMPTY = 10023;
    public static final int BONES = 526;
    public static final int BONECRUSHER = 13116;

    // Replaces killerId to prevent triggering the combat system
    private static final Map<NPC, Player> CAUGHT_KEBBITS = new HashMap<>();

    public enum KebbitData {
        SPOTTED(5531, 1342, 43, 104, 10125),
        DARK(5532, 1343, 57, 132, 10115),
        DASHING(5533, 1344, 69, 156, 10127);

        private final int aliveId;
        private final int caughtId;
        private final int reqLevel;
        private final int xp;
        private final int furId;

        KebbitData(int aliveId, int caughtId, int reqLevel, int xp, int furId) {
            this.aliveId = aliveId;
            this.caughtId = caughtId;
            this.reqLevel = reqLevel;
            this.xp = xp;
            this.furId = furId;
        }

        public static Optional<KebbitData> forId(int npcId) {
            for (KebbitData data : values()) {
                if (data.aliveId == npcId || data.caughtId == npcId) {
                    return Optional.of(data);
                }
            }
            return Optional.empty();
        }
    }

    public static void attemptCatch(Player player, NPC kebbit) {
        Optional<KebbitData> dataOpt = KebbitData.forId(kebbit.npcType);
        if (!dataOpt.isPresent() || kebbit.isDead) return;

        KebbitData data = dataOpt.get();

        if (player.getSkills().getLevel(Skill.HUNTER) < data.reqLevel) {
            player.sendMessage("You need a Hunter level of " + data.reqLevel + " to catch this kebbit.");
            return;
        }

        if (player.playerEquipment[player.playerWeapon] != FALCON_GLOVE_WITH_BIRD) {
            player.sendMessage("You need a loaded falconer's glove equipped to catch this.");
            return;
        }

        int distance = Misc.distanceToPoint(player.getX(), player.getY(), kebbit.getX(), kebbit.getY());
        if (distance > 15) {
            player.sendMessage("You need to get closer to send your falcon.");
            return;
        }

        player.getMovementQueue().stop();
        player.turnPlayerTo(kebbit.getX(), kebbit.getY());

        // Capture the exact tile the kebbit is on right NOW
        final int targetX = kebbit.getX();
        final int targetY = kebbit.getY();

        // Remove the bird from the player's arm & play take-off GFX
        player.getItems().wearItem(FALCON_GLOVE_EMPTY, 1, player.playerWeapon);
        player.startAnimation(5162);
        player.gfx100(918);

        int pX = player.getX();
        int pY = player.getY();

        // Because createProjectile writes offY before offX,
        // we can just use normal, logical offsets!
        int offX = (targetX - pX);
        int offY = (targetY - pY);

        int speed = 50;
        int flightTime = speed + (distance * 5);

        // Fire the falcon using the raw packet method! (Lockon = 0 for static tile)
        player.getPA().createPlayersProjectile(
                pX, pY,
                offX, offY,
                75, speed, 922, 31, 0, 1, flightTime
        );
        int eventDelay = 1 + (distance / 3);
        World.getWorld().getEventHandler().submit(new Event<Player>("falcon_flight", player, eventDelay) {
            @Override
            public void execute() {

                // 1. SKILL SHOT CHECK: Did the kebbit walk away while the bird was flying?
                if (kebbit.getX() != targetX || kebbit.getY() != targetY) {
                    player.sendMessage("The falcon swoops down, but the kebbit had already moved!");
                    attachment.getItems().wearItem(FALCON_GLOVE_WITH_BIRD, 1, attachment.playerWeapon);
                    super.stop();
                    return;
                }

                // 2. RNG CHECK: If it stood still, roll for actual catch success
                int chance = (player.getSkills().getLevel(Skill.HUNTER) - data.reqLevel) + (15 - distance);
                boolean caught = Misc.random(20) <= chance || chance > 15;

                if (caught) {
                    player.sendMessage("The falcon successfully swoops down and catches the kebbit!");

                    // Now that it's caught, we freeze it and transform it
                    kebbit.getMovementQueue().stop();
                    kebbit.randomWalk = false;
                    kebbit.requestTransform(data.caughtId);

                    CAUGHT_KEBBITS.put(kebbit, player);

                    kebbit.gfx100(923);

                    startTimeoutEvent(attachment, kebbit);
                } else {
                    player.sendMessage("The falcon swoops down, but misses the kebbit.");
                    attachment.getItems().wearItem(FALCON_GLOVE_WITH_BIRD, 1, attachment.playerWeapon);
                }
                super.stop();
            }
        });
    }

    public static void retrieveCatch(Player player, NPC caughtKebbit) {
        Optional<KebbitData> dataOpt = KebbitData.forId(caughtKebbit.npcType);
        if (!dataOpt.isPresent()) return;

        KebbitData data = dataOpt.get();

        // Safe ownership check
        if (CAUGHT_KEBBITS.get(caughtKebbit) != player) {
            player.sendMessage("This isn't your catch.");
            return;
        }

        if (Misc.distanceToPoint(player.getX(), player.getY(), caughtKebbit.getX(), caughtKebbit.getY()) > 1) {
            return;
        }

        player.getMovementQueue().stop();
        player.startAnimation(827);

        if (player.getItems().playerHasItem(BONECRUSHER) || player.getItems().playerHasItem(13116)) {
            player.getPA().addSkillXP(4, Skill.PRAYER.getId());
            player.sendMessage("Your bonecrusher crushes the kebbit bones.");
        } else {
            player.getItems().addItem(BONES, 1);
        }

        if (player.getItems().playerHasItem(22416)) {
            player.sendMessage("You stuff the fur into your fur pouch.");
        } else {
            player.getItems().addItem(data.furId, 1);
        }

        player.getPA().addSkillXP(data.xp, Skill.HUNTER.getId());

        player.getItems().wearItem(FALCON_GLOVE_WITH_BIRD, 1, player.playerWeapon);
        //player.getPA().drawHintText(0);

        // Clean up ownership and respawn
        CAUGHT_KEBBITS.remove(caughtKebbit);
        caughtKebbit.isDead = true;
        caughtKebbit.applyDead = true;
        caughtKebbit.needRespawn = true;
        caughtKebbit.requestTransform(data.aliveId);
        caughtKebbit.updateRequired = true;
    }

    private static void startTimeoutEvent(Player player, NPC caughtKebbit) {
        World.getWorld().getEventHandler().submit(new Event<Player>("falcon_timeout", player, 50) {
            @Override
            public void execute() {
                if (!caughtKebbit.isDead && CAUGHT_KEBBITS.get(caughtKebbit) == attachment) {
                    attachment.sendMessage("Your falcon has left its prey. You see it heading back toward the falconer.");
                    //attachment.getPA().drawHintText(0);
                    attachment.getItems().wearItem(FALCON_GLOVE_WITH_BIRD, 1, attachment.playerWeapon);

                    CAUGHT_KEBBITS.remove(caughtKebbit);
                    caughtKebbit.isDead = true;
                    caughtKebbit.applyDead = true;
                    caughtKebbit.needRespawn = true;

                    Optional<KebbitData> dataOpt = KebbitData.forId(caughtKebbit.npcType);
                    dataOpt.ifPresent(kebbitData -> caughtKebbit.requestTransform(kebbitData.aliveId));
                    caughtKebbit.updateRequired = true;
                }
                super.stop();
            }
        });
    }
}