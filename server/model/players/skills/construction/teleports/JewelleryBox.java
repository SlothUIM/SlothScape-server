package server.model.players.skills.construction.teleports;

import server.model.players.Player;

public class JewelleryBox {

	
	public static void openTeleportMenu(Player c, int tier)
	{
		/*Ring of Dueling*/
		c.getPA().sendConfig(439, 0);
		c.getPA().sendFrame126("", 62420);
		c.getPA().itemOnInterface(c, 62421, 0, -1, 1);
		c.getPA().sendFrame126("", 62422);
		c.getPA().sendFrame126("", 62423);
		c.getPA().sendFrame126("", 62424);
		c.getPA().sendFrame126("", 62425);
		/**/
		/*Game Necklace*/
		c.getPA().sendConfig(440, 0);
		c.getPA().sendFrame126("", 62429);
		c.getPA().itemOnInterface(c, 62430, 0, -1, 1);
		c.getPA().sendFrame126("", 62431);
		c.getPA().sendFrame126("", 62432);
		c.getPA().sendFrame126("", 62433);
		c.getPA().sendFrame126("", 62434);
		c.getPA().sendFrame126("", 62435);
		/**/
		/*combat bracelet*/
		c.getPA().sendConfig(441, 0);
		c.getPA().sendFrame126("", 62438);
		c.getPA().itemOnInterface(c, 62439, 0, -1, 1);
		c.getPA().sendFrame126("", 62440);
		c.getPA().sendFrame126("", 62441);
		c.getPA().sendFrame126("", 62442);
		c.getPA().sendFrame126("", 62443);
		/**/
		/*Skills necklace*/
		c.getPA().sendConfig(442, 0);
		c.getPA().sendFrame126("", 62447);
		c.getPA().itemOnInterface(c, 62448, 0, -1, 1);
		c.getPA().sendFrame126("", 62449);
		c.getPA().sendFrame126("", 62450);
		c.getPA().sendFrame126("", 62451);
		c.getPA().sendFrame126("", 62452);
		c.getPA().sendFrame126("", 62453);
		/**/
		/*Ring of wealth*/
		c.getPA().sendConfig(443, 0);
		c.getPA().sendFrame126("", 62456);
		c.getPA().itemOnInterface(c, 62457, 0, -1, 1);
		c.getPA().sendFrame126("", 62458);
		c.getPA().sendFrame126("", 62459);
		c.getPA().sendFrame126("", 62460);
		c.getPA().sendFrame126("", 62461);
		/**/
		/*amulet of glory*/
		c.getPA().sendConfig(444, 0);
		c.getPA().sendFrame126("", 62465);
		c.getPA().itemOnInterface(c, 62466, 0, -1, 1);
		c.getPA().sendFrame126("", 62467);
		c.getPA().sendFrame126("", 62468);
		c.getPA().sendFrame126("", 62469);
		c.getPA().sendFrame126("", 62470);
		if(tier >= 0) {
			/*Ring of Dueling*/
			c.getPA().sendConfig(439, 1);
			c.getPA().sendFrame126("Ring of\\nDueling", 62420);
			c.getPA().itemOnInterface(c, 62421, 0, 2552, 1);
			c.getPA().sendFrame126("Duel Arena", 62422);
			c.getPA().sendFrame126("Castle Wars", 62423);
			c.getPA().sendFrame126("Ferox Enclave", 62424);
			/**/
			/*Game Necklace*/
			c.getPA().sendConfig(440, 1);
			c.getPA().sendFrame126("Games\\nNecklace", 62429);
			c.getPA().itemOnInterface(c, 62430, 0, 3853, 1);
			c.getPA().sendFrame126("Burthorpe Games Room", 62431);
			c.getPA().sendFrame126("Barbarian Outpost", 62432);
			c.getPA().sendFrame126("Corporeal Beast", 62433);
			c.getPA().sendFrame126("Tears of Guthix", 62434);
			c.getPA().sendFrame126("Wintertodt Camp", 62435);
			/**/
		}
		if(tier >= 1) {
			/*combat bracelet*/
			c.getPA().sendConfig(441, 1);
			c.getPA().sendFrame126("Combat\\nbracelet", 62438);
			c.getPA().itemOnInterface(c, 62439, 0, 11118, 1);
			c.getPA().sendFrame126("Warriors' Guild", 62440);
			c.getPA().sendFrame126("Champions' Guild", 62441);
			c.getPA().sendFrame126("Monastery", 62442);
			c.getPA().sendFrame126("Ranging Guild", 62443);
			/**/
			/*Skills necklace*/
			c.getPA().sendConfig(442, 1);
			c.getPA().sendFrame126("Skills\\nnecklace", 62447);
			c.getPA().itemOnInterface(c, 62448, 0, 11105, 1);
			c.getPA().sendFrame126("Fishing Guild", 62449);
			c.getPA().sendFrame126("Motherlode Mine", 62450);
			c.getPA().sendFrame126("Crafting Guild", 62451);
			c.getPA().sendFrame126("Cooking Guild", 62452);
			c.getPA().sendFrame126("Woodcutting Guild", 62453);
			/**/
		}
		if(tier >= 2) {
			/*Ring of wealth*/
			c.getPA().sendConfig(443, 1);
			c.getPA().sendFrame126("Ring of\\nWealth", 62456);
			c.getPA().itemOnInterface(c, 62457, 0, 11980, 1);
			c.getPA().sendFrame126("Miscellania", 62458);
			c.getPA().sendFrame126("Grand Exchange", 62459);
			c.getPA().sendFrame126("Falador Park", 62460);
			c.getPA().sendFrame126("Dondakan's Rock", 62461);
			/**/
			/*amulet of glory*/
			c.getPA().sendConfig(444, 1);
			c.getPA().sendFrame126("Amulet of\\nGlory", 62465);
			c.getPA().itemOnInterface(c, 62466, 0, 11978, 1);
			c.getPA().sendFrame126("Edgeville", 62467);
			c.getPA().sendFrame126("Karamja", 62468);
			c.getPA().sendFrame126("Draynor Village", 62469);
			c.getPA().sendFrame126("Al Kharid", 62470);
		}
		c.getPA().showInterface(62415);	
	}
}
