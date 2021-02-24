package graphics3d;


import mars.geometry.Vector;

public class HitData implements Hit {
	
	private final double t;
	private final Vec3 n_;
	private final Vector uv;
	
	
	
	private HitData(double t, Vec3 n_, Vector uv) {
		this.t = t;
		this.n_ = n_;
		this.uv = uv;
	}
	
	
	public static HitData tnu(double t, Vec3 n_, Vector uv) {
		return new HitData(t, n_, uv);
	}
	
	
	public static HitData tn(double t, Vec3 n_) {
		return new HitData(t, n_, Vector.ZERO);
	}
	
	
	public static HitData from(Hit hit) {
		return HitData.tnu(hit.t(), hit.n_(), hit.uv());
	}
	
	
	@Override
	public double t() {
		return t;
	}
	
	@Override
	public Vec3 n_() {
		return n_;
	}
	
	@Override
	public Vector uv() {
		return uv;
	}
	
	@Override
	public Hit inversed() {
		return tnu(t, n_.inverse(), uv);
	}
}
