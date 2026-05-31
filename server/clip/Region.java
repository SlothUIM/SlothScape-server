package server.clip;

import java.awt.Rectangle;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import server.world.Location;
import server.world.World;
import server.model.Entity;
import server.model.npcs.NPC;
import server.model.npcs.NPCClipping;
import server.world.Boundary;
import server.model.players.Player;
import server.model.map.Palette;
import server.model.map.PaletteTile;
import server.util.MapTileUtils;
import server.util.Misc;
import server.util.Utilities;
import server.world.objects.GlobalObject;

@Slf4j
public class Region {


	private static Map<Integer, Region> regions = Maps.newConcurrentMap();
	private int id;
	private Map<Integer, int[][]> clips = Maps.newConcurrentMap();
	private Map<Integer, int[][]> shootable = Maps.newConcurrentMap();
	private boolean members = false;

	public static final int PROJECTILE_NORTH_WEST_BLOCKED = 0x200;
	public static final int PROJECTILE_NORTH_BLOCKED = 0x400;
	public static final int PROJECTILE_NORTH_EAST_BLOCKED = 0x800;
	public static final int PROJECTILE_EAST_BLOCKED = 0x1000;
	public static final int PROJECTILE_SOUTH_EAST_BLOCKED = 0x2000;
	public static final int PROJECTILE_SOUTH_BLOCKED = 0x4000;
	public static final int PROJECTILE_SOUTH_WEST_BLOCKED = 0x8000;
	public static final int PROJECTILE_WEST_BLOCKED = 0x10000;
	public static final int PROJECTILE_TILE_BLOCKED = 0x20000;
	public static final int UNKNOWN = 0x80000;
	public static final int BLOCKED_TILE = 0x200000;
	public static final int UNLOADED_TILE = 0x1000000;
	public static final int OCEAN_TILE = 2097152;

	/**
	 * Finds a free region starting at X 10240 Y 10240
	 * @param width The number of regions to cover in the X direction
	 * @param length The number of regions to cover in the Y direction
	 * @return The lowest X and Y position of the free region
	 */
	public static Optional<Location> findFreeRegion(int width, int length) {
		for(int x = 160;x<220;x++) {
			for(int y = 160;y<220;y++) {
				int regionId = (x << 8) + y;
				boolean regionExists = regions.containsKey(regionId);
				if(!regionExists) {
					boolean regionFree = true;
					for(int x2 = x + 1;x2 <= x + width;x2++) {
						for(int y2 = y + 1; y2 <= y + length;y2++) {
							if(!regionFree)
								break;
							int nextRegionId = (x << 8) + y;
							if(regions.containsKey(nextRegionId)) {
								regionFree = false;
								break;
							}
						}
					}
					if(regionFree)
						return Optional.ofNullable(new Location(x * 64, y * 64));
				}
			}
		}
		return Optional.empty();
	}


	/**
	 * A map containing each region as the key, and a Collection of real world objects as the value.
	 */
	private static HashMap<Integer, ArrayList<WorldObject>> worldObjects = new HashMap<>();

	/**
	 * Determines if an object is real or not. If the Collection of regions and real objects contains the properties passed in the parameters then the object will be determined
	 * real
	 *
	 * @param id the id of the object
	 * @param x the x coordinate of the object
	 * @param y the y coordinate of the object
	 * @param height the height of the object
	 * @return
	 */
	public static boolean isWorldObject(int id, int x, int y, int height) {
		Region region = getRegion(x, y);
		if (region == null) {
			return true;
		}
		Collection<WorldObject> regionObjects = worldObjects.get(region.id);
		if (regionObjects == null) {
			return true;
		}
		return regionObjects.stream().anyMatch(object -> object.id == id && object.x == x && object.y == y && (object.height == (height & 3) || object.height == height));
	}

	/**
	 * Determines if an object is real or not. If the Collection of regions and real objects contains the properties passed in the parameters then the object will be determined
	 * real
	 *
	 * @param x the x coordinate of the object
	 * @param y the y coordinate of the object
	 * @param height the height of the object
	 * @return
	 */
	public static boolean solidObjectExists(int x, int y, int height) {
		Region region = getRegion(x, y);
		if (region == null) {
			return false;
		}
		Collection<WorldObject> regionObjects = worldObjects.get(region.id);
		if (regionObjects == null) {
			return false;
		}
		Optional<WorldObject> exists = regionObjects.stream().filter(object -> object.type != 22 && object.x == x && object.y == y && object.height == height).findFirst();
		if(!exists.isPresent())
			exists = regionObjects.stream().filter(object ->  object.type != 22 && object.x == x && object.y == y && object.height == (height & 3)).findFirst();

		return exists.isPresent();
	}

	public static Optional<WorldObject> getWorldObject(int id, int x, int y, int height, int type) {

		Region region = getRegion(x, y);
		if (region == null) {
			System.out.println("region is null");
			return Optional.empty();
		}
		Collection<WorldObject> regionObjects = worldObjects.get(region.id);
		if (regionObjects == null) {
			return Optional.empty();
		}
		Optional<WorldObject> exists = regionObjects.stream().filter(object -> (type == -1 || object.type == type) && object.id == id && object.x == x && object.y == y && object.height == height).findFirst();
		if(!exists.isPresent())
			exists = regionObjects.stream().filter(object -> (type == -1 || object.type == type) && object.id == id && object.x == x && object.y == y && object.height == (height & 3)).findFirst();

		return exists;
	}

	public static HashMap<Integer, ArrayList<WorldObject>> getWorldObjects() {
		return worldObjects;
	}

	public int getId() {
		return id;
	}

	/**
	 * Adds a {@link WorldObject} to the {@link worldObjects} map based on the x, y, height, and identification of the object.
	 *
	 * @param id the id of the object
	 * @param x the x position of the object
	 * @param y the y position of the object
	 * @param height the height of the object
	 */
	public static void addWorldObject(int id, int x, int y, int height, int type, int face) {
		//log.info("Spawning {} at {}, {}, {}", id, x, y, height);
		Region region = getRegion(x, y);
		if (region == null) {
			return;
		}
		int regionId = region.id;
		if (worldObjects.containsKey(regionId)) {
			if (objectExists(regionId, id, x, y, height, type)) {
				return;
			}
			worldObjects.get(regionId).add(new WorldObject(id, x, y, height, type, face));
		} else {
			ArrayList<WorldObject> object = new ArrayList<>(1);
			object.add(new WorldObject(id, x, y, height, type, face));
			worldObjects.put(regionId, object);
		}
	}

	public static int getObjectIdAt(int x, int y, int height) {
		Region region = getRegion(x, y);
		if (region == null) return -1;

		int regionId = region.id;
		List<WorldObject> objects = worldObjects.get(regionId);
		if (objects == null) return -1;

		for (WorldObject obj : objects) {
			if (obj == null) continue;
			if (obj.getX() == x && obj.getY() == y && (obj.getHeight() == (height & 3) || obj.getHeight() == height)) {
				return obj.getId();
			}
		}
		return -1;
	}
	/*public static int getObjectFaceAt(int x, int y, int height) {
	    Region region = getRegion(x, y);
	    if (region == null) return 0;

	    int regionId = region.id;
	    List<GlobalObject> objects = worldObjects.get(regionId);
	    if (objects == null) return -1;

	    for (GlobalObject obj : objects) {
	        if (obj == null) continue;
	        if (obj.getFace() == 0) {
	            return obj.getFace();
	        }
	    }
	    return -1;
	}*/
	public static void addGlobalObject(GlobalObject globalObject) {
		Region region = getRegion(globalObject.getX(), globalObject.getY());
		if (region == null) return;

		int regionId = region.id;
		// Ensure we are working with a clean list
		worldObjects.putIfAbsent(regionId, new ArrayList<>());
		ArrayList<WorldObject> regionObjects = worldObjects.get(regionId);

		// Remove any existing object at this location of the same type before adding
		regionObjects.removeIf(o -> o.x == globalObject.getX()
				&& o.y == globalObject.getY()
				&& o.height == globalObject.getHeight()
				&& o.type == globalObject.getType());

		if (globalObject.getObjectId() != -1) {
			regionObjects.add(new WorldObject(globalObject.getObjectId(), globalObject.getX(), globalObject.getY(),
					globalObject.getHeight(), globalObject.getType(), globalObject.getFace()));
		}
	}

