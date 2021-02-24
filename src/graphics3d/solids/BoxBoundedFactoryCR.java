package graphics3d.solids;

import graphics3d.Vec3;

public interface BoxBoundedFactoryCR<T> extends BoxBoundedFactory<T> {
	
	Vec3 E_2 = Vec3.EXYZ.div(2);
	
	
	default T pd(Vec3 p, Vec3 d) {
		Vec3 r = d.div(2);
		return cr(p.add(r), r);
	}
	
	default T pq(Vec3 p, Vec3 q) {
		return cr(p.add(q).div(2), q.sub(p).div(2));
	}
	
	default T r(Vec3 r) {
		return cr(Vec3.ZERO, r);
	}
	
	default T e() {
		return cr(E_2, E_2);
	}
}
