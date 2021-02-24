package graphics3d;

import mars.utils.Numeric;

public class Transform {
	
	public static Transform IDENTITY = new Transform(
			1.0, 0.0, 0.0, 0.0,
			0.0, 1.0, 0.0, 0.0,
			0.0, 0.0, 1.0, 0.0
	);



	private final double
			m00, m01, m02, m03,
			m10, m11, m12, m13,
			m20, m21, m22, m23;
	//        0,   0,   0,   1
	

	public Transform(
			double m00, double m01, double m02, double m03,
			double m10, double m11, double m12, double m13,
			double m20, double m21, double m22, double m23
	) {
		this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
		this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
		this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
	}
	

	public Transform andThen(Transform t) {
		return new Transform(
				t.m00*m00 + t.m01*m10 + t.m02*m20, t.m00*m01 + t.m01*m11 + t.m02*m21, t.m00*m02 + t.m01*m12 + t.m02*m22, t.m00*m03 + t.m01*m13 + t.m02*m23 + t.m03,
				t.m10*m00 + t.m11*m10 + t.m12*m20, t.m10*m01 + t.m11*m11 + t.m12*m21, t.m10*m02 + t.m11*m12 + t.m12*m22, t.m10*m03 + t.m11*m13 + t.m12*m23 + t.m13,
				t.m20*m00 + t.m21*m10 + t.m22*m20, t.m20*m01 + t.m21*m11 + t.m22*m21, t.m20*m02 + t.m21*m12 + t.m22*m22, t.m20*m03 + t.m21*m13 + t.m22*m23 + t.m23
		);
	}
	
	
	public Vec3 applyTo(Vec3 v) {
		return new Vec3(
				m00*v.x() + m01*v.y() + m02*v.z() + m03,
				m10*v.x() + m11*v.y() + m12*v.z() + m13,
				m20*v.x() + m21*v.y() + m22*v.z() + m23
		);
	}
	
	
	public Vec3 applyWithoutTranslationTo(Vec3 v) {
		return new Vec3(
				m00*v.x() + m01*v.y() + m02*v.z(),
				m10*v.x() + m11*v.y() + m12*v.z(),
				m20*v.x() + m21*v.y() + m22*v.z()
		);
	}
	
	
	public Ray applyTo(Ray r) {
		return new Ray(applyTo(r.p()), applyWithoutTranslationTo(r.d()));
	}
	

	public Transform inverse() {
		double det = determinant();
		return new Transform(
				 (m11*m22 - m12*m21)/det, -(m01*m22 - m02*m21)/det,  (m01*m12 - m02*m11)/det, -(m01*m12*m23 + m02*m13*m21 + m03*m11*m22 - m03*m12*m21 - m02*m11*m23 - m01*m13*m22)/det,
				-(m10*m22 - m12*m20)/det,  (m00*m22 - m02*m20)/det, -(m00*m12 - m02*m10)/det,  (m00*m12*m23 + m02*m13*m20 + m03*m10*m22 - m03*m12*m20 - m02*m10*m23 - m00*m13*m22)/det,
				 (m10*m21 - m11*m20)/det, -(m00*m21 - m01*m20)/det,  (m00*m11 - m01*m10)/det, -(m00*m11*m23 + m01*m13*m20 + m03*m10*m21 - m03*m11*m20 - m01*m10*m23 - m00*m13*m21)/det
				);
	}
	
	
	public Transform transposeWithoutTranslation() {
		return new Transform(
				m00, m10, m20, 0,
				m01, m11, m21, 0,
				m02, m12, m22, 0
		);
	}
	
	
	private double determinant() {
		return (
				+ (m00 * m11 * m22)
				+ (m01 * m12 * m20)
				+ (m02 * m10 * m21)
				- (m02 * m11 * m20)
				- (m01 * m10 * m22)
				- (m00 * m12 * m21)
		);
	}
	

	public static Transform translation(Vec3 d) {
		return new Transform(
				1.0, 0.0, 0.0, d.x(),
				0.0, 1.0, 0.0, d.y(),
				0.0, 0.0, 1.0, d.z()
		);
	}

	public static Transform rotationAboutX(double angle) {
		return new Transform(
				1.0, 0.0, 0.0, 0.0,
				0.0, Numeric.cosT(angle), -Numeric.sinT(angle), 0.0,
				0.0, Numeric.sinT(angle), Numeric.cosT(angle), 0.0
		);
	}

	public static Transform rotationAboutY(double angle) {
		return new Transform(
				Numeric.cosT(angle), 0.0, Numeric.sinT(angle), 0.0,
				0.0, 1.0, 0.0, 0.0,
				-Numeric.sinT(angle), 0.0, Numeric.cosT(angle), 0.0
		);
	}

	public static Transform rotationAboutZ(double angle) {
		return new Transform(
				Numeric.cosT(angle), -Numeric.sinT(angle), 0.0, 0.0,
				Numeric.sinT(angle), Numeric.cosT(angle), 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0
		);
	}

	public static Transform scaling(double c) {
		return new Transform(
				c, 0.0, 0.0, 0.0,
				0.0, c, 0.0, 0.0,
				0.0, 0.0, c, 0.0
		);
	}
	
	public static Transform scaling(Vec3 c) {
		return new Transform(
				c.x(), 0.0, 0.0, 0.0,
				0.0, c.y(), 0.0, 0.0,
				0.0, 0.0, c.z(), 0.0
		);
	}
	
}
