package server.model.players.content.Stronghold;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.awt.Point;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.Player;
import server.model.players.skills.agility.AgilityHandler;
import server.util.Misc;

public class StrongHold {

	Player player;
	public StrongHold(Player player) {
		// TODO Auto-generated constructor stub
		this.player = player;
	}
	public void doorDialogues(int dialogue, int npcId2) {
		int npcId = npcId2;
		// TODO Auto-generated method stub
		switch(dialogue) {
		case 24941:
			player.getDH().npcChat(npcId, "Gate of War", 4281, "Greetings Adventurer. This place is kept safe by the", "spirits within the doors. as you pass through you will be", "asked questions about security. Hopefully you will learn", "much from us.");
			//player.dialogueId = 24941;
			player.nextChat = 24942;

			break;
		case 24942:
			player.getDH().npcChat(npcId, "Gate of War", 4281, "Please pass through and begin your adventure, beware","of the various monsters that dwell within");
			//player.dialogueId = 24942;
			player.nextChat = 24943;

			break;
		case 24943:
			AgilityHandler.delayEmote(player, "OPEN", 1859, 5238, 0, 2);
			//player.getDH().NPCChat(npcId, "Gate of War", 4281, "Please pass through and begin your adventure, beware","of the various monsters that dwell within");
			player.dialogueId = -1;
			
			break;
			// --- Security Question 1 ---
	    case 30001: // Example question 1
	        player.getDH().npcChat(npcId, "Gate of War", 4281,
	            "Which of these is a good way to keep your account secure?");
	        player.nextChat = 30002; // Used in your button handler!
	        break;
	    case 30002:
	        player.getDH().sendOptions("Use a strong password", "Share your password", "Use '1234' as your password");
	        player.dialogueAction = 30002;
			 break;
	    // --- Security Question 2 ---
	    case 30003:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "If someone claiming to be a Jagex moderator asks for"," your password, what should you do?");
	        player.nextChat = 30004;
	        break;
	    case 30004:

	        player.getDH().sendOptions("Tell them your password", "Never share your password", "Post it on the forums");
	        player.dialogueAction = 30004;
			break;
	    // --- Security Question 3 ---
	    case 30005:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "Which of these is the safest place to enter your password?");
	        player.nextChat = 30006;
	        break;
	    case 30006:
	        player.getDH().sendOptions("Runescape homepage", "A fan site", "Your email provider");
	        player.dialogueAction = 30006;
	        break;

