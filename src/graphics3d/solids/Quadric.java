package graphics3d.solids;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class Quadric implements Solid {
	
	
	/*
	 * The class of quadrics (surfaces that can be defined by a quadra-
	 * tic equation) include cylinders, cones, ellipsoids, paraboloids,
	 * etc. Note that spheres and planes are a special subclass but ha-
	 * ve faster routines as special cases.
	 * 
	 * The general quadric surface equation is
	 * 
	 * F(x, y, z) = Ax2 + By2 + Cz2 + 
	 * 				Dxy + Exz + Fyz + 
	 * 				Gx  + Hy  + Iz  + J = 0
	 * 
	 * More info: http://skuld.bmsc.washington.edu/people/merritt/grap-
	 * hics/quadrics.html
	 * 
	 * LEGEND:
	 * type == 0, sphere
	 * type == 1, cylinder
	 * type == 2, cone
	 * type == 3, ellipsoid
	 * type == 4, paraboloid
	 * type == 5, hyperboloid (one sheet)
	 * type == 6, hyperboloid (two sheet)
	 * type == 7, hyperbolic paraboloid
	 */
	
	
	private final double A, B, C, D, E, F, G, H, I, J;
	private final int type;
	
		
	private Quadric(double A, double B, double C,
					double D, double E, double F,
					double G, double H, double I, 
					double J, 
					int type) {
		this.A = A; this.B = B; this.C = C;
		this.D = D; this.E = E; this.F = F;
		this.G = G; this.H = H; this.I = I;
		this.J = J; 
		this.type = type;
	}
	
	
	/**
	 * Creates a quadric sphere with set parameters.
	 * 
	 * @param A coefficient of x^2
	 * @param B coefficient of y^2
	 * @param C coefficient of z^2
	 * @param J free term
	 */
	public static Quadric sphere(double A, double B, double C, double J) {

		if (A <= 0 || B <= 0 || C <= 0 || J <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		return new Quadric(A, B, C, 0, 0, 0, 0, 0, 0, -J, 0);
	}
	
	/**
	 * Creates a quadric sphere with default parameters:<br>
	 * A = 100<br>
	 * B = 100<br>
	 * C = 100<br>
	 * J = -100
	 */
	public static Quadric sphere() {
		return new Quadric(100, 100, 100, 0, 0, 0, 0, 0, 0, -100, 0);
	}
	
	/**
	 * Creates a quadric cylinder along an axis with set parameters.
	 * 
	 * @param P coefficient of x^2 or y^2 (depending on axis)
	 * @param Q coefficient of y^2 or z^2
	 * @param axis 0: x axis (default), 1: y axis, 2: z axis
	 */
	public static Quadric cylinder(double P, double Q, int axis) {
		
		if (P <= 0 || Q <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric(P, Q, 0, 0, 0, 0, 0, 0, 0, -1, 1);
		else if (axis == 1) return new Quadric(P, 0, Q, 0, 0, 0, 0, 0, 0, -1, 1);
		else 				return new Quadric(0, P, Q, 0, 0, 0, 0, 0, 0, -1, 1);
	}

	/**
	 * Creates a quadric cylinder with defualt parameters:<br>
	 * B = 100<br>
	 * C = 100<br>
	 * J = -1
	 */
	public static Quadric cylinder() {
		return new Quadric(0, 100, 100, 0, 0, 0, 0, 0, 0, -1, 1);
	}
	
	/**
	 * Creates a quadric cone along an axis with set parameters.
	 * 
	 * @param A coefficient of x^2
	 * @param B coefficient of y^2
	 * @param C coefficient of z^2
	 * @param axis 0: x axis (default), 1: y axis, 2: z axis
	 */
	public static Quadric cone(double A, double B, double C, int axis) {

		if (A <= 0 || B <= 0 || C <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric(-A,  B,  C, 0, 0, 0, 0, 0, 0, 0, 2);
		else if (axis == 1) return new Quadric( A, -B,  C, 0, 0, 0, 0, 0, 0, 0, 2);
		else 				return new Quadric( A,  B, -C, 0, 0, 0, 0, 0, 0, 0, 2);
	}
	
	/**
	 * Creates a quadric cone with defualt parameters:<br>
	 * A = -100<br>
	 * B = 100<br>
	 * C = 100<br>
	 */
	public static Quadric cone() {
		return new Quadric(-100, 100, 100, 0, 0, 0, 0, 0, 0, 0, 2);
	}
	
	/**
	 * Creates a quadric ellipsoid with set parameters.
	 * 
	 * @param a coefficient of x^2 scaling factor
	 * @param b coefficient of y^2 scaling factor
	 * @param c coefficient of z^2 scaling factor
	 * @param j free term scaling factor
	 */
	public static Quadric ellipsoid(double a, double b, double c, double j) {

		if (a <= 0 || b <= 0 || c <= 0 || j <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		return new Quadric(a*100, b*100, c*100, 0, 0, 0, 0, 0, 0, -j*100, 3);
	}
	
	/**
	 * Creates a quadric ellipsoid with default parameters:<br>
	 * A = 100<br>
	 * B = 200<br>
	 * C = 400<br>
	 * J = -100
	 */
	public static Quadric ellipsoid() {
		return new Quadric(100, 200, 400, 0, 0, 0, 0, 0, 0, -100, 3);
	}
	
	/**
	 * Creates a quadric paraboloid along an axis with set parameters.
	 * 
	 * @param P coefficient of x^2 or y^2 (depending on axis)
	 * @param Q coefficient of y^2 or z^2
	 * @param R coefficient of x, y or z
	 */
	public static Quadric paraboloid(double P, double Q, double R, int axis) {

		if (P <= 0 || Q <= 0 || R <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric(P, Q, 0, 0, 0, 0,  0,  0, -R, 0, 4);
		else if (axis == 1) return new Quadric(P, 0, Q, 0, 0, 0,  0, -R,  0, 0, 4);
		else 				return new Quadric(0, P, Q, 0, 0, 0, -R,  0,  0, 0, 4);
	}
	
	/**
	 * Creates a quadric paraboloid with default parameters:<br>
	 * B = 100<br>
	 * C = 100<br>
	 * G = -100
	 */
	public static Quadric paraboloid() {
		return new Quadric(0, 100, 100, 0, 0, 0, -100, 0, 0, 0, 4);
	}
	
	/**
	 * Creates a quadric hyperboloid (one sheet) along an axis with set
	 *  parameters.
	 * 
	 * @param A coefficient of x^2
	 * @param B coefficient of y^2
	 * @param C coefficient of z^2
	 */
	public static Quadric hyperboloid1(double A, double B, double C, int axis) {

		if (A <= 0 || B <= 0 || C <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric( A,  B, -C, 0, 0, 0, 0, 0, 0, -1, 5);
		else if (axis == 1) return new Quadric( A, -B,  C, 0, 0, 0, 0, 0, 0, -1, 5);
		else 				return new Quadric(-A,  B,  C, 0, 0, 0, 0, 0, 0, -1, 5);
	}
	
	/**
	 * Creates a quadric hyperboloid (one sheet) with default parameter
	 * s:<br>
	 * A = -100<br>
	 * B = 100<br>
	 * C = 100<br>
	 * J = -1
	 */
	public static Quadric hyperboloid1() {
		return new Quadric(-100, 100, 100, 0, 0, 0, 0, 0, 0, -1, 5);
	}
	
	/**
	 * Creates a quadric hyperboloid (two sheet) along an axis with set
	 *  parameters.
	 * 
	 * @param A coefficient of x^2
	 * @param B coefficient of y^2
	 * @param C coefficient of z^2
	 * @param G coefficient of x
	 * @param H coefficient of y
	 * @param I coefficient of z
	 * @param J free term
	 */
	public static Quadric hyperboloid2(double A, double B, double C, 
									   double G, double H, double I, 
									   double J, int axis) {

		if (A <= 0 || B <= 0 || C <= 0 || G <= 0 || H <= 0 || I <= 0 || J <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric( A,  B, -C, 0, 0, 0, 0, 0, 0, -1, 6);
		else if (axis == 1) return new Quadric( A, -B,  C, 0, 0, 0, 0, 0, 0, -1, 6);
		else 				return new Quadric(-A,  B,  C, 0, 0, 0, 0, 0, 0, -1, 6);
	}
	
	/**
	 * Creates a quadric hyperboloid (two sheet) with default parameter
	 * s:<br>
	 * A = 100<br>
	 * B = -100<br>
	 * C = 100<br>
	 * G = -100<br>
	 * H = -100<br>
	 * I = -100<br>
	 * J = 100
	 */
	public static Quadric hyperboloid2() {
		return new Quadric(100, -100, 100, 0, 0, 0, -100, -100, -100, 100, 6);
	}
	
	/**
	 * Creates a quadric hyperbolic paraboloid along an axis with set p
	 * arameters.
	 * 
	 * @param A coefficient of x^2
	 * @param B coefficient of y^2
	 * @param C coefficient of z^2
	 * @param I coefficient of z
	 */
	public static Quadric hparaboloid(double P, double Q, double J, int axis) {

		if (P <= 0 || Q <= 0 || J <= 0)
			throw new IllegalArgumentException("Coefficients must be positive doubles!");
		
		if 		(axis == 2) return new Quadric( 0, -P,  Q, 0, 0, 0, 0, 0, -J, 0, 5);
		else if (axis == 1) return new Quadric( P,  0, -Q, 0, 0, 0, 0, 0, -J, 0, 5);
		else 				return new Quadric(-P,  Q,  0, 0, 0, 0, 0, 0, -J, 0, 5);
	}
	
	/**
	 * Creates a quadric hyperboloid (one sheet) with default parameter
	 * s:<br>
	 * A = -100<br>
	 * B = 100<br>
	 * C = 100<br>
	 * J = -1
	 */
	public static Quadric hparaboloid() {
		return new Quadric(-100, 100, 0, 0, 0, 0, 0, 0, -100, 0, 5);
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
				
		double xd = ray.d().x(), yd = ray.d().y(), zd = ray.d().z();
		double xo = ray.p().x(), yo = ray.p().y(), zo = ray.p().z();
		
		double Aq = A*xd*xd + B*yd*yd + C*zd*zd + D*xd*yd + E*xd*zd + F*yd*zd;
		double Bq = 2*A*xo*xd + 2*B*yo*yd + 2*C*zo*zd + D*(xo*yd + yo*xd) + E*(xo*zd + zo*xd) + F*(yo*zd + yd*zo) + G*xd + H*yd + I*zd;
		double Cq = A*xo*xo + B*yo*yo + C*zo*zo + D*xo*yo + E*xo*zo + F*yo*zo + G*xo + H*yo + I*zo + J;
		
		if (Math.abs(Aq) < 1e-8) {
			
			double t = -Cq / Bq;
			Vec3 Ri = ray.at(t);
			
			double xi = Ri.x(), yi = Ri.y(), zi = Ri.z();
			
			Vec3 Rn = Vec3.xyz(
					2*A*xi + D*yi + E*zi + G, 
					2*B*yi + D*xi + F*zi + H, 
					2*C*zi + E*xi + F*yi + I).normalized_();
			
			if (ray.at(t).dot(ray.d()) > 0) Rn = Rn.inverse();
			
			return new Hit[] { HitData.tn(t, Rn) };
		}
		
		double Dq = Bq*Bq - 4*Aq*Cq;
		
		if (Dq <= 0.0)
			return Solid.NO_HITS;
		
		double DqSqrt = Math.sqrt(Dq);
		
		double t0 = (-Bq - DqSqrt) / (2 * Aq);
		double t1 = (-Bq + DqSqrt) / (2 * Aq);
		
		if (t0 > t1) { double tmp = t0; t0 = t1; t1 = tmp; }
		
		Vec3 Ri0 = ray.at(t0);
		double xi0 = Ri0.x(), yi0 = Ri0.y(), zi0 = Ri0.z();
		
		Vec3 Rn0 = Vec3.xyz(
				2*A*xi0 + D*yi0 + E*zi0 + G, 
				2*B*yi0 + D*xi0 + F*zi0 + H, 
				2*C*zi0 + E*xi0 + F*yi0 + I).normalized_();
		
		Vec3 Ri1 = ray.at(t1);
		double xi1 = Ri1.x(), yi1 = Ri1.y(), zi1 = Ri1.z();
		
		Vec3 Rn1 = Vec3.xyz(
				2*A*xi1 + D*yi1 + E*zi1 + G, 
				2*B*yi1 + D*xi1 + F*zi1 + H, 
				2*C*zi1 + E*xi1 + F*yi1 + I).normalized_();
		
		return new Hit[] { HitData.tn(t0, Rn0), HitData.tn(t1, Rn1) };
	}
	

//	@Override
//	public Vector uv(Vec3 q) {
//		
//		// WORK IN PROGRESS		
//		
//		Vec3 a = q.sub(Vec3.ZERO);
//		
//		Vec3 n = Vec3.EY;
//		
//		double theta = Numeric.atan2T(a.z(), a.x());
//		double phi = Numeric.atan2T(Math.sqrt(a.x()*a.x() + a.z()*a.z()), a.y());
//		
//		return Vector.xy(theta, phi);
//	}
	
	
	@Override
	public String toString() {
		
		String out = "";
		
		switch (type) {
			case 0 -> out += "SPHERE [ ";
			case 1 -> out += "CYLINDER [ ";
			case 2 -> out += "CONE [ ";
			case 3 -> out += "ELLIPSOID [ ";
			case 4 -> out += "PARABOLOID [ ";
			case 5 -> out += "HYPERBOLOID (1-SHEET) [ ";
			case 6 -> out += "HYPERBOLOID (2-SHEET) [ ";
			case 7 -> out += "HYPERBOLIC PARABOLOID [ ";
		}
		
		if (A != 0) out += "A = " + A + ", ";	if (B != 0) out += "B = " + B + ", ";	if (B != 0) out += "B = " + B + ", ";
		if (D != 0) out += "D = " + D + ", ";	if (E != 0) out += "E = " + E + ", ";	if (F != 0) out += "F = " + F + ", ";
		if (G != 0) out += "G = " + G + ", ";	if (H != 0) out += "H = " + I + ", ";	if (I != 0) out += "I = " + I + ", ";
		if (J != 0) out += "J = " + J + ", ";
		
		return out.substring(0, out.length() - 2) + " ]";
	}
}
