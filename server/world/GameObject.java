package server.world;

import server.clip.ObjectDef;
import server.model.Entity;
import server.model.HealthStatus;
import server.model.players.Player;
import server.model.players.PlayerHandler;
import server.model.players.combat.Hitmark;
import server.util.Buffer;

public final class GameObject extends Entity {
	private int id;
	private int type;
	private int x;
	private int y;
	private int face;

	public GameObject(int id, Location position, int type, int x, int y, int face) {
		super(position);
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
		this.face = face;
	}

	public GameObject(int objectId, Location position) {
		super(position);
		this.id = objectId;
	}

	public int id() {
		return id;
	}

	public int type() {
		return type;
	}

	public int x() {
		return x;
	}

	public int y() {
		return y;
	}

	public int getFace() {
		return face;
	}

	@Override
	protected void appendHitUpdate(Buffer str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void appendHitUpdate2(Buffer str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void appendDamage(int damage, Hitmark hitmark) {
		// TODO Auto-generated method stub
		
	}
	public void performAnimation(int animation) {
		for (Player player : PlayerHandler.players) {
			if (player == null)
				continue;
			if (player.isWithinDistance(getLocation(), 32))
				player.getPA().sendObjectAnimation(player, this, animation);
			
		}
	}
	@Override
	public boolean susceptibleTo(HealthStatus status) {
		// TODO Auto-generated method stub
		return false;
	}
	public ObjectDef getDefinition() {
		return ObjectDef.forID(id);
	}
	@Override
	public int getSize() {
		ObjectDef definition = getDefinition();
		if (definition == null)
			return 1;

		switch (id) {
		case 38660:
		case 410:
		case 2320:
			return 2;
		case 2282:
			return 5;
		case 1767:
			return 9;
		}

		if (definition.width == 1)
			return definition.length;
		else if (definition.width > 1 && definition.length == 1)
			return definition.width;
		else
			return definition.length + definition.width;

	}
}