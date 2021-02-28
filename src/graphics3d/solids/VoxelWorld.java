package graphics3d.solids;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class VoxelWorld implements Solid {
	
	
	private Color[][][] v;
	
	
	private VoxelWorld(Color[][][] v) {
		this.v = v;
	}
	
	
	public static VoxelWorld v(Color[][][] v) {
		return new VoxelWorld(v);
	}
	
	
	public static VoxelWorld set(String baseLayerPath) {
		
		String setPath = getSetPath(baseLayerPath);
		String setName = getSetName(baseLayerPath);
		String imgExtn = getImgExtn(baseLayerPath);
		
		Image image = null;
		
		try {
			image = new Image(new FileInputStream(baseLayerPath));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
		int x = (int) image.getWidth();
		int y = (int) image.getHeight();
		int z = new File(setPath).listFiles().length;
		
		Color[][][] v = new Color[x][y][z];
		
		for (int k = 0; k < z; k++) {
			
			if (k != 0) {
				try {
					image = new Image(new FileInputStream(setPath + setName + "_" + String.format("%03d", k) + imgExtn));
				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
				}
			}
			
			PixelReader pr = image.getPixelReader();
			
			for (int j = 0; j < y; j++) {
				for (int i = 0; i < x; i++) {
					v[i][j][k] = pr.getColor(i, j);
				}
			}
		}
		
		return new VoxelWorld(v);
	}
	
	
	private static String getImgExtn(String baseLayerPath) {
		String[] tokens = baseLayerPath.split("/");
		String f = tokens[tokens.length - 1];
		
		return f.substring(f.length() - 4, f.length());
	}


	private static String getSetName(String baseLayerPath) {
		
		String[] tokens = baseLayerPath.split("/");
		String f = tokens[tokens.length - 1];
		
		return f.substring(0, f.length() - 8);
	}


	private static String getSetPath(String baseLayerPath) {
		
		String[] tokens = baseLayerPath.split("/");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length - 1; i++) sb.append(tokens[i] + "/");
		
		return sb.toString();
	}


	public static VoxelWorld bresenham(Vec3 p, Vec3 q) {
		
		int minX = (int) (p.x() < q.x() ? p.x() : q.x()); 
		int minY = (int) (p.y() < q.y() ? p.y() : q.y());
		int minZ = (int) (p.z() < q.z() ? p.z() : q.z());
		
		Vec3 tr = Vec3.xyz(minX, minY, minZ);
		
		p = p.sub(tr);
		q = q.sub(tr);
		
		int dx = (int) q.x(),
			dy = (int) q.y(),
			dz = (int) q.z();
		
		Color[][][] v = new Color[dx + 1][dy + 1][dz + 1];
		
		int xc = 0,				// starting point at origin
			yc = 0,
			zc = 0;
		
		int step = 1;
		
		if (dx >= dy && dx >= dz) {
			
			// driving axis is X-axis
			
			int p1 = 2 * dy - dx,
				p2 = 2 * dz - dx;

			while (xc != dx) {
				
				xc += step;
				
				if (p1 >= 0) {
					yc += step;
					p1 -= 2 * dx;
				}
				
				if (p2 >= 0) {
					zc += step;
					p2 -= 2 * dx;
				}
				
				p1 += 2 * dy;
				p2 += 2 * dz;
				
				v[xc][yc][zc] = Color.WHITE;
			}
		} else if (dy >= dx && dy >= dz) {
			
			// driving axis is Y-axis
			
			int p1 = 2 * dx - dy,
				p2 = 2 * dz - dy;
			
			while (yc != dy) {
				
				yc += step;
				
				if (p1 >= 0) {
					xc += step;
					p1 -= 2 * dy;
				}
				
				if (p2 >= 0) {
					zc += step;
					p2 -= 2 * dy;
				}
				
				p1 += 2 * dx;
				p2 += 2 * dz;
				
				v[xc][yc][zc] = Color.WHITE;
			}
		} else {

			// driving axis is Z-axis
			
			int p1 = 2 * dy - dz,
				p2 = 2 * dx - dz;
			
			while (zc != dz) {
				
				zc += step;
				
				if (p1 >= 0) {
					yc += step;
					p1 -= 2 * dz;
				}
				
				if (p2 >= 0) {
					xc += step;
					p2 -= 2 * dz;
				}
				
				p1 += 2 * dy;
				p2 += 2 * dx;
				
				v[xc][yc][zc] = Color.WHITE;
			}
		}
		
		return new VoxelWorld(v);
	}
	
	
	private int sizeX()			{ return v.length;			}
	private int sizeY()			{ return v[0].length;		}
	private int sizeZ()			{ return v[0][0].length;	}
	
	
	private Hit[] getHits(Vec3 p, Vec3 d, Ray ray) {
		return Box.$.pd(p, d).hits(ray);
	}
	
	
	private Hit[] getHits(Vec3 p, Ray ray) {
		return getHits(p, Vec3.EXYZ, ray);
	}
	
	
	@Override
	public Hit[] hits(Ray ray) {
//		return hitsID01(ray);
//		return hitsID02(ray);
//		return hitsID03(ray);
		return hitsID04(ray);
	}


	private Hit[] hitsID01(Ray ray) {
		
		/********************************************************************
		 * 																	*
		 * ID : 01															*
		 * 																	*
		 * Description: uses ArrayList to store hits, discards hits between	*
		 * adjecent voxels, sorts the list by comparing hit times; the list	*
		 * is converted to array and put out as usual.						*
		 * 																	*
		 *******************************************************************/
				
		List<Hit> hits = new ArrayList<Hit>();
		
		for (int i = 0; i < sizeX(); i++) {
			for (int j = 0; j < sizeY(); j++) {
				for (int k = 0; k < sizeZ(); k++) {
					
					if (v[i][j][k].getBrightness() == 0) continue;
					
					Hit[] h = getHits(Vec3.xyz(i, j, k), ray);
					
					if (h.length == 0) continue;
					
					if (!hits.isEmpty()) {
						if (Math.abs(hits.get(hits.size() - 1).t() - h[0].t()) > 1e-8)
							hits.add(h[0]);
						else
							hits.remove(hits.size() - 1);
					} else {
						hits.add(h[0]);
					}
					
					hits.add(h[1]);
				}
			}
		}
		
		if (hits.size() == 0) return Solid.NO_HITS;
		
		hits.sort((h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		Hit[] out = new Hit[hits.size()];
		for (int i = 0; i < out.length; i++) out[i] = hits.get(i);
		
		return out;
	}
	

	private Hit[] hitsID02(Ray ray) {
	
		/********************************************************************
		 * 																	*
		 * ID : 02															*
		 * 																	*
		 * Description: uses array to store hits, discards hits between	ad- *
		 * jecent voxels, sorts the array by comparing hit times; the array	*
		 * is trimmed and put out as usual.									*
		 * 																	*
		 *******************************************************************/
		
		Hit[] hits = new Hit[sizeX() * sizeY() * sizeZ()];
		int num = 0;
		
		for (int i = 0; i < sizeX(); i++) {
			for (int j = 0; j < sizeY(); j++) {
				for (int k = 0; k < sizeZ(); k++) {
					
					if (v[i][j][k].getBrightness() == 0) continue;
					
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
		
		Arrays.sort(hits, 0, num, (h1, h2) -> h1.t() - h2.t() > 0 ? 1 : h1.t() - h2.t() == 0 ? 0 : -1);
		
		return Arrays.copyOf(hits, num);
	}
	

	private Hit[] hitsID03(Ray ray) {
	
		/********************************************************************
		 * 																	*
		 * ID : 03															*
		 * 																	*
		 * Description: uses array to store hits, the direction in which t-	*
		 * he voxel array is iterrated is based on the ray direction, disc-	*
		 * ards hits between adjecent voxels; the array	is trimmed, put out	* 
		 * as usual.														*
		 * 																	*
		 *******************************************************************/
		
		Hit[] hits = new Hit[sizeX() * sizeY() * sizeZ()];
		int num = 0;
		
		int dx = ray.d().x() >= 0 ?  +1 : -1,
			dy = ray.d().y() >= 0 ?  +1 : -1,
			dz = ray.d().z() >= 0 ?  +1 : -1;
		
		int 		   xs = -1, 		 xe = -1, 	   	   xd =  0;		
		if (dx == 1) { xs =  0; 		 xe = sizeX()	 ; xd = +1; }
		else		 { xs = sizeX() - 1; xe = -1; 	   	   xd = -1; }

		int 		   ys = -1, 		 ye = -1, 	   	   yd =  0;		
		if (dy == 1) { ys =  0; 		 ye = sizeY()	 ; yd = +1; }
		else		 { ys = sizeY() - 1; ye = -1; 	   	   yd = -1; }

		int 		   zs = -1, 		 ze = -1, 	   	   zd =  0;		
		if (dz == 1) { zs =  0; 		 ze = sizeZ()    ; zd = +1; }
		else		 { zs = sizeZ() - 1; ze = -1; 	   	   zd = -1; }
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {
					
					if (v[i][j][k].getBrightness() == 0) continue;
					
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
	
	
	private Hit[] hitsID04(Ray ray) {
		
		/********************************************************************
		 * 																	*
		 * ID : 04															*
		 * 																	*
		 * Description: 													*
		 * 																	*
		 *******************************************************************/
		
		Hit[] bbHits = getHits(Vec3.ZERO, Vec3.xyz(sizeX(), sizeY(), sizeZ()), ray);
		
		if (bbHits.length == 0) return Solid.NO_HITS;
		
		Vec3 in = ray.at(bbHits[0].t());
		int inX = (int) Math.floor(in.x());
		int inY = (int) Math.floor(in.y());
		int inZ = (int) Math.floor(in.z());
		
		Vec3 out = ray.at(bbHits[1].t());
		int outX = (int) Math.floor(out.x());
		int outY = (int) Math.floor(out.y());
		int outZ = (int) Math.floor(out.z());
		
		if ( inX == sizeX())  inX--;	if ( inY == sizeY())  inY--;	if ( inZ == sizeZ())  inZ--;
		if (outX == sizeX()) outX--;	if (outY == sizeY()) outY--;	if (outZ == sizeZ()) outZ--;
		
		if ( inX == -1)  inX++;	if ( inY == -1)  inY++;	if ( inZ == -1)  inZ++;
		if (outX == -1) outX++;	if (outY == -1) outY++;	if (outZ == -1) outZ++;
		
		int xs = -1, xe = -1, xd =  0,
			ys = -1, ye = -1, yd =  0,
			zs = -1, ze = -1, zd =  0;
		
		int dx = ray.d().x() >= 0 ?  +1 : -1,
			dy = ray.d().y() >= 0 ?  +1 : -1,
			dz = ray.d().z() >= 0 ?  +1 : -1;
				
		if (dx == 1) {
			xs = inX + 1;		xe = sizeX();		xd = +1;
		} else {
			xs = outX;	xe = -1;		xd = -1;
		}
		
		if (dy == 1) {
			ys = inY + 1;		ye = sizeY();	yd = +1;
		} else {
			ys = sizeY() - 1;	ye = -1;		yd = -1;
		}
		
		if (dz == 1) {
			zs = 0;			ze = sizeZ();	zd = +1;
		} else {
			zs = sizeZ() - 1;	ze = -1;		zd = -1;
		}
		
		Hit[] hits = new Hit[sizeX() * sizeY() * sizeZ()];
		int num = 0;
		
		for (int i = xs; i != xe; i += xd) {
			for (int j = ys; j != ye; j += yd) {
				for (int k = zs; k != ze; k += zd) {
					
					if (v[i][j][k].getBrightness() == 0) continue;
					
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
}
