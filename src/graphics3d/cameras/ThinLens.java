package graphics3d.cameras;

import graphics3d.Camera;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.geometry.Vector;
import mars.random.sampling.Sampler;

public class ThinLens implements Camera {
	
	private final double focalDistance, lensRadius;
	private final Sampler sampler = new Sampler();
	
	
	
	public ThinLens(double focalDistance, double lensRadius) {
		this.focalDistance = focalDistance;
		this.lensRadius = lensRadius;
	}
	
	
	@Override
	public Ray sampleExitingRay(Vector sensorPosition) {
		Vec3 q = Vec3.pz(sensorPosition, 1.0).mul(focalDistance);
		Vec3 p = Vec3.pz(sampler.randomInDisk(lensRadius), 0);
		return Ray.pq(p, q);
	}
}
