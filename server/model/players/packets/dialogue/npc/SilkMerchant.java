package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class SilkMerchant extends NPCDialogue {

	// Ardougne Silk Merchant (OSRS) — buys silk from player with negotiation

	public SilkMerchant(Player c) {
		super(c);
		// TODO Auto-generated constructor stub
	}

    @Override
    public int getNPCID() {
        return 8728; 
    }
	private static final int ITEM_COINS = 995;
	private static final int ITEM_SILK  = 950;

	private static final long SILK_COOLDOWN_MS = 20 * 60 * 1000L; // 20 minutes
	public int lastSilkTheftMs; // Consider moving this to Player as a long (e.g., c.lastSilkTheftMs)

	private boolean silkCooldownActive() {
	    return lastSilkTheftMs > 0 && (System.currentTimeMillis() - lastSilkTheftMs) < SILK_COOLDOWN_MS;
	}

	private void doSilkSale(int priceEach) {
	    // Re-check cooldown and inventory just before completing the sale
	    if (silkCooldownActive()) {
	        next(c, 270);
	        return;
	    }
	    if (!c.getItems().playerHasItem(ITEM_SILK)) {
	        npc(c, "Silk Merchant", Anim.CALM_1, "Looks like you don't have any silk after all.");
	        end(c);
	        return;
	    }
	    c.getItems().deleteItem(ITEM_SILK, 1);
	    c.getItems().addItem(ITEM_COINS, priceEach);
	    end(c);
	}

	@Override
	public void dialogue(Player c, int npcId, int startDialogueId) {
       // c.dialogueId = startDialogueId;
		switch(npcId) {
		case 8728:
		switch (startDialogueId) {

		    // Entry point: Talk-to silk merchant (Ardougne)
		    case 200:
		        if (silkCooldownActive()) {
		            next(c, 270);
		            break;
		        }
		        if (!c.getItems().playerHasItem(ITEM_SILK)) {
		            // No silk in inventory
		            npc(c, "Silk Merchant", Anim.CALM_1, "I buy silk. If you ever want to"," sell any silk, bring it here.");
		            c.nextChat = 0;
		            break;
		        }
		        // One or more silk in inventory — begin negotiation
		        player(c, Anim.CALM_1, "Hello. I have some fine silk from", " far away to sell to you.");
		        c.nextChat = 201;
		        break;

		    case 201:
		        npc(c, "Silk Merchant", Anim.CALM_1, "Ah I may be interested in that.","What sort of price were you looking at", "per piece of silk?");
		        c.nextChat = 202;
		        break;

		    case 202:
		        // Select an Option: 20 / 80 / 120 / 200
		        options(c, 300, "20 coins.", "80 coins.", "120 coins.", "200 coins.");
		        break;

		    // ========== 20 coins ==========
		    case 204:
		        player(c, Anim.CALM_1, "20 coins.");
		        c.nextChat = 205;
		        break;

		    case 205:
		        npc(c, "Silk Merchant", Anim.CALM_1, "Ok suits me.");
		        c.nextChat = 206;
		        break;

		    case 206:
		        doSilkSale(20);
		        break;

		    // ========== 80 coins ==========
		    case 207:
		        player(c, Anim.CALM_1, "80 coins.");
		        c.nextChat = 208;
		        break;

		    case 208:
		        npc(c, "Silk Merchant", Anim.CALM_1, "80 coins? That's a bit steep! How about 40 coins?");
		        c.nextChat = 209;
		        break;

		    case 209:
		        // Ok, 40 sounds good. / 50, and that's my final price. / No, that is not enough.
		        options3(c, 301, "Ok, 40 sounds good.", "50, and that's my final price.", "No, that is not enough.");
		        break;

		    // 80 -> accept 40
		    case 210:
		        player(c, Anim.CALM_1, "Ok, 40 sounds good.");
		        c.nextChat = 211;
		        break;

		    case 211:
		        doSilkSale(40);
		        break;

		    // 80 -> push 50 final
		    case 212:
		        player(c, Anim.CALM_1, "50, and that's my final price.");
		        c.nextChat = 213;
		        break;

		    case 213:
		        npc(c, "Silk Merchant", Anim.CALM_1, "Done.");
		        c.nextChat = 214;
		        break;

		    case 214:
		        doSilkSale(50);
		        break;

		    // 80 -> refuse
		    case 215:
		        player(c, Anim.CALM_1, "No, that is not enough.");
		        c.nextChat = 0;
		        break;

		    // ========== 120 coins ==========
		    case 220:
		        player(c, Anim.CALM_1, "120 coins.");
		        c.nextChat = 221;
		        break;

		    case 221:
		        npc(c, "Silk Merchant", Anim.CALM_1, "You'll never get that much for it.", " I'll be generous and give you 50 for it.");
		        c.nextChat = 222;
		        break;

		    case 222:
		        // Ok, I guess 50 will do. / I'll give it to you for 60. / No, that is not enough.
		        options3(c, 302, "Ok, I guess 50 will do.", "I'll give it to you for 60.", "No, that is not enough.");
		        break;

		    // 120 -> accept 50
		    case 223:
		        player(c, Anim.CALM_1, "Ok, I guess 50 will do.");
		        c.nextChat = 224;
		        break;

		    case 224:
		        doSilkSale(50);
		        break;

		    // 120 -> push 60
		    case 225:
		        player(c, Anim.CALM_1, "I'll give it to you for 60.");
		        c.nextChat = 226;
		        break;

		    case 226:
		        npc(c, "Silk Merchant", Anim.CALM_1, "You drive a hard bargain, but I guess that will have to do.");
		        c.nextChat = 227;
		        break;

		    case 227:
		        doSilkSale(60);
		        break;

		    // 120 -> refuse
		    case 228:
		        player(c, Anim.CALM_1, "No, that is not enough.");
		        c.nextChat = 0;
		        break;

		    // ========== 200 coins ==========
		    case 230:
		        player(c, Anim.CALM_1, "200 coins.");
		        c.nextChat = 231;
		        break;

		    case 231:
		        npc(c, "Silk Merchant", Anim.ANNOYED, "Don't be ridiculous! ","That is far too much.", " You insult me with that price.");
		        c.nextChat = 0;
		        break;

		    // ========== Cooldown refusal ==========
		    case 270:
		        npc(c, "Silk Merchant", Anim.ANNOYED, "I don't buy stolen goods!", "Come back when things have cooled down.");
		        c.nextChat = 0;
		        break;
		}
		break;
		}
	}
	 @Override
	    public void onOption(Player c, int buttonId) {
	        switch (c.dialogueAction) {
	            // initial 4-option price menu
	            case 300:
	                if (buttonId == OPT4_FIRST) {           // 20 coins
	                    next(c, 204);
	                } else if (buttonId == OPT4_SECOND) {   // 80 coins
	                    next(c, 207);
	                } else if (buttonId == OPT4_THIRD) {    // 120 coins
	                    next(c, 220);
	                } else if (buttonId == OPT4_FOURTH) {   // 200 coins
	                    next(c, 230);
	                }
	                return;

	            // 80 counter: 40 / 50 final / refuse
	            case 301:
	                if (buttonId == OPT3_FIRST) {           // Ok, 40 sounds good.
	                    next(c, 210);
	                } else if (buttonId == OPT3_SECOND) {   // 50, final
	                    next(c, 212);
	                } else if (buttonId == OPT3_THIRD) {    // No, not enough
	                    next(c, 215);
	                }
	                return;

	            // 120 counter: accept 50 / push 60 / refuse
	            case 302:
	                if (buttonId == OPT3_FIRST) {           // Ok, 50
	                    next(c, 223);
	                } else if (buttonId == OPT3_SECOND) {   // 60
	                    next(c, 225);
	                } else if (buttonId == OPT3_THIRD) {    // No, not enough
	                    next(c, 228);
	                }
	                return;
	        }
	    }
}