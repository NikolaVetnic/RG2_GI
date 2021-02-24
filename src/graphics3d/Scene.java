package graphics3d;

import java.util.List;

public interface Scene {
	
	List<Body> bodies();
	List<Light> lights();
	Transform cameraTransform();
	Color colorBackground();
	
}
