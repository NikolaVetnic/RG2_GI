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
import graphics3d.solids.Quadric;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class TestQuadrics extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble(p = -.5, q = 0.5)
		double px = 0.0;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double py = 0.0;
		
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
		int seed = 129832195;

		@GadgetDouble(p = 0, q = 0.125)
		double cameraAngle = 0.0;
		
		@Override
		public Scene at(Double time) {
			return new TestQuadrics(seed, cameraAngle, px, py, pz, xInt, yInt, zInt, dx, dy, dz, s, Numeric.interpolateLinear(-0.4, -4, time));
		}
	}
	
	
	public TestQuadrics(int seed, double cameraAngle, double px, double py, double pz, int xInt, int yInt, int zInt, double dx, double dy, double dz, double s, double z) {
		
		colorBackground = Color.WHITE;
		
		Random rng = new Random(seed);		
		
		Solid[] quadrics = { 
				Quadric.sphere(),
				Quadric.cylinder(),
				Quadric.cone(),
				Quadric.ellipsoid(),
				Quadric.paraboloid(),
				Quadric.hyperboloid1(),
				Quadric.hyperboloid2(),
				Quadric.hparaboloid()
		};		
		
		Solid solid = quadrics[xInt].transformed(
				 Transform.rotationAboutX	(px)
		.andThen(Transform.rotationAboutY	(py)
		.andThen(Transform.rotationAboutZ	(pz)
		.andThen(Transform.translation		(Vec3.xyz(dx, dy, dz))
		.andThen(Transform.scaling			(s))))));
		
		Material mGlass = new Material(BSDF.glossyRefractive(Color.hsb(210, 0.2, 0.9), 1.4, s));
		Material mFloor = new Material(BSDF.glossy(Color.hsb(  0, 0.0, 0.7), 0.4));
		
		Material mDiffuseR = new Material(BSDF.diffuse(Color.hsb(  0, 0.7, 0.8)));
		Material mDiffuseG = new Material(BSDF.diffuse(Color.hsb(120, 0.7, 0.8)));
		Material mDiffuseB = new Material(BSDF.diffuse(Color.hsb(240, 0.7, 0.8)));
		Material mDiffuseY = new Material(BSDF.diffuse(Color.hsb( 60, 0.7, 0.8)));
		
		Material mLight = Material.light(Color.hsb(0, 0.5, 120.0));
		
		bodies.addAll(List.of(
				Body.uniform(HalfSpace.pn(Vec3.xyz(-3, 0, 0), Vec3.xyz( 1, 0, 0)), mDiffuseR	 ),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 3, 0, 0), Vec3.xyz(-1, 0, 0)), mDiffuseB	 ),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0,-3, 0), Vec3.xyz( 0, 1, 0)), mDiffuseR	 ),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 9, 0), Vec3.xyz( 0,-1, 0)), Material.LIGHT),
				Body.uniform(HalfSpace.pn(Vec3.xyz( 0, 0, 3), Vec3.xyz( 0, 0,-1)), mDiffuseG	 ),
				
				Body.uniform(solid, mDiffuseY)
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
