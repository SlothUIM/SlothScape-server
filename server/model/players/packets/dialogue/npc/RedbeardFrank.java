package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.PiratesTreasure;

public class RedbeardFrank extends NPCDialogue {

    public RedbeardFrank(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 375 }; 
    }
    @Override
 	public String getDialogueRange() {
 		return "9700-9727";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int stage = c.questStages[PiratesTreasure.QUEST_ID];

        switch (startDialogueId) {
            case 9700:
                npc(c, "Redbeard Frank", Anim.HAPPY, "Arr, Matey!");
                c.nextChat = 9701;
                break;

            case 9701:
                if (stage == PiratesTreasure.NOT_STARTED) {
                    options(c, 9701, "I'm in search of treasure.", "Nothing, nevermind.");
                } else if (stage == PiratesTreasure.GATHERING_RUM) {
                    npc(c, "Redbeard Frank", Anim.CALM_1, "Have ye brought some rum for yer old mate Frank?");
                    c.nextChat = 9720;
                } else if (stage == PiratesTreasure.FIND_HECTORS_CHEST || stage == PiratesTreasure.DIG_FOR_TREASURE) {
                    npc(c, "Redbeard Frank", Anim.CALM_1, "Arrr, go find that chest in the Blue Moon Inn!", "Hector's stash awaits ye!");
                    c.nextChat = 0;
                } else if (stage == PiratesTreasure.COMPLETED) {
                    npc(c, "Redbeard Frank", Anim.HAPPY, "Arrr, I see ye found the treasure!", "Enjoy yer spoils, matey!");
                    c.nextChat = 0;
                }
                break;

            // ==========================================
            // STARTING THE QUEST
            // ==========================================
            case 9710:
                player(c, Anim.HAPPY, "I'm in search of treasure.");
                c.nextChat = 9711;
                break;
            case 9711:
                npc(c, "Redbeard Frank", Anim.CALM_1, "Arr, treasure you say?", "Well, I might know where some is buried.", "But I need a favor first.");
                c.nextChat = 9712;
                break;
            case 9712:
                player(c, Anim.CONFUSED, "What kind of favor?");
                c.nextChat = 9713;
                break;
            case 9713:
                npc(c, "Redbeard Frank", Anim.CALM_1, "I've a mighty thirst for some Karamjan rum.", "Bring me a bottle, and I'll tell ye where", "the treasure is.");
                c.nextChat = 9714;
                break;
            case 9714:
                options(c, 9714, "Ok, I will bring you some rum.", "No, I'm not getting you rum.");
                break;
            case 9715:
                player(c, Anim.HAPPY, "Ok, I will bring you some rum.");
                new PiratesTreasure(c).setStage(PiratesTreasure.GATHERING_RUM);
                c.nextChat = 9716;
                break;
            case 9716:
                npc(c, "Redbeard Frank", Anim.HAPPY, "Yer a good mate! Remember, it has to be", "Karamjan rum. None of that cheap swill!");
                c.nextChat = 0;
                break;
            case 9717:
                player(c, Anim.ANGRY, "No, I'm not getting you rum.");
                c.nextChat = 9718;
                break;
            case 9718:
                npc(c, "Redbeard Frank", Anim.ANNOYED, "Suit yerself, landlubber!");
                c.nextChat = 0;
                break;

            // ==========================================
            // HANDING IN THE RUM
            // ==========================================
            case 9720:
                if (c.getItems().playerHasItem(PiratesTreasure.KARAMJAN_RUM)) {
                    player(c, Anim.HAPPY, "Yes, I've got some right here.");
                    c.nextChat = 9722;
                } else {
                    player(c, Anim.SAD, "No, not yet.");
                    c.nextChat = 9721;
                }
                break;
            case 9721:
                npc(c, "Redbeard Frank", Anim.ANNOYED, "Well get to it then! A pirate needs his rum!");
                c.nextChat = 0;
                break;
            case 9722:
                c.getItems().deleteItem(PiratesTreasure.KARAMJAN_RUM, 1);
                item("You give Redbeard Frank a bottle of Karamjan rum.", c, PiratesTreasure.KARAMJAN_RUM, "");
                c.nextChat = 9723;
                break;
            case 9723:
                npc(c, "Redbeard Frank", Anim.HAPPY, "Ahhh, that's the stuff! Good work, matey!");
                c.nextChat = 9724;
                break;
            case 9724:
                player(c, Anim.CONFUSED, "So, what about the treasure?");
                c.nextChat = 9725;
                break;
            case 9725:
                npc(c, "Redbeard Frank", Anim.CALM_1, "Right you are. A pirate always keeps his word.", "A while back, an old shipmate of mine by the name", "of One-Eyed Hector was killed.");
                c.nextChat = 9726;
                break;
            case 9726:
                npc(c, "Redbeard Frank", Anim.CALM_1, "He had a chest full of treasure, which he kept in", "his room at the Blue Moon Inn in Varrock.", "I managed to get his key. Here, take it.");
                c.nextChat = 9727;
                break;
            case 9727:
                if (c.getItems().freeSlots() > 0) {
                    c.getItems().addItem(PiratesTreasure.HECTORS_KEY, 1);
                    item("Frank hands you a small, rusty key.", c, PiratesTreasure.HECTORS_KEY, "");
                    new PiratesTreasure(c).setStage(PiratesTreasure.FIND_HECTORS_CHEST);
                    c.nextChat = 0;
                } else {
                    npc(c, "Redbeard Frank", Anim.ANNOYED, "Yer bags are full, matey! Make some room", "and talk to me again.");
                    c.nextChat = 0;
                }
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9701) {
            if (buttonId == OPT2_FIRST) next(c, 9710);
            // Doing nothing on OPT2_SECOND naturally closes the dialogue!
        } else if (c.dialogueAction == 9714) {
            if (buttonId == OPT2_FIRST) next(c, 9715);
            else if (buttonId == OPT2_SECOND) next(c, 9717);
        }
    }
}