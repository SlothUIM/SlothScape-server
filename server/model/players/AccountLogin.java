package server.model.players;

import server.ServerStatusWriter;
import server.model.content.STASH;
import server.model.players.quests.QuestAssistant;
import server.world.ApiClient;
import server.world.Boundary;
import server.world.Location;
import server.model.players.skills.Skill;
import server.model.items.ItemAssistant;
import server.Config;
import server.world.World;
import server.util.Misc;

public class AccountLogin {

    public static void enforceLoginBoundaryCheck(Player p) {
        for (LoginBoundaryCheck safeguard : LoginBoundaryCheck.values()) {
            if (Boundary.isIn(p, safeguard.getForbiddenZone())) {
                Location safeLoc = safeguard.getSafeLocation();
                p.getPA().movePlayer(safeLoc.getX(), safeLoc.getY(), safeLoc.getZ());
                p.sendMessage("You have been safely relocated outside of the instanced area.");
                return;
            }
        }
    }

    // Phase 1: Core Engine prep (Before the player is visible)
    public static void setupPlayerEnvironment(Player p) {
        p.graceSum();
        World.getWorld().getGlobalObjects().updateRegionObjects(p);
        ApiClient.updatePlayerStatus(p.playerName, ServerStatusWriter.WORLD_ID);
        p.setStopPlayer(false);
        p.getPlayerAction().setAction(false);
        p.setNeedsPlacement(true);
        p.combatLevel = p.calculateCombatLevel();

        p.outStream.createFrame(249);
        p.outStream.writeByteA(1); // 1 for members, 0 for free
        p.outStream.writeWordBigEndianA(p.getIndex());

        // Disconnect duplicate logins safely
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (j == p.getIndex()) continue;
            if (PlayerHandler.players[j] != null && PlayerHandler.players[j].playerName.equalsIgnoreCase(p.playerName)) {
                p.disconnected = true;
                break;
            }
        }

