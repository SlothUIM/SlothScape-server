package server.model.players;

import java.util.ArrayList;


import server.Server;
//import com.allantois.GameEngine;
import server.model.npcs.NPC;
import server.model.players.skills.construction.util.POHPalette;
import server.model.map.Palette;
import server.model.map.PaletteTile;
import server.world.World;
/**
 * 
 * @author Owner Blade
 *
 */
public class MapInstance {

	private POHPalette palette;
	private POHPalette secondaryPalette;
	public enum RegionInstanceType {
		BARROWS, GRAVEYARD, FIGHT_CAVE, WARRIORS_GUILD, NOMAD, RECIPE_FOR_DISASTER, CONSTRUCTION_HOUSE, CONSTRUCTION_DUNGEON;
	}

	private RegionInstanceType type;
	public ArrayList<Player> members;
	public ArrayList<NPC> npcs;
	private boolean global;
	private int leaveX, leaveY;
	private Player owner;
	public MapInstance(int leaveX, int leaveY, boolean global)
	{
		this(leaveX, leaveY);
		this.global = global;
	}
	
	public MapInstance(int leaveX, int leaveY)
	{
		this.setLeaveX(leaveX);
		this.setLeaveY(leaveY);
		members = new ArrayList<Player>();
		npcs = new ArrayList<NPC>();
		type = RegionInstanceType.CONSTRUCTION_HOUSE;
	}
	public void setPalette(POHPalette palette)
	{
		this.palette = palette;
	}
	public POHPalette getPalette()
	{
		return palette;
	}
	public void addMember(Player p)
	{
		members.add(p);
		p.mapInstance = this;
	}

	public void addNPC(NPC npc)
	{
		npcs.add(npc);
		npc.mapInstance = this;
	}
	public void destroy()
	{
		for(Player p : members)
		{
			if(p == null)
				continue;
			if(p.mapInstance == null)
				continue;
			if(!(getLeaveX() == -1 && getLeaveY() == -1))
				p.getPA().movePlayer(getLeaveX(), getLeaveY(), 0);
			p.mapInstance = null;
		}
		for(NPC npc : npcs)
		{
			if(npc == null)
				continue;
			if(npc.mapInstance == null)
				continue;
			if(npc.mapInstance != this)
				continue;
			if(!npc.isDead) {
				//World.getWorld().npcHandler.kill(npc);
			}
		}  
		members = null;
		npcs = null;
		
	}
	public void removePlayer(Player p)
	{
		if(!(getLeaveX() == -1 && getLeaveY() == -1))
			p.getPA().movePlayer(getLeaveX(), getLeaveY(), 0);
		if(members == null)
			return;
		members.remove(p);
		p.mapInstance = null;
		playerDied();
	}
	public void playerDied()
	{
		if(global)
			return;
		if(members.size() == 1)
		{
			destroy();
		}
	}
	public POHPalette getSecondaryPalette() {
		return secondaryPalette;
	}
	public void setSecondaryPalette(POHPalette palette2) {
		this.secondaryPalette = palette2;
	}
	public Player getOwner() {
		return owner;
	}
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	public int getLeaveX() {
		return leaveX;
	}
	public void setLeaveX(int leaveX) {
		this.leaveX = leaveX;
	}
	public int getLeaveY() {
		return leaveY;
	}
	public void setLeaveY(int leaveY) {
		this.leaveY = leaveY;
	}

	public RegionInstanceType getType() {
		// TODO Auto-generated method stub
		return type;
	}
}
