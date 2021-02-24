package graphics3d;

import mars.geometry.Vector;


public interface Renderer {
	
	Vector imageSize();
	
	/**
	 * @param pixelColors Output matrix. Color of each pixel.
	 */
	void renderIteration(Color[][] pixelColors);
	
	
	default Color[][] renderIteration() {
		Vector size = imageSize();
		Color[][] pixelColors = new Color[size.yInt()][size.xInt()];
		renderIteration(pixelColors);
		return pixelColors;
	}
	
	
	default boolean goodEnough() {
		return false;
	}
}
