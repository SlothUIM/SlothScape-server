package server.model.npcs;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

import server.clip.Region;
import server.clip.Tile;
import server.clip.TileControl;
import server.world.Location;
import server.model.Entity;
import server.model.players.Player;
import server.util.Misc;

public class NPCDumbPathFinder {

	private static final int NORTH = 0, EAST = 1,  SOUTH = 2, WEST = 3;

	public static void follow(NPC npc, Entity following) {

		Tile[] npcTiles = TileControl.getTiles(npc);

		int[] npcLocation = TileControl.currentLocation(npc);
		int[] followingLocation = TileControl.currentLocation(following);

		/** test 4 movements **/
		boolean[] moves = new boolean[4];

		int dir = -1;

		int distance = TileControl.calculateDistance(npc, following);

		if (distance > 16) {
			return;
		}

		npc.faceEntity(following);

		if (npc.freezeTimer > 0) {
			return;
		}

		if (distance > 1) { //continue pathing to follow point

			for (int i = 0; i < moves.length; i++) {
				moves[i] = true;
			}

			/** remove false moves **/
			if (npcLocation[0] < followingLocation[0]) {
				moves[EAST] = true;
				moves[WEST] = false;
			} else if (npcLocation[0] > followingLocation[0]) {
				moves[WEST] = true;
				moves[EAST] = false;
			} else {
				moves[EAST] = false;
				moves[WEST] = false;
			}
			if (npcLocation[1] > followingLocation[1]) {
				moves[SOUTH] = true;
				moves[NORTH] = false;
			} else if (npcLocation[1] < followingLocation[1]) {
				moves[NORTH] = true;
				moves[SOUTH] = false;
			} else {
				moves[NORTH] = false;
				moves[SOUTH] = false;
			}
			for (Tile tiles : npcTiles) {
				if (tiles.getTile()[0] == following.getX()) { //same x line
					moves[EAST] = false;
					moves[WEST] = false;
				} else if (tiles.getTile()[1] == following.getY()) { //same y line
					moves[NORTH] = false;
					moves[SOUTH] = false;
				}
			}
			boolean[] blocked = new boolean[3];

			if (moves[NORTH] && moves[EAST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedNorthEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) {  //northeast
					dir = 2;
				} else if (!blocked[0]) { //north
					dir = 0;
				} else if (!blocked[1]) { //east
					dir = 4;
				}

			} else if (moves[NORTH] && moves[WEST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedNorthWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //north-west
					dir = 14;
				} else if (!blocked[0]) { //north
					dir = 0;
				} else if (!blocked[1]) { //west
					dir = 12;
				}
			} else if (moves[SOUTH] && moves[EAST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedSouthEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-east
					dir = 6;
				} else if (!blocked[0]) { //south
					dir = 8;
				} else if (!blocked[1]) { //east
					dir = 4;
				}
			} else if (moves[SOUTH] && moves[WEST]) {
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[0] = true;
					}
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[1] = true;
					}
					if (Region.blockedSouthWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						blocked[2] = true;
					}
				}
				if (!blocked[2] && !blocked[0] && !blocked[1]) { //south-west
					dir = 10;
				} else if (!blocked[0]) { //south
					dir = 8;
				} else if (!blocked[1]) { //west
					dir = 12;
				}

			} else if (moves[NORTH]) {
				dir = 0;
				for (Tile tiles : npcTiles) {
					if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[EAST]) {
				dir = 4;
				for (Tile tiles : npcTiles) {
					if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[SOUTH]) {
				dir = 8;
				for (Tile tiles : npcTiles) {
					if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			} else if (moves[WEST]) {
				dir = 12;
				for (Tile tiles : npcTiles) {
					if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
						dir = -1;
					}
				}
			}
		} else if (distance == 0) {
			for (int i = 0; i < moves.length; i++) {
				moves[i] = true;
			}
			for (Tile tiles : npcTiles) {

				if (Region.blockedNorth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[NORTH] = false;
				}
				if (Region.blockedEast(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[EAST] = false;
				}
				if (Region.blockedSouth(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[SOUTH] = false;
				}
				if (Region.blockedWest(tiles.getTileX(), tiles.getTileY(), tiles.getTileHeight())) {
					moves[WEST] = false;
				}
			}
			int randomSelection = Misc.random(3);

			if (moves[randomSelection]) {
				dir = randomSelection * 4;
			} else if (moves[NORTH]) {
				dir = 0;
			} else if (moves[EAST]) {
				dir = 4;
			} else if (moves[SOUTH])	{
				dir = 8;
			} else if (moves[WEST]) {
				dir = 12;
			}
		}

		if (dir == -1) {
			return;
		}

		dir >>= 1;

		if (dir < 0) {
			return;
		}

		npc.moveX = Misc.directionDeltaX[dir];
		npc.moveY = Misc.directionDeltaY[dir];
		npc.getNextNPCMovement();
		npc.updateRequired = true;

	}

	public static Location walkTowards(NPC npc, Entity entity) {
		return walkTowards(npc, entity.getX(), entity.getY());
	}

	public static Location walkTowards(NPC npc, int x, int y) {
		return walkTowards(npc, x, y, false);
	}

	public static Location walkTowards(NPC npc, int waypointx, int waypointy, boolean ignoreClipping) {
		int x = npc.getX();
		int y = npc.getY();

		if (waypointx == x && waypointy == y) {
			return null;
		}

		int direction = -1;
		final int xDifference = waypointx - x;
		final int yDifference = waypointy - y;

		int toX = 0;
		int toY = 0;

		if (xDifference > 0) {
			toX = 1;
		} else if (xDifference < 0) {
			toX = -1;
		}

		if (yDifference > 0) {
			toY = 1;
		} else if (yDifference < 0) {
			toY = -1;
		}

		int toDir = NPCClipping.getDirection(x, y, x + toX, y + toY);


		// Primary direction
		if (canMoveTo(npc, toDir) || ignoreClipping) {
			direction = toDir;
		} else {
			// Try all 8 directions, prioritize those closer to target
			int bestDir = -1;
			int bestDist = Integer.MAX_VALUE;

			for (int dirTry = 0; dirTry < 8; dirTry++) {
				if (!canMoveTo(npc, dirTry))
					continue;

				int testX = x + NPCClipping.DIR[dirTry][0];
				int testY = y + NPCClipping.DIR[dirTry][1];

				int dist = Math.abs(testX - waypointx) + Math.abs(testY - waypointy);
				if (dist < bestDist) {
					bestDist = dist;
					bestDir = dirTry;
				}
			}

			direction = bestDir;
		}


		if (direction == -1) {
			return null;
		}

		List<Location> path = new ArrayList<>();
		int steps = 2; // Walk up to 2 tiles in one direction if not blocked

		int cx = x, cy = y;
		for (int i = 0; i < steps; i++) {
			int dx = Integer.compare(waypointx - cx, 0);
			int dy = Integer.compare(waypointy - cy, 0);

			if (dx == 0 && dy == 0)
				break;

			int dir = NPCClipping.getDirection(cx, cy, cx + dx, cy + dy);
			if (canMoveTo(npc, dir)) {
				cx += dx;
				cy += dy;
				path.add(new Location(cx, cy, npc.getHeight()));
			} else {
				break;
			}
		}

		if (!path.isEmpty()) {
			npc.setWalkQueue(path);
		}

		return new Location(npc.moveX, npc.moveY);
	}

	public static boolean canMoveTo(final NPC mob, final int direction) {
		return canMoveTo(mob, mob.getX(), mob.getY(), direction);
	}

	public static boolean canMoveTo(final NPC mob, int startX, int startY, final int direction) {
		if (direction == -1) {
			return false;
		}

		final int z = mob.getHeight();

		final int destX = startX + NPCClipping.DIR[direction][0];
		final int destY = startY + NPCClipping.DIR[direction][1];

		final int size = mob.getSize();

		for (int i = 1; i < size + 1; i++) {
			for (int k = 0; k < NPCClipping.SIZES[i].length; k++) {
				int x3 = startX + NPCClipping.SIZES[i][k][0];
				int y3 = startY + NPCClipping.SIZES[i][k][1];

				int x2 = destX + NPCClipping.SIZES[i][k][0];
				int y2 = destY + NPCClipping.SIZES[i][k][1];

				if (NPCClipping.withinBlock(startX, startY, size, x2, y2)) {
					continue;
				}

				Region region = Region.getRegion(x3, y3);
				if (region == null)
					return false;

				if (!Region.getRegion(x3, y3).canMove(x3, y3, z, direction)) {
					return false;
				}

				for (int j = 0; j < 8; j++) {
					int x6 = x3 + NPCClipping.DIR[j][0];
					int y6 = y3 + NPCClipping.DIR[j][1];

					if (NPCClipping.withinBlock(destX, destY, size, x6, y6)) {
						if (!Region.getRegion(x3, y3).canMove(x3, y3, z, j)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	private static final int[][] DIR = {
			{0, 1},  {1, 0},  {0, -1}, {-1, 0},  // cardinal
			{1, 1},  {1, -1}, {-1, -1}, {-1, 1}  // diagonal
	};

	/**
	 * BFS pathfinding that works on world coordinates.
	 */
	public static List<Location> findPath(NPC npc, int destX, int destY) {
		int srcX = npc.getX();
		int srcY = npc.getY();
		int z = npc.getHeight();

		// BFS bounds: expand enough to reach destination
		int minX = Math.min(srcX, destX) - 10;
		int maxX = Math.max(srcX, destX) + 10;
		int minY = Math.min(srcY, destY) - 10;
		int maxY = Math.max(srcY, destY) + 10;

		int width = maxX - minX + 1;
		int height = maxY - minY + 1;

		boolean[][] visited = new boolean[width][height];
		int[][] via = new int[width][height];
		for (int i = 0; i < width; i++) Arrays.fill(via[i], -1);

		Queue<int[]> queue = new ArrayDeque<>();
		queue.add(new int[]{srcX, srcY});
		visited[srcX - minX][srcY - minY] = true;

		while (!queue.isEmpty()) {
			int[] cur = queue.poll();
			int x = cur[0], y = cur[1];

			if (x == destX && y == destY) {
				return buildPath(npc, via, destX, destY, minX, minY);
			}

			// Loop through 8 directions
			for (int dir = 0; dir < DIR.length; dir++) {
				int nx = x + DIR[dir][0];
				int ny = y + DIR[dir][1];

				if (nx < minX || nx > maxX || ny < minY || ny > maxY) continue;
				if (visited[nx - minX][ny - minY]) continue;

				// Check movement including diagonal clipping
				if (!canMoveTo(npc, x, y, DIR[dir][0], DIR[dir][1])) continue;

				queue.add(new int[]{nx, ny});
				visited[nx - minX][ny - minY] = true;
				via[nx - minX][ny - minY] = dir;
			}
		}

		return Collections.emptyList();
	}


	/**
	 * Backtrack BFS 'via' array into path.
	 */
	private static List<Location> buildPath(NPC npc, int[][] via, int destX, int destY, int minX, int minY) {
		LinkedList<Location> steps = new LinkedList<>();
		int x = destX;
		int y = destY;
		int z = npc.getHeight();

		while (x != npc.getX() || y != npc.getY()) {
			steps.addFirst(new Location(x, y, z));
			int dir = via[x - minX][y - minY];
			if (dir == -1) break; // safety
			x -= DIR[dir][0]; // reverse
			y -= DIR[dir][1];
		}

		return steps;
	}

	/**
	 * Checks whether the NPC can move one step from (x, y) in direction (dx, dy).
	 */
	public static boolean canMoveTo(NPC npc, int x, int y, int dx, int dy) {
		int size = npc.getSize();
		int z = npc.getHeight();

		int nx = x + dx;
		int ny = y + dy;

		int dir = getDirection(dx, dy);
		if (dir == -1) return false;

		// Check all NPC tiles except the final destination tile
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int checkX = nx + i;
				int checkY = ny + j;

				// Skip the destination tile itself
				if (checkX == nx + size - 1 && checkY == ny + size - 1) continue;

				Region region = Region.getRegion(checkX, checkY);
				if (region == null) return false;

				if (!region.canMove(checkX, checkY, z, dir))
					return false;

				// Diagonal: only block if both adjacent tiles blocked
				if (dx != 0 && dy != 0) {
					Region hor = Region.getRegion(x + dx, y + j);
					Region ver = Region.getRegion(x + i, y + dy);
					if (hor == null || ver == null) return false;

					boolean blockedHor = !hor.canMove(x + dx, y + j, z, dx > 0 ? 4 : 3);
					boolean blockedVer = !ver.canMove(x + i, y + dy, z, dy > 0 ? 1 : 0);
					if (blockedHor && blockedVer) return false;
				}
			}
		}

		return true;
	}

	public static final int getDirection(int x, int y) {
		for (int i = 0; i < 8; i++) {
			if (DIR[i][0] == x && DIR[i][1] == y)
				return i;
		}

		return -1;
	}





}