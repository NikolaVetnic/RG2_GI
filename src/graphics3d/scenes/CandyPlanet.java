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
import java.util.Random;

public class CandyPlanet extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {

//		@GadgetDouble(p = -5., q = 5.)
		double y = 1.0;
		
//		@GadgetDouble(p = -10, q = 10)
		double z = 1;

//		@GadgetDouble(p = 0.1, q = 5.)
		double rr = 1;

		@GadgetInteger(min = 1, max = 12)
		int numColors = 5;

		@GadgetDouble(p = 0.0, q = 360.0)
		double colorSlider = 0.0;
		
		@Override
		public Scene at(Double time) {
			return new CandyPlanet(time, y, z, rr, numColors, colorSlider);
		}
	}
	
	
	
	
	public boolean intersectingAnother(Ball a, Ball b) {
		double l = a.r() + b.r();
		return a.c().sub(b.c()).lengthSquared() < l * l;
	}
	
	
	public Vec3 rtp(double r, double theta, double phi) {
		
		double x = r * Numeric.cosT(theta) * Numeric.sinT(phi);
		double y = r * Numeric.sinT(theta) * Numeric.sinT(phi);
		double z = r * Numeric.cosT(phi);
		
		return new Vec3(x, y, z);
	}

	
	
	
	public CandyPlanet(double time, double y, double z, double rr, int numColors, double colorSlider) {
		
		Random rng = new Random(12983219075175143L);
		
		int ballCount = 0, maxBalls = (int) 300;
		int iterCount = 0, maxIters = (int) 1e4;
		
		// backdrop
		Material whiteDiffuse = new Material(Color.gray(1));
//		bodies.add(Body.uniform(HalfSpace.pn(Vec3.xyz(0, 0, 4), Vec3.EZ), whiteDiffuse));
		lights.add(Light.pc(Vec3.xyz(0, 0, 3), Color.gray(0.95)));
		
		// main sphere parameters
		Vec3 center = Vec3.xyz(0.5, -0.5, 2.5);
		double r = 1.35;
		Material whiteSpecular = new Material(Color.gray(1.00), Color.gray(1.00), 32);
		
		bodies.add(Body.uniform(Ball.cr(center, r), whiteSpecular));
		
		// small spheres
			
		Color[] smallBallsColor = new Color[numColors];
		for (int i = 0; i < numColors; i++)
			smallBallsColor[i] = Color.hsb((int) (360.0 * i / numColors), 0.95, 1.00);
		
		double smallBallsSizeFactor = 0.3;
		
		ArrayList<Ball> balls = new ArrayList<>();
		
		outer:
		while (ballCount < maxBalls && iterCount < maxIters) {
			
			// create new small ball on the surface of the bigger one
			Ball tmp = Ball.cr(rtp(
							r, rng.nextDouble(), rng.nextDouble()).add(center), 
							rng.nextDouble() * smallBallsSizeFactor);
			
			// check if any of the small balls intersects with any other
			for (int i = 0; i < ballCount; i++)
				if (intersectingAnother(tmp, balls.get(i))) {
					iterCount++;
					continue outer;
				}
			
			// if there is no intersection the ball is added to the list
			balls.add(tmp);
			ballCount++;
			iterCount = 0;
		}
			
		
		for (int i = 0; i < maxBalls; i++)
			bodies.add(
					Body.uniform(
							balls.get(i), 
							new Material(smallBallsColor[rng.nextInt(numColors)], Color.gray(1.00), 64))
					);
		
		// lights
		lights.add(Light.pc(Vec3.xyz(0.10, 0.75, 1.00), Color.gray(1.00)));
		
		int centralColor = (int) colorSlider;
		int lightsNum = 20;
		for (int i = 0; i < lightsNum; i++)
			lights.add(
					Light.pc(
							rtp(rr + 1.5, rng.nextDouble(), rng.nextDouble()).add(center),
							Color.hsb((rng.nextInt(60) + centralColor) % 360, 0.95, .65))
					);
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(center.inverse()))
				.andThen(Transform.rotationAboutX(time/5))
				.andThen(Transform.translation(center))
				;
		
		colorBackground = Color.WHITE;
	}

}
