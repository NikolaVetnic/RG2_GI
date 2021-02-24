package graphics3d.solids;

import graphics3d.Vec3;

public interface BoxBoundedFactoryPQ<T> extends BoxBoundedFactory<T> {
	default T pd(Vec3 p, Vec3 d) {
		return pq(p, p.add(d));
	}
	
	default T cr(Vec3 c, Vec3 r) {
		return pq(c.sub(r), c.add(r));
	}
	
	default T r(Vec3 r) {
		return pq(r.inverse(), r);
	}
	
	default T e() {
		return pq(Vec3.ZERO, Vec3.EXYZ);
	}
}
