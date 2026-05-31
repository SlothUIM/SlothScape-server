package server.model.players.skills.fishing;

import java.util.*;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.items.ItemAssistant;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.skills.Skill;
import server.model.players.skills.fishing.spots.FishSpotDef;
import server.model.players.Client;
import server.util.Misc;

@Slf4j @Getter @Setter
public class Fishing {
	private final SecondaryCatch secondary;
	private final CatchOption primary;
	private final int levelReq;
	private final int toolId;
	private final int baitId; // -1 if none
	private final int itemId; // the fish you get
	private final int exp;
	private final int animation;
	private final int catchDelayTicks;

	public Fishing(int levelReq, int toolId, int baitId, int itemId, int exp, int animation,
	               int low, int high, int catchDelayTicks, SecondaryCatch secondary) {
		this.levelReq = levelReq;
		this.toolId = toolId;
		this.baitId = baitId;
		this.itemId = itemId;
		this.exp = exp;
		this.animation = animation;
		this.secondary = secondary;
		this.primary = new CatchOption(itemId, levelReq, exp, low, high);
		this.catchDelayTicks = catchDelayTicks;
	}

	public static final Map<FishSpotDef.SpotCategory, List<Fishing>> FISHABLES = new HashMap<>();

	static {
		FISHABLES.put(FishSpotDef.SpotCategory.SMALL_NET, Arrays.asList(
				new Fishing(1, 303, -1, 317, 10, 621, 16, 96, 4,
						new SecondaryCatch(321, 15, 40, 8, 64))
		));

		FISHABLES.put(FishSpotDef.SpotCategory.SMALL_NET_BAIT, Arrays.asList(
				new Fishing(5, 307, 313, 327, 20, 622, 20, 100, 4,
						new SecondaryCatch(345, 10, 30, 10, 80))
		));

		FISHABLES.put(FishSpotDef.SpotCategory.ROD_LURE, Arrays.asList(
				new Fishing(20, 309, 314, 335, 50, 622, 32, 192, 4,
						new SecondaryCatch(331, 30, 70, 16, 96))
		));

		FISHABLES.put(FishSpotDef.SpotCategory.TUTORIAL, Arrays.asList(
				new Fishing(1, 6209, -1, 2514, 10, 621, 8, 48, 4, null)
		));

		FISHABLES.put(FishSpotDef.SpotCategory.ROD_BAIT, Arrays.asList(
				new Fishing(25, 307, 313, 349, 60, 622, 24, 120, 4, null)
		));

		FISHABLES.put(FishSpotDef.SpotCategory.CAGE, Arrays.asList(
				new Fishing(40, 301, -1, 377, 90, 619, 12, 64, 4, null)
		));

		FISHABLES.put(FishSpotDef.SpotCategory.HARPOON, Arrays.asList(
				new Fishing(35, 311, -1, 359, 80, 618, 10, 60, 4,
						new MultiCatches(
								new int[]{371, 359},
								50,
								100,
								new int[]{10, 6},
								new int[]{60, 32}
						)
				)
		));

		FISHABLES.put(FishSpotDef.SpotCategory.BIG_NET, Arrays.asList(
				new Fishing(76, 305, -1, 383, 110, 620, 12, 48, 4,
						new MultiCatches(
								new int[]{353, 341, 363, 405, 401},
								16,
								20,
								new int[]{12, 8, 6, 4, 2},
								new int[]{48, 32, 24, 16, 12}
						)
				)
		));

		FISHABLES.put(FishSpotDef.SpotCategory.BIG_HARPOON, Arrays.asList(
				new Fishing(76, 311, -1, 383, 110, 618, 6, 32, 4, null)
		));
	}

	public int rollCatch(Player player) {
		int playerLevel = player.getSkills().getLevel(Skill.FISHING);

		if (Math.random() <= primary.getChance(playerLevel))
			return primary.itemId;

		if (secondary instanceof MultiCatches multi)
			return multi.getRandomCatch(playerLevel);
		else if (secondary != null && Math.random() <= secondary.option.getChance(playerLevel))
			return secondary.option.itemId;

		return -1;
	}

