package graphics3d.solids;

import graphics3d.Vec3;


public interface BoxBoundedFactory<T> {
	T pd(Vec3 p, Vec3 d);
	T pq(Vec3 p, Vec3 q);
	T cr(Vec3 c, Vec3 r);
	T r(Vec3 r);
	T e();
	
	default T r(double r) {
		return r(Vec3.xyz(r, r, r));
	}
}


