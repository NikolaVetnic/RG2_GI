package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Parametrizable;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Triangle implements Solid, Parametrizable {
	
	
	private final Vec3 p; // A point on the boundary plane
	private final Vec3 e; // A vector parallel to the boundary plane.
	private final Vec3 f; // A vector parallel to the boundary plane, not parallel to e.
	private final Vec3 n_; // A normal vector to the boundary plane
	
	private final double e_f, f_e, sinSqr;
	
	
	private Triangle(Vec3 p, Vec3 e, Vec3 f) {
		this.p = p;
		this.e = e;
		this.f = f;
		this.n_ = e.cross(f).normalized_();
		
		double ef = e.dot(f);
		e_f = ef / f.lengthSquared();
		f_e = ef / e.lengthSquared();
		sinSqr = 1 - e_f * f_e;
	}
	
	
	public static Triangle pef(Vec3 p, Vec3 e, Vec3 f) {
		return new Triangle(p, e, f);
	}
	
	
	public static Triangle pqr(Vec3 p, Vec3 q, Vec3 r) {
		return pef(p, q.sub(p), r.sub(p));
	}
	
	
	public static Triangle pqr(Vec3[] a) {
		return pef(a[0], a[1].sub(a[0]), a[2].sub(a[0]));
	}
	

	@Override
	public Hit[] hits(Ray ray) {
		
		double o = n_.dot(ray.d());
		double t = n_.dot(p.sub(ray.p())) / o;
		
		Vector uv = uv(ray.at(t));
		if (uv.x() < 0 || uv.y() < 0 || uv.x() + uv.y() > 1) {
			return Solid.NO_HITS;
		}
		
		if (o < 0) {			// The ray enters the half-space
			return new Hit[] { HitData.tn(t, n_), HitData.tn(Double.POSITIVE_INFINITY, n_) }; 	
		}
		if (o > 0) {			// The ray exits the half-space
			return new Hit[] { HitData.tn(Double.NEGATIVE_INFINITY, n_), HitData.tn(t, n_) }; 	
		}
		return Solid.NO_HITS;	// The ray is parallel to the half-space;                         									
	}
	
	
	@Override
	public Vector uv(Vec3 p) {
		Vec3 l = p.sub(this.p);
		
		double b_e = l.dot(e) / e.lengthSquared();
		double b_f = l.dot(f) / f.lengthSquared();
		
		return Vector.xy(
				(b_e - b_f * f_e) / sinSqr,
				(b_f - b_e * e_f) / sinSqr
		);
	}
	
	
	public Vec3 n_() { return n_; }
}
