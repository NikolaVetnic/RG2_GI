package graphics3d;

public interface Collider {
	
	/**
	 * Returns the first (positive time) intersection of the ray r with a body from this set.
	 * Returned collision time is "ray-time", that is, it depends on the speed (r.d.length) of the ray.
	 */
	Collision collide(Ray r);
	
	/**
	 * Returns whether any collision occur with the positive time less than 1.
	 */
	default boolean collidesIn01(Ray r) {
		return collide(r).hit().t() < 1;
	}

}
