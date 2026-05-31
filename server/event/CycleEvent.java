package server.event;

/**
 * What the event must implement
 * 
 * @author Stuart <RogueX>
 * 
 */
public interface CycleEvent {

	/**
	 * Code which should be ran when the event is executed
	 * 
	 * @param container
	 */
	public void execute(CycleEventContainer container);

	/**
	 * The update function is referenced every cycle the event is alive for.
	 */
	default void update(CycleEventContainer container) {
	}
	/**
	 * Code which should be ran when the event stops
	 */
	default void stop() {
	}

}