package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class Wydin extends NPCDialogue {

    public Wydin(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 2890 }; 
    }	   
    @Override
 	public String getDialogueRange() {
 		return "9770-9779";
 	}

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 9770:
                npc(c, "Wydin", Anim.CALM_1, "Welcome to my food store! Would you like to buy anything?");
                c.nextChat = 9771;
                break;
            case 9771:
                options(c, 9771, "Yes please.", "Can I get a job here?", "No thanks.");
                break;
            case 9772:
                player(c, Anim.CALM_1, "Yes please.");
                c.nextChat = 9773;
                break;
            case 9773:
                c.getShops().openShop(17); // Change this 17 to whatever Wydin's shop ID is in your shops.cfg!
                c.nextChat = 0;
                break;
            case 9774:
                player(c, Anim.CALM_1, "Can I get a job here?");
                c.nextChat = 9775;
                break;
            case 9775:
                npc(c, "Wydin", Anim.CALM_1, "Well, I do need some help in the back room.", "But company policy says all employees must wear", "a white apron. Have you got one?");
                c.nextChat = 9776;
                break;
            case 9776:
                if (c.playerEquipment[c.playerChest] == 1005 || c.getItems().playerHasItem(1005)) {
                    player(c, Anim.HAPPY, "Yes, I have one right here.");
                    c.nextChat = 9777;
                } else {
                    player(c, Anim.SAD, "No, I don't.");
                    c.nextChat = 9778;
                }
                break;
            case 9777:
                npc(c, "Wydin", Anim.HAPPY, "Great! You're hired. Go on through to the back", "and tidy up those crates for me.");
                c.nextChat = 0;
                break;
            case 9778:
                npc(c, "Wydin", Anim.CALM_1, "Well, you can't work here without one. Come back", "when you are properly dressed.");
                c.nextChat = 0;
                break;
            case 9779:
                player(c, Anim.CALM_1, "No thanks.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9771) {
            if (buttonId == OPT3_FIRST) next(c, 9772);
            else if (buttonId == OPT3_SECOND) next(c, 9774);
            else if (buttonId == OPT3_THIRD) next(c, 9779);
        }
    }
}