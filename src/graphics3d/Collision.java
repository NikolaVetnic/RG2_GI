package graphics3d;


public class Collision {
	
	private final Hit hit;
	private final Body body;
	
	
	public Collision(Hit hit, Body body) {
		this.hit = hit;
		this.body = body;
	}
	
	
	public Hit hit() {
		return hit;
	}
	
	
	public Body body() {
		return body;
	}
	
}
