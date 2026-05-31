package server.model.players.skills.construction;

import server.model.npcs.NPC;
import server.model.npcs.NPCDefinitions;
import server.model.players.Player;
/**
 * 
 * @author Owner Blade
 *
 */
public class Servant extends NPC {

	private boolean fetching, greetVisitors;
	private int[] inventory;

	private static NPCDefinitions definition;
	public Servant(int _npcId, int _npcType) {
		super(_npcId, _npcType, definition);
	}
	public Servant(int npcId, int npcType, int inventorySize, NPCDefinitions definition)
	{
		this(npcId, npcType);
		definition = this.definition;
		inventory = new int[inventorySize];
	}
	public boolean addInventoryItem(int itemId)
	{
		for(int i = 0; i < inventory.length; i++)
		{
			if(inventory[i] == 0)
			{
				inventory[i] = itemId;
				return true;
			}
		}
		return false;
	}
	public int freeSlots()
	{
		int value = 0;
		for(int i = 0; i < inventory.length; i++)
		{
			if(inventory[i] == 0)
			{
				value++;
			}
		}
		return value;
	}
	@Override
	public void kill()
	{
		if(mapInstance == null)
			return;
		super.kill();
		putBackInBank(mapInstance.getOwner());
	}
	public void putBackInBank(Player p)
	{
		p.isBanking = true;
		for(int i = 0; i < inventory.length; i++)
		{
			if(i <= 0)
				continue;
			p.getItems().addItemToBank(inventory[i], 1);
		}
		p.isBanking = false;
	}
	public void takeItemsFromBank(Player p, int itemId, int amount)
	{
		for(int i = 0; i < amount; i++)
		{
			if(freeSlots() == 0)
				return;
			//if(!p.getItems().checkBank(itemId))
			//	return;
			//if(addInventoryItem(itemId))
				//p.getItems().deleteFromBank(itemId+1, 1);
		}
	}
	public void giveItems(Player p)
	{
		for(int i = 0; i < inventory.length; i++)
		{
			if(p.getItems().freeSlots() == 0)
				break;
			p.getItems().addItem(inventory[i], 1);
			inventory[i] = 0;
		}
	}
	public int[] getInventory()
	{
		return inventory;
	}
	public boolean isFetching() {
		return fetching;
	}
	public void setFetching(boolean fetching) {
		this.fetching = fetching;
	}
	public boolean isGreetVisitors() {
		return greetVisitors;
	}
	public void setGreetVisitors(boolean greetVisitors) {
		this.greetVisitors = greetVisitors;
	}

	
}
