package graphics3d.solids.voxelworld;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OctreeRec extends Base {


	/********************************************************************
	 * 																	*
	 * ID : 08															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/
	
	
	protected OctreeRec(boolean[][][] arr0) 					{ super(Octree.fromModel(arr0).data()); 		}
	protected OctreeRec(boolean[][][] arr0, Color[][][] arr1) 	{ super(Octree.fromModel(arr0).data(), arr1); 	}
	protected OctreeRec(ModelData data) 						{ super(data); 									}
	
	
	public static OctreeRec model(boolean[][][] arr0)						{ return new OctreeRec(arr0); 							}
	public static OctreeRec model(boolean[][][] arr0, Color[][][] arr1)		{ return new OctreeRec(arr0, arr1); 					}
	public static OctreeRec set(String baseLayerPath) throws IOException 	{ return new OctreeRec(Loaders.set(baseLayerPath)); 	}
	public static OctreeRec line(Vec3 p, Vec3 q, Color c) 					{ return new OctreeRec(Loaders.line(p, q, c)); 		}


	@Override
	public Hit firstHit(Ray ray, double afterTime) {

		List<Vec3Hit> container = new ArrayList<>();

		octreeDFS(container, Vec3.ZERO, model().length - 1, ray, afterTime);

		if (container.isEmpty())
			return Hit.POSITIVE_INFINITY;

		if (container.get(0) != Hit.POSITIVE_INFINITY && container.get(0).t() > afterTime)
			return new HitVoxel(ray, container.get(0).h(), container.get(0).v());

		return Hit.POSITIVE_INFINITY;
	}


	private void octreeDFS(List<Vec3Hit> container, Vec3 curr, int lvl, Ray ray, double afterTime) {

		int unit = 1 << (lvl - 1);						// unit size for current tree level, same as Math.pow(2, l - 1)
		Vec3 unitVec3 = Vec3.EXYZ.mul(unit);

		int xs, xe, xd, ys, ye, yd, zs, ze, zd;			// loop data for XYZ axes

		if (ray.d().x() >= 0) {
			xs =  0; xe =  2; xd = +1;
		} else {
			xs =  1; xe = -1; xd = -1;
		}

		if (ray.d().y() >= 0) {
			ys =  0; ye =  2; yd = +1;
		} else {
			ys =  1; ye = -1; yd = -1;
		}

		if (ray.d().z() >= 0) {
			zs =  0; ze =  2; zd = +1;
		} else {
			zs =  1; ze = -1; zd = -1;
		}

		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {

					Vec3 idx = Vec3.xyz(i, j, k);		// get position of current box relative to parent box
					Vec3 pos = curr.mul(2);				// base position of box hit in current level
					Vec3 currPos = pos.add(idx);		// position of current box in current level

					Vec3 p = currPos.mul(unit);			// position of current box in world space

					if (isPopulated(lvl - 1, currPos)) {
														// current box of unitVec3 size - ray intersection
						Hit hit = Box.$.pd(p, unitVec3).firstHit(ray, afterTime);

						if (hit != Hit.POSITIVE_INFINITY)
							if (lvl > 1)				// if there are more layers, go deeper
								octreeDFS(container, currPos, lvl - 1, ray, afterTime);
							else						// if curr layer is penultimate, we have a hit
								container.add(new Vec3Hit(currPos, hit));
					}
				}
			}
		}
	}


	@Override
	public Hit[] hits(Ray ray) {
		return NO_HITS;
	}
}
