package graphics3d;

import mars.geometry.Vector;

/** Interaction of a ray with a solid.*/
public interface Hit {
	
	/** The time of the hit */
	double t();
	
	/** The normalized normal at the point of the hit */
	Vec3 n_();
	
	/** 2D coordinates in the internal coordinate system of the surface. */
	default Vector uv() {
		return Vector.ZERO;
	}
	
	
	// =====================================================================================================
	
	abstract class HitRayT implements Hit {
		private final Ray ray;
		private final double t;
		
		protected HitRayT(Ray ray, double t) {
			this.ray = ray;
			this.t = t;
		}
		
		public Ray ray() {
			return ray;
		}
		
		@Override
		public double t() {
			return t;
		}
	}
	
	
	Hit NEGATIVE_INFINITY = new Hit() {
		@Override public double t () { return Double.NEGATIVE_INFINITY; }
		@Override public Vec3   n_() { return Vec3.ZERO; }
	};
	
	
	Hit POSITIVE_INFINITY = new Hit() {
		@Override public double t () { return Double.POSITIVE_INFINITY; }
		@Override public Vec3   n_() { return Vec3.ZERO; }
	};
	
	
	default Hit inversed() {
		return new Hit() {
			@Override public double t () { return Hit.this.t(); }
			@Override public Vec3   n_() { return Hit.this.n_().inverse(); }
			@Override public Vector uv() { return Hit.this.uv(); }
		};
	}
	
	default Hit withN(Vec3 n_) {
		return new Hit() {
			@Override public double t () { return Hit.this.t(); }
			@Override public Vec3   n_() { return n_; }
			@Override public Vector uv() { return Hit.this.uv(); }
		};
	}
}
