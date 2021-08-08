package graphics3d.solids.voxelworld.f;

import graphics3d.*;
import graphics3d.solids.HalfSpace;
import graphics3d.solids.voxelworld.a.BaseF;
import graphics3d.solids.voxelworld.a.BaseM;
import graphics3d.solids.voxelworld.d.HitVoxel;
import graphics3d.solids.voxelworld.d.ModelData3;
import graphics3d.solids.voxelworld.d.ModelData4;
import graphics3d.solids.voxelworld.u.Loaders;
import graphics3d.solids.voxelworld.u.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public class FGridMarch2 extends BaseF {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 05															*
	 * 																	*
	 * Description:														*
	 * 																	*
	 *******************************************************************/


	protected FGridMarch2(Vec3 d, Predicate<Vec3> p, Function<Vec3, Color> c) { super(d, p, c); }


	public static FGridMarch2 d(Vec3 d, Predicate<Vec3> p, Function<Vec3, Color> c) 	{
		return new FGridMarch2(d, p, c);
	}


	public static FGridMarch2 xyz(int x, int y, int z, Predicate<Vec3> p, Function<Vec3, Color> c) {
		return new FGridMarch2(Vec3.xyz(x, y, z), p, c);
	}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Hit[] boundingBoxHits = getBoundingBoxHits(ray);
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = Util.getLoopData(len(), ray, boundingBoxHits);
		
		Vec3 v0 = loopData[0];
		Vec3 v1 = loopData[1];
		
		Vec3 s0 = ray.d().signum();
		Vec3 s1 = s0.inverse();
		Vec3 s2 = s1.add(Vec3.EXYZ).mul(0.5);
		
		Vec3 t = Vec3.xyz(
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EX)).hits(ray)[0].t(), 
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EY)).hits(ray)[0].t(), 
				HalfSpace.pn(v0.add(s2), s1.mul(Vec3.EZ)).hits(ray)[0].t());

		Vec3 dt = s0.div(ray.d());
		
		while (v0.inBoundingBox(v1)) {
			
			if (isPopulated(v0) && t.max() > afterTime)
				return new HitVoxel(ray, HitData.tn(t.max(), s1.mul(Vec3.E[t.maxIndex()])), v0);

			Vec3 tNext = t.add(dt);
			int k = tNext.minIndex();
			v0 = v0.add(s0.mul(Vec3.E[k]));
			t = t.add(dt.mul(Vec3.E[k]));
		}
		
		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		
		// NOT YET IMPLEMENTED
		
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
