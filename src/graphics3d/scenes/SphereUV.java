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

public class SphereUV extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0.0, q = 3)
		double a = 1.4;
		
		@GadgetDouble(p = 0, q = 1)
		double b = 0.1;
		
		@Override
		public Scene at(Double time) {
			return new SphereUV(time, a, b);
		}
	}
	
	
	public SphereUV(double time, double a, double b) {
		{
			Solid solid = Ball.cr(Vec3.xyz(0, 0, 0), 0.5).transformed(Transform.rotationAboutY(time));
			
			Material materialA = new Material(Color.hsb(240, 0.7, 1.0), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1, Color.BLACK);
			Material materialB = new Material(Color.hsb(240, 0.7, 0.5), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1, Color.BLACK);
			
			Texture texture = new Checkerboard(Vector.xy(0.1), materialA, materialB);
			bodies.add(Body.textured(solid, texture));
		}
		
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
		
		
		{ // Lights
			double s = 0.6;
			double h = 0.6;
			
			lights.addAll(List.of(
					Light.pc(Vec3.xyz(-s, h, -s), Color.hsb(0, 0, 1)),
					Light.pc(Vec3.xyz(-s, h,  s), Color.hsb(0, 0, 1)),
					Light.pc(Vec3.xyz( s, h, -s), Color.hsb(0, 0, 1)),
					Light.pc(Vec3.xyz( s, h,  s), Color.hsb(0, 0, 1))
			));
		}
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
				.andThen(Transform.rotationAboutX(0.03))
				.andThen(Transform.rotationAboutY(0.03))
		;
	}
}
