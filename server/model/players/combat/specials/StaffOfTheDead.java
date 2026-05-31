package server.model.players.combat.specials;

//import server.event.impl.StaffOfTheDeadEvent;
import server.model.Entity;
import server.model.players.Player;
import server.model.players.combat.Damage;
import server.model.players.combat.Special;
import server.Server;

public class StaffOfTheDead extends Special {

	public StaffOfTheDead() {
		super(10.0, 1.0, 1.0, new int[] { 11791, 12904 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		//player.gfx0(1228, 255);
		player.getEventHandler().stop(player, "staff_of_the_dead");
		//player.getEventHandler().submit(new StaffOfTheDeadEvent(player));
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 1) {
			//player.gfx(1229, 355);
			damage.setAmount(damage.getAmount() / 2);
		}
	}

}
