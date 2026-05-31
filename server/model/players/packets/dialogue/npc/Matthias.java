package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class Matthias extends NPCDialogue {

    public Matthias(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 1341;
    }

    @Override
    public String getDialogueRange() {
        return "11000-11099";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {

            // ==========================================
            // ENTRY LOGIC
            // ==========================================
            case 11000:
                // Checks if they are currently holding the bird
                if (c.playerEquipment[c.playerWeapon] == 10024) {
                    player(c, Anim.CALM_1, "Hello again.");
                    c.nextChat = 11071;
                }
                // Checks if they are wearing the empty glove (lost bird)
                else if (c.playerEquipment[c.playerWeapon] == 10023) {
                    player(c, Anim.SAD, "Hi, er, your falcon ran away.");
                    c.nextChat = 11081;
                }
                // Standard Greeting
                else {
                    player(c, Anim.CALM_1, "Hello there.");
                    c.nextChat = 11001;
                }
                break;

            case 11001:
                npc(c, "Matthias", Anim.CALM_1, "Greetings. Can I help you at all?");
                c.nextChat = 11002;
                break;

            case 11002: // MAIN MENU
                options(c, 11002,
                        "Do you have any quests I could do?",
                        "What is this place?",
                        "That sounds like fun; could I have a go?",
                        "What's this falconry thing all about then?",
                        "Could I invest in your setup here?");
                break;


            // ==========================================
            // QUESTS BRANCH
            // ==========================================
            case 11004:
                player(c, Anim.CALM_1, "Do you have any quests I could do?");
                c.nextChat = 11005;
                break;
            case 11005:
                npc(c, "Matthias", Anim.CONFUSED, "A quest? What a strange notion. Do you normally go", "around asking complete strangers for quests?");
                c.nextChat = 11006;
                break;
            case 11006:
                player(c, Anim.CALM_1, "Er, yes, now you come to mention it.");
                c.nextChat = 11007;
                break;
            case 11007:
                npc(c, "Matthias", Anim.CALM_1, "Oh, ok then. Well, no, I don't; sorry.");
                c.nextChat = -1;
                break;


            // ==========================================
            // WHAT IS THIS PLACE BRANCH
            // ==========================================
            case 11008:
                player(c, Anim.CALM_1, "What is this place?");
                c.nextChat = 11009;
                break;
            case 11009:
                npc(c, "Matthias", Anim.CALM_1, "A good question; straight and to the point.");
                c.nextChat = 11010;
                break;
            case 11010:
                npc(c, "Matthias", Anim.CALM_1, "My name is Matthias, I am a falconer, and this is", "where I train my birds.");
                c.nextChat = 11011;
                break;
            case 11011:
                options(c, 11011, "Do you have any quests I could do?", "That sounds like fun; could I have a go?");
                break;


            // ==========================================
            // WHAT IS FALCONRY BRANCH
            // ==========================================
            case 11012:
                player(c, Anim.CALM_1, "What's this falconry thing all about then?");
                c.nextChat = 11013;
                break;
            case 11013:
                npc(c, "Matthias", Anim.CALM_1, "Well, some people see it as a sport, although such a", "term does not really convey the amount of patience", "and dedication required to be proficient at the task.");
                c.nextChat = 11014;
                break;
            case 11014:
                npc(c, "Matthias", Anim.CALM_1, "Putting it simply, it is the training and use of birds", "of prey in hunting quarry.");
                c.nextChat = 11015;
                break;
            case 11015:
                player(c, Anim.CALM_1, "So it's like keeping a pet then?");
                c.nextChat = 11016;
                break;
            case 11016:
                npc(c, "Matthias", Anim.CALM_1, "Not exactly, no. Such a bird can never really be", "considered tame in the same way that a dog can.");
                c.nextChat = 11017;
                break;
            case 11017:
                npc(c, "Matthias", Anim.CALM_1, "They can be trained to associate people or places", "with food though, and, as such, a good falconer can", "get a trained bird to do as he wishes.");
                c.nextChat = 11018;
                break;
            case 11018:
                options(c, 11018, "Could I have a go with your bird?", "Could I invest in your setup here?");
                break;


            // ==========================================
            // HAVE A GO / RENTING BRANCH
            // ==========================================
            case 11020:
                player(c, Anim.CALM_1, "That sounds like fun; could I have a go?");
                c.nextChat = 11021;
                break;
            case 11021:
                if (c.falconryUnlocked) {
                    npc(c, "Matthias", Anim.HAPPY, "Absolutely, are you ready?");
                    c.nextChat = 11029; // Skip payment
                } else {
                    npc(c, "Matthias", Anim.CALM_1, "Training falcons is a lot of work and I doubt you're", "up to the task. However, I suppose I could let you", "try hunting with one.");
                    c.nextChat = 11022;
                }
                break;
            case 11022:
                npc(c, "Matthias", Anim.CALM_1, "I have some tamer birds that I occasionally lend to", "rich noblemen who consider it a sufficiently refined sport", "for their tastes, and you look like the kind who", "might appreciate a good hunt.");
                c.nextChat = 11023;
                break;
            case 11023:
                npc(c, "Matthias", Anim.CALM_1, "I'd have to request a small fee, mind you;", "how does 500 gold pieces sound?");
                c.nextChat = 11024;
                break;
            case 11024:
                if (c.getItems().playerHasItem(995, 500)) {
                    options(c, 11024, "Ok, that seems reasonable.", "I'm not interested then, thanks.");
                } else {
                    player(c, Anim.SAD, "I'm afraid I don't have that much with me.");
                    c.nextChat = -1;
                }
                break;

            case 11028: // Did not want to rent
                player(c, Anim.CALM_1, "I'm not interested then, thanks.");
                c.nextChat = 11031;
                break;
            case 11031:
                npc(c, "Matthias", Anim.CALM_1, "Well, you're welcome to come back if you change your mind.");
                c.nextChat = -1;
                break;

            case 11032: // Agreed to rent
                player(c, Anim.CALM_1, "Ok, that seems reasonable.");
                c.nextChat = 11033;
                break;
            case 11029: // Agreed (Unlocked, free)
                player(c, Anim.HAPPY, "I am!");
                c.nextChat = 11033;
                break;

            case 11033: // Equip Check
                if (c.playerEquipment[c.playerWeapon] > 0 || c.playerEquipment[c.playerShield] > 0 || c.playerEquipment[c.playerHands] > 0) {
                    npc(c, "Matthias", Anim.CALM_1, "Good, however you really need both hands free.", "I'd suggest that you take off some of that kit", "you're wearing before we start.");
                    c.nextChat = -1;
                } else {
                    // Safe to give glove! Handled in action router for standard rent, or right here for unlocked:
                    if (!c.falconryUnlocked) {
                        c.getItems().deleteItem(995, 500);
                    }
                    c.getItems().addItem(10024, 1);
                    c.getItems().wearItem(10024, 1, 3); // Auto-equip to weapon slot

                    item("Falconry", c, 10024, "The falconer gives you a large leather glove and", "brings one of the smaller birds over to land on it.");
                    c.nextChat = 11036;
                }
                break;
            case 11036:
                npc(c, "Matthias", Anim.CALM_1, "Don't worry; I'll keep an eye on you to make sure", "you don't upset it too much.");
                c.nextChat = -1;
                break;


            // ==========================================
            // INVESTMENT BRANCH
            // ==========================================
            case 11040:
                player(c, Anim.CALM_1, "Could I invest in your setup here?");
                c.nextChat = 11041;
                break;
            case 11041:
                if (c.falconryUnlocked) {
                    npc(c, "Matthias", Anim.CALM_1, "I appreciate that you want to invest more, but I", "really don't need it right now!");
                    c.nextChat = -1;
                } else {
                    npc(c, "Matthias", Anim.CALM_1, "If you'd like to put money forward for training", "more falcons, be my guest!");
                    c.nextChat = 11043;
                }
                break;
            case 11043:
                player(c, Anim.CALM_1, "Would I get any benefit from this?");
                c.nextChat = 11044;
                break;
            case 11044:
                npc(c, "Matthias", Anim.CALM_1, "I suppose I could let you use my falcons for free if", "you stump up enough cash. How does 500,000 coins", "sound?");
                c.nextChat = 11045;
                break;
            case 11045:
                if (c.getItems().playerHasItem(995, 500000)) {
                    options(c, 11045, "Unlock permanent access for 500,000 coins?", "Hmm, that seems a little steep. I'll think on it.");
                } else {
                    player(c, Anim.SAD, "Ah, I don't have that much on me right now.");
                    c.nextChat = -1;
                }
                break;
            case 11048: // Declined Investment
                player(c, Anim.CALM_1, "Hmm, that seems a little steep. I'll think on it.");
                c.nextChat = -1;
                break;


            // ==========================================
            // CURRENTLY EQUIPPED BRANCH (10024)
            // ==========================================
            case 11071:
                npc(c, "Matthias", Anim.HAPPY, "Ah, you're back. How are you getting along with", "them then?");
                c.nextChat = 11072;
                break;
            case 11072:
                player(c, Anim.CALM_1, "It's certainly harder than it looks.");
                c.nextChat = 11073;
                break;
            case 11073:
                npc(c, "Matthias", Anim.CALM_1, "Sorry, but I was talking to the falcon, not you.", "But yes it is; have you had enough yet?");
                c.nextChat = 11074;
                break;
            case 11074:
                options(c, 11074, "Actually, I'd like to keep trying a little longer.", "I think I'll leave it for now.");
                break;
            case 11075:
                player(c, Anim.CALM_1, "Actually, I'd like to keep trying a little longer.");
                c.nextChat = 11076;
                break;
            case 11076:
                npc(c, "Matthias", Anim.CALM_1, "Ok then, just come talk to me when you're done.");
                c.nextChat = -1;
                break;
            case 11077:
                player(c, Anim.CALM_1, "I think I'll leave it for now.");
                c.nextChat = 11078;
                break;
            case 11078:
                // Remove the glove
                c.getItems().removeItem(10024, 3);
                c.getItems().deleteItem(10024, 1);
                item("Falconry", c, 10024, "You give the falcon and glove back to Matthias.");
                c.nextChat = -1;
                break;


            // ==========================================
            // LOST FALCON BRANCH (10023)
            // ==========================================
            case 11081:
                npc(c, "Matthias", Anim.CALM_1, "Falcons aren't domesticated you know. You can't expect", "them to just automatically return to you. Fortunately", "for you, they can learn to associate areas with food.", "He'll probably be back on his post.");
                c.nextChat = 11082;
                break;
            case 11082:
                npc(c, "Matthias", Anim.CALM_1, "Do you want me to fetch him for you?");
                c.nextChat = 11083;
                break;
            case 11083:
                options(c, 11083, "Yes, please.", "Actually, I think I'll leave it for now.");
                break;
            case 11084:
                player(c, Anim.CALM_1, "Yes, please.");
                c.nextChat = 11085;
                break;
            case 11085:
                // Return the bird
                c.getItems().wearItem(10024, 1, 3);
                item("Falconry", c, 10024, "The falconer brings a bird over to land on your glove.");
                c.nextChat = 11086;
                break;
            case 11086:
                npc(c, "Matthias", Anim.CALM_1, "Right, try to be a bit more careful this time; these birds", "take a long time to raise. I'd rather not lose one.");
                c.nextChat = -1;
                break;
            case 11087:
                player(c, Anim.CALM_1, "Actually, I think I'll leave it for now.");
                c.nextChat = 11088;
                break;
            case 11088:
                // Remove the empty glove
                c.getItems().removeItem(10023, c.playerWeapon);
                c.getItems().deleteItem(10023, 1);
                item("Falconry", c, 10024, "You give the falcon glove back to Matthias.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {

        if (c.dialogueAction == 11002) { // Main Menu
            switch (buttonId) {
                case OPT5_FIRST: next(c, 11004); break; // Quests
                case OPT5_SECOND: next(c, 11008); break; // What is this place
                case OPT5_THIRD: next(c, 11020); break; // Have a go
                case OPT5_FOURTH: next(c, 11012); break; // What's this about
                case OPT5_FIFTH: next(c, 11040); break; // Invest
            }
        }

        else if (c.dialogueAction == 11011) { // Secondary Menu (What is this place -> Options)
            switch (buttonId) {
                case OPT2_FIRST: next(c, 11004); break; // Quests
                case OPT2_SECOND: next(c, 11020); break; // Have a go
            }
        }

        else if (c.dialogueAction == 11018) { // Secondary Menu (What's this about -> Options)
            switch (buttonId) {
                case OPT2_FIRST: next(c, 11020); break; // Have a go
                case OPT2_SECOND: next(c, 11040); break; // Invest
            }
        }

        else if (c.dialogueAction == 11024) { // Rent Choice
            switch (buttonId) {
                case OPT2_FIRST: next(c, 11032); break; // Agreed
                case OPT2_SECOND: next(c, 11028); break; // Declined
            }
        }

        else if (c.dialogueAction == 11045) { // Investment Choice
            switch (buttonId) {
                case OPT2_FIRST:
                    if (c.getItems().playerHasItem(995, 500000)) {
                        c.getItems().deleteItem(995, 500000);
                        c.falconryUnlocked = true;
                        item("Falconry", c, 995, "You hand over 500,000 coins.", "You are now a permanent investor in the Falconry.");
                        c.nextChat = -1;
                    }
                    break;
                case OPT2_SECOND: next(c, 11048); break; // Declined
            }
        }

        else if (c.dialogueAction == 11074) { // Equipped Choice
            switch (buttonId) {
                case OPT2_FIRST: next(c, 11075); break; // Keep trying
                case OPT2_SECOND: next(c, 11077); break; // Give it back
            }
        }

        else if (c.dialogueAction == 11083) { // Lost Bird Choice
            switch (buttonId) {
                case OPT2_FIRST: next(c, 11084); break; // Fetch it
                case OPT2_SECOND: next(c, 11087); break; // Give empty glove back
            }
        }
    }
}