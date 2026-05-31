package server.model.players.packets;

import server.Config;
import server.event.*;
import server.model.items.ItemAssistant;
import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.combat.magic.MagicData;
import server.model.players.packets.npcoptions.NpcOptionOne;
import server.model.players.packets.npcoptions.NpcOptionThree;
import server.model.players.packets.npcoptions.NpcOptionTwo;
import server.model.players.PacketType;
import server.world.Boundary;

/**
 * Click NPC
 */
public class ClickNPC implements PacketType {
	public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155,
			SECOND_CLICK = 17, THIRD_CLICK = 21, FOURTH_CLICK = 18;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.npcIndex = 0;
		c.npcClickIndex = 0;
		c.playerIndex = 0;
		c.clickNpcType = 0;
		c.getPA().resetFollow();
		c.followId2 = c.npcIndex;
		c.getPA().followNpc();
		if (c.isForceMovementActive()) {
			return;
		}
		if (c.isForceMovementActive()) {
			return;
		}
		if (c.teleTimer > 0) {
			return;
		}
		switch (packetType) {

		/**
		 * Attack npc melee or range
		 **/
		case ATTACK_NPC:

			if (!c.mageAllowed) {
				c.mageAllowed = true;
				c.sendMessage("I can't reach that.");
				break;
			}
			c.npcIndex = c.getInStream().readUnsignedWordA();
			if (c.npcIndex >= NPCHandler.npcs.length || c.npcIndex < 0) {
				return;
			}
			if (NPCHandler.npcs[c.npcIndex] == null) {
				c.npcIndex = 0;
				break;
			}
			if (NPCHandler.npcs[c.npcIndex].getHealth().getMaximum() == 0) {
				c.npcIndex = 0;
				break;
			}
			if (NPCHandler.npcs[c.npcIndex] == null) {
				break;
			}
			if (c.autocastId > 0)
				c.autocasting = true;
			if (!c.autocasting && c.spellId >= 0) {
				c.spellId = 0;
			}
			c.faceUpdate(c.npcIndex);
			c.usingMagic = false;
			boolean usingBow = false;
			boolean usingOtherRangeWeapons = false;
			boolean usingArrows = false;
			boolean usingBP = c.playerEquipment[c.playerWeapon] == 12926;
			boolean usingCross = c.playerEquipment[c.playerWeapon] == 9185 || c.playerEquipment[c.playerWeapon] == 8880 ||
			c.playerEquipment[c.playerWeapon] == 9174 || c.playerEquipment[c.playerWeapon] == 9176 || c.playerEquipment[c.playerWeapon] == 9177
			 || c.playerEquipment[c.playerWeapon] == 9179 || c.playerEquipment[c.playerWeapon] == 9181 || c.playerEquipment[c.playerWeapon] == 9183
			  || c.playerEquipment[c.playerWeapon] == 10156 || c.playerEquipment[c.playerWeapon] == 837;
			boolean BpDistance = c.goodDistance(NPCHandler.npcs[c.npcIndex].getX(), NPCHandler.npcs[c.npcIndex].getY(), c.getX(),
				c.getY(), 6);
			if (c.playerEquipment[c.playerWeapon] >= 4214
					&& c.playerEquipment[c.playerWeapon] <= 4223)
				usingBow = true;
			for (int bowId : c.BOWS) {
				if (c.playerEquipment[c.playerWeapon] == bowId) {
					usingBow = true;
					for (int arrowId : c.ARROWS) {
						if (c.playerEquipment[c.playerArrows] == arrowId) {
							usingArrows = true;
						}
					}
				}
			}
			for (int otherRangeId : c.OTHER_RANGE_WEAPONS) {
				if (c.playerEquipment[c.playerWeapon] == otherRangeId) {
					usingOtherRangeWeapons = true;
				}
			}
			if ((usingBow || c.autocasting || usingBP)
					&& c.goodDistance(c.getX(), c.getY(),
							NPCHandler.npcs[c.npcIndex].getX(),
							NPCHandler.npcs[c.npcIndex].getY(), 7)) {
				c.stopMovement();
			}

			if(usingBP && BpDistance)
				c.stopMovement();
			if (usingOtherRangeWeapons
					&& c.goodDistance(c.getX(), c.getY(),
							NPCHandler.npcs[c.npcIndex].getX(),
							NPCHandler.npcs[c.npcIndex].getY(), 4)) {
				c.stopMovement();
			}
			if (!usingCross && !usingArrows && usingBow && !usingBP
					&& c.playerEquipment[c.playerWeapon] < 4212
					&& c.playerEquipment[c.playerWeapon] > 4223) {
				c.sendMessage("You have run out of arrows!");
				break;
			}
			if (!c.getCombat().correctBowAndArrows()
					&& Config.CORRECT_ARROWS
					&& c.usingBow
					&& !c.getCombat().usingCrystalBow()
					&& c.playerEquipment[c.playerWeapon] != 9185 && !usingBP) {
				c.sendMessage("You can't use "+ ItemAssistant.getItemName(c.playerEquipment[c.playerArrows]).toLowerCase()+ "s with a "+ c.getItems().getItemName(c.playerEquipment[c.playerWeapon]).toLowerCase() + ".");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}
			if (c.playerEquipment[c.playerWeapon] == 9185
					&& !c.getCombat().properBolts()) {
				c.sendMessage("You must use bolts with a crossbow.");
				c.stopMovement();
				c.getCombat().resetPlayerAttack();
				return;
			}

			if (c.followId > 0 && c.followId2 > 0) {
				c.getPA().resetFollow();
			}
			//if (c.attackTimer <= 0) {
				c.getCombat().attackNpc(c.npcIndex);
			//}

			c.followId2 = c.npcIndex;
			break;

		/**
		 * Attack npc with magic
		 **/
		case MAGE_NPC:
			
			if (!c.mageAllowed) {
				c.mageAllowed = true;
				c.sendMessage("I can't reach that.");
				break;
			}
			 c.usingSpecial = false;
			 c.getItems().updateSpecialBar();

			c.npcIndex = c.getInStream().readSignedWordBigEndianA();
			int castingSpellId = c.getInStream().readSignedWordA();
			System.out.println(castingSpellId);
			c.usingMagic = false;
			
			if (c.npcIndex >= NPCHandler.npcs.length || c.npcIndex < 0 || NPCHandler.npcs[c.npcIndex] == null) {
				break;
			}

			if (NPCHandler.npcs[c.npcIndex].getHealth().getMaximum() == 0
					|| NPCHandler.npcs[c.npcIndex].npcType == 944) {
				c.sendMessage("You can't attack this npc.");
				break;
			}

			for (int i = 1; i < MagicData.MAGIC_SPELLS.length; i++) {
				if (castingSpellId == MagicData.MAGIC_SPELLS[i][0]) {
					c.spellId = i;
					c.usingMagic = true;
					break;
				}
			}
			if (castingSpellId == 1171) { // crumble undead
				for (int npc : Config.UNDEAD_NPCS) {
					if (NPCHandler.npcs[c.npcIndex].npcType != npc) {
						c.sendMessage("You can only attack undead monsters with this spell.");
						c.usingMagic = false;
						c.stopMovement();
						break;
					}
				}
			}
			/*
			 * if(!c.getCombat().checkMagicReqs(c.spellId)) { c.stopMovement();
			 * break; }
			 */

			if (c.autocasting)
				c.autocasting = false;

			if (c.usingMagic) {
				if (c.goodDistance(c.getX(), c.getY(),
						NPCHandler.npcs[c.npcIndex].getX(),
						NPCHandler.npcs[c.npcIndex].getY(), 6)) {
					c.stopMovement();
				}
				if (c.attackTimer <= 0) {
					c.getCombat().attackNpc(c.npcIndex);
					c.attackTimer++;
				}
			}

			break;

			case FIRST_CLICK:
				c.npcClickIndex = c.inStream.readSignedWordBigEndian();
				if (c.npcClickIndex < 0 || c.npcClickIndex >= NPCHandler.npcs.length) break;
				NPC npc1 = NPCHandler.npcs[c.npcClickIndex];

				c.faceUpdate(c.npcClickIndex);
				c.followId2 = c.npcClickIndex;
				c.getPA().followNpc();

				if (c.playerRights == 3) c.sendMessage("[DEBUG] NPC Option #1-> Click index: " + c.npcClickIndex + ", NPC Id: " + npc1.npcType);

				walkToAndInteract(c, npc1, 1, () -> NpcOptionOne.handleOption(c, npc1));
				break;

			case SECOND_CLICK:
				c.npcClickIndex = c.inStream.readUnsignedWordBigEndianA();
				if (c.npcClickIndex < 0 || c.npcClickIndex >= NPCHandler.npcs.length) break;
				NPC npc2 = NPCHandler.npcs[c.npcClickIndex];

				c.faceUpdate(c.npcClickIndex);
				c.followId2 = c.npcClickIndex;
				c.getPA().followNpc();

				if (c.playerRights == 3) c.sendMessage("[DEBUG] NPC Option #2-> Click index: " + c.npcClickIndex + ", NPC Id: " + npc2.npcType);

				walkToAndInteract(c, npc2, 2, () -> NpcOptionTwo.handleOption(c, npc2));
				break;

			case THIRD_CLICK:
				c.npcClickIndex = c.inStream.readSignedWord();
				if (c.npcClickIndex < 0 || c.npcClickIndex >= NPCHandler.npcs.length) break;
				NPC npc3 = NPCHandler.npcs[c.npcClickIndex];

				c.faceUpdate(c.npcClickIndex);
				c.followId2 = c.npcClickIndex;
				c.getPA().followNpc();

				if (c.playerRights == 3) c.sendMessage("[DEBUG] NPC Option #3-> Click index: " + c.npcClickIndex + ", NPC Id: " + npc3.npcType);

				walkToAndInteract(c, npc3, 3, () -> NpcOptionThree.handleOption(c, npc3));
				break;

			case FOURTH_CLICK:
				c.npcClickIndex = c.inStream.readUnsignedWordBigEndianA();
				if (c.npcClickIndex < 0 || c.npcClickIndex >= NPCHandler.npcs.length) break;
				NPC npc4 = NPCHandler.npcs[c.npcClickIndex];

				c.faceUpdate(c.npcClickIndex);
				c.followId2 = c.npcClickIndex;
				c.getPA().followNpc();

				if (c.playerRights == 3) c.sendMessage("[DEBUG] NPC Option #4-> Click index: " + c.npcClickIndex + ", NPC Id: " + npc4.npcType);

				// Assuming you have or will create NpcOptionFour.handleOption
				// walkToAndInteract(c, npc4, 4, () -> NpcOptionFour.handleOption(c, npc4));
				break;
		}

	}
	/**
	 * Handles walking to an NPC and executing an action once within OSRS-accurate distance.
	 */
	private void walkToAndInteract(Player c, NPC npc, int clickType, Runnable action) {
		if (npc == null) return;

		// Dynamic Interaction Distance based on NPC Bounding Box
		int reqDist = npc.getSize();

		// Falconry Exception: Alive Kebbits can be clicked from 15 tiles away
		if (npc.npcType >= 5531 && npc.npcType <= 5533) {
			reqDist = 15;
		}

		// If we are already close enough, execute instantly
		if (c.goodDistance(npc.getX(), npc.getY(), c.getX(), c.getY(), reqDist)) {
			c.turnPlayerTo(npc.getX(), npc.getY());
			c.faceUpdate(0);
			c.getPA().resetFollow();
			action.run();
		} else {
			// Otherwise, set our target and wait until we walk into range
			c.clickNpcType = clickType;
			int finalReqDist = reqDist;
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.clickNpcType == clickType && !npc.isDead) {
						if (c.goodDistance(c.getX(), c.getY(), npc.getX(), npc.getY(), finalReqDist)) {
							c.turnPlayerTo(npc.getX(), npc.getY());
							c.faceUpdate(0);
							c.getPA().resetFollow();
							action.run();
							container.stop();
						}
					} else {
						container.stop(); // Stop if the player clicked somewhere else
					}
				}

				@Override
				public void stop() {
					if (c.clickNpcType == clickType) {
						c.clickNpcType = 0;
					}
				}
			}, 1);
		}
	}
}
