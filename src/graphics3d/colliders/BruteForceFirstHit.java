package graphics3d.colliders;

import graphics3d.*;

import java.util.List;


public class BruteForceFirstHit implements Collider {
	
	private final Body[] bodies;
	
	
	
	public BruteForceFirstHit(List<Body> bodies) {
		this.bodies = bodies.toArray(new Body[0]);
	}
	
	
	@Override
	public Collision collide(Ray r) {
		Hit hitMin = HitData.POSITIVE_INFINITY;
		double hitMinT = hitMin.t();
		Body bodyMin = null;
		
		for (Body b : bodies) {
			Hit hit = b.solid().firstHit(r, 1e-9);
			double hitT = hit.t();
			if (hitT < hitMinT) {
				hitMinT = hitT;
				hitMin = hit;
				bodyMin = b;
			}
		}
		
		return new Collision(hitMin, bodyMin);
	}
	

	@Override
	public boolean collidesIn01(Ray r) {
		for (Body b : bodies) {
			Hit hit = b.solid().firstHit(r, 1e-9);
			if (hit.t() < 1) {
				return true;
			}
		}
		
		return false;
	}
	
}
