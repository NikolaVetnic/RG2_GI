package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.functions.interfaces.Function1;

import java.util.List;

public class TestGI extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@Override
		public Scene at(Double time) {
			return new TestGI();
		}
	}
	
	
	public TestGI() {
		colorBackground = Color.WHITE;
		
		bodies.addAll(List.of(
				Body.textured(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), Grid.standard(Color.hsb(  0, 0.7, 0.5))),
				Body.textured(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), Grid.standard(Color.hsb(120, 0.7, 0.5))),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), Grid.standard(Color.hsb(  0,   0, 0.5))),
				Body.textured(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), Grid.standard(Color.hsb(  0,   0, 0.5))),

				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT)
		));

		test();
//		oranges();
//		dice();
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
		;
	}

	
	void test() {
		bodies.addAll(List.of(
				Body.uniform (Ball.cr(Vec3.xyz( 0.6,  0.6, 0.6), 0.3), Material.light(Color.hsb(60, 0.8, 3.0))),
				Body.uniform (Ball.cr(Vec3.xyz( 0.0,  0.3,-0.1), 0.3), Material.diffuse(0.9)),
				Body.uniform (Ball.cr(Vec3.xyz(-0.2, -0.5, 0.0), 0.3), Material.mirror(0.9)),
				Body.uniform (Ball.cr(Vec3.xyz( 0.6, -0.5, 0.0), 0.3), Material.GLASS)
		));
	}
	
	
	void oranges() {
		int n = 4;
		double sqrt3 = Math.sqrt(3);
		
		Vec3 dI = Vec3.xyz(1.0, 0.0, 0.0);
		Vec3 dJ = Vec3.xyz(0.5, 0.0, sqrt3/2);
		Vec3 dK = Vec3.xyz(0.5, Math.sqrt(2.0/3.0), 1.0/(2*sqrt3));
		
		Vec3 o = dI.add(dJ.add(dK)).mul((n-1)/4.0);
		
		double d = 1.6 / n;
		double q = 0.125;
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; i+j < n; j++) {
				for (int k = 0; i+j+k < n; k++) {
					Vec3 c = dI.mul(i).add(dJ.mul(j)).add(dK.mul(k)).sub(o).mul(d).sub(Vec3.EY.mul(1-d/2-d*o.y()));
					bodies.add(Body.uniform(Ball.cr(c, d/2),
//								new Material(Color.gray(0.5), Color.BLACK, 0, Color.gray(0.5), Color.BLACK, 1.0, Color.BLACK)
							Material.GLASS
					));
				}
			}
		}
	}
	
	
	void dice() {
		Solid solidA = Box.$.r(0.5).transformed(Transform.rotationAboutX(0.1).andThen(Transform.rotationAboutY(0.6)));
		Solid solidB = Ball.cr(Vec3.xyz(0, 0, 0), 0.62);
		Solid solidC = Ball.cr(Vec3.xyz(0, 0, 0), 0.68);
		Solid solid = Solid.intersection(Solid.difference(solidA, solidB), solidC);
		
		Material material = Material.glass(Color.hsb(240, 0.25, 1.0), 1.4, 0.04);
//		Material material = Material.diffuse(0.9);
		bodies.add(Body.uniform(solid, material));
	}
	
}
