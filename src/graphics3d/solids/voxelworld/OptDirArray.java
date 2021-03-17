package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class OptDirArray extends Base {
	
	
	/********************************************************************
	 * 																	*
	 * ID : 03															*
	 * 																	*
	 * Description: uses array to store hits, the direction in which t-	*
	 * he voxel array is iterrated is based on ray direction and starts *
	 * from the point of entry into the baounding box, discards hits b- *
	 * etween adjecent voxels; the array is trimmed and put out as usu- *
	 * al; firstHit implemented.										*
	 * 																	*
	 *******************************************************************/

	
	protected OptDirArray(Color[][][] model) {
		super(model);
	}
	
	
	public static OptDirArray arr(Color[][][] arr)			{ return new OptDirArray(arr); 							}
	public static OptDirArray set(String baseLayerPath) throws IOException 	
															{ return new OptDirArray(Loaders.set(baseLayerPath)); 	}
	public static OptDirArray line(Vec3 p, Vec3 q, Color c) { return new OptDirArray(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		// change collider in RaytracingBase.java
		
		Hit[] boundingBoxHits = getHits(Vec3.ZERO, Vec3.xyz(lenX(), lenY(), lenZ()), ray);
		
		if (boundingBoxHits.length == 0) return Hit.POSITIVE_INFINITY;
		
		Vec3[] loopData = getLoopData(ray, boundingBoxHits);
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {

					if (model[i][j][k] == null) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (h[0].t() > afterTime) return h[0];
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
		
		int xs = loopData[0].xInt(),	xe = loopData[1].xInt(),	xd = loopData[2].xInt(),
			ys = loopData[0].yInt(),	ye = loopData[1].yInt(),	yd = loopData[2].yInt(),
			zs = loopData[0].zInt(),	ze = loopData[1].zInt(),	zd = loopData[2].zInt();
		
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
		
		int xs = -1, xe = -1,
			ys = -1, ye = -1,
			zs = -1, ze = -1;
		
		int xd = ray.d().x() >= 0 ?  +1 : -1,
			yd = ray.d().y() >= 0 ?  +1 : -1,
			zd = ray.d().z() >= 0 ?  +1 : -1;
			
		xs = x0;	xe = x1 + xd;
		ys = y0;	ye = y1 + yd;
		zs = z0;	ze = z1 + zd;
		
		return new Vec3[] { 
	    		Vec3.xyz(xs, ys, zs),
	    		Vec3.xyz(xe, ye, ze),
	    		Vec3.xyz(xd, yd, zd),
	        };
	}
}
