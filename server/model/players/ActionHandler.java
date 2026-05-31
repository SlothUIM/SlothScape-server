package server.model.players;

import server.Config;
import server.Server;
import server.model.objects.Object;
import server.util.Misc;
import server.util.ScriptManager;
import server.model.minigames.Sailing;
import server.event.*;
import server.model.content.STASHConfig;
import server.model.content.STASH;
import server.model.npcs.NPCHandler;
import server.model.npcs.NPC;
import server.event.Event;
import server.clip.ObjectDef;
import server.model.players.skills.farming.*;
import server.model.players.skills.smithing.*;
import server.model.players.skills.*;
import server.model.players.skills.agility.*;
import server.model.players.skills.agility.impl.*;
import server.model.players.skills.agility.impl.rooftop.*;
import server.model.players.skills.mining.*;
import server.model.players.movements.ladders;
import server.model.players.Position;
import server.model.players.packets.AttackPlayer;

public class ActionHandler {

	private Player c;

	public ActionHandler(Player player) {
		this.c = player;
	}
				private void handleRandomEventOptionA(Player c) {
					c.getPA().walkTo(4, 0);
					c.getPA().addSkillXP(5, c.playerAgility);
					c.getPA().requestUpdates();

					c.sendMessage("You make your way across.");
					// Schedule a delayed action to reset the jump
					scheduleDelayedAction(c, 4, null);
				}

				private void handleRandomEventOptionB(Player c) {
					c.playerTurnIndex = 762;
					c.sendMessage("You begin to cross..");
					c.getPA().walkTo(2, 0);
					c.postProcessing();
					c.getPA().addSkillXP(5, c.playerAgility);
					c.getPA().requestUpdates();

					// Schedule a series of actions
					scheduleOptionBActions(c);
				}

				private void scheduleOptionBActions(Player c) {
					// Schedule the animation change
					scheduleDelayedAction(c, 4, () -> { c.startAnimation(771);
														c.sendMessage("..you slip..");
					});

					// Schedule the movement and final adjustments
					scheduleDelayedAction(c, 5, () -> {
						c.sendMessage("..and fall off the log.");
						c.turnPlayerTo(c.getX(), c.getY() + 1);
						c.getPA().movePlayer(c.getX(), c.getY() - 1, 0);
						c.getPA().requestUpdates();

						// Schedule additional actions
						scheduleDelayedAction(c, 2, () -> {
							c.playerStandIndex = 773;
							c.playerWalkIndex = 772;
							c.playerRunIndex = 772;
							c.playerTurnIndex = 772;
							c.getPA().requestUpdates();
							c.postProcessing();

							// Schedule further actions
							scheduleDelayedAction(c, 2, () -> {
								c.turnPlayerTo(c.getX(), c.getY() - 1);

								// Schedule the final actions
								scheduleDelayedAction(c, 4, () -> {
									c.getPA().walkTo(0, -6);
									c.postProcessing();

									// Schedule the last set of actions
									scheduleDelayedAction(c, 4, () -> {
										// Reset the log crossing
										c.sendMessage("You barely swim your way to shore");
										c.getPA().walkTo(-2, 0);
										c.postProcessing();
										c.getPA().resetlogCross(c);

									});
								});
							});
						});
					});
				}
				public void PassDoor(Player c, int ID, int X, int Y, int X2, int Y2, int moveX, int moveY, int face){
					c.getPA().object(ID, X2, Y2, face, 0);
					c.getPA().object(-1, X, Y, 0, 0);
							c.getPA().walkTo(moveX, moveY);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
								container.stop();
						}

