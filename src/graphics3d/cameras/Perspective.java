package graphics3d.cameras;

import graphics3d.Camera;
import graphics3d.Ray;
import graphics3d.Vec3;
import mars.geometry.Vector;

public class Perspective implements Camera {
	
	@Override
	public Ray sampleExitingRay(Vector sensorPosition) {
		return Ray.pd(
				Vec3.ZERO,
				Vec3.pz(sensorPosition, 1.0)
		);
	}
	
}
