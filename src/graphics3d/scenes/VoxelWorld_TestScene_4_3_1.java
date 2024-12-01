package graphics3d.scenes;

import java.io.IOException;
import java.util.List;
import java.util.Random;

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

public class VoxelWorld_TestScene_4_3_1 extends SceneBase {

	public static class Factory implements Function1<Scene, Double> {

		@GadgetDouble(p = -.5, q = 0.5) double rotateX = -0.125;
		@GadgetDouble(p = -.5, q = 0.5) double rotateY = -0.375;
		@GadgetDouble(p = -.5, q = 0.5) double rotateZ = -0.125;

		@GadgetDouble(p = -100, q = 100) double translateX = 0.0;
		@GadgetDouble(p = -100, q = 100) double translateY = 0.0;
		@GadgetDouble(p = -100, q = 100) double translateZ = 2.0;
		
		@GadgetInteger(min = 0, max = 10) int xInt = 0;
		@GadgetInteger(min = 3, max = 10) int yInt = 0;
		@GadgetInteger(min = 3, max = 10) int zInt = 0;

		@GadgetDouble(p = 0, q = 5.0) double scale = .375 - .01875;

		@GadgetInteger int seed = 129832191;

		@GadgetDouble(p = 0, q = 0.125) double cameraAngle = 0.0;

		@Override public Scene at(Double time) {
			try {
				return new VoxelWorld_TestScene_4_3_1(
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
	public VoxelWorld_TestScene_4_3_1(
			int 	seed, 		double 	cameraAngle, 
			double 	rotateX, 	double 	rotateY, 		double 	rotateZ, 
			int 	xInt, 		int 	yInt, 			int 	zInt, 
			double 	translateX, double 	translateY, 	double 	translateZ, 
			double 	scale, 		double 	z) throws IOException {
		
		Random rng = new Random(seed);

		int lvl = 4;
		double prob = 1 - 1 / Math.pow(2, 5);
		
		Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

		boolean[][][] arr0 = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];
		Color[][][] arr1 = new Color[dim.xInt()][dim.yInt()][dim.zInt()];

		for (int i = 0; i < dim.xInt(); i++) {
			for (int j = 0; j < dim.yInt(); j++) {
				for (int k = 0; k < dim.zInt(); k++) {
					
					if (rng.nextDouble() < prob) {
						arr0[i][j][k] = true;
						arr1[i][j][k] = Color.rgb(rng.nextDouble(), rng.nextDouble(), 0.0);
					}
				}
			}
		}

//		BruteForce 	vo = BruteForce	.model(arr0, arr1);
//		DirArray 	vo = DirArray	.model(arr0, arr1);
//		DirArrayO 	vo = DirArrayO	.model(arr0, arr1);
//		GridMarch1 	vo = GridMarch1	.model(arr0, arr1);
//		GridMarch2 	vo = GridMarch2	.model(arr0, arr1);
//		GridMarch2O	vo = GridMarch2O.model(arr0, arr1);
//		OctreeBF	vo = OctreeBF	.model(arr0, arr1);
//		OctreeRec	vo = OctreeRec	.model(arr0, arr1);
		OctreeRecO	vo = OctreeRecO	.model(arr0, arr1);

		Transform t = Transform.translation			(Vec3.xyz(translateX, translateY, translateZ).sub(dim.mul(0.5)))
				.andThen(Transform.rotationAboutX	(rotateX)
				.andThen(Transform.rotationAboutY	(rotateY)
				.andThen(Transform.rotationAboutZ	(rotateZ)
				.andThen(Transform.scaling			(scale)))));

		Material mDiffuseY 	= new Material(BSDF.diffuse(Color.hsb( 60, 0.7, 0.8)));
		Material mDiffuseK 	= new Material(BSDF.diffuse(Color.hsb(  0, 0.0, 0.0)));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,25, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT), 
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,25), Vec3.xyz( 0, 0,-1)), mDiffuseK	 ),
				
				Body.voxelDiffuseF(vo, t)
 		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}