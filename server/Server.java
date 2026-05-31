package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;

import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
import server.net.PipelineFactory;

import org.jboss.netty.bootstrap.ServerBootstrap;
import server.event.CycleEventHandler;
import server.model.players.PlayerHandler;
import server.util.SimpleTimer;
import server.world.World;

@Slf4j
public class Server {
	private static long minutesCounter;

	public static boolean sleeping;

	// FIX: Use try-with-resources to automatically close the file reader and prevent memory/handle leaks.
	private static void startMinutesCounter() {
		try (BufferedReader minuteFile = new BufferedReader(new FileReader("./data/minutes.log"))) {
			minutesCounter = Long.parseLong(minuteFile.readLine());
		} catch (Exception e) {
			log.error("Failed to read minutes.log", e);
		}
	}

	// FIX: Use try-with-resources for the writer as well.
	private static void setMinutesCounter(long minutesCounter) {
		try (BufferedWriter minuteCounter = new BufferedWriter(new FileWriter("./data/minutes.log"))) {
			minuteCounter.write(Long.toString(minutesCounter));
		} catch (IOException e) {
			log.error("Failed to write to minutes.log", e);
		}
	}

	public static long getMinutesCounter() {
		return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - minutesCounter);
	}

	private static final SimpleTimer debugTimer = new SimpleTimer();
	private static final DecimalFormat debugPercentFormat = new DecimalFormat("0.00%");

	public static boolean UpdateServer = false;
	public static long lastMassSave = System.currentTimeMillis();
	private static long cycles, totalCycleTime, sleepTime;

	public static int serverlistenerPort;
	public static final int cycleRate;

	private static final ScheduledExecutorService GAME_THREAD = Executors.newSingleThreadScheduledExecutor();
	private static final ScheduledExecutorService IO_THREAD = Executors.newSingleThreadScheduledExecutor();

	static {
		serverlistenerPort = 43595;
		cycleRate = 600;
		sleepTime = 0;
	}

	private static final Runnable IO_TASKS = () -> {
		try {
			// TODO tasks(players online, etc)
		} catch (Throwable ttt) {
			log.error("IO tasks threw an error!", ttt);
		}
	};

	public static void main(String args[]) {
		try {
			long startTime = System.currentTimeMillis();

			patchDatabase();
			initializeWorld();
			registerShutdownHook();
			bindPorts();
			startSchedulers();

			long endTime = System.currentTimeMillis();
			World.getWorld().setWorldLoaded(true);
			long elapsed = endTime - startTime;
			String prefix = World.getWorld().isBetaWorld() ? "BETA" : World.getWorld().isLocalWorld() ? "LOCAL" : "LIVE";
			log.info("[{}] Server started successfully in {} ms", prefix, elapsed);
		} catch (Exception e) {
			log.error("Critical failure during server startup!", e);
		}
	}

	private static void patchDatabase() {
		try (java.sql.Connection conn = server.util.Database.getConnection();
		     java.sql.Statement stmt = conn.createStatement()) {
			stmt.execute("ALTER TABLE house_settings ADD COLUMN location VARCHAR(50) DEFAULT 'RIMMINGTON'");
			System.out.println("[Database] Successfully patched house_settings table with location column.");
		} catch (Exception e) {
			System.out.println("[Database] Location column already exists. Moving on!");
		}
	}

	private static void initializeWorld() throws Exception {
		World.getWorld().init();
	}

	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	private static void startSchedulers() {
		GAME_THREAD.scheduleAtFixedRate(SERVER_TASKS, 0, cycleRate, TimeUnit.MILLISECONDS);
		IO_THREAD.scheduleAtFixedRate(IO_TASKS, 0, 30, TimeUnit.SECONDS);
	}

	private static void bindPorts() {
		ServerBootstrap serverBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		serverBootstrap.setPipelineFactory(new PipelineFactory(new HashedWheelTimer()));
		int worldPort = 43595;
		serverBootstrap.bind(new InetSocketAddress(worldPort));
		System.out.println("World "+ ServerStatusWriter.WORLD_ID+" started on port " + worldPort);
	}

	private static final Runnable SERVER_TASKS = () -> {
		long start = System.currentTimeMillis();

		try {
			World.getWorld().tick();
			CycleEventHandler.getSingleton().process();
		} catch (Throwable t) {
			// FIX: Never swallow the main thread throwable. If an error occurs here, you need to know immediately.
			log.error("FATAL ERROR IN MAIN GAME TICK:", t);
		}

		long elapsed = System.currentTimeMillis() - start;
		totalCycleTime += elapsed;
		cycles++;

		debug();
	};

	public static boolean playerExecuted = false;

	public static void debug() {
		if (debugTimer.elapsed() > 360_000 || playerExecuted) {
			long avgCycleTime = totalCycleTime / Math.max(cycles, 1);
			double engineLoad = ((double) avgCycleTime / (double) Config.CYCLE_TIME);

			long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
			long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
			long usedMem = totalMem - freeMem;

			System.out.println("[DEBUG] Average Cycle Time: " + avgCycleTime + "ms");
			System.out.println("[DEBUG] Engine Load: " + debugPercentFormat.format(engineLoad));
			System.out.println("[DEBUG] Players Online: " + PlayerHandler.playerCount);
			System.out.println("[DEBUG] Memory Usage: " + usedMem + "MB / " + totalMem + "MB");

			totalCycleTime = 0;
			cycles = 0;

			// FIX: Removed System.gc() and System.runFinalization() to prevent
			// massive server-wide lag spikes every 6 minutes.

			debugTimer.reset();
			playerExecuted = false;
		}
	}

	public static long getSleepTimer() {
		return sleepTime;
	}
}