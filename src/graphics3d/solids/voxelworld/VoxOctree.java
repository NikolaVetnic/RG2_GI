package graphics3d.solids.voxelworld;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;
import mars.geometry.Vector;

public class VoxOctree implements Solid {

	
	/********************************************************************
	 * 																	*
	 * ID : 0x															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/
	
	
	Octree octree;
	
	
	private VoxOctree(Octree octree) {
		this.octree = octree;
	}
	
	
	public static VoxOctree arr(boolean[][][] arr) {
		return new VoxOctree(Octree.fromModel(arr));
	}
	
	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		boolean[][][] bb = octree.data()[0];
		
		Vec3 len = Vec3.xyz(bb.length, bb[0].length, bb[0][0].length);
		
		Hit[] boundingBoxHits = Box.$.pd(Vec3.ZERO, len).hits(ray);
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3 v0 = Vec3.ZERO;
		double t = 0.0;
		
		for (int i = octree.data().length - 1; i >= 0; i--) {
			
			bb = octree.data()[i];
			len = Vec3.xyz(bb.length, bb[0].length, bb[0][0].length);
			
			boundingBoxHits = Box.$.pd(Vec3.ZERO, len).hits(ray);
			
				 v0 = ray.at(boundingBoxHits[0].t()).floor();					// starting voxel of grid march
			Vec3 u0 = Vec3.xyz(													// checking to see if ray is floored to a voxel outside of model (somehow it can happen)
					v0.xInt() == bb.length ? -1 : (v0.xInt() == -1 ? 1 : 0), 
					v0.yInt() == bb[0].length ? -1 : (v0.yInt() == -1 ? 1 : 0), 
					v0.zInt() == bb[0][0].length ? -1 : (v0.zInt() == -1 ? 1 : 0));
				 v0 = v0.add(u0);
				 				 
			if (!bb[v0.xInt()][v0.yInt()][v0.zInt()])
				return Hit.POSITIVE_INFINITY;
			
			t = v0.length() / ray.d().length();
		}
		
		return new HitVoxel(ray, t);
	}


	@Override
	public Hit[] hits(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}
}
