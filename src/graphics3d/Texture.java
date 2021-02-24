package graphics3d;

import mars.geometry.Vector;

public interface Texture {
	
	/**
	 * Returns a material at the specified location on a surface of a parametrizable solid.
	 */
	Material materialAt(Vector uv);
	
}
