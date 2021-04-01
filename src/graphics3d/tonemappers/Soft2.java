package graphics3d.tonemappers;

import graphics3d.Color;
import graphics3d.ToneMapper;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.functions.interfaces.Function0;

import java.util.stream.IntStream;


public class Soft2 implements ToneMapper {
	
	public static class Factory implements Function0<ToneMapper> {
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 0.001, q = 1000)
//		double preFactor = 1.0;
		double preFactor = .35;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 1.0/16, q = 16)
//		double p1 = 1.0;
		double p1 = 2.45;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 1.0/16, q = 16)
//		double p2 = 1.0;
		double p2 = .615;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 1.0/16, q = 16)
		double a = 1.0;
		
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 1.0/16, q = 16)
		double b = 1.0;
		
		@DoNotDetectChanges
		@GadgetDouble
		double postFactor = 0.072187;
		
		@DoNotDetectChanges
		@GadgetDouble
		double bonusLight = 0;
		
		
		@Override
		public ToneMapper at() {
			return new Soft2(preFactor, p1, p2, a, b, postFactor, bonusLight);
		}
	}

	
	private final double preFactor;
	private final double p1;
	private final double p2;
	private final double a;
	private final double b;
	private final double postFactor;
	private final double bonusLight;
	
	
	public Soft2(double preFactor, double p1, double p2, double a, double b, double postFactor, double bonusLight) {
		this.preFactor = preFactor;
		this.p1 = p1;
		this.p2 = p2;
		this.a = a;
		this.b = b;
		this.postFactor = postFactor;
		this.bonusLight = bonusLight;
	}
	
	
	private Color toneMap(Color c) {
		double lSrc = c.luminance();
		
		double lPre = lSrc * preFactor;
		double l = Math.pow(1 - a / (1 + b * Math.pow(lPre, p1)), p2);
		double lDst = l * postFactor;

		
		double dl = lPre - l;
		double v = bonusLight * dl / lPre;
		
		
		double f = lDst / lSrc;
		if (Double.isNaN(f)) {
			f = 0;
		}
		return c.mul((1 - v) * f).add(Color.gray(v));
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
