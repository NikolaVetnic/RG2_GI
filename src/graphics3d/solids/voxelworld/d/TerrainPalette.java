package graphics3d.solids.voxelworld.d;

import graphics3d.Color;

public class TerrainPalette {

	Color[] c;
	double[] h;
	
	protected TerrainPalette(Color[] c, double[] h) {
		this.c = c;
		this.h = h;
	}

	
	public static final TerrainPalette PASTORAL = new TerrainPalette(
			new Color[] { 
					Color.hsb(210, 0.70, 0.8), 		// duboka voda
					Color.hsb(210, 0.70, 0.9), 		// voda
					Color.hsb( 50, 0.50, 1.0), 		// pesak
					Color.hsb(110, 0.60, 0.8), 		// trava
					Color.hsb( 30, 0.60, 0.5), 		// planina
					Color.hsb(  0, 0.00, 1.0)  		// sneg
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 } );
	
	
	public static final TerrainPalette ARCTIC = new TerrainPalette(
			new Color[] { 
					Color.hsb(180, 0.70, 0.50),		// duboka voda
					Color.hsb(210, 0.70, 0.90),		// voda
					Color.hsb( 30, 0.45, 0.30),		// blatnjava obala
					Color.hsb(210, 0.30, 0.90),		// ravnica
					Color.hsb(210, 0.15, 0.95),		// bregovi
					Color.hsb(210, 0.05, 0.95)		// planine
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 } );
	
	
	public static final TerrainPalette DUNE = new TerrainPalette(
			new Color[] { 
					Color.hsb( 47, 0.06, 0.91),		// svetli erg
					Color.hsb( 35, 0.24, 0.92),		// tamniji erg
					Color.hsb( 26, 0.41, 0.94),		// niske stene
					Color.hsb( 21, 0.44, 0.86),		// visoke stene
					Color.hsb( 19, 0.49, 0.78),		// visoravan
					Color.hsb(  0, 0.05, 0.46)		// vrhovi
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 } );
	
	
	public static final TerrainPalette MARS = new TerrainPalette(
			new Color[] { 
					Color.hsb( 16, 0.30, 0.76),		// 
					Color.hsb( 18, 0.39, 0.67),		// 
					Color.hsb( 19, 0.49, 0.61),		// 
					Color.hsb( 18, 0.48, 0.48),		// 
					Color.hsb( 15, 0.52, 0.38),		// 
					Color.hsb( 16, 0.46, 0.22)		// 
			}, new double[] { -0.5, 0.0, 0.1, 0.4, 0.8 } );

	
	public Color[] colors() 	{ return c; }
	public double[] heights() 	{ return h; }
}
