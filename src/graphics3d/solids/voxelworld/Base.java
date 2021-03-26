package graphics3d.solids.voxelworld;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;

abstract class Base implements Solid {
	
	
	protected Color[][][] model;
	
	
	protected Base(graphics3d.Color[][][] model) { 
		this.model = model;
	}
	
	
	protected Vec3 len()	{ return Vec3.xyz(
								lenX(), lenY(), lenZ()); 	}
	
	protected int lenX()	{ return model.length;			}
	protected int lenY()	{ return model[0].length;		}
	protected int lenZ()	{ return model[0][0].length;	}
	
	protected Color cell(int i, int j, int k) {
		return model[i][j][k]; 		
	}
	
	protected Color cell(Vec3 p) { 
		return model[p.xInt()][p.yInt()][p.zInt()]; 
	}
	
	
	public Color[][][] model() {
		return model;
	}
	
	
	public Color voxel(int x, int y, int z) {
		return model[x][y][z];
	}
	
	
	protected static Hit[] getHits(Vec3 p, Vec3 d, Ray ray) {
		return Box.$.pd(p, d).hits(ray);
	}
	
	
	protected static Hit[] getHits(Vec3 p, Ray ray) {
		return getHits(p, Vec3.EXYZ, ray);
	}
	
	
	protected Hit[] getBoundingBoxHits(Ray ray) {
		return getHits(Vec3.ZERO, len(), ray);
	}
	
	
	public abstract Hit[] hits(Ray ray);
}
