package graphics3d.scenes;

import java.io.IOException;
import java.util.List;

import graphics3d.BSDF;
import graphics3d.Body;
import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Scene;
import graphics3d.Transform;
import graphics3d.Vec3;
import graphics3d.solids.HalfSpace;
import graphics3d.solids.voxelworld.m.OctreeRecO;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class VoxelWorld_TestMap_Medium extends SceneBase {

	public static class Factory implements Function1<Scene, Double> {

		@GadgetDouble(p = -.5, q = 0.5) double rotateX = 0.0625;
		@GadgetDouble(p = -.5, q = 0.5) double rotateY = 0.0625;
		@GadgetDouble(p = -.5, q = 0.5) double rotateZ = -0.125;

		@GadgetDouble(p = -1000, q = 1000) double translateX = 0.0;
		@GadgetDouble(p = -1000, q = 1000) double translateY = 0.0;
		@GadgetDouble(p = -1000, q = 1000) double translateZ = 20.;

		@GadgetInteger(min = 0, max = 10) int xInt = 0;
		@GadgetInteger(min = 3, max = 10) int yInt = 0;
		@GadgetInteger(min = 3, max = 10) int zInt = 0;

		@GadgetDouble(p = 0, q = 5.0) double scale = 1.5 / 512;

		@GadgetInteger int seed = 129832191;

		@GadgetDouble(p = 0, q = 0.125) double cameraAngle = 0.0;

		@Override public Scene at(Double time) {
			try {
				return new VoxelWorld_TestMap_Medium(
						seed, cameraAngle,
						rotateX, rotateY, rotateZ,
						xInt, yInt, zInt,
						translateX, translateY, translateZ,
						scale, Numeric.interpolateLinear(-0.4, -4, time));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	@SuppressWarnings("unused")
	public VoxelWorld_TestMap_Medium(
			int 	seed, 		double 	cameraAngle, 
			double 	rotateX, 	double 	rotateY, 		double 	rotateZ, 
			int 	xInt, 		int 	yInt, 			int 	zInt, 
			double 	translateX, double 	translateY, 	double 	translateZ, 
			double 	scale, 		double 	z) throws IOException {
		
		String path = "img//ares-vallis_256.jpg";

//		BruteForce 	vo = BruteForce	.map(path);
//		DirArray 	vo = DirArray	.map(path);
//		DirArrayO 	vo = DirArrayO	.map(path);
//		GridMarch1 	vo = GridMarch1	.map(path);
//		GridMarch2	vo = GridMarch2	.map(path);
//		GridMarch2O	vo = GridMarch2O.map(path);
//		OctreeBF	vo = OctreeBF	.map(path);
//		OctreeRec	vo = OctreeRec	.map(path);
		OctreeRecO 	vo = OctreeRecO	.map(path);

		Transform t = Transform.translation			(Vec3.xyz(translateX, translateY, translateZ).sub(vo.len().mul(0.5)))
				.andThen(Transform.rotationAboutX	(rotateX - 0.5)
				.andThen(Transform.rotationAboutY	(rotateY)
				.andThen(Transform.rotationAboutZ	(rotateZ)
				.andThen(Transform.scaling			(scale)))));

		Material mDiffuseY 	= new Material(BSDF.diffuse(Color.hsb( 60, 0.7, 0.8)));
		Material mDiffuseK 	= new Material(BSDF.diffuse(Color.hsb(  0, 0.0, 0.0)));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,25, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT), 
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,25), Vec3.xyz( 0, 0,-1)), mDiffuseK	 ),
				
//				Body.uniform(vo.transformed(t), mDiffuseY)
				Body.voxelDiffuseF(vo, t)
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
