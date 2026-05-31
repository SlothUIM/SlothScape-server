package server.model.players.skills.farming;

import java.util.HashMap;
import java.util.Map;


public enum CompostBinLocations {
	NORTH_ARDOUGNE(0, 7839, 3, 2661, 3375), 
	PHASMATYS(1, 7837, 1, 3610, 3522), 
	FALADOR(2, 7837, 4, 3056, 3312), 
	CATHERBY(3, 7837, 3, 2804, 3464);

	public int compostIndex;
	public int binObjectId;
	public int objectFace;
	public int x, y, z;
	public static Map<Integer, CompostBinLocations> bins = new HashMap<Integer, CompostBinLocations>();

	static {
		for (CompostBinLocations data : CompostBinLocations.values()) {
			bins.put(data.compostIndex, data);
		}
	}

	CompostBinLocations(int compostIndex, int binObjectId, int objectFace,
			int x, int y) {
		this.compostIndex = compostIndex;
		this.binObjectId = binObjectId;
		this.objectFace = objectFace;
		this.x = x;
		this.y = y;
	}

	public static CompostBinLocations forId(int index) {
		return bins.get(index);
	}

	public static CompostBinLocations forPosition(int x, int y) {
		for (CompostBinLocations compostBinLocations : CompostBinLocations
				.values()) {
			if (compostBinLocations.x == x && compostBinLocations.y == y) {
				return compostBinLocations;
			}
		}
		return null;
	}

	public int getCompostIndex() {
		return compostIndex;
	}

	public int getBinObjectId() {
		return binObjectId;
	}

	public int getObjectFace() {
		return objectFace;
	}
}