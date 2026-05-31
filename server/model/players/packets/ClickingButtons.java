package server.model.players.packets;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import server.Config;
import server.Server;
import server.model.items.GameItem;
import server.model.items.ItemAssistant;
import server.model.items.bank.Bank;
import server.model.items.bank.BankTab;
import server.model.items.collectionlog.CollectionLogData;
import server.model.items.collectionlog.boss.*;
import server.model.items.collectionlog.clues.*;
import server.model.items.collectionlog.other.*;
import server.model.items.collectionlog.raid.*;
import server.model.minigames.cox.CoxButtonHandler;
import server.model.players.Player;
import server.model.players.Music;
import server.model.players.skills.*;
import server.model.players.skills.agility.AgilityHandler;
import server.model.players.skills.construction.Construction;
import server.model.players.skills.construction.House;
import server.model.players.skills.construction.HouseData;
import server.model.players.skills.construction.RoomObject;
import server.model.players.skills.construction.sawmill.Sawmill;
import server.model.players.skills.cooking.*;
import server.model.players.quests.*;
import server.model.players.*;
import server.model.players.PacketType;
import server.model.players.PlayerHandler;
import server.util.Misc;
import server.world.World;
import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.event.CycleEventHandler;
import server.model.players.PriceChecker;
import server.model.players.combat.Special;
import server.model.players.combat.Specials;
import server.model.players.combat.effects.bolts.BoltEnchant;
import server.model.players.combat.magic.MagicData;
import server.model.players.content.DeathRetrieval;
import server.model.players.content.GodBookManager;
import server.model.players.content.ItemsKeptOnDeath;
import server.model.players.content.teleports.FairyRings;
import server.model.players.content.teleports.JewelleryTeleports;
import server.model.players.content.treasuretrails.types.EmoteClues;
import server.model.players.packets.dialogue.DialogueOptions;
import server.model.minigames.GnomeGlider;
import server.model.minigames.NMZBosses;
import server.model.minigames.NMZRewards;
import server.model.minigames.NightmareZone;
import server.model.multiplayer_session.MultiplayerSessionStage;
import server.model.multiplayer_session.MultiplayerSessionType;
import server.model.multiplayer_session.duel.DuelSession;
import server.model.multiplayer_session.duel.DuelSessionRules.Rule;
/**
 * Clicking most buttons
 **/
