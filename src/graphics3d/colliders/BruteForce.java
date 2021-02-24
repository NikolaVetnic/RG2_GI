package graphics3d.colliders;

import graphics3d.*;

import java.util.List;


public class BruteForce implements Collider {
	
	private final Body[] bodies;
	
	
	
	public BruteForce(List<Body> bodies) {
		this.bodies = bodies.toArray(new Body[0]);
	}
	
	
	@Override
	public Collision collide(Ray r) {
		Hit hitMin = HitData.POSITIVE_INFINITY;
		Body bodyMin = null;
		
		for (Body b : bodies) {
			for (Hit hit : b.solid().hits(r)) {
				double t = hit.t();
				if (t > 1e-9) {
					if (t < hitMin.t()) {
						hitMin = hit;
						bodyMin = b;
					}
					break;
				}
			}
		}
		
		return new Collision(hitMin, bodyMin);
	}
	
	
	@Override
	public boolean collidesIn01(Ray r) {
		// We can save time by exiting early.
		
		for (Body b : bodies) {
			for (Hit hit : b.solid().hits(r)) {
				double t = hit.t();
				if (t > 1e-9) {
					if (t < 1) {
						return true;
					}
					break;
				}
			}
		}
		
		return false;
	}
}
