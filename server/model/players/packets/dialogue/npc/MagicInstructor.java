package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class MagicInstructor extends NPCDialogue {

    public MagicInstructor(Player c) { super(c); }

    @Override
    public int getNPCID() { return 941; } 
    @Override
	public String getDialogueRange() {
		return "3105-3116";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3105:
                player(c, Anim.CALM_1, "Hello.");
                c.nextChat = 3106;
                break;
            case 3106:
                npc3(c, "Magic Instructor", Anim.CALM_1, "Good day, newcomer. My name is Terrova. I'm here", "to tell you about Magic. Let's start by opening your", "spell list.");
                c.nextChat = 3107;
                break;
            case 3107:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Open up the Magic menu by clicking on the flashing icon next", "to the Prayer button you just learned about.", "", "Open up your final menu");
                c.getPA().chatbox(6179);
                c.nextChat = 0;
                c.setSidebarInterface(6, 1151);
                c.getPA().flashSideBarIcon(-6);
                break;
            case 3108:
                npc3(c, "Magic Instructor", Anim.CALM_1, "Good. This is a list of your spells. Currently you can", "only cast one offensive spell called Wind Strike. Let's", "try it out on one of those chickens.");
                c.nextChat = 3109;
                break;
            case 3109:
                item(c, 556, "Terrova gives you five air runes and five mind runes!");
                c.getItems().addItem(558, 5);
                c.getItems().addItem(556, 5);
                c.nextChat = 0;
                c.getDH().chatboxText("Now you have runes you should see the Wind Strike icon at the", "top left corner of the Magic interface - second in from the", "left. Walk over to the caged chickens, click the Wind Strike icon", "and then select one of the chicken to cast it on.", "Cast Wind Strke at a chicken");
                c.getPA().drawHeadicon(1, 3316, 0, 0);
                break;
            case 3110:
                npc2(c, "Magic Instructor", Anim.CALM_1, "Well you're all finished here now. I'll give you a", "reasonable number of runes when you leave.");
                c.nextChat = 3111;
                break;
            case 3111:
                options(c, 3111, "Mainland", "Stay here");
                break;
            case 3112:
                c.tutorialProgress = 35;
                npc4(c, "Magic Instructor", Anim.CALM_1, "When you get to the mainland you will find yourself in", "the town of Lumbridge. If you want some ideas on", "where to go next, talk to my friend the Lumbridge", "Guide. You can't miss him; he's holding a big staff with");
                c.nextChat = 3113;
                break;
            case 3113:
                npc4(c, "Magic Instructor", Anim.CALM_1, "a question mark on the end. He also has a white beard", "and carries a rucksack full of scrolls. There are also", "many tutors willing to teach you about the many skills", "you could learn.");
                c.nextChat = 3114;
                break;
            case 3114:
                npc4(c, "Magic Instructor", Anim.CALM_1, "If all else fails, visit the SlothScape website for a whole", "chestload of information on quests, skills, and minigames", "as well as a very good starter's guide.", "");
                c.nextChat = 3115;
                break;
            case 3115:
                c.tutorialProgress = 36;
                c.getDH().sendStatement("Teleporting to the main land.");
                c.getItems().deleteAllItems();
                c.getPA().startTeleport2(3222, 3218, 0); // Lumbridge coordinates
                c.nextChat = 3116;
                c.canWalkTutorial = true;
                break;
            case 3116:
                c.getPA().addStarter();
                c.getDH().sendStatement("Welcome to Lumbridge! To get more help, simply click on the", "Lumbridge Guide or one of the Tutors - these can be found by", "looking for the question mark icon on your mini-map. If you find", "you are lost at any time, look for a signpost.");
                c.sendSidebars();
                c.nextChat = -1;
                c.canWalkTutorial = true;
                c.closeTutorialInterface = true;
                c.getPA().removeAllWindows();
                break;
        }
    }

    // Handles the "Mainland" vs "Stay here" choice
    @Override
    public void onOption(Player c, int buttonId) {
        if (c.dialogueAction == 3111) {
            switch (buttonId) {
                case OPT2_FIRST:
                    next(c, 3112); 
                    break;
                case OPT2_SECOND:
                    end(c);
                    break;
            }
        }
    }
}