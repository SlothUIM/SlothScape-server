package server.model.players.packets.dialogue.npc;

import server.model.players.Player;
import server.model.players.packets.dialogue.Anim;
import server.model.players.packets.dialogue.NPCDialogue;

public class CombatInstructor extends NPCDialogue {

    public CombatInstructor(Player c) { super(c); }

    @Override
    public int getNPCID() { return 3307; }
    @Override
	public String getDialogueRange() {
		return "3067-3077";
	}
    @Override
    public void dialogue(Player c, int npcId, int startDialogueId) {
        switch (startDialogueId) {
            case 3067:
                player(c, Anim.CALM_1, "Hi! My name is " + c.playerName + ".");
                c.nextChat = 3068;
                break;
            case 3068:
                npc2(c, "Combat Instructor", Anim.CALM_1, "Do I look like I care? To me you're just another", "newcomer who thinks they're ready to fight.");
                c.nextChat = 3069;
                break;
            case 3069:
                npc(c, "Combat Instructor", Anim.CALM_1, "I am Vannaka, the greatest swordsman alive.");
                c.nextChat = 3070;
                break;
            case 3070:
                npc(c, "Combat Instructor", Anim.CALM_1, "Let's get started by teaching you to wield a weapon");
                c.nextChat = 3071;
                break;
            case 3071:
                c.getPA().closeAllWindows();
                c.getPA().chatbox(6180);
                c.getDH().chatboxText("", "You now have access to a new interface. Click on the flashing", "icon of a man the one to the right of your backpack icon.", "", "Wielding weapons");
                c.getPA().chatbox(6179);
                c.setSidebarInterface(4, 1644);
                c.getPA().flashSideBarIcon(-4);
                c.nextChat = 0;
                break;
            case 3072:
                npc2(c, "Combat Instructor", Anim.CALM_1, "Very good, but that little butter knife isn't going to", "protect you much. Here, take these.");
                c.nextChat = 3073;
                break;
            case 3073:
                item(c, 1171, "The Combat Guide gives you a @blu@bronze sword@bla@ and a", "@blu@wooden shield!");
                c.getItems().addItem(1171, 1);
                c.getItems().addItem(1277, 1);
                c.nextChat = 0;
                c.getDH().chatboxText("In your worn inventory panel, right click on the dagger and", "select the remove option from the drop down list. After you've", "unequipped the dagger, wield the sword and shield. As you", "pass the mouse over an item you will see its name.", "Unequipping items");
                c.getPA().removeHintIcon(c);
                break;
            case 3074:
                player(c, Anim.CALM_1, "I did it! I killed a giant rat!");
                c.nextChat = 3075;
                break;
            case 3075:
                npc3(c, "Combat Instructor", Anim.CALM_1, "I saw, " + c.playerName + ". You seem better at this than I", "thought. Now that you have grasped basic swordplay", "let's move on.");
                c.nextChat = 3076;
                break;
            case 3076:
                npc4(c, "Combat Instructor", Anim.CALM_1, "Let's try some ranged attacking, with this you can kill", "foes from a distance. Also, foes unable to reach you are", "as good as dead. You'll be able to attack the rats", "without entering the pit.");
                c.nextChat = 3077;
                break;
            case 3077:
                item(c, 841, "The Combat Guide gives you some @blu@bronze arrows@bla@ and", "a @blu@shortbow!");
                c.getItems().addItem(841, 1);
                c.getItems().addItem(882, 50);
                c.nextChat = 0;
                c.getDH().chatboxText("Now you have a bow and some arrows. Before you can use", "them you'll need to equip them. Remember: to attack, right", "click on the monster and select attack.", "", "Rat ranging");
                c.ratdied2 = true;
                c.getPA().drawHeadicon(1, 13, 0, 0);
                break;
        }
    }
}