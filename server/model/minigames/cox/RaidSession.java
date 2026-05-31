package server.model.minigames.cox;

import lombok.Getter;
import server.model.Entity;
import server.model.instance.Instance;
import server.model.minigames.cox.rooms.ThievingRoom;
import server.model.minigames.raids.RaidParty;
import server.model.npcs.NPC;
import server.model.players.Player;
import server.model.players.skills.construction.util.POHPalette;
import server.model.map.Palette;
import server.model.map.PaletteTile;
import server.world.Location;
import server.clip.Region;
import java.util.ArrayList;
import java.util.List;

public class RaidSession extends Instance {

    private Player leader;
    @Getter
    private List<RaidNode> layoutNodes;
    private final int BASE_CHUNK_X = 160;
    private final int BASE_CHUNK_Y = 656;
    public static final int INSTANCE_BASE_X = 1280;
    public static final int INSTANCE_BASE_Y = 5248;
    private POHPalette.POHPaletteTile[][][] virtualGrid = new POHPalette.POHPaletteTile[4][25][25];

    @Getter
    private RaidParty party;

    // Update the constructor:
    public RaidSession(RaidParty party, List<RaidNode> layoutNodes) {
        super(); // This assigns the unique ID and registers it to the InstanceManager
        this.party = party;
        this.layoutNodes = layoutNodes;
        buildVirtualGrid();
        applyServerClipping(); // <--- ADD THIS HERE!
        populateRooms();
    }

    @Override
    protected void initialize() {
        System.out.println("[CoX] Raid Instance " + getId() + " initialized. Height Level: " + getHeight());
    }
    /**
     * Reverses the coordinate math to find which RaidRoom the player is currently standing in.
     */
    public RaidRoom getRoomAt(int worldX, int worldY) {
        // Translate world coordinates back to the 0-4 grid layout
        int gridX = (worldX - INSTANCE_BASE_X) / 32;
        int gridY = (worldY - INSTANCE_BASE_Y) / 32;

        for (RaidRoom room : activeRooms) {
            if (room.node.getGridX() == gridX && room.node.getGridY() == gridY) {
                return room;
            }
        }
        return null; // Player is in a hallway or start room (which don't have behaviors)
    }
    @Override
    public boolean destroyOnEmpty() {
        // In OSRS, Team raids stay open for a bit, but solo raids die instantly.
        // Returning true means if the instance is empty of players, it deletes itself.
        return true;
    }

