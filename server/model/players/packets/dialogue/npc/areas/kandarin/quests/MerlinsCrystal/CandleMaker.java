package server.model.players.packets.dialogue.npc.areas.kandarin.quests.MerlinsCrystal;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.quests.MerlinsCrystal;

public class CandleMaker extends NPCDialogue {

    public CandleMaker(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return 3199; // Candle maker in Catherby
    }

    @Override
    public String getDialogueRange() {
        return "86000-86099";
    }

    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {

        int merlinsCrystalStage = c.questStages[MerlinsCrystal.QUEST_ID];

        switch (startDialogueId) {
            // --- Entry Point ---
            case 86000:
                // If they are on the quest step and have spoken to him before, he asks for wax immediately
                if (merlinsCrystalStage == MerlinsCrystal.GATHERING_ITEMS && c.hasAskedForBlackCandle) {
                    c.nextChat = 86050;
                } else {
                    npc(c, "Candle maker", Anim.HAPPY, "Hi! Would you be interested in some of my fine candles?");
                    c.nextChat = 86001;
                }
                break;

            case 86001:
                if (merlinsCrystalStage == MerlinsCrystal.GATHERING_ITEMS) {
                    options3(c, 86001, "Yes please.", "No thank you.", "Have you got any black candles?");
                } else {
                    options(c, 86001, "Yes please.", "No thank you.");
                }
                break;

            // ==========================================
            // SHOP OPTIONS
            // ==========================================
            case 86010:
                player(c, Anim.HAPPY, "Yes please.");
                c.nextChat = -1;
                c.getShops().openShop(22); // Assuming Catherby Candle Shop is 22, adjust accordingly
                break;
            case 86020:
                player(c, Anim.CALM_1, "No thank you.");
                c.nextChat = -1;
                break;

            // ==========================================
            // QUEST OPTION: BLACK CANDLES
            // ==========================================
            case 86030:
                player(c, Anim.CONFUSED, "Have you got any black candles?");
                c.nextChat = 86031;

                // Save flag so he remembers we asked
                c.hasAskedForBlackCandle = true;
                break;
            case 86031:
                npc(c, "Candle maker", Anim.SCARED, "BLACK candles???");
                c.nextChat = 86032;
                break;
            case 86032:
                npc(c, "Candle maker", Anim.SAD, "Hmmm. In the candle making trade, we have a tradition", "that it's very bad luck to make black candles.", "VERY bad luck.");
                c.nextChat = 86033;
                break;
            case 86033:
                player(c, Anim.CALM_1, "I will pay good money for one...");
                c.nextChat = 86034;
                break;
            case 86034:
                npc(c, "Candle maker", Anim.SAD, "I still dunno...", "IF you can bring me a bucket FULL of wax.");
                c.nextChat = -1;
                break;

            // ==========================================
            // RETURNING WITH WAX
            // ==========================================
            case 86050:
                npc(c, "Candle maker", Anim.CONFUSED, "Have you got any wax yet?");
                c.nextChat = 86051;
                break;
            case 86051:
                if (c.getItems().playerHasItem(MerlinsCrystal.BUCKET_OF_WAX)) {
                    c.nextChat = 86060; // Has Wax
                } else {
                    c.nextChat = 86070; // No Wax
                }
                break;

            case 86060:
                player(c, Anim.HAPPY, "Yes, I have some now.");
                c.nextChat = 86061;
                break;
            case 86061:
                c.getItems().deleteItem(MerlinsCrystal.BUCKET_OF_WAX, 1);
                c.getItems().addItem(326, 1); // 326 = Unlit Black Candle
                c.getItems().addItem(1925, 1); // Gives back the empty bucket
                c.getDH().sendStatement("You exchange the wax with the candle maker for a black candle.");
                c.nextChat = -1;
                break;

            case 86070:
                player(c, Anim.SAD, "Nope, not yet.");
                c.nextChat = -1;
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 86001) {
            // Handling dynamic 2 vs 3 options menu
            if (c.questStages[MerlinsCrystal.QUEST_ID] == MerlinsCrystal.GATHERING_ITEMS) {
                if (buttonId == OPT3_FIRST) next(c, 86010);
                if (buttonId == OPT3_SECOND) next(c, 86020);
                if (buttonId == OPT3_THIRD) next(c, 86030); // Black Candle
            } else {
                if (buttonId == OPT2_FIRST) next(c, 86010);
                if (buttonId == OPT2_SECOND) next(c, 86020);
            }
        }
    }
}