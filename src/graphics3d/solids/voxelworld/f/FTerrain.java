package graphics3d.solids.voxelworld.f;

import java.util.function.Function;

import graphics3d.Color;
import graphics3d.Vec3;
import graphics3d.solids.voxelworld.d.TerrainPalette;
import graphics3d.solids.voxelworld.d.VoxelData;
import mars.random.RNG;
import mars.random.fixed.continuous.PerlinNoise;

public class FTerrain extends FGridMarch2O {
	
	
	static PerlinNoise[] pn;
	
	static TerrainPalette terrainPalette;
	
	static double 	zoom = 1;
	static int 		nLevels = 8;
	static double 	z = 0.5;
	static double 	factor = 1.62;
	
	static boolean 	archipelago = false;
	static boolean	flatSea		= true;

	
	protected FTerrain(Vec3 d, Function<Vec3, VoxelData> f) {
		super(d, f);
		setup();
	}
	
	
	void setup() {
		pn = new PerlinNoise[10];
		RNG rng = new RNG();
		
		for (int i = 0; i < pn.length; i++)
			pn[i] = new PerlinNoise(rng.nextLong());
	}
	
	
	public static FTerrain newTFS(Vec3 d, Function<Vec3, VoxelData> f) {
		return new FTerrain(d, f);
	}
	
	
	public static FTerrain newTerrain(Vec3 dim, TerrainPalette palette, boolean flatSea) {
		
		return new FTerrain(dim, v -> {
			
			double dx = (v.x() / dim.xInt());
			double dy = (v.y() / dim.yInt());
			
			dx *= zoom;
			dy *= zoom;
			
			double amplitude = 1.0;
			double frequency = 1.0;
			double h = 0.0;
			
			for (int l = 0; l < nLevels; l++) {
				
				h += amplitude * pn[l].getValue(dx * frequency, dy * frequency, z);
				amplitude /= factor;
				frequency *= factor;
			}
			
			if (archipelago) {
				
				double dd = Math.sqrt(Math.pow(dx * 2.0 - 1.0, 2) + Math.pow(dy * 2.0 - 1.0, 2));
				
				dd = dd > 1.0 ? 1.0 : dd;
				dd = dd * 2.0 - 1.0;
				
				h = archipelago ? h - dd : h;
			}
			
			Color c = palette.colors()[5];
			
			if (h < palette.heights()[4]) c = palette.colors()[4];
			if (h < palette.heights()[3]) c = palette.colors()[3];
			if (h < palette.heights()[2]) c = palette.colors()[2];
			if (h < palette.heights()[1]) c = palette.colors()[1];
			if (h < palette.heights()[0]) c = palette.colors()[0];
			
			if (flatSea)
				h = h < 0.0 ? 0.0 : h;
			
			return 2.0 * v.z() / dim.z() - 1.0 <= h ? VoxelData.create(c) : VoxelData.UNPOPULATED;
		});
	}
}
