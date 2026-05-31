package server.model.players.packets.dialogue.npc;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.skills.thieving.Thieving;

public class Baker extends NPCDialogue {

	public Baker(Player c) {
		super(c);
	}

	@Override
	public int[] getNPCIDs() {
		return new int[] { 8724, 8725 };
	}
	@Override
	public String getDialogueRange() {
	    return "5300-5303";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		String sirMadame = c.playerAppearance[0] == 0 ? "Sir" : "Madame";

		switch (startDialogueId) {

		// --- Entry Point ---
		case 5300:
			// Checks if the player stole from a bakery stall in the last 3 minutes (180,000 milliseconds)
			boolean recentlyStolen = (System.currentTimeMillis() - c.lastBakerTheft < 180000);

			if (recentlyStolen) {
				npc(c, "Baker", Anim.ANGRY, "You're the one who stole something from me!");
				c.nextChat = 5301;
				
			} else {
				npc(c, "Baker", Anim.CALM_1, "Good day " + sirMadame + ". Would you like some nice freshly", "baked bread? Or perhaps a nice piece of cake?");
				c.nextChat = 5302;
			}
			break;

		// --- Post-Stealing Route ---
					case 5301:

						npc(c, "Baker", Anim.ANGRY, "Guards! Guards!");
						c.nextChat = -1;
						// 1. Make the Baker actually yell it overhead
						if (c.rememberNpcIndex > 0) {
							server.model.npcs.NPC baker = server.model.npcs.NPCHandler.npcs[c.rememberNpcIndex];
							if (baker != null) {
								baker.forceChat("Guards! Guards!");
							}
						}

						// 2. Alert the nearby guards to attack!
						boolean guardFound = false;
						for (server.model.npcs.NPC guard : server.model.npcs.NPCHandler.npcs) {
							if (guard != null && !guard.isDead && guard.heightLevel == c.HeightLevel) {
								
								// Use your new method to ensure it's actually a combat-capable guard/knight!
								if (c.getThieving().isGuard(guard.npcType)) { 
									
									// Check if the guard is within 15 tiles
									if (c.goodDistance(c.getX(), c.getY(), guard.getX(), guard.getY(), 15)) {
										
										// FACING CHECK: Is the guard actually looking in the player's direction?
										if (c.getThieving().isLookingAt(guard, c.getX(), c.getY())) {
											
											// LINE OF SIGHT: Make sure there isn't a wall between the guard and the player
											if (server.clip.Region.canProjectileMove(guard.getX(), guard.getY(), c.getX(), c.getY(), guard.heightLevel, 1, 1)) {
												
												guard.turnNpc(c.getX(), c.getY());
												guard.forceChat("Stop right there!");
												
												// Trigger the combat aggro 
												guard.underAttack = true;
												guard.killerId = c.getIndex();
												c.underAttackBy = guard.getIndex();
												c.underAttackBy2 = guard.getIndex();
												
												guardFound = true;
												break; // We only need one guard to jump them!
											}
										}
									}
								}
							}
						}
						
						if (!guardFound) {
							c.sendMessage("Luckily, the guards weren't looking your way.");
						}
						break;

		// --- Standard Shop Route ---
		case 5302:
			options(c, 5302, "Let's see what you have.", "No thank you.");
			break;

		case 5303:
			player(c, Anim.CALM_1, "No thank you.");
			c.nextChat = -1;
			break;
		}
	}

	@Override
	public void onOption(Player c, int buttonId) {
		
		// 5302: Main Menu
		if (c.dialogueAction == 5302) {
			switch (buttonId) {
				case OPT2_FIRST: // Let's see what you have.
					// We can dynamically check the NPC ID to open different shops if needed!
					if (c.npcClickIndex > 0) {
						int interactingNpcId = server.model.npcs.NPCHandler.npcs[c.npcClickIndex].npcType;
						if (interactingNpcId == 8725) {
							c.getShops().openShop(48); // TODO: Kourend Baker Shop ID
						} else {
							c.getShops().openShop(59); // TODO: Ardougne Baker Shop ID
						}
					} else {
						c.getShops().openShop(48); // Fallback Shop
					}
					break;
				case OPT2_SECOND: // No thank you.
					next(c, 5303); 
					break;
			}
		}
	}
}