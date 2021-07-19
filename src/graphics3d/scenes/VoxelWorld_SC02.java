package graphics3d.scenes;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import graphics3d.BSDF;
import graphics3d.Body;
import graphics3d.Color;
import graphics3d.Material;
import graphics3d.Scene;
import graphics3d.Solid;
import graphics3d.Transform;
import graphics3d.Vec3;
import graphics3d.solids.HalfSpace;
import graphics3d.solids.voxelworld.VoxOctree2_1;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class VoxelWorld_SC02 extends SceneBase {

	public static class Factory implements Function1<Scene, Double> {

		@GadgetDouble(p = -.5, q = 0.5)
		double px = 0.105;

		@GadgetDouble(p = -.5, q = 0.5)
		double py = 0.075;

		@GadgetDouble(p = -.5, q = 0.5)
		double pz = 0.0;

		@GadgetDouble(p = -50, q = 50)
		double dx = 0.0;

		@GadgetDouble(p = -50, q = 50)
		double dy = 0.0;

		@GadgetDouble(p = -50, q = 50)
		double dz = -1.5;

		@GadgetDouble(p = 0, q = 5.0)
		double s = 0.025;

		@GadgetInteger(min = 0, max = 7)
		int xInt = 0;

		@GadgetInteger(min = 3, max = 100)
		int yInt = 0;

		@GadgetInteger(min = 3, max = 100)
		int zInt = 0;

		@GadgetInteger
		int seed = 129832191;

		@GadgetDouble(p = 0, q = 0.125)
		double cameraAngle = 0.0;

		@Override
		public Scene at(Double time) {
			try {
				return new VoxelWorld_SC02(
						seed, cameraAngle,
						px, py, pz,
						xInt, yInt, zInt,
						dx, dy, dz,
						s, Numeric.interpolateLinear(-0.4, -4, time));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	public VoxelWorld_SC02(
			int 	seed, 	double 	cameraAngle, 
			double 	px, 	double 	py, 		double 	pz, 
			int 	xInt, 	int 	yInt, 		int 	zInt, 
			double 	dx, 	double 	dy, 		double 	dz, 
			double 	s, 		double 	z) throws IOException {
		
		Random rng = new Random(seed);

		int lvl = 6;

		Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

		boolean[][][] rv = new boolean[dim.xInt()][dim.yInt()][dim.zInt()];

		for (int i = 0; i < dim.xInt(); i++)
			for (int j = 0; j < dim.yInt(); j++)
				for (int k = 0; k < dim.zInt(); k++)
					rv[i][j][k] = rng.nextDouble() < 0.125;	// test model, random voxels
//					rv[i][j][k] = i == j && j == k;				// test model, cube diagonal

//		VoxOctree1 vo = VoxOctree1.arr(rv);
//		VoxOctree2 vo = VoxOctree2.arr(rv);
		VoxOctree2_1 vo = VoxOctree2_1.arr(rv);

		Solid solid = vo
				.transformed(
						 Transform.translation		(Vec3.xyz(dx, dy, dz).sub(dim.mul(0.5)))
				.andThen(Transform.rotationAboutX	(px)
				.andThen(Transform.rotationAboutY	(py)
				.andThen(Transform.rotationAboutZ	(pz)
				.andThen(Transform.scaling			(s + 0.0))))));

		Material mDiffuseY 	= new Material(BSDF.diffuse(Color.hsb( 60, 0.7, 0.8)));
		Material mDiffuseK 	= new Material(BSDF.diffuse(Color.hsb(  0, 0.0, 0.0)));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,25, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT), 
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0,25), Vec3.xyz( 0, 0,-1)), mDiffuseK	 ),
				
				Body.uniform(solid, mDiffuseY)
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
