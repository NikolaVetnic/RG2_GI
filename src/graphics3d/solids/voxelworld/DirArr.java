package graphics3d.solids.voxelworld;

import java.io.IOException;
import java.util.Arrays;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;

public class DirArr extends Base {
	

	/********************************************************************
	 * 																	*
	 * ID : 02															*
	 * 																	*
	 * Description: uses array to store hits, the direction in which t-	*
	 * he voxel array is iterrated is based on the ray direction, disc-	*
	 * ards hits between adjecent voxels; the array	is trimmed, put out	* 
	 * as usual; firstHit implemented.									*
	 * 																	*
	 *******************************************************************/

	
	protected DirArr(Color[][][] v) {
		super(v);
	}
	
	
	public static DirArr arr(Color[][][] arr)			{ return new DirArr(arr); 							}
	public static DirArr set(String baseLayerPath) throws IOException 		
														{ return new DirArr(Loaders.set(baseLayerPath)); 	}
	public static DirArr line(Vec3 p, Vec3 q, Color c) 	{ return new DirArr(Loaders.line(p, q, c)); 		}

	
	@Override
	public Hit firstHit(Ray ray, double afterTime) {
		
		Vec3[] loopData = getLoopData(ray);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
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
				
		Hit[] hits = new Hit[lenX() * lenY() * lenZ()];
		int num = 0;
		
		Vec3[] loopData = getLoopData(ray);
		
		int xs = loopData[0].xInt(),	xe = loopData[0].yInt(),	xd = loopData[0].zInt(),
			ys = loopData[1].xInt(),	ye = loopData[1].yInt(),	yd = loopData[1].zInt(),
			zs = loopData[2].xInt(),	ze = loopData[2].yInt(),	zd = loopData[2].zInt();
		
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
	
	
	private Vec3[] getLoopData(Ray ray) {
		
		int dx = ray.d().x() >= 0 ?  +1 : -1,
				dy = ray.d().y() >= 0 ?  +1 : -1,
				dz = ray.d().z() >= 0 ?  +1 : -1;
			
		int 		   xs = -1		  , xe = -1		, xd =  0;		
		if (dx == 1) { xs =  0		  ; xe = lenX()	; xd = +1; }
		else		 { xs = lenX() - 1; xe = -1		; xd = -1; }

		int 		   ys = -1		  , ye = -1		, yd =  0;		
		if (dy == 1) { ys =  0		  ; ye = lenY()	; yd = +1; }
		else		 { ys = lenY() - 1; ye = -1		; yd = -1; }

		int 		   zs = -1		  , ze = -1		, zd =  0;		
		if (dz == 1) { zs =  0		  ; ze = lenZ() ; zd = +1; }
		else		 { zs = lenZ() - 1; ze = -1		; zd = -1; }
		
		return new Vec3[] { 
        		Vec3.xyz(xs, xe, xd),
        		Vec3.xyz(ys, ye, yd),
        		Vec3.xyz(zs, ze, zd),
        };
	}
}
