package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class SurvivalExpert extends NPCDialogue {

    public SurvivalExpert(Player c) { 
        super(c); 
    }

    @Override
    public int getNPCID() { 
        return 9477; 
    }
    @Override
	public String getDialogueRange() {
		return "3012-3019";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3012:
                npc4(c, "Survival Expert", Anim.HAPPY, "Hello there, newcomer. My name is Brynna. My job is", "to teach you a few survival tips and tricks. First off", "we're going to start with the most basic survival skill of", "all: making a fire.");
                c.nextChat = 3013;
                break;
            case 3013:
                item(c, 590, "The Survival Guide gives you a @blu@tinderbox @bla@and a", "@blu@bronze axe!");
                c.getItems().addItem(590, 1);
                if(!c.getItems().playerHasItem(1351, 1)){
                    c.getItems().addItem(1351, 1);
                } 
                if(!c.getItems().playerHasItem(590, 1)){
                    c.getItems().addItem(590, 1);
                }
                c.nextChat = 0;
                c.getDH().chatboxText("Click on the flashing backpack icons to the right hand side of", "the main window to view your inventory. Your inventory is a list", "of everything you have on your backpack.", "", "Viewing the items that you were given");
                c.setSidebarInterface(4, 3213);
                c.getPA().flashSideBarIcon(-4);
                c.tutorialProgress = 3;
                break;
            case 3014:
                item(c, 1511, "You got some logs");
                c.getPA().removeHintIcon(c);
                c.nextChat = 3015;
                break;
            case 3015:
                c.getPA().closeAllWindows();
                c.getDH().chatboxText("Well done! You managed to cut some logs from the tree! Next,", "use the tinderbox in your inventory to light the logs.", "First click on the tinderbox to use it.", "Then click on the logs in your inventory to light them.", "Making a fire");
                c.tutorialProgress = 4;
                c.nextChat = 0;
                break;
            case 3016:
                c.getDH().chatboxText("Click on the flashing bar graph icon near the inventory button", "to see your skill stats.", "", "", "You gained some experience.");
                c.getPA().flashSideBarIcon(-1);
                c.setSidebarInterface(1, 27101);
                c.nextChat = 3017;
                break;
            case 3017:
                npc3(c, "Survival Expert", Anim.TALKING_ALOT, "Well done! Next we need to get some food in our", "bellies. We'll need something to cook. There are shrimp", "in the pond there so let's catch and cook some.");
                c.nextChat = 3018;
                break;
            case 3018:
                item(c, 303, "The Survival Guide gives you a @blu@net!");
                c.getItems().addItem(6209, 1);
                c.nextChat = 0;
                c.getDH().chatboxText("Click on the sparkling fishing spot indicated by the flashing", "arrow. Remember, you can check your inventory by clicking the", "backpack icon.", "", "Catch some Shrimp");
                c.getPA().createObjectHints(3101, 3092, c.getHeight(), 2);
                c.tutorialProgress = 6;
                break;
            case 3019:
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("Now you have caught some shrimp let's cook it. First light a", "fire, chop down a tree and then use the tinderbox on the logs.", "If you've lost your axe or tinderbox, Brynna will give you", "another.", "Cooking your shrimp.");
                c.getPA().chatbox(6179);
                c.nextChat = 0;
                break;
        }
    }
}