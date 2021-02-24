package graphics3d.renderers.raytracing;

import graphics3d.*;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class IAmLight extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDouble(p = 0, q = 5)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 0.4)
		double focalBlurR = 0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new IAmLight(scene, imageSize, focalDistance, focalBlurR);
		}
	}
	
	
	public IAmLight(Scene scene, Vector imageSize, double focalDistance, double focalBlurR) {
		super(scene, imageSize, focalDistance, focalBlurR);
	}
	
	
	@Override
	protected Color radiance(Ray ray, Sampler sampler) {
		Collision collision = collider.collide(ray);
		
		Body body = collision.body();
		if (body == null) return Color.BLACK;
		
		Vec3 p = ray.at(collision.hit().t());           // Point of collision
		Vec3 n_ = collision.hit().n_();                 // Normal to the body surface at the point of collision
		Vec3 l = ray.p().sub(p);                        // The Vector to the light (which equals the ray origin for this renderer)
		
		double nl = n_.dot(l);
		if (nl < 0) return Color.BLACK;
		double cosNL = nl / l.length();
		
		return Color.gray(cosNL / (collision.hit().t() * collision.hit().t()));
	}
	
}
