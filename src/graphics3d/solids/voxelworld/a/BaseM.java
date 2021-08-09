package graphics3d.solids.voxelworld.a;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;
import graphics3d.solids.voxelworld.d.ModelData3;
import graphics3d.solids.voxelworld.d.ModelData4;

public abstract class BaseM implements Solid {
	
	
	protected boolean[][][][] model;					// array is 4d to accomodate for octree boolean[][][][] 
	protected Color[][][] diffuse;
	
	protected Vec3 len;
	
	
	protected BaseM(boolean[][][][] model) { 			// for use with octrees
		this.model = model;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected BaseM(boolean[][][][] model, Color[][][] diffuse) {
		this.model = model;
		this.diffuse = diffuse;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected BaseM(boolean[][][] model) {				// all algorithms other than octrees
		this.model = new boolean[1][][][];
		this.model[0] = model;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected BaseM(boolean[][][] model, Color[][][] diffuse) {
		this.model = new boolean[1][][][];
		this.model[0] = model;
		this.diffuse = diffuse;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}


	protected BaseM(ModelData3 data) {
		this(data.model(), data.diffuse());
	}
	
	
	protected BaseM(ModelData4 data) {
		this(data.model(), data.diffuse());
	}
	
	
	public Vec3 len()		{ return len; 			}
	
	
	protected int lenX()	{ return len.xInt();	}
	protected int lenY()	{ return len.yInt();	}
	protected int lenZ()	{ return len.zInt();	}
	
	
	protected int[] lenA() {
		return new int[] { lenX(), lenY(), lenZ() };
	}
	
	
	protected boolean isPopulated(int i, int j, int k) 	{ return model[0][i][j][k]; 						}
	protected boolean isPopulated(Vec3 p) 				{ return model[0][p.xInt()][p.yInt()][p.zInt()]; 	}
	protected boolean isPopulated(int l, Vec3 p) 		{ return model[l][p.xInt()][p.yInt()][p.zInt()]; 	}
	
	
	public Color color(int x, int y, int z) 			{ return diffuse[x][y][z]; 							}
	

	public boolean[][][][] model() 	{ return model; 	}
	public Color[][][] diffuse() 	{ return diffuse; 	}
	
	
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
