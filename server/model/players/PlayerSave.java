package server.model.players;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import server.Config;
import server.HighscoreSaver;
import server.model.items.Item;
import server.model.items.bank.BankItem;
import server.model.items.collectionlog.CollectionLogData;
import server.model.npcs.NPC;
import server.model.players.content.GodBookManager;
import server.model.players.skills.Skill;
import server.model.players.skills.mining.motherlode.MotherlodeMine;
import server.model.players.skills.mining.motherlode.OreStack;
import server.util.Misc;

public class PlayerSave {

    // Creates a Gson instance that automatically formats the JSON to look pretty and readable
    private static final Gson builder = new GsonBuilder().setPrettyPrinting().create();

    public static String getSaveDirectory() {
        String localDropboxPath = "C:/Users/Sasqu/Dropbox/public/Servercharacters/";
        File localDir = new File(localDropboxPath);

        if (localDir.exists() && localDir.isDirectory()) {
            return localDropboxPath;
        }

        String vpsPath = Config.CHARACTER_SAVE_DIRECTORY;
        if (!vpsPath.endsWith("/") && !vpsPath.endsWith("\\")) {
            vpsPath += "/";
        }
        return vpsPath;
    }

    /**
     * 1. THE JSON LOAD METHOD
     */
    public static int loadGame(Player p, String playerName, String playerPass) {
        File jsonFile = new File(getSaveDirectory() + playerName + ".json");

        // --- SEAMLESS MIGRATION ---
        if (!jsonFile.exists()) {
            File txtFile = new File(getSaveDirectory() + playerName + ".txt");
            if (txtFile.exists()) {
                System.out.println("Migrating " + playerName + " from TXT to JSON...");
                return loadLegacyTextGame(p, playerName, playerPass);
            }
            p.newPlayer = true;
            return 0; // New player
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            // The classic, pre-2.8.6 way to parse JSON:
            JsonParser parser = new JsonParser();
            JsonObject save = parser.parse(reader).getAsJsonObject();

            // --- ACCOUNT & SECURITY ---
            // Trust the WebAuth password entirely, overwrite the local memory to keep it synced
            p.playerPass = playerPass;
            p.playerRights = save.has("rights") ? save.get("rights").getAsInt() : 0;
            p.setEmail(save.has("email") ? save.get("email").getAsString() : "");

            // --- POSITION & VITALS ---
            p.setX(save.get("x").getAsInt());
            p.setY(save.get("y").getAsInt());
            p.setHeight(save.get("height").getAsInt());
            p.getHealth().setAmount(save.get("hitpoints").getAsInt());
            if (p.getHealth().getAmount() <= 0) p.getHealth().setAmount(10);

            // --- ATTRIBUTES (Booleans & Ints) ---
            if (save.has("attributes")) {
                JsonObject attr = save.get("attributes").getAsJsonObject();
                if (attr.has("tutorialProgress")) p.tutorialProgress = attr.get("tutorialProgress").getAsInt();
                if (attr.has("crystalBowShots")) p.crystalBowArrowCount = attr.get("crystalBowShots").getAsInt();
                if (attr.has("skullTimer")) p.skullTimer = attr.get("skullTimer").getAsInt();
                if (attr.has("magicBook")) p.playerMagicBook = attr.get("magicBook").getAsInt();
                if (attr.has("specAmount")) p.specAmount = attr.get("specAmount").getAsDouble();
                if (attr.has("randomCoffin")) p.randomCoffin = attr.get("randomCoffin").getAsInt();
                if (attr.has("EleWorkshopStage")) p.EleWorkshopWater_stage = attr.get("EleWorkshopStage").getAsInt();
                if (attr.has("questPoints")) p.questPoints = attr.get("questPoints").getAsInt();
                if (attr.has("woolHandedIn")) p.woolHandedIn = attr.get("woolHandedIn").getAsInt();
                if (attr.has("rumInCrate")) p.rumInCrate = attr.get("rumInCrate").getAsBoolean();
                if (attr.has("bananasInCrate")) p.bananasInCrate = attr.get("bananasInCrate").getAsInt();
                if (attr.has("teleblockLength")) {
                    p.teleBlockLength = attr.get("teleblockLength").getAsInt();
                    p.teleBlockDelay = System.currentTimeMillis();
                }
                if (attr.has("beginnerClueStep")) p.currentBeginnerClueStep = attr.get("beginnerClueStep").getAsInt();
                if (attr.has("pcPoints")) p.pcPoints = attr.get("pcPoints").getAsInt();
                if (attr.has("slayerTask")) p.slayerTask = attr.get("slayerTask").getAsInt();
                if (attr.has("taskAmount")) p.taskAmount = attr.get("taskAmount").getAsInt();
                if (attr.has("magePoints")) p.magePoints = attr.get("magePoints").getAsInt();
                if (attr.has("autoRet")) p.autoRet = attr.get("autoRet").getAsInt();
                if (attr.has("barrowsKillCount")) p.barrowsKillCount = attr.get("barrowsKillCount").getAsInt();
                if (attr.has("accountFlagged")) p.accountFlagged = attr.get("accountFlagged").getAsBoolean();
                if (attr.has("waveId")) p.waveId = attr.get("waveId").getAsInt();
                if (attr.has("gwkc")) p.killCount = attr.get("gwkc").getAsInt();
                if (attr.has("blowpipeCharges")) p.BlowpipeCharges = attr.get("blowpipeCharges").getAsInt();
                if (attr.has("blowpipeDarts")) p.BlowpipeDarts = attr.get("blowpipeDarts").getAsInt();
                if (attr.has("dartType")) p.DartType = attr.get("dartType").getAsInt();
                if (attr.has("fightMode")) p.fightMode = attr.get("fightMode").getAsInt();
                if (attr.has("daysSinceRecov")) p.daysSinceRecovChange = attr.get("daysSinceRecov").getAsInt();
                if (attr.has("unreadMessages")) p.unreadMessages = attr.get("unreadMessages").getAsInt();
                if (attr.has("daysSinceLastLogin")) p.daysSinceLastLogin = attr.get("daysSinceLastLogin").getAsInt();
                if (attr.has("lastLoginDate")) p.lastLoginDate = attr.get("lastLoginDate").getAsLong();
                if (attr.has("accountCreationDate")) p.accountCreationDate = attr.get("accountCreationDate").getAsLong();
                if (attr.has("emailRegistered")) p.isEmailRegistered = attr.get("emailRegistered").getAsBoolean();
            }

            // --- ARRAYS (Quests, Emotes, Stashes, Void, Barrows) ---
            if (save.has("questStages")) {
                JsonArray arr = save.get("questStages").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.questStages.length; i++) p.questStages[i] = arr.get(i).getAsInt();
            }
            if (save.has("stashBuilt")) {
                JsonArray arr = save.get("stashBuilt").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.stashBuilt.length; i++) p.stashBuilt[i] = arr.get(i).getAsBoolean();
            }
            if (save.has("stashFilled")) {
                JsonArray arr = save.get("stashFilled").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.stashFilled.length; i++) p.stashFilled[i] = arr.get(i).getAsBoolean();
            }
            if (save.has("emoteUnlock")) {
                JsonArray arr = save.get("emoteUnlock").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.emoteUnlock.length; i++) p.emoteUnlock[i] = arr.get(i).getAsBoolean();
            }
            if (save.has("voidStatus")) {
                JsonArray arr = save.get("voidStatus").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.voidStatus.length; i++) p.voidStatus[i] = arr.get(i).getAsInt();
            }
            if (save.has("barrowsNpcs")) {
                JsonArray arr = save.get("barrowsNpcs").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.barrowsNpcs.length; i++) p.barrowsNpcs[i][1] = arr.get(i).getAsInt();
            }

            // --- SKILLS ---
            if (save.has("skills")) {
                JsonArray arr = save.get("skills").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject obj = arr.get(i).getAsJsonObject();
                    Skill skill = Skill.forId(i);
                    if (skill != null) {
                        p.getSkills().setLevel(obj.get("level").getAsInt(), skill);
                        p.getSkills().setExperience(obj.get("exp").getAsInt(), skill);
                    }
                }
            }

            // --- LOOK & EQUIPMENT ---
            if (save.has("look")) {
                JsonArray arr = save.get("look").getAsJsonArray();
                for (int i = 0; i < arr.size() && i < p.playerAppearance.length; i++) p.playerAppearance[i] = arr.get(i).getAsInt();
            }
            if (save.has("equipment")) {
                JsonArray arr = save.get("equipment").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject item = arr.get(i).getAsJsonObject();
                    p.playerEquipment[item.get("slot").getAsInt()] = item.get("id").getAsInt();
                    p.playerEquipmentN[item.get("slot").getAsInt()] = item.get("amount").getAsInt();
                }
            }

            // --- INVENTORIES (Main, Bank, Lootbag, Pouches) ---
            if (save.has("inventory")) {
                JsonArray arr = save.get("inventory").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject item = arr.get(i).getAsJsonObject();
                    p.playerItems[item.get("slot").getAsInt()] = item.get("id").getAsInt();
                    p.playerItemsN[item.get("slot").getAsInt()] = item.get("amount").getAsInt();
                }
            }
            if (save.has("lootbag")) {
                JsonArray arr = save.get("lootbag").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    JsonObject item = arr.get(i).getAsJsonObject();
                    p.playerLootItems[item.get("slot").getAsInt()] = item.get("id").getAsInt();
                    p.playerLootItemsN[item.get("slot").getAsInt()] = item.get("amount").getAsInt();
                }
            }
            if (save.has("bank")) {
                JsonArray bankTabs = save.get("bank").getAsJsonArray();
                for (int i = 0; i < bankTabs.size(); i++) {
                    JsonObject tabObj = bankTabs.get(i).getAsJsonObject();
                    int tabId = tabObj.get("tab").getAsInt();
                    JsonArray items = tabObj.get("items").getAsJsonArray();
                    for (int j = 0; j < items.size(); j++) {
                        JsonObject item = items.get(j).getAsJsonObject();
                        p.getBank().getBankTab()[tabId].add(new BankItem(item.get("id").getAsInt(), item.get("amount").getAsInt()));
                        // Keep legacy arrays synced just in case
                        p.bankItems[tabId] = item.get("id").getAsInt();
                        p.bankItemsN[tabId] = item.get("amount").getAsInt();
                    }
                }
            }

            // --- BAGS & SACKS ---
            if (save.has("bags")) {
                JsonObject bags = save.get("bags").getAsJsonObject();
                if (bags.has("runePouch")) {
                    JsonArray arr = bags.get("runePouch").getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject item = arr.get(i).getAsJsonObject();
                        p.getRunePouch().getItems().add(new server.model.items.Item(item.get("id").getAsInt(), item.get("amount").getAsInt()));
                    }
                }
                if (bags.has("herbSack")) {
                    JsonArray arr = bags.get("herbSack").getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject item = arr.get(i).getAsJsonObject();
                        p.getHerbSack().getItems().add(new server.model.items.Item(item.get("id").getAsInt(), item.get("amount").getAsInt()));
                    }
                }
                if (bags.has("gemBag")) {
                    JsonArray arr = bags.get("gemBag").getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject item = arr.get(i).getAsJsonObject();
                        p.getGemBag().getItems().add(new server.model.items.Item(item.get("id").getAsInt(), item.get("amount").getAsInt()));
                    }
                }
            }

            // --- MINIGAMES & FEATURES ---
            if (save.has("minigames")) {
                JsonObject mg = save.get("minigames").getAsJsonObject();
                if (mg.has("nmz")) {
                    JsonObject nmz = mg.get("nmz").getAsJsonObject();
                    p.nmzCoffer = nmz.get("coffer").getAsInt();
                    p.nmzPoints = nmz.get("points").getAsInt();
                    p.nmzOverloadDoses = nmz.get("overloads").getAsInt();
                    p.nmzAbsorptionDoses = nmz.get("absorbs").getAsInt();
                    p.nmzSuperMagicDoses = nmz.get("superMagic").getAsInt();
                    p.nmzSuperRangingDoses = nmz.get("superRange").getAsInt();
                }
                if (mg.has("motherlode")) {
                    JsonObject ml = mg.get("motherlode").getAsJsonObject();
                    p.payDirtSackAmt = ml.get("sackAmt").getAsInt();
                    p.setPayDirtSack(new java.util.ArrayList<>());
                    if (ml.has("sackItems")) {
                        for (com.google.gson.JsonElement e : ml.get("sackItems").getAsJsonArray()) {
                            JsonObject obj = e.getAsJsonObject();
                            p.getPayDirtSack().add(new server.model.players.skills.mining.motherlode.OreStack(obj.get("id").getAsInt(), obj.get("amount").getAsInt()));
                        }
                    }
                    p.payDirtPending = new java.util.ArrayList<>();
                    if (ml.has("pendingItems")) {
                        for (com.google.gson.JsonElement e : ml.get("pendingItems").getAsJsonArray()) {
                            JsonObject obj = e.getAsJsonObject();
                            p.payDirtPending.add(new server.model.players.skills.mining.motherlode.OreStack(obj.get("id").getAsInt(), obj.get("amount").getAsInt()));
                        }
                    }
                }
                if (mg.has("agilityRooftops")) {
                    JsonArray arr = mg.get("agilityRooftops").getAsJsonArray();
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject obj = arr.get(i).getAsJsonObject();
                        int r = obj.get("r").getAsInt();
                        int c = obj.get("c").getAsInt();
                        if (r < p.getAgilityHandler().RoofAgilityProgress.length && c < p.getAgilityHandler().RoofAgilityProgress[r].length) {
                            p.getAgilityHandler().RoofAgilityProgress[r][c] = obj.get("v").getAsBoolean();
                        }
                    }
                }
            }

            // --- FARMING ---
            if (save.has("farming")) {
                JsonObject farm = save.get("farming").getAsJsonObject();
                if (farm.has("herbs")) {
                    JsonArray arr = farm.get("herbs").getAsJsonArray();
                    for (int i = 0; i < arr.size() && i < p.getHerbs().herbStages.length; i++) {
                        JsonObject obj = arr.get(i).getAsJsonObject();
                        p.getHerbs().herbState[i] = obj.get("state").getAsInt();
                        p.getHerbs().herbSeeds[i] = obj.get("seed").getAsInt();
                        p.getHerbs().herbStages[i] = obj.get("stage").getAsInt();
                        p.getHerbs().herbTimer[i] = obj.get("timer").getAsLong();
                    }
                }
                if (farm.has("flowers")) {
                    JsonArray arr = farm.get("flowers").getAsJsonArray();
                    for (int i = 0; i < arr.size() && i < p.getFlowers().flowerStages.length; i++) {
                        JsonObject obj = arr.get(i).getAsJsonObject();
                        p.getFlowers().flowerState[i] = obj.get("state").getAsInt();
                        p.getFlowers().flowerSeeds[i] = obj.get("seed").getAsInt();
                        p.getFlowers().flowerStages[i] = obj.get("stage").getAsInt();
                        p.getFlowers().flowerTimer[i] = obj.get("timer").getAsLong();
                    }
                }
                if (farm.has("allotments")) {
                    JsonArray arr = farm.get("allotments").getAsJsonArray();
                    for (int i = 0; i < arr.size() && i < p.getAllotment().allotmentStages.length; i++) {
                        JsonObject obj = arr.get(i).getAsJsonObject();
                        p.getAllotment().allotmentState[i] = obj.get("state").getAsInt();
                        p.getAllotment().allotmentSeeds[i] = obj.get("seed").getAsInt();
                        p.getAllotment().allotmentStages[i] = obj.get("stage").getAsInt();
                        p.getAllotment().allotmentTimer[i] = obj.get("timer").getAsLong();
                    }
                }
            }
    // --- Tool Leprechaun Load ---
            if (save.has("leprechaunTools")) {
                com.google.gson.JsonArray toolsArray = save.get("leprechaunTools").getAsJsonArray();
                for (int i = 0; i < toolsArray.size() && i < p.getFarmingTools().tools.length; i++) {
                    p.getFarmingTools().tools[i] = toolsArray.get(i).getAsInt();
                }
            }
            // --- MUSIC UNLOCKS ---
            if (save.has("unlockedMusic")) {
                JsonArray music = save.get("unlockedMusic").getAsJsonArray();
                for (int i = 0; i < music.size(); i++) p.getMusic().unlockedSongIds.add(music.get(i).getAsInt());
            }

            // --- GOD BOOKS ---
            if (save.has("godBooks")) {
                JsonArray books = save.get("godBooks").getAsJsonArray();
                for (int i = 0; i < books.size(); i++) {
                    JsonObject obj = books.get(i).getAsJsonObject();
                    server.model.players.content.GodBookManager.markPageAdded(p, obj.get("bookId").getAsInt(), obj.get("pageId").getAsInt());
                }
            }

            // --- COLLECTION LOG ---
            if (save.has("collectionLog")) {
                JsonObject cl = save.get("collectionLog").getAsJsonObject();
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : cl.entrySet()) {
                    server.model.items.collectionlog.CollectionLogData logData = p.getCollectionLog().getLogBySaveKey(entry.getKey());

                    if (logData != null) {
                        // BACKWARDS COMPATIBILITY: Check if it's the old array format or the new Object format
                        if (entry.getValue().isJsonArray()) {
                            JsonArray items = entry.getValue().getAsJsonArray();
                            for (int i = 0; i < items.size(); i++) {
                                JsonObject item = items.get(i).getAsJsonObject();
                                logData.setAmount(item.get("index").getAsInt(), item.get("amount").getAsInt());
                            }
                        } else if (entry.getValue().isJsonObject()) {
                            JsonObject logObj = entry.getValue().getAsJsonObject();

                            // Load Kill Count
                            if (logObj.has("kc")) {
                                logData.setKillCount(logObj.get("kc").getAsInt());
                            }

                            // Load Items
                            if (logObj.has("items")) {
                                JsonArray items = logObj.get("items").getAsJsonArray();
                                for (int i = 0; i < items.size(); i++) {
                                    JsonObject item = items.get(i).getAsJsonObject();
                                    logData.setAmount(item.get("index").getAsInt(), item.get("amount").getAsInt());
                                }
                            }
                        }
                    }
                }
            }

            // --- ACHIEVEMENTS ---
            if (save.has("achievements")) {
                JsonObject ach = save.get("achievements").getAsJsonObject();
                for (java.util.Map.Entry<String, com.google.gson.JsonElement> entry : ach.entrySet()) {
                    String region = entry.getKey();
                    java.util.List<AchievementTask> tasks = p.getAD().playerAchievementMap.get(region);
                    if (tasks != null) {
                        JsonArray completedTasks = entry.getValue().getAsJsonArray();
                        for (int i = 0; i < completedTasks.size(); i++) {
                            String taskName = completedTasks.get(i).getAsString();
                            for (AchievementTask task : tasks) {
                                if (task.getTask().equalsIgnoreCase(taskName)) {
                                    task.setDone(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // --- FRIENDS ---
            if (save.has("friends")) {
                JsonArray arr = save.get("friends").getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    // Assuming you have a method to add friends
                        p.getFriends().add(arr.get(i).getAsLong());
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading JSON save for " + playerName);
            e.printStackTrace();
            return 13;
        }
        return 1;
    }

    /**
     * 2. THE JSON SAVE METHOD
     */
    public static boolean saveGame(Player p) {
        if (!p.saveFile || !p.saveCharacter || p.playerName == null || PlayerHandler.players[p.getIndex()] == null) {
            return false;
        }
        p.playerName = p.playerName2;

        int tbTime = (int) (p.teleBlockDelay - System.currentTimeMillis() + p.teleBlockLength);
        if (tbTime > 300000 || tbTime < 0) tbTime = 0;

        try {
            JsonObject save = new JsonObject();

            HighscoreSaver.savePlayer(p);
            // --- ACCOUNT & SECURITY ---
            save.addProperty("username", p.playerName);
            save.addProperty("password", p.playerPass);
            save.addProperty("rights", p.playerRights);
            save.addProperty("email", p.getEmail());

            // --- POSITION & VITALS ---
            save.addProperty("x", p.getX());
            save.addProperty("y", p.getY());
            save.addProperty("height", p.getHeight());
            save.addProperty("hitpoints", p.getHealth().getAmount());

            // --- ATTRIBUTES ---
            JsonObject attr = new JsonObject();
            attr.addProperty("tutorialProgress", p.tutorialProgress);
            attr.addProperty("crystalBowShots", p.crystalBowArrowCount);
            attr.addProperty("skullTimer", p.skullTimer);
            attr.addProperty("magicBook", p.playerMagicBook);
            attr.addProperty("specAmount", p.specAmount);
            attr.addProperty("randomCoffin", p.randomCoffin);
            attr.addProperty("EleWorkshopStage", p.EleWorkshopWater_stage);
            attr.addProperty("questPoints", p.questPoints);
            attr.addProperty("woolHandedIn", p.woolHandedIn);
            attr.addProperty("rumInCrate", p.rumInCrate);
            attr.addProperty("bananasInCrate", p.bananasInCrate);
            attr.addProperty("teleblockLength", tbTime);
            attr.addProperty("beginnerClueStep", p.currentBeginnerClueStep);
            attr.addProperty("pcPoints", p.pcPoints);
            attr.addProperty("slayerTask", p.slayerTask);
            attr.addProperty("taskAmount", p.taskAmount);
            attr.addProperty("magePoints", p.magePoints);
            attr.addProperty("autoRet", p.autoRet);
            attr.addProperty("barrowsKillCount", p.barrowsKillCount);
            attr.addProperty("accountFlagged", p.accountFlagged);
            attr.addProperty("waveId", p.waveId);
            attr.addProperty("gwkc", p.killCount);
            attr.addProperty("blowpipeCharges", p.BlowpipeCharges);
            attr.addProperty("blowpipeDarts", p.BlowpipeDarts);
            attr.addProperty("dartType", p.DartType);
            attr.addProperty("fightMode", p.fightMode);
            attr.addProperty("daysSinceRecov", p.daysSinceRecovChange);
            attr.addProperty("unreadMessages", p.unreadMessages);
            attr.addProperty("daysSinceLastLogin", p.daysSinceLastLogin);
            attr.addProperty("lastLoginDate", p.lastLoginDate);
            attr.addProperty("accountCreationDate", p.accountCreationDate);
            attr.addProperty("emailRegistered", p.isEmailRegistered);
            save.add("attributes", attr);

            // --- ARRAYS ---
            JsonArray questStages = new JsonArray();
            for (int val : p.questStages) questStages.add(new JsonPrimitive(val));
            save.add("questStages", questStages);

            JsonArray stashBuilt = new JsonArray();
            for (boolean val : p.stashBuilt) stashBuilt.add(new JsonPrimitive(val));
            save.add("stashBuilt", stashBuilt);

            JsonArray stashFilled = new JsonArray();
            for (boolean val : p.stashFilled) stashFilled.add(new JsonPrimitive(val));
            save.add("stashFilled", stashFilled);

            JsonArray emoteUnlock = new JsonArray();
            for (boolean val : p.emoteUnlock) emoteUnlock.add(new JsonPrimitive(val));
            save.add("emoteUnlock", emoteUnlock);

            JsonArray voidStatus = new JsonArray();
            for (int val : p.voidStatus) voidStatus.add(new JsonPrimitive(val));
            save.add("voidStatus", voidStatus);

            JsonArray barrowsNpcs = new JsonArray();
            for (int i = 0; i < p.barrowsNpcs.length; i++) barrowsNpcs.add(new JsonPrimitive(p.barrowsNpcs[i][1]));
            save.add("barrowsNpcs", barrowsNpcs);

            // --- SKILLS ---
            JsonArray skillsArray = new JsonArray();
            for (int i = 0; i < Skill.length(); i++) {
                JsonObject skillObj = new JsonObject();
                skillObj.addProperty("level", p.getSkills().getLevel(Skill.forId(i)));
                skillObj.addProperty("exp", p.getSkills().getExperience(Skill.forId(i)));
                skillsArray.add(skillObj);
            }
            save.add("skills", skillsArray);

            // --- LOOK & EQUIPMENT ---
            JsonArray look = new JsonArray();
            for (int val : p.playerAppearance) look.add(new JsonPrimitive(val));
            save.add("look", look);

            JsonArray equipment = new JsonArray();
            for (int i = 0; i < p.playerEquipment.length; i++) {
                JsonObject item = new JsonObject();
                item.addProperty("slot", i);
                item.addProperty("id", p.playerEquipment[i]);
                item.addProperty("amount", p.playerEquipmentN[i]);
                equipment.add(item);
            }
            save.add("equipment", equipment);

            // --- INVENTORIES ---
            JsonArray inventory = new JsonArray();
            for (int i = 0; i < p.playerItems.length; i++) {
                if (p.playerItems[i] > 0) {
                    JsonObject item = new JsonObject();
                    item.addProperty("slot", i);
                    item.addProperty("id", p.playerItems[i]);
                    item.addProperty("amount", p.playerItemsN[i]);
                    inventory.add(item);
                }
            }
            save.add("inventory", inventory);

            JsonArray lootbag = new JsonArray();
            for (int i = 0; i < p.playerLootItems.length; i++) {
                if (p.playerLootItems[i] > 0) {
                    JsonObject item = new JsonObject();
                    item.addProperty("slot", i);
                    item.addProperty("id", p.playerLootItems[i]);
                    item.addProperty("amount", p.playerLootItemsN[i]);
                    lootbag.add(item);
                }
            }
            save.add("lootbag", lootbag);

            JsonArray bankArray = new JsonArray();
            for (int i = 0; i < 9; i++) {
                if (p.getBank().getBankTab()[i].size() > 0) {
                    JsonObject tabObj = new JsonObject();
                    tabObj.addProperty("tab", i);
                    JsonArray tabItems = new JsonArray();
                    for (int j = 0; j < p.getBank().getBankTab()[i].size(); j++) {
                        BankItem item = p.getBank().getBankTab()[i].getItem(j);
                        if (item != null) {
                            JsonObject bItem = new JsonObject();
                            bItem.addProperty("id", item.getId());
                            bItem.addProperty("amount", item.getAmount());
                            tabItems.add(bItem);
                        }
                    }
                    tabObj.add("items", tabItems);
                    bankArray.add(tabObj);
                }
            }
            save.add("bank", bankArray);

            // --- BAGS & SACKS ---
            JsonObject bags = new JsonObject();
            JsonArray runePouch = new JsonArray();
            for (server.model.items.Item i : p.getRunePouch().getItems()) {
                if (i.getId() > 0) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", i.getId());
                    obj.addProperty("amount", i.getAmount());
                    runePouch.add(obj);
                }
            }
            bags.add("runePouch", runePouch);

            JsonArray herbSack = new JsonArray();
            for (server.model.items.Item i : p.getHerbSack().getItems()) {
                if (i.getId() > 0) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", i.getId());
                    obj.addProperty("amount", i.getAmount());
                    herbSack.add(obj);
                }
            }
            bags.add("herbSack", herbSack);

            JsonArray gemBag = new JsonArray();
            for (server.model.items.Item i : p.getGemBag().getItems()) {
                if (i.getId() > 0) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("id", i.getId());
                    obj.addProperty("amount", i.getAmount());
                    gemBag.add(obj);
                }
            }
            bags.add("gemBag", gemBag);
            save.add("bags", bags);

            // --- MINIGAMES & FEATURES ---
            JsonObject mg = new JsonObject();

            JsonObject nmz = new JsonObject();
            nmz.addProperty("coffer", p.nmzCoffer);
            nmz.addProperty("points", p.nmzPoints);
            nmz.addProperty("overloads", p.nmzOverloadDoses);
            nmz.addProperty("absorbs", p.nmzAbsorptionDoses);
            nmz.addProperty("superMagic", p.nmzSuperMagicDoses);
            nmz.addProperty("superRange", p.nmzSuperRangingDoses);
            mg.add("nmz", nmz);

            JsonObject motherlode = new JsonObject();
            motherlode.addProperty("sackAmt", p.payDirtSackAmt);
            JsonArray sackItems = new JsonArray();
            for (server.model.players.skills.mining.motherlode.OreStack ore : p.payDirtSackList) {
                JsonObject o = new JsonObject(); o.addProperty("id", ore.id); o.addProperty("amount", ore.amount); sackItems.add(o);
            }
            motherlode.add("sackItems", sackItems);

            JsonArray pendingItems = new JsonArray();
            for (server.model.players.skills.mining.motherlode.OreStack ore : p.payDirtPending) {
                JsonObject o = new JsonObject(); o.addProperty("id", ore.id); o.addProperty("amount", ore.amount); pendingItems.add(o);
            }
            motherlode.add("pendingItems", pendingItems);
            mg.add("motherlode", motherlode);

            JsonArray rooftops = new JsonArray();
            for (int i = 0; i < p.getAgilityHandler().RoofAgilityProgress.length; i++) {
                for (int j = 0; j < p.getAgilityHandler().RoofAgilityProgress[i].length; j++) {
                    if (p.getAgilityHandler().RoofAgilityProgress[i][j]) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("r", i); obj.addProperty("c", j); obj.addProperty("v", true);
                        rooftops.add(obj);
                    }
                }
            }
            mg.add("agilityRooftops", rooftops);
            save.add("minigames", mg);

            // --- FARMING ---
            JsonObject farm = new JsonObject();
            JsonArray herbs = new JsonArray();
            for (int i = 0; i < p.getHerbs().herbStages.length; i++) {
                JsonObject obj = new JsonObject();
                obj.addProperty("state", p.getHerbs().herbState[i]);
                obj.addProperty("seed", p.getHerbs().herbSeeds[i]);
                obj.addProperty("stage", p.getHerbs().herbStages[i]);
                obj.addProperty("timer", p.getHerbs().herbTimer[i]);
                herbs.add(obj);
            }
            farm.add("herbs", herbs);

            JsonArray flowers = new JsonArray();
            for (int i = 0; i < p.getFlowers().flowerStages.length; i++) {
                JsonObject obj = new JsonObject();
                obj.addProperty("state", p.getFlowers().flowerState[i]);
                obj.addProperty("seed", p.getFlowers().flowerSeeds[i]);
                obj.addProperty("stage", p.getFlowers().flowerStages[i]);
                obj.addProperty("timer", p.getFlowers().flowerTimer[i]);
                flowers.add(obj);
            }
            farm.add("flowers", flowers);

            JsonArray allotments = new JsonArray();
            for (int i = 0; i < p.getAllotment().allotmentStages.length; i++) {
                JsonObject obj = new JsonObject();
                obj.addProperty("state", p.getAllotment().allotmentState[i]);
                obj.addProperty("seed", p.getAllotment().allotmentSeeds[i]);
                obj.addProperty("stage", p.getAllotment().allotmentStages[i]);
                obj.addProperty("timer", p.getAllotment().allotmentTimer[i]);
                allotments.add(obj);
            }
            farm.add("allotments", allotments);

            save.add("farming", farm);
// --- Tool Leprechaun Save ---
            com.google.gson.JsonArray leprechaunTools = new com.google.gson.JsonArray();
            for (int val : p.getFarmingTools().tools) {
                leprechaunTools.add(new com.google.gson.JsonPrimitive(val));
            }
            save.add("leprechaunTools", leprechaunTools);
            // --- MUSIC UNLOCKS ---
            JsonArray musicArray = new JsonArray();
            for (Integer songId : p.getMusic().unlockedSongIds) musicArray.add(new JsonPrimitive(songId));
            save.add("unlockedMusic", musicArray);

            // --- GOD BOOKS ---
            JsonArray godBooks = new JsonArray();
            for (java.util.Map.Entry<Integer, java.util.Set<Integer>> entry : server.model.players.content.GodBookManager.getAllBookProgress(p).entrySet()) {
                int bookId = entry.getKey();
                for (int pageId : entry.getValue()) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("bookId", bookId);
                    obj.addProperty("pageId", pageId);
                    godBooks.add(obj);
                }
            }
            save.add("godBooks", godBooks);

            // --- COLLECTION LOG ---
            JsonObject clObj = new JsonObject();

            // Loop through the registry keys, but pull the data from the PLAYER'S instance!
            for (String key : p.getCollectionLog().registry.logs.keySet()) {
                server.model.items.collectionlog.CollectionLogData playerLog = p.getCollectionLog().getLogBySaveKey(key);

                if (playerLog == null) continue;

                int[][] entries = playerLog.getEntries();
                JsonArray items = new JsonArray();

                for (int i = 0; i < entries.length; i++) {
                    if (entries[i][2] > 0) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("index", i);
                        obj.addProperty("id", entries[i][0]);
                        obj.addProperty("amount", entries[i][2]);
                        items.add(obj);
                    }
                }

                // Wrap items and KC into a single object
                JsonObject logWrapper = new JsonObject();
                logWrapper.addProperty("kc", playerLog.getKillCount());

                if (items.size() > 0) {
                    logWrapper.add("items", items);
                }

                // Only save to JSON if they have actually killed the boss or obtained an item
                if (items.size() > 0 || playerLog.getKillCount() > 0) {
                    clObj.add(key, logWrapper);
                }
            }
            save.add("collectionLog", clObj);

            // --- ACHIEVEMENTS ---
            JsonObject achObj = new JsonObject();
            for (java.util.Map.Entry<String, java.util.List<AchievementTask>> entry : p.getAD().playerAchievementMap.entrySet()) {
                JsonArray completed = new JsonArray();
                for (AchievementTask task : entry.getValue()) {
                    if (task.isDone()) completed.add(new JsonPrimitive(task.getTask()));
                }
                if (completed.size() > 0) achObj.add(entry.getKey(), completed);
            }
            save.add("achievements", achObj);

            // --- FRIENDS ---
            JsonArray friends = new JsonArray();
            for (Long friend : p.getFriends().getFriends()) friends.add(new JsonPrimitive(friend));
            save.add("friends", friends);


            HighscoreSaver.savePlayer(p);
            // --- WRITE THE JSON FILE ---
            File file = new File(getSaveDirectory() + p.playerName + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                builder.toJson(save, writer);
            }

            // --- BACKUP SYSTEM ---
            if (!getSaveDirectory().contains("Dropbox")) {
                File backupDir = new File(Config.CHARACTER_SAVE_DIRECTORY_BACKUP);
                if (!backupDir.exists()) backupDir.mkdirs();
                File destination = new File(backupDir, p.playerName + ".json");
                Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;

        } catch (Exception e) {
            System.err.println("Error saving JSON for " + p.playerName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 3. THE LEGACY FALLBACK METHOD
     * Paste your entire OLD loadGame() method in here so old accounts can still log in
     * and get converted to JSON!
     */
    public static int loadLegacyTextGame(Player p, String playerName, String playerPass) {
        String line = "";
        String token = "";
        String token2 = "";
        String[] token3 = new String[3];
        boolean EndOfFile = false;
        NPC currentNpc = null;
        int ReadMode = 0;
        BufferedReader characterfile = null;
        boolean File1 = false;

        String loadPath = getSaveDirectory() + playerName + ".txt";

        try {
            characterfile = new BufferedReader(new FileReader(loadPath));
            File1 = true;
        } catch (FileNotFoundException fileex1) {
            // Silently handle new players
        }

        if (!File1) {
            Misc.println(playerName + ": character file not found at " + loadPath);
            p.newPlayer = false;
            return 0;
        }

        try {
            line = characterfile.readLine();
        } catch (IOException ioexception) {
            Misc.println(playerName + ": error loading file.");
            return 3;
        }
        while (EndOfFile == false && line != null) {
            line = line.trim();
            int spot = line.indexOf("=");
            if (spot > -1) {
                token = line.substring(0, spot);
                token = token.trim();
                token2 = line.substring(spot + 1);
                token2 = token2.trim();
                token3 = token2.split("\t");
                switch (ReadMode) {
                    case 1:
                        if (token.equals("character-password")) {
                            if (playerPass.equalsIgnoreCase(token2) || Misc.basicEncrypt(playerPass).equals(token2) || Misc.md5Hash(playerPass).equals(token2)) {
                                playerPass = token2;
                            } else {
                                return 3;
                            }
                        }
                        break;
                    case 2:
                        if (token.equals("character-height")) {
                            p.setHeight(Integer.parseInt(token2));
                        } if (token.equals("character-hp")) {
                        p.getHealth().setAmount(Integer.parseInt(token2));
                        if (p.getHealth().getAmount() <= 0) {
                            p.getHealth().setAmount(10);
                        }
                    } else if (token.equals("email")) {
                        p.setEmail(token2);
                    } else if (token.equals("character-posx")) {
                        p.setX(Integer.parseInt(token2) <= 0 ? 3210
                                : Integer.parseInt(token2));
                    } else if (token.equals("character-posy")) {
                        p.setY(Integer.parseInt(token2) <= 0 ? 3424
                                : Integer.parseInt(token2));
                    } else if (token.equals("character-rights")) {
                        p.playerRights = Integer.parseInt(token2);
                    } else if (token.equals("tutorial-progress")) {
                        p.tutorialProgress = Integer.parseInt(token2);
                    } else if (token.equals("EleWorkshop-stage")) {
                        p.EleWorkshopWater_stage = Integer.parseInt(token2);
                    } else if (token.equals("quest-stages")) {
                        String[] stages = token2.split("\t");
                        for (int i = 0; i < stages.length; i++) {
                            if (i < p.questStages.length) {
                                p.questStages[i] = Integer.parseInt(stages[i]);
                            }
                        }
                    } else if (token.equals("rumInCrate")) {
                        p.rumInCrate = Boolean.parseBoolean(token2);
                    } else if (token.equals("bananasInCrate")) {
                        p.bananasInCrate = Integer.parseInt(token2);
                    } else if (token.equals("questPoints")) {
                        p.questPoints = Integer.parseInt(token2);
                    } else if (token.equals("woolHandedIn")) {
                        p.woolHandedIn = Integer.parseInt(token2);
                    }else if (token.equals("crystal-bow-shots")) {
                        p.crystalBowArrowCount = Integer.parseInt(token2);
                    } else if (token.equals("skull-timer")) {
                        p.skullTimer = Integer.parseInt(token2);
                    } else if (token.equals("magic-book")) {
                        p.playerMagicBook = Integer.parseInt(token2);
                    } else if (token.equals("brother-info")) {
                        p.barrowsNpcs[Integer.parseInt(token3[0])][1] = Integer
                                .parseInt(token3[1]);
                    } else if (token.equals("special-amount")) {
                        p.specAmount = Double.parseDouble(token2);
                    } else if (token.equals("selected-coffin")) {
                        p.randomCoffin = Integer.parseInt(token2);
                    } else if (token.equals("teleblock-length")) {
                        p.teleBlockDelay = System.currentTimeMillis();
                        p.teleBlockLength = Integer.parseInt(token2);
                    } else if (token.equals("stash-built")) {
                        String[] splits = token2.split(",");
                        for (int i = 0; i < Math.min(splits.length, p.stashBuilt.length); i++) {
                            p.stashBuilt[i] = Boolean.parseBoolean(splits[i]);
                        }
                    } else if (token.equals("stash-filled")) {
                        String[] splits = token2.split(",");
                        for (int i = 0; i < Math.min(splits.length, p.stashFilled.length); i++) {
                            p.stashFilled[i] = Boolean.parseBoolean(splits[i]);
                        }
                    } else if (token.equals("beginner-clue-step")) {
                        p.currentBeginnerClueStep = Integer.parseInt(token2);
                    }  else if (token.equals("pc-points")) {
                        p.pcPoints = Integer.parseInt(token2);
                    } else if (token.equals("slayerTask")) {
                        p.slayerTask = Integer.parseInt(token2);
                    } else if (token.equals("taskAmount")) {
                        p.taskAmount = Integer.parseInt(token2);
                    } else if (token.equals("magePoints")) {
                        p.magePoints = Integer.parseInt(token2);
                    } else if (token.equals("autoRet")) {
                        p.autoRet = Integer.parseInt(token2);
                    } else if (token.equals("barrowskillcount")) {
                        p.barrowsKillCount = Integer.parseInt(token2);
                    } else if (token.equals("flagged")) {
                        p.accountFlagged = Boolean.parseBoolean(token2);
                    } else if (token.equals("emoteUnlock")) {
                        for (int i = 0; i < p.emoteUnlock.length && i < token2.length(); i++) {
                            p.emoteUnlock[i] = token2.charAt(i) == '1';
                        }
                    } // Loading logic
                    else if (token.equals("RoofTopProgress")) {
                        String[] parts = token2.split("\t");
                        if (parts.length == 3) {
                            int row = Integer.parseInt(parts[0]);
                            int col = Integer.parseInt(parts[1]);
                            int val = Integer.parseInt(parts[2]);

                            if (row < p.getAgilityHandler().RoofAgilityProgress.length &&
                                    col < p.getAgilityHandler().RoofAgilityProgress[row].length) {
                                p.getAgilityHandler().RoofAgilityProgress[row][col] = (val == 1);
                            }
                        }
                    } else if (token.equals("wave")) {
                        p.waveId = Integer.parseInt(token2);
                    } else if (token.equals("void")) {
                        for (int j = 0; j < token3.length; j++) {
                            p.voidStatus[j] = Integer.parseInt(token3[j]);
                        }
                    } else if(token.equals("Blowpipe-Charges")) {
                        p.BlowpipeCharges = Integer.parseInt(token2);
                    } else if(token.equals("Blowpipe-Darts")) {
                        p.BlowpipeDarts = Integer.parseInt(token2);
                    } else if(token.equals("Blowpipe-DartType")) {
                        p.DartType = Integer.parseInt(token2);
                    } else if (token.equals("gwkc")) {
                        p.killCount = Integer.parseInt(token2);
                    } else if (token.equals("fightMode")) {
                        p.fightMode = Integer.parseInt(token2);
                    } else if (token.equals("days-since-recov")) {
                        p.daysSinceRecovChange = Integer.parseInt(token2);
                    } else if (token.equals("unread-messages")) {
                        p.unreadMessages = Integer.parseInt(token2);
                    } else if (token.equals("last-login-date")) {
                        p.lastLoginDate = Long.parseLong(token2);
                    } else if (token.equals("account-creation-date")) {
                        p.accountCreationDate = Long.parseLong(token2);
                    } else if (token.equals("days-last-login")) {
                        p.daysSinceLastLogin = Integer.parseInt(token2);
                    } else if (token.equals("email-registered")) {
                        p.isEmailRegistered = Boolean.parseBoolean(token2);
                    } else if (token.equals("payDirtSackAmt")) {
                        p.payDirtSackAmt = Integer.parseInt(token2);
                        p.setPayDirtSack(new ArrayList<>());
                    } else if (token.equals("payDirtSack")) {
                        String[] parts = token2.split(",");
                        int id = Integer.parseInt(parts[0]);
                        int amt = Integer.parseInt(parts[1]);
                        p.getPayDirtSack().add(new OreStack(id, amt));
                    } else if (token.equals("payDirtPendingAmt")) {
                        p.payDirtPending = new ArrayList<>();
                    } else if (token.equals("payDirtPending")) {
                        String[] parts = token2.split(",");
                        int id = Integer.parseInt(parts[0]);
                        int amt = Integer.parseInt(parts[1]);
                        p.payDirtPending.add(new OreStack(id, amt));
                    }
                    else if (token.equals("npcPayDirtId")) {
                        int npcIndex = Integer.parseInt(token2);
                        if(npcIndex == 6564)
                            MotherlodeMine.spawnPayDirtNpc(p);
                        currentNpc = p.getNpcByIndex(npcIndex);
                        if (currentNpc != null) {
                            currentNpc.payDirtPending = new ArrayList<>();
                            p.npcsWithPayDirt.add(currentNpc);
                        }
                    }
                    else if (token.equals("npcPayDirtAmt")) {
                        // number of ores pending
                    }
                    else if (token.equals("npcPayDirt")) {
                        currentNpc = p.getNpcByIndex(6564);
                        if (currentNpc != null) {
                            String[] parts = token2.split(",");
                            int id = Integer.parseInt(parts[0]);
                            int amt = Integer.parseInt(parts[1]);
                            currentNpc.payDirtPending.add(new OreStack(id, amt));
                        }
                    }
                    else if (token.equals("bookpage")) {
                        int bookId = Integer.parseInt(token3[0]);
                        int pageId = Integer.parseInt(token3[1]);
                        GodBookManager.markPageAdded(p, bookId, pageId);
                    }
                    else if (token.equals("NightmareZone")) {
                        int coffer = Integer.parseInt(token3[0]);
                        int points = Integer.parseInt(token3[1]);
                        int overloads = Integer.parseInt(token3[2]);
                        int absorbs = Integer.parseInt(token3[3]);
                        int superMagic = Integer.parseInt(token3[4]);
                        int superRange = Integer.parseInt(token3[5]);
                        p.nmzCoffer = coffer;
                        p.nmzPoints = points;
                        p.nmzOverloadDoses = overloads;
                        p.nmzAbsorptionDoses = absorbs;
                        p.nmzSuperMagicDoses = superMagic;
                        p.nmzSuperRangingDoses = superRange;
                    }
                        break;
                    case 3:
                        if (token.equals("character-equip")) {
                            p.playerEquipment[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[1]);
                            p.playerEquipmentN[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[2]);
                        }
                        break;
                    case 4:
                        if (token.equals("character-look")) {
                            p.playerAppearance[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[1]);
                        }
                        break;
                    case 5:
                        if (token.equals("character-skill")) {
                            Skill skill = Skill.forId(Integer.parseInt(token3[0]));
                            if(skill == null)
                                break;
                            p.getSkills().setLevel( Integer.parseInt(token3[1]), skill );
                            p.getSkills().setExperience(Integer.parseInt(token3[2]), skill);
                        }
                        break;
                    case 6:
                        if (token.equals("character-item")) {
                            p.playerItems[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[1]);
                            p.playerItemsN[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[2]);
                        }
                        break;
                    case 7:
                        if (token.equals("character-item")) {
                            p.playerLootItems[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[1]);
                            p.playerLootItemsN[Integer.parseInt(token3[0])] = Integer
                                    .parseInt(token3[2]);
                        }
                        break;

                    case 52:
                        if (token.equals("item")) {
                            int itemId = Integer.parseInt(token3[0]);
                            int value = Integer.parseInt(token3[1]);
                            String date = token3[2];
                            p.getRechargeItems().loadItem(itemId, value, date);
                        }
                        break;
                    case 55:
                        if (token.equals("pouch-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getRunePouch().getItems().add(new Item(id, amt));
                        }
                        break;
                    case 56:
                        if (token.equals("sack-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getHerbSack().getItems().add(new Item(id, amt));
                        }
                        break;
                    case 57:
                        if (token.equals("bag-item")) {
                            int id = Integer.parseInt(token3[1]);
                            int amt = Integer.parseInt(token3[2]);
                            p.getGemBag().getItems().add(new Item(id, amt));
                        }
                        break;
                    case 8:
                        if (token.equals("character-bank")) {
                            p.bankItems[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                            p.bankItemsN[Integer.parseInt(token3[0])] = Integer.parseInt(token3[2]);
                            p.getBank().getBankTab()[0].add(new BankItem(Integer.parseInt(token3[1]), Integer.parseInt(token3[2])));
                        } else if (token.equals("bank-tab")) {
                            int tabId = Integer.parseInt(token3[0]);
                            int itemId = Integer.parseInt(token3[1]);
                            int itemAmount = Integer.parseInt(token3[2]);
                            p.getBank().getBankTab()[tabId].add(new BankItem(itemId, itemAmount));
                        }
                        break;

                    case 9:
                        if (token.equals("character-friend")) {
                            //p.getFriends().add(Long.parseLong(token3[0]));
                        }
                        break;
                    case 10:
                        break;
                    case 11:
                        if (token.endsWith("-item")) {
                            String logKey = token.replace("-item", "");
                            CollectionLogData logData = p.getCollectionLog().getLogBySaveKey(logKey);

                            if (logData != null) {
                                int index = Integer.parseInt(token3[0]);
                                int amount = Integer.parseInt(token3[2]);
                                logData.setAmount(index, amount);
                            }
                        }
                        break;
                    case 12:
                        if (token.startsWith("Task - ")) {
                            String regionAndTask = token.substring(7);
                            String[] regionAndTaskParts = regionAndTask.split(" - ");

                            if (regionAndTaskParts.length == 2) {
                                String region = regionAndTaskParts[0].trim();
                                String taskName = regionAndTaskParts[1].trim();
                                boolean isCompleted = Boolean.parseBoolean(token2.trim());

                                List<AchievementTask> tasks = p.getAD().playerAchievementMap.get(region);

                                if (tasks != null) {
                                    for (AchievementTask task : tasks) {
                                        if (task.getTask().equalsIgnoreCase(taskName)) {
                                            task.setDone(isCompleted);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 13:
                        if (token.equals("Herb-State")) {
                            p.getHerbs().herbState[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Herb-Seeds")) {
                            p.getHerbs().herbSeeds[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Herb-Stages")) {
                            p.getHerbs().herbStages[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Herb-Timer")) {
                            p.getHerbs().herbTimer[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        break;
                    case 14:
                        if (token.equals("Flower-State")) {
                            p.getFlowers().flowerState[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Flower-Seeds")) {
                            p.getFlowers().flowerSeeds[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Flower-Stages")) {
                            p.getFlowers().flowerStages[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Flower-Timer")) {
                            p.getFlowers().flowerTimer[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        break;
                    case 15:
                        if (token.equals("Allotment-State")) {
                            p.getAllotment().allotmentState[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Allotment-Seeds")) {
                            p.getAllotment().allotmentSeeds[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Allotment-Stages")) {
                            p.getAllotment().allotmentStages[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        if (token.equals("Allotment-Timer")) {
                            p.getAllotment().allotmentTimer[Integer.parseInt(token3[0])] = Integer.parseInt(token3[1]);
                        }
                        break;

                }
            } else {
                if (line.equals("[ACCOUNT]")) {
                    ReadMode = 1;
                } else if (line.equals("[CHARACTER]")) {
                    ReadMode = 2;
                } else if (line.equals("[EQUIPMENT]")) {
                    ReadMode = 3;
                } else if (line.equals("[LOOK]")) {
                    ReadMode = 4;
                } else if (line.equals("[SKILLS]")) {
                    ReadMode = 5;
                } else if (line.equals("[ITEMS]")) {
                    ReadMode = 6;
                } else if (line.equals("[LOOTBAG]")) {
                    ReadMode = 7;
                } else if (line.equals("[RECHARGEITEMS]")) {
                    ReadMode = 52;
                } else if (line.equals("[RUNEPOUCH]")) {
                    ReadMode = 55;
                } else if (line.equals("[HERBSACK]")) {
                    ReadMode = 56;
                } else if (line.equals("[GEMBAG]")) {
                    ReadMode = 57;
                }else if (line.equals("[BANK]")) {
                    ReadMode = 8;
                } else if (line.equals("[FRIENDS]")) {
                    ReadMode = 9;
                } else if (line.equals("[IGNORES]")) {
                    ReadMode = 10;
                } else if (line.equals("[COLLECTLOG]")) {
                    ReadMode = 11;
                } else if (line.equals("[TASKS]")) {
                    ReadMode = 12;
                } else if (line.equals("[HERBS]")) {
                    ReadMode = 13;
                } else if (line.equals("[FLOWERS]")) {
                    ReadMode = 14;
                } else if (line.equals("[ALLOTMENTS]")) {
                    ReadMode = 15;
                } else if (line.equals("[EOF]")) {
                    try {
                        characterfile.close();
                    } catch (IOException ioexception) {
                    }
                    return 1;
                }
            }
            try {
                line = characterfile.readLine();
            } catch (IOException ioexception1) {
                EndOfFile = true;
            }
        }
        try {
            characterfile.close();
        } catch (IOException ioexception) {
        }
        return 13;
    }
    public static boolean isFriend(String name, String friend) {
        long friendLong = Misc.playerNameToInt64(friend);

        File jsonFile = new File(getSaveDirectory() + name + ".json");
        File txtFile = new File(getSaveDirectory() + name + ".txt");

        // --- 1. TRY JSON FORMAT FIRST ---
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader(jsonFile)) {
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                com.google.gson.JsonObject save = parser.parse(reader).getAsJsonObject();

                if (save.has("friends")) {
                    com.google.gson.JsonArray friendsArray = save.get("friends").getAsJsonArray();
                    for (int i = 0; i < friendsArray.size(); i++) {
                        if (friendsArray.get(i).getAsLong() == friendLong) {
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading friends from JSON for: " + name);
            }
            return false;
        }

        // --- 2. FALLBACK TO LEGACY TXT FORMAT ---
        if (txtFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("character-friend =")) {
                        try {
                            long readFriend = Long.parseLong(line.split("=")[1].trim());
                            if (readFriend == friendLong) {
                                return true;
                            }
                        } catch (Exception e) {
                            // Ignore malformed lines
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading friends from TXT for: " + name);
            }
        }

        return false;
    }
    public static boolean playerExists(String name) {
        File jsonFile = new File(getSaveDirectory() + name + ".json");
        File txtFile = new File(getSaveDirectory() + name + ".txt");
        return jsonFile.exists() || txtFile.exists();
    }
}