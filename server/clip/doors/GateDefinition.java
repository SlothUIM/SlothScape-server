package server.clip.doors;

import server.clip.WorldObject;
import server.world.Location;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GateDefinition {

    private WorldObject closed1;
    private WorldObject closed2;
    private WorldObject open1;
    private WorldObject open2;

    // Maps both physical locations to the same GateDefinition object
    public static final Map<Location, GateDefinition> definitions = new HashMap<>();

    public WorldObject getClosed1() { return closed1; }
    public WorldObject getClosed2() { return closed2; }
    public WorldObject getOpen1() { return open1; }
    public WorldObject getOpen2() { return open2; }

    public static void load() {
        definitions.clear();
        try (FileReader fr = new FileReader("./Data/json/gate_definitions.json")) {
            List<GateDefinition> list = new Gson().fromJson(fr, new TypeToken<List<GateDefinition>>(){}.getType());
            for (GateDefinition def : list) {
                // Map both CLOSED positions
                definitions.put(def.getClosed1().getLocation(), def);
                definitions.put(def.getClosed2().getLocation(), def);
                
                // Map both OPEN positions (where the gates swing to)
                definitions.put(def.getOpen1().getLocation(), def);
                definitions.put(def.getOpen2().getLocation(), def);
            }
            System.out.println("Loaded " + list.size() + " gate definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GateDefinition forCoordinate(Location loc) {
        return definitions.get(loc);
    }
}