package graphics3d.renderers.adhoc;

import graphics3d.Color;
import graphics3d.Renderer;
import graphics3d.Scene;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;

import java.util.stream.IntStream;

public class Oklab implements Renderer {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDouble(p = 0, q = 1)
		double h = 1;
		
		@GadgetDouble(p = 0, q = 1)
		double c = 0;
		
		@GadgetDouble(p = 0, q = 1)
		double l = 1;
		
		@GadgetDouble(p = 0, q = 1)
		double ll = 0.726;
		
		@GadgetDouble(p = 0, q = 1)
		double cl = 0.123;
		
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new Oklab(imageSize, h, c, l, ll, cl);
		}
	}
	
	
	
/*
	private static final Vector size = Vector.xy(640);
	private static final boolean[][] ok = new boolean[size.yInt()][size.xInt()];
	static {
//		IntStream.range(0, size.yInt()).parallel().forEach(y -> { // hangs!?
		for (int y = 0; y < size.yInt(); y++) {
			for (int x = 0; x < size.xInt(); x++) {
				boolean o = true;
				for (double h_ = 0; h_ < 1; h_ += 0.01) {
					double dy = 1.0 * y / size.yInt();
					double dx = 1.0 * x / size.xInt();
					Color cl = Color.oklabPolar(h_, dx, dy);
					if (cl.zero()) {
						o = false;
						break;
					}
				}
				ok[y][x] = o;
			}
		}
	};
*/
	
	
	private final Vector imageSize;
	private final double h, c, l, ll, cl;

	
	public Oklab(Vector imageSize, double h, double c, double l, double ll, double cl) {
		this.imageSize = imageSize;
		this.h = h;
		this.c = c;
		this.l = l;
		this.ll = ll;
		this.cl = cl;
	}
	
	@Override
	public Vector imageSize() {
		return imageSize;
	}
	
	
	@Override
	public boolean goodEnough() {
		return true;
	}
	
	
	public Color oklabStretchedSafePolar(double h, double c, double l) {
		double k = l < ll ? l / ll : (1-l) / (1-ll);
		return Color.oklabPolar(h, c * cl * k, l);
	}
	
	
	
	@Override
	public void renderIteration(Color[][] pixelColors) {
		IntStream.range(0, imageSize.yInt()).parallel().forEach(y -> {
			for (int x = 0; x < imageSize.xInt(); x++) {
				double dy = 1.0 * y / imageSize().yInt();
				double dx = 1.0 * x / imageSize().xInt();
//				if (ok[y][x]) {
//					pixelColors[y][x] = Color.oklabPolar(h, dx, dy);
//				}
				pixelColors[y][x] = Color.oklabPolar(dx, c, dy);
//				pixelColors[y][x] = oklabStretchedSafePolar(dx, c, dy);
			}
		});
	}
	
}
