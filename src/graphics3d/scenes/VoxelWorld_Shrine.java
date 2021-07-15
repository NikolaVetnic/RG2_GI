package graphics3d.scenes;

import java.io.IOException;
import java.util.List;

import graphics3d.BSDF;
import graphics3d.Body;
import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Scene;
import graphics3d.Solid;
import graphics3d.Transform;
import graphics3d.Vec3;
import graphics3d.solids.HalfSpace;
import graphics3d.solids.voxelworld.GridMarch2Opt;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class VoxelWorld_Shrine extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble(p = -.5, q = 0.5)
		double px = 0.0;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double py = 0.0;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double pz = 0.0;

		@GadgetDouble(p = -50, q = 50)
		double dx = 0.0;
		
		@GadgetDouble(p = -50, q = 50)
		double dy = 0.0;
		
		@GadgetDouble(p = -50, q = 50)
		double dz = -1.5;

		@GadgetDouble(p = 0, q = 5.0)
		double s = 0.5;

		@GadgetDouble(p = 0, q = 0.125)
		double cameraAngle = 0.0;
		
		@Override
		public Scene at(Double time) {
			try {
				return new VoxelWorld_Shrine(
						cameraAngle, 
						px, py, pz,
						dx, dy, dz, 
						s, Numeric.interpolateLinear(-0.4, -4, time));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	public VoxelWorld_Shrine(
			double 	cameraAngle, 
			double 	px, 	double 	py, 		double 	pz,
			double 	dx, 	double 	dy, 		double 	dz, 
			double 	s, 		double 	z) throws IOException {
		
		GridMarch2Opt shrine = GridMarch2Opt.set("img/voxel_set_03/shrine_000.bmp");
		
		Transform transform = Transform.IDENTITY
				.andThen(Transform.scaling(10 * s / shrine.len().max()))
				.andThen(Transform.translation(Vec3.EXYZ.div(-2.0).add(Vec3.xyz(dx, dy, dz))))
				.andThen(Transform.rotationAboutX(px - 0.25))
				.andThen(Transform.rotationAboutY(py + .125))
				.andThen(Transform.rotationAboutZ(pz + 0.00));
		
		Solid shrineTransformed = shrine.transformed(transform);

		Material mDiffuseK 	= new Material(BSDF.diffuse(Color.hsb(  0, 0.0, 0.0)));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,25, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,25), Vec3.xyz( 0, 0,-1)), mDiffuseK	 ),

				Body.v(shrineTransformed, shrine.model())
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
