package server.model.players.skills;

import server.Config;
import server.model.players.Client;
import server.model.players.Player;
import server.event.*;
import server.event.Event;

/**
 * RuneCrafting.java
 * 
 * @author Sanity
 * 
 **/

public class Runecrafting {

	private Player c;

	public Runecrafting(Player player) {
		this.c = player;
	}

	/**
	 * Rune essence ID constant.
	 */
	private static final int RUNE_ESS = 1436;

	/**
	 * Pure essence ID constant.
	 */
	private static final int PURE_ESS = 7936;

	/**
	 * An array containing the rune item numbers.
	 */
	public int[] runes = { 556, 558, 555, 557, 554, 559, 564, 562, 561, 563,
			560, 565 };
	public int[][] talismans = { 
	{1438}, //air
	{1440}, //earth
	{1442}, //fire
	{1444}, //water
	{1446}, //body
	{1448}, //mind
	{1450}, //blood
	{1452}, //chaos
	{1454}, //cosmic
	{1456}, //death
	{1458}, //law
	{1462}, //nature
	};
	public int[][] tiaras = { 
	{5527}, //air
	{5535}, //earth
	{5537}, //fire
	{5531}, //water
	{5533}, //body
	{5529}, //mind
	{5549}, //blood
	{5543}, //chaos
	{5539}, //cosmic
	{5547}, //death
	{5545}, //law
	{5541}, //nature
	};
	public final int[][] overworldAltar = {
		//ID, startX, startY, FinalX, FinalY
		{2452, 2986, 3294, 2845, 4832}, //air
		{2455, 3305, 3472, 2660, 4839}, //earth
		{2456, 3312, 3253, 2584, 4836}, //fire
		{2454, 3183, 3164, 2713, 4836}, //water
		{2457, 3054, 3443, 2527, 4833}, //body
		{2453, 2981, 3513, 2796, 4818}, //mind
		{2464, 3561, 9774, 2467, 4888}, //blood
		{2461, 3059, 3589, 2269, 4843}, //chaos
		{2458, 2409, 4379, 2162, 4833}, //cosmic
		{2462, 1863, 4639, 2207, 4836}, //death
		{2459, 2857, 3379, 2464, 4834}, //law
		{2460, 2869, 3021, 2398, 4841} //nature
		};
		public void TalismanOnAltar(int itemId, int objectId) {
			for(int i = 0; i < talismans.length; i++) {
				int j = talismans[i][0];
				if(itemId == j && objectId == overworldAltar[i][0]){
					c.startAnimation(1670, 0);
					c.sendMessage("A mysterious force grabs hold of you.");
					final int ui = i;
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					public void execute(CycleEventContainer container) {
						container.stop();
					}
					public void stop() {
						c.getPA().movePlayer(overworldAltar[ui][3], overworldAltar[ui][4], 0);
					}
				}, 4);
				}
			}
		}
	/**
	 * An array containing the object IDs of the runecrafting altars.
	 */
	public int[] altarID = { 2478, 2479, 2480, 2481, 2482, 2483, 2484, 2487,
			2486, 2485, 2488, 2489 };

	/**
	 * 2D Array containing the levels required to craft the specific rune.
	 */
	public int[][] craftLevelReq = { { 556, 1 }, { 558, 2 }, { 555, 5 },
			{ 557, 9 }, { 554, 14 }, { 559, 20 }, { 564, 27 }, { 562, 35 },
			{ 561, 44 }, { 563, 54 }, { 560, 65 }, { 565, 77 } };

	/**
	 * 2D Array containing the levels that you can craft multiple runes.
	 */
	public int[][] multipleRunes = { { 11, 22, 33, 44, 55, 66, 77, 88, 99 },
			{ 14, 28, 42, 56, 70, 84, 98 }, { 19, 38, 57, 76, 95 },
			{ 26, 52, 78 }, { 35, 70 }, { 46, 92 }, { 59 }, { 74 }, { 91 },
			{ 100 }, { 100 }, { 100 } };

	public int[] runecraftExp = { 5, 6, 6, 7, 7, 8, 9, 9, 10, 11, 11, 11 };

	/**
	 * Checks through all 28 item inventory slots for the specified item.
	 */
	private boolean itemInInv(int itemID, int slot, boolean checkWholeInv) {
		if (checkWholeInv) {
			for (int i = 0; i < 28; i++) {
				if (c.playerItems[i] == itemID + 1) {
					return true;
				}
			}
		} else {
			if (c.playerItems[slot] == itemID + 1) {
				return true;
			}
		}
		return false;
	}
	public boolean isTalisman(int id) {
		for (int j = 0; j < talismans.length; j++)
			if (talismans[j][0] == id)
				return true;
		return false;
	}
	public boolean isTiaras(int id) {
		for (int j = 0; j < tiaras.length; j++)
			if (tiaras[j][0] == id)
				return true;
		return false;
	}
	/**
	 * Replaces essence in the inventory with the specified rune.
	 */
	private void replaceEssence(int essType, int runeID, int multiplier,
			int index) {
		System.out.println("multipler: " + multiplier);
		int exp = 0;
		for (int i = 0; i < 28; i++) {
			if (itemInInv(essType, i, false)) {
				c.getItems().deleteItem(essType, i, 1);
				c.getItems().addItem(runeID, 1 * multiplier);
				exp += runecraftExp[index];
			}
		}
		c.getPA().addSkillXP(exp * Config.RUNECRAFTING_EXPERIENCE,
				c.playerRunecrafting);
	}

	/**
	 * Crafts the specific rune.
	 */
	public void craftRunes(int altarID) {
		int runeID = 0;

		for (int i = 0; i < this.altarID.length; i++) {
			if (altarID == this.altarID[i]) {
				runeID = runes[i];
			}
		}
		for (int i = 0; i < craftLevelReq.length; i++) {
			if (runeID == runes[i]) {
				if (c.playerLevel[20] >= craftLevelReq[i][1]) {
					if (c.getItems().playerHasItem(RUNE_ESS)
							|| c.getItems().playerHasItem(PURE_ESS)) {
						int multiplier = 1;
						for (int j = 0; j < multipleRunes[i].length; j++) {
							if (c.playerLevel[20] >= multipleRunes[i][j]) {
								multiplier += 1;
							}
						}
						replaceEssence(RUNE_ESS, runeID, multiplier, i);
						c.startAnimation(791);
						// c.frame174(481, 0, 0); for sound
						c.gfx100(186);
						return;
					}
					c.sendMessage("You need to have essence to craft runes!");
					return;
				}
				c.sendMessage("You need a Runecrafting level of "
						+ craftLevelReq[i][1] + " to craft this rune.");
			}
		}
	}

}