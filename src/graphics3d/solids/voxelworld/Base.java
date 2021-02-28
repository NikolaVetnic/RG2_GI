package graphics3d.solids.voxelworld;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Solid;
import graphics3d.Vec3;
import graphics3d.solids.Box;
import javafx.scene.paint.Color;

abstract class Base implements Solid {
	
	
	protected Color[][][] v;
	
	
	protected Base(Color[][][] v) { 
		this.v = v;
	}
	
	
	protected int lenX()	{ return v.length;			}
	protected int lenY()	{ return v[0].length;		}
	protected int lenZ()	{ return v[0][0].length;	}
	
	
	protected static Hit[] getHits(Vec3 p, Vec3 d, Ray ray) {
		return Box.$.pd(p, d).hits(ray);
	}
	
	
	protected static Hit[] getHits(Vec3 p, Ray ray) {
		return getHits(p, Vec3.EXYZ, ray);
	}
	
	
	public abstract Hit[] hits(Ray ray);
}
