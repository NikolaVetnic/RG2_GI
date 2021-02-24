package graphics3d.renderers.raytracing;

import graphics3d.*;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;


public class PathTracer extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDoubleLogarithmic(p = 0.125, q = 256)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 1)
		double focalBlurR = 0;
		
		@GadgetInteger(min = 0)
		int maxDepth = 100;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new PathTracer(scene, imageSize, focalDistance, focalBlurR, maxDepth);
		}
	}
	
	
	private final int maxDepth;
	
	
	public PathTracer(Scene scene, Vector imageSize, double focalDistance, double focalBlurR, int maxDepth) {
		super(scene, imageSize, focalDistance, focalBlurR);
		this.maxDepth = maxDepth;
	}
	
	
	@Override
	protected Color radiance(Ray r, Sampler sampler) {
		return radiance(r, maxDepth, sampler);
	}
	
	
	private Color radiance(Ray ray, int depthRemaining, Sampler sampler) {
		if (depthRemaining <= 0) return Color.BLACK;

		Collision collision = collider.collide(ray);
		
		Body body = collision.body();
		if (body == null) {
			return scene.colorBackground();
		}
		
		Material material = body.materialAt(collision.hit());
		Color result = material.emittance();
		
		Vec3 i = ray.d().inverse();                              // Incoming direction
		Vec3 n_ = collision.hit().n_();                          // Normal to the body surface at the point of collision
		BSDF.Result bsdfResult = material.bsdf().sample(sampler, n_, i);
		
		if (bsdfResult.color().notZero()) {
			Vec3 p = ray.at(collision.hit().t());                // Point of collision
			Ray rayScattered = Ray.pd(p, bsdfResult.out());
			Color rO = radiance(rayScattered, depthRemaining - 1, sampler);
			Color rI = rO.mul(bsdfResult.color());
			result = result.add(rI);
		}
		
		return result;
	}
	
}
