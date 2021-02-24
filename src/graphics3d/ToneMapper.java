package graphics3d;

public interface ToneMapper {
	
	/**
	 * @param pixelColors Input matrix. The color of each pixel
	 * @param imageData Output array. Fill with color codes for each pixel.
	 */
	void toneMap(Color[][] pixelColors, int[] imageData);
	
	
	default int[] toneMap(Color[][] pixelColors) {
		int[] imageData = new int[pixelColors.length * pixelColors[0].length];
		toneMap(pixelColors, imageData);
		return imageData;
	}
}
