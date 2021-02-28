package graphics3d.solids.voxelworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import graphics3d.Vec3;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

public class Loaders {

	
	public static Color[][][] set(String baseLayerPath) {
		
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
					Color c = pr.getColor(i, j);
					v[i][j][k] = c.getBrightness() == 0 ? null : c;
				}
			}
		}
		
		return v;
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
	
	
	public static Color[][][] line(Vec3 p, Vec3 q, Color c) {
		
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
				
				v[xc][yc][zc] = c;
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
				
				v[xc][yc][zc] = c;
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
				
				v[xc][yc][zc] = c;
			}
		}
		
		return v;
	}
}
