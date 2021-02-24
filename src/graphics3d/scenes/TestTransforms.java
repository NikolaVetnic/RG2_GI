package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;

import java.util.List;

public class TestTransforms extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetDouble
		double phiY = 0.0;
		
		@GadgetDouble
		double phiZ = 0.0;
		
		@Override
		public Scene at() {
			return new TestTransforms(phiY, phiZ);
		}
	}
	
	
	public TestTransforms(double phiY, double phiZ) {
		{
			Solid solid = Ball.cr(Vec3.ZERO, 1);
//			Solid solid = HalfSpace.pn(Vec3.ZERO, Vec3.EZ.inverse());

			Transform t = Transform.IDENTITY
					.andThen(Transform.scaling(Vec3.xyz(0.1, 0.4, 0.4)))
					.andThen(Transform.rotationAboutZ(phiZ))
					.andThen(Transform.rotationAboutY(phiY))
				;
			
			Solid solidT = solid.transformed(t);
			
			Material material = new Material(Color.hsb(60, 0.7, 1), Color.WHITE, 32, Color.BLACK, Color.BLACK, 1, Color.BLACK);
//			Material material = Material.GLASS;
			bodies.add(Body.uniform(solidT, material));
			
//			bodies.add(Body.textured(solidT, Grid.standard(Color.WHITE)));
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
					Light.pc(Vec3.xyz(-s, h, s), Color.hsb(0, 0, 1)),
					Light.pc(Vec3.xyz(s, h, -s), Color.hsb(0, 0, 1)),
					Light.pc(Vec3.xyz(s, h, s), Color.hsb(0, 0, 1))
			));
		}
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
		;
	}
}
