package graphics3d;

import mars.geometry.Vector;
import mars.utils.Numeric;

public class Vec3 {
	
	
	public static final Vec3 ZERO = xyz(0, 0, 0);
	public static final Vec3 EX   = xyz(1, 0, 0);
	public static final Vec3 EY   = xyz(0, 1, 0);
	public static final Vec3 EZ   = xyz(0, 0, 1);
	public static final Vec3 EXY  = xyz(1, 1, 0);
	public static final Vec3 EYZ  = xyz(0, 1, 1);
	public static final Vec3 EZX  = xyz(1, 0, 1);
	public static final Vec3 EXYZ = xyz(1, 1, 1);
	public static final Vec3[] E  = {EX, EY, EZ};
	
	
	private final double x, y, z;
	
	
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	
	public static Vec3 xyz(double x, double y, double z) {
		return new Vec3(x, y, z);
	}
	
	
	public static Vec3 pz(Vector p, double z) {
		return xyz(p.x(), p.y(), z);
	}
	
	
	public static Vec3 py(Vector p, double y) {
		return xyz(p.x(), y, p.y());
	}
	
	
	public static Vec3 rtp(double r, double theta, double phi) {
		
		double x = r * Numeric.cosT(phi) * Numeric.sinT(theta);
		double y = r * Numeric.sinT(phi) * Numeric.sinT(theta);
		double z = r * Numeric.cosT(theta);
		
		return xyz(x, y, z);
	}
	
	
	public double x() 	{ return x; 		}
	public double y() 	{ return y; 		}
	public double z() 	{ return z; 		}
	
	public int xInt() 	{ return (int) x; 	}
	public int yInt() 	{ return (int) y; 	}
	public int zInt() 	{ return (int) z; 	}
	
	
	public double get(int i) {
		return switch (i) {
			case 0 -> x();
			case 1 -> y();
			case 2 -> z();
			default -> throw new IllegalArgumentException();
		};
	}
	
	
	public Vec3 add(Vec3 o) {
		return xyz(x() + o.x(), y() + o.y(), z() + o.z());
	}
	
	
	public Vec3 sub(Vec3 o) {
		return xyz(x() - o.x(), y() - o.y(), z() - o.z());
	}
	
	
	public Vec3 mul(double k) {
		return xyz(x() * k, y() * k, z() * k);
	}
	
	
	public Vec3 mul(Vec3 o) {
		return xyz(x() * o.x(), y() * o.y(), z() * o.z());
	}


	public Vec3 div(double k) {
		return xyz(x() / k, y() / k, z() / k);
	}
	
	
	public Vec3 div(Vec3 o) {
		return xyz(x() / o.x(), y() / o.y(), z() / o.z());
	}
	
	
	public Vec3 floor() {
		return xyz(Math.floor(x()), Math.floor(y()), Math.floor(z()));
	}

	
	public Vec3 round() {
		return xyz(Math.round(x()), Math.round(y()), Math.round(z()));
	}
	
	
	public Vec3 signum() {
		return xyz(Math.signum(x()), Math.signum(y()), Math.signum(z()));
	}
	
	
	public double lengthSquared() {
		return x() * x() + y() * y() + z() * z();
	}
	
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	
	public Vec3 normalized_() {
		return div(length());
	}
	
	
	public Vec3 normalizedTo(double l) {
		return mul(l / length());
	}
	
	
	public Vec3 inverse() {
		return xyz(-x(), -y(), -z());
	}
	
	
	public double dot(Vec3 o) {
		return x() * o.x() + y() * o.y() + z() * o.z();
	}
	
	
	public Vec3 cross(Vec3 o) {
		return xyz(
			y() * o.z() - z() * o.y(),
			z() * o.x() - x() * o.z(),
			x() * o.y() - y() * o.x()
		);
	}
	
	
	public Vec3 projection(Vec3 d) {
		return d.mul(this.dot(d) / d.lengthSquared());
	}
	

	public Vec3 projectionN(Vec3 d_) {
		return d_.mul(this.dot(d_));
	}
	
	
	public Vec3 rejection(Vec3 d) {
		return this.sub(projection(d));
	}
	
	
	public Vec3 rejectionN(Vec3 d_) {
		return this.sub(projectionN(d_));
	}
	
	
	public double min() {
		return Math.min(Math.min(x(), y()), z());
	}

	
	public double max() {
		return Math.max(Math.max(x(), y()), z());
	}
	
	
	public Vec3 minIndicator() {
		if (x() < y()) {
			return x() < z() ? Vec3.EX : Vec3.EZ;
		} else {
			return y() < z() ? Vec3.EY : Vec3.EZ;
		}
	}
	
	
	public Vec3 maxIndicator() {
		if (x() > y()) {
			return x() > z() ? Vec3.EX : Vec3.EZ;
		} else {
			return y() > z() ? Vec3.EY : Vec3.EZ;
		}
	}
	
	
	public int minIndex() {
		if (x() < y()) {
			return x() < z() ? 0 : 2;
		} else {
			return y() < z() ? 1 : 2;
		}
	}
	
	
	public int maxIndex() {
		if (x() > y()) {
			return x() > z() ? 0 : 2;
		} else {
			return y() > z() ? 1 : 2;
		}
	}
	
	
	public boolean isZero() {
		return (x() == 0) && (y() == 0) && (z() == 0);
	}
	
	
	@Override
	public String toString() {
		return String.format("(%6.2f, %6.2f, %6.2f)", x(), y(), z());
	}
	
	
	
	public static Vec3 min(Vec3 a, Vec3 b) {
		return xyz(
				Math.min(a.x(), b.x()),
				Math.min(a.y(), b.y()),
				Math.min(a.z(), b.z())
		);
	}
	
	
	public static Vec3 max(Vec3 a, Vec3 b) {
		return xyz(
				Math.max(a.x(), b.x()),
				Math.max(a.y(), b.y()),
				Math.max(a.z(), b.z())
		);
	}
	
	
	public static Vec3 lerp(Vec3 v0, Vec3 v1, double t) {
		return v0.mul(1 - t).add(v1.mul(t));
	}
}
