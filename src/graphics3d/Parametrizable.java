package graphics3d;

import mars.geometry.Vector;

/**
 * Solids implementing this interface can be assigned 2D coordinates (u, v) to each point on their surface.
 */
public interface Parametrizable {
	
	/**
	 * @param p A point on the surface.
	 * @return 2D coordinates in the internal coordinate system of the surface.
	 */
	Vector uv(Vec3 p);
	
}
