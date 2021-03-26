package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.HalfSpace;
import mars.utils.Numeric;

public class GridMarch3 extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 05															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/

	
	protected GridMarch3(Color[][][] model) {
		super(model);
	}
	
	
	public static GridMarch3 arr(Color[][][] arr)			{ return new GridMarch3(arr); 						}
	public static GridMarch3 set(String baseLayerPath) throws IOException 		
															{ return new GridMarch3(Loaders.set(baseLayerPath)); }
	public static GridMarch3 line(Vec3 p, Vec3 q, Color c) 	{ return new GridMarch3(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit[] boundingBoxHits = getBoundingBoxHits(ray);
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = Util.getLoopData(len(), ray, boundingBoxHits);
		
		Vec3 v0 = loopData[0];
		Vec3 v1 = loopData[1];
		
		Vec3 s0 = ray.d().signum();
		Vec3 s1 = s0.inverse();
		Vec3 s2 = s1.add(Vec3.EXYZ).mul(0.5).inverse();
		
		Vec3 t = Vec3.xyz(
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EX)).hits(ray)[0].t(), 
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EY)).hits(ray)[0].t(), 
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EZ)).hits(ray)[0].t());

		Vec3 dt = Vec3.EXYZ.div(ray.d());
		
		while (v0.inBoundingBox(v1)) {
			
			// old approach
			if (cell(v0) != null) {				
				
				Hit[] hits = getHits(v0, ray);
				
				if (cell(v0) != null && hits[0].t() > afterTime)
					return new HitVoxel(ray, hits[0], v0);
			}
			
			// idea taken from Box.hits() method
//			if (cell(v0) != null) {
//				
//				Vec3 u = v0.add(Vec3.EXYZ).sub(ray.p()).div(ray.d());	// exit time from current voxel
//				Vec3 v = Vec3.min(t, u);
//				
//				if (v.max() > afterTime)
//					return new HitVoxel(ray, HitData.tn(v.max(), Vec3.E[v.maxIndex()].mul(s1)), v0);
//			}

			Vec3 tNext = t.add(dt);
			int k = tNext.minIndex();
			v0 = v0.add(s0.mul(Vec3.E[k]));
			t = t.add(dt.mul(s0.mul(Vec3.E[k])));
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
			
			if (cell(p) != null) {
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
