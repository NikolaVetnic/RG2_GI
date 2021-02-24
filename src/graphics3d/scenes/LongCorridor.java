package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;
import mars.utils.Numeric;

import java.util.List;

public class LongCorridor extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetLongRandomStream
		long seed = 0;
		
		@GadgetInteger(min = 0)
		int nBalls  = 48;

		@GadgetInteger(min = 0)
		int nLights = 16;
		
		@GadgetDouble(p = -10, q = 10)
		double z = -8;
		
		@Override
		public Scene at(Double time) {
			return new LongCorridor(time, seed, nLights, nBalls, z);
		}
	}


	
	public LongCorridor(double time, long seed, int nLights, int nBalls, double z) {
		Sampler sampler = new Sampler(seed);
		Sampler samplerB = new Sampler(sampler.rng().nextLong());
		
		Texture texture = new Grid(
				Vector.xy(0.25, 0.25),
				Vector.xy(0.01, 0.01),
				Material.diffuse(Color.gray(1.00)),
				Material.diffuse(Color.gray(0.75))
		);
		
		bodies.addAll(List.of(
			Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), texture),
			Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), texture),
			Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), texture),
			Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), texture)
		));

		
		Material material = Material.MIRROR;
		
		for (int i = 0; i < nBalls; i++) {
//			Vec3 c = Vec3.xyz(
//					samplerB.uniform(-1, 1),
//					samplerB.uniform(-1, 1),
//					samplerB.uniform(-7, 7)
//			);
//			double r = samplerB.uniform(0.1, 0.2);
//			Ball     ball     = Ball.cr(c, r);
//			Color    color    = Color.hsb(samplerB.uniform(0, 360), 0.75, 1);
//			Material material = Material.diffuse(color);
//			Body     body     = Body.uniform(ball, material);
//			bodies.add(body);
			
			
			bodies.add(
					Body.uniform(
							Ball.cr(
									Vec3.xyz(
											samplerB.uniform(-1, 1),
											samplerB.uniform(-1, 1),
											samplerB.uniform(-7, 7)
									),
									samplerB.uniform(0.1, 0.2)
							),
							material
//							Material.diffuse(
//									Color.hsb(
//											samplerB.uniform(0, 360),
//											0.75,
//											1.0
//									)
//							)
					)
			);
		}

		
		Sampler samplerL = new Sampler(sampler.rng().nextLong());
		
		for (int i = 0; i < nLights; i++) {
			lights.add(
					Light.pc(
							Vec3.xyz(
									samplerL.uniform(-1, 1),
									samplerL.uniform(-1, 1),
									samplerL.uniform(-9, 9)
							),
							Color.hsb(
									samplerL.uniform(0, 360),
									0.75,
									1.0
							)
					)
			);
		}
		
		
		cameraTransform = Transform.IDENTITY.andThen(
				Transform.translation(Vec3.xyz(0, 0, Numeric.interpolateLinear(-10, 10, time / 10)))
		);
	}
	
}
