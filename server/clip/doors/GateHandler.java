package server.clip.doors;

import java.util.Map;
import java.util.HashMap;

import server.model.players.Player;
import server.model.players.Sound;
import server.clip.WorldObject;
import server.world.Location;
import server.world.World;
import server.world.objects.GlobalObject;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

public class GateHandler {

    private static final Map<Location, GateDefinition> openGates = new HashMap<>();
    private static final Map<Location, GateDefinition> openByLocation = new HashMap<>();
    private static final Map<DoorKey, GateDefinition> openByKey = new HashMap<>();
    private static volatile boolean indexBuilt = false;

    private static void ensureIndex() {
        if (indexBuilt) return;
        rebuildOpenIndex();
    }

    public static void rebuildOpenIndex() {
        openByLocation.clear();
        openByKey.clear();

        for (GateDefinition def : GateDefinition.definitions.values()) {
            indexGate(def, def.getOpen1());
            indexGate(def, def.getOpen2());
        }
        indexBuilt = true;
    }

    private static void indexGate(GateDefinition def, WorldObject openHalf) {
        openByLocation.put(openHalf.getLocation(), def);
        openByKey.put(new DoorKey(openHalf.getId(), openHalf.getX(), openHalf.getY(), openHalf.getHeight()), def);
    }

    public static GateDefinition findGateAt(int x, int y, int height, int id) {
        ensureIndex();
        Location loc = new Location(x, y, height);

        GateDefinition closed = GateDefinition.forCoordinate(loc);
        if (closed != null && (id == closed.getClosed1().getId() || id == closed.getClosed2().getId())) {
            return closed;
        }

        GateDefinition closedFromOpen = openByKey.get(new DoorKey(id, x, y, height));
        if (closedFromOpen != null) {
            return closedFromOpen;
        }

        return openByLocation.get(loc);
    }

    public static boolean clickGate(Player player, WorldObject object) {
        ensureIndex();
        Location clickedLoc = new Location(object.getX(), object.getY(), player.getHeight());

        if (player.getLocation().getDistance(clickedLoc) >= 2) {
            return false;
        }

        int id = object.id;
        GateDefinition def = findGateAt(clickedLoc.getX(), clickedLoc.getY(), clickedLoc.getZ(), id);
        
        if (def == null) {
            return false; 
        }

        boolean req = isForceWalk(player, id, true);
        int timer = req ? 4 : 124; 

        if (!meetsRequirement(player, id) && !req) {
            player.sendMessage("You do not meet the requirements to open this gate.");
            return false;
        }

        boolean isAlreadyOpen = openGates.containsKey(def.getClosed1().getLocation());

        player.getPA().sendSound(Sound.SOUND_LIST.DOOR.getSound(), 6, player.EffectVolume);

        if (isAlreadyOpen) {
            closeGate(def);
        } else {
            openGate(player, def, timer);
        }

        return true;
    }

    private static void openGate(Player player, GateDefinition def, int timer) {
        GlobalObject remove1 = new GlobalObject(-1, def.getClosed1().getX(), def.getClosed1().getY(), def.getClosed1().getHeight(), def.getClosed1().getFace(), def.getClosed1().getType(), -1, -1);
        GlobalObject remove2 = new GlobalObject(-1, def.getClosed2().getX(), def.getClosed2().getY(), def.getClosed2().getHeight(), def.getClosed2().getFace(), def.getClosed2().getType(), -1, -1);
        GlobalObject open1 = new GlobalObject(def.getOpen1().getId(), def.getOpen1().getX(), def.getOpen1().getY(), def.getOpen1().getHeight(), def.getOpen1().getFace(), def.getOpen1().getType(), -1, def.getClosed1().getId());
        GlobalObject open2 = new GlobalObject(def.getOpen2().getId(), def.getOpen2().getX(), def.getOpen2().getY(), def.getOpen2().getHeight(), def.getOpen2().getFace(), def.getOpen2().getType(), -1, def.getClosed2().getId());
        

        World.getWorld().getGlobalObjects().add(remove1);
        World.getWorld().getGlobalObjects().add(remove2);
        World.getWorld().getGlobalObjects().add(open1);
        World.getWorld().getGlobalObjects().add(open2);

        Location primaryKey = def.getClosed1().getLocation();
        openGates.put(primaryKey, def);

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (openGates.containsKey(primaryKey)) {
                    closeGate(def);
                }
                container.stop();
            }

            @Override
            public void stop() {}
        }, timer);
    }

    private static void closeGate(GateDefinition def) {
        // --- THIS IS THE FIX --- 
        // Actively spawn -1 objects over the OPEN models to visually delete them for the player
        GlobalObject removeOpen1 = new GlobalObject(-1, def.getOpen1().getX(), def.getOpen1().getY(), def.getOpen1().getHeight(), def.getOpen1().getFace(), def.getOpen1().getType(), -1, -1);
        GlobalObject removeOpen2 = new GlobalObject(-1, def.getOpen2().getX(), def.getOpen2().getY(), def.getOpen2().getHeight(), def.getOpen2().getFace(), def.getOpen2().getType(), -1, -1);
        World.getWorld().getGlobalObjects().add(removeOpen1);
        World.getWorld().getGlobalObjects().add(removeOpen2);

        // Respawn the original CLOSED objects
        GlobalObject closed1 = new GlobalObject(def.getClosed1().getId(), def.getClosed1().getX(), def.getClosed1().getY(), def.getClosed1().getHeight(), def.getClosed1().getFace(), def.getClosed1().getType(), -1, -1);
        GlobalObject closed2 = new GlobalObject(def.getClosed2().getId(), def.getClosed2().getX(), def.getClosed2().getY(), def.getClosed2().getHeight(), def.getClosed2().getFace(), def.getClosed2().getType(), -1, -1);
        World.getWorld().getGlobalObjects().add(closed1);
        World.getWorld().getGlobalObjects().add(closed2);

        openGates.remove(def.getClosed1().getLocation());
    }

    public static boolean isForceWalk(Player p, int id, boolean message) {
        return false; 
    }

    public static boolean meetsRequirement(Player p, int id) {
        return true; 
    }
}