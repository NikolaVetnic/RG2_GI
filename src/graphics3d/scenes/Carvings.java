package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function1;
import mars.random.sampling.Sampler;

import java.util.List;

public class Carvings extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetInteger(min = 0)
		int n = 100;
		
		@GadgetLongRandomStream
		long seed = 0;
		
		@GadgetDouble
		double a = 0.5;
		
		@GadgetDouble
		double s = 0.04;
		
		@Override
		public Scene at(Double time) {
			return new Carvings(time, n, a, s, seed);
		}
	}
	
	public Carvings(double time, int n, double a, double s, long seed) {
		Sampler sampler = new Sampler(seed);
		
		colorBackground = Color.WHITE;
		
		bodies.addAll(List.of(
//				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 100, 0), Vec3.xyz( 0,-1, 0)), Material.light(Color.hsb(0, 0.5, 1))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, -1, 0), Vec3.xyz( 0, 1, 0)), Material.diffuse(0.8))
				
//				Body.uniform(Ball.cr(Vec3.ZERO, 0.2), Material.LIGHT)
		));

//		Solid base = Box.$.r(1);
		Solid base = Ball.cr(Vec3.ZERO, 1);
		
		Solid[] cuts = new Solid[n];
		for (int i = 0; i < n; i++) {
			cuts[i] = Box.$.cr(Box.$.r(1).random(sampler), Box.$.r(a).random(sampler));
//			cuts[i] = Ball.cr(Box.$.r(1).random(sampler), sampler.uniform(a));
		}
		
		Solid solid = Solid.difference(base, Solid.union(cuts));
		Material material =
//				new Material(BSDF.mix(
//						BSDF.glossy(Color.hsb(240, 0.0, 0.7), s),
//						BSDF.diffuse(Color.hsb(240, 0.6, 0.7)),
//						0.5
//				));
				new Material(BSDF.mix(
					BSDF.reflective(0.8),
					BSDF.diffuse(Color.hsb(0, 0.5, 0.8)),
					0.5
				));
//				Material.diffuse(Color.hsb(240, 0.6, 0.8));
//				Material.mirror(0.6);
//				Material.glass(Color.hsb(240, 0.125, 1.0), 1.4, 0.04);
//				Material.diffuse(0.9);
		
		bodies.add(Body.uniform(solid, material));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
				.andThen(Transform.rotationAboutX(0.08))
				.andThen(Transform.rotationAboutY(0.1 + time))
		;
	}
	
}
