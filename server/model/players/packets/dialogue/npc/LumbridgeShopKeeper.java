package server.model.players.packets.dialogue.npc;

import server.model.npcs.NPC;
import server.model.npcs.NPCHandler;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class LumbridgeShopKeeper extends NPCDialogue {

    public LumbridgeShopKeeper(Player c) {
        super(c);
    }

    @Override
    public int[] getNPCIDs() {
        // 524 = Shop Keeper, 525 = Shop Assistant
        return new int[] { 2813, 2814 }; 
    }
    @Override
 	public String getDialogueRange() {
 		return "6000-6020";
 	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        String name = (npcId == 2814) ? "Shop assistant" : "Shop keeper";

        switch (startDialogueId) {

            // ==========================================
            // ENTRY POINT
            // ==========================================
            case 6000:
                if (isWithZanik(c)) {
                    npc(c, name, Anim.CALM_1, "Can I help you at all?");
                    c.nextChat = 6011; // Jump to Death to the Dorgeshuun path
                } else {
                    npc(c, name, Anim.CALM_1, "Can I help you at all?");
                    c.nextChat = 6001; // Standard path
                }
                break;

            // ==========================================
            // STANDARD PATH
            // ==========================================
            case 6001:
                options(c, 6001, "Yes please. What are you selling?", "No thanks.");
                break;

            case 6002:
                player(c, Anim.CALM_1, "No thanks.");
                c.nextChat = 0;
                break;

            // ==========================================
            // DEATH TO THE DORGESHUUN PATH
            // ==========================================
            case 6011:
                // 4511 is Zanik's standard NPC ID. We use c.getDH().npcChat directly to show her chathead!
                c.getDH().npcChat(4511, "Zanik", Anim.HAPPY.getAnimationId(), "It's a surface shop full of exotic surface goods!", "What are you selling?");
                c.nextChat = 6012;
                break;
            case 6012:
                npc(c, name, Anim.CONFUSED, "Um, yes, exotic surface goods... here, have a", "look at our wares...");
                c.nextChat = 6013;
                break;
            case 6013:
                c.getDH().npcChat(4511, "Zanik", Anim.HAPPY.getAnimationId(), "Wow... The bucket and the hammer, what are they", "made of?");
                c.nextChat = 6014;
                break;
            case 6014:
                npc(c, name, Anim.CONFUSED, "Um... wood. It, er, grows on trees?");
                c.nextChat = 6015;
                break;
            case 6015:
                c.getDH().npcChat(4511, "Zanik", Anim.HAPPY.getAnimationId(), "Amazing!");
                c.nextChat = 6016;
                break;
            case 6016:
                npc(c, name, Anim.CALM_1, "So, do you want to buy anything?");
                c.nextChat = 6017;
                break;
            case 6017:
                c.getDH().npcChat(4511, "Zanik", Anim.HAPPY.getAnimationId(), "A wood bucket! And a wood hammer! And a newcomer", "map! I brought some surface money.");
                c.nextChat = 6018;
                break;
            case 6018:
                npc(c, name, Anim.CALM_1, "There you go!");
                c.nextChat = 6019;
                break;
            case 6019:
                c.getDH().npcChat(4511, "Zanik", Anim.HAPPY.getAnimationId(), "Thank you!");
                c.nextChat = 6020;
                break;
            case 6020:
                npc(c, name, Anim.CALM_1, "How about you?");
                c.nextChat = 6001; // Loops back to the standard options menu!
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 6001) {
            if (buttonId == OPT2_FIRST) {
                // "Yes please. What are you selling?"
                // Open the Lumbridge General Store. Adjust the shop ID (e.g., 1) to match your shops.cfg
                c.getShops().openShop(1); 
                c.nextChat = -1;
               // end(c); // Closes the dialogue box
            } else if (buttonId == OPT2_SECOND) {
                // "No thanks."
                next(c, 6002);
            }
        }
    }

    // --- PLACEHOLDERS ---
    
    private boolean isWithZanik(Player c) {
    	NPC npc = NPCHandler.getNpc(c.talkingNpc);
    	if(c.summonId == 4506)
    		return true;
        // Return true if the player is currently on the specific step of Death to the Dorgeshuun
        // where Zanik is following them.
        // e.g., return c.deathToTheDorgeshuun >= 5 && c.hasFollower == 4511;
        return false;
    }
}