package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.util.Misc;

public class DonieAndGee extends NPCDialogue {

    public DonieAndGee(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        return new int[] { 6817, 6816 }; // 2237 = Gee, 2238 = Donie
    }
    @Override
    public String getDialogueRange() {
        return "7000-7040, 7100-7115, 7120-7123, 7130-7133, 7140-7146, 7150-7153, 7160-7163, 7170-7171, 7199, 7200-7202, 7300-7338, 7400-7440, 7500-7525, 7600-7647";
    }
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        
        // Dynamically set the name so the chatbox matches who they clicked!
        String npcName = (npcId == 6817) ? "Donie" : "Gee";

        switch (startDialogueId) {

            // ==========================================
            // MAIN ENTRY & RANDOM SETS
            // ==========================================
            case 7000:
                npc(c, npcName, Anim.CALM_1, "Hello there, can I help you?");
                
                if (isTheLostTribeActive(c)) {
                    c.nextChat = 7040; // The Lost Tribe specific options
                } else {
                    int rand = Misc.random(2);
                    if (rand == 0) c.nextChat = 7010;
                    else if (rand == 1) c.nextChat = 7020;
                    else c.nextChat = 7030;
                }
                break;

            case 7010: // Set 1 & 4 Combined
                c.getDH().sendOptions("Where am I?", "How are you today?", "Do you know of any quests I can do?", "Your shoe lace is untied.", "Where can I get a haircut like yours?");
                c.dialogueAction = 7010;
                break;

            case 7020: // Set 2
                options3(c, 7020, "What's up?", "Are there any quests I can do here?", "Can I buy your stick?");
                break;

            case 7030: // Set 3
                options3(c, 7030, "Do you have anything of value which I can have?", "Are there any quests I can do here?", "Can I buy your stick?");
                break;

            case 7040: // The Lost Tribe Set
                c.getDH().sendOptions("Where am I?", "How are you today?", "Are there any quests I can do here?", "Your shoe lace is untied.", "Do you know what happened in the castle cellar?");
                c.dialogueAction = 7040;
                break;

            // ==========================================
            // GENERIC RESPONSES
            // ==========================================
            case 7100:
                player(c, Anim.CALM_1, "Where am I?");
                c.nextChat = 7101;
                break;
            case 7101:
                npc(c, npcName, Anim.CALM_1, "This is the town of Lumbridge my friend.");
                c.nextChat = 0;
                break;

            case 7110:
                player(c, Anim.CALM_1, "How are you today?");
                c.nextChat = 7111;
                break;
            case 7111:
                npc(c, npcName, Anim.HAPPY, "Aye, not too bad thank you. Lovely weather in Gielinor", "this fine day.");
                c.nextChat = 7112;
                break;
            case 7112:
                player(c, Anim.CONFUSED, "Weather?");
                c.nextChat = 7113;
                break;
            case 7113:
                npc(c, npcName, Anim.CALM_1, "Yes weather, you know.", "The state or condition of the atmosphere at a time and", "place, with respect to variables such as temperature,", "moisture, wind velocity, and barometric pressure.");
                c.nextChat = 7114;
                break;
            case 7114:
                player(c, Anim.CALM_1, "...");
                c.nextChat = 7115;
                break;
            case 7115:
                npc(c, npcName, Anim.HAPPY, "Not just a pretty face eh? Ha ha ha.");
                c.nextChat = 0;
                break;

            case 7120:
                player(c, Anim.CALM_1, "Your shoe lace is untied.");
                c.nextChat = 7121;
                break;
            case 7121:
                npc(c, npcName, Anim.ANGRY, "No it's not!");
                c.nextChat = 7122;
                break;
            case 7122:
                player(c, Anim.CALM_1, "No you're right. I have nothing to back that up.");
                c.nextChat = 7123;
                break;
            case 7123:
                npc(c, npcName, Anim.ANGRY, "Fool! Leave me alone!");
                c.nextChat = 0;
                break;

            case 7130:
                player(c, Anim.CALM_1, "What's up?");
                c.nextChat = 7131;
                break;
            case 7131:
                npc(c, npcName, Anim.CALM_1, "I assume the sky is up..");
                c.nextChat = 7132;
                break;
            case 7132:
                player(c, Anim.CONFUSED, "You assume?");
                c.nextChat = 7133;
                break;
            case 7133:
                npc(c, npcName, Anim.CALM_1, "Yeah, unfortunately I don't seem to be able to look up.");
                c.nextChat = 0;
                break;

            case 7140:
                player(c, Anim.CALM_1, "Can I buy your stick?");
                c.nextChat = 7141;
                break;
            case 7141:
                npc(c, npcName, Anim.ANGRY, "It's not a stick! I'll have you know it's a very", "powerful staff!");
                c.nextChat = 7142;
                break;
            case 7142:
                player(c, Anim.HAPPY, "Really? Show me what it can do!");
                c.nextChat = 7143;
                break;
            case 7143:
                npc(c, npcName, Anim.SAD, "Um..It's a bit low on power at the moment..");
                c.nextChat = 7144;
                break;
            case 7144:
                player(c, Anim.CALM_1, "It's a stick isn't it?");
                c.nextChat = 7145;
                break;
            case 7145:
                npc(c, npcName, Anim.SAD, "...Ok it's a stick.. But only while I save up for a staff.", "Zaff in Varrock square sells them in his shop.");
                c.nextChat = 7146;
                break;
            case 7146:
                player(c, Anim.CALM_1, "Well good luck with that.");
                c.nextChat = 0;
                break;

            case 7150:
                player(c, Anim.CALM_1, "Do you have anything of value which I can have?");
                c.nextChat = 7151;
                break;
            case 7151:
                npc(c, npcName, Anim.CONFUSED, "Are you asking for free stuff?");
                c.nextChat = 7152;
                break;
            case 7152:
                player(c, Anim.CALM_1, "Well... er... yes.");
                c.nextChat = 7153;
                break;
            case 7153:
                npc(c, npcName, Anim.ANNOYED, "No I do not have anything I can give you. If I did have", "anything of value I wouldn't want to give it away.");
                c.nextChat = 0;
                break;

            case 7160:
                player(c, Anim.CALM_1, "Where can I get a haircut like yours?");
                c.nextChat = 7161;
                break;
            case 7161:
                npc(c, npcName, Anim.HAPPY, "Yes, it does look like you need a hairdresser.");
                c.nextChat = 7162;
                break;
            case 7162:
                player(c, Anim.HAPPY, "Oh thanks!");
                c.nextChat = 7163;
                break;
            case 7163:
                npc(c, npcName, Anim.CALM_1, "No problem. The hairdresser in Falador will probably", "be able to sort you out.", "The Lumbridge general store sells useful maps", "if you don't know the way.");
                c.nextChat = 0;
                break;

            case 7170:
                player(c, Anim.CALM_1, "Do you know what happened in the castle cellar?");
                c.nextChat = 7171;
                break;
            case 7171:
                npc(c, npcName, Anim.CALM_1, "I heard the wall collapsed or something. I don't know why.", "Perhaps you could get to the bottom of this mystery!");
                c.nextChat = 0;
                break;

            case 7199: // "Maybe another time" exit node
                player(c, Anim.CALM_1, "Maybe another time.");
                c.nextChat = 0;
                break;

            // ==========================================
            // QUEST CATEGORY MENU
            // ==========================================
            case 7200:
                player(c, Anim.CALM_1, "Do you know of any quests I can do?");
                c.nextChat = 7201;
                break;
            case 7201:
                npc(c, npcName, Anim.CALM_1, "What kind of quest are you looking for?");
                c.nextChat = 7202;
                break;
            case 7202:
                c.getDH().sendOptions("I fancy a bit of a fight, anything dangerous?", "Something easy please, I'm new here.", "I'm a thinker rather than fighter, anything skill oriented?", "I want to do all kinds of things...", "Maybe another time.");
                c.dialogueAction = 7202;
                break;

            // --- CATEGORY 1: DANGEROUS ---
            case 7300:
                player(c, Anim.CALM_1, "I fancy a bit of a fight, anything dangerous?");
                c.nextChat = 7301;
                break;
            case 7301:
                npc(c, npcName, Anim.CALM_1, "Hmm.. dangerous you say? What sort of creatures", "are you looking to fight?");
                c.nextChat = 7302;
                break;
            case 7302:
                options4(c, 7302, "Big scary demons!", "Vampyres!", "Small.. something small would be good.", "Maybe another time.");
                break;

            case 7310: // Demons
                player(c, Anim.ANGRY, "Big scary demons!");
                c.nextChat = 7311;
                break;
            case 7311:
                npc(c, npcName, Anim.CALM_1, "You are a brave soul indeed.", "Now that you mention it, I heard a rumour about a", "fortune-teller in Varrock who is rambling about some", "kind of greater evil.. sounds demon-like if you ask me.");
                c.nextChat = 7312;
                break;
            case 7312:
                npc(c, npcName, Anim.CALM_1, "Perhaps you could check it out if you are", "as brave as you say?");
                c.nextChat = isDemonSlayerDone(c) ? 7315 : 7313;
                break;
            case 7313:
                player(c, Anim.HAPPY, "Thanks for the tip, perhaps I will.");
                c.nextChat = 0;
                break;
            case 7315:
                player(c, Anim.HAPPY, "I've already killed the demon Delrith. He was merely a", "stain on my sword when I was finished with him!");
                c.nextChat = 7316;
                break;
            case 7316:
                npc(c, npcName, Anim.HAPPY, "Well done! However I'm sure if you search around the", "world you will find more challenging foes to slay.");
                c.nextChat = 0;
                break;

            case 7320: // Vampyres
                player(c, Anim.ANGRY, "Vampyres!");
                c.nextChat = 7321;
                break;
            case 7321:
                npc(c, npcName, Anim.CALM_1, "Ha ha. I personally don't believe in such things.", "However, there is a man in Draynor Village who has", "been scaring the village folk with stories of vampyres.");
                c.nextChat = 7322;
                break;
            case 7322:
                npc(c, npcName, Anim.CALM_1, "He's named Morgan and can be found in one of the", "village houses. Perhaps you could see what the matter is?");
                c.nextChat = isVampyreSlayerDone(c) ? 7325 : 7323;
                break;
            case 7323:
                player(c, Anim.HAPPY, "Thanks for the tip.");
                c.nextChat = 0;
                break;
            case 7325:
                player(c, Anim.HAPPY, "Oh I have already killed that nasty blood-sucking", "vampyre. Draynor will be safe now.");
                c.nextChat = 7326;
                break;
            case 7326:
                npc(c, npcName, Anim.CALM_1, "Yeah, yeah of course you did. Everyone knows", "vampyres are not real....");
                c.nextChat = 7327;
                break;
            case 7327:
                player(c, Anim.ANGRY, "What! I did slay the beast..I really did.");
                c.nextChat = 7328;
                break;
            case 7328:
                npc(c, npcName, Anim.CALM_1, "You're not fooling anyone you know.");
                c.nextChat = 7329;
                break;
            case 7329:
                player(c, Anim.ANNOYED, "..Huh.. But... Hey! I did... believe what you like.");
                c.nextChat = 0;
                break;

            case 7330: // Small
                player(c, Anim.CALM_1, "Small.. something small would be good.");
                c.nextChat = 7331;
                break;
            case 7331:
                npc(c, npcName, Anim.CONFUSED, "Small? Small isn't really that dangerous though is it?");
                c.nextChat = 7332;
                break;
            case 7332:
                player(c, Anim.CALM_1, "Yes it can be! There could be anything from an evil", "chicken to a poisonous spider. They attack in", "numbers you know!");
                c.nextChat = 7333;
                break;
            case 7333:
                npc(c, npcName, Anim.CALM_1, "Yes ok, point taken. Speaking of small monsters, I hear", "old Wizard Mizgog in the wizards' tower has just had all", "his beads taken by a gang of mischievous imps.");
                c.nextChat = 7334;
                break;
            case 7334:
                npc(c, npcName, Anim.CALM_1, "Sounds like it could be a quest for you?");
                c.nextChat = isImpCatcherDone(c) ? 7337 : 7335;
                break;
            case 7335:
                player(c, Anim.HAPPY, "Thanks for your help.");
                c.nextChat = 0;
                break;
            case 7337:
                player(c, Anim.HAPPY, "Yes I know of Mizgog and have already helped him with", "his imp problem. It took me ages to find those beads!");
                c.nextChat = 7338;
                break;
            case 7338:
                npc(c, npcName, Anim.HAPPY, "Imps will be imps!");
                c.nextChat = 0;
                break;

            // --- CATEGORY 2: EASY ---
            case 7400:
                player(c, Anim.CALM_1, "Something easy please, I'm new here.");
                c.nextChat = 7401;
                break;
            case 7401:
                npc(c, npcName, Anim.CALM_1, "I can tell you about plenty of small easy tasks.", "The Lumbridge cook has been having problems, the Duke", "is confused over some strange talisman and on top of", "all that, poor lad Romeo in Varrock has girlfriend problems.");
                c.nextChat = 7402;
                break;
            case 7402:
                options4(c, 7402, "The Lumbridge cook.", "The Duke's strange talisman.", "Romeo and his girlfriend.", "Maybe another time.");
                break;

            case 7410: // Cook
                player(c, Anim.CALM_1, "Tell me about the Lumbridge cook.");
                c.nextChat = 7411;
                break;
            case 7411:
                npc(c, npcName, Anim.CALM_1, "It's funny really, the cook would forget his head if it", "wasn't screwed on. This time he forgot to get", "ingredients for the Duke's birthday cake.");
                c.nextChat = 7412;
                break;
            case 7412:
                npc(c, npcName, Anim.CALM_1, "Perhaps you could help him? You will probably find him", "in the Lumbridge Castle kitchen.");
                c.nextChat = isCooksAssistantDone(c) ? 7415 : 7413;
                break;
            case 7413:
                player(c, Anim.HAPPY, "Thank you. I shall go speak with him.");
                c.nextChat = 0;
                break;
            case 7415:
                player(c, Anim.HAPPY, "I have already helped the cook in Lumbridge.");
                c.nextChat = 7416;
                break;
            case 7416:
                npc(c, npcName, Anim.HAPPY, "Oh yes, so you have. I am sure the Duke will be pleased.");
                c.nextChat = 0;
                break;

            case 7420: // Talisman
                player(c, Anim.CALM_1, "Tell me about the Duke's strange talisman.");
                c.nextChat = 7421;
                break;
            case 7421:
                npc(c, npcName, Anim.CALM_1, "Well the Duke of Lumbridge has found a strange", "talisman that no one seems to understand. Perhaps you", "could help him? You can probably find him", "upstairs in Lumbridge Castle.");
                c.nextChat = isRuneMysteriesDone(c) ? 7424 : 7422;
                break;
            case 7422:
                player(c, Anim.CALM_1, "Sounds mysterious. I may just do that. Thanks.");
                c.nextChat = 0;
                break;
            case 7424:
                player(c, Anim.HAPPY, "Yes, I have already solved the Rune mysteries.");
                c.nextChat = 7425;
                break;
            case 7425:
                npc(c, npcName, Anim.HAPPY, "Ah excellent. Thank you very much adventurer.");
                c.nextChat = 0;
                break;

            case 7430: // Romeo
                player(c, Anim.CALM_1, "Tell me about Romeo and his girlfriend please.");
                c.nextChat = 7431;
                break;
            case 7431:
                npc(c, npcName, Anim.CALM_1, "Romeo in Varrock needs help with finding his beloved", "Juliet, you may be able to help him out.", "Unless of course you manage to find Juliet first", "in which case she has probably lost Romeo.");
                c.nextChat = isRomeoAndJulietDone(c) ? 7435 : 7432;
                break;
            case 7432:
                player(c, Anim.CONFUSED, "Right, ok. Romeo is in Varrock?");
                c.nextChat = 7433;
                break;
            case 7433:
                npc(c, npcName, Anim.CALM_1, "Yes you can't miss him, he's wandering aimlessly in the square.");
                c.nextChat = 0;
                break;
            case 7435:
                player(c, Anim.CALM_1, "Oh yes, I've already helped Romeo in the best", "possible way I can...");
                c.nextChat = 7436;
                break;
            case 7436:
                npc(c, npcName, Anim.CONFUSED, "Really?");
                c.nextChat = 7437;
                break;
            case 7437:
                player(c, Anim.CALM_1, "Yup.");
                c.nextChat = 7438;
                break;
            case 7438:
                npc(c, npcName, Anim.CONFUSED, "...How?");
                c.nextChat = 7439;
                break;
            case 7439:
                player(c, Anim.CALM_1, "He thinks Juliet is dead...");
                c.nextChat = 7440;
                break;
            case 7440:
                npc(c, npcName, Anim.CONFUSED, "Well.. ok.. well done... I think...");
                c.nextChat = 0;
                break;

            // --- CATEGORY 3: SKILL ORIENTED ---
            case 7500:
                player(c, Anim.CALM_1, "I'm a thinker rather than fighter, anything skill orientated?");
                c.nextChat = 7501;
                break;
            case 7501:
                npc(c, npcName, Anim.CALM_1, "Skills play a big part when you want to progress in", "knowledge throughout Gielinor. I know of a few", "skill-related quests that can get you started.");
                c.nextChat = 7502;
                break;
            case 7502:
                npc(c, npcName, Anim.CALM_1, "You may be able to help out Fred the farmer who is in", "need of someones crafting expertise.", "Or, there's always Doric the dwarf who needs an errand", "running for him?");
                c.nextChat = 7503;
                break;
            case 7503:
                options3(c, 7503, "Fred the farmer.", "Doric the dwarf.", "Maybe another time.");
                break;

            case 7510: // Fred
                player(c, Anim.CALM_1, "Tell me about Fred the farmer please.");
                c.nextChat = 7511;
                break;
            case 7511:
                npc(c, npcName, Anim.CALM_1, "You can find Fred next to the field of sheep in", "Lumbridge. Perhaps you should go and speak with him.");
                c.nextChat = isSheepShearerDone(c) ? 7514 : 7512;
                break;
            case 7512:
                player(c, Anim.HAPPY, "Thanks, maybe I will.");
                c.nextChat = 0;
                break;
            case 7514:
                player(c, Anim.HAPPY, "I have already helped Fred the farmer. I sheared his", "sheep and made 20 balls of wool for him.", "He wouldn't let me kill his chickens though.");
                c.nextChat = 7515;
                break;
            case 7515:
                npc(c, npcName, Anim.CALM_1, "Lumbridge chickens do make good target practice.", "You will have to wait until he isn't looking.");
                c.nextChat = 0;
                break;

            case 7520: // Doric
                player(c, Anim.CALM_1, "Tell me about Doric the dwarf.");
                c.nextChat = 7521;
                break;
            case 7521:
                npc(c, npcName, Anim.CALM_1, "Doric the dwarf is located north of Falador. He might be", "able to help you with smithing. You should speak", "to him. He may let you use his anvils.");
                c.nextChat = isDoricsQuestDone(c) ? 7524 : 7522;
                break;
            case 7522:
                player(c, Anim.HAPPY, "Thanks for the tip.");
                c.nextChat = 0;
                break;
            case 7524:
                player(c, Anim.HAPPY, "Yes, I've been to see Doric already. He was happy to let", "me use his anvils after I ran a small errand for him.");
                c.nextChat = 7525;
                break;
            case 7525:
                npc(c, npcName, Anim.HAPPY, "Oh good, Thank you " + c.playerName + "!");
                c.nextChat = 0;
                break;

            // --- CATEGORY 4: ALL KINDS ---
            case 7600:
                player(c, Anim.CALM_1, "I want to do all kinds of things, do you know of anything like that?");
                c.nextChat = 7601;
                break;
            case 7601:
                npc(c, npcName, Anim.CALM_1, "Of course I do. Gielinor is a huge place you know,", "now let me think...", "Hetty the witch in Rimmington might be able to offer", "help in the ways of magical abilities..");
                c.nextChat = 7602;
                break;
            case 7602:
                npc(c, npcName, Anim.CALM_1, "Also, pirates are currently docked in Port Sarim,", "Where pirates are, treasure is never far away...", "Or you could go help out Ernest who got lost in", "Draynor Manor, spooky place that.");
                c.nextChat = 7603;
                break;
            case 7603:
                options4(c, 7603, "Hetty the Witch.", "Pirate's treasure.", "Ernest and Draynor Manor.", "Maybe another time.");
                break;

            case 7610: // Hetty
                player(c, Anim.CALM_1, "Tell me about Hetty the witch.");
                c.nextChat = 7611;
                break;
            case 7611:
                npc(c, npcName, Anim.CALM_1, "Hetty the witch can be found in Rimmington, south of", "Falador. She's currently working on some new potions.", "Perhaps you could give her a hand? She might be", "able to offer help with your magical abilities.");
                c.nextChat = isWitchsPotionDone(c) ? 7614 : 7612;
                break;
            case 7612:
                player(c, Anim.CALM_1, "Ok thanks, let's hope she doesn't turn me into a potato or something..");
                c.nextChat = 0;
                break;
            case 7614:
                player(c, Anim.HAPPY, "Yes, I have already been to see Hetty, she gave me", "super cosmic powers after I helped out with her potion!", "I could probably destroy you with a single thought!");
                c.nextChat = 7615;
                break;
            case 7615:
                npc(c, npcName, Anim.CONFUSED, "Did she really?");
                c.nextChat = 7616;
                break;
            case 7616:
                player(c, Anim.CALM_1, "No not really...");
                c.nextChat = 7617;
                break;
            case 7617:
                npc(c, npcName, Anim.CALM_1, "Right.....");
                c.nextChat = 0;
                break;

            case 7620: // Pirate
                player(c, Anim.CALM_1, "Tell me about Pirate's Treasure.");
                c.nextChat = 7621;
                break;
            case 7621:
                npc(c, npcName, Anim.CALM_1, "RedBeard Frank in Port Sarim's bar, the Rusty Anchor,", "might be able to tell you about the rumored treasure", "that is buried somewhere in Gielinor.");
                c.nextChat = isPiratesTreasureDone(c) ? 7624 : 7622;
                break;
            case 7622:
                player(c, Anim.CALM_1, "Sounds adventurous, I may have to check that out. Thank you.");
                c.nextChat = 0;
                break;
            case 7624:
                player(c, Anim.HAPPY, "Yarr! I already found the booty!");
                c.nextChat = 7625;
                break;
            case 7625:
                npc(c, npcName, Anim.HAPPY, "Yarr indeed my friend. A most excellent find.");
                c.nextChat = 7626;
                break;
            case 7626:
                player(c, Anim.ANGRY, "Yarr!");
                c.nextChat = 7627;
                break;
            case 7627:
                npc(c, npcName, Anim.ANGRY, "Yarrr!");
                c.nextChat = 7628;
                break;
            case 7628:
                player(c, Anim.ANGRY, "YARRR!");
                c.nextChat = 7629;
                break;
            case 7629:
                npc(c, npcName, Anim.ANNOYED, "Right, that's enough of that!");
                c.nextChat = 7630;
                break;
            case 7630:
                player(c, Anim.SAD, "..Sorry.");
                c.nextChat = 0;
                break;

            case 7640: // Ernest
                player(c, Anim.CALM_1, "Tell me about Ernest please.");
                c.nextChat = 7641;
                break;
            case 7641:
                npc(c, npcName, Anim.CALM_1, "The best place to start would be at the gate to Draynor", "Manor. There you will find Veronica who will", "be able to tell you more.", "I suggest you tread carefully in that place; it's haunted.");
                c.nextChat = isErnestTheChickenDone(c) ? 7644 : 7642;
                break;
            case 7642:
                player(c, Anim.HAPPY, "Sounds like fun. I've never been to a Haunted Manor before.");
                c.nextChat = 0;
                break;
            case 7644:
                player(c, Anim.HAPPY, "Yeah, I found Ernest already. Professor Oddenstein", "had turned him into a chicken!");
                c.nextChat = 7645;
                break;
            case 7645:
                npc(c, npcName, Anim.CONFUSED, "A chicken!?");
                c.nextChat = 7646;
                break;
            case 7646:
                player(c, Anim.CALM_1, "Yeah a chicken. It could have been worse though.");
                c.nextChat = 7647;
                break;
            case 7647:
                npc(c, npcName, Anim.CALM_1, "Very true, poor guy.");
                c.nextChat = 0;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        
        // Option Set 1 & 4 (5-Options)
        if (c.dialogueAction == 7010) {
            switch (buttonId) {
                case OPT5_FIRST: next(c, 7100); break; // Where am I
                case OPT5_SECOND: next(c, 7110); break; // How are you
                case OPT5_THIRD: next(c, 7200); break; // Quests
                case OPT5_FOURTH: next(c, 7120); break; // Shoe lace
                case OPT5_FIFTH: next(c, 7160); break; // Haircut
            }
        } 
        // Option Set 2
        else if (c.dialogueAction == 7020) {
            switch (buttonId) {
                case OPT3_FIRST: next(c, 7130); break; // What's up
                case OPT3_SECOND: next(c, 7200); break; // Quests
                case OPT3_THIRD: next(c, 7140); break; // Stick
            }
        }
        // Option Set 3
        else if (c.dialogueAction == 7030) {
            switch (buttonId) {
                case OPT3_FIRST: next(c, 7150); break; // Free stuff
                case OPT3_SECOND: next(c, 7200); break; // Quests
                case OPT3_THIRD: next(c, 7140); break; // Stick
            }
        }
        // The Lost Tribe Options (5-Options)
        else if (c.dialogueAction == 7040) {
            switch (buttonId) {
                case OPT5_FIRST: next(c, 7100); break; // Where am I
                case OPT5_SECOND: next(c, 7110); break; // How are you
                case OPT5_THIRD: next(c, 7200); break; // Quests
                case OPT5_FOURTH: next(c, 7120); break; // Shoe lace
                case OPT5_FIFTH: next(c, 7170); break; // Castle cellar
            }
        }
        // Main Quest Menu
        else if (c.dialogueAction == 7202) {
            switch (buttonId) {
                case OPT5_FIRST: next(c, 7300); break; // Dangerous
                case OPT5_SECOND: next(c, 7400); break; // Easy
                case OPT5_THIRD: next(c, 7500); break; // Skill
                case OPT5_FOURTH: next(c, 7600); break; // All kinds
                case OPT5_FIFTH: next(c, 7199); break; // Exit
            }
        }
        // Dangerous Quests Menu
        else if (c.dialogueAction == 7302) {
            switch (buttonId) {
                case OPT4_FIRST: next(c, 7310); break; // Demons
                case OPT4_SECOND: next(c, 7320); break; // Vampyres
                case OPT4_THIRD: next(c, 7330); break; // Small
                case OPT4_FOURTH: next(c, 7199); break; // Exit
            }
        }
        // Easy Quests Menu
        else if (c.dialogueAction == 7402) {
            switch (buttonId) {
                case OPT4_FIRST: next(c, 7410); break; // Cook
                case OPT4_SECOND: next(c, 7420); break; // Talisman
                case OPT4_THIRD: next(c, 7430); break; // Romeo
                case OPT4_FOURTH: next(c, 7199); break; // Exit
            }
        }
        // Skill Quests Menu
        else if (c.dialogueAction == 7503) {
            switch (buttonId) {
                case OPT3_FIRST: next(c, 7510); break; // Fred
                case OPT3_SECOND: next(c, 7520); break; // Doric
                case OPT3_THIRD: next(c, 7199); break; // Exit
            }
        }
        // All Kinds Quests Menu
        else if (c.dialogueAction == 7603) {
            switch (buttonId) {
                case OPT4_FIRST: next(c, 7610); break; // Hetty
                case OPT4_SECOND: next(c, 7620); break; // Pirates
                case OPT4_THIRD: next(c, 7640); break; // Ernest
                case OPT4_FOURTH: next(c, 7199); break; // Exit
            }
        }
    }

    // ==========================================
    // QUEST COMPLETION PLACEHOLDERS
    // Replace these with your server's variables
    // ==========================================
    private boolean isDemonSlayerDone(Player c) { return false; }
    private boolean isVampyreSlayerDone(Player c) { return false; }
    private boolean isImpCatcherDone(Player c) { return false; }
    private boolean isCooksAssistantDone(Player c) { return false; } // e.g. return c.cookAss == 3;
    private boolean isRuneMysteriesDone(Player c) { return false; }
    private boolean isRomeoAndJulietDone(Player c) { return false; }
    private boolean isSheepShearerDone(Player c) { return false; }
    private boolean isDoricsQuestDone(Player c) { return false; }
    private boolean isWitchsPotionDone(Player c) { return false; }
    private boolean isPiratesTreasureDone(Player c) { return false; }
    private boolean isErnestTheChickenDone(Player c) { return false; }
    
    // Returns true if The Lost Tribe quest is actively at the point where the cellar collapsed
    private boolean isTheLostTribeActive(Player c) { return false; }
}