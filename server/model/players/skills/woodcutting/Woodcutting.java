package server.model.players.skills.woodcutting;

import server.model.players.Player;
import server.model.players.Sound;
import server.model.players.skills.Skill;
import server.world.World;

public class Woodcutting {

	private static final Woodcutting INSTANCE = new Woodcutting();

	public void chop(Player player, int objectId, int x, int y) {
		Tree tree = Tree.forObject(objectId);
		if (tree == null) {
			return; // Object isn't a valid tree
		}

		if (player.getSkills().getLevel(Skill.WOODCUTTING) < tree.getLevelRequired()) {
			player.sendMessage("You need a Woodcutting level of "+ tree.getLevelRequired() +" to cut down a "+ tree.forObject(objectId).name().toLowerCase()+" tree");
			return;
		}
		Hatchet hatchet = Hatchet.getBest(player);
		if (hatchet == null) {
			player.sendMessage("You must have an axe and the level required to cut this tree down.");
			return;
		}
		if (player.getItems().freeSlots() == 0) {
			player.sendMessage("You must have at least one free inventory space to do this.");
			return;
		}
		if (World.getWorld().getGlobalObjects().exists(tree.getStumpId(), x, y)) {
			player.sendMessage("This tree has been cut down to a stump, you must wait for it to grow.");
			return;
		}
		player.getSkilling().stop();
		if (player.tutorialProgress == 3) {
			player.getPA().closeAllWindows();
			player.getPA().chatbox(6180);
			if (player.playerAppearance[0] == 0) {
				player.getDH().sendStartInfo("", "Your character is now attempting to cut down the tree. Sit back", "for a moment while he does all the hard work.", "", "Please wait");
			} else {
				player.getDH().sendStartInfo("", "Your character is now attempting to cut down the tree. Sit back", "for a moment while she does all the hard work.", "", "Please wait");
			}
			player.getPA().chatbox(6179);
		} else {
			player.sendMessage("You swing your axe at the tree.");
		}
		player.getPA().sendSound(Sound.SOUND_LIST.WOODCHOP.getSound(), 2, player.EffectVolume);
		player.startAnimation(hatchet.getAnimation());
		player.getSkilling().setSkill(Skill.WOODCUTTING);
		World.getWorld().getEventHandler().submit(new WoodcuttingEvent(player, tree, hatchet, objectId, x, y));
	}

	public static Woodcutting getInstance() {
		return INSTANCE;
	}

}
