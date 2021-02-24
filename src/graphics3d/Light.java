package graphics3d;

/**
 * Point Light
 */
public class Light {
	
	private final Vec3 p;
	private final Color c;
	
	
	
	private Light(Vec3 p, Color c) {
		this.p = p;
		this.c = c;
	}
	

	public static Light pc(Vec3 p, Color c) {
		return new Light(p, c);
	}

	
	public Vec3 p() {
		return p;
	}
	
	
	public Color color() {
		return c;
	}
	
}
