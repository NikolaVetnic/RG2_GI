package graphics3d.solids.voxelworld.u;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import graphics3d.Color;
import graphics3d.Vec3;
import graphics3d.solids.voxelworld.d.ModelData3;
import graphics3d.solids.voxelworld.d.ModelData4;
import graphics3d.solids.voxelworld.d.TerrainPalette;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class Loaders {
	
	
	private static final int DEFAULT_MAP_HEIGHT = 100;
	private static final TerrainPalette DEFAULT_PALETTE = TerrainPalette.PASTORAL;


	private static boolean lerpMapColors = true;
	
	
	public static ModelData3 map(String path) throws IOException {
		
		Image image = null;
		
		try {
			image = new Image(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
		
		int x = (int) image.getWidth();
		int y = (int) image.getHeight();
		int z = DEFAULT_MAP_HEIGHT;
		
		boolean[][][] arr0 = new boolean[x][y][z];
		Color[][][] arr1 = new Color[x][y][z];
		
		PixelReader pr = image.getPixelReader();
		
		for (int j = 0; j < y; j++) {
			for (int i = 0; i < x; i++) {
				
				javafx.scene.paint.Color imageValue = pr.getColor(i, j);
				int currZ = (int) (imageValue.getBrightness() * z);

				for (int k = 0; k < currZ; k++) {
					arr0[i][j][k] = true;
					arr1[i][j][k] = lerpMapColors ?
							DEFAULT_PALETTE.lerpedColorAtHeightMixedWithImageValue(
									imageValue.getBrightness(), 1.0, imageValue) :
							DEFAULT_PALETTE.colorAtHeightMixedWithImageValue(
									imageValue.getBrightness(), 1.0, imageValue);
				}

				if (!DEFAULT_PALETTE.isArid()) {

					int seaLevelHeight = (int) (DEFAULT_PALETTE.heightNormalized(1) * z);

					if (currZ >= seaLevelHeight) {
						for (int k = 0; k < seaLevelHeight; k++) {
							arr0[i][j][k] = true;
							arr1[i][j][k] = lerpMapColors ?
									DEFAULT_PALETTE.lerpedColorAtHeightMixedWithImageValue(
											imageValue.getBrightness(), 1.0, imageValue) :
									DEFAULT_PALETTE.colorAtHeightMixedWithImageValue(
											imageValue.getBrightness(), 1.0, imageValue);
						}
					}
				}
			}
		}
		
		return ModelData3.arr(arr0, arr1);
	}

	
	public static ModelData3 set(String baseLayerPath) throws IOException {
		
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
		
		boolean[][][] arr0 = new boolean[x][y][z];
		Color[][][] arr1 = new Color[x][y][z];
		
		for (int k = 0; k < z; k++) {
			
			if (k != 0)
				image = new Image(new FileInputStream(setPath + setName + "_" + String.format("%03d", k) + imgExtn));
			
			PixelReader pr = image.getPixelReader();
			
			for (int j = 0; j < y; j++) {
				for (int i = 0; i < x; i++) {
					javafx.scene.paint.Color c = pr.getColor(i, j);
					arr1[i][j][k] = c.toString().equals("0xffffffff") ? null : Color.rgb(c.getRed(), c.getGreen(), c.getBlue());
					arr0[i][j][k] = arr1[i][j][k] == null ? false : true;
				}
			}
		}
		
		return ModelData3.arr(arr0, arr1);
	}
	
	
	private static String getImgExtn(String baseLayerPath) {
		String[] tokens = baseLayerPath.split("/");
		String f = tokens[tokens.length - 1];
		
		return f.substring(f.length() - 4);
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


	public static ModelData4 line()
	{
		Random rng = new Random(42);

		int lvl = 4;

		double rngLimit = 0.0025;

		Vec3 dim = Vec3.xyz(Math.pow(2, lvl), Math.pow(2, lvl), Math.pow(2, lvl));

		boolean[][][][] arr0 = new boolean[1][dim.xInt()][dim.yInt()][dim.zInt()];
		Color[][][] arr1 = new Color[dim.xInt()][dim.yInt()][dim.zInt()];

		for (int i = 0; i < dim.xInt(); i++) {
			for (int j = 0; j < dim.yInt(); j++) {
				for (int k = 0; k < dim.zInt(); k++) {

					if (rng.nextDouble() < 0.25) {			// test model, random voxels
						arr0[0][i][j][k] = true;
						arr1[i][j][k] = Color.rgb(rng.nextDouble(), rng.nextDouble(), 0.0);
					}

					if (i == j && j == k) {						// test model, cube diagonal
						arr0[0][i][j][k] = false;
						arr1[i][j][k] = Color.rgb(rng.nextDouble(), rng.nextDouble(), 0.0);
					}
				}
			}
		}

		return ModelData4.arr(arr0, arr1);
	}
	
	
	public static ModelData3 line(Vec3 p, Vec3 q, Color c) {
		
		int minX = (int) (p.x() < q.x() ? p.x() : q.x()); 
		int minY = (int) (p.y() < q.y() ? p.y() : q.y());
		int minZ = (int) (p.z() < q.z() ? p.z() : q.z());
		
		Vec3 tr = Vec3.xyz(minX, minY, minZ);
		
		p = p.sub(tr);
		q = q.sub(tr);
		
		int dx = (int) q.x(),
			dy = (int) q.y(),
			dz = (int) q.z();
		
		boolean[][][] arr0 = new boolean[dx + 1][dy + 1][dz + 1];
		Color[][][] arr1 = new Color[dx + 1][dy + 1][dz + 1];
		
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

				arr0[xc][yc][zc] = true;
				arr1[xc][yc][zc] = c;
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

				arr0[xc][yc][zc] = true;
				arr1[xc][yc][zc] = c;
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

				arr0[xc][yc][zc] = true;
				arr1[xc][yc][zc] = c;
			}
		}

		return ModelData3.arr(arr0, arr1);
	}
}
