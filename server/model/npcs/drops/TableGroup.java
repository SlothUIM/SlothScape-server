package server.model.npcs.drops;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import server.Config;
import server.model.players.Player;
import server.model.items.Item;
import server.util.Misc;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

	private final List<Integer> npcIds;

	public TableGroup(List<Integer> npcsIds) {
		this.npcIds = npcsIds;
	}

	/**
	 * Helper method to quickly grab a specific table policy
	 */
	private Table getTable(TablePolicy policy) {
		for (Table table : this) {
			if (table.getPolicy() == policy) {
				return table;
			}
		}
		return null;
	}

	/**
	 * Accesses the tables hierarchically.
	 * Rare drops will replace Common/Main drops to prevent double-loot.
	 */
	public List<Item> access(Player player, double dropModifier, int repeats) {
		List<Item> items = new ArrayList<>();

		// 1. CONSTANT DROPS (Bones, Ashes - These ALWAYS drop)
		Table constantTable = getTable(TablePolicy.CONSTANT);
		if (constantTable != null) {
			for (Drop drop : constantTable) {
				items.add(new Item(drop.getItemId(), drop.getMinimumAmount() + Misc.random(drop.getMaximumAmount() - drop.getMinimumAmount())));
			}
		}

		// 2. EXCLUSIVE DROPS (Only ONE of these tables should drop a real item per kill)
		// We check in order of Rarest to Most Common.
		TablePolicy[] exclusiveOrder = {
				TablePolicy.ULTRA_RARE,
				TablePolicy.VERY_RARE,
				TablePolicy.RARE,
				TablePolicy.UNCOMMON,
				TablePolicy.COMMON,
				TablePolicy.MAIN
		};

		for (int r = 0; r < repeats; r++) {
			boolean securedExclusiveDrop = false;

			for (TablePolicy policy : exclusiveOrder) {
				Table table = getTable(policy);
				if (table != null) {
					for (int i = 0; i < table.getRolls(); i++) {
						Drop drop = table.fetchRandom(dropModifier);

						// If it rolled a REAL item (Not the empty '0' drop)
						if (drop.getItemId() > 0) {
							items.add(new Item(drop.getItemId(), drop.getMinimumAmount() + Misc.random(drop.getMaximumAmount() - drop.getMinimumAmount())));
							securedExclusiveDrop = true;

							// Broadcast Logic for Rare Tables
							if (policy == TablePolicy.VERY_RARE || policy == TablePolicy.RARE || policy == TablePolicy.ULTRA_RARE) {
								if(!Config.includes(drop.getItemId())){
									// Put your DiscordBot or GlobalMessages broadcast here!
								}
							}

							break; // Stop rolling on this specific table
						}
					}
				}

				// The Magic Fix: If we got a drop from a rare table, SKIP the main/common tables!
				if (securedExclusiveDrop) {
					break;
				}
			}
		}

		// 3. TERTIARY DROPS (Like Gem table - these roll independently of the main loot)
		Table gemTable = getTable(TablePolicy.GEM);
		if (gemTable != null) {
			for (int i = 0; i < gemTable.getRolls(); i++) {
				Drop drop = gemTable.fetchRandom(dropModifier);
				if (drop.getItemId() > 0) {
					items.add(new Item(drop.getItemId(), drop.getMinimumAmount() + Misc.random(drop.getMaximumAmount() - drop.getMinimumAmount())));
				}
			}
		}

		return items;
	}

	public Optional<Drop> getDropMeta(int itemId) {
		for (Table table : this) {
			for (Drop drop : table) {
				if (drop.getItemId() == itemId) {
					return Optional.of(drop);
				}
			}
		}
		return Optional.empty();
	}

	public List<Integer> getNpcIds() {
		return npcIds;
	}
}