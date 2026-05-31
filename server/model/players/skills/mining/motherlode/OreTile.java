package server.model.players.skills.mining.motherlode;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import server.clip.WorldObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.objects.Object;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.util.Misc;
import server.world.World;
import server.world.objects.GlobalObject;

@Slf4j @Getter
public class OreTile {
    public int x, y, face;
    public boolean occupied = false; // occupied by a vein
    public int objectId;             // current object ID (ore or depleted)
    public int respawnTimer = 0;  
    public VeinGroup veinGroup; // null if empty

    public OreTile(int x, int y, int face) {
        this.x = x;
        this.y = y;
        this.face = face;
        this.objectId = getDepletedId(this.objectId); // default depleted
    }
    public OreTile() {
    	
    }

    private int getDepletionTicks(Player player) {
        // Convert seconds to ticks (OSRS = 0.6s/tick, adjust for your server)
        int seconds;
        if (isUpperLevel(player)) {
            seconds = Misc.random(56, 60);
        } else {
            seconds = Misc.random(36, 40);
        }
        return seconds * 4; // assuming 1 tick = 0.6s ~ 3 server ticks/sec
    }
    private boolean isUpperLevel(Player player) {
		// TODO Auto-generated method stub
		return player.MLMUpperOrLower;
	}
	public void mine(Player player) {
        // Already depleted? stop
        if (objectId == getDepletedId(objectId)) {
            player.sendMessage("This vein is empty.");
            return;
        }

        // Already mining? stop
        if (player.isMining) {
            return;
        }

        // Set a timer to track depletion
        int depletionTicks = getDepletionTicks(player); // OSRS-style: 23-27 or 36-40 sec
        player.isMining = true;
        player.sendMessage("You swing your pick at the rock...");
        player.turnPlayerTo(x, y);
        CycleEventHandler.getSingleton().addEvent(5325, player, new CycleEvent() {
            int ticks = 0;

            @Override
            public void execute(CycleEventContainer container) {
                // If player stops or moves, cancel
                if (player == null || player.disconnected || objectId == getDepletedId(objectId) || player.distanceToPoint(x, y) > 2) {
                    player.isMining = false;
                    container.stop();
                    return;
                }

                // Repeat animation every ~3 ticks
                if (ticks % 4 == 0) {
                    player.startAnimation(getPickAxeAnimation(player.playerEquipment[player.playerWeapon]));
                }
                OreStack ore = MotherlodeMine.rollPayDirtOre(player);

                // Every 4 ticks, give pay-dirt
                if (Misc.random(2) == 0) {
                	if (ticks % 4 == 0) {
                	    // Give pay-dirt to inventory
                	    if (!player.getItems().addItem(12011, 1)) {
                	        player.sendMessage("Your inventory is too full to hold more pay-dirt.");
                	        player.isMining = false;
                	        container.stop();
                	        return;
                	    }
                	    if (ore != null) {
                	    	player.payDirtPending.add(ore);
                	    	player.sendMessage("You manage to mine some pay-dirt.");
                	    	player.getSkills().addExperience(60, Skill.MINING);
                        	respawnTimer = Misc.random(56, 60);
                	    }

                	    
                	    // Chance to deplete
                	    // Start depletion timer after first pay-dirt
                        if (depletionTicks < 1) {
                                    //player.isMining = false;
                                    container.stop();
                              }
                	}

                }

                ticks++;
            }


			@Override
            public void stop() {
                player.isMining = false;
                player.startAnimation(-1);
                                    deplete(); // set depleted ID
            }
        }, 1); // Runs every tick
    }
	//8337 dragon harpoon, crystal harpoon
            private int getPickAxeAnimation(int playerWeapon) {
				// TODO Auto-generated method stub
            	switch(playerWeapon) {
            	case 1265://bronze
            	case 12297://black
            		return 625;
            	case 1267://iron
            		return 626;
            	case 1269://steel
            		return 627;
            	case 1271://addy
            		return 628;
            	case 1273://mith
            		return 629;
            	case 1275://rune
            		return 624;
            	case 23276://gilded
            		return 8358;
            	case 11920://dragon normal
            		return 7139;
            	case 23677://dragon(or)
            		return 8361;
            	case 12797://dragon variant
            		return 8360;
            	case 13243: //infernal
            		return 8362;
            	case 20014://third-age
            		return 8363;
            	case 23680://crystal
            		return 8364;
            	case 23822://corrupted crystal
            		return 8364;
            	}
				return 8364;
			}
    public void deplete() {
        objectId = getDepletedId(objectId);
        respawnTimer = 75; // Or however long you want
        occupied = false;   // mark vein as unavailable
        MotherlodeMine.updateAllVeinVisuals();
    }


    public void process() {
        if (respawnTimer > 0) {
            respawnTimer--;

            if (respawnTimer <= 0) {
                // Move this vein to a new free tile
                MotherlodeMine.moveVeinToNewSpot(this);
            }
        }
    }

    public int getDepletedId(int id) {
        switch(id) {
            case 26661: return 26665;
            case 26662: return 26666;
            case 26663: return 26667;
            case 26664: return 26668;
            default: return 26656;
        }
    }

    public int getActiveId(int id) {
        switch(id) {
            case 26665: return 26661;
            case 26666: return 26662;
            case 26667: return 26663;
            case 26668: return 26664;
            default: return 26656;
        }
    }

    public void updateObject() {
        // Type 0 is for Wall Objects (which MLM veins are)
        int type = 0;
        int height = 0;

        // 1. Remove the old vein/depleted rock
        GlobalObject removeVein = new GlobalObject(-1, x, y, height, face, type, -1);

        // 2. Spawn the new active vein or depleted rock
        GlobalObject newVein = new GlobalObject(objectId, x, y, height, face, type, -1);

        // 3. Push to the global queue
        World.getWorld().getGlobalObjects().add(removeVein);
        World.getWorld().getGlobalObjects().add(newVein);
    }

	public int getRespawnTicks() {
		// TODO Auto-generated method stub
		return respawnTimer;
	}
}
