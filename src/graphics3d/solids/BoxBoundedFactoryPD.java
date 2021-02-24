package graphics3d.solids;

import graphics3d.Vec3;

public interface BoxBoundedFactoryPD<T> extends BoxBoundedFactory<T> {
	default T pq(Vec3 p, Vec3 q) {
		return pd(p, q.sub(p));
	}
	
	default T cr(Vec3 c, Vec3 r) {
		return pd(c.sub(r), r.mul(2));
	}
	
	default T r(Vec3 r) {
		return pd(r.inverse(), r.mul(2));
	}
	
	default T e() {
		return pd(Vec3.ZERO, Vec3.EXYZ);
	}
}
