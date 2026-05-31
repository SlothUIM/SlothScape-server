package server.model.players.skills.farming;
import server.model.players.Client;
import server.util.Misc;
import server.tick.Tick;

public class FarmingTask extends Tick {

	private Client player;

	public FarmingTask(Client player) {
		super(10);
		this.player = player;
	}

	protected void execute() {
		//Farming.processCalc(player);
	}
}