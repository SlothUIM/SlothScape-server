package server.model.players.content.Skillcapes;

import server.model.players.Player;
import server.model.players.skills.Skill;

public class SkillCapes {
	public Player player;
	public SkillCapes(Player player) {
		this.player = player;
	}
	public void sendDialogue(int dialogueId, String...line) {
		player.dialogueAction = dialogueId;
		player.getDH().sendOptions(line);
	}
	public void handleCape(int removeId, int option) {
		// TODO Auto-generated method stub
		player.lastX = player.getX();
		player.lastY = player.getY();
		switch(removeId) {
		case 9810:
		case 9811:
			switch(option) {
			case 2:

				sendDialogue(5534, "Farming guild", "Catherby", "Ardougne", "Falador", "Morytania");
				break;
			}
			break;
		case 9750:
		case 9751:
			switch(option) {
			case 1:
			player.getPA().startTeleport(2878, 3546, 0, "glory");
			break;
			case 2:
				if (System.currentTimeMillis() - player.lastStrengthBoost > 10000) {
					player.getSkills().setLevel(player.getSkills().getLevel(Skill.STRENGTH)+1, Skill.forId(2));
					player.lastStrengthBoost = System.currentTimeMillis();
					player.getSkills().sendRefresh();
				}
			break;
			}
			break;
		case 13342:
			switch(option) {
				case 5:
				player.getPA().swapSpellBookOperate();
				break;
			}
			break;

			/*
			 * Crafting cape
			 */
		case 9780:
		case 9781:
			player.getPA().startTeleport(2936, 3283, 0, "modern");
			break;
			/*
			 * Magic skillcape
			 */
		case 9762:
		case 9763:
			switch(option) {
			case 3:
				player.sendMessage("Daily spell book swap: "+player.swapCount+"/3");
				break;
			case 2:

				player.getPA().swapSpellBookOperate();
			break;
			case 1:
				if (System.currentTimeMillis() - player.lastMagicBoost > 10000) {
					player.getSkills().setLevel(player.getSkills().getLevel(Skill.MAGIC)+1, Skill.forId(6));
					player.lastMagicBoost = System.currentTimeMillis();
					player.getSkills().sendRefresh();
				}
			break;
			}
			break;
		}
	}

}
