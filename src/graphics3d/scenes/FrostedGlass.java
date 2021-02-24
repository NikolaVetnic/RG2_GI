package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

import java.util.List;

public class FrostedGlass extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble(p = -2, q = 2)
		double x = -0.3;
		
		@GadgetDouble(p = -3, q = 1)
		double z = -1;

		@GadgetDouble
		double s = 0.04;
		
		@Override
		public Scene at(Double time) {
			return new FrostedGlass(x, Numeric.interpolateLinear(-0.4, -4, time), s);
		}
	}
	
	
	public FrostedGlass(double x, double z, double s) {
		colorBackground = Color.WHITE;
		
		Material mGlass = new Material(BSDF.glossyRefractive(Color.hsb(210, 0.2, 0.9), 1.4, s));
		Material mFloor = new Material(BSDF.glossy(Color.hsb(  0, 0.0, 0.7), 0.4));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), mFloor),
				
				Body.uniform(Box.$.cr(Vec3.xyz(x, 0, z), Vec3.xyz(0.4, 0.4, 0.06)), mGlass))
		);

//		test();
		oranges();
//		dice();
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, -1, -3.4)))
				.andThen(Transform.rotationAboutX(0.08))
		;
	}

	
	void oranges() {
		int n = 4;
		double sqrt3 = Math.sqrt(3);
		
		Vec3 dI = Vec3.xyz(1.0, 0.0, 0.0);
		Vec3 dJ = Vec3.xyz(0.5, 0.0, sqrt3/2);
		Vec3 dK = Vec3.xyz(0.5, Math.sqrt(2.0/3.0), 1.0/(2*sqrt3));
		
		Vec3 o = dI.add(dJ.add(dK)).mul((n-1)/4.0);
		
		double d = 1.6 / n;
		
		Material material = new Material(BSDF.reflective(Color.hsb(30, 0.6, 0.7)));
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; i+j < n; j++) {
				for (int k = 0; i+j+k < n; k++) {
					Vec3 c = dI.mul(i).add(dJ.mul(j)).add(dK.mul(k)).sub(o).mul(d).sub(Vec3.EY.mul(1-d/2-d*o.y()));
					bodies.add(Body.uniform(Ball.cr(c, d/2), material));
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
