package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;

public class Space implements Solid {
	
	public static final Space INSTANCE = new Space();

	private Space() {
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		return Solid.FULL_RANGE;
	}
}
