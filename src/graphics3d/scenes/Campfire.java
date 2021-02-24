package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import graphics3d.textures.Grid;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;

public class Campfire extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetInteger(min = 0)
		int n = 12;
		
		@GadgetDouble(p = 0, q = 256)
		double shininess = 32;

		@Override
		public Scene at(Double time) {
			return new Campfire(n, shininess);
		}
	}

	
	public Campfire(int n, double shininess) {
		Texture texture = new Grid(
				Vector.xy(1.00,1.00),
				Vector.xy(0.05,0.05),
				Material.diffuse(Color.gray(0.8)),
				Material.diffuse(Color.gray(0.6))
		);
		bodies.add(Body.textured(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), texture));
		
		for (int i = 0; i < n; i++) {
			double phi = 1.0 * i / n;
			Vector pxz = Vector.polar(6, phi);
			Vec3 c = Vec3.xyz(pxz.x(), 0, pxz.y());

			bodies.add(
					Body.uniform(
							Ball.cr(c, 1.0),
							new Material(
									Color.oklabPolar(phi, 0.12, 0.75),
									Color.WHITE,
									shininess,
									Color.BLACK,
									Color.BLACK,
									1.5, Color.BLACK)
					)
			);
		}
		
		for (int j = 0; j < 6; j++) {
			lights.add(Light.pc(Vec3.xyz(0, 2*j, 0), Color.gray(1.0)));
		}
		
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.rotationAboutX(0.13))
				.andThen(Transform.translation(Vec3.xyz(0, 16, -16)))
				;
	}
	
}
