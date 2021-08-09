package graphics3d.solids.voxelworld.f;

import graphics3d.*;
import graphics3d.solids.voxelworld.d.HitVoxel;
import graphics3d.solids.voxelworld.d.VoxelData;
import graphics3d.solids.voxelworld.a.BasePF;
import graphics3d.solids.voxelworld.a.BaseF;

import java.util.function.Function;
import java.util.function.Predicate;

public class FGridMarch2O extends BaseF {


	/********************************************************************
	 * 																	*
	 * ID : 06f															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/


	protected FGridMarch2O(Vec3 d, Function<Vec3, VoxelData> f) { super(d, f); }


	public static FGridMarch2O d(Vec3 d, Function<Vec3, VoxelData> f) 	{
		return new FGridMarch2O(d, f);
	}


	public static FGridMarch2O xyz(int x, int y, int z, Function<Vec3, VoxelData> f) {
		return new FGridMarch2O(Vec3.xyz(x, y, z), f);
	}

	
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
