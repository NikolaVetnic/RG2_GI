package graphics3d.solids;

import graphics3d.*;
import mars.geometry.Vector;


public class HalfSpace implements Solid {
	
	private final Vec3 p; // A point on the boundary plane
	private final Vec3 e; // A vector parallel to the boundary plane.
	private final Vec3 f; // A vector parallel to the boundary plane, not parallel to e.
	
	// transient
	private final Vec3 n_; // A normal vector to the boundary plane
	private final double e_f, f_e, eLSqr, fLSqr, sinSqr;
	
	
	
	private HalfSpace(Vec3 p, Vec3 e, Vec3 f) {
		this.p = p;
		this.e = e;
		this.f = f;
		this.n_ = e.cross(f).normalized_();

		eLSqr = e.lengthSquared();
		fLSqr = f.lengthSquared();
		double ef = e.dot(f);
		e_f = ef / fLSqr;
		f_e = ef / eLSqr;
		sinSqr = 1 - e_f * f_e;
	}
	
	
	public static HalfSpace pef(Vec3 p, Vec3 e, Vec3 f) {
		return new HalfSpace(p, e, f);
	}
	
	
	public static HalfSpace pqr(Vec3 p, Vec3 q, Vec3 r) {
		return pef(p, q.sub(p), r.sub(p));
	}
	
	
	public static HalfSpace pn(Vec3 p, Vec3 n) {
		double nl = n.length();
		Vec3 e = Utils.normal(n).normalizedTo(nl);
		Vec3 f = n.cross(e).normalizedTo(nl);
		return new HalfSpace(p, e, f);
	}
	
	
	public Vec3 p() {
		return p;
	}
	
	
	public Vec3 e() {
		return e;
	}
	
	
	public Vec3 f() {
		return f;
	}
	
	
	public Vec3 n_() {
		return n_;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		double o = n_().dot(ray.d());
		double t = n_().dot(p().sub(ray.p())) / o;
		
		if (o < 0) {
			return new Hit[] {new HitHalfSpace(ray, t), Hit.POSITIVE_INFINITY}; // The ray enters the half-space.
		}
		if (o > 0) {
			return new Hit[] {Hit.NEGATIVE_INFINITY, new HitHalfSpace(ray, t)}; // The ray exits the half-space.
		}
		return Solid.NO_HITS;                                        // The ray is parallel to the half-space.
	}
	
	
	@Override
	public String toString() {
		return "HalfSpace{" +
				"p=" + p +
				", e=" + e +
				", f=" + f +
				", n=" + n_ +
				'}';
	}



//	@Override
//	public Solid complement() {
//		return HalfSpace.pef(p(), f(), e());
//	}
	
	
	class HitHalfSpace extends Hit.HitRayT {
		
		protected HitHalfSpace(Ray ray, double t) {
			super(ray, t);
		}
		
		@Override
		public Vec3 n_() {
			return n_;
		}
		
		@Override
		public Vector uv() {
			Vec3 b = ray().at(t()).sub(p);
			
			double b_e = b.dot(e) / eLSqr;
			double b_f = b.dot(f) / fLSqr;
			
			return Vector.xy(
					(b_e - b_f * f_e) / sinSqr,
					(b_f - b_e * e_f) / sinSqr
			);
		}
	}
	
}
