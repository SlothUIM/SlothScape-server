package server.model.players.skills;

import server.Config;
import server.model.players.Client;
import server.model.players.Player;
import server.model.players.Sound;

public class Prayer {

	Player c;

	/*public int[][] bonesExp = { { 526, 5 }, { 532, 15 }, { 534, 30 },
			{ 536, 72 }, { 6729, 125 } };*/
			public int[][] bonesExp = {
    {526, 4},     // Bones
    {528, 4},   // Burnt bones
    {2859, 5},     // Wolf bones
    {530, 5},   // Bat bones
    {532, 5},    // Big bones
    {534, 15},  // Babydragon bones
    {22780, 50}, // Wyrm bones
    {536, 25},    // Dragon bones
    {4830, 30},   // Fayrg bones
    {4832, 30},   // Raurg bones
    {4834, 30},   // Ourg bones
    {6729, 72},   // Dagannoth bones
    //{18830, 72},  // Dinosaur bones
   // {10977, 82.5},// Superior dragon bones
    //{10976, 150}, // Drake bones
    {22786, 200}, // Hydra bones
   // {18832, 250}, // Wyvern bones
    {20058, 250}, // Long kebbit bones
    {10977, 350}, // Curved bones
    {3123, 400}, // Shaikahan bones
    {3125, 500}, // Jogre bones
   // {14793, 750}, // Frost dragon bones
    {3183, 850}, // Monkey bones
    {3179, 950}, // Monkey bones (small)
    {3181, 1100},// Monkey bones (large)
    {3180, 1200},// Monkey bones (medium)
    {20074, 1350},// Monkey bones (medium)
    {20076, 1500},// Monkey bones (medium)
    {4812, 1560},// Zogre bones
   // {11951, 1750},// Fayre dragon bones
   // {23042, 2000},// Hydra bones (elite)
   // {23621, 2020},// Vorkath bones
    //{3127, 2500}, // Lava dragon bones
};

	public Prayer(Player player) {
		this.c = player;
	}

	public void buryBone(int id, int slot) {
		if (System.currentTimeMillis() - c.buryDelay > 1500) {
			c.getItems().deleteItem(id, slot, 1);
			c.sendMessage("You bury the bones.");
			c.getPA().addSkillXP(getExp(id) * Config.PRAYER_EXPERIENCE, 5);
			c.buryDelay = System.currentTimeMillis();
			c.getPA().sendSound(Sound.SOUND_LIST.BONE_BURY.getSound(), 0, 8);
				c.startAnimation(827);
		}
	}

	public void bonesOnAltar(int id) {
		c.getItems().deleteItem(id, c.getItems().getItemSlot(id), 1);
		c.sendMessage("The gods are pleased with your offering.");
		c.getPA().sendSound(Sound.SOUND_LIST.BONE_BURY.getSound(), 0, 8);
		c.getPA().addSkillXP(getExp(id) * 4 * Config.PRAYER_EXPERIENCE, 5);
	}

	public boolean isBone(int id) {
		for (int j = 0; j < bonesExp.length; j++)
			if (bonesExp[j][0] == id)
				return true;
		return false;
	}

	public int getExp(int id) {
		for (int j = 0; j < bonesExp.length; j++) {
			if (bonesExp[j][0] == id)
				return bonesExp[j][1];
		}
		return 0;
	}
}