	public static void removeGlobalObject(GlobalObject globalObject) {
		Region region = getRegion(globalObject.getX(), globalObject.getY());
		if (region == null) {
			return;
		}
		int regionId = region.id;
		WorldObject toWorldObject = new WorldObject(globalObject.getObjectId(), globalObject.getX(), globalObject.getY(), globalObject.getHeight(), globalObject.getType(), globalObject.getFace());
		if (worldObjects.containsKey(regionId)) {
			worldObjects.get(regionId).remove(toWorldObject);
			Region.remove(globalObject.getObjectId(), globalObject.getX(), globalObject.getY(), globalObject.getHeight(), globalObject.getType(), globalObject.getFace());
		}
	}

	/**
	 * A convenience method for lamda expressions
	 *
	 * @param object the world object being added
	 */
	public static void addWorldObject(WorldObject object) {

		addWorldObject(object.getId(), object.getX(), object.getY(), object.getHeight(), object.getType(), object.getFace());
	}

	/**
	 * Determines if an object exists in a region
	 *
	 * @param region the region
	 * @param id the object id
	 * @param x` the object x pos
	 * @param y the object y pos
	 * @param height the object z pos
	 * @return true if the object exists in the region, otherwise false
	 */

	private static boolean objectExists(int region, int id, int x, int y, int height, int type) {
		List<WorldObject> objects = worldObjects.get(region);
		for (WorldObject object : objects) {
			if (object == null) {
				continue;
			}
			if (object.getId() == id && object.getType() == type && object.getX() == x && object.getY() == y && (object.getHeight() == (height & 3) || object.getHeight() == height)) {
				return true;
			}
		}
		return false;
	}
	private void addClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;

		// Ensure the 2D array exists for this specific height (4, 8, 12, etc.)
		clips.putIfAbsent(height, new int[64][64]);

