package graphics3d;

import graphics3d.solids.Nothing;
import graphics3d.solids.Space;

import java.util.Arrays;

public interface Solid {
	/**
	 * All the hits of the ray into the surface of the solid, sorted by increasing time (without repetitions),
	 * including negative-time hits. Even and odd indices correspond to the hits in which the ray is entering
	 * and exiting the solid, respectively.
	 */
	Hit[] hits(Ray ray);

	
	/**
	 * Returns the first hit of the ray into the surface of the solid, occuring after the given time. The
	 * default implementation is based on hits method, but implementations of Solid can choose to override
	 * this method to increase performance when only the first hit is needed.
	 * If there is no hit, return Hit.POSITIVE_INFINITY.
	 */
	default Hit firstHit(Ray ray, double afterTime) {
		Hit[] hits = hits(ray);
		for (Hit hit : hits) {
			if (hit.t() > afterTime) {
				return hit;
			}
		}
		return Hit.POSITIVE_INFINITY;
	}
	
	
	// ==============================================================================================
	
	
	Hit[] NO_HITS = new Hit[0];
	Hit[] FULL_RANGE = new Hit[] {
			Hit.NEGATIVE_INFINITY,
			Hit.POSITIVE_INFINITY
	};
	
	
	// Utility methods for transforming solids, and performing constructive operations
	

