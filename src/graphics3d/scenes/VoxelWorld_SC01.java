package graphics3d.scenes;

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
import graphics3d.solids.VoxelWorld;
import graphics3d.solids.voxelworld.BruteForceArray;
import graphics3d.solids.voxelworld.BruteForceList;
import graphics3d.solids.voxelworld.DirArray;
import graphics3d.solids.voxelworld.OptDirArray;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class VoxelWorld_SC01 extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble(p = -.5, q = 0.5)
		double px = -.2500;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double py = 0.0625;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double pz = 0.0;

		@GadgetDouble(p = -5, q = 5)
		double dx = 0.0;
		
		@GadgetDouble(p = -5, q = 5)
		double dy = 0.0;
		
		@GadgetDouble(p = -5, q = 5)
		double dz = 0.0;

		@GadgetDouble(p = 0, q = 5.0)
		double s = 0.25;

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
			return new VoxelWorld_SC01(seed, cameraAngle, px, py, pz, xInt, yInt, zInt, dx, dy, dz, s, Numeric.interpolateLinear(-0.4, -4, time));
		}
	}
	
	
	public VoxelWorld_SC01(int seed, double cameraAngle, double px, double py, double pz, int xInt, int yInt, int zInt, double dx, double dy, double dz, double s, double z) {
		
		colorBackground = Color.WHITE;
		
		Random rng = new Random(seed);
		
		// test object 01 : random voxel array 
		
		Vec3 dim = Vec3.xyz(7, 7, 7);
		
		javafx.scene.paint.Color[][][] rv = new javafx.scene.paint.Color[dim.xInt()][dim.yInt()][dim.zInt()];
		
		for (int i = 0; i < dim.xInt(); i++) 
			for (int j = 0; j < dim.yInt(); j++) 
				for (int k = 0; k < dim.zInt(); k++)
					rv[i][j][k] = rng.nextDouble() < 0.35 ? javafx.scene.paint.Color.WHITE : null;
		
		Solid obj01 = OptDirArray.v(rv).transformed(
				 Transform.translation		(Vec3.xyz(dx, dy, dz).sub(dim.mul(0.5)))
		.andThen(Transform.rotationAboutX	(px)
		.andThen(Transform.rotationAboutY	(py)
		.andThen(Transform.rotationAboutZ	(pz)
		.andThen(Transform.scaling			(s + 0.4))))));;
		
		// test object 02 : voxel set
		
		Solid obj02 = OptDirArray.set("img/voxel_set_01/test_000.bmp").transformed(
				 Transform.translation		(Vec3.xyz(dx, dy, dz).sub(Vec3.xyz(20, 20, 14).mul(0.5)))
		.andThen(Transform.rotationAboutX	(px)
		.andThen(Transform.rotationAboutY	(py)
		.andThen(Transform.rotationAboutZ	(pz)
		.andThen(Transform.scaling			(s))))));;
		
		Solid vw = obj02;
		
		Material mGlass 	= new Material(BSDF.glossyRefractive(Color.hsb(210, 0.2, 0.9), 1.4, s));
		Material mFloor 	= new Material(BSDF.glossy(Color.hsb(  0, 0.0, 0.7), 0.4));
		
		Material mDiffuseR 	= new Material(BSDF.diffuse(Color.hsb(  0, 0.7, 0.8)));
		Material mDiffuseY 	= new Material(BSDF.diffuse(Color.hsb( 60, 0.7, 0.8)));
		Material mDiffuseG 	= new Material(BSDF.diffuse(Color.hsb(120, 0.7, 0.8)));
		Material mDiffuseB 	= new Material(BSDF.diffuse(Color.hsb(240, 0.7, 0.8)));
		Material mDiffuseP 	= new Material(BSDF.diffuse(Color.hsb(270, 0.4, 0.8)));
		
		Material mLight = Material.light(Color.hsb(0, 0.5, 120.0));
		
		bodies.addAll(List.of(
//				Body.uniform(HalfSpace.pn(Vec3.xyz(-3, 0, 0), Vec3.xyz( 1, 0, 0)), mDiffuseR	 ),
//				Body.uniform(HalfSpace.pn(Vec3.xyz( 3, 0, 0), Vec3.xyz(-1, 0, 0)), mDiffuseB	 ),
//				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-3, 0), Vec3.xyz( 0, 1, 0)), mDiffuseB	 ),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 9, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 3), Vec3.xyz( 0, 0,-1)), mDiffuseP	 ),
				
				Body.uniform(vw, mDiffuseY)
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
