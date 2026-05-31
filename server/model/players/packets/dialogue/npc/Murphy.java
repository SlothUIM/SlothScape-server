package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.util.Misc;

public class Murphy extends NPCDialogue {

    /**
     * Define the Quest ID for Recipe for Disaster (Pirate Pete)
     * Adjust this index to match your questStages array.
     */
    private static final int RFD_QUEST_ID = 10; 

    public Murphy(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 5607; 
    }

    @Override
    public String getDialogueRange() {
        return "Custom: 40500 - 40680";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        // Dynamic Gender Check for Murphy's unique "Game Magic" dialogue
        String gender = c.playerAppearance[0] == 0 ? "son" : "lass";

        switch (startDialogueId) {

            // ==========================================
            // ENTRY POINT - LOGIC BRANCHING
            // ==========================================
            case 40500:
                // 1. Check Recipe for Disaster (Pirate Pete Subquest)
                if (c.questStages[RFD_QUEST_ID] == 5) { 
                    next(c, 40620);
                    return;
                }

                // 2. Check if first time or returning
                if (!c.hasTalkedToMurphy) { 
                    player(c, Anim.CALM_1, "Good day to you Sir.");
                    c.nextChat = 40501;
                } else {
                    player(c, Anim.CALM_1, "Hello again Murphy.");
                    c.nextChat = 40590;
                }
                break;

            // ==========================================
            // INITIAL CONVERSATION
            // ==========================================
            case 40501:
                npc(c, "Murphy", Anim.CALM_1, "Well hello my brave adventurer.");
                c.nextChat = 40502;
                break;
            case 40502:
                player(c, Anim.CALM_1, "What are you up to?");
                c.nextChat = 40503;
                break;
            case 40503:
                npc(c, "Murphy", Anim.HAPPY, "Getting ready to go fishing of course. There's no time to waste!");
                c.nextChat = 40504;
                break;
            case 40504:
                npc(c, "Murphy", Anim.CALM_1, "I've got all the supplies I need from the shop", "at the end of the pier. They sell good rope, hammers and axes", "although their bailing buckets aren't too effective.");
                c.nextChat = 40505;
                break;
            case 40505:
                options3(c, 40505, "What fish do you catch?", "Your boat doesn't look too safe.", "Could I help?");
                break;

            case 40506:
                player(c, Anim.CALM_1, "What fish do you catch?");
                c.nextChat = 40507;
                break;
            case 40507:
                npc(c, "Murphy", Anim.CALM_1, "I get all sorts, anything that lies on the sea bed,", "you never know what you're going to get until", "you pull up the net!");
                c.nextChat = 40505; 
                break;

            case 40508:
                player(c, Anim.WORRIED, "Your boat doesn't look too safe.");
                c.nextChat = 40509;
                break;
            case 40509:
                npc(c, "Murphy", Anim.CALM_1, "That's because it's not, the darn thing's full of holes.");
                c.nextChat = 40510;
                break;
            case 40510:
                player(c, Anim.CALM_1, "Oh, so I suppose you can't go out for a while?");
                c.nextChat = 40511;
                break;
            case 40511:
                npc(c, "Murphy", Anim.HAPPY, "Oh no, I don't let a few holes stop an experienced", "sailor like me. I could sail these seas in a barrel,", "I'll be going out soon enough.");
                c.nextChat = 40505;
                break;

            // ==========================================
            // THE TRAWLER EXPLANATION
            // ==========================================
            case 40520:
                player(c, Anim.CALM_1, "Could I help?");
                c.nextChat = 40521;
                break;
            case 40521:
                npc(c, "Murphy", Anim.CALM_1, "Well of course you can! I'll warn you though,", "the seas are merciless and without fishing experience", "you won't catch much.");
                c.nextChat = 40522;
                break;
            case 40522:
                npc(c, "Murphy", Anim.CALM_1, "You need a fishing level of 15 or above", "to catch any fish on the trawler.");
                c.nextChat = 40523;
                break;
            case 40523:
                npc(c, "Murphy", Anim.CALM_1, "On occasions the net rips, so you'll need some rope to repair it.");
                c.nextChat = 40524;
                break;
            case 40524:
                npc(c, "Murphy", Anim.CALM_1, "Repairing the net is difficult in the harsh conditions", "so you'll find it easier with a higher crafting level.");
                c.nextChat = 40525;
                break;
            case 40525:
                player(c, Anim.CALM_1, "Right...ok.");
                c.nextChat = 40526;
                break;
            case 40526:
                npc(c, "Murphy", Anim.CALM_1, "There's also a slight problem with leaks.");
                c.nextChat = 40527;
                break;
            case 40527:
                player(c, Anim.SAD, "Leaks?!");
                c.nextChat = 40528;
                break;
            case 40528:
                npc(c, "Murphy", Anim.HAPPY, "Nothing some swamp paste won't fix...");
                c.nextChat = 40529;
                break;
            case 40529:
                player(c, Anim.CALM_1, "Swamp paste?");
                c.nextChat = 40530;
                break;
            case 40530:
                npc(c, "Murphy", Anim.CALM_1, "Also, there's the small matter of the Kraken.");
                c.nextChat = 40531;
                break;
            case 40531:
                player(c, Anim.CALM_1, "You have got to be kidding me?");
                c.nextChat = 40532;
                break;
            case 40532:
                npc(c, "Murphy", Anim.CALM_1, "Occupational hazard. I usually whack it with an axe", "a few times and fix the damage with a hammer once it's gone.");
                c.nextChat = 40533;
                break;
            case 40533:
                npc(c, "Murphy", Anim.CALM_1, "Oh, and one more thing... I hope you're a good swimmer?");
                c.nextChat = 40534;
                break;
            case 40534:
                options(c, 40534, "Actually, I think I'll leave it.", "I'll be fine, let's go.");
                break;

            case 40535:
                npc(c, "Murphy", Anim.CALM_1, "Bloomin' land lovers!!!");
                end(c);
                break;

            case 40540:
                player(c, Anim.HAPPY, "I'll be fine, let's go.");
                if (c.TrawlerInPort) {
                    c.nextChat = 40541;
                } else {
                    c.nextChat = 40542;
                }
                break;

            // ==========================================
            // PORT VS SEA & GAME MAGIC
            // ==========================================
            case 40541:
                npc(c, "Murphy", Anim.HAPPY, "Aye aye! Meet me on board the trawler.", "I just need to get a few things together.");
                end(c);
                break;
            case 40542:
                npc(c, "Murphy", Anim.HAPPY, "Aye aye! Meet me on board the trawler.");
                c.nextChat = 40543;
                break;
            case 40543:
                npc(c, "Murphy", Anim.CALM_1, "Oh hang on. I'm already on a fishing trip with", "someone. You'll have to try back in a few minutes.");
                c.nextChat = 40544;
                break;
            case 40544:
                player(c, Anim.ANNOYED, "How can you be here and on board the boat?!");
                c.nextChat = 40545;
                break;
            case 40545:
                npc(c, "Murphy", Anim.CALM_1, "That's Game Magic, " + gender + ".");
                c.nextChat = 40546;
                break;
            case 40546:
                player(c, Anim.CONFUSED, "What?!");
                c.nextChat = 40547;
                break;
            case 40547:
                npc(c, "Murphy", Anim.CALM_1, "Never mind. Just try back later OK?");
                end(c);
                break;

            // ==========================================
            // SUBSEQUENT TALK & SEXTANT
            // ==========================================
            case 40590:
                npc(c, "Murphy", Anim.HAPPY, "Good day to you land lover. Fancy hitting the high seas again?");
                c.nextChat = 40591;
                break;
            case 40591:
                options4(c, 40591, "No thanks, I still feel ill from last time.", "We keep sinking! Can you give me some advice?", "Could you teach me to how to trawl for myself?", "Yes, let's do it!");
                break;

            // ==========================================
            // RECIPE FOR DISASTER (DIVING)
            // ==========================================
            case 40620:
                options(c, 40620, "Talk about Recipe for Disaster.", "Talk about something else.");
                break;
            case 40621:
                player(c, Anim.CALM_1, "Murphy, what can you tell me about giant crabs?");
                c.nextChat = 40622;
                break;
            case 40622:
                npc(c, "Murphy", Anim.CALM_1, "Giant Crabs? Why, I used to haul up a few of those", "whenever I set my nets around Rimmington.");
                c.nextChat = 40623;
                break;
            case 40623:
                npc(c, "Murphy", Anim.CALM_1, "Why the interest?");
                c.nextChat = 40624;
                break;
            case 40624:
                player(c, Anim.CALM_1, "I need to get some Giant Crab Meat and Kelp. Can you", "tell me where I can get some?");
                c.nextChat = 40625;
                break;
            case 40625:
                npc(c, "Murphy", Anim.CALM_1, "Well, the only thing I can think to do is to go down", "there and look to see if there are any left.");
                c.nextChat = 40626;
                break;
            case 40626:
                player(c, Anim.CALM_1, "Go down there? What do you mean?");
                c.nextChat = 40627;
                break;
            case 40627:
                npc(c, "Murphy", Anim.CALM_1, "As in under the water!");
                c.nextChat = 40628;
                break;
            case 40628:
                npc(c, "Murphy", Anim.CALM_1, "Tell me, can you get your hands on a fishbowl?");
                c.nextChat = 40629;
                break;
            case 40629:
                player(c, Anim.CALM_1, "I'm sure I will be able to if I put my mind to it,", "why do you ask?");
                c.nextChat = 40630;
                break;
            case 40630:
                npc(c, "Murphy", Anim.CALM_1, "Well, I know how to rig up a handy set of breathing gear", "that can allow you to keep air inside the fishbowl.");
                c.nextChat = 40631;
                break;
            case 40631:
                npc(c, "Murphy", Anim.CALM_1, "You just put the barrel of air on your back, with the", "fishbowl over your head, and then dive over the side.");
                c.nextChat = 40632;
                break;
            case 40632:
                npc(c, "Murphy", Anim.CALM_1, "It should hold enough air to keep you alive for quite some time.");
                c.nextChat = 40633;
                break;
            case 40633:
                player(c, Anim.WORRIED, "That sounds pretty dangerous...");
                c.nextChat = 40634;
                break;
            case 40634:
                npc(c, "Murphy", Anim.CALM_1, "Not at all. I'll even weigh anchor with the chain", "somewhere easy to reach so you can climb out easily", "enough if you run into trouble.");
                c.nextChat = 40635;
                break;
            case 40635:
                player(c, Anim.SAD, "I'm still not happy about this...");
                c.nextChat = 40636;
                break;
            case 40636:
                npc(c, "Murphy", Anim.CALM_1, "Well, I can't think of any other way to check. If you", "come up with a better plan then feel free to tell me.");
                c.nextChat = 40637;
                break;
            case 40637:
                npc(c, "Murphy", Anim.CALM_1, "In the meantime I'll wait, and if you want me to take", "you diving I'll be ready.");
                c.nextChat = 40638;
                break;
            case 40638:
                npc(c, "Murphy", Anim.CALM_1, "By the way, why are you so interested in crabs all of a sudden?");
                c.nextChat = 40639;
                break;
            case 40639:
                player(c, Anim.CALM_1, "I can't tell you I'm afraid. The very fabric of space", "and time could collapse!");
                c.nextChat = 40640;
                break;
            case 40640:
                npc(c, "Murphy", Anim.CALM_1, "O...k...");
                c.nextChat = 40641;
                break;
            case 40641:
                npc(c, "Murphy", Anim.CALM_1, "Well, I guess some people will do anything for a bit of", "fresh seafood.");
                end(c);
                break;

            // ==========================================
            // DIVING VALIDATION (Pets, Weight, Items)
            // ==========================================
            case 40650:
                // 1. Gear Check
                if (!c.getItems().isWearingItem(7534) || !c.getItems().isWearingItem(6696)) {
                    npc(c, "Murphy", Anim.CALM_1, "Well I can't take you out until you are properly", "geared up. We lost three diving parties that way!");
                    end(c);
                    return;
                }
                // 2. Weight Check
                if (c.getWeight() > 27.0) {
                    npc(c, "Murphy", Anim.CALM_1, "Well that's the spirit! However you are far too heavy.", " trial and error taught us that, and the errors were", "not pretty. Go bank some gear.");
                    end(c);
                    return;
                }
                // 3. Pet Validation
                if (c.hasNpc) {
                    if (isCat(c.summonId)) {
                        npc(c, "Murphy", Anim.CALM_1, "You want that cat of yours to drown or something?", "I'm not a cat-sitter. Go bank it!");
                    } else {
                        npc(c, "Murphy", Anim.CALM_1, "I'm not taking you diving with that pet.", "It'll get wet! Bank it first.");
                    }
                    end(c);
                    return;
                }
                // 4. Air-filled Items
                if (c.getItems().playerHasItem(759) || c.getItems().playerHasItem(10392)) {
                    npc(c, "Murphy", Anim.CALM_1, "Now, do you have a gnomeball or hand egg on you?", "They are so full of air they'll stop you diving!");
                    end(c);
                    return;
                }

                npc(c, "Murphy", Anim.HAPPY, "Ok, let's be off then.");
                c.nextChat = 40655;
                break;

            case 40655:
                c.getPA().movePlayer(2911, 4832, 0); // Example Dive Coords
                end(c);
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 40505) {
            switch (buttonId) {
                case OPT3_FIRST: next(c, 40506); break;
                case OPT3_SECOND: next(c, 40508); break;
                case OPT3_THIRD: next(c, 40520); break;
            }
        } else if (c.dialogueAction == 40534) {
            switch (buttonId) {
                case OPT2_FIRST: next(c, 40535); break;
                case OPT2_SECOND: next(c, 40540); break;
            }
        } else if (c.dialogueAction == 40620) {
            switch (buttonId) {
                case OPT2_FIRST: next(c, 40621); break;
                case OPT2_SECOND: next(c, 40500); break;
            }
        } else if (c.dialogueAction == 40591) {
            switch (buttonId) {
                case OPT4_FIRST: end(c); break; 
                case OPT4_SECOND: next(c, 40521); break; 
                case OPT4_THIRD: next(c, 40670); break; 
                case OPT4_FOURTH: next(c, 40540); break; 
            }
        }
    }

    private boolean isCat(int id) {
        return id == 1555 || id == 1556 || id == 1557 || id == 1558 || 
               id == 1559 || id == 1560 || id == 7582;
    }
}