package graphics3d.solids.voxelworld;

import java.util.ArrayList;
import java.util.List;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import javafx.scene.paint.Color;

public class BruteForceList extends Base {
	

	/********************************************************************
	 * 																	*
	 * ID : 01															*
	 * 																	*
	 * Description: uses ArrayList to store hits, discards hits between	*
	 * adjecent voxels, sorts the list by comparing hit times; the list	*
	 * is converted to array and put out as usual.						*
	 * 																	*
	 *******************************************************************/

	
	protected BruteForceList(Color[][][] v) {
		super(v);
	}
	
	
	public static BruteForceList v(Color[][][] v) 				{ return new BruteForceList(v); 								}
	public static BruteForceList set(String baseLayerPath) 		{ return new BruteForceList(Loaders.set(baseLayerPath)); 	}
	public static BruteForceList line(Vec3 p, Vec3 q, Color c) 	{ return new BruteForceList(Loaders.line(p, q, c)); 		}
	
	
	@Override
	public Hit[] hits(Ray ray) {
				
		List<Hit> hits = new ArrayList<Hit>();
		
		for (int i = 0; i < lenX(); i++) {
			for (int j = 0; j < lenY(); j++) {
				for (int k = 0; k < lenZ(); k++) {
					
					if (v[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (!hits.isEmpty()) {
						if (Math.abs(hits.get(hits.size() - 1).t() - h[0].t()) > 1e-8)
							hits.add(h[0]);
						else
							hits.remove(hits.size() - 1);
					} else {
						hits.add(h[0]);
					}
					
					hits.add(h[1]);
				}
			}
		}
		
		if (hits.size() == 0) return Solid.NO_HITS;
		
		hits.sort((h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		Hit[] out = new Hit[hits.size()];
		for (int i = 0; i < out.length; i++) out[i] = hits.get(i);
		
		return out;
	}
}
