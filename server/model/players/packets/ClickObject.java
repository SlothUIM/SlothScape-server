package server.model.players.packets;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import server.model.players.*;
import server.clip.ObjectDef;
import server.clip.Region;
import server.clip.WorldObject;
import server.world.World;
import server.model.players.packets.objectoptions.*;
import server.model.multiplayer_session.MultiplayerSessionFinalizeType;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

public class ClickObject implements PacketType {

	public static final int FIRST_CLICK = 132, SECOND_CLICK = 252, THIRD_CLICK = 70, FOURTH_CLICK = 234, FIFTH_CLICK = 228;

	@Override
	public void processPacket(final Player c, int packetType, int packetSize) {
		int objectY = c.getInStream().readSignedWordBigEndian();
		int objectX = c.getInStream().readUnsignedWordA();
		int objectId = c.getInStream().readUnsignedWordBigEndianA();
		int objectType = c.getInStream().readUnsignedByte();

		c.objectId = objectId;
		c.objectX = objectX;
		c.objectY = objectY;
		c.objectType = objectType;
		c.getCombat().resetPlayerAttack();

		// --- FIX 1: Prioritize object interaction over everything else ---
		c.npcIndex = 0;
		c.playerIndex = 0;
		c.clickNpcType = 0;
		c.walkingToItem = false;
		if (c.isForceMovementActive() || c.teleTimer > 0) return;
		if (c.viewingLootBag || c.addingItemsToLootBag || c.viewingRunePouch) return;

		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) return;

		// --- FIX 2: Fetch the REAL object to get its true rotation! ---
		Optional<WorldObject> existingObject = Region.getWorldObject(objectId, objectX, objectY, c.getLocation().getZ(), objectType);
		WorldObject targetObject = existingObject.orElseGet(() ->
				new WorldObject(objectId, objectX, objectY, c.getLocation().getZ(), objectType, 0)
		);

		DuelSession duelSession = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}

		ObjectDef objectDef = ObjectDef.forID(objectId);
		if (objectDef != null) {
			c.setInteractingObject(Optional.of(targetObject));

			// Now direction is perfectly accurate, so width and length will be perfectly accurate!
			int direction = targetObject.getFace();
			int objectWidth;
			int objectLength;

			if (objectId == 19044) {
				objectDef.width = 2;
				objectDef.length = 1;
			}

			if (direction != 1 && direction != 3) {
				objectWidth = objectDef.width;
				objectLength = objectDef.length;
			} else {
				objectWidth = objectDef.length;
				objectLength = objectDef.width;
			}

			Runnable onReached = () -> {
				try {
					if(c.playerName.equalsIgnoreCase("Sloth"))
						c.sendMessage("Click Type Packet: "+packetType+" ObjectID: "+objectId+" objectX: "+objectX+" objectY: "+objectY+" Face: "+targetObject.getFace());
					c.turnToObject(objectX, objectY, objectWidth, objectLength);
					switch(packetType) {
						case FIRST_CLICK: ObjectOptionOne.handleOption(c, targetObject, targetObject.getFace()); break;
						case SECOND_CLICK: ObjectOptionTwo.handleOption(c, targetObject, targetObject.getFace()); break;
						case THIRD_CLICK: ObjectOptionThree.handleOption(c, objectId, objectX, objectY); break;
						case FOURTH_CLICK: ObjectOptionFour.handleOption(c, targetObject, targetObject.getFace()); break;
						case FIFTH_CLICK: ObjectOptionFive.handleOption(c, targetObject, targetObject.getFace()); break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					c.resetInteractingObject();
				}
			};

			// If we are already there, execute instantly!
			if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), objectWidth + 1, objectLength + 1)) {
				onReached.run();
			} else {
				// Otherwise, wait until we arrive.
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						Optional<WorldObject> target = c.getInteractingObject();
						if (!target.isPresent()) {
							container.stop();
							return;
						}
						WorldObject obj = target.get();
						if (obj.getX() != objectX || obj.getY() != objectY || obj.getId() != objectId) {
							container.stop();
							return;
						}

						if (c.goodDistance(objectX, objectY, c.getX(), c.getY(), objectWidth, objectLength)) {
							onReached.run();
							container.stop();
						}
					}
				}, 1);
			}
		}
	}
}