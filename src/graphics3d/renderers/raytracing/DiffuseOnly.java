package graphics3d.renderers.raytracing;

import graphics3d.*;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class DiffuseOnly extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDouble(p = 0, q = 5)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 0.4)
		double focalBlurR = 0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new DiffuseOnly(scene, imageSize, focalDistance, focalBlurR);
		}
	}
	
	
	public DiffuseOnly(Scene scene, Vector imageSize, double focalDistance, double focalBlurR) {
		super(scene, imageSize, focalDistance, focalBlurR);
	}
	
	
	@Override
	protected Color radiance(Ray r, Sampler sampler) {
		Collision collision = collider.collide(r);
		Body body = collision.body();
		return body == null ? scene.colorBackground() : body.materialAt(collision.hit()).diffuse();
	}
	
}
