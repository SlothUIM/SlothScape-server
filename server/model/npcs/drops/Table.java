package server.model.npcs.drops;

import java.util.ArrayList;
import org.apache.commons.lang3.Range;
import server.util.Misc;

@SuppressWarnings("serial")
public class Table extends ArrayList<Drop> {

	private final TablePolicy policy;
	private final int rolls;

	public Table(TablePolicy policy, int rolls) {
		this.policy = policy;
		this.rolls = rolls;
	}

	/**
	 * Selects a random drop based on WEIGHT, applying a drop rate multiplier.
	 */
	public Drop fetchRandom(double dropRateMultiplier) {
		double totalWeight = 0;

		// 1. Calculate the total tickets in the hat
		for (Drop drop : this) {
			// If the item is "Nothing" (ID 0 or -1), it doesn't get boosted.
			if (drop.getItemId() <= 0) {
				totalWeight += drop.getWeight();
			} else {
				// Real items get their tickets multiplied by your Config/Donator rates!
				totalWeight += (drop.getWeight() * dropRateMultiplier);
			}
		}

		// 2. Roll a random ticket
		double random = Misc.preciseRandom(Range.between(0.0, totalWeight));
		double current = 0;

		// 3. Find which item won the raffle
		for (Drop drop : this) {
			double effectiveWeight = drop.getItemId() <= 0 ? drop.getWeight() : (drop.getWeight() * dropRateMultiplier);
			current += effectiveWeight;

			if (random <= current) {
				return drop;
			}
		}

		// Fallback safety
		return get(0);
	}

	public TablePolicy getPolicy() {
		return policy;
	}

	public int getRolls() {
		return rolls;
	}
}