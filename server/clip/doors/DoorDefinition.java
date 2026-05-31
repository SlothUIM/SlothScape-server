package server.clip.doors;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import server.world.Location;

// DoorDefinition, now supporting both single and open/closed pair
public class DoorDefinition {
    private int id;
    private int x;
    private int y;
    private int h;
    private int face;

    // Optional: open variant for richer definitions
    private DoorDefinition openDef; // can be null

    // --- Getters and setters ---

    public DoorDefinition getOpenDef() { return openDef; }
    public void setOpenDef(DoorDefinition openDef) { this.openDef = openDef; }

    public int getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getH() { return h; }
    public int getFace() { return face; }
    public Location getCoordinate() { return new Location(x, y, h); }

    // --- Constructors ---

    // Legacy/simple
    public DoorDefinition(int id, int x, int y, int h, int face) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.h = h;
        this.face = face;
    }

    // For GSON
    public DoorDefinition() {}

    // --- Loading logic ---

    public static Map<Location, DoorDefinition> definitions = new HashMap<>();

    public static void load() {
        definitions.clear();
        try (FileReader fr = new FileReader("./Data/json/door_definitions.json")) {
            Gson gson = new Gson();

            // Try to detect if it's an array of pairs (closed+open) or legacy list
            List<JsonObject> rawList = gson.fromJson(fr, new TypeToken<List<JsonObject>>(){}.getType());
            for (JsonObject obj : rawList) {
                if (obj.has("closed") && obj.has("open")) {
                    // New format: door pair
                    DoorDefinition closed = gson.fromJson(obj.get("closed"), DoorDefinition.class);
                    DoorDefinition open = gson.fromJson(obj.get("open"), DoorDefinition.class);
                    closed.setOpenDef(open);
                    definitions.put(closed.getCoordinate(), closed);
                } else if (obj.has("id")) {
                    // Legacy format: single door
                    DoorDefinition def = gson.fromJson(obj, DoorDefinition.class);
                    definitions.put(def.getCoordinate(), def);
                }
            }
            System.out.println("Loaded " + definitions.size() + " door definitions");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DoorDefinition forCoordinate(Location coordinate) {
        return definitions.get(coordinate);
    }

	@Override
	public String toString() {
		return "DoorDefinition [id=" + id + ", x=" + x + ", y=" + y + ", h=" + h + ", face=" + face + "]";
	}

	public void setId(int id2) {
		this.id = id2;
		
	}


}
