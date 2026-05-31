package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class BrotherBrace extends NPCDialogue {

    public BrotherBrace(Player c) { super(c); }

    @Override
    public int getNPCID() { return 949; }
    @Override
	public String getDialogueRange() {
		return "3089-3104";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3089:
                player(c, Anim.CALM_1, "Good day, brother, my name's " + c.playerName + ".");
                c.nextChat = 3090;
                break;
            case 3090:
                npc2(c, "Brother Brace", Anim.CALM_1, "Hello, " + c.playerName + ". I'm Brother Brace. I'm here to", "tell you all about Prayer.");
                c.nextChat = 3091;
                break;
            case 3091:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Click on the flashing icon to open the Prayer menu.", "", "", "Your Prayer menu");
                c.getPA().chatbox(6179);
                c.setSidebarInterface(5, 5608);
                c.getPA().flashSideBarIcon(-5);
                c.tutorialProgress = 29;
                c.nextChat = 0;
                break;
            case 3092:
                npc3(c, "Brother Brace", Anim.CALM_1, "This is your Prayer list. Prayers can help a lot in", "combat. Click on the prayer you wish to use to activate", "it and click it again to deactivate it.");
                c.nextChat = 3093;
                break;
            case 3093:
                npc3(c, "Brother Brace", Anim.CALM_1, "Active prayers will drain your Prayer Points which", "you can recharge by finding an altar or other holy spot", "and praying there.");
                c.nextChat = 3094;
                break;
            case 3094:
                npc3(c, "Brother Brace", Anim.CALM_1, "As you noticed, most enemies will drop bones when", "defeated. Burying bones by clicking them in your", "inventory will gain you Prayer experience.");
                c.nextChat = 3095;
                break;
            case 3095:
                npc2(c, "Brother Brace", Anim.CALM_1, "I'm also the community officer 'round here, so it's my", "job to tell you about your friends and ignore list.");
                c.nextChat = 3096;
                break;
            case 3096:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("You should now see another new icon. Click on the flashing", "icon to open your friends list.", "", "", "Friends list");
                c.getPA().chatbox(6179);
                c.setSidebarInterface(8, 5065);
                c.getPA().flashSideBarIcon(-8);
                c.tutorialProgress = 30;
                c.nextChat = 0;
                break;
            case 3097:
                npc4(c, "Brother Brace", Anim.CALM_1, "Good. Now you have both menus open, I'll tell you a", "little about each. You can add people to either list by", "clicking the add button then typing their name into the", "box that appears.");
                c.nextChat = 3098;
                break;
            case 3098:
                npc4(c, "Brother Brace", Anim.CALM_1, "You remove people from the lists in the same way. If", "you add someone to your ignore list they will not be", "able to talk to you or send any form of message to", "you.");
                c.nextChat = 3099;
                break;
            case 3099:
                npc4(c, "Brother Brace", Anim.CALM_1, "Your friends list shows the online status of your", "friends. Friends in the red are offline, friends in green are", "online and on the same server and friends in yellow", "are online but on a different server.");
                c.nextChat = 3100;
                break;
            case 3100:
                player(c, Anim.CALM_1, "Are there rules on in-game behaviour?");
                c.nextChat = 3101;
                break;
            case 3101:
                npc3(c, "Brother Brace", Anim.CALM_1, "Yes, you should read the rules of conduct on the", "website to make sure you do nothing to get yourself", "banned.");
                c.nextChat = 3102;
                break;
            case 3102:
                npc3(c, "Brother Brace", Anim.CALM_1, "But in general, always try to be courteous to other", "players - remember the people in the game are real", "people with real feelings.");
                c.nextChat = 3103;
                break;
            case 3103:
                npc2(c, "Brother Brace", Anim.CALM_1, "If you go 'round being abusive or causing trouble your", "character could end up being the one in trouble.");
                c.nextChat = 3104;
                break;
            case 3104:
                player(c, Anim.CALM_1, "Okay thanks. I'll bear that in mind.");
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("You're almost finished on tutorial island. Pass through the", "door to find the path leading to your final instructor.", "", "", "Your final instructor!");
                c.getPA().removeHintIcon(c);
                c.tutorialProgress = 32;
                c.getPA().chatbox(6179);
                c.getPA().createPlayerHints(1, 9);
                c.nextChat = 0;
                break;
        }
    }
}