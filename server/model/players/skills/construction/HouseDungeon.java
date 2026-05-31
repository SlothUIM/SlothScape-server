package server.model.players.skills.construction;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Client;
import server.model.players.MapInstance;
import server.model.players.Player;
import server.model.players.skills.construction.Construction;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.construction.Room;
import server.model.players.skills.construction.util.POHPalette;
import server.model.players.skills.construction.util.RoomData;
/**
 * 
 * @author Owner Blade
 *
 */
public class HouseDungeon extends MapInstance {

	private House house;
	public HouseDungeon(int leaveX, int leaveY, boolean b) {
		super(leaveX, leaveY, b);
	}
	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}

	public void playerKilled(Client p)
	{
		house.members.remove(p);
		removePlayer(p);
		p.getPA().showOption(3, 0, null, 1);
	}
	//@Override
	public void removePlayer(final Client p)
	{
		if(p.properLogout)
		{
			members.remove(p);
			house.members.remove(p);
			if(members.isEmpty() && house.members.isEmpty())
				house.destroy();
			return;
		}
		p.getPA().showOption(3, 0, null, 1);
		house.addMember(p);
		members.remove(p);
		int[] myTiles = Construction.getMyChunk(p);
		RoomData room = getOwner().Rooms[0][myTiles[0] - 1][myTiles[1] - 1];
		int[] converted = Construction.getConvertedCoords(3, 2, myTiles, room);
		p.toConsCoords = converted;
		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
			@Override
				public void execute(CycleEventContainer container) {

				p.getPA().removeObjects(0, 0, p.getHeight());
				if(p.mapInstance != null)
				HouseData.enterHouse(p, p.mapInstance.getOwner(),
						p.inBuildingMode);
				container.stop();
				}
				@Override
				public void stop() {
				}
			}, 1);
	}
	//@Override
	public void addMember(Client p)
	{
		members.add(p);
		p.mapInstance = this;
		final int[] myTiles_ = Construction.getMyChunk(p);
		final RoomData room_ = getOwner().Rooms[4][myTiles_[0] - 1][myTiles_[1] - 1];
		//p.getPA().showInterface(28640);
		//p.getPA().setMinimapState(2);
		final Client p_ = p;

		CycleEventHandler.getSingleton().addEvent(p, new CycleEvent() {
				
			int ticks;
			@Override
				public void execute(CycleEventContainer container) {
				if (ticks == 0) {
					p_.getPA().removeObjects(0, 0, p_.getHeight());
				}
				int[] converted = Construction.getConvertedCoords(p_.toConsCoords == null ? 1 : p_.toConsCoords[0], 
						p_.toConsCoords == null ? 5 : p_.toConsCoords[1],
						myTiles_, room_);
				p_.getPA().movePlayer(converted[0],
						converted[1], 0);
				p_.toConsCoords = null;
				if (ticks == 1) {
					p_.getPA().sendConstructedMapPOH(
							getHouse()
									.getSecondaryPalette());
					Construction.placeAllFurniture(p_, 4);
					p_.getPA().closeAllWindows();
					//p_.getPA().setMinimapState(0);
					container.stop();
				}
				ticks++;
				}
				@Override
				public void stop() {
				}
			}, 1);
		

	}
	@Override
	public POHPalette getPalette()
	{
		return house.getPalette();
	}

	
}
