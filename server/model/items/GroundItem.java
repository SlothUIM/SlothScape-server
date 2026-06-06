package server.model.items;

import lombok.Getter;
import lombok.Setter;
import server.model.Entity;
import server.model.instance.Instance;

/**
 * Represents an item on the ground at a specific location on the map.
 * 
 * @author Jason MacKeigan
 * @date Feb 10, 2015, 4:08:14 PM
 */
public class GroundItem {
	
	@Getter @Setter
	private Instance instance;
	
	public boolean sameInstance(Entity entity) {
		return this.instance == entity.getInstance();
	}

	/**
	 * The identification value of the item that makes it unique from the rest
     * -- GETTER --
     *  The item identification value
     *
     * @return the item id

     */
	@Getter
    private int itemId;

	/**
	 * The location of the item on the x-axis
	 */
	@Getter
    private int itemX;

	/**
	 * The location of the item on the y-axis
	 */
	@Getter
    private int itemY;

	/**
	 * The height level of the item on the ground
	 */
	private int itemZ;

	/**
	 * The amount of the item.
     * -- GETTER --
     *  The amount of the item that exists at this position
     *
     * @return the amount of the item

     */
	@Getter
    private int itemAmount;

	public int hideTicks;

	public int removeTicks;

	public String ownerName;

	/**
	 * Creates a new {@link GroundItem} object on the x, y, and z-axis.
	 * 
	 * @param id the identification value of the item
	 * @param x the x location
	 * @param y the y location
	 * @param height the height on the map
	 * @param amount the amount of the item
	 * @param controller the player id, the controller
	 * @param hideTicks the amount of ticks until hidden
	 * @param name the name of the owner
	 */
	public GroundItem(int id, int x, int y, int height, int amount, int hideTicks, String name) {
		this.itemId = id;
		this.itemX = x;
		this.itemY = y;
		this.itemZ = height;
		this.itemAmount = amount;
		this.hideTicks = hideTicks;
		this.ownerName = name;
	}

    /**
	 * Retrieves the absolute x position of the item on the map
	 * 
	 * @return the x position
	 */
	public int getX() {
		return itemX;
	}

	/**
	 * Retrieves the absolute y position of the item on the map
	 * 
	 * @return the y position
	 */
	public int getY() {
		return itemY;
	}

    /**
	 * Item name.
	 * 
	 * @return
	 */
	public String getItemController() {
		return this.ownerName;
	}

	/**
	 * The ground item must be displayed on a height level. The ground item can only appear on the height level its created on.
	 * 
	 * @return the height level of the ground item
	 */
	public int getHeight() {
		return itemZ;
	}

    public int getItemZ() {
		return itemZ;
	}
	public String getName() {
		return ownerName;
	}
}