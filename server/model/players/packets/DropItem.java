package server.model.players.packets;

import server.Config;
import server.Server;
import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.content.treasuretrails.types.PuzzleBox;
import server.model.players.skills.*;
import server.model.players.skills.pets.PetHandler;
import server.model.players.skills.pets.PetHandler.Pets;
import server.world.World;
import server.model.items.ItemAssistant;
import server.model.players.PacketType;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;

/**
 * Drop Item
 **/
public class DropItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int itemId = c.getInStream().readUnsignedWordA();
		c.getInStream().readUnsignedByte();
		c.getInStream().readUnsignedByte();
		int slot = c.getInStream().readUnsignedWordA();
		if (System.currentTimeMillis() - c.alchDelay < 1800) {
			return;
		}
		if (c.arenas()) {
			c.sendMessage("You can't drop items inside the arena!");
			return;
		}
		if (c.inTrade) {
			c.sendMessage("You can't drop items while trading!");
			return;
		}
		SkillHandler.resetSkills(c);

		Pets pet = PetHandler.forItem(itemId);

		if (pet != null) {
			c.startAnimation(827);
			PetHandler.spawn(c, pet, false, false);
			return;
		}
		/*if(itemId == 11941){
			c.getDH().sendDestroy("Looting Bag", "You will have to buy another", itemId);
			return;
		}*/
		boolean droppable = true;
		for (int i : Config.UNDROPPABLE_ITEMS) {
			if (i == itemId) {
				droppable = false;
				break;
			}
		}
		for (int p : Config.CAT_ITEMS) {
				if (p == itemId) {
				if(c.hasNpc == true) {
					droppable = false;
					break;
				}
			}
		}		
		switch(itemId) {
			case 10092:
				c.getItems().deleteItem(itemId, 1);
				c.sendMessage("You have released the ferret...");
				c.sendMessage("It scurries away into the woods...");
				return;

		}
		for (int i : Config.DESTROYABLE_ITEMS) {
			if (i == itemId) {
				c.DestroyID = itemId;
					c.getDH().sendDestroy(ItemAssistant.getItemName(itemId), c.getDH().sendDestroyDialogue(itemId), itemId);
				return;
			}
		}
		if (c.playerItemsN[slot] != 0 && itemId != -1
				&& c.playerItems[slot] == itemId + 1) {
			if (droppable) {
				World.getWorld().getItemHandler().createGroundItem(c, itemId, c.getX(),
						c.getY(), c.getHeight(), c.playerItemsN[slot], c.getId());
				c.getItems().deleteItem(itemId, slot, c.playerItemsN[slot]);
				c.getPA().sendSound(Sound.SOUND_LIST.ITEM_DROP.getSound(), 0, 8);
		} else {
				c.sendMessage("This items cannot be dropped.");
			}
		}

	}
}