public class ClickingButtons implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int actionButtonId = Misc.hexToInt(c.getInStream().buffer, 0,
				packetSize);
		int currentConfigValue = 0;
		GnomeGlider.flightButtons(c, actionButtonId);
		QuestAssistant.questButtons(c, actionButtonId);
		DialogueOptions.handleDialogueOptions(c, actionButtonId);
		//Smelting.getBar(c, actionButtonId);
		int interfaceId = c.getInStream().readUnsignedWord();
		if (c.isDead)
			return;

		if (RoomObject.handleButtons(c, actionButtonId)) {
			}
		if (EmoteClues.handleEmote(c, interfaceId)) {
			}
		if(CoxButtonHandler.handleButton(c, interfaceId)){
			return;
		}
		if (c.getCrafting().handleButtons(actionButtonId)) {
		    return;
		}
		if(c.teleAction > 0)
			return;
		if (c.playerRights == 3){
			Misc.println(c.playerName + " - actionbutton: " + actionButtonId+ " InterfaceId: " + interfaceId);
			c.sendMessage("actionbutton: " + actionButtonId + " InterfaceId: " + interfaceId);
		}
		if (c.getMusic().handleMusicButton(actionButtonId)) {
			return;
		}


		if (actionButtonId > 97000 && actionButtonId <= 97231) {
			int maxVolume = 100;  // Maximum volume percentage
			int minId = 97001;    // Minimum actionButtonId in the range
			int maxId = 97231;    // Maximum actionButtonId in the range

			// Calculate the volume as a percentage based on the actionButtonId
			int volume = (actionButtonId - minId) * maxVolume / (maxId - minId);

			c.getPA().sendFrame36(168, 0);
			c.getPA().sendFrame126("Music volume: " + volume + "%", 25819);
		} else if(actionButtonId == 97000) {
			c.getPA().sendFrame36(168, 1);
			c.getPA().sendFrame126("Music volume: Muted", 25819);
		}

		if (interfaceId > 23757 && interfaceId <= 23942) {
			int maxVolume = 100;  // Maximum volume percentage
			int minId = 23758;    // Minimum actionButtonId in the range
			int maxId = 23942;    // Maximum actionButtonId in the range

			// Calculate the volume as a percentage based on the actionButtonId
			int volume2 = (interfaceId - minId) * maxVolume / (maxId - minId);

			c.getPA().sendFrame36(169, 0);
			c.getPA().sendFrame126("Effect volume: " + volume2 + "%", 25820);
		} else if(interfaceId == 23765) {
			c.getPA().sendFrame36(169, 1);
			c.getPA().sendFrame126("Effect volume: Muted", 25820);
		}
		switch(interfaceId) {
			case 57776:
				c.getPA().showInterface(19000);
				break;
			case 985:
				c.lobbyLogout();
				break;
			case 986:
				c.getPA().updateWorldTabCounts();
				c.setSidebarInterface(10, 55000);
				break;
			case 55007:
				c.setSidebarInterface(10, 2449);
				break;
		case 915:
			c.splitChat = false;
			break;
		case 36436:
			c.getDH().boltEnchantInterface();
			break;
		case 37433:
			c.getPA().sendPlayerObjectAnimation(c, 3207, 3423, 4129, 3, 0, 0);
			//c.splitChat = true;
			break;
			// inside handleButton or switch(buttonId)
		case 44721: case 44722: case 44723:
		case 44724: case 44725: case 44726: case 44727: case 44728: case 44729: case 44730: case 44731:
		case 44744: case 44745: case 44746: case 44747: case 44748: case 44749: case 44750: case 44751:
		case 44754: case 44755: case 44756: case 44757: case 44758: case 44759: case 44760: case 44761:
			switch (c.activeAction) {
	        case BOLT_ENCHANT:
	            BoltEnchant.handleButton(c, interfaceId);
	            break;
	            
	        case SAWMILL:
	            Sawmill.handleButton(c, interfaceId);
	            break;
	            
	        case SMITHING:
	            c.getSmithing().handleButton(c, interfaceId);
	            break;
	        case FLETCHING: 
	        	Fletching.handleButton(c, interfaceId); 
	        	break;

	        case SPINNING: 
	    		if (c.getCrafting().handleButtons(interfaceId)) {
	    		   // return;
	    		}
			break;
	        case GLASSBLOWING: 
	    		if (c.getCrafting().handleButtons(interfaceId)) {
	    		   // return;
	    		}
			break;
	        case LEATHERCRAFTING: 
	    		if (c.getCrafting().handleButtons(interfaceId)) {
	    		   // return;
	    		}
			break;
	        default:
	            // Optional: c.getPA().removeAllWindows();
	        	c.sendMessage("Nothing interesting happens.");
	            break;
	    }
			return;
		
		case 7487:
			DuelSession session = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
					MultiplayerSessionType.DUEL);
			if (session != null) {
				if (session.getRules().contains(Rule.NO_SPECIAL_ATTACK)) {
					c.sendMessage("You are not permitted to activate special attacks during a duel.");
					return;
				}
			}
			Special special = Specials.DRAGON_BATTLEAXE.getSpecial();
			if (c.specAmount < special.getRequiredCost()) {
				c.sendMessage("You don't have the special amount to use this.");
				return;
			}
			if (!Arrays.stream(special.getWeapon()).anyMatch(axe -> c.getItems().isWearingItem(axe))) {
				return;
			}
			special.activate(c, null, null);
			c.specAmount -= special.getRequiredCost();
			c.usingSpecial = false;
			c.getItems().updateSpecialBar();
			break;
		case 7788:
			if (c.getItems().isWearingItem(11791, c.playerWeapon)
					|| c.getItems().isWearingItem(12904, c.playerWeapon)) {
				session = (DuelSession) World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c,
						MultiplayerSessionType.DUEL);
				if (session != null) {
					if (session.getRules().contains(Rule.NO_SPECIAL_ATTACK)) {
						c.sendMessage("You are not permitted to activate special attacks during a duel.");
						return;
					}
				}
				Special sotd = Specials.STAFF_OF_THE_DEAD.getSpecial();
				if (c.specAmount >= sotd.getRequiredCost()) {
					c.specAmount -= sotd.getRequiredCost();
					sotd.activate(c, c, null);
					c.specBarId = 7812;
					c.usingSpecial = false;
					c.getItems().updateSpecialBar();
					return;
				}
			}
			c.specBarId = 7812;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;
		case 55814:
			boolean toggle = true;
			toggle = !toggle;
			c.getPA().sendFrame36(195, toggle ? 2 : 0);
			break;
		case 37853:
			c.hideRoofs = !c.hideRoofs;
			//c.getPA().sendFrame36(356, c.hideRoofs ? 1 : 0); // Display + sprite for songs not in the playlist
			break;
		case 37854:
			c.ScrollWheel = !c.ScrollWheel;
			//c.getPA().sendFrame36(213, c.ScrollWheel ? 1 : 0); // Display + sprite for songs not in the playlist
			break;
		case 28932:
			c.transChat = !c.transChat;
			//c.getPA().sendFrame36(214, c.transChat ? 1 : 0); 
			break;
		case 28946:
			c.smoothshading = !c.smoothshading;
			//c.getPA().sendFrame36(215, c.smoothshading ? 1 : 0); 
			break;
		case 29000:
			c.fogToggle = !c.fogToggle;
			//c.getPA().sendFrame36(216, c.fogToggle ? 0 : 1); 
			break;
		case 29001:
			c.dataorbs = !c.dataorbs;
			//c.getPA().sendFrame36(217, c.dataorbs ? 1 : 0); 
			break;
		case 37855:
			c.toggle = !c.toggle;
			c.getPA().sendFrame126("Show @whi@" + (c.toggle ? "less": "more") + " @or1@information", 29891);
			break;
		}
		FairyRings.handleLogButtons(c, actionButtonId);
		// Inside ClickingButtons.processPacket
		if (JewelleryTeleports.handleButton(c, actionButtonId)) {
		    return;
		}
		// --- NIGHTMARE ZONE INDIVIDUAL BOSS TOGGLES ---
        // Range: 234147 (Boss 0 Background) through 234221 (Boss 24 Checkmark)
		// --- NIGHTMARE ZONE INDIVIDUAL BOSS TOGGLES ---
        if (actionButtonId >= 234147 && actionButtonId <= 234221) {
            
            // THE FIX: Ignore the Toggle button packets (234149, 234152, etc.)
            // We only process the Background buttons (234147, 234150) to prevent double-flipping!
            if ((actionButtonId - 234147) % 3 != 0) {
                return; 
            }
            
            // Now we know it's purely the background button ID!
            int interfaceId2 = actionButtonId - 174096; 
            int bossIndex = (interfaceId2 - 60051) / 3;
            server.model.minigames.NMZBosses boss = server.model.minigames.NMZBosses.values()[bossIndex];
            
            // 3. Check if the boss is locked
            if (!boss.isUnlocked(c)) {
                c.sendMessage("You must complete the quest '" + boss.getUnlockRequirement() + "' to fight this boss.");
                return;
            }
            
            // 4. Flip the memory
            c.blockedNMZBosses[bossIndex] = !c.blockedNMZBosses[bossIndex];
            
            // Send visual update (Make sure the ? 0 : 1 matches whatever turns the Red X on in your client!)
            c.getPA().sendConfig(boss.getConfigId(), c.blockedNMZBosses[bossIndex] ? 0 : 1);
            return;
        }
		switch (actionButtonId) {
		// --- NIGHTMARE ZONE MAIN BUTTONS ---
        case 234113: // Accept Button (Interface 60017)
            NightmareZone.setupCustomisableRumble(c);
            break;
            
        case 234114: // Cancel Button (Interface 60018)
            c.getPA().removeAllWindows();
            break;
            
        case 234115: // Toggle All Button (Interface 60019)
            boolean blockAll = false; 
            
            for (int i = 0; i < c.blockedNMZBosses.length; i++) {
                NMZBosses boss = NMZBosses.values()[i];
                
                // Only toggle if they actually have the boss unlocked
                if (boss.isUnlocked(c)) {
                    c.blockedNMZBosses[i] = blockAll;
                    c.getPA().sendConfig(boss.getConfigId(), blockAll ? 1 : 0);
                }
            }
            break;
		case 193042: // Dial 1 Clockwise
		    c.fairyRingOption1 = (c.fairyRingOption1 + 3) & 3; // Cycle 0,1,2,3
		    c.getPA().sendConfig(756, c.fairyRingOption1);
		    System.out.println("Option 1 - "+c.fairyRingOption1);
		    break;

		case 193043: // Dial 1 Counter-Clockwise
		    c.fairyRingOption1 = (c.fairyRingOption1 + 1) & 3; // Cycle 3,2,1,0
		    c.getPA().sendConfig(756, c.fairyRingOption1);
		    System.out.println("Option 1 - "+c.fairyRingOption1);
		    break;

		case 193044: // Dial 2 Clockwise
		    c.fairyRingOption2 = (c.fairyRingOption2 + 3) & 3;
		    c.getPA().sendConfig(757, c.fairyRingOption2);
		    System.out.println("Option 2 - "+c.fairyRingOption2);
		    break;

		case 193045: // Dial 2 Counter-Clockwise
		    c.fairyRingOption2 = (c.fairyRingOption2 + 1) & 3;
		    c.getPA().sendConfig(757, c.fairyRingOption2);
		    System.out.println("Option 2 - "+c.fairyRingOption2);
		    break;

		case 193046: // Dial 3 Clockwise
		    c.fairyRingOption3 = (c.fairyRingOption3 + 3) & 3;
		    c.getPA().sendConfig(758, c.fairyRingOption3);
		    System.out.println("Option 3 - "+c.fairyRingOption3);
		    break;

		case 193047: // Dial 3 Counter-Clockwise
		    c.fairyRingOption3 = (c.fairyRingOption3 + 1) & 3;
		    c.getPA().sendConfig(758, c.fairyRingOption3);
		    System.out.println("Option 3 - "+c.fairyRingOption3);
		    break;
		case 193049:
			FairyRings.teleport(c);
			break;
		case 24127:
			c.getMusic().nextSong(c);
			break;
		case 24129:
			c.getMusic().prevSong(c);
			break;
		case 9178: // Option 1: Wedding Ceremony
		case 9179: // Option 2: Last Rites
		case 9180: // Option 3: Blessing
		case 9181: // Option 4: Preach
			if (c.preachingBook > 0) {
				GodBookManager.handlePreachOption(c, c.preachingBook, actionButtonId);
				c.preachingBook = -1; // clear after use
				return;
			}
			break;

		case 71071:// closebutton
			if (c.isChecking) {
				PriceChecker.clearConfig(c);
			}
			c.getPA().removeAllWindows();
			break;
		case 73177:// Price checker
			if (System.currentTimeMillis() - c.logoutDelay > 10000) {
				c.isBanking = false;
				c.isChecking = true;
				PriceChecker.open(c);
			} else {
				c.sendMessage("I can't open this in combat..");
			}
			break;
		case 24130: // Pause
			c.MusicOn = false;
			c.RegionMusicOn = false;
			c.lastSong = -1;
			c.getPA().musicManager("STOP", -1);
			c.getMusic().refreshPlaylist(c, c.onPlaylist);
			break;

		case 24128: // Play Button
		    if (c.lastSong > 0) {
				c.getMusic().playSong(c, c.lastSong, true, false);
		    } else {
		        // Convert the Set to a temporary list to allow index-based random selection
		        // We use a local variable to avoid repeating the ternary check
		        Set<Integer> sourceSet = c.onPlaylist ? c.getMusic().favoriteSongIds : c.getMusic().unlockedSongIds;

		        if (!sourceSet.isEmpty()) {
		            List<Integer> sourceList = new java.util.ArrayList<>(sourceSet);
		            int songId = sourceList.get(Misc.random(sourceList.size() - 1));
		            
		            c.lastSong = songId;
					c.getMusic().playSong(c, songId, true, false);
		        } else {
		            c.sendMessage(c.onPlaylist ? "Your favorites list is empty." : "You haven't unlocked any songs.");
		        }
		    }
		    
		    // Refresh the UI to show the current song as selected/highlighted
			c.getMusic().refreshPlaylist(c, c.onPlaylist);
		    break;

		case 24125: // Shuffle

			c.shuffleMode = !c.shuffleMode;
			if(c.shuffleMode)
				c.getMusic().shufflePlaylist(c);
			else
				c.getMusic().refreshPlaylist(c, false);
			break;

		case 24126: // Toggle playlist view
		    c.onPlaylist = !c.onPlaylist;
		    
		    // 1. Determine which source we are looking at
		    // Using a List here for index-based access, but the Set/HashSet for lookups elsewhere
		    List<Integer> currentList = new java.util.ArrayList<>(c.onPlaylist ? c.getMusic().favoriteSongIds : c.getMusic().unlockedSongIds);

		    // 2. Playback Logic
		    if (!currentList.isEmpty()) {
		        c.MusicOn = true;
		        c.RegionMusicOn = false;
		        c.lastSong = currentList.get(0);
		        // Automatically play the first song in the new view
		        c.getMusic().playSong(c, c.lastSong, true, false);
		    } else {
		        c.MusicOn = false;
		        c.RegionMusicOn = false;
		        c.lastSong = -1;
		        c.sendMessage(c.onPlaylist ? "Your favorites list is empty." : "You haven't unlocked any songs.");
		    }

		    // 3. Reset Shuffle
		    c.shuffleMode = false;

		    // 4. Force UI Refresh
		    // We pass currentList.size() so setText knows how many rows to fill
			c.getMusic().setText(c, 373); // Always check the full scrollable range
			c.getMusic().refreshPlaylist(c, c.onPlaylist);
		    
		    c.sendMessage("View switched to: " + (c.onPlaylist ? "@gre@Favorites" : "@gre@All Unlocked"));
		    break;


		case 95113://grouping teleport button
			int telex = Config.LUMBY_X, teley = Config.LUMBY_Y;
			if(c.groupingOption > 0) {
				switch(c.groupingOption) {
					case 1:
						telex = Config.BARBOUTPOST_X;
						teley = Config.BARBOUTPOST_Y;
					break;
					case 2:
						telex = Config.BLAST_FURNACE_X;
						teley = Config.BLAST_FURNACE_Y;
					break;
					case 3:
						telex = Config.BURTHORPE_GAMES_ROOM_X;
						teley = Config.BURTHORPE_GAMES_ROOM_Y;
					break;
					case 4:
						telex = Config.CASTLEWARS_X+Misc.random(3);
						teley = Config.CASTLEWARS_Y+Misc.random(3);
					break;
				}
				c.getPA().startTeleport(telex, teley, 0, "home");
			}
			break;
		case 95116://barb assult
			c.getPA().sendFrame126("Barbarian Assault", 24435);
			for(int i = 25433; i <= 25532; i++)
				c.getPA().sendFrame126("", i);//35702
			c.groupingOption = 1;
			break;
		case 95117://blast furnace
			c.getPA().sendFrame126("Blast furnace", 24435);
			c.groupingOption = 2;
			break;
		case 95118://games room
			c.getPA().sendFrame126("Burthorpe Games Room", 24435);
			c.groupingOption = 3;
			break;
		case 95119://castle wars
			c.getPA().sendFrame126("Castle wars", 24435);
			c.groupingOption = 4;
			break;
		case 95121:
			c.getPA().sendFrame126("Fishing Trawler", 24435);
			break;
		case 95133:
			c.getPA().sendFrame126("Select an activity...", 24435);
			break;
			//Slayer rewards buttons
			//Extend buttons
		case 185247://need more darkness
			if(c.slayerPoints >= 100 && !c.extend[0]){
				c.getPA().sendFrame126("Need more Darkness" , 47907);
				c.getPA().sendFrame126("Whenever you get a Dark Beast" , 47908);
				c.getPA().sendFrame126("task, it will be a bigger task." , 47909);
				c.getPA().sendFrame126("" , 47910);
				c.getPA().sendFrame126("@red@Cost: 100 points" , 47917);
				c.getPA().sendFrame126("If you turn this off in the future, you" , 47911);
				c.getPA().sendFrame126("will not get your points back." , 47912);
				c.getPA().showInterface(47900);
				c.dialogueId = 47600;
				c.dialogueAction = 185247;
			} else if(c.extend[0]){
				c.extend[0] = false;
			}
			break;
		case 190170:
			if(c.slayerPoints >= 30 && c.slayerTask > 0){
				c.slayerPoints -= 30;
				c.slayerTask = 0;
				c.taskAmount = 0;
			}
			break;

		case 218009:
		    c.CollectionLog = 1;

		    CollectionLogData logData12 = new SireLog();
		    String title12 = "Abyssal Sire";
		    String identifier12 = "Sire";
		    String line112 = "Abyssal Sire kills: ";
		    String line212 = "";

		    c.getCollectionLog().showLog(c, logData12.getEntries(), title12, identifier12, line112, line212);
		    c.getCollectionLog().openLog(0); // Presumably selects the "Bosses" tab
		    c.getPA().showInterface(35700);  // Opens the interface
		    break;

			case 139130:
			    c.CollectionLog = 2;

			    CollectionLogData logData = null;
			    String title = "";
			    String identifier = "";
			    String line1 = "";
			    String line2 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData = new SireLog();
			            title = "Abyssal Sire";
			            identifier = "Sire";
			            line1 = "Abyssal Sire kills: ";
			            break;

			        case 1: // raids
			            logData = new XericLog();
			            title = "Chambers Of Xeric";
			            identifier = "ChambersofXeric";
			            line1 = "Chambers Of Xeric completed: ";
			            break;
			        case 2: // Clues
			            logData = new BeginnerClueLog();
			            title = "Beginner Treasure Trails";
			            identifier = "BeginnerClue";
			            line1 = "Beginner clues completed: ";
			            break;

			        case 3: // minigames
			            logData = new AerialFishingLog();
			            title = "Barbarian Assault";
			            identifier = "BarbAss";
			            break;
			        case 4: // Other
			            logData = new AerialFishingLog();
			            title = "Aerial Fishing";
			            identifier = "AerialFish";
			            break;
			    }

			    if (logData != null) {
			        c.getCollectionLog().showLog(c, logData.getEntries(), title, identifier, line1, line2);
			    }
			    break;

			case 139131:
			    c.CollectionLog = 2;

			    CollectionLogData logData131 = null;
			    String title131 = "";
			    String identifier131 = "";
			    String line1_131 = "";
			    String line2_131 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData131 = new BarrowsLog();
			            title131 = "Barrows Chests";
			            identifier131 = "Barrows";
			            line1_131 = "Barrows Chests opened: ";
			            line2_131 = String.valueOf(c.BarrowsKC);
			            break;

			        case 2: // Clues
			            logData131 = new EasyClueLog();
			            title131 = "Easy Treasure Trails";
			            identifier131 = "EasyClue";
			            line1_131 = "Easy clues completed: ";
			            break;

			        case 4: // Other
			            logData131 = new PetLog();
			            title131 = "All pets";
			            identifier131 = "pets";
			            break;
			    }

			    if (logData131 != null) {
			        c.getCollectionLog().showLog(c, logData131.getEntries(), title131, identifier131, line1_131, line2_131);
			    }
			    break;
			case 202011:
				break;
			case 202013:
				DeathRetrieval.takeAll(c);
				break;
			/*
			 * showLog(Player c, int[][] log, String title, String logKey, String killLabel, String killCount)
			 */
			case 139132:
			    c.CollectionLog = 3;

			    CollectionLogData logData132 = null;
			    String title132 = "";
			    String identifier132 = "";
			    String line1_132 = "";
			    String line2_132 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData132 = new BryoLog();
			            title132 = "Bryophyta";
			            identifier132 = "Bryophyta";
			            line1_132 = "Bryophyta kills: ";
			            line2_132 = String.valueOf(c.bryophytaKills);
			            break;

			        case 2: // Clues
			            logData132 = new MediumClueLog();
			            title132 = "Medium Treasure Trails";
			            identifier132 = "MediumClue";
			            line1_132 = "Medium clues completed: ";
			            break;

			        case 4: // Other
			            logData132 = new ChampsLog();
			            title132 = "Champion's Challenge";
			            identifier132 = "ChampsChallenge";
			            line1_132 = "Champion's beaten: ";
			            break;
			    }

			    if (logData132 != null) {
			        c.getCollectionLog().showLog(c, logData132.getEntries(), title132, identifier132, line1_132, line2_132);
			    }
			    break;

			case 139133:
			    c.CollectionLog = 4;

			    CollectionLogData logData133 = null;
			    String title133 = "";
			    String identifier133 = "";
			    String line1_133 = "";
			    String line2_133 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData133 = new CallistoLog();
			            title133 = "Callisto";
			            identifier133 = "Callisto";
			            line1_133 = "Callisto kills: ";
			            line2_133 = String.valueOf(c.callistoKills); // You had `bryophytaKills` before, corrected here.
			            break;

			        case 2: // Clues
			            logData133 = new HardClueLog();
			            title133 = "Hard Treasure Trails";
			            identifier133 = "HardClue";
			            line1_133 = "Hard clues completed: ";
			            break;

			        case 4: // Other
			            logData133 = new ChaosDruidLog();
			            title133 = "Chaos Druids";
			            identifier133 = "ChaosDruid";
			            line1_133 = "Druid's slain: ";
			            break;
			    }

			    if (logData133 != null) {
			        c.getCollectionLog().showLog(c, logData133.getEntries(), title133, identifier133, line1_133, line2_133);
			    }
			    break;

			case 139134:
			    c.CollectionLog = 5;

			    CollectionLogData logData134 = null;
			    String title134 = "";
			    String identifier134 = "";
			    String line1_134 = "";
			    String line2_134 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData134 = new CerberusLog();
			            title134 = "Cerberus";
			            identifier134 = "Cerberus";
			            line1_134 = "Cerberus kills: ";
			            break;

			        case 2: // Clues
			            logData134 = new EliteClueLog();
			            title134 = "Elite Treasure Trails";
			            identifier134 = "EliteClue";
			            line1_134 = "Elite clues completed: ";
			            break;

			        case 4: // Other
			            logData134 = new ChompyLog();
			            title134 = "Chompy Bird Hunting";
			            identifier134 = "chompy";
			            line1_134 = "Chompy's killed: ";
			            break;
			    }

			    if (logData134 != null) {
			        c.getCollectionLog().showLog(c, logData134.getEntries(), title134, identifier134, line1_134, line2_134);
			    }
			    break;

			case 139135:
			    c.CollectionLog = 6;

			    CollectionLogData logData135 = null;
			    String title135 = "";
			    String identifier135 = "";
			    String line1_135 = "";
			    String line2_135 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData135 = new ChaosEleLog();
			            title135 = "Chaos Elemental";
			            identifier135 = "Chaos Elemental";
			            line1_135 = "Chaos Elemental kills: ";
			            break;

			        case 2: // Clues
			            logData135 = new MasterClueLog();
			            title135 = "Master Treasure Trails";
			            identifier135 = "MasterClue";
			            line1_135 = "Master clues completed: ";
			            break;

			        case 4: // Other
			            logData135 = new CreatureCreationLog();
			            title135 = "Creature Creation";
			            identifier135 = "creation";
			            break;
			    }

			    if (logData135 != null) {
			        c.getCollectionLog().showLog(c, logData135.getEntries(), title135, identifier135, line1_135, line2_135);
			    }
			    break;

			case 139136:
			    c.CollectionLog = 7;

			    CollectionLogData logData136 = null;
			    String title136 = "";
			    String identifier136 = "";
			    String line1_136 = "";
			    String line2_136 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Bosses
			            logData136 = new FanaticLog();
			            title136 = "Chaos Fanatic";
			            identifier136 = "Fanatic";
			            line1_136 = "Chaos Fanatic kills: ";
			            break;

			        case 2: // Clues
			            logData136 = new HardClueLogRare();
			            title136 = "Hard Treasure Trails(Rare)";
			            identifier136 = "EliteRareClue";
			            line1_136 = "Hard clues completed: ";
			            break;

			        case 4: // Other
			            logData136 = new CyclopesLog();
			            title136 = "Cyclopes";
			            identifier136 = "Cyclopes";
			            break;
			    }

			    if (logData136 != null) {
			        c.getCollectionLog().showLog(c, logData136.getEntries(), title136, identifier136, line1_136, line2_136);
			    }
			    break;

			case 139137:
			    c.CollectionLog = 8;

			    CollectionLogData logData137 = null;
			    String title137 = "";
			    String identifier137 = "";
			    String line1_137 = "";
			    String line2_137 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Boss
			            logData137 = new ZilyanaLog();
			            title137 = "Commander Zilyana";
			            identifier137 = "CommanderZilyana";
			            line1_137 = "Commander Zilyana kills: "+c.ZilyanaKills;
			            break;

			        case 2: // Clue
			            logData137 = new EliteClueLogRare();
			            title137 = "Elite Treasure Trails(Rare)";
			            identifier137 = "EliteRareClue";
			            line1_137 = "Elite clues completed: ";
			            break;

			        case 4: // Other
			            logData137 = new MiscLog();
			            title137 = "Miscellaneous";
			            identifier137 = "Misc";
			            break;
			    }

			    if (logData137 != null) {
			        c.getCollectionLog().showLog(c, logData137.getEntries(), title137, identifier137, line1_137, line2_137);
			    }
			    break;

			case 139138:
			    c.CollectionLog = 9;

			    CollectionLogData logData138 = null;
			    String title138 = "";
			    String identifier138 = "";
			    String line1_138 = "";
			    String line2_138 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Boss
			            logData138 = new CorpLog();
			            title138 = "Corporeal Beast";
			            identifier138 = "Corp";
			            line1_138 = "Corporeal Beast kills: ";
			            break;

			        case 2: // Clue
			            logData138 = new MasterClueLogRare();
			            title138 = "Master Treasure Trails(Rare)";
			            identifier138 = "MasterRareClue";
			            line1_138 = "Master clues completed: ";
			            break;

			        case 4: // Other
			            logData138 = new RandomEventLog();
			            title138 = "Random Events";
			            identifier138 = "RandomEvent";
			            break;
			    }

			    if (logData138 != null) {
			        c.getCollectionLog().showLog(c, logData138.getEntries(), title138, identifier138, line1_138, line2_138);
			    }
			    break;

			case 139139:
			    c.CollectionLog = 10;

			    CollectionLogData logData139 = null;
			    String title139 = "";
			    String identifier139 = "";
			    String line1_139 = "";
			    String line2_139 = "";

			    switch (c.CollLogOpen) {
			        case 0: // Boss
			            logData139 = new DGKLog();
			            title139 = "Dagannoth Kings";
			            identifier139 = "DGK";
			            line1_139 = "Dagannoth Prime kills: ";
			            line2_139 = "Dagannoth Rex kills:\nDagannoth Supreme kills:"; // Optional multiline label
			            break;

			        case 2: // Clue
			            logData139 = new SharedClueLog();
			            title139 = "Shared Treasure Trails";
			            identifier139 = "SharedClue";
			            line1_139 = "Total clues completed: ";
			            break;
			    }

			    if (logData139 != null) {
			        c.getCollectionLog().showLog(c, logData139.getEntries(), title139, identifier139, line1_139, line2_139);
			    }
			    break;

			case 139140:
			    c.CollectionLog = 11;

			    CollectionLogData logData140 = null;
			    String title140 = "";
			    String identifier140 = "";
			    String line1_140 = "";
			    String line2_140 = "";

			    switch (c.CollLogOpen) {
			        case 0:
			            logData140 = new FcLog();
			            title140 = "The Fight Caves";
			            identifier140 = "fightCaves";
			            line1_140 = "Fight Caves Completions: ";
			            break;
			        case 2:
			            logData140 = new SharedClueLog();
			            title140 = "Shared Treasure Trails";
			            identifier140 = "SharedClue";
			            line1_140 = "Total clues completed: ";
			            break;
			    }

			    if (logData140 != null) {
			        c.getCollectionLog().showLog(c, logData140.getEntries(), title140, identifier140, line1_140, line2_140);
			    }
			    break;

			case 139141:
			    c.CollectionLog = 12;

			    CollectionLogData logData141 = new GraardorLog();
			    c.getCollectionLog().showLog(
			        c,
			        logData141.getEntries(),
			        "General Graardor",
			        "GeneralGraardor",
			        "General Graardor kills: " + c.GraadorKills,
			        ""
			    );
			    break;

			case 139144:
			    c.CollectionLog = 15;

			    CollectionLogData logData144 = new KBDLog();
			    c.getCollectionLog().showLog(
			        c,
			        logData144.getEntries(),
			        "King Black Dragon",
			        "KingBlackDragon",
			        "King Black Dragon kills: "+c.KBDKills,
			        ""
			    );
			    break;

			case 139145:
			    c.CollectionLog = 16;

			    CollectionLogData logData145 = new KrakenLog();
			    c.getCollectionLog().showLog(
			        c,
			        logData145.getEntries(),
			        "Kraken",
			        "Kraken",
			        "Kraken kills: " + c.KrakenKills,
			        ""
			    );
			    break;

			case 139146:
			    c.CollectionLog = 17;

			    CollectionLogData logData146 = new KreeLog();
			    c.getCollectionLog().showLog(
			        c,
			        logData146.getEntries(),
			        "Kree'arra",
			        "Kree'arra",
			        "Kree'arra kills: " + c.KreeKills,
			        ""
			    );
			    break;

			case 139147:
			    c.CollectionLog = 18;

			    CollectionLogData logData147 = new KrilLog();
			    c.getCollectionLog().showLog(
			        c,
			        logData147.getEntries(),
			        "K'ril Tsutsaroth",
			        "K'rilTsutsaroth",
			        "K'ril Tsutsaroth kills: " + c.KrilKills,
			        ""
			    );
			    break;
			case 139148:
			    c.CollectionLog = 18;

			    CollectionLogData logData148 = new SkotizoLog();
			    c.getCollectionLog().showLog(
			    	    c,
			    	    logData148.getEntries(),
			    	    "Skotizo",
			    	    "Skotizo",
			    	    "Skotizo kills: ",
			    	    String.valueOf(((SkotizoLog) logData148).getKillCount())
			    	);

			    break;

		case 139124: 
			c.getCollectionLog().openLog(0);
			break;
		case 139125: 
			c.getCollectionLog().openLog(1);
			break;
		case 139126: 
			c.getCollectionLog().openLog(2);
			break;
		case 139127: 
			c.getCollectionLog().openLog(3);
			break;
		case 139128: 
			c.getCollectionLog().openLog(4);
			break;
		case 105224:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(16, 0, true);
			break;
		case 105226:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(9, 0, true);
			break;
		case 105222:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(0, 0, true);
			break;
		case 105223:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(8, 0, true);
			break;
		case 105225:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(1, 0, true);
			break;
		case 105228:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(2, 0, true);
			break;
		case 105233:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(19, 0, true);
			break;
		case 105234:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(4, 0, true);
			break;
		case 105237:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(5, 0, true);
			break;
		case 105240:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(6, 0, true);
			break;
		case 105243:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(7, 0, true);
			break;
		case 105231:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(3, 0, true);
			break;
		case 105229:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(10, 0, true);
			break;
		case 105238:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(13, 0, true);
			break;
		case 105242:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(22, 0, true);
			break;
		case 34014:
			c.getSkillGuide().clearInterface();
			c.getPA().closeAllWindows();
			break;
		case 191027:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 0, true);
			break;
		case 191028:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 1, true);
			break;
		case 191029:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 2, true);
			break;
		case 191030:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 3, true);
			break;
		case 191031:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 4, true);
			break;
		case 191032:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 5, true);
			break;
		case 191033:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 6, true);
			break;
		case 191034:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 7, true);
			break;
		case 191035:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 8, true);
			break;
		case 191036:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 9, true);
			break;
		case 191037:
			c.getSkillGuide().clearInterface();
			c.getSkillGuide().LoadMainInterface(c.getSkillGuide().skillid, 9, true);
			break;

		case 3071:
			EleWorkShop.readBatteredBook(c, 2);
			break;
		case 39178:
			c.getPA().removeAllWindows();
			c.bookPage = 0;
			for(int i = 843; i < 841; i++)
				c.getPA().sendFrame126("", i);
			break;	
		case 3073:
			EleWorkShop.readBatteredBook(c, 1);
			break;
			/** Hairdresser buttons */
		case 8100:
			c.playerAppearance[7] = 11; // beard 11: long
			c.getPA().requestUpdates();
			break;

		case 8101:
			c.playerAppearance[7] = 10; // beard 10: goatee
			c.getPA().requestUpdates();
			break;

		case 8102:
			c.playerAppearance[7] = 13; // beard 13: mustache
			c.getPA().requestUpdates();
			break;

		case 8103:
			c.playerAppearance[7] = 15; // beard 15: Chin strap
			c.getPA().requestUpdates();
			break;

		case 8104:
			c.playerAppearance[7] = 17; // beard 17: Barbarian beard?
			c.getPA().requestUpdates();
			break;

		case 8105:
			c.playerAppearance[7] = 12; // beard 12: Egyptian beard?
			c.getPA().requestUpdates();
			break;

		case 8106:
			c.playerAppearance[7] = 14; // beard 14: Clean shaven 
			c.getPA().requestUpdates();
			break;

		case 8107:
			c.playerAppearance[7] = 16; // beard 16: Goatee + Chin strap
			c.getPA().requestUpdates();
			break;

		case 8088:
			c.playerAppearance[8] = 0; // hair/beard color: Dark-brown
			c.getPA().requestUpdates();
			break;

		case 8089:
			c.playerAppearance[8] = 1; // hair/beard color: White
			c.getPA().requestUpdates();
			break;

		case 8090:
			c.playerAppearance[8] = 2; // hair/beard color: Gray
			c.getPA().requestUpdates();
			break;

		case 8091:
			c.playerAppearance[8] = 3; // hair/beard color: Black
			c.getPA().requestUpdates();
			break;

		case 8092:
			c.playerAppearance[8] = 4; // hair/beard color: Orange
			c.getPA().requestUpdates();
			break;

		case 8093:
			c.playerAppearance[8] = 5; // hair/beard color: Blonde
			c.getPA().requestUpdates();
			break;

		case 8094:
			c.playerAppearance[8] = 6; // hair/beard color: Light-brown
			c.getPA().requestUpdates();
			break;

		case 8095:
			c.playerAppearance[8] = 7; // hair/beard color: Brown
			c.getPA().requestUpdates();
			break;

		case 8096:
			c.playerAppearance[8] = 8; // hair/beard color: Cyan
			c.getPA().requestUpdates();
			break;

		case 8097:
			c.playerAppearance[8] = 9; // hair/beard color: Green
			c.getPA().requestUpdates();
			break;

		case 8098:
			c.playerAppearance[8] = 10; // hair/beard color: Red
			c.getPA().requestUpdates();
			break;

		case 8099:
			c.playerAppearance[8] = 11; // hair/beard color: Pink
			c.getPA().requestUpdates();
			break;
			
		case 10229:
			c.playerAppearance[1] = 0; // 0: Bald
			c.getPA().requestUpdates();
			break;
			
		case 10230:
			c.playerAppearance[1] = 1; // 1: Dreadlocks
			c.getPA().requestUpdates();
			break;
			
		case 10231:
			c.playerAppearance[1] = 2; // 2: Long hair
			c.getPA().requestUpdates();
			break;
			
		case 10232:
			c.playerAppearance[1] = 3; // 3: Medium hair
			c.getPA().requestUpdates();
			break;
			
		case 10233:
			c.playerAppearance[1] = 4; // 4: Monk
			c.getPA().requestUpdates();
			break;
			
		case 10234:
			c.playerAppearance[1] = 5; // 5: Comb-over
			c.getPA().requestUpdates();
			break;
			
		case 10235:
			c.playerAppearance[1] = 6; // 6: Close-cropped
			c.getPA().requestUpdates();
			break;
			
		case 10236:
			c.playerAppearance[1] = 7; // Wild spikes
			c.getPA().requestUpdates();
			break;
			
		case 10237:
			c.playerAppearance[1] = 8; // Spikes
			c.getPA().requestUpdates();
			break;
			
		case 10217:
			c.playerAppearance[8] = 0; // hair/beard color: Dark-brown
			c.getPA().requestUpdates();
			break;

		case 10218:
			c.playerAppearance[8] = 1; // hair/beard color: White
			c.getPA().requestUpdates();
			break;

		case 10219:
			c.playerAppearance[8] = 2; // hair/beard color: Gray
			c.getPA().requestUpdates();
			break;

		case 10220:
			c.playerAppearance[8] = 3; // hair/beard color: Black
			c.getPA().requestUpdates();
			break;

		case 10221:
			c.playerAppearance[8] = 4; // hair/beard color: Orange
			c.getPA().requestUpdates();
			break;

		case 10222:
			c.playerAppearance[8] = 5; // hair/beard color: Blonde
			c.getPA().requestUpdates();
			break;

		case 10223:
			c.playerAppearance[8] = 6; // hair/beard color: Light-brown
			c.getPA().requestUpdates();
			break;

		case 10224:
			c.playerAppearance[8] = 7; // hair/beard color: Brown
			c.getPA().requestUpdates();
			break;

		case 10225:
			c.playerAppearance[8] = 8; // hair/beard color: Cyan
			c.getPA().requestUpdates();
			break;

		case 10226:
			c.playerAppearance[8] = 9; // hair/beard color: Green
			c.getPA().requestUpdates();
			break;

		case 10227:
			c.playerAppearance[8] = 10; // hair/beard color: Red
			c.getPA().requestUpdates();
			break;

		case 10228:
			c.playerAppearance[8] = 11; // hair/beard color: Pink
			c.getPA().requestUpdates();
			break;
			
		case 10193:
			c.getItems().deleteItem(995, 2000);
			c.getAD().completeAchievement("FaladorEasy", "Get a haircut from the Falador hairdresser.", 4);
			c.getPA().removeAllWindows();
			break;

		case 8065:
			c.getItems().deleteItem(995, 2000);
			c.getPA().removeAllWindows();
			break;
			/** End of Hairdresser buttons */
		case 213173:
			c.getAD().updateAchievementInterface("Ardougne");
			break;
		case 213183:
			c.getAD().updateAchievementInterface("Desert");
			break;
		case 213193:
			c.getAD().updateAchievementInterface("Falador");
			break;
		case 213203:
			c.getAD().updateAchievementInterface("Fremennik");
			break;
		case 213213:
			c.getAD().updateAchievementInterface("Kandarin");
			break;
		case 213223:
			c.getAD().updateAchievementInterface("Karamja");
			break;
		case 213233:
			c.getAD().updateAchievementInterface("Kourend");
			break;
		case 213243:
			c.getAD().updateAchievementInterface("Lumbridge&Draynor");
			break;
		case 213253:
			c.getAD().updateAchievementInterface("Morytania");
			break;
		case 214007:
			c.getAD().updateAchievementInterface("Varrock");
			break;
		case 214017:
			c.getAD().updateAchievementInterface("WesternProvinces");
			break;
		case 214027:
			c.getAD().updateAchievementInterface("Wilderness");
			break;
			/*case 218008:
						c.getPA().sendSong(169);
						break;*/
		case 190171:
			c.getPA().sendFrame126(World.getWorld().npcHandler.getNpcListName(c.slayerTask)+"" , 47907);
			c.getPA().sendFrame126("Your current task will be cancelled, and the" , 47908);
			c.getPA().sendFrame126("Slayer Masters will be blocked from" , 47909);
			c.getPA().sendFrame126("assigning this category to you again." , 47910);
			c.getPA().sendFrame126("@red@Cost: 100 points" , 47917);
			c.getPA().sendFrame126("If you unblock this creature in the future, you" , 47911);
			c.getPA().sendFrame126("will not get your points back." , 47912);
			c.getPA().showInterface(47900);
			c.dialogueId = 47800;
			c.dialogueAction = 190171;
			break;
		case 190172:
			if(c.BlockID[0] > 0) {
				c.alreadyBlocked[0] = false;
				c.BlockID[0] = -1;
			}
			break;
		case 190173:
			if(c.BlockID[1] > 0) {
				c.alreadyBlocked[1] = false;
				c.BlockID[1] = -1;
			}
			break;
		case 190174:
			if(c.BlockID[2] > 0) {
				c.alreadyBlocked[2] = false;
				c.BlockID[2] = -1;
			}
			break;
		case 190175:
			if(c.BlockID[3] > 0) {
				c.alreadyBlocked[3] = false;
				c.BlockID[3] = -1;
			}
			break;
		case 190176:
			if(c.BlockID[4] > 0) {
				c.alreadyBlocked[4] = false;
				c.BlockID[4] = -1;
			}
			break;
		case 190177:
			if(c.BlockID[5] > 0) {
				c.alreadyBlocked[5] = false;
				c.BlockID[5] = -1;
			}
			break;
		case 187033:
			if(c.dialogueId == 47600)
				c.getPA().showInterface(47600);
			if(c.dialogueId == 47700)
				c.getPA().showInterface(47700);
			if(c.dialogueId == 47800)
				c.getPA().showInterface(47800);
			break;
		case 187034://slayer rewards confirm
			if(c.slayerPoints >= 100 && !c.extend[0] && c.dialogueAction == 185247){
				c.extend[0] = true;
				c.getPA().sendFrame36(899, 1);
				c.slayerPoints -= 100;
			}	
			if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && c.slayerTask != 0){
				for(int i = 0; i < 6; i++) {
					if(c.BlockID[i] != c.slayerTask && !c.alreadyBlocked[i]){
						c.alreadyBlocked[i] = true;
						c.BlockID[i] = c.slayerTask;
						c.getPA().sendFrame126(World.getWorld().npcHandler.getNpcListName(c.BlockID[i]), 48818+i);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}
				}
			}

			if(c.dialogueId == 47800)
				c.getPA().showInterface(47800);/* else 	
					if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && c.alreadyBlocked[1] && c.slayerTask != 0){
						c.alreadyBlocked[1] = true;
						c.BlockID[1] = c.slayerTask;
						c.getPA().sendFrame126(Server.npcHandler.getNpcListName(c.BlockID[1]), 48819);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}	 else 	
					if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && !c.alreadyBlocked[2] && c.slayerTask != 0){
						c.alreadyBlocked[2] = true;
						c.BlockID[2] = c.slayerTask;
						c.getPA().sendFrame126(Server.npcHandler.getNpcListName(c.BlockID[2]), 48820);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}	 else 	
					if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && !c.alreadyBlocked[3] && c.slayerTask != 0){
						c.alreadyBlocked[3] = true;
						c.BlockID[3] = c.slayerTask;
						c.getPA().sendFrame126(Server.npcHandler.getNpcListName(c.BlockID[3]), 48821);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}	 else 	
					if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && !c.alreadyBlocked[4] && c.slayerTask != 0){
						c.alreadyBlocked[4] = true;
						c.BlockID[4] = c.slayerTask;
						c.getPA().sendFrame126(Server.npcHandler.getNpcListName(c.BlockID[4]), 48822);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}	 else 	
					if(c.slayerPoints >= 100 && c.dialogueAction == 190171 && !c.alreadyBlocked[4] && c.slayerTask != 0){
						c.alreadyBlocked[4] = true;
						c.BlockID[4] = c.slayerTask;
						c.getPA().sendFrame126(Server.npcHandler.getNpcListName(c.BlockID[4]), 48823);
						c.slayerPoints -= 100;
						c.slayerTask = 0;
						c.taskAmount = 0;
					}	*/
			if(c.dialogueId == 47600)
				c.getPA().showInterface(47600);
			break;
		case 185249://Ankou very much
			if(c.slayerPoints >= 100){
				c.extend[1] = true;
				c.getPA().sendFrame36(900, 1);
				c.slayerPoints -= 100;
			}	
			break;
		case 185251://suq-a-nother one
			if(c.slayerPoints >= 100){
				c.extend[3] = true;
				c.getPA().sendFrame36(901, 1);
				c.slayerPoints -= 100;
			}	
			break;
		case 185253://fire and darkness
			if(c.slayerPoints >= 50 && !c.extend[4]){
				c.extend[4] = true;
				c.getPA().sendFrame36(902, 1);
				c.slayerPoints -= 50;
			}	
			break;
		case 185255://pedal to the metals
			if(c.slayerPoints >= 100){
				c.extend[5] = true;
				c.getPA().sendFrame36(903, 1);
				c.slayerPoints -= 100;
			}	
			break;
		case 185257://need more darkness
			if(c.slayerPoints >= 120){
				c.extend[6] = true;
				c.getPA().sendFrame36(904, 1);
				c.slayerPoints -= 120;
			}	
			break;

		case 166206:
			c.setSidebarInterface(3, 3213);
			break;
		case 2048:
			c.getPA().closeAllWindows();
			break;
		case 108201:

			c.getPA().showInterface(100);
			break;
		case 107013:
			c.getPA().showInterface(47500);
			break;
		case 107014:
			c.getPA().showInterface(47600);
			break;
		case 107015:
			c.getPA().sendFrame34a(47706, 554, 0, 100);
			c.getPA().sendFrame34a(47706, 554, 1, 100);
			c.getPA().sendFrame34a(47706, 554, 2, 100);
			c.getPA().sendFrame34a(47706, 554, 3, 100);
			c.getPA().sendFrame34a(47706, 554, 4, 100);
			c.getPA().showInterface(47700);
			break;
		case 107016:
			c.getPA().showInterface(47800);
			break;
		case 108184:

			c.getPA().showInterface(27841);
			break;
		case 80023:
			c.getPA().showInterface(100);
			break;
		case 118098:
			c.getPA().castVeng();
			break;
			// crafting + fletching interface:
		case 150:
			if (c.autoRet == 0)
				c.autoRet = 1;
			else
				c.autoRet = 0;
			break;
		case 73181: // The button to open "Items Kept on Death"
		    ItemsKeptOnDeath.open(c);
		    break;
		 // --- NMZ REWARDS TAB SWITCHING ---
        case 99193: // Button 25537 (Resources)
            NMZRewards.refreshResourcesTab(c);
            c.getPA().showInterface(25533);
            break;
            
        case 99196: // Button 25540 (Upgrades)
            c.getPA().showInterface(25600);
            NMZRewards.refreshUpgradesTab(c); // Fills the container!
            break;

        case 99199: // Button 25543 (Benefits)
            NMZRewards.refreshBenefitsTab(c);
            c.getPA().showInterface(25700);
            break;
		case 73179:
			c.getPA().showInterface(36106);
			c.getItems().writeBonus();
			break;
		case 1204:
			c.setSidebarInterface(11, 904);
			c.getPA().removeAllWindows();
			c.addingToRP = false;
			for (int k = 0; k < 28; k++)
				c.getPA().sendFrame34a(41711, -1, k, 1);
			break;

		case 59004:
			c.getPA().removeAllWindows();
			break;
		case 147221:
			c.getPA().requestUpdates();
			break;
		case 213144:
			c.setSidebarInterface(2, 55790);
			break;
			/*
			 *
			 * Options tab 
			 *
			 */
		case 3145://control settings
			c.setSidebarInterface(11, 20600);
			break;
		case 3147://chat options
			c.getFancyBox().dressBoxTier = 3;
			c.getArmourCase().open(c);
           /* c.getPA().sendFrame126("<col=656565>Camo Outfit", 19523);
	        c.getPA().sendFrame34a(19524, 6656, 0, 0);
            c.getPA().sendFrame34a(19524, 6654, 1, 1);
            c.getPA().sendFrame34a(19524, 6655, 2, 0);

            c.getPA().sendFrame126("<col=656565>Frog costume", 19526);
	        c.getPA().sendFrame34a(19527, 6188, 0, 0);
	        if(c.playerAppearance[0] == 0) {
	        	c.getPA().sendFrame34a(19527, 6184, 1, 1);
	        	c.getPA().sendFrame34a(19527, 6185, 2, 0);
	        } else {
	        	c.getPA().sendFrame34a(19527, 6186, 1, 1);
	        	c.getPA().sendFrame34a(19527, 6187, 2, 0);	        	
	        }

            c.getPA().sendFrame126("<col=656565>Lederhosen Outfit", 19529);
	        c.getPA().sendFrame34a(19530, 6182, 0, 0);
            c.getPA().sendFrame34a(19530, 6180, 1, 1);
            c.getPA().sendFrame34a(19530, 6181, 2, 0);

            c.getPA().sendFrame126("<col=656565>Mime costume", 19532);
	        c.getPA().sendFrame34a(19533, 3057, 0, 0);
            c.getPA().sendFrame34a(19533, 3058, 1, 1);
            c.getPA().sendFrame34a(19533, 3059, 2, 0);
            c.getPA().sendFrame34a(19533, 3060, 3, 0);
            c.getPA().sendFrame34a(19533, 3061, 4, 0);

            c.getPA().sendFrame126("<col=656565>Shade robes", 19535);
	        c.getPA().sendFrame34a(19536, 546, 0, 0);
            c.getPA().sendFrame34a(19536, 548, 1, 1);

            c.getPA().sendFrame126("<col=656565>Stale baguette", 19538);
	        c.getPA().sendFrame34a(19539, 20590, 0, 0);
	        
            c.getPA().sendFrame126("<col=656565>Zombie outfit", 19541);
	        c.getPA().sendFrame34a(19542, 7594, 0, 0);
            c.getPA().sendFrame34a(19542, 7592, 1, 1);
            c.getPA().sendFrame34a(19542, 7593, 2, 0);
            c.getPA().sendFrame34a(19542, 7595, 3, 0);
            c.getPA().sendFrame34a(19542, 7596, 4, 0);
            c.getPA().showInterface(19543);
            if (c.getOutStream() != null && c != null) {
    			c.isBanking = false;
    			c.isChecking = false;
    			c.isInInterface = 19543;
    			c.getItems().resetItems(5064);
    			c.getOutStream().createFrame(248);
    			c.getOutStream().writeWordA(19543);// ok perfect
    			c.getOutStream().writeWord(5063);
    			c.flushOutStream();
    		}*/
			break;

		/*case 3147://chat options
			for(int i = 0; i < 80; i++)
            c.getPA().sendFrame34a(51710, 6701, i, 1);
            c.getPA().showInterface(51700);
            c.getPA().sendFrame126("Stack count: @whi@4", 51720);
            c.getPA().sendFrame126("Guide value: @whi@37,331 (approximate)", 51722);
			return;*/
		case 16213://graphics
			c.setSidebarInterface(11, 904);
			break;
		case 16214://audio
			c.setSidebarInterface(11, 4301);
			break;
		case 48176:
			if (!c.acceptAid) {
				c.acceptAid = true;
				c.getPA().sendFrame36(503, 1);
				c.getPA().sendFrame36(427, 1);
			} else {
				c.acceptAid = false;
				c.getPA().sendFrame36(503, 0);
				c.getPA().sendFrame36(427, 0);
			}
			break;

		case 153:
			c.isRunning2 = !c.isRunning2;
			if (c.tutorialProgress == 11) {
				c.getDH().sendDialogues(3041, 0);
			}
			break;

		case 152://run
			if (c.tutorialProgress == 11) {
				c.getDH().sendDialogues(3041, 0);
			}
			c.isRunning2 = !c.isRunning2;
			
			break;
		case 100244:
			c.setSidebarInterface(11, 37430);
			//c.getPA().sendFrame36(208, 1);
			break;

		case 146058:
			c.setSidebarInterface(11, 904);
			//c.getPA().sendFrame36(208, 1);
			break;
			//End//
		case 217240:
			c.getPA().sendFrame36(208, 1);
			break;
		case 213145:
			c.setSidebarInterface(2, 638);
			break;
		case 213147:
			for(int i = 25433; i <= 25532; i++)
				c.getPA().sendFrame126("", i);//35702
			c.setSidebarInterface(2, 24430);
			break;
		case 218007:
			c.getPA().sendFrame36(208, 2);
		case 213146:
			c.setSidebarInterface(2, 54670);
			break;
		case 1093:
		case 1094:
		case 1097:
			if (c.autocastId > 0) {
				c.getPA().resetAutocast();
			} else {
				if (c.playerMagicBook == 1) {
					if (c.playerEquipment[c.playerWeapon] == 4675)
						c.setSidebarInterface(0, 1689);
					else
						c.sendMessage("You can't autocast ancients without an ancient staff.");
				} else if (c.playerMagicBook == 0) {
					if (c.playerEquipment[c.playerWeapon] == 4170) {
						c.setSidebarInterface(0, 12050);
					} else {
						c.setSidebarInterface(0, 1829);
					}
				}

			}
			break;
			/** Specials **/
		case 29188:
			c.specBarId = 7636; // the special attack text - sendframe126(S P E
			// C I A L A T T A C K, c.specBarId);
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29163:
			c.specBarId = 7611;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 33033:
			c.specBarId = 8505;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29038:
			c.specBarId = 7486;
			/*
			 * if (c.specAmount >= 5) { c.attackTimer = 0;
			 * c.getCombat().attackPlayer(c.playerIndex); c.usingSpecial = true;
			 * c.specAmount -= 5; }
			 */
			c.getCombat().handleGmaulPlayer();
			c.getItems().updateSpecialBar();
			break;

		case 29063:
			if (c.getCombat()
					.checkSpecAmount(c.playerEquipment[c.playerWeapon])) {
				c.gfx0(246);
				c.forcedChat("Raarrrrrgggggghhhhhhh!");
				c.startAnimation(1056);
				c.playerLevel[2] = c.getLevelForXP(c.playerXP[2])
						+ (c.getLevelForXP(c.playerXP[2]) * 15 / 100);
				c.getPA().refreshSkill(2);
				c.getItems().updateSpecialBar();
			} else {
				c.sendMessage("You don't have the required special energy to use this attack.");
			}
			break;
		case 221090:
			c.specBarId = 12335;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;
		case 48023:
			c.specBarId = 12335;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29138:
			c.specBarId = 7586;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29113:
			c.specBarId = 7561;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

		case 29238:
			c.specBarId = 7686;
			c.usingSpecial = !c.usingSpecial;
			c.getItems().updateSpecialBar();
			break;

			/** Dueling **/
		case 26065: // no forfeit
		case 26040:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(0);
			break;

		case 26066: // no movement
		case 26048:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(1);
			break;

		case 26069: // no range
		case 26042:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(2);
			break;

		case 26070: // no melee
		case 26043:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(3);
			break;

		case 26071: // no mage
		case 26041:
			c.duelSlot = -1;
			//	c.getTradeAndDuel().selectRule(4);
			break;

		case 26072: // no drinks
		case 26045:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(5);
			break;

		case 26073: // no food
		case 26046:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(6);
			break;

		case 26074: // no prayer
		case 26047:
			c.duelSlot = -1;
			//	c.getTradeAndDuel().selectRule(7);
			break;

		case 26076: // obsticals
		case 26075:
			c.duelSlot = -1;
			//c.getTradeAndDuel().selectRule(8);
			break;

		case 2158: // fun weapons
		case 2157:
			c.duelSlot = -1;
			//	c.getTradeAndDuel().selectRule(9);
			break;

		case 30136: // sp attack
		case 30137:
			c.duelSlot = -1;
			//	c.getTradeAndDuel().selectRule(10);
			break;

		case 53245: // no helm
			c.duelSlot = 0;
			//c.getTradeAndDuel().selectRule(11);
			break;

		case 53246: // no cape
			c.duelSlot = 1;
			//c.getTradeAndDuel().selectRule(12);
			break;

		case 53247: // no ammy
			c.duelSlot = 2;
			//	c.getTradeAndDuel().selectRule(13);
			break;

		case 53249: // no weapon.
			c.duelSlot = 3;
			//c.getTradeAndDuel().selectRule(14);
			break;

		case 53250: // no body
			c.duelSlot = 4;
			//c.getTradeAndDuel().selectRule(15);
			break;

		case 53251: // no shield
			c.duelSlot = 5;
			//c.getTradeAndDuel().selectRule(16);
			break;

		case 53252: // no legs
			c.duelSlot = 7;
			//c.getTradeAndDuel().selectRule(17);
			break;

		case 53255: // no gloves
			c.duelSlot = 9;
			//c.getTradeAndDuel().selectRule(18);
			break;

		case 53254: // no boots
			c.duelSlot = 10;
			//c.getTradeAndDuel().selectRule(19);
			break;

		case 53253: // no rings
			c.duelSlot = 12;
			//c.getTradeAndDuel().selectRule(20);
			break;

		case 53248: // no arrows
			c.duelSlot = 13;
			//c.getTradeAndDuel().selectRule(21);
			break;

		case 26018:
			Player o = (Player) PlayerHandler.players[c.duelingWith];
			if (o == null) {
				//c.getTradeAndDuel().declineDuel();
				return;
			}

			if (c.duelRule[2] && c.duelRule[3] && c.duelRule[4]) {
				c.sendMessage("You won't be able to attack the player with the rules you have set.");
				break;
			}
			c.duelStatus = 2;
			if (c.duelStatus == 2) {
				c.getPA().sendFrame126("Waiting for other player...", 6684);
				o.getPA().sendFrame126("Other player has accepted.", 6684);
			}
			if (o.duelStatus == 2) {
				o.getPA().sendFrame126("Waiting for other player...", 6684);
				c.getPA().sendFrame126("Other player has accepted.", 6684);
			}

			if (c.duelStatus == 2 && o.duelStatus == 2) {
				c.canOffer = false;
				o.canOffer = false;
				c.duelStatus = 3;
				o.duelStatus = 3;
				//	c.getTradeAndDuel().confirmDuel();
				//o.getTradeAndDuel().confirmDuel();
			}
			break;

		case 25120:
			if (c.duelStatus == 5) {
				break;
			}
			Player o1 = (Player) PlayerHandler.players[c.duelingWith];
			if (o1 == null) {
				//	c.getTradeAndDuel().declineDuel();
				return;
			}

			c.duelStatus = 4;
			if (o1.duelStatus == 4 && c.duelStatus == 4) {
				//c.getTradeAndDuel().startDuel();
				//o1.getTradeAndDuel().startDuel();
				o1.duelCount = 4;
				c.duelCount = 4;
				c.duelDelay = System.currentTimeMillis();
				o1.duelDelay = System.currentTimeMillis();
			} else {
				c.getPA().sendFrame126("Waiting for other player...", 6571);
				o1.getPA().sendFrame126("Other player has accepted", 6571);
			}
			break;

		case 4169: // god spell charge
			c.usingMagic = true;
			if (!c.getCombat().checkMagicReqs(48)) {
				break;
			}

			if (System.currentTimeMillis() - c.godSpellDelay < Config.GOD_SPELL_CHARGE) {
				c.sendMessage("You still feel the charge in your body!");
				break;
			}
			c.godSpellDelay = System.currentTimeMillis();
			c.sendMessage("You feel charged with a magical power!");
			c.gfx100(MagicData.MAGIC_SPELLS[48][3]);
			c.startAnimation(MagicData.MAGIC_SPELLS[48][2]);
			c.usingMagic = false;
			break;




		case 9154:
			c.logout();
			break;

		case 58025:
		case 58026:
		case 58027:
		case 58028:
		case 58029:
		case 58030:
		case 58031:
		case 58032:
		case 58033:
		case 58034:
			c.getBankPin().bankPinEnter(actionButtonId);
			break;
		case 58230:
			if (!c.hasBankpin) {
				c.getBankPin().openPin();
			} else if (c.hasBankpin && c.enterdBankpin) {
				c.getBankPin().resetBankPin();
				c.sendMessage(
						"Your PIN has been deleted as requested.");
			} else {
				c.sendMessage("Please enter your Bank Pin before requesting a delete.");
				c.sendMessage("You can do this by simply opening your bank. This is to verify it's really you.");
				c.getPA().closeAllWindows();
			}
			break;
		case 20174:
			c.getBankPin().bankPinSettings();
			break;

		case 58074:
			c.getBankPin().closeBankPin();
			break;

		case 58073:
			if (c.hasBankpin && !c.requestPinDelete) {
				c.requestPinDelete = true;
				c.getBankPin().dateRequested();
				c.getBankPin().dateExpired();
				c.getDH().sendDialogues(1017, 1);
				c.sendMessage(
						"[Notice] A PIN delete has been requested. Your PIN will be deleted in "
								+ c.getBankPin().recovery_Delay
								+ " days.");
				c.sendMessage(
						"To cancel this change just type in the correct PIN.");
			} else {
				c.sendMessage(
						"[Notice] Your PIN is already pending deletion. Please wait the entire 2 days.");
				c.getPA().closeAllWindows();
			}
			break;

		case 40095:
		case 40096:
		case 40097:
		case 40098:
		case 40099:
		case 40100:
		case 40101:
		case 40102:
		case 40103: {

			if (!c.isBanking) {
				c.getPA().removeAllWindows();
				return;
			}
			if (c.getBankPin().requiresUnlock()) {
				c.isBanking = false;
				c.getBankPin().open(2);
				return;
			}
			int tabId = actionButtonId == 40095 ? 0
					: actionButtonId == 40096 ? 1
							: actionButtonId == 40097 ? 2
									: actionButtonId == 40098 ? 3
											: actionButtonId == 40099 ? 4
													: actionButtonId == 40100 ? 5
															: actionButtonId == 40101 ? 6
																	: actionButtonId == 40102 ? 7
																			: actionButtonId == 40103 ? 8 : -1;
			if (tabId <= -1 || tabId > 8)
				return;
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset(tabId);
				return;
			}
			if (c.getBank().getBankSearch().isSearching()) {
				c.getBank().getBankSearch().reset();
				return;
			}
			BankTab tab = c.getBank().getBankTab(tabId);
			if (tab.getTabId() == c.getBank().getCurrentBankTab().getTabId())
				return;
			if (tab.size() <= 0 && tab.getTabId() != 0) {
				c.sendMessage("Drag an item into the new tab slot to create a tab.");
				return;
			}		
			c.getBank().setCurrentBankTab(tab);
			BankTab tab1 = c.getBank().getCurrentBankTab();
			int visibleItemCount = tab.size();

			// If viewing main tab, include preview items from other tabs (Tabs 1–8)
			if (tab1.getTabId() == 0) {
			    for (int i = 1; i <= 8; i++) {
			        if (i < c.getBank().getBankTab().length) {
			            BankTab previewTab = c.getBank().getBankTab()[i];
			            visibleItemCount += previewTab.size()*15;
			        }
			    }
			}
			if(tabId != 0)
				c.getPA().sendFrame126("Tab "+tabId, 19990);
			else
				c.getPA().sendFrame126("Main tab", 19990);
			int scrollMax = 200;
			if (visibleItemCount <= 15) {
			    scrollMax = 207; // static scroll height
			} else {
			    int rows = (int) Math.ceil(visibleItemCount / 10.0); // 10 items per row
			    scrollMax = rows * 50;
			}

			// Optional: Cap it so it never goes crazy
			//scrollMax = Math.min(scrollMax, Config.BANK_SIZE * 55);

			// Send scroll max height to the bank interface

			c.getPA().sendScrollMax(scrollMax, 5385); // 5385 = scrollable container frame ID
			c.getPA().openUpBank();
			break;
		}


		case 82016:
			if (!c.takeAsNote) {
				c.takeAsNote = true;
				c.sendMessage("You are now withdrawing items as notes.");
				c.getPA().sendFrame36(309, 1);
			} else if (c.takeAsNote){
				c.takeAsNote = false;
				c.sendMessage("You are now withdrawing items as normal.");
				c.getPA().sendFrame36(309, 0);
			}
			break;

		case 113084:
			if(c.isBanking) {
				for (int slot = 0; slot < c.playerItems.length; slot++) {
					if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
						c.getItems().addToBank(c.playerItems[slot] - 1, c.playerItemsN[slot], slot, false);
					}
				}
			}
			if(c.isChecking) {
				for (int slot = 0; slot < c.playerItems.length; slot++) {
					if (c.playerItems[slot] > 0 && c.playerItemsN[slot] > 0) {
						PriceChecker.depositItem(c, c.playerItems[slot] - 1, c.playerItemsN[slot]);
					}
				}
			}
			c.getItems().updateInventory();
			c.getItems().resetBank();
			c.getItems().resetTempItems();
			break;		
		case 82024://Deposit Worn items
			for (int i = 0; i < c.playerEquipment.length; i++) {
				int itemId = c.playerEquipment[i];
				int itemAmount = c.playerEquipmentN[i];
				if(itemId == -1)
					return;
				c.getItems().removeItem(itemId, i);
				c.getItems().addToBank(itemId, itemAmount, c.getItems().getItemSlot(itemId), true);
				//c.getItems().bankItem(itemId, c.getItems().getItemSlot(itemId), itemAmount, c.currentTab);
			}
			break;
			// home teleports
		case 4171:
			//String type = c.playerMagicBook == 0 ? "modern" : "ancient";
			//c.getPA().startTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0,
					//type);
			//c.startHomeTeleport(Config.EDGEVILLE_X, Config.EDGEVILLE_Y, 0);
            c.getPA().startTeleport(Config.LUMBY_X, Config.LUMBY_Y, 0, "home");
			break;
		case 4140://varrock
			c.handleTeleportRunes(556, 554, 563, 3, 1, 1, Config.VARROCK_X+Misc.random(4), Config.VARROCK_Y+Misc.random(2));
			break;

		case 213156://varrock
			c.handleTeleportRunes(556, 554, 563, 3, 1, 1, Config.GRAND_EXCHANGE_X+Misc.random(3), Config.GRAND_EXCHANGE_Y+Misc.random(3));
			break;
		case 4143://lumbridge
			c.handleTeleportRunes(556, 557, 563, 3, 1, 1, Config.LUMBY_X+Misc.random(5), Config.LUMBY_Y+Misc.random(5));
			break;
		case 54682:
            c.getPA().startTeleport(2725, 3485, 0, "modern");
            break;

        // The "Configure" Option (Toggles the Default)
        case 213155:
            if (c.seersTeleportUnlocked) { // Assuming they need diaries done, etc.
                if (c.seersTeleportDefault) {
                    c.seersTeleportDefault = false;
                    c.getPA().sendFrame36(644, 0); // Updates the client config instantly!
                    c.sendMessage("Your Camelot teleport will now take you to the Castle.");
                } else {
                    c.seersTeleportDefault = true;
                    c.getPA().sendFrame36(644, 1);
                    c.sendMessage("Your Camelot teleport will now take you to Seers' Village.");
                }
            } else {
                c.sendMessage("You must complete the Kandarin Hard Diary to configure this teleport.");
            }
            break;
        case 213157:
            if (c.GETeleportUnlocked) { // Assuming they need diaries done, etc.
                if (c.GETeleportDefault) {
                    c.GETeleportDefault = false;
                    c.getPA().sendFrame36(645, 0); // Updates the client config instantly!
                    c.sendMessage("Your Varrock teleport will now take you to Varrock square.");
                } else {
                    c.GETeleportDefault = true;
                    c.getPA().sendFrame36(645, 1);
                    c.sendMessage("Your Varrock teleport will now take you to the Grand Exchange.");
                }
            } else {
                c.sendMessage("You must complete the Varrock Hard Diary to configure this teleport.");
            }
            break;
		case 4146:
			c.handleTeleportRunes(556, 555, 563, 3, 1, 1, 2965+Misc.random(5), 3381+Misc.random(5));
			break;

		case 4150://camelot
			c.handleTeleportRunes(556, 563, -1, 5, 1, -1, Config.CAMELOT_X+Misc.random(2), Config.CAMELOT_Y+Misc.random(3));
			break;
		case 213154://seers
			c.handleTeleportRunes(556, 563, -1, 5, 1, -1, Config.SEERS_X+Misc.random(3), Config.SEERS_Y+Misc.random(2));
			break;

		case 6004://ardougne
			c.handleTeleportRunes(555, 563, -1, 2, 2, -1, Config.ARDOUGNE_X+Misc.random(5), Config.ARDOUGNE_Y+Misc.random(5));
			break;

		case 6005://watchtower
			c.getAD().completeAchievement("ArdougneHard", "Teleport to the Watchtower", 5);
			c.handleTeleportRunes(557, 563, -1, 2, 2, -1, Config.WATCHTOWER_X+Misc.random(5), Config.WATCHTOWER_Y+Misc.random(5));
			break;
		case 142197://kourend
			c.handleTeleportRunes(557, 556, 563, 1, 1, 1, Config.KOUREND_X+Misc.random(5), Config.KOUREND_Y+Misc.random(5));
			break;
		
		case 146060:
			c.inBuildingMode = true;
			if(c.isInsideHouse())
				HouseData.enterHouse(c, c, c.inBuildingMode);
			break;
		case 146061:
			c.inBuildingMode = false;
			if(c.isInsideHouse())
				HouseData.enterHouse(c, c, c.inBuildingMode);
			break;
		case 146059:
			House house = (House)c.getMapInstance();
			c.setInHouse(false);
			c.getPA().movePlayer(house.getLeaveX(), house.getLeaveY());
			break;
		case 243191: // Option 1
			if (c.dialogueAction == 100) { // Moving House
				if (c.getItems().playerHasItem(995, 5000)) {
					c.getItems().deleteItem(995, 5000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.RIMMINGTON;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Rimmington.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 5,000 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) { // Redecorating
				if (c.getItems().playerHasItem(995, 5000)) {
					c.getItems().deleteItem(995, 5000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 0);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Basic Wood.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 5,000 coins to redecorate.");
				}
			}
			break;

		case 243192: // Option 2
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 10) {
					c.sendMessage("You need a Construction level of 10 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 5000)) {
					c.getItems().deleteItem(995, 5000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.TAVERLY;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Taverley.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 5,000 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 5000)) {
					c.getItems().deleteItem(995, 5000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 1);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Basic Stone.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 5,000 coins to redecorate.");
				}
			}
			break;

		case 243193: // Option 3
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 20) {
					c.sendMessage("You need a Construction level of 20 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 7500)) {
					c.getItems().deleteItem(995, 7500);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.POLLNIVNEACH;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Pollnivneach.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 7,500 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 7500)) {
					c.getItems().deleteItem(995, 7500);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 2);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Whitewashed Stone.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 7,500 coins to redecorate.");
				}
			}
			break;

		case 243194: // Option 4
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 25) {
					c.sendMessage("You need a Construction level of 25 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 8750)) {
					c.getItems().deleteItem(995, 8750);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.HOSIDIUS;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Hosidius.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 8,750 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 10000)) {
					c.getItems().deleteItem(995, 10000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 3);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Fremennik-style wood.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 10,000 coins to redecorate.");
				}
			}
			break;

		case 243195: // Option 5
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 30) {
					c.sendMessage("You need a Construction level of 30 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 10000)) {
					c.getItems().deleteItem(995, 10000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.RELLEKKA;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Rellekka.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 10,000 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 15000)) {
					c.getItems().deleteItem(995, 15000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 4);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Tropical wood.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 15,000 coins to redecorate.");
				}
			}
			break;

		case 243196: // Option 6
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 40) {
					c.sendMessage("You need a Construction level of 40 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 15000)) {
					c.getItems().deleteItem(995, 15000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.BRIMHAVEN;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Brimhaven.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 15,000 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 25000)) {
					c.getItems().deleteItem(995, 25000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 5);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Fancy stone.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 25,000 coins to redecorate.");
				}
			}
			break;

		case 243197: // Option 7
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 50) {
					c.sendMessage("You need a Construction level of 50 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 25000)) {
					c.getItems().deleteItem(995, 25000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.YANILLE;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Yanille.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 25,000 coins to move your house.");
				}
			} else if (c.dialogueAction == 101) {
				if (c.getItems().playerHasItem(995, 35000)) {
					c.getItems().deleteItem(995, 35000);
					server.model.players.skills.construction.Construction.updateHouseStyle(c, 6);
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been redecorated to Dark stone.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 35,000 coins to redecorate.");
				}
			}
			break;
            
		case 243198: // Option 8 (Prifddinas, just in case your interface expands to 8 options!)
			if (c.dialogueAction == 100) {
				if (c.getSkills().getLevel(server.model.players.skills.Skill.CONSTRUCTION) < 70) {
					c.sendMessage("You need a Construction level of 70 to move here.");
					return;
				}
				if (c.getItems().playerHasItem(995, 50000)) {
					c.getItems().deleteItem(995, 50000);
					c.houseLocation = server.model.players.skills.construction.HouseLocation.PRIFDDINAS;
					server.model.players.skills.construction.Construction.saveHouse(c);
					c.sendMessage("Your house has been moved to Prifddinas.");
					c.getPA().closeAllWindows();
				} else {
					c.sendMessage("You need 50,000 coins to move your house.");
				}
			}
			break;
		case 146062:
			c.teleportInsidePOH = true;
			c.getPA().sendConfig(732, 1);
			break;
		case 146063:
			c.teleportInsidePOH = false;
			c.getPA().sendConfig(732, 0);
			break;
		case 146064:
			c.defaultBuildMode = true;
			break;
		case 146065:
			c.defaultBuildMode = false;
			break;
		case 146066:
			c.setPOHDoor(2);
			break;
		case 146067:
			c.setPOHDoor(3);
			//new Object(13102);
			break;
		case 146068:
			c.setPOHDoor(1);
			break;
		case 142172:// cast house teleport (Standard Magicbook)
			c.setInHouse(false);
			
			// Dynamically pull the X and Y from the player's saved location
			int spellX = c.houseLocation != null ? c.houseLocation.getX() : 2954; // 2954 is Rimmington fallback
			int spellY = c.houseLocation != null ? c.houseLocation.getY() : 3224;
			
			c.handleTeleportRunes(557, 556, 563, 1, 1, 1, spellX + Misc.random(1), spellY + Misc.random(2));
			break;

		case 213158:// Outside/Inside house teleport (Settings Menu / POH Portal)
			if (c.teleportInsidePOH) {
				c.setLastKnownLocation(c.getLocation());
				c.lastTeleportX = c.getLastKnownLocation().getX();
				c.lastTeleportY = c.getLastKnownLocation().getY();
				server.model.players.skills.construction.HouseData.enterHouse(c, c, c.defaultBuildMode);
			} else {
				c.setInHouse(false);
				
				// Dynamically pull the X and Y from the player's saved location
				int outsideX = c.houseLocation != null ? c.houseLocation.getX() : 2954;
				int outsideY = c.houseLocation != null ? c.houseLocation.getY() : 3224;
				
				c.handleTeleportRunes(557, 556, 563, 1, 1, 1, outsideX + Misc.random(1), outsideY + Misc.random(2));
			}
			break;
		case 51031:
		case 29031:
			c.handleTeleportRunes(554, 563, -1, 2, 2, -1, Config.TROLLHEIM_X+Misc.random(1), Config.TROLLHEIM_Y+Misc.random(1));
			break;
		case 117112:
			c.handleTeleportRunes(9075, 563, 557, 2, 1, 2, Config.MOONCLAN_X+Misc.random(1), Config.MOONCLAN_Y+Misc.random(1));
			break;
		case 117186:
			c.handleTeleportRunes(9075, 563, 554, 2, 2, 3, Config.BARBOUTPOST_X+Misc.random(1), Config.BARBOUTPOST_Y+Misc.random(1));
			break;
		case 72038:
		case 51039:
			// c.getDH().sendOption5("Option 18", "Option 2", "Option 3",
			// "Option 4", "Option 5");
			// c.teleAction = 8;
			break;
		case 53152:
			if (c.tutorialProgress < 36) {
				CookingTutorialIsland.getAmount(c, 1);
			} else {
				Cooking.cookItem(c, c.cookingItem, 1, c.cookingObject);
			}
			break;

		case 53151:
			if (c.tutorialProgress < 36) {
				CookingTutorialIsland.getAmount(c, 5);
			} else {
				Cooking.cookItem(c, c.cookingItem, 5, c.cookingObject);
			}
			break;

		case 53150:
			if (c.tutorialProgress < 36) {
				CookingTutorialIsland.getAmount(c, 10);
			} else {
				c.playerIsCooking = true;
				c.getOutStream().createFrame(27);
			}
			break;

		case 53149:
			if (c.tutorialProgress < 36) {
				CookingTutorialIsland.getAmount(c, 28);
			} else {
				Cooking.cookItem(c, c.cookingItem, 28, c.cookingObject);
			}
			break;

		case 9125: // Accurate
		case 6221: // range accurate
		case 22230: // punch (unarmed)
		case 48010: // flick (whip)
		case 21200: // spike (pickaxe)
		case 1080: // bash (staff)
		case 6168: // chop (axe)
		case 6236: // accurate (long bow)
		case 17102: // accurate (darts)
		case 8234: // stab (dagger)
			c.fightMode = 0;
			if (c.autocasting)
				c.getPA().resetAutocast();
			break;

		case 9126: // Defensive
		case 48008: // deflect (whip)
		case 22228: // kick (unarmed)
		case 21201: // block (pickaxe)
		case 1078: // focus - block (staff)
		case 6169: // block (axe)
		case 33019: // fend (hally)
		case 18078: // block (spear)
		case 8235: // block (dagger)
			c.fightMode = 1;
			if (c.autocasting)
				c.getPA().resetAutocast();
			break;

		case 9127: // Controlled
		case 48009: // lash (whip)
		case 33018: // jab (hally)
		case 6234: // longrange (long bow)
		case 6219: // longrange
		case 18077: // lunge (spear)
		case 18080: // swipe (spear)
		case 18079: // pound (spear)
		case 17100: // longrange (darts)
			c.fightMode = 3;
			if (c.autocasting)
				c.getPA().resetAutocast();
			break;

		case 9128: // Aggressive
		case 6220: // range rapid
		case 22229: // kick (unarmed)
		case 21203: // impale (pickaxe)
		case 21202: // smash (pickaxe)
		case 1079: // pound (staff)
		case 6171: // hack (axe)
		case 6170: // smash (axe)
		case 33020: // swipe (hally)
		case 6235: // rapid (long bow)
		case 17101: // repid (darts)
		case 8237: // lunge (dagger)
		case 8236: // slash (dagger)
			c.fightMode = 2;
			if (c.autocasting)
				c.getPA().resetAutocast();
			break;
		case 67048:
			c.setSidebarInterface(5, 17200);
			break;
		case 67049:
			c.setSidebarInterface(5, 5608);
			break;
			/** Prayers **/
		case 82132: // thick skin
			c.getCombat().activatePrayer(0);
			break;
		case 82133: // burst of str
			c.getCombat().activatePrayer(1);
			break;
		case 82134: // charity of thought
			c.getCombat().activatePrayer(2);
			break;
		case 70080: // range
			c.getCombat().activatePrayer(3);
			break;
		case 70082: // mage
			c.getCombat().activatePrayer(4);
			break;
		case 82135: // rockskin
			c.getCombat().activatePrayer(5);
			break;
		case 82136: // super human
			c.getCombat().activatePrayer(6);
			break;
		case 82137: // improved reflexes
			c.getCombat().activatePrayer(7);
			break;
		case 82138: // hawk eye
			c.getCombat().activatePrayer(8);
			break;
		case 82139:
			c.getCombat().activatePrayer(9);
			break;
		case 82140: // protect Item
			c.getCombat().activatePrayer(10);
			break;
		case 70084: // 26 range
			c.getCombat().activatePrayer(11);
			break;
		case 70086: // 27 mage
			c.getCombat().activatePrayer(12);
			break;
		case 82141: // steel skin
			c.getCombat().activatePrayer(13);
			break;
		case 82142: // ultimate str
			c.getCombat().activatePrayer(14);
			break;
		case 82143: // incredible reflex
			c.getCombat().activatePrayer(15);
			break;
		case 82144: // protect from magic
			c.getCombat().activatePrayer(16);
			break;
		case 82145: // protect from range
			c.getCombat().activatePrayer(17);
			break;
		case 82146: // protect from melee
			c.getCombat().activatePrayer(18);
			break;
		case 70088: // 44 range
			c.getCombat().activatePrayer(19);
			break;
		case 70090: // 45 mystic
			c.getCombat().activatePrayer(20);
			break;
		case 2171: // retrui
			c.getCombat().activatePrayer(21);
			break;
		case 2172: // redem
			c.getCombat().activatePrayer(22);
			break;
		case 2173: // smite
			c.getCombat().activatePrayer(23);
			break;
		case 73://preserve
			c.getCombat().activatePrayer(24);
			break;
		case 70092: // chiv
			c.getCombat().activatePrayer(25);
			break;
		case 70094: // piety
			c.getCombat().activatePrayer(26);
			break;
		case 75://rigour
			c.getCombat().activatePrayer(27);
			break;
		case 74://augury
			c.getCombat().activatePrayer(28);
			break;

		case 13092:
			if (!World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				c.sendMessage("You are not trading!");
				return;
			}
			if (System.currentTimeMillis() - c.getTrade().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).accept(c,
					MultiplayerSessionStage.OFFER_ITEMS);
			break;

		case 13218:

			if (!World.getWorld().getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				c.sendMessage("You are not trading!");
				return;
			}
			if (System.currentTimeMillis() - c.getTrade().getLastAccept() < 1000) {
				return;
			}
			c.getTrade().setLastAccept(System.currentTimeMillis());
			World.getWorld().getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).accept(c,
					MultiplayerSessionStage.CONFIRM_DECISION);
			break;
			/*
			 *
			 *Player Emotes 
			 *
			 */

		case 168:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(855);
			break;
		case 169:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(856);
			break;
		case 162:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(857);
			break;
		case 164:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(858);
			break;
		case 165:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(859);
			break;
		case 161:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(860);
			break;
		case 170:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(861);
			break;
		case 171:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(862);
			break;
		case 163:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(863);
			break;
		case 167:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(864);
			break;
		case 172:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(865);
			break;
		case 166:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(866);
			break;
		case 52050:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2105);
			break;
		case 52051:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2106);
			break;
		case 52052:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2107);
			break;
		case 52053:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2108);
			break;
		case 52054:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2109);
			break;
		case 52055:
			if (c.tutorialProgress == 10) {
				c.getDH().sendDialogues(3039, 0);
			}
			c.startAnimation(2110);
			break;
		case 52056:
			c.startAnimation(2111);
			break;
		case 52057:
			c.startAnimation(2112);
			break;
		case 52058:
			c.startAnimation(2113);
			break;
		case 43092:
			c.startAnimation(0x558);
			break;
		case 2155:
			c.startAnimation(0x46B);
			break;
		case 25103:
			c.startAnimation(0x46A);
			break;
		case 25106:
			c.startAnimation(0x469);
			break;
		case 2154:
			c.startAnimation(0x468);
			break;
		case 52071:
			c.startAnimation(0x84F);
			break;
		case 52072:
			c.startAnimation(0x850);
			break;
		case 59062:
			c.startAnimation(2836);
			break;
		case 72032:
			c.startAnimation(3544);
			break;
		case 72033:
			c.startAnimation(3543);
			break;
		case 72254:
			c.startAnimation(3866);
			break;
			
		case 112024:
			if(c.emoteUnlock[6])
				c.startAnimation(4276);
			else
				c.sendMessage("You must unlock this from the @blu@Stronghold Of Security.");
			break;
		case 112026:
			if(c.emoteUnlock[7])
				c.startAnimation(4278);
			else
				c.sendMessage("You must unlock this from the @blu@Stronghold Of Security.");
			break;
		case 112028:
			if(c.emoteUnlock[8])
				c.startAnimation(4280);
			else
				c.sendMessage("You must unlock this from the @blu@Stronghold Of Security.");
			break;
		case 112030:
			if(c.emoteUnlock[9])
				c.startAnimation(4275);
			else
				c.sendMessage("You must unlock this from the @blu@Stronghold Of Security.");
			break;
		case 112046:
			c.startAnimation(4751);
			c.gfx0(1239);
			break;
		case 112048:
			c.performUriTransform(c);
			break;
		case 151048:
			c.startAnimation(874);
			break;
		case 112054:
			c.startAnimation(874);
			c.gfx0(1412);
			break;
		case 151050:
			c.startAnimation(872);
			break;
		case 151052:
			c.startAnimation(870);
			break;
		case 151054:
			c.startAnimation(868);
			break;
		case 112040:
			c.startAnimation(8917);
			break;
		case 112042:
			c.startAnimation(1708);
			c.gfx100(320);
			break;
		case 112044:
			c.startAnimation(7131);
			break;
			/* END OF EMOTES */
		case 71073:

			c.getItems().updateInventory();
			c.getItems().resetItems(5064);
			break;
		case 24017:
			c.getPA().resetAutocast();
			// c.sendFrame246(329, 200, c.playerEquipment[c.playerWeapon]);
			c.getItems().sendWeapon(c.playerEquipment[c.playerWeapon],ItemAssistant.getItemName(c.playerEquipment[c.playerWeapon]));
			// c.setSidebarInterface(0, 328);
			// c.setSidebarInterface(6, c.playerMagicBook == 0 ? 1151 :
			// c.playerMagicBook == 1 ? 12855 : 1151);
			break;
		}
		if (c.isAutoButton(actionButtonId))
			c.assignAutocast(actionButtonId);
	}

}
