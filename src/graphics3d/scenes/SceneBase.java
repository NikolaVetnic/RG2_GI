package graphics3d.scenes;

import graphics3d.*;

import java.util.ArrayList;
import java.util.List;

public abstract class SceneBase implements Scene {
	protected final List<Body > bodies = new ArrayList<>();
	protected final List<Light> lights = new ArrayList<>();
	protected Transform cameraTransform = Transform.IDENTITY;
	protected Color colorBackground = Color.BLACK;
	
	
	@Override
	public Transform cameraTransform() {
		return cameraTransform;
	}
	
	
	@Override
	public List<Body> bodies() {
		return bodies;
	}
	
	
	@Override
	public List<Light> lights() {
		return lights;
	}
	
	@Override
	public Color colorBackground() {
		return colorBackground;
	}
}