	public static void attemptCatch(Player player, int npcId, int option) {
		if (!FishSpotDef.isFishingSpot(npcId))
			return;

		FishSpotDef.SpotCategory category = FishSpotDef.getCategory(npcId, option);
		if (category == null)
			return;

		List<Fishing> catches = Fishing.FISHABLES.get(category);
		if (catches == null || catches.isEmpty())
			return;

		Fishing fishDef = catches.stream()
				.filter(f -> player.getSkills().getLevel(Skill.FISHING) >= f.levelReq)
				.findAny()
				.orElse(null);

		if (fishDef == null) {
			player.sendMessage("You need a higher Fishing level to fish here.");
			return;
		}

		// --- PRE-CHECKS BEFORE STARTING ---
		if (!player.getItems().playerHasItem(fishDef.getToolId())) {
			player.sendMessage("You need a " + ItemAssistant.getItemName(fishDef.getToolId()) + " to fish here.");
			return;
		}

		if (fishDef.getBaitId() != -1 && !player.getItems().playerHasItem(fishDef.getBaitId())) {
			player.sendMessage("You do not have any bait to fish here.");
			return;
		}

		// --- START ACTIONS ---
		int catchDelay = fishDef.getCatchDelayTicks() > 0 ? fishDef.getCatchDelayTicks() : 4;
		player.startAnimation(fishDef.getAnimation());

		// Tutorial handling
		if (player.tutorialProgress == 6) {
			player.getPA().drawHeadicon(0, 0, 0, 0);
			player.getPA().chatbox(6180);
			player.getDH().chatboxText(
					"This should only take a few seconds.",
					"As you gain Fishing experience you'll find that there are many",
					"types of fish and many ways to catch them.",
					"", "Please wait");
			player.getPA().chatbox(6179);
		} else {
			player.sendMessage("You begin fishing...");
		}

		// --- THE FIX: Lock the starting coordinates ---
		final int startX = player.getX();
		final int startY = player.getY();

		CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
			int ticks = 0;

			@Override
			public void execute(CycleEventContainer container) {

				// STOP CONDITION 1: If the player walks away, instantly terminate the event.
				if (player.disconnected || player.isDead || player.getX() != startX || player.getY() != startY) {
					container.stop();
					return;
				}

				// STOP CONDITION 2: Inventory check
				if (player.getItems().freeSlots() < 1) {
					player.sendMessage("You don't have enough inventory space.");
					container.stop();
					return;
				}

				// STOP CONDITION 3: Tool check during cycle
				if (!player.getItems().playerHasItem(fishDef.getToolId())) {
					player.sendMessage("You need a " + ItemAssistant.getItemName(fishDef.getToolId()) + " to fish here.");
					container.stop();
					return;
				}

				// STOP CONDITION 4: Bait check during cycle (Fixes the infinite bait glitch)
				if (fishDef.getBaitId() != -1 && !player.getItems().playerHasItem(fishDef.getBaitId())) {
					player.sendMessage("You have run out of bait.");
					container.stop();
					return;
				}

				// STOP CONDITION 5: NPC check
				NPC npc = NPCHandler.getNpc(npcId);
				if (npc == null || npc.isDead) {
					player.sendMessage("The fishing spot has moved.");
					container.stop();
					return;
				}

				// Attempt Catch
				int caughtId = fishDef.rollCatch(player);
				if (caughtId != -1) {
					int expAwarded = fishDef.exp;

					if (fishDef.secondary instanceof MultiCatches multi) {
						for (CatchOption opt : multi.options) {
							if (opt.itemId == caughtId) {
								expAwarded = opt.exp;
								break;
							}
						}
					} else if (fishDef.secondary != null && fishDef.secondary.option.itemId == caughtId) {
						expAwarded = fishDef.secondary.option.exp;
					}

					// We already verified they have bait above, safe to delete
					if (fishDef.getBaitId() != -1) {
						player.getItems().deleteItem(fishDef.getBaitId(), 1);
					}

					if (player.getItems().addItem(caughtId, 1)) {
						player.getSkills().addExperience(expAwarded * 10, Skill.FISHING);

						String name = ItemAssistant.getItemName(caughtId);
						if (name.toLowerCase().startsWith("raw ")) {
							name = name.substring(4);
						}
						player.sendMessage("You catch a " + name + "!");

						// Tutorial success handling
						if (player.tutorialProgress == 6) {
							player.getDH().sendDialogues(3019, -1);
							container.stop();
							return;
						}
					}
				}

				// Refresh animation cleanly
				ticks++;
				if (ticks % 3 == 0) {
					player.startAnimation(fishDef.getAnimation());
				}
			}

			@Override
			public void stop() {
				player.startAnimation(-1);
			}
		}, catchDelay);
	}
}