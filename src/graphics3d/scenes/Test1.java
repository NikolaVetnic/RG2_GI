package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;

import java.util.List;

public class Test1 extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetDouble(p = 0, q = 12)
		double z = 10;
		
		@Override
		public Scene at(Double time) {
			return new Test1(time, z);
		}
	}

	
	
	public Test1(double time, double z) {
		bodies.addAll(List.of(
				Body.uniform(Ball.cr(Vec3.xyz(-5,-4, 5), 6)),
				Body.uniform(Ball.cr(Vec3.xyz( 0, 0, 7), 1)),
				Body.uniform(Ball.cr(Vec3.xyz (2, 2,10), 2)),
				
				Body.uniform(HalfSpace.pn(Vec3.xyz(0, -1, 0), Vec3.xyz(0, 1, 0))),
				Body.uniform(HalfSpace.pn(Vec3.xyz(-2, 0, 0), Vec3.xyz(1, 0, 0))),
				Body.uniform(HalfSpace.pn(Vec3.xyz(0, 0, time), Vec3.xyz(0, 0, -1)))
		));

		lights.addAll(List.of(
				Light.pc(Vec3.xyz(-1, 0, 8), Color.hsb(0, 0.75, 1.0)),
				Light.pc(Vec3.xyz(2, 1, 6), Color.hsb(120, 0.75, 1.0)),
				Light.pc(Vec3.xyz(1, 2, 3), Color.hsb(240, 0.75, 1.0))
		));
	}
	
}
