package server.model.players.packets;

import server.model.items.GameItem;
import server.model.items.Item;
import server.model.items.bank.BankItem;
import server.model.players.Player;
import server.model.players.PacketType;
import server.model.players.PriceChecker;

/**
 * Bank All Items
 **/
public class BankAllbutPlaceholder implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int removeSlot = c.getInStream().readUnsignedWordA();
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWordA();

				System.out.println("BankAllbutPlaceholder "+interfaceId+"]: "+removeId);
		switch (interfaceId) {

		case 1688:
			c.getPA().useOperate(removeId, 5);
			break;
		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 10);
			break;
			
		case 41711:
		//if(c.addingToRP){
		for(int i = 0; i < 20; i++){
			if(removeId == c.RuneIDS[i]){
				c.getCharges().addRunes(removeId, removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
				//c.openRunePouch();
				break;
			}
		}
		break;
		
			case -21826:
		case 43710:
		c.getItems().addtoLootbagFromDeposit(removeId, removeSlot, c.getItems().itemAmount(c.playerItems[removeSlot]));
		break;
		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 10);
			break;
		

		case 5382:
			int amount = c.getBank().getCurrentBankTab().getItemAmount(new BankItem(removeId + 1));
			if (amount < 1)
				return;
			if (amount == 1) {
				c.sendMessage("Your bank only contains one of this item.");
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().removeItem(removeId, amount - 1);
				return;
			}
			if ((c.getBank().getCurrentBankTab().getItemAmount(new BankItem(removeId + 1)) - 1) > 1)
				c.getItems().removeFromBank(removeId, amount - 1, true);
			break;


		case 43933:
			PriceChecker.withdrawItem(c, removeId, removeSlot,
					c.priceN[removeSlot]);
		break;

		}
	}

}
