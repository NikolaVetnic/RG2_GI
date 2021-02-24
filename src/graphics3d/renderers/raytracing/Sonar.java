package graphics3d.renderers.raytracing;

import graphics3d.Color;
import graphics3d.Ray;
import graphics3d.Renderer;
import graphics3d.Scene;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class Sonar extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDouble(p = 0, q = 5)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 0.4)
		double focalBlurR = 0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new Sonar(scene, imageSize, focalDistance, focalBlurR);
		}
	}
	
	
	public Sonar(Scene scene, Vector imageSize, double focalDistance, double focalBlurR) {
		super(scene, imageSize, focalDistance, focalBlurR);
	}
	

	@Override
	protected Color radiance(Ray ray, Sampler sampler) {
		double l = collider.collide(ray).hit().t() / ray.d().length();
		return Color.gray(1.0 / l);
	}
	
}
