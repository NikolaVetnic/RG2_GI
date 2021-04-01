package graphics3d.tonemappers;

import graphics3d.Color;
import graphics3d.ToneMapper;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.functions.interfaces.Function0;

import java.util.stream.IntStream;


public class Auto implements ToneMapper {
	
	public static class Factory implements Function0<ToneMapper> {
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 0x1p-16, q = 0x1p+16)
//		double preFactor = 1.0;
		double preFactor = .35;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 0x1p-4, q = 0x1p+4)
//		double p1 = 1.0;
		double p1 = 2.45;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 0x1p-4, q = 0x1p+4)
//		double p2 = 1.0;
		double p2 = .615;

		@Override
		public ToneMapper at() {
			return new Auto(preFactor, p1, p2);
		}
	}

	
	private final double preFactor;
	private final double p1;
	private final double p2;
	
	
	public Auto(double preFactor, double p1, double p2) {
		this.preFactor = preFactor;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	
	private double lFactor(double lSrc) {
		double lPre = lSrc * preFactor;
		double lDst = Math.pow(1 - 1 / (1 + Math.pow(lPre, p1)), p2);
		
		double f = lDst / lSrc;
		if (Double.isNaN(f)) {
			f = 0;
		}
		return f;
	}
	
	
	private double[] factors;
	
	@Override
	public void toneMap(Color[][] pixelColors, int[] imageData) {
		int w = pixelColors[0].length;

		if (factors == null || factors.length != imageData.length) {
			factors = new double[imageData.length];
		}
		
		double[] maxX = new double[pixelColors.length];
		
		IntStream.range(0, pixelColors.length).parallel().forEach(y -> {
			maxX[y] = Double.NEGATIVE_INFINITY;
			for (int x = 0; x < w; x++) {
				double factor = lFactor(pixelColors[y][x].luminance());
				Color result = pixelColors[y][x].mul(factor);
				maxX[y] = Math.max(maxX[y], result.max());
				factors[y * w + x] = factor;
			}
		});
		
		double max = Double.NEGATIVE_INFINITY;
		for (int y = 0; y < pixelColors.length; y++) {
			max = Math.max(max, maxX[y]);
		}
		
		double max_ = max;
		
		IntStream.range(0, pixelColors.length).parallel().forEach(y -> {
			for (int x = 0; x < w; x++) {
				int k = y * w + x;
				imageData[k] = pixelColors[y][x].mul(factors[k] / max_).code();
			}
		});
	}
}
