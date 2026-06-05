package server.model.players.skills.agility.impl.rooftop;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.agility.MarkOfGrace;
import server.world.Location;

/**
 * Rooftop Agility Canifis
 * Generated via Slothscape Agility Studio V7
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
                // 1. Path to the base of the tree
                c.getMovementQueue().addStep(3507, 3489, 0);
                //c.setMove(new int[][]{{3507, 3489}}, "NORTH", 1209, -1, 0, 30, 3507, 3489, 1, 1, 1, 0);

                c.turnPlayerTo(3508, 3489);
                //c.getPA().requestUpdates();
                CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {

                        // 2. We reached the base. Face the trunk and slide across the grass while climbing
                        if (c.getX() == 3507 && c.getY() == 3489 && c.getHeight() == 0) {
                            c.setMove(new int[][]{{3505, 3489}}, "EAST", 1765, -1, 45, 90, 3505, 3489, 1, 1, 1, 0);
                        }
                        // 3. We reached the end of the slide. Teleport instantly to the roof!
                        else if (c.getX() == 3505 && c.getY() == 3489 && c.getHeight() == 0) {
                            c.stopAnimation();
                            c.getPA().movePlayer(3506, 3492, 2);
                        }
                        // 4. We are on the roof. Grant XP and stop.
                        else if (c.getX() == 3506 && c.getY() == 3492 && c.getHeight() == 2) {
                            c.getAgilityHandler().RoofAgilityProgress[3][0] = true;
                            c.getAgilityHandler().lapProgress(c, 0, TALL_TREE, 3);
                            container.stop();
                        }
                    }
                    @Override
                    public void stop() {}
                }, 2);
                return true;

            case JUMP_GAP:
                if (c.getAgilityHandler().RoofAgilityProgress[3][0]) {
                    if(c.isRunning())
                        c.isRunning = false;
                    c.getMovementQueue().addStep(3505, 3497, 2);

                    c.turnPlayerTo(3502, 3504);
                    //c.setMove(new int[][]{{3505, 3497}}, "NORTH", 1995, -1, 0, 30, 3505, 3497, 1, 1, 1, 2);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getY() == 3497 && c.getX() == 3505) {
                                c.setMove(new int[][]{{3505, 3498}}, "NORTH", 1995, -1, 0, 40, 3505, 3498, 1, 1, 1, 2);
                                c.startAnimation(2586);
                            } else if (c.getY() == 3498 && c.getX() == 3505) {
                                c.startAnimation(2588);
                                c.getPA().movePlayer(3502, 3504, 2);
                                //c.setMove(new int[][]{{3505, 3499}}, "NORTH", 2586, -1, 0, 40, 3505, 3499, 1, 1, 1, 2);
                            } else if (c.getY() == 3504 && c.getX() == 3502 && c.getHeight() == 2) {
                                //c.setMove(new int[][]{{3502, 3504}}, "NORTH", 2588, -1, 0, 30, 3502, 3504, 1, 1, 1, 2);
                                c.getAgilityHandler().RoofAgilityProgress[3][1] = true;
                                c.getAgilityHandler().lapProgress(c, 1, JUMP_GAP, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case JUMP_2ND_GAP:
                if (c.getAgilityHandler().RoofAgilityProgress[3][1]) {
                    c.turnPlayerTo(3493, 3504);
                    c.setMove(new int[][]{{3498, 3504}}, "WEST", 2586, -1, 0, 0, 3498, 3504, 1, 1, 1, 2);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getY() == 3504 && c.getX() == 3498) {
                                c.setMove(new int[][]{{3493, 3504}}, "WEST", 2588, -1, 0, 0, 3493, 3504, 1, 1, 1, 2);
                            } else if (c.getY() == 3504 && c.getX() == 3493) {
                                c.getMovementQueue().addStep(3492, 3504, 2);
                            } else if (c.getY() == 3504 && c.getX() == 3492) {

                                c.getAgilityHandler().RoofAgilityProgress[3][2] = true;
                                c.getAgilityHandler().lapProgress(c, 2, JUMP_2ND_GAP, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case JUMP_3RD_GAP:
                if (c.getAgilityHandler().RoofAgilityProgress[3][2]) {
                    c.setMove(new int[][]{{3486, 3499}}, "WEST", 2583, -1, 0, 30, 3485, 3499, 1, 1, 1, 2);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getX() == 3486 && c.getY() == 3499 && c.getHeight() == 2) {
                                c.setMove(new int[][]{{3480, 3499}}, "WEST", 2585, -1, 0, 30, 3480, 3499, 1, 1, 1, 3);
                            } else if (c.getX() == 3480 && c.getY() == 3499 && c.getHeight() == 3) {
                                c.stopAnimation();
                                c.getPA().movePlayer(3479, 3499, 3);
                                c.getAgilityHandler().RoofAgilityProgress[3][3] = true;
                                c.getAgilityHandler().lapProgress(c, 3, JUMP_3RD_GAP, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case JUMP_4TH_GAP:


                if (c.getAgilityHandler().RoofAgilityProgress[3][3]) {
                    c.getMovementQueue().addStep(3479, 3493, 2);
                    c.turnPlayerTo(3479, 3486);
                    c.startAnimation(2586);
                   // c.setMove(new int[][]{{3479, 3504}}, "SOUTH", 2586, -1, 0, 0, 3479, 3504, 1, 1, 1, 2);
                   CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getY() == 3493 && c.getX() == 3479) {
                                c.startAnimation(2588);
                                c.getPA().movePlayer(3479, 3486, 2);

                            } else if (c.getY() == 3486 && c.getX() == 3479) {
                                //c.getPA().movePlayer(3479, 3486, 3);
                               // c.setMove(new int[][]{{3493, 3504}}, "SOUTH", 2588, -1, 0, 0, 3493, 3504, 1, 1, 1, 2);

                                c.getAgilityHandler().RoofAgilityProgress[3][4] = true;
                                c.getAgilityHandler().lapProgress(c, 4, JUMP_4TH_GAP, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case POLE_VAULT:
                if (c.getAgilityHandler().RoofAgilityProgress[3][4]) {
                    c.getPA().movePlayer(3478, 3485, 2);
                    c.stopAnimation();
                    c.turnPlayerTo(3481, 3482);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getX() == 3478 && c.getY() == 3485 && c.getHeight() == 2) {
                                c.setMove(new int[][]{{3481, 3482}}, "SOUTHEAST", 1995, -1, 20, 60, 3481, 3482, 1, 1, 1, 2);
                            } else if (c.getX() == 3481 && c.getY() == 3482 && c.getHeight() == 2) {
                                c.setMove(new int[][]{{3489, 3476}}, "SOUTHEAST", 7132, -1, 25, 125, 3489, 3476, 0, 1, 1, 2);
                            } else if (c.getX() == 3489 && c.getY() == 3476) {
                                c.startAnimation(2588);
                                c.getPA().movePlayer(3489, 3476, 3);
                                c.getAgilityHandler().RoofAgilityProgress[3][5] = true;
                                c.getAgilityHandler().lapProgress(c, 5, POLE_VAULT, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case JUMP_5TH_GAP:
                if (c.getAgilityHandler().RoofAgilityProgress[3][5]) {
                    c.getMovementQueue().addStep(3503, 3473, 3);
                    c.turnPlayerTo(3510, 3476);
                    c.startAnimation(2586);
                    // c.setMove(new int[][]{{3479, 3504}}, "SOUTH", 2586, -1, 0, 0, 3479, 3504, 1, 1, 1, 2);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getX() == 3503 && c.getY() == 3473) {
                                c.startAnimation(2588);
                                c.getPA().movePlayer(3510, 3476, 2);

                            } else if (c.getY() == 3476 && c.getX() == 3510) {
                                //c.getPA().movePlayer(3479, 3486, 3);
                                // c.setMove(new int[][]{{3493, 3504}}, "SOUTH", 2588, -1, 0, 0, 3493, 3504, 1, 1, 1, 2);

                                c.getAgilityHandler().RoofAgilityProgress[3][6] = true;
                                c.getAgilityHandler().lapProgress(c, 6, JUMP_5TH_GAP, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 1);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;

            case JUMP_DOWN:
                if (c.getAgilityHandler().RoofAgilityProgress[3][6]) {
                    c.turnPlayerTo(3511, 3485);
                    c.startAnimation(2586);
                    c.getPA().movePlayer(3510, 3482, 2);
                   // c.setMove(new int[][]{{0, 0}}, "NORTH", 2586, -1, 0, 60, 0, 0, 1, 1, 1, 0);
                    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (c.getX() == 3510 && c.getY() == 3482) {
                                c.startAnimation(2588);
                                c.getPA().movePlayer(3511, 3485, 0);
                                c.getAgilityHandler().RoofAgilityProgress[3][7] = true;
                                c.getAgilityHandler().roofTopFinished(c, 7, 8000, 238, 3);
                                container.stop();
                            }
                        }
                        @Override
                        public void stop() {}
                    }, 2);
                } else {
                    c.sendMessage("Apparently I skipped a gap, ouch..");
                }
                return true;
        }
        return false;
    }
}