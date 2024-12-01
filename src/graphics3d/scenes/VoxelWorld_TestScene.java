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
import graphics3d.solids.voxelworld.m.GridMarch2O;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class VoxelWorld_TestScene extends SceneBase {

	public static class Factory implements Function1<Scene, Double> {

		@GadgetDouble(p = -.5, q = 0.5) double rotateX = -0.125;
		@GadgetDouble(p = -.5, q = 0.5) double rotateY = -0.375;
		@GadgetDouble(p = -.5, q = 0.5) double rotateZ = -0.125;

		@GadgetDouble(p = -100, q = 100) double translateX = 0.0 /* -75.0 */;
		@GadgetDouble(p = -100, q = 100) double translateY = 0.0;
		@GadgetDouble(p = -100, q = 100) double translateZ = 7.5 /* -37.5 */;
		
		@GadgetInteger(min = 0, max = 10) int xInt = 0;
		@GadgetInteger(min = 3, max = 10) int yInt = 0;
		@GadgetInteger(min = 3, max = 10) int zInt = 0;

		@GadgetDouble(p = 0, q = 5.0) double scale = /* 0.0625 */ .375;

		@GadgetInteger int seed = 129832191;

		@GadgetDouble(p = 0, q = 0.125) double cameraAngle = 0.0;

		@Override public Scene at(Double time) {
			try {
				return new VoxelWorld_TestScene(
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
	public VoxelWorld_TestScene(
			int 	seed, 		double 	cameraAngle, 
			double 	rotateX, 	double 	rotateY, 		double 	rotateZ, 
			int 	xInt, 		int 	yInt, 			int 	zInt, 
			double 	translateX, double 	translateY, 	double 	translateZ, 
			double 	scale, 		double 	z) throws IOException {
		
		Random rng = new Random(seed);

		int lvl = 6;
		
		double rngLimit = 0.0025;
		
		Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

		boolean[][][] arr0 = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];
		Color[][][] arr1 = new Color[dim.xInt()][dim.yInt()][dim.zInt()];

		for (int i = 0; i < dim.xInt(); i++) {
			for (int j = 0; j < dim.yInt(); j++) {
				for (int k = 0; k < dim.zInt(); k++) {
					
					if (rng.nextDouble() < 0.0625) {			// test model, random voxels
						arr0[i][j][k] = true;
						arr1[i][j][k] = Color.rgb(rng.nextDouble(), rng.nextDouble(), 0.0);
					}
					
//					if (i == j && j == k) {						// test model, cube diagonal
//						arr0[i][j][k] = false;
//						arr1[i][j][k] = Color.rgb(rng.nextDouble(), rng.nextDouble(), 0.0);
//					}
					
//					arr0[i][j][k] = true;
//					arr1[i][j][k] = Color.rgb(.8 * i / dim.xInt() + .2, 0.8 * j / dim.yInt() + .2, 0.0);
				}
			}
		}

//		BruteForce 	vo = BruteForce	.model(arr0, arr1);
//		DirArray 	vo = DirArray	.model(arr0, arr1);
//		DirArrayO 	vo = DirArrayO	.model(arr0, arr1);
//		GridMarch1 	vo = GridMarch1	.model(arr0, arr1);
//		GridMarch2 	vo = GridMarch2	.model(arr0, arr1);
		GridMarch2O	vo = GridMarch2O.model(arr0, arr1);
//		OctreeBF	vo = OctreeBF	.model(arr0, arr1);
//		OctreeRec	vo = OctreeRec	.model(arr0, arr1);
//		OctreeRecO	vo = OctreeRecO	.model(arr0, arr1);


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
//		GridMarch2 	vo = GridMarch2	.map("img//ares-vallis.jpg");
//		GridMarch2O	vo = GridMarch2O.map("img//valles-marineris.jpg");
//		OctreeBF	vo = OctreeBF	.map("img//ares-vallis.jpg");
//		OctreeRec	vo = OctreeRec	.map("img//ares-vallis.jpg");
//		OctreeRecO	vo = OctreeRecO	.map("img//ares-vallis.jpg");


//		BruteForce 	vo = BruteForce	.set("img//voxel_set_01/test_000.bmp");
//		DirArray 	vo = DirArray	.set("img//voxel_set_01/test_000.bmp");
//		DirArrayO 	vo = DirArrayO	.set("img//voxel_set_01/test_000.bmp");
//		GridMarch1	vo = GridMarch1 .set("img//voxel_set_01/test_000.bmp");
//		GridMarch2	vo = GridMarch2 .set("img//voxel_set_01/test_000.bmp");
//		GridMarch2O	vo = GridMarch2O.set("img//voxel_set_01/test_000.bmp");
//		OctreeBF	vo = OctreeBF	.set("img//voxel_set_01/test_000.bmp");
//		OctreeRec	vo = OctreeRec	.set("img//voxel_set_01/test_000.bmp");
//		OctreeRecO 	vo = OctreeRecO	.set("img//voxel_set_01/test_000.bmp");
		
		// =-=-=-=
		
		/*
		var model = vo.model()[0];
		var height = vo.len().z();
		var sheet = new Vec3[vo.len().xInt()][vo.len().yInt()];
		
		for (var i = 0; i < vo.len().xInt(); i++) {
			for (var j = 0; j < vo.len().yInt(); j++) {
				
				var prevKIdx = 0;
				var prevKVal = model[i][j][0];
				
				for (var k = 1; k < vo.len().zInt(); k++) {
					
					if (prevKVal && !model[i][j][k])
						break;
					
					prevKIdx = k;
					prevKVal = true;
				}
				
				sheet[i][j] = Vec3.xyz(i - vo.len().xInt() / 2, j - vo.len().yInt() / 2, prevKIdx);
			}
		}
		*/
		
//        String filePath = "mesh//M_V ArresVallisLoad.obj"; // Specify the path and filename
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//
//			for (var i = 0; i < vo.len().xInt(); i++) {
//				for (var j = 0; j < vo.len().yInt(); j++) {
//					writer.write("v " + sheet[i][j].xInt() + " " + sheet[i][j].yInt() + " " + (1 - sheet[i][j].zInt() / vo.len().z() + .5) * 15);
//					writer.newLine();
//				}
//			}
//			
//			writer.newLine();
//		
//			for (var i = 1; i < vo.len().xInt(); i++) {
//				for (var j = 1; j < vo.len().yInt(); j++) {
//					
//					/*
//					 * 11 12 ...
//					 * 1  2  ...
//					 * 
//					 * 1 2 12 -> 	(j - 1) * vo.len().yInt()   +    (i - 1)
//					 * 				(j - 1) * vo.len().yInt()	+	 (i - 0)
//					 * 				(j - 0) * vo.len().yInt()	+	 (i - 1)
//					 * 
//					 * 2 11 12 ->	(j - 1) * vo.len().yInt()	+	 (i - 0)
//					 * 				(j - 0) * vo.len().yInt()	+	 (i - 1)
//					 * 				(j - 0) * vo.len().yInt()	+	 (i - 0)
//					 */
//					
//					var f1_0 = (j - 1) * vo.len().yInt() + (i - 1) + 1;
//					var f1_1 = (j - 1) * vo.len().yInt() + (i - 0) + 1;
//					var f1_2 = (j - 0) * vo.len().yInt() + (i - 1) + 1;
//					
//					var f2_0 = (j - 1) * vo.len().yInt() + (i - 0) + 1;
//					var f2_1 = (j - 0) * vo.len().yInt() + (i - 1) + 1;
//					var f2_2 = (j - 0) * vo.len().yInt() + (i - 0) + 1;
//					
//					writer.write("f " + f1_0 + " " + f1_1 + " " + f1_2); writer.newLine();
//					writer.write("f " + f2_2 + " " + f2_1 + " " + f2_0); writer.newLine();
//				}
//			}
//		
//        } catch (IOException e) {
//        	e.printStackTrace();
//        }
//		
//		Solid m = Mesh.loadV("M_V ArresVallisLoad");

		// =-=-=-=

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
				
//				Body.uniform(vo.transformed(t), mDiffuseY)
				Body.voxelDiffuseF(vo, t)
//				Body.uniform(m.transformed(t), mDiffuseY)
 		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
