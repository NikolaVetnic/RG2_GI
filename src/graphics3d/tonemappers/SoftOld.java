package graphics3d.tonemappers;

import graphics3d.Color;
import graphics3d.ToneMapper;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.functions.interfaces.Function0;

import java.util.stream.IntStream;


public class SoftOld implements ToneMapper {
	
	
	public static class Factory implements Function0<ToneMapper> {
		@GadgetDoubleLogarithmic(p = 0.001, q = 1000)
		@DoNotDetectChanges
		double brightnessFactor = 1.0;
		
		@GadgetDoubleLogarithmic(p = 0.01, q = 100)
		@DoNotDetectChanges
		double b = 0.0625;
		
		@GadgetDoubleLogarithmic(p = 1.0/16, q = 16)
		@DoNotDetectChanges
		double p = 2.0;
		
		@GadgetDouble(p = 0, q = 1)
		@DoNotDetectChanges
		double a = 0.75;
		
		@Override
		public ToneMapper at() {
			return new SoftOld(brightnessFactor, p, a, b);
		}
	}

	
	private final double brightnessFactor;
	private final double p, a, b;
	
	
	
	public SoftOld(double brightnessFactor, double p, double a, double b) {
		this.brightnessFactor = brightnessFactor;
//		this.brightnessFactor = Math.random();
		
		this.p = p;
		this.a = a;
		this.b = b;
	}
	
	
	private Color toneMap(Color c) {
		double lSrc = c.luminance();
		double l = lSrc * brightnessFactor;

		double lDst = 0.072187 * Math.pow(1 - 1 / (1 + l), p);             // Scaled so that max(r,g,b) < 1
		
//		return c.mul(lDst / lSrc);                              // Scale to the new luminance. That's too dark.
		
		// Let's use remaining space to "brighten to white".
		
		double dl = l - lDst;
		
		double m = 1 - 1 / (1 + b * dl);
		double v = m * a;

		return c.mul((1 - v) * lDst / lSrc).add(Color.gray(v));
	}
	
	
	@Override
	public void toneMap(Color[][] pixelColors, int[] imageData) {
		int w = pixelColors[0].length;
		IntStream.range(0, pixelColors.length).parallel().forEach(y -> {
			for (int x = 0; x < pixelColors[y].length; x++) {
				imageData[y * w + x] = toneMap(pixelColors[y][x]).code();
			}
		});
	}
}
