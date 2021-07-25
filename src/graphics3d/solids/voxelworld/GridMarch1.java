package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class GridMarch1 extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 04															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/
	
	
	protected GridMarch1(boolean[][][] arr0) 					{ super(arr0); 			}
	protected GridMarch1(boolean[][][] arr0, Color[][][] arr1) 	{ super(arr0, arr1); 	}
	protected GridMarch1(ModelData data) 						{ super(data); 			}
	
	
	public static GridMarch1 model(boolean[][][] arr0)						{ return new GridMarch1(arr0); 							}
	public static GridMarch1 model(boolean[][][] arr0, Color[][][] arr1)	{ return new GridMarch1(arr0, arr1); 					}
	public static GridMarch1 set(String baseLayerPath) throws IOException 	{ return new GridMarch1(Loaders.set(baseLayerPath)); 	}
	public static GridMarch1 line(Vec3 p, Vec3 q, Color c) 					{ return new GridMarch1(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
		/*
		 * Render only the voxel faces that lie on the bounding box:
		 * 
		 * if (cell(loopData[0]) != null && boundingBoxHits[0].t() > afterTime) 
		 *     return new HitVoxel(ray, boundingBoxHits[0], xs, ys, zs);
		 *     
		 * if (cell(loopData[1].sub(loopData[2])) != null && boundingBoxHits[1].t() > afterTime) 
		 *     return new HitVoxel(ray, boundingBoxHits[1], xe - xd, ye - yd, ze - zd);
		 */
		
		int[] xi = {  0,  0, xd };
		int[] yi = {  0, yd,  0 };
		int[] zi = { zd,  0,  0 };

		while (	xs != -1 && xs != xe &&
				ys != -1 && ys != ye &&
				zs != -1 && zs != ze ) {
			
			Vec3 p = Vec3.xyz(xs, ys, zs);
			Hit[] hits = getHits(p, ray);
			
			if (isPopulated(p) && hits[0].t() > afterTime)				// if rays hits the voxel AND there is a voxel 
				return new HitVoxel(ray, hits[0], xs, ys, zs);	// there AND it's after afterTime...
			
			Hit[][] neighbours = new Hit[3][2];					// ray can intersect no more than three voxels
			
			int idx = -1;
			double minTime = Double.MAX_VALUE;
			
			for (int i = 0; i < neighbours.length; i++) {
				
				neighbours[i] = getHits(p.add(Vec3.xyz(xi[i], yi[i], zi[i])), ray);
				
				if (neighbours[i].length == 0) {
					continue;
				} else if (minTime > neighbours[i][0].t()) {
					idx = i;
					minTime = neighbours[i][0].t();
				}
			}
			
			xs += xi[idx];
			ys += yi[idx];
			zs += zi[idx];
		}
		
		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Solid.NO_HITS;
		
		Vec3[] loopData = Util.getLoopData(Vec3.xyz(lenX(), lenY(), lenZ()), ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
		HitVoxel[] hits = new HitVoxel[lenX() * lenY() * lenZ()];
		int num = 0;
		
		int[] xi = {  0,  0, xd };
		int[] yi = {  0, yd,  0 };
		int[] zi = { zd,  0,  0 };
		
		while (	xs != -1 && xs != xe &&
				ys != -1 && ys != ye &&
				zs != -1 && zs != ze ) {
			
			Vec3 p = Vec3.xyz(xs, ys, zs);
			Hit[] h = getHits(p, ray);
			
			if (isPopulated(p)) {
				if (num > 0) {
					if (Math.abs(hits[num - 1].t() - h[0].t()) > 1e-8)
						hits[num++] = new HitVoxel(ray, h[0], xs, ys, zs);
					else
						num--;
				} else {
					hits[num++] = new HitVoxel(ray, h[0], xs, ys, zs);
				}

				hits[num++] = new HitVoxel(ray, h[1], xs, ys, zs);
			}
				
			Hit[][] neighbours = new Hit[3][2];
			
			int idx = -1;
			double minTime = Double.MAX_VALUE;
			
			for (int i = 0; i < neighbours.length; i++) {
				
				neighbours[i] = getHits(p.add(Vec3.xyz(xi[i], yi[i], zi[i])), ray);
				
				if (neighbours[i].length == 0) {
					continue;
				} else if (minTime > neighbours[i][0].t()) {
					idx = i;
					minTime = neighbours[i][0].t();
				}
			}
			
			xs += xi[idx];
			ys += yi[idx];
			zs += zi[idx];
		}
		
		if (num == 0) return Solid.NO_HITS;
		
		return Arrays.copyOf(hits, num);
	}
}
