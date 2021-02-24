package graphics3d.renderers.raytracing;

import graphics3d.*;
import graphics3d.renderers.RaytracingBase;
import mars.drawingx.gadgets.annotations.GadgetDouble;
import mars.drawingx.gadgets.annotations.GadgetDoubleLogarithmic;
import mars.drawingx.gadgets.annotations.GadgetInteger;
import mars.functions.interfaces.Function2;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class RayTracer extends RaytracingBase {
	
	public static class Factory implements Function2<Renderer, Scene, Vector> {
		@GadgetDoubleLogarithmic(p = 0.25, q = 32)
		double focalDistance = 1;
		
		@GadgetDouble(p = 0, q = 0.4)
		double focalBlurR = 0;
		
		@GadgetInteger(min = 0)
		int maxDepth = 9;
		
		@GadgetInteger(min = 1)
		int ambientOcclusionSamples = 8;
		
		@GadgetDouble(p = 0, q = 10)
		double ambientOcclusionFactor = 0;
		
		@Override
		public Renderer at(Scene scene, Vector imageSize) {
			return new RayTracer(scene, imageSize, focalDistance, focalBlurR, maxDepth, ambientOcclusionSamples, ambientOcclusionFactor);
		}
	}
	
	
	private final int maxDepth;
	private final int ambientOcclusionSamples;
	private final double ambientOcclusionFactor;
	
	
	public RayTracer(Scene scene, Vector imageSize, double focalDistance, double focalBlurR, int maxDepth, int ambientOcclusionSamples, double ambientOcclusionFactor) {
		super(scene, imageSize, focalDistance, focalBlurR);
		this.maxDepth = maxDepth;
		this.ambientOcclusionSamples = ambientOcclusionSamples;
		this.ambientOcclusionFactor = ambientOcclusionFactor;
	}
	
	
	@Override
	protected Color radiance(Ray r, Sampler sampler) {
		return radiance(r, maxDepth, sampler);
	}
	
	
	private Color radiance(Ray ray, int depthRemaining, Sampler sampler) {
		if (depthRemaining <= 0) return Color.BLACK;
		
		Collision collision = collider.collide(ray);
		
		Body body = collision.body();
		if (body == null) return scene.colorBackground();
		
		Vec3 p = ray.at(collision.hit().t());                    // Point of collision
		Vec3 i = ray.d().inverse();                              // Incoming direction
		Vec3 n_ = collision.hit().n_();                          // Normal to the body surface at the point of collision
		Vec3 r = Utils.reflectN(n_, i);                          // Reflected ray (i reflected over n)
		double lI = r.length();
		
		Material material = body.materialAt(collision.hit());
		
		Color totalDiffuse  = Color.BLACK;
		Color totalSpecular = Color.BLACK;
		
		for (Light light : scene.lights()) {
			Vec3 l = light.p().sub(p);                           // Vector from p to light;

			Ray rayToLight = Ray.pd(p, l);

			if (!collider.collidesIn01(rayToLight)) {            // If a path to the light is unobstructed
				double lLSqr = l.lengthSquared();                // Distance from p to light squared
				double lL = Math.sqrt(lLSqr);                    // Distance from p to light
				double cosLN = n_.dot(l) / lL;                   // Cosine of the angle between n and l

				if (cosLN > 0) {                                 // If p is visible to the light
					Color intensity = light.color().div(lLSqr);  // Inverse square law
					totalDiffuse = totalDiffuse.add(intensity.mul(cosLN));

					if (material.specular().notZero()) {
						double lr = r.dot(l);
						if (lr > 0) {
							double cosLR = lr / (lL * lI);       // cos angle between reflected ray and light
							totalSpecular = totalSpecular.add(intensity.mul(Math.pow(cosLR, material.shininess())));
						}
					}
				}
			}
		}
		
		// ambient occlusion
		
		if (ambientOcclusionFactor > 0) {
			double aoFactor = 0.0;
			for (int iter = 0; iter < ambientOcclusionSamples; iter++) {
				Ray out_ = Ray.pd(p, Utils.sampleHemisphereCosineDistributed(sampler, n_));
				double lC = collider.collide(out_).hit().t() / ambientOcclusionFactor;
				aoFactor += 1 - 1 / (1 + lC * lC);
			}
			aoFactor /= ambientOcclusionSamples;
			totalDiffuse = totalDiffuse.mul(aoFactor);
		}
		
		Color result = totalDiffuse.mul(material.diffuse()).add(totalSpecular.mul(material.specular()));
		
		
		// reflection
		
		if (material.reflective().notZero()) {
			Color reflection = radiance(Ray.pd(p, r), depthRemaining - 1, sampler);
			result = result.add(reflection.mul(material.reflective()));
		}
		
		
		// refraction
		
		if (material.refractive().notZero()) {
			double ri = material.refractiveIndex();
			double k = 1;
			
			double c1 = i.dot(n_) / lI;
			if (c1 < 0) { 		                 // We are exiting the object
				ri = 1.0 / ri;
				k = -1;
			}
//			double s1 = Math.sqrt(1 - c1*c1);
//			double s2 = s1 / ri;
//			double c2 = Math.sqrt(1 - s2 * s2);
			// That is ok, but we'll get one square root less if we inline s2 (because we are squaring it)
			// s2^2 = (s1 / ri)^2 = s1^2 / ri^2 = (1 - c1^2) / ri^2
			// c2^2 = 1 - s2^2 = 1 - (1 - c1^2) / ri^2
			double c2Sqr = 1 - (1 - c1 * c1) / (ri * ri);
			
			Vec3 f;
			if (c2Sqr > 0) {
				double c2 = Math.sqrt(c2Sqr);
				f = n_.mul(c1/ri - k * c2).sub(i.div(ri*lI)); // refraction
			} else {
				// total reflection
				f = r;
			}
			
			Color refraction = radiance(Ray.pd(p, f), depthRemaining - 1, sampler);
			result = result.add(refraction.mul(material.refractive()));
		}
		
		return result;
	}
	
}
