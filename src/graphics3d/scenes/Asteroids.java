package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Asteroids extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetInteger
		int seed = 129832194;

		@GadgetDouble(p = 0, q = 5)
		double y = 1.0;
		
		@GadgetDouble(p = -10, q = 10)
		double z = -2;
		
		@GadgetDouble(p = 0.0, q = 360.0)
		double colorSlider = 90.0;
		
		@Override
		public Scene at(Double time) {
			return new Asteroids(y, z, seed, colorSlider);
		}
	}
	
	
	public static Vec3 rtp(double r, double theta, double phi) {
		double x = r * Numeric.cosT(phi) * Numeric.sinT(theta);
		double y = r * Numeric.sinT(phi) * Numeric.sinT(theta);
		double z = r * Numeric.cosT(theta);
		
		return Vec3.xyz(x, y, z);
	}
	
	public Asteroids(double y, double z, int seed, double colorSlider) {

		Random rng = new Random(seed);
		
		Vec3 baseCenter = Vec3.xyz(0, 0, 3);
		
		Material whiteSpecular = new Material(Color.gray(1.00), Color.gray(1.00), 32);
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz(-2, 0, 0), Vec3.xyz( 1, 0, 0)), new Material(Color.hsb(  0, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 2, 0, 0), Vec3.xyz(-1, 0, 0)), new Material(Color.hsb(240, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-2, 0), Vec3.xyz( 0, 1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 2, 0), Vec3.xyz( 0,-1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
		        Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 8), Vec3.xyz( 0, 0,-1)), new Material(Color.hsb(  0, 0  , 0.8)))
//				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 2), Vec3.xyz( 0, 0, 1)), new Material(Color.hsb(120, 0.7, 0.8)))
		));
		
		double r = y;
		Ball base = Ball.cr(baseCenter, r);
		
		Solid res = base;
		int maxBalls = 100;
		
		for (int i = 0; i < maxBalls; i++) {
			Ball tmp = Ball.cr(rtp(r * 0.95 + rng.nextDouble(), rng.nextDouble(), rng.nextDouble()).add(baseCenter), rng.nextDouble() * .75 + .25);
			res = Solid.difference(res, tmp);
		}

		bodies.add(Body.uniform(res, Material.GLASS));
		
		// lights
		int centralColor = (int) colorSlider;	// when range is 360 it does nothing but offset the color
		int centralColorRange = 240; 			// when 360 it does nothing
		int lightsNum = 5;
		for (int i = 0; i < lightsNum; i++)
			lights.add(
					Light.pc(
							rtp(r + 1.5, rng.nextDouble(), rng.nextDouble()).add(baseCenter),
							Color.hsb((rng.nextInt(centralColorRange) + centralColor) % 360, 0.95, .95))
					);
	}
}
