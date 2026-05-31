package server.model.players.skills;

public enum LogData {

	LOG(1511, 1, 40), 
	ACHEY(2862, 1, 40), 
	OAK(1521, 15, 60), 
	WILLOW(1519, 30, 90), 
	TEAK(6333, 35, 105), 
	ARCTIC_PINE(10810, 42, 125), 
	MAPLE(1517, 45, 135), 
	MAHOGANY(6332, 50, 157.5), 
	EUCALYPTUS(12581, 58, 193.5), 
	YEW(1515, 60, 202.5), 
	MAGIC(1513, 75, 303.8), 
	RED(7404, 1, 40), // RED LOG
	BLUE(7406, 1, 40), // BLUE LOG
	GREEN(7405, 1, 40), // RED LOG;
	WHITE(10328, 1, 40), // RED LOG;
	PURPLE(10329, 1, 40); // RED LOG;

	private int logId, level;
	private double xp;

	private LogData(int logId, int level, double xp) {
		this.logId = logId;
		this.level = level;
		this.xp = xp;
	}

	public int getLogId() {
		return logId;
	}

	public int getLevel() {
		return level;
	}

	public double getXp() {
		return xp;
	}
}
