package graphics3d.solids.voxelworld.d;

import graphics3d.Hit;
import graphics3d.Ray;
import graphics3d.Vec3;
import graphics3d.solids.voxelworld.u.Util;
import mars.geometry.Vector;

public class HitVoxel extends Hit.HitRayT implements Comparable<HitVoxel> {
	
	
	private Vec3 n_;
	private int p, q, r;
	
	
	public HitVoxel(Ray ray, double t) {
		super(ray, t);
	}


	public HitVoxel(Ray ray, Hit h, Vec3 p) {
		this(ray, h, p.xInt(), p.yInt(), p.zInt());
	}


	public HitVoxel(Ray ray, Hit h, int i, int j, int k) {
		this(ray, h.t());
		this.n_ = h.n_();
		
		this.p = i;
		this.q = j;
		this.r = k;
	}
	
	
	@Override
	public Vec3 n_() {
		return n_;
	}
	
	
	@Override
	public Vector uv() {
		return Util.pack(p, q, r);
	}


	@Override
	public int compareTo(HitVoxel h) {
		double d = t() - h.t();
		
		if 		(d > 0) return  1;
		else if (d < 0) return -1;
		else 			return  0;
	}
}