		clips.get(height)[x - regionAbsX][y - regionAbsY] |= shift;
	}

	private void addProjectileClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;

		shootable.putIfAbsent(height, new int[64][64]);

		shootable.get(height)[x - regionAbsX][y - regionAbsY] |= shift;
	}

	private void removeProjectileClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (shootable.get(height) == null) {
			shootable.put(height, new int[64][64]);
		}
		shootable.get(height)[x - regionAbsX][y - regionAbsY] += shift;
	}

	private void removeClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (clips.get(height) == null) {
			clips.put(height, new int[64][64]);
		}
		clips.get(height)[x - regionAbsX][y - regionAbsY] += shift;
	}



	private int getClip(int x, int y, int height) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;

		// If the specific height map exists (e.g. 4, 8, 12), use it.
		if (clips.containsKey(height)) {
			return clips.get(height)[x - regionAbsX][y - regionAbsY];
		}

		// Failsafe: if height is a multiple of 4 (our dimensions),
		// but no clip map exists, it's effectively empty space.
		// We only fall back to 'height & 3' for standard map planes (0-3).
		int plane = height;
		if (clips.containsKey(plane)) {
			return clips.get(plane)[x - regionAbsX][y - regionAbsY];
		}

		return 0; // High dimension with no clipping data = empty space
	}



	public static int[] getNextStep(int baseX, int baseY, int toX, int toY, int height, int xLength, int yLength) {
		int moveX = 0;
		int moveY = 0;
		if (baseX - toX > 0) {
			moveX--;
		} else if (baseX - toX < 0) {
			moveX++;
		}
		if (baseY - toY > 0) {
			moveY--;
		} else if (baseY - toY < 0) {
			moveY++;
		}
		if (canMove(baseX, baseY, baseX + moveX, baseY + moveY, height, xLength, yLength)) {
			return new int[] { baseX + moveX, baseY + moveY };
		} else if (moveX != 0 && canMove(baseX, baseY, baseX + moveX, baseY, height, xLength, yLength)) {
			return new int[] { baseX + moveX, baseY };
		} else if (moveY != 0 && canMove(baseX, baseY, baseX, baseY + moveY, height, xLength, yLength)) {
			return new int[] { baseX, baseY + moveY };
		}
		return new int[] { baseX, baseY };
	}

	public static boolean lineOfSight(Entity entity, Entity other) {
		int dir = NPCClipping.getDirection(entity.getX(), entity.getY(), other.getX(), other.getY());
		if (canMove(entity.getX(), entity.getY(), entity.getHeight(), dir)) {
			return true;
		}
		return Region.canShoot(entity.getX(), entity.getY(), entity.getHeight(), dir);
	}

	public static boolean pathBlocked(Player attacker, NPC victim) {

		double offsetX = Math.abs(attacker.getX() - victim.getX());
		double offsetY = Math.abs(attacker.getY() - victim.getY());

		int distance = Misc.distanceToPoint(attacker.getX(), attacker.getY(), victim.getX(), victim.getY());

		if (distance == 0) {
			return true;
		}

		offsetX = offsetX > 0 ? offsetX / distance : 0;
		offsetY = offsetY > 0 ? offsetY / distance : 0;

		int[][] path = new int[distance][5];

		int curX = attacker.getX();
		int curY = attacker.getY();
		int next = 0;
		int nextMoveX = 0;
		int nextMoveY = 0;

		double currentTileXCount = 0.0;
		double currentTileYCount = 0.0;

		while (distance > 0) {
			distance--;
			nextMoveX = 0;
			nextMoveY = 0;
			if (curX > victim.getX()) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX--;
					curX--;
					currentTileXCount -= offsetX;
				}
			} else if (curX < victim.getX()) {
				currentTileXCount += offsetX;
				if (currentTileXCount >= 1.0) {
					nextMoveX++;
					curX++;
					currentTileXCount -= offsetX;
				}
			}
			if (curY > victim.getY()) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY--;
					curY--;
					currentTileYCount -= offsetY;
				}
			} else if (curY < victim.getY()) {
				currentTileYCount += offsetY;
				if (currentTileYCount >= 1.0) {
					nextMoveY++;
					curY++;
					currentTileYCount -= offsetY;
				}
			}
			path[next][0] = curX;
			path[next][1] = curY;
			path[next][2] = attacker.getHeight();
			path[next][3] = nextMoveX;
			path[next][4] = nextMoveY;
			next++;
		}
		for (int i = 0; i < path.length; i++) {
			if (!getClipping(path[i][0], path[i][1], path[i][2], path[i][3], path[i][4])) {
				return true;
			}
		}
		return false;
	}

	public static boolean canMove(Location location, int direction) {
		return canMove(location.getX(), location.getY(), location.getZ(), direction);
	}

	public static boolean canProjectileMove(int startX, int startY, int endX, int endY, int height, int xLength,
	                                        int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		// height %= 4;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++) {
					if (diffX < 0 && diffY < 0) {
						if ((getClipping(currentX + i - 1, currentY + i2 - 1, height) & (UNLOADED_TILE
								| /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_EAST_BLOCKED
								| PROJECTILE_NORTH_EAST_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0
								|| (getClipping(currentX + i - 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_EAST_BLOCKED)) != 0
								|| (getClipping(currentX + i, currentY + i2 - 1, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY > 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 + 1, height) & (UNLOADED_TILE
								| /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED
								| PROJECTILE_SOUTH_WEST_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0
								|| (getClipping(currentX + i + 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_WEST_BLOCKED)) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY > 0) {
						if ((getClipping(currentX + i - 1, currentY + i2 + 1, height) & (UNLOADED_TILE
								| /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED
								| PROJECTILE_SOUTH_EAST_BLOCKED | PROJECTILE_EAST_BLOCKED)) != 0
								|| (getClipping(currentX + i - 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_EAST_BLOCKED)) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY < 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 - 1, height) & (UNLOADED_TILE
								| /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_WEST_BLOCKED
								| PROJECTILE_NORTH_BLOCKED | PROJECTILE_NORTH_WEST_BLOCKED)) != 0
								|| (getClipping(currentX + i + 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_WEST_BLOCKED)) != 0
								|| (getClipping(currentX + i, currentY + i2 - 1, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY == 0) {
						if ((getClipping(currentX + i + 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_WEST_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY == 0) {
						if ((getClipping(currentX + i - 1, currentY + i2, height)
								& (UNLOADED_TILE | /* BLOCKED_TILE | */UNKNOWN | PROJECTILE_TILE_BLOCKED
								| PROJECTILE_EAST_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY > 0) {
						if ((getClipping(currentX + i, currentY + i2 + 1, height) & (UNLOADED_TILE
								| /*
						 * BLOCKED_TILE |
						 */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_SOUTH_BLOCKED)) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY < 0) {
						if ((getClipping(currentX + i, currentY + i2 - 1, height) & (UNLOADED_TILE
								| /*
						 * BLOCKED_TILE |
						 */UNKNOWN | PROJECTILE_TILE_BLOCKED | PROJECTILE_NORTH_BLOCKED)) != 0) {
							return false;
						}
					}
				}
			}
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) {
				diffX--;
			}
			if (diffY < 0) {
				diffY++; // change
			} else if (diffY > 0) {
				diffY--;
			}
		}
		return true;
	}

	public static boolean canMove(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !blockedNorthWest(x, y, z) && !blockedNorth(x, y, z)
					&& !blockedWest(x, y, z);
		} else if (direction == 1) {
			return !blockedNorth(x, y, z);
		} else if (direction == 2) {
			return !blockedNorthEast(x, y, z) && !blockedNorth(x, y, z)
					&& !blockedEast(x, y, z);
		} else if (direction == 3) {
			return !blockedWest(x, y, z);
		} else if (direction == 4) {
			return !blockedEast(x, y, z);
		} else if (direction == 5) {
			return !blockedSouthWest(x, y, z) && !blockedSouth(x, y, z)
					&& !blockedWest(x, y, z);
		} else if (direction == 6) {
			return !blockedSouth(x, y, z);
		} else if (direction == 7) {
			return !blockedSouthEast(x, y, z) && !blockedSouth(x, y, z)
					&& !blockedEast(x, y, z);
		}
		return false;
	}

	public static boolean canShoot(int x, int y, int z, int direction) {
		if (direction == 0) {
			return !projectileBlockedNorthWest(x, y, z) && !projectileBlockedNorth(x, y, z)
					&& !projectileBlockedWest(x, y, z);
		} else if (direction == 1) {
			return !projectileBlockedNorth(x, y, z);
		} else if (direction == 2) {
			return !projectileBlockedNorthEast(x, y, z) && !projectileBlockedNorth(x, y, z)
					&& !projectileBlockedEast(x, y, z);
		} else if (direction == 3) {
			return !projectileBlockedWest(x, y, z);
		} else if (direction == 4) {
			return !projectileBlockedEast(x, y, z);
		} else if (direction == 5) {
			return !projectileBlockedSouthWest(x, y, z) && !projectileBlockedSouth(x, y, z)
					&& !projectileBlockedWest(x, y, z);
		} else if (direction == 6) {
			return !projectileBlockedSouth(x, y, z);
		} else if (direction == 7) {
			return !projectileBlockedSouthEast(x, y, z) && !projectileBlockedSouth(x, y, z)
					&& !projectileBlockedEast(x, y, z);
		}
		return false;
	}

	public static boolean projectileBlockedNorth(int x, int y, int z) {
		return (getProjectileClipping(x, y + 1, z) & 0x1280120) != 0;
	}

	public static boolean projectileBlockedEast(int x, int y, int z) {
		return (getProjectileClipping(x + 1, y, z) & 0x1280180) != 0;
	}

	public static boolean projectileBlockedSouth(int x, int y, int z) {
		return (getProjectileClipping(x, y - 1, z) & 0x1280102) != 0;
	}

	public static boolean projectileBlockedWest(int x, int y, int z) {
		return (getProjectileClipping(x - 1, y, z) & 0x1280108) != 0;
	}

	public static boolean projectileBlockedNorthEast(int x, int y, int z) {
		return (getProjectileClipping(x + 1, y + 1, z) & 0x12801e0) != 0;
	}

	public static boolean projectileBlockedNorthWest(int x, int y, int z) {
		return (getProjectileClipping(x - 1, y + 1, z) & 0x1280138) != 0;
	}

	public static boolean projectileBlockedSouthEast(int x, int y, int z) {
		return (getProjectileClipping(x + 1, y - 1, z) & 0x1280183) != 0;
	}

	public static boolean projectileBlockedSouthWest(int x, int y, int z) {
		return (getProjectileClipping(x - 1, y - 1, z) & 0x128010e) != 0;
	}

	public static boolean canMove(int startX, int startY, int endX, int endY, int height, int xLength, int yLength) {
		int diffX = endX - startX;
		int diffY = endY - startY;
		int max = Math.max(Math.abs(diffX), Math.abs(diffY));
		for (int ii = 0; ii < max; ii++) {
			int currentX = endX - diffX;
			int currentY = endY - diffY;
			for (int i = 0; i < xLength; i++) {
				for (int i2 = 0; i2 < yLength; i2++) {
					if (diffX < 0 && diffY < 0) {
						if ((getClipping(currentX + i - 1, currentY + i2 - 1, height) & 0x128010e) != 0 || (getClipping(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0
								|| (getClipping(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY > 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 + 1, height) & 0x12801e0) != 0 || (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY > 0) {
						if ((getClipping(currentX + i - 1, currentY + i2 + 1, height) & 0x1280138) != 0 || (getClipping(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0
								|| (getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY < 0) {
						if ((getClipping(currentX + i + 1, currentY + i2 - 1, height) & 0x1280183) != 0 || (getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0
								|| (getClipping(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
							return false;
						}
					} else if (diffX > 0 && diffY == 0) {
						if ((getClipping(currentX + i + 1, currentY + i2, height) & 0x1280180) != 0) {
							return false;
						}
					} else if (diffX < 0 && diffY == 0) {
						if ((getClipping(currentX + i - 1, currentY + i2, height) & 0x1280108) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY > 0) {
						if ((getClipping(currentX + i, currentY + i2 + 1, height) & 0x1280120) != 0) {
							return false;
						}
					} else if (diffX == 0 && diffY < 0) {
						if ((getClipping(currentX + i, currentY + i2 - 1, height) & 0x1280102) != 0) {
							return false;
						}
					}
				}
			}
			if (diffX < 0) {
				diffX++;
			} else if (diffX > 0) {
				diffX--;
			}
			if (diffY < 0) {
				diffY++;
			} else if (diffY > 0) {
				diffY--;
			}
		}
		return true;
	}
	public static boolean isWalkableForNpc(int x, int y, int height) {
		int objectId = getObjectIdAt(x, y, height);
		return objectId == 26679 || objectId == 26680;
	}
	public static boolean canMove(int x, int y, int z, int direction, int size) {
		if (size == 1) {
			return canMove(x, y, z, direction);
		}
		if (direction == 0) { // NW
			return !blockedNorthWestNPC(x, y, z, size) && !blockedNorthNPC(x, y, z, size) && !blockedWestNPC(x, y, z, size);
		} else if (direction == 1) { // N
			return !blockedNorthNPC(x, y, z, size);
		} else if (direction == 2) { // NE
			return !blockedNorthEastNPC(x, y, z, size) && !blockedNorthNPC(x, y, z, size) && !blockedEastNPC(x, y, z, size);
		} else if (direction == 3) { // W
			return !blockedWestNPC(x, y, z, size);
		} else if (direction == 4) { // E
			return !blockedEastNPC(x, y, z, size);
		} else if (direction == 5) { // SW
			return !blockedSouthWestNPC(x, y, z, size) && !blockedSouthNPC(x, y, z, size) && !blockedWestNPC(x, y, z, size);
		} else if (direction == 6) { // S
			return !blockedSouthNPC(x, y, z, size);
		} else if (direction == 7) { // SE
			return !blockedSouthEastNPC(x, y, z, size) && !blockedSouthNPC(x, y, z, size) && !blockedEastNPC(x, y, z, size);
		}
		return false;
	}
	public static boolean canMove(Location start, Location end, int xLength, int yLength) {
		return canMove(start.getX(), start.getY(), end.getX(), end.getY(), start.getZ(), xLength, yLength);
	}

	public static boolean blockedNorth(int x, int y, int z) {
		return (getClipping(x, y + 1, z) & 0x1280120) != 0;
	}
	public static boolean blockedEast(int x, int y, int z) {
		return (getClipping(x + 1, y, z) & 0x1280180) != 0;
	}
	public static boolean blockedSouth(int x, int y, int z) {
		return (getClipping(x, y - 1, z) & 0x1280102) != 0;
	}
	public static boolean blockedWest(int x, int y, int z) {
		return (getClipping(x - 1, y, z) & 0x1280108) != 0;
	}
	public static boolean blockedNorthEast(int x, int y, int z) {
		return (getClipping(x + 1, y + 1, z) & 0x12801e0) != 0;
	}
	public static boolean blockedNorthWest(int x, int y, int z) {
		return (getClipping(x - 1, y + 1, z) & 0x1280138) != 0;
	}
	public static boolean blockedSouthEast(int x, int y, int z) {
		return (getClipping(x + 1, y - 1, z) & 0x1280183) != 0;
	}
	public static boolean blockedSouthWest(int x, int y, int z) {
		return (getClipping(x - 1, y - 1, z) & 0x128010e) != 0;
	}

	private static void addProjectileClipping(int x, int y, int height, int shift) {
		Region region = getRegion(x, y);
		if (region != null) {
			if (shift > 0) {
				region.addProjectileClip(x, y, height, shift);
			} else {
				region.removeProjectileClip(x, y, height, shift);
			}
		}
	}

	private static void addClipping(int x, int y, int height, int shift) {
		Region region = getRegion(x, y);
		if(region != null) {
			if(shift > 0) {
				region.addClip(x, y, height, shift);
			} else {
				region.removeClip(x, y, height, shift);
			}
		}
	}


	private static void setClipping(int x, int y, int height, int shift) {
		Region region = getRegion(x, y);
		if(region != null) {
			region.setClip(x, y, height, shift);
		}
	}

	private static void setProjectileClipping(int x, int y, int height, int shift) {
		Region region = getRegion(x, y);
		if(region != null) {
			region.setProjectileClip(x, y, height, shift);
		}
	}


	private void setProjectileClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (this.shootable.get(height) == null) {
			shootable.put(height, new int[64][64]);
		}
		shootable.get(height)[x - regionAbsX][y - regionAbsY] = shift;
	}

	private void setClip(int x, int y, int height, int shift) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;
		if (clips.get(height) == null) {
			clips.put(height, new int[64][64]);
		}
		clips.get(height)[x - regionAbsX][y - regionAbsY] = shift;
	}

	public static Region getRegion(int x, int y) {
		int regionX = x >> 3;
		int regionY = y >> 3;
		int regionId = (regionX / 8 << 8) + regionY / 8;
		Region region = regions.get(regionId);
		if(region == null) {
			region = new Region(regionId, false);
		}
		return region;
	}

	public Region(int id, boolean members) {
		this.id = id;
		this.members = members;
		regions.put(id, this);
	}

	public int id() {
		return id;
	}

	public boolean members() {
		return members;
	}

	public static boolean isMembers(int x, int y) {
		if (x >= 3272 && x <= 3320 && y >= 2752 && y <= 2809)
			return false;
		if (x >= 2640 && x <= 2677 && y >= 2638 && y <= 2679)
			return false;

		return getRegion(x, y).members;
	}

	private static void setProjectileClippingForVariableObject(int x, int y, int height,
	                                                           int type, int direction, boolean flag, boolean negative) {
		if (type == 0) {
			if (direction == 0) {
				addProjectileClipping(x, y, height, negative ? -128 : 128);
				addProjectileClipping(x - 1, y, height, negative ? -8 : 8);
			} else if (direction == 1) {
				addProjectileClipping(x, y, height, negative ? -2 : 2);
				addProjectileClipping(x, y + 1, height, negative ? -32 : 32);
			} else if (direction == 2) {
				addProjectileClipping(x, y, height, negative ? -8 : 8);
				addProjectileClipping(x + 1, y, height, negative ? -128 : 128);
			} else if (direction == 3) {
				addProjectileClipping(x, y, height, negative ? -32 : 32);
				addProjectileClipping(x, y - 1, height, negative ? -2 : 2);
			}
		} else if (type == 1 || type == 3) {
			if (direction == 0) {
				addProjectileClipping(x, y, height, negative ? -1 : 1);
				addProjectileClipping(x - 1, y + 1, height, negative ? -16 : 16);//wrong method217(16, x - 1, y + 1);
			} else if (direction == 1) {
				addProjectileClipping(x, y, height, negative ? -4 : 4);
				addProjectileClipping(x + 1, y + 1, height, negative ? -64 : 64);
			} else if (direction == 2) {
				addProjectileClipping(x, y, height, negative ? -16 : 16);
				addProjectileClipping(x + 1, y - 1, height, negative ? -1 : 1);
			} else if (direction == 3) {
				addProjectileClipping(x, y, height, negative ? -64 : 64);
				addProjectileClipping(x - 1, y - 1, height, negative ? -4 : 4);
			}
		} else if (type == 2) {
			if (direction == 0) {
				addProjectileClipping(x, y, height, 130);
				addProjectileClipping(x - 1, y, height, negative ? -8 : 8);
				addProjectileClipping(x, y + 1, height, negative ? -32 : 32);
			} else if (direction == 1) {
				addProjectileClipping(x, y, height, negative ? -10 : 10);
				addProjectileClipping(x, y + 1, height, negative ? -32 : 32);
				addProjectileClipping(x + 1, y, height, negative ? -128 : 128);
			} else if (direction == 2) {
				addProjectileClipping(x, y, height, negative ? -40 : 40);
				addProjectileClipping(x + 1, y, height, negative ? -128 : 128);
				addProjectileClipping(x, y - 1, height, negative ? -2 : 2);
			} else if (direction == 3) {
				addProjectileClipping(x, y, height, negative ? -160 : 160);
				addProjectileClipping(x, y - 1, height, negative ? -2 : 2);
				addProjectileClipping(x - 1, y, height, negative ? -8 : 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addProjectileClipping(x, y, height, negative ? -0x10000 : 0x10000);
					addProjectileClipping(x - 1, y, height, negative ? -4096 : 4096);
				} else if (direction == 1) {
					addProjectileClipping(x, y, height, negative ? -1024 : 1024);
					addProjectileClipping(x, y + 1, height, negative ? -16384 : 16384);
				} else if (direction == 2) {
					addProjectileClipping(x, y, height, negative ? -4096 : 4096);
					addProjectileClipping(x + 1, y, height, negative ? -0x10000 : 0x10000);
				} else if (direction == 3) {
					addProjectileClipping(x, y, height, negative ? -16384 : 16384);
					addProjectileClipping(x, y - 1, height, negative ? -1024 : 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addProjectileClipping(x, y, height, negative ? -512 : 512);
					addProjectileClipping(x - 1, y + 1, height, negative ? -8192 : 8192);
				} else if (direction == 1) {
					addProjectileClipping(x, y, height, negative ? -2048 : 2048);
					addProjectileClipping(x + 1, y + 1, height, negative ? -32768 : 32768);
				} else if (direction == 2) {
					addProjectileClipping(x, y, height, negative ? -8192 : 8192);
					addProjectileClipping(x + 1, y + 1, height, negative ? -512 : 512);
				} else if (direction == 3) {
					addProjectileClipping(x, y, height, negative ? -32768 : 32768);
					addProjectileClipping(x - 1, y - 1, height, negative ? -2048 : 2048);
				}
			} else if (type == 2) {
				if (direction == 0) {
					addProjectileClipping(x, y, height, negative ? -0x10400 : 0x10400);
					addProjectileClipping(x - 1, y, height, negative ? -4096 : 4096);
					addProjectileClipping(x, y + 1, height, negative ? -16384 : 16384);
				} else if (direction == 1) {
					addProjectileClipping(x, y, height, negative ? -5120 : 5120);
					addProjectileClipping(x, y + 1, height, negative ? -16384 : 16384);
					addProjectileClipping(x + 1, y, height, negative ? -0x10000 : 0x10000);
				} else if (direction == 2) {
					addProjectileClipping(x, y, height, negative ? -20480 : 20480);
					addProjectileClipping(x + 1, y, height, negative ? -0x10000 : 0x10000);
					addProjectileClipping(x, y - 1, height, negative ? -1024 : 1024);
				} else if (direction == 3) {
					addProjectileClipping(x, y, height, negative ? -81920 : 81920);
					addProjectileClipping(x, y - 1, height, negative ? -1024 : 1024);
					addProjectileClipping(x - 1, y, height, negative ? -4096 : 4096);
				}
			}
		}
	}

	private static void addClippingForVariableObject(int x, int y, int height, int type, int direction, boolean flag) {
		if (type == 0) {
			if (direction == 0) {
				addClipping(x, y, height, 128);
				addClipping(x - 1, y, height, 8);
			} else if (direction == 1) {
				addClipping(x, y, height, 2);
				addClipping(x, y + 1, height, 32);
			} else if (direction == 2) {
				addClipping(x, y, height, 8);
				addClipping(x + 1, y, height, 128);
			} else if (direction == 3) {
				addClipping(x, y, height, 32);
				addClipping(x, y - 1, height, 2);
			}
		} else if (type == 1 || type == 3) {
			if (direction == 0) {
				addClipping(x, y, height, 1);
				addClipping(x - 1, y, height, 16);
			} else if (direction == 1) {
				addClipping(x, y, height, 4);
				addClipping(x + 1, y + 1, height, 64);
			} else if (direction == 2) {
				addClipping(x, y, height, 16);
				addClipping(x + 1, y - 1, height, 1);
			} else if (direction == 3) {
				addClipping(x, y, height, 64);
				addClipping(x - 1, y - 1, height, 4);
			}
		} else if (type == 2) {
			if (direction == 0) {
				addClipping(x, y, height, 130);
				addClipping(x - 1, y, height, 8);
				addClipping(x, y + 1, height, 32);
			} else if (direction == 1) {
				addClipping(x, y, height, 10);
				addClipping(x, y + 1, height, 32);
				addClipping(x + 1, y, height, 128);
			} else if (direction == 2) {
				addClipping(x, y, height, 40);
				addClipping(x + 1, y, height, 128);
				addClipping(x, y - 1, height, 2);
			} else if (direction == 3) {
				addClipping(x, y, height, 160);
				addClipping(x, y - 1, height, 2);
				addClipping(x - 1, y, height, 8);
			}
		}
		if (flag) {
			if (type == 0) {
				if (direction == 0) {
					addClipping(x, y, height, 65536);
					addClipping(x - 1, y, height, 4096);
				} else if (direction == 1) {
					addClipping(x, y, height, 1024);
					addClipping(x, y + 1, height, 16384);
				} else if (direction == 2) {
					addClipping(x, y, height, 4096);
					addClipping(x + 1, y, height, 65536);
				} else if (direction == 3) {
					addClipping(x, y, height, 16384);
					addClipping(x, y - 1, height, 1024);
				}
			}
			if (type == 1 || type == 3) {
				if (direction == 0) {
					addClipping(x, y, height, 512);
					addClipping(x - 1, y + 1, height, 8192);
				} else if (direction == 1) {
					addClipping(x, y, height, 2048);
					addClipping(x + 1, y + 1, height, 32768);
				} else if (direction == 2) {
					addClipping(x, y, height, 8192);
					addClipping(x + 1, y + 1, height, 512);
				} else if (direction == 3) {
					addClipping(x, y, height, 32768);
					addClipping(x - 1, y - 1, height, 2048);
				}
			} else if (type == 2) {
				if (direction == 0) {
					addClipping(x, y, height, 66560);
					addClipping(x - 1, y, height, 4096);
					addClipping(x, y + 1, height, 16384);
				} else if (direction == 1) {
					addClipping(x, y, height, 5120);
					addClipping(x, y + 1, height, 16384);
					addClipping(x + 1, y, height, 65536);
				} else if (direction == 2) {
					addClipping(x, y, height, 20480);
					addClipping(x + 1, y, height, 65536);
					addClipping(x, y - 1, height, 1024);
				} else if (direction == 3) {
					addClipping(x, y, height, 81920);
					addClipping(x, y - 1, height, 1024);
					addClipping(x - 1, y, height, 4096);
				}
			}
		}
	}

	public static boolean isPathClear(final int x, final int y, int z, final int x2, final int y2) {
		double x3 = x;
		double y3 = y;
		double xs = x2 - x;
		double ys = y2 - y;

		while (xs >= 1 || ys >= 1 || xs <= -1 || ys <= -1) {
			xs = xs / 2;
			ys = ys / 2;
		}

		int prevX = x;
		int prevY = y;

		while (true) {
			x3 += xs;
			y3 += ys;

			if (!(prevX == (int) x3 && prevY == (int) y3)) {

				if (!canShootOver((int) x3, (int) y3, z, prevX, prevY)) {
					return false;
				}

				prevX = (int) x3;
				prevY = (int) y3;
			}

			if (x3 >= x2 && y3 >= y2) {
				return true;
			}
		}
	}

	private static boolean canShootOver(int x, int y, int z, int prevX, int prevY) {
		int dir = -1;
		int dir2 = -1;

		for (int i = 0; i < DIR.length; i++) {
			if (x + DIR[i][0] == prevX && y + DIR[i][1] == prevY) {
				dir = i;
			}

			if (prevX + DIR[i][0] == x && prevY + DIR[i][1] == y) {
				dir2 = i;
			}
		}

		if (dir == -1 || dir2 == -1) {
			return false;
		}
		Region region2 = Region.getRegion(prevX, prevY);
		if (canMove(x, y, prevX, prevY, z, x - prevX, y - prevY)) {
			return true;
		}
		return (region2.getClip(x, y, z) & 0x20000) == 0;
	}

	private static final int[][] DIR = { { -1, 1 }, { 0, 1 }, { 1, 1 }, { -1, 0 }, { 1, 0 }, { -1, -1 }, { 0, -1 }, { 1, -1 } };

	private static void addProjectileClippingForSolidObject(int x, int y, int height,
	                                                        int xLength, int yLength, boolean flag) {
		int clipping = 256;
		if (flag) {
			clipping += 0x20000;
		}
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				addProjectileClipping(i, i2, height, clipping);
			}
		}
	}

	private static void addClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag) {
		int clipping = 256;
		if (flag) {
			clipping += 0x20000;
		}
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				addClipping(i, i2, height, clipping);
			}
		}
	}

	private static void removeProjectileClippingForSolidObject(int x, int y, int height,
	                                                           int xLength, int yLength, boolean flag) {
		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				setProjectileClipping(i, i2, height, 0);
			}
		}
	}

	private static void removeClippingForSolidObject(int x, int y, int height, int xLength, int yLength, boolean flag) {

		for (int i = x; i < x + xLength; i++) {
			for (int i2 = y; i2 < y + yLength; i2++) {
				setClipping(i, i2, height, 0);
			}
		}
	}

	public static void remove(int objectId, int x, int y, int height, int type, int direction) {
		ObjectDef def = ObjectDef.forID(objectId);
		if (def == null) {
			return;
		}
		int xLength;
		int yLength;
		if (direction != 1 && direction != 3) {
			xLength = def.width;
			yLength = def.length;
		} else {
			xLength = def.length;
			yLength = def.width;
		}
		if (type == 22) {
			if (def.hasActions && def.solid) {
				addClipping(x, y, height, -0x200000);
				if (def.impenetrable) {
					addProjectileClipping(x, y, height, -0x200000);
				}
			}
		} else if (type >= 9) {
			if (def.solid) {
				removeClippingForSolidObject(x, y, height, xLength, yLength, def.solid);
				if (def.impenetrable) {
					removeProjectileClippingForSolidObject(x, y, height, xLength, yLength, true);
				}
			}
		} else if (type >= 0 && type <= 3) {
			if (def.solid) {
				addClippingForVariableObject(x, y, height, type, direction, def.solid);
				if (def.impenetrable) {
					setProjectileClippingForVariableObject(x, y, height, type, direction, def.solid, false);
				}
			}
		}
	}

	public static void addObject(int objectId, int x, int y, int height, int type, int direction) {

		ObjectDef def = ObjectDef.forID(objectId);
		if (def == null) {
			return;
		}
		int xLength;
		int yLength;
		if (direction != 1 && direction != 3) {
			xLength = def.width;
			yLength = def.length;
		} else {
			xLength = def.length;
			yLength = def.width;
		}
		if (type == 22) {
			if (def.hasActions && def.solid) {
				addClipping(x, y, height, 0x200000);
				if (def.impenetrable) {
					addProjectileClipping(x, y, height, 0x200000);
				}
			}
		} else if (type >= 9) {
			if (def.solid) {
				addClippingForSolidObject(x, y, height, xLength, yLength, def.solid);
				if (def.impenetrable) {
					addProjectileClippingForSolidObject(x, y, height, xLength, yLength, true);
				}
			}
		} else if (type >= 0 && type <= 3) {
			if (def.solid) {
				addClippingForVariableObject(x, y, height, type, direction, def.solid);
				if (def.impenetrable) {
					setProjectileClippingForVariableObject(x, y, height, type, direction, def.solid, false);
				}
			}
		}
	}

	public static int getProjectileClipping(int x, int y, int height) {
		Region region = getRegion(x, y);
		if (region == null) {
			return 0;
		}
		return region.getProjectileClip(x, y, height);
	}

	private int getProjectileClip(int x, int y, int height) {
		int regionAbsX = (id >> 8) * 64;
		int regionAbsY = (id & 0xff) * 64;

		if (shootable.containsKey(height)) {
			return shootable.get(height)[x - regionAbsX][y - regionAbsY];
		}

		if (height < 4) {
			if (shootable.get(height) == null) return 0;
			return shootable.get(height)[x - regionAbsX][y - regionAbsY];
		}

		return 0;
	}

	public static int getClipping(int x, int y, int height) {
		Region region = getRegion(x, y);
		if (region == null) {
			// System.out.println("MISSING REGION AT: " + x + ", " + y);
			return 0;
		}
		return region.getClip(x, y, height);
	}
	/**
	 * Call this when a minigame instance finishes to free up memory.
	 */
	public static void removeRegion(int x, int y) {
		int regionId = (x >> 3 << 8) + (y >> 3);
		Region r = regions.remove(regionId);
		if (r != null) {
			r.destroy(); // Clears internal arrays (clips/shootable)
		}
		worldObjects.remove(regionId); // Clears the list of objects for this region
	}

	public static boolean getClipping(int x, int y, int height, int moveTypeX, int moveTypeY) {
		try {
			int checkX = (x + moveTypeX);
			int checkY = (y + moveTypeY);
			if (moveTypeX == -1 && moveTypeY == 0)
				return (getClipping(x, y, height) & 0x1280108) == 0;
			else if (moveTypeX == 1 && moveTypeY == 0)
				return (getClipping(x, y, height) & 0x1280180) == 0;
			else if (moveTypeX == 0 && moveTypeY == -1)
				return (getClipping(x, y, height) & 0x1280102) == 0;
			else if (moveTypeX == 0 && moveTypeY == 1)
				return (getClipping(x, y, height) & 0x1280120) == 0;
			else if (moveTypeX == -1 && moveTypeY == -1)
				return ((getClipping(x, y, height) & 0x128010e) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
						&& (getClipping(checkX - 1, checkY, height) & 0x1280102) == 0);
			else if (moveTypeX == 1 && moveTypeY == -1)
				return ((getClipping(x, y, height) & 0x1280183) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
						&& (getClipping(checkX, checkY - 1, height) & 0x1280102) == 0);
			else if (moveTypeX == -1 && moveTypeY == 1)
				return ((getClipping(x, y, height) & 0x1280138) == 0 && (getClipping(checkX - 1, checkY, height) & 0x1280108) == 0
						&& (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
			else if (moveTypeX == 1 && moveTypeY == 1)
				return ((getClipping(x, y, height) & 0x12801e0) == 0 && (getClipping(checkX + 1, checkY, height) & 0x1280180) == 0
						&& (getClipping(checkX, checkY + 1, height) & 0x1280120) == 0);
			else {
				// System.out.println("[FATAL ERROR]: At getClipping: " + x + ", "
				// + y + ", " + height + ", " + moveTypeX + ", "
				// + moveTypeY);
				return false;
			}
		} catch (Exception e) {
			return true;
		}
	}

	public static void load() {
		try {

			ForkJoinPool.commonPool().submit(
					() -> MapIndexLoader.stream().forEach(Region::loadMap)
			).get();
			//Arrays.asList(EXISTANT_OBJECTS).forEach(Region::addWorldObject);
			int numberOfRegions = worldObjects.size();
			long totalObjects = worldObjects.values().stream().mapToInt(list -> list.size()).sum();
			//log.info("Loaded region configuration: {} regions and {} total objects.", numberOfRegions, totalObjects);
			System.out.println("Loaded region configuration: "+numberOfRegions+" regions and "+totalObjects+" total objects.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadMap(RegionData regionData) {
		try {
			byte[] file1 = getBuffer(new File("./Data/world/map/" + regionData.getObjects() + ".gz"));
			byte[] file2 = getBuffer(new File("./Data/world/map/" + regionData.getLandscape() + ".gz"));

			if (file1 == null || file2 == null) {
				return;
			}
			//System.out.println("Loaded region " + regionData.getRegionHash());

			loadMaps(regionData.getRegionHash(), new ByteStream(file1), new ByteStream(file2));
		} catch (Exception e) {
			//System.out.println("Error loading map region "+ regionData.getRegionHash()+" (map "+regionData.getObjects()+", landscape "+regionData.getLandscape()+") "+e+"");

		}

	}

	private static void loadMaps(int regionId, ByteStream str1, ByteStream str2) {
		int absX = (regionId >> 8) * 64;
		int absY = (regionId & 0xff) * 64;
		int[][][] clippingFlags = new int[4][64][64];
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					while (true) {
						int v = str2.getUByte();
						if (v == 0) {
							break;
						} else if (v == 1) {
							str2.skip(1);
							break;
						} else if (v <= 49) {
							str2.skip(1);
						} else if (v <= 81) {
							clippingFlags[i][i2][i3] = v - 49;
						}
					}
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			for (int i2 = 0; i2 < 64; i2++) {
				for (int i3 = 0; i3 < 64; i3++) {
					if ((clippingFlags[i][i2][i3] & 1) == 1) {
						int height = i;
						if ((clippingFlags[1][i2][i3] & 2) == 2) {
							height--;
						}
						if (height >= 0 && height <= 3) {
							addClipping(absX + i2, absY + i3, height, 0x200000);
						}
					}
				}
			}
		}
		int objectId = -1;
		int incr;
		while ((incr = str1.readUnsignedIntSmartShortCompat()) != 0) {
			objectId += incr;
			int location = 0;
			int incr2;
			while ((incr2 = str1.getUSmart()) != 0) {
				location += incr2 - 1;
				int localX = (location >> 6 & 0x3f);
				int localY = (location & 0x3f);
				int height = location >> 12;
				int objectData = str1.getUByte();
				int type = objectData >> 2;
				int direction = objectData & 0x3;
				if (localX < 0 || localX >= 64 || localY < 0 || localY >= 64) {
					continue;
				}
				if ((clippingFlags[1][localX][localY] & 2) == 2) {
					height--;
				}
				if (height >= 0 && height <= 3) {
					addObject(objectId, absX + localX, absY + localY, height, type, direction);
					addWorldObject(objectId, absX + localX, absY + localY, height, type, direction);
					//System.out.println("Spawned object " + objectId + " at X:" + absX + localX + ", Y:" + absY + localY + ", H:" + height +", face: "+direction);

				}
			}
		}
	}

	public static final void decodeConstructedMapsLandscape(byte[][][] tileFlags, ByteStream stream, int tileZ, int rotation, int baseX, int tileX, int tileY, int plane,
	                                                        int baseY) {


		for (int l2 = 0; l2 < 4; l2++) {
			for (int i3 = 0; i3 < 64; i3++) {
				for (int j3 = 0; j3 < 64; j3++)
					if (l2 == tileZ && i3 >= tileX && i3 < tileX + 8 && j3 >= tileY && j3 < tileY + 8 )
						decodeLandscape(tileFlags, baseY + MapTileUtils.getRotatedLandscapeChunkY(j3 & 7, rotation, i3 & 7), 0, stream,
								baseX + MapTileUtils.getRotatedLandscapeChunkX(rotation, j3 & 7, i3 & 7), plane, rotation, 0);
					else
						decodeLandscape(tileFlags, -1, 0, stream, -1, 0, 0, 0);

			}

		}

	}

	private static void decodeLandscape(byte[][][] tileFlags, int i, int j, ByteStream stream, int k, int l, int i1, int baseX) {
		try {
			if (k >= 0 && k < tileFlags[0].length && i >= 0 && i < tileFlags[0].length) {
				int absX = (baseX + k);
				int absY = (j + i);
				int absZ = (i1 + l);
				tileFlags[l][k][i] = 0;
				do {
					int l1 = stream.getUByte();
					if (l1 == 0)
						if (l == 0) {
							return;
						} else {

							return;
						}
					if (l1 == 1) {
						int j2 = stream.getUByte();
						if (j2 == 1)
							j2 = 0;
						if (l == 0) {
							return;
						} else {
							return;
						}
					}

					if (l1 <= 49) {

					} else if (l1 <= 81)
						tileFlags[l][k][i] = (byte) (l1 - 49);
				} while (true);
			}
			do {
				int i2 = stream.getUByte();
				if (i2 == 0)
					break;
				if (i2 == 1) {
					stream.getUByte();
					return;
				}
				if (i2 <= 49)
					stream.getUByte();
			} while (true);
		} catch (Exception e) {
		}
	}

	public static final void decodeConstructedMapObjects(Location baseLocation, byte[][][] tileFlags, int tileZ, int baseX, int tileY, int plane,
	                                                     ByteStream stream, int tileX, int rotation, int baseY) {
		int objectId = -1;
		int incr;
		while ((incr = stream.readUnsignedIntSmartShortCompat()) != 0) {
			objectId += incr;
			int location = 0;
			int incr2;
			while ((incr2 = stream.getUSmart()) != 0) {
				location += incr2 - 1;
				int localY = location & 0x3f;
				int localX = (location >> 6) & 0x3f;
				int height = location >> 12;
				int objectData = stream.getUByte();
				int type = objectData >> 2;
				int orientation = objectData & 3;

				// Check if this object belongs to the specific 8x8 chunk we are decoding
				if (height == tileZ && localX >= tileX && localX < tileX + 8 && localY >= tileY && localY < tileY + 8) {
					ObjectDef definition = ObjectDef.forID(objectId);
					if (definition == null) continue;

					int size1 = (orientation == 1 || orientation == 3) ? definition.length : definition.width;
					int size2 = (orientation == 1 || orientation == 3) ? definition.width : definition.length;

					// Apply rotation to the coordinates
					int locX = baseX + MapTileUtils.getRotatedMapChunkX(rotation, size2, localX & 7, localY & 7, size1);
					int locY = baseY + MapTileUtils.getRotatedMapChunkY(localY & 7, size2, rotation, size1, localX & 7);

					// FIX: Changed from > 0 to >= 0 to catch objects at the very edge of the palette
					if (locX >= 0 && locY >= 0 && locX < tileFlags[0].length && locY < tileFlags[0].length) {
						int l4 = plane;
						if ((tileFlags[1][locX][locY] & 2) == 2) l4--;

						int actualHeight = baseLocation.getZ() + l4;

						// Add the object to the server's collision map
						addObject(objectId, baseLocation.getX() + locX, baseLocation.getY() + locY, actualHeight, type, (orientation + rotation) & 3);
					}
				}
			}
		}
	}

	public static byte[] getBuffer(File f) throws Exception {
		if (!f.exists()) {
			return null;
		}
		byte[] buffer = new byte[(int) f.length()];
		try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
			dis.readFully(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] gzipInputBuffer = new byte[999999];
		int bufferlength = 0;
		try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer))) {
			do {
				if (bufferlength == gzipInputBuffer.length) {
					System.out.println("Error inflating data.\nGZIP buffer overflow.");
					break;
				}
				int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
				if (readByte == -1)
					break;
				bufferlength += readByte;
			} while (true);
			byte[] inflated = new byte[bufferlength];
			System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
			buffer = inflated;
			if (buffer.length < 10) {
				return null;
			}
		}
		return buffer;
	}

	public static boolean blockedNorthNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x + i, y + size, z) & 0x1280120) != 0) return true;
		}
		return false;
	}

	public static boolean blockedEastNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x + size, y + i, z) & 0x1280180) != 0) return true;
		}
		return false;
	}

	public static boolean blockedSouthNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x + i, y - 1, z) & 0x1280102) != 0) return true;
		}
		return false;
	}

	public static boolean blockedWestNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x - 1, y + i, z) & 0x1280108) != 0) return true;
		}
		return false;
	}

	public static boolean blockedNorthEastNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x + size, y + i, z) & 0x1280180) != 0) return true; // East edge
			if ((getClipping(x + i, y + size, z) & 0x1280120) != 0) return true; // North edge
		}
		return (getClipping(x + size, y + size, z) & 0x12801e0) != 0; // Exact NE corner
	}

	public static boolean blockedNorthWestNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x - 1, y + i, z) & 0x1280108) != 0) return true; // West edge
			if ((getClipping(x + i, y + size, z) & 0x1280120) != 0) return true; // North edge
		}
		return (getClipping(x - 1, y + size, z) & 0x1280138) != 0; // Exact NW corner
	}

	public static boolean blockedSouthEastNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x + size, y + i, z) & 0x1280180) != 0) return true; // East edge
			if ((getClipping(x + i, y - 1, z) & 0x1280102) != 0) return true; // South edge
		}
		return (getClipping(x + size, y - 1, z) & 0x1280183) != 0; // Exact SE corner
	}

	public static boolean blockedSouthWestNPC(int x, int y, int z, int size) {
		for (int i = 0; i < size; i++) {
			if ((getClipping(x - 1, y + i, z) & 0x1280108) != 0) return true; // West edge
			if ((getClipping(x + i, y - 1, z) & 0x1280102) != 0) return true; // South edge
		}
		return (getClipping(x - 1, y - 1, z) & 0x128010e) != 0; // Exact SW corner
	}

	public static boolean isBlocked(int x, int y, int z) {
		return getClipping(x, y, z) != 0;
	}

	public static List<WorldObject> getWorldObjectsAt(int x, int y, int z) {
		Region region = getRegion(x, y);
		if (region == null) {
			return Lists.newArrayList();
		}
		Collection<WorldObject> regionObjects = worldObjects.get(region.id);
		if (regionObjects == null) {
			return Lists.newArrayList();
		}
		return regionObjects.stream().filter(object -> object.x == x && object.y == y && object.height == z).collect(Collectors.toList());
	}


	public Region copyOf(int newId) {
		Region region = new Region(newId, this.members);
		clips.entrySet().forEach(entry -> {
			region.clips.put(entry.getKey(), Utilities.copyOf(entry.getValue()));
		});
		shootable.entrySet().forEach(entry -> {
			region.shootable.put(entry.getKey(), Utilities.copyOf(entry.getValue()));
		});

		worldObjects.put(newId, worldObjects.get(id));

		return region;
	}

	public void destroy() {
		clips.clear();
		shootable.clear();
		regions.remove(id);
		worldObjects.remove(id);
	}

	public int getWorldX() {
		return ((id >> 8) & 0xFF) * 64;
	}

	public int getWorldY() {
		return (id & 0xFF) * 64;
	}

	public static void copyArea(int startX, int startY, Boundary worldBound) {
		for(int z = 0; z < 4; z++) {
			for(int x = worldBound.getMinimumX(); x < worldBound.getMaximumX(); x++) {
				for(int y = worldBound.getMinimumY(); y < worldBound.getMaximumY(); y++) {
					Location newLocation = new Location(startX + (x - worldBound.getMinimumX()), startY + (y - worldBound.getMinimumY()), z);
					setClipping(newLocation.getX(), newLocation.getY(), newLocation.getZ(), getClipping(x, y, z));
					setProjectileClipping(newLocation.getX(), newLocation.getY(), newLocation.getZ(), getProjectileClipping(x, y, z));
					List<WorldObject> objectsOnTile = Region.getWorldObjectsAt(x, y, z);
					objectsOnTile
							.stream()
							.map(worldObject -> worldObject.copy(newLocation))
							.forEach(Region::addWorldObject);
				}
			}
		}
	}

	public static void deleteRegion(int regionId, int width, int length) {
		int regionX = regionId >> 8;
		int regionY = regionId & 0xff;
		for (int rX = 0; rX < width; rX++) {
			for (int rY = 0; rY < length; rY++) {
				int newRegionId = ((regionX + rX) << 8) + (regionY + rY);
				Region region = regions.get(newRegionId);
				if (region != null) {
					region.destroy();
					World.getWorld().getGlobalObjects().destroyObjectsAt(region.getBoundary().streamWithHeight().collect(Collectors.toList()));
				}
			}
		}
	}

	private Boundary getBoundary() {
		int regionX = (id >> 8) * 64;
		int regionY = (id & 0xff) * 64;
		return new Boundary(regionX, regionY, regionX + 64, regionY + 64);
	}

	public static void loadPalette(Location minimum, Palette palette) {
		// [plane][x][y]
		byte[][][] tileFlags = new byte[palette.getHeight()][palette.getWidth() * 8][palette.getLength() * 8];

		for (int z = 0; z < palette.getHeight(); z++) {
			for (int x = 0; x < palette.getWidth(); x++) {
				for (int y = 0; y < palette.getLength(); y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					if (tile == null) continue;

					int sourceChunkX = tile.getX();
					int sourceChunkY = tile.getY();
					int rotation = tile.getRotation();

					// Calculate Region ID from the SOURCE chunk, not the instance chunk!
					int regionId = (sourceChunkX / 8 << 8) + (sourceChunkY / 8);

					Optional<RegionData> data = MapIndexLoader.lookup(regionId);
					if (data.isPresent()) {
						try {
							RegionData regionData = data.get();
							byte[] objFile = getBuffer(new File("./Data/world/map/" + regionData.getObjects() + ".gz"));
							byte[] lsFile = getBuffer(new File("./Data/world/map/" + regionData.getLandscape() + ".gz"));

							if (objFile != null && lsFile != null) {
								for (int planeIndex = 0; planeIndex < 4; planeIndex++) {
									// These need to be the 0-56 local chunk offsets
									int localChunkX = (sourceChunkX & 7) * 8;
									int localChunkY = (sourceChunkY & 7) * 8;
									int sourcePlane = tile.getZ();

									// Decode the landscape and objects for ONLY the source plane
									decodeConstructedMapsLandscape(tileFlags, new ByteStream(lsFile), sourcePlane, rotation, x * 8, localChunkX, localChunkY, z, y * 8);
									decodeConstructedMapObjects(minimum, tileFlags, sourcePlane, x * 8, localChunkY, z, new ByteStream(objFile), localChunkX, rotation, y * 8);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						// This will now print Region 13138 (or similar) instead of Region 1!
						System.out.println("[CoX ERR] No map data found for Region: " + regionId + " (Source Chunk: " + sourceChunkX + ", " + sourceChunkY + ")");
					}
				}
			}
		}

		// Apply clipping to the grid
		int count = 0;
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < tileFlags[z].length; x++) {
				for (int y = 0; y < tileFlags[z][x].length; y++) {
					if ((tileFlags[z][x][y] & 1) == 1) {
						int heightOffset = z;
						if ((tileFlags[1][x][y] & 2) == 2) heightOffset--;
						int finalHeight = minimum.getZ() + heightOffset;
						if (finalHeight >= 0) {
							addClipping(minimum.getX() + x, minimum.getY() + y, finalHeight, 0x200000);
							count++;
						}
					}
				}
			}
		}
		System.out.println("[CoX] Successfully injected " + count + " floor clipping flags to Height " + minimum.getZ());
	}
	/**
	 * Reverts collision changes caused by a dynamic map palette load.
	 */
	public static void unloadPalette(Location base, int width, int length) {
		for (int z = 0; z < 4; z++) {
			for (int x = 0; x < width * 8; x++) {
				for (int y = 0; y < length * 8; y++) {
					int absX = base.getX() + x;
					int absY = base.getY() + y;

					// Reset clipping for this tile
					setClipping(absX, absY, base.getZ() + z, 0);
					setProjectileClipping(absX, absY, base.getZ() + z, 0);

					// Clean up world objects in this area
					List<WorldObject> objs = getWorldObjectsAt(absX, absY, base.getZ() + z);
					for(WorldObject obj : objs) {
						remove(obj.getId(), obj.getX(), obj.getY(), obj.getHeight(), obj.getType(), obj.getFace());
					}
				}
			}
		}
	}
	public static void replace(WorldObject oldObj, WorldObject newObj) {
		remove(oldObj.getId(), oldObj.getX(), oldObj.getY(), oldObj.getHeight(), oldObj.getType(), oldObj.getFace());
		World.getWorld().getGlobalObjects().add(newObj.toGlobalObject());
	}



}