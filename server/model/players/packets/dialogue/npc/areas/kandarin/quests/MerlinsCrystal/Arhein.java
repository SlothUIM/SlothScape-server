package server.model.players.packets.dialogue.npc.areas.kandarin.quests.MerlinsCrystal;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class Arhein extends NPCDialogue {

    public Arhein(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3200; // Arhein
    }

    @Override
    public String getDialogueRange() {
        return "85500-85699";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {

            // ==========================================
            // ARHEIN STANDARD DIALOGUE
            // ==========================================
            case 85500:
                npc(c, "Arhein", Anim.CALM_1, "Hello! Would you like to trade?");
                c.nextChat = 85501;
                break;
            case 85501:
                options3(c, 85501, "Yes.", "No thank you.", "Is that your ship?");
                break;
            case 85502:
                player(c, Anim.CALM_1, "No thank you.");
                c.nextChat = -1;
                break;

            case 85510:
                player(c, Anim.THINKING, "Is that your ship?");
                c.nextChat = 85511;
                break;
            case 85511:
                npc(c, "Arhein", Anim.CALM_1, "Yes, I use it to make deliveries to my customers up and", "down the coast. These crates here are all ready for my", "next trip.");
                c.nextChat = 85512;
                break;
            case 85512:
                options(c, 85512, "Do you deliver to the fort just down the coast?", "Where do you deliver to?");
                break;

            case 85520:
                player(c, Anim.THINKING, "Do you deliver to the fort just down the coast?");
                c.nextChat = 85521;
                break;
            case 85521:
                npc(c, "Arhein", Anim.CALM_1, "Yes, I do have orders to deliver there from time to time.", "I think I may have some bits and pieces for them when", "I leave here next actually.");
                c.nextChat = 85522;
                break;
            case 85522:
                options(c, 85522, "Can you drop me off on the way down please?", "Aren't you worried about supplying evil knights?");
                break;

            case 85530:
                player(c, Anim.THINKING, "Can you drop me off on the way down please?");
                c.nextChat = 85531;
                break;
            case 85531:
                npc(c, "Arhein", Anim.SAD, "I don't think Sir Mordred would like that. He wants as few", "outsiders visiting as possible. I wouldn't want to lose", "his business.");
                c.nextChat = -1;
                break;

            case 85540:
                player(c, Anim.THINKING, "Aren't you worried about supplying evil knights?");
                c.nextChat = 85541;
                break;
            case 85541:
                npc(c, "Arhein", Anim.HAPPY, "Hey, you gotta take business where you can find it these", "days! Besides, if I didn't supply them, someone else", "would.");
                c.nextChat = -1;
                break;

            case 85550:
                player(c, Anim.THINKING, "Where do you deliver to?");
                c.nextChat = 85551;
                break;
            case 85551:
                npc(c, "Arhein", Anim.CALM_1, "Oh, various places up and down the coast. Mostly", "Karamja and Port Sarim.");
                c.nextChat = 85552;
                break;
            case 85552:
                options3(c, 85552, "I don't suppose I could get a lift anywhere?", "Well, good luck with your business.", "Are you rich then?");
                break;

            case 85560:
                player(c, Anim.THINKING, "I don't suppose I could get a lift anywhere?");
                c.nextChat = 85561;
                break;
            case 85561:
                npc(c, "Arhein", Anim.SAD, "Sorry pal, but I'm afraid I'm not quite ready to sail yet.", "I'm waiting on a big delivery of candles which I need", "to deliver further along the coast.");
                c.nextChat = -1;
                break;

            case 85570:
                player(c, Anim.HAPPY, "Well, good luck with your business.");
                c.nextChat = 85571;
                break;
            case 85571:
                npc(c, "Arhein", Anim.HAPPY, "Thanks buddy!");
                c.nextChat = -1;
                break;

            case 85580:
                player(c, Anim.THINKING, "Are you rich then?");
                c.nextChat = 85581;
                break;
            case 85581:
                npc(c, "Arhein", Anim.HAPPY, "Business is going reasonably well... I wouldn't say I was", "the richest of merchants ever, but I'm doing fairly well", "all things considered.");
                c.nextChat = -1;
                break;

            // ==========================================
            // CRATE INFILTRATION SEQUENCE (Object 63)
            // ==========================================
            case 85600:
                c.getDH().sendStatement("The crate is empty. It's just about big enough to hide inside.");
                c.nextChat = 85601;
                break;
            case 85601:
                options(c, 85601, "Would you like to hide inside the crate?", "Yes.", "No.");
                break;
            case 85602: // Yes
                c.getDH().sendStatement("You climb inside the crate and wait.", "And wait...");
                c.nextChat = 85603;
                break;
            case 85603:
                c.getDH().sendStatement("You hear voices outside the crate.", "Is this your crate, Arhein?", "Yeah, I think so. Pack it aboard soon as you can.", "I'm on a tight schedule for deliveries!");
                c.nextChat = 85604;
                break;
            case 85604:
                c.getDH().sendStatement("You feel the crate being lifted.", "Oof. Wow, this is pretty heavy!", "I never knew candles weighed so much!");
                c.nextChat = 85605;
                break;
            case 85605:
                c.getDH().sendStatement("Quit your whining, and stow it in the hold.", "You feel the crate being put down inside the ship.");
                c.nextChat = 85606;
                break;
            case 85606:
                c.getDH().sendStatement("Casting off!", "You feel the ship start to move.", "Feels like you're now out at sea.");
                c.nextChat = 85607;
                break;
            case 85607:
                c.getDH().sendStatement("The ship comes to a stop.", "Unload Mordred's deliveries onto the jetty. Aye-aye cap'n!");
                c.nextChat = 85608;
                break;
            case 85608:
                c.getDH().sendStatement("You feel the crate being lifted.", "You can hear someone mumbling outside the crate.", "...stupid Arhein... making me... candles... never weigh", "THIS much....hurts....union about this!...");
                c.nextChat = 85609;
                break;
            case 85609:
                c.getDH().sendStatement("You feel the crate being put down.");
                c.nextChat = 85610;
                break;
            case 85610:
                options(c, 85610, "Would you like to get back out of the crate?", "Yes.", "No.");
                break;
            case 85611:
                c.getDH().sendStatement("You climb out of the crate.");
                c.nextChat = -1;

                // TODO: Verify Keep Le Faye ground floor coordinates in your server
                c.getPA().movePlayer(2756, 3399, 0);
                break;

            case 85612: // No, wait longer
                c.getDH().sendStatement("You wait.", "And wait...", "And wait...");
                c.nextChat = 85610; // Loops back to the prompt
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        switch (c.dialogueAction) {
            case 85501:
                if (buttonId == OPT3_FIRST) {
                    c.getShops().openShop(16); // Arhein's Shop
                } else if (buttonId == OPT3_SECOND) {
                    next(c, 85502);
                } else if (buttonId == OPT3_THIRD) {
                    next(c, 85510);
                }
                break;
            case 85512:
                if (buttonId == OPT2_FIRST) next(c, 85520);
                if (buttonId == OPT2_SECOND) next(c, 85550);
                break;
            case 85522:
                if (buttonId == OPT2_FIRST) next(c, 85530);
                if (buttonId == OPT2_SECOND) next(c, 85540);
                break;
            case 85552:
                if (buttonId == OPT3_FIRST) next(c, 85560);
                if (buttonId == OPT3_SECOND) next(c, 85570);
                if (buttonId == OPT3_THIRD) next(c, 85580);
                break;
            case 85601: // First crate prompt
                if (buttonId == OPT2_FIRST) next(c, 85602);
                if (buttonId == OPT2_SECOND) c.getPA().closeAllWindows();
                break;
            case 85610: // Arrival crate prompt
                if (buttonId == OPT2_FIRST) next(c, 85611); // Climb out
                if (buttonId == OPT2_SECOND) next(c, 85612); // Wait more
                break;
        }
    }
}