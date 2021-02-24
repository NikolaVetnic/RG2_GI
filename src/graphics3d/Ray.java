package graphics3d;

public class Ray {

	private final Vec3 p, d;
	

	
	public Ray(Vec3 p, Vec3 d) {
		this.p = p;
		this.d = d;
	}
	
	
	public static Ray pd(Vec3 p, Vec3 d) {
		return new Ray(p, d);
	}
	
	
	public static Ray pq(Vec3 p, Vec3 q) {
		return pd(p, q.sub(p));
	}
	

	public Vec3 p() {
		return p;
	}
	

	public Vec3 d() {
		return d;
	}
	

	public Vec3 at(double t) {
		return p.add(d.mul(t));
	}
	
	
	public Ray normalized_() {
		return Ray.pd(p(), d().normalized_());
	}
	
}