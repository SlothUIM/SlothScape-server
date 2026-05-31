package server.model.players.packets.dialogue.impl; // Adjust package as needed

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;
import server.model.players.skills.Skill;
// Import your Anim enum here

public class ProspectorPercy extends NPCDialogue {

    private static final int PERCY = 6562;
    private static final String NAME = "Prospector Percy";

    public ProspectorPercy(Player c) {
        super(c);
    }

    @Override
    public int getNPCID() {
        return PERCY;
    }

    @Override
    public String getDialogueRange() {
        return "40000-40100";
    }

    @Override
    public void dialogue(Player c, int npcId, int dialogueId) {
        switch (dialogueId) {
            case 40001:
                npc(c, NAME, Anim.ANGRY,
                        "Git back ter work, ye young varmint! There's treasure",
                        "in them walls, and it's not gonna mine itself while",
                        "ye stand here yappin'.");
                c.nextChat = 40002;
                break;

            case 40002:
                options_with_title(c, 4001, "What would you like to say?",
                        "How do I mine here?",
                        "Would you like to trade?",
                        "Tell me about yourself.",
                        "Is there anything else I can unlock here?",
                        "I'll leave you alone.");
                break;

            // --- BRANCH 1: HOW DO I MINE HERE? ---
            case 40010:
                player(c, Anim.CALM_1, "How do I mine here?");
                c.nextChat = 40011;
                break;
            case 40011:
                npc(c, NAME, Anim.CALM_1,
                        "Git ahold of yer pickaxe, find a vein of ore, and set",
                        "to work. If ye got a bit of skill, ye'll have a pocket o'",
                        "pay-dirt in no time.");
                c.nextChat = 40012;
                break;
            case 40012:
                npc(c, NAME, Anim.CALM_1,
                        "I've built me a contraption to wash the pay-dirt.",
                        "Just drop yer pay-dirt in the hopper, an' wait fer it",
                        "at the other end.");
                c.nextChat = 40013;
                break;
            case 40013:
                npc(c, NAME, Anim.CALM_1,
                        "I won't charge ye fer usin' my contraption, but",
                        "ye'd better fix it yerself when it breaks. A good",
                        "whack with a hammer usually settles it.");
                c.nextChat = 40014;
                break;
            case 40014:
                npc(c, NAME, Anim.CALM_1,
                        "Now will ye be gettin' to work now, or are ye gonna",
                        "keep yappin' like a doggone galoot?");
                c.nextChat = 40002; // Return to main options
                break;

            // --- BRANCH 2: TRADE ---
            case 40020:
                player(c, Anim.CALM_1, "Would you like to trade?");
                c.nextChat = 40021;
                break;
            case 40021:
                npc(c, NAME, Anim.CALM_1,
                        "If ye've found yerself some golden nuggets in this",
                        "'ere mine, I'll do you a swap, yeah.");
                c.nextChat = 40022;
                break;
            case 40022:
                // TODO: Hook to open your Nugget Shop
                // c.getShops().openShop(150);
                end(c);
                break;

            // --- BRANCH 3: TELL ME ABOUT YOURSELF ---
            case 40030:
                player(c, Anim.CALM_1, "Tell me about yourself.");
                c.nextChat = 40031;
                break;
            case 40031:
                npc(c, NAME, Anim.CALM_1,
                        "Why, I'm Percy. Prospector Percy, the roughest,",
                        "toughest, gruffest miner in the land. I've been",
                        "pannin' fer gold since I were a young-un, and",
                        "here's where I've struck it lucky.");
                c.nextChat = 40032;
                break;
            case 40032:
                options_with_title(c, 4002, "What would you like to say?",
                        "Excuse me, but what language are you speaking?",
                        "You discovered this mine?",
                        "Nevermind.");
                break;

            case 40033:
                player(c, Anim.CALM_1, "Excuse me, but what language are you speaking?");
                c.nextChat = 40034;
                break;
            case 40034:
                npc(c, NAME, Anim.ANGRY,
                        "Don't ye give me any of yer lip, ye dern varmint!",
                        "Young'uns these days got no respect.");
                c.nextChat = 40035;
                break;
            case 40035:
                player(c, Anim.CALM_1, "Do go on.");
                c.nextChat = 40037;
                break;

            case 40036:
                player(c, Anim.CALM_1, "You discovered this mine?");
                c.nextChat = 40037;
                break;
            case 40037:
                npc(c, NAME, Anim.CALM_1,
                        "This here's the richest seam of ore I've found in all",
                        "my days. After I built a contraption for washing the",
                        "pay-dirt, the dwarves let me run things down here.");
                c.nextChat = 40038;
                break;
            case 40038:
                npc(c, NAME, Anim.CALM_1,
                        "Now, have ye any more idjit questions, or are ye",
                        "ready to do some real work?");
                c.nextChat = 40002; // Back to main menu
                break;

            // --- BRANCH 4: UNLOCKS (Hooked to Player & Items) ---
            case 40040:
                player(c, Anim.CALM_1, "Is there anything else I can unlock here?");
                c.nextChat = 40041;
                break;
            case 40041:
                // Note: Ensure these exist in Player.java and save to your character files!
                boolean hasUpper = c.unlockedRestrictedMine;
                boolean hasSack = c.unlockedBigSack;
                boolean hasHopper = c.unlockedSuperHopper;

                if (hasUpper && hasSack && hasHopper) {
                    npc(c, NAME, Anim.CALM_1,
                            "Well, now, I think ye've got everything already.",
                            "Ye can already go climb the ladder to the restricted",
                            "mine, ye've got the bigger sack capacity too and ye",
                            "have access to that restricted hopper.");
                    c.nextChat = 40002; // Loop back
                } else if (hasUpper && hasSack && !hasHopper) {
                    npc(c, NAME, Anim.CALM_1,
                            "I can give ye access to a special hopper in the",
                            "restricted mine for 50 nuggets, it connects to the",
                            "hopper down here.");
                    c.nextChat = 40045; // Hopper only options
                } else if (hasUpper && !hasSack) {
                    npc(c, NAME, Anim.CALM_1,
                            "Ye can already climb the ladder to the restricted mine.",
                            "But how would ye like it if the sack were bigger?",
                            "I'll let ye have more ore in there if ye pays me 200", "nuggets.");
                    c.nextChat = 40044; // Sack only options
                } else {
                    npc(c, NAME, Anim.CALM_1, "Let me have a think...");
                    c.nextChat = 40042;
                }
                break;
            case 40042:
                npc(c, NAME, Anim.CALM_1,
                        "If ye've got level 57 Mining, ye could pay to use",
                        "my restricted mine, up the ladder. 100 nuggets",
                        "gives unlimited access.");
                c.nextChat = 40043;
                break;
            case 40043:
                options_with_title(c, 4003, "Select an Option",
                        "Restricted mine access: 100 nuggets",
                        "Bigger sack: 200 nuggets",
                        "Cancel");
                break;
            case 40044:
                options_with_title(c, 4004, "Select an Option",
                        "Bigger sack: 200 nuggets",
                        "Cancel");
                break;
            case 40045:
                options_with_title(c, 4005, "Select an Option",
                        "Restricted hopper: 50 nuggets",
                        "Cancel");
                break;

            // Unlock: Upper Level
            case 40060:
                player(c, Anim.CALM_1, "I'd like to buy access to the restricted mine, please.");
                c.nextChat = 40061;
                break;
            case 40061:
                int currentNuggets = c.getItems().getItemAmount(12012); // Check inventory for Nuggets
                int miningLevel = c.getSkills().getLevel(Skill.MINING); // Check actual level

                if (miningLevel < 57) {
                    npc(c, NAME, Anim.ANGRY,
                            "Ye'll need level 57 Mining first. An' don't think ye",
                            "can fool me with yer potions and fancy stat-boosts.",
                            "Get yer level up for real.");
                    c.nextChat = 40002;
                } else if (currentNuggets < 100) {
                    npc(c, NAME, Anim.ANGRY,
                            "That'll be 100 nuggets. If ye ain't got enough, ye'd",
                            "better do some more mining - that's how ye gets stuff", "round here!");
                    c.nextChat = 40002;
                } else {
                    c.getItems().deleteItem(12012, 100); // Take 100 nuggets
                    c.unlockedRestrictedMine = true;          // Unlock it
                    item(c, 12012, "You pay Percy 100 nuggets.");
                    c.nextChat = 40062;
                }
                break;
            case 40062:
                npc(c, NAME, Anim.CALM_1,
                        "Right, ye can go up there whenever ye likes. Just",
                        "behave yerself, or my Ma will tan yer hide.");
                c.nextChat = 40002;
                break;

            // Unlock: Bigger Sack
            case 40070:
                player(c, Anim.CALM_1, "I'd like to get the bigger sack, please.");
                c.nextChat = 40071;
                break;
            case 40071:
                if (c.getItems().getItemAmount(12012) < 200) {
                    npc(c, NAME, Anim.ANGRY,
                            "That'll be 200 nuggets. If ye ain't got enough, ye'd",
                            "better do some more mining - that's how ye gets stuff", "round here!");
                    c.nextChat = 40002;
                } else {
                    c.getItems().deleteItem(12012, 200); // Take 200 nuggets
                    c.unlockedBigSack = true;         // Unlock it
                    item(c, 12012, "You pay Percy 200 nuggets.");
                    c.nextChat = 40072;
                }
                break;
            case 40072:
                npc(c, NAME, Anim.CALM_1,
                        "There ye go. Yer sack can hold twice as much pay-dirt",
                        "now. Don't go breakin' yer back liftin' it!");
                c.nextChat = 40002;
                break;

            // Unlock: Super Hopper
            case 40080:
                player(c, Anim.CALM_1, "I'd like to get the restricted hopper, please.");
                c.nextChat = 40081;
                break;
            case 40081:
                if (c.getItems().getItemAmount(12012) < 50) {
                    npc(c, NAME, Anim.ANGRY,
                            "That'll be 50 nuggets. If ye ain't got enough, ye'd",
                            "better do some more mining - that's how ye gets stuff", "round here!");
                    c.nextChat = 40002;
                } else {
                    c.getItems().deleteItem(12012, 50); // Take 50 nuggets
                    c.unlockedSuperHopper = true;       // Unlock it
                    item(c, 12012, "You pay Percy 50 nuggets.");
                    c.nextChat = 40082;
                }
                break;
            case 40082:
                npc(c, NAME, Anim.CALM_1,
                        "Ye can now use that fancy new hopper in the",
                        "restricted mine.");
                c.nextChat = 40002;
                break;

            // --- BRANCH 5: LEAVE ---
            case 40090:
                player(c, Anim.CALM_1, "I'll leave you alone.");
                c.nextChat = 40091;
                break;
            case 40091:
                npc(c, NAME, Anim.ANGRY, "Dern straight ye will.");
                end(c); // Closes interfaces and resets values
                break;
        }
    }

