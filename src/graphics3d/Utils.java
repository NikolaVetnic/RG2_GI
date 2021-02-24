package graphics3d;

import mars.random.sampling.Sampler;
import mars.utils.Numeric;


public class Utils {
	
	/** Some normal to v */
	public static Vec3 normal(Vec3 v) {
		if (v.x() != 0 || v.y() != 0) {
			return Vec3.xyz(-v.y(), v.x(), 0);
		} else {
			return Vec3.EX;
		}

//		Vec3 p = v.cross(Vec3.EX);
//		if (p.isZero()) {
//			p = v.cross(Vec3.EY);
//		}
//		return p;
	}
	
	
	/** d reflected over n_ */
	public static Vec3 reflectN(Vec3 n_, Vec3 d) {
		return n_.mul(2 * d.dot(n_)).sub(d);
	}
	
	
	/** d reflected over n */
	public static Vec3 reflected(Vec3 n, Vec3 d) {
		return n.mul(2 * d.dot(n) / n.lengthSquared()).sub(d);
	}
	
	
	/** The length of the result equals the length of n. */
	public static Vec3 sampleHemisphereUniform(Sampler sampler, Vec3 n) {
		double nL = n.length();
		
		Vec3 p = normal(n) .normalizedTo(nL);
		Vec3 q = n.cross(p).normalizedTo(nL);
		
		double phi = sampler.uniform();
		double z   = sampler.uniform();
		
		double r = Math.sqrt(1 - z*z);
		double x = Numeric.cosT(phi) * r;
		double y = Numeric.sinT(phi) * r;
		
		return      (n.mul(z))
				.add(p.mul(x))
				.add(q.mul(y));
	}
	
	
	/** The length of the result equals the length of n. */
	public static Vec3 sampleHemisphereCosineDistributed(Sampler sampler, Vec3 n) {
		double nL = n.length();
		
		Vec3 p = normal(n).normalizedTo(nL);
		Vec3 q = n.cross(p).normalizedTo(nL);
		
		double phi  = sampler.uniform();
		double zSqr = sampler.uniform();
		double z = Math.sqrt(zSqr);
		
		double r = Math.sqrt(1 - zSqr);
		double x = Numeric.cosT(phi) * r;
		double y = Numeric.sinT(phi) * r;
		
		return (n.mul(z))
				.add(p.mul(x))
				.add(q.mul(y));
	}

	
	public static Vec3 sampleHemisphereUniformRejection(Sampler sampler, Vec3 n) {
		// Not faster than the analytical approach, even when dot product is not checked.
		
		Vec3 v;
		
		do {
			v = Vec3.xyz(
					2 * sampler.uniform() - 1,
					2 * sampler.uniform() - 1,
					2 * sampler.uniform() - 1
			);
		} while (v.lengthSquared() > 1 || v.dot(n) < 0);
		
		return v.normalized_();
	}


	public static Vec3 sampleHemisphereCosineDistributedRejection(Sampler sampler, Vec3 n_) {
		// Sample the sphere with radius 1, add n_, normalize
		// Again a bit slower than analytical approach.
		// Can this be done with only one sqrt?
		double x, y, z, lVSqr;
		
		do {
			x = 2 * sampler.uniform() - 1;
			y = 2 * sampler.uniform() - 1;
			z = 2 * sampler.uniform() - 1;
			lVSqr = x*x + y*y + z*z;
		} while (lVSqr > 1);
		
		double lV = Math.sqrt(lVSqr);
		Vec3 v_ = Vec3.xyz(x / lV, y / lV, z / lV); // Should be *p + y*q + z*n
		Vec3 a = v_.add(n_);
		return a.normalized_();
	}
	
	
//	public static Vec3 sampleHemisphereCosineDistributedRejectionFromDisk(Sampler sampler, Vec3 n_) {
//		// Sample the unit disk, lift to the unit sphere
//		// Wrong!
//		double x, y, lSqr;
//
//		do {
//			x = 2 * sampler.uniform() - 1;
//			y = 2 * sampler.uniform() - 1;
//			lSqr = x*x + y*y;
//		} while (lSqr > 1);
//
//		double z = Math.sqrt(1 - lSqr);
//		return Vec3.xyz(x, y, z);
//	}
	
	
	public static Vec3 sampleBallUniform(Sampler sampler) {
		double x, y, z;
		do {
			x = 2 * sampler.uniform() - 1;
			y = 2 * sampler.uniform() - 1;
			z = 2 * sampler.uniform() - 1;
		} while (x*x + y*y + z*z > 1);
		return Vec3.xyz(x, y, z);
	}
	
	
	static Vec3 refracted(double refractiveIndex, Vec3 n_, Vec3 i) {
		double ri = refractiveIndex;
		double k = 1;
		double lI = i.length();
		
		double c1 = i.dot(n_) / lI;
		if (c1 < 0) { 		                              // We are exiting the object
			ri = 1.0 / ri;
			k = -1;
		}
		double c2Sqr = 1 - (1 - c1 * c1) / (ri * ri);
		
		Vec3 f;
		if (c2Sqr > 0) {
			double c2 = Math.sqrt(c2Sqr);
			f = n_.mul(c1/ri - k * c2).sub(i.div(ri*lI)); // refraction
		} else {
			f = reflectN(n_, i);                          // total reflection
		}
		return f;
	}
}
