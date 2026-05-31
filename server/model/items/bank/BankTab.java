package server.model.items.bank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import server.Config;

/**
 * 
 * @author Jason http://www.rune-server.org/members/jason
 * @date Apr 11, 2014
 */
public class BankTab {

	CopyOnWriteArrayList<BankItem> bankItems = new CopyOnWriteArrayList<>();

	private int tabId;

	public BankTab() {
	}

	/**
	 * 
	 * @param tabId The bank tab id
	 */
	public BankTab(int tabId) {
		this.setTabId(tabId);
	}

	/**
	 *
	 * @param bankItem The object that contains the item id and amount
	 */
	public void add(BankItem bankItem) {
		if (bankItem == null || bankItem.getAmount() <= 0)
			return;

		for (BankItem item : bankItems) {
			if (item.getId() == bankItem.getId()) {
				long newAmount = (long) item.getAmount() + bankItem.getAmount(); // Use long to avoid overflow
				item.setAmount((int) Math.min(newAmount, Integer.MAX_VALUE));
				return;
			}
		}
		bankItems.add(bankItem);
	}

	public void addAt(BankItem item, int index) {
	    if (item == null || item.getId() <= 0 || item.getAmount() <= 0) {
	        return;
	    }

	    // If index is out of bounds or not specified, just append
	    if (index < 0 || index > bankItems.size()) {
	        add(item); // fallback to default add behavior
	        return;
	    }

	    // Try merging with existing item at that index
	    BankItem existing = index < bankItems.size() ? bankItems.get(index) : null;
	    if (existing != null && existing.getId() == item.getId()) {
	        existing.setAmount(existing.getAmount() + item.getAmount());
	        return;
	    }

	    // Otherwise, insert at specified index
	    bankItems.add(index, item);
	}

	/**
	 * 
	 * @param bankItem Removes the BankItem object from the ArrayList
	 */
	public void remove(BankItem bankItem, int type, boolean placeHolder) {
		Collection<BankItem> items = new ArrayList<>();
		for (BankItem item : bankItems) {
			if (item != null && item.getId() == bankItem.getId()) {
				if (item.getAmount() - bankItem.getAmount() <= 0) {
					if (placeHolder && type == 0) {
						item.setAmount(0);
					}
					else
					items.add(item); 
				} else {
					//Stil some item amount left
					item.setAmount(item.getAmount() - bankItem.getAmount());
					if (item.getAmount() <= 0 && placeHolder) {
						item.setAmount(0);
					}
				}
				break;
			}
		}
		bankItems.removeAll(items);
	}

	/**
	 * 
	 * @return The current amount of items in the bank tab
	 */
	public int size() {
		return bankItems.size();
	}

	/**
	 * 
	 * @return The amount of free slots remaining in this tab
	 */
	public int freeSlots() {
		return Config.BANK_SIZE - bankItems.size();
	}

	public boolean contains(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++)
			if (bankItems.get(i) != null)
				if (bankItems.get(i).getId() == bankItem.getId())
					return true;
		return false;
	}

	public boolean containsAmount(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++)
			if (bankItems.get(i) != null)
				if (bankItems.get(i).getId() == bankItem.getId())
					return bankItems.get(i).getAmount() >= bankItem.getAmount();
		return false;
	}

	public boolean spaceAvailable(BankItem bankItem) {
		for (int i = 0; i < bankItems.size(); i++) {
			if (bankItems.get(i) != null) {
				if (bankItems.get(i).getId() == bankItem.getId()) {
					long total = (long) bankItems.get(i).getAmount() + (long) bankItem.getAmount();
					return total > Integer.MAX_VALUE ? false : true;
				}
			}
		}
		return true;
	}

	public int getItemAmount(BankItem bankItem) {
		for (BankItem item : bankItems)
			if (item.getId() == bankItem.getId())
				return item.getAmount();
		return 0;
	}

	public BankItem getItem(int slot) {
		if (slot < bankItems.size()) {
			return bankItems.get(slot);
		}
		return null;
	}

	public BankItem getItem(BankItem item) {
		for (BankItem items : bankItems)
			if (items.getId() == item.getId())
				return items;
		return null;
	}

	public void setItem(int slot, BankItem item) {
		bankItems.set(slot, item);
	}

	public CopyOnWriteArrayList<BankItem> getItems() {
		return bankItems;
	}

	public int getTabId() {
		return tabId;
	}

	public void setTabId(int tabId) {
		this.tabId = tabId;
	}

	public BankItem getItemById(int i) {
		for (BankItem items : bankItems)
			if (items.getId() == i)
				return items;
		return null;
	}

}
