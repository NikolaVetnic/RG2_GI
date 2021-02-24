package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class Cylinder implements Solid {
	
	private final Vec3 p, d;
	private final double r;
	
	// transient
	private final double rSqr;

	
	
	private Cylinder(Vec3 p, Vec3 d, double r) {
		this.p = p;
		this.d = d;
		this.r = r;
		rSqr = r * r;
	}
	
	
	public static Cylinder pdr(Vec3 p, Vec3 d, double r) {
		return new Cylinder(p, d, r);
	}
	
	
	public Vec3 p() {
		return p;
	}
	
	
	public Vec3 d() {
		return d;
	}
	
	
	public double r() {
		return r;
	}
	
	
	
	@Override
	public Hit[] hits(Ray ray) {
		Vec3 e = p.sub(ray.p());                                // Vector from the ray origin to the cylinder center
		Vec3 eP = e.rejection(d);
		Vec3 dP = ray.d().rejection(d);
		double dSqr = dP.lengthSquared();
		double l = eP.dot(dP) / dSqr;
		double mSqr = l * l - (eP.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr <= 0) {
			return Solid.NO_HITS;
		} else {
			double m = Math.sqrt(mSqr);
			return new Hit[] {
					new HitCylinder(ray, l - m),
					new HitCylinder(ray, l + m)
			};
		}
	}
	
	
	class HitCylinder extends Hit.HitRayT {
		
		protected HitCylinder(Ray ray, double t) {
			super(ray, t);
		}
		
		@Override
		public Vec3 n_() {
			return ray().at(t()).sub(p()).rejection(d).div(r);
		}
	}
	
}
