package server.model.players.skills.runecraft;

import server.Server;
import server.model.players.*;
import server.model.items.*;
import server.model.npcs.*;
import server.model.players.skills.*;
import server.util.Misc;
import server.event.*;
import server.model.objects.Object;
import server.event.Event;

	public class Abyss {
			
			
			private Client c;
			public Abyss(Client c) {
				this.c = c;
			}
			
				int abyssObjectConfig;
				public void setConfig(int slot) {
					c.getPA().sendConfig(491, slot << 18);
				}
				int[] object = new int[12];
				public void loadAbyss(Client c, int absX, int absY) {
					abyssObjectConfig = Misc.random(3);
					setConfig(abyssObjectConfig);
					c.getPA().startTeleport2(absX, absY, 0);
				}
				public void objectInteraction(int objectID, int obX, int obY) {
					
					if(objectID == 7146){
					setObjectSkills(obX, obY);
						if(object[3] == c.playerThieving)
							timer(7170, 7168, 843, 3028, 4841);
						if(object[3] == c.playerAgility)
							timer(7164, 7164, 843, 3028, 4841);
						if(object[3] == c.playerFiremaking)
							timer(7167, 7165, 843, 3028, 4841);
						if(object[3] == c.playerWoodcutting)
							timer(7163, 7161, 843, 3028, 4841);
						if(object[3] == c.playerMining)
							timer(7160, 7158, 843, 3028, 4841);
						if(object[3] == -1)
							timer(7163, 7161, 843, 3028, 4841);
						if(object[3] == 65535)
							return;
					}
					if(objectID == 7147){
					setObjectSkills(obX, obY);
						if(object[4] == c.playerAgility)
							timer(7164, 7164, 843, 3031, 4843);
						if(object[4] == c.playerThieving)
							timer(7170, 7168, 843, 3031, 4843);
						if(object[4] == c.playerFiremaking)
							timer(7167, 7165, 843, 3031, 4843);
						if(object[4] == c.playerWoodcutting)
							timer(7163, 7161, 843, 3031, 4843);
						if(object[4] == c.playerMining)
							timer(7160, 7158, 843, 3031, 4843);
						if(object[4] == -1)
							timer(7154, 7154, 843, 3031, 4843);
						if(object[4] == 65535)
							return;
					}
					if(objectID == 7148){
					setObjectSkills(obX, obY);
						if(object[5] == c.playerAgility)
							timer(7164, 7164, 843, 3040, 4845);
						if(object[5] == c.playerThieving)
							timer(7164, 7164, 843, 3040, 4845);
						if(object[5] == c.playerFiremaking)
							timer(7167, 7165, 843, 3040, 4845);
						if(object[5] == c.playerWoodcutting)
							timer(7163, 7161, 843, 3040, 4845);
						if(object[5] == c.playerMining)
							timer(7160, 7158, 843, 3040, 4845);
						if(object[5] == 65535)
							return;
						if(object[5] == -1)
							timer(-1, -1, 843, 3040, 4845);
					}	
					if(objectID == 7149){
					setObjectSkills(obX, obY);
						if(object[5] == c.playerAgility)
							timer(7164, 7164, 843, 3040, 4845);
						if(object[5] == c.playerThieving)
							timer(7164, 7164, 843, 3040, 4845);
						if(object[5] == c.playerFiremaking)
							timer(7167, 7165, 843, 3040, 4845);
						if(object[5] == c.playerWoodcutting)
							timer(7163, 7161, 843, 3040, 4845);
						if(object[5] == c.playerMining)
							timer(7160, 7158, 843, 3040, 4845);
						if(object[5] == 65535)
							return;
						if(object[5] == -1)
							timer(-1, -1, 843, 3040, 4845);
					}	
					if(objectID == 7133)//nature
						c.getPA().startTeleport2(2398, 4841, 0);
					if(objectID == 7132)//cosmic
						c.getPA().startTeleport2(2162, 4833, 0);
					if(objectID == 7131)//body
						c.getPA().startTeleport2(2527, 4833, 0);
					if(objectID == 7130)//earth
						c.getPA().startTeleport2(2660, 4839, 0);
					if(objectID == 7129)//fire
						c.getPA().startTeleport2(2584, 4836, 0);
					if(objectID == 7134)//chaos
						c.getPA().startTeleport2(2269, 4843, 0);
					if(objectID == 7135)//law
						c.getPA().startTeleport2(2464, 4834, 0);
					if(objectID == 7136)//death
						c.getPA().startTeleport2(2207, 4836, 0);
					if(objectID == 7137)//water
						c.getPA().startTeleport2(2713, 4836, 0);
					if(objectID == 7138)//soul
						c.getPA().startTeleport2(2269, 4843, 0);
					if(objectID == 7139)//air
						c.getPA().startTeleport2(2845, 4832, 0);
					if(objectID == 7140)//mind
						c.getPA().startTeleport2(2796, 4818, 0);
				}
				
				public void timer(int object, int object2, int anim, int nextX, int nextY) {
					c.startAnimation(anim);
					CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
						@Override
						public void execute(CycleEventContainer container) {
							c.sendMessage("You attempt to find a way through.");
									new Object(object, c.objectX, c.objectY, c.getHeight(), 0, 10, object2, 10);
							container.stop();
						}

						@Override
						public void stop() {
							c.getPA().movePlayer(nextX, nextY, 0);
						}
					}, 7);
				}
				public void setObjectSkills(int objectX, int objectY){
					if(abyssObjectConfig == 0){
						if(objectX == 3026 && objectY == 4813)
							object[0] = 65535;
						if(objectX == 3018 && objectY == 4821)
							object[1] = c.playerWoodcutting;
						if(objectX == 3018 && objectY == 4833)
							object[2] = c.playerFiremaking;
						if(objectX == 3021 && objectY == 4842)
							object[3] = c.playerThieving; 
						if(objectX == 3028 && objectY == 4849)
							object[4] = c.playerAgility; 
						if(objectX == 3038 && objectY == 4853)
							object[5] = -1;
						if(objectX == 3049 && objectY == 4849)
							object[6] = c.playerAgility;
						if(objectX == 3058 && objectY == 4839)
							object[7] = c.playerThieving;
						if(objectX == 3060 && objectY == 4830)
							object[8] = c.playerFiremaking;
						if(objectX == 3057 && objectY == 4821)
							object[9] = c.playerWoodcutting;
						if(objectX == 3049 && objectY == 4813)
							object[10] = c.playerMining;
						if(objectX == 3041 && objectY == 4811)
							object[11] = 65535;
					} else if(abyssObjectConfig == 1){
						
						if(objectX == 3026 && objectY == 4813)
							object[0] = 65535;
						if(objectX == 3018 && objectY == 4821)
							object[1] = c.playerMining;
						if(objectX == 3018 && objectY == 4833)
							object[2] = c.playerWoodcutting;
						if(objectX == 3021 && objectY == 4842)
							object[3] = c.playerFiremaking; 
						if(objectX == 3028 && objectY == 4849)
							object[4] = c.playerThieving; 
						if(objectX == 3038 && objectY == 4853)
							object[5] = c.playerAgility;
						if(objectX == 3049 && objectY == 4849)
							object[6] = -1;
						if(objectX == 3058 && objectY == 4839)
							object[7] = c.playerAgility;
						if(objectX == 3060 && objectY == 4830)
							object[8] = c.playerThieving;
						if(objectX == 3057 && objectY == 4821)
							object[9] = c.playerFiremaking;
						if(objectX == 3049 && objectY == 4813)
							object[10] = c.playerWoodcutting;
						if(objectX == 3041 && objectY == 4811)
							object[11] = c.playerMining;
					} else if(abyssObjectConfig == 2){
						if(objectX == 3026 && objectY == 4813)
							object[0] = c.playerMining;
						if(objectX == 3018 && objectY == 4821)
							object[1] = 65535;
						if(objectX == 3018 && objectY == 4833)
							object[2] = c.playerMining;
						if(objectX == 3021 && objectY == 4842)
							object[3] = c.playerWoodcutting; 
						if(objectX == 3028 && objectY == 4849)
							object[4] = c.playerFiremaking; 
						if(objectX == 3038 && objectY == 4853)
							object[5] = c.playerThieving;
						if(objectX == 3049 && objectY == 4849)
							object[6] = c.playerAgility;
						if(objectX == 3058 && objectY == 4839)
							object[7] = -1;
						if(objectX == 3060 && objectY == 4830)
							object[8] = c.playerAgility;
						if(objectX == 3057 && objectY == 4821)
							object[9] = c.playerThieving;
						if(objectX == 3049 && objectY == 4813)
							object[10] = c.playerFiremaking;
						if(objectX == 3041 && objectY == 4811)
							object[11] = c.playerWoodcutting;
						
					}
				}
			
			

	}
