package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import graphics3d.solids.voxelworld.m.GridMarch2;
import graphics3d.solids.voxelworld.m.GridMarch2O;
import graphics3d.solids.voxelworld.m.OctreeRecO;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class VoxelWorld_TestMap extends SceneBase {

	public static class Factory implements Function1<Scene, Double> {

		@GadgetDouble(p = -.5, q = 0.5) double rotateX = 0.125;
		@GadgetDouble(p = -.5, q = 0.5) double rotateY = 0.0;
		@GadgetDouble(p = -.5, q = 0.5) double rotateZ = 0.0;

		@GadgetDouble(p = -1000, q = 1000) double translateX = 0.0;
		@GadgetDouble(p = -1000, q = 1000) double translateY = -150.0;
		@GadgetDouble(p = -1000, q = 1000) double translateZ = 125;

		@GadgetInteger(min = 0, max = 10) int xInt = 0;
		@GadgetInteger(min = 3, max = 10) int yInt = 0;
		@GadgetInteger(min = 3, max = 10) int zInt = 0;

		@GadgetDouble(p = 0, q = 5.0) double scale = 0.01875;

		@GadgetInteger int seed = 129832191;

		@GadgetDouble(p = 0, q = 0.125) double cameraAngle = 0.0;

		@Override public Scene at(Double time) {
			try {
				return new VoxelWorld_TestMap(
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
	public VoxelWorld_TestMap(
			int 	seed, 		double 	cameraAngle, 
			double 	rotateX, 	double 	rotateY, 		double 	rotateZ, 
			int 	xInt, 		int 	yInt, 			int 	zInt, 
			double 	translateX, double 	translateY, 	double 	translateZ, 
			double 	scale, 		double 	z) throws IOException {


//		BruteForce 	vo = BruteForce	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		DirArray 	vo = DirArray	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		DirArrayO 	vo = DirArrayO	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		GridMarch1 	vo = GridMarch1	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		GridMarch2 	vo = GridMarch2	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		GridMarch2O	vo = GridMarch2O.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		OctreeBF	vo = OctreeBF	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(21), Color.WHITE);
//		OctreeRec	vo = OctreeRec	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);
//		OctreeRecO	vo = OctreeRecO	.line(Vec3.EXYZ.mul(0), Vec3.EXYZ.mul(7), Color.WHITE);


//		BruteForce 	vo = BruteForce	.map("img//ares-vallis.jpg");
//		DirArray 	vo = DirArray	.map("img//ares-vallis.jpg");
//		DirArrayO 	vo = DirArrayO	.map("img//ares-vallis.jpg");
//		GridMarch1 	vo = GridMarch1	.map("img//ares-vallis.jpg");
//		GridMarch2	vo = GridMarch2	.map("img//ares-vallis.jpg");
		GridMarch2O	vo = GridMarch2O.map("img//ares-vallis-raised-1536x1024.jpg");
//		OctreeBF	vo = OctreeBF	.map("img//ares-vallis.jpg");
//		OctreeRec	vo = OctreeRec	.map("img//ares-vallis.jpg");
//		OctreeRecO 	vo = OctreeRecO	.map("img//ares-vallis.jpg");


//		BruteForce 	vo = BruteForce	.set("img//voxel_set_01/test_000.bmp");
//		DirArray 	vo = DirArray	.set("img//voxel_set_01/test_000.bmp");
//		DirArrayO 	vo = DirArrayO	.set("img//voxel_set_01/test_000.bmp");
//		GridMarch1	vo = GridMarch1 .set("img//voxel_set_01/test_000.bmp");
//		GridMarch2	vo = GridMarch2 .set("img//voxel_set_01/test_000.bmp");
//		GridMarch2O	vo = GridMarch2O.set("img//voxel_set_01/test_000.bmp");
//		OctreeBF	vo = OctreeBF	.set("img//voxel_set_01/test_000.bmp");
//		OctreeRec	vo = OctreeRec	.set("img//voxel_set_01/test_000.bmp");
//		OctreeRecO 	vo = OctreeRecO	.set("img//voxel_set_01/test_000.bmp");


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
