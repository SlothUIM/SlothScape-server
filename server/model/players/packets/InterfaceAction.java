package server.model.players.packets;

import server.content.clans.Clan;
import server.model.minigames.cox.CoxPartyManager;
import server.model.minigames.raids.RaidParty;
import server.model.players.Player;
import server.model.players.PlayerSave;
import server.model.players.PacketType;
import server.model.players.skills.Skill;
import server.util.Misc;

import java.util.ArrayList;
import java.util.List;

public class InterfaceAction implements PacketType {

	boolean toggle = false;
	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int id = player.getInStream().readUnsignedWord();
		int action = player.getInStream().readUnsignedWord();
		// --- THE MAIN WORLD LIST (IDs 55050 to 55164) ---
		if (id >= 55050 && id <= 55170) {
			// Reverse engineer the world number based on the +6 ID spacing
			int worldNum = ((id - 55050) / 6) + 1;

			if (action == 0) {
				// OPTION 1: Hop-to
				if(worldNum == 4 && !player.playerName.equalsIgnoreCase("sloth")){
					player.sendMessage("You cannot hop to this world.");
					return;
				} else if(worldNum == 4 && player.playerName.equalsIgnoreCase("sloth")){
					worldNum = 4;
				}

				// Assuming World 1 is port 43594, World 2 is 43595, etc.
				int targetPort = 43593 + worldNum;
				player.getPA().sendWorldHop(targetPort);

			} else if (action == 1) {
				// OPTION 2: Favourite
				// Push current Fav 1 to Fav 2, and make this new world Fav 1
				if (player.favoriteWorld1 == worldNum || player.favoriteWorld2 == worldNum) {
					player.sendMessage("World " + worldNum + " is already in your favourites.");
					return;
				}

				player.getPA().setInterfaceVisible(55410, false);
				player.getPA().setInterfaceVisible(55400, false);
				player.favoriteWorld2 = player.favoriteWorld1;
				player.favoriteWorld1 = worldNum;
				player.sendMessage("You have favorited World " + worldNum + ".");

				// Instantly refresh the interface text to show the new favorites!
				player.getPA().updateWorldTabCounts();

			}
			return; // Stop processing so it doesn't hit the switch block
		}

// --- THE PINNED FAVORITES BAR (IDs 55400 & 55410) ---
		if (id == 55400 || id == 55410) {
			// Figure out which favorite slot they clicked
			int targetWorld = (id == 55400) ? player.favoriteWorld1 : player.favoriteWorld2;

			if (targetWorld <= 0) {
				player.sendMessage("You haven't set a favourite for this slot yet.");
				return;
			}

			if (action == 0) {
				// OPTION 1: Hop-to
				int targetPort = 43593 + targetWorld;
				player.getPA().sendWorldHop(targetPort);

			} else if (action == 1) {
				// OPTION 2: Favourite (Acts as a removal/un-favourite for pinned slots)
				if (id == 55400) {
					player.favoriteWorld1 = player.favoriteWorld2; // Shift slot 2 up
					player.favoriteWorld2 = 0; // Clear slot 2
					if(player.favoriteWorld1 == 0)
						player.getPA().setInterfaceVisible(55400, true);
					else
						player.getPA().setInterfaceVisible(55400, false);
					player.getPA().setInterfaceVisible(55410, true);
				} else {
					player.favoriteWorld2 = 0; // Just clear slot 2
				}
				player.sendMessage("Favourite removed.");
				player.getPA().updateWorldTabCounts();
			}
			return;
		}
		if (id >= 51400 && id <= 51439) {
			if (player.coxParty == null) return;

			// 1. Figure out EXACTLY who they clicked by recreating their unique sorted list
			int listIndex = id - 51400; // E.g., Button 51402 = Index 2
			List<Player> sortedMembers = new ArrayList<>(player.coxParty.getMembers());

			// Use the method we put in RaidParty.java to sort it identically to their screen
			player.coxParty.sortMembers(sortedMembers, player.activeCoxSort, player.coxSortDescending);

			// If they clicked an empty row, do nothing
			if (listIndex >= sortedMembers.size()) return;

			// We successfully found the exact player they clicked on!
			Player target = sortedMembers.get(listIndex);
			// ==========================================
			// ACTION 0: STATS OVERLAY
			// ==========================================
			if (action == 0) {
				toggle = !toggle;
				//if(target.playerName != player.playerName)
					//toggle = true;
				// 1. Unhide the overlay (Assuming your base uses sendInterfaceHidden)
				player.getPA().setInterfaceVisible(51300, toggle);
				String targetName = target.playerName;
				if (targetName != null && targetName.length() > 0) {
					// Capitalize first letter, lowercase the rest
					targetName = targetName.substring(0, 1).toUpperCase() + targetName.substring(1).toLowerCase();
				} else {
					targetName = "Unknown";
				}
				// 2. Set the target's name
				player.getPA().sendFrame126(targetName, 51302);

				// 3. Loop through and set all 23 of their skills
				// (Assuming your base uses playerLevel array and the text IDs start at 51350)
				for (int i = 0; i <= 23; i++) {
					Skill skill = Skill.forId(i);
					// NOTE: Double check your text ID starts at 51350 for the stats!
					if(i == 23)
						player.getPA().sendFrame126(String.valueOf(target.getSkills().getTotalLevel()), 51350 + i);
					else
						player.getPA().sendFrame126(String.valueOf(target.getSkills().getActualLevel(skill)), 51350 + i);
				}
			}
			// ==========================================
			// ACTION 1: KICK MEMBER
			// ==========================================
			else if (action == 1) {
				// Ensure only the leader can kick
				if (player.coxParty.getLeader() != player) {
					player.sendMessage("Only the party leader can kick members.");
					return;
				}

				// Ensure they aren't trying to kick themselves
				if (target == player) {
					player.sendMessage("You cannot kick yourself. Use the Disband button instead.");
					return;
				}

				// Remove the target and notify them
				player.coxParty.removeMember(target);
				target.sendMessage("You have been kicked from the raiding party.");
				target.getPA().closeAllWindows();
			}
			return;
		}
		switch (id) {
		case 58304:
			if (action == 1) {
				if(player.getPA().getClan() != null) {
				player.getPA().getClan().delete();
				player.getPA().setClanData();
				}
			}
			break;
		case 58307:
		case 58310:
		case 58313:
		case 58316:
			Clan clan = player.getPA().getClan();
			if (clan != null) {
				if (id == 58307) {
					clan.setRankCanJoin(action == 0 ? -1 : action);
				} else if (id == 58310) {
					clan.setRankCanTalk(action == 0 ? -1 : action);
				} else if (id == 58313) {
					clan.setRankCanKick(action == 0 ? -1 : action);
				} else if (id == 58316) {
					clan.setRankCanBan(action == 0 ? -1 : action);
				}
				String title = "";
				if (id == 58307) {
					title = clan.getRankTitle(clan.whoCanJoin) + (clan.whoCanJoin > Clan.Rank.ANYONE && clan.whoCanJoin < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 58310) {
					title = clan.getRankTitle(clan.whoCanTalk) + (clan.whoCanTalk > Clan.Rank.ANYONE && clan.whoCanTalk < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 58313) {
					title = clan.getRankTitle(clan.whoCanKick) + (clan.whoCanKick > Clan.Rank.ANYONE && clan.whoCanKick < Clan.Rank.OWNER ? "+" : "");
				} else if (id == 58316) {
					title = clan.getRankTitle(clan.whoCanBan) + (clan.whoCanBan > Clan.Rank.ANYONE && clan.whoCanBan < Clan.Rank.OWNER ? "+" : "");
				}
				player.getPA().sendFrame126(title, id + 2);
			}
			break;

			case 27102://attack
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(0, 0, true);
				break;
			case 27105://strength
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(1, 0, true);
				break;
			case 27108://defence
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(2, 0, true);
				break;
			case 27111://ranged
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(3, 0, true);
				break;
			case 27114://prayer
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(4, 0, true);
				break;
			case 27117://magic
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(5, 0, true);
				break;

			case 27120://runecrafting
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(6, 0, true);
				break;
			case 27123://construction
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(7, 0, true);
				break;

			case 27103://hitpoints
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(8, 0, true);
				break;
			case 27106://agility
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(9, 0, true);
				break;
			case 27109://agility
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(10, 0, true);
				break;
			case 27112:
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(11, 0, true);
				break;
			case 27115:
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(12, 0, true);
				break;
			case 27118:
				player.getSkillGuide().clearInterface();
				player.getSkillGuide().LoadMainInterface(13, 0, true);
				break;
				case 27121:
					player.getSkillGuide().clearInterface();
					player.getSkillGuide().LoadMainInterface(14, 0, true);
					break;
					case 27124:
						player.getSkillGuide().clearInterface();
						player.getSkillGuide().LoadMainInterface(15, 0, true);
						break;
						case 27104:
							player.getSkillGuide().clearInterface();
							player.getSkillGuide().LoadMainInterface(16, 0, true);
							break;
							case 27107:
								player.getSkillGuide().clearInterface();
								player.getSkillGuide().LoadMainInterface(17, 0, true);
								break;
								case 27110:
									player.getSkillGuide().clearInterface();
									player.getSkillGuide().LoadMainInterface(18, 0, true);
									break;
									case 27113:
										player.getSkillGuide().clearInterface();
										player.getSkillGuide().LoadMainInterface(19, 0, true);
											break;
											case 27116:
												player.getSkillGuide().clearInterface();
												player.getSkillGuide().LoadMainInterface(20, 0, true);
												break;
												case 27119:
													player.getSkillGuide().clearInterface();
													player.getSkillGuide().LoadMainInterface(21, 0, true);
													break;
													case 27122:
														player.getSkillGuide().clearInterface();
														player.getSkillGuide().LoadMainInterface(22, 0, true);
														break;

		default:
			 System.out.println("Interface action: [id=" + id +",action=" +
			 action +"]");
			break;
		}
		System.out.println("Interface action: [id=" + id +",action=" +
				action +"]");
		if (id >= 58323 && id < 58423) {
		    Clan clan = player.getPA().getClan();
		    if (clan != null) {
		        int clickIndex = id - 58323;
		        List<String> setupList = player.getClanSetupList();

		        // Target the exact name from the cached snapshot
		        if (clickIndex >= 0 && clickIndex < setupList.size()) {
		            String targetMember = setupList.get(clickIndex);

		            if (action == 0) { // Demote
		                clan.demote(targetMember);
		                player.sendMessage(Misc.capitalize(targetMember) + " removed from clan ranks.");
		            } else { // Promote
		                clan.setRank(targetMember, action);
		            }
		            
		            // Refresh UI: This will rebuild the cache list for the next click
		            player.getPA().setClanData();
		        }
		    }
		}
		if (id >= 18665 && id <= 18764) {
			for (int index = 0; index < 100; index++) {
				if (id == index + 18665) {
					String member = player.clan.activeMembers.get(id - 18665);
					switch (action) {
					case 0:
						if (player.clan.isFounder(player.playerName)) {
							player.getPA().showInterface(58300);
						}
						break;
					case 1:
						if (member.equalsIgnoreCase(player.playerName)) {
							player.sendMessage("You can't kick yourself!");
						} else {
							if (player.clan.canKick(player.playerName)) {
								player.clan.kickMember(member);
							} else {
								player.sendMessage("You do not have sufficient privileges to do this.");
							}
						}
						break;
					case 2:
						if (member.length() == 0) {
							break;
						} else if (member.length() > 12) {
							member = member.substring(0, 12);
						}
						if (member.equalsIgnoreCase(player.playerName)) {
							break;
						}
						if (!PlayerSave.playerExists(member.toLowerCase())) {
							player.sendMessage("This player doesn't exist!");
							break;
						}
						Clan clan = player.getPA().getClan();
						if (clan.isRanked(member)) {
							try {
								player.sendMessage("You cannot ban a ranked member.");
								break;
							} catch (Exception e) {
							}
						}
						if (clan != null) {
							clan.banMember(Misc.formatPlayerName(member));
							player.getPA().setClanData();
							clan.save();
						}
						break;
					}
					break;
				}
			}
		}
	}
}