package graphics3d.tonemappers;

import graphics3d.Color;
import graphics3d.ToneMapper;
import mars.drawingx.gadgets.annotations.DoNotDetectChanges;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.functions.interfaces.Function0;

import java.util.stream.IntStream;


public class SoftClamp implements ToneMapper {
	
	public static class Factory implements Function0<ToneMapper> {
		@DoNotDetectChanges
		@GadgetDoubleLogarithmic(p = 0x1p-16, q = 0x1p+16)
		double max = 1;

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
			return new SoftClamp(preFactor, max, p1, p2);
		}
	}

	
	private final double preFactor;
	private final double max;
	private final double p1;
	private final double p2;
	
	
	public SoftClamp(double preFactor, double max, double p1, double p2) {
		this.preFactor = preFactor;
		this.max = max;
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
	
	
	@Override
	public void toneMap(Color[][] pixelColors, int[] imageData) {
		int w = pixelColors[0].length;
		IntStream.range(0, pixelColors.length).parallel().forEach(y -> {
			for (int x = 0; x < pixelColors[y].length; x++) {
				double factor = lFactor(pixelColors[y][x].luminance());
				imageData[y * w + x] = pixelColors[y][x].mul(factor / max).codeClamp();
			}
		});
	}
}
