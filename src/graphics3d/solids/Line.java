package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class Line implements Solid {
	
	
	private static final double LINE_THICKNESS = 0.025;

	
	private Vec3 p, q;
	private Solid l;
	
	
	private Line(Vec3 p, Vec3 q) {
		this.p = p;
		this.q = q;
		this.l = Solid.intersection(
				Cylinder.pdr(p, q.sub(p), LINE_THICKNESS), 
				HalfSpace.pn(q, q.sub(p)), 
				HalfSpace.pn(p, p.sub(q)));
	}
	
	
	public static Line pq(Vec3 p, Vec3 q) {
		return new Line(p, q);
	}
	
	
	public Vec3 p() { return this.p; }
	public Vec3 q() { return this.q; }
	
	
	@Override
	public boolean equals(Object o) {
		
		if (this == o) 
			return true;
		
		if (o == null) 
			return false;
		
		if (this.getClass() != o.getClass()) 
			return false;
		
		Line l = (Line) o;
		
		if 		(p.equals(l.p()))
			return q.equals(l.q()) ? true : false;
		else if (p.equals(l.q()))
			return q.equals(l.p()) ? true : false;
		else
			return false;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		return l.hits(ray);
	}
}