    @Override
    protected void onEnter(Entity entity) {
        if (entity.isPlayer()) {
            Player p = entity.asPlayer();
            // Anytime a player enters (or reconnects!), we send them the constructed map.
            refreshRaidMap(p);
        }
    }
    /**
     * Translates local room coordinates (0-31) into absolute world coordinates
     * based on where the room was generated and how it was rotated.
     */
    @Getter
    private List<RaidRoom> activeRooms = new ArrayList<>();
    public int[] getAbsoluteCoordinates(RaidNode node, int localX, int localY) {
        // Find the absolute base of this specific room
        int roomBaseX = INSTANCE_BASE_X + (node.getGridX() * 32);
        int roomBaseY = INSTANCE_BASE_Y + (node.getGridY() * 32);

        int rotatedX = localX;
        int rotatedY = localY;
        int roomSize = node.getRoom().getSize() * 8; // Usually 4 chunks * 8 = 32

        // Apply 2D rotation matrix based on the room's generated rotation
        switch (node.getRotation()) {
            case 0: // North (Default)
                break;
            case 1: // East (Rotated 90 degrees clockwise)
                rotatedX = localY;
                rotatedY = (roomSize - 1) - localX;
                break;
            case 2: // South (Rotated 180 degrees)
                rotatedX = (roomSize - 1) - localX;
                rotatedY = (roomSize - 1) - localY;
                break;
            case 3: // West (Rotated 270 degrees)
                rotatedX = (roomSize - 1) - localY;
                rotatedY = localX;
                break;
        }

        return new int[] { roomBaseX + rotatedX, roomBaseY + rotatedY };
    }
    @Override
    protected void onLeave(Entity entity) {
        if (entity.isPlayer()) {
            Player p = entity.asPlayer();
            // Teleport them to the mountain when they leave the instance
            p.getPA().movePlayer(1232, 3557, 0);
            p.sendMessage("You have left the Chambers of Xeric.");
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("[CoX] Raid Instance " + getId() + " destroyed. Clearing memory...");

        // 1. Despawn any lingering Bosses/NPCs inside the active rooms
        for (RaidRoom room : activeRooms) {
            if (room != null) {
                // If you have a cleanup method in your base RaidRoom class, call it here!
                // e.g., room.cleanup();
            }
        }

        // 2. Clear all heavy collections
        layoutNodes.clear();
        activeRooms.clear();
        storageLocations.clear();

        // 3. Nullify heavy arrays and circular references to allow instant GC
        virtualGrid = null;
        party = null;

        // 4. THE MOST IMPORTANT FIX: Clear the region clipping!
        for (int floor = 0; floor < 4; floor++) {
            int instanceHeight = getHeight() + (floor * 4);
            Location floorBase = new Location(INSTANCE_BASE_X, INSTANCE_BASE_Y, instanceHeight);

            // FIX: Pass floorBase so it unloads height 0, 4, 8, and 12 correctly!
            server.clip.Region.unloadPalette(floorBase, 20, 20);

            // Purge the Region map cache for this specific instance boundary
            // A 20x20 chunk palette covers about 3x3 Regions (64x64 tiles each)
            int startRegionX = INSTANCE_BASE_X >> 6;
            int startRegionY = INSTANCE_BASE_Y >> 6;

            for (int rx = 0; rx <= 3; rx++) {
                for (int ry = 0; ry <= 3; ry++) {
                    int absX = (startRegionX + rx) * 64;
                    int absY = (startRegionY + ry) * 64;
                    server.clip.Region.removeRegion(absX, absY);
                }
            }
        }
    }

    @Override
    public boolean onDeath(Entity entity) {
        if (entity.isPlayer()) {
            Player p = entity.asPlayer();
            // TODO: Handle CoX point deduction and respawn them at the start of the floor!
            return true; // True means we are intercepting the death (so they don't lose items)
        }
        return false; // Let NPCs die normally so their loot drops
    }

    @Override
    public void tick() {
        // TODO: Handle the raid timer and point degradation here!
    }

    /**
     * Registers a player to this specific raid instance and triggers the entry logic.
     */
    public void add(Player player) {
        if (player == null) return;

        // 1. Link the player to this instance so the server knows where they are
        // NOTE: Depending on your base, this might be player.setInstance(this); or player.instance = this;
        player.setInstance(this);

        // 2. Add them to the base Instance entity list (if your base requires it)
        // super.add(player); // Uncomment this if your base Instance.java has its own add() method

        // 3. Trigger the CoX specific onEnter logic (which sends them the constructed map!)
        onEnter(player);
    }
    // =========================================================================
    // COX SPECIFIC LOGIC
    // =========================================================================

    private void buildVirtualGrid() {
        for (RaidNode node : layoutNodes) {
            ChambersOfXeric.CoxRoom room = node.getRoom();
            int rot = node.getRotation();
            int size = room.getSize();
            int targetZ = node.getGridZ(); // <-- The instance floor (0, 1, 2, or 3)

            for (int srcX = 0; srcX < size; srcX++) {
                for (int srcY = 0; srcY < size; srcY++) {
                    int destX = srcX;
                    int destY = srcY;

                    if (rot == 1) { destX = srcY; destY = (size - 1) - srcX; }
                    else if (rot == 2) { destX = (size - 1) - srcX; destY = (size - 1) - srcY; }
                    else if (rot == 3) { destX = (size - 1) - srcY; destY = srcX; }

                    int targetAbsX = room.getAbsoluteSourceX() + (srcX * 8);
                    int targetAbsY = room.getAbsoluteSourceY() + (srcY * 8);

                    int gridX = (node.getGridX() * 4) + destX;
                    int gridY = (node.getGridY() * 4) + destY;

                    // Place the tile exactly on the correct Z floor!
                    virtualGrid[targetZ][gridX][gridY] = new POHPalette.POHPaletteTile(targetAbsX, targetAbsY, room.getSourceZ(), rot);
                }
            }
        }
    }
    private void applyServerClipping() {
        for (int floor = 0; floor < 4; floor++) {
            Palette serverPalette = new Palette(20, 20, 4);

            for (int x = 0; x < 20; x++) {
                for (int y = 0; y < 20; y++) {
                    POHPalette.POHPaletteTile tile = virtualGrid[floor][x][y];

                    if (tile != null) {
                        int rawX = tile.getX() * 8;
                        int rawY = tile.getY() * 8;
                        int sourceZ = tile.getZ(); // Get the room's native Z height

                        // FIX: Do not loop z 4 times! Just set it to the base plane of this generated chunk.
                        // This prevents creating thousands of useless PaletteTile objects in memory.
                        serverPalette.setTile(x, y, 0, new PaletteTile(rawX, rawY, sourceZ, tile.getRot()));
                    }
                }
            }

            Location floorBase = new Location(INSTANCE_BASE_X, INSTANCE_BASE_Y, getHeight() + (floor * 4));
            server.clip.Region.loadPalette(floorBase, serverPalette);
        }
        System.out.println("[CoX] 3D Server-sided clipping applied for Instance " + getId());
    }
    private void populateRooms() {
        for (RaidNode node : layoutNodes) {
            ChambersOfXeric.RoomType type = node.getRoom().getType();
            RaidRoom room = null;

            // Depending on the generated room type, instantiate the correct logic class
            switch (type) {
                case SCAVENGER:
                    room = new ScavengerRoom(this, node);
                    break;
               case PUZZLE:
                   room = new ThievingRoom(this, node);
                   break;
                case COMBAT:
                     room = new TektonRoom(this, node);
                     break;
                // case BOSS:
                //     room = new OlmRoom(this, node);
                //     break;
                default:
                    break; // Ignore empty/hallway rooms for now
            }

            // If we successfully created a behavior class for this room, save it and spawn its stuff!
            if (room != null) {
                activeRooms.add(room);
                room.spawn();
            }
        }
    }
    public void refreshRaidMap(Player player) {
        POHPalette palette = new POHPalette();
        int playerChunkX = player.getX() / 8;
        int playerChunkY = player.getY() / 8;
        int paletteStartX = playerChunkX - 6;
        int paletteStartY = playerChunkY - 6;

        // Figure out which floor's map we need to draw based on the player's dimension
        int currentFloor = (player.getHeight() - this.getHeight()) / 4;

        for (int px = 0; px < 13; px++) {
            for (int py = 0; py < 13; py++) {
                int worldChunkX = paletteStartX + px;
                int worldChunkY = paletteStartY + py;
                int gridX = worldChunkX - BASE_CHUNK_X;
                int gridY = worldChunkY - BASE_CHUNK_Y;

                if (gridX >= 0 && gridX < 20 && gridY >= 0 && gridY < 20) {
                    // Grab the layout for ONLY the floor we are currently on
                    POHPalette.POHPaletteTile tile = virtualGrid[currentFloor][gridX][gridY];
                    if (tile != null) {
                        // THE FIX: Always set the destination Z to 0 (Ground level)
                        palette.setTile(px, py, 0, tile);
                    }
                }
            }
        }
        player.getPA().sendConstructedMapPOH(palette);
    }
    /**
     * Handles transitioning the player to the next floor dimension when they click the hole.
     */
    public void descendFloor(final Player player) {
        // Find which floor dimension we are currently in (0, 1, or 2)
        int currentFloor = (player.getHeight() - this.getHeight()) / 4;
        final int nextFloor = currentFloor + 1;

        if (nextFloor > 3) return; // Failsafe: They are already at Olm!

        RaidNode targetNode = null;

        for (RaidNode node : layoutNodes) {
            if (node.getGridZ() == nextFloor) { // gridZ now represents the Floor Index (0-3)
                if (node.getRoom().getType() == ChambersOfXeric.RoomType.FLOORSTART
                        || node.getRoom().getType() == ChambersOfXeric.RoomType.BOSS) {
                    targetNode = node;
                    break;
                }
            }
        }

        if (targetNode != null) {
            int[] safeCoords = getAbsoluteCoordinates(targetNode, targetNode.getRoom().getSpawnX(), targetNode.getRoom().getSpawnY());
            final int targetX = safeCoords[0];
            final int targetY = safeCoords[1];

            // THE FIX: Multiply by 4 to jump to a completely new, empty dimension!
            final int nextDimensionHeight = this.getHeight() + (nextFloor * 4);

            player.getPA().movePlayer(targetX, targetY, nextDimensionHeight);
            player.sendMessage("@pur@You descend to the next level of the Chambers.");

            server.event.CycleEventHandler.getSingleton().addEvent(player, new server.event.CycleEvent() {
                int ticks = 0;
                @Override
                public void execute(server.event.CycleEventContainer container) {
                    ticks++;
                    if (ticks == 2) {
                        refreshRaidMap(player);
                        container.stop();
                    }
                }
                @Override
                public void stop() {}
            }, 1);

        } else {
            player.sendMessage("@red@Error: Could not find the next floor.");
        }
    }
    /**
     * Intercepts an NPC death from NPCHandler and routes it to the specific room.
     */
    public void handleNpcDeath(NPC npc) {
        if (npc == null) return;

        // Find which room the NPC was standing in when it died
        RaidRoom room = getRoomAt(npc.getX(), npc.getY());

        if (room != null) {
            room.handleDeath(npc); // Trigger the specific boss/scavenger logic!
        }
    }

    // Storage Tier Enum
    public enum StorageTier {
        NONE(29769),
        SMALL(29770),
        MEDIUM(29779),
        LARGE(29780);

        public final int objectId;
        StorageTier(int objectId) { this.objectId = objectId; }
    }

    private StorageTier currentStorageTier = StorageTier.NONE;

    // A list of every absolute coordinate where a storage unit exists in this raid
    private List<Location> storageLocations = new ArrayList<>();

    public void registerStorageNode(Location loc) {
        storageLocations.add(loc);
    }

    public StorageTier getStorageTier() {
        return currentStorageTier;
    }

}