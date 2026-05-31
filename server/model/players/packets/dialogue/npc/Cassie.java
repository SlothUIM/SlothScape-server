package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.VampyreSlayer;

public class Cassie extends NPCDialogue{

	public Cassie(Player c) {
		super(c);
		// TODO Auto-generated constructor stub
	}
    @Override
    public int[] getNPCIDs() {
        return new int[] { 3214 }; // 2237 = Gee, 2238 = Donie
    }	   
    @Override
	public String getDialogueRange() {
		return "9750-9754";
	}
	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
		// TODO Auto-generated method stub
        item("You've been given another clue!", c, 2677, ""); 
        switch(startDialogueId) {
		case 9750:
	        npc(c, "Cassie", Anim.CALM_1, "I buy and sell shields, do you want to trade?");
	        c.nextChat = 9751;
	        break;
	
	    case 9751:
	    	options(c, 9751, "Yes please.", "No thank you."); // Generic filler option
	    	break;

	    case 9752:
	        player(c, Anim.CALM_1, "Yes please.");
	        c.nextChat = 9754;
	        break;
	    case 9753:
	        player(c, Anim.CALM_1, "No thank you.");
	        c.nextChat = 0;
	        break;
	    case 9754:
	        c.getShops().openShop(27);
	        c.nextChat = 0;
	        break;
        }
	}
        @Override
        public void onOption(Player c, int buttonId) {
            if (c.dialogueAction == 9751) {
                if (buttonId == OPT2_FIRST) 
                	next(c, 9752);
                else if (buttonId == OPT2_SECOND) 
                	next(c, 9753);
            }
        }
}
