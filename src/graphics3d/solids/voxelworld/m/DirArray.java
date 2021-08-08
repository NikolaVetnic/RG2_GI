package graphics3d.solids.voxelworld.m;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.voxelworld.a.BaseM;
import graphics3d.solids.voxelworld.d.HitVoxel;
import graphics3d.solids.voxelworld.d.ModelData3;
import graphics3d.solids.voxelworld.d.ModelData4;
import graphics3d.solids.voxelworld.u.Loaders;
import graphics3d.solids.voxelworld.u.Util;

public class DirArray extends BaseM {
	

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

	
	protected DirArray(boolean[][][] arr0) 						{ super(arr0); 			}
	protected DirArray(boolean[][][] arr0, Color[][][] arr1) 	{ super(arr0, arr1); 	}
	protected DirArray(ModelData3 data) 						{ super(data); 			}
	protected DirArray(ModelData4 data) 						{ super(data); 			}
	
	
	public static DirArray model(boolean[][][] arr0)						{ return new DirArray(arr0); 							}
	public static DirArray model(boolean[][][] arr0, Color[][][] arr1)		{ return new DirArray(arr0, arr1); 						}
	public static DirArray set(String baseLayerPath) throws IOException 	{ return new DirArray(Loaders.set(baseLayerPath)); 		}
	public static DirArray map(String baseLayerPath) throws IOException 	{ return new DirArray(Loaders.map(baseLayerPath)); 		}
	public static DirArray line(Vec3 p, Vec3 q, Color c) 					{ return new DirArray(Loaders.line(p, q, c)); 			}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {
					
					if (!isPopulated(i, j, k)) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime)
						return new HitVoxel(ray, h[0], i, j, k);
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
					
					if (isPopulated(i, j, k)) continue;
					
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
