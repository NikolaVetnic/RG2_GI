package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;

import java.util.List;

public class TestCSG extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0.0, q = 3)
		double a = 1.4;
		
		@GadgetDouble(p = 0, q = 1)
		double b = 0.1;
		
		@Override
		public Scene at(Double time) {
			return new TestCSG(time, a, b);
		}
	}
	
	
	public TestCSG(double time, double a, double b) {
		{
			Solid solidA = Box.$.r(0.5).transformed(Transform.rotationAboutX(0.1).andThen(Transform.rotationAboutY(b+time)));
			Solid solidB = Ball.cr(Vec3.xyz(0, 0, 0), 0.62);
			Solid solidC = Ball.cr(Vec3.xyz(0, 0, 0), 0.68);
			Solid solid = Solid.intersection(Solid.difference(solidA, solidB), solidC);
			
//			Material material = new Material(Color.hsb(60, 0.7, 1), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1);
			Material material = Material.GLASS;
			bodies.add(Body.uniform(solid, material));
		}
		
		// Room
		
		{
			bodies.addAll(List.of(
					Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), Grid.standard(Color.hsb(  0, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), Grid.standard(Color.hsb(120, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), Grid.standard(Color.hsb(240, 0.7, 0.75)))
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
		;
	}
}
