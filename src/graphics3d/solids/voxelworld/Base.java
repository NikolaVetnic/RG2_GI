package graphics3d.solids.voxelworld;

import graphics3d.Color;
import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;

public abstract class Base implements Solid {
	
	
	protected class Vec3Hit {
		
		/*
		 * A simple wrapper class used to pair up hits and associated vect-
		 * ors for easier retreival of material information from the model.
		 */
		
		private Vec3 v;
		private Hit h;
		
		public Vec3Hit(Vec3 v, Hit h) {
			this.v = v;
			this.h = h;
		}
		
		public Hit h() {
			return h;
		}
		
		public double t() {
			return h.t();
		}
		
		public Vec3 v() {
			return v;
		}
	}
	
	
	protected boolean[][][][] model;					// array is 4d to accomodate for octree boolean[][][][] 
	protected Color[][][] diffuse;
	
	protected Vec3 len;
	
	
	protected Base(boolean[][][][] model) { 			// for use with octrees
		this.model = model;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected Base(boolean[][][][] model, Color[][][] diffuse) { 
		this.model = model;
		this.diffuse = diffuse;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected Base(boolean[][][] model) {				// all algorithms other than octrees
		this.model = new boolean[1][][][];
		this.model[0] = model;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}
	
	
	protected Base(boolean[][][] model, Color[][][] diffuse) {
		this.model = new boolean[1][][][];
		this.model[0] = model;
		this.diffuse = diffuse;
		this.len = Vec3.xyz(model.length, model[0].length, model[0][0].length);
	}


	protected Base(ModelData3 data) {
		this(data.model(), data.diffuse());
	}
	
	
	protected Base(ModelData4 data) {
		this(data.model(), data.diffuse());
	}
	
	
	public Vec3 len()		{ return len; 			}
	
	
	protected int lenX()	{ return len.xInt();	}
	protected int lenY()	{ return len.yInt();	}
	protected int lenZ()	{ return len.zInt();	}
	
	
	protected boolean isPopulated(int i, int j, int k) {
		return model[0][i][j][k]; 		
	}
	
	
	protected boolean isPopulated(Vec3 p) { 
		return model[0][p.xInt()][p.yInt()][p.zInt()]; 
	}
	
	
	protected boolean isPopulated(int l, Vec3 p) { 
		return model[l][p.xInt()][p.yInt()][p.zInt()]; 
	}
	
	
	protected int[] lenA() {
		return new int[] { lenX(), lenY(), lenZ() };
	}
	

	public boolean[][][][] model() {
		return model;
	}
	
	
	public Color[][][] diffuse() {
		return diffuse;
	}
	
	
	public Color color(int x, int y, int z) {
		return diffuse[x][y][z];
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
