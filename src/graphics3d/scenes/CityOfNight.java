package graphics3d.scenes;

import graphics3d.*;
import graphics3d.solids.Ball;
import graphics3d.solids.Box;
import graphics3d.solids.HalfSpace;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.drawingx.gadgets.annotations.GadgetLongRandomStream;
import mars.functions.interfaces.Function1;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

import java.util.ArrayList;
import java.util.List;


public class CityOfNight extends SceneBase {
	
	public static class Factory implements Function1<Scene, Double> {
		@GadgetInteger
		int n = 50;
		
		@GadgetLongRandomStream
		long seed = 0;
		
		
		@Override
		public Scene at(Double time) {
			return new CityOfNight(n, seed);
		}
	}
	
	
	public CityOfNight(int n, long seed) {
		Sampler sampler = new Sampler(seed);
		
		List<mars.geometry.Box> boxes = new ArrayList<mars.geometry.Box>();
		
		int nTrials = 0;
		
		while (boxes.size() < n && nTrials < 10000) {
			nTrials++;
			Vector c = sampler.randomGaussian(1);
			Vector r = Vector.xy(sampler.uniform(0.05, 0.5), sampler.uniform(0.05, 0.5));
			mars.geometry.Box bCandidate = mars.geometry.Box.cr(c, r);
			
			if (boxes.stream().noneMatch(b -> b.intersects(bCandidate))) {
				boxes.add(bCandidate.extend(0.05));
			
				double h = sampler.exponential(1);
				Solid solid = sampler.uniform() < 0.5 ?
						Box.$.pd(Vec3.py(bCandidate.p(), 0), Vec3.py(bCandidate.d(), h)) :
						Ball.cr(Vec3.py(bCandidate.c(), h), bCandidate.r().min());
				
				Material material =
						sampler.uniform() < 0.1 ?
								Material.LIGHT :
								sampler.uniform() < 0.1 ?
										Material.mirror(0.75) :
										Material.diffuse(Color.hsb(sampler.uniform(360), 0.6, 0.8));
				
				Body body = Body.uniform(solid, material);
				bodies.add(body);
				nTrials = 0;
			}
		}
		System.out.println("Number of boxes: " + boxes.size());
		
		{
			Solid solid = HalfSpace.pn(Vec3.ZERO, Vec3.EY);
//			Material material0 = Material.diffuse(Color.gray(0.4));
//			Material material1 = Material.diffuse(Color.gray(0.5));
//			Texture texture = new Checkerboard(Vector.UNIT_DIAGONAL, material0, material1);
//			Body body = Body.textured(solid, texture);
//			Body body = Body.uniform(solid, Material.diffuse(Color.hsb(120, 0.0, 0.6)));
			Body body = Body.uniform(solid, Material.diffuse(Color.gray(0.8)));
			bodies.add(body);
		}
		
		{
			double d = 20;
			lights.add(Light.pc(Vec3.xyz(-1, 2, -2).mul(d), Color.gray(1.0)));
			lights.add(Light.pc(Vec3.xyz( 2, 2,  1).mul(d), Color.gray(0.5)));
		}
		
//		colorBackground = Color.WHITE;
		
		cameraTransform = Transform.IDENTITY
				.andThen(Transform.translation(Vec3.xyz(0, 0, -7)))
				.andThen(Transform.rotationAboutX(0.12))
				.andThen(Transform.rotationAboutY(-0.1))
		;
	}
}
