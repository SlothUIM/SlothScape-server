package server.model.players.skills.construction;

import java.util.ArrayList;





import server.model.players.Player;
import server.model.npcs.NPC;
//import com.allantois.game.npcs.NPC;
import server.model.players.skills.construction.Servant;
import server.model.players.skills.construction.util.HouseFurniture;
import server.model.players.skills.construction.util.RoomData;
import server.model.players.MapInstance;
import server.model.players.skills.construction.Construction;
import server.model.players.skills.construction.ConstructionData;
/**
 * 
 * @author Owner Blade
 *
 */
public class House extends MapInstance {

	private HouseDungeon dungeon;
	private ArrayList<HouseFurniture> litBurners;
	private ArrayList<HouseFurniture> furnitureActivated;
	private boolean locked = false;
	public House(int leaveX, int leaveY) {
		super(leaveX, leaveY);
		setLitBurners(new ArrayList<HouseFurniture>());
	}
	public House(int leaveX, int leaveY, boolean b)
	{
		super(leaveX, leaveY, b);
		setLitBurners(new ArrayList<HouseFurniture>());
		setFurnitureActivated(new ArrayList<HouseFurniture>());
		dungeon = new HouseDungeon(leaveY, leaveY, false);
		dungeon.setHouse(this);
	}
	public Servant getButler()
	{
		for(NPC npc : npcs)
		{
			if(npc.npcType == getOwner().houseServant)
				return (Servant)npc;
		}
		return null;
	}
	public void processPlayer(Player player)
	{
		House house = player.mapInstance instanceof House ? (House)player.mapInstance : ((HouseDungeon)player.mapInstance).getHouse();
		int[] myTiles = Construction.getMyChunk(player);
		if(myTiles == null)
			return;
		if(myTiles[0] == -1 || myTiles[1] == -1)
			return;
		RoomData r = house.getOwner().Rooms[player.inDungeon() ? 4 : player.getHeight()][myTiles[0]-1][myTiles[1]-1];
		if(r == null)
			return;
		if(r.getType() == ConstructionData.OUBLIETTE)
		{
			int xOnTile = Construction.getXTilesOnTile(myTiles, player.getX());
			int yOnTile = Construction.getYTilesOnTile(myTiles, player.getY());
			if(xOnTile >=  2 && xOnTile <= 5
					&& yOnTile >= 2 && yOnTile <= 5)
			{
				HouseFurniture pf = null;
				for(HouseFurniture pf_ : house.getOwner().houseFurniture)
				{
					if(pf_.getRoomX() == myTiles[0]-1 && pf_.getRoomY() == myTiles[1]-1
							&& pf_.getHotSpotId() == 85)
					{
						pf = pf_;
						break;
					}
				}
				if(pf != null) {	
					if(pf.getFurnitureId() == 13334
							|| pf.getFurnitureId() == 13337)
					{
						/*if(!p.isDead)
							p.getCombat().appendHit(p, 20, 0, 2, false);*/
					}
				}
			}
		}
		
		if(r.getType() == ConstructionData.CORRIDOR)
		{
			int[] converted = Construction.getConvertedCoords(3, 2, myTiles, r);
			int[] converted_1 = Construction.getConvertedCoords(4, 2, myTiles, r);
			int[] converted_2 = Construction.getConvertedCoords(3, 5, myTiles, r);
			int[] converted_3 = Construction.getConvertedCoords(4, 5, myTiles, r);
			if(player.getX() == converted[0] && player.getY() == converted[1]
					|| player.getX() == converted_1[0] && player.getY() == converted_1[1]
					|| player.getX() == converted_2[0] && player.getY() == converted_2[1]
					|| player.getX() == converted_3[0] && player.getY() == converted_3[1])
			{
				HouseFurniture pf = null;
				for(HouseFurniture pf_ : house.getOwner().houseFurniture)
				{
					if(pf_.getRoomX() == myTiles[0] && pf_.getRoomY() == myTiles[1]
							&& pf_.getHotSpotId() == 91)
					{
						int[] coords = Construction.getConvertedCoords(pf_.getStandardXOff(), pf_.getStandardYOff(), myTiles, r);
						if(coords[0] == myTiles[0] && coords[1] == myTiles[1]) {
							pf = pf_;
							break;
						}
					}
				}
				if(pf != null) {
					if(pf.getFurnitureId() >= 13356
							|| pf.getFurnitureId() <= 13360)
					{
						/*if(!p.isDead)
							p.getCombat().appendHit(p, 20, 0, 2, false);*/
					}
				}
			}
		}
	}
	
	public void playerKilled(Player p)
	{
		int[] myTiles = Construction.getMyChunk(p);
		if(p.combatRingType > 0)
		{

			p.getPA().showOption(3, 0, null, 1);
			p.combatRingType = 0;
			p.getPA().movePlayer(ConstructionData.BASE_X+(myTiles[0]*8)+1, ConstructionData.BASE_Y+(myTiles[1]*8)+1, p.getHeight());
		} else {
			HouseFurniture portal = Construction.findNearestPortal(p);
			int toX = ConstructionData.BASE_X+((portal.getRoomX()+1)*8);
			int toY = ConstructionData.BASE_Y+((portal.getRoomY()+1)*8);
			p.getPA().movePlayer(toX+2, toY+2, 0);
		}
	}
	public ArrayList<HouseFurniture> getLitBurners() {
		return litBurners;
	}
	public void setLitBurners(ArrayList<HouseFurniture> litBurners) {
		this.litBurners = litBurners;
	}

	public int getBurnersLit(int roomX, int roomY, int roomZ)
	{
		int i = 0;
		for(HouseFurniture pf : litBurners)
		{
			if(roomX == pf.getRoomX() && roomY == pf.getRoomY() && roomZ == pf.getRoomZ())
				i++;
				
		}
		return i;
	}
	public ArrayList<HouseFurniture> getFurnitureActivated() {
		return furnitureActivated;
	}
	public void setFurnitureActivated(ArrayList<HouseFurniture> furnitureActivated) {
		this.furnitureActivated = furnitureActivated;
	}
	public ArrayList<HouseFurniture> getActivatedObject(int roomX, int roomY, int roomZ)
	{
		ArrayList<HouseFurniture> pfs = new ArrayList<HouseFurniture>();
		for(HouseFurniture pf : furnitureActivated)
		{
			if(roomX == pf.getRoomX() && roomY == pf.getRoomY() && roomZ == pf.getRoomZ())
				pfs.add(pf);
				
		}
		return pfs;
	}
	
	public void greet(Player p)
	{
		Servant s = getButler();
		if(s == null)
			return;
		if(s.isGreetVisitors())
		{
			s.forceChat("Welcome "+p.playerName+"!");
		}
	}
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	public HouseDungeon getDungeon() {
		return dungeon;
	}
	public void setDungeon(HouseDungeon dungeon) {
		this.dungeon = dungeon;
	}
	@Override
	public void removePlayer(Player p)
	{
		dungeon.removePlayer(p);
		super.removePlayer(p);
	}
	@Override
	public void destroy()
	{
		dungeon.destroy();
		super.destroy();
	}
	@Override
	public void setOwner(Player p)
	{
		super.setOwner(p);
		dungeon.setOwner(p);
	}
	
}
