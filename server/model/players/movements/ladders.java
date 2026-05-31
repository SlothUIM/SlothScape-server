package server.model.players.movements;

import server.model.players.Player;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.Config;
import server.Server;
import server.clip.ObjectDef;
import server.model.players.packets.*;
import server.world.map.*;
import server.model.players.PlayerAssistant;


/***Handles ladders***/
public class ladders {
	private Player c;
	public ladders(Player c){
		this.c = c;
	}
	public int[] stairs = {};
	public int[] holes = {20790};
	
	public void objectClick(int clickType, int objectID, int obX, int obY) {
		int click = clickType;
		
		switch(click) {
			case 1:
			switch(objectID) {
				case 16683:
				case 16679:
					if(obX == 2665 && obY == 3290)
						climbLadder(objectID, obX, obY, 0, 1, 0);
					else if(obX == 2674 && obY == 3309)
						climbLadder(objectID, obX, obY, 0, -1, 0);
				break;
				case 11789://varrock east bank stairs
					if(obX == 3255 && obY == 3421)
						climbLadderorStair(3233, 3424, 3257, 3421, 1, true, true);
				break;
				case 11793://varrock east bank stairs down
					if(obX == 3255 && obY == 3421)
						climbLadderorStair(3233, 3424, 3257, 3421, 0, true, true);
				break;
				case 11794://lowes store ladder up
				case 11795://lowes store ladder down
					if(obX == 3233 && obY == 3424)
						climbLadder(objectID, obX, obY, 1, 0, 0);
				break;
				case 25938://seers bank up
				case 25939://seers bank down
					if(obX == 2728 && obY == 3491)
						climbLadder(objectID, obX, obY, 0, 1, 0);
					else if(obX == 2715 && obY == 3470)
						climbLadder(objectID, obX, obY, 0, 1, 0);
				break;
				case 25941://seers house up
				case 25940://seers house down
					if(obX == 2699 && obY == 3476)
						climbLadder(objectID, obX, obY, 0, -1, 0);
				break;
				case 20790:
					climbLadderorStair(obX, obY, 1859, 5243, 0, true, false);
				break;
				case 20784:
				if(obX == 1913 && obY == 5226)
					climbLadderorStair(obX, obY, 1859, 5243, 0, true, true);
				break;
				case 20786:
				if(obX == 1863 && obY == 5328)
					usePortal(1912, 5226, 0);
				break;
				case 12389:
				if(obX == 3116 && obY == 3452)
					climbLadderorStair(obX, obY, 2956, 3338, 1, false, false);
				break;
				case 28894:
				if(obX == 1666 && obY == 10051)
					climbLadderorStair(obX, obY, 1639, 3673, 0, false, false);
				break;
				case 27785:
				if(obX == 1634 && obY == 3671)
					climbLadderorStair(obX, obY, 1666, 10051, 0, false, false);
				break;
				case 28897:
				if(obX == 1726 && obY == 9993)
					climbLadderorStair(obX, obY, 1467, 3653, 0, false, false);
				break;
				case 34862:
					climbLadderorStair(obX, obY, 1829, 9973, 0, false, false);
				break;
				case 34863:
					climbLadderorStair(obX, obY, 1702, 3575, 0, false, false);
				break;
			}
			break;
			case 2:
			switch(objectID) {
			}
			break;
			case 3:
			switch(objectID) {
			}
			break;
		}
			
	}
	public void climbLadderorStair(int obX, int obY, int nextX, int nextY, int height, boolean ladder, boolean ladderUp) {
		if(ladder){
            c.turnPlayerTo(obX, obY);
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						container.stop();
					}
					@Override
					public void stop() {
						if(ladderUp)
						c.startAnimation(828);
						if(!ladderUp)
						c.startAnimation(827);
							CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}
						@Override
						public void stop() {
							c.getPA().movePlayer(nextX, nextY, height);
						}
					}, 2);	
					}
				}, 1);	
		} else{ 			
			c.getPA().movePlayer(nextX, nextY, height);
		}
	}	
			/*******Must Match up and down*****/
	public int[][] ladders = {{11794, 16683, 17026, 25938, 25941},//up
							  {11795, 16679, 16685, 25939, 25940},};//down
							  
		public void climbLadder(int objID, int obX, int obY, int offsetX, int offsetY, int height) {
			ObjectDef objectdef = ObjectDef.forID(objID);
            c.turnPlayerTo(obX, obY);
			for(int i = 0; i < ladders[0].length; i++){
				if(objectdef.type == ladders[0][i]){//going up ladders
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}
						@Override
						public void stop() {
							c.startAnimation(828);
								CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								container.stop();
							}
							@Override
							public void stop() {
								c.getPA().movePlayer(obX+offsetX, obY+offsetY, height+1);
							}
						}, 2);	
						}
					}, 1);	
				} else if(objectdef.type == ladders[1][i]){//going down ladders
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							container.stop();
						}
						@Override
						public void stop() {
							c.startAnimation(827);
								CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
							@Override
							public void execute(CycleEventContainer container) {
								container.stop();
							}
							@Override
							public void stop() {
								c.getPA().movePlayer(obX+offsetX, obY+offsetY, height+0);
							}
						}, 2);	
						}
					}, 1);	
				}
			}
		}
	public void usePortal(int nextX, int nextY, int nextHeight){
		c.getPA().movePlayer(nextX, nextY, nextHeight);
	}
		
}
