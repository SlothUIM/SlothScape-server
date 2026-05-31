package server.clip.doors;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.world.Location;

public class DoubleDoorDefinition {

    private DoorDefinition closed1;
    private DoorDefinition closed2;
    private DoorDefinition open1;
    private DoorDefinition open2;

    // Map both physical locations to the same DoubleDoor object
    public static Map<Location, DoubleDoorDefinition> definitions = new HashMap<>();

    public DoorDefinition getClosed1() { return closed1; }
    public DoorDefinition getClosed2() { return closed2; }
    public DoorDefinition getOpen1() { return open1; }
    public DoorDefinition getOpen2() { return open2; }

    public static void load() {
        definitions.clear();
        try (FileReader fr = new FileReader("./Data/json/double_doors.json")) {
            List<DoubleDoorDefinition> list = new Gson().fromJson(fr, new TypeToken<List<DoubleDoorDefinition>>(){}.getType());
            for (DoubleDoorDefinition def : list) {
                // Map both CLOSED positions
                definitions.put(def.getClosed1().getCoordinate(), def);
                definitions.put(def.getClosed2().getCoordinate(), def);
                
                // Map both OPEN positions (where the doors swing to)
                definitions.put(def.getOpen1().getCoordinate(), def);
                definitions.put(def.getOpen2().getCoordinate(), def);
            }
            System.out.println("Loaded " + list.size() + " double door definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DoubleDoorDefinition forCoordinate(Location loc) {
        return definitions.get(loc);
    }
}