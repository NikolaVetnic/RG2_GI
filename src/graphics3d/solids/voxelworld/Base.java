package graphics3d.solids.voxelworld;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;

abstract class Base implements Solid {
	
	
	protected graphics3d.Color[][][] model;
	
	
	protected Base(graphics3d.Color[][][] model) { 
		this.model = model;
	}
	
	
	protected int lenX()	{ return model.length;			}
	protected int lenY()	{ return model[0].length;		}
	protected int lenZ()	{ return model[0][0].length;	}
	
	
	public graphics3d.Color[][][] model() {
		return model;
	}
	
	
	public graphics3d.Color voxel(int x, int y, int z) {
		return model[x][y][z];
	}
	
	
	protected static Hit[] getHits(Vec3 p, Vec3 d, Ray ray) {
		return Box.$.pd(p, d).hits(ray);
	}
	
	
	protected static Hit[] getHits(Vec3 p, Ray ray) {
		return getHits(p, Vec3.EXYZ, ray);
	}
	
	
	public abstract Hit[] hits(Ray ray);
}
