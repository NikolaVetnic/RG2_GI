package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;

public class TestIntegral extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0, q = 1)
		double phi = 0.1;
		
		@Override
		public Scene at(Double time) {
			return new TestIntegral(time, phi);
		}
	}
	
	
	public TestIntegral(double time, double phi) {
		colorBackground = Color.WHITE;
		
		bodies.add(
				Body.uniform(Ball.cr(Vec3.ZERO, 1), Material.diffuse(0.35))
//				Body.textured(
//						HalfSpace.pn(Vec3.xyz(0, -1, 0), Vec3.xyz(0, 1, 0))
//							.transformed(Transform.rotationAboutZ(phi)),
//						Grid.standardUnit(Color.gray(0.5))
//				)
		);
		
		cameraTransform = Transform.translation(Vec3.EZ.mul(-3));
	}
}
