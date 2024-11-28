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

public class DirArrayO extends BaseM {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 03															*
	 * 																	*
	 * Description: uses array to store hits, the direction in which t-	*
	 * he voxel array is iterated is based on the ray direction and st- *
	 * arts from the point of entry into the bounding box, discards hi- *
	 * ts between adjecent voxels; array is trimmed and put out as usu- *
	 * al; firstHit implemented.										*
	 * 																	*
	 *******************************************************************/

	
	protected DirArrayO(boolean[][][] arr0) 					{ super(arr0); 			}
	protected DirArrayO(boolean[][][] arr0, Color[][][] arr1) 	{ super(arr0, arr1); 	}
	protected DirArrayO(ModelData3 data) 						{ super(data); 			}
	protected DirArrayO(ModelData4 data) 						{ super(data); 			}
	
	
	public static DirArrayO model(boolean[][][] arr0)						{ return new DirArrayO(arr0); 							}
	public static DirArrayO model(boolean[][][] arr0, Color[][][] arr1)		{ return new DirArrayO(arr0, arr1); 					}
	public static DirArrayO set(String baseLayerPath) throws IOException 	{ return new DirArrayO(Loaders.set(baseLayerPath)); 	}
	public static DirArrayO map(String baseLayerPath) throws IOException 	{ return new DirArrayO(Loaders.map(baseLayerPath)); 	}
	public static DirArrayO line(Vec3 p, Vec3 q, Color c) 					{ return new DirArrayO(Loaders.line(p, q, c)); 			}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		// change collider in RaytracingBase.java
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
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
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Solid.NO_HITS;
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
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
