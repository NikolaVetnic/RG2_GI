package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;

import java.util.List;

public class Glass extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble
		double k = 0.96;
		
		@GadgetDouble(p = -0.1, q = 0.1)
		double phi = -0.04;
		
		@GadgetDouble(p = -10, q = 30)
		double wallZ = 1.0;

		@GadgetDouble(p = 0, q = 5)
		double refractiveIndex = 1.5;
		
		@Override
		public Scene at(Double time) {
			return new Glass(k, phi, wallZ, refractiveIndex);
		}
	}
	
	
	
	public Glass(double k, double phi, double wallZ, double refractiveIndex) {
		Material material1 = Material.diffuse(Color.gray(0.750));
		Material material2 = Material.diffuse(Color.gray(0.625));
		Material material1r = Material.diffuse(Color.hsb(  0, 0.75, 0.750));
		Material material2r = Material.diffuse(Color.hsb(  0, 0.75, 0.625));
		Material material1g = Material.diffuse(Color.hsb(120, 0.75, 0.750));
		Material material2g = Material.diffuse(Color.hsb(120, 0.75, 0.625));
		Material material1b = Material.diffuse(Color.hsb(240, 0.75, 0.750));
		Material material2b = Material.diffuse(Color.hsb(240, 0.75, 0.625));
		
		Material materialBall = new Material(Color.BLACK, Color.BLACK, 0, Color.gray(1-k), Color.gray(k), refractiveIndex, Color.BLACK);
		
		
		Texture texture  = new Checkerboard(Vector.xy(0.25, 0.25), material1 , material2 );
		Texture textureR = new Checkerboard(Vector.xy(0.25, 0.25), material1r, material2r);
		Texture textureG = new Checkerboard(Vector.xy(0.25, 0.25), material1g, material2g);
		Texture textureB = new Checkerboard(Vector.xy(0.25, 0.25), material1b, material2b);
		
		
		double r = 0.5;
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), textureR),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), textureG),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), texture),
//		        Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, wallZ), Vec3.xyz( 0, 0,-1)), textureB),
				
				Body.uniform(Ball.cr(Vec3.pz(Vector.ZERO, 0), 0.5), materialBall)

//				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 0.0/3), 0), 0.4), materialBall),
//				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 1.0/3), 0), 0.4), materialBall),
//				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 2.0/3), 0), 0.4), materialBall)
				
//				Body.uniform(Ball.cr(Vec3.xyz(-0.5, -0.5, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.5, -0.5, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0,  0.5), 0.3), materialBall)
		));
		
		
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
				.andThen(Transform.rotationAboutY(phi))
		;
	}
	
}
