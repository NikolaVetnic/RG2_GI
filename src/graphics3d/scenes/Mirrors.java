package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Checkerboard;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function0;
import mars.geometry.Vector;

import java.util.List;

public class Mirrors extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetDouble
		double k = 1.0;
		
		@GadgetDouble
		double phi = 0.0;
		
		@GadgetDouble(p = -10, q = 10)
		double cameraZ = -3.0;
		
		@Override
		public Scene at() {
			return new Mirrors(k, phi, cameraZ);
		}
	}
	
	
	public Mirrors(double k, double phi, double cameraZ) {
		Material material1 = Material.diffuse(Color.gray(0.750));
		Material material2 = Material.diffuse(Color.gray(0.625));
		Material material1r = Material.diffuse(Color.hsb(  0, 0.75, 0.750));
		Material material2r = Material.diffuse(Color.hsb(  0, 0.75, 0.625));
		Material material1g = Material.diffuse(Color.hsb(120, 0.75, 0.750));
		Material material2g = Material.diffuse(Color.hsb(120, 0.75, 0.625));
		
		Material materialBall = new Material(Color.gray(1-k), Color.BLACK, 0, Color.gray(k), Color.BLACK, 1.5, Color.BLACK);
		
		
		Texture texture  = new Checkerboard(Vector.xy(0.25, 0.25), material1 , material2 );
		Texture textureR = new Checkerboard(Vector.xy(0.25, 0.25), material1r, material2r);
		Texture textureG = new Checkerboard(Vector.xy(0.25, 0.25), material1g, material2g);
		
		
		double r = 0.5;
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), textureR),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), textureG),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), texture),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), texture),
//		        Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), texture),
//				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), texture),
				
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 0.0/3), 0), 0.4), materialBall),
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 1.0/3), 0), 0.4), materialBall),
				Body.uniform(Ball.cr(Vec3.pz(Vector.polar(r, phi + 2.0/3), 0), 0.4), materialBall)
				
//				Body.uniform(Ball.cr(Vec3.xyz(-0.5, -0.5, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.5, -0.5, -0.5), 0.3), materialBall)
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0, -0.5), 0.3), materialBall),
//				Body.uniform(Ball.cr(Vec3.xyz( 0.0,  0.0,  0.5), 0.3), materialBall)
		));
		
		
		double d = 0.6;
		
		lights.addAll(List.of(
				Light.pc(Vec3.xyz(-d, 0.9, -d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz(-d, 0.9,  d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( d, 0.9, -d), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( d, 0.9,  d), Color.hsb(0, 0, 1))
		));
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, cameraZ))
		);
	}

}
