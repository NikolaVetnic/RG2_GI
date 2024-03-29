package graphics3d.solids.voxelworld.a;

import graphics3d.*;
import graphics3d.solids.Box;
import graphics3d.solids.voxelworld.d.VoxelData;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BaseF implements Solid {
	

	protected Function<Vec3, VoxelData> dataFunc;
	protected Vec3 len;


	protected BaseF(Vec3 d, Function<Vec3, VoxelData> f) {
		this.len = d;
		this.dataFunc = f;
	}


	protected BaseF(int x, int y, int z, Function<Vec3, VoxelData> f) {
		this(Vec3.xyz(x, y, z), f);
	}
	
	
	public Vec3 len()		{ return len; 			}
	
	
	protected int lenX()	{ return len.xInt();	}
	protected int lenY()	{ return len.yInt();	}
	protected int lenZ()	{ return len.zInt();	}
	
	
	protected int[] lenA() {
		return new int[] { lenX(), lenY(), lenZ() };
	}


	public boolean isPopulated(int i, int j, int k) 	{ return isPopulated(Vec3.xyz(i, j, k)); 	}
	public boolean isPopulated(Vec3 p) 					{ return dataFunc.apply(p).isPopulated(); 	}
	
	
	public Color color(int x, int y, int z) { return dataFunc.apply(Vec3.xyz(x, y, z)).color(); 	}
	public Color color(Vec3 p) 				{ return dataFunc.apply(p).color(); 					}
	
	
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
