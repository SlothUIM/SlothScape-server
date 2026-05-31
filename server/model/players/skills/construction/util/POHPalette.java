package server.model.players.skills.construction.util;

import lombok.Getter;
import lombok.Setter;

public class POHPalette {
	
	/**
	 * Normal direction.
	 */
	public static final int DIRECTION_NORMAL = 0;
	
	/**
	 * Rotation direction clockwise by 0 degrees.
	 */
	public static final int DIRECTION_CW_0 = 0;
	
	/**
	 * Rotation direction clockwise by 90 degrees.
	 */
	public static final int DIRECTION_CW_90 = 1;
	
	/**
	 * Rotation direction clockwise by 180 degrees.
	 */
	public static final int DIRECTION_CW_180 = 2;
	
	/**
	 * Rotation direction clockwise by 270 degrees.
	 */
	public static final int DIRECTION_CW_270 = 3;
	
	/**
	 * Represents a tile to copy in the palette.
	 * @author Graham Edgecombe
	 *
	 */
	public static class POHPaletteTile {
		
		/**
		 * X coordinate.
		 */
		private int x;
		
		/**
		 * Y coordinate.
		 */
		private int y;
		
		/**
		 * Z coordinate.
		 */
		private int z;
		
		/**
		 * Rotation.
		 */
		@Getter @Setter
		private int rot;

		/**
		 * If visible
		 */
		@Getter @Setter
		private boolean visible = true;
		/**
		 * Creates a tile.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 */
		public POHPaletteTile(int x, int y) {
			this(x, y, 0);
		}
		
		/**
		 * Creates a tile.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 * @param z The z coordinate.
		 */
		public POHPaletteTile(int x, int y, int z) {
			this(x, y, z, DIRECTION_NORMAL);
		}
		
		/**
		 * Creates a tile.
		 * @param x The x coordinate.
		 * @param y The y coordinate.
		 * @param z The z coordinate.
		 * @param rot The rotation.
		 */
		public int locationHash;
		public POHPaletteTile(int x, int y, int z, int rot) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.rot = rot;
			this.locationHash = x / 8 << 14 | y / 8 << 3 | z % 4 << 24 | rot % 4 << 1;
		}
		/**
		 * Gets the x coordinate.
		 * @return The x coordinate.
		 */
		public int getX() {
			return x / 8;
		}
		
		/**
		 * Gets the y coordinate.
		 * @return The y coordinate.
		 */
		public int getY() {
			return y / 8;
		}
		public boolean isEmpty() {
		    return x < 0 || y < 0;
		}
		/**
		 * Gets the z coordinate.
		 * @return The z coordinate.
		 */
		public int getZ() {
			return z % 4;
		}
		
		/**
		 * Gets the rotation.
		 * @return The rotation.
		 */
		public int getRotation() {
			return rot % 4;
		}
		
	}
	
	/**
	 * The array of tiles.
	 */
	private POHPaletteTile[][][] tiles = new POHPaletteTile[4][13][13];
	
	/**
	 * Gets a tile.
	 * @param x X position.
	 * @param y Y position.
	 * @param z Z position.
	 * @return The tile.
	 */
	public POHPaletteTile getTile(int x, int y, int z) {
		return tiles[z][x][y];
	}
	
	/**
	 * Sets a tile.
	 * @param x X position.
	 * @param y Y position.
	 * @param z Z position.
	 * @param tile The tile.
	 */
	public void setTile(int x, int y, int z, POHPaletteTile tile) {
		tiles[z][x][y] = tile;
	}

}