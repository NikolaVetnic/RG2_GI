package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Cylinder;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;

import java.util.List;

public class TestCylinder extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0, q = 1)
		double a = 0.3;
		
		@GadgetDouble(p = -1, q = 1)
		double b = 0.6;
		
		@Override
		public Scene at(Double time) {
			return new TestCylinder(a, b);
		}
	}
	
	
	public TestCylinder(double a, double b) {
		{
			Solid solidA = Cylinder.pdr(Vec3.xyz(0, 0, 0), Vec3.EX, a);
			Solid solidB = Cylinder.pdr(Vec3.xyz(0, 0, 0), Vec3.EY, a);
			Solid solidC = Cylinder.pdr(Vec3.xyz(0, 0, 0), Vec3.EZ, a);
			
			Solid solid =
				Solid.difference(
					Solid.union(solidA, solidB, solidC),
					Ball.cr(Vec3.xyz(0, 0, 0), b)
				);
			
//			Material material = new Material(Color.hsb(240, 0.7, 1.0), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1);
			Material material = Material.MIRROR;
			
			bodies.add(Body.uniform(solid, material));
		}
		
		// Room
		
		{
			bodies.addAll(List.of(
					Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), Grid.standard(Color.hsb(  0, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), Grid.standard(Color.hsb(120, 0.7, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), Grid.standard(Color.hsb(  0,   0, 0.75))),
					Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), Grid.standard(Color.hsb(  0,   0, 0.75)))
			));
		}
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -1.5)))
				.andThen(Transform.rotationAboutX(0.1))
				.andThen(Transform.rotationAboutY(0.1))
		;
	}
}
