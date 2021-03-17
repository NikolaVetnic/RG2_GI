package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class BruteForce extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 01															*
	 * 																	*
	 * Description: uses array to store hits, discards hits between	ad- *
	 * jecent voxels, sorts the array by comparing hit times; the array	*
	 * is trimmed and put out as usual.									*
	 * 																	*
	 *******************************************************************/

	
	protected BruteForce(Color[][][] model) {
		super(model);
	}
	
	
	public static BruteForce arr(Color[][][] arr)			{ return new BruteForce(arr); 							}
	public static BruteForce set(String baseLayerPath) throws IOException 		
															{ return new BruteForce(Loaders.set(baseLayerPath)); 	}
	public static BruteForce line(Vec3 p, Vec3 q, Color c) 	{ return new BruteForce(Loaders.line(p, q, c)); 		}
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit out = Hit.POSITIVE_INFINITY;
		
		for (int i = 0; i < lenX(); i++) {
			for (int j = 0; j < lenY(); j++) {
				for (int k = 0; k < lenZ(); k++) {
					
					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime) {
						if (h[0].t() < out.t()) {
							out = h[0];
						}
					} else if (h[1].t() > afterTime){
						if (h[1].t() < out.t()) {
							out = h[1];
						}
					}	
				}
			}
		}
		
		return out;
	}

	
	@Override
	public Hit[] hits(Ray ray) {
		
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
		for (int i = 0; i < lenX(); i++) {
			for (int j = 0; j < lenY(); j++) {
				for (int k = 0; k < lenZ(); k++) {
					
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
		
		Arrays.sort(hits, 0, num, (h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		return Arrays.copyOf(hits, num);
	}
}
