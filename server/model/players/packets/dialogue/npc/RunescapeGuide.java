package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class RunescapeGuide extends NPCDialogue {

    public RunescapeGuide(Player c) { super(c); }

    @Override
    public int getNPCID() { return 9476; } // Standard 317 RS Guide ID (Update if using OSRS IDs)
    @Override
	public String getDialogueRange() {
		return "3000-3011";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3000:
                c.getPA().tutorialIslandInterface(0, 0);
                c.getDH().chatboxText("To start the tutorial use your left mouse button to click on the",
                        "Runescape guide in this room. He is indicated by a flashing",
                        "yellow arrow above his head. If you can't see him, use your",
                        "keyboard's arrow keys to rotate the view.", "@blu@Getting started");
                c.getPA().chatbox(6179);
                c.tutorialProgress = 1;
                c.canWalkTutorial = true;
                c.nextChat = 0;
                break;
            case 3001:
                npc2(c, "Runescape Guide", Anim.TALKING_ALOT, "Greetings! I see you are a new arrival to the land. My", "job is to welcome all new visitors. So welcome!");
                c.nextChat = 3002;
                break;
            case 3002:
                npc2(c, "Runescape Guide", Anim.TALKING_ALOT, "You have already learned the first thing needed to", "succeed in this world: talking to other people!");
                c.nextChat = 3003;
                break;
            case 3003:
                npc3(c, "Runescape Guide", Anim.TALKING_ALOT, "You will find many inhabitants of this world have useful", "things to say to you. By clicking on them with your", "mouse you can talk to them.");
                c.nextChat = 3004;
                break;
            case 3004:
                npc4(c, "Runescape Guide", Anim.TALKING_ALOT, "I would also suggest reading through some of the", "supporting information on the website. There you can", "find the Knowledge Base, which contains all the", "additional information you're ever likely to need. It also");
                c.nextChat = 3005;
                break;
            case 3005:
                npc2(c, "Runescape Guide", Anim.TALKING_ALOT, "contains maps and helpful tips to help you on your", "journey.");
                c.nextChat = 3006;
                break;
            case 3006:
                c.getDH().clearChatBoxText(c);
                npc2(c, "Runescape Guide", Anim.TALKING_ALOT, "You will notice a flashing icon of a wrench, please click", "on this to continue the tutorial.");
                c.setSidebarInterface(11, 904);
                c.getPA().flashSideBarIcon(-11);
                c.nextChat = 3007;
                break;
            case 3007:
                c.getPA().closeAllWindows();
                c.getDH().chatboxText("Please click on the flashing wrench icon found at the bottom", "right of your screen. This will display your player controls.", "", "", "Player controls");
                c.nextChat = 0;
                break;
            case 3008:
                npc(c, "Runescape Guide", Anim.HAPPY, "I'm glad you're making progress!");
                c.nextChat = 3009;
                break;
            case 3009:
                npc2(c, "Runescape Guide", Anim.HAPPY, "To continue the tutorial go through that door over", "there and speak to your first instructor!");
                c.nextChat = 3010;
                break;
            case 3010:
                c.tutorialProgress = 2;
                c.getPA().chatbox(6180);
                c.getPA().closeAllWindows();
                c.getDH().chatboxText("You can interact with many items of scenery by simply clicking", "on them. Right clicking will also give more options. Feel free to", "try it with the things in this room, then click on the door", "indicated with the yellow arrow to go through to the next instructor.", "Interacting with scenery");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3098, 3107, c.getHeight(), 2);
                c.nextChat = 0;
                break;
            case 3011:
                c.getPA().closeAllWindows();
                c.getDH().chatboxText("Follow the path to find the next instructor. Clicking on the", "ground will walk you to that point. Talk to the Survival Expert by", "the pond to the continue the tutorial. Remember you can rotate", "the view by pressing the arrow keys.", "Moving around");
                c.getPA().drawHeadicon(1, 9477, 0, 0);
                c.nextChat = 0;
                break;
        }
    }
}