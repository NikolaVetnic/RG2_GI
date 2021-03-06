package graphics3d.solids.voxelworld;

import java.util.Arrays;

import graphics3d.Hit;
import graphics3d.HitData;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import javafx.scene.paint.Color;
import mars.geometry.Vector;
import mars.utils.Numeric;

public class OptDirArrayM extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 05															*
	 * 																	*
	 * Description: adds per-voxel material support to v04.				*
	 * 																	*
	 *******************************************************************/

	
	protected OptDirArrayM(graphics3d.Color[][][] model) {
		super(model);
	}
	
	
	public static OptDirArrayM arr(graphics3d.Color[][][] arr)			{ return new OptDirArrayM(arr); 						}
	public static OptDirArrayM set(String baseLayerPath) 				{ return new OptDirArrayM(Loaders.set(baseLayerPath)); 	}
	public static OptDirArrayM line(Vec3 p, Vec3 q, graphics3d.Color c) { return new OptDirArrayM(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		// change collider in RaytracingBase.java
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = getLoopData(ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {

					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime) return new HitVoxel(ray, h[0], i, j, k);
				}
			}
		}
		
		return Hit.POSITIVE_INFINITY;
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Solid.NO_HITS;
		
		Vec3[] loopData = getLoopData(ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {

					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (num > 0) {
						if (Math.abs(hits[num - 1].t() - h[0].t()) > 1e-8)
							hits[num++] = h[0];
						else
							num--;
					} else {
						hits[num++] = h[0];
					}

					hits[num++] = h[1];
				}
			}
		}
		
		if (num == 0) return Solid.NO_HITS;
		
		return Arrays.copyOf(hits, num);
	}
	
	
	private Vec3[] getLoopData(Ray ray, Hit[] boundingBoxHits) {
		
		Vec3 v0 = ray.at(boundingBoxHits[0].t()).floor();
		int x0 = v0.xInt(),
			y0 = v0.yInt(),
			z0 = v0.zInt();
		
		Vec3 v1 = ray.at(boundingBoxHits[1].t()).floor();
		int x1 = v1.xInt(),
			y1 = v1.yInt(),
			z1 = v1.zInt();
		
		if (x0 == lenX()) x0--;	if (y0 == lenY()) y0--;	if (z0 == lenZ()) z0--;
		if (x0 == -1) 	  x0++;	if (y0 == -1) 	  y0++;	if (z0 == -1) 	  z0++;

		if (x1 == lenX()) x1--;	if (y1 == lenY()) y1--;	if (z1 == lenZ()) z1--;
		if (x1 == -1) 	  x1++;	if (y1 == -1) 	  y1++;	if (z1 == -1) 	  z1++;
		
		int xs = -1, xe = -1, xd =  0,
			ys = -1, ye = -1, yd =  0,
			zs = -1, ze = -1, zd =  0;
		
		int dx = ray.d().x() >= 0 ?  +1 : -1,
			dy = ray.d().y() >= 0 ?  +1 : -1,
			dz = ray.d().z() >= 0 ?  +1 : -1;
			
		xs = x0;	xe = x1 + dx;	xd = dx;
		ys = y0;	ye = y1 + dy;	yd = dy;
		zs = z0;	ze = z1 + dz;	zd = dz;
        
        return new Vec3[] { 
    		Vec3.xyz(xs, xe, xd),
    		Vec3.xyz(ys, ye, yd),
    		Vec3.xyz(zs, ze, zd),
        };
	}
	
	
	class HitVoxel extends Hit.HitRayT {
		
		private Vec3 n_;
		private int p, q, r;
		
		protected HitVoxel(Ray ray, double t) {
			super(ray, t);
		}
		
		protected HitVoxel(Ray ray, Hit h, int i, int j, int k) {
			this(ray, h.t());
			this.n_ = h.n_();
			
			this.p = i;
			this.q = j;
			this.r = k;
		}
		
		@Override
		public Vec3 n_() {
			return n_;
		}
		
		@Override
		public Vector uv() {
			return Vector.xy(p, (q << 4) + r);
		}
	}
}
