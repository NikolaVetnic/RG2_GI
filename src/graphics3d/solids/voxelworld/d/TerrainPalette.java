package graphics3d.solids.voxelworld.d;

import graphics3d.Color;
import graphics3d.Vec3;

public class TerrainPalette {

	Color[] c;
	double[] h;
	boolean arid;
	
	protected TerrainPalette(Color[] c, double[] h, boolean arid) {
		this.c = c;
		this.h = h;
		this.arid = arid;
	}

	
	public static final TerrainPalette PASTORAL = new TerrainPalette(
			new Color[] { 
					Color.hsb(210, 0.70, 0.8), 		// duboka voda
					Color.hsb(210, 0.70, 0.9), 		// voda
					Color.hsb( 50, 0.50, 1.0), 		// pesak
					Color.hsb(110, 0.60, 0.8), 		// trava
					Color.hsb( 30, 0.60, 0.5), 		// planina
					Color.hsb(  0, 0.00, 1.0)  		// sneg
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 }, false );
	
	
	public static final TerrainPalette PASTORAL_SHALLOW = new TerrainPalette(
			new Color[] { 
					Color.hsb(210, 0.70, 0.8), 		// duboka voda
					Color.hsb(210, 0.70, 0.9), 		// voda
					Color.hsb( 50, 0.50, 1.0), 		// pesak
					Color.hsb(110, 0.60, 0.8), 		// trava
					Color.hsb( 30, 0.60, 0.5), 		// planina
					Color.hsb(  0, 0.00, 1.0)  		// sneg
			}, new double[] { -0.6, -0.4, 0.1, 0.4, 0.8 }, false );
	
	
	public static final TerrainPalette ARCTIC = new TerrainPalette(
			new Color[] { 
					Color.hsb(180, 0.70, 0.50),		// duboka voda
					Color.hsb(210, 0.70, 0.90),		// voda
					Color.hsb( 30, 0.45, 0.30),		// blatnjava obala
					Color.hsb(210, 0.30, 0.90),		// ravnica
					Color.hsb(210, 0.15, 0.95),		// bregovi
					Color.hsb(210, 0.05, 0.95)		// planine
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 }, false );
	
	
	public static final TerrainPalette ARCTIC_SHALLOW = new TerrainPalette(
			new Color[] { 
					Color.hsb(180, 0.70, 0.50),		// duboka voda
					Color.hsb(210, 0.70, 0.90),		// voda
					Color.hsb( 30, 0.45, 0.30),		// blatnjava obala
					Color.hsb(210, 0.30, 0.90),		// ravnica
					Color.hsb(210, 0.15, 0.95),		// bregovi
					Color.hsb(210, 0.05, 0.95)		// planine
			}, new double[] { -0.6, -0.4, -0.3, 0.4, 0.8 }, false );
	
	
	public static final TerrainPalette DUNE = new TerrainPalette(
			new Color[] { 
					Color.hsb( 47, 0.06, 0.91),		// svetli erg
					Color.hsb( 35, 0.24, 0.92),		// tamniji erg
					Color.hsb( 26, 0.41, 0.94),		// niske stene
					Color.hsb( 21, 0.44, 0.86),		// visoke stene
					Color.hsb( 19, 0.49, 0.78),		// visoravan
					Color.hsb(  0, 0.05, 0.46)		// vrhovi
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 }, true );
	
	
	public static final TerrainPalette MARS = new TerrainPalette(
			new Color[] { 
					Color.hsb( 16, 0.30, 0.76),		// 
					Color.hsb( 18, 0.39, 0.67),		// 
					Color.hsb( 19, 0.49, 0.61),		// 
					Color.hsb( 18, 0.48, 0.48),		// 
					Color.hsb( 15, 0.52, 0.38),		// 
					Color.hsb( 16, 0.46, 0.22)		// 
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 }, true );

	
	public Color[] colors() 	{ return c; 	}
	public double[] heights() 	{ return h; 	}
	public boolean isArid()		{ return arid; 	}


	public Color colorAtHeight(double value, double min, double max)	{

		double valueScaled = 2.0 * (value - min) / (max - min) - 1.0;

		Color output = c[5];

		if (valueScaled < h[4]) output = c[4];
		if (valueScaled < h[3]) output = c[3];
		if (valueScaled < h[2]) output = c[2];
		if (valueScaled < h[1]) output = c[1];
		if (valueScaled < h[0]) output = c[0];

		return output;
	}


	public Color colorAtHeight(double value, double max)	{
		return colorAtHeight(value, 0, max);
	}


	public Color colorAtHeightMixedWithImageValue(double value, double max, javafx.scene.paint.Color imageValue) {
		return Color.rgb(imageValue.getRed(), imageValue.getGreen(), imageValue.getBlue()).mul(colorAtHeight(value, max));
	}


	public Color lerpedColorAtHeightMixedWithImageValue(double value, double max, javafx.scene.paint.Color imageValue) {
		return Color.rgb(imageValue.getRed(), imageValue.getGreen(), imageValue.getBlue()).mul(getLerpedColor(value));
	}


	public double heightNormalized(int idx) {
		return (heights()[idx] + 1.0) * 0.5;
	}


	public Color getLerpedColor(double value) {

		value = value * 2.0 - 1.0;

		Color c0 = colors()[0];		double h0 = -1.0;
		Color c1 = colors()[1];		double h1 = heights()[0];

		for (int i = 0; i < heights().length; i++) {
			if (value > heights()[i]) {
				c0 = colors()[i];		h0 = heights()[i];
				c1 = colors()[i + 1];	h1 = i + 1 < heights().length ? heights()[i + 1] : 1.0;
			} else {
				break;
			}
		}

		h1 -= h0;
		value = (value - h0) / h1;

		double r0 = c0.r(), g0 = c0.g(), b0 = c0.b();
		double r1 = c1.r(), g1 = c1.g(), b1 = c1.b();

		double r = r0 < r1 ? r0 + (r1 - r0) * value : r0 - (r0 - r1) * value;
		double g = g0 < g1 ? g0 + (g1 - g0) * value : g0 - (g0 - g1) * value;
		double b = b0 < b1 ? b0 + (b1 - b0) * value : b0 - (b0 - b1) * value;

		return Color.rgb(r, g, b);
	}
}
