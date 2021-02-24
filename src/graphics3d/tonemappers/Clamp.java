package graphics3d.tonemappers;

import graphics3d.Color;
import graphics3d.ToneMapper;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.drawingx.gadgets.annotations.Properties;
import mars.functions.interfaces.Function0;

import java.util.stream.IntStream;


public class Clamp implements ToneMapper {
	
	public static class Factory implements Function0<ToneMapper> {
		@GadgetDoubleLogarithmic(p = 0.001, q = 1000)
		@DoNotDetectChanges
		double brightnessFactor = 1.0;
		
		@Override
		public ToneMapper at() {
			return new Clamp(brightnessFactor);
		}
	}

	
	private final double brightnessFactor;
	
	
	public Clamp(double brightnessFactor) {
		this.brightnessFactor = brightnessFactor;
	}
	
	
	private Color toneMap(Color c) {
		return c.mul(brightnessFactor);
	}
	
	
	@Override
	public void toneMap(Color[][] pixelColors, int[] imageData) {
		int w = pixelColors[0].length;
		IntStream.range(0, pixelColors.length).parallel().forEach(y -> {
			for (int x = 0; x < pixelColors[y].length; x++) {
				imageData[y * w + x] = toneMap(pixelColors[y][x]).codeClamp();
			}
		});
	}
}
