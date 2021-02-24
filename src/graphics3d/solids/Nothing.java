package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;

public class Nothing implements Solid {
	
	public static final Nothing INSTANCE = new Nothing();

	private Nothing() {
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		return Solid.NO_HITS;
	}
}
