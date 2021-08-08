package graphics3d.solids.voxelworld.m;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.voxelworld.d.HitVoxel;
import graphics3d.solids.voxelworld.u.Loaders;
import graphics3d.solids.voxelworld.d.ModelData3;
import graphics3d.solids.voxelworld.d.ModelData4;
import graphics3d.solids.voxelworld.a.BaseM;

public class BruteForce extends BaseM {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 01															*
	 * 																	*
	 * Description: uses array to store hits, discards hits between	ad- *
	 * jecent voxels, sorts the array by comparing hit times; the array	*
	 * is trimmed and put out as usual.									*
	 * 																	*
	 *******************************************************************/

	
	protected BruteForce(boolean[][][] arr0) 					{ super(arr0); 			}
	protected BruteForce(boolean[][][] arr0, Color[][][] arr1) 	{ super(arr0, arr1); 	}
	protected BruteForce(ModelData3 data) 						{ super(data); 			}
	protected BruteForce(ModelData4 data) 						{ super(data); 			}
	
	
	public static BruteForce model(boolean[][][] arr0)						{ return new BruteForce(arr0); 							}
	public static BruteForce model(boolean[][][] arr0, Color[][][] arr1)	{ return new BruteForce(arr0, arr1); 					}
	public static BruteForce map(String path) throws IOException 			{ return new BruteForce(Loaders.map(path)); 			}
	public static BruteForce set(String baseLayerPath) throws IOException 	{ return new BruteForce(Loaders.set(baseLayerPath)); 	}
	public static BruteForce line(Vec3 p, Vec3 q, Color c) 					{ return new BruteForce(Loaders.line(p, q, c)); 		}
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit out = Hit.POSITIVE_INFINITY;
		Vec3 v0 = Vec3.ZERO;
		
		for (int i = 0; i < lenX(); i++) {
			for (int j = 0; j < lenY(); j++) {
				for (int k = 0; k < lenZ(); k++) {
					
					if (!isPopulated(i, j, k)) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime) {
						if (h[0].t() < out.t()) {
							out = h[0];
							v0 = Vec3.xyz(i, j, k);
						}
					} else if (h[1].t() > afterTime){
						if (h[1].t() < out.t()) {
							out = h[1];
							v0 = Vec3.xyz(i, j, k);
						}
					}	
				}
			}
		}
		
		return new HitVoxel(ray, out, v0);
	}

	
	@Override
	public Hit[] hits(Ray ray) {
		
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
		for (int i = 0; i < lenX(); i++) {
			for (int j = 0; j < lenY(); j++) {
				for (int k = 0; k < lenZ(); k++) {
					
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
		
		Arrays.sort(hits, 0, num, (h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		return Arrays.copyOf(hits, num);
	}
}
