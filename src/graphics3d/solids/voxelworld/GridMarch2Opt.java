package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class GridMarch2Opt extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 0x															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/

	
	protected GridMarch2Opt(Color[][][] model) {
		super(model);
	}
	
	
	public static GridMarch2Opt arr(Color[][][] arr)			{ return new GridMarch2Opt(arr); 						}
	public static GridMarch2Opt set(String baseLayerPath) throws IOException 		
																{ return new GridMarch2Opt(Loaders.set(baseLayerPath)); }
	public static GridMarch2Opt map(String path) throws IOException
																{ return new GridMarch2Opt(Loaders.map(path)); 			}
	public static GridMarch2Opt line(Vec3 p, Vec3 q, Color c) 	{ return new GridMarch2Opt(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit[] boundingBoxHits = getBoundingBoxHits(ray);
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3 v0 = ray.at(boundingBoxHits[0].t()).floor();
		Vec3 u0 = Vec3.xyz(
				v0.xInt() == lenX() ? -1 : (v0.xInt() == -1 ? 1 : 0), 
				v0.yInt() == lenY() ? -1 : (v0.yInt() == -1 ? 1 : 0), 
				v0.zInt() == lenZ() ? -1 : (v0.zInt() == -1 ? 1 : 0));
			 v0 = v0.add(u0);
		
		Vec3 s0 = ray.d().signum();
		Vec3 s1 = s0.inverse();
		Vec3 s2 = s1.add(Vec3.EXYZ).mul(0.5);
		
		Vec3 t 	= v0.add(s2).sub(ray.p()).div(ray.d());
		Vec3 dt = s0.div(ray.d());
		
		Vec3[] s0A = new Vec3[] { s0.mul(Vec3.E[0]), s0.mul(Vec3.E[1]), s0.mul(Vec3.E[2]) };
		Vec3[] dtA = new Vec3[] { dt.mul(Vec3.E[0]), dt.mul(Vec3.E[1]), dt.mul(Vec3.E[2]) }; 
		
		while (true) {

			int k = t.maxIndex();
			double tMax = t.get(k);
			
			if (cell(v0) != null && tMax > afterTime)
				return new HitVoxel(ray, HitData.tn(tMax, s1.mul(Vec3.E[k])), v0);

			Vec3 tNext = t.add(dt);
			k = tNext.minIndex();
			v0 = v0.add(s0A[k]);
			
			if (v0.get(k) == -1 || v0.get(k) == lenA()[k]) break;
			
			t = t.add(dtA[k]);
		}
		
		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		
		return Solid.NO_HITS;
	}
}
