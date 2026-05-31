package server.model.players.skills.farming;

import java.util.HashMap;
import java.util.Map;

/* this is the enum that stores the different compost bins stages */

public enum CompostBinStages {
	FIRST_TYPE(7839, 3851, 3835, 3848, 7811, 3849, 7814, 7815, 7816, 7817, 7828, 7829, 7830, 7831), 
	SECOND_TYPE(7818, 7823, 7819, 7820, 7821, 7822, 7824, 7825, 7826, 7827, 7832, 7833, 7834, 7835);
	private int binEmpty;
	private int closedBin;
	private int binWithCompostable;
	private int binFullOfCompostable;
	private int binWithSuperCompostable;
	private int binFullOFSuperCompostable;
	private int binWithCompost;
	private int binFullOfCompost;
	private int binWithSuperCompost;
	private int binFullOfSuperCompost;
	private int binWithTomatoes;
	private int binFullOfTomatoes;
	private int binWithRottenTomatoes;
	private int binFullOfRottenTomatoes;

	private static Map<Integer, CompostBinStages> bins = new HashMap<Integer, CompostBinStages>();

	static {
		for (CompostBinStages data : CompostBinStages.values()) {
			bins.put(data.binEmpty, data);
		}
	}

	CompostBinStages(int binEmpty, int closedBin, int binWithCompostable,
			int binFullOfCompostable, int binWithSuperCompostable,
			int binFullOFSuperCompostable, int binWithCompost,
			int binFullOfCompost, int binWithSuperCompost,
			int binFullOfSuperCompost, int binWithTomatoes,
			int binFullOfTomatoes, int binWithRottenTomatoes,
			int binFullOfRottenTomatoes) {
		this.binEmpty = binEmpty;
		this.closedBin = closedBin;
		this.binWithCompostable = binWithCompostable;
		this.binFullOfCompostable = binFullOfCompostable;
		this.binWithSuperCompostable = binWithSuperCompostable;
		this.binFullOFSuperCompostable = binFullOFSuperCompostable;
		this.binWithCompost = binWithCompost;
		this.binFullOfCompost = binFullOfCompost;
		this.binWithSuperCompost = binWithSuperCompost;
		this.binFullOfSuperCompost = binFullOfSuperCompost;
		this.binWithTomatoes = binWithTomatoes;
		this.binFullOfTomatoes = binFullOfTomatoes;
		this.binWithRottenTomatoes = binWithRottenTomatoes;
		this.binFullOfRottenTomatoes = binFullOfRottenTomatoes;
	}

	public static CompostBinStages forId(int binId) {
		return bins.get(binId);
	}

	public int getBinEmpty() {
		return binEmpty;
	}

	public int getClosedBin() {
		return closedBin;
	}

	public int getBinWithCompostable() {
		return binWithCompostable;
	}

	public int getBinFullOfCompostable() {
		return binFullOfCompostable;
	}

	public int getBinWithSuperCompostable() {
		return binWithSuperCompostable;
	}

	public int getBinFullOFSuperCompostable() {
		return binFullOFSuperCompostable;
	}

	public int getBinWithCompost() {
		return binWithCompost;
	}

	public int getBinFullOfCompost() {
		return binFullOfCompost;
	}

	public int getBinWithSuperCompost() {
		return binWithSuperCompost;
	}

	public int getBinFullOfSuperCompost() {
		return binFullOfSuperCompost;
	}

	public int getBinWithTomatoes() {
		return binWithTomatoes;
	}

	public int getBinFullOfTomatoes() {
		return binFullOfTomatoes;
	}

	public int getBinWithRottenTomatoes() {
		return binWithRottenTomatoes;
	}

	public int getBinFullOfRottenTomatoes() {
		return binFullOfRottenTomatoes;
	}
}