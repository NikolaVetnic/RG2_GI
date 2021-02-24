package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.ImageTexture;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;

import java.util.List;

public class TestImageTextures extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble
		private double phiX;
		
		@GadgetDouble
		private double phiY;
		
		@Override
		public Scene at(Double time) {
			return new TestImageTextures(time, phiX, phiY);
		}
	}
	
	private static Texture textureFloor, textureBall;
	
	
	public TestImageTextures(double time, double phiX, double phiY) {
		if (textureFloor == null) {
			textureFloor = ImageTexture.fromFile("resources/hull.jpg");
			textureBall  = ImageTexture.fromFile("resources/basketball 2.png");
		}
		
		colorBackground = Color.hsb(230, 0.3, 1.0);
		
		bodies.addAll(List.of(
				Body.uniform (Ball.cr(Vec3.xyz( 1.6,  1.6, -1.6), 0.6), Material.light(Color.hsb(60, 0.8, 60.0))),
				
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), textureFloor),
				
				Body.textured(
						Ball.cr(Vec3.ZERO, 0.5).transformed(Transform.IDENTITY
								.andThen(Transform.rotationAboutY(phiY))
								.andThen(Transform.rotationAboutX(phiX))
								.andThen(Transform.translation(Vec3.xyz( 0.0,  0.4, 0.0)))
						),
						textureBall
				),
				
				Body.uniform (Ball.cr(Vec3.xyz(-0.2, -0.5, 0.0), 0.3), Material.mirror(0.9)),
				
				Body.uniform (Ball.cr(Vec3.xyz( 0.6, -0.5, 0.0), 0.3), Material.GLASS)
		));
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
		;
	}
	
	
}
