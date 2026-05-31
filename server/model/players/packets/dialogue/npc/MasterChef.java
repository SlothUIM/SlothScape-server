package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class MasterChef extends NPCDialogue {

    public MasterChef(Player c) { super(c); }

    @Override
    public int getNPCID() { return 3305; }
    @Override
	public String getDialogueRange() {
		return "3020-3041";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3020:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Talk to the chef indicated. He will teach you the more advanced", "aspects of Cooking such as combining ingredients. He will also", "teach you about your music player menu as well.", "", "Find your next instructor");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3078, 3084, c.getHeight(), 2);
                c.nextChat = 0;
                break;
            case 3021:
                npc3(c, "Master Chef", Anim.TALKING_ALOT, "Ah! Welcome newcomer. I am the Master Chef Leo. It", "is here I will teach you how to cook food truly fit for a", "king.");
                c.nextChat = 3022;
                break;
            case 3022:
                player2(c, Anim.TALKING_ALOT, "I already know how to cook. Brynna taught me just", "now.");
                c.nextChat = 3023;
                break;
            case 3023:
                npc3(c, "Master Chef", Anim.LAUGH_2, "Hahahahahaha! You call THAT cooking? Some shrimp", "on an open log fire? Oh no, no, no. I am going to", "teach you the fine art of cooking bread.");
                c.nextChat = 3024;
                break;
            case 3024:
                npc2(c, "Master Chef", Anim.HAPPY, "And no fine meal is complete without good music, so", "we'll cover that while you're here too.");
                c.nextChat = 3025;
                break;
            case 3025:
                item(c, 1933, "The Cooking Guide gives you a @blu@bucket of water@bla@ and a", "@blu@pot of flour!");
                if(c.getItems().playerHasItem(1931, 1)){ c.getItems().deleteItem(1931, 1); c.getItems().addItem(1933, 1); }
                if(c.getItems().playerHasItem(1925, 1)){ c.getItems().deleteItem(1925, 1); c.getItems().addItem(1929, 1);
                } else { c.getItems().addItem(1929, 1); c.getItems().addItem(1933, 1); }
                c.nextChat = 3026;

                if(c.tutorialProgress < 8)
                    c.tutorialProgress = 9;
                break;
            case 3026:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Now you have made dough, you can cook it. To cook the dough", "use it with the range shown by the arrow. If you lose your", "dough, talk to Leo - he will give you more ingredients.", "", "Cooking dough");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3075, 3081, c.getHeight(), 2);
                //c.nextChat = 0;
                break;	
            case 3037:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Well done! Your first loaf of bread. As you gain experience in", "Cooking you will be able to make other things like pies, cakes", "and even kebabs. Now you've got the hang of cooking, let's", "move on. Click on the flashing icon in the bottom right.", "Cooking dough");
                c.getPA().chatbox(6179);
                c.getPA().removeHintIcon(c);
                c.setSidebarInterface(13, 50020);
                c.getPA().flashSideBarIcon(-13);
                c.tutorialProgress = 9;
                c.nextChat = 0;
                break;
            case 3038:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "Now, how about showing some feelings? You will see a flashing", "icon in the shape of a person. Click on that to access your", "emotes.", "Emotes");
                c.getPA().chatbox(6179);
                c.getPA().removeHintIcon(c);
                c.setSidebarInterface(12, 147);
                c.getPA().flashSideBarIcon(-12);
                c.nextChat = 0;
                break;
            case 3039:
                c.tutorialProgress = 11;
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("It's only a short distance to the next guide.", "Why not try running there? Start by opening the player", "settings, that's the flashing icon of a wrench.", "", "Running");
                c.getPA().chatbox(6179);
                c.getPA().flashSideBarIcon(-11);
                c.getPA().createObjectHints(3086, 3126, c.getHeight(),2);
                c.nextChat = 0;
                break;
            case 3040:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("In this menu you will see many options. At the bottom in the", "middle is a button with the symbol of a running shoe. You can", "turn this button on or off to select run or walk. Give it a go,", "click on the run button now.", "Running");
                c.getPA().chatbox(6179);
                c.nextChat = 0;
                break;
            case 3041:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Now that you have the run button turned on, follow the path", "until you come to the end. You may notice that the numbers on", "the button goes down. This is your run energy. If your run", "energy reaches zero, you'll stop running.", "Run to the next guide");
                c.getPA().chatbox(6179);
                c.getPA().createObjectHints(3086, 3125, c.getHeight(),2);
                c.nextChat = 0;
                break;
        }
    }
}