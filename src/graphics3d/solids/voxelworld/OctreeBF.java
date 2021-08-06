package graphics3d.solids.voxelworld;

import graphics3d.*;
import graphics3d.solids.Box;

import java.io.IOException;
import java.util.*;

public class OctreeBF extends Base {

	
	/********************************************************************
	 * 																	*
	 * ID : 07															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/
	
	
	protected OctreeBF(boolean[][][] arr0) 						{ super(Octree.fromModel(arr0).data()); 		}
	protected OctreeBF(boolean[][][] arr0, Color[][][] arr1) 	{ super(Octree.fromModel(arr0).data(), arr1); 	}
	protected OctreeBF(ModelData3 data) 						{ super(data); 									}
	protected OctreeBF(ModelData4 data) 						{ super(data); 									}
	
	
	public static OctreeBF model(boolean[][][] arr0)						{ return new OctreeBF(arr0); 							}
	public static OctreeBF model(boolean[][][] arr0, Color[][][] arr1)		{ return new OctreeBF(arr0, arr1); 						}
	public static OctreeBF set(String baseLayerPath) throws IOException 	{ return new OctreeBF(Loaders.set(baseLayerPath)); 		}


	public static OctreeBF map(String path) throws IOException {
		ModelData3 modelData = Loaders.map(path);
		return new OctreeBF(modelData.model(), modelData.diffuse());
	}


	public static OctreeBF line(Vec3 p, Vec3 q, Color c) {
		ModelData3 modelData = Loaders.line(p, q, c);
		return new OctreeBF(modelData.model(), modelData.diffuse());
	}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		boolean[][][][] b = model(); 					// convenience

		List<Vec3> l0 = new ArrayList<>();				// contains hit from previous iteration
		List<Vec3> l1 = new ArrayList<>();				// hits in current iteration

		l0.add(Vec3.ZERO);								// root of every octree is (0, 0, 0)

		for (int l = b.length - 1; l > 0; l--) {		// uses current and subsequent tree level, hence > 0

			int unit = 1 << (l - 1);					// unit size for current tree level, same as Math.pow(2, l - 1)
			Vec3 unitVec3 = Vec3.EXYZ.mul(unit);

			for (Vec3 v : l0) {							// check all boxes hit in the previous iteration
				for (int i = 0; i < 8; i++) {			// check all eight subdivisions of current box for hits

					Vec3 idx = getVec3FromIndex(i);		// get position of current box relative to parent box
					Vec3 pos = v.mul(2);				// base position of box hit in current level
					Vec3 currPos = pos.add(idx);		// position of current box in current level

					Vec3 p = currPos.mul(unit);			// position of current box in world space

					Hit[] hits = Box.$.pd(p, unitVec3).hits(ray);
														// current box of unitVec3 size - ray intersection
					if (isPopulated(l - 1, currPos) && hits.length > 0 && hits[0].t() > afterTime)
						l1.add(currPos);				// if box is hit and populated it is carried over
				}
			}

			l0.clear();	l0.addAll(l1);					// clear "carry over" list and populate with hits
			l1.clear();									// clear hits in current iteration
		}

		/*
		 * Now l0 is populated with actual voxels hit with the current ray,
		 * iterate through voxels and return first hit. First approach tak-
		 * en was to find all hits, sort them and return the first one, al-
		 * so to, as a secondary check, see if hits[1].t() > afterTime - v-
		 * isally all approaches yield same results so I opted for returni-
		 * ng the first one as it is the fastest one. UPDATE: returning the
		 * first one was wrong, but produced a cool bug.
		 */

		List<Vec3Hit> vec3HitList = new ArrayList<>();

		for (Vec3 v : l0) {

			Hit hit = Box.$.pd(v, Vec3.EXYZ).firstHit(ray, afterTime);
														// current voxel of (1, 1, 1) size - ray intersection
			if (hit != Hit.POSITIVE_INFINITY && hit.t() > afterTime)
				vec3HitList.add(new Vec3Hit(v, hit));
		}

		if (!vec3HitList.isEmpty()) {
			vec3HitList.sort(Comparator.comparingDouble(Vec3Hit::t));
			return new HitVoxel(ray, vec3HitList.get(0).h(), vec3HitList.get(0).v());
		}

		return Hit.POSITIVE_INFINITY;
	}

	
	private Vec3 getVec3FromIndex(int idx) {

		int f = idx & 1;
		int g = (idx >> 1) & 1;
		int h = (idx >> 2) & 1;

		return Vec3.xyz(f, g, h);
	}


	@Override
	public Hit[] hits(Ray ray) {
		return NO_HITS;
	}
}
