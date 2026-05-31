package server.model.players.skills.firemake;

import lombok.Getter;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum LogData {

	LOGS(1511, 1, 40),
	ACHEY_TREE_LOGS(2862, 1, 40),
	OAK_LOGS(1521, 15, 60),
	WILLOW_LOGS(1519, 30, 105),
	TEAK_LOGS(6333, 35, 105),
	MAPLE_LOGS(1517, 45, 135),
	YEW_LOGS(1515, 60, 203),
	ARCTIC_PINE_LOGS(10810, 45, 135),
	MAHOGANY_LOGS(6332, 50, 158),
	MAGIC_LOGS(1513, 75, 304),
	RED_LOGS(7404, 1, 256),
	GREEN_LOGS(7405, 1, 256),
	BLUE_LOGS(7406, 1, 256),
	WHITE_LOGS(10328, 1, 256),
	PURPLE_LOGS(10329, 1, 256),
	REDWOOD_LOGS(19669, 90, 350);

	private final int logId;
	private final int levelRequirement;
	private final double experience;

	LogData(int logId, int levelRequirement, double experience) {
		this.logId = logId;
		this.levelRequirement = levelRequirement;
		this.experience = experience;
	}

	private static final Map<Integer, LogData> LOG_CACHE = new HashMap<>();

	static {
		for (LogData log : values()) {
			LOG_CACHE.put(log.getLogId(), log);
		}
	}

	public static LogData getLogData(int logId) {
		return LOG_CACHE.get(logId);
	}
}