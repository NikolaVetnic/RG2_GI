package graphics3d.solids.voxelworld.m;

import java.io.IOException;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Vec3;
import graphics3d.solids.Box;
import graphics3d.solids.voxelworld.a.BaseM;
import graphics3d.solids.voxelworld.d.*;
import graphics3d.solids.voxelworld.u.Loaders;

public class OctreeRecO extends BaseM {


	/********************************************************************
	 * 																	*
	 * ID : 09															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/


	protected OctreeRecO(boolean[][][] arr0) 					{ super(Octree.fromModel(arr0).data()); 		}
	protected OctreeRecO(boolean[][][] arr0, Color[][][] arr1) 	{ super(Octree.fromModel(arr0).data(), arr1); 	}
	protected OctreeRecO(ModelData3 data) 						{ super(data); 									}
	protected OctreeRecO(ModelData4 data) 						{ super(data); 									}
	
	
	public static OctreeRecO model(boolean[][][] arr0)						{ return new OctreeRecO(arr0); 							}
	public static OctreeRecO model(boolean[][][] arr0, Color[][][] arr1)	{ return new OctreeRecO(arr0, arr1); 					}


	public static OctreeRecO map(String path) throws IOException {
		ModelData3 modelData = Loaders.map(path);
		return new OctreeRecO(modelData.model(), modelData.diffuse());
	}


	public static OctreeRecO set(String baseLayerPath) throws IOException {
		ModelData3 modelData = Loaders.set(baseLayerPath);
		return new OctreeRecO(modelData.model(), modelData.diffuse());
	}


	public static OctreeRecO line(Vec3 p, Vec3 q, Color c) {
		ModelData3 modelData = Loaders.line(p, q, c);
		return new OctreeRecO(modelData.model(), modelData.diffuse());
	}


	private Vec3Hit octreeDFS(Vec3 curr, int lvl, Ray ray, double afterTime) {

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

						if (hit != Hit.POSITIVE_INFINITY) {
							if (lvl > 1) {				// if there are more layers, go deeper and return if hit
								Vec3Hit pp = octreeDFS(currPos, lvl - 1, ray, afterTime);
								if (pp.h() != Hit.POSITIVE_INFINITY) return pp;
							} else {					// if this is the last layer there is a hit
								return new Vec3Hit(currPos, hit);
							}
						}
					}
				}
			}
		}
		
		return new Vec3Hit(Vec3.ZERO, Hit.POSITIVE_INFINITY);
	}


	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Vec3Hit vh = octreeDFS(Vec3.ZERO, model().length - 1, ray, afterTime);

		return new HitVoxel(ray, vh.h(), vh.v());
	}


	@Override
	public Hit[] hits(Ray ray) {
		return NO_HITS;
	}
}
