package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class GridMarch2O extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 06															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/

	
	protected GridMarch2O(boolean[][][] arr0) 					{ super(arr0); 			}
	protected GridMarch2O(boolean[][][] arr0, Color[][][] arr1) { super(arr0, arr1); 	}
	protected GridMarch2O(ModelData data) 						{ super(data); 			}
	
	
	public static GridMarch2O model(boolean[][][] arr0)						{ return new GridMarch2O(arr0); 						}
	public static GridMarch2O model(boolean[][][] arr0, Color[][][] arr1)	{ return new GridMarch2O(arr0, arr1); 					}
	public static GridMarch2O set(String baseLayerPath) throws IOException 	{ return new GridMarch2O(Loaders.set(baseLayerPath)); 	}
	public static GridMarch2O map(String baseLayerPath) throws IOException 	{ return new GridMarch2O(Loaders.map(baseLayerPath)); 	}
	public static GridMarch2O line(Vec3 p, Vec3 q, Color c) 				{ return new GridMarch2O(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit[] boundingBoxHits = getBoundingBoxHits(ray);					// check if ray hits the bounding box
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3 v0 = ray.at(boundingBoxHits[0].t()).floor();					// starting voxel of grid march
		Vec3 u0 = Vec3.xyz(													// checking to see if ray is floored to a voxel outside of model (somehow it can happen)
				v0.xInt() == lenX() ? -1 : (v0.xInt() == -1 ? 1 : 0), 
				v0.yInt() == lenY() ? -1 : (v0.yInt() == -1 ? 1 : 0), 
				v0.zInt() == lenZ() ? -1 : (v0.zInt() == -1 ? 1 : 0));
			 v0 = v0.add(u0);												// basically clamping to model volume
		
		Vec3 s0 = ray.d().signum();											// get direction of grid march
		Vec3 s1 = s0.inverse();
		Vec3 s2 = s1.add(Vec3.EXYZ).mul(0.5);
		
		Vec3 t 	= v0.add(s2).sub(ray.p()).div(ray.d());						// get time of hit
		Vec3 dt = s0.div(ray.d());											// time to pass the distance of direction of grid march
		
		Vec3[] s0A = new Vec3[] { s0.mul(Vec3.E[0]), s0.mul(Vec3.E[1]), s0.mul(Vec3.E[2]) };	// array of next voxels: next is horizontal / vertical / depth-wise
		Vec3[] dtA = new Vec3[] { dt.mul(Vec3.E[0]), dt.mul(Vec3.E[1]), dt.mul(Vec3.E[2]) }; 
		
		while (true) {

			int k = t.maxIndex();
			double tMax = t.get(k);
			
			if (isPopulated(v0) && tMax > afterTime)
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
