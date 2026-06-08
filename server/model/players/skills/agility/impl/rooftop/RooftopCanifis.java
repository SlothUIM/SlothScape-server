package server.model.players.skills.agility.impl.rooftop;

import server.model.players.Player;
import server.model.players.skills.agility.MarkOfGrace;

/**
 * Rooftop Agility Canifis
 */
public class RooftopCanifis {

    public static final int TALL_TREE = 14843, JUMP_GAP = 14844, JUMP_2ND_GAP = 14845,
            JUMP_3RD_GAP = 14848, JUMP_4TH_GAP = 14846, POLE_VAULT = 14894,
            JUMP_5TH_GAP = 14847, JUMP_DOWN = 14897;

    public static int[] CANIFIS_OBJECTS = { TALL_TREE, JUMP_GAP, JUMP_2ND_GAP, JUMP_3RD_GAP, JUMP_4TH_GAP, POLE_VAULT, JUMP_5TH_GAP, JUMP_DOWN };

    public boolean execute(final Player c, final int objectId) {

        for (int id : CANIFIS_OBJECTS) {
            if (c.getAgilityHandler().checkLevel(c, objectId)) return false;
            if (id == objectId) MarkOfGrace.spawnMarks(c, "CANIFIS");
        }

        switch (objectId) {
            case TALL_TREE:
                AgilitySequence.create(c, 3, 0, TALL_TREE)
                        .walk(3507, 3489, 0)
                        .face(3508, 3489)
                        .waitUntil(3507, 3489, 0)
                        .slide(3505, 3489, 0, "EAST", 1765, 45, 90)
                        .waitUntil(3505, 3489, 0)
                        .stopAnim()
                        .teleport(3506, 3492, 2)
                        .waitUntil(3506, 3492, 2)
                        .xp(10)
                        .execute();
                return true;

            case JUMP_GAP:
                AgilitySequence.create(c, 3, 1, JUMP_GAP)
                        .walkOff()
                        .walk(3505, 3497, 2)
                        .face(3502, 3504)
                        .waitUntil(3505, 3497, -1)
                        .slide(3505, 3498, 2, "NORTH", 1995, 0, 40)
                        .anim(2586)
                        .waitUntil(3505, 3498, -1)
                        .anim(2588)
                        .teleport(3502, 3504, 2)
                        .waitUntil(3502, 3504, 2)
                        .xp(8)
                        .execute();
                return true;

            case JUMP_2ND_GAP:
                AgilitySequence.create(c, 3, 2, JUMP_2ND_GAP)
                        .face(3493, 3504)
                        .slide(3498, 3504, 2, "WEST", 2586, 0, 0)
                        .waitUntil(3498, 3504, -1)
                        .slide(3493, 3504, 2, "WEST", 2588, 0, 0)
                        .waitUntil(3493, 3504, -1)
                        .walk(3492, 3504, 2)
                        .waitUntil(3492, 3504, -1)
                        .xp(8)
                        .execute();
                return true;

            case JUMP_3RD_GAP:
                AgilitySequence.create(c, 3, 3, JUMP_3RD_GAP)
                        .slide(3486, 3499, 3485, 3499, 2, 1, "WEST", 2583, 0, 30) // Overload applied for Swimming failure offset matching
                        .waitUntil(3486, 3499, 2)
                        .slide(3480, 3499, 3, "WEST", 2585, 0, 30)
                        .waitUntil(3480, 3499, 3)
                        .stopAnim()
                        .teleport(3479, 3499, 3)
                        .xp(10)
                        .execute();
                return true;

            case JUMP_4TH_GAP:
                AgilitySequence.create(c, 3, 4, JUMP_4TH_GAP)
                        .walk(3479, 3493, 2)
                        .face(3479, 3486)
                        .anim(2586)
                        .waitUntil(3479, 3493, -1)
                        .anim(2588)
                        .teleport(3479, 3486, 2)
                        .waitUntil(3479, 3486, -1)
                        .xp(8)
                        .execute();
                return true;

            case POLE_VAULT:
                AgilitySequence.create(c, 3, 5, POLE_VAULT)
                        .teleport(3478, 3485, 2)
                        .stopAnim()
                        .face(3481, 3482)
                        .waitUntil(3478, 3485, 2)
                        .slide(3481, 3482, 2, "SOUTHEAST", 1995, 20, 60)
                        .waitUntil(3481, 3482, 2)
                        .slide(3489, 3476, 3489, 3476, 2, 0, "SOUTHEAST", 7132, 25, 120) // Kept your specific 0 offset
                        .waitUntil(3489, 3476, -1)
                        .anim(2588)
                        .teleport(3489, 3476, 3)
                        .xp(10)
                        .execute();
                return true;

            case JUMP_5TH_GAP:
                AgilitySequence.create(c, 3, 6, JUMP_5TH_GAP)
                        .walk(3503, 3473, 3)
                        .face(3510, 3476)
                        .anim(2586)
                        .waitUntil(3503, 3473, -1)
                        .anim(2588)
                        .teleport(3510, 3476, 2)
                        .waitUntil(3510, 3476, -1)
                        .xp(11)
                        .execute();
                return true;

            case JUMP_DOWN:
                AgilitySequence.create(c, 3, 7, JUMP_DOWN)
                        .npcAnimation(5921, 2, 863)
                        .face(3511, 3485)
                        .anim(2586)
                        .teleport(3510, 3482, 2)
                        .waitUntil(3510, 3482, -1)
                        .anim(2588)
                        .teleport(3511, 3485, 0)
                        .finish(175, 238)

                        .execute();
                return true;
        }
        return false;
    }
}