package server.event.impl;

import server.event.Event;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.skills.Skill;

public class RunEnergyEvent extends Event<Player> {

	/**
	 * The maximum amount of ticks that we have to wait for our run to regenerate
	 */
	private static final int MAXIMUM_TICKS = 13;

	/**
	 * The minimum amount of ticks that we have to wait for our run to regenerate
	 */
	private static final int MINIMUM_TICKS = 8;

	/**
	 * The amount of agility levels that impact run energy regeneration
	 */
	private static final int INTERVAL = 99 / (MAXIMUM_TICKS - MINIMUM_TICKS);

	/**
	 * The amount of ticks that must pass before energy can be restored
	 */
	private int ticksRequired;

	public RunEnergyEvent(Player attachment, int ticks) {
		super(attachment, ticks);
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.disconnected || attachment.getSession() == null) {
			super.stop();
			return;
		}
		
		if (attachment.getRunEnergy() == 100) {
			return;
		}
		// Inside RunEnergyEvent execute()
		if (super.getElapsedTicks() >= ticksRequired) {
		    int oldEnergy = attachment.getRunEnergy();
		    attachment.setRunEnergy(oldEnergy + 1);
		    
		    // Only send the frame if the energy actually changed (though it always will here)
		    // or use this logic in your main Player process/Run decrement logic.
		    attachment.getPA().sendFrame126(attachment.getRunEnergy() + "%", 149);
		    
		    ticksRequired = super.getElapsedTicks() + updateTicksRequired();
		}
	}

	private final int updateTicksRequired() {
		int level = Integer.min(99, attachment.getSkills().getLevel(Skill.AGILITY));
		int reduction = level < INTERVAL ? 0 : level / INTERVAL;
		return Integer.max(MINIMUM_TICKS, MAXIMUM_TICKS - reduction);
	}

}