    @Override
    public void onOption(Player c, int buttonId) {
        // We route the button clicks based on the 'dialogueAction' set in our options_with_title calls
        switch (c.dialogueAction) {
            case 4001: // Main Menu (5 options)
                if (buttonId == OPT5_FIRST) next(c, 40010);
                else if (buttonId == OPT5_SECOND) next(c, 40020);
                else if (buttonId == OPT5_THIRD) next(c, 40030);
                else if (buttonId == OPT5_FOURTH) next(c, 40040);
                else if (buttonId == OPT5_FIFTH) next(c, 40090);
                break;

            case 4002: // "Tell me about yourself" sub-menu (3 options)
                if (buttonId == OPT3_FIRST) next(c, 40033);
                else if (buttonId == OPT3_SECOND) next(c, 40036);
                else if (buttonId == OPT3_THIRD) next(c, 40002); // Back to main
                break;

            case 4003: // Full Unlocks menu (3 options)
                if (buttonId == OPT3_FIRST) next(c, 40060);
                else if (buttonId == OPT3_SECOND) next(c, 40070);
                else if (buttonId == OPT3_THIRD) next(c, 40002); // Cancel
                break;

            case 4004: // Sack-only menu (2 options)
                if (buttonId == OPT2_FIRST) next(c, 40070);
                else if (buttonId == OPT2_SECOND) next(c, 40002); // Cancel
                break;

            case 4005: // Hopper-only menu (2 options)
                if (buttonId == OPT2_FIRST) next(c, 40080);
                else if (buttonId == OPT2_SECOND) next(c, 40002); // Cancel
                break;
        }
    }
}