package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.SheepShearer;

public class FredTheFarmer extends NPCDialogue {

    public FredTheFarmer(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 732 };
    }
    @Override
	public String getDialogueRange() {
		return "9600-9646";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        int stage = c.questStages[SheepShearer.QUEST_ID];

        switch (startDialogueId) {
            case 9600:
                npc(c, "Fred the Farmer", Anim.CALM_1, "What are you doing on my land? You're not the one", "who keeps leaving all my gates open and letting out all", "my sheep, are you?");
                c.nextChat = 9601;
                break;

            case 9601:
                if (stage == SheepShearer.NOT_STARTED) {
                    options(c, 9601, "I'm looking for a quest.", "I'm looking for something to kill.", "I'm lost.");
                } else if (stage == SheepShearer.GATHERING_WOOL) {
                    options(c, 9601, "I need to talk to you about shearing these sheep!", "I'm looking for something to kill.", "I'm lost.");
                } else if (stage == SheepShearer.COMPLETED) {
                    options(c, 9601, "I'm looking for something to kill.", "I'm lost.");
                }
                break;

            // ==========================================
            // NON-QUEST OPTIONS
            // ==========================================
            case 9602:
                player(c, Anim.CALM_1, "I'm looking for something to kill.");
                c.nextChat = 9603;
                break;
            case 9603:
                npc(c, "Fred the Farmer", Anim.ANGRY, "What, on my land? Leave my livestock alone", "you scoundrel!");
                c.nextChat = 0;
                break;

            case 9604:
                player(c, Anim.CALM_1, "I'm lost.");
                c.nextChat = 9605;
                break;
            case 9605:
                npc(c, "Fred the Farmer", Anim.CONFUSED, "How can you be lost? Just follow the road east", "and south. You'll end up in Lumbridge fairly quickly.");
                c.nextChat = 0;
                break;

            // ==========================================
            // STARTING THE QUEST
            // ==========================================
            case 9610:
                player(c, Anim.CALM_1, "I'm looking for a quest.");
                c.nextChat = 9611;
                break;
            case 9611:
                npc(c, "Fred the Farmer", Anim.CALM_1, "You're after a quest, you say? Actually, I could do", "with a bit of help.", "My sheep are getting mighty woolly. I'd be much obliged", "if you could shear them. And while you're at it,");
                c.nextChat = 9612;
                break;
            case 9612:
                npc(c, "Fred the Farmer", Anim.CALM_1, "spin the wool for me too.", "Yes, that's it. Bring me 20 balls of wool. And I'm sure I", "could sort out some sort of payment. Of course, there's", "the small matter of The Thing.");
                c.nextChat = 9613;
                break;
            case 9613:
                player(c, Anim.CONFUSED, "What do you mean, The Thing?");
                c.nextChat = 9614;
                break;
            case 9614:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Well now, no one has ever seen The Thing. That's why", "we call it The Thing, 'cos we don't know what it is.", "Some say it's a black hearted shapeshifter, hungering", "for the souls of hard working decent folk like me.");
                c.nextChat = 9615;
                break;
            case 9615:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Others say it's just a sheep.", "Well I don't have all day to stand around and gossip.", "Are you going to shear my sheep or what!");
                c.nextChat = 9616;
                break;
            case 9616:
                options(c, 9616, "Start the Sheep Shearer quest?", "Yes.", "No.");
                break;
            case 9617:
                player(c, Anim.CALM_1, "No, I'll give it a miss.");
                c.nextChat = 9618;
                break;
            case 9618:
                npc(c, "Fred the Farmer", Anim.ANNOYED, "Suit yourself.");
                c.nextChat = 0;
                break;
            case 9619:
                player(c, Anim.HAPPY, "Yes, okay. I can do that.");
                new SheepShearer(c).setStage(SheepShearer.GATHERING_WOOL);
                c.nextChat = 9620;
                break;
            case 9620:
                npc(c, "Fred the Farmer", Anim.HAPPY, "Good! Now one more thing, do you actually know", "how to shear a sheep?");
                c.nextChat = 9621;
                break;
            case 9621:
                player(c, Anim.CONFUSED, "Err. No, I don't know actually.");
                c.nextChat = 9622;
                break;
            case 9622:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Well, first things first, you need a pair of shears.", "I've got some here you can use.");
                c.nextChat = 9623;
                break;
            case 9623:
                if (!c.getItems().playerHasItem(SheepShearer.SHEARS)) {
                    c.getItems().addItem(SheepShearer.SHEARS, 1);
                    item("Fred gives you a set of sharp shears.", c, SheepShearer.SHEARS, "");
                } else {
                    npc(c, "Fred the Farmer", Anim.CALM_1, "Oh, I see you already have a pair on you! Excellent.");
                }
                c.nextChat = 9624;
                break;
            case 9624:
                npc(c, "Fred the Farmer", Anim.CALM_1, "You just need to go and use them on the sheep", "out in my field.", "Some of the sheep don't like it too much...", "Persistence is the key.");
                c.nextChat = 9625;
                break;
            case 9625:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Once you've collected some wool you can spin it", "into balls. The nearest Spinning Wheel can be found", "on the first floor of Lumbridge Castle.", "To get to Lumbridge Castle just follow the road east.");
                c.nextChat = 9626;
                break;
            case 9626:
                player(c, Anim.HAPPY, "Thank you!");
                c.nextChat = 0;
                break;

            // ==========================================
            // HANDING IN WOOL
            // ==========================================
            case 9630:
                player(c, Anim.CALM_1, "I need to talk to you about shearing these sheep!");
                c.nextChat = 9631;
                break;
            case 9631:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Oh. How are you doing getting those balls of wool?");
                c.nextChat = 9632;
                break;
            case 9632:
                int woolInInventory = c.getItems().getItemAmount(SheepShearer.BALL_OF_WOOL);
                int needed = SheepShearer.WOOL_REQUIRED - c.woolHandedIn;

                if (woolInInventory == 0) {
                    player(c, Anim.SAD, "How many more do I need to give you?");
                    c.nextChat = 9633;
                } else {
                    player(c, Anim.HAPPY, "I have some.");
                    c.nextChat = 9640;
                }
                break;
            case 9633:
                npc(c, "Fred the Farmer", Anim.CALM_1, "You need to collect " + (SheepShearer.WOOL_REQUIRED - c.woolHandedIn) + " more balls of wool.");
                c.nextChat = 9634;
                break;
            case 9634:
                player(c, Anim.SAD, "I haven't got any at the moment.");
                c.nextChat = 9635;
                break;
            case 9635:
                npc(c, "Fred the Farmer", Anim.CALM_1, "Ah well at least you haven't been eaten. You know", "what you're doing, right?");
                c.nextChat = 0;
                break;

            // Giving the wool logic
            case 9640:
                npc(c, "Fred the Farmer", Anim.HAPPY, "Give 'em here then.");
                c.nextChat = 9641;
                break;
            case 9641:
                int invWool = c.getItems().getItemAmount(SheepShearer.BALL_OF_WOOL);
                int required = SheepShearer.WOOL_REQUIRED - c.woolHandedIn;
                int amountToTake = Math.min(invWool, required); // Don't take more than he needs!

                c.getItems().deleteItem2(SheepShearer.BALL_OF_WOOL, amountToTake);
                c.woolHandedIn += amountToTake;

                item("You give Fred " + amountToTake + " balls of wool.", c, SheepShearer.BALL_OF_WOOL, "");
                
                if (c.woolHandedIn >= SheepShearer.WOOL_REQUIRED) {
                    c.nextChat = 9645; // Finished!
                } else {
                    c.nextChat = 9642; // Needs more
                }
                break;
            case 9642:
                player(c, Anim.CALM_1, "That's all I've got so far.");
                c.nextChat = 9643;
                break;
            case 9643:
                npc(c, "Fred the Farmer", Anim.CALM_1, "I need " + (SheepShearer.WOOL_REQUIRED - c.woolHandedIn) + " more before I can pay you.");
                c.nextChat = 9644;
                break;
            case 9644:
                player(c, Anim.CALM_1, "Ok I'll work on it.");
                c.nextChat = 0;
                break;
                
            // Quest complete logic
            case 9645:
                player(c, Anim.HAPPY, "That's the last of them.");
                c.nextChat = 9646;
                break;
            case 9646:
                npc(c, "Fred the Farmer", Anim.HAPPY, "I guess I'd better pay you then.");
                new SheepShearer(c).setStage(SheepShearer.COMPLETED);
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 9601) {
            int stage = c.questStages[SheepShearer.QUEST_ID];
            if (stage == SheepShearer.NOT_STARTED) {
                if (buttonId == OPT3_FIRST) next(c, 9610); // Quest
                else if (buttonId == OPT3_SECOND) next(c, 9602); // Kill
                else if (buttonId == OPT3_THIRD) next(c, 9604); // Lost
            } else if (stage == SheepShearer.GATHERING_WOOL) {
                if (buttonId == OPT3_FIRST) next(c, 9630); // Hand in
                else if (buttonId == OPT3_SECOND) next(c, 9602);
                else if (buttonId == OPT3_THIRD) next(c, 9604);
            } else if (stage == SheepShearer.COMPLETED) {
                if (buttonId == OPT2_FIRST) next(c, 9602); // Kill
                else if (buttonId == OPT2_SECOND) next(c, 9604); // Lost
            }
        } else if (c.dialogueAction == 9616) {
            if (buttonId == OPT2_FIRST) next(c, 9619);
            else if (buttonId == OPT2_SECOND) next(c, 9617);
        }
    }
}