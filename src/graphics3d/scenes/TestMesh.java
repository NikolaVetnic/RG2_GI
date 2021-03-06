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
import graphics3d.solids.Mesh;
import graphics3d.solids.MeshWireframe;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function1;
import mars.utils.Numeric;

public class TestMesh extends SceneBase {

	
	String[][] mesh = {
			{ "v", "M_V Spacecraft" },
			{ "v", "M_V SpacecraftRotated" },
			{ "v", "M_V SpikyBox" },
			{ "v", "M_V UtahTeapot" },
			
			{ "vt", "M_VT ChamferedBox" },
			{ "vt", "M_VT ChamferedBoxOptimizedVT" },
			{ "vt", "M_VT ComplexBox" },
			{ "vt", "M_VT DistortedPlane" },
			{ "vt", "M_VT Houseoid" },
			{ "vt", "M_VT HouseoidOptimizedVT" },
			{ "vt", "M_VT Spacecraft" },
			{ "vt", "M_VT UtahTeapot" },
			
			{ "vtn", "M_VTN Octahedron" },
			{ "vtn", "M_VTN Sphere" },
			{ "vtn", "M_VTN ImplodedSphere" },
			{ "vtn", "M_VTN UtahTeapot" },
	};
	
	
	public static class Factory implements Function1<Scene, Double> {
		
		@GadgetDouble(p = -.5, q = 0.5)
		double px = 0.0625;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double py = 0.1250;
		
		@GadgetDouble(p = -.5, q = 0.5)
		double pz = 0.3750;

		@GadgetDouble(p = -5, q = 5)
		double dx = 0.0;
		
		@GadgetDouble(p = -5, q = 5)
		double dy = 0.0;
		
		@GadgetDouble(p = -5, q = 5)
		double dz = 0.0;

		@GadgetDouble(p = 0, q = 5.0)
		double s = 0.45;

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
			return new TestMesh(seed, cameraAngle, px, py, pz, xInt, yInt, zInt, dx, dy, dz, s, Numeric.interpolateLinear(-0.4, -4, time));
		}
	}
	
	
	public TestMesh(int seed, double cameraAngle, double px, double py, double pz, int xInt, int yInt, int zInt, double dx, double dy, double dz, double s, double z) {
		
		colorBackground = Color.WHITE;
		
		Random rng = new Random(seed);
		
		Solid m = null;
		
		try {
			if (mesh[xInt][0].equals("v"))
				m = Mesh.loadV(mesh[xInt][1]).transformed(
						 Transform.translation		(Vec3.xyz(dx, dy, dz))
				.andThen(Transform.rotationAboutY	(py)
				.andThen(Transform.rotationAboutZ	(pz)
				.andThen(Transform.rotationAboutX	(px)
				.andThen(Transform.scaling			(s))))));
			
			else if (mesh[xInt][0].equals("vt"))
				m = Mesh.loadVT(mesh[xInt][1]).transformed(
						 Transform.translation		(Vec3.xyz(dx, dy, dz))
				.andThen(Transform.rotationAboutY	(px)
				.andThen(Transform.rotationAboutZ	(py)
				.andThen(Transform.rotationAboutX	(pz)
				.andThen(Transform.scaling			(s))))));
			
			else
				m = Mesh.loadVTN(mesh[xInt][1]).transformed(
						 Transform.translation		(Vec3.xyz(dx, dy, dz))
				.andThen(Transform.rotationAboutY	(py)
				.andThen(Transform.rotationAboutZ	(pz)
				.andThen(Transform.rotationAboutX	(px)
				.andThen(Transform.scaling			(s))))));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Solid mwf = null;
		
		try {
			mwf = MeshWireframe.m(Mesh.loadVT("M_VT Spacecraft")).transformed(
					 Transform.translation		(Vec3.xyz(dx, dy, dz))
			.andThen(Transform.rotationAboutY	(px)
			.andThen(Transform.rotationAboutZ	(py)
			.andThen(Transform.rotationAboutX	(pz)
			.andThen(Transform.scaling			(s))))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
				
				Body.uniform(mwf, mDiffuseY)
		));
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0.0, 0.0, -10)))
				.andThen(Transform.rotationAboutX(0.0))
		;
	}
}