	default Solid transformed(Transform t) {
		return new Solid() {
			private final Transform tInv = t.inverse();
			private final Transform tInvT = tInv.transposeWithoutTranslation();
			
			@Override
			public Hit[] hits(Ray ray) {
				Hit[] hitsO = Solid.this.hits(tInv.applyTo(ray));
				if (hitsO.length == 0) {
					return NO_HITS;
				}
				Hit[] hitsT = new Hit[hitsO.length];
				for (int i = 0; i < hitsO.length; i++) {
					Hit h = hitsO[i];
					hitsT[i] = h.withN(tInvT.applyTo(h.n_()));
				}
				
				return hitsT;
			}
			
			@Override
			public Hit firstHit(Ray ray, double afterTime) {
				Ray rayO = tInv.applyTo(ray);
				Hit hitO = Solid.this.firstHit(rayO, afterTime * rayO.d().length());
				if (hitO.t() == Double.POSITIVE_INFINITY) {
					return hitO;
				}
				return hitO.withN(tInvT.applyTo(hitO.n_()));
			}
			
		};
	}
	
	
	default Solid complement() {
		return ray -> {
			// We just add -inf at the beginning and +inf at the end.
			// Repetitions might be introduced this way, so we check and remove them.
			
			Hit[] hitsS = Solid.this.hits(ray);
			int nS = hitsS.length;
			if (nS == 0) {
				return FULL_RANGE;
			}
			
			int sS = hitsS[0     ].t() == Double.NEGATIVE_INFINITY ? 1 : 0; // How many to skip from the start.
			int eS = hitsS[nS - 1].t() == Double.POSITIVE_INFINITY ? 1 : 0; // How many to skip from the end.
			
			int nT = nS + 2 - 2 * (sS + eS);
			if (nT == 0) {
				return NO_HITS;
			}
			
			Hit[] hitsT = new Hit[nT];
			hitsT[0               ] = Hit.NEGATIVE_INFINITY;                // Avoiding ifs. These elements might be overwritten in the for loop.
			hitsT[hitsT.length - 1] = Hit.POSITIVE_INFINITY;
			int n = nS - sS - eS;
			for (int i = 0; i < n; i++) {
				Hit h = hitsS[sS + i];
				hitsT[1 - sS + i] = h.inversed();
			}
			
			return hitsT;
		};
	}
	
	
	static Solid union(Solid sA, Solid sB) {
		return ray -> {
			Hit[] hitsA = sA.hits(ray);
			int nA = hitsA.length;

			Hit[] hitsB = sB.hits(ray);
			int nB = hitsB.length;
			
			if (nA == 0) return hitsB;
			if (nB == 0) return hitsA;
			
			int n = nA + nB;
			Hit[] hits = new Hit[n];
			
			int i = 0;
			int iA = 0;
			int iB = 0;
			double tA = hitsA[iA].t();
			double tB = hitsB[iB].t();
			
			while (iA < nA || iB < nB) {
				if (tA <= tB && iA < nA) {
					if ((iB & 1) == 0) {             // if we are outside B then this hit is relevant.
						hits[i++] = hitsA[iA];
					}
					iA++;
					tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
				} else {
					if ((iA & 1) == 0) {             // if we are outside A then this hit is relevant.
						hits[i++] = hitsB[iB];
					}
					iB++;
					tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
				}
			}
			
			return i == n ? hits : Arrays.copyOf(hits, i);
		};
	}
	
	
	static Solid intersection(Solid sA, Solid sB) {
		return ray -> {
			Hit[] hitsA = sA.hits(ray);
			int nA = hitsA.length;
			if (nA == 0) return Solid.NO_HITS;

			Hit[] hitsB = sB.hits(ray);
			int nB = hitsB.length;
			if (nB == 0) return Solid.NO_HITS;
			
			int n = nA + nB - 1;
			Hit[] hits = new Hit[n];
			
			int i = 0;
			int iA = 0;
			int iB = 0;
			double tA = hitsA[iA].t();
			double tB = hitsB[iB].t();
			
			while (iA < nA || iB < nB) {
				if (tA <= tB && iA < nA) {
					if ((iB & 1) == 1) {             // if we are inside B then this hit is relevant.
						hits[i++] = hitsA[iA];
					}
					iA++;
					tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
				} else {
					if ((iA & 1) == 1) {             // if we are inside A then this hit is relevant.
						hits[i++] = hitsB[iB];
					}
					iB++;
					tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
				}
			}
			
			return i == n ? hits : Arrays.copyOf(hits, i);
		};
	}

	
	static Solid difference(Solid sA, Solid sB) {
		return ray -> {
			Hit[] hitsA = sA.hits(ray);
			int nA = hitsA.length;
			if (nA == 0) return Solid.NO_HITS;

			Hit[] hitsB = sB.hits(ray);
			int nB = hitsB.length;
			if (nB == 0) return hitsA;
			
			int n = nA + nB;
			Hit[] hits = new Hit[n];
			
			int i = 0;
			int iA = 0;
			int iB = 0;
			double tA = hitsA[iA].t();
			double tB = hitsB[iB].t();
			
			while (iA < nA || iB < nB) {
				if (tA <= tB && iA < nA) {
					if ((iB & 1) == 0) {             // if we are outside B then this hit is relevant.
						hits[i++] = hitsA[iA];
					}
					iA++;
					tA = iA < nA ? hitsA[iA].t() : Double.POSITIVE_INFINITY;
				} else {
					if ((iA & 1) == 1) {             // if we are inside A then this hit is relevant.
						hits[i++] = hitsB[iB].inversed();
					}
					iB++;
					tB = iB < nB ? hitsB[iB].t() : Double.POSITIVE_INFINITY;
				}
			}
			
			return i == n ? hits : Arrays.copyOf(hits, i);
		};
	}
	
	
	private static Solid union(Solid[] solids, int i, int n) {
		if (n == 1) return solids[i];
		if (n == 2) return Solid.union(solids[i], solids[i+1]);
		int m = n/2;
		return union(union(solids, i, m), union(solids, i+m, n-m));
	}
	
	static Solid union(Solid... solids) {
		if (solids.length == 0) return Nothing.INSTANCE;
		return union(solids, 0, solids.length);
	}
	
	
	private static Solid intersection(Solid[] solids, int i, int n) {
		if (n == 1) return solids[i];
		if (n == 2) return Solid.intersection(solids[i], solids[i+1]);
		int m = n/2;
		return intersection(intersection(solids, i, m), intersection(solids, i+m, n-m));
	}
	
	static Solid intersection(Solid... solids) {
		if (solids.length == 0) return Space.INSTANCE;
		return intersection(solids, 0, solids.length);
	}
}
