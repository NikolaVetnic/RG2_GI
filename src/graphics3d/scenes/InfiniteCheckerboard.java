package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;

import java.util.List;

public class InfiniteCheckerboard extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetDouble(p = -10, q = 10)
		double y = 1.0;
		
		@GadgetDouble(p = -1, q = 1)
		double phiX = 0;
		
		@GadgetDouble(p = -1, q = 1)
		double phiY = 0;
		
		@GadgetDouble(p = -1, q = 1)
		double phiZ = 0;
		
		
		@Override
		public Scene at() {
			return new InfiniteCheckerboard(y, phiX, phiY, phiZ);
		}
	}
	
	
	public InfiniteCheckerboard(double y, double phiX, double phiY, double phiZ) {
		Material material1 = Material.diffuse(Color.BLACK);
		Material material2 = Material.diffuse(Color.WHITE);
		
		Texture texture = new Checkerboard(Vector.xy(1, 1), material1, material2);
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(0, 0, 0), Vec3.xyz( 0, 1, 0)), texture)
		));
		
		lights.addAll(List.of(
				Light.pc(Vec3.xyz(0, 10, 0), Color.hsb(0, 0, 1000))
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, y, 0)))
				.andThen(Transform.rotationAboutX(phiX))
				.andThen(Transform.rotationAboutY(phiY))
				.andThen(Transform.rotationAboutZ(phiZ))
		;
	}
	
}