	    // --- Security Question 4 ---
	    case 30007:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "What should you do if you receive an email asking for your account details?");
	        player.nextChat = 30008;
	        break;
	    case 30008:
	        player.getDH().sendOptions("Reply with your details", "Ignore and report it", "Post your details publicly");
	        player.dialogueAction = 30008;
	        break;
	    // --- Security Question 5 ---
	    case 30009:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "What is the best way to recover a lost account?");
	        player.nextChat = 30010;
	        break;
	    case 30010:
	        player.getDH().sendOptions("Use the account recovery system", "Ask on public chat", "Share your password");
	        player.dialogueAction = 30010;
	        break;

	    // --- Security Question 6 ---
	    case 30011:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "How can you make your account harder to guess?");
	        player.nextChat = 30012;
	        break;
	    case 30012:
	        player.getDH().sendOptions("Use personal info", "Use a random password", "Use your username as password");
	        player.dialogueAction = 30012;
	        break;

	    // --- Security Question 7 ---
	    case 30013:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "What should you do if you think your account has been compromised?");
	        player.nextChat = 30014;
	        break;
	    case 30014:
	        player.getDH().sendOptions("Change your password", "Ignore the problem", "Share your password with friends");
	        player.dialogueAction = 30014;
	        break;

	    // --- Security Question 8 ---
	    case 30015:
	        player.getDH().npcChat(npcId, "Security Guardian", 4281,
	            "Why should you keep your password secret?");
	        player.nextChat = 30016;
	        break;
	    case 30016:
	        player.getDH().sendOptions("So only you can access your account", "To let friends play", "It's not important");
	        player.dialogueAction = 30016;
	        break;
	    case 45000:
			player.getDH().sendStatement("You find a book of hand written notes.");
	        player.nextChat = -1;
	    	break;
	    case 23732:
	        player.getDH().sendOptions("Fancy Boots", "Fighting boots");
	        player.dialogueAction = 23732;
	    	break;
		}

	}
	public void strongholdDoorMove(Player c, int destX, int destY) {
	    AgilityHandler.delayEmote(c, "OPEN_STRONGHOLD_DOOR", destX, destY, 0, 2);
	    CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
	        @Override
	        public void execute(CycleEventContainer container) {
	            container.stop();
	        }
	        @Override
	        public void stop() {
	            c.startAnimation(4283);
	            c.stopMovement();
	        }
	    }, 3);
	}
	public static int[] questions = {30001, 30003, 30005, 30007, 30009, 30011, 30013, 30015};
	private StrongholdDoor findDoorByCoords(int x, int y) {
	    // Add all your level door maps here
	    List<Map<Point, StrongholdDoor>> doorMaps = Arrays.asList(LEVEL1_DOORS, LEVEL2_DOORS /*, LEVEL3_DOORS */);
	    return findDoorByCoords(x, y, doorMaps);
	}

	private StrongholdDoor findDoorByCoords(int x, int y, List<Map<Point, StrongholdDoor>> doorMaps) {
	    for (Map<Point, StrongholdDoor> map : doorMaps) {
	        Point p = new Point(x, y);
	        StrongholdDoor door = map.get(p);
	        if (door != null) return door;

	        // Check wide doors (horizontal/vertical)
	        StrongholdDoor adj;
	        adj = map.get(new Point(x + 1, y));
	        if (adj != null && adj.horizontal) return adj;
	        adj = map.get(new Point(x - 1, y));
	        if (adj != null && adj.horizontal) return adj;
	        adj = map.get(new Point(x, y + 1));
	        if (adj != null && !adj.horizontal) return adj;
	        adj = map.get(new Point(x, y - 1));
	        if (adj != null && !adj.horizontal) return adj;
	    }
	    return null;
	}

	public boolean handleStrongholdDoors(Player c, int objectId) {
	    if (objectId == 19207 || objectId == 19206) {
	        StrongholdDoor door = findDoorByCoords(c.getX(), c.getY());
	        if (door != null) {
	            if (door.dialogueId > 0 && Misc.random(10) == 1) {
	                doorDialogues(door.dialogueId, 2494);
	            } else {
	                strongholdDoorMove(c, door.destX, door.destY);
	            }
	            return true;
	        }
	    } 
	    if (objectId == 17100 || objectId == 17009) {
	        StrongholdDoor door = findDoorByCoords(c.getX(), c.getY());
	        if (door != null) {
	            if (door.dialogueId > 0 && Misc.random(10) == 1) {
	                doorDialogues(door.dialogueId, 2494);
	            } else {
	                strongholdDoorMove(c, door.destX, door.destY);
	            }
	            return true;
	        }
	    }
	    if(objectId == 20656) {
	    	if(!c.emoteUnlock[8]) {
		    	c.getItems().addItem(995, 2000);
		    	c.getPA().sendConfig(708, 1);
		    	c.emoteUnlock[8] = true;
		    	c.sendMessage("You've unlocked the @blu@Flap @bla@emote.");
		    	return true;
	    	} else {
	    		c.getDH().sendStatement("You have already claimed your reward from this level.");
		    	return false;
	    	} 
	    }
	    if(objectId == 19000) {
	    	if(!c.emoteUnlock[9]) {
		    	c.getItems().addItem(995, 3000);
		    	c.getPA().sendConfig(709, 1);
		    	c.emoteUnlock[9] = true;
		    	c.sendMessage("You've unlocked the @blu@Slap-head @bla@emote.");
		    	return true;
	    	} else {
	    		c.getDH().sendStatement("You have already claimed your reward from this level.");
		    	return false;
	    	} 
	    }
	    if(objectId == 23709) {
	    	if(!c.emoteUnlock[6]) {
		    	c.getItems().addItem(995, 5000);
		    	c.getPA().sendConfig(706, 1);
		    	c.getSkills().sendRefresh();
		    	c.emoteUnlock[6] = true;
		    	c.sendMessage("You've unlocked the @blu@Bright-Idea @bla@emote.");
		    	return true;
	    	} else {
	    		c.getDH().sendStatement("You have already claimed your reward from this level.");
		    	return false;
	    	} 
	    }
	    if(objectId == 23731) {
	    		c.getDH().sendOption2_withTitle("Accept","Decline", "You hear the voices offering you more boots!");
	    		c.dialogueAction = 23731;
		    	c.getPA().sendConfig(707, 1);
		    	if(!c.emoteUnlock[7]) {
		    		c.emoteUnlock[7] = true;
		    		c.sendMessage("You've unlocked the @blu@Stomp @bla@emote.");
		    	}
		    	return true;
	    }
	    if(objectId == 20782) {
			if(c.objectX == 1881 && c.objectY == 5232) {
				AgilityHandler.delayEmote(c, "CLIMB_UP", 1860, 5244, 0, 2);
	    		c.sendMessage("You climb up the chain very very carefully, squeeze through a passage then climb a");
	    		c.sendMessage("ladder.");
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
			        @Override
			        public void execute(CycleEventContainer container) {
			            container.stop();
			        }
			        @Override
			        public void stop() {
			        	c.sendMessage("You climb up the ladder which seems to twist and wind in all directions.");
			        }
			    }, 2);
			}
	    	return true;
	    }
	    return false;
	}
	private static final Map<Point, StrongholdDoor> LEVEL1_DOORS = new HashMap<>();
	static {
	    // Fill this list with all your first-level doors and their logic!
	    LEVEL1_DOORS.put(new Point(1859, 5239), new StrongholdDoor(1859, 5239, 24941, 1859, 5238, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1859, 5238), new StrongholdDoor(1859, 5238, -1, 1859, 5239, true));    // exit (move only)
	    // Add more doors as needed
	    LEVEL1_DOORS.put(new Point(1859, 5236), new StrongholdDoor(1859, 5236, questions[Misc.random(questions.length-1)], 1859, 5235, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1859, 5235), new StrongholdDoor(1859, 5235, -1, 1859, 5236, true));    // exit (move only)

	    LEVEL1_DOORS.put(new Point(1865, 5227), new StrongholdDoor(1865, 5227, questions[Misc.random(questions.length-1)], 1864, 5227, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1864, 5227), new StrongholdDoor(1864, 5227, -1, 1865, 5227, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1867, 5227), new StrongholdDoor(1867, 5227, questions[Misc.random(questions.length-1)], 1868, 5227, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1868, 5227), new StrongholdDoor(1868, 5227, -1, 1867, 5227, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1875, 5240), new StrongholdDoor(1875, 5240, questions[Misc.random(questions.length-1)], 1876, 5240, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1876, 5240), new StrongholdDoor(1876, 5240, -1, 1875, 5240, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1878, 5240), new StrongholdDoor(1878, 5240, questions[Misc.random(questions.length-1)], 1879, 5240, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1879, 5240), new StrongholdDoor(1879, 5240, -1, 1878, 5240, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1883, 5244), new StrongholdDoor(1883, 5244, questions[Misc.random(questions.length-1)], 1884, 5244, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1884, 5244), new StrongholdDoor(1884, 5244, -1, 1883, 5244, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1886, 5244), new StrongholdDoor(1886, 5244, questions[Misc.random(questions.length-1)], 1887, 5244, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1887, 5244), new StrongholdDoor(1887, 5244, -1, 1886, 5244, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1885, 5236), new StrongholdDoor(1885, 5236, questions[Misc.random(questions.length-1)], 1886, 5236, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1886, 5236), new StrongholdDoor(1886, 5236, -1, 1885, 5236, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1888, 5236), new StrongholdDoor(1888, 5236, questions[Misc.random(questions.length-1)], 1889, 5236, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1889, 5236), new StrongholdDoor(1889, 5236, -1, 1888, 5236, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1903, 5243), new StrongholdDoor(1903, 5243, questions[Misc.random(questions.length-1)], 1904, 5243, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1904, 5243), new StrongholdDoor(1904, 5243, -1, 1903, 5243, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1907, 5243), new StrongholdDoor(1907, 5243, questions[Misc.random(questions.length-1)], 1908, 5243, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1908, 5243), new StrongholdDoor(1908, 5243, -1, 1907, 5243, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1904, 5233), new StrongholdDoor(1904, 5233, questions[Misc.random(questions.length-1)], 1904, 5234, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1904, 5234), new StrongholdDoor(1904, 5234, -1, 1904, 5233, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1905, 5230), new StrongholdDoor(1905, 5230, questions[Misc.random(questions.length-1)], 1905, 5231, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1905, 5231), new StrongholdDoor(1905, 5231, -1, 1905, 5230, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1912, 5210), new StrongholdDoor(1912, 5210, questions[Misc.random(questions.length-1)], 1912, 5209, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1912, 5209), new StrongholdDoor(1912, 5209, -1, 1912, 5210, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1912, 5207), new StrongholdDoor(1912, 5207, questions[Misc.random(questions.length-1)], 1912, 5206, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1912, 5206), new StrongholdDoor(1912, 5206, -1, 1912, 5207, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1906, 5204), new StrongholdDoor(1906, 5204, questions[Misc.random(questions.length-1)], 1907, 5204, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1907, 5204), new StrongholdDoor(1907, 5204, -1, 1906, 5204, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1904, 5204), new StrongholdDoor(1904, 5204, questions[Misc.random(questions.length-1)], 1903, 5204, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1903, 5204), new StrongholdDoor(1903, 5204, -1, 1904, 5204, false));  // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1881, 5189), new StrongholdDoor(1881, 5189, questions[Misc.random(questions.length-1)], 1882, 5189, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1882, 5189), new StrongholdDoor(1882, 5189, -1, 1881, 5189, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1879, 5189), new StrongholdDoor(1879, 5189, questions[Misc.random(questions.length-1)], 1878, 5189, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1878, 5189), new StrongholdDoor(1878, 5189, -1, 1879, 5189, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1877, 5192), new StrongholdDoor(1877, 5192, questions[Misc.random(questions.length-1)], 1877, 5191, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1877, 5191), new StrongholdDoor(1877, 5191, -1, 1877, 5192, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1877, 5194), new StrongholdDoor(1877, 5194, questions[Misc.random(questions.length-1)], 1877, 5195, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1877, 5195), new StrongholdDoor(1877, 5195, -1, 1877, 5194, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1875, 5205), new StrongholdDoor(1875, 5205, questions[Misc.random(questions.length-1)], 1875, 5204, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1875, 5204), new StrongholdDoor(1875, 5204, -1, 1875, 5205, true));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1875, 5207), new StrongholdDoor(1875, 5207, questions[Misc.random(questions.length-1)], 1875, 5208, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1875, 5208), new StrongholdDoor(1875, 5208, -1, 1875, 5207, true));   // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1869, 5217), new StrongholdDoor(1869, 5217, questions[Misc.random(questions.length-1)], 1870, 5217, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1870, 5217), new StrongholdDoor(1870, 5217, -1, 1869, 5217, false));  
	    
	    LEVEL1_DOORS.put(new Point(1867, 5217), new StrongholdDoor(1867, 5217, questions[Misc.random(questions.length-1)], 1866, 5217, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1866, 5217), new StrongholdDoor(1866, 5217, -1, 1867, 5217, false));    // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1878, 5223), new StrongholdDoor(1878, 5223, questions[Misc.random(questions.length-1)], 1878, 5222, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1878, 5222), new StrongholdDoor(1878, 5222, -1, 1878, 5223, true));   // exit (move only)
	    
	    LEVEL1_DOORS.put(new Point(1878, 5225), new StrongholdDoor(1878, 5225, questions[Misc.random(questions.length-1)], 1878, 5226, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1878, 5226), new StrongholdDoor(1878, 5226, -1, 1878, 5225, true)); 
	    
	    LEVEL1_DOORS.put(new Point(1861, 5212), new StrongholdDoor(1861, 5212, questions[Misc.random(questions.length-1)], 1861, 5213, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1861, 5213), new StrongholdDoor(1861, 5213, -1, 1861, 5212, true)); 
	    
	    LEVEL1_DOORS.put(new Point(1861, 5210), new StrongholdDoor(1861, 5210, questions[Misc.random(questions.length-1)], 1861, 5209, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1861, 5209), new StrongholdDoor(1861, 5209, -1, 1861, 5210, true)); 
	    
	    LEVEL1_DOORS.put(new Point(1861, 5198), new StrongholdDoor(1861, 5198, questions[Misc.random(questions.length-1)], 1861, 5199, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1861, 5199), new StrongholdDoor(1861, 5199, -1, 1861, 5198, true)); 

	    LEVEL1_DOORS.put(new Point(1861, 5196), new StrongholdDoor(1861, 5196, questions[Misc.random(questions.length-1)], 1861, 5195, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1861, 5195), new StrongholdDoor(1861, 5195, -1, 1861, 5196, true)); 

	    LEVEL1_DOORS.put(new Point(1890, 5208), new StrongholdDoor(1890, 5208, questions[Misc.random(questions.length-1)], 1890, 5207, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1890, 5207), new StrongholdDoor(1890, 5207, -1, 1890, 5208, true)); 

	    LEVEL1_DOORS.put(new Point(1890, 5211), new StrongholdDoor(1890, 5211, questions[Misc.random(questions.length-1)], 1890, 5212, true)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1890, 5212), new StrongholdDoor(1890, 5212, -1, 1890, 5211, true)); 

	    LEVEL1_DOORS.put(new Point(1894, 5213), new StrongholdDoor(1894, 5213, questions[Misc.random(questions.length-1)], 1893, 5213, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1893, 5213), new StrongholdDoor(1893, 5213, -1, 1894, 5213, false)); 

	    LEVEL1_DOORS.put(new Point(1896, 5213), new StrongholdDoor(1896, 5213, questions[Misc.random(questions.length-1)], 1897, 5213, false)); // entrance (dialogue)
	    LEVEL1_DOORS.put(new Point(1897, 5213), new StrongholdDoor(1897, 5213, -1, 1896, 5213, false)); 
	}
	private static final Map<Point, StrongholdDoor> LEVEL2_DOORS = new HashMap<>();
	static {
	    // Fill this list with all your second-level doors and their logic!
		LEVEL2_DOORS.put(new Point(2039, 5245), new StrongholdDoor(2039, 5245, -1, 2040, 5245, false)); // entrance (dialogue)
		LEVEL2_DOORS.put(new Point(2040, 5245), new StrongholdDoor(2040, 5245, -1, 2039, 5245, false));    // exit (move only)
	    
		LEVEL2_DOORS.put(new Point(2037, 5245), new StrongholdDoor(2037, 5245, -1, 2036, 5245, false)); // entrance (dialogue)
		LEVEL2_DOORS.put(new Point(2036, 5245), new StrongholdDoor(2036, 5245, -1, 2037, 5245, false));    // exit (move only)

	    LEVEL2_DOORS.put(new Point(2026, 5239), new StrongholdDoor(2026, 5239, -1, 2026, 5238, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2026, 5238), new StrongholdDoor(2026, 5238, -1, 2026, 5239, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2026, 5241), new StrongholdDoor(2026, 5241, -1, 2026, 5242, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2026, 5242), new StrongholdDoor(2026, 5242, -1, 2026, 5241, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2019, 5242), new StrongholdDoor(2019, 5242, -1, 2019, 5243, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2019, 5243), new StrongholdDoor(2019, 5243, -1, 2019, 5242, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2019, 5240), new StrongholdDoor(2019, 5240, -1, 2019, 5239, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2019, 5239), new StrongholdDoor(2019, 5239, -1, 2019, 5240, true));    // exit (move only)
	    /*
	     * 8/22/2025 progress ^
	     */
	    LEVEL2_DOORS.put(new Point(2044, 5239), new StrongholdDoor(2044, 5239, questions[Misc.random(questions.length-1)], 2044, 5240, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2044, 5240), new StrongholdDoor(2044, 5240, -1, 2044, 5239, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2044, 5237), new StrongholdDoor(2044, 5237, questions[Misc.random(questions.length-1)], 2044, 5236, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2044, 5236), new StrongholdDoor(2044, 5236, -1, 2044, 5237, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2042, 5223), new StrongholdDoor(2042, 5223, questions[Misc.random(questions.length-1)], 2043, 5223, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2043, 5223), new StrongholdDoor(2043, 5223, -1, 2042, 5223, false));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2040, 5223), new StrongholdDoor(2040, 5223, questions[Misc.random(questions.length-1)], 2039, 5223, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2039, 5223), new StrongholdDoor(2039, 5223, -1, 2040, 5223, false));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2031, 5225), new StrongholdDoor(2031, 5225, questions[Misc.random(questions.length-1)], 2031, 5224, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2031, 5224), new StrongholdDoor(2031, 5224, -1, 2031, 5225, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2031, 5227), new StrongholdDoor(2031, 5227, questions[Misc.random(questions.length-1)], 2031, 5228, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2031, 5228), new StrongholdDoor(2031, 5228, -1, 2031, 5227, true));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2013, 5240), new StrongholdDoor(2013, 5240, questions[Misc.random(questions.length-1)], 2013, 5239, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2013, 5239), new StrongholdDoor(2013, 5239, -1, 2013, 5240, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2013, 5242), new StrongholdDoor(2013, 5242, questions[Misc.random(questions.length-1)], 2013, 5243, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2013, 5243), new StrongholdDoor(2013, 5243, -1, 2013, 5242, true));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2018, 5228), new StrongholdDoor(2018, 5228, questions[Misc.random(questions.length-1)], 2019, 5228, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2019, 5228), new StrongholdDoor(2019, 5228, -1, 2018, 5228, false));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2016, 5228), new StrongholdDoor(2016, 5228, questions[Misc.random(questions.length-1)], 2015, 5228, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2015, 5228), new StrongholdDoor(2015, 5228, -1, 2016, 5228, false));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2005, 5235), new StrongholdDoor(2005, 5235, questions[Misc.random(questions.length-1)], 2005, 5234, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2005, 5234), new StrongholdDoor(2005, 5234, -1, 2005, 5235, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2005, 5237), new StrongholdDoor(2005, 5237, questions[Misc.random(questions.length-1)], 2005, 5238, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2005, 5238), new StrongholdDoor(2005, 5238, -1, 2005, 5237, true));  // exit (move only)
	    /*
	     * >>>
	     */
	    LEVEL2_DOORS.put(new Point(2008, 5216), new StrongholdDoor(2008, 5216, questions[Misc.random(questions.length-1)], 2009, 5216, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2009, 5216), new StrongholdDoor(2009, 5216, -1, 2008, 5216, false));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2006, 5216), new StrongholdDoor(2006, 5216, questions[Misc.random(questions.length-1)], 2005, 5216, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2005, 5216), new StrongholdDoor(2005, 5216, -1, 2006, 5216, false));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(1999, 5216), new StrongholdDoor(1999, 5216, questions[Misc.random(questions.length-1)], 2000, 5216, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2000, 5216), new StrongholdDoor(2000, 5216, -1, 1999, 5216, false));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(1997, 5216), new StrongholdDoor(1997, 5216, questions[Misc.random(questions.length-1)], 1996, 5216, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(1996, 5216), new StrongholdDoor(1996, 5216, -1, 1997, 5216, false));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(1995, 5196), new StrongholdDoor(1995, 5196, questions[Misc.random(questions.length-1)], 1995, 5197, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(1995, 5197), new StrongholdDoor(1995, 5197, -1, 1995, 5196, true));    // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(1995, 5194), new StrongholdDoor(1995, 5194, questions[Misc.random(questions.length-1)], 1995, 5193, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(1995, 5193), new StrongholdDoor(1995, 5193, -1, 1995, 5194, true));   // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2005, 5192), new StrongholdDoor(2005, 5192, questions[Misc.random(questions.length-1)], 2005, 5191, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2005, 5191), new StrongholdDoor(2005, 5191, -1, 2005, 5192, true));  
	    
	    LEVEL2_DOORS.put(new Point(2005, 5194), new StrongholdDoor(2005, 5194, questions[Misc.random(questions.length-1)], 1866, 5195, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2005, 5195), new StrongholdDoor(2005, 5195, -1, 2005, 5194, true));    // exit (move only)
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2020, 5200), new StrongholdDoor(2020, 5200, questions[Misc.random(questions.length-1)], 2020, 5199, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2020, 5199), new StrongholdDoor(2020, 5199, -1, 2020, 5200, true));   // exit (move only)
	    
	    LEVEL2_DOORS.put(new Point(2020, 5202), new StrongholdDoor(2020, 5202, questions[Misc.random(questions.length-1)], 2020, 5203, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2020, 5203), new StrongholdDoor(2020, 5203, -1, 2020, 5202, true)); 
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2033, 5208), new StrongholdDoor(2033, 5208, questions[Misc.random(questions.length-1)], 2033, 5207, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2033, 5207), new StrongholdDoor(2033, 5207, -1, 2033, 5208, true)); 
	    
	    LEVEL2_DOORS.put(new Point(2033, 5210), new StrongholdDoor(2033, 5210, questions[Misc.random(questions.length-1)], 2033, 5211, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2033, 5211), new StrongholdDoor(2033, 5211, -1, 2033, 5210, true)); 
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2037, 5201), new StrongholdDoor(2037, 5201, questions[Misc.random(questions.length-1)], 2037, 5200, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2037, 5200), new StrongholdDoor(2037, 5200, -1, 2037, 5201, true)); 

	    LEVEL2_DOORS.put(new Point(2037, 5203), new StrongholdDoor(2037, 5203, questions[Misc.random(questions.length-1)], 2037, 5204, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2037, 5204), new StrongholdDoor(2037, 5204, -1, 2037, 5203, true)); 
		/*
		 * 
		 */
	    LEVEL2_DOORS.put(new Point(2046, 5197), new StrongholdDoor(2046, 5197, questions[Misc.random(questions.length-1)], 2046, 5198, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2046, 5198), new StrongholdDoor(2046, 5198, -1, 2046, 5197, true)); 

	    LEVEL2_DOORS.put(new Point(2046, 5195), new StrongholdDoor(2046, 5195, questions[Misc.random(questions.length-1)], 2046, 5194, true)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2046, 5194), new StrongholdDoor(2046, 5194, -1, 2046, 5195, true)); 
	    /*
	     * 
	     */
	    LEVEL2_DOORS.put(new Point(2036, 5186), new StrongholdDoor(2036, 5186, questions[Misc.random(questions.length-1)], 2037, 5186, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2037, 5186), new StrongholdDoor(2037, 5186, -1, 2036, 5186, false)); 

	    LEVEL2_DOORS.put(new Point(2034, 5186), new StrongholdDoor(2034, 5186, questions[Misc.random(questions.length-1)], 2033, 5186, false)); // entrance (dialogue)
	    LEVEL2_DOORS.put(new Point(2033, 5186), new StrongholdDoor(2033, 5186, -1, 2034, 5186, false)); 
	}
	public boolean dialogOption(Player c, int actionButtonId) {
	    // Map of dialogueAction to correct buttonId
	    Map<Integer, Integer> correctAnswers = new HashMap<>();
	    correctAnswers.put(30002, 9167); // Q1: first option
	    correctAnswers.put(30004, 9168); // Q2: second option
	    correctAnswers.put(30006, 9167); // Q3: first option
	    correctAnswers.put(30008, 9168); // Q4: second option
	    correctAnswers.put(30010, 9167); // Q5: first option
	    correctAnswers.put(30012, 9168); // Q6: second option
	    correctAnswers.put(30014, 9167); // Q7: first option
	    correctAnswers.put(30016, 9168); // Q8: first option

	    Integer correctButton = correctAnswers.get(c.dialogueAction);
	    if (correctButton != null) {
	        if (actionButtonId == correctButton) {
	            // Move player through the door
	    	    c.sendMessage("Correct! You may proceed.");
	            movePlayerThroughDoor(c);
	        } else {
	            c.sendMessage("Incorrect. Try again!");
	        }
	        return true;
	    }
	    if(c.dialogueAction == 23731 && actionButtonId == 9157) {
	    	doorDialogues(23732, -1);
	    	return true;
	    }
	    if(c.dialogueAction == 23732 && actionButtonId == 9157) {
	    	c.getItems().addItem(9005, 1);
	    	c.nextChat = -1;
	    	c.getPA().removeAllWindows();
	    	return true;
	    }
	    if(c.dialogueAction == 23732 && actionButtonId == 9158) {
	    	c.getItems().addItem(9006, 1);
	    	c.nextChat = -1;
	    	c.getPA().removeAllWindows();
	    	return true;
	    }
	    return false;
	}
	private void movePlayerThroughDoor(Player c) {
	    if (c.getX() < c.objectX && c.getY() == c.objectY) {
	        c.getSecurity().strongholdDoorMove(c, c.getX()+1, c.getY());
	    } else if (c.getX() >= c.objectX && c.getY() == c.objectY) {
	        c.getSecurity().strongholdDoorMove(c, c.getX()-1, c.getY());
	    } else if (c.getY() >= c.objectY && c.getX() == c.objectX) {
	        c.getSecurity().strongholdDoorMove(c, c.getX(), c.getY()-1);
	    } else if (c.getY() < c.objectY && c.getX() == c.objectX) {
	        c.getSecurity().strongholdDoorMove(c, c.getX(), c.getY()+1);
	    }
	}
}
