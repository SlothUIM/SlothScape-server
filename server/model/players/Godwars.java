package server.model.players;

import server.model.players.Client;

/**
	Zamorakian spear
	Godsword (with Zamorak hilt attached)
	Cape of Zamorak (mage arena)
	Staff of Zamorak (mage arena)
	Zamorak vestments: robe top, robe legs, mitre, cloak, stole, or crozier (treasure trails)
	Zamorak d'hide, coif, chaps, or bracers (treasure trails)
	Zamorak platebody, full helm, platelegs, plateskirt, or kiteshield (treasure trails)
	Zamorak robe top or bottom
	Unholy symbol
	Unholy book (Horror from the Deep quest)
	Zamorak mjolnir (Making History quest)
	*/
	
/**
* @author Aintaro
*/

public class Godwars {
	
	
	/**
	* An array of zamorak items
	*/
	private static final int[] zamorakItems =  {11716, 11700, 2414, 2417, 2653, 2655, 2657, 2659, 3478, 3674, 3841, 3842,
										3852, 4683, 6764, 8056, 10368, 10370, 10372, 10374, 10444, 10450, 10456,
										10460, 10468, 10474, 10776, 10786, 10790};
	private static final int[] saradominItems = {1718, 2412, 2415, 2661, 2663, 2665, 2667, 3479, 3675, 3489, 3840, 4682,
										6762, 8055, 10384, 10386, 10388, 10390, 10440, 10446, 10452, 10458, 10464, 10470,
										11181, 11698, 11730};
	private static final int[] bandosItems = {11696, 11724, 11726, 11728};
	private static final int[] armadylItems = {87, 11694 ,11718, 11720, 11722, 12670, 12671};
	
	/**
	* @return true if player is wearing zamorak items else return false
	*/
	public static boolean isWearingZamorakItems(Player c) {
		for (int i = 0; i < zamorakItems.length;i++) {
			for (int x = 0; x < c.playerEquipment.length; x++) {
				if (c.playerEquipment[x] == zamorakItems[i]) {
					return true;
				}
			}
		}
		return false;
	}
	/**
	* @return true if player is wearing saradomin items else return false
	*/
	public static boolean isWearingSaradominItems(Player c) {
		for (int i = 0; i < saradominItems.length;i++) {
			for (int x = 0; x < c.playerEquipment.length; x++) {
				if (c.playerEquipment[x] == saradominItems[i]) {
					return true;
				}
			}
		}
		return false;
	}
    /**
     * @return true if player is wearing bandos items else return false
     */
    public static boolean isWearingBandosItems(Player c) {
        for (int i = 0; i < bandosItems.length;i++) {
            for (int x = 0; x < c.playerEquipment.length; x++) {
                if (c.playerEquipment[x] == bandosItems[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return true if player is wearing bandos items else return false
     */
    public static boolean isWearingArmadylItems(Player c) {
        for (int i = 0; i < armadylItems.length;i++) {
            for (int x = 0; x < c.playerEquipment.length; x++) {
                if (c.playerEquipment[x] == armadylItems[i]) {
                    return true;
                }
            }
        }
        return false;
    }
}