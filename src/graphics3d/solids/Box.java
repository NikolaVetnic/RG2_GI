package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;
import mars.utils.Numeric;

public class Box implements Solid {
	
	public static class Factory implements BoxBoundedFactoryPQ<Box> {
		@Override
		public Box pq(Vec3 p, Vec3 q) {
			return new Box(p, q);
		}
	}
	
	public static final Factory $ = new Factory();
	public static Box UNIT = Box.$.pq(Vec3.ZERO, Vec3.EXYZ);
	
	
	private final Vec3 p, q;
	
	
	protected Box(Vec3 p, Vec3 q) {
		this.p = p;
		this.q = q;
	}
	
	
	public Vec3 p() {
		return p;
	}

	
	public Vec3 q() {
		return q;
	}


	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		Vec3 tP = p().sub(ray.p()).div(ray.d());
		Vec3 tQ = q().sub(ray.p()).div(ray.d());

		Vec3 t0 = Vec3.min(tP, tQ);
		Vec3 t1 = Vec3.max(tP, tQ);

		int iMax0 = t0.maxIndex();
		int iMin1 = t1.minIndex();

		double max0 = t0.get(iMax0);
		double min1 = t1.get(iMin1);

		if (max0 < min1) {
			if (max0 > afterTime) return new HitBox(ray, max0, Vec3.E[iMax0].mul(-Numeric.sign(ray.d().get(iMax0)))); // TODO use Vec3.only
			if (min1 > afterTime) return new HitBox(ray, min1, Vec3.E[iMin1].mul( Numeric.sign(ray.d().get(iMin1))));
		}
		
		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		Vec3 tP = p().sub(ray.p()).div(ray.d());
		Vec3 tQ = q().sub(ray.p()).div(ray.d());
		
		Vec3 t0 = Vec3.min(tP, tQ);
		Vec3 t1 = Vec3.max(tP, tQ);
		
		int iMax0 = t0.maxIndex();
		int iMin1 = t1.minIndex();
		
		double max0 = t0.get(iMax0);
		double min1 = t1.get(iMin1);
		