        // Reset Prayers
        for (int i = 0; i < p.PRAYER.length; i++) {
            p.prayerActive[i] = false;
            p.getPA().sendFrame36(p.PRAYER_GLOW[i], 0);
        }
    }

    // Phase 2: Building the UI and loading social data
    public static void loadInterfacesAndGameData(Player p) {
        p.getPA().handleWeaponStyle();
        p.accountFlagged = p.getPA().checkForFlags();
        p.getPA().sendFrame36(108, 0);
        p.getPA().sendFrame107(); // reset screen
        p.getPA().setChatOptions(0, 0, 0);
        QuestAssistant.sendStages(p);

        // Interfaces & Texts
        p.getPA().sendFrame126(p.runEnergy + "", 149);
        p.getPA().sendFrame36(427, p.acceptAid ? 1 : 0);
        p.getPA().sendFrame126(p.getCollectionLog().getUnlockedLogCount() + "/447", 55810);
        p.getPA().sendFrame126(p.getCollectionLog().getUnlockedLogCount() + "/447", 35702);
        p.sendMessage("Join clan <col=3F48CC>'Sloth'</col> for help.");
        for (int i = 25433; i <= 25532; i++) {
            p.getPA().sendFrame126("", i);
        }

        p.loadSlayerInterfaceText();
        p.getPA().sendFrame126("" + p.combatLevel, 55804);
        p.getPA().sendFrame126("Total level:\\n "+ p.getSkills().getTotalLevel(), 27127);
        if (p.playerRights != 0) p.getPA().sendFrame126("", 14605); // Mod Mute Option

        // Right Click Options
        p.getPA().showOption(4, 0, "Follow", 4);
        p.getPA().showOption(5, 0, "Trade with", 3);

        // Chat, Friends & Clans
        if (p.getPrivateChat() > 2) p.setPrivateChat(0);
        p.outStream.createFrame(221);
        p.outStream.writeByte(2);
        p.outStream.createFrame(206);
        p.outStream.writeByte(0);
        p.outStream.writeByte(p.getPrivateChat());
        p.outStream.writeByte(0);
        p.getFriends().sendList();
        p.getIgnores().sendList();
        p.getPA().clearClanChat();
        p.getPA().setClanData();
    }

    // Phase 3: Loading the items so the player model updates
    public static void loadEquipment(Player p) {
        p.getItems().resetItems(3214);
        p.getItems().sendWeapon(p.playerEquipment[p.playerWeapon], ItemAssistant.getItemName(p.playerEquipment[p.playerWeapon]));
        p.getItems().resetBonus();
        p.getItems().getBonus();
        p.getItems().writeBonus();

        p.getItems().setEquipment(p.playerEquipment[p.playerHat], 1, p.playerHat);
        p.getItems().setEquipment(p.playerEquipment[p.playerCape], 1, p.playerCape);
        p.getItems().setEquipment(p.playerEquipment[p.playerAmulet], 1, p.playerAmulet);
        p.getItems().setEquipment(p.playerEquipment[p.playerArrows], p.playerEquipmentN[p.playerArrows], p.playerArrows);
        p.getItems().setEquipment(p.playerEquipment[p.playerChest], 1, p.playerChest);
        p.getItems().setEquipment(p.playerEquipment[p.playerShield], 1, p.playerShield);
        p.getItems().setEquipment(p.playerEquipment[p.playerLegs], 1, p.playerLegs);
        p.getItems().setEquipment(p.playerEquipment[p.playerHands], 1, p.playerHands);
        p.getItems().setEquipment(p.playerEquipment[p.playerFeet], 1, p.playerFeet);
        p.getItems().setEquipment(p.playerEquipment[p.playerRing], 1, p.playerRing);
        p.getItems().setEquipment(p.playerEquipment[p.playerWeapon], p.playerEquipmentN[p.playerWeapon], p.playerWeapon);

        p.getCombat().getPlayerAnimIndex(ItemAssistant.getItemName(p.playerEquipment[p.playerWeapon]).toLowerCase());
        p.getItems().addSpecialBar(p.playerEquipment[p.playerWeapon]);
    }

    // Phase 4: Final triggers, late-UI loading (RUNE POUCH FIX!), and announcements
    public static void finalizeLogin(Player p) {
        // --- FIX: RUNE POUCH LOADED LAST ---
        // The UI is completely built now, so this packet won't get lost!
        p.getRunePouch().sendLegacyRuneTypes();

        if (p.getSlayer().superiorSpawned) p.getSlayer().superiorSpawned = false;

        p.saveTimer = Config.SAVE_TIMER;
        p.saveCharacter = true;

        p.getPA().resetFollow();
        p.getPA().sendAutoRetalitate();
        p.getPA().sendConfigStates();
        p.getNpcDeathTracker().load();
        server.model.players.skills.construction.Construction.loadFullHouse(p);
        p.getAD().loadAchievementProgress();
        STASH.refreshConfigs(p);
        p.addEvents();

        p.getHealth().setMaximum(p.getSkills().getActualLevel(Skill.HITPOINTS));
        p.isDead = false;
        p.getSkills().sendRefresh();

        if (p.getHealth().getAmount() <= 0) p.getHealth().setAmount(10);
        p.getRechargeItems().onLogin();

        // Welcome messages & tutorial
        for (Player player : PlayerHandler.players) {
            if (player != null && player.isActive && player != p) {
                player.sendMessage("<col=255>[SlothLite]</col> " + p.playerName + " has just logged in!");
            }
        }

        if (p.tutorialProgress > 0 && p.tutorialProgress < 36 && Config.TUTORIAL_ISLAND) {
            p.sendMessage("@blu@Continue the tutorial from the last step you were on.@bla@");
        }
        if (p.tutorialProgress > 35) {
            p.sendSidebars();
            p.sendMessage("Welcome to @blu@" + Config.SERVER_NAME + "@bla@ - we are currently on v@blu@" + Config.CLIENT_VERSION + "@bla@.");
            p.getAD().loadAchievementProgress();
        } else if (p.tutorialProgress <= 0) {
            p.getPA().firstTimeTutorial();
        }

        server.model.players.BankPin pin = p.getBankPin();
        if (pin.requiresUnlock()) {
            pin.open(2);
        }

        p.initialized = true;
        Misc.println("[REGISTERED]: " + p.playerName);
    }
}