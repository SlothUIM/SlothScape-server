package server.model.players.skills.agility.impl.rooftop;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.agility.MarkOfGrace;

/**
 * Rooftop Agility Canifis
 * Generated via Slothscape Agility Studio V5
 */
public class RooftopCanifis {

    public static final int TALL_TREE = 14843,
            JUMP_GAP = 14844,
            JUMP_2ND_GAP = 14845,
            JUMP_3RD_GAP = 14848,
            JUMP_4TH_GAP = 14846,
            POLE_VAULT = 14894,
            JUMP_5TH_GAP = 14847,
            JUMP_DOWN = 14897;

    public static int[] CANIFIS_OBJECTS = { TALL_TREE, JUMP_GAP, JUMP_2ND_GAP, JUMP_3RD_GAP, JUMP_4TH_GAP, POLE_VAULT, JUMP_5TH_GAP, JUMP_DOWN };

    public boolean execute(final Player c, final int objectId) {

        for (int id : CANIFIS_OBJECTS) {
            if (c.getAgilityHandler().checkLevel(c, objectId)) {
                return false;
            }
            if (id == objectId) {
                MarkOfGrace.spawnMarks(c, "CANIFIS");
            }
        }

        switch (objectId) {
            case TALL_TREE:
                if (c.getAgilityHandler().RoofAgilityProgress[3][0] || true) { // First obstacle, always true
                    // 1. Climb the trunk to Height 2
                    c.setMove(new int[][]{{3506, 3489}}, "NORTH", 828, -1, 30, 60, 3506, 3489, 1, 1, 1, 2);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getHeight() == 2) {
                                // 2. Vault forward onto the roof tiles
                                c.setMove(new int[][]{{3506, 3492}}, "NORTH", 2585, -1, 0, 30, 3506, 3492, 1, 1, 1, 2);
                                c.getAgilityHandler().RoofAgilityProgress[3][0] = true;
                                c.getAgilityHandler().lapProgress(c, 0, TALL_TREE, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 2);
                }
                return true;

            case JUMP_GAP:
                c.getAgilityHandler().RoofAgilityProgress[3][1] = true;
                c.getAgilityHandler().lapProgress(c, 1, JUMP_GAP, 3);
                return true;

            case JUMP_2ND_GAP:
                c.getAgilityHandler().RoofAgilityProgress[3][2] = true;
                c.getAgilityHandler().lapProgress(c, 2, JUMP_2ND_GAP, 3);
                return true;

            case JUMP_3RD_GAP:
                c.getAgilityHandler().RoofAgilityProgress[3][3] = true;
                c.getAgilityHandler().lapProgress(c, 3, JUMP_3RD_GAP, 3);
                return true;

            case JUMP_4TH_GAP:
                c.getAgilityHandler().RoofAgilityProgress[3][4] = true;
                c.getAgilityHandler().lapProgress(c, 4, JUMP_4TH_GAP, 3);
                return true;

            case POLE_VAULT:
                c.getAgilityHandler().RoofAgilityProgress[3][5] = true;
                c.getAgilityHandler().lapProgress(c, 5, POLE_VAULT, 3);
                return true;

            case JUMP_5TH_GAP:
                c.getAgilityHandler().RoofAgilityProgress[3][6] = true;
                c.getAgilityHandler().lapProgress(c, 6, JUMP_5TH_GAP, 3);
                return true;

            case JUMP_DOWN:
                c.getAgilityHandler().RoofAgilityProgress[3][7] = true;
                c.getAgilityHandler().lapProgress(c, 7, JUMP_DOWN, 3);
                return true;
        }
        return false;
    }
}