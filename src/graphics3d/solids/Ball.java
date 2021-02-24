package graphics3d.solids;

import graphics3d.*;
import mars.geometry.Vector;
import mars.utils.Numeric;


public class Ball implements Solid {
	
	private final Vec3 c;
	private final double r;
	
	// transient
	private final double rSqr;

	
	
	private Ball(Vec3 c, double r) {
		this.c = c;
		this.r = r;
		rSqr = r * r;
	}
	
	
	public static Ball cr(Vec3 c, double r) {
		return new Ball(c, r);
	}
	
	
	public Vec3 c() {
		return c;
	}
	
	
	public double r() {
		return r;
	}
	
	
	
	@Override
	public Hit[] hits(Ray ray) {
		Vec3 e = c().sub(ray.p());                                // Vector from the ray origin to the ball center
		
		double dSqr = ray.d().lengthSquared();
		double l = e.dot(ray.d()) / dSqr;
		double mSqr = l * l - (e.lengthSquared() - rSqr) / dSqr;
		
		if (mSqr <= 0) {
			return Solid.NO_HITS;
		} else {
			double m = Math.sqrt(mSqr);
			return new Hit[] {
					new HitBall(ray, l - m),
					new HitBall(ray, l + m)
			};
		}
	}
	
	
	class HitBall extends Hit.HitRayT {
		
		protected HitBall(Ray ray, double t) {
			super(ray, t);
		}
		
		@Override
		public Vec3 n_() {
			return ray().at(t()).sub(c()).div(r());
		}
		
		@Override
		public Vector uv() {
			Vec3 n_ = n_();
			return Vector.xy(
					Numeric.atan2T(n_.z(), n_.x()),
-					2 * Math.asin(n_.y()) / Numeric.TAU + 0.5
//					Numeric.asinT(n_.y()) // Bug in the library
//					(n_.y()+1)/2
			);
		}
	}
	
}
