package graphics3d.renderers.raytracing;

import graphics3d.*;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class RayCaster extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDouble(p = 0, q = 5)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 0.4)
		double focalBlurR = 0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new RayCaster(scene, imageSize, focalDistance, focalBlurR);
		}
	}
	
	
	public RayCaster(Scene scene, Vector imageSize, double focalDistance, double focalBlurR) {
		super(scene, imageSize, focalDistance, focalBlurR);
	}
	
	
	protected Color radiance(Ray ray, Sampler sampler) {
		Collision collision = collider.collide(ray);
		
		Body body = collision.body();
		if (body == null) return scene.colorBackground();
		
		Vec3 p = ray.at(collision.hit().t());           // Point of collision
		Vec3 n_ = collision.hit().n_();                 // Normal to the body surface at the point of collision
		
		Color totalLight = Color.BLACK;
		
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);                  // Vector from p to light;
			double lLSqr = l.lengthSquared() ;          // Distance from p to light squared
			double lL = Math.sqrt(lLSqr);               // Distance from p to light
			double cosNL = n_.dot(l) / lL;              // Cosine of the angle between n and l
			
			if (cosNL > 0) {
				totalLight = totalLight.add(light.color().mul(cosNL / lLSqr));
			}
		}
		
		Material material = body.materialAt(collision.hit());
		
		return totalLight.mul(material.diffuse());
	}
	
}
