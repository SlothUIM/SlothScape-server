package server.model.players;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.event.Event;
import server.model.players.Client;
import server.world.World;

/**
 * Class WildernessDitch Handles Crossing the wilderness ditch
 * 
 * @author Organic 5-4-2012
 */

public class WildernessDitch {

	private static final int EMOTE = 6132;
	private static int AMOUNT_TO_MOVE = 3;

	
	public static void setDitch(Player p, int[][] path, String direction) {
	    if (p.isForceMovementActive()) return;

	    p.getMovementQueue().stop();
	    p.forceMovementActive = true;
	    p.isRunning2 = false;

	    // Start the process at index 0
	    processDitch(p, 0, path, direction);
	}

	private static void processDitch(Player p, int index, int[][] path, String direction) {
	    if (index >= path.length) {
	    	p.finalizeMove(); 
	        return;
	    }
	    p.startAnimation(6132); // Climb Up

	    // 2. COORDINATE SYNC
	    final int startX = p.getX();
	    final int startY = p.getY();
	    final int targetX = path[index][0];
	    final int targetY = path[index][1];

	    int baseRegionX = p.getLastKnownLocation().getRegionX() * 8;
	    int baseRegionY = p.getLastKnownLocation().getRegionY() * 8;

	    p.x1 = startX - baseRegionX;
	    p.y1 = startY - baseRegionY;
	    p.x2 = targetX - baseRegionX;
	    p.y2 = targetY - baseRegionY;

	    // Adjust speed: Crawling is usually slower than jumping stones
	    p.speed1 = 30; 
	    p.speed2 = 60; 
	    
	    switch (direction.toUpperCase()) {
	        case "NORTH": p.direction = 0; break;
	        case "EAST":  p.direction = 1; break;
	        case "SOUTH": p.direction = 2; break;
	        case "WEST":  p.direction = 3; break;
	    }

	    p.updateRequired = true;
	    p.forceMovement = true;
	    p.getPA().requestUpdates();

	    // 3. EVENT DELAY (Matches the speed of the crawl)
	    // Wall crawling is slow, so we use a 3-tick delay per tile
	    World.getWorld().getEventHandler().submit(new Event<Player>("ditch_jump", p, 3) {
	        @Override
	        public void execute() {
	            attachment.setX(targetX);
	            attachment.setY(targetY);
	            attachment.getLocation().setX(targetX);
	            attachment.getLocation().setY(targetY);
	            attachment.getPA().requestUpdates();

	            processDitch(p, index + 1, path, direction);
	            super.stop();
	        }
	    });
	}
	public static void movePlayer(Player c, int x, int y) {
		c.resetWalkingQueue();
		c.setX(x);
		c.setY(y);
		c.setNeedsPlacement(true);
		c.getPA().requestUpdates();
	}

	public static boolean isRunning;
	
	public static void wildernessDitchEnter(final Player c) {
		int[][] wallPath = {
	        {c.getX(), 3523}, //
	    };
		setDitch(c, wallPath, "NORTH");
	}

	public static void wildernessDitchLeave(final Player c) {

		int[][] wallPath = {
	        {c.getX(), 3520}, //
	    };
		setDitch(c, wallPath, "SOUTH");
	}
}