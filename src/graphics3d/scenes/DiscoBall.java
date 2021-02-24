package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;

import java.util.List;

public class DiscoBall extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0.0, q = 3)
		double a = 1.4;
		
		@GadgetDouble(p = 0, q = 1)
		double b = 0.1;
		
		@Override
		public Scene at(Double time) {
			return new DiscoBall(time, a, b);
		}
	}
	
	
	public DiscoBall(double time, double a, double b) {
		Vec3 c = Vec3.xyz(0, 0.6, 0);
		{
			Solid solid = Ball.cr(c, 0.3).transformed(Transform.rotationAboutY(time));
			
			Material materialA = Material.ABSORPTIVE;
			Material materialB = Material.glass(1.0, 0.0);
			
			Texture texture = new Checkerboard(Vector.xy(0.1), materialA, materialB);
			bodies.add(Body.textured(solid, texture));
		}
		
		bodies.add(Body.uniform(Ball.cr(c, 0.02), Material.LIGHT));
		
		// Room
		
		{
			bodies.addAll(List.of(
					Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), Grid.standard(Color.hsb(  0, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), Grid.standard(Color.hsb(120, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), Grid.standard(Color.hsb(  0,   0, 0.75)))
			));
		}
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
				.andThen(Transform.rotationAboutX(0.03))
				.andThen(Transform.rotationAboutY(0.029))
		;
	}
}
