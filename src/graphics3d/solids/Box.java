package graphics3d.solids;

import graphics3d.*;
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
			if (max0 > afterTime) return HitData.tnu(max0, Vec3.E[iMax0].mul(-Numeric.sign(ray.d().get(iMax0))), Vector.xy(max0, min1)); // TODO use Vec3.only
			if (min1 > afterTime) return HitData.tnu(min1, Vec3.E[iMin1].mul( Numeric.sign(ray.d().get(iMin1))), Vector.xy(max0, min1));
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
	
}
