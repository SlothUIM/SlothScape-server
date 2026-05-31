package server.model.players.packets;

import server.model.players.Player;
import server.model.items.containers.FancyDressBox;
import server.model.minigames.NightmareZone;
import server.model.players.PacketType;
import server.model.players.PriceChecker;
import server.model.players.content.treasuretrails.types.ChallengeClue;

/**
 * Bank X Items
 **/
public class BankX2 implements PacketType {
	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int Xamount = c.getInStream().readDWord();

		if(c.playerName.equalsIgnoreCase("Sloth")) {
			c.sendMessage("Bank x2: interfaceid: " + c.xInterfaceId + ", removeSlot: " + c.xRemoveSlot + ", removeID: " + c.xRemoveId);
		}
		if (c.chatInputType != null && !c.chatInputType.isEmpty()) {
			if (c.coxParty != null && c.coxParty.getLeader() == c) {
				switch (c.chatInputType) {
					case "COX_SIZE":
						c.coxParty.setPreferredSize(Xamount);
						break;
					case "COX_COMBAT":
						c.coxParty.setPreferredCombat(Xamount);
						break;
					case "COX_TOTAL":
						c.coxParty.setPreferredTotal(Xamount);
						break;
					case "COX_SCALING":
						c.coxParty.setScaling(Xamount);
						break;
				}
				for (Player p : c.coxParty.getMembers()) {
					if (p != null) {
						switch (c.chatInputType) {
							case "COX_SIZE":
								p.getPA().sendFrame126("Preferred party size: " + (Xamount == 0 ? "---" : Xamount), 51006);
								break;
							case "COX_COMBAT":
								p.getPA().sendFrame126("Preferred combat level: " + (Xamount == 0 ? "---" : Xamount), 51007);
								break;
							case "COX_TOTAL":
								p.getPA().sendFrame126("Preferred skill total: " + (Xamount == 0 ? "---" : Xamount), 51008);
								break;
							case "COX_SCALING":
								p.getPA().sendFrame126("Scaling: " + (Xamount == 0 ? "---" : Xamount), 51009);
								break;
						}
					}
				}
			} else {
				c.sendMessage("Only the party leader can change raid settings.");
			}

			c.chatInputType = ""; // Reset the chat state
			return;
		}
		if (Xamount == 0)
			Xamount = 1;
		switch (c.xInterfaceId) {
			case 19524: case 19527: case 19530: case 19533: case 19536: case 19539: case 19542:
				if (c.isInInterface == 19543) {
					c.getFancyDressBox().handleWithdraw(c, c.xRemoveId, c.xInterfaceId, Xamount);
					return;
				}
				break;

			case 5064:
				if(c.inTrade) {
					c.sendMessage("You can't store items while trading!");
					return;
				}
				if(c.isBanking){
					c.getItems().addToBank(c.xRemoveId, Xamount, c.xRemoveSlot, true);
				} else if (c.isChecking) {
					PriceChecker.depositItem(c, c.playerItems[c.xRemoveSlot] - 1, Xamount);
				} else if (c.isInInterface == 19543) {
					c.getFancyDressBox().deposit(c, c.xRemoveId, Xamount);
					return;
				}
				break;

			case 43933:
				PriceChecker.withdrawItem(c, c.price[c.xRemoveSlot], c.xRemoveSlot, Xamount);
				break;

			case -21826:
			case 43710:
				c.getItems().addtoLootbagFromDeposit(c.xRemoveSlot, c.xRemoveSlot, Xamount);
				break;

			case 56432:
			case 56434:
			case 5382:
				c.getItems().removeFromBank(c.bankItems[c.xRemoveSlot], Xamount, true);
				break;

			case 3900:
				c.getShops().buyItem(c.xRemoveId, c.xRemoveSlot, Xamount);
				break;

			case 3823:
				if(c.xRemoveId == 995){
					c.sendMessage("You can't sell coins.");
					return;
				}
				c.getShops().sellItem(c.xRemoveId , c.xRemoveSlot, Xamount);
				break;

			case 3322:
				if (c.duelStatus <= 0) {
				} else {
				}
				break;

			case 3415:
				if (c.duelStatus <= 0) {
				}
				break;

			case 41710:
			case 41711:
				c.getRunePouch().addRunesFromInventory(c.xRemoveId, c.xRemoveSlot, Xamount);
				break;

			case 44722:
				c.toMake = Xamount;
				break;

			case 6669:
				break;
		}

		// Baraek's Challenge Scroll: "How many stalls are there in Varrock Square?"
		if (!c.answeringChallengeNpc.isEmpty()) {
			boolean correct = ChallengeClue.isCorrect(c.answeringChallengeNpc, Xamount);
			int npcId = server.model.npcs.NPCHandler.npcs[c.npcClickIndex].npcType;

			if (correct) {
				c.getDH().sendDialogues(2784, npcId);
			} else {
				c.getDH().sendDialogues(2783, npcId);
			}
			c.answeringChallengeNpc = "";
			return;
		}

		if(c.addingCharges || c.addingDarts)
			c.getCharges().addCharges(c.xRemoveId, c.xRemoveSlot, Xamount);
		if(c.addingToRP && c.xInterfaceId == 41711)
			c.getCharges().addRunes(c.xRemoveId, c.xRemoveSlot, Xamount);
		if(c.addingToNMZCoffer && c.xInterfaceId == 60013)
			NightmareZone.addToCoffer(c, Xamount);
		if(c.withdrawFromNMZCoffer && c.xInterfaceId == 60013)
			NightmareZone.removeFromCoffer(c, Xamount);
	}
}