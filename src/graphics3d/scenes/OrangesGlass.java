package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function0;

import java.util.List;

public class OrangesGlass extends SceneBase {
	
	public static class Factory implements Function0<Scene> {
		@GadgetInteger(min = 0)
		int n = 5;

		@Override
		public Scene at() {
			return new OrangesGlass(n);
		}
	}
	
	
	public OrangesGlass(int n) {
		
		// Bodies
		
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
					bodies.add(Body.uniform(Ball.cr(c, d/2), Material.GLASS));
				}
			}
		}
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz(-1, 0, 0), Vec3.xyz( 1, 0, 0)), new Material(Color.hsb(  0, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 1, 0, 0), Vec3.xyz(-1, 0, 0)), new Material(Color.hsb(240, 0.7, 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-1, 0), Vec3.xyz( 0, 1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 1, 0), Vec3.xyz( 0,-1, 0)), new Material(Color.hsb(  0, 0  , 0.8))),
//		        Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,-1), Vec3.xyz( 0, 0, 1)), new Material(Color.hsb(  0, 0  , 0.8))),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 1), Vec3.xyz( 0, 0,-1)), new Material(Color.hsb(120, 0.7, 0.8)))
		));
		
		
		// Lights
		
		double s = 0.6;
		double h = 0.6;
		
		lights.addAll(List.of(
				Light.pc(Vec3.xyz(-s, h, -s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz(-s, h,  s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( s, h, -s), Color.hsb(0, 0, 1)),
				Light.pc(Vec3.xyz( s, h,  s), Color.hsb(0, 0, 1))
		));

		
		// Camera
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -3)))
		;
	}
	
}
