package graphics3d.solids.voxelworld.a;

import graphics3d.*;
import graphics3d.solids.Box;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class BasePF implements Solid {


	protected Predicate<Vec3> modelPred;
	protected Function<Vec3, Color> colorFunc;
	protected Vec3 len;


	protected BasePF(Vec3 d, Predicate<Vec3> p, Function<Vec3, Color> c) {
		this.len = d;
		this.modelPred = p;
		this.colorFunc = c;
	}


	protected BasePF(int x, int y, int z, Predicate<Vec3> p, Function<Vec3, Color> c) {
		this(Vec3.xyz(x, y, z), p, c);
	}
	
	
	public Vec3 len()		{ return len; 			}
	
	
	protected int lenX()	{ return len.xInt();	}
	protected int lenY()	{ return len.yInt();	}
	protected int lenZ()	{ return len.zInt();	}
	
	
	protected int[] lenA() {
		return new int[] { lenX(), lenY(), lenZ() };
	}


	public boolean isPopulated(int i, int j, int k) 	{ return isPopulated(Vec3.xyz(i, j, k)); 		}
	public boolean isPopulated(Vec3 p) 					{ return modelPred.test(p); 					}


	public Color color(int x, int y, int z) 			{ return colorFunc.apply(Vec3.xyz(x, y, z)); 	}
	public Color color(Vec3 p) 							{ return colorFunc.apply(p); 					}
	
	
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
