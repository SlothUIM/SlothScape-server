package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class Luthas extends NPCDialogue {

    public Luthas(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 379 }; 
    }
    @Override
 	public String getDialogueRange() {
 		return "9740-9749";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 9740:
                npc(c, "Luthas", Anim.CALM_1, "Hello, I own this banana plantation.");
                c.nextChat = 9741;
                break;
            case 9741:
                options(c, 9741, "Could I get a job?", "I've filled the crate with bananas.", "Just passing through.");
                break;
            case 9742:
                player(c, Anim.CALM_1, "Could I get a job?");
                c.nextChat = 9743;
                break;
            case 9743:
                npc(c, "Luthas", Anim.HAPPY, "Certainly! I will pay you 30 coins to fill the crate", "outside with bananas. Just pick them from my trees.");
                c.nextChat = 0;
                break;
            case 9744:
                player(c, Anim.CALM_1, "I've filled the crate with bananas.");
                c.nextChat = 9745;
                break;
            case 9745:
                if (c.bananasInCrate >= 10) {
                    npc(c, "Luthas", Anim.HAPPY, "Excellent work! Here is your payment.");
                    c.nextChat = 9746;
                } else {
                    npc(c, "Luthas", Anim.ANNOYED, "Don't try to cheat me! The crate isn't full yet.", "It needs 10 bananas.");
                    c.nextChat = 0;
                }
                break;
            case 9746:
                c.getItems().addItem(995, 30);
                item("Luthas hands you 30 coins.", c, 995, "");
                c.nextChat = 9747;
                break;
            case 9747:
                player(c, Anim.CONFUSED, "Where does the crate go anyway?");
                c.nextChat = 9748;
                break;
            case 9748:
                npc(c, "Luthas", Anim.CALM_1, "I ship them to Wydin's food store in Port Sarim.", "He sells them in his shop.");
                // We leave c.bananasInCrate at 10 so the player can retrieve their rum in Port Sarim!
                c.nextChat = 0;
                break;
            case 9749:
                player(c, Anim.CALM_1, "Just passing through.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9741) {
            if (buttonId == OPT3_FIRST) next(c, 9742);
            else if (buttonId == OPT3_SECOND) next(c, 9744);
            else if (buttonId == OPT3_THIRD) next(c, 9749);
        }
    }
}