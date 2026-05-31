package server.model.players.packets.dialogue.npc.areas.kandarin.quests;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;

public class SirBedivere extends NPCDialogue {

    public SirBedivere(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3522; // Sir Bedivere
    }

    @Override
    public String getDialogueRange() {
        return "85200-85299";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {

        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];

        // TODO: Map these to actual variables or quest stages when you create these quests
        boolean kingsRansomActive = false;
        boolean kingsRansomDone = false;
        boolean holyGrailActive = false;

        switch (startDialogueId) {

            // --- Entry Point ---
            case 85200:
                npc(c, "Sir Bedivere", Anim.CALM_1, "May I help you?");

                // Route to correct dialogue tree based on highest quest progress
                if (kingsRansomDone) {
                    c.nextChat = 85260; // Post-King's Ransom
                } else if (kingsRansomActive) {
                    c.nextChat = 85250; // During King's Ransom
                } else if (holyGrailActive) {
                    c.nextChat = 85240; // During Holy Grail
                } else {
                    // Merlin's Crystal routing
                    if (merlinsCrystalStage == MerlinsCrystal.NOT_STARTED) {
                        c.nextChat = 85210;
                    } else if (merlinsCrystalStage == MerlinsCrystal.INVESTIGATING) {
                        c.nextChat = 85215;
                    } else if (merlinsCrystalStage == MerlinsCrystal.INFILTRATING_KEEP || merlinsCrystalStage == MerlinsCrystal.DEFEATED_MORDRED) {
                        c.nextChat = 85220;
                    } else if (merlinsCrystalStage >= MerlinsCrystal.GATHERING_ITEMS && merlinsCrystalStage < MerlinsCrystal.COMPLETED) {
                        c.nextChat = 85225;
                    } else if (merlinsCrystalStage == MerlinsCrystal.COMPLETED) {
                        c.nextChat = 85235;
                    }
                }
                break;

            // ==========================================
            // PRE-QUEST
            // ==========================================
            case 85210:
                player(c, Anim.CALM_1, "I'm really just looking for a quest...");
                c.nextChat = 85211;
                break;
            case 85211:
                npc(c, "Sir Bedivere", Anim.HAPPY, "Fortune favours us both then adventurer. I suggest you go", "and speak to King Arthur.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: INVESTIGATING
            // ==========================================
            case 85215:
                player(c, Anim.THINKING, "Merlin's in a crystal. Little help?");
                c.nextChat = 85216;
                break;
            case 85216:
                npc(c, "Sir Bedivere", Anim.CALM_1, "That is what we were hoping for from you, adventurer!");
                c.nextChat = 85217;
                break;
            case 85217:
                player(c, Anim.CALM_1, "Hmmm. Well, ok, thanks anyway.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: INVESTIGATING KEEP LE FAYE
            // ==========================================
            case 85220:
                player(c, Anim.THINKING, "I don't suppose you have any idea how to break into", "Mordred's fort do you?");
                c.nextChat = 85221;
                break;
            case 85221:
                npc(c, "Sir Bedivere", Anim.SAD, "I am afraid not. Would that we could! Mordred and his", "cronies have been thorns in our side for far too long", "already!");
                c.nextChat = 85222;
                break;
            case 85222:
                player(c, Anim.CALM_1, "Ok. Thanks. See you later!");
                c.nextChat = 85223;
                break;
            case 85223:
                npc(c, "Sir Bedivere", Anim.CALM_1, "Take care adventurer, Mordred is an evil and powerful foe.");
                c.nextChat = -1;
                break;

            // ==========================================
            // MERLIN'S CRYSTAL: SEARCHING FOR EXCALIBUR
            // ==========================================
            case 85225:
                player(c, Anim.THINKING, "Know anything about Excalibur?");
                c.nextChat = 85226;
                break;
            case 85226:
                npc(c, "Sir Bedivere", Anim.THINKING, "Um... it's a really good sword?");
                c.nextChat = 85227;
                break;
            case 85227:
                player(c, Anim.THINKING, "Know where I can find it?");
                c.nextChat = 85228;
                break;
            case 85228:
                npc(c, "Sir Bedivere", Anim.CALM_1, "Nope, sorry.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-MERLIN'S CRYSTAL
            // ==========================================
            case 85235:
                npc(c, "Sir Bedivere", Anim.HAPPY, "All Knights of the Round thank you for your assistance in", "this trying time for us.");
                c.nextChat = -1;
                break;

            // ==========================================
            // DURING HOLY GRAIL (Placeholders)
            // ==========================================
            case 85240:
                npc(c, "Sir Bedivere", Anim.THINKING, "You are looking for the Grail now adventurer?");
                c.nextChat = 85241;
                break;
            case 85241:
                player(c, Anim.HAPPY, "Absolutely.");
                c.nextChat = 85242;
                break;
            case 85242:
                npc(c, "Sir Bedivere", Anim.HAPPY, "The best of luck to you! Make the name of Camelot proud,", "and bring it back to us.");
                c.nextChat = -1;
                break;

            // ==========================================
            // DURING KING'S RANSOM (Placeholders)
            // ==========================================
            case 85250:
                player(c, Anim.THINKING, "How did you let a bunch of Black Knights get the better of", "you? I thought you guys were the toughest knights around.");
                c.nextChat = 85251;
                break;
            case 85251:
                npc(c, "Sir Bedivere", Anim.ANGRY, "We are true knights who fight for honour. Those Black", "Knights didn't fight fair, so we were unprepared.", "Most unchivalrous of them.");
                c.nextChat = -1;
                break;

            // ==========================================
            // POST-KING'S RANSOM (Placeholders)
            // ==========================================
            case 85260:
                npc(c, "Sir Bedivere", Anim.HAPPY, "Thank you for your assistance in our time of need.");
                c.nextChat = 85261;
                break;
            case 85261:
                player(c, Anim.HAPPY, "You're welcome.");
                c.nextChat = -1;
                break;
        }
    }
}