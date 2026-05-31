package server.model.npcs.combat;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Graphic {
	
	private int id, height;
	
	public Graphic(int id) {
		this(id, 0);
	}
	
	public Graphic(int id, int height) {
		this.id = id;
		this.height = height;
	}

	public int getHeight() {
		// TODO Auto-generated method stub
		return height;
	}
	
}