		if (max0 < min1) {
			return new Hit[] {
					HitData.tnu(max0, Vec3.E[iMax0].mul(-Numeric.sign(ray.d().get(iMax0))), Vector.xy(max0, min1)),
					HitData.tnu(min1, Vec3.E[iMin1].mul( Numeric.sign(ray.d().get(iMin1))), Vector.xy(max0, min1))
			};
		} else {
			return Solid.NO_HITS;
		}
	}
	
	
	public Vec3 random(Sampler sampler) {
		return Vec3.xyz(
				Numeric.interpolateLinear(p().x(), q().x(), sampler.uniform()),
				Numeric.interpolateLinear(p().y(), q().y(), sampler.uniform()),
				Numeric.interpolateLinear(p().z(), q().z(), sampler.uniform())
		);
	}
	
	class HitBox extends Hit.HitRayT {
		
		private Vec3 n_;
		
		protected HitBox(Ray ray, double t, Vec3 n_) {
			super(ray, t);
			this.n_ = n_;
		}
		
		@Override
		public Vec3 n_() {
			return n_;
		}
		
		@Override
		public Vector uv() {
			
			var shouldUseFauxVoxelMapping = true;
			
			if (shouldUseFauxVoxelMapping)
				return uvFauxVoxel();
			else
				return uvProper();
		}
		
		private Vector uvProper() {
			
//			a = a.add(p).projection(q.sub(a));
			
			var a = this.ray().at(t()).sub(q.sub(p).mul(0.5)).sub(p);
			
//			a = a.sub(q).inverse();
			
			double x = a.x();
			double y = a.y();
			double z = a.z();
			
//			double x = q.sub(p).sub(a).x();
//			double y = q.sub(p).sub(a).y();
//			double z = q.sub(p).sub(a).z();
			
			double absX = a.x() >= 0 ? a.x() : -a.x();
			double absY = a.y() >= 0 ? a.y() : -a.y();
			double absZ = a.z() >= 0 ? a.z() : -a.z();
			
			boolean isXPositive = a.x() > 0;
			boolean isYPositive = a.y() > 0;
			boolean isZPositive = a.z() > 0;
			
			double maxAxis = 0.0, uc = 0.0, vc = 0.0;
			int index = -1;
			
			// Positive X.
			if (isXPositive && absX >= absY && absX >= absZ) {
				// u (0 to 1) goes from +z to -z
			    // v (0 to 1) goes from -y to +y
				maxAxis = absX;
			    uc = -z;
			    vc = y;
			    index = 0;
			}
			
			// Negative X.
			if (!isXPositive && absX >= absY && absX >= absZ) {
			    // u (0 to 1) goes from -z to +z
			    // v (0 to 1) goes from -y to +y
			    maxAxis = absX;
			    uc = z;
			    vc = y;
			    index = 1;
			  }
			// Positive Y.
			if (isYPositive && absY >= absX && absY >= absZ) {
			    // u (0 to 1) goes from -x to +x
			    // v (0 to 1) goes from +z to -z
			    maxAxis = absY;
			    uc = x;
			    vc = -z;
			    index = 2;
			}
			// Negative Y.
			if (!isYPositive && absY >= absX && absY >= absZ) {
			    // u (0 to 1) goes from -x to +x
			    // v (0 to 1) goes from -z to +z
			    maxAxis = absY;
			    uc = x;
			    vc = z;
			    index = 3;
			}
			// Positive Z.
			if (isZPositive && absZ >= absX && absZ >= absY) {
			    // u (0 to 1) goes from -x to +x
			    // v (0 to 1) goes from -y to +y
			    maxAxis = absZ;
			    uc = x;
			    vc = y;
			    index = 4;
			}
			// Negative Z.
			if (!isZPositive && absZ >= absX && absZ >= absY) {
			    // u (0 to 1) goes from +x to -x
			    // v (0 to 1) goes from -y to +y
			    maxAxis = absZ;
			    uc = -x;
			    vc = y;
			    index = 5;
			}
			
			return Vector.xy(0.5 * (uc / maxAxis + 1.0), 0.5 * (vc / maxAxis + 1.0));
		}
		
		private Vector uvFauxVoxel() { // voxel-like checkerbox mapping
		
		    var localHitPosition = this.ray().at(t()).sub(q.sub(p).mul(0.5)).sub(p);

		    var absX = Math.abs(localHitPosition.x());
		    var absY = Math.abs(localHitPosition.y());
		    var absZ = Math.abs(localHitPosition.z());

		    double u = 0.0, v = 0.0; // store calculated uv coordinates

		    if (absX >= absY && absX >= absZ) { // determine the dominant axis (the face the hit point lies on) and map uv coordinates

		        u = localHitPosition.z();
		        v = localHitPosition.y();
		        
		        if (localHitPosition.x() > 0) // positive x face
		            u = 1 - u; // offset for seamless mapping
		            
		    } else if (absY >= absX && absY >= absZ) {
		    	
		        u = localHitPosition.x();
		        v = localHitPosition.z();
		        
		        if (localHitPosition.y() > 0)
		            v = 1 - v;

		    } else {

		        u = localHitPosition.x();
		        v = localHitPosition.y();
		        
		        if (localHitPosition.z() > 0)
		            u = 1 - u;

		    }

		    u = 0.5 * (u + 1.0); // normalize u and v to [0, 1]
		    v = 0.5 * (v + 1.0);
		    
		    if 		(n_.z() > 0)
		    	return Vector.xy(-u, -v);
		    else if (n_.z() < 0)
		    	return Vector.xy(+u, +v);
		    else if (n_.y() > 0)
		    	return Vector.xy(+u, +v);
		    else if (n_.y() < 0)
	    		return Vector.xy(-u, -v);
		    else if (n_.z() > 0)
		    	return Vector.xy(-u, +v);
		    else
		    	return Vector.xy(+u, +v);
		}
	}
}
