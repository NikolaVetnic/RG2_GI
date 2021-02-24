package graphics3d;


import mars.geometry.Vector;

import static graphics3d.Color.BLACK;
import static graphics3d.Color.WHITE;

/**
 * Properties of a surface at a single point.
 */
public class Material implements Texture {
	
	private final Color diffuse, specular, reflective, refractive;
	private final double shininess, refractiveIndex;
	private final Color emittance;
	private final BSDF bsdf;
	
	
	
	public Material(Color diffuse, Color specular, double shininess, Color reflective, Color refractive, double refractionIndex, Color emittance) {
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
		this.reflective = reflective;
		this.refractive = refractive;
		this.refractiveIndex = refractionIndex;
		this.emittance = emittance;
		
		this.bsdf = BSDF.add(
				new BSDF[] {
						BSDF.diffuse   (diffuse),
						BSDF.reflective(reflective),
						BSDF.refractive(refractive, refractiveIndex)
				},
				new double[] {
						diffuse.luminance(),
						reflective.luminance(),
						refractive.luminance()
				}
		);
	}
	
	
	public Material(BSDF bsdf) {
		this.diffuse = BLACK;
		this.specular = BLACK;
		this.shininess = 0.0;
		this.reflective = BLACK;
		this.refractive = BLACK;
		this.refractiveIndex = 1.0;
		this.emittance = BLACK;
		
		this.bsdf = bsdf;
	}
	
	
	public Material(Color diffuse) {
		this(diffuse, BLACK, 32, BLACK, BLACK, 1, Color.BLACK);
	}
	
	public Material(Color diffuse, Color specular, double shininess) {
		this(diffuse, specular, shininess, BLACK, BLACK, 1, Color.BLACK);
	}
	
	public Material(Color diffuse, Color specular, double shininess, Color refractive) {
		this(diffuse, specular, shininess, refractive, BLACK, 1, Color.BLACK);
	}
	
	public Material(Color diffuse, Color specular, double shininess, Color reflective, Color refractive, double refractionIndex) {
		this(diffuse, specular, shininess, reflective, refractive, refractionIndex, BLACK);
	}
	
	
	
	/** The basic color of an object. The incoming light multiplied by this color is scattered in every direction equally. */
	public Color diffuse() {
		return diffuse;
	}
	
	public double shininess() {
		return shininess;
	}
	
	public Color specular() {
		return specular;
	}
	
	public Color reflective() {
		return reflective;
	}
	
	public Color refractive() {
		return refractive;
	}
	
	public double refractiveIndex() {
		return refractiveIndex;
	}
	
	public Color emittance() {
		return emittance;
	}
	
	public BSDF bsdf() {
		return bsdf;
	}
	
	
	
	// Factory methods for some specific materials.

	public static final Material ABSORPTIVE = diffuse(BLACK);
	public static final Material DIFFUSE    = diffuse(WHITE);
	public static final Material GLASS      = glass(1.5, 0.04);
	public static final Material MIRROR     = mirror(WHITE);
	public static final Material LIGHT      = light(WHITE);
	
	
	public static Material diffuse(double k) {
		return diffuse(Color.gray(k));
	}
	
	public static Material diffuse(Color diffuse) {
		return new Material(diffuse);
	}
	
	public static Material light(double k) {
		return light(Color.gray(k));
	};
	
	public static Material light(Color color) {
		return new Material(BLACK, BLACK, 0, BLACK, BLACK, 1, color);
	};
	
	public static Material mirror(double k) {
		return mirror(Color.gray(k));
	}
	
	public static Material mirror(Color color) {
		return new Material(BLACK, BLACK, 0, color, BLACK, 1, Color.BLACK);
	}
	
	public static Material glass(double refractiveIndex, double reflectance) {
		return new Material(BLACK, BLACK, 0, Color.gray(reflectance), Color.gray(1 - reflectance), refractiveIndex, Color.BLACK);
	}
	
	public static Material glass(Color color, double refractiveIndex, double reflectance) {
		return new Material(BLACK, BLACK, 0, Color.gray(reflectance), color.mul(1 - reflectance), refractiveIndex, Color.BLACK);
	}
	
	@Override
	public Material materialAt(Vector uv) {
		return this;
	}
}
