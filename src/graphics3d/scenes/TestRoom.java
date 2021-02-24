package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

import java.util.List;

public class TestRoom extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetLongRandomStream
		long seed = 0;
		
		@GadgetInteger(min = 0)
		int nBalls  = 8;

		@GadgetInteger(min = 0)
		int nLights = 8;
		
		@Override
		public Scene at(Double time) {
			return new TestRoom(seed, nLights, nBalls);
		}
	}

	
	
	public TestRoom(long seed, int nLights, int nBalls) {
		Sampler sampler = new Sampler(seed);
		Sampler samplerB = new Sampler(sampler.rng().nextLong());
		
		
		Material material1 = Material.diffuse(Color.gray(0.75));
//		Material material2 = Material.diffuse(Color.gray(0.50));
		Material material2 = Material.MIRROR;
		
		Texture texture = new Grid(
				Vector.xy(0.25, 0.25),
				Vector.xy(0.01, 0.01),
				material1,
				material2
		);
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), texture),
//		        Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), texture)
		));

		
		for (int i = 0; i < nBalls; i++) {
			Vec3 c = Vec3.xyz(
					samplerB.uniform(-1, 1),
					samplerB.uniform(-1, 1),
					samplerB.uniform(-1, 1)
			);
			double   r        = samplerB.uniform(0.1, 0.2);
			Ball     ball     = Ball.cr(c, r);
			Material material = new Material(
					Color.hsb(samplerB.uniform(0, 360), 0.75, 1.0),
					Color.WHITE,
					32,
					Color.gray(0.9),
					Color.BLACK,
					1.5, Color.BLACK);
			Body     body     = Body.uniform(ball, material);
			bodies.add(body);
		}

		
		Sampler samplerL = new Sampler(sampler.rng().nextLong());
		
		for (int i = 0; i < nLights; i++) {
			lights.add(
					Light.pc(
							Vec3.xyz(
									samplerL.uniform(-1, 1),
									samplerL.uniform(-1, 1),
									samplerL.uniform(-1, 1)
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
				Transform.translation(Vec3.xyz(0, 0, -3))
		);
	}
	
}
