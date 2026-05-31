package server.model.players.combat;

import java.util.LinkedList;
import java.util.Queue;

import server.event.CycleEvent;
import server.event.CycleEventContainer;
import server.model.Entity;
import server.model.npcs.NPC;
import server.model.players.Player;

/**
 * 
 * @author Jason MacKeigan
 * @date Nov 9, 2014, 10:02:13 AM
 */
public class DamageQueueEvent implements CycleEvent {
	/**
	 * The queue containing all of the damage being dealt by the player
	 */
	private Queue<Damage> damageQueue = new LinkedList<>();

	/**
	 * The damage dealer, the owner of the queue
	 */
	private Entity owner;

	/**
	 * Creates a new class that will manage all damage dealt by the player
	 * 
	 * @param owner the player dealing the damage
	 */
	public DamageQueueEvent(Entity owner) {
		this.owner = owner;
	}

	/**
	 * Adds a damage object to the end of the queued damage list
	 * 
	 * @param damage the damage to be dealt
	 */
	public void add(Damage damage) {
		//System.out.println("Delayed Hit Success");
		damageQueue.add(damage);
	}

	public void execute(CycleEventContainer container) {
		if (damageQueue.size() <= 0) {
			return;
		}
		Damage damage;
		Queue<Damage> updatedQueue = new LinkedList<>();
		while ((damage = damageQueue.poll()) != null) {
			damage.removeTick();
			if (damage.getTicks() == 1) {
				if (damage.getTarget().isPlayer()) {
					Player target = damage.getTarget().asPlayer();
					AttackPlayer.playerDelayedHit(owner.asPlayer(), target.getIndex(), damage);
				} else if (damage.getTarget().isNPC()) {
					NPC target = damage.getTarget().asNPC();
					//System.out.println("Delayed Hit Success");
					AttackNPC.delayedHit(owner.asPlayer(), target.getIndex(), damage);

				}
			} else if (damage.getTicks() > 1) {
				updatedQueue.add(damage);
			}
		}
		damageQueue.addAll(updatedQueue);
	}

	public Queue<Damage> getQueue() {
		return damageQueue;
	}

}
