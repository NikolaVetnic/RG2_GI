package graphics3d;

import mars.geometry.Vector;

public interface Camera {
	Ray sampleExitingRay(Vector sensorPosition);
}
