package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class MiningInstructor extends NPCDialogue {

    public MiningInstructor(Player c) { super(c); }

    @Override
    public int getNPCID() { return 9481; }
    @Override
	public String getDialogueRange() {
		return "3051-3066";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3051:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Next let's get you a weapon or more to the point, you can", "make your first weapon yourself. Don't panic, the Mining", "Instructor will help you. Talk to him and he'll tell you all about it.", "Mining and Smithing");
                c.getPA().chatbox(6179);
                c.getPA().createPlayerHints(1, 5);
                c.nextChat = 0;
                break;
            case 3052:
                npc4(c, "Mining Instructor", Anim.TALKING_ALOT, "Hi there. You must be new around here. So what do I", "call you? Newcomer seems so impersonal and if we're", "going to be working together, I'd rather call you by", "name.");
                c.nextChat = 3053;
                break;
            case 3053:
                player(c, Anim.HAPPY, "You can call me " + c.playerName + ".");
                c.nextChat = 3054;
                break;
            case 3054:
                npc2(c, "Mining Instructor", Anim.CALM_1, "Ok then, " + c.playerName + ". My name is Dezzick and I'm a", "miner by trade. Let's prospect some of those rocks.");
                c.nextChat = 3055;
                break;
            case 3055:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "To prospect a mineable rock, just right click it and select the", "'prospect rock' option. This will tell you the type of ore you can", "mine from it. Try it now on one of the rocks indicated.", "Prospecting");
                c.getPA().chatbox(6179);
                c.tutorialProgress = 15;
                c.nextChat = 0;
                break;
            case 3056:
                player2(c, Anim.HAPPY, "I prospected both types of rocks! One set contains tin", "and the other has copper ore inside.");
                c.nextChat = 3057;
                break;
            case 3057:
                npc2(c, "Mining Instructor", Anim.CALM_1, "Absolutely right, " + c.playerName + ". These two ore types", "can be smelted together to make bronze.");
                c.nextChat = 3058;
                break;
            case 3058:
                npc3(c, "Mining Instructor", Anim.TALKING_ALOT, "So now you know what ore is in the rocks over there,", "why don't you have a go at mining some tin and", "copper? here, you'll need this to start with.");
                c.nextChat = 3060;
                break;
            case 3060:
                item(c, 1265, "Dezzick gives you a @blu@bronze pickaxe!");
                c.getItems().addItem(1265, 1);
                c.nextChat = 0;
                c.getDH().chatboxText("It's quite simple really. All you need to do is right click on the", "rock and select 'mine'. You can only mine when you have a", "pickaxe. So give a try: first mine one tin ore.", "", "Mining");
                c.getPA().createObjectHints(3076, 9504, c.getHeight(), 2);
                c.tutorialProgress = 17;
                break;
            case 3061:
                c.tutorialProgress = 19;
                c.nextChat = 0;
                c.getDH().chatboxText("You should now have both some copper and tin ore. So let's", "smelt them to make a bronze bar. To do this, right click on", "either tin or copper ore and select use, then left click on the", "furnace. Try it now.", "Smelting");
                break;
            case 3062:
                c.tutorialProgress = 20;
                c.nextChat = 0;
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Speak to the Mining Instructor and he'll show you how to make", "it into a weapon.", "", "You've made a bronze bar!");
                c.getPA().chatbox(6179);
                c.getPA().createPlayerHints(1, 5);
                break;
            case 3063:
                player(c, Anim.CONFUSED, "How do I make a weapon out of this?");
                c.nextChat = 3064;
                break;
            case 3064:
                npc2(c, "Mining Instructor", Anim.CALM_1, "Okay, I'll show you how to make a dagger out of it.", "You'll be needing this...");
                c.nextChat = 3065;
                break;
            case 3065:
                item(c, 2347, "Dezzick gives you a @blu@hammer!");
                c.getItems().addItem(2347, 1);
                c.nextChat = 0;
                c.getDH().chatboxText("To smith you'll need a hammer - like the one you were given by", "Dezzick - access to an anvil like the one with the arrow over it", "and enough metal bars to make what you are trying to smith.", "", "Smithing a dagger");
                c.getPA().createObjectHints(3082, 9499, c.getHeight(), 2);
                break;
            case 3066:
                c.getDH().chatboxText("So let's move on. Go through the gates shown by the arrow.", "Remember you may need to move the camera to see your,", "surroundings. Speak to the guide for a recap at any time.", "", "You've finished in this area");
                c.tutorialProgress = 21;
                c.getPA().createObjectHints(3094, 9503, c.getHeight(), 2);
                c.nextChat = 0;
                break;
        }
    }
}