						@Override
						public void stop() {

						c.getPA().object(-1, X2, Y2, face, 0);
						c.getPA().object(ID, X, Y, 0, 0);
						}

					}, 2);
				}	
				public void PassDoubleDoor(Player c, int ID, int ID2, int X, int Y, int X2, int Y2, int X3, int Y3, int X4, int Y4, int moveX, int moveY, int face, int face2){
					c.getPA().object(ID, X3, Y3, face, 0);
					c.getPA().object(ID2, X4, Y4, face2, 0);
					c.getPA().object(-1, X, Y, 0, 0);
					c.getPA().object(-1, X2, Y2, 0, 0);
							c.getPA().walkTo(moveX, moveY);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
								container.stop();
						}

						@Override
						public void stop() {

					c.getPA().object(-1, X3, Y3, face, 0);
					c.getPA().object(-1, X4, Y4, face, 0);
					c.getPA().object(ID, X, Y, 0, 0);
					c.getPA().object(ID2, X2, Y2, 0, 0);
						}

					}, 2);
				}
				public void PassDoubleGate(Player c, int ID, int X, int Y, int X2, int Y2, int ID2, int face, int face2, int moveX, int moveY, int lastX, int lastY, int lastX2, int lastY2){
					
					c.getPA().object(-1, lastX, lastY, 0, 0);
					c.getPA().object(-1, lastX2, lastY2, 0, 0);
					c.getPA().object(ID, X, Y, face, 0);
					c.getPA().object(ID2, X2, Y2, face, 0);
					c.getPA().walkTo(moveX, moveY);
						//PassDoubleGate(c, 3015, 3090, 3092, 3091, 3092, 3016, 1, 2, -1, 0);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
								container.stop();
						}

						@Override
						public void stop() {

						c.getPA().object(-1, X, Y, 0, 0);
						c.getPA().object(-1, X2, Y2, 0, 0);
						c.getPA().object(ID, lastX, lastY, face2, 0);
						c.getPA().object(ID2, lastX2, lastY2, face2, 0);
						}

					}, 2);
				}
		private void scheduleDelayedAction(Player c, int cycles, Runnable action) {
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if(action != null)
					action.run();
					container.stop();
				}

				@Override
				public void stop() {
				}
			}, cycles);
		}

						int test = 0;
	public void firstClickObject(int objectType, int obX, int obY) {
        c.faceUpdate(0);
		c.clickObjectType = 0;
		ObjectDef def = ObjectDef.forID(objectType);
        c.turnPlayerTo(obX, obY);
        if (c.stopPlayerPacket) {
            return;
        }
						switch (def.type) {
			

		default:
			ScriptManager.callFunc("objectClick1_" + objectType, c, objectType,
					obX, obY);
			break;

		}
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		//c.getAD().DiaryObjectClick(objectType, 2);
						//c.getPA().requestUpdates();
						
		ObjectDef def = ObjectDef.forID(objectType);
		c.sendMessage("Object click type 2: " + def.type);
        if (Farming.inspectObject(c, obX, obY)) {
            return;
        }
		switch (def.type) {
			
		default:
			ScriptManager.callFunc("objectClick2_" + objectType, c, objectType,
					obX, obY);
			break;
		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.sendMessage("Object type 3: " + objectType);
						//c.getPA().requestUpdates();
		switch (objectType) {
			
		default:
			ScriptManager.callFunc("objectClick3_" + objectType, c, objectType,
					obX, obY);
			break;
		}
	}

	public void firstClickNpc(int i) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		
		/*if (Fishing.fishingNPC(c, i)) {
			Fishing.fishingNPC(c, 1, i);
		}*/
		switch (i) {
			
			default:
			break;
		}
	}

	public void secondClickNpc(int i) {
		int x = NPCHandler.npcs[c.npcClickIndex].getX();
		int y = NPCHandler.npcs[c.npcClickIndex].getY();
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		//NPC npc = NPCHandler.npcs[i];
	/*	if (Fishing.fishingNPC(c, i)) {
			Fishing.fishingNPC(c, 2, i);
		}*/
		switch (i) {
			
		}
	}

	public void thirdClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {
			
			case 705:
			/*if(!c.MeleeTutorClaimed) {
				if(!c.getItems().playerHasItem(9703, 1))
					c.getItems().addItem(9703, 1);
				if(!c.getItems().playerHasItem(9704, 1))
					c.getItems().addItem(9704, 1);*/
					c.getDH().sendDialogues(502, 705);
					//c.MeleeTutorClaimed = true;
					//c.LastMeleeTutorClaimed = System.currentTimeMillis();
			//}
				break;
		default:
			ScriptManager.callFunc("npcClick3_" + npcType, c, npcType);
			if (c.playerRights == 3)
				Misc.println("Third Click NPC : " + npcType);
			break;

		}
	}

}