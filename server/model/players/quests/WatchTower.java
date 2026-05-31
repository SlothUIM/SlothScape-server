package server.model.players.quests;

	import server.model.players.Client;
import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;;

	public class WatchTower extends QuestManager {
	    public static final int QUEST_ID = 20;
	    public static final String QUEST_NAME = "Watchtower";
	    public static final int NOT_STARTED = 0, STARTED = 1, FINGERNAILS_FOUND = 2, MAP_DISCOVERED = 3, RELICS_GIVEN = 4,
	            POTION_MADE = 5, POTION_ENCHANTED = 6, CRYSTALS_FOUND = 7, ACTIVATED = 8, COMPLETED = 9;

	    private int stage = 0;

	    public WatchTower(Player c) {
	        super(QUEST_NAME, c);
	        this.stage = NOT_STARTED;
	    }

	    @Override
	    public int getCurrentStage() {
	        // Use the actual variable stored on the player object
	        return c.questStages[QUEST_ID]; 
	    }

	    @Override
	    public void setStage(int stage) {
	        // Save the progress directly to the player
	    	c.questStages[QUEST_ID] = stage;
	        this.stage = c.questStages[QUEST_ID];
	        // Debug: This will show you in the console if the stage actually changes
	        System.out.println("Watchtower Quest Stage set to: " + stage);
	        QuestAssistant.sendStages(c);
	    }

	    @Override
	    public int getTotalStages() {
	        return 10;
	    }

	    @Override
	    public boolean hasRequirements() {
	        return true;
	    }

	    @Override
	    public void giveRewards() {
	        // Example: player.addExperience(Skill.MAGIC, 15250);
	        // player.unlockSpell("Watchtower Teleport");
	    }

	    @Override
	    public boolean isCompleted() {
	        return stage == COMPLETED;
	    }

	    @Override
	    public boolean isStarted() {
	        return stage > NOT_STARTED;
	    }

	    @Override
	    public boolean isRepeatable() {
	        return false;
	    }

	    @Override
	    public String getQuestJournalEntry() {
	        switch (stage) {
	            case NOT_STARTED:
	                return "I can start this quest by speaking to the Watchtower Wizard at the Watchtower north of Yanille.";
	            case STARTED:
	                return "The Watchtower Wizard has asked me to search for clues about the missing crystals.";
	            case FINGERNAILS_FOUND:
	                return "I found strange fingernails. The Wizard believes they're from a Skavid. He suggested seeking a map from ogres in Gu'Tanoth.";
	            case MAP_DISCOVERED:
	                return "I have a map to the Skavid caves. I should explore them and investigate for clues.";
	            case RELICS_GIVEN:
	                return "I've collected the ogre relic pieces. The Wizard assembled them into a statue for me. I should use this to befriend the ogres and find a way into Gu'Tanoth.";
	            case POTION_MADE:
	                return "I need to bring the potion (guam leaf, jangerberries, ground bat bones) to the Wizard for enchanting.";
	            case POTION_ENCHANTED:
	                return "The Wizard has enchanted my potion. I can now fight the ogre shamans.";
	            case CRYSTALS_FOUND:
	                return "I possess all four crystals. The Wizard said to put them on the pillars and throw the lever.";
	            case ACTIVATED:
	                return "I've activated the Watchtower. The Wizard will want to know!";
	            case COMPLETED:
	                return "Quest complete! Yanille is now safe from ogres.";
	            default:
	                return "";
	        }
	    }

	    @Override
	    public void showQuestScroll(Player c) {
	        for (int i = 8144; i < 8195; i++) {
	            c.getPA().sendFrame126("", i);
	        }
	        c.getPA().showInterface(8134);

	        c.getPA().sendFrame126("@dre@Watchtower", 8144);
	        int line = 8145;
	        stage = c.questStages[QUEST_ID];
	        switch (stage) {
	            case NOT_STARTED:
	                c.getPA().sendFrame126("I can start this quest by speaking to the", line++);
	                c.getPA().sendFrame126("Watchtower Wizard at the Watchtower north of Yanille.", line++);
	                c.getPA().sendFrame126("", line++);
	                c.getPA().sendFrame126("There are no requirements.", line++);
	                break;
	            case STARTED:
	                c.getPA().sendFrame126("The Watchtower Wizard has asked me to search", line++);
	                c.getPA().sendFrame126("for clues about the missing crystals.", line++);
	                break;
	            case FINGERNAILS_FOUND:
	                c.getPA().sendFrame126("I found strange fingernails. The Wizard believes", line++);
	                c.getPA().sendFrame126("they're from a Skavid. He suggested seeking a map", line++);
	                c.getPA().sendFrame126("from ogres in Gu'Tanoth.", line++);
	                break;
	            case MAP_DISCOVERED:
	                c.getPA().sendFrame126("I have a map to the Skavid caves. I should explore", line++);
	                c.getPA().sendFrame126("them and investigate for clues.", line++);
	                break;
	            case RELICS_GIVEN:
	                c.getPA().sendFrame126("I've collected the ogre relic pieces. The Wizard", line++);
	                c.getPA().sendFrame126("assembled them into a statue for me. I should use", line++);
	                c.getPA().sendFrame126("this to befriend the ogres and find a way into Gu'Tanoth.", line++);
	                break;
	            case POTION_MADE:
	                c.getPA().sendFrame126("I need to bring the potion (guam leaf, jangerberries,", line++);
	                c.getPA().sendFrame126("ground bat bones) to the Wizard for enchanting.", line++);
	                break;
	            case POTION_ENCHANTED:
	                c.getPA().sendFrame126("The Wizard has enchanted my potion. I can now fight", line++);
	                c.getPA().sendFrame126("the ogre shamans.", line++);
	                break;
	            case CRYSTALS_FOUND:
	                c.getPA().sendFrame126("I possess all four crystals. The Wizard said to put", line++);
	                c.getPA().sendFrame126("them on the pillars and throw the lever.", line++);
	                break;
	            case ACTIVATED:
	                c.getPA().sendFrame126("I've activated the Watchtower. The Wizard will want to know!", line++);
	                break;
	            case COMPLETED:
	                c.getPA().sendFrame126("@gre@QUEST COMPLETE!", line++);
	                c.getPA().sendFrame126("", line++);
	                c.getPA().sendFrame126("I have restored the Watchtower's power and", line++);
	                c.getPA().sendFrame126("helped save Yanille from the ogres.", line++);
	                c.getPA().sendFrame126("", line++);
	                c.getPA().sendFrame126("Rewards:", line++);
	                c.getPA().sendFrame126("4 Quest Points", line++);
	                c.getPA().sendFrame126("15,250 Magic XP", line++);
	                c.getPA().sendFrame126("The Watchtower teleport spell", line++);
	                break;
	        }
	    }

	    /**
	     * Handles the dialogue for the quest, with each case containing only one action or option.
	     */
	    @Override
	    public void handleDialogue(Player c, int dialogueId) {
	        switch (dialogueId) {
	            case 7000:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "Who are you? Are you one of the new guards?");
	                c.nextChat = 7001;
	                break;
	            case 7001:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "No, I'm an adventurer.");
	                c.nextChat = 7002;
	                break;
	            case 7002:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "Well what are you doing here?");
	                c.nextChat = 7003;
	                break;
	            case 7003:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "Looking for adventures - what else?");
	                c.nextChat = 7004;
	                break;
	            case 7004:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "Oh my, oh my! What does it all matter in the end, anyway?");
	                c.nextChat = 7005;
	                break;
	            case 7005:
	                c.getDH().sendOptions("What's the matter?", "You wizards are always complaining.");
	                c.dialogueAction = 701;
	                break;
	            case 7006:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "What's the matter?");
	                c.nextChat = 7007;
	                break;
	            case 7007:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "Oh dear, oh dear. Darn and drat!");
	                c.nextChat = 7008;
	                break;
	            case 7008:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "We try hard to keep this town protected.");
	                c.nextChat = 7009;
	                break;
	            case 7009:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "But how can we do that when the Watchtower isn't working?");
	                c.nextChat = 7010;
	                break;
	            case 7010:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "What do you mean it isn't working?");
	                c.nextChat = 7011;
	                break;
	            case 7011:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "The Watchtower over here works by the power of magic.",
	                    " An ancient spell designed to ward off ogres that"," has been in place here for many moons.");
	                c.nextChat = 7012;
	                break;
	            case 7012:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "The exact knowledge of the spell is lost to us now, but the essence of",
	                    "the spell has been infused into four powering crystals that keep the tower",
	                    "protected from the hordes in the Feldips.");
	                c.nextChat = 7013;
	                break;
	            case 7013:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "So how come the spell doesn't work?");
	                c.nextChat = 7014;
	                break;
	            case 7014:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "The crystals! The crystals have been taken!");
	                c.nextChat = 7015;
	                break;
	            case 7015:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "Taken?");
	                c.nextChat = 7016;
	                break;
	            case 7016:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.SAD.getAnimationId(),
	                    "Stolen!");
	                c.nextChat = 7017;
	                break;
	            case 7017:
	                c.getDH().playerChat(Anim.ANNOYED.getAnimationId(), "Stolen?");
	                c.nextChat = 7018;
	                break;
	            case 7018:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.ANNOYED.getAnimationId(),
	                    "Yes, yes! Do I have to repeat myself?");
	                c.nextChat = 7019;
	                break;
	            case 7019:
	                c.getDH().sendOptions("Can I be of help?", "I'm not interested.", "I'm not interested in your rantings.", "You wizards are always complaining.");
	                c.dialogueAction = 702;
	                break;
	            case 7020:
	                c.getDH().playerChat(Anim.CALM_1.getAnimationId(), "Can I be of help?");
	                c.nextChat = 7021;
	                break;
	            case 7021:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.HAPPY.getAnimationId(),
	                    "Help? Oh wonderful, dear traveller!");
	                c.nextChat = 7022;
	                break;
	            case 7022:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.HAPPY.getAnimationId(),
	                    "Yes I could do with an extra pair of eyes here.");
	                c.nextChat = 7023;
	                break;
	            case 7023:
	                c.getDH().playerChat(Anim.CONFUSED.getAnimationId(), "???");
	                c.nextChat = 7024;
	                break;
	            case 7024:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "There must be some evidence of what has happened here."," Perhaps you could assist me in searching for clues?");
	                c.nextChat = 7025;
	                break;
	            case 7025:
	                c.getDH().playerChat(Anim.HAPPY.getAnimationId(), "I would be happy to.");
	                c.nextChat = 7026;
	                break;
	            case 7026:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.CALM_1.getAnimationId(),
	                    "Try searching the surrounding area. If you find anything",
	                    " unusual, bring it here. Try the bushes - I've read ",
	                    "enough adventure stories to know that clues get caught",
	                    " in bushes all the time.");
	                c.nextChat = 7027;
	                break;
	            case 7027: 
	            	setStage(STARTED);
	                
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.HAPPY.getAnimationId(),
	                    "I will tell the guards to let you past - that way,"," you can just use the ladder to get in and out.");
	               c.nextChat = -1;
	                break;
	            case 7030:
	                c.getDH().playerChat(Anim.ANNOYED.getAnimationId(), "I'm not interested.");
	                c.nextChat = 7031;
	                break;
	            case 7031:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.ANNOYED.getAnimationId(),
	                    "That's typical, nowadays. It's left to us wizards to do all the work.");
	                c.nextChat = -1;
	                break;
	            case 7032:
	                c.getDH().playerChat(Anim.ANNOYED.getAnimationId(), "I'm not interested in your rantings.");
	                c.nextChat = 7033;
	                break;
	            case 7033:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.ANNOYED.getAnimationId(),
	                    "Hmph! Suit yourself.");
	                c.nextChat = -1;
	                break;
	            case 7034:
	                c.getDH().playerChat(Anim.ANNOYED.getAnimationId(), "You wizards are always complaining.");
	                c.nextChat = 7035;
	                break;
	            case 7035:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.ANNOYED.getAnimationId(),
	                    "Complaining? Complaining!");
	                c.nextChat = 7036;
	                break;
	            case 7036:
	                c.getDH().npcChat(c.talkingNpc, "Watchtower Wizard", Anim.ANNOYED.getAnimationId(),
	                    "What folks these days don't realise is that if it weren't for us wizards, this entire world would be overrun with every creature you could possibly imagine. And some you couldn't even conceive of!");
	                c.nextChat = -1;
	                break;
	        }
			QuestAssistant.sendStages(c);
	    }

	    @Override
	    public boolean handleDialogueAction(Player c, int actionButtonId) {
	        switch (c.dialogueAction) {
	            case 701:
	                if (actionButtonId == 9157) { // "What's the matter?"
	                    handleDialogue(c, 7006);
	                } else if (actionButtonId == 9158) { // "You wizards are always complaining."
	                    handleDialogue(c, 7034);
	                }
	                return true;
	            case 702:
	                if (actionButtonId == 9178) { // "Can I be of help?"
	                    handleDialogue(c, 7020);
	                } else if (actionButtonId == 9179) { // "I'm not interested."
	                    handleDialogue(c, 7030);
	                } else if (actionButtonId == 9180) { // "I'm not interested in your rantings."
	                    handleDialogue(c, 7032);
	                } else if (actionButtonId == 9181) { // "You wizards are always complaining."
	                    handleDialogue(c, 7034);
	                }
	                return true;
	        }
	        return false;
	    }
	
	public void showInformation(Player player) {
		for (int i = 8144; i < 8295; i++) {
			player.getPA().sendFrame126("", i);
		}
		player.getPA().sendFrame126("@dre@Imp Catcher", 8144);
		player.getPA().sendFrame126("", 8145);
		if (getCurrentStage() == 0) {
			player.getPA().sendFrame126( "I can start this quest by talking to  the Watchtower Wizard in the", 8147);
			player.getPA().sendFrame126("Yanille Watchtower.", 8148);
			player.getPA().sendFrame126("___________________________________________________", 8149);
			player.getPA().sendFrame126("Difficulty:                                    Length:", 8150);
			player.getPA().sendFrame126("@red@Intermediate                               Medium", 8151);
			player.getPA().sendFrame126("     Storyline:                                     Release Year:  ", 8152);
			player.getPA().sendFrame126("@red@Standalone:                                           2003      ", 8153);
			player.getPA().sendFrame126("___________________________________________________", 8154);
		} else if (player.questStages[QUEST_ID] == 1) {
			player.getPA().sendFrame126("@str@I can start this quest by speaking to Wizard Mizgog who is", 8147);
			player.getPA().sendFrame126("@str@in the Wizard's Tower.", 8148);
			player.getPA().sendFrame126("", 8149);
			player.getPA().sendFrame126("Wizard Mizgog have asked you to get the following items:", 8150);
			player.getPA().sendFrame126("Red bead", 8151);
			player.getPA().sendFrame126("Yellow bead", 8152);
			player.getPA().sendFrame126("Black bead", 8153);
			player.getPA().sendFrame126("White bead", 8154);
		} else if (player.questStages[QUEST_ID] == 2) {
			player.getPA().sendFrame126("@str@I can start this quest by speaking to Wizard Mizgog who is", 8147);
			player.getPA().sendFrame126("@str@in the Wizard's Tower.", 8148);
			player.getPA().sendFrame126("", 8149);
			player.getPA().sendFrame126("@str@Wizard Mizgog have asked you to get the following items:", 8150);
			player.getPA().sendFrame126("@str@Red bead", 8151);
			player.getPA().sendFrame126("@str@Yellow bead", 8152);
			player.getPA().sendFrame126("@str@Black bead", 8153);
			player.getPA().sendFrame126("@str@White bead", 8154);
			player.getPA().sendFrame126("", 8155);
			player.getPA().sendFrame126("You have completed this quest!", 8156);
		}
		player.getPA().showInterface(8134);
	}
}
