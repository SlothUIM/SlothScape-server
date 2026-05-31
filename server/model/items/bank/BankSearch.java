package server.model.items.bank;

import java.util.ArrayList;
import java.util.Collection;

import server.model.players.PacketType;
import server.model.players.Player;
import server.util.Misc;
import server.model.items.ItemAssistant;

public class BankSearch implements PacketType {
	

	String text;
	Player player;
	BankTab tab = new BankTab();
	boolean searching;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
	    player = c;
	    String textSent = Misc.longToPlayerName2(c.getInStream().readQWord());
	    text = textSent.replaceAll("_", " ");
	 // If search text is empty, reset bank
	    if (text.trim().isEmpty()) {
	        reset();
	        return;
	    }
	    // Mark that we're searching
	    searching = true;

	    // Update the search results tab
	    updateItems();

	    // Switch to the temporary search results tab
	    player.getBank().setCurrentBankTab(tab);

	    // Refresh bank to show only matching items
	    player.getItems().resetBank();

	    // Optional feedback
	    player.sendMessage("Showing results for: " + text);
	}
	public void updateItems() {
	    tab.bankItems.clear();
	    Collection<BankItem> results = new ArrayList<>();

	    for (BankTab bankTab : player.getBank().getBankTab()) {
	        for (BankItem item : bankTab.getItems()) {
	            if (item.getAmount() > 0) {
	                String itemName = ItemAssistant.getItemName(item.getId() - 1).toLowerCase();
	                if (itemName.contains(text.toLowerCase())) {
	                    results.add(item);
	                }
	            }
	        }
	    }

	    tab.bankItems.addAll(results);
	}


	public void removeItem(int itemId, int amount) {
		if (!tab.contains(new BankItem(itemId + 1, amount))) {
			return;
		}
		for (BankTab tab : player.getBank().getBankTab()) {
			for (BankItem bankItem : tab.getItems()) {
				if (itemId == bankItem.getId() - 1) {
					player.getBank().setCurrentBankTab(tab);
					//player.getItems().removeFromBank(itemId, amount, false);
					break;
				}
			}
		}
		player.getBank().setCurrentBankTab(tab);
		updateItems();
		player.getItems().resetBank();
	}

	public void reset() {
		if (player.getBank().getBankSearch().isSearching()) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(0));
			player.getItems().rearrangeBank();
			player.getItems().resetBank();
			player.sendMessage("Search results reset.");
			searching = false;
		}
	}

	public void reset(int tabId) {
		if (player.getBank().getBankSearch().isSearching()) {
			player.getBank().setCurrentBankTab(player.getBank().getBankTab(tabId));
			player.getItems().resetBank();
			player.sendMessage("Search results reset.");
			searching = false;
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public BankTab getTab() {
		return this.tab;
	}

	public boolean isSearching() {
		return this.searching;
	}

	public boolean setSearching(boolean searching) {
		return this.searching = searching;
	}

}
