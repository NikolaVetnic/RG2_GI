package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class DirArray extends Base {
	

	/********************************************************************
	 * 																	*
	 * ID : 02															*
	 * 																	*
	 * Description: uses array to store hits, the direction in which t-	*
	 * he voxel array is iterrated is based on the ray direction, disc-	*
	 * ards hits between adjecent voxels; the array	is trimmed, put out	* 
	 * as usual; firstHit implemented.									*
	 * 																	*
	 *******************************************************************/

	
	protected DirArray(Color[][][] v) {
		super(v);
	}
	
	
	public static DirArray arr(Color[][][] arr)				{ return new DirArray(arr); 						}
	public static DirArray set(String baseLayerPath) throws IOException 		
															{ return new DirArray(Loaders.set(baseLayerPath)); 	}
	public static DirArray line(Vec3 p, Vec3 q, Color c) 	{ return new DirArray(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {
					
					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime) return h[0];
				}
			}
		}

		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
				
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {
					
					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (num > 0) {
						if (Math.abs(hits[num - 1].t() - h[0].t()) > 1e-8)
							hits[num++] = h[0];
						else
							num--;
					} else {
						hits[num++] = h[0];
					}
					
					hits[num++] = h[1];
				}
			}
		}
		
		if (num == 0) return Solid.NO_HITS;
		
		return Arrays.copyOf(hits, num);
	}
}
